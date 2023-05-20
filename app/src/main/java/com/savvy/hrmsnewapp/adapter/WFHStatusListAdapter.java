package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

public class WFHStatusListAdapter extends RecyclerView.Adapter<WFHStatusListAdapter.MyViewHolder> {

    List<HashMap<String, String>> data;
    CoordinatorLayout coordinatorLayout;
    public CustomTextView txt_odclear, txt_odPullback;


    public WFHStatusListAdapter(Context context, CoordinatorLayout coordinatorLayout, List<HashMap<String, String>> data) {
        this.data = data;
        this.coordinatorLayout = coordinatorLayout;
    }

    @Override
    public WFHStatusListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wfh_status_row, parent, false);
        return new WFHStatusListAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(WFHStatusListAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> mapdata = data.get(position);
        try {
            String OdRequestSubType = mapdata.get("WFH_REQUEST_SUB_TYPE_NAME");
            String ToDate = mapdata.get("TO_DATE");

            holder.txt_punchToken_no.setText(mapdata.get("TOKEN_NO"));
            holder.txt_fromdate_value.setText(mapdata.get("FROM_DATE"));
            holder.txt_todate_value.setText(mapdata.get("TO_DATE"));

            holder.txt_odRequestType_value.setText(mapdata.get("WFH_REQUEST_TYPE_NAME"));
            holder.txt_odRequestSubType_value.setText(mapdata.get("WFH_REQUEST_SUB_TYPE_NAME"));
            holder.txt_odStatus_value.setText(mapdata.get("REQUEST_STATUS"));
            holder.txt_odctionby_value.setText(mapdata.get("REQUESTED_BY"));
            holder.txt_odactiondate_value.setText(mapdata.get("REQUESTED_DATE"));
            holder.txt_reason.setText(mapdata.get("WFH_REASON"));

            if (OdRequestSubType.equals(""))
                holder.txt_odRequestSubType_value.setText("    -");
            if (ToDate.equals(""))
                holder.txt_todate_value.setText("    -");

            String od_Status = mapdata.get("WFH_STATUS_1");
            if (od_Status.equals("0")) {
                txt_odclear.setVisibility(View.INVISIBLE);
                txt_odPullback.setVisibility(View.VISIBLE);
            } else if (od_Status.equals("2")) {
                txt_odclear.setVisibility(View.VISIBLE);
                txt_odPullback.setVisibility(View.INVISIBLE);
            } else {
                txt_odclear.setVisibility(View.INVISIBLE);
                txt_odPullback.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView txt_punchToken_no, txt_fromdate_value, txt_todate_value, txt_odType_value, txt_odRequestType_value;
        public CustomTextView txt_odRequestSubType_value, txt_odStatus_value, txt_odctionby_value, txt_odactiondate_value, txt_reason;//txt_odclear,txt_odPullback;
        CustomTextView txt_actionValuePull, txt_actionValueCancel;

        public MyViewHolder(View view) {
            super(view);
            txt_punchToken_no = view.findViewById(R.id.txt_odToken_value);
            txt_fromdate_value = view.findViewById(R.id.txt_fromodDate_value);
            txt_todate_value = view.findViewById(R.id.txt_toOd_date);
            txt_odType_value = view.findViewById(R.id.txt_od_Type_Value);
            txt_odRequestType_value = view.findViewById(R.id.txt_requestType_value);
            txt_odRequestSubType_value = view.findViewById(R.id.requestsubType_value);
            txt_odStatus_value = view.findViewById(R.id.txt_od_status_value);
            txt_odctionby_value = view.findViewById(R.id.txt_actionBy_value);
            txt_odactiondate_value = view.findViewById(R.id.txt_actionOdDate);
            txt_odclear = view.findViewById(R.id.txt_cancel_od);
            txt_odPullback = view.findViewById(R.id.txt_pullback_od);
            txt_reason = view.findViewById(R.id.txt_reason_value);


            txt_actionValuePull = view.findViewById(R.id.txt_pullback_od);
            txt_actionValueCancel = view.findViewById(R.id.txt_cancel_od);

            txt_actionValuePull.setOnClickListener(this);
            txt_actionValueCancel.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
