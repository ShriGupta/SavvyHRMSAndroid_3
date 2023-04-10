package com.savvy.hrmsnewapp.adapter;

/**
 * Created by orapc7 on 5/12/2017.
 */

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

public class NewCustomSpinnerAdapter extends BaseAdapter {

    Context context;
    CustomTextView statusnames;
    Beans beans;
    ArrayList<HashMap<String, String>> statusarraylist;
    LayoutInflater inflter;

    public NewCustomSpinnerAdapter(Context applicationContext, ArrayList<HashMap<String, String>> status) {
        this.context = applicationContext;

        this.statusarraylist = status;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return statusarraylist.size() + 1;
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
        View view = inflter.inflate(R.layout.new_custom_spinner_adapter_row, null);

        beans = new Beans(context);
        statusnames = view.findViewById(R.id.txtSpinnerValue);
        if (position == 0) {
            statusnames.setText("Please Select");
        } else if (position > 0) {
            statusnames.setText(statusarraylist.get(position - 1).get("VALUE"));
        }
        return view;

    }
}
