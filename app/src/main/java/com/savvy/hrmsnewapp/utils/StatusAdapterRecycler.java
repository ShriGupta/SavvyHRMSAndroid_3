package com.savvy.hrmsnewapp.utils;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.savvy.hrmsnewapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 8/24/2017.
 */

public class StatusAdapterRecycler extends RecyclerView.Adapter<StatusAdapterRecycler.MyViewHolder> {

    Context context;
    List<HashMap<String,String>> data;

    public StatusAdapterRecycler(Context context, List<HashMap<String,String>> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_spin_option,parent,false);
        return new StatusAdapterRecycler.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);
        holder.checkBox.setText(mapdata.get("Pending")+" "+position);
    }

    @Override
    public int getItemCount() {
        Log.e("Size",""+data.size());
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.hello);

        }
    }
}
