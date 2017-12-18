package com.wep.common.app.print;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by PriyabratP on 06-09-2016.
 */
public class PrintKotBillItem implements Serializable {

    private String billNo;
    private String tableNo;
    private String waiterName;
    private int waiterNo;
    private int boldHeader;
    private int AmountInNextLine;
    private int ownerDetail;
    private int printService;
    private String orderBy;
    private String customerName;
    private String date;
    private String time;
    private double subTotal;
    private double netTotal;
    private String addressLine1 = "";
    private String addressLine2 = "";
    private String addressLine3 = "";
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
    private ArrayList<BillKotItem> billKotItems;
    private ArrayList<BillTaxItem> billTaxItems;
    private ArrayList<BillServiceTaxItem> billServiceTaxItems;
    private ArrayList<BillServiceTaxItem> billcessTaxItems;
    private ArrayList<BillSubTaxItem> billSubTaxItems;
    private ArrayList<BillTaxSlab> billTaxSlabs;

    private ArrayList<BillTaxItem> billOtherChargesItems;
    private String strBillingModeName = "";


    private String strBillingMode = "";
    private String strPaymentStatus = "";
    private String strTotalSalesTaxAmount = "";
    private String strTotalServiceTaxAmount = "";
    private float fTotalsubTaxPercent = 0;
    private float fdiscount = 0;
    private float fdiscountPercentage = 0;
    private String isInterState = "";
    private String isDuplicate = "";

    private int UTGSTEnabled = 0;
    private int HSNPrintEnabled_out = 0;
    private float roundOff = 0;
    private  double cardPaymentValue =0.00;
    private  double pettyCashPaymentValue =0.00;
    private  double eWalletPaymentValue =0.00;
    private  double couponPaymentValue =0.00;
    private  double cashPaymentValue =0.00;
    private double changePaymentValue =0.00;

    public PrintKotBillItem() {
    }

    // please check all the parameters are present in this constructor or not, before calling from your activity
    public PrintKotBillItem(String billNo, String tableNo, String waiterName, int waiterNo,
                            int boldHeader, int ownerDetail, int printService, String orderBy,
                            String customerName, String date, String time, double subTotal,
                            double netTotal, String addressLine1, String addressLine2, String addressLine3,
                            String headerLine1, String headerLine2, String headerLine3, String headerLine4,
                            String headerLine5, String footerLine1, String footerLine2, String footerLine3,
                            String footerLine4, String footerLine5, ArrayList<BillKotItem> billKotItems,
                            ArrayList<BillTaxItem> billTaxItems, ArrayList<BillServiceTaxItem> billServiceTaxItems,
                            ArrayList<BillServiceTaxItem> billcessTaxItems, ArrayList<BillSubTaxItem> billSubTaxItems,
                            ArrayList<BillTaxSlab> billTaxSlabs, ArrayList<BillTaxItem> billOtherChargesItems,
                            String strBillingModeName, String strBillingMode, String strPaymentStatus, String strTotalSalesTaxAmount,
                            String strTotalServiceTaxAmount, float fTotalsubTaxPercent, float fdiscount,
                            float fdiscountPercentage, String isInterState, String isDuplicate, int UTGSTEnabled,
                            int HSNPrintEnabled_out, float roundOff) {
        this.billNo = billNo;
        this.tableNo = tableNo;
        this.waiterName = waiterName;
        this.waiterNo = waiterNo;
        this.boldHeader = boldHeader;
        this.ownerDetail = ownerDetail;
        this.printService = printService;
        this.orderBy = orderBy;
        this.customerName = customerName;
        this.date = date;
        this.time = time;
        this.subTotal = subTotal;
        this.netTotal = netTotal;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        HeaderLine1 = headerLine1;
        HeaderLine2 = headerLine2;
        HeaderLine3 = headerLine3;
        HeaderLine4 = headerLine4;
        HeaderLine5 = headerLine5;
        this.footerLine1 = footerLine1;
        this.footerLine2 = footerLine2;
        this.footerLine3 = footerLine3;
        this.footerLine4 = footerLine4;
        this.footerLine5 = footerLine5;
        this.billKotItems = billKotItems;
        this.billTaxItems = billTaxItems;
        this.billServiceTaxItems = billServiceTaxItems;
        this.billcessTaxItems = billcessTaxItems;
        this.billSubTaxItems = billSubTaxItems;
        this.billTaxSlabs = billTaxSlabs;
        this.billOtherChargesItems = billOtherChargesItems;
        this.strBillingModeName = strBillingModeName;
        this.strBillingMode = strBillingMode;
        this.strPaymentStatus = strPaymentStatus;
        this.strTotalSalesTaxAmount = strTotalSalesTaxAmount;
        this.strTotalServiceTaxAmount = strTotalServiceTaxAmount;
        this.fTotalsubTaxPercent = fTotalsubTaxPercent;
        this.fdiscount = fdiscount;
        this.fdiscountPercentage = fdiscountPercentage;
        this.isInterState = isInterState;
        this.isDuplicate = isDuplicate;
        this.UTGSTEnabled = UTGSTEnabled;
        this.HSNPrintEnabled_out = HSNPrintEnabled_out;
        this.roundOff = roundOff;
    }

