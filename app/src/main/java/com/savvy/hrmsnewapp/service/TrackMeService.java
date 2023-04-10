package com.savvy.hrmsnewapp.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.TrackMeActivity;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.room_database.TrackMeModel;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.Utilities;
import com.savvy.hrmsnewapp.volleyMultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;
import static com.savvy.hrmsnewapp.fragment.TravelRequestFicciFragment.MY_PREFS_NAME;

public class TrackMeService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    String dateTime = "";
    String track_lat = "";
    String track_lng = "";
    String batteryLevel = "";
    String locationString = "";
    String trackmeDetails = "";
    String empId = "";
    String empUsername = "";

    SharedPreferences sharedPreferences;

    int localDataInsertInterval;
    int serverDataUploadInterval;
    private Timer mTimer = null;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        forgroundNotification();
        sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        try {
            empId = (sharedPreferences.getString("EmpoyeeId", ""));
            empUsername = (sharedPreferences.getString("EmpoyeeName", ""));
            localDataInsertInterval = Integer.parseInt(sharedPreferences.getString("localDataInsertInterval", ""));
            serverDataUploadInterval = Integer.parseInt(sharedPreferences.getString("serverDataUploadInterval", ""));
            Log.e(TAG, "onCreate: " + "LocalDataInterval" + localDataInsertInterval + ", " + "ServerDataInterval" + serverDataUploadInterval);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        track_lat = String.valueOf(location.getLatitude());
                        track_lng = String.valueOf(location.getLongitude());
                        Log.e(TAG, "onLocationChanged: " + track_lat + ", " + track_lng);
                        locationString = Utilities.getAddressFromLocation(getApplicationContext(), location);
                        sendMessage(locationString);
                    }
                }
            }
        };


    }

    private void sendMessage(String locationString) {
        Intent intent = new Intent("my-message");
        intent.putExtra("my-integer", locationString);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: Called");
        startLocationUpdates();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToInsertData(), 5, localDataInsertInterval*1000);
        mTimer.schedule(new TimerTaskToUploadData(), 5, serverDataUploadInterval*1000);
    }


    private class TimerTaskToInsertData extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            insertDataIntoLocalDb();
        }
    }

    private class TimerTaskToUploadData extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            getTrackMeData();
        }
    }

    private void batteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                //batterLevel.setText("Battery Level Remaining: " + level + "%");
                batteryLevel = String.valueOf(level);
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getCurrentDateandTime() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YY hh:mm:ss a", Locale.US);
        String dateToStr = format.format(today);
        dateTime = dateToStr + " " + ZonedDateTime.now().getZone().toString();
    }

    private void generateTrackmeDetails(String dateTime, String latitude, String longitude, String locationString, String batteryLevel) {
        trackmeDetails = "@" + dateTime + "|" + latitude + "|" + longitude + "|" + locationString + "|" + batteryLevel;
        Log.e(TAG, "generateTrackmeDetails: " + trackmeDetails);
        if (!track_lat.equals("")) {
            TrackMeModel trackMeModel = new TrackMeModel();
            trackMeModel.setTrackMeDetails(trackmeDetails);
            new BackgroundInsertData(trackMeModel).execute();
        } else {
            trackmeDetails = "";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void insertDataIntoLocalDb() {
        getCurrentDateandTime();
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                //batterLevel.setText("Battery Level Remaining: " + level + "%");
                batteryLevel = String.valueOf(level);
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        // batteryLevel();
        generateTrackmeDetails(dateTime, track_lat, track_lng, locationString, batteryLevel);

    }

    private void getTrackMeData() {
        new GetDataBackGroundTask().execute();
    }

    public void sendTrackMeDataToServer(String username, String empId, String trackmeDetail) {

        String TRACK_ME_URL = Constants.IP_ADDRESS + "/SavvyMobileService.svc/SaveTrackMePostInBulk";
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userName", username);
            jsonObject.put("employeeId", empId);
            jsonObject.put("deatails", trackmeDetail);

            Log.e(TAG, "sendTrackMeDataToServer: " + username + "," + empId + "," + trackmeDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(TRACK_ME_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse: " + response.toString());
                try {
                    if (response.getString("SaveTrackMePostInBulkResult").equals("1")) {
                        deleteDbData();
                    } else if (response.getString("SaveTrackMePostInBulkResult").equals("2")) {
                        deleteDbData();
                        stopTimerTask();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "onErrorResponse: " + error.toString());
            }
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

        int socketTimeOut = 3000000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    class BackgroundInsertData extends AsyncTask<Void, Void, Long> {
        TrackMeModel trackMeModel;

        public BackgroundInsertData(TrackMeModel trackMeModel) {
            this.trackMeModel = trackMeModel;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            long value = DatabaseClient.getInstance(TrackMeService.this).getAppDatabase().passengerDao().insertTrackMeDetails(trackMeModel);
            return value;
        }

        @Override
        protected void onPostExecute(Long aVoid) {
            super.onPostExecute(aVoid);
            Log.e(TAG, "insertDataIntoLocalDb: " + aVoid);
            trackmeDetails = "";
            dateTime = "";
            track_lat = "";
            track_lng = "";
            //batteryLevel = "";
            locationString = "";
        }
    }

    class GetDataBackGroundTask extends AsyncTask<Void, Void, List<TrackMeModel>> {
        @Override
        protected List<TrackMeModel> doInBackground(Void... voids) {
            List<TrackMeModel> value = DatabaseClient.getInstance(TrackMeService.this).getAppDatabase().passengerDao().getTrackMeDetails();
            return value;
        }

        @Override
        protected void onPostExecute(List<TrackMeModel> trackMeModelsList) {
            super.onPostExecute(trackMeModelsList);
            String dbtrackmeDetails = "";
            for (int i = 0; i < trackMeModelsList.size(); i++) {
                dbtrackmeDetails += trackMeModelsList.get(i).getTrackMeDetails();
            }
            Log.e(TAG, "getTrackMeData: " + trackMeModelsList.size() + " : " + dbtrackmeDetails);
            if (!dbtrackmeDetails.equals("")) {
                if (Utilities.isNetworkAvailable(TrackMeService.this)) {
                    sendTrackMeDataToServer(empUsername, empId, dbtrackmeDetails);
                }
            }
        }
    }

    public void deleteDbData() {
        Log.e(TAG, "data deleting from db: ");
        AsyncTask.execute(() -> DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().passengerDao().deleteAllTrackMeData());
    }

    private void stopTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            stopForeground(true);
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        stopTimerTask();
    }


    void forgroundNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service");
        } else {

            // Create notification default intent.
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            // Create notification builder.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            // Make notification show big text.
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("Music player implemented by foreground service.");
            bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.");
            // Set big text style.
            builder.setStyle(bigTextStyle);

            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            builder.setLargeIcon(largeIconBitmap);
            // Make the notification max priority.
            builder.setPriority(Notification.PRIORITY_MAX);
            // Make head-up notification.
            builder.setFullScreenIntent(pendingIntent, true);
            Intent playIntent = new Intent(this, TrackMeActivity.class);
            playIntent.setAction("ACTION_PLAY");
            PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
            NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);
            builder.addAction(playAction);

            // Add Pause button intent in notification.
            Intent pauseIntent = new Intent(this, TrackMeActivity.class);
            pauseIntent.setAction("ACTION_PAUSE");
            PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
            NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
            builder.addAction(prevAction);

            // Build the notification.
            Notification notification = builder.build();

            // Start foreground service.
            startForeground(1, notification);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName) {
        Intent resultIntent = new Intent(this, TrackMeActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("SavvyHRMS is actively using your location.")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(resultPendingIntent) //intent
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notificationBuilder.build());
        startForeground(1, notification);
    }
}
