package co.tomac.datapuppy.devicemonitor;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import co.tomac.datapuppy.devicemonitor.db.EventRepository;

class MonitorContext {

    public static final String TAG = "DeviceMonitorContext";
    private final Context context;
    private final EventRepository eventRepository;

    private boolean isRunning = false;
    private HandlerThread handlerThread;
    private Handler handler;
    private boolean firstRead = true;
    private List<DeviceMonitorListener> listeners = new CopyOnWriteArrayList<>();
    private int lastBattery;
    private double lastRamUsage;
    private int lastCpuValue;


    public MonitorContext(Context context) {
        this.context = context;
        this.eventRepository = new EventRepository(context);
    }

    public synchronized void start() {
        if(isRunning) {
            return;
        }
        isRunning = true;
        startResourcesMonitoring();
    }

    public synchronized void stop() {
        if(!isRunning) {
            return;
        }
        isRunning = false;
        stopMeasurements();
    }

    private void stopMeasurements() {
        handler.removeCallbacks(null);
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void startResourcesMonitoring() {
        handlerThread = new HandlerThread("monitoring-thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MonitorContext.this.runAndSaveMeasurements(true, true, true);
                if(!isRunning) {
                    return;
                }
                handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1));
            }
        }, TimeUnit.SECONDS.toMillis(1));
    }

    private void runAndSaveMeasurements(boolean cpu, boolean ram, boolean battery) {
        if(cpu) {
            runCpuMeasurement();
        }
        if(ram) {
            runRamMeasurement();
        }
        if(battery) {
            runBatteryMeasurement();
        }
        broadcastValues();
    }

    private void broadcastValues() {
        for(DeviceMonitorListener listener : listeners) {
            listener.onCpuSampling(lastCpuValue);
            listener.onRamSampling(lastRamUsage);
            listener.onBatterySampling(lastBattery);
        }
    }


    private void runBatteryMeasurement() {
        int batLevel = BatteryInfo.getBatteryLevel(context);
        if(batLevel < 0) {
            Log.e(TAG, "Unable to read battery levels");
            return;
        }
        eventRepository.insertBatteryEvent(batLevel);
        lastBattery = batLevel;
    }

    private void runRamMeasurement() {
        double ramUsage = RamInfo.getCurrentRamUsage(context);
        if(ramUsage < 0) {
            Log.e(TAG, "Unable to read RAM Usage");
            return;
        }
        eventRepository.insertRamEvent(ramUsage);
        lastRamUsage = ramUsage;
    }

    private void runCpuMeasurement() {
        int cpuUsage = CpuInfo.getCpuUsage();
        if(firstRead) { //first CPU reading will always return -1
            firstRead = false;
            return;
        }
        if(cpuUsage < 0) {
            Log.e(TAG, "Unable to read CPU levels. ");
            return;
        }
        eventRepository.insertCpuEvent(cpuUsage);
        lastCpuValue = cpuUsage;
    }

    void registerListener(@Nullable DeviceMonitorListener listener) {
        if(listener == null) {
            return;
        }
        if(listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    void unregisterListener(@Nullable DeviceMonitorListener listener) {
        if(listener == null) {
            return;
        }
        listeners.remove(listener);
    }

    EventRepository getEventRepo() {
        return eventRepository;
    }
}
