package com.savvy.hrmsnewapp.fragment.CompanyDirectory.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompanyDirectoryModel {
    @SerializedName("MyHierarchyBasedOnRolePostResult")
    @Expose
    private List<MyHierarchyBasedOnRolePostResult> myHierarchyBasedOnRolePostResult;


    public List<MyHierarchyBasedOnRolePostResult> getMyHierarchyBasedOnRolePostResult() {
        return myHierarchyBasedOnRolePostResult;
    }

    public void setMyHierarchyBasedOnRolePostResult(List<MyHierarchyBasedOnRolePostResult> myHierarchyBasedOnRolePostResult) {
        this.myHierarchyBasedOnRolePostResult = myHierarchyBasedOnRolePostResult;
    }
}
