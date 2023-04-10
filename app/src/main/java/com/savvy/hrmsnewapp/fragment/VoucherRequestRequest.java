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
import com.savvy.hrmsnewapp.adapter.StatusConveyanceVoucherAdapter;
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

public class VoucherRequestRequest extends BaseFragment {

    StatusConveyanceVoucherAdapter mAdapter;

    Button btn_back_voucher;
    SharedPreferences shared, shareData;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "";
    String token_no = "";

    RecyclerView recyclerView, recyclerViewDetail;
    Button btn_closeDetail;

    CustomTextView txt_ConEdit, txt_ConDetail, txt_ConToken_no, txt_ConCancel, txt_ConAction;

    String employeeId = "", requestId = "", convenyanceId = "";
    String comment = "", xmlData = "";
    CustomTextView txtDataNotFound;

    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData, arlData1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arlData = new ArrayList<HashMap<String, String>>();
        arlData1 = new ArrayList<HashMap<String, String>>();

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        employeeId = (shared.getString("EmpoyeeId", ""));

        getConveyanceForVoucher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voucher_request_request, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_back_voucher = getActivity().findViewById(R.id.btn_voucher_back);

        recyclerView = getActivity().findViewById(R.id.conveyanceRecyclerVoucher);
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
                    txt_ConAction = view.findViewById(R.id.txt_RVoucherAction);
                    txt_ConDetail = view.findViewById(R.id.txt_RVoucherDetail);
                    txt_ConToken_no = view.findViewById(R.id.txt_RVoucherToken);

