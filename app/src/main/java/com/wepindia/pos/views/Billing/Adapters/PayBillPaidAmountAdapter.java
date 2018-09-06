package com.wepindia.pos.views.Billing.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wep.common.app.models.PayBillPaidAmountBean;
import com.wepindia.pos.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 16-01-2018.
 */

public class PayBillPaidAmountAdapter extends BaseAdapter {

    Activity activity;
    private ArrayList<PayBillPaidAmountBean> payList;
    private onDeleteClickListener deleteClickListener;

    public PayBillPaidAmountAdapter(Activity activity, ArrayList<PayBillPaidAmountBean> payList, onDeleteClickListener deleteClickListener) {
        this.activity = activity;
        this.payList = payList;
        this.deleteClickListener = deleteClickListener;
    }

    public void notifyNewDataAdded(ArrayList<PayBillPaidAmountBean> list) {
        this.payList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return payList.size();
    }

    @Override
    public Object getItem(int i) {
        return payList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        PayBillPaidAmountAdapter.ViewHolder viewHolder = null;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_paid_amount,null);
        viewHolder = new PayBillPaidAmountAdapter.ViewHolder();
        viewHolder.paidModeName = (TextView) convertView.findViewById(R.id.tv_paid_mode_name);
        viewHolder.paidModeAmount = (TextView) convertView.findViewById(R.id.tv_paid_mode_value);
        viewHolder.imgDel = (ImageView) convertView.findViewById(R.id.img_paid_mode_del);
        viewHolder.imgDel.setTag(position);
        convertView.setTag(viewHolder);

        PayBillPaidAmountBean paidMode = payList.get(position);
        viewHolder.paidModeName.setText(paidMode.getModeName());
        viewHolder.paidModeAmount.setText(String.format("%.2f",paidMode.getAmount()));
        viewHolder.imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteClickListener.onDeleteClicked(position);
            }
        });

        return convertView;
    }


    static class ViewHolder {
        TextView paidModeName;
        TextView paidModeAmount;
        ImageView imgDel;
    }

    public interface onDeleteClickListener
    {
        public void onDeleteClicked(int position);
    }
}
