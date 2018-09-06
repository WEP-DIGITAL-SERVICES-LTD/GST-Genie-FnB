package com.wepindia.pos.views.Billing.Listeners;

import com.wep.common.app.models.PaymentDetails;

/**
 * Created by Administrator on 22-01-2018.
 */

public interface OnProceedToPayCompleteListener {
    void onProceedToPayComplete(PaymentDetails obj);
    void onDismiss();
}
