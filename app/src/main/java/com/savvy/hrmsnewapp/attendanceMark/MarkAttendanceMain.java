package com.savvy.hrmsnewapp.attendanceMark;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by savvy on 7/2/2018.
 */

public class MarkAttendanceMain extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener, View.OnClickListener {

    private static final int REQUEST_CHECK_SETTINGS_GPS = 11;
    private static final long INTERVAL = 1000 * 60;
    private static final int ACCESS_FINE_LOCATION = 111;

    private CoordinatorLayout coordinatorLayout;
    private CustomTextView txv_currentDate;
    private CustomTextView txv_currentTime;
    private EditText edt_messagefeedback;
    private Button btn_submit;

    private String token;
    private String empoyeeId;
    private String username;
    private String gpsMendatory;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences shared;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private double locationLatitude;
    private double locationLongitude;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        setUpGClient();
        initView();
    }

    private void initView() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));
        gpsMendatory = (shared.getString(Constants.GPS_MENDATORY, ""));

        txv_currentDate = (CustomTextView) findViewById(R.id.txv_currentDate);
        txv_currentTime = (CustomTextView) findViewById(R.id.txv_currentTime);
        edt_messagefeedback = (EditText) findViewById(R.id.edt_messagefeedback);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        viewOnClickListener();
    }

    private void viewOnClickListener() {
        btn_submit.setOnClickListener(this);

        setDataInView();
    }

    private void setDataInView() {
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:MM:ss", Locale.getDefault());
            txv_currentDate.setText(sdf.format(date));
            txv_currentTime.setText(sdf1.format(date));

            assert getSupportActionBar() != null;
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpGClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MarkAttendanceMain.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck1 = ContextCompat.checkSelfPermission(MarkAttendanceMain.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
        } else {
            getMyLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("MissingPermission")
    private void getMyLocation() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                LocationRequest mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(INTERVAL);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
                builder.setAlwaysShow(true);

                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                    @Override
                    public void onResult(@NonNull LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied. The client can initialize location requests here.
                                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                if (mCurrentLocation != null) {
                                    locationLatitude = mCurrentLocation.getLatitude();
                                    locationLongitude = mCurrentLocation.getLongitude();
                                }
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                    status.startResolutionForResult(MarkAttendanceMain.this, REQUEST_CHECK_SETTINGS_GPS);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                                break;
                        }
                    }
                });
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case RESULT_OK:
                        getMyLocation();
                        break;
                    case RESULT_CANCELED:

                        break;
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyLocation();
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (mCurrentLocation != null) {
            locationLatitude = mCurrentLocation.getLatitude();
            locationLongitude = mCurrentLocation.getLongitude();
            Log.d("LOCATION", String.valueOf(locationLatitude) + " " + String.valueOf(locationLongitude));
        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (mCurrentLocation != null) {
                    btn_submit.setEnabled(false);
                    markAttendancePost("Test", String.valueOf(locationLatitude), String.valueOf(locationLongitude));
                } else {
                    getMyLocation();
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void markAttendancePost(String attendanceRemark, String latitude, String longitude) {
        try {
            Log.e("Mark Att", " Latitude =  " + latitude + " ,Longitude = " + longitude);
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(MarkAttendanceMain.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveAttendanceMarkPost";
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", attendanceRemark);
            params_final.put("latitude", latitude);
            params_final.put("longitude", longitude);

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            pDialog.dismiss();

                            try {
                                JSONObject jsonobj = response.getJSONObject("SaveAttendanceMarkPostResult");
                                String statusId = jsonobj.getString("statusId");
                                String statusDescription = jsonobj.getString("statusDescription");
                                String errorMessage = jsonobj.getString("errorMessage");

                                if (statusId.equals("1")) {
                                    Utilities.showDialog(coordinatorLayout, statusDescription);
                                    edt_messagefeedback.setText("");
                                } else if (statusId.equals("2")) {
                                    Utilities.showDialog(coordinatorLayout, statusDescription);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error", "" + error.getMessage());
                }
            });

            int socketTimeout = 300000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
