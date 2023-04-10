package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.HolidayListAdapter;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AM00347646 on 29-06-2016.
 */
public class MyHoliday extends BaseFragment {

    private TextView currentYear;
    private TextView currentMonth;
    private RelativeLayout rl_previous;
    private RelativeLayout rl_next;
    SimpleDateFormat df;
    String formattedDate = "";
    String eventDate = "";
    RecyclerView recyclerView;
    ArrayList<HashMap<String, String>> arlData;
    SharedPreferences shared;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    HolidayListAsynTask holidaylistasynctask;
    HolidayListAdapter mAdapter;
    LinearLayoutManager recylerViewLayoutManager;

    public MyHoliday() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        String token = (shared.getString("Token", ""));
        String empoyeeId = (shared.getString("EmpoyeeId", ""));

        getHolidayListData(empoyeeId, token);

        //    recylerViewLayoutManager = new LinearLayoutManager(getActivity());

//        recyclerView.setLayoutManager(recylerViewLayoutManager);

       /* mAdapter = new HolidayListAdapter(getActivity(), arlData);

        recyclerView.setAdapter(mAdapter);*/
    }

    private void getHolidayListData(String empoyeeId, String token) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<HashMap<String, String>>();
                holidaylistasynctask = new HolidayListAsynTask();
                holidaylistasynctask.empid = empoyeeId;

                holidaylistasynctask.token = token;
                holidaylistasynctask.execute();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notices, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        recyclerView = getActivity().findViewById(R.id.holidayList);
        mAdapter = new HolidayListAdapter(getActivity(), arlData);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

      /*  mAdapter = new HolidayListAdapter(getActivity(), arlData);
        recyclerView.setAdapter(mAdapter);*/

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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
            // execution of result of Long time consuming operation
            //finalResult.setText(result);

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

                            System.out.println("holidayName===" + holidayName);

                            holidaymap.put("holidayName", holidayName);
                            holidaymap.put("dayName", dayName);
                            holidaymap.put("holidayDate", holidayDate);
                            holidaymap.put("holidayType", holidayType);
                            holidaymap.put("employeeId", empoyeeId);
                            arlData.add(holidaymap);
                        }
                        System.out.println("Array===" + arlData);

                        //DisplayHolidayList(arlData);
                        mAdapter = new HolidayListAdapter(getActivity(), arlData);
                        recyclerView.setAdapter(mAdapter);
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
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

    private void DisplayHolidayList(ArrayList<HashMap<String, String>> arlData) {

        if (arlData.size() > 0) {
            // recyclerView.setVisibility(View.VISIBLE);
            // noData.setVisibility(View.GONE);
            System.out.println("Array=11111==" + arlData);
            mAdapter = new HolidayListAdapter(getActivity(), arlData);
            System.out.println("Array==2222=" + arlData);
            recyclerView.setAdapter(mAdapter);
            System.out.println("Array==3333=" + arlData);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    //drawerListener.onDrawerItemSelected(view, position);
                    //mDrawerLayout.closeDrawer(containerView);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } else {
            // recyclerView.setVisibility(View.GONE);

            //noData.setVisibility(View.VISIBLE);
        }

    }


}

