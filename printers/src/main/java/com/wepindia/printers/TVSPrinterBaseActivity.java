package com.wepindia.printers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.wep.common.app.Database.Customer;
import com.wep.common.app.Database.PaymentReceipt;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.SalesReturnBean;
import com.wep.common.app.models.SalesReturnPrintBean;
import com.wep.common.app.print.BillKotItem;
import com.wep.common.app.print.BillServiceTaxItem;
import com.wep.common.app.print.BillTaxItem;
import com.wep.common.app.print.BillTaxSlab;
import com.wep.common.app.print.PrintKotBillItem;
import com.wepindia.printers.tvs_printer.ConstantDefine;
import com.wepindia.printers.tvs_printer.TestPrintInfo;
import com.wepindia.printers.utils.PrinterUtil;
import com.wepindia.printers.wep.PrinterConnectionError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import POSAPI.POSInterfaceAPI;
import POSAPI.POSUSBAPI;
import POSSDK.POSSDK;

import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_CASH_DRAWER_OPEN;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_IMAGE_DOWNLOAD_AND_PRINT;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_IMAGE_DOWNLOAD_FLASH;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_IMAGE_DOWNLOAD_RAM;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_IMAGE_FLASH_PRINT;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_IMAGE_RAM_PRINT;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_IMAGE_STANDARD_MODE_RASTER_PRINT;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_SYSTEM_CUT_PAPER;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_SYSTEM_FEED_LINE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_SYSTEM_QUERY_STATUS;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_SYSTEM_SELECT_PAPER_TYPE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_SYSTEM_SELECT_PRINT_MODE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_SYSTEM_SET_MOTION_UNIT;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_ENTER_QUIT_COLOR_PRINT;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_FONT_USER_DEFINED;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_FONT_USER_DEFINED_CANCEL;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_FONT_USER_DEFINED_ENABLE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SELECT_CHAR_SET;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SELECT_CODE_PAGE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SELECT_FONT_TYPE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SELECT_MAGNIFY_TIMES;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SET_CHARACTER_SPACE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SET_COLOR_PRINT;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SET_FONT_STYLE_BOLD;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SET_FONT_STYLE_REVERSE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SET_FONT_STYLE_SMOOTH;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SET_FONT_STYLE_UNDERLINE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_SET_LINE_HEIGHT;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_STANDARD_MODE_ALIGNMENT;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_STANDARD_MODE_ROTATE;
import static com.wepindia.printers.tvs_printer.ConstantDefine.ERR_TEXT_STANDARD_MODE_UPSIDEDOWN;

/**
 * Created by Mohan on 8/10/2018.
 */
public class TVSPrinterBaseActivity extends WepBaseActivity {

    private Context mContext;
    private String mTarget = null;
    protected PrinterUtil printerUtil;

    //PortType value
    public static final int USBPORT = 1;        //USB
    public static final int SERIALPORT = 2;        //COM
    public static final int WIFIPORT = 3;        //WIFI
    public static final int BLUETOOTHPORT = 4;    //Bluetooth

    //Returned Value Statement
    public static final int POS_SUCCESS = 1000;        //success
    public static final int ERR_PROCESSING = 1001;    //processing error
    public static final int ERR_PARAM = 1002;        //parameter error


    //Print Mode
    private static final int PRINT_MODE_STANDARD = 0;
    private static final int PRINT_MODE_PAGE = 1;
    public static int printMode = PRINT_MODE_STANDARD;

    //Print line length
    private static final int PRINTER_LINE_HEIGHT = 5;
    private static final int PRINTER_LINE_HEIGHT_NORMAL = 2;


    //SDK variable
    public static POSSDK pos_usb = null;
    private POSInterfaceAPI interface_usb = null;
    private int error_code = 0;
    private boolean sdk_flag = false;

    private TestPrintInfo testprint;

    private PrinterConnectionError printerConnectionError = null;

