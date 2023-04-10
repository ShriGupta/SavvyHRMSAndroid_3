package com.savvy.hrmsnewapp.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.ConveyanceCustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.TravelVoucherAdapter;
import com.savvy.hrmsnewapp.adapter.TravelVoucherExpenseAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleyMultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class TravelVoucherRequestFragment extends BaseFragment {

    TravelVoucherAdapter mAdapter;

    JsonObjectRequest jsonObjectRequest;
    RequestQueue requestQueue;

    Button btn_fileUpload;
    CustomTextView nofileChosen;
    ImageView uploadimgIcon;
    AlertDialog alertD;
    String displayFileName = "";
    String actualFileName = "";
    ProgressDialog progressDialog;

    int PICK_IMAGE_REQUEST = 1;

    Button btn_back_voucher;
    SharedPreferences shared, shareData;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "";
    String token_no = "";
    RecyclerView recyclerView, recyclerViewDetail;
    Button btn_closeDetail;

    CustomTextView txt_ConEdit, txt_ConDetail, txt_ConToken_no, txt_ConCancel, txt_ConAction, txt_TRVExpense;

    String employeeId = "", requestId = "", convenyanceId = "";
    String comment = "", xmlData = "";

    ConveyanceCustomSpinnerAdapter mAdapter1;

    //Dialog for Travel Expense
    static String policyId = "";
    String EST_IS_BILL_DATE_REQUIRED = "";
    String EST_IS_BILL_NO_REQUIRED = "";

    Button btn_showTravelDetail, close_travel_detail, btn_TREBillDate;
    LinearLayout linear_travel_detail;
    Button btn_TREclose, btn_TREclear, btn_TREsubmit;
    Spinner TRE_Spin_ExpenseType;
    EditText edt_TREBillAmount, edt_TREBillNo, TRE_remarks;
    CheckBox check_companyProvided;
    RecyclerView recycler_new_travelExpense, recycler_add_travel_expense;
    String travel_id = "", Travel_Type = "", JourneyStartDate = "", JourneyReturnDate = "", JourneyStartDate_1 = "",
            JourneyReturnDate_1 = "";
    CustomTextView TRDTravelType, TRDStartDate, TRDReturnDate;
    CustomTextView TRE_Remove;

    //Title of Expense type
    CustomTextView TRE_remarksTitle, edt_TREBillAmountTitle, btn_TREBillDateTitle, edt_TREBillNoTitle, TRE_Spin_ExpenseTypeTitle;

    TravelVoucherExpenseAdapter travelVoucherExpenseAdapter;
    String positionValue = "", positionId = "";
    String TRAVEL_REQUEST_ID = "", EXPENSE_ID = "";

    LinearLayout linearErrorResultText;
    CustomTextView errorResultText, txtDataNotFound;
    Handler handler;
    Runnable rRunnable;
    Runnable runnable;
    String BillDate = "";

    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData, arlData1, arlDataExpenseBillReq;
    ArrayList<HashMap<String, String>> arlDataTravelExpense, arlDataJourneyDetail, arlDataTravelExpenseType;
    ArrayList<HashMap<String, String>> arlDataTravelExpensep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arlData = new ArrayList<HashMap<String, String>>();
        arlData1 = new ArrayList<HashMap<String, String>>();
        arlDataTravelExpense = new ArrayList<HashMap<String, String>>();
        arlDataJourneyDetail = new ArrayList<HashMap<String, String>>();
        arlDataTravelExpenseType = new ArrayList<HashMap<String, String>>();
        arlDataExpenseBillReq = new ArrayList<HashMap<String, String>>();
        arlDataTravelExpensep = new ArrayList<HashMap<String, String>>();

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        employeeId = (shared.getString("EmpoyeeId", ""));

        getTravelForVoucher();
        getTravelPolicyId();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_voucher_request, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_back_voucher = getActivity().findViewById(R.id.btn_voucher_back);

        recyclerView = getActivity().findViewById(R.id.travelVoucherApprovRecycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.addOnItemTouchListener(new VoucherRequestRequest.RecyclerTouchListener(getActivity(), recyclerView, new VoucherRequestRequest.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_ConAction = view.findViewById(R.id.txt_TRVAction);
                    txt_TRVExpense = view.findViewById(R.id.txt_TRVExpense);
                    txt_ConToken_no = view.findViewById(R.id.txt_TRVoucherToken);

                    txt_ConAction.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            token_no = txt_ConToken_no.getText().toString();
                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {
                                String request_Id = mapdata.get("ERFS_REQUEST_ID");
                                TRAVEL_REQUEST_ID= mapdata.get("TR_TRAVEL_REQUEST_ID");


//
                            }
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.dialogbox_approval_toggle);
                            dialog.setTitle("Voucher Request");
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            LinearLayout toggleLayout = dialog.findViewById(R.id.toggleLayout);
//                            CustomTextView txt_header = (CustomTextView)dialog.findViewById(R.id.dialog_header);
//                            CustomTextView comment_dialog = (CustomTextView)dialog.findViewById(R.id.edt_comment_dialog);
                            Button btn_ApproveGo, btn_close;
                            btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                            btn_close = dialog.findViewById(R.id.btn_close);

                            toggleLayout.setVisibility(View.GONE);

                            CustomTextView txt_ApprovalToggleTitle, edt_comment_dialog;
                            txt_ApprovalToggleTitle = dialog.findViewById(R.id.txt_ApprovalToggleTitle);
                            edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);

                            String str1 = "<font color='#EE0000'>*</font>";
                            txt_ApprovalToggleTitle.setText(Html.fromHtml("Type " + str1));
                            edt_comment_dialog.setText(Html.fromHtml("Remarks " + str1));

                            final EditText edt_comment = dialog.findViewById(R.id.edt_approve_comment);

                            String TravelTitle = "<font color=\"#277ddb\"><bold><u>" + "Travel Voucher Request" + "</u></bold></font>";
                            CustomTextView txt_voucher_title = dialog.findViewById(R.id.dialog_header);
                            txt_voucher_title.setText(Html.fromHtml(TravelTitle));

                            txt_voucher_title.setText("Travel Voucher Request");
//                            comment_dialog.setText("Remarks *");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    comment = edt_comment.getText().toString().trim();
                                    if (comment.equals("")) {
                                        Utilities.showDialog(coordinatorLayout, "Please Enter Comment");
                                    } else {
                                        sendTravelVoucherRequest(TRAVEL_REQUEST_ID, comment);
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
                        }
                    });

                    txt_TRVExpense.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getExpenseTypeTravel();

                            token_no = txt_ConToken_no.getText().toString().trim();
                            String str = mapdata.get("TOKEN_NO");
                            Log.e("Travel", "Token No " + token_no + " New Token " + str);
                            if (token_no.equals(str)) {
                                String request_Id = mapdata.get("TR_TRAVEL_REQUEST_ID");
                                Travel_Type = mapdata.get("TT_TRAVEL_TYPE_NAME");
                                JourneyStartDate = mapdata.get("TR_START_DATE");
                                JourneyReturnDate = mapdata.get("TR_RETURN_DATE");

                                JourneyStartDate_1 = mapdata.get("TR_START_DATE_1");
                                JourneyReturnDate_1 = mapdata.get("TR_RETURN_DATE_1");

                                TRAVEL_REQUEST_ID = request_Id;

                            }
                            Log.e("Travel", "request_Id= " + TRAVEL_REQUEST_ID + " Start = " + JourneyStartDate + " Return = " + JourneyReturnDate);

                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.travel_expense_add_voucher);
                            dialog.setTitle("Voucher Request");
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(true);

                            CustomTextView travelExpenseTitle = dialog.findViewById(R.id.travelExpenseTitle);

                            String TravelExpenseTitle = "<font color=\"#277ddb\"><bold><u>" + "Travel Expense" + "</u></bold></font>";
                            travelExpenseTitle.setText(Html.fromHtml(TravelExpenseTitle));

                            btn_fileUpload = dialog.findViewById(R.id.btnVoucher_imgUpload);
                            nofileChosen = dialog.findViewById(R.id.txt_Voucher_NoFileChoose);
                            uploadimgIcon = dialog.findViewById(R.id.btnVoucher_imgIcon);

                            btn_showTravelDetail = dialog.findViewById(R.id.btn_showTravelDetail);
                            close_travel_detail = dialog.findViewById(R.id.close_travel_detail);
                            btn_TREsubmit = dialog.findViewById(R.id.btn_TREsubmit);
                            btn_TREclear = dialog.findViewById(R.id.btn_TREclear);
                            btn_TREclose = dialog.findViewById(R.id.btn_TREclose);
                            btn_TREBillDate = dialog.findViewById(R.id.btn_TREBillDate);

                            TRDTravelType = dialog.findViewById(R.id.TRDTravelType);
                            TRDStartDate = dialog.findViewById(R.id.TRDStartDate);
                            TRDReturnDate = dialog.findViewById(R.id.TRDReturnDate);

                            edt_TREBillAmount = dialog.findViewById(R.id.edt_TREBillAmount);
                            edt_TREBillNo = dialog.findViewById(R.id.edt_TREBillNo);
                            TRE_remarks = dialog.findViewById(R.id.TRE_remarks);

                            linearErrorResultText = dialog.findViewById(R.id.linearErrorResultText);
                            errorResultText = dialog.findViewById(R.id.errorResultText);

                            linearErrorResultText.setVisibility(View.GONE);

                            // Title Custom Text for travel_expense_add_voucher
                            TRE_remarksTitle = dialog.findViewById(R.id.TRE_remarksTitle);
                            edt_TREBillAmountTitle = dialog.findViewById(R.id.edt_TREBillAmountTitle);
                            btn_TREBillDateTitle = dialog.findViewById(R.id.btn_TREBillDateTitle);
                            edt_TREBillNoTitle = dialog.findViewById(R.id.TREBillNoTitle);
                            TRE_Spin_ExpenseTypeTitle = dialog.findViewById(R.id.TRE_ExpenseTypeTitle);


                            check_companyProvided = dialog.findViewById(R.id.check_companyProvided);

                            TRE_Spin_ExpenseType = dialog.findViewById(R.id.TRE_Spin_ExpenseType);

                            String str1 = "<font color='#EE0000'>*</font>";

                            TRE_Spin_ExpenseTypeTitle.setText(Html.fromHtml("Expense Type " + str1));
                            edt_TREBillNoTitle.setText(Html.fromHtml("Bill No."));
                            btn_TREBillDateTitle.setText(Html.fromHtml("Bill Date"));
                            edt_TREBillAmountTitle.setText(Html.fromHtml("Bill Amount " + str1));
                            TRE_remarksTitle.setText(Html.fromHtml("Remark " + str1));

                            recycler_new_travelExpense = dialog.findViewById(R.id.recycler_new_travelExpense);
                            recycler_add_travel_expense = dialog.findViewById(R.id.recycler_add_travel_expense);

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recycler_new_travelExpense.setLayoutManager(mLayoutManager);
                            recycler_new_travelExpense.setItemAnimator(new DefaultItemAnimator());

                            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
                            recycler_add_travel_expense.setLayoutManager(mLayoutManager1);
                            recycler_add_travel_expense.setItemAnimator(new DefaultItemAnimator());

                            getTravelExpense(TRAVEL_REQUEST_ID, recycler_new_travelExpense);

                            linear_travel_detail = dialog.findViewById(R.id.linear_travel_detail);


