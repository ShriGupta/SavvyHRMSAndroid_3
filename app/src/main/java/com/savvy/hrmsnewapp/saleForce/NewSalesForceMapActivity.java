package com.savvy.hrmsnewapp.saleForce;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.SaleForceAdapter.SaleForceMasterAdapter;
import com.savvy.hrmsnewapp.activity.DashBoardActivity;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.service.TrackGPS;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.LogMaintainance;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by savvy on 1/30/2018.
 */

public class NewSalesForceMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, View.OnClickListener, GoogleMap.OnMarkerDragListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static long count = 0;
    private GoogleMap mMap;
    ImageView syncImg;
    SaleForceMasterAdapter mAdapter;

    CoordinatorLayout coordinatorLayout;

    ArrayList<HashMap<String,String>> arlDataIndustry;
    ArrayList<HashMap<String,String>> arlDataTerritory;
    ArrayList<HashMap<String,String>> arlDataCustomer;
    public static ArrayList<String> Custmer_Name;

    String INDUSRTY_ID = "", INDUSTRY_NAME = "";
    String LOCATION_ID = "", LOCATION_NAME = "";

    GoogleApiClient googleApiClient;
    private CustomTextView buttonSave;
    private CustomTextView buttonCurrent;
    private CustomTextView buttonView;
    FusedLocationProviderApi fusedLocationProviderApi;
