package com.savvy.hrmsnewapp.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.service.GoogleServices;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.LogMaintainance;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class Gps1Activity extends AppCompatActivity implements LocationListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 60 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 60 * 2;

    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;

    Context context;
    public static Timer timer;
    TimerTask timerTask;
    Handler handler;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime = "";
    CustomTextView txt_heading, txtResult;
    Button btn_startService, btn_stopService, btn_Back;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "", employeeName = "";
    GoogleServices track;
    TextView tv1;


    protected void createLocationRequest() {
        try {
            LogMaintainance.WriteLog("NewTrackServiceActivity->Start CreateLocationRequest ");
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }catch (Exception e){
            LogMaintainance.WriteLog("NewTrackServiceActivity->CreateLocationRequest Exception = "+e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps1);

        tv1=(TextView)findViewById(R.id.locationText);

        LogMaintainance.WriteLog("NewTrackServiceActivity-> On Create start ");
         timer = new Timer();
         track = new GoogleServices(Gps1Activity.this);
        if(!track.canGetLocation()){
            LogMaintainance.WriteLog("NewTrackServiceActivity-> TrackGPS location get if ");
//            track.showSettingsAlert();
        } else{
            LogMaintainance.WriteLog("NewTrackServiceActivity-> TrackGPS location get else");
//            track.showSettingsAlert();
        }

        if (!isGooglePlayServicesAvailable()) {
            LogMaintainance.WriteLog("Google Play Services didn't work");
        }
        LogMaintainance.WriteLog("NewTrackServiceActivity-> Google Api Client  Start");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        LogMaintainance.WriteLog("NewTrackServiceActivity-> Google Api Client  Finish "+mGoogleApiClient.isConnected());
//
        createLocationRequest();

        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        employeeName = (shared.getString("EmpoyeeName", ""));

        txt_heading = (CustomTextView) findViewById(R.id.track_location_heading);
        LogMaintainance.WriteLog("NewTrackServiceActivity-> Employee Detail "+empoyeeId+" , "+employeeName);

        btn_startService = (Button) findViewById(R.id.btn_start);
        btn_stopService = (Button) findViewById(R.id. btn_stop);

        if (Constants.TRACK_ME_START_SERVICE == 2) {
            btn_startService.setEnabled(false);
            btn_stopService.setEnabled(true);
            btn_startService.setBackgroundResource(R.drawable.button_border_click);
            btn_stopService.setBackgroundResource(R.drawable.button_border);
        } else if (Constants.TRACK_ME_START_SERVICE == 1) {
            btn_startService.setEnabled(true);
            btn_stopService.setEnabled(false);
            btn_startService.setBackgroundResource(R.drawable.button_border);
            btn_stopService.setBackgroundResource(R.drawable.button_border_click);
        }

        btn_startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Constants.TRACK_ME_START_SERVICE = 2;
                btn_startService.setEnabled(false);
                btn_stopService.setEnabled(true);

                try {
                    btn_startService.setBackgroundResource(R.drawable.button_border_click);
                    btn_stopService.setBackgroundResource(R.drawable.button_border);
                    LogMaintainance.WriteLog("NewTrackServiceActivity-> Start Button Clicked");

                    timer = new Timer();
                    handler = new Handler();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(!track.canGetLocation()){
                                        LogMaintainance.WriteLog("Gps1Activity-> Start Button If Condition");
                                        track.showSettingsAlert();
                                    }else {
                                        LogMaintainance.WriteLog("Gps1Activity-> Start Button Else Condition");
                                        updateUI();
                                    }
                                }
                            });
                        }
                    };
                    timer.schedule(timerTask, 1000*60*1, 1000 * 60 * 1);
                } catch (Exception e) {
                    LogMaintainance.WriteLog("NewTrackServiceActivity->Timer Exception = "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        btn_stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer = new Timer();
                Constants.TRACK_ME_START_SERVICE = 1;

                btn_startService.setEnabled(true);
                btn_stopService.setEnabled(false);
                LogMaintainance.WriteLog("NewTrackServiceActivity-> Stop Button Clicked");

                btn_startService.setBackgroundResource(R.drawable.button_border);
                btn_stopService.setBackgroundResource(R.drawable.button_border_click);
                Toast.makeText(Gps1Activity.this, "Service Stop", Toast.LENGTH_SHORT).show();
                timer.cancel();
                stopLocationUpdates();
                mGoogleApiClient.disconnect();
            }
        });
    }

    private void stopLocationUpdates() {

        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        } else {
            if(mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
                Log.d(TAG, "Location update stopped .......................");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            startActivity(new Intent(Gps1Activity.this, Gps1Activity.class));
        }catch (Exception e){
            LogMaintainance.WriteLog("Exception onDestroy");
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
//            updateUI();
            Log.d(TAG, "Location update resumed .....................");
        }
    }
    protected void onPause() {
        super.onPause();
        updateUI();
//        stopLocationUpdates();
    }
    private void updateUI() {

        try {
            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            } else{
                if(mGoogleApiClient.isConnected()){
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
//                    startLocationUpdates();
//                    createLocationRequest();
//                                 /* .......error aa raha hai..... */
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Update Initiate");
                    Log.d(TAG, "UI update initiated .............");
                    if (null != mCurrentLocation) {
                        LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Update Get Location "+mCurrentLocation.getLatitude());
                        String lat = String.valueOf(mCurrentLocation.getLatitude());
                        String lng = String.valueOf(mCurrentLocation.getLongitude());

                        SendTrackMeData(lat,lng);
//                        timer.purge();

                        Log.e("Location Update ", "At Time: " + mLastUpdateTime + "\n" +
                                "Latitude: " + lat + "\n" +
                                "Longitude: " + lng + "\n" +
                                "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                                "Provider: " + mCurrentLocation.getProvider());
                        LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Update Get Location Location Update \nAt Time: " + mLastUpdateTime + "\n" +
                                "Latitude: " + lat + "\n" +
                                "Longitude: " + lng + "\n" +
                                "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                                "Provider: " + mCurrentLocation.getProvider()+"\n");


//            tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
//                    "Latitude: " + lat + "\n" +
//                    "Longitude: " + lng + "\n" +
//                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
//                    "Provider: " + mCurrentLocation.getProvider());
                    } else {
                        Log.d(TAG, "location is null ...............");
                    }
                }
            }
        }catch (Exception e){
            LogMaintainance.WriteLog("Exception UpdateLocation..............");
            e.printStackTrace();
        }
    }

    public void SendTrackMeData(String latitude, String longitude) {

        try {
            if(Utilities.isNetworkAvailable(getApplicationContext())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTrackMePost";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("userName", employeeName);
                params_final.put("employeeId", empoyeeId);
                params_final.put("latitude", latitude);
                params_final.put("longitude", longitude);

                LogMaintainance.WriteLog("NewTrackServiceActivity-> SendTrackMe Request "+params_final.toString());
                requestQueue = Volley.newRequestQueue(getApplicationContext());

                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
//
                                try {
                                    LogMaintainance.WriteLog("NewTrackServiceActivity-> SendTrackMe Request Response "+response.toString());
                                    jsonObjectRequest.cancel();
                                    requestQueue.stop();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        LogMaintainance.WriteLog("NewTrackServiceActivity-> SendTrackMe Request ErrorListener "+error.toString());
                        Log.e("Error", "" + error.getMessage());
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("securityToken", shared.getString("EMPLOYEE_ID_FINAL",""));
                        return params;
                    }


                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };

                int socketTimeout = 3000000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);

//
            } else{
            }


        }catch (Exception e){
            LogMaintainance.WriteLog("NewTrackServiceActivity-> SendTrackMe Request Exception "+e.getMessage());
            e.printStackTrace();
        }

    }


    private boolean isGooglePlayServicesAvailable() {

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
         tv1.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        if(mCurrentLocation!=null) {
            LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Changed " + mCurrentLocation.getLatitude());
        }
//        updateUI();

    }


    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {

    }

    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
