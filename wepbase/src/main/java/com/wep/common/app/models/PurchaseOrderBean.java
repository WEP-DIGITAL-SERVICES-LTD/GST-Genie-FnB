package com.wep.common.app.models;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by MohanN on 1/5/2018.
 */

public class PurchaseOrderBean implements Serializable,Comparable<PurchaseOrderBean> {

    private int _id, iItemId,iIsgoodInward, iPurchaseOrderNo,iSupplierID,iPOStatus;
    private String strInvoiceNo, strInvoiceDate, strSupplierId, strSupplierName, strPurchaseOrderDate,
            strSupplierPhone, strSupplierAddress, strSupplierGSTIN, strSupplierType, strSupplyType, strSupplierEmail,
            strBarcode, strHSNCode, strItemName, strUOM, strAdditionalCharge, strSupplierPOS;
    private double dblPurchaseRate, dblRate, dblValue, dblQuantity, dblTaxableValue, dblIGSTRate, dblIGSTAmount, dblCGSTRate,
            dblCGSTAmount, dblSGSTRate, dblSGSTAmount, dblCessRate, dblCessAmount, dblCessAmountPerUnit, dblAdditionalCessAmount, dblSalesTaxAmount,
            dblServiceTaxAmount, dblAmount, dblAdditionalChargeAmount;
    private boolean bSupplierStateCodeStatus, bPOItemCbStatus;

    public PurchaseOrderBean() {
    }

