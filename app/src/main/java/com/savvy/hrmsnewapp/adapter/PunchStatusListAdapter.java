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
 * Created by orapc7 on 5/13/2017.
 */

public class PunchStatusListAdapter extends RecyclerView.Adapter<PunchStatusListAdapter.MyViewHolder>{

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    private Context context;
    SharedPreferences shared;
    ArrayList<Integer> arrayList;
    CoordinatorLayout coordinatorLayout;
    CustomTextView txt_punchToken_no,txt_actionValuePull,txt_actionValueCancel;

    public PunchStatusListAdapter(Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data,ArrayList<Integer> arrayList) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.arrayList = arrayList;
        this.coordinatorLayout =coordinatorLayout;

    }


    @Override
    public PunchStatusListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.punch_status_row, parent, false);
        return new PunchStatusListAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(PunchStatusListAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);
        //Movie movie = moviesList.get(position);
//        for(int k = 0;k<data.size();k++) {
//            HashMap<String, String> mapdata = data.get(k);
//            if (arrayList.get(position) == Integer.parseInt(mapdata.get("TOKEN_NO"))) {
                holder.txt_punchToken_no.setText(mapdata.get("TOKEN_NO"));
                holder.txt_punchdate_value.setText(mapdata.get("PUNCH_DATE"));
                holder.txt_punchtime_value.setText(mapdata.get("PUNCH_TIME"));
                holder.txt_punchreason_value.setText(mapdata.get("REASON"));
                holder.txt_punchstatus_value.setText(mapdata.get("REQUEST_STATUS"));
                holder.txt_punchactionby_value.setText(mapdata.get("REQUESTED_BY"));
                holder.txt_punchactiondate_value.setText(mapdata.get("REQUESTED_DATE"));

                String punch_status_1 = mapdata.get("PUNCH_STATUS_1");
                if (punch_status_1.equals("0")) {
                    txt_actionValueCancel.setVisibility(View.INVISIBLE);
                    txt_actionValuePull.setVisibility(View.VISIBLE);
                } else if (punch_status_1.equals("2")) {
                    txt_actionValueCancel.setVisibility(View.VISIBLE);
                    txt_actionValuePull.setVisibility(View.INVISIBLE);
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

        public CustomTextView txt_punchToken_no, txt_punchdate_value, txt_punchtime_value, txt_punchreason_value, txt_punchstatus_value;
        public CustomTextView txt_punchactionby_value, txt_punchactiondate_value, txt_testData;//,txt_punchclear,txt_punchPullback;

        public MyViewHolder(View view) {
            super(view);
            txt_punchToken_no = view.findViewById(R.id.txt_puchToken_value);
            txt_punchdate_value = view.findViewById(R.id.txt_puchDate_value);
            txt_punchtime_value = view.findViewById(R.id.txt_puchTime_value);
            txt_punchreason_value = view.findViewById(R.id.txt_puchReason_value);
            txt_punchstatus_value = view.findViewById(R.id.txt_status_value);
            txt_punchactionby_value = view.findViewById(R.id.txt_actionBy_value);
            txt_punchactiondate_value = view.findViewById(R.id.txt_actionDate_value);

//            txt_actionValuePull = (CustomTextView) view.findViewById(R.id.txt_pullback);
//            txt_actionValueCancel = (CustomTextView) view.findViewById(R.id.txt_cancel);
            txt_actionValuePull = view.findViewById(R.id.txt_pullback);
            txt_actionValueCancel = view.findViewById(R.id.txt_cancel);
            txt_testData = view.findViewById(R.id.txt_puchDate_value);

            view.setOnClickListener(this);
            txt_actionValuePull.setOnClickListener(this);
            txt_actionValueCancel.setOnClickListener(this);
            txt_testData.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }

}