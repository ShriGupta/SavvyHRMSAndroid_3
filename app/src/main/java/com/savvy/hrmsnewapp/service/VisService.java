package com.savvy.hrmsnewapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.activity.BaseTrackMeActivity;
import com.savvy.hrmsnewapp.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VisService extends Service {

    private RequestQueue mRequestQueue;
    private static String empoyeeId = "";
    private static String employeeName = "",trackMeIntervalSecond;
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    public static double latitude;
    public static double longitude;
    public static Location location;
    public static float batteryPct;
    private Runnable runnable;
    private Handler handler;
    public Timer timer;
   // public static int deviceStatus=0;
   // IntentFilter intentfilter;
   SharedPreferences shared;

    @Override
    public void onCreate() {
         shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        employeeName = (shared.getString("EmpoyeeName", ""));
        trackMeIntervalSecond = (shared.getString("trackMeIntervalSecond", "60"));
        batteryLevel();
       // intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);


        Log.e("VisService: ", "onCreate");


        try {

            timer = new Timer();
            Log.e("trackMeIntervalSecond",Long.parseLong(trackMeIntervalSecond)+"");

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (BaseTrackMeActivity.mCurrentLocation != null) {
                        location = BaseTrackMeActivity.mCurrentLocation;
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                       // VisService.this.registerReceiver(broadcastreceiver, intentfilter);

                        sendDataToServer(String.valueOf(latitude), String.valueOf(longitude));
                        batteryLevel();
                        Log.e("Timer","Running");
                    }

                }
            }, 0, 1000 * Long.parseLong(trackMeIntervalSecond));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // super.onStartCommand(intent, flags, startId);
        Log.e("VisService: ", "onStartCommand");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendDataToServer(String latitude, String longitude) {
        JSONObject jsonPayload = new JSONObject();
        try {

            jsonPayload.put("userName", employeeName);
            jsonPayload.put("employeeId", empoyeeId);
            jsonPayload.put("latitude", latitude);
            jsonPayload.put("longitude", longitude);

            jsonPayload.put("batteryStatus", batteryPct);


            Log.e("VisService: ", jsonPayload.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTrackMePost", jsonPayload,
               // "http://savvyhrms.com/SavvyMobile/SavvyMobileService.svc/SaveTrackMePost"
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
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
        jsonObjReq.setTag("1");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        addToRequestQueue(jsonObjReq);

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
    }

    public void stopTimerTask() {
        Log.e("VisService: ", "stopTimerTask");
        if (timer != null) {

            timer.cancel();


        }

    }

    @Override
    public void onDestroy() {
        Log.e("VisService: ", "onDestroy");
        super.onDestroy();
        stopTimerTask();

    }
    public void batteryLevel(){
        //---------------Battery Status------------------
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        batteryPct = (level / (float)scale)*100;
        Log.e("Battery", String.valueOf(batteryPct));


    }
    }

