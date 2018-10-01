package com.wepindia.pos.views.InwardManagement.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.wep.common.app.models.ItemInward;
import com.wep.common.app.models.ItemModel;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners.CustomItemClickListener;

import java.util.ArrayList;

/**
 * Created by SachinV on 12-03-2018.
 */

public class SupplierItemLinkageLinkingAdapter extends RecyclerView.Adapter<SupplierItemLinkageLinkingViewHolder>  {

    private Context mContext;
    private ArrayList<ItemInward> data;
    private CustomItemClickListener listener;

    public SupplierItemLinkageLinkingAdapter(Context mContext, ArrayList<ItemInward> data, CustomItemClickListener listener) {
        this.data = data;
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    public SupplierItemLinkageLinkingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_supplier_item_linkage_items, parent,false);
        final SupplierItemLinkageLinkingViewHolder viewHolder = new SupplierItemLinkageLinkingViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, viewHolder.getPosition(), viewHolder.cbItem.isChecked());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SupplierItemLinkageLinkingViewHolder holder, final int position) {
        ItemInward myObject = data.get(position);
        holder.bind(myObject,position);
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
