package com.savvy.hrmsnewapp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends BaseActivity {

    //
    EditText edt_oldPassword, edt_newPassword, edt_retypePassword;
    Button btn_submit, btn_back;
    String empoyeeId = "";
    String regularExpressionAndroid = "";
    String LastPasswordChange = "";
    CoordinatorLayout coordinatorLayout;
    Handler handler;
    Runnable rRunnable;
    CustomTextView txt_old, txt_new, txt_retype;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    private CustomTextView changeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        empoyeeId = (shared.getString("EmpoyeeId", ""));
        LastPasswordChange = (shared.getString(Constants.LastPasswordChange, ""));
        regularExpressionAndroid = (shared.getString(Constants.regularExpressionAndroid, ""));

        edt_oldPassword = (EditText) findViewById(R.id.chng_old_password);
        edt_newPassword = (EditText) findViewById(R.id.chng_new_password);
        edt_retypePassword = (EditText) findViewById(R.id.chng_retype_password);
        changeResult = (CustomTextView) findViewById(R.id.changeResult);

        txt_old = (CustomTextView) findViewById(R.id.title_oldPassword);
        txt_new = (CustomTextView) findViewById(R.id.title_newPassword);
        txt_retype = (CustomTextView) findViewById(R.id.title_retype);

        txt_old.setText(Html.fromHtml("Old Password" + "<font color=\"#E72A02\"><bold>" + " *" + "</bold></font>"));
        txt_new.setText(Html.fromHtml("New Password" + "<font color=\"#E72A02\"><bold>" + " *" + "</bold></font>"));
        txt_retype.setText(Html.fromHtml("Retype Password" + "<font color=\"#E72A02\"><bold>" + " *" + "</bold></font>"));

        btn_submit = (Button) findViewById(R.id.chng_submit_password);
        btn_back = (Button) findViewById(R.id.chng_back_password);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = edt_oldPassword.getText().toString().trim();
                String newPassword = edt_newPassword.getText().toString();
                String retypePassword = edt_retypePassword.getText().toString();

                String result = "New Password must contain at least one uppercase & one lowercase letter & two number & two special characters , and at least 9 or more characters required";

                if (oldPassword.equals("")) {
                    Utilities.showDialog(coordinatorLayout, "Please Enter Old Password.");
                } else if (!TextUtils.isEmpty(regularExpressionAndroid)) {
                    if (isValidPassword(newPassword)) {
                        changeResult.setVisibility(View.GONE);
                        if (!newPassword.equals(retypePassword)) {
                            edt_retypePassword.setText("");
                            edt_retypePassword.requestFocus();
                            Utilities.showDialog(coordinatorLayout, "New Password and Re-Type Password not Matched.");
                        } else {
                            change_password(oldPassword, newPassword);
                        }
                    } else {
                        changeResult.setVisibility(View.VISIBLE);
                        changeResult.setText(Html.fromHtml(result));
                    }
                } else {
                    changeResult.setVisibility(View.GONE);
                    if (!newPassword.equals(retypePassword)) {
                        edt_retypePassword.setText("");
                        edt_retypePassword.requestFocus();
                        Utilities.showDialog(coordinatorLayout, "New Password and Re-Type Password not Matched.");
                    } else {
                        change_password(oldPassword, newPassword);
                    }
                }


            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (!LastPasswordChange.equalsIgnoreCase("0")) {
                    intent = new Intent(ChangePasswordActivity.this, DashBoardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(ChangePasswordActivity.this, LoginActivity_1.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        setUpToolBar();
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
//        final String PASSWORD_PATTERN = "(?=.*[0-9]{2,})(?=.*[a-z])(?=.*[A-Z]).(?=.*?[#?!@$%^&*-]{2,}).{8,}";
        final String PASSWORD_PATTERN = regularExpressionAndroid;

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public void change_password(String oldPassword, String newPassword) {
        try {

            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(ChangePasswordActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/ChangeUserPassword";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", empoyeeId);
            params_final.put("OLD_PASSWORD", oldPassword);
            params_final.put("NEW_PASSWORD", newPassword);

            pm.put("objChangePasswordInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array " + " Length = " + len + " Value = " + response.toString());
                            Log.e("Value", " Length = " + len + " Value = " + response.toString());

                            try {
                                final String result = response.getString("ChangeUserPasswordResult");
                                handler = new Handler();
                                rRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (result != "") {
                                                pDialog.dismiss();

                                                String result1 = result.replaceAll("^\"+|\"+$", " ").trim();

                                                int res = Integer.parseInt(result1);

                                                if (res == 1) {
//                                                    Utilities.showDialog(coordinatorLayout, "Password has been changed successfully.");
                                                    edt_oldPassword.setText("");
                                                    edt_newPassword.setText("");
                                                    edt_retypePassword.setText("");
                                                    openDialog();
                                                } else if (res == 2) {
                                                    Utilities.showDialog(coordinatorLayout, "Invalid old password.");
                                                    edt_oldPassword.setText("");
                                                    edt_oldPassword.requestFocus();
                                                } else if (res == 3) {
                                                    Utilities.showDialog(coordinatorLayout, "Old password and new password can not be same.");
                                                    edt_newPassword.setText("");
                                                    edt_retypePassword.setText("");
                                                    edt_newPassword.requestFocus();
                                                } else {
                                                    Utilities.showDialog(coordinatorLayout, "Error during password change.");
                                                    edt_oldPassword.requestFocus();
                                                }
                                            }

                                        } catch (Exception e) {
                                            e.getMessage();
                                        }
                                    }
                                };

                                handler.postDelayed(rRunnable, 2000);
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
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("securityToken", shared.getString("EMPLOYEE_ID_FINAL",""));
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


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId", "1");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialog() {
        final Dialog dialog = new Dialog(ChangePasswordActivity.this);
        dialog.setContentView(R.layout.pullback_dialogbox);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        CustomTextView txt_header = dialog.findViewById(R.id.dialog_header);
        CustomTextView edt_comment_dialog = dialog.findViewById(R.id.edt_comment_dialog);
        Button btn_ApproveGo, btn_close;
        btn_ApproveGo = dialog.findViewById(R.id.btn_apply);
        btn_close = dialog.findViewById(R.id.btn_close);

        btn_close.setVisibility(View.GONE);

        edt_comment_dialog.setText("Password Change Successfully.");
        txt_header.setText("Change Password");
        btn_ApproveGo.setText("OK");

        btn_ApproveGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.putBoolean(Constants.LOGIN_STATUS, false);
                editor.putBoolean(Constants.DASHBOARD_STATUS, false);
                editor.commit();

                dialog.dismiss();
                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity_1.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }
}
