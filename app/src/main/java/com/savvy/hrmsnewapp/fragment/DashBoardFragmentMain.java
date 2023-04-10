package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.savvy.hrmsnewapp.activity.BirthDayActivity;
import com.savvy.hrmsnewapp.activity.LoginActivity_1;
import com.savvy.hrmsnewapp.adapter.DashboardMainAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.RoundedImageView;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class DashBoardFragmentMain extends Fragment {
    RecyclerView empRecyclerView;
    DashboardMainAdapter dashboardMainAdapter;
    DashBoardFragmentMain.ProfilephotoAsynTask profilephotoAsynTask;
    DashBoardFragmentMain.HolidayListAsynTask holidayListAsynTask;

    ArrayList<HashMap<String, String>> arlData_Birthday;
    ArrayList<HashMap<String, String>> arlData_Marriage_Anniversary;
    ArrayList<HashMap<String, String>> arlData_Join_Anniversary;
    ArrayList<HashMap<String, String>> holiday_Data;
    ArrayList<HashMap<String, String>> arlData_Announcement;
    ArrayList<HashMap<String, String>> arlData_ThoughtOfDay;


    CoordinatorLayout coordinatorLayout;
    // ImageView profile_image_dashboard;
    RoundedImageView profile_image_dashboard;
    CustomTextView empDashboardName;
    LinearLayout empLinearLayout;

    SharedPreferences shared;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String Group_Id = "";
    String photoCode = "";


    String[] emp_Festive = {"Birthday", "Marriage Anniversary", "Joining Anniversary", "Holidays", "Thought of the day", "Announcement"};
    private static final Integer[] FestivePicture = {
            R.drawable.new_birthday_icon,
            R.drawable.marriage_anniversarry_icon,
            R.drawable.join_anniversary_icon,
            R.drawable.holidays,
            R.drawable.new_thought_icon,
            R.drawable.announcement,
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constants.Holiday_Count = 0;
        Constants.Birthday_Count = 0;
        Constants.Anniversary_Count = 0;
        Constants.Join_Anniversary_Count = 0;
        Constants.Announcement_Count = 0;
        Constants.Thought_Count = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        String token = (shared.getString("Token", ""));
        String empoyeeId = (shared.getString("EmpoyeeId", ""));
        Group_Id = (shared.getString("GroupId", ""));
        Log.e("GroupId = ", Group_Id);
        arlData_Birthday = new ArrayList<HashMap<String, String>>();
        arlData_Join_Anniversary = new ArrayList<HashMap<String, String>>();
        holiday_Data = new ArrayList<HashMap<String, String>>();
        arlData_Announcement = new ArrayList<HashMap<String, String>>();
        arlData_ThoughtOfDay = new ArrayList<HashMap<String, String>>();
        arlData_Marriage_Anniversary = new ArrayList<HashMap<String, String>>();

        getProfileData(empoyeeId, token);
        getHolidayListData(empoyeeId, token);

        return inflater.inflate(R.layout.fragment_dashboad_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        empRecyclerView = getActivity().findViewById(R.id.empRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        empRecyclerView.setLayoutManager(layoutManager);
        empRecyclerView.setItemAnimator(new DefaultItemAnimator());
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        profile_image_dashboard = getActivity().findViewById(R.id.profile_image_dashboard);
        empDashboardName = getActivity().findViewById(R.id.empDashboardName);

        getAnnouncementData(Group_Id);
        getBirthDayData(Group_Id);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dashboardMainAdapter = new DashboardMainAdapter(getActivity(), emp_Festive, FestivePicture);
                empRecyclerView.setAdapter(dashboardMainAdapter);
            }
        }, 500);


        empRecyclerView.addOnItemTouchListener(new DashBoardFragmentMain.RecyclerTouchListener(getContext(), empRecyclerView, new DashBoardFragmentMain.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                    // final HashMap<String, String> mapdata = arlData.get(position);


                    empLinearLayout = view.findViewById(R.id.linear_dashboard_row);

                    empLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e(TAG, "onClick: " + position);
                            if (position == 0) {
                                Intent intent = new Intent(getActivity(), BirthDayActivity.class);
                                intent.putExtra("key", arlData_Birthday);
                                intent.putExtra("type", "arlData_Birthday");
                                startActivity(intent);
                            } else if (position == 1) {
                                Intent intent = new Intent(getActivity(), BirthDayActivity.class);
                                intent.putExtra("key", arlData_Marriage_Anniversary);
                                intent.putExtra("type", "arlData_Marriage_Anniversary");
                                startActivity(intent);
                            } else if (position == 2) {
                                Intent intent = new Intent(getActivity(), BirthDayActivity.class);
                                intent.putExtra("key", arlData_Join_Anniversary);
                                intent.putExtra("type", "arlData_Join_Anniversary");
                                startActivity(intent);
                            } else if (position == 3) {
                                Intent intent = new Intent(getActivity(), BirthDayActivity.class);
                                intent.putExtra("key", holiday_Data);
                                intent.putExtra("type", "holiday_Data");
                                startActivity(intent);
                            } else if (position == 4) {
                                Intent intent = new Intent(getActivity(), BirthDayActivity.class);
                                intent.putExtra("key", arlData_ThoughtOfDay);
                                intent.putExtra("type", "arlData_ThoughtOfDay");
                                startActivity(intent);
                            } else if (position == 5) {
                                Intent intent = new Intent(getActivity(), BirthDayActivity.class);
                                intent.putExtra("key", arlData_Announcement);
                                intent.putExtra("type", "arlData_Announcement");
                                startActivity(intent);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    private void getProfileData(String empoyeeId, String token) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                profilephotoAsynTask = new DashBoardFragmentMain.ProfilephotoAsynTask();
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
                    String empoyeeCode = jsonobj.getString("employeeCode");
                    String empDetail = "[" + empoyeeCode + "]" + " " + employeeName;
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
                        empDashboardName.setText(employeeName);
                        if (photoCode != "" && photoCode.length() > 0) {
                            String photourl = photoCode.replace("\\", "");
                            Picasso.get().load(photourl).error(R.drawable.profile_image).into(profile_image_dashboard);
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

    private void getHolidayListData(String empoyeeId, String token) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

                holidayListAsynTask = new DashBoardFragmentMain.HolidayListAsynTask();
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
                            shareHoliday.apply();
                            holiday_Data.add(holidaymap);

//                            listNewData1.put(listDataHeader1.get(0),arlData1);
//                            mAdapter1 = new DashBoardHolidayListAdapter(getActivity(),listDataHeader1,listNewData1, arlData1);


//                            recyclerView.setAdapter(mAdapter);
                        }
                        Constants.Holiday_Count = holiday_Data.size();
                        System.out.println("Array===" + holiday_Data);


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

                                    if (TYPE.equals("2")) {
                                        mapData.put("AS_MESSAGE", AS_MESSAGE);
                                        mapData.put("AS_TYPE", TYPE);
                                        Constants.Announcement_Count++;
                                        mapData.put("Announcement", "" + Constants.Announcement_Count);
                                        arlData_Announcement.add(mapData);
                                        Log.e("Announcement_Count", String.valueOf(Constants.Announcement_Count));
                                    } else if ((TYPE.equals("1"))) {
                                        mapData1.put("AS_MESSAGE", AS_MESSAGE);
                                        mapData1.put("AS_TYPE", TYPE);
                                        Constants.Thought_Count++;
                                        mapData.put("Thought_Count", "" + Constants.Thought_Count);
                                        arlData_ThoughtOfDay.add(mapData1);
                                        Log.e("Thought_Count", String.valueOf(Constants.Thought_Count));
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
//                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getBirthDayData(String GroupId) {
        try {

            final JSONObject pm = new JSONObject();
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
//
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
                                    String joinDate = explrObject.getString("ACTUAL_DATE");
                                    String joinyear = explrObject.getString("TENURE");

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

                                        Log.e("Birthday_Count", String.valueOf(Constants.Birthday_Count));

                                    } else if (type.equals("2")) {
                                        mapData1.put("EMPLOYEE_ID", employeeId);
                                        mapData1.put("EMPLOYEE_CODE", employeeCode);
                                        mapData1.put("EMPLOYEE_NAME", employeeName);
                                        mapData1.put("D_DEPARTMENT_NAME", departmentName);
                                        mapData1.put("D_DESIGNATION", designation);
                                        mapData1.put("TYPE", type);

                                        Constants.Anniversary_Count++;
                                        arlData_Marriage_Anniversary.add(mapData1);
                                        Log.e("Anniversary_Count", String.valueOf(Constants.Anniversary_Count));
                                    } else if (type.equals("3")) {
                                        mapData2.put("EMPLOYEE_ID", employeeId);
                                        mapData2.put("EMPLOYEE_CODE", employeeCode);
                                        mapData2.put("EMPLOYEE_NAME", employeeName);
                                        mapData2.put("D_DEPARTMENT_NAME", departmentName);
                                        mapData2.put("D_DESIGNATION", designation);
                                        mapData2.put("TYPE", type);
                                        mapData2.put("ACTUAL_DATE", joinDate);
                                        mapData2.put("TENURE", joinyear);

                                        Constants.Join_Anniversary_Count++;
                                        arlData_Join_Anniversary.add(mapData2);
                                        Log.e("Join_Anniversary_Count", String.valueOf(Constants.Join_Anniversary_Count));
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
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private DashBoardFragmentMain.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final DashBoardFragmentMain.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
//                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null && clickListener != null) {
//                        clickListener.onClick(child, recyclerView.getChildPosition(child));
//                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
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
