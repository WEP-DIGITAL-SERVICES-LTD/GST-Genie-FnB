package com.wep.common.app.models;

/**
 * Created by Administrator on 16-01-2018.
 */

public class PayBillPaidAmountBean {

    String modeName;
    double Amount;

    public PayBillPaidAmountBean(String modeName, double amount) {
        this.modeName = modeName;
        Amount = amount;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }
}
