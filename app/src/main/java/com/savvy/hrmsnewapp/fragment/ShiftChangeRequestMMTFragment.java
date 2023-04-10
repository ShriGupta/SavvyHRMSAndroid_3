package com.savvy.hrmsnewapp.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.savvy.hrmsnewapp.adapter.ShiftChangeRequestMMTAdapter;
import com.savvy.hrmsnewapp.adapter.ShiftChangeRequestStatusAdapter;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ShiftChangeRequestMMTFragment extends BaseFragment implements View.OnClickListener {
    ShiftChangeRequestMMTFragment.PullBackAsync pullBackAsync;
    ShiftChangeRequestMMTAdapter shiftChangeRequestMMTAdapter;
    ShiftChangeRequestStatusAdapter shiftChangeRequestStatusAdapter;
    ShiftChangeRequestMMTFragment.SCRSpinnerAsyncTask scrSpinnerAsyncTask;
    ShiftChangeRequestMMTFragment.SCRStatusAsync scrStatusAsync;
    Button selectDate, getDetail, senRequest, reset;
    Button fromDate, toDate, spinButon, searchButoon;
    CalanderHRMS calanderHRMS;
    CoordinatorLayout coordinatorLayout;
    ProgressDialog progressDialog;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    String token = "";
    String json = "";
    ArrayList<HashMap<String, String>> arrayList, spinnerArrayList;
    RecyclerView recyclerView, statusRecyclerView;
    CustomTextView dataNotFound, txtDataNotFound, txtCompareDate;
    LinearLayout scrLinearLayout, scrStatusLinearLayOut;
    EditText remarks;
    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapData;
    ArrayList<HashMap<String, String>> arlData, arrListRequestStatusData;
    CustomTextView txt_punchToken_no;
    Button txt_actionValuePull;
    String  request_Id = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinArrayID = new ArrayList<String>();
        spinArray = new ArrayList<String>();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        try {
            getStatusList();
            token = (shared.getString("Token", ""));
            employeeId = (shared.getString("EmpoyeeId", ""));
            getSCRStatusResult(employeeId, token, "-", "-", "0,1,2,3,4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shift_change_request_mmt, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        calanderHRMS = new CalanderHRMS(getActivity());
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        selectDate = getActivity().findViewById(R.id.scrDate);
        getDetail = getActivity().findViewById(R.id.scrGetDetailsButton);
        dataNotFound = getActivity().findViewById(R.id.scrDataNotFound);
        scrLinearLayout = getActivity().findViewById(R.id.scrLinearLayout);

        remarks = getActivity().findViewById(R.id.scrRemarks);
        senRequest = getActivity().findViewById(R.id.scrSendRequest);
        reset = getActivity().findViewById(R.id.scrReset);

        fromDate = getActivity().findViewById(R.id.scrFromDate);
        toDate = getActivity().findViewById(R.id.scrToDate);
        spinButon = getActivity().findViewById(R.id.scr_SpinStatusButton);
        searchButoon = getActivity().findViewById(R.id.scrSearchButton);
        txtDataNotFound = getActivity().findViewById(R.id.scrTxtDataNotFound);
        scrStatusLinearLayOut = getActivity().findViewById(R.id.scrCompareDateLayout);
        txtCompareDate = getActivity().findViewById(R.id.scrCompareDateTextView);


        if (spinArrayID.size() == 0) {
            spinButon.setText("Pending,Inprocess");
        }
        recyclerView = getActivity().findViewById(R.id.scrRecyclerView);
        statusRecyclerView = getActivity().findViewById(R.id.scrStatusRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager statusLayoutManager = new LinearLayoutManager(getActivity());
        statusRecyclerView.setLayoutManager(statusLayoutManager);
        statusRecyclerView.setItemAnimator(new DefaultItemAnimator());
        selectDate.setOnClickListener(this);
        getDetail.setOnClickListener(this);
        senRequest.setOnClickListener(this);
        reset.setOnClickListener(this);

        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        spinButon.setOnClickListener(this);
        searchButoon.setOnClickListener(this);

        statusRecyclerView.addOnItemTouchListener(new ShiftChangeRequestMMTFragment.RecyclerTouchListener(getActivity(), statusRecyclerView, new ShiftChangeRequestMMTFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_punchToken_no = view.findViewById(R.id.scrStatusTokenNo);
                    txt_actionValuePull = view.findViewById(R.id.scrPullBack);

                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                String token_no = txt_punchToken_no.getText().toString();
                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {

                                    request_Id = mapdata.get("ERFS_REQUEST_ID");
                                    Log.e("Request Id", "" + request_Id);
                                }

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.pullback_dialogbox);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback Shift Change Request.");
                                txt_header.setText("Pull Back Shift Change Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getPullBackStatus(employeeId, request_Id);
                                        dialog.dismiss();
                                    }
                                });
                                btn_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scrDate:
                calanderHRMS.datePicker(selectDate);
                break;
            case R.id.scrGetDetailsButton:
                if (selectDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Date");
                } else {
                    getSpinnerData(employeeId, selectDate.getText().toString().replace("/", "-"));
                }
                break;

            case R.id.scrSendRequest:

                if (remarks.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter remarks");
                } else {
                    String xmldata;
                    xmldata = shiftChangeRequestMMTAdapter.getXMLStringData();

                    List<String> xmlList = Arrays.asList(xmldata.split("#"));
                    xmldata = xmlList.get(0);
                    if (xmlList.get(1).equals("true")) {
                        Utilities.showDialog(coordinatorLayout, "You can not select more than two weekly off in a week.");
                    } else {
                        sendSCRequest(employeeId, xmldata, remarks.getText().toString().replaceAll("\\s", "_"));
                    }

                }
                break;
            case R.id.scrReset:

                scrLinearLayout.setVisibility(View.GONE);
                recyclerView.setAdapter(null);
                recyclerView.setVisibility(View.GONE);
                selectDate.setText("");
                /*getWeekByList(employeeId, selectDate.getText().toString().replace("/", "-"));*/
                remarks.setText("");
                break;

            case R.id.scrFromDate:
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
            case R.id.scrToDate:
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
            case R.id.scr_SpinStatusButton:

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
                        spinButon.setText(spinArray.toString().replace("[", "").replace("]", ""));
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
            case R.id.scrSearchButton:
                try {
                    token = (shared.getString("Token", ""));


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
                    if (scrStatusLinearLayOut.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getSCRStatusResult(employeeId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int monthOfYear, int dayOfMonth) {
            Log.e("onDateSet()", "arg0 = [" + arg0 + "], year = [" + year + "], monthOfYear = [" + monthOfYear + "], dayOfMonth = [" + dayOfMonth + "]");


        }
    };


    private void getSpinnerData(final String employeeId, String selectdate) {
        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final String SPINNER_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeShiftMasterMMT/" + employeeId + "/" + selectdate;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, SPINNER_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressDialog.dismiss();
                    try {

                        HashMap<String, String> mapData;
                        spinnerArrayList = new ArrayList<>();
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                mapData = new HashMap<>();
                                mapData.put("KEY", response.getJSONObject(i).getString("SM_SHIFT_ID"));
                                mapData.put("VALUE", response.getJSONObject(i).getString("SM_SHIFT_NAME"));
                                spinnerArrayList.add(mapData);
                            }

                            getWeekByList(employeeId, selectDate.getText().toString().replace("/", "-"));

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
            jsonArrayRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getWeekByList(String employeeId, String selectdate) {


        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final String WEEKLIST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeRegisterWithShiftMasterMMT/" + employeeId + "/" + selectdate;
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, WEEKLIST_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressDialog.dismiss();
                    try {
                        HashMap<String, String> mapData;
                        arrayList = new ArrayList<>();
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                mapData = new HashMap<>();
                                mapData.put("ATT_DATE", response.getJSONObject(i).getString("ATT_DATE"));
                                mapData.put("DAY_NAME", response.getJSONObject(i).getString("DAY_NAME"));
                                mapData.put("SHIFT_NAME", response.getJSONObject(i).getString("SHIFT_NAME"));
                                mapData.put("SHIFT_ID", response.getJSONObject(i).getString("SHIFT_ID"));
                                arrayList.add(mapData);
                            }

                            shiftChangeRequestMMTAdapter = new ShiftChangeRequestMMTAdapter(getActivity(), arrayList, spinnerArrayList);
                            recyclerView.setAdapter(shiftChangeRequestMMTAdapter);
                            scrLinearLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            recyclerView.setAdapter(null);
                            scrLinearLayout.setVisibility(View.GONE);
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void sendSCRequest(String employeeId, String xmldata, final String reason) {
        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final String SEND_REQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendShiftChangeRequestMMT/" + employeeId + "/" + xmldata + "/" + reason;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, SEND_REQUEST_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {

                        int value = Integer.valueOf(response.replaceAll("^\"|\"$", ""));

                        switch (value) {
                            case -1:
                                Utilities.showDialog(coordinatorLayout, "Shift Change Request on the same date already exists.");
                                break;
                            case -2:
                                Utilities.showDialog(coordinatorLayout, "Shift Change Request for previous payroll cycle not allowed.");
                                break;
                            case 0:
                                Utilities.showDialog(coordinatorLayout, "Error during sending Shift Change Request.");
                                break;
                            default:
                                if (value > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Shift Change Request sent successfully.");
                                    removeData();
                                }
                                break;
                        }
                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void removeData() {
        selectDate.setText("");
        scrLinearLayout.setVisibility(View.GONE);
        remarks.setText("");
        getSCRStatusResult(employeeId, token, "-", "-", "0,1,2,3,4");
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
                                        txtCompareDate.setText("From Date should be less than or equal To Date!");
                                        scrStatusLinearLayOut.setVisibility(View.VISIBLE);
                                    } else {
                                        scrStatusLinearLayOut.setVisibility(View.GONE);
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

    private void getStatusList() {

        arrListRequestStatusData = new ArrayList();
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                scrSpinnerAsyncTask = new ShiftChangeRequestMMTFragment.SCRSpinnerAsyncTask();
                scrSpinnerAsyncTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SCRSpinnerAsyncTask extends AsyncTask<String, String, String> {
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

    private void getSCRStatusResult(String empid, String token, String fromdate, String todate, String spinId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<>();
                scrStatusAsync = new ShiftChangeRequestMMTFragment.SCRStatusAsync();
                scrStatusAsync.empid = empid;
                scrStatusAsync.fromdate = fromdate;
                scrStatusAsync.todate = todate;
                scrStatusAsync.spnid = spinId;
                scrStatusAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SCRStatusAsync extends AsyncTask<String, String, String> {
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
                final String SHIFTCHANGE_STATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetShiftChangeRequestStatusMMT/" + empid + "/" + fromdate + "/" + todate + "/" + spnid;
                System.out.println("SHIFTCHANGE_STATUS_URL====" + SHIFTCHANGE_STATUS_URL);
                JSONParser jParser = new JSONParser(getActivity());
                json = jParser.makeHttpRequest(SHIFTCHANGE_STATUS_URL, "GET");
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
                                opeStatusmap.put("REQUEST_STATUS", explrObject.getString("REQUEST_STATUS"));
                                opeStatusmap.put("ACTION_BY", explrObject.getString("ACTION_BY"));
                                opeStatusmap.put("ACTION_DATE", explrObject.getString("ACTION_DATE"));
                                opeStatusmap.put("SCR_REASON", explrObject.getString("SCR_REASON"));
                                opeStatusmap.put("SCR_EMPLOYEE_ID", explrObject.getString("SCR_EMPLOYEE_ID"));
                                opeStatusmap.put("ERFS_REQUEST_ID", explrObject.getString("ERFS_REQUEST_ID"));
                                opeStatusmap.put("SCR_STATUS_1", explrObject.getString("SCR_STATUS_1"));

                                arlData.add(opeStatusmap);
                            }
                            txtDataNotFound.setVisibility(View.GONE);
                            shiftChangeRequestStatusAdapter = new ShiftChangeRequestStatusAdapter(getActivity(), arlData);
                            statusRecyclerView.setAdapter(shiftChangeRequestStatusAdapter);
                            statusRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            statusRecyclerView.setAdapter(null);
                            statusRecyclerView.setVisibility(View.GONE);
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

    private void getPullBackStatus(String employee_id, String request_id) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                pullBackAsync = new ShiftChangeRequestMMTFragment.PullBackAsync();
                pullBackAsync.employeeId = employee_id;
                pullBackAsync.requestId = request_id;
                pullBackAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PullBackAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId, requestId;

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

                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackShiftChangeRequestMMT" + "/" + employeeId + "/" + requestId;
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
                        result = result.replaceAll("^\"+|\"+$", " ").trim();
                        int res = Integer.valueOf(result);

                        String keyid;
                        if (spinArrayID.size() == 0) {
                            keyid = "0,1,6,7";
                        } else {
                            keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                        }
                        if (res > 0) {
                            Utilities.showDialog(coordinatorLayout, "Shift Change Request Pullback Successfully.");
                            getSCRStatusResult(employeeId, token, "-", "-", keyid);
                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error During Pullback of Shift Change Request.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ShiftChangeRequestMMTFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ShiftChangeRequestMMTFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
