package com.wepindia.pos.views.Billing.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wep.common.app.models.PayBillDialogBean;
import com.wepindia.pos.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MohanN on 12/18/2017.
 */

public class PayBillViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.tv_proceed_to_pay_dialog)     TextView tvData;
    @BindView(R.id.iv_proceed_to_pay_dialog)     ImageView imageView;
    @BindView(R.id.ll_proceed_to_pay_dialog)     LinearLayout linearLayout;
    PayBillDialogBean item;
    protected ProceedToPayItemListener mListener;

    private Context mContext;

    public PayBillViewHolder(View v, ProceedToPayItemListener mListener, Context context) {
        super(v);
        this.mListener = mListener;
        this.mContext = context;
        ButterKnife.bind(this,v);
    }

    @OnClick({R.id.ll_proceed_to_pay_dialog})
    public void mOnClickEvent(View view){
        switch (view.getId()){
            case R.id.ll_proceed_to_pay_dialog:
                if (mListener != null) {
                    mListener.onItemClick(item);
                }
                break;
            default:
                break;
        }
    }

    public void setData(PayBillDialogBean item) {
        this.item = item;
        tvData.setText(item.strData);
        tvData.setCompoundDrawablesWithIntrinsicBounds(null,mContext.getResources().getDrawable(item.iDrawable),null,null);
        //imageView.setImageDrawable(mContext.getResources().getDrawable(item.iDrawable));
        //relativeLayout.setBackgroundColor(Color.parseColor(item.color));
    }

    public interface ProceedToPayItemListener {
        void onItemClick(PayBillDialogBean item);
    }
}
