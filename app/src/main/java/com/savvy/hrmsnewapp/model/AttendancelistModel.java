package com.savvy.hrmsnewapp.model;

/**
 * Created by Hari Om on 7/27/2016.
 */
public class AttendancelistModel {

    public String getClass_held() {
        return class_held;
    }

    public void setClass_held(String class_held) {
        this.class_held = class_held;
    }

    public String getClass_attended() {
        return class_attended;
    }

    public void setClass_attended(String class_attended) {
        this.class_attended = class_attended;
    }

    public String getAttendance_percentage() {
        return attendance_percentage;
    }

    public void setAttendance_percentage(String attendance_percentage) {
        this.attendance_percentage = attendance_percentage;
    }

    public String getProjected_att() {
        return projected_att;
    }

    public void setProjected_att(String projected_att) {
        this.projected_att = projected_att;
    }


    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject_color() {
        return subject_color;
    }

    public void setSubject_color(String subject_color) {
        this.subject_color = subject_color;
    }

    public String subject_id;
    public String subject;
    public String class_held;
    public String class_attended;
    public String attendance_percentage;
    public String projected_att;
    public String subject_color;
}
