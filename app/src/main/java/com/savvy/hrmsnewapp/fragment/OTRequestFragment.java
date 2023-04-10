package com.savvy.hrmsnewapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.CustomerSpinnerTesting;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.model.Pojo;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.DialogType;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.StatusAdapterRecycler;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.fragment.DashBoardMainFragment.MY_PREFS_NAME;

public class OTRequestFragment extends BaseFragment {

    AlertDialog.Builder builder;
    AlertDialog dialog;

    Button btn_overtime_date,btn_overtime_from,btn_overtime_to;
    Button btn_submit, btn_overtime_from_date;
    ToggleButton tog_shift;
    Spinner spin_reason_ot;
    CustomSpinnerAdapter customspinnerAdapter;
    CustomerSpinnerTesting customerSpinnerTesting;

    ImageView imageView;
    CalanderHRMS calanderHRMS;
    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;
    RequestStatusAsynTask requestStatusasynctask;

    String str_hour = "",str_minute = "";
    String Input = "";
    String spinStr = "";
    static int counter = 0;

    String str = "";
    StringBuilder stringBuilder;
    boolean FLAG_MY = false;

    HashMap<String, String> mapdata;
    HashMap<String, String> mapdata1;
    String str1 = "";

    RecyclerView recyclerView;
    StatusAdapterRecycler mAdapter;
    ArrayList<HashMap<String,String>> arldata;
    SharedPreferences shared;

    CustomTextView txtOt_DateTitle,txtOt_FromTimeTitle,txtOt_ToTimeTitle,txtOt_ReasonTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counter++;
        arldata = new ArrayList<HashMap<String,String>>();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

//        GetCompareDateResult();
//
//
//        final Handler someHandler = new Handler(getMainLooper());
//        someHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                TextView tvClock = (TextView)getActivity().findViewById(R.id.dateFormat);
//                tvClock.setText(new SimpleDateFormat("HH:mm", Locale.US).format(new Date()));
//                someHandler.postDelayed(this, 1000);
//            }
//        }, 10);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_otrequest, container, false);

        calanderHRMS = new CalanderHRMS(getActivity());

        btn_overtime_date = view.findViewById(R.id.btn_overtime_date);
        btn_overtime_from_date = view.findViewById(R.id.btn_overtime_from_date);
        btn_overtime_from = view.findViewById(R.id.btn_overtime_from);
        btn_overtime_to = view.findViewById(R.id.btn_overtime_to);
        tog_shift = view.findViewById(R.id.tog_post_preShift);
        spin_reason_ot = view.findViewById(R.id.spin_ot_reason);
        imageView = view.findViewById(R.id.imageView);

        btn_submit = view.findViewById(R.id.btn_ot_submit);

        txtOt_DateTitle= view.findViewById(R.id.txtOt_DateTitle);
        txtOt_FromTimeTitle= view.findViewById(R.id.txtOt_FromTimeTitle);
        txtOt_ToTimeTitle= view.findViewById(R.id.txtOt_ToTimeTitle);
        txtOt_ReasonTitle= view.findViewById(R.id.txtOt_ReasonTitle);

        String str1 = "<font color='#EE0000'>*</font>";

        txtOt_DateTitle.setText(Html.fromHtml("Overtime Date " + str1));
        txtOt_FromTimeTitle.setText(Html.fromHtml("From Time " + str1));
        txtOt_ToTimeTitle.setText(Html.fromHtml("To Time " + str1));
        txtOt_ReasonTitle.setText(Html.fromHtml("Reason " + str1));

        getStatusList();

        String[] str_reason = {"Please Select Reason","Project Work is Pending","Extra Work","Collaegues are on leave"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,str_reason);
        spin_reason_ot.setAdapter(arrayAdapter);

        btn_overtime_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogType dialogType = new DialogType(getActivity());
                dialogType.myDialog("422");
            }
        });

        final View view21 = LayoutInflater.from(getActivity()).inflate(R.layout.camera, null);
        builder = new AlertDialog.Builder(getActivity());
        builder.setView(view21);
        dialog =builder.create();

        Button camera1 = view21.findViewById(R.id.camera);
        Button importLibrary = view21.findViewById(R.id.gallery);

        camera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        importLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureActionIntent,1);


            }
        });
//        builder.show();

        btn_overtime_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(getActivity(), ScrollingActivity.class);
//                startActivity(intent);
//                builder.show();
//                btn_overtime_date.setText(String.format("%s.png", btn_overtime_from.getText().toString()));

