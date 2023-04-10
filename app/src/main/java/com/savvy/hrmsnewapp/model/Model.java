package com.savvy.hrmsnewapp.model;

/**
 * Created by orapc7 on 5/6/2017.
 */

public class Model {

    private String punchDate;
    private String punchTime;

    public Model(String punchDate, String punchTime) {
        this.punchDate = punchDate;
        this.punchTime = punchTime;

    }

    public String getpunchDate() {
        return punchDate;
    }

    public String getpunchTime() {
        return punchTime;
    }

}
