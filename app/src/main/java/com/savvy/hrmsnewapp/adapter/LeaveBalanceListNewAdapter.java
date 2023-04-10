package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 8/1/2017.
 */

public class LeaveBalanceListNewAdapter extends RecyclerView.Adapter<LeaveBalanceListNewAdapter.MyViewHolder> {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    List<HashMap<String, String>> data;
    private LayoutInflater inflater;
    CustomSpinnerLeaveAdapter customspinnerAdapter;
    //ImageLoader imageLoader;
    ArrayList<HashMap<String, String>> arlData, arlRequestStatusData;
    private Context context;
    SharedPreferences shared, shareEmp;
    String employeeId, requestId, punch_status_1;
    CoordinatorLayout coordinatorLayout;
    ArrayList<Integer> arrayList;

    CustomTextView txt_applyLeaveValueGreen,txt_applyLeaveValue,txt_insufficietValue;

    public CustomTextView od_reject, leave_action;

    public LeaveBalanceListNewAdapter(Context context, CoordinatorLayout coordinatorLayout, List<HashMap<String, String>> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.arrayList = arrayList;
        this.coordinatorLayout = coordinatorLayout;
    }

    @Override
    public LeaveBalanceListNewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_balance_row, parent, false);

        return new LeaveBalanceListNewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LeaveBalanceListNewAdapter.MyViewHolder holder, int position) {

        HashMap<String, String> mapdata = data.get(position);

        WriteLog("Leave Balance List Adapter On Bind My Holder. Position = " + position, "LeaveRequestFicciAdapter");
//        Toast.makeText(context,"Leave Balance Adapter accurate position = "+position,Toast.LENGTH_LONG).show();
        holder.leaveNameValue.setText(mapdata.get("LM_ABBREVATION"));
        holder.approvedValue.setText(mapdata.get("APPROVED_LEAVE"));

        holder.balanceValue.setText(mapdata.get("CURRENT_BALANCE"));
        holder.pendingValue.setText(mapdata.get("PENDING_LEAVE"));
        holder.runningValue.setText(mapdata.get("RUNNING_BALANCE"));

        String enable = mapdata.get("ENABLE");
        if (enable.equals("1")) {
            txt_applyLeaveValueGreen.setVisibility(View.VISIBLE);
            txt_applyLeaveValue.setVisibility(View.INVISIBLE);
            txt_insufficietValue.setVisibility(View.INVISIBLE);
        } else if (enable.equals("2")) {
            txt_applyLeaveValueGreen.setVisibility(View.INVISIBLE);
            txt_applyLeaveValue.setVisibility(View.VISIBLE);
            txt_insufficietValue.setVisibility(View.INVISIBLE);
        } else if (enable.equals("0")) {
            txt_applyLeaveValueGreen.setVisibility(View.INVISIBLE);
            txt_applyLeaveValue.setVisibility(View.INVISIBLE);
            txt_insufficietValue.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        WriteLog("Leave Balance List Adapter Size = " + data.size(), "Leave Balance Adapter");
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        public TextView leaveNameValue, approvedValue,advanceValue,lopValue,balanceValue,pendingValue,runningValue,applyLeaveValue;
        public CustomTextView leaveNameValue, approvedValue,advanceValue,lopValue,balanceValue,pendingValue,runningValue,applyLeaveValue;

        public MyViewHolder(View view) {
            super(view);
//            WriteLog("Leave Balance List Adapter My holder Main", "LeaveRequestFicciAdapter");
//            leaveNameValue = (TextView) view.findViewById(R.id.txv_leaveNameValue);
//            approvedValue = (TextView) view.findViewById(R.id.txt_approvedValue);
//
//            balanceValue = (TextView) view.findViewById(R.id.txt_balanceValue);
//            pendingValue = (TextView) view.findViewById(R.id.txt_pendingValue);
//            runningValue = (TextView) view.findViewById(R.id.txt_runningValue);
//            //applyLeaveValue = (TextView) view.findViewById(R.id.txt_applyLeaveValue);
//
//            txt_applyLeaveValue = (TextView) view.findViewById(R.id.txt_applyLeaveValue);
//            txt_applyLeaveValueGreen = (TextView) view.findViewById(R.id.txt_applyLeaveValuegreen);
//            txt_insufficietValue = (TextView) view.findViewById(R.id.txt_insufficiant);

            leaveNameValue = view.findViewById(R.id.txv_leaveNameValue);
            approvedValue = view.findViewById(R.id.txt_approvedValue);

            balanceValue = view.findViewById(R.id.txt_balanceValue);
            pendingValue = view.findViewById(R.id.txt_pendingValue);
            runningValue = view.findViewById(R.id.txt_runningValue);
            //applyLeaveValue = (CustomTextView) view.findViewById(R.id.txt_applyLeaveValue);

            txt_applyLeaveValue = view.findViewById(R.id.txt_applyLeaveValue);
            txt_applyLeaveValueGreen = view.findViewById(R.id.txt_applyLeaveValuegreen);
            txt_insufficietValue = view.findViewById(R.id.txt_insufficiant);

            view.setOnClickListener(this);
            txt_applyLeaveValue.setOnClickListener(this);
            txt_applyLeaveValueGreen.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }

    public void WriteLog(String data, String fragment) {
        try {
//            Calendar c = Calendar.getInstance();
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
//            String formattedDate = df.format(c.getTime());
//            String date = new SimpleDateFormat("yyyyMMdd", Locale.UK).format(new Date());
//            data = formattedDate + "->" + data + "\n";
//            byte[] bytes = data.getBytes("UTF-8");
//            String path = Environment.getExternalStorageDirectory() + "//Megamind//Logs";
//            File file = new File(path);
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            path = path + "//" + fragment + "_Log_" + date + ".txt";
//            file = new File(path);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            FileOutputStream stream = new FileOutputStream(path, true);
//            stream.write(bytes);
//            stream.close();
        } catch (Exception e1) {
            Toast.makeText(context, "Give Permission for Read Write", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
        }
    }
}