package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> messageList;
    private ItemListener itemListener;
    private String readMore;

    public MyMessageAdapter(Context context, String readMore, ArrayList<HashMap<String, String>> messageList, ItemListener itemListener) {
        this.context = context;
        this.messageList = messageList;
        this.itemListener = itemListener;
        this.readMore = readMore;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_message, parent, false);
        return new MyMessageAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> hashMap = messageList.get(position);
        holder.tvSerialNumber.setText(String.valueOf(position + 1));
        holder.tvMessageData.setText(hashMap.get("message"));
        holder.tvMessageDate.setText("Date - " + hashMap.get("messageDate"));
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

        private final TextView tvSerialNumber;
        private final TextView tvMessageData;
        private final TextView tvMessageDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvSerialNumber = (TextView) itemView.findViewById(R.id.tvSerialNumber);
            tvMessageData = (TextView) itemView.findViewById(R.id.tvMessageData);
            tvMessageDate = (TextView) itemView.findViewById(R.id.tvMessageDate);


        }
    }

    public interface ItemListener {
        void onItemeClick(int pos);
    }
}
