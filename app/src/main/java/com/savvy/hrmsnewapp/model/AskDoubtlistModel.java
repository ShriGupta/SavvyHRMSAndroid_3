package com.savvy.hrmsnewapp.model;

/**
 * Created by Hari Om on 8/14/2016.
 */
public class AskDoubtlistModel {

    public String getFaculty_id() {
        return faculty_id;
    }

    public void setFaculty_id(String faculty_id) {
        this.faculty_id = faculty_id;
    }

    public String getFaculty_name() {
        return faculty_name;
    }

    public void setFaculty_name(String faculty_name) {
        this.faculty_name = faculty_name;
    }

    public String getFaculty_image() {
        return faculty_image;
    }

    public void setFaculty_image(String faculty_image) {
        this.faculty_image = faculty_image;
    }

    public String getSender_question() {
        return sender_question;
    }

    public void setSender_question(String sender_question) {
        this.sender_question = sender_question;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_image() {
        return sender_image;
    }

    public void setSender_image(String sender_image) {
        this.sender_image = sender_image;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer_timing() {
        return answer_timing;
    }

    public void setAnswer_timing(String answer_timing) {
        this.answer_timing = answer_timing;
    }

    public String faculty_id;
    public String faculty_name;
    public String faculty_image;
    public String sender_question;
    public String sender_name;
    public String sender_image;
    public String answer;
    public String answer_timing;

}
