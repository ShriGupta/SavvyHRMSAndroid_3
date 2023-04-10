package com.savvy.hrmsnewapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.fragment.OfflineLoginFragmentHolder;

import java.util.Objects;

public class OfflineMarkAttendanceActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_mark_attendance2);
        setFragment();
    }
    private void setFragment() {

        OfflineLoginFragmentHolder fragmentHolder = new OfflineLoginFragmentHolder();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.offline_frame_layout, fragmentHolder);
        fragmentTransaction.commit();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Offline Mark Attendance");
    }
}