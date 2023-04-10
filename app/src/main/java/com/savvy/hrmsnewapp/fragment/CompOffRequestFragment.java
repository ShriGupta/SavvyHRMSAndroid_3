package com.savvy.hrmsnewapp.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
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

import static android.content.Context.MODE_PRIVATE;
import static androidx.appcompat.widget.AppCompatDrawableManager.get;

public class CompOffRequestFragment extends BaseFragment {


    Button btn_comp_off_date, btn_cor_date_from, btn_cor_time_from, btn_cor_date_to, btn_cor_time_to;
    Button btn_submit;

    LinearLayout layoutSpin;
    int counter = 0;

    Runnable rRunnable1;
    boolean COMP_OFF_FLAG = false;
    boolean COMP_OFF_TIME_FLAG = false;

    String comp_off_date = "", comp_off_time_out1 = "";
    String comp_off_date_from = "", comp_off_time_from = "", comp_off_date_to = "", comp_off_time_to = "";

    String System_time_in = "", System_time_out = "";
    Handler handler;
    Runnable rRunnable;
    Handler handler1;
    CustomSpinnerAdapter customspinnerAdapter;

    ArrayList<HashMap<String, String>> arlData1;
    Spinner spin_reason_cor;
    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData;

    SendCompOffAsync sendCompOffAsync;
    GetMyPolicyCompOffAsync getMyPolicyCompOffAsync;
    GetCompOffDateValidation getCompOffDateValidation;
    GetValidateCompOffDateAsync getValidateCompOffDateAsync;

    SharedPreferences shared, shareData;
    CalanderHRMS calanderHRMS;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "";
    String spinnerPosition = "";
    String empoyeeId = "";
    String MY_POLICY_ID = "-";
    CustomTextView txt_result, txt_result1, txt_remarks;
    String positionId = "", positionValue = "";
    EditText compOff_remarks;
    LinearLayout linear_resultData;

    CustomTextView txtCompOff_compoffDateTitle, txtCompOff_fromTimeTitle, txtCompOff_toTimeTitle, txtCompOff_ReasonTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));

        arlData = new ArrayList<HashMap<String, String>>();
        arlData1 = new ArrayList<HashMap<String, String>>();
        arlRequestStatusData = new ArrayList<HashMap<String, String>>();

        getMyPolicyData(empoyeeId);
        GetCompOffOvertimeReason getCompOffOvertimeReason = new GetCompOffOvertimeReason();
        getCompOffOvertimeReason.execute();

    }

    public void getMyPolicyData(String employeeId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                getMyPolicyCompOffAsync = new CompOffRequestFragment.GetMyPolicyCompOffAsync();
                getMyPolicyCompOffAsync.employeeId = employeeId;
                getMyPolicyCompOffAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comp_off_request, container, false);

        calanderHRMS = new CalanderHRMS(getActivity());

        arlData = new ArrayList<HashMap<String, String>>();

        btn_comp_off_date = view.findViewById(R.id.btn_cortime_date);
        btn_cor_date_from = view.findViewById(R.id.btn_cortime_from_date);
        btn_cor_time_from = view.findViewById(R.id.btn_cortime_from);
        btn_cor_date_to = view.findViewById(R.id.btn_cortime_to_date);
        btn_cor_time_to = view.findViewById(R.id.btn_cortime_to);
        spin_reason_cor = view.findViewById(R.id.spin_cor_reason);

        compOff_remarks = view.findViewById(R.id.compOff_remarks);

        linear_resultData = view.findViewById(R.id.linear_resultData);
        txt_result = view.findViewById(R.id.textResult);
        txt_result1 = view.findViewById(R.id.textResult1);

        linear_resultData.setVisibility(View.GONE);

        txtCompOff_compoffDateTitle = view.findViewById(R.id.txtCompOff_compoffDateTitle);
        txtCompOff_fromTimeTitle = view.findViewById(R.id.txtCompOff_fromTimeTitle);
        txtCompOff_toTimeTitle = view.findViewById(R.id.txtCompOff_toTimeTitle);
        txtCompOff_ReasonTitle = view.findViewById(R.id.txtCompOff_ReasonTitle);

        txt_remarks = view.findViewById(R.id.txtCompOff_remarks);

        String str1 = "<font color='#A8172A' size='101>*</font>";
