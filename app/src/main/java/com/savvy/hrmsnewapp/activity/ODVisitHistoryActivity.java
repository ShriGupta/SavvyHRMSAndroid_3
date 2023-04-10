package com.savvy.hrmsnewapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.ODApprovalListAdapter;
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

public class ODVisitHistoryActivity extends AppCompatActivity {

    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;
    VisitSpinnerStatusAsync visitSpinnerStatusAsync;
    ODApprovalAsyn odApprovalAsyn;
    ODApprovalActionAsyn odApprovalActionAsyn;

    CalanderHRMS calanderHRMS;
    private int year, month, day;
    RecyclerView recyclerView;
    private boolean isDateDialogopenned;
    Button btn_fromDate,btn_toDate;
    Button btn_submit, btn_spin_visit;
    Spinner spinner_status;
    CustomSpinnerAdapter customspinnerAdapter;
    ODApprovalListAdapter mAdapter;
    private SavvyHRMSApplication mSavvyHrmsApp;
    // private OnFragmentInteractionListener mListener;
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    String employeeId = "",requestId = "",punch_status_1 = "";
    String token = "";

    String empoyeeId = "";
    RecyclerView VisitRecyclerStatus;
    ArrayList<Integer> arrayList;
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapdata;

    boolean recycler_status = false;
    CoordinatorLayout coordinatorLayout;
    String xmlData = "";
    String token_no = "";
    CustomTextView od_punchToken_no;

    CustomTextView txt_punchToken_no,txt_actionValuePull,txt_actionValueCancel;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String REQUEST_ID = "",REQUEST_FLOW_STATUS_ID = "",ACTION_LEVEL_SEQUENCE = "",MAX_ACTION_LEVEL_SEQUENCE = ""
            ,R_STATUS = "",EMPLOYEE_ID = "",ERFS_REQUEST_FLOW_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_history);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.coordinatorLayout);

        shared = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        spinArray = new ArrayList();
        spinArrayID = new ArrayList();

        getStatusList();
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        getODApproval(empoyeeId);

        Init();

    }

    private void getODApproval(String empid) {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                arrayList = new ArrayList<>();
                arlData = new ArrayList<HashMap<String, String>>();
                odApprovalAsyn = new ODVisitHistoryActivity.ODApprovalAsyn();
                odApprovalAsyn.empid = empid;
                odApprovalAsyn.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void Init(){

        calanderHRMS = new CalanderHRMS(ODVisitHistoryActivity.this);
        btn_fromDate = (Button)findViewById(R.id.btn_visitFromDate);
        btn_toDate = (Button)findViewById(R.id.btn_to_visitDate);
        btn_spin_visit = (Button)findViewById(R.id.btn_spin_visit_status);
        btn_submit = (Button)findViewById(R.id.btn_visitStatus_submit);

        mAdapter = new ODApprovalListAdapter(this,coordinatorLayout,arlData,arrayList);

        VisitRecyclerStatus = (RecyclerView)findViewById(R.id.VistRecyclerStatus);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        VisitRecyclerStatus.setLayoutManager(mLayoutManager);
        VisitRecyclerStatus.setItemAnimator(new DefaultItemAnimator());

        btn_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(btn_fromDate);
            }
        });
        btn_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(btn_toDate);
            }
        });

        btn_spin_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CheckBox[] checkBox = new CheckBox[arlRequestStatusData.size()];
                final TextView[] txtView = new TextView[arlRequestStatusData.size()];
                LinearLayout l1 = new LinearLayout(ODVisitHistoryActivity.this);
                l1.setOrientation(LinearLayout.VERTICAL);

