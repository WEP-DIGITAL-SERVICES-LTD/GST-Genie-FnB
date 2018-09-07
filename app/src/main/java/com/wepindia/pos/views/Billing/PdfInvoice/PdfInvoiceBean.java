package com.wepindia.pos.views.Billing.PdfInvoice;

import java.util.ArrayList;

/**
 * Created by SachinV on 16-03-2018.
 */

public class PdfInvoiceBean {


    private String invoiceNo = "";
    private String invoiceDate = "";
    private String ownerGstin = "";
    private String ownerPos = "";
    private String ownerStateCode = "";

    private String customerName = "";
    private String customerAddress = "";
    private String customerGstin = "";
    private String customerState = "";
    private String companyLogoPath = "";

    private String HeaderLine1 = "";
    private String HeaderLine2 = "";
    private String HeaderLine3 = "";
    private String HeaderLine4 = "";
    private String HeaderLine5 = "";
    private String footerLine1 = "";
    private String footerLine2 = "";
    private String footerLine3 = "";
    private String footerLine4 = "";
    private String footerLine5 = "";
    private String strJurisdictionsPrint = "";
    private int billAmountRoundOff = 0;
    private double otherCharges = 0;
    private boolean isReverseTax;
    private boolean isTrainingMode;

    private ArrayList<PdfItemBean> pdfItemBeanArrayList;

    public String getStrJurisdictionsPrint() {
        return strJurisdictionsPrint;
    }

    public void setStrJurisdictionsPrint(String strJurisdictionsPrint) {
        this.strJurisdictionsPrint = strJurisdictionsPrint;
    }

    public PdfInvoiceBean() {
    }

    public boolean isTrainingMode() {
        return isTrainingMode;
    }

    public void setTrainingMode(boolean trainingMode) {
        isTrainingMode = trainingMode;
    }

    public boolean isReverseTax() {
        return isReverseTax;
    }

    public void setReverseTax(boolean reverseTax) {
        isReverseTax = reverseTax;
    }

    public double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(double otherCharges) {
        this.otherCharges = otherCharges;
    }

    public int getBillAmountRoundOff() {
        return billAmountRoundOff;
    }

    public void setBillAmountRoundOff(int billAmountRoundOff) {
        this.billAmountRoundOff = billAmountRoundOff;
    }

    public String getCompanyLogoPath() {
        return companyLogoPath;
    }

    public void setCompanyLogoPath(String companyLogoPath) {
        this.companyLogoPath = companyLogoPath;
    }

    public String getOwnerStateCode() {
        return ownerStateCode;
    }

    public void setOwnerStateCode(String ownerStateCode) {
        this.ownerStateCode = ownerStateCode;
    }

    public ArrayList<PdfItemBean> getPdfItemBeanArrayList() {
        return pdfItemBeanArrayList;
    }

    public void setPdfItemBeanArrayList(ArrayList<PdfItemBean> pdfItemBeanArrayList) {
        this.pdfItemBeanArrayList = pdfItemBeanArrayList;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerGstin() {
        return customerGstin;
    }

    public void setCustomerGstin(String customerGstin) {
        this.customerGstin = customerGstin;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public String getHeaderLine1() {
        return HeaderLine1;
    }

    public void setHeaderLine1(String headerLine1) {
        HeaderLine1 = headerLine1;
    }

    public String getHeaderLine2() {
        return HeaderLine2;
    }

    public void setHeaderLine2(String headerLine2) {
        HeaderLine2 = headerLine2;
    }

    public String getHeaderLine3() {
        return HeaderLine3;
    }

    public void setHeaderLine3(String headerLine3) {
        HeaderLine3 = headerLine3;
    }

    public String getHeaderLine4() {
        return HeaderLine4;
    }

    public void setHeaderLine4(String headerLine4) {
        HeaderLine4 = headerLine4;
    }

    public String getHeaderLine5() {
        return HeaderLine5;
    }

    public void setHeaderLine5(String headerLine5) {
        HeaderLine5 = headerLine5;
    }

    public String getFooterLine1() {
        return footerLine1;
    }

    public void setFooterLine1(String footerLine1) {
        this.footerLine1 = footerLine1;
    }

    public String getFooterLine2() {
        return footerLine2;
    }

    public void setFooterLine2(String footerLine2) {
        this.footerLine2 = footerLine2;
    }

    public String getFooterLine3() {
        return footerLine3;
    }

    public void setFooterLine3(String footerLine3) {
        this.footerLine3 = footerLine3;
    }

    public String getFooterLine4() {
        return footerLine4;
    }

    public void setFooterLine4(String footerLine4) {
        this.footerLine4 = footerLine4;
    }

    public String getFooterLine5() {
        return footerLine5;
    }

    public void setFooterLine5(String footerLine5) {
        this.footerLine5 = footerLine5;
    }
}
