package com.wep.common.app.gst;

import java.util.ArrayList;

/**
 * Created by RichaA on 5/3/2017.
 */

public class GSTR2_B2B_A_Data_Unregistered {

    private ArrayList<GSTR2_B2B_A_invoices_Unregistered> inv;

    public GSTR2_B2B_A_Data_Unregistered(ArrayList<GSTR2_B2B_A_invoices_Unregistered> inv) {
        this.inv = inv;
    }

    public ArrayList<GSTR2_B2B_A_invoices_Unregistered> getInv() {
        return inv;
    }

    public void setInv(ArrayList<GSTR2_B2B_A_invoices_Unregistered> inv) {
        this.inv = inv;
    }
}
