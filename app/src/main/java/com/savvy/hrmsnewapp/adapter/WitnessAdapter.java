package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class WitnessAdapter extends RecyclerView.Adapter<WitnessAdapter.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public WitnessAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.witness_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        HashMap<String,String> hashMap=arrayList.get(position);
        holder.employee_Code.setText(hashMap.get(""));

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CustomTextView employee_Code;
        Button delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

        employee_Code=itemView.findViewById(R.id.employee_Code);
        delete=itemView.findViewById(R.id.witness_deleteButton);
        delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
