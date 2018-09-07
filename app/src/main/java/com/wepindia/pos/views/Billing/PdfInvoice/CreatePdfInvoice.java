package com.wepindia.pos.views.Billing.PdfInvoice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.wepindia.pos.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by SachinV on 15-03-2018.
 */

public class CreatePdfInvoice extends AsyncTask {

    private static Context context;
    private static PdfInvoiceBean item;
    private float h1FontSize = 12f;
    private float h2FontSize = 12f;
    private float h3FontSize = 10f;
    private float h4FontSize = 8f;
    private float lineSpacing = 10f;
    private float tableCellBorderWidth = 0.9f;
    private String E_INVOICE_NAME = "";
    private String PDF_INVOICES_GENERATE_PATH = Environment.getExternalStorageDirectory().getPath() + "/"+ Constants.PDF_INVOICE_DIRECTORY+"/";
    private static  CreatePdfInvoice createPdfInvoice = null;

    public static  CreatePdfInvoice getInstance(Context cxt, PdfInvoiceBean bean) {
        if (createPdfInvoice == null) {
            createPdfInvoice = new CreatePdfInvoice();
        }
        context = cxt;
        item = bean;
        createPdfInvoice.execute();
        return createPdfInvoice;
    }



   /* public CreatePdfInvoice(Context context, PdfInvoiceBean item) {
        this.context = context;
        this.item = item;
    }*/

