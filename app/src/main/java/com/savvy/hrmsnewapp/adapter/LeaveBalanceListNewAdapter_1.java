package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 8/4/2017.
 */

public class LeaveBalanceListNewAdapter_1 extends RecyclerView.Adapter<LeaveBalanceListNewAdapter_1.MyViewHolder> {

    List<HashMap<String, String>> data;
    Context context;

    TextView applyGreen, applyOrange, insufficient;

    public  LeaveBalanceListNewAdapter_1(Context context, List<HashMap<String, String>> data){
        this.context = context;
        this.data = data;
//        HashMap<String,String> mapdata = new HashMap<String, String>();

    }

    @Override
    public LeaveBalanceListNewAdapter_1.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.balancerow_testing,parent,false);
        return new LeaveBalanceListNewAdapter_1.MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(LeaveBalanceListNewAdapter_1.MyViewHolder holder, int position) {

            HashMap<String,String> mapdata = data.get(position);
            holder.txt_Approved.setText(mapdata.get("APPROVED_LEAVE"));
            holder.txt_leaveName.setText(mapdata.get("LM_ABBREVATION"));
            holder.txt_Balance.setText(mapdata.get("CURRENT_BALANCE"));
            holder.txt_Pending.setText(mapdata.get("PENDING_LEAVE"));
            holder.txt_Running.setText(mapdata.get("RUNNING_BALANCE"));


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txt_leaveName, txt_Approved, txt_Balance, txt_Pending, txt_Running;

        public MyViewHolder(View view) {
            super(view);
//            txt_leaveName = (TextView)view.findViewById(R.id.dtata);

            txt_leaveName = view.findViewById(R.id.txv_leaveNameValue);
            txt_Approved = view.findViewById(R.id.txt_approvedValue);
            txt_Balance = view.findViewById(R.id.txt_balanceValue);
            txt_Pending = view.findViewById(R.id.txt_pendingValue);
            txt_Running = view.findViewById(R.id.txt_runningValue);

//            applyGreen = (TextView)view.findViewById(R.id.txt_applyLeaveValuegreen);
//            applyOrange = (TextView)view.findViewById(R.id.txt_applyLeaveValue);
//            insufficient = (TextView)view.findViewById(R.id.txt_insufficiant);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}