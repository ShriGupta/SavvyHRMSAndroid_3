package com.savvy.hrmsnewapp.fragment;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.LoginActivity_1;
import com.savvy.hrmsnewapp.adapter.EmployeeDynamicProfileAdapter;
import com.savvy.hrmsnewapp.adapter.ProfileFragmentMainAdapter;
import com.savvy.hrmsnewapp.customwidgets.CircularImageView;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.EmployeeProfilePostDynamicResult;
import com.savvy.hrmsnewapp.retrofitModel.MenuModule;
import com.savvy.hrmsnewapp.retrofitModel.ProfileDataModel;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.RoundedImageView;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class ProfileFragmentMain extends Fragment {

    ProfileFragmentMainAdapter mainAdapter;
    RecyclerView menuItem_List, dynamicProfileRecyclerView;
    CoordinatorLayout coordinatorLayout;
    final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    RoundedImageView circleImageView;
    ImageView clientlogo_MainFragment;
    String employeeId, token;
    CustomTextView tv_empDetail, tv_employeeCode;
    Button showhide_Profile;
    LinearLayout emp_profileLayout;
    ArrayList<HashMap<String, String>> dynamicArraylist;
    EmployeeDynamicProfileAdapter profileAdapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = (shared.getString("Token", ""));
        employeeId = (shared.getString("EmpoyeeId", ""));
        dynamicArraylist = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_main, container, false);
        menuItem_List = view.findViewById(R.id.menuitem_List);
        dynamicProfileRecyclerView = view.findViewById(R.id.dynamicProfileRecyclerView);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        circleImageView = view.findViewById(R.id.profile_main_dashboard);
        clientlogo_MainFragment = view.findViewById(R.id.clientlogo_MainFragment);
        tv_employeeCode = view.findViewById(R.id.tv_employeeCode);
        tv_empDetail = view.findViewById(R.id.tv_empDetail);
        showhide_Profile = view.findViewById(R.id.showhide_Profile);
        emp_profileLayout = view.findViewById(R.id.emp_profileLayout);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        menuItem_List.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        menuItem_List.setNestedScrollingEnabled(false);
        dynamicProfileRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        emp_profileLayout.setVisibility(View.GONE);
        LayoutTransition lt = new LayoutTransition();
        lt.disableTransitionType(LayoutTransition.APPEARING);
        emp_profileLayout.setLayoutTransition(lt);

        try {
            String empPhotoUrl = shared.getString("EmpPhotoPath", "");
            Log.e(TAG, "EmpPhotoPath: " + empPhotoUrl);
            if (empPhotoUrl.equals("")) {
                circleImageView.setImageResource(R.drawable.profile_image);
            } else {
                empPhotoUrl = empPhotoUrl + "?" + System.currentTimeMillis();
                RequestOptions requestOptions = new RequestOptions();
                RequestOptions error = requestOptions.error(R.drawable.profile_image);
                Glide.with(requireActivity()).load(empPhotoUrl).apply(error).into(circleImageView);
            }

            String employeecode = "[" + shared.getString("EmployeeCode", "") + "]";
            tv_empDetail.setText(employeecode);
            tv_employeeCode.setText(shared.getString("EmpoyeeName", ""));

            SharedPreferences sharedpreferencesIP = Objects.requireNonNull(getActivity()).getSharedPreferences("IP_ADDRESS_CONSTANT", MODE_PRIVATE);
            String clienLogoUrl = sharedpreferencesIP.getString("CLIENT_LOGO_URL", "");
            Log.e(TAG, "CLIENT_LOGO_URL: " + clienLogoUrl);
            if (clienLogoUrl.equals("")) {
                clientlogo_MainFragment.setImageResource(R.drawable.companylogo);
            } else {
                Picasso.get().load(clienLogoUrl).error(R.drawable.companylogo).noFade().into(clientlogo_MainFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getDynamicProfileData(employeeId, token);
        showhide_Profile.setOnClickListener(view -> {
            if (menuItem_List.getVisibility() == View.VISIBLE) {
                emp_profileLayout.setVisibility(View.VISIBLE);
                menuItem_List.setVisibility(View.GONE);
                showhide_Profile.setText("Hide Profile");
            } else {
                emp_profileLayout.setVisibility(View.GONE);
                menuItem_List.setVisibility(View.VISIBLE);
                showhide_Profile.setText("Show Profile");
            }

        });
        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                getMenuDetails(employeeId, token);
            } else {
                Toast.makeText(getActivity(), ErrorConstants.NO_NETWORK, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDynamicProfileData(String empoyeeId, String token) {
        try {
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            dynamicArraylist.clear();
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/EmployeeProfilePostDynamic";
            Log.e(TAG, "callDynamicApi: " + url);
            final JSONObject params_final = new JSONObject();

            params_final.put("employeeId", empoyeeId);
            params_final.put("securityToken", token);
            Log.e("TAG", "callPostApi: " + params_final.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params_final,
                    response -> {

                        try {
                            pDialog.dismiss();
                            Log.e("TAG", "onResponse: " + response.toString());
                            JSONArray jarray = new JSONArray(response.getString("EmployeeProfilePostDynamicResult"));
                            HashMap<String, String> hashMap;
                            if (jarray.length() > 0) {
                                for (int i = 0; i < jarray.length(); i++) {
                                    hashMap = new HashMap<>();
                                    hashMap.put("CaptionKey", jarray.getJSONObject(i).getString("CaptionName"));
                                    hashMap.put("CaptionValue", jarray.getJSONObject(i).getString("CaptionValue"));
                                    dynamicArraylist.add(hashMap);
                                }

                                profileAdapter = new EmployeeDynamicProfileAdapter(getActivity(), dynamicArraylist);
                                dynamicProfileRecyclerView.setAdapter(profileAdapter);


                                String employeeId = shared.getString("EmpoyeeId", "");
                                if (employeeId.equals("")) {

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                        public void run() {
                                            Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            startActivity(intent);
                                            Objects.requireNonNull(getActivity()).finish();
                                        }
                                    }, 200);

                                }
                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();
                            try {
                                pDialog.dismiss();
                                Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                Objects.requireNonNull(getActivity()).finish();
                            } catch (Exception ex1) {
                                ex1.printStackTrace();
                            }
                        }

                    }, error -> {
                error.printStackTrace();
                pDialog.dismiss();
                Log.e("Error", "" + error.getMessage());
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

            int socketTimeout = 300000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //All DashBoard Menu Item List Api Call here and set Adapter Dynamically-----------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getMenuDetails(String employeeId, String token) {
        APIServiceClass.getInstance().sendMenuDataRequest(employeeId, token, new ResultHandler<List<MenuModule>>() {
            @Override
            public void onSuccess(List<MenuModule> data) {
                Log.e(TAG, "onSuccess: " + data);
                try {
                    mainAdapter = new ProfileFragmentMainAdapter(getActivity(), data);
                    menuItem_List.setAdapter(mainAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                }
            }

            @Override
            public void onFailure(String message) {
                //  dismissProgressDialog();
                Log.e(TAG, "onFailure: " + message);
            }
        });

    }
}
