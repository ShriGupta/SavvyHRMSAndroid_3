package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

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
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ODRequestFragment extends BaseFragment {

    protected CoordinatorLayout coordinatorLayout;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "", employeeId = "", username = "";
    SharedPreferences shared;

    Button btn_od_from_date, btn_od_to_date, btn_od_to_time, btn_od_from_time, btn_submit, btnOdRequestSubmit;
    Spinner spin_reason_od;

    EditText edt_od_reason;
    CalanderHRMS calanderHRMS;
    CustomSpinnerAdapter customspinnerAdapter;

    ODRequestFragmentAsync odRequestFragmentAsync;

    CustomTextView fullDay_on, halfDay_on, firstHalf_on, secondHalf_on, fullDay_off, halfDay_off, firstHalf_off, secondHalf_off;

    String str_hour = "", str_minute = "";
    int pos, pos1;


    LinearLayout linear_compareDate;
    CustomTextView txt_compareDate;
    Handler handler;
    Runnable rRunnable;

    String FROM_DATE = "", TO_DATE = "";
    String COMPARE_DATE = "";
    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData;
    String positionId = "", positionValue = "";

    String spinnerPosition = "";


    String odStatusPosition = "1";
    String odsubstatusPosition = "0";

    CustomTextView txtOD_ReasonTitle, txtOD_TypeTitle, txtOD_ToDateTitle, txtOD_FromDateTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        employeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));
        arlRequestStatusData = new ArrayList<HashMap<String, String>>();

        GetOvertimeReason getOvertimeReason = new GetOvertimeReason();
        getOvertimeReason.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.od_request_fragment, container, false);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        calanderHRMS = new CalanderHRMS(getActivity());

        btn_od_from_date = view.findViewById(R.id.btn_from_oddate);
        btn_od_to_date = view.findViewById(R.id.btn_to_oddate);
        btn_od_from_time = view.findViewById(R.id.btn_od_time_from);
        btn_od_to_time = view.findViewById(R.id.btn_od_to_time);
        //btn_submit = (Button)view.findViewById(R.id.btn_od_request_submit);
        btnOdRequestSubmit = view.findViewById(R.id.btn_od_request_submit);
        spin_reason_od = view.findViewById(R.id.spin_od_reason);
        edt_od_reason = view.findViewById(R.id.edt_od_reason);

        linear_compareDate = view.findViewById(R.id.linear_compareDate);
        txt_compareDate = view.findViewById(R.id.txt_compareDate);
        linear_compareDate.setVisibility(View.GONE);

        txtOD_ReasonTitle = view.findViewById(R.id.txtOD_ReasonTitle);
        txtOD_TypeTitle = view.findViewById(R.id.txtOD_TypeTitle);
        txtOD_ToDateTitle = view.findViewById(R.id.txtOD_ToDateTitle);
        txtOD_FromDateTitle = view.findViewById(R.id.txtOD_FromDateTitle);

        String str1 = "<font color='#EE0000'>*</font>";

        txtOD_ReasonTitle.setText(Html.fromHtml("Reason " + str1));
        txtOD_TypeTitle.setText(Html.fromHtml("OD Type " + str1));
        txtOD_ToDateTitle.setText(Html.fromHtml("To Date " + str1));
        txtOD_FromDateTitle.setText(Html.fromHtml("From Date " + str1));

        fullDay_on = view.findViewById(R.id.fullDay_on);
        fullDay_off = view.findViewById(R.id.fullDay_off);
        halfDay_on = view.findViewById(R.id.halfDay_on);
        halfDay_off = view.findViewById(R.id.halfDay_off);
        firstHalf_on = view.findViewById(R.id.first_half_on);
        firstHalf_off = view.findViewById(R.id.first_half_off);
        secondHalf_on = view.findViewById(R.id.second_half_on);
        secondHalf_off = view.findViewById(R.id.second_half_off);

        firstHalf_on.setEnabled(false);
        secondHalf_on.setEnabled(false);
        firstHalf_off.setEnabled(false);
        secondHalf_off.setEnabled(false);

        firstHalf_on.setTextColor(Color.parseColor("#DCDADE"));
        secondHalf_on.setTextColor(Color.parseColor("#DCDADE"));
        firstHalf_off.setTextColor(Color.parseColor("#DCDADE"));
        secondHalf_off.setTextColor(Color.parseColor("#DCDADE"));

        fullDay_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstHalf_on.setEnabled(false);
                secondHalf_on.setEnabled(false);
                firstHalf_off.setEnabled(false);
                secondHalf_off.setEnabled(false);

                firstHalf_on.setTextColor(Color.parseColor("#DCDADE"));
                secondHalf_on.setTextColor(Color.parseColor("#DCDADE"));
                firstHalf_off.setTextColor(Color.parseColor("#DCDADE"));
                secondHalf_off.setTextColor(Color.parseColor("#DCDADE"));

                fullDay_on.setVisibility(View.VISIBLE);
                fullDay_off.setVisibility(View.INVISIBLE);
                halfDay_on.setVisibility(View.INVISIBLE);
                halfDay_off.setVisibility(View.VISIBLE);
                firstHalf_on.setVisibility(View.INVISIBLE);
                firstHalf_off.setVisibility(View.VISIBLE);
                secondHalf_on.setVisibility(View.INVISIBLE);
                secondHalf_off.setVisibility(View.VISIBLE);

                odStatusPosition = "" + "1";
                odsubstatusPosition = "" + "0";

            }
        });

        halfDay_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstHalf_on.setEnabled(true);
                secondHalf_on.setEnabled(true);
                firstHalf_off.setEnabled(true);
                secondHalf_off.setEnabled(true);

                firstHalf_on.setTextColor(Color.parseColor("#404040"));
                secondHalf_on.setTextColor(Color.parseColor("#404040"));
                firstHalf_off.setTextColor(Color.parseColor("#404040"));
                secondHalf_off.setTextColor(Color.parseColor("#404040"));

                fullDay_on.setVisibility(View.INVISIBLE);
                fullDay_off.setVisibility(View.VISIBLE);
                halfDay_on.setVisibility(View.VISIBLE);
                halfDay_off.setVisibility(View.INVISIBLE);
                firstHalf_on.setVisibility(View.VISIBLE);
                firstHalf_off.setVisibility(View.INVISIBLE);

                odStatusPosition = "" + "2";
                odsubstatusPosition = "" + "1";

            }
        });

        firstHalf_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstHalf_on.setEnabled(true);
                secondHalf_on.setEnabled(true);
                firstHalf_off.setEnabled(true);
                secondHalf_off.setEnabled(true);

                firstHalf_on.setVisibility(View.VISIBLE);
                firstHalf_off.setVisibility(View.INVISIBLE);
                secondHalf_on.setVisibility(View.INVISIBLE);
                secondHalf_off.setVisibility(View.VISIBLE);
                odsubstatusPosition = "" + "1";

            }
        });

        secondHalf_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstHalf_on.setEnabled(true);
                secondHalf_on.setEnabled(true);
                firstHalf_off.setEnabled(true);
                secondHalf_off.setEnabled(true);

                firstHalf_on.setVisibility(View.INVISIBLE);
                firstHalf_off.setVisibility(View.VISIBLE);
                secondHalf_on.setVisibility(View.VISIBLE);
                secondHalf_off.setVisibility(View.INVISIBLE);

                odsubstatusPosition = "" + "2";
            }
        });


