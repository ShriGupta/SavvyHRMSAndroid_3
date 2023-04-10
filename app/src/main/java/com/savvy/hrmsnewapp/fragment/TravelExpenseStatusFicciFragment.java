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
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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

import androidx.annotation.RequiresApi;

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
import com.savvy.hrmsnewapp.adapter.TravelExpenseStatusFicciAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TravelExpenseStatusFicciFragment extends BaseFragment {

    TravelExpenseStatusFicciFragment.TESpinnerAsyncTask teSpinnerAsyncTask;
    CoordinatorLayout coordinatorLayout;
    TravelExpenseStatusFicciAdapter travelExpenseStatusFicciAdapter;
    CalanderHRMS calanderHRMS;
    RecyclerView recyclerView;
    Button btn_teFromDate, btn_teToDate, btn_spin_te_status, btn_teStatus_search;
    LinearLayout teCompareDateLayout;
    CustomTextView teCompareDateTextView, teTxtDataNotFound;
    ArrayList<String> spinArrayID;
    ArrayList<HashMap<String, String>> arlData, arrListRequestStatusData;
    String token = "";
    String json = "";
    SharedPreferences shared;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    ArrayList<String> spinArray;
    HashMap<String, String> mapData;
    String token_no = "";
    CustomTextView txt_punchToken_no, txt_actionValuePull;
    boolean recycler_status = false;

    String employee_Id = "";
    String request_Id = "";
    ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinArrayID = new ArrayList<>();
        spinArray = new ArrayList<>();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        try {
            getStatusList();
            token = (shared.getString("Token", ""));
            employee_Id = (shared.getString("EmpoyeeId", ""));
            getTEStatusResult(employee_Id, token, "-", "-", "0,1,6,7");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travel_expense_status_ficci, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        btn_teFromDate = getActivity().findViewById(R.id.btn_TEStatusFromDate);
        btn_teToDate = getActivity().findViewById(R.id.btn_TEStatusToDate);
        btn_teStatus_search = getActivity().findViewById(R.id.btn_teStatus_search);
        teTxtDataNotFound = getActivity().findViewById(R.id.teTxtDataNotFound);
        btn_spin_te_status = getActivity().findViewById(R.id.btn_spin_travelExpense_status);
        teCompareDateLayout = getActivity().findViewById(R.id.travelExpenseCompareDateLayout);
        teCompareDateTextView = getActivity().findViewById(R.id.travelExpenseCompareDateTextView);
        if (spinArrayID.size() == 0) {
            btn_spin_te_status.setText("Pending,Inprocess");
        }

        recyclerView = getActivity().findViewById(R.id.travelExpenseStatusRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        teTxtDataNotFound.setVisibility(View.GONE);
        teCompareDateLayout.setVisibility(View.GONE);

        recyclerView.addOnItemTouchListener(new OPE_StatusFicciFragment.RecyclerTouchListener(getActivity(), recyclerView, new OPE_StatusFicciFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_punchToken_no = view.findViewById(R.id.tv_travelExpenseToken_value);
                    txt_actionValuePull = view.findViewById(R.id.tv_pullback_travelExpense);

                    recycler_status = false;

                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = txt_punchToken_no.getText().toString();
                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {

                                    request_Id = mapdata.get("ERFS_REQUEST_ID");
                                    Log.e("ERFS_REQUEST_ID Id", "" + request_Id);
                                }

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.pullback_dialogbox);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback Travel Expense Request.");
                                txt_header.setText("Pull Back Trave Expense Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getPullBackStatus(employee_Id, request_Id);
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

        btn_teFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_teFromDate.setText("");
                calanderHRMS.datePicker(btn_teFromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btn_teFromDate.getText().toString().trim();
                            String ToDate = btn_teToDate.getText().toString().trim();

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
        btn_teToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(btn_teToDate);
                handler1 = new Handler();
                runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = btn_teFromDate.getText().toString().trim();
                            String ToDate = btn_teToDate.getText().toString().trim();

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
        btn_spin_te_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CheckBox[] checkBox = new CheckBox[arrListRequestStatusData.size()];
                final TextView[] txtView = new TextView[arrListRequestStatusData.size()];
                LinearLayout l1 = new LinearLayout(getActivity());
                l1.setOrientation(LinearLayout.VERTICAL);

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

                                    } else if (!isChecked) {
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
                        // TODO Auto-generated method stub
                        btn_spin_te_status.setText(spinArray.toString().replace("[", "").replace("]", ""));
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
        btn_teStatus_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    token = (shared.getString("Token", ""));
                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "1,2,6,7";
                    }
                    String gettodate = btn_teToDate.getText().toString().replace("/", "-");
                    String getfromdate = btn_teFromDate.getText().toString().replace("/", "-");
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";
                    if (teCompareDateLayout.getVisibility() == View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                    } else {
                        getTEStatusResult(employee_Id, token, getfromdate, gettodate, keyid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getStatusList() {

        arrListRequestStatusData = new ArrayList();
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                teSpinnerAsyncTask = new TravelExpenseStatusFicciFragment.TESpinnerAsyncTask();
                teSpinnerAsyncTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TESpinnerAsyncTask extends AsyncTask<String, String, String> {
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
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(GETREQUESTSTATUS_URL, "GET");

                if (json != null) {
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

    private void getTEStatusResult(String empid, String token, String fromdate, String todate, String spinId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

                arlData = new ArrayList<>();
                JSONObject pm = new JSONObject();
                JSONObject param_final = new JSONObject();

                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                pm.put("EMPLOYEE_ID", empid);
                pm.put("FROM_DATE", fromdate);
                pm.put("TO_DATE", todate);
                pm.put("REQUEST_STATUS", spinId);

                final String TRAVEL_STATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelVoucherRequestStatusFicci";

                param_final.put("objTravelVoucherRequestStatusInfo", pm);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TRAVEL_STATUS_URL, param_final, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("GetTravelVoucherRequestStatusFicciResult"));

                            if (pDialog != null && pDialog.isShowing()) {
                                try {
                                    pDialog.dismiss();
                                    HashMap<String, String> teStatusmap;

                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            teStatusmap = new HashMap<>();
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            teStatusmap.put("TOKEN_NO", explrObject.getString("TOKEN_NO"));
                                            teStatusmap.put("TVR_VOUCHER_NO", explrObject.getString("TVR_VOUCHER_NO"));
                                            teStatusmap.put("REQUESTED_DATE", explrObject.getString("REQUESTED_DATE"));
                                            teStatusmap.put("TVR_REQUESTED_AMOUNT", explrObject.getString("TVR_REQUESTED_AMOUNT"));
                                            teStatusmap.put("TVR_REMARKS", explrObject.getString("TVR_REMARKS"));
                                            teStatusmap.put("REQUEST_STATUS", explrObject.getString("REQUEST_STATUS"));
                                            teStatusmap.put("ACTION_DATE", explrObject.getString("ACTION_DATE"));
                                            teStatusmap.put("TVR_APPROVED_AMOUNT", explrObject.getString("TVR_APPROVED_AMOUNT"));
                                            teStatusmap.put("TRAVEL_VOUCHER_STATUS_1", explrObject.getString("TRAVEL_VOUCHER_STATUS_1"));

                                            teStatusmap.put("TVR_EMPLOYEE_ID", explrObject.getString("TVR_EMPLOYEE_ID"));
                                            teStatusmap.put("ERFS_REQUEST_ID", explrObject.getString("ERFS_REQUEST_ID"));

                                            arlData.add(teStatusmap);
                                        }

                                        recyclerView.setVisibility(View.VISIBLE);
                                        teTxtDataNotFound.setVisibility(View.GONE);
                                        travelExpenseStatusFicciAdapter = new TravelExpenseStatusFicciAdapter(getActivity(), coordinatorLayout, arlData);
                                        recyclerView.setAdapter(travelExpenseStatusFicciAdapter);

                                    } else {
                                        recyclerView.setAdapter(null);
                                        recyclerView.setVisibility(View.GONE);
                                        teTxtDataNotFound.setVisibility(View.VISIBLE);
                                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
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
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPullBackStatus(final String employee_id, String request_id) {

        //todo  need to update the code


        if (Utilities.isNetworkAvailable(getActivity())) {

            arlData = new ArrayList<>();
            JSONObject pm = new JSONObject();
            JSONObject param_final = new JSONObject();

            try {
                pm.put("EMPLOYEE_ID", employee_id);
                pm.put("FROM_DATE", request_id);
                param_final.put("objEmployeeRequestInfo", pm);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String PULLBACK_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackTravelVoucherRequestFicci";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, PULLBACK_URL, param_final, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if (pDialog != null && pDialog.isShowing()) {
                        try {
                            pDialog.dismiss();

                            int res = Integer.valueOf(response.getString("PullBackTravelVoucherRequestFicciResult"));

                            String keyid;
                            if (spinArrayID.size() == 0) {
                                keyid = "0,1,6,7";
                            } else {
                                keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                            }
                            if (res > 0) {
                                Utilities.showDialog(coordinatorLayout, "Travel Expense Request Pullback Successfully.");
                                getTEStatusResult(employee_id, token, "-", "-", keyid);
                            } else if (res == 0) {
                                Utilities.showDialog(coordinatorLayout, "Error During Pullback of Travel Expense Request.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                        }
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
            jsonObjectRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
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
                                        teCompareDateTextView.setText("From Date should be less than or equal To Date!");
                                        teCompareDateLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        teCompareDateLayout.setVisibility(View.GONE);
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TravelExpenseStatusFicciFragment.ClickListener clickListener) {
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

