package com.savvy.hrmsnewapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CustomCitySpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import javax.net.ssl.SSLEngineResult;

import static android.content.ContentValues.TAG;
import static com.savvy.hrmsnewapp.fragment.TravelRequestFicciFragment.MY_PREFS_NAME;

public class ShortLeaveRequestFragment extends BaseFragment {

    Button edt_shortDate, short_Leave_SubmitButton;
    EditText edt_shortLeave_Reason;
    RadioButton lateRadioButton, earlyRadioButton;
    Spinner shortLeave_Spinner;
    private CalanderHRMS calanderHRMS;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences sharedPreferences;
    String employeeId;
    CustomSpinnerAdapter customSpinnerAdapter;
    ArrayList<HashMap<String, String>> typeArrayList;
    String SHORT_LEAVE_CONFIGURATION_ID = "";
    String REQUEST_TYPE = "1";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        employeeId = sharedPreferences.getString("EmpoyeeId", "");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_short_leave_request, container, false);

        edt_shortDate = view.findViewById(R.id.edt_shortDate);
        edt_shortLeave_Reason = view.findViewById(R.id.edt_shortLeave_Reason);
        lateRadioButton = view.findViewById(R.id.lateRadioButton);
        earlyRadioButton = view.findViewById(R.id.earlyRadioButton);
        shortLeave_Spinner = view.findViewById(R.id.shortLeave_Spinner);
        short_Leave_SubmitButton = view.findViewById(R.id.short_Leave_SubmitButton);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

        lateRadioButton.setChecked(true);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        calanderHRMS = new CalanderHRMS(getContext());

        getShortLeaveTypeData(employeeId);

        edt_shortDate.setOnClickListener(view -> calanderHRMS.datePicker(edt_shortDate));
        lateRadioButton.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.e(TAG, "lateRadioButton   onCheckedChanged: " + b);
            if (b) {
                earlyRadioButton.setChecked(false);
                REQUEST_TYPE = "1";
                Log.e(TAG, "onCheckedChanged: " + REQUEST_TYPE);
            }
        });
        earlyRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e(TAG, "earlyRadioButton onCheckedChanged: " + b);
                if (b) {
                    lateRadioButton.setChecked(false);
                    REQUEST_TYPE = "2";
                    Log.e(TAG, "onCheckedChanged: " + REQUEST_TYPE);
                }
            }
        });

        shortLeave_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemSelected: " + i);
                SHORT_LEAVE_CONFIGURATION_ID = "";
                if (i > 0) {
                    SHORT_LEAVE_CONFIGURATION_ID = typeArrayList.get(i - 1).get("KEY_ID");
                    Log.e(TAG, "onItemSelected: " + SHORT_LEAVE_CONFIGURATION_ID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        short_Leave_SubmitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String REQUEST_ID = "0";
                String APPROVED_STATUS = "false";
                String SHORT_DEDUCTION_DATE = edt_shortDate.getText().toString().replaceAll("/", "-");
                String REASON = edt_shortLeave_Reason.getText().toString().trim().replaceAll(" ", "_");

                if (SHORT_DEDUCTION_DATE.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Short Leave Date.");
                } else if (SHORT_LEAVE_CONFIGURATION_ID.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Short Leave Type.");
                } else if (REASON.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please enter reason.");
                } else {
                    sendFinalSLRequest(REQUEST_ID, SHORT_DEDUCTION_DATE, SHORT_LEAVE_CONFIGURATION_ID, REASON, APPROVED_STATUS, REQUEST_TYPE, employeeId);
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getShortLeaveTypeData(String employeeId) {

        typeArrayList = new ArrayList<>();
        if (Utilities.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {


            String LEAVE_TYPE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeShortDeductionLeaveType/" + employeeId;
            Log.e(TAG, "LEAVE_TYPE_URL: " + LEAVE_TYPE_URL);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(LEAVE_TYPE_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    Log.e(TAG, "onResponse: " + response.toString());
                    HashMap<String, String> hashMap;

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            hashMap = new HashMap<>();
                            hashMap.put("KEY_ID", response.getJSONObject(i).getString("SLC_SHORT_LEAVE_CONFIGURATION_ID"));
                            hashMap.put("VALUE", response.getJSONObject(i).getString("SLC_LEAVE_TYPE"));
                            typeArrayList.add(hashMap);
                        }
                        customSpinnerAdapter = new CustomSpinnerAdapter(getActivity(), typeArrayList);
                        shortLeave_Spinner.setAdapter(customSpinnerAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendFinalSLRequest(String REQUEST_ID, String SHORT_DEDUCTION_DATE, String SHORT_LEAVE_CONFIGURATION_ID, String REASON, String APPROVED_STATUS, String REQUEST_TYPE, String employeeId) {

        if (Utilities.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {

            String FINAL_SL_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendShortDeductionRequest/" + REQUEST_ID + "/" + SHORT_DEDUCTION_DATE + "/" + SHORT_LEAVE_CONFIGURATION_ID + "/" + REASON + "/" + APPROVED_STATUS + "/" + REQUEST_TYPE + "/" + employeeId;
            Log.e(TAG, "sendFinalSLRequest: " + FINAL_SL_URL);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, FINAL_SL_URL, new Response.Listener<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(String response) {

                    Log.e(TAG, "onResponse: " + response);
                    String value = response.replaceAll("^\"|\"$", "");

                    switch (value) {
                        case "-1":
                            Utilities.showDialog(coordinatorLayout, "Short Leave request onthe same date and same type already exist!");
                            break;
                        case "-2":
                            Utilities.showDialog(coordinatorLayout, "Short Leave Request for previous payroll cycle not allow.");
                            break;
                        case "-3":
                            Utilities.showDialog(coordinatorLayout, "Short Leave for same date already requested.");
                            break;

                        case "-4":
                            Utilities.showDialog(coordinatorLayout, "Attendance period is locked, can not apply request for the period");

                            break;
                        case "0":
                            Utilities.showDialog(coordinatorLayout, "Error during sending Short Leave request.");
                            break;
                        default:
                            Utilities.showDialog(coordinatorLayout, "Short Leave Request Sent Successfully.");
                            edt_shortLeave_Reason.setText("");
                            shortLeave_Spinner.setSelection(0);
                            edt_shortDate.setText("");
                            lateRadioButton.setChecked(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, Utilities.handleVolleyError(error));
                }
            });

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, "Network is not Available!");
        }
    }
}
