package com.savvy.hrmsnewapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.LogMaintainance;


public class SplashActivity extends Activity {

    SharedPreferences sharedpreferences;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String empoyeeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseApp.initializeApp(this);
        LogMaintainance.WriteLog("Splash Start");
        FirebaseMessaging.getInstance().subscribeToTopic("NEWS");
        try {
            sharedpreferences = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
            empoyeeId = sharedpreferences.getString(Constants.EMPLOYEE_ID_FINAL, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::CheckLoginStatus, 200);

    }

    public void CheckLoginStatus() {
        String username = sharedpreferences.getString("USER_ID", "");
        String password = sharedpreferences.getString("PASSWORD", "");
        String ipaddress = sharedpreferences.getString("IPADDRESS", "");

        if (!(username.equals("") && password.equals(""))) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity_1.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("password", password);
            bundle.putString("IPADDRESS", ipaddress);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity_1.class);
            startActivity(intent);
            finish();
        }
    }
}
