package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
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
import com.savvy.hrmsnewapp.activity.MarkAttendance;
import com.savvy.hrmsnewapp.adapter.DetailsPunchAdapter;
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

public class CustomerViewPunchDetail extends BaseFragment {

    DetailsPunchAdapter mAdapter;
    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    LinearLayout linearCalendarLayout1;
    ImageView imgLine;
    String empoyeeId = "";
    CalanderHRMS cal;

    GetCustDetailAsync getCustDetailAsync;

    Button btn_fromDate,btn_toDate,btn_submit,btn_mark_att;
    CustomTextView txtFromDate,txtToDate;

    String xmlData = "";
    String token_no = "";

    String FROM_DATE = "",TO_DATE = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_view_punch_detail, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        cal = new CalanderHRMS(getActivity());

        btn_fromDate = getActivity().findViewById(R.id.btn_CustDetailFromDate);
        btn_toDate = getActivity().findViewById(R.id.btn_CustDetailToDate);
        btn_submit = getActivity().findViewById(R.id.btn_cust_detail_submit);
        btn_mark_att = getActivity().findViewById(R.id.btn_mark_attendance);

        txtFromDate = getActivity().findViewById(R.id.txtFromDate);
        txtToDate = getActivity().findViewById(R.id.txtToDate);

        String fromdate = "From Date " + "<font color=\"#E72A02\"><bold>"+ " *"+ "</bold></font>";
        String todate = "To Date " + "<font color=\"#E72A02\"><bold>"+ " *"+ "</bold></font>";
        txtFromDate.setText(Html.fromHtml(fromdate));
        txtToDate.setText(Html.fromHtml(todate));

        SimpleDateFormat simpleDateFormatFrom = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        Date dateFrom = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        btn_fromDate.setText("01/"+simpleDateFormatFrom.format(dateFrom));
        btn_toDate.setText(simpleDateFormat.format(date));

        btn_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.datePicker(btn_fromDate);
            }
        });
        btn_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.datePicker(btn_toDate);
            }
        });

        recyclerView = getActivity().findViewById(R.id.cust_detail);
        mAdapter = new DetailsPunchAdapter(getActivity(),coordinatorLayout,arlData);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String from_date = btn_fromDate.getText().toString().replace("/","-");
                String to_date = btn_toDate.getText().toString().replace("/","-");

                FROM_DATE = btn_fromDate.getText().toString().replace("/","-");
                TO_DATE = btn_toDate.getText().toString().replace("/","-");

                if(from_date.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter From Date");
                } else if(to_date.equals("")) {
                    Utilities.showDialog(coordinatorLayout,"Please Enter To Date");
                } else if(from_date.compareTo(to_date)>0) {
                    Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than To Date");
                } else{
                    getCustomerDetails(empoyeeId,from_date,to_date);
                }

            }
        });

        btn_mark_att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), MarkAttendance.class));
                getActivity().overridePendingTransition(0,0);
            }
        });
    }

    private void getCustomerDetails(String empoyeeId,String from_date,String to_date) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<HashMap<String, String>>();
                getCustDetailAsync = new CustomerViewPunchDetail.GetCustDetailAsync();
                getCustDetailAsync.empoyeeId = empoyeeId;
                getCustDetailAsync.from_date = from_date;
                getCustDetailAsync.to_date = to_date;
                getCustDetailAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class GetCustDetailAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empoyeeId,from_date ,to_date ;

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
//                String ATTENDANCE_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetMarkAttendanceList/"+empoyeeId+"/"+from_date+"/"+to_date;
                String ATTENDANCE_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetMarkAttendanceListPost";

//                System.out.println("ATTENDANCE_URL===="+ATTENDANCE_URL);
//                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
//                String json = jParser.makeHttpRequest(
//                        ATTENDANCE_URL, "GET");
//
//                if (json != null) {
//                    Log.d("JSON result", json.toString());
//
//                    return json;
//                }
                return ATTENDANCE_URL;


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
                try{
                    String url = result;
                    System.out.println("URL=1==" + url);

                    final JSONObject pm = new JSONObject();
                    JSONObject params_final = new JSONObject();

                    params_final.put("FromDate",FROM_DATE);
                    params_final.put("Todate",TO_DATE);
                    params_final.put("EmployeeId",empoyeeId);
                    pm.put("objEmployee",params_final);


                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    int len = (String.valueOf(response)).length();

                                    System.out.print("Array "+ " Length = "+len+" Value = " +response.toString() );
                                    Log.e("Value"," Length = "+len+" Value = " +response.toString());

                                    try{
                                        HashMap<String, String> mapData;
                                        JSONArray jsonArray = response.getJSONArray("GetMarkAttendanceListPostResult");

                                        if(jsonArray.length()>0) {

                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                mapData = new HashMap<String, String>();
                                                JSONObject explrObject = jsonArray.getJSONObject(i);

                                                String Punch_date = explrObject.getString("PunchDate");
                                                String Punch_time = explrObject.getString("PunchTime");
                                                String remark = explrObject.getString("Remark");

                                                mapData.put("PunchDate", Punch_date);
                                                mapData.put("PunchTime", Punch_time);
                                                mapData.put("Remark", remark);

                                                arlData.add(mapData);

                                            }

                                            System.out.println("ArrayList==" + arlData);

                                            mAdapter = new DetailsPunchAdapter(getActivity(), coordinatorLayout, arlData);
                                            recyclerView.setAdapter(mAdapter);
                                        } else {
                                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    recyclerView.setAdapter(null);
                                                }
                                            }, 500);
                                        }

                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            error.getMessage();
                            error.printStackTrace();
                            Log.e("Error",""+error.getMessage());
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
//                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    jsonObjectRequest.setRetryPolicy(policy);
                    requestQueue.add(jsonObjectRequest);


                }catch (Exception e){
                    e.printStackTrace();
                }


                try{
//                    HashMap<String, String> punchStatusmap;
//                    JSONArray jsonArray = new JSONArray(result);
//                    System.out.println("jsonArray===" + jsonArray);
//                    if (jsonArray.length() > 0){
//                        for(int i=0;i<jsonArray.length();i++){
//                            punchStatusmap=new HashMap<String, String>();
//                            JSONObject explrObject = jsonArray.getJSONObject(i);
//
//                            String punchDate=explrObject.getString("PunchDate");
//                            String punchTime=explrObject.getString("PunchTime");
//                            String remark=explrObject.getString("Remark");
//
//                            punchStatusmap.put("PunchDate", punchDate);
//                            punchStatusmap.put("PunchTime", punchTime);
//                            punchStatusmap.put("Remark", remark);
//
//                            arlData.add(punchStatusmap);
//                        }
//                        System.out.println("Array===" + arlData);
//
//                        //DisplayHolidayList(arlData);
//                        mAdapter = new DetailsPunchAdapter(getActivity(), coordinatorLayout,arlData);
//                        recyclerView.setAdapter(mAdapter);
//                    }else{
//                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
//                        System.out.println("Data not getting on server side");
//                    }

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
}
