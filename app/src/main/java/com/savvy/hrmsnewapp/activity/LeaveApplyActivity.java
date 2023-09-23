package com.savvy.hrmsnewapp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.AlertListAdapter;
import com.savvy.hrmsnewapp.adapter.ApplyCOLeaveAdapter;
import com.savvy.hrmsnewapp.adapter.ApplyRHLeaveAdapter;
import com.savvy.hrmsnewapp.adapter.ShowDataListAdapter;
import com.savvy.hrmsnewapp.adapter.TravelExpenseExpandListAdapter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

//import android.widget.Toolbar;

public class LeaveApplyActivity extends BaseActivity implements View.OnClickListener {
    private SavvyHRMSApplication mSavvyHrmsApp;
    CoordinatorLayout coordinatorLayout;

    LeaveApplyActivity.SendLeaveRequestAsync sendLeaveRequestAsync;

    CalanderHRMS calanderHRMS;
    String value_name = "";
    String value_todate = "";
    String value_fromdate = "";
    String value_config_id = "";
    String value_employee_id = "";
    String value_fromdate1 = "";
    String value_todate1 = "";
    String CoffHolidayID = "";
    String value_is_previous_year = "", value_leave_type_year = "",
            value_leave_type_fin_year = "", value_leave_type_year_fin_year = "",
            value_leave_abbrevation = "";

    ArrayList<HashMap<String, String>> arlData;

    private int year, month, day;
    private boolean isDateDialogopenned;
    DatesDataAsynTask datesDataAsynctask;
    StringBuilder stringBuilder;

    EditText edt_reason, edt_contactNo, edt_workHandover, edt_address;
    Button btn_apply, btn_close, btn_showDates, btn_fromDate, btn_toDate;
    Switch mySwitch;
    String mySwitchValue = "true";
    String from_date, to_date;
    RecyclerView recylerLeaveactivity;
    FrameLayout frameLayout;
    ShowDataListAdapter mAdapter;
    Button btnCross;
    TextView txtData;
    boolean showButtonClick = false;

    Handler handler;
    Runnable rRunnable;
    String leave_toDate = "";
    int counter = 0;
    int count = 0;

    boolean spinner_status = true;

    Spinner spin_deduct_type;
    CustomTextView deduction_days;
    ArrayList<String> deductDays;

    String Spintype = "";
    String Spintype1 = "";

    HashMap<Integer, String> mapdata1;
    HashMap<Integer, String> mapdata2;
    //Handler CompareDate
    public static Handler handler2;
    public static Runnable runnable2;
    LinearLayout linear_result_compareDate;
    CustomTextView txt_result_compareDate;

    CustomTextView txv_fromDate, txv_toDate, txv_Reason;

    String ENABLE_NOTE;
    String NOTE_MESSAGE;
    private TextView txtLeaveNote;
    SharedPreferences shared;

