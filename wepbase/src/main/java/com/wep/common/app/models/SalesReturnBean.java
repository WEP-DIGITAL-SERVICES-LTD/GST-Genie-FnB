package com.wep.common.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SalesReturnBean implements Parcelable {

    double dblReturnCGSTAmount, dblReturnSGSTAmount, dblReturnIGSTAmount, dblReturncessAmount, dblReturnAmount, dblReturnQuantity;
    String strCustGSTIN, strCustName, strCustEmail, strCustPhone, strInvoiceNo, strInvoiceDate, strCustId, strUOM, strSalesReturnDate,
            strItemName, strIsReverseTaxEnabled, strReason, strPaymentMode;
    int iID, iItemId, iStockUpdate, iSrId;
    double  dblQty, dblAmount, dblValue, dblTaxableValue,
            dblIGSTRate, dblIGSTAmount,
            dblCGSTRate, dblCGSTAmount,
            dblSGSTRate, dblSGSTAmount,
            dblCessRate, dblCessAmount,
            dblCessAmountPerUnit, dblAdditionalCessAmount, dblTotalAdditionalCessAmount, dblTotalReturnAdditionalCessAmount;

    public SalesReturnBean() {
    }


    protected SalesReturnBean(Parcel in) {
        dblReturnCGSTAmount = in.readDouble();
        dblReturnSGSTAmount = in.readDouble();
        dblReturnIGSTAmount = in.readDouble();
        dblReturncessAmount = in.readDouble();
        dblReturnAmount = in.readDouble();
        dblReturnQuantity = in.readDouble();
        strCustGSTIN = in.readString();
        strCustName = in.readString();
        strCustEmail = in.readString();
        strCustPhone = in.readString();
        strInvoiceNo = in.readString();
        strInvoiceDate = in.readString();
        strCustId = in.readString();
        strUOM = in.readString();
        strSalesReturnDate = in.readString();
        strItemName = in.readString();
        strIsReverseTaxEnabled = in.readString();
        strReason = in.readString();
        strPaymentMode = in.readString();
        iID = in.readInt();
        iItemId = in.readInt();
        iStockUpdate = in.readInt();
        iSrId = in.readInt();
        dblQty = in.readDouble();
        dblAmount = in.readDouble();
        dblValue = in.readDouble();
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
        dblTotalAdditionalCessAmount = in.readDouble();
        dblTotalReturnAdditionalCessAmount = in.readDouble();
    }

    public static final Creator<SalesReturnBean> CREATOR = new Creator<SalesReturnBean>() {
        @Override
        public SalesReturnBean createFromParcel(Parcel in) {
            return new SalesReturnBean(in);
        }

        @Override
        public SalesReturnBean[] newArray(int size) {
            return new SalesReturnBean[size];
        }
    };

    public double getDblTotalAdditionalCessAmount() {
        return dblTotalAdditionalCessAmount;
    }

    public void setDblTotalAdditionalCessAmount(double dblTotalAdditionalCessAmount) {
        this.dblTotalAdditionalCessAmount = dblTotalAdditionalCessAmount;
    }

    public double getDblTotalReturnAdditionalCessAmount() {
        return dblTotalReturnAdditionalCessAmount;
    }

    public void setDblTotalReturnAdditionalCessAmount(double dblTotalReturnAdditionalCessAmount) {
        this.dblTotalReturnAdditionalCessAmount = dblTotalReturnAdditionalCessAmount;
    }

    public int getiSrId() {
        return iSrId;
    }

    public void setiSrId(int iSrId) {
        this.iSrId = iSrId;
    }

    public String getStrReason() {
        return strReason;
    }

    public void setStrReason(String strReason) {
        this.strReason = strReason;
    }

    public String getStrPaymentMode() {
        return strPaymentMode;
    }

    public void setStrPaymentMode(String strPaymentMode) {
        this.strPaymentMode = strPaymentMode;
    }

    public int getiStockUpdate() {
        return iStockUpdate;
    }

    public void setiStockUpdate(int iStockUpdate) {
        this.iStockUpdate = iStockUpdate;
    }

    public String getStrUOM() {
        return strUOM;
    }

    public void setStrUOM(String strUOM) {
        this.strUOM = strUOM;
    }

    public double getDblReturnCGSTAmount() {
        return dblReturnCGSTAmount;
    }

    public void setDblReturnCGSTAmount(double dblReturnCGSTAmount) {
        this.dblReturnCGSTAmount = dblReturnCGSTAmount;
    }

    public double getDblReturnSGSTAmount() {
        return dblReturnSGSTAmount;
    }

    public void setDblReturnSGSTAmount(double dblReturnSGSTAmount) {
        this.dblReturnSGSTAmount = dblReturnSGSTAmount;
    }

    public double getDblReturnIGSTAmount() {
        return dblReturnIGSTAmount;
    }

    public void setDblReturnIGSTAmount(double dblReturnIGSTAmount) {
        this.dblReturnIGSTAmount = dblReturnIGSTAmount;
    }

    public double getDblReturncessAmount() {
        return dblReturncessAmount;
    }

    public void setDblReturncessAmount(double dblReturncessAmount) {
        this.dblReturncessAmount = dblReturncessAmount;
    }

    public double getDblReturnAmount() {
        return dblReturnAmount;
    }

    public void setDblReturnAmount(double dblReturnAmount) {
        this.dblReturnAmount = dblReturnAmount;
    }

    public double getDblReturnQuantity() {
        return dblReturnQuantity;
    }

    public void setDblReturnQuantity(double dblReturnQuantity) {
        this.dblReturnQuantity = dblReturnQuantity;
    }

    public String getStrCustGSTIN() {
        return strCustGSTIN;
    }

    public void setStrCustGSTIN(String strCustGSTIN) {
        this.strCustGSTIN = strCustGSTIN;
    }

    public String getStrCustName() {
        return strCustName;
    }

    public void setStrCustName(String strCustName) {
        this.strCustName = strCustName;
    }

    public String getStrCustEmail() {
        return strCustEmail;
    }

    public void setStrCustEmail(String strCustEmail) {
        this.strCustEmail = strCustEmail;
    }

    public String getStrCustPhone() {
        return strCustPhone;
    }

    public void setStrCustPhone(String strCustPhone) {
        this.strCustPhone = strCustPhone;
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

    public String getStrCustId() {
        return strCustId;
    }

    public void setStrCustId(String strCustId) {
        this.strCustId = strCustId;
    }

    public String getStrItemName() {
        return strItemName;
    }

    public void setStrItemName(String strItemName) {
        this.strItemName = strItemName;
    }

    public String getStrIsReverseTaxEnabled() {
        return strIsReverseTaxEnabled;
    }

    public void setStrIsReverseTaxEnabled(String strIsReverseTaxEnabled) {
        this.strIsReverseTaxEnabled = strIsReverseTaxEnabled;
    }

    public int getiID() {
        return iID;
    }

    public void setiID(int iID) {
        this.iID = iID;
    }

    public int getiItemId() {
        return iItemId;
    }

    public void setiItemId(int iItemId) {
        this.iItemId = iItemId;
    }

    public double getDblQty() {
        return dblQty;
    }

    public void setDblQty(double dblQty) {
        this.dblQty = dblQty;
    }

    public double getDblAmount() {
        return dblAmount;
    }

    public void setDblAmount(double dblAmount) {
        this.dblAmount = dblAmount;
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

    public double getDblValue() {
        return dblValue;
    }

    public void setDblValue(double dblValue) {
        this.dblValue = dblValue;
    }

    public String getStrSalesReturnDate() {
        return strSalesReturnDate;
    }

    public void setStrSalesReturnDate(String strSalesReturnDate) {
        this.strSalesReturnDate = strSalesReturnDate;
    }

    public double getDblTaxableValue() {
        return dblTaxableValue;
    }

    public void setDblTaxableValue(double dblTaxableValue) {
        this.dblTaxableValue = dblTaxableValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeDouble(dblReturnCGSTAmount);
        parcel.writeDouble(dblReturnSGSTAmount);
        parcel.writeDouble(dblReturnIGSTAmount);
        parcel.writeDouble(dblReturncessAmount);
        parcel.writeDouble(dblReturnAmount);
        parcel.writeDouble(dblReturnQuantity);
        parcel.writeString(strCustGSTIN);
        parcel.writeString(strCustName);
        parcel.writeString(strCustEmail);
        parcel.writeString(strCustPhone);
        parcel.writeString(strInvoiceNo);
        parcel.writeString(strInvoiceDate);
        parcel.writeString(strCustId);
        parcel.writeString(strUOM);
        parcel.writeString(strSalesReturnDate);
        parcel.writeString(strItemName);
        parcel.writeString(strIsReverseTaxEnabled);
        parcel.writeString(strReason);
        parcel.writeString(strPaymentMode);
        parcel.writeInt(iID);
        parcel.writeInt(iItemId);
        parcel.writeInt(iStockUpdate);
        parcel.writeInt(iSrId);
        parcel.writeDouble(dblQty);
        parcel.writeDouble(dblAmount);
        parcel.writeDouble(dblValue);
        parcel.writeDouble(dblTaxableValue);
        parcel.writeDouble(dblIGSTRate);
        parcel.writeDouble(dblIGSTAmount);
        parcel.writeDouble(dblCGSTRate);
        parcel.writeDouble(dblCGSTAmount);
        parcel.writeDouble(dblSGSTRate);
        parcel.writeDouble(dblSGSTAmount);
        parcel.writeDouble(dblCessRate);
        parcel.writeDouble(dblCessAmount);
        parcel.writeDouble(dblCessAmountPerUnit);
        parcel.writeDouble(dblAdditionalCessAmount);
        parcel.writeDouble(dblTotalAdditionalCessAmount);
        parcel.writeDouble(dblTotalReturnAdditionalCessAmount);
    }
}
