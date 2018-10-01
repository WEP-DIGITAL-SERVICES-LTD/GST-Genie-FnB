package com.wep.common.app.models;

/**
 * Created by MohanN on 1/3/2018.
 */

public class SupplierItemLinkageBean {

    private int _id, iSupplierID, iItemID;
    private String strGSTIN, strSupplierName, strSupplierType, strSupplierPhone,
            strSupplierAddress, strItemName, strBarcode, strHSNCode, strUOM, strSupplyType;
    private double dblPurchaseRate, dblRate1, dblCGSTPer, dblUTGST_SGSTPer, dblIGSTPer, dblCessPer, dblCessAmount, dblAdditionalCessAmount;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getiSupplierID() {
        return iSupplierID;
    }

    public void setiSupplierID(int iSupplierID) {
        this.iSupplierID = iSupplierID;
    }

    public int getiItemID() {
        return iItemID;
    }

    public void setiItemID(int iItemID) {
        this.iItemID = iItemID;
    }

    public String getStrGSTIN() {
        return strGSTIN;
    }

    public void setStrGSTIN(String strGSTIN) {
        this.strGSTIN = strGSTIN;
    }

    public String getStrSupplierName() {
        return strSupplierName;
    }

    public void setStrSupplierName(String strSupplierName) {
        this.strSupplierName = strSupplierName;
    }

    public String getStrSupplierType() {
        return strSupplierType;
    }

    public void setStrSupplierType(String strSupplierType) {
        this.strSupplierType = strSupplierType;
    }

    public String getStrSupplierPhone() {
        return strSupplierPhone;
    }

    public void setStrSupplierPhone(String strSupplierPhone) {
        this.strSupplierPhone = strSupplierPhone;
    }

    public String getStrSupplierAddress() {
        return strSupplierAddress;
    }

    public void setStrSupplierAddress(String strSupplierAddress) {
        this.strSupplierAddress = strSupplierAddress;
    }

    public String getStrItemName() {
        return strItemName;
    }

    public void setStrItemName(String strItemName) {
        this.strItemName = strItemName;
    }

    public String getStrBarcode() {
        return strBarcode;
    }

    public void setStrBarcode(String strBarcode) {
        this.strBarcode = strBarcode;
    }

    public String getStrHSNCode() {
        return strHSNCode;
    }

    public void setStrHSNCode(String strHSNCode) {
        this.strHSNCode = strHSNCode;
    }

    public String getStrUOM() {
        return strUOM;
    }

    public void setStrUOM(String strUOM) {
        this.strUOM = strUOM;
    }

    public double getDblPurchaseRate() {
        return dblPurchaseRate;
    }

    public void setDblPurchaseRate(double dblPurchaseRate) {
        this.dblPurchaseRate = dblPurchaseRate;
    }

    public double getDblRate1() {
        return dblRate1;
    }

    public void setDblRate1(double dblRate1) {
        this.dblRate1 = dblRate1;
    }

    public double getDblCGSTPer() {
        return dblCGSTPer;
    }

    public void setDblCGSTPer(double dblCGSTPer) {
        this.dblCGSTPer = dblCGSTPer;
    }

    public double getDblUTGST_SGSTPer() {
        return dblUTGST_SGSTPer;
    }

    public void setDblUTGST_SGSTPer(double dblUTGST_SGSTPer) {
        this.dblUTGST_SGSTPer = dblUTGST_SGSTPer;
    }

    public double getDblIGSTPer() {
        return dblIGSTPer;
    }

    public void setDblIGSTPer(double dblIGSTPer) {
        this.dblIGSTPer = dblIGSTPer;
    }

    public double getDblCessPer() {
        return dblCessPer;
    }

    public void setDblCessPer(double dblCessPer) {
        this.dblCessPer = dblCessPer;
    }

    public String getStrSupplyType() {
        return strSupplyType;
    }

    public void setStrSupplyType(String strSupplyType) {
        this.strSupplyType = strSupplyType;
    }

    public double getDblCessAmount() {
        return dblCessAmount;
    }

    public void setDblCessAmount(double dblCessAmount) {
        this.dblCessAmount = dblCessAmount;
    }

    public double getDblAdditionalCessAmount() {
        return dblAdditionalCessAmount;
    }

    public void setDblAdditionalCessAmount(double dblAdditionalCessAmount) {
        this.dblAdditionalCessAmount = dblAdditionalCessAmount;
    }
}