    protected PurchaseOrderBean(Parcel in) {
        _id = in.readInt();
        iItemId = in.readInt();
        iIsgoodInward = in.readInt();
        iPurchaseOrderNo = in.readInt();
        strInvoiceNo = in.readString();
        strInvoiceDate = in.readString();
        strSupplierId = in.readString();
        strSupplierName = in.readString();
        strPurchaseOrderDate = in.readString();
        strSupplierPhone = in.readString();
        strSupplierAddress = in.readString();
        strSupplierGSTIN = in.readString();
        strSupplierType = in.readString();
        strSupplyType = in.readString();
        strSupplierEmail = in.readString();
        strBarcode = in.readString();
        strHSNCode = in.readString();
        strItemName = in.readString();
        strUOM = in.readString();
        strAdditionalCharge = in.readString();
        strSupplierPOS = in.readString();
        dblPurchaseRate = in.readDouble();
        dblRate = in.readDouble();
        dblValue = in.readDouble();
        dblQuantity = in.readDouble();
        dblTaxableValue = in.readDouble();
        dblIGSTRate = in.readDouble();
        dblIGSTAmount = in.readDouble();
        dblCGSTRate = in.readDouble();
        dblCGSTAmount = in.readDouble();
        dblSGSTRate = in.readDouble();
        dblSGSTAmount = in.readDouble();
        dblCessRate = in.readDouble();
        dblCessAmount = in.readDouble();
        dblCessAmountPerUnit = in.readDouble();
        dblAdditionalCessAmount = in.readDouble();
        dblSalesTaxAmount = in.readDouble();
        dblServiceTaxAmount = in.readDouble();
        dblAmount = in.readDouble();
        dblAdditionalChargeAmount = in.readDouble();
        bSupplierStateCodeStatus = in.readByte() != 0;
        iSupplierID = in.readInt();
        iPOStatus = in.readInt();
        bPOItemCbStatus = in.readByte() != 0;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getiItemId() {
        return iItemId;
    }

    public void setiItemId(int iItemId) {
        this.iItemId = iItemId;
    }

    public int getiIsgoodInward() {
        return iIsgoodInward;
    }

    public void setiIsgoodInward(int iIsgoodInward) {
        this.iIsgoodInward = iIsgoodInward;
    }

    public int getiPurchaseOrderNo() {
        return iPurchaseOrderNo;
    }

    public void setiPurchaseOrderNo(int iPurchaseOrderNo) {
        this.iPurchaseOrderNo = iPurchaseOrderNo;
    }

    public String getStrInvoiceNo() {
        return strInvoiceNo;
    }

    public void setStrInvoiceNo(String strInvoiceNo) {
        this.strInvoiceNo = strInvoiceNo;
    }

    public String getStrInvoiceDate() {
        return strInvoiceDate;
    }

    public void setStrInvoiceDate(String strInvoiceDate) {
        this.strInvoiceDate = strInvoiceDate;
    }

    public String getStrSupplierId() {
        return strSupplierId;
    }

    public void setStrSupplierId(String strSupplierId) {
        this.strSupplierId = strSupplierId;
    }

    public String getStrSupplierName() {
        return strSupplierName;
    }

    public void setStrSupplierName(String strSupplierName) {
        this.strSupplierName = strSupplierName;
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

    public String getStrSupplierGSTIN() {
        return strSupplierGSTIN;
    }

    public void setStrSupplierGSTIN(String strSupplierGSTIN) {
        this.strSupplierGSTIN = strSupplierGSTIN;
    }

    public String getStrSupplierType() {
        return strSupplierType;
    }

    public void setStrSupplierType(String strSupplierType) {
        this.strSupplierType = strSupplierType;
    }

    public String getStrSupplyType() {
        return strSupplyType;
    }

    public void setStrSupplyType(String strSupplyType) {
        this.strSupplyType = strSupplyType;
    }

    public String getStrHSNCode() {
        return strHSNCode;
    }

    public void setStrHSNCode(String strHSNCode) {
        this.strHSNCode = strHSNCode;
    }

    public String getStrItemName() {
        return strItemName;
    }

    public void setStrItemName(String strItemName) {
        this.strItemName = strItemName;
    }

    public String getStrUOM() {
        return strUOM;
    }

    public void setStrUOM(String strUOM) {
        this.strUOM = strUOM;
    }

    public String getStrAdditionalCharge() {
        return strAdditionalCharge;
    }

    public void setStrAdditionalCharge(String strAdditionalCharge) {
        this.strAdditionalCharge = strAdditionalCharge;
    }

    public String getStrSupplierPOS() {
        return strSupplierPOS;
    }

    public void setStrSupplierPOS(String strSupplierPOS) {
        this.strSupplierPOS = strSupplierPOS;
    }

    public double getDblValue() {
        return dblValue;
    }

    public void setDblValue(double dblValue) {
        this.dblValue = dblValue;
    }

    public double getDblQuantity() {
        return dblQuantity;
    }

    public void setDblQuantity(double dblQuantity) {
        this.dblQuantity = dblQuantity;
    }

    public double getDblTaxableValue() {
        return dblTaxableValue;
    }

    public void setDblTaxableValue(double dblTaxableValue) {
        this.dblTaxableValue = dblTaxableValue;
    }

    public double getDblIGSTRate() {
        return dblIGSTRate;
    }

    public void setDblIGSTRate(double dblIGSTRate) {
        this.dblIGSTRate = dblIGSTRate;
    }

    public double getDblIGSTAmount() {
        return dblIGSTAmount;
    }

    public void setDblIGSTAmount(double dblIGSTAmount) {
        this.dblIGSTAmount = dblIGSTAmount;
    }

    public double getDblCGSTRate() {
        return dblCGSTRate;
    }

    public void setDblCGSTRate(double dblCGSTRate) {
        this.dblCGSTRate = dblCGSTRate;
    }

    public double getDblCGSTAmount() {
        return dblCGSTAmount;
    }

    public void setDblCGSTAmount(double dblCGSTAmount) {
        this.dblCGSTAmount = dblCGSTAmount;
    }

    public double getDblSGSTRate() {
        return dblSGSTRate;
    }

    public void setDblSGSTRate(double dblSGSTRate) {
        this.dblSGSTRate = dblSGSTRate;
    }

    public double getDblSGSTAmount() {
        return dblSGSTAmount;
    }

    public void setDblSGSTAmount(double dblSGSTAmount) {
        this.dblSGSTAmount = dblSGSTAmount;
    }

    public double getDblCessRate() {
        return dblCessRate;
    }

    public void setDblCessRate(double dblCessRate) {
        this.dblCessRate = dblCessRate;
    }

    public double getDblCessAmount() {
        return dblCessAmount;
    }

    public void setDblCessAmount(double dblCessAmount) {
        this.dblCessAmount = dblCessAmount;
    }

    public double getDblSalesTaxAmount() {
        return dblSalesTaxAmount;
    }

    public void setDblSalesTaxAmount(double dblSalesTaxAmount) {
        this.dblSalesTaxAmount = dblSalesTaxAmount;
    }

    public double getDblServiceTaxAmount() {
        return dblServiceTaxAmount;
    }

    public void setDblServiceTaxAmount(double dblServiceTaxAmount) {
        this.dblServiceTaxAmount = dblServiceTaxAmount;
    }

    public double getDblAmount() {
        return dblAmount;
    }

    public void setDblAmount(double dblAmount) {
        this.dblAmount = dblAmount;
    }

    public double getDblAdditionalChargeAmount() {
        return dblAdditionalChargeAmount;
    }

    public void setDblAdditionalChargeAmount(double dblAdditionalChargeAmount) {
        this.dblAdditionalChargeAmount = dblAdditionalChargeAmount;
    }

    public String getStrBarcode() {
        return strBarcode;
    }

    public void setStrBarcode(String strBarcode) {
        this.strBarcode = strBarcode;
    }

    public double getDblRate() {
        return dblRate;
    }

    public void setDblRate(double dblRate) {
        this.dblRate = dblRate;
    }

    public boolean isbSupplierStateCodeStatus() {
        return bSupplierStateCodeStatus;
    }

    public void setbSupplierStateCodeStatus(boolean bSupplierStateCodeStatus) {
        this.bSupplierStateCodeStatus = bSupplierStateCodeStatus;
    }

    public double getDblPurchaseRate() {
        return dblPurchaseRate;
    }

    public void setDblPurchaseRate(double dblPurchaseRate) {
        this.dblPurchaseRate = dblPurchaseRate;
    }

    public double getDblCessAmountPerUnit() {
        return dblCessAmountPerUnit;
    }

    public void setDblCessAmountPerUnit(double dblCessAmountPerUnit) {
        this.dblCessAmountPerUnit = dblCessAmountPerUnit;
    }

    public double getDblAdditionalCessAmount() {
        return dblAdditionalCessAmount;
    }

    public void setDblAdditionalCessAmount(double dblAdditionalCessAmount) {
        this.dblAdditionalCessAmount = dblAdditionalCessAmount;
    }

    public String getStrPurchaseOrderDate() {
        return strPurchaseOrderDate;
    }

    public void setStrPurchaseOrderDate(String strPurchaseOrderDate) {
        this.strPurchaseOrderDate = strPurchaseOrderDate;
    }

    public String getStrSupplierEmail() {
        return strSupplierEmail;
    }

    public void setStrSupplierEmail(String strSupplierEmail) {
        this.strSupplierEmail = strSupplierEmail;
    }

    public int getiSupplierID() {
        return iSupplierID;
    }

    public void setiSupplierID(int iSupplierID) {
        this.iSupplierID = iSupplierID;
    }

    public int getiPOStatus() {
        return iPOStatus;
    }

    public void setiPOStatus(int iPOStatus) {
        this.iPOStatus = iPOStatus;
    }

    public boolean isbPOItemCbStatus() {
        return bPOItemCbStatus;
    }

    public void setbPOItemCbStatus(boolean bPOItemCbStatus) {
        this.bPOItemCbStatus = bPOItemCbStatus;
    }

    public int compareTo(PurchaseOrderBean comparePurchaseOrderBean) {

        int compareSupplierId = ((PurchaseOrderBean) comparePurchaseOrderBean).getiSupplierID();

        //ascending order
        return this.iSupplierID - compareSupplierId;
    }
}
