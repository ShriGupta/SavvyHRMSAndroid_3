package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class TravelDeskFicciHolder extends BaseFragment implements View.OnClickListener {
    LinearLayout ll_LERequest, ll_LEStatus;
    ImageView img_LERequest, imgLEStatus;
    CustomTextView txt_LERequest, txt_LEStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_desk_ficci_holder, container, false);

        ll_LERequest = view.findViewById(R.id.travel_desk_linear2);
        ll_LEStatus = view.findViewById(R.id.travel_desk_linear1);
        img_LERequest = view.findViewById(R.id.travel_desk_line_status1);
        imgLEStatus = view.findViewById(R.id.travel_desk_line_status);

        txt_LERequest = view.findViewById(R.id.travel_desk_tab2);
        txt_LEStatus = view.findViewById(R.id.travel_desk_tab1);

        ll_LEStatus.setOnClickListener(this);
        ll_LERequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            TravelDeskFicciFragment travelDeskFicciFragment = new TravelDeskFicciFragment();
            transaction.replace(R.id.frame_travel_Desk_holder, travelDeskFicciFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            txt_LEStatus.setTextColor(0xFF5082E6);
            txt_LERequest.setTextColor(0xFF7B7979);

            imgLEStatus.setVisibility(View.GONE);
            img_LERequest.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.travel_desk_linear1:

                try {
                    TravelDeskFicciFragment travelDeskFicciFragment = new TravelDeskFicciFragment();
                    transaction.replace(R.id.frame_travel_Desk_holder, travelDeskFicciFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_LERequest.setTextColor(0xFF7B7979);
                    txt_LEStatus.setTextColor(0xFF5082E6);

                    imgLEStatus.setVisibility(View.GONE);
                    img_LERequest.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.travel_desk_linear2:

                try {
                    TravelDeskHistoryFicciFragment travelDeskHistoryFicciFragment = new TravelDeskHistoryFicciFragment();
                    transaction.replace(R.id.frame_travel_Desk_holder, travelDeskHistoryFicciFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_LEStatus.setTextColor(0xFF7B7979);
                    txt_LERequest.setTextColor(0xFF5082E6);

                    imgLEStatus.setVisibility(View.VISIBLE);
                    img_LERequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }
}

