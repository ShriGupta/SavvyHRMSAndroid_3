package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.savvy.hrmsnewapp.activity.CalendarAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Handler;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

//import com.stacktips.view.utils.CalendarUtils

public class CalendarWeb extends BaseFragment{

    public Calendar month;
    public CalendarAdapter adapter;
    public Handler handler;
    public ArrayList<String> items;
    ArrayList<HashMap<String,String>> arlData;
    GridView gridview;
    String empoyeeId = "", from_Date = "",to_Date = "";
    CalendarWeb.GetAttendanceCalendarAsync getAttendanceCalendarAsync;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared,shareData;
    LinearLayout linear_days_name1;
    LinearLayout linearLayout_gridtableLayout1;

    CustomTextView sun,mon,tue,wed,thu,fri,sat;
    String[] dateSplit;
    String[] dateSplit1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arlData = new ArrayList<HashMap<String, String>>();

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));

    }

    private void getAttendanceCalendar(String empoyeeId,String month12,String year) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<HashMap<String, String>>();
                getAttendanceCalendarAsync = new CalendarWeb.GetAttendanceCalendarAsync();
                getAttendanceCalendarAsync.empoyeeId = empoyeeId;
                getAttendanceCalendarAsync.month12 = month12;
                getAttendanceCalendarAsync.year = year;
                getAttendanceCalendarAsync.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.calendar, container, false);

        month = Calendar.getInstance();
//        onNewIntent(getIntent());

        Calendar c = Calendar.getInstance();
        final int year1 = c.get(Calendar.YEAR);
        int month1 = c.get(Calendar.MONTH);

        month1 =month1+1;

        String year_1 = String.valueOf(year1);
        String month_1 = String.valueOf(month1);

        Log.e("IDOn","Emp = "+empoyeeId+" month = "+month_1+" year = "+year_1);

        getAttendanceCalendar(empoyeeId,month_1,year_1);
        items = new ArrayList<String>();
//        adapter = new CalendarAdapter(getActivity(), month, arlData);

        sun = view.findViewById(R.id.sun);
        mon = view.findViewById(R.id.mon);
        tue = view.findViewById(R.id.tue);
        wed = view.findViewById(R.id.wed);
        thu = view.findViewById(R.id.thu);
        fri = view.findViewById(R.id.fri);
        sat = view.findViewById(R.id.sat);

        linear_days_name1 = view.findViewById(R.id.linear_days_name);
        linearLayout_gridtableLayout1 = view.findViewById(R.id.linearLayout_gridtableLayout);
        gridview = view.findViewById(R.id.gridview);
        //gridview.setAdapter(adapter);

        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int mWidthScreen = display.getWidth();
        int mHeightScreen = display.getHeight();

        int resizeWidth = mWidthScreen/7;

        sun.getLayoutParams().width= resizeWidth-3;
        mon.getLayoutParams().width= resizeWidth-3;
        tue.getLayoutParams().width= resizeWidth-3;
        wed.getLayoutParams().width= resizeWidth-3;
        thu.getLayoutParams().width= resizeWidth-3;
        fri.getLayoutParams().width= resizeWidth-3;
        sat.getLayoutParams().width= resizeWidth-3;

        gridview.getLayoutParams().width = mWidthScreen-6;
        gridview.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        linearLayout_gridtableLayout1.getLayoutParams().width = mWidthScreen-5;
        linearLayout_gridtableLayout1.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        linearLayout_gridtableLayout1.getLayoutParams().height = mHeightScreen;
