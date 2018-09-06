package com.wepindia.printers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.Log;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
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
import com.wepindia.printers.epson.ShowMsg;
import com.wepindia.printers.wep.PrinterConnectionError;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SachinV on 21-03-2018.
 */

public class EPSONPrinterBaseActivity extends WepBaseActivity implements ReceiveListener {

    private Context mContext = null;
    private Printer mPrinter = null;
    private String mTarget = null;
    private PrinterConnectionError printerConnectionError = null;
    private int printer_connection_status = -1;
    private String printType = "";
    private List<List<String>> itemReport;
    private String tmpList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        try {
            Log.setLogSettings(mContext, Log.PERIOD_TEMPORARY, Log.OUTPUT_STORAGE, null, 0, 1, Log.LOGLEVEL_LOW);
        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, "setLogSettings", mContext);
        }
    }

    @Override
    public void onHomePressed() {

    }

    public int getPrinter_connection_status() {
        return printer_connection_status;
    }

    public void setPrinter_connection_status(int printer_connection_status) {
        this.printer_connection_status = printer_connection_status;
    }

    public void setmTarget(String mTarget) {
        this.mTarget = mTarget;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void mInitListener(PrinterConnectionError printerConnectionError){
        this.printerConnectionError = printerConnectionError;
    }

    public boolean runPrintBillSequence(PrintKotBillItem item, int printCount) {
        if (!initializeObject()) {
            return false;
        }

        if (!createBillData(item)) {
            finalizeObject();
            return false;
        }

        if (!printData(printCount)) {
            finalizeObject();
            return false;
        }

        return true;
    }

    public boolean runPrintKOTSequence(PrintKotBillItem item) {
        if (!initializeObject()) {
            return false;
        }

        if (!createKotData(item)) {
            finalizeObject();
            return false;
        }

        if (!printData(1)) {
            finalizeObject();
            return false;
        }

        return true;
    }

    public boolean runPrintSalesReturnSequence(SalesReturnPrintBean item) {
        if (!initializeObject()) {
            return false;
        }

        if (!createSalesReturnData(item)) {
            finalizeObject();
            return false;
        }

        if (!printData(1)) {
            finalizeObject();
            return false;
        }

        return true;
    }

    public boolean runPrintDepositReceiptSequence(Customer item, String type) {
        if (!initializeObject()) {
            return false;
        }

        if (!createDepositReceiptData(item)) {
            finalizeObject();
            return false;
        }

        if (!printData(1)) {
            finalizeObject();
            return false;
        }

        return true;
    }

    public boolean runPrintReceiptSequence(PaymentReceipt item, String type) {
        if (!initializeObject()) {
            return false;
        }

        if (!createReceiptData(item)) {
            finalizeObject();
            return false;
        }

        if (!printData(1)) {
            finalizeObject();
            return false;
        }

        return true;
    }

    public boolean runPrintReportSequence(List<List<String>> Report, String ReportName, String type) {
        if (!initializeObject()) {
            return false;
        }

        printType = type;
        String reportName = ReportName;
        itemReport = Report;

        if (ReportName.contains("Cumulative")) {
//            tmpList = createCumulativeReportData(itemReport, reportName);
            if (!createCumulativeReportData(itemReport, reportName)) {
                finalizeObject();
                return false;
            }
        }
        else {
//            tmpList = createReportData(itemReport, reportName);
            if (!createReportData(itemReport, reportName)) {
                finalizeObject();
                return false;
            }
        }

        if (!printData(1)) {
            finalizeObject();
            return false;
        }

        return true;
    }

    private boolean createBillData(PrintKotBillItem item) {
        String method = "";
//        Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            if (item.getCompanyLogoPath() != null
                    && !item.getCompanyLogoPath().isEmpty()
                    && !item.getCompanyLogoPath().equalsIgnoreCase("1234567890")) {
                InputStream ims = new FileInputStream(item.getCompanyLogoPath());
//                Bitmap logoData = BitmapFactory.decodeStream(ims);

                Bitmap logoData = ShrinkBitmap(item.getCompanyLogoPath(), 200, 200);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                logoData.compress(Bitmap.CompressFormat.PNG, 50, stream);
                method = "addImage";
                mPrinter.addImage(logoData, 0, 0,
                        logoData.getWidth(),
                        logoData.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);

                textData.append("\n");
            }

            /*if(item.getIsDuplicate().equals(""))
                textData.append("TAX INVOICE"+"\n");
            else
                textData.append("TAX INVOICE"+"\n"+item.getIsDuplicate()+"\n");*/

            textData.append("TAX INVOICE"+item.getIsDuplicate()+"\n");

            if (item.getBoldHeader() == 1) {
                method = "addTextSize";
                mPrinter.addTextSize(2, 2);
            }
            else {
                method = "addTextSize";
                mPrinter.addTextSize(1, 1);
                method = "addTextStyle";
                mPrinter.addTextStyle(0, 0, 1, 0);
            }

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                textData.append(item.getHeaderLine1()+"\n");
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                textData.append(item.getHeaderLine2()+"\n");
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                textData.append(item.getHeaderLine3()+"\n");
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                textData.append(item.getHeaderLine4()+"\n");
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                textData.append(item.getHeaderLine5()+"\n");
            }

            textData.append("\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 0, 0);

            if (item.getOwnerDetail() == 1) {
                textData.append("GSTIN       : "+item.getAddressLine1()+"\n");
                textData.append("Name        : "+item.getAddressLine2()+"\n");
                textData.append("Address     : "+item.getAddressLine3()+"\n");
            }

            textData.append("\n");

            textData.append("Bill no         : "+item.getBillNo()+"\n");
            if(item.getBillingMode().equals("1"))
                textData.append("Table       : "+item.getTableNo()+"\n");
            textData.append("Date            : "+item.getDate() +"    Time : "+item.getTime() +"\n");
            textData.append("Cashier         : "+item.getOrderBy()+"\n");

            if (!item.getCustomerName().equals("") && !item.getCustomerName().contains("-")) {
                textData.append("Customer Name   : "+item.getCustomerName()+"\n");
            }
            if (!item.getSalesManId().equals("") && !item.getSalesManId().contains("-")) {
                textData.append("Salesman Id     : "+item.getSalesManId()+"\n");
            }

            if(item.getBillingMode().equalsIgnoreCase("4") || item.getBillingMode().equalsIgnoreCase("3")) {
                textData.append("Payment Status  : " + item.getPaymentStatus()+"\n");
            }

            if (item.getPrintService() == 1) {
                if(item.getBillingMode().equalsIgnoreCase("1") || item.getBillingMode().equalsIgnoreCase("2") ||
                        item.getBillingMode().equalsIgnoreCase("3") || item.getBillingMode().equalsIgnoreCase("4")){
                    textData.append("Service         : "+ item.getStrBillingModeName() + "\n");
                } else {
                    textData.append("-----------" + "\n");
                }
            }

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 1, 0);
            textData.append("================================================"+"\n");

            if(item.getAmountInNextLine() ==0) {
                textData.append("SI  ITEM NAME            QTY     RATE    AMOUNT " + "\n");
                if(item.getHSNPrintEnabled_out()== 1)
                {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        textData.append("HSN       DISC"+"\n");
                    } else {
                        textData.append("HSN"+"\n");
                    }
                } else {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        textData.append("          DISC"+"\n");
                    }
                }
            }
            else {
                if(item.getHSNPrintEnabled_out()== 1)
                {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        textData.append("SI  ITEM NAME            QTY     RATE           ");
                        textData.append("HSN       DISC                          AMOUNT " + "\n");
                    } else {
                        textData.append("SI  ITEM NAME            QTY     RATE           ");
                        textData.append("HSN                                     AMOUNT " + "\n");
                    }
                }
                else {
                    textData.append("SI  ITEM NAME          QTY    RATE            ");
                    textData.append("                                        AMOUNT" + "\n");
                    if (item.getItemWiseDiscountPrint() == 1) {
                        textData.append("SI  ITEM NAME            QTY     RATE            ");
                        textData.append("          DISC                           AMOUNT" + "\n");
                    } else {
                        textData.append("SI  ITEM NAME            QTY     RATE            ");
                        textData.append("                                         AMOUNT" + "\n");
                    }
                }
            }

            textData.append("================================================"+"\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 0, 0);

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
                String HSN = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getHSNCode()),10,1),8,1);
                String itemWiseDisc = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getDiscount()),10,1),10,1);

                String preQty = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.valueOf(billKotItem.getQty())+billKotItem.getUOM(),9,1),10,1);
                String preRate = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getRate()),7,1),8,1);
                String preAmount = "0";
                String pre = "";
                if(String.format("%.2f",billKotItem.getAmount()).length() <= 9)
                {
                    preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getAmount())
                            +billKotItem.getTaxIndex(),9,1),10,1);
                    if(billKotItem.getItemName().length() > 16) {
                        pre = preId + preName.substring(0,16)+" " +/*HSN+*/preQty + preRate + preAmount;
                        textData.append(pre + "\n");
                        if(preName.length() > 32){
                            textData.append("   " + preName.substring(16,32) + "\n");
                        } else {
                            textData.append("   " + preName.substring(16, preName.length()) + "\n");
                        }
                    } else {
                        pre = preId + preName +/*HSN+*/preQty + preRate + preAmount;
                        textData.append(pre + "\n");
                    }
                    if(item.getHSNPrintEnabled_out() == 1) {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            textData.append(HSN + itemWiseDisc +"\n");
                        } else {
                            textData.append(HSN+"\n");
                        }
                    } else {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            textData.append( getFormatedCharacterForPrint_init(itemWiseDisc,20,1) +"\n");
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
                    textData.append(pre+"\n");
                }

                totalitemtypes++;
                totalquantitycount += billKotItem.getQty();
                subtotal += billKotItem.getAmount();
            }

            textData.append("------------------------------------------------"+"\n");
            textData.append(getSpaceFormater("Total Item(s) : "+totalitemtypes+" /Qty : "+totalquantitycount,String.format("%.2f",subtotal),48,1));

            double discount = item.getFdiscount();
            float discountPercentage = item.getdiscountPercentage();
            if(discountPercentage > 0)
            {
                textData.append("\n");
                String DiscName = getPostAddedSpaceFormat("","Discount Amount",23,1);
                String DiscPercent = getPostAddedSpaceFormat("","@ " + String.format("%.2f",discountPercentage) + " %",15,1);
                String DiscValue = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",discount),10,1),8 ,1);
                String pre = DiscName + DiscPercent + DiscValue;
                textData.append(pre+"\n");
            }
            else if (discount > 0)
            {
                textData.append("\n");
                textData.append(getSpaceFormater("Discount Amount",String.format("%.2f",discount),48,1)+"\n");
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
                    String TxValue = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getPrice()),10,1),8 ,1);
                    String pre = TxName + TxPercent + TxValue;
                    textData.append(pre+"\n");
                }
            }

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if(item.getIsInterState().equalsIgnoreCase("n")) // IntraState
            {
                if (it11.hasNext())
                {
                    method = "addTextStyle";
                    mPrinter.addTextStyle(0, 0, 1, 0);
                    textData.append("================================================"+"\n");
                    if(item.getUTGSTEnabled() ==0) // disabled
                        textData.append("Tax(%)   TaxableVal   CGSTAmt  SGSTAmt    TaxAmt"+"\n");
                    else
                        textData.append("Tax(%)   TaxableVal   CGSTAmt  UTGSTAmt   TaxAmt"+"\n");
                    textData.append("================================================"+"\n");

//                    method = "addText";
//                    mPrinter.addText(textData.toString());
//                    textData.delete(0, textData.length());

                    method = "addTextStyle";
                    mPrinter.addTextStyle(0, 0, 0, 0);
                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()),12,1),13,1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getCGSTAmount()),8,1),9,1);
                            String SGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getSGSTAmount()),8,1),9,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),10,1),10,1);

                            String pre = TxIndex + TaxableValue + CGSTAmt+ SGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getCGSTAmount()+billTaxSlabEntry.getSGSTAmount();
                            textData.append(pre+"\n");

                        }
                    }while (it11.hasNext());
                }
            }else // InterState
            {
                if (it11.hasNext())
                {
                    method = "addTextStyle";
                    mPrinter.addTextStyle(0, 0, 1, 0);
                    textData.append("================================================"+"\n");
                    textData.append("Tax(%)   TaxableVal   IGSTAmt             TaxAmt"+"\n");
                    textData.append("================================================"+"\n");
                    method = "addTextStyle";
                    mPrinter.addTextStyle(0, 0, 0, 0);
                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()),12,1),13,1);
                            String IGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getIGSTAmount()),8,1),9,1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init("",8,1),9,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),10,1),10,1);

                            String pre = TxIndex + TaxableValue + IGSTAmt+ CGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getIGSTAmount();
                            textData.append(pre+"\n");

                        }
                    }while (it11.hasNext());
                }
            }

            textData.append("\n");
            double  dtotalcessAmt =0;
            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            Iterator it21 = billcessTaxItems.iterator();
            while (it21.hasNext()) {

                BillServiceTaxItem billKotItem = (BillServiceTaxItem) it21.next();
                if (billKotItem.getServicePercent() > 0 || billKotItem.getServicePrice() > 0){

                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getServiceTxName()), 23, 1);
                    String TxPercent = "";
                    if (billKotItem.getServicePercent() > 0)
                        TxPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", billKotItem.getServicePercent()) + "% ", 15, 1);
                    else
                        TxPercent = getPostAddedSpaceFormat("", "@ Rs." + String.format("%.2f", billKotItem.getPricePerUnit()) + " ", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getServicePrice()), 10, 1), 8, 1);
                    dtotalcessAmt += billKotItem.getServicePrice();
                    String pre = TxName + TxPercent + TxValue;
                    textData.append(pre + "\n");
                }
            }

            double dTotalTaxAmt = dTotTaxAmt +dtotalcessAmt;
            if(dTotalTaxAmt >0)
            {
                textData.append(getSpaceFormater("Total Tax Amount",String.format("%.2f",dTotalTaxAmt),48,1)+"\n");
            }
            textData.append("================================================"+"\n");

            textData.append(getSpaceFormater("TOTAL",String.format("%.2f",item.getNetTotal()),48,1)+"\n");
            if(item.getCardPaymentValue()>0 || item.geteWalletPaymentValue()>0 ||
                    item.getCouponPaymentValue()>0 || item.getPettyCashPaymentValue()>0 || item.getRewardPoints()>0 || item.getCashPaymentValue()>0
                    || item.getAepsPaymentValue()>0 || item.getDblMSwipeVale() > 0 || item.getDblPaytmValue() > 0){
                textData.append("================================================"+"\n");
                if(item.getCardPaymentValue()>0)
                    textData.append(getSpaceFormater("OtherCard Payment",String.format("%.2f",item.getCardPaymentValue()),48,1)+"\n");
                if(item.geteWalletPaymentValue()>0)
                    textData.append(getSpaceFormater("eWallet Payment",String.format("%.2f",item.geteWalletPaymentValue()),48,1)+"\n");
                if(item.getCouponPaymentValue()>0)
                    textData.append(getSpaceFormater("Coupon Payment",String.format("%.2f",item.getCouponPaymentValue()),48,1)+"\n");
                if(item.getPettyCashPaymentValue()>0)
                    textData.append(getSpaceFormater("PettyCash Payment",String.format("%.2f",item.getPettyCashPaymentValue()),48,1)+"\n");
                if(item.getCashPaymentValue()>0)
                    textData.append(getSpaceFormater("Cash Payment",String.format("%.2f",item.getCashPaymentValue()),48,1)+"\n");
                if(item.getRewardPoints()>0)
                    textData.append(getSpaceFormater("Reward Pt Payment",String.format("%.2f",item.getRewardPoints()),48,1)+"\n");
                if(item.getAepsPaymentValue()>0)
                    textData.append(getSpaceFormater("AEPS Payment",String.format("%.2f",item.getAepsPaymentValue()),48,1)+"\n");
                if(item.getDblMSwipeVale()>0)
                    textData.append(getSpaceFormater("MSwipe Payment",String.format("%.2f",item.getDblMSwipeVale()),48,1)+"\n");
                if(item.getDblPaytmValue()>0)
                    textData.append(getSpaceFormater("Paytm Payment",String.format("%.2f",item.getDblPaytmValue()),48,1)+"\n");
            }

            if (item.getChangePaymentValue()>0) {
                textData.append("------------------------------------------------"+"\n");
                textData.append(getSpaceFormater("Due amount",String.format("%.2f",item.getChangePaymentValue()),48,1)+"\n");
            }

            textData.append("================================================"+"\n");

            if(item.getRoundOff() > 0){
                textData.append(getSpaceFormater("Total Roundoff to 1.00 ","",48,1)+"\n");
                textData.append("================================================"+"\n");
            }

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            if(!item.getFooterLine1().equals(""))
                textData.append(item.getFooterLine1()+"\n");
            if(!item.getFooterLine2().equals(""))
                textData.append(item.getFooterLine2()+"\n");
            if(!item.getFooterLine3().equals(""))
                textData.append(item.getFooterLine3()+"\n");
            if(!item.getFooterLine4().equals(""))
                textData.append(item.getFooterLine4()+"\n");
            if(!item.getFooterLine5().equals(""))
                textData.append(item.getFooterLine5()+"\n");

            method = "addFeedLine";
            mPrinter.addFeedLine(1);

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

