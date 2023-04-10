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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CompOffStatusRequestMMTAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class CompOffAccrualRequestMMTFragment extends BaseFragment implements View.OnClickListener {

    CompOffStatusRequestMMTAdapter compOffStatusRequestMMTAdapter;
    CompOffAccrualRequestMMTFragment.CompOffSpinnerAsyncTask compOffSpinnerAsyncTask;
    CompOffAccrualRequestMMTFragment.CompOffStatusAsync compOffStatusAsync;
    CompOffAccrualRequestMMTFragment.GetMyPolicyCompOffAsync getMyPolicyCompOffAsync;
    private Button selectDate, sendRequest, reset, fromDate, toDate, spinButton, searchButton;
    private EditText compOffReason;
    private CustomTextView txtDataNotFound, compareDate;
    LinearLayout compareDateLayout;
    CoordinatorLayout coordinatorLayout;
    RecyclerView coffStatusRecyclerView;
    CalanderHRMS calanderHRMS;
    public static Handler handler, handler1, handler2;
    public static Runnable runnable, runnable1, runnable2;
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapData;
    ArrayList<HashMap<String, String>> arlData, arrListRequestStatusData;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String token = "";
    String empId = "";
    String json = "";
    String MY_POLICY_ID = "";
    ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinArrayID = new ArrayList<>();
        spinArray = new ArrayList<>();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        try {
            getStatusList();
            token = (shared.getString("Token", ""));
            empId = (shared.getString("EmpoyeeId", ""));
            getMyPolicyData(empId);
            getCompOffStatusResult(empId, token, "-", "-", "0,1,2,3,4");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compoff_accural_request_mmt, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new Handler();
        calanderHRMS = new CalanderHRMS(getActivity());
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        selectDate = getActivity().findViewById(R.id.c_off_SelectDate);
        compOffReason = getActivity().findViewById(R.id.c_off_Reason);
        sendRequest = getActivity().findViewById(R.id.coff_SendRequest);
        reset = getActivity().findViewById(R.id.coffReset);

        fromDate = getActivity().findViewById(R.id.coffFromDate);
        toDate = getActivity().findViewById(R.id.coffToDate);
        spinButton = getActivity().findViewById(R.id.coff_SpinStatusButton);
        searchButton = getActivity().findViewById(R.id.coffSearchButton);

        txtDataNotFound = getActivity().findViewById(R.id.coffTxtDataNotFound);
        compareDate = getActivity().findViewById(R.id.coffCompareDateTextView);
        compareDateLayout = getActivity().findViewById(R.id.coffCompareDateLayout);

        coffStatusRecyclerView = getActivity().findViewById(R.id.coffStatusRecyclerView);
        RecyclerView.LayoutManager statusLayoutManager = new LinearLayoutManager(getActivity());
        coffStatusRecyclerView.setLayoutManager(statusLayoutManager);
        coffStatusRecyclerView.setItemAnimator(new DefaultItemAnimator());

        selectDate.setOnClickListener(this);
        sendRequest.setOnClickListener(this);
        reset.setOnClickListener(this);

        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        spinButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.c_off_SelectDate:
                calanderHRMS.datePicker(selectDate);
                handler2 = new Handler();
                runnable2 = new Runnable() {
                    @Override
                    public void run() {
                        String selectdate = selectDate.getText().toString();
                        if (!selectdate.equals("")) {
                            checkCompOffRequest(empId, selectDate.getText().toString().replaceAll("/", "-"), MY_POLICY_ID);
                        } else {
                            handler.postDelayed(runnable2, 3000);
                        }
                    }
                };
                handler2.postDelayed(runnable2, 3000);

                break;
            case R.id.coff_SendRequest:
                if (selectDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Date");
                } else if (compOffReason.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                } else {
                    String COFF_DATE = selectDate.getText().toString().replace("/", "-");
                    String COMMENT = compOffReason.getText().toString().trim().replace("\\s", "_");
                    sendCompOffRequest(empId, "0", COFF_DATE, "-", "-", "-", "-", "1", MY_POLICY_ID, COMMENT);
                }
                break;
            case R.id.coffReset:

                selectDate.setText("");
                compOffReason.setText("");
                break;

            case R.id.coffFromDate:
                fromDate.setText("");
                calanderHRMS.datePicker(fromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = fromDate.getText().toString().trim();
                            String ToDate = toDate.getText().toString().trim();

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
            case R.id.coffToDate:
                calanderHRMS.datePicker(toDate);
                handler1 = new Handler();
                runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = fromDate.getText().toString().trim();
                            String ToDate = toDate.getText().toString().trim();

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
            case R.id.coff_SpinStatusButton:

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
                        spinButton.setText(spinArray.toString().replace("[", "").replace("]", ""));
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
            case R.id.coffSearchButton:

                try {
                    token = (shared.getString("Token", ""));
                    empId = (shared.getString("EmpoyeeId", ""));

                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "0,1,2,3,4";
                    }
                    String gettodate = toDate.getText().toString().replace("/", "-");
                    String getfromdate = fromDate.getText().toString().replace("/", "-");
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";
                    if (compareDateLayout.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getCompOffStatusResult(empId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
        }
    }

    private void sendCompOffRequest(String EMPLOYEE_ID, String REQUEST_ID, String COFF_DATE, String CO_FROM_DATE, String CO_FROM_TIME, String CO_TO_DATE, String CO_TO_TIME, String REASON, String POLICY_ID, String COMMENTS) {
        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String SENDPOST_REQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendCompOffRequestPostMMT";
            Log.d(TAG, "sendCompOffRequest: " + SENDPOST_REQUEST_URL);
            JSONObject param = new JSONObject();
            try {
                param.put("EMPLOYEE_ID", EMPLOYEE_ID);
                param.put("REQUEST_ID", REQUEST_ID);
                param.put("COFF_DATE", COFF_DATE);
                param.put("CO_FROM_DATE", CO_FROM_DATE);
                param.put("CO_FROM_TIME", CO_FROM_TIME);
                param.put("CO_TO_DATE", CO_TO_DATE);
                param.put("CO_TO_TIME", CO_TO_TIME);
                param.put("REASON", REASON);
                param.put("POLICY_ID", POLICY_ID);
                param.put("COMMENTS", COMMENTS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SENDPOST_REQUEST_URL, param, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        Log.d(TAG, "onResponse: " + response);
                        if (response.length() > 0) {
                            int value = Integer.valueOf(response.getString("SendCompOffRequestPostMMTResult"));
                            if (value == -1) {
                                Utilities.showDialog(coordinatorLayout, "Comp Off Request on the same date and same type already exists.");
                            } else if (value == -2) {
                                Utilities.showDialog(coordinatorLayout, "Comp Off Request for previous payroll cycle not allowed.");
                            } else if (value == 0) {
                                Utilities.showDialog(coordinatorLayout, "Error during sending Comp Off Request.");
                            } else if (value > 0) {
                                Utilities.showDialog(coordinatorLayout, "Comp Off Request sent successfully");
                                getCompOffStatusResult(empId, token, "-", "-", "0,1,2,3,4");
                            }
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
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

    private void checkCompOffRequest(String empId, String selectdate, String my_policy_id) {
        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final String SPINNER_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/CheckCompOffRequestMMT/" + empId + "/" + selectdate + "/" + my_policy_id;
            Log.d(TAG, "getSpinnerData: " + SPINNER_URL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, SPINNER_URL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        Log.d(TAG, "onResponse: " + response);
                        if (response.length() > 0) {

                            JsonObject obj = new JsonParser().parse(String.valueOf(response)).getAsJsonObject();

                            String allow_co = (String.valueOf(obj.get("ALLOW_CO"))).replaceAll("^\"|\"$", "");
                            String co_time_in = (String.valueOf(obj.get("CO_TIME_IN"))).replaceAll("^\"|\"$", "");
                            String co_time_out = (String.valueOf(obj.get("CO_TIME_OUT"))).replaceAll("^\"|\"$", "");

                            if (allow_co.equals("1")) {
                                Utilities.showDialog(coordinatorLayout, "Comp off start time and end time should be between " + co_time_in + " and " + co_time_out);
                                sendRequest.setEnabled(true);
                            } else {
                                Utilities.showDialog(coordinatorLayout, "Compoff request is not allowed for the selected date.");
                                sendRequest.setEnabled(false);
                            }
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
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

    public void getMyPolicyData(String employeeId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                getMyPolicyCompOffAsync = new CompOffAccrualRequestMMTFragment.GetMyPolicyCompOffAsync();
                getMyPolicyCompOffAsync.employeeId = employeeId;
                getMyPolicyCompOffAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetMyPolicyCompOffAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId;

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

                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyPolicyIdCompOff" + "/" + empId;
                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity());
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
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try {
                    result = result.replaceAll("^\"+|\"+$", " ").trim();
                    MY_POLICY_ID = result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getCompareDate(String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);
                Log.e("RequestData", "JSon->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                Log.e("Url", url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        compareDate.setText("From Date should be less than or equal To Date!");
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

    private void getStatusList() {

        arrListRequestStatusData = new ArrayList();
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                compOffSpinnerAsyncTask = new CompOffAccrualRequestMMTFragment.CompOffSpinnerAsyncTask();
                compOffSpinnerAsyncTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CompOffSpinnerAsyncTask extends AsyncTask<String, String, String> {
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

    private void getCompOffStatusResult(String empid, String token, String fromdate, String todate, String spinId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<>();
                compOffStatusAsync = new CompOffAccrualRequestMMTFragment.CompOffStatusAsync();
                compOffStatusAsync.empid = empid;
                compOffStatusAsync.fromdate = fromdate;
                compOffStatusAsync.todate = todate;
                compOffStatusAsync.spnid = spinId;
                compOffStatusAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CompOffStatusAsync extends AsyncTask<String, String, String> {
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
                final String COMPOFF_GET_STATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCompOffRequestStatusMMT/" + empid + "/" + fromdate + "/" + todate + "/" + spnid;
                System.out.println("COMPOFF_GET_STATUS_URL====" + COMPOFF_GET_STATUS_URL);
                JSONParser jParser = new JSONParser(getActivity());
                json = jParser.makeHttpRequest(COMPOFF_GET_STATUS_URL, "GET");
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
                                opeStatusmap.put("COMP_OFF_DATE", explrObject.getString("COMP_OFF_DATE"));
                                opeStatusmap.put("COR_REASON_COMMENT", explrObject.getString("COR_REASON_COMMENT"));
                                opeStatusmap.put("COR_REQUEST_DAYS", explrObject.getString("COR_REQUEST_DAYS"));
                                opeStatusmap.put("COR_APPROVE_DAYS", explrObject.getString("COR_APPROVE_DAYS"));
                                opeStatusmap.put("REQUEST_STATUS", explrObject.getString("REQUEST_STATUS"));
                                opeStatusmap.put("COR_EMPLOYEE_ID", explrObject.getString("COR_EMPLOYEE_ID"));
                                opeStatusmap.put("ERFS_REQUEST_ID", explrObject.getString("ERFS_REQUEST_ID"));

                                arlData.add(opeStatusmap);
                            }
                            txtDataNotFound.setVisibility(View.GONE);
                            compOffStatusRequestMMTAdapter = new CompOffStatusRequestMMTAdapter(getActivity(), arlData);
                            coffStatusRecyclerView.setAdapter(compOffStatusRequestMMTAdapter);
                            coffStatusRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            coffStatusRecyclerView.setAdapter(null);
                            coffStatusRecyclerView.setVisibility(View.GONE);
                            txtDataNotFound.setVisibility(View.VISIBLE);
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
}
