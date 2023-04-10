package com.savvy.hrmsnewapp.model;

/**
 * Created by Hari Om on 8/14/2016.
 */
public class FeedbackModel {

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

    public String status;
    public String code;
    public String message;
}
