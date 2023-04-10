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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.TravelExpenseApprovalAdapter;
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

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class TravelExpenseApprovalFicciFragment extends BaseFragment implements View.OnClickListener {


    TravelExpenseApprovalAdapter approvalAdapter;
    CoordinatorLayout coordinatorLayout;
    CustomTextView dataNotFound;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> arrayList;
    Button approveButton, rejectButton;
    EditText edtComment;
    LinearLayout expenseLinearLayout;
    ArrayList<HashMap<String, String>> arrListRequestStatusData;
    TravelExpenseApprovalFicciFragment.ExpenseApprovalSpinnerTask expenseApprovalSpinnerTask;
    Button exp_fromDate, exp_toDate, exp_SpinButton, exp_searchButton;
    CalanderHRMS calanderHRMS;

    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    CustomTextView compareDateTextView;
    LinearLayout compareDateLayout;
    HashMap<String, String> mapData;

    String token = "";
    ArrayList<String> spinArrayID;
    ArrayList<String> spinArray;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinArrayID = new ArrayList();
        spinArray = new ArrayList();
        getStatusList();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        getTravelExpenseData(employeeId, token, "-", "-", "0");

    }

    private void getStatusList() {
        arrListRequestStatusData = new ArrayList();
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                expenseApprovalSpinnerTask = new TravelExpenseApprovalFicciFragment.ExpenseApprovalSpinnerTask();
                expenseApprovalSpinnerTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ExpenseApprovalSpinnerTask extends AsyncTask<String, String, String> {
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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetActionStatusForApproval";
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

    private void getTravelExpenseData(String employeeID, String token, String fromdate, String todate, String spinid) {

        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            final String GET_EXPENSE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelVoucherRequestDetailFicciwithStatusDateRange/" + employeeId + "/" + fromdate + "/" + todate + "/" + spinid;
            Log.d(TAG, "getTravelRequestData: " + GET_EXPENSE_URL);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_EXPENSE_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        arrayList = new ArrayList<>();
                        HashMap<String, String> mapdata;
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                mapdata = new HashMap<>();
                                mapdata.put("REQUEST_ID", response.getJSONObject(i).getString("ERFS_REQUEST_ID"));
                                mapdata.put("REQUEST_FLOW_STATUS_ID", response.getJSONObject(i).getString("REQUEST_STATUS_ID"));
                                mapdata.put("ACTION_LEVEL_SEQUENCE", response.getJSONObject(i).getString("ERFS_ACTION_LEVEL_SEQUENCE"));
                                mapdata.put("MAX_ACTION_LEVEL_SEQUENCE", response.getJSONObject(i).getString("MAX_ACTION_LEVEL_SEQUENCE"));
                                mapdata.put("TRAVEL_STATUS", response.getJSONObject(i).getString("TRAVEL_VOUCHER_STATUS"));
                                mapdata.put("EMPLOYEE_ID", response.getJSONObject(i).getString("TVR_EMPLOYEE_ID"));
                                mapdata.put("ERFS_REQUEST_FLOW_ID", response.getJSONObject(i).getString("ERFS_REQUEST_FLOW_ID"));

                                mapdata.put("TOKEN_NO", response.getJSONObject(i).getString("TOKEN_NO"));
                                mapdata.put("EMPLOYEE_CODE", response.getJSONObject(i).getString("EMPLOYEE_CODE"));
                                mapdata.put("EMPLOYEE_NAME", response.getJSONObject(i).getString("EMPLOYEE_NAME"));
                                mapdata.put("TVR_VOUCHER_NO", response.getJSONObject(i).getString("TVR_VOUCHER_NO"));
                                mapdata.put("REQUESTED_DATE", response.getJSONObject(i).getString("REQUESTED_DATE"));
                                mapdata.put("TVR_REQUESTED_AMOUNT", response.getJSONObject(i).getString("TVR_REQUESTED_AMOUNT"));
                                mapdata.put("TVR_APPROVED_AMOUNT", response.getJSONObject(i).getString("TVR_APPROVED_AMOUNT"));
                                mapdata.put("R_TYPE", response.getJSONObject(i).getString("R_TYPE"));
                                mapdata.put("ERFS_REQUEST_STATUS_NAME", response.getJSONObject(i).getString("ERFS_REQUEST_STATUS_NAME"));
                                mapdata.put("TRAVEL_VOUCHER_STATUS", response.getJSONObject(i).getString("TRAVEL_VOUCHER_STATUS"));
                                mapdata.put("ERFS_REQUEST_STATUS", response.getJSONObject(i).getString("ERFS_REQUEST_STATUS"));


                                arrayList.add(mapdata);
                            }
                            approvalAdapter = new TravelExpenseApprovalAdapter(getActivity(), arrayList);
                            recyclerView.setAdapter(approvalAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            expenseLinearLayout.setVisibility(View.VISIBLE);
                            dataNotFound.setVisibility(View.GONE);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            recyclerView.setAdapter(null);
                            recyclerView.setVisibility(View.INVISIBLE);
                            expenseLinearLayout.setVisibility(View.INVISIBLE);
                            dataNotFound.setVisibility(View.VISIBLE);
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
                    expenseLinearLayout.setVisibility(View.INVISIBLE);
                    dataNotFound.setVisibility(View.VISIBLE);
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
            jsonArrayRequest.setRetryPolicy(policy);
            requestQueue.add(jsonArrayRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_expense_approval_ficci, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        calanderHRMS = new CalanderHRMS(getActivity());
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        dataNotFound = getActivity().findViewById(R.id.travelExpenseDataNotFound);
        recyclerView = getActivity().findViewById(R.id.travelExpenseRecyclerView);
        approveButton = getActivity().findViewById(R.id.expenseapproveButton);
        rejectButton = getActivity().findViewById(R.id.expenserejectButton);
        edtComment = getActivity().findViewById(R.id.travelExpenseApprovalComment);
        expenseLinearLayout = getActivity().findViewById(R.id.expenseLinearLayout);

        exp_fromDate = getActivity().findViewById(R.id.btn_expApprovalFromDate);
        exp_toDate = getActivity().findViewById(R.id.btn_expApprovalToDate);
        exp_SpinButton = getActivity().findViewById(R.id.btn_spin_expenseApproval);
        exp_searchButton = getActivity().findViewById(R.id.btn_expenseApproval_search);

        if (spinArrayID.size() == 0) {
            exp_SpinButton.setText("Pending");
        }

        compareDateTextView = getActivity().findViewById(R.id.expenseApproval_CompareDateTextView);
        compareDateLayout = getActivity().findViewById(R.id.expenseApproval_CompareDateLayout);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        exp_fromDate.setOnClickListener(this);
        exp_toDate.setOnClickListener(this);
        exp_SpinButton.setOnClickListener(this);
        exp_searchButton.setOnClickListener(this);

        approveButton.setOnClickListener(this);
        rejectButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String xmldata;
        switch (v.getId()) {
            case R.id.btn_expApprovalFromDate:
                exp_fromDate.setText("");
                calanderHRMS.datePicker(exp_fromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = exp_fromDate.getText().toString().trim();
                            String ToDate = exp_toDate.getText().toString().trim();

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
            case R.id.btn_expApprovalToDate:
                calanderHRMS.datePicker(exp_toDate);
                handler1 = new Handler();
                runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = exp_fromDate.getText().toString().trim();
                            String ToDate = exp_toDate.getText().toString().trim();

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
            case R.id.btn_spin_expenseApproval:


                try {
                    final CheckBox[] checkBox = new CheckBox[arrListRequestStatusData.size()];
                    final TextView[] txtView = new TextView[arrListRequestStatusData.size()];
                    LinearLayout l1 = new LinearLayout(getActivity());
                    l1.setOrientation(LinearLayout.VERTICAL);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle("Please Select");

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

                                        } else {
                                            spinArray.remove(checkBox[finalI].getText().toString());
                                            spinArrayID.remove(txtView[finalI].getText().toString());
                                        }

                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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
                        builder.setView(l1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            exp_SpinButton.setText(spinArray.toString().replace("[", "").replace("]", ""));
                        }
                    });
                    builder.show();
                    if (spinArrayID.size() == 0) {
                        checkBox[0].setChecked(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }

                break;
            case R.id.btn_expenseApproval_search:
                try {
                    token = (shared.getString("Token", ""));

                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "0";
                    }
                    String gettodate = exp_fromDate.getText().toString().replace("/", "-");
                    String getfromdate = exp_toDate.getText().toString().replace("/", "-");
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";
                    if (compareDateLayout.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getTravelExpenseData(employeeId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case R.id.expenseapproveButton:

                xmldata = approvalAdapter.getTravelApprovalXMLData();
                if (xmldata.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select any Checkbox ");
                } else if (edtComment.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter comment ");
                } else {
                    approveRequest(true, edtComment.getText().toString().trim().replaceAll("\\s", "_"), xmldata);
                }

                break;
            case R.id.expenserejectButton:
                xmldata = approvalAdapter.getTravelApprovalXMLData();
                if (xmldata.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select any Checkbox ");
                } else if (edtComment.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter comment ");
                } else {
                    approveRequest(false, edtComment.getText().toString().trim().replaceAll("\\s", "_"), xmldata);
                }
                break;
        }
    }

    private void approveRequest(final boolean status, final String comment, String xmldata) {
        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("sending request..");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            final String APPROVE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ProcessTravelVoucherRequestFicci/" + employeeId + "/" + status + "/" + comment + "/" + xmldata;
            Log.d(TAG, "getTravelRequestData: " + APPROVE_URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, APPROVE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    int result = Integer.valueOf(response);
                    if (status) {
                        if (result > 0) {
                            Utilities.showDialog(coordinatorLayout, "Travel Voucher Request Processed Sucessfuly");
                            getTravelExpenseData(employeeId, token, "-", "-", "0");
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Error During Travel Voucher Request Proccessing!");
                        }
                    } else {
                        if (result > 0) {
                            Utilities.showDialog(coordinatorLayout, "Travel Voucher Request Proccessed Successfuly");
                            getTravelExpenseData(employeeId, token, "-", "-", "0");
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Error During Travel Voucher Request Proccessing!");
                        }
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
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    public void getCompareDate(String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);


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
                                        compareDateTextView.setText("From Date should be less than or equal To Date!");
                                        compareDateLayout.setVisibility(View.VISIBLE);
                                        exp_toDate.setText("");
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

}
