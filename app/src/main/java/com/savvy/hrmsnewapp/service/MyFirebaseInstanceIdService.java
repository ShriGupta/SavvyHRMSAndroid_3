package com.savvy.hrmsnewapp.service;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.savvy.hrmsnewapp.utils.Config;

/**
 * Created by savvy on 1/22/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String token) {
        Log.e("token>>","<><><><>"+token);
        super.onNewToken(token);
        Log.e("token>>","<><><><>"+token);
        storeRegIdInPref(token);

        sendRegistrationToServer(token);
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }



    private void sendRegistrationToServer(final String token) {
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }
}
