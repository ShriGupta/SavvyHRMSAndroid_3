package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 8/12/2016.
 */
public class EmailModel {


    public ArrayList<EmailListModel> getMail_list() {
        return mail_list;
    }

    public void setMail_list(ArrayList<EmailListModel> mail_list) {
        this.mail_list = mail_list;
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

    public ArrayList<EmailListModel> mail_list;
    public String status;
    public String code;
    public String message;
}
