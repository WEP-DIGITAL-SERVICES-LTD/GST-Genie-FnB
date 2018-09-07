package com.wepindia.printers;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.save.PortParamDataBase;
import com.gprinter.service.GpPrintService;

import com.ngx.BluetoothPrinter;
import com.ngx.PrinterWidth;
import com.wep.common.app.Database.Customer;
import com.wep.common.app.Database.PaymentReceipt;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.SalesReturnBean;
import com.wep.common.app.models.SalesReturnPrintBean;
import com.wep.common.app.print.BillKotItem;
import com.wep.common.app.print.BillServiceTaxItem;
import com.wep.common.app.print.BillTaxItem;
import com.wep.common.app.print.BillTaxSlab;
import com.wep.common.app.print.Payment;
import com.wep.common.app.print.PrintIngredientsModel;
import com.wep.common.app.print.PrintKotBillItem;
import com.wep.common.app.utils.Preferences;
import com.wepindia.printers.heydey.DiscoverBluetoothPrinter;
import com.wepindia.printers.heydey.Util;
import com.wepindia.printers.utils.PrinterUtil;
import com.wepindia.printers.wep.OnDeviceClickListener;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public abstract class HeyDeyBaseActivity extends WepBaseActivity implements View.OnClickListener, OnDeviceClickListener {

    private GpService mGpService = null;
    private static final String DEBUG_TAG = HeyDeyBaseActivity.class.getSimpleName();
    public static final String ACTION_CONNECT_STATUS = "action.connect.status";
    private HeyDeyBaseActivity.PrinterServiceConnection conn = null;
    private PortParameters mPortParam = null;
    int printerNum = 0;
    private String printType = "";
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int REQUEST_CONNECT_DEVICE = 3;
    public static final int REQUEST_USB_DEVICE = 4;
    private int code = 0;
    private String name = "sachinverma";
    private PrintKotBillItem item;
    private String tmpList;
    private List<List<String>> itemReport;
    protected PrinterUtil printerUtil = null;
    private OnDeviceClickListener onDeviceClickListener = null;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isDeviceClicked = false;
    private String address = "";
    private boolean isRegistered = false;
    private int PRINTER_CODE = -1; // -1 for no printer config 0 for heyday and 1 for NGX

    public static BluetoothPrinter mBtp = null;

    public abstract void onConfigurationRequired();

    public abstract void onPrinterAvailable(int flag);

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("ServiceConnection", "onServiceConnected() called");
            mGpService = GpService.Stub.asInterface(service);
            checkPrinterConfig(false);
            if (isConnected) {
//                letsRockAndRoll();
            }
        }
    }

    void letsRockAndRoll(){
        if (mPortParam != null && !address.isEmpty()) {
            mPortParam.setBluetoothAddr(address);
            mPortParam.setPortType(4); // 4 For Bluetooth Connectivity
            mPortParam.setIpAddr(mPortParam.getIpAddr());
            mPortParam.setPortNumber(mPortParam.getPortNumber());
            mPortParam.setUsbDeviceName(mPortParam.getUsbDeviceName());
            if (CheckPortParamters(mPortParam)) {
                PortParamDataBase database = new PortParamDataBase(this);
                database.deleteDataBase("" + printerNum);
                database.insertPortParam(printerNum, mPortParam);
                // Write Print code
                //Toast.makeText(this, "Value inserted", Toast.LENGTH_SHORT).show();
                if (printType.equalsIgnoreCase("TEST")) {
                    Intent intent = new Intent();
                    intent.putExtra("code", code);
                    intent.putExtra("name", name);
                    intent.putExtra("printer", printerNum);
                    setResult(Activity.RESULT_OK, intent);
                    //finish();

                }
                try {
                    connectOrDisConnectToDevice();
                } catch (DeadObjectException e) {
                    Log.i("HeyDey", "Error on connect to device : " + e.getMessage());
                }
            } else {
                messageBox(getString(R.string.port_parameters_wrong));
            }
        }
        address = "";
        isDeviceClicked = false;
    }

    @Override
    public void onDeviceClicked(String key, HashMap<String, String> device) {
//        String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                printerUtil = new PrinterUtil(HeyDeyBaseActivity.this);
                connection();
                if (onDeviceClickListener != null)
                    onDeviceClickListener.onDeviceClicked("Target", device);

                if(printerUtil == null) {
                    printerUtil = new PrinterUtil(HeyDeyBaseActivity.this);
                    connection();
                    //registerBroadcast();
                } else {
                    if (onDeviceClickListener != null)
                        onDeviceClickListener.onDeviceClicked("Target", device);

                    address = device.get("Heyday");
                    if (mPortParam != null) {
                        mPortParam.setBluetoothAddr(address);
                        mPortParam.setPortType(4); // 4 For Bluetooth Connectivity
                        mPortParam.setIpAddr(mPortParam.getIpAddr());
                        mPortParam.setPortNumber(mPortParam.getPortNumber());
                        mPortParam.setUsbDeviceName(mPortParam.getUsbDeviceName());
                        if (CheckPortParamters(mPortParam)) {
                            PortParamDataBase database = new PortParamDataBase(this);
                            database.deleteDataBase("" + printerNum);
                            database.insertPortParam(printerNum, mPortParam);
                            // Write Print code
                            //Toast.makeText(this, "Value inserted", Toast.LENGTH_SHORT).show();
                            if (printType.equalsIgnoreCase("TEST")) {
                                Intent intent = new Intent();
                                intent.putExtra("code", code);
                                intent.putExtra("name", name);
                                intent.putExtra("printer", printerNum);
                                setResult(Activity.RESULT_OK, intent);
                                //finish();

                            }
                            try {
                                connectOrDisConnectToDevice();
                            } catch (DeadObjectException e) {
                                Log.i("HeyDey", "Error on connect to device : " + e.getMessage());
                            }
                        } else {
                            messageBox(getString(R.string.port_parameters_wrong));
                        }
                    } else {
                        isDeviceClicked = true;
                    }
                }
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("PrinterConfigurationActivity", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (getPrinterName(this, "bill").equalsIgnoreCase("Heyday")){
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    printerUtil = new PrinterUtil(HeyDeyBaseActivity.this);
                    connection();
                    //registerBroadcast();
                }
            }
            registerBroadcast();
            PRINTER_CODE = 2;
        } else   if (getPrinterName(this, "bill").equalsIgnoreCase("NGX")){
            try {
                if (mBtp == null)
                    mBtp = BluetoothPrinter.INSTANCE;
                mBtp.initService(this, mHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mBtp.setPrinterWidth(PrinterWidth.PRINT_WIDTH_72MM);
            PRINTER_CODE = 1;
        }
    }

    public void mConnectNGX() {
        if (mBtp != null && mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
            mBtp.connectToPrinter();
        }
    }

    public void mConnect() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                printerUtil = new PrinterUtil(HeyDeyBaseActivity.this);
                connection();
                //registerBroadcast();
            }
        }
        registerBroadcast();
    }

    public String getPrinterName(Activity activity, String module) {
        return Preferences.getSharedPreferencesForPrint(activity).getString(module, "--Select--");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            Log.d(DEBUG_TAG, "requestCode" + requestCode + '\n' + "resultCode" + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_ENABLE_BT) {
                if (resultCode == Activity.RESULT_OK) {
                    // bluetooth is opened , select bluetooth device fome list
//                    Intent intent = new Intent(HeyDeyBaseActivity.this, BluetoothDeviceList.class);
//                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);


                    FragmentManager fm = getSupportFragmentManager();
                    DiscoverBluetoothPrinter discoverBluetoothPrinter = new DiscoverBluetoothPrinter();
                    discoverBluetoothPrinter.mInitListener(HeyDeyBaseActivity.this);
                    discoverBluetoothPrinter.show(fm, "Discover new devices");

                } else {
                    // bluetooth is not open
                    Toast.makeText(this, R.string.bluetooth_is_not_enabled, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_USB_DEVICE) {
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
                    mPortParam.setUsbDeviceName(address);
                }
            } else if (requestCode == REQUEST_CONNECT_DEVICE) {
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
                    mPortParam.setBluetoothAddr(address);
                    mPortParam.setPortType(4/*mPortParam.getPortType()*/); // 4 For Bluetooth Connectivity
                    mPortParam.setIpAddr(mPortParam.getIpAddr());
                    mPortParam.setPortNumber(mPortParam.getPortNumber());
                    mPortParam.setUsbDeviceName(mPortParam.getUsbDeviceName());
                    if (CheckPortParamters(mPortParam)) {
                        PortParamDataBase database = new PortParamDataBase(this);
                        database.deleteDataBase("" + printerNum);
                        database.insertPortParam(printerNum, mPortParam);
                        // Write Print code
                        //Toast.makeText(this, "Value inserted", Toast.LENGTH_SHORT).show();
                        if (printType.equalsIgnoreCase("TEST")) {
                            Intent intent = new Intent();
                            intent.putExtra("code", code);
                            intent.putExtra("name", name);
                            intent.putExtra("printer", printerNum);
                            setResult(Activity.RESULT_OK, intent);
                            //finish();

                        }
                        try{
                            connectOrDisConnectToDevice();
                        }catch (DeadObjectException e){

                        }
                    } else {
                        messageBox(getString(R.string.port_parameters_wrong));
                    }

                } else {
                    messageBox(getString(R.string.port_parameters_is_not_save));
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    public void setPrinterCode (int printerCode) {
        PRINTER_CODE = printerCode;
        switch (printerCode) {
            case 0: // HeyDay
                if (mBtp != null) {
                    mBtp.closeService();
                    mBtp = null;
                }
                registerBroadcast();
                break;
            case 1: // NGX
                if (conn != null) {
                    unbindService(conn);
                    conn = null;
                }
                break;
        }
    }

    public void askForConfig(OnDeviceClickListener onDeviceClickListener) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            messageBox("Bluetooth is not supported by the device");
        } else {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
//                Intent intent = new Intent(HeyDeyBaseActivity.this, BluetoothDeviceList.class);
//                startActivityForResult(intent, REQUEST_CONNECT_DEVICE);

                this.onDeviceClickListener = onDeviceClickListener;

                FragmentManager fm = getSupportFragmentManager();
                DiscoverBluetoothPrinter discoverBluetoothPrinter = new DiscoverBluetoothPrinter();
                discoverBluetoothPrinter.mInitListener(HeyDeyBaseActivity.this);
                discoverBluetoothPrinter.show(fm, "Discover new devices");

            }
        }
    }

    public void askForConfigNGX(OnDeviceClickListener onDeviceClickListener) {
        try {
            if (mBtp == null)
                mBtp = BluetoothPrinter.INSTANCE;
            mBtp.initService(this, mHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBtp.showDeviceList(this);
        mBtp.setPrinterWidth(PrinterWidth.PRINT_WIDTH_72MM);
        if(onDeviceClickListener != null) {
            this.onDeviceClickListener = onDeviceClickListener;
        }
    }

    private String mConnectedDeviceName = "";

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothPrinter.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothPrinter.STATE_CONNECTED:
                            /*tvStatus.setText(title_connected_to);
                            tvStatus.append(mConnectedDeviceName);*/
                            messageBox("Connected to: " + mConnectedDeviceName);
                            /*editor.putString("bill", "NGX");
                            editor.putString("receipt", "NGX");
                            editor.putString("deposit_receipt", "NGX");
                            editor.putString("report", "NGX");
                            editor.putString("NGX", "target");
                            editor.commit();*/
                            HashMap<String, String> device = new HashMap<>();
                            device.put("PrinterName", "NGX");
                            device.put("NGX", "Target");
                            if (onDeviceClickListener != null)
                                onDeviceClickListener.onDeviceClicked("Target", device);
                            onPrinterAvailable(5);
                            break;
                        case BluetoothPrinter.STATE_CONNECTING:
//                            tvStatus.setText(title_connecting);
                            onPrinterAvailable(2);
                            break;
                        case BluetoothPrinter.STATE_LISTEN:
                        case BluetoothPrinter.STATE_NONE:
//                            tvStatus.setText(title_not_connected);
                            onPrinterAvailable(0);
                            break;
                    }
                    break;
                case BluetoothPrinter.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(
                            BluetoothPrinter.DEVICE_NAME);
                    break;
                case BluetoothPrinter.MESSAGE_STATUS:
                    /*tvStatus.setText(msg.getData().getString(
                            BluetoothPrinter.STATUS_TEXT));*/
                    break;
                default:
                    break;
            }
        }
    };

    public void askForConfig() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            messageBox("Bluetooth is not supported by the device");
        } else {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
//                Intent intent = new Intent(HeyDeyBaseActivity.this, BluetoothDeviceList.class);
//                startActivityForResult(intent, REQUEST_CONNECT_DEVICE);

                this.onDeviceClickListener = null;

                FragmentManager fm = getSupportFragmentManager();
                DiscoverBluetoothPrinter discoverBluetoothPrinter = new DiscoverBluetoothPrinter();
                discoverBluetoothPrinter.mInitListener(HeyDeyBaseActivity.this);
                discoverBluetoothPrinter.show(fm, "Discover new devices");

            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(DEBUG_TAG, "onDestroy ");
        super.onDestroy();
        if (isRegistered)
            this.unregisterReceiver(PrinterStatusBroadcastReceiver);
        isRegistered = false;
        if (conn != null) {
            unbindService(conn);
        }
        if (mBtp != null)
            mBtp.onActivityDestroy();
    }

    //For testing commented on 16/08/2018
   /* @Override
    public void onPause() {
        if (mBtp != null)
            mBtp.onActivityPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mBtp != null)
            mBtp.onActivityResume();
        super.onResume();
    }
*/
    boolean isConnected = false;

    private void connection() {
        if (conn != null) {
            unbindService(conn);
        }
        conn = new HeyDeyBaseActivity.PrinterServiceConnection();
        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
        intent.setPackage(this.getPackageName());
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
        isConnected = true;
    }

    public void disconnect(){
        if (isConnected)
            this.unregisterReceiver(PrinterStatusBroadcastReceiver);
        if (conn != null) {
            unbindService(conn);
        }
        isConnected = false;
    }

    private void checkPrinterConfig(boolean isErrorCorrection) {
        String str = new String();
        boolean[] state = getConnectState();
        PortParamDataBase database = new PortParamDataBase(this);
        mPortParam = new PortParameters();
        mPortParam = database.queryPortParamDataBase("" + printerNum);
        mPortParam.setPortOpenState(state[printerNum]);
        if (mPortParam.getBluetoothAddr().equalsIgnoreCase("") || state[printerNum] == false) {
            if (mPortParam.getBluetoothAddr().equalsIgnoreCase("")) {
                // Printer Not Configured
                str = "Configure";
                //btnConfig.setText(str);
                onConfigurationRequired();
            } else if (state[printerNum] == false) {
                // Printer port not connected
                str = "Connect";
                //btnConfig.setText(str);
                //btnConnect.setVisibility(View.GONE);
                try{
                    connectOrDisConnectToDevice();
                }catch (DeadObjectException e){

                }
            }
        } else {
            try {
                int status = mGpService.queryPrinterStatus(printerNum, 500);

                if (status == GpCom.STATE_NO_ERR) {
                    //"The printer is OK";
                    str = "Disconnect";
                    //btnConfig.setText(str);
                    //btnConnect.setVisibility(View.VISIBLE);
                    if (printType.equalsIgnoreCase("TEST")) {
                        Intent intent = new Intent();
                        intent.putExtra("code", code);
                        intent.putExtra("name", name);
                        intent.putExtra("printer", printerNum);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    setResultt();
                } else {
                    str = "printer ";
                    if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                        str += "Offline";
                    }
                    if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                        str += "Out of paper";
                    }
                    if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                        str += "The printer is opened";
                    }
                    if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                        str += "Printer error";
                    }
                    if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                        str += "The query timed out";
                    }
                }
                if (isErrorCorrection && status == 0) {
                    printReceiptPrint();
                }
                //Toast.makeText(this, "printerï¼š" + printerNum + " statusï¼š" + str, Toast.LENGTH_SHORT).show();
            } catch (RemoteException e1) {
                Toast.makeText(this, "exception", Toast.LENGTH_SHORT).show();
                e1.printStackTrace();
            }
        }
    }

    public boolean[] getConnectState() {
        boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            state[i] = false;
        }
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            try {
                if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
                    state[i] = true;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return state;
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONNECT_STATUS);
        this.registerReceiver(PrinterStatusBroadcastReceiver, filter);
    }

    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isRegistered = true;
            if (ACTION_CONNECT_STATUS.equals(intent.getAction())) {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                Log.d(DEBUG_TAG, "connect status " + type);
                onPrinterAvailable(type);
                if (type == GpDevice.STATE_CONNECTING) {
                    //btnConfig.setText("Connecting...");
//                    onPrinterAvailable(type);
                } else if (type == GpDevice.STATE_NONE) {
                    //btnConfig.setText("Connect");
                    //btnConnect.setVisibility(View.GONE);
                    /*try{
                        connectOrDisConnectToDevice();
                    }catch (DeadObjectException e){

                    }*/
//                    onPrinterAvailable(type);
                } else if (type == GpDevice.STATE_VALID_PRINTER) {
                    //btnConfig.setText("Disconnect");
                    //btnConnect.setVisibility(View.VISIBLE);
                    //connectOrDisConnectToDevice();
                    //setResultt();
                    //onPrinterAvailable(type);

                } else if (type == GpDevice.STATE_INVALID_PRINTER) {
                    messageBox("Please use Gprinter!");
                }
            }
        }
    };

    public void setResultt() {
        if (printType.equalsIgnoreCase("TEST")) {
            printReceiptPrint();
        }
    }

    public void connectOrDisConnectToDevice() throws DeadObjectException {
        int rel = 0;
        if (mPortParam.getPortOpenState() == false) {
            if (CheckPortParamters(mPortParam)) {
                switch (mPortParam.getPortType()) {
                    case PortParameters.USB:
                        try {
                            rel = mGpService.openPort(printerNum, mPortParam.getPortType(), mPortParam.getUsbDeviceName(), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case PortParameters.ETHERNET:
                        try {
                            rel = mGpService.openPort(printerNum, mPortParam.getPortType(), mPortParam.getIpAddr(), mPortParam.getPortNumber());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case PortParameters.BLUETOOTH:
                        try {
                            rel = mGpService.openPort(printerNum, mPortParam.getPortType(), mPortParam.getBluetoothAddr(), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i("HeyDey","Connect device error : " +e.getMessage());
                        }
                        break;
                }
                GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                if (r != GpCom.ERROR_CODE.SUCCESS) {
                    if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                        mPortParam.setPortOpenState(true);
                        //btnConfig.setText("Disconnect");
                        //btnConnect.setVisibility(View.VISIBLE);
                        if (printType.equalsIgnoreCase("TEST")) {
                            Intent intent = new Intent();
                            intent.putExtra("code", code);
                            intent.putExtra("name", name);
                            intent.putExtra("printer", printerNum);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        setResultt();
                    } else {
                        messageBox(GpCom.getErrorText(r));
                    }
                }
            } else {
                messageBox(getString(R.string.port_parameters_wrong));
            }
        } else {
            Log.d(DEBUG_TAG, "Disconnecting to Device ");
            //btnConfig.setText("Disconnecting");
            try {
                mGpService.closePort(printerNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Boolean CheckPortParamters(PortParameters param) {
        boolean rel = false;
        int type = param.getPortType();
        if (type == PortParameters.BLUETOOTH) {
            if (!param.getBluetoothAddr().equals("")) {
                rel = true;
            }
        } else if (type == PortParameters.ETHERNET) {
            if ((!param.getIpAddr().equals("")) && (param.getPortNumber() != 0)) {
                rel = true;
            }
        } else if (type == PortParameters.USB) {
            if (!param.getUsbDeviceName().equals("")) {
                rel = true;
            }
        }
        return rel;
    }

    private void messageBox(String err) {
        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
    }

    public void printReceiptPrint() {
        try {
            int type = mGpService.getPrinterCommandType(printerNum);
            if (type == GpCom.ESC_COMMAND) {
                int status = mGpService.queryPrinterStatus(printerNum, 500);
                if (status == GpCom.STATE_NO_ERR)
                {
                    if (printType.equalsIgnoreCase("TEST")) {
                        testPrint();
                    } else {
                        int rel;
                        try {
                            rel = mGpService.sendEscCommand(printerNum, tmpList);
                            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                            if (r != GpCom.ERROR_CODE.SUCCESS) {
                                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(status == GpCom.STATE_OFFLINE)
                {
                    Toast.makeText(this, "Printer Offline", Toast.LENGTH_SHORT).show();
//                    checkPrinterConfig(false);
                }
                else
                {
                    //Toast.makeText(getApplicationContext(), "Printer error!", Toast.LENGTH_SHORT).show();
                    try {
                        checkPrinterConfig(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    void testPrint() {
        EscCommand esc = new EscCommand();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
        esc.addText("Sample\n");
        esc.addPrintAndLineFeed();

        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText("Print text\n");
        esc.addText("Welcome to use Gprinter!\n");

        String message = Util.SimToTra("ä½³åšç¥¨æ®æ‰“å°æœº\n");
        esc.addText(message, "GB2312");
        esc.addPrintAndLineFeed();

        esc.addText("Print bitmap!\n");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.gprinter);
        esc.addRastBitImage(b, b.getWidth(), 0);

        esc.addText("Print code128\n");
        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
        esc.addSetBarcodeHeight((byte) 60);
        esc.addCODE128("Gprinter");
        esc.addPrintAndLineFeed();


        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        esc.addText("Completed!\r\n");
        esc.addPrintAndFeedLines((byte) 2);

        Vector<Byte> datas = esc.getCommand();
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendEscCommand(printerNum, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void feedPrinter(){
        EscCommand esc = new EscCommand();
        esc.addPrintAndLineFeed();
        Vector<Byte> datas = esc.getCommand();
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendEscCommand(printerNum, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void printHeydeyKOT(PrintKotBillItem item, String type) {
        if(printerUtil != null) {
            printType = type;
            tmpList = printerUtil.getPrintKOT(item);
            printReceiptPrint();
        }
    }

    public void printHeydeyBILL(PrintKotBillItem item, String type) {
        if(printerUtil != null) {
            printType = type;
            tmpList = printerUtil.getPrintBill(item);
            printReceiptPrint();
        }
    }

    public void printHeydeySalesReturn(SalesReturnPrintBean item, String type) {
        if(printerUtil != null) {
            printType = type;
//            tmpList = printerUtil.getPrintSalesReturn(item);
            printReceiptPrint();
        }
    }

    private String getFormatedCharacterForPrint(String txt, int limit,int type) {
        if(txt.length()<limit){
            return txt+getSpaces(limit-txt.length(),type);
        }else {
            return txt.substring(0,limit);
        }
    }
    private String getFormatedCharacterForPrint_init(String txt, int limit,int type) {
        if(txt.length()<limit){
            return getSpaces(limit-txt.length(),type)+txt;
        }else {
            return txt.substring(0,limit);
        }
    }

    public String getSpaces(int num,int type)
    {
        StringBuffer sb = new StringBuffer();
        if(type==0)
        {
            for (int i=0;i<num;i++)
            {
                sb.append(getResources().getString(R.string.superSpace));
            }
        }
        else
        {
            for (int i=0;i<num;i++)
            {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public String getPreAddedSpaceFormat(String sourceTxt, String toAddTxt, int max,int type)
    {
        return sourceTxt+getSpaces(max-(sourceTxt.length()+toAddTxt.length()),type)+toAddTxt;
    }

    private String getSpaceFormat(String txtPre, String txtPercent, String txtPost, int num, int type) {
        return txtPre+getSpaces(num-(txtPre.length()+txtPercent.length()+txtPost.length()),type)+txtPost+txtPercent;
    }

    private String getSpaceFormater(String txtPre, String txtPost, int num, int type) {
        return txtPre+getSpaces(num-(txtPre.length()+txtPost.length()),type)+txtPost;
    }

    public String getAbsoluteCharacter(String str, int num,int type) {
        String strToDo = "";
        if(str.length() > num)
        {
            strToDo = str.substring(0,num);
        }
        else
        {
            strToDo = str;
        }
        String preTxt = getPostAddedSpaceFormat("", String.valueOf(strToDo),num,type);
        return preTxt;
    }

    private String getAbsoluteCharacter1(String str,int type) {
        return getSpaces(42-str.length(),type)+str;
    }

    public String getPostAddedSpaceFormat(String sourceTxt, String toAddTxt, int max,int type)
    {
        return sourceTxt+toAddTxt+getSpaces(max-(sourceTxt.length()+toAddTxt.length()),type);
    }

    public void printHeydeyIngredients(ArrayList<PrintIngredientsModel> item, String type) {
        printType = type;
        tmpList = printerUtil.getPrintIngredients(item);
        printReceiptPrint();
    }

    public void printHeydeyReport(List<List<String>> Report, String ReportName, String type) {
        printType = type;
        String reportName = ReportName;
        itemReport = Report;
        tmpList = printerUtil.getPrintReport(itemReport, reportName);
        printReceiptPrint();
    }

    public void printHeydeyPayment(Payment item, String ReportName, String type) {
        if(printerUtil != null) {
            printType = type;
            String reportName = ReportName;
            tmpList = printerUtil.getPrintMSwipePaymentBill(item, reportName);
            printReceiptPrint();
        }
    }

    public void printHeydeyPaymentReceipt(PaymentReceipt item, String type) {
        if(printerUtil != null) {
            printType = type;
            tmpList = printerUtil.getPrintPaymentReceipt(item);
            printReceiptPrint();
        }
    }

    public void printHeydeyDepositAmountReceipt(Customer item, String type) {
        if(printerUtil != null) {
            printType = type;
//            tmpList = printerUtil.getPrintDepositAmountReceipt(item);
            printReceiptPrint();
        }
    }

    public void printTest() {
        testPrint();
//        feedprinter();
    }

    public void printNGXBILL(PrintKotBillItem item, String type) {
        if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
            Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (item.getCompanyLogoPath() != null
                    && !item.getCompanyLogoPath().isEmpty()
                    && !item.getCompanyLogoPath().equalsIgnoreCase("1234567890")) {
                mBtp.printImage(item.getCompanyLogoPath());
            }

            Typeface tf = Typeface.createFromAsset(getAssets(), "Shruti.ttf");

            TextPaint tp = new TextPaint();
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(30);
            tp.setColor(Color.BLACK);
//            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);
            mBtp.addText("TAX INVOICE" + item.getIsDuplicate(), Layout.Alignment.ALIGN_CENTER, tp);

            if (item.getBoldHeader() == 1) {
                tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            } else {
                tp.setTypeface(Typeface.create(tf, Typeface.NORMAL));
            }

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                mBtp.addText(item.getHeaderLine1(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                mBtp.addText(item.getHeaderLine2(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                mBtp.addText(item.getHeaderLine3(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                mBtp.addText(item.getHeaderLine4(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                mBtp.addText(item.getHeaderLine5(), Layout.Alignment.ALIGN_CENTER, tp);
            }

            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);
            StringBuilder stringBuilder = new StringBuilder();

            if (item.getOwnerDetail() == 1) {
                stringBuilder.append("GSTIN     : "+item.getAddressLine1()+"\n");
                stringBuilder.append("Name      : "+item.getAddressLine2()+"\n");
                stringBuilder.append("Address   : "+item.getAddressLine3()+"\n");
            }

            stringBuilder.append("\n");
            stringBuilder.append("Bill no         : "+item.getBillNo()+"\n");
            if(item.getBillingMode().equals("1"))
                stringBuilder.append("Table           : "+item.getTableNo()+"\n");
            stringBuilder.append("Date            : "+item.getDate() +"    Time : "+item.getTime() +"\n");
            stringBuilder.append("Cashier         : "+item.getOrderBy()+"\n");

            if (!item.getCustomerName().equals("") && !item.getCustomerName().contains("-")) {
                stringBuilder.append("Customer Name   : "+item.getCustomerName()+"\n");
            }
            if (!item.getSalesManId().equals("") && !item.getSalesManId().contains("-")) {
                stringBuilder.append("Salesman Id     : "+item.getSalesManId()+"\n");
            }

            if(item.getBillingMode().equalsIgnoreCase("4") || item.getBillingMode().equalsIgnoreCase("3")) {
                stringBuilder.append("Payment Status  : " + item.getPaymentStatus()+"\n");
            }

            if (item.getPrintService() == 1) {
                if(item.getBillingMode().equalsIgnoreCase("1") || item.getBillingMode().equalsIgnoreCase("2") ||
                        item.getBillingMode().equalsIgnoreCase("3") || item.getBillingMode().equalsIgnoreCase("4")){
                    stringBuilder.append("Service         : "+ item.getStrBillingModeName() + "\n");
                } else {
                    stringBuilder.append("-----------" + "\n");
                }
            }

            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            stringBuilder.append("================================================"+"\n");

            if(item.getAmountInNextLine() ==0) {
                stringBuilder.append("SI  ITEM NAME            QTY     RATE    AMOUNT " + "\n");
                if(item.getHSNPrintEnabled_out()== 1)
                {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        stringBuilder.append("HSN       DISC"+"\n");
                    } else {
                        stringBuilder.append("HSN"+"\n");
                    }
                } else {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        stringBuilder.append("          DISC"+"\n");
                    }
                }
            }
            else {
                if(item.getHSNPrintEnabled_out()== 1)
                {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        stringBuilder.append("SI  ITEM NAME            QTY     RATE           "+ "\n");
                        stringBuilder.append("HSN       DISC                          AMOUNT " + "\n");
                    } else {
                        stringBuilder.append("SI  ITEM NAME            QTY     RATE           "+ "\n");
                        stringBuilder.append("HSN                                     AMOUNT " + "\n");
                    }
                }
                else {
                    if (item.getItemWiseDiscountPrint() == 1) {
                        stringBuilder.append("SI  ITEM NAME            QTY     RATE            "+"\n");
                        stringBuilder.append("          DISC                           AMOUNT" + "\n");
                    } else {
                        stringBuilder.append("SI  ITEM NAME            QTY     RATE            "+"\n");
                        stringBuilder.append("                                         AMOUNT" + "\n");
                    }
                }
            }

            stringBuilder.append("================================================"+"\n");
            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            int totalitemtypes =0;
            double totalquantitycount =0;
            double subtotal =0;

            while (it.hasNext())
            {

                BillKotItem billKotItem = (BillKotItem) it.next();

                String preId = getPostAddedSpaceFormat("",String.valueOf(billKotItem.getItemId()),3,1);
                String preName = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getItemName()),billKotItem.getItemName().length(),1),17,1);
                String HSN = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getHSNCode()),10,1),8,1);
                String itemWiseDisc = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(billKotItem.getDiscount()),10,1),10,1);

                String preQty = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.valueOf(billKotItem.getQty())+billKotItem.getUOM(),9,1),10,1);
                String preRate = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getRate()),7,1),8,1);
                String preAmount = "0";
                String pre = "";
                if(String.format("%.2f",billKotItem.getAmount()).length() <= 9)
                {
                    preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getAmount())
                            +billKotItem.getTaxIndex(),9,1),10,1);
                    if(billKotItem.getItemName().length() > 16) {
                        pre = preId + preName.substring(0,16)+" " +/*HSN+*/preQty + preRate + preAmount;
                        stringBuilder.append(pre+"\n");
                        if(preName.length() > 32) {
                            stringBuilder.append("   " + preName.substring(16, 32) + "\n");
                        } else {
                            stringBuilder.append("   " + preName.substring(16, preName.length()) + "\n");
                        }
                    } else {
                        pre = preId + preName +/*HSN+*/preQty + preRate + preAmount;
                        stringBuilder.append(pre+"\n");
                    }
                    if(item.getHSNPrintEnabled_out() == 1) {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            stringBuilder.append(HSN + itemWiseDisc +"\n");
                        } else {
                            stringBuilder.append(HSN+"\n");
                        }
                    } else {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            stringBuilder.append(getFormatedCharacterForPrint_init(itemWiseDisc,20,1) +"\n");
                        }
                    }
                }
                else // item.getAmountInNextLine() ==1
                {
                    preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getAmount())
                            +billKotItem.getTaxIndex(),(String.format("%.2f",billKotItem.getAmount()) +billKotItem.getTaxIndex() ).length(),1),(String.format("%.2f",billKotItem.getAmount()) +billKotItem.getTaxIndex() ).length(),1);
                    String pre2 = "", pre3 ="", pre4 = "";
                    if(item.getHSNPrintEnabled_out() == 1) {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            pre2 = getPostAddedSpaceFormat("",HSN,8,1);
                            pre4 = getPostAddedSpaceFormat("",itemWiseDisc,8,1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount,28,1);
                        } else {
                            pre2 = getPostAddedSpaceFormat("",HSN,8,1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount,37,1);
                        }
                    }else
                    {
                        if (item.getItemWiseDiscountPrint() == 1) {
                            pre4 = getFormatedCharacterForPrint_init(itemWiseDisc,18,1);
                            pre3 = getFormatedCharacterForPrint_init(preAmount,30,1);
                        } else {
                            pre3 = getFormatedCharacterForPrint_init(preAmount,48,1);
                        }
                    }
                    if(billKotItem.getItemName().length() > 16) {
                        if(preName.length() > 32){
                            pre = preId + preName.substring(0,16)+" " +/*HSN+*/preQty + preRate + "\n" + "   " + preName.substring(16,32)+ "\n" + pre2 + pre4 + pre3;
                        } else {
                            pre = preId + preName.substring(0,16)+" " +/*HSN+*/preQty + preRate + "\n" + "   " + preName.substring(16,preName.length())+ "\n" + pre2 + pre4 + pre3;
                        }
                    } else {
                        pre = preId + preName +/*HSN+*/preQty + preRate + "\n" + pre2 + pre4 + pre3;
                    }
                    stringBuilder.append(pre+"\n");
                }

                totalitemtypes++;
                totalquantitycount += billKotItem.getQty();
                subtotal += billKotItem.getAmount();
            }

            stringBuilder.append("------------------------------------------------"+"\n");
            stringBuilder.append(getSpaceFormater("Total Item(s) : "+totalitemtypes+" /Qty : "+totalquantitycount,String.format("%.2f",subtotal),48,1)+"\n");
            double discount = item.getFdiscount();
            float discountPercentage = item.getdiscountPercentage();
            if(discountPercentage > 0)
            {
                String DiscName = getPostAddedSpaceFormat("","Discount Amount",23,1);
                String DiscPercent = getPostAddedSpaceFormat("","@ " + String.format("%.2f",discountPercentage) + " %",15,1);
                String DiscValue = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",discount),10,1),8 ,1);
                String pre = DiscName + DiscPercent + DiscValue;
                stringBuilder.append(pre+"\n");
            }
            else if (discount > 0)
            {
                stringBuilder.append(getSpaceFormater("Discount Amount",String.format("%.2f",discount),48,1)+"\n");
            }

            ArrayList<BillTaxItem> billOtherChargesItems = item.getBillOtherChargesItems();
            if(billOtherChargesItems.size()>0)
            {
                Iterator it1 = billOtherChargesItems.iterator();
                while (it1.hasNext())
                {
                    BillTaxItem billKotItem = (BillTaxItem) it1.next();
                    String TxName = getPostAddedSpaceFormat("",String.valueOf(billKotItem.getTxName()),23,1);
                    String TxPercent = getPostAddedSpaceFormat("","",15,1);
                    String TxValue = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f",billKotItem.getPrice()),10,1),8 ,1);
                    String pre = TxName + TxPercent + TxValue;
                    stringBuilder.append(pre+"\n");
                }
            }
            // Tax Slab
            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if(item.getIsInterState().equalsIgnoreCase("n")) // IntraState
            {
                if (it11.hasNext())
                {
                    stringBuilder.append("================================================"+"\n");
                    if(item.getUTGSTEnabled() ==0) // disabled
                        stringBuilder.append("Tax(%)   TaxableVal   CGSTAmt  SGSTAmt    TaxAmt"+"\n");
                    else
                        stringBuilder.append("Tax(%)   TaxableVal   CGSTAmt  UTGSTAmt   TaxAmt"+"\n");
                    stringBuilder.append("================================================"+"\n");
                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()),12,1),13,1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getCGSTAmount()),8,1),9,1);
                            String SGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getSGSTAmount()),8,1),9,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),10,1),10,1);

                            String pre = TxIndex + TaxableValue + CGSTAmt+ SGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getCGSTAmount()+billTaxSlabEntry.getSGSTAmount();
                            stringBuilder.append(pre+"\n");

                        }
                    }while (it11.hasNext());
                }
            }else // InterState
            {
                if (it11.hasNext())
                {
                    stringBuilder.append("================================================"+"\n");
                    stringBuilder.append("Tax(%)   TaxableVal   IGSTAmt             TaxAmt"+"\n");
                    stringBuilder.append("================================================"+"\n");
                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String TaxableValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getTaxableValue()),12,1),13,1);
                            String IGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getIGSTAmount()),8,1),9,1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init("",8,1),9,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),10,1),10,1);

                            String pre = TxIndex + TaxableValue + IGSTAmt+ CGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getIGSTAmount();
                            stringBuilder.append(pre+"\n");
                        }
                    }while (it11.hasNext());
                }
            }
            stringBuilder.append("\n");
            double  dtotalcessAmt =0;
            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            Iterator it21 = billcessTaxItems.iterator();
            while (it21.hasNext()) {

                BillServiceTaxItem billKotItem = (BillServiceTaxItem) it21.next();
                if (billKotItem.getServicePercent() > 0 || billKotItem.getPricePerUnit() > 0){

                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getServiceTxName()), 23, 1);
                    String TxPercent = "";
                    if (billKotItem.getServicePercent() > 0)
                        TxPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", billKotItem.getServicePercent()) + "% ", 15, 1);
                    else
                        TxPercent = getPostAddedSpaceFormat("", "@ Rs." + String.format("%.2f", billKotItem.getPricePerUnit()) + " ", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getServicePrice()), 10, 1), 8, 1);
                    dtotalcessAmt += billKotItem.getServicePrice();
                    String pre = TxName + TxPercent + TxValue;
                    stringBuilder.append(pre + "\n");
                }
            }
            double dTotalTaxAmt = dTotTaxAmt +dtotalcessAmt;
            if(dTotalTaxAmt >0)
            {
                stringBuilder.append(getSpaceFormater("Total Tax Amount",String.format("%.2f",dTotalTaxAmt),48,1)+"\n");
            }
            stringBuilder.append("================================================"+"\n");
            stringBuilder.append(getSpaceFormater("TOTAL",String.format("%.2f",item.getNetTotal()),48,1)+"\n");
            if(item.getCardPaymentValue()>0 || item.geteWalletPaymentValue()>0 ||
                    item.getCouponPaymentValue()>0 || item.getPettyCashPaymentValue()>0 || item.getRewardPoints()>0 || item.getCashPaymentValue()>0
                    || item.getAepsPaymentValue()>0 || item.getDblMSwipeVale() > 0 || item.getDblPaytmValue() > 0){
                stringBuilder.append("================================================"+"\n");
                if(item.getCardPaymentValue()>0)
                    stringBuilder.append(getSpaceFormater("OtherCard Payment",String.format("%.2f",item.getCardPaymentValue()),48,1)+"\n");
                if(item.geteWalletPaymentValue()>0)
                    stringBuilder.append(getSpaceFormater("eWallet Payment",String.format("%.2f",item.geteWalletPaymentValue()),48,1)+"\n");
                if(item.getCouponPaymentValue()>0)
                    stringBuilder.append(getSpaceFormater("Coupon Payment",String.format("%.2f",item.getCouponPaymentValue()),48,1)+"\n");
                if(item.getPettyCashPaymentValue()>0)
                    stringBuilder.append(getSpaceFormater("PettyCash Payment",String.format("%.2f",item.getPettyCashPaymentValue()),48,1)+"\n");
                if(item.getCashPaymentValue()>0)
                    stringBuilder.append(getSpaceFormater("Cash Payment",String.format("%.2f",item.getCashPaymentValue()),48,1)+"\n");
                if(item.getRewardPoints()>0)
                    stringBuilder.append(getSpaceFormater("Reward Pt Payment",String.format("%.2f",item.getRewardPoints()),48,1)+"\n");
                if(item.getAepsPaymentValue()>0)
                    stringBuilder.append(getSpaceFormater("AEPS Payment",String.format("%.2f",item.getAepsPaymentValue()),48,1)+"\n");
                if(item.getDblMSwipeVale()>0)
                    stringBuilder.append(getSpaceFormater("MSwipe Payment",String.format("%.2f",item.getDblMSwipeVale()),48,1)+"\n");
                if(item.getDblPaytmValue()>0)
                    stringBuilder.append(getSpaceFormater("Paytm Payment",String.format("%.2f",item.getDblPaytmValue()),48,1)+"\n");
            }

            if (item.getChangePaymentValue()>0) {
                stringBuilder.append("------------------------------------------------"+"\n");
                stringBuilder.append(getSpaceFormater("Due amount",String.format("%.2f",item.getChangePaymentValue()),48,1)+"\n");
            }
            stringBuilder.append("================================================"+"\n");

