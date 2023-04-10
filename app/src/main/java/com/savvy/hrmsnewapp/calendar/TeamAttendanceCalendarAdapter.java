package com.savvy.hrmsnewapp.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 5/23/2017.
 */

public class TeamAttendanceCalendarAdapter extends RecyclerView.Adapter<TeamAttendanceCalendarAdapter.MyViewHolder>{

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    private Context context;
    SharedPreferences shared;
    CoordinatorLayout coordinatorLayout;

    public TeamAttendanceCalendarAdapter(Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.coordinatorLayout =coordinatorLayout;

    }


    @Override
    public TeamAttendanceCalendarAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.team_attendance_calendar_row, parent, false);
        return new TeamAttendanceCalendarAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(TeamAttendanceCalendarAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);
        //Movie movie = moviesList.get(position);
//
        String time_in = mapdata.get("EAR_TIME_IN");
        String time_out = mapdata.get("EAR_TIME_OUT");
        String worked_hours = mapdata.get("TOTAL_HOURS");

        holder.txt_date.setText(mapdata.get("DISPLAY_ATTENDANCE_DATE"));
        holder.txt_day_name.setText(mapdata.get("DAY_NAME"));
        holder.txt_time_in.setText(mapdata.get("EAR_TIME_IN"));
        holder.txt_time_out.setText(mapdata.get("EAR_TIME_OUT"));
        holder.txt_worked_hours.setText(mapdata.get("TOTAL_HOURS"));
        holder.txt_status.setText(mapdata.get("AA_ATTENDANCE_DESCRIPTION"));

        if(time_in.equals(""))
            holder.txt_time_in.setText("     -");
        if(time_out.equals(""))
            holder.txt_time_out.setText("     -");
        if(worked_hours.equals(""))
            holder.txt_worked_hours.setText("     -");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextView txt_date,txt_day_name,txt_time_in,txt_time_out,txt_worked_hours,txt_status;

        public MyViewHolder(View view) {
            super(view);
            txt_date = view.findViewById(R.id.att_cal_date);
            txt_day_name = view.findViewById(R.id.att_cal_day);
            txt_time_in = view.findViewById(R.id.att_cal_time_in);
            txt_time_out = view.findViewById(R.id.att_cal_time_out);
            txt_worked_hours = view.findViewById(R.id.att_cal_worked_hours);
            txt_status = view.findViewById(R.id.att_cal_status);
        }
    }

}