package com.savvy.hrmsnewapp.utils;

import android.content.Context;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by orapc7 on 8/20/2017.
 */

public class CompareDate {

    Context context;
    boolean resultDate;

    public CompareDate(Context context) {
        this.context = context;
    }

    public String GetCompareDateResult(String fromDate, String toDate) throws InterruptedException {

        String From_Date = fromDate;
        String To_Date = toDate;

        try {

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("From_Date", From_Date);
            params_final.put("To_Date", To_Date);

//            pm.put("objConveyanceVoucherRequestStatusInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                resultDate = response.getBoolean("Compare_DateResult");

                                Constants.COMPARE_DATE = "" + resultDate;
                                Log.d("Result Value", "" + Constants.COMPARE_DATE);


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
        Thread.sleep(5000);
        Log.e("Result Value", "Hello " + Constants.COMPARE_DATE);
        return Constants.COMPARE_DATE;
    }


}
