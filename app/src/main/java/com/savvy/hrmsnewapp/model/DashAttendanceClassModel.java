package com.savvy.hrmsnewapp.model;

/**
 * Created by am00347646 on 28-07-2016.
 */
public class DashAttendanceClassModel {



    public String getClass_record() {
        return class_record;
    }

    public void setClass_record(String class_record) {
        this.class_record = class_record;
    }

    public String getCircle_color() {
        return circle_color;
    }

    public void setCircle_color(String circle_color) {
        this.circle_color = circle_color;
    }

    public int getProgress_value() {
        return progress_value;
    }

    public void setProgress_value(int progress_value) {
        this.progress_value = progress_value;
    }

    public int getClass_attend() {
        return class_attend;
    }

    public void setClass_attend(int class_attend) {
        this.class_attend = class_attend;
    }

    public int class_attend;
    public String class_record;
    public String circle_color;
    public int progress_value;
}
