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

public class LeaveRequestStatusMMTAdapter extends RecyclerView.Adapter<LeaveRequestStatusMMTAdapter.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayList;
    Button lRequest_PullBack;

    public LeaveRequestStatusMMTAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_request_status_mmt_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = arrayList.get(position);
        holder.lrStatus_TokenNo.setText(hashMap.get("TOKEN_NO"));
        holder.lRequest_fromDate.setText(hashMap.get("FROM_DATE"));
        holder.lRequest_actionBy.setText(hashMap.get("ACTION_BY"));
        holder.lRequest_Reason.setText(hashMap.get("LR_REASON"));
        holder.lRequest_leaveName.setText(hashMap.get("LM_LEAVE_NAME"));
        holder.lRequest_toDate.setText(hashMap.get("TO_DATE"));
        holder.lRequest_actionDate.setText(hashMap.get("ACTION_DATE"));
        holder.lRequest_status.setText(hashMap.get("REQUEST_STATUS"));

        String od_Status = hashMap.get("LR_STATUS");
        if (od_Status.equals("0")) {
            lRequest_PullBack.setVisibility(View.VISIBLE);
        } else {
            lRequest_PullBack.setVisibility(View.INVISIBLE);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView lrStatus_TokenNo, lRequest_fromDate, lRequest_actionBy, lRequest_Reason, lRequest_leaveName, lRequest_toDate, lRequest_actionDate, lRequest_status;

        public MyViewHolder(View itemView) {
            super(itemView);
            lrStatus_TokenNo = itemView.findViewById(R.id.lrStatus_TokenNo);
            lRequest_fromDate = itemView.findViewById(R.id.lRequest_fromDate);
            lRequest_actionBy = itemView.findViewById(R.id.lRequest_actionBy);
            lRequest_Reason = itemView.findViewById(R.id.lRequest_Reason);
            lRequest_leaveName = itemView.findViewById(R.id.lRequest_leaveName);
            lRequest_toDate = itemView.findViewById(R.id.lRequest_toDate);
            lRequest_actionDate = itemView.findViewById(R.id.lRequest_actionDate);
            lRequest_status = itemView.findViewById(R.id.lRequest_status);
            lRequest_PullBack = itemView.findViewById(R.id.lRequest_PullBack);

            lRequest_PullBack.setOnClickListener(this);
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
