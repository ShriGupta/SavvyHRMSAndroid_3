package com.savvy.hrmsnewapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by orapc7 on 8/10/2017.
 */

public class APIs {

    Context context;
    ArrayList<HashMap<String, String>> arlData, arlData1;
    String empoyeeId = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    CoordinatorLayout coordinatorLayout;

    public APIs(Context context, CoordinatorLayout coordinatorLayout) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        arlData = new ArrayList<HashMap<String, String>>();
        arlData1 = new ArrayList<HashMap<String, String>>();

        shared = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));
    }

    public ArrayList<HashMap<String, String>> getConveyanceForVoucher() {
        try {
            arlData = new ArrayList<HashMap<String, String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetConveyanceForVoucher";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", empoyeeId);

//            pm.put("objConveyanceVoucherRequestStatusInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

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

                                    getConveyanceVoucherDetail(CVRD_CONVEYANCE_REQUEST_ID);

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
                                if (jsonArray.length() == 0) {
                                    Utilities.showDialog(coordinatorLayout, "Data Not Found");
                                } else {
                                    Log.e("ArrayList 1", arlData.toString());
                                    System.out.println("ArrayList==" + arlData);
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
        return arlData;

    }

    public void getConveyanceVoucherDetail(String conId) {
        try {
            arlData1 = new ArrayList<HashMap<String, String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeConveyanceDetail";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("CONVEYANCE_ID", conId);

//            pm.put("objConveyanceVoucherRequestStatusInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

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
