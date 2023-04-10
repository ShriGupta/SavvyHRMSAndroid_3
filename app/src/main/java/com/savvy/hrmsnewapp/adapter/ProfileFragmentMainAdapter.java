package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.DashBoardActivity;
import com.savvy.hrmsnewapp.customwidgets.CircularImageView;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.retrofitModel.MenuModule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProfileFragmentMainAdapter extends RecyclerView.Adapter<ProfileFragmentMainAdapter.MyViewHolder> {
    Context context;
    List<MenuModule> arrayList;

    public ProfileFragmentMainAdapter(Context context, List<MenuModule> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_fragment_main_adapter_row, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.customTextView.setText(arrayList.get(position).getPrivilegeName());
            Log.e("menu item url",">>"+arrayList.get(position).getMenuImageURL());

            if (arrayList.get(position).getMenuImageURL().equals("")) {
                holder.circleImageView.setImageResource(R.drawable.custom_round_image);
            } else {
                Picasso.get().load(arrayList.get(position).getMenuImageURL()).error(R.drawable.custom_round_image).into(holder.circleImageView);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            holder.circleImageView.setImageResource(R.drawable.custom_round_image);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView circleImageView;
        CustomTextView customTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.adapter_image_view);
            customTextView = itemView.findViewById(R.id.adapter_TextView);
            itemView.setOnClickListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View view) {
            String privilegeId = arrayList.get(getAdapterPosition()).getPrivilegeId();
            String privilegeName = arrayList.get(getAdapterPosition()).getPrivilegeName();
            System.out.println(privilegeId + "   : " + privilegeName);
            if (context instanceof DashBoardActivity) {
                ((DashBoardActivity) context).onDrawerItemSelected(view, privilegeId, privilegeName);
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
