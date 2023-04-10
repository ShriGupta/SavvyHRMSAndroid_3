package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.DashBoardActivity;
import com.savvy.hrmsnewapp.activity.TravelExpenseActivity;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TravelExpenseRequestFicciFragment extends BaseFragment implements View.OnClickListener {

    TravelExpenseRequestAdapter travelExpenseRequestAdapter;
    CoordinatorLayout coordinatorLayout;
    LinearLayout dataNotFound;
    EditText edt_remarks;
    Button sendRequestButton, resetButton;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> arrayList;
    LinearLayout linearLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        arrayList = new ArrayList<>();
        getTravelForVoucherData(employeeId);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travel_expense_request_ficci, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        dataNotFound = getActivity().findViewById(R.id.travel_expense_txt_DataNotFound);
        edt_remarks = getActivity().findViewById(R.id.edt_Travel_expense_remarks);
        sendRequestButton = getActivity().findViewById(R.id.tavel_expense_sendButton);
        resetButton = getActivity().findViewById(R.id.travel_expense_resetButton);
        linearLayout = getActivity().findViewById(R.id.travelExpenseLinearLayout);

        recyclerView = getActivity().findViewById(R.id.travel_expense_RecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        sendRequestButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tavel_expense_sendButton:

                if (travelExpenseRequestAdapter.getTripCount() == 0) {
                    Utilities.showDialog(coordinatorLayout, "Please select any travel request for voucher request.");
                } else if (travelExpenseRequestAdapter.getTripCount() > 1) {
                    Utilities.showDialog(coordinatorLayout, "Only one travel request can be select for voucher request.");
                } else if (edt_remarks.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Remarks");
                } else {
                    String travelRequestId = travelExpenseRequestAdapter.getTripDetail();
                    sendRequest(employeeId, travelRequestId, edt_remarks.getText().toString().trim().replaceAll("\\s", "'"));
                }
                break;
            case R.id.travel_expense_resetButton:
                edt_remarks.setText("");
                break;

        }
    }

    private void sendRequest(String employeeId, String travelRequestId, String remarks) {

        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Sending travel request...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final String SEND_FINAL_REQUEST = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendTravelVoucherRequestFicci";

            JSONObject params_final = new JSONObject();
            JSONObject pm = new JSONObject();
            try {
                params_final.put("EMPLOYEE_ID", employeeId);
                params_final.put("TRAVEL_REQUEST_ID", travelRequestId);
                params_final.put("REMARKS", remarks);
                pm.put("objSendTravelVoucherRequestInfo", params_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SEND_FINAL_REQUEST, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        int value = Integer.valueOf(response.getString("SendTravelVoucherRequestFicciResult"));

                        if (value == 1) {
                            Utilities.showDialog(coordinatorLayout, "Travel Voucher request send sucessfully.");
                            reloadActivity();
                        } else if (value == -1) {
                            Utilities.showDialog(coordinatorLayout, "Please add expense for the travel to request for voucher.");
                        } else if (value == 0) {
                            Utilities.showDialog(coordinatorLayout, "Some error occur on processing the request.");
                        } else if (value == -2) {
                            Utilities.showDialog(coordinatorLayout, "Please add Travel Trip Report before you proceed for Expense Request.");
                        }


                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void reloadActivity() {
        Intent intent = new Intent(getActivity(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putString("PostionId", "65");
        intent.putExtras(bundle);
        startActivity(intent);

    }

    private void getTravelForVoucherData(String employeeId) {
        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final String GET_TRAVEL_VOUCHER = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelForVoucherFicci";
            JSONObject params_final = new JSONObject();
            try {
                params_final.put("EMPLOYEE_ID", employeeId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, GET_TRAVEL_VOUCHER, params_final, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {

                        HashMap<String, String> mapdata;
                        JSONArray jsonArray = response.getJSONArray("GetTravelForVoucherFicciResult");

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mapdata = new HashMap<>();
                                mapdata.put("TOKEN_NO", jsonArray.getJSONObject(i).getString("TOKEN_NO"));
                                mapdata.put("TRF_TRAVEL_TYPE", jsonArray.getJSONObject(i).getString("TRF_TRAVEL_TYPE"));
                                mapdata.put("TRF_START_DATE", jsonArray.getJSONObject(i).getString("TRF_START_DATE"));
                                mapdata.put("TRF_END_DATE", jsonArray.getJSONObject(i).getString("TRF_END_DATE"));
                                mapdata.put("TRF_BUDGETED_AMOUNT", jsonArray.getJSONObject(i).getString("TRF_BUDGETED_AMOUNT"));
                                mapdata.put("TPM_PROJECT_NAME", jsonArray.getJSONObject(i).getString("TPM_PROJECT_NAME"));
                                mapdata.put("TRF_TRAVEL_REASON", jsonArray.getJSONObject(i).getString("TRF_TRAVEL_REASON"));
                                mapdata.put("TRF_TRAVEL_REQUTRF_START_DATEEST_ID", jsonArray.getJSONObject(i).getString("TRF_TRAVEL_REQUEST_ID"));
                                mapdata.put("TRF_REQUEST_TYPE", jsonArray.getJSONObject(i).getString("TRF_REQUEST_TYPE"));
                                mapdata.put("TRF_TRAVEL_REQUEST_ID", jsonArray.getJSONObject(i).getString("TRF_TRAVEL_REQUEST_ID"));

                                arrayList.add(mapdata);
                            }

                            travelExpenseRequestAdapter = new TravelExpenseRequestAdapter(getActivity(), arrayList, new TravelExpenseRequestAdapter.RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("TRF_TRAVEL_REQUEST_ID", arrayList.get(position).get("TRF_TRAVEL_REQUEST_ID"));
                                    bundle.putString("TRF_REQUEST_TYPE", arrayList.get(position).get("TRF_REQUEST_TYPE"));
                                    bundle.putString("TRF_TRAVEL_TYPE", arrayList.get(position).get("TRF_TRAVEL_TYPE"));
                                    bundle.putString("TRF_START_DATE", arrayList.get(position).get("TRF_START_DATE"));
                                    bundle.putString("TRF_END_DATE", arrayList.get(position).get("TRF_END_DATE"));

                                    Intent intent = new Intent(getActivity(), TravelExpenseActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }
                            });
                            recyclerView.setAdapter(travelExpenseRequestAdapter);
                            dataNotFound.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);

                        } else {

                            dataNotFound.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);
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
            jsonObjectRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }


}
