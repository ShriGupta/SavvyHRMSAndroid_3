package com.savvy.hrmsnewapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionAnswerModel {
    @SerializedName("EMT_TEMPLATE_ID")
    @Expose
    private String eMTTEMPLATEID;
    @SerializedName("OTQ_TEST_QUESTION_ID")
    @Expose
    private String oTQTESTQUESTIONID;
    @SerializedName("OTQ_TEST_QUESTION_NAME")
    @Expose
    private String oTQTESTQUESTIONNAME;
    @SerializedName("OTQ_TEST_QUESTION_TYPE")
    @Expose
    private String oTQTESTQUESTIONTYPE;

    private String OTQA_RIGHT_ANSWER;

    private String OTQA_RIGHT_ANSWER_ID;
    @SerializedName("OTQ_ANSWERS")
    @Expose
    private List<AnswersModel> oTQANSWERS = null;

    public QuestionAnswerModel() {
    }

    public QuestionAnswerModel(String eMTTEMPLATEID, String oTQTESTQUESTIONID, String oTQTESTQUESTIONNAME, String oTQTESTQUESTIONTYPE, String OTQA_RIGHT_ANSWER,String OTQA_RIGHT_ANSWER_ID, List<AnswersModel> oTQANSWERS) {
        this.eMTTEMPLATEID = eMTTEMPLATEID;
        this.oTQTESTQUESTIONID = oTQTESTQUESTIONID;
        this.oTQTESTQUESTIONNAME = oTQTESTQUESTIONNAME;
        this.oTQTESTQUESTIONTYPE = oTQTESTQUESTIONTYPE;
        this.OTQA_RIGHT_ANSWER = OTQA_RIGHT_ANSWER;
        this.OTQA_RIGHT_ANSWER_ID= OTQA_RIGHT_ANSWER_ID;
        this.oTQANSWERS = oTQANSWERS;
    }

    public String getEMTTEMPLATEID() {
        return eMTTEMPLATEID;
    }

    public void setEMTTEMPLATEID(String eMTTEMPLATEID) {
        this.eMTTEMPLATEID = eMTTEMPLATEID;
    }

    public String getOTQTESTQUESTIONID() {
        return oTQTESTQUESTIONID;
    }

    public void setOTQTESTQUESTIONID(String oTQTESTQUESTIONID) {
        this.oTQTESTQUESTIONID = oTQTESTQUESTIONID;
    }

    public String getOTQTESTQUESTIONNAME() {
        return oTQTESTQUESTIONNAME;
    }

    public void setOTQTESTQUESTIONNAME(String oTQTESTQUESTIONNAME) {
        this.oTQTESTQUESTIONNAME = oTQTESTQUESTIONNAME;
    }

    public String getOTQTESTQUESTIONTYPE() {
        return oTQTESTQUESTIONTYPE;
    }

    public void setOTQTESTQUESTIONTYPE(String oTQTESTQUESTIONTYPE) {
        this.oTQTESTQUESTIONTYPE = oTQTESTQUESTIONTYPE;
    }

    public String getOTQA_RIGHT_ANSWER() {
        return OTQA_RIGHT_ANSWER;
    }

    public void setOTQA_RIGHT_ANSWER(String OTQA_RIGHT_ANSWER) {
        this.OTQA_RIGHT_ANSWER = OTQA_RIGHT_ANSWER;
    }

    public String getOTQA_RIGHT_ANSWER_ID() {
        return OTQA_RIGHT_ANSWER_ID;
    }

    public void setOTQA_RIGHT_ANSWER_ID(String OTQA_RIGHT_ANSWER_ID) {
        this.OTQA_RIGHT_ANSWER_ID = OTQA_RIGHT_ANSWER_ID;
    }

    public List<AnswersModel> getOTQANSWERS() {
        return oTQANSWERS;
    }

    public void setOTQANSWERS(List<AnswersModel> oTQANSWERS) {
        this.oTQANSWERS = oTQANSWERS;
    }

}
