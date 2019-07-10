package co.tomac.datapuppy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationsConfigHelper {

    private static final String ALARM_NOTIFICATION_CONFIG_KEY = "alarm-notification-service";

    static Map<MetricType, Integer> loadConfiguredNotifications(Context context) {
        Map<MetricType, Integer> activeNotificationsConfig = new ConcurrentHashMap<>();
        SharedPreferences preferences = context.getSharedPreferences("alarm-notification-service", Context.MODE_PRIVATE);
        String serializedConfig = preferences.getString(ALARM_NOTIFICATION_CONFIG_KEY, null);
        if(serializedConfig == null) {
            return activeNotificationsConfig;
        }
        try {
            JSONObject configJSON = new JSONObject(serializedConfig);
            for (Iterator<String> it = configJSON.keys(); it.hasNext(); ) {
                String key = it.next();
                Integer value = Integer.valueOf(configJSON.getString(key));
                activeNotificationsConfig.put(MetricType.valueOf(key), value);
            }
        } catch (JSONException e) {
            Toast.makeText(context, "Unable to load notifications from shared prefs",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return activeNotificationsConfig;
    }

    static void persistNotificationsConfig(Context context, Map<MetricType, Integer> activeNotificationsConfig) {
        SharedPreferences preferences =
                context.getSharedPreferences("alarm-notification-service", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(activeNotificationsConfig.isEmpty()) {
            editor.remove(ALARM_NOTIFICATION_CONFIG_KEY);
            return;
        }
        JSONObject json = new JSONObject();
        try {
            for(Map.Entry <MetricType, Integer> entry : activeNotificationsConfig.entrySet()) {
                    json.put(entry.getKey().toString(), entry.getValue());
            }
        } catch (JSONException exception) {
            Log.e("ConfigurationHelper", "Notifications serialization failed. " + exception);
            return;
        }
        editor.putString(ALARM_NOTIFICATION_CONFIG_KEY, json.toString());
        editor.apply();
    }
}