    public double getChangePaymentValue() {
        return changePaymentValue;
    }

    public void setChangePaymentValue(double changePaymentValue) {
        this.changePaymentValue = changePaymentValue;
    }

    public double getCardPaymentValue() {
        return cardPaymentValue;
    }

    public void setCardPaymentValue(double cardPaymentValue) {
        this.cardPaymentValue = cardPaymentValue;
    }

    public double getPettyCashPaymentValue() {
        return pettyCashPaymentValue;
    }

    public void setPettyCashPaymentValue(double pettyCashPaymentValue) {
        this.pettyCashPaymentValue = pettyCashPaymentValue;
    }

    public double geteWalletPaymentValue() {
        return eWalletPaymentValue;
    }

    public void seteWalletPaymentValue(double eWalletPaymentValue) {
        this.eWalletPaymentValue = eWalletPaymentValue;
    }

    public double getCouponPaymentValue() {
        return couponPaymentValue;
    }

    public void setCouponPaymentValue(double couponPaymentValue) {
        this.couponPaymentValue = couponPaymentValue;
    }

    public double getCashPaymentValue() {
        return cashPaymentValue;
    }

    public void setCashPaymentValue(double cashPaymentValue) {
        this.cashPaymentValue = cashPaymentValue;
    }

    public int getAmountInNextLine() {
        return AmountInNextLine;
    }

