package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.calendar.CalanderHRMS;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ODRequestFicciAdapter extends RecyclerView.Adapter<ODRequestFicciAdapter.MyView_Holder> {

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


    public ODRequestFicciAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> listData, ClickListener listener) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.listData = listData;
        this.listener = listener;
    }

    @Override
    public MyView_Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MyView_Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.od_request_ficcifragment_row, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(MyView_Holder holder, final int position) {

        HashMap<String, String> mapdata = listData.get(position);
        holder.showDate.setText(mapdata.get("EAR_ATTENDANCE_DATE"));
        holder.fromTime.setText("");
        holder.toTime.setText("");
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class MyView_Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView showDate;
        Button fromTime, toTime;

        private WeakReference<ClickListener> listenerRef;

        public MyView_Holder(View view, ClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            showDate = view.findViewById(R.id.showDate);
            fromTime = view.findViewById(R.id.fromTime_Button);
            toTime = view.findViewById(R.id.toTime_Button);
            calanderHRMS = new CalanderHRMS(context);

            fromTime.setOnClickListener(this);
            toTime.setOnClickListener(this);
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

                        for (int key : fromTimeMap.keySet()) {
                            if (key == getAdapterPosition()) {
                                fromTimeMap.replace(key, s.toString());
                            }
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

                        for (int key : toTimeMap.keySet()) {
                            if (key == getAdapterPosition()) {
                                toTimeMap.replace(key, s.toString());
                            }
                        }

                    }
                });
            }
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    public String getODDetails() {
        String finalValue = "";

        for (int i = 0; i < listData.size(); i++) {
            if (fromTimeMap.get(i) == null) {
                return "fromtime";
            } else if (toTimeMap.get(i) == null) {
                return "totime";
            } else {
                finalValue += "@" + listData.get(i).get("EAR_ATTENDANCE_DATE").replace("/", "-") + "," +
                        (fromTimeMap.get(i).replace(":", "-")).replace(" ", "_") + "," +
                        (toTimeMap.get(i).replace(":", "-")).replace(" ", "_");
            }
        }
        return finalValue;
    }
}

