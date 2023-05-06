package com.savvy.hrmsnewapp.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.FaceAttendanceActivity;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.ServerDateTimeModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class FaceDetectionHolderFrgamnet extends Fragment {

    CoordinatorLayout coordinatorLayout;
    CustomTextView currentDate, currentTime, tv_location;
    EditText tv_remark;
    Button markAttandanceButton;
    FusedLocationProviderClient fusedClient;
    private LocationRequest mRequest;
    private LocationCallback mCallback;
    String locationAddress;
    String geoString;
    String latitude="";
    String longitude="";
    String countryName = "";
    ProgressDialog locationProcessDialog;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getActivity() != null;
        shared = requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        fusedClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "locationResult null");
                    return;
                }
                Log.d(TAG, "received " + locationResult.getLocations().size() + " locations");
                for (Location loc : locationResult.getLocations()) {
                    try {
                        Log.e(TAG, "onLocationResult: " + loc.getLatitude() + " , " + loc.getLongitude());
                        if (checkMockLocation(loc)) {
                            mockAlertDialog();
                            locationAddress = null;
                            tv_location.setText(locationAddress);
                        } else {

                            latitude = String.valueOf(loc.getLatitude());
                            longitude = String.valueOf(loc.getLongitude());
                            locationAddress = Utilities.getAddressFromLateLong(getActivity(), loc.getLatitude(), loc.getLongitude());
                            countryName = Utilities.getCountryFromLocation(getActivity(), loc);
                            if (locationAddress.equals("")) {
                                tv_location.setText("Latitude :" + latitude + "\n" + "Longitude" + longitude);

                            } else {
                                tv_location.setText(locationAddress);
                            }

                            Date date = new Date(loc.getTime());
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.US);
                            geoString = sdf.format(date) + " " + countryName;

                            if (locationProcessDialog != null && locationProcessDialog.isShowing())
                                locationProcessDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d(TAG, "locationAvailability is " + locationAvailability.isLocationAvailable());
                super.onLocationAvailability(locationAvailability);
            }
        };

        startLocationUpdate();

    }

    private void mockAlertDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Mock location detected!").setMessage("Please disable mock location.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void startLocationUpdate() {
        mRequest = new LocationRequest();
        mRequest.setInterval(10000);//time in ms; every ~10 seconds
        mRequest.setFastestInterval(5000);
        mRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkLocationPermission()) {
            fusedClient.requestLocationUpdates(mRequest, mCallback, Looper.getMainLooper());
        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_detection_holder, container, false);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        currentDate = view.findViewById(R.id.tv_face_currentDate);
        currentTime = view.findViewById(R.id.tv_face_currentTime);
        tv_remark = view.findViewById(R.id.tv_face_remark);
        tv_location = view.findViewById(R.id.tv_face_locationAddress);
        markAttandanceButton = view.findViewById(R.id.btn_face_submit);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        markAttandanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLocationPermission()) {
                    Log.e("TAG","lat--lng"+latitude +"=="+longitude);
                    if (!(latitude.equals("") && longitude.equals(""))) {
                        String remarks = tv_remark.getText().toString().trim();
                        String encodeLocation = "";

                        if (!locationAddress.equals("")) {
                             encodeLocation = Base64.encodeToString(locationAddress.getBytes(), Base64.DEFAULT);
                        }

                        String encodeGeoTime = Base64.encodeToString(geoString.getBytes(), Base64.DEFAULT);

                        Intent intent = new Intent(getActivity(), FaceAttendanceActivity.class);
                        intent.putExtra("LOCATION_ADDRESS", encodeLocation);
                        intent.putExtra("GEO_STRING", encodeGeoTime);
                        intent.putExtra("LATITUDE", latitude);
                        intent.putExtra("LONGITUDE", longitude);
                        intent.putExtra("REMARK", remarks);
                        intent.putExtra("SIMPLE_FACE", "");
                        intent.putExtra("PUNCHMODE", "");
                        intent.putExtra("ATTDATE", "");
                        intent.putExtra("COUNTRY_NAME", countryName);
                        startActivity(intent);
                    } else {
                        Utilities.showDialog(coordinatorLayout, "Please wait while getting your location...");
                    }
                } else {
                    Utilities.showDialog(coordinatorLayout, "Getting your location...");
                    final LocationManager manager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                    assert manager != null;
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 1);
                    }
                    checkLocationPermission();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentDateTime();
        if (fusedClient != null) {
            startLocationUpdate();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (fusedClient != null) {
            fusedClient.removeLocationUpdates(mCallback);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdate();
            } else {
                checkLocationPermission();
            }
        }
    }

    private void showRationale() {
        AlertDialog dialog = new AlertDialog.Builder(requireActivity()).setMessage("SavvyHRMS wants to access your device location..").setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(FaceDetectionHolderFrgamnet.this.requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                dialogInterface.dismiss();
            }
        })
                .create();
        dialog.show();
    }


    boolean checkLocationPermission() {
        boolean isPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationale();
                isPermissionGranted = false;
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE}, 2);
                isPermissionGranted = false;

            }
        } else {
            final LocationManager manager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
            assert manager != null;
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                isPermissionGranted = true;
            }
        }
        return isPermissionGranted;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Your GPS seems to be disabled, please enable it to get your location.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            checkLocationPermission();
        }
    }

    private boolean checkMockLocation(Location location) {
        boolean isMock;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            isMock = !Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
        return isMock;
    }

    private void getCurrentDateTime() {
        APIServiceClass.getInstance().sendDateTimeRequest(shared.getString(Constants.EMPLOYEE_ID_FINAL, ""), new ResultHandler<ServerDateTimeModel>() {
            @Override
            public void onSuccess(ServerDateTimeModel data) {
                String[] serverDateSplit = data.getServerDateDDMMYYYYY().split(" ");
                String replacecurrDate = serverDateSplit[0].replace("\\", "");
                currentTime.setText(data.getServerTime());
                currentDate.setText(replacecurrDate);
            }

            @Override
            public void onFailure(String message) {
            }
        });
    }
}
