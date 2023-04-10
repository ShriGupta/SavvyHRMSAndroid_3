package com.savvy.hrmsnewapp.SaleForceAdapter;

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
 * Created by orapc7 on 13-Jan-18.
 */

public class SaleForceMasterAdapter extends BaseAdapter {

    ArrayList<HashMap<String,String>> arlData;
    Context context;
    CustomTextView statusnames;
    String TYPE = "";

    public SaleForceMasterAdapter(Context context, ArrayList<HashMap<String,String>> arlData,String TYPE){
        this.context = context;
        this.arlData = arlData;
        this.TYPE = TYPE;
    }
    @Override
    public int getCount() {
        return arlData.size()+1;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinn_dropdown_item, null);
        statusnames = view.findViewById(R.id.txv_statusItem);
        if(position==0){
            if(TYPE.equals("INDUSTRY")) {
                statusnames.setText("Select Industry");
            } else if(TYPE.equals("LOCATION")) {
                statusnames.setText("Select Location");
            } else{
                statusnames.setText("Please Select");
            }
        } else if(position>0){
            statusnames.setText(arlData.get(position-1).get("PositionName"));
        }
        return view;
    }
}
