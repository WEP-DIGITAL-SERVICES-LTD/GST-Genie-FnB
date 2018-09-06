package com.wepindia.pos.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Administrator on 16-01-2018.
 */

public class EMOJI_FILTER implements  InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int index = start; index < end; index++) {

            int type = Character.getType(source.charAt(index));

            if (type == Character.SURROGATE) {
                return "";
            }
        }
        return null;
    }
}
