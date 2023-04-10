package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LoanDetailAdapter extends RecyclerView.Adapter<LoanDetailAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String,String>> arrayList;
    public  LoanDetailAdapter(Context context,ArrayList<HashMap<String,String>> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_detail_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        HashMap<String,String> hashMap=arrayList.get(position);
        holder.txt_Principal.setText(hashMap.get(""));
        holder.txt_Interest.setText(hashMap.get(""));
        holder.txt_TotalEMI.setText(hashMap.get(""));
        holder.txt_BalancePrincipal.setText(hashMap.get(""));
        holder.txt_BalanceAmount.setText(hashMap.get(""));
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView txt_Principal,txt_Interest, txt_TotalEMI, txt_BalancePrincipal, txt_BalanceAmount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_Principal=itemView.findViewById(R.id.txt_Principal);
            txt_Interest=itemView.findViewById(R.id.txt_Interest);
            txt_TotalEMI=itemView.findViewById(R.id.txt_TotalEMI);
            txt_BalancePrincipal=itemView.findViewById(R.id.txt_BalancePrincipal);
            txt_BalanceAmount=itemView.findViewById(R.id.txt_BalanceAmount);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
