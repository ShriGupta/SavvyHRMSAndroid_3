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

/**
 * Created by orapc7 on 9/12/2017.
 */

public class SalesActivityNotesContactAdapter extends RecyclerView.Adapter<SalesActivityNotesContactAdapter.MyViewHolder> {

    ArrayList<HashMap<String,String>> arlData;
    String type = "";
    Context context;
    View view;

    public SalesActivityNotesContactAdapter(Context context, String type, ArrayList<HashMap<String,String>> arlData){
        this.context = context;
        this.type = type;
        this.arlData = arlData;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(type.toUpperCase().trim().equals("ACTIVITY")){
           view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_activity_adapter_row,parent,false);
        } else if(type.toUpperCase().trim().equals("CONTACT")){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_contact_adapter_row,parent,false);
        } else if(type.toUpperCase().trim().equals("NOTES")){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_notes_adapter_row,parent,false);
        }

        return new SalesActivityNotesContactAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        HashMap<String,String> hashMap = arlData.get(position);
        if(type.toUpperCase().trim().equals("ACTIVITY")){
            holder.sale_activity_name.setText(hashMap.get("CustomerName"));
            holder.sale_activity_location.setText(hashMap.get("Purpose"));
            holder.sale_activity_time.setText(hashMap.get("TIME"));
        } else if(type.toUpperCase().trim().equals("CONTACT")){
            holder.sale_contact_list_of_contact.setText(hashMap.get("ContactName"));
        } else if(type.toUpperCase().trim().equals("NOTES")){
           holder.sale_notes_list_notes.setText(hashMap.get("NoteDescription"));
           holder.sale_notes_time.setText(hashMap.get("TIME"));
        }
    }

    @Override
    public int getItemCount() {
        return arlData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CustomTextView sale_activity_name, sale_activity_location,sale_activity_time;
        CustomTextView  sale_contact_list_of_contact, sale_contact_phone, sale_contact_message;
        CustomTextView sale_notes_list_notes, sale_notes_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            if(type.toUpperCase().trim().equals("ACTIVITY")){
                sale_activity_name = itemView.findViewById(R.id.sale_activity_name);
                sale_activity_location = itemView.findViewById(R.id.sale_activity_location);
                sale_activity_time = itemView.findViewById(R.id.sale_activity_time);

                sale_activity_time.setOnClickListener(this);

            } else if(type.toUpperCase().trim().equals("NOTES")){
                sale_notes_list_notes = itemView.findViewById(R.id.sale_notes_list_notes);
                sale_notes_time = itemView.findViewById(R.id.sale_notes_time);

            } else if(type.toUpperCase().trim().equals("CONTACT")){
                sale_contact_list_of_contact = itemView.findViewById(R.id.sale_contact_list_of_contact);
                sale_contact_phone = itemView.findViewById(R.id.sale_contact_phone);
                sale_contact_message = itemView.findViewById(R.id.sale_contact_message);

                sale_contact_phone.setOnClickListener(this);
                sale_contact_message.setOnClickListener(this);
            }

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
