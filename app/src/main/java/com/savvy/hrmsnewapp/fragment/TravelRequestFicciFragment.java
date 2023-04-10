package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.AddAdvanceActivity;
import com.savvy.hrmsnewapp.activity.AddCarPickUpActivity;
import com.savvy.hrmsnewapp.activity.AddIteneraryActivity;
import com.savvy.hrmsnewapp.activity.AddPassengerActivity;
import com.savvy.hrmsnewapp.adapter.AccommodationListAdapter;
import com.savvy.hrmsnewapp.adapter.AdvanceListAdapter;
import com.savvy.hrmsnewapp.adapter.CarPickUpListAdapter;
import com.savvy.hrmsnewapp.adapter.CurrencySpinnerAdapter;
import com.savvy.hrmsnewapp.adapter.IteneraryDetailsAdapter;
import com.savvy.hrmsnewapp.adapter.PassangerDetailsAdapter;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.room_database.AdvanceModel;
import com.savvy.hrmsnewapp.room_database.CarDetailsModel;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.IteneraryModel;
import com.savvy.hrmsnewapp.room_database.PassengerModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;
/*import static com.crashlytics.android.core.CrashlyticsCore.TAG;*/

public class TravelRequestFicciFragment extends BaseFragment implements View.OnClickListener {

    CoordinatorLayout coordinatorLayout;
    RadioGroup radioTravelType, radioTrip;
    Button fromDate, endDate, addPassenger, addItenerary, addAccommodation, addAdvance, addCarPickUp;
    RadioButton travelTypeValue, tripRadioButtonValue;
    RadioButton radioDomestic, oneWay;

    Button saveData, clearButton;
    CheckBox checkbox_Accommodation, checkbox_Advance, checkbox_CarPickUp;
    RelativeLayout accommodationLayout, advanceLayout, carpickupLayout;
    LinearLayout compareLinearLayout;
    Spinner projectSpinner, currencySpinner;
    EditText travelReason;
    ExpandableListView passengerListView, itenerayListView, advanceListView, carPickupListView;
    // ExpandableListView accommodationListView;

    PassangerDetailsAdapter passangerDetailsAdapter;
    IteneraryDetailsAdapter iteneraryDetailsAdapter;
    // AccommodationListAdapter accommodationListAdapter;
    AdvanceListAdapter advanceListAdapter;
    CarPickUpListAdapter carPickUpListAdapter;

    List<String> listDataHeader;
    ProgressDialog progressDialog;

    CurrencySpinnerAdapter currencySpinnerAdapter;

    CustomTextView compareDateTextview, txt_budgetedAmount, eventStartDate, eventEndDate;
    List passengerData, iteneraryData, accommodationData, advanceData, carPickupData;

    List<HashMap<String, String>> childItemList = new ArrayList<>();
    List<HashMap<String, String>> iteneraryChildItem = new ArrayList<>();
    //List<HashMap<String, String>> accommodationChildItem = new ArrayList<>();
    List<HashMap<String, String>> advanceChildItem = new ArrayList<>();
    List<HashMap<String, String>> carDetailChildItem = new ArrayList<>();

    ArrayList<HashMap<String, String>> projectArrayList, currencyList;
    HashMap<String, List<HashMap<String, String>>> passengerArrayList = new HashMap<>();
    HashMap<String, List<HashMap<String, String>>> iteneraryArrayList = new HashMap<>();
    // HashMap<String, List<HashMap<String, String>>> accommodationArrayList = new HashMap<>();
    HashMap<String, List<HashMap<String, String>>> advanceArrayList = new HashMap<>();
    HashMap<String, List<HashMap<String, String>>> carDetailsArrayList = new HashMap<>();
    ArrayAdapter<String> autoCompleteAdapter;

    public static String travelType = "";
    public static String travelWay = "";

    public static boolean isNewPassengerAdd;
    public static boolean isNewIteneraryAdd = true;
    public static boolean isNewAccommodationAdd = true;
    public static boolean isAdvanceButtonClicked = true;
    public static boolean isCarPickupButtonClicked = true;

    public static boolean isAccommodationChecked = false;
    public static boolean isAdvanceChecked = false;
    public static boolean isCarPickupChecked = false;

