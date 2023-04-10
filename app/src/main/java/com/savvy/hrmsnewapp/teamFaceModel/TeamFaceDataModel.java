package com.savvy.hrmsnewapp.teamFaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TeamFaceDataModel {
    @SerializedName("AttendanceDate")
    @Expose
    private String attendanceDate;
    @SerializedName("EnableStatus")
    @Expose
    private String enableStatus;
    @SerializedName("Intime")
    @Expose
    private String intime;
    @SerializedName("OutTime")
    @Expose
    private String outTime;
    @SerializedName("branch")
    @Expose
    private String branch;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("depth")
    @Expose
    private Object depth;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("employeeCode")
    @Expose
    private String employeeCode;
    @SerializedName("employeeId")
    @Expose
    private String employeeId;
    @SerializedName("employeeName")
    @Expose
    private String employeeName;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("photoCode")
    @Expose
    private String photoCode;
    @SerializedName("supervisorIdFirst")
    @Expose
    private String supervisorIdFirst;

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(String enableStatus) {
        this.enableStatus = enableStatus;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Object getDepth() {
        return depth;
    }

    public void setDepth(Object depth) {
        this.depth = depth;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getPhotoCode() {
        return photoCode;
    }

    public void setPhotoCode(String photoCode) {
        this.photoCode = photoCode;
    }

    public String getSupervisorIdFirst() {
        return supervisorIdFirst;
    }

    public void setSupervisorIdFirst(String supervisorIdFirst) {
        this.supervisorIdFirst = supervisorIdFirst;
    }
}
