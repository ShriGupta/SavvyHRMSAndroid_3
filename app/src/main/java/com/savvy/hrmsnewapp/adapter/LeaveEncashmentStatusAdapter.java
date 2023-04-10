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


public class LeaveEncashmentStatusAdapter extends RecyclerView.Adapter<LeaveEncashmentStatusAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> data;
    private LayoutInflater inflater;
    private Context context;
    SharedPreferences shared;
    CoordinatorLayout coordinatorLayout;
    CustomTextView txt_odPullback;

    public LeaveEncashmentStatusAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.coordinatorLayout = coordinatorLayout;

    }


    @Override
    public LeaveEncashmentStatusAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_encashment_status_row, parent, false);
        return new LeaveEncashmentStatusAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(LeaveEncashmentStatusAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = data.get(position);

        holder.LE_Token_no.setText(mapdata.get("TOKEN_NO"));
        holder.LE_type.setText(mapdata.get("LM_LEAVE_NAME"));
        holder.LE_ApprovalEncash_days.setText(mapdata.get("LER_APPROVED_NO_OF_DAYS"));
        holder.LE_comments.setText(mapdata.get("LER_REASON"));
        holder.LE_Status.setText(mapdata.get("REQUEST_STATUS"));
        holder.LE_NO_of_Days.setText(mapdata.get("LER_NO_OF_DAYS"));

        String LE_status = mapdata.get("LEAVE_ENCASHMENT_STATUS");
        if (LE_status.equals("0")) {
            txt_odPullback.setVisibility(View.VISIBLE);
        } else if (LE_status.equals("2")) {
            txt_odPullback.setVisibility(View.INVISIBLE);
        } else {
            txt_odPullback.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView LE_Token_no, LE_type, LE_ApprovalEncash_days, LE_comments, LE_Status, LE_NO_of_Days, LE_action;
        CustomTextView txt_ActionValuePull;

        public MyViewHolder(View view) {
            super(view);
            LE_Token_no = view.findViewById(R.id.txt_LE_token);
            LE_type = view.findViewById(R.id.txt_LE_type);
            LE_ApprovalEncash_days = view.findViewById(R.id.txt_LE_days);
            LE_comments = view.findViewById(R.id.txt_LE_Comments);
            LE_Status = view.findViewById(R.id.txt_LE_status_value);
            LE_NO_of_Days = view.findViewById(R.id.txt_LE_No_ofDays);
            LE_action = view.findViewById(R.id.txt_LE_action);

            txt_odPullback = view.findViewById(R.id.txt_LE_action);

            LE_action.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}