package com.savvy.hrmsnewapp.saleForce;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.savvy.hrmsnewapp.SaleForceAdapter.AllCustomerDetailAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.BaseFragment;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*import io.fabric.sdk.android.services.concurrency.AsyncTask;*/

import static android.content.Context.MODE_PRIVATE;
import static android.service.controls.ControlsProviderService.TAG;

public class AllCustomerDetail extends BaseFragment implements ItemClickListener {

    RecyclerView recyclerView;
    CoordinatorLayout coordinatorLayout;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String empoyeeId = "";
    SharedPreferences shared;
    AllCustomerDetailAdapter mAdapter;
    Spinner yearSpinner;
    List<HashMap<String, String>> arrayList = new ArrayList<>();
    TextView tvNodata;
    String selectedYear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_customer_detail, container, false);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        recyclerView = view.findViewById(R.id.allCustomerDetail);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        yearSpinner = view.findViewById(R.id.sp_year_spinner);
        mAdapter = new AllCustomerDetailAdapter(requireActivity(), this);
        recyclerView.setAdapter(mAdapter);
        tvNodata = view.findViewById(R.id.tv_no_data);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareSpinnerYearData();
    }

    private void prepareSpinnerYearData() {
        ArrayList<String> yearArrayList = new ArrayList<>();
        int[] yearArray = new int[10];

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.getDefault());
        int formattedDate = Integer.parseInt(df.format(c));

        for (int i = 0; i < 10; i++) {
            yearArray[i] = formattedDate;
            formattedDate--;
        }

        for (int s : yearArray) {
            yearArrayList.add(String.valueOf(s));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, yearArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = String.valueOf(yearArray[i]);
                getEmployeeSlipData(empoyeeId, selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getEmployeeSlipData(String empoyeeId, String selectedYear) {

        if (Utilities.isNetworkAvailable(requireActivity())) {
            showProgressDialog();
            arrayList.clear();
            mAdapter.clearItems();

            JSONObject param = new JSONObject();
            try {
                param.put("employeeId", empoyeeId);
                param.put("year", selectedYear);
                param.put("securityToken", shared.getString("Token", ""));
                Log.e(TAG, "getEmployeeSlipData: " + param.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/PaySlipListPost";
            Log.e(TAG, "getEmployeeSlipData: " + url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: " + response);
                            dismissProgressDialog();
                            try {
                                if (response.getJSONArray("PaySlipListPostResult").length() > 0) {
                                    HashMap<String, String> mapData;
                                    for (int i = 0; i < response.getJSONArray("PaySlipListPostResult").length(); i++) {
                                        mapData = new HashMap<>();
                                        mapData.put("PayrollMonthName", response.getJSONArray("PaySlipListPostResult").getJSONObject(i).getString("PayrollMonthName"));
                                        mapData.put("PayrollMonth", response.getJSONArray("PaySlipListPostResult").getJSONObject(i).getString("PayrollMonth"));
                                        arrayList.add(mapData);
                                    }
                                    mAdapter.addItems(arrayList);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    tvNodata.setVisibility(View.GONE);

                                } else {
                                    tvNodata.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    showToast(error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", shared.getString("Token", ""));
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    @Override
    public void onClickItem(int position, String data) {
        downloadSlip(data);
    }

    private void downloadSlip(String month) {
        if (Utilities.isNetworkAvailable(requireActivity())) {
            showProgressDialog();
            /*JSONObject param = new JSONObject();
            try {
                param.put("employeeid", empoyeeId);
                param.put("year", selectedYear);
                param.put("month", month);
                param.put("securityToken", shared.getString("EMPLOYEE_ID_FINAL", ""));
                Log.e(TAG, "downloadSlip: " + param.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            String url = Constants.IP_ADDRESS + "/MobileFileDownload.ashx?employeeid=" + empoyeeId +
                    "&" + "year=" + selectedYear + "&" + "month=" + month + "&" + "securityToken=" + shared.getString("Token", "");
            Log.e(TAG, "downloadSlip: " + url);

            openFilePDF(url);


          /*  BackGroundTask backGroundTask = new BackGroundTask();
            backGroundTask.execute(url);*/
           /* JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dismissProgressDialog();
                            Log.e(TAG, "onResponse: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onResponse: " + error);
                    dismissProgressDialog();
                    showToast(error.getMessage());
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);*/

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    class BackGroundTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                File SDCardRoot = Environment.getExternalStorageDirectory();
                File file = new File(SDCardRoot, "somefile.ext");

                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();
                int totalSize = urlConnection.getContentLength();

                byte[] buffer = new byte[1024];
                int bufferLength;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgressDialog();
        }
    }

    private void openFilePDF(String url){
        try{
            dismissProgressDialog();
            Toast.makeText(requireActivity(), "Opening PDF... ", Toast.LENGTH_SHORT).show();
            Intent inte = new Intent(Intent.ACTION_VIEW);
            inte.setDataAndType(Uri.parse(url), "application/pdf");
            startActivity(inte);
        }catch(ActivityNotFoundException e){
            Log.e("Viewer", e.getMessage());
        }
    }
}
