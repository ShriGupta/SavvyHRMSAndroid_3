package com.savvy.hrmsnewapp.model;

/**
 * Created by hariom on 26/7/16.
 */
public class TimeTableInfoData {


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getLecture_time() {
        return lecture_time;
    }

    public void setLecture_time(String lecture_time) {
        this.lecture_time = lecture_time;
    }

    public String getLecture_endtime() {
        return lecture_endtime;
    }

    public void setLecture_endtime(String lecture_endtime) {
        this.lecture_endtime = lecture_endtime;
    }

    public String getFaculty_name() {
        return faculty_name;
    }

    public void setFaculty_name(String faculty_name) {
        this.faculty_name = faculty_name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String date;
    public String batch;
    public String lecture_time;
    public String lecture_endtime;
    public String faculty_name;
    public String subject;
    public String chapter;


}
