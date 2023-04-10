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
import com.savvy.hrmsnewapp.activity.SavvyHRMSApplication;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.StatusConveyanceAdapter;
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

public class ConveyanceStatusFragment extends BaseFragment {

    ConveyanceStatusFragment.ConveyanceSpinnerStatusAsync conveyanceSpinnerStatusAsync;
    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;


    CalanderHRMS calanderHRMS;
    private int year, month, day;
    RecyclerView recyclerView;
    private boolean isDateDialogopenned;
    Button btn_fromDate,btn_toDate;
    Button btn_submit, btn_spin_conveyance;
    Spinner spinner_status;
    CustomSpinnerAdapter customspinnerAdapter;
    StatusConveyanceAdapter mAdapter;
    private SavvyHRMSApplication mSavvyHrmsApp;
    // private OnFragmentInteractionListener mListener;
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    String employeeId = "",requestId = "",punch_status_1 = "";
    String token = "";
    String token_no = "";
    String empoyeeId = "";
    ArrayList<Integer> arrayList;
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapdata;

    boolean recycler_status = false;
    Handler handler;
    Runnable rRunnable;

    String FROM_DATE = "", TO_DATE = "";

    LinearLayout linear_result_compareDate;
    CustomTextView txt_result_compareDate;

    CustomTextView txt_ConEdit,txt_ConPullback,txt_ConToken_no,txt_ConCancel;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Dialog dialog;
    CustomTextView errorDataCustomTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arlData = new ArrayList<HashMap<String,String>> ();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        spinArray = new ArrayList();
        spinArrayID = new ArrayList();

