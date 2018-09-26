package com.wepindia.pos.views.Billing.Listeners;

public interface ViewBillPrintOption {

    void printViewedBillToken(String invoiceNo, String invoiceDate);
    void printViewedBill(String invoiceNo, String invoiceDate);

}
