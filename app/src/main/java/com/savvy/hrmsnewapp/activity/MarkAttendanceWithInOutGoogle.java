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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
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

public class MarkAttendanceWithInOutGoogle extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, View.OnClickListener {

    CustomTextView txt_cal_date, txt_cal_time;
    Button btn_punch_in, btn_punch_out, btn_back_cancel;
    EditText edt_messagefeedback;
    ArrayList<HashMap<String,String>> arlData;

    MarkAttendanceWithInOutGoogle.CurrentDateTimeAsynTask currentDateTimeAsynTask;
    GoogleApiClient googleApiClient;

    Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    private double longitude;
    private double latitude;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String token = "",empoyeeId = "",username = "";
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance_with_in_out_google);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));

        btn_punch_in = (Button)findViewById(R.id.btn_punchIn);
        btn_punch_out = (Button)findViewById(R.id.btn_punchOut);
        btn_back_cancel = (Button)findViewById(R.id.btn_back_cancel_1);

        txt_cal_date = (CustomTextView)findViewById(R.id.txv_currentDate);
        txt_cal_time = (CustomTextView)findViewById(R.id.txv_currentTime);

        edt_messagefeedback = (EditText) findViewById(R.id.edt_messagefeedback);

        btn_punch_in.setOnClickListener(this);
        btn_punch_out.setOnClickListener(this);
        btn_back_cancel.setOnClickListener(this);

        getMyLocation();
        getCurrentDateTime();
        setUpGClient();

        setUpToolBar();
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(MarkAttendanceWithInOutGoogle.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void getCurrentDateTime() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                currentDateTimeAsynTask = new MarkAttendanceWithInOutGoogle.CurrentDateTimeAsynTask();
                currentDateTimeAsynTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_punchIn:
                if(mylocation!=null){
                    latitude = mylocation.getLatitude();
                    longitude = mylocation.getLongitude();
                    String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ","-");
                    markAttendanceInOut(commentreplace, String.valueOf(latitude), String.valueOf(longitude),"I");
                } else{
                    getMyLocation();
                    Utilities.showDialog(coordinatorLayout,"Location Not Found, Try Again.");
                }
                break;

            case R.id.btn_punchOut:
                if(mylocation!=null){
                    latitude = mylocation.getLatitude();
                    longitude = mylocation.getLongitude();
                    String commentreplace = edt_messagefeedback.getText().toString().trim().replace(" ","-");
                    markAttendanceInOut(commentreplace, String.valueOf(latitude), String.valueOf(longitude),"O");
                } else{
                    getMyLocation();
                    Utilities.showDialog(coordinatorLayout,"Location Not Found, Try Again.");
                }
                break;

            case R.id.btn_back_cancel_1:
                finish();
                break;

            default:
                break;

        }
    }

    public void markAttendanceInOut(String comment,String lat,String lon,String type){
        try {
//            string userName, string empoyeeId, string securityToken, string attendanceRemark, string latitude, string longitude,string punchType
            arlData = new ArrayList<HashMap<String,String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(MarkAttendanceWithInOutGoogle.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/SaveAttendanceMarkInOut";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", comment);
            params_final.put("latitude", lat);
            params_final.put("longitude", lon);
            params_final.put("punchType", type);

            if(Utilities.isNetworkAvailable(MarkAttendanceWithInOutGoogle.this)) {
                RequestQueue requestQueue = Volley.newRequestQueue(MarkAttendanceWithInOutGoogle.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("Value", " Length = " + (String.valueOf(response)).length() + " Value = " + response.toString());
                                pDialog.dismiss();

                                try {
                                    JSONObject jsonobj = response.getJSONObject("SaveAttendanceMarkInOutResult");
                                    String  statusId=jsonobj.getString("statusId");
                                    String  statusDescription=jsonobj.getString("statusDescription");
                                    String  errorMessage=jsonobj.getString("errorMessage");

                                    if(statusId.equals("1") ){
                                        Utilities.showDialog(coordinatorLayout,statusDescription);
                                        edt_messagefeedback.setText("");
                                    }else if(statusId.equals("2") ){
                                        Utilities.showDialog(coordinatorLayout, statusDescription);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
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
            } else{
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
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
            Double latitude=mylocation.getLatitude();
            Double longitude=mylocation.getLongitude();
            Log.e("Locqation","Latitude = "+latitude+" Longitude = "+longitude);
//            Toast.makeText(getApplicationContext(),"Location Latitude = "+latitude+" Longitude = "+longitude,Toast.LENGTH_LONG).show();
//            latitudeTextView.setText("Latitude : "+latitude);
//            longitudeTextView.setText("Longitude : "+longitude);
            //Or Do whatever you want with your location

        }
    }

    public Location getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MarkAttendanceWithInOutGoogle.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(100);
                    locationRequest.setFastestInterval(100);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MarkAttendanceWithInOutGoogle.this);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat.checkSelfPermission(MarkAttendanceWithInOutGoogle.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                        if(mylocation!=null) {
                                            latitude = mylocation.getLatitude();
                                            longitude = mylocation.getLongitude();

//                             Toast.makeText(MarkAttendanceGoogleApi.this, "Location button "+latitude+","+longitude, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    break;
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
//                                        // --------------------------------------------------------------------NamanSingh_28Feb2022
//                                        Toast.makeText(MarkAttendanceWithInOutGoogle.this,"Location"+result,Toast.LENGTH_LONG).show();
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        if(mylocation!=null) {
                                            latitude = mylocation.getLatitude();
                                            longitude = mylocation.getLongitude();

//                                            Toast.makeText(MarkAttendanceGoogleApi.this, "Location Resolution "+latitude+","+longitude, Toast.LENGTH_SHORT).show();

                                        }
                                        status.startResolutionForResult(MarkAttendanceWithInOutGoogle.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getMyLocation();
//                        moveMap();
                    break;
                case Activity.RESULT_CANCELED:
                    finish();
                    break;
            }
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(MarkAttendanceWithInOutGoogle.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(MarkAttendanceWithInOutGoogle.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    private class CurrentDateTimeAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(MarkAttendanceWithInOutGoogle.this);
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
                JSONParser jParser = new JSONParser(MarkAttendanceWithInOutGoogle.this); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        CURRENTDATETIME_URL, "GET");

                if (json != null) {

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
                    txt_cal_time.setText(serverTime);
                    txt_cal_date.setText(replacecurrDate);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu != null) {
            menu.findItem(R.id.home_dashboard).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home) {
            Intent intent = new Intent(this,DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId","34");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
//            startActivity(new Intent(this, DashBoardActivity.class));
        }
        return super.onOptionsItemSelected(item);

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

    public class AddNewLocationInPopUp extends AsyncTask<Void, Void, Location> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MarkAttendanceWithInOutGoogle.this);
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
        protected void onPostExecute(final Location location) {
            super.onPostExecute(location);

            if(location!=null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
//                Toast.makeText(MarkAttendanceWithInOutGoogle.this, "Location Post "+latitude, Toast.LENGTH_SHORT).show();
                Log.e("Location Post","Hello "+latitude);
                pDialog.dismiss();
            } else{

                Thread thread = new Thread(){
                    public void run(){
                        try{
                            sleep(3000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        } finally {
                            if(location!=null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
//                                Toast.makeText(MarkAttendanceWithInOutGoogle.this, "Location Thread "+latitude, Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            } else{
                                Toast.makeText(MarkAttendanceWithInOutGoogle.this, "Location Not Found ", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                        }
                    }
                };
                thread.start();
//                Toast.makeText(MarkAttendanceWithInOutGoogle.this, "Location Else "+latitude, Toast.LENGTH_SHORT).show();
                Log.e("Location Else","pDialog "+latitude);
//                new AddNewLocationInPopUp().execute();
//                getMyLocation();
            }
        }


    }
}
