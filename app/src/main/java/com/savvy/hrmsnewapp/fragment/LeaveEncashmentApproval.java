package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.LeaveEncashmentApprovalAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*import static com.crashlytics.android.core.CrashlyticsCore.TAG;*/

public class LeaveEncashmentApproval extends BaseFragment {
    LeaveEncashmentApprovalAdapter approvalAdapter;
    RecyclerView recyclerView;
    CoordinatorLayout coordinatorLayout;
    CustomTextView dataNotFound;
    ProgressDialog progressDialog;
    SharedPreferences shared;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String employeeId = "";
    LeaveEncashmentApproval.GetLeaveDataAsyn getLeaveDataAsyn;
    ArrayList<HashMap<String, String>> arrayListData;
    CustomTextView txt_tokenNumber;
    Button actionButton;
    String tokenNumber;

    String tg_text = "true", comment = "";
    String xmlData = "";
    String token_no = "";
    String ERFS_REQUEST_ID = "", REQUEST_STATUS_ID = "", LEAVE_ENCASH_STATUS = "", ACTION_LEVEL_SEQUENCE = "", MAX_ACTION_LEVEL_SEQUENCE = "",
            APPROVED_NO_OF_DAYS = "", EMPLOYEE_ID = "", ERFS_REQUEST_FLOW_ID = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayListData = new ArrayList<>();
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        //getLeaveApprovalData(employeeId);
        sendAndRequestResponse(employeeId);
    }

    private void sendAndRequestResponse(String employeeId) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {


                final String GETAPPROVAL_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveEncashmentRequestDetail/" + employeeId;
                Log.d("TAG", "sendAndRequestResponse: " + GETAPPROVAL_URL);
                mRequestQueue = Volley.newRequestQueue(getActivity());
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Please Wait..");
                progressDialog.setCancelable(true);
                progressDialog.show();

                mStringRequest = new StringRequest(Request.Method.GET, GETAPPROVAL_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", "onPostExecute: " + response);
                        HashMap<String, String> map;
                        JSONObject jsonObject;

                        try {
                            progressDialog.dismiss();
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    map = new HashMap<>();

                                    jsonObject = jsonArray.getJSONObject(i);
                                    map.put("TOKEN_NO", jsonObject.getString("TOKEN_NO"));
                                    map.put("EMPLOYEE_CODE", jsonObject.getString("EMPLOYEE_CODE"));
                                    map.put("EMPLOYEE_NAME", jsonObject.getString("EMPLOYEE_NAME"));
                                    map.put("LER_LEAVE_TYPE_ID", jsonObject.getString("LER_LEAVE_TYPE_ID"));
                                    map.put("LER_APPROVED_NO_OF_DAYS", jsonObject.getString("LER_APPROVED_NO_OF_DAYS"));
                                    map.put("LER_NO_OF_DAYS", jsonObject.getString("LER_NO_OF_DAYS"));
                                    map.put("LER_REASON", jsonObject.getString("LER_REASON"));
                                    map.put("ERFS_REQUEST_ID", jsonObject.getString("ERFS_REQUEST_ID"));
                                    map.put("REQUEST_STATUS_ID", jsonObject.getString("REQUEST_STATUS_ID"));
                                    map.put("ERFS_ACTION_LEVEL_SEQUENCE", jsonObject.getString("ERFS_ACTION_LEVEL_SEQUENCE"));
                                    map.put("MAX_ACTION_LEVEL_SEQUENCE", jsonObject.getString("MAX_ACTION_LEVEL_SEQUENCE"));
                                    map.put("LEAVE_ENCASH_STATUS", jsonObject.getString("LEAVE_ENCASH_STATUS"));
                                    map.put("LER_EMPLOYEE_ID", jsonObject.getString("LER_EMPLOYEE_ID"));
                                    map.put("ERFS_REQUEST_FLOW_ID", jsonObject.getString("ERFS_REQUEST_FLOW_ID"));


                                    arrayListData.add(map);
                                }

                                approvalAdapter = new LeaveEncashmentApprovalAdapter(getActivity(), coordinatorLayout, arrayListData);
                                recyclerView.setAdapter(approvalAdapter);
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setAdapter(null);
                                recyclerView.setVisibility(View.INVISIBLE);
                                dataNotFound.setVisibility(View.VISIBLE);
                                Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Utilities.showDialog(coordinatorLayout, Utilities.handleVolleyError(error));
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
                mRequestQueue.add(mStringRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_encashment_approval, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        recyclerView = getActivity().findViewById(R.id.approvalRecyclerView);
        dataNotFound = getActivity().findViewById(R.id.DataNotFound);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVisibility(View.INVISIBLE);

        recyclerView.addOnItemTouchListener(new LeaveApprovalFragment.RecyclerTouchListener(getActivity(), recyclerView, new LeaveApprovalFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final HashMap<String, String> mapdata = arrayListData.get(position);
                actionButton = view.findViewById(R.id.btn_Action);
                txt_tokenNumber = view.findViewById(R.id.tv_tokenNumber);

                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            tokenNumber = txt_tokenNumber.getText().toString();
                            String str = mapdata.get("TOKEN_NO");
                            if (tokenNumber.equals(str)) {

                                ERFS_REQUEST_ID = mapdata.get("ERFS_REQUEST_ID");
                                REQUEST_STATUS_ID = mapdata.get("REQUEST_STATUS_ID");
                                ACTION_LEVEL_SEQUENCE = mapdata.get("ERFS_ACTION_LEVEL_SEQUENCE");
                                MAX_ACTION_LEVEL_SEQUENCE = mapdata.get("MAX_ACTION_LEVEL_SEQUENCE");
                                LEAVE_ENCASH_STATUS = mapdata.get("LEAVE_ENCASH_STATUS");
                                EMPLOYEE_ID = mapdata.get("LER_EMPLOYEE_ID");
                                ERFS_REQUEST_FLOW_ID = mapdata.get("ERFS_REQUEST_FLOW_ID");
                                APPROVED_NO_OF_DAYS = mapdata.get("LER_APPROVED_NO_OF_DAYS");

                                xmlData = ERFS_REQUEST_ID + "," + REQUEST_STATUS_ID + "," + ACTION_LEVEL_SEQUENCE + "," + MAX_ACTION_LEVEL_SEQUENCE + "," +
                                        LEAVE_ENCASH_STATUS + "," + EMPLOYEE_ID + "," + ERFS_REQUEST_FLOW_ID + "," + APPROVED_NO_OF_DAYS;

                                Log.e("XML DATA", xmlData);
                                Log.e("Token No.", str);

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.dialogbox_approval_toggle);
                                dialog.setTitle("Approval");
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                CustomTextView txt_ApprovalToggleTitle, edt_comment_dialog;
                                CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
                                Button btn_ApproveGo, btn_close;
                                btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
                                btn_close = dialog.findViewById(R.id.btn_close);
                                txt_ApprovalToggleTitle = dialog.findViewById(R.id.txt_ApprovalToggleTitle);
                                edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);

                                String str1 = "<font color='#EE0000'>*</font>";
                                txt_ApprovalToggleTitle.setText(Html.fromHtml("Type " + str1));
                                edt_comment_dialog.setText(Html.fromHtml("Comment " + str1));

                                final EditText edt_comment = dialog.findViewById(R.id.edt_approve_comment);
                                final ToggleButton tgText = dialog.findViewById(R.id.tg_approve);

                                tgText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (tgText.isChecked()) {
                                            tg_text = "" + "true";
                                        } else {
                                            tg_text = "" + "false";
                                        }
                                    }
                                });
                                txt_header.setText("Leave Approve/Reject");


                                btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        comment = edt_comment.getText().toString().trim();
                                        if (comment.equals("")) {
                                            Utilities.showDialog(coordinatorLayout, "Please Enter Comment");
                                        } else {
                                            comment = comment.replace(" ", "_").replace("\n", "");
                                            sendLeaveApprovalRequest(employeeId, tg_text, comment, xmlData);
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
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                        }
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void getLeaveApprovalData(String employeeId) {
        getLeaveDataAsyn = new LeaveEncashmentApproval.GetLeaveDataAsyn();
        getLeaveDataAsyn.employeeId = employeeId;
        getLeaveDataAsyn.execute();

    }

    private class GetLeaveDataAsyn extends AsyncTask<String, String, String> {
        String employeeId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Please Wail..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                final String GETAPPROVAL_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveEncashmentRequestDetail/" + employeeId;

                Log.d("TAG", "doInBackground: " + GETAPPROVAL_URL);
                JSONParser jsonParser = new JSONParser();
                String result = jsonParser.makeHttpRequest(GETAPPROVAL_URL, "GET");

                Log.d("TAG", "doInBackground: " + result);
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("TAG", "onPostExecute: " + result);

            HashMap<String, String> map;
            JSONObject jsonObject;

            try {
                JSONArray jsonArray = new JSONArray(result);

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        map = new HashMap<>();

                        jsonObject = jsonArray.getJSONObject(i);
                        map.put("TOKEN_NO", jsonObject.getString("TOKEN_NO"));
                        map.put("EMPLOYEE_CODE", jsonObject.getString("EMPLOYEE_CODE"));
                        map.put("EMPLOYEE_NAME", jsonObject.getString("EMPLOYEE_NAME"));
                        map.put("LER_LEAVE_TYPE_ID", jsonObject.getString("LER_LEAVE_TYPE_ID"));
                        map.put("LER_APPROVED_NO_OF_DAYS", jsonObject.getString("LER_APPROVED_NO_OF_DAYS"));
                        map.put("LER_NO_OF_DAYS", jsonObject.getString("LER_NO_OF_DAYS"));
                        map.put("LER_REASON", jsonObject.getString("LER_REASON"));
                        map.put("ERFS_REQUEST_ID", jsonObject.getString("ERFS_REQUEST_ID"));
                        map.put("REQUEST_STATUS_ID", jsonObject.getString("REQUEST_STATUS_ID"));
                        map.put("ERFS_ACTION_LEVEL_SEQUENCE", jsonObject.getString("ERFS_ACTION_LEVEL_SEQUENCE"));
                        map.put("MAX_ACTION_LEVEL_SEQUENCE", jsonObject.getString("MAX_ACTION_LEVEL_SEQUENCE"));
                        map.put("LEAVE_ENCASH_STATUS", jsonObject.getString("LEAVE_ENCASH_STATUS"));
                        map.put("LER_EMPLOYEE_ID", jsonObject.getString("LER_EMPLOYEE_ID"));
                        map.put("ERFS_REQUEST_FLOW_ID", jsonObject.getString("ERFS_REQUEST_FLOW_ID"));
                        map.put("LER_APPROVED_NO_OF_DAYS", jsonObject.getString("LER_APPROVED_NO_OF_DAYS"));

                        arrayListData.add(map);
                    }

                    approvalAdapter = new LeaveEncashmentApprovalAdapter(getActivity(), coordinatorLayout, arrayListData);
                    recyclerView.setAdapter(approvalAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setAdapter(null);
                    recyclerView.setVisibility(View.INVISIBLE);
                    dataNotFound.setVisibility(View.VISIBLE);
                    Utilities.showDialog(coordinatorLayout, "Data not getting on server side");
                }


            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    public void sendLeaveApprovalRequest(String emp_id, String status, String comment, String xmlData) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ProcessLeaveEncashmentRequest/" + emp_id + "/" + status + "/" + comment + "/" + xmlData;
                Log.e("Url", "Leave Approval Url = " + url);

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.d("TAG", "onResponse: " + response);
                                    int value = Integer.valueOf(response);
                                    if (value > 0) {
                                        Utilities.showDialog(coordinatorLayout, "Leave Encashment Approval Request processed sucessfully.");
                                        sendAndRequestResponse(employeeId);
                                    } else if (value == 0) {
                                        Utilities.showDialog(coordinatorLayout, "Error during processing of Leave Encashment Approval Request.");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "onErrorResponse: " + error.toString());
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
                int socketTime = 30000;
                RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(retryPolicy);
                requestQueue.add(stringRequest);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);

    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        GestureDetector gestureDetector;
        ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LeaveEncashmentApproval.ClickListener clickListener) {
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
}
