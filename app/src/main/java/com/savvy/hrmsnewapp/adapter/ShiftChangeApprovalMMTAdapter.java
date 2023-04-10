package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


public class ShiftChangeApprovalMMTAdapter extends RecyclerView.Adapter<ShiftChangeApprovalMMTAdapter.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayList;
    TreeMap<Integer, String> mapData = new TreeMap<>();
    StringBuilder stringBuilder;

    public ShiftChangeApprovalMMTAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ShiftChangeApprovalMMTAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shift_change_approval_mmt_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShiftChangeApprovalMMTAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = arrayList.get(position);
        holder.tokenNo.setText(hashMap.get("TOKEN_NO"));
        holder.empCode.setText(hashMap.get("EMPLOYEE_CODE"));
        holder.empName.setText(hashMap.get("EMPLOYEE_NAME"));
        holder.fromDate.setText(hashMap.get("FROM_DATE"));
        holder.toDate.setText(hashMap.get("TO_DATE"));
        holder.type.setText(hashMap.get("R_TYPE"));
        holder.reason.setText(hashMap.get("SCR_REASON"));
        holder.approvalCheckBoxMMT.setChecked(false);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tokenNo, empCode, empName, fromDate, toDate, type, reason;
        CheckBox approvalCheckBoxMMT;

        public MyViewHolder(View itemView) {
            super(itemView);
            tokenNo = itemView.findViewById(R.id.SCA_TokenNo);
            empCode = itemView.findViewById(R.id.SCA_EmpCode);
            empName = itemView.findViewById(R.id.SCA_EmpName);
            fromDate = itemView.findViewById(R.id.SCA_FromDate);
            toDate = itemView.findViewById(R.id.SCA_ToDate);
            type = itemView.findViewById(R.id.SCA_Type);
            reason = itemView.findViewById(R.id.SCA_Reason);
            approvalCheckBoxMMT = itemView.findViewById(R.id.approvalCheckBoxMMT);

            approvalCheckBoxMMT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        saveXMLData(getAdapterPosition());
                    } else {
                        mapData.remove(getAdapterPosition());
                    }
                }
            });
        }

        private void saveXMLData(int adapterPosition) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("@").
                    append(arrayList.get(adapterPosition).get("ERFS_REQUEST_ID")).append(",").
                    append(arrayList.get(adapterPosition).get("REQUEST_STATUS_ID")).append(",").
                    append(arrayList.get(adapterPosition).get("ERFS_ACTION_LEVEL_SEQUENCE")).append(",").
                    append(arrayList.get(adapterPosition).get("MAX_ACTION_LEVEL_SEQUENCE")).append(",").
                    append(arrayList.get(adapterPosition).get("SCR_STATUS").equals("null") ? "" : arrayList.get(adapterPosition).get("SCR_STATUS")).append(",").
                    append(arrayList.get(adapterPosition).get("SCR_EMPLOYEE_ID")).append(",").
                    append(arrayList.get(adapterPosition).get("ERFS_REQUEST_FLOW_ID"));
            mapData.put(adapterPosition, stringBuilder.toString());
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public String getXMLData() {
        stringBuilder = new StringBuilder();
        if (mapData.size() > 0) {
            for (int key : mapData.keySet()) {
                stringBuilder.append(mapData.get(key));
            }
            return stringBuilder.toString();
        } else {
            return "";
        }

    }
}


