package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ODContractListAdapter extends RecyclerView.Adapter<ODContractListAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public ODContractListAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }


    @Override
    public ODContractListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.odc_contract_list_adapter_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ODContractListAdapter.MyViewHolder holder, int position) {

        HashMap<String, String> mapdata = arrayList.get(position);
        holder.odc_date.setText(mapdata.get("ODRD_DATE"));
        holder.odc_Time_in.setText(mapdata.get("ODRD_TIME_IN"));
        holder.odc_Time_out.setText(mapdata.get("ODRD_TIME_OUT"));

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView odc_date,odc_Time_in,odc_Time_out;
        public MyViewHolder(View itemView) {
            super(itemView);

            odc_date=itemView.findViewById(R.id.odc_Date);
            odc_Time_in=itemView.findViewById(R.id.odc_TimeIn);
            odc_Time_out=itemView.findViewById(R.id.odc_TimeOut);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
