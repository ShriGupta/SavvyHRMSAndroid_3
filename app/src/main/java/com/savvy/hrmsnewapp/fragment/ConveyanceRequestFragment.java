package com.savvy.hrmsnewapp.fragment;

import android.annotation.TargetApi;
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
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
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
import com.savvy.hrmsnewapp.adapter.Conveyance_Add_fieldAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleyMultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class ConveyanceRequestFragment extends BaseFragment {

    private static final int REQUEST_CODE_GALLERY = 21;
    private static final int REQUEST_CODE_CAMERA = 11;
    private static final int SELECT_PDF = 121;

    Conveyance_Add_fieldAdapter mAdapter;
    ConveyanceCustomSpinnerAdapter mAdapter1;
    Button btn_add, btn_close, btn_voucher_detail;
    Button btn_final_submit;
    RecyclerView recyclerView_add_conveyance;
    EditText edt_conveyance_remarks;

    Button btn_fileUpload;
    Dialog dialogCamera;
    ImageView updloadFileImage;
    int PICK_IMAGE_REQUEST = 1;

    CustomTextView nofileChosen;
    ImageView uploadIcon;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String empoyeeId = "";
    SharedPreferences shared, shareData;
    String policyId = "";
    String positionId = "", positionValue = "";

    LinearLayout linear_compareDate;
    CustomTextView txt_compareDate;

    String COMPARE_DATE = "";

    Handler handler;
    Runnable rRunnable;

    String EST_IS_BILL_NO_REQUIRED = "", EST_IS_BILL_DATE_REQUIRED = "",
            EST_MAX_REQUEST_AMOUNT = "", EST_RATE = "", EST_RATE_REQUIRED = "";
    double Distance = 0;
    double BillAmount;

    ArrayList<HashMap<String, String>> statusarraylist;
    ArrayList<HashMap<String, String>> arlData, arlData1, arlDataMyConType, arlData2;

    EditText edt_place_from, edt_place_to, edt_distance, edt_bill_amount, edt_bill_no, edt_con_reason;
    CustomTextView txt_con_type, txt_placeFrom, txt_placeTo, txt_distance, txt_bill_amt, txt_bill_no, txt_billDate, txt_con_reason;
    Spinner spin;

    String Bill_Amount_Conveyance = "";

    StringBuilder ConveyancestringBuilder;

    CustomTextView txt_con_request_Edit, txt_con_request_Detail, txt_con_request_Remove;
    String Hidden_Detail = "";

    String Conveyance_spinner_value = "";
    CustomTextView txt_remarks, txt_conveyanceRequestDetail;
    private String conditionValue = "1";
    private byte[] byteArrayImage;
    private String Document_Type;
    private Uri filePath;
    private String compressedImage;


    AlertDialog alertD;
    ProgressDialog progressDialog;

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    String actualFileName = "";
    String displayFileName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.VOUCHER_DETAIL = "2";

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));

        getConveyancePolicyId();
        arlData = new ArrayList<HashMap<String, String>>();
        arlData1 = new ArrayList<HashMap<String, String>>();
        arlData2 = new ArrayList<HashMap<String, String>>();
        arlDataMyConType = new ArrayList<HashMap<String, String>>();
        statusarraylist = new ArrayList<HashMap<String, String>>();

        ConveyancestringBuilder = new StringBuilder();

        getConveyanceType_1();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conveyance_request, container, false);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_add = getActivity().findViewById(R.id.btn_add_con);
        btn_final_submit = getActivity().findViewById(R.id.btn_submit);
        btn_close = getActivity().findViewById(R.id.btn_close);
        btn_voucher_detail = getActivity().findViewById(R.id.btn_Voucher_conveyance);


        txt_conveyanceRequestDetail = getActivity().findViewById(R.id.txt_conveyanceRequestDetail);
        txt_remarks = getActivity().findViewById(R.id.txt_remarks);

        String str1 = "<font color='#A8172A' size='10'>*</font>";

        txt_conveyanceRequestDetail.setText(Html.fromHtml("Conveyance Details " + str1));
        txt_remarks.setText(Html.fromHtml("Remarks " + str1));

        edt_conveyance_remarks = getActivity().findViewById(R.id.conveyance_remarks);

        recyclerView_add_conveyance = getActivity().findViewById(R.id.recycler_add_conveyance);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView_add_conveyance.setLayoutManager(mLayoutManager);
        recyclerView_add_conveyance.setItemAnimator(new DefaultItemAnimator());

        recyclerView_add_conveyance.setAdapter(null);

        Log.d("Status", "Id = " + Constants.CONVEYANCE_REQUEST_ID + " Status = " + Constants.CONVEYANCE_REQUEST_ID_STATUS);
        if (!(Constants.CONVEYANCE_REQUEST_ID.equals("")) && Constants.CONVEYANCE_REQUEST_ID_STATUS == false) {
            Log.d("Hello", "Inside the method");
            getConveyanceVoucherDetail(Constants.CONVEYANCE_REQUEST_ID, recyclerView_add_conveyance);
        }

        recyclerView_add_conveyance.addOnItemTouchListener(new ConveyanceRequestFragment.RecyclerTouchListener(getActivity(), recyclerView_add_conveyance, new ConveyanceRequestFragment.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addConveyanceDialog(100, recyclerView_add_conveyance);
            }
        });

        btn_voucher_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                VoucherFragmentHolder voucherFragmentHolder = new VoucherFragmentHolder();
                transaction.replace(R.id.container_body, voucherFragmentHolder);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        btn_final_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conveyanceRequestXmlData();

                String con_xmlData = ConveyancestringBuilder.toString();
                String reason = edt_conveyance_remarks.getText().toString().trim();
                if (con_xmlData.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Add Conveyance Details.");
                } else if (reason.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Remarks.");
                } else {
                    sendConveyanceRequest(edt_conveyance_remarks.getText().toString().trim());
                }

            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_add_conveyance.setAdapter(null);
                edt_conveyance_remarks.setText("");
                arlData = new ArrayList<HashMap<String, String>>();
            }
        });
    }

    public void conveyanceRequestXmlData() {
//        Constants.CONVEYANCE_XMLDATA = positionId+","+placeFrom+","+placeTo+","+distance+","+bill_amt+","+bill_no+","
//                +bill_date+","+con_reason;

        ConveyancestringBuilder = new StringBuilder();
        for (int i = 0; i < arlData.size(); i++) {
            final HashMap<String, String> mapdata = arlData.get(i);

            ConveyancestringBuilder.append("@");
            ConveyancestringBuilder.append(positionId);
            ConveyancestringBuilder.append("," + mapdata.get("Place_From"));
            ConveyancestringBuilder.append("," + mapdata.get("Place_To"));
            ConveyancestringBuilder.append("," + mapdata.get("Distance"));
            ConveyancestringBuilder.append("," + mapdata.get("Bill_Amount"));
            ConveyancestringBuilder.append("," + mapdata.get("Bill_No"));
            ConveyancestringBuilder.append("," + mapdata.get("Date"));
            ConveyancestringBuilder.append("," + mapdata.get("Reason"));
            //ConveyancestringBuilder.append("," + mapdata.get("Bill_Document"));

            ConveyancestringBuilder.append("," + mapdata.get("displayFileName"));
            ConveyancestringBuilder.append("," + mapdata.get("actualFileName"));

            Log.e("String Builder", ConveyancestringBuilder.toString());

        }
    }

    public void getConveyancePolicyId() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyPolicyIdConveyance";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("EMPLOYEE_ID", empoyeeId);

