package com.savvy.hrmsnewapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.RequisitionPositionDetailAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.model.RequisitionRequestPositionDetModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;


public class RequisitionRequestFragment extends Fragment {
    RecyclerView rvReqPositionDetList;
    RequisitionPositionDetailAdapter adapter;
    Button btnDate;
    CalanderHRMS calanderHRMS;
    public static Handler handler;
    public static Runnable runnable;
    ArrayList<RequisitionRequestPositionDetModel> requisitionPositionList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token, employeeId;
    Spinner departmentSpinner;
    String departmentID;
    Spinner shiftTypeSpinner;
    String shiftTypeID;
    Button btnSave, btnCancel;
    ArrayList<HashMap<String, String>> departmentArrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> deaprtmentShiftArrayList = new ArrayList<>();
    LinearLayout llPositionLayout, llPositionHeading;
    TextView tvNodata;
    String xmlString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.requisition_request, container, false);
        sharedpreferences = requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        token = (sharedpreferences.getString("Token", ""));
        employeeId = (sharedpreferences.getString("EmpoyeeId", ""));

        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        tvNodata = view.findViewById(R.id.tv_no_data);
        llPositionLayout = view.findViewById(R.id.ll_position_detail_layout);
        llPositionHeading = view.findViewById(R.id.ll_position_heading_layout);
        departmentSpinner = view.findViewById(R.id.sp_department_name);
        shiftTypeSpinner = view.findViewById(R.id.sp_shift_type);
        rvReqPositionDetList = view.findViewById(R.id.rv_position_detail);
        rvReqPositionDetList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RequisitionPositionDetailAdapter(requireActivity());
        rvReqPositionDetList.setAdapter(adapter);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calanderHRMS = new CalanderHRMS(getActivity());
        btnDate = getActivity().findViewById(R.id.btn_requisitionCalendar_Date);
        btnDate.setOnClickListener(v -> {
            btnDate.setText("");
            calanderHRMS.datePicker(btnDate);

            handler = new Handler();
            runnable = () -> {
                try {
                    String FromDate = btnDate.getText().toString().trim();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            handler.postDelayed(runnable, 1000);
        });
        getRequisitionDepartmentData();
        getRequisitionShiftTypeData();
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i > 0) {
                    if (departmentArrayList != null && departmentArrayList.size() > 0) {
                        departmentID = String.valueOf(departmentArrayList.get(i - 1).get("ID"));
                        Log.e(TAG, "onItemSelected: " + departmentID);
                        if ((departmentID != null) && !(shiftTypeID.equals(""))) {
                            getPositionFromDepartmentData(departmentID, shiftTypeID);
                        }
                    }
                } else {
                    departmentID = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        shiftTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    if (deaprtmentShiftArrayList != null && deaprtmentShiftArrayList.size() > 0) {
                        shiftTypeID = deaprtmentShiftArrayList.get(i - 1).get("ID");
                        Log.e(TAG, "onItemSelected: " + shiftTypeID);
                        if (!departmentID.equals("")) {
                            getPositionFromDepartmentData(departmentID, shiftTypeID);
                        } else {
                            Toast.makeText(requireActivity(), "Please select department name.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    shiftTypeID = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xmlString = adapter.createXMLString();
                Log.e(TAG, "onClick: " + xmlString);

                if (departmentID.equals("")) {
                    Toast.makeText(requireActivity(), "Please select departmnent.", Toast.LENGTH_SHORT).show();
                } else if (btnDate.getText().toString().trim().equals("SELECT DATE") || btnDate.getText().toString().trim().equals("")) {
                    Toast.makeText(requireActivity(), "Please select Date.", Toast.LENGTH_SHORT).show();
                } else if (shiftTypeID.equals("")) {
                    Toast.makeText(requireActivity(), "Please select shift type.", Toast.LENGTH_SHORT).show();
                } else if (xmlString.equals("")) {
                    Toast.makeText(requireActivity(), "Please enter position count.", Toast.LENGTH_SHORT).show();
                } else {
                    saveRequisitionRequest("0", departmentID, shiftTypeID, xmlString);
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearData();
            }
        });

    }

    private void getRequisitionDepartmentData() {
        departmentArrayList.clear();
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/BindDepartmentList";
            final JSONObject params_final = new JSONObject();
            params_final.put("employeeId", employeeId);
            //  params_final.put("securityToken", token);
            Log.e(TAG, "requisitionDepartmentName: " + url + employeeId);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e(TAG, "onResponse: " + response.toString());
                            try {
                                JSONArray jarray = new JSONArray(response.getString("BindDepartmentListResult"));
                                HashMap<String, String> hashMap;
                                if (jarray.length() > 0) {
                                    for (int i = 0; i < jarray.length(); i++) {
                                        hashMap = new HashMap<>();
                                        hashMap.put("ID", jarray.getJSONObject(i).getString("DepartmentId"));
                                        hashMap.put("VALUE", jarray.getJSONObject(i).getString("DepartmentName"));
                                        departmentArrayList.add(hashMap);
                                    }

                                    CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(requireActivity(), departmentArrayList);
                                    departmentSpinner.setAdapter(customSpinnerAdapter);
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

    private void getRequisitionShiftTypeData() {
        deaprtmentShiftArrayList.clear();
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetShift";
            final JSONObject params_final = new JSONObject();
            params_final.put("employeeId", employeeId);
            //  params_final.put("securityToken", token);
            Log.e(TAG, "requisitionDepartmentName: " + url + employeeId);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e(TAG, "onResponse: " + response.toString());
                            try {
                                JSONArray jarray = new JSONArray(response.getString("GetShiftResult"));
                                HashMap<String, String> hashMap;
                                if (jarray.length() > 0) {
                                    for (int i = 0; i < jarray.length(); i++) {
                                        hashMap = new HashMap<>();
                                        hashMap.put("ID", jarray.getJSONObject(i).getString("ShiftId"));
                                        hashMap.put("VALUE", jarray.getJSONObject(i).getString("ShiftName"));
                                        deaprtmentShiftArrayList.add(hashMap);
                                    }
                                    CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(requireActivity(), deaprtmentShiftArrayList);
                                    shiftTypeSpinner.setAdapter(customSpinnerAdapter);
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

    private void getPositionFromDepartmentData(String department_id, String shift_id) {
        requisitionPositionList.clear();
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetPositionFromDepartment";
            final JSONObject params_final = new JSONObject();
            params_final.put("department_id", department_id);
            params_final.put("shift_id", shift_id);
            params_final.put("requisitionid", "0");
            params_final.put("employeeId", employeeId);
            Log.e(TAG, "requisitionDepartmentName: " + url + params_final);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e(TAG, "onResponse: " + response.toString());
                            try {
                                JSONArray jarray = new JSONArray(response.getString("GetPositionFromDepartmentResult"));
                                RequisitionRequestPositionDetModel requisitionRequestPositionDetModel;
                                if (jarray.length() > 0) {
                                    for (int i = 0; i < jarray.length(); i++) {
                                        requisitionRequestPositionDetModel = new RequisitionRequestPositionDetModel();
                                        requisitionRequestPositionDetModel.setPrmPositionName(jarray.getJSONObject(i).getString("PRM_POSITION_NAME"));
                                        requisitionRequestPositionDetModel.setPrmPositionHeadCountDayShift(jarray.getJSONObject(i).getString("PRM_POSITION_HEAD_COUNT_DAY_SHIFT"));
                                        requisitionRequestPositionDetModel.setRrwdNoOfEmployee(jarray.getJSONObject(i).getString("RRWD_NO_OF_EMPLOYEE"));
                                        requisitionRequestPositionDetModel.setPrmPositionRoleId(jarray.getJSONObject(i).getString("PRM_POSITION_ROLE_ID"));
                                        requisitionPositionList.add(requisitionRequestPositionDetModel);
                                    }
                                    adapter.addItems(requisitionPositionList);
                                    rvReqPositionDetList.setVisibility(View.VISIBLE);
                                    llPositionLayout.setVisibility(View.VISIBLE);
                                    llPositionHeading.setVisibility(View.VISIBLE);
                                    tvNodata.setVisibility(View.GONE);
                                } else {
                                    llPositionLayout.setVisibility(View.GONE);
                                    rvReqPositionDetList.setVisibility(View.GONE);
                                    llPositionHeading.setVisibility(View.GONE);
                                    tvNodata.setVisibility(View.VISIBLE);
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
                public Map<String, String> getHeaders() {
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

    @SuppressLint("ResourceType")
    private void saveRequisitionRequest(String requisitionid, String department_id, String shift_id, String position_details) {

        try {
            //(string requisition_id, string department_id, string shift_id, string requisition_date, string position_details, string employeeId)
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveRequisitionRequestEmployee";
            final JSONObject params_final = new JSONObject();
            params_final.put("requisitionid", requisitionid);
            params_final.put("department_id", department_id);
            params_final.put("shift_id", shift_id);
            params_final.put("requisition_date", btnDate.getText().toString());
            params_final.put("position_details", position_details);
            params_final.put("employeeId", employeeId);
            Log.e(TAG, "requisitionDepartmentName: " + url + params_final);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e(TAG, "onResponse: " + response.toString());
                            try {
                                String result = response.getString("SaveRequisitionRequestEmployeeResult");
                                if (result.equals("0")) {
                                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                                } else if (result.equals("1")) {
                                    Toast.makeText(getActivity(), "Requisition request send successfully.", Toast.LENGTH_SHORT).show();
                                    clearData();
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

    private void clearData() {
        getRequisitionDepartmentData();
        getRequisitionShiftTypeData();
        btnDate.setText("SELECT DATE");
        xmlString = "";
        llPositionLayout.setVisibility(View.GONE);
        rvReqPositionDetList.setVisibility(View.GONE);
        llPositionHeading.setVisibility(View.GONE);
    }
}
