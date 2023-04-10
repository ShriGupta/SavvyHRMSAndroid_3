package com.savvy.hrmsnewapp.markPunch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.LoginActivity_1;
import com.savvy.hrmsnewapp.attendanceMark.MarkAttendance;
import com.savvy.hrmsnewapp.attendanceMark.MarkAttendanceInOut;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.BaseFragment;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.rahul.AddressDetails;
import com.savvy.hrmsnewapp.runtimePermission.PermissionsManager;
import com.savvy.hrmsnewapp.service.TrackGPS;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.LogMaintainance;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.MODE_PRIVATE;

public class MarkPunchFragment extends BaseFragment implements LocationListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    FusedLocationProviderClient fusedLocationClient;
    private CustomTextView txtCurrentDate;
    private CustomTextView txtCurrentTime;
    private CustomTextView txtShiftStartTime;
    private CustomTextView txtShiftEndTime;
    private Button btnPunchIn;
    private Button btnPunchOut;
    private ArrayList<HashMap<String, String>> arlData = new ArrayList<>();
    String empoyeeId;
    SharedPreferences shared, shareData;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private MarkPunchAsyncTask markPunchAsyncTask;
    private CheckPunchInStatusAsyncTask checkPunchInStatus;
    private MarkAttendanceAsyncTask markAttendanceAsyncTask;
    private String mPunchStatus = "";
    double longitude = 0, longi;
    double latitude = 0, latit;
    private LocationManager locationManager = null;
    AddressDetails addressDetails;
    private String mAddressLocation;
    private TrackGPS mTrackGps;
    Location loc = null;
    private Boolean flag = false;

    Location mCurrentLocation;

    private TextView address;

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private static final long INTERVAL = 1000 * 60 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1;
    private LinearLayout parentLinearLayout;
    private final int PERMISSION_REQUEST_CODE = 1111;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    String locationAddress = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTrackGps = new TrackGPS(getContext());
        if (!mTrackGps.canGetLocation()) {
            mTrackGps.showSettingsAlert();
        }
    }

    protected void createLocationRequest() {
        LogMaintainance.WriteLog("Start CreateLocationRequest ");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_punch_attendence, container, false);
        txtCurrentDate = rootView.findViewById(R.id.txv_currentDate);
        txtCurrentTime = rootView.findViewById(R.id.txv_currentTime);
        txtShiftStartTime = rootView.findViewById(R.id.txv_shiftStartTime);
        txtShiftEndTime = rootView.findViewById(R.id.txv_shiftEndTime);
        btnPunchIn = rootView.findViewById(R.id.btn_punchIn);
        btnPunchOut = rootView.findViewById(R.id.btn_punchOut);
        address = (CustomTextView) rootView.findViewById(R.id.address);
        parentLinearLayout = (LinearLayout) rootView.findViewById(R.id.parentLinearLayout);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        try {
            if (latitude==0 && longitude==0) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (isLocationEnable()) {
                    getCurrentLocation();
                }
            } else {
                getLocationAddress(latitude,longitude);
            }
        } catch (Exception e) {
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


       // createLocationRequest();
       // updateUI();


        btnPunchOut.setEnabled(false);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));


        btnPunchIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    checkPunchInStatus = new CheckPunchInStatusAsyncTask();
                    checkPunchInStatus.empid = empoyeeId;
                    checkPunchInStatus.execute();
                } else {
                    requestLocationPermission();
                }

            }
        });

        btnPunchOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    checkPunchInStatus = new CheckPunchInStatusAsyncTask();
                    checkPunchInStatus.empid = empoyeeId;
                    checkPunchInStatus.execute();
                } else {
                    requestLocationPermission();
                }

            }
        });

        return rootView;
    }
    private boolean isLocationEnable() {
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            statusOfGPS = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return statusOfGPS;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        int result5 = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);
        return result5 == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                try {
                    if (grantResults.length > 0) {
                        boolean AccessLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        Log.e("AccessLocation>>", "<>" + AccessLocation);
                        if (!AccessLocation) {
                        } else {

                            getCurrentLocation();
                        }

                    }
                } catch (Exception e) {}
                break;


        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location lastLocation = task.getResult();
                if (lastLocation != null) {
                    getLocationAddress(lastLocation.getLatitude(), lastLocation.getLongitude());
                }
            }
        });

    }

    private void getLocationAddress(double lat, double longitude_) {
        List<Address> addresses = null;
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, longitude_, 1);

            latitude = lat;
            longitude = longitude_;
            if (addresses != null && addresses.size() > 0) {
                String cityName = addresses.get(0).getAddressLine(0);
                String fullAddress = addresses.get(0).getAddressLine(1);
                String city = addresses.get(0).getLocality();
                String countryName = addresses.get(0).getAddressLine(2);
                Log.e("City", city + "," + cityName);
                locationAddress = cityName;

                Log.e("City", locationAddress );

            }else {
                locationAddress = "";
            }
            if (!locationAddress.equals("")) {
                updateUI(locationAddress);
            } else {
                locationAddress = "Latitude :" + latitude + "\n" + "Longitude :" + longitude_;
                updateUI(locationAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserAttendanceData(String emp_id) {
        try {

            if (Utilities.isNetworkAvailable(getActivity())) {
                markPunchAsyncTask = new MarkPunchAsyncTask();
                markPunchAsyncTask.empid = emp_id;
                markPunchAsyncTask.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        locationAddress = "";
        getUserAttendanceData(empoyeeId);
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                    if (isLocationEnable()) {
                        getCurrentLocation();
                    }
                } else {
                    requestPermission();
                }
            }
        } catch (Exception e) {
        }
        //todo need to test


    }


    @SuppressLint("MissingPermission")
    private void markAttendanceRequest(String testPass, String empId, String location, double latitude, double longitude) {

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {


                if (mTrackGps.canGetLocation()) {
                    flag = displayGpsStatus();
                    if (flag) {
                        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                        if (locationManager != null) {
                            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, MarkPunchFragment.this);
                                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                                if (loc != null && latit == 0.0) {
                                    latit = loc.getLatitude();
                                    longi = loc.getLongitude();
                                }

                            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, MarkPunchFragment.this);
                                loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (loc != null && latit == 0.0) {
                                    latit = loc.getLatitude();
                                    longi = loc.getLongitude();
                                }
                            }
                            if (latit == 0.0 && longi == 0.0) {
                                Snackbar snackbar = Snackbar
                                        .make(parentLinearLayout, "Please wait while we're fetching your current location.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {

                                try {
                                    if (!Utilities.getAddressFromLocation(getContext(), loc).isEmpty()) {
                                        mAddressLocation = Utilities.getAddressFromLocation(getContext(), loc);
                                    }
                                    if (isMockLocationON(loc)) {
                                        showMockAlert();
                                    } else {
                                        callMarkPunchAsyncTask(testPass, empoyeeId, mAddressLocation, String.valueOf(latit), String.valueOf(longi));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }




                    /*@SuppressLint("MissingPermission") Location mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String addressLocation = Utilities.getAddressFromLocation(getContext(), mCurrentLocation);
                    if (mCurrentLocation != null) {
                        callMarkPunchAsyncTask(testPass, empoyeeId, addressLocation, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()));
                    } else {
                        Toast.makeText(getContext(), "Please wait while we're fetching location.", Toast.LENGTH_SHORT).show();
                    }*/
                    } else {
                        mTrackGps.showSettingsAlert();
                    }
                } else {
                    mTrackGps.showSettingsAlert();
                }
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getActivity().getBaseContext().getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
        return gpsStatus;
    }

    private void callMarkPunchAsyncTask(String testPass, String empId, String location, String latitude, String longitude) {
        markAttendanceAsyncTask = new MarkAttendanceAsyncTask();
        markAttendanceAsyncTask.empid = empId;
        markAttendanceAsyncTask.longitudeStr = longitude;
        markAttendanceAsyncTask.latitudeStr = latitude;
        markAttendanceAsyncTask.location_Address = location;
        markAttendanceAsyncTask.testPass = testPass;
        markAttendanceAsyncTask.execute();
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

    public class MarkAttendanceAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        String empid;
        String testPass;
        String location_Address;
        String latitudeStr;
        String longitudeStr;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.setMax(100);
                pDialog.show();


            } catch (Exception e) {
                e.printStackTrace();
                Snackbar snackbar = Snackbar
                        .make(parentLinearLayout, "" + e.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                location_Address = location_Address.replace(" ", "_");
                String MARK_ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/MarkAttendanceGet/" + "0" + "/" + empid + "/" + location_Address + "/" + latitudeStr + "/" + longitudeStr;
                System.out.println("CHECK PUNCH IN STATUS====" + MARK_ATTENDANCE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        MARK_ATTENDANCE_URL, "GET");

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

                try {
                    pDialog.dismiss();
                   // System.out.println("RESULT ATTENDANCE====" + result);
                    JSONObject jsonobj = new JSONObject(result);
                    String resultId = jsonobj.getString("RESULT_ID");

                    if (resultId.equals("1")) {
                        Snackbar snackbar = Snackbar
                                .make(parentLinearLayout, "Attendance marked Successfully.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else if (resultId.equals("3")) {
                        Snackbar snackbar = Snackbar
                                .make(parentLinearLayout, "Location out of range.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    onResume();

                   /* if (resultId.equals("1")) {
                        Toast.makeText(getActivity(), "Punch Mark Successfully.", Toast.LENGTH_SHORT).show();

                    }*/
                }catch (JSONException e){}
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        getActivity().finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }


        }
    }


    public class CheckPunchInStatusAsyncTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.setMax(100);
                pDialog.show();


            } catch (Exception e) {
                e.printStackTrace();
                Snackbar snackbar = Snackbar
                        .make(parentLinearLayout, "" + e.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String MARK_ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/CheckTestDateAssignedGet/" + empid;
                System.out.println("CHECK PUNCH IN STATUS====" + MARK_ATTENDANCE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        MARK_ATTENDANCE_URL, "GET");

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

                try {
                    pDialog.dismiss();
                   // System.out.println("RESULT ATTENDANCE====" + result);
                    JSONObject jsonobj = new JSONObject(result);
                    String resultId = jsonobj.getString("RESULT_ID");
                    if (resultId.equals("1")) {
                        Intent intent = new Intent(getActivity(), OnlineTestActivity.class);
                        startActivity(intent);
                    } else {
                        markAttendanceRequest("0", empoyeeId, "", latitude, longitude);
                    }
                }catch (JSONException e){}
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        getActivity().finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


    private class MarkPunchAsyncTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.setMax(100);
                pDialog.show();


            } catch (Exception e) {
                e.printStackTrace();
                Snackbar snackbar = Snackbar
                        .make(parentLinearLayout, "" + e.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String MARK_ATTENDANCE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetEmployeeMarkAttendanceStatusGet/" + empid;
                System.out.println("ATTENDANCE_URL====" + MARK_ATTENDANCE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        MARK_ATTENDANCE_URL, "GET");

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(String result) {
            if (pDialog != null && pDialog.isShowing()) {

                try {
                    pDialog.dismiss();
                   // System.out.println("RESULT ATTENDANCE====" + result);
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonobj = jsonArray.getJSONObject(0);
                    String punchStatus = jsonobj.getString("PUNCH_STATUS");
                    String shiftEndTime = jsonobj.getString("SHIFT_END_TIME");
                    String shiftStartTime = jsonobj.getString("SHIFT_START_TIME");
                    String serverDate = jsonobj.getString("TODAY_DATE");
                    String serverTime = jsonobj.getString("TODAY_TIME");
                    txtShiftStartTime.setText(shiftStartTime);
                    txtShiftEndTime.setText(shiftEndTime);
                    txtCurrentDate.setText(serverDate);
                    txtCurrentTime.setText(serverTime);
                    mPunchStatus = punchStatus;
                    if (punchStatus.equals("0")) {
                        btnPunchOut.setEnabled(false);
                        btnPunchOut.setBackgroundColor(getResources().getColor(R.color.colorGray));
                    } else if (punchStatus.endsWith("1")) {
                        btnPunchOut.setEnabled(true);
                        btnPunchIn.setEnabled(false);
                        btnPunchOut.setBackground(getResources().getDrawable(R.drawable.button_border_cancel));
                        btnPunchIn.setBackgroundColor(getResources().getColor(R.color.colorGray));
                        btnPunchIn.setClickable(false);
                    } else if (punchStatus.endsWith("2")) {
                        btnPunchOut.setEnabled(false);
                        btnPunchOut.setBackgroundColor(getResources().getColor(R.color.colorGray));
                        btnPunchIn.setEnabled(false);
                        btnPunchIn.setBackgroundColor(getResources().getColor(R.color.colorGray));
                    }
                } catch (JSONException e){}
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        /*Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        getActivity().finish();*/
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {

               /* if (isMockLocationON(location)) {
                    address.setText(R.string.mocklocation);
                    showMockAlert();
                }
                else {*/

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    mCurrentLocation = location;
                    if (mCurrentLocation != null) {
                        LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Changed " + mCurrentLocation.getLatitude());
                        latitude = mCurrentLocation.getLatitude();
                        longitude = mCurrentLocation.getLongitude();
                        addressDetails = new AddressDetails(getContext(), latitude, longitude);
                        try {
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            mAddressLocation = addresses.get(0).getAddressLine(0);
                            address.setText("\n" + addresses.get(0).getAddressLine(0));
                        } catch (Exception e) {
                        }
                   /* }*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isMockLocationON(Location location) {
        boolean isMockLocation=false;
      /* try{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location != null && location.isFromMockProvider()) {
            isMockLocation = true;
        } else {
            assert getActivity() != null;
            isMockLocation = !Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }}catch (Exception w){}*/
        return isMockLocation;
    }

    public void showMockAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Mock Location is ON");
        builder.setMessage("Please disable the mock location for Punch In/Punch Out");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
    private void updateUI(String locationAddrees) {
        address.setText(locationAddrees);
    }
    private void updateUI() {
        try {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            } else {
                if (mGoogleApiClient.isConnected()) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MarkPunchFragment.this);
                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Update Initiate");

                    if (null != mCurrentLocation) {
                        LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Update Get Location " + mCurrentLocation.getLatitude());
                        String lat = String.valueOf(mCurrentLocation.getLatitude());
                        String lng = String.valueOf(mCurrentLocation.getLongitude());

//                        SendTrackMeData(lat,lng);
                        latitude = mCurrentLocation.getLatitude();
                        longitude = mCurrentLocation.getLongitude();

                        latit = mCurrentLocation.getLatitude();
                        longi = mCurrentLocation.getLongitude();

                        Log.e("Location Update ", "At Time: " +
                                "Latitude: " + lat + "\n" +
                                "Longitude: " + lng + "\n" +
                                "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                                "Provider: " + mCurrentLocation.getProvider());
//                        LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Update Get Location Location Update \nAt Time: " + mLastUpdateTime + "\n" +
//                                "Latitude: " + lat + "\n" +
//                                "Longitude: " + lng + "\n" +
//                                "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
//                                "Provider: " + mCurrentLocation.getProvider()+"\n");


//            tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
//                    "Latitude: " + lat + "\n" +
//                    "Longitude: " + lng + "\n" +
//                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
//                    "Provider: " + mCurrentLocation.getProvider());
                    } else {
                        Log.d("Mark Punch", "location is null ...............");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startLocationUpdates() {
        try {
            if (!mGoogleApiClient.isConnected()) {
                LogMaintainance.WriteLog("NewTrackServiceActivity-> StartLocationUpdates Google Api Client is not connected");
                mGoogleApiClient.connect();
            } else {
                if (mGoogleApiClient.isConnected()) {
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
//                    createLocationRequest();

//                    PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
//                            mGoogleApiClient, mLocationRequest, this);
                    LogMaintainance.WriteLog("NewTrackServiceActivity-> StartLocationUpdates start");
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mCurrentLocation != null) {
                        LogMaintainance.WriteLog("NewTrackServiceActivity-> StartLocationUpdates getLastLocation " + mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude());
                    }
                    Log.d("Update UI", "Location update started ..............: ");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
