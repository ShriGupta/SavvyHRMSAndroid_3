package com.savvy.hrmsnewapp.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TravelDeskItineraryDetailsAdapter extends RecyclerView.Adapter<TravelDeskItineraryDetailsAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public TravelDeskItineraryDetailsAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_desk_itinerary_details_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> mapData = arrayList.get(position);

        holder.source.setText(mapData.get("DEPARTURE"));
        holder.destination.setText(mapData.get("RETURN"));
        holder.departureDate.setText(mapData.get("ID_DEPARTURE_DATE"));
        holder.returnDate.setText(mapData.get("ID_RETURN_DATE"));
        holder.modeValue.setText(mapData.get("MODE"));
        holder.classValue.setText(mapData.get("CLASS"));
        holder.typeValue.setText(mapData.get("ID_TYPE"));
        holder.travelDeskAmount.setText(mapData.get("ID_TRAVEL_DESK_AMOUNT"));
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView source, destination, departureDate, returnDate, modeValue, classValue, typeValue, travelDeskAmount;
        Button addTicket, download;

        public MyViewHolder(View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.travelDeskItinerarySource);
            destination = itemView.findViewById(R.id.travelDeskItineraryDestination);
            departureDate = itemView.findViewById(R.id.travelDeskItineraryDepartureDate);
            returnDate = itemView.findViewById(R.id.travelDeskItineraryReturnDate);
            modeValue = itemView.findViewById(R.id.travelDeskItineraryMode);
            classValue = itemView.findViewById(R.id.travelDeskItineraryClass);
            typeValue = itemView.findViewById(R.id.travelDeskItineraryType);
            travelDeskAmount = itemView.findViewById(R.id.travelDeskItinerarybudgetAmount);
            addTicket = itemView.findViewById(R.id.travelDeskItineraryAddTicket);
            download = itemView.findViewById(R.id.travelDeskItineraryDownload);

            addTicket.setOnClickListener(this);
            download.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
