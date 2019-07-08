package co.tomac.datapuppy.devicemonitor.db;



import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    void insertEvent(Event event);

    @Query("SELECT * FROM Event ORDER BY created_at desc")
    LiveData<List<Event>> fetchAllEvents();

    @Query("SELECT * FROM Event WHERE type =:eventType")
    LiveData<List<Event>> fetchEventWithType(String eventType);

    @Query("SELECT * FROM Event WHERE id =:id")
    LiveData<Event> getEvent(int id);

    @Delete
    void deleteEvent(Event event);
}
