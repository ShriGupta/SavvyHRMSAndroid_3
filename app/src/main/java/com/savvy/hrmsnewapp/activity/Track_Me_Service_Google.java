package com.savvy.hrmsnewapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.service.Track_Me_Service;
import com.savvy.hrmsnewapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orapc7 on 04-Jan-18.
 */

public class Track_Me_Service_Google extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "", employeeName = "";

    CustomTextView txt_heading, txtResult;
    Button btn_startService, btn_stopService, btn_Back;

    protected static final String TAG = "LocationOnOff";

    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track__me);


        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        employeeName = (shared.getString("EmpoyeeName", ""));

        txt_heading = (CustomTextView) findViewById(R.id.track_location_heading);

        btn_startService = (Button) findViewById(R.id.btn_startService);
        btn_stopService = (Button) findViewById(R.id.btn_stopService);
        btn_Back = (Button) findViewById(R.id.btn_Back);

        if(Constants.TRACK_ME_START_SERVICE == 2){
            btn_startService.setEnabled(false);
            btn_stopService.setEnabled(true);
            btn_startService.setBackgroundResource(R.drawable.button_border_click);
            btn_stopService.setBackgroundResource(R.drawable.button_border);
        } else if(Constants.TRACK_ME_START_SERVICE == 1){
            btn_startService.setEnabled(true);
            btn_stopService.setEnabled(false);
            btn_startService.setBackgroundResource(R.drawable.button_border);
            btn_stopService.setBackgroundResource(R.drawable.button_border_click);
        }

        setUpGClient();
        getMyLocation();

        String TrackHeading = "<font color=\"#277ddb\"><bold><u>" + "Track My Location" + "</u></bold></font>";
        txt_heading.setText(Html.fromHtml(TrackHeading));
        txt_heading.setTextSize(20);

        btn_startService.setOnClickListener(this);
        btn_stopService.setOnClickListener(this);
        btn_Back.setOnClickListener(this);

        assert getSupportActionBar()!=null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_startService){

            Constants.TRACK_ME_START_SERVICE = 2;
            btn_startService.setEnabled(false);
            btn_stopService.setEnabled(true);

            btn_startService.setBackgroundResource(R.drawable.button_border_click);
            btn_stopService.setBackgroundResource(R.drawable.button_border);
            try{
                Intent intent = new Intent(Track_Me_Service_Google.this, Track_Me_Service.class);
                this.startService(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(v.getId()==R.id.btn_stopService){
            Constants.TRACK_ME_START_SERVICE = 1;
            btn_startService.setEnabled(true);
            btn_stopService.setEnabled(false);

            btn_startService.setBackgroundResource(R.drawable.button_border);
            btn_stopService.setBackgroundResource(R.drawable.button_border_click);

            try{
                Intent intent = new Intent(Track_Me_Service_Google.this, Track_Me_Service.class);
                this.stopService(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(v.getId()==R.id.btn_Back){
            Intent intent = new Intent(this,DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId","32");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }
    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(Track_Me_Service_Google.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    public void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(Track_Me_Service_Google.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(100);
                    locationRequest.setFastestInterval(100);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, Track_Me_Service_Google.this);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat.checkSelfPermission(Track_Me_Service_Google.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                                        if(mylocation!=null) {
//                                            latitude = mylocation.getLatitude();
//                                            longitude = mylocation.getLongitude();
//
////                                            Toast.makeText(MarkAttendanceGoogleApi.this, "Location button "+latitude+","+longitude, Toast.LENGTH_SHORT).show();
//
//                                        }
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically

                                        status.startResolutionForResult(Track_Me_Service_Google.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
//                        moveMap();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(Track_Me_Service_Google.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(Track_Me_Service_Google.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home) {
            Intent intent = new Intent(this,DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId","32");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
//            startActivity(new Intent(this, DashBoardActivity.class));
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putString("PostionId","32");
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
