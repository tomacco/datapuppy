package co.tomac.datapuppy.devicemonitor;

import android.app.ActivityManager;
import android.content.Context;

public class RamInfo {

    /**
     * Returns RAM usage of the device as an integer percentage of total capacity
     * @param context Android Context
     * @return the used memory from 0 to 100. It returns -1 if it was unable to get the measurement.
     */
    public static double getCurrentRamUsage(Context context) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager == null) {
            return -1;
        }
        activityManager.getMemoryInfo(memoryInfo);
        return (1 - memoryInfo.availMem / (double) memoryInfo.totalMem) * 100;
    }
}
