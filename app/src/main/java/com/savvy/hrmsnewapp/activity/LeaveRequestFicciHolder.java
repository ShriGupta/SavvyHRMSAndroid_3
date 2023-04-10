package com.savvy.hrmsnewapp.activity;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.BaseFragment;
import com.savvy.hrmsnewapp.fragment.LeaveRequestFicciFragment;
import com.savvy.hrmsnewapp.fragment.LeaveStatusFicciFragment;

public class LeaveRequestFicciHolder extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_leaveRequest, ll_leaveStatus;
    ImageView img_leaveRequest, imgleaveStatus;
    CustomTextView txt_leaveRequest, txt_leaveStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_request, container, false);

        ll_leaveRequest = view.findViewById(R.id.leave_linear2);
        ll_leaveStatus = view.findViewById(R.id.leave_linear1);
        img_leaveRequest = view.findViewById(R.id.line_status1_leave);
        imgleaveStatus = view.findViewById(R.id.line_status_leave);

        txt_leaveRequest = view.findViewById(R.id.leave_tab2);
        txt_leaveStatus = view.findViewById(R.id.leave_tab1);

        ll_leaveStatus.setOnClickListener(this);
        ll_leaveRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            LeaveRequestFicciFragment leaveRequestFicciFragment = new LeaveRequestFicciFragment();
            transaction.replace(R.id.frame_leave_holder, leaveRequestFicciFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_leaveRequest.setTextColor(0xFF5082E6);
            txt_leaveStatus.setTextColor(0xFF7B7979);

            img_leaveRequest.setVisibility(View.VISIBLE);
            imgleaveStatus.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }


    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.leave_linear1:
                try {
                    LeaveStatusFicciFragment leaveStatusFragment = new LeaveStatusFicciFragment();
                    transaction.replace(R.id.frame_leave_holder, leaveStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_leaveStatus.setTextColor(0xFF5082E6);
                    txt_leaveRequest.setTextColor(0xFF7B7979);

                    imgleaveStatus.setVisibility(View.VISIBLE);
                    img_leaveRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.leave_linear2:
                try {
                    LeaveRequestFicciFragment leaveRequestFicciFragment = new LeaveRequestFicciFragment();
                    transaction.replace(R.id.frame_leave_holder, leaveRequestFicciFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_leaveRequest.setTextColor(0xFF5082E6);
                    txt_leaveStatus.setTextColor(0xFF7B7979);

                    img_leaveRequest.setVisibility(View.VISIBLE);
                    imgleaveStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }

    }
}
