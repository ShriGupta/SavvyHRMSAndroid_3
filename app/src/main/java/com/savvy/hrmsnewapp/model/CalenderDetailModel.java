package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 8/9/2016.
 */
public class CalenderDetailModel {


    public CalenderDetailDataModel getFaculty_data() {
        return faculty_data;
    }

    public void setFaculty_data(CalenderDetailDataModel faculty_data) {
        this.faculty_data = faculty_data;
    }

    public ArrayList<CalenderDetaillistModel> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<CalenderDetaillistModel> topics) {
        this.topics = topics;
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

    public CalenderDetailDataModel faculty_data;
    public ArrayList<CalenderDetaillistModel> topics;
    public String status;
    public String code;
    public String message;
}
