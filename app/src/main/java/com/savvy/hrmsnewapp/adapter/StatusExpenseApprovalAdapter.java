package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 9/22/2017.
 */

public class StatusExpenseApprovalAdapter extends RecyclerView.Adapter<StatusExpenseApprovalAdapter.MyViewHolder> {
    List<HashMap<String,String>> data ;

    Context context;
    SharedPreferences shared;
    ArrayList<Integer> arrayList;
    CoordinatorLayout coordinatorLayout;
    View itemView;

    String VoucherType = "";
    String token_no = "";
    CustomTextView txt_conPullBack, txt_conCancel, txt_ConDetail, txt_ConAction;

    public StatusExpenseApprovalAdapter(String VoucherType,Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data) {
        this.context    = context;
        this.data       = data;
        this.VoucherType = VoucherType;
        this.coordinatorLayout =coordinatorLayout;

    }


    @Override
    public StatusExpenseApprovalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(VoucherType.toUpperCase().equals("EXPENSE_DETAIL_EDIT")){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.approval_amount_expense_edit_row, parent, false);
        }
        return new StatusExpenseApprovalAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatusExpenseApprovalAdapter.MyViewHolder holder, int position) {
        if(VoucherType.toUpperCase().equals("EXPENSE_DETAIL_EDIT")){
            try{
                HashMap<String,String> mapdata=data.get(position);

                holder.txt_EAE_Type.setText(mapdata.get("EST_EXPENSE_SUB_TYPE"));
                holder.txt_EAE_BillDate.setText(mapdata.get("ERD_BILL_DATE"));
                holder.txt_EAE_BillNo.setText(mapdata.get("ERD_BILL_NO"));
                holder.txt_EAE_Reason.setText(mapdata.get("ERD_REASON"));
                holder.txt_EAE_Amount.setText(mapdata.get("ERD_BILL_AMOUNT"));
                holder.txt_EAEAppAmtReason.setText(mapdata.get("ERD_APPROVED_AMOUNT"));

                Constants.EXPENSE_APPROVED_AMOUNT = mapdata.get("ERD_APPROVED_AMOUNT");
                Constants.hashMap2.put(position,mapdata.get("ERD_APPROVED_AMOUNT"));

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

        public CustomTextView txt_conVoucherToken, txt_conVoucherNo, txt_conVoucherReqDate, txt_conVoucherReqAmt,
                txt_conVoucherRemarks, txt_conVoucherStatus, txt_conVoucherActionDate, txt_conVoucherApproveAmt;

        public CustomTextView txt_RVoucherToken, txt_RVoucherreqDate, txt_RVoucherReqAmt, txt_RVoucherremarks, txt_RVoucherAppAmt;
        public CustomTextView txt_VDType, txt_VDDate, txt_VDFrom, txt_VDTo, txt_VDdistance, txt_VDAmount, txt_VDReason;

        public EditText txt_EAEAppAmtReason;
        public CustomTextView txt_EAE_Type,txt_EAE_BillDate,txt_EAE_BillNo,txt_EAE_Reason,txt_EAE_Amount;

        public MyViewHolder(View view) {
            super(view);

            if(VoucherType.toUpperCase().equals("EXPENSE_DETAIL_EDIT")) {
                txt_EAE_Type = view.findViewById(R.id.EAEDetailType);
                txt_EAE_BillDate = view.findViewById(R.id.EAEDetailDate);
                txt_EAE_BillNo = view.findViewById(R.id.EAEDetailBillNo);
                txt_EAE_Reason = view.findViewById(R.id.EAEDetailReason);
                txt_EAE_Amount = view.findViewById(R.id.EAEDetailAmt);

                txt_EAEAppAmtReason = view.findViewById(R.id.EAEVoucher_appAmt);

            }

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
