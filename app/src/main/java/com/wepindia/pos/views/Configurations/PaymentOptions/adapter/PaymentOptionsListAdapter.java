package com.wepindia.pos.views.Configurations.PaymentOptions.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.models.PaymentOptionsBean;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Configurations.PaymentOptions.listeners.OnPaymentOptionListener;

import java.util.List;

/**
 * Created by MohanN on 2/15/2018.
 */

public class PaymentOptionsListAdapter extends RecyclerView.Adapter<PaymentOptionsListViewHolder> {
    private static final String TAG = PaymentOptionsListAdapter.class.getName();
    private Context mContext;
    private List<PaymentOptionsBean> list;
    private OnPaymentOptionListener paymentOptionListener;

    public PaymentOptionsListAdapter(Context context, OnPaymentOptionListener paymentOptionListener, List<PaymentOptionsBean> list) {
        this.mContext = context;
        this.paymentOptionListener = paymentOptionListener;
        this.list = list;
    }

    @Override
    public PaymentOptionsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.payment_mode_option_row_list,viewGroup,false);
        return new PaymentOptionsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaymentOptionsListViewHolder holder, final int position) {
        PaymentOptionsBean myObject = list.get(position);
        holder.bind(mContext,myObject);

        holder.llPaymentOptionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.get(position).getStrName().toUpperCase().trim().equals(DatabaseHandler.CASH_PAYMENT_OPTION_CONFIG.toUpperCase().trim())){
                    return;
                }
                if(list.get(position).isActive()){
                    paymentOptionListener.onPaymentOptionDisable(position);
                } else {
                    paymentOptionListener.onPaymentOptionEnable(position);
                }
            }
        });

       /* holder.cbIsActive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                list.get(position).setActive(cb.isChecked());
                if(list.get(position).getStrName().toUpperCase().trim().equals(DatabaseHandler.CASH_PAYMENT_OPTION_CONFIG.toUpperCase().trim())){
                    list.get(position).setActive(true);
                }
                cb.setChecked(list.get(position).isActive());
                if(list.get(position).isActive()){
                    paymentOptionListener.onPaymentOptionEnable(position);
                } else {
                    paymentOptionListener.onPaymentOptionDisable(position);
                }
            }
        });*/
    }

    public void notify(List<PaymentOptionsBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}