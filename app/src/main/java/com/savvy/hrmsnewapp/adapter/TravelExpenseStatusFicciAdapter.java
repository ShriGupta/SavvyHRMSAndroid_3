package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;


public class TravelExpenseStatusFicciAdapter extends RecyclerView.Adapter<TravelExpenseStatusFicciAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> data;
    LayoutInflater inflater;
    Context context;
    ArrayList<Integer> arrayList;
    CoordinatorLayout coordinatorLayout;
    CustomTextView txt_actionValuePull;


    public TravelExpenseStatusFicciAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.coordinatorLayout = coordinatorLayout;
    }

    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_expense_status_ficci_row, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = data.get(position);

        holder.txt_punchToken_no.setText(mapdata.get("TOKEN_NO"));
        holder.txt_VoucherNo.setText(mapdata.get("TVR_VOUCHER_NO"));
        holder.teRequestedDate.setText(mapdata.get("REQUESTED_DATE"));
        holder.teRequestedAmount.setText(mapdata.get("TVR_REQUESTED_AMOUNT"));
        holder.teRemarks.setText(mapdata.get("TVR_REMARKS"));
        holder.teStatus.setText(mapdata.get("REQUEST_STATUS"));
        holder.teActionDate.setText(mapdata.get("ACTION_DATE"));
        holder.teApprovedAmount.setText(mapdata.get("TVR_APPROVED_AMOUNT"));

        String te_Status = mapdata.get("TRAVEL_VOUCHER_STATUS_1");
        if (te_Status.equals("0")) {
            txt_actionValuePull.setVisibility(View.VISIBLE);
        } else if (te_Status.equals("2")) {
            txt_actionValuePull.setVisibility(View.INVISIBLE);
        } else {
            txt_actionValuePull.setVisibility(View.INVISIBLE);
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView txt_punchToken_no, txt_VoucherNo, teRequestedDate, teRemarks;
        public CustomTextView teStatus, teActionDate, teRequestedAmount, teApprovedAmount;


        public MyViewHolder(View view) {
            super(view);
            txt_punchToken_no = view.findViewById(R.id.tv_travelExpenseToken_value);
            txt_VoucherNo = view.findViewById(R.id.travelExpenseVoucherNo);
            teRequestedDate = view.findViewById(R.id.traveExpenseRequestedDate);
            teRemarks = view.findViewById(R.id.traveExpenseRemarks);
            teStatus = view.findViewById(R.id.travelExpenseStatus);
            teActionDate = view.findViewById(R.id.traveExpenseActionDate);
            teApprovedAmount = view.findViewById(R.id.travelExpenseApprovedAmount);
            teRequestedAmount = view.findViewById(R.id.travelExpenseRequestedAmount);
            teApprovedAmount = view.findViewById(R.id.travelExpenseApprovedAmount);
            txt_actionValuePull = view.findViewById(R.id.tv_pullback_travelExpense);

            txt_actionValuePull.setOnClickListener(this);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
