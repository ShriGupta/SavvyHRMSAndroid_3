package com.savvy.hrmsnewapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.savvy.hrmsnewapp.service.TrackMeService;

public class TrackMeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BroadcastReceiver"  , "You are in TrackMeReceiver class.");
        Intent background = new Intent(context, TrackMeService.class);
        ContextCompat.startForegroundService(context, background);
    }
}
