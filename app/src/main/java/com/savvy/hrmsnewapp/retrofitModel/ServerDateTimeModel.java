package com.savvy.hrmsnewapp.retrofitModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerDateTimeModel {
    @SerializedName("LatLongLocationRequired")
    @Expose
    private String latelongFlag;
    @SerializedName("ServerDate")
    @Expose
    private String serverDate;
    @SerializedName("ServerDateDDMMYYYYY")
    @Expose
    private String serverDateDDMMYYYYY;
    @SerializedName("ServerTime")
    @Expose
    private String serverTime;
    @SerializedName("ServerTime24hour")
    @Expose
    private String serverTime24hour;
    @SerializedName("errorMessage")
    @Expose
    private Object errorMessage;

    public String getLatelongFlag() {
        return latelongFlag;
    }

    public void setLatelongFlag(String latelongFlag) {
        this.latelongFlag = latelongFlag;
    }

    public String getServerDate() {
        return serverDate;
    }

    public void setServerDate(String serverDate) {
        this.serverDate = serverDate;
    }

    public String getServerDateDDMMYYYYY() {
        return serverDateDDMMYYYYY;
    }

    public void setServerDateDDMMYYYYY(String serverDateDDMMYYYYY) {
        this.serverDateDDMMYYYYY = serverDateDDMMYYYYY;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getServerTime24hour() {
        return serverTime24hour;
    }

    public void setServerTime24hour(String serverTime24hour) {
        this.serverTime24hour = serverTime24hour;
    }

    public Object getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Object errorMessage) {
        this.errorMessage = errorMessage;
    }

}
