package co.tomac.datapuppy.devicemonitor.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.Date;

public class EventRepository {

    private static final String DB_NAME = "db_task";

    private EventDatabase eventDatabase;
    private static final String TAG = "EventRepository";

    public EventRepository(Context context) {
        eventDatabase = Room.databaseBuilder(context, EventDatabase.class, DB_NAME).build();
    }

    public void insertBatteryEvent(Integer value) {
        this.insertEvent("battery", Double.valueOf(value));
    }

    public void insertCpuEvent(Integer value) {
        this.insertEvent("cpu", Double.valueOf(value));
    }

    public void insertRamEvent(Double value) {
        this.insertEvent("ram", value);
    }

    private void insertEvent(String type, Double value) {
        if(value == null) {
            Log.w(TAG, "Attempted to insert a null value into the database.");
        }
        Event event = new Event();
        event.setType(type);
        event.setValue(value);
        event.setCreatedAt(new Date());
    }

    public void insertEventAsync(final Event event) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                eventDatabase.daoAccess().insertEvent(event);
            }
        });
    }

    public void deleteEventAsync(final int id) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                LiveData<Event> event = getEvent(id);
                if(event == null) {
                    return;
                }
                eventDatabase.daoAccess().deleteEvent(event.getValue());
            }
        });
    }

    public LiveData<Event> getEvent(final int id) {
        return eventDatabase.daoAccess().getEvent(id);
    }

}