                    txt_ConAction.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            token_no = txt_ConToken_no.getText().toString();
                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {
                                String conveyance_Id = mapdata.get("CR_CONVEYANCE_REQUEST_ID");

                                convenyanceId = conveyance_Id;
                                xmlData = "@" + conveyance_Id;
                            }
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.dialogbox_approval_toggle);
                            dialog.setTitle("Voucher Request");
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            LinearLayout toggleLayout = dialog.findViewById(R.id.toggleLayout);

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

                            String TravelTitle = "<font color=\"#277ddb\"><bold><u>" + "Conveyance Voucher Request" + "</u></bold></font>";
                            CustomTextView txt_voucher_title = dialog.findViewById(R.id.dialog_header);
                            txt_voucher_title.setText(Html.fromHtml(TravelTitle));

                            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    comment = edt_comment.getText().toString().trim();
                                    if (comment.equals("")) {
                                        Utilities.showDialog(coordinatorLayout, "Please Enter Comment");
                                    } else {
                                        sendConveyanceVoucherRequest(comment, xmlData);
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

                    txt_ConDetail.setOnClickListener(new View.OnClickListener() {
                        //  if(recycler_status){}
                        @Override
                        public void onClick(View v) {
                            token_no = txt_ConToken_no.getText().toString();

                            String str = mapdata.get("TOKEN_NO");

                            if (token_no.equals(str)) {

                                String request_Id = mapdata.get("ERFS_REQUEST_ID");
                                String conveyance_Id = mapdata.get("CR_CONVEYANCE_REQUEST_ID");


                                requestId = request_Id;
                                convenyanceId = conveyance_Id;

                            }
                            final Dialog dialog = new Dialog(getActivity());

                            dialog.setTitle("Conveyance Detail");
                            dialog.setContentView(R.layout.voucher_detail_main_row);
                            dialog.setCancelable(false);

                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            recyclerViewDetail = dialog.findViewById(R.id.recyclerDetailVoucher);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerViewDetail.setLayoutManager(mLayoutManager);
                            recyclerViewDetail.setItemAnimator(new DefaultItemAnimator());

                            btn_closeDetail = dialog.findViewById(R.id.btn_closeDetailVoucher);
                            mAdapter = new StatusConveyanceVoucherAdapter("CONVEYANCE_VOUCHER_DETAIL", getActivity(), coordinatorLayout, arlData1);
                            getConveyanceVoucherDetail(convenyanceId);

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
                ConveyancefragmentHolder conveyancefragmentHolder = new ConveyancefragmentHolder();
                transaction.replace(R.id.container_body, conveyancefragmentHolder);
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
        private VoucherRequestRequest.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final VoucherRequestRequest.ClickListener clickListener) {
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

    public void getConveyanceForVoucher() {
        try {
            arlData = new ArrayList<HashMap<String, String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetConveyanceForVoucher";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", employeeId);

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
                                JSONArray jsonArray = response.getJSONArray("GetConveyanceForVoucherResult");

                                Log.d("Result", jsonArray.toString());
//
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    mapData = new HashMap<String, String>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String TOKEN_NO = explrObject.getString("TOKEN_NO");
                                    String CR_CONVEYANCE_REQUEST_ID = explrObject.getString("CR_CONVEYANCE_REQUEST_ID");
                                    String CR_REQUESTED_AMOUNT = explrObject.getString("CR_REQUESTED_AMOUNT");
                                    String CR_APPROVED_AMOUNT = explrObject.getString("CR_APPROVED_AMOUNT");
                                    String CR_REMARKS = explrObject.getString("CR_REMARKS");
                                    String CR_REQUESTED_DATE = explrObject.getString("CR_REQUESTED_DATE");
                                    String CVRD_CONVEYANCE_REQUEST_ID = explrObject.getString("CVRD_CONVEYANCE_REQUEST_ID");
                                    String CVR_CONVEYANCE_VOUCHER_REQUEST_ID = explrObject.getString("CVR_CONVEYANCE_VOUCHER_REQUEST_ID");

                                    mapData.put("TOKEN_NO", TOKEN_NO);
                                    mapData.put("CR_CONVEYANCE_REQUEST_ID", CR_CONVEYANCE_REQUEST_ID);
                                    mapData.put("CR_REQUESTED_AMOUNT", CR_REQUESTED_AMOUNT);
                                    mapData.put("CR_APPROVED_AMOUNT", CR_APPROVED_AMOUNT);
                                    mapData.put("CR_REMARKS", CR_REMARKS);
                                    mapData.put("CR_REQUESTED_DATE", CR_REQUESTED_DATE);
                                    mapData.put("CVRD_CONVEYANCE_REQUEST_ID", CVRD_CONVEYANCE_REQUEST_ID);
                                    mapData.put("CVR_CONVEYANCE_VOUCHER_REQUEST_ID", CVR_CONVEYANCE_VOUCHER_REQUEST_ID);

                                    arlData.add(mapData);

                                }
                                if (jsonArray.length() <= 0) {
                                    recyclerView.setAdapter(null);
                                    recyclerView.setVisibility(View.GONE);
                                    txtDataNotFound.setVisibility(View.VISIBLE);
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    txtDataNotFound.setVisibility(View.GONE);
                                    Log.e("Conveyance Voucher", arlData.toString());
                                    System.out.println("Conveyance Voucher==" + arlData);
                                    mAdapter = new StatusConveyanceVoucherAdapter("CONVEYANCE_VOUCHER", getActivity(), coordinatorLayout, arlData);
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


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getConveyanceVoucherDetail(String conId) {
        try {
            arlData1 = new ArrayList<HashMap<String, String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeConveyanceDetail";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("CONVEYANCE_ID", conId);

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
                                JSONArray jsonArray = response.getJSONArray("GetEmployeeConveyanceDetailResult");

                                Log.d("Result", jsonArray.toString());
//
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    mapData = new HashMap<String, String>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String CRD_CONVEYANCE_REQUEST_DETAIL_ID = explrObject.getString("CRD_CONVEYANCE_REQUEST_DETAIL_ID");
                                    String CRD_CONVEYANCE_TYPE = explrObject.getString("CRD_CONVEYANCE_TYPE");
                                    String EST_EXPENSE_SUB_TYPE = explrObject.getString("EST_EXPENSE_SUB_TYPE");
                                    String CRD_PLACE_FROM = explrObject.getString("CRD_PLACE_FROM");
                                    String CRD_PLACE_TO = explrObject.getString("CRD_PLACE_TO");
                                    String CRD_DISTANCE = explrObject.getString("CRD_DISTANCE");
                                    String CRD_BILL_AMOUNT = explrObject.getString("CRD_BILL_AMOUNT");
                                    String CRD_BILL_NO = explrObject.getString("CRD_BILL_NO");
                                    String CRD_BILL_DATE = explrObject.getString("CRD_BILL_DATE");
                                    String CRD_BILL_DATE_1 = explrObject.getString("CRD_BILL_DATE_1");
                                    String CRD_APPROVED_AMOUNT = explrObject.getString("CRD_APPROVED_AMOUNT");
                                    String CRD_REASON = explrObject.getString("CRD_REASON");

                                    mapData.put("CRD_CONVEYANCE_REQUEST_DETAIL_ID", CRD_CONVEYANCE_REQUEST_DETAIL_ID);
                                    mapData.put("CRD_CONVEYANCE_TYPE", CRD_CONVEYANCE_TYPE);
                                    mapData.put("EST_EXPENSE_SUB_TYPE", EST_EXPENSE_SUB_TYPE);
                                    mapData.put("CRD_PLACE_FROM", CRD_PLACE_FROM);
                                    mapData.put("CRD_PLACE_TO", CRD_PLACE_TO);
                                    mapData.put("CRD_DISTANCE", CRD_DISTANCE);
                                    mapData.put("CRD_BILL_AMOUNT", CRD_BILL_AMOUNT);
                                    mapData.put("CRD_BILL_NO", CRD_BILL_NO);
                                    mapData.put("CRD_BILL_DATE", CRD_BILL_DATE);
                                    mapData.put("CRD_BILL_DATE_1", CRD_BILL_DATE_1);
                                    mapData.put("CRD_APPROVED_AMOUNT", CRD_APPROVED_AMOUNT);
                                    mapData.put("CRD_REASON", CRD_REASON);

                                    arlData1.add(mapData);

                                }
                                Log.e("ArrayList 1", arlData1.toString());
                                System.out.println("ArrayList==" + arlData1);
                                mAdapter = new StatusConveyanceVoucherAdapter("CONVEYANCE_VOUCHER_DETAIL", getActivity(), coordinatorLayout, arlData1);
                                recyclerViewDetail.setAdapter(mAdapter);
//                                mAdapter = new CalendarAdapter(getActivity(),month,arlData);
//                                gridview.setAdapter(adapter);
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

    public void sendConveyanceVoucherRequest(String comment, String xmlData) {
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendConveyanceVoucherRequest";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", employeeId);
            params_final.put("REQUEST_ID", "0");
            params_final.put("REMARK", comment);
            params_final.put("REQUEST_IDS", xmlData);

            pm.put("objSendVoucherRequestInfo", params_final);

            Log.e("Request ", "Request for Conveyance Voucher Request " + pm.toString());

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
                                String result = response.getString("SendConveyanceVoucherRequestResult");

                                int res = Integer.valueOf(result);

                                if (res > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Conveyance Vocuher Request send sucessfully.");
                                    getConveyanceForVoucher();
//                                    recyclerView
                                } else if (res == 0) {
                                    Utilities.showDialog(coordinatorLayout, "Error during sending of Conveyance Voucher Request.");
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