//                if(FLAG_MY){
//                    stringBuilder =new StringBuilder();
//                    str = "";
//                }
//                final CheckBox[] checkBox = new CheckBox[arlRequestStatusData.size()];
//                TextView txt1 = new TextView(getActivity());
//                LinearLayout l1 = new LinearLayout(getActivity());
//                l1.setOrientation(LinearLayout.VERTICAL);
//                stringBuilder = new StringBuilder();
////                getActivity().addContentView(txt1,null);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setCancelable(true);
//                builder.setTitle("Please Select");
//
//                Log.e("ArrayList ",arlRequestStatusData.toString());
//                Log.e("ArrayList ",""+arlRequestStatusData.size());
//                try {
//                    for (int i = 0; i < arlRequestStatusData.size(); i++) {
//                        mapdata = arlRequestStatusData.get(i);
//
//                        checkBox[i] = new CheckBox(getActivity());
//                        Log.e("Value Of pending ",mapdata.get("VALUE"));
//                        Log.e("Value Of Check ",""+checkBox.length);
//                        checkBox[i].setText(mapdata.get("VALUE"));
//                        l1.addView(checkBox[i]);
//
//                        final int finalI = i;
//                        checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                                if(isChecked){
//                                    str = str+checkBox[finalI].getText().toString()+",";
//                                    stringBuilder.append(mapdata.get("VALUE")+",");
//
//                                    Log.e("String Buider",stringBuilder.toString());
//                                    Log.e("String Simple",str);
//                                }
//                            }
//                        });
//                    }
////                    for(int j = 0;j<arlRequestStatusData.size();j++){
////                        mapdata1 = arlRequestStatusData.get(j);
////                        checkBox[j].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////                            @Override
////                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////
////                                if(isChecked){
////                                    str = str+checkBox[j].getText().toString()+",";
////                                    stringBuilder.append(mapdata1.get("VALUE")+",");
////
////                                    Log.e("String Buider",stringBuilder.toString());
////                                    Log.e("String Simple",str);
////                                }
////                            }
////                        });
////                    }
//                    builder.setView(l1);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        btn_overtime_date.setText(str.substring(0,str.length()-1));
//                        FLAG_MY = true;
//
//                    }
//                });
//                builder.show();


               // calanderHRMS.datePicker(btn_overtime_date);
               // showSelectColoursDialog();
                /*try {
                    //getStatusList();
                  //  arlRequestStatusData = new ArrayList<HashMap<String, String>>();
                    String str1 = "";
                    for(int position = 0;position<arlRequestStatusData.size();position++) {
                        HashMap<String, String> mapdata = arlRequestStatusData.get(position);

                        str1 = str1+","+mapdata.get("VALUE");
                        Log.e("String Value",str1);
                    }
                    btn_overtime_date.setText(str1);
                    Log.e("String Value",str1);
                    //Movie movie = moviesList.get(position);
//                    holder.txt_leavenameValue.setText(mapdata.get("LM_LEAVE_NAME"));
//                    holder.txv_tokenNoValue.setText(mapdata.get("TOKEN_NO"));
//                    holder.txt_fromDateValue.setText(mapdata.get("FROM_DATE"));
                }catch(Exception e){
                    e.printStackTrace();
                }*/

            }
        });

        btn_overtime_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                TravelFragmentHolder travelFragmentHolder = new TravelFragmentHolder();
//                transaction.replace(R.id.container_body, travelFragmentHolder);
//                transaction.addToBackStack(null);
//                transaction.commit();
                try {
                    Pojo pojo = new Pojo(getActivity());
                    Bitmap bitmap = pojo.getPic();
                    int size = bitmap.getByteCount();
                    while (size > (300 * 8 * 1024)) {
                        int height = (int) (bitmap.getHeight() * 0.1);
                        int width = (int) (bitmap.getWidth() * 0.1);
                        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        size = bitmap.getByteCount();
                    }
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    String lines="";
                    while(size>0){
                        lines = lines+bitmapdata;
                    }

                    System.out.print("String Value "+lines);
                    System.out.print("Byte Value "+bitmapdata.toString());
                    Log.e("String Value ",lines);
                    Log.e("Byte Value ",bitmapdata.toString());
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Image not selected",Toast.LENGTH_LONG).show();

                }


               // calanderHRMS.timePicker(btn_overtime_from);
//                Intent intent = new Intent(getActivity(), ViewHistoryActivity.class);
//                startActivity(intent);

//                Intent intent = new Intent(getActivity(), MyService12.class);




            }
        });
        btn_overtime_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.spinselect);
