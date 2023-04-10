package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by orapc7 on 7/20/2017.
 */

public class Conveyance_Add_fieldAdapter extends RecyclerView.Adapter<Conveyance_Add_fieldAdapter.MyViewHolder> {


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    List<HashMap<String, String>> data;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    SharedPreferences shared, shareEmp;
    String employeeId = "", requestId = "", punch_status_1 = "";
    CoordinatorLayout coordinatorLayout;

    ArrayList<Integer> arrayList;
    String xmlData = "";
    String token_no = "";
    public CustomTextView con_Edit, con_Remove, comp_action;

    String type = "";

    public Conveyance_Add_fieldAdapter(String type, Context context, List<HashMap<String, String>> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.arrayList = arrayList;
        this.type = type;
        this.coordinatorLayout = coordinatorLayout;

        shareEmp = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shareEmp.getString("EmpoyeeId", ""));
        Constants.CONVEYANCE_EDIT = new HashMap<Integer, HashMap<String, String>>();

    }


    @Override
    public Conveyance_Add_fieldAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_conve_row_main, parent, false);
        return new Conveyance_Add_fieldAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(Conveyance_Add_fieldAdapter.MyViewHolder holder, int position) {

        if (type.equals("Gen")) {
            Log.e("Conveyance", "Inside Gen");
            HashMap<String, String> mapdata = data.get(position);

            holder.con_type.setText(mapdata.get("TYPE"));
            holder.con_placeFrom.setText(mapdata.get("Place_From"));
            holder.con_Distance.setText(mapdata.get("Distance"));
            holder.con_Reason.setText(mapdata.get("Reason"));
            holder.con_Date.setText(mapdata.get("Date"));
            holder.con_placeTo.setText(mapdata.get("Place_To"));
            holder.con_Amount.setText(mapdata.get("Bill_Amount"));
            holder.con_DisplayName.setText(mapdata.get("displayFileName"));
            holder.con_ActualName.setText(mapdata.get("actualFileName"));
            Constants.CONVEYANCE_EDIT.put(position, mapdata);

        } else if (type.equals("Edit")) {
            Log.e("Conveyance", "Inside Edit");
            HashMap<String, String> mapdata = data.get(position);

            holder.con_type.setText(mapdata.get("EST_EXPENSE_SUB_TYPE"));
            holder.con_placeFrom.setText(mapdata.get("CRD_PLACE_FROM"));
            holder.con_Distance.setText(mapdata.get("CRD_DISTANCE"));
            holder.con_Reason.setText(mapdata.get("CRD_REASON"));
            holder.con_Date.setText(mapdata.get("CRD_BILL_DATE_1"));
            holder.con_placeTo.setText(mapdata.get("CRD_PLACE_TO"));
            holder.con_Amount.setText(mapdata.get("CRD_BILL_AMOUNT"));
//            holder.conDetail.setText(mapdata.get("CRD_CONVEYANCE_REQUEST_DETAIL_ID"));
//            holder.conDetail.setText("" + position);

            Constants.CONVEYANCE_EDIT.put(position, mapdata);
        }
//        }else {
//            Log.e("Conveyance", "OutSide Edit");
//            HashMap<String, String> mapdata = data.get(position);
//
//            holder.con_type.setText(mapdata.get("EST_EXPENSE_SUB_TYPE"));
//            holder.con_placeFrom.setText(mapdata.get("CRD_PLACE_FROM"));
//            holder.con_Distance.setText(mapdata.get("CRD_DISTANCE"));
//            holder.con_Reason.setText(mapdata.get("CRD_REASON"));
//            holder.con_Date.setText(mapdata.get("CRD_BILL_DATE"));
//            holder.con_placeTo.setText(mapdata.get("CRD_PLACE_TO"));
//            holder.con_Amount.setText(mapdata.get("CRD_BILL_AMOUNT"));
//        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView con_type, con_placeFrom, con_placeTo, con_Distance, con_Reason, con_DisplayName, con_ActualName;
        public CustomTextView con_Date, con_Amount, conDetail;//,txt_punchclear,txt_punchPullback;

        public MyViewHolder(View view) {
            super(view);
            con_type = view.findViewById(R.id.con_type);
            con_placeFrom = view.findViewById(R.id.con_placeFrom);
            con_Distance = view.findViewById(R.id.con_Distance);
            con_Reason = view.findViewById(R.id.con_reason);
            con_Date = view.findViewById(R.id.con_Date);
            con_placeTo = view.findViewById(R.id.con_placeTo);
            con_Amount = view.findViewById(R.id.con_amount);
            con_DisplayName = view.findViewById(R.id.dName);
            con_ActualName = view.findViewById(R.id.actualName);
//            conDetail = (CustomTextView) view.findViewById(R.id.con_hidden_detail);

//            con_Edit = (CustomTextView) view.findViewById(R.id.con_action);
//            con_Remove = (CustomTextView) view.findViewById(R.id.con_remove);

            view.setOnClickListener(this);
//            con_Edit.setOnClickListener(this);
//            con_Remove.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}