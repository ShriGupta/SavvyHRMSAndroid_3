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
import com.savvy.hrmsnewapp.activity.SavvyHRMSApplication;
import com.savvy.hrmsnewapp.adapter.CompOffStatusListAdapter;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
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

import static android.content.Context.MODE_PRIVATE;

public class CompOffStatusFragment extends BaseFragment {

    PullBackCompOffAsync pullBackCompOffAsync;
    CompOffStatusFragment.CancelCompOffAsync cancelCompOffAsync;

    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;

    CompOffStatusFragment.CompOffStatusAsynTask compOffStatusAsynTask;
    CompOffStatusFragment.CompOffSpinnerStatusAsync compOffSpinnerStatusAsync;

    CalanderHRMS calanderHRMS;
    private int year, month, day;
    RecyclerView recyclerView;
    private boolean isDateDialogopenned;
    Button btn_fromDate,btn_toDate;
    Button btn_submit, btn_comp_spin;
    Spinner spinner_status;
    ArrayList<Integer> arrayList;
    CustomSpinnerAdapter customspinnerAdapter;
    CompOffStatusListAdapter mAdapter;
    private SavvyHRMSApplication mSavvyHrmsApp;
    // private OnFragmentInteractionListener mListener;
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    String token_no="";
    String employeeId = "",requestId = "",comp_status_1 = "";
    String token = "";
    String empoyeeId = "";
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapdata;
    boolean FLAG_MY = false;
    CustomTextView txt_corToken_no,txt_actionValuePull,txt_actionValueCancel;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

//    String spin_value="";
//    String spin_id="";
    StringBuilder spin_value=null;
    CustomTextView txtDataNotFound;

    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    LinearLayout linear_result_compareDate;
    CustomTextView txt_result_compareDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinArray = new ArrayList();
        spinArrayID = new ArrayList();

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        mSavvyHrmsApp = SavvyHRMSApplication.getInstance();
        arlData = new ArrayList<>();
        arrayList = new ArrayList<>();

