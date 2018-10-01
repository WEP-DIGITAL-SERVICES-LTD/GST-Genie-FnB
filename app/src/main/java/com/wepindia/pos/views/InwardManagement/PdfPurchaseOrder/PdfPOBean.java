package com.wepindia.pos.views.InwardManagement.PdfPurchaseOrder;

import java.util.ArrayList;

public class PdfPOBean {

    private int _id,iIsgoodInward, iPurchaseOrderNo;
    private String strInvoiceNo, strInvoiceDate, strSupplierId, strSupplierName, strPurchaseOrderDate,
            strSupplierPhone, strSupplierAddress, strSupplierGSTIN, strSupplierType, strSupplyType, companyLogoPath = "",
            strAdditionalCharge, strSupplierPOS;
    private String ownerGstin = "";
    private String ownerPos = "";
    private String ownerStateCode = "";
    private String ownerName = "";
    private String ownerAddress = "";
    private double dblAmount, dblAdditionalChargeAmount;
    private boolean bSupplierStateCodeStatus;
    private ArrayList<PdfPOItemBean> pdfItemBeanArrayList;

    public String getOwnerGstin() {
        return ownerGstin;
    }

    public void setOwnerGstin(String ownerGstin) {
        this.ownerGstin = ownerGstin;
    }

    public String getOwnerPos() {
        return ownerPos;
    }

    public void setOwnerPos(String ownerPos) {
        this.ownerPos = ownerPos;
    }

    public String getOwnerStateCode() {
        return ownerStateCode;
    }

    public void setOwnerStateCode(String ownerStateCode) {
        this.ownerStateCode = ownerStateCode;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public String getStrPurchaseOrderDate() {
        return strPurchaseOrderDate;
    }

    public void setStrPurchaseOrderDate(String strPurchaseOrderDate) {
        this.strPurchaseOrderDate = strPurchaseOrderDate;
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

    public String getCompanyLogoPath() {
        return companyLogoPath;
    }

    public void setCompanyLogoPath(String companyLogoPath) {
        this.companyLogoPath = companyLogoPath;
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

    public boolean isbSupplierStateCodeStatus() {
        return bSupplierStateCodeStatus;
    }

    public void setbSupplierStateCodeStatus(boolean bSupplierStateCodeStatus) {
        this.bSupplierStateCodeStatus = bSupplierStateCodeStatus;
    }

    public ArrayList<PdfPOItemBean> getPdfItemBeanArrayList() {
        return pdfItemBeanArrayList;
    }

    public void setPdfItemBeanArrayList(ArrayList<PdfPOItemBean> pdfItemBeanArrayList) {
        this.pdfItemBeanArrayList = pdfItemBeanArrayList;
    }
}
