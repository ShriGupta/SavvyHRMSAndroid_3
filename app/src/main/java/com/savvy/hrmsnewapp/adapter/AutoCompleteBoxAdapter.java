package com.savvy.hrmsnewapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.model.MeMyTeamObject;
import com.savvy.hrmsnewapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by savvy on 4/23/2018.
 */

public class AutoCompleteBoxAdapter extends ArrayAdapter<MeMyTeamObject> {

    private Context context;
    private List<MeMyTeamObject> modelList;
    int resource, textViewResourceId;
    List<MeMyTeamObject> items, tempItems, suggestions;

    public AutoCompleteBoxAdapter(Context context, int resource, List<MeMyTeamObject> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;

        tempItems = new ArrayList<MeMyTeamObject>(items); // this makes the difference.
        suggestions = new ArrayList<MeMyTeamObject>();
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.item_autocomplete_view, null);
        }

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvId = view.findViewById(R.id.tvId);

        tvName.setText(items.get(position).getEMPLOYEE_NAME());
        tvId.setText(items.get(position).getEMPLOYEE_CODE());

        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((MeMyTeamObject) resultValue).getEMPLOYEE_NAME();
            Constants.EMPLOYEE_CODE = ((MeMyTeamObject) resultValue).getEMPLOYEE_CODE();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (MeMyTeamObject people : tempItems) {
                    if (people.getEMPLOYEE_NAME().toLowerCase().contains(constraint.toString().toLowerCase())
                            || people.getEMPLOYEE_CODE().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<MeMyTeamObject> filterList = (ArrayList<MeMyTeamObject>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (MeMyTeamObject people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
