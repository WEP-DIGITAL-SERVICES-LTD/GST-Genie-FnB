package com.wepindia.printers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.epson.EpsonCom.CallbackInterface;
import com.epson.EpsonCom.EpsonCom;
import com.epson.EpsonCom.EpsonComCallbackInfo;
import com.epson.EpsonCom.EpsonComDevice;
import com.epson.EpsonCom.EpsonComDeviceParameters;
import com.gprinter.command.EscCommand;
import com.wep.common.app.Database.Customer;
import com.wep.common.app.Database.PaymentReceipt;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.print.BillKotItem;
import com.wep.common.app.print.BillServiceTaxItem;
import com.wep.common.app.print.BillTaxItem;
import com.wep.common.app.print.BillTaxSlab;
import com.wep.common.app.print.PrintKotBillItem;
import com.wepindia.printers.utils.PrinterUtil;
import com.wepindia.printers.wep.PrinterConnectionError;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class WePTHPrinterBaseActivity extends WepBaseActivity {


    private Context mContext;
    private String mTarget = null;
    private boolean DEBUG = false;
    protected PrinterUtil printerUtil;
    private EpsonComDevice printer = null;
    private EpsonComDeviceParameters devParams = null;
    private PrinterConnectionError printerConnectionError = null;

    public void setmTarget(String mTarget) {
        this.mTarget = mTarget;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void mInitListener(PrinterConnectionError printerConnectionError){
        this.printerConnectionError = printerConnectionError;
    }

    private EpsonComDevice getmWEPPrinter(){

        if (printer == null)
            printer = new EpsonComDevice();

        if (devParams == null)
            devParams = new EpsonComDeviceParameters();

        devParams.PortType = EpsonCom.PORT_TYPE.USB;
        devParams.PortName = mTarget;
        devParams.ApplicationContext = mContext;

        printer.setDeviceParameters(devParams);
        printer.registerCallback(callbackInterface);

        printerUtil = new PrinterUtil(mContext);

        return printer;
    }

    private boolean initializeObject() {
        try {
            getmWEPPrinter();
        }
        catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            return false;
        }

        return true;
    }

    private boolean connectPrinter() {
        if (printer == null) {
            return false;
        }

        try {
            if (EpsonCom.ERROR_CODE.SUCCESS != printer.openDevice()) {
                return false;
            }
        }
        catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean runPrintBillSequence(PrintKotBillItem item, int printCount) {
        if (!initializeObject()) {
            return false;
        }

        if (!printBillData(item, printCount)) {
            finalizeObject();
            return false;
        }

        return true;
    }

    public boolean runPrintKOTSequence(PrintKotBillItem item, int printCount) {
        if (!initializeObject()) {
            return false;
        }

        if (!printKOTData(item, printCount)) {
            finalizeObject();
            return false;
        }

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

    public boolean runPrintReportSequence(ArrayList<ArrayList<String>> Report, String ReportName, String type) {
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

    public boolean printReportData(ArrayList<ArrayList<String>> Report, String ReportName, String type) {
        if (printer == null) {
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
            printerConnectionError.onError(e.getMessage());
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    private void sendReportData(ArrayList<ArrayList<String>> itemReport, String reportName, String type){

        try{

            EscCommand esc = new EscCommand();
            esc.addPrintAndFeedLines((byte)1);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addText(reportName+"\n");
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("==============================================="+"\n");
            Calendar time = Calendar.getInstance();
            esc.addText("["+String.format("%tR", time)+"]\n");
            esc.addPrintAndLineFeed();

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
                        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
                        esc.addText(sb.toString()+"\n");
                        sb = new StringBuffer();
                    }

                    if(rem != 0 && (arrayListColumn.size()-1)==i)
                    {
                        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
                        esc.addText(getAbsoluteCharacter1(sb.toString(),1)+"\n");
                        sb = new StringBuffer();
                    }
                /*else if(i == arrayListColumn.size())
                {
                    tmpList.add(CreateBitmap.AddText(18, "Bold","MONOSPACE", sb.toString(), TextAlign.LEFT,getApplicationContext()));
                    sb = new StringBuffer();
                }*/
                }
                if(j==0)
                    esc.addText("==============================================="+"\n");
            }
            esc.addText("==============================================="+"\n");
            esc.addPrintAndFeedLines((byte)3);
            Vector<Byte> datas = esc.getCommand();
            printer.sendData(datas);
            printer.cutPaper();

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
    }

    private void sendCumulativeReportData(ArrayList<ArrayList<String>> itemReport, String reportName, String type){

        try{

            EscCommand esc = new EscCommand();
            esc.addPrintAndFeedLines((byte)1);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addText(reportName+"\n");
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("==============================================="+"\n");
            Calendar time = Calendar.getInstance();
            esc.addText("["+String.format("%tR", time)+"]\n");
            esc.addPrintAndLineFeed();

            int j;
            for(j=0;j<itemReport.size();j++)
            {
                if(j == itemReport.size() - 1){
                    esc.addText("==============================================="+"\n");
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
                        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
                        esc.addText(sb.toString()+"\n");
                        sb = new StringBuffer();
                    }

                    if(rem != 0 && (arrayListColumn.size()-1)==i)
                    {
                        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
                        esc.addText(getAbsoluteCharacter1(sb.toString(),1)+"\n");
                        sb = new StringBuffer();
                    }
                /*else if(i == arrayListColumn.size())
                {
                    tmpList.add(CreateBitmap.AddText(18, "Bold","MONOSPACE", sb.toString(), TextAlign.LEFT,getApplicationContext()));
                    sb = new StringBuffer();
                }*/
                }
                if(j==1)
                    esc.addText("==============================================="+"\n");
            }
            esc.addText("==============================================="+"\n");
            esc.addPrintAndFeedLines((byte)3);
            Vector<Byte> datas = esc.getCommand();
            printer.sendData(datas);
            printer.cutPaper();

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
    }

    private boolean printReceiptData(PaymentReceipt item) {
        if (printer == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {

//            sendReceiptData(item);
        }
        catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    /*private void sendReceiptData(PaymentReceipt item){

        try{

            EscCommand esc = new EscCommand();
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            if (item.getiBillType() == 1) {
                if(item.getIsDuplicate().equals(""))
                    esc.addText("PAYMENT"+"\n");
                else
                    esc.addText("PAYMENT"+"\n"+item.getIsDuplicate()+"\n");
            } else {
                if(item.getIsDuplicate().equals(""))
                    esc.addText("RECEIPT"+"\n");
                else
                    esc.addText("RECEIPT"+"\n"+item.getIsDuplicate()+"\n");
            }

            if (item.getHeaderPrintBold() == 1)
                esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                esc.addText(item.getHeaderLine1()+"\n");
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                esc.addText(item.getHeaderLine2()+"\n");
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                esc.addText(item.getHeaderLine3()+"\n");
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                esc.addText(item.getHeaderLine4()+"\n");
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                esc.addText(item.getHeaderLine5()+"\n");
            }

            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

            esc.addText("================================================"+"\n");
            if (item.getiBillType() == 1) {
                esc.addText("Payment No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n");
            }
            else {
                esc.addText("Receipt No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n");
            }
            esc.addText("Invoice Date    : "+item.getStrDate()+"\n");
            esc.addText("Description     : "+item.getDescriptionText()+"\n");
            esc.addText("Amount          : "+String.valueOf(item.getdAmount())+"\n");
            esc.addText("Reason          : "+item.getStrReason()+"\n");
            esc.addText("================================================"+"\n");
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            if(item.getFooterLine1()!=null && !item.getFooterLine1().equals(""))
                esc.addText(item.getFooterLine1()+"\n");
            if(item.getFooterLine2()!=null && !item.getFooterLine2().equals(""))
                esc.addText(item.getFooterLine2()+"\n");
            if(item.getFooterLine3()!=null && !item.getFooterLine3().equals(""))
                esc.addText(item.getFooterLine3()+"\n");
            if(item.getFooterLine4()!=null && !item.getFooterLine4().equals(""))
                esc.addText(item.getFooterLine4()+"\n");
            if(item.getFooterLine5()!=null && !item.getFooterLine5().equals(""))
                esc.addText(item.getFooterLine5()+"\n");

            esc.addPrintAndFeedLines((byte)3);
            Vector<Byte> datas = esc.getCommand();
            printer.sendData(datas);
            printer.cutPaper();

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
    }*/

    private boolean printDepositReceiptData(Customer item) {
        if (printer == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {

//            sendDepositReceiptData(item);
        }
        catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    private boolean printBillData(PrintKotBillItem item, int printCount) {
        if (printer == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {

            if (printCount == 1) {

                getPrintBill(item);

            } else {
                for (int i =0;i<printCount; i++) {
                    getPrintBill(item);
                }
            }
        }
        catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    private boolean printKOTData(PrintKotBillItem item, int printCount) {
        if (printer == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {

            if (printCount == 1) {

                getPrintKOT(item);

            } else {
                for (int i =0;i<printCount; i++) {
                    getPrintKOT(item);
                }
            }
        }
        catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        finally {
//            if (mIsConnected) {
            try {
                printer.closeDevice();
            }
            catch (Exception ex) {
                // Do nothing
            }
//            }
        }

        return true;
    }

    public void getPrintBill(PrintKotBillItem item) {

        try{

            EscCommand esc = new EscCommand();
            //esc.addPrintAndFeedLines((byte)2);
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            esc.addText("TAX INVOICE"+item.getIsDuplicate()+"\n");

            if (item.getBoldHeader() == 1)
                esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                esc.addText(item.getHeaderLine1()+"\n");
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                esc.addText(item.getHeaderLine2()+"\n");
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                esc.addText(item.getHeaderLine3()+"\n");
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                esc.addText(item.getHeaderLine4()+"\n");
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                esc.addText(item.getHeaderLine5()+"\n");
            }

            esc.addPrintAndLineFeed();
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);

            if (item.getOwnerDetail() == 1) {
                esc.addText("GSTIN     : "+item.getAddressLine1()+"\n");
                esc.addText("Name      : "+item.getAddressLine2()+"\n");
                esc.addText("Address   : "+item.getAddressLine3()+"\n");
                esc.addPrintAndLineFeed();
            }

            esc.addText("Bill no         : "+item.getBillNo()+"\n");
            if(item.getBillingMode().equals("1"))
                esc.addText("Table           : "+item.getTableNo()+"\n");
            esc.addText("Date            : "+item.getDate() +"    Time : "+item.getTime() +"\n");
       /* esc.addText("Date            : "+item.getDate() +"\n");
        esc.addText("Time            : "+item.getTime() +"\n");*/
            esc.addText("Cashier         : "+item.getOrderBy()+"\n");

            if (!item.getCustomerName().equals("") && !item.getCustomerName().contains("-")) {
                esc.addText("Customer Name   : "+item.getCustomerName()+"\n");
            }

            if(item.getBillingMode().equalsIgnoreCase("4") || item.getBillingMode().equalsIgnoreCase("3")) {
                esc.addText("Payment Status  : " + item.getPaymentStatus()+"\n");
            }

            if (item.getPrintService() == 1) {
                if(item.getBillingMode().equalsIgnoreCase("1") || item.getBillingMode().equalsIgnoreCase("2") ||
                        item.getBillingMode().equalsIgnoreCase("3") || item.getBillingMode().equalsIgnoreCase("4")){
                    esc.addText("Service         : "+ item.getStrBillingModeName() + "\n");
                } else {
                    esc.addText("-----------" + "\n");
                }
            }

            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            esc.addText("================================================"+"\n");
        /*if(item.getAmountInNextLine() ==0)
            esc.addText("SI  ITEM NAME            QTY     RATE    AMOUNT "+"\n");
        else
            esc.addText("SI  ITEM NAME            QTY     RATE    \nAMOUNT "+"\n");
        if(item.getHSNPrintEnabled_out()== 1)
        {
            esc.addText("HSN"+"\n");
        }*/
            if(item.getAmountInNextLine() ==0) {
                esc.addText("SI  ITEM NAME            QTY     RATE    AMOUNT " + "\n");
                if(item.getHSNPrintEnabled_out()== 1)
                {
                    esc.addText("HSN"+"\n");
                }
            }
            else {
                if(item.getHSNPrintEnabled_out()== 1)
                {
                    esc.addText("SI  ITEM NAME            QTY     RATE           ");
                    esc.addText("HSN                                     AMOUNT " + "\n");
                }
                else {
                    esc.addText("SI  ITEM NAME            QTY     RATE            ");
                    esc.addText("                                         AMOUNT" + "\n");
                }
            }

            esc.addText("================================================"+"\n");
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            int totalitemtypes =0, totalquantitycount =0;
            double subtotal =0;
            while (it.hasNext())
            {

                BillKotItem billKotItem = (BillKotItem) it.next();

                String preId = getPostAddedSpaceFormat("",String.valueOf(billKotItem.getItemId()),3,1);
                String preName = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getItemName()),16,1),17,1);
                String HSN = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getHSNCode()),8,1),8,1);

                String preQty = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.valueOf(billKotItem.getQty())+billKotItem.getUOM(),9,1),10,1);
                String preRate = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getRate()),7,1),8,1);
                String preAmount = "0";
                String pre = "";
                if(item.getAmountInNextLine() ==0)
                {
                    preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getAmount())
                            +billKotItem.getTaxIndex(),9,1),10,1);
                    pre = preId+preName+/*HSN+*/preQty+preRate+preAmount;
                    esc.addText(pre+"\n");
                    if(item.getHSNPrintEnabled_out() == 1) {
                        esc.addText(HSN+"\n");
                    }
                }
                else // item.getAmountInNextLine() ==1
                {
                    preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getAmount())
                            +billKotItem.getTaxIndex(),(String.format("%.2f",billKotItem.getAmount()) +billKotItem.getTaxIndex() ).length(),1),(String.format("%.2f",billKotItem.getAmount()) +billKotItem.getTaxIndex() ).length(),1);
                    String pre2 = "", pre3 ="";
                    if(item.getHSNPrintEnabled_out() == 1) {
                        pre2 = getPostAddedSpaceFormat("",HSN,8,1);
                        pre3 = getFormatedCharacterForPrint_init(preAmount,40,1);
                    }else
                    {
                        pre3 = getFormatedCharacterForPrint_init(preAmount,48,1);
                    }
                    pre = preId+preName+/*HSN+*/preQty+preRate+"\n"+pre2+pre3;
                    esc.addText(pre+"\n");
                }



                totalitemtypes++;
                totalquantitycount += billKotItem.getQty();
                subtotal += billKotItem.getAmount();
            }
            esc.addText("------------------------------------------------"+"\n");
            esc.addText(getSpaceFormater("Total Item(s) : "+totalitemtypes+" /Qty : "+totalquantitycount,String.format("%.2f",subtotal),48,1)+"\n");
            float discount = item.getFdiscount();
            float discountPercentage = item.getdiscountPercentage();
            if(discountPercentage > 0)
            {
                String DiscName = getPostAddedSpaceFormat("","Discount Amount",23,1);
                String DiscPercent = getPostAddedSpaceFormat("","@ " + String.format("%.2f",discountPercentage) + " %",15,1);
                String DiscValue = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",discount),10,1),8 ,1);
                String pre = DiscName + DiscPercent + DiscValue;
                esc.addText(pre+"\n");
            }
            else if (discount > 0)
            {
                esc.addText(getSpaceFormater("Discount Amount",String.format("%.2f",discount),48,1)+"\n");
            }
            ArrayList<BillTaxItem> billOtherChargesItems = item.getBillOtherChargesItems();
            if(billOtherChargesItems.size()>0)
            {
                Iterator it1 = billOtherChargesItems.iterator();
                double otherchargesamount = 0;
                while (it1.hasNext())
                {
                    BillTaxItem billKotItem = (BillTaxItem) it1.next();
                /*String TxName = getPostAddedSpaceFormat("",String.valueOf(billKotItem.getTxName()),23,1);
                String TxPercent = getPostAddedSpaceFormat("","",15,1);
                String TxValue = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getPrice()),10,1),8 ,1);
                String pre = TxName + TxPercent + TxValue;
                esc.addText(pre+"\n");*/
                    otherchargesamount += billKotItem.getPrice();
                }
                if(otherchargesamount>0){
                    String TxName = getPostAddedSpaceFormat("","Other Charges",23,1);
                    String TxPercent = getPostAddedSpaceFormat("","",15,1);
                    String TxValue = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",otherchargesamount),10,1),8 ,1);
                    String pre = TxName + TxPercent + TxValue;
                    esc.addText(pre+"\n");
                }

            }
            // Tax Slab
            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if(item.getIsInterState().equalsIgnoreCase("n")) // IntraState
            {
                if (it11.hasNext())
                {
                    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                    esc.addText("================================================"+"\n");
                    if(item.getUTGSTEnabled() ==0) // disabled
                        esc.addText("Tax(%)   TaxableVal   CGSTAmt  SGSTAmt    TaxAmt"+"\n");
                    else
                        esc.addText("Tax(%)   TaxableVal   CGSTAmt  UTGSTAmt   TaxAmt"+"\n");
                    esc.addText("================================================"+"\n");
                    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
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
                            esc.addText(pre+"\n");

                        }
                    }while (it11.hasNext());
                }
            }else // InterState
            {
                if (it11.hasNext())
                {
                    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                    esc.addText("================================================"+"\n");
                    esc.addText("Tax(%)   TaxableVal   IGSTAmt             TaxAmt"+"\n");
                    esc.addText("================================================"+"\n");
                    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
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
                            esc.addText(pre+"\n");

                        }
                    }while (it11.hasNext());
                }
            }

            esc.addText("\n");
            double  dtotalcessAmt =0;
            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            Iterator it21 = billcessTaxItems.iterator();
            while (it21.hasNext()) {

                BillServiceTaxItem billKotItem = (BillServiceTaxItem) it21.next();
                if (billKotItem.getServicePercent() > 0){

                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getServiceTxName()), 23, 1);
                    String TxPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", billKotItem.getServicePercent()) + " %", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getServicePrice()), 10, 1), 8, 1);
                    dtotalcessAmt += billKotItem.getServicePrice();
                    String pre = TxName + TxPercent + TxValue;
                    esc.addText(pre + "\n");
                }
            }
            double dTotalTaxAmt = dTotTaxAmt +dtotalcessAmt;
            if(dTotalTaxAmt >0)
            {   esc.addText(getSpaceFormater("Total Tax Amount",String.format("%.2f",dTotalTaxAmt),48,1)+"\n");}
            esc.addText("================================================"+"\n");
            esc.addText(getSpaceFormater("TOTAL",String.format("%.2f",item.getNetTotal()),48,1)+"\n");
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            if(item.getRoundOff()>0){
                //esc.addText("------------------------------------------------\n");
                esc.addText(getSpaceFormater("Total Roundoff to 1.00 ","",48,1)+"\n");
            }
            if(item.getCardPaymentValue()>0 || item.geteWalletPaymentValue()>0 ||
                    item.getCouponPaymentValue()>0 || item.getPettyCashPaymentValue()>0 ){
                esc.addText("================================================"+"\n");
                if(item.getCardPaymentValue()>0)
                    esc.addText(getSpaceFormater("Card Payment",String.format("%.2f",item.getCardPaymentValue()),48,1)+"\n");
                if(item.geteWalletPaymentValue()>0)
                    esc.addText(getSpaceFormater("eWallet Payment",String.format("%.2f",item.geteWalletPaymentValue()),48,1)+"\n");
                if(item.getCouponPaymentValue()>0)
                    esc.addText(getSpaceFormater("Coupon Payment",String.format("%.2f",item.getCouponPaymentValue()),48,1)+"\n");
                if(item.getPettyCashPaymentValue()>0)
                    esc.addText(getSpaceFormater("PettyCash Payment",String.format("%.2f",item.getPettyCashPaymentValue()),48,1)+"\n");
                if(item.getCashPaymentValue()>0)
                    esc.addText(getSpaceFormater("Cash Payment",String.format("%.2f",item.getCashPaymentValue()),48,1)+"\n");
            }

            if (item.getChangePaymentValue()>0) {
                esc.addText("------------------------------------------------"+"\n");
                esc.addText(getSpaceFormater("Due amount",String.format("%.2f",item.getChangePaymentValue()),48,1)+"\n");
            }
            esc.addText("================================================"+"\n");
            if(!item.getFooterLine1().equals(""))
                esc.addText(item.getFooterLine1()+"\n");
            if(!item.getFooterLine2().equals(""))
                esc.addText(item.getFooterLine2()+"\n");
            if(!item.getFooterLine3().equals(""))
                esc.addText(item.getFooterLine3()+"\n");
            if(!item.getFooterLine4().equals(""))
                esc.addText(item.getFooterLine4()+"\n");
            if(!item.getFooterLine5().equals(""))
                esc.addText(item.getFooterLine5()+"\n");
            esc.addPrintAndFeedLines((byte)3);

            Vector<Byte> datas = esc.getCommand();

            printer.sendData(datas);
            printer.cutPaper();

        } catch (Exception e) {

        }
    }

    public void getPrintKOT(PrintKotBillItem item) {

        try{

            EscCommand esc = new EscCommand();
            //esc.addPrintAndFeedLines((byte)1);
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
            String tblno = "", modename = "";
        /*if(item.getBillingMode().equalsIgnoreCase("1")){
            tblno = "Table # "+item.getTableNo() + " | ";
            modename = "Dine In";
        } else if(item.getBillingMode().equalsIgnoreCase("2")){
            tblno = "";
            modename = "Counter Sales";
        } else if(item.getBillingMode().equalsIgnoreCase("3")){
            tblno = "";
            modename = "Pick Up";
        } else if(item.getBillingMode().equalsIgnoreCase("4")){
            tblno = "";
            modename = "Home Delivery";
        }*/
            if(item.getBillingMode().equalsIgnoreCase("1")){
                tblno = "Table # "+item.getTableNo() + " | ";
                modename = item.getStrBillingModeName();
            } else {
                tblno = "";
                modename = item.getStrBillingModeName();
            }

            esc.addText(modename+"\n");
            esc.addText(tblno +"KOT # "+item.getBillNo()+"\n");
            esc.addPrintAndLineFeed();

            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("=============================================\n");
            esc.addText("Attendant : "+item.getOrderBy()+"\n");
            if ((item.getWaiterName() != null) && !(item.getWaiterName().equals("")))
                esc.addText("Waiter : "+item.getWaiterName()+"\n");
            esc.addText("Date : "+item.getDate() +" | "+"Time : "+item.getTime()+"\n");
            esc.addText("=============================================\n");
//        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
//        esc.addText("Lists\n");
//        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
//        esc.addText("=============================================\n");
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("SNo        NAME                    QTY"+"\n");
            esc.addText("=============================================\n");
            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            while (it.hasNext())
            {
                BillKotItem billKotItem = (BillKotItem) it.next();
                int id = billKotItem.getItemId();
                String name = getFormatedCharacterForPrint(billKotItem.getItemName(),16,1);
                String qty = String.format("%.2f",billKotItem.getQty());
                String pre = getPostAddedSpaceFormat("",String.valueOf(id),10,1)+name;
                esc.addText(getPreAddedSpaceFormat(pre,qty,38,1)+"\n");
            }
            esc.addText("=============================================\n");
            esc.addPrintAndFeedLines((byte)3);

            Vector<Byte> datas = esc.getCommand();

            printer.sendData(datas);
            printer.cutPaper();

        } catch (Exception e) {

        }
    }

   /* private void sendDepositReceiptData(Customer item){

        try{

            EscCommand esc = new EscCommand();
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            esc.addText("Deposit Amount Receipt"+"\n");

            if (item.getHeaderPrintBold() == 1)
                esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                esc.addText(item.getHeaderLine1()+"\n");
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                esc.addText(item.getHeaderLine2()+"\n");
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                esc.addText(item.getHeaderLine3()+"\n");
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                esc.addText(item.getHeaderLine4()+"\n");
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                esc.addText(item.getHeaderLine5()+"\n");
            }

            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

            esc.addText("================================================"+"\n");
            esc.addText("Deposit Date    : "+item.getBusinessDate()+"\n");
            esc.addText("Customer Name   : "+item.getStrCustName()+"\n");
            esc.addText("Mobile No.      : "+item.getStrCustContactNumber()+"\n");
            esc.addText("Deposit Amount  : "+item.getDblDepositAmt()+"\n");
            esc.addText("Credit Amount   : "+item.getdCreditAmount()+"\n");
            esc.addText("================================================"+"\n");
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            if(item.getFooterLine1()!=null && !item.getFooterLine1().equals(""))
                esc.addText(item.getFooterLine1()+"\n");
            if(item.getFooterLine2()!=null && !item.getFooterLine2().equals(""))
                esc.addText(item.getFooterLine2()+"\n");
            if(item.getFooterLine3()!=null && !item.getFooterLine3().equals(""))
                esc.addText(item.getFooterLine3()+"\n");
            if(item.getFooterLine4()!=null && !item.getFooterLine4().equals(""))
                esc.addText(item.getFooterLine4()+"\n");
            if(item.getFooterLine5()!=null && !item.getFooterLine5().equals(""))
                esc.addText(item.getFooterLine5()+"\n");

            esc.addPrintAndFeedLines((byte)3);
            Vector<Byte> datas = esc.getCommand();

            printer.sendData(datas);
            printer.cutPaper();

        } catch (Exception e) {
            printerConnectionError.onError(e.getMessage());
        }
    }*/

    private void finalizeObject() {
        printer.unregisterCallback();
        mTarget = null;
        mContext = null;
        printer = null;
        printerConnectionError = null;
    }

    private CallbackInterface callbackInterface = new CallbackInterface() {
        @Override
        public EpsonCom.ERROR_CODE CallbackMethod(EpsonComCallbackInfo epsonComCallbackInfo) {
            return null;
        }
    };

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

    public String getPreAddedSpaceFormat(String sourceTxt, String toAddTxt, int max,int type)
    {
        return sourceTxt+getSpaces(max-(sourceTxt.length()+toAddTxt.length()),type)+toAddTxt;
    }


    @Override
    public void onHomePressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finalizeObject();
    }
}