//    Location mylocation;
    com.savvy.hrmsnewapp.service.TrackGPS track;
    private static double latitude;
    private static double longitude;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime = "";

    AutoCompleteTextView auto_customer_detail;
    HashMap<String,String> customerId_map;
    String CUST_NAME = "", CUST_ID = "";
    private static final long INTERVAL = 1000 * 60 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1;

    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    String SAVE_CUR_TYPE = "", SAVE_CUR_CUST_NAME = "",SAVE_CUR_LAT = "",SAVE_CUR_LOC_IDEN = "",SAVE_CUR_TERROTORY = "",SAVE_CUR_INDUSTRY = "";

    protected void createLocationRequest() {
        LogMaintainance.WriteLog("Start CreateLocationRequest ");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_force_map);

        arlDataIndustry = new ArrayList<HashMap<String, String>>();
        arlDataTerritory = new ArrayList<HashMap<String, String>>();
        arlDataCustomer = new ArrayList<HashMap<String, String>>();
        Custmer_Name = new ArrayList<String>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getIndustryDetails();
        getTerritoryDetails();
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        //Initializing views and adding onclick listeners
        buttonSave = (CustomTextView) findViewById(R.id.loc_newCustomer);
        buttonCurrent = (CustomTextView) findViewById(R.id.loc_Visit);
        buttonView = (CustomTextView) findViewById(R.id.loc_noScheduled);

        syncImg = (ImageView) findViewById(R.id.syncImg);

        buttonSave.setOnClickListener(this);
        buttonCurrent.setOnClickListener(this);
        buttonView.setOnClickListener(this);

        final RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(10);
        rotate.setInterpolator(new LinearInterpolator());

        syncImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncImg.startAnimation(rotate);
            }
        });

        track = new TrackGPS(NewSalesForceMapActivity.this);
        if(!track.canGetLocation()){
            track.showSettingsAlert();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();
        updateUI();

        assert getSupportActionBar()!=null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.loc_newCustomer){
            addNewCustomerDialog();
        }
        if(view.getId()==R.id.action_change_password){
            updateUI();
            moveMap();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        try {
            mMap.clear();

            //Adding a new marker to the current pressed position
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            updateUI();
            if (mCurrentLocation != null) {
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(""));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.setOnMarkerDragListener(this);
                mMap.setOnMapLongClickListener(this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getIndustryDetails(){
        try{

            if(Utilities.isNetworkAvailable(NewSalesForceMapActivity.this)) {
                arlDataIndustry = new ArrayList<HashMap<String, String>>();
                JSONObject param = new JSONObject();

                param.put("groupId", "1");
                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetIndustrySalesforcePost";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.e("Response", "Industry " + response.toString());
                                    try {
                                        JSONArray jsonArray = response.getJSONArray("GetIndustrySalesforcePostResult");

                                        HashMap<String, String> mapData;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            mapData = new HashMap<String, String>();

                                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                                            String IndustryId = jsonObject.getString("IndustryId");
                                            String IndustryName = jsonObject.getString("IndustryName");

                                            mapData.put("PositionId", IndustryId);
                                            mapData.put("PositionName", IndustryName);

                                            arlDataIndustry.add(mapData);
                                        }
//                                        mAdapter = new SaleForceMasterAdapter(SalesForceMapActivity.this,arlDataIndustry,"INDUSTRY");
//                                        spin_saveLoc_Industry.setAdapter(mAdapter);
                                        mAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
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
                });

                RequestQueue requestQueue = Volley.newRequestQueue(NewSalesForceMapActivity.this);
                RetryPolicy policy = new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else{
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getTerritoryDetails(){
        try{
            if(Utilities.isNetworkAvailable(NewSalesForceMapActivity.this)){
                JSONObject param = new JSONObject();

                param.put("groupId","1");
                String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetLocationSalesforcePost";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    Log.e("Response","Location "+response.toString());
                                    JSONArray jsonArray = response.getJSONArray("GetLocationSalesforcePostResult");
                                    HashMap<String,String> mapData;
                                    for(int i=0;i<jsonArray.length();i++){
                                        mapData = new HashMap<String, String>();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String Location_Id = jsonObject.getString("Location_Id");
                                        String Location_Name = jsonObject.getString("Location_Name");

                                        mapData.put("PositionId",Location_Id);
                                        mapData.put("PositionName",Location_Name);

                                        arlDataTerritory.add(mapData);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(NewSalesForceMapActivity.this);
                RetryPolicy policy = new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
//                SavvyHRMSApplication.getInstance().addToRequestQueue(jsonObjectRequest);
            } else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        try{
            latitude = marker.getPosition().latitude;
            longitude = marker.getPosition().longitude;

            //Moving the map
            moveMap();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void moveMap() {
        try {
            if (count != 0) {
                mMap.clear();
            }
            count++;

            LatLng latLng1 = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            Log.e("CurrentM Location", "" + mCurrentLocation.getLongitude() + "," + mCurrentLocation.getLongitude());

            mMap.addMarker(new MarkerOptions().position(latLng1).draggable(true).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            mMap.setOnMarkerDragListener(this);
            mMap.setOnMapLongClickListener(this);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addNewCustomerDialog(){
        try{
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.save_location_dialog);
            dialog.setTitle("Save Location");
            dialog.setCancelable(false);

            Button btn_Save = dialog.findViewById(R.id.btn_SaveLoc_Save);
            Button btn_Cancel = dialog.findViewById(R.id.btn_SaveLoc_Cancel);

            auto_customer_detail = dialog.findViewById(R.id.av_custVisit_Name);

            final Spinner spin_saveLoc_Type = dialog.findViewById(R.id.spin_saveLoc_Type);
            final Spinner spin_saveLoc_Territory = dialog.findViewById(R.id.spin_saveLoc_Territory);
            final Spinner spin_saveLoc_Industry = dialog.findViewById(R.id.spin_saveLoc_Industry);

            final EditText edt_saveLoc_EmpName = dialog.findViewById(R.id.edt_saveLoc_EmpName);
            final CustomTextView edt_saveLoc_LatLong = dialog.findViewById(R.id.edt_saveLoc_LatLong);
            final EditText edt_saveLoc_LocIdentifier = dialog.findViewById(R.id.edt_saveLoc_LocIdentifier);

            final LinearLayout linear_Type = dialog.findViewById(R.id.linear_Type);
            final LinearLayout linear_customerDetail = dialog.findViewById(R.id.linear_customerDetail );
            final LinearLayout linear_LatLong = dialog.findViewById(R.id.linear_LatLong);
            final LinearLayout linear_locIdentifier = dialog.findViewById(R.id.linear_locIdentifier);
            final LinearLayout linear_territory = dialog.findViewById(R.id.linear_territory);
            final LinearLayout linear_industry = dialog.findViewById(R.id.linear_industry);

            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            String[] array = {"Select Type","New Customer","Existing Customer","Office","Home"};

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,array);

            spin_saveLoc_Type.setAdapter(arrayAdapter);
//            spin_saveLoc_Territory.setAdapter(arrayAdapter1);

            mAdapter = new SaleForceMasterAdapter(NewSalesForceMapActivity.this,arlDataIndustry,"INDUSTRY");
            spin_saveLoc_Industry.setAdapter(mAdapter);

            mAdapter = new SaleForceMasterAdapter(NewSalesForceMapActivity.this,arlDataTerritory,"LOCATION");
            spin_saveLoc_Territory.setAdapter(mAdapter);

            linear_Type.setVisibility(View.VISIBLE);
            linear_customerDetail.setVisibility(View.GONE);
            linear_LatLong.setVisibility(View.VISIBLE);
            linear_locIdentifier.setVisibility(View.GONE);
            linear_territory.setVisibility(View.VISIBLE);
            linear_industry.setVisibility(View.VISIBLE);

            spin_saveLoc_Type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==1){

                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.VISIBLE);
                        linear_LatLong.setVisibility(View.VISIBLE);
                        linear_locIdentifier.setVisibility(View.VISIBLE);
                        linear_territory.setVisibility(View.VISIBLE);
                        linear_industry.setVisibility(View.VISIBLE);

                        edt_saveLoc_LatLong.setEnabled(false);
                    } else if(position==2){
                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.VISIBLE);
                        linear_LatLong.setVisibility(View.VISIBLE);
                        linear_locIdentifier.setVisibility(View.GONE);
                        linear_territory.setVisibility(View.GONE);
                        linear_industry.setVisibility(View.GONE);

                        edt_saveLoc_LatLong.setEnabled(false);
                    } else if(position==3){
                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.GONE);
                        linear_LatLong.setVisibility(View.VISIBLE);
                        linear_locIdentifier.setVisibility(View.GONE);
                        linear_territory.setVisibility(View.GONE);
                        linear_industry.setVisibility(View.GONE);

                        edt_saveLoc_LatLong.setEnabled(false);
                    } else if(position==4){
                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.GONE);
                        linear_LatLong.setVisibility(View.VISIBLE);
                        linear_locIdentifier.setVisibility(View.GONE);
                        linear_territory.setVisibility(View.GONE);
                        linear_industry.setVisibility(View.GONE);

                        edt_saveLoc_LatLong.setEnabled(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            try {
                if(mCurrentLocation!=null){
                    double lat = mCurrentLocation.getLatitude();
                    double lng = mCurrentLocation.getLongitude();

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.UK);
                    List<Address> addresses;

                    addresses = geocoder.getFromLocation(lat, lng, 1);
                    if (addresses.size() > 0) {
                        String cityName = addresses.get(0).getLocality() + "," + addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getPostalCode();
                        edt_saveLoc_LatLong.setText(cityName);
                    } else {
                        edt_saveLoc_LatLong.setText("Lat : " + lat + "\nLon : " + lng);
                    }
//                    Toast.makeText(getApplicationContext(), "Latitude " + lat + " Longitude " + lng, Toast.LENGTH_LONG).show();
                } else{
//                    getMyLocation();
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            btn_Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String SpinSaveType = spin_saveLoc_Type.getSelectedItem().toString();
                    Log.e("Spin Save Location",SpinSaveType);
                    if(SpinSaveType.toUpperCase().trim().equals("SELECT TYPE")){
                        Toast.makeText(getApplicationContext(),"Please Select Any Type",Toast.LENGTH_LONG).show();
                    } else  if(SpinSaveType.toUpperCase().trim().equals("NEW CUSTOMER")){
                        Log.e("New Customer",SpinSaveType);
                        edt_saveLoc_EmpName.setVisibility(View.VISIBLE);
                        auto_customer_detail.setVisibility(View.GONE);

                        SAVE_CUR_CUST_NAME = edt_saveLoc_EmpName.getText().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();
                        SAVE_CUR_LOC_IDEN = edt_saveLoc_LocIdentifier.getText().toString();

                        spin_saveLoc_Industry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    INDUSRTY_ID = "";
                                    INDUSTRY_NAME = "";
                                } else if (position > 0) {
                                    INDUSRTY_ID = arlDataIndustry.get(position - 1).get("PositionId");
                                    INDUSTRY_NAME = arlDataIndustry.get(position - 1).get("PositionName");
                                    Log.e("Postion ","Position of Spinner "+INDUSRTY_ID);
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });

                        spin_saveLoc_Territory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    LOCATION_ID = "";
                                    LOCATION_NAME = "";
                                } else if (position > 0) {
                                    LOCATION_ID = arlDataTerritory.get(position - 1).get("PositionId");
                                    LOCATION_NAME = arlDataTerritory.get(position - 1).get("PositionName");
                                    Log.e("Postion ","Position of Spinner "+LOCATION_ID);
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
//                        SAVE_CUR_TERROTORY = spin_saveLoc_Territory.getSelectedItem().toString();
//                        SAVE_CUR_INDUSTRY = spin_saveLoc_Industry.getSelectedItem().toString();

                        if(SAVE_CUR_CUST_NAME.equals("")){
                            Toast.makeText(getApplicationContext(),"Please Enter Customer Name",Toast.LENGTH_LONG).show();
                        } else if(SAVE_CUR_LOC_IDEN.equals("")){
                            Toast.makeText(getApplicationContext(),"Please Enter Location Identifier",Toast.LENGTH_LONG).show();
                        } else if(LOCATION_NAME.equals("SELECT TERRITORY")){
                            Toast.makeText(getApplicationContext(),"Please Select Territory",Toast.LENGTH_LONG).show();
                        } else if(INDUSTRY_NAME.equals("SELECT INDUSTRY")){
                            Toast.makeText(getApplicationContext(),"Please Enter Industory",Toast.LENGTH_LONG).show();
                        } else{
                            String lat = "0.0", lng = "0.0";
                            if(mCurrentLocation!=null){
                                lat = String.valueOf(mCurrentLocation.getLatitude());
                                lng = String.valueOf(mCurrentLocation.getLongitude());
                            }
                            SaveCustomerDetail(SAVE_CUR_CUST_NAME,"3",LOCATION_ID,SAVE_CUR_LOC_IDEN,
                                    INDUSRTY_ID,SAVE_CUR_LAT,String.valueOf(latitude),String.valueOf(longitude));
                            dialog.dismiss();
//                            HashMap<String,String> hashMap = new HashMap<>();
//                            hashMap.put("SAVE_CUR_TYPE",SAVE_CUR_TYPE);
//                            hashMap.put("SAVE_CUR_CUST_NAME",SAVE_CUR_CUST_NAME);
//                            hashMap.put("SAVE_CUR_LAT",SAVE_CUR_LAT);
//                            hashMap.put("SAVE_CUR_LOC_IDEN",SAVE_CUR_LOC_IDEN);
//                            hashMap.put("SAVE_CUR_TERROTORY",SAVE_CUR_TERROTORY);
//                            hashMap.put("SAVE_CUR_INDUSTRY",SAVE_CUR_INDUSTRY);
                        }
                    } else if(SpinSaveType.toUpperCase().trim().equals("EXISTING CUSTOMER")){
                        GetAllCustomerDetail();
                        Log.e("Existing Customer",SpinSaveType);
                        auto_customer_detail.setVisibility(View.VISIBLE);
                        edt_saveLoc_EmpName.setVisibility(View.GONE);

                        SAVE_CUR_TYPE = spin_saveLoc_Type.getSelectedItem().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();

                        auto_customer_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                customerId_map = arlDataCustomer.get(position);
                                CUST_NAME = customerId_map.get("customerName");
                                CUST_ID = customerId_map.get("customerId");
                            }
                        });

                        auto_customer_detail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if(!b) {
                                    // on focus off
                                    String str = auto_customer_detail.getText().toString();

                                    ListAdapter listAdapter = auto_customer_detail.getAdapter();
                                    for(int i = 0; i < listAdapter.getCount(); i++) {
                                        String temp = listAdapter.getItem(i).toString();
                                        if(str.compareTo(temp) == 0) {
                                            return;
                                        }
                                    }
                                    auto_customer_detail.setText("");
                                }
                            }
                        });

                        if(CUST_NAME.equals("")){
                            Toast.makeText(getApplicationContext(),"Please Enter Existing Customer Name",Toast.LENGTH_LONG).show();
                        } else{
                            String lat = "0.0", lng = "0.0";
                            if(mCurrentLocation!=null){
                                lat = String.valueOf(mCurrentLocation.getLatitude());
                                lng = String.valueOf(mCurrentLocation.getLongitude());
                            }
                            SaveCustomerDetail(CUST_NAME,CUST_ID," "," ",
                                    " ",SAVE_CUR_LAT,String.valueOf(latitude),String.valueOf(longitude));
                            dialog.dismiss();
//                            SaveCustomerDetail();
//                            HashMap<String,String> hashMap = new HashMap<>();
//                            hashMap.put("SAVE_CUR_TYPE",SAVE_CUR_TYPE);
//                            hashMap.put("SAVE_CUR_CUST_NAME",SAVE_CUR_CUST_NAME);
//                            hashMap.put("SAVE_CUR_LAT",SAVE_CUR_LAT);
                        }
                    }  else if(SpinSaveType.toUpperCase().trim().equals("OFFICE")){
                        Log.e("Office",SpinSaveType);
                        SAVE_CUR_TYPE = spin_saveLoc_Type.getSelectedItem().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();

                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("SAVE_CUR_TYPE",SAVE_CUR_TYPE);
                        hashMap.put("SAVE_CUR_CUST_NAME","-");
                        hashMap.put("SAVE_CUR_LAT",SAVE_CUR_LAT);

                    }  else if(SpinSaveType.toUpperCase().trim().equals("HOME")){
                        Log.e("Home",SpinSaveType);
                        SAVE_CUR_TYPE = spin_saveLoc_Type.getSelectedItem().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();

                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("SAVE_CUR_TYPE",SAVE_CUR_TYPE);
                        hashMap.put("SAVE_CUR_CUST_NAME","-");
                        hashMap.put("SAVE_CUR_LAT",SAVE_CUR_LAT);
                    }
                }
            });
            btn_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            if(mCurrentLocation!=null){
                dialog.show();
            } else{
                updateUI();
                Toast.makeText(this, "Location Not Found now, Please try Again.", Toast.LENGTH_SHORT).show();
//                getMyLocation();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try{
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            if(mCurrentLocation!=null) {
                latitude = mCurrentLocation.getLatitude();
                longitude = mCurrentLocation.getLongitude();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void startLocationUpdates() {
        try {
            if(!mGoogleApiClient.isConnected()){
                LogMaintainance.WriteLog("NewTrackServiceActivity-> StartLocationUpdates Google Api Client is not connected");
                mGoogleApiClient.connect();
            } else {
                if (mGoogleApiClient.isConnected()) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    LogMaintainance.WriteLog("NewTrackServiceActivity-> StartLocationUpdates start");
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if(mCurrentLocation!=null) {
                        LogMaintainance.WriteLog("NewTrackServiceActivity-> StartLocationUpdates getLastLocation "+mCurrentLocation.getLatitude()+", "+mCurrentLocation.getLongitude());
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home) {
            Intent intent = new Intent(this,DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId","1");
            intent.putExtras(bundle);
            startActivity(intent);
            count=0;
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        try {
            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            } else{
                if(mGoogleApiClient.isConnected()){
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
//                    startLocationUpdates();
//                    createLocationRequest();
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Update Initiate");

                    if (null != mCurrentLocation) {
                        LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Update Get Location "+mCurrentLocation.getLatitude());
                        String lat = String.valueOf(mCurrentLocation.getLatitude());
                        String lng = String.valueOf(mCurrentLocation.getLongitude());

                        latitude = mCurrentLocation.getLatitude();
                        longitude = mCurrentLocation.getLongitude();

                        Log.e("Location Update ", "At Time: " + mLastUpdateTime + "\n" +
                                "Latitude: " + lat + "\n" +
                                "Longitude: " + lng + "\n" +
                                "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                                "Provider: " + mCurrentLocation.getProvider());
//
                    } else {
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void GetAllCustomerDetail(){
        try{
            if(Utilities.isNetworkAvailable(NewSalesForceMapActivity.this)){
                try {
                    Custmer_Name = new ArrayList<>();
                    arlDataCustomer = new ArrayList<>();
                    JSONObject param = new JSONObject();

                    param.put("GROUP_ID","1");
                    param.put("USER_ID","3");

                    String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/GetCustomerSalesforcePost";

                    final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try{
                                        HashMap<String,String> hashMap;
                                        JSONArray jsonArray = response.getJSONArray("GetCustomerSalesforcePostResult");
                                        for (int i=0;i<jsonArray.length();i++){
                                            hashMap = new HashMap<>();

                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String customerId = jsonObject.getString("CustomerId");
                                            String customerName = jsonObject.getString("CustomerName");

                                            hashMap.put("customerId",customerId);
                                            hashMap.put("customerName",customerName);

                                            Custmer_Name.add(customerName);
                                            arlDataCustomer.add(hashMap);
                                        }
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, Custmer_Name);
                                        auto_customer_detail.setAdapter(adapter);
                                        Log.e("Customer Data","Data->"+arlDataCustomer.toString());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(NewSalesForceMapActivity.this);
                    RetryPolicy policy = new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    jsonObjectRequest.setRetryPolicy(policy);
                    requestQueue.add(jsonObjectRequest);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void SaveCustomerDetail(String Customer_Name,String Customer_Id,String loc_id,String landmark,String industry_id,String address,
                                   String Lat,String Lng){
        try{
            if(Utilities.isNetworkAvailable(getApplicationContext())){
                JSONObject param = new JSONObject();
                param.put("GROUP_ID","0");
                param.put("CUSTOMER_ID",Customer_Id);
                param.put("CUSTOMER_NAME",Customer_Name);
                param.put("CUSTOMER_ALIAS_NAME"," ");
                param.put("LOCATION_ID",loc_id);
                param.put("LOCATION_LANDMARK",landmark);
                param.put("IS_HEAD_OFFICE","1");
                param.put("INDUSTRY_ID",industry_id);
                param.put("ADDRESS",address);
                param.put("LAT",Lat);
                param.put("LONG",Lng);
                param.put("PHONE1"," ");
                param.put("PHONE2"," ");
                param.put("PHONE3"," ");
                param.put("EMAIL1"," ");
                param.put("EMAIL2"," ");
                param.put("WEBSITE"," ");
                param.put("STATUS","0");
                param.put("USER_ID","3");

                Log.e("ParamData","param->"+param.toString());

                String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/SaveCustomerSalesforcePost";
                Log.e("URL DATA","Url Data->"+url.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,param,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response){
                                Log.e("Response","Save Data->"+response.toString());
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(NewSalesForceMapActivity.this);
                RetryPolicy policy = new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } else{
                Utilities.showDialog(coordinatorLayout,ErrorConstants.NO_NETWORK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
