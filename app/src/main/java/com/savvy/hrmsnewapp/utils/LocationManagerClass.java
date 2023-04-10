package com.savvy.hrmsnewapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.savvy.hrmsnewapp.interfaces.LocationInterface;

public class LocationManagerClass {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private final Context context;

    LocationInterface locationInterface;

    public LocationManagerClass(Context context, LocationInterface locationInterface) {
        this.context = context;
        this.locationInterface = locationInterface;
    }


    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
        locationRequest.setSmallestDisplacement(1);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);



        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location mLocation : locationResult.getLocations()) {

                    if (mLocation != null) {
                        locationInterface.onLocationRecieve(mLocation);
                    }
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}
