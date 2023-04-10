package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Amulya on 7/24/2016.
 */
public class TimeTableModel {

    public ArrayList<TimeTablelistModel> getTime_table() {
        return time_table;
    }

    public void setTime_table(ArrayList<TimeTablelistModel> time_table) {
        this.time_table = time_table;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<TimeTablelistModel> time_table;
    public String status;
    public String code;
    public String message;
}
