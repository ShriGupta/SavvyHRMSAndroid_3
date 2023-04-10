package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class LTFRequestFragmentHolder extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_LTFRequest, ll_LTFStatus;
    ImageView img_LTFRequest, imgLTFStatus;
    CustomTextView txt_LTFRequest, txt_LTFStatus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ltf_request_holder, container, false);

        ll_LTFRequest = view.findViewById(R.id.LTFRequest_linear2);
        ll_LTFStatus = view.findViewById(R.id.LTFRequest_linear1);
        img_LTFRequest = view.findViewById(R.id.LTFRequest_line_status1);
        imgLTFStatus = view.findViewById(R.id.LTFRequest_line_status);

        txt_LTFRequest = view.findViewById(R.id.LTFRequest_tab2);
        txt_LTFStatus = view.findViewById(R.id.LTFRequest_tab1);

        ll_LTFStatus.setOnClickListener(this);
        ll_LTFRequest.setOnClickListener(this);

        try {

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

            LTFRequestFragment ltfRequestFragment = new LTFRequestFragment();
            fragmentTransaction.replace(R.id.frame_LTF_Request_holder, ltfRequestFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            txt_LTFStatus.setTextColor(0xFF5082E6);
            txt_LTFRequest.setTextColor(0xFF7B7979);

            imgLTFStatus.setVisibility(View.GONE);
            img_LTFRequest.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.LTFRequest_linear1:

                try {
                    LTFRequestFragment ltfRequestFragment = new LTFRequestFragment();
                    transaction.replace(R.id.frame_LTF_Request_holder, ltfRequestFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_LTFRequest.setTextColor(0xFF7B7979);
                    txt_LTFStatus.setTextColor(0xFF5082E6);

                    imgLTFStatus.setVisibility(View.GONE);
                    img_LTFRequest.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case R.id.LTFRequest_linear2:

                try {
                    LTFStatusFragment ltfStatusFragment = new LTFStatusFragment();
                    transaction.replace(R.id.frame_LTF_Request_holder, ltfStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_LTFStatus.setTextColor(0xFF7B7979);
                    txt_LTFRequest.setTextColor(0xFF5082E6);

                    imgLTFStatus.setVisibility(View.VISIBLE);
                    img_LTFRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

    }
}
