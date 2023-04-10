package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.OPEApprovalFicciAdapter;
import com.savvy.hrmsnewapp.adapter.SpinnerCustomAdapter;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.fragment.ODStatusFragment.MY_PREFS_NAME;
/*import static io.fabric.sdk.android.Fabric.TAG;*/

public class OPE_ApprovalFicciFragment extends BaseFragment {
    OPE_ApprovalFicciFragment.LoadSpinnerDataAsync loadSpinnerDataAsync;
    OPE_ApprovalFicciFragment.ShowDataAsynTask showDataAsynTask;
    OPE_ApprovalFicciFragment.SendProceedAsynTask sendProceedAsynTask;
    Spinner empSpinner;
    SpinnerCustomAdapter spinnerCustomAdapter;
    OPEApprovalFicciAdapter opeApprovalFicciAdapter;
    CustomTextView tv_dataNotFound;
    RecyclerView recyclerView_Approval;
    Button btn_proceed;
    SharedPreferences shared;
    String employeeId = "";
    ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> employeeArrayList;
    ArrayList<HashMap<String, String>> arrayListData;
    String positionValue = "";
    String positionId = "";
    List list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        loadSpinnerData(employeeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ope__approval_ficci, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        empSpinner = getActivity().findViewById(R.id.empSpinner);
        tv_dataNotFound = getActivity().findViewById(R.id.tv_dataNotFound);
        btn_proceed = getActivity().findViewById(R.id.btn_proceed);
        recyclerView_Approval = getActivity().findViewById(R.id.recyclerView_Approval);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView_Approval.setLayoutManager(mLayoutManager);
        recyclerView_Approval.setItemAnimator(new DefaultItemAnimator());

        btn_proceed.setVisibility(View.INVISIBLE);
        recyclerView_Approval.setVisibility(View.INVISIBLE);
        empSpinner.setVisibility(View.INVISIBLE);

        empSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (position == 0) {
                        positionValue = "";
                        positionId = "";
                    } else if (position > 0) {
                        positionId = employeeArrayList.get(position - 1).get("KEY");
                        positionValue = employeeArrayList.get(position - 1).get("VALUE");

                        loadRecyclerViewData(positionId);
                    }
                    Log.e("Spin Value", "Spin Id " + positionId + " Value " + positionValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendProceedRequest();
            }
        });
    }


    private void loadSpinnerData(String employeeId) {
        if (Utilities.isNetworkAvailable(getActivity())) {
            loadSpinnerDataAsync = new OPE_ApprovalFicciFragment.LoadSpinnerDataAsync();
            loadSpinnerDataAsync.employeeId = employeeId;
            loadSpinnerDataAsync.execute();
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    public class LoadSpinnerDataAsync extends AsyncTask<String, String, String> {
        String employeeId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                final String EMLOYEEDATA_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetPendingEmployeeList/" + employeeId;
                JSONParser jParser = new JSONParser(getActivity());
                Log.d("TAG", "doInBackground: APPROVAL_AUTHORITY_URL: " + EMLOYEEDATA_URL);
                String json = jParser.makeHttpRequest(EMLOYEEDATA_URL, "GET");
                return json;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            list = new ArrayList();
            employeeArrayList = new ArrayList<>();
            try {
                pDialog.dismiss();
                HashMap<String, String> odRequestMap;
                JSONArray jsonArray = new JSONArray(result);

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        odRequestMap = new HashMap<>();
                        JSONObject explrObject = jsonArray.getJSONObject(i);

                        String key = explrObject.getString("EMPLOYEE_ID");
                        String value = explrObject.getString("EMPLOYEE_NAME");

                        odRequestMap.put("KEY", key);
                        odRequestMap.put("VALUE", value);
                        employeeArrayList.add(odRequestMap);
                    }

                    System.out.println("Array===" + employeeArrayList);
                    list.add("Approve");
                    list.add("Reject");
                    spinnerCustomAdapter = new SpinnerCustomAdapter(getActivity(), employeeArrayList);
                    empSpinner.setAdapter(spinnerCustomAdapter);
                    empSpinner.setVisibility(View.VISIBLE);

                } else {
                    tv_dataNotFound.setVisibility(View.VISIBLE);
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    public void loadRecyclerViewData(String empID) {

        if (Utilities.isNetworkAvailable(getActivity())) {
            showDataAsynTask = new OPE_ApprovalFicciFragment.ShowDataAsynTask();
            showDataAsynTask.employeeId = employeeId;
            showDataAsynTask.empID = empID;
            showDataAsynTask.execute();
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    public class ShowDataAsynTask extends AsyncTask<String, String, String> {
        String employeeId, empID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                final String OPE_APPROVALDATA_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetPendingRequestByEmployeeId/" + employeeId + "/" + empID;
                JSONParser jParser = new JSONParser(getActivity());
                Log.d("TAG", "doInBackground: APPROVAL_AUTHORITY_URL: " + OPE_APPROVALDATA_URL);
                String json = jParser.makeHttpRequest(OPE_APPROVALDATA_URL, "GET");
                return json;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            arrayListData = new ArrayList<>();
            try {
                HashMap<String, String> mapData;
                JSONArray jsonArray = new JSONArray(result);

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mapData = new HashMap<>();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        mapData.put("EMPLOYEE_NAME", jsonObject.getString("EMPLOYEE_NAME"));
                        mapData.put("D_DEPARTMENT_NAME", jsonObject.getString("D_DEPARTMENT_NAME"));
                        mapData.put("OR_ATTENDANCE_DATE", jsonObject.getString("OR_ATTENDANCE_DATE"));
                        mapData.put("OR_INTIME", jsonObject.getString("OR_INTIME"));
                        mapData.put("OR_OUTIME", jsonObject.getString("OR_OUTIME"));
                        mapData.put("hrs", jsonObject.getString("hrs"));
                        mapData.put("OR_TOTALAMOUNT", jsonObject.getString("OR_TOTALAMOUNT"));
                        mapData.put("ERFS_REQUEST_STATUS_NAME", jsonObject.getString("ERFS_REQUEST_STATUS_NAME"));
                        mapData.put("OR_COMMENT", jsonObject.getString("OR_COMMENT"));

                        mapData.put("ERFS_REQUEST_ID", jsonObject.getString("ERFS_REQUEST_ID"));
                        mapData.put("REQUEST_STATUS_ID", jsonObject.getString("REQUEST_STATUS_ID"));
                        mapData.put("ERFS_ACTION_LEVEL_SEQUENCE", jsonObject.getString("ERFS_ACTION_LEVEL_SEQUENCE"));
                        mapData.put("MAX_ACTION_LEVEL_SEQUENCE", jsonObject.getString("MAX_ACTION_LEVEL_SEQUENCE"));
                        mapData.put("OR_STATUS", jsonObject.getString("OR_STATUS"));
                        mapData.put("OR_EMPLOYEE_ID", jsonObject.getString("OR_EMPLOYEE_ID"));
                        mapData.put("ERFS_REQUEST_FLOW_ID", jsonObject.getString("ERFS_REQUEST_FLOW_ID"));

                        arrayListData.add(mapData);
                    }
                    System.out.println("Array===" + arrayListData);

                    opeApprovalFicciAdapter = new OPEApprovalFicciAdapter(getActivity(), coordinatorLayout, arrayListData, list);
                    recyclerView_Approval.setAdapter(opeApprovalFicciAdapter);
                    recyclerView_Approval.setVisibility(View.VISIBLE);
                    btn_proceed.setVisibility(View.VISIBLE);
                } else {
                    tv_dataNotFound.setVisibility(View.VISIBLE);
                    recyclerView_Approval.setAdapter(null);
                    recyclerView_Approval.setVisibility(View.INVISIBLE);
                    btn_proceed.setVisibility(View.INVISIBLE);
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    private void sendProceedRequest() {

        try {
            String xmlData = "";
            xmlData = opeApprovalFicciAdapter.getxmlData();

            if (xmlData.equals("NoCheckBox Selected")) {
                Utilities.showDialog(coordinatorLayout, "Please Select Checkbox");
                return;
            } else if (xmlData.equals("dropdown")) {
                Utilities.showDialog(coordinatorLayout, "Please Select Action");
                return;
            } else {
                if (Utilities.isNetworkAvailable(getActivity())) {
                    sendProceedAsynTask = new OPE_ApprovalFicciFragment.SendProceedAsynTask();
                    sendProceedAsynTask.xmlData = xmlData;
                    sendProceedAsynTask.employeeId = employeeId;
                    sendProceedAsynTask.execute();
                } else {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }
    }

    public class SendProceedAsynTask extends AsyncTask<String, String, String> {
        String employeeId, xmlData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                final String APPLYACTION_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveApproveRequestOPE/" + employeeId + "/" + xmlData;
                JSONParser jParser = new JSONParser(getActivity());
                Log.d("TAG", "doInBackground: APPROVAL_AUTHORITY_URL: " + APPLYACTION_URL);
                String json = jParser.makeHttpRequest(APPLYACTION_URL, "GET");
                return json;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                pDialog.dismiss();
                int value = 0;
                result = result.replaceAll("^\"|\"$", "").trim();
                value = Integer.parseInt(result);

                if (value > 0) {
                    Utilities.showDialog(coordinatorLayout, "OPE Request Sent Successfully");
                    loadRecyclerViewData(positionId);
                } else {
                    Utilities.showDialog(coordinatorLayout, "Invalid Data");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }

        }
    }
}
