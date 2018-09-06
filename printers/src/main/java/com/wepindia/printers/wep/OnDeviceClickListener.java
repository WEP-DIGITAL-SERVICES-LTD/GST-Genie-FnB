package com.wepindia.printers.wep;

import java.util.HashMap;

/**
 * Created by SachinV on 20-03-2018.
 */

public interface OnDeviceClickListener {
    public void onDeviceClicked(String key, HashMap<String, String> device);
}
