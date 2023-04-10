package com.savvy.hrmsnewapp.service;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by hariom on 25/4/17.
 */
public class AppLocationService  implements LocationListener {



    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            Log.e("Lattitude :",""+location.getLatitude());
            Log.e("Longitude :",""+location.getLongitude());
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /*@Override
    public IBinder onBind(Intent arg0) {
        return null;
    }*/

}
