package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Amulya on 7/27/2016.
 */
public class DashboardSyllabusModel {

    public String getSyllabus_status() {
        return syllabus_status;
    }

    public void setSyllabus_status(String syllabus_status) {
        this.syllabus_status = syllabus_status;
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

    public ArrayList<SyllabusListModel> getSyllabus_data() {
        return syllabus_data;
    }

    public void setSyllabus_data(ArrayList<SyllabusListModel> syllabus_data) {
        this.syllabus_data = syllabus_data;
    }

    public ArrayList<DashboardSubjectModel> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<DashboardSubjectModel> subjects) {
        this.subjects = subjects;
    }

    public ArrayList<DashAttendanceMonthModel> getMonth_data() {
        return month_data;
    }

    public void setMonth_data(ArrayList<DashAttendanceMonthModel> month_data) {
        this.month_data = month_data;
    }

    public ArrayList<DashAttendanceClassModel> getStudent_data() {
        return student_data;
    }

    public void setStudent_data(ArrayList<DashAttendanceClassModel> student_data) {
        this.student_data = student_data;
    }

    public String getTotal_class() {
        return total_class;
    }

    public void setTotal_class(String total_class) {
        this.total_class = total_class;
    }


    public String syllabus_status;
    public ArrayList<SyllabusListModel> syllabus_data;
    public ArrayList<DashboardSubjectModel> subjects;
    public ArrayList<DashAttendanceMonthModel> month_data;
    public ArrayList<DashAttendanceClassModel> student_data;
    public String total_class;
    public String status;
    public String code;
    public String message;

}