    public void setAmountInNextLine(int amountInNextLine) {
        AmountInNextLine = amountInNextLine;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
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

    public int getBoldHeader() {
        return boldHeader;
    }

    public void setBoldHeader(int boldHeader) {
        this.boldHeader = boldHeader;
    }

    public int getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(int ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public int getPrintService() {
        return printService;
    }

    public void setPrintService(int printService) {
        this.printService = printService;
    }

    public ArrayList<BillTaxSlab> getBillTaxSlabs() {
        return billTaxSlabs;
    }

    public void setBillTaxSlabs(ArrayList<BillTaxSlab> billTaxSlabs) {
        this.billTaxSlabs = billTaxSlabs;
    }

    public int getHSNPrintEnabled_out() {
        return HSNPrintEnabled_out;
    }

    public void setHSNPrintEnabled_out(int HSNPrintEnabled_out) {
        this.HSNPrintEnabled_out = HSNPrintEnabled_out;
    }

    public float getRoundOff() {
        return roundOff;
    }

    public void setRoundOff(float roundOff) {
        this.roundOff = roundOff;
    }

    public int getUTGSTEnabled() {
        return UTGSTEnabled;
    }

    public void setUTGSTEnabled(int UTGSTEnabled) {
        this.UTGSTEnabled = UTGSTEnabled;
    }

    public String getIsDuplicate() {
        return isDuplicate;
    }

    public void setIsDuplicate(String isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    public String getIsInterState() {
        return isInterState;
    }

    public void setIsInterState(String isInterState) {
        this.isInterState = isInterState;
    }

    public ArrayList<BillServiceTaxItem> getBillcessTaxItems() {
        return billcessTaxItems;
    }

    public void setBillcessTaxItems(ArrayList<BillServiceTaxItem> billcessTaxItems) {
        this.billcessTaxItems = billcessTaxItems;
    }

    public float getdiscountPercentage() {
        return fdiscountPercentage;
    }

    public void setdiscountPercentage(float fdiscountPercentage) {
        this.fdiscountPercentage = fdiscountPercentage;
    }

    public float getFdiscount() {
        return fdiscount;
    }

    public void setFdiscount(float fdiscount) {
        this.fdiscount = fdiscount;
    }

    public String getStrBillingModeName() {
        return strBillingModeName;
    }

    public void setStrBillingModeName(String strBillingModeName) {
        this.strBillingModeName = strBillingModeName;
    }

    public ArrayList<BillTaxItem> getBillOtherChargesItems() {
        return billOtherChargesItems;
    }

    public void setBillOtherChargesItems(ArrayList<BillTaxItem> billOtherChargesItems) {
        this.billOtherChargesItems = billOtherChargesItems;
    }

    public Float getTotalsubTaxPercent() {
        return fTotalsubTaxPercent;
    }

    public void setTotalsubTaxPercent(Float TotalsubTaxPercent) {
        this.fTotalsubTaxPercent = TotalsubTaxPercent;
    }

    public String getTotalSalesTaxAmount() {
        return strTotalSalesTaxAmount;
    }

    public void setTotalSalesTaxAmount(String TotalSalesTaxAmount) {
        this.strTotalSalesTaxAmount = TotalSalesTaxAmount;
    }

    public String getTotalServiceTaxAmount() {
        return strTotalServiceTaxAmount;
    }

    public void setTotalServiceTaxAmount(String TotalServiceTaxAmount) {
        this.strTotalServiceTaxAmount = TotalServiceTaxAmount;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public int getWaiterNo() {
        return waiterNo;
    }

    public void setWaiterNo(int waiterNo) {
        this.waiterNo = waiterNo;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }



    public ArrayList<BillKotItem> getBillKotItems() {
        return billKotItems;
    }

    public void setBillKotItems(ArrayList<BillKotItem> billKotItems) {
        this.billKotItems = billKotItems;
    }

    public ArrayList<BillTaxItem> getBillTaxItems() {
        return billTaxItems;
    }

    public void setBillTaxItems(ArrayList<BillTaxItem> billTaxItems) {
        this.billTaxItems = billTaxItems;
    }

    public ArrayList<BillServiceTaxItem> getBillServiceTaxItems() {
        return billServiceTaxItems;
    }

    public void setBillServiceTaxItems(ArrayList<BillServiceTaxItem> billServiceTaxItems) {
        this.billServiceTaxItems = billServiceTaxItems;
    }

    public ArrayList<BillSubTaxItem> getBillSubTaxItems() {
        return billSubTaxItems;
    }

    public void setBillSubTaxItems(ArrayList<BillSubTaxItem> billSubTaxItems) {
        this.billSubTaxItems = billSubTaxItems;
    }

    // ---  added by raja
    public String getBillingMode() {
        return strBillingMode;
    }

    public void setBillingMode(String BillingMode) {
        this.strBillingMode = BillingMode;
    }

    public String getPaymentStatus() {
        return strPaymentStatus;
    }

    public void setPaymentStatus(String PaymentStatus) {
        this.strPaymentStatus = PaymentStatus;
    }

    // --------------------
}
