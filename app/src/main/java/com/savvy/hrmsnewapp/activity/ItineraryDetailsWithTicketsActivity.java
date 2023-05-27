package com.savvy.hrmsnewapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
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
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.ItineraryDetailsWithTicketsAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class ItineraryDetailsWithTicketsActivity extends BaseActivity implements MenuItem.OnMenuItemClickListener {

    ItineraryDetailsWithTicketsAdapter adapter;
    CoordinatorLayout coordinatorLayout;
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> arrayList;
    CustomTextView noDataFound;
    RecyclerView recyclerView;
    LinearLayout travelItineraryLinearLayout;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_details_with_tickets);
        setTitle("Itinerary Details with Tickets");
        setUpToolBar();

        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        arrayList = new ArrayList<>();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        noDataFound = (CustomTextView) findViewById(R.id.txt_travelDeskHistoryItinerary);
        travelItineraryLinearLayout = (LinearLayout) findViewById(R.id.travelHistoryItineraryLinearLayout);
        recyclerView = (RecyclerView) findViewById(R.id.travelDeskHistoryItineraryRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ItineraryDetailsWithTicketsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new TravelDeskItineraryDetails.RecyclerTouchListener(ItineraryDetailsWithTicketsActivity.this, recyclerView, new TravelDeskItineraryDetails.ClickListener() {
            @Override
            public void onClick(View view, final int position) {


                Button download = view.findViewById(R.id.travelDeskHistoryItineraryDownload);
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }

            @Override
            public void onLongClick(View view, int posotion) {
            }
        }));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String requestId = bundle.getString("TRF_TRAVEL_REQUEST_ID");
            getItineraryDetails(employeeId, requestId);
        }
    }

    private void getItineraryDetails(String employeeId, String requestId) {

        if (Utilities.isNetworkAvailable(ItineraryDetailsWithTicketsActivity.this)) {

            progressDialog = new ProgressDialog(ItineraryDetailsWithTicketsActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final RequestQueue requestQueue = Volley.newRequestQueue(ItineraryDetailsWithTicketsActivity.this);
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

                                arrayList.add(mapdata);
                            }
                            adapter = new ItineraryDetailsWithTicketsAdapter(ItineraryDetailsWithTicketsActivity.this, arrayList);
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

}
