package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class BirthDayAndAnniversaryAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    SharedPreferences sharedpreferencesIP;
    SharedPreferences.Editor editor;
    String CONSTANT_IP_ADDRESS_PHOTO_CODE = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    ArrayList<HashMap<String, String>> arrayList;
    String type;
    private static final int LAYOUT_ONE = 0;
    private static final int LAYOUT_TWO = 1;
    private static final int LAYOUT_THREE = 2;

    public BirthDayAndAnniversaryAdaptor(Context context, ArrayList<HashMap<String, String>> arrayList, String type) {

        this.context = context;
        this.arrayList = arrayList;
        this.type = type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == LAYOUT_TWO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holiday_list_row_new, parent, false);
            viewHolder = new ViewHolderTwo(view);
        } else if (viewType == LAYOUT_THREE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_birthday, parent, false);
            viewHolder = new ViewHolderThree(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_list_item, parent, false);
            viewHolder = new ViewHolderOne(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HashMap<String, String> mapData = arrayList.get(position);

        if (holder.getItemViewType() == LAYOUT_TWO) {

            ViewHolderTwo holder1 = (ViewHolderTwo) holder;
            if (arrayList.size() == 0) {
                holder1.linearLayoutImage1.setVisibility(View.GONE);
                holder1.noDataFound1.setVisibility(View.VISIBLE);
            } else {
                try {
                    holder1.linearLayoutImage1.setVisibility(View.VISIBLE);
                    holder1.noDataFound1.setVisibility(View.GONE);
                    holder1.holidayTitle.setText(mapData.get("holidayName"));
                    holder1.holidayDate1.setText(mapData.get("holidayDate"));
                    holder1.holidayDayName.setText(mapData.get("dayName"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else if (holder.getItemViewType() == LAYOUT_THREE) {
            ViewHolderThree holder1 = (ViewHolderThree) holder;
            holder1.announce.setText(mapData.get("AS_MESSAGE"));
        } else {
            ViewHolderOne holder1 = (ViewHolderOne) holder;

            if (arrayList.size() == 0) {
                holder1.cardView.setVisibility(View.GONE);
                holder1.noDataFound.setVisibility(View.VISIBLE);
                holder1.linearLayout.setVisibility(View.VISIBLE);
            } else {
                sharedpreferencesIP = context.getSharedPreferences("IP_ADDRESS_CONSTANT", MODE_PRIVATE);
                Constants.IP_ADDRESS = sharedpreferencesIP.getString("IP_ADDRESS", "");
                Constants.IP_ADDRESS_STATUS = sharedpreferencesIP.getBoolean("IP_ADDRESS_STATUS", false);
                CONSTANT_IP_ADDRESS_PHOTO_CODE = sharedpreferencesIP.getString("IP_ADDRESS_PHOTO_CODE", "");

                holder1.cardView.setVisibility(View.VISIBLE);
                holder1.noDataFound.setVisibility(View.GONE);
                holder1.linearLayout.setVisibility(View.GONE);

                String empCode1 = mapData.get("EMPLOYEE_CODE");
                String photourl = null;
                if (Constants.COMPANY_STATUS_PHOTO_CODE.equals("GENERAL")) {
                    photourl = "http://" + CONSTANT_IP_ADDRESS_PHOTO_CODE + "/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
                } else {
                    photourl = "http://" + Constants.PHOTO_CODE_IP_ADDRESS + "/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
                }

                holder1.empCode.setText(mapData.get("EMPLOYEE_CODE"));
                holder1.empName.setText(mapData.get("EMPLOYEE_NAME"));
                holder1.department.setText(mapData.get("D_DEPARTMENT_NAME"));
                holder1.designation.setText(mapData.get("D_DESIGNATION"));
                holder1.joiningDate.setText(mapData.get("ACTUAL_DATE"));
                String joinYearValue = mapData.get("TENURE");
                if (joinYearValue != null) {
                    if (joinYearValue.equals("1")) {
                        joinYearValue = "Congratulations for completing " + mapData.get("TENURE") + " Year in Organisation.";
                    } else {
                        joinYearValue = "Congratulations for completing " + mapData.get("TENURE") + " Years in Organisation.";
                    }
                    holder1.joiningYear.setText(joinYearValue);
                }

                Picasso.get().load(photourl).into(holder1.imageView);
            }
        }
    }


    public class ViewHolderOne extends RecyclerView.ViewHolder {

        LinearLayout cardView, linearLayout;
        CustomTextView noDataFound, empCode, empName, department, designation, joiningDate, joiningYear;
        ImageView imageView;

        public ViewHolderOne(View convertView) {
            super(convertView);

            cardView = convertView.findViewById(R.id.card_birthday_anniversary);
            linearLayout = convertView.findViewById(R.id.linear_card_not);
            noDataFound = convertView.findViewById(R.id.no_data_found);
            imageView = convertView.findViewById(R.id.birthday_icon);
            empCode = convertView.findViewById(R.id.emp_code_value);
            empName = convertView.findViewById(R.id.emp_name_value);
            department = convertView.findViewById(R.id.department_value);
            designation = convertView.findViewById(R.id.designation_value);
            joiningDate = convertView.findViewById(R.id.joiningdate_value);
            joiningYear = convertView.findViewById(R.id.joiningyearValue);

        }
    }

    public class ViewHolderTwo extends RecyclerView.ViewHolder {
        CustomTextView holidayTitle, holidayDate1, holidayDayName;
        LinearLayout linearLayoutImage1;
        TextView noDataFound1;

        public ViewHolderTwo(View itemView) {
            super(itemView);
            holidayTitle = itemView.findViewById(R.id.holidaytitle);
            holidayDate1 = itemView.findViewById(R.id.holidayDate);
            holidayDayName = itemView.findViewById(R.id.holiday_dayName);
            linearLayoutImage1 = itemView.findViewById(R.id.linearHolidayBack);
            noDataFound1 = itemView.findViewById(R.id.holiday_no_data_found);

        }
    }

    public class ViewHolderThree extends RecyclerView.ViewHolder {

        CustomTextView announce, noDataFound;

        public ViewHolderThree(View itemView) {
            super(itemView);


            announce = itemView.findViewById(R.id.announcement_birthday);
            noDataFound = itemView.findViewById(R.id.no_data_found1);

            if (arrayList.size() == 0) {
                announce.setVisibility(View.GONE);
                noDataFound.setVisibility(View.VISIBLE);
            } else {
                announce.setVisibility(View.VISIBLE);
                noDataFound.setVisibility(View.GONE);

            }
        }

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (type.equals("holiday_Data")) {
            return LAYOUT_TWO;
        } else if (type.equals("arlData_Announcement") || type.equals("arlData_ThoughtOfDay")) {
            return LAYOUT_THREE;
        } else
            return LAYOUT_ONE;
    }
}


