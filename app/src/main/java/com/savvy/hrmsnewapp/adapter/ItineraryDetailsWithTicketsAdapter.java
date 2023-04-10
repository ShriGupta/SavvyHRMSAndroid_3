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

public class ItineraryDetailsWithTicketsAdapter extends RecyclerView.Adapter<ItineraryDetailsWithTicketsAdapter.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public ItineraryDetailsWithTicketsAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ItineraryDetailsWithTicketsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_details_with_tickets_adapter, parent, false);
        return new ItineraryDetailsWithTicketsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItineraryDetailsWithTicketsAdapter.MyViewHolder holder, int position) {
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


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView source, destination, departureDate, returnDate, modeValue, classValue, typeValue, travelDeskAmount;
        Button download;

        public MyViewHolder(View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.travelDeskHistoryItinerarySource);
            destination = itemView.findViewById(R.id.travelDeskHistoryItineraryDestination);
            departureDate = itemView.findViewById(R.id.travelDeskHistoryItineraryDepartureDate);
            returnDate = itemView.findViewById(R.id.travelDeskHistoryItineraryReturnDate);
            modeValue = itemView.findViewById(R.id.travelDeskHistoryItineraryMode);
            classValue = itemView.findViewById(R.id.travelDeskHistoryItineraryClass);
            typeValue = itemView.findViewById(R.id.travelDeskHistoryItineraryType);
            travelDeskAmount = itemView.findViewById(R.id.travelDeskHistoryTicketAmount);
            download = itemView.findViewById(R.id.travelDeskHistoryItineraryDownload);
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
