package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LeaveApplyFicciAdapter extends RecyclerView.Adapter<LeaveApplyFicciAdapter.MyViewHolder> {

    Context context;
    CoordinatorLayout coordinatorLayout;
    ArrayList<HashMap<String, String>> arrayList;
    public static String spin_value;


    public LeaveApplyFicciAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.arrayList = arrayList;

    }

    @Override
    public LeaveApplyFicciAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LeaveApplyFicciAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_apply_ficci_row, parent, false));
    }

    @Override
    public void onBindViewHolder(LeaveApplyFicciAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = arrayList.get(position);
        holder.leaveDate.setText(hashMap.get("EAR_ATTENDANCE_DATE"));
        holder.leave_Status.setText(hashMap.get("EAR_ATTENDANCE_STATUS"));

        spin_value = hashMap.get("DEDUCTION");
        Log.e("Spin Value", "" + spin_value);
        holder.showMenu.setText(spin_value);
        holder.deductionValue.setText(hashMap.get("DEDUCTION_DAYS"));

    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener, View.OnClickListener, MenuItem.OnMenuItemClickListener {
        CustomTextView leaveDate, leave_Status, deductionValue;
        Button showMenu;

        public MyViewHolder(View view) {
            super(view);
            leaveDate = view.findViewById(R.id.leaveDate);
            leave_Status = view.findViewById(R.id.leaveStatus);
            showMenu = view.findViewById(R.id.deductionSpinner);
            deductionValue = view.findViewById(R.id.deductionValue);
            view.setOnClickListener(this);
            showMenu.setOnClickListener(this);

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return false;
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
