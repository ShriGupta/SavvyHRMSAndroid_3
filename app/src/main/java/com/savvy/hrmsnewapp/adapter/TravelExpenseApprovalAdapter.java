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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TravelExpenseApprovalAdapter extends RecyclerView.Adapter<TravelExpenseApprovalAdapter.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayList;
    StringBuilder stringBuilder;
    TreeMap<Integer, Boolean> checkboxStatus = new TreeMap<>();
    Map<Integer, String> mapData = new HashMap<>();
    List<Integer> adapterPosition;

    public TravelExpenseApprovalAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_expense_approval_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = arrayList.get(position);

        holder.requestNo.setText(mapdata.get("TOKEN_NO"));
        holder.eCode.setText(mapdata.get("EMPLOYEE_CODE"));
        holder.empName.setText(mapdata.get("EMPLOYEE_NAME"));
        holder.requestedAmount.setText(mapdata.get("TVR_REQUESTED_AMOUNT"));
        holder.approvedAmount.setText(mapdata.get("TVR_APPROVED_AMOUNT"));
        holder.type.setText(mapdata.get("R_TYPE"));
        holder.requestedDate.setText(mapdata.get("REQUESTED_DATE"));
        holder.voucherNo.setText(mapdata.get("TVR_VOUCHER_NO"));
        holder.approvalStatus.setText(mapdata.get("ERFS_REQUEST_STATUS_NAME"));

        try {
            String travelVoucherStatus = mapdata.get("ERFS_REQUEST_STATUS");
            if (travelVoucherStatus.equals("0")) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        CustomTextView requestNo, eCode, empName, voucherNo, requestedDate, requestedAmount, approvedAmount, type, approvalStatus;

        public MyViewHolder(View itemView) {
            super(itemView);


            checkBox = itemView.findViewById(R.id.travelExpenseApprovalCheckbox);
            requestNo = itemView.findViewById(R.id.travelExpenseNo);
            eCode = itemView.findViewById(R.id.expenseEmployeeCode);
            empName = itemView.findViewById(R.id.expenseApprovalEmpName);
            type = itemView.findViewById(R.id.expenseApprovalType);
            requestedAmount = itemView.findViewById(R.id.travleExpenseApprovalRAmount);
            approvedAmount = itemView.findViewById(R.id.travelExpenseApprovalAAmount);
            requestedDate = itemView.findViewById(R.id.travleApprovalRequestedDate);
            voucherNo = itemView.findViewById(R.id.expenseVoucherNumber);
            approvalStatus = itemView.findViewById(R.id.expenseApprovalStatus);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    HashMap<String, String> mapdata = arrayList.get(getAdapterPosition());
                    if (isChecked) {
                        checkboxStatus.put(getAdapterPosition(), isChecked);
                        createXMLString(isChecked, getAdapterPosition(), mapdata.get("ERFS_REQUEST_ID"), mapdata.get("REQUEST_STATUS_ID"),
                                mapdata.get("ERFS_ACTION_LEVEL_SEQUENCE"), mapdata.get("MAX_ACTION_LEVEL_SEQUENCE"),
                                mapdata.get("TRAVEL_VOUCHER_STATUS"), mapdata.get("TVR_EMPLOYEE_ID"), mapdata.get("ERFS_REQUEST_FLOW_ID"), mapdata.get("TVR_APPROVED_AMOUNT"));
                        ;


                    } else {
                        checkboxStatus.remove(getAdapterPosition());
                        mapData.remove(getAdapterPosition());
                    }
                }
            });

        }

        private void createXMLString(boolean isChecked, int adapterPosition, String erfs_request_id, String request_status_id, String erfs_action_level_sequence, String max_action_level_sequence, String travel_voucher_status, String tvr_employee_id, String erfs_request_flow_id, String tvr_approved_amount) {


            stringBuilder = new StringBuilder();
            stringBuilder.append("@").
                    append(erfs_request_id).append(",").
                    append(request_status_id).append(",").
                    append(erfs_action_level_sequence).append(",").
                    append(max_action_level_sequence).append(",").
                    append(travel_voucher_status).append(",").
                    append(tvr_employee_id).append(",").
                    append(erfs_request_flow_id).append(",").
                    append(tvr_approved_amount);

            mapData.put(adapterPosition, stringBuilder.toString());
        }
    }

    public String getTravelApprovalXMLData() {
        stringBuilder = new StringBuilder();
        adapterPosition = new ArrayList<>();
        int position;
        if (checkboxStatus.size() > 0) {
            for (Map.Entry<Integer, Boolean> entry : checkboxStatus.entrySet()) {
                position = entry.getKey();
                stringBuilder.append(mapData.get(position));
                adapterPosition.add(position);
            }
            return stringBuilder.toString();
        } else {
            return "";
        }
    }

    public void updateList() {

        for (int i = 0; i < adapterPosition.size(); i++) {
            arrayList.remove(adapterPosition.get(i));
        }
        TravelExpenseApprovalAdapter.this.notifyDataSetChanged();
    }
}
