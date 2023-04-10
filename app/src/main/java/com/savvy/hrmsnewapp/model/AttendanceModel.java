package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 7/27/2016.
 */
public class AttendanceModel {


    public ArrayList<AttendancelistModel> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<AttendancelistModel> subjects) {
        this.subjects = subjects;
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

    public ArrayList<AttendancelistModel> subjects;
    public String status;
    public String code;
    public String message;
}
