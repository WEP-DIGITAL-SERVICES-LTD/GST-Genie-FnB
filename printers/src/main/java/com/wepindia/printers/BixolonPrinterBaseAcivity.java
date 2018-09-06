package com.wepindia.printers;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;
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
import com.wepindia.printers.bixolon.BixolonPrinterStatus;
import com.wepindia.printers.bixolon.DialogManager;
import com.wepindia.printers.wep.PrinterConnectionError;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BixolonPrinterBaseAcivity extends WepBaseActivity {

    private Context context;
    public BixolonPrinter mBixolonPrinter;
    private String mConnectedDeviceName = null;
    private String mTarget = null;
    private boolean mIsConnected;
    private BixolonPrinterStatus bixolonPrinterStatus = null;
    static final int REQUEST_CODE_SELECT_FIRMWARE = Integer.MAX_VALUE;
    static final int RESULT_CODE_SELECT_FIRMWARE = Integer.MAX_VALUE - 1;
    static final int MESSAGE_START_WORK = Integer.MAX_VALUE - 2;
    static final int MESSAGE_END_WORK = Integer.MAX_VALUE - 3;
    private PrinterConnectionError printerConnectionError = null;
    private boolean DEBUG = false;

    public BixolonPrinter getInstance(Context cxt, BixolonPrinterStatus listener){
        bixolonPrinterStatus = listener;
        context = cxt;
        if (mBixolonPrinter == null)
            mBixolonPrinter = new BixolonPrinter(cxt, mHandler, null);
        return mBixolonPrinter;
    }

    public BixolonPrinter getmBixolonPrinter(Context context, Handler mHandler){
        this.context = context;
        if (mBixolonPrinter == null)
            mBixolonPrinter = new BixolonPrinter(context, mHandler, null);
        return mBixolonPrinter;
    }

    public void setmTarget(String mTarget) {
        this.mTarget = mTarget;
    }

    public void setmContext(Context mContext) {
        this.context = mContext;
    }

    public void mInitListener(PrinterConnectionError printerConnectionError){
        this.printerConnectionError = printerConnectionError;
    }

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

    private boolean initializeObject() {
        try {
            getmBixolonPrinter(context, mHandler);
        }
        catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
            return false;
        }

        return true;
    }

    private boolean printBillData(PrintKotBillItem item, int printCount) {
        if (mBixolonPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        /*if (!mIsConnected) {
            return false;
        }*/

        try {

            if (printCount == 1) {
                sendBillData(item);
            } else {
                for (int i =0;i<printCount; i++) {
                    sendBillData(item);
//                    Thread.sleep(3000);
                }
            }
        }
        catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
                try {
                    mBixolonPrinter.disconnect();
                }
                catch (Exception ex) {
                    // Do nothing
                }
//            }
        }

        return true;
    }
    private boolean printKotData(PrintKotBillItem item) {
        if (mBixolonPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {
            sendKotData(item);
        }
        catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    private boolean printSalesReturnData(SalesReturnPrintBean item) {
        if (mBixolonPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        /*if (!mIsConnected) {
            return false;
        }*/

        try {

            sendSalesReturnData(item);
        }
        catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    private boolean printDepositReceiptData(Customer item) {
        if (mBixolonPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        /*if (!mIsConnected) {
            return false;
        }*/

        try {

            sendDepositReceiptData(item);
        }
        catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    private boolean printReceiptData(PaymentReceipt item) {
        if (mBixolonPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        /*if (!mIsConnected) {
            return false;
        }*/

        try {

            sendReceiptData(item);
        }
        catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    public boolean printReportData(List<List<String>> Report, String ReportName, String type) {
        if (mBixolonPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {

            if (ReportName.contains("Cumulative")) {
//            tmpList = createCumulativeReportData(itemReport, reportName);
                sendCumulativeReportData(Report, ReportName, type);
            }
            else {
//            tmpList = createReportData(itemReport, reportName);
                sendReportData(Report, ReportName, type);
            }
        }
        catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                mBixolonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    private boolean connectPrinter() {
        if (mBixolonPrinter == null) {
            return false;
        }

        try {

            mBixolonPrinter.connectUsb(mTarget);
        }
        catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
            return false;
        }

        return true;
    }

    private void finalizeObject() {
        bixolonPrinterStatus = null;
        context = null;
        mBixolonPrinter = null;
    }

    private void sendBillData(PrintKotBillItem item){

        try{

            if (item.getCompanyLogoPath() != null
                    && !item.getCompanyLogoPath().isEmpty()
                    && !item.getCompanyLogoPath().equalsIgnoreCase("1234567890")) {
                InputStream ims = new FileInputStream(item.getCompanyLogoPath());
//                Bitmap logoData = BitmapFactory.decodeStream(ims);

                Bitmap logoData = ShrinkBitmap(item.getCompanyLogoPath(), 200, 200);

//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                logoData.compress(Bitmap.CompressFormat.PNG, 100, stream);
                mBixolonPrinter.printBitmap(logoData, BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.BITMAP_WIDTH_NONE, 88, false);

                mBixolonPrinter.printText("\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);

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

            mBixolonPrinter.printText("TAX INVOICE"+item.getIsDuplicate()+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            if (item.getBoldHeader() == 1) {
                if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine1()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine2()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine3()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine4()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine5()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
            }
            else {
                if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine1()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine2()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine3()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine4()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine5()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
            }

            if (item.getOwnerDetail() == 1) {
                mBixolonPrinter.printText("GSTIN     : "+item.getAddressLine1()+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
                mBixolonPrinter.printText("Name      : "+item.getAddressLine2()+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
                mBixolonPrinter.printText("Address   : "+item.getAddressLine3()+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }

            mBixolonPrinter.printText("\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Bill no         : "+item.getBillNo()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            if(item.getBillingMode().equals("1"))
                mBixolonPrinter.printText("Table           : "+item.getTableNo()+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            mBixolonPrinter.printText("Date            : "+item.getDate() +"    Time : "+item.getTime() +"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText("Cashier         : "+item.getOrderBy()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            if (!item.getCustomerName().equals("") && !item.getCustomerName().contains("-")) {
                mBixolonPrinter.printText("Customer Name   : "+item.getCustomerName()+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }
            if (!item.getSalesManId().equals("") && !item.getSalesManId().contains("-")) {
                mBixolonPrinter.printText("Salesman Id     : "+item.getSalesManId()+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }

            if(item.getBillingMode().equalsIgnoreCase("4") || item.getBillingMode().equalsIgnoreCase("3")) {
                mBixolonPrinter.printText("Payment Status  : " + item.getPaymentStatus()+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }

            if (item.getPrintService() == 1) {
                if(item.getBillingMode().equalsIgnoreCase("1") || item.getBillingMode().equalsIgnoreCase("2") ||
                        item.getBillingMode().equalsIgnoreCase("3") || item.getBillingMode().equalsIgnoreCase("4")){
                    mBixolonPrinter.printText("Service         : "+ item.getStrBillingModeName() + "\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                } else {
                    mBixolonPrinter.printText("-----------" + "\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
            }

            mBixolonPrinter.printText("========================================================"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            if(item.getAmountInNextLine() ==0) {
                mBixolonPrinter.printText("SI  ITEM NAME              QTY        RATE       AMOUNT " + "\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
                if(item.getHSNPrintEnabled_out()== 1)
                {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        mBixolonPrinter.printText("HSN       DISC"+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    } else {
                        mBixolonPrinter.printText("HSN"+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    }
                } else {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        mBixolonPrinter.printText("          DISC"+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    }
                }
            }
            else {
                if(item.getHSNPrintEnabled_out()== 1)
                {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        mBixolonPrinter.printText("SI  ITEM NAME              QTY        RATE              ",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                        mBixolonPrinter.printText("HSN       DISC                                  AMOUNT " + "\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    } else {
                        mBixolonPrinter.printText("SI  ITEM NAME              QTY        RATE              ",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                        mBixolonPrinter.printText("HSN                                             AMOUNT " + "\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    }
                }
                else {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        mBixolonPrinter.printText("SI  ITEM NAME              QTY        RATE               ",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                        mBixolonPrinter.printText("          DIS        C                           AMOUNT" + "\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    } else {
                        mBixolonPrinter.printText("SI  ITEM NAME              QTY        RATE               ",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                        mBixolonPrinter.printText("                                                 AMOUNT" + "\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    }
                }
            }

            mBixolonPrinter.printText("========================================================"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            int totalitemtypes =0;
            double totalquantitycount =0;
            double subtotal =0;

            while (it.hasNext())
            {
                BillKotItem billKotItem = (BillKotItem) it.next();

                String preId = getPostAddedSpaceFormat("",String.valueOf(billKotItem.getItemId()),3,1);
                String preName = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getItemName()),billKotItem.getItemName().length(),1),17,1);
                String HSN = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getHSNCode()),10,1),10,1);
                String itemWiseDisc = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getDiscount()),10,1),10,1);

                String preQty = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.valueOf(billKotItem.getQty())+billKotItem.getUOM(),11,1),13,1);
                String preRate = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getRate()),10,1),11,1);
                String preAmount = "0";
                String pre = "";
                //if(item.getAmountInNextLine() ==0)
                if(String.format("%.2f",billKotItem.getAmount()).length() <= 9)
                {
                    preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getAmount())
                            +billKotItem.getTaxIndex(),11,1),10,1);
                    if(billKotItem.getItemName().length() > 16) {
                        pre = preId+preName.substring(0,16)+" "+/*HSN+*/preQty+preRate+preAmount;
                        mBixolonPrinter.printText(pre+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                        if(preName.length() > 32){
                            mBixolonPrinter.printText("   " + preName.substring(16,32)+"\n",
                                    BixolonPrinter.ALIGNMENT_LEFT,
                                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                    false);
                        } else {
                            mBixolonPrinter.printText("   " + preName.substring(16,preName.length())+"\n",
                                    BixolonPrinter.ALIGNMENT_LEFT,
                                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                    false);
                        }

                    } else {
                        pre = preId+preName+/*HSN+*/preQty+preRate+preAmount;
                        mBixolonPrinter.printText(pre+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    }
                    if(item.getHSNPrintEnabled_out() == 1) {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            mBixolonPrinter.printText(HSN + itemWiseDisc +"\n",
                                    BixolonPrinter.ALIGNMENT_LEFT,
                                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                    false);
                        } else {
                            mBixolonPrinter.printText(HSN+"\n",
                                    BixolonPrinter.ALIGNMENT_LEFT,
                                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                    false);
                        }
                    } else {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            mBixolonPrinter.printText(getFormatedCharacterForPrint_init(itemWiseDisc,20,1) +"\n",
                                    BixolonPrinter.ALIGNMENT_LEFT,
                                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                    false);
                        }
                    }
                }
                else // item.getAmountInNextLine() ==1
                {
                    preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getAmount())
                            +billKotItem.getTaxIndex(),(String.format("%.2f",billKotItem.getAmount()) +billKotItem.getTaxIndex() ).length(),1),(String.format("%.2f",billKotItem.getAmount()) +billKotItem.getTaxIndex() ).length(),1);
                    String pre2 = "", pre3 ="", pre4 = "";
                    if(item.getHSNPrintEnabled_out() == 1) {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            pre2 = getPostAddedSpaceFormat("",HSN,8,1);
                            pre4 = getPostAddedSpaceFormat("",itemWiseDisc,8,1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount,28,1);
                        } else {
                            pre2 = getPostAddedSpaceFormat("",HSN,8,1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount,37,1);
                        }
                    }else
                    {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            pre4 = getFormatedCharacterForPrint_init(itemWiseDisc,18,1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount,30,1);
                        } else {
                            pre3 = getFormatedCharacterForPrint_init(preAmount,48,1);
                        }
                    }
                    if(billKotItem.getItemName().length() > 16) {
                        if(preName.length() > 32){
                            pre = preId + preName.substring(0,16)+" " +/*HSN+*/preQty + preRate + "\n" + "   " + preName.substring(16,32)+ "\n" + pre2 + pre4 + pre3;
                        } else {
                            pre = preId + preName.substring(0,16)+" " +/*HSN+*/preQty + preRate + "\n" + "   " + preName.substring(16,preName.length())+ "\n" + pre2 + pre4 + pre3;
                        }
                    } else {
                        pre = preId + preName +/*HSN+*/preQty + preRate + "\n" + pre2 + pre4 + pre3;
                    }
                    mBixolonPrinter.printText(pre+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }

                totalitemtypes++;
                totalquantitycount += billKotItem.getQty();
                subtotal += billKotItem.getAmount();
            }

            mBixolonPrinter.printText("--------------------------------------------------------"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText(getSpaceFormater("Total Item(s) : "+totalitemtypes+" /Qty : "+totalquantitycount,String.format("%.2f",subtotal),56,1)+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            double discount = item.getFdiscount();
            float discountPercentage = item.getdiscountPercentage();
            if(discountPercentage > 0)
            {
               /* mBixolonPrinter.printText("\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);*/
                String DiscName = getPostAddedSpaceFormat("","Discount Amount",23,1);
                String DiscPercent = getPostAddedSpaceFormat("","@ " + String.format("%.2f",discountPercentage) + " %",23,1);
                String DiscValue = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",discount),18,1),8 ,1);
                String pre = DiscName + DiscPercent + DiscValue;
                mBixolonPrinter.printText(pre+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }
            else if (discount > 0)
            {
               /* mBixolonPrinter.printText("\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);*/
                mBixolonPrinter.printText(getSpaceFormater("Discount Amount",String.format("%.2f",discount),56,1)+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }

            ArrayList<BillTaxItem> billOtherChargesItems = item.getBillOtherChargesItems();
            if(billOtherChargesItems.size()>0)
            {
                Iterator it1 = billOtherChargesItems.iterator();
                while (it1.hasNext())
                {
                    BillTaxItem billKotItem = (BillTaxItem) it1.next();
                    String TxName = getPostAddedSpaceFormat("",String.valueOf(billKotItem.getTxName()),23,1);
                    String TxPercent = getPostAddedSpaceFormat("","",15,1);
                    String TxValue = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getPrice()),18,1),8 ,1);
                    String pre = TxName + TxPercent + TxValue;
                    mBixolonPrinter.printText(pre+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
            }

            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if(item.getIsInterState().equalsIgnoreCase("n")) // IntraState
            {
                if (it11.hasNext())
                {
                    mBixolonPrinter.printText("========================================================"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                    if(item.getUTGSTEnabled() ==0) // disabled
                        mBixolonPrinter.printText("Tax(%)     TaxableVal     CGSTAmt    SGSTAmt      TaxAmt"+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    else
                        mBixolonPrinter.printText("Tax(%)     TaxableVal     CGSTAmt    UTGSTAmt     TaxAmt"+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                    mBixolonPrinter.printText("========================================================"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);

                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()),14,1),15,1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getCGSTAmount()),10,1),11,1);
                            String SGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getSGSTAmount()),10,1),11,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),12,1),10,1);

                            String pre = TxIndex + TaxableValue + CGSTAmt+ SGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getCGSTAmount()+billTaxSlabEntry.getSGSTAmount();
                            mBixolonPrinter.printText(pre+"\n",
                                    BixolonPrinter.ALIGNMENT_LEFT,
                                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                    false);
                        }
                    }while (it11.hasNext());
                }
            }else // InterState
            {
                if (it11.hasNext())
                {
                    mBixolonPrinter.printText("========================================================"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                    mBixolonPrinter.printText("Tax(%)     TaxableVal       IGSTAmt               TaxAmt"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                    mBixolonPrinter.printText("========================================================"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);

                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()),14,1),15,1);
                            String IGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getIGSTAmount()),12,1),11,1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init("",8,1),9,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),12,1),10,1);

                            String pre = TxIndex + TaxableValue + IGSTAmt+ CGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getIGSTAmount();
                            mBixolonPrinter.printText(pre+"\n",
                                    BixolonPrinter.ALIGNMENT_LEFT,
                                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                    false);
                        }
                    }while (it11.hasNext());
                }
            }

            mBixolonPrinter.printText("\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            double  dtotalcessAmt =0;
            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            Iterator it21 = billcessTaxItems.iterator();
            while (it21.hasNext()) {

                BillServiceTaxItem billKotItem = (BillServiceTaxItem) it21.next();
                if (billKotItem.getServicePercent() > 0 || billKotItem.getServicePrice() > 0){

                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getServiceTxName()), 27, 1);
                    String TxPercent = "";
                    if (billKotItem.getServicePercent() > 0)
                        TxPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", billKotItem.getServicePercent()) + "% ", 15, 1);
                    else
                        TxPercent = getPostAddedSpaceFormat("", "@ Rs." + String.format("%.2f", billKotItem.getPricePerUnit()) + " ", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getServicePrice()), 10, 1), 8, 1);
                    dtotalcessAmt += billKotItem.getServicePrice();
                    String pre = TxName + TxPercent + TxValue;
                    mBixolonPrinter.printText(pre + "\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
            }

            double dTotalTaxAmt = dTotTaxAmt +dtotalcessAmt;
            if(dTotalTaxAmt >0)
            {
                mBixolonPrinter.printText(getSpaceFormater("Total Tax Amount",String.format("%.2f",dTotalTaxAmt),56,1)+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }
            mBixolonPrinter.printText("========================================================"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText(getSpaceFormater("TOTAL",String.format("%.2f",item.getNetTotal()),56,1)+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            if(item.getCardPaymentValue()>0 || item.geteWalletPaymentValue()>0 ||
                    item.getCouponPaymentValue()>0 || item.getPettyCashPaymentValue()>0 || item.getRewardPoints()>0 || item.getCashPaymentValue()>0
                    || item.getAepsPaymentValue()>0 || item.getDblMSwipeVale() > 0){
                mBixolonPrinter.printText("========================================================"+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
                if(item.getCardPaymentValue()>0)
                    mBixolonPrinter.printText(getSpaceFormater("OtherCard Payment",String.format("%.2f",item.getCardPaymentValue()),56,1)+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                if(item.geteWalletPaymentValue()>0)
                    mBixolonPrinter.printText(getSpaceFormater("eWallet Payment",String.format("%.2f",item.geteWalletPaymentValue()),56,1)+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                if(item.getCouponPaymentValue()>0)
                    mBixolonPrinter.printText(getSpaceFormater("Coupon Payment",String.format("%.2f",item.getCouponPaymentValue()),56,1)+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                if(item.getPettyCashPaymentValue()>0)
                    mBixolonPrinter.printText(getSpaceFormater("PettyCash Payment",String.format("%.2f",item.getPettyCashPaymentValue()),56,1)+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                if(item.getCashPaymentValue()>0)
                    mBixolonPrinter.printText(getSpaceFormater("Cash Payment",String.format("%.2f",item.getCashPaymentValue()),56,1)+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                if(item.getRewardPoints()>0)
                    mBixolonPrinter.printText(getSpaceFormater("Reward Pt Payment",String.format("%.2f",item.getRewardPoints()),56,1)+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                if(item.getAepsPaymentValue()>0)
                    mBixolonPrinter.printText(getSpaceFormater("AEPS Payment",String.format("%.2f",item.getAepsPaymentValue()),56,1)+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                if(item.getDblMSwipeVale()>0)
                    mBixolonPrinter.printText(getSpaceFormater("MSwipe Payment",String.format("%.2f",item.getDblMSwipeVale()),56,1)+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                if(item.getDblPaytmValue()>0)
                    mBixolonPrinter.printText(getSpaceFormater("Paytm Payment",String.format("%.2f",item.getDblPaytmValue()),56,1)+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
            }

            if (item.getChangePaymentValue()>0) {
                mBixolonPrinter.printText("--------------------------------------------------------"+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
                mBixolonPrinter.printText(getSpaceFormater("Due amount",String.format("%.2f",item.getChangePaymentValue()),56,1)+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }

            mBixolonPrinter.printText("========================================================"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            if(item.getRoundOff() > 0){
                mBixolonPrinter.printText(getSpaceFormater("Total Roundoff to 1.00 ","",56,1)+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
                mBixolonPrinter.printText("========================================================"+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }

            if(!item.getFooterLine1().equals(""))
                mBixolonPrinter.printText(item.getFooterLine1()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine2().equals(""))
                mBixolonPrinter.printText(item.getFooterLine2()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine3().equals(""))
                mBixolonPrinter.printText(item.getFooterLine3()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine4().equals(""))
                mBixolonPrinter.printText(item.getFooterLine4()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine5().equals(""))
                mBixolonPrinter.printText(item.getFooterLine5()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);

            mBixolonPrinter.cutPaper(5, true);

        } catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
        }
    }

    private void sendKotData(PrintKotBillItem item){
        String tblno = "", modename = "";
        try{
            if(item.getBillingMode().equalsIgnoreCase("1")){
                tblno = "Table # "+item.getTableNo() + " | ";
                modename = item.getStrBillingModeName();
            } else {
                tblno = "";
                modename = item.getStrBillingModeName();
            }
            mBixolonPrinter.printText(modename+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText(tblno +"KOT # "+item.getBillNo()+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);


            mBixolonPrinter.printText("=============================================\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Attendant : "+item.getOrderBy()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            if ((item.getWaiterName() != null) && !(item.getWaiterName().equals("")))
                mBixolonPrinter.printText("Waiter : "+item.getWaiterName()+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            mBixolonPrinter.printText("Date        : "+item.getDate() +" | "+"Time : "+item.getTime()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("========================================================"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("SNo        NAME                    QTY"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("========================================================"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            while (it.hasNext())
            {
                BillKotItem billKotItem = (BillKotItem) it.next();
                int id = billKotItem.getItemId();
                String name = getFormatedCharacterForPrint(billKotItem.getItemName(),16,1);
                String qty = billKotItem.getQty()+"";
                String pre = getPostAddedSpaceFormat("",String.valueOf(id),10,1)+name;
                mBixolonPrinter.printText(getPreAddedSpaceFormat(pre,qty,38,1)+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }
            mBixolonPrinter.printText("=============================================\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.cutPaper(5, true);

        } catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
        }
    }

    public String getPreAddedSpaceFormat(String sourceTxt, String toAddTxt, int max,int type)
    {
        return sourceTxt+getSpaces(max-(sourceTxt.length()+toAddTxt.length()),type)+toAddTxt;
    }

    private void sendSalesReturnData(SalesReturnPrintBean item){

        try{

            mBixolonPrinter.printText(item.getIsDuplicate(),
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("GSTIN     : "+item.getStrOwnerGSTIN()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText("Name      : "+item.getStrOwnerName()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText("Address   : "+item.getStrOwnerAddress()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Bill no         : "+item.getStrInvoiceNo()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Bill Date            : "+item.getStrInvoiceDate()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText("\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("CDNI      : "+item.getiSrId()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Note Date : "+item.getStrSalesReturnDate() +"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Customer Name   : "+item.getStrCustName()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText("Notes     : "+item.getStrReason()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("========================================================"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("ITEM NAME                  QTY                   AMOUNT" + "\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("========================================================"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            ArrayList<SalesReturnBean> salesReturnItems = item.getArrayList();
            Iterator it = salesReturnItems.iterator();
            int totalitemtypes =0;

            while (it.hasNext())
            {
                BillKotItem billKotItem = (BillKotItem) it.next();

                SalesReturnBean bean = (SalesReturnBean) it.next();

                String preName = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(bean.getStrItemName()),bean.getStrItemName().length(),1),17,1);
                String preQty = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.valueOf(bean.getDblReturnQuantity()),9,1),10,1);
                String preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f", bean.getDblReturnAmount()),20,1),10,1);
                String pre = "";
                pre = preName+ preQty + preAmount;
                mBixolonPrinter.printText(pre+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
                totalitemtypes++;
            }

            mBixolonPrinter.printText("--------------------------------------------------------"+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText(getSpaceFormater("Total Item(s) : "+totalitemtypes,"",48,1),
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if(item.getIsInterstate() == 0) // IntraState
            {
                if (it11.hasNext())
                {
                    mBixolonPrinter.printText("========================================================"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                    mBixolonPrinter.printText("Tax(%)                    CGSTAmt    SGSTAmt      TaxAmt"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                    mBixolonPrinter.printText("========================================================"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);

                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getCGSTAmount()),13,1),9,1);
                            String SGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getSGSTAmount()),13,1),9,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),15,1),10,1);

                            String pre = TxIndex + CGSTAmt+ SGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getCGSTAmount()+billTaxSlabEntry.getSGSTAmount();
                            mBixolonPrinter.printText(pre+"\n",
                                    BixolonPrinter.ALIGNMENT_LEFT,
                                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                    false);
                        }
                    }while (it11.hasNext());
                }
            }else // InterState
            {
                if (it11.hasNext())
                {
                    mBixolonPrinter.printText("========================================================"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                    mBixolonPrinter.printText("Tax(%)                      IGSTAmt               TaxAmt"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                    mBixolonPrinter.printText("========================================================"+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);

                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String IGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getIGSTAmount()),20,1),9,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),20,1),10,1);

                            String pre = TxIndex + IGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getIGSTAmount();
                            mBixolonPrinter.printText(pre+"\n",
                                    BixolonPrinter.ALIGNMENT_LEFT,
                                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                    false);
                        }
                    }while (it11.hasNext());
                }
            }

            mBixolonPrinter.printText("\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            double  dtotalcessAmt =0;
            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            Iterator it21 = billcessTaxItems.iterator();
            while (it21.hasNext()) {

                BillServiceTaxItem billKotItem = (BillServiceTaxItem) it21.next();
                if (billKotItem.getServicePercent() > 0 || billKotItem.getPricePerUnit() > 0){

                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getServiceTxName()), 23, 1);
                    String TxPercent = "";
                    if (billKotItem.getServicePercent() > 0)
                        TxPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", billKotItem.getServicePercent()) + "% ", 15, 1);
                    else
                        TxPercent = getPostAddedSpaceFormat("", "@ Rs." + String.format("%.2f", billKotItem.getPricePerUnit()) + " ", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getServicePrice()), 10, 1), 8, 1);
                    dtotalcessAmt += billKotItem.getServicePrice();
                    String pre = TxName + TxPercent + TxValue;
                    mBixolonPrinter.printText(pre + "\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
            }

            double dTotalTaxAmt = dTotTaxAmt +dtotalcessAmt;
            if(dTotalTaxAmt >0)
            {
                mBixolonPrinter.printText(getSpaceFormater("Total Tax Amount",String.format("%.2f",dTotalTaxAmt),56,1)+"\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }
            mBixolonPrinter.printText(getSpaceFormater("Total Invoice Amount",String.format("%.2f",item.getDblAmount()),48,1)+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText(getSpaceFormater("Total Return Amount",String.format("%.2f",item.getDblReturnAmount()),48,1)+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.cutPaper(5, true);

        } catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
        }
    }

    private void sendDepositReceiptData(Customer item){

        try{

            Thread.sleep(100);

            mBixolonPrinter.printText("Deposit Amount Receipt"+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            if (item.getHeaderPrintBold() == 1) {
                if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine1()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine2()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine3()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine4()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine5()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
            }
            else {
                if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine1()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine2()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine3()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine4()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine5()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
            }

            mBixolonPrinter.printText("=========================================="+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);


            mBixolonPrinter.printText("Deposit Date    : "+item.getBusinessDate()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Customer Name   : "+item.getStrCustName()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Mobile No.      : "+item.getStrCustContactNumber()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Deposit Amount  : "+item.getDblDepositAmt()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Credit Amount   : "+item.getdCreditAmount()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("=========================================="+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            if(!item.getFooterLine1().equals(""))
                mBixolonPrinter.printText(item.getFooterLine1()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine2().equals(""))
                mBixolonPrinter.printText(item.getFooterLine2()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine3().equals(""))
                mBixolonPrinter.printText(item.getFooterLine3()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine4().equals(""))
                mBixolonPrinter.printText(item.getFooterLine4()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine5().equals(""))
                mBixolonPrinter.printText(item.getFooterLine5()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);

            mBixolonPrinter.cutPaper(5, true);

        } catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
        }
    }

    private void sendReceiptData(PaymentReceipt item){

        try{

            Thread.sleep(100);

            if (item.getiBillType() == 1) {
                if(item.getIsDuplicate().equals(""))
                    mBixolonPrinter.printText("PAYMENT"+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                else
                    mBixolonPrinter.printText("PAYMENT"+"\n"+item.getIsDuplicate()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
            } else {
                if(item.getIsDuplicate().equals(""))
                    mBixolonPrinter.printText("RECEIPT"+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                else
                    mBixolonPrinter.printText("RECEIPT"+"\n"+item.getIsDuplicate()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
            }

            if (item.getHeaderPrintBold() == 1) {
                if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine1()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine2()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine3()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine4()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
                if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine5()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL2 | BixolonPrinter.TEXT_SIZE_VERTICAL2,
                            false);
                }
            }
            else {
                if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine1()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine2()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine3()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine4()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                    mBixolonPrinter.printText(item.getHeaderLine5()+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
            }

            mBixolonPrinter.printText("=========================================="+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            if (item.getiBillType() == 1) {
                mBixolonPrinter.printText("Payment No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            } else {
                mBixolonPrinter.printText("Receipt No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n",
                        BixolonPrinter.ALIGNMENT_LEFT,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            }
            mBixolonPrinter.printText("Invoice Date    : "+item.getStrDate()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Description     : "+item.getDescriptionText()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Amount          : "+String.valueOf(item.getdAmount())+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("Reason          : "+item.getStrReason()+"\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("=========================================="+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            /*if(!item.getFooterLine1().equals(""))
                mBixolonPrinter.printText(item.getFooterLine1()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine2().equals(""))
                mBixolonPrinter.printText(item.getFooterLine2()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine3().equals(""))
                mBixolonPrinter.printText(item.getFooterLine3()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine4().equals(""))
                mBixolonPrinter.printText(item.getFooterLine4()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);
            if(!item.getFooterLine5().equals(""))
                mBixolonPrinter.printText(item.getFooterLine5()+"\n",
                        BixolonPrinter.ALIGNMENT_CENTER,
                        BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                        BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                        false);*/

            mBixolonPrinter.cutPaper(5, true);

        } catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
        }
    }

    private void sendReportData(List<List<String>> itemReport, String reportName, String type){

        try{

            Thread.sleep(100);

            mBixolonPrinter.printText(reportName+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("======================================================="+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            Calendar time = Calendar.getInstance();
            mBixolonPrinter.printText("["+String.format("%tR", time)+"]\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText("\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            for(int j=0;j<itemReport.size();j++)
            {
                List<String> arrayListColumn = itemReport.get(j);
                StringBuffer sb = new StringBuffer();
                for (int i=0;i<arrayListColumn.size();i++)
                {
                    String str = arrayListColumn.get(i);
                    String preTxt = getAbsoluteCharacter(String.valueOf(str),10,1);
                    if(j==0)
                        sb.append(preTxt+" |");
                    else
                        sb.append(" "+preTxt+" ");
                    int rem = i%3;
                    if(rem == 0 && i!=0)
                    {
                        mBixolonPrinter.printText(sb.toString()+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                        sb = new StringBuffer();
                    }

                    if(rem != 0 && (arrayListColumn.size()-1)==i)
                    {
                        mBixolonPrinter.printText(getAbsoluteCharacter1(sb.toString(),1)+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                        sb = new StringBuffer();
                    }
                /*else if(i == arrayListColumn.size())
                {
                    tmpList.add(CreateBitmap.AddText(18, "Bold","MONOSPACE", sb.toString(), TextAlign.LEFT,getApplicationContext()));
                    sb = new StringBuffer();
                }*/
                }
                if(j==0)
                    mBixolonPrinter.printText("======================================================="+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
            }

            mBixolonPrinter.printText("======================================================="+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.cutPaper(5, true);

        } catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
        }
    }

    private void sendCumulativeReportData(List<List<String>> itemReport, String reportName, String type){

        try{

            Thread.sleep(100);

            mBixolonPrinter.printText(reportName+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.printText("======================================================="+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            Calendar time = Calendar.getInstance();
            mBixolonPrinter.printText("["+String.format("%tR", time)+"]\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);
            mBixolonPrinter.printText("\n",
                    BixolonPrinter.ALIGNMENT_LEFT,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            for(int j=0;j<itemReport.size();j++)
            {
                if(j == itemReport.size() - 1){
                    mBixolonPrinter.printText("======================================================="+"\n",
                            BixolonPrinter.ALIGNMENT_LEFT,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
                }
                List<String> arrayListColumn = itemReport.get(j);
                StringBuffer sb = new StringBuffer();
                for (int i=0;i<arrayListColumn.size();i++)
                {
                    String str = arrayListColumn.get(i);
                    String preTxt = getAbsoluteCharacter(String.valueOf(str),10,1);
                    if(j==0 || j==1)
                        sb.append(preTxt+" |");
                    else
                        sb.append(" "+preTxt+" ");
                    int rem = i%3;
                    if(rem == 0 && i!=0)
                    {
                        mBixolonPrinter.printText(sb.toString()+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                        sb = new StringBuffer();
                    }

                    if(rem != 0 && (arrayListColumn.size()-1)==i)
                    {
                        mBixolonPrinter.printText(getAbsoluteCharacter1(sb.toString(),1)+"\n",
                                BixolonPrinter.ALIGNMENT_LEFT,
                                BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                                BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                                false);
                        sb = new StringBuffer();
                    }
                }
                if(j==1)
                    mBixolonPrinter.printText("======================================================="+"\n",
                            BixolonPrinter.ALIGNMENT_CENTER,
                            BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                            BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                            false);
            }

            mBixolonPrinter.printText("======================================================="+"\n",
                    BixolonPrinter.ALIGNMENT_CENTER,
                    BixolonPrinter.TEXT_ATTRIBUTE_FONT_C,
                    BixolonPrinter.TEXT_SIZE_HORIZONTAL1 | BixolonPrinter.TEXT_SIZE_VERTICAL1,
                    false);

            mBixolonPrinter.cutPaper(5, true);

        } catch (Exception e) {
            printerConnectionError.onError(mBixolonPrinter, e.getMessage());
        }
    }

    private String getSpaceFormater(String txtPre, String txtPost, int num, int type) {
        return txtPre+getSpaces(num-(txtPre.length()+txtPost.length()),type)+txtPost;
    }

    private String getFormatedCharacterForPrint(String txt, int limit,int type) {
        if(txt.length()<limit){
            return txt+getSpaces(limit-txt.length(),type);
        }else {
            return txt.substring(0,limit);
        }
    }

    private String getFormatedCharacterForPrint_init(String txt, int limit, int type) {
        if(txt.length()<limit){
            return getSpaces(limit-txt.length(),type)+txt;
        }else {
            return txt.substring(0,limit);
        }
    }

    public String getPostAddedSpaceFormat(String sourceTxt, String toAddTxt, int max,int type)
    {
        return sourceTxt+toAddTxt+getSpaces(max-(sourceTxt.length()+toAddTxt.length()),type);
    }

    public String getSpaces(int num,int type)
    {
        StringBuffer sb = new StringBuffer();
        if(type==0)
        {
            for (int i=0;i<num;i++)
            {
                sb.append(context.getResources().getString(R.string.superSpace));
            }
        }
        else
        {
            for (int i=0;i<num;i++)
            {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public String getAbsoluteCharacter(String str, int num,int type) {
        String strToDo = "";
        if(str.length() > num)
        {
            strToDo = str.substring(0,num);
        }
        else
        {
            strToDo = str;
        }
        String preTxt = getPostAddedSpaceFormat("", String.valueOf(strToDo),num,type);
        return preTxt;
    }

    private String getAbsoluteCharacter1(String str,int type) {
        return getSpaces(42-str.length(),type)+str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB || Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR1) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBixolonPrinter.disconnect();
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BixolonPrinter.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BixolonPrinter.STATE_CONNECTED:
                            if (bixolonPrinterStatus != null)
                                bixolonPrinterStatus.onStatusChanged(BixolonPrinter.STATE_CONNECTED);
                            mIsConnected = true;
                            break;

                        case BixolonPrinter.STATE_CONNECTING:
                            if (bixolonPrinterStatus != null)
                                bixolonPrinterStatus.onStatusChanged(BixolonPrinter.STATE_CONNECTING);
                            break;

                        case BixolonPrinter.STATE_NONE:
                            if (bixolonPrinterStatus != null)
                                bixolonPrinterStatus.onStatusChanged(BixolonPrinter.STATE_NONE);
                            mIsConnected = false;
                            break;
                    }
                    return true;

                case BixolonPrinter.MESSAGE_WRITE:
                    switch (msg.arg1) {
                        case BixolonPrinter.PROCESS_SET_DOUBLE_BYTE_FONT:
                            mHandler.obtainMessage(MESSAGE_END_WORK).sendToTarget();
                            if (DEBUG)
                                Toast.makeText(context, "Complete to set double byte font.", Toast.LENGTH_SHORT).show();
                            break;

                        case BixolonPrinter.PROCESS_DEFINE_NV_IMAGE:
                            mBixolonPrinter.getDefinedNvImageKeyCodes();
                            if (DEBUG)
                                Toast.makeText(context, "Complete to define NV image", Toast.LENGTH_LONG).show();
                            break;

                        case BixolonPrinter.PROCESS_REMOVE_NV_IMAGE:
                            mBixolonPrinter.getDefinedNvImageKeyCodes();
                            if (DEBUG)
                                Toast.makeText(context, "Complete to remove NV image", Toast.LENGTH_LONG).show();
                            break;

                        case BixolonPrinter.PROCESS_UPDATE_FIRMWARE:
                            mBixolonPrinter.disconnect();
                            if (DEBUG)
                                Toast.makeText(context, "Complete to download firmware.\nPlease reboot the printer.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;

                case BixolonPrinter.MESSAGE_READ:
//                    this.dispatchMessage(msg);
                    return true;

                case BixolonPrinter.MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME);
//                    Toast.makeText(context, mConnectedDeviceName, Toast.LENGTH_LONG).show();
                    return true;

                case BixolonPrinter.MESSAGE_TOAST:
                    if (DEBUG)
                        Toast.makeText(context, msg.getData().getString(BixolonPrinter.KEY_STRING_TOAST), Toast.LENGTH_SHORT).show();
                    return true;

                case BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    if (msg.obj == null) {
                        Toast.makeText(context, "No paired device", Toast.LENGTH_SHORT).show();
                    } else {
                        DialogManager.showBluetoothDialog(context, (Set<BluetoothDevice>) msg.obj);
                    }
                    return true;

                case BixolonPrinter.MESSAGE_PRINT_COMPLETE:
//                    Toast.makeText(context, "Complete to print", Toast.LENGTH_SHORT).show();
                    return true;

                case BixolonPrinter.MESSAGE_ERROR_INVALID_ARGUMENT:
                    if (DEBUG)
                        Toast.makeText(context, "Invalid argument", Toast.LENGTH_SHORT).show();
                    return true;

                case BixolonPrinter.MESSAGE_ERROR_NV_MEMORY_CAPACITY:
                    if (DEBUG)
                        Toast.makeText(context, "NV memory capacity error", Toast.LENGTH_SHORT).show();
                    return true;

                case BixolonPrinter.MESSAGE_ERROR_OUT_OF_MEMORY:
                    if (DEBUG)
                        Toast.makeText(context, "Out of memory", Toast.LENGTH_SHORT).show();
                    return true;

                case BixolonPrinter.MESSAGE_COMPLETE_PROCESS_BITMAP:
                    String text = "Complete to process bitmap.";
                    Bundle data = msg.getData();
                    byte[] value = data.getByteArray(BixolonPrinter.KEY_STRING_MONO_PIXELS);
                    if (value != null) {
                        /*Intent intent = new Intent();
                        intent.setAction(ACTION_COMPLETE_PROCESS_BITMAP);
                        intent.putExtra(EXTRA_NAME_BITMAP_WIDTH, msg.arg1);
                        intent.putExtra(EXTRA_NAME_BITMAP_HEIGHT, msg.arg2);
                        intent.putExtra(EXTRA_NAME_BITMAP_PIXELS, value);
                        sendBroadcast(intent);*/
                    }
                    if (DEBUG)
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    return true;

                case MESSAGE_START_WORK:

                    return true;

                case MESSAGE_END_WORK:

                    return true;

                case BixolonPrinter.MESSAGE_USB_DEVICE_SET:
                    if (msg.obj == null) {
                        Toast.makeText(context, "No connected device", Toast.LENGTH_SHORT).show();
                    } else {
                        if (bixolonPrinterStatus != null)
                            bixolonPrinterStatus.onBixolonPrinterFound((Set<UsbDevice>) msg.obj);
//                        DialogManager.showUsbDialog(context, (Set<UsbDevice>) msg.obj, mUsbReceiver);
                    }
                    return true;

                case BixolonPrinter.MESSAGE_USB_SERIAL_SET:
                    if (msg.obj == null) {
                        Toast.makeText(context, "No connected device", Toast.LENGTH_SHORT).show();
                    } else {
                        final HashMap<String, UsbDevice> usbDeviceMap = (HashMap<String, UsbDevice>) msg.obj;
                        final String[] items = usbDeviceMap.keySet().toArray(new String[usbDeviceMap.size()]);
                        new AlertDialog.Builder(context).setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBixolonPrinter.connect(usbDeviceMap.get(items[which]));
                            }
                        }).show();
                    }
                    return true;

                case BixolonPrinter.MESSAGE_NETWORK_DEVICE_SET:
                    if (msg.obj == null) {
                        Toast.makeText(context, "No connectable device", Toast.LENGTH_SHORT).show();
                    }
//                    DialogManager.showNetworkDialog(context, (Set<String>) msg.obj);
                    return true;
            }
            return false;
        }
    });

   /* private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Bixolon Printer", "mUsbReceiver.onReceive(" + context + ", " + intent + ")");
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                mBixolonPrinter.connect();
                Toast.makeText(context, "Found USB device", Toast.LENGTH_SHORT).show();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                mBixolonPrinter.disconnect();
                Toast.makeText(context, "USB device removed", Toast.LENGTH_SHORT).show();
            }

        }
    };*/

    Bitmap ShrinkBitmap(String file, int width, int height){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    @Override
    public void onHomePressed() {

    }
}
