package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LeaveRequestCoListAdapter extends RecyclerView.Adapter<LeaveRequestCoListAdapter.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayList;
    HashMap<Integer, String> uncheckedListData;
    Boolean value;

    public LeaveRequestCoListAdapter(Context context, ArrayList<HashMap<String, String>> arrayList, Boolean value) {
        this.context = context;
        this.arrayList = arrayList;
        this.value = value;
    }

    public LeaveRequestCoListAdapter(ArrayList<HashMap<String, String>> arrayList, HashMap<Integer, String> uncheckedListData) {
        this.arrayList = arrayList;
        this.uncheckedListData = uncheckedListData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_request_co_list_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> hashmap = arrayList.get(position);
        holder.coDate.setText(hashmap.get("COR_COMPOFF_DATE"));
        holder.coDays.setText(hashmap.get("DAYS"));
        holder.coDayName.setText(hashmap.get("DAY_NAME"));

        if (uncheckedListData != null) {
            if (uncheckedListData.containsKey(position)) {
                holder.coCheckBox.setChecked(true);
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        CustomTextView coDate, coDays, coDayName;
        CheckBox coCheckBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            coCheckBox = itemView.findViewById(R.id.coCheckBox);
            coDate = itemView.findViewById(R.id.coDate);
            coDays = itemView.findViewById(R.id.co_Days);
            coDayName = itemView.findViewById(R.id.co_daysName);
            coCheckBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