//                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = 500;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.gravity = Gravity.CENTER;

                    dialog.getWindow().setAttributes(lp);

                    recyclerView = dialog.findViewById(R.id.recycler);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    for (int j = 0; j < 10; j++) {
                        hashMap.put("Pending", "Pending");
//                hashMap.put("InProcess","InProcess");
//                hashMap.put("Approved","Approved");
//                hashMap.put("Rejected","Rejected");
//                hashMap.put("Pullback","Pullback");
//                hashMap.put("Cancelled","Cancelled");
//                hashMap.put("CancelPending","CancelPending");
//                hashMap.put("CancelInProcess","CancelInProcess");
//                hashMap.put("CancelApproved","CancelApproved");
//                hashMap.put("CancelRejected","CancelRejected");

                        arldata.add(hashMap);
                    }
                    Log.d("Arldata ", arldata.toString());

                    mAdapter = new StatusAdapterRecycler(getActivity(), arldata);
                    recyclerView.setAdapter(mAdapter);

                    dialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }



            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if(null != data) {
                if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    imageView.setImageBitmap(photo);
                    Pojo pojo = new Pojo(getActivity());
                    pojo.setPic(photo);

                    try {
                        Bitmap bitmap = photo;
                        int size = bitmap.getByteCount();
                        while (size > (300 * 8 * 1024)) {
                            int height = (int) (bitmap.getHeight() * 0.1);
                            int width = (int) (bitmap.getWidth() * 0.1);
                            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                            size = bitmap.getByteCount();
                        }
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        String lines="";
                        while(size>0){
                            lines = lines+bitmapdata;
                        }

                        System.out.print("String Value "+lines);
                        System.out.print("Byte Value "+bitmapdata.toString());
                        Log.e("String Value ",lines);
                        Log.e("Byte Value ",bitmapdata.toString());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"Image not selected",Toast.LENGTH_LONG).show();

                    }

                    dialog.dismiss();
                }
            }else{Log.e("Cancelled-Message","No pic selected..");}
        }catch (Exception e)
        {
//           {
//            new FileChooser(SubActivity.this).setFileListener(new FileChooser.FileSelectedListener() {
//                @Override
//                public void fileSelected(final File file) {
//                    String ext = android.webkit.MimeTypeMap.getFileExtensionFromUrl(file.getName());
//                    if (ext.toUpperCase().trim().equals("")) {
//                        importDB(file);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Please select a valid file !!!", Toast.LENGTH_SHORT).show();
//                    }
//                }

        }
    }

    private void getStatusList() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlRequestStatusData = new ArrayList<HashMap<String, String>>();
                requestStatusasynctask = new RequestStatusAsynTask();
                // requestStatusasynctask.empid=empid;
                requestStatusasynctask.execute();

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class RequestStatusAsynTask extends AsyncTask<String, String, String> {
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


                //String GETREQUESTSTATUS_URL = "http://savvyshippingsoftware.com/SavvyMobileNew/SavvyMobileService.svc/GetRequestStatus";


                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetRequestStatus";

                System.out.println("ATTENDANCE_URL===="+GETREQUESTSTATUS_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        GETREQUESTSTATUS_URL, "GET");

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
                try{
                    HashMap<String, String> requestStatusmap;
                    // ArrayList<String> requestArray;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    //requestArray=new ArrayList<String>();
                    if (jsonArray.length() > 0){
                        for(int i=0;i<jsonArray.length();i++){
                            requestStatusmap=new HashMap<String, String>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            String key=explrObject.getString("KEY");
                            String value=explrObject.getString("VALUE");
                            // requestArray.add(value);
                            requestStatusmap.put("KEY", key);
                            requestStatusmap.put("VALUE", value);
                            requestStatusmap.put("isSelected",""+false);

                            arlRequestStatusData.add(requestStatusmap);
                        }
                        System.out.println("Array===" + arlRequestStatusData);


//                        customerSpinnerTesting=new CustomerSpinnerTesting(getActivity(),arlRequestStatusData ,btn_overtime_date);
//                        btn_overtime_date.setText(customerSpinnerTesting.toString());
                       // spinner_status.setAdapter(customspinnerAdapter);

                      /*  adapterrequest = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_spinner_item, requestArray);
                        adapterrequest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_status.setAdapter(adapterrequest);*/

                        //DisplayHolidayList(arlData);
                       /* mAdapter = new LeaveStatusListAdapter(getActivity(), arlRequestStatusData);
                        recyclerView.setAdapter(mAdapter);*/

                    }else{
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

    public void GetCompareDateResult(){
//        Constants.COMPARE_DATE_API = true;
//        Log.e("Compare Method",""+Constants.COMPARE_DATE_API);


        try {
            if(Utilities.isNetworkAvailable(getActivity())) {
                String url = "http://192.168.0.16/wcfapi/megamindservice.svc/DeviceData1";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("ImageName", "ghfvs");
                params_final.put("ImagePath", "ngfvasghfc");

                pm.put("Device",params_final);


                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();
                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value", " Length = " + len + " Value = " + response.toString());
                                try {
//                                    boolean resultDate = response.getBoolean("Compare_DateResult");

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("Error", "" + error.getMessage());
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
            }else{
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
