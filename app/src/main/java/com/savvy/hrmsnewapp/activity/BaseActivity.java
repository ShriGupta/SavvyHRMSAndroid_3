package com.savvy.hrmsnewapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

/**
 * Changes done by Jilendra singh - 11 SEPT 2021
 */
public class BaseActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Dialog dialog;
    ProgressDialog pDialog;
    final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        sharedpreferences = getSharedPreferences("IP_ADDRESS_CONSTANT", MODE_PRIVATE);
        String showpassword = sharedpreferences.getString("ShowPassword", "");
        if (showpassword.equals("1")) {
            MenuItem logout = menu.findItem(R.id.action_change_password);
            logout.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home_dashboard) {
            Intent intent = new Intent(BaseActivity.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }


        if (id == R.id.action_settings) {
            editor.putBoolean(Constants.LOGIN_STATUS, false);
            editor.putBoolean(Constants.DASHBOARD_STATUS, false);
            editor.putString("USER_ID", "");
            editor.putString("PASSWORD", "");
            editor.commit();

            Intent intent = new Intent(this, LoginActivity_1.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_change_password) {

            if (!(Constants.LEAVE_APPLY_ACTIVITY > 1)) {
                try {
                    Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public void showProgressDialog() {
        pDialog = new ProgressDialog(BaseActivity.this);
        pDialog.setMessage("loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        try {
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToast(String message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                response -> {
                    try {
                        // Response 1 means data send to server Response 0 means data not send to server
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
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_demo);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvMessage = dialog.findViewById(R.id.tv_error_textview);
        tvMessage.setText(message);
        Button btnDialogOk = dialog.findViewById(R.id.btn_ok);
        btnDialogOk.setOnClickListener(v -> {
            try {
                dialog.dismiss();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialog.show();

    }

    public boolean isGPSTurnedOn() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void moveToLocationSetting() {
        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(callGPSSettingIntent, 101);
    }

    public void showMockAlert(Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
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

    public boolean chwckNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isNetworkAvailable() {
        if (chwckNetworkAvailable()) {
            return true;
        } else {
            Toast.makeText(BaseActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void showOfflineAlert(Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("Would you mark attandance offline?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(context, OfflineLoginActivity.class));
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

    public void hideKeyBoard(){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

     protected void setUpToolBar(){
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
    }
}
