package com.wep.common.app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MohanN on 3/2/2018.
 */

public class CustomerPassbookBean implements Parcelable{

    private int _id;
    private String strCustomerID, strName, strPhoneNo, strDate, strDescription, strBillNo;
    private double dblOpeningBalance, dblDepositAmount, dblCreditAmount,
                        dblPettyCashTransaction, dblRewardPoints, dblTotalAmount;

    public CustomerPassbookBean(){}

    protected CustomerPassbookBean(Parcel in) {
        _id = in.readInt();
        strCustomerID = in.readString();
        strName = in.readString();
        strPhoneNo = in.readString();
        strDate = in.readString();
        strDescription = in.readString();
        strBillNo = in.readString();
        dblOpeningBalance = in.readDouble();
        dblDepositAmount = in.readDouble();
        dblCreditAmount = in.readDouble();
        dblPettyCashTransaction = in.readDouble();
        dblRewardPoints = in.readDouble();
        dblTotalAmount = in.readDouble();
    }

    public static final Creator<CustomerPassbookBean> CREATOR = new Creator<CustomerPassbookBean>() {
        @Override
        public CustomerPassbookBean createFromParcel(Parcel in) {
            return new CustomerPassbookBean(in);
        }

        @Override
        public CustomerPassbookBean[] newArray(int size) {
            return new CustomerPassbookBean[size];
        }
    };

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getStrCustomerID() {
        return strCustomerID;
    }

    public void setStrCustomerID(String strCustomerID) {
        this.strCustomerID = strCustomerID;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrPhoneNo() {
        return strPhoneNo;
    }

    public void setStrPhoneNo(String strPhoneNo) {
        this.strPhoneNo = strPhoneNo;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getStrDescription() {
        return strDescription;
    }

    public void setStrDescription(String strDescription) {
        this.strDescription = strDescription;
    }

    public String getStrBillNo() {
        return strBillNo;
    }

    public void setStrBillNo(String strBillNo) {
        this.strBillNo = strBillNo;
    }

    public double getDblOpeningBalance() {
        return dblOpeningBalance;
    }

    public void setDblOpeningBalance(double dblOpeningBalance) {
        this.dblOpeningBalance = dblOpeningBalance;
    }

    public double getDblDepositAmount() {
        return dblDepositAmount;
    }

    public void setDblDepositAmount(double dblDepositAmount) {
        this.dblDepositAmount = dblDepositAmount;
    }

    public double getDblCreditAmount() {
        return dblCreditAmount;
    }

    public void setDblCreditAmount(double dblCreditAmount) {
        this.dblCreditAmount = dblCreditAmount;
    }

    public double getDblPettyCashTransaction() {
        return dblPettyCashTransaction;
    }

    public void setDblPettyCashTransaction(double dblPettyCashTransaction) {
        this.dblPettyCashTransaction = dblPettyCashTransaction;
    }

    public double getDblRewardPoints() {
        return dblRewardPoints;
    }

    public void setDblRewardPoints(double dblRewardPoints) {
        this.dblRewardPoints = dblRewardPoints;
    }

    public double getDblTotalAmount() {
        return dblTotalAmount;
    }

    public void setDblTotalAmount(double dblTotalAmount) {
        this.dblTotalAmount = dblTotalAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(_id);
        parcel.writeString(strCustomerID);
        parcel.writeString(strName);
        parcel.writeString(strPhoneNo);
        parcel.writeString(strDate);
        parcel.writeString(strDescription);
        parcel.writeString(strBillNo);
        parcel.writeDouble(dblOpeningBalance);
        parcel.writeDouble(dblDepositAmount);
        parcel.writeDouble(dblCreditAmount);
        parcel.writeDouble(dblPettyCashTransaction);
        parcel.writeDouble(dblRewardPoints);
        parcel.writeDouble(dblTotalAmount);
    }
}
