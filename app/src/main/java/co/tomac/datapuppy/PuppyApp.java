package co.tomac.datapuppy;


import android.app.Application;

import co.tomac.datapuppy.devicemonitor.DeviceMonitor;

public class PuppyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceMonitor.initialize(this);
        DeviceMonitor.start();
    }
}
