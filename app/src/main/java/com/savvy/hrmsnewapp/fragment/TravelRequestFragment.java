package com.savvy.hrmsnewapp.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.ConveyanceCustomSpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.RequestTravelAddRecycle;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.CompareDate;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleyMultipartRequest;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class TravelRequestFragment extends BaseFragment {

    RequestTravelAddRecycle mAdapter;
    ConveyanceCustomSpinnerAdapter mAdapter1;

    Spinner spinner_travel;
    Button btn_add, btn_close, btn_voucher;
    Button btn_final_submit;

    Date Boarding_Date, Arrival_Date;
    String BoardingDate = "", ArrivalDate = "";

    AutoCompleteTextView av_boardingPlace, av_arrivalPlace;
    ArrayList<String> Travel_City_Places, Travel_Country_Places;
    public static String[][] TravelCityPlacesArray;

    Date Travel_Boarding_Date, Travel_Arrival_Date;

    Button btn_BoardingDate, btn_ArrivalDate;
    EditText remarks;
    EditText edt_travelDetailReason;
    EditText edt_place_from, edt_place_to, edt_distance, edt_bill_amount, edt_bill_no, edt_con_reason;
    Spinner travel_mode, travel_class, travel_ExpenseNature;
    //    Spinner travel_BoardingPlace, travel_ArrivalPlace;
    CheckBox chk_ticket, chk_cab, chk_hotel;
    String isTicket = "", isCab = "", isHotel = "";

    static String policyId = "";

    Button btn_TravelStart, btn_TravelEnd;
    EditText edt_EstimatedCost, edt_AdvanceAmount, edt_TravelReason;

    CustomTextView txt_resultData;
    LinearLayout linearArrivalResultDate;

    CustomTextView txt_resultDataMain;
    LinearLayout linearArrivalResultDateMain;

    RecyclerView recyclerView_expense;
    String positionId = "", positionValue = "", ExpenseType = "";

    String travelModeId = "", travelModeValue = "";
    String travelClassId = "", travelClassValue = "";
    String travelTypeId = "", travelTypeValue = "";
    String travelBoardingId = "", travelBoardingValue = "";
    String travelArrivalId = "", travelArrivalValue = "";
    String travelExpenseId = "", travelExpenseValue = "";

    CalanderHRMS calanderHRMS;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String empoyeeId = "";
    SharedPreferences shared, shareData;

    ArrayList<HashMap<String, String>> statusarraylist;
    ArrayList<HashMap<String, String>> arlData, arlData1, arlDataExpenseNature;
    ArrayList<HashMap<String, String>> arlDataTravelType, arlDataTravelMode, arlDataTravelClass, arlDataTravelCity, arlDataTravelCountry;

    Handler handler, handler1;
    Runnable rRunnable, rRunnable1;
    Runnable runnable;

    StringBuilder TravelStringBuilder;

    boolean COMPARE_DATE = true;
    String travelStartDate = "", travelEndDate = "";

    CompareDate compareDate;
    String compare = "";

    CustomTextView txtTitle_travelRemark, txtTitle_advanceAmt, txtTitle_estCost, txtTitle_travelDetail,
            txtTitle_returnDate, txtTitle_startDate, txtTitle_travelType;
    int coun1 = 0;
    private String travelAddRequest = "1";

    Button fileuploadButton;
    CustomTextView nofileChosen;
    ImageView uploadimgIcon;

    AlertDialog alertD;
    ProgressDialog progressDialog;
    String actualFileName = "";
    String displayFileName = "";
    public static boolean isAllPermissionGranted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.VOUCHER_DETAIL = "3";

        compareDate = new CompareDate(getActivity());

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));

        arlData = new ArrayList<HashMap<String, String>>();
        arlData1 = new ArrayList<HashMap<String, String>>();
        statusarraylist = new ArrayList<HashMap<String, String>>();

        arlDataTravelType = new ArrayList<HashMap<String, String>>();
        arlDataTravelMode = new ArrayList<HashMap<String, String>>();
        arlDataTravelClass = new ArrayList<HashMap<String, String>>();
        arlDataTravelCity = new ArrayList<HashMap<String, String>>();
        arlDataTravelCountry = new ArrayList<HashMap<String, String>>();
        arlDataExpenseNature = new ArrayList<HashMap<String, String>>();
        Travel_City_Places = new ArrayList<>();
        Travel_Country_Places = new ArrayList<>();

        getTravelPolicyId();
        getTravelType();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_request, container, false);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        calanderHRMS = new CalanderHRMS(getActivity());

        btn_add = getActivity().findViewById(R.id.btn_add1_travel);
        btn_close = getActivity().findViewById(R.id.btn_close_travel);
        btn_voucher = getActivity().findViewById(R.id.btn_voucher_travel);
        btn_final_submit = getActivity().findViewById(R.id.travelRequestSubmit);

        spinner_travel = getActivity().findViewById(R.id.add_travel_spin);

        linearArrivalResultDateMain = getActivity().findViewById(R.id.linearEndTravel);
        txt_resultDataMain = getActivity().findViewById(R.id.text_resultmain);
        linearArrivalResultDateMain.setVisibility(View.GONE);

        btn_TravelStart = getActivity().findViewById(R.id.btn_TravelFromDate);
        btn_TravelEnd = getActivity().findViewById(R.id.btn_to_TravelDate);

        edt_EstimatedCost = getActivity().findViewById(R.id.edt_EstimatedCost);
        edt_AdvanceAmount = getActivity().findViewById(R.id.edt_AdvanceAmount);
        edt_TravelReason = getActivity().findViewById(R.id.edt_Travel_Reason);


        fileuploadButton = getActivity().findViewById(R.id.btnTravel_fileUpload);
        nofileChosen = getActivity().findViewById(R.id.travel_nofileAttached);
        uploadimgIcon = getActivity().findViewById(R.id.travel_conImageUploade);

        CustomTextView txtTitle_travelRemark, txtTitle_advanceAmt, txtTitle_estCost, txtTitle_travelDetail,
                txtTitle_returnDate, txtTitle_startDate, txtTitle_travelType;

        txtTitle_travelType = getActivity().findViewById(R.id.txtTitle_travelType);
        txtTitle_advanceAmt = getActivity().findViewById(R.id.txtTitle_advanceAmt);
        txtTitle_estCost = getActivity().findViewById(R.id.txtTitle_estCost);
        txtTitle_travelDetail = getActivity().findViewById(R.id.txtTitle_travelDetail);
        txtTitle_returnDate = getActivity().findViewById(R.id.txtTitle_returnDate);
        txtTitle_startDate = getActivity().findViewById(R.id.txtTitle_startDate);
        txtTitle_travelRemark = getActivity().findViewById(R.id.txtTitle_travelRemark);

        String str1 = "<font color='#EE0000'>*</font>";

        txtTitle_travelType.setText(Html.fromHtml("Travel Type " + str1));
        txtTitle_advanceAmt.setText(Html.fromHtml("Advance Amount " + str1));
        txtTitle_estCost.setText(Html.fromHtml("Estimated Cost of Trip " + str1));
        txtTitle_travelDetail.setText(Html.fromHtml("Travel Details " + str1));
        txtTitle_returnDate.setText(Html.fromHtml("Ret. Date " + str1));
        txtTitle_startDate.setText(Html.fromHtml("Start Date " + str1));
        txtTitle_travelRemark.setText(Html.fromHtml("Remarks " + str1));

        spinner_travel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 0) {
                        travelTypeId = "";
                        travelTypeValue = "";
                    } else if (position > 0) {
                        travelTypeId = arlDataTravelType.get(position - 1).get("TT_TRAVEL_TYPE_ID");
                        travelTypeValue = arlDataTravelType.get(position - 1).get("TT_TRAVEL_TYPE_NAME");
                    }
                    Log.e("TravelType Id", travelTypeId + " Value Type " + travelTypeValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView_expense = getActivity().findViewById(R.id.recycler_travel);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView_expense.setLayoutManager(mLayoutManager);
        recyclerView_expense.setItemAnimator(new DefaultItemAnimator());

        btn_TravelStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_TravelStart.setText("");
                calanderHRMS.datePicker(btn_TravelStart);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            travelStartDate = btn_TravelStart.getText().toString().trim();
                            travelEndDate = btn_TravelEnd.getText().toString().trim();
                            if (!travelStartDate.equals("") && !travelEndDate.equals("")) {
                                if (Utilities.isNetworkAvailable(getActivity())) {
                                    GetCompareDateResult(travelStartDate, travelEndDate);
                                }
                            } else {
                                handler.postDelayed(rRunnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GetFuture Method ", e.getMessage());
                        }
                    }
                };
                handler.postDelayed(rRunnable, 1000);

            }
        });

        btn_TravelEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_TravelEnd.setText("");
                calanderHRMS.datePicker(btn_TravelEnd);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            travelStartDate = btn_TravelStart.getText().toString().trim();
                            travelEndDate = btn_TravelEnd.getText().toString().trim();
                            if (!travelStartDate.equals("") && !travelEndDate.equals("")) {
                                if (Utilities.isNetworkAvailable(getActivity())) {
                                    GetCompareDateResult(travelStartDate, travelEndDate);
                                }
                            } else {
                                handler.postDelayed(rRunnable, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(rRunnable, 1000);

            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (travelTypeValue.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Travel Type.");
                } else if (btn_TravelStart.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Travel Start Date.");
                } else if (btn_TravelEnd.getText().toString().equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Select Travel Return Date.");
                } else if (linearArrivalResultDateMain.getVisibility() == View.VISIBLE) {
                    Utilities.showDialog(coordinatorLayout, "From Date Should be Less Than or Equal to To Date.");
                } else {
                    addTravelDialog(recyclerView_expense, travelTypeId);
                }
            }
        });

        btn_voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                TravelVoucherHolderFragment travelVoucherHolderFragment = new TravelVoucherHolderFragment();
                transaction.replace(R.id.container_body, travelVoucherHolderFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    spinner_travel.setSelection(0);
                    btn_TravelStart.setText("");
                    btn_TravelEnd.setText("");
                    edt_TravelReason.setText("");
                    edt_AdvanceAmount.setText("");
                    edt_EstimatedCost.setText("");
                    recyclerView_expense.setAdapter(null);
                    arlData = new ArrayList<HashMap<String, String>>();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_final_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelRequestXmlData();
                String StartTravelDate = btn_TravelStart.getText().toString().trim();
                String ReturnbTravelDate = btn_TravelEnd.getText().toString().trim();
                String estimatedCost = edt_EstimatedCost.getText().toString().trim();
                String advanceCost = edt_AdvanceAmount.getText().toString().trim();
                String Reason = edt_TravelReason.getText().toString().trim();

                if (estimatedCost.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Estimated Cost.");
                } else if (Reason.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Reason.");
                } else {
                    sendTravelRequest(travelTypeId, StartTravelDate, ReturnbTravelDate, estimatedCost, advanceCost, Reason);
                }
            }
        });

        fileuploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    public void addTravelDialog(final RecyclerView recyclerView_add_conveyance, final String travelTypeId1) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.travel_detail_popup);
        dialog.setCancelable(false);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        btn_BoardingDate = dialog.findViewById(R.id.btn_BoardingDate);
        btn_ArrivalDate = dialog.findViewById(R.id.btn_ArrivalDate);

        String TravelTitle = "<font color=\"#26a131\"><bold><u>" + "Add Travel Detail" + "</u></bold></font>";
        CustomTextView txt_travel_title = dialog.findViewById(R.id.travelTitle);
        txt_travel_title.setText(Html.fromHtml(TravelTitle));

        txt_resultData = dialog.findViewById(R.id.txtResultData);
        linearArrivalResultDate = dialog.findViewById(R.id.linearResultTravel);
        linearArrivalResultDate.setVisibility(View.GONE);

        edt_travelDetailReason = dialog.findViewById(R.id.edt_travelDetailReason);

        chk_ticket = dialog.findViewById(R.id.chk_travelTicket);
        chk_cab = dialog.findViewById(R.id.chk_travelCab);
        chk_hotel = dialog.findViewById(R.id.chk_travelHotel);

        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btnTravelAddDetails = dialog.findViewById(R.id.btn_submit_travelRequest);

        travel_mode = dialog.findViewById(R.id.travel_mode);
        travel_class = dialog.findViewById(R.id.travel_class);
        travel_ExpenseNature = dialog.findViewById(R.id.travel_ExpenseNature);

        av_boardingPlace = dialog.findViewById(R.id.av_boardingPlace);
        av_arrivalPlace = dialog.findViewById(R.id.av_arrivalPlace);
        av_boardingPlace.setThreshold(1);
        av_arrivalPlace.setThreshold(1);

        getTravelMode();
        getExpenseNature();

        btn_BoardingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_BoardingDate.setText("");
                CalanderHRMS calanderHRMS = new CalanderHRMS(getActivity());
                calanderHRMS.datePicker(btn_BoardingDate);
                coun1 = 0;

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String travelBoardingDate = btn_BoardingDate.getText().toString().trim();
                            String travelArrivalDate = btn_ArrivalDate.getText().toString().trim();
                            if (!travelBoardingDate.equals("")) {
                                if (Utilities.isNetworkAvailable(getActivity())) {
                                    getCompareDateRequest(travelStartDate, travelEndDate, travelBoardingDate, travelArrivalDate, "Board");
                                }
                            } else {
                                handler.postDelayed(rRunnable, 200);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(rRunnable, 200);

            }
        });

        Log.d("String Value", travelTypeId1);

        btn_ArrivalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ArrivalDate.setText("");
                CalanderHRMS calanderHRMS = new CalanderHRMS(getActivity());
                calanderHRMS.datePicker(btn_ArrivalDate);


                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String travelBoardingDate = btn_BoardingDate.getText().toString().trim();
                            String travelArrivalDate = btn_ArrivalDate.getText().toString().trim();
                            if (!travelArrivalDate.equals("")) {
                                if (Utilities.isNetworkAvailable(getActivity())) {
                                    getCompareDateRequest(travelStartDate, travelEndDate, travelBoardingDate, travelArrivalDate, "Arrive");
                                }
                            } else {
                                handler.postDelayed(rRunnable, 200);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(rRunnable, 200);


            }
        });

        mAdapter1 = new ConveyanceCustomSpinnerAdapter("TRAVELCLASS", getActivity(), arlData1);

        travel_class.setAdapter(mAdapter1);

        travel_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (position == 0) {
                        travelModeId = "";
                        travelModeValue = "";
                    } else if (position > 0) {
//                        arlData1 = new ArrayList<HashMap<String, String>>();
                        travelModeId = arlDataTravelMode.get(position - 1).get("TM_TRAVEL_MODE_ID");
                        travelModeValue = arlDataTravelMode.get(position - 1).get("TM_TRAVEL_MODE_NAME");
                    }
                    if (!travelModeId.equals(""))
                        getTravelClass(travelModeId);

                    Log.e("Travel Id", travelModeId + " Value " + travelModeValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        travel_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (position == 0) {
                        travelClassId = "";
                        travelClassValue = "";
                    } else if (position > 0) {
//                        arlData1 = new ArrayList<HashMap<String, String>>();
                        travelClassId = arlDataTravelClass.get(position - 1).get("TC_TRAVEL_CLASS_ID");
                        travelClassValue = arlDataTravelClass.get(position - 1).get("TC_TRAVEL_CLASS_NAME");
                    }
                    Log.e("Travel Class Id", travelClassId + " Value " + travelClassValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        travel_ExpenseNature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (position == 0) {
                        travelExpenseId = "";
                        travelExpenseValue = "";
                    } else if (position > 0) {
//                        arlData1 = new ArrayList<HashMap<String, String>>();
                        travelExpenseId = arlDataExpenseNature.get(position - 1).get("TEN_TRAVEL_EXPENSE_NATURE_ID");
                        travelExpenseValue = arlDataExpenseNature.get(position - 1).get("TEN_TRAVEL_EXPENSE_NATURE_NAME");
                    }
                    Log.e("Travel Expense Id", travelExpenseId + " Value = " + travelExpenseValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        av_boardingPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    if (s.toString().length() >= 2) {
                        if (travelTypeId1.equals("1")) {
                            getTravelCityLike(s.toString(), av_boardingPlace, "BOARDING");
                        } else if (travelTypeId1.equals("2")) {
                            getTravelCountryLike(s.toString(), av_boardingPlace, "BOARDING");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Constants.TRAVEL_BOARDING_ID = "";
                Constants.TRAVEL_BOARDING_NAME = "";
            }
        });

        av_arrivalPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    if (s.toString().length() >= 2) {
                        if (travelTypeId1.equals("1")) {
                            getTravelCityLike(s.toString(), av_arrivalPlace, "ARRIVING");
                        } else if (travelTypeId1.equals("2")) {
                            getTravelCountryLike(s.toString(), av_arrivalPlace, "ARRIVING");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /*av_boardingPlace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = av_boardingPlace.getText().toString();

                    ListAdapter listAdapter = av_boardingPlace.getAdapter();
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    av_boardingPlace.setText("");
                }
            }
        });

        av_arrivalPlace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String str = av_arrivalPlace.getText().toString();

                    ListAdapter listAdapter = av_arrivalPlace.getAdapter();
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    av_arrivalPlace.setText("");
                }
            }
        });*/

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.closeOptionsMenu();
                dialog.cancel();
            }
        });

        btnTravelAddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelAddRequest = "1";
                HashMap<String, String> map_data = new HashMap<String, String>();

                String BoardingDate1 = btn_BoardingDate.getText().toString().trim();
                String ArrivalDate1 = btn_ArrivalDate.getText().toString().trim();
                String travelReason = edt_travelDetailReason.getText().toString().trim();
                String fromDate = btn_BoardingDate.getText().toString().trim();
                String toDate = btn_ArrivalDate.getText().toString().trim();

                isTicket = ((chk_ticket.isChecked()) ? "1" : "0");
                isCab = ((chk_cab.isChecked()) ? "1" : "0");
                isHotel = ((chk_hotel.isChecked()) ? "1" : "0");

                Log.e("Arrival", Constants.TRAVEL_ARRIVING_ID + " " + Constants.TRAVEL_BOARDING_ID);


                if (Constants.TRAVEL_BOARDING_ID.equals("")) {
                    Toast.makeText(getActivity(), "Please enter boarding place", Toast.LENGTH_SHORT).show();
                    travelAddRequest = "-1";
                } else if (Constants.TRAVEL_ARRIVING_ID.equals("")) {
                    travelAddRequest = "-1";
                    Toast.makeText(getActivity(), "Please enter arrival place", Toast.LENGTH_SHORT).show();
                } else if (Constants.TRAVEL_ARRIVING_ID.toUpperCase().trim().equals(Constants.TRAVEL_BOARDING_ID.toUpperCase().trim())) {
                    travelAddRequest = "-1";
                    Toast.makeText(getActivity(), "Boarding and arrival place can not be same !", Toast.LENGTH_LONG).show();
                } else if (travelModeValue.equals("") || travelClassValue.equals("") || Constants.TRAVEL_ARRIVING_NAME.equals("") || Constants.TRAVEL_BOARDING_NAME.equals("")
                        || travelExpenseValue.equals("") || fromDate.equals("") || toDate.equals("")) {
                    travelAddRequest = "-1";
                    Toast.makeText(getActivity(), "Please Select All Required Data", Toast.LENGTH_LONG).show();
                } else if (linearArrivalResultDate.getVisibility() == View.VISIBLE) {
                    travelAddRequest = "-1";
                    Utilities.showDialog(coordinatorLayout, "From Date Should be Less Than or Equal to To Date.");
                }


                if (!travelAddRequest.equals("-1")) {
                    map_data.put("TravelModeValue", travelModeValue);
                    map_data.put("TravelModeId", travelModeId);
                    map_data.put("TravelClassValue", travelClassValue);
                    map_data.put("TravelClassId", travelClassId);
                    map_data.put("TravelBoardingValue", Constants.TRAVEL_BOARDING_NAME);
                    map_data.put("TravelBoardingId", Constants.TRAVEL_BOARDING_ID);
                    map_data.put("TravelArrivalValue", Constants.TRAVEL_ARRIVING_NAME);
                    map_data.put("TravelArrivalId", Constants.TRAVEL_ARRIVING_ID);
                    map_data.put("BoardingDate", BoardingDate1);
                    map_data.put("ArrivalDate", ArrivalDate1);
                    map_data.put("TravelExpenseValue", travelExpenseValue);
                    map_data.put("TravelExpenseId", travelExpenseId);
                    map_data.put("TravelTicket", isTicket);
                    map_data.put("TravelCab", isCab);
                    map_data.put("TravelHotel", isHotel);
                    map_data.put("TravelReason", travelReason);

                    arlData.add(map_data);
                    Log.e("Arldata DATA", "" + arlData.toString());
                    mAdapter = new RequestTravelAddRecycle(getActivity(), arlData);
                    recyclerView_add_conveyance.setAdapter(mAdapter);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    public void TravelRequestXmlData() {

        TravelStringBuilder = new StringBuilder();
        for (int i = 0; i < arlData.size(); i++) {
            final HashMap<String, String> mapdata = arlData.get(i);

            TravelStringBuilder.append("@");
            TravelStringBuilder.append(mapdata.get("TravelModeId"));
            TravelStringBuilder.append("," + mapdata.get("TravelClassId"));
            TravelStringBuilder.append("," + mapdata.get("TravelBoardingId"));
            TravelStringBuilder.append("," + mapdata.get("TravelArrivalId"));
            TravelStringBuilder.append("," + mapdata.get("BoardingDate"));
            TravelStringBuilder.append("," + mapdata.get("ArrivalDate"));
            TravelStringBuilder.append("," + mapdata.get("TravelExpenseId"));
            TravelStringBuilder.append("," + mapdata.get("TravelTicket"));
            TravelStringBuilder.append("," + mapdata.get("TravelCab"));
            TravelStringBuilder.append("," + mapdata.get("TravelHotel"));
            TravelStringBuilder.append("," + mapdata.get("TravelReason"));

            Log.e("String Builder", TravelStringBuilder.toString());

        }
    }

    public void getTravelType() {
        try {
            arlDataTravelType.clear();
//            arlDataTravelType = new ArrayList<HashMap<String, String>>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelType";


            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                JSONArray jsonArray = response.getJSONArray("GetTravelTypeResult");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> mapData = new HashMap<String, String>();

                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String SpinnerId = explrObject.getString("TT_TRAVEL_TYPE_ID");
                                    String SpinnerType = explrObject.getString("TT_TRAVEL_TYPE_NAME");

                                    mapData.put("TT_TRAVEL_TYPE_ID", SpinnerId);
                                    mapData.put("TT_TRAVEL_TYPE_NAME", SpinnerType);

                                    arlDataTravelType.add(mapData);
                                }
                                mAdapter1 = new ConveyanceCustomSpinnerAdapter("TravelType", getActivity(), arlDataTravelType);
                                spinner_travel.setAdapter(mAdapter1);
                                Log.e("Value of Con Type", arlDataTravelType.toString());
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTravelMode() {
        try {
            arlDataTravelMode.clear();
//            arlDataTravelMode = new ArrayList<HashMap<String, String>>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelMode";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();
//
//            params_final.put("POLICY_ID", policyId);
//            params_final.put("EXPENSE_SUB_TYPE_ID", "0");
//
//            pm.put("objExpenseSubTypeInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                JSONArray jsonArray = response.getJSONArray("GetTravelModeResult");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> mapData = new HashMap<String, String>();

                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String SpinnerId = explrObject.getString("TM_TRAVEL_MODE_ID");
                                    String SpinnerType = explrObject.getString("TM_TRAVEL_MODE_NAME");

                                    mapData.put("TM_TRAVEL_MODE_ID", SpinnerId);
                                    mapData.put("TM_TRAVEL_MODE_NAME", SpinnerType);

                                    arlDataTravelMode.add(mapData);
                                }
                                mAdapter1 = new ConveyanceCustomSpinnerAdapter("TravelMode", getActivity(), arlDataTravelMode);
                                travel_mode.setAdapter(mAdapter1);
                                Log.e("Value of Con Type", arlDataTravelMode.toString());
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTravelClass(String mode) {
        try {
            arlDataTravelClass.clear();
//            arlDataTravelClass = new ArrayList<HashMap<String, String>>();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelClass";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();
//
            params_final.put("TRAVEL_MODE_ID", mode);
//            params_final.put("EXPENSE_SUB_TYPE_ID", "0");
//
//            pm.put("objExpenseSubTypeInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                JSONArray jsonArray = response.getJSONArray("GetTravelClassResult");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> mapData = new HashMap<String, String>();

                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String SpinnerId = explrObject.getString("TC_TRAVEL_CLASS_ID");
                                    String SpinnerType = explrObject.getString("TC_TRAVEL_CLASS_NAME");

                                    mapData.put("TC_TRAVEL_CLASS_ID", SpinnerId);
                                    mapData.put("TC_TRAVEL_CLASS_NAME", SpinnerType);

                                    arlDataTravelClass.add(mapData);
                                }
                                mAdapter1 = new ConveyanceCustomSpinnerAdapter("TRAVELCLASS", getActivity(), arlDataTravelClass);
                                travel_class.setAdapter(mAdapter1);
                                Log.e("Value of Con Type", arlDataTravelClass.toString());
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTravelPolicyId() {
        try {

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetMyPolicyIdTravel";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", empoyeeId);

//            pm.put("objSendConveyanceRequestInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                policyId = response.getString("GetMyPolicyIdTravelResult");
                                Log.e("Policy ID", policyId);
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTravelCity(final AutoCompleteTextView av_boardingPlace, final AutoCompleteTextView av_arrivalPlace) {
        try {
            Travel_City_Places.clear();
            arlDataTravelCity.clear();
//            arlDataTravelCity = new ArrayList<HashMap<String, String>>();
//            Travel_City_Places = new ArrayList<>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCity";


            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                JSONArray jsonArray = response.getJSONArray("GetCityResult");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> mapData = new HashMap<String, String>();

                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String SpinnerId = explrObject.getString("CM_CITY_ID");
                                    String SpinnerType = explrObject.getString("CM_CITY_NAME");

                                    mapData.put("CM_CITY_ID", SpinnerId);
                                    mapData.put("CM_CITY_NAME", SpinnerType);

                                    Travel_City_Places.add(SpinnerType);
                                    arlDataTravelCity.add(mapData);
                                }
                                Log.e("Travel City", "City->" + Travel_City_Places.toString());
                                Log.e("Travel ArlData", "City->" + arlDataTravelCity.toString());
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Travel_City_Places);
                                av_boardingPlace.setAdapter(adapter);
                                av_arrivalPlace.setAdapter(adapter);

//                                mAdapter1 = new ConveyanceCustomSpinnerAdapter("CITY",getActivity(),arlDataTravelCity);
//                                travel_BoardingPlace.setAdapter(mAdapter1);
//                                travel_ArrivalPlace.setAdapter(mAdapter1);
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTravelCountry(final AutoCompleteTextView av_boardingPlace, final AutoCompleteTextView av_arrivalPlace) {
        try {
            Travel_Country_Places.clear();
            arlDataTravelCountry.clear();
//            arlDataTravelCountry = new ArrayList<HashMap<String, String>>();
//            Travel_Country_Places = new ArrayList<>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCountry";

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                JSONArray jsonArray = response.getJSONArray("GetCountryResult");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> mapData = new HashMap<String, String>();

                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String SpinnerId = explrObject.getString("CM_COUNTRY_ID");
                                    String SpinnerType = explrObject.getString("CM_COUNTRY_NAME");

                                    mapData.put("CM_COUNTRY_ID", SpinnerId);
                                    mapData.put("CM_COUNTRY_NAME", SpinnerType);

                                    Travel_Country_Places.add(SpinnerType);
                                    arlDataTravelCountry.add(mapData);
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Travel_Country_Places);
                                av_boardingPlace.setAdapter(adapter);
                                av_arrivalPlace.setAdapter(adapter);
//                                adapter.notifyDataSetChanged();
//                                mAdapter1 = new ConveyanceCustomSpinnerAdapter("COUNTRY",getActivity(),arlDataTravelCountry);
//                                travel_BoardingPlace.setAdapter(mAdapter1);
//                                travel_ArrivalPlace.setAdapter(mAdapter1);
                                Log.e("Value of Con Type", "arlDataTravelCountry = " + arlDataTravelCountry.toString());
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTravelCityLike(String charCity, final AutoCompleteTextView av_complete, final String type) {
        try {
            JSONObject param = new JSONObject();

            param.put("charCity", charCity);
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCityLike";

            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                Travel_City_Places.clear();
                                arlDataTravelCity.clear();
                                JSONArray jsonArray = response.getJSONArray("GetCityLikeResult");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        HashMap<String, String> mapData = new HashMap<>();

                                        JSONObject explrObject = jsonArray.getJSONObject(i);

                                        String SpinnerId = explrObject.getString("CM_CITY_ID");
                                        String SpinnerType = explrObject.getString("CM_CITY_NAME");

                                        mapData.put("CM_CITY_ID", SpinnerId);
                                        mapData.put("CM_CITY_NAME", SpinnerType);

                                        Travel_City_Places.add(SpinnerType);
                                        arlDataTravelCity.add(mapData);

                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                            android.R.layout.simple_list_item_1, Travel_City_Places) {
                                        @Override
                                        public View getView(int position, View convertView, ViewGroup parent) {
                                            View view = super.getView(position, convertView, parent);
                                            TextView text = view.findViewById(android.R.id.text1);
                                            text.setTextColor(Color.BLACK);
                                            return view;
                                        }
                                    };
                                    av_complete.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                    av_complete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                            if (type.equals("BOARDING")) {
                                                Constants.TRAVEL_BOARDING_ID = arlDataTravelCity.get(position).get("CM_CITY_ID");
                                                Constants.TRAVEL_BOARDING_NAME = arlDataTravelCity.get(position).get("CM_CITY_NAME");
                                            } else if (type.equals("ARRIVING")) {
                                                Constants.TRAVEL_ARRIVING_ID = arlDataTravelCity.get(position).get("CM_CITY_ID");
                                                Constants.TRAVEL_ARRIVING_NAME = arlDataTravelCity.get(position).get("CM_CITY_NAME");
                                            }

                                            Log.e("Travel Board", "CityId=" + Constants.TRAVEL_BOARDING_ID + ",CityName = " + Constants.TRAVEL_BOARDING_NAME);
                                            Log.e("Travel Arrival", "CityId=" + Constants.TRAVEL_ARRIVING_ID + ",CityName = " + Constants.TRAVEL_ARRIVING_NAME);

                                        }
                                    });
                                } else {
                                    if (type.equals("BOARDING")) {
                                        Constants.TRAVEL_BOARDING_ID = "";
                                        Constants.TRAVEL_BOARDING_NAME = "";
                                    } else if (type.equals("ARRIVING")) {
                                        Constants.TRAVEL_ARRIVING_ID = "";
                                        Constants.TRAVEL_ARRIVING_NAME = "";
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTravelCountryLike(String charCountry, final AutoCompleteTextView av_complete, final String type) {
        try {

//            arlDataTravelCity = new ArrayList<HashMap<String, String>>();
//            Travel_City_Places = new ArrayList<>();
            JSONObject param = new JSONObject();

            param.put("charCountry", charCountry);
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCountryLike";


            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                Travel_Country_Places.clear();
                                arlDataTravelCountry.clear();
                                JSONArray jsonArray = response.getJSONArray("GetCountryLikeResult");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        HashMap<String, String> mapData = new HashMap<String, String>();

                                        JSONObject explrObject = jsonArray.getJSONObject(i);

                                        String SpinnerId = explrObject.getString("CM_COUNTRY_ID");
                                        String SpinnerType = explrObject.getString("CM_COUNTRY_NAME");

                                        mapData.put("CM_COUNTRY_ID", SpinnerId);
                                        mapData.put("CM_COUNTRY_NAME", SpinnerType);

                                        Travel_Country_Places.add(SpinnerType);
                                        arlDataTravelCountry.add(mapData);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                            android.R.layout.simple_list_item_1, Travel_Country_Places) {
                                        @Override
                                        public View getView(int position, View convertView, ViewGroup parent) {
                                            View view = super.getView(position, convertView, parent);
                                            TextView text = view.findViewById(android.R.id.text1);
                                            text.setTextColor(Color.BLACK);
                                            return view;
                                        }
                                    };
                                    av_complete.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    av_complete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                            if (type.equals("BOARDING")) {
                                                Constants.TRAVEL_BOARDING_ID = arlDataTravelCountry.get(position).get("CM_COUNTRY_ID");
                                                Constants.TRAVEL_BOARDING_NAME = arlDataTravelCountry.get(position).get("CM_COUNTRY_NAME");
                                            } else if (type.equals("ARRIVING")) {
                                                Constants.TRAVEL_ARRIVING_ID = arlDataTravelCountry.get(position).get("CM_COUNTRY_ID");
                                                Constants.TRAVEL_ARRIVING_NAME = arlDataTravelCountry.get(position).get("CM_COUNTRY_NAME");
                                            }

                                            Log.e("Travel Board", "CM_COUNTRY_ID=" + Constants.TRAVEL_BOARDING_ID + ",CM_COUNTRY_NAME = " + Constants.TRAVEL_BOARDING_NAME);
                                            Log.e("Travel Arrival", "CM_COUNTRY_ID=" + Constants.TRAVEL_ARRIVING_ID + ",CM_COUNTRY_NAME = " + Constants.TRAVEL_ARRIVING_NAME);

                                        }
                                    });
                                } else {
                                    if (type.equals("BOARDING")) {
                                        Constants.TRAVEL_BOARDING_ID = "";
                                        Constants.TRAVEL_BOARDING_NAME = "";
                                    } else if (type.equals("ARRIVING")) {
                                        Constants.TRAVEL_ARRIVING_ID = "";
                                        Constants.TRAVEL_ARRIVING_NAME = "";
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getExpenseNature() {
        try {
            arlDataExpenseNature.clear();
//            arlDataExpenseNature = new ArrayList<HashMap<String, String>>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetTravelExpenseNature";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();
//
//            params_final.put("POLICY_ID", policyId);
//            params_final.put("EXPENSE_SUB_TYPE_ID", "0");
//
//            pm.put("objExpenseSubTypeInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                JSONArray jsonArray = response.getJSONArray("GetTravelExpenseNatureResult");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> mapData = new HashMap<String, String>();

                                    JSONObject explrObject = jsonArray.getJSONObject(i);

                                    String SpinnerId = explrObject.getString("TEN_TRAVEL_EXPENSE_NATURE_ID");
                                    String SpinnerType = explrObject.getString("TEN_TRAVEL_EXPENSE_NATURE_NAME");

                                    mapData.put("TEN_TRAVEL_EXPENSE_NATURE_ID", SpinnerId);
                                    mapData.put("TEN_TRAVEL_EXPENSE_NATURE_NAME", SpinnerType);

                                    arlDataExpenseNature.add(mapData);
                                }
                                mAdapter1 = new ConveyanceCustomSpinnerAdapter("EXPENSE_NATURE", getActivity(), arlDataExpenseNature);
                                travel_ExpenseNature.setAdapter(mAdapter1);
                                Log.e("Value of Con Type", arlDataExpenseNature.toString());
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendTravelRequest(String type, String startDate, String ednDate, String estimatedCost, String AdvanceCost, String Reason) {
        try {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Sending Travel Request...");
            progressDialog.show();
            arlDataExpenseNature = new ArrayList<HashMap<String, String>>();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendTravelRequest";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();
//
            Log.e("XML DATA12", "" + TravelStringBuilder.toString());
            Log.d("Parameters", "Travel Type = " + type + ",Start Date=" + startDate + ",REturnDate = "
                    + ednDate + ",Estimated Cost=" + estimatedCost + ",AdvanceCost=" + AdvanceCost + ",Reason=" + Reason + ",Policy Id=" + policyId + ",Employee Id=" + empoyeeId);

            params_final.put("POLICY_ID", policyId);
            params_final.put("EMPLOYEE_ID", empoyeeId);
            params_final.put("REQUEST_ID", "0");
            params_final.put("TRAVEL_TYPE", type);
            params_final.put("START_DATE", startDate);
            params_final.put("RETURN_DATE", ednDate);
            params_final.put("ESTIMATED_COST", estimatedCost);
            params_final.put("ADVANCE_AMOUNT", AdvanceCost);
            params_final.put("REMARKS", Reason);
            params_final.put("JOURNEY_DETAIL", TravelStringBuilder.toString());
            params_final.put("DISPLAY_FILE_NAME", displayFileName);
            params_final.put("ACTUAL_FILE_NAME", actualFileName);
//
            pm.put("objSendTravelRequestInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            int len = (String.valueOf(response)).length();
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                progressDialog.dismiss();
                                String result = response.getString("SendTravelRequestResult");

                                int res = Integer.valueOf(result);

                                if (res > 0) {
                                    Utilities.showDialog(coordinatorLayout, "Travel request send successfully.");
                                    recyclerView_expense.setAdapter(null);
                                    arlData = new ArrayList<>();
                                    edt_EstimatedCost.setText("");
                                    edt_AdvanceAmount.setText("");
                                    edt_TravelReason.setText("");
                                    btn_TravelStart.setText("");
                                    btn_TravelEnd.setText("");
                                    spinner_travel.setSelection(0);
                                    displayFileName = "";
                                    actualFileName = "";

                                } else if (res == -1) {
                                    Utilities.showDialog(coordinatorLayout, "Travel request is already inprocess.");
                                } else if (res == 0) {
                                    Utilities.showDialog(coordinatorLayout, "Error during sending Travel Request.");
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
                    progressDialog.dismiss();
                    Log.d("Error", "" + error.getMessage());
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
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void GetCompareDateResult(String fromDate, String toDate) {
        Constants.COMPARE_DATE_API = true;
        Log.e("Compare Method", "" + Constants.COMPARE_DATE_API);

        String From_Date = fromDate;
        String To_Date = toDate;

        try {

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("From_Date", From_Date);
            params_final.put("To_Date", To_Date);


            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();
                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());
                            try {
                                boolean resultDate = response.getBoolean("Compare_DateResult");
                                if (!resultDate) {
                                    txt_resultDataMain.setText("Start date should be less than or equal to return date!");
                                    linearArrivalResultDateMain.setVisibility(View.VISIBLE);
                                } else {
                                    linearArrivalResultDateMain.setVisibility(View.GONE);
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error", "" + error.getMessage());
                }
            });

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCompareDateRequest(final String start, final String ret, final String Board, final String Arrive, final String Type) {
        try {

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();
            if (Type.equals("Board")) {
                params_final.put("From_Date", start);
                params_final.put("To_Date", Board);
            } else if (Type.equals("Arrive")) {
                params_final.put("From_Date", Arrive);
                params_final.put("To_Date", ret);
            }
            Log.e("JsonObject", "" + params_final.toString());

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean resultDate = response.getBoolean("Compare_DateResult");
                                if (resultDate) {
                                    linearArrivalResultDate.setVisibility(View.GONE);
                                    if ((!Board.equals("")) && (!Arrive.equals(""))) {
                                        getCompareDateRequest1(Board, Arrive);
                                    }
                                } else {
                                    if (Type.equals("Board")) {
                                        linearArrivalResultDate.setVisibility(View.VISIBLE);
                                        btn_BoardingDate.setText("");
                                        txt_resultData.setText("Boarding date should be grater than or equal to Travel Start date!");
                                    } else if (Type.equals("Arrive")) {
                                        linearArrivalResultDate.setVisibility(View.VISIBLE);
                                        btn_ArrivalDate.setText("");
                                        txt_resultData.setText("Arrival date should be less than or equal to Return date!");
                                    }
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error", "" + error.getMessage());
                }
            });

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCompareDateRequest1(String Board, String Arrive) {
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("From_Date", Board);
            params_final.put("To_Date", Arrive);

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean resultDate = response.getBoolean("Compare_DateResult");
                                if (!resultDate) {
                                    linearArrivalResultDate.setVisibility(View.VISIBLE);
                                    txt_resultData.setText("Boarding date should be less than or equal to arrival date!");
                                } else {
                                    linearArrivalResultDate.setVisibility(View.GONE);
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error", "" + error.getMessage());
                }
            });

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadFile() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.leave_request_custom_dialog_mmt, null);
        alertD = new AlertDialog.Builder(getActivity()).create();
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
                displayFileName = fileName(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                    if (bitmap != null) {
                        uploadImage(bitmap);
                    } else {
                        getFileData(Objects.requireNonNull(uri));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                uploadimgIcon.setVisibility(View.GONE);
            }
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            displayFileName = "CaptureImage.jpg";
            uploadImage(photo);
        }
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

                    uploadPDF(displayFileName, uri);
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayFileName = myFile.getName();
        }
    }

    private void uploadPDF(final String pdfname, Uri pdffile) {

        InputStream iStream = null;
        try {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Uploading Image...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            iStream = getActivity().getContentResolver().openInputStream(pdffile);
            final byte[] inputData = getBytes(iStream);
            String upload_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + empoyeeId;

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            progressDialog.dismiss();

                            List<String> list = Arrays.asList(new String(response.data).split(","));
                            int value = Integer.valueOf(list.get(0));
                            if (value == 1) {
                                Utilities.showDialog(coordinatorLayout, "File Upload Successfully");
                                uploadimgIcon.setVisibility(View.VISIBLE);
                                nofileChosen.setText(displayFileName);
                            }
                            actualFileName = list.get(1);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            nofileChosen.setText("");
                            uploadimgIcon.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    return params;
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

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void uploadImage(final Bitmap bitmap) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        String IMG_URL = Constants.IP_ADDRESS + "/MobileFileUpload.ashx?empCode=" + empoyeeId;

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, IMG_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        Log.d("ressssssoo", new String(response.data));

                        List<String> list = Arrays.asList(new String(response.data).split(","));
                        int value = Integer.valueOf(list.get(0));
                        if (value == 1) {
                            Utilities.showDialog(coordinatorLayout, "Image uploaded Successfully");
                            uploadimgIcon.setVisibility(View.VISIBLE);
                            nofileChosen.setText(displayFileName);
                        }
                        actualFileName = list.get(1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        nofileChosen.setText("");
                        uploadimgIcon.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("filename", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(volleyMultipartRequest);
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
}