//            pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    policyId = response.getString("GetMyPolicyIdConveyanceResult");
                                    Log.e("Policy ID", policyId);
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

    public void getConveyanceType() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData1 = new ArrayList<>();
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetConveyanceType";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("POLICY_ID", policyId);
                params_final.put("EXPENSE_SUB_TYPE_ID", "0");

                pm.put("objConveyanceTypeInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    JSONArray jsonArray = response.getJSONArray("GetConveyanceTypeResult");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        HashMap<String, String> mapData = new HashMap<String, String>();

                                        JSONObject explrObject = jsonArray.getJSONObject(i);

                                        String SpinnerValue = explrObject.getString("EST_EXPENSE_SUB_TYPE");
                                        String SpinnerId = explrObject.getString("EST_EXPENSE_SUB_TYPE_ID");

                                        mapData.put("EST_EXPENSE_SUB_TYPE", SpinnerValue);
                                        mapData.put("EST_EXPENSE_SUB_TYPE_ID", SpinnerId);

                                        arlData1.add(mapData);
                                    }
                                    mAdapter1 = new ConveyanceCustomSpinnerAdapter("Conveyance", getActivity(), arlData1);
                                    spin.setAdapter(mAdapter1);
                                    Log.e("Value of Con Type", arlData1.toString());
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

    public void getConveyanceType_1() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData2 = new ArrayList<HashMap<String, String>>();
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetConveyanceType";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("POLICY_ID", policyId);
                params_final.put("EXPENSE_SUB_TYPE_ID", "0");

                pm.put("objConveyanceTypeInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    JSONArray jsonArray = response.getJSONArray("GetConveyanceTypeResult");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        HashMap<String, String> mapData = new HashMap<String, String>();

                                        JSONObject explrObject = jsonArray.getJSONObject(i);

                                        String SpinnerValue = explrObject.getString("EST_EXPENSE_SUB_TYPE");
                                        String SpinnerId = explrObject.getString("EST_EXPENSE_SUB_TYPE_ID");

                                        mapData.put("EST_EXPENSE_SUB_TYPE", SpinnerValue);
                                        mapData.put("EST_EXPENSE_SUB_TYPE_ID", SpinnerId);

                                        arlData2.add(mapData);
                                    }
                                    mAdapter1 = new ConveyanceCustomSpinnerAdapter("Conveyance", getActivity(), arlData2);
