package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.INOUTRequestFragment;
import com.savvy.hrmsnewapp.interfaces.FilePathListener;
import com.savvy.hrmsnewapp.model.FileNameModel;

import java.util.List;

public class AddMultipleItemsAdapter extends RecyclerView.Adapter<AddMultipleItemsAdapter.MyViewHolder> {

    Context context;
    List<FileNameModel> CustomerLists;


    public AddMultipleItemsAdapter(Context context, List<FileNameModel> customerList) {
        this.context = context;
        this.CustomerLists = customerList;

    }

    @Override
    public AddMultipleItemsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_multiple_item_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AddMultipleItemsAdapter.MyViewHolder holder, final int position) {
        FileNameModel fileNameModel=CustomerLists.get(position);
        Log.e("fileNameModel>>","<>"+fileNameModel.getFile_name());
        if (!fileNameModel.getFile_name().equals("")) {
            holder.txt_TEnoFileChoose.setText("" + fileNameModel.getFile_name());
        }else {

            holder.txt_TEnoFileChoose.setText(" upload file");
        }
       holder.cancel_img.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               CustomerLists.remove(position);
               notifyDataSetChanged();
           }
       });
    }

    @Override
    public int getItemViewType(int position) {
        //   ErrorMessage.E("getItemViewType>>" + position);
        return position;
    }

    @Override
    public long getItemId(int position) {
        //   ErrorMessage.E("getItemId>>" + position);
        return position;
    }

    @Override
    public int getItemCount() {
        return CustomerLists.size();
    }


    protected static class MyViewHolder extends RecyclerView.ViewHolder {

        Button txt_TEnoFileChoose;
        ImageButton cancel_img;


        public MyViewHolder(View view) {
            super(view);

            txt_TEnoFileChoose = view.findViewById(R.id.txt_TEnoFileChoose);
            cancel_img = view.findViewById(R.id.cancel_img);


        }
    }
}
