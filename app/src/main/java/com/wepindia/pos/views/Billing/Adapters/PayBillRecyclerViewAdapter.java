package com.wepindia.pos.views.Billing.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wep.common.app.models.PayBillDialogBean;
import com.wepindia.pos.R;

import java.util.List;

/**
 * Created by MohanN on 12/18/2017.
 */

public class PayBillRecyclerViewAdapter extends RecyclerView.Adapter<PayBillViewHolder> {
    private static final String TAG = PayBillRecyclerViewAdapter.class.getName();
    List<PayBillDialogBean> mValues;
    Context mContext;
    PayBillViewHolder.ProceedToPayItemListener mListener;

    public PayBillRecyclerViewAdapter(Context context, List<PayBillDialogBean> values, PayBillViewHolder.ProceedToPayItemListener mListener) {
        this.mValues = values;
        this.mContext = context;
        this.mListener= mListener;
    }

    @Override
    public PayBillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.proceed_to_pay_dialog_rv_item, parent, false);
        return new PayBillViewHolder(view,mListener,mContext);
    }

    @Override
    public void onBindViewHolder(PayBillViewHolder holder, int position) {
        PayBillDialogBean proceedToPayDialogBean = mValues.get(position);
        holder.setData(proceedToPayDialogBean);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
