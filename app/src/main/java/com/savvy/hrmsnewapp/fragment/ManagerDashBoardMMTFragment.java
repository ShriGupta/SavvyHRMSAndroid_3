package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.ManagerDashBoardSearchAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.ManagerDashboardMMTModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class ManagerDashBoardMMTFragment extends BaseFragment implements View.OnClickListener {
    ManagerDashBoardSearchAdapter managerDashBoardSearchAdapter;
    ManagerDashBoardMMTFragment.GetSummaryGridRequest getSummaryGridRequest;
    ManagerDashBoardMMTFragment.GetChartDataRequest getChartDataRequest;
    CoordinatorLayout coordinatorLayout;
    Button md_fromDate, md_toDate, mdshowButton, mdSpinnerButton, mdListButton;
    AutoCompleteTextView autoCompleteTextView;
    CustomTextView txtWorkingHours, txt_avgIn, txt_leaveConsumed, txt_avgOut, compareDateTextView, md_txtDataNotFound, mdBar_DataNotFound;
    LinearLayout compareDateLayout;
    RecyclerView mdRecyclerView;
    CalanderHRMS calanderHRMS;
    Handler handler;
    Runnable rRunnable;
    String FROM_DATE = "", TO_DATE = "";
    ProgressDialog progressDialog;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    ArrayList<HashMap<String, String>> teamArrayList;
    ArrayList<HashMap<String, String>> summarGridArrayList = new ArrayList<>();
    List<ManagerDashboardMMTModel> loadItemList = new ArrayList<>();
    List<ManagerDashboardMMTModel> totalItemInList = new ArrayList<>();
    ArrayAdapter<String> autoCompleteAdapter;
    ArrayList<String> teamArray;
    BarChart chart;
    List<String> labelArrayList;
    List<String> valueArrayList;
    public static String staffId = "0";
    String empPositionId = "0";
    String firstDate = "", lastDate = "";
    LinearLayoutManager layoutManager;
    boolean loading = true;
    int visibleItemCount, totalItemCount, pastVisiblesItems;
    boolean isLoading = false;
    boolean isDataDeleted = false;
    int[] userIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        firstDate = df.format(calendar.getTime());
        lastDate = getLastDayOfTheMonth(firstDate);
        getMyTeamData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manager_dashboard_mmt, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        md_fromDate = getActivity().findViewById(R.id.md_fromDate);
        md_toDate = getActivity().findViewById(R.id.md_toDate);
        autoCompleteTextView = getActivity().findViewById(R.id.autoCompleteTextView);

        mdshowButton = getActivity().findViewById(R.id.md_showButton);
        mdSpinnerButton = getActivity().findViewById(R.id.md_Spinner);

        txtWorkingHours = getActivity().findViewById(R.id.md_workingHours);
        txt_avgIn = getActivity().findViewById(R.id.md_avgIn);
        txt_leaveConsumed = getActivity().findViewById(R.id.md_leaveConsumed);
        txt_avgOut = getActivity().findViewById(R.id.md_AvgOut);

        md_txtDataNotFound = getActivity().findViewById(R.id.md_txtDataNotFound);
        mdBar_DataNotFound = getActivity().findViewById(R.id.mdBar_txtDataNotFound);
        compareDateTextView = getActivity().findViewById(R.id.mdCompareDateTextView);
        compareDateLayout = getActivity().findViewById(R.id.mdCompareDateLayout);

        chart = getActivity().findViewById(R.id.barchart);

        mdRecyclerView = getActivity().findViewById(R.id.md_recyclerView);

        layoutManager = new LinearLayoutManager(getActivity());
        mdRecyclerView.setLayoutManager(layoutManager);
        mdRecyclerView.setItemAnimator(new DefaultItemAnimator());

        md_fromDate.setOnClickListener(this);
        md_toDate.setOnClickListener(this);
        mdshowButton.setOnClickListener(this);

        md_fromDate.setText(firstDate);
        md_toDate.setText(lastDate);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String empName = autoCompleteAdapter.getItem(position);
                teamArray.indexOf(empName);
                empPositionId = teamArrayList.get(teamArray.indexOf(empName)).get("KEY");

            }
        });

        mdSpinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), mdSpinnerButton);
                popup.getMenuInflater().inflate(R.menu.manager_dashboard_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("By Employee")) {
                            staffId = "0";
                            mdSpinnerButton.setText(item.getTitle().toString());
                        } else {
                            staffId = "1";
                            mdSpinnerButton.setText(item.getTitle().toString());
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


        mdRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (loading) {
                    if (dy > 0) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isLoading = true;
                            if (isDataDeleted) {
                                loadDatainList();
                            }

                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.md_fromDate:
                md_fromDate.setText("");
                calanderHRMS.datePicker(md_fromDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = md_fromDate.getText().toString().trim();
                            TO_DATE = md_toDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE);
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
            case R.id.md_toDate:
                md_toDate.setText("");
                calanderHRMS.datePicker(md_toDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = md_fromDate.getText().toString().trim();
                            TO_DATE = md_toDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE);
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
            case R.id.md_showButton:
                isLoading = false;
                try {
                    if (summarGridArrayList.size() > 0) {
                        summarGridArrayList.clear();
                    }

                    if (!teamArray.contains(autoCompleteTextView.getText().toString())) {
                        empPositionId = "0";
                    }
                    android.os.AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeDashboardData();
                        }
                    });
                    getSummaryData(employeeId, md_fromDate.getText().toString().replace("/", "-"), md_toDate.getText().toString().replace("/", "-"), staffId, empPositionId, "0");
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
        }
    }

    private void getMyTeamData() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String LEAVE_TYPE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyTeamMMT/" + employeeId;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, LEAVE_TYPE_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        HashMap<String, String> mapdata;
                        teamArrayList = new ArrayList<>();
                        progressDialog.dismiss();
                        if (response.length() > 0) {
                            teamArray = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                mapdata = new HashMap<>();
                                mapdata.put("KEY", response.getJSONObject(i).getString("EMPLOYEE_ID"));
                                mapdata.put("VALUE", response.getJSONObject(i).getString("EMPLOYEE_NAME"));
                                teamArray.add(response.getJSONObject(i).getString("EMPLOYEE_NAME"));
                                teamArrayList.add(mapdata);
                            }

                            autoCompleteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, teamArray) {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);

                                    ((TextView) v).setTextSize(13);
                                    ((TextView) v).setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                                    return v;
                                }
                            };
                            autoCompleteTextView.setAdapter(autoCompleteAdapter);
                            getSummaryData(employeeId, md_fromDate.getText().toString().replace("/", "-"), md_toDate.getText().toString().replace("/", "-"), staffId, empPositionId, "0");
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getSummaryData(final String employeeId, String fromdate, String todate, final String staffId, final String empPositionId, String ischart) {
        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            final String SUMMARY_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GeSummaryDataMMT/" + employeeId + "/" + fromdate + "/" + todate + "/" + staffId + "/" + empPositionId + "/" + ischart;
            Log.d(TAG, "getSummaryData: " + SUMMARY_URL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, SUMMARY_URL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        if (response.length() > 0) {

                            txtWorkingHours.setText(response.getString("TOTAL_WORK_HOUR"));
                            txt_avgIn.setText(response.getString("IN_TIME"));
                            txt_leaveConsumed.setText(response.getString("AVG_LEAVE"));
                            txt_avgOut.setText(response.getString("OUT_TIME"));
                        }

                        getChartData(employeeId, md_fromDate.getText().toString().replace("/", "-"), md_toDate.getText().toString().replace("/", "-"), staffId, empPositionId);

                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                        progressDialog.dismiss();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    progressDialog.dismiss();
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    public static String getLastDayOfTheMonth(String date) {
        String lastDayOfTheMonth = "";

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            java.util.Date dt = formatter.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            java.util.Date lastDay = calendar.getTime();
            lastDayOfTheMonth = formatter.format(lastDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lastDayOfTheMonth;
    }

    public void getCompareTodayDate(String From_date, String To_date) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String From_Date = From_date;
                String To_Date = To_date;

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("From_Date", From_Date);
                params_final.put("To_Date", To_Date);
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        compareDateTextView.setText(R.string.compareDate);
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


    private void getChartData(String employeeId, String fromdate, String todate, String staffId, String emppositionid) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                getChartDataRequest = new ManagerDashBoardMMTFragment.GetChartDataRequest();
                getChartDataRequest.employeeId = employeeId;
                getChartDataRequest.fromdate = fromdate;
                getChartDataRequest.todate = todate;
                getChartDataRequest.staffId = staffId;
                getChartDataRequest.empPositionId = emppositionid;
                getChartDataRequest.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetChartDataRequest extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String employeeId, fromdate, todate, staffId, empPositionId;

        @Override
        protected void onPreExecute() {
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                final String CHART_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetChartDataMMT/" + employeeId + "/" + fromdate + "/" + todate + "/" + staffId + "/" + empPositionId;
                Log.d(TAG, "getChartData: " + CHART_URL);
                JSONParser jParser = new JSONParser(getActivity());
                String json = jParser.makeHttpRequest(CHART_URL, "GET");

                if (json != null) {
                    Log.d("JSON result", json);
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String response) {
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        valueArrayList = new ArrayList<>();
                        labelArrayList = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(response);
                        jsonArray.length();
                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                labelArrayList.add(jsonArray.getJSONObject(i).getString("LABEL"));
                                String value = String.valueOf(jsonArray.getJSONObject(i).getString("VALUE"));

                                valueArrayList.add(value);
                            }

                            /*BarDataSet bardataset = new BarDataSet(valueArrayList, "WORKING HOURS SPREAD");
                            bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                            barchart.animateY(1000);
                            BarData data = new BarData(labelArrayList, bardataset);
                            barchart.setData(data);
                            barchart.setVisibility(View.VISIBLE);
                            mdBar_DataNotFound.setVisibility(View.GONE);*/

                            create_graph(labelArrayList, valueArrayList);
                            chart.setVisibility(View.VISIBLE);
                            mdBar_DataNotFound.setVisibility(View.GONE);

                            getSummaryGridData(employeeId, md_fromDate.getText().toString().replace("/", "-"), md_toDate.getText().toString().replace("/", "-"), staffId, empPositionId, "2");

                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            chart.setVisibility(View.GONE);
                            mdBar_DataNotFound.setVisibility(View.VISIBLE);
                            getSummaryGridData(employeeId, md_fromDate.getText().toString().replace("/", "-"), md_toDate.getText().toString().replace("/", "-"), staffId, empPositionId, "2");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    private void create_graph(List<String> graph_label, List<String> userScore) {

        try {
            chart.setDrawBarShadow(false);
            chart.setDrawValueAboveBar(true);
            chart.getDescription().setEnabled(false);
            chart.setPinchZoom(false);
            chart.setDrawGridBackground(false);

            YAxis yAxis = chart.getAxisLeft();
            yAxis.setValueFormatter(new DefaultAxisValueFormatter(1));
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            yAxis.setGranularity(1f);
            yAxis.setGranularityEnabled(true);
            chart.getAxisRight().setEnabled(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setCenterAxisLabels(true);
            xAxis.setDrawGridLines(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(graph_label));
            xAxis.setCenterAxisLabels(false);

            List<BarEntry> yVals1 = new ArrayList<>();

            for (int i = 0; i < userScore.size(); i++) {
                yVals1.add(new BarEntry(i, Float.parseFloat(userScore.get(i))));
            }
            BarDataSet set1;

            if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
                set1.setValues(yVals1);
                set1.setValueTextSize(13);
                set1.setColors(ContextCompat.getColor(chart.getContext(), R.color.color_Green),
                        ContextCompat.getColor(chart.getContext(), R.color.color_yellow),
                        ContextCompat.getColor(chart.getContext(), R.color.color_red));
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
            } else {

                set1 = new BarDataSet(yVals1, "Working Hours Spread");
                set1.setValueTextSize(13);
                set1.setColors(ContextCompat.getColor(chart.getContext(), R.color.color_Green),
                        ContextCompat.getColor(chart.getContext(), R.color.color_yellow),
                        ContextCompat.getColor(chart.getContext(), R.color.color_red));
                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);
                BarData data = new BarData(dataSets);
                chart.setData(data);


            }

            chart.setFitBars(true);
            Legend l = chart.getLegend();
            l.setFormSize(12f); // set the size of the legend forms/shapes
            l.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used

            //l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
            l.setTextSize(10f);
            l.setTextColor(Color.BLACK);
            /*l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
            l.setYEntrySpace(5f);*/ // set the space between the legend entries on the y-axis

            chart.invalidate();

            chart.animateY(2000);

        } catch (Exception ignored) {
        }
    }


    private void getSummaryGridData(final String employeeId, String fromdate, String todate, final String staffId, String emppositionid, String isChart) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                getSummaryGridRequest = new ManagerDashBoardMMTFragment.GetSummaryGridRequest();
                getSummaryGridRequest.employeeId = employeeId;
                getSummaryGridRequest.fromdate = fromdate;
                getSummaryGridRequest.todate = todate;
                getSummaryGridRequest.staffId = staffId;
                getSummaryGridRequest.empPositionId = emppositionid;
                getSummaryGridRequest.isChart = isChart;
                getSummaryGridRequest.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class GetSummaryGridRequest extends AsyncTask<String, String, String> {

        String employeeId, fromdate, todate, staffId, empPositionId, isChart;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GeSummaryGridDataMMT/" + employeeId + "/" + fromdate + "/" + todate + "/" + staffId + "/" + empPositionId + "/" + isChart;
                JSONParser jParser = new JSONParser(getActivity());
                String json = jParser.makeHttpRequest(GETREQUESTSTATUS_URL, "GET");

                if (json != null) {
                    Log.d("JSON result", json);
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String response) {

            try {
                progressDialog.dismiss();
                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final ManagerDashboardMMTModel mmtModel = new ManagerDashboardMMTModel();
                        mmtModel.setEMPLOYEE_CODE(jsonArray.getJSONObject(i).getString("EMPLOYEE_CODE"));
                        mmtModel.setEMPLOYEE_NAME(jsonArray.getJSONObject(i).getString("EMPLOYEE_NAME"));
                        mmtModel.setAVG_WORKTIME(jsonArray.getJSONObject(i).getString("AVG_WORKTIME"));
                        mmtModel.setAVG_IN_TIME(jsonArray.getJSONObject(i).getString("AVG_IN_TIME"));
                        mmtModel.setAVG_OUT_TIME(jsonArray.getJSONObject(i).getString("AVG_OUT_TIME"));
                        mmtModel.setLEAVE(jsonArray.getJSONObject(i).getString("LEAVE"));
                        mmtModel.setWFH(jsonArray.getJSONObject(i).getString("WFH"));
                        mmtModel.setOD(jsonArray.getJSONObject(i).getString("OD"));
                        mmtModel.setAVG_WORKED1(jsonArray.getJSONObject(i).getString("AVG_WORKED1"));
                        android.os.AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().insertManagerMMTData(mmtModel);
                            }
                        });
                    }
                    loadDatainList();

                } else {
                    progressDialog.dismiss();
                    mdRecyclerView.setVisibility(View.GONE);
                    md_txtDataNotFound.setVisibility(View.VISIBLE);
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }

        }
    }

    private void loadDatainList() {
        isDataDeleted = false;
        LoadDataAsynTask loadDataAsynTask = new LoadDataAsynTask();
        loadDataAsynTask.execute();
    }

    public class LoadDataAsynTask extends AsyncTask<String, Void, List<ManagerDashboardMMTModel>> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected List<ManagerDashboardMMTModel> doInBackground(String... value) {
            loadItemList = DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().getMDshboardMMTData();
            return loadItemList;
        }

        @Override
        protected void onPostExecute(List<ManagerDashboardMMTModel> loadItemList) {
            super.onPostExecute(loadItemList);
            try {
                pDialog.dismiss();
                HashMap<String, String> mapdata;
                if (loadItemList.size() > 0) {
                    for (int i = 0; i < loadItemList.size(); i++) {
                        mapdata = new HashMap<>();
                        mapdata.put("EMPLOYEE_CODE", loadItemList.get(i).getEMPLOYEE_CODE());
                        mapdata.put("EMPLOYEE_NAME", loadItemList.get(i).getEMPLOYEE_NAME());
                        mapdata.put("AVG_WORKTIME", loadItemList.get(i).getAVG_WORKTIME());
                        mapdata.put("AVG_IN_TIME", loadItemList.get(i).getAVG_IN_TIME());
                        mapdata.put("AVG_OUT_TIME", loadItemList.get(i).getAVG_OUT_TIME());
                        mapdata.put("LEAVE", loadItemList.get(i).getLEAVE());
                        mapdata.put("WFH", loadItemList.get(i).getWFH());
                        mapdata.put("OD", loadItemList.get(i).getOD());
                        mapdata.put("AVG_WORKED1", loadItemList.get(i).getAVG_WORKED1());
                        summarGridArrayList.add(mapdata);
                    }
                    if (isLoading) {
                        managerDashBoardSearchAdapter.notifyDataSetChanged();
                        isLoading = false;
                    } else {
                        managerDashBoardSearchAdapter = new ManagerDashBoardSearchAdapter(getActivity(), summarGridArrayList);
                        mdRecyclerView.setAdapter(managerDashBoardSearchAdapter);
                        mdRecyclerView.setVisibility(View.VISIBLE);
                        md_txtDataNotFound.setVisibility(View.GONE);

                    }
                    userIds = new int[loadItemList.size()];
                    for (int i = 0; i < loadItemList.size(); i++) {
                        userIds[i] = loadItemList.get(i).getId();
                    }

                    RoomDataDeleteAsync roomDataDeleteAsync = new RoomDataDeleteAsync();
                    roomDataDeleteAsync.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
                pDialog.dismiss();
            }

        }
    }

    class RoomDataDeleteAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().deleteDashBoardData(userIds);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadItemList.clear();
            isDataDeleted = true;

        }
    }
}
