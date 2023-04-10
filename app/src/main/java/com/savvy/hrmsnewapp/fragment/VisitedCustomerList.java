package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;

public class VisitedCustomerList extends BaseFragment implements View.OnClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visited_customer_list, container, false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }




    @Override
    public void onClick(View v) {

    }
}