//
//        gridview.getLayoutParams().width = mWidthScreen-7;
        gridview.setColumnWidth((int) (resizeWidth-3.2));

        linear_days_name1.getLayoutParams().width = mWidthScreen-5;

        TextView title  = view.findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        TextView previous  = view.findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
                    int mo = month.get(Calendar.MONTH);
                    mo=mo+1;
                    String mon = String.valueOf(mo);

                    int year12 = month.get(Calendar.YEAR);
                    getAttendanceCalendar(empoyeeId,mon,String.valueOf(year12));
                    Log.e("MonthP Value",mon+year12);
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
                    int mo = month.get(Calendar.MONTH);
                    mo=mo+1;
                    String mon = String.valueOf(mo);
                    int year12 = month.get(Calendar.YEAR);
                    getAttendanceCalendar(empoyeeId,mon,String.valueOf(year12));
                }
                refreshCalendar();
            }
        });

        TextView next  = view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
                    int mo = month.get((Calendar.YEAR)+1);
                    mo=mo+1;
                    String mon = String.valueOf(mo);
                    int year12 = month.get(Calendar.YEAR);
                    getAttendanceCalendar(empoyeeId,mon,String.valueOf(year12));
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
                    int mo = month.get((Calendar.YEAR)+1);
                    mo=mo+1;
                    String mon = String.valueOf(mo);
                    int year12 = month.get(Calendar.YEAR);
                    getAttendanceCalendar(empoyeeId,mon,String.valueOf(year12));
                }
                refreshCalendar();

            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = v.findViewById(R.id.date);
                if(date instanceof TextView && !date.getText().equals("")) {

                    Intent intent = new Intent();
                    String day = date.getText().toString();
                    if(day.length()==1) {
                        day = "0"+day;
                    }
                    // return chosen date as string format
                    intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day);
                    getActivity().setResult(RESULT_OK, intent);
                    //finish();
                }

            }
        });
        return view;
    }

    private class GetAttendanceCalendarAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empoyeeId ,month12 ,year;

        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()

            try {

                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                Log.e("IDs","Emp = "+empoyeeId+" month = "+month12+" year = "+year);

                params_final.put("EMPLOYEE_ID", empoyeeId);
                params_final.put("MONTH", month12);
                params_final.put("YEAR", year);

                pm.put("objEmployeeAttendanceCalendarInfo", params_final);
                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String ATTENDANCE_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetEmployeeRegister";

                System.out.println("ATTENDANCE_URL123===="+ATTENDANCE_URL);

                System.out.println("URL=1==" + ATTENDANCE_URL);
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ATTENDANCE_URL, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array "+ " Length = "+len+" Value = " +response.toString() );
                                Log.e("Value"," Length = "+len+" Value = " +response.toString());


                                try{
                                        HashMap<String, String> mapData;
//                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONArray jsonArray = response.getJSONArray("GetEmployeeRegisterResult");

                                        for(int i=0;i<jsonArray.length();i++){
                                            mapData = new HashMap<String, String>();
                                            JSONObject explrObject = jsonArray.getJSONObject(i);

                                            String attandance_description=explrObject.getString("AA_ATTENDANCE_DESCRIPTION");
                                            String attandance_status=explrObject.getString("EAR_ATTENDANCE_STATUS");
                                            String display_attendance_date=explrObject.getString("DISPLAY_ATTENDANCE_DATE");
                                            String ear_attendance_date=explrObject.getString("EAR_ATTENDANCE_DATE");

//                                            if(i==0){
                                            dateSplit = display_attendance_date.split("-");
//                                            }

                                            dateSplit1 = ear_attendance_date.split("/");

                                            Log.e("Display Date","Display Att Date"+display_attendance_date+" Split = "+dateSplit[0]);
                                            Log.e("Ear Date","Ear Att Date"+ear_attendance_date+" Split = "+dateSplit1[1]);

                                            mapData.put("AA_ATTENDANCE_DESCRIPTION",attandance_description);
                                            mapData.put("EAR_ATTENDANCE_STATUS",attandance_status);
                                            mapData.put("DISPLAY_ATTENDANCE_DATE",dateSplit[0]);
                                            mapData.put("EAR_ATTENDANCE_DATE",dateSplit1[1]);

                                            arlData.add(mapData);
//
                                        }
//                                        Log.e("ArrayList 1","arlData.toString()"+dateSplit[0]);
                                        System.out.println("ArrayList==" + arlData);

                                    adapter = new CalendarAdapter(getActivity(),month,arlData);
                                    gridview.setAdapter(adapter);

                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                            error.getMessage();
                        error.printStackTrace();
                        Log.e("Error",""+error.getMessage());
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
//                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);

                return ATTENDANCE_URL;


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            Log.d("Simple Result", result);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try{


                        //DisplayHolidayList(arlData);
//                        adapter = new CalendarAdapter(getActivity(),month,arlData);
//                        gridview.setAdapter(adapter);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    public void refreshCalendar() {

        try {
            TextView title = getActivity().findViewById(R.id.title);

            if(adapter != null) {
                adapter.refreshDays();
                adapter.notifyDataSetChanged();
            }
//        handler.post(calendarUpdater); // generate some random calendar items

            title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void onNewIntent(Intent intent) {
        String date = intent.getStringExtra("date");
        // String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
        //  month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            items.clear();
            // format random values. You can implement a dedicated class to provide real values
            for(int i=0;i<31;i++) {
                Random r = new Random();

                if(r.nextInt(10)>6)
                {
                    items.add(Integer.toString(i));
                }
            }

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    };
}


