package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TravelExpenseRecyclerViewAdapter extends RecyclerView.Adapter<TravelExpenseRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;
    RecyclerViewClickListener listener;


    public TravelExpenseRecyclerViewAdapter(Context context, ArrayList<HashMap<String, String>> arrayList,RecyclerViewClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener=listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_expense_recyclerview_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        HashMap<String, String> hashMap = arrayList.get(position);
        holder.expenseType.setText(hashMap.get("EST_EXPENSE_SUB_TYPE"));
        holder.billNo.setText(hashMap.get("TED_BILL_NO"));
        holder.billdate.setText(hashMap.get("TED_BILL_DATE"));
        holder.billAmount.setText(hashMap.get("TED_BILL_AMOUNT"));
        holder.companyProvided.setText(hashMap.get("TED_IS_PAID_BY_COMPANY"));
        holder.reason.setText(hashMap.get("TED_REASON"));
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CustomTextView expenseType, billNo, billdate, billAmount, companyProvided, reason;
        Button removeButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            expenseType = itemView.findViewById(R.id.expenseType);
            billNo = itemView.findViewById(R.id.billNo);
            billdate = itemView.findViewById(R.id.billDate);
            billAmount = itemView.findViewById(R.id.billAmount);
            companyProvided = itemView.findViewById(R.id.companyProvided);
            reason = itemView.findViewById(R.id.expenseReason);
            removeButton = itemView.findViewById(R.id.travelExpenseRemoveButton);
            removeButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }



    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
