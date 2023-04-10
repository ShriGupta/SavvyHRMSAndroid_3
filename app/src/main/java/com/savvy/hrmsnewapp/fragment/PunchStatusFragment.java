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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.SavvyHRMSApplication;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.PunchStatusListAdapter;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by orapc7 on 5/12/2017.
 */

public class PunchStatusFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemSelectedListener
{

    //PunchStatusFragment.RequestStatusAsynTask requestStatusasynctask;

    PunchStatusFragment.PullBackAsync pullBackAsync;
    PunchStatusFragment.CancelPunchAsync cancelPunchAsync;
    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;
    PunchStatusFragment.PunchStatusAsynTask punchStatusasynctask;

    PunchStatusFragment.PunchSpinnerStatusAsync punchSpinnerStatusAsync;

    CalanderHRMS calanderHRMS;
    private int year, month, day;
    RecyclerView recyclerView;
    private boolean isDateDialogopenned;
    Button btn_fromDate,btn_toDate;
    Button btn_submit ,btn_spin_punch;
    Spinner spinner_status;
    CustomSpinnerAdapter customspinnerAdapter;
    PunchStatusListAdapter mAdapter;
//    TeamMembersDemoAdapter mAdapter;
    private SavvyHRMSApplication mSavvyHrmsApp;
    // private OnFragmentInteractionListener mListener;
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    String token_no = "";
    String employeeId = "",requestId = "",punch_status_1 = "";
    String token;
    ArrayList<Integer> arrayList;

    String json = "";

    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapdata;
    CustomTextView txt_punchToken_no,txt_actionValuePull,txt_actionValueCancel, txt_TestData;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    Handler handler;
    Runnable rRunnable;

    String FROM_DATE = "", TO_DATE = "";

    LinearLayout linear_result_compareDate;
    CustomTextView txt_result_compareDate, txtDataNotFound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinArray = new ArrayList();
        spinArrayID = new ArrayList();
        try {
            shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        mSavvyHrmsApp = SavvyHRMSApplication.getInstance();

            getStatusList();
            token = (shared.getString("Token", ""));
            employeeId = (shared.getString("EmpoyeeId", ""));
            getPunchRequestStatus(employeeId, token, "-", "-", "0,1,6,7");
        }catch (Exception e){
            e.printStackTrace();
        }

//                Thread.setDefaultUncaughtExceptionHandler(
//                    new Thread.UncaughtExceptionHandler() {
//
//                    @Override
//                    public void uncaughtException(Thread thread, Throwable ex) {
//                        Log.e("Error", "Unhandled exception: " + ex.getMessage());
//                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
//                        System.exit(1);
//                    }
//                });
    }

    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequestStatusData = new ArrayList<HashMap<String, String>>();
                punchSpinnerStatusAsync = new PunchStatusFragment.PunchSpinnerStatusAsync();
                // requestStatusasynctask.empid=empid;
                punchSpinnerStatusAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getPunchRequestStatus(String empid,String token,String fromdate,String todate,String spinId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arrayList = new ArrayList<>();
                arlData = new ArrayList<HashMap<String, String>>();
                punchStatusasynctask = new PunchStatusFragment.PunchStatusAsynTask();
                punchStatusasynctask.empid = empid;
                punchStatusasynctask.fromdate = fromdate;
                punchStatusasynctask.todate = todate;
                punchStatusasynctask.spnid = spinId;
                punchStatusasynctask.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_punch_status,container,false);

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        calanderHRMS = new CalanderHRMS(getActivity());

        btn_fromDate= getActivity().findViewById(R.id.btn_punchdate_status);
        btn_toDate= getActivity().findViewById(R.id.btn_to_punchdate);
        btn_submit= getActivity().findViewById(R.id.btn_puchStatus_submit);
        btn_spin_punch= getActivity().findViewById(R.id.btn_spin_punch_status);


        linear_result_compareDate= getActivity().findViewById(R.id.Linear_result_compareDate);
        txt_result_compareDate= getActivity().findViewById(R.id.result_compareDate);

        linear_result_compareDate.setVisibility(View.GONE);


        recyclerView        = getActivity().findViewById(R.id.punchRecyclerStatus);
        mAdapter = new PunchStatusListAdapter(getActivity(),coordinatorLayout,arlData,arrayList);
