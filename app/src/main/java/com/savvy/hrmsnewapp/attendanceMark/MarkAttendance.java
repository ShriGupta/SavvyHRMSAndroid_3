package com.savvy.hrmsnewapp.attendanceMark;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.BaseActivity;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.LocationManagerClass;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

public class MarkAttendance extends BaseActivity implements View.OnClickListener {

    CoordinatorLayout coordinatorLayout;
    CustomTextView txv_currentDate, txv_currentTime;
    EditText edt_messagefeedback;
    String token = "", empoyeeId = "", username = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    TextView address;
    Button btn_submit;
    private static final String TAG = "MarkAttendance";
    Context context;

    private static String latitude = "";
    private static String longitude_ = "";
    String locationAddress = "";
    String latlongFlag = "";

    private final int PERMISSION_REQUEST_CODE = 1111;

    LocationManagerClass locationManagerClass;
    Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);
        try {

            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
            context = MarkAttendance.this;

            token = (shared.getString("Token", ""));
            empoyeeId = (shared.getString("EmpoyeeId", ""));
            username = (shared.getString("UserName", ""));

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                latlongFlag = bundle.getString("latlongFlag") == null ? "" : bundle.getString("latlongFlag");
            }

            txv_currentDate = (CustomTextView) findViewById(R.id.txv_currentDate);
            txv_currentTime = (CustomTextView) findViewById(R.id.txv_currentTime);
            edt_messagefeedback = (EditText) findViewById(R.id.edt_messagefeedback);
            address = (CustomTextView) findViewById(R.id.address);
            btn_submit = (Button) findViewById(R.id.btn_submit);
            btn_submit.setOnClickListener(this);
            if (latlongFlag != null && latlongFlag.equals("2")) {
                address.setText("");
            }
            assert getSupportActionBar() != null;
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorToServer(empoyeeId, "OnCreate : Error :" + e.getMessage(), "Privil_Id : 4", "", "", "");
        }

    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }


    private void initLocation() {
        if (checkLocationPermission()) {
            address.setText("Please wait, fetching your location...");
            locationManagerClass = new LocationManagerClass(MarkAttendance.this, mLocation -> {
                location = mLocation;
                Log.e("TAG", "onLocationRecieve: Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                if (location == null)
                    return;
                latitude = String.valueOf(location.getLatitude());
                longitude_ = String.valueOf(location.getLongitude());
                locationAddress = Utilities.getAddressFromLateLong(MarkAttendance.this, location.getLatitude(), location.getLongitude());
                if (!locationAddress.equals("")) {
                    updateUI(locationAddress);
                } else {
                    locationAddress = "Latitude :" + latitude + "\n" + "Longitude :" + longitude_;
                    updateUI(locationAddress);
                }
            });
            locationManagerClass.startLocationUpdates();

        } else {
            requestPermission();
        }
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard();
        if (v.getId() == R.id.btn_submit) {
            Log.e("TAG", "onClick: latlongFlag: " + latlongFlag);
            if (Utilities.isGPSTurnedOn(MarkAttendance.this)) {
                if (checkLocationPermission()) {
                    String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ", "-");
                    if (latlongFlag.equals("0")) {
                        if (!(latitude.equals("") && longitude_.equals(""))) {
                            markAttendancePostWithLocation(commentreplace, latitude, longitude_, locationAddress);
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Please wait fetching your current location...");
                        }
                    } else if (latlongFlag.equals("1")) {
                        if (!(latitude.equals("") && longitude_.equals(""))) {
                            markAttendancePostWithLocation(commentreplace, latitude, longitude_, "");
                        }
                    } else if (latlongFlag.equals("2")) {
                        markAttendancePostWithLocation(commentreplace, "", "", "");
                    } else {
                        if (!(latitude.equals("") && longitude_.equals(""))) {
                            markAttendancePostWithLocation(commentreplace, latitude, longitude_, locationAddress);
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Please wait fetching your current location...");
                        }
                    }
                } else {
                    requestPermission();

                }

            } else {
                Utilities.showLocationErrorDialog(MarkAttendance.this, getResources().getString(R.string.location_string));
            }
        }
    }

    public void markAttendancePost(String attendanceRemark, String latitude, String longitude) {
        try {
            showProgressDialog();
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
                    response -> {
                        locationAddress = "";
                        dismissProgressDialog();

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
                            btn_submit.setEnabled(false);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 2000);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            sendErrorToServer(empoyeeId, "SaveAttendanceMarkPost", ex.getMessage(), "", "Privilage id: 5", "");

                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast(error.getMessage());
                    locationAddress = "";
                }
            });

            int socketTimeout = 300000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorToServer(empoyeeId, "SaveAttendanceMarkPost", e.getMessage(), "", "Privilage id: 5", "");
        }
    }

    private void markAttendancePostWithLocation(final String attendanceRemark, final String lat, final String longi, String locationaddress) {
        try {
            showProgressDialog();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveAttendanceMarkPostWithLocationAddress";
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", attendanceRemark);
            params_final.put("latitude", lat);
            params_final.put("longitude", longi);
            params_final.put("locationAddress", locationaddress);
            Log.e(TAG, "params_final: " + params_final.toString());

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dismissProgressDialog();
                            try {
                                JSONObject jsonobj = response.getJSONObject("SaveAttendanceMarkPostWithLocationAddressResult");
                                String statusId = jsonobj.getString("statusId");
                                String statusDescription = jsonobj.getString("statusDescription");
                                String errorMessage = jsonobj.getString("errorMessage");

                                if (statusId.equals("1")) {
                                    Utilities.showDialog(coordinatorLayout, statusDescription);
                                    edt_messagefeedback.setText("");
                                } else if (statusId.equals("2")) {
                                    Utilities.showDialog(coordinatorLayout, statusDescription);
                                } else {
                                    showToast(errorMessage);
                                }
                                btn_submit.setEnabled(false);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    markAttendancePost(attendanceRemark, String.valueOf(lat), String.valueOf(longi));
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentDateTime();
        if (latlongFlag != null && latlongFlag.equals("2")) {
            address.setText("");
        }

        if (Utilities.isGPSTurnedOn(MarkAttendance.this)) {
            initLocation();
        } else {
            Utilities.showLocationErrorDialog(MarkAttendance.this, "PLease Enable Your Location");
        }
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
                Utilities.showLocationErrorDialog(MarkAttendance.this, "Location permission not granted !");
            }
        }
    }

    private void updateUI(String locationAddrees) {
        address.setText(locationAddrees);
    }

    private void getCurrentDateTime() {

        APIServiceClass.getInstance().sendDateTimeRequest(shared.getString(Constants.EMPLOYEE_ID_FINAL, ""), new ResultHandler<ServerDateTimeModel>() {
            @Override
            public void onSuccess(ServerDateTimeModel data) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManagerClass.stopLocationUpdates();
    }


    @Override
    public void onStop() {
        super.onStop();
        locationManagerClass.stopLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (Utilities.isGPSTurnedOn(MarkAttendance.this)) {
                initLocation();
            } else {
                Utilities.showLocationErrorDialog(MarkAttendance.this, "Please Enable Your Location");
            }
        }
    }
}
