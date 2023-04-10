package com.savvy.hrmsnewapp.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.LeaveApplyFicciAdapter;
import com.savvy.hrmsnewapp.adapter.RHSpinnerAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.MultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LeaveApplyFicciActivity extends BaseActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    LeaveApplyFicciActivity.DatesDataAsynTask datesDataAsynctask;
    RHSpinnerAdapter rhSpinnerAdapter;
    LeaveApplyFicciAdapter mAdapter;
    CoordinatorLayout coordinatorLayout;
    Button fromDate, toDate;
    Button applyButton, resetButton, closeButton;
    EditText edt_Reason, edt_Contact, edt_Address;
    Spinner spinner, whandOverSpinner;
    LinearLayout linearLayout, linear_spinnerLaylout, linearCheckboxLayout, linear_uploadImageLayout;
    LinearLayout linear_Imageupload;
    CustomTextView tvDataNotFound, compareDateTextView, txt_imageUpload;
    TextView dateRange;
    RecyclerView recyclerView;
    CheckBox checkBox;
    CalanderHRMS calanderHRMS;
    ArrayList<HashMap<String, String>> arlData;
    StringBuilder stringBuilder;
    HashMap<Integer, String> mapdata1;
    HashMap<Integer, String> mapdata2;

    Button deductionType;
    CustomTextView deductionValue;
    public static boolean checkboxStatus = false;

    public static final String TAG = "My Tag";
    public static Handler handler;
    public static Runnable runnable;
    Bundle extras;

    ArrayList<HashMap<String, String>> rhArrayListData;
    String value_name = "";
    String value_todate = "";
    String value_fromdate = "";
    String value_config_id = "";
    String value_employee_id = "";
    String value_fromdate1 = "";
    String value_todate1 = "";
    String value_is_previous_year = "", value_leave_type_year = "", value_leave_type_fin_year = "", value_leave_type_year_fin_year = "", value_leave_abbrevation = "";

    public static boolean isHalfPay = false;
    public static boolean dropDownButtonStatus = false;
    public static int itemPosition;
    ProgressDialog progressDialog;
    AlertDialog alertD;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    String displayFileName = "", actualFileName = "";

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_apply_ficci);

        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));

        arlData = new ArrayList<>();
        mapdata1 = new HashMap<>();
        mapdata2 = new HashMap<>();
        stringBuilder = new StringBuilder();
        calanderHRMS = new CalanderHRMS(LeaveApplyFicciActivity.this);
        extras = getIntent().getExtras();

        try {
            if (extras != null) {
                value_name = extras.getString("LEAVE_NAME");
                value_fromdate = extras.getString("FROM_DATE");
                value_todate = extras.getString("TO_DATE");
                value_config_id = extras.getString("LEAVE_CONFIG_ID");
                value_employee_id = extras.getString("EMPLOYEE_ID");

                value_is_previous_year = extras.getString("IS_PREVIOUS_YEAR");
                value_leave_type_year = extras.getString("YEAR");
                value_leave_type_fin_year = extras.getString("FIN_YEAR");
                value_leave_type_year_fin_year = extras.getString("ELBD_YEAR_FIN_YEAR");
                value_leave_abbrevation = extras.getString("LM_ABBREVATION");

                value_fromdate1 = extras.getString("FROM_DATE1");
                value_todate1 = extras.getString("TO_DATE1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Request For " + value_name);
        initValue();
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initValue() {

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        fromDate = (Button) findViewById(R.id.leaveFromDate);
        toDate = (Button) findViewById(R.id.leaveToDate);
        applyButton = (Button) findViewById(R.id.applyButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        closeButton = (Button) findViewById(R.id.closeButton);
        edt_Reason = (EditText) findViewById(R.id.leaveApply_Reason);
        edt_Address = (EditText) findViewById(R.id.leaveApplyAddress);
        spinner = (Spinner) findViewById(R.id.applyFicciSpinner);
        edt_Contact = (EditText) findViewById(R.id.leaveApplyContact);
        whandOverSpinner = (Spinner) findViewById(R.id.handoverSpinner);
        linearLayout = (LinearLayout) findViewById(R.id.LinearApplyCompareDate);

        linear_spinnerLaylout = (LinearLayout) findViewById(R.id.linear_spinnerLaylout);
        linearCheckboxLayout = (LinearLayout) findViewById(R.id.linearCheckboxLayout);
        linear_uploadImageLayout = (LinearLayout) findViewById(R.id.linear_uploadImageLayout);

        compareDateTextView = (CustomTextView) findViewById(R.id.txt_leave_compareDate);
        tvDataNotFound = (CustomTextView) findViewById(R.id.txtleaveApplyDataNotFound);
        dateRange = (TextView) findViewById(R.id.dateRange);

        txt_imageUpload = (CustomTextView) findViewById(R.id.txt_imageUpload);

        checkBox = (CheckBox) findViewById(R.id.halfDayCheckbox);
        linear_Imageupload = (LinearLayout) findViewById(R.id.linear_Imageupload);
        recyclerView = (RecyclerView) findViewById(R.id.leaveApplyList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.INVISIBLE);

        recyclerView.addOnItemTouchListener(new LeaveApplyFicciActivity.RecyclerTouchListener(LeaveApplyFicciActivity.this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {


                deductionType = view.findViewById(R.id.deductionSpinner);
                deductionValue = view.findViewById(R.id.deductionValue);


                if (checkboxStatus == true) {
                    deductionType.setEnabled(false);
                } else {
                    deductionType.setEnabled(true);
                    deductionType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), deductionType);
                            dropDownMenu.getMenuInflater().inflate(R.menu.dropdown_menu, dropDownMenu.getMenu());
                            dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {

                                    if (menuItem.getTitle().equals("Full Day")) {
                                        deductionType.setText("Full Day");
                                        deductionValue.setText("1");
                                    } else if (menuItem.getTitle().equals("First Half")) {
                                        deductionType.setText("First Half");
                                        deductionValue.setText("0.5");
                                    } else if (menuItem.getTitle().equals("Second Half")) {
                                        deductionType.setText("Second Half");
                                        deductionValue.setText("0.5");
                                    }

                                    mapdata1.replace(position, deductionType.getText().toString());
                                    mapdata2.replace(position, deductionValue.getText().toString());

                                    return true;
                                }
                            });
                            dropDownMenu.show();
                        }
                    });
                }
                if (dropDownButtonStatus == true) {
                    deductionType.setEnabled(false);
                } else {
                    deductionType.setEnabled(true);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (value_name.equals("RH (Restricted Leave)")) {
            linear_spinnerLaylout.setVisibility(View.VISIBLE);
        } else if (value_name.equals("SL (Sick Leave)")) {
            linearCheckboxLayout.setVisibility(View.VISIBLE);
            linear_uploadImageLayout.setVisibility(View.VISIBLE);
        }

        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        applyButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        dateRange.setText("Leave Policy Date Range From " + value_fromdate1 + " To " + value_todate1 + ".");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        fromDate.setText(simpleDateFormat.format(date));
        toDate.setText(simpleDateFormat.format(date));

        final String fromdate = fromDate.getText().toString().replace("/", "-");
        String todate = toDate.getText().toString().replace("/", "-");
        getDatesData(value_employee_id, fromdate, todate, value_config_id, isHalfPay);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    isHalfPay = true;
                    checkboxStatus = true;
                    getDatesData(value_employee_id, fromDate.getText().toString(), toDate.getText().toString(), value_config_id, isHalfPay);
                } else {
                    isHalfPay = false;
                    checkboxStatus = false;
                    getDatesData(value_employee_id, fromDate.getText().toString(), toDate.getText().toString(), value_config_id, isHalfPay);
                }
            }
        });

        getRHListData(value_employee_id, value_fromdate1.replace("/", "-"), value_todate1.replace("/", "-"));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    dropDownButtonStatus = false;
                    itemPosition = 0;
                    return;
                } else {
                    itemPosition = position;
                    dropDownButtonStatus = true;
                    fromDate.setText(rhArrayListData.get(0).get("HM_HOLIDAY_DATE_1"));
                    toDate.setText(rhArrayListData.get(0).get("HM_HOLIDAY_DATE_1"));
                    fromDate.setEnabled(false);
                    toDate.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        linear_Imageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFilefromDevice();
            }
        });
    }

    private void uploadFilefromDevice() {

        LayoutInflater layoutInflater = LayoutInflater.from(LeaveApplyFicciActivity.this);
        View promptView = layoutInflater.inflate(R.layout.leave_request_custom_dialog_mmt, null);
        alertD = new AlertDialog.Builder(LeaveApplyFicciActivity.this).create();
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
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            Uri uri = getImageUri(LeaveApplyFicciActivity.this, photo);
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
        if (LeaveApplyFicciActivity.this.getContentResolver() != null) {
            Cursor cursor = LeaveApplyFicciActivity.this.getContentResolver().query(uri, null, null, null, null);
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

        progressDialog = new ProgressDialog(LeaveApplyFicciActivity.this);
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
                    Toast.makeText(LeaveApplyFicciActivity.this, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }, new MultipartRequest.FileUploadListener() {
                @Override
                public void onUpdateProgress(int percentage, long totalSize) {
                }
            });
            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            multipartRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(LeaveApplyFicciActivity.this).addToRequestQueue(multipartRequest);

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
                cursor = LeaveApplyFicciActivity.this.getContentResolver().query(uri, null, null, null, null);
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
        txt_imageUpload.setText(displayFileName);
        imageProcessRequest(byteArray, displayFileName);
    }

    private void getRHListData(String value_employee_id, String value_fromdate, String value_todate) {

        String RHLIST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetRHListForLeave/" + value_employee_id + "/" + value_fromdate + "/" + value_todate;
        final RequestQueue requestQueue = Volley.newRequestQueue(LeaveApplyFicciActivity.this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, RHLIST_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("", "onResponse: " + response);
                try {
                    HashMap<String, String> rhobject;
                    rhArrayListData = new ArrayList<>();
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            rhobject = new HashMap<>();
                            rhobject.put("DISPLAY_NAME", response.getJSONObject(i).getString("DISPLAY_NAME"));
                            rhobject.put("HM_HOLIDAY_DATE_1", response.getJSONObject(i).getString("HM_HOLIDAY_DATE_1"));
                            rhArrayListData.add(rhobject);
                        }
                        rhSpinnerAdapter = new RHSpinnerAdapter(LeaveApplyFicciActivity.this, rhArrayListData);
                        spinner.setAdapter(rhSpinnerAdapter);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
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
        jsonArrayRequest.setRetryPolicy(policy);
        requestQueue.add(jsonArrayRequest);
    }

    private void getDatesData(String empid, String fromdate, String todate, String configid, Boolean isHalfPay) {
        try {

            if (Utilities.isNetworkAvailable(this)) {
                arlData = new ArrayList<>();

                stringBuilder = new StringBuilder();
                datesDataAsynctask = new LeaveApplyFicciActivity.DatesDataAsynTask();
                datesDataAsynctask.empid = empid;
                datesDataAsynctask.fromdate = fromdate.replace("/", "-");
                datesDataAsynctask.todate = todate.replace("/", "-");
                datesDataAsynctask.isHalfPay = isHalfPay;
                datesDataAsynctask.configid = configid;

                datesDataAsynctask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        return false;
    }

    private class DatesDataAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        String fromdate, todate, configid;
        boolean isHalfPay;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LeaveApplyFicciActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String GETDATECOMPAREDATA_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetLeaveBreakupForApplicationFicciPermanent/" + empid + "/" + fromdate + "/" + todate + "/" + configid + "/" + isHalfPay;
                System.out.println("GETRUNNINGBALANCE_URL====" + GETDATECOMPAREDATA_URL);
                JSONParser jParser = new JSONParser(LeaveApplyFicciActivity.this);
                String json = jParser.makeHttpRequest(GETDATECOMPAREDATA_URL, "GET");
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

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try {
                    HashMap<String, String> datesDatamap;
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println("jsonArray===" + jsonArray);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            datesDatamap = new HashMap<>();
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            String attendance_date = explrObject.getString("ATTENDANCE_DATE");
                            String deduction = explrObject.getString("DEDUCTION");
                            String deduction_days = explrObject.getString("DEDUCTION_DAYS");

                            String ear_attendance_date = explrObject.getString("EAR_ATTENDANCE_DATE");
                            String ear_employee_id = explrObject.getString("EAR_EMPLOYEE_ID");

                            String ear_attendance_status = explrObject.getString("EAR_ATTENDANCE_STATUS");

                            mapdata1.put(i, explrObject.getString("DEDUCTION").replace(" ", "-"));
                            mapdata2.put(i, explrObject.getString("DEDUCTION_DAYS").trim());

                            datesDatamap.put("ATTENDANCE_DATE", attendance_date);
                            datesDatamap.put("DEDUCTION", deduction);
                            datesDatamap.put("DEDUCTION_DAYS", deduction_days);
                            datesDatamap.put("EAR_ATTENDANCE_DATE", ear_attendance_date);
                            datesDatamap.put("EAR_EMPLOYEE_ID", ear_employee_id);
                            datesDatamap.put("EAR_ATTENDANCE_STATUS", ear_attendance_status);

                            arlData.add(datesDatamap);

                        }
                        System.out.println("LeaveBalanceArray===" + arlData);
                        mAdapter = new LeaveApplyFicciAdapter(LeaveApplyFicciActivity.this, coordinatorLayout, arlData);
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setVisibility(View.VISIBLE);

                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                        recyclerView.setAdapter(null);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leaveFromDate:
                fromDate.setText("");
                calanderHRMS.datePicker(fromDate);
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = fromDate.getText().toString().trim();
                            String ToDate = toDate.getText().toString().trim();

                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
                            } else {
                                handler.postDelayed(runnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
                break;

            case R.id.leaveToDate:

                toDate.setText("");
                calanderHRMS.datePicker(toDate);
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String FromDate = fromDate.getText().toString().trim();
                            String ToDate = toDate.getText().toString().trim();
                            if (!FromDate.equals("") && (!ToDate.equals(""))) {
                                getCompareDate(FromDate, ToDate);
                            } else {
                                handler.postDelayed(runnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
                break;

            case R.id.applyButton:

                stringBuilder = new StringBuilder();
                if (fromDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select From Date");
                } else if (toDate.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select To Date");
                } else if (!(compareDateTextView.getText().toString().equals(""))) {
                    Utilities.showDialog(coordinatorLayout, "From Date should be less than or equal To Date!");
                } else if (value_name.equals("RH (Restricted Leave)") && itemPosition == 0) {
                    Utilities.showDialog(coordinatorLayout, "Please Select RH Leave");
                } else if (value_name.equals("SL (Sick Leave)") && (isHalfPay == true && arlData.size() < 10)) {
                    Utilities.showDialog(coordinatorLayout, "Sorry you cant take half pay leave less than 10 days.");
                } else if (edt_Reason.getText().toString().trim().equals("")) {
                    edt_Reason.setError("Please Enter Reason");
                } else if (edt_Contact.getText().toString().trim().equals("")) {
                    edt_Contact.setError("Please Enter contact number");
                } else if (edt_Address.getText().toString().trim().equals("")) {
                    edt_Address.setError("Please enter address");
                } else {
                    for (int i = 0; i < arlData.size(); i++) {
                        stringBuilder.append("@").
                                append(arlData.get(i).get("ATTENDANCE_DATE")).
                                append(",").
                                append(arlData.get(i).get("EAR_ATTENDANCE_STATUS")).
                                append(",").
                                append(mapdata1.get(i)).
                                append(",").
                                append(mapdata2.get(i));
                    }

                    if (Utilities.isNetworkAvailable(LeaveApplyFicciActivity.this)) {
                        applyButtonRequest(value_employee_id, value_config_id,
                                value_leave_type_year_fin_year,
                                fromDate.getText().toString().replace("/", "-"),
                                toDate.getText().toString().replace("/", "-"),
                                true,
                                "0",
                                edt_Reason.getText().toString().trim().replaceAll("\\s", "_"),
                                edt_Contact.getText().toString().trim(),
                                edt_Address.getText().toString().trim().replaceAll("\\s", "_"),
                                "0", stringBuilder.toString(),
                                value_is_previous_year,
                                value_leave_type_year,
                                value_leave_type_fin_year,
                                value_leave_abbrevation,
                                "0",
                                isHalfPay, displayFileName, actualFileName);
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }

                }

                break;

            case R.id.resetButton:
                removeData();
                break;

            case R.id.closeButton:
                finish();
                break;

        }
    }

    private void applyButtonRequest(String employeeId, String LeaveConfigID, String LeaveTypeYearFinYear,
                                    final String FromDate, final String ToDate, boolean isLTA, String leaveType, String Reason,
                                    String ContactNo, String Address, String workHandoverTo, String breakUpLeave,
                                    String IS_PREVIOUS_YEAR, String LeaveTypeYear, String LeaveTypeFinYear,
                                    String LeaveAbbrevation, String CoffHolidayID, final boolean isHalfPay, String displayFile_name,
                                    String actualFile_name) {

        final JSONObject pm = new JSONObject();
        JSONObject param = new JSONObject();
        try {

            progressDialog = new ProgressDialog(LeaveApplyFicciActivity.this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Please Wait");
            progressDialog.show();

            param.put("employeeId", employeeId);
            param.put("LeaveConfigID", LeaveConfigID);
            param.put("LeaveTypeYearFinYear", LeaveTypeYearFinYear);
            param.put("FromDate", FromDate);
            param.put("ToDate", ToDate);
            param.put("isLTA", "true");
            param.put("LeaveType", "0");
            param.put("Reason", Reason);
            param.put("ContactNo", ContactNo);
            param.put("Address", Address);
            param.put("WorkHandOverTo", "0");
            param.put("breakUpLeave", breakUpLeave);
            param.put("IS_PREVIOUS_YEAR", IS_PREVIOUS_YEAR);
            param.put("LeaveTypeYear", LeaveTypeYear);
            param.put("LeaveTypeFinYear", LeaveTypeFinYear);
            param.put("LeaveAbbrevation", LeaveAbbrevation);
            param.put("CoffHolidayID", "0");
            param.put("ISHALFPAY", isHalfPay);
            param.put("DisplayFileName", displayFile_name);
            param.put("ActualFileName", actualFile_name);

            Log.d(TAG, "applyButtonRequest: " + param);

            pm.put("objLeaveRequestInfoFicciPermanent", param);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String LEAVEAPPLY_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendLeaveRequestFicciPermanentPost";

        Log.d(TAG, "applyButtonRequest: " + LEAVEAPPLY_URL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LEAVEAPPLY_URL, pm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    String result = response.getString("SendLeaveRequestFicciPermanentPostResult");
                    String result1 = "", message = "";
                    if (result.contains(",")) {
                        String parts[] = result.split("\\,");
                        if (parts.length > 1) {
                            result1 = parts[0];
                            message = parts[1];
                        } else {
                            result1 = result.replace(",", "");
                        }

                    } else {
                        result1 = result.replace(",", "");
                    }
                    Log.e("Result Message", "" + message);
                    Log.e("Result Result1", "" + result1);
                    result1 = result1.replaceAll("^\"+|\"+$", " ").trim();
                    int res = Integer.parseInt(result1);
                    switch (res) {
                        case -1:
                            Utilities.showDialog(coordinatorLayout, "Leave/OD/Punch request already pending for same date.");
                            break;
                        case -2:
                            Utilities.showDialog(coordinatorLayout, "You are not eligible for this leave type.");
                            break;

                        case -3:
                            Utilities.showDialog(coordinatorLayout, "Leave/OD/Punch Request already applied for the same date.");
                            break;

                        case -4:
                            Utilities.showDialog(coordinatorLayout, "You have exceed max limit of ESI Leave.");
                            break;

                        case -5:
                            Utilities.showDialog(coordinatorLayout, "You are not eligible for this leave type.");
                            break;

                        case -6:
                            Utilities.showDialog(coordinatorLayout, "You can not request leave less than " + message + " days.");
                            break;

                        case -7:
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " days.");
                            break;

                        case -8:
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " time in a year.");
                            break;

                        case -9:
                            Utilities.showDialog(coordinatorLayout, "You have to intimate atleast " + message + " days in advance.");
                            break;

                        case -10:
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " days in back.");
                            break;


                        case -11:
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " time in job tenure.");
                            break;


                        case -12:
                            Utilities.showDialog(coordinatorLayout, "You can not clubbed with other leave.");
                            break;


                        case -13:
                            Utilities.showDialog(coordinatorLayout, "This leave can only be clubbed with " + message + " leave");
                            break;

                        case -14:
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than remaining.");
                            break;

                        case -15:
                            Utilities.showDialog(coordinatorLayout, "You can not request LOP Leave more " + message + " days.");
                            break;

                        case -16:
                            Utilities.showDialog(coordinatorLayout, "You can not request Advance Leave more " + message + " days.");
                            break;

                        case -17:
                            Utilities.showDialog(coordinatorLayout, "You can not request Leave as LOP and Advance both consumed.");
                            break;
                        case -18:
                            Utilities.showDialog(coordinatorLayout, "You can not request Leave for previous payroll cycle.");
                            break;

                        case -19:
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " time in a month.");
                            break;

                        case -20:
                            Utilities.showDialog(coordinatorLayout, "You can not request leave more than " + message + " Days in month.");
                            break;

                        case -21:
                            Utilities.showDialog(coordinatorLayout, "You have to select " + message + " Days C-Off from List.");
                            break;

                        case -25:
                            Utilities.showDialog(coordinatorLayout, "Policy not define for Requested Leave Period.");
                            break;

                        case -26:
                            Utilities.showDialog(coordinatorLayout, "Leave date can not be less than Leave Policy Date Range.");
                            break;

                        case -27:
                            Utilities.showDialog(coordinatorLayout, "You can take birth day leave on your birthday date only.");
                            break;

                        case -100:
                            Utilities.showDialog(coordinatorLayout, "Error in Leave break-up details, kindly try it again.");
                            break;

                        case -101:
                            Utilities.showDialog(coordinatorLayout, "Attendance Period has been locked can not apply request for the period.");
                            break;

                        case -102:
                            Utilities.showDialog(coordinatorLayout, "You need to close the previous comp-off before applying the selected compoff date.");
                            break;

                        case -103:
                            Utilities.showDialog(coordinatorLayout, message);
                            break;

                        case 0:
                            Utilities.showDialog(coordinatorLayout, "Error during sending Leave Request");
                            break;

                        default:
                            Utilities.showDialog(coordinatorLayout, "Leave Request send successfully");
                            getDatesData(value_employee_id, FromDate, ToDate, value_config_id, isHalfPay);

                            removeData();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                }

                Log.d(TAG, "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                Log.d(TAG, "onErrorResponse: " + error.toString());
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

        int socketTime = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);
    }

    private void removeData() {
        edt_Reason.setText("");
        edt_Contact.setText("");
        edt_Address.setText("");
        checkBox.setChecked(false);
        txt_imageUpload.setText("");

        getDatesData(value_employee_id, fromDate.getText().toString().replace("/", "-"), toDate.getText().toString().replace("/", "-"), value_config_id, isHalfPay);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId", "61");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }


        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu != null) {
            menu.findItem(R.id.home_dashboard).setVisible(false);
        }
        return true;
    }

    public void getCompareDate(final String fromDate, final String toDate) {
        try {
            if (Utilities.isNetworkAvailable(LeaveApplyFicciActivity.this)) {
                JSONObject param = new JSONObject();

                param.put("From_Date", fromDate);
                param.put("To_Date", toDate);

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";

                RequestQueue requestQueue = Volley.newRequestQueue(LeaveApplyFicciActivity.this);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        compareDateTextView.setText("From Date should be less than or equal To Date!");
                                        linearLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        compareDateTextView.setText("");
                                        linearLayout.setVisibility(View.GONE);
                                        getDatesData(value_employee_id, fromDate, toDate, value_config_id, isHalfPay);
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

                int socketTime = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonRequest.setRetryPolicy(policy);
                requestQueue.add(jsonRequest);
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
        LeaveApplyFicciActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LeaveApplyFicciActivity.ClickListener clickListener) {
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
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
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
