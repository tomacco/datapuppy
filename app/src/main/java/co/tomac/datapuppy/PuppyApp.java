package co.tomac.datapuppy;


import android.app.Application;

import co.tomac.datapuppy.devicemonitor.DeviceMonitor;

public class PuppyApp extends Application {

    public static final String NOTIFICATION_CHANNEL_ID = "alerts";

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceMonitor.initialize(this);
        DeviceMonitor.start();
        NotificationService.configureChannelsIfOreoOrGreater(this);
    }

    public static void createNotification(MetricType type) {

    }
}
