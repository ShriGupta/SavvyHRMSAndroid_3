package com.savvy.hrmsnewapp.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.MainActivity;
import com.savvy.hrmsnewapp.adapter.MyActionCenterAdapter;

import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyActionCenterFragment extends Fragment implements View.OnClickListener, MyActionCenterAdapter.ItemListener {

    private View rootView;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String, String>> arlData;
    private String empoyeeId = "";
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    private TextView tvNoDataFound;
    private TextView tvReadMore;
    private CoordinatorLayout coordinatorLayout;
    SharedPreferences shared;
    ProgressDialog progressDoalog;

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
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void fetchMessageData() {

        progressDoalog = new ProgressDialog(requireActivity());
        progressDoalog.setTitle("Sending Request");
        progressDoalog.show();

        arlData.clear();
        String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyActionCenter";
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
                    progressDoalog.dismiss();
                    Log.e("MyActionCenterFragment", response.toString());
                    HashMap<String, String> mapData;
                    JSONArray jsonArray = response.getJSONArray("GetMyActionCenterResult");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        mapData = new HashMap<>();
                        JSONObject explrObject = jsonArray.getJSONObject(i);

                        String actionMessage = explrObject.getString("actionMessage");
                        String employeeId = explrObject.getString("employeeId");
                        String message = explrObject.getString("message");
                        String messageDate = explrObject.getString("messageDate");
                        String actionType = explrObject.getString("actionType");
                        String moduleName = explrObject.getString("moduleName");
                        String status = explrObject.getString("status");
                        String apiName = explrObject.getString("apiName");

                        mapData.put("actionMessage", actionMessage);
                        mapData.put("employeeId", employeeId);
                        mapData.put("message", message);
                        mapData.put("messageDate", messageDate);
                        mapData.put("actionType", actionType);
                        mapData.put("moduleName", moduleName);
                        mapData.put("status", status);
                        mapData.put("apiName", apiName);

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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.add(jsonObjectRequest);
    }

    private void setDataInRecyclerView(String readData) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        MyActionCenterAdapter messageAdapter = new MyActionCenterAdapter(getActivity(), readData, arlData,this);

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


    @Override
    public void onItemeClick(String employeeId, String actionString, String actionStatus) {
        actionRequest(employeeId,actionString,actionStatus);
    }

    private void actionRequest(String employeeId,String actionString,String actionStatus) {
        String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/MYActionOnNotification";
        JSONObject paramData = new JSONObject();
        try {
            paramData.put("EMPLOYEE_ID", employeeId);
            paramData.put("PARAMETER", actionString);
            paramData.put("STATUS", actionStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue request = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, paramData, response -> {
            Log.e("MyActionCenterFragment", "actionRequest: "+response );
            try {
                String result=response.getString("MYActionOnNotificationResult");
                Toast.makeText(requireActivity(), result, Toast.LENGTH_SHORT).show();
                fetchMessageData();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {

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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.add(jsonObjectRequest);
    }
}
