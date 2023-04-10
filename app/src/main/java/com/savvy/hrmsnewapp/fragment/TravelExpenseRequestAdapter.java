package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class TravelExpenseRequestAdapter extends RecyclerView.Adapter<TravelExpenseRequestAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> arrayList;
    TreeMap<Integer, String> mapData = new TreeMap<Integer, String>();
    Context context;
    RecyclerViewClickListener listener;
    StringBuilder stringBuilder;

    public TravelExpenseRequestAdapter(Context context, ArrayList<HashMap<String, String>> arrayList, RecyclerViewClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_expense_request_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = arrayList.get(position);
        holder.txt_TokenNo.setText(hashMap.get("TOKEN_NO"));
        holder.txt_TravelType.setText(hashMap.get("TRF_TRAVEL_TYPE"));
        holder.txt_StartDate.setText(hashMap.get("TRF_START_DATE"));
        holder.txt_ReturnDate.setText(hashMap.get("TRF_END_DATE"));
        holder.txt_BudgetAmount.setText(hashMap.get("TRF_BUDGETED_AMOUNT"));
        holder.txt_Project.setText(hashMap.get("TPM_PROJECT_NAME"));
        holder.txt_Remarks.setText(hashMap.get("TRF_TRAVEL_REASON"));
        holder.checkBox.setChecked(false);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView txt_TokenNo, txt_TravelType, txt_StartDate, txt_ReturnDate, txt_BudgetAmount, txt_Project, txt_Remarks;
        Button addExpense;
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt_TokenNo = itemView.findViewById(R.id.travel_expense_TokenNo);
            txt_TravelType = itemView.findViewById(R.id.travel_expense_TravelType);
            txt_StartDate = itemView.findViewById(R.id.travel_expense_StartDate);
            txt_ReturnDate = itemView.findViewById(R.id.travel_expense_ReturnDate);
            txt_BudgetAmount = itemView.findViewById(R.id.travel_expense_BudgetAmount);
            txt_Project = itemView.findViewById(R.id.travel_expense_Project);
            txt_Remarks = itemView.findViewById(R.id.travel_expense_Remarks);
            addExpense = itemView.findViewById(R.id.travel_expense_ExpenseButton);
            checkBox = itemView.findViewById(R.id.travelExpenseCheckBox);
            addExpense.setOnClickListener(this);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mapData.put(getAdapterPosition(), arrayList.get(getAdapterPosition()).get("TRF_TRAVEL_REQUEST_ID"));
                    } else {
                        mapData.remove(getAdapterPosition());
                    }
                }
            });

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

    public String getTripDetail() {

        stringBuilder = new StringBuilder();

        for (int i = 0; i < mapData.size(); i++) {

            for (int key : mapData.keySet()) {
                mapData.containsKey(key);
                stringBuilder.append(mapData.get(key));
            }

        }
        return stringBuilder.toString();
    }

    public int getTripCount() {
        return mapData.size();
    }
}
