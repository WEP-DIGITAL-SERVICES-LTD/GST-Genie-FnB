package com.wep.common.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Administrator on 22-01-2018.
 */

public class PaymentDetails implements Parcelable{

    String custPhoneNo;
    boolean isDiscounted;
    boolean  isToPrintBill;
    double totalDiscountAmount;
    double totalDiscountPercent;
    double cashAmount;
    double cardAmount;
    double couponAmount;
    double walletAmount;
    double aepsAmount;
    double pettyCash;
    double rewardPoints ;
    double totalPaidAmount;
    double totalreturnAmount;
    double totalRoundOff;
    double totalFinalBillAmount;
    boolean isOrderDelivered;
    ArrayList<AddedItemsToOrderTableClass> orderList;
    double totalBillAmount;
    double totalIGSTAmount;
    double totalCGSTAmount;
    double totalSGSTAmount;
    double totalcessAmount;
    int noOfPrint;
    double mSwipeAmount;
    double mPaytmAmount;


    public PaymentDetails(String custPhoneNo, boolean isDiscounted, boolean isToPrintBill, double totalDiscountAmount, double totalDiscountPercent, double cashAmount, double cardAmount, double couponAmount, double walletAmount, double aepsAmount, double pettyCash, double rewardPoints, double totalPaidAmount, double totalreturnAmount, double totalRoundOff, double totalFinalBillAmount, boolean isOrderDelivered, ArrayList<AddedItemsToOrderTableClass> orderList, double totalBillAmount, double totalIGSTAmount, double totalCGSTAmount, double totalSGSTAmount, double totalcessAmount, int noOfPrint, double dblMSwipeAmount, double dblPaytmAmount) {
        this.custPhoneNo = custPhoneNo;
        this.isDiscounted = isDiscounted;
        this.isToPrintBill = isToPrintBill;
        this.totalDiscountAmount = totalDiscountAmount;
        this.totalDiscountPercent = totalDiscountPercent;
        this.cashAmount = cashAmount;
        this.cardAmount = cardAmount;
        this.couponAmount = couponAmount;
        this.walletAmount = walletAmount;
        this.aepsAmount = aepsAmount;
        this.pettyCash = pettyCash;
        this.rewardPoints = rewardPoints;
        this.totalPaidAmount = totalPaidAmount;
        this.totalreturnAmount = totalreturnAmount;
        this.totalRoundOff = totalRoundOff;
        this.totalFinalBillAmount = totalFinalBillAmount;
        this.isOrderDelivered = isOrderDelivered;
        this.orderList = orderList;
        this.totalBillAmount = totalBillAmount;
        this.totalIGSTAmount = totalIGSTAmount;
        this.totalCGSTAmount = totalCGSTAmount;
        this.totalSGSTAmount = totalSGSTAmount;
        this.totalcessAmount = totalcessAmount;
        this.noOfPrint = noOfPrint;
        this.mSwipeAmount = dblMSwipeAmount;
        this.mPaytmAmount = dblPaytmAmount;
    }

    public PaymentDetails() {
        this.custPhoneNo = "";
        this.isDiscounted = false;
        this.isToPrintBill = false;
        this.totalDiscountAmount = 0.00;
        this.totalDiscountPercent = 0.00;
        this.cashAmount = 0.00;
        this.cardAmount = 0.00;
        this.couponAmount = 0.00;
        this.aepsAmount = 0.00;
        this.pettyCash = 0.00;
        this.totalPaidAmount = 0.00;
        this.totalreturnAmount = 0.00;
        this.totalRoundOff = 0.00;
        this.walletAmount = 0.00;
        this.rewardPoints = 0.00;
        this.totalFinalBillAmount = 0.00;
        this.isOrderDelivered = false;
        this.orderList = new ArrayList<>();
        this.totalBillAmount = 0.00;
        this.totalIGSTAmount = 0.00;
        this.totalCGSTAmount = 0.00;
        this.totalSGSTAmount = 0.00;
        this.totalcessAmount = 0.00;
        this.noOfPrint = 1;
        this.mSwipeAmount = 0.00;
        this.mPaytmAmount = 0.00;
    }


    protected PaymentDetails(Parcel in) {
        custPhoneNo = in.readString();
        isDiscounted = in.readByte() != 0;
        isToPrintBill = in.readByte() != 0;
        totalDiscountAmount = in.readDouble();
        totalDiscountPercent = in.readDouble();
        cashAmount = in.readDouble();
        cardAmount = in.readDouble();
        couponAmount = in.readDouble();
        walletAmount = in.readDouble();
        aepsAmount = in.readDouble();
        pettyCash = in.readDouble();
        rewardPoints = in.readDouble();
        totalPaidAmount = in.readDouble();
        totalreturnAmount = in.readDouble();
        totalRoundOff = in.readDouble();
        totalFinalBillAmount = in.readDouble();
        isOrderDelivered = in.readByte() != 0;
        orderList = in.createTypedArrayList(AddedItemsToOrderTableClass.CREATOR);
        totalBillAmount = in.readDouble();
        totalIGSTAmount = in.readDouble();
        totalCGSTAmount = in.readDouble();
        totalSGSTAmount = in.readDouble();
        totalcessAmount = in.readDouble();
        noOfPrint = in.readInt();
        mSwipeAmount = in.readDouble();
        mPaytmAmount = in.readDouble();
    }

