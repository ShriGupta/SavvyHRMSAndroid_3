package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.savvy.hrmsnewapp.activity.TravelDeskItineraryDetails;
import com.savvy.hrmsnewapp.adapter.TravelDeskFicciAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;
import static com.savvy.hrmsnewapp.fragment.DashBoardMainFragment.MY_PREFS_NAME;

public class TravelDeskFicciFragment extends BaseFragment {
    TravelDeskFicciAdapter approvalAdapter;
    CoordinatorLayout coordinatorLayout;
    RecyclerView recyclerView;
    CustomTextView noDataFound;
    ArrayList<HashMap<String, String>> arrayList;
    ProgressDialog progressDialog;
    Button addTicket;
    SharedPreferences shared;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        arrayList = new ArrayList<>();
        getTravelDeskData("0");
    }

    private void getTravelDeskData(String value) {

        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            final String GET_TRAVELDESK_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelDeskRequestFicci/" + value;
            Log.d(TAG, "getTravelDeskData: " + GET_TRAVELDESK_URL);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_TRAVELDESK_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        HashMap<String, String> mapdata;
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                mapdata = new HashMap<>();

                                mapdata.put("TOKEN_NO", response.getJSONObject(i).getString("TOKEN_NO"));
                                mapdata.put("REQUEST_BY_EMPLOYEE_CODE", response.getJSONObject(i).getString("REQUEST_BY_EMPLOYEE_CODE"));
                                mapdata.put("REQUEST_BY_EMPLOYEE_NAME", response.getJSONObject(i).getString("REQUEST_BY_EMPLOYEE_NAME"));
                                mapdata.put("TRF_TRAVEL_TYPE", response.getJSONObject(i).getString("TRF_TRAVEL_TYPE"));
                                mapdata.put("TRF_ITINERARY_TYPE", response.getJSONObject(i).getString("TRF_ITINERARY_TYPE"));
                                mapdata.put("TRF_START_DATE_1", response.getJSONObject(i).getString("TRF_START_DATE_1"));
                                mapdata.put("TRF_END_DATE_1", response.getJSONObject(i).getString("TRF_END_DATE_1"));
                                mapdata.put("TPM_PROJECT_NAME", response.getJSONObject(i).getString("TPM_PROJECT_NAME"));
                                mapdata.put("TRF_BUDGETED_AMOUNT", response.getJSONObject(i).getString("TRF_BUDGETED_AMOUNT"));
                                mapdata.put("TRF_TRAVEL_REQUEST_ID", response.getJSONObject(i).getString("TRF_TRAVEL_REQUEST_ID"));


                                arrayList.add(mapdata);
                            }
                            approvalAdapter = new TravelDeskFicciAdapter(getActivity(), arrayList);
                            recyclerView.setAdapter(approvalAdapter);
                            recyclerView.setVisibility(View.VISIBLE);

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            recyclerView.setAdapter(null);
                            recyclerView.setVisibility(View.INVISIBLE);
                            noDataFound.setVisibility(View.VISIBLE);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_desk_ficci, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        recyclerView = getActivity().findViewById(R.id.travelDeskRecyclerView);
        noDataFound = getActivity().findViewById(R.id.txt_travelDesk);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new TravelDeskFicciFragment.RecyclerTouchListener(getActivity(), recyclerView, new TravelDeskFicciFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final HashMap<String, String> mapdata = arrayList.get(position);
                String requestId = mapdata.get("TRF_TRAVEL_REQUEST_ID");
                Bundle bundle = new Bundle();
                bundle.putString("TRF_TRAVEL_REQUEST_ID", requestId);
                Intent intent = new Intent(getActivity(), TravelDeskItineraryDetails.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int posotion) {
                Log.d(ContentValues.TAG, "onLongClick: ");
            }
        }));
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int posotion);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TravelDeskFicciFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TravelDeskFicciFragment.ClickListener clickListener) {

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
}
