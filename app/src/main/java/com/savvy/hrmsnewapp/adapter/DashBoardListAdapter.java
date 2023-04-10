package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by orapc7 on 7/3/2017.
 */

public class DashBoardListAdapter extends BaseExpandableListAdapter {


    private Context _context;
    private List<String> _listDataHeader;
    HashMap<String, ArrayList<HashMap<String, String>>> listNewData;
    ArrayList<HashMap<String, String>> arlData1;
    ArrayList<HashMap<String, String>> arlData_announcement;
    ArrayList<HashMap<String, String>> arlData_Birthday;
    ArrayList<HashMap<String, String>> arlData_Anniversary;
    ArrayList<HashMap<String, String>> arlData_Join_Anniversary;
    ArrayList<HashMap<String, String>> arlData_ThoughtOfDay;
    ExpandableListView expListView;
    SharedPreferences shareHoliday;
    List<HashMap<String, String>> data;

    SharedPreferences sharedpreferencesIP;
    SharedPreferences.Editor editor;
    String CONSTANT_IP_ADDRESS_PHOTO_CODE = "";

    int Birthday_Count = 0;
    int Anniversary_Count = 0;
    int Announcement_Count = 0;
    int Thought_Count = 0;
    int Holiday_Count = 0;
    int flag = 0;
    int counter = 0;
    Date date;
    SimpleDateFormat sdf;
    String currentDate = "";

    String holidayName = "", holidayDate = "";

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    HashMap<String, String> mapData;

    public DashBoardListAdapter(ExpandableListView expListView, Context context, List<String> listDataHeader, HashMap<String, ArrayList<HashMap<String, String>>> listNewData,
                                ArrayList<HashMap<String, String>> arlData1,
                                ArrayList<HashMap<String, String>> arlData_announcement, ArrayList<HashMap<String, String>> arlData_Birthday,
                                ArrayList<HashMap<String, String>> arlData_Anniversary, ArrayList<HashMap<String, String>> arlData_ThoughtOfDay,
                                ArrayList<HashMap<String, String>> arlData_Join_Anniversary, String currentDate) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this.listNewData = listNewData;
        this.arlData1 = arlData1;
        this.arlData_announcement = arlData_announcement;
        this.arlData_Birthday = arlData_Birthday;
        this.arlData_Anniversary = arlData_Anniversary;
        this.arlData_ThoughtOfDay = arlData_ThoughtOfDay;
        this.arlData_Join_Anniversary = arlData_Join_Anniversary;

