package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 8/15/2016.
 */
public class FacultyModel {

    public ArrayList<FacultylistModel> getFaculty_list() {
        return faculty_list;
    }

    public void setFaculty_list(ArrayList<FacultylistModel> faculty_list) {
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

    public ArrayList<FacultylistModel> faculty_list;
    public String status;
    public String code;
    public String message;
}