//                                    spin.setAdapter(mAdapter1);
                                    Log.e("Value of Con Type", arlData2.toString());
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

    public void getMyConveyanceType(String positionId) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlDataMyConType = new ArrayList<HashMap<String, String>>();
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyConveyanceType";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                Log.d("Position", positionId);

                params_final.put("POLICY_ID", policyId);
                params_final.put("EXPENSE_SUB_TYPE_ID", positionId);

                pm.put("objMyConveyanceTypeInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    JSONArray jsonArray = response.getJSONArray("GetMyConveyanceTypeResult");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        HashMap<String, String> mapData = new HashMap<String, String>();

                                        JSONObject explrObject = jsonArray.getJSONObject(i);
                                        String str2 = "<font color='#EE0000'>*</font>";

                                        EST_IS_BILL_NO_REQUIRED = explrObject.getString("EST_IS_BILL_NO_REQUIRED");
                                        EST_IS_BILL_DATE_REQUIRED = explrObject.getString("EST_IS_BILL_DATE_REQUIRED");
                                        EST_MAX_REQUEST_AMOUNT = explrObject.getString("EST_MAX_REQUEST_AMOUNT");
                                        EST_RATE = explrObject.getString("EST_RATE");
                                        EST_RATE_REQUIRED = explrObject.getString("EST_RATE_REQUIRED");

                                        edt_bill_amount.setText("");
                                        if (EST_IS_BILL_NO_REQUIRED.equals("True")) {
                                            txt_bill_no.setText(Html.fromHtml("Bill No. " + str2));
                                        } else {
                                            edt_bill_amount.setEnabled(false);
                                            txt_bill_no.setText(Html.fromHtml("Bill No. "));
                                        }

                                        if (EST_IS_BILL_DATE_REQUIRED.equals("True")) {
                                            txt_billDate.setText(Html.fromHtml("Bill Date " + str2));
                                        } else {
                                            edt_bill_amount.setEnabled(false);
                                            txt_billDate.setText(Html.fromHtml("Bill Date "));
                                        }
                                        changeBillAmount();

                                        mapData.put("EST_IS_BILL_NO_REQUIRED", EST_IS_BILL_NO_REQUIRED);
                                        mapData.put("EST_IS_BILL_DATE_REQUIRED", EST_IS_BILL_DATE_REQUIRED);
                                        mapData.put("EST_MAX_REQUEST_AMOUNT", EST_MAX_REQUEST_AMOUNT);
                                        mapData.put("EST_RATE", EST_RATE);
                                        mapData.put("EST_RATE_REQUIRED", EST_RATE_REQUIRED);

                                        arlDataMyConType.add(mapData);
                                    }
                                    Log.e("Value of Con Type", arlDataMyConType.toString());
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

    public void sendConveyanceRequest(String remarks) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendConveyanceRequest";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("EMPLOYEE_ID", empoyeeId);
                params_final.put("REQUEST_ID", "0");
                params_final.put("REMARKS", remarks);
                params_final.put("CONVEYANCE_DETAIL", ConveyancestringBuilder.toString());

                pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    String result = response.getString("SendConveyanceRequestResult");

                                    int res = Integer.valueOf(result);

                                    Log.d(TAG, "onResponse: " + res);
                                    if (res > 0) {
                                        Utilities.showDialog(coordinatorLayout, "Conveyance request send successfully.");
                                        recyclerView_add_conveyance.setAdapter(null);
                                        arlData = new ArrayList<>();
                                        edt_conveyance_remarks.setText("");
                                    } else if (res == -1) {
                                        Utilities.showDialog(coordinatorLayout, "Conveyance request is already inprocess.");
                                    } else if (res == 0) {
                                        Utilities.showDialog(coordinatorLayout, "Error during sending Conveyance Request.");
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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeBillAmount() {
        try {
            if (EST_RATE_REQUIRED.toUpperCase().equals("TRUE")) {
                double Rate = Double.parseDouble(EST_RATE);

                if (!TextUtils.isEmpty(edt_distance.getText().toString())) {
                    Distance = Float.parseFloat(edt_distance.getText().toString());
                } else {
                    Distance = 0.0;
                }

                BillAmount = Distance * Rate;
                Log.d("Rate", "Rate " + Rate + " Distance " + Distance + " Amount " + BillAmount);
                edt_bill_amount.setText("" + BillAmount);
                edt_bill_amount.setEnabled(false);
            } else {
                edt_bill_amount.setText("");
                edt_bill_amount.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addConveyanceDialog(final int position, final RecyclerView recyclerView_add_conveyance) {

        getConveyanceType();
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.add_convayance_row);
        dialog.setCancelable(false);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        txt_con_type = dialog.findViewById(R.id.txt_conveyanceType);
        txt_placeFrom = dialog.findViewById(R.id.txt_placeFrom);
        txt_placeTo = dialog.findViewById(R.id.txt_placeTo);
        txt_distance = dialog.findViewById(R.id.txt_distance);
        txt_bill_amt = dialog.findViewById(R.id.txt_billAmount);
        txt_bill_no = dialog.findViewById(R.id.txt_billNo);
        txt_billDate = dialog.findViewById(R.id.txt_billDate);
        txt_con_reason = dialog.findViewById(R.id.txt_reason);

        btn_fileUpload = dialog.findViewById(R.id.btn_fileUpload);
        updloadFileImage = dialog.findViewById(R.id.updloadFileImage);

        nofileChosen = dialog.findViewById(R.id.nofileAttached);
        uploadIcon = dialog.findViewById(R.id.conImageUploade);

        String str1 = "<font color='#EE0000'>*</font>";

        txt_placeFrom.setText(Html.fromHtml("Place From " + str1));
        txt_con_type.setText(Html.fromHtml("Con. Type " + str1));
        txt_placeTo.setText(Html.fromHtml("Place To " + str1));
        txt_distance.setText(Html.fromHtml("Distance " + str1));
        txt_bill_amt.setText(Html.fromHtml("Bill Amount " + str1));
        txt_con_reason.setText(Html.fromHtml("Reason " + str1));

        String ConveyanceTitle = "<font color=\"#277ddb\"><bold><u>" + "Add Conveyance Detail" + "</u></bold></font>";
        CustomTextView txt_travel_title = dialog.findViewById(R.id.conveyance_pop_title);
        txt_travel_title.setText(Html.fromHtml(ConveyanceTitle));

        linear_compareDate = dialog.findViewById(R.id.layout_compare_date_conveyance);
        txt_compareDate = dialog.findViewById(R.id.compare_dateResult_conveyance);
        linear_compareDate.setVisibility(View.GONE);

        edt_place_from = dialog.findViewById(R.id.edt_place_from);
        edt_place_to = dialog.findViewById(R.id.edt_place_to);
        edt_distance = dialog.findViewById(R.id.edt_distance);
        edt_bill_amount = dialog.findViewById(R.id.edt_bill_amt);
        edt_bill_no = dialog.findViewById(R.id.edt_bill_no);
        edt_con_reason = dialog.findViewById(R.id.edt_con_reason);

        edt_bill_amount.setText("");

        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final Button btn_AddConveyance = dialog.findViewById(R.id.btn_add_conveyance);
        final Button con_bill_date = dialog.findViewById(R.id.con_bill_date);
        spin = dialog.findViewById(R.id.add_conveyance_spin);

        edt_bill_amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (EST_RATE_REQUIRED.toUpperCase().equals("TRUE")) {
                    changeBillAmount();
                }
            }
        });


        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 0) {
                        positionValue = "";
                        positionId = "";
                    } else if (position > 0) {
                        positionId = arlData1.get(position - 1).get("EST_EXPENSE_SUB_TYPE_ID");
                        positionValue = arlData1.get(position - 1).get("EST_EXPENSE_SUB_TYPE");

                        Log.e("Postion ", "Position of Spinner " + position);
                        EST_IS_BILL_NO_REQUIRED = "";
                        EST_IS_BILL_DATE_REQUIRED = "";

                        getMyConveyanceType(positionId);
                    }

                    Log.e("PositonId = ", positionId + " " + positionValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        con_bill_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con_bill_date.setText("");
                CalanderHRMS calanderHRMS = new CalanderHRMS(getActivity());
                calanderHRMS.datePicker(con_bill_date);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String today_date = con_bill_date.getText().toString().trim().replace("/", "-");
                            Log.e("Date", today_date);
                            if (!today_date.equals("")) {
                                getCompareTodayDate(today_date);
                            } else {
                                handler.postDelayed(rRunnable, 200);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(rRunnable, 200);

            }
        });

        btn_fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.closeOptionsMenu();
                dialog.cancel();
            }
        });

        btn_AddConveyance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conditionValue = "1";
                String placeFrom = edt_place_from.getText().toString().trim();
                String placeTo = edt_place_to.getText().toString().trim();
                String distance = edt_distance.getText().toString().trim();
                String bill_amt = edt_bill_amount.getText().toString().trim();
                String bill_no = edt_bill_no.getText().toString().trim();
                String con_reason = edt_con_reason.getText().toString().trim();
                String bill_date = con_bill_date.getText().toString().trim();

                Log.e("All Value", "Place From " + placeFrom + ", Place To " + placeTo + ", Distance " + distance +
                        ", Bill Amount " + bill_amt + ", Bill No. " + bill_no + ", Reason " + con_reason + " Bill Date " + bill_date
                        + ", Spinner " + positionValue);
                String re_date = bill_date.replace("/", "-");


                if (positionValue.equals("") || placeFrom.equals("") || placeTo.equals("")
                        || distance.equals("") || bill_amt.equals("") || con_reason.equals("")) {
                    conditionValue = "-1";
                }
                if (EST_IS_BILL_NO_REQUIRED.equals("True")) {
                    if (bill_no.equals("")) {
                        conditionValue = "-1";
                    }
                }
                if (EST_IS_BILL_DATE_REQUIRED.equals("True")) {
                    if (bill_date.equals("")) {
                        conditionValue = "-1";
                    }
                }

                if (conditionValue.equals("1")) {
                    HashMap<String, String> map_data = new HashMap<>();
                    map_data.put("TYPE", positionValue);
                    map_data.put("TYPE_ID", positionId);
                    map_data.put("Place_From", placeFrom);
                    map_data.put("Place_To", placeTo);
                    map_data.put("Distance", distance);
                    map_data.put("Bill_Amount", bill_amt);
                    map_data.put("Bill_No", bill_no);
                    map_data.put("Reason", con_reason);
                    map_data.put("Date", bill_date);
                    map_data.put("displayFileName", displayFileName);
                    map_data.put("actualFileName", actualFileName);
                    arlData.add(map_data);

                    Log.e("EveryThing", "Mapdata " + arlData.toString());
                    mAdapter = new Conveyance_Add_fieldAdapter("Gen", getActivity(), arlData);
                    recyclerView_add_conveyance.setAdapter(mAdapter);
                    dialog.dismiss();
                } else if (conditionValue.equals("-1")) {
                    Toast.makeText(getActivity(), "Fileds are vacent.", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();

    }

    public void getCompareTodayDate(String todayDate) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Today_Date";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("From_Date", todayDate);

//            pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
                                    COMPARE_DATE = response.getString("Compare_Today_DateResult");
                                    Log.e("Compare Date", "" + COMPARE_DATE);
                                    if (COMPARE_DATE.toUpperCase().equals("FALSE")) {
                                        linear_compareDate.setVisibility(View.VISIBLE);
                                        txt_compareDate.setText("Bill date should be less than or equal To current date!");
                                        COMPARE_DATE = "";
                                    } else {
                                        linear_compareDate.setVisibility(View.GONE);
                                        COMPARE_DATE = "";
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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getConveyanceVoucherDetail(String conId, RecyclerView recyclerView) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                final RecyclerView recyclerView1 = recyclerView;
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
                                    HashMap<String, String> map_data;
                                    JSONArray jsonArray = response.getJSONArray("GetEmployeeConveyanceDetailResult");

                                    Log.d("Result", jsonArray.toString());
//
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        mapData = new HashMap<String, String>();
                                        map_data = new HashMap<String, String>();
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

                                        map_data.put("TYPE", EST_EXPENSE_SUB_TYPE);
                                        map_data.put("Place_From", CRD_PLACE_FROM);
                                        map_data.put("Place_To", CRD_PLACE_TO);
                                        map_data.put("Distance", CRD_DISTANCE);
                                        map_data.put("Bill_Amount", CRD_BILL_AMOUNT);
                                        map_data.put("Bill_No", CRD_BILL_NO);
                                        map_data.put("Reason", CRD_REASON);
                                        map_data.put("Date", CRD_BILL_DATE_1);

                                        arlData1.add(mapData);
                                        arlData.add(map_data);
                                    }
                                    Log.e("Conveyance Request", arlData1.toString());
                                    Log.e("Conveyance ARLDATA", arlData.toString());
                                    System.out.println("ArrayList==" + arlData1);
                                    mAdapter = new Conveyance_Add_fieldAdapter("Edit", getActivity(), arlData1);
////                                mAdapter = new StatusConveyanceVoucherAdapter("CONVEYANCE_VOUCHER_DETAIL",getActivity(),coordinatorLayout,arlData1);
                                    recyclerView1.setAdapter(mAdapter);
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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


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
        private ConveyanceRequestFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ConveyanceRequestFragment.ClickListener clickListener) {
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
                displayFileName = fileName(uri);
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
                uploadIcon.setVisibility(View.GONE);
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

                    uploadFiles(displayFileName, uri);
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayFileName = myFile.getName();
        }
    }

    private void uploadFiles(final String pdfname, Uri pdffile) {

        InputStream iStream = null;
        try {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Uploading Image...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            iStream = getActivity().getContentResolver().openInputStream(pdffile);
            final byte[] inputData = getBytes(iStream);
            String upload_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + empoyeeId;

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            progressDialog.dismiss();

                            List<String> list = Arrays.asList(new String(response.data).split(","));
                            int value = Integer.valueOf(list.get(0));
                            if (value == 1) {
                                Toast.makeText(getActivity(), "File uploaded Successfully", Toast.LENGTH_SHORT).show();
                                uploadIcon.setVisibility(View.VISIBLE);
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
                            uploadIcon.setVisibility(View.GONE);
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

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void uploadImage(final Bitmap bitmap) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        String IMG_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + empoyeeId;

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, IMG_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        Log.d("ressssssoo", new String(response.data));

                        List<String> list = Arrays.asList(new String(response.data).split(","));
                        int value = Integer.valueOf(list.get(0));
                        if (value == 1) {
                            Toast.makeText(getActivity(), "Image uploaded Successfully", Toast.LENGTH_SHORT).show();
                            uploadIcon.setVisibility(View.VISIBLE);
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
                        uploadIcon.setVisibility(View.GONE);
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
                params.put("filename", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        if (bitmap != null) {
            int imgquality = 100;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, imgquality, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } else {
            return null;
        }

    }

    private String fileName(Uri uri) {
        Cursor returnCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
}
