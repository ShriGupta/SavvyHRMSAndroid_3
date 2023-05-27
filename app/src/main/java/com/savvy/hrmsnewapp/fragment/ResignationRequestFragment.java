package com.savvy.hrmsnewapp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.service.Track_Me_Service;

public class ResignationRequestFragment extends BaseFragment {

    Button btn_add1_resignation, btn_cancel;
    Button btn_pickFromDate, btn_pickToDate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resignation_request, container, false);

        btn_add1_resignation = view.findViewById(R.id.btn_add1_resignation);
        btn_pickFromDate = view.findViewById(R.id.pickFromDate);
        btn_pickToDate = view.findViewById(R.id.pickToDate);

        btn_add1_resignation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                String PullBackTitle = "<font color=\"#b52f2f\"><bold>" + "Add Resignation" + "</bold></font>";
                dialog.setTitle(Html.fromHtml(PullBackTitle));
                dialog.setContentView(R.layout.resignation_details_popup);

                dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                btn_cancel = dialog.findViewById(R.id.btn_cancel);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.closeOptionsMenu();
                        dialog.cancel();
                    }
                });



                dialog.show();
            }
        });

        btn_pickFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_pickToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


}
