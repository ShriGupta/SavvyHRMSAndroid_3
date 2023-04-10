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

public class VoucherFragmentHolder extends BaseFragment implements View.OnClickListener{

    LinearLayout ll_voucherRequest,ll_voucherStatus;
    ImageView img_voucherRequest,img_voucherStatus;
    CustomTextView txt_voucherRequest,txt_voucherStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voucher_fragment_holder, container, false);

        ll_voucherRequest = view.findViewById(R.id.voucher_linear1);
        ll_voucherStatus = view.findViewById(R.id.voucher_linear2);
        img_voucherRequest = view.findViewById(R.id.line_status);
        img_voucherStatus = view.findViewById(R.id.line_status1);

        txt_voucherRequest = view.findViewById(R.id.voucher_tab1);
        txt_voucherStatus = view.findViewById(R.id.voucher_tab2);

        ll_voucherStatus.setOnClickListener(this);
        ll_voucherRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            VoucherRequestRequest voucherRequestRequest = new VoucherRequestRequest();
            transaction.replace(R.id.frame_voucher_holder, voucherRequestRequest);
            transaction.addToBackStack(null);
            transaction.commit();

            txt_voucherStatus.setTextColor(0xFF5082E6);
            txt_voucherRequest.setTextColor(0xFF7B7979);

            img_voucherStatus.setVisibility(View.VISIBLE);
            img_voucherRequest.setVisibility(View.GONE);

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
            case R.id.voucher_linear1:
                try {
                    VoucherStatusFragment voucherStatusFragment = new VoucherStatusFragment();
                    transaction.replace(R.id.frame_voucher_holder, voucherStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_voucherRequest.setTextColor(0xFF5082E6);
                    txt_voucherStatus.setTextColor(0xFF7B7979);

                    img_voucherRequest.setVisibility(View.VISIBLE);
                    img_voucherStatus.setVisibility(View.GONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.voucher_linear2:
                try {
                    VoucherRequestRequest voucherRequestRequest = new VoucherRequestRequest();
                    transaction.replace(R.id.frame_voucher_holder, voucherRequestRequest);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_voucherStatus.setTextColor(0xFF5082E6);
                    txt_voucherRequest.setTextColor(0xFF7B7979);

                    img_voucherStatus.setVisibility(View.VISIBLE);
                    img_voucherRequest.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }

}
