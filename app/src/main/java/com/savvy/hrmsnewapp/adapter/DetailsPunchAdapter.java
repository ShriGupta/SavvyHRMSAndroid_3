package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 6/23/2017.
 */

public class DetailsPunchAdapter  extends RecyclerView.Adapter<DetailsPunchAdapter.MyViewHolder>{

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    private Context context;
    SharedPreferences shared;
    CoordinatorLayout coordinatorLayout;

    public DetailsPunchAdapter(Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.coordinatorLayout =coordinatorLayout;

    }


    @Override
    public DetailsPunchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.punch_detail_row, parent, false);
        return new DetailsPunchAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(DetailsPunchAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);

        holder.txt_punchDate.setText(mapdata.get("PunchDate"));
        holder.txt_punchTime.setText(mapdata.get("PunchTime"));
        holder.txt_remarks.setText(mapdata.get("Remark"));


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextView txt_date,txt_day_name,txt_time_in,txt_time_out,txt_worked_hours,txt_status;
        public CustomTextView txt_punchDate,txt_punchTime,txt_remarks;
        LinearLayout cal_layout;
        public MyViewHolder(View view) {
            super(view);
            txt_punchDate = view.findViewById(R.id.punch_date_detail);
            txt_punchTime = view.findViewById(R.id.punch_time_detail);
            txt_remarks = view.findViewById(R.id.punch_remark);

        }
    }

}