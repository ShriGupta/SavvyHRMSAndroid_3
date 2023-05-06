package com.savvy.hrmsnewapp.fragment;

import static com.savvy.hrmsnewapp.fragment.ODStatusFragment.MY_PREFS_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;

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
import com.savvy.hrmsnewapp.classes.SavvyHRMS;

import com.savvy.hrmsnewapp.databinding.FragmentWfhLayoutBinding;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WorkFromHomeRequestFragment extends BaseFragment {
    SavvyHRMS app;
    FragmentWfhLayoutBinding binding;
    CalanderHRMS calanderHRMS;
    Handler handler;
    Runnable rRunnable;
    String FROM_DATE = "", TO_DATE = "";

    String odStatusPosition = "1";
    String odsubstatusPosition = "0";
    private CoordinatorLayout coordinatorLayout;
    SharedPreferences shared;
    String token,employeeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        employeeId = (shared.getString("EmpoyeeId", ""));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wfh_layout, container, false);
        View view = binding.getRoot();
        calanderHRMS = new CalanderHRMS(getActivity());
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        init();
        return view;
    }

    private void init() {

        binding.btnFromWfhdate.setOnClickListener(view -> {
            binding.btnFromWfhdate.setText("");
            calanderHRMS.datePicker(binding.btnFromWfhdate);

            handler = new Handler();
            rRunnable = () -> {
                try {
                    FROM_DATE = binding.btnFromWfhdate.getText().toString().trim();
                    TO_DATE = binding.btnToWfhdate.getText().toString().trim();

                    if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                        getCompareTodayDate(FROM_DATE, TO_DATE);
                    } else {
                        handler.postDelayed(rRunnable, 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("GetFuture Method ", e.getMessage());
                }
            };
            handler.postDelayed(rRunnable, 1000);
        });
        binding.btnToWfhdate.setOnClickListener(view -> {
            binding.btnToWfhdate.setText("");
            calanderHRMS.datePicker(binding.btnToWfhdate);

            handler = new Handler();
            rRunnable = () -> {
                try {
                    FROM_DATE = binding.btnFromWfhdate.getText().toString().trim();
                    TO_DATE = binding.btnToWfhdate.getText().toString().trim();

                    if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                        getCompareTodayDate(FROM_DATE, TO_DATE);
                    } else {
                        handler.postDelayed(rRunnable, 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("GetFuture Method ", e.getMessage());
                }
            };
            handler.postDelayed(rRunnable, 1000);
        });

        binding.radioButtonFullday.setChecked(true);

        binding.radioButtonFullday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                odStatusPosition = "" + "1";
                odsubstatusPosition = "" + "0";

                binding.radioButtonHalfday.setChecked(false);
                binding.radioButtonFirsthalf.setEnabled(false);
                binding.radioButtonSecondHalf.setEnabled(false);
                binding.radioButtonFirsthalf.setChecked(false);
                binding.radioButtonSecondHalf.setChecked(false);
            }
        });

        binding.radioButtonHalfday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                odStatusPosition = "" + "2";
                odsubstatusPosition = "" + "1";

                binding.radioButtonFullday.setChecked(false);

                binding.radioButtonFirsthalf.setEnabled(true);
                binding.radioButtonFirsthalf.setChecked(true);

                binding.radioButtonSecondHalf.setEnabled(true);
                binding.radioButtonSecondHalf.setChecked(false);
            }
        });

        binding.radioButtonFirsthalf.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                odsubstatusPosition = "" + "1";
                binding.radioButtonFullday.setChecked(false);
                binding.radioButtonSecondHalf.setChecked(false);


            }
        });

        binding.radioButtonSecondHalf.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                odsubstatusPosition = "" + "2";
                binding.radioButtonFullday.setChecked(false);
                binding.radioButtonFirsthalf.setChecked(false);

            }
        });

        binding.btnWhfTimeFrom.setOnClickListener(view -> {
            calanderHRMS.timePicker(binding.btnWhfTimeFrom);
        });

        binding.btnWhfToTime.setOnClickListener(view -> {
            calanderHRMS.timePicker(binding.btnWhfToTime);
        });

        binding.btnWhfRequestSubmit.setOnClickListener(view -> {
            sendWFHRequest();
        });

    }

    private void sendWFHRequest() {

        String fromDate = "", toDate = "", fromTime = "", toTime = "", edtreason = "", odtype = "", od_status = "", odsubstatus = "";

        fromDate = binding.btnFromWfhdate.getText().toString().trim().replace("/", "-");
        toDate = binding.btnToWfhdate.getText().toString().trim().replace("/", "-");
        fromTime = binding.btnWhfTimeFrom.getText().toString().trim().replace(":", "-").replace(" ", "_");
        toTime = binding.btnWhfToTime.getText().toString().trim().replace(":", "-").replace(" ", "_");
        edtreason = binding.edtWhfReason.getText().toString().trim();

        od_status = odStatusPosition;
        odsubstatus = odsubstatusPosition;

        System.out.print("OD Status " + od_status + "   OD Sub Status " + odsubstatus);

        if (fromDate.equals("")) {
            Utilities.showDialog(coordinatorLayout, "Please Enter From Date.");
        } else if (toDate.equals("")) {
            Utilities.showDialog(coordinatorLayout, "Please Enter To Date.");
        } else if (edtreason.equals("")) {
            Utilities.showDialog(coordinatorLayout, "Please Enter Reason.");

        } else {
            if (Utilities.isNetworkAvailable(getActivity())) {
                Send_OD_Request(employeeId, "0", fromDate, toDate, od_status, odsubstatus, fromTime, toTime, odtype, edtreason);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        }

    }

    public void getCompareTodayDate(String From_date, String To_date) {
        try {
            if (Utilities.isNetworkAvailable(requireActivity())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                JSONObject params_final = new JSONObject();
                params_final.put("From_Date", From_date);
                params_final.put("To_Date", To_date);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        response -> {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                boolean resultDate = response.getBoolean("Compare_DateResult");
                                if (!resultDate) {

                                    binding.txtCompareDate.setText("From Date should be less than or equal To Date!");
                                    binding.linearCompareDate.setVisibility(View.VISIBLE);
                                } else {
                                    binding.linearCompareDate.setVisibility(View.GONE);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.e("Error In", "" + ex.getMessage());
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("Error", "" + error.getMessage());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("securityToken", app.user.EMPLOYEE_ID_FINAL);
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
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Send_OD_Request(String emp_id, String req_id, String fromDate, String toDate, String od_type,
                                String od_sub_type, String fromTime, String toTime, String odType, String reason) {
        try {
            if (toTime.equals("")) {
                toTime = "-";
            }
            if (fromTime.equals("")) {
                fromTime = "-";
            }
            JSONObject param = new JSONObject();

            param.put("EMPLOYEE_ID", emp_id);
            param.put("REQUEST_ID", req_id);
            param.put("FROM_DATE", fromDate);
            param.put("TO_DATE", toDate);
            param.put("OD_REQUEST_TYPE", od_type);
            param.put("OD_REQUEST_SUB_TYPE", od_sub_type);
            param.put("FROM_TIME", fromTime);
            param.put("TO_TIME", toTime);
            param.put("OD_TYPE", odType);
            param.put("REASON", reason);
            Log.e("RequestJson", param.toString());

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendOdRequestPost";
            Log.e("WorkFromHomeRequestFragment", url);

            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                    response -> {
                        try {
                            String result = response.getString("SendODRequestPostResult");
                            Log.e("WorkFromHomeRequestFragment", "Send_OD_Request: "+result );
                            int res = Integer.parseInt(result);
                            if (res > 0) {
                                binding.btnFromWfhdate.setText("");
                                binding.btnToWfhdate.setText("");
                                binding.btnWhfTimeFrom.setText("");
                                binding.btnWhfToTime.setText("");
                                binding.edtWhfReason.setText("");
                                Utilities.showDialog(coordinatorLayout, "On Duty request Send successfully.");
                            } else if (res == -1) {
                                Utilities.showDialog(coordinatorLayout, "On Duty request on the same date and same type already exists.");
                            } else if (res == -2) {
                                Utilities.showDialog(coordinatorLayout, "On Duty request for previous payroll cycle not allowed.");
                            } else if (res == -3) {
                                Utilities.showDialog(coordinatorLayout, "OD/AR/Leave on Same Date Already Requested.");
                            } else if (res == 0) {
                                Utilities.showDialog(coordinatorLayout, "Error during sending On Duty Request.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Utilities.showDialog(coordinatorLayout, ex.getMessage());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, Utilities.handleVolleyError(error));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



