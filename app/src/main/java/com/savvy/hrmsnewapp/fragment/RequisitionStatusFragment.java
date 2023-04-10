package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.RequisitionStatusAdapter;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;
import com.savvy.hrmsnewapp.model.RequisitionStatusModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class RequisitionStatusFragment extends Fragment implements ItemClickListener {
    RecyclerView rvReqStatusList;
    RequisitionStatusAdapter adapter;
    ArrayList<RequisitionStatusModel> requisitionStatusList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token, employeeId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        token = (sharedpreferences.getString("Token", ""));
        employeeId = (sharedpreferences.getString("EmpoyeeId", ""));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.requisition_status, container, false);

        rvReqStatusList = view.findViewById(R.id.rv_requisition_status_list);
        rvReqStatusList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RequisitionStatusAdapter(this);
        rvReqStatusList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getPositionRequisitionRequestData();
        // adapter.addItems();

    }

    private void getPositionRequisitionRequestData() {
        requisitionStatusList.clear();
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetPositionRequisitionRequestEmployee";
            final JSONObject params_final = new JSONObject();
            params_final.put("employeeId", employeeId);
            //  params_final.put("securityToken", token);
            Log.e(TAG, "requisitionStatus: " + url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e(TAG, "onResponse: " + response.toString());
                            try {
                                JSONArray jarray = new JSONArray(response.getString("GetPositionRequisitionRequestEmployeeResult"));
                                RequisitionStatusModel requisitionStatusModel;
                                if (jarray.length() > 0) {
                                    for (int i = 0; i < jarray.length(); i++) {
                                        requisitionStatusModel = new RequisitionStatusModel();
                                        requisitionStatusModel.setRrwRequisitionId(jarray.getJSONObject(i).getString("RRW_REQUISITION_ID"));
                                        requisitionStatusModel.setDepartmentName(jarray.getJSONObject(i).getString("DEPARTMENT_NAME"));
                                        requisitionStatusModel.setRrwRequisitionDate1(jarray.getJSONObject(i).getString("RRW_REQUISITION_DATE_1"));
                                        requisitionStatusModel.setShiftNameWithStatus(jarray.getJSONObject(i).getString("SHIFT_NAME_WITH_STATUS"));
                                        requisitionStatusModel.setRrwStatus(jarray.getJSONObject(i).getString("RRW_STATUS"));
                                        requisitionStatusModel.setRrwApprovalStatus1(jarray.getJSONObject(i).getString("RRW_APPROVAL_STATUS_1"));
                                        requisitionStatusList.add(requisitionStatusModel);
                                    }
                                    adapter.addItems(requisitionStatusList);

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();

                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: " + error);
                    //showToast(error.getMessage());
                    Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", sharedpreferences.getString("EMPLOYEE_ID_FINAL", ""));
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            int socketTimeout = 300000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //DeleteRequisitionEmployee
    private void deleteRequisitionEmployeeData(String requisition_id) {
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/DeleteRequisitionEmployee";
            final JSONObject params_final = new JSONObject();
            params_final.put("requisition_id", requisition_id);
            params_final.put("employeeId", employeeId);
            Log.e(TAG, "DeleteRequisitionStatus: " + url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e(TAG, "onResponse: " + response.toString());
                            try {
                                String result = response.getString("DeleteRequisitionEmployeeResult");
                                if (Integer.parseInt(result) > 0) {
                                    getPositionRequisitionRequestData();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();

                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: " + error);
                    //showToast(error.getMessage());
                    Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", sharedpreferences.getString("EMPLOYEE_ID_FINAL", ""));
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            int socketTimeout = 300000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClickItem(int position, String data) {
        deleteRequisitionEmployeeData(requisitionStatusList.get(position).getRrwRequisitionId());
    }
}
