package com.savvy.hrmsnewapp.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 5/23/2017.
 */

public class AttendanceCalendarAdapter extends RecyclerView.Adapter<AttendanceCalendarAdapter.MyViewHolder> {

    List<HashMap<String, String>> data;
    private LayoutInflater inflater;
    private Context context;
    SharedPreferences shared;
    CoordinatorLayout coordinatorLayout;

    public AttendanceCalendarAdapter(Context context, CoordinatorLayout coordinatorLayout, List<HashMap<String, String>> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.coordinatorLayout = coordinatorLayout;

    }

    @Override
    public AttendanceCalendarAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_cal_row, parent, false);
        return new AttendanceCalendarAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(AttendanceCalendarAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = data.get(position);

        holder.txt_date1.setText(mapdata.get("EAR_ATTENDANCE_DATE"));
        holder.txt_status1.setText(mapdata.get("AA_ATTENDANCE_DESCRIPTION"));
        holder.calendarTimeIn.setText(mapdata.get("EAR_TIME_IN"));
        holder.calendarTimeOut.setText(mapdata.get("EAR_TIME_OUT"));

        String time_in = mapdata.get("EAR_TIME_IN");
        String time_out = mapdata.get("EAR_TIME_OUT");

        if (time_in.equals(""))
            holder.calendarTimeIn.setText("     -");
        if (time_out.equals(""))
            holder.calendarTimeOut.setText("     -");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final CustomTextView calendarTimeIn;
        private final CustomTextView calendarTimeOut;
        public CustomTextView txt_status1, txt_date1;
        LinearLayout cal_layout;

        public MyViewHolder(View view) {
            super(view);

            txt_date1 = view.findViewById(R.id.calendarDate);
            txt_status1 = view.findViewById(R.id.calendarStatus);
            calendarTimeIn = view.findViewById(R.id.calendarTimeIn);
            calendarTimeOut = view.findViewById(R.id.calendarTimeOut);

            cal_layout = view.findViewById(R.id.calendarLayout);
        }
    }

}