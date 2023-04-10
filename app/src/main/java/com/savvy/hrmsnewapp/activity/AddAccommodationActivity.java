package com.savvy.hrmsnewapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

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
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.room_database.AccommodationModel;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.TravelDeskItineraryDetails.MY_PREFS_NAME;

public class AddAccommodationActivity extends AppCompatActivity implements View.OnClickListener {

    CoordinatorLayout coordinatorLayout;
    EditText edt_City, edt_HotelLocation;
    Button accommodationFromDate, accommodationToDate, accomoodationCheckinTime, accommodationCheckoutTime;
    Button addButton, add_closeButton, closeButton;
    LinearLayout compareDateLayout;
    CustomTextView compareDateTextView;

    CalanderHRMS calanderHRMS;
    Handler handler;
    Runnable rRunnable;
    String FROM_DATE = "", TO_DATE = "";
    String startDate = "", endDate = "";
    String traveWay = "";
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accommodation);
        shared = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        setTitle("Accommodation Details");
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        traveWay = bundle.getString("Travel_Way");
        startDate = bundle.getString("startDate");
        endDate = bundle.getString("endDate");

        calanderHRMS = new CalanderHRMS(AddAccommodationActivity.this);
        handler = new Handler();

        accommodationFromDate = (Button) findViewById(R.id.accommodationFromDate);
        accommodationToDate = (Button) findViewById(R.id.accommodationToDate);
        accomoodationCheckinTime = (Button) findViewById(R.id.accomoodationCheckinTime);
        accommodationCheckoutTime = (Button) findViewById(R.id.accommodationCheckoutTime);
        addButton = (Button) findViewById(R.id.btn_addAccommodation);
        add_closeButton = (Button) findViewById(R.id.btn_addCloseAccomodation);
        closeButton = (Button) findViewById(R.id.close_accommodationButton);

        edt_City = (EditText) findViewById(R.id.edt_city);
        edt_HotelLocation = (EditText) findViewById(R.id.edt_HotelLocation);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        compareDateLayout = (LinearLayout) findViewById(R.id.copareAccommodationLayout);
        compareDateTextView = (CustomTextView) findViewById(R.id.compareAccommodationTextView);

        accommodationFromDate.setOnClickListener(this);
        accommodationToDate.setOnClickListener(this);
        accomoodationCheckinTime.setOnClickListener(this);
        accommodationCheckoutTime.setOnClickListener(this);
        addButton.setOnClickListener(this);
        add_closeButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        accommodationFromDate.setText(startDate);
        accommodationToDate.setText(endDate);

        if (traveWay.equals("One Way")) {
            accommodationToDate.setEnabled(false);
        } else {
            accommodationToDate.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accommodationFromDate:

                accommodationFromDate.setText("");
                calanderHRMS.datePicker(accommodationFromDate);
                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = accommodationFromDate.getText().toString().trim();
                            TO_DATE = accommodationToDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                compareTravelDates();
                                //getCompareTodayDate(FROM_DATE, TO_DATE);
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
            case R.id.accommodationToDate:

                accommodationToDate.setText("");
                calanderHRMS.datePicker(accommodationToDate);
                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = accommodationFromDate.getText().toString().trim();
                            TO_DATE = accommodationToDate.getText().toString().trim();

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
            case R.id.accomoodationCheckinTime:
                calanderHRMS.timePicker(accomoodationCheckinTime);
                break;
            case R.id.accommodationCheckoutTime:
                calanderHRMS.timePicker(accommodationCheckoutTime);
                break;
            case R.id.btn_addAccommodation:
            case R.id.btn_addCloseAccomodation:

                if (traveWay.equals("One Way")) {
                    if (edt_City.getText().toString().trim().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter City");
                    } else if (accommodationFromDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter From Date");
                    } else if (accomoodationCheckinTime.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Check In Time");
                    } else if (accommodationCheckoutTime.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Check out Time");
                    } else if (edt_HotelLocation.getText().toString().trim().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Hotel Location");
                    } else {
                        saveData(edt_City.getText().toString().trim(),
                                accommodationFromDate.getText().toString(), "",
                                accomoodationCheckinTime.getText().toString(),
                                accommodationCheckoutTime.getText().toString(),
                                edt_HotelLocation.getText().toString().trim(), v);
                    }
                } else {
                    if (edt_City.getText().toString().trim().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter City");
                    } else if (accommodationFromDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter From Date");
                    } else if (accommodationToDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter To Date");
                    } else if (accomoodationCheckinTime.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Check In Time");
                    } else if (accommodationCheckoutTime.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Check out Time");
                    } else if (edt_HotelLocation.getText().toString().trim().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Hotel Location");
                    } else {
                        saveData(edt_City.getText().toString().trim(),
                                accommodationFromDate.getText().toString(),
                                accommodationToDate.getText().toString(),
                                accomoodationCheckinTime.getText().toString(),
                                accommodationCheckoutTime.getText().toString(),
                                edt_HotelLocation.getText().toString().trim(), v);
                    }
                }


                break;
            case R.id.close_accommodationButton:
                finish();
                break;
        }
    }

    private void saveData(String cityvalue, String fromdate, String todate, String fromtime, String totime, String hotellocation, View v) {
        Log.d("Save Data", "saveData: ");

        String city = cityvalue.replaceAll("\\s", "_");
        String hotel = hotellocation.replaceAll("\\s", "_");

        final AccommodationModel accommodationModel = new AccommodationModel();
        accommodationModel.setCity(city);
        accommodationModel.setFromDate(fromdate);
        accommodationModel.setTodate(todate);
        accommodationModel.setCheckintime(fromtime);
        accommodationModel.setCheckouttime(totime);
        accommodationModel.setHotellocation(hotel);

        android.os.AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseClient.getInstance(AddAccommodationActivity.this).getAppDatabase().passengerDao().insertAccommodationData(accommodationModel);
            }
        });

        Utilities.showDialog(coordinatorLayout, "Record Inserted successfully...");
        edt_City.setText("");
        edt_HotelLocation.setText("");
        accomoodationCheckinTime.setText("");
        accommodationCheckoutTime.setText("");

        if (v.getId() == R.id.btn_addCloseAccomodation) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }

    }

    public void getCompareTodayDate(String From_date, String To_date) {
        try {
            if (Utilities.isNetworkAvailable(AddAccommodationActivity.this)) {

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("From_Date", From_date);
                params_final.put("To_Date", To_date);

                RequestQueue requestQueue = Volley.newRequestQueue(AddAccommodationActivity.this);

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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compareTravelDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date start_date = null;
        Date end_date = null;
        Date departure_date = null;
        Date return_date = null;
        try {
            start_date = sdf.parse(startDate);
            departure_date = sdf.parse(FROM_DATE);
            end_date = sdf.parse(endDate);
            return_date = sdf.parse(TO_DATE);

            if (!((departure_date.after(start_date) && departure_date.before(end_date)) || (departure_date.equals(start_date) || departure_date.equals(end_date)))) {
                Utilities.showDialog(coordinatorLayout, "Departure date should be between Travel Start Date and Travel End Date!");
                accommodationFromDate.setText("");
            }
            if (!((return_date.after(start_date) && return_date.before(end_date)) || (return_date.equals(end_date) || return_date.equals(start_date)))) {
                Utilities.showDialog(coordinatorLayout, "Return Date should be between Travel Start Date and Travel End Date!");
                accommodationToDate.setText("");

            }
            if (!(departure_date.before(return_date) || departure_date.equals(return_date))) {
                Utilities.showDialog(coordinatorLayout, "Return Date should be greater than or equal to Departure Date ");
                accommodationToDate.setText("");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
