package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

public class TravelExpenseExpandListAdapter extends BaseExpandableListAdapter {

    Context context;
    CoordinatorLayout coordinatorLayout;
    private List<String> listDataHeader;
    private HashMap<String, List<HashMap<String, String>>> listDataChild;
    StringBuilder stringBuilder;

    public TravelExpenseExpandListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<HashMap<String, String>>> listDataChild) {

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
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        final HashMap<String, String> hashMap = (HashMap<String, String>) getChild(groupPosition, childPosition);


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.travel_expense_expand_list_adapter_row, null);
        }

        CustomTextView source = convertView.findViewById(R.id.travelExpenseSource);
        CustomTextView destination = convertView.findViewById(R.id.travelExpenseDestination);
        CustomTextView departureDate = convertView.findViewById(R.id.travelExpenseDepartureDate);
        CustomTextView returnDate = convertView.findViewById(R.id.travelExpenseReturnDate);
        CustomTextView mode = convertView.findViewById(R.id.travelExpenseMode);
        CustomTextView classValue = convertView.findViewById(R.id.travelExpenseClass);
        CustomTextView starttime = convertView.findViewById(R.id.travelExpenseStartTime);
        CustomTextView endtime = convertView.findViewById(R.id.travelExpenseEndTime);
        CustomTextView type = convertView.findViewById(R.id.travelExpenseType);

        source.setText(hashMap.get("DEPARTURE"));
        destination.setText(hashMap.get("RETURN"));
        departureDate.setText(hashMap.get("ID_DEPARTURE_DATE"));
        returnDate.setText(hashMap.get("ID_RETURN_DATE"));
        mode.setText(hashMap.get("MODE"));
        classValue.setText(hashMap.get("CLASS"));
        starttime.setText(hashMap.get("ID_START_TIME"));
        endtime.setText(hashMap.get("ID_END_TIME"));
        type.setText(hashMap.get("ID_TYPE"));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String getAccommodationStringData() {
        stringBuilder = new StringBuilder();
        if (listDataChild.get(listDataHeader.get(0)) != null) {
            int items = listDataChild.get(listDataHeader.get(0)).size();

            for (int i = 0; i < items; i++) {
                stringBuilder.append("@").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("city")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("fromdate")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("todate")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("checkintime")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("checkouttime")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("hotellocation"));
            }
            return stringBuilder.toString();
        } else {
            return "";
        }
    }
}

