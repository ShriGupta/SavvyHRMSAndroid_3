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
import java.util.List;

public class LeaveStatusFicciAdapter extends RecyclerView.Adapter<LeaveStatusFicciAdapter.MyViewHolder> {

    List<HashMap<String, String>> data;
    public LayoutInflater inflater;
    public Context context;
    ArrayList<Integer> arrayList;
    CustomTextView txt_applyCancel, txt_applyValue;

    public LeaveStatusFicciAdapter(Context context, List<HashMap<String, String>> data, ArrayList<Integer> arrayList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.arrayList = arrayList;
    }


    @Override
    public LeaveStatusFicciAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_status_ficci_row, parent, false);

        return new LeaveStatusFicciAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(LeaveStatusFicciAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = data.get(position);
        holder.txt_leavenameValue.setText(mapdata.get("LM_LEAVE_NAME"));
        holder.txv_tokenNoValue.setText(mapdata.get("TOKEN_NO"));
        holder.txt_fromDateValue.setText(mapdata.get("FROM_DATE"));
        holder.txt_reasonValue.setText(mapdata.get("LR_REASON"));
        holder.txt_statusValue.setText(mapdata.get("REQUEST_STATUS"));
        holder.txt_toDateValue.setText(mapdata.get("TO_DATE"));
        holder.txt_actionByValue.setText(mapdata.get("ACTION_BY"));
        holder.txt_actiondateValue.setText(mapdata.get("ACTION_DATE"));

        String lr_Status = mapdata.get("LR_STATUS");
        if (lr_Status.equals("0")) {
            txt_applyValue.setVisibility(View.VISIBLE);
            txt_applyCancel.setVisibility(View.INVISIBLE);
        } else if (lr_Status.equals("2")) {
            txt_applyValue.setVisibility(View.INVISIBLE);
            txt_applyCancel.setVisibility(View.VISIBLE);
        } else {
            txt_applyValue.setVisibility(View.INVISIBLE);
            txt_applyCancel.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CustomTextView txt_leavenameValue, txv_tokenNoValue, txt_fromDateValue, txt_reasonValue, txt_statusValue, txt_toDateValue, txt_actionByValue, txt_actiondateValue;

        public MyViewHolder(View view) {
            super(view);
            txt_leavenameValue = view.findViewById(R.id.tv_leaveNameFicciValue);
            txv_tokenNoValue = view.findViewById(R.id.tv_leaveTokenNumber);
            txt_fromDateValue = view.findViewById(R.id.tv_leaveFromDate);
            txt_reasonValue = view.findViewById(R.id.leaveStatusFicciReason);
            txt_statusValue = view.findViewById(R.id.leaveStatusFicci);
            txt_toDateValue = view.findViewById(R.id.leaveStatusFicci_toDate);
            txt_actionByValue = view.findViewById(R.id.tv_leaveFicciActionBy);
            txt_actiondateValue = view.findViewById(R.id.leaveStatusActionDate);

            txt_applyValue = view.findViewById(R.id.tv_pullback_Ficci);
            txt_applyCancel = view.findViewById(R.id.tv_cancel_Ficci);

            txt_applyValue.setOnClickListener(this);
            txt_applyCancel.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}