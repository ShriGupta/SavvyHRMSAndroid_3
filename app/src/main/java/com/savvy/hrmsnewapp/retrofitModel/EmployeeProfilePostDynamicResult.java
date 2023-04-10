package com.savvy.hrmsnewapp.retrofitModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmployeeProfilePostDynamicResult {
    @SerializedName("CaptionName")
    @Expose
    private String captionName;
    @SerializedName("CaptionValue")
    @Expose
    private String captionValue;

    public String getCaptionName() {
        return captionName;
    }

    public void setCaptionName(String captionName) {
        this.captionName = captionName;
    }

    public String getCaptionValue() {
        return captionValue;
    }

    public void setCaptionValue(String captionValue) {
        this.captionValue = captionValue;
    }
}
