package com.savvy.hrmsnewapp.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.OfflineCredentialModel;
import com.savvy.hrmsnewapp.utils.Config;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.internal.Util;

public class LoginActivity_1 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "";
    final String IP_ADDRESS_CONSTANT = "IP_ADDRESS_CONSTANT";
    final String MY_PREFS_NAME = "MyPrefsFile";
    EditText edt_username;
    EditText edt_password;
    EditText edt_ipaddress;
    CustomTextView forgotPassword;
    String CONSTANT_IP_ADDRESS = "";
    SharedPreferences.Editor shareIp;
    CoordinatorLayout coordinatorLayout;
    private Button getstartedBtn;
    String EMP_NAME = "";
    SharedPreferences sharedpreferencesIP;
    SharedPreferences.Editor editor;
    int socketTimeout = 30000;
    private String empoyeeId;
    private SharedPreferences shared;
    private String regId;
    private String deviceId_id;
    private String LastPasswordChange;
    private ImageView imageView;
    String clienLogoUrl = "";
    ImageView showImage, hideImage;
    String showpassword = "";
    String trackMeIntervalSecond = "";
    String localDataInsertInterval = "";
    String serverDataUploadInterval = "";
    TextView tvVersionName, tvLogingWithQRCode;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_1);
        init();
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        } else {

                            // Get new FCM registration token
                            regId = task.getResult();
                            Log.e("regId>>", "<><>" + regId);
                            storeRegIdInPref(regId);
                        }

                    });
        } catch (Exception ignored) {
        }
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }

    @SuppressLint("HardwareIds")
    private void init() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        edt_ipaddress = (EditText) findViewById(R.id.edt_ipaddress);
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        getstartedBtn = (Button) findViewById(R.id.bt_getstarted);
        showImage = (ImageView) findViewById(R.id.show_Password);
        hideImage = (ImageView) findViewById(R.id.hide_Password);
        hideImage.setVisibility(View.VISIBLE);
        imageView = (ImageView) findViewById(R.id.clientLogo);
        forgotPassword = (CustomTextView) findViewById(R.id.txv_forgotpwd);
        forgotPassword.setOnClickListener(this);
        tvVersionName = findViewById(R.id.tv_version_name);
        tvLogingWithQRCode = findViewById(R.id.tv_login_with_qr);
        try {
            String versionName = "Version - " + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            tvVersionName.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sharedpreferencesIP = getSharedPreferences("IP_ADDRESS_CONSTANT", MODE_PRIVATE);
        Constants.IP_ADDRESS = sharedpreferencesIP.getString("IP_ADDRESS", "");
        Constants.IP_ADDRESS_STATUS = sharedpreferencesIP.getBoolean("IP_ADDRESS_STATUS", false);
        clienLogoUrl = sharedpreferencesIP.getString("CLIENT_LOGO_URL", "");
        showpassword = sharedpreferencesIP.getString("ShowPassword", "");
        assert showpassword != null;
        if (showpassword.equals("1")) {
            forgotPassword.setVisibility(View.GONE);
        }
        if (!Constants.IP_ADDRESS.equals("") && Constants.IP_ADDRESS_STATUS) {
            edt_ipaddress.setVisibility(View.INVISIBLE);
            if (!clienLogoUrl.equals("")) {
                Picasso.get().load(clienLogoUrl).placeholder(R.drawable.companylogo).error(R.drawable.companylogo).noFade().into(imageView);
                imageView.setVisibility(View.VISIBLE);
            }
        } else {
            edt_ipaddress.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        }

        getstartedBtn.setOnClickListener(this);
        deviceId_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        shared = getSharedPreferences(Config.SHARED_PREF, 0);
        regId = (shared.getString("regId", ""));
        hideImage.setOnClickListener(view -> {
            edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            hideImage.setVisibility(View.GONE);
            showImage.setVisibility(View.VISIBLE);
        });
        showImage.setOnClickListener(view -> {
            edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            hideImage.setVisibility(View.VISIBLE);
            showImage.setVisibility(View.GONE);
        });


        loginWithSavedCredential();
        tvLogingWithQRCode.setOnClickListener(view -> loginWithQRCode());
    }

    private void saveOfflineUser(String userName, String password) {
        android.os.AsyncTask.execute(() -> {
            List<OfflineCredentialModel> list = DatabaseClient.getInstance(LoginActivity_1.this).getAppDatabase().passengerDao().getAllOfflineUser();
            if (!(list.size() > 0)) {
                OfflineCredentialModel offlineCredentialModel = new OfflineCredentialModel();
                offlineCredentialModel.setUsername(userName);
                offlineCredentialModel.setPassword(password);
                DatabaseClient.getInstance(LoginActivity_1.this).getAppDatabase().passengerDao().insertOfflineUser(offlineCredentialModel);
            }
        });
    }

    private void loginWithSavedCredential() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String username = bundle.getString("username");
            String password = bundle.getString("password");
            String ipaddress = bundle.getString("IPADDRESS");
            LoginAsynTask(username, password, ipaddress);

        }
    }

    private void loginWithQRCode() {
        if (!Constants.IP_ADDRESS.equals("")) {
            Intent intent = new Intent(LoginActivity_1.this, BarCodeActivity.class);
            startActivity(intent);
        } else {
            if (edt_ipaddress.getText().toString().trim().equals("")) {
                Toast.makeText(LoginActivity_1.this, "IP ADDRESS Required!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(LoginActivity_1.this, BarCodeActivity.class);
                intent.putExtra("ip_address", edt_ipaddress.getText().toString().trim());
                startActivity(intent);

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_getstarted:

                String getusername = edt_username.getText().toString().trim();
                String getpwd = edt_password.getText().toString().trim();

                if (!Constants.IP_ADDRESS.equals("") && Constants.IP_ADDRESS_STATUS) {
                    if ((Utilities.isNetworkAvailable(LoginActivity_1.this))) {
                        if (getusername.length() > 0 && getpwd.length() > 0) {
                            try {
                                LoginAsynTask(getusername, getpwd, Constants.IP_ADDRESS);
//                                Log.d(TAG,getusername+" "+getpwd+" "+Constants.IP_ADDRESS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.BLANK_FIELD_TEXT);
                        }
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }

                } else {
                    String getipaddress = edt_ipaddress.getText().toString().trim();
                    CONSTANT_IP_ADDRESS = "http://" + getipaddress + "/savvymobile";
                    if (Utilities.isNetworkAvailable(LoginActivity_1.this)) {
                        if (getusername.length() > 0 && getpwd.length() > 0) {
                            try {
                                LoginAsynTask(getusername, getpwd, CONSTANT_IP_ADDRESS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utilities.showDialog(coordinatorLayout, ErrorConstants.BLANK_FIELD_TEXT);
                        }
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }
                }
                break;
            case R.id.txv_forgotpwd:
                String username = edt_username.getText().toString().trim();
                if (!Constants.IP_ADDRESS.equals("") && Constants.IP_ADDRESS_STATUS) {
                    CONSTANT_IP_ADDRESS = Constants.IP_ADDRESS;
                    if ((Utilities.isNetworkAvailable(LoginActivity_1.this))) {
                        if (username.length() > 0)
                            new ForgotpassAsync().execute();
                        else
                            Utilities.showDialog(coordinatorLayout, "Please Enter User Name.");
                    } else
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);

                } else {
                    String getipaddress = edt_ipaddress.getText().toString().trim();
                    CONSTANT_IP_ADDRESS = "http://" + getipaddress + "/savvymobile";
                    if (Utilities.isNetworkAvailable(LoginActivity_1.this)) {
                        if (username.length() > 0 && getipaddress.length() > 0) {
                            new ForgotpassAsync().execute();
                        } else {
                            Utilities.showDialog(coordinatorLayout, "Please Enter User Name.");
                        }
                    } else {
                        Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
                    }
                }
                break;

            default:
                break;
        }
    }

    private String getDeviceDetail() {
        return "Model :" + Build.MODEL + "/" +
                "Device: " + Build.DEVICE + "/" +
                "Manufacturer: " + Build.MANUFACTURER + "/" +
                "Board: " + Build.BOARD + "/" +
                "Brand: " + Build.BRAND + "/" +
                "OS Version: " + Build.VERSION.SDK_INT + "/" +
                "Android Version: " + Build.VERSION.RELEASE + "/" +
                "GPS_Status: " + Utilities.isGPSTurnedOn(this);
    }

    private class ForgotpassAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = CONSTANT_IP_ADDRESS + "/SavvyMobileService.svc/ForgotPassword/" + edt_username.getText().toString().trim();
                JSONParser jParser = new JSONParser(LoginActivity_1.this); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(url, "GET");
                if (json != null) {
                    Log.e("JSON result", json.toString());
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dismissProgressDialog();
            try {
                String[] arrayRes = result.split("\\+");
                if (arrayRes.length > 1)
                    Toast.makeText(LoginActivity_1.this, "" + arrayRes[1], Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(LoginActivity_1.this, "" + arrayRes[1], Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(LoginActivity_1.this, "Invalid IP Address Or User Name..", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void LoginAsynTask(final String stremail, final String strpassword, final String stripaddress) {

        if (isNetworkAvailable()) {
            saveOfflineUser(stremail, strpassword);
            showProgressDialog();
            try {
                String url = stripaddress + "/SavvyMobileService.svc/AuthenticateLoginSavvyUserPost";
                JSONObject params_final = new JSONObject();
                params_final.put("userid", stremail);
                params_final.put("password", strpassword);
                // params_final.put("deviceDetails", getDeviceDetail());

                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity_1.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                dismissProgressDialog();
                                //new Log("On response in Login",""+response);
                                Log.d(TAG, response.toString() + "Login responce");
                                int len = (String.valueOf(response)).length();
                                try {
                                    JSONObject jsonobj = response.getJSONObject("AuthenticateLoginUserPostResult");
                                    String accessModules = jsonobj.getString("accessModules");
                                    empoyeeId = jsonobj.getString("employeeId");
                                    String employeeCode = jsonobj.getString("employeeCode");
                                    String empoyeeName = jsonobj.getString("employeeName");
                                    String errorMessage = jsonobj.getString("errorMessage");
                                    String isValidUser = jsonobj.getString("isValidUser");
                                    String password = jsonobj.getString("password");
                                    String securityToken = jsonobj.getString("securityToken");
                                    String userName = jsonobj.getString("userName");
                                    String groupId = jsonobj.getString("groupId");
                                    String mobile = null,address = null;
                                    try{
                                         mobile=jsonobj.getString("ContactNo");
                                         address=jsonobj.getString("EmployeeAddress");
                                    }catch (Exception e){e.printStackTrace();}
                                    

                                    try {
                                        showpassword = jsonobj.getString("ShowPassword");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        clienLogoUrl = jsonobj.getString("clientLogoPath");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (!clienLogoUrl.equals("")) {
                                        try {
                                            Picasso.get().load(clienLogoUrl).placeholder(R.drawable.companylogo).error(R.drawable.companylogo).noFade().into(imageView);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    try {
                                        trackMeIntervalSecond = jsonobj.getString("trackMeIntervalSecond");
                                        if (trackMeIntervalSecond.contains(",")) {
                                            String[] separatedString = trackMeIntervalSecond.split(",");
                                            localDataInsertInterval = separatedString[0].trim();
                                            serverDataUploadInterval = separatedString[1].trim();
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        dismissProgressDialog();
                                    }


                                    String employeePhotoPath = jsonobj.getString("employeePhotoPath");
                                    LastPasswordChange = jsonobj.getString("LastPasswordChange");
                                    String regularExpressionAndroid = jsonobj.getString("regularExpressionAndroid");
                                    String gpsMendatory = jsonobj.getString("regularExpressionAndroid");
                                    EMP_NAME = jsonobj.getString("employeeName");
                                    if (isValidUser.equals("YES")) {
                                        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                        editor.putString("USER_ID", stremail);
                                        editor.putString("PASSWORD", strpassword);
                                        editor.putString("IPADDRESS", stripaddress);
                                        editor.putString("Token", securityToken);
                                        editor.putString("EmpoyeeId", empoyeeId);
                                        editor.putString("EmpoyeeName", empoyeeName);
                                        editor.putString("UserName", userName);
                                        editor.putString("GroupId", groupId);
                                        editor.putString("Mobile", mobile);
                                        editor.putString("Address", address);
                                        editor.putString("AccessModules", accessModules);
                                        editor.putString("EmpPhotoPath", employeePhotoPath);
                                        editor.putString("localDataInsertInterval", localDataInsertInterval);
                                        editor.putString("serverDataUploadInterval", serverDataUploadInterval);
                                        editor.putString("EmployeeCode", employeeCode);
                                        editor.putString(Constants.LastPasswordChange, LastPasswordChange);
                                        editor.putString(Constants.regularExpressionAndroid, regularExpressionAndroid);
                                        editor.putBoolean(Constants.LOGIN_STATUS, true);
                                        editor.putString(Constants.EMPLOYEE_ID_FINAL, empoyeeId);
                                        editor.putString(Constants.TOKEN, securityToken);
                                        editor.apply();

                                        shareIp = getSharedPreferences(IP_ADDRESS_CONSTANT, MODE_PRIVATE).edit();
                                        Constants.IP_ADDRESS = stripaddress;
                                        shareIp.putString("IP_ADDRESS", Constants.IP_ADDRESS);
                                        shareIp.putBoolean("IP_ADDRESS_STATUS", true);
                                        shareIp.putString("CLIENT_LOGO_URL", clienLogoUrl);
                                        shareIp.putString("ShowPassword", showpassword);
                                        shareIp.apply();

                                        try {
                                            callSendUserInfoApi(stremail, strpassword);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                    } else if (isValidUser.equals("NO")) {
                                        Utilities.showDialog(coordinatorLayout, "Please Enter Correct User Name And Password");
                                        shareIp = getSharedPreferences(IP_ADDRESS_CONSTANT, MODE_PRIVATE).edit();
                                        Constants.IP_ADDRESS = stripaddress;
                                        shareIp.putString("IP_ADDRESS", Constants.IP_ADDRESS);
                                        shareIp.putBoolean("IP_ADDRESS_STATUS", true);
                                        shareIp.commit();
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.e("Error In", "" + ex.getMessage());
                                    if (Constants.IP_ADDRESS.equals("") && !Constants.IP_ADDRESS_STATUS) {
                                        Utilities.showDialog(coordinatorLayout, "Network is Slow");
                                        Intent in = new Intent(LoginActivity_1.this, LoginActivity_1.class);
                                        startActivity(in);
                                        LoginActivity_1.this.finish();
                                        overridePendingTransition(0, 0);
                                    }
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        checkClientIPAddress(stripaddress);
                        if (error.getNetworkTimeMs() > 30000) {
                            String getipaddress = edt_ipaddress.getText().toString().trim();
                            CONSTANT_IP_ADDRESS = "http://" + getipaddress + "/savvymobile";
                            Log.e("Ip Adderss", CONSTANT_IP_ADDRESS);
                            Log.e("UserName", stremail);
                            Log.e("Password", strpassword);
                            try {
                                LoginAsynTask(stremail, strpassword, CONSTANT_IP_ADDRESS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (Constants.IP_ADDRESS.equals("") && !Constants.IP_ADDRESS_STATUS) {
                            try {
                                Utilities.showDialog(coordinatorLayout, "Invalid IP Address");
                            } catch (Exception e) {
                                dismissProgressDialog();
                            }
                        } else {
                            Utilities.showDialog(coordinatorLayout, Utilities.handleVolleyError(error));
                        }
                    }
                });

                socketTimeout = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void checkClientIPAddress(String stripaddress) {

        String url = stripaddress;
        url = url.replace("/savvymobile", "");
        String finalUrl = url;
        new Thread(() -> {
            try {
                URL url1 = new URL(finalUrl);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                } else {
                    showOfflineAlert(LoginActivity_1.this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void callSendUserInfoApi(String stremail, String strpassword) {
        try {
            showProgressDialog();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/AuthenticatedLoggedUserDeviceInfoPost";
            JSONObject params_final = new JSONObject();
            params_final.put("employeeId", empoyeeId);
            params_final.put("deviceId", deviceId_id);
            params_final.put("registrationId", regId);
            if (Utilities.isNetworkAvailable(LoginActivity_1.this)) {
                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity_1.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        response -> {
                            try {
                                dismissProgressDialog();
                                Intent intent;
                                String result = response.getString("AuthenticatedLoggedUserDeviceInfoPostResult");
                                if (result.equals("NOTALLOW")) {
                                    if (coordinatorLayout.getVisibility() == View.GONE)
                                        coordinatorLayout.setVisibility(View.VISIBLE);
                                    Utilities.showDialog(coordinatorLayout, "You have already Registered with another Mobile. Please contact to Administrator/HR to allow.");
                                } else {
                                    if (LastPasswordChange.equalsIgnoreCase("0")) {
                                        intent = new Intent(LoginActivity_1.this, ChangePasswordActivity.class);
                                        startActivity(intent);
                                        LoginActivity_1.this.finish();
                                    } else {
                                        intent = new Intent(LoginActivity_1.this, DashBoardActivity.class);
                                        startActivity(intent);
                                        LoginActivity_1.this.finish();
                                    }
                                }
                            } catch (Exception ex) {

                            }

                        }, error -> {
                    dismissProgressDialog();

                }) {

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

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit ?");
        builder.setPositiveButton("Yes", (dialog, which) -> finish());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void showProgressDialog() {
        pDialog = new ProgressDialog(LoginActivity_1.this);
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

    public void showOfflineAlert(Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("Would you mark attandance offline?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialogInterface, i) -> startActivity(new Intent(context, OfflineLoginActivity.class)));
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
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
            Toast.makeText(LoginActivity_1.this, "No Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}


