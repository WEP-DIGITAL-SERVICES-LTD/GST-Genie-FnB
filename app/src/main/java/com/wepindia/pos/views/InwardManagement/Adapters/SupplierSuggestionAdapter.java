package com.wepindia.pos.views.InwardManagement.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.wepindia.pos.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SachinV on 29-08-2017.
 */

public class SupplierSuggestionAdapter extends ArrayAdapter<HashMap<String, String>> {

    private Context context;
    private int resources;
    private ArrayList<HashMap<String, String>> objects;
    private ArrayList<HashMap<String, String>> objectsAll;
    private ArrayList<HashMap<String, String>> suggestions;

    public SupplierSuggestionAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<HashMap<String, String>> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resources = resource;
        this.objects = objects;
        this.objectsAll = (ArrayList<HashMap<String, String>>) objects.clone();
        this.suggestions = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        try{
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) ((Activity) context).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(resources, parent, false);
            }
            HashMap<String, String> data = objects.get(position);
            TextView adapterName = (TextView) row.findViewById(R.id.adapterName);
            TextView adapterPhone = (TextView) row.findViewById(R.id.adapterPhone);
            adapterName.setText(data.get("name"));
            adapterPhone.setText(data.get("phone"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String name = ((HashMap<String, String>) resultValue).get("name");
            return name;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if(charSequence != null) {
                suggestions.clear();
                for (HashMap<String, String> data : objectsAll) {
                    if(data.get("name").toLowerCase().startsWith(charSequence.toString().toLowerCase())){ // Filter list on the basis of name
                        suggestions.add(data);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                FilterResults filter = new FilterResults();
                filter.values = objectsAll;
                filter.count = objectsAll.size();
                return filter;
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            ArrayList<HashMap<String, String>> filtered = (ArrayList<HashMap<String, String>>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (HashMap<String, String> c : filtered) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}
