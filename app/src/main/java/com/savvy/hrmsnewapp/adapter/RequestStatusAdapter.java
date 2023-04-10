package com.savvy.hrmsnewapp.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestStatusAdapter extends RecyclerView.Adapter<RequestStatusAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String,String>> arrayList;
     public RequestStatusAdapter(Context context,ArrayList<HashMap<String,String>> arrayList)
     {
         this.context=context;
         this.arrayList=arrayList;
     }

    @Override
    public RequestStatusAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.request_status_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestStatusAdapter.MyViewHolder holder, int position) {

             HashMap<String, String>  mapda = arrayList.get(position);
             holder.checkBox.setChecked(mapda.get("KEY")=="0"?true:false);
             holder.customTextView.setText(mapda.get("VALUE"));

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

         CheckBox checkBox;
         CustomTextView customTextView;
         public MyViewHolder(View itemView) {
            super(itemView);

            checkBox =itemView.findViewById(R.id.requestCheckBox);
             customTextView =itemView.findViewById(R.id.requestTextView);
        }
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
