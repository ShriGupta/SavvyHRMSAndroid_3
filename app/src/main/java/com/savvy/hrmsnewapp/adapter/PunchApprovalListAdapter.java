package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.PunchApprovalFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by orapc7 on 5/17/2017.
 */

public class PunchApprovalListAdapter extends RecyclerView.Adapter<PunchApprovalListAdapter.MyViewHolder>{


    PunchApprovalFragment punchApprovalFragment;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
//    PunchStatusListAdapter.CancelPunchAsync cancelPunchAsync;
    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    SharedPreferences shared,shareEmp;

    String str_token="";

    String xmlData = "";
    String token_no = "";
    HashMap<String,String> mapdata;
    HashMap<String,String> mapdata1;

    SharedPreferences.Editor editor;
    String employeeId = "",requestId = "",punch_status_1 = "";
    CoordinatorLayout coordinatorLayout;

    ArrayList<Integer> arrayList;
    String REQUEST_ID = "",REQUEST_FLOW_STATUS_ID = "",ACTION_LEVEL_SEQUENCE = "",
            MAX_ACTION_LEVEL_SEQUENCE = "",R_STATUS = "",EMPLOYEE_ID = "",ERFS_REQUEST_FLOW_ID = "";

    public CustomTextView approv_punchPullback;

    public PunchApprovalListAdapter(Context context, CoordinatorLayout coordinatorLayout , List<HashMap<String, String>> data, ArrayList<Integer> arrayList) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.coordinatorLayout =coordinatorLayout;
        this.arrayList = arrayList;

        shareEmp = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shareEmp.getString("EmpoyeeId", ""));

    }


    @Override
    public PunchApprovalListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.punch_approval_row, parent, false);
        return new PunchApprovalListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PunchApprovalListAdapter.MyViewHolder holder, int position) {
        mapdata=data.get(position);

//        for(int k = 0;k<data.size();k++){
//            HashMap<String,String> mapdata = data.get(k);
//            if(arrayList.get(position)==Integer.parseInt(mapdata.get("TOKEN_NO"))){
                holder.approv_punchToken_no.setText(mapdata.get("TOKEN_NO"));
                holder.approv_punchdate_value.setText(mapdata.get("PUNCH_DATE"));
                holder.approv_punchtime_value.setText(mapdata.get("PUNCH_TIME"));
                holder.approv_reason_value.setText(mapdata.get("REASON"));
                holder.approv_empId_value.setText(mapdata.get("EMPLOYEE_CODE"));
                holder.approv_empName_value.setText(mapdata.get("EMPLOYEE_NAME"));
//                holder.approv_type_value.setText(mapdata.get("R_TYPE"));
//            }
//        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CustomTextView approv_punchToken_no,approv_punchdate_value,approv_punchtime_value,approv_reason_value,approv_empId_value;
        public CustomTextView approv_empName_value,approv_type_value;//,txt_punchclear,txt_punchPullback;
        public RelativeLayout relativeLayout;
        public MyViewHolder(View view) {
            super(view);

            approv_punchToken_no = view.findViewById(R.id.txt_puchApprov_value);
            approv_punchdate_value = view.findViewById(R.id.approv_puchDate_value);
            approv_punchtime_value = view.findViewById(R.id.approv_puchTime_value);
            approv_reason_value = view.findViewById(R.id.approv_reason_value);
            approv_empId_value = view.findViewById(R.id.approv_empId_value);
            approv_empName_value = view.findViewById(R.id.approv_empName_value);
//            approv_type_value = (CustomTextView)view.findViewById(R.id.approv_type_value);
            relativeLayout = view.findViewById(R.id.relativePunchApprove);
            CustomTextView txt_actionValue = view.findViewById(R.id.approv_pullback);

            view.setOnClickListener(this);
            txt_actionValue.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }

}
