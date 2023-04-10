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
 * Created by orapc7 on 7/24/2017.
 */

public class ConveyanceApprovalAdapter extends RecyclerView.Adapter<ConveyanceApprovalAdapter.MyViewHolder>{

  List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    CoordinatorLayout coordinatorLayout;
    String xmlData = "";
    String token_no = "";
    String Approve_Type = "";
    View itemView;

    CustomTextView txt_conModify, txt_conAction , txt_conVAction;
    public ConveyanceApprovalAdapter(String Approve_Type,Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.Approve_Type = Approve_Type;
        this.coordinatorLayout =coordinatorLayout;

    }


    @Override
    public ConveyanceApprovalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(Approve_Type.toUpperCase().equals("CONVEYANCE")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.coveyance_approval_row, parent, false);
        } else if(Approve_Type.toUpperCase().equals("CONVEYANCE_VOUCHER")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conveyance_voucher_approval_row, parent, false);
        }
        return new ConveyanceApprovalAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ConveyanceApprovalAdapter.MyViewHolder holder, int position) {
        if(Approve_Type.toUpperCase().equals("CONVEYANCE")) {
            HashMap<String, String> mapdata = data.get(position);

            holder.txt_conToken.setText(mapdata.get("TOKEN_NO"));
            holder.txt_conEName.setText(mapdata.get("EMPLOYEE_NAME"));
            holder.txt_conReqAmt.setText(mapdata.get("CR_REQUESTED_AMOUNT"));
            holder.txt_conType.setText(mapdata.get("R_TYPE"));
            holder.txt_conECode.setText(mapdata.get("EMPLOYEE_CODE"));
            holder.txt_conReqDate.setText(mapdata.get("REQUESTED_DATE"));
            holder.txt_conAppAmt.setText(mapdata.get("CR_APPROVED_AMOUNT"));
        } else if(Approve_Type.toUpperCase().equals("CONVEYANCE_VOUCHER")) {
            HashMap<String, String> mapdata = data.get(position);

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

         public CustomTextView txt_conToken, txt_conEName, txt_conReqAmt, txt_conType, txt_conECode, txt_conReqDate, txt_conAppAmt;
         public CustomTextView txt_conVToken, txt_conVEName, txt_conVReqAmt, txt_conVType, txt_conVECode, txt_conVReqDate, txt_conVAppAmt,
                                txt_conVvoucherNo;
        public MyViewHolder(View view) {
            super(view);
            if(Approve_Type.toUpperCase().equals("CONVEYANCE")) {
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
            } else if(Approve_Type.toUpperCase().equals("CONVEYANCE_VOUCHER")) {
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
