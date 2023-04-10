package com.savvy.hrmsnewapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
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
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by orapc7 on 09-Jan-18.
 */

public class Track_Me_Location_Service extends Service {

    Timer timer = new Timer();
    TimerTask timerTask;
    Handler handler;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "", employeeName="";

    @Override
    public void onCreate() {
        super.onCreate();
        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        employeeName = (shared.getString("EmpoyeeName", ""));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            timer = new Timer();
            timer.cancel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            timer = new Timer();
            handler = new Handler();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getLocation();
                        }
                    });
                }
            };
            timer.schedule(timerTask,0,1000*1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getLocation(){
        try{
            TrackGPS trackGPS = new TrackGPS(Track_Me_Location_Service.this);
            if(trackGPS.canGetLocation){
//                TrackMeLocationConstants.TRACK_LOC_LATITUDE = trackGPS.getLatitude();
//                TrackMeLocationConstants.TRACK_LOC_LONGITUDE = trackGPS.getLongitude();

                Log.e("Location","Latitude = "+TrackMeLocationConstants.LOC_LATITUDE);
                Log.e("Location","Longitude = "+TrackMeLocationConstants.LOC_LONGITUDE);

//                sendDataToServer(String.valueOf(TrackMeLocationConstants.TRACK_LOC_LATITUDE),String.valueOf(TrackMeLocationConstants.TRACK_LOC_LONGITUDE));
            }else{
                trackGPS.showSettingsAlert();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendDataToServer(String lat,String lng){
        try {
            if (Utilities.isNetworkAvailable(getApplicationContext())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTrackMePost";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("userName", employeeName);
                params_final.put("employeeId", empoyeeId);
                params_final.put("latitude", lat);
                params_final.put("longitude", lng);


                requestQueue = Volley.newRequestQueue(getApplicationContext());

                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
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
            } else {
                Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
