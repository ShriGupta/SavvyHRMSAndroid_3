package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentTransaction;
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
import android.widget.Spinner;
import android.widget.TextView;

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
import com.savvy.hrmsnewapp.adapter.ODStatusListAdapter;
import com.savvy.hrmsnewapp.adapter.StatusExpenseAdapter;
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

public class ExpenseStatusFragment extends BaseFragment implements View.OnClickListener{

    ExpenseStatusFragment.ExpenseSpinnerStatusAsync expenseSpinnerStatusAsync;
    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;

    CalanderHRMS calanderHRMS;
    RecyclerView recyclerView;
    Button btn_fromDate,btn_toDate;
    Button btn_submit, btn_spin_expense;
    Spinner spinner_status;
    CustomSpinnerAdapter customspinnerAdapter;
    ODStatusListAdapter mAdapter;
    StatusExpenseAdapter mAdapter1;
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    String employeeId = "",requestId = "";
    String token = "";
    String token_no = "";
    String empoyeeId = "";
    ArrayList<Integer> arrayList;
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapdata;

    Handler handler;
    Runnable rRunnable;

    String FROM_DATE = "", TO_DATE = "";

    LinearLayout linear_result_compareDate;
    CustomTextView txt_result_compareDate;

    CustomTextView txt_expenseToken_no,txt_actionValuePull,txt_actionValueCancel,txt_actionValueEdit;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    CustomTextView txtDataNotFound;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        arlData = new ArrayList<HashMap<String,String>> ();

        spinArray = new ArrayList();
        spinArrayID = new ArrayList();

        getStatusList();
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));

    }

    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequestStatusData = new ArrayList<HashMap<String, String>>();
                expenseSpinnerStatusAsync = new ExpenseStatusFragment.ExpenseSpinnerStatusAsync();
                // requestStatusasynctask.empid=empid;
                expenseSpinnerStatusAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_status, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
//        mAdapter = new ODStatusListAdapter(getActivity(),coordinatorLayout,arlData,arrayList);

        calanderHRMS = new CalanderHRMS(getActivity());

        btn_fromDate= getActivity().findViewById(R.id.btn_expenseFromDate);
        btn_toDate= getActivity().findViewById(R.id.btn_to_expenseDate);
        btn_submit= getActivity().findViewById(R.id.btn_expenseStatus_submit);
        btn_spin_expense= getActivity().findViewById(R.id.btn_spin_expense_status);

        linear_result_compareDate= getActivity().findViewById(R.id.Linear_result_compareDate);
        txt_result_compareDate= getActivity().findViewById(R.id.result_compareDate);

        linear_result_compareDate.setVisibility(View.GONE);

        if(spinArrayID.size()==0){
            btn_spin_expense.setText("Pending,Inprocess");
        }
        recyclerView = getActivity().findViewById(R.id.expenseRecyclerStatus);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        txtDataNotFound = getActivity().findViewById(R.id.errorData);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        String from_date = btn_fromDate.getText().toString().trim();
        String to_date = btn_toDate.getText().toString().trim();

        if(from_date.equals("")){
            from_date = "-";
        }
        if(to_date.equals("")){
            to_date = "-";
        }

        getExpenseDetailStatus(from_date,to_date,"0,1,6,7");

        recyclerView.addOnItemTouchListener(new ExpenseApprovalFragment.RecyclerTouchListener(getActivity(), recyclerView, new ExpenseApprovalFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_actionValueEdit = view.findViewById(R.id.con_edit);
                    txt_actionValuePull = view.findViewById(R.id.con_pullback);
                    txt_actionValueCancel = view.findViewById(R.id.con_cancel);
                    txt_expenseToken_no = view.findViewById(R.id.con_TokenNo);

                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            token_no = txt_expenseToken_no.getText().toString();
                            // Toast.makeText(getActivity(),"Success 2",Toast.LENGTH_LONG).show();
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

                            edt_comment_dialog.setText("Are you sure, Do you want to pullback Expense Request.");
                            txt_header.setText("Pull Back Expense Request");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendPullbackExpenseRequest(employeeId, requestId);
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

//                            String PullBackTitle = "<font color=\"#5cb85c\"><bold>" + "PullBack Expense Request" + "</bold></font>";
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                            builder.setTitle(Html.fromHtml(PullBackTitle));
//                            builder.setCancelable(false);
//                            builder.setMessage("Are you sure, Do you want to pullback Expense Request.");
//
//                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    sendPullbackExpenseRequest(employeeId, requestId);
//                                }
//                            });
//
//                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//                            builder.show();
                        }
                    });

                    txt_actionValueCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String str = mapdata.get("TOKEN_NO");
                            token_no = txt_expenseToken_no.getText().toString();

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

