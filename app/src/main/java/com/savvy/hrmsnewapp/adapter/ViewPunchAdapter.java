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
 * Created by orapc7 on 5/12/2017.
 */

public class ViewPunchAdapter extends RecyclerView.Adapter<ViewPunchAdapter.MyViewPunchHolder>{

    List<HashMap<String,String>> data ;
    Context context;
    private LayoutInflater inflater;


    public ViewPunchAdapter (Context context, List<HashMap<String,String>> data) {
        this.context    = context;
        this.inflater        = LayoutInflater.from(context);
        this.data       = data;
    }
    @Override
    public MyViewPunchHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_view_punch, parent, false);

        return new ViewPunchAdapter.MyViewPunchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewPunchHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);
        //Movie movie = moviesList.get(position);
        holder.punchDate.setText(mapdata.get("PUNCH_DATE"));
        holder.punchTime.setText(mapdata.get("PUNCH_TIME"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewPunchHolder extends RecyclerView.ViewHolder {
        public CustomTextView punchDate, punchTime;

        public MyViewPunchHolder(View view) {
            super(view);
            punchDate = view.findViewById(R.id.datePunchTxt);
            punchTime = view.findViewById(R.id.timePunchTxt);
           /* year = (TextView) view.findViewById(R.id.year);*/
        }
    }

}