        getStatusList();
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
//        getCompOffRequestStatus(empoyeeId,token,"-","-","0,1,6,7");
        GetCompOffStatusData(empoyeeId,"-","-","0,1,6,7");
    }

    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequestStatusData = new ArrayList<HashMap<String, String>>();
                compOffSpinnerStatusAsync = new CompOffStatusFragment.CompOffSpinnerStatusAsync();
                // requestStatusasynctask.empid=empid;
                compOffSpinnerStatusAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCompOffRequestStatus(String empid,String token,String fromdate,String todate,String spinId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arrayList = new ArrayList<>();
                arlData = new ArrayList<HashMap<String, String>>();
                compOffStatusAsynTask = new CompOffStatusFragment.CompOffStatusAsynTask();
                compOffStatusAsynTask.empid = empid;
                compOffStatusAsynTask.fromdate = fromdate;
                compOffStatusAsynTask.todate = todate;
                compOffStatusAsynTask.spnid = spinId;
                compOffStatusAsynTask.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comp_off_status, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        calanderHRMS = new CalanderHRMS(getActivity());

        btn_fromDate= getActivity().findViewById(R.id.btn_compFromDate);
        btn_toDate= getActivity().findViewById(R.id.btn_to_compDate);
        btn_submit= getActivity().findViewById(R.id.btn_compOffStatus_submit);
        btn_comp_spin= getActivity().findViewById(R.id.btn_spin_comp_status);

        linear_result_compareDate= getActivity().findViewById(R.id.Linear_result_compareDate);
        txt_result_compareDate= getActivity().findViewById(R.id.result_compareDate);

        linear_result_compareDate.setVisibility(View.GONE);

        if(spinArrayID.size()==0){
            btn_comp_spin.setText("Pending,Inprocess");
        }

        recyclerView        = getActivity().findViewById(R.id.compOffRecyclerStatus);
        mAdapter = new CompOffStatusListAdapter(getActivity(),coordinatorLayout,arlData,arrayList);

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btn_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fromDate.setText("");
                calanderHRMS.datePicker(btn_fromDate);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String FromDate = btn_fromDate.getText().toString().trim();
                            String ToDate = btn_toDate.getText().toString().trim();

                            if(!FromDate.equals("")&&(!ToDate.equals(""))){
                                getCompareDate(FromDate,ToDate);
                            } else{
                                handler.postDelayed(runnable,1000);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(runnable,1000);
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
                        try{
                            String FromDate = btn_fromDate.getText().toString().trim();
                            String ToDate = btn_toDate.getText().toString().trim();

                            if(!FromDate.equals("")&&(!ToDate.equals(""))){
                                getCompareDate(FromDate,ToDate);
                            } else{
                                handler1.postDelayed(runnable1,1000);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                handler1.postDelayed(runnable1,1000);
            }
        });

        btn_comp_spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FLAG_MY){

                }
                final CheckBox[] checkBox = new CheckBox[arlRequestStatusData.size()];
                final TextView[] txtView = new TextView[arlRequestStatusData.size()];
                TextView txt1 = new TextView(getActivity());
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
                            checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {

                                        String spin_new_value = checkBox[finalI].getText().toString();
                                        String spin_new_id = txtView[finalI].getText().toString();

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
                        btn_comp_spin.setText(spinArray.toString().replace("[","").replace("]",""));
                        FLAG_MY = true;

                    }
                });
                builder.show();

            }
        });

        recyclerView.addOnItemTouchListener(new CompOffStatusFragment.RecyclerTouchListener(getContext(), recyclerView, new CompOffStatusFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_actionValuePull = view.findViewById(R.id.txt_cor_pullback);
                    txt_actionValueCancel = view.findViewById(R.id.txt_cor_cancel);
                    txt_corToken_no = view.findViewById(R.id.cor_Token_value);

                    //Toast.makeText(getActivity(),"Press Again",Toast.LENGTH_LONG).show();
                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            token_no = txt_corToken_no.getText().toString();

                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {
                                String request_Id = mapdata.get("ERFS_REQUEST_ID");
                                String comp_status_1_Id = mapdata.get("COMP_OFF_STATUS_1");

                                requestId = request_Id;
                                comp_status_1 = comp_status_1_Id;

                                Log.e("Employee Data", "" + employeeId);
                                Log.e("Request Id", "" + requestId);
                                Log.e("Punch Status", "" + comp_status_1);
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

                            edt_comment_dialog.setText("Are you sure, Do you want to pullback Comp Off Request.");
                            txt_header.setText("Pull Back Punch Request");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getPullBackCompOffStatus(employeeId, requestId, comp_status_1);
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
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        builder.setTitle("PullBack");
//                        builder.setCancelable(false);
//                        builder.setMessage("Are you sure, Do you want to pullback Punch Request.");
//
//                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                getPullBackCompOffStatus(employeeId, requestId, comp_status_1);
//
//                            }
//                        });
//
//                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        builder.show();
                        }
                    });
                    txt_actionValueCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            token_no = txt_corToken_no.getText().toString();

                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {
                                String request_Id = mapdata.get("REQUEST_STATUS_ID");
                                String comp_status_1_Id = mapdata.get("COMP_OFF_STATUS_1");

                                requestId = request_Id;
                                comp_status_1 = comp_status_1_Id;

                                Log.e("Employee Data", "" + employeeId);
                                Log.e("Request Id", "" + requestId);
                                Log.e("Punch Status", "" + comp_status_1);
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
                            txt_header.setText("Cancel Comp Off Request");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String comment = edt_approve_comment_Cancel.getText().toString().replace(" ", "_");
                                    if (comment.equals("")) {
                                        Utilities.showDialog(coordinatorLayout, "Please Enter Comment.");
                                    } else {
                                        getCancelCompOffRequest(employeeId, comment, requestId);
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
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        builder.setTitle("Cancel");
//                        builder.setCancelable(false);
//                        builder.setMessage("Are you sure, Do you want to Cancel Punch Request.");
//
//                        final TextView txt_comment = new TextView(getActivity());
//                        txt_comment.setText("    Comment :   ");
//                        final EditText edit_comment = new EditText(getActivity());
//                        edit_comment.setEms(10);
//                        edit_comment.setWidth(400);
//                        edit_comment.setSingleLine();
//
//                        LinearLayout ll =new LinearLayout(getActivity());
//                        ll.setOrientation(LinearLayout.HORIZONTAL);
//                        ll.addView(txt_comment);
//                        ll.addView(edit_comment);
//                        builder.setView(ll);
//
//                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                try {
////                                    String comment = edit_comment.getText().toString().replace(" ", "_");
////                                    getCancelCompOffRequest(employeeId, comment, requestId);
//                                }catch(Exception e){
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        });
//
//                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        builder.show();
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


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                token = (shared.getString("Token", ""));
                empoyeeId = (shared.getString("EmpoyeeId", ""));

