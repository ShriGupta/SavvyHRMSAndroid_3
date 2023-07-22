package com.savvy.hrmsnewapp.activity;

import static com.savvy.hrmsnewapp.activity.DashBoardActivity.MY_PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

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
import com.savvy.hrmsnewapp.adapter.EmployeeDynamicProfileAdapter;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployeeDetailActivity extends BaseActivity {
    SharedPreferences shared;
    ArrayList<HashMap<String, String>> dynamicArraylist;
    RecyclerView rvList;
    EmployeeDynamicProfileAdapter profileAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);
        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        dynamicArraylist = new ArrayList<>();

        setUpToolBar();
        ImageView imageView = findViewById(R.id.iv_profile);
        rvList = findViewById(R.id.rv_emp_list);
        rvList.setLayoutManager(new LinearLayoutManager(EmployeeDetailActivity.this));
        if (getIntent().getExtras() != null) {
            getEmployeeDetail(getIntent().getExtras().getString("EMP_ID", ""));
            String PHOTO_CODE=getIntent().getExtras().getString("PHOTO_CODE", "");
            if (PHOTO_CODE.equals("")) {
                imageView.setImageResource(R.drawable.profile_image);
            } else {
                Picasso.get().load(PHOTO_CODE).error(R.drawable.profile_image).into(imageView);
            }
        }



    }

    private void getEmployeeDetail(String emp_id) {
        if (Utilities.isNetworkAvailable(EmployeeDetailActivity.this)) {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/EmployeeDetailsPostDynamic";
            JSONObject params_final = new JSONObject();

            try {
                params_final.put("employeeId", emp_id);
                params_final.put("securityToken", shared.getString("Token", ""));
            } catch (JSONException e) {
                Log.e("TAG", "getEmployeeDetail: ",e );
            }
            RequestQueue requestQueue = Volley.newRequestQueue(EmployeeDetailActivity.this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    response -> {

                        Log.e("savvylogs", "getEmployeeDetail = " + response.toString());
                        HashMap<String, String> hashMap;
                        try {
                            JSONArray jarray = response.getJSONArray("EmployeeDetailsPostDynamicResult");

                            if (jarray.length() > 0) {
                                for (int i = 0; i < jarray.length(); i++) {
                                    hashMap = new HashMap<>();
                                    hashMap.put("CaptionKey", jarray.getJSONObject(i).getString("CaptionName"));
                                    hashMap.put("CaptionValue", jarray.getJSONObject(i).getString("CaptionValue"));
                                    dynamicArraylist.add(hashMap);
                                }
                                profileAdapter = new EmployeeDynamicProfileAdapter(EmployeeDetailActivity.this, dynamicArraylist);
                                rvList.setAdapter(profileAdapter);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.e("Error In", "" + ex.getMessage());
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d("Error", "" + error.getMessage());
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
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}