        this.expListView = expListView;
        mapData = new HashMap<String, String>();
        shareHoliday = _context.getSharedPreferences("", MODE_PRIVATE);
        this.currentDate = currentDate;


    }


    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        counter = listNewData.get(this._listDataHeader.get(groupPosition)).size();
        if (counter == 0) {
            return 1;
        } else {
            return this.listNewData.get(this._listDataHeader.get(groupPosition)).size();
        }
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
        final int New_Group_Position = groupPosition;

        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        final TextView headerCount = convertView.findViewById(R.id.headerCount);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (New_Group_Position == 0) {
//            headerCount.setText(""+ arlData_Birthday.size());
                    headerCount.setText("" + Constants.Birthday_Count);
                } else if (New_Group_Position == 1) {
//            headerCount.setText("" + arlData_Anniversary.size());
                    headerCount.setText("" + Constants.Anniversary_Count);
                } else if (New_Group_Position == 2) {
                    headerCount.setText("" + arlData1.size());
//            headerCount.setText("" +Constants.Holiday_Count );
                } else if (New_Group_Position == 3) {
//            headerCount.setText("" + arlData_announcement.size());
                    headerCount.setText("" + Constants.Announcement_Count);
                } else if (New_Group_Position == 4) {
//            headerCount.setText(""+arlData_ThoughtOfDay.size());
                    headerCount.setText("" + Constants.Thought_Count);
                } else if (New_Group_Position == 5) {
//            headerCount.setText(""+arlData_ThoughtOfDay.size());
                    headerCount.setText("" + Constants.Join_Anniversary_Count);
                }
            }
        }, 1000 * 1);


        if (isExpanded) {
            ImageView imageView = convertView.findViewById(R.id.imgbase);
            imageView.setImageResource(R.drawable.up_arrow);
        } else {
            ImageView imageView = convertView.findViewById(R.id.imgbase);
            imageView.setImageResource(R.drawable.down_arrow);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        final String childText = (String) getChild(groupPosition, childPosition);
        final String groupText1 = (String) getGroup(groupPosition);

        if (groupText1.equals("BirthDay") || groupPosition == 0) {
//                if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.dashboard_list_item, null);
//                }
            LinearLayout cardView = convertView.findViewById(R.id.card_birthday_anniversary);
            LinearLayout linearLayout = convertView.findViewById(R.id.linear_card_not);
            CustomTextView noDataFound = convertView.findViewById(R.id.no_data_found);
            ImageView imageView = convertView.findViewById(R.id.birthday_icon);
            CustomTextView empCode = convertView.findViewById(R.id.emp_code_value);
            CustomTextView empName = convertView.findViewById(R.id.emp_name_value);
            CustomTextView department = convertView.findViewById(R.id.department_value);
            CustomTextView designation = convertView.findViewById(R.id.designation_value);

            if (arlData_Birthday.size() == 0) {
                cardView.setVisibility(View.GONE);
                noDataFound.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                sharedpreferencesIP = _context.getSharedPreferences("IP_ADDRESS_CONSTANT", MODE_PRIVATE);
                Constants.IP_ADDRESS = sharedpreferencesIP.getString("IP_ADDRESS", "");
                Constants.IP_ADDRESS_STATUS = sharedpreferencesIP.getBoolean("IP_ADDRESS_STATUS", false);
                CONSTANT_IP_ADDRESS_PHOTO_CODE = sharedpreferencesIP.getString("IP_ADDRESS_PHOTO_CODE", "");

                cardView.setVisibility(View.VISIBLE);
                noDataFound.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);

                mapData = arlData_Birthday.get(childPosition);

                String empCode1 = mapData.get("EMPLOYEE_CODE");
                String photourl = null;

                if (Constants.COMPANY_STATUS_PHOTO_CODE.equals("GENERAL")) {
                    photourl = "http://" + CONSTANT_IP_ADDRESS_PHOTO_CODE + "/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
                } else {
                    photourl = "http://" + Constants.PHOTO_CODE_IP_ADDRESS + "/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
                }
//                String photourl = "http://savvyhrms.com/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";

                empCode.setText(mapData.get("EMPLOYEE_CODE"));
                empName.setText(mapData.get("EMPLOYEE_NAME"));
                department.setText(mapData.get("D_DEPARTMENT_NAME"));
                designation.setText(mapData.get("D_DESIGNATION"));

                Picasso.get().load(photourl).into(imageView);
            }


        } else if (groupText1.equals("Marriage Anniversary") || groupPosition == 1) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.dashboard_list_item, null);

            LinearLayout cardView = convertView.findViewById(R.id.card_birthday_anniversary);
            LinearLayout linearLayout = convertView.findViewById(R.id.linear_card_not);
            CustomTextView noDataFound = convertView.findViewById(R.id.no_data_found);
            ImageView imageView = convertView.findViewById(R.id.birthday_icon);
            CustomTextView empCode = convertView.findViewById(R.id.emp_code_value);
            CustomTextView empName = convertView.findViewById(R.id.emp_name_value);
            CustomTextView department = convertView.findViewById(R.id.department_value);
            CustomTextView designation = convertView.findViewById(R.id.designation_value);

            if (arlData_Anniversary.size() == 0) {
                cardView.setVisibility(View.GONE);
                noDataFound.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                sharedpreferencesIP = _context.getSharedPreferences("IP_ADDRESS_CONSTANT", MODE_PRIVATE);
                Constants.IP_ADDRESS = sharedpreferencesIP.getString("IP_ADDRESS", "");
                Constants.IP_ADDRESS_STATUS = sharedpreferencesIP.getBoolean("IP_ADDRESS_STATUS", false);
                CONSTANT_IP_ADDRESS_PHOTO_CODE = sharedpreferencesIP.getString("IP_ADDRESS_PHOTO_CODE", "");

                cardView.setVisibility(View.VISIBLE);
                noDataFound.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);

                mapData = arlData_Anniversary.get(childPosition);

                String empCode1 = mapData.get("EMPLOYEE_CODE");
                String photourl = null;
                if (Constants.COMPANY_STATUS_PHOTO_CODE.equals("GENERAL")) {
                    photourl = "http://" + CONSTANT_IP_ADDRESS_PHOTO_CODE + "/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
                } else {
                    photourl = "http://" + Constants.PHOTO_CODE_IP_ADDRESS + "/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
                }

                empCode.setText(mapData.get("EMPLOYEE_CODE"));
                empName.setText(mapData.get("EMPLOYEE_NAME"));
                department.setText(mapData.get("D_DEPARTMENT_NAME"));
                designation.setText(mapData.get("D_DESIGNATION"));
                Picasso.get().load(photourl).into(imageView);
            }


        } else if (groupText1.equals("Joining Anniversary") || groupPosition == 5) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.dashboard_list_item, null);

            LinearLayout cardView = convertView.findViewById(R.id.card_birthday_anniversary);
            LinearLayout linearLayout = convertView.findViewById(R.id.linear_card_not);
            CustomTextView noDataFound = convertView.findViewById(R.id.no_data_found);
            ImageView imageView = convertView.findViewById(R.id.birthday_icon);
            CustomTextView empCode = convertView.findViewById(R.id.emp_code_value);
            CustomTextView empName = convertView.findViewById(R.id.emp_name_value);
            CustomTextView department = convertView.findViewById(R.id.department_value);
            CustomTextView designation = convertView.findViewById(R.id.designation_value);

            if (arlData_Join_Anniversary.size() == 0) {
                cardView.setVisibility(View.GONE);
                noDataFound.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                sharedpreferencesIP = _context.getSharedPreferences("IP_ADDRESS_CONSTANT", MODE_PRIVATE);
                Constants.IP_ADDRESS = sharedpreferencesIP.getString("IP_ADDRESS", "");
                Constants.IP_ADDRESS_STATUS = sharedpreferencesIP.getBoolean("IP_ADDRESS_STATUS", false);
                CONSTANT_IP_ADDRESS_PHOTO_CODE = sharedpreferencesIP.getString("IP_ADDRESS_PHOTO_CODE", "");

                cardView.setVisibility(View.VISIBLE);
                noDataFound.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);

                mapData = arlData_Join_Anniversary.get(childPosition);

                String empCode1 = mapData.get("EMPLOYEE_CODE");
                String photourl = null;
                if (Constants.COMPANY_STATUS_PHOTO_CODE.equals("GENERAL")) {
                    photourl = "http://" + CONSTANT_IP_ADDRESS_PHOTO_CODE + "/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
                } else {
                    photourl = "http://" + Constants.PHOTO_CODE_IP_ADDRESS + "/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
                }

                empCode.setText(mapData.get("EMPLOYEE_CODE"));
                empName.setText(mapData.get("EMPLOYEE_NAME"));
                department.setText(mapData.get("D_DEPARTMENT_NAME"));
                designation.setText(mapData.get("D_DESIGNATION"));
                Picasso.get().load(photourl).into(imageView);
            }


        }
        else if (groupText1.equals("Holidays") || groupPosition == 2) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.holiday_list_row_new, null);

            CustomTextView holidayTitle = convertView.findViewById(R.id.holidaytitle);
            CustomTextView holidayDate1 = convertView.findViewById(R.id.holidayDate);
            CustomTextView holidayDayName = convertView.findViewById(R.id.holiday_dayName);

            LinearLayout linearLayoutImage1 = convertView.findViewById(R.id.linearHolidayBack);
