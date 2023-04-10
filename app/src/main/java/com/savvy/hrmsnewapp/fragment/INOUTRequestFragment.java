package com.savvy.hrmsnewapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import com.savvy.hrmsnewapp.R;

import com.savvy.hrmsnewapp.activity.DashBoardActivity;
import com.savvy.hrmsnewapp.activity.TravelDeskRequest;
import com.savvy.hrmsnewapp.adapter.AddMultipleItemsAdapter;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.classes.RecyclerTouchListener;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.databinding.InOutRequestFragmentBinding;
import com.savvy.hrmsnewapp.interfaces.ClickListener;
import com.savvy.hrmsnewapp.interfaces.FilePathListener;
import com.savvy.hrmsnewapp.model.FileNameModel;
import com.savvy.hrmsnewapp.retrofitModel.MenuModule;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.MultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleyMultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class INOUTRequestFragment extends BaseFragment {

    protected CoordinatorLayout coordinatorLayout;
    ProgressDialog progressDialog;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token = "", employeeId = "", username = "";
    SharedPreferences shared;
    String displayFileName = "", actualFileName = "", other_actualFileName = "";
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    Button btn_InOutdate, btn_otheruploadFile, btn_spin_meeting_type, btn_spin_work_type, btn_spin_charges_type, btn_TEuploadFile, txt_TEnoFileChoose, btn_spin_cab, btn_cabuploadFile, btn_submit;
    Spinner btn_spin_select_supplier, spin_comp_status, spin_meeting_type, spin_charge_type, spin_cab_type, spin_dropdown_type, hotel_book_spinner, flight_book_spinner, train_book_spinner;
    RecyclerView add_multiple_file;
    EditText edt_InOut_activity, edt_InOut_location, edt_InOut_sublocation, edt_InOut_amount, edt_InOut_remarks, edt_InOut_other;
    CalanderHRMS calanderHRMS;
    CustomSpinnerAdapter customspinnerAdapter;
    TextView btn_od_to_time;
    public static FilePathListener ml;
    public static Context context;
    boolean click_handle = false;
    //    ODRequestFragmentAsync odRequestFragmentAsync;

    CustomTextView txt_AddMore, halfDay_on, firstHalf_on, secondHalf_on, fullDay_off, halfDay_off, firstHalf_off, secondHalf_off;

    String str_hour = "", str_minute = "";
    int pos, pos1;
    Uri mImageCaptureUri;
    private String selectedImagePath = "";

    CustomTextView txt_compareDate;
    Handler handler;
    Runnable rRunnable;

    String FROM_DATE = "", TO_DATE = "";
    String COMPARE_DATE = "";
    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData;
    String positionId = "", positionValue = "";

    String spinnerPosition = "";
    String odsubstatusPosition = "0";
    String odStatusPosition = "1";
    CustomTextView txtOD_ReasonTitle, txtOD_TypeTitle, txtOD_ToDateTitle, txtOD_FromDateTitle;
    String supplierid = "", supplier_name = "", worktype = "", meetingtype = "", chargestype = "", cabtype = "", cabtmt = "", cabtype_id = "";
    RadioButton ncr_radio, non_ncr_radio;
    String check_click = "";
    int Position;
    String hotelid = "", flightid = "", trainid = "", hotelbookby = "", flightbookby = "", trainbookby = "";
    ArrayList<FileNameModel> multiple_item_list = new ArrayList<>();
    LinearLayout ncr_view_layout, non_ncr_view_layout;
    View view;
    InOutRequestFragmentBinding binding;
    ArrayList<String> meetingTypeStringArray = new ArrayList<>();
    ArrayList<String> meetingTypeIDArray = new ArrayList<>();
    ArrayList<String> chargesTypeStringArray = new ArrayList<>();
    ArrayList<String> chargesTypeIDArray = new ArrayList<>();
    ArrayList<String> workTypeStringArray = new ArrayList<>();
    ArrayList<String> workTypeIdArray = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (multiple_item_list != null) {
            multiple_item_list.clear();
        }
//        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
//        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
//        token = (shared.getString("Token", ""));
//        employeeId = (shared.getString("EmpoyeeId", ""));
//        username = (shared.getString("UserName", ""));
//        arlRequestStatusData = new ArrayList<HashMap<String, String>>();
//
//        GetOvertimeReason getOvertimeReason = new GetOvertimeReason();
//        getOvertimeReason.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // View view = inflater.inflate(R.layout.in_out_request_fragment, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.in_out_request_fragment, container, false);
        view = binding.getRoot();
        context = getActivity();
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
//

        calanderHRMS = new CalanderHRMS(getActivity());
//
        btn_InOutdate = view.findViewById(R.id.btn_InOutdate);
        btn_spin_select_supplier = view.findViewById(R.id.btn_spin_select_supplier);
        spin_comp_status = view.findViewById(R.id.spin_comp_status);
        spin_meeting_type = view.findViewById(R.id.spin_meeting_type);
        spin_charge_type = view.findViewById(R.id.spin_charge_type);
        spin_cab_type = view.findViewById(R.id.spin_cab_type);
        spin_dropdown_type = view.findViewById(R.id.spin_dropdown_type);
        edt_InOut_activity = view.findViewById(R.id.edt_InOut_activity);
        edt_InOut_location = view.findViewById(R.id.edt_InOut_location);
        edt_InOut_sublocation = view.findViewById(R.id.edt_InOut_sublocation);
        btn_cabuploadFile = view.findViewById(R.id.btn_cabuploadFile);
        edt_InOut_amount = view.findViewById(R.id.edt_InOut_amount);
        btn_od_to_time = view.findViewById(R.id.btn_od_to_time);
        btn_submit = view.findViewById(R.id.btn_od_request_submit);
        edt_InOut_remarks = view.findViewById(R.id.edt_InOut_remarks);
        edt_InOut_other = view.findViewById(R.id.edt_InOut_other);
        non_ncr_radio = view.findViewById(R.id.non_ncr_radio);
        ncr_radio = view.findViewById(R.id.ncr_radio);
        txt_AddMore = view.findViewById(R.id.txt_AddMore);
        add_multiple_file = view.findViewById(R.id.add_multiple_file);
        non_ncr_view_layout = view.findViewById(R.id.non_ncr_view_layout);
        ncr_view_layout = view.findViewById(R.id.ncr_view_layout);
        hotel_book_spinner = view.findViewById(R.id.hotel_book_spinner);
        flight_book_spinner = view.findViewById(R.id.flight_book_spinner);
        train_book_spinner = view.findViewById(R.id.train_book_spinner);
        btn_otheruploadFile = view.findViewById(R.id.btn_otheruploadFile);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            btn_od_to_time.setText(currentDateandTime);
        } catch (Exception e) {
        }
        try {
            ArrayList<String> spinnerArray = new ArrayList<>();
            spinnerArray.add("Please Select");
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item,
                    spinnerArray);
            btn_spin_select_supplier.setAdapter(spinnerArrayAdapter);
        } catch (Exception e) {
        }
        FileNameModel fileNameModel = new FileNameModel();
        fileNameModel.setFile_name("");
        fileNameModel.setPosition(0);
        multiple_item_list.add(fileNameModel);
        addMultipleItem();
        txt_AddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click_handle = true;
                FileNameModel fileNameModel = new FileNameModel();
                fileNameModel.setFile_name("");
                fileNameModel.setPosition(0);
                multiple_item_list.add(fileNameModel);
                addMultipleItem();
            }
        });
        btn_InOutdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*\  btn_InOutdate.setText("");
                calanderHRMS.datePickerWithOtherFormate(btn_InOutdate);*/
                datePicker();

            }
        });
        btn_cabuploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    check_click = "";
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 999);
                } catch (Exception e) {
                }
            }
        });
        btn_otheruploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    check_click = "other";
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 999);
                } catch (Exception e) {
                }
            }
        });
        binding.btnHotelUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    check_click = "hotel";
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 999);
                } catch (Exception e) {
                }
            }
        });
        binding.btnFlightUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    check_click = "flight";
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 999);
                } catch (Exception e) {
                }
            }
        });
        binding.btnTrainUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    check_click = "train";
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 999);
                } catch (Exception e) {
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!supplier_name.contains("TRAVELLING")) {
                    if (!btn_InOutdate.getText().toString().isEmpty() && !meetingtype.equals("") && !worktype.equals("") && !chargestype.equals("") && !supplierid.equals("")) {
                        saveAllDetails();
                    } else {
                        Toast.makeText(getActivity(), "Please select all required feilds", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    saveAllDetails();
                }
            }
        });
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        employeeId=shared.getString("EMPLOYEE_ID_FINAL", "");
        getWorkType();
        getMeetingType();
        getChargeType();
        getCabType();
        getDropDownType();


        return view;
    }

    public void datePicker() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Date d = new Date(year - 1900, monthOfYear, dayOfMonth);
                String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.UK).format(d);
                btn_InOutdate.setText(date1);
                Intent intent = new Intent("refresh");
                intent.putExtra("date", btn_InOutdate.getText().toString());
                LocalBroadcastManager.getInstance((context)).sendBroadcast(intent);
                getSupplierData(btn_InOutdate.getText().toString());
                //  getAllDetails(date1);
            }
        }, yy, mm, dd);
        datePicker.getDatePicker().setCalendarViewShown(false);
        datePicker.getDatePicker().setMaxDate(new Date().getTime());
        datePicker.show();
    }

    public void getAllDetails(String date) {
        try {

            if (Utilities.isNetworkAvailable(getActivity())) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Fetching...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelPlanCheckInOut";
                Log.e("Save all data", "<><>" + url);
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));
                params_final.put("type","0");
                params_final.put("date", date);


                Log.e("Save all data", "<><>" + params_final.toString());

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                Log.e("Save all data", "<><>" + response.toString());

                                try {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    //    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("GetTravelPlanCheckInOutResult");
                                    if (jsonArray.length() > 0) {
                                        btn_submit.setText("Check Out");
                                        btn_submit.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.color_red)));
                                    } else {
                                        btn_submit.setText("Check In");
                                        btn_submit.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.color_parat_green)));

                                    }
                                } catch (Exception e) {
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("Error", "" + error.getMessage());
                        try {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                        }
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
            } else {
                // Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // alertD.dismiss();
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            try {


                try {
                    Uri uri = data.getData();
                    displayFileName = fileName(uri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        if (bitmap != null) {
                            imageProcessRequest(getFileDataFromDrawable(bitmap), displayFileName);
                        } else {
                            getFileData(uri);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri uri = getImageUri(getActivity(), photo);
            File finalFile = new File(getRealPathFromURI(uri));

            getImageFromDevide(uri, finalFile);
        }
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void getFileData(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.d("nameeeee>>>>  ", displayFileName);
                    try {
                       /* showProgressDialog();
                        InputStream iStream = getActivity().getContentResolver().openInputStream(Uri.parse(displayFileName));
                        assert iStream != null;
                        final byte[] inputData = getBytes(iStream);
                        imageProcessRequest(inputData, displayFileName);*/
                        uploadPDF(displayFileName, uri);
                    }catch (Exception e){}
                    // uploadPDF(displayFileName, uri);
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayFileName = myFile.getName();
        }
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        if (bitmap != null) {
            int imgquality = 100;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, imgquality, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } else {
            return null;
        }

    }

    private String fileName(Uri uri) {
        Cursor returnCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getActivity().getContentResolver() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public void getImageFromDevide(Uri uri, File finalFile) {
        byte[] byteArray = new byte[0];
        String uriString = uri.toString();
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.e("displayFileName>>",""+displayFileName.toString());
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayFileName = finalFile.getName();
        }
        try {
            File file = new File(uri.getPath());
            Log.e("getImageFromDevide>>",""+file.getPath().toString());

            byteArray = new byte[(int) file.length()];
            Log.e("getImageFromDevide>>",""+byteArray.length);
            RandomAccessFile f = new RandomAccessFile(displayFileName, "r");
            byte[] b = new byte[(int)f.length()];
            f.readFully(b);

        } catch (Exception e) {
            e.printStackTrace();

        }
        //  txt_imageUpload.setText(displayFileName);
        imageProcessRequest(byteArray, displayFileName);
    }

    private void odRequest(String fromDate, String toDate, String fromTime, String toTime, String edtreason,
                           String odstatus, String odsubstatus, String odtype) {

        System.out.print("\n\nInside OD request Click  : OD type = " + odtype + " OD Status = " + odstatus + " OD Sub Status =  " + odsubstatus
                + "\n\nFrom Time = " + fromTime + " To time = " + toTime + "\n\nFrom Date = " + fromDate + " To date = " + toDate + " Reason = " + edtreason);

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

//                odRequestFragmentAsync = new ODRequestFragment.ODRequestFragmentAsync();
//                odRequestFragmentAsync.from_date = fromDate;
//                odRequestFragmentAsync.to_date = toDate;
//                odRequestFragmentAsync.from_time = fromTime;
//                odRequestFragmentAsync.to_time = toTime;
//                odRequestFragmentAsync.edtreason = edtreason;
//                odRequestFragmentAsync.odstatus = ""+odstatus;
//                odRequestFragmentAsync.odsubstatus = odsubstatus;
//                odRequestFragmentAsync.odtype = odtype;
//
//                odRequestFragmentAsync.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
    }


    public void getSupplierData(String From_date) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String From_Date = From_date;


                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetSupplierData";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();
                params_final.put("datetime", From_Date);
                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));


//            pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("getSupplierData", "<>><>" + response.toString());

                                try {
//                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("GetSupplierDataResult");
                                    ArrayList<String> spinnerArray = new ArrayList<>();
                                    ArrayList<String> spinnerItemIDArray = new ArrayList<>();
                                    spinnerArray.add("Please Select");
                                    spinnerItemIDArray.add("0");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            String sm_supplier_name = explrObject.getString("SM_SUPPLIER_NAME");
                                            String SM_SUPPLIER_ID = explrObject.getString("SM_SUPPLIER_ID");
                                            spinnerArray.add(sm_supplier_name);
                                            spinnerItemIDArray.add(SM_SUPPLIER_ID);
                                        }
                                    }
                                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                                            android.R.layout.simple_spinner_dropdown_item,
                                            spinnerArray);
                                    btn_spin_select_supplier.setAdapter(spinnerArrayAdapter);
                                    btn_spin_select_supplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            // your code here
                                            if (!btn_spin_select_supplier.getSelectedItem().toString().equals("Please Select")) {
                                                supplierid = spinnerItemIDArray.get(position).toString();
                                                supplier_name = spinnerArray.get(position).toString();
                                                getShowDetailsBySupplier(spinnerItemIDArray.get(position).toString(), From_Date);
                                                getCheckInCheckOutForButton();
                                            } // getCheckInCheckOutForButton();
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parentView) {
                                            // your code here
                                        }

                                    });
