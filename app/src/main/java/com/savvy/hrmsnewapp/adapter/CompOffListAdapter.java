package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by orapc7 on 5/18/2017.
 */

public class CompOffListAdapter extends RecyclerView.Adapter<CompOffListAdapter.MyViewHolder>{


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    SharedPreferences shared,shareEmp;
    String employeeId = "",requestId = "",punch_status_1 = "";
    CoordinatorLayout coordinatorLayout;

    ArrayList<Integer> arrayList;
    String xmlData = "";
    String token_no = "";
    public CustomTextView od_reject,comp_action;

    String REQUEST_ID = "",REQUEST_FLOW_STATUS_ID = "",ACTION_LEVEL_SEQUENCE = "",
            MAX_ACTION_LEVEL_SEQUENCE = "",R_STATUS = "",EMPLOYEE_ID = "",ERFS_REQUEST_FLOW_ID = "";


    public CompOffListAdapter(Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data,ArrayList<Integer> arrayList) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.arrayList = arrayList;
        this.coordinatorLayout =coordinatorLayout;

        shareEmp = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shareEmp.getString("EmpoyeeId", ""));

    }


    @Override
    public CompOffListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comp_off_approval_row, parent, false);
        return new CompOffListAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(CompOffListAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);
        //Movie movie = moviesList.get(position);
//        for(int k = 0;k<data.size();k++) {
//            HashMap<String, String> mapdata = data.get(k);
//            if (arrayList.get(position) == Integer.parseInt(mapdata.get("TOKEN_NO"))) {
                holder.comp_Token_no.setText(mapdata.get("TOKEN_NO"));
                holder.comp_Off_Date.setText(mapdata.get("COMP_OFF_DATE"));
                holder.comp_time_in.setText(mapdata.get("TIME_IN"));
                holder.comp_time_out.setText(mapdata.get("TIME_OUT"));
                holder.comp_emp_code.setText(mapdata.get("EMPLOYEE_CODE"));
                holder.comp_empName.setText(mapdata.get("EMPLOYEE_NAME"));
                holder.comp_reason.setText(mapdata.get("REASON"));
                holder.comp_requested_days.setText(mapdata.get("COR_REQUEST_DAYS"));
                holder.comp_approved_days.setText(mapdata.get("COR_APPROVE_DAYS"));
                holder.comp_type.setText(mapdata.get("R_TYPE"));
//            }
//        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView comp_Token_no, comp_Off_Date, comp_time_in, comp_time_out, comp_emp_code;
        public CustomTextView comp_empName, comp_reason, comp_requested_days, comp_approved_days, comp_type;//,txt_punchclear,txt_punchPullback;

        public MyViewHolder(View view) {
            super(view);
            comp_Token_no = view.findViewById(R.id.comp_token_value);
            comp_Off_Date = view.findViewById(R.id.comp_offDate_value);
            comp_time_in = view.findViewById(R.id.comp_timeIn_value);
            comp_time_out = view.findViewById(R.id.comp_timeOut_value);
            comp_emp_code = view.findViewById(R.id.comp_empId_value);
            comp_empName = view.findViewById(R.id.comp_empName_value);
            comp_reason = view.findViewById(R.id.comp_reason_value);
            comp_requested_days = view.findViewById(R.id.comp_requestedDays_value);
            comp_approved_days = view.findViewById(R.id.comp_approve_days_value);
            comp_type = view.findViewById(R.id.comp_type_value);
            comp_action = view.findViewById(R.id.comp_action);

            view.setOnClickListener(this);
            comp_action.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}