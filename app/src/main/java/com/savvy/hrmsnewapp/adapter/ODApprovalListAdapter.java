package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.ODApprovalFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by orapc7 on 5/17/2017.
 */

public class ODApprovalListAdapter extends RecyclerView.Adapter<ODApprovalListAdapter.MyViewHolder> {


    ODApprovalFragment.ODApprovalAsyn odApprovalAsyn;
    //ODApprovalFragment.ODApprovalActionAsyn odApprovalActionAsyn;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    List<HashMap<String, String>> data;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    SharedPreferences shared, shareEmp;
    String employeeId = "", requestId = "", punch_status_1 = "";
    CoordinatorLayout coordinatorLayout;
    String xmlData = "";
    String token_no = "";
    ArrayList<Integer> arrayList;
    SpannableString customText = null;

    public CustomTextView od_reject, od_punchPullback, txt_reason;

    String REQUEST_ID = "", REQUEST_FLOW_STATUS_ID = "", ACTION_LEVEL_SEQUENCE = "",
            MAX_ACTION_LEVEL_SEQUENCE = "", R_STATUS = "", EMPLOYEE_ID = "", ERFS_REQUEST_FLOW_ID = "";


    public ODApprovalListAdapter(Context context, CoordinatorLayout coordinatorLayout, List<HashMap<String, String>> data, ArrayList<Integer> arrayList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.arrayList = arrayList;
        this.coordinatorLayout = coordinatorLayout;

        shareEmp = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shareEmp.getString("EmpoyeeId", ""));

    }


    @Override
    public ODApprovalListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.od_approval_row, parent, false);
        // TouchListener touch = new TouchListener(itemView,context);

        //coordinatorLayout = (CoordinatorLayout)itemView.findViewById(R.id.coordinatorLayout);

//        return touch;


        return new ODApprovalListAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ODApprovalListAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = data.get(position);
        //Movie movie = moviesList.get(position);
//        for(int k = 0;k<data.size();k++) {
//            HashMap<String,String> mapdata=data.get(k);
//            if (arrayList.get(position) == Integer.parseInt(mapdata.get("TOKEN_NO"))) {
        holder.od_Token_no.setText(mapdata.get("TOKEN_NO"));
        holder.od_fromDate.setText(mapdata.get("FROM_DATE"));
        holder.od_toDate.setText(mapdata.get("TO_DATE"));
        holder.od_emp_code.setText(mapdata.get("EMPLOYEE_CODE"));
        holder.od_empName.setText(mapdata.get("EMPLOYEE_NAME"));
        holder.od_type.setText(mapdata.get("OD_TYPE_NAME"));

        /*holder.txt_reason.setText(mapdata.get("REASON"));*/
        final String reasonString = mapdata.get("REASON");
        holder.txt_reason.setText(reasonString);

//        makeTextViewResizable(holder.txt_reason, 1, "more", true);
        holder.btShowmore.setVisibility(View.GONE);
        holder.btShowless.setVisibility(View.GONE);


//                holder.od_request_type.setText(mapdata.get("OD_REQUEST_TYPE_NAME"));
//                holder.od_subrequest_type.setText(mapdata.get("OD_REQUEST_SUB_TYPE_NAME"));
//                holder.od_type_1.setText(mapdata.get("R_TYPE"));

//                if(mapdata.get("OD_REQUEST_SUB_TYPE_NAME").equals(""))
//                    holder.od_subrequest_type.setText("    -");
//            }
//        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView od_Token_no, od_fromDate, od_toDate, od_type, od_emp_code,txt_reason;
        public CustomTextView od_empName, od_request_type, od_subrequest_type, od_type_1;//,txt_punchclear,txt_punchPullback;
        Button btShowmore;
        Button btShowless;

        public MyViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            od_Token_no = view.findViewById(R.id.od_token_value);
            od_fromDate = view.findViewById(R.id.od_fromDate_value);
            od_toDate = view.findViewById(R.id.od_toDate_value);
            od_emp_code = view.findViewById(R.id.od_empId_value);
            od_empName = view.findViewById(R.id.od_empName_value);
            od_type = view.findViewById(R.id.od_type1_value);
            CustomTextView txt_actionValue = view.findViewById(R.id.od_approve);
            txt_reason = view.findViewById(R.id.txt_reason_value);
            btShowmore = view.findViewById(R.id.btShowmore);
            btShowless = view.findViewById(R.id.btShowless);

//            od_request_type = (CustomTextView)view.findViewById(R.id.od_request_value);
//            od_subrequest_type = (CustomTextView)view.findViewById(R.id.od_subrequest_value);
//            od_type_1 = (CustomTextView)view.findViewById(R.id.od_type_value);
            txt_actionValue.setOnClickListener(this);
//            Log.e("6:33", String.valueOf(txt_reason.length()));
//            if(txt_reason.length()>20) {
//                btShowmore.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        btShowmore.setVisibility(View.GONE);
//                        btShowless.setVisibility(View.VISIBLE);
//                        txt_reason.setMaxLines(Integer.MAX_VALUE);
//                    }
//
//                });
//                btShowless.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        btShowless.setVisibility(View.GONE);
//                        btShowmore.setVisibility(View.VISIBLE);
//                        txt_reason.setMaxLines(2);
//                    }
//
//                });
//
//            }
//            else if(txt_reason.length()<20)
//            {
//                btShowmore.setVisibility(View.GONE);
//            }
        }

        @Override
        public void onClick(View v) {

        }
    }


}
