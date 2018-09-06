package com.wep.common.app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MohanN on 12/18/2017.
 */

public class PayBillDialogBean implements Parcelable {

    public String strData;
    public int iDrawable;

    public PayBillDialogBean(String strData, int iDrawable )
    {
        this.strData=strData;
        this.iDrawable=iDrawable;
    }

    protected PayBillDialogBean(Parcel in) {
        strData = in.readString();
        iDrawable = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strData);
        dest.writeInt(iDrawable);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PayBillDialogBean> CREATOR = new Creator<PayBillDialogBean>() {
        @Override
        public PayBillDialogBean createFromParcel(Parcel in) {
            return new PayBillDialogBean(in);
        }

        @Override
        public PayBillDialogBean[] newArray(int size) {
            return new PayBillDialogBean[size];
        }
    };
}
