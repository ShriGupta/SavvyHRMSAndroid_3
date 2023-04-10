package com.savvy.hrmsnewapp.teamFaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyHierarchyForMarkInOutPostResult {
    @SerializedName("MyHierarchyForMarkInOutPostResult")
    @Expose
    private List<MyHierarchyForMarkInOutPostResult> myHierarchyForMarkInOutPostResult = null;

    public List<MyHierarchyForMarkInOutPostResult> getMyHierarchyForMarkInOutPostResult() {
        return myHierarchyForMarkInOutPostResult;
    }

    public void setMyHierarchyForMarkInOutPostResult(List<MyHierarchyForMarkInOutPostResult> myHierarchyForMarkInOutPostResult) {
        this.myHierarchyForMarkInOutPostResult = myHierarchyForMarkInOutPostResult;
    }
}
