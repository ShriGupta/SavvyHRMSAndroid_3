package com.savvy.hrmsnewapp.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public abstract class BaseFragment extends Fragment {
    protected CoordinatorLayout coordinatorLayout;
    ProgressDialog pDialog;
    Dialog dialog;

    public void showProgressDialog() {
        pDialog = new ProgressDialog(requireActivity());
        pDialog.setMessage("loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        try {
            pDialog.dismiss();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void showToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void moveToLocationSetting() {
        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(callGPSSettingIntent, 101);
    }

    public void sendErrorToServer(String employeeId, String error1, String error2, String error3, String error4, String error5) {
        String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SendErrorToServer";
        JSONObject params_final = new JSONObject();
        try {
            params_final.put("employeeId", employeeId);
            params_final.put("Error1", error1);
            params_final.put("Error2", error2);
            params_final.put("Error3", error3);
            params_final.put("Error4", error4);
            params_final.put("Error5", error5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                response -> {
                    try {
                        Log.e("TAG", "onResponse: " + response.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }, error -> {
            showToast(error.getMessage());
        });

        int socketTimeout = 3000000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);

    }

    public void showLocationErrorDialog(String message) {
        dialog = new Dialog(requireActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_demo);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvMessage = dialog.findViewById(R.id.tv_error_textview);
        tvMessage.setText(message);
        Button btnDialogOk = dialog.findViewById(R.id.btn_ok);
        btnDialogOk.setOnClickListener(v -> {
            try {
                dialog.dismiss();
                if (checkLocationOnorOff()) {
                    showToast("Please wait while fetching your location...");
                } else {
                    moveToLocationSetting();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialog.show();

    }

    public Boolean checkLocationOnorOff() {
        LocationManager lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled;
    }

    public boolean isGPSTurnedOn() {
        LocationManager manager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isPermissionGranted(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
    }

    public boolean checkNEtworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED);

    }

    public boolean isNetworkAvailable(Context context) {
        if (checkNEtworkAvailable(context)) {
            return true;
        } else {
            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isMockLocationON(Location location) {
        boolean isMockLocation=false;
        try{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location != null && location.isFromMockProvider()) {
            isMockLocation = true;
        } else {
            isMockLocation = !Settings.Secure.getString(Objects.requireNonNull(getActivity()).getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
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

}