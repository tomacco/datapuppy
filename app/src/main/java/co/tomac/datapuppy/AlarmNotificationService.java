package co.tomac.datapuppy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import co.tomac.datapuppy.devicemonitor.DeviceMonitor;
import co.tomac.datapuppy.devicemonitor.DeviceMonitorListener;
import co.tomac.datapuppy.devicemonitor.db.EventRepository;

public class AlarmNotificationService extends Service implements DeviceMonitorListener {

    private final IBinder binder = new Binder();

    private Map<MetricType, Integer> activeNotificationsConfig = new HashMap<>();

    private boolean cpuOverThreshold = false;
    private boolean ramOverThreshold = false;
    private boolean batteryBelowThreshold = false;

    private EventRepository eventRepo;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundIfOreoOrGreater();
        activeNotificationsConfig =
                NotificationsConfigHelper.loadConfiguredNotifications(this);
        eventRepo = DeviceMonitor.getEventRepository();
    }


    private void startForegroundIfOreoOrGreater() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        Notification notification =
                new NotificationCompat.Builder(this, PuppyApp.NOTIFICATION_CHANNEL_ID)
                        .setContentTitle("DataPuppy is monitoring device resources...")
                        .setSmallIcon(R.drawable.ic_home_black_24dp)
                        .build();
        this.startForeground(100, notification);
    }

    static void configureChannelsIfOreoOrGreater(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel notiChannel =
                new NotificationChannel(PuppyApp.NOTIFICATION_CHANNEL_ID,
                        "Alerts",
                        NotificationManager.IMPORTANCE_HIGH);

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
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    /*
     **************** PUBLIC SERVICE API ****************
     */
    public Map<MetricType, Integer> getConfiguredNotifications() {
        return activeNotificationsConfig;
    }


    public void monitorResourceAndAlertWithThreshold(MetricType type, int threshold) {
        if (threshold == -1) { //remove notification
            activeNotificationsConfig.remove(type);
            eventRepo.insertAlarmEvent("Removing alarm for " + type.toString());
        } else { //add notification
            activeNotificationsConfig.put(type, threshold);
            String message =
                    String.format(Locale.getDefault(),
                            "Adding alarm for %s. Threshold: %d%%", type.toString(), threshold);
            eventRepo.insertAlarmEvent(message);
        }
        NotificationsConfigHelper
                .persistNotificationsConfig(this, activeNotificationsConfig);
        evaluateServiceState();
    }


    /*
     ******************************************************
     */

    private void evaluateServiceState() {
        if (activeNotificationsConfig.isEmpty()) {
            DeviceMonitor.unregisterListener(this);
            stopSelf();
            return; //stop service if no notifications have been requested
        }

        DeviceMonitor.registerListener(this); //listen to device monitor events otherwise
    }

    protected void showNotificationForMetric(String message, MetricType type) {
        eventRepo.insertAlarmEvent(message);
        showNotification(message, type.ordinal());
    }

    private void showNotification(String message, int identifier) {
        Notification notification =
                new NotificationCompat.Builder(this, PuppyApp.NOTIFICATION_CHANNEL_ID)
                        .setContentTitle("ALARM")
                        .setAutoCancel(true)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_home_black_24dp)
                        .build();
        getNotificationManager(this).notify(identifier, notification);

    }

    @Override
    public void onCpuSampling(int cpuUsage) {
        MetricType eventType = MetricType.CPU;
        if (!activeNotificationsConfig.containsKey(eventType)) {
            return;
        }
        Integer alarmThreshold = activeNotificationsConfig.get(eventType);
        //noinspection ConstantConditions
        if (cpuUsage >= alarmThreshold) {
            if (!cpuOverThreshold) {
                this.showNotificationForMetric("CPU Usage. Load: "
                        + cpuUsage + "%", eventType);
                cpuOverThreshold = true; //avoid showing notifications multiple times
            }
        } else if (cpuOverThreshold) {
            cpuOverThreshold = false;
            this.showNotificationForMetric("CPU Usage load is now below "
                    + alarmThreshold + "%", MetricType.CPU);
        }
    }

    @Override
    public void onRamSampling(double ramUsage) {
        MetricType eventType = MetricType.RAM;
        if (!activeNotificationsConfig.containsKey(eventType)) {
            return;
        }
        Integer alarmThreshold = activeNotificationsConfig.get(eventType);
        //noinspection ConstantConditions
        if (ramUsage >= alarmThreshold) {
            if (!ramOverThreshold) {
                String message =
                        String.format(Locale.getDefault(), "RAM Usage: %.2f%%", ramUsage);
                this.showNotificationForMetric(message, eventType);
                ramOverThreshold = true; //avoid showing notifications multiple times
            }
        } else if (ramOverThreshold) {
            ramOverThreshold = false;
            this.showNotificationForMetric("RAM Usage returned back below "
                    + alarmThreshold + "%", eventType);
        }
    }

    @Override
    public void onBatterySampling(int batteryRemaining) {
        MetricType eventType = MetricType.BATTERY;
        if (!activeNotificationsConfig.containsKey(eventType)) {
            return;
        }
        Integer alarmThreshold = activeNotificationsConfig.get(eventType);
        //noinspection ConstantConditions
        if (batteryRemaining <= alarmThreshold) {
            if (!batteryBelowThreshold) {
                this.showNotificationForMetric("Battery level. Current level: "
                        + batteryRemaining + "%", eventType);
                batteryBelowThreshold = true; //avoid showing notifications multiple times
            }
        } else if (batteryBelowThreshold) {
            batteryBelowThreshold = false;
            this.showNotificationForMetric("Battery charged above " + alarmThreshold + "%",
                    MetricType.BATTERY);
        }
    }

    class Binder extends android.os.Binder {
        AlarmNotificationService getService() {
            return AlarmNotificationService.this;
        }
    }
}
