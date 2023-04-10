package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class OnDutyRequestMMTAdapter extends RecyclerView.Adapter<OnDutyRequestMMTAdapter.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayList;
    Button txt_PullBack, txt_Cancel;

    public OnDutyRequestMMTAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onduty_request_mmt_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapData = arrayList.get(position);
        holder.odrTokenNo.setText(mapData.get("TOKEN_NO"));
        holder.odrOdType.setText(mapData.get("OD_TYPE_NAME"));
        holder.odrFromDate.setText(mapData.get("FROM_DATE"));
        holder.odrTodate.setText(mapData.get("TO_DATE"));
        holder.odrActionBy.setText(mapData.get("ACTION_BY"));
        holder.odrActionDate.setText(mapData.get("ACTION_DATE"));
        holder.odrLocationsVisit.setText(mapData.get("ODR_LOCATIONS_VISITED"));
        holder.odrStatus.setText(mapData.get("REQUEST_STATUS"));
        holder.odrReason.setText(mapData.get("ODR_REASON"));

        String od_Status = mapData.get("OD_STATUS_1");
        if (od_Status.equals("0")) {
            txt_Cancel.setVisibility(View.INVISIBLE);
            txt_PullBack.setVisibility(View.VISIBLE);
        } else if (od_Status.equals("2")) {
            txt_Cancel.setVisibility(View.VISIBLE);
            txt_PullBack.setVisibility(View.INVISIBLE);
        } else {
            txt_Cancel.setVisibility(View.INVISIBLE);
            txt_PullBack.setVisibility(View.INVISIBLE);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView odrTokenNo, odrOdType, odrFromDate, odrTodate, odrActionBy, odrActionDate, odrLocationsVisit, odrStatus, odrReason;

        public MyViewHolder(View itemView) {
            super(itemView);
            odrTokenNo = itemView.findViewById(R.id.odRTokenNo);
            odrOdType = itemView.findViewById(R.id.odRODType);
            odrFromDate = itemView.findViewById(R.id.odRFromDate);
            odrTodate = itemView.findViewById(R.id.odRToDate);
            odrActionBy = itemView.findViewById(R.id.odRActionBy);
            odrActionDate = itemView.findViewById(R.id.odrActionDate);
            odrLocationsVisit = itemView.findViewById(R.id.odRLocationVisit);
            odrStatus = itemView.findViewById(R.id.odRStatus);
            odrReason = itemView.findViewById(R.id.odRReason);

            txt_PullBack = itemView.findViewById(R.id.odR_Pullback);
            txt_Cancel = itemView.findViewById(R.id.odRCancel);

            txt_PullBack.setOnClickListener(this);
            txt_Cancel.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
