package com.savvy.hrmsnewapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.DashBoardActivity;
import com.savvy.hrmsnewapp.activity.LoginActivity_1;
import com.savvy.hrmsnewapp.customwidgets.CircularImageView;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.parser.JSONParser;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

//import com.cross.pace.iit.model.RootLoginModel;

/**
 * Created by AM00347646 on 29-06-2016.
 */
public class ProfileFragment extends BaseFragment {

    public static int PERMISSION_REQUEST_CODE = 100;
    //public static final String MyPREFERENCES = "MyPrefs";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor_1;
    private LinearLayout rl_addparent;
    SharedPreferences sharedpreferencesIP;
    ProfileAsynTask profileasynctask;
    CustomTextView user_profile_name, txv_mobileValue, txv_emailValue, txv_ageValue, txv_stuCodeValue, txv_batchValue, txv_birthdayValue;
    //    ImageView headcoverimage;

    //todo have to remove this imageview

    //CircularImageView user_profile_photo;
    ImageView profileCircleImage;
    DashBoardActivity boardActivity;
    String token = "", empoyeeId = "", empoyeeName = "";

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferencesIP = getActivity().getSharedPreferences("IP_ADDRESS_CONSTANT", MODE_PRIVATE);
        Constants.IP_ADDRESS = sharedpreferencesIP.getString("IP_ADDRESS", "");

        boardActivity = new DashBoardActivity();
        //sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //editor = sharedpreferences.edit();

        // editor.putBoolean(Constants.DASHBOARD_STATUS, true);
        // editor.commit();

//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
//            requestForSpecificPermission();
//        }

    }

    private void getProfileData(String empoyeeId, String token) {
        try {

            if (Utilities.isNetworkAvailable(getActivity())) {

                profileasynctask = new ProfileAsynTask();
                profileasynctask.empid = empoyeeId;

                profileasynctask.token = token;
                profileasynctask.execute();

			/*new LoginAsynTask(LoginActivity.this, LoginActivity.this,
                    strEmail, strPassword, OperationPerformName.OPERATION_LOGIN)
					.execute();*/

            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
////                        No_Internet no_internet = new No_Internet();
////                        FragmentManager fragmentManager = getFragmentManager();
////                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////                        fragmentTransaction.replace(R.id.container_body,no_internet);
////                        fragmentTransaction.commit();
////                        boardActivity.finish();
//                    }
//                }, 1000*1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void requestForSpecificPermission() {
//        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();

        user_profile_name = rootView.findViewById(R.id.user_profile_name);
        //user_profile_photo = rootView.findViewById(R.id.user_profile_photo); // todo uncomment this code
        profileCircleImage = (ImageView) rootView.findViewById(R.id.profileCircleImage); // todo uncomment this code
        txv_mobileValue = rootView.findViewById(R.id.txv_mobileValue);
        //txv_emailValue           =   (CustomTextView)rootView.findViewById(R.id.txv_emailValue);
        txv_stuCodeValue = rootView.findViewById(R.id.txv_stuCodeValue);
        txv_batchValue = rootView.findViewById(R.id.txv_batchValue);
        txv_birthdayValue = rootView.findViewById(R.id.txv_birthdayValue);
        //CustomTextView txv_courseValue          =   (CustomTextView)rootView.findViewById(R.id.txv_courseValue);
        txv_ageValue = rootView.findViewById(R.id.txv_ageValue);
        //CustomTextView txv_bloodgroupValue      =   (CustomTextView)rootView.findViewById(R.id.txv_bloodgroupValue);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        token = (shared.getString("Token", ""));
        empoyeeId = (shared.getString(Constants.EMPLOYEE_ID_FINAL, ""));

        getProfileData(empoyeeId, token);

    }


    private class ProfileAsynTask extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String empid;
        // private double lati;
        String token;
        // String stremail,strpassword;

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            try {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.setMax(100);
                pDialog.show();


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String PROFILE_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/EmployeeProfile/" + empid + "/" + token;
                System.out.println("ATTENDANCE_URL====" + PROFILE_URL);
                JSONParser jParser = new JSONParser(getActivity()); // get JSON data from URL JSONArray json = jParser.getJSONFromUrl(url);
                String json = jParser.makeHttpRequest(
                        PROFILE_URL, "GET");

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (pDialog != null && pDialog.isShowing()) {

                try {
                    pDialog.dismiss();
                    System.out.println("RESULT HARIOM====" + result);
                    JSONObject jsonobj = new JSONObject(result);
                    String branch = jsonobj.getString("branch");
                    String department = jsonobj.getString("department");
                    String designation = jsonobj.getString("designation");
                    String emailId = jsonobj.getString("emailId");
                    String employeeCode = jsonobj.getString("employeeCode");
                    String employeeName = jsonobj.getString("employeeName");

                    String empoyeeId = jsonobj.getString("employeeId");
                    String errorMessage = jsonobj.getString("errorMessage");
                    String grade = jsonobj.getString("grade");

                    String mobileNo = jsonobj.getString("mobileNo");
                    String photoCode = jsonobj.getString("photoCode");
                    String supervisor = jsonobj.getString("supervisor");

                    if (empoyeeId.equals("") || empoyeeId == null) {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }, 200);

                    } else {
                        try {
                            editor_1 = getActivity().getSharedPreferences("PROFILE_SESSION", MODE_PRIVATE).edit();
                            editor_1.putString("designation", designation);
                            editor_1.putString("EmpoyeeId", empoyeeId);
                            editor_1.putString("EmployeeName", employeeName);
                            editor_1.putString("supervisor", supervisor);
                            editor_1.putString("EmpPhotoPath", photoCode);
                            editor_1.putString("EmployeeCode", employeeCode);
                            editor_1.putString("department", department);

                            editor_1.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        user_profile_name.setText(employeeName);
                        txv_mobileValue.setText(employeeCode);
                        txv_ageValue.setText(department);
                        //txv_emailValue.setText(designation);
                        txv_stuCodeValue.setText(supervisor);
                        txv_batchValue.setText(mobileNo);
                        txv_birthdayValue.setText(emailId);

                        if (photoCode != "" && photoCode.length() > 0) {
                            String photourl = photoCode.replace("\\", "");
                            Picasso.get().load(photourl).error(R.drawable.profile_image).into(profileCircleImage);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        getActivity().finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }


        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}

