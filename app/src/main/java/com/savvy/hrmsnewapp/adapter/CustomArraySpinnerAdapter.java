package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.calendar.Beans;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.List;

public class CustomArraySpinnerAdapter extends BaseAdapter {

    Context context;
    String str = "";
    String str1 = "";
    CustomTextView statusnames;
    Beans beans;

    private boolean isFromView = false;

    /*ArrayList<HashMap<String,String>> statusarraylist;*/
    List list;
    LayoutInflater inflter;


    public CustomArraySpinnerAdapter(Context applicationContext, List list) {
        this.context = applicationContext;

        /* this.statusarraylist = status;*/
        this.list = list;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return list.size() + 1;
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

        beans = new Beans(context);
        statusnames = view.findViewById(R.id.tv_statusItem);
        if (position == 0) {
            statusnames.setText("Action");
        } else if (position > 0) {
            statusnames.setText(list.get(position - 1).toString());

        }
        return view;
    }
}
