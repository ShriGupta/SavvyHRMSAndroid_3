package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class PunchRequestHolder extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_punchRequest,ll_punchStatus;
    ImageView img_punchRequest,img_punchStatus;
    CustomTextView txt_punchRequest,txt_punchStatus;
    CoordinatorLayout coordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_punch_request_holder, container, false);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

        ll_punchRequest = view.findViewById(R.id.punch_linear1);
        ll_punchStatus = view.findViewById(R.id.punch_linear2);
        img_punchRequest = view.findViewById(R.id.punch_line_status);
        img_punchStatus = view.findViewById(R.id.punch_line_status1);

        txt_punchRequest = view.findViewById(R.id.punch_tab1);
        txt_punchStatus = view.findViewById(R.id.punch_tab2);

        ll_punchStatus.setOnClickListener(this);
        ll_punchRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            PunchRegularizationRequestFragment punchRegular = new PunchRegularizationRequestFragment();
            transaction.replace(R.id.frame_punch_holder1, punchRegular);
            transaction.addToBackStack(null);
            transaction.commit();

            txt_punchStatus.setTextColor(0xFF5082E6);
            txt_punchRequest.setTextColor(0xFF7B7979);

            img_punchRequest.setVisibility(View.GONE);
            img_punchStatus.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  view;
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId())
        {
            case R.id.punch_linear1:
                try {
                    PunchStatusFragment punchStatusFragment = new PunchStatusFragment();
                    transaction.replace(R.id.frame_punch_holder1, punchStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_punchRequest.setTextColor(0xFF5082E6);
                    txt_punchStatus.setTextColor(0xFF7B7979);

                    img_punchRequest.setVisibility(View.VISIBLE);
                    img_punchStatus.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.punch_linear2:
                try {
                    PunchRegularizationRequestFragment punchRegular = new PunchRegularizationRequestFragment();
                    transaction.replace(R.id.frame_punch_holder1, punchRegular);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_punchStatus.setTextColor(0xFF5082E6);
                    txt_punchRequest.setTextColor(0xFF7B7979);

                    img_punchRequest.setVisibility(View.GONE);
                    img_punchStatus.setVisibility(View.VISIBLE);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }


    }
}
