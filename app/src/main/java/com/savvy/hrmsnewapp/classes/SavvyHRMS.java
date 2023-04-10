package com.savvy.hrmsnewapp.classes;

import android.app.Application;

public class SavvyHRMS extends Application {
    public User user;
    @Override
    public void onCreate() {
        super.onCreate();
        user = new User(getApplicationContext());
    }
}
