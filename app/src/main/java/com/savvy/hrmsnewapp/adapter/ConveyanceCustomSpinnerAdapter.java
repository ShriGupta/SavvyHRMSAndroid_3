package com.savvy.hrmsnewapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.calendar.Beans;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by orapc7 on 7/20/2017.
 */

public class ConveyanceCustomSpinnerAdapter extends BaseAdapter {

    Context context;
    CustomTextView statusnames;

    ArrayList<HashMap<String,String>> statusarraylist;
    LayoutInflater inflter;

    String Type = "";

    public ConveyanceCustomSpinnerAdapter(String Type,Context applicationContext,  ArrayList<HashMap<String,String>> status) {
        this.context = applicationContext;
        this.Type = Type;

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

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = inflter.inflate(R.layout.conveyance_type_spinner, null);

        statusnames = view.findViewById(R.id.txv_statusItem);

        if(position==0){
            statusnames.setText("Please Select");

            if(Type.toUpperCase().equals("EXPENSE_MAIN")){
                statusnames.setText(statusarraylist.get(position).get("ET_EXPENSE_TYPE"));
                statusnames.setTextColor(R.color.color_gray);
            }
        } else if(position>0){
            if(Type.toUpperCase().equals("EXPENSE")){
                statusnames.setText(statusarraylist.get(position-1).get("EST_EXPENSE_SUB_TYPE"));
                Constants.EXPENSE_SPINNER.put(position,statusarraylist.get(position-1).get("EST_EXPENSE_SUB_TYPE"));
            } else if(Type.toUpperCase().equals("CONVEYANCE")){
                statusnames.setText(statusarraylist.get(position-1).get("EST_EXPENSE_SUB_TYPE"));
                Constants.CONVEYANCE_SPINEER.put(position,statusarraylist.get(position-1).get("EST_EXPENSE_SUB_TYPE"));
            } else if(Type.toUpperCase().equals("TRAVELTYPE")){
                statusnames.setText(statusarraylist.get(position-1).get("TT_TRAVEL_TYPE_NAME"));
            } else if(Type.toUpperCase().equals("TRAVELMODE")){
                statusnames.setText(statusarraylist.get(position-1).get("TM_TRAVEL_MODE_NAME"));
            } else if(Type.toUpperCase().equals("TRAVELCLASS")){
                statusnames.setText(statusarraylist.get(position-1).get("TC_TRAVEL_CLASS_NAME"));
            } else if(Type.toUpperCase().equals("COUNTRY")){
                statusnames.setText(statusarraylist.get(position-1).get("CM_COUNTRY_NAME"));
            } else if(Type.toUpperCase().equals("CITY")){
                statusnames.setText(statusarraylist.get(position-1).get("CM_CITY_NAME"));
            } else if(Type.toUpperCase().equals("EXPENSE_NATURE")){
                statusnames.setText(statusarraylist.get(position-1).get("TEN_TRAVEL_EXPENSE_NATURE_NAME"));
            } else if(Type.toUpperCase().equals("EXPENSE_MAIN")){
                statusnames.setText(statusarraylist.get(position-1).get("ET_EXPENSE_TYPE"));
            }  else if(Type.toUpperCase().equals("TRAVEL_EXPENSE_TYPE")){
                statusnames.setText(statusarraylist.get(position-1).get("EST_EXPENSE_SUB_TYPE"));
            }
        }
        return view;

    }

}
