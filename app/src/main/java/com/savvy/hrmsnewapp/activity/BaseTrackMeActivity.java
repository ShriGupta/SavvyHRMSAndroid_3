package com.savvy.hrmsnewapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.savvy.hrmsnewapp.BuildConfig;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.service.VisService;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;

import static com.google.android.gms.common.api.GoogleApiClient.*;
//mport static com.savvy.hrmsnewapp.activity.Timer.*;

public class BaseTrackMeActivity extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener, View.OnClickListener {
    private static final int REQUEST_CHECK_SETTINGS_GPS = 11;
    private static final long INTERVAL = 1000 * 60;
    private static final int ACCESS_LOCATION = 111;
    private GoogleApiClient mGoogleApiClient;
    public static Location mCurrentLocation;
    private Double locationLatitude;
    private Double locationLongitude;
    private CoordinatorLayout coordinatorLayout;
    private Button btn_startService;
    private Button btn_startStop;
    private TextView txt_CurrentTrackingStatus;


    /////////Anubhav///////
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    Timer myTimer = null;
    private Boolean mRequestingLocationUpdates;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private String mLastUpdateTime;
    private static final String TAG = BaseTrackMeActivity.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private String empoyeeId = "";
    private String employeeName = "";
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    Intent intent;
    // public Timer timer=null;
    private double latitude;
    private double longitude;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_track_me);
        restoreValuesFromBundle(savedInstanceState);

        SharedPreferences shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        employeeName = (shared.getString("EmpoyeeName", ""));

        initView();
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    private void initView() {
        btn_startService = (Button) findViewById(R.id.btn_startService);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //btn_startStop = (Button) findViewById(R.id.btn_startStop);
        txt_CurrentTrackingStatus = (TextView) findViewById(R.id.CurrentTrackingStatus);

//        setUpGClient();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
//         mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//         mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        clickListener();

    }

    private synchronized void setUpGClient() {
        mGoogleApiClient = new Builder(BaseTrackMeActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void clickListener() {
        btn_startService.setOnClickListener(this);
        btn_startStop.setOnClickListener(this);

        changeBackGround();

    }

    private void showPermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                        // sendToServer();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // open device settings when the permission is
                        // denied permanently
                        if (response.isPermanentlyDenied()) openSettings();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        checkPermissions();
        if (ActivityCompat.checkSelfPermission(BaseTrackMeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BaseTrackMeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private void checkPermissions() {
//        int permissionCheck1 = ContextCompat.checkSelfPermission(BaseTrackMeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(BaseTrackMeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION);
//        } else {
//            getMyLocation();
//        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getMyLocation();
                } else {
                    Utilities.showDialog(coordinatorLayout, "Please allow permission to get location");
                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mCurrentLocation = location;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startService:
                try {
                    Constants.TRACK_ME_START_SERVICE = 2;
                    changeBackGround();




                /*   if (mCurrentLocation != null) {
                        location = BaseTrackMeActivity.mCurrentLocation;
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        sendDataToServer(String.valueOf(latitude), String.valueOf(longitude));
                    }*/






                    startService(new Intent(BaseTrackMeActivity.this, VisService.class));
                    showPermissions();


                } catch (Exception ex) {
                    Utilities.showDialog(coordinatorLayout, ex.toString());
                }
                break;
           /* case R.id.btn_startStop:
                try {
                    Constants.TRACK_ME_START_SERVICE = 1;
                    changeBackGround();
                    //stopLocationUpdates();
                    mRequestingLocationUpdates = false;
                    stopService(new Intent(BaseTrackMeActivity.this, VisService.class));
                } catch (Exception ex) {
                    Utilities.showDialog(coordinatorLayout, ex.toString());
                }
                break;*/
        }
    }




    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.i("isMyServiceRunning?", true + "");
                    return true;
                }
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    private void changeBackGround() {
        if (Constants.TRACK_ME_START_SERVICE == 2) {
            txt_CurrentTrackingStatus.setText("Current Tracking Status: Running");
            btn_startService.setEnabled(false);
            btn_startStop.setEnabled(true);
            btn_startService.setBackgroundResource(R.drawable.button_border_click);
            btn_startStop.setBackgroundResource(R.drawable.button_border);
        } else if (Constants.TRACK_ME_START_SERVICE == 1) {
            txt_CurrentTrackingStatus.setText("Current Tracking Status: OFF");
            btn_startService.setEnabled(true);
            btn_startStop.setEnabled(false);
            btn_startService.setBackgroundResource(R.drawable.button_border);
            btn_startStop.setBackgroundResource(R.drawable.button_border_click);
        }
    }

    ////////////////Anubhav///////////////////
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates"))
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");

            if (savedInstanceState.containsKey("last_known_location"))
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");

            if (savedInstanceState.containsKey("last_updated_on"))
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
        }
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                    }
                })

                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(BaseTrackMeActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                        }
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User choose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
       bundle.putString("PostionId", "32");
        intent.putExtras(bundle);
        startActivity(intent);
        //startService(new Intent(BaseTrackMeActivity.this, VisService.class));
        finish();
       // moveTaskToBack(true);

    }
   /* private void batteryLevel() {
        final BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
               // batterLevel.setText("Battery Level Remaining: " + level + "%");

            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        Log.e("Battery status", level.to);

    }
*/

  /*  private void sendDataToServer(String s, String valueOf) {
        JSONObject jsonPayload = new JSONObject();
        try {
            if (mCurrentLocation == null) {
                Utilities.showDialog(coordinatorLayout, "Please Check/Enable Internet & Turn On GPS and Restart Tracking");
            } else {
                jsonPayload.put("userName", employeeName);
                jsonPayload.put("employeeId", empoyeeId);
                jsonPayload.put("latitude", latitude);
                jsonPayload.put("longitude", longitude);
                // "http://savvyhrms.com/SavvyMobile
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTrackMePost", jsonPayload,
                        //  String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/SaveAttendanceMarkInOut";
                        // Log.e(TAG,)
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("VolleyInvokeWebService", "Response - >" + response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getCustomHeaders();
                    }
                };
                jsonObjReq.setTag("1");
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                addToRequestQueue(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, e.toString());
        }
    }

    private HashMap<String, String> getCustomHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }*/
    public boolean onOptionsItemSelected(MenuItem item) {
        //moveTaskToBack(true);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId", "32");
            intent.putExtras(bundle);
            startActivity(intent);
            //startService(new Intent(BaseTrackMeActivity.this, VisService.class));
             finish();

        }
        return super.onOptionsItemSelected(item);


    }

}


