package com.savvy.hrmsnewapp.activity;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.savvy.hrmsnewapp.R;

/**
 * Created by orapc7 on 14/12/2017.
 */

public class TestingActivity extends MarkAttendanceGoogleApi{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_location_testing);

        new AddNewLocationInPopUp().execute();
        Location loc = getMyLocation();
        if(loc!=null){
            Toast.makeText(TestingActivity.this, "Location Found "+loc.getLatitude(), Toast.LENGTH_SHORT).show();
        }
    }
}
