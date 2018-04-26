package com.wepindia.pos.fragments.Model;

/**
 * Created by SachinV on 22-03-2018.
 */

public class PrinterChoice {

    private int mImageDrawable;
    private String mName;

    public PrinterChoice(int mImageDrawable, String mName) {
        this.mImageDrawable = mImageDrawable;
        this.mName = mName;
    }

    public int getmImageDrawable() {
        return mImageDrawable;
    }

    public void setmImageDrawable(int mImageDrawable) {
        this.mImageDrawable = mImageDrawable;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
