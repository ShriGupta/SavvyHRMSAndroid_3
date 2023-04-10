package com.savvy.hrmsnewapp.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

public class MarkAttendanceWithInOut extends BaseFragment implements View.OnClickListener {

    MarkAttendanceWithInOut.CurrentDateTimeAsynTask currentDateTimeAsynTask;
    ArrayList<HashMap<String, String>> arlData;
    Button btn_punch_in, btn_punch_out;
    EditText edt_comment;
    CustomTextView txt_cal_date, txt_cal_time;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String token = "", empoyeeId = "", username = "";

    Handler handler1;
    Runnable rRunnable;
    static int counter = 0;
    int count_method = 0;

    private TrackGPS gps;
    double longitude;
    double latitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));

        getCurrentDateTime();

    }

    private void getCurrentDateTime() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                currentDateTimeAsynTask = new MarkAttendanceWithInOut.CurrentDateTimeAsynTask();
                currentDateTimeAsynTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark_with_in_out, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_punch_in = getActivity().findViewById(R.id.btn_punchIn);
        btn_punch_out = getActivity().findViewById(R.id.btn_punchOut);

        txt_cal_date = getActivity().findViewById(R.id.txv_currentDate);
        txt_cal_time = getActivity().findViewById(R.id.txv_currentTime);

        edt_comment = getActivity().findViewById(R.id.edt_messagefeedback);

        btn_punch_in.setOnClickListener(this);
        btn_punch_out.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_punchIn) {
            counter = 0;
            String getcomment = edt_comment.getText().toString().trim();
            final String commentreplace = getcomment.replace(" ", "_");

            if (Utilities.isNetworkAvailable(getActivity())) {
                gps = new TrackGPS(getActivity());
                if (gps.canGetLocation()) {
                    final ProgressDialog pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Find Location...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    handler1 = new Handler();
                    rRunnable = new Runnable() {
                        @Override
                        public void run() {
                            longitude = gps.getLongitude();
                            latitude = gps.getLatitude();
                            Log.d("Counter", "" + counter);
                            Log.d("Location", "" + latitude + "," + longitude);

                            if (counter != 5) {
                                if (latitude != 0.0 && longitude != 0.0) {
                                    count_method = 6;
                                    pDialog.cancel();
                                    String lat = String.valueOf(latitude);
                                    String lon = String.valueOf(longitude);
                                    String locatioAddress = Utilities.getAddressFromLateLong(getActivity(), latitude, longitude);
                                    markAttendanceInOut(commentreplace, lat, lon,locatioAddress, "I");
//                                    markAttendance(longitude, latitude, commentreplace);
                                    return;
                                } else {
                                    handler1.postDelayed(rRunnable, 200);
                                }
                            } else {
                                return;
                            }
                            counter++;
                        }
                    };
                    handler1.postDelayed(rRunnable, 200);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {

                            if (latitude == 0.0 && longitude == 0.0 && counter >= 5 && count_method != 6) {
                                pDialog.cancel();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                dialog.setCancelable(true);
                                dialog.setTitle("Location Error");
                                dialog.setMessage("Punch Regularization not marked due to GPS location not found.\n" +
                                        "please try after some time!");
                                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                                Log.d("Location If", "Latit" + latitude + "," + longitude);
                            }
                        }
                    }, 1000 * 5);
                } else {
                    gps.showSettingsAlert();
                }
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        }
        if (v.getId() == R.id.btn_punchOut) {
            counter = 0;
            String getcomment = edt_comment.getText().toString().trim();
            final String commentreplace = getcomment.replace(" ", "_");

            if (Utilities.isNetworkAvailable(getActivity())) {
                gps = new TrackGPS(getActivity());
                if (gps.canGetLocation()) {
                    final ProgressDialog pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Find Location...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    handler1 = new Handler();
                    rRunnable = new Runnable() {
                        @Override
                        public void run() {
                            longitude = gps.getLongitude();
                            latitude = gps.getLatitude();
                            Log.d("Counter", "" + counter);
                            Log.d("Location", "" + latitude + "," + longitude);

                            if (counter != 5) {
                                if (latitude != 0.0 && longitude != 0.0) {
                                    count_method = 6;
                                    String lat = String.valueOf(latitude);
                                    String lon = String.valueOf(longitude);
                                    pDialog.cancel();
                                    String locatioAddress = Utilities.getAddressFromLateLong(getActivity(), latitude, longitude);
                                    markAttendanceInOut(commentreplace, lat, lon, locatioAddress,"O");
//                                    markAttendance(longitude, latitude, commentreplace);
                                    return;
                                } else {
                                    handler1.postDelayed(rRunnable, 200);
                                }
                            } else {
                                return;
                            }
                            counter++;
                        }
                    };
                    handler1.postDelayed(rRunnable, 200);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {

                            if (latitude == 0.0 && longitude == 0.0 && counter >= 5 && count_method != 6) {
                                pDialog.cancel();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                dialog.setCancelable(true);
                                dialog.setTitle("Location Error");
                                dialog.setMessage("Punch Regularization not marked due to GPS location not found.\n" +
                                        "please try after some time!");
                                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                                Log.d("Location If", "Latit" + latitude + "," + longitude);
                            }
                        }
                    }, 1000 * 5);
                } else {
                    gps.showSettingsAlert();
                }
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        }

    }

    public void markAttendanceInOut(String comment, String lat, String lon,String location_address, String type) {
        try {
//            string userName, string empoyeeId, string securityToken, string attendanceRemark, string latitude, string longitude,string punchType
            arlData = new ArrayList<HashMap<String, String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveAttendanceMarkInPremise";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", comment);
            params_final.put("latitude", lat);
            params_final.put("longitude", lon);
            params_final.put("punchType", type);
            params_final.put("location", location_address);

            if (Utilities.isNetworkAvailable(getActivity())) {
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("Value", " Length = " + (String.valueOf(response)).length() + " Value = " + response.toString());
                                pDialog.dismiss();

                                try {
                                    JSONObject jsonobj = response.getJSONObject("SaveAttendanceMarkInPremiseResult");

                                    String statusId = jsonobj.getString("statusId");
                                    String statusDescription = jsonobj.getString("statusDescription");
                                    String errorMessage = jsonobj.getString("errorMessage");
                                    String exception = jsonobj.getString("exception");
                                    Log.d("Response", statusId + "," + statusDescription + "," + errorMessage + "," + exception);

                                    if (statusId.equals("1")) {
                                        if (exception.toUpperCase().equals("TRUE")) {
                                            Utilities.showDialog(coordinatorLayout, statusDescription + " But Out Of Define Range Location !");
                                            edt_comment.setText("");
                                        } else {
                                            Utilities.showDialog(coordinatorLayout, statusDescription);
                                            edt_comment.setText("");
                                        }
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
                        pDialog.dismiss();
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
            pDialog = new ProgressDialog(getActivity());
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
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        CURRENTDATETIME_URL, "GET");

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
}
