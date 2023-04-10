package com.savvy.hrmsnewapp.markPunch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.BaseActivity;
import com.savvy.hrmsnewapp.adapter.OnlineTestAdapter;
import com.savvy.hrmsnewapp.interfaces.OnOptionSelected;
import com.savvy.hrmsnewapp.model.AnswersModel;
import com.savvy.hrmsnewapp.model.QuestionAnswerModel;
import com.savvy.hrmsnewapp.model.SelectedAnswersModel;
import com.savvy.hrmsnewapp.service.TrackGPS;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnlineTestActivity extends BaseActivity implements OnOptionSelected, LocationListener {
    private RecyclerView recyclerViewOnlineTest;
    private Button mBtnNext;
    private OnlineTestAdapter mOnlineTestAdapter;
    SharedPreferences shared, shareData;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private String employeeId;
    ArrayList<HashMap<String, String>> arlData, arlData1, arlDataConveyanceId;
    private int answerCount = 0;
    private ArrayList<SelectedAnswersModel> selectedAnswersModelList;
    private List<QuestionAnswerModel> mQuestionAnswerModelList;
    private String xmlData = "";
    private LocationManager locationManager = null;
    Location loc = null;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private TrackGPS mTrackGPS;
    private Boolean flag = false;
    double longitude = 0, longi;
    double latitude = 0, latit;
    private RelativeLayout parentRelativeLayout;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_test);
        recyclerViewOnlineTest = (RecyclerView) findViewById(R.id.recycler_online_test);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        parentRelativeLayout = (RelativeLayout) findViewById(R.id.parentRelativeLayout);
        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        arlData = new ArrayList<HashMap<String, String>>();
        arlData1 = new ArrayList<HashMap<String, String>>();
        mTrackGPS = new TrackGPS(OnlineTestActivity.this);
        employeeId = (shared.getString("EmpoyeeId", ""));
        mQuestionAnswerModelList = new ArrayList<>();
        selectedAnswersModelList = new ArrayList<>();
        getQuestionAnswerList(employeeId);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        recyclerViewOnlineTest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAnswersModelList = Utilities.removeDuplicates(selectedAnswersModelList);
                if (selectedAnswersModelList.size() < mQuestionAnswerModelList.size()) {
                    Snackbar snackbar = Snackbar
                            .make(parentRelativeLayout, "Please answer all the questions", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    xmlData = "";
                    for (SelectedAnswersModel selectedAnswersModel : selectedAnswersModelList) {
                        xmlData = xmlData + selectedAnswersModel.getQuesId() + ", " + selectedAnswersModel.getAnsId() + ":";
                    }
                    xmlData = xmlData.substring(0, xmlData.length() - 1);
                    try {
                        if (Utilities.isNetworkAvailable(OnlineTestActivity.this)) {


                            if (mTrackGPS.canGetLocation()) {
                                flag = displayGpsStatus();
                                if (flag) {
                                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                                    if (locationManager != null) {
                                        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                            if (ActivityCompat.checkSelfPermission(OnlineTestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OnlineTestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, OnlineTestActivity.this);
                                            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                                            if (loc != null) {
                                                latitude = loc.getLatitude();
                                                longitude = loc.getLongitude();
                                            }

                                        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, OnlineTestActivity.this);
                                            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                            if (loc != null) {
                                                latitude = loc.getLatitude();
                                                longitude = loc.getLongitude();
                                            }
                                        }
                                        if (latitude == 0.0 && longitude == 0.0) {
                                            Snackbar snackbar = Snackbar
                                                    .make(parentRelativeLayout, "Please wait while we're fetching your current location.", Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                        } else {
                                            String addressLocation = Utilities.getAddressFromLocation(OnlineTestActivity.this, loc);
                                            sendQuestionData(employeeId, mQuestionAnswerModelList.get(0).getEMTTEMPLATEID(), xmlData, addressLocation, String.valueOf(latitude), String.valueOf(longitude));
                                        }
                                    }
                                } else {
                                    mTrackGPS.showSettingsAlert();
                                }
                            } else {
                                mTrackGPS.showSettingsAlert();
                            }
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(parentRelativeLayout, ""+ErrorConstants.NO_NETWORK, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*TrackGPS trackGPS = new TrackGPS(OnlineTestActivity.this);
                    if (trackGPS.canGetLocation()) {

                        @SuppressLint("MissingPermission") Location mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        String addressLocation = Utilities.getAddressFromLocation(OnlineTestActivity.this, mCurrentLocation).replace(" ", "_");
                        if (mCurrentLocation != null) {
                            sendQuestionData(employeeId, mQuestionAnswerModelList.get(0).getEMTTEMPLATEID(), xmlData, addressLocation, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()));
                        } else {
                            Toast.makeText(OnlineTestActivity.this, "Please wait while we're fetching location.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        trackGPS.showSettingsAlert();
                    }*/


                }
            }
        });
    }

    private void getQuestionAnswerList(String empId) {
        try {
            arlData = new ArrayList<HashMap<String, String>>();
            final ProgressDialog pDialog;
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/GetQuestionAndAnswerGet/" + empId;

//            pm.put("objConveyanceVoucherRequestStatusInfo", params_final);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    pDialog.dismiss();
                    List<QuestionAnswerModel> questionAnswerModelList = new ArrayList<>();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            QuestionAnswerModel questionAnswerModel;
                            List<AnswersModel> answersModelList = new ArrayList<>();

                            JSONObject jsonObject = response.getJSONObject(i);
                            String EMT_TEMPLATE_ID = jsonObject.getString("EMT_TEMPLATE_ID");
                            JSONArray OPTION_LIST = jsonObject.getJSONArray("OPTION_LIST");
                            String OTQ_TEST_QUESTION_ID = jsonObject.getString("OTQ_TEST_QUESTION_ID");
                            String OTQ_TEST_QUESTION_NAME = jsonObject.getString("OTQ_TEST_QUESTION_NAME");
                            String OTQA_RIGHT_ANSWER = jsonObject.getString("OTQA_RIGHT_ANSWER");
                            String OTQA_RIGHT_ANSWER_ID = jsonObject.getString("OTQA_RIGHT_ANSWER_ID");
                            String OTQ_TEST_QUESTION_TYPE = jsonObject.getString("OTQ_TEST_QUESTION_TYPE");
                            System.out.println("OTQA_TEST_ANSWER ANswers" + OPTION_LIST.length());

                            for (int j = 0; j < OPTION_LIST.length(); j++) {
                                JSONObject answerObject = OPTION_LIST.getJSONObject(j);
                                AnswersModel answersModel = new AnswersModel();
                                String OTQA_TEST_ANSWER = answerObject.getString("OTQA_TEST_ANSWER");
                                String OTQA_TEST_QUESTION_ANSWER_ID = answerObject.getString("OTQA_TEST_QUESTION_ANSWER_ID");
                                answersModel.setOTQATESTANSWER(OTQA_TEST_ANSWER);
                                answersModel.setOTQATESTQUESTIONANSWERID(OTQA_TEST_QUESTION_ANSWER_ID);
                                answersModelList.add(answersModel);

                            }
                            questionAnswerModel = new QuestionAnswerModel(EMT_TEMPLATE_ID, OTQ_TEST_QUESTION_ID, OTQ_TEST_QUESTION_NAME, OTQ_TEST_QUESTION_TYPE, OTQA_RIGHT_ANSWER, OTQA_RIGHT_ANSWER_ID, answersModelList);
                            questionAnswerModelList.add(questionAnswerModel);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mQuestionAnswerModelList = questionAnswerModelList;
                    mOnlineTestAdapter = new OnlineTestAdapter(getApplicationContext(), questionAnswerModelList);
                    recyclerViewOnlineTest.setAdapter(mOnlineTestAdapter);
                    mOnlineTestAdapter.setOnOptionSelected(OnlineTestActivity.this);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    System.out.println("API LIST ERRor: " + error.getLocalizedMessage());
                }
            });

            int socketTimeout = 3000000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onOptionSelected(String quesId, String quesName, String ansId, String ansName, String correctAnsId, String correcrAnsName) {
        Log.e("Submitted answers :", "" + quesId + "," + quesName + "," + ansId + "," + ansName + "," + correctAnsId + "," + correcrAnsName);
        SelectedAnswersModel selectedAnswersModel = new SelectedAnswersModel(quesId, quesName, ansId, ansName, correctAnsId, correcrAnsName);

        if (!selectedAnswersModelList.isEmpty()) {
            if (Utilities.contains(selectedAnswersModelList, quesId)) {
                if (Utilities.getIndexOfListItem(selectedAnswersModelList, quesId) != 500) {
                    selectedAnswersModelList.remove(Utilities.getIndexOfListItem(selectedAnswersModelList, quesId));
                    selectedAnswersModelList.add(selectedAnswersModel);
                }
            } else {
                selectedAnswersModelList.add(selectedAnswersModel);
            }
        } else {
            selectedAnswersModelList.add(selectedAnswersModel);
        }

        Log.e("size of selected answers :", "" + selectedAnswersModelList.size());
        mOnlineTestAdapter.notifyDataSetChanged();

    }


    private void sendQuestionData(String emp_id, String tempId, String xmlData, String locationName, String lattitude, String longitude) {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                final ProgressDialog pDialog;
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                JSONObject param = new JSONObject();

                param.put("EMPLOYEE_ID", emp_id);
                param.put("TEMPLATEID", tempId);
                param.put("XMLDATA", xmlData);
                param.put("LOCATIONADDRESS", locationName);
                param.put("LATITUDE", lattitude);
                param.put("LONGITUDE", longitude);
                Log.e("RequestData", param.toString());

                String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTestDataByEmployee";
                Log.e("Url", "OD Approval Url = " + url);

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    pDialog.dismiss();
                                    JSONObject jsonobj = response.getJSONObject("SaveTestDataByEmployeeResult");
                                    String totalScore = jsonobj.getString("TOTALSCORE");
                                    String resultStatus = jsonobj.getString("RESULT_STATUS");
                                    String totalMarks = jsonobj.getString("TOTALMARK");
                                    String totalAttempt = jsonobj.getString("ATTEMPT");
                                    String totalQuestions = jsonobj.getString("TOTAL_QUESTIONS");

                                    Log.e("STATUS OF SAVE TEST API", " total score " + totalScore);
                                    Log.e("STATUS OF SAVE TEST API", " total resultStatus " + resultStatus);
                                    Log.e("STATUS OF SAVE TEST API", " total totalMarks " + totalMarks);
                                    Log.e("STATUS OF SAVE TEST API", " total totalAttempt " + totalAttempt);
                                    Log.e("STATUS OF SAVE TEST API", " total totalQuestions " + totalQuestions);

                                    Bundle bundle = new Bundle();
                                    bundle.putParcelableArrayList("SelectedAnswerList", selectedAnswersModelList);
                                    Intent intent = new Intent(OnlineTestActivity.this,
                                            OnlineTestResultActivity.class);
                                    intent.putExtra("bundle", bundle);
                                    intent.putExtra("totalScore", totalScore);
                                    intent.putExtra("resultStatus", resultStatus);
                                    intent.putExtra("totalMarks", totalMarks);
                                    intent.putExtra("totalAttempt", totalAttempt);
                                    intent.putExtra("totalQuestions", totalQuestions);
                                    startActivity(intent);
                                    finish();
                                /*int res = response.getInt("ProcessOnDutyRequestPostResult");
                                if (res > 0) {
                                    Utilities.showDialog(coordinatorLayout, "On Duty Request processed sucessfully.");
                                    getODApproval(empoyeeId);
                                } else if (res == 0) {
                                    Utilities.showDialog(coordinatorLayout, "Error during processing of On Duty Request.");
                                } else if (res == -1) {
                                    Utilities.showDialog(coordinatorLayout, "Request Flow Plan is not available.");
                                } else if (res == -2) {
                                    Utilities.showDialog(coordinatorLayout, "Can not take any action on the previous payroll requests.");
                                }*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.e("ERROR SaveTestDataByEmployee", "" + error.getLocalizedMessage());
                    }
                });

                int socketTime = 30000;
                RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(retryPolicy);
                requestQueue.add(jsonObjectRequest);
            } else {
                Snackbar snackbar = Snackbar
                        .make(parentRelativeLayout, "NO internet connection.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
        return gpsStatus;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
