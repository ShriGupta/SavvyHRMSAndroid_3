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
import android.provider.Settings;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
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
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.service.TrackGPS;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SaveMarkAttWithInOut extends BaseActivity implements View.OnClickListener,LocationListener{

    CustomTextView txt_cal_date, txt_cal_time;
    Button btn_punch_in, btn_punch_out, btn_back_cancel;
    EditText edt_messagefeedback;
    ArrayList<HashMap<String,String>> arlData;

    SaveMarkAttWithInOut.CurrentDateTimeAsynTask currentDateTimeAsynTask;
    private TrackGPS gps;
    double longitude = 0, longi;
    double latitude = 0, latit;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String token = "",empoyeeId = "",username = "";

    private LocationManager locationManager = null;
    CoordinatorLayout coordinatorLayout;
    Location loc = null;
    static int counter = 0;
    private Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_mark_att_with_in_out);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));

        btn_punch_in = (Button)findViewById(R.id.btn_punchIn);
        btn_punch_out = (Button)findViewById(R.id.btn_punchOut);
        btn_back_cancel = (Button)findViewById(R.id.btn_back_cancel_1);


        String str = "<<";
        btn_back_cancel.setText(Html.fromHtml(str));
        btn_back_cancel.setVisibility(View.GONE);

        txt_cal_date = (CustomTextView)findViewById(R.id.txv_currentDate);
        txt_cal_time = (CustomTextView)findViewById(R.id.txv_currentTime);

        edt_messagefeedback = (EditText) findViewById(R.id.edt_messagefeedback);

        btn_punch_in.setOnClickListener(this);
        btn_punch_out.setOnClickListener(this);
        btn_back_cancel.setOnClickListener(this);

        getCurrentDateTime();
        setUpToolBar();
    }

    private void getCurrentDateTime() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                currentDateTimeAsynTask = new SaveMarkAttWithInOut.CurrentDateTimeAsynTask();
                currentDateTimeAsynTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void markAttendanceInOut(String comment,String lat,String lon,String type){
        try {
//            string userName, string empoyeeId, string securityToken, string attendanceRemark, string latitude, string longitude,string punchType
            arlData = new ArrayList<HashMap<String,String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(SaveMarkAttWithInOut.this);
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

            if(Utilities.isNetworkAvailable(SaveMarkAttWithInOut.this)) {
                RequestQueue requestQueue = Volley.newRequestQueue(SaveMarkAttWithInOut.this);
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
    public void onClick(View v) {
        if(v.getId()==R.id.btn_punchIn){
            String getcomment = edt_messagefeedback.getText().toString().trim();
            final String commentreplace = getcomment.replace(" ", "_");

            MarkAttendanceLocation(commentreplace,"I");
        }
        if(v.getId()==R.id.btn_punchOut){
            String getcomment = edt_messagefeedback.getText().toString().trim();
            final String commentreplace = getcomment.replace(" ", "_");

            MarkAttendanceLocation(commentreplace,"O");
        }
        if(v.getId()==R.id.btn_back_cancel_1){
            this.finish();
//            Intent intent = new Intent(SaveMarkAttWithInOut.this,LeaveApplyActivity.class);
//            startActivity(intent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
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
            pDialog = new ProgressDialog(SaveMarkAttWithInOut.this);
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
                JSONParser jParser = new JSONParser(SaveMarkAttWithInOut.this); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
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

    public void MarkAttendanceLocation(String comment, String type){
        try {
            if (Utilities.isNetworkAvailable(SaveMarkAttWithInOut.this)) {
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
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, SaveMarkAttWithInOut.this);
                            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (loc != null) {
                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();
                            }

                        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, SaveMarkAttWithInOut.this);
                            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (loc != null) {
                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();
                            }
                        }
                        if(latitude==0.0&&longitude==0.0){
                            counter++;
                            if (counter == 3) {
                                counter = 0;
                                markAttendanceInOut(comment, String.valueOf(latitude), String.valueOf(longitude),type);
                            } else{
                                Utilities.showDialog(coordinatorLayout,"Location Not Found, Try Again.");
                            }
                        } else {
                            markAttendanceInOut(comment, String.valueOf(latitude), String.valueOf(longitude),type);
                        }
                    }
                } else {
                    alertbox("Gps Status!!", "Your GPS is: OFF");
                }
            } else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Location Not Found, Please try after some time",Toast.LENGTH_LONG).show();
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
            case 2:{
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
}
