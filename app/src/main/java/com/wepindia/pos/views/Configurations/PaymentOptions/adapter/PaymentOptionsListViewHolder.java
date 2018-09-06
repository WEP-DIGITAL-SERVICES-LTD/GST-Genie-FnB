package com.wepindia.pos.views.Configurations.PaymentOptions.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wep.common.app.models.PaymentOptionsBean;
import com.wepindia.pos.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MohanN on 2/15/2018.
 */

public class PaymentOptionsListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_payment_mode_option_row_list_name)
    TextView tvName;
    @BindView(R.id.ll_payment_mode_option_row_list)
    LinearLayout llPaymentOptionList;

    public PaymentOptionsListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Context mContext, PaymentOptionsBean optionsBean) {
        tvName.setText(optionsBean.getStrName());
        tvName.setCompoundDrawablesWithIntrinsicBounds(null,mContext.getResources().getDrawable(optionsBean.getiDrawable()),null,null);
        llPaymentOptionList.setBackgroundResource(optionsBean.getiColor());
    }
}