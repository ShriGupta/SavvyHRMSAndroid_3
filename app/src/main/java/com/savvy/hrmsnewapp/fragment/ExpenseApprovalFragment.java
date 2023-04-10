package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import com.savvy.hrmsnewapp.adapter.ApprovalExpenceDetailAdapter;
import com.savvy.hrmsnewapp.adapter.StatusExpenseApprovalAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ExpenseApprovalFragment extends BaseFragment {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared,shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "";
    ApprovalExpenceDetailAdapter mAdapter;
    StatusExpenseApprovalAdapter mAdapter1;

    RecyclerView recyclerView, recyclerViewDetail;
    String token_no = "";
    ArrayList<HashMap<String,String>> arlData, arlData1, arlDataConveyanceId;

//    CustomTextView txt_ErrorData;
    String employeeId = "",requestId = "", ERFS_REQUEST_ID1 = "";
    Button btn_closeDetail, btn_saveDetail;
    EditText edt_approved_Amt;
    HashMap<Integer,String> mapdata2;


    CustomTextView txt_ApproveAction, txt_ApproveToken, txt_ApproveModify;
    String XML_ERFS_REQUEST_ID = "", XML_REQUEST_STATUS_ID = "", XML_ERFS_ACTION_LEVEL_SEQUENCE = "",
            XML_MAX_ACTION_LEVEL_SEQUENCE = "", XML_EXPENSE_STATUS = "",
            XML_ER_EMPLOYEE_ID = "", XML_ERFS_REQUEST_FLOW_ID = "";

    String   CRD_CONVEYANCE_REQUEST_DETAIL_ID1 = "", CRD_CONVEYANCE_TYPE1 = "",
            CRD_APPROVED_AMOUNT1 = "";

    String approved_value = "";

    HashMap<Integer,String> mapDataXml;
    String CRD_CONVEYANCE_REQUEST_DETAIL_ID = "", CRD_CONVEYANCE_TYPE = "";
    String xmlData = "", tg_text = "true", comment = "", xmlData1="@";
    String conveyanceId = "";

    Handler handler;
    Runnable rRunnable;

    Handler handler1;
    Runnable runnable1;
    StringBuilder expenseStringBuilder;
    HashMap<String, String> mapdata;
    CustomTextView txtDataNotFound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        arlData = new ArrayList<HashMap<String,String>>();
        arlData1 = new ArrayList<HashMap<String,String>>();
        arlDataConveyanceId = new ArrayList<HashMap<String,String>>();
        mapDataXml = new HashMap<>();

        mapdata2 = new HashMap<>();


        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));

//        getConveyanceForVoucher();

        if(Utilities.isNetworkAvailable(getActivity())){
            getExpenseApprovalStatus();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expense_approval, container, false);

        recyclerView = view.findViewById(R.id.recyclerExpenseApproval);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        txtDataNotFound = view.findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.addOnItemTouchListener(new ExpenseApprovalFragment.RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);

                    txt_ApproveModify = view.findViewById(R.id.txt_conApproveModify);
                    txt_ApproveAction = view.findViewById(R.id.txt_conAction);
                    txt_ApproveToken = view.findViewById(R.id.txt_conApproveToken);


                    txt_ApproveModify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            approved_value = "";
                            token_no = txt_ApproveToken.getText().toString();