//        String[] str_reason = {"Please Select","Client Meeting","On Site","Work From Home","Other"};
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,str_reason);
//        spin_reason_od.setAdapter(arrayAdapter);

        spin_reason_od.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 0) {
                        positionValue = "";
                        positionId = "";
                    } else if (position > 0) {
                        positionId = arlRequestStatusData.get(position - 1).get("KEY");
                        positionValue = arlRequestStatusData.get(position - 1).get("VALUE");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_od_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_od_from_date.setText("");
                calanderHRMS.datePicker(btn_od_from_date);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = btn_od_from_date.getText().toString().trim();
                            TO_DATE = btn_od_to_date.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE);
                            } else {
                                handler.postDelayed(rRunnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GetFuture Method ", e.getMessage());
                        }
                    }
                };
                handler.postDelayed(rRunnable, 1000);
            }
        });
        btn_od_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_od_to_date.setText("");
                calanderHRMS.datePicker(btn_od_to_date);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = btn_od_from_date.getText().toString().trim();
                            TO_DATE = btn_od_to_date.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE);
                            } else {
                                handler.postDelayed(rRunnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GetFuture Method ", e.getMessage());
                        }
                    }
                };
                handler.postDelayed(rRunnable, 1000);
            }
        });

        btn_od_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.timePicker(btn_od_from_time);
            }
        });

        btn_od_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.timePicker(btn_od_to_time);
            }
        });

        btnOdRequestSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fromDate = "", toDate = "", fromTime = "", toTime = "", edtreason = "", odtype = "", od_status = "", odsubstatus = "";

                fromDate = btn_od_from_date.getText().toString().trim().replace("/", "-");
                toDate = btn_od_to_date.getText().toString().trim().replace("/", "-");
                fromTime = btn_od_from_time.getText().toString().trim().replace(":", "-").replace(" ", "_");
                toTime = btn_od_to_time.getText().toString().trim().replace(":", "-").replace(" ", "_");
                edtreason = edt_od_reason.getText().toString().trim();
                odtype = positionId;
                od_status = odStatusPosition;
                odsubstatus = odsubstatusPosition;

                Log.v("Radio Button", "OD Status " + od_status + "   OD Sub Status " + odsubstatus);
                System.out.print("OD Status " + od_status + "   OD Sub Status " + odsubstatus);

                if (fromDate.equals("")) {
                    Log.v("From Date", "Error1");
                    Utilities.showDialog(coordinatorLayout, "Please Enter From Date.");
                } else if (toDate.equals("")) {
                    Log.v("To Date", "Error2");
                    Utilities.showDialog(coordinatorLayout, "Please Enter To Date.");

                } else if (odtype.equals("")) {
                    Log.v("OD Type", "Error3");
                    Utilities.showDialog(coordinatorLayout, "Please Enter OD Type.");

                } else if (edtreason.equals("")) {
                    Log.v("Reaon", "Error4");
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason.");

                } else {
                    if (Utilities.isNetworkAvailable(getActivity())) {
//                        odRequestFragmentAsync = new ODRequestFragment.ODRequestFragmentAsync();
//                        odRequestFragmentAsync.execute(fromDate, toDate, fromTime, toTime, odtype, od_status, odsubstatus, edtreason);
//
//                        btn_od_from_date.setHint("From Date");
//                        btn_od_to_date.setHint("To Date");
//                        btn_od_from_time.setHint("From Time");
//                        btn_od_to_time.setHint("To Time");
//                        edt_od_reason.setHint("Reason");
                        Send_OD_Request(employeeId, "0", fromDate, toDate, od_status, odsubstatus, fromTime, toTime, odtype, edtreason);

                    } else {

                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }
                }


            }
        });

        return view;
    }

    private void odRequest(String fromDate, String toDate, String fromTime, String toTime, String edtreason,
                           String odstatus, String odsubstatus, String odtype) {


        System.out.print("\n\nInside OD request Click  : OD type = " + odtype + " OD Status = " + odstatus + " OD Sub Status =  " + odsubstatus
                + "\n\nFrom Time = " + fromTime + " To time = " + toTime + "\n\nFrom Date = " + fromDate + " To date = " + toDate + " Reason = " + edtreason);

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

//                odRequestFragmentAsync = new ODRequestFragment.ODRequestFragmentAsync();
//                odRequestFragmentAsync.from_date = fromDate;
//                odRequestFragmentAsync.to_date = toDate;
//                odRequestFragmentAsync.from_time = fromTime;
//                odRequestFragmentAsync.to_time = toTime;
//                odRequestFragmentAsync.edtreason = edtreason;
//                odRequestFragmentAsync.odstatus = ""+odstatus;
//                odRequestFragmentAsync.odsubstatus = odsubstatus;
//                odRequestFragmentAsync.odtype = odtype;
//
//                odRequestFragmentAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
    }

    public void Send_OD_Request(String emp_id, String req_id, String fromDate, String toDate, String od_type,
                                String od_sub_type, String fromTime, String toTime, String odType, String reason) {
        try {
            if (toTime.equals("")) {
                toTime = "-";
            }
            if (fromTime.equals("")) {
                fromTime = "-";
            }
            JSONObject param = new JSONObject();

            param.put("EMPLOYEE_ID", emp_id);
            param.put("REQUEST_ID", req_id);
            param.put("FROM_DATE", fromDate);
            param.put("TO_DATE", toDate);
            param.put("OD_REQUEST_TYPE", od_type);
            param.put("OD_REQUEST_SUB_TYPE", od_sub_type);
            param.put("FROM_TIME", fromTime);
            param.put("TO_TIME", toTime);
            param.put("OD_TYPE", odType);
            param.put("REASON", reason);
            Log.e("RequestJson", param.toString());

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendOdRequestPost";
            Log.e("Send url", url);

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                    response -> {
                        try {
                            String result = response.getString("SendODRequestPostResult");
                            int res = Integer.parseInt(result);
                            if (res > 0) {
                                btn_od_from_date.setText("");
                                btn_od_to_date.setText("");
                                btn_od_from_time.setText("");
                                btn_od_to_time.setText("");
                                spin_reason_od.setSelection(0);
                                edt_od_reason.setText("");
                                Utilities.showDialog(coordinatorLayout, "On Duty request Send successfully.");
                            } else if (res == -1) {
                                Utilities.showDialog(coordinatorLayout, "On Duty request on the same date and same type already exists.");
                            } else if (res == -2) {
                                Utilities.showDialog(coordinatorLayout, "On Duty request for previous payroll cycle not allowed.");
                            } else if (res == -3) {
                                Utilities.showDialog(coordinatorLayout, "OD/AR/Leave on Same Date Already Requested.");
                            } else if (res == 0) {
                                Utilities.showDialog(coordinatorLayout, "Error during sending On Duty Request.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Utilities.showDialog(coordinatorLayout, ex.getMessage());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, Utilities.handleVolleyError(error));
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

            int socketTime = 30000;
            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(retryPolicy);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ODRequestFragmentAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

//        String from_date,to_date,from_time,to_time;
//        String edtreason,odtype,odstatus="",odsubstatus;


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
            String from_date, to_date, from_time, to_time;
            String edtreason, odtype, od_status, odsubstatus;

            from_date = params[0];
            to_date = params[1];
            from_time = params[2];
            to_time = params[3];
            odtype = params[4];
            od_status = params[5];
            odsubstatus = params[6];
            edtreason = params[7];
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            System.out.print("\n\nDo in background From Time = " + from_date + " To Time=  " + to_date + " From Time= " + from_time + " To Time  " + to_time +
                    "\n\nReason = " + edtreason + " OD type =  " + odtype + " \n\nOd Status  " + od_status + " Od Sub Status  " + odsubstatus);

            if (from_time.equals("")) {
                from_time = "-";
            }
            if (to_time.equals("")) {
                to_time = "-";
            }
            try {

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String OdRequestURL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendODRequest/" + employeeId + "/" + "0" + "/" + from_date + "/" + to_date + "/" + od_status + "/" + odsubstatus + "/" + from_time + "/" + to_time + "/" + odtype + "/" + edtreason;

                //String OdRequestURL = "http://savvyshippingsoftware.com/SavvyMobileNew/SavvyMobileService.svc/SendODRequest/" + empoyeeId + "/" + "0" + "/" + "10-05-2017" + "/" + "11-05-2017" + "/" + "1" + "/" + "0" + "/" + "08-08_AM" + "/" + "09-08_AM" + "/" + "1" + "/" + "ujhgha";

                System.out.println("OdRequestURL====" + OdRequestURL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        OdRequestURL, "GET");

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }
                //jsonlist.add(map);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();

                result = result.replaceAll("^\"+|\"+$", " ").trim();
                try {

                    int res = Integer.valueOf(result);
                    if (res > 0) {
                        Utilities.showDialog(coordinatorLayout, "On Duty request Send successfully.");
                    } else if (res == -1) {
                        Utilities.showDialog(coordinatorLayout, "On Duty request on the same date and same type already exists.");
                    } else if (res == -2) {
                        Utilities.showDialog(coordinatorLayout, "On Duty request for previous payroll cycle not allowed.");
                    } else if (res == -3) {
                        Utilities.showDialog(coordinatorLayout, "OD/AR/Leave on Same Date Already Requested.");
                    } else if (res == 0) {
                        Utilities.showDialog(coordinatorLayout, "Error during sending On Duty Request.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

//               if(result.equals("0")) {
//                        Utilities.showDialog(coordinatorLayout, "On Duty request send successfully.");
//                    }else if(result.equals("-1")){
//                        Utilities.showDialog(coordinatorLayout,"On Duty request on the same date and same type already exists.");
//                    }else if(result.equals("-2")) {
//                        Utilities.showDialog(coordinatorLayout,"On Duty request for previous payroll cycle not allowed.");
//                    }else if(result.equals(" -3 ")) {
//                        System.out.print("This is the output "+result);
//                       Utilities.showDialog(coordinatorLayout,"Request already applied in the same date.");
//                    }else if(result==" -3 "){
//                        System.out.print("This is the Another output "+result);
//                        Utilities.showDialog(coordinatorLayout,"Error in -3");
//                    }
//
//
                btn_od_from_date.setText("");
                btn_od_to_date.setText("");
                btn_od_from_time.setText("");
                btn_od_to_time.setText("");
                edt_od_reason.setText("");
            }
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


    }

    public void getCompareTodayDate(String From_date, String To_date) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String From_Date = From_date;
                String To_Date = To_date;

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("From_Date", From_Date);
                params_final.put("To_Date", To_Date);


//            pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        txt_compareDate.setText("From Date should be less than or equal To Date!");
                                        linear_compareDate.setVisibility(View.VISIBLE);
                                    } else {
                                        linear_compareDate.setVisibility(View.GONE);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("Error", "" + error.getMessage());
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

    private class GetOvertimeReason extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetODTypeMaster/1";

                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        GETREQUESTSTATUS_URL, "GET");

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
                    HashMap<String, String> requestStatusmap;
                    // ArrayList<String> requestArray;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    //requestArray=new ArrayList<String>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            requestStatusmap = new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            String key = explrObject.getString("ODMasterId");
                            String value = explrObject.getString("ODType");
                            // requestArray.add(value);
                            requestStatusmap.put("KEY", key);
                            requestStatusmap.put("VALUE", value);

                            arlRequestStatusData.add(requestStatusmap);
                        }
                        System.out.println("Array===" + arlRequestStatusData);

                        customspinnerAdapter = new CustomSpinnerAdapter(getActivity(), arlRequestStatusData);
                        spin_reason_od.setAdapter(customspinnerAdapter);

                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        System.out.println("Data not getting on server side");
                    }
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
