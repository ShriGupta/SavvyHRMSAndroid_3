package com.savvy.hrmsnewapp.fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class CustomerVisit_GOIP extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_CustomerRequest,ll_CustomerStatus;
    ImageView img_CustomerRequest,imgCustomerStatus;
    CustomTextView txt_CustomerRequest,txt_CustomerStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //counter = savedInstanceState.getInt("Counter");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view= inflater.inflate(R.layout.activity_customer_visit__goip, container, false);

        ll_CustomerRequest = view.findViewById(R.id.Customer_linear1);
        ll_CustomerStatus = view.findViewById(R.id.Customer_linear2);
        img_CustomerRequest = view.findViewById(R.id.Customer_status);
        imgCustomerStatus = view.findViewById(R.id.Customer_status1);

        txt_CustomerRequest = view.findViewById(R.id.Customer_tab1);
        txt_CustomerStatus = view.findViewById(R.id.Customer_tab2);

        ll_CustomerStatus.setOnClickListener(this);
        ll_CustomerRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            VisitedCustomerList visitedCustomerList = new VisitedCustomerList();
            transaction.replace(R.id.frame_Customer_holder, visitedCustomerList);
            transaction.addToBackStack(null);
            transaction.commit();
            txt_CustomerStatus.setTextColor(0xFF5082E6);
            txt_CustomerRequest.setTextColor(0xFF7B7979);

            imgCustomerStatus.setVisibility(View.VISIBLE);
            img_CustomerRequest.setVisibility(View.GONE);
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
            case R.id.Customer_linear1:
                try {
                    NewCustomerVisit newCustomerVisit = new NewCustomerVisit();
                    transaction.replace(R.id.frame_Customer_holder, newCustomerVisit);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_CustomerRequest.setTextColor(0xFF5082E6);
                    txt_CustomerStatus.setTextColor(0xFF7B7979);

                    img_CustomerRequest.setVisibility(View.VISIBLE);
                    imgCustomerStatus.setVisibility(View.GONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.Customer_linear2:
                try {
                    VisitedCustomerList visitedCustomerList = new VisitedCustomerList();
                    transaction.replace(R.id.frame_Customer_holder, visitedCustomerList);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    txt_CustomerStatus.setTextColor(0xFF5082E6);
                    txt_CustomerRequest.setTextColor(0xFF7B7979);

                    imgCustomerStatus.setVisibility(View.VISIBLE);
                    img_CustomerRequest.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }
}
