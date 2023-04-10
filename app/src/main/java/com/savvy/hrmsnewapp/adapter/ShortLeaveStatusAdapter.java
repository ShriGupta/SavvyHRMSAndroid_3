package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ShortLeaveStatusAdapter extends RecyclerView.Adapter<ShortLeaveStatusAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public ShortLeaveStatusAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.short_leave_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = arrayList.get(position);
        holder.tv_sl_TokenNumber.setText(hashMap.get("TOKEN_NO"));
        holder.tv_sl_Date.setText(hashMap.get("SLR_DATE"));
        holder.tv_sl_Type.setText(hashMap.get("SLC_LEAVE_TYPE"));
        holder.tv_type.setText(hashMap.get("TYPE"));
        holder.tv_sl_ActionDate.setText(hashMap.get("ACTION_DATE"));
        holder.tv_sl_ActionBy.setText(hashMap.get("ACTION_BY"));
        holder.tv_sl_Status.setText(hashMap.get("REQUEST_STATUS"));
        holder.tv_sl_Reason.setText(hashMap.get("SLR_REASON"));

        String slr_status_1 = hashMap.get("SLR_STATUS_1");
        assert slr_status_1 != null;
        if (slr_status_1.equals("0")) {
            holder.tv_sl_PullBack.setVisibility(View.VISIBLE);
        } else if (slr_status_1.equals("2")) {
            holder.tv_sl_PullBack.setVisibility(View.INVISIBLE);
        } else {
            holder.tv_sl_PullBack.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CustomTextView tv_sl_TokenNumber, tv_sl_Date, tv_sl_Type, tv_type, tv_sl_Status,
                tv_sl_ActionDate, tv_sl_ActionBy, tv_sl_Reason, tv_sl_PullBack;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_sl_TokenNumber = itemView.findViewById(R.id.tv_sl_TokenNumber);
            tv_sl_Date = itemView.findViewById(R.id.tv_sl_Date);
            tv_sl_Type = itemView.findViewById(R.id.tv_sl_Type);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_sl_Status = itemView.findViewById(R.id.tv_sl_Status);
            tv_sl_ActionDate = itemView.findViewById(R.id.tv_sl_ActionDate);
            tv_sl_ActionBy = itemView.findViewById(R.id.tv_sl_ActionBy);
            tv_sl_Reason = itemView.findViewById(R.id.tv_sl_Reason);
            tv_sl_PullBack = itemView.findViewById(R.id.tv_sl_PullBack);
            tv_sl_PullBack.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
