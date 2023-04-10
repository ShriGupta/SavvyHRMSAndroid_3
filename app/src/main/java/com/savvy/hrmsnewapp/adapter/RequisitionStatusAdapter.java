package com.savvy.hrmsnewapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;
import com.savvy.hrmsnewapp.model.RequisitionStatusModel;

import java.util.ArrayList;

public class RequisitionStatusAdapter extends RecyclerView.Adapter<RequisitionStatusAdapter.MyViewHolder> {
    ArrayList<RequisitionStatusModel> arrayList = new ArrayList<>();
    ItemClickListener itemClickListener;
    public RequisitionStatusAdapter(ItemClickListener itemClickListener) {
        this.itemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    public RequisitionStatusAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_requsition_status_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequisitionStatusAdapter.MyViewHolder holder, int position) {

        RequisitionStatusModel model=arrayList.get(position);
        holder.tvDepName.setText(model.getDepartmentName());
        holder.tvRequDate.setText(model.getRrwRequisitionDate1());
        holder.tvShiftName.setText(model.getShiftNameWithStatus());
        holder.tvStatus.setText(model.getRrwStatus());
        holder.tvAppStatus.setText(model.getRrwApprovalStatus1());
        holder.btnDelete.setOnClickListener(view -> itemClickListener.onClickItem(position,""));
    }

    public void addItems(ArrayList<RequisitionStatusModel> list) {
        arrayList.clear();
        arrayList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDepName, tvRequDate, tvShiftName, tvStatus, tvAppStatus;
        Button btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepName = itemView.findViewById(R.id.tv_dep_name);
            tvRequDate = itemView.findViewById(R.id.tv_requ_date);
            tvShiftName = itemView.findViewById(R.id.tv_shift_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvAppStatus = itemView.findViewById(R.id.tv_app_status);
            btnDelete = itemView.findViewById(R.id.btn_Requ_delete);
        }
    }
}
