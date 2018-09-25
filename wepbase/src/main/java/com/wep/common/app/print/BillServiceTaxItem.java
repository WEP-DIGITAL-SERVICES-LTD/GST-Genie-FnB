package com.wep.common.app.print;

import java.io.Serializable;

/**
 * Created by PriyabratP on 06-09-2016.
 */
public class BillServiceTaxItem implements Serializable {

    private String txName;
    private double percent;
    private double price;
    private double pricePerUnit;

    public BillServiceTaxItem() {
    }

    public BillServiceTaxItem(String txName, double percent, double price, double pricePerUnit) {
        this.txName = txName;
        this.percent = percent;
        this.price = price;
        this.pricePerUnit = pricePerUnit;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getTxName() {
        return txName;
    }

    public void setTxName(String txName) {
        this.txName = txName;
    }

    public String getServiceTxName() {
        return txName;
    }

    public void setServiceTxName(String txName) {
        this.txName = txName;
    }

    public double getServicePercent() {
        return percent;
    }

    public void setServicePercent(double Percent) {
        this.percent = Percent;
    }

    public double getServicePrice() {
        return price;
    }

    public void setServicePrice(double price) {
        this.price = price;
    }
}
