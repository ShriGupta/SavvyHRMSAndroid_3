package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by orapc7 on 2/12/2017.
 */

public class GetCurrentLocationAdapter extends RecyclerView.Adapter<GetCurrentLocationAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String,String>> arldata;

    public GetCurrentLocationAdapter(Context context, ArrayList<HashMap<String,String>> arldata){
        this.context = context;
        this.arldata = arldata;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_visit_location_adaptar_row,parent,false);
        return new GetCurrentLocationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            HashMap<String,String> hashMap = arldata.get(position);

            holder.txt_getCurLocEmpName.setText(hashMap.get("SAVE_CUR_CUST_NAME"));
            holder.txt_getCurLocEmpLoc.setText(hashMap.get("SAVE_CUR_LAT"));
            holder.txt_getCurLocTime.setText("04:30 AM");

    }

    @Override
    public int getItemCount() {
        return arldata.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CustomTextView txt_getCurLocEmpName, txt_getCurLocEmpLoc, txt_getCurLocTime;
        public MyViewHolder(View itemView) {
            super(itemView);

            txt_getCurLocEmpName = itemView.findViewById(R.id.txt_getCurLocEmpName);
            txt_getCurLocEmpLoc = itemView.findViewById(R.id.txt_getCurLocEmpLoc);
            txt_getCurLocTime = itemView.findViewById(R.id.txt_getCurLocTime);
        }
    }
}
