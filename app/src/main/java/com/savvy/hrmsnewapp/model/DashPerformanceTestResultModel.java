package com.savvy.hrmsnewapp.model;

/**
 * Created by am00347646 on 29-07-2016.
 */
public class DashPerformanceTestResultModel {

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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String test;
    public String percentage;
    public String rank;
}
