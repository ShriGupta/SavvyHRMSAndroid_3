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

/**
 * Created by orapc7 on 7/22/2017.
 */

public class StatusConveyanceAdapter extends RecyclerView.Adapter<StatusConveyanceAdapter.MyViewHolder>{

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    SharedPreferences shared;
    ArrayList<Integer> arrayList;
    CoordinatorLayout coordinatorLayout;

    String token_no = "";
    public CustomTextView txt_odclear,txt_odPullback;
    CustomTextView txt_conPullBack, txt_conEdit, txt_conCancel;


    public StatusConveyanceAdapter(Context context,CoordinatorLayout coordinatorLayout ,List<HashMap<String, String>> data) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.arrayList = arrayList;
        this.coordinatorLayout =coordinatorLayout;

    }


    @Override
    public StatusConveyanceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conveyance_status_row_adapter, parent, false);
        return new StatusConveyanceAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(StatusConveyanceAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);
        try {
            holder.txt_conToken_no.setText(mapdata.get("TOKEN_NO"));
            holder.txt_conReqAmount.setText(mapdata.get("CR_REQUESTED_AMOUNT"));
            holder.txt_ConStatus.setText(mapdata.get("REQUEST_STATUS"));
            holder.txt_ConBillDate.setText(mapdata.get("REQUESTED_DATE"));
            holder.txt_ConRemarks.setText(mapdata.get("CR_REMARKS"));
            holder.txt_ConApprovedAmt.setText(mapdata.get("CR_APPROVED_AMOUNT"));

            String status = mapdata.get("CONVEYANCE_STATUS_1");
            String status1 = mapdata.get("CONVEYANCE_STATUS");

            if(status.equals("0")){
                txt_conPullBack.setVisibility(View.VISIBLE);
                txt_conCancel.setVisibility(View.INVISIBLE);
            } else if(status.equals("2")){
                txt_conPullBack.setVisibility(View.INVISIBLE);
                txt_conCancel.setVisibility(View.VISIBLE);
            } else{
                txt_conPullBack.setVisibility(View.INVISIBLE);
                txt_conCancel.setVisibility(View.INVISIBLE);
            }

//            if(status1.equals("0")){
//                txt_conEdit.setVisibility(View.VISIBLE);
//            } else{
//                txt_conEdit.setVisibility(View.INVISIBLE);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CustomTextView txt_conToken_no,txt_conReqAmount,txt_ConBillDate,txt_ConRemarks,txt_ConStatus;
        public CustomTextView txt_ConApprovedAmt;//txt_odclear,txt_odPullback;

        public MyViewHolder(View view) {
            super(view);
            txt_conToken_no = view.findViewById(R.id.con_TokenNo);
            txt_conReqAmount = view.findViewById(R.id.con_reqAmt);
            txt_ConStatus = view.findViewById(R.id.con_Status);
            txt_ConBillDate = view.findViewById(R.id.con_BillDate);
            txt_ConRemarks = view.findViewById(R.id.con_Remarks);
            txt_ConApprovedAmt = view.findViewById(R.id.con_ApprAmt);

            txt_conEdit = view.findViewById(R.id.con_edit);
            txt_conPullBack = view.findViewById(R.id.con_pullback);
            txt_conCancel = view.findViewById(R.id.con_cancel);

            txt_conEdit.setOnClickListener(this);
            txt_conPullBack.setOnClickListener(this);
            txt_conCancel.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }



}