//
                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {

                                ERFS_REQUEST_ID1 = mapdata.get("ERFS_REQUEST_ID");

                            }
                            final Dialog dialog = new Dialog(getActivity());

                            dialog.setTitle("Conveyance Detail");
                            dialog.setContentView(R.layout.approval_detail_main_row);
                            dialog.setCancelable(false);

                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            recyclerViewDetail = dialog.findViewById(R.id.recyclerDetailVoucher);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerViewDetail.setLayoutManager(mLayoutManager);
                            recyclerViewDetail.setItemAnimator(new DefaultItemAnimator());

                            btn_closeDetail = dialog.findViewById(R.id.btn_closeDetailVoucher);
                            btn_saveDetail = dialog.findViewById(R.id.btn_saveDetailVoucher);
                            edt_approved_Amt = dialog.findViewById(R.id.EAEDetailAmt);

                            mAdapter1 = new StatusExpenseApprovalAdapter("EXPENSE_DETAIL_EDIT", getActivity(), coordinatorLayout, arlData1);

                            getExpenseVoucherDetail(ERFS_REQUEST_ID1);

                            try {
                                recyclerViewDetail.addOnItemTouchListener(new ConveyanceApprovalFragment.RecyclerTouchListener(getActivity(), recyclerViewDetail, new ConveyanceApprovalFragment.ClickListener() {
                                    @Override
                                    public void onClick(View view, int position1) {
                                        try {
                                            final int pos_final = position1;

                                            edt_approved_Amt = view.findViewById(R.id.EAEVoucher_appAmt);

                                            edt_approved_Amt.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                    approved_value = edt_approved_Amt.getText().toString().trim();
                                                    mapdata2.put(pos_final, approved_value);
                                                    Log.d("Before Text", "Change " + mapdata2.toString());
                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    approved_value = edt_approved_Amt.getText().toString().trim();
                                                    mapdata2.put(pos_final, approved_value);
                                                    Log.d("OnText Text", "Change " + mapdata2.toString());
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                    approved_value = edt_approved_Amt.getText().toString().trim();
                                                    mapdata2.put(pos_final, approved_value);
                                                    Log.d("After Text", "Change " + mapdata2.toString());
                                                }
                                            });

                                            Log.e("MapData Value", mapdata2.get(0));
                                            Log.e("Approved Amt Value", approved_value);
                                            handler1 = new Handler();
                                            runnable1 = new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (arlData1.size() != 0) {
                                                        try {
                                                            Log.e("Size New ", "" + arlData1.size());
                                                            for (int j = 0; j < arlData1.size(); j++) {

                                                                approved_value = edt_approved_Amt.getText().toString().trim();
                                                                mapdata2.put(pos_final, approved_value);
                                                                Log.d("Last Text", "Change " + mapdata2.toString());

                                                            }
                                                        } catch (Exception e) {
                                                            e.getMessage();
                                                        }
                                                    } else {
                                                        handler1.postDelayed(runnable1, 200);
                                                    }
                                                }
                                            };
                                            handler1.postDelayed(runnable1, 2000);

                                        } catch (Exception e) {
                                            e.getMessage();
                                        }
                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {

                                    }
                                }));
                            } catch (Exception e) {
                                e.getMessage();
                            }
                            btn_closeDetail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            btn_saveDetail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (approved_value.equals("")) {
                                        for (int j = 0; j < Constants.hashMap2.size(); j++) {
                                            mapdata2.put(j, Constants.hashMap2.get(j));
                                        }
                                    }

                                    saveConveyanceXmlData();
                                    Log.e("Request Id ", "" + ERFS_REQUEST_ID1);
                                    Log.e("Xml FData", xmlData1 + " value " + expenseStringBuilder.toString());
                                    SaveExpenseDetailsChangesInfo(ERFS_REQUEST_ID1, expenseStringBuilder.toString());

                                    Log.e("Request Id ", "" + ERFS_REQUEST_ID1);
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();

                        }
                    });

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
                                    XML_EXPENSE_STATUS = mapdata.get("EXPENSE_STATUS");
                                    XML_ER_EMPLOYEE_ID = mapdata.get("ER_EMPLOYEE_ID");
                                    XML_ERFS_REQUEST_FLOW_ID = mapdata.get("ERFS_REQUEST_FLOW_ID");

                                    xmlData = XML_ERFS_REQUEST_ID + "," + XML_REQUEST_STATUS_ID + "," + XML_ERFS_ACTION_LEVEL_SEQUENCE + "," + XML_MAX_ACTION_LEVEL_SEQUENCE + "," +
                                            XML_EXPENSE_STATUS + "," + XML_ER_EMPLOYEE_ID + "," + XML_ERFS_REQUEST_FLOW_ID;

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
                                txt_header.setText("Expense Approve/Reject");

                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        comment = edt_comment.getText().toString().trim();
                                        if (comment.equals("")) {
                                            Utilities.showDialog(coordinatorLayout, "Please Enter Comment");
                                        } else {
                                            sendExpenseApprovalRequest(comment, tg_text, xmlData);
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
        return view;
    }

    public void saveConveyanceXmlData() {

        expenseStringBuilder = new StringBuilder();
        for(int i=0;i<arlData1.size();i++) {
            mapdata = arlData1.get(i);
            expenseStringBuilder.append("@");
            expenseStringBuilder.append(mapdata.get("ERD_EXPENSE_REQUEST_DETAIL_ID"));
            expenseStringBuilder.append(","+mapdata.get("ERD_EXPENSE_TYPE"));
            expenseStringBuilder.append(","+mapdata.get("ERD_EXPENSE_SUB_TYPE"));
            expenseStringBuilder.append(","+mapdata2.get(i));

            Log.e("String Builder",expenseStringBuilder.toString());

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ExpenseApprovalFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ExpenseApprovalFragment.ClickListener clickListener) {
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

    public void getExpenseVoucherDetail(String EXPENSE_ID){
        try {
//            final RecyclerView recyclerView12 = recyclerViewDetail;
            arlData1 = new ArrayList<HashMap<String,String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetEmployeeExpenseDetail";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EXPENSE_ID", EXPENSE_ID);

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
                                JSONArray jsonArray = response.getJSONArray("GetEmployeeExpenseDetailResult");

                                Log.d("Result",jsonArray.toString());
//
                                for(int i=0;i<jsonArray.length();i++){
                                    mapData = new HashMap<String, String>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String ERD_APPROVED_AMOUNT=explrObject.getString("ERD_APPROVED_AMOUNT");
                                    String ERD_BILL_AMOUNT=explrObject.getString("ERD_BILL_AMOUNT");
                                    String ERD_BILL_DATE=explrObject.getString("ERD_BILL_DATE");
                                    String ERD_BILL_DATE_1=explrObject.getString("ERD_BILL_DATE_1");
                                    String ERD_BILL_NO=explrObject.getString("ERD_BILL_NO");
                                    String ERD_EXPENSE_REQUEST_DETAIL_ID=explrObject.getString("ERD_EXPENSE_REQUEST_DETAIL_ID");
                                    String ERD_EXPENSE_REQUEST_ID=explrObject.getString("ERD_EXPENSE_REQUEST_ID");
                                    String ERD_EXPENSE_SUB_TYPE=explrObject.getString("ERD_EXPENSE_SUB_TYPE");
                                    String ERD_EXPENSE_TYPE=explrObject.getString("ERD_EXPENSE_TYPE");
                                    String ERD_REASON=explrObject.getString("ERD_REASON");
                                    String EST_EXPENSE_SUB_TYPE=explrObject.getString("EST_EXPENSE_SUB_TYPE");
                                    String ET_EXPENSE_TYPE=explrObject.getString("ET_EXPENSE_TYPE");


//                                    mapData.put("CRD_CONVEYANCE_REQUEST_DETAIL_ID",CRD_CONVEYANCE_REQUEST_DETAIL_ID1);
//                                    mapData.put("CRD_CONVEYANCE_TYPE",CRD_CONVEYANCE_TYPE1);
                                    mapData.put("ERD_APPROVED_AMOUNT",ERD_APPROVED_AMOUNT);
                                    mapData.put("ERD_BILL_AMOUNT",ERD_BILL_AMOUNT);
                                    mapData.put("ERD_BILL_DATE",ERD_BILL_DATE);
                                    mapData.put("ERD_BILL_DATE_1",ERD_BILL_DATE_1);
                                    mapData.put("ERD_BILL_NO",ERD_BILL_NO);
                                    mapData.put("ERD_EXPENSE_REQUEST_DETAIL_ID",ERD_EXPENSE_REQUEST_DETAIL_ID);
                                    mapData.put("ERD_EXPENSE_REQUEST_ID",ERD_EXPENSE_REQUEST_ID);
                                    mapData.put("ERD_EXPENSE_SUB_TYPE",ERD_EXPENSE_SUB_TYPE);
                                    mapData.put("ERD_EXPENSE_TYPE",ERD_EXPENSE_TYPE);
                                    mapData.put("ERD_REASON",ERD_REASON);
                                    mapData.put("EST_EXPENSE_SUB_TYPE",EST_EXPENSE_SUB_TYPE);
                                    mapData.put("ET_EXPENSE_TYPE",ET_EXPENSE_TYPE);

                                    arlData1.add(mapData);

                                }
                                Log.e("ArrayList 1",arlData1.toString());
                                System.out.println("ArrayList==" + arlData1);
                                mAdapter1 = new StatusExpenseApprovalAdapter("EXPENSE_DETAIL_EDIT",getActivity(),coordinatorLayout,arlData1);
////                                mAdapter = new StatusConveyanceVoucherAdapter("CONVEYANCE_VOUCHER_DETAIL",getActivity(),coordinatorLayout,arlData1);
                                recyclerViewDetail.setAdapter(mAdapter1);
//                                mAdapter = new CalendarAdapter(getActivity(),month,arlData);
//                                gridview.setAdapter(adapter);
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

    public void getExpenseApprovalStatus(){
        try {
            arlData = new ArrayList<HashMap<String,String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetExpenseRequestDetail";
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
                                JSONArray jsonArray = response.getJSONArray("GetExpenseRequestDetailResult");

                                Log.d("Result",jsonArray.toString());
//
                                for(int i=0;i<jsonArray.length();i++){
                                    mapData = new HashMap<String, String>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);
//
                                    String TOKEN_NO = explrObject.getString("TOKEN_NO");
                                    String ERFS_REQUEST_ID = explrObject.getString("ERFS_REQUEST_ID");
                                    String REQUEST_STATUS_ID = explrObject.getString("REQUEST_STATUS_ID");
                                    String ERFS_ACTION_LEVEL_SEQUENCE = explrObject.getString("ERFS_ACTION_LEVEL_SEQUENCE");
                                    String MAX_ACTION_LEVEL_SEQUENCE = explrObject.getString("MAX_ACTION_LEVEL_SEQUENCE");
                                    String ERFS_REQUEST_STATUS = explrObject.getString("ERFS_REQUEST_STATUS");
                                    String ERFS_ACTION_BY = explrObject.getString("ERFS_ACTION_BY");
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
                                    String REQUESTED_DATE = explrObject.getString("REQUESTED_DATE");
                                    String REQUEST_STATUS = explrObject.getString("REQUEST_STATUS");
                                    String ACTION_BY = explrObject.getString("ACTION_BY");
//                                    String TR_START_DATE_1 = explrObject.getString("TR_START_DATE_1");
//                                    String TR_RETURN_DATE = explrObject.getString("TR_RETURN_DATE");
//                                    String TR_RETURN_DATE_1 = explrObject.getString("TR_RETURN_DATE_1");
//                                    String TR_ESTIMATED_COST = explrObject.getString("TR_ESTIMATED_COST");
//                                    String TR_ADVANCE_AMOUNT = explrObject.getString("TR_ADVANCE_AMOUNT");
//                                    String TR_REMARKS = explrObject.getString("TR_REMARKS");
//                                    String TR_APPROVED_ADVANCE_AMOUNT = explrObject.getString("TR_APPROVED_ADVANCE_AMOUNT");
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
                                    mapData.put("ER_EMPLOYEE_ID",ER_EMPLOYEE_ID);
                                    mapData.put("EMPLOYEE_CODE",EMPLOYEE_CODE);
                                    mapData.put("EMPLOYEE_NAME",EMPLOYEE_NAME);
                                    mapData.put("B_BRANCH_NAME",B_BRANCH_NAME);
                                    mapData.put("D_DEPARTMENT_NAME",D_DEPARTMENT_NAME);
                                    mapData.put("D_DESIGNATION",D_DESIGNATION);
                                    mapData.put("ER_REMARKS",ER_REMARKS);
                                    mapData.put("ER_REQUESTED_AMOUNT",ER_REQUESTED_AMOUNT);
                                    mapData.put("ER_APPROVED_AMOUNT",ER_APPROVED_AMOUNT);
//                                    mapData.put("TR_START_DATE_1",TR_START_DATE_1);
//                                    mapData.put("TR_RETURN_DATE",TR_RETURN_DATE);
//                                    mapData.put("TR_RETURN_DATE_1",TR_RETURN_DATE_1);
//                                    mapData.put("TR_ESTIMATED_COST",TR_ESTIMATED_COST);
//                                    mapData.put("TR_ADVANCE_AMOUNT",TR_ADVANCE_AMOUNT);
//                                    mapData.put("TR_REMARKS",TR_REMARKS);
//                                    mapData.put("TR_APPROVED_ADVANCE_AMOUNT",TR_APPROVED_ADVANCE_AMOUNT);
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
                                    mapData.put("EXPENSE_STATUS",EXPENSE_STATUS);
                                    mapData.put("EXPENSE_STATUS_1",EXPENSE_STATUS_1);
                                    mapData.put("ERFS_REQUEST_STATUS_NAME",ERFS_REQUEST_STATUS_NAME);
                                    mapData.put("ERFS_ACTION_DATE",ERFS_ACTION_DATE);
                                    mapData.put("ERFS_REQUEST_FLOW_ID",ERFS_REQUEST_FLOW_ID);
                                    mapData.put("R_TYPE",R_TYPE);

                                    arlData.add(mapData);

                                }
                                if(jsonArray.length()<=0){
                                    recyclerView.setAdapter(null);
                                    recyclerView.setVisibility(View.GONE);
                                    txtDataNotFound.setVisibility(View.VISIBLE);
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                } else {
                                    Log.e("ArrayList 1", arlData.toString());
                                    System.out.println("ArrayList==" + arlData);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    txtDataNotFound.setVisibility(View.GONE);
                                    mAdapter = new ApprovalExpenceDetailAdapter(getActivity(), arlData);
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

    public void SaveExpenseDetailsChangesInfo(String requestId, String expenseDetails){
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveExpensedetailsChanges";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", empoyeeId);
            params_final.put("REQUEST_ID", requestId);
            params_final.put("EXPENSE_DETAIL", expenseDetails);

            pm.put("objSaveExpensedetailsChangesInfo",params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                String result = response.getString("SaveExpensedetailsChangesResult");

                                int res = Integer.parseInt(result);
                                if(res>0){
                                    Utilities.showDialog(coordinatorLayout,"Expense details updated successfully.");
                                    getExpenseApprovalStatus();
                                } else if(res==0){
                                    Utilities.showDialog(coordinatorLayout,"Error during updating Expense details.");
                                }
//                                getConveyanceApprovalStatus();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d("Error", "" + error.getMessage());
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

    public void sendExpenseApprovalRequest(String comment, String toggletext, String xmlData){
        try {
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ProcessExpenseRequest";
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
                                String result = response.getString("ProcessExpenseRequestResult");

                                int res = Integer.valueOf(result);
                                if (res > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Expense Request processed sucessfully.");
                                    getExpenseApprovalStatus();
                                } else if (res == 0) {
                                    Utilities.showDialog(coordinatorLayout, "Error during processing of Expense Request.");
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
