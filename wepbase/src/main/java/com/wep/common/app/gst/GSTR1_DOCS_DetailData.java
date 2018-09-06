package com.wep.common.app.gst;

import java.util.ArrayList;

/**
 * Created by RichaA on 12/21/2017.
 */

public class GSTR1_DOCS_DetailData {
    private ArrayList<GSTR1_DOCS_Data> doc_det = new ArrayList<>();

    public GSTR1_DOCS_DetailData(ArrayList<GSTR1_DOCS_Data> doc_det) {
        this.doc_det = doc_det;
    }

    public ArrayList<GSTR1_DOCS_Data> getDoc_det() {
        return doc_det;
    }

    public void setDoc_det(ArrayList<GSTR1_DOCS_Data> doc_det) {
        this.doc_det = doc_det;
    }
}
