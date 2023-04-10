package com.savvy.hrmsnewapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnswersModel {
    @SerializedName("OTQA_TEST_ANSWER")
    @Expose
    private String oTQATESTANSWER;
    @SerializedName("OTQA_TEST_QUESTION_ANSWER_ID")
    @Expose
    private String oTQATESTQUESTIONANSWERID;

    public String getOTQATESTANSWER() {
        return oTQATESTANSWER;
    }

    public void setOTQATESTANSWER(String oTQATESTANSWER) {
        this.oTQATESTANSWER = oTQATESTANSWER;
    }

    public String getOTQATESTQUESTIONANSWERID() {
        return oTQATESTQUESTIONANSWERID;
    }

    public void setOTQATESTQUESTIONANSWERID(String oTQATESTQUESTIONANSWERID) {
        this.oTQATESTQUESTIONANSWERID = oTQATESTQUESTIONANSWERID;
    }
}
