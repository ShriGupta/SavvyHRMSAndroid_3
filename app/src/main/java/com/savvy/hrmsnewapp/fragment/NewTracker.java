package com.savvy.hrmsnewapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.Gps1Activity;


public class NewTracker extends BaseFragment {

    Button btn_submit;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_new_tracker,container, false);

        btn_submit= rootView.findViewById(R.id.btn_submit);

         btn_submit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 Intent i = new Intent(getContext(),Gps1Activity.class);
                 startActivity(i);
             }
         });

        return rootView;
    }

}
