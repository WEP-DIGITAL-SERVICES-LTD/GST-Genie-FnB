
package com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners;


import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wep.common.app.Database.DatabaseHandler;

import java.util.HashMap;


public class SimpleCursorRecyclerAdapter extends CursorRecyclerAdapter<SimpleCursorRecyclerAdapter.SimpleViewHolder> {

    private int mLayout;
    private int[] mFrom;
    private int[] mTo;
    private String[] mOriginalFrom;
    HashMap<Integer, String> deptCategBrandList = new HashMap<>();

    private OnItemClickListener listener;

    public SimpleCursorRecyclerAdapter(int layout, Cursor c, String[] from, int[] to, OnItemClickListener listener, HashMap<Integer, String> list) {
        super(c);
        mLayout = layout;
        mTo = to;
        mOriginalFrom = from;
        findColumns(c, from);
        this.listener = listener;
        this.deptCategBrandList = list;
        /*
        deptCategBrandList.put(1,"String1");
        deptCategBrandList.put(2,"String2");*/
    }

    @Override
    public SimpleViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        final SimpleViewHolder mViewHolder = new SimpleViewHolder(mView, mTo);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return  mViewHolder;
    }

    @Override
    public void onBindViewHolder (SimpleViewHolder holder, Cursor cursor) {
        final int count = mTo.length;
        final int[] from = mFrom;

        for (int i = 0; i < count; i++) {

            if(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_MinimumStock)) >= cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Quantity)))
                holder.views[i].setTextColor(Color.RED);
            else
                holder.views[i].setTextColor(Color.BLACK);

            if(mOriginalFrom[i].equalsIgnoreCase(DatabaseHandler.KEY_Quantity) || mOriginalFrom[i].equalsIgnoreCase(DatabaseHandler.KEY_DineInPrice1))
            {
                holder.views[i].setText(String.format("%.2f",cursor.getDouble(from[i])));
            }else{
                holder.views[i].setText(cursor.getString(from[i]));
            }

            if(mOriginalFrom[i].equalsIgnoreCase(DatabaseHandler.KEY_isActive)){
                if(cursor.getInt(from[i]) == 1){
                    holder.views[i].setText("Active");
                } else {
                    holder.views[i].setText("InActive");
                }
            }else if(mOriginalFrom[i].equalsIgnoreCase(DatabaseHandler.KEY_KitchenCode) ||
                    mOriginalFrom[i].equalsIgnoreCase(DatabaseHandler.KEY_DepartmentCode) ||
                    mOriginalFrom[i].equalsIgnoreCase(DatabaseHandler.KEY_CategoryCode)){
                if(cursor.getInt(from[i]) >0){
                    {
                        int code = cursor.getInt(from[i]);
                        holder.views[i].setText(deptCategBrandList.get(code).toString());
                    }
                } else {
                    holder.views[i].setText("-");
                }
            }

        }

    }

    /*@Override
    public void onBindViewHolderCursor(SimpleViewHolder holder, Cursor cursor) {
        final int count = mTo.length;
        final int[] from = mFrom;

        for (int i = 0; i < count; i++) {
            holder.views[i].setText(cursor.getString(from[i]));
        }
    }*/

    /**
     * Create a map from an array of strings to an array of column-id integers in cursor c.
     * If c is null, the array will be discarded.
     *
     * @param c the cursor to find the columns from
     * @param from the Strings naming the columns of interest
     */
    private void findColumns(Cursor c, String[] from) {
        if (c != null) {
            int i;
            int count = from.length;
            if (mFrom == null || mFrom.length != count) {
                mFrom = new int[count];
            }
            for (i = 0; i < count; i++) {
                mFrom[i] = c.getColumnIndexOrThrow(from[i]);
            }
        } else {
            mFrom = null;
        }
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        findColumns(c, mOriginalFrom);
        return super.swapCursor(c);
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder
    {
        public TextView[] views;

        public SimpleViewHolder (View itemView, int[] to)
        {
            super(itemView);
            views = new TextView[to.length];
            for(int i = 0 ; i < to.length ; i++) {
                views[i] = (TextView) itemView.findViewById(to[i]);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}


