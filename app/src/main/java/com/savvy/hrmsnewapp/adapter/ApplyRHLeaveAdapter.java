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

public class ApplyRHLeaveAdapter extends BaseAdapter {
    Context context;
    CustomTextView tv_HolidayName, tv_HolidayDate, tv_DayName;
    CustomTextView simple_textView;

    List<HashMap<String, String>> statusarraylist;
    LayoutInflater inflter;

    public ApplyRHLeaveAdapter(Context applicationContext, List<HashMap<String, String>> status) {
        this.context = applicationContext;

        this.statusarraylist = status;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return statusarraylist.size() + 2;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = inflter.inflate(R.layout.rh_leave_layout_row, null);

        tv_HolidayName = view.findViewById(R.id.tv_HolidayName);
        tv_HolidayDate = view.findViewById(R.id.tv_HolidayDate);
        tv_DayName = view.findViewById(R.id.tv_DayName);


        if (position == 0) {
            view = inflter.inflate(R.layout.simple_header, null);
            simple_textView = view.findViewById(R.id.simple_textView);
            simple_textView.setText("Please Select Holiday");

        } else if (position == 1) {
            view = inflter.inflate(R.layout.custom_rh_leave_header, null);
        } else {
            tv_HolidayName.setText(statusarraylist.get(position - 2).get("HM_HOLIDAY_NAME"));
            tv_HolidayDate.setText(statusarraylist.get(position - 2).get("HM_HOLIDAY_DATE"));
            tv_DayName.setText(statusarraylist.get(position - 2).get("DAY_NAME"));
        }
        return view;
    }
}
