package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class LoanRequestFragmentHolder extends BaseFragment implements View.OnClickListener {

    private LinearLayout ll_comVisitRequest, l2_loanStatus;
    private CustomTextView txt_loanRequest, txt_loanStatus;
    private ImageView img_loanRequest, img_loanStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_loan_request_holder, container, false);

        ll_comVisitRequest = view.findViewById(R.id.travel_LoanRequestLinear1);
        txt_loanRequest = view.findViewById(R.id.travel_LoanRequestTab1);
        img_loanRequest = view.findViewById(R.id.travel_LoanlineRequest1);
        l2_loanStatus = view.findViewById(R.id.travel_LoanStatusLinear2);
        txt_loanStatus = view.findViewById(R.id.travel_LoanStatusTab2);
        img_loanStatus = view.findViewById(R.id.travel_LoanLineImageStatus2);

        ll_comVisitRequest.setOnClickListener(this);
        l2_loanStatus.setOnClickListener(this);
        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            LoanRequestFragment loanRequestFragment = new LoanRequestFragment();
            transaction.replace(R.id.frame_Loan_holder, loanRequestFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_loanStatus.setTextColor(0xFF7B7979);
            txt_loanRequest.setTextColor(0xFF5082E6);

            img_loanStatus.setVisibility(View.GONE);
            img_loanRequest.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;

    }


    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.travel_LoanRequestLinear1:
                try {
                    LoanRequestFragment loanRequestFragment = new LoanRequestFragment();
                    transaction.replace(R.id.frame_Loan_holder, loanRequestFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_loanStatus.setTextColor(0xFF7B7979);
                    txt_loanRequest.setTextColor(0xFF5082E6);

                    img_loanRequest.setVisibility(View.VISIBLE);
                    img_loanStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.travel_LoanStatusLinear2:
                try {
                    LoanStatusFragment loanStatusFragment = new LoanStatusFragment();
                    transaction.replace(R.id.frame_Loan_holder, loanStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_loanRequest.setTextColor(0xFF7B7979);
                    txt_loanStatus.setTextColor(0xFF5082E6);

                    img_loanStatus.setVisibility(View.VISIBLE);
                    img_loanRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }
    }
}
