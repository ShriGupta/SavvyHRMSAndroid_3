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
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import java.util.HashMap;
import java.util.List;

public class PassangerDetailsAdapter extends BaseExpandableListAdapter {
    Context context;
    CoordinatorLayout coordinatorLayout;
    private List<String> listDataHeader;
    private HashMap<String, List<HashMap<String, String>>> childListData;
    StringBuilder stringBuilder;

    public PassangerDetailsAdapter(Context context, List<String> listDataHeader, HashMap<String, List<HashMap<String, String>>> childListData) {

        this.context = context;
        this.listDataHeader = listDataHeader;
        this.childListData = childListData;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childListData.size() == 0 ? 0 : childListData.get(listDataHeader.get(groupPosition)).size();
    }


    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childListData.get(listDataHeader.get(groupPosition)).get(childPosition);
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
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        final HashMap<String, String> hashMap = (HashMap<String, String>) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.passenger_details_adapter, null);
        }

        CustomTextView firstName = convertView.findViewById(R.id.txt_FirstName);
        CustomTextView middlename = convertView.findViewById(R.id.txt_MiddleName);
        CustomTextView lastname = convertView.findViewById(R.id.txt_LastName);
        CustomTextView contact = convertView.findViewById(R.id.txt_ContactNumber);
        CustomTextView address = convertView.findViewById(R.id.txt_Address);
        CustomTextView age = convertView.findViewById(R.id.txt_Age);
        CustomTextView gender = convertView.findViewById(R.id.txt_Gender);
        CustomTextView type = convertView.findViewById(R.id.txt_Type);
        Button removeButton = convertView.findViewById(R.id.removeButton);

        firstName.setText(hashMap.get("firstname"));
        middlename.setText(hashMap.get("middlename"));
        lastname.setText(hashMap.get("lastname"));
        contact.setText(hashMap.get("contact"));
        address.setText(hashMap.get("address"));
        age.setText(hashMap.get("age"));
        gender.setText(hashMap.get("gender"));
        type.setText(hashMap.get("employeetype")==null?"Self":hashMap.get("employeetype"));

        try {

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    final String id = childListData.get(listDataHeader.get(0)).get(childPosition).get("id");
                    if(id!=null)
                    {
                        android.os.AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseClient.getInstance(context).getAppDatabase().passengerDao().removePassengerDetail(Integer.valueOf(id));
                            }
                        });
                    }

                    childListData.get(listDataHeader.get(0)).remove(childPosition);
                    PassangerDetailsAdapter.this.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showDialog(coordinatorLayout, ErrorConstants.CODE_FAILURE);
        }


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String getPassengerStringData() {
        stringBuilder = new StringBuilder();
        if (childListData.get(listDataHeader.get(0)) != null) {
            int items = childListData.get(listDataHeader.get(0)).size();

            for (int i = 0; i < items; i++) {
                stringBuilder.append("@").
                        append(childListData.get(listDataHeader.get(0)).get(i).get("firstname")).append(",").
                        append(childListData.get(listDataHeader.get(0)).get(i).get("middlename")).append(",").
                        append(childListData.get(listDataHeader.get(0)).get(i).get("lastname")).append(",").
                        append(childListData.get(listDataHeader.get(0)).get(i).get("contact")).append(",").
                        append(childListData.get(listDataHeader.get(0)).get(i).get("address")).append(",").
                        append(childListData.get(listDataHeader.get(0)).get(i).get("age")).append(",").
                        append(childListData.get(listDataHeader.get(0)).get(i).get("gender")).append(",").
                        append(childListData.get(listDataHeader.get(0)).get(i).get("employeetype")).append(",").
                        append(childListData.get(listDataHeader.get(0)).get(i).get("foodtype"));
            }

            return stringBuilder.toString();

        } else {
            return "";
        }
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }
}
