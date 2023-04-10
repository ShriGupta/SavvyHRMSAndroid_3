package com.savvy.hrmsnewapp.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.fragment.AnnouncementFragment;
import com.savvy.hrmsnewapp.fragment.AttendanceCalanderFragment;
import com.savvy.hrmsnewapp.fragment.AttendanceCalendarMMTFragment;
import com.savvy.hrmsnewapp.fragment.CalendarWeb;
import com.savvy.hrmsnewapp.fragment.CompOffAccrualRequestMMTFragment;
import com.savvy.hrmsnewapp.fragment.CompOffAcuralApprovalMMTFragment;
import com.savvy.hrmsnewapp.fragment.CompOffApproval;
import com.savvy.hrmsnewapp.fragment.CompOffHolderFragment;
import com.savvy.hrmsnewapp.fragment.ConveyanceApprovalHolder;
import com.savvy.hrmsnewapp.fragment.ConveyancefragmentHolder;
import com.savvy.hrmsnewapp.fragment.CustomerVisitInOutHolder;
import com.savvy.hrmsnewapp.fragment.CustomerVisit_GOIP;
import com.savvy.hrmsnewapp.fragment.Customer_view_holder;
import com.savvy.hrmsnewapp.fragment.Customer_view_holder_1;
import com.savvy.hrmsnewapp.fragment.Customer_visit_new;
import com.savvy.hrmsnewapp.fragment.DashBoardFragmentMain;
import com.savvy.hrmsnewapp.fragment.ExpenseApprovalHolder;
import com.savvy.hrmsnewapp.fragment.ExpenseFramentHolder;
import com.savvy.hrmsnewapp.fragment.FaceDetectionHolderFrgamnet;
import com.savvy.hrmsnewapp.fragment.FaceRegistrationFragment;
import com.savvy.hrmsnewapp.fragment.FragmentDrawer;
import com.savvy.hrmsnewapp.fragment.INOUTHolderFragment;
import com.savvy.hrmsnewapp.fragment.LeaveApprovalFragment;
import com.savvy.hrmsnewapp.fragment.LeaveApprovalRequestMMTFragment;
import com.savvy.hrmsnewapp.fragment.LeaveEncashmentApproval;
import com.savvy.hrmsnewapp.fragment.LeaveEncashmentHolder;
import com.savvy.hrmsnewapp.fragment.LeaveRequest;
import com.savvy.hrmsnewapp.fragment.LeaveRequestMMTFragment;
import com.savvy.hrmsnewapp.fragment.ManagerDashBoardMMTFragment;
import com.savvy.hrmsnewapp.fragment.MarkTeamAttendance;
import com.savvy.hrmsnewapp.fragment.MyHoliday;
import com.savvy.hrmsnewapp.fragment.MyMessageFragment;
import com.savvy.hrmsnewapp.fragment.MyTeamMembers;
import com.savvy.hrmsnewapp.fragment.ODApprovalFragment;
import com.savvy.hrmsnewapp.fragment.ODHolderContractualFicciFragment;
import com.savvy.hrmsnewapp.fragment.ODHolderFicciFragment;
import com.savvy.hrmsnewapp.fragment.ODHolderFragment;
import com.savvy.hrmsnewapp.fragment.OPEHolderFicciFragment;
import com.savvy.hrmsnewapp.fragment.OPE_ApprovalFicciFragment;
import com.savvy.hrmsnewapp.fragment.OTRequestFragment;
import com.savvy.hrmsnewapp.fragment.OnDutyApprovalRequestMMTFragment;
import com.savvy.hrmsnewapp.fragment.OnDutyRequestMMTFragment;
import com.savvy.hrmsnewapp.fragment.ProfileFragmentMain;
import com.savvy.hrmsnewapp.fragment.PunchApprovalFragment;
import com.savvy.hrmsnewapp.fragment.PunchInOutNew;
import com.savvy.hrmsnewapp.fragment.PunchRequestHolder;
import com.savvy.hrmsnewapp.fragment.ReimbursementApprovalFragment;
import com.savvy.hrmsnewapp.fragment.ReimbursementFragmentHolder;
import com.savvy.hrmsnewapp.fragment.RequisitionFragmentHolder;
import com.savvy.hrmsnewapp.fragment.ResignationFragmentHolder;
import com.savvy.hrmsnewapp.fragment.ShiftChangeApprovalMMTFragment;
import com.savvy.hrmsnewapp.fragment.ShiftChangeRequestMMTFragment;
import com.savvy.hrmsnewapp.fragment.ShortLeaveApprovalFragment;
import com.savvy.hrmsnewapp.fragment.ShortLeaveRequestHolder;
import com.savvy.hrmsnewapp.fragment.TeamAttendanceCalendar;
import com.savvy.hrmsnewapp.fragment.TeamFaceAttendanceFragment;
import com.savvy.hrmsnewapp.fragment.TrackMeFragment;
import com.savvy.hrmsnewapp.fragment.TravelApprovalHolderFragment;
import com.savvy.hrmsnewapp.fragment.TravelDeskFicciHolder;
import com.savvy.hrmsnewapp.fragment.TravelExpenseApprovalFicciFragment;
import com.savvy.hrmsnewapp.fragment.TravelExpenseFicciHolder;
import com.savvy.hrmsnewapp.fragment.TravelFragmentHolder;
import com.savvy.hrmsnewapp.fragment.TravelRequestApprovalFicciFragment;
import com.savvy.hrmsnewapp.fragment.TravelRequestFiccciFragmentHolder;
import com.savvy.hrmsnewapp.fragment.WorkFromHomeApprovalMMTFragment;
import com.savvy.hrmsnewapp.fragment.WorkFromHomeMMTRequestFrgament;
import com.savvy.hrmsnewapp.interfaces.FragmentDrawerListener;
import com.savvy.hrmsnewapp.markPunch.MarkPunchFragment;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.OfflinePunchInModel;
import com.savvy.hrmsnewapp.saleForce.AllCustomerDetail;
import com.savvy.hrmsnewapp.saleForce.SalesForceMapActivity;
import com.savvy.hrmsnewapp.service.LocationService;
import com.savvy.hrmsnewapp.utils.Config;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.LogMaintainance;
import com.savvy.hrmsnewapp.utils.MyNotificationManager;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.savvy.hrmsnewapp.utils.Constants.CHANNEL_ID;

