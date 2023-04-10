package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 7/5/2017.
 */

public class DashBoardHolidayListAdapter extends BaseExpandableListAdapter {


    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    HashMap<String, ArrayList<HashMap<String,String>>> listNewData;
    HashMap<String, HashMap<String, String>> listDataChild1;
    ArrayList<HashMap<String,String>> arlData;
    ExpandableListView expListView;
    SharedPreferences shareHoliday;
    List<HashMap<String,String>> data;

    int flag = 0;
    int counter = 0;

    String holidayName = "", holidayDate = "";

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    HashMap<String, String> mapData;

    public DashBoardHolidayListAdapter(Context context, List<String> listDataHeader, HashMap<String, ArrayList<HashMap<String,String>>> listNewData, ArrayList<HashMap<String,String>> arlData){
        this._context = context;
        this._listDataHeader = listDataHeader;
        this.listNewData = listNewData;
        this.arlData = arlData;
        mapData  = new HashMap<String, String>();

    }


    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        if(flag==1) {
        counter=listNewData.get(this._listDataHeader.get(groupPosition)).size();
        Log.e("Size",""+counter);
        return this.listNewData.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listNewData.get(this._listDataHeader.get(groupPosition)).get(childPosition);
//        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.dashboard_group_item, null);
        }


        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        TextView headerCount = convertView.findViewById(R.id.headerCount);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        headerCount.setText(""+arlData.size());

        if(isExpanded){
            ImageView imageView = convertView.findViewById(R.id.imgbase);
            imageView.setImageResource(R.drawable.up_arrow);
        }else{
            ImageView imageView = convertView.findViewById(R.id.imgbase);
            imageView.setImageResource(R.drawable.down_arrow);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        final String childText = (String) getChild(groupPosition, childPosition);
        final  String groupText1 = (String)getGroup(groupPosition);

       if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.dashboard_list_item, null);
            }
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.holidaylist_row, null);
        }
        CustomTextView holidayTitle = convertView.findViewById(R.id.holidaytitle);
        CustomTextView holidayDate1 = convertView.findViewById(R.id.holidayDate);

            mapData = arlData.get(childPosition);
            holidayTitle.setText(mapData.get("holidayName"));
            holidayDate1.setText(mapData.get("holidayDate"));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}


