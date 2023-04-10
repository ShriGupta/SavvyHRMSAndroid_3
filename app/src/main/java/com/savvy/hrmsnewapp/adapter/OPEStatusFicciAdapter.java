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


public class OPEStatusFicciAdapter extends RecyclerView.Adapter<OPEStatusFicciAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> data;
    LayoutInflater inflater;
    Context context;
    ArrayList<Integer> arrayList;
    CoordinatorLayout coordinatorLayout;
    public CustomTextView txt_odclear, txt_odPullback;


    public OPEStatusFicciAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.coordinatorLayout = coordinatorLayout;
    }

    @Override

    public OPEStatusFicciAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ope_statusficci_row, parent, false);
        return new OPEStatusFicciAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OPEStatusFicciAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = data.get(position);

        holder.txt_punchToken_no.setText(mapdata.get("TOKEN_NO"));
        holder.ope_InTime.setText(mapdata.get("INTIME"));
        holder.ope_OutTime.setText(mapdata.get("OUTIME"));
        holder.opeDepartmentName.setText(mapdata.get("DEPARTMENT_NAME"));
        holder.opeAttendanceDate.setText(mapdata.get("ATTENDANCE_DATE"));
        holder.opeTotalHours.setText(mapdata.get("TOTALOPE"));
        holder.opeAmount.setText(mapdata.get("TOTALAMOUNT"));
        holder.opeStatus.setText(mapdata.get("STATUS"));
        holder.opeComments.setText(mapdata.get("COMMENT"));


        holder.txt_actionValuePull.setText("Pull Back");

        String ope_Status = mapdata.get("OR_STATUS_1");
        if (ope_Status.equals("0")) {
            txt_odPullback.setVisibility(View.VISIBLE);
        } else if (ope_Status.equals("2")) {
            txt_odPullback.setVisibility(View.INVISIBLE);
        } else {
            txt_odPullback.setVisibility(View.INVISIBLE);
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView txt_punchToken_no, ope_InTime, ope_OutTime, opeComments;
        public CustomTextView opeDepartmentName, opeAttendanceDate, opeTotalHours, opeStatus, opeAmount;
        CustomTextView txt_actionValuePull;

        public MyViewHolder(View view) {
            super(view);
            txt_punchToken_no = view.findViewById(R.id.tv_opeToken_value);
            ope_InTime = view.findViewById(R.id.ope_InTime);
            ope_OutTime = view.findViewById(R.id.ope_OutTime);
            opeDepartmentName = view.findViewById(R.id.opeDepartmentName);
            opeAttendanceDate = view.findViewById(R.id.opeAttendanceDate);
            opeTotalHours = view.findViewById(R.id.opeTotalHours);
            opeComments = view.findViewById(R.id.opeComments);

            opeStatus = view.findViewById(R.id.opeStatus);
            opeAmount = view.findViewById(R.id.opeAmount);

            txt_actionValuePull = view.findViewById(R.id.tv_pullback_ope);
            txt_odPullback = view.findViewById(R.id.tv_pullback_ope);
            txt_actionValuePull.setOnClickListener(this);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
