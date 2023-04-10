package com.savvy.hrmsnewapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequisitionStatusModel {
    @SerializedName("DEPARTMENT_NAME")
    @Expose
    private String departmentName;
    @SerializedName("RRW_APPROVAL_STATUS")
    @Expose
    private String rrwApprovalStatus;
    @SerializedName("RRW_APPROVAL_STATUS_1")
    @Expose
    private String rrwApprovalStatus1;
    @SerializedName("RRW_DEPARTMENT_ID")
    @Expose
    private String rrwDepartmentId;
    @SerializedName("RRW_REQUISITION_DATE_1")
    @Expose
    private String rrwRequisitionDate1;
    @SerializedName("RRW_REQUISITION_ID")
    @Expose
    private String rrwRequisitionId;
    @SerializedName("RRW_SHIFT_ID")
    @Expose
    private String rrwShiftId;
    @SerializedName("RRW_STATUS")
    @Expose
    private String rrwStatus;
    @SerializedName("SHIFT_NAME_WITH_STATUS")
    @Expose
    private String shiftNameWithStatus;

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRrwApprovalStatus() {
        return rrwApprovalStatus;
    }

    public void setRrwApprovalStatus(String rrwApprovalStatus) {
        this.rrwApprovalStatus = rrwApprovalStatus;
    }

    public String getRrwApprovalStatus1() {
        return rrwApprovalStatus1;
    }

    public void setRrwApprovalStatus1(String rrwApprovalStatus1) {
        this.rrwApprovalStatus1 = rrwApprovalStatus1;
    }

    public String getRrwDepartmentId() {
        return rrwDepartmentId;
    }

    public void setRrwDepartmentId(String rrwDepartmentId) {
        this.rrwDepartmentId = rrwDepartmentId;
    }

    public String getRrwRequisitionDate1() {
        return rrwRequisitionDate1;
    }

    public void setRrwRequisitionDate1(String rrwRequisitionDate1) {
        this.rrwRequisitionDate1 = rrwRequisitionDate1;
    }

    public String getRrwRequisitionId() {
        return rrwRequisitionId;
    }

    public void setRrwRequisitionId(String rrwRequisitionId) {
        this.rrwRequisitionId = rrwRequisitionId;
    }

    public String getRrwShiftId() {
        return rrwShiftId;
    }

    public void setRrwShiftId(String rrwShiftId) {
        this.rrwShiftId = rrwShiftId;
    }

    public String getRrwStatus() {
        return rrwStatus;
    }

    public void setRrwStatus(String rrwStatus) {
        this.rrwStatus = rrwStatus;
    }

    public String getShiftNameWithStatus() {
        return shiftNameWithStatus;
    }

    public void setShiftNameWithStatus(String shiftNameWithStatus) {
        this.shiftNameWithStatus = shiftNameWithStatus;
    }
}
