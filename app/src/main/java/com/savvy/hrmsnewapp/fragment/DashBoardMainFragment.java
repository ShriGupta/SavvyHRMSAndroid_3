package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.savvy.hrmsnewapp.activity.LoginActivity_1;
import com.savvy.hrmsnewapp.adapter.DashBoardHolidayListAdapter;
import com.savvy.hrmsnewapp.adapter.DashBoardListAdapter;
import com.savvy.hrmsnewapp.customwidgets.CircularImageView;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashBoardMainFragment extends BaseFragment {

    HolidayListAsynTask holidayListAsynTask;
    ExpandableListView expListView, expListViewHoliday;
    DashBoardListAdapter mAdapter;
    DashBoardHolidayListAdapter mAdapter1;
    CustomTextView user_profile_name;
    ImageView headcoverimage;
    CircularImageView user_profile_photo;
    //ImageView user_profile_photo;

    ProfilephotoAsynTask profilephotoAsynTask;
    List<String> listDataHeader;
    List<String> listDataHeader1;
    HashMap<String, List<String>> listDataChild;

    HashMap<String, ArrayList<HashMap<String, String>>> listNewData;
    HashMap<String, ArrayList<HashMap<String, String>>> listNewData1;
    ArrayList<HashMap<String, String>> arlData1;
    ArrayList<HashMap<String, String>> arlData_Announcement;
    ArrayList<HashMap<String, String>> arlData_ThoughtOfDay;
    ArrayList<HashMap<String, String>> arlData_Birthday;
    ArrayList<HashMap<String, String>> arlData_Marriage_Anniversary;
    ArrayList<HashMap<String, String>> arlData_Join_Anniversary;
    SharedPreferences shared;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    String Group_Id = "";
    String photoCode = "";

    LinearLayout linear_expandable;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.Holiday_Count = 0;
        Constants.Birthday_Count = 0;
        Constants.Anniversary_Count = 0;
        Constants.Join_Anniversary_Count = 0;
        Constants.Announcement_Count = 0;
        Constants.Thought_Count = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.testingdevice, container, false);

        arlData1 = new ArrayList<HashMap<String, String>>();
        arlData_Announcement = new ArrayList<HashMap<String, String>>();
        arlData_ThoughtOfDay = new ArrayList<HashMap<String, String>>();
        arlData_Birthday = new ArrayList<HashMap<String, String>>();
        arlData_Marriage_Anniversary = new ArrayList<HashMap<String, String>>();
        arlData_Join_Anniversary = new ArrayList<HashMap<String, String>>();

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        String token = (shared.getString("Token", ""));
        String empoyeeId = (shared.getString("EmpoyeeId", ""));
        Group_Id = (shared.getString("GroupId", ""));
        Log.e("GroupId = ", Group_Id);

        getProfileData(empoyeeId, token);
        getHolidayListData(empoyeeId, token);

        expListView = view.findViewById(R.id.dashBoardList);
        linear_expandable = view.findViewById(R.id.linear_expandable);

        user_profile_name = view.findViewById(R.id.user_profile_name);
        headcoverimage = view.findViewById(R.id.header_cover_image);
        user_profile_photo = view.findViewById(R.id.user_profile_photo);

//        expListView.setVisibility(View.GONE);
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                expListView.setVisibility(View.VISIBLE);
//            }
//        }, 500*1);
//
        expListView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//        linear_expandable.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;

        expListView.setDividerHeight(5);
        prepareListData();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy", Locale.UK);
//        currentDate = sdf.format(date);
        String newDateFormat = "<font color='#EE0000'>" + sdf.format(date) + "</font>";
        String currentDate = String.valueOf(Html.fromHtml(newDateFormat));
        mAdapter = new DashBoardListAdapter(expListView, getActivity(), listDataHeader, listNewData, arlData1, arlData_Announcement,
                arlData_Birthday, arlData_Marriage_Anniversary, arlData_ThoughtOfDay, arlData_Join_Anniversary, newDateFormat);

        expListView.setAdapter(mAdapter);

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousList = -1;

            @Override
            public void onGroupExpand(int groupPosition) {

                if (groupPosition != previousList) {
                    expListView.collapseGroup(previousList);
                }
                previousList = groupPosition;
//                expListView.getLayoutParams().height=4500;
//                linear_expandable.getLayoutParams().height=4500;
            }
        });

