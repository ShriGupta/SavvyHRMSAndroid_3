package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
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

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.SavvyHRMSApplication.TAG;

public class LeaveEncashmentRequest extends BaseFragment {

    LeaveEncashmentRequest.SendEncashmentAsync sendEncashmentAsync;
    LeaveEncashmentRequest.GETEncashmentAsync getEncashmentAsync;
    LeaveEncashmentRequest.CheckPloicyAsyncTask checkPloicyAsyncTask;
    LeaveEncashmentRequest.EncashmentBalanceTask encashmentBalanceTask;
    Spinner spin_LE;
    EditText edt_leave_days, edt_leave_remarks;
    Button btn_LE_sendRequest, btn_Reset;
    SharedPreferences shared;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "";
    String spinnerSelectionID = "";
    int spinnerPosition;
    String empoyeeId = "";
    String balance = "";
    CustomTextView txt_Remarks, txt_leaveEncashDays, txt_leaveTypeYear;
    ArrayList<HashMap<String, String>> arraylistData;
    private ProgressDialog pDialog;
    HashMap<String, String> mapData;
    CustomSpinnerAdapter customSpinnerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        checkMyPloicy(empoyeeId);
    }

    private void checkMyPloicy(String empoyeeId) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                checkPloicyAsyncTask = new LeaveEncashmentRequest.CheckPloicyAsyncTask();
                checkPloicyAsyncTask.empoyeeId = empoyeeId;
                checkPloicyAsyncTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CheckPloicyAsyncTask extends AsyncTask<String, String, String> {
        String empoyeeId;

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
        protected String doInBackground(String... strings) {

            try {
                String GETLEAVEENCASHMENT_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyPolicyIdLeaveEncashment/" + empoyeeId;
                System.out.println("ATTENDANCE_URL====" + GETLEAVEENCASHMENT_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(GETLEAVEENCASHMENT_URL, "GET");
                if (json != null) {
                    Log.d("JSON result", json);
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                pDialog.dismiss();
                int value = 0;
                result = result.replaceAll("^\"|\"$", "").trim();
                value = Integer.parseInt(result);

                if (value > 0) {
                    getEncashmentData(empoyeeId);
                } else {
                    Utilities.showDialog(coordinatorLayout, "You are not authorise for Leave Encashment Request");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    private void getEncashmentData(String empoyeeId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                getEncashmentAsync = new LeaveEncashmentRequest.GETEncashmentAsync();
                getEncashmentAsync.empoyeeId = empoyeeId;
                getEncashmentAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GETEncashmentAsync extends AsyncTask<String, String, String> {
        String empoyeeId;

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
        protected String doInBackground(String... strings) {

            try {
                String GETLEAVEENCASHMENT_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeLeaveEncashmentPolicy/" + empoyeeId;
                System.out.println("ATTENDANCE_URL====" + GETLEAVEENCASHMENT_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(GETLEAVEENCASHMENT_URL, "GET");
                if (json != null) {
                    Log.d("JSON result", json);
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                pDialog.dismiss();
                JSONArray jsonArray = new JSONArray(result);
                arraylistData = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mapData = new HashMap<>();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String key = jsonObject.getString("LEC_LEAVE_ENCASHMENT_CONFIG_ID");
                        String value = jsonObject.getString("LM_LEAVE_NAME");

                        mapData.put("KEY", key);
                        mapData.put("VALUE", value);

                        arraylistData.add(mapData);
                    }
                    customSpinnerAdapter = new CustomSpinnerAdapter(getActivity(), arraylistData);
                    spin_LE.setAdapter(customSpinnerAdapter);
                } else {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_encashment_request, container, false);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        spin_LE = view.findViewById(R.id.spin_LE_reason);
        edt_leave_days = view.findViewById(R.id.edt_LE_days);
        edt_leave_remarks = view.findViewById(R.id.edt_LE_remarks);
        btn_LE_sendRequest = view.findViewById(R.id.btn_LE_submit);
        txt_Remarks = view.findViewById(R.id.txt_Remarks);
        txt_leaveEncashDays = view.findViewById(R.id.txt_leaveEncashDays);
        txt_leaveTypeYear = view.findViewById(R.id.txt_leaveTypeYear);
        btn_Reset = view.findViewById(R.id.btn_LE_Reset);

        String str1 = "<font color='#EE0000'>*</font>";

        txt_leaveTypeYear.setText(Html.fromHtml("Leave Type " + str1));
        txt_leaveEncashDays.setText(Html.fromHtml("Leave Encashment " + str1 + "\nDays"));
        txt_Remarks.setText(Html.fromHtml("Remarks " + str1));

        spin_LE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = spin_LE.getSelectedView().findViewById(R.id.txv_statusItem);
                String tvValue = textView.getText().toString();
                spinnerPosition = position;
                if (position > 0) {
                    HashMap<String, String> mapData = arraylistData.get(position - 1);
                    if (tvValue.equals(mapData.get("VALUE"))) {
                        spinnerSelectionID = mapData.get("KEY");
                        getEncashmentBalance(spinnerSelectionID);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_LE_sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (spinnerPosition == 0) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Leave Type");
                        return;
                    } else if (edt_leave_days.getText().toString().trim().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Leave Days");
                        return;
                    } else if (edt_leave_remarks.getText().toString().trim().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Remarks");
                        return;
                    } else {
                        String remark = edt_leave_remarks.getText().toString().replace(" ", "_").replaceAll("\\s", "");
                        String days = edt_leave_days.getText().toString();
                        String requestID = "0";
                        getLeaveEncashmentData(empoyeeId, requestID, remark, spinnerSelectionID, days, balance);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                }
            }
        });

        btn_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spin_LE.setSelection(0);
                edt_leave_days.setText("");
                edt_leave_remarks.setText("");
            }
        });
        return view;
    }

    public void getLeaveEncashmentData(String empoyeeId, String requestID, String remarks, String spinnerID, String days, String balance) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                sendEncashmentAsync = new LeaveEncashmentRequest.SendEncashmentAsync();
                sendEncashmentAsync.empoyeeId = empoyeeId;
                sendEncashmentAsync.requestID = requestID;
                sendEncashmentAsync.remarks = remarks;
                sendEncashmentAsync.spinnerID = spinnerID;
                sendEncashmentAsync.days = days;
                sendEncashmentAsync.balance = balance;
                sendEncashmentAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SendEncashmentAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empoyeeId, requestID, remarks, spinnerID, days, balance;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendLeaveEncashmentRequest" + "/" + empoyeeId +
                        "/" + requestID + "/" + remarks + "/" + spinnerID + "/" + days + "/" + balance + "/" + false;

                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity());
                String json = jParser.makeHttpRequest(GETREQUESTSTATUS_URL, "GET");

                if (json != null) {
                    Log.d("JSON result", json);
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.e("Result", "" + result);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();

                try {
                    result = result.replaceAll("^\"+|\"+$", " ").trim();
                    int value = Integer.valueOf(result);

                    switch (value) {
                        case -1:
                            Utilities.showDialog(coordinatorLayout, "Leave Encashment request is already inprocess.");
                            break;
                        case -2:
                            Utilities.showDialog(coordinatorLayout, "Leave Encashment No of times in a year exceeded from policy.");
                            break;
                        case -3:
                            Utilities.showDialog(coordinatorLayout, "Duration between two encashment request policy failed.");
                            break;
                        case -4:
                            Utilities.showDialog(coordinatorLayout, "Max encashment days exceeded from policy.");
                            break;
                        case -5:
                            Utilities.showDialog(coordinatorLayout, "Minimum balance after encashment policy failed.");
                            break;
                        case 0:
                            Utilities.showDialog(coordinatorLayout, "Error during sending Leave Encashment Request.");
                            break;
                        default:
                            Utilities.showDialog(coordinatorLayout, "Leave Encashment request send successfully.");

                            spin_LE.setSelection(0);
                            edt_leave_days.setText("");
                            edt_leave_remarks.setText("");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                }
            }
        }
    }

    private void getEncashmentBalance(String key) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                encashmentBalanceTask = new LeaveEncashmentRequest.EncashmentBalanceTask();
                encashmentBalanceTask.empoyeeId = empoyeeId;
                encashmentBalanceTask.key = key;
                encashmentBalanceTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class EncashmentBalanceTask extends AsyncTask<String, String, String> {
        String empoyeeId, key;

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
        protected String doInBackground(String... strings) {
            try {

                String GETBALANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveBalanceEncash/" + empoyeeId + "/" + key;

                System.out.println("ATTENDANCE_URL====" + GETBALANCE_URL);
                JSONParser jParser = new JSONParser(getActivity());
                String json = jParser.makeHttpRequest(GETBALANCE_URL, "GET");
                if (json != null) {
                    Log.d("JSON result", json);
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                pDialog.dismiss();
                balance = result.replaceAll("^\"|\"$", "").trim();
                Log.d(TAG, "onPostExecute: " + balance);
            } catch (Exception e) {
                e.printStackTrace();
                spinnerPosition = 0;
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }
}
