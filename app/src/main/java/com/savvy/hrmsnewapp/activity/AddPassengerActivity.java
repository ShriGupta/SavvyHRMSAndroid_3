package com.savvy.hrmsnewapp.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.CustomCitySpinnerAdapter;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.PassengerModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.DashBoardActivity.MY_PREFS_NAME;

public class AddPassengerActivity extends AppCompatActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    CoordinatorLayout coordinatorLayout;
    Button addPassenger, add_cloasePassenger, closeButton;
    EditText firstname, middlename, lastname, contact, address, age;
    RadioGroup radioPassengerGroup;
    RadioButton guest;

    RadioButton passengerRadioButton;
    RadioButton genderrRadioButton;
    RadioGroup radioGroupGender;
    RadioButton maleRadioButton, femaleRadioButton;
    String gender = "";
    SharedPreferences shared;

    String employeeId = "";
    String type = "0";
    public static String employeeType = "";
    Handler handler;
    Spinner foodSpinner;
    ArrayList<HashMap<String, String>> foodArrayList;
    String foodId = "";
    String foodValue = "";
    ProgressDialog progressDialog;
    int listSize = 0;
    String emptype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_passenger);

        setTitle("Passenger Details");
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shared = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            emptype = bundle.getString("emptype");
        }
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        addPassenger = (Button) findViewById(R.id.addPassenger);
        add_cloasePassenger = (Button) findViewById(R.id.add_cloasePassenger);
        closeButton = (Button) findViewById(R.id.close_Button);

        firstname = (EditText) findViewById(R.id.firstName);
        middlename = (EditText) findViewById(R.id.middleName);
        lastname = (EditText) findViewById(R.id.lastName);
        contact = (EditText) findViewById(R.id.contactNumber);
        address = (EditText) findViewById(R.id.addressValue);
        age = (EditText) findViewById(R.id.age);

        maleRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
        femaleRadioButton = (RadioButton) findViewById(R.id.femaleRadioButton);

        radioPassengerGroup = (RadioGroup) findViewById(R.id.radioPassengerGroup);
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        guest = (RadioButton) findViewById(R.id.guest);
        handler = new Handler();
        foodSpinner = (Spinner) findViewById(R.id.foodPrefSpinner);

        getFoodSpinData();
        foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                foodId = "";
                foodValue = "";
                if (position > 0) {
                    foodId = foodArrayList.get(position - 1).get("KEY");
                    foodValue = foodArrayList.get(position - 1).get("VALUE");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (guest.isChecked()) {
            employeeType = "Guest";
        }


        radioPassengerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                passengerRadioButton = (RadioButton) findViewById(checkedId);
                employeeType = passengerRadioButton.getText().toString();
                if (passengerRadioButton.getText().equals("Self")) {

                    if (emptype.equals("Self")) {
                        addPassenger.setEnabled(false);
                        add_cloasePassenger.setEnabled(false);
                        Utilities.showDialog(coordinatorLayout, "Can not add self record in Passenger List !");
                    } else {
                        getEmployeeDetail();
                    }
                } else {
                    firstname.setText("");
                    middlename.setText("");
                    lastname.setText("");
                    contact.setText("");
                    address.setText("");
                    age.setText("");
                    addPassenger.setEnabled(true);
                    add_cloasePassenger.setEnabled(true);
                    maleRadioButton.setChecked(false);
                    femaleRadioButton.setChecked(false);
                }
            }
        });
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.maleRadioButton:
                        genderrRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
                        gender = genderrRadioButton.getText().toString();
                        maleRadioButton.setChecked(true);
                        break;
                    case R.id.femaleRadioButton:
                        genderrRadioButton = (RadioButton) findViewById(R.id.femaleRadioButton);
                        gender = genderrRadioButton.getText().toString();
                        femaleRadioButton.setChecked(true);
                        break;
                }
            }
        });

        addPassenger.setOnClickListener(this);
        add_cloasePassenger.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void getFoodSpinData() {
        if (Utilities.isNetworkAvailable(AddPassengerActivity.this)) {

            final String GET_FOOD_DATA = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetFoodPreference";

            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, GET_FOOD_DATA, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    try {
                        foodArrayList = new ArrayList<>();
                        HashMap<String, String> hashMap;
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {

                                hashMap = new HashMap();
                                hashMap.put("KEY", response.getJSONObject(i).getString("TFP_ID"));
                                hashMap.put("VALUE", response.getJSONObject(i).getString("TFP_FOOD_TYPE"));
                                foodArrayList.add(hashMap);
                            }

                            CustomCitySpinnerAdapter citySpinnerAdapter = new CustomCitySpinnerAdapter(AddPassengerActivity.this, foodArrayList, "Please Select");
                            foodSpinner.setAdapter(citySpinnerAdapter);
                            foodSpinner.setSelection(1);
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Data Not Found In Food Type List");
                        }


                    } catch (Exception e) {
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
            stringRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(AddPassengerActivity.this).addToRequestQueue(stringRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getEmployeeDetail() {

        if (Utilities.isNetworkAvailable(AddPassengerActivity.this)) {
            progressDialog = new ProgressDialog(AddPassengerActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            String EMPLOYEE_DETAIL_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeDetailsFicci/" + employeeId + "/" + type;

            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, EMPLOYEE_DETAIL_URL,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                progressDialog.dismiss();
                                if (response.length() > 0) {
                                    firstname.setText(response.getJSONObject(0).getString("FIRST_NAME"));
                                    middlename.setText(response.getJSONObject(0).getString("MIDDLE_NAME"));
                                    lastname.setText(response.getJSONObject(0).getString("LAST_NAME"));
                                    contact.setText(response.getJSONObject(0).getString("CONTACT_NO"));
                                    address.setText(response.getJSONObject(0).getString("ADDRESS"));
                                    age.setText(response.getJSONObject(0).getString("AGE"));

                                    String genderValue = response.getJSONObject(0).getString("GENDER");

                                    if (genderValue.equals("MALE")) {
                                        maleRadioButton.setChecked(true);
                                    } else {
                                        femaleRadioButton.setChecked(true);
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
            queue.add(stringRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPassenger:
            case R.id.add_cloasePassenger:

                if (maleRadioButton.isChecked()) {
                    gender = maleRadioButton.getText().toString();
                } else {
                    gender = femaleRadioButton.getText().toString();
                }
                if (firstname.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.FIRST_NAME);
                } else if (contact.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.ENTER_CONTACTNUMBER);
                } else if (age.getText().toString().trim().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Age");
                } else if (!((maleRadioButton.isChecked()) || (femaleRadioButton.isChecked()))) {
                    Utilities.showDialog(coordinatorLayout, "Please select gender");
                } else if (!isValidPhoneNumber(contact.getText())) {
                    Utilities.showDialog(coordinatorLayout, "Invalid Contact Number");
                } else {
                    savePassengerData(firstname.getText().toString().trim(),
                            middlename.getText().toString().trim(),
                            lastname.getText().toString().trim(),
                            contact.getText().toString().trim(),
                            address.getText().toString().trim(),
                            Integer.valueOf(age.getText().toString().trim()), foodId, foodValue,
                            gender, employeeType, v);
                }

                break;
            case R.id.close_Button:
                finish();
                break;
        }
    }

    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target.length() != 10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    private void savePassengerData(String fName, String mName, String lName, String contactNumber, String addressValue, int ageValue, String foodId, String foodValue, String genderValue, String employeeType, View view) {

        final PassengerModel passengerModel = new PassengerModel();
        passengerModel.setFirstname(fName);
        passengerModel.setMiddlename(mName);
        passengerModel.setLastname(lName);
        passengerModel.setContact(contactNumber);
        passengerModel.setAddress(addressValue);
        passengerModel.setAge(ageValue);
        passengerModel.setFoodId(foodId);
        passengerModel.setFoodValue(foodValue);
        passengerModel.setGender(genderValue);
        passengerModel.setEmployeetype(employeeType);

        android.os.AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().passengerDao().insertPassengerData(passengerModel);
            }
        });

        firstname.setText("");
        middlename.setText("");
        lastname.setText("");
        contact.setText("");
        address.setText("");
        age.setText("");
        if (foodArrayList != null) {
            foodArrayList.clear();
        }
        maleRadioButton.setChecked(false);
        femaleRadioButton.setChecked(false);
        Utilities.showDialog(coordinatorLayout, "Record Inserted Successfully");

        if (view.getId() == R.id.add_cloasePassenger) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
