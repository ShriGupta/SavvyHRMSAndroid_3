package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by hariom on 26/7/16.
 */
public class TimeTableInfoModel {


    public ArrayList<TimeTableInfolistModel> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<TimeTableInfolistModel> topics) {
        this.topics = topics;
    }

    public TimeTableInfoData getTime_table_data() {
        return time_table_data;
    }

    public void setTime_table_data(TimeTableInfoData time_table_data) {
        this.time_table_data = time_table_data;
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


    public TimeTableInfoData time_table_data;
    public ArrayList<TimeTableInfolistModel> topics;
    public String status;
    public String code;
    public String message;

}
