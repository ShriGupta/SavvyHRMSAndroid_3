package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class WFH_HolderFragment extends Fragment implements View.OnClickListener {

    LinearLayout ll_whfRequest, ll_whfStatus;
    ImageView img_whfRequest, imgwhfStatus;
    CustomTextView txt_whfRequest, txt_whfStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmnt_whf_holder, container, false);

        ll_whfRequest = view.findViewById(R.id.od_linear1);
        ll_whfStatus = view.findViewById(R.id.od_linear2);
        img_whfRequest = view.findViewById(R.id.line_status);
        imgwhfStatus = view.findViewById(R.id.line_status1);

        txt_whfRequest = view.findViewById(R.id.od_tab1);
        txt_whfStatus = view.findViewById(R.id.od_tab2);

        ll_whfStatus.setOnClickListener(this);
        ll_whfRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            WorkFromHomeRequestFragment whfRequestFragment = new WorkFromHomeRequestFragment();
            transaction.replace(R.id.frame_od_holder, whfRequestFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_whfStatus.setTextColor(0xFF5082E6);
            txt_whfRequest.setTextColor(0xFF7B7979);

            imgwhfStatus.setVisibility(View.VISIBLE);
            img_whfRequest.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;


    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.od_linear1:
                try {
                    ODStatusFragment odStatusFragment = new ODStatusFragment();
                    transaction.replace(R.id.frame_od_holder, odStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_whfRequest.setTextColor(0xFF5082E6);
                    txt_whfStatus.setTextColor(0xFF7B7979);

                    img_whfRequest.setVisibility(View.VISIBLE);
                    imgwhfStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.od_linear2:
                try {
                    WorkFromHomeRequestFragment wfhFragmet = new WorkFromHomeRequestFragment();
                    transaction.replace(R.id.frame_od_holder, wfhFragmet);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_whfStatus.setTextColor(0xFF5082E6);
                    txt_whfRequest.setTextColor(0xFF7B7979);

                    imgwhfStatus.setVisibility(View.VISIBLE);
                    img_whfRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }
}
