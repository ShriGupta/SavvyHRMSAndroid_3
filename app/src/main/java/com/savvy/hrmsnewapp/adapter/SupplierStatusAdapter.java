package com.savvy.hrmsnewapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;

import java.util.HashMap;
import java.util.List;

public class SupplierStatusAdapter extends RecyclerView.Adapter<SupplierStatusAdapter.MyViewHolder> {

    List<HashMap<String,String>> supplierList;
    public SupplierStatusAdapter(List<HashMap<String,String>> list){
        this.supplierList=list;
    }

    @NonNull
    @Override
    public SupplierStatusAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_status_adapter,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierStatusAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> h=supplierList.get(position);

        holder.supName.setText(h.get("SM_SUPPLIER_NAME"));
        holder.activityName.setText(h.get("AM_ACTIVITY_NAME"));
        holder.tvLocation.setText(h.get("SLD_LOCATION"));
        holder.tvLocationType.setText(h.get("SLD_LOCATION_TYPE"));
        holder.tvMeetingType.setText(h.get("TPCICO_MEETING_TYPE"));
        holder.tvWorkType.setText(h.get("TPCICO_WORK_TYPE"));
        holder.tvCharge.setText(h.get("TPCICO_CHARGES"));
        holder.tvToll.setText(h.get("TPCICO_TOLL"));
        holder.tvCheckIn.setText(h.get("TPCICO_CHECK_IN_TIME"));
        holder.tvCheckOut.setText(h.get("TPCICO_CHECK_OUT_TIME"));
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView supName,activityName,tvLocation,tvLocationType,tvMeetingType,tvWorkType,tvCharge,tvToll,tvCheckIn,tvCheckOut;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            supName=itemView.findViewById(R.id.supplier_name_tv);
            activityName=itemView.findViewById(R.id.activity_name_tv);
            tvLocation=itemView.findViewById(R.id.location_tv);
            tvLocationType=itemView.findViewById(R.id.location_type_tv);
            tvMeetingType=itemView.findViewById(R.id.meeting_type_tv);
            tvWorkType=itemView.findViewById(R.id.work_type_tv);
            tvCharge=itemView.findViewById(R.id.charges_type_tv);
            tvToll=itemView.findViewById(R.id.toll_tv);
            tvCheckIn=itemView.findViewById(R.id.check_in_time_tv);
            tvCheckOut=itemView.findViewById(R.id.check_out_time);
        }
    }
}
