package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 9/19/2017.
 */

public class TravelVoucherExpenseAdapter extends RecyclerView.Adapter<TravelVoucherExpenseAdapter.MyViewHolder> {

    Context context;
    String Type = "";
    List<HashMap<String,String>> data;
    CustomTextView TRE_Edit,TRE_Remove;

    View view;

    public TravelVoucherExpenseAdapter(Context context, String Type, List<HashMap<String, String>> data){
        this.context = context;
        this.Type = Type;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(Type.toUpperCase().trim().equals("TRAVEL_EXPENSE")){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_expense_add_voucher_row,parent,false);
        } else  if(Type.toUpperCase().trim().equals("TRAVEL_EXPENSE_DETAIL")){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_expense_detail_row_voucher,parent,false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(Type.toUpperCase().trim().equals("TRAVEL_EXPENSE")){
            HashMap<String, String> hashMap = data.get(position);

            holder.txt_ExpenseType.setText(hashMap.get("TRE_ExpenseType"));
            holder.txt_BillNo.setText(hashMap.get("TRE_BillNo"));
            holder.txt_BillDate.setText(hashMap.get("TRE_BillDate"));
            holder.txt_BillAmount.setText(hashMap.get("TRE_BillAmount"));
            holder.txt_ComProvided.setText(hashMap.get("TRE_CompanyProvided"));
            holder.txt_Reason.setText(hashMap.get("TRE_Remarks"));
        }
        if(Type.toUpperCase().trim().equals("TRAVEL_EXPENSE_DETAIL")){
            HashMap<String, String> hashMap = data.get(position);

            holder.txt_travelMode.setText(hashMap.get("TM_TRAVEL_MODE_NAME"));
            holder.txt_travelClass.setText(hashMap.get("TC_TRAVEL_CLASS_NAME"));
            holder.txt_BPlace.setText(hashMap.get("TRD_BOARDING_PLACE_NAME"));
            holder.txt_APlace.setText(hashMap.get("TRD_ARRIVAL_PLACE_NAME"));
            holder.txt_BDate.setText(hashMap.get("TRD_BOARDING_DATE"));
            holder.txt_ADate.setText(hashMap.get("TRD_ARRIVAL_DATE"));
            holder.txt_ExpenseTypeDetail.setText(hashMap.get("TEN_TRAVEL_EXPENSE_NATURE_NAME"));
            holder.txt_Ticket.setText(hashMap.get("TRD_IS_TICKET"));
            holder.txt_Hotel.setText(hashMap.get("TRD_IS_HOTEL"));
            holder.txt_Cab.setText(hashMap.get("TRD_IS_CAB"));
            holder.txt_ReasonTravel.setText(hashMap.get("TRD_REASON"));

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CustomTextView txt_ExpenseType, txt_BillNo, txt_BillDate, txt_BillAmount, txt_ComProvided, txt_Reason;

        CustomTextView txt_travelMode, txt_travelClass, txt_BPlace, txt_APlace, txt_BDate, txt_ADate,
                    txt_ExpenseTypeDetail, txt_Ticket, txt_Hotel, txt_Cab, txt_ReasonTravel;
        public MyViewHolder(View view) {
            super(view);

            if(Type.toUpperCase().trim().equals("TRAVEL_EXPENSE")){
                txt_ExpenseType = view.findViewById(R.id.TREexpenseType);
                txt_BillNo = view.findViewById(R.id.TREBillNo);
                txt_BillDate = view.findViewById(R.id.TREBillDate);
                txt_BillAmount = view.findViewById(R.id.TREBillAmount);
                txt_ComProvided = view.findViewById(R.id.TREComProvided);
                txt_Reason = view.findViewById(R.id.TRE_Reason);

                TRE_Edit = view.findViewById(R.id.TRE_Edit);
                TRE_Remove = view.findViewById(R.id.TRE_Remove);

                TRE_Edit.setVisibility(View.GONE);
                TRE_Remove.setVisibility(View.GONE);

                TRE_Edit.setOnClickListener(this);
                TRE_Remove.setOnClickListener(this);
            }
            if(Type.toUpperCase().trim().equals("TRAVEL_EXPENSE_DETAIL")){
                txt_travelMode = view.findViewById(R.id.TREDtravel_mode);
                txt_travelClass = view.findViewById(R.id.TREDtravel_class);
                txt_BPlace = view.findViewById(R.id.TREDBoardingPlace);
                txt_APlace = view.findViewById(R.id.TREDArrivalPlace);
                txt_BDate = view.findViewById(R.id.TREDBoardingDate);
                txt_ADate = view.findViewById(R.id.TREDArrivalDate);
                txt_ExpenseTypeDetail = view.findViewById(R.id.TREDExpenseType);
                txt_Ticket = view.findViewById(R.id.TREDTicket);
                txt_Hotel = view.findViewById(R.id.TREDHotel);
                txt_Cab = view.findViewById(R.id.TREDCab);
                txt_ReasonTravel = view.findViewById(R.id.txt_ReasonTravel);

            }

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
