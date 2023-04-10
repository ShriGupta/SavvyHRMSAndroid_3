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

public class ResignationFragmentHolder extends BaseFragment implements View.OnClickListener{

    LinearLayout ll_resignationRequest,ll_resignationStatus;
    ImageView img_resignationRequest,img_resignationStatus;
    CustomTextView txt_resignationRequest,txt_resignationStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resignation_fragment_holder, container, false);

        ll_resignationRequest = view.findViewById(R.id.resignation_linear1);
        ll_resignationStatus = view.findViewById(R.id.resignation_linear2);
        img_resignationRequest = view.findViewById(R.id.line_status);
        img_resignationStatus = view.findViewById(R.id.line_status1);

        txt_resignationRequest = view.findViewById(R.id.resignation_tab1);
        txt_resignationStatus = view.findViewById(R.id.resignation_tab2);

        ll_resignationStatus.setOnClickListener(this);
        ll_resignationRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            ResignationRequestFragment resignationRequestFragment = new ResignationRequestFragment();
            transaction.replace(R.id.frame_resignation_holder, resignationRequestFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            txt_resignationStatus.setTextColor(0xFF5082E6);
            txt_resignationRequest.setTextColor(0xFF7B7979);

            img_resignationStatus.setVisibility(View.VISIBLE);
            img_resignationRequest.setVisibility(View.GONE);

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
            case R.id.resignation_linear1:
                try {
                    ResignationStatusFragment resignationStatusFragment = new ResignationStatusFragment();
                    transaction.replace(R.id.frame_resignation_holder, resignationStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_resignationRequest.setTextColor(0xFF5082E6);
                    txt_resignationStatus.setTextColor(0xFF7B7979);

                    img_resignationRequest.setVisibility(View.VISIBLE);
                    img_resignationStatus.setVisibility(View.GONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.resignation_linear2:
                try {
                    ResignationRequestFragment resignationRequestFragment = new ResignationRequestFragment();
                    transaction.replace(R.id.frame_resignation_holder, resignationRequestFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_resignationStatus.setTextColor(0xFF5082E6);
                    txt_resignationRequest.setTextColor(0xFF7B7979);

                    img_resignationStatus.setVisibility(View.VISIBLE);
                    img_resignationRequest.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }

}
