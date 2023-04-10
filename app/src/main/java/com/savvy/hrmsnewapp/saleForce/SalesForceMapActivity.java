package com.savvy.hrmsnewapp.saleForce;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
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
import android.widget.ProgressBar;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SalesForceMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        com.google.android.gms.location.LocationListener,
        GoogleMap.OnMapLongClickListener, View.OnClickListener {

    public static long count = 0;
    private GoogleMap mMap;
    private double longitude = 0.0;
    private double latitude = 0.0;
    LocationRequest mLocationRequest;
    Location mlocation;
    ProgressBar loc_progressDialog;
    ImageView syncImg;
    SaleForceMasterAdapter mAdapter;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    String empoyeeId = "";

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
    Location mylocation;
    //Add Customer Detail.....
    AutoCompleteTextView auto_customer_detail;
    Spinner spin_saveLoc_Type, spin_saveLoc_Territory, spin_saveLoc_Industry;
    EditText edt_saveLoc_EmpName, edt_saveLoc_LocIdentifier;
    CustomTextView edt_saveLoc_LatLong;
    //End
    HashMap<String,String> customerId_map;
    String CUST_NAME = "", CUST_ID = "";

    LinearLayout linear_fragmentContainer, linear_map_fragment;

    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    String SAVE_CUR_TYPE = "", SAVE_CUR_CUST_NAME = "",SAVE_CUR_LAT = "",SAVE_CUR_LOC_IDEN = "",SAVE_CUR_TERROTORY = "",SAVE_CUR_INDUSTRY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_force_map);

        arlDataIndustry = new ArrayList<HashMap<String, String>>();
        arlDataTerritory = new ArrayList<HashMap<String, String>>();
        arlDataCustomer = new ArrayList<HashMap<String, String>>();
        Custmer_Name = new ArrayList<String>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        getMyLocation();
        shared = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        googleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        //Initializing views and adding onclick listeners
        buttonSave = (CustomTextView) findViewById(R.id.loc_newCustomer);
        buttonCurrent = (CustomTextView) findViewById(R.id.loc_Visit);
        buttonView = (CustomTextView) findViewById(R.id.loc_noScheduled);
//        loc_progressDialog = (ProgressBar) findViewById(R.id.loc_progressDialog);

        linear_fragmentContainer = (LinearLayout)findViewById(R.id.linear_fragmentContainer);
        linear_map_fragment = (LinearLayout)findViewById(R.id.linear_map_fragment);

        linear_map_fragment.setVisibility(View.VISIBLE);
        linear_fragmentContainer.setVisibility(View.GONE);

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

//        loc_progressDialog.setProgressTintList(ColorStateList.valueOf(Color.RED));

