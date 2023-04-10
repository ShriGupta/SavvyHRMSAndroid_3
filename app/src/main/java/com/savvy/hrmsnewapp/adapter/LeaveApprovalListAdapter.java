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

public class LeaveApprovalListAdapter extends RecyclerView.Adapter<LeaveApprovalListAdapter.MyViewHolder>{

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    CustomSpinnerLeaveAdapter customspinnerAdapter;
    //ImageLoader imageLoader;
    ArrayList<HashMap<String,String>> arlData,arlRequestStatusData;
    private Context context;
    SharedPreferences shared,shareEmp;
    String employeeId = "",requestId = "",punch_status_1 = "";
    CoordinatorLayout coordinatorLayout;
    ArrayList<Integer> arrayList;

    public CustomTextView od_reject,leave_action;

    public LeaveApprovalListAdapter(Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data,ArrayList<Integer> arrayList) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.arrayList = arrayList;
        this.coordinatorLayout =coordinatorLayout;

        shareEmp = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shareEmp.getString("EmpoyeeId", ""));

    }

    @Override
    public LeaveApprovalListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_approval_row, parent, false);
        return new LeaveApprovalListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LeaveApprovalListAdapter.MyViewHolder holder, int position) {

        HashMap<String,String> mapdata=data.get(position);

        WriteLog("Leave Approval List Adapter On Bind My Holder. Position = " + position, "Leave Approval Adapter");
        //Movie movie = moviesList.get(position);
//        for(int k = 0;k<data.size();k++) {
//            HashMap<String, String> mapdata = data.get(k);
//            if (arrayList.get(position) == Integer.parseInt(mapdata.get("TOKEN_NO"))) {
                holder.leave_Token_no.setText(mapdata.get("TOKEN_NO"));
                holder.leave_Name.setText(mapdata.get("LM_LEAVE_NAME"));
                holder.leave_from_date.setText(mapdata.get("FROM_DATE"));
                holder.leave_to_date.setText(mapdata.get("TO_DATE"));
                holder.leave_emp_code.setText(mapdata.get("EMPLOYEE_CODE"));
                holder.leave_emp_name.setText(mapdata.get("EMPLOYEE_NAME"));
                holder.leave_days.setText(mapdata.get("TOTAL_DAYS_REQUESTED"));
//                holder.leave_type_full.setText(mapdata.get("TYPE"));
                holder.leave_reason.setText(mapdata.get("LR_REASON"));
//                holder.leave_type.setText(mapdata.get("R_TYPE"));
//            }
//        }

    }

    @Override
    public int getItemCount() {
        WriteLog("Leave Approval List Adapter Size = " + data.size(), "Leave Approval Adapter");
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CustomTextView leave_Token_no, leave_Name, leave_from_date, leave_to_date, leave_days;
        public CustomTextView leave_type_full, leave_reason, leave_emp_code, leave_emp_name, leave_type;//,txt_punchclear,txt_punchPullback;

        public MyViewHolder(View view) {
            super(view);
            leave_Token_no = view.findViewById(R.id.leave_token_value);
            leave_Name = view.findViewById(R.id.leave_name_value);
            leave_from_date = view.findViewById(R.id.leave_fromDate_value);
            leave_to_date = view.findViewById(R.id.leave_toDate_value);
            leave_emp_code = view.findViewById(R.id.leave_empId_value);
            leave_emp_name = view.findViewById(R.id.leave_empName_value);
            leave_days = view.findViewById(R.id.leave_days_value);
//            leave_type_full = (CustomTextView) view.findViewById(R.id.leave_fulltype1_value);
            leave_reason = view.findViewById(R.id.leave_reason_value);
//            leave_type = (CustomTextView) view.findViewById(R.id.leave_freshtype_value);
            leave_action = view.findViewById(R.id.leave_action);

            view.setOnClickListener(this);
            leave_action.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
    public void WriteLog(String data,String fragment) {
//        try {
//            Calendar c = Calendar.getInstance();
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
//            String formattedDate = df.format(c.getTime());
//            String date = new SimpleDateFormat("yyyyMMdd",Locale.UK).format(new Date());
//            data = formattedDate + "->" + data + "\n";
//            byte[] bytes = data.getBytes("UTF-8");
//            String path = Environment.getExternalStorageDirectory() + "//Megamind//Logs";
//            File file = new File(path);
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            path = path + "//" + fragment+"_Log_" + date + ".txt";
//            file = new File(path);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            FileOutputStream stream = new FileOutputStream(path, true);
//            stream.write(bytes);
//            stream.close();
//        } catch (Exception e1) {
//            Toast.makeText(context, "Give Permission for Read Write", Toast.LENGTH_SHORT).show();
//            e1.printStackTrace();
//        }
    }

}
