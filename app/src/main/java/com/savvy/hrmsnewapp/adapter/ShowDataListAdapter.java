package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by orapc7 on 6/13/2017.
 */

public class ShowDataListAdapter  extends RecyclerView.Adapter<ShowDataListAdapter.MyViewHolder>{


    public static final String MY_PREFS_NAME = "MyPrefsFile";

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    SharedPreferences shared,shareEmp;
    String employeeId = "",requestId = "",punch_status_1 = "";
    CoordinatorLayout coordinatorLayout;
    String xmlData = "";
    String token_no = "";
    ArrayList<Integer> arrayList;

    StringBuilder stringBuilder = new StringBuilder();

    public CustomTextView od_reject,od_punchPullback;

    public ShowDataListAdapter(Context context,List<HashMap<String, String>> data) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.arrayList = arrayList;
        this.coordinatorLayout =coordinatorLayout;

        shareEmp = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shareEmp.getString("EmpoyeeId", ""));

        stringBuilder = new StringBuilder();

    }


    @Override
    public ShowDataListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alert_list, parent, false);
        // TouchListener touch = new TouchListener(itemView,context);

        //coordinatorLayout = (CoordinatorLayout)itemView.findViewById(R.id.coordinatorLayout);

//        return touch;
        return new ShowDataListAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final ShowDataListAdapter.MyViewHolder holder, int position) {
        //HashMap<String,String> mapdata=data.get(position);
        //Movie movie = moviesList.get(position);
        HashMap<String,String> mapdata=data.get(position);
        holder.txv_leaveDateValue.setText(mapdata.get("EAR_ATTENDANCE_DATE"));
        holder.txt_statusValue.setText(mapdata.get("EAR_ATTENDANCE_STATUS"));
//        holder.txt_deductiontypeValue.setText(mapdata.get("DEDUCTION"));
        String spin_value = mapdata.get("DEDUCTION");

        Log.e("Spin Value",""+spin_value);
        if(spin_value.equals("NA"))
            holder.spin_deduction_type.setSelection(0);
        else if(spin_value.equals("Full Day"))
            holder.spin_deduction_type.setSelection(1);
        else if(spin_value.equals("First Half"))
            holder.spin_deduction_type.setSelection(2);
        else if(spin_value.equals("Second Half"))
            holder.spin_deduction_type.setSelection(3);
        //holder.spin_deduction_type.setSelection(1);
        holder.txt_deductiondaysValue.setText(mapdata.get("DEDUCTION_DAYS"));

//
//        String ear_attendance_status = mapdata.get("EAR_ATTENDANCE_STATUS");
//        if(ear_attendance_status.equals(""))
//            ear_attendance_status = "-";
//
//        stringBuilder.append("@");
//        stringBuilder.append(mapdata.get("ATTENDANCE_DATE").replace("/","-"));
//        stringBuilder.append(","+ear_attendance_status);
//        stringBuilder.append(","+mapdata.get("DEDUCTION").replace(" ","-"));
//        stringBuilder.append(","+mapdata.get("DEDUCTION_DAYS").trim());


//        Log.e("String Builder Adapter",stringBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener,View.OnClickListener {

        CustomTextView txv_leaveDateValue, txt_statusValue, txt_deductiontypeValue, txt_deductiondaysValue;
        Spinner spin_deduction_type;
        String[] array = new String[]{"NA","Full Day","First Half","Second Half"};
        public MyViewHolder(View view) {
            super(view);
            txv_leaveDateValue = view.findViewById(R.id.txv_leaveDateValue);
            txt_statusValue = view.findViewById(R.id.txt_statusValue);
            spin_deduction_type = view.findViewById(R.id.txt_deductiontypeValue);
            txt_deductiondaysValue = view.findViewById(R.id.txt_deductiondaysValue);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item,array);
            spin_deduction_type.setAdapter(arrayAdapter);

            spin_deduction_type.setOnItemSelectedListener(this);
            view.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
