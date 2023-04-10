package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class OPEApprovalFicciAdapter extends RecyclerView.Adapter<OPEApprovalFicciAdapter.MyView_Holder> {

    Context context;
    CustomArraySpinnerAdapter customArraySpinnerAdapter;
    CoordinatorLayout coordinatorLayout;
    ArrayList<HashMap<String, String>> arrayListData;
    HashMap<Integer, Integer> actionMap = new HashMap<>();
    TreeMap<Integer, Boolean> checkboxMap = new TreeMap<>();
    HashMap<Integer, String> dataMap = new HashMap<>();
    StringBuilder stringBuilder;
    List list;

    public OPEApprovalFicciAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> arrayListData, List list) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.arrayListData = arrayListData;
        this.list = list;
    }

    @Override
    public OPEApprovalFicciAdapter.MyView_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OPEApprovalFicciAdapter.MyView_Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ope_approvalficci_row, parent, false));
    }

    @Override
    public void onBindViewHolder(OPEApprovalFicciAdapter.MyView_Holder holder, int position) {

        HashMap<String, String> mapData = arrayListData.get(position);
        holder.txt_employeeName.setText(mapData.get("EMPLOYEE_NAME"));
        holder.txt_department.setText(mapData.get("D_DEPARTMENT_NAME"));
        holder.txt_attendanceDate.setText(mapData.get("OR_ATTENDANCE_DATE"));
        holder.txt_inTime.setText(mapData.get("OR_INTIME"));
        holder.txt_outTime.setText(mapData.get("OR_OUTIME"));
        holder.txt_opeHours.setText(mapData.get("hrs"));
        holder.txt_amount.setText(mapData.get("OR_TOTALAMOUNT"));
        holder.txt_status.setText(mapData.get("ERFS_REQUEST_STATUS_NAME"));
        holder.txt_Comments.setText(mapData.get("OR_COMMENT"));

        dataMap.put(position, mapData.get("ERFS_REQUEST_ID") + "," +
                mapData.get("REQUEST_STATUS_ID") + "," +
                mapData.get(("ERFS_ACTION_LEVEL_SEQUENCE")) + "," +
                mapData.get("MAX_ACTION_LEVEL_SEQUENCE") + "," +
                mapData.get("OR_STATUS") + "," +
                mapData.get("OR_EMPLOYEE_ID") + "," +
                mapData.get("ERFS_REQUEST_FLOW_ID")

        );
    }

    public class MyView_Holder extends RecyclerView.ViewHolder {
        CustomTextView txt_employeeName, txt_department, txt_attendanceDate, txt_inTime, txt_outTime, txt_opeHours, txt_amount, txt_status, txt_Comments;
        Spinner actionSpinner;
        Button btn_proceed;
        CheckBox approvalCheckbox;

        MyView_Holder(View view) {
            super(view);
            txt_employeeName = view.findViewById(R.id.txt_employeeName);
            txt_department = view.findViewById(R.id.txt_department);
            txt_attendanceDate = view.findViewById(R.id.txt_attendanceDate);
            txt_inTime = view.findViewById(R.id.txt_inTime);
            txt_outTime = view.findViewById(R.id.txt_outTime);
            txt_opeHours = view.findViewById(R.id.txt_opeHours);
            txt_amount = view.findViewById(R.id.txt_amount);
            txt_status = view.findViewById(R.id.txt_status);
            txt_Comments = view.findViewById(R.id.txt_Comments);
            approvalCheckbox = view.findViewById(R.id.approvalCheckbox);
            actionSpinner = view.findViewById(R.id.actionButton);
            btn_proceed = view.findViewById(R.id.btn_proceed);

            customArraySpinnerAdapter = new CustomArraySpinnerAdapter(context, list);
            actionSpinner.setAdapter(customArraySpinnerAdapter);

            actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position > 0) {
                        if (!actionMap.containsKey(getAdapterPosition())) {
                            actionMap.put(getAdapterPosition(), position);
                        } else {
                            actionMap.replace(getAdapterPosition(), position);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            approvalCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (approvalCheckbox.isChecked()) {
                        checkboxMap.put(getAdapterPosition(), isChecked);

                    } else {
                        checkboxMap.remove(getAdapterPosition());
                    }
                }
            });
        }
    }

    public String getxmlData() {
        stringBuilder = new StringBuilder();
        String result = "";

        if (checkboxMap.size() == 0) {
            return "NoCheckBox Selected";
        } else if (checkboxMap.size() > 0) {
            for (int adapterKey : checkboxMap.keySet()) {
                if (actionMap.get(adapterKey) != null && !(actionMap.get(adapterKey) == 0)) {
                    int position = actionMap.get(adapterKey);
                    stringBuilder.append("@")
                            .append(dataMap.get(adapterKey))
                            .append(",")
                            .append(position);
                } else {
                    return "dropdown";
                }
            }
            return stringBuilder.toString();
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
