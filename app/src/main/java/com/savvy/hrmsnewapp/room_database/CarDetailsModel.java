package com.savvy.hrmsnewapp.room_database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class CarDetailsModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "pickupdate")
    private String pickupdate;

    @ColumnInfo(name = "pickupat")
    private String pickupat;

    @ColumnInfo(name = "dropat")
    private String dropat;

    @ColumnInfo(name = "pickuptime")
    private String pickuptime;

    @ColumnInfo(name = "releasetime")
    private String releasetime;

    @ColumnInfo(name = "comment")
    private String comment;

    public int getId() {
        return id;
    }

    public String getPickupdate() {
        return pickupdate;
    }

    public String getPickupat() {
        return pickupat;
    }

    public String getDropat() {
        return dropat;
    }

    public String getPickuptime() {
        return pickuptime;
    }

    public String getReleasetime() {
        return releasetime;
    }

    public String getComment() {
        return comment;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPickupdate(String pickupdate) {
        this.pickupdate = pickupdate;
    }

    public void setPickupat(String pickupat) {
        this.pickupat = pickupat;
    }

    public void setDropat(String dropat) {
        this.dropat = dropat;
    }

    public void setPickuptime(String pickuptime) {
        this.pickuptime = pickuptime;
    }

    public void setReleasetime(String releasetime) {
        this.releasetime = releasetime;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
