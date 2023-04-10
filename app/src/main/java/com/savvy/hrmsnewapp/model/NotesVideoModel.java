package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 8/15/2016.
 */
public class NotesVideoModel {

    public ArrayList<VideoslistModel> getVideos_list() {
        return videos_list;
    }

    public void setVideos_list(ArrayList<VideoslistModel> videos_list) {
        this.videos_list = videos_list;
    }

    public ArrayList<NoteslistModel> getNotes_list() {
        return notes_list;
    }

    public void setNotes_list(ArrayList<NoteslistModel> notes_list) {
        this.notes_list = notes_list;
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

    public ArrayList<VideoslistModel> videos_list;
    public ArrayList<NoteslistModel> notes_list;
    public String status;
    public String code;
    public String message;
}
