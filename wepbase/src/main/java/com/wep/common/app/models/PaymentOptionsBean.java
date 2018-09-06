package com.wep.common.app.models;

/**
 * Created by MohanN on 2/15/2018.
 */

public class PaymentOptionsBean {
    private String strName;
    private int iColor;
    private int iDrawable;
    private boolean isActive;

    public PaymentOptionsBean(String strName, int iColor, int iDrawable, boolean isActive){
        this.strName = strName;
        this.iColor = iColor;
        this.iDrawable = iDrawable;
        this.isActive = isActive;
    }

    public PaymentOptionsBean(){}

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public int getiColor() {
        return iColor;
    }

    public void setiColor(int iColor) {
        this.iColor = iColor;
    }

    public int getiDrawable() {
        return iDrawable;
    }

    public void setiDrawable(int iDrawable) {
        this.iDrawable = iDrawable;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
