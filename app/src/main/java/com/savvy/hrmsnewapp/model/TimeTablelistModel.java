package com.savvy.hrmsnewapp.model;

/**
 * Created by Amulya on 7/24/2016.
 */
public class TimeTablelistModel {

    public String getTimetable_id() {
        return timetable_id;
    }

    public void setTimetable_id(String timetable_id) {
        this.timetable_id = timetable_id;
    }

    public String getClass_status() {
        return class_status;
    }

    public void setClass_status(String class_status) {
        this.class_status = class_status;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getClass_starttime() {
        return class_starttime;
    }

    public void setClass_starttime(String class_starttime) {
        this.class_starttime = class_starttime;
    }

    public String getClass_endtime() {
        return class_endtime;
    }

    public void setClass_endtime(String class_endtime) {
        this.class_endtime = class_endtime;
    }

    public String getTime_interval() {
        return time_interval;
    }

    public void setTime_interval(String time_interval) {
        this.time_interval = time_interval;
    }

    public String getFaculty_image_url() {
        return faculty_image_url;
    }

    public void setFaculty_image_url(String faculty_image_url) {
        this.faculty_image_url = faculty_image_url;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String timetable_id;
    public String class_status;
    public String teacher_name;
    public String class_starttime;
    public String class_endtime;
    public String time_interval;
    public String faculty_image_url;
    public String subject;
    public String batch;
}
