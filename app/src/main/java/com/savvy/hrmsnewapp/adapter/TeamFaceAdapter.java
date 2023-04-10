package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.interfaces.OnTeamButtonClickListener;
import com.savvy.hrmsnewapp.teamFaceModel.TeamFaceDataModel;
import com.savvy.hrmsnewapp.utils.RoundedCornersTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TeamFaceAdapter extends RecyclerView.Adapter<TeamFaceAdapter.TeamFaceViewHolder> implements Filterable {
    Context context;
    List<TeamFaceDataModel> arrayList = new ArrayList<>();
    List<TeamFaceDataModel> filterList = new ArrayList<>();
    OnTeamButtonClickListener onTeamButtonClickListener;

    public TeamFaceAdapter(Context context, OnTeamButtonClickListener onTeamButtonClickListener) {
        this.context = context;
        this.onTeamButtonClickListener = onTeamButtonClickListener;
    }

    @NonNull
    @Override
    public TeamFaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_team_face_layout, parent, false);
        return new TeamFaceViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull TeamFaceViewHolder holder, int position) {

        try {
            TeamFaceDataModel data = filterList.get(position);
            holder.tvEmployeeName.setText(data.getEmployeeName());
            holder.tvEmployeeCode.setText(data.getEmployeeCode());
            holder.tvEmployeeDepartment.setText(data.getDepartment());
            holder.tvEmployeeDesignation.setText(data.getDesignation());
            holder.tvInTime.setText(data.getIntime());
            holder.tvOutTime.setText(data.getOutTime());

            try {
                Picasso.get().load(data.getPhotoCode()).error(R.drawable.profile_image).into(holder.userImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String enableStatus = data.getEnableStatus();
            assert enableStatus != null;
            switch (enableStatus) {
                case "0":
                    holder.punchIn.setEnabled(false);
                    holder.punchOut.setEnabled(false);
                    holder.punchIn.setBackgroundResource(R.color.color_gray);
                    holder.punchOut.setBackgroundResource(R.color.color_gray);
                    break;
                case "1":

                    holder.punchIn.setEnabled(true);
                    holder.punchOut.setEnabled(false);
                    holder.punchOut.setBackgroundResource(R.color.color_gray);
                    holder.punchIn.setBackgroundResource(R.color.color_blue);
                    break;
                case "2":
                    holder.punchIn.setEnabled(false);
                    holder.punchOut.setEnabled(true);
                    holder.punchIn.setBackgroundResource(R.color.color_gray);
                    holder.punchOut.setBackgroundResource(R.color.color_blue);
                    break;
                case "3":
                    holder.punchIn.setEnabled(true);
                    holder.punchOut.setEnabled(true);
                    holder.punchIn.setBackgroundResource(R.color.color_blue);
                    holder.punchOut.setBackgroundResource(R.color.color_blue);
                    break;
            }

            holder.punchIn.setOnClickListener(v -> onTeamButtonClickListener.onClick(position, data.getEmployeeCode(),data.getAttendanceDate(), "I"));
            holder.punchOut.setOnClickListener(v -> onTeamButtonClickListener.onClick(position, data.getEmployeeCode(),data.getAttendanceDate(), "O"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clearItems() {
        arrayList.clear();
        filterList.clear();
        notifyDataSetChanged();
    }
    /*public void addItems(List<HashMap<String, String>> list) {

        arrayList.addAll(list);
        filterList.addAll(list);
        notifyDataSetChanged();
    }*/

    public void addItems(List<TeamFaceDataModel> list) {
        arrayList.addAll(list);
        filterList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (!filterList.isEmpty()) {
            return filterList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterList = arrayList;
                } else {
                    List<TeamFaceDataModel> filteredList = new ArrayList<>();
                    for (TeamFaceDataModel map : arrayList) {
                        if (Objects.requireNonNull(map.getEmployeeName()).toLowerCase().contains(charString.toLowerCase())
                                || Objects.requireNonNull(map.getEmployeeCode()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(map);
                        }
                    }
                    filterList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterList = (List<TeamFaceDataModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class TeamFaceViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView tvEmployeeName, tvEmployeeCode, tvEmployeeDepartment, tvEmployeeDesignation, tvInTime, tvOutTime;
        Button punchIn, punchOut;

        public TeamFaceViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.iv_user_image);
            tvEmployeeName = itemView.findViewById(R.id.tv_employee_name);
            tvEmployeeCode = itemView.findViewById(R.id.tv_employee_code);
            tvEmployeeDepartment = itemView.findViewById(R.id.tv_department);
            tvEmployeeDesignation = itemView.findViewById(R.id.tv_designation);
            tvInTime = itemView.findViewById(R.id.tv_in_time);
            tvOutTime = itemView.findViewById(R.id.tv_out_time);
            punchIn = itemView.findViewById(R.id.punch_in);
            punchOut = itemView.findViewById(R.id.punch_out);
        }
    }
}
