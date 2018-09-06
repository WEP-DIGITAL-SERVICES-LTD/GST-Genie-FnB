package com.wepindia.printers.wep;

import com.bixolon.printer.BixolonPrinter;
import com.epson.epos2.printer.Printer;

/**
 * Created by SachinV on 21-03-2018.
 */

public interface PrinterConnectionError {
    void onError (Printer printer, int errCode, String errMsg);
    void onError (BixolonPrinter printer, String errMsg);
    void onError (String errMsg);
    void onError (int iError);
}
