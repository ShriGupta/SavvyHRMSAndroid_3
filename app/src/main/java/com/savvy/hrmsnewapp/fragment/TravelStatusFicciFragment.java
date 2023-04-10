package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
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
import com.savvy.hrmsnewapp.activity.AddShowTripReport;
import com.savvy.hrmsnewapp.adapter.TravelStatusFicciListAdapter;
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

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class TravelStatusFicciFragment extends BaseFragment {

    TravelStatusFicciListAdapter mAdapter;
    TravelStatusFicciFragment.PullBackAsync pullBackAsync;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    CoordinatorLayout coordinatorLayout;
    Button fromDate, toDate, btnSearch, btnSpinStatus;
    CustomTextView txtDataNotFound, txtComapreDate;
    RecyclerView recyclerView;
    LinearLayout travelCompareDateLayout;
    CalanderHRMS calanderHRMS;

    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    String token = "";
    String empId = "";
    String requestId = "";
    String token_no = "";
    ArrayList<String> spinArrayID;
    SharedPreferences shared;

    ArrayList<String> spinArray;
    HashMap<String, String> mapData;

    ArrayList<HashMap<String, String>> arrayList, arrListRequestStatusData;
    TravelStatusFicciFragment.TravelStatusSpinnerTask travelStatusSpinnerTask;
    ProgressDialog progressDialog;

    CustomTextView txt_actionValuePull, txt_punchToken_no, addTrip, txt_statusCancel, txt_statusAddViewReport;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStatusList();
        spinArrayID = new ArrayList<>();
        spinArray = new ArrayList<>();
        shared = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        empId = (shared.getString("EmpoyeeId", ""));

        getTravelRequestStatus(empId, token, "-", "-", "0,1,6,7");

    }

    private void getTravelRequestStatus(String empId, String token, String fromDate, String toDate, final String requestStatus) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        final String TRVAL_REQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelRequestStatusFicci/" + empId + "/" + fromDate + "/" + toDate + "/" + requestStatus;
        Log.d(TAG, "getTravelRequestStatus: " + TRVAL_REQUEST_URL);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, TRVAL_REQUEST_URL, new Response.Listener<JSONArray>() {
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
                            mapdata.put("ERFS_REQUEST_ID", response.getJSONObject(i).get("ERFS_REQUEST_ID").toString());
                            mapdata.put("TRF_EMPLOYEE_ID", response.getJSONObject(i).get("TRF_EMPLOYEE_ID").toString());
                            mapdata.put("TOKEN_NO", response.getJSONObject(i).get("TOKEN_NO").toString());
                            mapdata.put("TRF_START_DATE", response.getJSONObject(i).get("TRF_START_DATE").toString());
                            mapdata.put("TRF_END_DATE", response.getJSONObject(i).get("TRF_END_DATE").toString());
                            mapdata.put("TRF_TRAVEL_TYPE", response.getJSONObject(i).get("TRF_TRAVEL_TYPE").toString());
                            mapdata.put("TPM_PROJECT_NAME", response.getJSONObject(i).get("TPM_PROJECT_NAME").toString());
                            mapdata.put("TRF_TRAVEL_REASON", response.getJSONObject(i).get("TRF_TRAVEL_REASON").toString());
                            mapdata.put("REQUEST_STATUS", response.getJSONObject(i).get("REQUEST_STATUS").toString());
                            mapdata.put("ACTION_BY", response.getJSONObject(i).get("ACTION_BY").toString());
                            mapdata.put("TRF_STATUS", response.getJSONObject(i).get("TRF_STATUS").toString());
                            mapdata.put("TTRF_TRAVEL_REQUEST_ID", response.getJSONObject(i).get("TTRF_TRAVEL_REQUEST_ID").toString());
                            mapdata.put("TTRF_ID", response.getJSONObject(i).get("TTRF_ID").toString());
                            mapdata.put("TVRD_TRAVEL_VOUCHER_REQUEST_ID", response.getJSONObject(i).get("TVRD_TRAVEL_VOUCHER_REQUEST_ID").toString());
                            mapdata.put("TRF_TRAVEL_TRIP_REPORT", response.getJSONObject(i).get("TRF_TRAVEL_TRIP_REPORT").toString());
                            mapdata.put("TTRF_STATUS", response.getJSONObject(i).get("TTRF_STATUS").toString());

                            arrayList.add(mapdata);
                        }

                        mAdapter = new TravelStatusFicciListAdapter(getActivity(), coordinatorLayout, arrayList);
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

        requestQueue.add(jsonArrayRequest);
    }


    private void getStatusList() {

        arrListRequestStatusData = new ArrayList();
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                travelStatusSpinnerTask = new TravelStatusFicciFragment.TravelStatusSpinnerTask();
                travelStatusSpinnerTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TravelStatusSpinnerTask extends AsyncTask<String, String, String> {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travel_status_ficci, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        fromDate = getActivity().findViewById(R.id.btn_StatusFromDate);
        toDate = getActivity().findViewById(R.id.btn_StatusToDate);
        btnSpinStatus = getActivity().findViewById(R.id.btn_spin_travel_Status);
        btnSearch = getActivity().findViewById(R.id.btn_travelStatus_search);
        txtDataNotFound = getActivity().findViewById(R.id.travelTxtDataNotFound);
        txtComapreDate = getActivity().findViewById(R.id.travelCompareDateTextView);
        travelCompareDateLayout = getActivity().findViewById(R.id.travelCompareDateLayout);

        if (spinArrayID.size() == 0) {
            btnSpinStatus.setText("Pending,Inprocess");
        }

        recyclerView = getActivity().findViewById(R.id.travelStatusRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new TravelStatusFicciFragment.RecyclerTouchListener(getActivity(), recyclerView, new TravelStatusFicciFragment.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                    final HashMap<String, String> mapdata = arrayList.get(position);
                    txt_punchToken_no = view.findViewById(R.id.txt_travelNumber);
                    txt_actionValuePull = view.findViewById(R.id.txt_statusPullBack);
                    txt_statusCancel = view.findViewById(R.id.txt_statusCancel);
                    txt_statusAddViewReport = view.findViewById(R.id.txt_statusAddViewReport);
                    addTrip = view.findViewById(R.id.txt_statusAddTrip);


                    txt_statusAddViewReport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("ERFS_REQUEST_ID", mapdata.get("ERFS_REQUEST_ID"));
                            bundle.putString("TTRF_ID", mapdata.get("TTRF_ID"));
                            bundle.putString("TRF_TRAVEL_TRIP_REPORT", mapdata.get("TRF_TRAVEL_TRIP_REPORT"));

                            Intent intent = new Intent(getActivity(), AddShowTripReport.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = txt_punchToken_no.getText().toString();
                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {
                                    requestId = mapdata.get("ERFS_REQUEST_ID");
                                }

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.pullback_dialogbox);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback Travel Request.");
                                txt_header.setText("Pull Back Travel Request");

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

                    addTrip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Bundle bundle = new Bundle();
                            bundle.putString("ERFS_REQUEST_ID", arrayList.get(position).get("ERFS_REQUEST_ID"));
                            bundle.putString("TTRF_ID", arrayList.get(position).get("TTRF_ID"));
                            bundle.putString("TTRF_STATUS", arrayList.get(position).get("TTRF_STATUS"));

                            Intent intent = new Intent(getActivity(), AddShowTripReport.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    txt_statusCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = txt_punchToken_no.getText().toString();

                                String str = mapdata.get("TOKEN_NO");
                                if (token_no.equals(str)) {
                                    requestId = mapdata.get("ERFS_REQUEST_ID");
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
                                txt_header.setText("Cancel Travel Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String comment = edt_approve_comment_Cancel.getText().toString();
                                        if (comment.equals("")) {
                                            Utilities.showDialog(coordinatorLayout, "Please Enter Comment.");
                                        } else {
                                            sendTravelCancelRequest(empId, comment, requestId);
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

                                        } else  {
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
                        getTravelRequestStatus(empId, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private void getPullBackStatus(String empId, String requestId) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                pullBackAsync = new TravelStatusFicciFragment.PullBackAsync();
                pullBackAsync.employeeId = empId;
                pullBackAsync.requestId = requestId;
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

                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackTravelRequestFicci" + "/" + employeeId + "/" + requestId;
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
                            Utilities.showDialog(coordinatorLayout, "Travel Request Pullback Successfully.");
                            getTravelRequestStatus(employeeId, token, "-", "-", keyid);
                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error During Pullback of Travel Request.");
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

    private void sendTravelCancelRequest(final String emp_id, String comment, String requestId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();
                JSONObject pm = new JSONObject();

                param.put("EMPLOYEE_ID", emp_id);
                param.put("COMMENT", comment);
                param.put("REQUEST_ID", requestId);

                pm.put("objSendCancellationRequestInfo", param);
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendCancellationRequestFicci";

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int res = Integer.valueOf(response.getString("SendCancellationRequestFicciResult"));
                                    String keyid;
                                    if (spinArrayID.size() == 0) {
                                        keyid = "0,1,6,7";
                                    } else {
                                        keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                                    }

                                    if (res == 1) {
                                        Utilities.showDialog(coordinatorLayout, "Travel Request Cancellation Request Send Sucessfully.");
                                        getTravelRequestStatus(emp_id, token, "-", "-", keyid);
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TravelStatusFicciFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TravelStatusFicciFragment.ClickListener clickListener) {
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
