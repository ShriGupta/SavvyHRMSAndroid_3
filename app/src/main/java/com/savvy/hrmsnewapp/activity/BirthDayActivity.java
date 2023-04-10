package com.savvy.hrmsnewapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.BirthDayAndAnniversaryAdaptor;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.DashBoardFragmentMain;

import java.util.ArrayList;
import java.util.HashMap;

public class BirthDayActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    BirthDayAndAnniversaryAdaptor birthDayAndAnniversaryAdaptor;
    RecyclerView birthDayandAnniversaryListView;
    ImageView imageView;
    CustomTextView datanotFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birth_day);


        birthDayandAnniversaryListView = findViewById(R.id.birthDayandAnniversaryListView);
        imageView = findViewById(R.id.dashboard_image_view);
        datanotFound = findViewById(R.id.dashboard_data_not_found);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        birthDayandAnniversaryListView.setLayoutManager(mLayoutManager);
        birthDayandAnniversaryListView.setItemAnimator(new DefaultItemAnimator());

        ArrayList<HashMap<String, String>> arrayList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("key");
        String type = getIntent().getStringExtra("type");
        Log.e("", "onCreate: " + type);
        assert arrayList != null;
        int size = arrayList.size();

        if (type != null) {


            if (type.equals("arlData_Birthday")) {
                imageView.setImageResource(R.drawable.birth_day_image);
                setTitle("Birthday");
                assert getSupportActionBar() != null;
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                if (size == 0) {
                    datanotFound.setVisibility(View.VISIBLE);
                } else {
                    datanotFound.setVisibility(View.GONE);
                }
            } else if (type.equals("arlData_Marriage_Anniversary")) {
                imageView.setImageResource(R.drawable.marriage_anniversary_image);
                setTitle("Marriage Anniversary");
                assert getSupportActionBar() != null;
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (size == 0) {
                    datanotFound.setVisibility(View.VISIBLE);
                } else {
                    datanotFound.setVisibility(View.GONE);
                }
            } else if (type.equals("arlData_Join_Anniversary")) {
                imageView.setImageResource(R.drawable.join_anniversary_image);
                setTitle("Joining Anniversary");
                assert getSupportActionBar() != null;
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (size == 0) {
                    datanotFound.setVisibility(View.VISIBLE);
                } else {
                    datanotFound.setVisibility(View.GONE);
                }
            } else if (type.equals("holiday_Data")) {
                imageView.setImageResource(R.drawable.holiday_image);
                setTitle("Holidays");
                assert getSupportActionBar() != null;
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (size == 0) {
                    datanotFound.setVisibility(View.VISIBLE);
                } else {
                    datanotFound.setVisibility(View.GONE);
                }
            } else if (type.equals("arlData_Announcement")) {
                imageView.setImageResource(R.drawable.announcement_image);
                setTitle("Announcement");
                assert getSupportActionBar() != null;
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (size == 0) {
                    datanotFound.setVisibility(View.VISIBLE);
                } else {
                    datanotFound.setVisibility(View.GONE);
                }
            } else if (type.equals("arlData_ThoughtOfDay")) {
                imageView.setImageResource(R.drawable.thought_image);
                setTitle("Thought Of The Day");
                assert getSupportActionBar() != null;
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (size == 0) {
                    datanotFound.setVisibility(View.VISIBLE);
                } else {
                    datanotFound.setVisibility(View.GONE);
                }
            }


            birthDayAndAnniversaryAdaptor = new BirthDayAndAnniversaryAdaptor(BirthDayActivity.this, arrayList, type);
            birthDayandAnniversaryListView.setAdapter(birthDayAndAnniversaryAdaptor);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }
}
