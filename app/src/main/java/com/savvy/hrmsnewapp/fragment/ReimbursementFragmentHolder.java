package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentTransaction;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class ReimbursementFragmentHolder extends BaseFragment implements View.OnClickListener{

    LinearLayout ll_reimbursementRequest,ll_reimbursementStatus;
    ImageView img_reimbursementRequest,img_reimbursementStatus;
    CustomTextView txt_reimbursementRequest,txt_reimbursementStatus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reimbursement_fragment_holder, container, false);

        ll_reimbursementRequest = view.findViewById(R.id.reimbur_linear1);
        ll_reimbursementStatus = view.findViewById(R.id.reimbur_linear2);
        img_reimbursementRequest = view.findViewById(R.id.line_status);
        img_reimbursementStatus = view.findViewById(R.id.line_status1);

        txt_reimbursementRequest = view.findViewById(R.id.reimbur_tab1);
        txt_reimbursementStatus = view.findViewById(R.id.reimbur_tab2);

        ll_reimbursementStatus.setOnClickListener(this);
        ll_reimbursementRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            ReimbursementRequestFragment reimbursementRequestFragment = new ReimbursementRequestFragment();
            transaction.replace(R.id.frame_reimbur_holder, reimbursementRequestFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            txt_reimbursementStatus.setTextColor(0xFF5082E6);
            txt_reimbursementRequest.setTextColor(0xFF7B7979);

            img_reimbursementStatus.setVisibility(View.VISIBLE);
            img_reimbursementRequest.setVisibility(View.GONE);

        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId())
        {
            case R.id.reimbur_linear1:
                try {
                    ReimbursementStatusFragment reimbursementStatusFragment = new ReimbursementStatusFragment();
                    transaction.replace(R.id.frame_reimbur_holder, reimbursementStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_reimbursementRequest.setTextColor(0xFF5082E6);
                    txt_reimbursementStatus.setTextColor(0xFF7B7979);

                    img_reimbursementRequest.setVisibility(View.VISIBLE);
                    img_reimbursementStatus.setVisibility(View.GONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.reimbur_linear2:
                try {
                    ReimbursementRequestFragment reimbursementRequestFragment = new ReimbursementRequestFragment();
                    transaction.replace(R.id.frame_reimbur_holder, reimbursementRequestFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_reimbursementStatus.setTextColor(0xFF5082E6);
                    txt_reimbursementRequest.setTextColor(0xFF7B7979);

                    img_reimbursementStatus.setVisibility(View.VISIBLE);
                    img_reimbursementRequest.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }

}
