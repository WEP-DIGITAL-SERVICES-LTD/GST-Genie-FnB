package com.wepindia.pos.views.Billing.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wep.common.app.models.BillItemBean;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Billing.Listeners.OnBillingSelectedProductListsListeners;

import java.util.List;

/**
 * Created by MohanN on 12/15/2017.
 */

public class BillingListAdapter extends RecyclerView.Adapter<BillingItemsListViewHolder> {

    private static final String TAG = BillingListAdapter.class.getName();
    private List<BillItemBean> billingItemsLists;
    private OnBillingSelectedProductListsListeners onBillingSelectedProductListsListeners;
    private int isHsnVisibleEnabled;
    private int viewBill;

    public BillingListAdapter(OnBillingSelectedProductListsListeners onBillingSelectedProductListsListeners,
                              List<BillItemBean> list, int isHsnVisibleEnabled, int viewBill) {
        this.onBillingSelectedProductListsListeners = onBillingSelectedProductListsListeners;
        this.billingItemsLists = list;
        this.isHsnVisibleEnabled = isHsnVisibleEnabled;
        this.viewBill = viewBill;
    }


    @Override
    public BillingItemsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_card_billing_items_list,viewGroup,false);
        return new BillingItemsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BillingItemsListViewHolder holder, final int position) {
        BillItemBean myObject = billingItemsLists.get(position);
        holder.bind(myObject);

        holder.llBillingItemsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBillingSelectedProductListsListeners.onBillingListItemSelected(position);
            }
        });

        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBillingSelectedProductListsListeners.onBillingListItemRemove(position);
            }
        });

        if(isHsnVisibleEnabled != 1)
            holder.tvHSN.setVisibility(View.INVISIBLE);

        if (viewBill == 1)
            holder.ivRemove.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return billingItemsLists.size();
    }

    public void notifyData(List<BillItemBean> list){
        this.billingItemsLists = list;
        this.notifyDataSetChanged();
    }
}
