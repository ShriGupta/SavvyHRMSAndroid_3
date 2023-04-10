package com.savvy.hrmsnewapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReimbursementStatusAdapter extends RecyclerView.Adapter<ReimbursementStatusAdapter.MyViewHolder> {

    List<HashMap<String, String>> reArrayList = new ArrayList<>();
    ItemClickListener itemClickListener;
    public ReimbursementStatusAdapter(ItemClickListener itemClickListener) {
        this.itemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_reimbursement_status_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = reArrayList.get(position);
        holder.tvTokenNo.setText(hashMap.get("TOKEN_NO"));
        holder.tvReimType.setText(hashMap.get("RC_REIMBURSEMENT_NAME"));
        holder.tvMonth.setText(hashMap.get("RR_REIMBURSEMENT_REQUEST_MONTH_NAME"));
        holder.tvYear.setText(hashMap.get("RR_REIMBURSEMENT_REQUEST_YEAR"));
        holder.tvStatus.setText(hashMap.get("REQUEST_STATUS"));
        holder.tvNTaxAmount.setText(hashMap.get("RR_NON_TAXABALE_AMOUNT"));
        holder.tvTaxAmount.setText(hashMap.get("RR_TAXABALE_AMOUNT"));
        holder.tvReason.setText(hashMap.get("RR_REMARKS"));
        holder.tvPullBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClickItem(position,hashMap.get("ERFS_REQUEST_ID"));
            }
        });

    }

    public void clearItems() {
        reArrayList.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<HashMap<String, String>> list) {
        reArrayList.clear();
        reArrayList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvTokenNo, tvReimType, tvMonth, tvYear, tvStatus, tvNTaxAmount, tvTaxAmount, tvReason, tvPullBack, tvCancel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTokenNo = itemView.findViewById(R.id.tv_token_no);
            tvReimType = itemView.findViewById(R.id.tv_reim_type);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvNTaxAmount = itemView.findViewById(R.id.tv_n_text_amount);
            tvTaxAmount = itemView.findViewById(R.id.tv_tax_amount);
            tvReason = itemView.findViewById(R.id.tv_reason);
            tvPullBack = itemView.findViewById(R.id.tv_pull_back);
        }
    }
}
