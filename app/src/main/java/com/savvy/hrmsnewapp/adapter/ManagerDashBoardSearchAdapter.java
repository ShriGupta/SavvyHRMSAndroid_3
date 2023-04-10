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

public class ManagerDashBoardSearchAdapter extends RecyclerView.Adapter<ManagerDashBoardSearchAdapter.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public ManagerDashBoardSearchAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_dashboard_search_adapter_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = arrayList.get(position);
        holder.empCode.setText(mapdata.get("EMPLOYEE_CODE"));
        holder.empName.setText(mapdata.get("EMPLOYEE_NAME"));
        holder.whCategory.setText(mapdata.get("AVG_WORKTIME"));
        holder.avg_In.setText(mapdata.get("AVG_IN_TIME"));
        holder.avg_Out.setText(mapdata.get("AVG_OUT_TIME"));
        holder.leave.setText(mapdata.get("LEAVE"));
        holder.whf.setText(mapdata.get("WFH"));
        holder.od.setText(mapdata.get("OD"));
        holder.avg_WorkingHours.setText(mapdata.get("AVG_WORKED1"));
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView empCode, empName, whCategory, avg_In, avg_Out, leave, whf, od, avg_WorkingHours;

        public MyViewHolder(View itemView) {
            super(itemView);

            empCode = itemView.findViewById(R.id.md_empCode);
            empName = itemView.findViewById(R.id.md_empName);
            whCategory = itemView.findViewById(R.id.md_Working_HoursCategory);
            avg_In = itemView.findViewById(R.id.md_AVGIn);
            avg_Out = itemView.findViewById(R.id.md_AvgOut);
            leave = itemView.findViewById(R.id.md_leave);
            whf = itemView.findViewById(R.id.md_Whf);
            od = itemView.findViewById(R.id.md_Od);
            avg_WorkingHours = itemView.findViewById(R.id.md_AvgWorkingHours);

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}



