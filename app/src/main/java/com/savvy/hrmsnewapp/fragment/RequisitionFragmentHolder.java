package com.savvy.hrmsnewapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class RequisitionFragmentHolder extends Fragment {

    TextView tvRequest,status;
    LinearLayout ll_requsitionRequest,ll_requsitionStatus;
    ImageView img_requsitionRequest,img_requsitionStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.requisition_layout, container, false);

        ll_requsitionRequest = view.findViewById(R.id.requisition_Req_linear2);
        ll_requsitionStatus = view.findViewById(R.id.requisition_status_linear1);
        img_requsitionRequest = view.findViewById(R.id.requisition_req_line_status1);
        img_requsitionStatus = view.findViewById(R.id.requisition_status_line_status);

        tvRequest = view.findViewById(R.id.tv_requsition_request);
        status = view.findViewById(R.id.tv_requsition_status);

//        ll_requsitionStatus.setOnClickListener(this);
//        ll_requsitionRequest.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            RequisitionRequestFragment requestFragment = new RequisitionRequestFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, requestFragment);
            fragmentTransaction.commit();
            status.setTextColor(0xFF7B7979);
            tvRequest.setTextColor(0xFF5082E6);

            img_requsitionStatus.setVisibility(View.GONE);
            img_requsitionRequest.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
        tvRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                RequisitionRequestFragment requestFragment=new RequisitionRequestFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, requestFragment);
                fragmentTransaction.commit();

                tvRequest.setTextColor(0xFF5082E6);
                status.setTextColor(0xFF7B7979);

                img_requsitionRequest.setVisibility(View.VISIBLE);
                img_requsitionStatus.setVisibility(View.GONE);

            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    //RequisitionStatusFragment
                RequisitionStatusFragment statusFragment=new RequisitionStatusFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, statusFragment);
                fragmentTransaction.commit();
                status.setTextColor(0xFF5082E6);
                tvRequest.setTextColor(0xFF7B7979);

                img_requsitionStatus.setVisibility(View.VISIBLE);
                img_requsitionRequest.setVisibility(View.GONE);

            }
        });


    }
}
