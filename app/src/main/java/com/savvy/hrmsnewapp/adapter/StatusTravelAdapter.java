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
 * Created by orapc7 on 8/22/2017.
 */

public class StatusTravelAdapter extends RecyclerView.Adapter<StatusTravelAdapter.MyViewHolder> {

    Context context;
    List<HashMap<String,String>> data;
    CustomTextView txt_TS_Edit,txt_TS_Pullback,txt_TS_Cancel;
    String type = "";
    View view;
    CustomTextView txt_conPullBack, txt_conEdit, txt_conCancel, txt_ConDetail, txt_ConAction;

    public StatusTravelAdapter(String type, Context context, List<HashMap<String,String>> data){
        this.context = context;
        this.data = data;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (type.toUpperCase().trim().equals("TRAVEL_STATUS")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_status_row, parent, false);
        } else if (type.toUpperCase().trim().equals("TRAVEL_VOUCHER_STATUS")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_status_adapter_row, parent, false);
        }
        return new StatusTravelAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (type.toUpperCase().trim().equals("TRAVEL_STATUS")) {
            HashMap<String, String> mapdata = data.get(position);
            try {
                holder.txt_TS_token.setText(mapdata.get("TOKEN_NO"));
                holder.txt_TS_type.setText(mapdata.get("TT_TRAVEL_TYPE_NAME"));
                holder.txt_TS_startDate.setText(mapdata.get("TR_START_DATE"));
                holder.txt_TS_endDate.setText(mapdata.get("TR_RETURN_DATE"));
                holder.txt_TS_AdvAmt.setText(mapdata.get("TR_ADVANCE_AMOUNT"));
                holder.txt_TS_Remarks.setText(mapdata.get("TR_REMARKS"));
                holder.txt_TS_Status.setText(mapdata.get("REQUEST_STATUS"));
                holder.txt_TS_AppAmt.setText(mapdata.get("TR_APPROVED_ADVANCE_AMOUNT"));

                String status = mapdata.get("TRAVEL_STATUS_1");
                String status1 = mapdata.get("TRAVEL_STATUS");

                if (status.equals("0")) {
                    txt_TS_Pullback.setVisibility(View.VISIBLE);
                    txt_TS_Cancel.setVisibility(View.INVISIBLE);
                } else if (status.equals("2")) {
                    txt_TS_Pullback.setVisibility(View.INVISIBLE);
                    txt_TS_Cancel.setVisibility(View.VISIBLE);
                } else {
                    txt_TS_Pullback.setVisibility(View.INVISIBLE);
                    txt_TS_Cancel.setVisibility(View.INVISIBLE);
                }

//                if (status1.equals("0")) {
//                    txt_TS_Edit.setVisibility(View.VISIBLE);
//                } else {
//                    txt_TS_Edit.setVisibility(View.INVISIBLE);
//                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.toUpperCase().trim().equals("TRAVEL_VOUCHER_STATUS")) {
            try{
                HashMap<String,String> mapdata=data.get(position);

                holder.txt_conVoucherToken.setText(mapdata.get("TOKEN_NO"));
                holder.txt_conVoucherNo.setText(mapdata.get("TVR_VOUCHER_NO"));
                holder.txt_conVoucherReqDate.setText(mapdata.get("REQUESTED_DATE"));
                holder.txt_conVoucherReqAmt.setText(mapdata.get("TVR_REQUESTED_AMOUNT"));
                holder.txt_conVoucherRemarks.setText(mapdata.get("TVR_REMARKS"));
                holder.txt_conVoucherStatus.setText(mapdata.get("REQUEST_STATUS"));
                holder.txt_conVoucherActionDate.setText(mapdata.get("ACTION_DATE"));
                holder.txt_conVoucherApproveAmt.setText(mapdata.get("TVR_APPROVED_AMOUNT"));

                String status = mapdata.get("TRAVEL_VOUCHER_STATUS_1");

                if(status.equals("0")){
                    txt_conPullBack.setVisibility(View.VISIBLE);
                    txt_conCancel.setVisibility(View.INVISIBLE);
                } else{
                    txt_conPullBack.setVisibility(View.INVISIBLE);
                    txt_conCancel.setVisibility(View.INVISIBLE);
                }
            }catch (Exception e){
                e.printStackTrace();
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

        CustomTextView txt_TS_token,txt_TS_type,txt_TS_startDate,txt_TS_endDate,txt_TS_AdvAmt,
                txt_TS_Remarks,txt_TS_Status,txt_TS_AppAmt;

        public MyViewHolder(View itemView) {
            super(itemView);

            if (type.toUpperCase().trim().equals("TRAVEL_STATUS")) {

                txt_TS_token = itemView.findViewById(R.id.txt_travelStatusToken);
                txt_TS_type = itemView.findViewById(R.id.txt_travelStatusType);
                txt_TS_startDate = itemView.findViewById(R.id.txt_travelStatusStartDate);
                txt_TS_endDate = itemView.findViewById(R.id.txt_travelStatusReturnDate);
                txt_TS_AdvAmt = itemView.findViewById(R.id.txt_travelStatusAdvAmt);
                txt_TS_Remarks = itemView.findViewById(R.id.txt_travelStatusRemark);
                txt_TS_Status = itemView.findViewById(R.id.txt_travelStatusStatus);
                txt_TS_AppAmt = itemView.findViewById(R.id.txt_travelStatusApproveAmt);

                txt_TS_Edit = itemView.findViewById(R.id.txt_travelEdit);
                txt_TS_Pullback = itemView.findViewById(R.id.txt_travelPullback);
                txt_TS_Cancel = itemView.findViewById(R.id.txt_travelCancel);

                txt_TS_Edit.setOnClickListener(this);
                txt_TS_Pullback.setOnClickListener(this);
                txt_TS_Cancel.setOnClickListener(this);
            } else if(type.toUpperCase().trim().equals("TRAVEL_VOUCHER_STATUS")) {

                txt_conVoucherToken = view.findViewById(R.id.conVoucher_tokenno);
                txt_conVoucherNo = view.findViewById(R.id.conVoucher_No);
                txt_conVoucherReqDate = view.findViewById(R.id.conVoucher_ReqDate);
                txt_conVoucherReqAmt = view.findViewById(R.id.conVoucher_ReqAmt);
                txt_conVoucherRemarks = view.findViewById(R.id.conVoucher_Remarks);
                txt_conVoucherStatus = view.findViewById(R.id.conVoucher_Status);
                txt_conVoucherActionDate = view.findViewById(R.id.conVoucher_actionDate);
                txt_conVoucherApproveAmt = view.findViewById(R.id.conVoucher_appAmt);

                txt_conPullBack = view.findViewById(R.id.conVoucher_pullBack);
                txt_conCancel = view.findViewById(R.id.conVoucher_Cancel);

                txt_conPullBack.setOnClickListener(this);
                txt_conCancel.setOnClickListener(this);
            }

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