//                                    boolean resultDate = response.getBoolean("Compare_DateResult");
//                                    if (!resultDate) {
//                                        txt_compareDate.setText("From Date should be less than or equal To Date!");
//                                        linear_compareDate.setVisibility(View.VISIBLE);
//                                    } else {
//                                        linear_compareDate.setVisibility(View.GONE);
//                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
                                }

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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //activity ------------------------------------------------------------

    public void getShowDetailsBySupplier(String id, String To_date) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                // String From_Date = From_date;
                String To_Date = To_date;

                Log.e("TAG", "activity" + To_Date);
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ShowDetailsBySupplier";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();
                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));
                params_final.put("supplierid", id);
                params_final.put("date", To_Date);


//            pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();
                                Log.e("TAG", "activityResponse" + response.toString());

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                // Log.e("Value", " Length = " + len + " Value = " + response.toString());

                                try {
//                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("ShowDetailsBySupplierResult");
                                    Log.e("TAG", "activityResponseArray" + jsonArray.length());
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i <= jsonArray.length(); i++) {
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            Log.e("TAG", "activityResponseArrayacti" + explrObject.getString("AM_ACTIVITY_NAME"));

                                            edt_InOut_activity.setText(explrObject.getString("AM_ACTIVITY_NAME"));
                                            edt_InOut_location.setText(explrObject.getString("SLD_LOCATION"));
                                            edt_InOut_sublocation.setText(explrObject.getString("TLM_LOCATION"));
                                            binding.edtInOutConveyance.setText(explrObject.getString("SLD_CONVEYANCE_PER_VISIT"));
                                            SharedPreferences.Editor editor = shared.edit();

                                            if (explrObject.getString("SLD_NCR_TYPE").equals("2")) {
                                                non_ncr_radio.setChecked(true);
                                                ncr_radio.setChecked(false);
                                                non_ncr_view_layout.setVisibility(View.VISIBLE);
                                                ncr_view_layout.setVisibility(View.GONE);

                                                editor.putString("ncr", "2");
                                                editor.apply();
                                            } else if (explrObject.getString("SLD_NCR_TYPE").equals("1")) {
                                                ncr_radio.setChecked(true);
                                                non_ncr_radio.setChecked(false);
                                                non_ncr_view_layout.setVisibility(View.GONE);
                                                ncr_view_layout.setVisibility(View.VISIBLE);
                                                editor.putString("ncr", "1");
                                                editor.apply();
                                            }
                                            if (supplier_name.contains("TRAVELLING")) {
                                                non_ncr_view_layout.setVisibility(View.GONE);
                                                ncr_view_layout.setVisibility(View.GONE);
                                                binding.meetingTypeLayout.setVisibility(View.GONE);
                                                binding.cabAmount.setVisibility(View.GONE);
                                                binding.cabTypeLayout.setVisibility(View.GONE);
                                                binding.otherLayout.setVisibility(View.GONE);
                                            } else {
                                                binding.meetingTypeLayout.setVisibility(View.VISIBLE);
                                                binding.cabAmount.setVisibility(View.VISIBLE);
                                                binding.cabTypeLayout.setVisibility(View.VISIBLE);
                                                binding.otherLayout.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
                                }

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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getWorkType() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetWorkType";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));


//            pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("Value2", " Length = " + len + " Value = " + response.toString());

                                try {
//                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("GetWorkTypeResult");

                                    workTypeStringArray.add("Please Select");
                                    workTypeIdArray.add("0");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            String sm_supplier_name = explrObject.getString("Type");
                                            String SM_SUPPLIER_ID = explrObject.getString("Value");
                                            workTypeStringArray.add(sm_supplier_name);
                                            workTypeIdArray.add(SM_SUPPLIER_ID);
                                        }
                                    }
                                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                                            android.R.layout.simple_spinner_dropdown_item,
                                            workTypeStringArray);
                                    spin_comp_status.setAdapter(spinnerArrayAdapter);
                                    spin_comp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            // your code here
                                            if (!spin_comp_status.getSelectedItem().toString().equals("Please Select")) {
                                                worktype = workTypeIdArray.get(position).toString();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parentView) {
                                            // your code here
                                        }

                                    });
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
                                }

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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMeetingType() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMeetingType";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));


