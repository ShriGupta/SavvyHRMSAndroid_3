package com.savvy.hrmsnewapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by orapc7 on 14/12/2017.
 */

public class MarkTeamAttendanceGoogleApi extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, View.OnClickListener{

    CoordinatorLayout coordinatorLayout;
    MarkTeamAttendanceGoogleApi.CurrentDateTimeAsynTask profileasynctask;
    CustomTextView txv_currentDate, txv_currentTime;
    EditText edt_messagefeedback, edt_TeamEmployeeCode;
    String token = "", empoyeeId = "", username = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    GoogleApiClient googleApiClient;
    Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    private double longitude;
    private double latitude;
    Button btn_submit, btn_Cancel;
    CustomTextView txt_employeeCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_attendance_google_api);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));

        txv_currentDate = (CustomTextView) findViewById(R.id.txv_currentDate);
        txv_currentTime = (CustomTextView) findViewById(R.id.txv_currentTime);
        edt_messagefeedback = (EditText) findViewById(R.id.edt_messagefeedback);
        edt_TeamEmployeeCode = (EditText)findViewById(R.id.edt_TeamEmployeeId);

        txt_employeeCode=(CustomTextView)findViewById(R.id.txt_employeeCode);
        String str1 = "<font color='#EE0000'>*</font>";

        txt_employeeCode.setText(Html.fromHtml("Employee Code " + str1));

        btn_submit = (Button) findViewById(R.id.btn_submit);
//        btn_Cancel = (Button) findViewById(R.id.btn_Cancel);
        btn_submit.setOnClickListener(this);
//        btn_Cancel.setOnClickListener(this);


        getCurrentDateTime();
        setUpGClient();

