package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 8/8/2016.
 */
public class CalenderModel {


    public ArrayList<CalenderlistModel> getFaculty_list() {
        return faculty_list;
    }

    public void setFaculty_list(ArrayList<CalenderlistModel> faculty_list) {
        this.faculty_list = faculty_list;
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

    public ArrayList<CalenderlistModel> faculty_list;
    public String status;
    public String code;
    public String message;
}
