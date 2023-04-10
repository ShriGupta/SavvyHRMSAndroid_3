package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.fragment.ODStatusFragment.MY_PREFS_NAME;
/*
import static io.fabric.sdk.android.Fabric.TAG;
*/

public class ODRequestConFicciFragment extends BaseFragment {
    ODRequestConFicciFragment.SendRequestButtonAsynTask sendRequestButtonAsynTask;
    ODRequestConFicciAdapter odRequestConFicciAdapter;
    ODRequestConFicciFragment.ODRequestResultAsynTask odRequestResultAsynTask;
    Button odc_FromDate, odc_ToDate, getButton, final_SendRequest, odc_Reset;
    EditText odc_remarks;
    LinearLayout odcCompareDateLayout;
    CustomTextView odc_txtDataNotFound, odc_CompareDateTextView;
    RecyclerView recyclerView;
    ScrollView scrollView;
    CalanderHRMS calanderHRMS;
    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    CoordinatorLayout coordinatorLayout;
    String employeeId = "";
    SharedPreferences shared;
    ArrayList arrayList;
    ProgressDialog pDialog;
    String REQUEST_ID = "0", OD_REQUEST_TYPE = "0", OD_REQUEST_SUB_TYPE = "0", OD_TYPE = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_odrequest_con_ficc, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        odc_FromDate = getActivity().findViewById(R.id.odc_FromDate);
        odc_ToDate = getActivity().findViewById(R.id.odc_todate);
        getButton = getActivity().findViewById(R.id.odc_getButton);
        final_SendRequest = getActivity().findViewById(R.id.odc_btn_Request);
        odc_Reset = getActivity().findViewById(R.id.odc_btn_Reset);

        odc_remarks = getActivity().findViewById(R.id.odc_edt_Reson);

        odcCompareDateLayout = getActivity().findViewById(R.id.Linearodc_result_compareDate);
        odc_CompareDateTextView = getActivity().findViewById(R.id.resultodc_compareDate);
        odc_txtDataNotFound = getActivity().findViewById(R.id.odc_txtDataNotFound);

        scrollView = getActivity().findViewById(R.id.odc_recyclerScrollview);
        scrollView.setVisibility(View.INVISIBLE);

