package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.calendar.TeamAttendanceCalendarAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TeamAttendanceCalendar extends BaseFragment {

    TeamAttendanceCalendar.GetTeamAttendanceCalendarAsync getAttendanceCalendarAsync;

    TeamAttendanceCalendarAdapter mAdapter;
    ArrayList<HashMap<String, String>> arlData;
    ArrayList<HashMap<String, String>> arlRequestStatusData;
    ArrayList<String> arrayList;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "";
    CalanderHRMS cal;
    String EMPLOYEE_CODE = "";
    SharedPreferences.Editor shareEditor;
    LinearLayout Layout_recycler;
    //TeamAttendanceCalendar.GetAttendanceCalendarAsync getAttendanceCalendarAsync;

    Button btn_fromDate, btn_toDate, btn_submit;
    CustomTextView txtFromDate, txtToDate, txtEmployeeCode;
    //    EditText edt_employeeCode;
    LinearLayout linearCompareDate;
    CustomTextView txtResulDate;

    AutoCompleteTextView avEmployeeCode;

    Handler handler, handler1;
    Runnable runnable, runnable1;
    private List<HashMap<String, String>> teamArrayList = new ArrayList<>();
    private List<String> teamArray = new ArrayList<>();
    private String GroupId;

    private String TEAM_EMPLOYEE_CODE = "";
    private String TEAM_EMPLOYEE_ID = "";
    private String TEAM_EMPLOYEE_NAME = "";
    ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        GroupId = (shared.getString("GroupId", ""));

        shareData = getActivity().getSharedPreferences("EMPLOYEE_ID", MODE_PRIVATE);
        String EmpId = shareData.getString("EMP_CODE", "");
        Log.e("Employee 1Id", EmpId);
        arlData = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team_attendance_calendar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        cal = new CalanderHRMS(getActivity());

        btn_fromDate = getActivity().findViewById(R.id.btn_team_attendanceCalFromDate);
        btn_toDate = getActivity().findViewById(R.id.btn_team_attendanceCalToDate);
        btn_submit = getActivity().findViewById(R.id.btn_team_attendanceCal_submit);
//        edt_employeeCode = getActivity().findViewById(R.id.edt_employee_code);

        txtFromDate = getActivity().findViewById(R.id.txtFromDate);
        txtToDate = getActivity().findViewById(R.id.txtToDate);
        txtEmployeeCode = getActivity().findViewById(R.id.txtEmployeeCode);

        txtResulDate = getActivity().findViewById(R.id.txtResulDate);
        linearCompareDate = getActivity().findViewById(R.id.linearCompareDate);

        linearCompareDate.setVisibility(View.GONE);

        String fromdate = "From Date " + "<font color=\"#E72A02\"><bold>" + " *" + "</bold></font>";
        String todate = "To Date " + "<font color=\"#E72A02\"><bold>" + " *" + "</bold></font>";
        final String employeeid = "Employee Code " + "<font color=\"#E72A02\"><bold>" + " *" + "</bold></font>";

        txtFromDate.setText(Html.fromHtml(fromdate));
        txtToDate.setText(Html.fromHtml(todate));
        txtEmployeeCode.setText(Html.fromHtml(employeeid));

        SimpleDateFormat simpleDateFormatFrom = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        Date dateFrom = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        btn_fromDate.setText("01/" + simpleDateFormatFrom.format(dateFrom));
        btn_toDate.setText(simpleDateFormat.format(date));

        recyclerView = getActivity().findViewById(R.id.team_attendanceCal_Recycler);
        mAdapter = new TeamAttendanceCalendarAdapter(getActivity(), coordinatorLayout, arlData);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btn_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fromDate.setText("");
                cal.datePicker(btn_fromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btn_fromDate.getText().toString().trim();
                            String ToDate = btn_toDate.getText().toString().trim();

                            if ((!FromDate.equals("")) && (!ToDate.equals(""))) {
                                getCompareDateResult(FromDate, ToDate);
                            } else {
                                handler.postDelayed(runnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });
        btn_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btn_toDate.setText("");
                    cal.datePicker(btn_toDate);

                    try {
                        handler1 = new Handler();
                        runnable1 = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String FromDate = btn_fromDate.getText().toString().trim();
                                    String ToDate = btn_toDate.getText().toString().trim();

                                    if ((!FromDate.equals("")) && (!ToDate.equals(""))) {
                                        getCompareDateResult(FromDate, ToDate);
                                    } else {
                                        handler1.postDelayed(runnable1, 1000);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        handler1.postDelayed(runnable1, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edtEmployeeId = TEAM_EMPLOYEE_ID;/*edt_employeeCode.getText().toString().trim();*/
                String from_date = btn_fromDate.getText().toString();
                String to_date = btn_toDate.getText().toString();

                if (edtEmployeeId.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Employee Code");
                } else if (from_date.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter From Date");
                } else if (to_date.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter To Date");
                } else if (linearCompareDate.getVisibility() == View.VISIBLE) {
                    Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                } else {
//                    getAttendanceCalendar(empoyeeId,edtEmployeeId,from_date,to_date);
                    if (teamArray.contains(avEmployeeCode.getText().toString())) {
                        getAttendanceTeamCalendarPost(empoyeeId, edtEmployeeId, from_date, to_date);
                    } else {
                        Utilities.showDialog(coordinatorLayout, "Employee code is invalid !");
                    }

                }
            }
        });

        setAutoCompleteTextData();
        avEmployeeCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = arrayAdapter.getItem(position);
                if (value != null) {
                    TEAM_EMPLOYEE_CODE = teamArrayList.get(teamArray.indexOf(value)).get("EMPLOYEE_CODE");
                    TEAM_EMPLOYEE_ID = teamArrayList.get(teamArray.indexOf(value)).get("EMPLOYEE_ID");
                    TEAM_EMPLOYEE_NAME = teamArrayList.get(teamArray.indexOf(value)).get("EMPLOYEE_NAME");
                }
            }
        });
        avEmployeeCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().trim().equals("")) {
                    TEAM_EMPLOYEE_ID = "";
                }
            }
        });
    }

    private void setAutoCompleteTextData() {
        avEmployeeCode = getActivity().findViewById(R.id.edt_employee_code);
        allTeamEmployeeCodeDetail();
    }

    private void getAttendanceCalendar(String empoyeeId, String edtEmployeeId, String from_date, String to_date) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<HashMap<String, String>>();
                getAttendanceCalendarAsync = new TeamAttendanceCalendar.GetTeamAttendanceCalendarAsync();
                getAttendanceCalendarAsync.empoyeeId = empoyeeId;
                getAttendanceCalendarAsync.edtEmployeeId = edtEmployeeId;
                getAttendanceCalendarAsync.from_date = from_date;
                getAttendanceCalendarAsync.to_date = to_date;
                getAttendanceCalendarAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetTeamAttendanceCalendarAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empoyeeId, from_date, to_date, edtEmployeeId;

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
                String ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeTeamAttendanceCalendar/" + empoyeeId + "/" + edtEmployeeId + "/" + from_date + "/" + to_date;

                System.out.println("ATTENDANCE_URL====" + ATTENDANCE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        ATTENDANCE_URL, "GET");

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
                    HashMap<String, String> punchStatusmap;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            punchStatusmap = new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);

                            String att_date = explrObject.getString("DISPLAY_ATTENDANCE_DATE");                           //1
                            String timeIn = explrObject.getString("EAR_TIME_IN");                           //1
                            String timeOut = explrObject.getString("EAR_TIME_OUT");                           //1
                            String day_name = explrObject.getString("DAY_NAME");                         //6
                            String total_hours = explrObject.getString("TOTAL_HOURS");                     //7
                            String status = explrObject.getString("AA_ATTENDANCE_DESCRIPTION");                     //7

                            String emp_id = explrObject.getString("EAR_EMPLOYEE_ID");
                            if (emp_id.equals("")) {
                                Utilities.showDialog(coordinatorLayout, "Please Enter Valid Employee Code");
                            } else {
                                punchStatusmap.put("DISPLAY_ATTENDANCE_DATE", att_date);
                                punchStatusmap.put("EAR_TIME_IN", timeIn);
                                punchStatusmap.put("EAR_TIME_OUT", timeOut);
                                punchStatusmap.put("DAY_NAME", day_name);
                                punchStatusmap.put("TOTAL_HOURS", total_hours);
                                punchStatusmap.put("AA_ATTENDANCE_DESCRIPTION", status);

                                arlData.add(punchStatusmap);
                            }
                        }
                        System.out.println("Array===" + arlData);

                        //DisplayHolidayList(arlData);
                        mAdapter = new TeamAttendanceCalendarAdapter(getActivity(), coordinatorLayout, arlData);
                        recyclerView.setAdapter(mAdapter);
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

    public void getAttendanceTeamCalendarPost(String empId, String empCode, String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", empId);
                param.put("EMPLOYEE_CODE", TEAM_EMPLOYEE_CODE);
                param.put("FROM_DATE", fromDate);
                param.put("TO_DATE", toDate);
                Log.e("RequestData", "Json->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeTeamAttendanceCalendarPost";
                Log.e("URL", "url->" + url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    arlData.clear();
                                    JSONArray jsonArray = response.getJSONArray("GetEmployeeTeamAttendanceCalendarPostResult");
                                    HashMap<String, String> hashMap;

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        hashMap = new HashMap<>();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String att_date = jsonObject.getString("DISPLAY_ATTENDANCE_DATE");                           //1
                                        String timeIn = jsonObject.getString("EAR_TIME_IN");                           //1
                                        String timeOut = jsonObject.getString("EAR_TIME_OUT");                           //1
                                        String day_name = jsonObject.getString("DAY_NAME");                         //6
                                        String total_hours = jsonObject.getString("TOTAL_HOURS");                     //7
                                        String status = jsonObject.getString("AA_ATTENDANCE_DESCRIPTION");                     //7

                                        String emp_id = jsonObject.getString("EAR_EMPLOYEE_ID");

                                        if (emp_id.equals("")) {
                                            Utilities.showDialog(coordinatorLayout, "Please Enter Valid Employee Code");
                                        } else {
                                            hashMap.put("DISPLAY_ATTENDANCE_DATE", att_date);
                                            hashMap.put("EAR_TIME_IN", timeIn);
                                            hashMap.put("EAR_TIME_OUT", timeOut);
                                            hashMap.put("DAY_NAME", day_name);
                                            hashMap.put("TOTAL_HOURS", total_hours);
                                            hashMap.put("AA_ATTENDANCE_DESCRIPTION", status);

                                            arlData.add(hashMap);
                                        }
                                    }
                                    if (jsonArray.length() > 0) {
                                        mAdapter = new TeamAttendanceCalendarAdapter(getActivity(), coordinatorLayout, arlData);
                                        recyclerView.setAdapter(mAdapter);
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
                        error.printStackTrace();
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
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCompareDateResult(String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);
                Log.e("RequestData", "Json->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                Log.e("Url", "url->" + url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        txtResulDate.setText("From Date should be less than or equal To Date!");
                                        linearCompareDate.setVisibility(View.VISIBLE);
                                    } else {
                                        linearCompareDate.setVisibility(View.GONE);
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
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void allTeamEmployeeCodeDetail() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();
                param.put("GROUP_ID", GroupId);
                param.put("EMPLOYEE_ID", empoyeeId);
                Log.e("RequestData", "Json->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMeMyTeamPost";
                Log.e("URL", "url->" + url);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("GetMeMyTeamPostResult");
                            HashMap<String, String> hashMap;
                            teamArray = new ArrayList<>();
                            teamArrayList = new ArrayList<>();
                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    hashMap = new HashMap<>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);
                                    hashMap.put("EMPLOYEE_CODE", explrObject.getString("EMPLOYEE_CODE"));
                                    hashMap.put("EMPLOYEE_ID", explrObject.getString("EMPLOYEE_ID"));
                                    hashMap.put("EMPLOYEE_NAME", explrObject.getString("EMPLOYEE_NAME"));
                                    teamArray.add(hashMap.put("EMPLOYEE_NAME", explrObject.getString("EMPLOYEE_NAME")));
                                    teamArrayList.add(hashMap);
                                }
                            }

                            arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, teamArray) {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);
                                    ((TextView) v).setTextSize(12);
                                    ((TextView) v).setGravity(Gravity.START);
                                    return v;
                                }
                            };

                            avEmployeeCode.setAdapter(arrayAdapter);

                        } catch (Exception e) {

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error",error.getMessage());
                        error.printStackTrace();
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
