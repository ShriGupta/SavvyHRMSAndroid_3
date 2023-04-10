package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.HashMap;
import java.util.List;

public class LTFStatusFragmentAdapter extends RecyclerView.Adapter<LTFStatusFragmentAdapter.MyViewHolder> {

    Context context;
    List<HashMap<String, String>> arraList;
    Button pullBackAction;

    public LTFStatusFragmentAdapter(Context context, List<HashMap<String, String>> arraList) {
        this.context = context;
        this.arraList = arraList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ltfstatus_fragment_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        HashMap<String, String> hashMap = arraList.get(position);

        holder.ltf_TokenNo.setText("Label");
        holder.ltf_Status.setText("Label");
        holder.ltf_FromDate.setText("Label");
        holder.ltf_Todate.setText("Label");
        holder.ltf_Actionby.setText("Label");
        holder.ltf_ActionDate.setText("Label");
        holder.ltf_LeaveName.setText("Label");
        holder.ltf_Reason.setText("Label");
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CustomTextView ltf_TokenNo, ltf_Status, ltf_FromDate, ltf_Todate, ltf_Actionby, ltf_ActionDate, ltf_LeaveName, ltf_Reason;

        public MyViewHolder(View itemView) {
            super(itemView);

            ltf_TokenNo = itemView.findViewById(R.id.LTF_TokenNo);
            ltf_Status = itemView.findViewById(R.id.LTF_Status);
            ltf_FromDate = itemView.findViewById(R.id.LTF_FromDate);
            ltf_Todate = itemView.findViewById(R.id.LTF_ToDate);
            ltf_Actionby = itemView.findViewById(R.id.LTF_ActionBy);
            ltf_ActionDate = itemView.findViewById(R.id.LTF_ActionDate);
            ltf_LeaveName = itemView.findViewById(R.id.LTF_LeaveName);
            ltf_Reason = itemView.findViewById(R.id.LTF_Reason);
            pullBackAction = itemView.findViewById(R.id.LTF_PullBack);

            pullBackAction.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return arraList.size();
    }
}
