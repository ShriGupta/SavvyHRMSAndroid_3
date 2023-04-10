package com.savvy.hrmsnewapp.saleForce;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.util.ArrayList;
import java.util.HashMap;

public class Sale_Contact_Activity extends BaseFragment {

    RecyclerView sale_contact_recycler;
    SalesActivityNotesContactAdapter mAdapter;

    CoordinatorLayout coordinatorLayout;

    ArrayList<HashMap<String,String>> arlData;
    ArrayList<HashMap<String,String>> ContactArlData;
    CustomTextView sale_contact_phone,sale_contact_message;
    CustomTextView txtDataNotFound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arlData = new ArrayList<HashMap<String, String>>();
        ContactArlData = new ArrayList<HashMap<String, String>>();
//        GetCustomerNotesDetails();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale__contact_,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sale_contact_recycler = getActivity().findViewById(R.id.sale_contact_recycler);

        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        sale_contact_recycler.setVisibility(View.VISIBLE);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        sale_contact_recycler.setLayoutManager(mLayoutManager);
        sale_contact_recycler.setItemAnimator(new DefaultItemAnimator());
//
//        mAdapter = new SalesActivityNotesContactAdapter(getActivity(),"CONTACT",arlData);
//        sale_contact_recycler.setAdapter(mAdapter);

//        getDetailSaleValues();
        GetCustomerContactDetail();

