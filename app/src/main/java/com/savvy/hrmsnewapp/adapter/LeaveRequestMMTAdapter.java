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

public class LeaveRequestMMTAdapter extends RecyclerView.Adapter<LeaveRequestMMTAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public LeaveRequestMMTAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_request_mmt_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = arrayList.get(position);
        holder.leaveBalance.setText(hashMap.get("EAR_ATTENDANCE_DATE"));
        holder.lr_status.setText(hashMap.get("EAR_ATTENDANCE_STATUS"));
        holder.lr_deductionType.setText(hashMap.get("DEDUCTION"));
        holder.lr_decuctionDays.setText(hashMap.get("DEDUCTION_DAYS"));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView leaveBalance, lr_status, lr_deductionType, lr_decuctionDays;

        public MyViewHolder(View itemView) {
            super(itemView);

            leaveBalance = itemView.findViewById(R.id.lr_balance);
            lr_status = itemView.findViewById(R.id.lr_status);
            lr_deductionType = itemView.findViewById(R.id.lr_deduction_type);
            lr_decuctionDays = itemView.findViewById(R.id.lr_deduction_days);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
