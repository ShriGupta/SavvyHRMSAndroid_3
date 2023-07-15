package com.savvy.hrmsnewapp.fragment.CompanyDirectory.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyHierarchyBasedOnRolePostResult {
    @SerializedName("Gender")
    @Expose
    private String gender;
    @SerializedName("branch")
    @Expose
    private String branch;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("depth")
    @Expose
    private String depth;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
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
