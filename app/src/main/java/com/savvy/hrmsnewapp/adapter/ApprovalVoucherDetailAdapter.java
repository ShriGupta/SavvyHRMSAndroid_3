package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 8/2/2017.
 */

public class ApprovalVoucherDetailAdapter extends RecyclerView.Adapter<ApprovalVoucherDetailAdapter.MyViewHolder> {

    CustomTextView txt_conVAction;
    List<HashMap<String,String>> data ;

    Context context;
    String type = "";

    public ApprovalVoucherDetailAdapter(String type,Context context, List<HashMap<String,String>> data){
        this.context = context;
        this.data = data;
        this.type = type;
    }

    @Override
    public ApprovalVoucherDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conveyance_voucher_approval_row,parent,false);
        return new ApprovalVoucherDetailAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApprovalVoucherDetailAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = data.get(position);

        if(type.toUpperCase().equals("EXPENSE")){
            holder.txt_conVToken.setText(mapdata.get("TOKEN_NO"));
            holder.txt_conVEName.setText(mapdata.get("EMPLOYEE_NAME"));
            holder.txt_conVReqAmt.setText(mapdata.get("EVR_REQUESTED_AMOUNT"));
            holder.txt_conVType.setText(mapdata.get("R_TYPE"));
            holder.txt_conVECode.setText(mapdata.get("EMPLOYEE_CODE"));
            holder.txt_conVReqDate.setText(mapdata.get("REQUESTED_DATE"));
            holder.txt_conVAppAmt.setText(mapdata.get("EVR_APPROVED_AMOUNT"));
            holder.txt_conVvoucherNo.setText(mapdata.get("EVR_VOUCHER_NO"));

        } else if(type.toUpperCase().equals("TRAVEL")){
            holder.txt_conVToken.setText(mapdata.get("TOKEN_NO"));
            holder.txt_conVEName.setText(mapdata.get("EMPLOYEE_NAME"));
            holder.txt_conVReqAmt.setText(mapdata.get("TVR_REQUESTED_AMOUNT"));
            holder.txt_conVType.setText(mapdata.get("R_TYPE"));
            holder.txt_conVECode.setText(mapdata.get("EMPLOYEE_CODE"));
            holder.txt_conVReqDate.setText(mapdata.get("REQUESTED_DATE"));
            holder.txt_conVAppAmt.setText(mapdata.get("TVR_APPROVED_AMOUNT"));
            holder.txt_conVvoucherNo.setText(mapdata.get("TVR_VOUCHER_NO"));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CustomTextView txt_conVToken, txt_conVEName, txt_conVReqAmt, txt_conVType, txt_conVECode, txt_conVReqDate, txt_conVAppAmt,
                txt_conVvoucherNo;

        public MyViewHolder(View view) {
            super(view);

            txt_conVToken = view.findViewById(R.id.txt_conVApproveToken);
            txt_conVEName = view.findViewById(R.id.txt_conVApproveEName);
            txt_conVReqAmt = view.findViewById(R.id.txt_conVApproveReqAmt);
            txt_conVType = view.findViewById(R.id.txt_conVApproveType);
            txt_conVECode = view.findViewById(R.id.txt_conVApproveECode);
            txt_conVReqDate = view.findViewById(R.id.txt_conVApproveReqDate);
            txt_conVAppAmt = view.findViewById(R.id.txt_conVApproveAmount);
            txt_conVvoucherNo = view.findViewById(R.id.txt_conVApproveVoucherNo);

            txt_conVAction = view.findViewById(R.id.conVapproveAction);
            txt_conVAction.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
