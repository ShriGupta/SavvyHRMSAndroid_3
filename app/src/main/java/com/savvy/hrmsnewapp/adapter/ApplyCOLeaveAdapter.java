package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

public class ApplyCOLeaveAdapter extends BaseAdapter implements SpinnerAdapter {

    Context context;
    List<HashMap<String, String>> coLeaveListData;
    LayoutInflater inflter;
    CustomTextView coDate, coDays, coDayName;
    CustomTextView textView;


    public ApplyCOLeaveAdapter(Context context, List<HashMap<String, String>> coLeaveListData) {
        this.context = context;
        this.coLeaveListData = coLeaveListData;
        inflter = (LayoutInflater.from(context));

    }

    @Override
    public int getCount() {
        return coLeaveListData.size() + 2;
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
    public View getView(int position, View view1, ViewGroup viewGroup) {
        View view = inflter.inflate(R.layout.co_leave_data_layout, null);

        coDate = view.findViewById(R.id.tv_coDate);
        coDays = view.findViewById(R.id.tv_days);
        coDayName = view.findViewById(R.id.tv_coleaveDayName);

        if (position == 0) {
            view = inflter.inflate(R.layout.simple_header, null);
            textView = view.findViewById(R.id.simple_textView);
        } else if (position == 1) {
            view = inflter.inflate(R.layout.co_leave_custom_header, null);
        } else {

            String codate = coLeaveListData.get(position - 2).get("COR_COMPOFF_DATE");
            String codays = coLeaveListData.get(position - 2).get("DAYS");
            String codayname = coLeaveListData.get(position - 2).get("DAY_NAME");
            coDate.setText(codate);
            coDays.setText(codays);
            coDayName.setText(codayname);

        }
        return view;
    }
}
