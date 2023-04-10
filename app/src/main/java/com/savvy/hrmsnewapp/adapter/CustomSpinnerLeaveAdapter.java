package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by orapc7 on 5/18/2017.
 */

public class CustomSpinnerLeaveAdapter extends BaseAdapter {

    Context context;

    ArrayList<HashMap<String,String>> statusarraylist;
    LayoutInflater inflter;

    public CustomSpinnerLeaveAdapter(Context applicationContext,  ArrayList<HashMap<String,String>> status) {
        this.context = applicationContext;

        this.statusarraylist = status;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return statusarraylist.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinn_dropdown_item, null);
        //ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        CustomTextView statusLeave = view.findViewById(R.id.txv_statusItem);
        //icon.setImageResource(flags[i]);
        if(i==0){
            statusLeave.setText("Please Select");
        } else if(i>0){
            //ImageView icon = (ImageView) view.findViewById(R.id.imageView);
            statusLeave.setText(statusarraylist.get(i-1).get("Reason"));
        }
//        statusLeave.setText(statusarraylist.get(i).get("Reason"));
        return view;
    }



}