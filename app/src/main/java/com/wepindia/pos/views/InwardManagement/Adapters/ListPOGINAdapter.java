package com.wepindia.pos.views.InwardManagement.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wep.common.app.models.PurchaseOrderBean;
import com.wepindia.pos.R;

import java.util.List;

public class ListPOGINAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<PurchaseOrderBean> objects = null;
    private TextView tv_po_gin_date, tv_po_gin_po_no, tv_po_gin_invoice_no,
            tv_po_gin_invoice_date, tv_po_gin_supplier, tv_po_gin_supplier_phone,
            tv_po_gin_gstin, tv_po_gin_total, tv_po_gin_gin_status;

    public ListPOGINAdapter(@NonNull Context context, int resource, @NonNull List<PurchaseOrderBean> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(resource, null);

        if (objects.size() > 0) {
            PurchaseOrderBean bean = objects.get(position);

            tv_po_gin_date = (TextView) convertView.findViewById(R.id.tv_po_gin_date);
            tv_po_gin_po_no = (TextView) convertView.findViewById(R.id.tv_po_gin_po_no);
            tv_po_gin_invoice_no = (TextView) convertView.findViewById(R.id.tv_po_gin_invoice_no);
            tv_po_gin_invoice_date = (TextView) convertView.findViewById(R.id.tv_po_gin_invoice_date);
            tv_po_gin_supplier = (TextView) convertView.findViewById(R.id.tv_po_gin_supplier);
            tv_po_gin_supplier_phone = (TextView) convertView.findViewById(R.id.tv_po_gin_supplier_phone);
            tv_po_gin_gstin = (TextView) convertView.findViewById(R.id.tv_po_gin_gstin);
            tv_po_gin_total = (TextView) convertView.findViewById(R.id.tv_po_gin_total);
            tv_po_gin_gin_status = (TextView) convertView.findViewById(R.id.tv_po_gin_gin_status);

            tv_po_gin_date.setText(bean.getStrPurchaseOrderDate());
            if (bean.getiPurchaseOrderNo() == -1)
                tv_po_gin_po_no.setText("-");
            else
                tv_po_gin_po_no.setText(""+bean.getiPurchaseOrderNo());
            tv_po_gin_invoice_no.setText(bean.getStrInvoiceNo());
            tv_po_gin_invoice_date.setText(bean.getStrInvoiceDate());
            tv_po_gin_supplier.setText(bean.getStrSupplierName());
            tv_po_gin_supplier_phone.setText(bean.getStrSupplierPhone());
            tv_po_gin_gstin.setText(bean.getStrSupplierGSTIN());
            tv_po_gin_total.setText(String.format("%.2f", bean.getDblAmount() + bean.getDblAdditionalChargeAmount()));
            if (bean.getiIsgoodInward() == 0)
                tv_po_gin_gin_status.setText("Pending");
            else
                tv_po_gin_gin_status.setText("Inwarded");

        }

        return convertView;
    }

    public void setNotifyAdapter(List<PurchaseOrderBean> objects){
        this.objects = objects;
        this.notifyDataSetChanged();
    }
}
