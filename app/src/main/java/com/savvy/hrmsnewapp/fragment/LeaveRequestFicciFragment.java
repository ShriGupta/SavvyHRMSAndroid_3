package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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
import com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity;
import com.savvy.hrmsnewapp.adapter.LeaveRequestFicciAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LeaveRequestFicciFragment extends BaseFragment {

    LeaveRequestFicciAdapter mAdapter;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "", employeeId = "";
    String fin_year = "", curr_year = "", isBackDate = "";
    CustomTextView txv_YearValue, txv_financ_year_value, txt_applyLeaveValueGreen, txt_applyLeaveValueOrange, txt_insufficietValue;

    SharedPreferences shared;
    ArrayList<HashMap<String, String>> arlData;
    RecyclerView recyclerView;
    CoordinatorLayout coordinatorLayout;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        arlData = new ArrayList<>();
        token = (shared.getString("Token", ""));
        employeeId = (shared.getString("EmpoyeeId", ""));
        getFinancialYearPost();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_request_ficci, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        txv_YearValue = getActivity().findViewById(R.id.tv_YearValue);
        txv_financ_year_value = getActivity().findViewById(R.id.tv_financ_year_value);
        recyclerView = getActivity().findViewById(R.id.leaveRequestList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new LeaveRequestFicciFragment.RecyclerTouchListener(getActivity(), recyclerView, new LeaveRequestFicciFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                final HashMap<String, String> mapdata = arlData.get(position);
                txt_applyLeaveValueGreen = view.findViewById(R.id.txt_applyLeaveGreen);
                txt_applyLeaveValueOrange = view.findViewById(R.id.txt_applyLeaveOrange);
                txt_insufficietValue = view.findViewById(R.id.txt_insufficiantRed);

                txt_applyLeaveValueGreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            if (mapdata.get("ENABLE").equals("1")) {
                                Intent intent = new Intent(getActivity(), LeaveApplyFicciActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("LEAVE_NAME", mapdata.get("LEAVE_NAME"));
                                bundle.putString("FROM_DATE", mapdata.get("FROM_DATE"));
                                bundle.putString("TO_DATE", mapdata.get("TO_DATE"));
                                bundle.putString("LEAVE_CONFIG_ID", mapdata.get("LEAVE_CONFIG_ID"));
                                bundle.putString("EMPLOYEE_ID",employeeId);
                                bundle.putString("IS_PREVIOUS_YEAR", mapdata.get("IS_PREVIOUS_YEAR"));
                                bundle.putString("YEAR", mapdata.get("YEAR"));
                                bundle.putString("FIN_YEAR", mapdata.get("FIN_YEAR"));
                                bundle.putString("ELBD_YEAR_FIN_YEAR", mapdata.get("ELBD_YEAR_FIN_YEAR"));
                                bundle.putString("LM_ABBREVATION", mapdata.get("LM_ABBREVATION"));
                                bundle.putString("FROM_DATE1", mapdata.get("FROM_DATE1"));
                                bundle.putString("TO_DATE1", mapdata.get("TO_DATE1"));

                                intent.putExtras(bundle);
                                Log.e("Sorry", "Disturb");
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                txt_applyLeaveValueOrange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (mapdata.get("ENABLE").equals("2")) {
                                Intent intent = new Intent(getActivity(), LeaveApplyFicciActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("LEAVE_NAME", mapdata.get("LEAVE_NAME"));
                                bundle.putString("FROM_DATE", mapdata.get("FROM_DATE"));
                                bundle.putString("TO_DATE", mapdata.get("TO_DATE"));
                                bundle.putString("LEAVE_CONFIG_ID", mapdata.get("LEAVE_CONFIG_ID"));
                                bundle.putString("EMPLOYEE_ID", employeeId);

                                bundle.putString("IS_PREVIOUS_YEAR", mapdata.get("IS_PREVIOUS_YEAR"));
                                bundle.putString("YEAR", mapdata.get("YEAR"));
                                bundle.putString("FIN_YEAR", mapdata.get("FIN_YEAR"));
                                bundle.putString("ELBD_YEAR_FIN_YEAR", mapdata.get("ELBD_YEAR_FIN_YEAR"));
                                bundle.putString("LM_ABBREVATION", mapdata.get("LM_ABBREVATION"));

                                bundle.putString("FROM_DATE1", mapdata.get("FROM_DATE"));
                                bundle.putString("TO_DATE1", mapdata.get("TO_DATE"));
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onLongClick(View view, int posotion) {

                Log.d(TAG, "onLongClick: ");
            }
        }));

    }

    private void getFinancialYearPost() {

        try {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            arlData = new ArrayList<>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveTypeYear";

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
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
                    progressDialog.dismiss();
                    error.printStackTrace();
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
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }
    }

    private void getLeaveRunningBalancePost(String currYear, String fincYear, String isbackdate) {
        try {
            arlData = new ArrayList<>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveRunningBalance";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("employeeId", employeeId);
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

                                        arlData.add(leaveBalancemap);
                                    }
                                    System.out.println("LeaveBalanceArray===" + arlData);

                                    mAdapter = new LeaveRequestFicciAdapter(getActivity(), coordinatorLayout, arlData);
                                    recyclerView.setAdapter(mAdapter);
                                } else {
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
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
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int posotion);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private LeaveRequestFicciFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LeaveRequestFicciFragment.ClickListener clickListener) {

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
