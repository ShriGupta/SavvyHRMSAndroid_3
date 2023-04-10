package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.savvy.hrmsnewapp.adapter.WorkFromHomeStatusAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class WorkFromHomeMMTRequestFrgament extends BaseFragment implements View.OnClickListener {

    WorkFromHomeMMTRequestFrgament.WFHSpinnerAsyncTask wfhSpinnerAsyncTask;
    WorkFromHomeMMTRequestFrgament.WFHStatusAsync wfhStatusAsync;
    WorkFromHomeStatusAdapter workFromHomeStatusAdapter;
    CoordinatorLayout coordinatorLayout;
    Button request_FromDate, request_ToDate, request_FromTime, request_ToTime, request_SendButton, request_ResetButton;
    LinearLayout requestCompareLayout;
    CustomTextView requestCompareDate;
    EditText reason;

    CalanderHRMS calanderHRMS;
    Handler handler;
    Runnable rRunnable;
    String FROM_DATE = "", TO_DATE = "";

    Button status_FromDate, status_ToDate, status_SpinButton, status_SearchButton;
    LinearLayout statusCompareLayout;
    CustomTextView statusCompareDate, dataNotFound;
    RecyclerView recyclerView;

    ProgressDialog progressDialog;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    ArrayList<HashMap<String, String>> arlData, arrListRequestStatusData;
    ArrayList<String> spinArrayID, spinArray;
    HashMap<String, String> mapData;
    String token = "";
    String json = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinArrayID = new ArrayList<String>();
        spinArray = new ArrayList<String>();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        employeeId = (shared.getString("EmpoyeeId", ""));

        getPolicyIDWFH(employeeId);
        getStatusList();
        getWFHStatusResult(employeeId, token, "-", "-", "0,1,2,3,4");
    }

    private void getWFHStatusResult(String employeeId, String token, String fromdate, String todate, String spinId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<>();
                wfhStatusAsync = new WorkFromHomeMMTRequestFrgament.WFHStatusAsync();
                wfhStatusAsync.empid = employeeId;
                wfhStatusAsync.fromdate = fromdate;
                wfhStatusAsync.todate = todate;
                wfhStatusAsync.spnid = spinId;
                wfhStatusAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class WFHStatusAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid, fromdate, todate, spnid;
        String token;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                final String OPESTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetWorkFromHomeStatusMMT/" + empid + "/" + fromdate + "/" + todate + "/" + spnid;
                System.out.println("ATTENDANCE_URL====" + OPESTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity());
                json = jParser.makeHttpRequest(OPESTATUS_URL, "GET");
                return json;
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        HashMap<String, String> opeStatusmap;
                        JSONArray jsonArray = new JSONArray(result);

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                opeStatusmap = new HashMap<>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);

                                opeStatusmap.put("TOKEN_NO", explrObject.getString("TOKEN_NO"));
                                opeStatusmap.put("FROM_DATE", explrObject.getString("FROM_DATE"));
                                opeStatusmap.put("TO_DATE", explrObject.getString("TO_DATE"));
                                opeStatusmap.put("WFH_REASON", explrObject.getString("WFH_REASON"));
                                opeStatusmap.put("REQUEST_STATUS", explrObject.getString("REQUEST_STATUS"));
                                opeStatusmap.put("ACTION_BY", explrObject.getString("ACTION_BY"));
                                opeStatusmap.put("ACTION_DATE", explrObject.getString("ACTION_DATE"));

                                arlData.add(opeStatusmap);
                            }

                            recyclerView.setVisibility(View.VISIBLE);
                            dataNotFound.setVisibility(View.GONE);
                            workFromHomeStatusAdapter = new WorkFromHomeStatusAdapter(getActivity(), arlData);
                            recyclerView.setAdapter(workFromHomeStatusAdapter);

                        } else {
                            recyclerView.setAdapter(null);
                            recyclerView.setVisibility(View.GONE);
                            dataNotFound.setVisibility(View.VISIBLE);
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    private void getPolicyIDWFH(String employeeId) {
        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final String POLICY_REQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyPolicyIdWorkFromHomePostMMT";
            Log.d(TAG, "sendWFHRequest: " + POLICY_REQUEST_URL);
            JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();
            try {
                params_final.put("employeeId", employeeId);
                pm.put("", params_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, POLICY_REQUEST_URL, params_final, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        int value = Integer.valueOf(response.getString("GetMyPolicyIdWorkFromHomePostMMTResult"));
                        if (value <= 0) {
                            Utilities.showDialog(coordinatorLayout, "You are not authourise for Work From Home Request");
                            request_SendButton.setEnabled(false);
                        }
                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getStatusList() {

        arrListRequestStatusData = new ArrayList();
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                wfhSpinnerAsyncTask = new WorkFromHomeMMTRequestFrgament.WFHSpinnerAsyncTask();
                wfhSpinnerAsyncTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class WFHSpinnerAsyncTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetRequestStatus";
                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(GETREQUESTSTATUS_URL, "GET");

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
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        HashMap<String, String> requestStatusmap;
                        JSONArray jsonArray = new JSONArray(result);
                        System.out.println("jsonArray===" + jsonArray);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                requestStatusmap = new HashMap<>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String key = explrObject.getString("KEY");
                                String value = explrObject.getString("VALUE");
                                requestStatusmap.put("KEY", key);
                                requestStatusmap.put("VALUE", value);
                                arrListRequestStatusData.add(requestStatusmap);
                            }
                            System.out.println("Array===" + arrListRequestStatusData);
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_from_home_request, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        //request
        request_FromDate = getActivity().findViewById(R.id.wfhRequest_fromDate);
        request_ToDate = getActivity().findViewById(R.id.wfhRequest_toDate);
        request_FromTime = getActivity().findViewById(R.id.wfhRequest_fromTime);
        request_ToTime = getActivity().findViewById(R.id.wfhRequest_toTime);
        request_SendButton = getActivity().findViewById(R.id.whfRequestSendButton);
        request_ResetButton = getActivity().findViewById(R.id.whfRequestResetButton);
        requestCompareLayout = getActivity().findViewById(R.id.whfRequestLinearLayout);
        requestCompareDate = getActivity().findViewById(R.id.whfRequestCompareDateTextView);
        reason = getActivity().findViewById(R.id.wfhRequest_Reason);
        //status
        status_FromDate = getActivity().findViewById(R.id.wfhStatus_fromDate);
        status_ToDate = getActivity().findViewById(R.id.wfhStatus_toDate);
        status_SpinButton = getActivity().findViewById(R.id.wfhStatus_SpinButton);
        status_SearchButton = getActivity().findViewById(R.id.wfhStatus_SearchButton);
        statusCompareLayout = getActivity().findViewById(R.id.wfhStatus_LinearLayout);
        statusCompareDate = getActivity().findViewById(R.id.wfhStatus_compareDateTextView);
        dataNotFound = getActivity().findViewById(R.id.txt_wfhStatus_DataNotFount);
        recyclerView = getActivity().findViewById(R.id.wfhStatus_RecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (spinArrayID.size() == 0) {
            status_SpinButton.setText("Pending,Inprocess");
        }

        request_FromDate.setOnClickListener(this);
        request_ToDate.setOnClickListener(this);
        request_FromTime.setOnClickListener(this);
        request_ToTime.setOnClickListener(this);
        request_SendButton.setOnClickListener(this);
        request_ResetButton.setOnClickListener(this);

        status_FromDate.setOnClickListener(this);
        status_ToDate.setOnClickListener(this);
        status_SpinButton.setOnClickListener(this);
        status_SearchButton.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.wfhRequest_fromDate:
                request_FromDate.setText("");
                calanderHRMS.datePicker(request_FromDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = request_FromDate.getText().toString().trim();
                            TO_DATE = request_ToDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE, "REQUEST");
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
                break;

            case R.id.wfhRequest_toDate:

                request_ToDate.setText("");
                calanderHRMS.datePicker(request_ToDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = request_FromDate.getText().toString().trim();
                            TO_DATE = request_ToDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE, "REQUEST");
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
                break;

            case R.id.wfhRequest_fromTime:
                calanderHRMS.timePicker(request_FromTime);
                break;

            case R.id.wfhRequest_toTime:
                calanderHRMS.timePicker(request_ToTime);
                break;

            case R.id.whfRequestSendButton:
                if (request_FromDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select From Date");
                } else if (request_ToDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select To Date");
                } else if (request_FromTime.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select From Time");
                } else if (request_ToTime.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select To Time");
                } else if (reason.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                } else {
                    sendWFHRequest(employeeId, "0", request_FromDate.getText().toString().replace("/", "-"),
                            request_ToDate.getText().toString().replace("/", "-"),
                            request_FromTime.getText().toString(),
                            request_ToTime.getText().toString(),
                            reason.getText().toString().trim().replaceAll("\\s", "_"));
                }

                break;
            case R.id.whfRequestResetButton:
                removeData();
                break;
            //status

            case R.id.wfhStatus_fromDate:
                status_FromDate.setText("");
                calanderHRMS.datePicker(status_FromDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = status_FromDate.getText().toString().trim();
                            TO_DATE = status_ToDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE, "");
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
                break;
            case R.id.wfhStatus_toDate:
                status_ToDate.setText("");
                calanderHRMS.datePicker(status_ToDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = status_FromDate.getText().toString().trim();
                            TO_DATE = status_ToDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE, "");
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
                break;
            case R.id.wfhStatus_SpinButton:

                final CheckBox[] checkBox = new CheckBox[arrListRequestStatusData.size()];
                final TextView[] txtView = new TextView[arrListRequestStatusData.size()];
                LinearLayout l1 = new LinearLayout(getActivity());
                l1.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle("Please Select");

                Log.e("ArrayList ", arrListRequestStatusData.toString());
                Log.e("ArrayList ", "" + arrListRequestStatusData.size());
                try {

                    for (int i = 0; i < arrListRequestStatusData.size(); i++) {
                        mapData = arrListRequestStatusData.get(i);

                        checkBox[i] = new CheckBox(getActivity());
                        txtView[i] = new TextView(getActivity());

                        checkBox[i].setTextSize(10);
                        checkBox[i].setPadding(0, 0, 0, 0);
                        checkBox[i].setLines((int) 2.0);
                        checkBox[i].setText(mapData.get("VALUE"));
                        txtView[i].setText(mapData.get("KEY"));
                        l1.addView(checkBox[i]);
                        final int finalI = i;
                        try {

                            checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {

                                        String spin_new_value = checkBox[finalI].getText().toString();
                                        String spin_new_id = txtView[finalI].getText().toString();

                                        if (!(spinArray.contains(spin_new_value))) {
                                            spinArray.add(spin_new_value);
                                            spinArrayID.add(spin_new_id);
                                        }

                                    } else if (!isChecked) {
                                        spinArray.remove(checkBox[finalI].getText().toString());
                                        spinArrayID.remove(txtView[finalI].getText().toString());
                                    }

                                }
                            });

                            for (int k = 0; k < spinArrayID.size(); k++) {
                                int index = Integer.parseInt(spinArrayID.get(k));
                                Log.e("Spin Index", "" + index);
                                for (int j = 0; j < arrListRequestStatusData.size(); j++) {
                                    mapData = arrListRequestStatusData.get(j);

                                    int spin_index = Integer.parseInt(mapData.get("KEY"));

                                    if (index == spin_index) {
                                        checkBox[spin_index].setChecked(true);
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    builder.setView(l1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        status_SpinButton.setText(spinArray.toString().replace("[", "").replace("]", ""));
                    }
                });
                builder.show();
                if (spinArrayID.size() == 0) {
                    checkBox[0].setChecked(true);
                    checkBox[1].setChecked(true);
                    checkBox[2].setChecked(true);
                    checkBox[3].setChecked(true);
                    checkBox[4].setChecked(true);
                }
                break;
            case R.id.wfhStatus_SearchButton:

                try {
                    token = (shared.getString("Token", ""));
                    employeeId = (shared.getString("EmpoyeeId", ""));

                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "0,1,2,3,4";
                    }
                    String gettodate = status_ToDate.getText().toString().replace("/", "-");
                    String getfromdate = status_FromDate.getText().toString().replace("/", "-");
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";
                    if (statusCompareLayout.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getWFHStatusResult(employeeId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void removeData() {
        request_FromDate.setText("");
        request_ToDate.setText("");
        request_FromTime.setText("");
        request_ToTime.setText("");
        reason.setText("");
        getWFHStatusResult(employeeId, token, "-", "-", "0,1,2,3,4");
    }

    private void sendWFHRequest(String employeeid, String requestid, String fromdate, String todate, String logintime, String logouttime, String reason) {
        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String SEND_REQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendWorkFromHomeRequestPostMMT";
            Log.d(TAG, "sendWFHRequest: " + SEND_REQUEST_URL);
            JSONObject params_final = new JSONObject();

            try {
                params_final.put("EMPLOYEE_ID", employeeid);
                params_final.put("REQUEST_ID", requestid);
                params_final.put("FROM_DATE", fromdate);
                params_final.put("TO_DATE", todate);
                params_final.put("FROM_TIME", logintime);
                params_final.put("TO_TIME", logouttime);
                params_final.put("REASON", reason);
            } catch (Exception e) {
                e.printStackTrace();
            }


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SEND_REQUEST_URL, params_final, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        int value = Integer.valueOf(response.getString("SendWorkFromHomeRequestPostMMTResult"));

                        if (value == -1) {
                            Utilities.showDialog(coordinatorLayout, "WFH request on the same date and same type already exists.");
                        } else if (value == -2) {
                            Utilities.showDialog(coordinatorLayout, "WFH request for previous payroll cycle not allowed.");
                        } else if (value == -3) {
                            Utilities.showDialog(coordinatorLayout, "WFH can be applied for max. 03 days at a stretch.");
                        } else if (value == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error during sending WFH Request.");
                        } else if (value > 0) {
                            Utilities.showDialog(coordinatorLayout, "WFH request send successfully");
                            removeData();
                        }

                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    public void getCompareTodayDate(String From_date, String To_date, final String value) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String From_Date = From_date;
                String To_Date = To_date;

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("From_Date", From_Date);
                params_final.put("To_Date", To_Date);
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
                                        if (value.equals("REQUEST")) {
                                            requestCompareDate.setText("From Date should be less than or equal To Date!");
                                            requestCompareLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            statusCompareDate.setText("From Date should be less than or equal To Date!");
                                            statusCompareLayout.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        requestCompareLayout.setVisibility(View.GONE);
                                        statusCompareLayout.setVisibility(View.GONE);
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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
