package com.wep.common.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationPaperCountBean implements Parcelable {
    public String strMessage, strDate;
    public int iID, iEventCode;

    public NotificationPaperCountBean(){}

    protected NotificationPaperCountBean(Parcel in) {
        strMessage = in.readString();
        strDate = in.readString();
        iID = in.readInt();
        iEventCode = in.readInt();
    }

    public static final Creator<NotificationPaperCountBean> CREATOR = new Creator<NotificationPaperCountBean>() {
        @Override
        public NotificationPaperCountBean createFromParcel(Parcel in) {
            return new NotificationPaperCountBean(in);
        }

        @Override
        public NotificationPaperCountBean[] newArray(int size) {
            return new NotificationPaperCountBean[size];
        }
    };

    public String getStrMessage() {
        return strMessage;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public int getiID() {
        return iID;
    }

    public void setiID(int iID) {
        this.iID = iID;
    }

    public int getiEventCode() {
        return iEventCode;
    }

    public void setiEventCode(int iEventCode) {
        this.iEventCode = iEventCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strMessage);
        dest.writeString(strDate);
        dest.writeInt(iID);
        dest.writeInt(iEventCode);
    }
}
