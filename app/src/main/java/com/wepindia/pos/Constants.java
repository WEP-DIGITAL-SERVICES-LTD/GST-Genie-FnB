package com.wepindia.pos;

/**
 * Created by Administrator on 09-01-2018.
 */

public interface Constants {

    public final String SHAREDPREFERENCE = "SharedPreference";
    public  final String KEY_id = "_id";
    public  final String KEY_CustId = "CustId";

    public static final String CUSTOMER_PASSBOOK_TAG = "Customer Passbook";
    public String SALES_MAN_ID_KEY = "Salesman";
    public static final String OPENING_BALANCE = "Opening Balance";
    public static final String DEPOSIT = "Deposit";
    public static final String BILL_NO = "BillNo";

    public static final int INSERT = 1;
    public static final int UPDATE = 2;

    public static final String NOTIFICATION_PAPER_COUNT = "NOTIFICATION_PAPER_COUNT";
    public static final int NOTIFICATION_PAPER_COUNT_1 = 1001;
    public static String BLOCKBILLING = "BlockBilling";

    // user constants
    public static String USER_ID = "user_id";
    public static String USER_NAME = "user_name";
    public static String USER_ROLE = "user_role";

    // Progress Dialog Title
    public static final String waiting = "Waiting";
    public static final String processing = "Processing";
    public static final String waring = "Warning";
    public static final String success = "Success";
    public static final String loading = "Loading";

    public static final int MODE = 0;
    public static final int KITCHEN_MODE = 1;
    public static final int DEPARTMENT_MODE = 2;
    public static final int CATEGORY_MODE = 3;
    public static final int ACTIVE_MODE = 4;
    public static final int INACTIVE_MODE = 5;
    public static final int MINSTOCK_MODE = 6;

    //FilePicker
    public String FRAGMENTNAME = "FragmentName";

    // SMS Sending
    public static int HTTP_GET = 1;
    public static int HTTP_POST = 2;
    public static int SMS_SENDING = 1200;
    String message_email = "Dear Customer, \nThank you for shopping with us.\nKindly find the attached invoice.\nRegards,\n";

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
