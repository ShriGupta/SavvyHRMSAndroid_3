package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TravelStatusFicciListAdapter extends RecyclerView.Adapter<TravelStatusFicciListAdapter.MyViewHolder> {
    Context context;
    CoordinatorLayout coordinatorLayoutl;
    ArrayList<HashMap<String, String>> arrayList;
    CustomTextView txt_odPullback, addTrip, txt_statusCancel,txt_statusAddViewReport;

    public TravelStatusFicciListAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.coordinatorLayoutl = coordinatorLayout;
        this.arrayList = arrayList;
    }

    @Override
    public TravelStatusFicciListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_status_ficci_row, parent, false);
        return new TravelStatusFicciListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TravelStatusFicciListAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> map = arrayList.get(position);

        holder.txt_travelNumber.setText(map.get("TOKEN_NO"));
        holder.txt_Status.setText(map.get("REQUEST_STATUS"));
        holder.txt_statusStartDate.setText(map.get("TRF_START_DATE"));
        holder.txt_statusEndDate.setText(map.get("TRF_END_DATE"));
        holder.txt_projectName.setText(map.get("TPM_PROJECT_NAME"));
        holder.txt_travelType.setText(map.get("TRF_TRAVEL_TYPE"));
        holder.txt_StatusReason.setText(map.get("TRF_TRAVEL_REASON"));
        holder.txt_actionBy.setText(map.get("ACTION_BY"));

        int te_Status = Integer.valueOf(map.get("TRF_STATUS"));
        int voucherStatus = Integer.valueOf(map.get("TVRD_TRAVEL_VOUCHER_REQUEST_ID"));
        String TRF_TRAVEL_TRIP_REPORT = map.get("TRF_TRAVEL_TRIP_REPORT");
        if (te_Status == 0) {
            txt_odPullback.setVisibility(View.VISIBLE);
        } else if ((voucherStatus == 0 && te_Status == 2) || (voucherStatus == 0 && te_Status == 1)) {
            txt_odPullback.setVisibility(View.INVISIBLE);
            txt_statusCancel.setVisibility(View.VISIBLE);
        } else {
            txt_odPullback.setVisibility(View.INVISIBLE);
            txt_statusCancel.setVisibility(View.INVISIBLE);
        }

        if (te_Status == 2 && voucherStatus == 0 && TRF_TRAVEL_TRIP_REPORT.equals("False")) {
            addTrip.setVisibility(View.VISIBLE);
        }else if(TRF_TRAVEL_TRIP_REPORT.equals("True"))
        {
            txt_statusAddViewReport.setVisibility(View.VISIBLE);
        }else {
            txt_statusAddViewReport.setVisibility(View.GONE);
            addTrip.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView txt_travelNumber, txt_Status, txt_statusStartDate, txt_statusEndDate, txt_projectName, txt_travelType, txt_StatusReason, txt_actionBy;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt_travelNumber = itemView.findViewById(R.id.txt_travelNumber);
            txt_Status = itemView.findViewById(R.id.txt_Status);
            txt_statusStartDate = itemView.findViewById(R.id.txt_statusStartDate);
            txt_statusEndDate = itemView.findViewById(R.id.txt_statusEndDate);
            txt_projectName = itemView.findViewById(R.id.txt_projectName);
            txt_travelType = itemView.findViewById(R.id.txt_travelType);
            txt_StatusReason = itemView.findViewById(R.id.txt_StatusReason);
            txt_actionBy = itemView.findViewById(R.id.txt_actionBy);

            txt_odPullback = itemView.findViewById(R.id.txt_statusPullBack);
            addTrip = itemView.findViewById(R.id.txt_statusAddTrip);
            txt_statusCancel = itemView.findViewById(R.id.txt_statusCancel);
            txt_statusAddViewReport = itemView.findViewById(R.id.txt_statusAddViewReport);
            addTrip.setOnClickListener(this);
            txt_odPullback.setOnClickListener(this);
            txt_statusCancel.setOnClickListener(this);
            txt_statusAddViewReport.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

        }
    }
}