    CustomTextView tv_RHLeave, tv_COLeave;
    Spinner rhLeaveSpinner, coLeaveSpinner;
    LinearLayout rhLeave_Layout, coLeave_Layout;
    ApplyRHLeaveAdapter rhLeaveAdapter;
    ApplyCOLeaveAdapter coLeaveAdapter;
    List<HashMap<String, String>> rhLeaveDataList;
    List<HashMap<String, String>> coLeaveDataList;
    String formantedCoffDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_apply);
        shared = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        Constants.LEAVE_APPLY_ACTIVITY++;
        Log.e("Count", "" + Constants.LEAVE_APPLY_ACTIVITY);
        arlData = new ArrayList<>();
        mapdata1 = new HashMap<>();
        mapdata2 = new HashMap<>();
        stringBuilder = new StringBuilder();
        calanderHRMS = new CalanderHRMS(LeaveApplyActivity.this);

        Bundle extras = getIntent().getExtras();

        try {
            if (extras != null) {
                value_name = extras.getString("LEAVE_NAME").substring(0, 2);
                value_fromdate = extras.getString("FROM_DATE");
                value_todate = extras.getString("TO_DATE");
                value_config_id = extras.getString("LEAVE_CONFIG_ID");
                value_employee_id = extras.getString("EMPLOYEE_ID");

                value_is_previous_year = extras.getString("IS_PREVIOUS_YEAR");
                value_leave_type_year = extras.getString("YEAR");
                value_leave_type_fin_year = extras.getString("FIN_YEAR");
                value_leave_type_year_fin_year = extras.getString("ELBD_YEAR_FIN_YEAR");
                value_leave_abbrevation = extras.getString("LM_ABBREVATION");

                value_fromdate1 = extras.getString("FROM_DATE1");
                value_todate1 = extras.getString("TO_DATE1");

//                ENABLE_NOTE = extras.getString("ENABLE_NOTE");
//                NOTE_MESSAGE = extras.getString("NOTE_MESSAGE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            View view1 = this.getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Request For " + value_name);


        mSavvyHrmsApp = SavvyHRMSApplication.getInstance();
        init();
        setUpToolBar();

    }

    private void init() {
        edt_contactNo = findViewById(R.id.edt_contactNo);
        edt_address = findViewById(R.id.edt_address);

        try{
            edt_contactNo.setText(shared.getString("Mobile",""));
            edt_address.setText(shared.getString("Address",""));
        }catch (Exception e){e.printStackTrace();}

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        edt_reason = findViewById(R.id.edt_reason);

//        edt_workHandover=(EditText)findViewById(R.id.edt_workHandover);
        btn_fromDate = findViewById(R.id.btn_fromDate_leave);
        btn_toDate = findViewById(R.id.btn_toDate_leave);
        btn_apply = findViewById(R.id.btn_apply);
        btn_close = findViewById(R.id.btn_close);
        btn_showDates = findViewById(R.id.btn_showDates);
        txtData = findViewById(R.id.txt_date_range);
        txtLeaveNote = findViewById(R.id.txtLeaveNote);

        btnCross = findViewById(R.id.btn_closeFrame);

        txv_fromDate = findViewById(R.id.txv_fromDate_title);
        txv_toDate = findViewById(R.id.txv_toDate_title);
        txv_Reason = findViewById(R.id.txv_reason_title);

        String validation = "<font color=\"#EE0000\"><bold>" + "*" + "</bold></font>";
        txv_fromDate.setText(Html.fromHtml("From Date " + validation));
        txv_toDate.setText(Html.fromHtml("To Date " + validation));
        txv_Reason.setText(Html.fromHtml("Reason " + validation));
        CoffHolidayID = "0";

        recylerLeaveactivity = findViewById(R.id.recylerFrame);
        frameLayout = findViewById(R.id.frameLeaveActivityLayout);

        linear_result_compareDate = findViewById(R.id.Linear_result_compareDate);
        txt_result_compareDate = findViewById(R.id.result_compareDate);

        linear_result_compareDate.setVisibility(View.GONE);
        mAdapter = new ShowDataListAdapter(this, arlData);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recylerLeaveactivity.setLayoutManager(mLayoutManager);
        recylerLeaveactivity.setItemAnimator(new DefaultItemAnimator());

        mSavvyHrmsApp.setButtonFont(btn_showDates, "Nexa_Bold.otf");
        mSavvyHrmsApp.setButtonFont(btn_apply, "Nexa_Bold.otf");
        mSavvyHrmsApp.setButtonFont(btn_close, "Nexa_Bold.otf");
        mSavvyHrmsApp.setEditTextFont(btn_fromDate, "Nexa_Light.otf");
        mSavvyHrmsApp.setEditTextFont(btn_toDate, "Nexa_Light.otf");

        txtData.setText("Leave Policy Date Range From " + value_fromdate + " To " + value_todate + ".");

        /*if (ENABLE_NOTE.equals("1")) {
            if (TextUtils.isEmpty(NOTE_MESSAGE)) {
                txtLeaveNote.setText("NOTE : You need to submit the medical certificate in case of applying leave more than 3 days.");
            } else {
                txtLeaveNote.setText("NOTE : " + NOTE_MESSAGE);
            }
        } else {
            txtLeaveNote.setVisibility(View.GONE);
        }*/

        btn_fromDate.setOnClickListener(this);
        btn_toDate.setOnClickListener(this);
        btn_showDates.setOnClickListener(this);
        btn_apply.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btnCross.setOnClickListener(this);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        btn_fromDate.setText(simpleDateFormat.format(date));
        btn_toDate.setText(simpleDateFormat.format(date));
        String fromdate = btn_fromDate.getText().toString().replace("/", "-");
        String todate = btn_toDate.getText().toString().replace("/", "-");
        getDatesData(value_employee_id, fromdate, todate, value_config_id);

        tv_RHLeave = findViewById(R.id.tv_RH_Leave);
        rhLeaveSpinner = findViewById(R.id.rh_LeaveSpinner);
        rhLeave_Layout = findViewById(R.id.rhLeave_Layout);
        rhLeave_Layout.setVisibility(View.GONE);

        tv_COLeave = findViewById(R.id.tv_CO_Leave);
        coLeaveSpinner = findViewById(R.id.co_LeaveSpinner);
        coLeave_Layout = findViewById(R.id.coLeave_Layout);
        coLeave_Layout.setVisibility(View.GONE);


        if (value_name.equals("RH")) {
            rhLeave_Layout.setVisibility(View.VISIBLE);
            btn_fromDate.setEnabled(false);
            btn_toDate.setEnabled(false);
            getRHLeaveData(value_employee_id, value_fromdate1.replace("/", "-"), value_todate1.replace("/", "-"));
        }
        rhLeaveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    CoffHolidayID = "RH";
                } else if (i == 1) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Holiday");
                    CoffHolidayID = "RH";
                    rhLeaveSpinner.setSelection(0);
                } else {

                    try {

                        CustomTextView tvHolidayName = rhLeaveSpinner.getSelectedView().findViewById(R.id.tv_HolidayDate);
                        CustomTextView tvHolidayDate = rhLeaveSpinner.getSelectedView().findViewById(R.id.tv_DayName);
                        tvHolidayName.setText("");
                        tvHolidayDate.setText("");

                        String holidayDate = rhLeaveDataList.get(i - 2).get("HM_HOLIDAY_DATE_1");

                        CoffHolidayID = rhLeaveDataList.get(i - 2).get("HM_HOLIDAY_ID");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date convertedDate;
                        try {
                            convertedDate = dateFormat.parse(holidayDate);
                            SimpleDateFormat sdfnewformat = new SimpleDateFormat("dd/MM/yyyy");
                            holidayDate = sdfnewformat.format(convertedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        btn_fromDate.setText(holidayDate);
                        btn_toDate.setText(holidayDate);
                        refreshListData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (value_name.equals("CO")) {
            coLeave_Layout.setVisibility(View.VISIBLE);
            getCOLeaveData(value_employee_id);
            btn_toDate.setEnabled(false);

        }

        coLeaveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Log.e("TAG", "onItemSelected: " + i);
                    CustomTextView coSimple_textView = view.findViewById(R.id.simple_textView);
                    coSimple_textView.setText("Select Date");
                    CoffHolidayID = "CO";
                } else if (i == 1) {
                    Utilities.showDialog(coordinatorLayout, "Please Select CO List");
                    CoffHolidayID = "CO";
                    coLeaveSpinner.setSelection(0);

                } else {
                    CustomTextView coDays = view.findViewById(R.id.tv_days);
                    CustomTextView coDayName = view.findViewById(R.id.tv_coleaveDayName);
                    coDays.setText("");
                    coDayName.setText("");
                    CoffHolidayID = coLeaveDataList.get(i - 2).get("COR_ID");
                    refreshListData();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        recylerLeaveactivity.addOnItemTouchListener(new LeaveApplyActivity.RecyclerTouchListener(LeaveApplyActivity.this, recylerLeaveactivity, new LeaveApplyActivity.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                try {

                    spin_deduct_type = view.findViewById(R.id.txt_deductiontypeValue);
                    deduction_days = view.findViewById(R.id.txt_deductiondaysValue);

                    spin_deduct_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos1, long id) {
                            int pos = spin_deduct_type.getSelectedItemPosition();
                            if (pos == 0) {
                                AlertDialog.Builder dailog = new AlertDialog.Builder(LeaveApplyActivity.this);
                                dailog.setTitle("Savvyhrms.com says : ");
                                dailog.setMessage("Can not be changed.");
                                dailog.setCancelable(true);

                                dailog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dailog.show();

                                spin_deduct_type.setSelection(1);

                            } else if (pos == 1) {
                                deduction_days.setText("1.0");
                            } else if (pos == 2) {
                                deduction_days.setText("0.5");
                            } else if (pos == 3) {
                                deduction_days.setText("0.5");

                            }
                            Spintype1 = parent.getItemAtPosition(pos1).toString();

                            Log.e("Map Before", " " + mapdata1);
                            Log.e("Map Before 2", " " + mapdata2);
                            mapdata1.put(position, Spintype1);
                            mapdata2.put(position, deduction_days.getText().toString());
                            Log.e("Map After", " " + mapdata1);
                            Log.e("Map After 2", " " + mapdata2);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    Log.e("Spin Type1", " " + Spintype1);

                    if (Spintype1.equals("")) {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void refreshCoffListData(String coff_date) {
        btn_fromDate.setText("");
        btn_toDate.setText("");
        btn_toDate.setEnabled(false);

        SimpleDateFormat month_date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        Date date = null;
        try {
            date = sdf.parse(coff_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        assert date != null;
        formantedCoffDate = month_date.format(date);
        System.out.println("Month :" + formantedCoffDate);  //Mar 2016

    }


    private void getRHLeaveData(String empId, String fromdate, String todate) {

        if (Utilities.isNetworkAvailable(LeaveApplyActivity.this)) {

            final String GET_RHLEAVE_DATA = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetRHListForLeave/" + empId + "/" + fromdate + "/" + todate;
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_RHLEAVE_DATA, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    try {

                        rhLeaveDataList = new ArrayList<>();
                        HashMap<String, String> mapdata;

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                mapdata = new HashMap<>();
                                mapdata.put("HM_HOLIDAY_NAME", response.getJSONObject(i).getString("HM_HOLIDAY_NAME"));
                                mapdata.put("HM_HOLIDAY_DATE", response.getJSONObject(i).getString("HM_HOLIDAY_DATE"));
                                mapdata.put("DAY_NAME", response.getJSONObject(i).getString("DAY_NAME"));
                                mapdata.put("HM_HOLIDAY_DATE_1", response.getJSONObject(i).getString("HM_HOLIDAY_DATE_1"));
                                mapdata.put("HM_HOLIDAY_ID", response.getJSONObject(i).getString("HM_HOLIDAY_ID"));

                                rhLeaveDataList.add(mapdata);
                            }
                        }
                        Log.e("RH Leave Data Response", "onResponse: " + rhLeaveDataList.toString() + rhLeaveDataList.size());

                        rhLeaveAdapter = new ApplyRHLeaveAdapter(LeaveApplyActivity.this, rhLeaveDataList);
                        rhLeaveSpinner.setAdapter(rhLeaveAdapter);

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
            VolleySingleton.getInstance(LeaveApplyActivity.this).addToRequestQueue(jsonArrayRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }


    private void getCOLeaveData(String value_employee_id) {

        if (Utilities.isNetworkAvailable(LeaveApplyActivity.this)) {

            final String GET_COLIST_DATA = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCOListForLeave/" + value_employee_id;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_COLIST_DATA, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    coLeaveDataList = new ArrayList<>();
                    HashMap<String, String> mapdata;
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            mapdata = new HashMap<>();

                            try {
                                mapdata.put("COR_COMPOFF_DATE", response.getJSONObject(i).getString("COR_COMPOFF_DATE"));
                                mapdata.put("DAYS", response.getJSONObject(i).getString("DAYS"));
                                mapdata.put("DAY_NAME", response.getJSONObject(i).getString("DAY_NAME"));
                                mapdata.put("COR_ID", response.getJSONObject(i).getString("COR_ID"));
                                coLeaveDataList.add(mapdata);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            coLeaveAdapter = new ApplyCOLeaveAdapter(LeaveApplyActivity.this, coLeaveDataList);
                            coLeaveSpinner.setAdapter(coLeaveAdapter);
                        }
                    }

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
            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonArrayRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(LeaveApplyActivity.this).addToRequestQueue(jsonArrayRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    public void getBreakupLeave() {
        stringBuilder = new StringBuilder();
        for (int i = 0; i < arlData.size(); i++) {
            final HashMap<String, String> mapdata = arlData.get(i);
//            final HashMap<Integer, String> mapdata2= arlData1.get(i);

            String ear_attendance_status = mapdata.get("EAR_ATTENDANCE_STATUS");
            if (ear_attendance_status.equals(""))
                ear_attendance_status = "-";

            stringBuilder.append("@");
            stringBuilder.append(mapdata.get("ATTENDANCE_DATE").replace("/", "-"));
            stringBuilder.append("," + ear_attendance_status);
            stringBuilder.append("," + mapdata1.get(i).replace(" ", "-"));
            stringBuilder.append("," + mapdata2.get(i).trim());

            Log.e("String Builder", stringBuilder.toString());

        }
        deductDays = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_closeFrame:
                frameLayout.setVisibility(View.GONE);
                break;

            case R.id.btn_close:
                //finish();
                Log.e("Sorry", "Disturb");
                this.finish();
//                getBreakupLeave();
                break;

            case R.id.btn_fromDate_leave:
                btn_fromDate.setText("");
                calanderHRMS.datePicker(btn_fromDate);
                showButtonClick = false;
                handler2 = new Handler();
                runnable2 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btn_fromDate.getText().toString().trim();
                            String ToDate;
                            if (value_name.equals("CO")) {
                                btn_toDate.setText(FromDate);
                            }
                            ToDate = btn_toDate.getText().toString().trim();

                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
                            } else {
                                handler2.postDelayed(runnable2, 1000);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler2.postDelayed(runnable2, 1000);

                break;

            case R.id.btn_toDate_leave:
                btn_toDate.setText("");
                calanderHRMS.datePicker(btn_toDate);
                showButtonClick = false;
                handler2 = new Handler();
                runnable2 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btn_fromDate.getText().toString().trim();
                            String ToDate = btn_toDate.getText().toString().trim();

                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
                            } else {
                                handler2.postDelayed(runnable2, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler2.postDelayed(runnable2, 1000);
//                if(!showButtonClick) {
//                    handler = new Handler();
//                    rRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                counter++;
//                                leave_toDate = btn_toDate.getText().toString().replace("/", "-");
//                                String from_date1 = btn_fromDate.getText().toString().replace("/", "-");
//                                Log.e("Date", leave_toDate);
//
//                                if (!leave_toDate.equals("")) {
//
//                                    if (from_date1.compareTo(leave_toDate) > 0) {
//                                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than To Date");
//                                        handler.postDelayed(rRunnable, 2000);
//                                    }else {
//                                        getDatesData(value_employee_id, from_date1, leave_toDate, value_config_id);
//                                        showButtonClick = true;
//                                        Log.e("Runnable Method", "Runnable");
//                                    }
//
//                                } else {
//                                        //Utilities.showDialog(coordinatorLayout,"Please Select Comp Off Date");
//                                        // handler = new Handler();
//                                        handler.postDelayed(rRunnable, 2000);
//                                }
//
//
//                                Log.e("Counter", "" + counter);
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    };
//                    handler.postDelayed(rRunnable, 2000);
//                }
//
                break;

            case R.id.btn_showDates:
                refreshListData();
                break;

            case R.id.btn_apply:
                getBreakupLeave();
                Date from_date1 = null;
                Date to_date1 = null;
                String fromdate1 = btn_fromDate.getText().toString().replace("/", "-");
                String todate1 = btn_toDate.getText().toString().replace("/", "-");
                String reason = edt_reason.getText().toString().replace(" ", "_");
                String contact = edt_contactNo.getText().toString();
                String address = edt_address.getText().toString().replace(" ", "_");
//                String workOverHead = edt_workHandover.getText().toString().replace(" ","_");
                String workOverHead = "0";
//                if(workOverHead.equals(""))
//                    workOverHead = "-";

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    from_date1 = sdf.parse(fromdate1);
                    to_date1 = sdf.parse(todate1);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                if (contact.equals(""))
                    contact = "-";

                if (address.equals(""))
                    address = "-";

                String breakUpLeave = stringBuilder.toString();
                System.out.print("\nApply Data====" + stringBuilder.toString());

                System.out.print("\nBreak Up Leave Data===" + breakUpLeave + "\n");

                if (CoffHolidayID.equals("RH")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select RH");
                } else if (CoffHolidayID.equals("CO")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select CO");
                } else if (linear_result_compareDate.getVisibility() == View.VISIBLE) {
                    Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than To Date");
                } else if (reason.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason.");
                    edt_reason.setFocusable(true);
                }/*else if(contact.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Enter Contact Details");
                    edt_contactNo.setFocusable(true);
                }else if(address.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Enter Address.");
                    edt_address.setFocusable(true);
                }*/ else if (showButtonClick == false) {
                    Utilities.showDialog(coordinatorLayout, "Kindly Verify Date Details.");
                } else {
                    getsendLeaveData(value_employee_id, value_config_id, value_leave_type_year_fin_year, fromdate1, todate1, mySwitchValue, reason
                            , contact, address, workOverHead, stringBuilder.toString(), value_is_previous_year, value_leave_type_year,
                            value_leave_type_fin_year, value_leave_abbrevation);
                }
                if (showButtonClick) {
                    showButtonClick = false;
                }
                break;

            default:
                break;
        }
    }

    public void refreshListData() {
        from_date = btn_fromDate.getText().toString();
        to_date = btn_toDate.getText().toString();
        showButtonClick = true;

//                try {
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//                    from_date2 = sdf.parse(from_date);
//                    to_date2 = sdf.parse(to_date);
//                }catch (Exception e3){
//                    e3.printStackTrace();
//                }
        if (linear_result_compareDate.getVisibility() == View.VISIBLE) {
            Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
        } else {
            stringBuilder = new StringBuilder();
            DatesDataAsynTaskPost(value_employee_id, from_date, to_date, value_config_id);
//                    getDatesData(value_employee_id, from_date, to_date, value_config_id);
        }
    }

    private void getsendLeaveData(String employee_id, String config_id, String leave_type_year_fin_year, String fromdate,
                                  String todate, String mySwitchValue, String reason, String contact,
                                  String address, String workOverHead, String breakUpLeave, String is_previous_year,
                                  String leave_type_year, String leave_type_fin_year, String leave_abbrevation) {

        try {
            if (Utilities.isNetworkAvailable(this)) {
                arlData = new ArrayList<HashMap<String, String>>();
                sendLeaveRequestAsync = new LeaveApplyActivity.SendLeaveRequestAsync();
                sendLeaveRequestAsync.employee_id = employee_id;
                sendLeaveRequestAsync.config_id = config_id;
                sendLeaveRequestAsync.leave_type_year_fin_year = leave_type_year_fin_year;
                sendLeaveRequestAsync.fromdate = fromdate;
                sendLeaveRequestAsync.todate = todate;
                sendLeaveRequestAsync.mySwitchValue = mySwitchValue;
                sendLeaveRequestAsync.reason = reason;
                sendLeaveRequestAsync.contact = contact;
                sendLeaveRequestAsync.address = address;
                sendLeaveRequestAsync.workOverHead = workOverHead;
                sendLeaveRequestAsync.breakUpLeave = breakUpLeave;
                sendLeaveRequestAsync.is_previous_year = is_previous_year;
                sendLeaveRequestAsync.leave_type_year = leave_type_year;
                sendLeaveRequestAsync.leave_type_fin_year = leave_type_fin_year;
                sendLeaveRequestAsync.leave_abbrevation = leave_abbrevation;

                sendLeaveRequestAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getDatesData(String empid, String fromdate, String todate, String configid) {
        try {

            if (Utilities.isNetworkAvailable(this)) {
                arlData = new ArrayList<>();

                stringBuilder = new StringBuilder();
                datesDataAsynctask = new DatesDataAsynTask();
                datesDataAsynctask.empid = empid;
                datesDataAsynctask.fromdate = fromdate;
                datesDataAsynctask.todate = todate;
                datesDataAsynctask.configid = configid;

                datesDataAsynctask.execute();
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
        private LeaveApplyActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LeaveApplyActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
//                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null && clickListener != null) {
//                        clickListener.onClick(child, recyclerView.getChildPosition(child));
//                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onClick(child, recyclerView.getChildPosition(child));
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

    private class SendLeaveRequestAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employee_id, config_id, leave_type_year_fin_year, fromdate;
        String todate, mySwitchValue, reason, contact = "-";
        String address = "-", workOverHead = "0", breakUpLeave, is_previous_year;
        String leave_type_year, leave_type_fin_year, leave_abbrevation;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(LeaveApplyActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {

                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", employee_id);
                params_final.put("LeaveConfigID", config_id);
                params_final.put("LeaveTypeYearFinYear", leave_type_year_fin_year);
                params_final.put("FromDate", fromdate);
                params_final.put("ToDate", todate);
                params_final.put("isLTA", mySwitchValue);
                params_final.put("LeaveType", "0");
                params_final.put("Reason", reason);
                params_final.put("ContactNo", contact);
                params_final.put("Address", address);
                params_final.put("WorkHandOverTo", workOverHead);
                params_final.put("breakUpLeave", breakUpLeave);
                params_final.put("IS_PREVIOUS_YEAR", is_previous_year);
                params_final.put("LeaveTypeYear", leave_type_year);
                params_final.put("LeaveTypeFinYear", leave_type_fin_year);
                params_final.put("LeaveAbbrevation", leave_abbrevation);
                params_final.put("CoffHolidayID", CoffHolidayID);

                pm.put("objLeaveRequestInfo", params_final);
                Log.e("Leave Apply", pm.toString() + CoffHolidayID);

//                String GETRUNNINGBALANCE_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/sendLeaveRequest/"+employee_id+"/"+config_id+"/"+leave_type_year_fin_year+"/"+fromdate+"/"+todate+"/"+mySwitchValue+"/"+"0"+"/"+reason+"/"+contact+"/"+address+"/"+"0"+"/"+breakUpLeave+"/"+is_previous_year+"/"+leave_type_year+"/"+leave_type_fin_year+"/"+leave_abbrevation+"/"+"0";

                String GETRUNNINGBALANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveLeaveRequest";
                System.out.println("GETRUNNINGBALANCE_URL====" + GETRUNNINGBALANCE_URL);

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, GETRUNNINGBALANCE_URL, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    final String result = response.getString("SaveLeaveRequestResult");

                                    Log.e("Result Value", "" + result);
                                    handler = new Handler();
                                    rRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            try {

                                                if (result != "") {
                                                    pDialog.dismiss();
                                                    String result1 = "", message = "";
                                                    String[] str;

                                                    if (!(result.substring(result.length() - 1).equals(","))) {
                                                        str = result.split(",");
                                                        result1 = str[0];
                                                        message = str[1];
                                                    } else {
                                                        str = result.split(",");
                                                        result1 = str[0];
                                                    }

                                                    Log.e("Result Message", "" + message);
                                                    Log.e("Result Result1", "" + result1);

                                                    result1 = result1.replaceAll("^\"+|\"+$", " ").trim();

                                                    int res = Integer.parseInt(result1);

                                                    if (res == -1) {
                                                        Utilities.showDialog(coordinatorLayout, "Leave/OD/Punch request already pending for same date.");
                                                    } else if (res == -2) {
                                                        Utilities.showDialog(coordinatorLayout, "You are not eligible for this leave type.");
                                                    } else if (res == -3) {
                                                        Utilities.showDialog(coordinatorLayout, "ESI Leave are not applicable.");
                                                    } else if (res == -4) {
                                                        Utilities.showDialog(coordinatorLayout, "You have exceed max limit of ESI Leave.");
                                                    } else if (res == -5) {
                                                        Utilities.showDialog(coordinatorLayout, "You are not eligible for this leave type.");
                                                    } else if (res == -6) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request leave less than " + message + " days.");
                                                    } else if (res == -7) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " days.");
                                                    } else if (res == -8) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " time in a year.");
                                                    } else if (res == -9) {
                                                        Utilities.showDialog(coordinatorLayout, "You have to intimate atleast " + message + " days in advance.");
                                                    } else if (res == -10) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " days in back.");
                                                    } else if (res == -11) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " time in job tenure.");
                                                    } else if (res == -12) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not clubbed with other leave.");
                                                    } else if (res == -13) {
                                                        Utilities.showDialog(coordinatorLayout, "This leave can only be clubbed with " + message + " leave.");
                                                    } else if (res == -14) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request leave more than remaining.");
                                                    } else if (res == -15) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request LOP Leave more " + message + " days.");
                                                    } else if (res == -16) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request Advance Leave more " + message + " days.");
                                                    } else if (res == -17) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request Leave as LOP and Advance both consumed.");
                                                    } else if (res == -18) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request Leave for previous payroll cycle.");
                                                    } else if (res == -19) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " time in a month.");
                                                    } else if (res == -20) {
                                                        Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " Days in month.");
                                                    } else if (res == -21) {
                                                        Utilities.showDialog(coordinatorLayout, "You have to select " + message + " Days C-Off from List.");
                                                    } else if (res == -25) {
                                                        Utilities.showDialog(coordinatorLayout, "Policy not define for Requested Leave Period.");
                                                    } else if (res == -26) {
                                                        Utilities.showDialog(coordinatorLayout, "Leave date can not be less than Leave Policy Date Range.");
                                                    }

                                                    if (res > 0) {
                                                        Utilities.showDialog(coordinatorLayout, "Leave Request sent for approval.");

                                                        Handler handler = new Handler();
                                                        handler.postDelayed(new Runnable() {
                                                            public void run() {
                                                                finish();
                                                            }
                                                        }, 1000 * 4);
                                                    } else if (res == 0) {
                                                        Utilities.showDialog(coordinatorLayout, "Error during saving the Leave Request." + message);
                                                        System.out.print("Error " + message);
                                                    }
                                                    Log.e("Reuslt Post", "" + result);


                                                }
                                            } catch (Exception ex1) {
                                                ex1.printStackTrace();
                                            }

                                        }
                                    };

                                    handler.postDelayed(rRunnable, 2000);


                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                            error.getMessage();
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
//                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);


