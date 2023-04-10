package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
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
import com.savvy.hrmsnewapp.adapter.PunchApprovalListAdapter;
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
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PunchApprovalFragment extends BaseFragment {

    PunchApprovalFragment.ApprovAsync approvAsync;
    PunchApprovalFragment.PunchApprovalAsyn punchApprovalAsyn;
    PunchApprovalListAdapter mAdapter;
    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "";
    List<HashMap<String,String>> data ;
    ArrayList<Integer> arrayList;

    String xmlData = "";
    String tg_text = "true",comment = "";
    String token_no = "";
    CustomTextView approv_punchToken_no;
    String REQUEST_ID = "",REQUEST_FLOW_STATUS_ID = "",ACTION_LEVEL_SEQUENCE = "",MAX_ACTION_LEVEL_SEQUENCE = "",
            R_STATUS = "",EMPLOYEE_ID = "",ERFS_REQUEST_FLOW_ID = "";

    CustomTextView txtDataNotFound;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  arrayList = new ArrayList<>();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        mSavvyHrmsApp = SavvyHRMSApplication.getInstance();

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        getPunchApproval(empoyeeId);
    }

    private void getPunchApproval(String empid) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arrayList = new ArrayList<>();
                arlData = new ArrayList<HashMap<String, String>>();
                punchApprovalAsyn = new PunchApprovalFragment.PunchApprovalAsyn();
                punchApprovalAsyn.empid = empid;
                punchApprovalAsyn.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_punch_approval, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        recyclerView = getActivity().findViewById(R.id.punchApprovRecycler);
        mAdapter = new PunchApprovalListAdapter(getActivity(),coordinatorLayout,arlData ,arrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new PunchApprovalFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    CustomTextView txt_actionValue = view.findViewById(R.id.approv_pullback);
                    approv_punchToken_no = view.findViewById(R.id.txt_puchApprov_value);
                    //    Toast.makeText(getActivity(),"Press Again",Toast.LENGTH_LONG).show();
                    txt_actionValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                               token_no = approv_punchToken_no.getText().toString();

                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {

                                    REQUEST_ID = mapdata.get("ERFS_REQUEST_ID");
                                    REQUEST_FLOW_STATUS_ID = mapdata.get("REQUEST_STATUS_ID");
                                    ACTION_LEVEL_SEQUENCE = mapdata.get("ERFS_ACTION_LEVEL_SEQUENCE");
                                    MAX_ACTION_LEVEL_SEQUENCE = mapdata.get("MAX_ACTION_LEVEL_SEQUENCE");
                                    R_STATUS = mapdata.get("PUNCH_STATUS");
                                    EMPLOYEE_ID = mapdata.get("PR_EMPLOYEE_ID");
                                    ERFS_REQUEST_FLOW_ID = mapdata.get("ERFS_REQUEST_FLOW_ID");

                                    xmlData = REQUEST_ID + "," + REQUEST_FLOW_STATUS_ID + "," + ACTION_LEVEL_SEQUENCE + "," +
                                            MAX_ACTION_LEVEL_SEQUENCE + "," + R_STATUS + "," + EMPLOYEE_ID + "," + ERFS_REQUEST_FLOW_ID;

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
                            txt_header.setText("Punch Approve/Reject");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    comment = edt_comment.getText().toString().trim();
                                    if(comment.equals("")) {
                                        Utilities.showDialog(coordinatorLayout,"Please Enter Comment");
                                    } else{
//                                        getApproveList(comment, tg_text, xmlData, empoyeeId);
                                        punchProcessApprove(empoyeeId,tg_text,comment,xmlData);
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
//                                    getApproveList(edit_comment_text, tg_text, xmlData, empoyeeId);
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

    public void getApproveList(String edit_comment_text,String tg_text, String xmlData,String employeeId) {
        try {
            if(Utilities.isNetworkAvailable(getActivity())) {
                approvAsync = new PunchApprovalFragment.ApprovAsync();
                approvAsync.edit_comment_text = edit_comment_text;
                approvAsync.tg_text_status = tg_text;
                approvAsync.xmlData = xmlData;
                approvAsync.employeeId = employeeId;

                approvAsync.execute();
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
        private PunchApprovalFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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

    public void punchProcessApprove(String employeeId,String status,String comment,String xmlData){
        try{
            if(Utilities.isNetworkAvailable(getActivity())){
                try{
                    JSONObject param = new JSONObject();

                    param.put("EMPLOYEE_ID",employeeId);
                    param.put("STATUS",status);
                    param.put("COMMENT",comment);
                    param.put("XMLDATA",xmlData);

                    String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ProcessPunchRequestPost";

                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        int res = response.getInt("ProcessPunchRequestPostResult");

                                        if (res > 0) {
                                            Utilities.showDialog(coordinatorLayout, "Punch Regularization Request processed sucessfully.");
                                            getPunchApproval(empoyeeId);
                                        } else if (res == 0) {
                                            Utilities.showDialog(coordinatorLayout, "Error during processing of Punch Regularization Request.");
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
                    RetryPolicy policy = new DefaultRetryPolicy(socketTime,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    jsonObjectRequest.setRetryPolicy(policy);
                    requestQueue.add(jsonObjectRequest);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class ApprovAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String edit_comment_text,tg_text_status,xmlData,employeeId;

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
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ApprovalProcessPunchRequest"+"/"+employeeId+"/"+tg_text_status+"/"+edit_comment_text+"/"+xmlData;

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
                try{
                    int res = Integer.parseInt(result);

                    if(res>0)
                    {
                        Utilities.showDialog(coordinatorLayout,"Punch Regularization Request processed sucessfully.");
                        getPunchApproval(empoyeeId);

                    }else if(res==0){
                        Utilities.showDialog(coordinatorLayout,"Error during processing of Punch Regularization Request.");
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
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

    private class PunchApprovalAsyn extends AsyncTask<String, String, String> {
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
                String ATTENDANCE_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ApprovalGetPunchRequestDetail/"+empid;

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
                            String employee_Code=explrObject.getString("EMPLOYEE_CODE");                         //6
                            String employee_Name=explrObject.getString("EMPLOYEE_NAME");                     //7
                            String punchDate=explrObject.getString("PUNCH_DATE");                         //2
                            String punchTime=explrObject.getString("PUNCH_TIME");
                            String paReason=explrObject.getString("REASON");                         //4
                            String patype=explrObject.getString("R_TYPE");

                            String REQUEST_ID=explrObject.getString("ERFS_REQUEST_ID");
                            String REQUEST_FLOW_STATUS_ID=explrObject.getString("REQUEST_STATUS_ID");
                            String ACTION_LEVEL_SEQUENCE = explrObject.getString("ERFS_ACTION_LEVEL_SEQUENCE");
                            String MAX_ACTION_LEVEL_SEQUENCE=explrObject.getString("MAX_ACTION_LEVEL_SEQUENCE");
                            String R_STATUS=explrObject.getString("PUNCH_STATUS");
                            String EMPLOYEE_ID = explrObject.getString("PR_EMPLOYEE_ID");
                            String ERFS_REQUEST_FLOW_ID = explrObject.getString("ERFS_REQUEST_FLOW_ID");


                            int token_no = Integer.parseInt(explrObject.getString("TOKEN_NO"));
                            arrayList.add(token_no);

                            punchStatusmap.put("TOKEN_NO", tokenNo);
                            punchStatusmap.put("PUNCH_DATE", punchDate);
                            punchStatusmap.put("PUNCH_TIME", punchTime);
                            punchStatusmap.put("EMPLOYEE_CODE", employee_Code);
                            punchStatusmap.put("EMPLOYEE_NAME", employee_Name);
                            punchStatusmap.put("REASON", paReason);
                            punchStatusmap.put("R_TYPE", patype);

                            punchStatusmap.put("ERFS_REQUEST_ID",REQUEST_ID);
                            punchStatusmap.put("REQUEST_STATUS_ID",REQUEST_FLOW_STATUS_ID);
                            punchStatusmap.put("ERFS_ACTION_LEVEL_SEQUENCE",ACTION_LEVEL_SEQUENCE);
                            punchStatusmap.put("MAX_ACTION_LEVEL_SEQUENCE",MAX_ACTION_LEVEL_SEQUENCE);
                            punchStatusmap.put("PUNCH_STATUS",R_STATUS);
                            punchStatusmap.put("PR_EMPLOYEE_ID",EMPLOYEE_ID);
                            punchStatusmap.put("ERFS_REQUEST_FLOW_ID",ERFS_REQUEST_FLOW_ID);

                            arlData.add(punchStatusmap);
                        }

                        System.out.println("Array===" + arlData);
                        Collections.sort(arrayList,Collections.<Integer>reverseOrder());
                        System.out.println("ArrayList===" + arrayList);
                        //DisplayHolidayList(arlData);
                        recyclerView.setVisibility(View.VISIBLE);
                        txtDataNotFound.setVisibility(View.GONE);
                        mAdapter = new PunchApprovalListAdapter(getActivity(), coordinatorLayout,arlData, arrayList);
                        recyclerView.setAdapter(mAdapter);
                    }else{
                        recyclerView.setAdapter(null);
                        recyclerView.setVisibility(View.GONE);
                        txtDataNotFound.setVisibility(View.VISIBLE);
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
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
}