//        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            if(item.getRoundOff() > 0){
                stringBuilder.append(getSpaceFormater("Total Roundoff to 1.00 ","",48,1)+"\n");
                stringBuilder.append("================================================"+"\n");
            }

            tp.setTextSize(22);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_NORMAL, tp);

            stringBuilder.setLength(0);

            if(!item.getFooterLine1().equals(""))
                stringBuilder.append(item.getFooterLine1()+"\n");
            if(!item.getFooterLine2().equals(""))
                stringBuilder.append(item.getFooterLine2()+"\n");
            if(!item.getFooterLine3().equals(""))
                stringBuilder.append(item.getFooterLine3()+"\n");
            if(!item.getFooterLine4().equals(""))
                stringBuilder.append(item.getFooterLine4()+"\n");
            if(!item.getFooterLine5().equals(""))
                stringBuilder.append(item.getFooterLine5()+"\n");

            tp.setTextSize(30);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_CENTER, tp);
            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);

            mBtp.print();
        } catch (Exception e) {
            e.printStackTrace();
            messageBox("Some error occurred while printing through NGX printer.");
        }
    }

    public void printNGXKOT(PrintKotBillItem item) {
        if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
            Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String tblno = "", modename = "";
            Typeface tf = Typeface.createFromAsset(getAssets(), "Shruti.ttf");

            TextPaint tp = new TextPaint();
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(30);
            tp.setColor(Color.BLACK);
            if(item.getBillingMode().equalsIgnoreCase("1")){
                tblno = "Table # "+item.getTableNo() + " | ";
                modename = item.getStrBillingModeName();
            } else {
                tblno = "";
                modename = item.getStrBillingModeName();
            }
            mBtp.addText("\n"+modename+"\n", Layout.Alignment.ALIGN_CENTER, tp);
            mBtp.addText(tblno +"KOT # "+item.getBillNo()+"\n", Layout.Alignment.ALIGN_CENTER, tp);

            StringBuilder stringBuilder = new StringBuilder();

            tp.setTypeface(Typeface.create(tf, Typeface.NORMAL));
            stringBuilder.append("=============================================\n");
            stringBuilder.append("Attendant   : "+item.getOrderBy()+"\n");
            if ((item.getWaiterName() != null) && !(item.getWaiterName().equals("")))
                stringBuilder.append("Waiter : "+item.getWaiterName()+"\n");
            stringBuilder.append("Date        : "+item.getDate() +" | "+"Time : "+item.getTime()+"\n");
            stringBuilder.append("=============================================\n");
            stringBuilder.append("SNo        NAME                    QTY"+"\n");
            stringBuilder.append("=============================================\n");
            ArrayList<BillKotItem> billKotItems = item.getBillKotItems();
            Iterator it = billKotItems.iterator();
            while (it.hasNext())
            {
                BillKotItem billKotItem = (BillKotItem) it.next();
                int id = billKotItem.getItemId();
                String name = getFormatedCharacterForPrint(billKotItem.getItemName(),16,1);
                String qty = billKotItem.getQty()+"";
                String pre = getPostAddedSpaceFormat("",String.valueOf(id),10,1)+name;
                stringBuilder.append(getPreAddedSpaceFormat(pre,qty,38,1)+"\n");
            }
            stringBuilder.append("=============================================\n\n\n\n");
            tp.setTextSize(22);
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_NORMAL, tp);
            mBtp.print();
        } catch (Exception e) {
            e.printStackTrace();
            messageBox("Some error occurred while printing through NGX printer.");
        }
    }

    public void printNGXSalesReturn(SalesReturnPrintBean item, String type) {
        if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
            Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            Typeface tf = Typeface.createFromAsset(getAssets(), "Shruti.ttf");

            TextPaint tp = new TextPaint();
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(30);
            tp.setColor(Color.BLACK);
//            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);
            mBtp.addText(item.getIsDuplicate(), Layout.Alignment.ALIGN_CENTER, tp);

            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("GSTIN     : "+item.getStrOwnerGSTIN()+"\n");
            stringBuilder.append("Name      : "+item.getStrOwnerName()+"\n");
            stringBuilder.append("Address   : "+item.getStrOwnerAddress()+"\n");
            stringBuilder.append("\n");
            stringBuilder.append("Bill No.  : "+item.getStrInvoiceNo()+"\n");
            stringBuilder.append("Bill Date : "+item.getStrInvoiceDate()+"\n");
            stringBuilder.append("\n");

            stringBuilder.append("CDNI      : "+item.getiSrId()+"\n");
            stringBuilder.append("Note Date : "+item.getStrSalesReturnDate() +"\n");

            if (!item.getStrCustName().equals("") && !item.getStrCustName().contains("-")) {
                stringBuilder.append("Customer  : "+item.getStrCustName()+"\n");
            }

            stringBuilder.append("Notes     : "+item.getStrReason()+"\n");

            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            stringBuilder.append("================================================"+"\n");
            stringBuilder.append("ITEM NAME              QTY               AMOUNT" + "\n");
            stringBuilder.append("================================================"+"\n");
            ArrayList<SalesReturnBean> salesReturnItems = item.getArrayList();
            Iterator it = salesReturnItems.iterator();
            int totalitemtypes =0;
            double totalquantitycount =0;
            double subtotal =0;

            while (it.hasNext())
            {

                SalesReturnBean bean = (SalesReturnBean) it.next();

                String preName = getPostAddedSpaceFormat("",getFormatedCharacterForPrint(String.valueOf(bean.getStrItemName()),bean.getStrItemName().length(),1),17,1);
                String preQty = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.valueOf(bean.getDblReturnQuantity()),9,1),10,1);
                String preAmount = getPostAddedSpaceFormat("",getFormatedCharacterForPrint_init(String.format("%.2f", bean.getDblReturnAmount()),20,1),10,1);
                String pre = "";
                pre = preName+ preQty + preAmount;
                stringBuilder.append(pre+"\n");

                totalitemtypes++;
            }

            stringBuilder.append("------------------------------------------------"+"\n");
            stringBuilder.append(getSpaceFormater("Total Item(s) : "+totalitemtypes,"",48,1));

            // Tax Slab
            double dTotTaxAmt = 0;
            ArrayList<BillTaxSlab> billTaxSlab = item.getBillTaxSlabs();
            Iterator it11 = billTaxSlab.iterator();
            if(item.getIsInterstate() == 0) // IntraState
            {
                if (it11.hasNext())
                {
                    stringBuilder.append("================================================"+"\n");
                    stringBuilder.append("Tax(%)        CGSTAmt     SGSTAmt         TaxAmt"+"\n");
                    stringBuilder.append("================================================"+"\n");
                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String CGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getCGSTAmount()),13,1),9,1);
                            String SGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getSGSTAmount()),13,1),9,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),15,1),10,1);

                            String pre = TxIndex + CGSTAmt+ SGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getCGSTAmount()+billTaxSlabEntry.getSGSTAmount();
                            stringBuilder.append(pre+"\n");

                        }
                    }while (it11.hasNext());
                }
            }else // InterState
            {
                if (it11.hasNext())
                {
                    stringBuilder.append("================================================"+"\n");
                    stringBuilder.append("Tax(%)               IGSTAmt              TaxAmt"+"\n");
                    stringBuilder.append("================================================"+"\n");
                    do
                    {
                        BillTaxSlab billTaxSlabEntry = (BillTaxSlab) it11.next();
                        if(billTaxSlabEntry.getTaxRate()> 0)
                        {
                            String TxIndex = getPostAddedSpaceFormat("",String.valueOf(billTaxSlabEntry.getTaxIndex())+" "+
                                    String.format("%.2f",billTaxSlabEntry.getTaxRate()),7,1);
                            String IGSTAmt = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billTaxSlabEntry.getIGSTAmount()),20,1),9,1);
                            String TotalTax = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f",billTaxSlabEntry.getTotalTaxAmount()),20,1),10,1);

                            String pre = TxIndex + IGSTAmt + TotalTax;
                            dTotTaxAmt += billTaxSlabEntry.getIGSTAmount();
                            stringBuilder.append(pre+"\n");

                        }
                    }while (it11.hasNext());
                }
            }
            stringBuilder.append("\n");
            ArrayList<BillServiceTaxItem> billcessTaxItems = item.getBillcessTaxItems();
            double  dtotalcessAmt =0;
            Iterator it21 = billcessTaxItems.iterator();
            while (it21.hasNext()) {

                BillServiceTaxItem billKotItem = (BillServiceTaxItem) it21.next();
                if (billKotItem.getServicePercent() > 0 || billKotItem.getPricePerUnit() > 0){

                    String TxName = getPostAddedSpaceFormat("", String.valueOf(billKotItem.getServiceTxName()), 23, 1);
                    String TxPercent = "";
                    if (billKotItem.getServicePercent() > 0)
                        TxPercent = getPostAddedSpaceFormat("", "@ " + String.format("%.2f", billKotItem.getServicePercent()) + "% ", 15, 1);
                    else
                        TxPercent = getPostAddedSpaceFormat("", "@ Rs." + String.format("%.2f", billKotItem.getPricePerUnit()) + " ", 15, 1);
                    String TxValue = getPostAddedSpaceFormat("", getFormatedCharacterForPrint_init(String.format("%.2f", billKotItem.getServicePrice()), 10, 1), 8, 1);
                    dtotalcessAmt += billKotItem.getServicePrice();
                    String pre = TxName + TxPercent + TxValue;
                    stringBuilder.append(pre + "\n");
                }
            }

            stringBuilder.append("------------------------------------------------"+"\n");

            if(dTotTaxAmt >0)
            {
                stringBuilder.append(getSpaceFormater("Total Tax Amount",String.format("%.2f",dTotTaxAmt+dtotalcessAmt),48,1)+"\n");
            }
            stringBuilder.append(getSpaceFormater("Total Invoice Amount",String.format("%.2f",item.getDblAmount()),48,1)+"\n");
            stringBuilder.append(getSpaceFormater("Total Return Amount",String.format("%.2f",item.getDblReturnAmount()),48,1)+"\n");
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_CENTER, tp);
            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);

            mBtp.print();
        } catch (Exception e) {
            e.printStackTrace();
            messageBox("Some error occurred while printing through NGX printer.");
        }
    }

    public void printNGXDepositAmountReceipt (Customer item, String type) {

        if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
            Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            Typeface tf = Typeface.createFromAsset(getAssets(), "Shruti.ttf");

            TextPaint tp = new TextPaint();
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(30);
            tp.setColor(Color.BLACK);

            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);
            mBtp.addText("Deposit Amount Receipt"+"\n", Layout.Alignment.ALIGN_CENTER, tp);

            if (item.getHeaderPrintBold() == 1) {
                tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            } else {
                tp.setTypeface(Typeface.create(tf, Typeface.NORMAL));
            }

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                mBtp.addText(item.getHeaderLine1(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                mBtp.addText(item.getHeaderLine2(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                mBtp.addText(item.getHeaderLine3(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                mBtp.addText(item.getHeaderLine4(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                mBtp.addText(item.getHeaderLine5()+"\n", Layout.Alignment.ALIGN_CENTER, tp);
            }

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("================================================"+"\n");
            stringBuilder.append("Deposit Date    : "+item.getBusinessDate()+"\n");
            stringBuilder.append("Customer Name   : "+item.getStrCustName()+"\n");
            stringBuilder.append("Mobile No.      : "+item.getStrCustContactNumber()+"\n");
            stringBuilder.append("Deposit Amount  : "+item.getDblDepositAmt()+"\n");
            stringBuilder.append("Credit Amount   : "+item.getdCreditAmount()+"\n");
            stringBuilder.append("================================================"+"\n");
            tp.setTextSize(22);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_NORMAL, tp);

            stringBuilder.setLength(0);

            if(!item.getFooterLine1().equals(""))
                stringBuilder.append(item.getFooterLine1()+"\n");
            if(!item.getFooterLine2().equals(""))
                stringBuilder.append(item.getFooterLine2()+"\n");
            if(!item.getFooterLine3().equals(""))
                stringBuilder.append(item.getFooterLine3()+"\n");
            if(!item.getFooterLine4().equals(""))
                stringBuilder.append(item.getFooterLine4()+"\n");
            if(!item.getFooterLine5().equals(""))
                stringBuilder.append(item.getFooterLine5()+"\n");

            tp.setTextSize(30);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_CENTER, tp);
            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);

            mBtp.print();


        } catch (Exception e) {
            e.printStackTrace();
            messageBox("Some error occurred while printing through NGX printer.");
        }
    }

    public void printNGXPaymentReceipt(PaymentReceipt item, String type) {
        if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
            Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            Typeface tf = Typeface.createFromAsset(getAssets(), "Shruti.ttf");

            TextPaint tp = new TextPaint();
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(30);
            tp.setColor(Color.BLACK);

            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);

            if (item.getiBillType() == 1) {
                if(item.getIsDuplicate().equals(""))
                    mBtp.addText("PAYMENT"+"\n", Layout.Alignment.ALIGN_CENTER, tp);
                else
                    mBtp.addText("PAYMENT"+"\n"+item.getIsDuplicate()+"\n", Layout.Alignment.ALIGN_CENTER, tp);
            } else {
                if(item.getIsDuplicate().equals(""))
                    mBtp.addText("RECEIPT"+"\n", Layout.Alignment.ALIGN_CENTER, tp);
                else
                    mBtp.addText("RECEIPT"+"\n"+item.getIsDuplicate()+"\n", Layout.Alignment.ALIGN_CENTER, tp);
            }

            if (item.getHeaderPrintBold() == 1) {
                tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            } else {
                tp.setTypeface(Typeface.create(tf, Typeface.NORMAL));
            }

            if(item.getHeaderLine1()!=null && !item.getHeaderLine1().equals("")) {
                mBtp.addText(item.getHeaderLine1(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine2()!=null && !item.getHeaderLine2().equals("")) {
                mBtp.addText(item.getHeaderLine2(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine3()!=null && !item.getHeaderLine3().equals("")) {
                mBtp.addText(item.getHeaderLine3(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine4()!=null && !item.getHeaderLine4().equals("")) {
                mBtp.addText(item.getHeaderLine4(), Layout.Alignment.ALIGN_CENTER, tp);
            }
            if(item.getHeaderLine5()!=null && !item.getHeaderLine5().equals("")) {
                mBtp.addText(item.getHeaderLine5()+"\n", Layout.Alignment.ALIGN_CENTER, tp);
            }

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("================================================"+"\n");
            if (item.getiBillType() == 1) {
                stringBuilder.append("Payment No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n");
            }
            else {
                stringBuilder.append("Receipt No.     : " + String.valueOf(item.getPaymentReceiptNo()) + "\n");
            }
            stringBuilder.append("Invoice Date    : "+item.getStrDate()+"\n");
            stringBuilder.append("Description     : "+item.getDescriptionText()+"\n");
            stringBuilder.append("Amount          : "+String.valueOf(item.getdAmount())+"\n");
            stringBuilder.append("Reason          : "+item.getStrReason()+"\n");
            stringBuilder.append("================================================"+"\n");

            tp.setTextSize(22);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_NORMAL, tp);

            mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);

            mBtp.print();

        } catch (Exception e) {
            e.printStackTrace();
            messageBox("Some error occurred while printing through NGX printer.");
        }
    }

    public void printNGXReport(List<List<String>> Report, String ReportName, String type) {

        if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
            Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            itemReport = Report;

            if (ReportName.contains("Cumulative")) {

                Typeface tf = Typeface.createFromAsset(getAssets(), "Shruti.ttf");

                TextPaint tp = new TextPaint();
                tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
                tp.setTextSize(30);
                tp.setColor(Color.BLACK);

                mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);
                mBtp.addText(ReportName+"\n", Layout.Alignment.ALIGN_CENTER, tp);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("==============================================="+"\n");
                Calendar time = Calendar.getInstance();
                stringBuilder.append("["+String.format("%tR", time)+"]\n");

                int j;
                for(j=0;j<itemReport.size();j++)
                {
                    if(j == itemReport.size() - 1){
                        stringBuilder.append("==============================================="+"\n");
                    }
                    List<String> arrayListColumn = itemReport.get(j);
                    StringBuffer sb = new StringBuffer();
                    for (int i=0;i<arrayListColumn.size();i++)
                    {
                        String str = arrayListColumn.get(i);
                        String preTxt = getAbsoluteCharacter(String.valueOf(str),10,1);
                        if(j==0 || j==1)
                            sb.append(preTxt+" |");
                        else
                            sb.append(" "+preTxt+" ");
                        int rem = i%3;
                        if(rem == 0 && i!=0)
                        {
                            stringBuilder.append(sb.toString()+"\n");
                            sb = new StringBuffer();
                        }

                        if(rem != 0 && (arrayListColumn.size()-1)==i)
                        {
                            stringBuilder.append(getAbsoluteCharacter1(sb.toString(),1)+"\n");
                            sb = new StringBuffer();
                        }
                    }
                    if(j==1)
                        stringBuilder.append("==============================================="+"\n");
                }
                stringBuilder.append("==============================================="+"\n");

                tp.setTextSize(22);
                tp.setTypeface(tf);
                mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_NORMAL, tp);

                mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);

                mBtp.print();
            }
            else {
                Typeface tf = Typeface.createFromAsset(getAssets(), "Shruti.ttf");

                TextPaint tp = new TextPaint();
                tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
                tp.setTextSize(30);
                tp.setColor(Color.BLACK);

                mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);
                mBtp.addText(ReportName+"\n", Layout.Alignment.ALIGN_CENTER, tp);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("==============================================="+"\n");
                Calendar time = Calendar.getInstance();
                stringBuilder.append("["+String.format("%tR", time)+"]\n");

                for(int j=0;j<itemReport.size();j++)
                {
                    List<String> arrayListColumn = itemReport.get(j);
                    StringBuffer sb = new StringBuffer();
                    for (int i=0;i<arrayListColumn.size();i++)
                    {
                        String str = arrayListColumn.get(i);
                        String preTxt = getAbsoluteCharacter(String.valueOf(str),10,1);
                        if(j==0)
                            sb.append(preTxt+" |");
                        else
                            sb.append(" "+preTxt+" ");
                        int rem = i%3;
                        if(rem == 0 && i!=0)
                        {
                            stringBuilder.append(sb.toString()+"\n");
                            sb = new StringBuffer();
                        }

                        if(rem != 0 && (arrayListColumn.size()-1)==i)
                        {
                            stringBuilder.append(getAbsoluteCharacter1(sb.toString(),1)+"\n");
                            sb = new StringBuffer();
                        }
                /*else if(i == arrayListColumn.size())
                {
                    tmpList.add(CreateBitmap.AddText(18, "Bold","MONOSPACE", sb.toString(), TextAlign.LEFT,getApplicationContext()));
                    sb = new StringBuffer();
                }*/
                    }
                    if(j==0)
                        stringBuilder.append("==============================================="+"\n");
                }
                stringBuilder.append("==============================================="+"\n");

                tp.setTextSize(22);
                tp.setTypeface(tf);
                mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_NORMAL, tp);

                mBtp.addText("\n", Layout.Alignment.ALIGN_CENTER, tp);

                mBtp.print();
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageBox("Some error occurred while printing through NGX printer.");
        }
    }
}