//                getActivity().addContentView(txt1,null);

                AlertDialog.Builder builder = new AlertDialog.Builder(ODVisitHistoryActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Please Select");

                Log.e("ArrayList ",arlRequestStatusData.toString());
                Log.e("ArrayList ",""+arlRequestStatusData.size());
                try {

                    for (int i = 0; i < arlRequestStatusData.size(); i++) {
                        mapdata = arlRequestStatusData.get(i);

                        checkBox[i] = new CheckBox(ODVisitHistoryActivity.this);
                        txtView[i] = new TextView(ODVisitHistoryActivity.this);

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
                }catch (Exception e){
                    e.printStackTrace();
                }

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        btn_spin_visit.setText(spinArray.toString().replace("[","").replace("]",""));


                    }
                });
                builder.show();


            }
        });


        VisitRecyclerStatus.addOnItemTouchListener(new ODVisitHistoryActivity.RecyclerTouchListener(ODVisitHistoryActivity.this, recyclerView, new ODVisitHistoryActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    CustomTextView txt_actionValue = view.findViewById(R.id.od_approve);
                    od_punchToken_no = view.findViewById(R.id.od_token_value);
                    //  Toast.makeText(getActivity(),"Press Again",Toast.LENGTH_LONG).show();
                    txt_actionValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            token_no = od_punchToken_no.getText().toString();

                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {

                                REQUEST_ID = mapdata.get("ERFS_REQUEST_ID");
                                REQUEST_FLOW_STATUS_ID = mapdata.get("REQUEST_STATUS_ID");
                                ACTION_LEVEL_SEQUENCE = mapdata.get("ERFS_ACTION_LEVEL_SEQUENCE");
                                MAX_ACTION_LEVEL_SEQUENCE = mapdata.get("MAX_ACTION_LEVEL_SEQUENCE");
                                R_STATUS = mapdata.get("OD_STATUS");
                                EMPLOYEE_ID = mapdata.get("ODR_EMPLOYEE_ID");
                                ERFS_REQUEST_FLOW_ID = mapdata.get("ERFS_REQUEST_FLOW_ID");

                                xmlData = REQUEST_ID + "," + REQUEST_FLOW_STATUS_ID + "," + ACTION_LEVEL_SEQUENCE + "," +
                                        MAX_ACTION_LEVEL_SEQUENCE + "," + R_STATUS + "," + EMPLOYEE_ID + "," + ERFS_REQUEST_FLOW_ID;

                                Log.e("XML DATA", xmlData);
                                Log.e("Token No.", str);
                            }


                            AlertDialog.Builder builder = new AlertDialog.Builder(ODVisitHistoryActivity.this);
                            builder.setTitle("  OD Approval Action ");
                            builder.setCancelable(false);

                            final TextView toggleText = new TextView(ODVisitHistoryActivity.this);
                            toggleText.setText("    Type :           ");

                            final TextView txt_comment = new TextView(ODVisitHistoryActivity.this);
                            txt_comment.setText("    Comment :   ");
                            final EditText edit_comment = new EditText(ODVisitHistoryActivity.this);
                            edit_comment.setEms(10);
                            edit_comment.setWidth(400);
                            edit_comment.setSingleLine();

                            final ToggleButton tg = new ToggleButton(ODVisitHistoryActivity.this);
                            tg.setChecked(true);
                            tg.setText("Approved");
                            tg.setTextOn("Approved");
                            tg.setTextOff("Reject");

                            LinearLayout ll1 = new LinearLayout(ODVisitHistoryActivity.this);
                            LinearLayout ll2 = new LinearLayout(ODVisitHistoryActivity.this);
                            LinearLayout ll3 = new LinearLayout(ODVisitHistoryActivity.this);

                            ll1.setOrientation(LinearLayout.HORIZONTAL);
                            ll1.addView(txt_comment);
                            ll1.addView(edit_comment);

                            ll2.setOrientation(LinearLayout.HORIZONTAL);
                            ll2.addView(toggleText);
                            ll2.addView(tg);

                            ll3.setOrientation(LinearLayout.VERTICAL);
                            ll3.addView(ll1);
                            ll3.addView(ll2);
                            builder.setView(ll3);

                            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.setNegativeButton("Go", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String tg_text;
                                    String edit_comment_text = edit_comment.getText().toString().replace(" ", "_");
                                    if (tg.isChecked()) {
                                        tg_text = "" + "true";
                                    } else {
                                        tg_text = "" + "false";
                                    }

                                    if (edit_comment_text.equals(""))
                                        edit_comment_text = "-";

                                    Log.e("XML DATA", "" + xmlData);
                                    getApproveList(empoyeeId, edit_comment_text, tg_text, xmlData);

                                }
                            });
                            builder.show();
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
                // TODO Auto-generated method stub
            }
        });
    }
    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                arlRequestStatusData = new ArrayList<HashMap<String, String>>();
                visitSpinnerStatusAsync = new ODVisitHistoryActivity.VisitSpinnerStatusAsync();
                // requestStatusasynctask.empid=empid;
                visitSpinnerStatusAsync.execute();

            } else {
                //Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void getApproveList(String empoyeeId,String edit_comment_text,String tg_text, String xmlData) {
        try {
            if(Utilities.isNetworkAvailable(this)) {
                odApprovalActionAsyn = new ODVisitHistoryActivity.ODApprovalActionAsyn();
                odApprovalActionAsyn.edit_comment_text = edit_comment_text;
                odApprovalActionAsyn.tg_text_status = tg_text;
                odApprovalActionAsyn.xmlData = xmlData;
                odApprovalActionAsyn.empoyeeId = empoyeeId;

                odApprovalActionAsyn.execute();
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
        private ODVisitHistoryActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ODVisitHistoryActivity.ClickListener clickListener) {
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


    private class VisitSpinnerStatusAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(ODVisitHistoryActivity.this);
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
                JSONParser jParser = new JSONParser(ODVisitHistoryActivity.this); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
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

    private class ODApprovalAsyn extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;

        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(ODVisitHistoryActivity.this);
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
                String ATTENDANCE_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ApprovalGetODRequestDetail/"+empid;

                System.out.println("ATTENDANCE_URL===="+ATTENDANCE_URL);
                JSONParser jParser = new JSONParser(ODVisitHistoryActivity.this); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
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

                            String tokenNo=explrObject.getString("TOKEN_NO");                           //1
                            String fromDate=explrObject.getString("FROM_DATE");                           //1
                            String toDate=explrObject.getString("TO_DATE");                           //1
                            String employee_Code=explrObject.getString("EMPLOYEE_CODE");                         //6
                            String employee_Name=explrObject.getString("EMPLOYEE_NAME");                     //7
                            String odtype=explrObject.getString("OD_TYPE_NAME");                         //2
                            String odRequestType=explrObject.getString("OD_REQUEST_TYPE_NAME");
                            String odSubRequestType=explrObject.getString("OD_REQUEST_SUB_TYPE_NAME");                         //4
                            String odApptype=explrObject.getString("R_TYPE");                         //5

                            String REQUEST_ID=explrObject.getString("ERFS_REQUEST_ID");
                            String REQUEST_FLOW_STATUS_ID=explrObject.getString("REQUEST_STATUS_ID");
                            String ACTION_LEVEL_SEQUENCE = explrObject.getString("ERFS_ACTION_LEVEL_SEQUENCE");
                            String MAX_ACTION_LEVEL_SEQUENCE=explrObject.getString("MAX_ACTION_LEVEL_SEQUENCE");
                            String R_STATUS=explrObject.getString("OD_STATUS");
                            String EMPLOYEE_ID = explrObject.getString("ODR_EMPLOYEE_ID");
                            String ERFS_REQUEST_FLOW_ID = explrObject.getString("ERFS_REQUEST_FLOW_ID");

                            int token_no = Integer.parseInt(explrObject.getString("TOKEN_NO"));
                            arrayList.add(token_no);

                            punchStatusmap.put("TOKEN_NO", tokenNo);
                            punchStatusmap.put("FROM_DATE", fromDate);
                            punchStatusmap.put("TO_DATE", toDate);
                            punchStatusmap.put("EMPLOYEE_CODE", employee_Code);
                            punchStatusmap.put("EMPLOYEE_NAME", employee_Name);
                            punchStatusmap.put("OD_TYPE_NAME", odtype);
                            punchStatusmap.put("OD_REQUEST_TYPE_NAME", odRequestType);
                            punchStatusmap.put("OD_REQUEST_SUB_TYPE_NAME", odSubRequestType);
                            punchStatusmap.put("R_TYPE", odApptype);

                            punchStatusmap.put("ERFS_REQUEST_ID",REQUEST_ID);
                            punchStatusmap.put("REQUEST_STATUS_ID",REQUEST_FLOW_STATUS_ID);
                            punchStatusmap.put("ERFS_ACTION_LEVEL_SEQUENCE",ACTION_LEVEL_SEQUENCE);
                            punchStatusmap.put("MAX_ACTION_LEVEL_SEQUENCE",MAX_ACTION_LEVEL_SEQUENCE);
                            punchStatusmap.put("OD_STATUS",R_STATUS);
                            punchStatusmap.put("ODR_EMPLOYEE_ID",EMPLOYEE_ID);
                            punchStatusmap.put("ERFS_REQUEST_FLOW_ID",ERFS_REQUEST_FLOW_ID);

                            arlData.add(punchStatusmap);


                        }
                        System.out.println("Array===" + arlData);
                        Collections.sort(arrayList,Collections.<Integer>reverseOrder());
                        System.out.println("ArrayList===" + arrayList);

                        //DisplayHolidayList(arlData);
                        mAdapter = new ODApprovalListAdapter(ODVisitHistoryActivity.this, coordinatorLayout,arlData,arrayList);
                        VisitRecyclerStatus.setAdapter(mAdapter);
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

    private class ODApprovalActionAsyn extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empoyeeId,edit_comment_text,tg_text_status,xmlData;
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(ODVisitHistoryActivity.this);
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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ApprovalProcessOnDutyRequest"+"/"+empoyeeId+"/"+tg_text_status+"/"+edit_comment_text+"/"+xmlData;

                System.out.println("ATTENDANCE_URL===="+GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(ODVisitHistoryActivity.this); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
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
                    int res = Integer.parseInt(result);
                    if (res > 0) {
                        Utilities.showDialog(coordinatorLayout, "On Duty Request processed sucessfully.");
                        getODApproval(empoyeeId);
                    } else if (res == 0) {
                        Utilities.showDialog(coordinatorLayout, "Error during processing of On Duty Request.");
                    } else if (res == -1) {
                        Utilities.showDialog(coordinatorLayout, "Request Flow Plan is not available.");
                    } else if (res == -2) {
                        Utilities.showDialog(coordinatorLayout, "Can not take any action on the previous payroll requests.");
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.v("Exception","Integer Value Not accepted");
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
