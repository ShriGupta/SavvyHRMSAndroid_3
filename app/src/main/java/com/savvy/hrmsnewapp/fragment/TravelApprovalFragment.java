package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
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
import com.savvy.hrmsnewapp.adapter.ApprovalTravalAdapter;
import com.savvy.hrmsnewapp.adapter.StatusConveyanceVoucherAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TravelApprovalFragment extends BaseFragment {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "";
    ApprovalTravalAdapter mAdapter;
    StatusConveyanceVoucherAdapter mAdapter1;

    RecyclerView recyclerView, recyclerViewDetail;
    Button btn_closeDetail;
    String token_no = "";


    LinearLayout linearNoDataFound;

    ArrayList<HashMap<String,String>> arlData, arlData1, arlDataConveyanceId;
    CustomTextView txt_ApproveAction, txt_ApproveToken, txt_ApproveModify;
    String XML_ERFS_REQUEST_ID = "", XML_REQUEST_STATUS_ID = "", XML_ERFS_ACTION_LEVEL_SEQUENCE = "",
            XML_MAX_ACTION_LEVEL_SEQUENCE = "", XML_CONVEYANCE_STATUS = "",
            XML_CR_EMPLOYEE_ID = "", XML_ERFS_REQUEST_FLOW_ID = "", XML_APPROVED_ADVANCE_AMOUNT = "";

    String xmlData = "", tg_text = "true", comment = "";
    String conveyanceId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arlData = new ArrayList<HashMap<String,String>>();
        arlData1 = new ArrayList<HashMap<String,String>>();

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));

