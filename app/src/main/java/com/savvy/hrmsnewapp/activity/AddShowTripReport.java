package com.savvy.hrmsnewapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class AddShowTripReport extends AppCompatActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    Button reportFromDate, reportTodate;
    EditText edt_FromPlace, edt_ToPlace, edt_Purpose, edt_Duration, edt_LeaderName,
            edt_EncounteredProblem, edt_KeyPerson, edt_PlaceUnit, edt_NarrativeSummary, edt_Action;
    Button btn_Draft, btn_Submit, btn_Cancel;

    Handler handler, handler1;
    public static Runnable runnable, runnable1;
    CustomTextView compareDateTextView;
    LinearLayout compareDateLayout;
    CalanderHRMS calanderHRMS;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences shared;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String employeeId = "", travelId = "", travelReportId = "", VoucherStatus = "", ReportStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show_trip_report);


        setTitle("Trip Report");
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shared = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = shared.getString("EmpoyeeId", "");

        calanderHRMS = new CalanderHRMS(AddShowTripReport.this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        reportFromDate = (Button) findViewById(R.id.travelReport_FromDate);
        reportTodate = (Button) findViewById(R.id.travelReport_ToDate);
        btn_Draft = (Button) findViewById(R.id.edt_travelReport_Draft);
        btn_Submit = (Button) findViewById(R.id.edt_travelReport_Submit);
        btn_Cancel = (Button) findViewById(R.id.edt_travelReport_Cancel);

        edt_FromPlace = (EditText) findViewById(R.id.edt_travelReportFrom);
        edt_ToPlace = (EditText) findViewById(R.id.edt_travelReportTo);
        edt_Purpose = (EditText) findViewById(R.id.edt_travelReportPurposeofTrip);
        edt_Duration = (EditText) findViewById(R.id.edt_travelReport_Duration);
        edt_LeaderName = (EditText) findViewById(R.id.edt_travelReportLeaderName);
        edt_EncounteredProblem = (EditText) findViewById(R.id.edt_travelReportEncounteredProblem);
        edt_KeyPerson = (EditText) findViewById(R.id.edt_travelReportKeyPerson);
        edt_PlaceUnit = (EditText) findViewById(R.id.edt_travelReportPlaceUnits);
        edt_NarrativeSummary = (EditText) findViewById(R.id.edt_travelReport_NarrativeSummary);
        edt_Action = (EditText) findViewById(R.id.edt_travelReport_Action);

        compareDateLayout = (LinearLayout) findViewById(R.id.travelReportCompareDateLayout);
        compareDateTextView = (CustomTextView) findViewById(R.id.travelReportCompareDateTextView);

        reportFromDate.setOnClickListener(this);
        reportTodate.setOnClickListener(this);
        btn_Draft.setOnClickListener(this);
        btn_Submit.setOnClickListener(this);
        btn_Cancel.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            travelId = bundle.getString("ERFS_REQUEST_ID");
            travelReportId = bundle.getString("TTRF_ID");
            VoucherStatus = bundle.getString("TRF_TRAVEL_TRIP_REPORT") == null ? "" : bundle.getString("TRF_TRAVEL_TRIP_REPORT");
            ReportStatus = bundle.getString("TTRF_STATUS") == null ? "" : bundle.getString("TTRF_STATUS");
        }
        getTravelReportData();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void getTravelReportData() {

        if (Utilities.isNetworkAvailable(AddShowTripReport.this)) {

            final String GET_TRAVEL_REPORT_DATA = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelReportFicci/" + employeeId + "/" + travelId + "/" + travelReportId;
            Log.d(TAG, "getTravelReportData: " + GET_TRAVEL_REPORT_DATA);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_TRAVEL_REPORT_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        Log.d(TAG, "onResponse: " + response);
                        JSONArray jsonArray = new JSONArray(response);

                        if (jsonArray.length() > 0) {
                            String depdate = jsonArray.getJSONObject(0).getString("TTRF_DATE_OF_DEPARTURE");
                            String arrivaldate = jsonArray.getJSONObject(0).getString("TTRF_DATE_OF_ARRIVAL");
                            String frompalace = jsonArray.getJSONObject(0).getString("TTRF_FROM_PLACE");
                            String toplace = jsonArray.getJSONObject(0).getString("TTRF_TO_PLACE");
                            String purpose = jsonArray.getJSONObject(0).getString("TTRF_PURPOSE_OF_TRIP");
                            String duration = jsonArray.getJSONObject(0).getString("TTRF_DURATION");
                            String teamleader = jsonArray.getJSONObject(0).getString("TTRF_TEAM_LEADER_NAME");
                            String problemencounter = jsonArray.getJSONObject(0).getString("TTRF_PROBLEMS_ENCOUNTERED");
                            String keyperson = jsonArray.getJSONObject(0).getString("TTRF_KEY_PERSON_CONTACTED_NO_TRIP");
                            String placeunits = jsonArray.getJSONObject(0).getString("TTRF_PLACES_UNITS_VISITED");
                            String narativesummary = jsonArray.getJSONObject(0).getString("TTRF_NARRATIVE_SUMMARY");
                            String ttrf_recommended_action = jsonArray.getJSONObject(0).getString("TTRF_RECOMMENDED_ACTION");

                            reportFromDate.setText(depdate);
                            reportTodate.setText(arrivaldate);
                            edt_FromPlace.setText(frompalace);
                            edt_ToPlace.setText(toplace);
                            edt_Purpose.setText(purpose);
                            edt_Duration.setText(duration);
                            edt_LeaderName.setText(teamleader);
                            edt_EncounteredProblem.setText(problemencounter);
                            edt_KeyPerson.setText(keyperson);
                            edt_PlaceUnit.setText(placeunits);
                            edt_NarrativeSummary.setText(narativesummary);
                            edt_Action.setText(ttrf_recommended_action);

                            if (VoucherStatus.equals("True")) {
                                reportFromDate.setEnabled(false);
                                reportTodate.setEnabled(false);
                                edt_FromPlace.setEnabled(false);
                                edt_ToPlace.setEnabled(false);
                                edt_Purpose.setEnabled(false);
                                edt_Duration.setEnabled(false);
                                edt_LeaderName.setEnabled(false);
                                edt_EncounteredProblem.setEnabled(false);
                                edt_KeyPerson.setEnabled(false);
                                edt_PlaceUnit.setEnabled(false);
                                edt_NarrativeSummary.setEnabled(false);
                                edt_Action.setEnabled(false);

                                btn_Draft.setEnabled(false);
                                btn_Submit.setEnabled(false);
                            }
                            if (ReportStatus.equals("1")) {

                                reportFromDate.setEnabled(true);
                                reportTodate.setEnabled(true);
                                edt_FromPlace.setEnabled(true);
                                edt_ToPlace.setEnabled(true);
                                edt_Purpose.setEnabled(true);
                                edt_Duration.setEnabled(true);
                                edt_LeaderName.setEnabled(true);
                                edt_EncounteredProblem.setEnabled(true);
                                edt_KeyPerson.setEnabled(true);
                                edt_PlaceUnit.setEnabled(true);
                                edt_NarrativeSummary.setEnabled(true);
                                edt_Action.setEnabled(true);
                                btn_Draft.setEnabled(true);
                                btn_Submit.setEnabled(true);
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
            stringRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(AddShowTripReport.this).addToRequestQueue(stringRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.travelReport_FromDate:
                reportFromDate.setText("");
                calanderHRMS.datePicker(reportFromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = reportFromDate.getText().toString().trim();
                            String ToDate = reportTodate.getText().toString().trim();

                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
                            } else {
                                handler.postDelayed(runnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
                break;
            case R.id.travelReport_ToDate:
                calanderHRMS.datePicker(reportTodate);
                handler1 = new Handler();
                runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = reportFromDate.getText().toString().trim();
                            String ToDate = reportTodate.getText().toString().trim();

                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
                            } else {
                                handler1.postDelayed(runnable1, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler1.postDelayed(runnable1, 1000);

                break;
            case R.id.edt_travelReport_Draft:

                saveData(employeeId, edt_PlaceUnit.getText().toString().trim().replaceAll("\\s", " "), "1", travelId, travelReportId, reportFromDate.getText().toString().replace("/", "-")
                        , reportTodate.getText().toString().replace("/", "-"), edt_FromPlace.getText().toString().trim().replaceAll("\\s", " "), edt_ToPlace.getText().toString().trim().replaceAll("\\s", " "),
                        edt_Purpose.getText().toString().trim().replaceAll("\\s", " "),
                        edt_Duration.getText().toString().trim().replaceAll("\\s", " "),
                        edt_LeaderName.getText().toString().trim().replaceAll("\\s", " "),
                        edt_EncounteredProblem.getText().toString().trim().replaceAll("\\s", " "),
                        edt_KeyPerson.getText().toString().trim().replaceAll("\\s", " "),
                        edt_NarrativeSummary.getText().toString().trim().replaceAll("\\s", " "),
                        edt_Action.getText().toString().trim().replaceAll("\\s", " "));

                break;
            case R.id.edt_travelReport_Submit:


                if (reportFromDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Date of Departure");
                } else if (reportTodate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Date of Arrival");
                } else if (edt_FromPlace.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter From Place");
                } else if (edt_ToPlace.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter To Place");
                } else if (edt_Purpose.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Purpose of Trip");
                } else if (edt_Duration.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Duration");
                } else if (edt_LeaderName.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Name of the Team Leader");
                } else if (edt_EncounteredProblem.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Problems Encountered");
                } else if (edt_KeyPerson.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Key Persons");
                } else if (edt_PlaceUnit.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Place/Units");
                } else if (edt_NarrativeSummary.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Narrative Summary");
                } else if (edt_Action.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Action/Way");
                } else {
                    saveData(employeeId, edt_PlaceUnit.getText().toString().trim().replaceAll("\\s", " "), "2", travelId, travelReportId, reportFromDate.getText().toString().replace("/", "-")
                            , reportTodate.getText().toString().replace("/", "-"), edt_FromPlace.getText().toString().trim().replaceAll("\\s", " "), edt_ToPlace.getText().toString().trim().replaceAll("\\s", " "),
                            edt_Purpose.getText().toString().trim().replaceAll("\\s", " "),
                            edt_Duration.getText().toString().trim().replaceAll("\\s", " "),
                            edt_LeaderName.getText().toString().trim().replaceAll("\\s", " "),
                            edt_EncounteredProblem.getText().toString().trim().replaceAll("\\s", " "),
                            edt_KeyPerson.getText().toString().trim().replaceAll("\\s", "_"),
                            edt_NarrativeSummary.getText().toString().trim().replaceAll("\\s", " "),
                            edt_Action.getText().toString().trim().replaceAll("\\s", " "));
                }

                break;
            case R.id.edt_travelReport_Cancel:
                finish();
                break;
        }
    }

    private void saveData(String employeeId, String placevisit, String isdraftorsave, String travelId, String travelReportId, String depdate, String arrivaldate,
                          String from, String to, String purpose, String duration,
                          String teamlead, String problem, String keyperson, String narrative, String action) {

        if (Utilities.isNetworkAvailable(AddShowTripReport.this)) {

            final String SAVE_DATA = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveDraftTravelTripReportFicci";
            Log.d(TAG, "GET_TRAVEL_REQUEST_FICCI: " + SAVE_DATA);

            JSONObject params_final = new JSONObject();
            JSONObject pm = new JSONObject();
            try {
                params_final.put("EMPLOYEE_ID", employeeId);
                params_final.put("placevisit", placevisit);
                params_final.put("isdraftorsave", isdraftorsave);
                params_final.put("TRAVEL_REQUEST_ID", travelId);
                params_final.put("travel_trip_report_id", travelReportId);
                params_final.put("depdate", depdate);
                params_final.put("arrivaldate", arrivaldate);
                params_final.put("from", from);
                params_final.put("to", to);
                params_final.put("purpose", purpose);
                params_final.put("duration", duration);
                params_final.put("teamlead", teamlead);
                params_final.put("problem", problem);
                params_final.put("keyperson", keyperson);
                params_final.put("narrative", narrative);
                params_final.put("action", action);
                pm.put("objSaveDraftTravelTripReportInfoFicci", params_final);

            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SAVE_DATA, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "onResponse: " + response);
                        int value = Integer.valueOf(response.getString("SaveDraftTravelTripReportFicciResult"));

                        if (value > 0) {
                            Utilities.showDialog(coordinatorLayout, "Travel Report Processed Successfully");
                            removeData();
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Error! During processing Travel Report Request");
                        }

                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
            VolleySingleton.getInstance(AddShowTripReport.this).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }

    }

    private void removeData() {
        reportFromDate.setText("");
        reportTodate.setText("");
        edt_FromPlace.setText("");
        edt_ToPlace.setText("");
        edt_Purpose.setText("");
        edt_Duration.setText("");
        edt_LeaderName.setText("");
        edt_EncounteredProblem.setText("");
        edt_KeyPerson.setText("");
        edt_PlaceUnit.setText("");
        edt_NarrativeSummary.setText("");
        edt_Action.setText("");


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 100);
    }

    public void getCompareDate(String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(AddShowTripReport.this)) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);
                Log.e("RequestData", "JSon->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                Log.e("Url", url);

                RequestQueue requestQueue = Volley.newRequestQueue(AddShowTripReport.this);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        compareDateTextView.setText("From Date should be less than or equal To Date!");
                                        compareDateLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        compareDateLayout.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                int socketTime = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonRequest.setRetryPolicy(policy);
                requestQueue.add(jsonRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }
}
