package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by orapc7 on 7/21/2017.
 */

public class RequestExpAddRecycle extends RecyclerView.Adapter<RequestExpAddRecycle.MyViewHolder> {


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    List<HashMap<String, String>> data;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    SharedPreferences shared, shareEmp;
    String employeeId = "", requestId = "", punch_status_1 = "";
    CoordinatorLayout coordinatorLayout;
    String type = "";

    ArrayList<Integer> arrayList;
    String xmlData = "";
    String token_no = "";
    public CustomTextView exp_Edit, exp_remove;

    public RequestExpAddRecycle(String type, Context context, List<HashMap<String, String>> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.type = type;
        this.arrayList = arrayList;
        this.coordinatorLayout = coordinatorLayout;

        shareEmp = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        employeeId = (shareEmp.getString("EmpoyeeId", ""));

    }


    @Override
    public RequestExpAddRecycle.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exp_recycle_adapterview_row, parent, false);
        return new RequestExpAddRecycle.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RequestExpAddRecycle.MyViewHolder holder, int position) {
        if (type.equals("GEN")) {
            HashMap<String, String> mapdata = data.get(position);

            holder.exp_type.setText(mapdata.get("Type"));
            holder.exp_billno.setText(mapdata.get("Bill_No"));
            holder.exp_Date.setText(mapdata.get("Date"));
            holder.exp_Amount.setText(mapdata.get("Bill_Amount"));
            holder.exp_Reason.setText(mapdata.get("Reason"));
            holder.expense_displayName.setText(mapdata.get("displayname"));
            holder.expense_actualName.setText(mapdata.get("actualname"));

        } else if (type.equals("EDIT")) {
            HashMap<String, String> mapdata = data.get(position);

            holder.exp_type.setText(mapdata.get("EST_EXPENSE_SUB_TYPE"));
            holder.exp_billno.setText(mapdata.get("ERD_BILL_NO"));
            holder.exp_Date.setText(mapdata.get("ERD_BILL_DATE_1"));
            holder.exp_Amount.setText(mapdata.get("ERD_BILL_AMOUNT"));
            holder.exp_Reason.setText(mapdata.get("ERD_REASON"));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomTextView exp_type, exp_billno, exp_Reason;
        public CustomTextView exp_Date, exp_Amount, expense_displayName, expense_actualName;
        public EditText edt_reason;//,txt_punchclear,txt_punchPullback;

        public MyViewHolder(View view) {
            super(view);
            exp_type = view.findViewById(R.id.exp_type);
            exp_billno = view.findViewById(R.id.exp_bill_no);
            exp_Date = view.findViewById(R.id.exp_Date);
            exp_Amount = view.findViewById(R.id.exp_amount);
            exp_Reason = view.findViewById(R.id.exp_reason);

            expense_displayName = view.findViewById(R.id.expense_displayName);
            expense_actualName = view.findViewById(R.id.expense_actualName);

            exp_Edit = view.findViewById(R.id.exp_action);
            exp_remove = view.findViewById(R.id.exp_remove);

            view.setOnClickListener(this);
            exp_Edit.setOnClickListener(this);
            exp_remove.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