        getStatusList();
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));

        Log.e("Employee Id",empoyeeId);
    }

    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequestStatusData = new ArrayList<HashMap<String, String>>();
                conveyanceSpinnerStatusAsync = new ConveyanceStatusFragment.ConveyanceSpinnerStatusAsync();
                // requestStatusasynctask.empid=empid;
                conveyanceSpinnerStatusAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conveyance_status, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        mAdapter = new StatusConveyanceAdapter(getActivity(),coordinatorLayout,arlData);

        calanderHRMS = new CalanderHRMS(getActivity());

        btn_fromDate= getActivity().findViewById(R.id.btn_conveyanceFromDate);
        btn_toDate= getActivity().findViewById(R.id.btn_to_conveyanceDate);
        btn_submit= getActivity().findViewById(R.id.btn_conveyanceStatus_submit);
        btn_spin_conveyance= getActivity().findViewById(R.id.btn_spin_conveyance_status);

        linear_result_compareDate= getActivity().findViewById(R.id.Linear_result_compareDate);
        txt_result_compareDate= getActivity().findViewById(R.id.result_compareDate);

        linear_result_compareDate.setVisibility(View.GONE);

        if(spinArrayID.size()==0){
            btn_spin_conveyance.setText("Pending,Inprocess");
        }
        recyclerView = getActivity().findViewById(R.id.ConveyanceRecyclerStatus);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        errorDataCustomTextView = getActivity().findViewById(R.id.errorData);
        errorDataCustomTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.addOnItemTouchListener(new ConveyanceStatusFragment.RecyclerTouchListener(getActivity(), recyclerView, new ConveyanceStatusFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_ConEdit = view.findViewById(R.id.con_edit);
                    txt_ConPullback = view.findViewById(R.id.con_pullback);
                    txt_ConCancel = view.findViewById(R.id.con_cancel);
                    txt_ConToken_no = view.findViewById(R.id.con_TokenNo);

                    recycler_status = false;

                    txt_ConPullback.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            token_no = txt_ConToken_no.getText().toString();
                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {
                                requestId = mapdata.get("ERFS_REQUEST_ID");
                            }

                            dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.pullback_dialogbox);
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                            CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
                            Button btn_ApproveGo, btn_close;
                            btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                            btn_close = dialog.findViewById(R.id.btn_close);

                            edt_comment_dialog.setText("Are you sure, Do you want to pullback Conveyance Request.");
                            txt_header.setText("Pull Back Conveyance Request");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getPullBackStatus(employeeId, requestId);
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

//                            String PullBackTitle = "<font color=\"#5cb85c\"><bold>" + "PullBack Conveyance Request" + "</bold></font>";
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                            builder.setTitle(Html.fromHtml(PullBackTitle));
//                            builder.setCancelable(false);
//                            builder.setMessage("Are you sure, Do you want to pullback Conveyance Request.");
//
//                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    getPullBackStatus(employeeId, requestId);
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

                    txt_ConCancel.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {

                            token_no = txt_ConToken_no.getText().toString();

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

//                                edt_comment_dialog.setText("Are you sure, Do you want to pullback OD Request.");
                            txt_header.setText("Cancel Conveyance Request");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String comment = edt_approve_comment_Cancel.getText().toString();
                                    if(comment.equals("")){
                                        Utilities.showDialog(coordinatorLayout,"Please Enter Comment.");
                                    } else {
                                        sendConveyanceCancelStatus(employeeId, requestId, comment, "Conveyance");
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
//                            String CancelTitle = "<font color=\"#E72A02\"><bold>" + "Cancel Conveyance Request" + "</bold></font>";
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
//                                    sendConveyanceCancelStatus(employeeId, requestId, finalComment,"Conveyance");
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

                    txt_ConEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            token_no = txt_ConToken_no.getText().toString();
                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {
                                requestId = mapdata.get("ERFS_REQUEST_ID");

                                Constants.CONVEYANCE_REQUEST_ID = requestId;
                                Constants.CONVEYANCE_REQUEST_ID_STATUS = false;
                            }

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            ConveyancefragmentHolder conveyancefragmentHolder = new ConveyancefragmentHolder();
                            transaction.replace(R.id.container_body, conveyancefragmentHolder);
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

        String from_date = btn_fromDate.getText().toString().trim().replace("/","-");
        String to_date = btn_toDate.getText().toString().trim().replace("/","-");

        if(from_date.equals("")||to_date.equals("")){
            from_date = "-";
            to_date = "-";
        }
        getConveyanceStatus(from_date,to_date,"0,1,6,7");

        btn_spin_conveyance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        btn_spin_conveyance.setText(spinArray.toString().replace("[","").replace("]",""));


                    }
                });
                builder.show();

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    getConveyanceStatus(getfromdate,gettodate,keyid);
                }
            }
        });

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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ConveyanceStatusFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ConveyanceStatusFragment.ClickListener clickListener) {
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

    public void getPullBackStatus(String empId,String ReqId){
        try{
            if(Utilities.isNetworkAvailable(getActivity())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackConveyanceRequest";
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
                                    String result = response.getString("PullBackConveyanceRequestResult");

                                    int res = Integer.valueOf(result);
                                    String keyid;
                                    if (spinArrayID.size() == 0) {
                                        keyid = "0,1,6,7";
                                    } else {
                                        keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                                    }
                                    if (res > 0) {
                                        Utilities.showDialog(coordinatorLayout, "Conveyance request pull-back successfully.");
                                        getConveyanceStatus("-", "-", keyid);
                                    } else if (res == 0) {
                                        Utilities.showDialog(coordinatorLayout, "Error during pull-back of Conveyance Request.");
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

    public void sendConveyanceCancelStatus(String empId, String ReqId, String comment, final String conveyance){
        try{
            if(Utilities.isNetworkAvailable(getActivity())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendCancellationRequest";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("EMPLOYEE_ID", empId);
                params_final.put("COMMENT", comment);
                params_final.put("REQUEST_ID", ReqId);
                params_final.put("TYPE", conveyance);

                pm.put("objSendCancellationRequestInfo", params_final);

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
                                    String result = response.getString("SendCancellationRequestResult");

                                    int res = Integer.valueOf(result);
                                    String keyid;
                                    if (spinArrayID.size() == 0) {
                                        keyid = "0,1,6,7";
                                    } else {
                                        keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                                    }
                                    if (res == 1) {
                                        if (conveyance.toUpperCase().equals("CONVEYANCE")) {
                                            Utilities.showDialog(coordinatorLayout, "Conveyance Cancellation request send sucessfully.");
                                            getConveyanceStatus("-", "-", keyid);
                                        } else {
                                            Utilities.showDialog(coordinatorLayout, "Conveyance Voucher Cancellation request send sucessfully.");
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
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getConveyanceStatus(String fromDate,String toDate, String KeyId){
        try {
            if(Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<HashMap<String, String>>();
                final ProgressDialog pDialog;
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetConveyanceRequestStatus";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("EMPLOYEE_ID", empoyeeId);
                params_final.put("FROM_DATE", fromDate);
                params_final.put("TO_DATE", toDate);
                params_final.put("REQUEST_STATUS", KeyId);

                pm.put("objConveyanceRequestStatusInfo", params_final);

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
                                    JSONArray jsonArray = response.getJSONArray("GetConveyanceRequestStatusResult");
//
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        mapData = new HashMap<String, String>();
                                        JSONObject explrObject = jsonArray.getJSONObject(i);

                                        String TOKEN_NO = explrObject.getString("TOKEN_NO");
                                        String CR_APPROVED_AMOUNT = explrObject.getString("CR_APPROVED_AMOUNT");
                                        String REQUEST_STATUS = explrObject.getString("REQUEST_STATUS");
                                        String REQUESTED_DATE = explrObject.getString("REQUESTED_DATE");
                                        String CR_REMARKS = explrObject.getString("CR_REMARKS");
                                        String CR_REQUESTED_AMOUNT = explrObject.getString("CR_REQUESTED_AMOUNT");
                                        String ERFS_REQUEST_ID = explrObject.getString("ERFS_REQUEST_ID");
                                        String CR_EMPLOYEE_ID = explrObject.getString("CR_EMPLOYEE_ID");
                                        String CONVEYANCE_STATUS_1 = explrObject.getString("CONVEYANCE_STATUS_1");
                                        String CONVEYANCE_STATUS = explrObject.getString("CONVEYANCE_STATUS");

                                        mapData.put("TOKEN_NO", TOKEN_NO);
                                        mapData.put("CR_APPROVED_AMOUNT", CR_APPROVED_AMOUNT);
                                        mapData.put("REQUEST_STATUS", REQUEST_STATUS);
                                        mapData.put("REQUESTED_DATE", REQUESTED_DATE);
                                        mapData.put("CR_REMARKS", CR_REMARKS);
                                        mapData.put("CR_REQUESTED_AMOUNT", CR_REQUESTED_AMOUNT);
                                        mapData.put("ERFS_REQUEST_ID", ERFS_REQUEST_ID);
                                        mapData.put("CR_EMPLOYEE_ID", CR_EMPLOYEE_ID);
                                        mapData.put("CONVEYANCE_STATUS_1", CONVEYANCE_STATUS_1);
                                        mapData.put("CONVEYANCE_STATUS", CONVEYANCE_STATUS);

                                        arlData.add(mapData);

                                    }
                                    Log.e("Conveyance Status", arlData.toString());
                                    System.out.println("Conveyance Status==" + arlData);

                                    if(jsonArray.length()<=0){
                                        errorDataCustomTextView.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                        recyclerView.setAdapter(null);
                                        Utilities.showDialog(coordinatorLayout,ErrorConstants.DATA_ERROR);
                                    } else{
                                        errorDataCustomTextView.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        mAdapter = new StatusConveyanceAdapter(getActivity(), coordinatorLayout, arlData);
                                        recyclerView.setAdapter(mAdapter);
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

    private class ConveyanceSpinnerStatusAsync extends AsyncTask<String, String, String> {
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

//                        customspinnerAdapter = new CustomSpinnerAdapter(getActivity(),arlRequestStatusData);
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
