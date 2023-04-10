package com.savvy.hrmsnewapp.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

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
import com.savvy.hrmsnewapp.databinding.FragmentINOUTStatusNewBinding;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class INOUTStatusNewFragment extends Fragment {

    View view;
    SharedPreferences shared;
    public final String MY_PREFS_NAME = "MyPrefsFile";
    FragmentINOUTStatusNewBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_i_n_o_u_t_status_new, container, false);
        view = binding.getRoot();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        binding.btnInOutdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*\  btn_InOutdate.setText("");
                calanderHRMS.datePickerWithOtherFormate(btn_InOutdate);*/
                datePicker();

            }
        });
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice1, new IntentFilter("refresh"));
        /*try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            getAllDetails(currentDateandTime);
        } catch (Exception e) {}*/

        return view;
    }
    public void datePicker() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Date d = new Date(year - 1900, monthOfYear, dayOfMonth);
                String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.UK).format(d);
                binding.btnInOutdate.setText(date1);
                getAllDetails(date1);
            }
        }, yy, mm, dd);
        datePicker.getDatePicker().setCalendarViewShown(false);
        datePicker.getDatePicker().setMaxDate(new Date().getTime());
        datePicker.show();
    }
    private final BroadcastReceiver onNotice1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                getAllDetails(intent.getStringExtra("date"));
            } catch (Exception e) {
            }
        }
    };
    public void getAllDetails(String date) {
        try {

            if (Utilities.isNetworkAvailable(getActivity())) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Fetching...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelPlanCheckInOut";
                Log.e("Save all data", "<><>" + url);
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));
                params_final.put("type", "0");
                params_final.put("date", date);


                Log.e("Save all data", "<><>" + params_final.toString());

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                Log.e("Save all data", "<><>" + response.toString());

                                try {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    //    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("GetTravelPlanCheckInOutResult");
                                    if (jsonArray.length() > 0) {
                                        binding.cardView.setVisibility(View.VISIBLE);
                                        binding.dataNotFoundTv.setVisibility(View.GONE);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            binding.supplierNameTv.setText(explrObject.getString("SM_SUPPLIER_NAME"));
                                            binding.activityNameTv.setText(explrObject.getString("AM_ACTIVITY_NAME"));
                                            binding.locationTv.setText(explrObject.getString("SLD_LOCATION"));
                                            binding.locationTypeTv.setText(explrObject.getString("SLD_LOCATION_TYPE"));
                                            /*if (explrObject.getString("SLD_LOCATION_TYPE").equals("NCR")) {
                                                ncr_radio.setChecked(true);
                                                non_ncr_radio.setChecked(false);
                                                non_ncr_view_layout.setVisibility(View.GONE);
                                                ncr_view_layout.setVisibility(View.VISIBLE);
                                            } else {
                                                non_ncr_radio.setChecked(true);
                                                ncr_radio.setChecked(false);
                                                non_ncr_view_layout.setVisibility(View.VISIBLE);
                                                ncr_view_layout.setVisibility(View.GONE);
                                            }*/
                                            // edt_InOut_conveyance.setText(explrObject.getString("SLD_LOCATION_TYPE"));
                                            binding.meetingTypeTv.setText(explrObject.getString("TPCICO_MEETING_TYPE"));
                                            binding.chargesTypeTv.setText(explrObject.getString("TPCICO_CHARGES"));
                                            binding.tollTv.setText(explrObject.getString("TPCICO_TOLL"));
                                            binding.checkInTimeTv.setText(explrObject.getString("TPCICO_CHECK_IN_TIME"));
                                            binding.workTypeTv.setText(explrObject.getString("TPCICO_WORK_TYPE"));


                                        }
                                    }else {
                                        binding.cardView.setVisibility(View.GONE);
                                        binding.dataNotFoundTv.setVisibility(View.VISIBLE);

                                        Toast.makeText(getActivity(), "Data Not Found!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("Error", "" + error.getMessage());
                        try {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                        }
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

                int socketTimeout = 3000000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else {
                // Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}