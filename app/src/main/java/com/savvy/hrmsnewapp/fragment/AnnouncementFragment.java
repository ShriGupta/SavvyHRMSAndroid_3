package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.savvy.hrmsnewapp.adapter.AnnouncementAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class AnnouncementFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences shared;
    private ArrayList<HashMap<String, String>> arlData = new ArrayList<>();
    private String empoyeeId = "";
    private String token = "";
    private String Group_Id = "";
    private CoordinatorLayout coordinatorLayout;
    private CustomTextView txtDataNotFound;
    private AnnouncementAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_announcement, container, false);

        initView();
        return rootView;
    }

    private void initView() {
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        Group_Id = (shared.getString("GroupId", ""));

        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        txtDataNotFound = rootView.findViewById(R.id.txtDataNotFound);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        getAnnouncementData();
    }

    private void getAnnouncementData() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                final ProgressDialog pDialog;
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetAnnouncementForEmployee";
                final JSONObject pm = new JSONObject();
                pm.put("GROUP_ID", Group_Id);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pDialog.dismiss();
                            HashMap<String, String> mapData;
                            JSONArray jsonArray = response.getJSONArray("GetAnnouncementForEmployeeResult");
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    mapData = new HashMap<>();
                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String TYPE=explrObject.getString("AS_TYPE");

                                    if(TYPE.equals("2")) {
                                        mapData.put("AS_MESSAGE", explrObject.getString("AS_MESSAGE"));
                                        mapData.put("AS_TYPE", explrObject.getString("AS_TYPE"));
                                        arlData.add(mapData);
                                    }
                                }
                                recyclerView.setVisibility(View.VISIBLE);
                                mAdapter = new AnnouncementAdapter(getActivity(), arlData);
                                recyclerView.setAdapter(mAdapter);
                                txtDataNotFound.setVisibility(View.GONE);
                            } else {
                                recyclerView.setAdapter(null);
                                Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                recyclerView.setVisibility(View.GONE);
                                txtDataNotFound.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
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
}
