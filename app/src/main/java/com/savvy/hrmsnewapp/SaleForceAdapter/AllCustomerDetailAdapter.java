package com.savvy.hrmsnewapp.SaleForceAdapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by savvy on 1/31/2018.
 */

public class AllCustomerDetailAdapter extends RecyclerView.Adapter<AllCustomerDetailAdapter.MyViewHolder> {

    List<HashMap<String, String>> arlData=new ArrayList<>();
    Context context;
    ItemClickListener itemClickListener;

    public AllCustomerDetailAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_all_customer_detail_row, parent, false);
        return new AllCustomerDetailAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            HashMap<String, String> hashMap = arlData.get(position);
            holder.tvPayrolName.setText(hashMap.get("PayrollMonthName"));
            holder.ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClickItem(position, hashMap.get("PayrollMonth"));
                }
            });
    }
    public void clearItems(){
        arlData.clear();
        notifyDataSetChanged();
    }
    public void addItems( List<HashMap<String, String>> list){
        arlData.clear();
        arlData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arlData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvPayrolName;
        ImageView ivDownload;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPayrolName = itemView.findViewById(R.id.tv_payrole_name);
            ivDownload = itemView.findViewById(R.id.iv_payslip_imageview);
        }
    }
}