//        new MarkTeamAttendanceGoogleApi.AddNewLocationInPopUp().execute();

        assert getSupportActionBar()!=null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(MarkTeamAttendanceGoogleApi.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void getCurrentDateTime() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                profileasynctask = new MarkTeamAttendanceGoogleApi.CurrentDateTimeAsynTask();
                profileasynctask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            final String getTeamEmployeeCode = edt_TeamEmployeeCode.getText().toString().trim();
            if (!getTeamEmployeeCode.equals("")) {
                if (mylocation != null) {
                    latitude = mylocation.getLatitude();
                    longitude = mylocation.getLongitude();

                    String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ", "-");
                    markTeamAttendancePost(commentreplace, String.valueOf(latitude), String.valueOf(longitude), getTeamEmployeeCode);
                } else {
                    Utilities.showDialog(coordinatorLayout, "Location Not Found, Try Again.");
                    getMyLocation();
                }
            } else {
                Utilities.showDialog(coordinatorLayout, "Please Enter Employee Code.");
            }

            //            case R.id.btn_Cancel:
//                finish();
//                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            double latitude=mylocation.getLatitude();
            double longitude=mylocation.getLongitude();
            Log.e("Locqation","Latitude = "+latitude+" Longitude = "+longitude);
//            Toast.makeText(getApplicationContext(),"Location Latitude = "+latitude+" Longitude = "+longitude,Toast.LENGTH_LONG).show();
//            latitudeTextView.setText("Latitude : "+latitude);
//            longitudeTextView.setText("Longitude : "+longitude);
            //Or Do whatever you want with your location

        }
    }

    private Location getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MarkTeamAttendanceGoogleApi.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(100);
                    locationRequest.setFastestInterval(100);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MarkTeamAttendanceGoogleApi.this);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat.checkSelfPermission(MarkTeamAttendanceGoogleApi.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                        if(mylocation!=null) {
                                            latitude = mylocation.getLatitude();
                                            longitude = mylocation.getLongitude();

//                                            Toast.makeText(MarkAttendanceGoogleApi.this, "Location button "+latitude+","+longitude, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        if(mylocation!=null) {
                                            latitude = mylocation.getLatitude();
                                            longitude = mylocation.getLongitude();

//                                            Toast.makeText(MarkAttendanceGoogleApi.this, "Location Resolution "+latitude+","+longitude, Toast.LENGTH_SHORT).show();

                                        }
                                        status.startResolutionForResult(MarkTeamAttendanceGoogleApi.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
        return mylocation;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
//                        moveMap();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(MarkTeamAttendanceGoogleApi.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MarkTeamAttendanceGoogleApi.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }


    private class CurrentDateTimeAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        String token;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(MarkTeamAttendanceGoogleApi.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String CURRENTDATETIME_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc//GetCurrentDateTime";

                System.out.println("CURRENTDATETIME_URL===="+CURRENTDATETIME_URL);
                JSONParser jParser = new JSONParser(MarkTeamAttendanceGoogleApi.this); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(CURRENTDATETIME_URL, "GET");
                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try{
                    // {"ServerDate":"4\/25\/2017 8:04:30 PM","ServerTime":"8:04 PM","errorMessage":null}
                    System.out.println("RESULT HARIOM===="+result);
                    JSONObject jsonobj = new JSONObject(result);
//                    String  serverDate=jsonobj.getString("ServerDate");
                    String  serverDate=jsonobj.getString("ServerDateDDMMYYYYY");
                    String  serverTime=jsonobj.getString("ServerTime");
                    String  errorMessage=jsonobj.getString("errorMessage");

                    String[] serverDateSplit=serverDate.split(" ");
                    String   replacecurrDate=serverDateSplit[0].replace("\\","");
                    txv_currentTime.setText(serverTime);
                    txv_currentDate.setText(replacecurrDate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public void markTeamAttendancePost(String attendanceRemark, String latitude, String longitude, String teamEmployeeCode){

        try {
            Log.e("Mark Team", " Method " + "Called");
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(MarkTeamAttendanceGoogleApi.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTeamAttendanceMarkPost";
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", attendanceRemark);
            params_final.put("latitude", latitude);
            params_final.put("longitude", longitude);
            params_final.put("teamEmployeeCode", teamEmployeeCode);

            RequestQueue requestQueue = Volley.newRequestQueue(MarkTeamAttendanceGoogleApi.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());
                            pDialog.dismiss();
                            try {
                                JSONObject jsonobj = response.getJSONObject("SaveTeamAttendanceMarkPostResult");

                                String  statusId=jsonobj.getString("statusId");
                                String  statusDescription=jsonobj.getString("statusDescription");
                                String  errorMessage=jsonobj.getString("errorMessage");

                                if(statusId.equals("1") ){
                                    Utilities.showDialog(coordinatorLayout,statusDescription);
                                    edt_messagefeedback.setText("");
                                    edt_TeamEmployeeCode.setText("");
                                }else if(statusId.equals("2") ){
                                    Utilities.showDialog(coordinatorLayout, statusDescription);
                                }
                                else if(statusId.equals("3") ){
                                    Utilities.showDialog(coordinatorLayout, statusDescription);
                                }
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
            int socketTimeout = 300000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home) {
            Intent intent = new Intent(this,DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId","5");
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

    public Location GetLocationForAttendance(){
        Location loc = mylocation;

        if(loc!=null){
            if(mylocation!=null) {
                latitude = mylocation.getLatitude();
                longitude = mylocation.getLongitude();
            }
            return loc;
        }
        return loc;
    }

    public class AddNewLocationInPopUp extends AsyncTask<Void, Void, Location> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MarkTeamAttendanceGoogleApi.this);
            pDialog.setMessage("Finding Location...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Location doInBackground(Void... voids) {

            Location myLoc = getMyLocation();
            return myLoc;
        }

        @Override
        protected void onPostExecute(Location location) {
            super.onPostExecute(location);

            if(location!=null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(MarkTeamAttendanceGoogleApi.this, "Location Post "+latitude, Toast.LENGTH_SHORT).show();
                Log.e("Location Post","Hello "+latitude);
                pDialog.dismiss();
            } else{
                Toast.makeText(MarkTeamAttendanceGoogleApi.this, "Location Else "+latitude, Toast.LENGTH_SHORT).show();
                Log.e("Location Else","pDialog "+latitude);
//                new AddNewLocationInPopUp().execute();
//                getMyLocation();
            }
        }

    }


}
