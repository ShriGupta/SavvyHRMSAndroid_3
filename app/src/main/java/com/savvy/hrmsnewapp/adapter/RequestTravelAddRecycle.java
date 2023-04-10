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
 * Created by orapc7 on 7/21/2017.
 */

public class RequestTravelAddRecycle extends RecyclerView.Adapter<RequestTravelAddRecycle.MyViewHolder>{


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    SharedPreferences shared,shareEmp;
    String employeeId = "",requestId = "",punch_status_1 = "";
    CoordinatorLayout coordinatorLayout;

    ArrayList<Integer> arrayList;
    String xmlData = "";
    String token_no = "";
    public CustomTextView trvEdit,comp_action;

    public RequestTravelAddRecycle(Context context, List<HashMap<String, String>> data) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.arrayList = arrayList;
        this.coordinatorLayout =coordinatorLayout;

        shareEmp = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shareEmp.getString("EmpoyeeId", ""));

    }


    @Override
    public RequestTravelAddRecycle.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_recycler_add_row, parent, false);
        return new RequestTravelAddRecycle.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(RequestTravelAddRecycle.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);

        holder.travel_mode.setText(mapdata.get("TravelModeValue"));
        holder.travel_class.setText(mapdata.get("TravelClassValue"));
        holder.trv_BoardingPlace.setText(mapdata.get("TravelBoardingValue"));
        holder.trv_ArrivalPlace.setText(mapdata.get("TravelArrivalValue"));
        holder.trv_BoardingDate.setText(mapdata.get("BoardingDate"));
        holder.trv_ArrivalDate.setText(mapdata.get("ArrivalDate"));
        holder.trv_ExpenseNature.setText(mapdata.get("TravelExpenseValue"));
//        holder.trv_Reason.setText(mapdata.get("Reason"));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView travel_mode, travel_class, trv_BoardingPlace, trv_ArrivalPlace, trv_BoardingDate, trv_ArrivalDate;
        public CustomTextView trv_ExpenseNature, trv_Reason;//,txt_punchclear,txt_punchPullback;

        public MyViewHolder(View view) {
            super(view);
            travel_mode = view.findViewById(R.id.travel_mode);
            travel_class = view.findViewById(R.id.travel_class);
            trv_BoardingPlace = view.findViewById(R.id.travelBoardingPlace);
            trv_ArrivalPlace = view.findViewById(R.id.travelArrivalPlace);
            trv_BoardingDate = view.findViewById(R.id.travelBoardingDate);
            trv_ArrivalDate = view.findViewById(R.id.travelArrivalDate);
            trv_ExpenseNature = view.findViewById(R.id.travelExpenseNature);
//            trv_Reason = (CustomTextView) view.findViewById(R.id.exp_reason);

//            trvEdit = (CustomTextView) view.findViewById(R.id.travelAction);

            view.setOnClickListener(this);
//            trvEdit.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}