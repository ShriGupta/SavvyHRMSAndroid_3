package com.savvy.hrmsnewapp.service;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.savvy.hrmsnewapp.utils.LogMaintainance;

/**
 * Created by hariom on 26/4/17.
 */
public class TrackGPS extends Service implements LocationListener {

    private final Context mContext;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;

    //    Location loc;
//    double latitude;
//    double longitude;
    /*private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;*/
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1;
    protected LocationManager locationManager;

    public TrackGPS(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    private Location getLocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                this.canGetLocation = false;
                Toast.makeText(mContext, "No Service Provider Available", Toast.LENGTH_SHORT).show();
            } else {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//
//                {
                this.canGetLocation = true;

                // First get location from Network Provider
                if (checkNetwork) {
                    //Toast.makeText(mContext, "Network", Toast.LENGTH_SHORT).show();

                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            TrackMeLocationConstants.LOCATION = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (TrackMeLocationConstants.LOCATION != null) {
                                TrackMeLocationConstants.LOC_LATITUDE = TrackMeLocationConstants.LOCATION.getLatitude();
                                TrackMeLocationConstants.LOC_LONGITUDE = TrackMeLocationConstants.LOCATION.getLongitude();
                            }
                        }

//                            if (TrackMeLocationConstants.LOCATION != null) {
//                                TrackMeLocationConstants.LOC_LATITUDE = TrackMeLocationConstants.LOCATION.getLatitude();
//                                TrackMeLocationConstants.LOC_LONGITUDE = TrackMeLocationConstants.LOCATION.getLongitude();
//                            }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {
                    // Toast.makeText(mContext, "GPS", Toast.LENGTH_SHORT).show();
                    if (TrackMeLocationConstants.LOCATION == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                TrackMeLocationConstants.LOCATION = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (TrackMeLocationConstants.LOCATION != null) {
                                    TrackMeLocationConstants.LOC_LATITUDE = TrackMeLocationConstants.LOCATION.getLatitude();
                                    TrackMeLocationConstants.LOC_LONGITUDE = TrackMeLocationConstants.LOCATION.getLongitude();
                                }
                            }
                        } catch (SecurityException e) {

                            e.printStackTrace();
                        }
                    }
                }
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TrackMeLocationConstants.LOCATION;
    }

    public double getLongitude() {
        if (TrackMeLocationConstants.LOCATION != null) {
            TrackMeLocationConstants.LOC_LONGITUDE = TrackMeLocationConstants.LOCATION.getLongitude();
        }
        return TrackMeLocationConstants.LOC_LONGITUDE;
    }

    public double getLatitude() {
        if (TrackMeLocationConstants.LOCATION != null) {
            TrackMeLocationConstants.LOC_LATITUDE = TrackMeLocationConstants.LOCATION.getLatitude();
        }
        return TrackMeLocationConstants.LOC_LONGITUDE;
    }

    public boolean canGetLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            LogMaintainance.WriteLog("TrackGps-> Excpetion CanGetLocation " + e.getMessage());
        }

        return checkGPS && checkNetwork;
//        return this.canGetLocation;
    }

//    public boolean displayGpsStatus() {
//        try {
//            ContentResolver contentResolver = getBaseContext().getContentResolver();
//            boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
//            if (gpsStatus) {
//                return true;
//            } else {
//                return false;
//            }
//        }catch (Exception e){
//            LogMaintainance.WriteLog("TrackGPS-> displayGpsStatus Exception "+e.getMessage());
//        }
//        return false;
//    }

    public void showSettingsAlert() {
//        final AmazingStatus status = new AmazingStatus();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS Not Enabled");
        alertDialog.setMessage("Do you wants to turn On GPS");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        if (alertDialog != null && !alertDialog.create().isShowing()) {
            alertDialog.show();
        }
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(TrackGPS.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        TrackMeLocationConstants.LOCATION = location;
        String providerName = location.getProvider();

        if (providerName.equals(LocationManager.GPS_PROVIDER)) {
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("GPS Enabled", "GPS Enabled");
                if (locationManager != null) {
                    TrackMeLocationConstants.LOCATION = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (TrackMeLocationConstants.LOCATION != null) {
                        TrackMeLocationConstants.LOC_LATITUDE = TrackMeLocationConstants.LOCATION.getLatitude();
                        TrackMeLocationConstants.LOC_LONGITUDE = TrackMeLocationConstants.LOCATION.getLongitude();
                    }
                }
            } catch (SecurityException e) {

                e.printStackTrace();
            }
        }

        if (providerName.equals(LocationManager.NETWORK_PROVIDER)) {
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {
                    TrackMeLocationConstants.LOCATION = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (TrackMeLocationConstants.LOCATION != null) {
                        TrackMeLocationConstants.LOC_LATITUDE = TrackMeLocationConstants.LOCATION.getLatitude();
                        TrackMeLocationConstants.LOC_LONGITUDE = TrackMeLocationConstants.LOCATION.getLongitude();
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }


    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
