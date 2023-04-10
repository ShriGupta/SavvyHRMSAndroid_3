package com.savvy.hrmsnewapp.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable.ConstantState;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.TrackMeModel;
import com.savvy.hrmsnewapp.service.TrackMeService;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;
import static com.savvy.hrmsnewapp.fragment.TravelRequestFicciFragment.MY_PREFS_NAME;

public class TrackMeActivity extends AppCompatActivity {
    Button btn_startService, btn_stop_service;
    String empId = "";
    String empUsername = "";
    boolean checkTrackingStatus;
    SharedPreferences sharedPreferences;
    TextView CurrentTrackingStatus, tv_location_address;
    public static final int locationRequestCode = 121;
    ViewGroup mainLayout;
    TextView root_view;
    private int xDelta;
    private int yDelta;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_me);
        sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empId = (sharedPreferences.getString("EmpoyeeId", ""));
        empUsername = (sharedPreferences.getString("EmpoyeeName", ""));

        setTitle("Track Me");
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_startService = (Button) findViewById(R.id.btn_startService);
        btn_stop_service = (Button) findViewById(R.id.btn_stop_service);
        tv_location_address = findViewById(R.id.tv_location_address);
        CurrentTrackingStatus = findViewById(R.id.CurrentTrackingStatus);

        checkTrackingStatus = Constants.checkTrackingStatus;


        // showTrackMyLocationView(checkTrackingStatus);

        if (checkTrackingStatus) {
            btn_startService.setEnabled(false);
            btn_stop_service.setEnabled(true);
            setBlueBackGround(btn_stop_service);
            setGrayBackground(btn_startService);
            CurrentTrackingStatus.setText("Current Tracking Status: RUNNING");
        } else {
            setBlueBackGround(btn_startService);
            setGrayBackground(btn_stop_service);
            btn_stop_service.setEnabled(false);
            btn_startService.setEnabled(true);
            CurrentTrackingStatus.setText("Current Tracking Status: OFF");
        }

        btn_startService.setOnClickListener(view -> {
            if (isLocationEnabled()) {
                checkPermission();
            } else {
                Toast.makeText(this, "Please Turn On Device Location!", Toast.LENGTH_SHORT).show();
            }

            //downLoadFile();
        });
        btn_stop_service.setOnClickListener(view -> {
            stopBackgroundService();
        });

        Log.e(TAG, "onCreateView: " + checkTrackingStatus);
    }

    private void stopBackgroundService() {
        checkTrackingStatus = false;
        btn_stop_service.setEnabled(false);
        btn_startService.setEnabled(true);
        setBlueBackGround(btn_startService);
        setGrayBackground(btn_stop_service);
        CurrentTrackingStatus.setText("Current Tracking Status: OFF");
        Constants.checkTrackingStatus = false;
        stopService(new Intent(TrackMeActivity.this, TrackMeService.class));
        if (Utilities.isNetworkAvailable(TrackMeActivity.this)) {
            getTrackMeData();
        }
        // show system window alert
        Intent intent = new Intent("track_my_location");
        intent.putExtra("track_locaion", checkTrackingStatus);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void getTrackMeData() {
        new GetDataBackGroundTask().execute();
    }

    class GetDataBackGroundTask extends AsyncTask<Void, Void, List<TrackMeModel>> {
        @Override
        protected List<TrackMeModel> doInBackground(Void... voids) {
            List<TrackMeModel> value = DatabaseClient.getInstance(TrackMeActivity.this).getAppDatabase().passengerDao().getTrackMeDetails();
            return value;
        }

        @Override
        protected void onPostExecute(List<TrackMeModel> trackMeModelsList) {
            super.onPostExecute(trackMeModelsList);
            StringBuilder dbtrackmeDetails = new StringBuilder();
            for (int i = 0; i < trackMeModelsList.size(); i++) {
                dbtrackmeDetails.append(trackMeModelsList.get(i).getTrackMeDetails());
            }
            Log.e(TAG, "getTrackMeData: " + trackMeModelsList.size() + " : " + dbtrackmeDetails);
            if (!dbtrackmeDetails.toString().equals("")) {
                if (Utilities.isNetworkAvailable(TrackMeActivity.this)) {
                    sendTrackMeDataToServer(empUsername, empId, dbtrackmeDetails.toString());
                }
            }
        }
    }

    public void sendTrackMeDataToServer(String username, String empId, String trackmeDetail) {

        String TRACK_ME_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTrackMePostInBulk";
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userName", username);
            jsonObject.put("employeeId", empId);
            jsonObject.put("deatails", trackmeDetail);

            Log.e(TAG, "sendTrackMeDataToServer: " + username + "," + empId + "," + trackmeDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(TRACK_ME_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse: " + response.toString());
            }
        }, error -> Log.e(TAG, "onErrorResponse: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        int socketTimeOut = 3000000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("my-message"));
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String location = intent.getStringExtra("my-integer");
            tv_location_address.setText(location);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

   /* private void sendRequest() {
        if (Utilities.isNetworkAvailable(TrackMeActivity.this)) {

            final String SEND_APPROVAL_REQUEST = "https://demo.savvyhrms.com/savvymobile/MobileFileDownload.ashx?employeeid=" + empId + "&year=2021&month=12&securityToken=" + sharedPreferences.getString("EMPLOYEE_ID_FINAL", "");
            Log.d(TAG, "sendRequest: " + SEND_APPROVAL_REQUEST);

            StringRequest jsonArrayRequest = new StringRequest(Request.Method.GET, SEND_APPROVAL_REQUEST, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        Log.d(TAG, "onResponse: " + response);


                    } catch (Exception e) {
                        // Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
                    return params;
                }


                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonArrayRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(TrackMeActivity.this).addToRequestQueue(jsonArrayRequest);
        } else {
            //Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            Toast.makeText(this, "No Internet available", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void statTrackMeService() {
        checkTrackingStatus = true;
        btn_startService.setEnabled(false);
        btn_stop_service.setEnabled(true);
        setGrayBackground(btn_startService);
        setBlueBackGround(btn_stop_service);
        CurrentTrackingStatus.setText("Current Tracking Status: RUNNING");

        Constants.checkTrackingStatus = true;
        Intent intent1 = new Intent(TrackMeActivity.this, TrackMeService.class);
        ContextCompat.startForegroundService(TrackMeActivity.this, intent1);
        Log.e(TAG, "statTrackMeService: " + checkTrackingStatus);

        // show system window alert
        Intent intent = new Intent("track_my_location");
        intent.putExtra("track_location", checkTrackingStatus);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //showTrackMyLocationView(checkTrackingStatus);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        locationRequestCode);

            }else{
                statTrackMeService();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        locationRequestCode);
            }else{
                statTrackMeService();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                statTrackMeService();
            }
        } else {
            Toast.makeText(TrackMeActivity.this, "Location Permission is not granted!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checkTrackingStatus", checkTrackingStatus);
        editor.apply();

        Log.e(TAG, "onPause: " + checkTrackingStatus);
    }

    public void setBlueBackGround(Button button) {
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackgroundDrawable(ContextCompat.getDrawable(TrackMeActivity.this, R.drawable.button_border));
        } else {
            button.setBackground(ContextCompat.getDrawable(TrackMeActivity.this, R.drawable.button_border));
        }
    }

    public void setGrayBackground(Button button) {
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackgroundDrawable(ContextCompat.getDrawable(TrackMeActivity.this, R.drawable.button_gray_border));
        } else {
            button.setBackground(ContextCompat.getDrawable(TrackMeActivity.this, R.drawable.button_gray_border));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showTrackMyLocationView(Boolean trakingLocation) {
        root_view = findViewById(R.id.tv_tracking_textview);
        mainLayout = (RelativeLayout) findViewById(R.id.rl_tracking_relativeLayout);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.location_animation);
        a.reset();
        root_view.clearAnimation();
        root_view.startAnimation(a);

        if (trakingLocation) {
            mainLayout.setVisibility(View.VISIBLE);

            mainLayout.bringToFront();
            root_view.setOnTouchListener((view, event) -> {
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                return true;
            });
        } else {
            mainLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId", "32");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu != null) {
            menu.findItem(R.id.home_dashboard).setVisible(false);
        }
        return true;
    }
}