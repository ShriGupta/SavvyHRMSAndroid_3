package com.savvy.hrmsnewapp.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.LeaveApplyActivity;
import com.savvy.hrmsnewapp.adapter.LeaveBalanceListNewAdapter;
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


public class LeaveBalanceFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private SharedPreferences shared;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private RecyclerView recyclerView;
    LeaveApplyActivity leaveApplyActivity;
    private CustomTextView txv_YearValue, txv_financ_year_value;
    private LeaveBalanceAsynTask leaveBalanceasynctask;
    private String fin_year = "", curr_year = "", isBackDate = "";
    private ArrayList<HashMap<String, String>> arlData;
    private LeaveBalanceListNewAdapter mAdapter;
    private String token = "", empoyeeId = "";

    private TextView txt_applyLeaveValueGreen, txt_applyLeaveValue, txt_insufficietValue;

    BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
            arlData = new ArrayList<>();
            token = (shared.getString("Token", ""));
            empoyeeId = (shared.getString("EmpoyeeId", ""));

//            getFinancialYear();
            getFinancialYearPost();
        } catch (Exception e) {
        }
    }


    private void getFinancialYear() {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                //arlRequestStatusData=new ArrayList<HashMap<String,String>>();
//                WriteLog("GetFinancial Year","LeaveBalance");
//                financialYearasynctask = new FinancialYearAsynTask();
//                financialYearasynctask.execute();

            } else {
//                WriteLog("Else GetFinancial Year","LeaveBalance");
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
//            WriteLog("Exception GetFinancial Year Message = "+e.getMessage(),"LeaveBalance");
            e.printStackTrace();
        }
    }

    private void getLeaveRunningBalance(String empid, String token, String currYear, String fincYear, String isbackdate) {
        try {
            if (Utilities.isNetworkAvailable(requireActivity())) {
//                WriteLog("Leave Running Balance","LeaveBalance");
                arlData = new ArrayList<HashMap<String, String>>();
                leaveBalanceasynctask = new LeaveBalanceAsynTask();
                leaveBalanceasynctask.empid = empid;
                leaveBalanceasynctask.currYear = currYear;
                leaveBalanceasynctask.fincYear = fincYear;
                leaveBalanceasynctask.isbackdate = isbackdate;
                leaveBalanceasynctask.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
//                WriteLog("Else Leave Running Balance","LeaveBalance");

            }
        } catch (Exception e) {
            e.printStackTrace();
//            WriteLog("Exception Leave Running Balance Message = "+e.getMessage(),"LeaveBalance");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Toast.makeText(getActivity(), "On Create View Called ", Toast.LENGTH_SHORT).show();

        /* new BottomNavigationAction(view, getActivity()).onHomeButtonClick();*/
        return inflater.inflate(R.layout.fragment_leave_balance, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);
            View view = getView();

            //Toast.makeText(getActivity(), "onActivityCreated Called ", Toast.LENGTH_SHORT).show();
            coordinatorLayout = requireActivity().findViewById(R.id.coordinatorLayout);
            txv_YearValue = requireActivity().findViewById(R.id.txv_YearValue);
            txv_financ_year_value = requireActivity().findViewById(R.id.txv_financ_year_value);
            recyclerView = requireActivity().findViewById(R.id.leaveBalanceList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new LeaveBalanceListNewAdapter(getActivity(), coordinatorLayout, arlData);


        } catch (Exception e) {
        }

        recyclerView.addOnItemTouchListener(new LeaveBalanceFragment.RecyclerTouchListener(getActivity(), recyclerView, new LeaveBalanceFragment.ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                //Values are passing to activity & to fragment as well
                try {
//                    WriteLog("RecyclerView OnClick","LeaveBalance");
                    int pos = recyclerView.getChildAdapterPosition(view);

                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_applyLeaveValue = view.findViewById(R.id.txt_applyLeaveValue);
                    txt_applyLeaveValueGreen = view.findViewById(R.id.txt_applyLeaveValuegreen);
                    txt_insufficietValue = view.findViewById(R.id.txt_insufficiant);

                    //Toast.makeText(getActivity(),"Press Again",Toast.LENGTH_LONG).show();

                    String enable = mapdata.get("ENABLE");
                    Log.v("Enable ", "" + enable);
                    if (enable.equals("1")) {
                        txt_applyLeaveValueGreen.setVisibility(View.VISIBLE);
                        txt_applyLeaveValue.setVisibility(View.INVISIBLE);
                        txt_insufficietValue.setVisibility(View.INVISIBLE);
                    } else if (enable.equals("2")) {
                        txt_applyLeaveValueGreen.setVisibility(View.INVISIBLE);
                        txt_applyLeaveValue.setVisibility(View.VISIBLE);
                        txt_insufficietValue.setVisibility(View.INVISIBLE);
                    } else if (enable.equals("0")) {
                        txt_applyLeaveValueGreen.setVisibility(View.INVISIBLE);
                        txt_applyLeaveValue.setVisibility(View.INVISIBLE);
                        txt_insufficietValue.setVisibility(View.VISIBLE);
                    }

                    txt_applyLeaveValueGreen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (mapdata.get("ENABLE").equals("1")) {
                                    Intent intent = new Intent(getActivity(), LeaveApplyActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("LEAVE_NAME", mapdata.get("LEAVE_NAME"));
                                    bundle.putString("FROM_DATE", mapdata.get("FROM_DATE"));
                                    bundle.putString("TO_DATE", mapdata.get("TO_DATE"));
                                    bundle.putString("LEAVE_CONFIG_ID", mapdata.get("LEAVE_CONFIG_ID"));
                                    bundle.putString("EMPLOYEE_ID", empoyeeId);

                                    bundle.putString("IS_PREVIOUS_YEAR", mapdata.get("IS_PREVIOUS_YEAR"));
                                    bundle.putString("YEAR", mapdata.get("YEAR"));
                                    bundle.putString("FIN_YEAR", mapdata.get("FIN_YEAR"));
                                    bundle.putString("ELBD_YEAR_FIN_YEAR", mapdata.get("ELBD_YEAR_FIN_YEAR"));
                                    bundle.putString("LM_ABBREVATION", mapdata.get("LM_ABBREVATION"));

                                    bundle.putString("FROM_DATE1", mapdata.get("FROM_DATE1"));
                                    bundle.putString("TO_DATE1", mapdata.get("TO_DATE1"));

//                                    bundle.putString("ENABLE_NOTE", mapdata.get("ENABLE_NOTE"));
//                                    bundle.putString("NOTE_MESSAGE", mapdata.get("NOTE_MESSAGE"));

                                    intent.putExtras(bundle);
                                    Log.e("Sorry", "Disturb");
                                    startActivity(intent);

                                }
                            } catch (Exception e) {
//                                WriteLog("RecyclerView Green OnClick Exception","LeaveBalance");
                            }
                        }
                    });
                    txt_applyLeaveValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (mapdata.get("ENABLE").equals("2")) {
//                                    WriteLog("RecyclerView Orange OnClick","LeaveBalance");
                                    Intent intent = new Intent(getActivity(), LeaveApplyActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("LEAVE_NAME", mapdata.get("LEAVE_NAME"));
                                    bundle.putString("FROM_DATE", mapdata.get("FROM_DATE"));
                                    bundle.putString("TO_DATE", mapdata.get("TO_DATE"));
                                    bundle.putString("LEAVE_CONFIG_ID", mapdata.get("LEAVE_CONFIG_ID"));
                                    bundle.putString("EMPLOYEE_ID", empoyeeId);

                                    bundle.putString("IS_PREVIOUS_YEAR", mapdata.get("IS_PREVIOUS_YEAR"));
                                    bundle.putString("YEAR", mapdata.get("YEAR"));
                                    bundle.putString("FIN_YEAR", mapdata.get("FIN_YEAR"));
                                    bundle.putString("ELBD_YEAR_FIN_YEAR", mapdata.get("ELBD_YEAR_FIN_YEAR"));
                                    bundle.putString("LM_ABBREVATION", mapdata.get("LM_ABBREVATION"));

                                    bundle.putString("FROM_DATE1", mapdata.get("FROM_DATE"));
                                    bundle.putString("TO_DATE1", mapdata.get("TO_DATE"));

//                                    bundle.putString("ENABLE_NOTE", mapdata.get("ENABLE_NOTE"));
//                                    bundle.putString("NOTE_MESSAGE", mapdata.get("NOTE_MESSAGE"));

                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }
                            } catch (Exception e) {
//                                WriteLog("RecyclerView Orange OnClick Exception","LeaveBalance");
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
//                    WriteLog("RecyclerView OnClick Exception","LeaveBalance");
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //Toast.makeText(getContext(), "Long press on position :" + position, Toast.LENGTH_LONG).show();
            }
        }));

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private LeaveBalanceFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LeaveBalanceFragment.ClickListener clickListener) {
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
                        Log.d("Ram ", "On Long Press");
                    }
                }

