package com.wepindia.printers.heydey;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wepindia.printers.R;
import com.wepindia.printers.wep.OnDeviceClickListener;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by SachinV on 22-03-2018.
 */

public class DiscoverBluetoothPrinter extends DialogFragment {

    private Context mContext = null;
//    private MessageDialog MsgBox ;

    private static final String DEBUG_TAG = "DeviceListActivity";
    public static LinearLayout deviceNamelinearLayout;
    // Member fields
    private ListView lvPairedDevice = null, lvNewDevice = null;
    private TextView tvPairedDevice = null, tvNewDevice = null;
    private Button btDeviceScan = null;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private OnDeviceClickListener onDeviceClickListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(DEBUG_TAG, "On Create");
    }

    public void mInitListener(OnDeviceClickListener onDeviceClickListener){
        this.onDeviceClickListener = onDeviceClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.dialog_bluetooth_list, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mContext = getActivity();
//        MsgBox = new MessageDialog(mContext);

        tvPairedDevice = (TextView)fragmentView.findViewById(com.wepindia.printers.R.id.tvPairedDevices);
        //ListView �����?
        lvPairedDevice = (ListView)fragmentView.findViewById(com.wepindia.printers.R.id.lvPairedDevices);
        // TextView �µ�
        tvNewDevice = (TextView)fragmentView.findViewById(com.wepindia.printers.R.id.tvNewDevices);
        // ListView �µ�
        lvNewDevice = (ListView)fragmentView.findViewById(com.wepindia.printers.R.id.lvNewDevices);
        // Button ɨ���豸
        btDeviceScan = (Button)fragmentView.findViewById(com.wepindia.printers.R.id.btBluetoothScan);
        btDeviceScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                v.setVisibility(View.GONE);
                discoveryDevice();
            }
        });

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDeviceList();
    }

    protected void getDeviceList() {
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(mContext,
                com.wepindia.printers.R.layout.bluetooth_device_name_item);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(mContext,
                com.wepindia.printers.R.layout.bluetooth_device_name_item);
        lvPairedDevice.setAdapter(mPairedDevicesArrayAdapter);
        lvPairedDevice.setOnItemClickListener(mDeviceClickListener);
        lvNewDevice.setAdapter(mNewDevicesArrayAdapter);
        lvNewDevice.setOnItemClickListener(mDeviceClickListener);
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mFindBlueToothReceiver, filter);
        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mFindBlueToothReceiver, filter);
        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            tvPairedDevice.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n"
                        + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(com.wepindia.printers.R.string.none_paired)
                    .toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }


    // changes the title when discovery is finished
    private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed
                // already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
//                setProgressBarIndeterminateVisibility(false);
//                setTitle(com.wepindia.printers.R.string.select_bluetooth_device);
                Log.i("tag", "finish discovery" +mNewDevicesArrayAdapter.getCount());
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(
                            com.wepindia.printers.R.string.none_bluetooth_device_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
    private void discoveryDevice() {
        // Indicate scanning in the title
//        setProgressBarIndeterminateVisibility(true);
//        setTitle(com.wepindia.printers.R.string.scaning);
        // Turn on sub-title for new devices
        tvNewDevice.setVisibility(View.VISIBLE);

        lvNewDevice.setVisibility(View.VISIBLE);
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBluetoothAdapter.cancelDiscovery();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String noDevices = getResources().getText(com.wepindia.printers.R.string.none_paired).toString();
            String noNewDevice = getResources().getText(com.wepindia.printers.R.string.none_bluetooth_device_found).toString();
            Log.i("tag", info);
            if (! info.equals(noDevices) && ! info.equals(noNewDevice)) {
                String address = info.substring(info.length() - 17);
                // Create the result Intent and include the MAC address

                HashMap<String, String> item  = new HashMap<>();
                item.put("PrinterName", "Heyday");
                item.put("Heyday", address);
                onDeviceClickListener.onDeviceClicked(getString(R.string.title_target), item);
                dismiss();

//                Intent intent = new Intent();
//                intent.putExtra(PortConfigurationActivity.EXTRA_DEVICE_ADDRESS, address);
//                // Set result and finish this Activity
//                setResult(Activity.RESULT_OK, intent);
//                finish();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        if(mFindBlueToothReceiver != null)
            mContext.unregisterReceiver(mFindBlueToothReceiver);
    }
}
