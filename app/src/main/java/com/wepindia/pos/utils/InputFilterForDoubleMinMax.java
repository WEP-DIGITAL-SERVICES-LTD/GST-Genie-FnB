package com.wepindia.pos.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterForDoubleMinMax implements InputFilter {

    private double min, max;

    public InputFilterForDoubleMinMax(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterForDoubleMinMax(String min, String max) {
        this.min = Double.parseDouble(min);
        this.max = Double.parseDouble(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String temp;
            if (dstart == 0) {
                temp = source.toString();
            } else {
                temp = dest.toString() + source.toString();
            }
            if (source.toString().equalsIgnoreCase(".")) {
                return ".";
            } else if (temp.toString().indexOf(".") != -1) {
                temp = temp.toString().substring(temp.toString().indexOf(".") + 1);
                if (temp.length() > 2) {
                    return "";
                }
            }


            double input;
            if (dstart == 0) {
                input = Double.parseDouble(source.toString().replace(",", ".").replace("€", ""));

            } else {
                input = Double.parseDouble(dest.toString().replace(",", ".").replace("€", "") + source.toString().replace(",", ".").replace("€", ""));
            }

            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

}
