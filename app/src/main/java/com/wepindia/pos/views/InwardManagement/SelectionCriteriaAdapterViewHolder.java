package com.wepindia.pos.views.InwardManagement;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wepindia.pos.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SachinV on 12-03-2018.
 */

public class SelectionCriteriaAdapterViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.checkBoxItem)
    CheckBox cbItem;
    @BindView(R.id.row_supplier_item_linkage_item_name)
    TextView tvItemName;
    @BindView(R.id.dash_1)
    TextView dash_1;
    @BindView(R.id.row_supplier_item_linkage_item_barcode)
    TextView tvBarcodeName;
    @BindView(R.id.dash_2)
    TextView dash_2;
    @BindView(R.id.row_supplier_item_linkage_item_mrp)
    TextView tvItemMrp;

    public SelectionCriteriaAdapterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(CriteriaBean criteriaBean) {
        if (criteriaBean != null) {
            cbItem.setChecked(criteriaBean.isSelected());
            tvItemName.setText(criteriaBean.getName());
            dash_1.setText("");
            tvBarcodeName.setText("");
            dash_2.setText("");
            tvItemMrp.setText("");
        }
    }
}