//        mAdapter = new TeamMembersDemoAdapter(getActivity(), arlData);


        btn_submit.setOnClickListener(this);
        btn_spin_punch.setOnClickListener(this);

        if(spinArrayID.size()==0){
            btn_spin_punch.setText("Pending,Inprocess");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.addOnItemTouchListener(new PunchStatusFragment.RecyclerTouchListener(getContext(), recyclerView, new PunchStatusFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_actionValuePull = view.findViewById(R.id.txt_pullback);
                    txt_actionValueCancel = view.findViewById(R.id.txt_cancel);
                    txt_punchToken_no = view.findViewById(R.id.txt_puchToken_value);
                    txt_TestData = view.findViewById(R.id.txt_puchDate_value);

                    //Toast.makeText(getActivity(),"Press Again",Toast.LENGTH_LONG).show();
//
//                    txt_TestData.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            token_no = txt_punchToken_no.getText().toString();
//                            DialogType dialogType = new DialogType(getActivity());
//                            dialogType.myDialog(token_no);
//                        }
//                    });

                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = txt_punchToken_no.getText().toString();

                                String str = mapdata.get("TOKEN_NO");


                                if (token_no.equals(str)) {

                                    String request_Id = mapdata.get("ERFS_REQUEST_ID");
                                    String punch_status_1_Id = mapdata.get("PUNCH_STATUS_1");

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

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback Punch Request.");
                                txt_header.setText("Pull Back Punch Request");

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

//                                String PullBackTitle = "<font color=\"#5cb85c\"><bold>" + "PullBack Punch Request" + "</bold></font>";
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                builder.setTitle(Html.fromHtml(PullBackTitle));
//                                builder.setCancelable(false);
//                                builder.setMessage("Are you sure, Do you want to pullback Punch Request.");
//
//                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        getPullBackStatus(employeeId, requestId, punch_status_1);
//                                        getPunchRequestStatus(empoyeeId, token, "-", "-", "0,1,6,7");
//
//                                    }
//                                });
//
//                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                    }
//                                });
//                                builder.show();
                            }catch (Exception e){
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
                                    String punch_status_1_Id = mapdata.get("PUNCH_STATUS_1");

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

                                //                                edt_comment_dialog.setText("Are you sure, Do you want to pullback OD Request.");
                                txt_header.setText("Cancel Punch Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String comment = edt_approve_comment_Cancel.getText().toString().replace(" ", "_");
                                        if(comment.equals("")){
                                            Utilities.showDialog(coordinatorLayout,"Please Enter Comment.");
                                        } else {
                                            getCancelPunchRequest(employeeId, comment, requestId);
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
//                                String CancelTitle = "<font color=\"#E72A02\"><bold>" + "Cancel Punch Request" + "</bold></font>";
//
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                builder.setTitle(Html.fromHtml(CancelTitle));
//                                builder.setCancelable(false);
//                                builder.setMessage("Are you sure, Do you want to Cancel Punch Request.");
//
//                                final TextView txt_comment = new TextView(getActivity());
//                                txt_comment.setText("    Comment :   ");
//                                final EditText edit_comment = new EditText(getActivity());
//                                edit_comment.setEms(10);
//                                edit_comment.setWidth(400);
//                                edit_comment.setSingleLine();
//
//                                LinearLayout ll = new LinearLayout(getActivity());
//                                ll.setOrientation(LinearLayout.HORIZONTAL);
//                                ll.addView(txt_comment);
//                                ll.addView(edit_comment);
//                                builder.setView(ll);
//
//                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        String comment = edit_comment.getText().toString().replace(" ", "_");
//                                        getCancelPunchRequest(employeeId, comment, requestId);
//
//                                    }
//                                });
//
//                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                    }
//                                });
//                                builder.show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
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

        btn_spin_punch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final CheckBox[] checkBox = new CheckBox[arlRequestStatusData.size()];
                    final TextView[] txtView = new TextView[arlRequestStatusData.size()];
                    TextView txt1 = new TextView(getActivity());
                    LinearLayout l1 = new LinearLayout(getActivity());
                    l1.setOrientation(LinearLayout.VERTICAL);

//                getActivity().addContentView(txt1,null);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        if(spinArrayID.size()==0){
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
                            // TODO Auto-generated method stub

                            btn_spin_punch.setText(spinArray.toString().replace("[", "").replace("]", ""));

                        }
                    });
                    builder.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    token = (shared.getString("Token", ""));

                    String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    if (keyid.equals("")) {
                        keyid = "-";
                    }

                    String gettodate = btn_toDate.getText().toString().replace("/", "-");
                    String getfromdate = btn_fromDate.getText().toString().replace("/", "-");
                    System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                    if (getfromdate == "")
                        getfromdate = "-";

                    if (gettodate == "")
                        gettodate = "-";

                    if(linear_result_compareDate.getVisibility()==View.VISIBLE) {
                        Utilities.showDialog(coordinatorLayout,"From Date Should Be Less Than or equal to To Date");
                    } else{
                        getPunchRequestStatus(employeeId, token, getfromdate, gettodate, keyid);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

 }

    public void getPullBackStatus(String employeeId,String requestId,String punch_status_1) {
        try {
            if(Utilities.isNetworkAvailable(getActivity())) {
                pullBackAsync = new PunchStatusFragment.PullBackAsync();
                pullBackAsync.employeeId = employeeId;
                pullBackAsync.requestId = requestId;
                pullBackAsync.punch_status_1 = punch_status_1;
                pullBackAsync.execute();
            }else {
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCancelPunchRequest(String employeeId,String comment, String requestId) {
        try {
            if(Utilities.isNetworkAvailable(getActivity())) {
                cancelPunchAsync=new PunchStatusFragment.CancelPunchAsync();
                cancelPunchAsync.employeeId = employeeId;
                cancelPunchAsync.comment = comment;
                cancelPunchAsync.requestId = requestId;
                cancelPunchAsync.execute();
            }else {
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private class PullBackAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId,requestId,punch_status_1;
        @Override
        protected void onPreExecute() {
            try {
                // Things to be done before execution of long running operation. For
                // example showing ProgessDialog
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                System.out.print("\nEmployee Id="+employeeId+" Request ID = "+requestId+"\n Punch Status = "+punch_status_1);
                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/PullBackPunchRequest"+"/"+employeeId+"/"+requestId;

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
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();

                        result = result.replaceAll("^\"+|\"+$", " ").trim();
                        int res = Integer.valueOf(result);
                        String keyid;
                        if (spinArrayID.size() == 0) {
                            keyid = "0,1,2,3";
                        } else {
                            keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                        }

                        if (res > 0) {
                            Utilities.showDialog(coordinatorLayout, "Punch Regularization request pull-back successfully.");
                            getPunchRequestStatus(employeeId, token, "-", "-", keyid);
                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Error during pull-back of Punch Regularization Request.");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    //    Cancel Punch Request Async Task

    private class CancelPunchAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId,requestId,comment;
        @Override
        protected void onPreExecute() {
            try {
                // Things to be done before execution of long running operation. For
                // example showing ProgessDialog
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                System.out.print("\nEmployee Id="+employeeId+" Request ID = "+requestId+"\n Punch Status = "+comment);
                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/SendPunchCancellationRequest"+"/"+employeeId+"/"+comment+"/"+requestId;

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
            try {

                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        result = result.replaceAll("^\"+|\"+$", " ").trim();
                        int res = Integer.valueOf(result);

                        String keyid;
                        if (spinArrayID.size() == 0) {
                            keyid = "0,1,2,3";
                        } else {
                            keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                        }

                        if (res == 1) {
                            Utilities.showDialog(coordinatorLayout, "Punch Regularization Cancellation request send sucessfully.");
//                            getPunchRequestStatus(empoyeeId, token, "-", "-", keyid);
                            mAdapter.notifyDataSetChanged();
                        } else if (res == -1) {
                            Utilities.showDialog(coordinatorLayout, "Request Flow Plan Is Not Available.");
                        } else if (res == -2) {
                            Utilities.showDialog(coordinatorLayout, "Can Not Take Any Action On The Previous Payroll Pequests.");
                        } else if (res == 0) {
                            Utilities.showDialog(coordinatorLayout, "Some Error Occur On Processing The Request.");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }


    private class PunchSpinnerStatusAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            try {
                // Things to be done before execution of long running operation. For
                // example showing ProgessDialog
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }

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
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        HashMap<String, String> requestStatusmap;
                        // ArrayList<String> requestArray;
                        JSONArray jsonArray = new JSONArray(result);
                        System.out.println("jsonArray===" + jsonArray);
                        //requestArray=new ArrayList<String>();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                requestStatusmap = new HashMap<String, String>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String key = explrObject.getString("KEY");
                                String value = explrObject.getString("VALUE");
                                // requestArray.add(value);
                                requestStatusmap.put("KEY", key);
                                requestStatusmap.put("VALUE", value);

                                arlRequestStatusData.add(requestStatusmap);
                            }
                            System.out.println("Array===" + arlRequestStatusData);

//                        customspinnerAdapter=new CustomSpinnerAdapter(getActivity(),arlRequestStatusData);
//                        spinner_status.setAdapter(customspinnerAdapter);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            System.out.println("Data not getting on server side");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
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
        private PunchStatusFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final PunchStatusFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
//                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null && clickListener != null) {
//                        //clickListener.onLongClick(child, recyclerView.getChildPosition(child));
//                        clickListener.onClick(child, recyclerView.getChildPosition(child));
//                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onClick(child, recyclerView.getChildPosition(child));
                       // clickListener.onLongClick(child, recyclerView.getChildPosition(child));
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class PunchStatusAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid,fromdate,todate,spnid;


        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            try {
                // Things to be done before execution of long running operation. For
                // example showing ProgessDialog
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                Log.e("From Date ",""+fromdate);
                Log.e("To Date ",""+todate);
                Log.e("Spinner Value ",""+spnid);

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
               final String ATTENDANCE_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetPunchRequestStatus/"+empid+"/"+fromdate+"/"+todate+"/"+spnid;

                System.out.println("ATTENDANCE_URL===="+ATTENDANCE_URL);
//                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);

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
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        HashMap<String, String> punchStatusmap;
                        JSONArray jsonArray = new JSONArray(result);
                        System.out.println("jsonArray===" + jsonArray);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                punchStatusmap = new HashMap<String, String>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);

                                String actionBy = explrObject.getString("REQUESTED_BY");                         //6
                                String actionDate = explrObject.getString("REQUESTED_DATE");                     //7

                                String punchDate = explrObject.getString("PUNCH_DATE");                         //2

                                String psReason = explrObject.getString("REASON");                         //4
                                String psStatus = explrObject.getString("REQUEST_STATUS");                         //5
                                String tokenNo = explrObject.getString("TOKEN_NO");                           //1
                                String punchTime = explrObject.getString("PUNCH_TIME");

                                String employeeId = explrObject.getString("PR_EMPLOYEE_ID");
                                String requestId = explrObject.getString("ERFS_REQUEST_ID");
                                String punch_status = explrObject.getString("PUNCH_STATUS_1");

                                int token_no = Integer.parseInt(explrObject.getString("TOKEN_NO"));
                                arrayList.add(token_no);

                                punchStatusmap.put("TOKEN_NO", tokenNo);
                                punchStatusmap.put("PUNCH_DATE", punchDate);
                                punchStatusmap.put("PUNCH_TIME", punchTime);
                                punchStatusmap.put("REASON", psReason);
                                punchStatusmap.put("REQUEST_STATUS", psStatus);
                                punchStatusmap.put("REQUESTED_BY", actionBy);
                                punchStatusmap.put("REQUESTED_DATE", actionDate);

                                punchStatusmap.put("PR_EMPLOYEE_ID", employeeId);
                                punchStatusmap.put("ERFS_REQUEST_ID", requestId);
                                punchStatusmap.put("PUNCH_STATUS_1", punch_status);

                                arlData.add(punchStatusmap);


                            }
                            System.out.println("Array===" + arlData);
                            Collections.sort(arrayList, Collections.<Integer>reverseOrder());
                            System.out.println("ArrayList===" + arrayList);

                            //Display Holiday List(arlData);
                            recyclerView.setVisibility(View.VISIBLE);
                            txtDataNotFound.setVisibility(View.GONE);
                            mAdapter = new PunchStatusListAdapter(getActivity(), coordinatorLayout, arlData, arrayList);
                            recyclerView.setAdapter(mAdapter);

//                            mAdapter = new TeamMembersDemoAdapter(getActivity(), arlData);
//                            recyclerView.setAdapter(mAdapter);

//                            recyclerView.setAdapter(null);
//                            Utilities.showDialog(coordinatorLayout, "Punch Status Accuracy");
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
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
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
                });

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
