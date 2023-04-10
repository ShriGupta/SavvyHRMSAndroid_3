package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class TravelRequestFiccciFragmentHolder extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_opeRequest, l2_opeStatus;
    CustomTextView txt_opeRequest, txt_opeStatus;
    ImageView img_opeRequest, imgopeStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_request_holder, container, false);

        ll_opeRequest = view.findViewById(R.id.travel_RequestLinear1);
        txt_opeRequest = view.findViewById(R.id.travel_RequestTab1);
        img_opeRequest = view.findViewById(R.id.travel_lineRequest1);
        l2_opeStatus = view.findViewById(R.id.travel_StatusLinear2);
        txt_opeStatus = view.findViewById(R.id.travel_StatusTab2);
        imgopeStatus = view.findViewById(R.id.travel_LineImageStatus2);

        ll_opeRequest.setOnClickListener(this);
        l2_opeStatus.setOnClickListener(this);
        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            TravelRequestFicciFragment travelRequestFicciFragment = new TravelRequestFicciFragment();
            transaction.replace(R.id.frame_travel_holder, travelRequestFicciFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_opeStatus.setTextColor(0xFF7B7979);
            txt_opeRequest.setTextColor(0xFF5082E6);

            imgopeStatus.setVisibility(View.GONE);
            img_opeRequest.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.travel_RequestLinear1:
                try {
                    TravelRequestFicciFragment travelRequestFicciFragment = new TravelRequestFicciFragment();
                    transaction.replace(R.id.frame_travel_holder, travelRequestFicciFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_opeStatus.setTextColor(0xFF7B7979);
                    txt_opeRequest.setTextColor(0xFF5082E6);

                    img_opeRequest.setVisibility(View.VISIBLE);
                    imgopeStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.travel_StatusLinear2:
                try {
                    TravelStatusFicciFragment travelStatusFicciFragment = new TravelStatusFicciFragment();
                    transaction.replace(R.id.frame_travel_holder, travelStatusFicciFragment);
                    transaction.addToBackStack(null);
                    transaction.setCustomAnimations(R.anim.slide_down,R.anim.slide_up);
                    transaction.commit();

                    txt_opeRequest.setTextColor(0xFF7B7979);
                    txt_opeStatus.setTextColor(0xFF5082E6);

                    imgopeStatus.setVisibility(View.VISIBLE);
                    img_opeRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }
    }
}
