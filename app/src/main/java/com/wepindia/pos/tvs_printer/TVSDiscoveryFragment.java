package com.wepindia.pos.tvs_printer;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
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

import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.printers.wep.OnDeviceClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohan on 8/10/2018.
 */
public class TVSDiscoveryFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private Context mContext = null;
    private MessageDialog MsgBox ;
    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private SimpleAdapter mPrinterListAdapter = null;
    private OnDeviceClickListener onDeviceClickListener = null;

    @BindView(R.id.btnRestart)
    Button btnResart;
    @BindView(R.id.lstReceiveData)
    ListView lstReceiveData;

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

        UsbManager manager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            HashMap<String, String> item = new HashMap<String, String>();
            if(device.getProductId() == Constants.PRODUCT_ID_TVS_POS_PRINTER
                    && device.getVendorId() == Constants.VENDOR_ID_TVS_POS_PRINTER) {
                item.put("PrinterName", Constants.USB_TVS_PRINTER_NAME);
                item.put("Target", device.getDeviceName());
                mPrinterList.add(item);
            }
        }
        if(mPrinterList != null && mPrinterList.size() > 0) {
            mPrinterListAdapter = new SimpleAdapter(mContext, mPrinterList, R.layout.list_at,
                    new String[]{"PrinterName", "Target"},
                    new int[]{R.id.PrinterName, R.id.Target});

            lstReceiveData.setAdapter(mPrinterListAdapter);
            lstReceiveData.setOnItemClickListener(this);
        } else {
            Toast.makeText(mContext,"Please check TVS printer is connected to device or not?",Toast.LENGTH_LONG).show();
        }

    }

    public void mInitListener(OnDeviceClickListener onDeviceClickListener){
        this.onDeviceClickListener = onDeviceClickListener;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> item  = mPrinterList.get(position);
        onDeviceClickListener.onDeviceClicked(getString(R.string.title_target), item);
        dismiss();
    }
}
