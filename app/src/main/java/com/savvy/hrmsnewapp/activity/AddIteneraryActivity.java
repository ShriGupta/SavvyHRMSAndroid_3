package com.savvy.hrmsnewapp.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CustomCitySpinnerAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.IteneraryModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.DashBoardActivity.MY_PREFS_NAME;

public class AddIteneraryActivity extends AppCompatActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    CoordinatorLayout coordinatorLayout;
    Button closeIteneraryButton;
    Spinner modeSpinner, classCodeSpinner;
    EditText flightDetails;
    private LinearLayout compareDateLayout;
    private CustomTextView compareDateTextView;
    Handler handler;
    Button iteneraryDepartureDate, inteneraryReturnDate, itenerary_FromTime, itenerary_ToTime, itenerayAddButton, iteraryAddCloseButton;

    ArrayList<HashMap<String, String>> travelList, modeList, classList;
    CustomCitySpinnerAdapter customSpinnerAdapter;
    ProgressDialog progressDialog;
    static int modeSelectionPosition;
    static int classSelectionPosition;


    CalanderHRMS calanderHRMS;
    Runnable rRunnable;
    String FROM_DATE = "", TO_DATE = "";
    String sourceId = "", destinationId = "", modeId = "", classId = "";
    String sourceValue = "", destinationValue = "", modeValue = "", classValue = "";
    String traveWay = "";
    int iteneraylistSize;
    String startDate = "", endDate = "";

    AutoCompleteTextView autoCompleteSourceList, autoCompleteDestinationList;
    ArrayAdapter<String> autoCompleteSourceAdapter, autoCompleteDestinationAdapter;
    ArrayList<String> travelArray;

    Spinner seatPrefSpinner;
    CheckBox insuranceCheckBox;
    EditText fFillerNo, specialRequest;

    ArrayList<HashMap<String, String>> seatPrefArrayList;
    String seatPrefId = "";
    String seatPrefValue = "";
    String insuranceCheckboxValue = "false";
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_itenerary);
        handler = new Handler();
        shared = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        setTitle("Itenerary Details");
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        seatPrefSpinner = (Spinner) findViewById(R.id.seatPrefSpinner);
        insuranceCheckBox = (CheckBox) findViewById(R.id.insuranceCheckBox);
        fFillerNo = (EditText) findViewById(R.id.fFillerNo);
        specialRequest = (EditText) findViewById(R.id.specialRequest);

        //todo above task

        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.coordinatorLayout);
        autoCompleteSourceList = (AutoCompleteTextView) findViewById(R.id.sourceSpinner);
        autoCompleteDestinationList = (AutoCompleteTextView) findViewById(R.id.destinationSpinner);

        Bundle bundle = getIntent().getExtras();
        final String travelType = bundle.getString("TRAVEL_TYPE");
        traveWay = bundle.getString("Travel_Way");
        iteneraylistSize = Integer.valueOf(bundle.getString("ItenerarySize"));

        startDate = bundle.getString("startDate");
        endDate = bundle.getString("endDate");

        getCityList();
        /*if (travelType.equals("Domestic")) {
            getCityList();
        } else {
            getCountryList();
        }*/

        getModeList();

        modeSpinner = (Spinner) findViewById(R.id.modeSpinner);
        classCodeSpinner = (Spinner) findViewById(R.id.classCodeSpinner);
        calanderHRMS = new CalanderHRMS(AddIteneraryActivity.this);

        iteneraryDepartureDate = (Button) findViewById(R.id.iteneraryDepartureDate);
        inteneraryReturnDate = (Button) findViewById(R.id.inteneraryReturnDate);
        itenerary_FromTime = (Button) findViewById(R.id.itenerary_FromTime);
        itenerary_ToTime = (Button) findViewById(R.id.itenerary_ToTime);
        itenerayAddButton = (Button) findViewById(R.id.itenerayAddButton);

        iteraryAddCloseButton = (Button) findViewById(R.id.iteraryAddCloseButton);
        closeIteneraryButton = (Button) findViewById(R.id.close_IteneraryButton);
        flightDetails = (EditText) findViewById(R.id.flightDetails);

        compareDateLayout = (LinearLayout) findViewById(R.id.compareIteneraryDateLayout);
        compareDateTextView = (CustomTextView) findViewById(R.id.compareIteneraryDateTextview);

        iteneraryDepartureDate.setOnClickListener(this);
        inteneraryReturnDate.setOnClickListener(this);
        itenerary_FromTime.setOnClickListener(this);
        itenerary_ToTime.setOnClickListener(this);
        itenerayAddButton.setOnClickListener(this);
        iteraryAddCloseButton.setOnClickListener(this);
        closeIteneraryButton.setOnClickListener(this);

        if (traveWay.equals("One Way")) {
            iteneraryDepartureDate.setEnabled(false);
        }

        if (traveWay.equals("One Way") && iteneraylistSize == 1) {
            iteneraryDepartureDate.setEnabled(false);
            itenerayAddButton.setEnabled(false);
            iteraryAddCloseButton.setEnabled(false);
            Utilities.showDialog(coordinatorLayout, "Can not add more than one Record in One Way !");
        }
        if (traveWay.equals("Round Trip") && iteneraylistSize == 2) {
            itenerayAddButton.setEnabled(false);
            iteraryAddCloseButton.setEnabled(false);
            Utilities.showDialog(coordinatorLayout, "Can not add more than one data in Round Trip");
        }
        iteneraryDepartureDate.setText(startDate);
        inteneraryReturnDate.setText(endDate);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        autoCompleteSourceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String empName = autoCompleteSourceAdapter.getItem(position);
                sourceId = travelList.get(travelArray.indexOf(empName)).get("KEY");
                sourceValue = travelList.get(travelArray.indexOf(empName)).get("VALUE");
                if (sourceValue.equals(destinationValue)) {
                    Utilities.showDialog(coordinatorLayout, "Source and Destination can not be same! ");
                    autoCompleteSourceList.setText("");
                }
            }
        });

        autoCompleteDestinationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String empName = autoCompleteDestinationAdapter.getItem(position);
                destinationId = travelList.get(travelArray.indexOf(empName)).get("KEY");
                destinationValue = travelList.get(travelArray.indexOf(empName)).get("VALUE");
                if (sourceValue.equals(destinationValue)) {
                    Utilities.showDialog(coordinatorLayout, "Source and Destination can not be same! ");
                    autoCompleteDestinationList.setText("");
                }

            }
        });


        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                modeSelectionPosition = position;
                if (position > 0) {
                    modeSelectionPosition = position;
                    modeId = modeList.get(position - 1).get("KEY");
                    modeValue = modeList.get(position - 1).get("VALUE");

                    getClassCodeList(modeId);
                    getSeatPrefenceData(modeId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classSelectionPosition = position;
                if (position > 0) {
                    classSelectionPosition = position;
                    classId = classList.get(position - 1).get("KEY");
                    classValue = classList.get(position - 1).get("VALUE");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        seatPrefSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seatPrefId = "";
                seatPrefValue = "";
                if (position > 0) {
                    seatPrefId = seatPrefArrayList.get(position - 1).get("KEY");
                    seatPrefValue = seatPrefArrayList.get(position - 1).get("VALUE");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        insuranceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    insuranceCheckboxValue = "true";
                } else {

                    insuranceCheckboxValue = "false";
                }
            }
        });
        if (traveWay.equals("One Way")) {
            inteneraryReturnDate.setEnabled(false);
        }
    }

    private void getSeatPrefenceData(String modeId) {

        if (Utilities.isNetworkAvailable(AddIteneraryActivity.this)) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String SEAT_PREFERENCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetSeatPreference/" + modeId;
            Log.d("CITY_URL", "getCityList: " + SEAT_PREFERENCE_URL);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, SEAT_PREFERENCE_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {

                        HashMap<String, String> hashMap;
                        seatPrefArrayList = new ArrayList<>();

                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                hashMap = new HashMap<>();
                                hashMap.put("KEY", response.getJSONObject(i).getString("TSR_ID"));
                                hashMap.put("VALUE", response.getJSONObject(i).getString("TSR_SEAT_NAME"));
                                seatPrefArrayList.add(hashMap);
                            }

                            CustomCitySpinnerAdapter mAdapter = new CustomCitySpinnerAdapter(AddIteneraryActivity.this, seatPrefArrayList, "Please Select");
                            seatPrefSpinner.setAdapter(mAdapter);
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
            jsonArrayRequest.setRetryPolicy(policy);
            queue.add(jsonArrayRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getCityList() {

        if (Utilities.isNetworkAvailable(this)) {
            progressDialog = new ProgressDialog(AddIteneraryActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            String CITY_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCityFicci";

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CITY_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        progressDialog.dismiss();
                        HashMap<String, String> hashMap;
                        travelList = new ArrayList<>();
                        travelArray = new ArrayList<>();

                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                hashMap = new HashMap<>();
                                String key = response.getJSONObject(i).getString("CityId");
                                String value = response.getJSONObject(i).getString("CityName");
                                hashMap.put("KEY", key);
                                hashMap.put("VALUE", value);
                                travelArray.add(response.getJSONObject(i).getString("CityName"));
                                travelList.add(hashMap);
                            }
                            autoCompleteSourceAdapter = new ArrayAdapter<String>(AddIteneraryActivity.this, android.R.layout.simple_list_item_1, travelArray) {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);
                                    ((TextView) v).setTextSize(12);
                                    ((TextView) v).setGravity(Gravity.LEFT);
                                    return v;
                                }
                            };
                            autoCompleteSourceList.setAdapter(autoCompleteSourceAdapter);

                            autoCompleteDestinationAdapter = new ArrayAdapter<String>(AddIteneraryActivity.this, android.R.layout.simple_list_item_1, travelArray) {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);

                                    ((TextView) v).setTextSize(12);
                                    ((TextView) v).setGravity(Gravity.LEFT);
                                    return v;
                                }
                            };
                            autoCompleteDestinationList.setAdapter(autoCompleteDestinationAdapter);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, error.toString());
                    progressDialog.dismiss();
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
            jsonArrayRequest.setRetryPolicy(policy);
            queue.add(jsonArrayRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }


    }

    private void getCountryList() {

        if (Utilities.isNetworkAvailable(this)) {
            progressDialog = new ProgressDialog(AddIteneraryActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            RequestQueue queue = Volley.newRequestQueue(this);
            String COUNTRY_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCountryFicci";

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, COUNTRY_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        progressDialog.dismiss();
                        HashMap<String, String> hashMap;
                        travelList = new ArrayList<>();
                        travelArray = new ArrayList<>();

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                hashMap = new HashMap<>();
                                String key = response.getJSONObject(i).getString("CountryId");
                                String value = response.getJSONObject(i).getString("CountryName");
                                hashMap.put("KEY", key);
                                hashMap.put("VALUE", value);
                                travelArray.add(response.getJSONObject(i).getString("CountryName"));
                                travelList.add(hashMap);
                            }

                            autoCompleteSourceAdapter = new ArrayAdapter<String>(AddIteneraryActivity.this, android.R.layout.simple_list_item_1, travelArray) {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);

                                    ((TextView) v).setTextSize(12);
                                    ((TextView) v).setGravity(Gravity.LEFT);
                                    return v;
                                }
                            };
                            autoCompleteSourceList.setAdapter(autoCompleteSourceAdapter);

                            autoCompleteDestinationAdapter = new ArrayAdapter<String>(AddIteneraryActivity.this, android.R.layout.simple_list_item_1, travelArray) {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);

                                    ((TextView) v).setTextSize(12);
                                    ((TextView) v).setGravity(Gravity.LEFT);
                                    return v;
                                }
                            };
                            autoCompleteDestinationList.setAdapter(autoCompleteDestinationAdapter);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    progressDialog.dismiss();
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
            jsonArrayRequest.setRetryPolicy(policy);
            queue.add(jsonArrayRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getModeList() {

        if (Utilities.isNetworkAvailable(this)) {
            final String headerValue = "Select Mode";
            RequestQueue queue = Volley.newRequestQueue(this);
            String MODE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelModeFicci";
            Log.d("MODE_URL", "getModeList: " + MODE_URL);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, MODE_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        Log.d("TAG", "onResponse: " + response);
                        HashMap<String, String> hashMap;
                        modeList = new ArrayList<>();

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                hashMap = new HashMap<>();
                                String key = response.getJSONObject(i).getString("TM_TRAVEL_MODE_ID");
                                String value = response.getJSONObject(i).getString("TM_TRAVEL_MODE_NAME");
                                hashMap.put("KEY", key);
                                hashMap.put("VALUE", value);
                                modeList.add(hashMap);
                            }

                            customSpinnerAdapter = new CustomCitySpinnerAdapter(getApplicationContext(), modeList, headerValue);
                            modeSpinner.setAdapter(customSpinnerAdapter);
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
            jsonArrayRequest.setRetryPolicy(policy);
            queue.add(jsonArrayRequest);


        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getClassCodeList(String modeId) {

        if (Utilities.isNetworkAvailable(getApplicationContext())) {
            progressDialog = new ProgressDialog(AddIteneraryActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            final String headerValue = "Select Class";
            RequestQueue queue = Volley.newRequestQueue(this);
            String CLASS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelClassFicci/" + modeId;
            Log.d("CLASS_URL", "getModeList: " + CLASS_URL);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CLASS_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        progressDialog.dismiss();
                        Log.d("TAG", "onResponse: " + response);
                        HashMap<String, String> hashMap;
                        classList = new ArrayList<>();

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                hashMap = new HashMap<>();
                                String key = response.getJSONObject(i).getString("TC_TRAVEL_CLASS_ID");
                                String value = response.getJSONObject(i).getString("TC_TRAVEL_CLASS_NAME");
                                hashMap.put("KEY", key);
                                hashMap.put("VALUE", value);
                                classList.add(hashMap);
                            }
                            customSpinnerAdapter = new CustomCitySpinnerAdapter(getApplicationContext(), classList, headerValue);
                            classCodeSpinner.setAdapter(customSpinnerAdapter);

                        } else {
                            progressDialog.dismiss();
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
            jsonArrayRequest.setRetryPolicy(policy);
            queue.add(jsonArrayRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iteneraryDepartureDate:

                iteneraryDepartureDate.setText("");
                calanderHRMS.datePicker(iteneraryDepartureDate);
                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = iteneraryDepartureDate.getText().toString().trim();
                            TO_DATE = inteneraryReturnDate.getText().toString().trim();
                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                compareTravelDates();
                                // getCompareTodayDate(FROM_DATE, TO_DATE);
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

                break;
            case R.id.inteneraryReturnDate:
                inteneraryReturnDate.setText("");
                calanderHRMS.datePicker(inteneraryReturnDate);
                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = iteneraryDepartureDate.getText().toString().trim();
                            TO_DATE = inteneraryReturnDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                compareTravelDates();
                                // getCompareTodayDate(FROM_DATE, TO_DATE);
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
                break;
            case R.id.itenerary_FromTime:
                calanderHRMS.timePicker(itenerary_FromTime);
                break;
            case R.id.itenerary_ToTime:
                calanderHRMS.timePicker(itenerary_ToTime);
                break;
            case R.id.itenerayAddButton:
            case R.id.iteraryAddCloseButton:

                if (traveWay.equals("One Way")) {
                    if (isValidSource()) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Valid Source");
                    } else if (isValidDestination()) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Valid Destination");
                    } else if (iteneraryDepartureDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Departure Date");
                    } else if (modeSelectionPosition == 0) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Mode");
                    } else if (classSelectionPosition == 0) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Class");
                    } else {
                        saveData(sourceValue, sourceId, destinationValue, destinationId,
                                iteneraryDepartureDate.getText().toString(),
                                "",
                                modeValue, modeId,
                                classValue, classId,
                                itenerary_FromTime.getText().toString(),
                                itenerary_ToTime.getText().toString(),
                                flightDetails.getText().toString(), traveWay, v, seatPrefId, seatPrefValue, insuranceCheckboxValue, fFillerNo.getText().toString(), specialRequest.getText().toString().trim().replaceAll("\\s", " "));
                    }

                } else {
                    if (isValidSource()) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Valid Source");
                    } else if (isValidDestination()) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Valid Destination");
                    } else if (iteneraryDepartureDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Departure Date");
                    } else if (inteneraryReturnDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Return Date");
                    } else if (modeSelectionPosition == 0) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Mode");
                    } else if (classSelectionPosition == 0) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Class");
                    } else {
                        saveData(sourceValue, sourceId, destinationValue, destinationId,
                                iteneraryDepartureDate.getText().toString(),
                                inteneraryReturnDate.getText().toString(),
                                modeValue, modeId,
                                classValue, classId,
                                itenerary_FromTime.getText().toString(),
                                itenerary_ToTime.getText().toString(),
                                flightDetails.getText().toString(), traveWay, v, seatPrefId, seatPrefValue, insuranceCheckboxValue, fFillerNo.getText().toString(), specialRequest.getText().toString().trim().replaceAll("\\s", ""));
                    }

                }

                break;

            case R.id.close_IteneraryButton:
                finish();
                break;
        }
    }

    private boolean isValidDestination() {

        String value = autoCompleteDestinationList.getText().toString();
        boolean isvalidValue = true;

        if (travelList.size() > 0) {
            for (int i = 0; i < travelList.size(); i++) {
                if (value.equals(travelList.get(i).get("VALUE"))) {
                    isvalidValue = false;
                    if (!value.equals(destinationValue)) {
                        destinationId = travelList.get(i).get("KEY");
                        destinationValue = travelList.get(i).get("VALUE");
                    }

                }
            }
        }
        return isvalidValue;
    }

    private boolean isValidSource() {

        String value = autoCompleteSourceList.getText().toString();
        boolean isvalidValue = true;

        if (travelList.size() > 0) {
            for (int i = 0; i < travelList.size(); i++) {
                if (value.equals(travelList.get(i).get("VALUE"))) {
                    isvalidValue = false;
                    if (!value.equals(sourceValue)) {
                        sourceId = travelList.get(i).get("KEY");
                        sourceValue = travelList.get(i).get("VALUE");
                    }
                }
            }
        }
        return isvalidValue;
    }

    private void compareTravelDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date start_date = null;
        Date end_date = null;
        Date departure_date = null;
        Date return_date = null;
        try {
            start_date = sdf.parse(startDate);
            departure_date = sdf.parse(iteneraryDepartureDate.getText().toString());
            end_date = sdf.parse(endDate);
            return_date = sdf.parse(inteneraryReturnDate.getText().toString());

            if (!((departure_date.after(start_date) && departure_date.before(end_date)) || (departure_date.equals(start_date) || departure_date.equals(end_date)))) {
                Utilities.showDialog(coordinatorLayout, "Departure date should be between Travel Start Date and Travel End Date!");
                iteneraryDepartureDate.setText("");
            }
            if (!((return_date.after(start_date) && return_date.before(end_date)) || (return_date.equals(end_date) || return_date.equals(start_date)))) {
                Utilities.showDialog(coordinatorLayout, "Return Date should be between Travel Start Date and Travel End Date!");
                inteneraryReturnDate.setText("");

            }
            if (!(departure_date.before(return_date) || departure_date.equals(return_date))) {
                Utilities.showDialog(coordinatorLayout, "Reutrn Date should be greater than or equal to Departure Date ");
                inteneraryReturnDate.setText("");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveData(String sourceValue, String sourceid, String destinationValue, String destinationid, String departureDate, String endDate, String modeValue, String modeid, String classValue, String classid, String startTime, String endTime, String fDetail, String travelway, View view,
                          String seatPrefId, String seatPrefValue, String insuranceCheckboxValue, String fFiller_No, String specialRequestValue) {

        fDetail = fDetail.replaceAll("\\s", "_");

        final IteneraryModel iteneraryModel = new IteneraryModel();
        iteneraryModel.setSource(sourceValue);
        iteneraryModel.setDestination(destinationValue);
        iteneraryModel.setDeparturedate(departureDate);
        iteneraryModel.setReturndate(endDate);
        iteneraryModel.setMode(modeValue);
        iteneraryModel.setClasscode(classValue);
        iteneraryModel.setStarttime(startTime);
        iteneraryModel.setEndtime(endTime);
        iteneraryModel.setFlightdetail(fDetail);
        iteneraryModel.setTravelwaytype(travelway);

        iteneraryModel.setSourceid(sourceid);
        iteneraryModel.setDestinationid(destinationid);
        iteneraryModel.setModeid(modeid);
        iteneraryModel.setClassid(classid);

        iteneraryModel.setSeatprefid(seatPrefId);
        iteneraryModel.setSeatpreValue(seatPrefValue);
        iteneraryModel.setInsuranceValue(insuranceCheckboxValue);
        iteneraryModel.setFrequentlyFillerno(fFiller_No);
        iteneraryModel.setSpecialRequest(specialRequestValue);

        android.os.AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long value = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().passengerDao().insertIteneraryData(iteneraryModel);
                Log.d("Inserting Value", "run: " + value);
            }
        });

        autoCompleteSourceList.setText("");
        autoCompleteDestinationList.setText("");

        itenerary_FromTime.setText("");
        itenerary_ToTime.setText("");
        fFillerNo.setText("");
        specialRequest.setText("");
        insuranceCheckBox.setChecked(false);

        modeSpinner.setSelection(0);
        classCodeSpinner.setSelection(0);
        flightDetails.setText("");

        if (traveWay.equals("One Way") || traveWay.equals("Round Trip")) {
            itenerayAddButton.setEnabled(false);
            iteraryAddCloseButton.setEnabled(false);
        } else {
            itenerayAddButton.setEnabled(true);
            iteraryAddCloseButton.setEnabled(true);
        }

        if (view.getId() == R.id.iteraryAddCloseButton) {
            Utilities.showDialog(coordinatorLayout, "Record inserted successfully");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
        if (view.getId() == R.id.itenerayAddButton) {
            Utilities.showDialog(coordinatorLayout, "Record Inserted successfully");
        }
    }

    public void getCompareTodayDate(String From_date, String To_date) {
        try {
            if (Utilities.isNetworkAvailable(AddIteneraryActivity.this)) {
                String From_Date = From_date;
                String To_Date = To_date;

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("From_Date", From_Date);
                params_final.put("To_Date", To_Date);

                RequestQueue requestQueue = Volley.newRequestQueue(AddIteneraryActivity.this);

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
                                        compareDateTextView.setText("From Date should be less than or equal To Date!");
                                        compareDateLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        compareDateLayout.setVisibility(View.GONE);
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
