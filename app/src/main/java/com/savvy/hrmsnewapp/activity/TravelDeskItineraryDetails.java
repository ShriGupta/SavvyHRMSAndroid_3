package com.savvy.hrmsnewapp.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.TravelDeskItineraryDetailsAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class TravelDeskItineraryDetails extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    TravelDeskItineraryDetailsAdapter adapter;
    CoordinatorLayout coordinatorLayout;
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> arrayList;
    CustomTextView noDataFound;
    RecyclerView recyclerView;
    Button itinerarySubmitTicket;
    LinearLayout travelItineraryLinearLayout;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    String requestId = "";
    boolean isBalanceZero = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_desk_itinerary_details);

        setTitle("Itinerary Details");
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        arrayList = new ArrayList<>();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        noDataFound = (CustomTextView) findViewById(R.id.txt_travelDeskItinerary);
        travelItineraryLinearLayout = (LinearLayout) findViewById(R.id.travelItineraryLinearLayout);
        itinerarySubmitTicket = (Button) findViewById(R.id.itinerarySubmitTicket);
        recyclerView = (RecyclerView) findViewById(R.id.travelDeskItineraryRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TravelDeskItineraryDetails.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new TravelDeskItineraryDetails.RecyclerTouchListener(TravelDeskItineraryDetails.this, recyclerView, new TravelDeskItineraryDetails.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Log.d(TAG, "onClick:");

                Button addTicket = view.findViewById(R.id.travelDeskItineraryAddTicket);
                Button download = view.findViewById(R.id.travelDeskItineraryDownload);

                addTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final HashMap<String, String> mapdata = arrayList.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("ITINERARY_ID", mapdata.get("ITINERARY_ID"));
                        bundle.putString("TRF_TRAVEL_REQUEST_ID", requestId);
                        Intent intent = new Intent(TravelDeskItineraryDetails.this, TravelDeskRequest.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @Override
            public void onLongClick(View view, int posotion) {
                Log.d(ContentValues.TAG, "onLongClick: ");
            }
        }));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            requestId = bundle.getString("TRF_TRAVEL_REQUEST_ID");
            Log.i(TAG, "onCreate: " + employeeId + "" + requestId);
            getItineraryDetails(employeeId, requestId);
        }
        itinerarySubmitTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBalanceZero()) {
                    Utilities.showDialog(coordinatorLayout, "Sorry you cannot submit the Ticket until you add all the Tickets");
                } else {
                    if (Utilities.isNetworkAvailable(TravelDeskItineraryDetails.this)) {

                        progressDialog = new ProgressDialog(TravelDeskItineraryDetails.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.setCancelable(true);
                        progressDialog.show();

                        final RequestQueue requestQueue = Volley.newRequestQueue(TravelDeskItineraryDetails.this);
                        final String GET_TRAVELDESK_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SubmitFinalTicketFicci/" + employeeId + "/" + requestId;
                        Log.d(TAG, "getTravelDeskData: " + GET_TRAVELDESK_URL);
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_TRAVELDESK_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                Log.d(TAG, "onResponse: " + response);
                                try {
                                    int value = Integer.valueOf(response.replaceAll("^\"|\"$", ""));

                                    if (value > 0) {
                                        Utilities.showDialog(coordinatorLayout, "Travel Tickets Submitted Successfully");
                                        loadActivity();
                                    } else {
                                        Utilities.showDialog(coordinatorLayout, "Error ! during submitting Travel Tickets");
                                    }
                                } catch (Exception e) {
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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

                        int socketTimeout = 3000000;
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        stringRequest.setRetryPolicy(policy);
                        requestQueue.add(stringRequest);
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }
                }
            }
        });

    }

    private void loadActivity() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putString("PostionId", "68");
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public boolean isBalanceZero() {

        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).get("ID_TRAVEL_TICKET_PATH").equals("")) {
                    isBalanceZero = true;
                    break;
                } else {
                    isBalanceZero = false;
                }

            }
        }
        return isBalanceZero;
    }

    private void getItineraryDetails(String employeeId, String requestId) {

        if (Utilities.isNetworkAvailable(TravelDeskItineraryDetails.this)) {

            progressDialog = new ProgressDialog(TravelDeskItineraryDetails.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final RequestQueue requestQueue = Volley.newRequestQueue(TravelDeskItineraryDetails.this);
            final String GET_ITINERARY_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetItineraryByTravelIdTDFicci/" + employeeId + "/" + requestId;
            Log.d(TAG, "getTravelDeskData: " + GET_ITINERARY_URL);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ITINERARY_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        HashMap<String, String> mapdata;
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                mapdata = new HashMap<>();

                                mapdata.put("DEPARTURE", response.getJSONObject(i).getString("DEPARTURE"));
                                mapdata.put("RETURN", response.getJSONObject(i).getString("RETURN"));
                                mapdata.put("ID_DEPARTURE_DATE", response.getJSONObject(i).getString("ID_DEPARTURE_DATE"));
                                mapdata.put("ID_RETURN_DATE", response.getJSONObject(i).getString("ID_RETURN_DATE"));
                                mapdata.put("MODE", response.getJSONObject(i).getString("MODE"));
                                mapdata.put("CLASS", response.getJSONObject(i).getString("CLASS"));
                                mapdata.put("ID_TYPE", response.getJSONObject(i).getString("ID_TYPE"));
                                mapdata.put("ID_TRAVEL_DESK_AMOUNT", response.getJSONObject(i).getString("ID_TRAVEL_DESK_AMOUNT"));
                                mapdata.put("ID_TRAVEL_REQUEST_ID", response.getJSONObject(i).getString("ID_TRAVEL_REQUEST_ID"));
                                mapdata.put("ITINERARY_ID", response.getJSONObject(i).getString("ITINERARY_ID"));
                                mapdata.put("ID_TRAVEL_TICKET_PATH", response.getJSONObject(i).getString("ID_TRAVEL_TICKET_PATH"));

                                arrayList.add(mapdata);
                            }
                            adapter = new TravelDeskItineraryDetailsAdapter(TravelDeskItineraryDetails.this, arrayList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setVisibility(View.VISIBLE);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            recyclerView.setAdapter(null);
                            recyclerView.setVisibility(View.INVISIBLE);
                            noDataFound.setVisibility(View.VISIBLE);
                            travelItineraryLinearLayout.setVisibility(View.INVISIBLE);
                        }


                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonArrayRequest.setRetryPolicy(policy);
            requestQueue.add(jsonArrayRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId", "68");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int posotion);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TravelDeskItineraryDetails.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TravelDeskItineraryDetails.ClickListener clickListener) {

            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                        Log.d("Ram ", "On Long Press");
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

    @Override
    protected void onResume() {
        super.onResume();

    }
}
