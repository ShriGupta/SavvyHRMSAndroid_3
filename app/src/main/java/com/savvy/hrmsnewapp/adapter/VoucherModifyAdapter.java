package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 8/10/2017.
 */

public class VoucherModifyAdapter extends RecyclerView.Adapter<VoucherModifyAdapter.MyViewHolder>{

    CoordinatorLayout coordinatorLayout;
    Context context;
    List<HashMap<String, String>> data;

    public VoucherModifyAdapter(Context context, CoordinatorLayout coordinatorLayout, List<HashMap<String, String>> data){
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.data = data;
    }

    @Override
    public VoucherModifyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VoucherModifyAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
