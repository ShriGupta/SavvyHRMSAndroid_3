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

public class CarPickUpListAdapter extends BaseExpandableListAdapter {
    Context context;
    CoordinatorLayout coordinatorLayout;
    private List<String> listDataHeader;
    private HashMap<String, List<HashMap<String, String>>> listDataChild;
    StringBuilder stringBuilder;

    public CarPickUpListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<HashMap<String, String>>> listChildData) {

        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
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
        lblListHeader.setText("Car Pick Up Details");

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        final HashMap<String, String> hashMap = (HashMap<String, String>) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.car_pickup_list_row, null);
        }

        CustomTextView txt_PickupAt = convertView.findViewById(R.id.txt_PickupAt);
        CustomTextView txt_DropAt = convertView.findViewById(R.id.txt_DropAt);
        CustomTextView txt_Pickuptime = convertView.findViewById(R.id.txt_Pickuptime);
        CustomTextView txt_ReleaseTime = convertView.findViewById(R.id.txt_ReleaseTime);
        CustomTextView txt_PickupDate = convertView.findViewById(R.id.txt_PickupDate);
        CustomTextView txt_Comment = convertView.findViewById(R.id.txt_Comment);

        Button removeButton = convertView.findViewById(R.id.carDetailRemoveButton);

        txt_PickupAt.setText(hashMap.get("pickupat"));
        txt_DropAt.setText(hashMap.get("dropat"));
        txt_Pickuptime.setText(hashMap.get("pickuptime"));
        txt_ReleaseTime.setText(hashMap.get("releasetime"));
        txt_PickupDate.setText(hashMap.get("pickupdate"));
        txt_Comment.setText(hashMap.get("comment"));

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int id = Integer.valueOf(hashMap.get("id"));
                android.os.AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseClient.getInstance(context).getAppDatabase().passengerDao().removeCarDetail(id);
                    }
                });
                listDataChild.get(listDataHeader.get(0)).remove(childPosition);
                CarPickUpListAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String getCarPickupStringData() {
        stringBuilder = new StringBuilder();

        if (listDataChild.get(listDataHeader.get(0)) != null) {
            int items = listDataChild.get(listDataHeader.get(0)).size();
            for (int i = 0; i < items; i++) {
                stringBuilder.append("@").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("pickupat")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("pickupdate")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("dropat")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("pickuptime")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("releasetime")).append(",").
                        append(listDataChild.get(listDataHeader.get(0)).get(i).get("comment"));
            }
            return stringBuilder.toString();
        } else {
            return "";
        }

    }
}
