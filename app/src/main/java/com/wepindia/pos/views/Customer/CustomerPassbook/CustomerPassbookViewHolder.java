package com.wepindia.pos.views.Customer.CustomerPassbook;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wep.common.app.models.CustomerPassbookBean;
import com.wepindia.pos.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MohanN on 3/2/2018.
 */

public class CustomerPassbookViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_customer_passbook_dialog_list_row_date)
    TextView tvDate;
    @BindView(R.id.tv_customer_passbook_dialog_list_row_description)
    TextView tvDescription;
    @BindView(R.id.tv_customer_passbook_dialog_list_row_debit_amount)
    TextView tvDebitAmount;
    @BindView(R.id.tv_customer_passbook_dialog_list_row_credit_amount)
    TextView tvCreditAmount;
    @BindView(R.id.tv_customer_passbook_dialog_list_row_total_amount)
    TextView tvTotalAmount;

    String strEmpty = "-";

    public CustomerPassbookViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(CustomerPassbookBean customerPassbookBean, int position) {
        if (customerPassbookBean != null) {
            tvDate.setText(customerPassbookBean.getStrDate());
            tvDescription.setText(customerPassbookBean.getStrDescription());
            if(customerPassbookBean.getDblDepositAmount() > 0) {
                tvDebitAmount.setText(String.format("%.2f", customerPassbookBean.getDblDepositAmount()));
            } else {
                tvDebitAmount.setText(strEmpty);
            }
            if(customerPassbookBean.getDblCreditAmount() > 0) {
                tvCreditAmount.setText(String.format("%.2f", customerPassbookBean.getDblCreditAmount()));
            } else {
                tvCreditAmount.setText(strEmpty);
            }
            tvTotalAmount.setText(String.format("%.2f",customerPassbookBean.getDblTotalAmount()));
        }
    }
}