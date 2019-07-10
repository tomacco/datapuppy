package co.tomac.datapuppy.devicemonitor.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    void insertEvent(Event event);

    @Query("SELECT * FROM Event ORDER BY created_at desc")
    LiveData<List<Event>> fetchAllEvents();

    @Query("SELECT * FROM Event WHERE type =:eventType")
    LiveData<List<Event>> fetchEventsWithType(String eventType);

    @Query("SELECT * FROM Event WHERE id =:id")
    LiveData<Event> getEvent(int id);

    @Delete
    void deleteEvent(Event event);

    @Query("DELETE FROM Event WHERE type=:eventType")
    void deleteEventsWithType(String eventType);

}