//            method = "addBarcode";
//            mPrinter.addBarcode("01209457",
//                    Printer.BARCODE_CODE39,
//                    Printer.HRI_BELOW,
//                    Printer.FONT_A,
//                    barcodeWidth,
//                    barcodeHeight);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;

        return true;
    }

    private boolean createKotData(PrintKotBillItem item) {
        String method = "";
        String tblno = "", modename = "";
        StringBuilder stringBuilder = new StringBuilder();

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 1, 0);
            if(item.getBillingMode().equalsIgnoreCase("1")){
                tblno = "Table # "+item.getTableNo() + " | ";
                modename = item.getStrBillingModeName();
            } else {
                tblno = "";
                modename = item.getStrBillingModeName();
            }
            stringBuilder.append(modename+"\n");
            stringBuilder.append(tblno +"KOT # "+item.getBillNo()+"\n");

            mPrinter.addText(stringBuilder.toString());
            stringBuilder.delete(0, stringBuilder.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 0, 0);

            stringBuilder.append("=============================================\n");
//            stringBuilder.append("Bill Number : "+item.getBillNo()+"\n");
            stringBuilder.append("Attendant   : "+item.getOrderBy()+"\n");
            if ((item.getWaiterName() != null) && !(item.getWaiterName().equals("")))
                stringBuilder.append("Waiter      : "+item.getWaiterName()+"\n");
            stringBuilder.append("Date        : "+item.getDate() +" | "+"Time : "+item.getTime()+"\n");
            stringBuilder.append("=============================================\n");
            stringBuilder.append("SNo        NAME                    QTY"+"\n");
            stringBuilder.append("=============================================\n");
            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            while (it.hasNext())
            {
                BillKotItem billKotItem = (BillKotItem) it.next();
                int id = billKotItem.getItemId();
                String name = getFormatedCharacterForPrint(billKotItem.getItemName(),16,1);
                String qty = billKotItem.getQty()+"";
                String pre = getPostAddedSpaceFormat("",String.valueOf(id),10,1)+name;
                stringBuilder.append(getPreAddedSpaceFormat(pre,qty,38,1)+"\n");
            }
            stringBuilder.append("=============================================\n");

            method = "addFeedLine";
            mPrinter.addFeedLine(1);

            method = "addText";
            mPrinter.addText(stringBuilder.toString());
            stringBuilder.delete(0, stringBuilder.length());

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        stringBuilder = null;

        return true;
    }

    public String getPreAddedSpaceFormat(String sourceTxt, String toAddTxt, int max,int type)
    {
        return sourceTxt+getSpaces(max-(sourceTxt.length()+toAddTxt.length()),type)+toAddTxt;
    }

    private boolean createSalesReturnData(SalesReturnPrintBean item) {
        String method = "";

        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 1, 0);

            textData.append(item.getIsDuplicate());
            textData.append("\n");
            textData.append("\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 0, 0);

            textData.append("GSTIN     : "+item.getStrOwnerGSTIN()+"\n");
            textData.append("Name      : "+item.getStrOwnerName()+"\n");
            textData.append("Address   : "+item.getStrOwnerAddress()+"\n");
            textData.append("\n");
            textData.append("Bill No.  : "+item.getStrInvoiceNo()+"\n");
            textData.append("Bill Date : "+item.getStrInvoiceDate()+"\n");
            textData.append("\n");

            textData.append("CDNI      : "+item.getiSrId()+"\n");
            textData.append("Note Date : "+item.getStrSalesReturnDate() +"\n");
            if (!item.getStrCustName().equals("") && !item.getStrCustName().contains("-")) {
                textData.append("Customer  : "+item.getStrCustName()+"\n");
            }

            textData.append("Notes     : "+item.getStrReason()+"\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 1, 0);
            textData.append("================================================"+"\n");
            textData.append("ITEM NAME              QTY               AMOUNT" + "\n");
            textData.append("================================================"+"\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 0, 0);

            ArrayList<SalesReturnBean> salesReturnItems = item.getArrayList();
            Iterator it = salesReturnItems.iterator();
            int totalitemtypes =0;

            while (it.hasNext())
            {

                SalesReturnBean bean = (SalesReturnBean) it.next();

                String preName = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(bean.getStrItemName()),bean.getStrItemName().length(),1),17,1);
                String preQty = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.valueOf(bean.getDblReturnQuantity()),9,1),10,1);
                String preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f", bean.getDblReturnAmount()),20,1),10,1);
                String pre = "";
                pre = preName+ preQty + preAmount;
                textData.append(pre+"\n");

                totalitemtypes++;
            }

            textData.append("------------------------------------------------"+"\n");
            textData.append(getSpaceFormater("Total Item(s) : "+totalitemtypes,"",48,1));

            // Tax Slab
            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if(item.getIsInterstate() == 0) // IntraState
            {
                if (it11.hasNext())
                {
                    textData.append("================================================"+"\n");
                    textData.append("Tax(%)        CGSTAmt     SGSTAmt         TaxAmt"+"\n");
                    textData.append("================================================"+"\n");
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
                            textData.append(pre+"\n");

                        }
                    }while (it11.hasNext());
                }
            }else // InterState
            {
                if (it11.hasNext())
                {
                    textData.append("================================================"+"\n");
                    textData.append("Tax(%)               IGSTAmt              TaxAmt"+"\n");
                    textData.append("================================================"+"\n");
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
                            textData.append(pre+"\n");

                        }
                    }while (it11.hasNext());
                }
            }
            textData.append("\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            double  dtotalcessAmt =0;
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
                    textData.append(pre + "\n");
                }
            }

            textData.append("------------------------------------------------"+"\n");

            if(dTotTaxAmt >0)
            {
                textData.append(getSpaceFormater("Total Tax Amount",String.format("%.2f",dTotTaxAmt+dtotalcessAmt),48,1)+"\n");
            }
            textData.append(getSpaceFormater("Total Invoice Amount",String.format("%.2f",item.getDblAmount()),48,1)+"\n");
            textData.append(getSpaceFormater("Total Return Amount",String.format("%.2f",item.getDblReturnAmount()),48,1)+"\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addFeedLine";
            mPrinter.addFeedLine(1);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;

        return true;
    }

    private boolean createCumulativeReportData(List<List<String>> itemReport, String reportName) {
        String method = "";
        StringBuilder textData = new StringBuilder();

        try{

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 0, 0);

            textData.append(reportName+"\n");

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            textData.append("==============================================="+"\n");
            Calendar time = Calendar.getInstance();
            textData.append("["+String.format("%tR", time)+"]\n");
            textData.append("\n");

            int j;
            for(j=0;j<itemReport.size();j++)
            {
                if(j == itemReport.size() - 1){
                    textData.append("==============================================="+"\n");
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
                        method = "addTextAlign";
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        textData.append(sb.toString()+"\n");
                        sb = new StringBuffer();
                    }

                    if(rem != 0 && (arrayListColumn.size()-1)==i)
                    {
                        method = "addTextAlign";
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        textData.append(getAbsoluteCharacter1(sb.toString(),1)+"\n");
                        sb = new StringBuffer();
                    }
                /*else if(i == arrayListColumn.size())
                {
                    tmpList.add(CreateBitmap.AddText(18, "Bold","MONOSPACE", sb.toString(), TextAlign.LEFT,getApplicationContext()));
                    sb = new StringBuffer();
                }*/
                }
                if(j==1)
                    textData.append("==============================================="+"\n");
            }
            textData.append("==============================================="+"\n");
            mPrinter.addFeedLine(2);
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            /*Vector<Byte> datas = esc.getCommand();
            Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
            byte[] bytes = ArrayUtils.toPrimitive(Bytes);
            String str = Base64.encodeToString(bytes, Base64.DEFAULT);*/

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);

        } catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;
        return true;
    }

    public boolean createReportData(List<List<String>> itemReport, String reportName) {

        String method = "";
        StringBuilder textData = new StringBuilder();

        try{

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 0, 0);

            textData.append(reportName+"\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            textData.append("============================================"+"\n");
            Calendar time = Calendar.getInstance();
            textData.append("["+String.format("%tR", time)+"]\n");
            textData.append("\n");

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
                        method = "addTextAlign";
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        textData.append(sb.toString()+"\n");
                        sb = new StringBuffer();
                    }

                    if(rem != 0 && (arrayListColumn.size()-1)==i)
                    {
                        method = "addTextAlign";
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        textData.append(getAbsoluteCharacter1(sb.toString(),1)+"\n");
                        sb = new StringBuffer();
                    }
                /*else if(i == arrayListColumn.size())
                {
                    tmpList.add(CreateBitmap.AddText(18, "Bold","MONOSPACE", sb.toString(), TextAlign.LEFT,getApplicationContext()));
                    sb = new StringBuffer();
                }*/
                }
                if(j==0)
                    textData.append("============================================"+"\n");
            }
            textData.append("============================================"+"\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            mPrinter.addFeedLine(2);
           /* Vector<Byte> datas = esc.getCommand();
            Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
            byte[] bytes = ArrayUtils.toPrimitive(Bytes);
            String str = Base64.encodeToString(bytes, Base64.DEFAULT);
            return str;*/

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);

        } catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;
        return true;
    }

    private boolean createReceiptData(PaymentReceipt item) {

        String method = "";
        StringBuilder textData = new StringBuilder();

        try{

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 1, 0);

            if (item.getiBillType() == 1) {
                if(item.getIsDuplicate().equals(""))
                    textData.append("PAYMENT"+"\n");
                else
                    textData.append("PAYMENT"+"\n"+item.getIsDuplicate()+"\n");
            } else {
                if(item.getIsDuplicate().equals(""))
                    textData.append("RECEIPT"+"\n");
                else
                    textData.append("RECEIPT"+"\n"+item.getIsDuplicate()+"\n");
            }

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            if (item.getHeaderPrintBold() == 1) {
                method = "addTextSize";
                mPrinter.addTextSize(2, 2);
            }
            else {
                method = "addTextSize";
                mPrinter.addTextSize(1, 1);
                method = "addTextStyle";
                mPrinter.addTextStyle(0, 0, 1, 0);
            }

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                textData.append(item.getHeaderLine1()+"\n");
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                textData.append(item.getHeaderLine2()+"\n");
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                textData.append(item.getHeaderLine3()+"\n");
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                textData.append(item.getHeaderLine4()+"\n");
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                textData.append(item.getHeaderLine5()+"\n");
            }

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 0, 0);

            textData.append("============================================"+"\n");
            if (item.getiBillType() == 1) {
                textData.append("Payment No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n");
            }
            else {
                textData.append("Receipt No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n");
            }
            textData.append("Invoice Date    : "+item.getStrDate()+"\n");
            textData.append("Description     : "+item.getDescriptionText()+"\n");
            textData.append("Amount          : "+String.valueOf(item.getdAmount())+"\n");
            textData.append("Reason          : "+item.getStrReason()+"\n");
            textData.append("============================================"+"\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

           /* method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 1, 0);
            if(item.getFooterLine1()!=null && !item.getFooterLine1().equals(""))
                textData.append(item.getFooterLine1()+"\n");
            if(item.getFooterLine2()!=null && !item.getFooterLine2().equals(""))
                textData.append(item.getFooterLine2()+"\n");
            if(item.getFooterLine3()!=null && !item.getFooterLine3().equals(""))
                textData.append(item.getFooterLine3()+"\n");
            if(item.getFooterLine4()!=null && !item.getFooterLine4().equals(""))
                textData.append(item.getFooterLine4()+"\n");
            if(item.getFooterLine5()!=null && !item.getFooterLine5().equals(""))
                textData.append(item.getFooterLine5()+"\n");*/

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            mPrinter.addFeedLine(2);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);

        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;

        return true;
    }

    private boolean createDepositReceiptData(Customer item) {

        String method = "";
        StringBuilder textData = new StringBuilder();

        try{

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 1, 0);

            textData.append("Deposit Amount Receipt"+"\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            if (item.getHeaderPrintBold() == 1) {
                method = "addTextSize";
                mPrinter.addTextSize(2, 2);
            }
            else {
                method = "addTextSize";
                mPrinter.addTextSize(1, 1);
                method = "addTextStyle";
                mPrinter.addTextStyle(0, 0, 1, 0);
            }

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                textData.append(item.getHeaderLine1()+"\n");
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                textData.append(item.getHeaderLine2()+"\n");
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                textData.append(item.getHeaderLine3()+"\n");
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                textData.append(item.getHeaderLine4()+"\n");
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                textData.append(item.getHeaderLine5()+"\n");
            }

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 0, 0);

            textData.append("============================================"+"\n");
            textData.append("Deposit Date    : "+item.getBusinessDate()+"\n");
            textData.append("Customer Name   : "+item.getStrCustName()+"\n");
            textData.append("Mobile No.      : "+item.getStrCustContactNumber()+"\n");
            textData.append("Deposit Amount  : "+item.getDblDepositAmt()+"\n");
            textData.append("Credit Amount   : "+item.getdCreditAmount()+"\n");
            textData.append("============================================"+"\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addTextStyle";
            mPrinter.addTextStyle(0, 0, 1, 0);

            if(item.getFooterLine1()!=null && !item.getFooterLine1().equals(""))
                textData.append(item.getFooterLine1()+"\n");
            if(item.getFooterLine2()!=null && !item.getFooterLine2().equals(""))
                textData.append(item.getFooterLine2()+"\n");
            if(item.getFooterLine3()!=null && !item.getFooterLine3().equals(""))
                textData.append(item.getFooterLine3()+"\n");
            if(item.getFooterLine4()!=null && !item.getFooterLine4().equals(""))
                textData.append(item.getFooterLine4()+"\n");
            if(item.getFooterLine5()!=null && !item.getFooterLine5().equals(""))
                textData.append(item.getFooterLine5()+"\n");

           /* esc.addPrintAndFeedLines((byte)3);
            Vector<Byte> datas = esc.getCommand();
            Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
            byte[] bytes = ArrayUtils.toPrimitive(Bytes);
            String str = Base64.encodeToString(bytes, Base64.DEFAULT);
            return str;*/

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            mPrinter.addFeedLine(2);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);

        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;

        return true;
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
                sb.append(mContext.getResources().getString(R.string.superSpace));
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

    /*private boolean runPrintCouponSequence() {
        if (!initializeObject()) {
            return false;
        }

        if (!createCouponData()) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }

        return true;
    }*/

    /*private boolean createCouponData() {
        String method = "";
        Bitmap coffeeData = BitmapFactory.decodeResource(getResources(), R.drawable.coffee);
        Bitmap wmarkData = BitmapFactory.decodeResource(getResources(), R.drawable.wmark);

        final int barcodeWidth = 2;
        final int barcodeHeight = 64;
        final int pageAreaHeight = 500;
        final int pageAreaWidth = 500;
        final int fontAHeight = 24;
        final int fontAWidth = 12;
        final int barcodeWidthPos = 110;
        final int barcodeHeightPos = 70;

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addPageBegin";
            mPrinter.addPageBegin();

            method = "addPageArea";
            mPrinter.addPageArea(0, 0, pageAreaWidth, pageAreaHeight);

            method = "addPageDirection";
            mPrinter.addPageDirection(Printer.DIRECTION_TOP_TO_BOTTOM);

            method = "addPagePosition";
            mPrinter.addPagePosition(0, coffeeData.getHeight());

            method = "addImage";
            mPrinter.addImage(coffeeData, 0, 0, coffeeData.getWidth(), coffeeData.getHeight(), Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, 3, Printer.PARAM_DEFAULT);

            method = "addPagePosition";
            mPrinter.addPagePosition(0, wmarkData.getHeight());

            method = "addImage";
            mPrinter.addImage(wmarkData, 0, 0, wmarkData.getWidth(), wmarkData.getHeight(), Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);

            method = "addPagePosition";
            mPrinter.addPagePosition(fontAWidth * 4, (pageAreaHeight / 2) - (fontAHeight * 2));

            method = "addTextSize";
            mPrinter.addTextSize(3, 3);

            method = "addTextStyle";
            mPrinter.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.TRUE, Printer.PARAM_DEFAULT);

            method = "addTextSmooth";
            mPrinter.addTextSmooth(Printer.TRUE);

            method = "addText";
            mPrinter.addText("FREE Coffee\n");

            method = "addPagePosition";
            mPrinter.addPagePosition((pageAreaWidth / barcodeWidth) - barcodeWidthPos, coffeeData.getHeight() + barcodeHeightPos);

            method = "addBarcode";
            mPrinter.addBarcode("01234567890", Printer.BARCODE_UPC_A, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, barcodeWidth, barcodeHeight);

            method = "addPageEnd";
            mPrinter.addPageEnd();

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        return true;
    }*/

    private boolean printData(int printCount) {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        if (!isPrintable(status)) {
            ShowMsg.showMsg("Printer Error", makeErrorMessage(status), mContext);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {

            if (printCount == 1) {
                mPrinter.sendData(Printer.PARAM_DEFAULT);
                Thread.sleep(3000);
            } else {
                for (int i =0;i<printCount; i++) {
                    mPrinter.sendData(Printer.PARAM_DEFAULT);
                    Thread.sleep(3000);
                }
            }
        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            if (((Epos2Exception) e).getErrorStatus() != Epos2Exception.ERR_PROCESSING)
                ShowMsg.showException(e, "sendData", mContext);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
            if (mPrinter.getStatus().getConnection() == Printer.TRUE) {
                try {
                    mPrinter.disconnect();
                }
                catch (Exception ex) {
                    // Do nothing
                }
            }
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_T82,
                    Printer.MODEL_ANK,
                    mContext);
        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, "Printer", mContext);
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        if (mPrinter == null) {
            return;
        }

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect(mTarget, Printer.PARAM_DEFAULT);

            mPrinter.setInterval(3000);
            mPrinter.startMonitor();
        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showConnectionError(e, "connect", mContext);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            printerConnectionError.onError(mPrinter, ((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
            ShowMsg.showException(e, "beginTransaction", mContext);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.stopMonitor();
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
//                    printerConnectionError.onError(((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
                    ShowMsg.showException(e, "endTransaction", mContext);
                }
            });
        }

        try {
            mPrinter.stopMonitor();
            mPrinter.disconnect();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
//                    printerConnectionError.onError(((Epos2Exception) e).getErrorStatus(), ShowMsg.getEposExceptionText(((Epos2Exception) e).getErrorStatus()));
                    ShowMsg.showException(e, "disconnect", mContext);
                }
            });
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }
        else {
            ;//print available
        }

        return true;
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += mContext.getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += mContext.getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += mContext.getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += mContext.getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += mContext.getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += mContext.getString(R.string.handlingmsg_err_autocutter);
            msg += mContext.getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += mContext.getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += mContext.getString(R.string.handlingmsg_err_overheat);
                msg += mContext.getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += mContext.getString(R.string.handlingmsg_err_overheat);
                msg += mContext.getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += mContext.getString(R.string.handlingmsg_err_overheat);
                msg += mContext.getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += mContext.getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += mContext.getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    /*private void dispPrinterWarnings(PrinterStatusInfo status) {
        EditText edtWarnings = (EditText)findViewById(R.id.edtWarnings);
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
        }

        edtWarnings.setText(warningsMsg);
    }*/

    private void updateButtonState(boolean state) {
        /*Button btnReceipt = (Button)findViewById(R.id.btnSampleReceipt);
        Button btnCoupon = (Button)findViewById(R.id.btnSampleCoupon);
        btnReceipt.setEnabled(state);
        btnCoupon.setEnabled(state);*/
    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {

                if (code != Epos2CallbackCode.CODE_SUCCESS) {
                    ShowMsg.showResult(code, makeErrorMessage(status), mContext);
                    printerConnectionError.onError(mPrinter, code, makeErrorMessage(status));
                }

//                dispPrinterWarnings(status);

                updateButtonState(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

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
}
