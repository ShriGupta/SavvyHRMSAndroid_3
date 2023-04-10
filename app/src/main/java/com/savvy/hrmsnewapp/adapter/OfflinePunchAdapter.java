package com.savvy.hrmsnewapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.room_database.OfflinePunchInModel;

import java.util.ArrayList;
import java.util.List;

public class OfflinePunchAdapter extends RecyclerView.Adapter<OfflinePunchAdapter.MyViewHolder> {

    List<OfflinePunchInModel> list = new ArrayList<>();

    public OfflinePunchAdapter() {

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_offline_punch_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OfflinePunchInModel model = list.get(position);
        holder.tvPunchDate.setText(model.getCurrentdate());
        holder.tvPunchTime.setText(model.getCurrenttime());
        holder.tvComment.setText(model.getComment());
    }

    public void clearItems() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<OfflinePunchInModel> arrayList) {
        list.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPunchDate, tvPunchTime, tvComment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPunchDate = itemView.findViewById(R.id.tv_punch_date);
            tvPunchTime = itemView.findViewById(R.id.tv_punch_time);
            tvComment = itemView.findViewById(R.id.tv_comment);
        }
    }
}
