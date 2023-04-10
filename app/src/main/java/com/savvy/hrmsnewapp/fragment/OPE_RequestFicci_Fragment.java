package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.OPERequestFicciAdapter;
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

/*import io.fabric.sdk.android.Fabric;*/

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.SavvyHRMSApplication.TAG;

public class OPE_RequestFicci_Fragment extends BaseFragment {

    OPE_RequestFicci_Fragment.GetButtonAsyn getButtonAsyn;
    OPE_RequestFicci_Fragment.ApplyButtonRequestAsync applyButtonRequestAsync;
    OPE_RequestFicci_Fragment.OPERequestAsynTask opeRequestAsynTask;
    private Button pickFromDate, pickToDate, getButton, applyButton;
    OPERequestFicciAdapter opeRequestFicciAdapter;
    private LinearLayout compareDateLayout;
    private CustomTextView customTextView, tvDataNotFound;
    CalanderHRMS calanderHRMS;
    Handler handler;
    Runnable rRunnable;
    String FROM_DATE = "", TO_DATE = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    String fromDate = "", toDate = "";
    ArrayList<HashMap<String, String>> arrayList;
    ArrayList<HashMap<String, String>> spinnerDataList;
    RecyclerView recyclerView;
    NestedScrollView scrollView;
    ProgressDialog pDialog;
    String departmentNameArray[];
    String departmentIDArray[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
    }

