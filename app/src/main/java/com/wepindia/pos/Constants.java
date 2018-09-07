package com.wepindia.pos;

/**
 * Created by Administrator on 09-01-2018.
 */

public interface Constants {

    // PDF Invoice
    String PDF_INVOICE_DIRECTORY = "WeP_FnB_Invoices";

    //Printer
    public static final String USB_EPSON_PRINTER_NAME = "TM Printer";
    public static final int VENDOR_ID_EPSON_POS_PRINTER = 1208;
    public static final int PRODUCT_ID_EPSON_POS_PRINTER = 3601;

    public static final String USB_BIXOLON_PRINTER_NAME = "Bixolon";
    public static final int VENDOR_ID_BIXOLON_POS_PRINTER = 5380;
    public static final int PRODUCT_ID_BIXOLON_POS_PRINTER = 42;

    public static final String USB_WEP_PRINTER_NAME = "Gprinter USB Printer";
    public static final int VENDOR_ID_WEP_POS_PRINTER = 1137;
    public static final int PRODUCT_ID_WEP_POS_PRINTER = 85;

    //TVS printer product and vendor id
    public static final String USB_TVS_PRINTER_NAME = "TVS";
    public static final int VENDOR_ID_TVS_POS_PRINTER = 5455;
    public static final int PRODUCT_ID_TVS_POS_PRINTER = 5455;

    //WiFi printer product and vendor id
    public static final String USB_WiFi_PRINTER_NAME = "WiFi Printer";

    //Billing Screens
    String CUSTID = "CustId";
    String TOTALBILLAMOUNT = "TotalBillAmount";
    String PHONENO = "PhoneNo";
    String TAXABLEVALUE = "TaxableValue";
    String TAXAMOUNT = "TaxAmount";
    String OTHERCHARGES = "OtherCharges";
    String DISCOUNTAMOUNT = "DiscountAmount";
    String TAXTYPE = "TaxType";
    String ROUNDOFFAMOUNT = "RoundOffAmount";
    String ORDERLIST = "Order List";

    //PayBill
    public String EWALLET = "E-Wallet";
    public String COUPON = "Coupon";
    public String CREDITCUSTOMER = "Credit Customer";
    public String CASH = "Cash";
    public String OTHERCARDS = "Other Cards";
    public String REWARDSPOINTS = "Reward Points";
    public String AEPS_UPI = "AEPS/UPI";
    public String MSWIPE = "MSwipe";
    public String PETTYCASH = "PettyCash";
    String DISCOUNT = "Discount";
    String ORDERDELIVERED = "Order Delivered";
}
