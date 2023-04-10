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

public class ConveyanceApprovalHolder extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_conveyanceRequest,ll_conveyanceStatus;
    ImageView img_conveyanceRequest,img_conveyanceStatus;
    CustomTextView txt_conveyanceRequest,txt_conveyanceStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conveyance_approval_holder, container, false);

        ll_conveyanceRequest = view.findViewById(R.id.conveyance_linear1);
        ll_conveyanceStatus = view.findViewById(R.id.conveyance_linear2);
        img_conveyanceRequest = view.findViewById(R.id.line_status);
        img_conveyanceStatus = view.findViewById(R.id.line_status1);

        txt_conveyanceRequest = view.findViewById(R.id.conveyance_tab1);
        txt_conveyanceStatus = view.findViewById(R.id.conveyance_tab2);

        ll_conveyanceStatus.setOnClickListener(this);
        ll_conveyanceRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            ConveyanceApprovalFragment conveyanceApprovalFragment = new ConveyanceApprovalFragment();
            transaction.replace(R.id.frame_conveyance_holder, conveyanceApprovalFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            txt_conveyanceStatus.setTextColor(0xFF5082E6);
            txt_conveyanceRequest.setTextColor(0xFF7B7979);

            img_conveyanceStatus.setVisibility(View.VISIBLE);
            img_conveyanceRequest.setVisibility(View.GONE);

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
            case R.id.conveyance_linear1:
                try {

                    ConveyanceVoucherApprovalFragment conveyanceVoucherApprovalFragment = new ConveyanceVoucherApprovalFragment();
                    transaction.replace(R.id.frame_conveyance_holder, conveyanceVoucherApprovalFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_conveyanceRequest.setTextColor(0xFF5082E6);
                    txt_conveyanceStatus.setTextColor(0xFF7B7979);

                    img_conveyanceRequest.setVisibility(View.VISIBLE);
                    img_conveyanceStatus.setVisibility(View.GONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.conveyance_linear2:
                try {
                    ConveyanceApprovalFragment conveyanceApprovalFragment = new ConveyanceApprovalFragment();
                    transaction.replace(R.id.frame_conveyance_holder, conveyanceApprovalFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_conveyanceStatus.setTextColor(0xFF5082E6);
                    txt_conveyanceRequest.setTextColor(0xFF7B7979);

                    img_conveyanceStatus.setVisibility(View.VISIBLE);
                    img_conveyanceRequest.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }
}
