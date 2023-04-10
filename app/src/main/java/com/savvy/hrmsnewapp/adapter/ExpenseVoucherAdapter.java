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
 * Created by orapc7 on 8/16/2017.
 */

public class ExpenseVoucherAdapter extends RecyclerView.Adapter<ExpenseVoucherAdapter.MyViewHolder> {
    Context context;
    List<HashMap<String,String>> data;
    CustomTextView txt_ExpDetail, txt_ExpAction;
    String type = "";
    View itemView;
    CustomTextView txt_PullBack, txt_Edit, txt_Cancel, txt_Detail, txt_Action;

    public ExpenseVoucherAdapter(String type,Context context,List<HashMap<String,String>> data){
        this.context = context;
        this.data = data;
        this.type = type;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(type.toUpperCase().equals("EXPENSE_VOUCHER")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_request_row_adapter,parent,false);
        } else if(type.toUpperCase().equals("EXPENSE_VOUCHER_DETAIL")){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_voucher_detail_row,parent,false);
        } else if(type.toUpperCase().equals("EXPENSE_VOUCHER_STATUS")){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_status_adapter_row,parent,false);
        }


        return new ExpenseVoucherAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if(type.toUpperCase().equals("EXPENSE_VOUCHER_STATUS")){
            try {
                HashMap<String,String> mapdata=data.get(position);

                holder.txt_VoucherToken.setText(mapdata.get("TOKEN_NO"));
                holder.txt_VoucherNo.setText(mapdata.get("EVR_VOUCHER_NO"));
                holder.txt_VoucherReqDate.setText(mapdata.get("REQUESTED_DATE"));
                holder.txt_VoucherReqAmt.setText(mapdata.get("EVR_REQUESTED_AMOUNT"));
                holder.txt_VoucherRemarks.setText(mapdata.get("EVR_REMARKS"));
                holder.txt_VoucherStatus.setText(mapdata.get("REQUEST_STATUS"));
                holder.txt_VoucherActionDate.setText(mapdata.get("ACTION_DATE"));
                holder.txt_VoucherApproveAmt.setText(mapdata.get("EVR_APPROVED_AMOUNT"));

                String status = mapdata.get("EXPENSE_VOUCHER_STATUS_1");

                if(status.equals("0")){
                    txt_PullBack.setVisibility(View.VISIBLE);
                    txt_Cancel.setVisibility(View.INVISIBLE);
                } else{
                    txt_PullBack.setVisibility(View.INVISIBLE);
                    txt_Cancel.setVisibility(View.INVISIBLE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(type.toUpperCase().equals("EXPENSE_VOUCHER")) {
            try {
                HashMap<String, String> mapdata = data.get(position);

                holder.txt_RVoucherToken.setText(mapdata.get("TOKEN_NO"));
                holder.txt_RVoucherreqDate.setText(mapdata.get("ER_REQUESTED_DATE"));
                holder.txt_RVoucherReqAmt.setText(mapdata.get("ER_REQUESTED_AMOUNT"));
                holder.txt_RVoucherremarks.setText(mapdata.get("ER_REMARKS"));
                holder.txt_RVoucherAppAmt.setText(mapdata.get("ER_APPROVED_AMOUNT"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(type.toUpperCase().equals("EXPENSE_VOUCHER_DETAIL")){
            try{
                HashMap<String,String> mapdata=data.get(position);

                holder.txt_VDType.setText(mapdata.get("EST_EXPENSE_SUB_TYPE"));
                holder.txt_VDDate.setText(mapdata.get("ERD_BILL_DATE"));
                holder.txt_VDBillNo.setText(mapdata.get("ERD_BILL_NO"));
                holder.txt_VDBillAmt.setText(mapdata.get("ERD_BILL_AMOUNT"));
                holder.txt_VDAmount.setText(mapdata.get("ERD_APPROVED_AMOUNT"));
                holder.txt_VDReason.setText(mapdata.get("ERD_REASON"));

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CustomTextView txt_VoucherToken, txt_VoucherNo, txt_VoucherReqDate, txt_VoucherReqAmt,
                txt_VoucherRemarks, txt_VoucherStatus, txt_VoucherActionDate, txt_VoucherApproveAmt;
        public CustomTextView txt_RVoucherToken, txt_RVoucherreqDate, txt_RVoucherReqAmt, txt_RVoucherremarks, txt_RVoucherAppAmt;
        public CustomTextView txt_VDType, txt_VDDate, txt_VDBillNo, txt_VDBillAmt, txt_VDAmount, txt_VDReason;
        public MyViewHolder(View view){
            super(view);

            if(type.toUpperCase().equals("EXPENSE_VOUCHER_STATUS")) {
                txt_VoucherToken = view.findViewById(R.id.conVoucher_tokenno);
                txt_VoucherNo = view.findViewById(R.id.conVoucher_No);
                txt_VoucherReqDate = view.findViewById(R.id.conVoucher_ReqDate);
                txt_VoucherReqAmt = view.findViewById(R.id.conVoucher_ReqAmt);
                txt_VoucherRemarks = view.findViewById(R.id.conVoucher_Remarks);
                txt_VoucherStatus = view.findViewById(R.id.conVoucher_Status);
                txt_VoucherActionDate = view.findViewById(R.id.conVoucher_actionDate);
                txt_VoucherApproveAmt = view.findViewById(R.id.conVoucher_appAmt);

                txt_PullBack = view.findViewById(R.id.conVoucher_pullBack);
                txt_Cancel = view.findViewById(R.id.conVoucher_Cancel);

                txt_PullBack.setOnClickListener(this);
                txt_Cancel.setOnClickListener(this);

            } else if(type.toUpperCase().equals("EXPENSE_VOUCHER")) {
                txt_RVoucherToken = view.findViewById(R.id.txt_RVoucherToken);
                txt_RVoucherreqDate = view.findViewById(R.id.txt_RVoucherreqDate);
                txt_RVoucherReqAmt = view.findViewById(R.id.txt_RVoucherReqAmt);
                txt_RVoucherremarks = view.findViewById(R.id.txt_RVoucherremarks);
                txt_RVoucherAppAmt = view.findViewById(R.id.txt_RVoucherAppAmt);
                txt_ExpAction = view.findViewById(R.id.txt_RVoucherAction);
                txt_ExpDetail = view.findViewById(R.id.txt_RVoucherDetail);

                txt_ExpAction.setOnClickListener(this);
                txt_ExpDetail.setOnClickListener(this);

            } else if(type.toUpperCase().equals("EXPENSE_VOUCHER_DETAIL")){
                txt_VDType = view.findViewById(R.id.vDetailType);
                txt_VDDate = view.findViewById(R.id.vDetailDate);
                txt_VDBillNo = view.findViewById(R.id.vDetailBillNo);
                txt_VDBillAmt = view.findViewById(R.id.vDetailBillAmt);
                txt_VDAmount = view.findViewById(R.id.vDetailAppAmt);
                txt_VDReason = view.findViewById(R.id.vDetailReason);
            }
        }

        @Override
        public void onClick(View v) {
        }
    }
}
