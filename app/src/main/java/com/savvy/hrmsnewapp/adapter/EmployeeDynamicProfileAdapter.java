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

public class EmployeeDynamicProfileAdapter extends RecyclerView.Adapter<EmployeeDynamicProfileAdapter.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> dynamicArrayList;

    public EmployeeDynamicProfileAdapter(Context context, ArrayList<HashMap<String, String>> dynamicArrayList) {
        this.context = context;
        this.dynamicArrayList = dynamicArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_dynamic_profile_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = dynamicArrayList.get(position);
            holder.tv_captionName.setText(hashMap.get("CaptionKey"));
            holder.tv_captionValue.setText(hashMap.get("CaptionValue"));
        }


    @Override
    public int getItemCount() {
        return dynamicArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView tv_captionName, tv_captionValue;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_captionName = itemView.findViewById(R.id.tv_captionName);
            tv_captionValue = itemView.findViewById(R.id.tv_captionValue);
        }
    }
}
