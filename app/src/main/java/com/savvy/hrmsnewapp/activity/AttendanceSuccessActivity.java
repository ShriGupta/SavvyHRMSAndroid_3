package com.savvy.hrmsnewapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.interfaces.OnSuccessFaceAttendance;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.GifImageView;

public class AttendanceSuccessActivity extends BaseActivity {

    TextView tv_gotoback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_success);
        setTitle("Face Verified");
        setUpToolBar();

        GifImageView gifImageView = (GifImageView) findViewById(R.id.gifImage);
        gifImageView.setGifImageResource(R.drawable.mark_attendance);

        tv_gotoback = findViewById(R.id.tv_gotoback);
        tv_gotoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}