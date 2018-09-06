package com.wepindia.pos.GenericClasses;

import java.util.ArrayList;


public class TableSplitShfitResponse {

    private ArrayList<TableSplitNumberResponse> tableSplitNumberResponses;
    private int tableNumber;

    public ArrayList<TableSplitNumberResponse> getTableSplitNumberResponses() {
        return tableSplitNumberResponses;
    }

    public void setTableSplitNumberResponses(ArrayList<TableSplitNumberResponse> tableSplitNumberResponses) {
        this.tableSplitNumberResponses = tableSplitNumberResponses;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
}
