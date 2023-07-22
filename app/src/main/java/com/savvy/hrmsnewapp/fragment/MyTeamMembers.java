package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.savvy.hrmsnewapp.activity.EmployeeDetailActivity;
import com.savvy.hrmsnewapp.adapter.EmployeeDynamicProfileAdapter;
import com.savvy.hrmsnewapp.adapter.MyTeamMembersAdapter;
import com.savvy.hrmsnewapp.adapter.TeamMembersDemoAdapter;
import com.savvy.hrmsnewapp.customwidgets.CircularImageView;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MyTeamMembers extends BaseFragment {
    RecyclerView recyclerView;
    String json = "";
    ArrayList<HashMap<String, String>> arlData;
    public static int count = 0;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    SharedPreferences shared_1;
    String EmployeeCode = "", employeeName = "", department = "", empoyeeId = "",
            designation = "", photocode = "";
    TeamMembersDemoAdapter adapter;
    MyTeamMembers.TeamMemberAsynTask teamMemberAsynTask;
    String TAG = "savvylogs";
    HashMap<String, String> hashMap;
    HashMap<String, String> hashMap_1;

    LinearLayout main_layout_heirachy;
    CircularImageView user_profile_photo;
    CustomTextView txt_empName_1, txt_empCode, txt_department, txt_designation, txt_BackButton, txt_home_button;
    String bundle_employeeId = "", bundle_employeeCode = "", bundle_photoCode = "",
            bundle_employeeName = "", bundle_department = "", bundle_designation = "", bundle_branch = "";

    String EMPLOYEE_NAME_1 = "", EMPLOYEE_ID_1 = "", EMPLOYEE_CODE_1 = "", PHOTO_CODE = "", DEPARTMENT = "", DESIGNATION = "", BRANCH = "";

    int listTouchCount=0;

    String EMP_PIC_URL="";
    ArrayList<HashMap<String, String>> dynamicArraylist;
    EmployeeDynamicProfileAdapter profileAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        shared_1 = getActivity().getSharedPreferences("PROFILE_SESSION", MODE_PRIVATE);
        arlData = new ArrayList<>();
        dynamicArraylist=new ArrayList<>();

//        getTeamRequestStatus();
    }

    private void getTeamRequestStatus() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                arlData = new ArrayList<HashMap<String, String>>();
                teamMemberAsynTask = new MyTeamMembers.TeamMemberAsynTask();
                teamMemberAsynTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_team_members_1, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listTouchCount=0;
        user_profile_photo = getActivity().findViewById(R.id.img_myHeirarchyTitle);

        txt_empName_1 = getActivity().findViewById(R.id.txt_MyEmployeeName);
        txt_empCode = getActivity().findViewById(R.id.txt_Mydetail);
        txt_department = getActivity().findViewById(R.id.txt_MymarginDetail);
        txt_designation = getActivity().findViewById(R.id.txt_MySupervisor);
        txt_BackButton = getActivity().findViewById(R.id.txt_BackButton);
        txt_home_button = getActivity().findViewById(R.id.txt_home_button);

        main_layout_heirachy = getActivity().findViewById(R.id.main_layout_heirachy);
        recyclerView = getActivity().findViewById(R.id.recycler_my_team_members);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Constants.MY_TEAM_MEMBERS == 0) {
            main_layout_heirachy.setVisibility(View.GONE);
            EmployeeCode = (shared_1.getString("EmployeeCode", ""));
            empoyeeId = (shared_1.getString("EmpoyeeId", ""));
            employeeName = (shared_1.getString("EmployeeName", ""));
            department = (shared_1.getString("department", ""));
            designation = (shared_1.getString("designation", ""));
            photocode = (shared_1.getString("EmpPhotoPath", ""));
            hashMap = new HashMap<>();

            hashMap.put("EmployeeName", employeeName);
            hashMap.put("EmployeeCode", EmployeeCode);
            hashMap.put("Designation", designation);
            hashMap.put("Department", department);
            hashMap.put("empoyeeId", empoyeeId);
            hashMap.put("photocode", photocode);
            Constants.My_Team_Members.add(hashMap);
        } else {
            main_layout_heirachy.setVisibility(View.VISIBLE);
            try {
                if (getArguments() != null) {
                    bundle_employeeId = getArguments().getString("employeeId");
                    bundle_employeeCode = getArguments().getString("employeeCode");
                    bundle_photoCode = getArguments().getString("photoCode");
                    bundle_employeeName = getArguments().getString("employeeName");
                    bundle_department = getArguments().getString("department");
                    bundle_designation = getArguments().getString("designation");
                    bundle_branch = getArguments().getString("branch");


                    hashMap = new HashMap<>();
                    hashMap.put("EmployeeName", bundle_employeeName);
                    hashMap.put("EmployeeCode", bundle_employeeCode);
                    hashMap.put("Designation", bundle_designation);
                    hashMap.put("Department", bundle_department);
                    hashMap.put("empoyeeId", bundle_employeeId);
                    hashMap.put("photocode", bundle_photoCode);

                    Constants.My_Team_Members.add(hashMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Constants.My_Team_Members.size() > 0) {
            hashMap_1 = Constants.My_Team_Members.get(Constants.My_Team_Members.size() - 1);
            EMPLOYEE_ID_1 = hashMap_1.get("empoyeeId");
            EMPLOYEE_CODE_1 = hashMap_1.get("EmployeeCode");
            EMPLOYEE_NAME_1 = hashMap_1.get("EmployeeName");
            PHOTO_CODE = hashMap_1.get("photocode");
            DEPARTMENT = hashMap_1.get("Department");
            DESIGNATION = hashMap_1.get("Designation");
        }

        txt_empName_1.setText(EMPLOYEE_NAME_1);
        txt_empCode.setText(EMPLOYEE_CODE_1);
        txt_department.setText(DEPARTMENT);
        txt_designation.setText(DESIGNATION);

        if (PHOTO_CODE.equals("")) {
            user_profile_photo.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.get().load(PHOTO_CODE).into(user_profile_photo);
        }

        getMyTeamHeirarchy(EMPLOYEE_ID_1);

        txt_BackButton.setOnClickListener(v -> {
            try {
                Log.e("Size 4", "Count = " + Constants.MY_TEAM_MEMBERS + "  Size = " + Constants.My_Team_Members.size() + ", " + Constants.My_Team_Members.toString());
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                MyTeamMembers myTeamMembers = new MyTeamMembers();
                transaction.replace(R.id.container_body, myTeamMembers);
                transaction.commit();
                Constants.MY_TEAM_MEMBERS = Constants.MY_TEAM_MEMBERS - 1;
                if (Constants.My_Team_Members.size() > 0) {
                    Constants.My_Team_Members.remove(Constants.My_Team_Members.size() - 1);
                }
                Log.e("Size 5", "Count = " + Constants.MY_TEAM_MEMBERS + "  Size = " + Constants.My_Team_Members.size() + ", " + Constants.My_Team_Members.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        txt_home_button.setOnClickListener(v -> {
            try {
                Log.e("Size 4", "Count = " + Constants.MY_TEAM_MEMBERS + "  Size = " + Constants.My_Team_Members.size() + ", " + Constants.My_Team_Members.toString());
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                MyTeamMembers myTeamMembers = new MyTeamMembers();
                transaction.replace(R.id.container_body, myTeamMembers);
                transaction.commit();
                Constants.MY_TEAM_MEMBERS = 0;
                if (Constants.My_Team_Members.size() > 0) {
                    Constants.My_Team_Members.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        adapter = new TeamMembersDemoAdapter(getActivity(), arlData);

        recyclerView.addOnItemTouchListener(new MyTeamMembers.RecyclerTouchListener(getActivity(), recyclerView, new MyTeamMembers.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if(listTouchCount>1)
                    listTouchCount=0;

                    CustomTextView txtDepth = view.findViewById(R.id.txtDepth);
                    final int pos = position;

                    String depth = txtDepth.getText().toString().trim();

                    Log.e("Depth", depth);

                    if (!depth.equals("0")) {
                        txtDepth.setOnClickListener(v -> {
                            try {
                                final HashMap<String, String> mapdata = arlData.get(pos);
                                String employeeId = mapdata.get("employeeId");
                                String employeeCode = mapdata.get("employeeCode");
                                String photoCode = mapdata.get("photoCode");
                                String employeeName = mapdata.get("employeeName");
                                String department = mapdata.get("department");
                                String designation = mapdata.get("designation");
                                String branch = mapdata.get("branch");

                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                MyTeamMembers myTeamMembers = new MyTeamMembers();

                                Bundle args = new Bundle();
                                args.putString("employeeId", employeeId);
                                args.putString("employeeCode", employeeCode);
                                args.putString("photoCode", photoCode);
                                args.putString("employeeName", employeeName);
                                args.putString("department", department);
                                args.putString("designation", designation);
                                args.putString("branch", branch);
                                myTeamMembers.setArguments(args);
                                transaction.replace(R.id.container_body, myTeamMembers);
                                transaction.commit();
                                Constants.MY_TEAM_MEMBERS++;


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }else {
                        if(listTouchCount==0){
                            final HashMap<String, String> mapdata = arlData.get(pos);
                            String employeeId = mapdata.get("employeeId");
                            EMP_PIC_URL = mapdata.get("photoCode");
                            dynamicArraylist.clear();
                            getEmployeeDetail(employeeId);
                        }
                        listTouchCount++;
                    }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void getMyTeamHeirarchy(String EMPLOYEE_ID_1) {
        try {
            arlData = new ArrayList<>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/MyHierarchyPost";
            JSONObject params_final = new JSONObject();
            if(EMPLOYEE_ID_1.equals("")){
                EMPLOYEE_ID_1=shared.getString("EmpoyeeId","");
            }
            params_final.put("employeeId", EMPLOYEE_ID_1);
            params_final.put("securityToken", shared.getString("Token",""));
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    response -> {

                        try {
                            JSONArray jsonArray = response.getJSONArray("MyHierarchyPostResult");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> mapData = new HashMap<String, String>();

                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String employeeId = explrObject.getString("employeeId");
                                String employeeCode = explrObject.getString("employeeCode");
                                String photoCode = explrObject.getString("photoCode");
                                String employeeName = explrObject.getString("employeeName");
                                String branch = explrObject.getString("branch");
                                String department = explrObject.getString("department");
                                String designation = explrObject.getString("designation");
                                String supervisorIdFirst = explrObject.getString("supervisorIdFirst");
                                String depth = explrObject.getString("depth");
                                String errorMessage = explrObject.getString("errorMessage");

                                mapData.put("employeeId", employeeId);
                                mapData.put("employeeCode", employeeCode);
                                mapData.put("photoCode", photoCode);
                                mapData.put("employeeName", employeeName);
                                mapData.put("branch", branch);
                                mapData.put("department", department);
                                mapData.put("designation", designation);
                                mapData.put("supervisorIdFirst", supervisorIdFirst);
                                mapData.put("depth", depth);
                                mapData.put("errorMessage", errorMessage);

                                arlData.add(mapData);
                            }
                            adapter = new TeamMembersDemoAdapter(getActivity(), arlData);
                            recyclerView.setAdapter(adapter);
                            Log.e("Value of Con Type", arlData.toString());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.e("Error In", "" + ex.getMessage());
                        }

                    }, error -> {
                        error.printStackTrace();
                        Log.d("Error", "" + error.getMessage());
                    }) {
                @Override
                public Map<String, String> getHeaders() {
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

    private class TeamMemberAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid, fromdate, todate, spnid;

        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            try {
                // Things to be done before execution of long running operation. For
                // example showing ProgessDialog
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                Log.e("From Date ", "" + fromdate);
                Log.e("To Date ", "" + todate);
                Log.e("Spinner Value ", "" + spnid);

                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                final String ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetPunchRequestStatus/3/-/-/0,1,2,3,4,5,6";

                System.out.println("ATTENDANCE_URL====" + ATTENDANCE_URL);
//                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);

                JSONParser jParser = new JSONParser(getActivity());
                json = jParser.makeHttpRequest(ATTENDANCE_URL, "GET");

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
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        HashMap<String, String> punchStatusmap;
                        JSONArray jsonArray = new JSONArray(result);
//                        System.out.println("jsonArray===" + jsonArray);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                punchStatusmap = new HashMap<String, String>();
                                JSONObject explrObject = jsonArray.getJSONObject(i);

                                String emp_name = explrObject.getString("EMPLOYEE_NAME");
                                String emp_code = explrObject.getString("EMPLOYEE_CODE");
                                String designation = explrObject.getString("D_DESIGNATION");

                                punchStatusmap.put("EMPLOYEE_NAME", emp_name);
                                punchStatusmap.put("EMPLOYEE_CODE", emp_code);
                                punchStatusmap.put("D_DESIGNATION", designation);

                                arlData.add(punchStatusmap);
                            }

                            //Display Holiday List(arlData);
                            Log.e(TAG, "ArrayList" + arlData);
//                            mAdapter = new MyTeamMembersAdapter(getActivity(), arlData);
                            adapter = new TeamMembersDemoAdapter(getActivity(), arlData);
                            Log.e(TAG, "ArrayList - 1" + arlData);
                            recyclerView.setAdapter(adapter);
                            Log.e(TAG, "ArrayList - 2");
//                            recyclerView.setAdapter(null);
//                            Utilities.showDialog(coordinatorLayout, "Punch Status Accuracy");
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MyTeamMembers.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MyTeamMembers.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onClick(child, recyclerView.getChildPosition(child));
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onClick(child, recyclerView.getChildPosition(child));
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

    private void getEmployeeDetail(String emp_id) {
        if (Utilities.isNetworkAvailable(requireActivity())) {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/EmployeeDetailsPostDynamic";
            JSONObject params_final = new JSONObject();

            try {
                params_final.put("employeeId", emp_id);
                params_final.put("securityToken", shared.getString("Token", ""));
            } catch (JSONException e) {
                Log.e("TAG", "getEmployeeDetail: ",e );
            }
            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    response -> {
                        HashMap<String, String> hashMap;
                        try {
                            JSONArray jarray = response.getJSONArray("EmployeeDetailsPostDynamicResult");

                            if (jarray.length() > 0) {
                                for (int i = 0; i < jarray.length(); i++) {
                                    hashMap = new HashMap<>();
                                    hashMap.put("CaptionKey", jarray.getJSONObject(i).getString("CaptionName"));
                                    hashMap.put("CaptionValue", jarray.getJSONObject(i).getString("CaptionValue"));
                                    dynamicArraylist.add(hashMap);
                                }
                                showEmpDetailDialog(dynamicArraylist);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.e("Error In", "" + ex.getMessage());
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d("Error", "" + error.getMessage());
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
        }

    }

    private void showEmpDetailDialog(ArrayList<HashMap<String, String>> dynamicArraylist){

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.emp_detail_dialog, null, false);
        RecyclerView list=dialogView.findViewById(R.id.rv_emp_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));
        ImageView imageview=dialogView.findViewById(R.id.iv_profile);
        if (EMP_PIC_URL.equals("")) {
            imageview.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.get().load(EMP_PIC_URL).error(R.drawable.profile_image).into(imageview);
        }
        profileAdapter = new EmployeeDynamicProfileAdapter(requireActivity(), dynamicArraylist);
        list.setAdapter(profileAdapter);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

}
