package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerLeaveAdapter;
import com.savvy.hrmsnewapp.adapter.LeaveStatusFicciAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
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

public class LeaveStatusFicciFragment extends BaseFragment {

    LeaveStatusFicciFragment.RequestStatusAsynTask requestStatusasynctask;
    LeaveStatusFicciFragment.CancelRequestAsynTask cancelrequestasynctask;
    LeaveStatusFicciFragment.PullBackRequestAsynTask pullbackrequestasynctask;

    CustomSpinnerLeaveAdapter customspinnercancelAdapter;
    LeaveStatusFicciFragment.GetSpinCancelDataAsynTask getSpinCancelDataAsynTask;
    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData, arlRequstedCancel;

    CalanderHRMS calanderHRMS;
    RecyclerView recyclerView;

    Button btn_fromDate, btn_toDate;
    Button btn_submit, btn_spin_leave;

    ArrayList<Integer> arrayList;

    CustomSpinnerAdapter customspinnerAdapter;

    LeaveStatusFicciAdapter mAdapter;
    String employeeId = "", requestId = "";
    CustomTextView txt_applyCancel, txt_applyValue, txt_leaveToken_no;

    SharedPreferences shared;
    String token_no = "";
    String token = "";
    String empoyeeId = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapdata;
    String url = "";

    LinearLayout linear_result_compareDate;
    CustomTextView txt_result_compareDate;
    Handler handler;
    Runnable rRunnable;

    String positionId = "";

    String FROM_DATE = "", TO_DATE = "";
    CustomTextView txtDataNotFound;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinArray = new ArrayList();
        spinArrayID = new ArrayList();

        try {
            shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
            calanderHRMS = new CalanderHRMS(getActivity());
            arrayList = new ArrayList<>();
            arlData = new ArrayList<>();
            getStatusList();
            token = (shared.getString("Token", ""));
            empoyeeId = (shared.getString("EmpoyeeId", ""));
            getLeaveStatusValue("-", "-", "0,1,6,7");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leave_status_ficci, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        btn_fromDate = getActivity().findViewById(R.id.btn_leaveStatusFicci_fromDate);
        btn_toDate = getActivity().findViewById(R.id.btn_leaveStatusFicci_toDate);
        btn_submit = getActivity().findViewById(R.id.btn_submitFicci);
        btn_spin_leave = getActivity().findViewById(R.id.btn_spin_leave_statusFicci);

        linear_result_compareDate = getActivity().findViewById(R.id.Linear_leaveStatusFiccicompareDate);
        txt_result_compareDate = getActivity().findViewById(R.id.leaveStatusFicci_result_compareDate);

        linear_result_compareDate.setVisibility(View.GONE);

        recyclerView = getActivity().findViewById(R.id.leaveStatusFicciRecyclerView);

        txtDataNotFound = getActivity().findViewById(R.id.tv_FicciDataNotFound);
        recyclerView.setVisibility(View.VISIBLE);


        if (spinArrayID.size() == 0) {
            btn_spin_leave.setText("Pending,Inprocess");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new LeaveStatusFragment.RecyclerTouchListener(getContext(), recyclerView, new LeaveStatusFragment.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);


                    txt_applyValue = view.findViewById(R.id.tv_pullback_Ficci);
                    txt_applyCancel = view.findViewById(R.id.tv_cancel_Ficci);
                    txt_leaveToken_no = view.findViewById(R.id.tv_leaveTokenNumber);

                    txt_applyValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                token_no = txt_leaveToken_no.getText().toString();
                                String str = mapdata.get("TOKEN_NO");
                                Log.e("str", "str " + str + " token " + token_no);
                                if (token_no.equals(str)) {

                                    String requestid = mapdata.get("ERFS_REQUEST_ID");

                                    requestId = requestid;
                                    Log.e("IF Condition", "Employee " + employeeId);
                                }

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.pullback_dialogbox);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback Leave Request.");
                                txt_header.setText("Pull Back Leave Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pullBackRequest(employeeId, requestId);
                                        dialog.dismiss();
                                    }
                                });
                                btn_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    txt_applyCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                token_no = txt_leaveToken_no.getText().toString();
                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {

                                    requestId = mapdata.get("ERFS_REQUEST_ID");
                                }
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.dialogbox_request_cancel);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialogCancel_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialogCancel);
                                CustomTextView txt_reason_dialogCancel = dialog.findViewById(R.id.txt_reason_dialogCancel);

                                LinearLayout linearCommentRow = dialog.findViewById(R.id.linearCommentRow);
                                LinearLayout linear_spinner = dialog.findViewById(R.id.linear_spinner);

                                linear_spinner.setVisibility(View.VISIBLE);
                                linearCommentRow.setVisibility(View.GONE);

                                final EditText edt_approve_comment_Cancel = dialog.findViewById(R.id.edt_approve_comment_Cancel);
                                final Spinner spin_reason_commentCancel = dialog.findViewById(R.id.spin_reason_commentCancel);

                                getSpinCancelData(spin_reason_commentCancel);

                                String str1 = "<font color='#EE0000'>*</font>";
                                edt_comment_dialog.setText(Html.fromHtml("Comment " + str1));
                                txt_reason_dialogCancel.setText(Html.fromHtml("Reason " + str1));

                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);

