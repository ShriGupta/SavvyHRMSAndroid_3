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

public class TravelDeskFicciAdapter extends RecyclerView.Adapter<TravelDeskFicciAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public TravelDeskFicciAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_desk_ficci_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapData = arrayList.get(position);

        holder.tokenNo.setText(mapData.get("TOKEN_NO"));
        holder.empCode.setText(mapData.get("REQUEST_BY_EMPLOYEE_CODE"));
        holder.empName.setText(mapData.get("REQUEST_BY_EMPLOYEE_NAME"));
        holder.travelType.setText(mapData.get("TRF_TRAVEL_TYPE"));
        holder.itineraryType.setText(mapData.get("TRF_ITINERARY_TYPE"));
        holder.startDate.setText(mapData.get("TRF_START_DATE_1"));
        holder.endDate.setText(mapData.get("TRF_END_DATE_1"));
        holder.project.setText(mapData.get("TPM_PROJECT_NAME"));
        holder.amount.setText(mapData.get("TRF_BUDGETED_AMOUNT"));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CustomTextView tokenNo, empCode, empName, travelType, itineraryType, startDate, endDate, project, amount;
        Button addTicket;

        public MyViewHolder(View itemView) {
            super(itemView);

            tokenNo = itemView.findViewById(R.id.travelDeskTokenNo);
            empCode = itemView.findViewById(R.id.travelDeskEmpCode);
            empName = itemView.findViewById(R.id.travelDeskEmpName);
            travelType = itemView.findViewById(R.id.travelDeskTravelType);
            itineraryType = itemView.findViewById(R.id.travelDeskItineraryType);
            startDate = itemView.findViewById(R.id.travelDeskStartDate);
            endDate = itemView.findViewById(R.id.travelDeskEndDate);
            project = itemView.findViewById(R.id.travelDeskProjectName);
            amount = itemView.findViewById(R.id.travelDeskBudgetAmount);
            addTicket = itemView.findViewById(R.id.travelDeskAddTicket);
            addTicket.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
