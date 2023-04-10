package com.savvy.hrmsnewapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QRLoginModel {
    @SerializedName("IsServerActive")
    @Expose
    private Object isServerActive;
    @SerializedName("LastPasswordChange")
    @Expose
    private String lastPasswordChange;
    @SerializedName("QRCode")
    @Expose
    private String qRCode;
    @SerializedName("ShowPassword")
    @Expose
    private String showPassword;
    @SerializedName("accessModules")
    @Expose
    private Object accessModules;
    @SerializedName("clientLogoPath")
    @Expose
    private String clientLogoPath;
    @SerializedName("employeeCode")
    @Expose
    private String employeeCode;
    @SerializedName("employeeId")
    @Expose
    private String employeeId;
    @SerializedName("employeeName")
    @Expose
    private String employeeName;
    @SerializedName("employeePhotoPath")
    @Expose
    private String employeePhotoPath;
    @SerializedName("errorMessage")
    @Expose
    private Object errorMessage;
    @SerializedName("groupId")
    @Expose
    private String groupId;
    @SerializedName("isLocked")
    @Expose
    private String isLocked;
    @SerializedName("isValidUser")
    @Expose
    private String isValidUser;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("regularExpressionAndroid")
    @Expose
    private String regularExpressionAndroid;
    @SerializedName("regularExpressioniPhone")
    @Expose
    private String regularExpressioniPhone;
    @SerializedName("securityToken")
    @Expose
    private String securityToken;
    @SerializedName("trackMeIntervalSecond")
    @Expose
    private String trackMeIntervalSecond;
    @SerializedName("userMobileRegistrationId")
    @Expose
    private Object userMobileRegistrationId;
    @SerializedName("userName")
    @Expose
    private String userName;

    public Object getIsServerActive() {
        return isServerActive;
    }

    public void setIsServerActive(Object isServerActive) {
        this.isServerActive = isServerActive;
    }

    public String getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(String lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    public String getQRCode() {
        return qRCode;
    }

    public void setQRCode(String qRCode) {
        this.qRCode = qRCode;
    }

    public String getShowPassword() {
        return showPassword;
    }

    public void setShowPassword(String showPassword) {
        this.showPassword = showPassword;
    }

    public Object getAccessModules() {
        return accessModules;
    }

    public void setAccessModules(Object accessModules) {
        this.accessModules = accessModules;
    }

    public String getClientLogoPath() {
        return clientLogoPath;
    }

    public void setClientLogoPath(String clientLogoPath) {
        this.clientLogoPath = clientLogoPath;
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

    public String getEmployeePhotoPath() {
        return employeePhotoPath;
    }

    public void setEmployeePhotoPath(String employeePhotoPath) {
        this.employeePhotoPath = employeePhotoPath;
    }

    public Object getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Object errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getIsValidUser() {
        return isValidUser;
    }

    public void setIsValidUser(String isValidUser) {
        this.isValidUser = isValidUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegularExpressionAndroid() {
        return regularExpressionAndroid;
    }

    public void setRegularExpressionAndroid(String regularExpressionAndroid) {
        this.regularExpressionAndroid = regularExpressionAndroid;
    }

    public String getRegularExpressioniPhone() {
        return regularExpressioniPhone;
    }

    public void setRegularExpressioniPhone(String regularExpressioniPhone) {
        this.regularExpressioniPhone = regularExpressioniPhone;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getTrackMeIntervalSecond() {
        return trackMeIntervalSecond;
    }

    public void setTrackMeIntervalSecond(String trackMeIntervalSecond) {
        this.trackMeIntervalSecond = trackMeIntervalSecond;
    }

    public Object getUserMobileRegistrationId() {
        return userMobileRegistrationId;
    }

    public void setUserMobileRegistrationId(Object userMobileRegistrationId) {
        this.userMobileRegistrationId = userMobileRegistrationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
