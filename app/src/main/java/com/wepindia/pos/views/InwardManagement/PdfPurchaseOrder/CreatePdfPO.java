package com.wepindia.pos.views.InwardManagement.PdfPurchaseOrder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import com.itextpdf.text.Document;
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
import com.wepindia.pos.views.Billing.PdfInvoice.ConvertToWord;
import com.wepindia.pos.views.Billing.PdfInvoice.ParagraphBorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class CreatePdfPO extends AsyncTask {

    private static Context context;
    private static PdfPOBean item;
    private float h1FontSize = 12f;
    private float h2FontSize = 12f;
    private float h3FontSize = 10f;
    private float h4FontSize = 8f;
    private float lineSpacing = 10f;
    private float tableCellBorderWidth = 0.9f;
    private String E_INVOICE_NAME = "";
    private String PDF_PURCHASEORDER_GENERATE_PATH = Environment.getExternalStorageDirectory().getPath() + "/WeP_Retail_PurchaseOrder/";
    private static CreatePdfPO createPdfPO = null;

    public static CreatePdfPO getInstance(Context cxt, PdfPOBean bean) {
        if (createPdfPO == null) {
            createPdfPO = new CreatePdfPO();
        }
        context = cxt;
        item = bean;
        createPdfPO.execute();
        return createPdfPO;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        File directory = new File(PDF_PURCHASEORDER_GENERATE_PATH);
        if (!directory.exists())
            directory.mkdirs();

        E_INVOICE_NAME = "PO_" + item.getiPurchaseOrderNo() + "_" + item.getStrInvoiceNo() + "_" + item.getStrInvoiceDate() + ".pdf";
        String filename = PDF_PURCHASEORDER_GENERATE_PATH + E_INVOICE_NAME;

        Document document = new Document(PageSize.A4, 10f, 10f, 10f, 10f);
        ParagraphBorder border = new ParagraphBorder();

        try {

            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            document.add(new Paragraph("\n"));

            Paragraph header = new Paragraph(new Phrase(lineSpacing, "Purchase Order", FontFactory.getFont(FontFactory.COURIER, h1FontSize, Font.BOLD)));
            header.setAlignment(Element.ALIGN_CENTER);
            header.setPaddingTop(15f);
            document.add(header);
            document.add(new Paragraph("\n"));
//            document.add(createSampleInvoice());
            document.add(createPurchaseOrder(item));
            //Paragraph notice = new Paragraph(new Phrase(lineSpacing, "*This is a computer generated invoice.", FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            Paragraph notice = new Paragraph(new Phrase(lineSpacing, "\n", FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
            notice.setPaddingTop(15f);
            document.add(notice);

            document.close();

        } catch (Exception e) {
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
        if (createPdfPO != null)
            createPdfPO = null;
    }

    public PdfPTable createPurchaseOrder(PdfPOBean item) {

        double totTaxableAmount = 0;
        double totCgstAmount = 0;
        double totSgstAmount = 0;
        double totIgstAmount = 0;
        double totCessAmount = 0;
        double totUnitPrice = 0;
        double totQty = 0;
        double grandTotal = 0;

        PdfPTable table = new PdfPTable(12);
        if (item != null) {

            table.setWidthPercentage(100);

            PdfPCell cell;
            Paragraph invoiceText;

            if (item.getCompanyLogoPath() != null && !item.getCompanyLogoPath().isEmpty()
                    && !item.getCompanyLogoPath().equals("1234567890")) {
                InputStream ims = null;
                try {
                    ims = new FileInputStream(item.getCompanyLogoPath());
                    Bitmap bmp = BitmapFactory.decodeStream(ims);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
                    Image image = Image.getInstance(stream.toByteArray());
                    image.setAlignment(Element.ALIGN_CENTER);
                    image.scaleAbsolute(70f, 70f);

                    cell = new PdfPCell(image);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setRowspan(1);
                    cell.setColspan(4);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                invoiceText = new Paragraph(new Phrase(lineSpacing,
                        "",
                        FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
                invoiceText.setAlignment(Element.ALIGN_LEFT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setRowspan(1);
                cell.setColspan(4);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);
            }


            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    item.getOwnerAddress() + "\n" + "GSTIN: " + item.getOwnerGstin(),
                    FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_LEFT);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setRowspan(1);
            cell.setColspan(4);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            String details_1 = "Invoice No: " + item.getStrInvoiceNo() + "\n" +
                    "Invoice Date: " + item.getStrInvoiceDate();

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    details_1,
                    FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_LEFT);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setRowspan(1);
            cell.setColspan(4);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    "Supplier details",
                    FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_LEFT);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setRowspan(1);
            cell.setColspan(6);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    "Bill To",
                    FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_LEFT);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setRowspan(1);
            cell.setColspan(6);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            String supplier = item.getStrSupplierName() + "\n" + item.getStrSupplierAddress() +
                    "\nState Code: " + item.getStrSupplierPOS() + " \nGSTIN: " + item.getStrSupplierGSTIN() + "\n\n";

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    supplier ,
                    FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_LEFT);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setRowspan(6);
            cell.setColspan(6);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            String owner = item.getOwnerName() + "\n" + item.getOwnerAddress() +
                    "\nState Code: " + item.getOwnerPos() + " \nGSTIN: " + item.getOwnerGstin() + "\n\n";

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    owner,
                    FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_LEFT);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setRowspan(6);
            cell.setColspan(6);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing,
                    "Dear Sir/Mam,\nWe are pleased to confirm our purchase order for supply of the items mentioned below.",
                    FontFactory.getFont(FontFactory.COURIER, h2FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_LEFT);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setRowspan(2);
            cell.setColspan(12);
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

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Description",
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

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Unit Price",
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
            cell.setColspan(2);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, "UOM",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(1);
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

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Amount",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            invoiceText.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(invoiceText);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            ArrayList<PdfPOItemBean> pdfItemBeanArrayList = item.getPdfItemBeanArrayList();
            int i=1;

            for (PdfPOItemBean bean: pdfItemBeanArrayList) {

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
                invoiceText = new Paragraph(new Phrase(lineSpacing, bean.getStrItemName(),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setColspan(3);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                // HSN Code
                invoiceText = new Paragraph(new Phrase(lineSpacing, bean.getStrHSNCode(),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                //Unit Price
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",bean.getDblValue()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                // QTY
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.valueOf(bean.getDblQuantity()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(2);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                // UOM
                invoiceText = new Paragraph(new Phrase(lineSpacing, bean.getStrUOM(),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                // GST Rate
                invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                // Total
                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",bean.getDblAmount()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(2);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);
                grandTotal += bean.getDblAmount();

                if (bean.getDblIGSTRate() > 0) {
                    // SNO
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // Tax Name
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "IGST",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setColspan(3);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // HSN Code
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    //Unit Price
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // QTY
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(2);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // UOM
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // GST Rate
                    invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",bean.getDblIGSTRate()),
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // Tax Total
                    invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",bean.getDblIGSTAmount()),
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(2);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);
//                    grandTotal += bean.getDblIGSTAmount();
                } else {
                    // SNO
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // Tax Name
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "CGST",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setColspan(3);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // HSN Code
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    //Unit Price
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // QTY
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(2);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // UOM
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // GST Rate
                    invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",bean.getDblCGSTRate()),
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // Tax Total
                    invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",bean.getDblCGSTAmount()),
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(2);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);
//                    grandTotal += bean.getDblCGSTAmount();

                    // SNO
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // Tax Name
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "SGST",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setColspan(3);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // HSN Code
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    //Unit Price
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // QTY
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(2);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // UOM
                    invoiceText = new Paragraph(new Phrase(lineSpacing, "",
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // GST Rate
                    invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",bean.getDblSGSTRate()),
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(1);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);

                    // Tax Total
                    invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f",bean.getDblSGSTAmount()),
                            FontFactory.getFont(FontFactory.COURIER, h3FontSize)));
                    invoiceText.setAlignment(Element.ALIGN_RIGHT);
                    cell = new PdfPCell(invoiceText);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setColspan(2);
                    cell.setBorderWidth(tableCellBorderWidth);
                    table.addCell(cell);
//                    grandTotal += bean.getDblSGSTAmount();
                }

                i++;
            }

            String text = "Total Invoice Amount in Words:" + "\n";

            double rupees = 0;
            rupees = grandTotal;
            String temp = String.format("%.2f", rupees);

            String aboveTenLakh = "0", belowTenLakh = "0", paise = "0";

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
            cell.setColspan(8);
            cell.setRowspan(8);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            if (item.getDblAdditionalChargeAmount() > 0) {
                invoiceText = new Paragraph(new Phrase(lineSpacing, item.getStrAdditionalCharge(),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
                cell = new PdfPCell(invoiceText);
                cell.setColspan(2);
                cell.setRowspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);

                invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", item.getDblAdditionalChargeAmount()),
                        FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
                invoiceText.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell(invoiceText);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setColspan(2);
                cell.setRowspan(1);
                cell.setBorderWidth(tableCellBorderWidth);
                table.addCell(cell);
            }

            invoiceText = new Paragraph(new Phrase(lineSpacing, "Grand Total",
                    FontFactory.getFont(FontFactory.COURIER, h3FontSize, Font.BOLD)));
            cell = new PdfPCell(invoiceText);
            cell.setColspan(2);
            cell.setRowspan(1);
            cell.setBorderWidth(tableCellBorderWidth);
            table.addCell(cell);

            invoiceText = new Paragraph(new Phrase(lineSpacing, String.format("%.2f", grandTotal + item.getDblAdditionalChargeAmount()),
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
}