//                            recycler_new_travelExpense.addOnItemTouchListener(new TravelVoucherRequestFragment.RecyclerTouchListener(getActivity(), recycler_new_travelExpense, new TravelVoucherRequestFragment.ClickListener() {
//                                        @Override
//                                        public void onClick(View view, int position) {
//                                            final HashMap<String, String> mapdata1 = arlDataTravelExpensep.get(position);
//                                            TRE_Remove = (CustomTextView)view.findViewById(R.id.TRE_Remove);
////                                            String str = mapdata1.get("TOKEN_NO");
//
//                                            TRE_Remove.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    try {
//                                                        EXPENSE_ID = mapdata1.get("EXPENSE_ID");
//                                                        Log.e("EXPENSE_ID", EXPENSE_ID);
//                                                        sendDeleteTravelExpense(EXPENSE_ID,TRAVEL_REQUEST_ID);
//                                                    }catch (Exception e){
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            });
//
//
//                                        }
//                                        @Override
//                                        public void onLongClick(View view, int position) {
//
//                                        }
//                                    })
//                            );
                            TRE_Spin_ExpenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    try {
//                    String positionValue = statusarraylist.get(position + 1).get("EST_EXPENSE_SUB_TYPE_ID");
                                        if (position == 0) {
                                            positionValue = "";
                                            positionId = "";
                                        } else if (position > 0) {
                                            positionId = arlDataTravelExpenseType.get(position - 1).get("EST_EXPENSE_SUB_TYPE_ID");
                                            positionValue = arlDataTravelExpenseType.get(position - 1).get("EST_EXPENSE_SUB_TYPE");

                                            Log.e("Postion ", "Position of Spinner " + position);
                                            EST_IS_BILL_NO_REQUIRED = "";
                                            EST_IS_BILL_DATE_REQUIRED = "";

                                            getMyExpenseType(positionId);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            btn_showTravelDetail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    linear_travel_detail.setVisibility(View.VISIBLE);
                                    Log.e("Travel", "Travel Type = " + Travel_Type + " Start = " + JourneyStartDate + " Return = " + JourneyReturnDate);
                                    TRDTravelType.setText(Travel_Type);
                                    TRDStartDate.setText(JourneyStartDate);
                                    TRDReturnDate.setText(JourneyReturnDate);
                                    GetEmployeeJourneyDetail(travel_id, recycler_add_travel_expense);

                                }
                            });

                            close_travel_detail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    linear_travel_detail.setVisibility(View.GONE);
                                }
                            });

                            btn_TREBillDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    btn_TREBillDate.setText("");
                                    CalanderHRMS calanderHRMS = new CalanderHRMS(getActivity());
                                    calanderHRMS.datePicker(btn_TREBillDate);

                                    handler = new Handler();
                                    rRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                BillDate = btn_TREBillDate.getText().toString().trim().replace("/", "-");
