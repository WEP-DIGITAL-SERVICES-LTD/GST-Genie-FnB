package com.wepindia.pos.bixolon;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.printers.BixolonPrinterBaseAcivity;
import com.wepindia.printers.bixolon.DialogManager;
import com.wepindia.printers.wep.OnDeviceClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
public class BixolonDiscoveryFragment extends DialogFragment implements AdapterView.OnItemClickListener {


    @BindView(R.id.btnRestart)
    Button btnResart;
    @BindView(R.id.lstReceiveData)
    ListView lstReceiveData;

    private Context mContext = null;
    private MessageDialog MsgBox ;
    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private SimpleAdapter mPrinterListAdapter = null;
    private OnDeviceClickListener onDeviceClickListener = null;
    private BixolonPrinter mBixolonPrinter = null;
    private BixolonPrinterBaseAcivity bixolon = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_discovery, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this,fragmentView);
        mContext = getActivity();
        MsgBox = new MessageDialog(mContext);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnResart.setVisibility(View.GONE);

        mPrinterList = new ArrayList<HashMap<String, String>>();
        mPrinterListAdapter = new SimpleAdapter(mContext, mPrinterList, R.layout.list_at,
                new String[] { "PrinterName", "Target" },
                new int[] { R.id.PrinterName, R.id.Target });

        lstReceiveData.setAdapter(mPrinterListAdapter);
        lstReceiveData.setOnItemClickListener(this);

        bixolon = new BixolonPrinterBaseAcivity();

        if (mBixolonPrinter == null)
            mBixolonPrinter = bixolon.getmBixolonPrinter(mContext, mHandler);
        mBixolonPrinter.findUsbPrinters();
    }

    public void mInitListener(OnDeviceClickListener onDeviceClickListener){
        this.onDeviceClickListener = onDeviceClickListener;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        HashMap<String, String> item  = mPrinterList.get(position);
        onDeviceClickListener.onDeviceClicked(getString(R.string.title_target), item);
        dismiss();
    }

    void populateData(final Set<UsbDevice> usbDevices){
        for (UsbDevice device : usbDevices) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("PrinterName", "Bixolon");
            item.put("Target", device.getSerialNumber());
            mPrinterList.add(item);

//            items[index++] = "Device name: " + device.getDeviceName() + ", Product ID: " + device.getProductId() + ", Device ID: " + device.getDeviceId() + ", Vendor ID: " + device.getVendorId();
        }
        mPrinterListAdapter.notifyDataSetChanged();
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    if (msg.obj == null) {
                        Toast.makeText(mContext, "No paired device", Toast.LENGTH_SHORT).show();
                    } else {
                        DialogManager.showBluetoothDialog(mContext, (Set<BluetoothDevice>) msg.obj);
                    }
                    return true;
                case BixolonPrinter.MESSAGE_USB_DEVICE_SET:
                    if (msg.obj == null) {
                        Toast.makeText(mContext, "No connected device", Toast.LENGTH_SHORT).show();
                    } else {

                        populateData((Set<UsbDevice>) msg.obj);
//                        if (bixolonPrinterStatus != null)
//                            bixolonPrinterStatus.onBixolonPrinterFound((Set<UsbDevice>) msg.obj);
//                        DialogManager.showUsbDialog(context, (Set<UsbDevice>) msg.obj, mUsbReceiver);
                    }
                    return true;

                case BixolonPrinter.MESSAGE_USB_SERIAL_SET:
                    if (msg.obj == null) {
                        Toast.makeText(mContext, "No connected device", Toast.LENGTH_SHORT).show();
                    } else {
                        final HashMap<String, UsbDevice> usbDeviceMap = (HashMap<String, UsbDevice>) msg.obj;
                        final String[] items = usbDeviceMap.keySet().toArray(new String[usbDeviceMap.size()]);
                        new AlertDialog.Builder(mContext).setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBixolonPrinter.connect(usbDeviceMap.get(items[which]));
                            }
                        }).show();
                    }
                    return true;

                case BixolonPrinter.MESSAGE_NETWORK_DEVICE_SET:
                    if (msg.obj == null) {
                        Toast.makeText(mContext, "No connectible device", Toast.LENGTH_SHORT).show();
                    }
//                    DialogManager.showNetworkDialog(context, (Set<String>) msg.obj);
                    return true;
            }
            return false;
        }
    });
}
