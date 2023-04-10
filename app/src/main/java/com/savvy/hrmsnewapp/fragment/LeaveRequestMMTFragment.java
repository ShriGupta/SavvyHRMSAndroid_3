package com.savvy.hrmsnewapp.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.LeaveRequestCoListAdapter;
import com.savvy.hrmsnewapp.adapter.LeaveRequestMMTAdapter;
import com.savvy.hrmsnewapp.adapter.LeaveRequestStatusMMTAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleyMultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class LeaveRequestMMTFragment extends BaseFragment implements View.OnClickListener {

    private CoordinatorLayout coordinatorLayout;
    LeaveRequestMMTFragment.LRSpinnerAsyncTask lrSpinnerAsyncTask;
    LeaveRequestMMTFragment.PullBackAsync pullBackAsync;
    Button lr_fromDate, lr_toDate, lr_applyButton, lr_resetButton, lrStatus_fromDate, lrStatus_toDate, lrStatus_spinButton, lrStatus_searchButton;
    Spinner lc_Spinner;
    EditText lr_reason;
    LinearLayout lr_compareDateLayout, lrStatus_compareDateLayout, lr_LinearLayout, lr_compensatoryLayout, lr_compensatoryButtonLayout;
    CustomTextView lr_compareDateTextview, lrStatus_compareDateTextview, lrStatus_dataNotFound;
    CustomTextView leave_Balance, comp_Off_Balance, availed_Balance;
    RecyclerView lr_recyclerView, lrStatus_recyclerView;
    CalanderHRMS calanderHRMS;
    Handler handler;
    Runnable rRunnable;
    String FROM_DATE = "", TO_DATE = "";
    String fin_year = null, year, is_back_dated;
    ProgressDialog progressDialog;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    ArrayList<HashMap<String, String>> arrayList, alrData, arrListRequestStatusData, arlData, statusArrayList;
    ArrayList<HashMap<String, String>> childListData;
    String leaveConfigId = "", currentDate = "", token_no;
    int positionId;
    ArrayList<String> spinArrayID, spinArray;
    HashMap<String, String> mapData;
    String token = "";
    String json = "", request_Id;
    CustomTextView txt_punchToken_no;
    Button txt_actionValuePull;
    RecyclerView expandableListView;
    Button coListButton, coSelectButton;
    CheckBox coCheckBoxAdapter;
    HashMap<Integer, String> expListData = new HashMap<>();
    TreeMap<Integer, String> expListCheckedId = new TreeMap<>();
    HashMap<Integer, String> expListUncheckedData;
    StringBuilder stringBuilder, stringBuilderId;
    int attachmentDays = 0;
    public int deductioncount;
    LinearLayout medicalCertificateLayout;
    Button uploadFileButton;
    String ELBD_YEAR_FIN_YEAR = "", FIN_YEAR = "", YEAR = "", IS_PREVIOUS_YEAR = "", LM_ABBREVATION = "", XML_DATA;
    String message;
    int value;
    String displayFileName = "", actualFileName = "";
    CustomTextView noFileChoosen;
    AlertDialog alertD;
    ProgressBar progressBar;
    ImageView imguploadIcon;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinArrayID = new ArrayList<String>();
        spinArray = new ArrayList<String>();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        getLeaveTypeYear();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        currentDate = df.format(c);
        getStatusList();
        getLRStatusResult(employeeId, token, "-", "-", "0,1,2,3,4");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_request_mmt, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());

        leave_Balance = getActivity().findViewById(R.id.lr_leaveBalance);
        comp_Off_Balance = getActivity().findViewById(R.id.lr_leaveComp_Off_Balance);
        availed_Balance = getActivity().findViewById(R.id.lr_leaveAvailedBalance);

        lr_fromDate = getActivity().findViewById(R.id.lrMMT_fromDate);
        lr_toDate = getActivity().findViewById(R.id.lrMMT_ToDate);
        lr_applyButton = getActivity().findViewById(R.id.lrMMT_ApplyButton);
        lr_resetButton = getActivity().findViewById(R.id.lrMMT_ResetButton);

        lc_Spinner = getActivity().findViewById(R.id.lc_Spinner);
        lr_reason = getActivity().findViewById(R.id.lrMMT_Reason);
        lr_compareDateLayout = getActivity().findViewById(R.id.lrCompareDateLayout);
        lr_compareDateTextview = getActivity().findViewById(R.id.lrcompareDateTextView);
        lr_LinearLayout = getActivity().findViewById(R.id.lr_LinearLayout);
        lr_compensatoryLayout = getActivity().findViewById(R.id.lr_compensatoryLayout);
        lr_compensatoryButtonLayout = getActivity().findViewById(R.id.lr_compensatoryButtonLayout);
        expandableListView = getActivity().findViewById(R.id.coExpandList);
        coListButton = getActivity().findViewById(R.id.coListButton);
        coSelectButton = getActivity().findViewById(R.id.coSelectButton);
        noFileChoosen = getActivity().findViewById(R.id.noFileChoose);


        lr_recyclerView = getActivity().findViewById(R.id.lc_RecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lr_recyclerView.setLayoutManager(mLayoutManager);
        lr_recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        expandableListView.setLayoutManager(layoutManager);
        expandableListView.setItemAnimator(new DefaultItemAnimator());


        lrStatus_fromDate = getActivity().findViewById(R.id.lrStatus_fromDate);
        lrStatus_toDate = getActivity().findViewById(R.id.lrStatus_toDate);
        lrStatus_spinButton = getActivity().findViewById(R.id.lrStatus_spinButton);
        lrStatus_searchButton = getActivity().findViewById(R.id.lrStatus_searchButton);

        lrStatus_compareDateLayout = getActivity().findViewById(R.id.lrStatusCompareDateLayout);
        lrStatus_compareDateTextview = getActivity().findViewById(R.id.lrStatus_compareDateTextView);
        lrStatus_dataNotFound = getActivity().findViewById(R.id.lrStatus_DataNotFound);
        lrStatus_recyclerView = getActivity().findViewById(R.id.lrStatus_RecyclerView);

        RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(getActivity());
        lrStatus_recyclerView.setLayoutManager(sLayoutManager);
        lrStatus_recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (spinArrayID.size() == 0) {
            lrStatus_spinButton.setText("Pending,Inprocess");
        }

        lr_fromDate.setOnClickListener(this);
        lr_toDate.setOnClickListener(this);
        lr_applyButton.setOnClickListener(this);
        lr_resetButton.setOnClickListener(this);
        lrStatus_fromDate.setOnClickListener(this);
        lrStatus_toDate.setOnClickListener(this);
        lrStatus_spinButton.setOnClickListener(this);
        lrStatus_searchButton.setOnClickListener(this);


        lr_fromDate.setText(currentDate);
        lr_toDate.setText(currentDate);

        medicalCertificateLayout = getActivity().findViewById(R.id.lr_MedicalCertificateLayout);
        uploadFileButton = getActivity().findViewById(R.id.btn_uploadFile);
        imguploadIcon = getActivity().findViewById(R.id.imguploadIcon);
        progressBar = getActivity().findViewById(R.id.progressBar);
        uploadFileButton.setOnClickListener(this);


        lc_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionId = position;
                if (position > 0) {
                    positionId = position;
                    leaveConfigId = arrayList.get(position - 1).get("KEY");
                    ELBD_YEAR_FIN_YEAR = arrayList.get(position - 1).get("ELBD_YEAR_FIN_YEAR");
                    FIN_YEAR = arrayList.get(position - 1).get("FIN_YEAR");
                    YEAR = arrayList.get(position - 1).get("YEAR");
                    IS_PREVIOUS_YEAR = arrayList.get(position - 1).get("IS_PREVIOUS_YEAR");
                    LM_ABBREVATION = arrayList.get(position - 1).get("LM_ABBREVATION");

                    getListData(employeeId, lr_fromDate.getText().toString().replace("/", "-"), lr_toDate.getText().toString().replace("/", "-"), leaveConfigId);
                    lr_LinearLayout.setVisibility(View.VISIBLE);
                    lr_compensatoryButtonLayout.setVisibility(View.GONE);
                    lr_compensatoryLayout.setVisibility(View.GONE);
                    if (position == 1) {
                        lr_compensatoryButtonLayout.setVisibility(View.VISIBLE);
                        getCOListData(employeeId, false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        coListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lr_compensatoryLayout.getVisibility() == View.VISIBLE) {
                    lr_compensatoryLayout.setVisibility(View.GONE);
                } else {


                    if (childListData.size() > 0) {

                        LeaveRequestCoListAdapter leaveRequestCoListAdapter = new LeaveRequestCoListAdapter(childListData, expListUncheckedData);
                        expandableListView.setAdapter(leaveRequestCoListAdapter);
                        expandableListView.setVisibility(View.VISIBLE);
                        lr_compensatoryLayout.setVisibility(View.VISIBLE);
                        expListData.clear();
                        if (expListUncheckedData != null) {
                            for (int key : expListUncheckedData.keySet()) {
                                expListData.put(key, expListUncheckedData.get(key));
                            }
                        }
                    } else {
                        Utilities.showDialog(coordinatorLayout, "Data Not Found in Co List");
                    }
                }
            }
        });
        coSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deductioncount == expListData.size()) {
                    stringBuilder = new StringBuilder();
                    int counter = 0;
                    Log.d(TAG, "onClick: select button clicked");
                    if (expListData.size() != 0) {
                        expListUncheckedData = new HashMap<>();
                        for (int key : expListData.keySet()) {
                            stringBuilder.append(expListData.get(key));
                            expListUncheckedData.put(key, expListData.get(key));
                            counter++;
                            if (!(counter == expListData.size())) {
                                stringBuilder.append("/");
                            }
                        }
                        coListButton.setText(stringBuilder.toString());
                        lr_compensatoryLayout.setVisibility(View.GONE);
                    } else {
                        Utilities.showDialog(coordinatorLayout, "Please Select CheckBox");
                        coListButton.setText("");
                    }
                } else {
                    Utilities.showDialog(coordinatorLayout, "Total apply leave cannot be greater than selected C-Off");
                }
            }
        });

        lrStatus_recyclerView.addOnItemTouchListener(new LeaveRequestMMTFragment.RecyclerTouchListener(getActivity(), lrStatus_recyclerView, new LeaveRequestMMTFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = statusArrayList.get(position);
                    txt_punchToken_no = view.findViewById(R.id.lrStatus_TokenNo);
                    txt_actionValuePull = view.findViewById(R.id.lRequest_PullBack);

                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = txt_punchToken_no.getText().toString();
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

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback Leave Request.");
                                txt_header.setText("Pull Back Leave Request");

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
        expandableListView.addOnItemTouchListener(new LeaveRequestMMTFragment.RecyclerTouchListener(getActivity(), expandableListView, new LeaveRequestMMTFragment.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                    coCheckBoxAdapter = view.findViewById(R.id.coCheckBox);
                    final CustomTextView coDate = view.findViewById(R.id.coDate);

                    coCheckBoxAdapter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.d(TAG, "onCheckedChanged: " + isChecked);

                            if (isChecked) {
                                getCoDate(isChecked, coDate.getText().toString(), position);
                            } else {
                                expListData.remove(position);
                                expListCheckedId.remove(position);
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

    private void getLeaveRunningBalance1(String year, String fin_year, String is_back_dated) {
        if (Utilities.isNetworkAvailable(getActivity())) {
            final String LEAVE_RUNNING_BALANCE = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveRunningBalance1MMT/" + employeeId + "/" + year + "/" + fin_year + "/" + is_back_dated + "/" + leaveConfigId;
            Log.d(TAG, "getCOListData: " + LEAVE_RUNNING_BALANCE);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, LEAVE_RUNNING_BALANCE, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        Log.d(TAG, "onResponse: " + response);
                        if (response.length() > 0) {
                            String attachmentValue = response.getString("LCM_ATTACHMENT_REQUIRED");
                            if (attachmentValue.equals("True")) {
                                attachmentDays = Integer.valueOf(response.getString("LCM_ATTACHMENT_REQUIRED_DAYS"));
                                if (deductioncount > attachmentDays) {
                                    medicalCertificateLayout.setVisibility(View.VISIBLE);
                                    noFileChoosen.setText("");

                                    imguploadIcon.setVisibility(View.GONE);
                                } else {
                                    medicalCertificateLayout.setVisibility(View.GONE);
                                }
                            } else {
                                medicalCertificateLayout.setVisibility(View.GONE);
                            }
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getCoDate(boolean isChecked, String codate, int position) {
        expListData.put(position, codate);
        expListCheckedId.put(position, childListData.get(position).get("COR_ID"));
    }


    private void getCOListData(String employeeId, final boolean value) {
        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String COLISTDATA_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCOListForLeaveMMT/" + employeeId;
            Log.d(TAG, "getCOListData: " + COLISTDATA_URL);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, COLISTDATA_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        progressDialog.dismiss();
                        HashMap<String, String> mapdata;
                        childListData = new ArrayList<>();
                        Log.d(TAG, "onResponse: " + response);
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                mapdata = new HashMap<>();
                                mapdata.put("COR_COMPOFF_DATE", response.getJSONObject(i).getString("COR_COMPOFF_DATE"));
                                mapdata.put("DAYS", response.getJSONObject(i).getString("DAYS"));
                                mapdata.put("DAY_NAME", response.getJSONObject(i).getString("DAY_NAME"));
                                mapdata.put("DISPLAY_NAME", response.getJSONObject(i).getString("DISPLAY_NAME"));
                                mapdata.put("COR_ID", response.getJSONObject(i).getString("COR_ID"));
                                childListData.add(mapdata);
                            }
                            LeaveRequestCoListAdapter listAdapter = new LeaveRequestCoListAdapter(getActivity(), childListData, value);
                            expandableListView.setAdapter(listAdapter);
                            expandableListView.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    progressDialog.dismiss();
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
            jsonArrayRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lrMMT_fromDate:
                lr_fromDate.setText("");
                calanderHRMS.datePicker(lr_fromDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = lr_fromDate.getText().toString().trim();
                            TO_DATE = lr_toDate.getText().toString().trim();

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

            case R.id.lrMMT_ToDate:

                lr_toDate.setText("");
                calanderHRMS.datePicker(lr_toDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = lr_fromDate.getText().toString().trim();
                            TO_DATE = lr_toDate.getText().toString().trim();

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
            case R.id.lrMMT_ApplyButton:
                if (positionId == 0) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Leave Type");
                } else if (medicalCertificateLayout.getVisibility() == View.VISIBLE && actualFileName.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Document");
                } else if (lr_reason.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                } else {
                    if (positionId == 1) {
                        if (deductioncount == expListData.size()) {
                            if (expListData.size() != 0) {
                                stringBuilder = new StringBuilder();
                                for (int i = 0; i < alrData.size(); i++) {
                                    stringBuilder.append("@").append(alrData.get(i).get("EAR_ATTENDANCE_DATE")).append(",").
                                            append(alrData.get(i).get("EAR_ATTENDANCE_STATUS")).append(",").
                                            append(alrData.get(i).get("DEDUCTION")).append(",").
                                            append(alrData.get(i).get("DEDUCTION_DAYS"));
                                }
                                if (expListCheckedId.size() > 0) {
                                    stringBuilderId = new StringBuilder();
                                    for (int key : expListCheckedId.keySet()) {
                                        stringBuilderId.append(expListCheckedId.get(key)).append(",");
                                    }
                                }
                                XML_DATA = stringBuilder.toString();
                                sendLeaveRequest(employeeId, leaveConfigId, ELBD_YEAR_FIN_YEAR, lr_fromDate.getText().toString().replaceAll("/", "-"), lr_toDate.getText().toString().replaceAll("/", "-"),
                                        "true", "0", lr_reason.getText().toString().replaceAll("\\s", "_"), "", "", "0",
                                        XML_DATA, IS_PREVIOUS_YEAR, YEAR, FIN_YEAR, LM_ABBREVATION, stringBuilderId.toString().replaceAll("(,)*$", ""), displayFileName, actualFileName);

                            } else {
                                Utilities.showDialog(coordinatorLayout, "Please Select C-Off List !");
                            }
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Total apply leave cannot be greater than selected C- Off !");
                        }

                    } else {

                        stringBuilder = new StringBuilder();
                        for (int i = 0; i < alrData.size(); i++) {
                            stringBuilder.append("@").append(alrData.get(i).get("EAR_ATTENDANCE_DATE")).append(",").
                                    append(alrData.get(i).get("EAR_ATTENDANCE_STATUS")).append(",").
                                    append(alrData.get(i).get("DEDUCTION")).append(",").
                                    append(alrData.get(i).get("DEDUCTION_DAYS"));
                        }
                        XML_DATA = stringBuilder.toString();
                        sendLeaveRequest(employeeId, leaveConfigId, ELBD_YEAR_FIN_YEAR, lr_fromDate.getText().toString().replaceAll("/", "-"), lr_toDate.getText().toString().replaceAll("/", "-"),
                                "true", "0", lr_reason.getText().toString().replaceAll("\\s", "_"), "", "", "0",
                                XML_DATA, IS_PREVIOUS_YEAR, YEAR, FIN_YEAR, LM_ABBREVATION, "0", displayFileName, actualFileName);
                    }

                }
                break;

            case R.id.lrMMT_ResetButton:
                resetData();
                break;

            case R.id.lrStatus_fromDate:

                lrStatus_fromDate.setText("");
                calanderHRMS.datePicker(lrStatus_fromDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = lrStatus_fromDate.getText().toString().trim();
                            TO_DATE = lrStatus_toDate.getText().toString().trim();

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

            case R.id.lrStatus_toDate:
                lrStatus_toDate.setText("");
                calanderHRMS.datePicker(lrStatus_toDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = lrStatus_fromDate.getText().toString().trim();
                            TO_DATE = lrStatus_toDate.getText().toString().trim();

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

            case R.id.lrStatus_spinButton:
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
                        lrStatus_spinButton.setText(spinArray.toString().replace("[", "").replace("]", ""));
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

            case R.id.lrStatus_searchButton:

                try {
                    token = (shared.getString("Token", ""));
                    employeeId = (shared.getString("EmpoyeeId", ""));

                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "0,1,2,3,4";
                    }
                    String gettodate = lrStatus_toDate.getText().toString().replace("/", "-");
                    String getfromdate = lrStatus_fromDate.getText().toString().replace("/", "-");
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";
                    if (lrStatus_compareDateLayout.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getLRStatusResult(employeeId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_uploadFile:
                selectFile();
                break;

        }
    }


    private void getLeaveTypeYear() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            final String LEAVE_TYPE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveTypeYear";
            Log.d(TAG, "sendWFHRequest: " + LEAVE_TYPE_URL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LEAVE_TYPE_URL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        fin_year = response.getJSONObject("GetLeaveTypeYearResult").getString("FIN_YEAR");
                        year = response.getJSONObject("GetLeaveTypeYearResult").getString("YEAR");
                        is_back_dated = response.getJSONObject("GetLeaveTypeYearResult").getString("IS_BACK_DATED");

                        if (fin_year != null) {
                            getLeaveSummaryData();
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getLeaveSummaryData() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            final String LEAVE_TYPE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveSummaryMMT/" + employeeId + "/" + year + "/" + fin_year;
            Log.d(TAG, "getLeaveSummarData: " + LEAVE_TYPE_URL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, LEAVE_TYPE_URL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "onResponse: " + response);
                        String Leave_Balance = response.getString("LEAVE_ENACASHMENT");
                        String Current_Comp_Off_Bal = response.getString("CURRENT_COFF_BAL");
                        String Leave_Availed_this_Year = response.getString("LEAVE_AVALIED");

                        leave_Balance.setText(Leave_Balance);
                        comp_Off_Balance.setText(Current_Comp_Off_Bal);
                        availed_Balance.setText(Leave_Availed_this_Year);

                        getSpinnerData();
                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getSpinnerData() {
        if (Utilities.isNetworkAvailable(getActivity())) {

            final String LEAVE_RUNNING_BALANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveRunningBalance";
            Log.d(TAG, "getSpinnerData: " + LEAVE_RUNNING_BALANCE_URL);
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            try {
                params_final.put("employeeId", employeeId);
                params_final.put("isPreviousYear", "0");
                params_final.put("year", year);
                params_final.put("finYear", fin_year);
                pm.put("objLeaveRunningBalanceInfo", params_final);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LEAVE_RUNNING_BALANCE_URL, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "onResponse: " + response);
                        HashMap<String, String> hashMap;
                        arrayList = new ArrayList<>();

                        if (response.length() > 0) {
                            for (int i = 0; i < response.getJSONArray("GetLeaveRunningBalanceResult").length(); i++) {

                                hashMap = new HashMap<>();
                                hashMap.put("ELBD_YEAR_FIN_YEAR", response.getJSONArray("GetLeaveRunningBalanceResult").getJSONObject(i).getString("ELBD_YEAR_FIN_YEAR"));
                                hashMap.put("FIN_YEAR", response.getJSONArray("GetLeaveRunningBalanceResult").getJSONObject(i).getString("FIN_YEAR"));
                                hashMap.put("YEAR", response.getJSONArray("GetLeaveRunningBalanceResult").getJSONObject(i).getString("YEAR"));
                                hashMap.put("IS_PREVIOUS_YEAR", response.getJSONArray("GetLeaveRunningBalanceResult").getJSONObject(i).getString("IS_PREVIOUS_YEAR"));
                                hashMap.put("LM_ABBREVATION", response.getJSONArray("GetLeaveRunningBalanceResult").getJSONObject(i).getString("LM_ABBREVATION"));

                                String leave_Name = response.getJSONArray("GetLeaveRunningBalanceResult").getJSONObject(i).getString("LEAVE_NAME");
                                String leave_ConfigId = response.getJSONArray("GetLeaveRunningBalanceResult").getJSONObject(i).getString("LEAVE_CONFIG_ID");
                                hashMap.put("KEY", leave_ConfigId);
                                hashMap.put("VALUE", leave_Name);
                                arrayList.add(hashMap);
                            }

                            CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getActivity(), arrayList);
                            lc_Spinner.setAdapter(customSpinnerAdapter);

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
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);

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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getListData(String employeeId, String fromdate, String todate, String leaveConfigId) {
        if (Utilities.isNetworkAvailable(getActivity())) {
            deductioncount = 0;
            final String LEAVE_TYPE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveBreakupForApplication/" + employeeId + "/" + fromdate + "/" + todate + "/" + leaveConfigId;
            Log.d(TAG, "getLeaveSummarData: " + LEAVE_TYPE_URL);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, LEAVE_TYPE_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        alrData = new ArrayList<>();
                        HashMap<String, String> hashMap;

                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                hashMap = new HashMap<>();
                                hashMap.put("EAR_ATTENDANCE_DATE", response.getJSONObject(i).getString("EAR_ATTENDANCE_DATE"));
                                hashMap.put("EAR_ATTENDANCE_STATUS", response.getJSONObject(i).getString("EAR_ATTENDANCE_STATUS"));
                                hashMap.put("DEDUCTION", response.getJSONObject(i).getString("DEDUCTION"));
                                hashMap.put("DEDUCTION_DAYS", response.getJSONObject(i).getString("DEDUCTION_DAYS"));
                                alrData.add(hashMap);
                                deductioncount += Math.round(Float.valueOf(response.getJSONObject(i).getString("DEDUCTION_DAYS")));
                            }

                            LeaveRequestMMTAdapter leaveRequestMMTAdapter = new LeaveRequestMMTAdapter(getActivity(), alrData);
                            lr_recyclerView.setAdapter(leaveRequestMMTAdapter);
                            lr_recyclerView.setVisibility(View.VISIBLE);
                            getLeaveRunningBalance1(year, fin_year, is_back_dated);
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
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
            jsonArrayRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
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
                                            lr_compareDateTextview.setText(R.string.compareDate);
                                            lr_compareDateLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            lrStatus_compareDateTextview.setText(R.string.compareDate);
                                            lrStatus_compareDateLayout.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        lr_compareDateLayout.setVisibility(View.GONE);
                                        lrStatus_compareDateLayout.setVisibility(View.GONE);
                                        if (value.equals("REQUEST") && positionId != 0) {
                                            getListData(employeeId, lr_fromDate.getText().toString().replace("/", "-"), lr_toDate.getText().toString().replace("/", "-"), leaveConfigId);
                                        } else {
                                            if (!value.equals(""))
                                                Utilities.showDialog(coordinatorLayout, "Please Select Leave Type");
                                        }
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

    private void getStatusList() {

        arrListRequestStatusData = new ArrayList();
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                lrSpinnerAsyncTask = new LeaveRequestMMTFragment.LRSpinnerAsyncTask();
                lrSpinnerAsyncTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LRSpinnerAsyncTask extends AsyncTask<String, String, String> {

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
    }

    private void getLRStatusResult(String employeeId, String token, String fromdate, String todate, String spinId) {

        if (Utilities.isNetworkAvailable(getActivity())) {
            final String LEAVE_REQUEST_STATUS = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveRequestStatus";
            Log.d(TAG, "getLRStatusResult: " + LEAVE_REQUEST_STATUS);

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            try {
                params_final.put("EmployeeId", employeeId);
                params_final.put("fromDate", fromdate);
                params_final.put("toDate", todate);
                params_final.put("requestStatus", spinId);
                pm.put("objLeaveRequestStatusInfo", params_final);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LEAVE_REQUEST_STATUS, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        Log.d(TAG, "onResponse: " + response);
                        HashMap<String, String> hashMap;
                        statusArrayList = new ArrayList<>();

                        JSONArray jsonArray = response.getJSONArray("GetLeaveRequestStatusResult");
                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                hashMap = new HashMap<>();
                                JSONObject jsonArrayJSONObject = jsonArray.getJSONObject(i);
                                hashMap.put("TOKEN_NO", jsonArrayJSONObject.getString("TOKEN_NO"));
                                hashMap.put("FROM_DATE", jsonArrayJSONObject.getString("FROM_DATE"));
                                hashMap.put("TO_DATE", jsonArrayJSONObject.getString("TO_DATE"));
                                hashMap.put("LM_LEAVE_NAME", jsonArrayJSONObject.getString("LM_LEAVE_NAME"));
                                hashMap.put("LR_REASON", jsonArrayJSONObject.getString("LR_REASON"));
                                hashMap.put("REQUEST_STATUS", jsonArrayJSONObject.getString("REQUEST_STATUS"));
                                hashMap.put("ACTION_BY", jsonArrayJSONObject.getString("ACTION_BY"));
                                hashMap.put("ACTION_DATE", jsonArrayJSONObject.getString("ACTION_DATE"));
                                hashMap.put("ERFS_REQUEST_ID", jsonArrayJSONObject.getString("ERFS_REQUEST_ID"));
                                hashMap.put("LR_EMPLOYEE_ID", jsonArrayJSONObject.getString("LR_EMPLOYEE_ID"));
                                hashMap.put("LR_STATUS", jsonArrayJSONObject.getString("LR_STATUS"));
                                statusArrayList.add(hashMap);
                            }
                            LeaveRequestStatusMMTAdapter adapter = new LeaveRequestStatusMMTAdapter(getActivity(), statusArrayList);
                            lrStatus_recyclerView.setAdapter(adapter);
                            lrStatus_recyclerView.setVisibility(View.VISIBLE);
                            lrStatus_dataNotFound.setVisibility(View.GONE);
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            lrStatus_recyclerView.setVisibility(View.GONE);
                            lrStatus_dataNotFound.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    progressDialog.dismiss();
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getPullBackStatus(String employee_id, String request_id) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                pullBackAsync = new LeaveRequestMMTFragment.PullBackAsync();
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

                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackLeaveRequest" + "/" + employeeId + "/" + requestId;
                JSONParser jParser = new JSONParser(getActivity());
                String json = jParser.makeHttpRequest(GETREQUESTSTATUS_URL, "GET");

                if (json != null) {
                    Log.d("JSON result", json);
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
                            Utilities.showDialog(coordinatorLayout, "Leave Request Pullback Successfully.");
                            getLRStatusResult(employeeId, token, "-", "-", keyid);
                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error During Pullback Leave Request.");
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

    public void sendLeaveRequest(String employeeId, String LeaveConfigID, String LeaveTypeYearFinYear, String FromDate, String ToDate,
                                 String isLTA, String LeaveType, String Reason, String ContactNo, String Address, String WorkHandOverTo,
                                 String breakUpLeave, String is_previous_year, String leave_type_year, String leave_type_fin_year, String leave_abbreviation
            , String coffholiday, String displayfilename, String actualfilename) {

        Log.d(TAG, "sendLeaveRequest: ");

        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String SEND_REQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendLeaveRequestMMT";
            Log.d(TAG, "sendLeaveRequest: " + SEND_REQUEST_URL);

            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            try {
                params_final.put("employeeId", employeeId);
                params_final.put("LeaveConfigID", LeaveConfigID);
                params_final.put("LeaveTypeYearFinYear", LeaveTypeYearFinYear);
                params_final.put("FromDate", FromDate);
                params_final.put("ToDate", ToDate);
                params_final.put("isLTA", isLTA);
                params_final.put("LeaveType", LeaveType);
                params_final.put("Reason", Reason);
                params_final.put("ContactNo", ContactNo);
                params_final.put("Address", Address);
                params_final.put("WorkHandOverTo", WorkHandOverTo);
                params_final.put("breakUpLeave", breakUpLeave);
                params_final.put("IS_PREVIOUS_YEAR", is_previous_year);
                params_final.put("LeaveTypeYear", leave_type_year);
                params_final.put("LeaveTypeFinYear", leave_type_fin_year);
                params_final.put("LeaveAbbrevation", leave_abbreviation);
                params_final.put("CoffHolidayID", coffholiday);
                params_final.put("DisplayFileName", displayfilename);
                params_final.put("ActualFileName", actualfilename);

                pm.put("objLeaveRequestInfoMMT", params_final);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SEND_REQUEST_URL, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        Log.d(TAG, "onResponse: " + response);


                        String result = response.getString("SendLeaveRequestMMTResult");
                        List<String> resultList = Arrays.asList(result.split(","));
                        if (resultList.size() > 1) {
                            value = Integer.valueOf(resultList.get(0));
                            message = resultList.get(1);
                        } else {
                            value = Integer.valueOf(resultList.get(0));
                        }

                        if (value == -1) {
                            Utilities.showDialog(coordinatorLayout, "Leave/OD/WFH request already pending for same date.");
                        } else if (value == -2) {
                            Utilities.showDialog(coordinatorLayout, "You are not eligible for this leave type.");
                        } else if (value == -3) {
                            Utilities.showDialog(coordinatorLayout, "ESI Leave are not applicable.");
                        } else if (value == -4) {
                            Utilities.showDialog(coordinatorLayout, "You have exceed max limit of ESI Leave.");
                        } else if (value == -5) {
                            Utilities.showDialog(coordinatorLayout, "You are not eligible for this leave type.");
                        } else if (value == -6) {
                            Utilities.showDialog(coordinatorLayout, "You can not request leave less than" + message + "days.");
                        } else if (value == -7) {
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than" + message + "days.");
                        } else if (value == -8) {
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than" + message + "time in a year.");
                        } else if (value == -9) {
                            Utilities.showDialog(coordinatorLayout, "You have to intimate atleast" + message + "days in advance.");
                        } else if (value == -10) {
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than" + message + "days in back.");
                        } else if (value == -11) {
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than" + message + "time in job tenure.");
                        } else if (value == -12) {
                            Utilities.showDialog(coordinatorLayout, "You can not clubbed with other leave.");
                        } else if (value == -13) {
                            Utilities.showDialog(coordinatorLayout, "This leave can only be clubbed with" + message + "leave.");
                        } else if (value == -14) {
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than remaining.");
                        } else if (value == -15) {
                            Utilities.showDialog(coordinatorLayout, "You can not request LOP Leave more" + message + "days.");
                        } else if (value == -16) {
                            Utilities.showDialog(coordinatorLayout, "You can not request Advance Leave more" + message + "days.");
                        } else if (value == -17) {
                            Utilities.showDialog(coordinatorLayout, "You can not request Leave as LOP and Advance both consumed.");
                        } else if (value == -18) {
                            Utilities.showDialog(coordinatorLayout, "You can not request Leave for previous payroll cycle.");
                        } else if (value == -19) {
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than" + message + "time in a month.");
                        } else if (value == -20) {
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than" + message + "Days in month.");
                        } else if (value == -21) {
                            Utilities.showDialog(coordinatorLayout, "You have to select" + message + "Days C-Off from List.");
                        } else if (value == -25) {
                            Utilities.showDialog(coordinatorLayout, "Policy not define for Requested Leave Period.");
                        } else if (value == -26) {
                            Utilities.showDialog(coordinatorLayout, "Leave date can not be less than Leave Policy Date Range.");
                        } else if (value == -35) {
                            Utilities.showDialog(coordinatorLayout, "Leave Request for previous payroll cycle not allowed.");
                        } else if (value == -36) {
                            Utilities.showDialog(coordinatorLayout, "Document Required.");
                        } else if (value == -41) {
                            Utilities.showDialog(coordinatorLayout, "Leave Request for future year are not allowed.");
                        } else if (value == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error during saving the Leave Request." + message);
                        } else if (value > 0) {
                            Utilities.showDialog(coordinatorLayout, "Leave Request sent for approval.");
                            resetData();

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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void resetData() {
        lr_fromDate.setText(currentDate);
        lr_toDate.setText(currentDate);
        lc_Spinner.setSelection(0);
        lr_reason.setText("");
        noFileChoosen.setText("");
        lr_LinearLayout.setVisibility(View.GONE);
        lr_recyclerView.setVisibility(View.GONE);
        coListButton.setText("Please Select");
        lr_compensatoryButtonLayout.setVisibility(View.GONE);
        lr_compensatoryLayout.setVisibility(View.GONE);
        deductioncount = 0;
        medicalCertificateLayout.setVisibility(View.GONE);
        getLRStatusResult(employeeId, token, "-", "-", "0,1,2,3,4");
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private LeaveRequestMMTFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LeaveRequestMMTFragment.ClickListener clickListener) {
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


    private void selectFile() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.leave_request_custom_dialog_mmt, null);
        alertD = new AlertDialog.Builder(getActivity()).create();
        ImageButton btnAdd1 = promptView.findViewById(R.id.galleryButton);
        ImageButton btnAdd2 = promptView.findViewById(R.id.cameraButton);
        btnAdd1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 999);
            }
        });
        btnAdd2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1000);
            }
        });
        alertD.setView(promptView);
        alertD.show();
        Window window = alertD.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setAttributes(wlp);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        alertD.dismiss();
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                displayFileName = fileName(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                    if (bitmap != null) {
                        uploadImage(bitmap);
                    } else {
                        getFileData(Objects.requireNonNull(uri));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                imguploadIcon.setVisibility(View.GONE);
            }
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            displayFileName = "CaptureImage.jpg";
            uploadImage(photo);
        }
    }

    private void getFileData(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.d("nameeeee>>>>  ", displayFileName);

                    uploadFiles(displayFileName, uri);
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayFileName = myFile.getName();
        }
    }

    private void uploadFiles(final String pdfname, Uri pdffile) {

        InputStream iStream = null;
        try {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Uploading Image...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            iStream = getActivity().getContentResolver().openInputStream(pdffile);
            final byte[] inputData = getBytes(iStream);
            String upload_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId;

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            progressDialog.dismiss();

                            List<String> list = Arrays.asList(new String(response.data).split(","));
                            int value = Integer.valueOf(list.get(0));
                            if (value == 1) {
                                Utilities.showDialog(coordinatorLayout, "File Upload Successfully");
                                imguploadIcon.setVisibility(View.VISIBLE);
                                noFileChoosen.setText(displayFileName);
                                actualFileName = list.get(1);
                            } else {
                                Utilities.showDialog(coordinatorLayout, "File upload failed !");
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            noFileChoosen.setText("");
                            imguploadIcon.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("filename", new DataPart(pdfname, inputData));
                    return params;
                }
            };
            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(volleyMultipartRequest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void uploadImage(final Bitmap bitmap) {
        imguploadIcon.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String IMG_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId;

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, IMG_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        Log.d("ressssssoo", new String(response.data));

                        List<String> list = Arrays.asList(new String(response.data).split(","));
                        int value = Integer.valueOf(list.get(0));
                        if (value == 1) {
                            Utilities.showDialog(coordinatorLayout, "Image uploaded Successfully");
                            imguploadIcon.setVisibility(View.VISIBLE);
                            noFileChoosen.setText(displayFileName);
                        }
                        actualFileName = list.get(1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        noFileChoosen.setText("");
                        imguploadIcon.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("filename", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        if (bitmap != null) {
            int imgquality = 100;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, imgquality, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } else {
            return null;
        }

    }

    private String fileName(Uri uri) {
        Cursor returnCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
}