        sale_contact_recycler.addOnItemTouchListener(new Sale_Contact_Activity.RecyclerTouchListener(getActivity(),sale_contact_recycler,
                new Sale_Contact_Activity.ClickListener(){

                    @Override
                    public void OnClick(View view, int position) {
                        try{
                            final HashMap<String,String> mapData = ContactArlData.get(position);
                            sale_contact_phone = view.findViewById(R.id.sale_contact_phone);
                            sale_contact_message = view.findViewById(R.id.sale_contact_message);

                            sale_contact_phone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try{
                                        String numberData = mapData.get("ContactMobile1");
                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse("tel:" +numberData));
                                        startActivity(intent);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });

                            sale_contact_message.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try{
                                        final Dialog dialog = new Dialog(getActivity());
                                        dialog.setContentView(R.layout.send_sms_row_box);
                                        dialog.setTitle("SEND SMS");
                                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                        CustomTextView txt_header = dialog.findViewById(R.id.send_sms_header);
                                        Button btn_Send_Sms, btn_Send_Cancel;
                                        btn_Send_Sms = dialog.findViewById(R.id.btn_send_sms_apply);
                                        btn_Send_Cancel = dialog.findViewById(R.id.btn_send_sms_close);

                                        final EditText edt_mobile_number, edt_Message;
                                        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

                                        edt_mobile_number = dialog.findViewById(R.id.edtMobileNumber);
                                        edt_Message = dialog.findViewById(R.id.edt_sms_message);

                                        String numberData = mapData.get("ContactMobile1");
                                        edt_mobile_number.setText(numberData);

                                        btn_Send_Sms.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(edt_mobile_number.getText().toString().trim().equals("")){
                                                    Utilities.showDialog(coordinatorLayout,"Mobile Number is not available");
                                                } else if(edt_Message.getText().toString().trim().equals("")){
                                                    Utilities.showDialog(coordinatorLayout,"Please Write something");
                                                } else {
                                                    String smsPhoneNo = edt_mobile_number.getText().toString().trim();
                                                    String smsMessage = edt_Message.getText().toString().trim();
                                                    sendSMS(smsPhoneNo, smsMessage,dialog);
                                                }
                                            }
                                        });

                                        btn_Send_Cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog.show();
//                                        String numberData = mapData.get("ContactMobile1");
//                                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("sms:"+numberData));
//                                        intent.putExtra("sms_body","");
//                                        startActivity(intent);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void OnLongClick(View view, int position) {

                    }
                }));
    }

    public void sendSMS(String phoneNo, String msg, Dialog dialog) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            dialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getDetailSaleValues() {
        arlData = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hashMap;
        for (int i = 0; i < 4; i++) {
            hashMap = new HashMap<String, String>();
            if(i%2==0) {
                hashMap.put("CUSTOMER_NAME", "Rahul Shukla");
            } else{
                hashMap.put("CUSTOMER_NAME", "Krishna Gupta");
            }
            arlData.add(hashMap);

        }

        mAdapter = new SalesActivityNotesContactAdapter(getActivity(),"CONTACT",arlData);
        sale_contact_recycler.setAdapter(mAdapter);
    }

    public void GetCustomerContactDetail(){
        try{
            ContactArlData = new ArrayList<HashMap<String, String>>();
            JSONObject param = new JSONObject();

            param.put("GROUP_ID","1");
            param.put("CUSTOMER_ID",Constants.CUSTOMER_ID);

            String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetCustomerContactSalesforcePost";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("GetCustomerContactSalesforcePostResult");

                                HashMap<String,String> contactHashMap;
                                for(int i = 0;i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    contactHashMap = new HashMap<String,String>();

                                    String ContactCoordinator = jsonObject.getString("ContactCoordinator");
                                    String ContactDOB = jsonObject.getString("ContactDOB");
                                    String ContactDesignation = jsonObject.getString("ContactDesignation");
                                    String ContactEmail1 = jsonObject.getString("ContactEmail1");
                                    String ContactEmail2 = jsonObject.getString("ContactEmail2");
                                    String ContactFirstName = jsonObject.getString("ContactFirstName");
                                    String ContactId = jsonObject.getString("ContactId");
                                    String ContactLastName = jsonObject.getString("ContactLastName");
                                    String ContactMiddleName = jsonObject.getString("ContactMiddleName");
                                    String ContactMobile1 = jsonObject.getString("ContactMobile1");
                                    String ContactMobile2 = jsonObject.getString("ContactMobile2");
                                    String ContactName = jsonObject.getString("ContactName");
                                    String ContactPhone1 = jsonObject.getString("ContactPhone1");
                                    String ContactPhone2 = jsonObject.getString("ContactPhone2");
                                    String ContactReportTo = jsonObject.getString("ContactReportTo");
                                    String ContactStatus = jsonObject.getString("ContactStatus");
                                    String CreatedBy = jsonObject.getString("CreatedBy");
                                    String CreatedDate = jsonObject.getString("CreatedDate");
                                    String CustomerId = jsonObject.getString("CustomerId");
                                    String GroupId = jsonObject.getString("GroupId");
                                    String ModifiedBy = jsonObject.getString("ModifiedBy");
                                    String ModifiedDate = jsonObject.getString("ModifiedDate");
                                    String NoteDescription = jsonObject.getString("NoteDescription");

                                    contactHashMap.put("ContactCoordinator",ContactCoordinator);
                                    contactHashMap.put("ContactDOB",ContactDOB);
                                    contactHashMap.put("ContactDesignation",ContactDesignation);
                                    contactHashMap.put("ContactEmail1",ContactEmail1);
                                    contactHashMap.put("ContactEmail2",ContactEmail2);
                                    contactHashMap.put("ContactFirstName",ContactFirstName);
                                    contactHashMap.put("ContactId",ContactId);
                                    contactHashMap.put("ContactLastName",ContactLastName);
                                    contactHashMap.put("ContactMiddleName",ContactMiddleName);
                                    contactHashMap.put("ContactMobile1",ContactMobile1);
                                    contactHashMap.put("ContactMobile2",ContactMobile2);
                                    contactHashMap.put("ContactName",ContactName);
                                    contactHashMap.put("ContactPhone1",ContactPhone1);
                                    contactHashMap.put("ContactPhone2",ContactPhone2);
                                    contactHashMap.put("ContactReportTo",ContactReportTo);
                                    contactHashMap.put("ContactStatus",ContactStatus);
                                    contactHashMap.put("CreatedBy",CreatedBy);
                                    contactHashMap.put("CreatedDate",CreatedDate);
                                    contactHashMap.put("CustomerId",CustomerId);
                                    contactHashMap.put("GroupId",GroupId);
                                    contactHashMap.put("ModifiedBy",ModifiedBy);
                                    contactHashMap.put("ModifiedDate",ModifiedDate);
                                    contactHashMap.put("NoteDescription",NoteDescription);

                                    ContactArlData.add(contactHashMap);
                                }

                                Log.e("Response Contact", "" + ContactArlData.toString());
                                if(jsonArray.length()>0){
                                    sale_contact_recycler.setVisibility(View.VISIBLE);
                                    txtDataNotFound.setVisibility(View.GONE);

                                    mAdapter = new SalesActivityNotesContactAdapter(getActivity(),"CONTACT",ContactArlData);
                                    sale_contact_recycler.setAdapter(mAdapter);
                                } else{
                                    sale_contact_recycler.setAdapter(null);
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                    sale_contact_recycler.setVisibility(View.GONE);
                                    txtDataNotFound.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        GestureDetector gestureDetector;
        Sale_Contact_Activity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final Sale_Contact_Activity.ClickListener clickListener){
            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e){
                    View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                    if(child != null && clickListener != null){
                        clickListener.OnClick(child,recyclerView.getChildPosition(child));
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e){
                    View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                    if(child != null && clickListener != null){
                        clickListener.OnClick(child,recyclerView.getChildPosition(child));
                    }
                }
            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(),e.getY());
            if(child != null && clickListener != null && gestureDetector.onTouchEvent(e)){
                clickListener.OnClick(child,rv.getChildPosition(child));
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
