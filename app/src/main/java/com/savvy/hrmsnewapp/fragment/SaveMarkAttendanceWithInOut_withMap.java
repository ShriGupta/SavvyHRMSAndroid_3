package com.savvy.hrmsnewapp.fragment;


import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.interfaces.LocationInterface;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.LocationManagerClass;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaveMarkAttendanceWithInOut_withMap extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {

    TextView tvHeader, tvAddress;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    Button btn_punchOut, btn_punchIn;
    EditText edt_messagefeedback;
    private static final int LOCATION_REQUEST_CODE = 101;
    final static int REQUEST_LOCATION = 199;
    String token = "", empoyeeId = "", username = "";
    private Context mContext;
    private GoogleMap mMap;
    String locationAddress = "";
    private String title;
    Location location;
    private FusedLocationProviderClient fusedLocationClient;


    public SaveMarkAttendanceWithInOut_withMap() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        username = (shared.getString("UserName", ""));
        title = (shared.getString("Title", ""));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_save_mark_attendance_with_in_out_with_map, container, false);
        Log.e("MarkAttendance", "onCreateView");

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = getView();
        setMap();
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        btn_punchIn = rootView.findViewById(R.id.btn_punchIn);
        btn_punchOut = rootView.findViewById(R.id.btn_punchOut);


        btn_punchOut.setOnClickListener(this);
        btn_punchIn.setOnClickListener(this);


        edt_messagefeedback = rootView.findViewById(R.id.edt_messagefeedback);
        tvHeader = rootView.findViewById(R.id.tvHeader);
        tvAddress = rootView.findViewById(R.id.tvAddress);
        edt_messagefeedback.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edt_messagefeedback.setRawInputType(InputType.TYPE_CLASS_TEXT);
        tvAddress.setText("");

        Log.e("Title Value", title);
        if (title.contains("Customer")) {
            tvHeader.setText("Customer Name/Remarks");
            edt_messagefeedback.setHint("Customer Name/Remarks");

        } else {
            tvHeader.setText("Comment");
            edt_messagefeedback.setHint("Comment");
        }
    }

    private void setMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        requestLocation();

    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), mLocation -> {
                        if (mLocation != null) {
                            location = mLocation;
                            Log.e("TAG", "onLocationRecieve: Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                            updateUI(location);
                        }
                    });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: Permission Granted");
                // checkLocationandAddToMap(location);
                requestLocation();
            } else {
                showLocationErrorDialog("Location permission not granted !");
                // sendErrorToServer(empoyeeId, "Location permission not granted !", "Privilage id 38", "", "", "");
            }
        }
    }

    private void checkLocationandAddToMap(Location location) {
        mMap.clear();
        if (location != null) {
            //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(location.getLatitude() + "," + location.getLongitude());
            //Adding the created the marker on the map
            mMap.addMarker(markerOptions);
            //Move the camera to the user's location and zoom in!
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.0f));

        } else {
            Log.e(TAG, "checkLocationandAddToMap: Location is Null");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        locationAddress = "";
        Log.e("MarkAttendance", "onResume");

        if (!isGPSTurnedOn()) {
            showLocationErrorDialog("Please Enable Location");
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOCATION) {
            onMapReady(mMap);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public boolean isMockLocationON(Location location) {
        boolean isMockLocation = false;
        try {
            if (location != null && location.isFromMockProvider()) {
                isMockLocation = true;
            } else {
                isMockLocation = !Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            }
        } catch (Exception ignored) {
        }
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
    public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            switch (v.getId()) {
                case R.id.btn_punchIn:
                    if (isGPSTurnedOn() && location != null) {
                        if ((Utilities.isNetworkAvailable(mContext))) {

                            if (locationAddress.equals("mocklocation")) {
                                showMockAlert();
                            } else {
                                String locationAddress = Utilities.getAddressFromLateLong(getActivity(), location.getLatitude(), location.getLongitude());
                                if (locationAddress.equals("")) {
                                    locationAddress = "Latitude: " + location.getLatitude() + "\n" + location.getLongitude();
                                }
                                tvAddress.setText(locationAddress);
                                markAttendanceInOut(edt_messagefeedback.getText().toString(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), locationAddress, "I");
                            }
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                        }
                    } else {
                        if (!isGPSTurnedOn()) {
                            moveToLocationSetting();
                        }

                    }
                    break;

                case R.id.btn_punchOut:
                    if (Utilities.isGPSTurnedOn(requireActivity()) && location != null) {
                        if ((Utilities.isNetworkAvailable(mContext))) {
                            if (locationAddress.equals("mocklocation")) {
                                showMockAlert();
                            } else {
                                String locationAddress = Utilities.getAddressFromLateLong(getActivity(), location.getLatitude(), location.getLongitude());
                                if (locationAddress.equals("")) {
                                    locationAddress = "Latitude: " + location.getLatitude() + "\n" + "Longitude:" + location.getLongitude();
                                }
                                tvAddress.setText(locationAddress);
                                markAttendanceInOut(edt_messagefeedback.getText().toString(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), locationAddress, "O");
                            }
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                        }
                    } else {
                        if (!Utilities.isGPSTurnedOn(requireActivity())) {
                            moveToLocationSetting();
                        }
                    }
                    break;
                default:

                    break;
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }


    public void markAttendanceInOut(String comment, String lat, String lon, String location_address, String type) {
        try {
//            string userName, string empoyeeId, string securityToken, string attendanceRemark, string latitude, string longitude,string punchType
//            arlData = new ArrayList<HashMap<String,String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveAttendanceMarkInOut";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("userName", username);
            params_final.put("empoyeeId", empoyeeId);
            params_final.put("securityToken", token);
            params_final.put("attendanceRemark", comment);
            params_final.put("latitude", lat);
            params_final.put("longitude", lon);
            params_final.put("punchType", type);
            params_final.put("location", location_address);
            Log.e(TAG, "markAttendanceInOut: " + params_final.toString());

            if (Utilities.isNetworkAvailable(mContext)) {
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("Value", " Length = " + (String.valueOf(response)).length() + " Value = " + response.toString());
                                pDialog.dismiss();

                                try {
                                    JSONObject jsonobj = response.getJSONObject("SaveAttendanceMarkInOutResult");
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
                                    sendErrorToServer(empoyeeId, "SaveAttendanceMarkInOut", "Privilage id 38", ex.getMessage(), "", "");
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        sendErrorToServer(empoyeeId, "SaveAttendanceMarkInOut", "Privilage id 38", error.getMessage(), "", "");
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

    private void updateUI(Location location) {
        checkLocationandAddToMap(location);
        if (location != null) {
            if (isMockLocationON(location)) {
                locationAddress = "mocklocation";
                tvAddress.setText(R.string.mocklocation);
                showMockAlert();
            } else {
                //   addressDetails = new AddressDetails(mContext, location.getLatitude(), location.getLongitude());
                String locationAddress = Utilities.getAddressFromLateLong(mContext, location.getLatitude(), location.getLongitude());
                if (locationAddress.equals("")) {
                    locationAddress = "Latitude: " + location.getLatitude() + "\n" + location.getLongitude();
                }
                tvAddress.setText(locationAddress);
            }
        }
    }

}