    public void setmTarget(String mTarget) {
        this.mTarget = mTarget;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void mInitListener(PrinterConnectionError printerConnectionError) {
        this.printerConnectionError = printerConnectionError;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializeObject();
    }

    private POSInterfaceAPI getmTVSPrinter() {

        if (interface_usb == null)
            interface_usb = new POSUSBAPI(mContext);

        //TestPrint variable
        testprint = new TestPrintInfo();

        return interface_usb;
    }

    private boolean initializeObject() {
        try {
            getmTVSPrinter();
        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            return false;
        }

        return true;
    }

    private boolean connectPrinter() {
        if (interface_usb == null) {
            return false;
        }

        try {
            error_code = interface_usb.OpenDevice(5455, 5455);
            if (error_code != POS_SUCCESS) {
                Toast.makeText(mContext, "Failed to connect printer, please try again.", Toast.LENGTH_LONG).show();
            } else {
                pos_usb = new POSSDK(interface_usb);
                //error_code = pos_usb.systemSelectPrintMode(printMode);
                sdk_flag = true;
                //Toast.makeText(mContext, "TVS printer connected.", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            return false;
        }

        return true;
    }

    private void finalizeObject() {
        try {
            if (interface_usb != null) {
                error_code = interface_usb.CloseDevice();
                if (error_code != POS_SUCCESS) {
                    Toast.makeText(mContext, "TVS printer failed to disconnect.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception ex) {
            interface_usb = null;
            pos_usb = null;
            //Toast.makeText(mContext, "TVS printer disconnected.", Toast.LENGTH_LONG).show();
        } finally {
            interface_usb = null;
            pos_usb = null;
            // Toast.makeText(mContext, "TVS printer disconnected.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean runPrintReceiptSequence(PaymentReceipt item) {
        if (!initializeObject()) {
            return false;
        }

        if (!printReceiptData(item)) {
            finalizeObject();
            return false;
        }

        finalizeObject();

        return true;
    }

    private boolean printReceiptData(PaymentReceipt item) {
        if (interface_usb == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {
            sendReceiptData(item);
        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                finalizeObject();
            } catch (Exception ex) {
                return false;
            }
            return false;
        } finally {
            try {
                finalizeObject();
            } catch (Exception ex) {
            }
        }

        return true;
    }

    private void sendReceiptData(PaymentReceipt item) {
        int iPrintStatus = -1;
        try {

            Thread.sleep(100);

            if (item.getiBillType() == 1) {
                if (item.getIsDuplicate().equals(""))
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "PAYMENT" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            ConstantDefine.FontStyleBold,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                else
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "PAYMENT" + "\n" + item.getIsDuplicate() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            ConstantDefine.FontStyleBold,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
            } else {
                if (item.getIsDuplicate().equals(""))
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "RECEIPT" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            ConstantDefine.FontStyleBold,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                else
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "RECEIPT" + "\n" + item.getIsDuplicate() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            ConstantDefine.FontStyleBold,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            if (item.getHeaderPrintBold() == 1) {
                if (item.getHeaderLine1() != null && !item.getHeaderLine1().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine1() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            ConstantDefine.FontStyleBold,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine2() != null && !item.getHeaderLine2().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine2() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            ConstantDefine.FontStyleBold,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine3() != null && !item.getHeaderLine3().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine3() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            ConstantDefine.FontStyleBold,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine4() != null && !item.getHeaderLine4().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine4() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            ConstantDefine.FontStyleBold,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine5() != null && !item.getHeaderLine5().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine5() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            ConstantDefine.FontStyleBold,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            } else {
                if (item.getHeaderLine1() != null && !item.getHeaderLine1().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine1() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine2() != null && !item.getHeaderLine2().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine2() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine3() != null && !item.getHeaderLine3().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine3() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine4() != null && !item.getHeaderLine4().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine4() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
                if (item.getHeaderLine5() != null && !item.getHeaderLine5().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine5() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_CENTER,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getiBillType() == 1) {

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Payment No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            } else {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Receipt No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Invoice Date    : " + item.getStrDate() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);


            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Description     : " + item.getDescriptionText() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Amount          : " + String.valueOf(item.getdAmount()) + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Reason          : " + item.getStrReason() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            //"==========================================" + "\n",
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestCutPaper(pos_usb, PRINT_MODE_STANDARD);

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
        printerConnectionError.onError(iPrintStatus);
    }

    //Print Sales Return
    public boolean runPrintSalesReturnSequence(SalesReturnPrintBean item) {
        if (!initializeObject()) {
            return false;
        }

        if (!printSalesReturnData(item)) {
            finalizeObject();
            return false;
        }

        finalizeObject();

        return true;
    }

    private boolean printSalesReturnData(SalesReturnPrintBean item) {
        if (interface_usb == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {
            sendSalesReturnData(item);
        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                finalizeObject();
            } catch (Exception ex) {
                return false;
            }
            return false;
        } finally {
            try {
                finalizeObject();
            } catch (Exception ex) {
            }
        }

        return true;
    }

    private void sendSalesReturnData(SalesReturnPrintBean item) {
        int iPrintStatus = -1;
        try {

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getIsDuplicate(),
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    ConstantDefine.FontStyleBold,
                    TestPrintInfo.ALIGNMENT_CENTER,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "GSTIN     : " + item.getStrOwnerGSTIN() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Name      : " + item.getStrOwnerName() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Address   : " + item.getStrOwnerAddress() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Bill no   : " + item.getStrInvoiceNo() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Bill Date : " + item.getStrInvoiceDate() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "CDNI      : " + item.getiSrId() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Note Date : " + item.getStrSalesReturnDate() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if(item.getStrCustName() != null && !item.getStrCustName().isEmpty()) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Customer Name   : " + item.getStrCustName() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        TestPrintInfo.ALIGNMENT_LEFT,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Notes     : " + item.getStrReason() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);


            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "ITEM NAME                  QTY                 AMOUNT" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_BOLD,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            ArrayList<SalesReturnBean> salesReturnItems = item.getArrayList();
            Iterator it = salesReturnItems.iterator();
            int totalitemtypes = 0;

            while (it.hasNext()) {
                //BillKotItem billKotItem = (BillKotItem) it.next();

                SalesReturnBean bean = (SalesReturnBean) it.next();

                String preName = getPostAddedSpaceFormat("", getFormatedCharacterForPrint(String.valueOf(bean.getStrItemName()), bean.getStrItemName().length(), 1), 21, 1);
                String preQty = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.valueOf(bean.getDblReturnQuantity()), 9, 1), 12, 1);
                String preAmount = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", bean.getDblReturnAmount()), 20, 1), 10, 1);
                String pre = "";
                pre = preName + preQty + preAmount;
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        TestPrintInfo.ALIGNMENT_LEFT,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
                totalitemtypes++;
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "--------------------------------------------------------" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Item(s) : " + totalitemtypes, "", 48, 1),
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if (item.getIsInterstate() == 0) // IntraState
            {
                if (it11.hasNext()) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_LEFT,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Tax(%)              CGSTAmt     SGSTAmt         TaxAmt" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            TestPrintInfo.ALIGNMENT_LEFT,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_LEFT,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    do {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if (billTaxSlabEntry.getTaxRate() > 0) {
                            String TxIndex = getPostAddedSpaceFormat("", String.valueOf(billTaxSlabEntry.getTaxIndex()) + " " +
                                    String.format("%.2f", billTaxSlabEntry.getTaxRate()), 13, 1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getCGSTAmount()), 13, 1), 9, 1);
                            String SGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getSGSTAmount()), 13, 1), 9, 1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTotalTaxAmount()), 15, 1), 10, 1);

                            String pre = TxIndex + CGSTAmt + SGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getCGSTAmount() + billTaxSlabEntry.getSGSTAmount();
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    TestPrintInfo.ALIGNMENT_LEFT,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    } while (it11.hasNext());
                }
            } else // InterState
            {
                if (it11.hasNext()) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_LEFT,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Tax(%)                      IGSTAmt               TaxAmt" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            TestPrintInfo.ALIGNMENT_LEFT,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_LEFT,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    do {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if (billTaxSlabEntry.getTaxRate() > 0) {
                            String TxIndex = getPostAddedSpaceFormat("", String.valueOf(billTaxSlabEntry.getTaxIndex()) + " " +
                                    String.format("%.2f", billTaxSlabEntry.getTaxRate()), 7, 1);
                            String IGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getIGSTAmount()), 20, 1), 9, 1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTotalTaxAmount()), 18, 1), 10, 1);

