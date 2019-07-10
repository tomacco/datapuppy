package co.tomac.datapuppy;


import android.app.Application;

import co.tomac.datapuppy.devicemonitor.DeviceMonitor;
import co.tomac.datapuppy.devicemonitor.db.EventRepository;

public class PuppyApp extends Application {

    public static final String NOTIFICATION_CHANNEL_ID = "alerts";


    @Override
    public void onCreate() {
        super.onCreate();
        DeviceMonitor.initialize(this);
        DeviceMonitor.start();
        AlarmNotificationService.configureChannelsIfOreoOrGreater(this);
    }

}
