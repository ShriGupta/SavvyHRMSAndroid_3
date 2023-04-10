package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 7/29/2017.
 */

public class ApprovalTravalAdapter extends RecyclerView.Adapter<ApprovalTravalAdapter.MyViewHolder>{

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    CoordinatorLayout coordinatorLayout;
    String xmlData = "";
    String token_no = "";
    String Approve_Type = "";
    View itemView;

    CustomTextView txt_conModify, txt_travelAction , txt_conVAction;
    public ApprovalTravalAdapter(String Approve_Type,Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.Approve_Type = Approve_Type;
        this.coordinatorLayout =coordinatorLayout;

    }


    @Override
    public ApprovalTravalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(Approve_Type.toUpperCase().equals("TRAVEL")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_approval_row, parent, false);
        } else if(Approve_Type.toUpperCase().equals("TRAVEL_VOUCHER")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conveyance_voucher_approval_row, parent, false);
        }
        return new ApprovalTravalAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ApprovalTravalAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> mapdata;
        if(Approve_Type.toUpperCase().equals("TRAVEL")) {
            mapdata = data.get(position);

            holder.txt_travelToken.setText(mapdata.get("TOKEN_NO"));
            holder.txt_travelEName.setText(mapdata.get("EMPLOYEE_NAME"));
            holder.txt_travelSDate.setText(mapdata.get("TR_START_DATE"));
            holder.txt_travelAdvanceAmt.setText(mapdata.get("TR_ADVANCE_AMOUNT"));
            holder.txt_travelType.setText(mapdata.get("R_TYPE"));
            holder.txt_travelECode.setText(mapdata.get("EMPLOYEE_CODE"));
            holder.txt_travelTravelType.setText(mapdata.get("TT_TRAVEL_TYPE_NAME"));
            holder.txt_travelRDate.setText(mapdata.get("TR_RETURN_DATE"));
            holder.txt_travelApproveAmt.setText(mapdata.get("TR_APPROVED_ADVANCE_AMOUNT"));

        } else if(Approve_Type.toUpperCase().equals("TRAVEL_VOUCHER")) {
            mapdata = data.get(position);

            holder.txt_conVToken.setText(mapdata.get("TOKEN_NO"));
            holder.txt_conVEName.setText(mapdata.get("EMPLOYEE_NAME"));
            holder.txt_conVReqAmt.setText(mapdata.get("CVR_REQUESTED_AMOUNT"));
            holder.txt_conVType.setText(mapdata.get("R_TYPE"));
            holder.txt_conVECode.setText(mapdata.get("EMPLOYEE_CODE"));
            holder.txt_conVReqDate.setText(mapdata.get("REQUESTED_DATE"));
            holder.txt_conVAppAmt.setText(mapdata.get("CVR_APPROVED_AMOUNT"));
            holder.txt_conVvoucherNo.setText(mapdata.get("CVR_VOUCHER_NO"));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CustomTextView txt_travelToken, txt_travelEName, txt_travelSDate, txt_travelAdvanceAmt,txt_travelType, txt_travelECode,
                txt_travelTravelType, txt_travelRDate,txt_travelApproveAmt;
        public CustomTextView txt_conVToken, txt_conVEName, txt_conVReqAmt, txt_conVType, txt_conVECode, txt_conVReqDate, txt_conVAppAmt,
                txt_conVvoucherNo;
        public MyViewHolder(View view) {
            super(view);
            if(Approve_Type.toUpperCase().equals("TRAVEL")) {
                txt_travelToken = view.findViewById(R.id.txt_travelApproveToken);
                txt_travelEName = view.findViewById(R.id.txt_travelApproveEName);
                txt_travelSDate = view.findViewById(R.id.txt_travelApproveSDate);
                txt_travelAdvanceAmt = view.findViewById(R.id.txt_travelApproveAdvAmt);
                txt_travelType = view.findViewById(R.id.txt_travelApproveType);
                txt_travelECode = view.findViewById(R.id.txt_travelApproveECode);
                txt_travelTravelType = view.findViewById(R.id.txt_travelApproveTravelType);
                txt_travelRDate = view.findViewById(R.id.txt_travelApproveReturnDate);
                txt_travelApproveAmt = view.findViewById(R.id.txt_travelApprovalApproveAmt);

//                txt_conModify = (CustomTextView) view.findViewById(R.id.txt_conApproveModify);
                txt_travelAction = view.findViewById(R.id.txt_travelAction);

//                txt_conModify.setOnClickListener(this);
                txt_travelAction.setOnClickListener(this);
            } else if(Approve_Type.toUpperCase().equals("TRAVEL_VOUCHER")) {
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
            }
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }



}