package com.savvy.hrmsnewapp.saleForce;

import android.content.Context;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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

public class sale_activity extends BaseFragment {

    RecyclerView recyclerView;
    ArrayList<HashMap<String, String>> arlData;
    SalesActivityNotesContactAdapter mAdapter;
    CoordinatorLayout coordinatorLayout;
    CustomTextView sale_activity_time;
    CustomTextView txtDataNotFound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getVisitDetail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale_activity,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        recyclerView = getActivity().findViewById(R.id.sale_activity_recycler);
        txtDataNotFound = getActivity().findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new SalesActivityNotesContactAdapter(getActivity(), "ACTIVITY", arlData);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new sale_activity.RecyclerTouchListener(getActivity(), recyclerView,
                new sale_activity.ClickListener() {
            @Override
            public void OnClick(View view, final int position) {
                sale_activity_time = view.findViewById(R.id.sale_activity_time);
                try{
                    sale_activity_time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try{
                                HashMap<String,String> hashMap = arlData.get(position);
                                String CustomerName = hashMap.get("CustomerName");
                                String CustomerId = hashMap.get("CustomerId");
                                String PurposeName = hashMap.get("Purpose");
                                String PurposeId = hashMap.get("PurposeId");
                                String StartDate = hashMap.get("Date1");
                                String StartTime = hashMap.get("StartTime1");
                                String EndTime = hashMap.get("EndTime1");
                                String Description = hashMap.get("Description");

                                Bundle bundle = new Bundle();

                                bundle.putString("Type","SaleActivity");
                                bundle.putString("CustomerName",CustomerName);
                                bundle.putString("CustomerId",CustomerId);
                                bundle.putString("PurposeName",PurposeName);
                                bundle.putString("PurposeId",PurposeId);
                                bundle.putString("StartDate",StartDate);
                                bundle.putString("StartTime",StartTime);
                                bundle.putString("EndTime",EndTime);
                                bundle.putString("Description",Description);

                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                SaleForce_Visit saleForceVisit = new SaleForce_Visit();
                                saleForceVisit.setArguments(bundle);
                                transaction.replace(R.id.container_body, saleForceVisit);
                                transaction.addToBackStack(null);
                                transaction.commit();
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
        }
        ));

//        getDetailSaleValues();

    }

    public void getDetailSaleValues() {
        arlData = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hashMap;
        for (int i = 0; i < 4; i++) {
            hashMap = new HashMap<String, String>();

            if(i%2==0){
                String createDate = getlongtoago("09-12-2017 17:00:11");
                hashMap.put("CUSTOMER_NAME", "Nitesh Kumar Verma");
                hashMap.put("LOCATION", "#F-17, Maohan Baba Nagar, Badarpur, New Delhi, India, 110044");
                hashMap.put("TIME", createDate);
            }else{
                String createDate1 = getlongtoago("09-12-2017 18:00:11");
                hashMap.put("CUSTOMER_NAME", "C.S. Verma");
                hashMap.put("LOCATION", "C41, Ganesh Nagar, Pandav Nagar Complex, Mother Dairy,New Delhi");
                hashMap.put("TIME", createDate1);
            }

            arlData.add(hashMap);

        }

        mAdapter = new SalesActivityNotesContactAdapter(getActivity(),"ACTIVITY",arlData);
        recyclerView.setAdapter(mAdapter);
    }


    public String getlongtoago(String create) {

        Log.e("New Date Method",create);

        // Date Calculation
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
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
        if (diffDays > 0) {
            if (diffDays == 1) {
                time = diffDays + " day ago ";
            } else {
                time = diffDays + " days ago ";
            }
        } else
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

    public void getVisitDetail(){
        try{
            if(Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<HashMap<String, String>>();
                JSONObject param = new JSONObject();

                param.put("GROUP_ID", "1");
                param.put("CUSTOMER_ID", Constants.CUSTOMER_ID);

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCustomerVisitSalesforcePost";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("GetCustomerVisitSalesforcePostResult");

                                    HashMap<String, String> visitHashMap;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        visitHashMap = new HashMap<String, String>();

                                        String AssigneToId = jsonObject.getString("AssigneToId");
                                        String AssigneToName = jsonObject.getString("AssigneToName");
                                        String ContactId = jsonObject.getString("ContactId");
                                        String ContactName = jsonObject.getString("ContactName");
                                        String CreatedBy = jsonObject.getString("CreatedBy");
                                        String CreatedDate = jsonObject.getString("CreatedDate");
                                        String CustomerId = jsonObject.getString("CustomerId");
                                        String CustomerName = jsonObject.getString("CustomerName");
                                        String Date = jsonObject.getString("Date");
                                        String Date1 = jsonObject.getString("Date1");
                                        String Description = jsonObject.getString("Description");
                                        String EndTime = jsonObject.getString("EndTime");
                                        String EndTime1 = jsonObject.getString("EndTime1");
                                        String GoodBadFlag = jsonObject.getString("GoodBadFlag");
                                        String GroupId = jsonObject.getString("GroupId");
                                        String ModifiedBy = jsonObject.getString("ModifiedBy");
                                        String ModifiedDate = jsonObject.getString("ModifiedDate");
                                        String Outcome = jsonObject.getString("Outcome");
                                        String OutcomeDescription = jsonObject.getString("OutcomeDescription");
                                        String OwnerId = jsonObject.getString("OwnerId");
                                        String OwnerName = jsonObject.getString("OwnerName");
                                        String Purpose = jsonObject.getString("Purpose");
                                        String PurposeId = jsonObject.getString("PurposeId");
                                        String StartTime = jsonObject.getString("StartTime");
                                        String StartTime1 = jsonObject.getString("StartTime1");
                                        String Status = jsonObject.getString("Status");
                                        String Title = jsonObject.getString("Title");
                                        String VisitId = jsonObject.getString("VisitId");
                                        String VisitStatusId = jsonObject.getString("VisitStatusId");
                                        String VisitStatusName = jsonObject.getString("VisitStatusName");

                                        String newDate = Date1.replace("/", "-");
                                        String createDate = getlongtoago(newDate);

                                        visitHashMap.put("TIME", createDate);

                                        visitHashMap.put("AssigneToId", AssigneToId);
                                        visitHashMap.put("AssigneToName", AssigneToName);
                                        visitHashMap.put("ContactId", ContactId);
                                        visitHashMap.put("ContactName", ContactName);
                                        visitHashMap.put("CreatedBy", CreatedBy);
                                        visitHashMap.put("CreatedDate", CreatedDate);
                                        visitHashMap.put("CustomerId", CustomerId);
                                        visitHashMap.put("CustomerName", CustomerName);
                                        visitHashMap.put("Date", Date);
                                        visitHashMap.put("Date1", Date1);
                                        visitHashMap.put("Description", Description);
                                        visitHashMap.put("EndTime", EndTime);
                                        visitHashMap.put("EndTime1", EndTime1);
                                        visitHashMap.put("GoodBadFlag", GoodBadFlag);
                                        visitHashMap.put("GroupId", GroupId);
                                        visitHashMap.put("ModifiedBy", ModifiedBy);
                                        visitHashMap.put("ModifiedDate", ModifiedDate);
                                        visitHashMap.put("Outcome", Outcome);
                                        visitHashMap.put("OutcomeDescription", OutcomeDescription);
                                        visitHashMap.put("OwnerId", OwnerId);
                                        visitHashMap.put("OwnerName", OwnerName);
                                        visitHashMap.put("Purpose", Purpose);
                                        visitHashMap.put("PurposeId", PurposeId);
                                        visitHashMap.put("StartTime", StartTime);
                                        visitHashMap.put("StartTime1", StartTime1);
                                        visitHashMap.put("Status", Status);
                                        visitHashMap.put("Title", Title);
                                        visitHashMap.put("VisitId", VisitId);
                                        visitHashMap.put("VisitStatusId", VisitStatusId);
                                        visitHashMap.put("VisitStatusName", VisitStatusName);

                                        arlData.add(visitHashMap);
                                    }
                                    if(jsonArray.length()>0){
                                        recyclerView.setVisibility(View.VISIBLE);
                                        txtDataNotFound.setVisibility(View.GONE);

                                        mAdapter = new SalesActivityNotesContactAdapter(getActivity(), "ACTIVITY", arlData);
                                        recyclerView.setAdapter(mAdapter);
                                    } else{
                                        recyclerView.setAdapter(null);
                                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                                        recyclerView.setVisibility(View.GONE);
                                        txtDataNotFound.setVisibility(View.VISIBLE);
                                    }

                                } catch (Exception e) {
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

    public interface ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        GestureDetector gestureDetector;
        sale_activity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final sale_activity.ClickListener clickListener){
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
