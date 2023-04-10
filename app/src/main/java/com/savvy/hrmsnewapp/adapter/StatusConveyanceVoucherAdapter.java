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
 * Created by orapc7 on 7/24/2017.
 */

public class StatusConveyanceVoucherAdapter extends RecyclerView.Adapter<StatusConveyanceVoucherAdapter.MyViewHolder>{

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    SharedPreferences shared;
    ArrayList<Integer> arrayList;
    CoordinatorLayout coordinatorLayout;
    View itemView;

    String VoucherType = "";
    String token_no = "";
    public CustomTextView txt_odclear,txt_odPullback;
    CustomTextView txt_conPullBack, txt_conEdit, txt_conCancel, txt_ConDetail, txt_ConAction;


    public StatusConveyanceVoucherAdapter(String VoucherType,Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.VoucherType = VoucherType;
        this.arrayList = arrayList;
        this.coordinatorLayout =coordinatorLayout;

    }


    @Override
    public StatusConveyanceVoucherAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(VoucherType.toUpperCase().equals("CONVEYANCE")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_status_adapter_row, parent, false);
        } else if(VoucherType.toUpperCase().equals("CONVEYANCE_VOUCHER")){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_request_row_adapter, parent, false);
        } else if(VoucherType.toUpperCase().equals("CONVEYANCE_VOUCHER_DETAIL")){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_detail_of_conveyence_row, parent, false);
        } else if(VoucherType.toUpperCase().equals("CONVEYANCE_DETAIL_EDIT")){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.approval_amount_edit_row, parent, false);
        }
        return new StatusConveyanceVoucherAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(StatusConveyanceVoucherAdapter.MyViewHolder holder, int position) {

        if(VoucherType.toUpperCase().equals("CONVEYANCE")){
            try {
                HashMap<String,String> mapdata=data.get(position);

                holder.txt_conVoucherToken.setText(mapdata.get("TOKEN_NO"));
                holder.txt_conVoucherNo.setText(mapdata.get("CVR_VOUCHER_NO"));
                holder.txt_conVoucherReqDate.setText(mapdata.get("REQUESTED_DATE"));
                holder.txt_conVoucherReqAmt.setText(mapdata.get("CVR_REQUESTED_AMOUNT"));
                holder.txt_conVoucherRemarks.setText(mapdata.get("CVR_REMARKS"));
                holder.txt_conVoucherStatus.setText(mapdata.get("REQUEST_STATUS"));
                holder.txt_conVoucherActionDate.setText(mapdata.get("ACTION_DATE"));
                holder.txt_conVoucherApproveAmt.setText(mapdata.get("CVR_APPROVED_AMOUNT"));

                String status = mapdata.get("CONVEYANCE_VOUCHER_STATUS_1");

                if(status.equals("0")){
                    txt_conPullBack.setVisibility(View.VISIBLE);
                    txt_conCancel.setVisibility(View.INVISIBLE);
                } else{
                    txt_conPullBack.setVisibility(View.INVISIBLE);
                    txt_conCancel.setVisibility(View.INVISIBLE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(VoucherType.toUpperCase().equals("CONVEYANCE_VOUCHER")){
            try {
                HashMap<String,String> mapdata=data.get(position);

                holder.txt_RVoucherToken.setText(mapdata.get("TOKEN_NO"));
                holder.txt_RVoucherreqDate.setText(mapdata.get("CR_REQUESTED_DATE"));
                holder.txt_RVoucherReqAmt.setText(mapdata.get("CR_REQUESTED_AMOUNT"));
                holder.txt_RVoucherremarks.setText(mapdata.get("CR_REMARKS"));
                holder.txt_RVoucherAppAmt.setText(mapdata.get("CR_APPROVED_AMOUNT"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(VoucherType.toUpperCase().equals("CONVEYANCE_VOUCHER_DETAIL")){
            try{
                HashMap<String,String> mapdata=data.get(position);

                holder.txt_VDType.setText(mapdata.get("EST_EXPENSE_SUB_TYPE"));
                holder.txt_VDDate.setText(mapdata.get("CRD_BILL_DATE"));
                holder.txt_VDFrom.setText(mapdata.get("CRD_PLACE_FROM"));
                holder.txt_VDTo.setText(mapdata.get("CRD_PLACE_TO"));
                holder.txt_VDdistance.setText(mapdata.get("CRD_DISTANCE"));
                holder.txt_VDAmount.setText(mapdata.get("CRD_BILL_AMOUNT"));
                holder.txt_VDReason.setText(mapdata.get("CRD_REASON"));

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }  else if(VoucherType.toUpperCase().equals("CONVEYANCE_DETAIL_EDIT")){
            try{
                HashMap<String,String> mapdata=data.get(position);

                holder.txt_VDType.setText(mapdata.get("EST_EXPENSE_SUB_TYPE"));
                holder.txt_VDDate.setText(mapdata.get("CRD_BILL_DATE"));
                holder.txt_VDFrom.setText(mapdata.get("CRD_PLACE_FROM"));
                holder.txt_VDTo.setText(mapdata.get("CRD_PLACE_TO"));
                holder.txt_VDdistance.setText(mapdata.get("CRD_DISTANCE"));
                holder.txt_VDAmount.setText(mapdata.get("CRD_BILL_AMOUNT"));
                holder.txt_VDReason.setText(mapdata.get("CRD_REASON"));
                holder.txt_VAppAmtReason.setText(mapdata.get("CRD_APPROVED_AMOUNT"));

                Constants.CONVEYANCE_APPROVED_AMOUNT = mapdata.get("CRD_APPROVED_AMOUNT");
                Constants.hashMap1.put(position,mapdata.get("CRD_APPROVED_AMOUNT"));

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

        public EditText txt_VAppAmtReason;

        public MyViewHolder(View view) {
            super(view);

            if(VoucherType.toUpperCase().equals("CONVEYANCE")) {
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

            } else if(VoucherType.toUpperCase().equals("CONVEYANCE_VOUCHER")){

                txt_RVoucherToken = view.findViewById(R.id.txt_RVoucherToken);
                txt_RVoucherreqDate = view.findViewById(R.id.txt_RVoucherreqDate);
                txt_RVoucherReqAmt = view.findViewById(R.id.txt_RVoucherReqAmt);
                txt_RVoucherremarks = view.findViewById(R.id.txt_RVoucherremarks);
                txt_RVoucherAppAmt = view.findViewById(R.id.txt_RVoucherAppAmt);

                txt_ConAction = view.findViewById(R.id.txt_RVoucherAction);
                txt_ConDetail = view.findViewById(R.id.txt_RVoucherDetail);

                txt_ConAction.setOnClickListener(this);
                txt_ConDetail.setOnClickListener(this);

            } else if(VoucherType.toUpperCase().equals("CONVEYANCE_VOUCHER_DETAIL")){
                txt_VDType = view.findViewById(R.id.vDetailType);
                txt_VDDate = view.findViewById(R.id.vDetailDate);
                txt_VDFrom = view.findViewById(R.id.vDetailPlaceFrom);
                txt_VDTo = view.findViewById(R.id.vDetailPlaceTo);
                txt_VDdistance = view.findViewById(R.id.vDetailDistance);
                txt_VDAmount = view.findViewById(R.id.vDetailAmt);
                txt_VDReason = view.findViewById(R.id.vDetailReason);
            }

            else if(VoucherType.toUpperCase().equals("CONVEYANCE_DETAIL_EDIT")){
                txt_VDType = view.findViewById(R.id.vDetailType);
                txt_VDDate = view.findViewById(R.id.vDetailDate);
                txt_VDFrom = view.findViewById(R.id.vDetailPlaceFrom);
                txt_VDTo = view.findViewById(R.id.vDetailPlaceTo);
                txt_VDdistance = view.findViewById(R.id.vDetailDistance);
                txt_VDAmount = view.findViewById(R.id.vDetailAmt);
                txt_VDReason = view.findViewById(R.id.vDetailReason);
                txt_VAppAmtReason = view.findViewById(R.id.conVoucher_appAmt);

                txt_VAppAmtReason.setOnClickListener(this);
            }

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }



}