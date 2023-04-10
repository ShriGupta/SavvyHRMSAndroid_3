package com.savvy.hrmsnewapp.room_database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ManagerDashboardMMTModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "EMPLOYEE_CODE")
    private String EMPLOYEE_CODE;

    @ColumnInfo(name = "EMPLOYEE_NAME")
    private String EMPLOYEE_NAME;

    @ColumnInfo(name = "AVG_WORKTIME")
    private String AVG_WORKTIME;

    @ColumnInfo(name = "AVG_IN_TIME")
    private String AVG_IN_TIME;


    @ColumnInfo(name = "LEAVE")
    private String LEAVE;

    @ColumnInfo(name = "WFH")
    private String WFH;

    @ColumnInfo(name = "OD")
    private String OD;

    @ColumnInfo(name = "AVG_WORKED1")
    private String AVG_WORKED1;

    @ColumnInfo(name = "AVG_OUT_TIME")
    private String AVG_OUT_TIME;


    public void setId(int id) {
        this.id = id;
    }

    public void setEMPLOYEE_CODE(String EMPLOYEE_CODE) {
        this.EMPLOYEE_CODE = EMPLOYEE_CODE;
    }

    public void setEMPLOYEE_NAME(String EMPLOYEE_NAME) {
        this.EMPLOYEE_NAME = EMPLOYEE_NAME;
    }

    public void setAVG_WORKTIME(String AVG_WORKTIME) {
        this.AVG_WORKTIME = AVG_WORKTIME;
    }

    public void setAVG_IN_TIME(String AVG_IN_TIME) {
        this.AVG_IN_TIME = AVG_IN_TIME;
    }

    public void setAVG_OUT_TIME(String AVG_OUT_TIME) {
        this.AVG_OUT_TIME = AVG_OUT_TIME;
    }

    public void setLEAVE(String LEAVE) {
        this.LEAVE = LEAVE;
    }

    public void setWFH(String WFH) {
        this.WFH = WFH;
    }

    public void setOD(String OD) {
        this.OD = OD;
    }

    public void setAVG_WORKED1(String AVG_WORKED1) {
        this.AVG_WORKED1 = AVG_WORKED1;
    }


    public int getId() {
        return id;
    }

    public String getEMPLOYEE_CODE() {
        return EMPLOYEE_CODE;
    }

    public String getEMPLOYEE_NAME() {
        return EMPLOYEE_NAME;
    }

    public String getAVG_WORKTIME() {
        return AVG_WORKTIME;
    }

    public String getAVG_IN_TIME() {
        return AVG_IN_TIME;
    }

    public String getAVG_OUT_TIME() {
        return AVG_OUT_TIME;
    }

    public String getLEAVE() {
        return LEAVE;
    }

    public String getWFH() {
        return WFH;
    }

    public String getOD() {
        return OD;
    }

    public String getAVG_WORKED1() {
        return AVG_WORKED1;
    }

}
