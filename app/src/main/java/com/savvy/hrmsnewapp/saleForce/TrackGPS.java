package com.savvy.hrmsnewapp.saleForce;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by orapc7 on 2/12/2017.
 */

public class TrackGPS implements LocationListener {

    Context context;
    boolean flag;
    LocationManager locationManager;
    Location loc;
    static int counter = 0;
    double latitude = 0.0, longitude = 0.0;

    public TrackGPS(Context context) {
        this.context = context;
        FindLocation();
    }

    public void FindLocation() {

        flag =  displayGpsStatus();
    //                        final ProgressDialog pDialog = new ProgressDialog(MarkAttendance.this);
    //                        pDialog.setMessage("Please Wait...");
    //                        pDialog.setIndeterminate(false);
    //                        pDialog.setCancelable(true);
    //                        pDialog.show();
//        if(flag)
//        {

            Log.d(TAG, "onClick");
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//            Log.d(TAG, "location Manager");
            if (locationManager != null) {
//                Log.d(TAG, "location Manager not equals to null");
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                    Log.d(TAG, "Network Found");
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
//                    Log.d(TAG, "Network Provider");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, this);
//                    Log.d(TAG, "Network Provider location Manager");
                    loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    Log.d(TAG, "Network Provider Last Known Location");

                    if (loc != null) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
//                        Log.e("Network Provider","Latitude : "+latitude+" Longitude : "+longitude);
                    }

                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    Log.d(TAG, "GPS Found");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
//                    Log.d(TAG, "GPS Found GPS Provider");
                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    Log.d(TAG, "GPS Found GPS Provider Last Known Location");

                    if (loc != null) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
//                        Log.e("GPS Provider","Latitude : "+latitude+" Longitude : "+longitude);
                    }
                }
            }
//        } else {
//            alertbox("Gps Status!!", "Your GPS is: OFF");
//        }

    }

    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
//            Log.e("Get Longitude","Latitude : "+latitude+" Longitude : "+longitude);
        }
        return longitude;
    }

    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
//            Log.e("Get Latitude","Latitude : "+latitude+" Longitude : "+longitude);
        }
        return latitude;
    }
    @Override
    public void onLocationChanged(Location loc) {
        if (loc != null) {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
//            Log.e("Get OnLocation Changed","Latitude : "+latitude+" Longitude : "+longitude);
        }
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

    public Boolean displayGpsStatus() {
        ContentResolver contentResolver = context.getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
        return gpsStatus;
    }

    protected void alertbox(String title, String mymessage) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(myIntent);
//                                Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
//                                intent.putExtra("enabled", true);
//                                context.sendBroadcast(intent);
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
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    private void turnGPSOff(){
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }
}
