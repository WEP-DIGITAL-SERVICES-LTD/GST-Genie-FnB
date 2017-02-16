package com.wepindia.pos.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wep.common.app.Database.Department;
import com.wep.common.app.models.Items;
import com.wepindia.pos.R;

import java.util.ArrayList;

/**
 * Created by PriyabratP on 14-02-2017.
 */

public class DepartmentAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Department> itemsList;

    public DepartmentAdapter(Activity activity, ArrayList<Department> itemsList){
        this.activity = activity;
        this.itemsList = itemsList;
    }
    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_cat, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textViewTitle);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Department items = itemsList.get(position);
        String title = items.getDeptName();
        viewHolder.textView.setText(title+"");
        return convertView;
    }

    public void notifyDataSetChanged(ArrayList<Department> list) {
        this.itemsList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView textView;
    }
}
