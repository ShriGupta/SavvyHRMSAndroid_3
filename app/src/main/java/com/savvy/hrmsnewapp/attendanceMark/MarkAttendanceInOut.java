package com.savvy.hrmsnewapp.attendanceMark;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.BaseActivity;
import com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.FaceDetectionHolderFrgamnet;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.LocationAssistant;
import com.savvy.hrmsnewapp.utils.LocationManagerClass;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MarkAttendanceInOut extends BaseActivity implements View.OnClickListener {

    CoordinatorLayout coordinatorLayout;
    CustomTextView txv_currentDate, txv_currentTime;
    EditText edt_messagefeedback;
    String token = "", empoyeeId = "", username = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    private TextView address;
    Button btn_punch_in, btn_punch_out;
    private static final String TAG = "savvyhrmslogs";

    private static String latitude = "";
    private static String longitude = "";
    String locationAddress = "";

    String countryName = "";
    String geoString;
    private final int PERMISSION_REQUEST_CODE = 1111;
    LocationManagerClass locationManagerClass;
    Location location;
    EditText edtTotalWorked;
    LinearLayout llEnableDisableLayout;
    String privilageId="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance_with_in_out_google);

        address = (CustomTextView) findViewById(R.id.address);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));

        txv_currentDate = (CustomTextView) findViewById(R.id.txv_currentDate);
        txv_currentTime = (CustomTextView) findViewById(R.id.txv_currentTime);
        edt_messagefeedback = (EditText) findViewById(R.id.edt_messagefeedback);
        edtTotalWorked = (EditText) findViewById(R.id.edt_total_worked);
        llEnableDisableLayout = (LinearLayout) findViewById(R.id.ll_enable_disable_layout);

        btn_punch_in = (Button) findViewById(R.id.btn_punchIn);
        btn_punch_out = (Button) findViewById(R.id.btn_punchOut);


        btn_punch_in.setOnClickListener(this);
        btn_punch_out.setOnClickListener(this);

        if (getIntent().getExtras() != null) {
             privilageId = getIntent().getStringExtra("privilageId");
            if(privilageId.equals("80")){
                llEnableDisableLayout.setVisibility(View.VISIBLE);
                getCurrentDateTimeWithEnableButton();
            }else {
                llEnableDisableLayout.setVisibility(View.GONE);
            }
        }
        setUpToolBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCurrentDateTimeWithEnableButton() {
        APIServiceClass.getInstance().getServerDateTimeWithEnableButton(shared.getString(Constants.EMPLOYEE_ID_FINAL, ""),
                shared.getString("EmpoyeeId", ""),new ResultHandler<ServerDateTimeModel>() {
                    @Override
                    public void onSuccess(ServerDateTimeModel data) {

                        String[] serverDateSplit = data.getServerDateDDMMYYYYY().split(" ");
                        String replacecurrDate = serverDateSplit[0].replace("\\", "");
                        txv_currentTime.setText(data.getServerTime());
                        txv_currentDate.setText(replacecurrDate);

                        if(!data.getTotalTimeWorked().equals("")){
                            edtTotalWorked.setText(data.getTotalTimeWorked());
                        }
                        switch (data.getIOButtonCurrentMode()) {
                            case "1":
                                btn_punch_in.setEnabled(true);
                                btn_punch_out.setEnabled(false);
                                break;
                            case "2":
                                btn_punch_in.setEnabled(false);
                                btn_punch_out.setEnabled(true);
                                break;
                            case "3":
                                btn_punch_in.setEnabled(false);
                                btn_punch_out.setEnabled(false);
                                break;
                        }
                    }
                    @Override
                    public void onFailure(String message) {
                    }
                });
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_punchIn: {

                    if (Utilities.isGPSTurnedOn(MarkAttendanceInOut.this)) {
                        if (latitude.equals("") && longitude.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Please wait while fetching your location...");
                        } else {
                            String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ", "-");
                            markAttendanceInOutWithLocation(commentreplace, latitude, longitude, "I", locationAddress);
                        }
                    } else {
                        Utilities.showLocationErrorDialog(MarkAttendanceInOut.this, "Please Enable The Device Location..");
                    }
                    break;
                }
                case R.id.btn_punchOut: {

                    if (Utilities.isGPSTurnedOn(MarkAttendanceInOut.this)) {
                        if (latitude.equals("") && longitude.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Please wait while fetching your location...");
                        } else {
                            String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ", "-");
                            markAttendanceInOutWithLocation(commentreplace, latitude, longitude, "O", locationAddress);
                        }
                    } else {
                        Utilities.showLocationErrorDialog(MarkAttendanceInOut.this, "Please Enable The Device Location..");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void markAttendanceInOut(String comment, String lat, String lon, String type) {
        try {
            showProgressDialog();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveAttendanceMarkInOut";
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", comment);
            params_final.put("latitude", lat);
            params_final.put("longitude", lon);
            params_final.put("punchType", type);

            if (Utilities.isNetworkAvailable(MarkAttendanceInOut.this)) {
                RequestQueue requestQueue = Volley.newRequestQueue(MarkAttendanceInOut.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                dismissProgressDialog();

                                try {
                                    locationAddress = "";
                                    JSONObject jsonobj = response.getJSONObject("SaveAttendanceMarkInOutResult");
                                    String statusId = jsonobj.getString("statusId");
                                    String statusDescription = jsonobj.getString("statusDescription");
                                    String errorMessage = jsonobj.getString("errorMessage");

                                    if (statusId.equals("1")) {
                                        Utilities.showDialog(coordinatorLayout, statusDescription);
                                        edt_messagefeedback.setText("");
                                    } else if (statusId.equals("2")) {
                                        Utilities.showDialog(coordinatorLayout, statusDescription);
                                    }
                                    if(privilageId.equals("80")){
                                        getCurrentDateTimeWithEnableButton();
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        locationAddress = "";
                        sendErrorToServer(empoyeeId, "SaveAttendanceMarkInOut", "Privilage id 34", error.getMessage(), "", "");
                    }
                });

                int socketTimeout = 3000000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorToServer(empoyeeId, "SaveAttendanceMarkInOut", "Privilage id 34", e.getMessage(), "", "");
        }
    }

    private void markAttendanceInOutWithLocation(final String comment, final String lat, final String lon, final String type, String locationaddress) {
        try {
            showProgressDialog();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveAttendanceMarkInOutWithLocationAddress";
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", comment);
            params_final.put("latitude", lat);
            params_final.put("longitude", lon);
            params_final.put("punchType", type);
            params_final.put("locationAddress", locationaddress);

            if (Utilities.isNetworkAvailable(MarkAttendanceInOut.this)) {
                RequestQueue requestQueue = Volley.newRequestQueue(MarkAttendanceInOut.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                dismissProgressDialog();
                                Log.e(TAG, "onResponse: "+response.toString() );
                                try {
                                    JSONObject jsonobj = response.getJSONObject("SaveAttendanceMarkInOutWithLocationAddressResult");
                                    String statusId = jsonobj.getString("statusId");
                                    String statusDescription = jsonobj.getString("statusDescription");
                                    String errorMessage = jsonobj.getString("errorMessage");

                                    if (statusId.equals("1")) {
                                        Utilities.showDialog(coordinatorLayout, statusDescription);
                                        edt_messagefeedback.setText("");
                                    } else if (statusId.equals("2")) {
                                        Utilities.showDialog(coordinatorLayout, statusDescription);
                                    }

                                    if(privilageId.equals("80")){
                                        getCurrentDateTimeWithEnableButton();
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }, error -> {
                    dismissProgressDialog();
                    markAttendanceInOut(comment, String.valueOf(lat), String.valueOf(lon), type);
                });

                int socketTimeout = 3000000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(privilageId.equals("80")){
            getCurrentDateTimeWithEnableButton();
        }else{
            getCurrentDateTime();
        }

        if (Utilities.isGPSTurnedOn(MarkAttendanceInOut.this)) {
            initLocation();
        } else {
            Utilities.showLocationErrorDialog(MarkAttendanceInOut.this, "PLease Enable Your Location");
        }

    }

    private void initLocation() {
        if (checkLocationPermission()) {
            address.setText("Please wait, fetching your location...");
            locationManagerClass = new LocationManagerClass(MarkAttendanceInOut.this, mLocation -> {
                location = mLocation;
                Log.e("TAG", "onLocationRecieve: Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                if (location == null)
                    return;
                updateUI(location);
            });
            locationManagerClass.startLocationUpdates();

        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }


    private boolean checkLocationPermission() {
        boolean isPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            isPermissionGranted = true;
        }
        return isPermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            } else {
                Utilities.showLocationErrorDialog(MarkAttendanceInOut.this, "Location permission not granted !");
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void updateUI(Location loc) {
        if (checkMockLocation(loc)) {
            mockAlertDialog();
            locationAddress = null;
            address.setText(locationAddress);
        } else {

            latitude = String.valueOf(loc.getLatitude());
            longitude = String.valueOf(loc.getLongitude());
            locationAddress = Utilities.getAddressFromLateLong(MarkAttendanceInOut.this, loc.getLatitude(), loc.getLongitude());
            countryName = Utilities.getCountryFromLocation(MarkAttendanceInOut.this, loc);
            if (locationAddress.equals("")) {
                address.setText("Latitude :" + latitude + "\n" + "Longitude" + longitude);

            } else {
                address.setText(locationAddress);
            }

            Date date = new Date(loc.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.US);
            geoString = sdf.format(date) + " " + countryName;
        }

    }

    private void getCurrentDateTime() {

        APIServiceClass.getInstance().sendDateTimeRequest(shared.getString(Constants.EMPLOYEE_ID_FINAL, ""), new ResultHandler<ServerDateTimeModel>() {
            @Override
            public void onSuccess(ServerDateTimeModel data) {
                Log.e(TAG, "onSuccess: " + data);

                String[] serverDateSplit = data.getServerDateDDMMYYYYY().split(" ");
                String replacecurrDate = serverDateSplit[0].replace("\\", "");
                txv_currentTime.setText(data.getServerTime());
                txv_currentDate.setText(replacecurrDate);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }


    private boolean checkMockLocation(Location location) {
        return location.isFromMockProvider();
    }

    private void mockAlertDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Mock location detected!").setMessage("Please disable mock location.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (locationManagerClass != null) {
            locationManagerClass.stopLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManagerClass != null) {
            locationManagerClass.stopLocationUpdates();
        }
    }


}
