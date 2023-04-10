package com.savvy.hrmsnewapp.model;

/**
 * Created by Hari Om on 8/15/2016.
 */
public class NoteslistModel {

    public String getNotes_id() {
        return notes_id;
    }

    public void setNotes_id(String notes_id) {
        this.notes_id = notes_id;
    }

    public String getNotes_name() {
        return notes_name;
    }

    public void setNotes_name(String notes_name) {
        this.notes_name = notes_name;
    }

    public String getNotes_file_link() {
        return notes_file_link;
    }

    public void setNotes_file_link(String notes_file_link) {
        this.notes_file_link = notes_file_link;
    }

    public String notes_id;
    public String notes_name;
    public String notes_file_link;

}
