package com.savvy.hrmsnewapp.saleForce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.SaleForceAdapter.SaleForceMasterAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.BaseFragment;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SaleForce_Visit extends BaseFragment {

    Button btn_custVisit_startDate, btn_custVisit_StartTime, btn_custVisit_EndDate, btn_custVisit_EndTime;
    Button btn_custVisit_Save, btn_custVisit_Cancel;
    EditText edt_custVisit_Description, edt_custVisit_Name;
    CustomTextView edt_custVisit_selectedPurpose;
    Spinner spin_custVisit_Purpose;
//    AutoCompleteTextView av_custVisit_Name;

    String subjectValue = "";

    CoordinatorLayout coordinatorLayout;
    ArrayList<HashMap<String,String>> arldata;
    ArrayList<HashMap<String,String>> arlDataCustomer;
    ArrayList<String> Custmer_Name;

    SaleForceMasterAdapter mAdapter;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String empoyeeId = "";
    SharedPreferences shared,shareData;

    String Purpose_Id = "", Purpose_Value = "";
    HashMap<String,String> customerId_map;
    String CUST_NAME = "", CUST_ID = "";
    Bundle bundle;
    String type = "";
    String CustomerName = "", PurposeName = "", PurposeId = "", StartDate = "",
            StartTime = "", EndTime = "", Description = "", CustomerId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetVisitPurpose();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale_force__visit,container,false);

        final CalanderHRMS calanderHRMS = new CalanderHRMS(getActivity());

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

        edt_custVisit_Name = view.findViewById(R.id.edt_custVisit_Name);
//        av_custVisit_Name = view.findViewById(R.id.av_custVisit_Name);
        edt_custVisit_Description = view.findViewById(R.id.edt_custVisit_Description);

        edt_custVisit_selectedPurpose = view.findViewById(R.id.edt_custVisit_selectedPurpose);

        btn_custVisit_startDate = view.findViewById(R.id.btn_custVisit_startDate);
        btn_custVisit_StartTime = view.findViewById(R.id.btn_custVisit_StartTime);
        btn_custVisit_EndDate = view.findViewById(R.id.btn_custVisit_EndDate);
        btn_custVisit_EndTime = view.findViewById(R.id.btn_custVisit_EndTime);

        btn_custVisit_Save = view.findViewById(R.id.btn_custVisit_Save);
        btn_custVisit_Cancel = view.findViewById(R.id.btn_custVisit_Cancel);

        spin_custVisit_Purpose = view.findViewById(R.id.spin_custVisit_Purpose);

        arldata = new ArrayList<HashMap<String, String>>();
        arlDataCustomer = new ArrayList<HashMap<String, String>>();
        Custmer_Name = new ArrayList<>();


//        GetAllCustomerDetail();


//        av_custVisit_Name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    String positionValue = av_custVisit_Name.getText().toString().trim();
//                    for (int i = 0; i < arlDataCustomer.size(); i++) {
//                        if (positionValue.equals(arlDataCustomer.get(i).get("customerName"))) {
//                            CUST_NAME = arlDataCustomer.get(i).get("customerName");
//                            CUST_ID = arlDataCustomer.get(i).get("customerId");
//                            Log.e("Customer Name", "CUST_NAME = " + CUST_NAME + " CUST_ID -> " + CUST_ID);
//                        }
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });


//        av_custVisit_Name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                customerId_map = arlDataCustomer.get(position);
//                CUST_NAME = customerId_map.get("customerName");
//                CUST_ID = customerId_map.get("customerId");
//            }
//        });

//        av_custVisit_Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                try {
//                    if (!b) {
//                        // on focus off
//                        String str = av_custVisit_Name.getText().toString();
//
//                        ListAdapter listAdapter = av_custVisit_Name.getAdapter();
//                        for (int i = 0; i < listAdapter.getCount(); i++) {
//                            String temp = listAdapter.getItem(i).toString();
//                            if (str.compareTo(temp) == 0) {
//                                return;
//                            }
//                        }
//                        av_custVisit_Name.setText("");
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });

