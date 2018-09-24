package com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.wepindia.pos.R;
import com.wepindia.pos.views.Masters.OutwardItemMaster.ItemDeletionBean;

import java.util.List;

public class ItemDeleteAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<ItemDeletionBean> objects;
    private CustomItemClickListener listener;

    private TextView tvSno, tvItemName, tvItemId, tvItemMrp;
    private CheckBox checkBox;

    public ItemDeleteAdapter(@NonNull Context context, int resource, @NonNull List<ItemDeletionBean> objects, CustomItemClickListener listener) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_supplier_deletion, null);

        if (objects.size() > 0) {
            ItemDeletionBean bean = objects.get(position);

            tvSno = (TextView) convertView.findViewById(R.id.tvSNo);
            tvItemId = (TextView) convertView.findViewById(R.id.tvSupplierId);
            tvItemName = (TextView) convertView.findViewById(R.id.tvSupplierName);
            tvItemMrp = (TextView) convertView.findViewById(R.id.tvSupplierPhone);
            checkBox = (CheckBox) convertView.findViewById(R.id.cbDelete);

            tvSno.setText(""+bean.getSno());
            tvItemId.setText(""+bean.getItemId());
            tvItemName.setText(bean.getItemName());
            tvItemMrp.setText(String.format("%.2f", bean.getItemMrp()));
            if (bean.getCheckBox() == 1) {
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    listener.onItemCheckedCallback(null, position, b);
                }
            });
        }

        return convertView;
    }

    public void setNotifyAdapter(List<ItemDeletionBean> objects){
        this.objects = objects;
        this.notifyDataSetChanged();
    }

}
