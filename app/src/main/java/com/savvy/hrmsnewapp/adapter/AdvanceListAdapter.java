package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;

import java.util.HashMap;
import java.util.List;

public class AdvanceListAdapter extends BaseExpandableListAdapter {


    Context context;
    CoordinatorLayout coordinatorLayout;
    private List<String> listDataHeader;
    private HashMap<String, List<HashMap<String, String>>> listDataChild;
    StringBuilder stringBuilder;

    public AdvanceListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<HashMap<String, String>>> listDataChild) {

        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.size() == 0 ? 0 : listDataChild.get(listDataHeader.get(groupPosition)).size();
    }


    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
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
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_title, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.listTitle);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText("Advance Details");

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final HashMap<String, String> hashMap = (HashMap<String, String>) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.advance_list_row, null);
        }

        CustomTextView advance = convertView.findViewById(R.id.txt_AdvanceAmount);
        CustomTextView currency = convertView.findViewById(R.id.txt_Currency);
        CustomTextView paymode = convertView.findViewById(R.id.txt_PayMode);
        CustomTextView remarks = convertView.findViewById(R.id.txt_Remark);

        Button remove = convertView.findViewById(R.id.advanceRemoveButton);

        advance.setText(hashMap.get("amount"));
        currency.setText(hashMap.get("currency"));
        paymode.setText(hashMap.get("paymode"));
        remarks.setText(hashMap.get("remarks"));

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = Integer.valueOf(hashMap.get("id"));
                android.os.AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseClient.getInstance(context).getAppDatabase().passengerDao().removeAdvanceData(id);
                    }
                });
                listDataChild.get(listDataHeader.get(0)).remove(childPosition);
                AdvanceListAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String getAdvanceStringData() {
        stringBuilder = new StringBuilder();
        if (listDataChild.get(listDataHeader.get(0)) != null) {
            int items = listDataChild.get(listDataHeader.get(0)).size();
            for (int i = 0; i < items; i++) {

                stringBuilder.append("@").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("currencyid")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("paymodeid")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("remarks")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("amount"));

            }
            return stringBuilder.toString();
        } else {
            return "";
        }
    }
}
