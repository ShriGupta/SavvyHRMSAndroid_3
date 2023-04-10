package com.savvy.hrmsnewapp.retrofitModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileDataModel {
    @SerializedName("EmployeeProfilePostDynamicResult")
    @Expose
    public List<EmployeeProfilePostDynamicResult> employeeProfilePostDynamicResult = null;

    public List<EmployeeProfilePostDynamicResult> getEmployeeProfilePostDynamicResult() {
        return employeeProfilePostDynamicResult;
    }
    public void setEmployeeProfilePostDynamicResult(List<EmployeeProfilePostDynamicResult> employeeProfilePostDynamicResult) {
        this.employeeProfilePostDynamicResult = employeeProfilePostDynamicResult;
    }
}
