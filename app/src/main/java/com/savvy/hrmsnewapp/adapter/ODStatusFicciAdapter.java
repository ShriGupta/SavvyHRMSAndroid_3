package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

public class ODStatusFicciAdapter extends RecyclerView.Adapter<ODStatusFicciAdapter.MyViewHolder> {

    List<HashMap<String, String>> data;
    Context context;
    LayoutInflater inflater;
    CoordinatorLayout coordinatorLayout;
    public CustomTextView txt_odclear, txt_odPullback;
    String odtype;

    public ODStatusFicciAdapter(Context context, CoordinatorLayout coordinatorLayout, List<HashMap<String, String>> data, String odtype) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.coordinatorLayout = coordinatorLayout;
        this.odtype = odtype;
    }

    @Override
    public ODStatusFicciAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.od_status_ficci_row, parent, false);

        return new ODStatusFicciAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ODStatusFicciAdapter.MyViewHolder holder, int position) {

        HashMap<String, String> mapdata = data.get(position);
        try {
            String ToDate = mapdata.get("TO_DATE");

            holder.txt_punchToken_no.setText(mapdata.get("TOKEN_NO"));
            holder.txt_fromdate_value.setText(mapdata.get("FROM_DATE"));
            holder.txt_todate_value.setText(mapdata.get("TO_DATE"));
            holder.txt_odStatus_value.setText(mapdata.get("REQUEST_STATUS"));
            holder.txt_odctionby_value.setText(mapdata.get("REQUESTED_BY"));
            holder.txt_odactiondate_value.setText(mapdata.get("REQUESTED_DATE"));
            holder.txt_reason.setText(mapdata.get("REASON").replace("_", " "));

            if (odtype.equals("ODCONTRACT")) {
                holder.viewDetail.setVisibility(View.VISIBLE);
            } else {
                holder.viewDetail.setVisibility(View.INVISIBLE);
            }
            if (ToDate.equals(""))
                holder.txt_todate_value.setText("    -");

            String od_Status = mapdata.get("OD_STATUS_1");
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView txt_punchToken_no, txt_fromdate_value, txt_todate_value;
        public CustomTextView txt_odStatus_value, txt_odctionby_value, txt_odactiondate_value, txt_reason;//txt_odclear,txt_odPullback;
        CustomTextView txt_actionValuePull, txt_actionValueCancel, viewDetail;

        public MyViewHolder(View view) {
            super(view);
            txt_punchToken_no = view.findViewById(R.id.tv_odToken_value);
            txt_fromdate_value = view.findViewById(R.id.tv_fromodDate_value);
            txt_odctionby_value = view.findViewById(R.id.tv_actionBy_value);
            txt_todate_value = view.findViewById(R.id.tv_toOd_date);
            txt_odStatus_value = view.findViewById(R.id.tv_od_status_value);
            txt_odactiondate_value = view.findViewById(R.id.tv_actionOdDate);
            txt_reason = view.findViewById(R.id.tv_reason_value);
            txt_odPullback = view.findViewById(R.id.tv_pullback_od);
            txt_actionValueCancel = view.findViewById(R.id.tv_cancel_od);

            txt_odclear = view.findViewById(R.id.tv_cancel_od);
            txt_actionValuePull = view.findViewById(R.id.tv_pullback_od);
            viewDetail = view.findViewById(R.id.tv_odViewDetail);

            txt_actionValuePull.setOnClickListener(this);
            txt_actionValueCancel.setOnClickListener(this);
            viewDetail.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
