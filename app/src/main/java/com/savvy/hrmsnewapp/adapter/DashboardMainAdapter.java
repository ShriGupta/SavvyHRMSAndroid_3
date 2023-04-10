package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

import static com.savvy.hrmsnewapp.activity.LeaveApplyFicciActivity.TAG;

public class DashboardMainAdapter extends RecyclerView.Adapter<DashboardMainAdapter.MyViewHolder> {
    Context context;
    String[] empFestive;
    Integer[] FestivePicture;

    public DashboardMainAdapter(Context context, String[] empFestive, Integer[] FestivePicture) {

        this.context = context;
        this.empFestive = empFestive;
        this.FestivePicture = FestivePicture;
    }

    @NonNull
    @Override
    public DashboardMainAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_main_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull final DashboardMainAdapter.MyViewHolder holder, final int position) {

        holder.emp_Festive.setText(empFestive[position]);
        holder.imageView.setImageResource(FestivePicture[position]);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (position == 0) {
                    holder.no_EmpFestive.setText(String.valueOf(Constants.Birthday_Count));
                } else if (position == 1) {
                    holder.no_EmpFestive.setText(String.valueOf(Constants.Anniversary_Count));
                } else if (position == 2) {
                    holder.no_EmpFestive.setText(String.valueOf(Constants.Join_Anniversary_Count));
                } else if (position == 3) {
                    holder.no_EmpFestive.setText(String.valueOf(Constants.Holiday_Count));
                } else if (position == 4) {
                    holder.no_EmpFestive.setText(String.valueOf(Constants.Thought_Count));
                } else {
                    holder.no_EmpFestive.setText(String.valueOf(Constants.Announcement_Count));
                }
            }
        }, 1000);

    }

    @Override
    public int getItemCount() {
        return empFestive.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView emp_Festive, no_EmpFestive;
        ImageView imageView;
        LinearLayout linear_dashboard_row;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.empPhoto);
            emp_Festive = itemView.findViewById(R.id.empFestive);
            no_EmpFestive = itemView.findViewById(R.id.noOfEmpFestive);
            linear_dashboard_row = itemView.findViewById(R.id.linear_dashboard_row);

            linear_dashboard_row.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
