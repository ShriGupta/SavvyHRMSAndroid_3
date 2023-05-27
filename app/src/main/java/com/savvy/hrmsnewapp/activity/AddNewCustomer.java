package com.savvy.hrmsnewapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.DashBoardActivity.MY_PREFS_NAME;

public class AddNewCustomer extends BaseActivity implements MenuItem.OnMenuItemClickListener {

    EditText name, mobile, address, email_id;
    Button saveDetail;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences shared;
    String group_Id;
    String employee_Id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_customer);
        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        group_Id = (shared.getString("GroupId", ""));
        employee_Id = (shared.getString("EmpoyeeId", ""));

        setTitle("Add New Customer");
        setUpToolBar();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        name = (EditText) findViewById(R.id.edt_CustomerName);
        mobile = (EditText) findViewById(R.id.edt_MobNumber);
        address = (EditText) findViewById(R.id.edt_CustomerAddress);
        saveDetail = (Button) findViewById(R.id.saveCustomerDetail);
        email_id = (EditText) findViewById(R.id.edt_CustomerMailId);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String locationAddress = bundle.getString("locationAddress");
            address.setText(locationAddress);
        }

        saveDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Name");
                } else if (address.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Address");
                } else {
                    saveCustomerDetail(name.getText().toString().trim().replaceAll("\\s", " "),
                            mobile.getText().toString(),
                            address.getText().toString().trim().replaceAll("\\s", ""),
                            email_id.getText().toString().trim().replaceAll("\\s", ""));
                }
            }
        });
    }

    private void saveCustomerDetail(String name, String mobileNumber, String address, String emailid) {

        if (Utilities.isNetworkAvailable(AddNewCustomer.this)) {

            final String SAVE_CUSTOMER_DETAIL_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveCustomerSalesforcePost";
            Log.d("", "saveCustomerDetail: " + SAVE_CUSTOMER_DETAIL_URL);

            JSONObject params = new JSONObject();
            try {
                params.put("GROUP_ID", group_Id);
                params.put("CUSTOMER_ID", "0");
                params.put("CUSTOMER_NAME", name);
                params.put("CUSTOMER_ALIAS_NAME", "");
                params.put("LOCATION_ID", "0");
                params.put("LOCATION_LANDMARK", "");
                params.put("IS_HEAD_OFFICE", "True");
                params.put("INDUSTRY_ID", "0");
                params.put("ADDRESS", address);
                params.put("LAT", "");
                params.put("LONG", "");
                params.put("PHONE1", mobileNumber);
                params.put("PHONE2", "");
                params.put("PHONE3", "");
                params.put("EMAIL1", emailid);
                params.put("EMAIL2", "");
                params.put("WEBSITE", "");
                params.put("STATUS", "True");
                params.put("USER_ID", employee_Id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SAVE_CUSTOMER_DETAIL_URL, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        int result = Integer.valueOf(response.getString("SaveCustomerSalesforcePostResult"));

                        if (result > 0) {
                            Utilities.showDialog(coordinatorLayout, "Customer Created Successfully");
                            finishActivity();
                        } else if (result == -1) {
                            Utilities.showDialog(coordinatorLayout, "Customer already Exist !");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, error.getMessage());
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

            int socketTimeOut = 3000000;
            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(retryPolicy);
            VolleySingleton.getInstance(AddNewCustomer.this).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }

    }

    private void finishActivity() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }
}
