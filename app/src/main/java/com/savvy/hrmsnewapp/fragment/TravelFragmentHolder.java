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

public class TravelFragmentHolder extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_travelRequest,ll_travelStatus;
    ImageView img_travelRequest,img_travelStatus;
    CustomTextView txt_travelRequest,txt_travelStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_fragment_holder, container, false);

        ll_travelRequest = view.findViewById(R.id.travel_linear1);
        ll_travelStatus = view.findViewById(R.id.travel_linear2);
        img_travelRequest = view.findViewById(R.id.line_status);
        img_travelStatus = view.findViewById(R.id.line_status1);

        txt_travelRequest = view.findViewById(R.id.travel_tab1);
        txt_travelStatus = view.findViewById(R.id.travel_tab2);

        ll_travelStatus.setOnClickListener(this);
        ll_travelRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            TravelRequestFragment travelRequestFragment= new TravelRequestFragment();
            transaction.replace(R.id.frame_travel_holder, travelRequestFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            txt_travelStatus.setTextColor(0xFF5082E6);
            txt_travelRequest.setTextColor(0xFF7B7979);

            img_travelStatus.setVisibility(View.VISIBLE);
            img_travelRequest.setVisibility(View.GONE);

        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId())
        {
            case R.id.travel_linear1:
                try {
                    TravelStatusFragment travelStatusFragment = new TravelStatusFragment();
                    transaction.replace(R.id.frame_travel_holder, travelStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_travelRequest.setTextColor(0xFF5082E6);
                    txt_travelStatus.setTextColor(0xFF7B7979);

                    img_travelRequest.setVisibility(View.VISIBLE);
                    img_travelStatus.setVisibility(View.GONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.travel_linear2:
                try {
                    TravelRequestFragment travelRequestFragment= new TravelRequestFragment();
                    transaction.replace(R.id.frame_travel_holder, travelRequestFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_travelStatus.setTextColor(0xFF5082E6);
                    txt_travelRequest.setTextColor(0xFF7B7979);

                    img_travelStatus.setVisibility(View.VISIBLE);
                    img_travelRequest.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }

}
