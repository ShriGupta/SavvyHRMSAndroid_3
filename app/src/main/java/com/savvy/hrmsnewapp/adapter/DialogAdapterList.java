package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

/**
 * Created by orapc7 on 6/29/2017.
 */

public class DialogAdapterList extends RecyclerView.Adapter<DialogAdapterList.MyViewHolder>{

    private LayoutInflater inflater;
    //ImageLoader imageLoader;
    private Context context;
    CustomTextView txt_applyCancel,txt_applyValue;

    public DialogAdapterList(Context context) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);


    }


    @Override
    public DialogAdapterList.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approval_details_row, parent, false);

        return new DialogAdapterList.MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
//        return data.size();
        return 3;
    }

    @Override
    public void onBindViewHolder(DialogAdapterList.MyViewHolder holder, int position) {

        Log.e("Position",""+position);
        holder.imageView1.setImageResource(R.mipmap.comp_off);
        holder.imageView2.setImageResource(R.mipmap.od_request);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView1,imageView2;
        public MyViewHolder(View view) {
            super(view);


            imageView1 = view.findViewById(R.id.image1);
            imageView2 = view.findViewById(R.id.image2);

            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

        }
    }


}

