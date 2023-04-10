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

public class ODHolderFicciFragment extends BaseFragment implements View.OnClickListener {
    LinearLayout ll_odRequest, ll_odStatus;
    ImageView img_odRequest, imgodStatus;
    CustomTextView txt_odRequest, txt_odStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //counter = savedInstanceState.getInt("Counter");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_odholder_ficci, container, false);

        ll_odRequest = view.findViewById(R.id.od_linear1);
        ll_odStatus = view.findViewById(R.id.od_linear2);
        img_odRequest = view.findViewById(R.id.line_status);
        imgodStatus = view.findViewById(R.id.line_status1);

        txt_odRequest = view.findViewById(R.id.od_tab1);
        txt_odStatus = view.findViewById(R.id.od_tab2);

        ll_odStatus.setOnClickListener(this);
        ll_odRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            OD_RequestFicciFragment odRequestFragment = new OD_RequestFicciFragment();
            transaction.replace(R.id.frame_od_ficci_holder, odRequestFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_odStatus.setTextColor(0xFF5082E6);
            txt_odRequest.setTextColor(0xFF7B7979);

            imgodStatus.setVisibility(View.VISIBLE);
            img_odRequest.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.od_linear1:
                try {
                    ODStatusFicciFragment odStatusFicciFragment = new ODStatusFicciFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("OD_TYPE", "");
                    odStatusFicciFragment.setArguments(bundle);
                    transaction.replace(R.id.frame_od_ficci_holder, odStatusFicciFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_odRequest.setTextColor(0xFF5082E6);
                    txt_odStatus.setTextColor(0xFF7B7979);

                    img_odRequest.setVisibility(View.VISIBLE);
                    imgodStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.od_linear2:
                try {
                    OD_RequestFicciFragment odRequestFragment = new OD_RequestFicciFragment();
                    transaction.replace(R.id.frame_od_ficci_holder, odRequestFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_odStatus.setTextColor(0xFF5082E6);
                    txt_odRequest.setTextColor(0xFF7B7979);

                    imgodStatus.setVisibility(View.VISIBLE);
                    img_odRequest.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }
}
