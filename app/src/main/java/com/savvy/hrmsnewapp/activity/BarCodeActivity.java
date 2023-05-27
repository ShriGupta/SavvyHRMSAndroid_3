package com.savvy.hrmsnewapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.ListenableFuture;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.classes.QRCodeImageAnalyzer;
import com.savvy.hrmsnewapp.interfaces.QRCodeFoundListener;
import com.savvy.hrmsnewapp.model.QRLoginModel;
import com.savvy.hrmsnewapp.utils.Config;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BarCodeActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    String IP_ADDRESS;
    String localDataInsertInterval = "";
    String serverDataUploadInterval = "";
    SharedPreferences.Editor shareIp;
    SharedPreferences.Editor qrEditor;
    SharedPreferences shared;
    private final String IP_ADDRESS_CONSTANT = "IP_ADDRESS_CONSTANT";
    private final String MY_PREFS_NAME = "MyPrefsFile";
    String deviceId_id, regId;
    CoordinatorLayout coordinatorLayout;
    static int QRCodeCount;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        deviceId_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        shared = getSharedPreferences(Config.SHARED_PREF, 0);
        regId = (shared.getString("regId", ""));
        checkIPAddress();
        previewView = findViewById(R.id.activity_main_previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();
        setUpToolBar();
    }

    private void checkIPAddress() {
        if (getIntent().getExtras() != null) {
            IP_ADDRESS = getIntent().getExtras().getString("ip_address");
        }
        if ((IP_ADDRESS != null) && (!IP_ADDRESS.equals(""))) {
            Constants.IP_ADDRESS = "http://" + IP_ADDRESS + "/savvymobile";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(BarCodeActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(720, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {

                if (QRCodeCount == 0) {
                    QRCodeCount++;
                    qrLoginRequest(_qrCode);
                }
            }

            @Override
            public void qrCodeNotFound() {
                Log.e("TAG", "onQRCode Not Found: ");

            }
        }));
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
    }

    private void qrLoginRequest(String _qrCode) {
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/AuthenticateLoginUserPostFromQRCode";
            JSONObject params_final = new JSONObject();
            params_final.put("QRCode", _qrCode);

            RequestQueue requestQueue = Volley.newRequestQueue(BarCodeActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            QRLoginModel qrLoginModel = new QRLoginModel();
                            Log.e("TAG", "onResponse: " + response);
                            try {

                                JSONObject jsonobj = response.getJSONObject("AuthenticateLoginUserPostFromQRCodeResult");
                                qrLoginModel.setEmployeeId(jsonobj.getString("employeeId"));
                                qrLoginModel.setEmployeeCode(jsonobj.getString("employeeCode"));
                                qrLoginModel.setEmployeeName(jsonobj.getString("employeeName"));
                                qrLoginModel.setErrorMessage(jsonobj.getString("errorMessage"));
                                qrLoginModel.setIsValidUser(jsonobj.getString("isValidUser"));
                                qrLoginModel.setPassword(jsonobj.getString("password"));
                                qrLoginModel.setSecurityToken(jsonobj.getString("securityToken"));
                                qrLoginModel.setUserName(jsonobj.getString("userName"));
                                qrLoginModel.setGroupId(jsonobj.getString("groupId"));
                                qrLoginModel.setShowPassword(jsonobj.getString("ShowPassword"));
                                qrLoginModel.setClientLogoPath(jsonobj.getString("clientLogoPath"));
                                qrLoginModel.setTrackMeIntervalSecond(jsonobj.getString("trackMeIntervalSecond"));
                                qrLoginModel.setEmployeePhotoPath(jsonobj.getString("employeePhotoPath"));
                                qrLoginModel.setLastPasswordChange(jsonobj.getString("LastPasswordChange"));
                                qrLoginModel.setRegularExpressionAndroid(jsonobj.getString("regularExpressionAndroid"));
                                qrLoginModel.setAccessModules(jsonobj.getString("accessModules"));
                                qrLoginModel.setLastPasswordChange(jsonobj.getString("LastPasswordChange"));

                                try {
                                    if (qrLoginModel.getTrackMeIntervalSecond().contains(",")) {
                                        String[] separatedString = qrLoginModel.getTrackMeIntervalSecond().split(",");
                                        localDataInsertInterval = separatedString[0].trim();
                                        serverDataUploadInterval = separatedString[1].trim();
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                if (qrLoginModel.getIsValidUser().equals("YES")) {
                                    qrEditor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                    qrEditor.putString("USER_ID", qrLoginModel.getUserName());
                                    qrEditor.putString("PASSWORD", qrLoginModel.getPassword());
                                    qrEditor.putString("IPADDRESS", Constants.IP_ADDRESS);
                                    qrEditor.putString("Token", qrLoginModel.getSecurityToken());
                                    qrEditor.putString("EmpoyeeId", qrLoginModel.getEmployeeId());
                                    qrEditor.putString("EmpoyeeName", qrLoginModel.getEmployeeName());
                                    qrEditor.putString("UserName", qrLoginModel.getUserName());
                                    qrEditor.putString("GroupId", qrLoginModel.getGroupId());
                                    qrEditor.putString("AccessModules", qrLoginModel.getAccessModules().toString());
                                    qrEditor.putString("EmpPhotoPath", qrLoginModel.getEmployeePhotoPath());
                                    qrEditor.putString("localDataInsertInterval", localDataInsertInterval);
                                    qrEditor.putString("serverDataUploadInterval", serverDataUploadInterval);
                                    qrEditor.putString("EmployeeCode", qrLoginModel.getEmployeeCode());
                                    qrEditor.putString(Constants.LastPasswordChange, qrLoginModel.getLastPasswordChange());
                                    qrEditor.putString(Constants.regularExpressionAndroid, qrLoginModel.getRegularExpressionAndroid());
                                    qrEditor.putBoolean(Constants.LOGIN_STATUS, true);
                                    qrEditor.putString(Constants.EMPLOYEE_ID_FINAL, qrLoginModel.getEmployeeId());
                                    qrEditor.putString(Constants.TOKEN, qrLoginModel.getSecurityToken());
                                    qrEditor.apply();

                                    shareIp = getSharedPreferences(IP_ADDRESS_CONSTANT, MODE_PRIVATE).edit();
                                    shareIp.putString("IP_ADDRESS", Constants.IP_ADDRESS);
                                    shareIp.putBoolean("IP_ADDRESS_STATUS", true);
                                    shareIp.putString("CLIENT_LOGO_URL", qrLoginModel.getClientLogoPath());
                                    shareIp.putString("ShowPassword", qrLoginModel.getShowPassword());
                                    shareIp.apply();

                                    try {
                                        callSendUserInfoApi(qrLoginModel);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                } else if (qrLoginModel.getIsValidUser().equals("NO")) {
                                    Utilities.showDialog(coordinatorLayout, "Please Enter Correct User Name And Password");
                                    shareIp = getSharedPreferences(IP_ADDRESS_CONSTANT, MODE_PRIVATE).edit();
                                    shareIp.putString("IP_ADDRESS", Constants.IP_ADDRESS);
                                    shareIp.putBoolean("IP_ADDRESS_STATUS", true);
                                    shareIp.commit();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(BarCodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                if (Constants.IP_ADDRESS.equals("") && !Constants.IP_ADDRESS_STATUS) {
                                    Utilities.showDialog(coordinatorLayout, "Network is Slow");
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            new Text_To_Speech().speech(BarCodeActivity.this, "Network is Slow");
                                        }
                                    }, 1000 * 2);
                                    Intent in = new Intent(BarCodeActivity.this, LoginActivity_1.class);
                                    startActivity(in);
                                    BarCodeActivity.this.finish();
                                    overridePendingTransition(0, 0);
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(BarCodeActivity.this, "Invalid IP Address!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callSendUserInfoApi(QRLoginModel qrLoginModel) {
        try {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/AuthenticatedLoggedUserDeviceInfoPost";
            JSONObject params_final = new JSONObject();
            params_final.put("employeeId", qrLoginModel.getEmployeeId());
            params_final.put("deviceId", deviceId_id);
            params_final.put("registrationId", regId);
            if (isNetworkAvailable()) {
                RequestQueue requestQueue = Volley.newRequestQueue(BarCodeActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                        response -> {
                            try {
                                Intent intent;
                                String result = response.getString("AuthenticatedLoggedUserDeviceInfoPostResult");
                                if (result.equals("NOTALLOW")) {
                                    if (coordinatorLayout.getVisibility() == View.GONE)
                                        coordinatorLayout.setVisibility(View.VISIBLE);
                                    Utilities.showDialog(coordinatorLayout, "You have already Registered with another Mobile. Please contact to Administrator/HR to allow.");
                                } else {
                                    if (qrLoginModel.getLastPasswordChange() != null && qrLoginModel.getLastPasswordChange().equalsIgnoreCase("0")) {
                                        intent = new Intent(BarCodeActivity.this, ChangePasswordActivity.class);
                                        startActivity(intent);
                                        BarCodeActivity.this.finish();

                                    } else {
                                        intent = new Intent(BarCodeActivity.this, DashBoardActivity.class);
                                        startActivity(intent);
                                        BarCodeActivity.this.finish();
                                    }
                                }
                            } catch (Exception ex) {
                            }

                        }, error -> {
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

    @Override
    protected void onResume() {
        super.onResume();
        QRCodeCount = 0;
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
            Toast.makeText(BarCodeActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}