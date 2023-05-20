package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MyActionCenterAdapter extends RecyclerView.Adapter<MyActionCenterAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> messageList;
    private ItemListener itemListener;
    private String readMore;

    public MyActionCenterAdapter(Context context, String readMore, ArrayList<HashMap<String, String>> messageList, ItemListener itemListener) {
        this.context = context;
        this.messageList = messageList;
        this.itemListener = itemListener;
        this.readMore = readMore;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_my_action_center_item, parent, false);
        return new MyActionCenterAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = messageList.get(position);
        holder.tvSerialNumber.setText(String.valueOf(position + 1));
        holder.tvMessageData.setText(hashMap.get("message"));
        holder.tvMessageDate.setText("Date - " + hashMap.get("messageDate"));


        String moduleName = hashMap.get("moduleName");
        holder.tvModuleName.setText(moduleName);

        String employeeId = hashMap.get("employeeId");
        String actionType = hashMap.get("actionType");
        String actionMessage = hashMap.get("actionMessage");
        String apiName = hashMap.get("apiName");

        if (actionType.equals("0")) {
            holder.llActionLayout.setVisibility(View.GONE);
            holder.tvActionMessage.setVisibility(View.GONE);
        } else if (actionType.equals("1")) {
            holder.llActionLayout.setVisibility(View.VISIBLE);
            holder.tvActionMessage.setVisibility(View.GONE);
        } else if (actionType.equals("2")) {
            holder.tvActionMessage.setVisibility(View.VISIBLE);
            holder.tvActionMessage.setText(actionMessage);
        }

        holder.btnApprove.setOnClickListener(view -> itemListener.onItemeClick(employeeId,apiName,"1"));
        holder.btnReject.setOnClickListener(view -> itemListener.onItemeClick(employeeId,apiName,"0"));

    }

    @Override
    public int getItemCount() {
        if (messageList != null) {
            if (TextUtils.isEmpty(readMore)) {
                if (messageList.size() >= 20) {
                    return 20;
                } else {
                    return messageList.size();
                }
            } else {
                return messageList.size();
            }
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvSerialNumber, tvMessageData, tvMessageDate, tvActionMessage,tvModuleName;
        LinearLayout llActionLayout;
        Button btnApprove, btnReject;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvSerialNumber = (TextView) itemView.findViewById(R.id.tvSerialNumber);
            tvMessageData = (TextView) itemView.findViewById(R.id.tvMessageData);
            tvMessageDate = (TextView) itemView.findViewById(R.id.tvMessageDate);

            llActionLayout = (LinearLayout) itemView.findViewById(R.id.ll_approve_layout);
            btnApprove = (Button) itemView.findViewById(R.id.btn_approve);
            btnReject = (Button) itemView.findViewById(R.id.btn_reject);
            tvActionMessage = (TextView) itemView.findViewById(R.id.tv_action_message);
            tvModuleName = (TextView) itemView.findViewById(R.id.tv_module_name);

        }
    }

    public interface ItemListener {
        void onItemeClick(String employeeId,String actionString,String actionStatus);
    }
}