public class DashBoardActivity extends BaseActivity implements FragmentDrawerListener {

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    ViewGroup mainLayout;
    TextView root_view;
    private int xDelta;
    private int yDelta;
    BroadcastReceiver act2InitReceiver;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    StringBuilder offlineXMLString = new StringBuilder();
    SharedPreferences sharedPreferences;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("UserName", "");

        checkOfflineData();
        if (Constants.checkTrackingStatus) {
            showTrackMyLocationView(Constants.checkTrackingStatus);
        }

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        FragmentDrawer drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        assert drawerFragment != null;
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, Constants.CHANNEL_NAME, importance);
            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        act2InitReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                boolean value = intent.getBooleanExtra("track_locaion", false);
                showTrackMyLocationView(value);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(act2InitReceiver, new IntentFilter("track_my_location"));
//        MyNotificationManager.getInstance(this).displayNotification("Greetings", "Hello how are you?");
        try {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // checking for type intent filter
                    if (Objects.equals(intent.getAction(), Config.REGISTRATION_COMPLETE)) {
                        // gcm successfully registered
                        // now subscribe to `global` topic to receive app wide notifications
                        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

//                        displayFirebaseRegId();

                    } else if (Objects.equals(intent.getAction(), Config.PUSH_NOTIFICATION)) {
                        // new push notification is received

                        String message = intent.getStringExtra("message");

//                        Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                        Intent intent1 = new Intent();
                        //Intent intent1 = new Intent(this,NotificationAnubhav.class);
                        PendingIntent pIntent = PendingIntent.getActivity(DashBoardActivity.this, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                        Notification noti = null;
                        noti = new Notification.Builder(DashBoardActivity.this)
                                .setTicker("Hello")
                                .setContentTitle("Firebase Message")
                                .setSmallIcon(R.drawable.profile_img)
                                .setContentText(message)
                                .setContentIntent(pIntent)
                                .getNotification();

                        noti.flags = Notification.FLAG_AUTO_CANCEL;
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        assert notificationManager != null;
                        notificationManager.notify(0, noti);

                        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardActivity.this);
                        builder.setTitle("Update");
                        builder.setMessage("" + message + "\n" + "if you had already updated this app,\nthen please ignore this message.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        });

                        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

                        builder.show();
//                        txtMessage.setText(message);
                    }
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                String positionId = extras.getString("PostionId");
                assert positionId != null;
                switch (positionId) {
                    case "4":
                        displayView("4", "Mark Attendance");
                        break;
                    case "34":
                        displayView("34", "Punch In Out");
                        break;
                    case "8":
                        displayView("8", "Leave Request");
                        break;
                    case "32":
                        displayView("32", "Track Me");
                        break;
                    case "1":
                        displayView("1", "Profile");
                        break;
                    case "5":
                        displayView("5", "Mark Team Attendance");
                        break;
                    case "39":
                        displayView("39", "Mark Punch In Out");
                        break;
                    case "61":
                        displayView("61", "Leave Request Ficci");
                        break;
                    case "68":
                        displayView("68", "Travel Desk");
                        break;
                    case "64":
                        displayView("64", "Travel Request");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            displayView("1", "Profile");
        }
    }

    private void checkOfflineData() {
        android.os.AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<OfflinePunchInModel> offlinePunchDetail = DatabaseClient.getInstance(DashBoardActivity.this).getAppDatabase().passengerDao().getAllOfflinePunch();
                Log.e("TAG", "run: " + offlinePunchDetail.size());
                if (offlinePunchDetail.size() > 0) {
                    for (int i = 0; i < offlinePunchDetail.size(); i++) {
                        offlineXMLString.append("@").append(offlinePunchDetail.get(i).getUserName())
                                .append("|").append(offlinePunchDetail.get(i).getCurrentdate())
                                .append("|").append(offlinePunchDetail.get(i).getCurrenttime())
                                .append("|").append(offlinePunchDetail.get(i).getLatitude())
                                .append("|").append(offlinePunchDetail.get(i).getLongitude())
                                .append("|").append(offlinePunchDetail.get(i).getLocation())
                                .append("|").append(offlinePunchDetail.get(i).getComment());
                    }
                    uploadOfflinePunch(offlineXMLString.toString());
                }
            }
        });
    }

    private void uploadOfflinePunch(String offlinexmlDetail) {
        if (isNetworkAvailable()) {
            String url = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveOffLineAttendance";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userName", username);
                jsonObject.put("deatails", offlinexmlDetail);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String value = response.getString("SaveOffLineAttendanceResult");
                            Log.e("", "onResponse: " + value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
                    Log.e("TAG", "uploadOfflinePunch: ");
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("securityToken", sharedPreferences.getString("EMPLOYEE_ID_FINAL", ""));
                        return params;
                    }


                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                RetryPolicy policy = new DefaultRetryPolicy(3000000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                VolleySingleton.getInstance(DashBoardActivity.this).addToRequestQueue(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void showTrackMyLocationView(Boolean trakingLocation) {
        root_view = findViewById(R.id.root_view);
        mainLayout = (RelativeLayout) findViewById(R.id.main_laout);
        if (trakingLocation) {
            mainLayout.setVisibility(View.VISIBLE);
            root_view.setOnTouchListener((view, event) -> {
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                return true;
            });
        } else {
            mainLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDrawerItemSelected(View view, String position, String privillageName) {
        displayView(position, privillageName);
    }

    @SuppressLint("RestrictedApi")
    private void displayView(String position, String privillageName) {
        LogMaintainance.WriteLog("DashBoard Activity DisplayView Method");
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        if (!Utilities.isNetworkAvailable(DashBoardActivity.this)) {
            showNetworkErrorDialog();
            return;
        }
        Log.e("TAG", "Position " + position + " privillageName" + privillageName);

        try {
            switch (position) {
                case "1":
                    fragment = new ProfileFragmentMain();
                    title = privillageName;
                    break;
                case "2":
                    fragment = new AttendanceCalanderFragment();
                    title = privillageName;
                    break;
                case "3":
                    fragment = new TeamAttendanceCalendar();
                    title = privillageName;
                    break;
                case "4":
                    fragment = new Customer_view_holder_1();
                    title = privillageName;
                    break;
                case "5":
                    fragment = new MarkTeamAttendance();
                    title = privillageName;
                    break;
                case "6":
//                    Intent intent = new Intent(DashBoardActivity.this, TestingActivity.class);
//                    startActivity(intent);
                    fragment = new MyHoliday();
                    title = privillageName;
                    break;
                case "7":
                    // salery slip----------------------------------------------------------------------------------------------------
                    fragment = new AllCustomerDetail();
//                    fragment = new MailFragment();
                    title = privillageName;
                    break;
                case "8":
                    fragment = new LeaveRequest();
                    title = privillageName;
                    break;
                case "9":
                    fragment = new ODHolderFragment();
                    title = privillageName;
                    break;
                case "10":
                    fragment = new PunchRequestHolder();
                    title = privillageName;
                    break;
                case "11":
                    fragment = new CompOffHolderFragment();
                    title = privillageName;
                    break;
                case "12":
                    fragment = new LeaveEncashmentHolder();
                    title = privillageName;
                    break;
                case "13":
                    //fragment = new TravelRequestFiccciFragmentHolder();
                    fragment = new OTRequestFragment();
                    title = privillageName;
                    break;
                case "14":
                    fragment = new LeaveApprovalFragment();
                    title = privillageName;
                    break;
                case "15":
                    fragment = new ODApprovalFragment();
                    title = privillageName;
                    break;
                case "16":
                    fragment = new PunchApprovalFragment();
                    title = privillageName;
                    break;
                case "17":
                    fragment = new CompOffApproval();
                    title = privillageName;
                    break;
                case "18":
                    fragment = new LeaveEncashmentApproval();
                    title = privillageName;
                    break;
                case "20":
                    fragment = new DashBoardFragmentMain();
                   // fragment = new PunchReportFragment();
                    // fragment = new FaceDetectionHolderFrgamnet();

                    //Todo  Face detection implemments.....
                    //fragment = new DashBoardMainFragment();
                    // fragment = new LoanRequestFragmentHolder();
                    title = privillageName;
                    break;
                case "21":
                    fragment = new CalendarWeb();
                    title = privillageName;
                    break;
                case "22":
                    fragment = new ConveyancefragmentHolder();
                    title = privillageName;
                    if (!Constants.CONVEYANCE_REQUEST_ID_STATUS) {
                        Constants.CONVEYANCE_REQUEST_ID_STATUS = true;
                    }
                    break;
                case "23":
                    fragment = new ExpenseFramentHolder();
                    title = privillageName;
                    break;
                case "24":
                    fragment = new TravelFragmentHolder();
                    title = privillageName;
                    break;
                case "25":
                    //  fragment = new SaleForce_Visit();
                    fragment = new ReimbursementFragmentHolder();
                    title = privillageName;
                    break;
                case "26":
                    fragment = new ResignationFragmentHolder();
                    title = privillageName;
                    break;
                case "27":
                    fragment = new ExpenseApprovalHolder();
                    title = privillageName;
                    break;
                case "28":
                    fragment = new TravelApprovalHolderFragment();
                    title = privillageName;
                    break;
                case "29":
                    fragment = new ReimbursementApprovalFragment();
                    title = privillageName;
                    break;
                case "30":
//                    fragment = new GetCurrentLocation();
                    Intent intent = new Intent(DashBoardActivity.this, SalesForceMapActivity.class);
                    startActivity(intent);
//                    fragment = new MailFragment();
//                    title = privillageName;
                    break;
                case "31":
                    fragment = new ConveyanceApprovalHolder();
                    title = privillageName;
                    break;
                case "32":
                     fragment = new TrackMeFragment();
                    title = privillageName;
                    break;
                case "33":
                    fragment = new MyTeamMembers();
                    Constants.MY_TEAM_MEMBERS = 0;
                    Constants.My_Team_Members = new ArrayList<>();
                    title = privillageName;
                    break;
                case "34":
                    fragment = new Customer_view_holder();
                    title = privillageName;
                    break;
                case "35":
                    fragment = new AnnouncementFragment();
                    title = privillageName;
                    break;
                case "36":
                    fragment = new MyMessageFragment();
                    title = privillageName;
                    break;
                case "37":
                    fragment = new Customer_visit_new();
                    title = privillageName;
                    editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("Title", title);
                    editor.apply();
                    break;
                case "38":
                    fragment = new PunchInOutNew();
                    title = privillageName;
                    editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("Title", title);
                    editor.commit();
                    break;

                case "39":
                    fragment = new MarkPunchFragment();
                    title = privillageName;
                    break;
                case "41":
                    fragment = new ShortLeaveRequestHolder();
                    title = privillageName;
                    break;
                case "42":
                    fragment = new ShortLeaveApprovalFragment();
                    title = privillageName;
                    break;
                case "44":
                    fragment = new CustomerVisit_GOIP();
                    title = privillageName;
                    editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("Title", title);
                    editor.commit();
                    break;
                case "45":
                    fragment = new ODHolderFicciFragment();
                    title = privillageName;
                    break;
                case "46":
                    //fragment = new TravelRequestFiccciFragmentHolder();
                    // fragment = new ODApprovalFicci_Fragment();
                    title = privillageName;
                    break;
                case "47":
                    fragment = new OPEHolderFicciFragment();
                    title = privillageName;
                    break;

                case "48":
                    fragment = new OPE_ApprovalFicciFragment();
                    title = privillageName;
                    break;
                case "49":
                    fragment = new WorkFromHomeMMTRequestFrgament();
                    title = privillageName;
                    break;
                case "50":
                    fragment = new ShiftChangeRequestMMTFragment();
                    title = privillageName;
                    break;
                case "51":
                    fragment = new CompOffAccrualRequestMMTFragment();
                    title = privillageName;
                    break;
                case "52":
                    fragment = new OnDutyRequestMMTFragment();
                    title = privillageName;
                    break;
                case "53":
                    fragment = new LeaveRequestMMTFragment();
                    title = privillageName;
                    break;
                case "54":
                    fragment = new AttendanceCalendarMMTFragment();
                    title = privillageName;
                    break;
                case "55":
                    fragment = new ShiftChangeApprovalMMTFragment();
                    title = privillageName;
                    break;
                case "56":
                    fragment = new CompOffAcuralApprovalMMTFragment();
                    title = privillageName;
                    break;
                case "57":
                    fragment = new OnDutyApprovalRequestMMTFragment();
                    title = privillageName;
                    break;
                case "58":
                    fragment = new LeaveApprovalRequestMMTFragment();
                    title = privillageName;
                    break;
                case "59":
                    fragment = new WorkFromHomeApprovalMMTFragment();
                    title = privillageName;
                    break;
                case "60":
                    fragment = new ManagerDashBoardMMTFragment();
                    // fragment = new LTFRequestFragmentHolder();
                    title = privillageName;
                    break;
                case "61":
                    fragment = new LeaveRequestFicciHolder();
                    title = privillageName;
                    break;
                case "64":
                    fragment = new TravelRequestFiccciFragmentHolder();
                    title = privillageName;
                    break;
                case "65":
                    fragment = new TravelExpenseFicciHolder();
                    title = privillageName;
                    break;
                case "66":
                    fragment = new TravelRequestApprovalFicciFragment();
                    title = privillageName;
                    break;
                case "67":
                    fragment = new TravelExpenseApprovalFicciFragment();
                    title = privillageName;

                    break;
                case "68":
                    fragment = new TravelDeskFicciHolder();
                    title = privillageName;
                    break;
                case "69":
                    fragment = new CustomerVisitInOutHolder();
                    title = privillageName;
                    break;
                case "70":
                    fragment = new ODHolderContractualFicciFragment();
                    title = privillageName;
                    break;
                //--------------------------------------------------------------------------------------------------------------
                case "73":
                    fragment = new FaceDetectionHolderFrgamnet();
                    title = privillageName;
                    break;
                case "74":
                    fragment = new FaceRegistrationFragment();
                    title = privillageName;
                    break;

                case "75":
                    fragment = new TeamFaceAttendanceFragment();
                    title = privillageName;
                    break;
                //--------------------------------------------------------------------------------------------------------------
                case "76":
                    fragment = new INOUTHolderFragment();
                    title = privillageName;
                    break;
                case "77":
                    Intent intent1 = new Intent(DashBoardActivity.this, WebviewActivity.class);
                    startActivity(intent1);
                    title = privillageName;
                    break;

            }
            if (fragment != null) {
                if (fragment instanceof Customer_visit_new) {
                    /*TODO WHEN NEED to check customerView instance */

                } else {
                    /*TODO  stop service for fetching gps status on customer visit fragment */
                    stopService(new Intent(DashBoardActivity.this, LocationService.class));
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle(title);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void showNetworkErrorDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DashBoardActivity.this);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_network_error, null);
        dialogBuilder.setView(dialogView);
        Button settingButton = dialogView.findViewById(R.id.settingButton);
        Button retryButton = dialogView.findViewById(R.id.retryButton);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        retryButton.setOnClickListener(view -> {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit ?");
        builder.setPositiveButton("Yes", (dialog, which) -> finish());
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
            displayView("1", "My Profile");
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        MyNotificationManager.clearNotifications(getApplicationContext());

        if (Constants.FACE_TYPE.equals("TEAM_FACE")) {
            displayView("75", "Team Face Attendance");
            Constants.FACE_TYPE = "";
        }
        if (Constants.FACE_TYPE.equals("SIMPLE_FACE")) {
            displayView("73", "Face Attandance");
            Constants.FACE_TYPE = "";
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(act2InitReceiver);
    }
}
