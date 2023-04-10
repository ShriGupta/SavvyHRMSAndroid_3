package com.savvy.hrmsnewapp.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.savvy.hrmsnewapp.adapter.ReimbursementStatusAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ReimbursementStatusFragment extends BaseFragment implements ItemClickListener {
    ReimbursementStatusAdapter reimbursementStatusAdapter;
    RecyclerView recyclerView;
    SharedPreferences shared;
    String employeeId = "";
    String token = "";
    ArrayList<String> spinArrayID;
    ArrayList<String> spinArray;
    Button fromDate, toDate, btnSearch, btnSpinStatus;
    ArrayList<HashMap<String, String>> arlRequestStatusData;
    ReimbursementStatusFragment.ReimbursementSpinnerTask reimbursementSpinnerTask;
    HashMap<String, String> mapdata;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    CalanderHRMS calanderHRMS;
    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    CustomTextView txtDataNotFound, txtComapreDate;
    LinearLayout travelCompareDateLayout;
    List<HashMap<String, String>> reArrayList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinArrayID = new ArrayList();
        spinArray = new ArrayList();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        employeeId = (shared.getString("EmpoyeeId", ""));
        reimbursementStatusAdapter = new ReimbursementStatusAdapter(this);
        getStatusList();
        getReimburesementStatusData(employeeId, token, "-", "-", "0,1,6,7");

    }

    private void getStatusList() {
        arlRequestStatusData = new ArrayList<>();
        try {
            if (isNetworkAvailable(requireActivity())) {
                reimbursementSpinnerTask = new ReimbursementStatusFragment.ReimbursementSpinnerTask();
                reimbursementSpinnerTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClickItem(int position, String requestid) {
        // pull back request
        showPullBackDialog(requestid);
    }

    private void showPullBackDialog(String requestid) {
        assert getActivity() != null;
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.pullback_dialogbox);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
        CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
        Button btn_ApproveGo, btn_close;
        btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
        btn_close = dialog.findViewById(R.id.btn_close);

        edt_comment_dialog.setText("Are you sure, Do you want to pullback Reimbursement Request.");
        txt_header.setText("Pull Back Reimbursement Request");

        btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPullBackRequest(requestid);
                dialog.dismiss();
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void sendPullBackRequest(String requestid) {
        if (isNetworkAvailable(requireContext())) {
            showProgressDialog();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackReimbursementRequest";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("employeeId", employeeId);
                jsonObject.put("requestID", requestid);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissProgressDialog();
                        try {
                            if (Integer.parseInt(response.getString("PullBackReimbursementRequestResult")) > 0) {
                                Utilities.showDialog(coordinatorLayout, "Pullback request execute successfully.");
                                getReimburesementStatusData(employeeId, token, "-", "-", "0,1,6,7");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
                    dismissProgressDialog();
                    Utilities.showDialog(coordinatorLayout, error.getMessage());
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
                RetryPolicy policy = new DefaultRetryPolicy(3000000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reimbursement_status, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = requireActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        fromDate = requireActivity().findViewById(R.id.btn_StatusFromDate);
        toDate = requireActivity().findViewById(R.id.btn_StatusToDate);
        btnSpinStatus = requireActivity().findViewById(R.id.btn_spin_travel_Status);
        btnSearch = requireActivity().findViewById(R.id.btn_travelStatus_search);
        txtDataNotFound = requireActivity().findViewById(R.id.travelTxtDataNotFound);
        txtComapreDate = requireActivity().findViewById(R.id.travelCompareDateTextView);
        travelCompareDateLayout = requireActivity().findViewById(R.id.travelCompareDateLayout);

        recyclerView = requireActivity().findViewById(R.id.rc_reimbursement_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reimbursementStatusAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        fromDate.setOnClickListener(v -> {
            fromDate.setText("");
            calanderHRMS.datePicker(fromDate);

            handler = new Handler();
            runnable = () -> {
                try {
                    String FromDate = fromDate.getText().toString().trim();
                    String ToDate = toDate.getText().toString().trim();

                    if (!FromDate.equals("") && (!ToDate.equals(""))) {
                        getCompareDate(FromDate, ToDate);
                    } else {
                        handler.postDelayed(runnable, 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            handler.postDelayed(runnable, 1000);
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(toDate);
                handler1 = new Handler();
                runnable1 = () -> {
                    try {
                        String FromDate = fromDate.getText().toString().trim();
                        String ToDate = toDate.getText().toString().trim();

                        if (!FromDate.equals("") && (!ToDate.equals(""))) {
                            getCompareDate(FromDate, ToDate);
                        } else {
                            handler1.postDelayed(runnable1, 1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                handler1.postDelayed(runnable1, 1000);
            }
        });
        btnSpinStatus.setOnClickListener(v -> {

            try {
                final CheckBox[] checkBox = new CheckBox[arlRequestStatusData.size()];
                final TextView[] txtView = new TextView[arlRequestStatusData.size()];
                LinearLayout l1 = new LinearLayout(getActivity());
                l1.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle("Please Select");

                try {

                    for (int i = 0; i < arlRequestStatusData.size(); i++) {
                        mapdata = arlRequestStatusData.get(i);

                        checkBox[i] = new CheckBox(getActivity());
                        txtView[i] = new TextView(getActivity());

                        checkBox[i].setTextSize(10);
                        checkBox[i].setPadding(0, 0, 0, 0);
                        checkBox[i].setLines((int) 2.0);
                        checkBox[i].setText(mapdata.get("VALUE"));
                        txtView[i].setText(mapdata.get("KEY"));
                        l1.addView(checkBox[i]);
                        final int finalI = i;
                        try {

                            checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {

                                        String spin_new_value = checkBox[finalI].getText().toString();
                                        String spin_new_id = txtView[finalI].getText().toString();

                                        if (!(spinArray.contains(spin_new_value))) {
                                            spinArray.add(spin_new_value);
                                            spinArrayID.add(spin_new_id);
                                        }

                                    } else {
                                        spinArray.remove(checkBox[finalI].getText().toString());
                                        spinArrayID.remove(txtView[finalI].getText().toString());
                                    }

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    for (int k = 0; k < spinArrayID.size(); k++) {
                        int index = Integer.parseInt(spinArrayID.get(k));
                        Log.e("Spin Index", "" + index);
                        for (int j = 0; j < arlRequestStatusData.size(); j++) {
                            mapdata = arlRequestStatusData.get(j);

                            int spin_index = Integer.parseInt(mapdata.get("KEY"));

                            if (index == spin_index) {
                                checkBox[spin_index].setChecked(true);
                            }
                        }
                    }
                    builder.setView(l1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        btnSpinStatus.setText(spinArray.toString().replace("[", "").replace("]", ""));
                    }
                });
                builder.show();
                if (spinArrayID.size() == 0) {
                    checkBox[0].setChecked(true);
                    checkBox[1].setChecked(true);
                    checkBox[6].setChecked(true);
                    checkBox[7].setChecked(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        btnSearch.setOnClickListener(v -> {
            try {
                String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                if (keyid.equals("")) {
                    keyid = "0,1,6,7";
                }
                String gettodate = toDate.getText().toString().replace("/", "-");
                String getfromdate = fromDate.getText().toString().replace("/", "-");
                System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                if (getfromdate.equals(""))
                    getfromdate = "-";

                if (gettodate.equals(""))
                    gettodate = "-";
                if (travelCompareDateLayout.getVisibility() == View.VISIBLE) {
                    Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                } else {
                    getReimburesementStatusData(employeeId, token, getfromdate, gettodate, keyid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getReimburesementStatusData(String employeeId, String token, String getfromdate, String gettodate, String keyid) {

        if (isNetworkAvailable(requireContext())) {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetReimbursementRequestStatus";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("employeeId", employeeId);
                jsonObject.put("securityToken", token);
                jsonObject.put("FromDate", getfromdate);
                jsonObject.put("ToDate", gettodate);
                jsonObject.put("RequestStatus", keyid);

                Log.e("TAG", "getReimburseType: " + jsonObject.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        reArrayList.clear();
                        reimbursementStatusAdapter.clearItems();
                        HashMap<String, String> arraMap;
                        try {
                            JSONArray jsonElements = response.getJSONArray("GetReimbursementRequestStatusResult");

                            if (jsonElements.length() > 0) {
                                for (int i = 0; i < jsonElements.length(); i++) {
                                    arraMap = new HashMap<>();
                                    arraMap.put("TOKEN_NO", jsonElements.getJSONObject(i).getString("TOKEN_NO"));
                                    arraMap.put("RC_REIMBURSEMENT_NAME", jsonElements.getJSONObject(i).getString("RC_REIMBURSEMENT_NAME"));
                                    arraMap.put("RR_REIMBURSEMENT_REQUEST_MONTH_NAME", jsonElements.getJSONObject(i).getString("RR_REIMBURSEMENT_REQUEST_MONTH_NAME"));
                                    arraMap.put("RR_REIMBURSEMENT_REQUEST_YEAR", jsonElements.getJSONObject(i).getString("RR_REIMBURSEMENT_REQUEST_YEAR"));
                                    arraMap.put("REQUEST_STATUS", jsonElements.getJSONObject(i).getString("REQUEST_STATUS"));
                                    arraMap.put("RR_NON_TAXABALE_AMOUNT", jsonElements.getJSONObject(i).getString("RR_NON_TAXABALE_AMOUNT"));
                                    arraMap.put("RR_TAXABALE_AMOUNT", jsonElements.getJSONObject(i).getString("RR_TAXABALE_AMOUNT"));
                                    arraMap.put("RR_REMARKS", jsonElements.getJSONObject(i).getString("RR_REMARKS"));
                                    arraMap.put("ERFS_REQUEST_ID", jsonElements.getJSONObject(i).getString("ERFS_REQUEST_ID"));
                                    reArrayList.add(arraMap);
                                }
                                reimbursementStatusAdapter.addItems(reArrayList);

                            } else {
                                Utilities.showDialog(coordinatorLayout, "No Data");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, error -> {
                    dismissProgressDialog();
                    Utilities.showDialog(coordinatorLayout, error.getMessage());
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
                RetryPolicy policy = new DefaultRetryPolicy(3000000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ReimbursementSpinnerTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetRequestStatus";
                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(requireActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
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
            dismissProgressDialog();
            try {
                HashMap<String, String> requestStatusmap;
                JSONArray jsonArray = new JSONArray(result);
                System.out.println("jsonArray===" + jsonArray);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        requestStatusmap = new HashMap<>();
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        String key = explrObject.getString("KEY");
                        String value = explrObject.getString("VALUE");
                        requestStatusmap.put("KEY", key);
                        requestStatusmap.put("VALUE", value);
                        arlRequestStatusData.add(requestStatusmap);
                    }
                } else {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    public void getCompareDate(String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);


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
                                        txtComapreDate.setText("From Date should be less than or equal To Date!");
                                        travelCompareDateLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        travelCompareDateLayout.setVisibility(View.GONE);
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
