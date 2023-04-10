package com.savvy.hrmsnewapp.adapter;

import android.app.Activity;
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
 * Created by hariom on 17/5/17.
 */
public class AlertListAdapter extends BaseAdapter {
    ArrayList<HashMap<String,String>> mData;
    Context mContext;
    LayoutInflater inflater;
    public AlertListAdapter(ArrayList<HashMap<String,String>>  data, Context context) {
        mData = data;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.support_simple_spinner_dropdown_item, null);
        }
        CustomTextView txv_leaveDateValue = convertView.findViewById(R.id.txv_leaveDateValue);
        CustomTextView txt_statusValue = convertView.findViewById(R.id.txt_statusValue);
        CustomTextView txt_deductiondaysValue = convertView.findViewById(R.id.txt_deductiondaysValue);

        txv_leaveDateValue.setText(mData.get(position).get("EAR_ATTENDANCE_DATE"));
        txt_statusValue.setText(mData.get(position).get("EAR_ATTENDANCE_STATUS"));

        txt_deductiondaysValue.setText(mData.get(position).get("DEDUCTION_DAYS"));

        return convertView;
    }
}
