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

public class WorkFromHomeStatusAdapter extends RecyclerView.Adapter<WorkFromHomeStatusAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> arrayList;
    Context context;

    public WorkFromHomeStatusAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_from_home_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = arrayList.get(position);

        holder.tokenNo.setText(mapdata.get("TOKEN_NO"));
        holder.fromDate.setText(mapdata.get("FROM_DATE"));
        holder.toDate.setText(mapdata.get("TO_DATE"));
        holder.wfhActionBy.setText(mapdata.get("ACTION_BY"));
        holder.wfhActionDate.setText(mapdata.get("ACTION_DATE"));
        holder.wfhStatus.setText(mapdata.get("REQUEST_STATUS"));
        holder.wfhReason.setText(mapdata.get("WFH_REASON"));
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView tokenNo, fromDate, toDate, wfhReason, wfhStatus, wfhActionBy, wfhActionDate;

        public MyViewHolder(View itemView) {
            super(itemView);

            tokenNo = itemView.findViewById(R.id.wfhStatusToken_value);
            fromDate = itemView.findViewById(R.id.wfhStatus_fromDate);
            toDate = itemView.findViewById(R.id.wfhStatus_To_Date);
            wfhActionBy = itemView.findViewById(R.id.wfhStatus_actionBy);
            wfhActionDate = itemView.findViewById(R.id.wfhStatus_actionDate);
            wfhStatus = itemView.findViewById(R.id.wfhStatus_Status);
            wfhReason = itemView.findViewById(R.id.wfhStatus_Reason);
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
