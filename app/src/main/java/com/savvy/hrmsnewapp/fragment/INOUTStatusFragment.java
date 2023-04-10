package com.savvy.hrmsnewapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.savvy.hrmsnewapp.adapter.AddMultipleItemsAdapter;
import com.savvy.hrmsnewapp.adapter.CustomSpinnerAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.classes.RecyclerTouchListener;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.interfaces.ClickListener;
import com.savvy.hrmsnewapp.interfaces.FilePathListener;
import com.savvy.hrmsnewapp.model.FileNameModel;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class INOUTStatusFragment extends BaseFragment {

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
    Button btn_spin_select_supplier, spin_comp_status, spin_meeting_type, spin_charge_type, spin_cab_type, spin_dropdown_type, hotel_book_spinner, flight_book_spinner, train_book_spinner;
    RecyclerView add_multiple_file;
    EditText edt_InOut_activity, edt_InOut_location, edt_InOut_sublocation, edt_InOut_conveyance, edt_InOut_amount, edt_InOut_remarks, edt_InOut_other;
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
    LinearLayout cab_upload_file_layout, cab_amount_layout;
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
    String supplierid = "", worktype = "", meetingtype = "", chargestype = "", cabtype = "", cabtmt = "", cabtype_id = "";
    RadioButton ncr_radio, non_ncr_radio;
    String check_click = "";
    int Position;
    String hotelid = "", flightid = "", trainid = "", hotelbookby = "", flightbookby = "", trainbookby = "";
    ArrayList<FileNameModel> multiple_item_list = new ArrayList<>();
    LinearLayout ncr_view_layout, non_ncr_view_layout;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.in_out_status_fragment, container, false);
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
        cab_upload_file_layout = view.findViewById(R.id.cab_upload_file_layout);
        cab_amount_layout = view.findViewById(R.id.cab_amount_layout);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            btn_od_to_time.setText(currentDateandTime);
        } catch (Exception e) {
        }
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        getAllDetails();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // alertD.dismiss();
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            try {

                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
    }



    public void getAllDetails() {
        try {
//            String Newfilemul = "";
//            if (multiple_item_list.size() > 0)
//                for (int i = 0; i < multiple_item_list.size(); i++) {
//                    if (i == 0) {
//                        Newfilemul = multiple_item_list.get(i).getFile_name();
//                    } else {
//                        Newfilemul = Newfilemul + "," + multiple_item_list.get(i).getFile_name();
//                    }
//                }
            if (Utilities.isNetworkAvailable(getActivity())) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Fetching...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelPlanCheckInOut";
                Log.e("Save all data", "<><>" + url);
                JSONObject params_final = new JSONObject();

                params_final.put("employeeId", shared.getString("EMPLOYEE_ID_FINAL", ""));
                params_final.put("type", shared.getString("ncr", ""));
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
                                        Utilities.showDialog(coordinatorLayout, "Data Fetched successfully!");
                                        Toast.makeText(context, "Data Fetched successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                //    JSONArray jsonArray = new JSONArray(response);
                                    JSONArray jsonArray = response.getJSONArray("GetTravelPlanCheckInOutResult");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject explrObject = jsonArray.getJSONObject(i);
                                            btn_spin_select_supplier.setText(explrObject.getString("SM_SUPPLIER_NAME"));
                                            edt_InOut_activity.setText(explrObject.getString("AM_ACTIVITY_NAME"));
                                            edt_InOut_location.setText(explrObject.getString("SLD_LOCATION"));
                                            edt_InOut_sublocation.setText(explrObject.getString("SLD_LOCATION_TYPE"));
                                           if (explrObject.getString("SLD_LOCATION_TYPE").equals("NCR")) {
                                                ncr_radio.setChecked(true);
                                                non_ncr_radio.setChecked(false);
                                                non_ncr_view_layout.setVisibility(View.GONE);
                                                ncr_view_layout.setVisibility(View.VISIBLE);
                                            }else{
                                               non_ncr_radio.setChecked(true);
                                               ncr_radio.setChecked(false);
                                               non_ncr_view_layout.setVisibility(View.VISIBLE);
                                               ncr_view_layout.setVisibility(View.GONE);
                                           }
                                            edt_InOut_conveyance.setText(explrObject.getString("SLD_LOCATION_TYPE"));
                                            spin_meeting_type.setText(explrObject.getString("TPCICO_MEETING_TYPE"));
                                            spin_comp_status.setText(explrObject.getString("TPCICO_MEETING_TYPE"));
                                            spin_charge_type.setText(explrObject.getString("TPCICO_CHARGES"));
                                            edt_InOut_amount.setText(explrObject.getString("TPCICO_TOLL"));
                                            spin_cab_type.setText(explrObject.getString("TPCICO_TOLL"));

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
}
