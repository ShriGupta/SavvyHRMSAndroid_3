package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.content.Context.MODE_PRIVATE;

public class ChangePasswordFragment extends BaseFragment {

    EditText edt_oldPassword, edt_newPassword, edt_retypePassword;
    Button btn_submit, btn_back;
    String empoyeeId = "";
    Handler handler;
    Runnable rRunnable;

    CustomTextView txt_old, txt_new, txt_retype;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared,shareData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        edt_oldPassword = view.findViewById(R.id.chng_old_password);
        edt_newPassword = view.findViewById(R.id.chng_new_password);
        edt_retypePassword = view.findViewById(R.id.chng_retype_password);

        txt_old = view.findViewById(R.id.title_oldPassword);
        txt_new = view.findViewById(R.id.title_newPassword);
        txt_retype = view.findViewById(R.id.title_retype);

        txt_old.setText(Html.fromHtml("Old Password" + "<font color=\"#E72A02\"><bold>"+ " *"+ "</bold></font>"));
        txt_new.setText(Html.fromHtml("New Password" + "<font color=\"#E72A02\"><bold>"+ " *"+ "</bold></font>"));
        txt_retype.setText(Html.fromHtml("Retype Password" + "<font color=\"#E72A02\"><bold>"+ " *"+ "</bold></font>"));

        btn_submit = view.findViewById(R.id.chng_submit_password);
        btn_back = view.findViewById(R.id.chng_back_password);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = edt_oldPassword.getText().toString();
                String newPassword = edt_newPassword.getText().toString();
                String retypePassword = edt_retypePassword.getText().toString();

                if(oldPassword.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Enter Old Password");
                } else if(newPassword.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Enter New Password");
                } else if(retypePassword.equals("")){
                    Utilities.showDialog(coordinatorLayout,"Please Enter Retpye Password");
                } else if(!newPassword.equals(retypePassword)) {
                    Utilities.showDialog(coordinatorLayout,"Password not match");
                }else
                {
                    change_password(oldPassword,newPassword);
                }

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container_body,profileFragment);
                ft.commit();
            }
        });

        return view;
    }

    public void change_password(String oldPassword, String newPassword){
        try {

            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = Constants.IP_ADDRESS+"/SavvyMobileService.svc/ChangeUserPassword";
            final JSONObject pm = new JSONObject();
            JSONObject params_final = new JSONObject();

            params_final.put("EMPLOYEE_ID", empoyeeId);
            params_final.put("OLD_PASSWORD", oldPassword);
            params_final.put("NEW_PASSWORD", newPassword);

            pm.put("objChangePasswordInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, pm,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            int len = (String.valueOf(response)).length();

                            System.out.print("Array "+ " Length = "+len+" Value = " +response.toString() );
                            Log.e("Value"," Length = "+len+" Value = " +response.toString());

                            try{
                                final String result = response.getString("ChangeUserPasswordResult");
                                handler = new Handler();
                                rRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            if (result != "") {
                                                pDialog.dismiss();

                                                String result1 = result.replaceAll("^\"+|\"+$", " ").trim();

                                                int res = Integer.parseInt(result1);

                                                if (res == 1) {
                                                    Utilities.showDialog(coordinatorLayout, "Password has been changed successfully.");
                                                    edt_oldPassword.setText("");
                                                    edt_newPassword.setText("");
                                                    edt_retypePassword.setText("");
                                                } else if (res == 2) {
                                                    Utilities.showDialog(coordinatorLayout, "Invalid old Password.");
                                                    edt_oldPassword.setText("");
                                                    edt_newPassword.setText("");
                                                    edt_retypePassword.setText("");
                                                    edt_oldPassword.requestFocus();
                                                } else{
                                                    Utilities.showDialog(coordinatorLayout, "Error during password change.");
                                                    edt_oldPassword.setText("");
                                                    edt_newPassword.setText("");
                                                    edt_retypePassword.setText("");
                                                    edt_oldPassword.requestFocus();
                                                }
                                            }

                                        }catch (Exception e){
                                            e.getMessage();
                                        }
                                    }
                                };

                                handler.postDelayed(rRunnable,2000);
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error",""+error.getMessage());
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


        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
