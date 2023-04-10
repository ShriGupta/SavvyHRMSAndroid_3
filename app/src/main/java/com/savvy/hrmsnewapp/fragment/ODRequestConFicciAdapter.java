package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Utilities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class ODRequestConFicciAdapter extends RecyclerView.Adapter<ODRequestConFicciAdapter.MyViewHolder> {
    public interface ClickListener {
        void onPositionClicked(int position);
    }

    private final ClickListener listener;
    Context context;
    CoordinatorLayout coordinatorLayout;
    List<HashMap<String, String>> listData;
    CalanderHRMS calanderHRMS = new CalanderHRMS(context);
    HashMap<Integer, String> fromTimeMap = new HashMap<>();
    HashMap<Integer, String> toTimeMap = new HashMap<>();
    TreeMap<Integer, Boolean> checkBoxMap = new TreeMap<>();

    public ODRequestConFicciAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> listData, ClickListener listener) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.listData = listData;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.odrequest_con_ficci_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        HashMap<String, String> mapdata = listData.get(position);
        holder.showDate.setText(mapdata.get("EAR_ATTENDANCE_DATE"));
        holder.fromTime.setText("");
        holder.toTime.setText("");
        holder.checkBox.setChecked(false);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CustomTextView showDate;
        Button fromTime, toTime;
        CheckBox checkBox;
        private WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View view) {
            super(view);

            listenerRef = new WeakReference<>(listener);
            showDate = view.findViewById(R.id.odc_showDate);
            fromTime = view.findViewById(R.id.odcfromTime_Button);
            toTime = view.findViewById(R.id.odctoTime_Button);
            checkBox = view.findViewById(R.id.odcCheckBox);
            calanderHRMS = new CalanderHRMS(context);

            fromTime.setOnClickListener(this);
            toTime.setOnClickListener(this);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.d(TAG, "onCheckedChanged: " + b);

                    if (b) {
                        if (fromTime.getText().toString().equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Please Select From Time");
                            checkBox.setChecked(false);
                        } else if (toTime.getText().toString().equals("")) {
                            Utilities.showDialog(coordinatorLayout, "Please Select To Time");
                            checkBox.setChecked(false);
                        } else {
                            checkBoxMap.put(getAdapterPosition(), b);
                        }
                    } else {
                        fromTime.setText("");
                        toTime.setText("");
                        checkBoxMap.remove(getAdapterPosition());
                    }

                }
            });
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == fromTime.getId()) {
                calanderHRMS.timePicker(fromTime);
                fromTime.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void afterTextChanged(Editable s) {


                        fromTimeMap.put(getAdapterPosition(), s.toString());

                        if (fromTimeMap.containsKey(getAdapterPosition())) {
                            fromTimeMap.replace(getAdapterPosition(), s.toString());
                        }
                    }
                });
            } else {
                calanderHRMS.timePicker(toTime);
                toTime.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void afterTextChanged(Editable s) {
                        toTimeMap.put(getAdapterPosition(), s.toString());
                        if (toTimeMap.containsKey(getAdapterPosition())) {
                            toTimeMap.replace(getAdapterPosition(), s.toString());
                        }
                    }
                });
            }
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public String xmlData() {
        StringBuilder stringBuilder = new StringBuilder();
        if (checkBoxMap.size() > 0) {
            for (int i : checkBoxMap.keySet()) {
                stringBuilder.append("@").append(listData.get(i).get("EAR_ATTENDANCE_DATE").replace("/", "-")).append(",")
                        .append((fromTimeMap.get(i).replace(":", "-")).replace(" ", "_")).append(",")
                        .append((toTimeMap.get(i).replace(":", "-")).replace(" ", "_"));
            }
            return stringBuilder.toString();
        } else {
            return "";
        }

    }
}
