package com.savvy.hrmsnewapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.database.Db_Helper;
import com.savvy.hrmsnewapp.service.TrackGPS;
import com.savvy.hrmsnewapp.service.Track_Me_Location_Service;
import com.savvy.hrmsnewapp.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

public class Track_Me extends BaseActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared, shareData;
    SharedPreferences.Editor editor;
    String empoyeeId = "", employeeName = "";
    CustomTextView txt_heading, txtResult;
    Timer timer;
    TimerTask timerTask;
    Handler handler;
    Thread thread = null;
    public static long counter = 0;
    Db_Helper db_helper;

    Button btn_startService, btn_stopService, btn_Back;
    boolean flag;
    double latitude, longitude;
    LocationManager locationManager;
    Location loc = null;
    String cityName = "";
    StringBuffer buffer;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue requestQueue;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 123;
    private static final int ACCESS_FINE_LOCATION = 2;
    private static final int ACCESS_CORSE_LOCATION = 3;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 3449;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track__me);

        db_helper = new Db_Helper(getApplicationContext());
        shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        empoyeeId = (shared.getString("EmpoyeeId", ""));
        employeeName = (shared.getString("EmpoyeeName", ""));

        txt_heading = (CustomTextView) findViewById(R.id.track_location_heading);

        btn_startService = (Button) findViewById(R.id.btn_startService);
        btn_stopService = (Button) findViewById(R.id.btn_stopService);
        btn_Back = (Button) findViewById(R.id.btn_Back);

        TrackGPS trackGPS = new TrackGPS(Track_Me.this);
        if(!trackGPS.canGetLocation()){
            trackGPS.showSettingsAlert();
        }

        if(Constants.TRACK_ME_START_SERVICE == 2){
            btn_startService.setEnabled(false);
            btn_stopService.setEnabled(true);
            btn_startService.setBackgroundResource(R.drawable.button_border_click);
            btn_stopService.setBackgroundResource(R.drawable.button_border);
        } else if(Constants.TRACK_ME_START_SERVICE == 1){
            btn_startService.setEnabled(true);
            btn_stopService.setEnabled(false);
            btn_startService.setBackgroundResource(R.drawable.button_border);
            btn_stopService.setBackgroundResource(R.drawable.button_border_click);
        }

        String TrackHeading = "<font color=\"#277ddb\"><bold><u>" + "Track My Location" + "</u></bold></font>";
        txt_heading.setText(Html.fromHtml(TrackHeading));
        txt_heading.setTextSize(20);

        Constants.NO_NETWORK = true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            }
        }


        btn_startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.TRACK_ME_START_SERVICE = 2;

                btn_startService.setEnabled(false);
                btn_stopService.setEnabled(true);
                try{
                    btn_startService.setBackgroundResource(R.drawable.button_border_click);
                    btn_stopService.setBackgroundResource(R.drawable.button_border);
                    Toast.makeText(getApplicationContext(), "Service Start", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Track_Me.this, Track_Me_Location_Service.class);
                    startService(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Constants.TRACK_ME_START_SERVICE = 1;

                    btn_startService.setEnabled(true);
                    btn_stopService.setEnabled(false);

                    btn_startService.setBackgroundResource(R.drawable.button_border);
                    btn_stopService.setBackgroundResource(R.drawable.button_border_click);

                    Toast.makeText(Track_Me.this, "Service Stop", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Track_Me.this, Track_Me_Location_Service.class);
                    stopService(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Track_Me.this,DashBoardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();
                bundle.putString("PostionId","32");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        assert getSupportActionBar()!=null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home) {
            Intent intent = new Intent(this,DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("PostionId","32");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkLocationPermission();
                    //hi
                }
            }
        }
    }

}
