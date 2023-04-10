package com.savvy.hrmsnewapp.rahul;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;


public class TrackMeRahul extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private static final long DELAY_TIME = 30*1000;
    private RequestQueue mRequestQueue;
    private String empoyeeId = "";
    private String employeeName = "";
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    private double latitude;
    private double longitude;
    private Location location;
    private Runnable runnable;
    private Handler handler;
    SharedPreferences shared;


    public Timer timer;
    public TrackMeRahul(Context context){
        super("ConnectionServices");
        shared = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SharedPreferences shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        employeeName = (shared.getString("EmpoyeeName", ""));
        Log.e("onHandleIntent: ", "start" );


        handler=new Handler();



       /* handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BaseTrackMeActivity.mCurrentLocation != null) {
                    location = BaseTrackMeActivity.mCurrentLocation;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.e("postDelayed: ", "run" );
                    sendDataToServer(String.valueOf(latitude), String.valueOf(longitude));
                }
            }
        },DELAY_TIME);*/

       try {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (BaseTrackMeActivity.mCurrentLocation != null) {
                        location = BaseTrackMeActivity.mCurrentLocation;
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        sendDataToServer(String.valueOf(latitude), String.valueOf(longitude));
                    }
                }
            }, 0, 1000 * 30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void sendDataToServer(String latitude, String longitude) {
        JSONObject jsonPayload = new JSONObject();
        try {

            jsonPayload.put("userName", employeeName);
            jsonPayload.put("employeeId", empoyeeId);
            jsonPayload.put("latitude", latitude);
            jsonPayload.put("longitude", longitude);
            Log.e("onHandleIntent: ", jsonPayload.toString() );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, "http://savvyhrms.com/SavvyMobile/SavvyMobileService.svc/SaveTrackMePost", jsonPayload,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("VolleyInvokeWebService", "Response - >" + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onErrorResponse: ", error.getMessage());
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

    public void stopTimerTask(){
        if(timer!=null){
            Log.d("TIMER", "timer canceled");
            timer.cancel();


        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopTimerTask();

    }
}
