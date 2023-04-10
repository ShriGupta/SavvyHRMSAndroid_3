package com.savvy.hrmsnewapp.model;

/**
 * Created by am00347646 on 28-07-2016.
 */
public class DashAttendanceMonthModel {



    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal_class() {
        return total_class;
    }

    public void setTotal_class(int total_class) {
        this.total_class = total_class;
    }

    public int total_class;
    public String month;
    public String id;
}
