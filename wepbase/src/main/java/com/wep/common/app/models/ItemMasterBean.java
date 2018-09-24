package com.wep.common.app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MohanN on 1/5/2018.
 */

public class ItemMasterBean implements Parcelable {
    public static final String ITEM_MASTER_PARCELABLE_KEY ="item_master";

    int _id, iShortCode, iBrandCode, iCategoryCode, iDeptCode, isFav, isActive, iMode, tempId, isRupeeBilling;
    String strShortName, strLongName, strBarcode, strSupplyType, strUOM, strHSNCode,
            strImageUri;
    double dblRetailPrice, dblMRP, dblWholeSalePrice, dblQty, dbDiscountPer, dblAdditionalCessAmount, dblAmount, dblValue,
            dblDiscountAmt, dblCGSTRate, dblSGSTRate, dblIGSTRate, dblCessRate, dblCessAmount, dblAveragePurchaseRate;

    public ItemMasterBean(){

    }

    protected ItemMasterBean(Parcel in) {
        _id = in.readInt();
        iShortCode = in.readInt();
        iBrandCode = in.readInt();
        iCategoryCode = in.readInt();
        iDeptCode = in.readInt();
        isFav = in.readInt();
        isActive = in.readInt();
        iMode = in.readInt();
        tempId = in.readInt();
        isRupeeBilling = in.readInt();
        strShortName = in.readString();
        strLongName = in.readString();
        strBarcode = in.readString();
        strSupplyType = in.readString();
        strUOM = in.readString();
        strHSNCode = in.readString();
        strImageUri = in.readString();
        dblRetailPrice = in.readDouble();
        dblMRP = in.readDouble();
        dblWholeSalePrice = in.readDouble();
        dblQty = in.readDouble();
        dbDiscountPer = in.readDouble();
        dblAdditionalCessAmount = in.readDouble();
        dblAmount = in.readDouble();
        dblValue = in.readDouble();
        dblDiscountAmt = in.readDouble();
        dblCGSTRate = in.readDouble();
        dblSGSTRate = in.readDouble();
        dblIGSTRate = in.readDouble();
        dblCessRate = in.readDouble();
        dblCessAmount = in.readDouble();
        dblAveragePurchaseRate = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeInt(iShortCode);
        dest.writeInt(iBrandCode);
        dest.writeInt(iCategoryCode);
        dest.writeInt(iDeptCode);
        dest.writeInt(isFav);
        dest.writeInt(isActive);
        dest.writeInt(iMode);
        dest.writeInt(tempId);
        dest.writeInt(isRupeeBilling);
        dest.writeString(strShortName);
        dest.writeString(strLongName);
        dest.writeString(strBarcode);
        dest.writeString(strSupplyType);
        dest.writeString(strUOM);
        dest.writeString(strHSNCode);
        dest.writeString(strImageUri);
        dest.writeDouble(dblRetailPrice);
        dest.writeDouble(dblMRP);
        dest.writeDouble(dblWholeSalePrice);
        dest.writeDouble(dblQty);
        dest.writeDouble(dbDiscountPer);
        dest.writeDouble(dblAdditionalCessAmount);
        dest.writeDouble(dblAmount);
        dest.writeDouble(dblValue);
        dest.writeDouble(dblDiscountAmt);
        dest.writeDouble(dblCGSTRate);
        dest.writeDouble(dblSGSTRate);
        dest.writeDouble(dblIGSTRate);
        dest.writeDouble(dblCessRate);
        dest.writeDouble(dblCessAmount);
        dest.writeDouble(dblAveragePurchaseRate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemMasterBean> CREATOR = new Creator<ItemMasterBean>() {
        @Override
        public ItemMasterBean createFromParcel(Parcel in) {
            return new ItemMasterBean(in);
        }

        @Override
        public ItemMasterBean[] newArray(int size) {
            return new ItemMasterBean[size];
        }
    };

    public int getiMode() {
        return iMode;
    }

    public void setiMode(int iMode) {
        this.iMode = iMode;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int tempId) {
        this.tempId = tempId;
    }

    public int getiShortCode() {
        return iShortCode;
    }

    public void setiShortCode(int iShortCode) {
        this.iShortCode = iShortCode;
    }

    public int getiBrandCode() {
        return iBrandCode;
    }

    public void setiBrandCode(int iBrandCode) {
        this.iBrandCode = iBrandCode;
    }

    public int getiCategoryCode() {
        return iCategoryCode;
    }

    public void setiCategoryCode(int iCategoryCode) {
        this.iCategoryCode = iCategoryCode;
    }

    public int getiDeptCode() {
        return iDeptCode;
    }

    public void setiDeptCode(int iDeptCode) {
        this.iDeptCode = iDeptCode;
    }

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getStrShortName() {
        return strShortName;
    }

    public void setStrShortName(String strShortName) {
        this.strShortName = strShortName;
    }

    public String getStrLongName() {
        return strLongName;
    }

    public void setStrLongName(String strLongName) {
        this.strLongName = strLongName;
    }

    public String getStrBarcode() {
        return strBarcode;
    }

    public void setStrBarcode(String strBarcode) {
        this.strBarcode = strBarcode;
    }

    public String getStrSupplyType() {
        return strSupplyType;
    }

    public void setStrSupplyType(String strSupplyType) {
        this.strSupplyType = strSupplyType;
    }

    public String getStrUOM() {
        return strUOM;
    }

    public void setStrUOM(String strUOM) {
        this.strUOM = strUOM;
    }

    public String getStrHSNCode() {
        return strHSNCode;
    }

    public void setStrHSNCode(String strHSNCode) {
        this.strHSNCode = strHSNCode;
    }

    public String getStrImageUri() {
        return strImageUri;
    }

    public void setStrImageUri(String strImageUri) {
        this.strImageUri = strImageUri;
    }

    public double getDblRetailPrice() {
        return dblRetailPrice;
    }

    public void setDblRetailPrice(double dblRetailPrice) {
        this.dblRetailPrice = dblRetailPrice;
    }

    public double getDblMRP() {
        return dblMRP;
    }

    public void setDblMRP(double dblMRP) {
        this.dblMRP = dblMRP;
    }

    public double getDblWholeSalePrice() {
        return dblWholeSalePrice;
    }

    public void setDblWholeSalePrice(double dblWholeSalePrice) {
        this.dblWholeSalePrice = dblWholeSalePrice;
    }

    public double getDblAmount() {
        return dblAmount;
    }

    public void setDblAmount(double dblAmount) {
        this.dblAmount = dblAmount;
    }

    public double getDblQty() {
        return dblQty;
    }

    public void setDblQty(double dblQty) {
        this.dblQty = dblQty;
    }

    public double getDbDiscountPer() {
        return dbDiscountPer;
    }

    public void setDbDiscountPer(double dbDiscountPer) {
        this.dbDiscountPer = dbDiscountPer;
    }

    public double getDblDiscountAmt() {
        return dblDiscountAmt;
    }

    public void setDblDiscountAmt(double dblDiscountAmt) {
        this.dblDiscountAmt = dblDiscountAmt;
    }

    public double getDblCGSTRate() {
        return dblCGSTRate;
    }

    public void setDblCGSTRate(double dblCGSTRate) {
        this.dblCGSTRate = dblCGSTRate;
    }

    public double getDblSGSTRate() {
        return dblSGSTRate;
    }

    public void setDblSGSTRate(double dblSGSTRate) {
        this.dblSGSTRate = dblSGSTRate;
    }

    public double getDblIGSTRate() {
        return dblIGSTRate;
    }

    public void setDblIGSTRate(double dblIGSTRate) {
        this.dblIGSTRate = dblIGSTRate;
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

    public double getDblAveragePurchaseRate() {
        return dblAveragePurchaseRate;
    }

    public void setDblAveragePurchaseRate(double dblAveragePurchaseRate) {
        this.dblAveragePurchaseRate = dblAveragePurchaseRate;
    }

    public double getDblAdditionalCessAmount() {
        return dblAdditionalCessAmount;
    }

    public void setDblAdditionalCessAmount(double dblAdditionalCessAmount) {
        this.dblAdditionalCessAmount = dblAdditionalCessAmount;
    }

    public int getIsRupeeBilling() {
        return isRupeeBilling;
    }

    public void setIsRupeeBilling(int isRupeeBilling) {
        this.isRupeeBilling = isRupeeBilling;
    }

    public double getDblValue() {
        return dblValue;
    }

    public void setDblValue(double dblValue) {
        this.dblValue = dblValue;
    }
}
