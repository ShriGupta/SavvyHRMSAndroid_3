package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CompOffAccuralApprovalMMTAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class CompOffAcuralApprovalMMTFragment extends BaseFragment implements View.OnClickListener {
    CompOffAccuralApprovalMMTAdapter accuralApprovalMMTAdapter;
    CoordinatorLayout coordinatorLayout;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> arrayList;
    CustomTextView DataNotFond;
    Button approvalGo, approvalClear;
    EditText approvalReason;
    LinearLayout componentLayout;
    Boolean actionStatus = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));

        getcOffRequestDetail(employeeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_compoff_accural_approval_mmt, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        DataNotFond = getActivity().findViewById(R.id.c_OffApprovalMMT_NoDataFound);
        recyclerView = getActivity().findViewById(R.id.c_OffApprovalMMT_RecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        componentLayout = getActivity().findViewById(R.id.c_OffApprovalMMT_componentLayout);
        approvalReason = getActivity().findViewById(R.id.c_OffApprovalMMT_edtReason);
        approvalGo = getActivity().findViewById(R.id.c_OffApprovalMMT_Go);
        approvalClear = getActivity().findViewById(R.id.c_OffApprovalMMT_Clear);

        approvalGo.setOnClickListener(this);
        approvalClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.approvalGo:
                String xmlData = accuralApprovalMMTAdapter.getXMLData();
                if (xmlData.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Any CheckBox");
                } else if (approvalReason.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                } else {
                    sendRequest(employeeId, true, approvalReason.getText().toString().trim().replaceAll("\\s", "_"), xmlData);
                }
                break;

            case R.id.approvalClear:
                String xmlData1 = accuralApprovalMMTAdapter.getXMLData();
                if (xmlData1.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Any CheckBox");
                } else if (approvalReason.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                } else {
                    sendRequest(employeeId, false, approvalReason.getText().toString().trim().replaceAll("\\s", "_"), xmlData1);
                }
                break;
        }
    }

    private void getcOffRequestDetail(String employeeId) {


        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String CompOff_CHANGE_GET_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ApprovalGetCompOffRequestDetail/" + employeeId;
            Log.d(TAG, "getMyTeamData: " + CompOff_CHANGE_GET_URL);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CompOff_CHANGE_GET_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        HashMap<String, String> mapdata;
                        progressDialog.dismiss();
                        arrayList = new ArrayList<>();
                        Log.d(TAG, "onResponse: " + response);
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                mapdata = new HashMap<>();
                                mapdata.put("TOKEN_NO", response.getJSONObject(i).getString("TOKEN_NO"));
                                mapdata.put("EMPLOYEE_CODE", response.getJSONObject(i).getString("EMPLOYEE_CODE"));
                                mapdata.put("EMPLOYEE_NAME", response.getJSONObject(i).getString("EMPLOYEE_NAME"));
                                mapdata.put("FROM_DATE", response.getJSONObject(i).getString("FROM_DATE"));
                                mapdata.put("TO_DATE", response.getJSONObject(i).getString("TO_DATE"));
                                mapdata.put("SCR_REASON", response.getJSONObject(i).getString("SCR_REASON"));
                                mapdata.put("R_TYPE", response.getJSONObject(i).getString("R_TYPE"));

                                mapdata.put("ERFS_REQUEST_ID", response.getJSONObject(i).getString("ERFS_REQUEST_ID"));
                                mapdata.put("REQUEST_STATUS_ID", response.getJSONObject(i).getString("REQUEST_STATUS_ID"));
                                mapdata.put("ERFS_ACTION_LEVEL_SEQUENCE", response.getJSONObject(i).getString("ERFS_ACTION_LEVEL_SEQUENCE"));
                                mapdata.put("MAX_ACTION_LEVEL_SEQUENCE", response.getJSONObject(i).getString("MAX_ACTION_LEVEL_SEQUENCE"));
                                mapdata.put("SCR_STATUS", response.getJSONObject(i).getString("SCR_STATUS"));
                                mapdata.put("SCR_EMPLOYEE_ID", response.getJSONObject(i).getString("SCR_EMPLOYEE_ID"));
                                mapdata.put("ERFS_REQUEST_FLOW_ID", response.getJSONObject(i).getString("ERFS_REQUEST_FLOW_ID"));
                                arrayList.add(mapdata);
                            }

                            accuralApprovalMMTAdapter = new CompOffAccuralApprovalMMTAdapter(getActivity(), arrayList);
                            recyclerView.setAdapter(accuralApprovalMMTAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            componentLayout.setVisibility(View.VISIBLE);
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            DataNotFond.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            componentLayout.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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
            jsonArrayRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void sendRequest(final String employeeId, Boolean actionStatus, String reason, String xmlData) {
        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String SEND_APPROVAL_REQUEST = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ProcessCompoffRequestMMT/" + employeeId + "/" + actionStatus + "/" + reason + "/" + xmlData;
            Log.d(TAG, "sendRequest: " + SEND_APPROVAL_REQUEST);

            StringRequest jsonArrayRequest = new StringRequest(Request.Method.GET, SEND_APPROVAL_REQUEST, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        progressDialog.dismiss();
                        Log.d(TAG, "onResponse: " + response);

                        int value = Integer.valueOf(response.replaceAll("^\"|\"$", ""));
                        if (value > 0) {
                            Utilities.showDialog(coordinatorLayout, "CompOff Approval Request Processed Successfuly");
                            getcOffRequestDetail(employeeId);
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Error During Processing CompOff Request ");
                        }

                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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
            jsonArrayRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

}
