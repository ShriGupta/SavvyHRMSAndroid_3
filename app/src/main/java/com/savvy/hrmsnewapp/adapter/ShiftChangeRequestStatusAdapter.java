package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ShiftChangeRequestStatusAdapter extends RecyclerView.Adapter<ShiftChangeRequestStatusAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public ShiftChangeRequestStatusAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shift_change_request_mmt_status_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = arrayList.get(position);

        holder.scrTokenNo.setText(mapdata.get("TOKEN_NO"));
        holder.scrFromDate.setText(mapdata.get("FROM_DATE"));
        holder.scrToDate.setText(mapdata.get("TO_DATE"));
        holder.scrStatus.setText(mapdata.get("REQUEST_STATUS"));
        holder.scrActionBy.setText(mapdata.get("ACTION_BY"));
        holder.scrActionDate.setText(mapdata.get("ACTION_DATE"));
        holder.scrRemarks.setText(mapdata.get("SCR_REASON"));

        String status = arrayList.get(position).get("SCR_STATUS_1");
        if (status.equals("0")) {
            holder.scrPullBack.setVisibility(View.VISIBLE);
        } else {
            holder.scrPullBack.setVisibility(View.GONE);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CustomTextView scrTokenNo, scrStatus, scrFromDate, scrToDate, scrActionBy, scrActionDate, scrRemarks;
        Button scrPullBack;

        public MyViewHolder(View itemView) {
            super(itemView);
            scrTokenNo = itemView.findViewById(R.id.scrStatusTokenNo);
            scrStatus = itemView.findViewById(R.id.scrStatus_Status);
            scrFromDate = itemView.findViewById(R.id.scrstatusFromDate);
            scrToDate = itemView.findViewById(R.id.scrToDate);
            scrActionBy = itemView.findViewById(R.id.scrStatusActionBy);
            scrActionDate = itemView.findViewById(R.id.scrActionDate);
            scrRemarks = itemView.findViewById(R.id.scrStatusRemarks);
            scrPullBack = itemView.findViewById(R.id.scrPullBack);

            scrPullBack.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
