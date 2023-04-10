package com.savvy.hrmsnewapp.room_database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TrackMeModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "trackMeDetails")
    String trackMeDetails;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrackMeDetails() {
        return trackMeDetails;
    }

    public void setTrackMeDetails(String trackMeDetails) {
        this.trackMeDetails = trackMeDetails;
    }


}
