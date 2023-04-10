package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.savvy.hrmsnewapp.adapter.CustomCitySpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.OnDutyRequestMMTAdapter;
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

public class OnDutyRequestMMTFragment extends BaseFragment implements View.OnClickListener {

    CoordinatorLayout coordinatorLayout;
    OnDutyRequestMMTFragment.ODRequestStatusAsync odRequestStatusAsync;
    OnDutyRequestMMTFragment.PullBackAsync pullBackAsync;
    OnDutyRequestMMTFragment.ODSpinnerStatusAsync odSpinnerStatusAsync;
    OnDutyRequestMMTAdapter onDutyRequestMMTAdapter;
    Button request_FromDate, request_ToDate, sendRequest, reset, status_FromDate, status_ToDate, spinButton, searchButton;
    Spinner oDTypeSpinner;
    CustomTextView txt_DataNotFound, txtCompareDateTextView;
    LinearLayout compareDateLayout;
    EditText odrReason, locationVisit;
    RecyclerView recyclerView;
    Handler handler;
    Runnable rRunnable;
    String FROM_DATE = "", TO_DATE = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    CalanderHRMS calanderHRMS;
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> arlData, arrayList, arlRequestStatusData;
    String oDTypeId = "", json = "", token = "", token_no = "";
    String employeeId, requestId, punch_status_1;
    String  request_Id = "";
    Button txt_Pullback, txt_Cancel;
    CustomTextView tokenNo;
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapdata;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinArray = new ArrayList();
        spinArrayID = new ArrayList();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        employeeId = (shared.getString("EmpoyeeId", ""));
        getStatusList();
        getODMasterType("1");
        getODRequestStatus(employeeId, token, "-", "-", "0,1,2,3,4");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onduty_request_mmt, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        request_FromDate = getActivity().findViewById(R.id.odr_FromDate);
        request_ToDate = getActivity().findViewById(R.id.odr_ToDate);
        sendRequest = getActivity().findViewById(R.id.odr_SendRequest);
        reset = getActivity().findViewById(R.id.odr_Reset);
        status_FromDate = getActivity().findViewById(R.id.odrStatus_FromDate);
        status_ToDate = getActivity().findViewById(R.id.odrStatus_ToDate);
        spinButton = getActivity().findViewById(R.id.odr_SpinButton);
        searchButton = getActivity().findViewById(R.id.odr_SearchButton);
        oDTypeSpinner = getActivity().findViewById(R.id.odrSpinner);
        locationVisit = getActivity().findViewById(R.id.odr_Locations);
        txt_DataNotFound = getActivity().findViewById(R.id.odrTxtDataNotFound);
        txtCompareDateTextView = getActivity().findViewById(R.id.odrCompareDateTextView);
        compareDateLayout = getActivity().findViewById(R.id.odrCompareDateLayout);
        odrReason = getActivity().findViewById(R.id.odr_Reason);
        recyclerView = getActivity().findViewById(R.id.odrStatusRecyclerView);