    @Override
    protected Object doInBackground(Object[] objects) {

        File directory = new File(PDF_INVOICES_GENERATE_PATH);
        if (!directory.exists())
            directory.mkdirs();

        E_INVOICE_NAME = "Invoice_" + item.getInvoiceNo() + "_" + item.getInvoiceDate() + ".pdf";
        String filename = PDF_INVOICES_GENERATE_PATH + E_INVOICE_NAME;

        Document document = new Document(PageSize.A4, 10f, 10f, 10f, 10f);
        ParagraphBorder border = new ParagraphBorder();
        try {

            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            if (item.getCompanyLogoPath() != null && !item.getCompanyLogoPath().isEmpty()
                    && !item.getCompanyLogoPath().equals("1234567890")) {
                InputStream ims = new FileInputStream(item.getCompanyLogoPath());
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.setAlignment(Element.ALIGN_CENTER);
                image.scaleAbsolute(70f, 70f);
                document.add(image);
            }
            document.add(new Paragraph("\n"));
            String taxInvoiceText = "";
            if (!item.isTrainingMode())
                taxInvoiceText = "Tax Invoice";
            Paragraph header = new Paragraph(new Phrase(lineSpacing, taxInvoiceText, FontFactory.getFont(FontFactory.COURIER, h1FontSize, Font.BOLD)));
            header.setAlignment(Element.ALIGN_CENTER);
            header.setPaddingTop(15f);
            document.add(header);
            document.add(new Paragraph("\n"));
//            document.add(createSampleInvoice());
            document.add(createInvoice(item));
            //Paragraph notice = new Paragraph(new Phrase(lineSpacing, "*This is a computer generated invoice.", FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            Paragraph notice = new Paragraph(new Phrase(lineSpacing, "\n", FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            notice.setPaddingTop(15f);
            document.add(notice);
            String footer = "";

            if(item.getFooterLine1()!=null && !item.getFooterLine1().equals("")) {
                footer += item.getFooterLine1() + "\n";
            }
            if(item.getFooterLine2()!=null && !item.getFooterLine2().equals("")) {
                footer += item.getFooterLine2() + "\n";
            }
            if(item.getFooterLine3()!=null && !item.getFooterLine3().equals("")) {
                footer += item.getFooterLine3() + "\n";
            }
            if(item.getFooterLine4()!=null && !item.getFooterLine4().equals("")) {
                footer += item.getFooterLine4() + "\n";
            }
            if(item.getFooterLine5()!=null && !item.getFooterLine5().equals("")) {
                footer += item.getFooterLine5() + "\n";
            }
            Paragraph footerPara = new Paragraph(new Phrase(lineSpacing, footer, FontFactory.getFont(FontFactory.COURIER, h2FontSize)));
            footerPara.setAlignment(Element.ALIGN_CENTER);
            footerPara.setPaddingTop(15f);
            document.add(footerPara);
            if(!item.getStrJurisdictionsPrint().isEmpty()) {
                Paragraph jurisdiction = new Paragraph(new Phrase(lineSpacing, "*" +item.getStrJurisdictionsPrint(), FontFactory.getFont(FontFactory.COURIER, h4FontSize)));
                footerPara.setAlignment(Element.ALIGN_LEFT);
                footerPara.setPaddingTop(15f);
                document.add(jurisdiction);
            }
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(document != null && document.isOpen()) {
                document.close();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (createPdfInvoice != null)
            createPdfInvoice = null;
    }

    public PdfPTable createInvoice(PdfInvoiceBean item) {

        double totTaxableAmount = 0;
        double totCgstAmount = 0;
        double totSgstAmount = 0;
        double totIgstAmount = 0;
        double totCessAmount = 0;
        double totUnitPrice = 0;
        double totQty = 0;
        double otherCharges = 0;
        double grandTotal = 0;

        PdfPTable table = new PdfPTable(16);
        if (item != null) {

            table.setWidthPercentage(100);

            PdfPCell cell;
            Paragraph invoiceText;

            String header = "";

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                header += item.getHeaderLine1() + "\n";
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                header += item.getHeaderLine2() + "\n";
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                header += item.getHeaderLine3() + "\n";
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                header += item.getHeaderLine4() + "\n";
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                header += item.getHeaderLine5() + "\n";
            }

            header += "GSTIN: " + item.getOwnerGstin();

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    header,
                    FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(16);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    "Invoice No.       : " +item.getInvoiceNo() + "\n" +
                            "Place Of Supply   : "+item.getOwnerPos(),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setRowspan(1);
            cell.setColspan(8);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    "Sate Code         : " + item.getOwnerStateCode()+ "\n" +
                            "Invoice Date      : "+item.getInvoiceDate(),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setRowspan(1);
            cell.setColspan(8);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            String detail = "";

            if (item.isReverseTax())
                detail = "#Details Of Receiver";
            else
                detail = "Details Of Receiver";

            invoiceText = new Paragraph(new Phrase(lineSpacing, detail,
                    FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(16);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    "Name         : " + item.getCustomerName() + "\n" +
                            "Address      : " + item.getCustomerAddress() + "\n" +
                            "GSTIN        : " + item.getCustomerGstin() +"\n" +
                            "State        : " + item.getCustomerState() ,
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(16);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "SNo.",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Item Name",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(3);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "HSN Code",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Qty",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "MRP",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Unit Price",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Disc Amt",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Taxable Value",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "GST Rate",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Tax Amt",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

           /* invoiceText = new Paragraph(new Phrase(lineSpacing, "CGST Amount",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "SGST Amount",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "IGST Amount",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);*/

            invoiceText = new Paragraph(new Phrase(lineSpacing, "cess Amt",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Total",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            ArrayList<PdfItemBean> pdfItemBeanArrayList = item.getPdfItemBeanArrayList();

            int i=1;

            for (PdfItemBean billKotItem: pdfItemBeanArrayList) {
                double taxableAmount = 0;
                double cgstAmount = 0;
                double sgstAmount = 0;
                double igstAmount = 0;
                double cessAmount = 0;
                double total = 0;

                // SNO
                invoiceText = new Paragraph(new Phrase(lineSpacing, ""+i,
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                // Item Name
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.valueOf(billKotItem.getItemName()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setColspan(3);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                // HSN Code
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.valueOf(billKotItem.getHSNCode()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                // QTY
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.valueOf(billKotItem.getQty())+billKotItem.getUOM(),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);
                totQty += Double.parseDouble(String.format("%.2f",billKotItem.getQty()));

                // MRP
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",billKotItem.getMrp()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                //Unit Price
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",billKotItem.getValue()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);
                totUnitPrice += billKotItem.getValue();

                // Dicount
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",billKotItem.getDiscAmount()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                // Taxable Value
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",billKotItem.getTaxableValue()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(2);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);
                taxableAmount = Double.parseDouble(String.format("%.2f",billKotItem.getTaxableValue()));
                totTaxableAmount += Double.parseDouble(String.format("%.2f",billKotItem.getTaxableValue()));

                // GST Rate
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",billKotItem.getGstRate()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

              /*  // CGST Amount
                invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);*/
                cgstAmount = Double.parseDouble(String.format("%.2f",billKotItem.getCgstAmount()));
                totCgstAmount += Double.parseDouble(String.format("%.2f",billKotItem.getCgstAmount()));

               /* // SGST Amount
                invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);*/
                sgstAmount = Double.parseDouble(String.format("%.2f",billKotItem.getSgstAmount()));
                totSgstAmount += Double.parseDouble(String.format("%.2f",billKotItem.getSgstAmount()));

                /*// IGST Amount
                invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);*/
                igstAmount = Double.parseDouble(String.format("%.2f",billKotItem.getIgstAmount()));
                totIgstAmount += Double.parseDouble(String.format("%.2f",billKotItem.getIgstAmount()));

                // Tax Amount
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",cgstAmount+sgstAmount+igstAmount),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);


                // CESS Amount
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",billKotItem.getCessAmount()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);
                cessAmount = billKotItem.getCessAmount();
                totCessAmount += cessAmount;

                total = taxableAmount + cgstAmount + sgstAmount + igstAmount + cessAmount;

                // Total
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",total),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(2);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);
                grandTotal += total;

                i++;
            }

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Sub Total",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(5);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.3f", totQty),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totUnitPrice),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
//            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totTaxableAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);
/*
            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totCgstAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totSgstAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totIgstAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);*/

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totCgstAmount+totSgstAmount+totIgstAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totCessAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", grandTotal),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(16);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            String text = "Total Invoice Amount in Words:" + "\n";

            double rupees = 0;
            int int_rupees = 0, int_paise = 0;
            otherCharges = item.getOtherCharges();
            rupees = totTaxableAmount+totCgstAmount+totSgstAmount+totIgstAmount+totCessAmount+otherCharges;
            String temp = String.format("%.2f", rupees);

            String aboveTenLakh = "0", belowTenLakh = "0", paise = "0";

            if (item.getBillAmountRoundOff() == 1) {

                temp = String.valueOf(Math.round(Double.parseDouble(temp)));

                if (temp.length() > 10) {
                    aboveTenLakh = temp.substring(0, temp.length()-10);
                    belowTenLakh = temp.substring(temp.length()-10);
                } else {
//                    int_rupees = Integer.parseInt(temp);
                    belowTenLakh = temp;
                }

            } else {

                temp = String.format("%.2f", rupees);

                if (temp.length() > 10) {
                    aboveTenLakh = temp.substring(0, temp.length()-10);
                    belowTenLakh = temp.substring(temp.length()-10);
                    belowTenLakh = belowTenLakh.substring(0, belowTenLakh.indexOf("."));
                    paise = temp.substring(temp.indexOf(".")+1);
                } else {
                    belowTenLakh = temp.substring(0, temp.indexOf("."));
                    paise = temp.substring(temp.indexOf(".")+1);
                }
            }

            if (Integer.parseInt(aboveTenLakh) > 0) {
                text = text + ConvertToWord.convert(Integer.parseInt(aboveTenLakh)) + " Crore ";
            }
            if (Integer.parseInt(belowTenLakh) > 0) {
                text = text + ConvertToWord.convert(Integer.parseInt(belowTenLakh)) + " Rupees";
            }
            if (Integer.parseInt(paise) > 0) {
                text = text + " and " + ConvertToWord.convert(Integer.parseInt(paise)) + " Paise";
            }

            invoiceText = new Paragraph(new Phrase(lineSpacing, text + " Only",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(10);
            cell.setRowspan(8);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Total Amount Before Tax",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            cell = new PdfPCell(invoiceText);
            cell.setColspan(4);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totTaxableAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Total CGST",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(4);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totCgstAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Total SGST/UTGST",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(4);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totSgstAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Total IGST",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(4);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totIgstAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Total cess",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(4);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totCessAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Tax Amount: GST",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(4);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", totCgstAmount+totSgstAmount+totIgstAmount),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Other Charges",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(4);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", item.getOtherCharges()),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);
            otherCharges = item.getOtherCharges();

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Total Amount After Tax",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(4);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            double totalAmount = 0;
            if (item.getBillAmountRoundOff() == 1)
                totalAmount = Math.round(totTaxableAmount+totCgstAmount+totSgstAmount+totIgstAmount+totCessAmount+otherCharges);
            else
                totalAmount = Double.parseDouble(String.format("%.2f", totTaxableAmount+totCgstAmount+totSgstAmount+totIgstAmount+totCessAmount+otherCharges));

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", grandTotal),
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);
        }

        return table;
    }

    public PdfPTable createSampleInvoice() {
        // a table with three columns
        PdfPTable table = new PdfPTable(16);
        table.setWidthPercentage(100);
        // the cell object
        PdfPCell cell;
        Paragraph invoiceText;
        // we add a cell with colspan 3
        invoiceText = new Paragraph(new Phrase(lineSpacing,
                "Tax Invoice" + "\n" + "Retail Center" + "\n" + "Bangalore" + "\n" + "GSTIN: 09ANTPA0870E1Z6",
                FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(16);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);
        // now we add a cell with rowspan 2
        invoiceText = new Paragraph(new Phrase(lineSpacing,
                 "Invoice No.       : INVIZY000001" + "\n" +
                        "Place Of Supply   : ARUNANCHAL PRADESH",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setRowspan(1);
        cell.setColspan(8);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing,
                "Sate Code         : 12" + "\n" +
                        "Invoice Date      : 15-03-2018",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setRowspan(1);
        cell.setColspan(8);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Details Of Receiver",
                FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(16);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing,
                "Name         : Sachin Verma" + "\n" +
                        "Address      : #123, XYZ Colony, Patiala"+ "\n" +
                        "GSTIN        : 09ANTPA0870E1Z6"+ "\n" +
                        "State        : PUNJAB",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(16);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Sr. No.",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Item Name",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(2);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "HSN Code",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Qty",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Unit Price",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Amount",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Discount",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Taxable Value",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(2);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "GST Rate",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "CGST Amount",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "SGST Amount",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "IGST Amount",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "CESS Amount",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Total",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        for (int i=1; i<=10; i++) {

            // SNO
            invoiceText = new Paragraph(new Phrase(lineSpacing, ""+i,
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // Item Name
            invoiceText = new Paragraph(new Phrase(lineSpacing, "Item "+i,
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(2);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // HSN Code
            invoiceText = new Paragraph(new Phrase(lineSpacing, "HSN "+i,
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // QTY
            invoiceText = new Paragraph(new Phrase(lineSpacing, ""+23,
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // Unit Price
            invoiceText = new Paragraph(new Phrase(lineSpacing, "1000.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            //Amount
            invoiceText = new Paragraph(new Phrase(lineSpacing, "1000.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // Dicount
            invoiceText = new Paragraph(new Phrase(lineSpacing, "0.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // Taxable Value
            invoiceText = new Paragraph(new Phrase(lineSpacing, "1000.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(2);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // GST Rate
            invoiceText = new Paragraph(new Phrase(lineSpacing, "3.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // CGST Amount
            invoiceText = new Paragraph(new Phrase(lineSpacing, "60.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // SGST Amount
            invoiceText = new Paragraph(new Phrase(lineSpacing, "60.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // IGST Amount
            invoiceText = new Paragraph(new Phrase(lineSpacing, "0.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // CESS Amount
            invoiceText = new Paragraph(new Phrase(lineSpacing, "0.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            // Total
            invoiceText = new Paragraph(new Phrase(lineSpacing, "1120.00",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            invoiceText.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell(invoiceText);
            cell.setColspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);
        }

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Total",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(4);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "2323",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "21344.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "344354.56",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(2);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "60.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "60.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "0.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "0.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "466778.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(16);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Total Invoice Amount in Words:" + "\n" + "One Thousand One Hundred and Twenty Rupees Only",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(10);
        cell.setRowspan(6);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Total Amount Before Tax :",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(4);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "1000.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(2);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Add CGST",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(4);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "60.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(2);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Add SGST",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(4);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "60.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(2);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Add IGST",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(4);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "0.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(2);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Tax Amount: GST :",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(4);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "120.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(2);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "Total Amount After Tax :",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(4);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

        invoiceText = new Paragraph(new Phrase(lineSpacing, "1120.00",
                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
        invoiceText.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(invoiceText);
        cell.setColspan(2);
        cell.setRowspan(1);
        cell.setBorderWidth(tableCellBorderWidth);
        table.addCell(cell);

//        invoiceText = new Paragraph(new Phrase(lineSpacing, "",
//                FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
//        invoiceText.setAlignment(Element.ALIGN_CENTER);
//        cell = new PdfPCell(invoiceText);
//        cell.setColspan(6);
//        cell.setBorderWidth(tableCellBorderWidth);
//        table.addCell(cell);

        // we add the four remaining cells with addCell()
//        table.addCell("row 1; cell 1");
//        table.addCell("row 1; cell 2");
//        table.addCell("row 2; cell 1");
//        table.addCell("row 2; cell 2");

        return table;
    }
}