//            LinearLayout linearLayoutImage2 = (LinearLayout)convertView.findViewById(R.id.image_data1);
            TextView noDataFound1 = convertView.findViewById(R.id.holiday_no_data_found);


            Log.e("Holiday List ", "" + arlData1.size());
            Log.e("CustomText", holidayDate1.getText().toString());
            System.out.println("CustomText  " + holidayDate1.getText().toString());

            if (arlData1.size() == 0) {
                linearLayoutImage1.setVisibility(View.GONE);
                noDataFound1.setVisibility(View.VISIBLE);
            } else {
                try {
                    linearLayoutImage1.setVisibility(View.VISIBLE);
                    noDataFound1.setVisibility(View.GONE);

                    mapData = arlData1.get(childPosition);
                    holidayTitle.setText(mapData.get("holidayName"));
                    holidayDate1.setText(mapData.get("holidayDate"));
                    holidayDayName.setText(mapData.get("dayName"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (groupText1.equals("Thought of the Day") || groupPosition == 3) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.announcement_birthday, null);

            CustomTextView announce = convertView.findViewById(R.id.announcement_birthday);
            CustomTextView noDataFound = convertView.findViewById(R.id.no_data_found1);

            if (arlData_announcement.size() == 0) {
                announce.setVisibility(View.GONE);
                noDataFound.setVisibility(View.VISIBLE);
            } else {
                announce.setVisibility(View.VISIBLE);
                noDataFound.setVisibility(View.GONE);

                mapData = arlData_announcement.get(childPosition);
//                date = new Date();
//                sdf = new SimpleDateFormat("dd-MM-yyy", Locale.UK);
////        currentDate = sdf.format(date);
//                String newDateFormat = "<font color='#EE0000' font-style='bold'>"+sdf.format(date)+"</font>";
//                currentDate = String.valueOf(Html.fromHtml(newDateFormat));
                announce.setText(mapData.get("AS_MESSAGE") + " -> " + Html.fromHtml(currentDate));
            }


        } else if (groupText1.equals("Announcement") || groupPosition == 4) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.announcement_birthday, null);

            CustomTextView announce = convertView.findViewById(R.id.announcement_birthday);
            CustomTextView noDataFound = convertView.findViewById(R.id.no_data_found1);

            if (arlData_ThoughtOfDay.size() == 0) {
                announce.setVisibility(View.GONE);
                noDataFound.setVisibility(View.VISIBLE);
            } else {
                announce.setVisibility(View.VISIBLE);
                noDataFound.setVisibility(View.GONE);

                mapData = arlData_ThoughtOfDay.get(childPosition);
                announce.setText(mapData.get("AS_MESSAGE") + " -> " + Html.fromHtml(currentDate));
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