//        if (loc_progressDialog != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                RotateDrawable rotateDrawable = (RotateDrawable) loc_progressDialog.getIndeterminateDrawable();
//                rotateDrawable.setToDegrees(0);
//            }
//        }
//        loc_progressDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loc_progressDialog.setMax(10);
//            }
//        });
        setUpGClient();

        assert getSupportActionBar()!=null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(SalesForceMapActivity.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();

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
////        buildLocationSettingsRequest();
////        createLocationRequest();
//        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(100);
//        locationRequest.setFastestInterval(100);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);

        mlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mlocation != null) {
            //Getting longitude and latitude
            longitude = mlocation.getLongitude();
            latitude = mlocation.getLatitude();

            //moving the map to location
            moveMap();
        }
    }


    //Function to move the map
    private void moveMap() {
        if(count!=0) {
            mMap.clear();
        }
        count++;
//        Log.e("Move Marker",""+mylocation.getLatitude()+","+mylocation.getLongitude());
        //String to display current latitude and longitude
        String msg = latitude + ", " + longitude;

        //Creating a LatLng Object to store Coordinates
//        LatLng latLng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
//
//        //Adding marker to map
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng) //setting position
//                .draggable(true) //Making the marker draggable
//                .title("Current Location")); //Adding a title
//
//        //Moving the camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//
//        //Animating the camera
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        LatLng latLng1 = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
        Log.e("CurrentM Location",""+mylocation.getLongitude()+","+mylocation.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.addMarker(new MarkerOptions().position(latLng1).draggable(true).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //Displaying current coordinates in toast
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMyLocation();

        if(mylocation!=null) {
            // Add a marker in Sydney and move the camera
            LatLng latLng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
            Log.e("Current Location",""+mylocation.getLongitude()+","+mylocation.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.setOnMarkerDragListener(this);
            mMap.setOnMapLongClickListener(this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();

//        if(displayGpsStatus()) {
//            getCurrentLocation();
//        } else{
//            alertbox("GPS Status","Your GPS is Off:");
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();

        //Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonCurrent){

            getMyLocation();

           /* if(displayGpsStatus()) {
                getCurrentLocation();
//                moveMap();
            } else{
                alertbox("GPS Status","Your GPS is Off:");
            }*/
//            moveMap(latitude,longitude);
        }
        if(v.getId()==R.id.loc_newCustomer){
            addNewCustomerDialog();
//            new AddNewLocationInPopUp().execute();
        }
    }

    public Boolean displayGpsStatus() {
        ContentResolver contentResolver = getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
        return gpsStatus;
    }

    protected void alertbox(String title, String mymessage) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SalesForceMapActivity.this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
//                                Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
//                                intent.putExtra("enabled", true);
//                                context.sendBroadcast(intent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            Double latitude=mylocation.getLatitude();
            Double longitude=mylocation.getLongitude();
            Log.e("Locqation","Latitude = "+latitude+" Longitude = "+longitude);
//            Toast.makeText(getApplicationContext(),"Location Latitude = "+latitude+" Longitude = "+longitude,Toast.LENGTH_LONG).show();
//            latitudeTextView.setText("Latitude : "+latitude);
//            longitudeTextView.setText("Longitude : "+longitude);
            //Or Do whatever you want with your location

            moveMap();
        }
    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(SalesForceMapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(100);
                    locationRequest.setFastestInterval(100);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, SalesForceMapActivity.this);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat.checkSelfPermission(SalesForceMapActivity.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                        if(mylocation!=null) {
                                            latitude = mylocation.getLatitude();
                                            longitude = mylocation.getLongitude();

//                                            Toast.makeText(SalesForceMapActivity.this, "Location button "+latitude+","+longitude, Toast.LENGTH_SHORT).show();
                                            moveMap();
                                        }
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        if(mylocation!=null) {
                                            latitude = mylocation.getLatitude();
                                            longitude = mylocation.getLongitude();

//                                            Toast.makeText(SalesForceMapActivity.this, "Location Resolution "+latitude+","+longitude, Toast.LENGTH_SHORT).show();
                                            moveMap();
                                        }
                                        status.startResolutionForResult(SalesForceMapActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
//                        moveMap();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(SalesForceMapActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(SalesForceMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
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

            spin_saveLoc_Type = dialog.findViewById(R.id.spin_saveLoc_Type);
            spin_saveLoc_Territory = dialog.findViewById(R.id.spin_saveLoc_Territory);
            spin_saveLoc_Industry = dialog.findViewById(R.id.spin_saveLoc_Industry);

            getIndustryDetails();
            getTerritoryDetails();
            GetAllCustomerDetail();

            edt_saveLoc_EmpName = dialog.findViewById(R.id.edt_saveLoc_EmpName);
            edt_saveLoc_LatLong = dialog.findViewById(R.id.edt_saveLoc_LatLong);
            edt_saveLoc_LocIdentifier = dialog.findViewById(R.id.edt_saveLoc_LocIdentifier);

            final LinearLayout linear_Type = dialog.findViewById(R.id.linear_Type);
            final LinearLayout linear_customerDetail = dialog.findViewById(R.id.linear_customerDetail );
            final LinearLayout linear_LatLong = dialog.findViewById(R.id.linear_LatLong);
            final LinearLayout linear_locIdentifier = dialog.findViewById(R.id.linear_locIdentifier);
            final LinearLayout linear_territory = dialog.findViewById(R.id.linear_territory);
            final LinearLayout linear_industry = dialog.findViewById(R.id.linear_industry);

            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            String[] array = {"Select Type","New Customer","Existing Customer"};

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,array);

            spin_saveLoc_Type.setAdapter(arrayAdapter);

            mAdapter = new SaleForceMasterAdapter(SalesForceMapActivity.this,arlDataIndustry,"INDUSTRY");
            spin_saveLoc_Industry.setAdapter(mAdapter);

            mAdapter = new SaleForceMasterAdapter(SalesForceMapActivity.this,arlDataTerritory,"LOCATION");
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
                        edt_saveLoc_EmpName.setVisibility(View.VISIBLE);
                        auto_customer_detail.setVisibility(View.GONE);

                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.VISIBLE);
                        linear_LatLong.setVisibility(View.VISIBLE);
                        linear_locIdentifier.setVisibility(View.VISIBLE);
                        linear_territory.setVisibility(View.VISIBLE);
                        linear_industry.setVisibility(View.VISIBLE);

                        edt_saveLoc_LatLong.setEnabled(false);
                    } else if(position==2){
                        edt_saveLoc_EmpName.setVisibility(View.GONE);
                        auto_customer_detail.setVisibility(View.VISIBLE);

                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.VISIBLE);
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

            auto_customer_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String positionValue = auto_customer_detail.getText().toString().trim();
                    for(int i =0;i<arlDataCustomer.size();i++){
                        if(positionValue.equals(arlDataCustomer.get(i).get("customerName"))){
                            CUST_NAME = arlDataCustomer.get(i).get("customerName");
                            CUST_ID = arlDataCustomer.get(i).get("customerId");
                            Log.e("CUST_ID","CUST_ID = "+CUST_ID+" CUST_NAME -> "+ CUST_NAME);
                        }
                    }
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

            try {
                if(mylocation!=null){
                    double lat = mylocation.getLatitude();
                    double lng = mylocation.getLongitude();

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
                    getMyLocation();
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

                        SAVE_CUR_CUST_NAME = edt_saveLoc_EmpName.getText().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();
                        SAVE_CUR_LOC_IDEN = edt_saveLoc_LocIdentifier.getText().toString();

//                        SAVE_CUR_TERROTORY = spin_saveLoc_Territory.getSelectedItem().toString();
//                        SAVE_CUR_INDUSTRY = spin_saveLoc_Industry.getSelectedItem().toString();

                        if(SAVE_CUR_CUST_NAME.equals("")){
                            Toast.makeText(getApplicationContext(),"Please Enter Customer Name",Toast.LENGTH_LONG).show();
                        } else if(LOCATION_NAME.equals("SELECT TERRITORY")){
                            Toast.makeText(getApplicationContext(),"Please Select Territory",Toast.LENGTH_LONG).show();
                        } else if(INDUSTRY_NAME.equals("SELECT INDUSTRY")){
                            Toast.makeText(getApplicationContext(),"Please Enter Industory",Toast.LENGTH_LONG).show();
                        } else{
                            String lat = "0.0", lng = "0.0";
                            if(mlocation!=null){
                                latitude = mlocation.getLatitude();
                                longitude = mlocation.getLongitude();
                            }
                            SaveCustomerDetail(SAVE_CUR_CUST_NAME,"0",LOCATION_ID,SAVE_CUR_LOC_IDEN,
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
                        Log.e("Existing Customer",SpinSaveType);
                        auto_customer_detail.setVisibility(View.VISIBLE);
                        edt_saveLoc_EmpName.setVisibility(View.GONE);

                        SAVE_CUR_TYPE = spin_saveLoc_Type.getSelectedItem().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();

                        if(CUST_NAME.equals("")){
                            Toast.makeText(getApplicationContext(),"Please Enter Existing Customer Name",Toast.LENGTH_LONG).show();
                        } else{
                            String lat = "0.0", lng = "0.0";
                            if(mlocation!=null){
                                latitude = mlocation.getLatitude();
                                longitude = mlocation.getLongitude();
                            }
                            addVisitForCustomer(CUST_ID,CUST_NAME);
//                            SaveCustomerDetail(CUST_NAME,CUST_ID," "," ",
//                                    " ",SAVE_CUR_LAT,String.valueOf(latitude),String.valueOf(longitude));
                            dialog.dismiss();
//                            SaveCustomerDetail();
//                            HashMap<String,String> hashMap = new HashMap<>();
//                            hashMap.put("SAVE_CUR_TYPE",SAVE_CUR_TYPE);
//                            hashMap.put("SAVE_CUR_CUST_NAME",SAVE_CUR_CUST_NAME);
//                            hashMap.put("SAVE_CUR_LAT",SAVE_CUR_LAT);
                        }
                    }
                }
            });
            btn_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            if(mylocation!=null){
                dialog.show();
            } else{
                Toast.makeText(this, "Location Not Found now, Please try Again.", Toast.LENGTH_SHORT).show();
                getMyLocation();
            }

        }catch (Exception e){
            e.printStackTrace();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public class AddNewLocationInPopUp extends AsyncTask<Void,Void,Void>{

        ProgressDialog pDialog;
        Location loc;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SalesForceMapActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(mylocation!=null) {
               addNewCustomerDialog();
               pDialog.dismiss();
            }
        }
    }

    public void getIndustryDetails(){
        try{

            if(Utilities.isNetworkAvailable(getApplicationContext())) {
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
                                        mAdapter = new SaleForceMasterAdapter(SalesForceMapActivity.this,arlDataIndustry,"INDUSTRY");
                                        spin_saveLoc_Industry.setAdapter(mAdapter);

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

                RequestQueue requestQueue = Volley.newRequestQueue(SalesForceMapActivity.this);
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
            if(Utilities.isNetworkAvailable(SalesForceMapActivity.this)){
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
                                mAdapter = new SaleForceMasterAdapter(SalesForceMapActivity.this,arlDataTerritory,"LOCATION");
                                spin_saveLoc_Territory.setAdapter(mAdapter);
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
                RequestQueue requestQueue = Volley.newRequestQueue(SalesForceMapActivity.this);
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

    public void GetAllCustomerDetail(){
        try{
            if(Utilities.isNetworkAvailable(getApplicationContext())){
                try {
                    Custmer_Name = new ArrayList<>();
                    arlDataCustomer = new ArrayList<>();
                    JSONObject param = new JSONObject();

                    param.put("GROUP_ID","1");
                    param.put("USER_ID",empoyeeId);

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
//                    int socket = 300000;
//                    RetryPolicy policy = new DefaultRetryPolicy(socket,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//                    jsonObjectRequest.setRetryPolicy(policy);
//                    requestQueue.add(jsonObjectRequest);
                    RequestQueue requestQueue = Volley.newRequestQueue(SalesForceMapActivity.this);
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
//GROUP_ID, CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_ALIAS_NAME, LOCATION_ID, LOCATION_LANDMARK, IS_HEAD_OFFICE,
// INDUSTRY_ID, ADDRESS, LAT, LONG, PHONE1, PHONE2, PHONE3, EMAIL1, EMAIL2, WEBSITE, STATUS, USER_ID
    public void SaveCustomerDetail(String Customer_Name,String Customer_Id,String loc_id,String landmark,String industry_id,String address,
                                   String Lat,String Lng){
        try{
            if(Utilities.isNetworkAvailable(getApplicationContext())){
                JSONObject param = new JSONObject();
                param.put("GROUP_ID","1");
                param.put("CUSTOMER_ID",Customer_Id);
                param.put("CUSTOMER_NAME",Customer_Name);
                param.put("CUSTOMER_ALIAS_NAME"," ");
                param.put("LOCATION_ID",loc_id);
                param.put("LOCATION_LANDMARK",landmark);
                param.put("IS_HEAD_OFFICE","false");
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
                param.put("STATUS","true");
                param.put("USER_ID","3");

                Log.e("ParamData","param->"+param.toString());

                String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/SaveCustomerSalesforcePost";
                Log.e("URL DATA","Url Data->"+url.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,param,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    Log.e("Response", "Save Data->" + response.toString());
                                    String saveNotesResult = response.getString("SaveCustomerSalesforcePostResult");
                                    int res = Integer.valueOf(saveNotesResult);
                                    if (res > 0) {
                                        String Customer_Name = auto_customer_detail.getText().toString().trim();
                                        addVisitForCustomer(""+res,Customer_Name);
                                        spin_saveLoc_Type.setSelection(0);
                                        spin_saveLoc_Territory.setSelection(0);
                                        spin_saveLoc_Industry.setSelection(0);
                                        edt_saveLoc_EmpName.setText("");
                                        edt_saveLoc_LocIdentifier.setText("");
                                        edt_saveLoc_LatLong.setText("");
                                        auto_customer_detail.setText("");
//                                        auto_customer_detail.setAdapter(null);
                                        Utilities.showDialog(coordinatorLayout, "Customer Save");
//                                        GetCustomerNotesDetails();
//                                        mAdapter.notifyDataSetChanged();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        },new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                error.printStackTrace();
                            }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(SalesForceMapActivity.this);
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

    public void addVisitForCustomer(final String CustomerId, final String CustomerName){
        try{
            final Dialog dialog = new Dialog(SalesForceMapActivity.this);
            dialog.setContentView(R.layout.add_visit_customer_detail);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            Button btn_ApproveGo, btn_close;
            btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
            btn_close = dialog.findViewById(R.id.btn_close);

            btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linear_map_fragment.setVisibility(View.GONE);
                    linear_fragmentContainer.setVisibility(View.VISIBLE);

                    Log.e("Customer","Detail = CustomerName = "+CustomerName+", CustomerId = "+CustomerId);
                    Bundle bundle = new Bundle();
                    bundle.putString("Type","SaleForceMap");
                    bundle.putString("CustomerName",CustomerName);
                    bundle.putString("CustomerId",CustomerId);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    SaleForce_Visit saleForceVisit = new SaleForce_Visit();
                    saleForceVisit.setArguments(bundle);
                    transaction.replace(R.id.frame_visit_map, saleForceVisit);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    dialog.dismiss();
                }
            });

            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