//        txt_remarks.setText(Html.fromHtml("Remarks " + str1));

        txtCompOff_compoffDateTitle.setText(Html.fromHtml("Comp Off Date " + str1));
        txtCompOff_fromTimeTitle.setText(Html.fromHtml("From Time " + str1));
        txtCompOff_toTimeTitle.setText(Html.fromHtml("To Time " + str1));
        txtCompOff_ReasonTitle.setText(Html.fromHtml("Reason " + str1));

        btn_submit = view.findViewById(R.id.btn_cor_submit);

        spin_reason_cor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 0) {
                        positionValue = "";
                        positionId = "";
                    } else if (position > 0) {
                        positionId = arlRequestStatusData.get(position - 1).get("KEY");
                        positionValue = arlRequestStatusData.get(position - 1).get("VALUE");
                    }
                    Log.e("Spin Value", "Spin Id " + positionId + " Value " + positionValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_comp_off_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_comp_off_date.setText("");
                linear_resultData.setVisibility(View.GONE);
                calanderHRMS.datePicker(btn_comp_off_date);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            counter++;
                            comp_off_date = btn_comp_off_date.getText().toString();
                            Log.e("Date", comp_off_date);

                            if (!comp_off_date.equals("")) {


//                                getCompOffDateValidate(empoyeeId, comp_off_date);

                                GetCompOffDateValidationPost(empoyeeId, comp_off_date);
                            } else {
                                //Utilities.showDialog(coordinatorLayout,"Please Select Comp Off Date");
                                // handler = new Handler();
                                handler.postDelayed(rRunnable, 2000);
                            }
                            Log.e("Counter", "" + counter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(rRunnable, 800);
            }
        });

        btn_cor_date_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calanderHRMS.datePicker(btn_cor_date_from);
                if (calanderHRMS.equals("")) {

                }
                btn_cor_date_from.setText("");
            }
        });

        btn_cor_time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.timePickerHHMM(btn_cor_time_from);
            }
        });

        btn_cor_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(btn_cor_date_to);
            }
        });

        btn_cor_time_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cor_time_to.setText("");
                counter = 0;
                calanderHRMS.timePickerHHMM(btn_cor_time_to);
                Log.d("ArrayList Time", "Out " + arlData1.toString());
                for (int i = 0; i < arlData1.size(); i++) {
                    HashMap<String, String> mapdata = arlData1.get(i);
                    System_time_in = mapdata.get("SYSTEM_CO_TIME_IN");
                    System_time_out = mapdata.get("SYSTEM_CO_TIME_OUT");

                    Log.e("Values", System_time_in + "  And  " + System_time_out);
                }

                handler1 = new Handler();
                rRunnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            counter++;

                            comp_off_date_from = btn_cor_date_from.getText().toString().replace("/", "-");
                            comp_off_time_from = btn_cor_time_from.getText().toString().replace(":", "-");
                            comp_off_date_to = btn_cor_date_to.getText().toString().replace("/", "-");
                            comp_off_time_to = btn_cor_time_to.getText().toString().replace(":", "-");

                            if ((!comp_off_date_from.equals("")) && (!comp_off_time_from.equals("")) &&
                                    (!comp_off_date_to.equals("")) && (!comp_off_time_to.equals(""))) {

//                                getValidateCompOffRequestDate(comp_off_date_from, comp_off_time_from, comp_off_date_to,
//                                        comp_off_time_to,System_time_in.replace(" ","_").replace(":","@").replace("/","-"),
//                                        System_time_out.replace(" ","_").replace(":","@").replace("/","-"));

                                GetValidateCompOffDatePost(comp_off_date_from, comp_off_time_from, comp_off_date_to,
                                        comp_off_time_to, System_time_in, System_time_out);
                                //COMP_OFF_TIME_FLAG = true;
                                Log.d("Inside ", "Button Time out");

                            } else {
                                handler1.postDelayed(rRunnable1, 800);
                            }
                            Log.e("Counter", "" + counter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler1.postDelayed(rRunnable1, 800);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String compDate = "", compDatefrom = "", compDatefromTime = "", compDateTo = "", compDateToTime = "", compOffRemark;

                String keyid = "" + positionId;
                if (keyid.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Any Reason.");

                } else {
                    Log.e("Button", "" + "Hii " + keyid + "Pos " + positionId);
                    compDate = btn_comp_off_date.getText().toString().replace("/", "-");
                    compDatefrom = btn_cor_date_from.getText().toString().replace("/", "-");
                    compDatefromTime = btn_cor_time_from.getText().toString().replace(":", "-");
                    compDateTo = btn_cor_date_to.getText().toString().replace("/", "-");
                    compDateToTime = btn_cor_time_to.getText().toString().replace(":", "-");
                    compOffRemark = compOff_remarks.getText().toString();

                    if (compOffRemark.equals("")) {
                        compOffRemark = "-";
                    }


//                    setCompOffAsyncdata(compDate, compDatefrom, compDatefromTime, compDateTo, compDateToTime, keyid,compOffRemark);
                    sendCompOffRequestPost(empoyeeId, "0", compDate, "-", "-", "-",
                            "-", keyid, compOffRemark);

                    Log.e("Button2", "" + "Hii " + keyid + " Pos " + positionValue);
                }
            }
        });


        return view;
    }

    public void getValidateCompOffRequestDate(String comp_off_date_from, String comp_off_time_from, String comp_off_date_to, String comp_off_time_to,
                                              String System_time_in, String System_time_out) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                getValidateCompOffDateAsync = new CompOffRequestFragment.GetValidateCompOffDateAsync();
                getValidateCompOffDateAsync.comp_off_date_from = comp_off_date_from;
                getValidateCompOffDateAsync.comp_off_time_from = comp_off_time_from;
                getValidateCompOffDateAsync.comp_off_date_to = comp_off_date_to;
                getValidateCompOffDateAsync.comp_off_time_to = comp_off_time_to;
                getValidateCompOffDateAsync.System_time_in = System_time_in;
                getValidateCompOffDateAsync.System_time_out = System_time_out;
                getValidateCompOffDateAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCompOffDateValidate(String employeeId, String comp_off_date) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData1 = new ArrayList<HashMap<String, String>>();
                getCompOffDateValidation = new CompOffRequestFragment.GetCompOffDateValidation();
                getCompOffDateValidation.employeeId = employeeId;
                getCompOffDateValidation.comp_off_date = comp_off_date;
                getCompOffDateValidation.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCompOffAsyncdata(String compDate, String compDatefrom, String compDatefromTime, String compDateTo, String compDateToTime, String keyid, String compOffRemark) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                sendCompOffAsync = new CompOffRequestFragment.SendCompOffAsync();
                sendCompOffAsync.compDate = compDate;
                sendCompOffAsync.compDatefrom = compDatefrom;
                sendCompOffAsync.compDatefromTime = compDatefromTime;
                sendCompOffAsync.compDateTo = compDateTo;
                sendCompOffAsync.compDateToTime = compDateToTime;
                sendCompOffAsync.keyid = keyid;
                sendCompOffAsync.compOffRemark = compOffRemark;
                sendCompOffAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendCompOffRequestPost(String emp_id, String req_id, String coff_Date, String coff_DateFrom
            , String coff_timeFrom, String coff_DateTo, String coff_TimeTo, String reason, String comments) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", emp_id);
                param.put("REQUEST_ID", req_id);
                param.put("COFF_DATE", coff_Date);
                param.put("CO_FROM_DATE", coff_DateFrom);
                param.put("CO_FROM_TIME", coff_timeFrom);
                param.put("CO_TO_DATE", coff_DateTo);
                param.put("CO_TO_TIME", coff_TimeTo);
                param.put("REASON", reason);
                param.put("POLICY_ID", MY_POLICY_ID);
                param.put("COMMENTS", comments);

                Log.e("RequestData", "Json -> " + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendCompOffRequestPost";
                Log.e("Url", "Url = " + url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String result = response.getString("SendCompOffRequestPostResult");
                                    int res = Integer.valueOf(result);

                                    if (res > 0) {
                                        btn_comp_off_date.setText("");
                                        btn_cor_date_from.setText("");
                                        btn_cor_time_from.setText("");
                                        btn_cor_date_to.setText("");
                                        btn_cor_time_to.setText("");
                                        compOff_remarks.setText("");
                                        spin_reason_cor.setSelection(0);

                                        Utilities.showDialog(coordinatorLayout, "Comp Off Request Successfully.");
                                    } else if (res == -1) {
                                        Utilities.showDialog(coordinatorLayout, "Comp Off request on the same date and time already exists.");
                                    } else if (res == -2) {
                                        Utilities.showDialog(coordinatorLayout, "Comp Off request for previous payroll cycle not allowed.");
                                    } else if (res == 0) {
                                        Utilities.showDialog(coordinatorLayout, "Error during sending Comp Off Request.");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SendCompOffAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String compDate, compDatefrom, compDatefromTime, compDateTo, compDateToTime, keyid, compOffRemark;

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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendCompOffRequest" + "/" + empoyeeId + "/" + "0" + "/" + compDate + "/" + compDatefrom + "/" + compDatefromTime + "/" + compDateTo + "/" + compDateToTime + "/" + keyid + "/" + MY_POLICY_ID + "/" + compOffRemark;

                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        GETREQUESTSTATUS_URL, "GET");

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
            Log.e("Result", "" + result);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();

                try {
                    result = result.replaceAll("^\"+|\"+$", " ").trim();
                    int res = Integer.valueOf(result);

                    if (res > 0) {
                        Utilities.showDialog(coordinatorLayout, "Comp Off Request Successfully.");
                    } else if (res == -1) {
                        Utilities.showDialog(coordinatorLayout, "Comp Off request on the same date and time already exists.");
                    } else if (res == -2) {
                        Utilities.showDialog(coordinatorLayout, "Comp Off request for previous payroll cycle not allowed.");
                    } else if (res == 0) {
                        Utilities.showDialog(coordinatorLayout, "Error during sending Comp Off Request.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
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

    private class GetMyPolicyCompOffAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId;

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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyPolicyIdCompOff" + "/" + empoyeeId;

                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        GETREQUESTSTATUS_URL, "GET");

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
                    result = result.replaceAll("^\"+|\"+$", " ").trim();
                    MY_POLICY_ID = result;
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

    private class GetCompOffDateValidation extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId;
        String comp_off_date;

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
                if (COMP_OFF_FLAG) {
                    handler.removeCallbacks(rRunnable);
                    counter = 0;
                    Log.e("Do in BackGround", "Backgrond");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/CheckCompOffRequest" + "/" + empoyeeId + "/" + comp_off_date + "/" + MY_POLICY_ID;

                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        GETREQUESTSTATUS_URL, "GET");

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
                    HashMap<String, String> comp_validation = new HashMap<String, String>();
                    JSONObject jsonobj = new JSONObject(result);
                    String allow_co = jsonobj.getString("ALLOW_CO");
                    String co_timein = jsonobj.getString("CO_TIME_IN");
                    String co_timeout = jsonobj.getString("CO_TIME_OUT");
                    String system_co_timein = jsonobj.getString("SYSTEM_CO_TIME_IN");
                    String system_co_timeout = jsonobj.getString("SYSTEM_CO_TIME_OUT");

                    comp_validation.put("ALLOW_CO", allow_co);
                    comp_validation.put("CO_TIME_IN", co_timein);
                    comp_validation.put("CO_TIME_OUT", co_timeout);
                    comp_validation.put("SYSTEM_CO_TIME_IN", system_co_timein);
                    comp_validation.put("SYSTEM_CO_TIME_OUT", system_co_timeout);

                    arlData1.add(comp_validation);

                    txt_result.setVisibility(View.VISIBLE);
                    System.out.println("Array===" + arlData1);
                    System.out.println("ArraySize===" + arlData1.size());

                    if (allow_co.equals(comp_off_date)) {
                        txt_result.setText("Comp off Start Time and end Time should be between " + co_timein + " And " + co_timeout);
                    } else {

                        // Toast.makeText(getActivity(), "hi", Toast.LENGTH_SHORT).show();
                        txt_result.setText("Compoff request is not allowed for the selected date.");
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

    public void GetCompOffDateValidationPost(String empId, final String compOffDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", empId);
                param.put("COMP_OFF_DATE", compOffDate);
                param.put("POLICY_ID", MY_POLICY_ID);

                Log.e("RequestData", "Json->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/CheckCompOffRequestPost";
                Log.e("Url", "url = " + url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    HashMap<String, String> comp_validation = new HashMap<>();
                                    JSONObject jsonobj = response.getJSONObject("CheckCompOffRequestPostResult");

                                    String allow_co = jsonobj.getString("ALLOW_CO");
                                    String co_timein = jsonobj.getString("CO_TIME_IN");
                                    String co_timeout = jsonobj.getString("CO_TIME_OUT");
                                    String system_co_timein = jsonobj.getString("SYSTEM_CO_TIME_IN");
                                    String system_co_timeout = jsonobj.getString("SYSTEM_CO_TIME_OUT");

                                    comp_validation.put("ALLOW_CO", allow_co);
                                    comp_validation.put("CO_TIME_IN", co_timein);
                                    comp_validation.put("CO_TIME_OUT", co_timeout);
                                    comp_validation.put("SYSTEM_CO_TIME_IN", system_co_timein);
                                    comp_validation.put("SYSTEM_CO_TIME_OUT", system_co_timeout);

                                    arlData1.add(comp_validation);

                                    linear_resultData.setVisibility(View.VISIBLE);

                                    if (allow_co.equals("1")) {
                                        txt_result.setText("Comp off Start Time and end Time should be between " + co_timein + " And " + co_timeout);
                                    } else {

                                        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                                        // textView is the TextView view that should display it
                                        //txt_result.setText("selected date : "+currentDateTimeString);

                                        txt_result.setText("Compoff request is not allowed for the selected date.");
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
                jsonObjectRequest.setRetryPolicy(retryPolicy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetValidateCompOffDateAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String comp_off_date_from, comp_off_time_from, comp_off_date_to, comp_off_time_to, System_time_in, System_time_out;

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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ValidateCompOffRequestDate" + "/" + comp_off_date_from + "/" + comp_off_time_from + "/" + comp_off_date_to + "/" + comp_off_time_to + "/" + System_time_in + "/" + System_time_out;

                System.out.println("CompOffTimeOut_URL====" + GETREQUESTSTATUS_URL);
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
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try {
                    String allow_co = result.replaceAll("^\"+|\"+$", " ").trim();

                    if (allow_co.equals("0")) {
                        txt_result1.setVisibility(View.VISIBLE);
                        txt_result1.setText("Comp off Time In and Time Out should be between allowed Time In and Time Out.");
                    } else {
                        txt_result1.setVisibility(View.GONE);
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

    public void GetValidateCompOffDatePost(String coff_timeInDate, String coff_timeIn, String coff_timeOutDate,
                                           String coff_timeOut, String systemTimeIn, String systemTimeOut) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("COMP_OFF_TIME_IN_DATE", coff_timeInDate);
                param.put("COMP_OFF_TIME_IN", coff_timeIn);
                param.put("COMP_OFF_TIME_OUT_DATE", coff_timeOutDate);
                param.put("COMP_OFF_TIME_OUT", coff_timeOut);
                param.put("SYSTEM_TIME_IN", systemTimeIn);
                param.put("SYSTEM_TIME_OUT", systemTimeOut);

                Log.e("ReqeustData", "Json->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ValidateCompOffRequestDatePost";
                Log.e("URL", url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String allow_co = response.getString("ValidateCompOffRequestDatePostResult");

                                    if (allow_co.equals("0")) {
                                        txt_result1.setVisibility(View.VISIBLE);
                                        txt_result1.setText("Comp off Time In and Time Out should be between allowed Time In and Time Out.");
                                    } else {
                                        txt_result1.setVisibility(View.GONE);
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

    private class GetValidateCompOffDateAsync1 extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String comp_off_date_from, comp_off_time_from, comp_off_date_to, comp_off_time_to, System_time_in, System_time_out;

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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ValidateCompOffRequestDate" + "/" + comp_off_date_from + "/" + comp_off_time_from + "/" + comp_off_date_to + "/" + comp_off_time_to + "/" + System_time_in + "/" + System_time_out;

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
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try {
                    String allow_co = result.replaceAll("^\"+|\"+$", " ").trim();

                    if (allow_co.equals("0")) {
                        txt_result1.setVisibility(View.VISIBLE);
                        txt_result1.setText("Comp off Time In and Time Out should be between allowed Time In and Time Out.");
                    } else {
                        txt_result1.setVisibility(View.GONE);
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

    private class GetCompOffOvertimeReason extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCompOffOvertimeReason/1";

                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        GETREQUESTSTATUS_URL, "GET");

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
                    HashMap<String, String> requestStatusmap;
                    // ArrayList<String> requestArray;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    //requestArray=new ArrayList<String>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            requestStatusmap = new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            String key = explrObject.getString("CompOffOvertimeReasonId");
                            String value = explrObject.getString("Reason");
                            // requestArray.add(value);
                            requestStatusmap.put("KEY", key);
                            requestStatusmap.put("VALUE", value);

                            arlRequestStatusData.add(requestStatusmap);
                        }
                        System.out.println("Array===" + arlRequestStatusData);

                        customspinnerAdapter = new CustomSpinnerAdapter(getActivity(), arlRequestStatusData);
                        spin_reason_cor.setAdapter(customspinnerAdapter);

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
}
