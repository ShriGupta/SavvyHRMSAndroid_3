package com.savvy.hrmsnewapp.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Customer_visit_new extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_custRequest, ll_custStatus;
    ImageView img_custRequest, img_custStatus;
    CustomTextView txt_custRequest, txt_custStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_visit_new, container, false);
        ll_custRequest = view.findViewById(R.id.cust_linear1);
        ll_custStatus = view.findViewById(R.id.cust_linear2);
        img_custRequest = view.findViewById(R.id.line_status);
        img_custStatus = view.findViewById(R.id.line_status1);

        txt_custRequest = view.findViewById(R.id.cust_tab1);
        txt_custStatus = view.findViewById(R.id.cust_tab2);

        ll_custStatus.setOnClickListener(this);
        ll_custRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            // AttendanceFragment custRequestFragment = new AttendanceFragment();
            MarkAttendanceWithMapFragment markAttendanceWithMapFragment = new MarkAttendanceWithMapFragment();
            transaction.replace(R.id.frame_cust_holder, markAttendanceWithMapFragment);
            transaction.addToBackStack(null);
            transaction.commit();

//            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//            MarkAttendanceWithInOut custRequestFragment = new MarkAttendanceWithInOut();
//            transaction.replace(R.id.frame_cust_holder, custRequestFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
            txt_custStatus.setTextColor(0xFF5082E6);
            txt_custRequest.setTextColor(0xFF7B7979);

            img_custStatus.setVisibility(View.VISIBLE);
            img_custRequest.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.cust_linear1:
                try {
                    CustomerViewPunchDetail customerViewPunchDetail = new CustomerViewPunchDetail();
                    transaction.replace(R.id.frame_cust_holder, customerViewPunchDetail);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_custRequest.setTextColor(0xFF5082E6);
                    txt_custStatus.setTextColor(0xFF7B7979);

                    img_custRequest.setVisibility(View.VISIBLE);
                    img_custStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.cust_linear2:
                try {
                    MarkAttendanceWithMapFragment markAttendanceWithMapFragment = new MarkAttendanceWithMapFragment();
                    transaction.replace(R.id.frame_cust_holder, markAttendanceWithMapFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_custStatus.setTextColor(0xFF5082E6);
                    txt_custRequest.setTextColor(0xFF7B7979);

                    img_custStatus.setVisibility(View.VISIBLE);
                    img_custRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }
    }
}