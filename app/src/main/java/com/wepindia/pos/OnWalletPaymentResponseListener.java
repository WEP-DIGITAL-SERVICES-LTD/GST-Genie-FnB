package com.wepindia.pos;

/**
 * Created by Administrator on 19-01-2018.
 */

public interface OnWalletPaymentResponseListener {
    void onWalletPaymentSuccessListener(String transaction_id);
    void onWalletPaymentErrorListener(int code, String response);
}
