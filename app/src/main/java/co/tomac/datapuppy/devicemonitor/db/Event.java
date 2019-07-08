package co.tomac.datapuppy.devicemonitor.db;


import androidx.room.*;

import java.util.Date;

@Entity
public class Event {

    @PrimaryKey(autoGenerate= true)
    private int id;

    @ColumnInfo(name = "created_at")
    @TypeConverters({TimestampConverter.class})
    private Date createdAt;

    private String type;

    private Double value;

    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getType() {
        return type;
    }

    public Double getValue() {
        return value;
    }

}