//        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
////                expListView.getLayoutParams().height=4500;
////                linear_expandable.getLayoutParams().height=4500;
//            }
//        });
        return view;
    }

    private void getProfileData(String empoyeeId, String token) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                profilephotoAsynTask = new DashBoardMainFragment.ProfilephotoAsynTask();
                profilephotoAsynTask.empid = empoyeeId;
                profilephotoAsynTask.token = token;
                profilephotoAsynTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getHolidayListData(String empoyeeId, String token) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData1 = new ArrayList<HashMap<String, String>>();
                holidayListAsynTask = new HolidayListAsynTask();
                holidayListAsynTask.empid = empoyeeId;

                holidayListAsynTask.token = token;
                holidayListAsynTask.execute();

			/*new LoginAsynTask(LoginActivity.this, LoginActivity.this,
					strEmail, strPassword, OperationPerformName.OPERATION_LOGIN)
					.execute();*/

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareListData() {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listNewData = new HashMap<String, ArrayList<HashMap<String, String>>>();

        // Adding child data
        listDataHeader.add("BirthDay");
        listDataHeader.add("Marriage Anniversary");
        listDataHeader.add("Holidays");
        listDataHeader.add("Thought of the Day");
        listDataHeader.add("Announcement");
        listDataHeader.add("Joining Anniversary");

        getAnnouncementData(Group_Id);
        getBirthdayAnniversaryData(Group_Id);

        listNewData.put(listDataHeader.get(0), arlData_Birthday);
        listNewData.put(listDataHeader.get(1), arlData_Marriage_Anniversary);
        listNewData.put(listDataHeader.get(2), arlData1);
        listNewData.put(listDataHeader.get(3), arlData_Announcement);
        listNewData.put(listDataHeader.get(4), arlData_ThoughtOfDay);
        listNewData.put(listDataHeader.get(5), arlData_Join_Anniversary);

    }

    public void getAnnouncementData(String GroupId) {
        try {

            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();
            pm.put("GROUP_ID", GroupId);

            final String Attd_Url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetAnnouncementForEmployee";

            System.out.println("URL=1==" + Attd_Url);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Attd_Url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + String.valueOf(response));

                            try {
                                JSONArray jsonArray = response.getJSONArray("GetAnnouncementForEmployeeResult");

                                Log.e("Length JsonArray", "" + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> mapData = new HashMap<String, String>();
                                    HashMap<String, String> mapData1 = new HashMap<String, String>();

                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String AS_MESSAGE = explrObject.getString("AS_MESSAGE");
                                    String TYPE = explrObject.getString("AS_TYPE");

                                    if (TYPE.equals("1")) {
                                        mapData.put("AS_MESSAGE", AS_MESSAGE);
                                        mapData.put("AS_TYPE", TYPE);
                                        Constants.Announcement_Count++;
                                        mapData.put("Announcement", "" + Constants.Announcement_Count);
                                        arlData_Announcement.add(mapData);
                                    } else if ((TYPE.equals("2"))) {
                                        mapData1.put("AS_MESSAGE", AS_MESSAGE);
                                        mapData1.put("AS_TYPE", TYPE);
                                        Constants.Thought_Count++;
                                        mapData.put("Thought_Count", "" + Constants.Thought_Count);
                                        arlData_ThoughtOfDay.add(mapData1);
                                    }

                                }


                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.e("Error1", "" + ex.getMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error", "" + error.toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getBirthdayAnniversaryData(String GroupId) {
        final ArrayList<HashMap<String, String>> arlData_Birthday1 = new ArrayList<HashMap<String, String>>();
        try {

            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();
            pm.put("GROUP_ID", GroupId);

            final String Attd_Url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeBirthdayAnniversary";

            System.out.println("URL=1==" + Attd_Url);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Attd_Url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + String.valueOf(response));

                            try {
//                                        JSONArray jsonArray = new JSONArray(response);
                                JSONArray jsonArray = response.getJSONArray("GetEmployeeBirthdayAnniversaryResult");

                                Log.e("Length JsonArray", "" + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> mapData = new HashMap<String, String>();
                                    HashMap<String, String> mapData1 = new HashMap<String, String>();
                                    HashMap<String, String> mapData2 = new HashMap<String, String>();

                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String employeeId = explrObject.getString("EMPLOYEE_ID");
                                    String employeeCode = explrObject.getString("EMPLOYEE_CODE");
                                    String employeeName = explrObject.getString("EMPLOYEE_NAME");
                                    String departmentName = explrObject.getString("D_DEPARTMENT_NAME");
                                    String designation = explrObject.getString("D_DESIGNATION");
                                    String type = explrObject.getString("TYPE");

                                    Log.e("Type Birthday", type);

                                    if ((type.equals("1"))) {
                                        mapData.put("EMPLOYEE_ID", employeeId);
                                        mapData.put("EMPLOYEE_CODE", employeeCode);
                                        mapData.put("EMPLOYEE_NAME", employeeName);
                                        mapData.put("D_DEPARTMENT_NAME", departmentName);
                                        mapData.put("D_DESIGNATION", designation);
                                        mapData.put("TYPE", type);

                                        Constants.Birthday_Count++;
                                        arlData_Birthday.add(mapData);

                                    } else if (type.equals("2")) {
                                        mapData1.put("EMPLOYEE_ID", employeeId);
                                        mapData1.put("EMPLOYEE_CODE", employeeCode);
                                        mapData1.put("EMPLOYEE_NAME", employeeName);
                                        mapData1.put("D_DEPARTMENT_NAME", departmentName);
                                        mapData1.put("D_DESIGNATION", designation);
                                        mapData1.put("TYPE", type);

                                        Constants.Anniversary_Count++;
                                        arlData_Marriage_Anniversary.add(mapData1);
                                    } else if (type.equals("3")) {
                                        mapData2.put("EMPLOYEE_ID", employeeId);
                                        mapData2.put("EMPLOYEE_CODE", employeeCode);
                                        mapData2.put("EMPLOYEE_NAME", employeeName);
                                        mapData2.put("D_DEPARTMENT_NAME", departmentName);
                                        mapData2.put("D_DESIGNATION", designation);
                                        mapData2.put("TYPE", type);

                                        Constants.Join_Anniversary_Count++;
                                        arlData_Join_Anniversary.add(mapData2);
                                    }
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.e("Error1", "" + ex.getMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error", "" + error.toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class HolidayListAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
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


                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";

                String ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/EmployeeHoliday/" + empid + "/0/" + token;
                System.out.println("ATTENDANCE_URL====" + ATTENDANCE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        ATTENDANCE_URL, "GET");

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try {
                    listDataHeader1 = new ArrayList<String>();
                    listNewData1 = new HashMap<String, ArrayList<HashMap<String, String>>>();

                    HashMap<String, String> holidaymap;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            holidaymap = new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            String holidayName = explrObject.getString("holidayName");
                            String dayName = explrObject.getString("dayName");
                            String empoyeeId = explrObject.getString("employeeId");
                            String holidayDate = explrObject.getString("holidayDate");
                            String holidayType = explrObject.getString("holidayType");

//                            System.out.println("holidayName===" + holidayName);

                            holidaymap.put("holidayName", holidayName);
                            holidaymap.put("dayName", dayName);
                            holidaymap.put("holidayDate", holidayDate);
                            holidaymap.put("holidayType", holidayType);
                            holidaymap.put("employeeId", empoyeeId);

//                            Constants.Holiday_Count++;
                            SharedPreferences.Editor shareHoliday = getActivity().getSharedPreferences("Holiday_List", Context.MODE_PRIVATE).edit();
                            shareHoliday.putString("holidayName", "holidayName");
                            shareHoliday.putString("holidayDate", "holidayDate");
                            shareHoliday.putString("dayName", "dayName");
                            arlData1.add(holidaymap);

                            listDataHeader1.add("Holiday");
//
//                            listNewData1.put(listDataHeader1.get(0),arlData1);
//                            mAdapter1 = new DashBoardHolidayListAdapter(getActivity(),listDataHeader1,listNewData1, arlData1);


//                            recyclerView.setAdapter(mAdapter);
                        }
                        Constants.Holiday_Count = arlData1.size();
                        System.out.println("Array===" + arlData1);

                    } else {
//                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        System.out.println("Data not getting on server side");
                    }


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

    private class ProfilephotoAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        String token;

        @Override
        protected void onPreExecute() {
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.setMax(100);
                pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String PROFILE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/EmployeeProfile/" + empid + "/" + token;
                System.out.println("ATTENDANCE_URL====" + PROFILE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(PROFILE_URL, "GET");
                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog != null && pDialog.isShowing()) {
                try {
                    pDialog.dismiss();

                    JSONObject jsonobj = new JSONObject(result);
                    String employeeName = jsonobj.getString("employeeName");
                    String empoyeeId = jsonobj.getString("employeeId");
                    photoCode = jsonobj.getString("photoCode");

                    if (empoyeeId.equals("") || empoyeeId == null) {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }, 200);
                    } else {
                        user_profile_name.setText(employeeName);
                        if (photoCode != "" && photoCode.length() > 0) {
                            String photourl = photoCode.replace("\\", "");
                            Picasso.get().load(photourl).into(headcoverimage);
                            Picasso.get().load(photourl).error(R.drawable.profile_image).into(user_profile_photo);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        getActivity().finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
