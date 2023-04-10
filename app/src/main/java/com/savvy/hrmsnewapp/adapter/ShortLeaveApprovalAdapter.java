package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShortLeaveApprovalAdapter extends RecyclerView.Adapter<ShortLeaveApprovalAdapter.MyViewHolder> {

    Context context;
    List<HashMap<String, String>> arrayList=new ArrayList<>();
    ItemClickListener onItemClickListener;

    public ShortLeaveApprovalAdapter(Context context,ItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener=onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_short_leave_approval, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HashMap<String, String> map = arrayList.get(position);
        holder.tvEmpCode.setText(map.get("EMPLOYEE_CODE"));
        holder.tvReqDate.setText(map.get("REQUESTED_DATE"));
        holder.tvSLType.setText(map.get("TYPE"));
        holder.tvType.setText(map.get("R_TYPE"));
        holder.tvSLDate.setText(map.get("SLR_DATE"));
        holder.tvReason.setText(map.get("SLR_REASON"));
        holder.tvToken.setText(map.get("TOKEN_NO"));
        holder.tvEmpName.setText(map.get("EMPLOYEE_NAME"));
        holder.tvAction.setOnClickListener(view -> {
            onItemClickListener.onClickItem(position,"");
        });
    }

    public void clearItems() {
        arrayList.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<HashMap<String, String>> list) {
        arrayList.clear();
        arrayList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvEmpCode, tvReqDate, tvSLType, tvType, tvEmpName, tvSLDate, tvReason, tvToken, tvAction;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmpCode = itemView.findViewById(R.id.tv_emp_code);
            tvReqDate = itemView.findViewById(R.id.tv_request_date);
            tvSLType = itemView.findViewById(R.id.tv_sl_tpye);
            tvType = itemView.findViewById(R.id.tv_type);
            tvEmpName = itemView.findViewById(R.id.tv_emp_name);
            tvSLDate = itemView.findViewById(R.id.tv_sl_date);
            tvReason = itemView.findViewById(R.id.tv_reason);
            tvToken = itemView.findViewById(R.id.tv_token_no);
            tvAction = itemView.findViewById(R.id.tv_action);

        }
    }
}
