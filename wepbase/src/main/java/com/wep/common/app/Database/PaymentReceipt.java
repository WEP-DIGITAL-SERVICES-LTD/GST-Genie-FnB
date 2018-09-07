package com.wep.common.app.Database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SachinV on 15-02-2018.
 */

public class PaymentReceipt implements Parcelable {

    private String strDate, strReason;
    private int iBillType, paymentReceiptNo;
    private Double dAmount;
    private String DescriptionText;
    private String isDuplicate;
    private int headerPrintBold;
    private String HeaderLine1;
    private String HeaderLine2;
    private String HeaderLine3;
    private String HeaderLine4;
    private String HeaderLine5;
    private String FooterLine1;
    private String FooterLine2;
    private String FooterLine3;
    private String FooterLine4;
    private String FooterLine5;

    public PaymentReceipt(){
        this.strDate = "";
        this.strReason = "";
        this.iBillType = 0;
        this.headerPrintBold = 0;
        this.paymentReceiptNo = 0;
        this.dAmount = 0.00;
        this.DescriptionText= "";
        this.HeaderLine1= "";
        this.HeaderLine2= "";
        this.HeaderLine3= "";
        this.HeaderLine4= "";
        this.HeaderLine5= "";
        this.FooterLine1= "";
        this.FooterLine2= "";
        this.FooterLine3= "";
        this.FooterLine4= "";
        this.FooterLine5= "";

    }

    public PaymentReceipt(int paymentReceiptNo, String strDate, String strReason, int iBillType, Double dAmount, String descriptionText) {
        this.paymentReceiptNo = paymentReceiptNo;
        this.strDate = strDate;
        this.strReason = strReason;
        this.iBillType = iBillType;
        this.dAmount = dAmount;
        DescriptionText = descriptionText;
    }

    protected PaymentReceipt(Parcel in) {
        strDate = in.readString();
        strReason = in.readString();
        iBillType = in.readInt();
        paymentReceiptNo = in.readInt();
        if (in.readByte() == 0) {
            dAmount = null;
        } else {
            dAmount = in.readDouble();
        }
        DescriptionText = in.readString();
        isDuplicate = in.readString();
        headerPrintBold = in.readInt();
        HeaderLine1 = in.readString();
        HeaderLine2 = in.readString();
        HeaderLine3 = in.readString();
        HeaderLine4 = in.readString();
        HeaderLine5 = in.readString();
        FooterLine1 = in.readString();
        FooterLine2 = in.readString();
        FooterLine3 = in.readString();
        FooterLine4 = in.readString();
        FooterLine5 = in.readString();
    }

    public static final Creator<PaymentReceipt> CREATOR = new Creator<PaymentReceipt>() {
        @Override
        public PaymentReceipt createFromParcel(Parcel in) {
            return new PaymentReceipt(in);
        }

        @Override
        public PaymentReceipt[] newArray(int size) {
            return new PaymentReceipt[size];
        }
    };

    public int getHeaderPrintBold() {
        return headerPrintBold;
    }

    public void setHeaderPrintBold(int headerPrintBold) {
        this.headerPrintBold = headerPrintBold;
    }

    public String getIsDuplicate() {
        return isDuplicate;
    }

    public void setIsDuplicate(String isDuplicate) {
        this.isDuplicate = isDuplicate;
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
        return FooterLine1;
    }

    public void setFooterLine1(String footerLine1) {
        FooterLine1 = footerLine1;
    }

    public String getFooterLine2() {
        return FooterLine2;
    }

    public void setFooterLine2(String footerLine2) {
        FooterLine2 = footerLine2;
    }

    public String getFooterLine3() {
        return FooterLine3;
    }

    public void setFooterLine3(String footerLine3) {
        FooterLine3 = footerLine3;
    }

    public String getFooterLine4() {
        return FooterLine4;
    }

    public void setFooterLine4(String footerLine4) {
        FooterLine4 = footerLine4;
    }

    public String getFooterLine5() {
        return FooterLine5;
    }

    public void setFooterLine5(String footerLine5) {
        FooterLine5 = footerLine5;
    }

    public int getPaymentReceiptNo() {
        return paymentReceiptNo;
    }

    public void setPaymentReceiptNo(int paymentReceiptNo) {
        this.paymentReceiptNo = paymentReceiptNo;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getStrReason() {
        return strReason;
    }

    public void setStrReason(String strReason) {
        this.strReason = strReason;
    }

    public int getiBillType() {
        return iBillType;
    }

    public void setiBillType(int iBillType) {
        this.iBillType = iBillType;
    }

    public Double getdAmount() {
        return dAmount;
    }

    public void setdAmount(Double dAmount) {
        this.dAmount = dAmount;
    }

    public String getDescriptionText() {
        return DescriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        DescriptionText = descriptionText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(strDate);
        parcel.writeString(strReason);
        parcel.writeInt(iBillType);
        parcel.writeInt(paymentReceiptNo);
        if (dAmount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(dAmount);
        }
        parcel.writeString(DescriptionText);
        parcel.writeString(isDuplicate);
        parcel.writeInt(headerPrintBold);
        parcel.writeString(HeaderLine1);
        parcel.writeString(HeaderLine2);
        parcel.writeString(HeaderLine3);
        parcel.writeString(HeaderLine4);
        parcel.writeString(HeaderLine5);
        parcel.writeString(FooterLine1);
        parcel.writeString(FooterLine2);
        parcel.writeString(FooterLine3);
        parcel.writeString(FooterLine4);
        parcel.writeString(FooterLine5);
    }
}
