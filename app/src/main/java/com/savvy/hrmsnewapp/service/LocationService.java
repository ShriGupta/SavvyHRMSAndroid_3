package com.savvy.hrmsnewapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {

    private boolean statusOfGPS;
    private static GPSStatusCallback gpsStatusCallback;
    private Timer timer;

    @Override
    public void onCreate() {

        try {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.e("LocationService: ", "run");
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    gpsStatusCallback.gpsStatusChanged(statusOfGPS);
                }
            }, 0, 100* 50);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // super.onStartCommand(intent, flags, startId);
        Log.e("LocationService: ", "onStartCommand");

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void stopTimerTask() {
        Log.e("LocationService: ", "stopTimerTask");
        if (timer != null) {
            Log.d("LocationService TIMER", "timer canceled");
            timer.cancel();


        }

    }

       public static void setCallbacks(GPSStatusCallback callbacks) {
        gpsStatusCallback = callbacks;
        }


    public static void removeCallbacks(GPSStatusCallback callbacks) {
        gpsStatusCallback = callbacks;
    }


    @Override
    public void onDestroy() {
        Log.e("LocationService: ", "onDestroy");
        super.onDestroy();
        stopTimerTask();
        }



        public interface GPSStatusCallback{
            void gpsStatusChanged(boolean status);
    }
}


