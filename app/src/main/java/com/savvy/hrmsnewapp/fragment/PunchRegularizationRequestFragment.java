package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.ViewPunchAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class PunchRegularizationRequestFragment extends BaseFragment implements View.OnClickListener {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    LinearLayout layout_viewPunch;
    //ListView modeList;
    //private ArrayList<HashMap> punchList;
    // TextView txt_result, txt_time;
    Button btnDatPicker, btnViewPunch, btnSubmit, btn_time_picker;
    String token = "", empoyeeId = "", username = "";
    //String str_hour = "", str_minute = "", str_am = "";
    Calendar calendar;
    View view;

    RecyclerView recyclerView;
    ViewPunchAdapter viewPunchAdapter;

    ArrayList<HashMap<String, String>> arlData;

    CalanderHRMS calanderHRMS;
    PunchRegularizationRequestFragment.PunchRequestAsync punchRequestAsync;
    private ArrayList<HashMap> list;
    CoordinatorLayout coordinatorLayout;

    ArrayAdapter<String> arrayAdapter;
    ListView lview;
    PunchRegularizationRequestFragment.CurrentDateTimeAsynTask profileasynctask;
    EditText edtReason;

    String empoyeeId1 = "";
    String empoyeeName1 = "";
    PunchRegularizationRequestFragment.PunchViewAsync punchViewAsync;

    ArrayList<String> listHour, listMinute;
    CustomTextView txt_punchDate, txt_punchTime, title_reason;
    private TextView txt_AMOrPM;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        View view = getView();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));
        try {
            getCurrentDateTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // get current date time automatic to the server
    private void getCurrentDateTime() {
        try {

            if (Utilities.isNetworkAvailable(getActivity())) {

                profileasynctask = new PunchRegularizationRequestFragment.CurrentDateTimeAsynTask();

                profileasynctask.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  View rootView = getView();
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        recyclerView = getActivity().findViewById(R.id.punchRecycler);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_punch_regularization, container, false);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        viewPunchAdapter = new ViewPunchAdapter(getActivity(), arlData);

        calanderHRMS = new CalanderHRMS(getActivity());
        layout_viewPunch = view.findViewById(R.id.layout_viewPunch);

        btnViewPunch = view.findViewById(R.id.btn_view_punch);
        btnDatPicker = view.findViewById(R.id.btn_punch_date);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btn_time_picker = view.findViewById(R.id.btn_time);
        edtReason = view.findViewById(R.id.edt_reason1);

        txt_punchDate = view.findViewById(R.id.txt_punchDate);
        txt_punchTime = view.findViewById(R.id.txt_punchTime);
        txt_AMOrPM = view.findViewById(R.id.tv_am_pm);

        title_reason = view.findViewById(R.id.title_reason);

        String str1 = "<font color='#EE0000'>*</font>";
        txt_punchDate.setText(Html.fromHtml("Punch Date " + str1));
        txt_punchTime.setText(Html.fromHtml("Punch Time " + str1));
        title_reason.setText(Html.fromHtml("Reason " + str1));

        edtReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_viewPunch.setVisibility(View.INVISIBLE);//what is that?
            }
        });

        if (Constants.COMPANY_STATUS.equals("OKAYA")) {
            btnDatPicker.setEnabled(false);
            btn_time_picker.setEnabled(false);

        } else {
            btnDatPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calanderHRMS.datePicker(btnDatPicker);
                    layout_viewPunch.setVisibility(View.INVISIBLE);
                }
            });

            btn_time_picker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calanderHRMS.timePickerHHMMnew(btn_time_picker,txt_AMOrPM);
                    layout_viewPunch.setVisibility(View.INVISIBLE);
                }
            });
        }
        // view Punch
        btnViewPunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GridView gridView = new GridView(getActivity());
                View alertLayout = inflater.inflate(R.layout.fragment_view_punch, null);
                //List<Integer> mList = new ArrayList<Integer>();
                arlData = new ArrayList<HashMap<String, String>>();
                //btnDatPicker is button? needs to have text in it
                String btn_punch_date = btnDatPicker.getText().toString().replace("/", "-").trim();
                if (!(btn_punch_date.equals("comp_off_date"))) {
                    //you need to do something here
                }
                layout_viewPunch.setVisibility(View.VISIBLE);
                try {
                    if (Utilities.isNetworkAvailable(getActivity())) {

                        punchViewAsync = new PunchRegularizationRequestFragment.PunchViewAsync();

                        punchViewAsync.execute(btn_punch_date);
                        //
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        // submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(getContext(), "button_checked", Toast.LENGTH_SHORT).show();

                layout_viewPunch.setVisibility(View.INVISIBLE);

                // get data from text

                String punch_date = btnDatPicker.getText().toString().trim().replace("/", "-");
                String punch_time = btn_time_picker.getText().toString().trim().replace(":", "-");
                String reason = edtReason.getText().toString().trim().replace(" ", "_");

                try {
                    if (Utilities.isNetworkAvailable(getActivity())) {
                        if (TextUtils.isEmpty(reason)) {
                            Utilities.showDialog(coordinatorLayout, "Reason is required.");
                        } else {
                            punchRequestAsync = new PunchRegularizationRequestFragment.PunchRequestAsync();
                            punchRequestAsync.execute(punch_date, punch_time, reason);
                        }
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        //  implements View.OnClickListener here
    }

    private class PunchViewAsync extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

        // String stremail, strpassword;
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("View Punch Detail...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()

            String punchDate = params[0];
            Log.e("Punch Date", punchDate);
            System.out.print("Punch date " + punchDate);
            try {
                // String PUNCH_VIEW_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc/ViewPunches/"+empoyeeId+"/"+punchDate;
                String PUNCH_VIEW_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ViewPunches/" + empoyeeId + "/" + punchDate;
                // String PUNCH_VIEW_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc/ViewPunches/"+"3"+"/"+"10-05-2017";

                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(PUNCH_VIEW_URL, "GET");

                if (json != null) {
                    Log.e("JSON result", json.toString());

                    return json;
                }
                //jsonlist.add(map);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //finalResult.setText(result);

            Log.e("Result", result);
            System.out.print("Result " + result);

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();

                try {
                    HashMap<String, String> viewPunchMap;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            viewPunchMap = new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);

                            String punchDate = explrObject.getString("PUNCH_DATE");
                            String punchTime = explrObject.getString("PUNCH_TIME");

                            System.out.println("Punch Date===" + punchDate);
                            System.out.println("Punch Time===" + punchTime);


                            viewPunchMap.put("PUNCH_DATE", punchDate);
                            viewPunchMap.put("PUNCH_TIME", punchTime);
                            arlData.add(viewPunchMap);
                        }
                        System.out.println("Array===" + arlData);
                        Log.e("TAG", String.valueOf(arlData));
                        //Toast.makeText(getActivity(),"Array"+arlData,Toast.LENGTH_LONG).show();
                        // final SimpleAdapter ADA = new SimpleAdapter(getActivity(),arlData,R.layout.fragment_view_punch);
                        //recyclerView = new RecyclerView(getActivity());
                        viewPunchAdapter = new ViewPunchAdapter(getActivity(), arlData);
                        recyclerView.setAdapter(viewPunchAdapter);
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        builder.setView(recyclerView);
//                        builder.setTitle("Punch View");
//                        builder.show();
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
//
            }
        }


        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
    // server work in android application

    private class PunchRequestAsync extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait.....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String punch_date = "";
            String punch_time = "";
            String edt_reason = "";
            try {
                punch_date = params[0];
                punch_time = params[1];
                edt_reason = params[2];   //codefine
            } catch (Exception e) {
                e.printStackTrace();
            }
            // publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                String OdRequestURL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendPunchRequest/" + empoyeeId + "/" + "0" + "/" + punch_date + "/" + punch_time + "/" + edt_reason;

                Log.e("Punch Url", OdRequestURL);

                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(OdRequestURL, "GET");
                if (json != null) {
                    Log.d("JSON result", json);
                    return json;
                }
                //jsonlist.add(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //result true
        @Override
        protected void onPostExecute(String result) {
            Log.e("Result", "" + result);

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try {
                    result = result.replaceAll("^\"+|\"+$", " ").trim();
                    int res = Integer.valueOf(result);

                    if (res > 0) {
                        Utilities.showDialog(coordinatorLayout, "Punch request send successfully.");
                        edtReason.setText(" ");
                        edtReason.setHint("Reason");
                    } else if (res == 0) {
                        Utilities.showDialog(coordinatorLayout, "Error during sending Punch Regularization Request.");
                    } else if (res == -1) {
                        Utilities.showDialog(coordinatorLayout, "Punch request on the same date and time already exists.");
                    } else if (res == -2) {
                        Utilities.showDialog(coordinatorLayout, "Punch request for previous payroll cycle not allowed.");
                    } else if (res == -3) {
                        Utilities.showDialog(coordinatorLayout, "Allready any request applied on same date.");
                    }
                    else if (res == -4) {
                        Utilities.showDialog(coordinatorLayout, "Future punch regulation is not allowed.");
                    }
                } catch (Exception ex) {
                    Log.e("First1", "here " + ex.getMessage().toString());
                    ex.printStackTrace();
                }
            }
        }
    }

    private class CurrentDateTimeAsynTask extends AsyncTask<String, String, String> {
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
                String CURRENTDATETIME_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCurrentDateTime";
                System.out.println("CURRENTDATETIME_URL====" + CURRENTDATETIME_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        CURRENTDATETIME_URL, "GET");

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

                    // {"ServerDate":"4\/25\/2017 8:04:30 PM","ServerTime":"8:04 PM","errorMessage":null}

                    System.out.println("RESULT HARIOM====" + result);
                    JSONObject jsonobj = new JSONObject(result);
//                    String  serverDate=jsonobj.getString("ServerDate");
                    String serverDate = jsonobj.getString("ServerDateDDMMYYYYY");
                    String serverTime = jsonobj.getString("ServerTime24hour");

                    String errorMessage = jsonobj.getString("errorMessage");

                    String[] serverDateSplit = serverDate.split(" ");
                    String replacecurrDate = serverDateSplit[0].replace("\\", "");
                    btn_time_picker.setText(serverTime);

                    //newly added by vikas
                    String serverArrayTime[]=serverTime.split(":");
                    if(serverArrayTime.length>1)
                    {
                        int hour=Integer.parseInt(serverArrayTime[0]);

                        String amPM="AM";
                        if(hour>12)
                        {
                            amPM="PM";
                            txt_AMOrPM.setText(""+(hour-12)+": "+serverArrayTime[1]+" "+amPM);
                        }
                        else
                            txt_AMOrPM.setText(""+serverTime+" "+amPM);


                    }

                    btnDatPicker.setText(replacecurrDate);

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

}
