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


public class TravelRequestApprovalAdapter extends RecyclerView.Adapter<TravelRequestApprovalAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;
    StringBuilder stringBuilder;
    TreeMap<Integer, Boolean> checkboxStatus = new TreeMap<>();
    Map<Integer, String> mapData = new HashMap<>();
    List<Integer> adapterPosition;

    public TravelRequestApprovalAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_request_approval_row, parent, false);
        return new TravelRequestApprovalAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = arrayList.get(position);
        holder.requestNo.setText(mapdata.get("TOKEN_NO"));
        holder.eCode.setText(mapdata.get("EMPLOYEE_CODE"));
        holder.empName.setText(mapdata.get("EMPLOYEE_NAME"));
        holder.traveltype.setText(mapdata.get("TRF_TRAVEL_TYPE"));
        holder.startDate.setText(mapdata.get("TRF_START_DATE"));
        holder.returnDate.setText(mapdata.get("TRF_END_DATE"));
        holder.amount.setText(mapdata.get("TRF_BUDGETED_AMOUNT"));
        holder.status.setText(mapdata.get("REQUEST_STATUS"));

        try {
            String travelStatus = mapdata.get("ERFS_REQUEST_STATUS");
            if (travelStatus.equals("0")) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        CustomTextView requestNo, eCode, empName, traveltype, startDate, returnDate, amount, status;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.travelApprovalCheckbox);
            requestNo = itemView.findViewById(R.id.travelRequestNo);
            eCode = itemView.findViewById(R.id.approvalEmployeeCode);
            empName = itemView.findViewById(R.id.travelApprovalEmpName);
            traveltype = itemView.findViewById(R.id.travelApprovalType);
            startDate = itemView.findViewById(R.id.travleApprovalStartDate);
            returnDate = itemView.findViewById(R.id.travelApprovalReturnDate);
            amount = itemView.findViewById(R.id.travelApprovalAmount);
            status = itemView.findViewById(R.id.travelApprovalStatus);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    HashMap<String, String> mapdata = arrayList.get(getAdapterPosition());
                    if (isChecked) {
                        checkboxStatus.put(getAdapterPosition(), isChecked);
                        createXMLString(isChecked, getAdapterPosition(), mapdata.get("REQUEST_ID"), mapdata.get("REQUEST_FLOW_STATUS_ID"), mapdata.get("ACTION_LEVEL_SEQUENCE"), mapdata.get("MAX_ACTION_LEVEL_SEQUENCE"), mapdata.get("TRAVEL_STATUS"), mapdata.get("EMPLOYEE_ID"), mapdata.get("ERFS_REQUEST_FLOW_ID"));

                    } else {
                        checkboxStatus.remove(getAdapterPosition());
                        mapData.remove(getAdapterPosition());
                    }
                }
            });
        }
    }

    private void createXMLString(boolean isChecked, int adapterPosition, String request_id, String request_flow_status_id, String action_level_sequence, String max_action_level_sequence, String travel_status, String employee_id, String erfs_request_flow_id) {

        stringBuilder = new StringBuilder();
        stringBuilder.append("@").
                append(request_id).append(",").
                append(request_flow_status_id).append(",").
                append(action_level_sequence).append(",").
                append(max_action_level_sequence).append(",").
                append(travel_status).append(",").
                append(employee_id).append(",").
                append(erfs_request_flow_id);

        mapData.put(adapterPosition, stringBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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

}