//                int pos=   spinner_status.getSelectedItemPosition();
//                HashMap<String,String> mapkeyvalue=arlRequestStatusData.get(pos);
//                String keyid=mapkeyvalue.get("KEY");
//                System.out.println("pos     "+ pos +"    keyid    "+   keyid);

                String keyid = spinArrayID.toString().replace("[","").replace("]","").replace(" ","");
                if(keyid.equals("")) {
                    keyid = "0,1,2,3,6";
                }

                String gettodate=   btn_toDate.getText().toString().replace("/","-");
                String getfromdate=    btn_fromDate.getText().toString().replace("/","-");
                System.out.print("Date ="+getfromdate+"To Date ="+gettodate+"Key Value = "+keyid);

                if(getfromdate=="")
                    getfromdate="-";

                if(gettodate=="")
                    gettodate="-";

                if(linear_result_compareDate.getVisibility()==View.VISIBLE){
                    Utilities.showDialog(coordinatorLayout,"From Date Should Be Less Than or equal to To Date");
                } else {
//                getCompOffRequestStatus(empoyeeId,token,getfromdate,gettodate,keyid);
                    GetCompOffStatusData(empoyeeId, getfromdate, gettodate, keyid);
                }
            }
        });

    }

    public void getPullBackCompOffStatus(String employeeId,String requestId,String punch_status_1) {
        try {
            if(Utilities.isNetworkAvailable(getActivity())) {
                pullBackCompOffAsync = new CompOffStatusFragment.PullBackCompOffAsync();
                pullBackCompOffAsync.employeeId = employeeId;
                pullBackCompOffAsync.requestId = requestId;
                pullBackCompOffAsync.punch_status_1 = punch_status_1;
                pullBackCompOffAsync.execute();
            }else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getCancelCompOffRequest(String employeeId,String comment, String requestId) {
        try {
            if(Utilities.isNetworkAvailable(getActivity())) {
                cancelCompOffAsync=new CompOffStatusFragment.CancelCompOffAsync();
                cancelCompOffAsync.employeeId = employeeId;
                cancelCompOffAsync.comment = comment;
                cancelCompOffAsync.requestId = requestId;
                cancelCompOffAsync.execute();
            }else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
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
        private CompOffStatusFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CompOffStatusFragment.ClickListener clickListener) {
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

    private class PullBackCompOffAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId,requestId,punch_status_1;
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

            System.out.print("\nEmployee Id="+employeeId+" Request ID = "+requestId+"\n Punch Status = "+punch_status_1);
            try {

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/PullBackCompOffRequest"+"/"+employeeId+"/"+requestId;

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

                result = result.replaceAll("^\"+|\"+$", " ").trim();
                try {

                    int res = Integer.valueOf(result);
                    String keyid;
                    if(spinArrayID.size()==0){
                        keyid = "0,1,2,3";
                    }
                    else{
                        keyid = spinArrayID.toString().replace("[","").replace("]","").replace(" ","");
                    }

                    if (res > 0) {
                        Utilities.showDialog(coordinatorLayout, "Comp Off request pull-back successfully.");
//                        getCompOffRequestStatus(empoyeeId,token,"-","-",keyid);
                        GetCompOffStatusData(employeeId,"-","-",keyid);
                    } else if (res == 0) {
                        Utilities.showDialog(coordinatorLayout, "Error during pull-back of Comp Off Request.");
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }

//                if(punch_status_1.equals("0"))
//                {
//                    txt_actionValueCancel.setVisibility(View.INVISIBLE);
//                    txt_actionValuePull.setVisibility(View.VISIBLE);
//                }else if(punch_status_1.equals("2"))
//                {
//                    txt_actionValueCancel.setVisibility(View.VISIBLE);
//                    txt_actionValuePull.setVisibility(View.INVISIBLE);
//                }
//                else{
//                    txt_actionValueCancel.setVisibility(View.INVISIBLE);
//                    txt_actionValuePull.setVisibility(View.INVISIBLE);
//                }

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

    private class CancelCompOffAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId,requestId,comment;
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

            System.out.print("\nEmployee Id="+employeeId+" Request ID = "+requestId+"\n Punch Status = "+comment);
            try {

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETREQUESTSTATUS_URL =Constants.IP_ADDRESS+"/SavvyMobileService.svc/SendPunchCancellationRequest"+"/"+employeeId+"/"+comment+"/"+requestId;

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
                try {

                    String keyid;
                    if(spinArrayID.size()==0){
                        keyid = "0,1,2,3";
                    }
                    else{
                        keyid = spinArrayID.toString().replace("[","").replace("]","").replace(" ","");
                    }
                    GetCompOffStatusData(employeeId,"-","-",keyid);
//                    getCompOffRequestStatus(empoyeeId, token, "-", "-", keyid);
                    Utilities.showDialog(coordinatorLayout, "Punch Request Cancel Successfully.");
                }catch (Exception e){
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

    private class CompOffSpinnerStatusAsync extends AsyncTask<String, String, String> {
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

                        customspinnerAdapter=new CustomSpinnerAdapter(getActivity(),arlRequestStatusData);
                        //spinner_status.setAdapter(customspinnerAdapter);

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


    private class CompOffStatusAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid,fromdate,todate,spnid;


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

            Log.e("From Date ",""+fromdate);
            Log.e("To Date ",""+todate);
            Log.e("Spinner Value ",""+spnid);
            try {
                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String ATTENDANCE_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetCompOffRequestStatus/"+empid+"/"+fromdate+"/"+todate+"/"+spnid;

                System.out.println("ATTENDANCE_URL===="+ATTENDANCE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        ATTENDANCE_URL, "GET");

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
                    HashMap<String, String> punchStatusmap;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    if (jsonArray.length() > 0){
                        for(int i=0;i<jsonArray.length();i++){
                            punchStatusmap=new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);

                            String tokenNo=explrObject.getString("TOKEN_NO");                         //6
                            String compOffDate=explrObject.getString("COMP_OFF_DATE");                     //7

                            String corTimeIn=explrObject.getString("TIME_IN");                         //2

                            String corTimeOut=explrObject.getString("TIME_OUT");                         //4
                            String corReason=explrObject.getString("REASON");                         //5
                            String corRequestedDays=explrObject.getString("COR_REQUEST_DAYS");                           //1
                            String corApprovedDays=explrObject.getString("COR_APPROVE_DAYS");
                            String corStatus=explrObject.getString("REQUEST_STATUS");

                            String employeeId=explrObject.getString("COR_EMPLOYEE_ID");
                            String requestId=explrObject.getString("ERFS_REQUEST_ID");
                            String cor_status = explrObject.getString("COMP_OFF_STATUS_1");

                            int token_no = Integer.parseInt(explrObject.getString("TOKEN_NO"));
                            arrayList.add(token_no);

                            punchStatusmap.put("TOKEN_NO", tokenNo);
                            punchStatusmap.put("COMP_OFF_DATE", compOffDate);
                            punchStatusmap.put("TIME_IN", corTimeIn);
                            punchStatusmap.put("TIME_OUT", corTimeOut);
                            punchStatusmap.put("REASON", corReason);
                            punchStatusmap.put("COR_REQUEST_DAYS", corRequestedDays);
                            punchStatusmap.put("COR_APPROVE_DAYS", corApprovedDays);
                            punchStatusmap.put("REQUEST_STATUS", corStatus);

                            punchStatusmap.put("COR_EMPLOYEE_ID",employeeId);
                            punchStatusmap.put("ERFS_REQUEST_ID",requestId);
                            punchStatusmap.put("COMP_OFF_STATUS_1",cor_status);

                            arlData.add(punchStatusmap);


                        }
                        System.out.println("Array===" + arlData);
                        Collections.sort(arrayList,Collections.<Integer>reverseOrder());
                        System.out.println("ArrayList===" + arrayList);

                        recyclerView.setVisibility(View.VISIBLE);
                        txtDataNotFound.setVisibility(View.GONE);
                        //DisplayHolidayList(arlData);
                        mAdapter = new CompOffStatusListAdapter(getActivity(), coordinatorLayout,arlData,arrayList);
                        recyclerView.setAdapter(mAdapter);
                    }else{
                        recyclerView.setAdapter(null);
                        Utilities.showDialog(coordinatorLayout,ErrorConstants.DATA_ERROR);
                        recyclerView.setVisibility(View.GONE);
                        txtDataNotFound.setVisibility(View.VISIBLE);
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

    public void GetCompOffStatusData(String emp_id, String fromDate, String toDate,String keyValue){
        try{
            if(Utilities.isNetworkAvailable(getActivity())){
                JSONObject param = new JSONObject();
                param.put("EMPLOYEE_ID",emp_id);
                param.put("FROM_DATE",fromDate);
                param.put("TO_DATE",toDate);
                param.put("REQUEST_STATUS",keyValue);

                Log.e("RequestData","Request Json - >"+param.toString());

                String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetCompOffRequestStatusPost";
                Log.e("Url",url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    arlData.clear();
                                    arrayList.clear();
                                    HashMap<String, String> compOffStatus;
                                    JSONArray jsonArray = response.getJSONArray("GetCompOffRequestStatusPostResult");
                                    for(int i=0;i<jsonArray.length();i++){
                                        compOffStatus = new HashMap<>();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String tokenNo = jsonObject.getString("TOKEN_NO");                         //6
                                        String compOffDate = jsonObject.getString("COMP_OFF_DATE");                     //7

                                        String corTimeIn = jsonObject.getString("TIME_IN");                         //2

                                        String corTimeOut = jsonObject.getString("TIME_OUT");                         //4
                                        String corReason = jsonObject.getString("REASON");                         //5
                                        String corRequestedDays = jsonObject.getString("COR_REQUEST_DAYS");                           //1
                                        String corApprovedDays = jsonObject.getString("COR_APPROVE_DAYS");
                                        String corStatus = jsonObject.getString("REQUEST_STATUS");

                                        String employeeId = jsonObject.getString("COR_EMPLOYEE_ID");
                                        String requestId = jsonObject.getString("ERFS_REQUEST_ID");
                                        String cor_status = jsonObject.getString("COMP_OFF_STATUS_1");

                                        int token_no = Integer.parseInt(jsonObject.getString("TOKEN_NO"));
                                        arrayList.add(token_no);

                                        compOffStatus.put("TOKEN_NO", tokenNo);
                                        compOffStatus.put("COMP_OFF_DATE", compOffDate);
                                        compOffStatus.put("TIME_IN", corTimeIn);
                                        compOffStatus.put("TIME_OUT", corTimeOut);
                                        compOffStatus.put("REASON", corReason);
                                        compOffStatus.put("COR_REQUEST_DAYS", corRequestedDays);
                                        compOffStatus.put("COR_APPROVE_DAYS", corApprovedDays);
                                        compOffStatus.put("REQUEST_STATUS", corStatus);

                                        compOffStatus.put("COR_EMPLOYEE_ID",employeeId);
                                        compOffStatus.put("ERFS_REQUEST_ID",requestId);
                                        compOffStatus.put("COMP_OFF_STATUS_1",cor_status);

                                        arlData.add(compOffStatus);
                                    }

                                    if(jsonArray.length()>0){
                                        recyclerView.setVisibility(View.VISIBLE);
                                        txtDataNotFound.setVisibility(View.GONE);
                                        //DisplayHolidayList(arlData);
                                        mAdapter = new CompOffStatusListAdapter(getActivity(), coordinatorLayout,arlData,arrayList);
                                        recyclerView.setAdapter(mAdapter);
                                    } else{
                                        recyclerView.setAdapter(null);
                                        Utilities.showDialog(coordinatorLayout,ErrorConstants.DATA_ERROR);
                                        recyclerView.setVisibility(View.GONE);
                                        txtDataNotFound.setVisibility(View.VISIBLE);
                                    }
                                }catch (Exception e){
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
                RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(retryPolicy);
                requestQueue.add(jsonObjectRequest);
            } else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCompareDate(String fromDate, String toDate){
        try{
            if(Utilities.isNetworkAvailable(getActivity())){
                JSONObject param = new JSONObject();

                param.put("From_Date",fromDate);
                param.put("To_Date",toDate);
                Log.e("RequestData","Json->"+param.toString());

                String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/Compare_Date";
                Log.e("Url",url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,param,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response){
                            try{
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
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){

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
                RetryPolicy policy = new DefaultRetryPolicy(socketTime,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonRequest.setRetryPolicy(policy);
                requestQueue.add(jsonRequest);
            } else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
