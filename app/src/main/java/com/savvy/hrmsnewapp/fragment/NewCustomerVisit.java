package com.savvy.hrmsnewapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.rahul.AddressDetails;
import com.savvy.hrmsnewapp.service.LocationService;
import com.savvy.hrmsnewapp.service.TrackGPS;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.LogMaintainance;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.android.volley.VolleyLog.TAG;

public class NewCustomerVisit extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    NewCustomerVisit.CurrentDateTimeAsynTask currentDateTimeAsynTask;
    CustomTextView txt_cal_date, txt_cal_time, address;
    Button btn_punch_in;
    EditText edt_messagefeedback;
    CoordinatorLayout coordinatorLayout;
    TrackGPS track;
    public static double latitude;
    public static double longitude;
    AddressDetails addressDetails;
    // public static String address ;
    Location mCurrentLocation;
    String mLastUpdateTime = "";
    private Context mContext;
    private boolean statusOfGPS;
    private LocationRequest mLocationRequest;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    private boolean permissionGranted;
    private static final int LOCATION_REQUEST_CODE = 101;
    private Location location;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

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
        track = new TrackGPS(getContext());
        if (!track.canGetLocation()) {
            track.showSettingsAlert();
        } else {
            latitude = track.getLatitude();
            longitude = track.getLongitude();

            addressDetails = new AddressDetails(getContext(), latitude, longitude);

        }
        getCurrentDateTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_customer_visit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assert getActivity() != null;
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        // token = (shared.getString("Token", ""));
        //empoyeeId = (shared.getString("EmpoyeeId", ""));
        // username = (shared.getString("UserName", ""));

        btn_punch_in = getActivity().findViewById(R.id.btn_punchIn);
//        btn_punch_out = (Button)findViewById(R.id.btn_punchOut);
//        btn_back_cancel = (Button)findViewById(R.id.btn_back_cancel_1);


        txt_cal_date = getActivity().findViewById(R.id.txv_currentDate);
        txt_cal_time = getActivity().findViewById(R.id.txv_currentTime);

        edt_messagefeedback = getActivity().findViewById(R.id.edt_messagefeedback);

        // btn_punch_in.setOnClickListener(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!track.canGetLocation()) {
            track.showSettingsAlert();
        } else {
            latitude = track.getLatitude();
            longitude = track.getLongitude();

        }
    }

    private void setLocationResult() {
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        if (googleApiClient != null) {
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
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
        // mMap.clear();

        if (!permissionGranted) {

            // btn_submit.setVisibility(View.INVISIBLE);
            // snackbar.show();


        } else {

            if (location != null && statusOfGPS) {

                //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
                // markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(location.getLatitude() + "," + location.getLongitude());
                //Adding the created the marker on the map

                // mMap.addMarker(markerOptions);

                //Move the camera to the user's location and zoom in!
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.0f));

            }


        }


    }


    private void getCurrentDateTime() {
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                currentDateTimeAsynTask = new NewCustomerVisit.CurrentDateTimeAsynTask();
                currentDateTimeAsynTask.execute();
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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
            checkLocationandAddToMap(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        location = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
    public void onLocationChanged(Location location) {

        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (mCurrentLocation != null) {
            LogMaintainance.WriteLog("NewTrackServiceActivity-> Location Changed " + mCurrentLocation.getLatitude());
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            addressDetails = new AddressDetails(getContext(), latitude, longitude);
            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                address.setText("\n" + addresses.get(0).getAddressLine(0));
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private class CurrentDateTimeAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        String token;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                //String LOGIN_URL = "http://savvyshippingsoftware.com/SavvyMobile/SavvyMobileService.svc//GetCurrentDateTime";
                String CURRENTDATETIME_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc//GetCurrentDateTime";

                System.out.println("CURRENTDATETIME_URL====" + CURRENTDATETIME_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(CURRENTDATETIME_URL, "GET");
                if (json != null) {
                    Log.d("JSON result", json);
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                try {
                    // {"ServerDate":"4\/25\/2017 8:04:30 PM","ServerTime":"8:04 PM","errorMessage":null}
                    System.out.println("RESULT HARIOM====" + result);
                    JSONObject jsonobj = new JSONObject(result);
//                    String  serverDate=jsonobj.getString("ServerDate");
                    String serverDate = jsonobj.getString("ServerDateDDMMYYYYY");
                    String serverTime = jsonobj.getString("ServerTime");
                    String errorMessage = jsonobj.getString("errorMessage");

                    String[] serverDateSplit = serverDate.split(" ");
                    String replacecurrDate = serverDateSplit[0].replace("\\", "");
                    txt_cal_time.setText(serverTime);
                    txt_cal_date.setText(replacecurrDate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... text) {

            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


    }

}
