package com.wepindia.pos.views.Billing.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wep.common.app.models.BillItemBean;
import com.wepindia.pos.GenericClasses.DecimalDigitsInputFilter;
import com.wepindia.pos.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MohanN on 12/15/2017.
 */

public class BillingItemsListViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = BillingItemsListViewHolder.class.getName();

    @BindView(R.id.tv_cell_card_billing_items_list_name)
    TextView tvItemName;
    @BindView(R.id.tv_cell_card_billing_items_list_hsn)
    TextView tvHSN;
    @BindView(R.id.tv_cell_card_billing_items_list_rate)
    TextView tvRate;
    @BindView(R.id.tv_cell_card_billing_items_list_qty)
    TextView tvQty;
    @BindView(R.id.tv_cell_card_billing_items_list_disc)
    TextView tvDiscount;
    @BindView(R.id.tv_cell_card_billing_items_list_taxable_value)
//    TextView tvTaxableValue;
    TextView tvAmount;
    @BindView(R.id.iv_cell_card_billing_items_list)
    ImageView ivRemove;
    @BindView(R.id.ll_cell_card_billing_items_list)
    LinearLayout llBillingItemsList;

    public BillingItemsListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
//        tvTaxableValue.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});
        tvAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});
        tvDiscount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});
        tvRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,3)});
    }

    public void bind(BillItemBean billItemBean){
        tvItemName.setText(billItemBean.getStrItemName());
        tvHSN.setText(billItemBean.getStrHSNCode());
        tvRate.setText(String.format("%.2f", billItemBean.getDblValue()));
        tvQty.setText(String.format("%.3f", billItemBean.getDblQty()));
        if (billItemBean.getDblDiscountAmount() > 0)
            tvDiscount.setText(String.format("%.2f", billItemBean.getDblDiscountAmount()));
        else
            tvDiscount.setText(String.format("%.2f", 0.00));
//        tvTaxableValue.setText(String.format("%.2f", billItemBean.getDblTaxbleValue()));
        tvAmount.setText(String.format("%.2f", billItemBean.getDblAmount()));
    }
}