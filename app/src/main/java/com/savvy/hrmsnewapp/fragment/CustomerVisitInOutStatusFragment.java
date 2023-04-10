package com.savvy.hrmsnewapp.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CustomerVisitInOutAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class CustomerVisitInOutStatusFragment extends Fragment {

    CustomerVisitInOutAdapter customerVisitInOutAdapter;
    RecyclerView recyclerView;
    CoordinatorLayout coordinatorLayout;
    private Button fromdate, todate;
    CustomTextView dataNotFound, compareDatetextView;
    LinearLayout compareDateLayout;
    CalanderHRMS calanderHRMS;
    Handler handler, handler1;
    public static Runnable runnable, runnable1;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employee_Id;
    ProgressDialog progressDialog;
    String from_date, to_date;
    List<HashMap<String, String>> arrayList;
    Button getDetailButton;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employee_Id = (shared.getString("EmpoyeeId", ""));

        from_date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        to_date = from_date;

        getCustomerStatusData(employee_Id, from_date.replace("/", "-"), to_date.replace("/", "-"));

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_customervisit_inout_status, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        calanderHRMS = new CalanderHRMS(getActivity());
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        recyclerView = getActivity().findViewById(R.id.punchInOutVisit_RecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        fromdate = getActivity().findViewById(R.id.punchvisit_FromDate);
        todate = getActivity().findViewById(R.id.punchvisit_ToDate);
        getDetailButton = getActivity().findViewById(R.id.getDetailButton);

        compareDatetextView = getActivity().findViewById(R.id.punchvisitCompareDateTextView);
        dataNotFound = getActivity().findViewById(R.id.punchvisit_DataNotFound);
        compareDateLayout = getActivity().findViewById(R.id.punchVisitCompareDateLayout);

        fromdate.setText(from_date);
        todate.setText(to_date);

        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromdate.setText("");
                calanderHRMS.datePicker(fromdate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = fromdate.getText().toString().trim();
                            String ToDate = todate.getText().toString().trim();

                            if (FromDate.equals(ToDate)) {
                                handler.postDelayed(runnable, 1000);
                                compareDateLayout.setVisibility(View.GONE);
                            } else {
                                if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                    getCompareDate(FromDate, ToDate);
                                } else {
                                    handler.postDelayed(runnable, 1000);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });

        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(todate);
                handler1 = new Handler();
                runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = fromdate.getText().toString().trim();
                            String ToDate = todate.getText().toString().trim();

                            if (FromDate.equals(ToDate)) {
                                handler1.postDelayed(runnable1, 1000);
                                compareDateLayout.setVisibility(View.GONE);
                            } else {
                                if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                    getCompareDate(FromDate, ToDate);
                                } else {
                                    handler1.postDelayed(runnable1, 1000);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler1.postDelayed(runnable1, 1000);
            }
        });

        getDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fromdate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select From Date");
                } else if (todate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select To Date");
                } else {
                    getCustomerStatusData(employee_Id, fromdate.getText().toString().replace("/", "-"), todate.getText().toString().replace("/", "-"));
                }

            }
        });


    }

    private void getCustomerStatusData(String employee_id, String fromdate, String todate) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        String GET_STATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCustomerVisitInOutStatusPost/" + employee_id + "/" + fromdate + "/" + todate;
        Log.d(TAG, "getCustomerStatusData: " + GET_STATUS_URL);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_STATUS_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    arrayList = new ArrayList<>();
                    HashMap<String, String> hashMap;
                    progressDialog.dismiss();
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            hashMap = new HashMap<>();

                            hashMap.put("CUSTOMER_NAME", response.getJSONObject(i).getString("CUSTOMER_NAME"));
                            hashMap.put("LOCATION_ADDRESS_IN", response.getJSONObject(i).getString("LOCATION_ADDRESS_IN"));
                            hashMap.put("LOCATION_ADDRESS_OUT", response.getJSONObject(i).getString("LOCATION_ADDRESS_OUT"));
                            hashMap.put("PUNCHIN_DATETIME", response.getJSONObject(i).getString("PUNCHIN_DATETIME"));
                            hashMap.put("PUNCHIN_REMARK", response.getJSONObject(i).getString("PUNCHIN_REMARK"));
                            hashMap.put("PUNCHOUT_DATETIME", response.getJSONObject(i).getString("PUNCHOUT_DATETIME"));
                            hashMap.put("PUNCHOUT_REMARKS", response.getJSONObject(i).getString("PUNCHOUT_REMARKS"));
                            arrayList.add(hashMap);
                        }
                        customerVisitInOutAdapter = new CustomerVisitInOutAdapter(getActivity(), arrayList);
                        recyclerView.setAdapter(customerVisitInOutAdapter);
                        dataNotFound.setVisibility(View.GONE);
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        dataNotFound.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.showDialog(coordinatorLayout,Utilities.handleVolleyError(error));
                progressDialog.dismiss();
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

        int socketTimeOut = 3000000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonArrayRequest.setRetryPolicy(retryPolicy);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    public void getCompareDate(String fromDate, final String toDate) {
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
                                        compareDatetextView.setText("From Date should be less than or equal To Date!");
                                        compareDateLayout.setVisibility(View.VISIBLE);
                                        todate.setText("");
                                    } else {
                                        compareDateLayout.setVisibility(View.GONE);
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
}
