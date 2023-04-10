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

public class ODHolderContractualFicciFragment extends BaseFragment implements View.OnClickListener {
    LinearLayout ll_odcRequest, ll_odcStatus;
    ImageView img_odcRequest, imgodcStatus;
    CustomTextView txt_odcRequest, txt_odcStatus;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_odholder_contractual_ficci, container, false);
        ll_odcRequest = view.findViewById(R.id.odContract_linear1);
        ll_odcStatus = view.findViewById(R.id.odContract_linear2);
        img_odcRequest = view.findViewById(R.id.odContractline_status);
        imgodcStatus = view.findViewById(R.id.odContractline_status1);

        txt_odcRequest = view.findViewById(R.id.odContract_tab1);
        txt_odcStatus = view.findViewById(R.id.odContract_tab2);

        ll_odcStatus.setOnClickListener(this);
        ll_odcRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            ODRequestConFicciFragment odRequestConFicciFragment = new ODRequestConFicciFragment();
            transaction.replace(R.id.frame_odContract_ficci_holder, odRequestConFicciFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_odcStatus.setTextColor(0xFF5082E6);
            txt_odcRequest.setTextColor(0xFF7B7979);

            imgodcStatus.setVisibility(View.VISIBLE);
            img_odcRequest.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.odContract_linear1:
                try {
                    ODStatusFicciFragment odStatusFicciFragment = new ODStatusFicciFragment();
                    Bundle args = new Bundle();
                    args.putString("OD_TYPE", "ODCONTRACT");
                    odStatusFicciFragment.setArguments(args);
                    transaction.replace(R.id.frame_odContract_ficci_holder, odStatusFicciFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_odcRequest.setTextColor(0xFF5082E6);
                    txt_odcStatus.setTextColor(0xFF7B7979);

                    img_odcRequest.setVisibility(View.VISIBLE);
                    imgodcStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.odContract_linear2:
                try {
                    ODRequestConFicciFragment odRequestConFicciFragment = new ODRequestConFicciFragment();
                    transaction.replace(R.id.frame_odContract_ficci_holder, odRequestConFicciFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_odcStatus.setTextColor(0xFF5082E6);
                    txt_odcRequest.setTextColor(0xFF7B7979);

                    imgodcStatus.setVisibility(View.VISIBLE);
                    img_odcRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }
    }
}
