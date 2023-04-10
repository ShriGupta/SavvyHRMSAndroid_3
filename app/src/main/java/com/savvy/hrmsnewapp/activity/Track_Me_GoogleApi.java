package com.savvy.hrmsnewapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by orapc7 on 29-Dec-17.
 */

public class Track_Me_GoogleApi extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, View.OnClickListener{

    GoogleApiClient googleApiClient;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    Button btn_startService, btn_stopService, btn_Back;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "", employeeName = "";

    CustomTextView txt_heading, txtResult;
    Timer timer;
    TimerTask timerTask;
    Handler handler;

    Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    private double longitude;
    private double latitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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


        String TrackHeading = "<font color=\"#277ddb\"><bold><u>" + "Track My Location" + "</u></bold></font>";
        txt_heading.setText(Html.fromHtml(TrackHeading));
        txt_heading.setTextSize(20);

        btn_startService.setOnClickListener(this);
        btn_stopService.setOnClickListener(this);
        btn_Back.setOnClickListener(this);

        getMyLocation();
        setUpGClient();

        assert getSupportActionBar()!=null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(Track_Me_GoogleApi.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_startService){
            if(mylocation!=null) {
                Constants.TRACK_ME_START_SERVICE = 2;

                btn_startService.setEnabled(false);
                btn_stopService.setEnabled(true);

                try {
                    btn_startService.setBackgroundResource(R.drawable.button_border_click);
                    btn_stopService.setBackgroundResource(R.drawable.button_border);
                    Toast.makeText(getApplicationContext(), "Service Start", Toast.LENGTH_LONG).show();
                    timer = new Timer();
                    handler = new Handler();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    SendTrackMeData(employeeName, empoyeeId, String.valueOf(mylocation.getLatitude()), String.valueOf(mylocation.getLongitude()));
                                }
                            });
                        }
                    };
                    timer.schedule(timerTask, 0, 1000 * 30);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                Toast.makeText(this, "Location not found, Please try after some time.", Toast.LENGTH_SHORT).show();
                getMyLocation();
            }
        }
        if(v.getId()==R.id.btn_stopService){
           try{
               Constants.TRACK_ME_START_SERVICE = 1;

               btn_startService.setEnabled(true);
               btn_stopService.setEnabled(false);

//               jsonObjectRequest.cancel();
//               requestQueue.stop();
               btn_startService.setBackgroundResource(R.drawable.button_border);
               btn_stopService.setBackgroundResource(R.drawable.button_border_click);
               Toast.makeText(Track_Me_GoogleApi.this, "Service Stop", Toast.LENGTH_SHORT).show();
               timer.cancel();
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
        mylocation = location;
        if (mylocation != null) {
            Double latitude=mylocation.getLatitude();
            Double longitude=mylocation.getLongitude();
            Log.e("Locqation","Latitude = "+latitude+" Longitude = "+longitude);
//            Toast.makeText(getApplicationContext(),"Location Latitude = "+latitude+" Longitude = "+longitude,Toast.LENGTH_LONG).show();
//            latitudeTextView.setText("Latitude : "+latitude);
//            longitudeTextView.setText("Longitude : "+longitude);
            //Or Do whatever you want with your location

        }
    }

    public Location getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(Track_Me_GoogleApi.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(100);
                    locationRequest.setFastestInterval(100);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, Track_Me_GoogleApi.this);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat.checkSelfPermission(Track_Me_GoogleApi.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                        if(mylocation!=null) {
                                            latitude = mylocation.getLatitude();
                                            longitude = mylocation.getLongitude();

//                                            Toast.makeText(MarkAttendanceGoogleApi.this, "Location button "+latitude+","+longitude, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        if(mylocation!=null) {
                                            latitude = mylocation.getLatitude();
                                            longitude = mylocation.getLongitude();

//                                            Toast.makeText(MarkAttendanceGoogleApi.this, "Location Resolution "+latitude+","+longitude, Toast.LENGTH_SHORT).show();

                                        }
                                        status.startResolutionForResult(Track_Me_GoogleApi.this,
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
        return mylocation;
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
        int permissionLocation = ContextCompat.checkSelfPermission(Track_Me_GoogleApi.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(Track_Me_GoogleApi.this,
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
        finish();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public void SendTrackMeData(String userName, String employeeId, String latitude, String longitude){
        Constants.COMPARE_DATE_API = true;
        Log.e("Compare Method",""+Constants.COMPARE_DATE_API);

        try {
            if(Utilities.isNetworkAvailable(getApplicationContext())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTrackMePost";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("userName", userName);
                params_final.put("employeeId", employeeId);
                params_final.put("latitude", latitude);
                params_final.put("longitude", longitude);


                requestQueue = Volley.newRequestQueue(getApplicationContext());

                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
//                                int len = (String.valueOf(response)).length();
//                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
//                                Log.e("Value", " Length = " + len + " Value = " + response.toString());
                                try {
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
            } else{
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
