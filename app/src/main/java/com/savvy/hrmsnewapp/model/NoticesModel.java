package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by AM00347646 on 09-08-2016.
 */
public class NoticesModel {

    public ArrayList<NoticesDataModel> getNotices_data() {
        return notices_data;
    }

    public void setNotices_data(ArrayList<NoticesDataModel> notices_data) {
        this.notices_data = notices_data;
    }

    public String getTotal_absents() {
        return total_absents;
    }

    public void setTotal_absents(String total_absents) {
        this.total_absents = total_absents;
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

    public ArrayList<NoticesDataModel> notices_data;
    public String total_absents;
    public String status;
    public String code;
    public String message;


}