//                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
//                String json = jParser.makeHttpRequest(
//                        GETRUNNINGBALANCE_URL, "GET");
//
//                if (json != null) {
//                    Log.d("JSON result", json.toString());
//
//                    return json;
//                }

                return null;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


//        @Override
//        protected void onPostExecute(String result) {
//            // execution of result of Long time consuming operation
//            //finalResult.setText(result);
//
//            Log.e("Reuslt Out Post",""+result);
//            if (pDialog != null && pDialog.isShowing()) {
//
//                try{
//                    if(result != null) {
//                        pDialog.dismiss();
//                        String message = result.split(",")[1];
//                        String result1 = result.split(",")[0];
//
//                        result1 = result1.replaceAll("^\"+|\"+$", " ").trim();
//
//                        int res = Integer.parseInt(result1);
//
//                        if (res == -1) {
//                            Utilities.showDialog(coordinatorLayout, "Leave/OD/Punch request already pending for same date.");
//                        } else if (res == -2) {
//                            Utilities.showDialog(coordinatorLayout, "You are not eligible for this leave type.");
//                        } else if (res == -3) {
//                            Utilities.showDialog(coordinatorLayout, "ESI Leave are not applicable.");
//                        } else if (res == -4) {
//                            Utilities.showDialog(coordinatorLayout, "You have exceed max limit of ESI Leave.");
//                        } else if (res == -5) {
//                            Utilities.showDialog(coordinatorLayout, "You are not eligible for this leave type.");
//                        } else if (res == -6) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request leave less than " + message + " days.");
//                        } else if (res == -7) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " days.");
//                        } else if (res == -8) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " time in a year.");
//                        } else if (res == -9) {
//                            Utilities.showDialog(coordinatorLayout, "You have to intimate atleast " + message + " days in advance.");
//                        } else if (res == -10) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " days in back.");
//                        } else if (res == -11) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " time in job tenure.");
//                        } else if (res == -12) {
//                            Utilities.showDialog(coordinatorLayout, "You can not clubbed with other leave.");
//                        } else if (res == -13) {
//                            Utilities.showDialog(coordinatorLayout, "This leave can only be clubbed with " + message + " leave.");
//                        } else if (res == -14) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than remaining.");
//                        } else if (res == -15) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request LOP Leave more " + message + " days.");
//                        } else if (res == -16) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request Advance Leave more " + message + " days.");
//                        } else if (res == -17) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request Leave as LOP and Advance both consumed.");
//                        } else if (res == -18) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request Leave for previous payroll cycle.");
//                        } else if (res == -19) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " time in a month.");
//                        } else if (res == -20) {
//                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " Days in month.");
//                        } else if (res == -21) {
//                            Utilities.showDialog(coordinatorLayout, "You have to select " + message + " Days C-Off from List.");
//                        } else if (res == -25) {
//                            Utilities.showDialog(coordinatorLayout, "Policy not define for Requested Leave Period.");
//                        } else if (res == -26) {
//                            Utilities.showDialog(coordinatorLayout, "Leave date can not be less than Leave Policy Date Range.");
//                        }
//
//                        if (res > 0) {
//                            Utilities.showDialog(coordinatorLayout, "Leave Request sent for approval.");
////                        edt_reason.setText("");
////                        edt_contactNo.setText("");
////                        edt_address.setText("");
////                        edt_workHandover.setText("");
////                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
////                        Date date = new Date();
////
////                        btn_fromDate.setText(simpleDateFormat.format(date));
////                        btn_toDate.setText(simpleDateFormat.format(date));
//
//
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                public void run() {
//                                    finish();
//                                }
//                            }, 1000 * 4);
//
//
//                        } else if (res == 0) {
//                            Utilities.showDialog(coordinatorLayout, "Error during saving the Leave Request." + message);
//                            System.out.print("Error " + message);
//                        }
//
//
//                        Log.e("Reuslt Post", "" + result);
//                    }else {
//                        System.out.print("Error " + "Else Part");
//                        Log.e("Reuslt Else Post", "" + result);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }


        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    private class DatesDataAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        //private double lati;
        String token;
        String fromdate, todate, configid;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(LeaveApplyActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {


            try {
                if (showButtonClick) {
                    handler.removeCallbacks(rRunnable);
                    counter = 0;
                    Log.e("Do in BackGround", "Backgrond");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

                // String GETRUNNINGBALANCE_URL = "http://savvyshippingsoftware.com/SavvyMobileNew/SavvyMobileService.svc/GetLeaveBreakupForApplication/"+empid+"/"+fromdate+"/"+todate+"/"+configid;

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETRUNNINGBALANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveBreakupForApplication/" + empid + "/" + fromdate + "/" + todate + "/" + configid;

                System.out.println("GETRUNNINGBALANCE_URL====" + GETRUNNINGBALANCE_URL);
                JSONParser jParser = new JSONParser(LeaveApplyActivity.this); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        GETRUNNINGBALANCE_URL, "GET");

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
                try {

                    // stringBuilder.append("<DATASET>");
                    HashMap<String, String> datesDatamap;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            datesDatamap = new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            String attendance_date = explrObject.getString("ATTENDANCE_DATE");
                            String deduction = explrObject.getString("DEDUCTION");
                            String deduction_days = explrObject.getString("DEDUCTION_DAYS");

                            String ear_attendance_date = explrObject.getString("EAR_ATTENDANCE_DATE");
                            String ear_employee_id = explrObject.getString("EAR_EMPLOYEE_ID");

                            String ear_attendance_status = explrObject.getString("EAR_ATTENDANCE_STATUS");
//                            if(ear_attendance_status.equals(""))
//                                ear_attendance_status = "-";

//                            stringBuilder.append("@");
//                            stringBuilder.append(explrObject.getString("ATTENDANCE_DATE").replace("/","-"));
//                            stringBuilder.append(","+ear_attendance_status);
//                            stringBuilder.append(","+explrObject.getString("DEDUCTION").replace(" ","-"));
//                            stringBuilder.append(","+explrObject.getString("DEDUCTION_DAYS").trim());

                            // deductDays.add(i,explrObject.getString("DEDUCTION_DAYS").trim());
                            mapdata1.put(i, explrObject.getString("DEDUCTION").replace(" ", "-"));
                            mapdata2.put(i, explrObject.getString("DEDUCTION_DAYS").trim());

                            //System.out.println("holidayName===" + holidayName);
                            datesDatamap.put("ATTENDANCE_DATE", attendance_date);
                            datesDatamap.put("DEDUCTION", deduction);
                            datesDatamap.put("DEDUCTION_DAYS", deduction_days);
                            datesDatamap.put("EAR_ATTENDANCE_DATE", ear_attendance_date);
                            datesDatamap.put("EAR_EMPLOYEE_ID", ear_employee_id);
                            datesDatamap.put("EAR_ATTENDANCE_STATUS", ear_attendance_status);

                            arlData.add(datesDatamap);

                        }

                        //stringBuilder.append("</DATASET>");
                        System.out.println("LeaveBalanceArray===" + arlData);
                        System.out.println("\n\nString Builder213=====" + stringBuilder.toString());
                        //DisplayHolidayList(arlData);

                        if (showButtonClick) {
//                            myDialog(arlData);
                            frameLayout.setVisibility(View.VISIBLE);
                            ShowDataListAdapter mAdapter = new ShowDataListAdapter(LeaveApplyActivity.this, arlData);
                            recylerLeaveactivity.setAdapter(mAdapter);
                        }

                        /*mAdapter = new LeaveBalanceListAdapter(getActivity(), arlData);
                        recyclerView.setAdapter(mAdapter);*/
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

    public void DatesDataAsynTaskPost(String empid, String fromdate, String todate, String configid) {
        try {
            if (Utilities.isNetworkAvailable(LeaveApplyActivity.this)) {
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", empid);
                param.put("FROM_DATE", fromdate);
                param.put("TO_DATE", todate);
                param.put("LEAVE_CONFIG_ID", configid);
                Log.e("RequestData", "Json->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveBreakupForApplicationPost";

                RequestQueue requestQueue = Volley.newRequestQueue(LeaveApplyActivity.this);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    arlData.clear();
                                    HashMap<String, String> datesDatamap;
                                    JSONArray jsonArray = response.getJSONArray("GetLeaveBreakupForApplicationPostResult");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        datesDatamap = new HashMap<>();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String attendance_date = jsonObject.getString("ATTENDANCE_DATE");
                                        String deduction = jsonObject.getString("DEDUCTION");
                                        String deduction_days = jsonObject.getString("DEDUCTION_DAYS");
                                        String ear_attendance_date = jsonObject.getString("EAR_ATTENDANCE_DATE");
                                        String ear_employee_id = jsonObject.getString("EAR_EMPLOYEE_ID");
                                        String ear_attendance_status = jsonObject.getString("EAR_ATTENDANCE_STATUS");

                                        mapdata1.put(i, jsonObject.getString("DEDUCTION").replace(" ", "-"));
                                        mapdata2.put(i, jsonObject.getString("DEDUCTION_DAYS").trim());

                                        datesDatamap.put("ATTENDANCE_DATE", attendance_date);
                                        datesDatamap.put("DEDUCTION", deduction);
                                        datesDatamap.put("DEDUCTION_DAYS", deduction_days);
                                        datesDatamap.put("EAR_ATTENDANCE_DATE", ear_attendance_date);
                                        datesDatamap.put("EAR_EMPLOYEE_ID", ear_employee_id);
                                        datesDatamap.put("EAR_ATTENDANCE_STATUS", ear_attendance_status);

                                        arlData.add(datesDatamap);

                                    }

                                    if (jsonArray.length() > 0) {
                                        if (showButtonClick) {
                                            frameLayout.setVisibility(View.VISIBLE);
                                            ShowDataListAdapter mAdapter = new ShowDataListAdapter(LeaveApplyActivity.this, arlData);
                                            recylerLeaveactivity.setAdapter(mAdapter);
                                        }
                                    } else {
                                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
                jsonRequest.setRetryPolicy(retryPolicy);
                requestQueue.add(jsonRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId", "8");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu != null) {
            menu.findItem(R.id.home_dashboard).setVisible(false);
        }
        return true;
    }

    public void getCompareDate(String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(LeaveApplyActivity.this)) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";

                RequestQueue requestQueue = Volley.newRequestQueue(LeaveApplyActivity.this);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        txt_result_compareDate.setText("From Date should be less than or equal To Date!");
                                        linear_result_compareDate.setVisibility(View.VISIBLE);
                                    } else {
                                        linear_result_compareDate.setVisibility(View.GONE);
                                        if (value_name.equals("CO")) {
                                            refreshListData();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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
                RetryPolicy policy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonRequest.setRetryPolicy(policy);
                requestQueue.add(jsonRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