//        getConveyanceForVoucher();

        getTravelApprovalStatus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.travel_approval_fragment, container, false);

        linearNoDataFound = view.findViewById(R.id.linearNoDataFound);

        linearNoDataFound.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.recyclerTravelApproval);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new TravelApprovalFragment.RecyclerTouchListener(getActivity(), recyclerView, new TravelApprovalFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_ApproveAction = view.findViewById(R.id.txt_travelAction);
                    txt_ApproveToken = view.findViewById(R.id.txt_travelApproveToken);


                    txt_ApproveAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                token_no = txt_ApproveToken.getText().toString();

                                String str = mapdata.get("TOKEN_NO");
                                if (token_no.equals(str)) {
                                    XML_ERFS_REQUEST_ID = mapdata.get("ERFS_REQUEST_ID");
                                    XML_REQUEST_STATUS_ID = mapdata.get("REQUEST_STATUS_ID");
                                    XML_ERFS_ACTION_LEVEL_SEQUENCE = mapdata.get("ERFS_ACTION_LEVEL_SEQUENCE");
                                    XML_MAX_ACTION_LEVEL_SEQUENCE = mapdata.get("MAX_ACTION_LEVEL_SEQUENCE");
                                    XML_CONVEYANCE_STATUS = mapdata.get("TRAVEL_STATUS");
                                    XML_CR_EMPLOYEE_ID = mapdata.get("TR_EMPLOYEE_ID");
                                    XML_ERFS_REQUEST_FLOW_ID = mapdata.get("ERFS_REQUEST_FLOW_ID");
                                    XML_APPROVED_ADVANCE_AMOUNT = mapdata.get("TR_APPROVED_ADVANCE_AMOUNT");

                                    xmlData = XML_ERFS_REQUEST_ID + "," + XML_REQUEST_STATUS_ID + "," + XML_ERFS_ACTION_LEVEL_SEQUENCE + "," + XML_MAX_ACTION_LEVEL_SEQUENCE + "," +
                                            XML_CONVEYANCE_STATUS + "," + XML_CR_EMPLOYEE_ID + "," + XML_ERFS_REQUEST_FLOW_ID + "," + XML_APPROVED_ADVANCE_AMOUNT;

                                    Log.e("XML DATA", xmlData);
                                    Log.e("Token No.", str);

                                }

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.dialogbox_approval_toggle);
                                dialog.setTitle("Approval");
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);

                                CustomTextView txt_ApprovalToggleTitle, edt_comment_dialog;
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
                                txt_header.setText("Travel Approve/Reject");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        comment = edt_comment.getText().toString().trim();
                                        if (comment.equals("")) {
                                            Utilities.showDialog(coordinatorLayout, "Please Enter Comment");
                                        } else {
                                            sendTravelApprovalRequest(comment, tg_text, xmlData);
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

                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                ////////////===============

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return view;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TravelApprovalFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TravelApprovalFragment.ClickListener clickListener) {
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

    public void getTravelApprovalStatus(){
        try {
            arlData = new ArrayList<HashMap<String,String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetTravelRequestDetail";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", empoyeeId);

//            pm.put("objConveyanceVoucherRequestStatusInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array "+ " Length = "+len+" Value = " +response.toString() );
                            Log.e("Value"," Length = "+len+" Value = " +response.toString());

                            pDialog.dismiss();

                            try{
                                HashMap<String, String> mapData;
                                JSONArray jsonArray = response.getJSONArray("GetTravelRequestDetailResult");

                                Log.d("Result",jsonArray.toString());
                                Log.d("Result Length",""+jsonArray.length());
//
                                for(int i=0;i<jsonArray.length();i++){
                                    mapData = new HashMap<String, String>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String TOKEN_NO = explrObject.getString("TOKEN_NO");
                                    String ERFS_REQUEST_ID = explrObject.getString("ERFS_REQUEST_ID");
                                    String REQUEST_STATUS_ID = explrObject.getString("REQUEST_STATUS_ID");
                                    String ERFS_ACTION_LEVEL_SEQUENCE = explrObject.getString("ERFS_ACTION_LEVEL_SEQUENCE");
                                    String MAX_ACTION_LEVEL_SEQUENCE = explrObject.getString("MAX_ACTION_LEVEL_SEQUENCE");
                                    String ERFS_REQUEST_STATUS = explrObject.getString("ERFS_REQUEST_STATUS");
                                    String ERFS_ACTION_BY = explrObject.getString("ERFS_ACTION_BY");
                                    String TR_EMPLOYEE_ID = explrObject.getString("TR_EMPLOYEE_ID");
                                    String EMPLOYEE_CODE = explrObject.getString("EMPLOYEE_CODE");
                                    String EMPLOYEE_NAME = explrObject.getString("EMPLOYEE_NAME");
                                    String B_BRANCH_NAME = explrObject.getString("B_BRANCH_NAME");
                                    String D_DEPARTMENT_NAME = explrObject.getString("D_DEPARTMENT_NAME");
                                    String D_DESIGNATION = explrObject.getString("D_DESIGNATION");
                                    String TR_TRAVEL_TYPE_ID = explrObject.getString("TR_TRAVEL_TYPE_ID");
                                    String TT_TRAVEL_TYPE_NAME = explrObject.getString("TT_TRAVEL_TYPE_NAME");
                                    String TR_START_DATE = explrObject.getString("TR_START_DATE");
                                    String TR_START_DATE_1 = explrObject.getString("TR_START_DATE_1");
                                    String TR_RETURN_DATE = explrObject.getString("TR_RETURN_DATE");
                                    String TR_RETURN_DATE_1 = explrObject.getString("TR_RETURN_DATE_1");
                                    String TR_ESTIMATED_COST = explrObject.getString("TR_ESTIMATED_COST");
                                    String TR_ADVANCE_AMOUNT = explrObject.getString("TR_ADVANCE_AMOUNT");
                                    String TR_REMARKS = explrObject.getString("TR_REMARKS");
                                    String TR_APPROVED_ADVANCE_AMOUNT = explrObject.getString("TR_APPROVED_ADVANCE_AMOUNT");
                                    String REQUESTED_BY = explrObject.getString("REQUESTED_BY");
                                    String REQUESTED_DATE = explrObject.getString("REQUESTED_DATE");
                                    String REQUEST_STATUS = explrObject.getString("REQUEST_STATUS");
                                    String ACTION_BY = explrObject.getString("ACTION_BY");
                                    String ACTION_DATE = explrObject.getString("ACTION_DATE");
                                    String CANCEL_REASON = explrObject.getString("CANCEL_REASON");
                                    String CANCEL_REQUEST_BY = explrObject.getString("CANCEL_REQUEST_BY");
                                    String CANCEL_REQUEST_DATE = explrObject.getString("CANCEL_REQUEST_DATE");
                                    String CANCEL_ACTION_BY = explrObject.getString("CANCEL_ACTION_BY");
                                    String CANCEL_ACTION_DATE = explrObject.getString("CANCEL_ACTION_DATE");
                                    String MODIFIED_BY = explrObject.getString("MODIFIED_BY");
                                    String MODIFIED_DATE = explrObject.getString("MODIFIED_DATE");
                                    String TRAVEL_STATUS = explrObject.getString("TRAVEL_STATUS");
                                    String TRAVEL_STATUS_1 = explrObject.getString("TRAVEL_STATUS_1");
                                    String ERFS_REQUEST_STATUS_NAME = explrObject.getString("ERFS_REQUEST_STATUS_NAME");
                                    String ERFS_ACTION_DATE = explrObject.getString("ERFS_ACTION_DATE");
                                    String ERFS_REQUEST_FLOW_ID = explrObject.getString("ERFS_REQUEST_FLOW_ID");
                                    String R_TYPE = explrObject.getString("R_TYPE");

                                    mapData.put("TOKEN_NO",TOKEN_NO);
                                    mapData.put("ERFS_REQUEST_ID",ERFS_REQUEST_ID);
                                    mapData.put("REQUEST_STATUS_ID",REQUEST_STATUS_ID);
                                    mapData.put("ERFS_ACTION_LEVEL_SEQUENCE",ERFS_ACTION_LEVEL_SEQUENCE);
                                    mapData.put("MAX_ACTION_LEVEL_SEQUENCE",MAX_ACTION_LEVEL_SEQUENCE);
                                    mapData.put("ERFS_REQUEST_STATUS",ERFS_REQUEST_STATUS);
                                    mapData.put("ERFS_ACTION_BY",ERFS_ACTION_BY);
                                    mapData.put("TR_EMPLOYEE_ID",TR_EMPLOYEE_ID);
                                    mapData.put("EMPLOYEE_CODE",EMPLOYEE_CODE);
                                    mapData.put("EMPLOYEE_NAME",EMPLOYEE_NAME);
                                    mapData.put("B_BRANCH_NAME",B_BRANCH_NAME);
                                    mapData.put("D_DEPARTMENT_NAME",D_DEPARTMENT_NAME);
                                    mapData.put("D_DESIGNATION",D_DESIGNATION);
                                    mapData.put("TR_TRAVEL_TYPE_ID",TR_TRAVEL_TYPE_ID);
                                    mapData.put("TT_TRAVEL_TYPE_NAME",TT_TRAVEL_TYPE_NAME);
                                    mapData.put("TR_START_DATE",TR_START_DATE);
                                    mapData.put("TR_START_DATE_1",TR_START_DATE_1);
                                    mapData.put("TR_RETURN_DATE",TR_RETURN_DATE);
                                    mapData.put("TR_RETURN_DATE_1",TR_RETURN_DATE_1);
                                    mapData.put("TR_ESTIMATED_COST",TR_ESTIMATED_COST);
                                    mapData.put("TR_ADVANCE_AMOUNT",TR_ADVANCE_AMOUNT);
                                    mapData.put("TR_REMARKS",TR_REMARKS);
                                    mapData.put("TR_APPROVED_ADVANCE_AMOUNT",TR_APPROVED_ADVANCE_AMOUNT);
                                    mapData.put("REQUESTED_BY",REQUESTED_BY);
                                    mapData.put("REQUESTED_DATE",REQUESTED_DATE);
                                    mapData.put("REQUEST_STATUS",REQUEST_STATUS);
                                    mapData.put("ACTION_BY",ACTION_BY);
                                    mapData.put("ACTION_DATE",ACTION_DATE);
                                    mapData.put("CANCEL_REASON",CANCEL_REASON);
                                    mapData.put("CANCEL_REQUEST_BY",CANCEL_REQUEST_BY);
                                    mapData.put("CANCEL_REQUEST_DATE",CANCEL_REQUEST_DATE);
                                    mapData.put("CANCEL_ACTION_BY",CANCEL_ACTION_BY);
                                    mapData.put("CANCEL_ACTION_DATE",CANCEL_ACTION_DATE);
                                    mapData.put("MODIFIED_BY",MODIFIED_BY);
                                    mapData.put("MODIFIED_DATE",MODIFIED_DATE);
                                    mapData.put("TRAVEL_STATUS",TRAVEL_STATUS);
                                    mapData.put("TRAVEL_STATUS_1",TRAVEL_STATUS_1);
                                    mapData.put("ERFS_REQUEST_STATUS_NAME",ERFS_REQUEST_STATUS_NAME);
                                    mapData.put("ERFS_ACTION_DATE",ERFS_ACTION_DATE);
                                    mapData.put("ERFS_REQUEST_FLOW_ID",ERFS_REQUEST_FLOW_ID);
                                    mapData.put("R_TYPE",R_TYPE);

                                    arlData.add(mapData);

                                }
                                if(jsonArray.length()<=0){
                                    recyclerView.setAdapter(null);
                                    Utilities.showDialog(coordinatorLayout,"Data Not Found.");
                                    recyclerView.setVisibility(View.GONE);
                                    linearNoDataFound.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    linearNoDataFound.setVisibility(View.GONE);
                                    Log.e("ArrayList 1", arlData.toString());
                                    System.out.println("ArrayList==" + arlData);
                                    mAdapter = new ApprovalTravalAdapter("Travel", getActivity(), coordinatorLayout, arlData);
                                    recyclerView.setAdapter(mAdapter);
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error",""+error.getMessage());
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,  String> params = new HashMap<String, String>();
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


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendTravelApprovalRequest(String comment, String toggletext, String xmlData){
        try {
            arlData = new ArrayList<HashMap<String,String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ProcessTravelRequest";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", empoyeeId);
            params_final.put("STATUS", toggletext);
            params_final.put("COMMENT", comment);
            params_final.put("XMLDATA", xmlData);

            pm.put("objProcessRequestInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array "+ " Length = "+len+" Value = " +response.toString() );
                            Log.e("Value"," Length = "+len+" Value = " +response.toString());

                            pDialog.dismiss();

                            try{
                                String result = response.getString("ProcessTravelRequestResult");

                                int res = Integer.valueOf(result);
                                if (res > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Travel Request processed sucessfully.");
                                    getTravelApprovalStatus();
                                } else if (res == 0) {
                                    Utilities.showDialog(coordinatorLayout, "Error during processing of Travel Request.");
                                    getTravelApprovalStatus();
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error",""+error.getMessage());
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


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
