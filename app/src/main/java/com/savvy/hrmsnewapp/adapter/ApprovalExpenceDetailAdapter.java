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

public class ApprovalExpenceDetailAdapter extends RecyclerView.Adapter<ApprovalExpenceDetailAdapter.MyViewHolder> {

    List<HashMap<String, String>> data;
    Context context;
    CustomTextView txt_conModify, txt_conAction , txt_conVAction;

    public ApprovalExpenceDetailAdapter(Context context, List<HashMap<String, String>> data){
        this.context = context;
        this.data = data;

    }
    @Override
    public ApprovalExpenceDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coveyance_approval_row,parent,false);
        return new ApprovalExpenceDetailAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApprovalExpenceDetailAdapter.MyViewHolder holder, int position) {
            HashMap<String,String> mapdata = data.get(position);

        holder.txt_conToken.setText(mapdata.get("TOKEN_NO"));
        holder.txt_conEName.setText(mapdata.get("EMPLOYEE_NAME"));
        holder.txt_conReqAmt.setText(mapdata.get("ER_REQUESTED_AMOUNT"));
        holder.txt_conType.setText(mapdata.get("R_TYPE"));
        holder.txt_conECode.setText(mapdata.get("EMPLOYEE_CODE"));
        holder.txt_conReqDate.setText(mapdata.get("REQUESTED_DATE"));
        holder.txt_conAppAmt.setText(mapdata.get("ER_APPROVED_AMOUNT"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CustomTextView txt_conToken, txt_conEName, txt_conReqAmt, txt_conType, txt_conECode, txt_conReqDate, txt_conAppAmt;

        public MyViewHolder(View view) {
            super(view);
            txt_conToken = view.findViewById(R.id.txt_conApproveToken);
            txt_conEName = view.findViewById(R.id.txt_conApproveEName);
            txt_conReqAmt = view.findViewById(R.id.txt_conApproveReqAmt);
            txt_conType = view.findViewById(R.id.txt_conApproveType);
            txt_conECode = view.findViewById(R.id.txt_conApproveECode);
            txt_conReqDate = view.findViewById(R.id.txt_conApproveReqDate);
            txt_conAppAmt = view.findViewById(R.id.txt_conApproveAppAmt);

            txt_conModify = view.findViewById(R.id.txt_conApproveModify);
            txt_conAction = view.findViewById(R.id.txt_conAction);

            txt_conModify.setOnClickListener(this);
            txt_conAction.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
