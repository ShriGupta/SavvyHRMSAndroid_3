package com.savvy.hrmsnewapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReimbursementApprovalModel {
    @SerializedName("GetReimbursementRequestDetailResult")
    @Expose
    private List<GetReimbursementRequestDetailResult> getReimbursementRequestDetailResult = null;


    public List<GetReimbursementRequestDetailResult> getGetReimbursementRequestDetailResult() {
        return getReimbursementRequestDetailResult;
    }

    public void setGetReimbursementRequestDetailResult(List<GetReimbursementRequestDetailResult> getReimbursementRequestDetailResult) {
        this.getReimbursementRequestDetailResult = getReimbursementRequestDetailResult;
    }
}
