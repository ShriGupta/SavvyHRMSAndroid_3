package com.savvy.hrmsnewapp.calendar;

import android.content.Context;

/**
 * Created by orapc7 on 6/7/2017.
 */

public class Beans {

    Context context;
    String spinValue;

    public Beans(Context context){
        this.context = context;
    }

    public void setSpinValue(String spinValue){
        this.spinValue = spinValue;
    }

    public String getSpinValue(){
        return spinValue;
    }
}
