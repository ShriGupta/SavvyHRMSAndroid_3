package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hariom on 3/5/17.
 */
public class LeaveStatusListAdapter extends RecyclerView.Adapter<LeaveStatusListAdapter.MyViewHolder>{

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    ArrayList<Integer> arrayList;
    CustomTextView txt_applyCancel,txt_applyValue;

    public LeaveStatusListAdapter(Context context, List<HashMap<String, String>> data,ArrayList<Integer> arrayList) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
        this.data       = data;
        this.arrayList = arrayList;
        //imageLoader     = ImageLoader.getInstance();
        //imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }


    @Override
    public LeaveStatusListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_status_row, parent, false);

        return new LeaveStatusListAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(LeaveStatusListAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);
        //Movie movie = moviesList.get(position);
//        for(int k = 0;k<data.size();k++) {
//            HashMap<String, String> mapdata = data.get(k);
//            if (arrayList.get(position) == Integer.parseInt(mapdata.get("TOKEN_NO"))) {
                holder.txt_leavenameValue.setText(mapdata.get("LM_LEAVE_NAME"));
                holder.txv_tokenNoValue.setText(mapdata.get("TOKEN_NO"));
                holder.txt_fromDateValue.setText(mapdata.get("FROM_DATE"));
                holder.txt_reasonValue.setText(mapdata.get("LR_REASON"));
                holder.txt_statusValue.setText(mapdata.get("REQUEST_STATUS"));
                holder.txt_toDateValue.setText(mapdata.get("TO_DATE"));
//                holder.txt_typeValue.setText(mapdata.get("TYPE"));
                holder.txt_actionByValue.setText(mapdata.get("ACTION_BY"));
                holder.txt_actiondateValue.setText(mapdata.get("ACTION_DATE"));

                String lr_Status = mapdata.get("LR_STATUS");
                if (lr_Status.equals("0")) {
                    txt_applyValue.setVisibility(View.VISIBLE);
                    txt_applyCancel.setVisibility(View.INVISIBLE);
                } else if (lr_Status.equals("2")) {
                    txt_applyValue.setVisibility(View.INVISIBLE);
                    txt_applyCancel.setVisibility(View.VISIBLE);
                }else {
                    txt_applyValue.setVisibility(View.INVISIBLE);
                    txt_applyCancel.setVisibility(View.INVISIBLE);
                }
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CustomTextView txt_leavenameValue, txv_tokenNoValue,txt_fromDateValue,txt_reasonValue,txt_statusValue,txt_toDateValue,
                                txt_typeValue,txt_actionByValue,txt_actiondateValue;

        public MyViewHolder(View view) {
            super(view);
            txt_leavenameValue = view.findViewById(R.id.txt_leavenameValue);
            txv_tokenNoValue = view.findViewById(R.id.txv_tokenNoValue);
            txt_fromDateValue = view.findViewById(R.id.txt_fromDateValue);
            txt_reasonValue = view.findViewById(R.id.txt_reasonValue);
            txt_statusValue = view.findViewById(R.id.txt_statusValue);
            txt_toDateValue = view.findViewById(R.id.txt_toDateValue);
//            txt_typeValue = (CustomTextView) view.findViewById(R.id.txt_typeValue);
            txt_actionByValue = view.findViewById(R.id.txt_actionByValue);
            txt_actiondateValue = view.findViewById(R.id.txt_actiondateValue);

            txt_applyValue = view.findViewById(R.id.txt_actionValue);
            txt_applyCancel = view.findViewById(R.id.txt_cancleButton);

            view.setOnClickListener(this);
            txt_applyValue.setOnClickListener(this);
            txt_applyCancel.setOnClickListener(this);

           /* year = (TextView) view.findViewById(R.id.year);*/
        }

        @Override
        public void onClick(View v) {

        }
    }


}
