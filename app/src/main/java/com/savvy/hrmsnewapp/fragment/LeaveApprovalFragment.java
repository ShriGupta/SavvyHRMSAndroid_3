package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

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
import com.savvy.hrmsnewapp.adapter.CustomSpinnerLeaveAdapter;
import com.savvy.hrmsnewapp.adapter.LeaveApprovalListAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LeaveApprovalFragment extends BaseFragment {

    LeaveApprovalFragment.LeaveSpinnerStatusAsync leaveSpinnerStatusAsync;

    CustomSpinnerLeaveAdapter customspinnerAdapter;
    LeaveApprovalFragment.LeaveApprovalAsync leaveApprovalAsync;
    LeaveApprovalFragment.LeaveApprovActionAsync leaveApprovActionAsync;
    LeaveApprovalListAdapter mAdapter;
    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    Spinner spin_reason;
    String empoyeeId = "";
    String xmlData = "";
    String token_no = "";
    String tg_text = "true", comment;
    String keyid = "0";
    ArrayList<Integer> arrayList;
    String positionId = "0", positionValue = "";
    CustomTextView txtDataNotFound;

    CustomTextView leave_Token_no;
    String edit_comment_text = "-";
    String REQUEST_ID = "", REQUEST_FLOW_STATUS_ID = "", ACTION_LEVEL_SEQUENCE = "",
            MAX_ACTION_LEVEL_SEQUENCE = "", R_STATUS = "", EMPLOYEE_ID = "", ERFS_REQUEST_FLOW_ID = "",
            LR_LEAVE_CONFIG_ID = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        getLeaveApproval(empoyeeId);
    }

    private void getLeaveApproval(String empid) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arrayList = new ArrayList<>();
                arlData = new ArrayList<HashMap<String, String>>();
                leaveApprovalAsync = new LeaveApprovalFragment.LeaveApprovalAsync();
                leaveApprovalAsync.empid = empid;
                leaveApprovalAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leave_approval, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        recyclerView = getActivity().findViewById(R.id.leaveApprovRecycler);
        mAdapter = new LeaveApprovalListAdapter(getActivity(), coordinatorLayout, arlData, arrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.addOnItemTouchListener(new LeaveApprovalFragment.RecyclerTouchListener(getContext(), recyclerView, new LeaveApprovalFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    CustomTextView txt_actionValue = view.findViewById(R.id.leave_action);
                    leave_Token_no = view.findViewById(R.id.leave_token_value);

                    // Toast.makeText(getActivity(),"Press Again",Toast.LENGTH_LONG).show();
                    txt_actionValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            token_no = leave_Token_no.getText().toString();

                            String str = mapdata.get("TOKEN_NO");

                            Log.e("XML DATA", token_no + "   " + str);

                            if (token_no.equals(str)) {

                                REQUEST_ID = mapdata.get("ERFS_REQUEST_ID");
                                LR_LEAVE_CONFIG_ID = mapdata.get("LR_LEAVE_CONFIG_ID");
                                REQUEST_FLOW_STATUS_ID = mapdata.get("REQUEST_STATUS_ID");
                                ACTION_LEVEL_SEQUENCE = mapdata.get("ERFS_ACTION_LEVEL_SEQUENCE");
                                MAX_ACTION_LEVEL_SEQUENCE = mapdata.get("MAX_ACTION_LEVEL_SEQUENCE");
                                R_STATUS = mapdata.get("LR_STATUS");
                                EMPLOYEE_ID = mapdata.get("LR_EMPLOYEE_ID");
                                ERFS_REQUEST_FLOW_ID = mapdata.get("ERFS_REQUEST_FLOW_ID");

                                xmlData = REQUEST_ID + "," + LR_LEAVE_CONFIG_ID + "," + REQUEST_FLOW_STATUS_ID + "," + ACTION_LEVEL_SEQUENCE + "," +
                                        MAX_ACTION_LEVEL_SEQUENCE + "," + R_STATUS + "," + EMPLOYEE_ID + "," + ERFS_REQUEST_FLOW_ID;

                                Log.e("XML DATA", xmlData);
                                Log.e("Token No.", str);
                            }


                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.dialogbox_leave_approval);
                            dialog.setTitle("Approval");
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                            Button btn_ApproveGo, btn_close;
                            Spinner spinnerReason;
                            btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                            btn_close = dialog.findViewById(R.id.btn_close);

                            CustomTextView txt_ApprovalToggleTitle, edt_comment_dialog, txt_reason_dialog;
                            txt_ApprovalToggleTitle = dialog.findViewById(R.id.txt_ApprovalToggleTitle);
                            edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
                            txt_reason_dialog = dialog.findViewById(R.id.txt_reason_dialog);

                            String str1 = "<font color='#EE0000'>*</font>";
                            txt_ApprovalToggleTitle.setText(Html.fromHtml("Type " + str1));
                            edt_comment_dialog.setText(Html.fromHtml("Comment " + str1));
                            txt_reason_dialog.setText(Html.fromHtml("Reason " + str1));

                            spinnerReason = dialog.findViewById(R.id.spin_reason_comment);

                            final LinearLayout linearLayoutSpinner = dialog.findViewById(R.id.linear_spinner);

                            linearLayoutSpinner.setVisibility(View.GONE);
                            getSpinReasonData(spinnerReason);

                            final EditText edt_comment = dialog.findViewById(R.id.edt_approve_comment);
                            final ToggleButton tgText = dialog.findViewById(R.id.tg_approve);

                            tgText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (tgText.isChecked()) {
                                        linearLayoutSpinner.setVisibility(View.GONE);
                                        tg_text = "" + "true";
                                        positionId = "0";
                                    } else {
                                        linearLayoutSpinner.setVisibility(View.VISIBLE);
                                        tg_text = "" + "false";
                                        positionId = "0";
                                    }
                                }
                            });
                            txt_header.setText("Leave Approve/Reject");
                            spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        if (position == 0) {
//                                            positionValue = "";
                                            positionId = "0";
                                        } else if (position > 0) {
                                            positionId = arlRequestStatusData.get(position - 1).get("LeaveCancelRejectReasonId");
//                                            positionValue = arlRequestStatusData.get(position - 1).get("VALUE");
                                        }
                                        Log.e("Spin Value", "Spin Id " + positionId + " Value " + positionValue);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    comment = edt_comment.getText().toString().trim();
                                    if (comment.equals("")) {
                                        Utilities.showDialog(coordinatorLayout, "Please Enter Comment");
                                    } else {
                                        try {
                                            if (tg_text.equals("false")) {
                                                if (positionId.equals("0")) {
                                                    Utilities.showDialog(coordinatorLayout, "Please Select Reason");
                                                } else {
                                                    sendLeaveProcessApprovalPost(empoyeeId, tg_text, comment, xmlData, positionId);
                                                    dialog.dismiss();
                                                }
                                            } else {
//                                                getApproveList(empoyeeId, comment, tg_text, xmlData, positionId);
                                                sendLeaveProcessApprovalPost(empoyeeId, tg_text, comment, xmlData, positionId);
                                                dialog.dismiss();
                                            }
//                                                positionId="0";


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
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
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void getSpinReasonData(Spinner spin_reason) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequestStatusData = new ArrayList<HashMap<String, String>>();
                leaveSpinnerStatusAsync = new LeaveApprovalFragment.LeaveSpinnerStatusAsync();
                leaveSpinnerStatusAsync.spin_reason = spin_reason;
                // requestStatusasynctask.empid=empid;
                leaveSpinnerStatusAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getApproveList(String empoyeeId, String edit_comment_text, String tg_text, String xmlData, String keyId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                leaveApprovActionAsync = new LeaveApprovalFragment.LeaveApprovActionAsync();
                leaveApprovActionAsync.edit_comment_text = edit_comment_text;
                leaveApprovActionAsync.tg_text_status = tg_text;
                leaveApprovActionAsync.xmlData = xmlData;
                leaveApprovActionAsync.employeeId = empoyeeId;
                leaveApprovActionAsync.keyId = keyId;

                leaveApprovActionAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class LeaveApprovActionAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String edit_comment_text, tg_text_status, xmlData, employeeId, keyId;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()

            try {

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ApprovalProcessLeaveRequest" + "/" + employeeId + "/" + tg_text_status + "/" + edit_comment_text + "/" + xmlData + "/" + keyId;

                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        GETREQUESTSTATUS_URL, "GET");

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
            // execution of result of Long time consuming operation
            //finalResult.setText(result);

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();

                try {
                    result = result.replaceAll("^\"+|\"+$", " ").trim();

                    int res = Integer.parseInt(result);
                    if (res == 1) {
                        Utilities.showDialog(coordinatorLayout, "Leave Request processed sucessfully.");
                        getLeaveApproval(empoyeeId);
                    } else if (res > 1) {
                        Utilities.showDialog(coordinatorLayout, "Error during processing of on Leave Request. Error Code is: " + result);
                    } else {
                        Utilities.showDialog(coordinatorLayout, "Error during processing of Leave Request.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.v("Exception", "Integer Value Not accepted");
                }
            }
        }


        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    public void sendLeaveProcessApprovalPost(String empId, String status, String comment, String xmlData,
                                             String rejectionId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", empId);
                param.put("STATUS", status);
                param.put("COMMENT", comment);
                param.put("XMLDATA", xmlData);
                param.put("REJECTION_REASON_ID", rejectionId);
                Log.e("RequestData", "Json->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ProcessLeaveRequestPost";
                Log.e("Url", url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int res = response.getInt("ProcessLeaveRequestPostResult");
                                    if (res == 1) {
                                        tg_text = "true";
                                        Utilities.showDialog(coordinatorLayout, "Leave Request processed sucessfully.");
                                        getLeaveApproval(empoyeeId);
                                    } else if (res > 1) {
                                        Utilities.showDialog(coordinatorLayout, "Error during processing of on Leave Request. Error Code is: " + res);
                                    } else {
                                        Utilities.showDialog(coordinatorLayout, "Error during processing of Leave Request.");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
                RetryPolicy policy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LeaveSpinnerStatusAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        Spinner spin_reason;
        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveCancelRejectReason/1";

                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        GETREQUESTSTATUS_URL, "GET");

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
            // execution of result of Long time consuming operation
            //finalResult.setText(result);

            System.out.println("jsonArray===" + result);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try {
                    HashMap<String, String> requestStatusmap;
                    // ArrayList<String> requestArray;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    //requestArray=new ArrayList<String>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            requestStatusmap = new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            String key = explrObject.getString("LeaveCancelRejectReasonId");
                            String value = explrObject.getString("Reason");
//                             requestArray.add(value);
                            requestStatusmap.put("LeaveCancelRejectReasonId", key);
                            requestStatusmap.put("Reason", value);

                            arlRequestStatusData.add(requestStatusmap);
                        }
                        System.out.println("Array===" + arlRequestStatusData);

                        customspinnerAdapter = new CustomSpinnerLeaveAdapter(getActivity(), arlRequestStatusData);
                        spin_reason.setAdapter(customspinnerAdapter);

                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        System.out.println("Data not getting on server side");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private LeaveApprovalFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LeaveApprovalFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {

//                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null && clickListener != null) {
//                        clickListener.onClick(child, recyclerView.getChildPosition(child));
//                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    private class LeaveApprovalAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        // private double lati;
        String token;

        // String stremail,strpassword;
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()

            try {
                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ApprovalGetLeaveRequestDetail/" + empid;

                System.out.println("ATTENDANCE_URL====" + ATTENDANCE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(ATTENDANCE_URL, "GET");

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
            // execution of result of Long time consuming operation
            //finalResult.setText(result);

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try {
                    HashMap<String, String> punchStatusmap;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            punchStatusmap = new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);

                            String tokenNo = explrObject.getString("TOKEN_NO");                           //1
                            String fromDate = explrObject.getString("FROM_DATE");                           //1
                            String toDate = explrObject.getString("TO_DATE");                           //1
                            String employee_Code = explrObject.getString("EMPLOYEE_CODE");                         //6
                            String employee_Name = explrObject.getString("EMPLOYEE_NAME");                     //7
                            String leaveName = explrObject.getString("LM_LEAVE_NAME");                         //2
                            String leaveDays = explrObject.getString("TOTAL_DAYS_REQUESTED");
                            String typeFull = explrObject.getString("TYPE");                         //4
                            String reason = explrObject.getString("LR_REASON");                         //5
                            String typeFresh = explrObject.getString("R_TYPE");                         //5

                            String REQUEST_ID = explrObject.getString("ERFS_REQUEST_ID");
                            String LR_LEAVE_CONFIG_ID = explrObject.getString("LR_LEAVE_CONFIG_ID");
                            String REQUEST_FLOW_STATUS_ID = explrObject.getString("REQUEST_STATUS_ID");
                            String ACTION_LEVEL_SEQUENCE = explrObject.getString("ERFS_ACTION_LEVEL_SEQUENCE");
                            String MAX_ACTION_LEVEL_SEQUENCE = explrObject.getString("MAX_ACTION_LEVEL_SEQUENCE");
                            String R_STATUS = explrObject.getString("LR_STATUS");
                            String EMPLOYEE_ID = explrObject.getString("LR_EMPLOYEE_ID");
                            String ERFS_REQUEST_FLOW_ID = explrObject.getString("ERFS_REQUEST_FLOW_ID");

                            int token_no = Integer.parseInt(explrObject.getString("TOKEN_NO"));
                            arrayList.add(token_no);


                            punchStatusmap.put("TOKEN_NO", tokenNo);
                            punchStatusmap.put("FROM_DATE", fromDate);
                            punchStatusmap.put("TO_DATE", toDate);
                            punchStatusmap.put("EMPLOYEE_CODE", employee_Code);
                            punchStatusmap.put("EMPLOYEE_NAME", employee_Name);
                            punchStatusmap.put("LM_LEAVE_NAME", leaveName);
                            punchStatusmap.put("TOTAL_DAYS_REQUESTED", leaveDays);
                            punchStatusmap.put("TYPE", typeFull);
                            punchStatusmap.put("LR_REASON", reason);
                            punchStatusmap.put("R_TYPE", typeFresh);

                            punchStatusmap.put("ERFS_REQUEST_ID", REQUEST_ID);
                            punchStatusmap.put("LR_LEAVE_CONFIG_ID", LR_LEAVE_CONFIG_ID);
                            punchStatusmap.put("REQUEST_STATUS_ID", REQUEST_FLOW_STATUS_ID);
                            punchStatusmap.put("ERFS_ACTION_LEVEL_SEQUENCE", ACTION_LEVEL_SEQUENCE);
                            punchStatusmap.put("MAX_ACTION_LEVEL_SEQUENCE", MAX_ACTION_LEVEL_SEQUENCE);
                            punchStatusmap.put("LR_STATUS", R_STATUS);
                            punchStatusmap.put("LR_EMPLOYEE_ID", EMPLOYEE_ID);
                            punchStatusmap.put("ERFS_REQUEST_FLOW_ID", ERFS_REQUEST_FLOW_ID);

                            arlData.add(punchStatusmap);


                        }
                        System.out.println("Array===" + arlData);

                        Collections.sort(arrayList, Collections.<Integer>reverseOrder());
                        System.out.println("ArrayList===" + arrayList);

                        //DisplayHolidayList(arlData);
                        recyclerView.setVisibility(View.VISIBLE);
                        txtDataNotFound.setVisibility(View.GONE);
                        mAdapter = new LeaveApprovalListAdapter(getActivity(), coordinatorLayout, arlData, arrayList);
                        recyclerView.setAdapter(mAdapter);
                    } else {
                        recyclerView.setAdapter(null);
                        recyclerView.setVisibility(View.GONE);
                        txtDataNotFound.setVisibility(View.VISIBLE);
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

}
