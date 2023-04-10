package com.savvy.hrmsnewapp.attendanceMark;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.BaseActivity;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.LocationAssistant;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.Calendar;

public class MarkTeamAttendanceNew extends BaseActivity implements View.OnClickListener, LocationAssistant.Listener {

    CoordinatorLayout coordinatorLayout;

    CustomTextView txv_currentDate, txv_currentTime;
    EditText edt_messagefeedback, edt_TeamEmployeeCode;
    String token = "", empoyeeId = "", username = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    Button btn_submit;
    private static final String TAG = "MarkTeamAttendanceNew";
    CustomTextView txt_employeeCode;
    TextView tv_locationAddress;

    public static final int REQUEST_CODE = 1;
    private static String latitude;
    private static String longitude;
    String locationAddress = "";
    private LocationAssistant assistant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_attendance_google_api);

        initLocation();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));

        tv_locationAddress = findViewById(R.id.tv_locationAddress);
        txv_currentDate = (CustomTextView) findViewById(R.id.txv_currentDate);
        txv_currentTime = (CustomTextView) findViewById(R.id.txv_currentTime);
        edt_messagefeedback = (EditText) findViewById(R.id.edt_messagefeedback);
        edt_TeamEmployeeCode = (EditText) findViewById(R.id.edt_TeamEmployeeId);

        txt_employeeCode = (CustomTextView) findViewById(R.id.txt_employeeCode);
        String str1 = "<font color='#EE0000'>*</font>";

        txt_employeeCode.setText(Html.fromHtml("Employee Code " + str1));
        getCurrentDateTime();

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initLocation() {
        if (assistant == null) {
            assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
            assistant.setVerbose(true);
        }
        assistant.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            initLocation();
            if (isGPSTurnedOn()) {
                final String getTeamEmployeeCode = edt_TeamEmployeeCode.getText().toString().trim();
                if (getTeamEmployeeCode.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Employee Code.");
                } else if (locationAddress.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please wait while fetching your location...");
                } else {
                    String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ", "-");
                    markTeamAttendancePost(commentreplace, latitude, longitude, getTeamEmployeeCode, locationAddress);
                }
            } else {
                moveToLocationSetting();
            }

        }
    }


    public void markTeamAttendancePost(String attendanceRemark, String latitude, String longitude, String teamEmployeeCode, String location_Address) {

        try {
            showProgressDialog();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTeamAttendanceMarkPost";
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", attendanceRemark);
            params_final.put("latitude", latitude);
            params_final.put("longitude", longitude);
            params_final.put("teamEmployeeCode", teamEmployeeCode);
            params_final.put("location", location_Address);
            Log.e(TAG, "params_final: " + params_final.toString());

            RequestQueue requestQueue = Volley.newRequestQueue(MarkTeamAttendanceNew.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dismissProgressDialog();
                            try {
                                JSONObject jsonobj = response.getJSONObject("SaveTeamAttendanceMarkPostResult");

                                String statusId = jsonobj.getString("statusId");
                                String statusDescription = jsonobj.getString("statusDescription");
                                String errorMessage = jsonobj.getString("errorMessage");

                                switch (statusId) {
                                    case "1":
                                        Utilities.showDialog(coordinatorLayout, statusDescription);
                                        edt_messagefeedback.setText("");
                                        edt_TeamEmployeeCode.setText("");
                                        break;
                                    case "2":
                                        Utilities.showDialog(coordinatorLayout, statusDescription);
                                        break;
                                    case "3":
                                        Utilities.showDialog(coordinatorLayout, statusDescription);
                                        break;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast(error.getMessage());
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            } else {
                showLocationErrorDialog("Location permission not granted !");
                sendErrorToServer(empoyeeId, "Location permission not granted !", "Privilage id 34", "", "", "");
            }
        }
    }


    private void updateUI(String locationAddrees) {
        tv_locationAddress.setText(locationAddrees);
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

    @Override
    public void onNeedLocationPermission() {
        assistant.requestLocationPermission();
    }

    @Override
    public void onExplainLocationPermission() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permissionExplanation)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        assistant.requestLocationPermission();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        assistant.requestLocationPermission();

                    }
                })
                .show();
    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permissionPermanentlyDeclined)
                .setPositiveButton(R.string.ok, fromDialog)
                .show();
    }

    @Override
    public void onNeedLocationSettingsChange() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.switchOnLocationShort)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        assistant.changeLocationSettings();
                    }
                })
                .show();
    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.switchOnLocationLong)
                .setPositiveButton(R.string.ok, fromDialog)
                .show();
    }

    @Override
    public void onNewLocationAvailable(Location location) {
        if (location == null)
            return;
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        locationAddress = Utilities.getAddressFromLateLong(MarkTeamAttendanceNew.this, location.getLatitude(), location.getLongitude());
        Log.e(TAG, "onLocationResult: " + location.getLatitude() + ", " + location.getLongitude() + ", " + Calendar.getInstance().getTime());
        updateUI(locationAddress);
    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        showMockAlert(MarkTeamAttendanceNew.this);
        locationAddress = "";
    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assistant.onActivityResult(requestCode, resultCode);
    }
}
