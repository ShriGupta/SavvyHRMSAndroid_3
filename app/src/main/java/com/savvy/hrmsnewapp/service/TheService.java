package com.savvy.hrmsnewapp.service;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.savvy.hrmsnewapp.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by orapc7 on 15/11/2017.
 */

public class TheService extends Service implements LocationListener {

    String cityName = "";
    private LocationManager locationManager = null;
    boolean flag;
    Timer timer;
    TimerTask timerTask;
    Handler handler;

    double lan = 0.0;
    double lat = 0.0;

    Location loc;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Toast.makeText(getApplicationContext(), "Service Start", Toast.LENGTH_LONG).show();
            timer = new Timer();
            handler = new Handler();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(Constants.NO_NETWORK){
                                locationFind();
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, 0, 1000 * 5);

        }catch(Exception e){
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(TheService.this, "Service Stop", Toast.LENGTH_SHORT).show();
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void locationFind() {
        try {
            flag = displayGpsStatus();
            if (flag) {

                Log.v(TAG, "onClick");
//                        locationListener = new MyLocationListener();

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

                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, TheService.this);
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null) {
//                    latituteField.setText("Latitude: " + String.valueOf(loc.getLatitude()));
//                    longitudeField.setText("Longitude: " + String.valueOf(loc.getLongitude()));
                            lan = loc.getLongitude();
                            lat = loc.getLatitude();
                        }
                    } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, TheService.this);
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            lan = loc.getLongitude();
                            lat = loc.getLatitude();
                        }
                    }
                    Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                        if (addresses.size() > 0)
                            System.out.println(addresses.get(0).getLocality());
                        Address address = addresses.get(0);
                        cityName = addresses.get(0).getLocality()+","+addresses.get(0).getAddressLine(0)+","+addresses.get(0).getPostalCode();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String s = loc.getLatitude() + "\n" + loc.getLongitude() + "\nMy Currrent City is: " + cityName;
                    Toast.makeText(TheService.this, "City Name = " + cityName + " Latitude = " + lat + " Longitude = " + lan, Toast.LENGTH_LONG).show();
//                addressField.setText("My City Name is "+cityName);

                }
            } else {
                alertbox("Gps Status!!", "Your GPS is: OFF");
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Exception "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
        return gpsStatus;
    }

    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location loc) {



//        String longitude = "Longitude: " + loc.getLongitude();
//        Log.v(TAG, longitude);
//        String latitude = "Latitude: " + loc.getLatitude();
//        Log.v(TAG, latitude);
//
//
//        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
//        List<Address> addresses;
//        try {
//            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
//            if (addresses.size() > 0)
//                System.out.println(addresses.get(0).getLocality());
//            cityName = addresses.get(0).getLocality();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String s = loc.getLatitude() + "\n" + loc.getLongitude() + "\nMy Currrent City is: " + cityName;
////        addressField.setText("My City Name is "+cityName);
//        Toast.makeText(getBaseContext(), "Location changed : Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude()+" , City = "+cityName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
