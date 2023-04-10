package com.savvy.hrmsnewapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.model.GetReimbursementRequestDetailResult;

import java.util.ArrayList;
import java.util.List;

public class ReimbursementApprovalAdapter extends RecyclerView.Adapter<ReimbursementApprovalAdapter.MyViewHolder> {

    List<GetReimbursementRequestDetailResult> arrayList = new ArrayList<>();
    ArrayList<Integer> integerArrayList = new ArrayList<>();

    public ReimbursementApprovalAdapter() {

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_reimbursement_approval_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GetReimbursementRequestDetailResult model = arrayList.get(position);
        holder.tvToken.setText(model.getTokenNo());
        holder.tvEmpCode.setText(model.getEmployeeCode());
        holder.tvEmpName.setText(model.getEmployeeName());
        holder.tvTaxAmt.setText(model.getRrTaxabaleAmount());
        holder.tvNonTaxAmt.setText(model.getRrNonTaxabaleAmount());
        holder.tvType.setText(model.getReimbursementStatus());
        holder.tvAppTaxAmt.setText(model.getRrApprovedTaxabaleAmount());
        holder.tvAppNonTaxAmt.setText(model.getRrApprovedNonTaxabaleAmount());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    integerArrayList.add(position);
                } else {
                    integerArrayList.remove(Integer.valueOf(position));
                }
                Log.e("TAG", "onCheckedChanged: " + integerArrayList);
            }
        });
    }

    public void addItems(List<GetReimbursementRequestDetailResult> list) {
        arrayList.clear();
        arrayList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvToken, tvEmpCode, tvEmpName, tvTaxAmt, tvNonTaxAmt, tvType, tvAppTaxAmt, tvAppNonTaxAmt;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvToken = itemView.findViewById(R.id.tv_tr_no);
            tvEmpCode = itemView.findViewById(R.id.tv_ecode);
            tvEmpName = itemView.findViewById(R.id.tv_emp_name);
            tvTaxAmt = itemView.findViewById(R.id.tv_tax_amt);
            tvNonTaxAmt = itemView.findViewById(R.id.tv_non_tax_amt);
            tvType = itemView.findViewById(R.id.tv_type);
            tvAppTaxAmt = itemView.findViewById(R.id.tv_app_tax_amt);
            tvAppNonTaxAmt = itemView.findViewById(R.id.tv_app_non_tax_amt);
            checkBox = itemView.findViewById(R.id.ch_approval_checkbox);
        }
    }

    public String XMLString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (integerArrayList.size() > 0) {
            for (int key : integerArrayList)
                stringBuilder.append("@").append(arrayList.get(key).getErfsRequestId()).append(",")
                        .append(arrayList.get(key).getRequestStatusId()).append(",")
                        .append(arrayList.get(key).getErfsActionLevelSequence()).append(",")
                        .append(arrayList.get(key).getMaxActionLevelSequence()).append(",")
                        .append(arrayList.get(key).getReimbursementStatus()).append(",")
                        .append(arrayList.get(key).getRrEmployeeId()).append(",")
                        .append(arrayList.get(key).getErfsRequestFlowId()).append(",")
                        .append(arrayList.get(key).getRrApprovedNonTaxabaleAmount()).append(",")
                        .append(arrayList.get(key).getRrApprovedTaxabaleAmount());
        }
        return stringBuilder.toString();
    }
}
