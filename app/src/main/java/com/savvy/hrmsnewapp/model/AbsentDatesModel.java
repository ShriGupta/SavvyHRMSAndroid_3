package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 7/28/2016.
 */
public class AbsentDatesModel {

    public ArrayList<AbsentDateslistModel> getAbsend_date() {
        return absend_date;
    }

    public void setAbsend_date(ArrayList<AbsentDateslistModel> absend_date) {
        this.absend_date = absend_date;
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

    public ArrayList<AbsentDateslistModel> absend_date;
    public String status;
    public String code;
    public String message;
}
