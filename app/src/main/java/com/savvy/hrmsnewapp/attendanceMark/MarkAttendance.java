package com.savvy.hrmsnewapp.attendanceMark;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.BaseActivity;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.runtimePermission.PermissionsManager;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.LocationAssistant;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class MarkAttendance extends BaseActivity implements View.OnClickListener, LocationAssistant.Listener {
    FusedLocationProviderClient fusedLocationClient;
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

    public static final int REQUEST_CODE = 1;
    private static String latitude = "";
    private static String longitude_ = "";
    String locationAddress = "";
    String latlongFlag = "";
    /* private LocationAssistant assistant;*/
    private final int PERMISSION_REQUEST_CODE = 1111;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);


        try {
            if (latitude.equals("") && longitude_.equals("")) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(MarkAttendance.this);
                if (isLocationEnable()) {
                    getCurrentLocation();
                }
            } else {
                getLocationAddress(Double.parseDouble(latitude), Double.parseDouble(longitude_));
            }
        } catch (Exception e) {
        }
        try {
            //initLocation();
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

    private boolean isLocationEnable() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            statusOfGPS = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return statusOfGPS;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        return result5 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                try {
                    if (grantResults.length > 0) {
                        boolean AccessLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        Log.e("AccessLocation>>", "<>" + AccessLocation);
                        if (!AccessLocation) {
                        } else {

                            getCurrentLocation();
                        }

                    }
                } catch (Exception e) {}
                break;


        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(MarkAttendance.this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location lastLocation = task.getResult();
                if (lastLocation != null) {
                    getLocationAddress(lastLocation.getLatitude(), lastLocation.getLongitude());
                }
            }
        });

    }

    private void getLocationAddress(double lat, double longitude) {
        List<Address> addresses = null;
        try {
            Geocoder geocoder = new Geocoder(MarkAttendance.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, longitude, 1);

            latitude = String.valueOf(lat);
            longitude_ = String.valueOf(longitude);
            if (addresses != null && addresses.size() > 0) {
                String cityName = addresses.get(0).getAddressLine(0);
                String fullAddress = addresses.get(0).getAddressLine(1);
                String city = addresses.get(0).getLocality();
                String countryName = addresses.get(0).getAddressLine(2);
                Log.e("City", city + "," + cityName);
                locationAddress = cityName;

                Log.e("City", locationAddress );

            }else {
                locationAddress = "";
            }
            if (!locationAddress.equals("")) {
                updateUI(locationAddress);
            } else {
                locationAddress = "Latitude :" + latitude + "\n" + "Longitude :" + longitude_;
                updateUI(locationAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void initLocation() {
        if (assistant == null) {
            assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
            assistant.setVerbose(true);
        }
        assistant.start();
    }
*/
    @Override
    public void onClick(View v) {
        hideKeyBoard();
        if (v.getId() == R.id.btn_submit) {
            /* initLocation();*/
            String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ", "-");

            if (latlongFlag.equals("0")) {
                if (isGPSTurnedOn()) {
                    if (checkLocationPermission()) {
                        if (locationAddress.equals("")) {
                            if (!(latitude.equals("") && longitude_.equals(""))) {
                                markAttendancePostWithLocation(commentreplace, latitude, longitude_, locationAddress);
                            } else {
                                Utilities.showDialog(coordinatorLayout, "Please Enable Your location...");
                            }
                        } else {
                            markAttendancePostWithLocation(commentreplace, latitude, longitude_, locationAddress);
                        }
                    }
                } else {
                    moveToLocationSetting();
                }
            } else if (latlongFlag.equals("1")) {
                if (isGPSTurnedOn()) {
                    if (checkLocationPermission()) {
                        if (!(latitude.equals("") && longitude_.equals(""))) {
                            markAttendancePostWithLocation(commentreplace, latitude, longitude_, "");
                        }
                    }
                } else {
                    moveToLocationSetting();
                }
            } else if (latlongFlag.equals("2")) {
                markAttendancePostWithLocation(commentreplace, "", "", "");
            } else {
                if (isGPSTurnedOn()) {
                    if (checkLocationPermission()) {
                        if (locationAddress.equals("")) {
                            if (!(latitude.equals("") && longitude_.equals(""))) {
                                markAttendancePostWithLocation(commentreplace, latitude, longitude_, locationAddress);
                            } else {
                                Utilities.showDialog(coordinatorLayout, "Please Enable Your location...");
                            }
                        } else {
                            markAttendancePostWithLocation(commentreplace, latitude, longitude_, locationAddress);
                        }
                    }
                } else {
                    moveToLocationSetting();
                }
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
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(MarkAttendance.this);
                    if (isLocationEnable()) {
                        getCurrentLocation();
                    }
                } else {
                    requestPermission();
                }
            }
        } catch (Exception e) {
        }
    }


    private boolean checkLocationPermission() {
        boolean isPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        } else {
            isPermissionGranted = true;
        }
        return isPermissionGranted;
    }


  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            } else {
                showLocationErrorDialog("Location permission not granted !");
                sendErrorToServer(empoyeeId, "Location permission not granted !", "Privilage id 4", "", "", "");
            }
        }
    }*/

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
    public void onNeedLocationPermission() {
        // assistant.requestLocationPermission();
    }

    @Override
    public void onExplainLocationPermission() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permissionExplanation)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // assistant.requestLocationPermission();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // assistant.requestLocationPermission();

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
                        // assistant.changeLocationSettings();
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
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
            // sendErrorToServer(empoyeeId, "onNewLocationAvailable : Error :" + e.getMessage(), "Privil_Id : 4", "", "", "");
        }

    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // assistant.onActivityResult(requestCode, resultCode);
    }
}
