package com.savvy.hrmsnewapp.model;

/**
 * Created by am00347646 on 29-07-2016.
 */
public class DashPerformanceGraphModel {

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String test_id;
    public String test;
    public String percentage;
}