        recyclerView = getActivity().findViewById(R.id.odc_data_Recycler);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        odc_FromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odc_FromDate.setText("");
                calanderHRMS.datePicker(odc_FromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = odc_FromDate.getText().toString().trim();
                            String ToDate = odc_ToDate.getText().toString().trim();

                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
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

        odc_ToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odc_ToDate.setText("");
                calanderHRMS.datePicker(odc_ToDate);
                handler1 = new Handler();
                runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = odc_FromDate.getText().toString().trim();
                            String ToDate = odc_ToDate.getText().toString().trim();

                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
                            } else {
                                handler1.postDelayed(runnable1, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler1.postDelayed(runnable1, 1000);

            }
        });
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FROM_DATE = odc_FromDate.getText().toString().trim().replace("/", "-");
                String TO_DATE = odc_ToDate.getText().toString().trim().replace("/", "-");
                if (FROM_DATE.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select From Date");
                    return;
                } else if (TO_DATE.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select To Date");
                    return;
                } else {
                    getButtonRequestStatus(employeeId, FROM_DATE, TO_DATE);
                }
            }
        });

        final_SendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String _odDetails = odRequestConFicciAdapter.xmlData();
                String _fromDate = odc_FromDate.getText().toString().replace("/", "-");
                String _toDate = odc_ToDate.getText().toString().replace("/", "-");
                String _Reson = odc_remarks.getText().toString().trim().replaceAll("\\s", "_");

                if (_odDetails.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Any checkBox");
                } else if (_Reson.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                } else {

                    try {
                        if (Utilities.isNetworkAvailable(getActivity())) {
                            sendRequestButtonAsynTask = new ODRequestConFicciFragment.SendRequestButtonAsynTask();
                            sendRequestButtonAsynTask.employeeId = employeeId;
                            sendRequestButtonAsynTask.REQUEST_ID = REQUEST_ID;
                            sendRequestButtonAsynTask._fromDate = _fromDate;
                            sendRequestButtonAsynTask._toDate = _toDate;
                            sendRequestButtonAsynTask.OD_REQUEST_TYPE = OD_REQUEST_TYPE;
                            sendRequestButtonAsynTask.OD_REQUEST_SUB_TYPE = OD_REQUEST_SUB_TYPE;
                            sendRequestButtonAsynTask.OD_TYPE = OD_TYPE;
                            sendRequestButtonAsynTask._Reson = _Reson;
                            sendRequestButtonAsynTask._departmentID = "0";
                            sendRequestButtonAsynTask._odDetails = _odDetails;

                            sendRequestButtonAsynTask.execute();
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        odc_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllData();
            }
        });

    }


    public void getCompareDate(String fromDate, String toDate) {
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
                                        odc_CompareDateTextView.setText("From Date should be less than or equal To Date!");
                                        odcCompareDateLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        odcCompareDateLayout.setVisibility(View.GONE);
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
                jsonRequest.setRetryPolicy(policy);
                requestQueue.add(jsonRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getButtonRequestStatus(String employeeId, String fromDate, String toDate) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                odRequestResultAsynTask = new ODRequestConFicciFragment.ODRequestResultAsynTask();
                odRequestResultAsynTask.employeeId = employeeId;
                odRequestResultAsynTask.fromDate = fromDate;
                odRequestResultAsynTask.toDate = toDate;
                odRequestResultAsynTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ODRequestResultAsynTask extends AsyncTask<String, String, String> {
        String employeeId, fromDate, toDate;

        @Override
        protected void onPreExecute() {
            arrayList = new ArrayList();
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            final String ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeRegisterDetailsForODPermanentEmployee/" + fromDate + "/" + toDate + "/" + employeeId;
            Log.d("TAG", "doInBackground: " + ATTENDANCE_URL);
            JSONParser jParser = new JSONParser(getActivity());
            String json = jParser.makeHttpRequest(ATTENDANCE_URL, "GET");
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            try {
                pDialog.dismiss();
                String ATTENDANCE_DATE = "";
                HashMap<String, String> attendanceData;
                JSONArray jsonArray = new JSONArray(json);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        attendanceData = new HashMap<>();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ATTENDANCE_DATE = jsonObject.getString("EAR_ATTENDANCE_DATE");
                        attendanceData.put("EAR_ATTENDANCE_DATE", ATTENDANCE_DATE);
                        arrayList.add(attendanceData);
                    }
                } else {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }

                odRequestConFicciAdapter = new ODRequestConFicciAdapter(getActivity(), coordinatorLayout, arrayList, new ODRequestConFicciAdapter.ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }
                });
                recyclerView.setAdapter(odRequestConFicciAdapter);
                scrollView.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    public class SendRequestButtonAsynTask extends AsyncTask<String, String, String> {
        String employeeId, _odDetails, _departmentID, _Reson, _fromDate, _toDate, REQUEST_ID, OD_REQUEST_TYPE, OD_REQUEST_SUB_TYPE, OD_TYPE;

        @Override
        protected String doInBackground(String... strings) {
            final String SEND_REQUEST_URL = Constants.IP_ADDRESS + "/savvymobileservice.svc/SendODRequestFicci/" + employeeId + "/" +
                    REQUEST_ID + "/" +
                    _fromDate + "/" +
                    _toDate + "/" +
                    OD_REQUEST_TYPE + "/" +
                    OD_REQUEST_SUB_TYPE + "/" +
                    OD_TYPE + "/" +
                    _Reson + "/" +
                    _departmentID + "/" +
                    _odDetails;
            Log.d("TAG", "doInBackground: " + SEND_REQUEST_URL);
            JSONParser jParser = new JSONParser(getActivity());
            String json = jParser.makeHttpRequest(SEND_REQUEST_URL, "GET");
            Log.d("TAG", "doInBackground: Response" + json);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int value = 0;
            try {
                System.out.println(result);
                result = result.replaceAll("^\"|\"$", "").trim();
                value = Integer.parseInt(result);
                switch (value) {
                    case -1:
                        Utilities.showDialog(coordinatorLayout, "On Duty Request on the same date and same type already exists.");
                        break;
                    case -2:
                        Utilities.showDialog(coordinatorLayout, "On Duty Request for previous payroll cycle not allow.");
                        break;
                    case -3:
                        Utilities.showDialog(coordinatorLayout, "OD/AR/Leave for same date already requested.");
                        break;
                    case -4:
                        Utilities.showDialog(coordinatorLayout, "Attendance period is locked, can not apply request for the period");
                        break;
                    case 0:
                        Utilities.showDialog(coordinatorLayout, "Error during sending on duty request.");
                        break;
                    default:
                        Utilities.showDialog(coordinatorLayout, "OD Request Sent Successfully.");
                        removeAllData();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
            }
        }
    }

    private void removeAllData() {
        odc_FromDate.setText("");
        odc_ToDate.setText("");
        odc_remarks.setText("");
        scrollView.setVisibility(View.INVISIBLE);
    }
}
