package com.savvy.hrmsnewapp.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CompOffStatusRequestMMTAdapter extends RecyclerView.Adapter<CompOffStatusRequestMMTAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public CompOffStatusRequestMMTAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.compoff_status_request_mmt_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapData = arrayList.get(position);
        holder.tokenNo.setText(mapData.get("TOKEN_NO"));
        holder.compOffDate.setText(mapData.get("COMP_OFF_DATE"));
        holder.compOffReason.setText(mapData.get("COR_REASON_COMMENT"));
        holder.requestedDays.setText(mapData.get("COR_REQUEST_DAYS"));
        holder.ApprovedDays.setText(mapData.get("COR_APPROVE_DAYS"));
        holder.compOffStatus.setText(mapData.get("REQUEST_STATUS"));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tokenNo, compOffDate, compOffReason, requestedDays, ApprovedDays, compOffStatus;

        public MyViewHolder(View itemView) {
            super(itemView);

            tokenNo = itemView.findViewById(R.id.tv_coffToken_value);
            compOffDate = itemView.findViewById(R.id.coffDate);
            compOffReason = itemView.findViewById(R.id.coffReason);
            requestedDays = itemView.findViewById(R.id.coffRequestedDays);
            ApprovedDays = itemView.findViewById(R.id.coffApprovedDays);
            compOffStatus = itemView.findViewById(R.id.coffStatus);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
