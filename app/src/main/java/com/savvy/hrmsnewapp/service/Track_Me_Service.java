package com.savvy.hrmsnewapp.service;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.LatLng;
import com.savvy.hrmsnewapp.activity.MyService;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by orapc7 on 04-Jan-18.
 */

public class Track_Me_Service extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String LOGSERVICE = "#######";
    Timer timer, timer1;
    TimerTask timerTask, timerTask1;
    Handler handler, handler1;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    LocationRequest locationRequest;

    public double latitude = 0.0;
    public double longitude = 0.0;
    String empoyeeId = "", employeeName = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    public static long count = 0;
    Context m_service;

    public class MyBinder extends Binder {
        public Track_Me_Service getService() {
            return Track_Me_Service.this;
        }
    }

    private ServiceConnection m_serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            m_service = ((Track_Me_Service.MyBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            m_service = null;
        }
    };

    final class TheThread implements Runnable {
        int serviceId;

        TheThread(int serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run() {
            synchronized (this) {
                try {
                    wait(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // stopSelf(serviceId);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        employeeName = (shared.getString("EmpoyeeName", ""));

        Toast.makeText(this, "Service Start", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (!mGoogleApiClient.isConnected()) {
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            } else{
                buildGoogleApiClient();
                Log.e("Google Api Client","Not Connected Yet");
            }
            getLocation();
//            getLeaveStatusLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        count = 0;
        stopLocationUpdate();
        Toast.makeText(this, "Service Stop", Toast.LENGTH_SHORT).show();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Intent intent1 = new Intent(this, MyService.class);
        bindService(intent1, m_serviceConnection, BIND_AUTO_CREATE);
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Log.i(LOGSERVICE, "onConnected " + bundle);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (l != null) {
                Log.i(LOGSERVICE, "lat Con " + l.getLatitude());
                Log.i(LOGSERVICE, "lng Con " + l.getLongitude());

            } else{
                Log.e("Location Connected","Location Not Found");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getLeaveStatusLoop(){
        handler1 = new Handler();
        timer1 = new Timer();

        timerTask1 = new TimerTask() {
            @Override
            public void run() {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        count++;
                        Log.e("Count","Count = "+count);
                        getLeaveStatus();
                    }
                });
            }
        };

        timer1.schedule(timerTask1,100,100);
    }

    public void getLocation(){
        handler = new Handler();
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(mGoogleApiClient!=null) {
                                if (mGoogleApiClient.isConnected()) {
                                    int permissionLocation = ContextCompat.checkSelfPermission(Track_Me_Service.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                        LocationRequest locationRequest = new LocationRequest();
                                        locationRequest.setInterval(1000*5);
                                        locationRequest.setFastestInterval(1000);
                                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                                        builder.setAlwaysShow(true);
                                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, Track_Me_Service.this);

                                        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                        if (l != null) {
                                            latitude = l.getLatitude();
                                            longitude = l.getLongitude();

                                            sendLocationToServer(String.valueOf(latitude),String.valueOf(longitude));
//                                            Log.i(LOGSERVICE, "lat " + l.getLatitude());
//                                            Log.i(LOGSERVICE, "lng " + l.getLongitude());
//                                            Toast.makeText(Track_Me_Service.this, "lat = "+l.getLatitude()+" lng = "+l.getLongitude(), Toast.LENGTH_SHORT).show();


                                        } else{
//                                            buildGoogleApiClient();
//                                            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                            Log.e("Location Status","Location Not Found");
                                            Toast.makeText(Track_Me_Service.this, "Location Not Found!!!!", Toast.LENGTH_SHORT).show();
                                        }

                                        startLocationUpdate();
                                    }
                                }
                            }
//                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                // TODO: Consider calling
//                                //    ActivityCompat#requestPermissions
//                                // here to request the missing permissions, and then overriding
//                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                //                                          int[] grantResults)
//                                // to handle the case where the user grants the permission. See the documentation
//                                // for ActivityCompat#requestPermissions for more details.
//                                return;
//                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        timer.schedule(timerTask,0,1000*2);

    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.i(LOGSERVICE, "lat " + location.getLatitude());
            Log.i(LOGSERVICE, "lng " + location.getLongitude());
            LatLng mLocation = (new LatLng(location.getLatitude(), location.getLongitude()));
            EventBus.getDefault().post(mLocation);
        }
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void startLocationUpdate() {
        initLocationRequest();

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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void sendLocationToServer(String latitude,String longitude){
        try{
            Log.i(LOGSERVICE, "lat " + latitude);
            Log.i(LOGSERVICE, "lng " + longitude);

            if(Utilities.isNetworkAvailable(getApplicationContext())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTrackMePost";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("userName", employeeName);
                params_final.put("employeeId", empoyeeId);
                params_final.put("latitude", latitude);
                params_final.put("longitude", longitude);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                count++;
                                Log.e("Status","Count = "+count);
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

                RequestQueue requestQueue = Volley.newRequestQueue(Track_Me_Service.this);
                RetryPolicy policy = new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);

            } else{
                Toast.makeText(this, "No Network Found", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getLeaveStatus(){
        try{
            JSONObject pm = new JSONObject();
            JSONObject param = new JSONObject();

            param.put("EmployeeId","3");
            param.put("fromDate","-");
            param.put("toDate","-");
            param.put("requestStatus","0,1,2,3,4");

            pm.put("objLeaveRequestStatusInfo",param);
            String url = "http://savvyshippingsoftware.com/savvymobile/savvymobileservice.svc/GetLeaveRequestStatus";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("Response","Success");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VolleyError","Failed");
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

            RequestQueue requestQueue = Volley.newRequestQueue(Track_Me_Service.this);
            RetryPolicy policy = new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