//                                edt_comment_dialog.setText("Are you sure, Do you want to pullback OD Request.");
                            txt_header.setText("Cancel Expense Request");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String comment = edt_approve_comment_Cancel.getText().toString().replace(" ", "_");
                                    if(comment.equals("")){
                                        Utilities.showDialog(coordinatorLayout,"Please Enter Comment.");
                                    } else {
                                        sendExpenseCancelStatus(employeeId, requestId, comment, "Expense");
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
//                            String CancelTitle = "<font color=\"#E72A02\"><bold>" + "Cancel Expense Request" + "</bold></font>";
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                            builder.setTitle(Html.fromHtml(CancelTitle));
//                            builder.setCancelable(false);
//                            builder.setMessage("Are you sure, Do you want to Cancel Conveyance Request.");
//
//                            final TextView txt_comment = new TextView(getActivity());
//                            txt_comment.setText("    Comment :   ");
//                            txt_comment.setPadding(25,0,5,0);
//                            final EditText edit_comment = new EditText(getActivity());
//                            edit_comment.setEms(10);
//                            edit_comment.setWidth(400);
//                            edit_comment.setSingleLine();
////                            edit_comment.setBackground(R.drawable.border_custom_new);
//
//                            LinearLayout ll = new LinearLayout(getActivity());
//                            ll.setOrientation(LinearLayout.HORIZONTAL);
//                            ll.addView(txt_comment);
//                            ll.addView(edit_comment);
//                            builder.setView(ll);
//                            String comment = edit_comment.getText().toString().replace(" ", "_");
//                            if(comment.equals(""))
//                                comment = "-";
//
//                            final String finalComment = comment;
//                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
////                                    sendConveyanceCancelStatus(employeeId, requestId, finalComment,"Conveyance");
//                                    sendExpenseCancelStatus(employeeId, requestId, finalComment,"Expense");
//                                }
//                            });
//
//                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//                            builder.show();
                        }
                    });

                    txt_actionValueEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            token_no = txt_expenseToken_no.getText().toString();
                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {
                                requestId = mapdata.get("ERFS_REQUEST_ID");

                                Constants.EXPENSE_REQUEST_ID = requestId;
                                Constants.EXPENSE_REQUEST_ID_STATUS = false;
                            }

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            ExpenseFramentHolder expenseFramentHolder = new ExpenseFramentHolder();
                            transaction.replace(R.id.container_body, expenseFramentHolder);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        btn_spin_expense.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_fromDate.setOnClickListener(this);
        btn_toDate.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_spin_expense_status){
            final CheckBox[] checkBox = new CheckBox[arlRequestStatusData.size()];
            final TextView[] txtView = new TextView[arlRequestStatusData.size()];
            LinearLayout l1 = new LinearLayout(getActivity());
            l1.setOrientation(LinearLayout.VERTICAL);

//                getActivity().addContentView(txt1,null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle("Please Select");

            Log.e("ArrayList ",arlRequestStatusData.toString());
            Log.e("ArrayList ",""+arlRequestStatusData.size());
            try {

                for (int i = 0; i < arlRequestStatusData.size(); i++) {
                    mapdata = arlRequestStatusData.get(i);

                    checkBox[i] = new CheckBox(getActivity());
                    txtView[i] = new TextView(getActivity());

                    checkBox[i].setTextSize(10);
                    checkBox[i].setPadding(0,0,0,0);
                    checkBox[i].setLines((int)2.0);
                    checkBox[i].setText(mapdata.get("VALUE"));
                    txtView[i].setText(mapdata.get("KEY"));
                    l1.addView(checkBox[i]);

                    final int finalI = i;

                    try {
//                            Log.e("Spin Id",""+spinArrayID.size());
                        checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {

                                    String spin_new_value = checkBox[finalI].getText().toString();
                                    String spin_new_id = txtView[finalI].getText().toString();

                                    //checkBox[finalI].setChecked(true);
                                    if(!(spinArray.contains(spin_new_value))) {
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

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                builder.setView(l1);
                if(spinArrayID.size()==0){
                    checkBox[0].setChecked(true);
                    checkBox[1].setChecked(true);
                    checkBox[6].setChecked(true);
                    checkBox[7].setChecked(true);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    btn_spin_expense.setText(spinArray.toString().replace("[","").replace("]",""));


                }
            });
            builder.show();
        }
        if(v.getId()==R.id.btn_expenseStatus_submit){
            String keyid = spinArrayID.toString().replace("[","").replace("]","").replace(" ","");
            if(keyid.equals("")) {
                keyid = "0,1,6,7";
            }

            String gettodate=   btn_toDate.getText().toString();
            String getfromdate=    btn_fromDate.getText().toString();
            System.out.print("Date ="+getfromdate+"To Date ="+gettodate+"Key Value = "+keyid);

            if(getfromdate=="")
                getfromdate="-";

            if(gettodate=="")
                gettodate="-";

            if(linear_result_compareDate.getVisibility()==View.VISIBLE){
                Utilities.showDialog(coordinatorLayout,"From Date Should Be Less Than or equal to To Date");
            } else{
                getExpenseDetailStatus(getfromdate,gettodate,keyid);
            }
        }

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

                            if(!FROM_DATE.equals("")&&!TO_DATE.equals("")) {
                                    GetCompareDateResult(FROM_DATE,TO_DATE);
                            } else{
                                handler.postDelayed(rRunnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GetFuture Method ",e.getMessage());
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

                            if(!FROM_DATE.equals("")&&!TO_DATE.equals("")) {
                                    GetCompareDateResult(FROM_DATE,TO_DATE);
                            } else{
                                handler.postDelayed(rRunnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GetFuture Method ",e.getMessage());
                        }
                    }
                };
                handler.postDelayed(rRunnable, 1000);

            }
        });
    }

    private class ExpenseSpinnerStatusAsync extends AsyncTask<String, String, String> {
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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetRequestStatus";

                System.out.println("ATTENDANCE_URL===="+GETREQUESTSTATUS_URL);
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
                try{
                    HashMap<String, String> requestStatusmap;
                    // ArrayList<String> requestArray;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    //requestArray=new ArrayList<String>();
                    if (jsonArray.length() > 0){
                        for(int i=0;i<jsonArray.length();i++){
                            requestStatusmap=new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            String key=explrObject.getString("KEY");
                            String value=explrObject.getString("VALUE");
                            // requestArray.add(value);
                            requestStatusmap.put("KEY", key);
                            requestStatusmap.put("VALUE", value);

                            arlRequestStatusData.add(requestStatusmap);
                        }
                        System.out.println("Array===" + arlRequestStatusData);

//                        customspinnerAdapter=new CustomSpinnerAdapter(getActivity(),arlRequestStatusData);
//                        spinner_status.setAdapter(customspinnerAdapter);

                    }else{
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

    public void getExpenseDetailStatus(String fromDate,String toDate,String key){
        try {
            if(Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<HashMap<String, String>>();
                final ProgressDialog pDialog;
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetExpenseRequestStatus";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("EMPLOYEE_ID", empoyeeId);
                params_final.put("FROM_DATE", fromDate);
                params_final.put("TO_DATE", toDate);
                params_final.put("REQUEST_STATUS", key);

                pm.put("objExpenseRequestStatusInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                pDialog.dismiss();

                                try {
                                    HashMap<String, String> mapData;
                                    JSONArray jsonArray = response.getJSONArray("GetExpenseRequestStatusResult");
//
                                    if(jsonArray.length()>0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            mapData = new HashMap<String, String>();
                                            JSONObject explrObject = jsonArray.getJSONObject(i);

                                            String TOKEN_NO = explrObject.getString("TOKEN_NO");
                                            String ERFS_REQUEST_ID = explrObject.getString("ERFS_REQUEST_ID");
                                            String REQUEST_STATUS_ID = explrObject.getString("REQUEST_STATUS_ID");
                                            String ER_EMPLOYEE_ID = explrObject.getString("ER_EMPLOYEE_ID");
                                            String EMPLOYEE_CODE = explrObject.getString("EMPLOYEE_CODE");
                                            String EMPLOYEE_NAME = explrObject.getString("EMPLOYEE_NAME");
                                            String B_BRANCH_NAME = explrObject.getString("B_BRANCH_NAME");
                                            String D_DEPARTMENT_NAME = explrObject.getString("D_DEPARTMENT_NAME");
                                            String D_DESIGNATION = explrObject.getString("D_DESIGNATION");
                                            String ER_REMARKS = explrObject.getString("ER_REMARKS");
                                            String ER_REQUESTED_AMOUNT = explrObject.getString("ER_REQUESTED_AMOUNT");
                                            String ER_APPROVED_AMOUNT = explrObject.getString("ER_APPROVED_AMOUNT");
                                            String REQUESTED_BY = explrObject.getString("REQUESTED_BY");
                                            String REQUEST_STATUS = explrObject.getString("REQUEST_STATUS");
                                            String REQUESTED_DATE = explrObject.getString("REQUESTED_DATE");
                                            String ACTION_BY = explrObject.getString("ACTION_BY");
                                            String ACTION_DATE = explrObject.getString("ACTION_DATE");
                                            String CANCEL_REASON = explrObject.getString("CANCEL_REASON");
                                            String CANCEL_REQUEST_BY = explrObject.getString("CANCEL_REQUEST_BY");
                                            String CANCEL_REQUEST_DATE = explrObject.getString("CANCEL_REQUEST_DATE");
                                            String CANCEL_ACTION_BY = explrObject.getString("CANCEL_ACTION_BY");
                                            String CANCEL_ACTION_DATE = explrObject.getString("CANCEL_ACTION_DATE");
                                            String MODIFIED_BY = explrObject.getString("MODIFIED_BY");
                                            String MODIFIED_DATE = explrObject.getString("MODIFIED_DATE");
                                            String EXPENSE_STATUS = explrObject.getString("EXPENSE_STATUS");
                                            String EXPENSE_STATUS_1 = explrObject.getString("EXPENSE_STATUS_1");

                                            mapData.put("TOKEN_NO", TOKEN_NO);
                                            mapData.put("ERFS_REQUEST_ID", ERFS_REQUEST_ID);
                                            mapData.put("REQUEST_STATUS_ID", REQUEST_STATUS_ID);
                                            mapData.put("ER_EMPLOYEE_ID", ER_EMPLOYEE_ID);
                                            mapData.put("EMPLOYEE_CODE", EMPLOYEE_CODE);
                                            mapData.put("EMPLOYEE_NAME", EMPLOYEE_NAME);
                                            mapData.put("B_BRANCH_NAME", B_BRANCH_NAME);
                                            mapData.put("D_DEPARTMENT_NAME", D_DEPARTMENT_NAME);
                                            mapData.put("D_DESIGNATION", D_DESIGNATION);
                                            mapData.put("ER_REMARKS", ER_REMARKS);
                                            mapData.put("ER_REQUESTED_AMOUNT", ER_REQUESTED_AMOUNT);
                                            mapData.put("ER_APPROVED_AMOUNT", ER_APPROVED_AMOUNT);
                                            mapData.put("REQUESTED_BY", REQUESTED_BY);
                                            mapData.put("REQUEST_STATUS", REQUEST_STATUS);
                                            mapData.put("REQUESTED_DATE", REQUESTED_DATE);
                                            mapData.put("ACTION_BY", ACTION_BY);
                                            mapData.put("ACTION_DATE", ACTION_DATE);
                                            mapData.put("CANCEL_REASON", CANCEL_REASON);
                                            mapData.put("CANCEL_REQUEST_BY", CANCEL_REQUEST_BY);
                                            mapData.put("CANCEL_REQUEST_DATE", CANCEL_REQUEST_DATE);
                                            mapData.put("CANCEL_ACTION_BY", CANCEL_ACTION_BY);
                                            mapData.put("CANCEL_ACTION_DATE", CANCEL_ACTION_DATE);
                                            mapData.put("MODIFIED_BY", MODIFIED_BY);
                                            mapData.put("MODIFIED_DATE", MODIFIED_DATE);
                                            mapData.put("EXPENSE_STATUS", EXPENSE_STATUS);
                                            mapData.put("EXPENSE_STATUS_1", EXPENSE_STATUS_1);

                                            arlData.add(mapData);
                                        }
                                        recyclerView.setVisibility(View.VISIBLE);
                                        txtDataNotFound.setVisibility(View.GONE);
                                        Log.e("Conveyance Status", arlData.toString());
                                        System.out.println("Conveyance Status==" + arlData);
                                        mAdapter1 = new StatusExpenseAdapter(getActivity(), arlData);
                                        recyclerView.setAdapter(mAdapter1);
                                    } else {
                                        recyclerView.setAdapter(null);
                                        Utilities.showDialog(coordinatorLayout,ErrorConstants.DATA_ERROR);
                                        recyclerView.setVisibility(View.GONE);
                                        txtDataNotFound.setVisibility(View.VISIBLE);
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

            } else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendPullbackExpenseRequest(String empId,String ReqId){
        try{
             if(Utilities.isNetworkAvailable(getActivity())) {
                 String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackExpenseRequest";
                 final JSONObject pm = new JSONObject();
                 JSONObject params_final = new JSONObject();

                 params_final.put("EMPLOYEE_ID", empId);
                 params_final.put("REQUEST_ID", ReqId);

                 pm.put("objEmployeeRequestInfo", params_final);

                 RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                 JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                         new Response.Listener<JSONObject>() {
                             @Override
                             public void onResponse(JSONObject response) {
                                 int len = (String.valueOf(response)).length();

                                 System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                 Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                 try {
                                     HashMap<String, String> mapData;
                                     String result = response.getString("PullBackExpenseRequestResult");

                                     int res = Integer.valueOf(result);
                                     String keyid;
                                     if (spinArrayID.size() == 0) {
                                         keyid = "0,1,6,7";
                                     } else {
                                         keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                                     }
                                     if (res > 0) {
                                         Utilities.showDialog(coordinatorLayout, "Expense request pull-back successfully.");

                                         getExpenseDetailStatus("-", "-", keyid);
                                     } else if (res == 0) {
                                         Utilities.showDialog(coordinatorLayout, "Error during pull-back of Expense Request.");
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

             } else{
                 Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
             }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendExpenseCancelStatus(String empId, String ReqId, String comment, final String expense){
        try{
            String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/SendCancellationExpenseRequest";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", empId);
            params_final.put("COMMENT", comment);
            params_final.put("REQUEST_ID", ReqId);
            params_final.put("TYPE", expense);

            pm.put("objSendCancellationRequestInfo", params_final);
            if(Utilities.isNetworkAvailable(getActivity())) {

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    HashMap<String, String> mapData;
                                    String result = response.getString("SendCancellationExpenseRequestResult");

                                    int res = Integer.valueOf(result);
                                    String keyid;
                                    if (spinArrayID.size() == 0) {
                                        keyid = "0,1,6,7";
                                    } else {
                                        keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                                    }
                                    if (res == 1) {
                                        if (expense.toUpperCase().equals("EXPENSE")) {
                                            Utilities.showDialog(coordinatorLayout, "Expense Cancellation request send sucessfully.");
                                            getExpenseDetailStatus("-", "-", keyid);
                                        } else {
                                            Utilities.showDialog(coordinatorLayout, "Expense Voucher Cancellation request send sucessfully.");
                                        }

                                    } else if (res == -1) {
                                        Utilities.showDialog(coordinatorLayout, "Request Flow Plan is not available.");
                                    } else if (res == 0) {
                                        Utilities.showDialog(coordinatorLayout, "Some error occur on processing the request.");
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
            } else{
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ExpenseStatusFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ExpenseStatusFragment.ClickListener clickListener) {
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

    public void GetCompareDateResult(String fromDate, String toDate){
        Constants.COMPARE_DATE_API = true;
        Log.e("Compare Method",""+Constants.COMPARE_DATE_API);

        String From_Date = fromDate;
        String To_Date = toDate;

        try {
            if(Utilities.isNetworkAvailable(getActivity())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                final JSONObject pm = new JSONObject();
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
            }else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
