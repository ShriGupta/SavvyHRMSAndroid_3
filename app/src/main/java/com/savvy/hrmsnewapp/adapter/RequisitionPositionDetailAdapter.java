package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.model.RequisitionRequestPositionDetModel;

import java.util.ArrayList;
import java.util.HashMap;

public class RequisitionPositionDetailAdapter extends RecyclerView.Adapter<RequisitionPositionDetailAdapter.MyViewHolder> {
    ArrayList<RequisitionRequestPositionDetModel> arrayList = new ArrayList<>();
    HashMap<Integer, Integer> hashmap = new HashMap<>();
    Context context;

    public RequisitionPositionDetailAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_requsition_position_detail_layout, parent, false);
        return new RequisitionPositionDetailAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RequisitionRequestPositionDetModel model = arrayList.get(position);
        holder.tvPosition.setText(model.getPrmPositionName());
        holder.tvHeadCount.setText(model.getPrmPositionHeadCountDayShift());
        holder.tvNoEmp.setText(model.getRrwdNoOfEmployee());
        holder.tvNoEmp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable editable) {
                if (hashmap.containsKey(position)) {
                    if (!editable.toString().equals("")) {
                        hashmap.put(position, Integer.parseInt(editable.toString()));
                        if (Integer.parseInt(editable.toString()) > Integer.parseInt(arrayList.get(position).getPrmPositionHeadCountDayShift())) {

                            Toast.makeText(context, "Please enter number of employee less than head count!", Toast.LENGTH_SHORT).show();
                            holder.tvNoEmp.setText("0");
                        }
                    }

                } else {
                    if (!editable.toString().equals("")) {
                        hashmap.put(position, Integer.parseInt(editable.toString()));
                        if (Integer.parseInt(editable.toString()) > Integer.parseInt(arrayList.get(position).getPrmPositionHeadCountDayShift())) {
                            Toast.makeText(context, "Please enter number of employee less than head count!", Toast.LENGTH_SHORT).show();
                            holder.tvNoEmp.setText("0");
                        }
                    }

                }

            }
        });
    }

    public void addItems(ArrayList<RequisitionRequestPositionDetModel> list) {
        arrayList.clear();
        arrayList.addAll(list);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosition, tvHeadCount;
        EditText tvNoEmp;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvHeadCount = itemView.findViewById(R.id.tv_head_count);
            tvNoEmp = itemView.findViewById(R.id.tv_no_emp);
        }
    }

    public String createXMLString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (arrayList.size() > 0) {
            if(hashmap.size()>0){
                for (int i = 0; i < arrayList.size(); i++) {
                    int numofEmp = 0;
                    if (hashmap.containsKey(i)) {
                        numofEmp = hashmap.get(i);
                    }
                    stringBuilder.append("@").append(arrayList.get(i).getPrmPositionRoleId()).append(",")
                            .append(numofEmp);
                }
            }else {
                return "";
            }

        } else {
            return "";
        }
        return stringBuilder.toString();
    }
}