    CalanderHRMS calanderHRMS;
    Handler handler, passengerHandler;
    Runnable rRunnable, prepareListRunnable;
    String FROM_DATE = "", TO_DATE = "";

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String employeeId = "";
    String requestId = "0", projectId = "", currencyId = "";
    int currencyPostion, positionId;
    String passengerXML = "", itineraryXML = "", accommodationXML = "", advanceXML = "", carpickupXML = "";
    ArrayList<String> teamArray;
    AutoCompleteTextView autoCompleteTextView;
    String economyType = "";
    String emptype = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shared.getString("EmpoyeeId", ""));
        deleteAllData();
    }

    private void getEmployeeDetail() {

        if (Utilities.isNetworkAvailable(getActivity())) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            String EMPLOYEE_DETAIL_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeDetailsFicci/" + employeeId + "/" + "0";

            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, EMPLOYEE_DETAIL_URL,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                progressDialog.dismiss();
                                if (response.length() > 0) {

                                    final PassengerModel passengerModel = new PassengerModel();

                                    passengerModel.setFirstname(response.getJSONObject(0).getString("FIRST_NAME"));
                                    passengerModel.setMiddlename(response.getJSONObject(0).getString("MIDDLE_NAME"));
                                    passengerModel.setLastname(response.getJSONObject(0).getString("LAST_NAME"));
                                    passengerModel.setContact(response.getJSONObject(0).getString("CONTACT_NO"));
                                    passengerModel.setAddress(response.getJSONObject(0).getString("ADDRESS"));
                                    passengerModel.setAge(Integer.valueOf(response.getJSONObject(0).getString("AGE")));
                                    passengerModel.setGender(response.getJSONObject(0).getString("GENDER"));
                                    passengerModel.setEmployeetype("Self");
                                    passengerModel.setFoodValue("Veg");
                                    passengerModel.setFoodId("1");

                                    android.os.AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().insertPassengerData(passengerModel);
                                        }
                                    });

                                    passengerHandler = new Handler();
                                    prepareListRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            preparePassengerList();
                                        }
                                    };
                                    passengerHandler.postDelayed(prepareListRunnable, 1000);
                                    //isEmployeeData = "true";

                                } else {
                                    // isEmployeeData = "false";
                                    preparePassengerList();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                preparePassengerList();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    preparePassengerList();
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
            queue.add(stringRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }

    }

    public void preparePassengerList() {
        passengerData = new ArrayList();
        PassengerAsynTask passengerAsynTask = new PassengerAsynTask();
        passengerAsynTask.execute();
    }

    class PassengerAsynTask extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... voids) {
            passengerData = DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().getPassengerData();
            return passengerData;
        }

        @Override
        protected void onPostExecute(List passengerData) {
            super.onPostExecute(passengerData);


            try {
                listDataHeader = new ArrayList<>();
                listDataHeader.add("Passenger Details");
                HashMap<String, String> mapdata;
                if (passengerData.size() > 0) {
                    for (int i = 0; i < passengerData.size(); i++) {
                        mapdata = new HashMap<>();

                        mapdata.put("id", String.valueOf(((PassengerModel) passengerData.get(i)).getId()));
                        mapdata.put("firstname", ((PassengerModel) passengerData.get(i)).getFirstname());
                        mapdata.put("middlename", ((PassengerModel) passengerData.get(i)).getMiddlename());
                        mapdata.put("lastname", ((PassengerModel) passengerData.get(i)).getLastname());
                        mapdata.put("contact", ((PassengerModel) passengerData.get(i)).getContact());
                        mapdata.put("address", ((PassengerModel) passengerData.get(i)).getAddress());
                        mapdata.put("age", String.valueOf(((PassengerModel) passengerData.get(i)).getAge()));
                        mapdata.put("gender", ((PassengerModel) passengerData.get(i)).getGender());
                        mapdata.put("employeetype", ((PassengerModel) passengerData.get(i)).getEmployeetype());
                        mapdata.put("foodtype", ((PassengerModel) passengerData.get(i)).getFoodValue());
                        childItemList.add(mapdata);
                        passengerArrayList.put(listDataHeader.get(0), childItemList);
                    }

                    if (isNewPassengerAdd) {
                        passangerDetailsAdapter = new PassangerDetailsAdapter(getActivity(), listDataHeader, passengerArrayList);
                        passengerListView.setAdapter(passangerDetailsAdapter);
                        passengerListView.setVisibility(View.VISIBLE);
                    } else {
                        passangerDetailsAdapter.notifyDataSetChanged();
                        isNewPassengerAdd = true;

                    }
                } else {
                    passangerDetailsAdapter = new PassangerDetailsAdapter(getActivity(), listDataHeader, passengerArrayList);
                    passengerListView.setAdapter(passangerDetailsAdapter);
                    passengerListView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travel_request_ficci, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        calanderHRMS = new CalanderHRMS(getActivity());
        radioTravelType = getActivity().findViewById(R.id.radioTravel);
        radioTrip = getActivity().findViewById(R.id.radioItenerary);
        radioDomestic = getActivity().findViewById(R.id.radioDomestic);
        oneWay = getActivity().findViewById(R.id.oneWay);

        fromDate = getActivity().findViewById(R.id.travelRequestFromDate);
        endDate = getActivity().findViewById(R.id.travelRequestEndDate);
        addPassenger = getActivity().findViewById(R.id.btn_addPassenger);
        addItenerary = getActivity().findViewById(R.id.btn_addItenerary);
        addAccommodation = getActivity().findViewById(R.id.btn_addAccommodation);
        addAdvance = getActivity().findViewById(R.id.btn_addAdvance);
        addCarPickUp = getActivity().findViewById(R.id.btn_addCarPickUp);

        //checkbox_Accommodation = getActivity().findViewById(R.id.checkbox_Accommodation);
        checkbox_Advance = getActivity().findViewById(R.id.checkbox_Advance);
        checkbox_CarPickUp = getActivity().findViewById(R.id.checkbox_CarPickUp);

        //accommodationLayout = getActivity().findViewById(R.id.accommodationLayout);
        advanceLayout = getActivity().findViewById(R.id.advanceLayout);
        carpickupLayout = getActivity().findViewById(R.id.carpickupLayout);

        saveData = getActivity().findViewById(R.id.saveButton);
        clearButton = getActivity().findViewById(R.id.clearButton);
        passengerListView = getActivity().findViewById(R.id.passengerList);
        itenerayListView = getActivity().findViewById(R.id.iteneraryDetailsList);
        // accommodationListView = getActivity().findViewById(R.id.accommodationListView);
        advanceListView = getActivity().findViewById(R.id.advanceListView);
        carPickupListView = getActivity().findViewById(R.id.carPickupListView);

        //projectSpinner = getActivity().findViewById(R.id.projectSpinner);
        currencySpinner = getActivity().findViewById(R.id.currencySpinner);
        autoCompleteTextView = getActivity().findViewById(R.id.travelExpenseautoCompleteTextView);

        txt_budgetedAmount = getActivity().findViewById(R.id.txt_budgetedAmount);
        eventStartDate = getActivity().findViewById(R.id.txt_EventStart);
        eventEndDate = getActivity().findViewById(R.id.txt_EventEnd);

        compareLinearLayout = getActivity().findViewById(R.id.compareTravelLayout);
        compareDateTextview = getActivity().findViewById(R.id.compareTravelDateTextview);
        travelReason = getActivity().findViewById(R.id.travelReason);
        fromDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        addPassenger.setOnClickListener(this);
        addItenerary.setOnClickListener(this);
        //  addAccommodation.setOnClickListener(this);
        addAdvance.setOnClickListener(this);
        addCarPickUp.setOnClickListener(this);
        saveData.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        isNewPassengerAdd = true;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {

            getEmployeeDetail();
            getProjectSpinnerData();
            getCurrencyList();
            prepareIteneraryList();
            //prepareAccommodationList();    // do not un-comment
            prepareAdvanceList();
            prepareCarDetailList();

        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String empName = autoCompleteAdapter.getItem(position);
                    teamArray.indexOf(empName);
                    projectId = projectArrayList.get(teamArray.indexOf(empName)).get("KEY");

                    if (!projectId.equals("")) {
                        getBudgetedAmount(projectId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals("")) {
                    txt_budgetedAmount.setText("");
                    eventStartDate.setText("");
                    eventEndDate.setText("");
                    projectId = "";
                }
            }
        });
        passengerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (passengerData.size() == 0) {
                    Utilities.showDialog(coordinatorLayout, " No Data in List");
                } else {
                    setListViewHeight1(parent, groupPosition);
                }
                return false;
            }
        });
        passengerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.i("TAG", "onChildClick: ");
                return false;
            }
        });

        itenerayListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (iteneraryData.size() == 0) {
                    Utilities.showDialog(coordinatorLayout, "No data in list");
                } else {
                    setListViewHeight2(parent, groupPosition);
                }
                return false;
            }
        });

        /*accommodationListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (accommodationData.size() == 0) {
                    Utilities.showDialog(coordinatorLayout, "No data in list");
                } else {
                    setListViewHeight3(parent, groupPosition);
                }

                return false;
            }
        });*/

        advanceListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (advanceData.size() == 0) {
                    Utilities.showDialog(coordinatorLayout, "No data in list");
                } else {
                    setListViewHeight4(parent, groupPosition);
                }
                return false;
            }
        });

        carPickupListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (carPickupData.size() == 0) {
                    Utilities.showDialog(coordinatorLayout, "No data in list");
                } else {
                    setListViewHeight5(parent, groupPosition);
                }
                return false;
            }
        });

       /* projectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionId = position;
                if (position == 0) {
                    positionId = position;
                }
                if (position > 0) {
                    try {
                        positionId = position;
                        projectId = projectArrayList.get(position - 1).get("KEY");
                        getBudgetedAmount(projectId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                currencyPostion = position;
                if (position > 0) {
                    currencyPostion = position;
                    currencyId = currencyList.get(position).get("KEY");
                } else {
                    currencyPostion = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*checkbox_Accommodation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAccommodationChecked = true;
                    accommodationLayout.setVisibility(View.VISIBLE);
                } else {
                    isAccommodationChecked = false;
                    accommodationLayout.setVisibility(View.GONE);
                }
            }
        });*/

        checkbox_Advance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAdvanceChecked = true;
                    advanceLayout.setVisibility(View.VISIBLE);
                } else {
                    isAdvanceChecked = false;
                    advanceLayout.setVisibility(View.GONE);
                }
            }
        });
        checkbox_CarPickUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCarPickupChecked = true;
                    carpickupLayout.setVisibility(View.VISIBLE);
                } else {
                    isCarPickupChecked = false;
                    carpickupLayout.setVisibility(View.GONE);
                }
            }
        });


        radioTravelType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                travelTypeValue = getActivity().findViewById(checkedId);
                travelType = travelTypeValue.getText().toString();
                clearListOnTravelChange(travelType);
                if (travelType.equals("International")) {
                    currencySpinner.setEnabled(false);
                    currencySpinner.setSelection(2);
                } else {
                    currencySpinner.setEnabled(false);
                    currencySpinner.setSelection(2);
                }
            }
        });

        radioTrip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tripRadioButtonValue = group.findViewById(checkedId);
                travelWay = (String) tripRadioButtonValue.getText();
                clearListOnTripChange(travelWay);
            }
        });


        if (radioDomestic.isChecked() && oneWay.isChecked()) {
            travelType = "Domestic";
            travelWay = "One Way";
            endDate.setEnabled(false);
        }

    }


    private void clearListOnTripChange(String travelWay) {

        if (travelWay.equals("One Way")) {
            android.os.AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllItineraryData();
                    DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllAccommodtionData();
                    DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllAdvanceData();
                    DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllCarDetail();
                }
            });
            if (iteneraryChildItem.size() > 0) {
                iteneraryChildItem.clear();
                iteneraryDetailsAdapter.notifyDataSetChanged();
            }
           /* if (accommodationChildItem.size() > 0) {
                accommodationChildItem.clear();
                accommodationListAdapter.notifyDataSetChanged();
            }*/
            if (advanceChildItem.size() > 0) {
                advanceChildItem.clear();
                advanceListAdapter.notifyDataSetChanged();
            }
            if (carDetailChildItem.size() > 0) {

                carDetailChildItem.clear();
                carPickUpListAdapter.notifyDataSetChanged();
            }/*if(passengerData.size()>0)
            {

                if(((PassengerModel) passengerData.get(0)).getEmployeetype().equals("Self"))
                {

                }

            }*/
            endDate.setEnabled(false);

        } else if (travelWay.equals("Round Trip")) {
            android.os.AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllItineraryData();
                    DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllAccommodtionData();
                    DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllAdvanceData();
                    DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllCarDetail();
                }
            });
            if (iteneraryChildItem.size() > 0) {
                iteneraryChildItem.clear();
                iteneraryDetailsAdapter.notifyDataSetChanged();
            }
            /*if (accommodationChildItem.size() > 0) {
                accommodationChildItem.clear();
                accommodationListAdapter.notifyDataSetChanged();
            }*/
            if (advanceChildItem.size() > 0) {
                advanceChildItem.clear();
                advanceListAdapter.notifyDataSetChanged();
            }
            if (carDetailChildItem.size() > 0) {
                carDetailChildItem.clear();
                carPickUpListAdapter.notifyDataSetChanged();
            }
            endDate.setEnabled(true);
            // todo setting height
            // setListViewHeight4(advanceListView, 0);
        } else {
            deleteAllData();
            if (iteneraryChildItem.size() > 0) {
                iteneraryChildItem.clear();
                iteneraryDetailsAdapter.notifyDataSetChanged();
            }
            /*if (accommodationChildItem.size() > 0) {
                accommodationChildItem.clear();
                accommodationListAdapter.notifyDataSetChanged();
            }*/
            if (advanceChildItem.size() > 0) {
                advanceChildItem.clear();
                advanceListAdapter.notifyDataSetChanged();
            }
            if (carDetailChildItem.size() > 0) {
                carDetailChildItem.clear();
                carPickUpListAdapter.notifyDataSetChanged();
            }
            endDate.setEnabled(true);
        }
    }

    private void clearListOnTravelChange(String travelType) {
        if (travelType.equals("Domestic")) {
            deleteAllData();
            if (iteneraryChildItem.size() > 0) {
                iteneraryChildItem.clear();
                iteneraryDetailsAdapter.notifyDataSetChanged();
            }
            /*if (accommodationChildItem.size() > 0) {
                accommodationChildItem.clear();
                accommodationListAdapter.notifyDataSetChanged();
            }*/
            if (advanceChildItem.size() > 0) {
                advanceChildItem.clear();
                advanceListAdapter.notifyDataSetChanged();
            }
            if (carDetailChildItem.size() > 0) {
                carDetailChildItem.clear();
                carPickUpListAdapter.notifyDataSetChanged();
            }
        } else {
            deleteAllData();
            if (iteneraryChildItem.size() > 0) {
                iteneraryChildItem.clear();
                iteneraryDetailsAdapter.notifyDataSetChanged();
            }
           /* if (accommodationChildItem.size() > 0) {
                accommodationChildItem.clear();
                accommodationListAdapter.notifyDataSetChanged();
            }*/
            if (advanceChildItem.size() > 0) {
                advanceChildItem.clear();
                advanceListAdapter.notifyDataSetChanged();
            }
            if (carDetailChildItem.size() > 0) {
                carDetailChildItem.clear();
                carPickUpListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void prepareCarDetailList() {

        CarDetailAsyncTask carDetailAsyncTask = new CarDetailAsyncTask();
        carDetailAsyncTask.execute();
    }

    class CarDetailAsyncTask extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... voids) {
            carPickupData = DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().getCarDetails();
            return carPickupData;
        }

        @Override
        protected void onPostExecute(List carPickupData) {
            super.onPostExecute(carPickupData);

            try {
                listDataHeader = new ArrayList<>();
                listDataHeader.add("Advance Details");
                HashMap<String, String> mapdata;
                if (carPickupData.size() > 0) {
                    for (int i = 0; i < carPickupData.size(); i++) {
                        mapdata = new HashMap<>();

                        mapdata.put("id", String.valueOf(((CarDetailsModel) carPickupData.get(i)).getId()));
                        mapdata.put("pickupdate", String.valueOf(((CarDetailsModel) carPickupData.get(i)).getPickupdate()));
                        mapdata.put("pickupat", String.valueOf(((CarDetailsModel) carPickupData.get(i)).getPickupat()));
                        mapdata.put("dropat", String.valueOf(((CarDetailsModel) carPickupData.get(i)).getDropat()));
                        mapdata.put("pickuptime", String.valueOf(((CarDetailsModel) carPickupData.get(i)).getPickuptime()));
                        mapdata.put("releasetime", String.valueOf(((CarDetailsModel) carPickupData.get(i)).getReleasetime()));
                        mapdata.put("comment", String.valueOf(((CarDetailsModel) carPickupData.get(i)).getComment()));
                        carDetailChildItem.add(mapdata);
                        carDetailsArrayList.put(listDataHeader.get(0), carDetailChildItem);
                    }

                    if (isCarPickupButtonClicked) {
                        carPickUpListAdapter = new CarPickUpListAdapter(getActivity(), listDataHeader, carDetailsArrayList);
                        carPickupListView.setAdapter(carPickUpListAdapter);
                        carPickupListView.setVisibility(View.VISIBLE);
                    } else {
                        carPickUpListAdapter.notifyDataSetChanged();
                        isCarPickupButtonClicked = true;
                    }
                } else {
                    carPickUpListAdapter = new CarPickUpListAdapter(getActivity(), listDataHeader, carDetailsArrayList);
                    carPickupListView.setAdapter(carPickUpListAdapter);
                    carPickupListView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }


    private void prepareAdvanceList() {
        AdvanceAsynTask advanceAsynTask = new AdvanceAsynTask();
        advanceAsynTask.execute();
    }

    class AdvanceAsynTask extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... voids) {
            advanceData = DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().getAdvanceData();
            return advanceData;
        }

        @Override
        protected void onPostExecute(List advanceData) {
            super.onPostExecute(advanceData);

            try {
                listDataHeader = new ArrayList<>();
                listDataHeader.add("Advance Details");
                HashMap<String, String> mapdata;
                if (advanceData.size() > 0) {
                    for (int i = 0; i < advanceData.size(); i++) {
                        mapdata = new HashMap<>();

                        mapdata.put("id", String.valueOf(((AdvanceModel) advanceData.get(i)).getId()));
                        mapdata.put("amount", String.valueOf(((AdvanceModel) advanceData.get(i)).getAmount()));
                        mapdata.put("currency", String.valueOf(((AdvanceModel) advanceData.get(i)).getCurrency()));
                        mapdata.put("paymode", String.valueOf(((AdvanceModel) advanceData.get(i)).getPaymode()));
                        mapdata.put("remarks", String.valueOf(((AdvanceModel) advanceData.get(i)).getRemarks()));
                        mapdata.put("currencyid", String.valueOf(((AdvanceModel) advanceData.get(i)).getCurrencyid()));
                        mapdata.put("paymodeid", String.valueOf(((AdvanceModel) advanceData.get(i)).getPaymodeid()));
                        advanceChildItem.add(mapdata);
                        advanceArrayList.put(listDataHeader.get(0), advanceChildItem);
                    }

                    if (isAdvanceButtonClicked) {
                        advanceListAdapter = new AdvanceListAdapter(getActivity(), listDataHeader, advanceArrayList);
                        advanceListView.setAdapter(advanceListAdapter);
                        advanceListView.setVisibility(View.VISIBLE);
                    } else {
                        advanceListAdapter.notifyDataSetChanged();
                        isAdvanceButtonClicked = true;
                    }
                } else {
                    advanceListAdapter = new AdvanceListAdapter(getActivity(), listDataHeader, advanceArrayList);
                    advanceListView.setAdapter(advanceListAdapter);
                    advanceListView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

/*    private void prepareAccommodationList() {

        AccomodationAsynTask accomodationAsynTask = new AccomodationAsynTask();
        accomodationAsynTask.execute();
    }

    class AccomodationAsynTask extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... voids) {
            accommodationData = DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().getaccommodationData();
            Log.d(TAG, "run: " + accommodationData);

            return accommodationData;
        }

        @Override
        protected void onPostExecute(List accommodationData) {
            super.onPostExecute(accommodationData);

            try {
                listDataHeader = new ArrayList<>();
                listDataHeader.add("Accommodation Details");
                HashMap<String, String> mapdata;
                if (accommodationData.size() > 0) {
                    for (int i = 0; i < accommodationData.size(); i++) {
                        mapdata = new HashMap<>();

                        mapdata.put("id", String.valueOf(((AccommodationModel) accommodationData.get(i)).getId()));
                        mapdata.put("city", String.valueOf(((AccommodationModel) accommodationData.get(i)).getCity()));
                        mapdata.put("fromdate", String.valueOf(((AccommodationModel) accommodationData.get(i)).getFromDate()));
                        mapdata.put("todate", String.valueOf(((AccommodationModel) accommodationData.get(i)).getTodate()));
                        mapdata.put("checkintime", String.valueOf(((AccommodationModel) accommodationData.get(i)).getCheckintime()));
                        mapdata.put("checkouttime", String.valueOf(((AccommodationModel) accommodationData.get(i)).getCheckouttime()));
                        mapdata.put("hotellocation", String.valueOf(((AccommodationModel) accommodationData.get(i)).getHotellocation()));

                        accommodationChildItem.add(mapdata);
                        accommodationArrayList.put(listDataHeader.get(0), accommodationChildItem);
                    }

                    if (isNewAccommodationAdd) {
                        accommodationListAdapter = new AccommodationListAdapter(getActivity(), listDataHeader, accommodationArrayList);
                        accommodationListView.setAdapter(accommodationListAdapter);
                        accommodationListView.setVisibility(View.VISIBLE);
                    } else {
                        accommodationListAdapter.notifyDataSetChanged();
                    }
                } else {
                    accommodationListAdapter = new AccommodationListAdapter(getActivity(), listDataHeader, accommodationArrayList);
                    accommodationListView.setAdapter(accommodationListAdapter);
                    accommodationListView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }*/


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addPassenger:

                BackgroundAsynTask backgroundAsynTask = new BackgroundAsynTask();
                backgroundAsynTask.execute();


                break;
            case R.id.btn_addItenerary:
                if (travelWay.equals("One Way")) {
                    if (fromDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Travel Start Date");
                    } else {
                        isNewIteneraryAdd = false;
                        Bundle bundle = new Bundle();
                        bundle.putString("TRAVEL_TYPE", travelType);
                        bundle.putString("Travel_Way", travelWay);
                        bundle.putString("ItenerarySize", String.valueOf(iteneraryChildItem.size()));
                        bundle.putString("startDate", fromDate.getText().toString());
                        bundle.putString("endDate", endDate.getText().toString());
                        Intent intent = new Intent(getActivity(), AddIteneraryActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    if (fromDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Travel Start Date");
                    } else if (endDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please select Travel End Date");
                    } else {
                        isNewIteneraryAdd = false;
                        Bundle bundle = new Bundle();
                        bundle.putString("TRAVEL_TYPE", travelType);
                        bundle.putString("Travel_Way", travelWay);
                        bundle.putString("ItenerarySize", String.valueOf(iteneraryChildItem.size()));
                        bundle.putString("startDate", fromDate.getText().toString());
                        bundle.putString("endDate", endDate.getText().toString());
                        Intent intent = new Intent(getActivity(), AddIteneraryActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }

                break;
        /*    case R.id.btn_addAccommodation:


                if (travelWay.equals("One Way")) {
                    if (fromDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Travel Start Date");
                    } else {
                        isNewAccommodationAdd = false;
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Travel_Way", travelWay);
                        bundle1.putString("startDate", fromDate.getText().toString());
                        bundle1.putString("endDate", endDate.getText().toString());
                        Intent intent1 = new Intent(getActivity(), AddAccommodationActivity.class);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                    }
                } else {
                    if (fromDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Travel Start Date");
                    } else if (endDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please select Travel End Date");
                    } else {
                        isNewAccommodationAdd = false;
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Travel_Way", travelWay);
                        bundle1.putString("startDate", fromDate.getText().toString());
                        bundle1.putString("endDate", endDate.getText().toString());
                        Intent intent1 = new Intent(getActivity(), AddAccommodationActivity.class);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                    }
                }
                break;*/

            case R.id.btn_addAdvance:
                isAdvanceButtonClicked = false;
                Bundle bundle1 = new Bundle();
                bundle1.putString("TRAVEL_TYPE", travelType);
                Intent intent1 = new Intent(getActivity(), AddAdvanceActivity.class);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;

            case R.id.btn_addCarPickUp:
                isCarPickupButtonClicked = false;
                Bundle bundle = new Bundle();
                bundle.putString("From_Date", fromDate.getText().toString());

                Intent intent = new Intent(getActivity(), AddCarPickUpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.travelRequestFromDate:
                fromDate.setText("");
                calanderHRMS.datePicker(fromDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = fromDate.getText().toString().trim();
                            TO_DATE = endDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE);
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
                break;
            case R.id.travelRequestEndDate:
                endDate.setText("");
                calanderHRMS.datePicker(endDate);

                handler = new Handler();
                rRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FROM_DATE = fromDate.getText().toString().trim();
                            TO_DATE = endDate.getText().toString().trim();

                            if (!FROM_DATE.equals("") && !TO_DATE.equals("")) {
                                getCompareTodayDate(FROM_DATE, TO_DATE);
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
                break;
            case R.id.saveButton:

                if (travelWay.equals("One Way")) {
                    if (fromDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Travel start Date");
                    } else if (projectId.equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Project Name");
                    } else if (currencyPostion == 0) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Currency");
                    } else if (travelReason.getText().toString().trim().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                    } else if (travelReason.getText().toString().trim().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                    } else {

                        passengerXML = passangerDetailsAdapter.getPassengerStringData();
                        itineraryXML = iteneraryDetailsAdapter.getItineraryStringData();
                        /*if (isAccommodationChecked) {
                            accommodationXML = accommodationListAdapter.getAccommodationStringData();

                        }*/
                        if (isAdvanceChecked) {
                            advanceXML = advanceListAdapter.getAdvanceStringData();
                        }
                        if (isCarPickupChecked) {
                            carpickupXML = carPickUpListAdapter.getCarPickupStringData();
                        }

                        if (passengerXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Passanger Details Required !");
                        } else if (itineraryXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Itinerary Details Required !");
                        } /*else if (isAccommodationChecked && accommodationXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Accommodation Details Required !");
                        }*/ else if (isAdvanceChecked && advanceXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Advance Details Required !");
                        } else if (isCarPickupChecked && carpickupXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Car Pickup Details Required !");
                        } else {
                            sendSaveRequest(employeeId, travelType, travelWay, requestId, fromDate.getText().toString().replace("/", "-"), "", projectId, txt_budgetedAmount.getText().toString().trim(), currencyId, travelReason.getText().toString(), passengerXML, itineraryXML, accommodationXML, advanceXML, carpickupXML);
                        }
                    }

                } else {

                    if (fromDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Travel start Date");
                    } else if (endDate.getText().toString().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Travel End Date");
                    } else if (projectId.equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Project Name");
                    } else if (currencyPostion == 0) {
                        Utilities.showDialog(coordinatorLayout, "Please Select Currency");
                    } else if (travelReason.getText().toString().trim().equals("")) {
                        Utilities.showDialog(coordinatorLayout, "Please Enter Reason");
                    } else {

                        passengerXML = passangerDetailsAdapter.getPassengerStringData();
                        itineraryXML = iteneraryDetailsAdapter.getItineraryStringData();
                       /* if (isAccommodationChecked) {
                            accommodationXML = accommodationListAdapter.getAccommodationStringData();
                        }*/
                        if (isAdvanceChecked) {
                            advanceXML = advanceListAdapter.getAdvanceStringData();
                        }
                        if (isCarPickupChecked) {
                            carpickupXML = carPickUpListAdapter.getCarPickupStringData();
                        }
                        if (passengerXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Passanger Details Required !");
                        } else if (itineraryXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Itinerary Details Required !");
                        } /*else if (isAccommodationChecked && accommodationXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Accommodation Details Required !");
                        }*/ else if (isAdvanceChecked && advanceXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Advance Details Required !");
                        } else if (isCarPickupChecked && carpickupXML.equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Car Pickup Details Required !");
                        } else {
                            sendSaveRequest(employeeId, travelType, travelWay, requestId, fromDate.getText().toString().replace("/", "-"), endDate.getText().toString().replace("/", "-"), projectId, txt_budgetedAmount.getText().toString().trim(), currencyId, travelReason.getText().toString(), passengerXML, itineraryXML, accommodationXML, advanceXML, carpickupXML);
                        }
                    }
                }
                break;
            case R.id.clearButton:
                deleteAllData();
                fromDate.setText("");
                endDate.setText("");
                //projectSpinner.setSelection(0);
                currencySpinner.setSelection(2);
                travelReason.setText("");
                autoCompleteTextView.setText("");

                if (iteneraryChildItem.size() > 0) {
                    iteneraryChildItem.clear();
                    iteneraryDetailsAdapter.notifyDataSetChanged();
                }
               /* if (accommodationChildItem.size() > 0) {
                    accommodationChildItem.clear();
                    accommodationListAdapter.notifyDataSetChanged();
                }*/
                if (advanceChildItem.size() > 0) {
                    advanceChildItem.clear();
                    advanceListAdapter.notifyDataSetChanged();
                }
                if (carDetailChildItem.size() > 0) {
                    carDetailChildItem.clear();
                    carPickUpListAdapter.notifyDataSetChanged();
                }
                break;
        }
    }


    private void sendSaveRequest(String employeeId, String travelType, String travelWay, String requestId, String startdate, String enddate, final String projectId, String budget, String currencyId, final String reason, String passengerXML, String itineraryXML, String accommodationXML, String advanceXML, String carpickupXML) {
        if (Utilities.isNetworkAvailable(getActivity())) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Sending Travel Request...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String SAVE_REQUEST_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTravelRequestFicci";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            try {
                params_final.put("EMPLOYEE_ID", employeeId);
                params_final.put("economictype", economyType);
                params_final.put("traveltype", travelType);
                params_final.put("itinerarytype", travelWay);
                params_final.put("travelrequestid", "0");
                params_final.put("startDate", startdate);
                params_final.put("endDate", enddate);
                params_final.put("projectid", projectId);
                params_final.put("budget", budget);
                params_final.put("currencyid", currencyId);
                params_final.put("reason", reason.replaceAll("\\s", "_"));
                params_final.put("passengerxmlRequest", passengerXML);
                params_final.put("itineraryxmlRequest", itineraryXML);
                params_final.put("accomodationxmlRequest", accommodationXML);
                params_final.put("advancexmlRequest", advanceXML);
                params_final.put("CarpickupxmlRequest", carpickupXML);
                pm.put("objTravelRequestFicciObjectInfo", params_final);

            } catch (Exception e) {
                e.printStackTrace();
            }


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SAVE_REQUEST_URL, pm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        int result = Integer.valueOf(response.getString("SaveTravelRequestFicciResult"));

                        if (result > 0) {
                            Utilities.showDialog(coordinatorLayout, "Trave Request sent successfully");
                            deleteAllData();
                            fromDate.setText("");
                            endDate.setText("");
                            projectSpinner.setSelection(0);
                            currencySpinner.setSelection(2);
                            travelReason.setText("");
                            autoCompleteTextView.setText("");
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Error During Sending Travel Request!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    public void getCompareTodayDate(String From_date, String To_date) {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String From_Date = From_date;
                String To_Date = To_date;

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/Compare_Date";
                final JSONObject pm = new JSONObject();
                JSONObject params_final = new JSONObject();

                params_final.put("From_Date", From_Date);
                params_final.put("To_Date", To_Date);


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
                                    boolean resultDate = response.getBoolean("Compare_DateResult");
                                    if (!resultDate) {
                                        compareDateTextview.setText("From Date should be less than or equal To Date!");
                                        compareLinearLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        compareLinearLayout.setVisibility(View.GONE);
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
                    }
                });

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

    private void setListViewHeight1(ExpandableListView listView, int group) {

        PassangerDetailsAdapter listAdapter = (PassangerDetailsAdapter) listView.getExpandableListAdapter();

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();
            if (((listView.isGroupExpanded(i)) && (i != group)) || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null, listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setListViewHeight2(ExpandableListView listView, int group) {
        IteneraryDetailsAdapter listAdapter = (IteneraryDetailsAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);
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
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setListViewHeight3(ExpandableListView listView, int group) {

        AccommodationListAdapter listAdapter = (AccommodationListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);
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
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setListViewHeight4(ExpandableListView listView, int group) {

        AdvanceListAdapter listAdapter = (AdvanceListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);
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
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setListViewHeight5(ExpandableListView listView, int group) {

        CarPickUpListAdapter listAdapter = (CarPickUpListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);
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
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public class BackgroundAsynTask extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... voids) {
            passengerData = DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().getPassengerData();
            return passengerData;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);

            if (list.size() != 0) {
                emptype = "";
                for (int i = 0; i < list.size(); i++) {
                    if (((PassengerModel) list.get(i)).getEmployeetype().equals("Self"))
                        emptype = ((PassengerModel) list.get(i)).getEmployeetype();
                }

                isNewPassengerAdd = false;
                Bundle bundleString = new Bundle();
                bundleString.putString("emptype", emptype);
                bundleString.putString("listsize", String.valueOf(list.size()));
                Intent intentData = new Intent(getActivity(), AddPassengerActivity.class);
                intentData.putExtras(bundleString);
                startActivity(intentData);

            } else {

                isNewPassengerAdd = false;
                Bundle bundleString = new Bundle();
                bundleString.putString("emptype", " ");
                Intent intentData = new Intent(getActivity(), AddPassengerActivity.class);
                intentData.putExtras(bundleString);
                startActivity(intentData);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        emptype = "";

        if (!isNewPassengerAdd) {
            childItemList.clear();
            preparePassengerList();
        }
        if (!isNewIteneraryAdd) {
            iteneraryChildItem.clear();
            prepareIteneraryList();
        }
        /*if (isNewAccommodationAdd == false) {
            accommodationChildItem.clear();
            prepareAccommodationList();
        }*/
        if (!isAdvanceButtonClicked) {
            advanceChildItem.clear();
            prepareAdvanceList();
        }
        if (!isCarPickupButtonClicked) {
            carDetailChildItem.clear();
            prepareCarDetailList();
        }
    }

    public void deleteAllData() {
        android.os.AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllPassengerDetail();
                DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllItineraryData();
                // DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllAccommodtionData();
                DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllAdvanceData();
                DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().removeAllCarDetail();
            }
        });
    }

    private void prepareIteneraryList() {
        IteneraryAsynTask iteneraryAsynTask = new IteneraryAsynTask();
        iteneraryAsynTask.execute();
    }

    class IteneraryAsynTask extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... voids) {
            iteneraryData = DatabaseClient.getInstance(getActivity()).getAppDatabase().passengerDao().getIteneraryData();
            return iteneraryData;
        }

        @Override
        protected void onPostExecute(List iteneraryData) {
            super.onPostExecute(iteneraryData);

            try {

                listDataHeader = new ArrayList<>();
                listDataHeader.add("Itenerary Details");
                HashMap<String, String> mapdata;
                if (iteneraryData.size() > 0) {

                    if (((IteneraryModel) iteneraryData.get(0)).getClasscode().equals("Economy")) {
                        economyType = "0";
                    } else {
                        economyType = "1";
                    }
                    if (travelWay.equals("Round Trip")) {
                        for (int i = 0; i < iteneraryData.size() + 1; i++) {
                            mapdata = new HashMap<>();
                            mapdata.put("id", String.valueOf(((IteneraryModel) iteneraryData.get(0)).getId()));
                            mapdata.put("source", ((IteneraryModel) iteneraryData.get(0)).getSource());
                            mapdata.put("destination", ((IteneraryModel) iteneraryData.get(0)).getDestination());
                            mapdata.put("departdate", ((IteneraryModel) iteneraryData.get(0)).getDeparturedate());
                            mapdata.put("returndate", ((IteneraryModel) iteneraryData.get(0)).getReturndate());
                            mapdata.put("mode", ((IteneraryModel) iteneraryData.get(0)).getMode());
                            mapdata.put("class", ((IteneraryModel) iteneraryData.get(0)).getClasscode());
                            mapdata.put("starttime", ((IteneraryModel) iteneraryData.get(0)).getStarttime());
                            mapdata.put("endtime", ((IteneraryModel) iteneraryData.get(0)).getEndtime());
                            mapdata.put("sourceid", ((IteneraryModel) iteneraryData.get(0)).getSourceid());
                            mapdata.put("destinationid", ((IteneraryModel) iteneraryData.get(0)).getDestinationid());
                            mapdata.put("modeid", ((IteneraryModel) iteneraryData.get(0)).getModeid());
                            mapdata.put("classid", ((IteneraryModel) iteneraryData.get(0)).getClassid());
                            mapdata.put("travelwaytype", ((IteneraryModel) iteneraryData.get(0)).getTravelwaytype());

                            mapdata.put("fdetail", ((IteneraryModel) iteneraryData.get(0)).getFlightdetail());
                            mapdata.put("seatprefid", ((IteneraryModel) iteneraryData.get(0)).getSeatprefid());
                            mapdata.put("frequentflier", ((IteneraryModel) iteneraryData.get(0)).getFrequentlyFillerno());
                            mapdata.put("specialrequest", ((IteneraryModel) iteneraryData.get(0)).getSpecialRequest());
                            mapdata.put("insurancevalue", ((IteneraryModel) iteneraryData.get(0)).isInsuranceValue());

                            iteneraryChildItem.add(mapdata);
                        }
                        iteneraryArrayList.put(listDataHeader.get(0), iteneraryChildItem);
                    } else {
                        for (int i = 0; i < iteneraryData.size(); i++) {
                            mapdata = new HashMap<>();
                            mapdata.put("id", String.valueOf(((IteneraryModel) iteneraryData.get(i)).getId()));
                            mapdata.put("source", ((IteneraryModel) iteneraryData.get(i)).getSource());
                            mapdata.put("destination", ((IteneraryModel) iteneraryData.get(i)).getDestination());
                            mapdata.put("departdate", ((IteneraryModel) iteneraryData.get(i)).getDeparturedate());
                            mapdata.put("returndate", ((IteneraryModel) iteneraryData.get(i)).getReturndate());
                            mapdata.put("mode", ((IteneraryModel) iteneraryData.get(i)).getMode());
                            mapdata.put("class", ((IteneraryModel) iteneraryData.get(i)).getClasscode());
                            mapdata.put("starttime", ((IteneraryModel) iteneraryData.get(i)).getStarttime());
                            mapdata.put("endtime", ((IteneraryModel) iteneraryData.get(i)).getEndtime());
                            mapdata.put("sourceid", ((IteneraryModel) iteneraryData.get(i)).getSourceid());
                            mapdata.put("destinationid", ((IteneraryModel) iteneraryData.get(i)).getDestinationid());
                            mapdata.put("modeid", ((IteneraryModel) iteneraryData.get(i)).getModeid());
                            mapdata.put("classid", ((IteneraryModel) iteneraryData.get(i)).getClassid());
                            mapdata.put("travelwaytype", ((IteneraryModel) iteneraryData.get(i)).getTravelwaytype());

                            mapdata.put("fdetail", ((IteneraryModel) iteneraryData.get(0)).getFlightdetail());
                            mapdata.put("seatprefid", ((IteneraryModel) iteneraryData.get(0)).getSeatprefid());
                            mapdata.put("frequentflier", ((IteneraryModel) iteneraryData.get(0)).getFrequentlyFillerno());
                            mapdata.put("specialrequest", ((IteneraryModel) iteneraryData.get(0)).getSpecialRequest());
                            mapdata.put("insurancevalue", ((IteneraryModel) iteneraryData.get(0)).isInsuranceValue());
                            iteneraryChildItem.add(mapdata);
                        }
                        iteneraryArrayList.put(listDataHeader.get(0), iteneraryChildItem);
                    }
                    if (!isNewIteneraryAdd) {
                        isNewIteneraryAdd = true;
                        iteneraryDetailsAdapter = new IteneraryDetailsAdapter(getActivity(), listDataHeader, iteneraryArrayList, travelWay);
                        itenerayListView.setAdapter(iteneraryDetailsAdapter);
                        itenerayListView.setVisibility(View.VISIBLE);
                    }
                } else {
                    //zero size list
                    iteneraryDetailsAdapter = new IteneraryDetailsAdapter(getActivity(), listDataHeader, iteneraryArrayList, travelWay);
                    itenerayListView.setAdapter(iteneraryDetailsAdapter);
                    itenerayListView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
            }
        }
    }

    private void getCurrencyList() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String CURRENCY_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetCurrencyDataFicci";

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CURRENCY_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    try {
                        HashMap<String, String> hashMap;
                        currencyList = new ArrayList<>();
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                hashMap = new HashMap<>();
                                hashMap.put("KEY", response.getJSONObject(i).getString("TCM_ID"));
                                hashMap.put("VALUE", response.getJSONObject(i).getString("TCM_CURRENCY_NAME"));
                                currencyList.add(hashMap);
                            }

                            currencySpinnerAdapter = new CurrencySpinnerAdapter(getActivity(), currencyList);
                            currencySpinner.setAdapter(currencySpinnerAdapter);
                            currencySpinner.setSelection(2);
                            currencySpinner.setEnabled(false);
                        } else {
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
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
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
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    private void getProjectSpinnerData() {
        Log.i("TAG", "getProjectSpinnerData: ");

        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String PROJECT_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetProjectDataFicci";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, PROJECT_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    try {
                        HashMap<String, String> hashMap;
                        projectArrayList = new ArrayList<>();
                        teamArray = new ArrayList<>();
                        progressDialog.dismiss();
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                hashMap = new HashMap<>();
                                hashMap.put("KEY", response.getJSONObject(i).getString("TPM_ID"));
                                hashMap.put("VALUE", response.getJSONObject(i).getString("TPM_PROJECT_NAME"));
                                teamArray.add(response.getJSONObject(i).getString("TPM_PROJECT_NAME"));
                                projectArrayList.add(hashMap);
                            }


                            autoCompleteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, teamArray) {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);
                                    ((TextView) v).setTextSize(12);
                                    ((TextView) v).setGravity(Gravity.LEFT);
                                    return v;
                                }
                            };
                            autoCompleteTextView.setAdapter(autoCompleteAdapter);

                          /*  newCustomSpinnerAdapter = new NewCustomSpinnerAdapter(getActivity(), projectArrayList);
                            projectSpinner.setAdapter(newCustomSpinnerAdapter);*/
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.DATA_ERROR);
                            progressDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    progressDialog.dismiss();
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
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }


    }

    private void getBudgetedAmount(String projectId) {

        if (Utilities.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String BUDGETED_AMOUNT_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetProjectBudgetAmountFicci/" + projectId;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, BUDGETED_AMOUNT_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        progressDialog.dismiss();
                        response = response.replaceAll("^\"|\"$", "");

                        StringTokenizer tokens = new StringTokenizer(response, "@");
                        String amount = tokens.nextToken();
                        String eventStart = tokens.nextToken();
                        String eventEnd = tokens.nextToken();
                        txt_budgetedAmount.setText(amount);
                        eventStartDate.setText(eventStart.replace("\\", ""));
                        eventEndDate.setText(eventEnd.replace("\\", ""));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
                    progressDialog.dismiss();
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
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } else {
            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (passengerHandler != null) {
            passengerHandler.removeCallbacks(prepareListRunnable);
        }
    }
}

