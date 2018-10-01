package com.wepindia.pos.views.InwardManagement.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wep.common.app.models.ItemInward;
import com.wep.common.app.models.ItemModel;
import com.wepindia.pos.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SachinV on 12-03-2018.
 */

public class SupplierItemLinkageLinkingViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.checkBoxItem)
    CheckBox cbItem;
    @BindView(R.id.row_supplier_item_linkage_item_name)
    TextView tvItemName;
    @BindView(R.id.row_supplier_item_linkage_item_barcode)
    TextView tvBarcodeName;
    @BindView(R.id.row_supplier_item_linkage_item_mrp)
    TextView tvItemMrp;

    public SupplierItemLinkageLinkingViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(ItemInward itemModel, int position) {
        if (itemModel != null) {
            tvItemName.setText(itemModel.getItemShortName());
            tvBarcodeName.setText(itemModel.getItemBarcode());
            tvItemMrp.setText(""+itemModel.getPurchaseRate());

            if (itemModel.isSelected())
                cbItem.setChecked(true);
            else
                cbItem.setChecked(false);
        }
    }

}