        RecyclerView.LayoutManager statusLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(statusLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (spinArrayID.size() == 0) {
            spinButton.setText("Pending,Inprocess");
        }

        request_FromDate.setOnClickListener(this);
        request_ToDate.setOnClickListener(this);
        sendRequest.setOnClickListener(this);
        reset.setOnClickListener(this);
        status_FromDate.setOnClickListener(this);
        status_ToDate.setOnClickListener(this);
        spinButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        oDTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oDTypeId = "";
                if (position > 0) {
                    oDTypeId = arrayList.get(position - 1).get("KEY");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView.addOnItemTouchListener(new OPE_StatusFicciFragment.RecyclerTouchListener(getActivity(), recyclerView, new OPE_StatusFicciFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    tokenNo = view.findViewById(R.id.odRTokenNo);
                    txt_Pullback = view.findViewById(R.id.odR_Pullback);
                    txt_Cancel = view.findViewById(R.id.odRCancel);

                    txt_Pullback.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = tokenNo.getText().toString();
                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {

                                    request_Id = mapdata.get("ERFS_REQUEST_ID");
                                    Log.e("Token No.", str);
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

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback OD Request.");
                                txt_header.setText("Pull Back OD Request");

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

                    txt_Cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = tokenNo.getText().toString();
                                String str = mapdata.get("TOKEN_NO");
                                if (token_no.equals(str)) {

                                    String request_Id = mapdata.get("ERFS_REQUEST_ID");
                                    String punch_status_1_Id = mapdata.get("OD_STATUS_1");


                                    requestId = request_Id;
                                    punch_status_1 = punch_status_1_Id;

                                    Log.e("Request Id", "" + requestId);
                                    Log.e("Punch Status", "" + punch_status_1);
                                    Log.e("Token No.", str);
                                }

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.dialogbox_request_cancel);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialogCancel_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialogCancel);

                                final EditText edt_approve_comment_Cancel = dialog.findViewById(R.id.edt_approve_comment_Cancel);

                                String str1 = "<font color='#EE0000'>*</font>";
                                edt_comment_dialog.setText(Html.fromHtml("Comment " + str1));

                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);
                                txt_header.setText("Cancel OD Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String comment = edt_approve_comment_Cancel.getText().toString();
                                        if (comment.equals("")) {
                                            Utilities.showDialog(coordinatorLayout, "Please Enter Comment.");
                                        } else {
                                            sendODCancelRequest(employeeId, comment, requestId);
//                                            getCancelPunchRequest(employeeId, comment, requestId);
                                            dialog.dismiss();
                                        }
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
            case R.id.odr_FromDate:

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
                break;
            case R.id.odr_ToDate:

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
                break;
            case R.id.odr_SendRequest:

                if (request_FromDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter From Date");
                } else if (request_ToDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter To Date");
                } else if (oDTypeId.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select OD Type");
                } else if (locationVisit.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Location");
                } else if (odrReason.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                } else {
                    sendODRequest(employeeId, "0", request_FromDate.getText().toString().replace("/", "-"), request_ToDate.getText().toString().replace("/", "-"), "0", "0", "-", "-", oDTypeId, odrReason.getText().toString().trim().replaceAll("'\\s", "_"), locationVisit.getText().toString().trim().replaceAll("\\s", "_"));
                }

                break;
            case R.id.odr_Reset:
                clearData();
                break;

            case R.id.odrStatus_FromDate:
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
                break;

            case R.id.odrStatus_ToDate:
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
                break;

            case R.id.odr_SpinButton:
                final CheckBox[] checkBox = new CheckBox[arlRequestStatusData.size()];
                final TextView[] txtView = new TextView[arlRequestStatusData.size()];
                LinearLayout l1 = new LinearLayout(getActivity());
                l1.setOrientation(LinearLayout.VERTICAL);
//                getActivity().addContentView(txt1,null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle("Please Select");

                Log.e("ArrayList ", arlRequestStatusData.toString());
                Log.e("ArrayList ", "" + arlRequestStatusData.size());
                try {

                    for (int i = 0; i < arlRequestStatusData.size(); i++) {
                        mapdata = arlRequestStatusData.get(i);

                        checkBox[i] = new CheckBox(getActivity());
                        txtView[i] = new TextView(getActivity());

                        checkBox[i].setTextSize(10);
                        checkBox[i].setPadding(0, 0, 0, 0);
                        checkBox[i].setLines((int) 2.0);
                        checkBox[i].setText(mapdata.get("VALUE"));
                        txtView[i].setText(mapdata.get("KEY"));
                        l1.addView(checkBox[i]);

                        final int finalI = i;

                        try {
//                            Log.e("Spin Id",""+spinArrayID.size());
                            checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {

                                        String spin_new_value = checkBox[finalI].getText().toString();
                                        String spin_new_id = txtView[finalI].getText().toString();

                                        //checkBox[finalI].setChecked(true);
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
                                for (int j = 0; j < arlRequestStatusData.size(); j++) {
                                    mapdata = arlRequestStatusData.get(j);

                                    int spin_index = Integer.parseInt(mapdata.get("KEY"));

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

            case R.id.odr_SearchButton:
                try {
                    token = (shared.getString("Token", ""));

                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "0,1,2,3,4";
                    }
                    String getfromdate = status_FromDate.getText().toString().replace("/", "-");
                    String gettodate = status_ToDate.getText().toString().replace("/", "-");
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);
                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";
                    if (compareDateLayout.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getODRequestStatus(employeeId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void clearData() {
        request_FromDate.setText("");
        request_ToDate.setText("");
        oDTypeSpinner.setSelection(0);
        locationVisit.setText("");
        odrReason.setText("");
        getODRequestStatus(employeeId, token, "-", "-", "0,1,2,3,4");
    }

    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequestStatusData = new ArrayList<>();
                odSpinnerStatusAsync = new OnDutyRequestMMTFragment.ODSpinnerStatusAsync();
                odSpinnerStatusAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ODSpinnerStatusAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
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
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetRequestStatus";

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
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        HashMap<String, String> requestStatusmap;
                        // ArrayList<String> requestArray;
                        JSONArray jsonArray = new JSONArray(result);
                        System.out.println("jsonArray===" + jsonArray);
                        //requestArray=new ArrayList<String>();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                requestStatusmap = new HashMap<String, String>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String key = explrObject.getString("KEY");
                                String value = explrObject.getString("VALUE");
                                // requestArray.add(value);
                                requestStatusmap.put("KEY", key);
                                requestStatusmap.put("VALUE", value);

                                arlRequestStatusData.add(requestStatusmap);
                            }
                            System.out.println("Array===" + arlRequestStatusData);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            System.out.println("Data not getting on server side");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void sendODRequest(String empId, String REQUEST_ID, String fromdate, String todate, String OD_REQUEST_TYPE, String OD_REQUEST_SUB_TYPE, String FROM_TIME, String TO_TIME, String OD_TYPE_ID, String REASON, String LOCATION_VISIT) {
        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String SEND_OD_REQUEST = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendODRequestMMT/" + empId + "/" + REQUEST_ID + "/" + fromdate + "/" + todate + "/" + OD_REQUEST_TYPE + "/" + OD_REQUEST_SUB_TYPE + "/" + FROM_TIME + "/" + TO_TIME + "/" + OD_TYPE_ID + "/" + REASON + "/" + LOCATION_VISIT;
            Log.d(TAG, "sendODRequest: " + SEND_OD_REQUEST);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, SEND_OD_REQUEST, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse: " + response);

                    try {
                        progressDialog.dismiss();
                        int value = Integer.valueOf(response.replaceAll("^\"|\"$", ""));

                        if (value == -1) {
                            Utilities.showDialog(coordinatorLayout, "On Duty Request on the same date and same type already exists.");
                        } else if (value == -2) {
                            Utilities.showDialog(coordinatorLayout, "On Duty Request for previous payroll cycle not allowed.");
                        } else if (value == -3) {
                            Utilities.showDialog(coordinatorLayout, "Leave Request on the same date already exists.");
                        } else if (value == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error during sending On Duty Request.");
                        } else if (value > 0) {
                            Utilities.showDialog(coordinatorLayout, "On Duty Request Sent Successfully.");
                            clearData();
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
            stringRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
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
                                        txtCompareDateTextView.setText("From Date should be less than or equal To Date!");
                                        compareDateLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        compareDateLayout.setVisibility(View.GONE);
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
                });

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

    private void getODMasterType(String groupId) {

        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String GETODMASTER_TYPE = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetODTypeMaster/" + groupId;
            Log.d(TAG, "getODMasterType: " + GETODMASTER_TYPE);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GETODMASTER_TYPE, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    HashMap<String, String> odTypeHashMap;
                    arrayList = new ArrayList<>();
                    try {
                        progressDialog.dismiss();

                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                odTypeHashMap = new HashMap<>();
                                odTypeHashMap.put("KEY", response.getJSONObject(i).getString("ODMasterId"));
                                odTypeHashMap.put("VALUE", response.getJSONObject(i).getString("ODType"));
                                arrayList.add(odTypeHashMap);
                            }
                            CustomCitySpinnerAdapter customCitySpinnerAdapter = new CustomCitySpinnerAdapter(getActivity(), arrayList, "Please Select ");
                            oDTypeSpinner.setAdapter(customCitySpinnerAdapter);
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

    private void getODRequestStatus(String empid, String token, String fromdate, String todate, String spinId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<>();
                odRequestStatusAsync = new OnDutyRequestMMTFragment.ODRequestStatusAsync();
                odRequestStatusAsync.empid = empid;
                odRequestStatusAsync.fromdate = fromdate;
                odRequestStatusAsync.todate = todate;
                odRequestStatusAsync.spnid = spinId;
                odRequestStatusAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ODRequestStatusAsync extends AsyncTask<String, String, String> {
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
                final String OD_REQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetODRequestStatus/" + empid + "/" + fromdate + "/" + todate + "/" + spnid;
                System.out.println("OD_REQUEST_URL====" + OD_REQUEST_URL);
                JSONParser jParser = new JSONParser(getActivity());
                json = jParser.makeHttpRequest(OD_REQUEST_URL, "GET");
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
                        HashMap<String, String> odRequestMap;
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                odRequestMap = new HashMap<>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);

                                odRequestMap.put("TOKEN_NO", explrObject.getString("TOKEN_NO"));
                                odRequestMap.put("FROM_DATE", explrObject.getString("FROM_DATE"));
                                odRequestMap.put("TO_DATE", explrObject.getString("TO_DATE"));
                                odRequestMap.put("OD_TYPE_NAME", explrObject.getString("OD_TYPE_NAME"));
                                odRequestMap.put("ODR_LOCATIONS_VISITED", explrObject.getString("ODR_LOCATIONS_VISITED"));
                                odRequestMap.put("ODR_REASON", explrObject.getString("ODR_REASON"));
                                odRequestMap.put("REQUEST_STATUS", explrObject.getString("REQUEST_STATUS"));
                                odRequestMap.put("ACTION_BY", explrObject.getString("ACTION_BY"));
                                odRequestMap.put("ACTION_DATE", explrObject.getString("ACTION_DATE"));
                                odRequestMap.put("OD_STATUS_1", explrObject.getString("OD_STATUS_1"));
                                odRequestMap.put("ODR_EMPLOYEE_ID", explrObject.getString("ODR_EMPLOYEE_ID"));
                                odRequestMap.put("ERFS_REQUEST_ID", explrObject.getString("ERFS_REQUEST_ID"));

                                arlData.add(odRequestMap);
                            }
                            txt_DataNotFound.setVisibility(View.GONE);
                            onDutyRequestMMTAdapter = new OnDutyRequestMMTAdapter(getActivity(), arlData);
                            recyclerView.setAdapter(onDutyRequestMMTAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setAdapter(null);
                            recyclerView.setVisibility(View.GONE);
                            txt_DataNotFound.setVisibility(View.VISIBLE);
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
                pullBackAsync = new OnDutyRequestMMTFragment.PullBackAsync();
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

                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackODRequest" + "/" + employeeId + "/" + requestId;
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
                            Utilities.showDialog(coordinatorLayout, "OD Request Pullback Successfully.");
                            getODRequestStatus(employeeId, token, "-", "-", keyid);
                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error During Pullback of On Duty Request.");
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


    private void sendODCancelRequest(String emp_id, String comment, String requestId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", emp_id);
                param.put("COMMENT", comment);
                param.put("REQUEST_ID", requestId);

                Log.e("RequestData", param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendODCancellationRequestPost";
                Log.e("URL", url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int res = response.getInt("SendODCancellationRequestPostResult");

                                    String keyid;
                                    if (spinArrayID.size() == 0) {
                                        keyid = "0,1,6,7";
                                    } else {
                                        keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                                    }

                                    if (res == 1) {
                                        Utilities.showDialog(coordinatorLayout, "On Duty Cancellation Request Send Sucessfully.");
                                        getODRequestStatus(employeeId, token, "-", "-", keyid);
                                    } else if (res == -1) {
                                        Utilities.showDialog(coordinatorLayout, "Request Flow Plan Is Not Available.");
                                    } else if (res == -2) {
                                        Utilities.showDialog(coordinatorLayout, "Can Not Take Any Action On The Previous Payroll Requests.");
                                    } else if (res == 0) {
                                        Utilities.showDialog(coordinatorLayout, "Some Error Occur On Processing The Request.");
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
                RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(retryPolicy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private OPE_StatusFicciFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final OPE_StatusFicciFragment.ClickListener clickListener) {
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
