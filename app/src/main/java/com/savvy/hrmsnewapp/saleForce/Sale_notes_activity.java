package com.savvy.hrmsnewapp.saleForce;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.SalesActivityNotesContactAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.BaseFragment;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Sale_notes_activity extends BaseFragment {

    RecyclerView recyclerView;
    CustomTextView txt_add_notes;
    SalesActivityNotesContactAdapter mAdapter;
    ArrayList<HashMap<String,String>> arlData;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String empoyeeId = "";

    CoordinatorLayout coordinatorLayout;
    CustomTextView txtDataNotFound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));

        GetCustomerNotesDetails();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale_notes_activity,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txt_add_notes = getActivity().findViewById(R.id.txt_add_notes);
        recyclerView = getActivity().findViewById(R.id.sale_notes_recycler);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arlData = new ArrayList<HashMap<String, String>>();

        mAdapter = new SalesActivityNotesContactAdapter(getActivity(),"NOTES",arlData);
        recyclerView.setAdapter(mAdapter);

//        getDetailSaleValues();
        txt_add_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddNotes();
            }
        });

    }

    public void getAddNotes(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.add_new_notes_row);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Button btn_ApproveGo, btn_close;
        final EditText edt_new_notes = dialog.findViewById(R.id.edt_comment_dialogNotes);
        btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
        btn_close = dialog.findViewById(R.id.btn_close);

        btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edt_new_notes.getText().toString();
                if(comment.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please write something");
                }else{
                    AddNewNotes(comment);
                    dialog.dismiss();
                }
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    public void getDetailSaleValues() {
        arlData = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hashMap;
        for (int i = 0; i < 4; i++) {
            hashMap = new HashMap<String, String>();

            if(i%2==0){
                String createDate = getlongtoago("09-12-2017 17:00:11");
                hashMap.put("LIST_HEAD", "Savvy HRMS is the most comprehensive HRMS and Payroll software solution in the industry, having complete HR automation solution, powered by more than 20 modules.");
                hashMap.put("TIME", createDate);
            }else{
                String createDate1 = getlongtoago("09-12-2017 18:00:11");
                hashMap.put("LIST_HEAD", "HR Services has many functions in the organization, such as payroll processing, employee benefits , compliance, employee orientations and new hire paperwork, employee files, employee handbooks and job descriptions, termination services more .");
                hashMap.put("TIME", createDate1);
            }

            arlData.add(hashMap);

        }

        mAdapter = new SalesActivityNotesContactAdapter(getActivity(),"NOTES",arlData);
        recyclerView.setAdapter(mAdapter);
    }

    public String getlongtoago(String create) {

        Log.e("New Date Method",create);

        // Date Calculation
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.UK);
//        String BackDate = dateFormat.format(create);
        // get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        String currenttime = dateFormat.format(cal.getTime());

        Date CreatedAt = null;
        Date current = null;
        try {
            CreatedAt = dateFormat.parse(create);
            current = dateFormat.parse(currenttime);
            Log.e("New Date Method","Created Date ->"+CreatedAt);
            Log.e("New Date Method","Current Date ->"+current);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = current.getTime() - CreatedAt.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        String time = "";
//        if (diffDays > 0) {
//            if (diffDays == 1) {
//                time = diffDays + " day ago ";
//            } else {
//                time = diffDays + " days ago ";
//            }
//        } else {
            if (diffHours > 0) {
                if (diffHours == 1) {
                    time = diffHours + " hr ago";
                } else {
                    time = create;
                }
            } else {
                if (diffMinutes > 0) {
                    if (diffMinutes == 1) {
                        time = diffMinutes + " min ago";
                    } else {
                        time = diffMinutes + " mins ago";
                    }
                } else {
                    if (diffSeconds > 0) {
                        time = diffSeconds + "secs ago";
                    }
                }
            }

//        }
        return time;
    }

    public void GetCustomerNotesDetails(){
        try{
            if(Utilities.isNetworkAvailable(getActivity())){
                arlData = new ArrayList<HashMap<String, String>>();
                JSONObject param = new JSONObject();
                param.put("GROUP_ID","1");
                param.put("CUSTOMER_ID",Constants.CUSTOMER_ID);

                String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetCustomerNoteSalesforcePost";
                Log.e("Notes Url",""+url);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    JSONArray jsonArray = response.getJSONArray("GetCustomerNoteSalesforcePostResult");

                                    HashMap<String,String>  NotesHashMap;
                                    for(int i=0;i<jsonArray.length();i++){
                                        NotesHashMap = new HashMap<String,String>();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String CreatedBy1 = jsonObject.getString("CreatedBy1");
                                        String CreatedDate1 = jsonObject.getString("CreatedDate1");
                                        String CustomerId = jsonObject.getString("CustomerId");
                                        String GroupId = jsonObject.getString("GroupId");
                                        String ModifiedBy = jsonObject.getString("ModifiedBy");
                                        String ModifiedDate = jsonObject.getString("ModifiedDate");
                                        String NoteDescription = jsonObject.getString("NoteDescription");
                                        String NoteId = jsonObject.getString("NoteId");
                                        String Status = jsonObject.getString("Status");

                                        String newDate = CreatedDate1.replace("/","-");

                                        String createDate = getlongtoago(newDate);
                                        Log.e("Created Date","Hello "+createDate);

                                        NotesHashMap.put("TIME", createDate);
                                        NotesHashMap.put("CreatedBy1",CreatedBy1);
                                        NotesHashMap.put("CreatedDate1",CreatedDate1);
                                        NotesHashMap.put("CustomerId",CustomerId);
                                        NotesHashMap.put("GroupId",GroupId);
                                        NotesHashMap.put("ModifiedBy",ModifiedBy);
                                        NotesHashMap.put("ModifiedDate",ModifiedDate);
                                        NotesHashMap.put("NoteDescription",NoteDescription);
                                        NotesHashMap.put("NoteId",NoteId);
                                        NotesHashMap.put("Status",Status);

                                        arlData.add(NotesHashMap);
                                    }
                                    if(jsonArray.length()>0){
                                        recyclerView.setVisibility(View.VISIBLE);
                                        txtDataNotFound.setVisibility(View.GONE);

                                        mAdapter = new SalesActivityNotesContactAdapter(getActivity(),"NOTES",arlData);
                                        recyclerView.setAdapter(mAdapter);
                                    } else{
                                        recyclerView.setAdapter(null);
                                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                        recyclerView.setVisibility(View.GONE);
                                        txtDataNotFound.setVisibility(View.VISIBLE);
                                    }
//                                    Log.e("Notes Url",""+arlData.toString());
//                                    mAdapter = new SalesActivityNotesContactAdapter(getActivity(),"NOTES",arlData);
//                                    recyclerView.setAdapter(mAdapter);

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

    public void AddNewNotes(String NoteDetails){
        try{
            if(Utilities.isNetworkAvailable(getActivity())){
                JSONObject param = new JSONObject();

                param.put("GROUP_ID","1");
                param.put("NOTE_ID","0");
                param.put("CUSTOMER_ID",Constants.CUSTOMER_ID);
                param.put("NOTE_DESCRIPTION",NoteDetails);
                param.put("USER_ID",empoyeeId);

                String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/SaveCustomerNoteSalesforcePost";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.e("Add Notes","Response ->"+response.toString());
                                    String saveNotesResult = response.getString("SaveCustomerNoteSalesforcePostResult");

                                    int res = Integer.valueOf(saveNotesResult);

                                    if(res>0){
                                        Utilities.showDialog(coordinatorLayout,"Save");
                                        GetCustomerNotesDetails();
//                                        mAdapter.notifyDataSetChanged();
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
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
