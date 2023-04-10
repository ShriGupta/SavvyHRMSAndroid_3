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
 * Created by savvy on 4/30/2018.
 */

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.MyViewHolder> {

    private Context context;
    private List<HashMap<String, String>> dataList;

    public AnnouncementAdapter(Context context, List<HashMap<String, String>> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txt_AnnouncementName.setText(dataList.get(position).get("AS_MESSAGE"));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final CustomTextView txt_AnnouncementName;

        public MyViewHolder(View itemView) {
            super(itemView);

            txt_AnnouncementName = itemView.findViewById(R.id.txt_AnnouncementName);
        }
    }
}
