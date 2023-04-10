package com.savvy.hrmsnewapp.model;

/**
 * Created by Amulya on 7/27/2016.
 */
public class SyllabusListModel {

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getSubject_color() {
        return subject_color;
    }

    public void setSubject_color(String subject_color) {
        this.subject_color = subject_color;
    }

    public double getCircle_value() {
        return circle_value;
    }

    public void setCircle_value(double circle_value) {
        this.circle_value = circle_value;
    }

    public String subject;
    public String percentage;
    public String subject_color;
    public double circle_value;
}
