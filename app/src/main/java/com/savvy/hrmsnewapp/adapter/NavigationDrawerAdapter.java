package com.savvy.hrmsnewapp.adapter;

/**
 * Created by Ravi on 29/07/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CircularImageView;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.retrofitModel.MenuModule;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.NavDrawerViewHolder> {

    List<MenuModule> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    long row_index = -1;
    private static final int TYPE_PROFILE = 1;
    private static final int TYPE_OPTION_MENU = 2;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;

    private static int[] navImages = {R.drawable.ic_dashboard, R.drawable.ic_dashboard, R.drawable.ic_timetable, R.drawable.ic_attendance, R.drawable.ic_academics, R.drawable.ic_notice, R.drawable.ic_calender, R.drawable.ic_feedback, R.drawable.ic_mail, R.drawable.ic_dashboard, R.drawable.ic_dashboard, R.drawable.ic_timetable, R.drawable.ic_attendance, R.drawable.ic_academics, R.drawable.ic_notice, R.drawable.ic_calender, R.drawable.ic_feedback, R.drawable.ic_mail, R.drawable.ic_mail};
    private String urlStr = "";

    public NavigationDrawerAdapter(Context context, List<MenuModule> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        shared = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        //imageLoader     = ImageLoader.getInstance();
        //imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 ? TYPE_PROFILE : TYPE_OPTION_MENU);
    }

    @Override
    public NavDrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_PROFILE)
            view = inflater.inflate(R.layout.nav_drawer_row_profile, parent, false);
        else if (viewType == TYPE_OPTION_MENU)
            view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        return new NavDrawerViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final NavDrawerViewHolder holder, int position) {

        final int pos = position;
        //urlStr = profileDB.getImageUrl();


        String photopath = (shared.getString("EmpPhotoPath", ""));
        String username = (shared.getString("EmpoyeeName", ""));


        if (holder.getItemViewType() == TYPE_PROFILE) {

            if (!photopath.equals("") && photopath.length() > 0) {
                String photourl = photopath.replace("\\", "")  + "?" + System.currentTimeMillis();
                //Picasso.with(context).load(photourl).into(holder.profileImage);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.error(R.drawable.profile_image);
                Glide.with(context).load(photourl).apply(requestOptions).into(holder.profileImage);
            }

            holder.userName.setText(username);
        } else if (holder.getItemViewType() == TYPE_OPTION_MENU) {
            if (data.size() > 0) {
                MenuModule current = data.get(position);

                String privillageId = current.getPrivilegeId();

                //Picasso.with(context).load(navImages[position]).into(holder.icon);

                holder.title.setText(current.getPrivilegeName());

//                if (privillageId.equals("2")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.calendar_view,0,0,0);
//                } else if (privillageId.equals("3")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.calendar_team,0,0,0);
//                } else if (privillageId.equals("4")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.mark_attendance,0,0,0);
//                } else if (privillageId.equals("5")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.team_attendance,0,0,0);
//                } else if (privillageId.equals("6")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.holiday_icon,0,0,0);
//                } else if (privillageId.equals("7")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.payslip,0,0,0);
//                } else if (privillageId.equals("8")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.leave_request,0,0,0);
//                } else if (privillageId.equals("9")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.od_request,0,0,0);
//                } else if (privillageId.equals("10")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.punch_request,0,0,0);
//                } else if (privillageId.equals("11")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.new_comp_off,0,0,0);
//                } else if (privillageId.equals("12")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.leave_encash,0,0,0);
//                } else if (privillageId.equals("13")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.new_comp_off1,0,0,0);
//                } else if (privillageId.equals("14")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.new_approval,0,0,0);
//                } else if (privillageId.equals("15")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.new_approval,0,0,0);
//                } else if (privillageId.equals("16")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.new_approval,0,0,0);
//                } else if (privillageId.equals("17")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.comp_off_approve,0,0,0);
//                } else if (privillageId.equals("18")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.new_approval,0,0,0);
//                } else if (privillageId.equals("19")) {
//                    holder.title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.new_approval,0,0,0);
//                }


                holder.linear_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        row_index = pos;
                        notifyDataSetChanged();
                    }
                });
                if (row_index == position) {
                    holder.linear_back.setBackgroundColor(Color.parseColor("#ffffff"));
                    holder.title.setTextColor(context.getResources().getColor(R.color.my_custom_color));
                } else {
                    holder.linear_back.setBackgroundColor(Color.parseColor("#277ddb"));
                    holder.title.setTextColor(Color.parseColor("#ffffff"));
                }


                //holder.icon.setBackgroundResource(navImages[position]);

                /*if (position == 1) {
                    holder.layoutNoti.setVisibility(View.VISIBLE);
                    holder.notiCount.setText(current.getNotification_count());
                } else if (position == 4) {
                    holder.layoutNoti.setVisibility(View.VISIBLE);
                    holder.notiCount.setText(current.getNotification_count());
                } else if (position == 5) {
                    holder.layoutNoti.setVisibility(View.VISIBLE);
                    holder.notiCount.setText(current.getNotification_count());
                } else if (position == 8) {
                    holder.layoutNoti.setVisibility(View.VISIBLE);
                    holder.notiCount.setText(current.getNotification_count());
                } else {
                    holder.layoutNoti.setVisibility(View.GONE);
                }*/
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class NavDrawerViewHolder extends RecyclerView.ViewHolder {
        CustomTextView title, userName, notiCount;
        RelativeLayout layoutNoti;
        LinearLayout linear_back;
        ImageView icon;
        // ImageView profileImage;
        CircularImageView profileImage;


        public NavDrawerViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_PROFILE) {
                profileImage = itemView.findViewById(R.id.user_profile_photo_Round);
                userName = itemView.findViewById(R.id.txt_user_name);
            } else if (viewType == TYPE_OPTION_MENU) {
                title = itemView.findViewById(R.id.title);
                notiCount = itemView.findViewById(R.id.txt_count);
                layoutNoti = itemView.findViewById(R.id.layout_noti);
                icon = itemView.findViewById(R.id.img_icon);
                linear_back = itemView.findViewById(R.id.linear_background);
            }
        }
    }

}
