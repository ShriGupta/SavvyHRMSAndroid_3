package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.ODContractListAdapter;
import com.savvy.hrmsnewapp.adapter.ODStatusFicciAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
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
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class ODStatusFicciFragment extends BaseFragment {

    ODStatusFicciFragment.ODSpinnerAsyncTask odSpinnerAsyncTask;
    ODStatusFicciFragment.ODStatusAsync odStatusAsync;
    ODStatusFicciFragment.PullBackAsyncTask pullBackAsyncTask;
    ODStatusFicciAdapter mAdapter;
    ODContractListAdapter odContractListAdapter;
    CoordinatorLayout coordinatorLayout;
    Button btn_fromDate, btn_toDate;
    Button btn_search, btn_spinnerStatus;
    CustomTextView txtDataNotFound;
    LinearLayout linear_result_compareDate;
    CustomTextView txt_result_compareDate;
    RecyclerView recyclerView, viewDetailList;
    CalanderHRMS calanderHRMS;
    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    ArrayList<HashMap<String, String>> arlData, arrListRequestStatusData;
    HashMap<String, String> mapData;
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    ArrayList<Integer> arrayList;
    String token = "";
    String json = "";
    SharedPreferences shared;
    CustomTextView txt_punchToken_no, txt_actionValuePull, txt_actionValueCancel, viewDetail;
    boolean recycler_status = false;
    String token_no = "";
    String employeeId = "", requestId = "", punch_status_1 = "";
    Button odc_Button;
    ArrayList<HashMap<String, String>> viewList;
    String odtypeValue = "";

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        spinArray = new ArrayList();
        spinArrayID = new ArrayList();
        try {
            getStatusList();
            token = (shared.getString("Token", ""));
            employeeId = (shared.getString("EmpoyeeId", ""));
            getoDRequestStatus(employeeId, token, "-", "-", "0,1,6,7");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_odstatus_ficci, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        mAdapter = new ODStatusFicciAdapter(getActivity(), coordinatorLayout, arlData, "");
        calanderHRMS = new CalanderHRMS(getActivity());
        btn_fromDate = getActivity().findViewById(R.id.btn_statusFromDate);
        btn_toDate = getActivity().findViewById(R.id.btn_statusToDate);
        btn_spinnerStatus = getActivity().findViewById(R.id.btn_spinnerStatus);
        btn_search = getActivity().findViewById(R.id.btn_searchStatus);
        txtDataNotFound = getActivity().findViewById(R.id.textDataNotFound);

        linear_result_compareDate = getActivity().findViewById(R.id.Linear_ResultCompareDate);
        txt_result_compareDate = getActivity().findViewById(R.id.tv_result_compareDate);
        if (spinArrayID.size() == 0) {
            btn_spinnerStatus.setText("Pending,Inprocess");
        }

        recyclerView = getActivity().findViewById(R.id.odstatusRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVisibility(View.VISIBLE);

        txtDataNotFound.setVisibility(View.GONE);
        linear_result_compareDate.setVisibility(View.GONE);

        odtypeValue = getArguments().getString("OD_TYPE");

        recyclerView.addOnItemTouchListener(new ODStatusFicciFragment.RecyclerTouchListener(getActivity(), recyclerView, new ODStatusFicciFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_actionValuePull = view.findViewById(R.id.tv_pullback_od);
                    txt_actionValueCancel = view.findViewById(R.id.tv_cancel_od);
                    txt_punchToken_no = view.findViewById(R.id.tv_odToken_value);
                    viewDetail = view.findViewById(R.id.tv_odViewDetail);

                    recycler_status = false;

                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            try {

                                token_no = txt_punchToken_no.getText().toString();
                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {

                                    String request_Id = mapdata.get("ERFS_REQUEST_ID");
                                    String punch_status_1_Id = mapdata.get("OD_STATUS_1");

                                    requestId = request_Id;
                                    punch_status_1 = punch_status_1_Id;
                                    Log.e("Request Id", "" + requestId);
                                    Log.e("Punch Status", "" + punch_status_1);
                                    Log.e("Token No.", str);
                                }

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.pullback_dialogbox);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback OD Request.");
                                txt_header.setText("Pull Back OD Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getPullBackResult(employeeId, requestId, punch_status_1);
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
                    txt_actionValueCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = txt_punchToken_no.getText().toString();

                                String str = mapdata.get("TOKEN_NO");
                                if (token_no.equals(str)) {

                                    String request_Id = mapdata.get("ERFS_REQUEST_ID");
                                    String punch_status_1_Id = mapdata.get("OD_STATUS_1");
                                    requestId = request_Id;
                                    punch_status_1 = punch_status_1_Id;
                                    Log.e("Request Id", "" + requestId);
                                    Log.e("Punch Status", "" + punch_status_1);
                                    Log.e("Token No.", str);
                                }

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.dialogbox_request_cancel);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialogCancel_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialogCancel);

                                final EditText edt_approve_comment_Cancel = dialog.findViewById(R.id.edt_approve_comment_Cancel);
                                String str1 = "<font color='#EE0000'>*</font>";
                                edt_comment_dialog.setText(Html.fromHtml("Comment " + str1));

                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);
                                txt_header.setText("Cancel OD Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String comment = edt_approve_comment_Cancel.getText().toString();
                                        if (comment.equals("")) {
                                            Utilities.showDialog(coordinatorLayout, "Please Enter Comment.");
                                        } else {
                                            sendODCancelRequest(employeeId, comment, requestId);
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

                    viewDetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestId = mapdata.get("ERFS_REQUEST_ID");
                            assert requestId != null;
                            if (!requestId.equals("")) {
                                viewList = new ArrayList<>();
                                getViewDetail(requestId);
                            }

                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.od_contract_view_detail_dialog);
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                            viewDetailList = dialog.findViewById(R.id.odc_DialogList);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            viewDetailList.setLayoutManager(layoutManager);
                            viewDetailList.setItemAnimator(new DefaultItemAnimator());

                            odc_Button = dialog.findViewById(R.id.odc_OkButton);

                            odc_Button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
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


        btn_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fromDate.setText("");
                calanderHRMS.datePicker(btn_fromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btn_fromDate.getText().toString().trim();
                            String ToDate = btn_toDate.getText().toString().trim();

                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
                            } else {
                                handler.postDelayed(runnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });
        btn_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(btn_toDate);
                handler1 = new Handler();
                runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btn_fromDate.getText().toString().trim();
                            String ToDate = btn_toDate.getText().toString().trim();

                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
                            } else {
                                handler1.postDelayed(runnable1, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler1.postDelayed(runnable1, 1000);
            }
        });
        btn_spinnerStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CheckBox[] checkBox = new CheckBox[arrListRequestStatusData.size()];
                final TextView[] txtView = new TextView[arrListRequestStatusData.size()];
                LinearLayout l1 = new LinearLayout(getActivity());
                l1.setOrientation(LinearLayout.VERTICAL);
//                getActivity().addContentView(txt1,null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle("Please Select");

                Log.e("ArrayList ", arrListRequestStatusData.toString());
                Log.e("ArrayList ", "" + arrListRequestStatusData.size());
                try {
                    for (int i = 0; i < arrListRequestStatusData.size(); i++) {
                        mapData = arrListRequestStatusData.get(i);

                        checkBox[i] = new CheckBox(getActivity());
                        txtView[i] = new TextView(getActivity());
                        checkBox[i].setTextSize(10);
                        checkBox[i].setPadding(0, 0, 0, 0);
                        checkBox[i].setLines((int) 2.0);
                        checkBox[i].setText(mapData.get("VALUE"));
                        txtView[i].setText(mapData.get("KEY"));
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

                                    } else {
                                        spinArray.remove(checkBox[finalI].getText().toString());
                                        spinArrayID.remove(txtView[finalI].getText().toString());
                                    }
                                }
                            });

                            for (int k = 0; k < spinArrayID.size(); k++) {
                                int index = Integer.parseInt(spinArrayID.get(k));
                                Log.e("Spin Index", "" + index);
                                for (int j = 0; j < arrListRequestStatusData.size(); j++) {
                                    mapData = arrListRequestStatusData.get(j);

                                    int spin_index = Integer.parseInt(mapData.get("KEY"));

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
                } catch (Exception e) {
                    e.printStackTrace();
                }

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btn_spinnerStatus.setText(spinArray.toString().replace("[", "").replace("]", ""));
                    }
                });
                builder.show();
                if (spinArrayID.size() == 0) {
                    checkBox[0].setChecked(true);
                    checkBox[1].setChecked(true);
                    checkBox[6].setChecked(true);
                    checkBox[7].setChecked(true);
                }
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    token = (shared.getString("Token", ""));
                    employeeId = (shared.getString("EmpoyeeId", ""));

                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "0,1,6,7";
                    }
                    String gettodate = btn_toDate.getText().toString().replace("/", "-");
                    String getfromdate = btn_fromDate.getText().toString().replace("/", "-");
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";
                    if (linear_result_compareDate.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getoDRequestStatus(employeeId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getViewDetail(String requestId) {


        String VIEW_DETAIL_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetODBreakupFicci/" + requestId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(VIEW_DETAIL_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d(TAG, "onResponse: ");

                    HashMap<String, String> data;

                    if (response.length() > 0) {

                        for (int i = 0; i < response.length(); i++) {
                            data = new HashMap<>();
                            String date = response.getJSONObject(i).getString("ODRD_DATE");
                            String time_in = response.getJSONObject(i).getString("ODRD_TIME_IN");
                            String time_out = response.getJSONObject(i).getString("ODRD_TIME_OUT");
                            data.put("ODRD_DATE", date);
                            data.put("ODRD_TIME_IN", time_in);
                            data.put("ODRD_TIME_OUT", time_out);
                            viewList.add(data);
                        }

                        odContractListAdapter = new ODContractListAdapter(getActivity(), viewList);
                        viewDetailList.setAdapter(odContractListAdapter);
                        viewDetailList.setVisibility(View.VISIBLE);

                    } else {
                        viewDetailList.setAdapter(null);
                        viewDetailList.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Data not Found", Toast.LENGTH_SHORT).show();
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
        int socketTimeout = 3000000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonArrayRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arrListRequestStatusData = new ArrayList<>();
                odSpinnerAsyncTask = new ODStatusFicciFragment.ODSpinnerAsyncTask();
                odSpinnerAsyncTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getoDRequestStatus(String empid, String token, String fromdate, String todate, String spinId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arrayList = new ArrayList<>();
                arlData = new ArrayList<>();
                odStatusAsync = new ODStatusFicciFragment.ODStatusAsync();
                odStatusAsync.empid = empid;
                odStatusAsync.fromdate = fromdate;
                odStatusAsync.todate = todate;
                odStatusAsync.spnid = spinId;
                odStatusAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ODStatusAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid, fromdate, todate, spnid;
        String token;

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
                Log.e("From Date ", "" + fromdate);
                Log.e("To Date ", "" + todate);
                Log.e("Spinner Value ", "" + spnid);

                final String ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetODRequestStatus/" + empid + "/" + fromdate + "/" + todate + "/" + spnid;
                System.out.println("ATTENDANCE_URL====" + ATTENDANCE_URL);
                JSONParser jParser = new JSONParser(getActivity());
                json = jParser.makeHttpRequest(ATTENDANCE_URL, "GET");
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
                        HashMap<String, String> odStatusmap;
                        JSONArray jsonArray = new JSONArray(result);
                        System.out.println("jsonArray===" + jsonArray);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                odStatusmap = new HashMap<>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);

                                String tokenNo = explrObject.getString("TOKEN_NO");
                                String actionBy = explrObject.getString("REQUESTED_BY");
                                String actionDate = explrObject.getString("REQUESTED_DATE");

                                String from_date = explrObject.getString("FROM_DATE");
                                String to_date = explrObject.getString("TO_DATE");
                                String od_Type = explrObject.getString("OD_TYPE_NAME");
                                String odRequestType = explrObject.getString("OD_REQUEST_TYPE_NAME");
                                String odRequestSubType = explrObject.getString("OD_REQUEST_SUB_TYPE_NAME");
                                String odstatus = explrObject.getString("REQUEST_STATUS");

                                String employeeId = explrObject.getString("ODR_EMPLOYEE_ID");
                                String requestId = explrObject.getString("ERFS_REQUEST_ID");
                                String od_status = explrObject.getString("OD_STATUS_1");
                                String reason = explrObject.getString("ODR_REASON");

                                odStatusmap.put("TOKEN_NO", tokenNo);

                                odStatusmap.put("FROM_DATE", from_date);
                                odStatusmap.put("TO_DATE", to_date);
                                odStatusmap.put("OD_TYPE_NAME", od_Type);
                                odStatusmap.put("OD_REQUEST_TYPE_NAME", odRequestType);
                                odStatusmap.put("OD_REQUEST_SUB_TYPE_NAME", odRequestSubType);
                                odStatusmap.put("REQUEST_STATUS", odstatus);
                                odStatusmap.put("REQUESTED_BY", actionBy);
                                odStatusmap.put("REQUESTED_DATE", actionDate);

                                odStatusmap.put("ODR_EMPLOYEE_ID", employeeId);
                                odStatusmap.put("ERFS_REQUEST_ID", requestId);
                                odStatusmap.put("OD_STATUS_1", od_status);
                                odStatusmap.put("REASON", reason);


                                arlData.add(odStatusmap);


                            }
                            System.out.println("Array===" + arlData);
                            System.out.println("ArrayList===" + arrayList);

                            //Display OD Status (arlData);
                            recyclerView.setVisibility(View.VISIBLE);
                            txtDataNotFound.setVisibility(View.GONE);
                            mAdapter = new ODStatusFicciAdapter(getActivity(), coordinatorLayout, arlData, odtypeValue);
                            recyclerView.setAdapter(mAdapter);
//
                        } else {
                            recyclerView.setAdapter(null);
                            recyclerView.setVisibility(View.GONE);
                            txtDataNotFound.setVisibility(View.VISIBLE);
                            Utilities.showDialog(coordinatorLayout, "Data Not Found");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class ODSpinnerAsyncTask extends AsyncTask<String, String, String> {
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
                e.printStackTrace();
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
                                requestStatusmap = new HashMap<String, String>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String key = explrObject.getString("KEY");
                                String value = explrObject.getString("VALUE");
                                requestStatusmap.put("KEY", key);
                                requestStatusmap.put("VALUE", value);

                                arrListRequestStatusData.add(requestStatusmap);
                            }
                            System.out.println("Array===" + arrListRequestStatusData);
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
            }
        }
    }


    public void getCompareDate(String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);
                Log.e("RequestData", "JSon->" + param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                Log.e("Url", url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        txt_result_compareDate.setText("From Date should be less than or equal To Date!");
                                        linear_result_compareDate.setVisibility(View.VISIBLE);
                                    } else {
                                        linear_result_compareDate.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                int socketTime = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonRequest.setRetryPolicy(policy);
                requestQueue.add(jsonRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ODStatusFicciFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ODStatusFicciFragment.ClickListener clickListener) {
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

    public void getPullBackResult(String employeeId, String requestId, String punch_status_1) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                pullBackAsyncTask = new ODStatusFicciFragment.PullBackAsyncTask();
                pullBackAsyncTask.employeeId = employeeId;
                pullBackAsyncTask.requestId = requestId;
                pullBackAsyncTask.punch_status_1 = punch_status_1;
                pullBackAsyncTask.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class PullBackAsyncTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId, requestId, punch_status_1;

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
                System.out.print("\nEmployee Id=" + employeeId + " Request ID = " + requestId + "\n Punch Status = " + punch_status_1);
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackODRequest" + "/" + employeeId + "/" + requestId;

                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
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

                        result = result.replaceAll("^\"+|\"+$", " ").trim();
                        int res = Integer.valueOf(result);

                        String keyid;
                        if (spinArrayID.size() == 0) {
                            keyid = "0,1,6,7";
                        } else {
                            keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                        }

                        if (res > 0) {
                            Utilities.showDialog(coordinatorLayout, "On Duty Request Pullback Successfully.");
                            getoDRequestStatus(employeeId, token, "-", "-", keyid);

                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error During Pullback of On Duty Request.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendODCancelRequest(String emp_id, String comment, String requestId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", emp_id);
                param.put("COMMENT", comment);
                param.put("REQUEST_ID", requestId);

                Log.e("RequestData", param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendODCancellationRequestPost";
                Log.e("URL", url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int res = response.getInt("SendODCancellationRequestPostResult");

                                    String keyid;
                                    if (spinArrayID.size() == 0) {
                                        keyid = "0,1,6,7";
                                    } else {
                                        keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                                    }

                                    if (res == 1) {
                                        Utilities.showDialog(coordinatorLayout, "On Duty Cancellation Request Send Sucessfully.");
                                        getoDRequestStatus(employeeId, token, "-", "-", keyid);
                                    } else if (res == -1) {
                                        Utilities.showDialog(coordinatorLayout, "Request Flow Plan Is Not Available.");
                                    } else if (res == -2) {
                                        Utilities.showDialog(coordinatorLayout, "Can Not Take Any Action On The Previous Payroll Requests.");
                                    } else if (res == 0) {
                                        Utilities.showDialog(coordinatorLayout, "Some Error Occur On Processing The Request.");
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
                RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(retryPolicy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
