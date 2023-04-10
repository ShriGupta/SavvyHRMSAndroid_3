package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.savvy.hrmsnewapp.adapter.ShortLeaveStatusAdapter;
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
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class ShortLeaveStatusFragment extends BaseFragment {

    ShortLeaveStatusFragment.ShortLeaveStatusSpinnerTask shortLeaveStatusSpinnerTask;
    ShortLeaveStatusFragment.PullBackAsync pullBackAsync;

    private Button fromDate, toDate, btnSearch, btnSpinStatus;
    private CustomTextView txtDataNotFound, txtComapreDate;
    private RecyclerView recyclerView;
    private LinearLayout travelCompareDateLayout;
    private CalanderHRMS calanderHRMS;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "";
    String empId = "";
    String requestId = "";
    String token_no = "";
    ArrayList spinArrayID;
    SharedPreferences shared;
    ArrayList spinArray;
    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    ArrayList<HashMap<String, String>> arrayList, arrListRequestStatusData;
    HashMap<String, String> mapData;
    ProgressDialog progressDialog;
    ShortLeaveStatusAdapter mAdapter;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinArrayID = new ArrayList();
        spinArray = new ArrayList();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        empId = (shared.getString("EmpoyeeId", ""));


        getStatusList();
        getShortLeaveRequestStatus(empId, token, "-", "-", "0,1,6,7");
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getShortLeaveRequestStatus(String empId, String token, String fromDate, String toDate, final String requestStatus) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

     //   final String SHORT_LEAVE_REQUEST_STATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetShortLeaveRequestStatus/" + fromDate + "/" + toDate + "/" + requestStatus + "/" + empId;
        final String SHORT_LEAVE_REQUEST_STATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetShortDeductionRequestStatus/" + fromDate + "/" + toDate + "/" + requestStatus + "/" + empId;
        Log.d(TAG, "SHORT_LEAVE_REQUEST_STATUS_URL: " + SHORT_LEAVE_REQUEST_STATUS_URL);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, SHORT_LEAVE_REQUEST_STATUS_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    arrayList = new ArrayList<>();
                    HashMap<String, String> mapdata;
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {

                            mapdata = new HashMap<>();
                            mapdata.put("TOKEN_NO", response.getJSONObject(i).get("TOKEN_NO").toString());
                            mapdata.put("REQUEST_STATUS", response.getJSONObject(i).get("REQUEST_STATUS").toString());
                            mapdata.put("SLR_DATE", response.getJSONObject(i).get("SLR_DATE").toString());
                            mapdata.put("SLC_LEAVE_TYPE", response.getJSONObject(i).get("SLC_LEAVE_TYPE").toString());
                            mapdata.put("ACTION_BY", response.getJSONObject(i).get("ACTION_BY").toString());
                            mapdata.put("ACTION_DATE", response.getJSONObject(i).get("ACTION_DATE").toString());
                            mapdata.put("TYPE", response.getJSONObject(i).get("TYPE").toString());
                            mapdata.put("SLR_REASON", response.getJSONObject(i).get("SLR_REASON").toString());
                            mapdata.put("SLR_STATUS_1", response.getJSONObject(i).get("SLR_STATUS_1").toString());
                         //   mapdata.put("ERFS_REQUEST_ID", response.getJSONObject(i).get("ERFS_REQUEST_ID").toString());
                            mapdata.put("ERFS_REQUEST_ID", response.getJSONObject(i).get("REQUEST_ID").toString());

                            arrayList.add(mapdata);
                        }

                        mAdapter = new ShortLeaveStatusAdapter(getActivity(), arrayList);
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        txtDataNotFound.setVisibility(View.GONE);
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        progressDialog.dismiss();
                        recyclerView.setAdapter(null);
                        recyclerView.setVisibility(View.INVISIBLE);
                        txtDataNotFound.setVisibility(View.VISIBLE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                progressDialog.dismiss();
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

        requestQueue.add(jsonArrayRequest);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frgament_short_leave_status, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        fromDate = getActivity().findViewById(R.id.btn_shortLeave_FromDate);
        toDate = getActivity().findViewById(R.id.btn_shortLeave_ToDate);
        btnSpinStatus = getActivity().findViewById(R.id.btn_shortLeave_Status);
        btnSearch = getActivity().findViewById(R.id.btn_shortLeave_search);
        txtDataNotFound = getActivity().findViewById(R.id.shortLeave_TxtDataNotFound);
        txtComapreDate = getActivity().findViewById(R.id.shortLeaveCompareDateTextView);
        travelCompareDateLayout = getActivity().findViewById(R.id.shortLeaveCompareDateLayout);

        if (spinArrayID.size() == 0) {
            btnSpinStatus.setText("Pending,Inprocess");
        }

        recyclerView = getActivity().findViewById(R.id.shortLeaveStatusRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new ShortLeaveStatusFragment.RecyclerTouchListener(getActivity(), recyclerView, new ShortLeaveStatusFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arrayList.get(position);

                    CustomTextView txt_actionValuePull = view.findViewById(R.id.tv_sl_PullBack);
                    final CustomTextView txt_punchToken_no = view.findViewById(R.id.tv_sl_TokenNumber);


                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = txt_punchToken_no.getText().toString();
                                String str = mapdata.get("TOKEN_NO");
                                Log.e("TAG","Token No.----------"+ str +"=="+token_no);

                                if (token_no.equals(str)) {
                                    Log.e("TAG","Token No.-------under");

                                    requestId = mapdata.get("ERFS_REQUEST_ID");
                                    Log.e("Token No.", str);
                                    Log.e("Employee Id", "" + empId);
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

                                edt_comment_dialog.setText("Are you sure ? Do you want to pullback Short Leave Request ?");
                                txt_header.setText("Pull Back Short Leave Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getPullBackStatus(empId, requestId);
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDate.setText("");
                calanderHRMS.datePicker(fromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = fromDate.getText().toString().trim();
                            String ToDate = toDate.getText().toString().trim();

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
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(toDate);
                handler1 = new Handler();
                runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = fromDate.getText().toString().trim();
                            String ToDate = toDate.getText().toString().trim();

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
        btnSpinStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    final CheckBox[] checkBox = new CheckBox[arrListRequestStatusData.size()];
                    final TextView[] txtView = new TextView[arrListRequestStatusData.size()];
                    LinearLayout l1 = new LinearLayout(getActivity());
                    l1.setOrientation(LinearLayout.VERTICAL);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle("Please Select");

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

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        for (int k = 0; k < spinArrayID.size(); k++) {
                            int index = Integer.parseInt((String) spinArrayID.get(k));
                            Log.e("Spin Index", "" + index);
                            for (int j = 0; j < arrListRequestStatusData.size(); j++) {
                                mapData = arrListRequestStatusData.get(j);

                                int spin_index = Integer.parseInt(mapData.get("KEY"));

                                if (index == spin_index) {
                                    checkBox[spin_index].setChecked(true);
                                }
                            }
                        }
                        builder.setView(l1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            btnSpinStatus.setText(spinArray.toString().replace("[", "").replace("]", ""));
                        }
                    });
                    builder.show();
                    if (spinArrayID.size() == 0) {
                        checkBox[0].setChecked(true);
                        checkBox[1].setChecked(true);
                        checkBox[6].setChecked(true);
                        checkBox[7].setChecked(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                try {
                    token = (shared.getString("Token", ""));
                    empId = (shared.getString("EmpoyeeId", ""));

                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "0,1,6,7";
                    }
                    String gettodate = toDate.getText().toString().replace("/", "-");
                    String getfromdate = fromDate.getText().toString().replace("/", "-");
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";
                    if (travelCompareDateLayout.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getShortLeaveRequestStatus(empId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public void getCompareDate(String fromDate, String toDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);


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
                                        txtComapreDate.setText("From Date should be less than or equal To Date!");
                                        travelCompareDateLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        travelCompareDateLayout.setVisibility(View.GONE);
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
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }
    }

    private void getStatusList() {

        arrListRequestStatusData = new ArrayList();
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                shortLeaveStatusSpinnerTask = new ShortLeaveStatusFragment.ShortLeaveStatusSpinnerTask();
                shortLeaveStatusSpinnerTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ShortLeaveStatusSpinnerTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

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
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }


    private void getPullBackStatus(String employee_id, String request_id) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                pullBackAsync = new ShortLeaveStatusFragment.PullBackAsync();
                pullBackAsync.employeeId = employee_id;
                pullBackAsync.requestId = request_id;
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
        String employeeId, requestId;

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
                Log.e("TAGG","employeeId=="+employeeId +"=="+requestId);


                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackShortDeductionRequest" + "/" + employeeId + "/" + requestId;
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(GETREQUESTSTATUS_URL, "GET");

                Log.e("TAGG","jsonn=="+json.toString());
                if (json != null) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String result) {
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        Log.e("TAG","ressss"+result);
                        result = result.replaceAll("^\"+|\"+$", " ").trim();
                        Log.e("TAG","ressss1"+result);

                        int res = Integer.valueOf(result);
                        Log.e("TAG","ressss2"+res);

                        String keyid;
                        if (spinArrayID.size() == 0) {
                            keyid = "0,1,6,7";
                        } else {
                            keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                        }
                        if (res > 0) {
                            Log.e("TAG","ressss3");

                            Utilities.showDialog(coordinatorLayout, "Short Leave Request Pullback Successfully.");
                            getShortLeaveRequestStatus(employeeId, token, "-", "-", keyid);
                        } else if (res == 0) {
                            Log.e("TAG","ressss4");

                            Utilities.showDialog(coordinatorLayout, "Error During Pullback of Short Leave Request.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.e("TAG","ressss5"+ex.getMessage());

                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG","ressss6");

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
        private ShortLeaveStatusFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ShortLeaveStatusFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
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
