package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;


public class LeaveEncashmentApprovalAdapter extends RecyclerView.Adapter<LeaveEncashmentApprovalAdapter.MyViewHolder> {

    CoordinatorLayout coordinatorLayout;
    Context context;
    ArrayList<HashMap<String, String>> arrayList;
    Button actionButton;

    public LeaveEncashmentApprovalAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.arrayList = arrayList;
    }

    @Override
    public LeaveEncashmentApprovalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_encashment_approval_row, parent, false);
        return new LeaveEncashmentApprovalAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeaveEncashmentApprovalAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = arrayList.get(position);
        holder.tokenNo.setText(hashMap.get("TOKEN_NO"));
        holder.empCode.setText(hashMap.get("EMPLOYEE_CODE"));
        holder.empName.setText(hashMap.get("EMPLOYEE_NAME"));
        holder.leaveType.setText(hashMap.get("LER_LEAVE_TYPE_ID"));
        holder.requestEncashDays.setText(hashMap.get("LER_APPROVED_NO_OF_DAYS"));
        holder.appEcncashDays.setText(hashMap.get("LER_NO_OF_DAYS"));
        holder.tv_Reson.setText(hashMap.get("LER_REASON"));

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView tokenNo, empCode, empName, leaveType, requestEncashDays, appEcncashDays, tv_Reson;


        public MyViewHolder(View itemView) {
            super(itemView);

            tokenNo = itemView.findViewById(R.id.tv_tokenNumber);
            empCode = itemView.findViewById(R.id.tv_empcode);
            empName = itemView.findViewById(R.id.tv_empName);
            leaveType = itemView.findViewById(R.id.tv_leaveType);
            requestEncashDays = itemView.findViewById(R.id.tv_requestEncashDays);
            appEcncashDays = itemView.findViewById(R.id.tv_appEncashDays);
            tv_Reson = itemView.findViewById(R.id.tv_Reason);

            actionButton = itemView.findViewById(R.id.btn_Action);
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
