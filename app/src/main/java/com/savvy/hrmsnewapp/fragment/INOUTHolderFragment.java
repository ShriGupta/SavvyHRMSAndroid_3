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

public class INOUTHolderFragment extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_in_outRequest, ll_in_outStatus;
    ImageView img_in_outRequest, imgin_outStatus;
    CustomTextView txt_in_outRequest, txt_in_outStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_in_out_holder, container, false);

        ll_in_outRequest = view.findViewById(R.id.in_out_linear1);
        ll_in_outStatus = view.findViewById(R.id.in_out_linear2);
        img_in_outRequest = view.findViewById(R.id.line_status);
        imgin_outStatus = view.findViewById(R.id.line_status1);

        txt_in_outRequest = view.findViewById(R.id.in_out_tab1);
        txt_in_outStatus = view.findViewById(R.id.in_out_tab2);

        ll_in_outStatus.setOnClickListener(this);
        ll_in_outRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            INOUTRequestFragment in_outRequestFragment = new INOUTRequestFragment();
            transaction.replace(R.id.frame_in_out_holder, in_outRequestFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_in_outStatus.setTextColor(0xFF5082E6);
            txt_in_outRequest.setTextColor(0xFF7B7979);

            imgin_outStatus.setVisibility(View.VISIBLE);
            img_in_outRequest.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.in_out_linear1:
                try {
                   // ODStatusFragment in_outStatusFragment = new ODStatusFragment();
                    INOUTStatusNewFragment in_outStatusFragment = new INOUTStatusNewFragment();
                    transaction.replace(R.id.frame_in_out_holder, in_outStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_in_outRequest.setTextColor(0xFF5082E6);
                    txt_in_outStatus.setTextColor(0xFF7B7979);

                    img_in_outRequest.setVisibility(View.VISIBLE);
                    imgin_outStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.in_out_linear2:
                try {
                    INOUTRequestFragment in_outRequestFragment = new INOUTRequestFragment();
                    transaction.replace(R.id.frame_in_out_holder, in_outRequestFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_in_outStatus.setTextColor(0xFF5082E6);
                    txt_in_outRequest.setTextColor(0xFF7B7979);

                    imgin_outStatus.setVisibility(View.VISIBLE);
                    img_in_outRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }
}
