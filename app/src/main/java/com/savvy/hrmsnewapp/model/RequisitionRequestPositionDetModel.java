package com.savvy.hrmsnewapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequisitionRequestPositionDetModel {

    @SerializedName("PRM_CONTRACTOR_ID")
    @Expose
    private Object prmContractorId;
    @SerializedName("PRM_POSITION_HEAD_COUNT_DAY_SHIFT")
    @Expose
    private String prmPositionHeadCountDayShift;
    @SerializedName("PRM_POSITION_HEAD_COUNT_NIGHT_SHIFT")
    @Expose
    private String prmPositionHeadCountNightShift;
    @SerializedName("PRM_POSITION_NAME")
    @Expose
    private String prmPositionName;
    @SerializedName("PRM_POSITION_ROLE_ID")
    @Expose
    private String prmPositionRoleId;
    @SerializedName("RRWD_NO_OF_EMPLOYEE")
    @Expose
    private String rrwdNoOfEmployee;
    @SerializedName("TOTAL_MAN_POWER")
    @Expose
    private String totalManPower;


    public Object getPrmContractorId() {
        return prmContractorId;
    }

    public void setPrmContractorId(Object prmContractorId) {
        this.prmContractorId = prmContractorId;
    }

    public String getPrmPositionHeadCountDayShift() {
        return prmPositionHeadCountDayShift;
    }

    public void setPrmPositionHeadCountDayShift(String prmPositionHeadCountDayShift) {
        this.prmPositionHeadCountDayShift = prmPositionHeadCountDayShift;
    }

    public String getPrmPositionHeadCountNightShift() {
        return prmPositionHeadCountNightShift;
    }

    public void setPrmPositionHeadCountNightShift(String prmPositionHeadCountNightShift) {
        this.prmPositionHeadCountNightShift = prmPositionHeadCountNightShift;
    }

    public String getPrmPositionName() {
        return prmPositionName;
    }

    public void setPrmPositionName(String prmPositionName) {
        this.prmPositionName = prmPositionName;
    }

    public String getPrmPositionRoleId() {
        return prmPositionRoleId;
    }

    public void setPrmPositionRoleId(String prmPositionRoleId) {
        this.prmPositionRoleId = prmPositionRoleId;
    }

    public String getRrwdNoOfEmployee() {
        return rrwdNoOfEmployee;
    }

    public void setRrwdNoOfEmployee(String rrwdNoOfEmployee) {
        this.rrwdNoOfEmployee = rrwdNoOfEmployee;
    }

    public String getTotalManPower() {
        return totalManPower;
    }

    public void setTotalManPower(String totalManPower) {
        this.totalManPower = totalManPower;
    }

}
