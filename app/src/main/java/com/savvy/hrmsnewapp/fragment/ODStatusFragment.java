package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.SavvyHRMSApplication;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.ODStatusListAdapter;
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

public class ODStatusFragment extends BaseFragment {
    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData;
    CalanderHRMS calanderHRMS;
    RecyclerView recyclerView;
    Button btn_fromDate, btn_toDate;
    Button btn_submit, btn_spin_od;
    ODStatusListAdapter mAdapter;
    SharedPreferences shared;
    String employeeId = "", requestId = "", punch_status_1 = "";
    String token = "";
    String token_no = "";
    ArrayList<Integer> arrayList;
    ArrayList<String> spinArray;
    ArrayList<String> spinArrayID;
    HashMap<String, String> mapdata;
    String json = "";
    public static Handler handler, handler1;
    public static Runnable runnable, runnable1;
    boolean recycler_status = false;
    CustomTextView txt_punchToken_no, txt_actionValuePull, txt_actionValueCancel;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    CustomTextView txtDataNotFound;
    LinearLayout linear_result_compareDate;
    CustomTextView txt_result_compareDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        spinArray = new ArrayList<>();
        spinArrayID = new ArrayList<>();
        arlRequestStatusData = new ArrayList<>();
        try {
            getStatusList();
            token = (shared.getString("Token", ""));
            employeeId = (shared.getString("EmpoyeeId", ""));
            getODRequestStatus(employeeId, token, "-", "-", "0,1,6,7");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_odstatus, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assert getActivity() != null;
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        mAdapter = new ODStatusListAdapter(getActivity(), coordinatorLayout, arlData);

        calanderHRMS = new CalanderHRMS(getActivity());

        btn_fromDate = getActivity().findViewById(R.id.btn_odFromDate);
        btn_toDate = getActivity().findViewById(R.id.btn_to_ODdate);
        btn_submit = getActivity().findViewById(R.id.btn_odStatus_submit);
        btn_spin_od = getActivity().findViewById(R.id.btn_spin_od_status);

        linear_result_compareDate = getActivity().findViewById(R.id.Linear_result_compareDate);
        txt_result_compareDate = getActivity().findViewById(R.id.result_compareDate);

        linear_result_compareDate.setVisibility(View.GONE);

        if (spinArrayID.size() == 0) {
            btn_spin_od.setText("Pending,Inprocess");
        }
        recyclerView = getActivity().findViewById(R.id.OdRecyclerStatus);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.addOnItemTouchListener(new ODStatusFragment.RecyclerTouchListener(getActivity(), recyclerView, new ODStatusFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_actionValuePull = view.findViewById(R.id.txt_pullback_od);
                    txt_actionValueCancel = view.findViewById(R.id.txt_cancel_od);
                    txt_punchToken_no = view.findViewById(R.id.txt_odToken_value);

                    recycler_status = false;
                    txt_actionValuePull.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {

                            try {

                                token_no = txt_punchToken_no.getText().toString();

                                // Toast.makeText(getActivity(),"Success 2",Toast.LENGTH_LONG).show();
                                String str = mapdata.get("TOKEN_NO");

                                if (token_no.equals(str)) {
                                    String request_Id = mapdata.get("ERFS_REQUEST_ID");
                                    String punch_status_1_Id = mapdata.get("OD_STATUS_1");

                                    requestId = request_Id;
                                    punch_status_1 = punch_status_1_Id;

                                    Log.e("Request Id", "" + requestId);
                                    Log.e("Punch Status", "" + punch_status_1);
                                    Log.e("Token No.", str);
                                }
                                assert getActivity() != null;
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.pullback_dialogbox);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                                CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);

                                edt_comment_dialog.setText("Are you sure, Do you want to pullback OD Request.");
                                txt_header.setText("Pull Back OD Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        if (Utilities.isNetworkAvailable(getActivity())) {
                                            getPullBackStatus(employeeId, requestId, punch_status_1);
                                        } else {
                                            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
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
                                /*Content View Dailog Box*/

//                            final Dialog dialog = new Dialog(getActivity());
//                            dialog.setContentView(R.layout.pullback_method);
//                            Button btn_pull = (Button)dialog.findViewById(R.id.btn_pullback);
//                            btn_pull.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Toast.makeText(getActivity(),"Hello Successs",Toast.LENGTH_LONG).show();
//                                }
//                            });
//
//                            dialog.show();
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                builder.setTitle("PullBack");
//                                builder.setCancelable(false);
//                                builder.setMessage("Are you sure, Do you want to pullback Punch Request.");
//
//                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        getPullBackStatus(employeeId, requestId, punch_status_1);
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
                            } catch (Exception e) {
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
                                    String punch_status_1_Id = mapdata.get("OD_STATUS_1");
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
                                txt_header.setText("Cancel OD Request");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String comment = edt_approve_comment_Cancel.getText().toString();
                                        if (comment.equals("")) {
                                            Utilities.showDialog(coordinatorLayout, "Please Enter Comment.");
                                        } else {
                                            sendODCancelRequest(employeeId, comment, requestId);
//                                            getCancelPunchRequest(employeeId, comment, requestId);
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
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                builder.setTitle("Cancel");
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


        btn_fromDate.setOnClickListener(v -> {
            btn_fromDate.setText("");
            calanderHRMS.datePicker(btn_fromDate);

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        String FromDate = btn_fromDate.getText().toString().trim();
                        String ToDate = btn_toDate.getText().toString().trim();

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
        });
        btn_toDate.setOnClickListener(v -> {
            btn_toDate.setText("");
            calanderHRMS.datePicker(btn_toDate);
            handler1 = new Handler();
            runnable1 = new Runnable() {
                @Override
                public void run() {
                    try {
                        String FromDate = btn_fromDate.getText().toString().trim();
                        String ToDate = btn_toDate.getText().toString().trim();

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

        });

        btn_spin_od.setOnClickListener(v -> {
            final CheckBox[] checkBox = new CheckBox[arlRequestStatusData.size()];
            final TextView[] txtView = new TextView[arlRequestStatusData.size()];
            LinearLayout l1 = new LinearLayout(getActivity());
            l1.setOrientation(LinearLayout.VERTICAL);
//                getActivity().addContentView(txt1,null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
//                            Log.e("Spin Id",""+spinArrayID.size());
                        checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {

                                    String spin_new_value = checkBox[finalI].getText().toString();
                                    String spin_new_id = txtView[finalI].getText().toString();

                                    //checkBox[finalI].setChecked(true);
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    btn_spin_od.setText(spinArray.toString().replace("[", "").replace("]", ""));


                }
            });
            builder.show();
            if (spinArrayID.size() == 0) {
                checkBox[0].setChecked(true);
                checkBox[1].setChecked(true);
                checkBox[6].setChecked(true);
                checkBox[7].setChecked(true);
            }

        });

        btn_submit.setOnClickListener(v -> {
            try {
                token = (shared.getString("Token", ""));

                String keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                if (keyid.equals("")) {
                    keyid = "0,1,6,7";
                }

//                String keyid;
//                if(spinArrayID.size()==0){
//                    keyid = "0,1,2,3";
//                }
//                else{
//                    keyid = spinArrayID.toString().replace("[","").replace("]","").replace(" ","");
//                }
                String gettodate = btn_toDate.getText().toString().replace("/", "-");
                String getfromdate = btn_fromDate.getText().toString().replace("/", "-");
                System.out.print("Date =" + getfromdate + "To Date =" + gettodate + "Key Value = " + keyid);

                if (getfromdate == "")
                    getfromdate = "-";

                if (gettodate == "")
                    gettodate = "-";
                if (linear_result_compareDate.getVisibility() == View.VISIBLE) {
                    Utilities.showDialog(coordinatorLayout, "From Date Should Be Less Than or equal to To Date");
                } else {
                    getODRequestStatus(employeeId, token, getfromdate, gettodate, keyid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    private void getODRequestStatus(String empid, String token, String fromdate, String todate, String spinId) {
        arrayList = new ArrayList<>();
        arlData = new ArrayList<>();

        final String ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetODRequestStatus/" + empid + "/" + fromdate + "/" + todate + "/" + spinId;
        Log.e("ODStatusFragment", "getODRequestStatus: " + ATTENDANCE_URL);
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        StringRequest request = new StringRequest(Request.Method.GET, ATTENDANCE_URL, result -> {
            try {
                Log.e("ODStatusFragment", "result: " + result);
                HashMap<String, String> odStatusmap;
                JSONArray jsonArray = new JSONArray(result);
                System.out.println("jsonArray===" + jsonArray);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        odStatusmap = new HashMap<>();
                        JSONObject explrObject = jsonArray.getJSONObject(i);

                        String tokenNo = explrObject.getString("TOKEN_NO");
                        String actionBy = explrObject.getString("REQUESTED_BY");
                        String actionDate = explrObject.getString("REQUESTED_DATE");

                        String from_date = explrObject.getString("FROM_DATE");
                        String to_date = explrObject.getString("TO_DATE");
                        String od_Type = explrObject.getString("OD_TYPE_NAME");
                        String odRequestType = explrObject.getString("OD_REQUEST_TYPE_NAME");
                        String odRequestSubType = explrObject.getString("OD_REQUEST_SUB_TYPE_NAME");
                        String odstatus = explrObject.getString("REQUEST_STATUS");

                        String employeeId = explrObject.getString("ODR_EMPLOYEE_ID");
                        String requestId = explrObject.getString("ERFS_REQUEST_ID");
                        String od_status = explrObject.getString("OD_STATUS_1");
                        String reason = explrObject.getString("ODR_REASON");
//
//                                int token_no = Integer.parseInt(explrObject.getString("TOKEN_NO"));
//                                arrayList.add(token_no);

                        odStatusmap.put("TOKEN_NO", tokenNo);

                        odStatusmap.put("FROM_DATE", from_date);
                        odStatusmap.put("TO_DATE", to_date);
                        odStatusmap.put("OD_TYPE_NAME", od_Type);
                        odStatusmap.put("OD_REQUEST_TYPE_NAME", odRequestType);
                        odStatusmap.put("OD_REQUEST_SUB_TYPE_NAME", odRequestSubType);
                        odStatusmap.put("REQUEST_STATUS", odstatus);
                        odStatusmap.put("REQUESTED_BY", actionBy);
                        odStatusmap.put("REQUESTED_DATE", actionDate);

                        odStatusmap.put("ODR_EMPLOYEE_ID", employeeId);
                        odStatusmap.put("ERFS_REQUEST_ID", requestId);
                        odStatusmap.put("OD_STATUS_1", od_status);
                        odStatusmap.put("REASON", reason);


                        arlData.add(odStatusmap);


                    }
                    System.out.println("Array===" + arlData);
                    System.out.println("ArrayList===" + arrayList);

                    //Display OD Status (arlData);
                    recyclerView.setVisibility(View.VISIBLE);
                    txtDataNotFound.setVisibility(View.GONE);
                    mAdapter = new ODStatusListAdapter(getActivity(), coordinatorLayout, arlData);
                    recyclerView.setAdapter(mAdapter);
//
                } else {
                    recyclerView.setAdapter(null);
                    recyclerView.setVisibility(View.GONE);
                    txtDataNotFound.setVisibility(View.VISIBLE);
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        queue.add(request);

    }

    private void getStatusList() {
        showProgressDialog();
        String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetRequestStatus";
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        StringRequest request = new StringRequest(Request.Method.GET, GETREQUESTSTATUS_URL, result -> {
            Log.e("ODStatusFragment", "getStatusList: result" + result);
            try {
                dismissProgressDialog();
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

        }, error -> {
            Log.d("error", error.toString());
            Toast.makeText(requireActivity(), "error.toString()", Toast.LENGTH_SHORT).show();
        });
        queue.add(request);

    }

    public void getPullBackStatus(String employeeId, String requestId, String punch_status_1) {

        String URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PullBackODRequest" + "/" + employeeId + "/" + requestId;
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        StringRequest request = new StringRequest(Request.Method.GET, URL, result -> {
            try {
                Log.e("ODStatusFragment", "onResponse: result " + result);
                try {
                    result = result.replaceAll("^\"+|\"+$", " ").trim();
                    int res = Integer.parseInt(result);
                    String keyid;
                    if (spinArrayID.size() == 0) {
                        keyid = "0,1,6,7";
                    } else {
                        keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                    }

                    if (res > 0) {
                        Utilities.showDialog(coordinatorLayout, "On Duty Request Pullback Successfully.");
                        getODRequestStatus(employeeId, token, "-", "-", keyid);

                    } else if (res == 0) {
                        Utilities.showDialog(coordinatorLayout, "Error During Pullback of On Duty Request.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Utilities.showDialog(coordinatorLayout, "Error During Pullback of On Duty Request."));
        queue.add(request);

    }

    public void getCancelPunchRequest(String employeeId, String comment, String requestId) {
        showProgressDialog();
        String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendODCancellationRequest" + "/" + employeeId + "/" + comment + "/" + requestId;
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        StringRequest request = new StringRequest(Request.Method.GET, GETREQUESTSTATUS_URL, result -> {
            try {
                dismissProgressDialog();
                result = result.replaceAll("^\"+|\"+$", " ").trim();

                int res = Integer.parseInt(result);
                String keyid;
                if (spinArrayID.size() == 0) {
                    keyid = "0,1,6,7";
                } else {
                    keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                }

                if (res == 1) {
                    Utilities.showDialog(coordinatorLayout, "On Duty Cancellation Request Send Sucessfully.");
                    getODRequestStatus(employeeId, token, "-", "-", keyid);
                } else if (res == -1) {
                    Utilities.showDialog(coordinatorLayout, "Request Flow Plan Is Not Available.");
                } else if (res == -2) {
                    Utilities.showDialog(coordinatorLayout, "Can Not Take Any Action On The Previous Payroll Requests.");
                } else if (res == 0) {
                    Utilities.showDialog(coordinatorLayout, "Some Error Occur On Processing The Request.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, error -> {
            Log.d("error", error.toString());
            Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        queue.add(request);

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ODStatusFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ODStatusFragment.ClickListener clickListener) {
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

    public void sendODCancelRequest(String emp_id, String comment, String requestId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", emp_id);
                param.put("COMMENT", comment);
                param.put("REQUEST_ID", requestId);

                Log.e("RequestData", param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendODCancellationRequestPost";
                Log.e("URL", url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int res = response.getInt("SendODCancellationRequestPostResult");

                                    String keyid;
                                    if (spinArrayID.size() == 0) {
                                        keyid = "0,1,6,7";
                                    } else {
                                        keyid = spinArrayID.toString().replace("[", "").replace("]", "").replace(" ", "");
                                    }

                                    if (res == 1) {
                                        Utilities.showDialog(coordinatorLayout, "On Duty Cancellation Request Send Sucessfully.");
                                        getODRequestStatus(employeeId, token, "-", "-", keyid);
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
                                        txt_result_compareDate.setText("From Date should be less than or equal To Date!");
                                        linear_result_compareDate.setVisibility(View.VISIBLE);
                                    } else {
                                        linear_result_compareDate.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

                int socketTime = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonRequest.setRetryPolicy(policy);
                requestQueue.add(jsonRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
