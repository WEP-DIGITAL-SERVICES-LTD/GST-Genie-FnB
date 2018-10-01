package com.wep.common.app.models;

/**
 * Created by RichaA on 6/5/2017.
 */

public class ItemInward {
    // Private variable
    String itemShortName, itemBarcode, strImageUri;
    int _id;
    double purchaseRate, quantity, rate;
    double IGSTRate, IGSTAmount, CGSTRate, CGSTAmount,SGSTRate, SGSTAmount, cessRate, cessAmount,
            cessAmountPerUnit, additionalCessAmount, totalAdditioalCessAmount;
    String UOM,HSNCode,TaxationType , SupplyType, pos;
    private boolean isSelected;



    public ItemInward(int _id, String itemShortName, String itemBarcode, String strImageUri, String HSNCode,
                      double fAveragerate, double quantity, String MOU,
                      double IGSTRate, double IGSTAmount, double CGSTRate, double CGSTAmount,
                      double SGSTRate, double SGSTAmount, double cessRate, double cessAmount,
                      String taxationType, String supplyType)
    {
        this.itemShortName = itemShortName;
        this.itemBarcode = itemBarcode;
        this.strImageUri = strImageUri;
        this._id = _id;
        this.purchaseRate = fAveragerate;
        this.quantity = quantity;
        this.IGSTRate = IGSTRate;
        this.IGSTAmount = IGSTAmount;
        this.CGSTRate = CGSTRate;
        this.CGSTAmount = CGSTAmount;
        this.SGSTRate = SGSTRate;
        this.SGSTAmount = SGSTAmount;
        this.cessRate = cessRate;
        this.cessAmount = cessAmount;
        this.UOM = MOU;
        this.HSNCode = HSNCode;
        TaxationType = taxationType;
        SupplyType = supplyType;
        this.pos = "";
    }
    public ItemInward() {
        this.itemShortName = "";
        this.itemBarcode = "";
        this.strImageUri = "";
        this._id = -1;
        this.purchaseRate = 0;
        this.quantity = 0;
        this.IGSTRate = 0;
        this.IGSTAmount = 0;
        this.CGSTRate = 0;
        this.CGSTAmount = 0;
        this.SGSTRate = 0;
        this.SGSTAmount = 0;
        this.cessRate = 0;
        this.cessAmount = 0;
        this.UOM = "";
        this.HSNCode = "";
        TaxationType = "";
        SupplyType = "";
        this.pos = "";

    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public double getCessAmountPerUnit() {
        return cessAmountPerUnit;
    }

    public void setCessAmountPerUnit(double cessAmountPerUnit) {
        this.cessAmountPerUnit = cessAmountPerUnit;
    }

    public double getAdditionalCessAmount() {
        return additionalCessAmount;
    }

    public void setAdditionalCessAmount(double additionalCessAmount) {
        this.additionalCessAmount = additionalCessAmount;
    }

    public double getTotalAdditioalCessAmount() {
        return totalAdditioalCessAmount;
    }

    public void setTotalAdditioalCessAmount(double totalAdditioalCessAmount) {
        this.totalAdditioalCessAmount = totalAdditioalCessAmount;
    }

    public String getItemShortName() {
        return itemShortName;
    }

    public void setItemShortName(String itemShortName) {
        this.itemShortName = itemShortName;
    }

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public String getStrImageUri() {
        return strImageUri;
    }

    public void setStrImageUri(String strImageUri) {
        this.strImageUri = strImageUri;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double getPurchaseRate() {
        return purchaseRate;
    }

    public void setPurchaseRate(double purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getCessRate() {
        return cessRate;
    }

    public void setCessRate(double cessRate) {
        this.cessRate = cessRate;
    }

    public double getCessAmount() {
        return cessAmount;
    }

    public void setCessAmount(double cessAmount) {
        this.cessAmount = cessAmount;
    }

    public double getIGSTRate() {
        return IGSTRate;
    }

    public void setIGSTRate(double IGSTRate) {
        this.IGSTRate = IGSTRate;
    }

    public double getIGSTAmount() {
        return IGSTAmount;
    }

    public void setIGSTAmount(double IGSTAmount) {
        this.IGSTAmount = IGSTAmount;
    }

    public double getCGSTRate() {
        return CGSTRate;
    }

    public void setCGSTRate(double CGSTRate) {
        this.CGSTRate = CGSTRate;
    }

    public double getCGSTAmount() {
        return CGSTAmount;
    }

    public void setCGSTAmount(double CGSTAmount) {
        this.CGSTAmount = CGSTAmount;
    }

    public double getSGSTRate() {
        return SGSTRate;
    }

    public void setSGSTRate(double SGSTRate) {
        this.SGSTRate = SGSTRate;
    }

    public double getSGSTAmount() {
        return SGSTAmount;
    }

    public void setSGSTAmount(double SGSTAmount) {
        this.SGSTAmount = SGSTAmount;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getHSNCode() {
        return HSNCode;
    }

    public void setHSNCode(String HSNCode) {
        this.HSNCode = HSNCode;
    }

    public String getTaxationType() {
        return TaxationType;
    }

    public void setTaxationType(String taxationType) {
        TaxationType = taxationType;
    }

    public String getSupplyType() {
        return SupplyType;
    }

    public void setSupplyType(String supplyType) {
        SupplyType = supplyType;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }
}
