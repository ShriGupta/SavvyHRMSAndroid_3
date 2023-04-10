package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

public class CustomerVisitInOutAdapter extends RecyclerView.Adapter<CustomerVisitInOutAdapter.MyViewHolder> {

    Context context;
    List<HashMap<String, String>> arrayList;

    public CustomerVisitInOutAdapter(Context context, List<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public CustomerVisitInOutAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_visit_inout_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerVisitInOutAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = arrayList.get(position);
        holder.customerName.setText(hashMap.get("CUSTOMER_NAME"));
        holder.punchinDateTime.setText(hashMap.get("PUNCHIN_DATETIME"));
        holder.punchoutDateTime.setText(hashMap.get("PUNCHOUT_DATETIME"));
        holder.punchinRemarks.setText(hashMap.get("PUNCHIN_REMARK"));
        holder.punchoutRemarks.setText(hashMap.get("PUNCHOUT_REMARKS"));
        holder.locationaddressIn.setText(hashMap.get("LOCATION_ADDRESS_IN"));
        holder.locationaddressOut.setText(hashMap.get("LOCATION_ADDRESS_OUT"));
        try {
            if (hashMap.get("LOCATION_ADDRESS_OUT").equals("") && hashMap.get("PUNCHOUT_DATETIME").equals("") && hashMap.get("PUNCHOUT_REMARKS").equals("")) {
                holder.visitOutDateTimeLinearLayout.setVisibility(View.GONE);
                holder.visitOutLocationLinearLayout.setVisibility(View.GONE);
                holder.visitOutRemarks.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView customerName, punchinDateTime, punchoutDateTime, punchinRemarks, punchoutRemarks, locationaddressIn, locationaddressOut;
        LinearLayout visitOutDateTimeLinearLayout, visitOutLocationLinearLayout, visitOutRemarks;

        public MyViewHolder(View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.customerName);
            punchinDateTime = itemView.findViewById(R.id.punchinDateTime);
            punchoutDateTime = itemView.findViewById(R.id.punchoutDateTime);
            punchinRemarks = itemView.findViewById(R.id.punchinRemarks);
            punchoutRemarks = itemView.findViewById(R.id.punchoutRemarks);
            locationaddressIn = itemView.findViewById(R.id.locationAddressIn);
            locationaddressOut = itemView.findViewById(R.id.locationAddressOut);
            visitOutDateTimeLinearLayout = itemView.findViewById(R.id.visitOutDateTimeLinearLayout);
            visitOutLocationLinearLayout = itemView.findViewById(R.id.visitOutLocationLinearLayout);
            visitOutRemarks = itemView.findViewById(R.id.visitOutRemarks);
        }

    }
}
