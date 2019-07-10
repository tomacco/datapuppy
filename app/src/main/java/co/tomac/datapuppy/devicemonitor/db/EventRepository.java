package co.tomac.datapuppy.devicemonitor.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.Date;
import java.util.List;

public class EventRepository {

    private static final String DB_NAME = "db_task";

    private EventDatabase eventDatabase;
    private static final String TAG = "EventRepository";

    public EventRepository(Context context) {
        eventDatabase = Room.databaseBuilder(context, EventDatabase.class, DB_NAME).build();
    }

    public void insertAlarmEvent(String message) {
        this.insertEvent("alarm", message);
    }

    public void insertBatteryEvent(Integer value) {
        this.insertEvent("battery", String.valueOf(value));
    }

    public void insertCpuEvent(Integer value) {
        this.insertEvent("cpu", String.valueOf(value));
    }

    public void insertRamEvent(Double value) {
        this.insertEvent("ram", String.valueOf(value));
    }

    private void insertEvent(String type, String value) {
        AsyncTask.execute(() -> {
            if(value == null) {
                Log.w(TAG, "Attempted to insert a null value into the database.");
                return;
            }
            Event event = new Event();
            event.setType(type);
            event.setValue(value);
            event.setCreatedAt(new Date());
            eventDatabase.daoAccess().insertEvent(event);
        });
    }

    public void insertEventAsync(final Event event) {
        AsyncTask.execute(() -> eventDatabase.daoAccess().insertEvent(event));
    }

    public void deleteEventAsync(final int id) {
        AsyncTask.execute(() -> {
            LiveData<Event> event = getEvent(id);
            if(event == null) {
                return;
            }
            eventDatabase.daoAccess().deleteEvent(event.getValue());
        });
    }

    public LiveData<Event> getEvent(final int id) {
        return eventDatabase.daoAccess().getEvent(id);
    }

    public LiveData<List<Event>> getEventsForType(String type) {
        return eventDatabase.daoAccess().fetchEventsWithType(type);
    }

    public void deleteEventsWithType(final String type) {
        if(type == null) {
            return;
        }
        AsyncTask.execute(() -> {
            eventDatabase.daoAccess().deleteEventsWithType(type);
        });
    }

}