//                                                travelEndDate = btn_TravelEnd.getText().toString().trim();
                                                if (!BillDate.equals("") && !JourneyStartDate.equals("") && !JourneyReturnDate.equals("")) {
                                                    if (Utilities.isNetworkAvailable(getActivity())) {
                                                        GetCompareDateResult(BillDate);
                                                    }
                                                } else {
                                                    handler.postDelayed(rRunnable, 1000);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Log.e("GetFuture Method ", e.getMessage());
                                            }
                                        }
                                    };
                                    handler.postDelayed(rRunnable, 1000);
                                }
                            });

                            btn_TREclose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            btn_TREclear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TRE_Spin_ExpenseType.setSelection(0);
                                    edt_TREBillAmount.setText("");
                                    edt_TREBillNo.setText("");
                                    TRE_remarks.setText("");
                                    btn_TREBillDate.setText("");
                                    check_companyProvided.setChecked(false);
                                    nofileChosen.setText("no file chosen");
                                    uploadimgIcon.setVisibility(View.GONE);
                                }
                            });

                            btn_fileUpload.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    selectFile();
                                }
                            });

                            btn_TREsubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    String ExpenseType = (String) TRE_Spin_ExpenseType.getSelectedItem();
                                    String BillNo = edt_TREBillNo.getText().toString().trim();
                                    String BillAmount = edt_TREBillAmount.getText().toString().trim();
                                    String BillDate = btn_TREBillDate.getText().toString().trim();
                                    String Remarks = TRE_remarks.getText().toString().trim();

                                    String chk_provide;
                                    String chk_provide1;
                                    if (check_companyProvided.isChecked()) {
                                        chk_provide = "true";
                                        chk_provide1 = "1";
                                    } else {
                                        chk_provide = "false";
                                        chk_provide1 = "0";
                                    }
                                    if (positionValue.equals("")) {
                                        Toast.makeText(getActivity(), "Please Select Expense Type.", Toast.LENGTH_LONG).show();
                                    } else if (BillNo.equals("")) {
                                        if (EST_IS_BILL_NO_REQUIRED.equals("True")) {
                                            Toast.makeText(getActivity(), "Please Enter Bill No.", Toast.LENGTH_LONG).show();
                                        }
                                    } else if (BillDate.equals("")) {
                                        if (EST_IS_BILL_DATE_REQUIRED.equals("True")) {
                                            Toast.makeText(getActivity(), "Please Enter Bill Date.", Toast.LENGTH_LONG).show();
                                        }
                                    } else if (BillAmount.equals("")) {
                                        Toast.makeText(getActivity(), "Please Enter Bill Amount.", Toast.LENGTH_LONG).show();
                                    } else if (Remarks.equals("")) {
                                        Toast.makeText(getActivity(), "Please Enter Reason.", Toast.LENGTH_LONG).show();
                                    } else {
                                        AddTravelExpenseVoucher(BillNo, BillDate, BillAmount, chk_provide, Remarks);
                                    }


                                }
                            });

                            dialog.show();
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

        btn_back_voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                TravelFragmentHolder travelFragmentHolder = new TravelFragmentHolder();
                transaction.replace(R.id.container_body, travelFragmentHolder);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TravelVoucherRequestFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TravelVoucherRequestFragment.ClickListener clickListener) {
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

    public void getTravelForVoucher() {
        try {
            arlData = new ArrayList<HashMap<String, String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelForVoucher";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", employeeId);

//            pm.put("objConveyanceVoucherRequestStatusInfo", params_final);

            requestQueue = Volley.newRequestQueue(getContext());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            pDialog.dismiss();

                            try {
                                HashMap<String, String> mapData;
                                JSONArray jsonArray = response.getJSONArray("GetTravelForVoucherResult");

                                Log.d("Result", jsonArray.toString());
//
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    mapData = new HashMap<String, String>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String TOKEN_NO = explrObject.getString("TOKEN_NO");
                                    String TR_TRAVEL_REQUEST_ID = explrObject.getString("TR_TRAVEL_REQUEST_ID");
                                    String TR_TRAVEL_REQUEST_ID_1 = explrObject.getString("TR_TRAVEL_REQUEST_ID_1");
                                    String TR_TRAVEL_TYPE_ID = explrObject.getString("TR_TRAVEL_TYPE_ID");
                                    String TT_TRAVEL_TYPE_NAME = explrObject.getString("TT_TRAVEL_TYPE_NAME");
                                    String TR_START_DATE = explrObject.getString("TR_START_DATE");
                                    String TR_RETURN_DATE = explrObject.getString("TR_RETURN_DATE");
                                    String TR_START_DATE_1 = explrObject.getString("TR_START_DATE_1");
                                    String TR_RETURN_DATE_1 = explrObject.getString("TR_RETURN_DATE_1");
                                    String TR_ESTIMATED_COST = explrObject.getString("TR_ESTIMATED_COST");
                                    String TR_ADVANCE_AMOUNT = explrObject.getString("TR_ADVANCE_AMOUNT");
                                    String TR_APPROVED_ADVANCE_AMOUNT = explrObject.getString("TR_APPROVED_ADVANCE_AMOUNT");
                                    String TR_REMARKS = explrObject.getString("TR_REMARKS");
                                    String TVRD_TRAVEL_REQUEST_ID = explrObject.getString("TVRD_TRAVEL_REQUEST_ID");
                                    String TVR_TRAVEL_VOUCHER_REQUEST_ID = explrObject.getString("TVR_TRAVEL_VOUCHER_REQUEST_ID");

//                                    TRAVEL_REQUEST_ID = TR_TRAVEL_REQUEST_ID;

                                    mapData.put("TOKEN_NO", TOKEN_NO);
                                    mapData.put("TR_TRAVEL_REQUEST_ID", TR_TRAVEL_REQUEST_ID);
                                    mapData.put("TR_TRAVEL_REQUEST_ID_1", TR_TRAVEL_REQUEST_ID_1);
                                    mapData.put("TR_TRAVEL_TYPE_ID", TR_TRAVEL_TYPE_ID);
                                    mapData.put("TT_TRAVEL_TYPE_NAME", TT_TRAVEL_TYPE_NAME);
                                    mapData.put("TR_START_DATE", TR_START_DATE);
                                    mapData.put("TR_RETURN_DATE", TR_RETURN_DATE);
                                    mapData.put("TR_START_DATE_1", TR_START_DATE_1);
                                    mapData.put("TR_RETURN_DATE_1", TR_RETURN_DATE_1);
                                    mapData.put("TR_ESTIMATED_COST", TR_ESTIMATED_COST);
                                    mapData.put("TR_ADVANCE_AMOUNT", TR_ADVANCE_AMOUNT);
                                    mapData.put("TR_APPROVED_ADVANCE_AMOUNT", TR_APPROVED_ADVANCE_AMOUNT);
                                    mapData.put("TR_REMARKS", TR_REMARKS);
                                    mapData.put("TVRD_TRAVEL_REQUEST_ID", TVRD_TRAVEL_REQUEST_ID);
                                    mapData.put("TVR_TRAVEL_VOUCHER_REQUEST_ID", TVR_TRAVEL_VOUCHER_REQUEST_ID);

                                    arlData.add(mapData);

                                }
                                if (jsonArray.length() <= 0) {
                                    recyclerView.setAdapter(null);
                                    recyclerView.setVisibility(View.GONE);
                                    txtDataNotFound.setVisibility(View.VISIBLE);
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                } else {
                                    Log.e("ArrayList 1", arlData.toString());
                                    System.out.println("ArrayList==" + arlData);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    txtDataNotFound.setVisibility(View.GONE);
                                    mAdapter = new TravelVoucherAdapter("TRAVEL_VOUCHER_REQUEST", getActivity(), arlData);
                                    recyclerView.setAdapter(mAdapter);
                                }
                                jsonObjectRequest.cancel();
                                requestQueue.stop();
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetEmployeeJourneyDetail(String travelId, final RecyclerView recyclerView) {
        try {
            arlDataJourneyDetail = new ArrayList<HashMap<String, String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeJourneyDetail";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("TRAVEL_ID", travelId);

//            pm.put("objConveyanceVoucherRequestStatusInfo", params_final);

            requestQueue = Volley.newRequestQueue(getContext());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            pDialog.dismiss();

                            try {
                                HashMap<String, String> mapData;
                                JSONArray jsonArray = response.getJSONArray("GetEmployeeJourneyDetailResult");

                                Log.d("Result", jsonArray.toString());
//
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    mapData = new HashMap<String, String>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String TRD_TRAVEL_REQUEST_DETAIL_ID = explrObject.getString("TRD_TRAVEL_REQUEST_DETAIL_ID");
                                    String TRD_TRAVEL_REQUEST_ID = explrObject.getString("TRD_TRAVEL_REQUEST_ID");
                                    String TRD_TRAVEL_MODE = explrObject.getString("TRD_TRAVEL_MODE");
                                    String TM_TRAVEL_MODE_NAME = explrObject.getString("TM_TRAVEL_MODE_NAME");
                                    String TRD_TRAVEL_CLASS = explrObject.getString("TRD_TRAVEL_CLASS");
                                    String TC_TRAVEL_CLASS_NAME = explrObject.getString("TC_TRAVEL_CLASS_NAME");
                                    String TRD_BOARDING_PLACE = explrObject.getString("TRD_BOARDING_PLACE");
                                    String TRD_BOARDING_PLACE_NAME = explrObject.getString("TRD_BOARDING_PLACE_NAME");
                                    String TRD_ARRIVAL_PLACE_NAME = explrObject.getString("TRD_ARRIVAL_PLACE_NAME");
                                    String TRD_ARRIVAL_PLACE = explrObject.getString("TRD_ARRIVAL_PLACE");
                                    String TRD_BOARDING_DATE = explrObject.getString("TRD_BOARDING_DATE");
                                    String TRD_BOARDING_DATE_1 = explrObject.getString("TRD_BOARDING_DATE_1");
                                    String TRD_ARRIVAL_DATE = explrObject.getString("TRD_ARRIVAL_DATE");
                                    String TRD_ARRIVAL_DATE_1 = explrObject.getString("TRD_ARRIVAL_DATE_1");
                                    String TRD_NATURE_OF_EXEPNSE = explrObject.getString("TRD_NATURE_OF_EXEPNSE");
                                    String TEN_TRAVEL_EXPENSE_NATURE_NAME = explrObject.getString("TEN_TRAVEL_EXPENSE_NATURE_NAME");
                                    String TRD_IS_TICKET = explrObject.getString("TRD_IS_TICKET");
                                    String TRD_IS_CAB = explrObject.getString("TRD_IS_CAB");
                                    String TRD_IS_HOTEL = explrObject.getString("TRD_IS_HOTEL");
                                    String TRD_REASON = explrObject.getString("TRD_REASON");

                                    mapData.put("TRD_TRAVEL_REQUEST_DETAIL_ID", TRD_TRAVEL_REQUEST_DETAIL_ID);
                                    mapData.put("TRD_TRAVEL_REQUEST_ID", TRD_TRAVEL_REQUEST_ID);
                                    mapData.put("TRD_TRAVEL_MODE", TRD_TRAVEL_MODE);
                                    mapData.put("TM_TRAVEL_MODE_NAME", TM_TRAVEL_MODE_NAME);
                                    mapData.put("TRD_TRAVEL_CLASS", TRD_TRAVEL_CLASS);
                                    mapData.put("TC_TRAVEL_CLASS_NAME", TC_TRAVEL_CLASS_NAME);
                                    mapData.put("TRD_BOARDING_PLACE", TRD_BOARDING_PLACE);
                                    mapData.put("TRD_BOARDING_PLACE_NAME", TRD_BOARDING_PLACE_NAME);
                                    mapData.put("TRD_ARRIVAL_PLACE_NAME", TRD_ARRIVAL_PLACE_NAME);
                                    mapData.put("TRD_ARRIVAL_PLACE", TRD_ARRIVAL_PLACE);
                                    mapData.put("TRD_BOARDING_DATE", TRD_BOARDING_DATE);
                                    mapData.put("TRD_BOARDING_DATE_1", TRD_BOARDING_DATE_1);
                                    mapData.put("TRD_ARRIVAL_DATE", TRD_ARRIVAL_DATE);
                                    mapData.put("TRD_ARRIVAL_DATE_1", TRD_ARRIVAL_DATE_1);
                                    mapData.put("TRD_NATURE_OF_EXEPNSE", TRD_NATURE_OF_EXEPNSE);
                                    mapData.put("TEN_TRAVEL_EXPENSE_NATURE_NAME", TEN_TRAVEL_EXPENSE_NATURE_NAME);
                                    mapData.put("TRD_IS_TICKET", TRD_IS_TICKET);
                                    mapData.put("TRD_IS_CAB", TRD_IS_CAB);
                                    mapData.put("TRD_IS_HOTEL", TRD_IS_HOTEL);
                                    mapData.put("TRD_REASON", TRD_REASON);

                                    arlDataJourneyDetail.add(mapData);

                                }
                                if (jsonArray.length() == 0) {
                                    Utilities.showDialog(coordinatorLayout, "Data Not Found");
                                } else {
                                    Log.e("Conveyance Voucher", arlDataJourneyDetail.toString());
                                    System.out.println("Conveyance Voucher==" + arlDataJourneyDetail);
                                    travelVoucherExpenseAdapter = new TravelVoucherExpenseAdapter(getActivity(), "TRAVEL_EXPENSE_DETAIL", arlDataJourneyDetail);
                                    recyclerView.setAdapter(travelVoucherExpenseAdapter);
                                }
                                jsonObjectRequest.cancel();
                                requestQueue.stop();
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    GetExpenseTypeTravel
    public void getExpenseTypeTravel() {
        try {
//        arlDataTravelCity = new ArrayList<HashMap<String, String>>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetExpenseTypeTravel";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();
//
            params_final.put("POLICY_ID", policyId);
            params_final.put("EXPENSE_SUB_TYPE_ID", "0");
//
            pm.put("objExpenseTypeTravelInfo", params_final);

            requestQueue = Volley.newRequestQueue(getContext());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                JSONArray jsonArray = response.getJSONArray("GetExpenseTypeTravelResult");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> mapData = new HashMap<String, String>();

                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String EST_EXPENSE_SUB_TYPE = explrObject.getString("EST_EXPENSE_SUB_TYPE");
                                    String EST_EXPENSE_SUB_TYPE_ID = explrObject.getString("EST_EXPENSE_SUB_TYPE_ID");
                                    String EST_IS_BILL_DATE_REQUIRED = explrObject.getString("EST_IS_BILL_DATE_REQUIRED");
                                    String EST_IS_BILL_NO_REQUIRED = explrObject.getString("EST_IS_BILL_NO_REQUIRED");
                                    String EST_RATE_REQUIRED = explrObject.getString("EST_RATE_REQUIRED");
                                    String ET_EXPENSE_TYPE = explrObject.getString("ET_EXPENSE_TYPE");

                                    mapData.put("EST_EXPENSE_SUB_TYPE", EST_EXPENSE_SUB_TYPE);
                                    mapData.put("EST_EXPENSE_SUB_TYPE_ID", EST_EXPENSE_SUB_TYPE_ID);
                                    mapData.put("EST_IS_BILL_DATE_REQUIRED", EST_IS_BILL_DATE_REQUIRED);
                                    mapData.put("EST_IS_BILL_NO_REQUIRED", EST_IS_BILL_NO_REQUIRED);
                                    mapData.put("EST_RATE_REQUIRED", EST_RATE_REQUIRED);
                                    mapData.put("ET_EXPENSE_TYPE", ET_EXPENSE_TYPE);

                                    arlDataTravelExpenseType.add(mapData);
                                }
                                mAdapter1 = new ConveyanceCustomSpinnerAdapter("TRAVEL_EXPENSE_TYPE", getActivity(), arlDataTravelExpenseType);
                                TRE_Spin_ExpenseType.setAdapter(mAdapter1);
                                Log.e("Value of Con Type", arlDataTravelExpenseType.toString());
                                jsonObjectRequest.cancel();
                                requestQueue.stop();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.e("Error In", "" + ex.getMessage());
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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTravelPolicyId() {
        try {

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyPolicyIdTravel";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", employeeId);

//            pm.put("objSendConveyanceRequestInfo", params_final);

            requestQueue = Volley.newRequestQueue(getContext());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                policyId = response.getString("GetMyPolicyIdTravelResult");
                                Log.e("Policy ID", policyId);
                                jsonObjectRequest.cancel();
                                requestQueue.stop();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.e("Error In", "" + ex.getMessage());
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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendTravelVoucherRequest(String travel_request_id, String Comment) {
        try {

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendTravelVoucherRequest";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", employeeId);
            params_final.put("TRAVEL_REQUEST_ID", travel_request_id);
            params_final.put("REMARKS", Comment);

            pm.put("objSendTravelVoucherRequestInfo", params_final);

            requestQueue = Volley.newRequestQueue(getContext());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                String result = response.getString("SendTravelVoucherRequestResult");

                                int res = Integer.valueOf(result);

                                if (res > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Travel Voucher Request send successfully.");
                                    getTravelForVoucher();
                                } else if (res == -1) {
                                    Utilities.showDialog(coordinatorLayout, "Please add expense for the travel to request for voucher.");
                                } else if (res == 0) {
                                    Utilities.showDialog(coordinatorLayout, "Some error occur on processing the request.");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.e("Error In", "" + ex.getMessage());
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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getMyExpenseType(String positionId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyExpenseType";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                Log.d("Position", positionId);

                params_final.put("POLICY_ID", policyId);
                params_final.put("EXPENSE_SUB_TYPE_ID", positionId);

                pm.put("objExpenseTypeInfo", params_final);

                requestQueue = Volley.newRequestQueue(getContext());

                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    JSONArray jsonArray = response.getJSONArray("GetMyExpenseTypeResult");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        HashMap<String, String> mapData = new HashMap<String, String>();

                                        JSONObject explrObject = jsonArray.getJSONObject(i);
                                        String str1 = "<font color='#EE0000'>*</font>";
                                        EST_IS_BILL_NO_REQUIRED = explrObject.getString("EST_IS_BILL_NO_REQUIRED");
                                        EST_IS_BILL_DATE_REQUIRED = explrObject.getString("EST_IS_BILL_DATE_REQUIRED");
//
                                        if (EST_IS_BILL_DATE_REQUIRED.equals("True")) {
                                            btn_TREBillDateTitle.setText(Html.fromHtml("Bill Date " + str1));
                                        } else {
                                            btn_TREBillDateTitle.setText(Html.fromHtml("Bill Date"));
                                        }
                                        if (EST_IS_BILL_NO_REQUIRED.equals("True")) {
                                            edt_TREBillNoTitle.setText(Html.fromHtml("Bill No. " + str1));
                                        } else {
                                            edt_TREBillNoTitle.setText(Html.fromHtml("Bill No."));
                                        }
                                        mapData.put("EST_IS_BILL_NO_REQUIRED", EST_IS_BILL_NO_REQUIRED);
                                        mapData.put("EST_IS_BILL_DATE_REQUIRED", EST_IS_BILL_DATE_REQUIRED);
//
                                        arlDataExpenseBillReq.add(mapData);

                                    }
                                    jsonObjectRequest.cancel();
                                    requestQueue.stop();
//                                    Log.e("Value of Con Type", arlDataMyConType.toString());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
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

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTravelExpense(String travelRequestId, final RecyclerView recyclerView) {
        try {
            arlDataTravelExpensep = new ArrayList<>();
            if (Utilities.isNetworkAvailable(getActivity())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelExpense";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

//                Log.d("Position", positionId);

                params_final.put("TRAVEL_REQUEST_ID", travelRequestId);

//                pm.put("objExpenseTypeInfo", params_final);

                requestQueue = Volley.newRequestQueue(getContext());

                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    JSONArray jsonArray = response.getJSONArray("GetTravelExpenseResult");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        HashMap<String, String> mapData = new HashMap<String, String>();

                                        JSONObject explrObject = jsonArray.getJSONObject(i);

                                        String EST_EXPENSE_SUB_TYPE = explrObject.getString("EST_EXPENSE_SUB_TYPE");
                                        String ET_EXPENSE_TYPE = explrObject.getString("ET_EXPENSE_TYPE");
                                        String EXPENSE_ID = explrObject.getString("EXPENSE_ID");
                                        String EXPENSE_ID_1 = explrObject.getString("EXPENSE_ID_1");
                                        String TED_APPROVED_AMOUNT = explrObject.getString("TED_APPROVED_AMOUNT");
                                        String TED_BILL_AMOUNT = explrObject.getString("TED_BILL_AMOUNT");
                                        String TED_BILL_DATE = explrObject.getString("TED_BILL_DATE");
                                        String TED_BILL_DATE_1 = explrObject.getString("TED_BILL_DATE_1");
                                        String TED_BILL_NO = explrObject.getString("TED_BILL_NO");
                                        String TED_EXPENSE_SUB_TYPE = explrObject.getString("TED_EXPENSE_SUB_TYPE");
                                        String TED_EXPENSE_TYPE = explrObject.getString("TED_EXPENSE_TYPE");
                                        String TED_IS_PAID_BY_COMPANY = explrObject.getString("TED_IS_PAID_BY_COMPANY");
                                        String TED_MODIFIED_BY = explrObject.getString("TED_REASON");
                                        String TED_MODIFIED_DATE = explrObject.getString("TED_REASON");
                                        String TED_REASON = explrObject.getString("TED_REASON");
                                        String TED_TRAVEL_REQUEST_ID = explrObject.getString("TED_TRAVEL_REQUEST_ID");

                                        mapData.put("TRE_ExpenseType", EST_EXPENSE_SUB_TYPE);
                                        mapData.put("TRE_BillNo", TED_BILL_NO);
                                        mapData.put("TRE_BillAmount", TED_BILL_AMOUNT);
                                        mapData.put("TRE_BillDate", TED_BILL_DATE);
                                        mapData.put("TRE_CompanyProvided", TED_IS_PAID_BY_COMPANY);
                                        mapData.put("TRE_Remarks", TED_REASON);
                                        mapData.put("EXPENSE_ID", EXPENSE_ID);

                                        arlDataTravelExpensep.add(mapData);

//                                        arlDataTravelExpensep.add(mapData);

                                    }
                                    Log.e("arlDataTravelExpensep", arlDataTravelExpensep.toString());
                                    travelVoucherExpenseAdapter = new TravelVoucherExpenseAdapter(getActivity(), "TRAVEL_EXPENSE", arlDataTravelExpensep);
                                    recyclerView.setAdapter(travelVoucherExpenseAdapter);
//                                    jsonObjectRequest.cancel();
//                                    requestQueue.stop();
//                                    Log.e("Value of Con Type", arlDataMyConType.toString());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
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

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void AddTravelExpenseVoucher(String BillNo, final String BillDate, String BillAmount, String ComProvided, String Remarks) {
        try {
            final HashMap<String, String> mapData = new HashMap<String, String>();
            final String Bill_No = BillNo;
            final String Bill_Date = BillDate;
            final String Bill_Amount = BillAmount;
            final String Com_Provided = ComProvided;
            final String Remarks_1 = Remarks;

            Log.e("Parameter", "Policy Id = " + policyId + ", Travel RequestId = " + TRAVEL_REQUEST_ID + ", Expense_SubId = " + positionId
                    + ", BillNo = " + Bill_No + ", Bill Date = " + Bill_Date + ", Bill Amount = " + Bill_Amount + ", Comp = " +
                    Com_Provided + ", Reason = " + Remarks_1);
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/AddTravelExpense";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("POLICY_ID", policyId);
            params_final.put("TRAVEL_REQUEST_ID", TRAVEL_REQUEST_ID);
            params_final.put("EXPENSE_ID", "0");
            params_final.put("EXPENSE_SUB_TYPE_ID", positionId);
            params_final.put("BILL_NO", BillNo);
            params_final.put("BILL_DATE", BillDate);
            params_final.put("BILL_AMOUNT", BillAmount);
            params_final.put("COMPANY_PROVIDED", ComProvided);
            params_final.put("REASON", Remarks);
            params_final.put("DISPLAY_FILE_NAME", displayFileName);
            params_final.put("DISPLAY_ACTUAL_NAME", actualFileName);


            pm.put("objAddTravelExpenseInfo", params_final);

            Log.d("param_final", pm.toString());

            requestQueue = Volley.newRequestQueue(getContext());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                String result = response.getString("AddTravelExpenseResult");
                                int res = Integer.valueOf(result);

                                if (res > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Travel Expense added sucessfully.");
                                    Toast.makeText(getActivity(), "Travel Expense added sucessfully.", Toast.LENGTH_LONG).show();

                                    mapData.put("TRE_ExpenseType", positionValue);
                                    mapData.put("TRE_BillNo", Bill_No);
                                    mapData.put("TRE_BillAmount", Bill_Amount);
                                    mapData.put("TRE_BillDate", Bill_Date);
                                    mapData.put("TRE_CompanyProvided", Com_Provided);
                                    mapData.put("TRE_Remarks", Remarks_1);

                                    arlDataTravelExpense.add(mapData);
                                    travelVoucherExpenseAdapter = new TravelVoucherExpenseAdapter(getActivity(), "TRAVEL_EXPENSE", arlDataTravelExpense);
                                    recycler_new_travelExpense.setAdapter(travelVoucherExpenseAdapter);

                                    TRE_Spin_ExpenseType.setSelection(0);
                                    edt_TREBillAmount.setText("");
                                    edt_TREBillNo.setText("");
                                    TRE_remarks.setText("");
                                    btn_TREBillDate.setText("");
                                    check_companyProvided.setChecked(false);

                                } else if (res == -1) {
                                    Toast.makeText(getActivity(), "Expense details with same bill no and bill date already exists.", Toast.LENGTH_LONG).show();
                                    Utilities.showDialog(coordinatorLayout, "Expense details with same bill no and bill date already exists.");
                                } else if (res == 0) {
                                    Toast.makeText(getActivity(), "Some error occur on processing the request.", Toast.LENGTH_LONG).show();
                                    Utilities.showDialog(coordinatorLayout, "Some error occur on processing the request.");
                                }
                                jsonObjectRequest.cancel();
                                requestQueue.stop();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.e("Error In", "" + ex.getMessage());
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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendDeleteTravelExpense(String EXPENSE_ID, final String T_RequestId) {
        try {
            final HashMap<String, String> mapData = new HashMap<String, String>();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/DeleteTravelExpense";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", employeeId);
            params_final.put("EXPENSE_ID", EXPENSE_ID);

            pm.put("objDeleteTravelExpenseInfo", params_final);

            Log.d("param_final", pm.toString());

            requestQueue = Volley.newRequestQueue(getContext());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                String result = response.getString("DeleteTravelExpenseResult");
                                int res = Integer.valueOf(result);

                                if (res > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Travel expenses deleted sucessfully.");
                                    Toast.makeText(getActivity(), "Travel expenses deleted sucessfully." + T_RequestId, Toast.LENGTH_LONG).show();
//                                    getTravelExpense(T_RequestId,recycler_new_travelExpense);
                                } else if (res == 0) {
                                    Toast.makeText(getActivity(), "Error during deletion of Travel Expense.", Toast.LENGTH_LONG).show();
                                    Utilities.showDialog(coordinatorLayout, "Error during deletion of Travel Expense.");
                                }
                                jsonObjectRequest.cancel();
                                requestQueue.stop();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.e("Error In", "" + ex.getMessage());
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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void GetCompareDateResult(final String BillDate) {
        Constants.COMPARE_DATE_API = true;
        Log.e("Compare Method", "" + Constants.COMPARE_DATE_API);


        try {

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("From_Date", JourneyStartDate_1.replace("/", "-"));
            params_final.put("To_Date", BillDate);

            Log.d("ParamFinal First", params_final.toString());

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
                                if (resultDate) {
                                    GetCompareDateResult1(BillDate);
                                    linearErrorResultText.setVisibility(View.GONE);
                                } else {
                                    errorResultText.setText("Bill date should be between start date and return date of travel!");
                                    linearErrorResultText.setVisibility(View.VISIBLE);
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetCompareDateResult1(final String BillDate) {
        Constants.COMPARE_DATE_API = true;
        Log.e("Compare Method", "" + Constants.COMPARE_DATE_API);

        try {

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("From_Date", BillDate);
            params_final.put("To_Date", JourneyReturnDate_1.replace("/", "-"));

            Log.d("ParamFinal Second", params_final.toString());

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
                                    errorResultText.setText("Bill date should be between start date and return date of travel!");
                                    linearErrorResultText.setVisibility(View.VISIBLE);
                                } else {
                                    linearErrorResultText.setVisibility(View.GONE);
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectFile() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.leave_request_custom_dialog_mmt, null);
        alertD = new AlertDialog.Builder(getActivity()).create();
        ImageButton btnAdd1 = promptView.findViewById(R.id.galleryButton);
        ImageButton btnAdd2 = promptView.findViewById(R.id.cameraButton);
        btnAdd1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 999);
            }
        });
        btnAdd2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1000);
            }
        });
        alertD.setView(promptView);
        alertD.show();
        Window window = alertD.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setAttributes(wlp);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        alertD.dismiss();
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                displayFileName = Utilities.fileName(getActivity(), uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                    if (bitmap != null) {
                        uploadImage(bitmap);
                    } else {
                        getFileData(Objects.requireNonNull(uri));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                uploadimgIcon.setVisibility(View.GONE);
            }
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            displayFileName = "CaptureImage.jpg";
            uploadImage(photo);
        }
    }

    private void getFileData(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.d("nameeeee>>>>  ", displayFileName);

                    uploadFile(displayFileName, uri);
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayFileName = myFile.getName();
        }
    }

    private void uploadFile(final String pdfname, Uri pdffile) {

        InputStream iStream = null;
        try {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Uploading Image...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            iStream = getActivity().getContentResolver().openInputStream(pdffile);
            final byte[] inputData = Utilities.getBytes(iStream);
            String upload_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId;

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            progressDialog.dismiss();

                            List<String> list = Arrays.asList(new String(response.data).split(","));
                            int value = Integer.valueOf(list.get(0));
                            if (value == 1) {
                                Toast.makeText(getActivity(), "File Upload Successfully", Toast.LENGTH_SHORT).show();
                                uploadimgIcon.setVisibility(View.VISIBLE);
                                nofileChosen.setText(displayFileName);
                            }
                            actualFileName = list.get(1);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            nofileChosen.setText("");
                            uploadimgIcon.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("filename", new DataPart(pdfname, inputData));
                    return params;
                }
            };
            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(volleyMultipartRequest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadImage(final Bitmap bitmap) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        String IMG_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId;

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, IMG_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        Log.d("ressssssoo", new String(response.data));

                        List<String> list = Arrays.asList(new String(response.data).split(","));
                        int value = Integer.valueOf(list.get(0));
                        if (value == 1) {
                            Toast.makeText(getActivity(), "Image Upload Successfully", Toast.LENGTH_SHORT).show();
                            uploadimgIcon.setVisibility(View.VISIBLE);
                            nofileChosen.setText(displayFileName);
                        }
                        actualFileName = list.get(1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        nofileChosen.setText("");
                        uploadimgIcon.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("filename", new DataPart(imagename + ".png", Utilities.getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(volleyMultipartRequest);
    }
}
