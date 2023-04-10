package com.savvy.hrmsnewapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CustomCitySpinnerAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.room_database.AdvanceModel;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.DashBoardActivity.MY_PREFS_NAME;


public class AddAdvanceActivity extends AppCompatActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    Button addButton, addcloseButton, closeButton;
    Spinner currecnySpinner, payModeSpinner;
    ArrayList<HashMap<String, String>> currencyList, payModeList;
    CustomCitySpinnerAdapter customSpinnerAdapter;
    CoordinatorLayout coordinatorLayout;
    EditText advanceAmount, remark;
    String currencyValue = "", payModeValue = "";
    String currencyID = "", payModeID = "";
    int currencySelection, paymodeSelection;
    List<AdvanceModel> advanceDataList;
    Handler handler;
    List<String> arrayList;
    String traveWay = "";
    SharedPreferences shared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advance);
        shared = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        setTitle("Advance Details");
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        traveWay = bundle.getString("TRAVEL_TYPE");

        advanceDataList = new ArrayList();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        advanceAmount = (EditText) findViewById(R.id.edt_AdvanceAmount);
        currecnySpinner = (Spinner) findViewById(R.id.accommodeCurrencySpinner);
        payModeSpinner = (Spinner) findViewById(R.id.payModeSpinner);
        remark = (EditText) findViewById(R.id.advanceRemark);

        addButton = (Button) findViewById(R.id.addAdvanceButton);
        addcloseButton = (Button) findViewById(R.id.addcloseButton);
        closeButton = (Button) findViewById(R.id.close_advanceButton);
        addButton.setOnClickListener(this);
        addcloseButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        handler = new Handler();
        android.os.AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                advanceDataList = DatabaseClient.getInstance(AddAdvanceActivity.this).getAppDatabase().passengerDao().getAdvanceData();
            }
        });

        getCurrencyData();
        getPayModeData();
        currecnySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currencySelection = position;
                if (currencySelection == 0) {
                    currencySelection = position;
                } else {
                    CustomTextView textView = currecnySpinner.getSelectedView().findViewById(R.id.txt_City);
                    currencyValue = textView.getText().toString();
                    currencyID = currencyList.get(position - 1).get("KEY");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        payModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                paymodeSelection = position;
                if (paymodeSelection == 0) {
                    paymodeSelection = position;
                } else {
                    CustomTextView textView = payModeSpinner.getSelectedView().findViewById(R.id.txt_City);
                    payModeValue = textView.getText().toString();
                    payModeID = payModeList.get(position - 1).get("KEY");

                    if (payModeValue.equals("CASH")) {
                        if (!advanceAmount.getText().toString().equals("")) {
                            long amount = Integer.valueOf(advanceAmount.getText().toString());
                            if (amount > 5000) {
                                Utilities.showDialog(coordinatorLayout, "Can't Select PayMode CASH if amount greater than 5000 ! ");
                                payModeSpinner.setSelection(0);
                            }
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCurrencyData() {

        final String headerValue = "Select Currency";
        RequestQueue queue = Volley.newRequestQueue(this);
        String CURRENCY_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCurrencyDataFicci";
        Log.d("MODE_URL", "getModeList: " + CURRENCY_URL);

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, CURRENCY_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("TAG", "onResponse: " + response);
                    HashMap<String, String> hashMap;
                    currencyList = new ArrayList<>();

                    if (response.length() > 0) {

                        for (int i = 0; i < response.length(); i++) {
                            hashMap = new HashMap<>();
                            String key = response.getJSONObject(i).getString("TCM_ID");
                            String value = response.getJSONObject(i).getString("TCM_CURRENCY_NAME");
                            hashMap.put("KEY", key);
                            hashMap.put("VALUE", value);
                            currencyList.add(hashMap);
                        }

                        customSpinnerAdapter = new CustomCitySpinnerAdapter(getApplicationContext(), currencyList, headerValue);
                        currecnySpinner.setAdapter(customSpinnerAdapter);
                        if (traveWay.equals("International")) {
                            currecnySpinner.setSelection(3);
                            currecnySpinner.setEnabled(true);
                        } else {
                            currecnySpinner.setSelection(3);
                            currecnySpinner.setEnabled(false);
                        }

                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
        queue.add(stringRequest);
    }

    private void getPayModeData() {

        final String headerValue = "Select Mode";
        RequestQueue queue = Volley.newRequestQueue(this);
        String PAYMENT_MODE = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetPaymentTypeFicci";
        Log.d("MODE_URL", "getModeList: " + PAYMENT_MODE);

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, PAYMENT_MODE, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("TAG", "onResponse: " + response);
                    HashMap<String, String> hashMap;
                    payModeList = new ArrayList<>();

                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            hashMap = new HashMap<>();
                            String key = response.getJSONObject(i).getString("PM_PAYMENT_MODE_ID");
                            String value = response.getJSONObject(i).getString("PM_PAYMENT_MODE");
                            hashMap.put("KEY", key);
                            hashMap.put("VALUE", value);
                            payModeList.add(hashMap);
                        }

                        customSpinnerAdapter = new CustomCitySpinnerAdapter(getApplicationContext(), payModeList, headerValue);
                        payModeSpinner.setAdapter(customSpinnerAdapter);
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
        queue.add(stringRequest);
    }


    @Override
    public void onClick(View v) {

        arrayList = new ArrayList<>();
        switch (v.getId()) {
            case R.id.addAdvanceButton:
            case R.id.addcloseButton:

                if (advanceAmount.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Advance Amount");
                } else if (currencySelection == 0) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Currency");
                } else if (paymodeSelection == 0) {
                    Utilities.showDialog(coordinatorLayout, "Please Select PayMode");
                } else if (remark.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Remarks");
                } else {

                    for (int i = 0; i < advanceDataList.size(); i++) {
                        arrayList.add(advanceDataList.get(i).getPaymode());
                    }
                    if (!arrayList.contains(payModeValue)) {
                        saveData(advanceAmount.getText().toString().trim(), currencyValue, currencyID, payModeValue, payModeID, remark.getText().toString().trim(), v);
                    } else {
                        Utilities.showDialog(coordinatorLayout, "Duplicate PayMode Entry Not Allowed !");
                        return;
                    }
                    advanceDataList = new ArrayList<>();
                    android.os.AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            advanceDataList = DatabaseClient.getInstance(AddAdvanceActivity.this).getAppDatabase().passengerDao().getAdvanceData();
                        }
                    });
                }

                break;
            case R.id.close_advanceButton:
                finish();
                break;
        }
    }

    private void saveData(String amount, String currencyValue, String currencyid, String payModeValue, String payModeid, String remarks, View view) {
        Log.d("TAG", "saveData: ");
        remarks = remarks.replaceAll("\\s", "_");
        final AdvanceModel advanceModel = new AdvanceModel();
        advanceModel.setAmount(amount);
        advanceModel.setCurrency(currencyValue);
        advanceModel.setPaymode(payModeValue);
        advanceModel.setRemarks(remarks);
        advanceModel.setCurrencyid(currencyid);
        advanceModel.setPaymodeid(payModeid);

        android.os.AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long value = DatabaseClient.getInstance(AddAdvanceActivity.this).getAppDatabase().passengerDao().insertAdvanceData(advanceModel);
                Log.d("", "run: " + value);
            }
        });


        advanceAmount.setText("");
        currecnySpinner.setSelection(0);
        payModeSpinner.setSelection(0);
        remark.setText("");

        Utilities.showDialog(coordinatorLayout, "Record inserted successfully..");
        if (view.getId() == R.id.addcloseButton) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
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
