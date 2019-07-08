package co.tomac.datapuppy.devicemonitor.db;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimestampConverter {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if(value == null) {
            return null;
        }
        try {
            TimeZone timeZone = TimeZone.getTimeZone("IST");
            df.setTimeZone(timeZone);
            return df.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    @TypeConverter
    public static String dateToTimestamp(Date value) {
        TimeZone timeZone = TimeZone.getTimeZone("IST");
        df.setTimeZone(timeZone);
        return value == null ? null : df.format(value);
    }
}
