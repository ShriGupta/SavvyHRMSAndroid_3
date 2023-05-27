package com.savvy.hrmsnewapp.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.AddNewCustomer;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Looper.getMainLooper;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class CustomerVisitInOutFragment extends BaseFragment implements View.OnClickListener {
    CustomTextView txt_Time, txt_Day, txt_Date, txt_Address;
    Handler someHandler;
    Runnable runnable;

    AutoCompleteTextView customerSpinner;
    Button add_NewCustomer;
    CoordinatorLayout coordinatorLayout;
    String group_Id, employee_Id;
    ProgressDialog progressDialog;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    ArrayAdapter<String> autoCompleteAdapter;
    List<HashMap<String, String>> customerArrayList;
    List<String> customerArray;
    String customerId = "";
    String userName = "";
    String punchDate_Time = "";
    String locationAddress = "";

    CustomTextView txt_name, txt_phone, txt_address, txt_VisitCustomerLabel;
    CustomTextView txtvisit_name, txtvisit_phone, txtvisit_punch_in, txt_locationAddress, txt_remarks;

    LinearLayout dropDownLinearLayout;
    LinearLayout customerLayout;
    LinearLayout customervisitLayout;

    Button punchIn, punchOut;
    String customer_visit_id = "";
    EditText punchInOut_Remarks;

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    public static double currentLatitude = 0.0;
    public static double currentLongitude = 0.0;
    boolean isLocationEnabled = false;
    boolean isCustomerAdd = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coordinatorLayout = requireActivity().findViewById(R.id.coordinatorLayout);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }

        someHandler = new Handler(getMainLooper());
        shared = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employee_Id = (shared.getString("EmpoyeeId", ""));
        group_Id = (shared.getString("GroupId", ""));
        userName = (shared.getString("UserName", ""));
        getCustomerData(group_Id, employee_Id);

        getCustomerVisitOutPunchPending(employee_Id);
    }

    private void getLastLocation() {
        if (isLocationEnabled()) {
            isLocationEnabled = true;

            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    task -> {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            if (isMockLocationON(location)) {
                                locationAddress = "mocklocation";
                                txt_Address.setText(R.string.mocklocation);
                                showMockAlert();
                            } else {
                                currentLatitude = location.getLatitude();
                                currentLongitude = location.getLongitude();
                                Log.d(TAG, "onComplete: " + currentLatitude + " " + currentLongitude);
                                locationAddress = Utilities.getAddressFromLateLong(getActivity(), location.getLatitude(), location.getLongitude());
                                if (locationAddress.equals("")) {
                                    locationAddress = "Latitude: " + currentLatitude + "\n" + "Longitude: " + currentLongitude;
                                }
                                txt_Address.setText(locationAddress);
                                requestNewLocationData();
                            }
                        }
                    }
            );
        } else {
            moveToLocationSetting();
            isLocationEnabled = false;
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(4000);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            if (isMockLocationON(mLastLocation)) {
                locationAddress = "mocklocation";
                txt_Address.setText(R.string.mocklocation);
                showMockAlert();
            } else {
                currentLatitude = mLastLocation.getLatitude();
                currentLongitude = mLastLocation.getLongitude();

                locationAddress = Utilities.getAddressFromLateLong(getActivity(), mLastLocation.getLatitude(), mLastLocation.getLongitude());
                if (locationAddress.equals("")) {
                    locationAddress = "Latitude: " + currentLatitude + "\n" + "Longitude: " + currentLongitude;
                }
                txt_Address.setText(locationAddress);
            }
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        return Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                showLocationErrorDialog("Location permission not granted !");
                sendErrorToServer(employee_Id, "Location permission not granted !", "Privilage id 69", "", "", "");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customervisit_inout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        txt_VisitCustomerLabel = getActivity().findViewById(R.id.txt_visitLabel);
        txt_Time = getActivity().findViewById(R.id.txt_comVisit_Time);
        txt_Day = getActivity().findViewById(R.id.txt_comVisit_Day);
        txt_Date = getActivity().findViewById(R.id.txt_comVisit_Date);
        txt_Address = getActivity().findViewById(R.id.txt_comVisit_Address);
        customerSpinner = getActivity().findViewById(R.id.customerSpinner);
        add_NewCustomer = getActivity().findViewById(R.id.add_NewCustomer);
        txt_name = getActivity().findViewById(R.id.punch_customerName);
        txt_phone = getActivity().findViewById(R.id.punch_customerPhone);
        txt_address = getActivity().findViewById(R.id.punch_customerAddress);

        txtvisit_name = getActivity().findViewById(R.id.punchvisit_customerName);
        txtvisit_phone = getActivity().findViewById(R.id.punchvisit_customerPhone);
        txtvisit_punch_in = getActivity().findViewById(R.id.punchvisit_PunchIn);
        txt_locationAddress = getActivity().findViewById(R.id.punchvisit_locationAddress);
        txt_remarks = getActivity().findViewById(R.id.punchvisit_remarks);

        dropDownLinearLayout = getActivity().findViewById(R.id.dropDownLinearLayout);
        customerLayout = getActivity().findViewById(R.id.customerLayout);
        customervisitLayout = getActivity().findViewById(R.id.visit_customerLayout);

        punchIn = getActivity().findViewById(R.id.punchIn_Button);
        punchOut = getActivity().findViewById(R.id.punchOut_Button);
        punchInOut_Remarks = getActivity().findViewById(R.id.punchInOut_Remarks);

        punchIn.setOnClickListener(this);
        punchOut.setOnClickListener(this);


        runnable = new Runnable() {
            @Override
            public void run() {
                txt_Time.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        };
        someHandler.postDelayed(runnable, 10);

        txt_Day.setText(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis()));
        String monthname = (String) android.text.format.DateFormat.format("dd MMM yyyy", new Date());
        txt_Date.setText(monthname);


        customerSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String empName = autoCompleteAdapter.getItem(position);
                    if (empName != null) {
                        customerId = customerArrayList.get(customerArray.indexOf(empName)).get("KEY");
                        txt_name.setText(customerArrayList.get(customerArray.indexOf(empName)).get("VALUE"));
                        txt_phone.setText(customerArrayList.get(customerArray.indexOf(empName)).get("phone1"));
                        txt_address.setText(customerArrayList.get(customerArray.indexOf(empName)).get("address"));
                        customerLayout.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onItemClick: " + customerId);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        customerSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    customerLayout.setVisibility(View.GONE);
                }
            }
        });

        add_NewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustomerAdd = true;
                Bundle bundle = new Bundle();
                bundle.putString("locationAddress", locationAddress);
                Intent intent = new Intent(getActivity(), AddNewCustomer.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        someHandler.removeCallbacks(runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        isLocationEnabled = false;
        customerId = "";
        customerSpinner.setText("");
        customerLayout.setVisibility(View.GONE);

        if (isCustomerAdd) {
            isCustomerAdd = false;
            getCustomerData(group_Id, employee_Id);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(final View v) {

        if (checkPermissions()) {
            requestPermissions();
        }

        switch (v.getId()) {
            case R.id.punchIn_Button:
                if (isLocationEnabled()) {
                    getLastLocation();
                } else {
                    moveToLocationSetting();
                    return;
                }

                punchDate_Time = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date()) + " " + new SimpleDateFormat("hh:mm a", Locale.US).format(new Date());
                customerId = customerSpinner.getText().toString().trim().equals("") ? "" : customerId;
                if (locationAddress.equals("mocklocation")) {
                    return;
                } else if (customerId.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Customer Name");
                } else if (!customerArray.contains(customerSpinner.getText().toString())) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Valid Customer Name");
                } else if (punchInOut_Remarks.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Remarks");
                } else {
                    final String punchType = "I";
                    customer_visit_id = customer_visit_id.equals("") ? "0" : customer_visit_id;
                    if (!(currentLatitude == 0.0 && currentLongitude == 0.0)) {
                        Log.e(TAG, "onClick: " + currentLatitude + "," + currentLongitude + "," + locationAddress);
                        savePunchRequest(customer_visit_id, userName, employee_Id, customerId, punchDate_Time, punchInOut_Remarks.getText().toString().trim().replaceAll("\\s", " "),
                                String.valueOf(currentLatitude), String.valueOf(currentLongitude), locationAddress, punchType, v);
                    } else {
                        Utilities.showDialog(coordinatorLayout, "Please wait while fetching your location...");
                        getLastLocation();
                    }
                }

                break;

            case R.id.punchOut_Button:
                if (isLocationEnabled()) {
                    getLastLocation();
                } else {
                    moveToLocationSetting();
                    return;
                }
                punchDate_Time = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date()) + " " + new SimpleDateFormat("hh:mm a", Locale.US).format(new Date());
                if (locationAddress.equals("mocklocation")) {
                    return;
                } else if (punchInOut_Remarks.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Remarks");
                } else {
                    final String punchType = "O";
                    customerId = "0";
                    if (!(currentLatitude == 0.0 && currentLongitude == 0.0)) {
                        Log.e(TAG, "onClick: " + currentLatitude + "," + currentLongitude + "," + locationAddress);
                        savePunchRequest(customer_visit_id, userName, employee_Id, customerId, punchDate_Time, punchInOut_Remarks.getText().toString().trim().replaceAll("\\s", " "),
                                String.valueOf(currentLatitude), String.valueOf(currentLongitude), locationAddress, punchType, v);
                    } else {
                        Utilities.showDialog(coordinatorLayout, "Please wait while fetching your location...");
                        getLastLocation();
                    }
                }
                break;

        }
    }

    private void getCustomerData(String group_id, String USER_ID) {

        if (Utilities.isNetworkAvailable(getActivity())) {
            final String CUSTOMER_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCustomerSalesforce/" + group_id + "/" + USER_ID;
            Log.d(TAG, "getCustomerData: " + CUSTOMER_URL);

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, CUSTOMER_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {

                        Log.i(TAG, "onResponse: " + response);
                        customerArrayList = new ArrayList<>();
                        customerArray = new ArrayList<>();
                        HashMap<String, String> mapdata;
                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                mapdata = new HashMap<>();

                                mapdata.put("KEY", response.getJSONObject(i).getString("CustomerId"));
                                mapdata.put("VALUE", response.getJSONObject(i).getString("CustomerName"));
                                mapdata.put("address", response.getJSONObject(i).getString("address"));
                                mapdata.put("phone1", response.getJSONObject(i).getString("phone1"));
                                customerArray.add(mapdata.put("VALUE", response.getJSONObject(i).getString("CustomerName")));
                                customerArrayList.add(mapdata);
                            }


                            autoCompleteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, customerArray) {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);
                                    ((TextView) v).setTextSize(12);
                                    ((TextView) v).setGravity(Gravity.START);
                                    return v;
                                }
                            };
                            customerSpinner.setAdapter(autoCompleteAdapter);
                        }

                    } catch (Exception e) {
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getCustomerVisitOutPunchPending(String employee_id) {
        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            String CUSTOMERVISIT_INOUT_PUNCH_PENDING = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCustomerVisitOutPunchPending/" + employee_id;
            Log.d(TAG, "getCustomerVisitOutPunchPending: " + CUSTOMERVISIT_INOUT_PUNCH_PENDING);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, CUSTOMERVISIT_INOUT_PUNCH_PENDING, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.length() > 0) {
                            progressDialog.dismiss();
                            customer_visit_id = response.getString("CUSTOMER_VISIT_ID");
                            customerId = "";
                            if (customer_visit_id.equals("")) {
                                txt_VisitCustomerLabel.setText("Visit New Customer");
                                customerSpinner.setText("");
                                dropDownLinearLayout.setVisibility(View.VISIBLE);
                                punchIn.setVisibility(View.VISIBLE);
                                customerLayout.setVisibility(View.GONE);
                                customervisitLayout.setVisibility(View.GONE);
                                punchOut.setVisibility(View.GONE);
                                punchInOut_Remarks.setText("");

                            } else {
                                punchInOut_Remarks.setText("");
                                txt_VisitCustomerLabel.setText("Customer Visit Out Pending");
                                customervisitLayout.setVisibility(View.VISIBLE);
                                punchOut.setVisibility(View.VISIBLE);
                                dropDownLinearLayout.setVisibility(View.GONE);
                                customerLayout.setVisibility(View.GONE);
                                punchIn.setVisibility(View.GONE);

                                txtvisit_name.setText(response.getString("CUSTOMER_NAME"));
                                txtvisit_phone.setText(response.getString("CUSTOMER_MOBILE_NO"));
                                txtvisit_punch_in.setText(response.getString("PUNCHIN_DATETIME"));
                                txt_locationAddress.setText(response.getString("LOCATION_ADDRESS_IN"));
                                txt_remarks.setText(response.getString("PUNCHIN_REMARK"));

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String errorName = Utilities.handleVolleyError(error);
                    Utilities.showDialog(coordinatorLayout, errorName);

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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void savePunchRequest(String customer_visit_id, String userName, final String employee_Id, String customerId, String punchDate_Time, String punchRemarks, String latitude, String longitude, final String locationaddress, String punchType, final View view) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing Punch In Request...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        String PUNCH_IN_REQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveCustomerVisitInOutPost";

        Log.d(TAG, "savePunchIn: " + PUNCH_IN_REQUEST_URL);

        JSONObject param_final = new JSONObject();
        try {
            param_final.put("CUSTOMER_VISIT_ID", customer_visit_id);
            param_final.put("USER_NAME", userName);
            param_final.put("EMPLOYEE_ID", employee_Id);
            param_final.put("CUSTOMER_ID", customerId);
            param_final.put("PUNCH_DATETIME", punchDate_Time);
            param_final.put("PUNCH_REMARKS", punchRemarks);
            param_final.put("LATITUDE", latitude);
            param_final.put("LONGITUDE", longitude);
            param_final.put("LOCATION_ADDRESS", locationaddress);
            param_final.put("PUNCH_TYPE", punchType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, PUNCH_IN_REQUEST_URL, param_final, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    int result = Integer.parseInt(response.getString("SaveCustomerVisitInOutPostResult"));
                    if (view.getId() == R.id.punchOut_Button) {
                        if (result > 0) {
                            Utilities.showDialog(coordinatorLayout, "Customer Visit Out saved successfully.");
                            getCustomerVisitOutPunchPending(employee_Id);
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Error during Visit Out !");
                        }
                    } else {
                        if (result > 0) {
                            Utilities.showDialog(coordinatorLayout, "Customer Visit In saved successfully.");
                            getCustomerVisitOutPunchPending(employee_Id);
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Error during Visit In !");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorToServer(employee_Id, "SaveCustomerVisitInOutPost", e.getMessage(), "privilage id 69", "", "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.showDialog(coordinatorLayout, error.getMessage());
                progressDialog.dismiss();
                locationAddress = "";
                sendErrorToServer(employee_Id, "SaveCustomerVisitInOutPost", error.getMessage(), "privilage id 69", "", "");
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

}


