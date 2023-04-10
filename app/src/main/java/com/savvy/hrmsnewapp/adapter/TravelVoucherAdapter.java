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
 * Created by orapc7 on 9/16/2017.
 */

public class TravelVoucherAdapter extends RecyclerView.Adapter<TravelVoucherAdapter.MyViewHolder> {

    Context context;
    List<HashMap<String,String>> data;
    String VoucherType = "";
    View itemView;

    CustomTextView txt_TRVAction, txt_TRVExpense;

    public TravelVoucherAdapter(String VoucherType, Context context, List<HashMap<String,String>> data){
        this.context = context;
        this.data = data;
        this.VoucherType = VoucherType;
    }
    @Override
    public TravelVoucherAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(VoucherType.toUpperCase().equals("TRAVEL_VOUCHER_REQUEST")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_request_voucher_row, parent, false);
//        } else if(VoucherType.toUpperCase().equals("CONVEYANCE_VOUCHER")){
//            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_request_row_adapter, parent, false);
//        } else if(VoucherType.toUpperCase().equals("CONVEYANCE_VOUCHER_DETAIL")){
//            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_detail_of_conveyence_row, parent, false);
//        } else if(VoucherType.toUpperCase().equals("CONVEYANCE_DETAIL_EDIT")){
//            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.approval_amount_edit_row, parent, false);
        }
        return new TravelVoucherAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TravelVoucherAdapter.MyViewHolder holder, int position) {
        if(VoucherType.toUpperCase().equals("TRAVEL_VOUCHER_REQUEST")){
            try {
                HashMap<String,String> mapdata=data.get(position);


                holder.TRV_Token.setText(mapdata.get("TOKEN_NO"));
                holder.TRV_StartDate.setText(mapdata.get("TR_START_DATE_1"));
                holder.TRV_EstCost.setText(mapdata.get("TR_ESTIMATED_COST"));
                holder.TRV_AppAmt.setText(mapdata.get("TR_APPROVED_ADVANCE_AMOUNT"));
//                holder.TRV_Expense.setText(mapdata.get("TR_RETURN_DATE"));
                holder.TRV_TravelType.setText(mapdata.get("TT_TRAVEL_TYPE_NAME"));
                holder.TRV_ReturnDate.setText(mapdata.get("TR_RETURN_DATE_1"));
                holder.TRV_AdvAmt.setText(mapdata.get("TR_ADVANCE_AMOUNT"));
                holder.TRV_Remarks.setText(mapdata.get("TR_REMARKS"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CustomTextView TRV_Token, TRV_StartDate, TRV_EstCost, TRV_AppAmt, TRV_Expense, TRV_TravelType, TRV_ReturnDate,
                TRV_AdvAmt,TRV_Remarks;
        public MyViewHolder(View itemView) {
            super(itemView);
            if(VoucherType.toUpperCase().equals("TRAVEL_VOUCHER_REQUEST")) {
                TRV_Token = itemView.findViewById(R.id.txt_TRVoucherToken);
                TRV_StartDate = itemView.findViewById(R.id.txt_TRVoucherStartDate);
                TRV_EstCost = itemView.findViewById(R.id.txt_TRVoucherEstCost);
                TRV_AppAmt = itemView.findViewById(R.id.txt_TRVoucherAppAmt);
                TRV_TravelType = itemView.findViewById(R.id.txt_TRVoucherTravelType);
                TRV_ReturnDate = itemView.findViewById(R.id.txt_TRVoucherReturnDate);
                TRV_AdvAmt = itemView.findViewById(R.id.txt_TRVoucherAdvAmt);
                TRV_Remarks = itemView.findViewById(R.id.txt_TRVoucherRemarks);

                txt_TRVAction = itemView.findViewById(R.id.txt_TRVAction);
                txt_TRVExpense = itemView.findViewById(R.id.txt_TRVExpense);
            }

            txt_TRVAction.setOnClickListener(this);
            txt_TRVExpense.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
