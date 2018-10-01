package com.wep.common.app.Database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by RichaA on 3/15/2017.
 */

public class Supplier_Model implements Parcelable {

    private int sno;
    private int _id;
    private String SupplierName;
    private String SupplierGSTIN;
    private String SupplierPhone;
    private String SupplierEmail;
    private String SupplierAddress;
    private String SupplierType;
    private int isActive;
    private int isDelete;

    public Supplier_Model() {
    }

    protected Supplier_Model(Parcel in) {
        sno = in.readInt();
        _id = in.readInt();
        SupplierName = in.readString();
        SupplierGSTIN = in.readString();
        SupplierPhone = in.readString();
        SupplierEmail = in.readString();
        SupplierAddress = in.readString();
        SupplierType = in.readString();
        isActive = in.readInt();
    }

    public static final Creator<Supplier_Model> CREATOR = new Creator<Supplier_Model>() {
        @Override
        public Supplier_Model createFromParcel(Parcel in) {
            return new Supplier_Model(in);
        }

        @Override
        public Supplier_Model[] newArray(int size) {
            return new Supplier_Model[size];
        }
    };

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSupplierName() {
        return SupplierName;
    }

    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }

    public String getSupplierGSTIN() {
        return SupplierGSTIN;
    }

    public void setSupplierGSTIN(String supplierGSTIN) {
        SupplierGSTIN = supplierGSTIN;
    }

    public String getSupplierPhone() {
        return SupplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        SupplierPhone = supplierPhone;
    }

    public String getSupplierEmail() {
        return SupplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        SupplierEmail = supplierEmail;
    }

    public String getSupplierAddress() {
        return SupplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        SupplierAddress = supplierAddress;
    }

    public String getSupplierType() {
        return SupplierType;
    }

    public void setSupplierType(String supplierType) {
        SupplierType = supplierType;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(sno);
        parcel.writeInt(_id);
        parcel.writeString(SupplierName);
        parcel.writeString(SupplierGSTIN);
        parcel.writeString(SupplierPhone);
        parcel.writeString(SupplierEmail);
        parcel.writeString(SupplierAddress);
        parcel.writeString(SupplierType);
        parcel.writeInt(isActive);
    }
}
