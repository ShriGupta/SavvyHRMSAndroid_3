package com.savvy.hrmsnewapp.fragment;

/**
 * Created by Amulya on 29/07/15.
 */

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;


public class DashboardFragment extends BaseFragment  {

    private Button btnSyllbus;
    private Button btnResult;
    private Button btnAttendance;

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
/*
        //coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
        btnSyllbus = (Button) view.findViewById(R.id.btn_information);
        btnResult = (Button) view.findViewById(R.id.btn_question);
        //btnAttendance = (Button) view.findViewById(R.id.btn_parent);

        btnSyllbus.setOnClickListener(this);
        btnResult.setOnClickListener(this);
        //btnAttendance.setOnClickListener(this);

        btnSyllbus.setBackgroundResource(R.drawable.btn_white_bg);
        btnSyllbus.setTextColor(getResources().getColor(R.color.colorBlack));

        btnResult.setBackgroundResource(R.drawable.trans_btn_bg);
        btnResult.setTextColor(getResources().getColor(R.color.textColorPrimary));

       *//* btnAttendance.setBackgroundResource(R.drawable.trans_btn_bg);
        btnAttendance.setTextColor(getResources().getColor(R.color.textColorPrimary));
*//*
        btnSyllbus.setEnabled(false);
        btnResult.setEnabled(true);
        //btnAttendance.setEnabled(true);*/

      /*  FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        DashboardFragment syllabusFragment = new DashboardFragment();
        transaction.replace(R.id.fragment_container, syllabusFragment);
        transaction.addToBackStack(null);
        transaction.commit();*/

    }

/*    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.btn_information:
                btnSyllbus.setEnabled(false);
                btnResult.setEnabled(true);
                btnAttendance.setEnabled(true);

                btnSyllbus.setBackgroundResource(R.drawable.btn_white_bg);
                btnSyllbus.setTextColor(getResources().getColor(R.color.colorBlack));

                btnResult.setBackgroundResource(R.drawable.trans_btn_bg);
                btnResult.setTextColor(getResources().getColor(R.color.textColorPrimary));

                btnAttendance.setBackgroundResource(R.drawable.trans_btn_bg);
                btnAttendance.setTextColor(getResources().getColor(R.color.textColorPrimary));



                break;

            case R.id.btn_question:
                btnSyllbus.setEnabled(true);
                btnResult.setEnabled(false);
                btnAttendance.setEnabled(true);




                break;

            case R.id.btn_parent:
                btnSyllbus.setEnabled(true);
                btnResult.setEnabled(true);
                btnAttendance.setEnabled(false);

                btnSyllbus.setBackgroundResource(R.drawable.trans_btn_bg);
                btnSyllbus.setTextColor(getResources().getColor(R.color.textColorPrimary));

                btnResult.setBackgroundResource(R.drawable.trans_btn_bg);
                btnResult.setTextColor(getResources().getColor(R.color.textColorPrimary));

                btnAttendance.setBackgroundResource(R.drawable.btn_white_bg);
                btnAttendance.setTextColor(getResources().getColor(R.color.colorBlack));



                break;

            default:
                break;
        }

    }*/
}