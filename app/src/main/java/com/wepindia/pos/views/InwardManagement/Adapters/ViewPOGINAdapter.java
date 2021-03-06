package com.wepindia.pos.views.InwardManagement.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wep.common.app.models.PurchaseOrderBean;
import com.wepindia.pos.R;

import java.util.List;

public class ViewPOGINAdapter extends BaseAdapter {

    private Activity activity;
    private List<PurchaseOrderBean> purchaseOrderList;

    public ViewPOGINAdapter(Activity activity, List<PurchaseOrderBean> itemsList) {
        this.activity = activity;
        this.purchaseOrderList = itemsList;
    }
    @Override
    public int getCount() {
        return purchaseOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return purchaseOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView tv_Sn;
        TextView tv_g_s;
        TextView tv_hsnCode;
        TextView tv_itemName;
        TextView tv_rate;
        TextView tv_qty;
        TextView tv_UOM;
        TextView tv_taxVal;
        TextView tv_igstAmt;
        TextView tv_cgstAmt;
        TextView tv_sgstAmt;
        TextView tv_cessAmt;
        TextView tv_amt;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PurchaseOrderAdapter.ViewHolder viewHolder;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_view_po_gin, null);
            viewHolder = new PurchaseOrderAdapter.ViewHolder();
            viewHolder.tv_Sn = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_Sn);
            viewHolder.tv_hsnCode = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_hsnCode);
            viewHolder.tv_itemName = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_itemName);
            viewHolder.tv_rate = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_rate);
            viewHolder.tv_qty = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_qty);
            viewHolder.tv_UOM = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_UOM);
            viewHolder.tv_taxVal = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_taxVal);
            viewHolder.tv_igstAmt = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_igstAmt);
            viewHolder.tv_cgstAmt = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_cgstAmt);
            viewHolder.tv_sgstAmt = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_sgstAmt);
            viewHolder.tv_cessAmt = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_cessAmt);
            viewHolder.tv_amt = (TextView) convertView.findViewById(R.id.tv_ViewPOGIN_row_list_amt);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (PurchaseOrderAdapter.ViewHolder) convertView.getTag();
        }
        PurchaseOrderBean po = purchaseOrderList.get(position);
        viewHolder.tv_Sn.setText(String.valueOf(position+1));
        viewHolder.tv_hsnCode.setText(po.getStrHSNCode());
        viewHolder.tv_itemName.setText(po.getStrItemName());
        viewHolder.tv_rate.setText(String.format("%.2f",po.getDblPurchaseRate()));
        viewHolder.tv_qty.setText(String.format("%.2f",po.getDblQuantity()));
        viewHolder.tv_UOM.setText(po.getStrUOM());
        viewHolder.tv_taxVal.setText(String.format("%.2f",po.getDblTaxableValue()));
        viewHolder.tv_igstAmt.setText(String.format("%.2f",po.getDblIGSTAmount()));
        viewHolder.tv_cgstAmt.setText(String.format("%.2f",po.getDblCGSTAmount()));
        viewHolder.tv_sgstAmt.setText(String.format("%.2f",po.getDblSGSTAmount()));
        viewHolder.tv_cessAmt.setText(String.format("%.2f",po.getDblCessAmount()));
        viewHolder.tv_amt.setText(String.format("%.2f",po.getDblAmount()));

        return convertView;
    }

    public void notifyDataSetChanged(List<PurchaseOrderBean> list) {
        this.purchaseOrderList = list;
        notifyDataSetChanged();
    }

}
