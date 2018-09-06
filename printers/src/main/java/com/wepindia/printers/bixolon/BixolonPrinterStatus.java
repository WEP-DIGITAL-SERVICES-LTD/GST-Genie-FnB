package com.wepindia.printers.bixolon;

import android.hardware.usb.UsbDevice;

import java.util.HashMap;
import java.util.Set;

public interface BixolonPrinterStatus {
    void onStatusChanged(int status);
    void onBixolonPrinterSelected(String key, HashMap<String, String> device);
    void onBixolonPrinterFound(Set<UsbDevice> usbDevices);
}
