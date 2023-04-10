package com.savvy.hrmsnewapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 6/7/2017.
 */

public class CalendarAdapter extends BaseAdapter {
    static final int FIRST_DAY_OF_WEEK =0; // Sunday = 0, Monday = 1

    int FIRST_DAY = 0;
    private Context mContext;
    CalendarAdapter mAdapter;
    List<HashMap<String,String>> data ;
    HashMap<String, String> mapdata;
    int dateIndex;
    int newIndex;

    int arlDataSize;

    private java.util.Calendar month;
    private Calendar selectedDate;
    private ArrayList<String> items;
    CustomTextView txt_status, txt_timeIn, txt_timeOut;

    public CalendarAdapter(Context c, Calendar monthCalendar,List<HashMap<String, String>> data) {
        month = monthCalendar;
        selectedDate = (Calendar)monthCalendar.clone();
        mContext = c;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();
        this.data = data;
        this.dateIndex = dateIndex;
        this.arlDataSize = data.size();
//        newIndex = data.size()+(dateIndex-1);
        refreshDays();
    }

    public void setItems(ArrayList<String> items) {
        for(int i = 0;i != items.size();i++){
            if(items.get(i).length()==1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }


    public int getCount() {

//        if(days.length==30){
//            return 30;
//        }else if(days.length==31) {
//            return 31;
//        }
//        else if(days.length==28) {
//            return 28;
//        }
       //return data.size();

        int posi = days.length;
        Log.e("Days Length","Length = "+days.length);
        if(posi<=35){
            int realIndex = 35-posi;
            posi = posi+realIndex;
        }
        else if(posi<=42){
            int realIndex = 42-posi;
            posi = posi+realIndex;
        }
        Log.e("Days Position Loop","Loop Length = "+posi);
       return posi;

    }



    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    @SuppressLint("ResourceAsColor")
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        CustomTextView txt_status, txt_timeIn, txt_timeOut;


        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);

        }
         txt_status = v.findViewById(R.id.date1);
        dayView = v.findViewById(R.id.date);
        if(position<days.length) {

//        txt_timeIn = (CustomTextView)v.findViewById(R.id.date2);
//        txt_timeOut = (CustomTextView)v.findViewById(R.id.date3);

            // disable empty days from the beginning

            if (days[position].equals("")) {
                dayView.setClickable(false);
                dayView.setFocusable(false);
//                dayView.setVisibility(View.INVISIBLE);
                //v.setBackgroundResource(R.color.white);

            } else {
                // mark current day as focused
                if (month.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) && month.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) && days[position].equals("" + selectedDate.get(Calendar.DAY_OF_MONTH))) {
//                   v.setBackgroundResource(R.color.colorGray);
                    dayView.setTextColor(R.color.colorPrimaryDark);
                } else {
                    // v.setBackgroundResource(R.color.white);
//                    v.setVisibility(View.INVISIBLE);
                }

            }

            dayView.setText(days[position]);
            Log.e("Day Date",""+days[position]);

            if (!(position < FIRST_DAY)) {
                try {
//                    HashMap<String,String> hashMap = data.get(0);
//
//                    int responseFirstDate = Integer.parseInt(hashMap.get("DISPLAY_ATTENDANCE_DATE"));


//                    String displayDate = mapdata.get("DISPLAY_ATTENDANCE_DATE");
//                    String time_in = mapdata.get("EAR_TIME_IN");
//                    String time_out = mapdata.get("EAR_TIME_OUT");
                    String dayDateValue = "";
                    int dayInt = 0;
                    String dayDate = dayView.getText().toString().trim();
                    int dayDateInt = Integer.parseInt(dayDate);
//                    if(dayDateInt<10)
//                        dayDateValue = "0"+dayDate;
//                    Integer.parseInt(dayDateValue)

                    for(int a=0;a<arlDataSize;a++){
                        HashMap<String,String> hashMap = data.get(a);
                        dayDateValue = hashMap.get("DISPLAY_ATTENDANCE_DATE");

                        if(Integer.parseInt(dayDateValue)==Integer.parseInt(dayDate)){
//                            int newIndex = position - FIRST_DAY-(dateIndex-1);
                            int newIndex = a;

                            Log.e("Position Arldata","Pos = "+newIndex+",Date Index "+dateIndex+", Data Size "+data.size()+", Current Date "+dayDateInt);
                            mapdata = data.get(newIndex);
                            String status = mapdata.get("AA_ATTENDANCE_DESCRIPTION");
                            String att_status = mapdata.get("EAR_ATTENDANCE_STATUS");

                            if (status.equals("")) {
                                txt_status.setText("  -");
                            } else if (status.equals("Absent")) {
                                txt_status.setText("A");
                                txt_status.setBackgroundColor(Color.parseColor("#BD2231"));
                                txt_status.setTextColor(Color.parseColor("#FFFFFF"));
                            } else if (att_status.equals("HO")) {
                                txt_status.setText(att_status);
                                txt_status.setBackgroundColor(Color.parseColor("#DDA23B"));
                                txt_status.setTextColor(Color.parseColor("#FFFFFF"));
                            } else if (att_status.equals("HA")) {
                                txt_status.setText(att_status);
                                txt_status.setBackgroundColor(Color.parseColor("#3F85F4"));
                                txt_status.setTextColor(Color.parseColor("#FFFFFF"));
                            } else if (att_status.equals("WO")) {
                                txt_status.setText(att_status);
                                txt_status.setBackgroundColor(Color.parseColor("#EE8F8F"));
                                txt_status.setTextColor(Color.parseColor("#FFFFFF"));
                            } else if (att_status.equals("P")) {
                                txt_status.setText(att_status);
                                txt_status.setBackgroundColor(Color.parseColor("#35BD42"));
                                txt_status.setTextColor(Color.parseColor("#FFFFFF"));
                            } else if (att_status.equals("OD")) {
                                txt_status.setText(att_status);
                                txt_status.setBackgroundColor(Color.parseColor("#91A39B"));
                                txt_status.setTextColor(Color.parseColor("#FFFFFF"));
                            } else if (att_status.equals("SL")) {
                                txt_status.setText(att_status);
                                txt_status.setBackgroundColor(Color.parseColor("#D3D693"));
                                txt_status.setTextColor(Color.parseColor("#FFFFFF"));
                            } else if (att_status.equals("CL")) {
                                txt_status.setText(att_status);
                                txt_status.setBackgroundColor(Color.parseColor("#D3D693"));
                                txt_status.setTextColor(Color.parseColor("#FFFFFF"));
                            }

                            txt_status.setVisibility(View.VISIBLE);
                        }
                    }


//                    if(dateIndex <= dayDateInt) {
//                        int newIndex = position - FIRST_DAY-(dateIndex-1);
//
//                        Log.e("Position Arldata","Pos = "+newIndex+",Date Index "+dateIndex+", Data Size "+data.size()+", Current Date "+dayDateInt);
//                        mapdata = data.get(newIndex);
//                        String status = mapdata.get("AA_ATTENDANCE_DESCRIPTION");
//                        String att_status = mapdata.get("EAR_ATTENDANCE_STATUS");
//
//                        if (status.equals("")) {
//                            txt_status.setText("  -");
//                        } else if (status.equals("Absent")) {
//                            txt_status.setText("A");
//                            txt_status.setBackgroundColor(Color.parseColor("#BD2231"));
//                            txt_status.setTextColor(Color.parseColor("#FFFFFF"));
//                        } else if (att_status.equals("HO")) {
//                            txt_status.setText(att_status);
//                            txt_status.setBackgroundColor(Color.parseColor("#DDA23B"));
//                            txt_status.setTextColor(Color.parseColor("#FFFFFF"));
//                        } else if (att_status.equals("HA")) {
//                            txt_status.setText(att_status);
//                            txt_status.setBackgroundColor(Color.parseColor("#3F85F4"));
//                            txt_status.setTextColor(Color.parseColor("#FFFFFF"));
//                        } else if (att_status.equals("WO")) {
//                            txt_status.setText(att_status);
//                            txt_status.setBackgroundColor(Color.parseColor("#EE8F8F"));
//                            txt_status.setTextColor(Color.parseColor("#FFFFFF"));
//                        } else if (att_status.equals("P")) {
//                            txt_status.setText(att_status);
//                            txt_status.setBackgroundColor(Color.parseColor("#35BD42"));
//                            txt_status.setTextColor(Color.parseColor("#FFFFFF"));
//                        } else if (att_status.equals("OD")) {
//                            txt_status.setText(att_status);
//                            txt_status.setBackgroundColor(Color.parseColor("#91A39B"));
//                            txt_status.setTextColor(Color.parseColor("#FFFFFF"));
//                        } else if (att_status.equals("SL")) {
//                            txt_status.setText(att_status);
//                            txt_status.setBackgroundColor(Color.parseColor("#D3D693"));
//                            txt_status.setTextColor(Color.parseColor("#FFFFFF"));
//                        } else if (att_status.equals("CL")) {
//                            txt_status.setText(att_status);
//                            txt_status.setBackgroundColor(Color.parseColor("#D3D693"));
//                            txt_status.setTextColor(Color.parseColor("#FFFFFF"));
//                        }
////                    }else if(status.equals("Weekly Off")){
////                        txt_status.setText("WO");
////                        txt_status.setTextColor(Color.parseColor("#EE8F8F"));
////                    }else if(status.equals("Absent")){
////                        txt_status.setText("A");
////                        txt_status.setTextColor(Color.parseColor("#BD2231"));
////                    }else if(status.equals("Presant")){
////                        txt_status.setText("P");
////                        txt_status.setTextColor(Color.parseColor("#35BD42"));
////                    } else if(status.equals("Sick Leave")){
////                        txt_status.setText("SL");
////                        txt_status.setTextColor(Color.parseColor("#D3D693"));
////                    } else if(status.equals("Halfday Absent")){
////                        txt_status.setText("HA");
////                        txt_status.setTextColor(Color.parseColor("#3F85F4"));
////                    } else if(status.equals("Present")){
////                        txt_status.setText("P");
////                        txt_status.setTextColor(Color.parseColor("#C9A024"));
////                    }  else if(status.equals("On Duty")){
////                        txt_status.setText("OD");
////                        txt_status.setTextColor(Color.parseColor("#91A39B"));
////                    } else {
////                        txt_status.setText("  -");
////                    }
////                    if (time_in.equals(""))
////                        time_in = "In";
////                    if (time_out.equals(""))
////                        time_out = "Out";
//
//                        txt_status.setVisibility(View.VISIBLE);
////                    txt_status.setText(status);
////                    txt_timeIn.setText(time_in);
////                    txt_timeOut.setText(time_out);
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            Log.e("Day Date 1",""+days[position]);
            // create date string for comparison
            String date = days[position];

            if (date.length() == 1) {
                date = "0" + date;
            }
            String monthStr = "" + (month.get(Calendar.MONTH) + 1);
            if (monthStr.length() == 1) {
                monthStr = "0" + monthStr;
            }
        } else{
            Log.e("Day Date 1","null");
            dayView.setText("");
            txt_status.setText("");
        }

        // show icon if date is not empty and it exists in the items array
        return v;
    }

    public void refreshDays() {
        // clear items
        items.clear();

        int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = month.get(Calendar.DAY_OF_WEEK);
        FIRST_DAY =firstDay-1;

        Log.e("Refresh","Last="+lastDay+" first="+firstDay);
        // figure size of the array
        if(firstDay==1){
            days = new String[lastDay+(FIRST_DAY_OF_WEEK*6)];
            Log.e("FirstDay","1 = "+days[1]);
        }
        else {
            days = new String[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1)];
            Log.e("FirstDay2","2 = "+days[lastDay]);
            Log.e("Days Of Week","Day = "+FIRST_DAY_OF_WEEK);
        }

        int j=FIRST_DAY_OF_WEEK;

        // populate empty days before first real day
        if(firstDay>1) {
            for(j=0;j<firstDay-FIRST_DAY_OF_WEEK;j++) {
                days[j] = "";
            }
        }
        else {
            for(j=0;j<FIRST_DAY_OF_WEEK*6;j++) {
                days[j] = "";
            }
            j=FIRST_DAY_OF_WEEK*6+1; // sunday => 1, monday => 7
        }

        // populate days
        int dayNumber = 1;
        for(int i=j-1;i<days.length;i++) {
            days[i] = ""+dayNumber;
            dayNumber++;
        }
    }

    // references to our items
    public String[] days;
}