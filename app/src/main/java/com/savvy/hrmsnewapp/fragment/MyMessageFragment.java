package com.savvy.hrmsnewapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.MyMessageAdapter;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MyMessageFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String, String>> arlData;
    private String empoyeeId = "";
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    private TextView tvNoDataFound;
    private TextView tvReadMore;
    private CoordinatorLayout coordinatorLayout;
    SharedPreferences shared;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arlData = new ArrayList<>();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_message, container, false);

        initView();
        return rootView;
    }

    private void initView() {
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        tvNoDataFound = (TextView) rootView.findViewById(R.id.tvNoDataFound);
        tvReadMore = (TextView) rootView.findViewById(R.id.tvReadMore);

        SharedPreferences shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));

        clickListener();
    }

    private void clickListener() {
        if (Utilities.isNetworkAvailable(getContext())) {
            tvReadMore.setOnClickListener(this);
            fetchMessageData();
        }
        else{
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void fetchMessageData() {
        String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyNotification";
        JSONObject paramData = new JSONObject();
        try {
            paramData.put("EMPLOYEE_ID", empoyeeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue request = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, paramData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    HashMap<String, String> mapData;
                    JSONArray jsonArray = response.getJSONArray("GetMyNotificationResult");
                    Log.e( "AAt",response.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        mapData = new HashMap<String, String>();
                        JSONObject explrObject = jsonArray.getJSONObject(i);

                        String employeeId = explrObject.getString("employeeId");
                        String message = explrObject.getString("message");
                        String messageDate = explrObject.getString("messageDate");
                        String status = explrObject.getString("status");

                        mapData.put("employeeId", employeeId);
                        mapData.put("message", message);
                        mapData.put("messageDate", messageDate);
                        mapData.put("status", status);

                        arlData.add(mapData);
                    }

                    if (jsonArray.length() <= 0) {
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        tvReadMore.setVisibility(View.GONE);
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                    } else {
                        if (jsonArray.length() > 20) {
                            tvReadMore.setVisibility(View.VISIBLE);
                        }
                        tvNoDataFound.setVisibility(View.GONE);
                        setDataInRecyclerView("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.add(jsonObjectRequest);
    }

    private void setDataInRecyclerView(String readData) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        MyMessageAdapter messageAdapter = new MyMessageAdapter(getActivity(), readData, arlData, new MyMessageAdapter.ItemListener() {
            @Override
            public void onItemeClick(int pos) {

            }
        });

        recyclerView.setAdapter(messageAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvReadMore:
                setDataInRecyclerView("More");
                tvReadMore.setVisibility(View.GONE);
                break;
        }
    }


}
