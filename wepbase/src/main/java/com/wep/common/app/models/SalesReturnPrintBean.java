package com.wep.common.app.models;

import com.wep.common.app.print.BillServiceTaxItem;
import com.wep.common.app.print.BillTaxSlab;

import java.util.ArrayList;

public class SalesReturnPrintBean {

    double dblReturnCGSTAmount, dblReturnSGSTAmount, dblReturnIGSTAmount, dblReturncessAmount, dblReturnAmount, dblReturnQuantity;
    String strCustGSTIN, strCustName, strOwnerGSTIN, strOwnerName, strCustEmail, strCustPhone, strOwnerAddress, strInvoiceNo, strInvoiceDate, strCustId, strUOM, strSalesReturnDate,
            strItemName, strIsReverseTaxEnabled, strReason, strPaymentMode, isDuplicate = "";;
    int iSrId, isInterstate;
    double  dblQty, dblAmount, dblValue,
            dblIGSTRate, dblIGSTAmount,
            dblCGSTRate, dblCGSTAmount,
            dblSGSTRate, dblSGSTAmount,
            dblCessRate, dblCessAmount,
            dblCessAmountPerUnit, dblAdditionalCessAmount;

    private ArrayList<SalesReturnBean> arrayList;
    private ArrayList<BillTaxSlab> billTaxSlabs;
    private ArrayList<BillServiceTaxItem> billcessTaxItems;

    public ArrayList<BillServiceTaxItem> getBillcessTaxItems() {
        return billcessTaxItems;
    }

    public void setBillcessTaxItems(ArrayList<BillServiceTaxItem> billcessTaxItems) {
        this.billcessTaxItems = billcessTaxItems;
    }

    public int getIsInterstate() {
        return isInterstate;
    }

    public void setIsInterstate(int isInterstate) {
        this.isInterstate = isInterstate;
    }

    public String getIsDuplicate() {
        return isDuplicate;
    }

    public void setIsDuplicate(String isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    public int getiSrId() {
        return iSrId;
    }

    public void setiSrId(int iSrId) {
        this.iSrId = iSrId;
    }

    public ArrayList<BillTaxSlab> getBillTaxSlabs() {
        return billTaxSlabs;
    }

    public void setBillTaxSlabs(ArrayList<BillTaxSlab> billTaxSlabs) {
        this.billTaxSlabs = billTaxSlabs;
    }

    public String getStrOwnerGSTIN() {
        return strOwnerGSTIN;
    }

    public void setStrOwnerGSTIN(String strOwnerGSTIN) {
        this.strOwnerGSTIN = strOwnerGSTIN;
    }

    public String getStrOwnerName() {
        return strOwnerName;
    }

    public void setStrOwnerName(String strOwnerName) {
        this.strOwnerName = strOwnerName;
    }

    public String getStrOwnerAddress() {
        return strOwnerAddress;
    }

    public void setStrOwnerAddress(String strOwnerAddress) {
        this.strOwnerAddress = strOwnerAddress;
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

    public String getStrUOM() {
        return strUOM;
    }

    public void setStrUOM(String strUOM) {
        this.strUOM = strUOM;
    }

    public String getStrSalesReturnDate() {
        return strSalesReturnDate;
    }

    public void setStrSalesReturnDate(String strSalesReturnDate) {
        this.strSalesReturnDate = strSalesReturnDate;
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

    public double getDblValue() {
        return dblValue;
    }

    public void setDblValue(double dblValue) {
        this.dblValue = dblValue;
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

    public ArrayList<SalesReturnBean> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<SalesReturnBean> arrayList) {
        this.arrayList = arrayList;
    }
}
