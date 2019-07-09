package co.tomac.datapuppy;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import co.tomac.datapuppy.devicemonitor.DeviceMonitor;
import co.tomac.datapuppy.devicemonitor.DeviceMonitorListener;

public class NotificationService extends Service implements DeviceMonitorListener {


    public static final String METRIC_TYPE = "metric-type";
    public static final String THRESHOLD = "threshold";
    private Map<MetricType, Integer> thresholds = new ConcurrentHashMap<>();

    private boolean cpuOverThreshold = false;
    private boolean ramOverThreshold = false;
    private boolean batteryBelowThreshold = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static void configureChannelsIfOreoOrGreater(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel notiChannel = new NotificationChannel(PuppyApp.NOTIFICATION_CHANNEL_ID, "Alerts", NotificationManager.IMPORTANCE_HIGH);
        notiChannel.enableLights(true);
        notiChannel.enableVibration(true);
        notiChannel.setLightColor(Color.RED);
        notiChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getNotificationManager(context).createNotificationChannel(notiChannel);
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MetricType type = MetricType.valueOf(intent.getStringExtra(METRIC_TYPE));
        int threshold = intent.getIntExtra(THRESHOLD, -1);

        if (threshold == -1) {
            thresholds.remove(type);
        } else {
            thresholds.put(type, threshold);
        }
        return START_STICKY;
    }

    private void evaluateState() {
        if (thresholds.isEmpty()) {
            DeviceMonitor.unregisterListener(this);
            stopSelf();
            return; //stop service if no notifications have been requested
        }

        DeviceMonitor.registerListener(this); //listen to device monitor events otherwise
    }

    private void showNotification(String message, MetricType type) {
        Notification notification = new NotificationCompat.Builder(this, PuppyApp.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("ALARM")
                .setAutoCancel(true)
                .setContentText(message).build();
        getNotificationManager(this).notify(type.ordinal(), notification);

    }

    @Override
    public void onCpuSampling(int cpuUsage) {
        MetricType eventType = MetricType.CPU;
        if (!thresholds.containsKey(eventType)) {
            return;
        }
        Integer alarmThreshold = thresholds.get(eventType);
        //noinspection ConstantConditions
        if (cpuUsage >= alarmThreshold) {
            if (!cpuOverThreshold) {
                this.showNotification("CPU Usage is over the selected threshold. Current load: "
                        + cpuUsage + "%", eventType);
                cpuOverThreshold = true; //avoid showing notifications multiple times
            }
        } else if (cpuOverThreshold) {
            cpuOverThreshold = false;
        }
    }

    @Override
    public void onRamSampling(double ramUsage) {
        MetricType eventType = MetricType.RAM;
        if (!thresholds.containsKey(eventType)) {
            return;
        }
        Integer alarmThreshold = thresholds.get(eventType);
        //noinspection ConstantConditions
        if (ramUsage >= alarmThreshold) {
            if (!ramOverThreshold) {
                this.showNotification("RAM Usage is over the selected threshold. Current usage: "
                        + ramUsage + "%", eventType);
                ramOverThreshold = true; //avoid showing notifications multiple times
            }
        } else if (ramOverThreshold) {
            ramOverThreshold = false;
        }
    }

    @Override
    public void onBatterySampling(int batteryRemaining) {
        MetricType eventType = MetricType.BATTERY;
        if (!thresholds.containsKey(eventType)) {
            return;
        }
        Integer alarmThreshold = thresholds.get(eventType);
        //noinspection ConstantConditions
        if (batteryRemaining <= alarmThreshold) {
            if (!batteryBelowThreshold) {
                this.showNotification("Battery level below the selected threshold. Current level: "
                        + batteryRemaining + "%", eventType);
                batteryBelowThreshold = true; //avoid showing notifications multiple times
            }
        } else if (batteryBelowThreshold) {
            batteryBelowThreshold = false;
        }
    }
}
