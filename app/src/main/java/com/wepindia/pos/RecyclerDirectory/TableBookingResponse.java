package com.wepindia.pos.RecyclerDirectory;

/**
 * Created by RakeshK on 8/4/2017.
 */

public class TableBookingResponse {
    private int sNo;
    private String customerName;
    private String timeBooking;
    private int tableNo;
    private String mobileNo;
    private int iTBookId;

    public int getiTBookId() {
        return iTBookId;
    }

    public void setiTBookId(int iTBookId) {
        this.iTBookId = iTBookId;
    }

    public int getsNo() {
        return sNo;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTimeBooking() {
        return timeBooking;
    }

    public void setTimeBooking(String timeBooking) {
        this.timeBooking = timeBooking;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }


}
