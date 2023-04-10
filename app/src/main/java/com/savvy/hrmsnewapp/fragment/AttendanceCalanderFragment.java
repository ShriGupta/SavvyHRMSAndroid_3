package com.savvy.hrmsnewapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.savvy.hrmsnewapp.calendar.AttendanceCalendarAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class AttendanceCalanderFragment extends BaseFragment {

    AttendanceCalendarAdapter mAdapter;
    ArrayList<HashMap<String, String>> arlData = new ArrayList<>();
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    LinearLayout linearCalendarLayout1;
    ImageView imgLine;
    String empoyeeId = "";
    CalanderHRMS cal;
    LinearLayout Layout_recycler;
    //  AttendanceCalanderFragment.GetAttendanceCalendarAsync getAttendanceCalendarAsync;

    Button btn_fromDate, btn_toDate, btn_submit;
    CustomTextView txtFromDate, txtToDate;
    LinearLayout linearResult;
    CustomTextView txtDateResult;

    String xmlData = "";
    String token_no = "";

    Handler handler, handler1;
    Runnable runnable, runnable1l;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_calander, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        cal = new CalanderHRMS(getActivity());

        btn_fromDate = getActivity().findViewById(R.id.btn_attendanceCalFromDate);
        btn_toDate = getActivity().findViewById(R.id.btn_attendanceCalToDate);
        btn_submit = getActivity().findViewById(R.id.btn_attendanceCal_submit);

        txtFromDate = getActivity().findViewById(R.id.txtFromDate);
        txtToDate = getActivity().findViewById(R.id.txtToDate);

        txtDateResult = getActivity().findViewById(R.id.txtDateResult);
        linearResult = getActivity().findViewById(R.id.linearResult);

        linearCalendarLayout1 = getActivity().findViewById(R.id.mainCalendarLayout);
        imgLine = getActivity().findViewById(R.id.img_line1);

        linearCalendarLayout1.setVisibility(View.INVISIBLE);
        imgLine.setVisibility(View.INVISIBLE);

        linearResult.setVisibility(View.GONE);

        String fromdate = "From Date " + "<font color=\"#E72A02\"><bold>" + " *" + "</bold></font>";
        String todate = "To Date " + "<font color=\"#E72A02\"><bold>" + " *" + "</bold></font>";
        txtFromDate.setText(Html.fromHtml(fromdate));
        txtToDate.setText(Html.fromHtml(todate));

        SimpleDateFormat simpleDateFormatFrom = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        Date dateFrom = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        btn_fromDate.setText("01/" + simpleDateFormatFrom.format(dateFrom));
        btn_toDate.setText(simpleDateFormat.format(date));

        recyclerView = getActivity().findViewById(R.id.attendanceCal_Recycler);
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
                            String FROM_DATE = btn_fromDate.getText().toString().trim();
                            String TO_DATE = btn_toDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                compareDateAttendance(FROM_DATE, TO_DATE);
                            } else {
                                handler.postDelayed(runnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GetFuture Method ", e.getMessage());
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });
        btn_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_toDate.setText("");
                cal.datePicker(btn_toDate);

                handler1 = new Handler();

                runnable1l = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FROM_DATE = btn_fromDate.getText().toString().trim();
                            String TO_DATE = btn_toDate.getText().toString().trim();
                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                compareDateAttendance(FROM_DATE, TO_DATE);
                            } else {
                                handler1.postDelayed(runnable1l, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GetFuture Method ", e.getMessage());
                        }
                    }
                };
                handler1.postDelayed(runnable1l, 1000);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arlData.clear();
                String from_date = btn_fromDate.getText().toString();
                String to_date = btn_toDate.getText().toString();

                if (from_date.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter From Date");
                } else if (to_date.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter To Date");
                } else if (linearResult.getVisibility() == View.VISIBLE) {
                    Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                } else {
                    linearCalendarLayout1.setVisibility(View.VISIBLE);
                    imgLine.setVisibility(View.VISIBLE);

//                    getAttendanceCalendar(empoyeeId,from_date,to_date);
                    getAttendanceCalendarPost(empoyeeId, from_date, to_date);
                }

            }
        });

    }

    /*private void getAttendanceCalendar(String empoyeeId, String from_date, String to_date) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<HashMap<String, String>>();
                getAttendanceCalendarAsync = new AttendanceCalanderFragment.GetAttendanceCalendarAsync();
                getAttendanceCalendarAsync.empoyeeId = empoyeeId;
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

    private class GetAttendanceCalendarAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empoyeeId, from_date, to_date;

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
                String ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeAttendanceCalendar/" + empoyeeId + "/" + from_date + "/" + to_date;

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
                            String att_date1 = explrObject.getString("EAR_ATTENDANCE_DATE");                     //7

                            punchStatusmap.put("DISPLAY_ATTENDANCE_DATE", att_date);
                            punchStatusmap.put("EAR_TIME_IN", timeIn);
                            punchStatusmap.put("EAR_TIME_OUT", timeOut);
                            punchStatusmap.put("DAY_NAME", day_name);
                            punchStatusmap.put("TOTAL_HOURS", total_hours);
                            punchStatusmap.put("AA_ATTENDANCE_DESCRIPTION", status);
                            punchStatusmap.put("EAR_ATTENDANCE_DATE", att_date1);

                            arlData.add(punchStatusmap);
                        }
                        System.out.println("Array===" + arlData);

                        //DisplayHolidayList(arlData);
                        mAdapter = new AttendanceCalendarAdapter(getActivity(), coordinatorLayout, arlData);
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
    }*/

    public void getAttendanceCalendarPost(String empId, String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();
                param.put("EMPLOYEE_ID", empId);
                param.put("FROM_DATE", fromDate);
                param.put("TO_DATE", toDate);
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeAttendanceCalendarPost";
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("GetEmployeeAttendanceCalendarPostResult");
                                    HashMap<String, String> hashMap;
                                    Log.d(TAG, "onResponse: JSon Array Length " + jsonArray.length());

                                    if (jsonArray.length() > 0) {

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            hashMap = new HashMap<>();
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String att_date = jsonObject.getString("DISPLAY_ATTENDANCE_DATE");                           //1
                                            String timeIn = jsonObject.getString("EAR_TIME_IN");                           //1
                                            String timeOut = jsonObject.getString("EAR_TIME_OUT");                           //1
                                            String day_name = jsonObject.getString("DAY_NAME");                         //6
                                            String total_hours = jsonObject.getString("TOTAL_HOURS");                     //7
                                            String status = jsonObject.getString("AA_ATTENDANCE_DESCRIPTION");                     //7
                                            String att_date1 = jsonObject.getString("EAR_ATTENDANCE_DATE");                     //7
                                            hashMap.put("DISPLAY_ATTENDANCE_DATE", att_date);
                                            hashMap.put("EAR_TIME_IN", timeIn);
                                            hashMap.put("EAR_TIME_OUT", timeOut);
                                            hashMap.put("DAY_NAME", day_name);
                                            hashMap.put("TOTAL_HOURS", total_hours);
                                            hashMap.put("AA_ATTENDANCE_DESCRIPTION", status);
                                            hashMap.put("EAR_ATTENDANCE_DATE", att_date1);

                                            arlData.add(hashMap);
                                        }
                                        mAdapter = new AttendanceCalendarAdapter(getActivity(), coordinatorLayout, arlData);
                                        recyclerView.setAdapter(mAdapter);
                                        Log.d(TAG, "onResponse:Array list size " + arlData.size());
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

    public void compareDateAttendance(String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);
                Log.e("RequestData", "Json->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                Log.e("Url", "Urls->" + url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        txtDateResult.setText("From Date should be less than or equal To Date!");
                                        linearResult.setVisibility(View.VISIBLE);
                                    } else {
                                        linearResult.setVisibility(View.GONE);
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
