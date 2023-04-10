package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 8/14/2016.
 */
public class AskDoubtModel {

    public ArrayList<AskDoubtlistModel> getAskdoubt_list() {
        return askdoubt_list;
    }

    public void setAskdoubt_list(ArrayList<AskDoubtlistModel> askdoubt_list) {
        this.askdoubt_list = askdoubt_list;
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

    public ArrayList<AskDoubtlistModel> askdoubt_list;
    public String status;
    public String code;
    public String message;

}
