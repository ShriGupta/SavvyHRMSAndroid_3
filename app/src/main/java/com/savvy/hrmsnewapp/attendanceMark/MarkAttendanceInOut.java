package com.savvy.hrmsnewapp.attendanceMark;

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
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.BaseActivity;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.FaceDetectionHolderFrgamnet;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.LocationAssistant;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MarkAttendanceInOut extends BaseActivity implements View.OnClickListener, LocationAssistant.Listener {

    CoordinatorLayout coordinatorLayout;
    CustomTextView txv_currentDate, txv_currentTime;
    EditText edt_messagefeedback;
    String token = "", empoyeeId = "", username = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    private TextView address;
    Button btn_punch_in, btn_punch_out;
    private static final String TAG = "MarkTeamAttendanceNew";

    public static final int REQUEST_CODE = 1;
    private static String latitude = "";
    private static String longitude = "";
    String locationAddress = "";
    private LocationAssistant assistant;

    FusedLocationProviderClient fusedClient;
    private LocationCallback mCallback;
    String countryName = "";
    String geoString;
    ProgressDialog locationProcessDialog;
    private LocationRequest mRequest;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance_with_in_out_google);

        address = (CustomTextView) findViewById(R.id.address);

       // initLocation();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));

        txv_currentDate = (CustomTextView) findViewById(R.id.txv_currentDate);
        txv_currentTime = (CustomTextView) findViewById(R.id.txv_currentTime);
        edt_messagefeedback = (EditText) findViewById(R.id.edt_messagefeedback);

        btn_punch_in = (Button) findViewById(R.id.btn_punchIn);
        btn_punch_out = (Button) findViewById(R.id.btn_punchOut);

        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        mCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "locationResult null");
                    return;
                }
                Log.d(TAG, "received " + locationResult.getLocations().size() + " locations");
                for (Location loc : locationResult.getLocations()) {
                    try {
                        Log.e(TAG, "onLocationResult: " + loc.getLatitude() + " , " + loc.getLongitude());
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

                            if (locationProcessDialog != null && locationProcessDialog.isShowing())
                                locationProcessDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d(TAG, "locationAvailability is " + locationAvailability.isLocationAvailable());
                super.onLocationAvailability(locationAvailability);
            }
        };

        startLocationUpdate();

        btn_punch_in.setOnClickListener(this);
        btn_punch_out.setOnClickListener(this);

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

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_punchIn: {
                  //  initLocation();
                    if (locationAddress.equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please wait while fetching your location...");
                    } else {
                        String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ", "-");
                        markAttendanceInOutWithLocation(commentreplace, latitude, longitude, "I", locationAddress);
                    }


                    break;
                }
                case R.id.btn_punchOut: {
                   // initLocation();

                    if (locationAddress.equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please wait while fetching your location...");
                    } else {
                        String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ", "-");
                        markAttendanceInOutWithLocation(commentreplace, latitude, longitude, "O", locationAddress);
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
            Log.e(TAG, "markAttendanceInOutWithLocation: " + params_final.toString());

            if (Utilities.isNetworkAvailable(MarkAttendanceInOut.this)) {
                RequestQueue requestQueue = Volley.newRequestQueue(MarkAttendanceInOut.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                dismissProgressDialog();

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
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        markAttendanceInOut(comment, String.valueOf(lat), String.valueOf(lon), type);
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
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdate();
            } else {
                checkLocationPermission();

               // showLocationErrorDialog("Location permission not granted !");
              //  sendErrorToServer(empoyeeId, "Location permission not granted !", "Privilage id 34", "", "", "");
            }
        }
    }


    private void updateUI(String locationAddrees) {
        try {
            address.setText(locationAddrees);
        } catch (Exception e) {
            e.printStackTrace();
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
        locationAddress = Utilities.getAddressFromLateLong(MarkAttendanceInOut.this, location.getLatitude(), location.getLongitude());
        if (locationAddress.equals("")) {
            locationAddress = "Latitude: " + latitude + "\n" + "Longitude: " + longitude;
            updateUI(locationAddress);
        } else {
            updateUI(locationAddress);
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

    private boolean checkMockLocation(Location location) {
        boolean isMock;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            isMock = !Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
        return isMock;
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
    private void startLocationUpdate() {
        mRequest = new LocationRequest();
        mRequest.setInterval(10000);//time in ms; every ~10 seconds
        mRequest.setFastestInterval(5000);
        mRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkLocationPermission()) {
            fusedClient.requestLocationUpdates(mRequest, mCallback, Looper.getMainLooper());
        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1);
        }
    }

    boolean checkLocationPermission() {
        boolean isPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationale();
                isPermissionGranted = false;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE}, 2);
                isPermissionGranted = false;

            }
        } else {
            final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            assert manager != null;
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                isPermissionGranted = true;
            }
        }
        return isPermissionGranted;
    }
    private void showRationale() {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this).setMessage("SavvyHRMS wants to access your device location..").setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MarkAttendanceInOut.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }


}
