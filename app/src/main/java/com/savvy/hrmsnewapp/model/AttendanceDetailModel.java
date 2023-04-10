package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Hari Om on 7/29/2016.
 */
public class AttendanceDetailModel   {

    public ArrayList<AttendanceDetailListModel> getAttendance_data() {
        return attendance_data;
    }

    public void setAttendance_data(ArrayList<AttendanceDetailListModel> attendance_data) {
        this.attendance_data = attendance_data;
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

    public String getTotal_absents() {
        return total_absents;
    }

    public void setTotal_absents(String total_absents) {
        this.total_absents = total_absents;
    }

    public ArrayList<AttendanceDetailListModel> attendance_data;
    public String status;
    public String code;
    public String message;
    public String total_absents;
}
