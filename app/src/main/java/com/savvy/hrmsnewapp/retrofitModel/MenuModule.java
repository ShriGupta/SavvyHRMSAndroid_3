package com.savvy.hrmsnewapp.retrofitModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenuModule {
    @SerializedName("MenuImageURL")
    @Expose
    private String menuImageURL;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("moduleName")
    @Expose
    private String moduleName;
    @SerializedName("orderBy")
    @Expose
    private String orderBy;
    @SerializedName("privilegeId")
    @Expose
    private String privilegeId;
    @SerializedName("privilegeName")
    @Expose
    private String privilegeName;

    public String getMenuImageURL() {
        return menuImageURL;
    }

    public void setMenuImageURL(String menuImageURL) {
        this.menuImageURL = menuImageURL;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(String privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }
}
