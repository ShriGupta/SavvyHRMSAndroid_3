package com.savvy.hrmsnewapp.room_database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class AccommodationModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "ciy")
    String city;

    @ColumnInfo(name = "fromdate")
    String fromDate;

    @ColumnInfo(name = "todate")
    String todate;

    @ColumnInfo(name = "checkintime")
    String checkintime;
    @ColumnInfo(name = "checkouttime")
    String checkouttime;

    @ColumnInfo(name = "hotellocation")
    String hotellocation;

    public void setId(int id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }

    public void setCheckintime(String checkintime) {
        this.checkintime = checkintime;
    }

    public void setCheckouttime(String checkouttime) {
        this.checkouttime = checkouttime;
    }

    public void setHotellocation(String hotellocation) {
        this.hotellocation = hotellocation;
    }

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getTodate() {
        return todate;
    }

    public String getCheckintime() {
        return checkintime;
    }

    public String getCheckouttime() {
        return checkouttime;
    }

    public String getHotellocation() {
        return hotellocation;
    }


}
