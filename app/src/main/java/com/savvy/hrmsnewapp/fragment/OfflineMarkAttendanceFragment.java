package com.savvy.hrmsnewapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.OfflinePunchInModel;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.DashBoardActivity.MY_PREFS_NAME;

public class OfflineMarkAttendanceFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    double longitude;
    double latitude;

    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    TextView tvLocation;
    EditText edtDate, edtTime, edtComment;
    Button btnMarkAttandance;
    String locationValue;
    boolean isPunchButtonClicked;
    SharedPreferences sharedPreferences;
    String userName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = requireActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        userName = sharedPreferences.getString("UserName", "");
        mGoogleApiClient = new GoogleApiClient.Builder(requireActivity()).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        initLocation();

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offline_markattandance, container, false);
        tvLocation = view.findViewById(R.id.tv_location);
        edtDate = view.findViewById(R.id.edt_current_date);
        edtTime = view.findViewById(R.id.edt_current_time);
        edtComment = view.findViewById(R.id.edt_comment);
        btnMarkAttandance = view.findViewById(R.id.btn_mark_attandance);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCurrentDatandTime();
        btnMarkAttandance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPunchButtonClicked = true;
                getCurrentDatandTime();
            }
        });
    }

    private void SavePunchDatainDb(OfflinePunchInModel offlinePunchInModel) {
        android.os.AsyncTask.execute(() -> {
            DatabaseClient.getInstance(requireActivity()).getAppDatabase().passengerDao().insertOfflinePunchInData(offlinePunchInModel);
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(requireContext(), "Punch Submitted successfully.", Toast.LENGTH_SHORT).show();
                    edtComment.setText("");
                    tvLocation.setText("");
                }
            });
        });
    }

    private void getCurrentDatandTime() {
        new Thread(() -> {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(new HttpGet("https://google.com/"));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    String dateStr = response.getFirstHeader("Date").getValue();
                    DateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
                    inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    Date d = inputFormat.parse(dateStr);

                    DateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    DateFormat currentTime = new SimpleDateFormat("HH:mm:s", Locale.US);

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            assert d != null;
                            edtDate.setText(currentDate.format(d));
                            edtTime.setText(currentTime.format(d));

                            if (isPunchButtonClicked)
                                savePunchDetail();
                        }
                    });
                } else {

                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.d("Response", e.getMessage());
            } catch (IOException e) {
                Log.d("Response", e.getMessage());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void savePunchDetail() {

        if (edtDate.getText().toString().trim().equals("")) {
            Toast.makeText(requireActivity(), "Required date and time", Toast.LENGTH_SHORT).show();
        } else if (isLocationEnabled(requireActivity())) {
            OfflinePunchInModel offlinePunchInModel = new OfflinePunchInModel();
            offlinePunchInModel.setUserName(userName);
            offlinePunchInModel.setCurrentdate(edtDate.getText().toString().trim());
            offlinePunchInModel.setCurrenttime(edtTime.getText().toString().trim());
            offlinePunchInModel.setLatitude(String.valueOf(latitude));
            offlinePunchInModel.setLongitude(String.valueOf(longitude));
            offlinePunchInModel.setLocation(locationValue);
            offlinePunchInModel.setComment(edtComment.getText().toString().trim());
            SavePunchDatainDb(offlinePunchInModel);

        } else {
            Toast.makeText(requireActivity(), "Turn on your device location!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000); // Update location every second
        mLocationRequest.setFastestInterval(4000);
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            setValue();
        }
    }

    private void setValue() {
        String strAddress = getAddressFromLocation(requireActivity(), latitude, longitude);
        Log.e("", "setValue: " + strAddress);
        locationValue = strAddress;
        tvLocation.setText(locationValue);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            locationValue = getAddressFromLocation(requireActivity(), latitude, longitude);
            tvLocation.setText(locationValue);
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        initLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }


    private void initLocation() {
        if (!checkAndRequestPermissions(requireActivity())) {
        }
    }

    public static boolean checkAndRequestPermissions(Context context) {

        int locationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public static String getAddressFromLocation(Context context, final double latitude, final double longitude) {
        String straddress = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                if (address.getAddressLine(0) != null && address.getAddressLine(0).length() > 0 && !address.getAddressLine(0).contentEquals("null")) {
                    sb.append(address.getAddressLine(0)).append("\n");
                } else {

                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                }
                straddress = sb.toString();
                //Log.e("leaddress","@"+straddress);
            }
        } catch (IOException e) {
            //Log.e(TAG, "Unable connect to Geocoder", e);
        }
        return straddress;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
