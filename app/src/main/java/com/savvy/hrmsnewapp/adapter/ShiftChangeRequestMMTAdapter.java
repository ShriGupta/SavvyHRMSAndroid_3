package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;


public class ShiftChangeRequestMMTAdapter extends RecyclerView.Adapter<ShiftChangeRequestMMTAdapter.MyviewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList, spinnerArrayList;
    StringBuilder stringBuilder;
    HashMap<Integer, String> shiftIdData = new HashMap<>();
    String shiftName;

    public ShiftChangeRequestMMTAdapter(Context context, ArrayList<HashMap<String, String>> arrayList, ArrayList<HashMap<String, String>> spinnerArrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.spinnerArrayList = spinnerArrayList;
    }

    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shift_change_request_mmt_row, parent, false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyviewHolder holder, int position) {
        HashMap<String, String> mapdata = arrayList.get(position);
        holder.srNo.setText(String.valueOf(position + 1));
        holder.attendanceDate.setText(mapdata.get("ATT_DATE"));
        holder.dayName.setText(mapdata.get("DAY_NAME"));
        shiftIdData.put(position, mapdata.get("SHIFT_ID"));

        shiftName = mapdata.get("SHIFT_ID").equals("0") ? "Weekly Off" : mapdata.get("SHIFT_NAME");

        CustomCitySpinnerAdapter customCitySpinnerAdapter = new CustomCitySpinnerAdapter(context, spinnerArrayList, shiftName);
        holder.spinner.setAdapter(customCitySpinnerAdapter);
    }


    public class MyviewHolder extends RecyclerView.ViewHolder {

        CustomTextView srNo, attendanceDate, dayName;
        Spinner spinner;

        public MyviewHolder(View itemView) {
            super(itemView);

            srNo = itemView.findViewById(R.id.scrSrNo);
            attendanceDate = itemView.findViewById(R.id.scrAttendanceDate);
            dayName = itemView.findViewById(R.id.scrDayName);
            spinner = itemView.findViewById(R.id.scrShiftTimingSpinner);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position > 0) {
                        shiftIdData.replace(getAdapterPosition(), spinnerArrayList.get(position - 1).get("KEY"));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public String getXMLStringData() {
        stringBuilder = new StringBuilder();
        int weeklyOff = 0;
        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                stringBuilder.append("@").append(arrayList.get(i).get("ATT_DATE")).append(",").
                        append(arrayList.get(i).get("SHIFT_ID")).append(",").
                        append(shiftIdData.get(i));

                if (shiftIdData.get(i).equals("0")) {
                    weeklyOff++;
                }
            }
            return stringBuilder.toString() + "#" + (weeklyOff >= 3 ? "true" : "false");
        } else {
            return "";
        }
    }
}
