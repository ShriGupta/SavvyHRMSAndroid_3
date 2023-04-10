package com.savvy.hrmsnewapp.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.TrackMeActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TrackMeFragment extends BaseFragment {

    TextView tv_trackme_date, tv_trackme_time;
    Button btn_track_my_location;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_base_track_me, container, false);
        tv_trackme_date = rootView.findViewById(R.id.tv_trackme_date);
        tv_trackme_time = rootView.findViewById(R.id.tv_trackme_time);
        btn_track_my_location = rootView.findViewById(R.id.btn_track_my_location);
        return rootView;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // todo for date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        tv_trackme_date.setText(formattedDate);

        // todo for time
        Date currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        String timeformat = time.format(currentTime);
        tv_trackme_time.setText(timeformat);
        btn_track_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(),TrackMeActivity.class));
            }
        });

    }
}
