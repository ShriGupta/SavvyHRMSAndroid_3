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

public class CompOffHolderFragment extends BaseFragment implements View.OnClickListener{
    LinearLayout ll_comRequest,ll_comStatus;
    ImageView img_comRequest,imgcomStatus;
    CustomTextView txt_comRequest,txt_comStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //counter = savedInstanceState.getInt("Counter");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_comp_off_holder, container, false);

        ll_comRequest = view.findViewById(R.id.comp_off_linear2);
        ll_comStatus = view.findViewById(R.id.comp_off_linear1);
        img_comRequest = view.findViewById(R.id.comp_off_line_status1);
        imgcomStatus= view.findViewById(R.id.comp_off_line_status);

        txt_comRequest = view.findViewById(R.id.comp_off_tab2);
        txt_comStatus = view.findViewById(R.id.comp_off_tab1);

        ll_comStatus.setOnClickListener(this);
        ll_comRequest.setOnClickListener(this);

        try {

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            CompOffRequestFragment compOffRequestFragment = new CompOffRequestFragment();
            transaction.replace(R.id.frame_comp_off_holder, compOffRequestFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            txt_comRequest.setTextColor(0xFF5082E6);
            txt_comStatus.setTextColor(0xFF7B7979);

            imgcomStatus.setVisibility(View.GONE);
            img_comRequest.setVisibility(View.VISIBLE);
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
            case R.id.comp_off_linear1:
                try {
                    CompOffStatusFragment compOffStatusFragment = new CompOffStatusFragment();
                    transaction.replace(R.id.frame_comp_off_holder, compOffStatusFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_comStatus.setTextColor(0xFF5082E6);
                    txt_comRequest.setTextColor(0xFF7B7979);

                    imgcomStatus.setVisibility(View.VISIBLE);
                    img_comRequest.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.comp_off_linear2:
                try{
                    CompOffRequestFragment compOffRequestFragment = new CompOffRequestFragment();
                    transaction.replace(R.id.frame_comp_off_holder, compOffRequestFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_comRequest.setTextColor(0xFF5082E6);
                    txt_comStatus.setTextColor(0xFF7B7979);

                    imgcomStatus.setVisibility(View.GONE);
                    img_comRequest.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }
}

