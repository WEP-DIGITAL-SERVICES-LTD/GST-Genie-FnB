package wep_th_printer;

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

import com.wepindia.pos.R;
import com.wepindia.printers.wep.OnDeviceClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class WePDiscoveryFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    ListView lstReceiveData;

    private Context mContext = null;
    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private SimpleAdapter mPrinterListAdapter = null;
    private OnDeviceClickListener onDeviceClickListener = null;

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
        mContext = getActivity();
        lstReceiveData = (ListView) fragmentView.findViewById(R.id.lstReceiveData);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPrinterList = new ArrayList<HashMap<String, String>>();

        UsbManager manager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("PrinterName", device.getProductName());
            item.put("Target", device.getDeviceName());
            mPrinterList.add(item);
        }
        mPrinterListAdapter = new SimpleAdapter(mContext, mPrinterList, R.layout.list_at,
                new String[] { "PrinterName", "Target" },
                new int[] { R.id.PrinterName, R.id.Target });

        lstReceiveData.setAdapter(mPrinterListAdapter);
        lstReceiveData.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        HashMap<String, String> item  = mPrinterList.get(i);
        onDeviceClickListener.onDeviceClicked(getString(R.string.title_target), item);
        dismiss();
    }

    public void mInitListener(OnDeviceClickListener onDeviceClickListener){
        this.onDeviceClickListener = onDeviceClickListener;
    }
}