    public static final Creator<PaymentDetails> CREATOR = new Creator<PaymentDetails>() {
        @Override
        public PaymentDetails createFromParcel(Parcel in) {
            return new PaymentDetails(in);
        }

        @Override
        public PaymentDetails[] newArray(int size) {
            return new PaymentDetails[size];
        }
    };

    public String getCustPhoneNo() {
        return custPhoneNo;
    }

    public void setCustPhoneNo(String custPhoneNo) {
        this.custPhoneNo = custPhoneNo;
    }

    public boolean getisDiscounted() {
        return isDiscounted;
    }

    public void setDiscounted(boolean discounted) {
        isDiscounted = discounted;
    }

    public boolean getToPrintBill() {
        return isToPrintBill;
    }

    public void setToPrintBill(boolean toPrintBill) {
        isToPrintBill = toPrintBill;
    }

    public double getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(double totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public double getTotalDiscountPercent() {
        return totalDiscountPercent;
    }

    public void setTotalDiscountPercent(double totalDiscountPercent) {
        this.totalDiscountPercent = totalDiscountPercent;
    }

    public double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public double getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(double cardAmount) {
        this.cardAmount = cardAmount;
    }

    public double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public double getPettyCash() {
        return pettyCash;
    }

    public void setPettyCash(double pettyCash) {
        this.pettyCash = pettyCash;
    }

    public double getmSwipeAmount() {
        return mSwipeAmount;
    }

    public void setmSwipeAmount(double mSwipeAmount) {
        this.mSwipeAmount = mSwipeAmount;
    }

    public double getmPaytmAmount() {
        return mPaytmAmount;
    }

    public void setmPaytmAmount(double mPaytmAmount) {
        this.mPaytmAmount = mPaytmAmount;
    }

    public double getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(double totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public double getTotalreturnAmount() {
        return totalreturnAmount;
    }

    public void setTotalreturnAmount(double totalreturnAmount) {
        this.totalreturnAmount = totalreturnAmount;
    }

    public double getTotalRoundOff() {
        return totalRoundOff;
    }

    public void setTotalRoundOff(double totalRoundOff) {
        this.totalRoundOff = totalRoundOff;
    }

    public double getTotalFinalBillAmount() {
        return totalFinalBillAmount;
    }

    public void setTotalFinalBillAmount(double totalFinalBillAmount) {
        this.totalFinalBillAmount = totalFinalBillAmount;
    }

    public boolean isOrderDelivered() {
        return isOrderDelivered;
    }

    public void setOrderDelivered(boolean orderDelivered) {
        isOrderDelivered = orderDelivered;
    }

    public ArrayList<AddedItemsToOrderTableClass> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<AddedItemsToOrderTableClass> orderList) {
        this.orderList = orderList;
    }

    public double getTotalBillAmount() {
        return totalBillAmount;
    }

    public void setTotalBillAmount(double totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    public double getTotalIGSTAmount() {
        return totalIGSTAmount;
    }

    public void setTotalIGSTAmount(double totalIGSTAmount) {
        this.totalIGSTAmount = totalIGSTAmount;
    }

    public double getTotalCGSTAmount() {
        return totalCGSTAmount;
    }

    public void setTotalCGSTAmount(double totalCGSTAmount) {
        this.totalCGSTAmount = totalCGSTAmount;
    }

    public double getTotalSGSTAmount() {
        return totalSGSTAmount;
    }

    public void setTotalSGSTAmount(double totalSGSTAmount) {
        this.totalSGSTAmount = totalSGSTAmount;
    }

    public double getTotalcessAmount() {
        return totalcessAmount;
    }

    public void setTotalcessAmount(double totalcessAmount) {
        this.totalcessAmount = totalcessAmount;
    }


    public double getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(double walletAmount) {
        this.walletAmount = walletAmount;
    }

    public double getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(double rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public int getNoOfPrint() {
        return noOfPrint;
    }

    public void setNoOfPrint(int noOfPrint) {
        this.noOfPrint = noOfPrint;
    }

    public boolean isDiscounted() {
        return isDiscounted;
    }

    public boolean isToPrintBill() {
        return isToPrintBill;
    }

    public double getAepsAmount() {
        return aepsAmount;
    }

    public void setAepsAmount(double aepsAmount) {
        this.aepsAmount = aepsAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(custPhoneNo);
        parcel.writeByte((byte) (isDiscounted ? 1 : 0));
        parcel.writeByte((byte) (isToPrintBill ? 1 : 0));
        parcel.writeDouble(totalDiscountAmount);
        parcel.writeDouble(totalDiscountPercent);
        parcel.writeDouble(cashAmount);
        parcel.writeDouble(cardAmount);
        parcel.writeDouble(couponAmount);
        parcel.writeDouble(walletAmount);
        parcel.writeDouble(aepsAmount);
        parcel.writeDouble(pettyCash);
        parcel.writeDouble(rewardPoints);
        parcel.writeDouble(totalPaidAmount);
        parcel.writeDouble(totalreturnAmount);
        parcel.writeDouble(totalRoundOff);
        parcel.writeDouble(totalFinalBillAmount);
        parcel.writeByte((byte) (isOrderDelivered ? 1 : 0));
        parcel.writeTypedList(orderList);
        parcel.writeDouble(totalBillAmount);
        parcel.writeDouble(totalIGSTAmount);
        parcel.writeDouble(totalCGSTAmount);
        parcel.writeDouble(totalSGSTAmount);
        parcel.writeDouble(totalcessAmount);
        parcel.writeInt(noOfPrint);
        parcel.writeDouble(mSwipeAmount);
        parcel.writeDouble(mPaytmAmount);
    }
}
