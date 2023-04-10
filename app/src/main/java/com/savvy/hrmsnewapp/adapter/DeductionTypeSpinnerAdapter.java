package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.calendar.Beans;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeductionTypeSpinnerAdapter extends BaseAdapter {

    Context context;
    CustomTextView statusnames;
    List list;
    LayoutInflater inflter;
    ArrayList<HashMap<String, String>> arrayList;
    int adapterPosition;
    String spinValue;

    public DeductionTypeSpinnerAdapter(Context applicationContext, List list, String spinValue) {
        this.context = applicationContext;
        this.list = list;
        inflter = (LayoutInflater.from(applicationContext));
        this.spinValue = spinValue;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflter.inflate(R.layout.custom_array_spinner_adapter, null);
        statusnames = view.findViewById(R.id.tv_statusItem);

        if (position == 0) {
            statusnames.setText(spinValue);
        } else if (position > 0) {
            statusnames.setText(list.get(position - 1).toString());
        }

        return view;
    }
}
