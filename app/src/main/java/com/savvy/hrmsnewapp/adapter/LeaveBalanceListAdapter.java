package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.os.Environment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by hariom on 8/5/17.
 */
public class LeaveBalanceListAdapter extends RecyclerView.Adapter<LeaveBalanceListAdapter.MyViewHolder> {
    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    CustomTextView txt_applyLeaveValueGreen,txt_applyLeaveValue,txt_insufficietValue;


    public LeaveBalanceListAdapter(Context context, List<HashMap<String, String>> data) {
        WriteLog("Leave Balance List Adapter Constructor","LeaveRequestFicciAdapter");
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        //imageLoader     = ImageLoader.getInstance();
        //imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public LeaveBalanceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_balance_row, parent, false);
        WriteLog("Leave Balance List Adapter Create My Holder","LeaveRequestFicciAdapter");
        return new LeaveBalanceListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LeaveBalanceListAdapter.MyViewHolder holder, int position) {
        try {
            HashMap<String, String> mapdata = data.get(position);
            //Movie movie = moviesList.get(position);
//        for(int k = 0;k<data.size();k++) {
//            HashMap<String, String> mapdata = data.get(k);

            WriteLog("Leave Balance List Adapter On Bind My Holder. Position = " + position, "LeaveRequestFicciAdapter");
            Toast.makeText(context,"Leave Balance Adapter accurate position = "+position,Toast.LENGTH_LONG).show();
            holder.leaveNameValue.setText(mapdata.get("LM_ABBREVATION"));
            holder.approvedValue.setText(mapdata.get("APPROVED_LEAVE"));

            holder.balanceValue.setText(mapdata.get("CURRENT_BALANCE"));
            holder.pendingValue.setText(mapdata.get("PENDING_LEAVE"));
            holder.runningValue.setText(mapdata.get("RUNNING_BALANCE"));
            //holder.applyLeaveValue.setText(mapdata.get("ACTION_BY"));
       /* holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getYear());
            Log.e("Position", "" + position);

            String enable = mapdata.get("ENABLE");
            Log.v("Enable ", "" + enable);
            if (enable.equals("1")) {
                WriteLog("Leave Balance List Adapter On Bind My Holder. 1", "LeaveRequestFicciAdapter");
                txt_applyLeaveValueGreen.setVisibility(View.VISIBLE);
                txt_applyLeaveValue.setVisibility(View.INVISIBLE);
                txt_insufficietValue.setVisibility(View.INVISIBLE);
            } else if (enable.equals("2")) {
                WriteLog("Leave Balance List Adapter On Bind My Holder. 2", "LeaveRequestFicciAdapter");
                txt_applyLeaveValueGreen.setVisibility(View.INVISIBLE);
                txt_applyLeaveValue.setVisibility(View.VISIBLE);
                txt_insufficietValue.setVisibility(View.INVISIBLE);
            } else if (enable.equals("0")) {
                WriteLog("Leave Balance List Adapter On Bind My Holder. 3", "LeaveRequestFicciAdapter");
                txt_applyLeaveValueGreen.setVisibility(View.INVISIBLE);
                txt_applyLeaveValue.setVisibility(View.INVISIBLE);
                txt_insufficietValue.setVisibility(View.VISIBLE);
            }*/
        }catch (Exception ex){
            WriteLog("Exception Leave Balance List Adapter On Bind My Holder. Position = " + position+" Message = "+ex.getMessage(), "LeaveRequestFicciAdapter");
        }
//        }
    }


    @Override
    public int getItemCount() {
        WriteLog("Leave Balance List Adapter getItem Size = "+data.size(),"LeaveRequestFicciAdapter");
        Log.e("Size",""+data.size());
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CustomTextView leaveNameValue, approvedValue,advanceValue,lopValue,balanceValue,pendingValue,runningValue,applyLeaveValue;

        public MyViewHolder(View view) {
            super(view);
            try {
                WriteLog("Leave Balance List Adapter My holder Main", "LeaveRequestFicciAdapter");
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
            }catch (Exception e){
                WriteLog("Exception Leave Balance List Adapter My holder Main. Message = "+e.getMessage(), "LeaveRequestFicciAdapter");
            }

           /* year = (TextView) view.findViewById(R.id.year);*/
        }

        @Override
        public void onClick(View v) {

        }
    }
    public void WriteLog(String data,String fragment) {
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
            String formattedDate = df.format(c.getTime());
            String date = new SimpleDateFormat("yyyyMMdd",Locale.UK).format(new Date());
            data = formattedDate + "->" + data + "\n";
            byte[] bytes = data.getBytes("UTF-8");
            String path = Environment.getExternalStorageDirectory() + "//Megamind//Logs";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = path + "//" + fragment+"_Log_" + date + ".txt";
            file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(path, true);
            stream.write(bytes);
            stream.close();
        } catch (Exception e1) {
            Toast.makeText(context, "Give Permission for Read Write", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
        }
    }

}
