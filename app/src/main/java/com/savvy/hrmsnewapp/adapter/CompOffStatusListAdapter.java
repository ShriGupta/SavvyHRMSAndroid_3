package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 5/20/2017.
 */

public class CompOffStatusListAdapter extends RecyclerView.Adapter<CompOffStatusListAdapter.MyViewHolder>{

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    private Context context;
    SharedPreferences shared;
    ArrayList<Integer> arrayList;
    CoordinatorLayout coordinatorLayout;
    public CustomTextView txt_actionValuePull, txt_actionValueCancel;

    public CompOffStatusListAdapter(Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data,ArrayList<Integer> arrayList) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.arrayList = arrayList;
        this.coordinatorLayout =coordinatorLayout;

    }


    @Override
    public CompOffStatusListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comp_off_status_row, parent, false);
        return new CompOffStatusListAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(CompOffStatusListAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);
        //Movie movie = moviesList.get(position);
//        for(int k = 0;k<data.size();k++) {
//            HashMap<String, String> mapdata = data.get(k);
//            if (arrayList.get(position) == Integer.parseInt(mapdata.get("TOKEN_NO"))) {
                holder.txt_CorToken_no.setText(mapdata.get("TOKEN_NO"));
                holder.txt_cor_date.setText(mapdata.get("COMP_OFF_DATE"));
                holder.txt_cor_time_in.setText(mapdata.get("TIME_IN"));
                holder.txt_cor_time_out.setText(mapdata.get("TIME_OUT"));
                holder.txt_cor_reason.setText(mapdata.get("REASON"));
                holder.txt_cor_requested_days.setText(mapdata.get("COR_REQUEST_DAYS"));
                holder.txt_cor_status.setText(mapdata.get("REQUEST_STATUS"));
                holder.txt_cor_approved_days.setText(mapdata.get("COR_APPROVE_DAYS"));

        String comp_off_status = mapdata.get("COMP_OFF_STATUS_1");
        if (comp_off_status.equals("0")) {
            txt_actionValueCancel.setVisibility(View.INVISIBLE);
            txt_actionValuePull.setVisibility(View.VISIBLE);
        } else {
            txt_actionValueCancel.setVisibility(View.INVISIBLE);
            txt_actionValuePull.setVisibility(View.INVISIBLE);
        }
//            }
//        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView txt_CorToken_no, txt_cor_date, txt_cor_time_in, txt_cor_time_out, txt_cor_reason;
        public CustomTextView txt_cor_requested_days, txt_cor_status,txt_cor_approved_days;//,txt_punchclear,txt_punchPullback;


        public MyViewHolder(View view) {
            super(view);
            txt_CorToken_no = view.findViewById(R.id.cor_Token_value);
            txt_cor_date = view.findViewById(R.id.txt_corDate_value);
            txt_cor_time_in = view.findViewById(R.id.txt_corTimeIn_value);
            txt_cor_time_out = view.findViewById(R.id.txt_corTimeOut_value);
            txt_cor_reason = view.findViewById(R.id.txt_cor_reason);
            txt_cor_requested_days = view.findViewById(R.id.txt_cor_requestedDays);
            txt_cor_status = view.findViewById(R.id.txt_cor_status);
            txt_cor_approved_days = view.findViewById(R.id.cor_approved_days);

            txt_actionValuePull = view.findViewById(R.id.txt_cor_pullback);
            txt_actionValueCancel = view.findViewById(R.id.txt_cor_cancel);

            view.setOnClickListener(this);
            txt_actionValuePull.setOnClickListener(this);
            txt_actionValueCancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

}