    private void loadAuthorityDepartmentList(String employeeId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                opeRequestAsynTask = new OPE_RequestFicci_Fragment.OPERequestAsynTask();
                opeRequestAsynTask.empid = employeeId;
                opeRequestAsynTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class OPERequestAsynTask extends AsyncTask<String, String, String> {
        String empid;

        @Override
        protected String doInBackground(String... params) {

            try {
                final String APPROVAL_AUTHORITY_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetDepartmentListForOD/" + empid;
                JSONParser jParser = new JSONParser(getActivity());
                Log.d(TAG, "doInBackground: APPROVAL_AUTHORITY_URL: " + APPROVAL_AUTHORITY_URL);
                String json = jParser.makeHttpRequest(APPROVAL_AUTHORITY_URL, "GET");
                return json;
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            try {
                HashMap<String, String> odRequestMap;
                JSONArray jsonArray = new JSONArray(json);
                System.out.println("jsonArray===" + jsonArray);
                spinnerDataList = new ArrayList<>();

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        odRequestMap = new HashMap<>();
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        String key = explrObject.getString("DepartmentId");
                        String value = explrObject.getString("DepartmentName");
                        odRequestMap.put("KEY", key);
                        odRequestMap.put("VALUE", value);
                        spinnerDataList.add(odRequestMap);
                    }
                } else {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ope__request_ficci_, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        calanderHRMS = new CalanderHRMS(getActivity());
        pickFromDate = getActivity().findViewById(R.id.pick_FromDate);
        pickToDate = getActivity().findViewById(R.id.pick_ToDate);
        compareDateLayout = getActivity().findViewById(R.id.compareDateLayout);
        customTextView = getActivity().findViewById(R.id.compareDateTextview);
        getButton = getActivity().findViewById(R.id.getResult);
        applyButton = getActivity().findViewById(R.id.appyButton);
        tvDataNotFound = getActivity().findViewById(R.id.tvDataNotFound);
        scrollView = getActivity().findViewById(R.id.opeScrollView);

        recyclerView = getActivity().findViewById(R.id.opeRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        applyButton.setVisibility(View.INVISIBLE);

        pickFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickFromDate.setText("");
                calanderHRMS.datePicker(pickFromDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = pickFromDate.getText().toString().trim();
                            TO_DATE = pickToDate.getText().toString().trim();

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
            }
        });
        pickToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickToDate.setText("");
                calanderHRMS.datePicker(pickToDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = pickFromDate.getText().toString().trim();
                            TO_DATE = pickToDate.getText().toString().trim();

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
            }
        });
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                fromDate = pickFromDate.getText().toString().replace("/", "-");
                toDate = pickToDate.getText().toString().replace("/", "-");

                if (fromDate.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select From Date");
                    return;
                } else if (toDate.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select To Date");
                    return;
                } else {
                    loadAuthorityDepartmentList(employeeId);
                    getButtonStatus(employeeId, fromDate, toDate);
                }

            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyButtonRequest(employeeId);
            }
        });
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
                                        customTextView.setText("From Date should be less than or equal To Date!");
                                        compareDateLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        compareDateLayout.setVisibility(View.GONE);
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
                });

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

    private void getButtonStatus(String empoyeeId, String fromDate, String toDate) {
        getButtonAsyn = new OPE_RequestFicci_Fragment.GetButtonAsyn();
        getButtonAsyn.employeeId = empoyeeId;
        getButtonAsyn.fromDate = fromDate;
        getButtonAsyn.toDate = toDate;
        getButtonAsyn.execute();
    }

    class GetButtonAsyn extends AsyncTask<String, String, String> {

        String employeeId, fromDate, toDate;

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

            try {

                String GETREQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetOPERequestData/" + employeeId + "/" + fromDate + "/" + toDate;
                System.out.println("GETREQUEST_URL====" + GETREQUEST_URL);
                JSONParser jParser = new JSONParser(getActivity());
                String result = jParser.makeHttpRequest(GETREQUEST_URL, "GET");

                Log.d(TAG, "doInBackground: " + result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            arrayList = new ArrayList();
            HashMap<String, String> mapData;
            try {
                pDialog.dismiss();
                JSONArray jsonArray = new JSONArray(result);

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mapData = new HashMap<>();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        mapData.put("WORKINGSTATUS", jsonObject.getString("WORKINGSTATUS"));
                        mapData.put("EAR_ATTENDANCE_DATE", jsonObject.getString("EAR_ATTENDANCE_DATE"));
                        mapData.put("EAR_TIME_IN", jsonObject.getString("EAR_TIME_IN"));
                        mapData.put("EAR_TIME_OUT", jsonObject.getString("EAR_TIME_OUT"));
                        mapData.put("OPE_HOURS", jsonObject.getString("OPE_HOURS"));
                        mapData.put("Amount", jsonObject.getString("Amount"));
                        arrayList.add(mapData);
                    }
                    loadRecyclerViewData(arrayList, spinnerDataList);

                } else {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
            }

        }
    }

    private void loadRecyclerViewData(ArrayList<HashMap<String, String>> arrayList, ArrayList<HashMap<String, String>> spinnerDataList) {

        try {
            if (spinnerDataList != null) {
                departmentNameArray = new String[spinnerDataList.size()];
                departmentIDArray = new String[spinnerDataList.size()];
                for (int i = 0; i < spinnerDataList.size(); i++) {
                    departmentNameArray[i] = spinnerDataList.get(i).get("VALUE");
                }
                for (int i = 0; i < spinnerDataList.size(); i++) {
                    departmentIDArray[i] = spinnerDataList.get(i).get("KEY");
                }

                if (arrayList.size() != 0) {
                    opeRequestFicciAdapter = new OPERequestFicciAdapter(getContext(), coordinatorLayout, arrayList, spinnerDataList, departmentNameArray, departmentIDArray);
                    recyclerView.setAdapter(opeRequestFicciAdapter);
                    applyButton.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvDataNotFound.setVisibility(View.GONE);
                } else {
                    recyclerView.setAdapter(null);
                    recyclerView.setVisibility(View.INVISIBLE);
                    applyButton.setVisibility(View.GONE);
                    tvDataNotFound.setVisibility(View.VISIBLE);
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }

            } else {
                loadAuthorityDepartmentList(employeeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }
    }

    private void applyButtonRequest(String employeeId) {
        try {
            String xmlData = "";
            xmlData = (opeRequestFicciAdapter.getxmlData().replace(":", "-"));
            if (xmlData.equals("NoCheckBox Selected")) {
                Utilities.showDialog(coordinatorLayout, "Please Select checkbox");
            } else if (xmlData.equals("dropdown")) {
                Utilities.showDialog(coordinatorLayout, "Please Select Department");
            } else if (xmlData.equals("commentbox")) {
                Utilities.showDialog(coordinatorLayout, "Please Enter Comments");
            } else {

                if (Utilities.isNetworkAvailable(getActivity())) {
                    applyButtonRequestAsync = new OPE_RequestFicci_Fragment.ApplyButtonRequestAsync();
                    applyButtonRequestAsync.employeeId = employeeId;
                    applyButtonRequestAsync.xmlData = xmlData.replaceAll("\\s", "_");
                    applyButtonRequestAsync.execute();
                } else {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }

    }

    public class ApplyButtonRequestAsync extends AsyncTask<String, String, String> {

        String employeeId, xmlData;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String APPLYREQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveRequestOPE/" + employeeId + "/" + xmlData;
                System.out.println("GETREQUEST_URL====" + APPLYREQUEST_URL);
                JSONParser jParser = new JSONParser(getActivity());
                String result = jParser.makeHttpRequest(APPLYREQUEST_URL, "GET");
                Log.d(TAG, "doInBackground: " + result);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
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
                    case 0:
                        Utilities.showDialog(coordinatorLayout, "Error during sending OPE request.");
                        break;
                    case -1:
                        Utilities.showDialog(coordinatorLayout, "OPE Request on the same date and same type already exists.");
                        break;
                    case -2:
                        Utilities.showDialog(coordinatorLayout, "OPE Request for previous payroll cycle not allow.");
                        break;
                    case -3:
                        Utilities.showDialog(coordinatorLayout, "OPE/AR/Leave for same date already requested.");
                        break;
                    case -4:
                        Utilities.showDialog(coordinatorLayout, "Attendance period is locked, can not apply request for the period");
                        break;
                    default:
                        Utilities.showDialog(coordinatorLayout, "OPE Request Sent Successfully.");
                        recyclerView.setAdapter(null);
                        recyclerView.setVisibility(View.INVISIBLE);
                        applyButton.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }
}
