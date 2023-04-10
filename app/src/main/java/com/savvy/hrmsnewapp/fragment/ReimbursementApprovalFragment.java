package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.ReimbursementApprovalAdapter;
import com.savvy.hrmsnewapp.model.GetReimbursementRequestDetailResult;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.savvy.hrmsnewapp.fragment.ConveyanceRequestFragment.MY_PREFS_NAME;

public class ReimbursementApprovalFragment extends BaseFragment {

    SharedPreferences sharedPreferences;
    String employeeID;
    static int SelectedYear, SelectedMonth;
    GetReimbursementRequestDetailResult approvalModel;
    ReimbursementApprovalAdapter approvalAdapter;
    List<GetReimbursementRequestDetailResult> arrayList = new ArrayList<>();
    RecyclerView recyclerView;
    TextView tvNoData;
    Spinner spMonthSpinner, spYearSpinner;
    String[] monthName;
    String[] monthValue;
    String[] yearName;
    Button btnApprove, btnReject;
    EditText edtComment;
    LinearLayout llBottomLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        employeeID = sharedPreferences.getString("EmpoyeeId", "");
        approvalAdapter = new ReimbursementApprovalAdapter();
        getMonthData();
        getYearData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mail, container, false);
        recyclerView = rootView.findViewById(R.id.rv_approval_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(approvalAdapter);
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        spMonthSpinner = rootView.findViewById(R.id.sp_month_spinner);
        spYearSpinner = rootView.findViewById(R.id.sp_year_spinner);
        btnApprove = rootView.findViewById(R.id.btn_appprove);
        btnReject = rootView.findViewById(R.id.btn_reject);
        edtComment = rootView.findViewById(R.id.edt_Comment);
        llBottomLayout = rootView.findViewById(R.id.ll_bottom_layout);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getApprovalData();

        spMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedMonth = Integer.parseInt(monthValue[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedYear = Integer.parseInt(yearName[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnApprove.setOnClickListener(view -> {


            if (compareMonthAndYear()) {
                String xmlData = approvalAdapter.XMLString();
                if (xmlData.equals("")) {
                    Toast.makeText(requireActivity(), "Please select data", Toast.LENGTH_SHORT).show();
                } else if (edtComment.getText().toString().trim().equals("")) {
                    Toast.makeText(requireActivity(), "Please enter comment", Toast.LENGTH_SHORT).show();
                } else {
                    sendApprovalRequest("True", edtComment.getText().toString().trim(), xmlData);
                }
            }

        });
        btnReject.setOnClickListener(view -> {

            String xmlData = approvalAdapter.XMLString();
            if (xmlData.equals("")) {
                Toast.makeText(requireActivity(), "Please select data", Toast.LENGTH_SHORT).show();
            } else if (edtComment.getText().toString().trim().equals("")) {
                Toast.makeText(requireActivity(), "Please enter comment", Toast.LENGTH_SHORT).show();
            } else {
                sendApprovalRequest("False", edtComment.getText().toString().trim(), xmlData);
            }
        });
    }

    private boolean compareMonthAndYear() {
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH) + 1;

        if (!(SelectedMonth >= currentMonth)) {
            Toast.makeText(requireActivity(), "You can not select previous Month !", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(SelectedYear >= currentYear)) {
            Toast.makeText(requireActivity(), "You can not select previous Year !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendApprovalRequest(String status, String comment, String xmlData) {
        if (Utilities.isNetworkAvailable(requireActivity())) {
            showProgressDialog();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ProcessReimbursementRequest";
            JSONObject params_final = new JSONObject();
            try {
                params_final.put("employeeId", employeeID);
                params_final.put("status", status);
                params_final.put("comment", comment);
                params_final.put("xmlData", xmlData);
                params_final.put("PayMonth", String.valueOf(SelectedMonth));
                params_final.put("PayYear", String.valueOf(SelectedYear));
                params_final.put("PayWithPayroll", "True");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    response -> {
                        dismissProgressDialog();
                        Log.e("TAG", "sendApprovalRequest: " + response);
                        try {
                            String result = response.getString("ProcessReimbursementRequestResult");
                            Log.e("TAG", "sendApprovalRequest: " + result);
                            if (Integer.parseInt(result) > 0) {
                                Toast.makeText(requireActivity(), "Reimbursement Request Processed successfully.", Toast.LENGTH_SHORT).show();
                                edtComment.setText("");
                                getApprovalData();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error", "" + error.getMessage());
                    dismissProgressDialog();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
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
            VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getMonthData() {
        String monthURL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMonthName";
        JsonArrayRequest jsonmonthrequest = new JsonArrayRequest(monthURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("TAG", "onResponse: " + response);
                if (response.length() > 0) {
                    monthName = new String[response.length()];
                    monthValue = new String[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            monthName[i] = response.getJSONObject(i).getString("Month_Name");
                            monthValue[i] = response.getJSONObject(i).getString("Month_Value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> aa = new ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, monthName);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spMonthSpinner.setAdapter(aa);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        int socketTimeout = 3000000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonmonthrequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonmonthrequest);
    }

    private void getYearData() {
        String monthURL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetYear";
        JsonArrayRequest jsonYearRequest = new JsonArrayRequest(monthURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    yearName = new String[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            yearName[i] = response.getJSONObject(i).getString("YEAR");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> aa = new ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, yearName);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spYearSpinner.setAdapter(aa);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        int socketTimeout = 3000000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonYearRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonYearRequest);
    }

    private void getApprovalData() {
        if (Utilities.isNetworkAvailable(requireActivity())) {
            showProgressDialog();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetReimbursementRequestDetail";
            JSONObject params_final = new JSONObject();
            try {
                params_final.put("employeeId", employeeID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    response -> {
                        dismissProgressDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("GetReimbursementRequestDetailResult"));

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    approvalModel = new GetReimbursementRequestDetailResult();
                                    approvalModel.setTokenNo(jsonArray.getJSONObject(i).getString("TOKEN_NO"));
                                    approvalModel.setEmployeeCode(jsonArray.getJSONObject(i).getString("EMPLOYEE_CODE"));
                                    approvalModel.setEmployeeCode(jsonArray.getJSONObject(i).getString("EMPLOYEE_NAME"));
                                    approvalModel.setRrTaxabaleAmount(jsonArray.getJSONObject(i).getString("RR_TAXABALE_AMOUNT"));
                                    approvalModel.setRrNonTaxabaleAmount(jsonArray.getJSONObject(i).getString("RR_NON_TAXABALE_AMOUNT"));
                                    approvalModel.setRcReimbursementName(jsonArray.getJSONObject(i).getString("RC_REIMBURSEMENT_NAME"));
                                    approvalModel.setRrApprovedTaxabaleAmount(jsonArray.getJSONObject(i).getString("RR_APPROVED_TAXABALE_AMOUNT"));
                                    approvalModel.setRrApprovedNonTaxabaleAmount(jsonArray.getJSONObject(i).getString("RR_APPROVED_NON_TAXABALE_AMOUNT"));
                                    approvalModel.setRrReimbursementRequestMonth(jsonArray.getJSONObject(i).getString("RR_REIMBURSEMENT_REQUEST_MONTH"));
                                    approvalModel.setRrReimbursementPayInYear(jsonArray.getJSONObject(i).getString("RR_REIMBURSEMENT_PAY_IN_YEAR"));

                                    approvalModel.setErfsRequestId(jsonArray.getJSONObject(i).getString("ERFS_REQUEST_ID"));
                                    approvalModel.setRequestStatusId(jsonArray.getJSONObject(i).getString("REQUEST_STATUS_ID"));
                                    approvalModel.setErfsActionLevelSequence(jsonArray.getJSONObject(i).getString("ERFS_ACTION_LEVEL_SEQUENCE"));
                                    approvalModel.setMaxActionLevelSequence(jsonArray.getJSONObject(i).getString("MAX_ACTION_LEVEL_SEQUENCE"));
                                    approvalModel.setReimbursementStatus(jsonArray.getJSONObject(i).getString("REIMBURSEMENT_STATUS"));
                                    approvalModel.setRrEmployeeId(jsonArray.getJSONObject(i).getString("RR_EMPLOYEE_ID"));
                                    approvalModel.setErfsRequestFlowId(jsonArray.getJSONObject(i).getString("ERFS_REQUEST_FLOW_ID"));
                                    approvalModel.setRrApprovedNonTaxabaleAmount(jsonArray.getJSONObject(i).getString("RR_APPROVED_NON_TAXABALE_AMOUNT"));
                                    approvalModel.setRrApprovedTaxabaleAmount(jsonArray.getJSONObject(i).getString("RR_APPROVED_TAXABALE_AMOUNT"));
                                    arrayList.add(approvalModel);
                                }
                                approvalAdapter.addItems(arrayList);
                                recyclerView.setVisibility(View.VISIBLE);
                                tvNoData.setVisibility(View.GONE);
                                llBottomLayout.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                tvNoData.setVisibility(View.VISIBLE);
                                llBottomLayout.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dismissProgressDialog();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error", "" + error.getMessage());
                    dismissProgressDialog();
                }
            });

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }
}