//                                edt_comment_dialog.setText("Are you sure, Do you want to pullback OD Request.");
                                txt_header.setText("Cancel Leave Request");
                                String keyid = "";
                                spin_reason_commentCancel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                                        HashMap<String, String> map = arlRequstedCancel.get(i);
//                                        keyid = map.get("LeaveCancelRejectReasonId");
//                                        System.out.println("pos     " + i + "    keyid    " + keyid);
                                        try {
                                            if (i == 0) {
//                                            positionValue = "";
                                                positionId = "";
                                            } else if (i > 0) {
                                                positionId = arlRequstedCancel.get(i - 1).get("LeaveCancelRejectReasonId");
//                                            positionValue = arlRequestStatusData.get(position - 1).get("VALUE");
                                            }
                                            Log.e("Spin Value", "Spin Id " + positionId + " Value " + "");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (positionId.equals("")) {
                                            Utilities.showDialog(coordinatorLayout, "Please Select any Reason.");
                                        } else {
//                                            cancelRequest(employeeId, positionId, requestId);
                                            sendLeaveRequestCancelPost(employeeId, positionId, requestId);
                                            dialog.dismiss();
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

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), "Long press on position :" + position,
                        Toast.LENGTH_LONG).show();
            }
        }));


        btn_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fromDate.setText("");
                calanderHRMS.datePicker(btn_fromDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = btn_fromDate.getText().toString().trim();
                            TO_DATE = btn_toDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                GetCompareDateResult(FROM_DATE, TO_DATE);
                            } else {
                                handler.postDelayed(rRunnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GetFuture Method ", e.getMessage());
                        }
                    }
                };
                handler.postDelayed(rRunnable, 1000);

            }
        });


        btn_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_toDate.setText("");
                calanderHRMS.datePicker(btn_toDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = btn_fromDate.getText().toString().trim();
                            TO_DATE = btn_toDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                GetCompareDateResult(FROM_DATE, TO_DATE);
                            } else {
                                handler.postDelayed(rRunnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GetFuture Method ", e.getMessage());
                        }
                    }
                };
                handler.postDelayed(rRunnable, 1000);

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    token = (shared.getString("Token", ""));
                    empoyeeId = (shared.getString("EmpoyeeId", ""));

                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "0,1,6,7";
                    }
                    String gettodate = btn_toDate.getText().toString().replace("/", "-");
                    String getfromdate = btn_fromDate.getText().toString().replace("/", "-");

                    if (getfromdate.equals(""))
                        getfromdate = "-";

                    if (gettodate.equals(""))
                        gettodate = "-";

                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (linear_result_compareDate.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getLeaveStatusValue(getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                }

            }
        });

        btn_spin_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final CheckBox[] checkBox = new CheckBox[arlRequestStatusData.size()];
                    final TextView[] txtView = new TextView[arlRequestStatusData.size()];
                    LinearLayout l1 = new LinearLayout(getActivity());
                    l1.setOrientation(LinearLayout.VERTICAL);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle("Please Select");

                    Log.e("ArrayList ", arlRequestStatusData.toString());
                    Log.e("ArrayList ", "" + arlRequestStatusData.size());
                    try {

                        for (int i = 0; i < arlRequestStatusData.size(); i++) {
                            mapdata = arlRequestStatusData.get(i);

                            checkBox[i] = new CheckBox(getActivity());
                            txtView[i] = new TextView(getActivity());

                            checkBox[i].setTextSize(10);
                            checkBox[i].setPadding(0, 0, 0, 0);
                            checkBox[i].setLines((int) 2.0);
                            checkBox[i].setText(mapdata.get("VALUE"));
                            txtView[i].setText(mapdata.get("KEY"));
                            l1.addView(checkBox[i]);

                            final int finalI = i;

                            try {
                                checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {

                                            String spin_new_value = checkBox[finalI].getText().toString();
                                            String spin_new_id = txtView[finalI].getText().toString();

                                            if (!(spinArray.contains(spin_new_value))) {
                                                spinArray.add(spin_new_value);
                                                spinArrayID.add(spin_new_id);

                                            }

                                        } else if (!isChecked) {
                                            spinArray.remove(checkBox[finalI].getText().toString());
                                            spinArrayID.remove(txtView[finalI].getText().toString());
                                        }

                                    }
                                });

                                for (int k = 0; k < spinArrayID.size(); k++) {
                                    int index = Integer.parseInt(spinArrayID.get(k));
                                    Log.e("Spin Index", "" + index);
                                    for (int j = 0; j < arlRequestStatusData.size(); j++) {
                                        mapdata = arlRequestStatusData.get(j);

                                        int spin_index = Integer.parseInt(mapdata.get("KEY"));

                                        if (index == spin_index) {
                                            checkBox[spin_index].setChecked(true);
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        builder.setView(l1);
                        if (spinArrayID.size() == 0) {
                            checkBox[0].setChecked(true);
                            checkBox[1].setChecked(true);
                            checkBox[6].setChecked(true);
                            checkBox[7].setChecked(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btn_spin_leave.setText(spinArray.toString().replace("[", "").replace("]", ""));

                        }
                    });
                    builder.show();
                } catch (Exception e) {
                }
            }
        });

    }

    public void getLeaveStatusValue(String fromDate, String toDate, String requestStatus) {
        try {
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            arlData = new ArrayList<>();
            url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveRequestStatus";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EmployeeId", empoyeeId);
            params_final.put("fromDate", fromDate);
            params_final.put("toDate", toDate);
            params_final.put("requestStatus", requestStatus);

            pm.put("objLeaveRequestStatusInfo", params_final);

            if (Utilities.isNetworkAvailable(getActivity())) {
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    pDialog.dismiss();
                                    HashMap<String, String> leaveStatusmap;
                                    JSONArray jsonArray = response.getJSONArray("GetLeaveRequestStatusResult");

                                    System.out.println("jsonArray===" + jsonArray);
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            leaveStatusmap = new HashMap<>();
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            String actionBy = explrObject.getString("ACTION_BY");
                                            String actionDate = explrObject.getString("ACTION_DATE");

                                            String requestId = explrObject.getString("ERFS_REQUEST_ID");
                                            String fromDate = explrObject.getString("FROM_DATE");
                                            String leaveName = explrObject.getString("LM_LEAVE_NAME");


                                            String lrEmployeeId = explrObject.getString("LR_EMPLOYEE_ID");
                                            String lrReason = explrObject.getString("LR_REASON");
                                            String lrStatus = explrObject.getString("LR_STATUS");
                                            String tokenNo = explrObject.getString("TOKEN_NO");
                                            String toDate = explrObject.getString("TO_DATE");
                                            String type = explrObject.getString("TYPE");
                                            String requestStatus = explrObject.getString("REQUEST_STATUS");

                                            int token_no = Integer.parseInt(explrObject.getString("TOKEN_NO"));
                                            arrayList.add(token_no);

                                            leaveStatusmap.put("TOKEN_NO", tokenNo);
                                            leaveStatusmap.put("FROM_DATE", fromDate);
                                            leaveStatusmap.put("TO_DATE", toDate);
                                            leaveStatusmap.put("LM_LEAVE_NAME", leaveName);
                                            leaveStatusmap.put("TYPE", type);
                                            leaveStatusmap.put("LR_REASON", lrReason);
                                            leaveStatusmap.put("LR_STATUS", lrStatus);
                                            leaveStatusmap.put("ACTION_BY", actionBy);
                                            leaveStatusmap.put("ACTION_DATE", actionDate);
                                            leaveStatusmap.put("LR_EMPLOYEE_ID", lrEmployeeId);
                                            leaveStatusmap.put("REQUEST_STATUS", requestStatus);
                                            leaveStatusmap.put("LR_STATUS", lrStatus);
                                            leaveStatusmap.put("ERFS_REQUEST_ID", requestId);

                                            arlData.add(leaveStatusmap);
                                        }
                                        System.out.println("Array===" + arlData);
                                        Collections.sort(arrayList, Collections.<Integer>reverseOrder());
                                        System.out.println("ArrayList===" + arrayList);

                                        mAdapter = new LeaveStatusFicciAdapter(getActivity(), arlData, arrayList);
                                        recyclerView.setAdapter(mAdapter);
                                        txtDataNotFound.setVisibility(View.GONE);
                                    } else {
                                        txtDataNotFound.setVisibility(View.VISIBLE);
                                        recyclerView.setAdapter(null);
                                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        error.printStackTrace();
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
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);

        }
    }

    public void GetCompareDateResult(String fromDate, String toDate) {
        Constants.COMPARE_DATE_API = true;
        Log.e("Compare Method", "" + Constants.COMPARE_DATE_API);

        String From_Date = fromDate;
        String To_Date = toDate;

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                JSONObject params_final = new JSONObject();
                params_final.put("From_Date", From_Date);
                params_final.put("To_Date", To_Date);
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();
                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());
                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        txt_result_compareDate.setText("From Date should be less than or equal To Date!");
                                        linear_result_compareDate.setVisibility(View.VISIBLE);
                                    } else {
                                        linear_result_compareDate.setVisibility(View.GONE);
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("Error", "" + error.getMessage());
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
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }
    }

    private void pullBackRequest(String empid, String reqId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                pullbackrequestasynctask = new LeaveStatusFicciFragment.PullBackRequestAsynTask();
                pullbackrequestasynctask.empid = empid;
                pullbackrequestasynctask.reqId = reqId;
                pullbackrequestasynctask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PullBackRequestAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        String reqId;
        String token;

        @Override
        protected void onPreExecute() {
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String PULLBACKLEAVEREQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackLeaveRequest/" + empid + "/" + reqId;

                System.out.println("PULLBACKLEAVEREQUEST_URL====" + PULLBACKLEAVEREQUEST_URL);
                JSONParser jParser = new JSONParser(getActivity());
                String json = jParser.makeHttpRequest(PULLBACKLEAVEREQUEST_URL, "GET");

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

            try {
                System.out.println("PULLBACKLEAVEREQUEST_URL==" + result);
                if (pDialog != null && pDialog.isShowing()) {

                    try {
                        pDialog.dismiss();
                        result = result.replaceAll("^\"+|\"+$", " ").trim();

                        int res = Integer.parseInt(result);
                        String keyid;
                        if (spinArrayID.size() == 0) {
                            keyid = "0,1,6,7";
                        } else {
                            keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                        }
                        if (res > 0) {
                            Utilities.showDialog(coordinatorLayout, "Leave Encashment request pull-back successfully.");
                            getLeaveStatusValue("-", "-", keyid);

                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error during pull-back of Leave Request.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    public void getSpinCancelData(Spinner spinReason) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequstedCancel = new ArrayList<>();
                getSpinCancelDataAsynTask = new LeaveStatusFicciFragment.GetSpinCancelDataAsynTask();
                getSpinCancelDataAsynTask.spinReason = spinReason;
                getSpinCancelDataAsynTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetSpinCancelDataAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        Spinner spinReason;
        String token;

        @Override
        protected void onPreExecute() {
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveCancelRejectReason/1";
                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity());
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
            try {
                System.out.println("jsonArray===" + result);
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        HashMap<String, String> requestStatusmap;

                        JSONArray jsonArray = new JSONArray(result);
                        System.out.println("jsonArray===" + jsonArray);

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                requestStatusmap = new HashMap<String, String>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String key = explrObject.getString("LeaveCancelRejectReasonId");
                                String value = explrObject.getString("Reason");
                                requestStatusmap.put("LeaveCancelRejectReasonId", key);
                                requestStatusmap.put("Reason", value);

                                arlRequstedCancel.add(requestStatusmap);
                            }
                            System.out.println("Array===" + arlRequstedCancel);

                            customspinnercancelAdapter = new CustomSpinnerLeaveAdapter(getActivity(), arlRequstedCancel);
                            spinReason.setAdapter(customspinnercancelAdapter);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }

    }

    private void cancelRequest(String empid, String reason, String reqId) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                cancelrequestasynctask = new LeaveStatusFicciFragment.CancelRequestAsynTask();
                cancelrequestasynctask.empid = empid;
                cancelrequestasynctask.reason = reason;
                cancelrequestasynctask.reqId = reqId;
                cancelrequestasynctask.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class CancelRequestAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        String reason, reqId;
        String token;


        @Override
        protected void onPreExecute() {
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {

            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                String LEAVECANCELREQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendLeaveCancellationRequest/" + empid + "/" + reason + "/" + reqId;

                System.out.println("LEAVECANCELREQUEST_URL====" + LEAVECANCELREQUEST_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        LEAVECANCELREQUEST_URL, "GET");

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
            try {
                System.out.println("LEAVECANCELREQUEST_RESULT==" + result);
                if (pDialog != null && pDialog.isShowing()) {

                    try {
                        pDialog.dismiss();
                        result = result.replaceAll("^\"+|\"+$", " ").trim();

                        int res = Integer.parseInt(result);
                        String keyid;
                        if (spinArrayID.size() == 0) {
                            keyid = "0,1,6,7";
                        } else {
                            keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                        }


                        if (res == 1) {
                            Utilities.showDialog(coordinatorLayout, "Leave Cancellation request send sucessfully.");
                            getLeaveStatusValue("-", "-", keyid);
                        } else if (res == -1) {
                            Utilities.showDialog(coordinatorLayout, "Request Flow Plan is not available.");
                        } else if (res == -2) {
                            Utilities.showDialog(coordinatorLayout, "Can not take any action on the previous payroll requests.");
                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Some error occur on processing the request.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.v("Exception", "Integer Value Not accepted");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    public void sendLeaveRequestCancelPost(String empId, String comment, String req_id) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", empId);
                param.put("COMMENT", comment);
                param.put("REQUEST_ID", req_id);
                Log.e("RequestData", "Json->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendLeaveCancellationRequestPost";
                Log.e("Url", url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int res = response.getInt("SendLeaveCancellationRequestPostResult");
                                    String keyid;
                                    if (spinArrayID.size() == 0) {
                                        keyid = "0,1,6,7";
                                    } else {
                                        keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                                    }


                                    if (res == 1) {
                                        Utilities.showDialog(coordinatorLayout, "Leave Cancellation request send sucessfully.");

                                        getLeaveStatusValue("-", "-", keyid);
                                    } else if (res == -1) {
                                        Utilities.showDialog(coordinatorLayout, "Request Flow Plan is not available.");
                                    } else if (res == -2) {
                                        Utilities.showDialog(coordinatorLayout, "Can not take any action on the previous payroll requests.");
                                    } else if (res == 0) {
                                        Utilities.showDialog(coordinatorLayout, "Some error occur on processing the request.");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
                int socketTime = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }
    }

    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequestStatusData = new ArrayList<>();
                requestStatusasynctask = new LeaveStatusFicciFragment.RequestStatusAsynTask();
                requestStatusasynctask.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RequestStatusAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        String token;

        @Override
        protected void onPreExecute() {
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {

            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetRequestStatus";
                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity());
                String json = jParser.makeHttpRequest(GETREQUESTSTATUS_URL, "GET");
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
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        HashMap<String, String> requestStatusmap;
                        JSONArray jsonArray = new JSONArray(result);
                        System.out.println("jsonArray===" + jsonArray);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                requestStatusmap = new HashMap<>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String key = explrObject.getString("KEY");
                                String value = explrObject.getString("VALUE");
                                requestStatusmap.put("KEY", key);
                                requestStatusmap.put("VALUE", value);
                                requestStatusmap.put("isSelected", "" + false);

                                arlRequestStatusData.add(requestStatusmap);
                            }
                            System.out.println("Array===" + arlRequestStatusData);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private LeaveStatusFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LeaveStatusFragment.ClickListener clickListener) {
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
}
