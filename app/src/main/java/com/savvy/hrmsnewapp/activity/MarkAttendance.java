package com.savvy.hrmsnewapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

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
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.service.TrackGPS;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MarkAttendance extends BaseActivity implements View.OnClickListener, LocationListener {
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    MarkAttendance.CurrentDateTimeAsynTask profileasynctask;
    CustomTextView txv_currentDate, txv_currentTime;
    Button btn_submit, btn_Cancel;
    EditText edt_messagefeedback;
    private TrackGPS gps;
    double longitude = 0, longi;
    double latitude = 0, latit;
    //    MarkAttendance.MarkAttendanceAsynTask markattendanceasynctask;
    String token = "", empoyeeId = "", username = "";
    Location loc = null;

    Handler handler;
    Runnable rRunnable;
    static int counter = 0;
    int count_method = 0;
    private Boolean flag = false;
    private LocationManager locationManager = null;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));

        txv_currentDate = (CustomTextView) findViewById(R.id.txv_currentDate);
        txv_currentTime = (CustomTextView) findViewById(R.id.txv_currentTime);
        edt_messagefeedback = (EditText) findViewById(R.id.edt_messagefeedback);
        btn_submit = (Button) findViewById(R.id.btn_submit);
//        btn_Cancel = (Button) findViewById(R.id.btn_Cancel);
        btn_submit.setOnClickListener(this);
//        btn_Cancel.setOnClickListener(this);


        getCurrentDateTime();

        setUpToolBar();
    }

    private void getCurrentDateTime() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                profileasynctask = new MarkAttendance.CurrentDateTimeAsynTask();
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
        switch (v.getId()) {
            case R.id.btn_submit:
//                counter = 0;
                String getcomment = edt_messagefeedback.getText().toString().trim();
                final String commentreplace = getcomment.replace(" ", "_");
                try {
                    if (Utilities.isNetworkAvailable(MarkAttendance.this)) {
                        flag = displayGpsStatus();
//                        final ProgressDialog pDialog = new ProgressDialog(MarkAttendance.this);
//                        pDialog.setMessage("Please Wait...");
//                        pDialog.setIndeterminate(false);
//                        pDialog.setCancelable(true);
//                        pDialog.show();
                        if (flag) {

                            Log.v(TAG, "onClick");
                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (locationManager != null) {
                                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, MarkAttendance.this);
                                    loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                                    if (loc != null) {
                                        latitude = loc.getLatitude();
                                        longitude = loc.getLongitude();
                                    }

                                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, MarkAttendance.this);
                                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    if (loc != null) {
                                        latitude = loc.getLatitude();
                                        longitude = loc.getLongitude();
                                    }
                                }
                                if (latitude == 0.0 && longitude == 0.0) {
                                    counter++;
                                    if (counter == 3) {
                                        counter = 0;
                                        String locationAddress = Utilities.getAddressFromLocation(MarkAttendance.this, loc);
                                        markAttendancePost(commentreplace, String.valueOf(latitude), String.valueOf(longitude), locationAddress);
                                    } else {
                                        Utilities.showDialog(coordinatorLayout, "Location Not Found, Try Again.");
                                    }
                                } else {
                                    String locationAddress = Utilities.getAddressFromLocation(MarkAttendance.this, loc);
                                    markAttendancePost(commentreplace, String.valueOf(latitude), String.valueOf(longitude), locationAddress);
                                }
                            }
                        } else {
                            alertbox("Gps Status!!", "Your GPS is: OFF");
                        }
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Location Not Found, Please try after some time", Toast.LENGTH_LONG).show();
                }
                break;

//            case R.id.btn_Cancel:
//                finish();
//                break;

            default:
                break;

        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
        return gpsStatus;
    }

    protected void alertbox(String title, String mymessage) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private class CurrentDateTimeAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        String token;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(MarkAttendance.this);
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
                String CURRENTDATETIME_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc//GetCurrentDateTime";

                System.out.println("CURRENTDATETIME_URL====" + CURRENTDATETIME_URL);
                JSONParser jParser = new JSONParser(MarkAttendance.this); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
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
                try {
                    // {"ServerDate":"4\/25\/2017 8:04:30 PM","ServerTime":"8:04 PM","errorMessage":null}
                    System.out.println("RESULT HARIOM====" + result);
                    JSONObject jsonobj = new JSONObject(result);
//                    String  serverDate=jsonobj.getString("ServerDate");
                    String serverDate = jsonobj.getString("ServerDateDDMMYYYYY");
                    String serverTime = jsonobj.getString("ServerTime");
                    String errorMessage = jsonobj.getString("errorMessage");

                    String[] serverDateSplit = serverDate.split(" ");
                    String replacecurrDate = serverDateSplit[0].replace("\\", "");
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

    public void markAttendancePost(String attendanceRemark, String latitude, String longitude, String location_address) {

        try {
            Log.e("Mark Att", " Method " + "Called");
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(MarkAttendance.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveAttendanceMarkPost";
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", attendanceRemark);
            params_final.put("latitude", latitude);
            params_final.put("longitude", longitude);
            params_final.put("location", location_address);
            Log.e(TAG, "markAttendancePost: " + params_final.toString());

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            pDialog.dismiss();

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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", shared.getString("EMPLOYEE_ID_FINAL", ""));
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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
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
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId", "4");
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