//        String[] strArray = {"Please Select","Demonstration","Follow Up","New Business","Support Visit","Unlnown"};
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,strArray);
//
//        spin_custVisit_Purpose.setAdapter(arrayAdapter);
        mAdapter = new SaleForceMasterAdapter(getActivity(),arldata,"");
        spin_custVisit_Purpose.setAdapter(mAdapter);

        try{
            bundle = this.getArguments();
            if (bundle != null) {
                type = bundle.getString("Type");

                if(type.equals("SaleForceMap")){
                    CustomerName = bundle.getString("CustomerName");
                    CustomerId = bundle.getString("CustomerId");
                } else if(type.equals("SaleActivity")){
                    CustomerName = bundle.getString("CustomerName");
                    CustomerId = bundle.getString("CustomerId");
                    PurposeName = bundle.getString("PurposeName");
                    PurposeId = bundle.getString("PurposeId");
                    StartDate = bundle.getString("StartDate");
                    StartTime = bundle.getString("StartTime");
                    EndTime = bundle.getString("EndTime");
                    Description = bundle.getString("Description");
                }

                Log.e("Bundle Value","Customer Name->"+CustomerName+", PurposeName = "+PurposeName
                        +", PurposeId = "+PurposeId+", StartTime = "+StartTime+", EndTime = "+EndTime+", Description = "+Description);

                try {
                    if(type.equals("SaleForceMap")){
                        edt_custVisit_Name.setText(CustomerName);
                    } else if(type.equals("SaleActivity")){
//                        Log.e("Size","ArlData Size = "+arldata.size());
//                        for(int i=0;i<arldata.size();i++){
//                            int res = Integer.parseInt(PurposeId)-1;
//                            if(res == i){
//                                spin_custVisit_Purpose.setSelection(arldata.indexOf(PurposeId));
//                            }
//                        }
                        edt_custVisit_Name.setText(CustomerName);
                        btn_custVisit_startDate.setText(StartDate);
                        btn_custVisit_StartTime.setText(StartTime);
                        btn_custVisit_EndTime.setText(EndTime);
                        edt_custVisit_Description.setText(Description);
                        edt_custVisit_selectedPurpose.setText("" + PurposeName + " (Subject)");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

//                String cust = arldata;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        btn_custVisit_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(btn_custVisit_startDate);
            }
        });

        btn_custVisit_StartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.timePickerHHMM(btn_custVisit_StartTime);
            }
        });

        btn_custVisit_EndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(btn_custVisit_EndDate);
            }
        });

        btn_custVisit_EndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.timePickerHHMM(btn_custVisit_EndTime);
            }
        });

        btn_custVisit_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (bundle != null) {
                        type = bundle.getString("Type");

                        if(type.equals("SaleForceMap")) {
                            Intent intent = new Intent(getActivity(), SalesForceMapActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else if(type.equals("SaleActivity")){
                            Activity_Notes_Contact_Holder getCurrentLocation = new Activity_Notes_Contact_Holder();

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container_body, getCurrentLocation);
                            fragmentTransaction.commit();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        spin_custVisit_Purpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {

                    if(position==0){
                        Purpose_Id = "";
                        Purpose_Value = "";
                        if(!type.equals("SaleActivity")){
                            edt_custVisit_selectedPurpose.setText("");
                        }
                    } else if(position>0){
//                        HashMap<String,String> mapData = arldata.get(position-1);
//                        arlData1 = new ArrayList<HashMap<String, String>>();
                        Purpose_Id = arldata.get(position-1).get("PositionId");
                        Purpose_Value = arldata.get(position-1).get("PositionName");
                        edt_custVisit_selectedPurpose.setText(""+Purpose_Value+" (Subject)");
                    }
                    Log.e("Purpose Expense Id",Purpose_Id+" Value = "+Purpose_Value);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_custVisit_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customer_name = CustomerName;
                String customer_id = CustomerId;
                String startDate = btn_custVisit_startDate.getText().toString().trim();
                String startTime = btn_custVisit_StartTime.getText().toString().trim();
                String endTime = btn_custVisit_EndTime.getText().toString().trim();

                String purpose = Purpose_Value;
                String purpose_id = Purpose_Id;
                String description = edt_custVisit_Description.getText().toString().trim();

                if(customer_name.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Enter Valid Customer Name.");
                } else if(startDate.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Select Start Date.");
                } else if(startTime.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Select Start Time.");
                } else if(purpose.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Select Valid Purpose.");
                } else if(description.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Enter Description.");
                } else{
                    SaveCustomerVisit(customer_id,startDate,startTime,endTime,purpose,purpose_id,description);
                }
            }
        });
        return view;
    }

    public void GetVisitPurpose(){
        try{
            if(Utilities.isNetworkAvailable(getActivity())){
                arldata = new ArrayList<HashMap<String, String>>();
                JSONObject param = new JSONObject();

                param.put("groupId","1");
                String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetPurposeSalesforcePost";

                Log.e("Url ",""+url);


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    Log.e("Vist Purpose","Response "+response.toString());
                                    HashMap<String,String> hashMap;
                                    JSONArray jsonArray = response.getJSONArray("GetPurposeSalesforcePostResult");

                                    for(int i=0;i<jsonArray.length();i++){
                                        hashMap = new HashMap<String, String>();

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String PurposeId = jsonObject.getString("PurposeId");
                                        String PurposeName = jsonObject.getString("PurposeName");

                                        hashMap.put("PositionId",PurposeId);
                                        hashMap.put("PositionName",PurposeName);
                                        arldata.add(hashMap);
                                    }
                                    Log.e("Vist Purpose","Arldata "+arldata.toString());
                                    mAdapter = new SaleForceMasterAdapter(getActivity(),arldata,"");
                                    spin_custVisit_Purpose.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                    if(type.equals("SaleActivity")) {
                                        try {
                                            spin_custVisit_Purpose.setSelection(Integer.parseInt(PurposeId));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        Log.e("Size", "ArlData Size = " + arldata.size()+",Purpose Id = "+PurposeId);
//                                        for (int i = 0; i < arldata.size(); i++) {
//                                            int res = Integer.parseInt(PurposeId) - 1;
//                                            if (res == i) {
//                                                spin_custVisit_Purpose.setSelection(arldata.indexOf(PurposeId));
//                                            }
//                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                RetryPolicy policy = new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else{
                Utilities.showDialog(coordinatorLayout,"No Network Found");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void GetAllCustomerDetail(){
        try{
            if(Utilities.isNetworkAvailable(getActivity())){
                try {
                    Custmer_Name = new ArrayList<>();
                    arlDataCustomer = new ArrayList<>();
                    JSONObject param = new JSONObject();

                    param.put("GROUP_ID","1");
                    param.put("USER_ID",empoyeeId);

                    String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetCustomerSalesforcePost";

                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try{
                                        HashMap<String,String> hashMap;
                                        JSONArray jsonArray = response.getJSONArray("GetCustomerSalesforcePostResult");
                                        for (int i=0;i<jsonArray.length();i++){
                                            hashMap = new HashMap<>();

                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String customerId = jsonObject.getString("CustomerId");
                                            String customerName = jsonObject.getString("CustomerName");

                                            hashMap.put("customerId",customerId);
                                            hashMap.put("customerName",customerName);

                                            Custmer_Name.add(customerName);
                                            arlDataCustomer.add(hashMap);
                                        }
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,Custmer_Name);
//                                        av_custVisit_Name.setAdapter(adapter);
                                        Log.e("Customer Data","Data->"+arlDataCustomer.toString());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    int socket = 300000;
                    RetryPolicy policy = new DefaultRetryPolicy(socket,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    jsonObjectRequest.setRetryPolicy(policy);
                    requestQueue.add(jsonObjectRequest);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void SaveCustomerVisit(String customerId,String startDate,String startTime,String endTime,String purpose,String purposeId,String descr){
        try{
            if(Utilities.isNetworkAvailable(getActivity())){

                JSONObject param = new JSONObject();
                param.put("GroupId","1");
                param.put("VisitId","0");
                param.put("CustomerId",customerId);
                param.put("ContactPersonId","0");
                param.put("VisitTitle",purpose);
                param.put("VisitDate",startDate);
                param.put("VisitStartTime",startTime);
                param.put("VisitEndTime",endTime);
                param.put("VisitPurposeId",purposeId);
                param.put("VisitOwnerId","0");
                param.put("VisitAssignedToId","0");
                param.put("VisitStatusId","0");
                param.put("VisitStatusDescription",descr);
                param.put("VisitOutcome","");
                param.put("VisitOutcomeDescription","");
                param.put("USER_ID","3");

                Log.e("Request","Sale Force Visit->"+param.toString());

                String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/SaveCustomerVisitSalesforcePost";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response){
                                Log.e("Response","Save Visit->"+response.toString());
                                try{
                                    String saveNotesResult = response.getString("SaveCustomerVisitSalesforcePostResult");
                                    int res = Integer.valueOf(saveNotesResult);
                                    if (res > 0) {
                                        edt_custVisit_Description.setText("");

                                        spin_custVisit_Purpose.setSelection(0);
                                        btn_custVisit_startDate.setText("");
                                        btn_custVisit_StartTime.setText("");
                                        btn_custVisit_EndTime.setText("");
                                        edt_custVisit_selectedPurpose.setText("");
                                        edt_custVisit_Name.setText("");
//                                        av_custVisit_Name.setAdapter(null);

                                        Utilities.showDialog(coordinatorLayout, "Customer Visit Save");
//                                        GetCustomerNotesDetails();
//                                        mAdapter.notifyDataSetChanged();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        },new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                error.printStackTrace();
                            }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                RetryPolicy policy = new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else{
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
