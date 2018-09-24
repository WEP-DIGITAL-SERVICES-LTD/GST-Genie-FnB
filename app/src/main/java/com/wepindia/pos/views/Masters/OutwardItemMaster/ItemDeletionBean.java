package com.wepindia.pos.views.Masters.OutwardItemMaster;

public class ItemDeletionBean {

    private int sno;
    private int itemId;
    private String itemName;
    private double itemMrp;
    private int checkBox;

    public ItemDeletionBean() {
    }

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemMrp() {
        return itemMrp;
    }

    public void setItemMrp(double itemMrp) {
        this.itemMrp = itemMrp;
    }

    public int getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(int checkBox) {
        this.checkBox = checkBox;
    }
}