//                @Override
//                public boolean onSingleTapConfirmed(MotionEvent e) {
//                    Log.d("Ram Rahim", "On Single Tap Confirmed");
//                    // Find the item view that was swiped based on the coordinates
//                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    int childPosition = recyclerView.getChildPosition(child);
//                    clickListener.onClick(child, childPosition);
//                    return true;
//                }
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

    private class FinancialYearAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            try {
//                WriteLog("Financial Year OnPreExecution","LeaveBalance");
                // Things to be done before execution of long running operation. For
                // example showing ProgessDialog
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {
            }

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                //String FINANCIALYEAR_URL = "http://savvyshippingsoftware.com/SavvyMobileNew/SavvyMobileService.svc/GetLeaveTypeYear";
//                WriteLog("Financial Year do in Background","LeaveBalance");

                String FINANCIALYEAR_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveTypeYear";

                System.out.println("FINANCIALYEAR_URL====" + FINANCIALYEAR_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(FINANCIALYEAR_URL, "GET");

                if (json != null) {
                    Log.d("JSON result", json.toString());
//                    WriteLog("Financial Year do in Background json not null","LeaveBalance");
                    return json;
                }


            } catch (Exception e) {
                e.printStackTrace();
//                WriteLog("Financial Year do in Background Exception Message = "+e.getMessage(),"LeaveBalance");
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
//                        WriteLog("Financial Year post Execution","LeaveBalance");
                        pDialog.dismiss();
                        HashMap<String, String> requestStatusmap;
                        ArrayList<String> requestArray;
                        JSONObject jsonObject = new JSONObject(result);
                        System.out.println("jsonArray===" + jsonObject);
                        requestArray = new ArrayList<String>();

                        if (jsonObject != null) {
//                            WriteLog("Financial Year post Execution json not null","LeaveBalance");
                            fin_year = jsonObject.getString("FIN_YEAR");
                            curr_year = jsonObject.getString("YEAR");
                            isBackDate = jsonObject.getString("IS_BACK_DATED");
                            String errorMessage = jsonObject.getString("errorMessage");

                            txv_financ_year_value.setText(fin_year);
                            txv_YearValue.setText(curr_year);
                            //DisplayHolidayList(arlData);
                       /* mAdapter = new LeaveStatusListAdapter(getActivity(), arlRequestStatusData);
                        recyclerView.setAdapter(mAdapter);*/
                            getLeaveRunningBalance(empoyeeId, token, curr_year, fin_year, isBackDate);
                        } else {
//                            WriteLog("Financial Year post Execution Data Not Found","LeaveBalance");
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            System.out.println("Data not getting on server side");
                        }
                    } catch (Exception e) {
//                        WriteLog("Financial Year post Execution pDialog is null Message = "+e.getMessage(),"LeaveBalance");
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
//                WriteLog("Financial Year post Execution Exception Message = "+e.getMessage(),"LeaveBalance");
            }
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    private class LeaveBalanceAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        //private double lati;
        String token;
        String currYear, fincYear, isbackdate;

        @Override
        protected void onPreExecute() {
            try {
//                WriteLog("Leave Balance pre Execution","LeaveBalance");
                // Things to be done before execution of long running operation. For
                // example showing ProgessDialog
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {
            }

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
//                WriteLog("Leave Balance Do in background Execution","LeaveBalance");

                // String GETRUNNINGBALANCE_URL = "http://savvyshippingsoftware.com/SavvyMobileNew/SavvyMobileService.svc/GetLeaveRunningBalance/"+empid+"/"+currYear+"/"+fincYear+"/"+"0";


                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETRUNNINGBALANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveRunningBalance/" + empid + "/" + currYear + "/" + fincYear + "/" + "0";

                System.out.println("GETRUNNINGBALANCE_URL====" + GETRUNNINGBALANCE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(GETRUNNINGBALANCE_URL, "GET");

                if (json != null) {
                    Log.d("JSON result", json.toString());
//                    WriteLog("Leave Balance Do in background JSOn","LeaveBalance");

                    return json;
                }


            } catch (Exception e) {
                e.printStackTrace();
//                WriteLog("Leave Balance Do in background Execution Exception Message = "+e.getMessage(),"LeaveBalance");
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
//                        WriteLog("Leave Balance Post Execution","LeaveBalance");
                        pDialog.dismiss();
                        HashMap<String, String> leaveBalancemap;
                        JSONArray jsonArray = new JSONArray(result);
                        System.out.println("jsonArray===" + jsonArray);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
//                                WriteLog("Leave Balance Post Execution Loop","LeaveBalance");
                                leaveBalancemap = new HashMap<String, String>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String advanceLeave = explrObject.getString("ADVANCE_LEAVE");
                                String approvedLeave = explrObject.getString("APPROVED_LEAVE");
                                String currentBalance = explrObject.getString("CURRENT_BALANCE");

                                String elbd_year_fin_year = explrObject.getString("ELBD_YEAR_FIN_YEAR");
                                String employee_id = explrObject.getString("EMPLOYEE_ID");

                                String enable = explrObject.getString("ENABLE");
                                String fin_year = explrObject.getString("FIN_YEAR");
                                String from_date = explrObject.getString("FROM_DATE");
                                String from_date1 = explrObject.getString("FROM_DATE1");
                                String is_previousyear = explrObject.getString("IS_PREVIOUS_YEAR");

                                String leave_config_id = explrObject.getString("LEAVE_CONFIG_ID");
                                String leaveName = explrObject.getString("LEAVE_NAME");
                                String lm_abbrevation = explrObject.getString("LM_ABBREVATION");
                                String lopLeave = explrObject.getString("LOP_LEAVE");
                                String pendingLeave = explrObject.getString("PENDING_LEAVE");

                                String runningBalance = explrObject.getString("RUNNING_BALANCE");
                                String to_date = explrObject.getString("TO_DATE");
                                String to_date1 = explrObject.getString("TO_DATE1");
                                String year = explrObject.getString("YEAR");

//                                String ENABLE_NOTE = explrObject.getString("ENABLE_NOTE");
//                                String NOTE_MESSAGE = explrObject.getString("NOTE_MESSAGE");


                                //System.out.println("holidayName===" + holidayName);
                                leaveBalancemap.put("LEAVE_NAME", leaveName);
                                leaveBalancemap.put("APPROVED_LEAVE", approvedLeave);
                                leaveBalancemap.put("ADVANCE_LEAVE", advanceLeave);
                                leaveBalancemap.put("LOP_LEAVE", lopLeave);
                                leaveBalancemap.put("CURRENT_BALANCE", currentBalance);
                                leaveBalancemap.put("PENDING_LEAVE", pendingLeave);
                                leaveBalancemap.put("RUNNING_BALANCE", runningBalance);
                                leaveBalancemap.put("ENABLE", enable);

                                leaveBalancemap.put("FROM_DATE", from_date);
                                leaveBalancemap.put("TO_DATE", to_date);
                                leaveBalancemap.put("LEAVE_CONFIG_ID", leave_config_id);
                                leaveBalancemap.put("EMPLOYEE_ID", employee_id);

                                leaveBalancemap.put("IS_PREVIOUS_YEAR", is_previousyear);
                                leaveBalancemap.put("YEAR", year);
                                leaveBalancemap.put("FIN_YEAR", fin_year);
                                leaveBalancemap.put("FROM_DATE1", from_date1);
                                leaveBalancemap.put("TO_DATE1", to_date1);
                                leaveBalancemap.put("LM_ABBREVATION", lm_abbrevation);
                                leaveBalancemap.put("ELBD_YEAR_FIN_YEAR", elbd_year_fin_year);

//                                leaveBalancemap.put("ENABLE_NOTE", ENABLE_NOTE);
//                                leaveBalancemap.put("NOTE_MESSAGE", NOTE_MESSAGE);

                                arlData.add(leaveBalancemap);
                            }
                            System.out.println("LeaveBalanceArray===" + arlData);

                            //DisplayHolidayList(arlData);
                            mAdapter = new LeaveBalanceListNewAdapter(getActivity(), coordinatorLayout, arlData);
                            recyclerView.setAdapter(mAdapter);
                        } else {
//                            WriteLog("Leave Balance Post Execution Data not Found ","LeaveBalance");
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            System.out.println("Data not getting on server side");
                        }
                    } catch (Exception e) {
//                        WriteLog("Leave Balance Post Execution pDialog Exception Message = "+e.getMessage(),"LeaveBalance");
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
//                WriteLog("Leave Balance Post Execution Exception Message = "+e.getMessage(),"LeaveBalance");
            }
        }
    }


    public void getFinancialYearPost() {
        try {
            arlData = new ArrayList<>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveTypeYear";

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                JSONObject jsonObject = response.getJSONObject("GetLeaveTypeYearResult");

                                fin_year = jsonObject.getString("FIN_YEAR");
                                curr_year = jsonObject.getString("YEAR");
                                isBackDate = jsonObject.getString("IS_BACK_DATED");
                                String errorMessage = jsonObject.getString("errorMessage");

                                txv_financ_year_value.setText(fin_year);
                                txv_YearValue.setText(curr_year);

                                getLeaveRunningBalancePost(curr_year, fin_year, isBackDate);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d("Error", "" + error.getMessage());
//                    WriteLog("Financial Year Response Error "+error.getMessage(),"LeaveBalance");
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


        } catch (Exception e) {
            e.printStackTrace();
//            WriteLog("Financial Year Response Exception "+e.getMessage(),"LeaveBalance");
        }
    }

    public void getLeaveRunningBalancePost(String currYear, String fincYear, String isbackdate) {
        try {
            arlData = new ArrayList<>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveRunningBalance";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("employeeId", empoyeeId);
            params_final.put("isPreviousYear", "0");
            params_final.put("year", currYear);
            params_final.put("finYear", fincYear);

            pm.put("objLeaveRunningBalanceInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {

                                HashMap<String, String> leaveBalancemap;
                                JSONArray jsonArray = response.getJSONArray("GetLeaveRunningBalanceResult");
                                System.out.println("jsonArray===" + jsonArray);
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        leaveBalancemap = new HashMap<>();
                                        JSONObject explrObject = jsonArray.getJSONObject(i);
                                        String advanceLeave = explrObject.getString("ADVANCE_LEAVE");
                                        String approvedLeave = explrObject.getString("APPROVED_LEAVE");
                                        String currentBalance = explrObject.getString("CURRENT_BALANCE");

                                        String elbd_year_fin_year = explrObject.getString("ELBD_YEAR_FIN_YEAR");
                                        String employee_id = explrObject.getString("EMPLOYEE_ID");

                                        String enable = explrObject.getString("ENABLE");
                                        String fin_year = explrObject.getString("FIN_YEAR");
                                        String from_date = explrObject.getString("FROM_DATE");
                                        String from_date1 = explrObject.getString("FROM_DATE1");
                                        String is_previousyear = explrObject.getString("IS_PREVIOUS_YEAR");

                                        String leave_config_id = explrObject.getString("LEAVE_CONFIG_ID");
                                        String leaveName = explrObject.getString("LEAVE_NAME");
                                        String lm_abbrevation = explrObject.getString("LM_ABBREVATION");
                                        String lopLeave = explrObject.getString("LOP_LEAVE");
                                        String pendingLeave = explrObject.getString("PENDING_LEAVE");

                                        String runningBalance = explrObject.getString("RUNNING_BALANCE");
                                        String to_date = explrObject.getString("TO_DATE");
                                        String to_date1 = explrObject.getString("TO_DATE1");
                                        String year = explrObject.getString("YEAR");

//                                        String ENABLE_NOTE = explrObject.getString("ENABLE_NOTE");
//                                        String NOTE_MESSAGE = explrObject.getString("NOTE_MESSAGE");

//                                        WriteLog("Leave Balance Post Execution MAPDATA","LeaveBalance");
                                        //System.out.println("holidayName===" + holidayName);
                                        leaveBalancemap.put("LEAVE_NAME", leaveName);
                                        leaveBalancemap.put("APPROVED_LEAVE", approvedLeave);
                                        leaveBalancemap.put("ADVANCE_LEAVE", advanceLeave);
                                        leaveBalancemap.put("LOP_LEAVE", lopLeave);
                                        leaveBalancemap.put("CURRENT_BALANCE", currentBalance);
                                        leaveBalancemap.put("PENDING_LEAVE", pendingLeave);
                                        leaveBalancemap.put("RUNNING_BALANCE", runningBalance);
                                        leaveBalancemap.put("ENABLE", enable);

                                        leaveBalancemap.put("FROM_DATE", from_date);
                                        leaveBalancemap.put("TO_DATE", to_date);
                                        leaveBalancemap.put("LEAVE_CONFIG_ID", leave_config_id);
                                        leaveBalancemap.put("EMPLOYEE_ID", employee_id);

                                        leaveBalancemap.put("IS_PREVIOUS_YEAR", is_previousyear);
                                        leaveBalancemap.put("YEAR", year);
                                        leaveBalancemap.put("FIN_YEAR", fin_year);
                                        leaveBalancemap.put("FROM_DATE1", from_date1);
                                        leaveBalancemap.put("TO_DATE1", to_date1);
                                        leaveBalancemap.put("LM_ABBREVATION", lm_abbrevation);
                                        leaveBalancemap.put("ELBD_YEAR_FIN_YEAR", elbd_year_fin_year);
//                                        leaveBalancemap.put("ENABLE_NOTE", ENABLE_NOTE);
//                                        leaveBalancemap.put("NOTE_MESSAGE", NOTE_MESSAGE);

                                        arlData.add(leaveBalancemap);
                                    }
                                    System.out.println("LeaveBalanceArray===" + arlData);


                                    // DisplayHolidayList(arlData);
                                    mAdapter = new LeaveBalanceListNewAdapter(getActivity(), coordinatorLayout, arlData);
                                    recyclerView.setAdapter(mAdapter);
                                } else {
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                    System.out.println("Data not getting on server side");
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
