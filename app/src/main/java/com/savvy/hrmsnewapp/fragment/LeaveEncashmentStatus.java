package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.savvy.hrmsnewapp.adapter.LeaveEncashmentStatusAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class LeaveEncashmentStatus extends BaseFragment {

    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData;

    LeaveEncashmentStatus.LeSpinnerStatusAsync leSpinnerStatusAsync;
    LeaveEncashmentStatus.LEStatusAsynTask leStatusAsynTask;
    LeaveEncashmentStatus.PullBackAsync pullBackAsync;
    LeaveEncashmentStatusAdapter mAdapter;
    CalanderHRMS calanderHRMS;
    RecyclerView recyclerView;
    Button btn_fromDate, btn_toDate;
    Button btn_Search, btn_spinLeStatus;

    CustomSpinnerAdapter customspinnerAdapter;
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    String employeeId = "", requestId = "", punch_status_1 = "";
    String token = "";
    String token_no = "";
    String json = "";
    String empoyeeId = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    CustomTextView txt_punchToken_no, txt_actionValuePull, resultTextView, tvDataNotFound;
    LinearLayout Linear_resultLayout;
    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    ArrayList<Integer> arrayList;
    HashMap<String, String> mapdata;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getStatusList();
        spinArray = new ArrayList();
        spinArrayID = new ArrayList();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        getLERequestStatus(empoyeeId, token, "-", "-", "0,1,6,7");
    }

    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequestStatusData = new ArrayList<>();
                leSpinnerStatusAsync = new LeaveEncashmentStatus.LeSpinnerStatusAsync();
                leSpinnerStatusAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LeSpinnerStatusAsync extends AsyncTask<String, String, String> {
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

    private void getLERequestStatus(String empid, String token, String fromdate, String todate, String spinId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arrayList = new ArrayList<>();
                arlData = new ArrayList<>();
                leStatusAsynTask = new LeaveEncashmentStatus.LEStatusAsynTask();
                leStatusAsynTask.empid = empid;
                leStatusAsynTask.fromdate = fromdate;
                leStatusAsynTask.todate = todate;
                leStatusAsynTask.spnid = spinId;
                leStatusAsynTask.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LEStatusAsynTask extends AsyncTask<String, String, String> {
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

                final String LEAVEENCASHMENT_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveEncashmentRequestStatus/" + empid + "/" + fromdate + "/" + todate + "/" + spnid;
                System.out.println("ATTENDANCE_URL====" + LEAVEENCASHMENT_URL);
                JSONParser jParser = new JSONParser(getActivity());
                json = jParser.makeHttpRequest(LEAVEENCASHMENT_URL, "GET");
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
                                odStatusmap = new HashMap<String, String>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);

                                String tokenNo = explrObject.getString("TOKEN_NO");
                                String leaveType = explrObject.getString("LM_LEAVE_NAME");
                                String approveDays = explrObject.getString("LER_APPROVED_NO_OF_DAYS");
                                String comment = explrObject.getString("LER_REASON");
                                String status = explrObject.getString("REQUEST_STATUS");
                                String encashDays = explrObject.getString("LER_NO_OF_DAYS");
                                String employee_Id = explrObject.getString("LER_EMPLOYEE_ID");
                                String request_Id = explrObject.getString("ERFS_REQUEST_ID");
                                String leStatus = explrObject.getString("LEAVE_ENCASHMENT_STATUS");

                                odStatusmap.put("TOKEN_NO", tokenNo);
                                odStatusmap.put("LM_LEAVE_NAME", leaveType);
                                odStatusmap.put("LER_APPROVED_NO_OF_DAYS", approveDays);
                                odStatusmap.put("LER_REASON", comment);
                                odStatusmap.put("REQUEST_STATUS", status);
                                odStatusmap.put("LER_NO_OF_DAYS", encashDays);
                                odStatusmap.put("LER_EMPLOYEE_ID", employee_Id);
                                odStatusmap.put("ERFS_REQUEST_ID", request_Id);
                                odStatusmap.put("LEAVE_ENCASHMENT_STATUS", leStatus);


                                arlData.add(odStatusmap);
                            }
                            System.out.println("Array===" + arlData);
                            System.out.println("ArrayList===" + arrayList);

                            recyclerView.setVisibility(View.VISIBLE);
                            tvDataNotFound.setVisibility(View.GONE);
                            mAdapter = new LeaveEncashmentStatusAdapter(getActivity(), coordinatorLayout, arlData);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            recyclerView.setAdapter(null);
                            recyclerView.setVisibility(View.GONE);
                            tvDataNotFound.setVisibility(View.VISIBLE);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leave_encashment_status, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        btn_fromDate = getActivity().findViewById(R.id.btn_LEFromDate);
        btn_toDate = getActivity().findViewById(R.id.btn_to_LEdate);
        tvDataNotFound = getActivity().findViewById(R.id.tvDataNotFound);
        btn_Search = getActivity().findViewById(R.id.btn_LeSearch);
        btn_spinLeStatus = getActivity().findViewById(R.id.btn_spinLeStatus);
        Linear_resultLayout = getActivity().findViewById(R.id.Linear_resultLayout);
        resultTextView = getActivity().findViewById(R.id.resultTextView);
        if (spinArrayID.size() == 0) {
            btn_spinLeStatus.setText("Pending,Inprocess");
        }
        recyclerView = getActivity().findViewById(R.id.leRecyclerStatus);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new LeaveEncashmentStatus.RecyclerTouchListener(getContext(), recyclerView, new LeaveEncashmentStatus.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_actionValuePull = view.findViewById(R.id.txt_LE_action);
                    txt_punchToken_no = view.findViewById(R.id.txt_LE_token);

                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            try {
                                token_no = txt_punchToken_no.getText().toString();
                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {

                                    String employee_Id = mapdata.get("LER_EMPLOYEE_ID");
                                    String request_Id = mapdata.get("ERFS_REQUEST_ID");

                                    employeeId = employee_Id;
                                    requestId = request_Id;

                                    Log.e("Token No.", str);
                                    Log.e("Employee Data", "" + employeeId);
                                    Log.e("Request Id", "" + requestId);

                                }

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.pullback_dialogbox);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback Leave Encashment Request.");
                                txt_header.setText("Pull Back Leave Encashment Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getPullBackStatus(employeeId, requestId, punch_status_1);
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
                                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
                btn_toDate.setText("");
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

        btn_spinLeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btn_spinLeStatus.setText(spinArray.toString().replace("[", "").replace("]", ""));

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

        btn_Search.setOnClickListener(new View.OnClickListener() {
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
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";
                    if (Linear_resultLayout.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getLERequestStatus(empoyeeId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }
            }
        });
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private LeaveEncashmentStatus.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LeaveEncashmentStatus.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onClick(child, recyclerView.getChildPosition(child));
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        //clickListener.onLongClick(child, recyclerView.getChildPosition(child));
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
                                        resultTextView.setText("From Date should be less than or equal To Date!");
                                        Linear_resultLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        Linear_resultLayout.setVisibility(View.GONE);
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

    public void getPullBackStatus(String employeeId, String requestId, String punch_status_1) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                pullBackAsync = new LeaveEncashmentStatus.PullBackAsync();
                pullBackAsync.employeeId = employeeId;
                pullBackAsync.requestId = requestId;
                pullBackAsync.punch_status_1 = punch_status_1;
                pullBackAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class PullBackAsync extends AsyncTask<String, String, String> {
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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackLeaveEncashmentRequest" + "/" + employeeId + "/" + requestId;

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
                        result = result.replaceAll("^\"+|\"+$", " ").trim();
                        int res = Integer.valueOf(result);
                        String keyid;
                        if (spinArrayID.size() == 0) {
                            keyid = "0,1,6,7";
                        } else {
                            keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                        }
                        if (res > 0) {
                            Utilities.showDialog(coordinatorLayout, "Leave Encashment Request Pullback Successfully.");
                            getLERequestStatus(empoyeeId, token, "-", "-", keyid);

                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error During Pullback Leave Encashment Request.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }
}
