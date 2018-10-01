package com.wepindia.pos.views.InwardManagement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.wepindia.pos.R;
import com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners.CustomItemClickListener;

import java.util.ArrayList;

/**
 * Created by SachinV on 12-03-2018.
 */

public class SelectionCriteriaAdapter extends RecyclerView.Adapter<SelectionCriteriaAdapterViewHolder> {

    private Context mContext;
    private ArrayList<CriteriaBean> data;
    private CustomItemClickListener listener;

    public SelectionCriteriaAdapter(Context mContext, ArrayList<CriteriaBean> data, CustomItemClickListener listener) {
        this.data = data;
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    public SelectionCriteriaAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_supplier_item_linkage_items, parent,false);
        final SelectionCriteriaAdapterViewHolder viewHolder = new SelectionCriteriaAdapterViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, viewHolder.getPosition(), viewHolder.cbItem.isChecked());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SelectionCriteriaAdapterViewHolder holder, final int position) {
        CriteriaBean myObject = data.get(position);
        holder.bind(myObject);
        holder.cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.onItemCheckedCallback(null, position, b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
