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

public class CustomerVisitInOutHolder extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_comVisitRequest, l2_comVisitStatus;
    CustomTextView txt_comVisitRequest, txt_comVisitStatus;
    ImageView img_comVisitRequest, imgcomVisitStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customervisit_inout_holder, container, false);

        ll_comVisitRequest = view.findViewById(R.id.travel_comVisitRequestLinear1);
        txt_comVisitRequest = view.findViewById(R.id.travel_comVisitRequestTab1);
        img_comVisitRequest = view.findViewById(R.id.travel_comVisitlineRequest1);
        l2_comVisitStatus = view.findViewById(R.id.travel_comVisitStatusLinear2);
        txt_comVisitStatus = view.findViewById(R.id.travel_comVisitStatusTab2);
        imgcomVisitStatus = view.findViewById(R.id.travel_comVisitLineImageStatus2);

        ll_comVisitRequest.setOnClickListener(this);
        l2_comVisitStatus.setOnClickListener(this);
        try {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            CustomerVisitInOutFragment customerVisitInOutFragment = new CustomerVisitInOutFragment();
            transaction.replace(R.id.frame_comVisit_holder, customerVisitInOutFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_comVisitStatus.setTextColor(0xFF7B7979);
            txt_comVisitRequest.setTextColor(0xFF5082E6);

            imgcomVisitStatus.setVisibility(View.GONE);
            img_comVisitRequest.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.travel_comVisitRequestLinear1:
                try {
                    CustomerVisitInOutFragment customerVisitInOutFragment = new CustomerVisitInOutFragment();
                    transaction.replace(R.id.frame_comVisit_holder, customerVisitInOutFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_comVisitStatus.setTextColor(0xFF7B7979);
                    txt_comVisitRequest.setTextColor(0xFF5082E6);

                    img_comVisitRequest.setVisibility(View.VISIBLE);
                    imgcomVisitStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.travel_comVisitStatusLinear2:
                try {
                    CustomerVisitInOutStatusFragment customerVisitInOutStatusFragment = new CustomerVisitInOutStatusFragment();
                    transaction.replace(R.id.frame_comVisit_holder, customerVisitInOutStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_comVisitRequest.setTextColor(0xFF7B7979);
                    txt_comVisitStatus.setTextColor(0xFF5082E6);

                    imgcomVisitStatus.setVisibility(View.VISIBLE);
                    img_comVisitRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }
    }
}