//            pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("getMeetingType", " Length = " + len + " Value = " + response.toString());

                                try {
//                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("GetMeetingTypeResult");

                                    meetingTypeStringArray.add("Please Select");
                                    meetingTypeIDArray.add("0");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            String sm_supplier_name = explrObject.getString("Type");
                                            String SM_SUPPLIER_ID = explrObject.getString("Value");
                                            meetingTypeStringArray.add(sm_supplier_name);
                                            meetingTypeIDArray.add(SM_SUPPLIER_ID);
                                        }
                                    }
                                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                                            android.R.layout.simple_spinner_dropdown_item,
                                            meetingTypeStringArray);
                                    spin_meeting_type.setAdapter(spinnerArrayAdapter);
                                    spin_meeting_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            // your code here
                                            if (!spin_meeting_type.getSelectedItem().toString().equals("Please Select")) {
                                                meetingtype = meetingTypeIDArray.get(position).toString();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parentView) {
                                            // your code here
                                        }

                                    });
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
                                }

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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getChargeType() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetChargesType";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));


//              pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                                Log.e("getChargeType", " Length = " + len + " Value = " + response.toString());

                                try {
//                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("GetChargesTypeResult");

                                    chargesTypeStringArray.add("Please Select");
                                    chargesTypeIDArray.add("0");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            String sm_supplier_name = explrObject.getString("Type");
                                            String SM_SUPPLIER_ID = explrObject.getString("Value");
                                            chargesTypeStringArray.add(sm_supplier_name);
                                            chargesTypeIDArray.add(SM_SUPPLIER_ID);
                                        }
                                    }
                                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                                            android.R.layout.simple_spinner_dropdown_item,
                                            chargesTypeStringArray);
                                    spin_charge_type.setAdapter(spinnerArrayAdapter);
                                    spin_charge_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            // your code here
                                            if (!spin_charge_type.getSelectedItem().toString().equals("Please Select")) {
                                                chargestype = chargesTypeIDArray.get(position).toString();
                                            }


                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parentView) {
                                            // your code here
                                        }

                                    });
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
                                }

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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCabType() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCabType";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));


