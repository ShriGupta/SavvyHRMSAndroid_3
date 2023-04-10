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

public class SpinnerCustomAdapter extends BaseAdapter {

    Context context;
    String str = "";
    String str1 = "";
    CustomTextView statusnames;
    Beans beans;

    private boolean isFromView = false;

    ArrayList<HashMap<String, String>> statusarraylist;
    LayoutInflater inflter;

    public SpinnerCustomAdapter(Context applicationContext, ArrayList<HashMap<String, String>> status) {
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
        View view = inflter.inflate(R.layout.spinner_custom_adapter, null);

        beans = new Beans(context);
        statusnames = view.findViewById(R.id.spinnerItem);
        if (position == 0) {
            statusnames.setText("Select Employee Code & Name");
        } else if (position > 0) {
            //ImageView icon = (ImageView) view.findViewById(R.id.imageView);
            statusnames.setText(statusarraylist.get(position - 1).get("VALUE"));
        }
        return view;

    }
}
