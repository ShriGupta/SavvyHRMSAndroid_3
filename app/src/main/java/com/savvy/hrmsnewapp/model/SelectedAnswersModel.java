package com.savvy.hrmsnewapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SelectedAnswersModel implements Parcelable {

    private String quesId, quesName, ansId, ansName, correctAnsId, correcrAnsName;

    public SelectedAnswersModel(String quesId, String quesName, String ansId, String ansName, String correctAnsId, String correcrAnsName) {
        this.quesId = quesId;
        this.quesName = quesName;
        this.ansId = ansId;
        this.ansName = ansName;
        this.correctAnsId = correctAnsId;
        this.correcrAnsName = correcrAnsName;
    }

    protected SelectedAnswersModel(Parcel in) {
        quesId = in.readString();
        quesName = in.readString();
        ansId = in.readString();
        ansName = in.readString();
        correctAnsId = in.readString();
        correcrAnsName = in.readString();
    }

    public static final Creator<SelectedAnswersModel> CREATOR = new Creator<SelectedAnswersModel>() {
        @Override
        public SelectedAnswersModel createFromParcel(Parcel in) {
            return new SelectedAnswersModel(in);
        }

        @Override
        public SelectedAnswersModel[] newArray(int size) {
            return new SelectedAnswersModel[size];
        }
    };

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }

    public String getQuesName() {
        return quesName;
    }

    public void setQuesName(String quesName) {
        this.quesName = quesName;
    }

    public String getAnsId() {
        return ansId;
    }

    public void setAnsId(String ansId) {
        this.ansId = ansId;
    }

    public String getAnsName() {
        return ansName;
    }

    public void setAnsName(String ansName) {
        this.ansName = ansName;
    }

    public String getCorrectAnsId() {
        return correctAnsId;
    }

    public void setCorrectAnsId(String correctAnsId) {
        this.correctAnsId = correctAnsId;
    }

    public String getCorrecrAnsName() {
        return correcrAnsName;
    }

    public void setCorrecrAnsName(String correcrAnsName) {
        this.correcrAnsName = correcrAnsName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quesId);
        dest.writeString(quesName);
        dest.writeString(ansId);
        dest.writeString(ansName);
        dest.writeString(correctAnsId);
        dest.writeString(correcrAnsName);
    }
}
