package com.wepindia.pos.views.Billing.PdfInvoice;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Created by SachinV on 15-03-2018.
 */

public class ParagraphBorder extends PdfPageEventHelper {

    public float offset = 5;
    public float startPosition;

    public boolean active = false;
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        startPosition = document.top();
    }

    @Override
    public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
        this.startPosition = paragraphPosition;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        if (active) {
            PdfContentByte cb = writer.getDirectContentUnder();
            cb.rectangle(document.left(), document.bottom() - offset,
                    document.right() - document.left(), startPosition - document.bottom());
            cb.stroke();
        }
    }

    @Override
    public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
        if (active) {
            PdfContentByte cb = writer.getDirectContentUnder();
            cb.rectangle(document.left(), paragraphPosition - offset,
                    document.right() - document.left(), startPosition - paragraphPosition);
            cb.stroke();
        }
    }

}
