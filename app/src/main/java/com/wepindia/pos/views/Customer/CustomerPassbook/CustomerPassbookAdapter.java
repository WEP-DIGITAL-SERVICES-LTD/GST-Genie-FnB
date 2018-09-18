package com.wepindia.pos.views.Customer.CustomerPassbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wep.common.app.models.CustomerPassbookBean;
import com.wepindia.pos.R;

import java.util.List;

/**
 * Created by MohanN on 3/2/2018.
 */

public class CustomerPassbookAdapter extends RecyclerView.Adapter<CustomerPassbookViewHolder> {

    private static final String TAG = CustomerPassbookAdapter.class.getName();
    private List<CustomerPassbookBean> customerPassbookBeanList;
    private Context mContext;

    public CustomerPassbookAdapter(Context context, List<CustomerPassbookBean> list) {
        this.mContext = context;
        this.customerPassbookBeanList = list;
    }


    @Override
    public CustomerPassbookViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.customer_passbook_dialog_list_row,viewGroup,false);
        return new CustomerPassbookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerPassbookViewHolder holder, final int position) {
        CustomerPassbookBean myObject = customerPassbookBeanList.get(position);
        holder.bind(myObject,position);
    }

    @Override
    public int getItemCount() {
        return customerPassbookBeanList.size();
    }

    public void setNotifyData(List<CustomerPassbookBean> list){
        this.customerPassbookBeanList = list;
        notifyDataSetChanged();
    }
}