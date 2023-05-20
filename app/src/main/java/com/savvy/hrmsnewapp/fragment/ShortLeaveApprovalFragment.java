package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.ShortLeaveApprovalAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;
import static com.savvy.hrmsnewapp.fragment.TravelRequestFicciFragment.MY_PREFS_NAME;

public class ShortLeaveApprovalFragment extends BaseFragment implements ItemClickListener {
    ShortLeaveApprovalAdapter shortLeaveApprovalAdapter;
    RecyclerView rvShortLeaveRecyclerview;
    TextView tvNoData;
    SharedPreferences sharedPreferences;
    String empId;
    List<HashMap<String, String>> arrayList = new ArrayList<>();
    String approvalStatus = "true";
    String comment = "";
    String xmlString;
    CoordinatorLayout coordinatorLayout;
    Dialog approvalDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empId = (sharedPreferences.getString("EmpoyeeId", ""));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_short_leave_approval, container, false);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        rvShortLeaveRecyclerview = view.findViewById(R.id.rv_short_leave_approval);
        rvShortLeaveRecyclerview.setLayoutManager(new LinearLayoutManager(requireActivity()));
        tvNoData = view.findViewById(R.id.tv_no_data);
        shortLeaveApprovalAdapter = new ShortLeaveApprovalAdapter(requireContext(), this);
        rvShortLeaveRecyclerview.setAdapter(shortLeaveApprovalAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getShortLeaveData();
    }

    private void getShortLeaveData() {
        showProgressDialog();
        arrayList.clear();
        shortLeaveApprovalAdapter.clearItems();
        if (Utilities.isNetworkAvailable(requireActivity())) {
            String slr_url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetShortLeaveChangeRequestDetail/" + empId;
            JsonArrayRequest request = new JsonArrayRequest(slr_url, response -> {
                dismissProgressDialog();
                Log.e("TAG","response"+response);
                HashMap<String, String> hashMap;
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        hashMap = new HashMap<>();
                        try {
                            hashMap.put("EMPLOYEE_CODE", response.getJSONObject(i).getString("EMPLOYEE_CODE"));
                            hashMap.put("REQUESTED_DATE", response.getJSONObject(i).getString("REQUESTED_DATE"));
                            hashMap.put("TYPE", response.getJSONObject(i).getString("TYPE"));
                            hashMap.put("R_TYPE", response.getJSONObject(i).getString("R_TYPE"));
                            hashMap.put("EMPLOYEE_NAME", response.getJSONObject(i).getString("EMPLOYEE_NAME"));
                            hashMap.put("SLR_DATE", response.getJSONObject(i).getString("SLR_DATE"));
                            hashMap.put("TOKEN_NO", response.getJSONObject(i).getString("TOKEN_NO"));
                            hashMap.put("SLR_REASON", response.getJSONObject(i).getString("SLR_REASON"));

                            hashMap.put("ERFS_REQUEST_ID", response.getJSONObject(i).getString("ERFS_REQUEST_ID"));
                            hashMap.put("REQUEST_STATUS_ID", response.getJSONObject(i).getString("REQUEST_STATUS_ID"));
                            hashMap.put("ERFS_ACTION_LEVEL_SEQUENCE", response.getJSONObject(i).getString("ERFS_ACTION_LEVEL_SEQUENCE"));
                            hashMap.put("MAX_ACTION_LEVEL_SEQUENCE", response.getJSONObject(i).getString("MAX_ACTION_LEVEL_SEQUENCE"));
                            hashMap.put("SLR_STATUS", response.getJSONObject(i).getString("SLR_STATUS"));
                            hashMap.put("SLR_EMPLOYEE_ID", response.getJSONObject(i).getString("SLR_EMPLOYEE_ID"));
                            hashMap.put("ERFS_REQUEST_FLOW_ID", response.getJSONObject(i).getString("ERFS_REQUEST_FLOW_ID"));


                            arrayList.add(hashMap);
                        } catch (JSONException e) {
                            dismissProgressDialog();
                            e.printStackTrace();
                            Log.e("ShortLeave", "getShortLeaveData: "+e.getMessage() );
                        }
                    }
                    shortLeaveApprovalAdapter.addItems(arrayList);
                } else {
                    rvShortLeaveRecyclerview.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }
            }, error -> {
                dismissProgressDialog();
                Log.e("ShortLeave", "getShortLeaveData: "+error );
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
                    return params;
                }


                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
        } else {
            Toast.makeText(requireActivity(), "No Internet", Toast.LENGTH_SHORT).show();
        }

       /* APIServiceClass.getInstance().getSLApprovalData(sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""),empId, new ResultHandler<ServerDateTimeModel>() {
            @Override
            public void onSuccess(ServerDateTimeModel data) {
                Log.e("TAG", "onSuccess: " + data);
            }

            @Override
            public void onFailure(String message) {
                Log.e("TAG", "onFailure: "+message);
            }
        });*/
    }

    @Override
    public void onClickItem(int position, String data) {

        xmlString = arrayList.get(position).get("ERFS_REQUEST_ID") + "," +
                arrayList.get(position).get("REQUEST_STATUS_ID") + "," +
                arrayList.get(position).get("ERFS_ACTION_LEVEL_SEQUENCE") + "," +
                arrayList.get(position).get("MAX_ACTION_LEVEL_SEQUENCE") + "," +
                arrayList.get(position).get("SLR_STATUS") + "," +
                arrayList.get(position).get("SLR_EMPLOYEE_ID") + "," +
                arrayList.get(position).get("ERFS_REQUEST_FLOW_ID");
        showApprovalDialod();
    }

    private void showApprovalDialod() {
        approvalDialog = new Dialog(requireActivity());
        approvalDialog.setContentView(R.layout.dialogbox_leave_approval);
        approvalDialog.setTitle("Approval");
        Objects.requireNonNull(approvalDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        CustomTextView txt_header = approvalDialog.findViewById(R.id.dialog_header);
        Button btn_ApproveGo, btn_close;

        btn_ApproveGo = approvalDialog.findViewById(R.id.btn_apply);
        btn_close = approvalDialog.findViewById(R.id.btn_close);

        CustomTextView txt_ApprovalToggleTitle, edt_comment_dialog, txt_reason_dialog;
        txt_ApprovalToggleTitle = approvalDialog.findViewById(R.id.txt_ApprovalToggleTitle);
        edt_comment_dialog = approvalDialog.findViewById(R.id.edt_comment_dialog);
        txt_reason_dialog = approvalDialog.findViewById(R.id.txt_reason_dialog);

        String str1 = "<font color='#EE0000'>*</font>";
        txt_ApprovalToggleTitle.setText(Html.fromHtml("Type " + str1));
        edt_comment_dialog.setText(Html.fromHtml("Comment " + str1));
        txt_reason_dialog.setText(Html.fromHtml("Reason " + str1));

        final LinearLayout linearLayoutSpinner = approvalDialog.findViewById(R.id.linear_spinner);

        linearLayoutSpinner.setVisibility(View.GONE);

        final EditText edt_comment = approvalDialog.findViewById(R.id.edt_approve_comment);
        final ToggleButton tgText = approvalDialog.findViewById(R.id.tg_approve);

        tgText.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                approvalStatus = "true";
            } else {
                approvalStatus = "false";
            }
        });
        txt_header.setText("Leave Approve/Reject");

        btn_ApproveGo.setOnClickListener(v -> {
            comment = edt_comment.getText().toString().trim();
            if (comment.equals("")) {
                Utilities.showDialog(coordinatorLayout, "Please Enter Comment");
            } else {
                try {
                    sendLeaveProcessApprovalPost(approvalStatus, comment, xmlString, empId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn_close.setOnClickListener(v -> approvalDialog.dismiss());
        approvalDialog.show();
    }

    private void sendLeaveProcessApprovalPost(String approvalStatus, String comment, String xmlString, String empId) {

        final String APPROVE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ProcessShortLeaveChangeRequest/" + approvalStatus + "/" + comment + "/" + xmlString + "/" + empId;
        Log.d(TAG, "getTravelRequestData: " + APPROVE_URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APPROVE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (approvalDialog != null) {
                        approvalDialog.dismiss();
                    }
                    int result = Integer.parseInt(response.replace("\"", ""));
                    switch (result) {
                        case -1:
                            Utilities.showDialog(coordinatorLayout, "Short Leave Request on the same date and same type already exists.");
                            break;
                        case -2:
                            Utilities.showDialog(coordinatorLayout, "Short Leave Request for previous payroll cycle not allow.");
                            break;
                        case -3:
                            Utilities.showDialog(coordinatorLayout, "Short Leave for same date already requested.");
                            break;
                        case -4:
                            Utilities.showDialog(coordinatorLayout, "Attendance period is locked, can not apply request for the period");
                            break;
                        case 0:
                            Utilities.showDialog(coordinatorLayout, "Error during sending Short Leave request.");
                            break;
                        default:
                            Utilities.showDialog(coordinatorLayout, "Short Leave Request Sent Successfully.");
                            getShortLeaveData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onResponse: " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
                return params;
            }


            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        int socketTimeout = 3000000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
