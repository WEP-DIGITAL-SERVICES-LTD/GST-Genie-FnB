package com.wepindia.pos.views.InwardManagement.PdfPurchaseOrder;

public class PdfPOItemBean {


    private int _id, iItemId, iPurchaseOrderNo;
    private String strBarcode, strHSNCode, strItemName, strUOM;
    private double dblPurchaseRate, dblMRP, dblRetailPrice, dblWholeSalePrice, dblValue, dblQuantity, dblTaxableValue, dblIGSTRate, dblIGSTAmount, dblCGSTRate,
            dblCGSTAmount, dblSGSTRate, dblSGSTAmount, dblCessRate, dblCessAmount, dblCessAmountPerUnit, dblAdditionalCessAmount, dblSalesTaxAmount,
            dblServiceTaxAmount, dblAmount;
    private boolean bSupplierStateCodeStatus;

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

    public int getiPurchaseOrderNo() {
        return iPurchaseOrderNo;
    }

    public void setiPurchaseOrderNo(int iPurchaseOrderNo) {
        this.iPurchaseOrderNo = iPurchaseOrderNo;
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

    public double getDblPurchaseRate() {
        return dblPurchaseRate;
    }

    public void setDblPurchaseRate(double dblPurchaseRate) {
        this.dblPurchaseRate = dblPurchaseRate;
    }

    public double getDblMRP() {
        return dblMRP;
    }

    public void setDblMRP(double dblMRP) {
        this.dblMRP = dblMRP;
    }

    public double getDblRetailPrice() {
        return dblRetailPrice;
    }

    public void setDblRetailPrice(double dblRetailPrice) {
        this.dblRetailPrice = dblRetailPrice;
    }

    public double getDblWholeSalePrice() {
        return dblWholeSalePrice;
    }

    public void setDblWholeSalePrice(double dblWholeSalePrice) {
        this.dblWholeSalePrice = dblWholeSalePrice;
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

    public boolean isbSupplierStateCodeStatus() {
        return bSupplierStateCodeStatus;
    }

    public void setbSupplierStateCodeStatus(boolean bSupplierStateCodeStatus) {
        this.bSupplierStateCodeStatus = bSupplierStateCodeStatus;
    }
}
