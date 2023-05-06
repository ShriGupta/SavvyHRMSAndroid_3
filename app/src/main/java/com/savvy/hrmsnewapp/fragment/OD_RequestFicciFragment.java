package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.savvy.hrmsnewapp.adapter.ODRequestFicciAdapter;
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

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.fragment.ODStatusFragment.MY_PREFS_NAME;
/*import static io.fabric.sdk.android.Fabric.TAG;*/

public class OD_RequestFicciFragment extends BaseFragment {

    OD_RequestFicciFragment.ODRequestAsynTask odRequestasynctask;
    OD_RequestFicciFragment.ODRequestResultAsynTask odRequestResultAsynTask;
    OD_RequestFicciFragment.SendRequestButtonAsynTask sendRequestButtonAsynTask;
    ODRequestFicciAdapter odRequestFicciAdapter;
    ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> arrayListData;
    ArrayList arrayList;
    CalanderHRMS calanderHRMS;
    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    CustomTextView txt_result_compareDate;
    LinearLayout linear_result_compareDate;
    Spinner authoritySpinner;
    CustomSpinnerAdapter customspinnerAdapter;
    String positionId = "", positionValue = "";
    SharedPreferences shared;
    RecyclerView recyclerView;

    ScrollView recyclerScrollview;
    EditText edt_reson;
    Button btn_Request, btn_Reset;
    Button btn_fromDate, btn_toDate, getButton;
    private ODRequestFicciAdapter.ClickListener listener;
    String employeeId = "", _odDetails = "", _departmentID = "", _Reson = "", _fromDate = "", _toDate = "";
    String REQUEST_ID = "0", OD_REQUEST_TYPE = "0", OD_REQUEST_SUB_TYPE = "0", OD_TYPE = "0";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        arrayListData = new ArrayList<HashMap<String, String>>();
        arrayList = new ArrayList();
        employeeId = (shared.getString("EmpoyeeId", ""));
        authoritySpinner = new Spinner(getActivity());
        loadAuthorityDepartmentList(employeeId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_od__request_ficci, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        calanderHRMS = new CalanderHRMS(getActivity());
        btn_fromDate = getActivity().findViewById(R.id.btn_FromDate);
        btn_toDate = getActivity().findViewById(R.id.btn_todate);
        btn_Request = getActivity().findViewById(R.id.btn_Request);
        btn_Reset = getActivity().findViewById(R.id.btn_Reset);
        edt_reson = getActivity().findViewById(R.id.edt_Reson);
        linear_result_compareDate = getActivity().findViewById(R.id.Linear_result_compareDate);
        txt_result_compareDate = getActivity().findViewById(R.id.result_compareDate);
        authoritySpinner = getActivity().findViewById(R.id.authoritySpinner);

        getButton = getActivity().findViewById(R.id.getButton);
        recyclerScrollview = getActivity().findViewById(R.id.recyclerScrollview);
        recyclerScrollview.setVisibility(View.INVISIBLE);
        odRequestFicciAdapter = new ODRequestFicciAdapter(getActivity(), coordinatorLayout, arrayList, listener);
        recyclerView = getActivity().findViewById(R.id.ear_attendance_Recycler);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        btn_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fromDate.setText("");
                calanderHRMS.datePicker(btn_fromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btn_fromDate.getText().toString().trim();
                            String ToDate = btn_toDate.getText().toString().trim();

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

        btn_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_toDate.setText("");
                calanderHRMS.datePicker(btn_toDate);
                handler1 = new Handler();
                runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btn_fromDate.getText().toString().trim();
                            String ToDate = btn_toDate.getText().toString().trim();

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
                String FROM_DATE = btn_fromDate.getText().toString().trim().replace("/", "-");
                String TO_DATE = btn_toDate.getText().toString().trim().replace("/", "-");
                if (FROM_DATE.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select From Date");
                    return;
                } else if (TO_DATE.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select To Date");
                    return;
                } else if (positionValue.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Approval Authority");
                    return;
                } else {
                    getButtonRequestStatus(employeeId, FROM_DATE, TO_DATE);
                }
            }
        });
        btn_Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _departmentID = positionId;
                _odDetails = odRequestFicciAdapter.getODDetails();
                _fromDate = btn_fromDate.getText().toString().replace("/", "-");
                _toDate = btn_toDate.getText().toString().replace("/", "-");
                _Reson = edt_reson.getText().toString().replace(" ", "_");

                if (_fromDate.isEmpty()) {
                    Utilities.showDialog(coordinatorLayout, "Please Select From Date");
                    return;
                } else if (_toDate.isEmpty()) {
                    Utilities.showDialog(coordinatorLayout, "Please Select To Date");
                    return;
                } else if (_departmentID.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Approval Authority");
                    return;
                } else if (_odDetails.equals("fromtime")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter From Time");
                    return;
                } else if (_odDetails.equals("totime")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter To Time");
                    return;
                } else if (_Reson.isEmpty()) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                    return;
                }

                try {
                    if (Utilities.isNetworkAvailable(getActivity())) {
                        sendRequestButtonAsynTask = new OD_RequestFicciFragment.SendRequestButtonAsynTask();
                        sendRequestButtonAsynTask.employeeId = employeeId;
                        sendRequestButtonAsynTask.REQUEST_ID = REQUEST_ID;
                        sendRequestButtonAsynTask._fromDate = _fromDate;
                        sendRequestButtonAsynTask._toDate = _toDate;
                        sendRequestButtonAsynTask.OD_REQUEST_TYPE = OD_REQUEST_TYPE;
                        sendRequestButtonAsynTask.OD_REQUEST_SUB_TYPE = OD_REQUEST_SUB_TYPE;
                        sendRequestButtonAsynTask.OD_TYPE = OD_TYPE;
                        sendRequestButtonAsynTask._Reson = _Reson;
                        sendRequestButtonAsynTask._departmentID = _departmentID;
                        sendRequestButtonAsynTask._odDetails = _odDetails;

                        sendRequestButtonAsynTask.execute();
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        authoritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (position == 0) {
                        positionValue = "";
                        positionId = "";
                    } else if (position > 0) {
                        positionId = arrayListData.get(position - 1).get("KEY");
                        positionValue = arrayListData.get(position - 1).get("VALUE");
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
        btn_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllData();
            }
        });
    }

    private void removeAllData() {
        btn_fromDate.setText("");
        btn_toDate.setText("");
        authoritySpinner.setSelection(0);
        edt_reson.setText("");
        recyclerScrollview.setVisibility(View.INVISIBLE);
    }

    public class SendRequestButtonAsynTask extends AsyncTask<String, String, String> {
        String employeeId, _odDetails, _departmentID, _Reson, _fromDate, _toDate, REQUEST_ID, OD_REQUEST_TYPE, OD_REQUEST_SUB_TYPE, OD_TYPE;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

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
                pDialog.dismiss();
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

    private void getButtonRequestStatus(String employeeId, String fromDate, String toDate) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                odRequestResultAsynTask = new OD_RequestFicciFragment.ODRequestResultAsynTask();
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
                                        txt_result_compareDate.setText("From Date should be less than or equal To Date!");
                                        linear_result_compareDate.setVisibility(View.VISIBLE);
                                    } else {
                                        linear_result_compareDate.setVisibility(View.GONE);
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
        }
    }

    private void loadAuthorityDepartmentList(String empid) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                odRequestasynctask = new OD_RequestFicciFragment.ODRequestAsynTask();
                odRequestasynctask.empid = empid;
                odRequestasynctask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ODRequestAsynTask extends AsyncTask<String, String, String> {
        String empid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            final String APPROVAL_AUTHORITY_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetDepartmentListForOD/" + empid;
            JSONParser jParser = new JSONParser(getActivity());
            Log.d("TAG", "doInBackground: APPROVAL_AUTHORITY_URL: " + APPROVAL_AUTHORITY_URL);
            String json = jParser.makeHttpRequest(APPROVAL_AUTHORITY_URL, "GET");
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            pDialog.dismiss();

            try {
                HashMap<String, String> odRequestMap;
                JSONArray jsonArray = new JSONArray(json);
                System.out.println("jsonArray===" + jsonArray);

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        odRequestMap = new HashMap<>();
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        String key = explrObject.getString("DepartmentId");
                        String value = explrObject.getString("DepartmentName");

                        odRequestMap.put("KEY", key);
                        odRequestMap.put("VALUE", value);

                        arrayListData.add(odRequestMap);
                    }
                    System.out.println("Array===" + arrayListData);

                    customspinnerAdapter = new CustomSpinnerAdapter(getActivity(), arrayListData);
                    authoritySpinner.setAdapter(customspinnerAdapter);

                } else {
                    Toast.makeText(requireActivity(), ErrorConstants.DATA_ERROR, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireActivity(), ErrorConstants.CODE_FAILURE, Toast.LENGTH_SHORT).show();

            }
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
            final String ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeRegisterDetailsForOD/" + fromDate + "/" + toDate + "/" + employeeId;
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

                odRequestFicciAdapter = new ODRequestFicciAdapter(getActivity(), coordinatorLayout, arrayList, new ODRequestFicciAdapter.ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }
                });
                recyclerView.setAdapter(odRequestFicciAdapter);
                recyclerScrollview.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

}
