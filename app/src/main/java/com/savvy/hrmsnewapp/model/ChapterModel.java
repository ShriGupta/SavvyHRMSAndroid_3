package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 8/15/2016.
 */
public class ChapterModel {


    public ArrayList<ChapterlistModel> getChapter_list() {
        return chapter_list;
    }

    public void setChapter_list(ArrayList<ChapterlistModel> chapter_list) {
        this.chapter_list = chapter_list;
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

    public ArrayList<ChapterlistModel> chapter_list;
    public String status;
    public String code;
    public String message;
}
