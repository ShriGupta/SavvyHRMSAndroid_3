package com.savvy.hrmsnewapp.fragment;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.FaceAttendanceActivity;
import com.savvy.hrmsnewapp.adapter.TeamFaceAdapter;
import com.savvy.hrmsnewapp.interfaces.OnTeamButtonClickListener;
import com.savvy.hrmsnewapp.teamFaceModel.TeamFaceDataModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class TeamFaceAttendanceFragment extends BaseFragment implements OnTeamButtonClickListener {

    RecyclerView recyclerView;
    TeamFaceAdapter teamFaceAdapter;
    List<TeamFaceDataModel> teamFaceDataList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String token, employeeId;
    FusedLocationProviderClient fusedClient;
    private LocationRequest mRequest;
    private LocationCallback mCallback;
    String locationAddress = "";
    String geoString;
    String latitude;
    String longitude;
    EditText edtSearchView;
    String countryName = "";
    public static final int REQUEST_LOCATION_PERMISSION = 101;
    // SearchView edtSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        token = (sharedpreferences.getString("Token", ""));
        employeeId = (sharedpreferences.getString("EmpoyeeId", ""));
        fusedClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(ContentValues.TAG, "locationResult null");
                    return;
                }
                for (Location loc : locationResult.getLocations()) {
                    try {
                        Log.e(ContentValues.TAG, "onLocationResult: " + loc.getLatitude() + " , " + loc.getLongitude());
                        if (checkMockLocation(loc)) {
                            mockAlertDialog();
                            locationAddress = null;
                        } else {
                            locationAddress = Utilities.getAddressFromLocation(getActivity(), loc);
                            latitude = String.valueOf(loc.getLatitude());
                            longitude = String.valueOf(loc.getLongitude());
                            Date date = new Date(loc.getTime());

                            countryName = Utilities.getCountryFromLocation(getActivity(), loc);
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.US);
                            geoString = sdf.format(date) + " " + countryName;

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d(ContentValues.TAG, "locationAvailability is " + locationAvailability.isLocationAvailable());
                super.onLocationAvailability(locationAvailability);
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        startLocationUpdate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_team_face_attendance_layout, container, false);
        edtSearchView = view.findViewById(R.id.edt_emp_search);
        recyclerView = view.findViewById(R.id.rv_team_face_recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        teamFaceAdapter = new TeamFaceAdapter(requireActivity(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(teamFaceAdapter);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                teamFaceAdapter.getFilter().filter(editable.toString().trim());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        teamFaceDataList.clear();
        teamFaceAdapter.clearItems();
        getTeamFaceData();
    }

    private void getTeamFaceData() {
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/MyHierarchyForMarkInOutPost";
            final JSONObject params_final = new JSONObject();
            params_final.put("employeeId", employeeId);
            params_final.put("securityToken", token);
            Log.e(TAG, "getTeamFaceData: " + url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray jarray = new JSONArray(response.getString("MyHierarchyForMarkInOutPostResult"));
                                TeamFaceDataModel teamFaceDataModel;
                                if (jarray.length() > 0) {
                                    for (int i = 0; i < jarray.length(); i++) {
                                        teamFaceDataModel = new TeamFaceDataModel();
                                        teamFaceDataModel.setAttendanceDate(jarray.getJSONObject(i).getString("AttendanceDate"));
                                        teamFaceDataModel.setEnableStatus(jarray.getJSONObject(i).getString("EnableStatus"));
                                        teamFaceDataModel.setIntime(jarray.getJSONObject(i).getString("Intime"));
                                        teamFaceDataModel.setOutTime(jarray.getJSONObject(i).getString("OutTime"));
                                        teamFaceDataModel.setBranch(jarray.getJSONObject(i).getString("branch"));
                                        teamFaceDataModel.setDepartment(jarray.getJSONObject(i).getString("department"));
                                        teamFaceDataModel.setDesignation(jarray.getJSONObject(i).getString("designation"));
                                        teamFaceDataModel.setEmployeeCode(jarray.getJSONObject(i).getString("employeeCode"));
                                        teamFaceDataModel.setEmployeeId(jarray.getJSONObject(i).getString("employeeId"));
                                        teamFaceDataModel.setEmployeeName(jarray.getJSONObject(i).getString("employeeName"));
                                        teamFaceDataModel.setErrorMessage(jarray.getJSONObject(i).getString("errorMessage"));
                                        teamFaceDataModel.setPhotoCode(jarray.getJSONObject(i).getString("photoCode"));
                                        teamFaceDataModel.setSupervisorIdFirst(jarray.getJSONObject(i).getString("supervisorIdFirst"));
                                        teamFaceDataList.add(teamFaceDataModel);
                                    }
                                    teamFaceAdapter.addItems(teamFaceDataList);

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();

                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    showToast(error.getMessage());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", sharedpreferences.getString("EMPLOYEE_ID_FINAL", ""));
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        if (checkLocationPermission()) {
            mRequest = new LocationRequest();
            mRequest.setInterval(5000);//time in ms; every ~10 seconds
            mRequest.setFastestInterval(2000);
            mRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedClient.requestLocationUpdates(mRequest, mCallback, Looper.getMainLooper());
        } else {
            askLocationPermission();
        }
    }

    boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void askLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            startLocationUpdate();
        } else {
            showToast("Location permission is not granted!");
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

    @Override
    public void onStop() {
        super.onStop();
        if (fusedClient != null) {
            fusedClient.removeLocationUpdates(mCallback);
        }
    }

    @Override
    public void onClick(int postion, String data, String attandanceDate, String punchMode) {
        try {
            if (!locationAddress.equals("")) {
                String encodeLocation = Base64.encodeToString(locationAddress.getBytes(), Base64.DEFAULT);
                String encodeGeoTime = Base64.encodeToString(geoString.getBytes(), Base64.DEFAULT);

                Intent intent = new Intent(getActivity(), FaceAttendanceActivity.class);
                intent.putExtra("LOCATION_ADDRESS", encodeLocation);
                intent.putExtra("GEO_STRING", encodeGeoTime);
                intent.putExtra("LATITUDE", latitude);
                intent.putExtra("LONGITUDE", longitude);
                intent.putExtra("REMARK", "");
                intent.putExtra("EmployeeCode", data);
                intent.putExtra("SIMPLE_FACE", "TEAM_FACE");
                intent.putExtra("PUNCHMODE", punchMode);
                intent.putExtra("ATTDATE", attandanceDate);
                intent.putExtra("COUNTRY_NAME", countryName);
                startActivity(intent);
            } else {
                showLocationErrorDialog("Please turn on your device location!");
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            sendErrorToServer(employeeId, "moving to face recognition : Exception", e.getMessage(), "", "", "");
        }
    }
}
