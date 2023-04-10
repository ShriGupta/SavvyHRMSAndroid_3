package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.savvy.hrmsnewapp.R;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by hariom on 10/8/16.
 */
public class CalenderFragment  extends BaseFragment {
    public GregorianCalendar month, itemmonth;// calendar instances.


    public Handler handler;// for grabbing some event values for showing the dot
    // marker.
    public ArrayList<String> items; // container to store calendar items which
    // needs showing the event marker
    ArrayList<String> event;
    // LinearLayout rLayout;
    ArrayList<String> date;
    ArrayList<String> desc;

    public GregorianCalendar cal_month, cal_month_copy;


    int lastPos;
    private GridView gridview;
    private RelativeLayout previous;
    private RelativeLayout next;
    private CoordinatorLayout coordinatorLayout;


    public CalenderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calender_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        coordinatorLayout   = rootView.findViewById(R.id.coordinatorLayout);








    }











}
