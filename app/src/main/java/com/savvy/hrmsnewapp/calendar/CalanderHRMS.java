package com.savvy.hrmsnewapp.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by orapc7 on 5/8/2017.
 */

public class CalanderHRMS// extends Activity
{
    Context context;
    String date1 = "", time1 = "";
    String str_hour = "", str_minute = "";
    Button btnDate, btnTime;

    public CalanderHRMS(Context context) {
        this.context = context;
    }

    public void datePicker(final Button btnDate) {
        this.btnDate = btnDate;

        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Date d = new Date(year - 1900, monthOfYear, dayOfMonth);
                date1 = new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(d);
                btnDate.setText(date1);

            }
        }, yy, mm, dd);
        datePicker.getDatePicker().setCalendarViewShown(false);
        datePicker.show();
    }
    public void datePickerWithOtherFormate(final Button btnDate) {
        this.btnDate = btnDate;

        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Date d = new Date(year - 1900, monthOfYear, dayOfMonth);
                date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.UK).format(d);
                btnDate.setText(date1);

            }
        }, yy, mm, dd);
        datePicker.getDatePicker().setCalendarViewShown(false);
        datePicker.show();
    }
    public void timePickerHHMM(final Button btnTime) {

        this.btnTime = btnTime;
        Calendar c = Calendar.getInstance();
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mm = c.get(Calendar.MINUTE);

        TimePickerDialog td;
        td = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int hour_of_day = hourOfDay;
                str_hour = String.valueOf(hour_of_day);
                str_minute = String.valueOf(minute);

                String am_pm = "";
                if (hour_of_day < 10) {
                    str_hour = "0" + str_hour;
                }
                if (minute < 10) {
                    str_minute = "0" + str_minute;
                }

                time1 = "" + str_hour + ":" + str_minute + "" + am_pm;

                btnTime.setText(time1);
                //btn_overtime_from.setText(""+str_hour+" : "+str_minute+" "+am_pm);
            }
        }, hh, mm, true);
        td.show();
    }

    public void timePickerHHMMnew(final Button btnTime, final TextView textView) {

        this.btnTime = btnTime;
        Calendar c = Calendar.getInstance();
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mm = c.get(Calendar.MINUTE);

        TimePickerDialog td;
        td = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int hour_of_day = hourOfDay;
                str_hour = String.valueOf(hour_of_day);
                str_minute = String.valueOf(minute);

                String am_pm = "";
                if (hour_of_day < 10) {
                    str_hour = "0" + str_hour;
                }
                if (minute < 10) {
                    str_minute = "0" + str_minute;
                }

                time1 = "" + str_hour + ":" + str_minute + "" + am_pm;

                btnTime.setText(time1);

                String amPm = "AM";
                int hourTemp = 0;
                if (hour_of_day > 12) {
                    amPm = "PM";
                    hourTemp = hour_of_day - 12;
                    textView.setText("" + hourTemp + ":" + str_minute + " " + amPm);
                } else {
                    textView.setText("" + hourOfDay + ":" + str_minute + " " + amPm);


                }


                //btn_overtime_from.setText(""+str_hour+" : "+str_minute+" "+am_pm);
            }
        }, hh, mm, true);
        td.show();

    }

    public void timePicker(final Button btnTime) {

        this.btnTime = btnTime;

        Calendar c = Calendar.getInstance();
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mm = c.get(Calendar.MINUTE);

        TimePickerDialog td;
        td = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int hour_of_day = hourOfDay;
                str_hour = String.valueOf(hour_of_day);
                str_minute = String.valueOf(minute);

                String am_pm = "";
                if (hour_of_day < 10) {
                    str_hour = "0" + str_hour;
                }
                if (minute < 10) {
                    str_minute = "0" + str_minute;
                }

                if (hour_of_day < 12) {
                    am_pm = "AM";
                } else {
                    am_pm = "PM";
                    hour_of_day = hour_of_day - 12;
                    str_hour = String.valueOf(hour_of_day);
                    if (hour_of_day < 10)
                        str_hour = "0" + String.valueOf(hour_of_day);
                }

                time1 = "" + str_hour + ":" + str_minute + " " + am_pm;

                btnTime.setText(time1);
                //btn_overtime_from.setText(""+str_hour+" : "+str_minute+" "+am_pm);
            }
        }, hh, mm, false);
        td.show();

    }
}
