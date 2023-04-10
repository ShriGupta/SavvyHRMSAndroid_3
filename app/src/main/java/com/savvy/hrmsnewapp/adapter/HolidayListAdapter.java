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
 * Created by hariom on 24/4/17.
 */
public class HolidayListAdapter  extends RecyclerView.Adapter<HolidayListAdapter.MyViewHolder> {
    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
  /*  private static final int TYPE_PROFILE = 1;
    private static final int TYPE_OPTION_MENU = 2;*/



    public HolidayListAdapter(Context context, List<HashMap<String,String>> data) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        //imageLoader     = ImageLoader.getInstance();
        //imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holiday_list_row_new, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);
        //Movie movie = moviesList.get(position);
        holder.holidaytitle.setText(mapdata.get("holidayName"));
        holder.holidayDate.setText(mapdata.get("holidayDate"));
        holder.holidayDay.setText(mapdata.get("dayName"));
       /* holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getYear());*/
    }



    @Override
    public int getItemCount() {
        return data.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextView holidaytitle, holidayDate, holidayDay;

        public MyViewHolder(View view) {
            super(view);
            holidaytitle = view.findViewById(R.id.holidaytitle);
            holidayDate = view.findViewById(R.id.holidayDate);
            holidayDay = view.findViewById(R.id.holiday_dayName);
           /* year = (TextView) view.findViewById(R.id.year);*/
        }
    }


    /*class HolidayListViewHolder extends RecyclerView.ViewHolder {
        TextView title, userName, notiCount;
        RelativeLayout layoutNoti;
        ImageView icon;
        CircularImageView profileImage;

        public HolidayListViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_PROFILE) {
                profileImage    = (CircularImageView) itemView.findViewById(R.id.user_profile_photo);
                userName        = (TextView) itemView.findViewById(R.id.txt_user_name);
            } else if (viewType == TYPE_OPTION_MENU) {
                title           = (TextView) itemView.findViewById(R.id.title);
                notiCount       = (TextView) itemView.findViewById(R.id.txt_count);
                layoutNoti      = (RelativeLayout) itemView.findViewById(R.id.layout_noti);
                icon            = (ImageView) itemView.findViewById(R.id.img_icon);
            }
        }
    }*/

}
