package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.savvy.hrmsnewapp.adapter.LeaveBalanceListNewAdapter_1;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.fragment.ExpenseStatusFragment.MY_PREFS_NAME;

public class LeaveBalanceFragment_1 extends BaseFragment {

    JSONObject pm;
    RecyclerView recyclerView;
    ArrayList<HashMap<String,String>> arlData;

    LeaveBalanceListNewAdapter_1 mAdapter;
    TextView txv_financ_year_value,txv_YearValue;
    SharedPreferences shared;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arlData = new ArrayList<HashMap<String,String>> ();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

//        getFinancialYear();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_leave_balance_fragment_1, container, false);

        txv_YearValue = view.findViewById(R.id.txv_YearValue);
        txv_financ_year_value = view.findViewById(R.id.txv_financ_year_value);

//        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.recyclerViewInsert);
        mAdapter = new LeaveBalanceListNewAdapter_1(getActivity(), arlData);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getBalanceYearStatus_1();
    }

    public void getFinancialYear(){
        String url = "http://savvyshippingsoftware.com/savvymobile/SavvyMobileService.svc/GetLeaveTypeYear";

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int len = (String.valueOf(response)).length();

                System.out.print("Array "+ " Length = "+len+" Value = " +response.toString() );
                Log.e("Value"," Length = "+len+" Value = " +response.toString());

                try{
                    JSONObject jsonObject = response.getJSONObject("GetLeaveTypeYearResult");

//                                WriteLog("Financial Year Response","LeaveBalance");
                    String fin_year = jsonObject.getString("FIN_YEAR");
                    String curr_year = jsonObject.getString("YEAR");
                    String isBackDate = jsonObject.getString("IS_BACK_DATED");
                    String errorMessage = jsonObject.getString("errorMessage");

                    txv_financ_year_value.setText(fin_year);
                    txv_YearValue.setText(curr_year);

//                    getBalanceYearStatus(curr_year, fin_year, isBackDate);
//                    getBalanceYearStatus();
                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.e("Error In",""+ex.getMessage());
                }
//                Toast.makeText(getActivity(), "Response "+response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error "+error.getMessage(),Toast.LENGTH_LONG).show();
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
    }

    public void getBalanceYearStatus(){
        arlData = new ArrayList<HashMap<String,String>> ();
        String url = "http://savvyshippingsoftware.com/savvymobile/SavvyMobileService.svc/GetLeaveRunningBalance";
        try {
            pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("employeeId", "10");
            params_final.put("isPreviousYear", "0");
            params_final.put("year", "2017");
            params_final.put("finYear", "2017-2018");

            pm.put("objLeaveRunningBalanceInfo", params_final);
        }catch (Exception e){
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int len = (String.valueOf(response)).length();

                        System.out.print("Array "+ " Length = "+len+" Value = " +response.toString() );
                        Log.e("Value"," Length = "+len+" Value = " +response.toString());
                        Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();

                        try{

                            HashMap<String, String> leaveBalancemap;
                            JSONArray jsonArray =response.getJSONArray("GetLeaveRunningBalanceResult");

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i <2; i++) {

                                    leaveBalancemap = new HashMap<String, String>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String approvedLeave = explrObject.getString("APPROVED_LEAVE");
                                    String currentBalance = explrObject.getString("CURRENT_BALANCE");
                                    String lm_abbrevation = explrObject.getString("LM_ABBREVATION");
                                    String pendingLeave = explrObject.getString("PENDING_LEAVE");
                                    String runningBalance = explrObject.getString("RUNNING_BALANCE");

                                    leaveBalancemap.put("APPROVED_LEAVE", approvedLeave);
                                    leaveBalancemap.put("CURRENT_BALANCE", currentBalance);
                                    leaveBalancemap.put("PENDING_LEAVE", pendingLeave);
                                    leaveBalancemap.put("RUNNING_BALANCE", runningBalance);
                                    leaveBalancemap.put("LM_ABBREVATION", lm_abbrevation);

                                    arlData.add(leaveBalancemap);
                                }
                                Toast.makeText(getActivity(), "ArlData "+arlData, Toast.LENGTH_SHORT).show();
                                mAdapter = new LeaveBalanceListNewAdapter_1(getActivity(), arlData);
                            try {

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        recyclerView.setAdapter(mAdapter);
                                    }
                                }, 1000 * 1);
                            }catch (Exception e){
                                Toast.makeText(getActivity(), "eXCEPTION"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            } else {
                                System.out.println("Data not getting on server side");
                                Toast.makeText(getActivity(), "Data not getting on server side ", Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                            Log.e("Error In",""+ex.getMessage());
                            Toast.makeText(getActivity(), "Error In "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error "+error.getMessage(),Toast.LENGTH_LONG).show();
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
    try {
        int socketTimeout = 3000000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);
    }
    catch(Exception ex){
        ex.printStackTrace();
    }
    }

    public void getBalanceYearStatus_1(){
        HashMap<String, String> leaveBalancemap = new HashMap<String, String>();
        for (int i = 0; i <6; i++) {
            String approvedLeave = "APPROVED";
            String currentBalance = "CURRENT";
            String lm_abbrevation = "ABBRE";
            String pendingLeave = "PENDING";
            String runningBalance = "RUNNING";

            leaveBalancemap.put("APPROVED_LEAVE", approvedLeave);
            leaveBalancemap.put("CURRENT_BALANCE", currentBalance);
            leaveBalancemap.put("PENDING_LEAVE", pendingLeave);
            leaveBalancemap.put("RUNNING_BALANCE", runningBalance);
            leaveBalancemap.put("LM_ABBREVATION", lm_abbrevation);

            arlData.add(leaveBalancemap);
        }
        Log.e("Arl Data",""+arlData.toString());
        Log.e("Arl Size",""+arlData.size());
//        Toast.makeText(getActivity(), "ArlData "+arlData, Toast.LENGTH_SHORT).show();
        mAdapter = new LeaveBalanceListNewAdapter_1(getActivity(), arlData);
        recyclerView.setAdapter(mAdapter);
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                recyclerView.setAdapter(mAdapter);
//            }
//        }, 1000 * 1);
    }
}
