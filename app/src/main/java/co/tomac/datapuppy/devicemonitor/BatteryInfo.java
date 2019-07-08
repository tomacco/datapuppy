package co.tomac.datapuppy.devicemonitor;

import android.content.Context;
import android.os.BatteryManager;

public class BatteryInfo {

    /**
     * Gets the system remaining battery as an integer percentage of total capacity
     *      * (with no fractional part).
     * @param context Android context
     * @return the battery percentage as an integer (0 .. 100). -1 if it was unable to get the information
     */
    public static int getBatteryLevel(Context context) {
        BatteryManager bm = (BatteryManager)context.getSystemService(Context.BATTERY_SERVICE);
        if(bm == null) {
            return -1;
        }
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }
}
