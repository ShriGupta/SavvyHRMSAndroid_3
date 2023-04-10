package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
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
import android.widget.EditText;
import android.widget.LinearLayout;

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
import com.savvy.hrmsnewapp.adapter.ExpenseVoucherAdapter;
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

public class ExpenseVoucherRequestFragment extends BaseFragment {

    ExpenseVoucherAdapter mAdapter;

    Button btn_back_voucher;
    SharedPreferences shared, shareData;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "";
    String token_no = "";
    String empoyeeId = "";
    RecyclerView recyclerView, recyclerViewDetail;
    Button btn_closeDetail;


    CustomTextView txt_ConEdit, txt_expDetail, txt_expToken_no, txt_ConCancel, txt_expAction;

    String employeeId = "", requestId = "", convenyanceId = "";
    String comment = "", xmlRequest = "";
    String expenseId = "";

    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData, arlData1;
    CustomTextView txtDataNotFound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arlData = new ArrayList<HashMap<String, String>>();
        arlData1 = new ArrayList<HashMap<String, String>>();

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));

        getExpenseForVoucher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_voucher_request, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_back_voucher = getActivity().findViewById(R.id.btn_voucher_back);
        recyclerView = getActivity().findViewById(R.id.expenseVoucherApprovRecycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ExpenseVoucherAdapter("EXPENSE_VOUCHER", getActivity(), arlData);

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.addOnItemTouchListener(new VoucherRequestRequest.RecyclerTouchListener(getActivity(), recyclerView, new VoucherRequestRequest.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    final HashMap<String, String> mapdata = arlData.get(position);
                    txt_expAction = view.findViewById(R.id.txt_RVoucherAction);
                    txt_expDetail = view.findViewById(R.id.txt_RVoucherDetail);
                    txt_expToken_no = view.findViewById(R.id.txt_RVoucherToken);

                    txt_expAction.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            token_no = txt_expToken_no.getText().toString();
                            xmlRequest = "";

                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {
                                expenseId = mapdata.get("ER_EXPENSE_REQUEST_ID");

//                                xmlRequest = xmlRequest + "<DATASET>";
//                                xmlRequest = xmlRequest + "<RECORD>";
//                                xmlRequest = xmlRequest + "<EXPENSE_REQUEST_ID>" + expenseId+ "</EXPENSE_REQUEST_ID>";
//                                xmlRequest = xmlRequest + "</RECORD>";
//                                xmlRequest = xmlRequest + "</DATASET>";
                                xmlRequest = "@" + expenseId;

                            }
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.dialogbox_approval_toggle);
                            dialog.setTitle("Voucher Request");
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            LinearLayout toggleLayout = dialog.findViewById(R.id.toggleLayout);
                            CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
//                            CustomTextView comment_dialog = (CustomTextView)dialog.findViewById(R.id.edt_comment_dialog);
                            Button btn_ApproveGo, btn_close;
                            btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                            btn_close = dialog.findViewById(R.id.btn_close);

                            CustomTextView txt_ApprovalToggleTitle, edt_comment_dialog;
                            txt_ApprovalToggleTitle = dialog.findViewById(R.id.txt_ApprovalToggleTitle);
                            edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);

                            String str1 = "<font color='#EE0000'>*</font>";
                            txt_ApprovalToggleTitle.setText(Html.fromHtml("Type " + str1));
                            edt_comment_dialog.setText(Html.fromHtml("Remarks " + str1));

                            toggleLayout.setVisibility(View.GONE);

                            final EditText edt_comment = dialog.findViewById(R.id.edt_approve_comment);

                            txt_header.setText("Expense Voucher Request");
//                            comment_dialog.setText("Remarks *");

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    comment = edt_comment.getText().toString().trim();
                                    Log.e("XMLREQUEST", xmlRequest);
                                    if (comment.equals("")) {
                                        Utilities.showDialog(coordinatorLayout, "Please Enter Comment");
                                    } else {
                                        sendExpenseVoucherRequest(empoyeeId, comment, xmlRequest);
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

                    txt_expDetail.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            token_no = txt_expToken_no.getText().toString();

                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {
                                expenseId = mapdata.get("ER_EXPENSE_REQUEST_ID");

                            }
                            final Dialog dialog = new Dialog(getActivity());

                            dialog.setTitle("Expense Detail");
                            dialog.setContentView(R.layout.voucher_detail_main_row);
                            dialog.setCancelable(false);

                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            recyclerViewDetail = dialog.findViewById(R.id.recyclerDetailVoucher);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerViewDetail.setLayoutManager(mLayoutManager);
                            recyclerViewDetail.setItemAnimator(new DefaultItemAnimator());

                            btn_closeDetail = dialog.findViewById(R.id.btn_closeDetailVoucher);
                            mAdapter = new ExpenseVoucherAdapter("EXPENSE_VOUCHER_DETAIL", getActivity(), arlData1);
                            getExpenseVoucherDetail(expenseId);

                            btn_closeDetail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
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
                ExpenseFramentHolder expenseFramentHolder = new ExpenseFramentHolder();
                transaction.replace(R.id.container_body, expenseFramentHolder);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
    }

    public void getExpenseForVoucher() {
        try {
            arlData = new ArrayList<HashMap<String, String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetExpenseForVoucher";
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

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            pDialog.dismiss();

                            try {
                                HashMap<String, String> mapData;
                                JSONArray jsonArray = response.getJSONArray("GetExpenseForVoucherResult");

                                Log.d("Result", jsonArray.toString());
//
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    mapData = new HashMap<String, String>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String TOKEN_NO = explrObject.getString("TOKEN_NO");
                                    String ER_EXPENSE_REQUEST_ID = explrObject.getString("ER_EXPENSE_REQUEST_ID");
                                    String ER_REQUESTED_AMOUNT = explrObject.getString("ER_REQUESTED_AMOUNT");
                                    String ER_APPROVED_AMOUNT = explrObject.getString("ER_APPROVED_AMOUNT");
                                    String ER_REMARKS = explrObject.getString("ER_REMARKS");
                                    String ER_REQUESTED_DATE = explrObject.getString("ER_REQUESTED_DATE");
                                    String EVRD_EXPENSE_REQUEST_ID = explrObject.getString("EVRD_EXPENSE_REQUEST_ID");
                                    String EVR_EXPENSE_VOUCHER_REQUEST_ID = explrObject.getString("EVR_EXPENSE_VOUCHER_REQUEST_ID");

                                    mapData.put("TOKEN_NO", TOKEN_NO);
                                    mapData.put("ER_EXPENSE_REQUEST_ID", ER_EXPENSE_REQUEST_ID);
                                    mapData.put("ER_REQUESTED_AMOUNT", ER_REQUESTED_AMOUNT);
                                    mapData.put("ER_APPROVED_AMOUNT", ER_APPROVED_AMOUNT);
                                    mapData.put("ER_REMARKS", ER_REMARKS);
                                    mapData.put("ER_REQUESTED_DATE", ER_REQUESTED_DATE);
                                    mapData.put("EVRD_EXPENSE_REQUEST_ID", EVRD_EXPENSE_REQUEST_ID);
                                    mapData.put("EVR_EXPENSE_VOUCHER_REQUEST_ID", EVR_EXPENSE_VOUCHER_REQUEST_ID);

                                    arlData.add(mapData);

                                }
                                if (jsonArray.length() <= 0) {
                                    recyclerView.setAdapter(null);
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                    recyclerView.setVisibility(View.GONE);
                                    txtDataNotFound.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    txtDataNotFound.setVisibility(View.GONE);
                                    Log.e("Conveyance Voucher", arlData.toString());
                                    System.out.println("Conveyance Voucher==" + arlData);
                                    mAdapter = new ExpenseVoucherAdapter("EXPENSE_VOUCHER", getActivity(), arlData);
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

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getExpenseVoucherDetail(String expId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData1 = new ArrayList<HashMap<String, String>>();
                final ProgressDialog pDialog;
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeExpenseDetail";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("EXPENSE_ID", expId);

//            pm.put("objConveyanceVoucherRequestStatusInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                pDialog.dismiss();

                                try {
                                    HashMap<String, String> mapData;
                                    JSONArray jsonArray = response.getJSONArray("GetEmployeeExpenseDetailResult");

                                    Log.d("Result", jsonArray.toString());
//
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        mapData = new HashMap<String, String>();
                                        JSONObject explrObject = jsonArray.getJSONObject(i);

                                        String ERD_EXPENSE_REQUEST_DETAIL_ID = explrObject.getString("ERD_EXPENSE_REQUEST_DETAIL_ID");
                                        String ERD_EXPENSE_REQUEST_ID = explrObject.getString("ERD_EXPENSE_REQUEST_ID");
                                        String ERD_EXPENSE_TYPE = explrObject.getString("ERD_EXPENSE_TYPE");
                                        String ET_EXPENSE_TYPE = explrObject.getString("ET_EXPENSE_TYPE");
                                        String ERD_EXPENSE_SUB_TYPE = explrObject.getString("ERD_EXPENSE_SUB_TYPE");
                                        String EST_EXPENSE_SUB_TYPE = explrObject.getString("EST_EXPENSE_SUB_TYPE");
                                        String ERD_BILL_NO = explrObject.getString("ERD_BILL_NO");
                                        String ERD_BILL_DATE = explrObject.getString("ERD_BILL_DATE");
                                        String ERD_BILL_DATE_1 = explrObject.getString("ERD_BILL_DATE_1");
                                        String ERD_BILL_AMOUNT = explrObject.getString("ERD_BILL_AMOUNT");
                                        String ERD_APPROVED_AMOUNT = explrObject.getString("ERD_APPROVED_AMOUNT");
                                        String ERD_REASON = explrObject.getString("ERD_REASON");

                                        mapData.put("ERD_EXPENSE_REQUEST_DETAIL_ID", ERD_EXPENSE_REQUEST_DETAIL_ID);
                                        mapData.put("ERD_EXPENSE_REQUEST_ID", ERD_EXPENSE_REQUEST_ID);
                                        mapData.put("ERD_EXPENSE_TYPE", ERD_EXPENSE_TYPE);
                                        mapData.put("ET_EXPENSE_TYPE", ET_EXPENSE_TYPE);
                                        mapData.put("ERD_EXPENSE_SUB_TYPE", ERD_EXPENSE_SUB_TYPE);
                                        mapData.put("EST_EXPENSE_SUB_TYPE", EST_EXPENSE_SUB_TYPE);
                                        mapData.put("ERD_BILL_NO", ERD_BILL_NO);
                                        mapData.put("ERD_BILL_DATE", ERD_BILL_DATE);
                                        mapData.put("ERD_BILL_DATE_1", ERD_BILL_DATE_1);
                                        mapData.put("ERD_BILL_AMOUNT", ERD_BILL_AMOUNT);
                                        mapData.put("ERD_APPROVED_AMOUNT", ERD_APPROVED_AMOUNT);
                                        mapData.put("ERD_REASON", ERD_REASON);

                                        arlData1.add(mapData);

                                    }

                                    if (jsonArray.length() <= 0) {
                                        recyclerViewDetail.setAdapter(null);
                                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                        recyclerViewDetail.setVisibility(View.GONE);
                                        txtDataNotFound.setVisibility(View.VISIBLE);
                                    } else {
                                        recyclerViewDetail.setVisibility(View.VISIBLE);
                                        txtDataNotFound.setVisibility(View.GONE);
                                        Log.e("ArrayList 1", arlData1.toString());
                                        System.out.println("ArrayList==" + arlData1);
                                        mAdapter = new ExpenseVoucherAdapter("EXPENSE_VOUCHER_DETAIL", getActivity(), arlData1);
                                        recyclerViewDetail.setAdapter(mAdapter);
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

    public void sendExpenseVoucherRequest(String employeeId, String comment, String xmlData) {
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendExpenseVoucherRequest";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", employeeId);
            params_final.put("REQUEST_ID", "0");
            params_final.put("REMARK", comment);
            params_final.put("REQUEST_IDS", xmlData);

            pm.put("objSendVoucherRequestInfo", params_final);

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
                                String result = response.getString("SendExpenseVoucherRequestResult");

                                int res = Integer.valueOf(result);

                                if (res > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Expense Vocuher Request send sucessfully.");
                                    getExpenseForVoucher();
                                } else if (res == 0) {
                                    Utilities.showDialog(coordinatorLayout, "Error during sending of Expense Voucher Request.");
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

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ExpenseVoucherRequestFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ExpenseVoucherRequestFragment.ClickListener clickListener) {
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
}
