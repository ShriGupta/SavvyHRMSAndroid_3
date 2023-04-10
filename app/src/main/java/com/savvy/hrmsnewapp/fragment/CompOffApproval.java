package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CompOffListAdapter;
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

public class CompOffApproval extends BaseFragment {

    CompOffApproval.CompApprovalAsync compApprovalAsync;
    CompOffApproval.ComApprovAsync comApprovAsync;

    CompOffListAdapter mAdapter;
    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "";
    ArrayList<Integer> arrayList;
    String tg_text = "true",comment = "";

    CustomTextView txtDataNotFound;
    String xmlData = "";
    String token_no = "";
    CustomTextView comp_punchToken_no;
    String REQUEST_ID = "",REQUEST_FLOW_STATUS_ID = "",ACTION_LEVEL_SEQUENCE = "",
            MAX_ACTION_LEVEL_SEQUENCE = "",R_STATUS = "",EMPLOYEE_ID = "",
            ERFS_REQUEST_FLOW_ID = "",COR_APPROVE_DAYS = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        getCompApproval(empoyeeId);
    }

    private void getCompApproval(String empid) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arrayList = new ArrayList<>();
                arlData = new ArrayList<HashMap<String, String>>();
                compApprovalAsync = new CompOffApproval.CompApprovalAsync();
                compApprovalAsync.empid = empid;
                compApprovalAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comp_off_approval, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        recyclerView = getActivity().findViewById(R.id.compApprovRecycler);
        mAdapter = new CompOffListAdapter(getActivity(),coordinatorLayout,arlData,arrayList);

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new CompOffApproval.RecyclerTouchListener(getContext(), recyclerView, new CompOffApproval.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    CustomTextView txt_actionValue = view.findViewById(R.id.comp_action);
                    comp_punchToken_no = view.findViewById(R.id.comp_token_value);
                    txt_actionValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                               token_no = comp_punchToken_no.getText().toString();

                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {


                                    REQUEST_ID = mapdata.get("ERFS_REQUEST_ID");
                                    REQUEST_FLOW_STATUS_ID = mapdata.get("REQUEST_STATUS_ID");
                                    ACTION_LEVEL_SEQUENCE = mapdata.get("ERFS_ACTION_LEVEL_SEQUENCE");
                                    MAX_ACTION_LEVEL_SEQUENCE = mapdata.get("MAX_ACTION_LEVEL_SEQUENCE");
                                    R_STATUS = mapdata.get("COMP_OFF_STATUS");
                                    EMPLOYEE_ID = mapdata.get("COR_EMPLOYEE_ID");
                                    ERFS_REQUEST_FLOW_ID = mapdata.get("ERFS_REQUEST_FLOW_ID");
                                    COR_APPROVE_DAYS = mapdata.get("COR_APPROVE_DAYS");

                                    xmlData = REQUEST_ID + "," + REQUEST_FLOW_STATUS_ID + "," + ACTION_LEVEL_SEQUENCE + "," +
                                            MAX_ACTION_LEVEL_SEQUENCE + "," + R_STATUS + "," + EMPLOYEE_ID + "," + ERFS_REQUEST_FLOW_ID + "," + COR_APPROVE_DAYS;

                                    Log.e("XML DATA", xmlData);
                                    Log.e("Token No.", str);
                                }

                            //}

                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.dialogbox_approval_toggle);
                            dialog.setTitle("Approval");
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                            Button btn_ApproveGo, btn_close;
                            btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                            btn_close = dialog.findViewById(R.id.btn_close);

                            CustomTextView txt_ApprovalToggleTitle,edt_comment_dialog;
                            txt_ApprovalToggleTitle = dialog.findViewById(R.id.txt_ApprovalToggleTitle);
                            edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);

                            String str1 = "<font color='#EE0000'>*</font>";
                            txt_ApprovalToggleTitle.setText(Html.fromHtml("Type " + str1));
                            edt_comment_dialog.setText(Html.fromHtml("Comment " + str1));

                            final EditText edt_comment = dialog.findViewById(R.id.edt_approve_comment);
                            final ToggleButton tgText = dialog.findViewById(R.id.tg_approve);

                            tgText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (tgText.isChecked()) {
                                        tg_text = "" + "true";
                                    } else {
                                        tg_text = "" + "false";
                                    }
                                }
                            });
                            txt_header.setText("Comp Off Approve/Reject");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    comment = edt_comment.getText().toString().trim().replace(" ","_");
                                    if(comment.equals("")) {
                                        Utilities.showDialog(coordinatorLayout,"Please Enter Comment");
                                    } else{
                                        getApproveList(empoyeeId, comment, tg_text, xmlData);
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
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                            builder.setTitle("  Punch Approval Action ");
//                            builder.setCancelable(false);
//
//                            final TextView toggleText = new TextView(getActivity());
//                            toggleText.setText("    Type :           ");
//
//                            final TextView txt_comment = new TextView(getActivity());
//                            txt_comment.setText("    Comment :   ");
//                            final EditText edit_comment = new EditText(getActivity());
//                            edit_comment.setEms(10);
//                            edit_comment.setWidth(400);
//                            edit_comment.setSingleLine();
//
//                            final ToggleButton tg = new ToggleButton(getActivity());
//                            tg.setChecked(true);
//                            tg.setText("Approved");
//                            tg.setTextOn("Approved");
//                            tg.setTextOff("Reject");
//
//                            LinearLayout ll1 = new LinearLayout(getActivity());
//                            LinearLayout ll2 = new LinearLayout(getActivity());
//                            LinearLayout ll3 = new LinearLayout(getActivity());
//
//                            ll1.setOrientation(LinearLayout.HORIZONTAL);
//                            ll1.addView(txt_comment);
//                            ll1.addView(edit_comment);
//
//                            ll2.setOrientation(LinearLayout.HORIZONTAL);
//                            ll2.addView(toggleText);
//                            ll2.addView(tg);
//
//                            ll3.setOrientation(LinearLayout.VERTICAL);
//                            ll3.addView(ll1);
//                            ll3.addView(ll2);
//                            builder.setView(ll3);
//
//                            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                            builder.setNegativeButton("Go", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    String tg_text;
//                                    String edit_comment_text = edit_comment.getText().toString().replace(" ", "_");
//                                    if (tg.isChecked()) {
//                                        tg_text = "" + "true";
//                                    } else {
//                                        tg_text = "" + "false";
//                                    }
//
//                                    if (edit_comment_text.equals(""))
//                                        edit_comment_text = "-";
//
//                                    Log.e("XML DATA", "" + xmlData);
//                                    try {
//                                        getApproveList(empoyeeId, edit_comment_text, tg_text, xmlData);
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                }
//                            });
//                            builder.show();
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
    }

    public void getApproveList(String empoyeeId,String edit_comment_text,String tg_text, String xmlData)
    {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                comApprovAsync = new CompOffApproval.ComApprovAsync();
                comApprovAsync.empoyeeId = empoyeeId;
                comApprovAsync.edit_comment_text = edit_comment_text;
                comApprovAsync.tg_text_status = tg_text;
                comApprovAsync.xmlData = xmlData;

                comApprovAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private class ComApprovAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empoyeeId,edit_comment_text,tg_text_status,xmlData;
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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ApprovalProcessCompoffRequest"+"/"+empoyeeId+"/"+tg_text_status+"/"+edit_comment_text+"/"+xmlData;

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
                    int res = Integer.parseInt(result);
                    if (res > 0) {
                        Utilities.showDialog(coordinatorLayout, "Comp-Off Request processed sucessfully.");
                        getCompApproval(empoyeeId);
                    } else if (res == 0) {
                        Utilities.showDialog(coordinatorLayout, "Error during processing of Comp-Off Request.");
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private CompOffApproval.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CompOffApproval.ClickListener clickListener) {
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

    private class CompApprovalAsync extends AsyncTask<String, String, String> {
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
                String ATTENDANCE_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ApprovalGetCompOffRequestDetail/"+empid;

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

                            String tokenNo=explrObject.getString("TOKEN_NO");                           //1
                            String timeIn=explrObject.getString("TIME_IN");                           //1
                            String timeOut=explrObject.getString("TIME_OUT");                           //1
                            String employee_Code=explrObject.getString("EMPLOYEE_CODE");                         //6
                            String employee_Name=explrObject.getString("EMPLOYEE_NAME");                     //7
                            String CompOffDate=explrObject.getString("COMP_OFF_DATE");                         //2
                            String reason=explrObject.getString("REASON");
                            String requestedDays=explrObject.getString("COR_REQUEST_DAYS");                         //4
                            String approvedDays=explrObject.getString("COR_APPROVE_DAYS");                         //5
                            String type=explrObject.getString("R_TYPE");                         //5

                            String REQUEST_ID=explrObject.getString("ERFS_REQUEST_ID");
                            String REQUEST_FLOW_STATUS_ID=explrObject.getString("REQUEST_STATUS_ID");
                            String ACTION_LEVEL_SEQUENCE = explrObject.getString("ERFS_ACTION_LEVEL_SEQUENCE");
                            String MAX_ACTION_LEVEL_SEQUENCE=explrObject.getString("MAX_ACTION_LEVEL_SEQUENCE");
                            String R_STATUS=explrObject.getString("COMP_OFF_STATUS");
                            String EMPLOYEE_ID = explrObject.getString("COR_EMPLOYEE_ID");
                            String ERFS_REQUEST_FLOW_ID = explrObject.getString("ERFS_REQUEST_FLOW_ID");
                            String COR_APPROVE_DAYS = explrObject.getString("COR_APPROVE_DAYS");

                            int token_no = Integer.parseInt(explrObject.getString("TOKEN_NO"));
                            arrayList.add(token_no);


                            punchStatusmap.put("TOKEN_NO", tokenNo);
                            punchStatusmap.put("TIME_IN", timeIn);
                            punchStatusmap.put("TIME_OUT", timeOut);
                            punchStatusmap.put("EMPLOYEE_CODE", employee_Code);
                            punchStatusmap.put("EMPLOYEE_NAME", employee_Name);
                            punchStatusmap.put("COMP_OFF_DATE", CompOffDate);
                            punchStatusmap.put("REASON", reason);
                            punchStatusmap.put("COR_REQUEST_DAYS", requestedDays);
                            punchStatusmap.put("COR_APPROVE_DAYS", approvedDays);
                            punchStatusmap.put("R_TYPE", type);

                            punchStatusmap.put("ERFS_REQUEST_ID",REQUEST_ID);
                            punchStatusmap.put("REQUEST_STATUS_ID",REQUEST_FLOW_STATUS_ID);
                            punchStatusmap.put("ERFS_ACTION_LEVEL_SEQUENCE",ACTION_LEVEL_SEQUENCE);
                            punchStatusmap.put("MAX_ACTION_LEVEL_SEQUENCE",MAX_ACTION_LEVEL_SEQUENCE);
                            punchStatusmap.put("COMP_OFF_STATUS",R_STATUS);
                            punchStatusmap.put("COR_EMPLOYEE_ID",EMPLOYEE_ID);
                            punchStatusmap.put("ERFS_REQUEST_FLOW_ID",ERFS_REQUEST_FLOW_ID);
                            punchStatusmap.put("COR_APPROVE_DAYS",COR_APPROVE_DAYS);

                            arlData.add(punchStatusmap);


                        }
                        System.out.println("Array===" + arlData);

                        Collections.sort(arrayList);
                        System.out.println("ArrayList===" + arrayList);

                        //DisplayHolidayList(arlData);
                        recyclerView.setVisibility(View.VISIBLE);
                        txtDataNotFound.setVisibility(View.GONE);

                        mAdapter = new CompOffListAdapter(getActivity(), coordinatorLayout,arlData,arrayList);
                        recyclerView.setAdapter(mAdapter);

                    } else{
                        recyclerView.setAdapter(null);
                        Utilities.showDialog(coordinatorLayout,ErrorConstants.DATA_ERROR);
                        recyclerView.setVisibility(View.GONE);
                        txtDataNotFound.setVisibility(View.VISIBLE);
                    }
//                    else{
//                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            public void run() {
//                                recyclerView.setAdapter(null);
//                            }
//                        }, 500);
//                        System.out.println("Data not getting on server side");
//                    }

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
}
