package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

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
import com.savvy.hrmsnewapp.adapter.AttCalendarMMTAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class AttendanceCalendarMMTFragment extends BaseFragment implements View.OnClickListener {

    CoordinatorLayout coordinatorLayout;
    AttCalendarMMTAdapter attCalendarMMTAdapter;
    Button attFromDate, attToDate, getCalendar;
    LinearLayout compareDatelayout;
    CustomTextView compareDateTextView, DataNotFound;
    RecyclerView recyclerView;
    Handler handler;
    Runnable rRunnable;
    String FROM_DATE = "", TO_DATE = "";
    CalanderHRMS calanderHRMS;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String empId;
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> arrayList;
    String firstDate = null, lastDate = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empId = (shared.getString("EmpoyeeId", ""));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        firstDate = df.format(calendar.getTime());
        lastDate = getLastDayOfTheMonth(firstDate);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_calendar_mmt, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        attFromDate = getActivity().findViewById(R.id.attCalendar_FromDate);
        attToDate = getActivity().findViewById(R.id.attCalendar_ToDate);
        getCalendar = getActivity().findViewById(R.id.getCalendarButton);
        compareDatelayout = getActivity().findViewById(R.id.attCalendarCompareDateLayout);
        compareDateTextView = getActivity().findViewById(R.id.attCalendarCompareDateTextView);
        DataNotFound = getActivity().findViewById(R.id.attCalendarTxtDataNotFound);
        recyclerView = getActivity().findViewById(R.id.attCalendarRecyclerView);

        RecyclerView.LayoutManager statusLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(statusLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (!(firstDate.equals("") && lastDate.equals(""))) {
            attFromDate.setText(firstDate);
            attToDate.setText(lastDate);
            getCalendarData(empId, firstDate, lastDate);
        }
        attFromDate.setOnClickListener(this);
        attToDate.setOnClickListener(this);
        getCalendar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attCalendar_FromDate:

                attFromDate.setText("");
                calanderHRMS.datePicker(attFromDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = attFromDate.getText().toString().trim();
                            TO_DATE = attToDate.getText().toString().trim();

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

            case R.id.attCalendar_ToDate:

                attToDate.setText("");
                calanderHRMS.datePicker(attToDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = attFromDate.getText().toString().trim();
                            TO_DATE = attToDate.getText().toString().trim();

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

            case R.id.getCalendarButton:
                if (attFromDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select From Date");
                } else if (attToDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select To Date");
                } else {
                    getCalendarData(empId, attFromDate.getText().toString(), attToDate.getText().toString());
                }
                break;
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
                                        compareDateTextView.setText("From Date should be less than or equal To Date!");
                                        compareDatelayout.setVisibility(View.VISIBLE);
                                    } else {
                                        compareDatelayout.setVisibility(View.GONE);
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

    public static String getLastDayOfTheMonth(String date) {
        String lastDayOfTheMonth = "";

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            java.util.Date dt = formatter.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            java.util.Date lastDay = calendar.getTime();
            lastDayOfTheMonth = formatter.format(lastDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lastDayOfTheMonth;
    }

    private void getCalendarData(String employeeId, String firstdate, String lastdate) {
        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final String WEEKLIST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeRegisterPostMMT";
            JSONObject pm = new JSONObject();
            JSONObject final_params = new JSONObject();
            try {
                pm.put("EMPLOYEE_ID", employeeId);
                pm.put("FROM_DATE", firstdate);
                pm.put("TO_DATE", lastdate);
                final_params.put("objEmployeeAttendanceCalendarInfoMMT", pm);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WEEKLIST_URL, final_params, new Response.Listener<JSONObject>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        HashMap<String, String> mapData;
                        arrayList = new ArrayList<>();

                        if (response.getJSONArray("GetEmployeeRegisterPostMMTResult").length() > 0) {
                            for (int i = 0; i < response.getJSONArray("GetEmployeeRegisterPostMMTResult").length(); i++) {
                                mapData = new HashMap<>();
                                mapData.put("EAR_ATTENDANCE_DATE", response.getJSONArray("GetEmployeeRegisterPostMMTResult").getJSONObject(i).getString("EAR_ATTENDANCE_DATE"));
                                mapData.put("DAY_NAME", response.getJSONArray("GetEmployeeRegisterPostMMTResult").getJSONObject(i).getString("DAY_NAME"));
                                mapData.put("EAR_TIME_IN", response.getJSONArray("GetEmployeeRegisterPostMMTResult").getJSONObject(i).getString("EAR_TIME_IN"));
                                mapData.put("EAR_TIME_OUT", response.getJSONArray("GetEmployeeRegisterPostMMTResult").getJSONObject(i).getString("EAR_TIME_OUT"));
                                mapData.put("TOTAL_HOURS", response.getJSONArray("GetEmployeeRegisterPostMMTResult").getJSONObject(i).getString("TOTAL_HOURS"));
                                mapData.put("AA_ATTENDANCE_DESCRIPTION", response.getJSONArray("GetEmployeeRegisterPostMMTResult").getJSONObject(i).getString("AA_ATTENDANCE_DESCRIPTION"));

                                arrayList.add(mapData);
                            }

                            attCalendarMMTAdapter = new AttCalendarMMTAdapter(getActivity(), arrayList);
                            recyclerView.setAdapter(attCalendarMMTAdapter);
                            recyclerView.setVisibility(View.VISIBLE);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            recyclerView.setAdapter(null);
                            DataNotFound.setVisibility(View.VISIBLE);
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
}