//            pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("GetCabType " + " Length = " + len + " Value = " + response.toString());
                                Log.e("getChargeType", " Length = " + len + " Value = " + response.toString());

                                try {
//                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("GetCabTypeResult");
                                    ArrayList<String> spinnerArray = new ArrayList<>();
                                    ArrayList<String> spinnerIDArray = new ArrayList<>();
                                    spinnerArray.add("Please Select");
                                    spinnerIDArray.add("0");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            String sm_supplier_name = explrObject.getString("Type");
                                            String SM_SUPPLIER_ID = explrObject.getString("Value");
                                            spinnerArray.add(sm_supplier_name);
                                            spinnerIDArray.add(SM_SUPPLIER_ID);
                                        }
                                    }
                                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                                            android.R.layout.simple_spinner_dropdown_item,
                                            spinnerArray);
                                    spin_cab_type.setAdapter(spinnerArrayAdapter);
                                    spin_cab_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            // your code here
                                            if (!spin_cab_type.getSelectedItem().toString().equals("Please Select")) {
                                                cabtype_id = spinnerIDArray.get(position).toString();
                                                cabtype = spinnerArray.get(position).toString();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parentView) {
                                            // your code here
                                        }

                                    });

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
                                }

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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDropDownType() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetDropDownType";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));


