package com.savvy.hrmsnewapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.attendanceMark.MarkAttendanceInOut;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.model.ProfileModel;
import com.savvy.hrmsnewapp.rahul.AddressDetails;
import com.savvy.hrmsnewapp.service.LocationService;
import com.savvy.hrmsnewapp.service.TrackGPS;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.LogMaintainance;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Ravi on 29/07/15.
 */
public class MarkAttendanceWithMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    int userid;
    SimpleDateFormat df;
    String formattedDate = "";
    TextView currentMonth, currentYear, tvHeader, tvAddress;
    String eventDate = "";
    ProfileModel profileDB;
    ProfileModel profileModel;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    CustomTextView txv_currentDate, txv_currentTime;
    Button btn_submit;
    EditText edt_messagefeedback;
    private TrackGPS gps;
    private static String address;
    String longitude, longi;
    String latitude, latit;
    private GoogleApiClient googleApiClient;
    private LocationService locationService;

    private static final int LOCATION_REQUEST_CODE = 101;
    final static int REQUEST_LOCATION = 199;

    String token = "", empoyeeId = "", username = "";

    Handler handler1;
    Runnable rRunnable;
    static int counter = 0;
    int count_method = 0;

    CustomTextView txv_userName, txv_studentcode_value, txv_course_value, txv_batch_value, txv_email_value, txv_fathername_value;
    private Context mContext;
    private GoogleMap mMap;
    private Snackbar snackbar;
    private TrackGPS track;
    private Location location;
    private boolean permissionGranted;
    private MarkerOptions markerOptions;


    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    private LocationRequest mLocationRequest;
    private boolean statusOfGPS;
    private String title;
    private AddressDetails addressDetails;

    public MarkAttendanceWithMapFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e("MarkAttendance", "onStart");
        LocationService.setCallbacks(new LocationService.GPSStatusCallback() {
            @Override
            public void gpsStatusChanged(boolean status) {
                Log.e("GPS Status: ", status + "");
                statusOfGPS = status;
                if (!statusOfGPS) {

                    setLocationResult();
                    /* TODO GPS off */

                } else {
                    /* TODO GPS on */

                }


            }
        });

        mContext.startService(new Intent(mContext, LocationService.class));


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MarkAttendance", "onCreate");
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));

        title = (shared.getString("Title", ""));


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mark_attendance_with_map, container, false);
        Log.e("MarkAttendance", "onCreateView");

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        Log.e("MarkAttendance", "onActivityCreated");
        setMap();

        setSnackbar();

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        btn_submit = rootView.findViewById(R.id.btn_submit);

        edt_messagefeedback = rootView.findViewById(R.id.edt_messagefeedback);
        tvHeader = rootView.findViewById(R.id.tvHeader);
        tvAddress = rootView.findViewById(R.id.tvAddress);
        edt_messagefeedback.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edt_messagefeedback.setRawInputType(InputType.TYPE_CLASS_TEXT);
        tvAddress.setText("");


        if (title.contains("Customer")) {

            tvHeader.setText("Customer Name/Remarks");
            edt_messagefeedback.setHint("Customer Name/Remarks");

        } else {
            tvHeader.setText("Comment");
            edt_messagefeedback.setHint("Comment");
        }


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().overridePendingTransition(0, 0);
                if (location != null) {
                    if ((Utilities.isNetworkAvailable(mContext))) {
                        if (isMockLocationON(location)) {
                            showMockAlert();
                        } else {
                            String locationAddress = Utilities.getAddressFromLocation(getActivity(), location);
                            markAttendancePost(edt_messagefeedback.getText().toString(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), locationAddress);
                        }


                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }


                } else {
                    snackbar.show();
                }

            }
        });
    }


    private void setSnackbar() {
        snackbar = Snackbar
                .make(coordinatorLayout, "Location not found,Please Refresh the Page and Try Again! ", Snackbar.LENGTH_SHORT)
                .setActionTextColor(getResources().getColor(R.color.white));

        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.color_red));


    }


    private void setMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Instantiating the GoogleApiClient
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        setLocationResult();


    }

    private void setLocationResult() {
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);


        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            startIntentSenderForResult(status.getResolution().getIntentSender(), REQUEST_LOCATION, null, 0, 0, 0, null);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    private void checkLocationPermission() {
        //Checking if the user has granted the permission
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;

            //Requesting the Location permission
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST_CODE);
            return;


        } else {
            permissionGranted = true;


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkLocationPermission();
        if (mMap != null) {
            LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!statusOfGPS) {

                location = null;
                mMap.clear();


            } else {
                checkLocationPermission();
                mMap.setMyLocationEnabled(true);
            }


        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;

                    checkLocationandAddToMap(location);
                } else {
                    permissionGranted = false;
                    Toast.makeText(mContext, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void checkLocationandAddToMap(Location location) {
        mMap.clear();

        if (!permissionGranted) {

            btn_submit.setVisibility(View.INVISIBLE);
            snackbar.show();


        } else {

            if (location != null && statusOfGPS) {

                //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
                markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(location.getLatitude() + "," + location.getLongitude());
                //Adding the created the marker on the map

                mMap.addMarker(markerOptions);

                //Move the camera to the user's location and zoom in!
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.0f));

            }


        }


    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("MarkAttendance", "onResume");
        googleApiClient.connect();


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("MarkAttendance", "onPause");
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        LocationService.removeCallbacks(null);

        mContext.stopService(new Intent(mContext, LocationService.class));

    }


    //Callback invoked once the GoogleApiClient is connected successfully
    @Override
    public void onConnected(Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location == null) {

            //  LogMaintainance.WriteLog("Location :"+location.getLatitude()+", "+location.getLongitude());

        } else {
            if (isMockLocationON(location)) {
                tvAddress.setText(R.string.mocklocation);
                showMockAlert();
            } else {
                checkLocationandAddToMap(location);
            }


        }
    }

    public boolean isMockLocationON(Location location) {
        boolean isMockLocation=false;
        try{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location != null && location.isFromMockProvider()) {
            isMockLocation = true;
        } else {
            isMockLocation = !Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }}catch (Exception e){}
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
    public void onConnectionSuspended(int i) {
        location = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult((Activity) mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i("Log", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                onMapReady(mMap);

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void markAttendancePost(String attendanceRemark, String latitude, String longitude, String location_Address) {

        try {
            Log.e("Mark Att", " Latitude =  " + latitude + " ,Longitude = " + longitude);
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveAttendanceMarkPost";
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", attendanceRemark);
            params_final.put("latitude", latitude != null ? latitude : "");
            params_final.put("longitude", longitude != null ? longitude : "");
            params_final.put("location", location_Address);
            Log.e(TAG, "SaveAttendanceMarkPost: " + params_final.toString());

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            pDialog.dismiss();

                            try {
                                JSONObject jsonobj = response.getJSONObject("SaveAttendanceMarkPostResult");
                                String statusId = jsonobj.getString("statusId");
                                String statusDescription = jsonobj.getString("statusDescription");
                                String errorMessage = jsonobj.getString("errorMessage");

                                if (statusId.equals("1")) {
                                    Utilities.showDialog(coordinatorLayout, statusDescription);
                                    edt_messagefeedback.setText("");

                                } else if (statusId.equals("2")) {
                                    Utilities.showDialog(coordinatorLayout, statusDescription);
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

            int socketTimeout = 300000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onLocationChanged(Location location) {

        checkLocationandAddToMap(location);
        if (location != null) {
            LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Changed " + location.getLatitude());

            addressDetails = new AddressDetails(mContext, location.getLatitude(), location.getLongitude());
            try {
                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    tvAddress.setText("\n" + addresses.get(0).getAddressLine(0));
                }


            } catch (Exception e) {
            }
        }
    }
}
