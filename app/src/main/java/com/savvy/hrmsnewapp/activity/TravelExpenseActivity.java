package com.savvy.hrmsnewapp.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.NewCustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.TravelExpenseExpandListAdapter;
import com.savvy.hrmsnewapp.adapter.TravelExpenseRecyclerViewAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.MultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelExpenseActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    TravelExpenseExpandListAdapter expandListAdapter;
    TravelExpenseRecyclerViewAdapter expenseRecyclerViewAdapter;
    NewCustomSpinnerAdapter customSpinnerAdapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    private HashMap<String, List<HashMap<String, String>>> listDataChild;
    ArrayList<HashMap<String, String>> arrayList, spinArraylist;

    RecyclerView recyclerView;
    String travelRequestId = "";
    String policyName = "";
    String policyID = "";
    ProgressDialog progressDialog;
    CoordinatorLayout coordinatorLayout;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    Spinner spinner;

    EditText billNo, billAmount, reason;
    Button billDate, uploadFile, sendRequestButton, resetButton;
    CustomTextView nofileChoose, noDataFound;
    CheckBox checkBox;
    CalanderHRMS calanderHRMS;
    AlertDialog alertD;
    CustomTextView traveltypeValue, startDateValue, returnDateValue;

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    String displayFileName = "", actualFileName = "";
    LinearLayout buttonLayout;
    public static boolean COMPANY_PROVIDED = false;
    public static String EXPENSE_SUB_TYPE_ID = "";

    String travelType = "", travelStartDate = "", travelEndDate = "";
    int maxAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_expense);

        calanderHRMS = new CalanderHRMS(TravelExpenseActivity.this);
        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            travelRequestId = extras.getString("TRF_TRAVEL_REQUEST_ID");
            travelType = extras.getString("TRF_TRAVEL_TYPE");
            policyName = "TRAVEL" + travelType;//extras.getString("TRF_REQUEST_TYPE");
            travelStartDate = extras.getString("TRF_START_DATE");
            travelEndDate = extras.getString("TRF_END_DATE");
        }

        setTitle("Travel Expense");
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getPolicyId(employeeId, policyName);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        expandableListView = (ExpandableListView) findViewById(R.id.travelExpenseExpandableListView);
        prepareListData(employeeId, travelRequestId);
        getRecyclerViewListData();

        recyclerView = (RecyclerView) findViewById(R.id.travelExpenseRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TravelExpenseActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        spinner = (Spinner) findViewById(R.id.expenseTypeInfo);

        billNo = (EditText) findViewById(R.id.edt_BillNo);
        billAmount = (EditText) findViewById(R.id.edt_BillAmount);
        reason = (EditText) findViewById(R.id.edt_Reason);
        billDate = (Button) findViewById(R.id.billDate_Button);
        uploadFile = (Button) findViewById(R.id.btn_TEuploadFile);
        sendRequestButton = (Button) findViewById(R.id.travelExpenseRequestSendButton);
        resetButton = (Button) findViewById(R.id.travelExpenseRequestResetButton);
        nofileChoose = (CustomTextView) findViewById(R.id.txt_TEnoFileChoose);
        noDataFound = (CustomTextView) findViewById(R.id.txt_NoDataToDisplay);

        traveltypeValue = (CustomTextView) findViewById(R.id.travelExpense_travelTypeValue);
        startDateValue = (CustomTextView) findViewById(R.id.travelExpense_StartDateValue);
        returnDateValue = (CustomTextView) findViewById(R.id.travelExpense_ReturnDateValue);

        traveltypeValue.setText(travelType);
        startDateValue.setText(travelStartDate);
        returnDateValue.setText(travelEndDate);

        checkBox = (CheckBox) findViewById(R.id.companyProvided_CheckBox);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayoutTravelExpense);

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (EXPENSE_SUB_TYPE_ID.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Expense Type");
                } else if (billDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Bill Date");
                } else if (billAmount.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Bill Amount");
                } else {

                    int billamount = Math.round(Float.valueOf(billAmount.getText().toString()));

                    if (!(maxAmount > billamount)) {

                        if (reason.getText().toString().trim().equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                        } else {
                            sendRequest(travelRequestId, "0", EXPENSE_SUB_TYPE_ID, billNo.getText().toString().trim(),
                                    billDate.getText().toString().replace("/", "-"),
                                    billAmount.getText().toString().trim(),
                                    COMPANY_PROVIDED,
                                    reason.getText().toString().trim().replaceAll("\\s", ""),
                                    actualFileName, "true", employeeId);
                        }


                    } else {
                        sendRequest(travelRequestId, "0", EXPENSE_SUB_TYPE_ID, billNo.getText().toString().trim(),
                                billDate.getText().toString().replace("/", "-"),
                                billAmount.getText().toString().trim(),
                                COMPANY_PROVIDED,
                                reason.getText().toString().trim().replaceAll("\\s", ""),
                                actualFileName, "false", employeeId);
                    }


                }

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllData();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    EXPENSE_SUB_TYPE_ID = "";
                } else {
                    EXPENSE_SUB_TYPE_ID = spinArraylist.get(position - 1).get("KEY");
                    maxAmount = Math.round(Float.valueOf(spinArraylist.get(position - 1).get("EST_MAX_REQUEST_AMOUNT")));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    COMPANY_PROVIDED = true;
                } else {
                    COMPANY_PROVIDED = false;
                }
            }
        });
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listDataChild.size() > 0) {
                    setListViewHeight(parent, groupPosition);
                } else {
                    Utilities.showDialog(coordinatorLayout, "No Data in List");
                }
                return false;
            }
        });

        billDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calanderHRMS.datePicker(billDate);
            }
        });
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFilefromDevice();
            }
        });
    }

    public void removeAllData() {
        spinner.setSelection(0);
        billNo.setText("");
        billDate.setText("");
        billAmount.setText("");
        checkBox.setChecked(false);
        reason.setText("");
        nofileChoose.setText("");
    }

    private void getPolicyId(String employeeId, String policyName) {
        if (Utilities.isNetworkAvailable(TravelExpenseActivity.this)) {

            final String GET_POLICY_ID = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyPolicyIdTravelVoucherFicci/" + employeeId + "/" + policyName;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_POLICY_ID, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        policyID = response.replaceAll("^\"|\"$", "");

                        if (Integer.valueOf(policyID) > 0) {
                            Utilities.showDialog(coordinatorLayout, "You are authorise for Expense Request");
                            buttonLayout.setVisibility(View.VISIBLE);
                        } else {
                            Utilities.showDialog(coordinatorLayout, "You are not authorise for Expense Request");
                            buttonLayout.setVisibility(View.GONE);
                        }


                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }

                    loadExpenseTypeData();
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
            stringRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }


    private void prepareListData(String employeeId, String travelRequestId) {

        if (Utilities.isNetworkAvailable(TravelExpenseActivity.this)) {

            final String GET_TRAVEL_REQUEST = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetItineraryByTravelId/" + employeeId + "/" + travelRequestId;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_TRAVEL_REQUEST, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    try {

                        List<HashMap<String, String>> childData = new ArrayList<>();
                        HashMap<String, String> mapdata;

                        listDataHeader = new ArrayList<>();
                        listDataChild = new HashMap<>();
                        listDataHeader.add("Click here to view the Journey Details");

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                mapdata = new HashMap<>();
                                mapdata.put("DEPARTURE", response.getJSONObject(i).getString("DEPARTURE"));
                                mapdata.put("RETURN", response.getJSONObject(i).getString("RETURN"));
                                mapdata.put("ID_DEPARTURE_DATE", response.getJSONObject(i).getString("ID_DEPARTURE_DATE"));
                                mapdata.put("ID_RETURN_DATE", response.getJSONObject(i).getString("ID_RETURN_DATE"));
                                mapdata.put("MODE", response.getJSONObject(i).getString("MODE"));
                                mapdata.put("CLASS", response.getJSONObject(i).getString("CLASS"));
                                mapdata.put("ID_START_TIME", response.getJSONObject(i).getString("ID_START_TIME"));
                                mapdata.put("ID_END_TIME", response.getJSONObject(i).getString("ID_END_TIME"));
                                mapdata.put("ID_TYPE", response.getJSONObject(i).getString("ID_TYPE"));

                                childData.add(mapdata);
                            }
                            listDataChild.put(listDataHeader.get(0), childData);

                            expandListAdapter = new TravelExpenseExpandListAdapter(TravelExpenseActivity.this, listDataHeader, listDataChild);
                            expandableListView.setAdapter(expandListAdapter);
                            expandableListView.setVisibility(View.VISIBLE);

                        } else {
                            expandableListView.setVisibility(View.GONE);
                        }


                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
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
            jsonArrayRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void removeDataOnClick(final int position, String expenseId) {

        if (Utilities.isNetworkAvailable(TravelExpenseActivity.this)) {

            final String DELETE_LIST_DATA = Constants.IP_ADDRESS + "/SavvyMobileService.svc/DeleteTravelExpenseFicci";
            JSONObject params_final = new JSONObject();
            JSONObject pm = new JSONObject();
            try {
                params_final.put("EMPLOYEE_ID", employeeId);
                params_final.put("EXPENSE_ID", expenseId);
                pm.put("objDeleteTravelExpenseInfo", params_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, DELETE_LIST_DATA, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        int value = Integer.valueOf(response.getString("DeleteTravelExpenseFicciResult"));
                        if (value > 0) {
                            getRecyclerViewListData();
                        }
                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
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
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }

    }

    private void loadExpenseTypeData() {

        if (Utilities.isNetworkAvailable(TravelExpenseActivity.this)) {

            progressDialog = new ProgressDialog(TravelExpenseActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final String GET_MY_EXPENSE_TYPE_FICCI = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyExpenseTypeFicci";

            JSONObject params_final = new JSONObject();
            JSONObject pm = new JSONObject();
            try {
                params_final.put("POLICY_ID", policyID);
                params_final.put("EXPENSE_SUB_TYPE_ID", "0");
                pm.put("objExpenseTypeInfo", params_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, GET_MY_EXPENSE_TYPE_FICCI, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {

                        HashMap<String, String> mapdata;
                        spinArraylist = new ArrayList<>();

                        JSONArray jsonArray = new JSONArray(response.getString("GetMyExpenseTypeFicciResult"));

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mapdata = new HashMap<>();
                                mapdata.put("KEY", jsonArray.getJSONObject(i).getString("EST_EXPENSE_TYPE_ID"));
                                mapdata.put("VALUE", jsonArray.getJSONObject(i).getString("EST_EXPENSE_SUB_TYPE"));
                                mapdata.put("EST_MAX_REQUEST_AMOUNT", jsonArray.getJSONObject(i).getString("EST_MAX_REQUEST_AMOUNT"));

                                spinArraylist.add(mapdata);
                            }

                            customSpinnerAdapter = new NewCustomSpinnerAdapter(TravelExpenseActivity.this, spinArraylist);
                            spinner.setAdapter(customSpinnerAdapter);
                        }
                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void setListViewHeight(ExpandableListView listView, int group) {
        TravelExpenseExpandListAdapter listAdapter = (TravelExpenseExpandListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
                //Add Divider Height
                totalHeight += listView.getDividerHeight() * (listAdapter.getChildrenCount(i) - 1);
            }
        }
        //Add Divider Height
        totalHeight += listView.getDividerHeight() * (listAdapter.getGroupCount() - 1);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private void uploadFilefromDevice() {

        LayoutInflater layoutInflater = LayoutInflater.from(TravelExpenseActivity.this);
        View promptView = layoutInflater.inflate(R.layout.leave_request_custom_dialog_mmt, null);
        alertD = new AlertDialog.Builder(TravelExpenseActivity.this).create();
        ImageButton btnAdd1 = promptView.findViewById(R.id.galleryButton);
        ImageButton btnAdd2 = promptView.findViewById(R.id.cameraButton);
        btnAdd1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 999);
            }
        });
        btnAdd2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1000);
            }
        });
        alertD.setView(promptView);
        alertD.show();
        Window window = alertD.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setAttributes(wlp);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        alertD.dismiss();
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            try {

                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                getImageFromDevide(uri, myFile);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri uri = getImageUri(TravelExpenseActivity.this, photo);
            File finalFile = new File(getRealPathFromURI(uri));

            getImageFromDevide(uri, finalFile);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (TravelExpenseActivity.this.getContentResolver() != null) {
            Cursor cursor = TravelExpenseActivity.this.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public void imageProcessRequest(byte[] fileData1, String filename) {
        String IMG_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        progressDialog = new ProgressDialog(TravelExpenseActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        try {
            buildPart(dos, fileData1, filename);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            multipartBody = bos.toByteArray();
            MultipartRequest multipartRequest = new MultipartRequest(IMG_URL, null, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        progressDialog.dismiss();
                        String result = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        List<String> list = Arrays.asList(result.split(","));
                        int value = Integer.valueOf(list.get(0));
                        if (value == 1) {
                            Utilities.showDialog(coordinatorLayout, "Image uploaded Successfully");
                        }
                        actualFileName = list.get(1);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(TravelExpenseActivity.this, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }, new MultipartRequest.FileUploadListener() {
                @Override
                public void onUpdateProgress(int percentage, long totalSize) {
                }
            });
            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            multipartRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    public void getImageFromDevide(Uri uri, File finalFile) {
        byte[] byteArray = new byte[0];
        String uriString = uri.toString();
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = TravelExpenseActivity.this.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayFileName = finalFile.getName();
        }
        try {
            File file = new File(uri.getPath());
            byteArray = new byte[(int) file.length()];

        } catch (Exception e) {
            e.printStackTrace();

        }
        nofileChoose.setText(displayFileName);
        imageProcessRequest(byteArray, displayFileName);
    }

    public void getRecyclerViewListData() {
        if (Utilities.isNetworkAvailable(TravelExpenseActivity.this)) {

            final String GET_TRAVEL_REQUEST_FICCI = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelExpenseFicci";

            JSONObject params_final = new JSONObject();
            try {
                params_final.put("TRAVEL_REQUEST_ID", travelRequestId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, GET_TRAVEL_REQUEST_FICCI, params_final, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        HashMap<String, String> mapdata;
                        arrayList = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(response.getString("GetTravelExpenseFicciResult"));

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mapdata = new HashMap<>();
                                mapdata.put("EST_EXPENSE_SUB_TYPE", jsonArray.getJSONObject(i).getString("EST_EXPENSE_SUB_TYPE"));
                                mapdata.put("TED_BILL_NO", jsonArray.getJSONObject(i).getString("TED_BILL_NO"));
                                mapdata.put("TED_BILL_DATE", jsonArray.getJSONObject(i).getString("TED_BILL_DATE"));
                                mapdata.put("TED_BILL_AMOUNT", jsonArray.getJSONObject(i).getString("TED_BILL_AMOUNT"));
                                mapdata.put("TED_IS_PAID_BY_COMPANY", jsonArray.getJSONObject(0).getString("TED_IS_PAID_BY_COMPANY"));
                                mapdata.put("TED_REASON", jsonArray.getJSONObject(i).getString("TED_REASON"));
                                mapdata.put("EXPENSE_ID", jsonArray.getJSONObject(i).getString("EXPENSE_ID"));
                                arrayList.add(mapdata);
                            }
                            expenseRecyclerViewAdapter = new TravelExpenseRecyclerViewAdapter(TravelExpenseActivity.this, arrayList, new TravelExpenseRecyclerViewAdapter.RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    removeDataOnClick(position, arrayList.get(position).get("EXPENSE_ID"));
                                }
                            });
                            recyclerView.setAdapter(expenseRecyclerViewAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            noDataFound.setVisibility(View.GONE);
                        } else {
                            noDataFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
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
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void sendRequest(String travelRequestId, String expenseid, String expenseSubTypeId, String billno, String billdate, String billamount, boolean companyProvided, String reason, String actualFileName, String isexception, String employeeId) {

        if (Utilities.isNetworkAvailable(TravelExpenseActivity.this)) {

            final String ADD_TRVAL_REQUEST = Constants.IP_ADDRESS + "/SavvyMobileService.svc/AddTravelExpenseFicci";

            JSONObject params_final = new JSONObject();
            JSONObject pm = new JSONObject();
            try {
                params_final.put("TRAVEL_REQUEST_ID", travelRequestId);
                params_final.put("EXPENSE_ID", expenseid);
                params_final.put("EXPENSE_SUB_TYPE_ID", expenseSubTypeId);
                params_final.put("BILL_NO", billno);
                params_final.put("BILL_DATE", billdate);
                params_final.put("BILL_AMOUNT", billamount);
                params_final.put("COMPANY_PROVIDED", companyProvided);
                params_final.put("REASON", reason);
                params_final.put("FILE_PATH", actualFileName);
                params_final.put("isexception", isexception);
                params_final.put("USER_ID", employeeId);

                pm.put("objAddTravelExpenseInfo", params_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ADD_TRVAL_REQUEST, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int value = Integer.valueOf(response.getString("AddTravelExpenseFicciResult"));
                        if (value > 0) {
                            Utilities.showDialog(coordinatorLayout, "Sent Request Successfully");
                            removeAllData();
                            getRecyclerViewListData();
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Error! during proccesing Request");
                        }
                    } catch (Exception e) {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
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
            VolleySingleton.getInstance(TravelExpenseActivity.this).addToRequestQueue(jsonObjectRequest);

        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }

}
