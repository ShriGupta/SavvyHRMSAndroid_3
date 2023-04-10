package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LeaveRequestFicciAdapter extends RecyclerView.Adapter<LeaveRequestFicciAdapter.MyViewHolder> {

    Context context;
    CoordinatorLayout coordinatorLayout;
    ArrayList<HashMap<String, String>> arrayList;

    CustomTextView applyLeaveGreen, applyLeaveOrange, insufficiantRed;

    public LeaveRequestFicciAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.arrayList = arrayList;
    }

    @Override
    public LeaveRequestFicciAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_request_ficci_adapter_row, parent, false);
        return new LeaveRequestFicciAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeaveRequestFicciAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = arrayList.get(position);

        holder.leaveNameValue.setText(hashMap.get("LEAVE_NAME"));
        holder.advanceValue.setText(hashMap.get("ADVANCE_LEAVE"));
        holder.balanceValue.setText(hashMap.get("CURRENT_BALANCE"));
        holder.runningValue.setText(hashMap.get("RUNNING_BALANCE"));
        holder.approvedValue.setText(hashMap.get("APPROVED_LEAVE"));
        holder.lopValue.setText(hashMap.get("LOP_LEAVE"));
        holder.pendingValue.setText(hashMap.get("PENDING_LEAVE"));

        String enable = hashMap.get("ENABLE");

        if (enable.equals("1")) {
            applyLeaveGreen.setVisibility(View.VISIBLE);
            applyLeaveOrange.setVisibility(View.INVISIBLE);
            insufficiantRed.setVisibility(View.INVISIBLE);
        } else if (enable.equals("2")) {
            applyLeaveGreen.setVisibility(View.INVISIBLE);
            applyLeaveOrange.setVisibility(View.VISIBLE);
            insufficiantRed.setVisibility(View.INVISIBLE);
        } else if (enable.equals("0")) {
            applyLeaveGreen.setVisibility(View.INVISIBLE);
            applyLeaveOrange.setVisibility(View.INVISIBLE);
            insufficiantRed.setVisibility(View.VISIBLE);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CustomTextView leaveNameValue, advanceValue, balanceValue, runningValue, approvedValue, lopValue, pendingValue;

        public MyViewHolder(View view) {
            super(view);
            try {

                leaveNameValue = view.findViewById(R.id.txv_leaveName);
                advanceValue = view.findViewById(R.id.txt_advance);
                balanceValue = view.findViewById(R.id.txt_balance);
                runningValue = view.findViewById(R.id.txt_running);
                approvedValue = view.findViewById(R.id.txt_approved);
                lopValue = view.findViewById(R.id.txt_lop);
                pendingValue = view.findViewById(R.id.txt_pending);

                applyLeaveGreen = view.findViewById(R.id.txt_applyLeaveGreen);
                applyLeaveOrange = view.findViewById(R.id.txt_applyLeaveOrange);
                insufficiantRed = view.findViewById(R.id.txt_insufficiantRed);

                view.setOnClickListener(this);
                applyLeaveGreen.setOnClickListener(this);
                applyLeaveOrange.setOnClickListener(this);


            } catch (Exception e) {

            }
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
