package com.wepindia.pos.views.InwardManagement.Listeners;

import com.wep.common.app.models.PurchaseOrderBean;

public interface OnViewPOGINListener {
    void onPurchaseOrderShareClicked(PurchaseOrderBean purchaseOrderBean);
    void onPurchaseOrderPrintClicked(PurchaseOrderBean purchaseOrderBean);
}
