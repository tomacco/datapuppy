package co.tomac.datapuppy.devicemonitor;

import android.app.Application;

public class DeviceMonitor {

    private static MonitorContext monitorContext;

    public static synchronized void initialize(Application application) {
        monitorContext = new MonitorContext(application);
    }

    public boolean isRunning() {
        return monitorContext != null && monitorContext.isRunning();
    }

    public static void start() {
        if(monitorContext == null) {
            return;
        }
        monitorContext.start();
    }

    public static void registerListener(DeviceMonitorListener listener) {
        monitorContext.registerListener(listener);
    }

    public static void unregisterListener(DeviceMonitorListener listener) {
        monitorContext.unregisterListener(listener);
    }


}
