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

public class AttCalendarMMTAdapter extends RecyclerView.Adapter<AttCalendarMMTAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public AttCalendarMMTAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public AttCalendarMMTAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.att_calendar_mmt_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttCalendarMMTAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> madata = arrayList.get(position);
        holder.attDate.setText(madata.get("EAR_ATTENDANCE_DATE"));
        holder.attTimeIn.setText(madata.get("EAR_TIME_IN"));
        holder.attWorkedHours.setText(madata.get("TOTAL_HOURS"));
        holder.attDayName.setText(madata.get("DAY_NAME"));
        holder.attTimeOut.setText(madata.get("EAR_TIME_OUT"));
        holder.attStatus.setText(madata.get("AA_ATTENDANCE_DESCRIPTION"));

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView attDate, attDayName, attTimeIn, attTimeOut, attWorkedHours, attStatus;

        public MyViewHolder(View view) {
            super(view);
            attDate = view.findViewById(R.id.attCalendar_Date);
            attTimeIn = view.findViewById(R.id.attCalendar_TimeIn);
            attWorkedHours = view.findViewById(R.id.attCalendar_WorkedHours);
            attDayName = view.findViewById(R.id.attCalendar_DayName);
            attTimeOut = view.findViewById(R.id.attCalendar_TimeOut);
            attStatus = view.findViewById(R.id.attCalendar_Status);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