//            pm.put("objSendConveyanceRequestInfo", params_final);

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                System.out.print("GetCabType " + " Length = " + len + " Value = " + response.toString());
                                Log.e("getDropDownType", " Length = " + len + " Value = " + response.toString());

                                try {
//                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("GetDropDownTypeResult");
                                    ArrayList<String> spinnerArray = new ArrayList<>();
                                    ArrayList<String> spinnerIDArray = new ArrayList<>();
                                    spinnerArray.add("Please Select");
                                    spinnerIDArray.add("0");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            String sm_supplier_name = explrObject.getString("Type");
                                            String SM_SUPPLIER_ID = explrObject.getString("Value");
                                            spinnerArray.add(sm_supplier_name);
                                            spinnerIDArray.add(SM_SUPPLIER_ID);
                                        }
                                    }
                                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                                            android.R.layout.simple_spinner_dropdown_item,
                                            spinnerArray);
                                    spin_dropdown_type.setAdapter(spinnerArrayAdapter);
                                    spin_dropdown_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            // your code here
                                            if (!spin_dropdown_type.getSelectedItem().toString().equals("Please Select")) {
                                                cabtmt = spinnerIDArray.get(position).toString();
                                                if (spinnerArray.get(position).toString().equals("Self")) {
                                                    btn_cabuploadFile.setVisibility(View.VISIBLE);
                                                    binding.cabAmount.setVisibility(View.VISIBLE);
                                                } else {
                                                    btn_cabuploadFile.setVisibility(View.GONE);
                                                    binding.cabAmount.setVisibility(View.GONE);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parentView) {
                                            // your code here
                                        }

                                    });
                                    hotel_book_spinner.setAdapter(spinnerArrayAdapter);
                                    train_book_spinner.setAdapter(spinnerArrayAdapter);
                                    flight_book_spinner.setAdapter(spinnerArrayAdapter);

                                    hotel_book_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            // your code here
                                            if (!hotel_book_spinner.getSelectedItem().toString().equals("Please Select")) {
                                                hotelid = spinnerIDArray.get(position).toString();
                                                hotelbookby = spinnerArray.get(position).toString();
                                                if (spinnerArray.get(position).toString().contains("Self")) {
                                                    binding.hotelAmountEtv.setVisibility(View.VISIBLE);
                                                    binding.btnHotelUploadFile.setVisibility(View.VISIBLE);
                                                } else {
                                                    binding.hotelAmountEtv.setVisibility(View.GONE);
                                                    binding.btnHotelUploadFile.setVisibility(View.GONE);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parentView) {
                                            // your code here
                                        }

                                    });
                                    train_book_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            // your code here
                                            if (!train_book_spinner.getSelectedItem().toString().equals("Please Select")) {

                                                trainid = spinnerIDArray.get(position).toString();
                                                trainbookby = spinnerArray.get(position).toString();
                                                Log.e("train_book_spinner>>", "<><>" + train_book_spinner.getSelectedItem().toString());
                                                if (train_book_spinner.getSelectedItem().toString().contains("Self")) {
                                                    binding.trainAmountEtv.setVisibility(View.VISIBLE);
                                                    binding.btnTrainUploadFile.setVisibility(View.VISIBLE);
                                                } else {
                                                    binding.trainAmountEtv.setVisibility(View.GONE);
                                                    binding.btnTrainUploadFile.setVisibility(View.GONE);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parentView) {
                                            // your code here
                                        }

                                    });
                                    flight_book_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            // your code here
                                            if (!flight_book_spinner.getSelectedItem().toString().equals("Please Select")) {
                                                flightid = spinnerIDArray.get(position).toString();
                                                flightbookby = spinnerArray.get(position).toString();
                                                if (flight_book_spinner.getSelectedItem().toString().contains("Self")) {
                                                    binding.flightAmountEtv.setVisibility(View.VISIBLE);
                                                    binding.btnFlightUploadFile.setVisibility(View.VISIBLE);
                                                } else {
                                                    binding.flightAmountEtv.setVisibility(View.GONE);
                                                    binding.btnFlightUploadFile.setVisibility(View.GONE);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parentView) {
                                            // your code here
                                        }

                                    });
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
                                }

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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAllDetails() {
        try {
            String Newfilemul = "";
            if (multiple_item_list.size() > 0)
                for (int i = 0; i < multiple_item_list.size(); i++) {
                    if (i == 0) {
                        Newfilemul = multiple_item_list.get(i).getFile_name();
                    } else {
                        Newfilemul = Newfilemul + "," + multiple_item_list.get(i).getFile_name();
                    }
                }
            if (Utilities.isNetworkAvailable(getActivity())) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Uploading...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveCheckInOutDetails";
                Log.e("Save all data", "<><>" + url);
                JSONObject params_final = new JSONObject();

                params_final.put("employeeid", shared.getString("EMPLOYEE_ID_FINAL", ""));
                params_final.put("supplierid", supplierid);
                params_final.put("worktype", worktype);
                params_final.put("meetingtype", meetingtype);
                params_final.put("chargestype", chargestype);
                params_final.put("date", btn_InOutdate.getText().toString());
                params_final.put("newfilemul", Newfilemul);
                params_final.put("orgfilemul", Newfilemul);
                params_final.put("toll", edt_InOut_amount.getText().toString());
                params_final.put("hotelbookby", hotelid);
                params_final.put("hotelamt", binding.hotelAmountEtv.getText().toString());
                params_final.put("flightbookby", flightid);
                params_final.put("flightamt", binding.flightAmountEtv.getText().toString());
                params_final.put("trainbookby", trainid);
                params_final.put("trainamt", binding.trainAmountEtv.getText().toString());
                params_final.put("cabbookby", cabtype_id);
                params_final.put("cabamt", binding.cabAmount.getText().toString());
                params_final.put("otheramt", "");
                params_final.put("newfileflight", binding.btnFlightUploadFile.getText().toString());
                params_final.put("newfiletrain", binding.btnTrainUploadFile.getText().toString());
                params_final.put("newfilehotel", binding.btnHotelUploadFile.getText().toString());
                params_final.put("newfilecab", btn_cabuploadFile.getText().toString());
                params_final.put("newfileother", "");
                params_final.put("orgfileflight", "");
                params_final.put("orgfiletrain", "");
                params_final.put("orgfilehotel", "");
                params_final.put("orgfilecab", actualFileName);
                params_final.put("orgfileother", btn_otheruploadFile.getText().toString());
                params_final.put("remarks", edt_InOut_remarks.getText().toString());
                params_final.put("time", btn_od_to_time.getText().toString());
                params_final.put("cabtype", cabtype_id);

                Log.e("Save all data", "<><>" + params_final.toString());

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                Log.e("Save all data", "<><>" + response.toString());
                                try {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                }
                                Utilities.showDialog(coordinatorLayout, "Data Saved successfully!");
                                Toast.makeText(context, "Data Saved successfully!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getActivity(), DashBoardActivity.class));

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("Error", "" + error.getMessage());
                        try {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                        }
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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getCheckInCheckOutForButton() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Uploading...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCheckInCheckOutForButton";
                Log.e("Save all data", "<><>" + url);
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));
                params_final.put("supplierid", supplierid);
                params_final.put("worktype", "0");
                params_final.put("meetingtype", "0");
                params_final.put("chargestype", "0");
                params_final.put("date", btn_InOutdate.getText().toString());


                Log.e("Save all data", "<><>" + params_final.toString());

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int len = (String.valueOf(response)).length();

                                Log.e("Save all data", "<><>" + response.toString());
                                try {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();

                                        JSONArray jsonArray = response.getJSONArray("GetCheckInCheckOutForButtonResult");

                                        if (jsonArray.length() > 0) {
                                            btn_submit.setText("Check Out");
                                            btn_submit.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.color_red)));
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            meetingtype = jsonObject.getString("TPCICO_MEETING_TYPE");
                                            worktype = jsonObject.getString("TPCICO_WORK_TYPE");
                                            chargestype = jsonObject.getString("TPCICO_CHARGES");
                                            binding.edtInOutAmount.setText(jsonObject.getString("TPCICO_TOLL"));
                                            binding.cabAmount.setText(jsonObject.getString("TPCICO_CAB_AMT"));
                                            if (meetingTypeIDArray.size()>0){
                                                for (int i=0;i<meetingTypeIDArray.size();i++){
                                                    if (meetingTypeIDArray.get(i).toString().equals(meetingtype)){
                                                        spin_meeting_type.setSelection(i);
                                                        break;
                                                    }
                                                }
                                            }if (workTypeIdArray.size()>0){
                                                for (int i=0;i<workTypeIdArray.size();i++){
                                                    if (workTypeIdArray.get(i).toString().equals(worktype)){
                                                        spin_comp_status.setSelection(i);
                                                        break;
                                                    }
                                                }
                                            }if (chargesTypeIDArray.size()>0){
                                                for (int i=0;i<chargesTypeIDArray.size();i++){
                                                    if (chargesTypeIDArray.get(i).toString().equals(chargestype)){
                                                        spin_charge_type.setSelection(i);
                                                        break;
                                                    }
                                                }
                                            }



                                        } else {
                                            btn_submit.setText("Check In");
                                            btn_submit.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.color_parat_green)));

                                        }
                                    }
                                } catch (Exception e) {
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("Error", "" + error.getMessage());
                        try {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                        }
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
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //
//    private class GetOvertimeReason extends AsyncTask<String, String, String> {
//        private ProgressDialog pDialog;
//
//        @Override
//        protected void onPreExecute() {
//            // Things to be done before execution of long running operation. For
//            // example showing ProgessDialog
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage("Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
//
//            try {
//
//                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
//                String GETREQUESTSTATUS_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetODTypeMaster/1";
//
//                System.out.println("ATTENDANCE_URL====" + GETREQUESTSTATUS_URL);
//                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
//                String json = jParser.makeHttpRequest(
//                        GETREQUESTSTATUS_URL, "GET");
//
//                if (json != null) {
//                    Log.d("JSON result", json.toString());
//
//                    return json;
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//
//        @Override
//        protected void onPostExecute(String result) {
//            // execution of result of Long time consuming operation
//            //finalResult.setText(result);
//            if (pDialog != null && pDialog.isShowing()) {
//                pDialog.dismiss();
//                try {
//                    HashMap<String, String> requestStatusmap;
//                    // ArrayList<String> requestArray;
//                    JSONArray jsonArray = new JSONArray(result);
//                    System.out.println("jsonArray===" + jsonArray);
//                    //requestArray=new ArrayList<String>();
//                    if (jsonArray.length() > 0) {
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            requestStatusmap = new HashMap<String, String>();
//                            JSONObject explrObject = jsonArray.getJSONObject(i);
//                            String key = explrObject.getString("ODMasterId");
//                            String value = explrObject.getString("ODType");
//                            // requestArray.add(value);
//                            requestStatusmap.put("KEY", key);
//                            requestStatusmap.put("VALUE", value);
//
//                            arlRequestStatusData.add(requestStatusmap);
//                        }
//                        System.out.println("Array===" + arlRequestStatusData);
//
//                        customspinnerAdapter = new CustomSpinnerAdapter(getActivity(), arlRequestStatusData);
//                        spin_reason_od.setAdapter(customspinnerAdapter);
//
//                    } else {
//                        Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
//                        System.out.println("Data not getting on server side");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//        @Override
//        protected void onProgressUpdate(String... text) {
//            // finalResult.setText(text[0]);
//            // Things to be done while execution of long running operation is in
//            // progress. For example updating ProgessDialog
//        }
//    }
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

    public void imageProcessRequest(byte[] fileData1, String filename) {
        //  String IMG_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId;
        String IMG_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?Type=TravelCheckInOut";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        progressDialog = new ProgressDialog(getActivity());
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
                       /* Log.e("response>>>",""+response.statusCode);
                        Log.e("response>>>",""+response.toString());*/
                        progressDialog.dismiss();
                        String result = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        List<String> list = Arrays.asList(result.split(","));
                        // int value = Integer.valueOf(list.get(0));
                        /* if (value == 1) {*/
                        Utilities.showDialog(coordinatorLayout, "Image uploaded Successfully");
                        /*}*/
                        actualFileName = list.get(1);
                        if (check_click.equals("multi")) {
                            // ml.callback(actualFileName);
                            FileNameModel fileNameModel = new FileNameModel();
                            fileNameModel.setFile_name(actualFileName);
                            fileNameModel.setPosition(Position);
                            multiple_item_list.set(Position, fileNameModel);
                            //  addMultipleItem();

                            click_handle = true;
                            FileNameModel fileNameModel1 = new FileNameModel();
                            fileNameModel1.setFile_name("");
                            fileNameModel1.setPosition(0);
                            multiple_item_list.add(fileNameModel1);
                            addMultipleItem();
                        }
                        else if (check_click.equals("other")) {
                            // ml.callback(actualFileName);
                            btn_otheruploadFile.setText(actualFileName);
                        }
                        else if (check_click.equals("hotel")) {
                            // ml.callback(actualFileName);
                            binding.btnHotelUploadFile.setText(actualFileName);
                        }
                        else if (check_click.equals("train")) {
                            // ml.callback(actualFileName);
                            binding.btnTrainUploadFile.setText(actualFileName);
                        }
                        else if (check_click.equals("flight")) {
                            // ml.callback(actualFileName);
                            binding.btnFlightUploadFile.setText(actualFileName);
                        }
                        else {
                            btn_cabuploadFile.setText(actualFileName);
                        }

                        Log.e("actualFileName", "<><>" + actualFileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Exception>>", "<><>" + e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }, new MultipartRequest.FileUploadListener() {
                @Override
                public void onUpdateProgress(int percentage, long totalSize) {
                }
            });
            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            multipartRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void uploadPDF(final String pdfname, Uri pdffile) {
        InputStream iStream;
        try {
            showProgressDialog();
            iStream = getActivity().getContentResolver().openInputStream(pdffile);
            assert iStream != null;
            final byte[] inputData = getBytes(iStream);
          //  String upload_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + employeeId;
            String upload_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?Type=TravelCheckInOut";
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            dismissProgressDialog();
                           /* Log.e("response>>>",""+response.data.toString());
                            Log.e("response>>>",""+response.toString());*/
                            List<String> list = Arrays.asList(new String(response.data).split(","));
                           /* int value = Integer.parseInt(list.get(0));
                            if (value == 1) {*/
                                Utilities.showDialog(coordinatorLayout, "File Upload Successfully");
                            /*}*/
                            actualFileName = list.get(1);
                            if (check_click.equals("multi")) {
                                // ml.callback(actualFileName);
                                FileNameModel fileNameModel = new FileNameModel();
                                fileNameModel.setFile_name(actualFileName);
                                fileNameModel.setPosition(Position);
                                multiple_item_list.set(Position, fileNameModel);
                                //  addMultipleItem();

                                click_handle = true;
                                FileNameModel fileNameModel1 = new FileNameModel();
                                fileNameModel1.setFile_name("");
                                fileNameModel1.setPosition(0);
                                multiple_item_list.add(fileNameModel1);
                                addMultipleItem();
                            }
                            else if (check_click.equals("other")) {
                                // ml.callback(actualFileName);
                                btn_otheruploadFile.setText(actualFileName);
                            }
                            else if (check_click.equals("hotel")) {
                                // ml.callback(actualFileName);
                                binding.btnHotelUploadFile.setText(actualFileName);
                            }
                            else if (check_click.equals("train")) {
                                // ml.callback(actualFileName);
                                binding.btnTrainUploadFile.setText(actualFileName);
                            }
                            else if (check_click.equals("flight")) {
                                // ml.callback(actualFileName);
                                binding.btnFlightUploadFile.setText(actualFileName);
                            }
                            else {
                                btn_cabuploadFile.setText(actualFileName);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgressDialog();

                            Toast.makeText(getActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return new HashMap<>();
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("filename", new DataPart(pdfname, inputData));
                    return params;
                }
            };
            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(volleyMultipartRequest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMultipleItem() {
        if (multiple_item_list.size() == 1) {
            click_handle = true;
        }
        Log.e("addMultipleItem", ">>" + multiple_item_list.size());
        AddMultipleItemsAdapter side_rv_adapter = new AddMultipleItemsAdapter(getActivity(), multiple_item_list);
        add_multiple_file.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        add_multiple_file.setItemAnimator(new DefaultItemAnimator());
        /*  cardView.scheduleLayoutAnimation();*/
        add_multiple_file.setNestedScrollingEnabled(false);
        add_multiple_file.setAdapter(side_rv_adapter);
        add_multiple_file.setItemViewCacheSize(multiple_item_list.size());
        add_multiple_file.setHasFixedSize(true);
        side_rv_adapter.notifyDataSetChanged();
        add_multiple_file.scrollToPosition(multiple_item_list.size() - 1);
        add_multiple_file.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), add_multiple_file, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    if (click_handle) {
                        click_handle = false;
                        Log.e("Exception", "<><>");
                        check_click = "multi";
                        Position = position;
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(intent, 999);
                    }
                } catch (Exception e) {
                    Log.e("Exception", "<><>" + e.toString());
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

    }

    public void getFile(int position, Context context1, FilePathListener filePathListener) {
        try {
            Activity act = (Activity) context1;
            ml = filePathListener;
            check_click = "multi";
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            ((Activity) context1).startActivityForResult(intent, 999);
        } catch (Exception e) {
            Log.e("Exception", "<><>" + e.toString());
        }
    }

}
