package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class ShortLeaveRequestHolder extends BaseFragment implements View.OnClickListener {


    private LinearLayout ll_shortLeaveRequest, l2_shortLeaveStatus;
    private CustomTextView txt_shortLeaveRequest, txt_shortLeaveStatus;
    private ImageView img_shortLeaveRequest, shortLeaveStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_short_leave_request_holder, container, false);

        ll_shortLeaveRequest = view.findViewById(R.id.shortLeaveLinear1);
        txt_shortLeaveRequest = view.findViewById(R.id.shortLeaveTab1);
        img_shortLeaveRequest = view.findViewById(R.id.shortLeave_lineRequest1);
        l2_shortLeaveStatus = view.findViewById(R.id.shortLeave_StatusLinear2);
        txt_shortLeaveStatus = view.findViewById(R.id.shortLeave_StatusTab2);
        shortLeaveStatus = view.findViewById(R.id.shortLeave_LineImageStatus2);

        ll_shortLeaveRequest.setOnClickListener(this);
        l2_shortLeaveStatus.setOnClickListener(this);
        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            ShortLeaveRequestFragment shortLeaveRequestFragment = new ShortLeaveRequestFragment();
            transaction.replace(R.id.frame_shorLeave_holder, shortLeaveRequestFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_shortLeaveStatus.setTextColor(0xFF7B7979);
            txt_shortLeaveRequest.setTextColor(0xFF5082E6);

            shortLeaveStatus.setVisibility(View.GONE);
            img_shortLeaveRequest.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.shortLeaveLinear1:
                try {
                    ShortLeaveRequestFragment shortLeaveRequestFragment = new ShortLeaveRequestFragment();
                    transaction.replace(R.id.frame_shorLeave_holder, shortLeaveRequestFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_shortLeaveStatus.setTextColor(0xFF7B7979);
                    txt_shortLeaveRequest.setTextColor(0xFF5082E6);

                    img_shortLeaveRequest.setVisibility(View.VISIBLE);
                    shortLeaveStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.shortLeave_StatusLinear2:
                try {
                    ShortLeaveStatusFragment shortLeaveStatusFragment = new ShortLeaveStatusFragment();
                    transaction.replace(R.id.frame_shorLeave_holder, shortLeaveStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_shortLeaveRequest.setTextColor(0xFF7B7979);
                    txt_shortLeaveStatus.setTextColor(0xFF5082E6);

                    shortLeaveStatus.setVisibility(View.VISIBLE);
                    img_shortLeaveRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }
    }
}