                            String pre = TxIndex + IGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getIGSTAmount();
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    TestPrintInfo.ALIGNMENT_LEFT,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    } while (it11.hasNext());
                }
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            double dtotalcessAmt = 0;
            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            Iterator it21 = billcessTaxItems.iterator();
            while (it21.hasNext()) {

                BillServiceTaxItem billKotItem = (BillServiceTaxItem) it21.next();
                if (billKotItem.getServicePercent() > 0 || billKotItem.getPricePerUnit() > 0) {

                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getServiceTxName()), 25, 1);
                    String TxPercent = "";
                    if (billKotItem.getServicePercent() > 0)
                        TxPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", billKotItem.getServicePercent()) + "% ", 15, 1);
                    else
                        TxPercent = getPostAddedSpaceFormat("", "@ Rs." + String.format("%.2f", billKotItem.getPricePerUnit()) + " ", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getServicePrice()), 14, 1), 8, 1);
                    dtotalcessAmt += billKotItem.getServicePrice();
                    String pre = TxName + TxPercent + TxValue;
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            TestPrintInfo.ALIGNMENT_LEFT,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "--------------------------------------------------------" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            double dTotalTaxAmt = dTotTaxAmt + dtotalcessAmt;
            if (dTotalTaxAmt > 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Tax Amount", String.format("%.2f", dTotalTaxAmt), 54, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        TestPrintInfo.ALIGNMENT_LEFT,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Invoice Amount", String.format("%.2f", item.getDblAmount()), 54, 1) + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Return Amount", String.format("%.2f", item.getDblReturnAmount()), 54, 1) + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    TestPrintInfo.ALIGNMENT_LEFT,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestCutPaper(pos_usb, PRINT_MODE_STANDARD);

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
        printerConnectionError.onError(iPrintStatus);
    }


    //Print KOT
    public boolean runPrintKOTSequence(PrintKotBillItem item) {
        if (!initializeObject()) {
            return false;
        }

        if (!printKotData(item)) {
            finalizeObject();
            return false;
        }

        finalizeObject();

        return true;
    }

    private void sendKotData(PrintKotBillItem item) {
        int iPrintStatus = -1;
        String tblno = "", modename = "";
        try {
            if(item.getBillingMode().equalsIgnoreCase("1")){
                tblno = "Table # "+item.getTableNo() + " | ";
                modename = item.getStrBillingModeName();
            } else {
                tblno = "";
                modename = item.getStrBillingModeName();
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, modename+"\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    ConstantDefine.FontStyleBold,
                    TestPrintInfo.ALIGNMENT_CENTER,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, tblno +"KOT # "+item.getBillNo()+"\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    ConstantDefine.FontStyleBold,
                    TestPrintInfo.ALIGNMENT_CENTER,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=============================================\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Attendant   : " + item.getOrderBy() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if ((item.getWaiterName() != null) && !(item.getWaiterName().equals("")))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Waiter : "+item.getWaiterName()+"\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Date        : " + item.getDate() + " | " + "Time : " + item.getTime() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=============================================\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SNo        NAME                    QTY" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_BOLD,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=============================================\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            while (it.hasNext()) {
                BillKotItem billKotItem = (BillKotItem) it.next();
                int id = billKotItem.getItemId();
                String name = getFormatedCharacterForPrint(billKotItem.getItemName(), 16, 1);
                String qty = billKotItem.getQty() + "";
                String pre = getPostAddedSpaceFormat("", String.valueOf(id), 10, 1) + name;
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getPreAddedSpaceFormat(pre, qty, 38, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=============================================\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestCutPaper(pos_usb, PRINT_MODE_STANDARD);

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
        printerConnectionError.onError(iPrintStatus);
    }

    private boolean printKotData(PrintKotBillItem item) {
        if (interface_usb == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {
            sendKotData(item);
        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                finalizeObject();
            } catch (Exception ex) {
                return false;
            }
            return false;
        } finally {
            try {
                finalizeObject();
            } catch (Exception ex) {
            }
        }
        return true;
    }

    //Customer deposit print
    public boolean runPrintDepositReceiptSequence(Customer item) {
        if (!initializeObject()) {
            return false;
        }

        if (!printDepositReceiptData(item)) {
            finalizeObject();
            return false;
        }

        finalizeObject();

        return true;
    }

    private boolean printDepositReceiptData(Customer item) {
        if (interface_usb == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {
            sendDepositReceiptData(item);
        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                finalizeObject();
            } catch (Exception ex) {
                return false;
            }
            return false;
        } finally {
            try {
                finalizeObject();
            } catch (Exception ex) {
            }
        }
        return true;
    }

    private void sendDepositReceiptData(Customer item) {
        int iPrintStatus = -1;
        try {

            Thread.sleep(100);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Deposit Amount Receipt" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentCenter,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getHeaderPrintBold() == 1) {
                if (item.getHeaderLine1() != null && !item.getHeaderLine1().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine1() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
                if (item.getHeaderLine2() != null && !item.getHeaderLine2().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine2() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
                if (item.getHeaderLine3() != null && !item.getHeaderLine3().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine3() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
                if (item.getHeaderLine4() != null && !item.getHeaderLine4().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine4() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
                if (item.getHeaderLine5() != null && !item.getHeaderLine5().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine5() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
            } else {
                if (item.getHeaderLine1() != null && !item.getHeaderLine1().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine1() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
                if (item.getHeaderLine2() != null && !item.getHeaderLine2().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine2() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
                if (item.getHeaderLine3() != null && !item.getHeaderLine3().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine3() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
                if (item.getHeaderLine4() != null && !item.getHeaderLine4().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine4() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
                if (item.getHeaderLine5() != null && !item.getHeaderLine5().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine5() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Deposit Date    : " + item.getBusinessDate() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Customer Name   : " + item.getStrCustName() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Mobile No.      : " + item.getStrCustPhone() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Deposit Amount  : " + item.getDblDepositAmt() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Credit Amount   : " + item.getdCreditAmount() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine1().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine1() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine2().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine2() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine3().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine3() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine4().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine4() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine5().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine5() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestCutPaper(pos_usb, PRINT_MODE_STANDARD);

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
        printerConnectionError.onError(iPrintStatus);
    }

    //Sales report printing
    public boolean runPrintReportSequence(List<List<String>> Report, String ReportName, String type) {
        if (!initializeObject()) {
            return false;
        }

        if (!printReportData(Report, ReportName, type)) {
            finalizeObject();
            return false;
        }

        finalizeObject();

        return true;
    }

    public boolean printReportData(List<List<String>> Report, String ReportName, String type) {
        if (interface_usb == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {

            if (ReportName.contains("Cumulative")) {
                sendCumulativeReportData(Report, ReportName, type);
            } else {
                sendReportData(Report, ReportName, type);
            }
        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                finalizeObject();
            } catch (Exception ex) {
                // Do nothing
                return false;
            }
            return false;
        } finally {
            try {
                finalizeObject();
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    private void sendCumulativeReportData(List<List<String>> itemReport, String reportName, String type) {
        int iPrintStatus = -1;
        try {

            Thread.sleep(100);
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, reportName + "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentCenter,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=======================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            Calendar time = Calendar.getInstance();
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "[" + String.format("%tR", time) + "]\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            for (int j = 0; j < itemReport.size(); j++) {
                if (j == itemReport.size() - 1) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=======================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                List<String> arrayListColumn = itemReport.get(j);
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < arrayListColumn.size(); i++) {
                    String str = arrayListColumn.get(i);
                    String preTxt = getAbsoluteCharacter(String.valueOf(str), 10, 1);
                    if (j == 0 || j == 1)
                        sb.append(preTxt + " |");
                    else
                        sb.append(" " + preTxt + " ");
                    int rem = i % 3;
                    if (rem == 0 && i != 0) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, sb.toString() + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                        sb = new StringBuffer();
                    }

                    if (rem != 0 && (arrayListColumn.size() - 1) == i) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getAbsoluteCharacter1(sb.toString(), 1) + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                        sb = new StringBuffer();
                    }
                }
                if (j == 1)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=======================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=======================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestCutPaper(pos_usb, PRINT_MODE_STANDARD);
        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
        printerConnectionError.onError(iPrintStatus);
    }

    private void sendReportData(List<List<String>> itemReport, String reportName, String type) {
        int iPrintStatus = -1;
        try {

            Thread.sleep(100);
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, reportName + "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentCenter,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=======================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            Calendar time = Calendar.getInstance();
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "[" + String.format("%tR", time) + "]\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            for (int j = 0; j < itemReport.size(); j++) {
                List<String> arrayListColumn = itemReport.get(j);
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < arrayListColumn.size(); i++) {
                    String str = arrayListColumn.get(i);
                    String preTxt = getAbsoluteCharacter(String.valueOf(str), 10, 1);
                    if (j == 0)
                        sb.append(preTxt + " |");
                    else
                        sb.append(" " + preTxt + " ");
                    int rem = i % 3;
                    if (rem == 0 && i != 0) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, sb.toString() + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                        sb = new StringBuffer();
                    }

                    if (rem != 0 && (arrayListColumn.size() - 1) == i) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getAbsoluteCharacter1(sb.toString(), 1) + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                        sb = new StringBuffer();
                    }
                }
                if (j == 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=======================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "=======================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestCutPaper(pos_usb, PRINT_MODE_STANDARD);

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
        printerConnectionError.onError(iPrintStatus);
    }

    //Bill printing
    public boolean runPrintBillSequence(PrintKotBillItem item, int printCount) {
        if (!initializeObject()) {
            return false;
        }

        if (!printBillData(item, printCount)) {
            finalizeObject();
            return false;
        }

        finalizeObject();

        return true;
    }

    private boolean printBillData(PrintKotBillItem item, int printCount) {
        if (interface_usb == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }
        try {

            if (printCount == 1) {
                sendBillData(item);
                //sendBillDataNormal(item);
            } else {
                for (int i = 0; i < printCount; i++) {
                    sendBillData(item);
                    //sendBillDataNormal(item);
                }
            }
        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                finalizeObject();
            } catch (Exception ex) {
                // Do nothing
                return false;
            }
            return false;
        } finally {
            try {
                finalizeObject();
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    private void sendBillDataNormal(PrintKotBillItem item) {
        int iPrintStatus = -1;
        try {

            if (item.getCompanyLogoPath() != null
                    && !item.getCompanyLogoPath().isEmpty()
                    && !item.getCompanyLogoPath().equalsIgnoreCase("1234567890")) {

                iPrintStatus = testprint.TestPrintBitmap(pos_usb, TestPrintInfo.POS_FONT_TYPE_STANDARD, item.getCompanyLogoPath(), 1);

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            Thread.sleep(100);

          /*  if(item.getIsDuplicate().equals(""))
                mBixolonPrinter.printText("TAX INVOICE"+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            else
                mBixolonPrinter.printText("TAX INVOICE"+"\n"+item.getIsDuplicate()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);*/

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getIsDuplicate(),
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentCenter,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getBoldHeader() == 1) {
                if (item.getHeaderLine1() != null && !item.getHeaderLine1().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine1() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine2() != null && !item.getHeaderLine2().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine2() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine3() != null && !item.getHeaderLine3().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine3() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine4() != null && !item.getHeaderLine4().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine4() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine5() != null && !item.getHeaderLine5().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine5() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            } else {
                if (item.getHeaderLine1() != null && !item.getHeaderLine1().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine1() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine2() != null && !item.getHeaderLine2().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine2() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine3() != null && !item.getHeaderLine3().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine3() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine4() != null && !item.getHeaderLine4().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine4() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine5() != null && !item.getHeaderLine5().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine5() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            }

            if (item.getOwnerDetail() == 1) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "GSTIN   : " + item.getAddressLine1() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Name    : " + item.getAddressLine2() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Address : " + item.getAddressLine3() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Bill no       : " + item.getBillNo() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getBillingMode().equals("1"))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Table         : " + item.getTableNo() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Date          : " + item.getDate() + " Time : " + item.getTime() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Cashier       : " + item.getOrderBy() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);


            if (!item.getCustomerName().equals("") && !item.getCustomerName().contains("-")) {
                String[] lines = item.getCustomerName().split("\\r?\\n");
                StringBuilder sb = new StringBuilder("");
                for (int k = 0; k < lines.length; k++) {
                    sb.append(lines[k]);
                    if (k == 0) {
                        sb.append("\n\n");
                    }
                }
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Customer Name : " + sb.toString() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }
            if (!item.getSalesManId().equals("") && !item.getSalesManId().contains("-")) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Salesman Id   : " + item.getSalesManId() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            if (item.getBillingMode().equalsIgnoreCase("4") || item.getBillingMode().equalsIgnoreCase("3")) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Payment Status : " + item.getPaymentStatus() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            if (item.getPrintService() == 1) {
                if (item.getBillingMode().equalsIgnoreCase("1") || item.getBillingMode().equalsIgnoreCase("2") ||
                        item.getBillingMode().equalsIgnoreCase("3") || item.getBillingMode().equalsIgnoreCase("4")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Service       : " + item.getStrBillingModeName() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                } else {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "-----------" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getAmountInNextLine() == 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE       AMOUNT " + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_BOLD,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getHSNPrintEnabled_out() == 1) {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "HSN       DISC" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    } else {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "HSN" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                } else {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "          DISC" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                }
            } else {
                if (item.getHSNPrintEnabled_out() == 1) {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE              ",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "HSN       DISC                                  AMOUNT " + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    } else {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE              ",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "HSN                                             AMOUNT " + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                } else {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE               ",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "          DIS        C                           AMOUNT" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                    } else {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE               ",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "                                                 AMOUNT" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                }
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            int totalitemtypes = 0;
            double totalquantitycount = 0;
            double subtotal = 0;

            while (it.hasNext()) {
                BillKotItem billKotItem = (BillKotItem) it.next();

                String preId = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getItemId()), 3, 1);
                String preName = getPostAddedSpaceFormat("", getFormatedCharacterForPrint(String.valueOf(billKotItem.getItemName()), billKotItem.getItemName().length(), 1), 17, 1);
                String HSN = getPostAddedSpaceFormat("", getFormatedCharacterForPrint(String.valueOf(billKotItem.getHSNCode()), 10, 1), 10, 1);
                String itemWiseDisc = getPostAddedSpaceFormat("", getFormatedCharacterForPrint(String.valueOf(billKotItem.getDiscount()), 10, 1), 10, 1);

                String preQty = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.valueOf(billKotItem.getQty()) + billKotItem.getUOM(), 11, 1), 13, 1);
                String preRate = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getRate()), 11, 1), 11, 1);
                String preAmount = "0";
                String pre = "";
                //if(item.getAmountInNextLine() ==0)
                if (String.format("%.2f", billKotItem.getAmount()).length() <= 9) {
                    preAmount = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getAmount())
                            + billKotItem.getTaxIndex(), 11, 1), 10, 1);
                    if (billKotItem.getItemName().length() > 16) {
                        pre = preId + preName.substring(0, 16) + " " +/*HSN+*/preQty + preRate + preAmount;
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                        if (preName.length() > 32) {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "   " + preName.substring(16, 32) + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        } else {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "   " + preName.substring(16, preName.length()) + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }

                    } else {
                        pre = preId + preName +/*HSN+*/preQty + preRate + preAmount;
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                    if (item.getHSNPrintEnabled_out() == 1) {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, HSN + itemWiseDisc + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        } else {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, HSN + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    } else {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getFormatedCharacterForPrint_init(itemWiseDisc, 20, 1) + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    }
                } else // item.getAmountInNextLine() ==1
                {
                    preAmount = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getAmount())
                            + billKotItem.getTaxIndex(), (String.format("%.2f", billKotItem.getAmount()) + billKotItem.getTaxIndex()).length(), 1), (String.format("%.2f", billKotItem.getAmount()) + billKotItem.getTaxIndex()).length(), 1);
                    String pre2 = "", pre3 = "", pre4 = "";
                    if (item.getHSNPrintEnabled_out() == 1) {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            pre2 = getPostAddedSpaceFormat("", HSN, 8, 1);
                            pre4 = getPostAddedSpaceFormat("", itemWiseDisc, 8, 1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount, 28, 1);
                        } else {
                            pre2 = getPostAddedSpaceFormat("", HSN, 8, 1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount, 37, 1);
                        }
                    } else {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            pre4 = getFormatedCharacterForPrint_init(itemWiseDisc, 18, 1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount, 30, 1);
                        } else {
                            pre3 = getFormatedCharacterForPrint_init(preAmount, 48, 1);
                        }
                    }
                    if (billKotItem.getItemName().length() > 16) {
                        if (preName.length() > 32) {
                            pre = preId + preName.substring(0, 16) + " " +/*HSN+*/preQty + preRate + "\n" + "   " + preName.substring(16, 32) + "\n" + pre2 + pre4 + pre3;
                        } else {
                            pre = preId + preName.substring(0, 16) + " " +/*HSN+*/preQty + preRate + "\n" + "   " + preName.substring(16, preName.length()) + "\n" + pre2 + pre4 + pre3;
                        }
                    } else {
                        pre = preId + preName +/*HSN+*/preQty + preRate + "\n" + pre2 + pre4 + pre3;
                    }
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }

                totalitemtypes++;
                totalquantitycount += billKotItem.getQty();
                subtotal += billKotItem.getAmount();
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "--------------------------------------------------------" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Item(s) : " + totalitemtypes + " /Qty : " + totalquantitycount, String.format("%.2f", subtotal), 56, 1) + "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            double discount = item.getFdiscount();
            float discountPercentage = item.getdiscountPercentage();
            if (discountPercentage > 0) {
               /* mBixolonPrinter.printText("\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);*/
                String DiscName = getPostAddedSpaceFormat("", "Discount Amount", 23, 1);
                String DiscPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", discountPercentage) + " %", 23, 1);
                String DiscValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", discount), 18, 1), 8, 1);
                String pre = DiscName + DiscPercent + DiscValue;
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            } else if (discount > 0) {
               /* mBixolonPrinter.printText("\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);*/
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Discount Amount", String.format("%.2f", discount), 56, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            ArrayList<BillTaxItem> billOtherChargesItems = item.getBillOtherChargesItems();
            if (billOtherChargesItems.size() > 0) {
                Iterator it1 = billOtherChargesItems.iterator();
                while (it1.hasNext()) {
                    BillTaxItem billKotItem = (BillTaxItem) it1.next();
                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getTxName()), 23, 1);
                    String TxPercent = getPostAddedSpaceFormat("", "", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getPrice()), 18, 1), 8, 1);
                    String pre = TxName + TxPercent + TxValue;
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            }

            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if (item.getIsInterState().equalsIgnoreCase("n")) // IntraState
            {
                if (it11.hasNext()) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    if (item.getUTGSTEnabled() == 0) // disabled
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Tax(%)     TaxableVal     CGSTAmt    SGSTAmt      TaxAmt" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    else
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Tax(%)     TaxableVal     CGSTAmt    UTGSTAmt     TaxAmt" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    do {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if (billTaxSlabEntry.getTaxRate() > 0) {
                            String TxIndex = getPostAddedSpaceFormat("", String.valueOf(billTaxSlabEntry.getTaxIndex()) + " " +
                                    String.format("%.2f", billTaxSlabEntry.getTaxRate()), 7, 1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()), 14, 1), 15, 1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getCGSTAmount()), 10, 1), 11, 1);
                            String SGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getSGSTAmount()), 10, 1), 11, 1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTotalTaxAmount()), 12, 1), 10, 1);

                            String pre = TxIndex + TaxableValue + CGSTAmt + SGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getCGSTAmount() + billTaxSlabEntry.getSGSTAmount();
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    } while (it11.hasNext());
                }
            } else // InterState
            {
                if (it11.hasNext()) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Tax(%)     TaxableVal       IGSTAmt               TaxAmt" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    do {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if (billTaxSlabEntry.getTaxRate() > 0) {
                            String TxIndex = getPostAddedSpaceFormat("", String.valueOf(billTaxSlabEntry.getTaxIndex()) + " " +
                                    String.format("%.2f", billTaxSlabEntry.getTaxRate()), 7, 1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()), 14, 1), 15, 1);
                            String IGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getIGSTAmount()), 12, 1), 11, 1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init("", 8, 1), 9, 1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTotalTaxAmount()), 12, 1), 10, 1);

                            String pre = TxIndex + TaxableValue + IGSTAmt + CGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getIGSTAmount();
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    } while (it11.hasNext());
                }
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            double dtotalcessAmt = 0;
            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            Iterator it21 = billcessTaxItems.iterator();
            while (it21.hasNext()) {

                BillServiceTaxItem billKotItem = (BillServiceTaxItem) it21.next();
                if (billKotItem.getServicePercent() > 0 || billKotItem.getServicePrice() > 0) {

                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getServiceTxName()), 27, 1);
                    String TxPercent = "";
                    if (billKotItem.getServicePercent() > 0)
                        TxPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", billKotItem.getServicePercent()) + "% ", 15, 1);
                    else
                        TxPercent = getPostAddedSpaceFormat("", "@ Rs." + String.format("%.2f", billKotItem.getPricePerUnit()) + " ", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getServicePrice()), 13, 1), 8, 1);
                    dtotalcessAmt += billKotItem.getServicePrice();
                    String pre = TxName + TxPercent + TxValue;
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
            }

            double dTotalTaxAmt = dTotTaxAmt + dtotalcessAmt;
            if (dTotalTaxAmt > 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Tax Amount", String.format("%.2f", dTotalTaxAmt), 56, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("TOTAL", String.format("%.2f", item.getNetTotal()), 56, 1) + "\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getCardPaymentValue() > 0 || item.geteWalletPaymentValue() > 0 ||
                    item.getCouponPaymentValue() > 0 || item.getPettyCashPaymentValue() > 0 || item.getRewardPoints() > 0 || item.getCashPaymentValue() > 0
                    || item.getAepsPaymentValue() > 0 || item.getDblMSwipeVale() > 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getCardPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("OtherCard Payment", String.format("%.2f", item.getCardPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                if (item.geteWalletPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("eWallet Payment", String.format("%.2f", item.geteWalletPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getCouponPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Coupon Payment", String.format("%.2f", item.getCouponPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getPettyCashPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("PettyCash Payment", String.format("%.2f", item.getPettyCashPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getCashPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Cash Payment", String.format("%.2f", item.getCashPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getRewardPoints() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Reward Pt Payment", String.format("%.2f", item.getRewardPoints()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getAepsPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("AEPS Payment", String.format("%.2f", item.getAepsPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                if (item.getDblMSwipeVale() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("MSwipe Payment", String.format("%.2f", item.getDblMSwipeVale()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getDblPaytmValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Paytm Payment", String.format("%.2f", item.getDblPaytmValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

            }

            if (item.getChangePaymentValue() > 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "--------------------------------------------------------" + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Due amount", String.format("%.2f", item.getChangePaymentValue()), 56, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getRoundOff() > 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Roundoff to 1.00 ", "", 56, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            if (!item.getFooterLine1().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine1() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine2().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine2() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine3().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine3() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine4().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine4() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine5().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine5() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_STANDARD,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestCutPaper(pos_usb, PRINT_MODE_STANDARD);

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
        printerConnectionError.onError(iPrintStatus);
    }



    private void sendBillData(PrintKotBillItem item) {
        int iPrintStatus = -1;
        try {

            if (item.getCompanyLogoPath() != null
                    && !item.getCompanyLogoPath().isEmpty()
                    && !item.getCompanyLogoPath().equalsIgnoreCase("1234567890")) {

                iPrintStatus = testprint.TestPrintBitmap(pos_usb, TestPrintInfo.POS_FONT_TYPE_STANDARD, item.getCompanyLogoPath(), 0);  //Img_type 0:Ram 1:flash

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            Thread.sleep(100);

          /*  if(item.getIsDuplicate().equals(""))
                mBixolonPrinter.printText("TAX INVOICE"+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            else
                mBixolonPrinter.printText("TAX INVOICE"+"\n"+item.getIsDuplicate()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);*/

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD,  "TAX INVOICE"+item.getIsDuplicate()+"\n",
                    TestPrintInfo.POS_FONT_TYPE_STANDARD,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentCenter,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getBoldHeader() == 1) {
                if (item.getHeaderLine1() != null && !item.getHeaderLine1().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine1() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine2() != null && !item.getHeaderLine2().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine2() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine3() != null && !item.getHeaderLine3().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine3() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine4() != null && !item.getHeaderLine4().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine4() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine5() != null && !item.getHeaderLine5().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine5() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            } else {
                if (item.getHeaderLine1() != null && !item.getHeaderLine1().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine1() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine2() != null && !item.getHeaderLine2().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine2() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine3() != null && !item.getHeaderLine3().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine3() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine4() != null && !item.getHeaderLine4().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine4() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
                if (item.getHeaderLine5() != null && !item.getHeaderLine5().equals("")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getHeaderLine5() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_STANDARD,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentCenter,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            }

            if (item.getOwnerDetail() == 1) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "GSTIN     : " + item.getAddressLine1() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Name      : " + item.getAddressLine2() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Address   : " + item.getAddressLine3() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Bill no         : " + item.getBillNo() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getBillingMode().equals("1"))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Table           : " + item.getTableNo() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Date            : " + item.getDate() + "    Time : " + item.getTime() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Cashier         : " + item.getOrderBy() + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);


            if (!item.getCustomerName().equals("") && !item.getCustomerName().contains("-")) {
                String[] lines = item.getCustomerName().split("\\r?\\n");
                StringBuilder sb = new StringBuilder("");
                for (int k = 0; k < lines.length; k++) {
                    sb.append(lines[k]);
                    if (k == 0) {
                        sb.append("\n\n");
                    }
                }
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Customer Name   : " + sb.toString() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }
            if (!item.getSalesManId().equals("") && !item.getSalesManId().contains("-")) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Salesman Id     : " + item.getSalesManId() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            if (item.getBillingMode().equalsIgnoreCase("4") || item.getBillingMode().equalsIgnoreCase("3")) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Payment Status  : " + item.getPaymentStatus() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            if (item.getPrintService() == 1) {
                if (item.getBillingMode().equalsIgnoreCase("1") || item.getBillingMode().equalsIgnoreCase("2") ||
                        item.getBillingMode().equalsIgnoreCase("3") || item.getBillingMode().equalsIgnoreCase("4")) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Service         : " + item.getStrBillingModeName() + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                } else {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "-----------" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getAmountInNextLine() == 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE       AMOUNT " + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_BOLD,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getHSNPrintEnabled_out() == 1) {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "HSN       DISC" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    } else {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "HSN" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                } else {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "          DISC" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                }
            } else {
                if (item.getHSNPrintEnabled_out() == 1) {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE              ",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "HSN       DISC                                  AMOUNT " + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    } else {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE              ",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "HSN                                             AMOUNT " + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                } else {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE               ",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "          DIS        C                           AMOUNT" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                    } else {
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "SI  ITEM NAME              QTY        RATE               ",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "                                                 AMOUNT" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                }
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            int totalitemtypes = 0;
            double totalquantitycount = 0;
            double subtotal = 0;

            while (it.hasNext()) {
                BillKotItem billKotItem = (BillKotItem) it.next();

                String preId = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getItemId()), 3, 1);
                String preName = getPostAddedSpaceFormat("", getFormatedCharacterForPrint(String.valueOf(billKotItem.getItemName()), billKotItem.getItemName().length(), 1), 17, 1);
                String HSN = getPostAddedSpaceFormat("", getFormatedCharacterForPrint(String.valueOf(billKotItem.getHSNCode()), 10, 1), 10, 1);
                String itemWiseDisc = getPostAddedSpaceFormat("", getFormatedCharacterForPrint(String.valueOf(billKotItem.getDiscount()), 10, 1), 10, 1);

                String preQty = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.valueOf(billKotItem.getQty()) + billKotItem.getUOM(), 11, 1), 13, 1);
                String preRate = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getRate()), 10, 1), 11, 1);
                String preAmount = "0";
                String pre = "";
                //if(item.getAmountInNextLine() ==0)
                if (String.format("%.2f", billKotItem.getAmount()).length() <= 9) {
                    preAmount = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getAmount())
                            + billKotItem.getTaxIndex(), 11, 1), 10, 1);
                    if (billKotItem.getItemName().length() > 16) {
                        pre = preId + preName.substring(0, 16) + " " +/*HSN+*/preQty + preRate + preAmount;
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                        if (preName.length() > 32) {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "   " + preName.substring(16, 32) + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        } else {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "   " + preName.substring(16, preName.length()) + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }

                    } else {
                        pre = preId + preName +/*HSN+*/preQty + preRate + preAmount;
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    }
                    if (item.getHSNPrintEnabled_out() == 1) {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, HSN + itemWiseDisc + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        } else {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, HSN + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    } else {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getFormatedCharacterForPrint_init(itemWiseDisc, 20, 1) + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    }
                } else // item.getAmountInNextLine() ==1
                {
                    preAmount = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getAmount())
                            + billKotItem.getTaxIndex(), (String.format("%.2f", billKotItem.getAmount()) + billKotItem.getTaxIndex()).length(), 1), (String.format("%.2f", billKotItem.getAmount()) + billKotItem.getTaxIndex()).length(), 1);
                    String pre2 = "", pre3 = "", pre4 = "";
                    if (item.getHSNPrintEnabled_out() == 1) {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            pre2 = getPostAddedSpaceFormat("", HSN, 8, 1);
                            pre4 = getPostAddedSpaceFormat("", itemWiseDisc, 8, 1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount, 28, 1);
                        } else {
                            pre2 = getPostAddedSpaceFormat("", HSN, 8, 1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount, 37, 1);
                        }
                    } else {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            pre4 = getFormatedCharacterForPrint_init(itemWiseDisc, 18, 1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount, 30, 1);
                        } else {
                            pre3 = getFormatedCharacterForPrint_init(preAmount, 48, 1);
                        }
                    }
                    if (billKotItem.getItemName().length() > 16) {
                        if (preName.length() > 32) {
                            pre = preId + preName.substring(0, 16) + " " +/*HSN+*/preQty + preRate + "\n" + "   " + preName.substring(16, 32) + "\n" + pre2 + pre4 + pre3;
                        } else {
                            pre = preId + preName.substring(0, 16) + " " +/*HSN+*/preQty + preRate + "\n" + "   " + preName.substring(16, preName.length()) + "\n" + pre2 + pre4 + pre3;
                        }
                    } else {
                        pre = preId + preName +/*HSN+*/preQty + preRate + "\n" + pre2 + pre4 + pre3;
                    }
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }

                totalitemtypes++;
                totalquantitycount += billKotItem.getQty();
                subtotal += billKotItem.getAmount();
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "--------------------------------------------------------" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Item(s) : " + totalitemtypes + " /Qty : " + totalquantitycount, String.format("%.2f", subtotal), 56, 1) + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            double discount = item.getFdiscount();
            float discountPercentage = item.getdiscountPercentage();
            if (discountPercentage > 0) {
               /* mBixolonPrinter.printText("\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);*/
                String DiscName = getPostAddedSpaceFormat("", "Discount Amount", 23, 1);
                String DiscPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", discountPercentage) + " %", 23, 1);
                String DiscValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", discount), 18, 1), 8, 1);
                String pre = DiscName + DiscPercent + DiscValue;
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            } else if (discount > 0) {
               /* mBixolonPrinter.printText("\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);*/
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Discount Amount", String.format("%.2f", discount), 56, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            ArrayList<BillTaxItem> billOtherChargesItems = item.getBillOtherChargesItems();
            if (billOtherChargesItems.size() > 0) {
                Iterator it1 = billOtherChargesItems.iterator();
                while (it1.hasNext()) {
                    BillTaxItem billKotItem = (BillTaxItem) it1.next();
                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getTxName()), 23, 1);
                    String TxPercent = getPostAddedSpaceFormat("", "", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getPrice()), 18, 1), 8, 1);
                    String pre = TxName + TxPercent + TxValue;
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                }
            }

            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if (item.getIsInterState().equalsIgnoreCase("n")) // IntraState
            {
                if (it11.hasNext()) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    if (item.getUTGSTEnabled() == 0) // disabled
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Tax(%)     TaxableVal     CGSTAmt    SGSTAmt      TaxAmt" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);
                    else
                        iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Tax(%)     TaxableVal     CGSTAmt    UTGSTAmt     TaxAmt" + "\n",
                                TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                TestPrintInfo.POS_FONT_STYLE_BOLD,
                                ConstantDefine.TextAlignmentLeft,
                                0, 0,
                                PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    do {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if (billTaxSlabEntry.getTaxRate() > 0) {
                            String TxIndex = getPostAddedSpaceFormat("", String.valueOf(billTaxSlabEntry.getTaxIndex()) + " " +
                                    String.format("%.2f", billTaxSlabEntry.getTaxRate()), 7, 1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()), 14, 1), 15, 1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getCGSTAmount()), 10, 1), 11, 1);
                            String SGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getSGSTAmount()), 10, 1), 11, 1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTotalTaxAmount()), 12, 1), 10, 1);

                            String pre = TxIndex + TaxableValue + CGSTAmt + SGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getCGSTAmount() + billTaxSlabEntry.getSGSTAmount();
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    } while (it11.hasNext());
                }
            } else // InterState
            {
                if (it11.hasNext()) {
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "Tax(%)     TaxableVal       IGSTAmt               TaxAmt" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_BOLD,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                    do {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if (billTaxSlabEntry.getTaxRate() > 0) {
                            String TxIndex = getPostAddedSpaceFormat("", String.valueOf(billTaxSlabEntry.getTaxIndex()) + " " +
                                    String.format("%.2f", billTaxSlabEntry.getTaxRate()), 7, 1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()), 14, 1), 15, 1);
                            String IGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getIGSTAmount()), 12, 1), 11, 1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init("", 8, 1), 9, 1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTotalTaxAmount()), 12, 1), 10, 1);

                            String pre = TxIndex + TaxableValue + IGSTAmt + CGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getIGSTAmount();
                            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                                    ConstantDefine.TextAlignmentLeft,
                                    0, 0,
                                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                                    TestPrintInfo.TEXT_VERTICAL_TIME1);
                        }
                    } while (it11.hasNext());
                }
            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            double dtotalcessAmt = 0;
            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            Iterator it21 = billcessTaxItems.iterator();
            while (it21.hasNext()) {

                BillServiceTaxItem billKotItem = (BillServiceTaxItem) it21.next();
                if (billKotItem.getServicePercent() > 0 || billKotItem.getServicePrice() > 0) {

                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getServiceTxName()), 27, 1);
                    String TxPercent = "";
                    if (billKotItem.getServicePercent() > 0)
                        TxPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", billKotItem.getServicePercent()) + "% ", 15, 1);
                    else
                        TxPercent = getPostAddedSpaceFormat("", "@ Rs." + String.format("%.2f", billKotItem.getPricePerUnit()) + " ", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getServicePrice()), 14, 1), 8, 1);
                    dtotalcessAmt += billKotItem.getServicePrice();
                    String pre = TxName + TxPercent + TxValue;
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, pre + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                }
            }

            double dTotalTaxAmt = dTotTaxAmt + dtotalcessAmt;
            if (dTotalTaxAmt > 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Tax Amount", String.format("%.2f", dTotalTaxAmt), 56, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }
            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("TOTAL", String.format("%.2f", item.getNetTotal()), 56, 1) + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getCardPaymentValue() > 0 || item.geteWalletPaymentValue() > 0 ||
                    item.getCouponPaymentValue() > 0 || item.getPettyCashPaymentValue() > 0 || item.getRewardPoints() > 0 || item.getCashPaymentValue() > 0
                    || item.getAepsPaymentValue() > 0 || item.getDblMSwipeVale() > 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getCardPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("OtherCard Payment", String.format("%.2f", item.getCardPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                if (item.geteWalletPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("eWallet Payment", String.format("%.2f", item.geteWalletPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getCouponPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Coupon Payment", String.format("%.2f", item.getCouponPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getPettyCashPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("PettyCash Payment", String.format("%.2f", item.getPettyCashPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getCashPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Cash Payment", String.format("%.2f", item.getCashPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getRewardPoints() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Reward Pt Payment", String.format("%.2f", item.getRewardPoints()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getAepsPaymentValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("AEPS Payment", String.format("%.2f", item.getAepsPaymentValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);
                if (item.getDblMSwipeVale() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("MSwipe Payment", String.format("%.2f", item.getDblMSwipeVale()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

                if (item.getDblPaytmValue() > 0)
                    iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Paytm Payment", String.format("%.2f", item.getDblPaytmValue()), 56, 1) + "\n",
                            TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                            TestPrintInfo.POS_FONT_STYLE_NORMAL,
                            ConstantDefine.TextAlignmentLeft,
                            0, 0,
                            PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                            TestPrintInfo.TEXT_VERTICAL_TIME1);

            }

            if (item.getChangePaymentValue() > 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "--------------------------------------------------------" + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Due amount", String.format("%.2f", item.getChangePaymentValue()), 56, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            }

            iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                    TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                    TestPrintInfo.POS_FONT_STYLE_NORMAL,
                    ConstantDefine.TextAlignmentLeft,
                    0, 0,
                    PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                    TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (item.getRoundOff() > 0) {
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, getSpaceFormater("Total Roundoff to 1.00 ", "", 56, 1) + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, "========================================================" + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentLeft,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);
            }

            if (!item.getFooterLine1().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine1() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine2().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine2() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine3().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine3() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine4().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine4() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            if (!item.getFooterLine5().equals(""))
                iPrintStatus = testprint.TestPrintText(pos_usb, PRINT_MODE_STANDARD, item.getFooterLine5() + "\n",
                        TestPrintInfo.POS_FONT_TYPE_COMPRESSED,
                        TestPrintInfo.POS_FONT_STYLE_NORMAL,
                        ConstantDefine.TextAlignmentCenter,
                        0, 0,
                        PRINTER_LINE_HEIGHT, TestPrintInfo.TEXT_HORIZONTAL_TIME1,
                        TestPrintInfo.TEXT_VERTICAL_TIME1);

            iPrintStatus = testprint.TestCutPaper(pos_usb, PRINT_MODE_STANDARD);

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
        printerConnectionError.onError(iPrintStatus);
    }


    @Override
    public void onHomePressed() {
        //initializeObject();
    }

    public void mTVSPrinterStatus(int iPrintError) {
        if (iPrintError != -1) {
            switch (iPrintError) {
                case POS_SUCCESS:
                    byte[] posStateTemp = new byte[1];
                    iPrintError = testprint.POSNETQueryStatus(pos_usb, posStateTemp);
                    if (iPrintError == POS_SUCCESS) {
                        byte btemp[] = new byte[8];
                        byte bitindex = 1;
                        for (int i = 0; i < btemp.length; i++) {
                            btemp[i] = (byte) (posStateTemp[0] & bitindex);
                            bitindex = (byte) (bitindex << 1);

                        }
                   /* if (btemp[0] == 1) {	//CashDrawer Open
                        Toast.makeText(mContext, "CashDrawer Open",
                                Toast.LENGTH_LONG).show();
                    } else*/
                        if (btemp[1] == 1) {    //Offline
                            Toast.makeText(mContext, "Offline",
                                    Toast.LENGTH_LONG).show();
                        } else if (btemp[2] == 1) {    //Cover Open
                            Toast.makeText(mContext, "Cover Open",
                                    Toast.LENGTH_LONG).show();
                        } else if (btemp[3] == 1) {    //Feeding
                            Toast.makeText(mContext, "Feeding",
                                    Toast.LENGTH_LONG).show();
                        } else if (btemp[4] == 1) {    //Printer Error
                            Toast.makeText(mContext, "Printer Error",
                                    Toast.LENGTH_LONG).show();
                        } else if (btemp[5] == 1) {    //Cutter Error
                            Toast.makeText(mContext, "Cutter Error",
                                    Toast.LENGTH_LONG).show();
                        } else if (btemp[6] == 1) {    //Paper	Near End
                            Toast.makeText(mContext, "Paper Near End",
                                    Toast.LENGTH_LONG).show();
                        } else if (btemp[7] == 1) {    //Paper End
                            Toast.makeText(mContext, "Paper End",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "Printed.",
                                    Toast.LENGTH_LONG).show();
                        }
                        btemp = null;
                    } else {
                        Toast.makeText(mContext, "Unable to print data.Please try again later.",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case ERR_SYSTEM_SELECT_PRINT_MODE:
                    break;
                case ERR_SYSTEM_SELECT_PAPER_TYPE:
                    break;
                case ERR_SYSTEM_SET_MOTION_UNIT:
                    break;
                case ERR_SYSTEM_QUERY_STATUS:
                    break;
                case ERR_SYSTEM_FEED_LINE:
                    break;
                case ERR_SYSTEM_CUT_PAPER:
                    break;
                case ERR_CASH_DRAWER_OPEN:
                    break;
                case ERR_TEXT_SELECT_CHAR_SET:
                    break;
                case ERR_TEXT_SELECT_CODE_PAGE:
                    break;
                case ERR_TEXT_SET_LINE_HEIGHT:
                    break;
                case ERR_TEXT_SET_CHARACTER_SPACE:
                    break;
                case ERR_TEXT_STANDARD_MODE_ALIGNMENT:
                    break;
                case ERR_TEXT_SELECT_FONT_TYPE:
                    break;
                case ERR_TEXT_SET_FONT_STYLE_REVERSE:
                    break;
                case ERR_TEXT_SET_FONT_STYLE_SMOOTH:
                    break;
                case ERR_TEXT_SET_FONT_STYLE_BOLD:
                    break;
                case ERR_TEXT_SET_FONT_STYLE_UNDERLINE:
                    break;
                case ERR_TEXT_STANDARD_MODE_UPSIDEDOWN:
                    break;
                case ERR_TEXT_SELECT_MAGNIFY_TIMES:
                    break;
                case ERR_TEXT_STANDARD_MODE_ROTATE:
                    break;
                case ERR_TEXT_ENTER_QUIT_COLOR_PRINT:
                    break;
                case ERR_TEXT_SET_COLOR_PRINT:
                    break;
                case ERR_TEXT_FONT_USER_DEFINED_ENABLE:
                    break;
                case ERR_TEXT_FONT_USER_DEFINED:
                    break;
                case ERR_TEXT_FONT_USER_DEFINED_CANCEL:
                    break;
                case ERR_IMAGE_DOWNLOAD_AND_PRINT:
                    break;
                case ERR_IMAGE_DOWNLOAD_RAM:
                    break;
                case ERR_IMAGE_RAM_PRINT:
                    break;
                case ERR_IMAGE_DOWNLOAD_FLASH:
                    break;
                case ERR_IMAGE_FLASH_PRINT:
                    break;
                case ERR_IMAGE_STANDARD_MODE_RASTER_PRINT:
                    break;
                default:
                    break;
            }
        }
    }

    private String getSpaceFormater(String txtPre, String txtPost, int num, int type) {
        return txtPre + getSpaces(num - (txtPre.length() + txtPost.length()), type) + txtPost;
    }

    private String getFormatedCharacterForPrint(String txt, int limit, int type) {
        if (txt.length() < limit) {
            return txt + getSpaces(limit - txt.length(), type);
        } else {
            return txt.substring(0, limit);
        }
    }

    private String getFormatedCharacterForPrint_init(String txt, int limit, int type) {
        if (txt.length() < limit) {
            return getSpaces(limit - txt.length(), type) + txt;
        } else {
            return txt.substring(0, limit);
        }
    }

    public String getPostAddedSpaceFormat(String sourceTxt, String toAddTxt, int max, int type) {
        return sourceTxt + toAddTxt + getSpaces(max - (sourceTxt.length() + toAddTxt.length()), type);
    }

    public String getSpaces(int num, int type) {
        StringBuffer sb = new StringBuffer();
        if (type == 0) {
            for (int i = 0; i < num; i++) {
                sb.append(mContext.getResources().getString(R.string.superSpace));
            }
        } else {
            for (int i = 0; i < num; i++) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public String getAbsoluteCharacter(String str, int num, int type) {
        String strToDo = "";
        if (str.length() > num) {
            strToDo = str.substring(0, num);
        } else {
            strToDo = str;
        }
        String preTxt = getPostAddedSpaceFormat("", String.valueOf(strToDo), num, type);
        return preTxt;
    }

    private String getAbsoluteCharacter1(String str, int type) {
        return getSpaces(42 - str.length(), type) + str;
    }

    public String getPreAddedSpaceFormat(String sourceTxt, String toAddTxt, int max, int type) {
        return sourceTxt + getSpaces(max - (sourceTxt.length() + toAddTxt.length()), type) + toAddTxt;
    }

    Bitmap ShrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

}
