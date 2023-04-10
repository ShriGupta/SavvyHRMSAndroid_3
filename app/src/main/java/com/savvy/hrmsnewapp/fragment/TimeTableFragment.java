package com.savvy.hrmsnewapp.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.savvy.hrmsnewapp.R;

import java.text.SimpleDateFormat;

/**
 * Created by Ravi on 29/07/15.
 */
public class TimeTableFragment extends BaseFragment {
    SimpleDateFormat df;
    String formattedDate;
    RecyclerView recyclerView;

    int userid,courseid;
    private DatePickerDialog calendarDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private RelativeLayout previous;
    private RelativeLayout next;
    private String monthString;
    private String mCurrentDate;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.fragment_timetable, container, false);


        return rootView;
    }









}
