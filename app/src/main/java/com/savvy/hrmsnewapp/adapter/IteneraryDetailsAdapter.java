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

public class IteneraryDetailsAdapter extends BaseExpandableListAdapter {

    Context context;
    CoordinatorLayout coordinatorLayout;
    private List<String> listDataHeader;
    private HashMap<String, List<HashMap<String, String>>> listDataChild;
    String travelWay;
    StringBuilder stringBuilder;

    public IteneraryDetailsAdapter(Context context, List<String> listDataHeader, HashMap<String, List<HashMap<String, String>>> listDataChild, String travelWay) {

        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.travelWay = travelWay;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        int value = listDataChild.size() == 0 ? 0 : listDataChild.get(listDataHeader.get(groupPosition)).size();
        return value;
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
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final HashMap<String, String> hashMap = (HashMap<String, String>) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.iteneray_details_row, null);
        }

        CustomTextView source = convertView.findViewById(R.id.txt_source);
        CustomTextView destination = convertView.findViewById(R.id.txt_Destination);
        CustomTextView departureDate = convertView.findViewById(R.id.txt_DepartureDate);
        CustomTextView mode = convertView.findViewById(R.id.txt_Mode);
        CustomTextView classcode = convertView.findViewById(R.id.txt_Class);
        CustomTextView starttime = convertView.findViewById(R.id.txt_StartTime);
        CustomTextView flightDetail = convertView.findViewById(R.id.txt_FlightDetrail);
        CustomTextView type = convertView.findViewById(R.id.txt_type);

        if (travelWay.equals("Round Trip") && childPosition == 1) {
            source.setText(hashMap.get("destination"));
            destination.setText(hashMap.get("source"));
            departureDate.setText(hashMap.get("returndate"));
            mode.setText(hashMap.get("mode"));
            classcode.setText(hashMap.get("class"));
            starttime.setText(hashMap.get("endtime"));
            flightDetail.setText(hashMap.get("fdetail"));
            type.setText(hashMap.get("travelwaytype"));

        } else {
            source.setText(hashMap.get("source"));
            destination.setText(hashMap.get("destination"));
            departureDate.setText(hashMap.get("departdate"));
            mode.setText(hashMap.get("mode"));
            classcode.setText(hashMap.get("class"));
            starttime.setText(hashMap.get("starttime"));
            flightDetail.setText(hashMap.get("fdetail"));
            type.setText(hashMap.get("travelwaytype"));
        }
        Button iteneraryRemoveButton = convertView.findViewById(R.id.iteneraryRemoveButton);
        try {

            iteneraryRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int id = Integer.valueOf(hashMap.get("id"));
                    android.os.AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseClient.getInstance(context).getAppDatabase().passengerDao().removeIteneraryDetail(id);
                        }
                    });
                    listDataChild.get(listDataHeader.get(0)).remove(childPosition);
                    IteneraryDetailsAdapter.this.notifyDataSetChanged();
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


    public String getItineraryStringData() {
        stringBuilder = new StringBuilder();

        if (listDataChild.get(listDataHeader.get(0)) != null) {
            int items = listDataChild.get(listDataHeader.get(0)).size();

            for (int i = 0; i < items; i++) {
                if (listDataChild.get(listDataHeader.get(0)).get(i).get("travelwaytype").equals("Round Trip")) {
                    if (i == 0) {
                        stringBuilder.append("@").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("sourceid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("destinationid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("departdate")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("returndate")).append(",").

                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("modeid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("classid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("starttime")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("endtime")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("travelwaytype")).append(",").

                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("fdetail")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("insurancevalue")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("seatprefid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("frequentflier")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("specialrequest"));


                    } else {
                        stringBuilder.append("@").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("sourceid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("destinationid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("returndate")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("departdate")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("modeid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("classid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("endtime")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("starttime")).append(",").

                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("travelwaytype")).append(",").

                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("fdetail")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("insurancevalue")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("seatprefid")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("frequentflier")).append(",").
                                append(listDataChild.get(listDataHeader.get(0)).get(i).get("specialrequest"));

                    }
                } else if (listDataChild.get(listDataHeader.get(0)).get(i).get("travelwaytype").equals("One Way")) {
                    stringBuilder.append("@").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("sourceid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("destinationid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("departdate")).append(",").
                            append("").append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("modeid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("classid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("starttime")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("endtime")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("travelwaytype")).append(",").

                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("fdetail")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("insurancevalue")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("seatprefid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("frequentflier")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("specialrequest"));


                } else {
                    stringBuilder.append("@").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("sourceid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("destinationid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("departdate")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("returndate")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("modeid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("classid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("starttime")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("endtime")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("travelwaytype")).append(",").

                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("fdetail")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("insurancevalue")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("seatprefid")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("frequentflier")).append(",").
                            append(listDataChild.get(listDataHeader.get(0)).get(i).get("specialrequest"));

                }
            }
            return stringBuilder.toString();
        } else {
            return "";
        }

    }
}
