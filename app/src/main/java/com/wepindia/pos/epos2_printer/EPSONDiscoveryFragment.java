package com.wepindia.pos.epos2_printer;

import android.content.Context;
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

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.printers.wep.OnDeviceClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SachinV on 20-03-2018.
 */

public class EPSONDiscoveryFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.btnRestart)          Button btnResart;
    @BindView(R.id.lstReceiveData)      ListView lstReceiveData;


    private Context mContext = null;
    private MessageDialog MsgBox ;
    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private SimpleAdapter mPrinterListAdapter = null;
    private FilterOption mFilterOption = null;
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
        ButterKnife.bind(this,fragmentView);
        mContext = getActivity();
        MsgBox = new MessageDialog(mContext);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPrinterList = new ArrayList<HashMap<String, String>>();
        mPrinterListAdapter = new SimpleAdapter(mContext, mPrinterList, R.layout.list_at,
                new String[] { "PrinterName", "Target" },
                new int[] { R.id.PrinterName, R.id.Target });

        lstReceiveData.setAdapter(mPrinterListAdapter);
        lstReceiveData.setOnItemClickListener(this);

        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        try {
            Discovery.start(mContext, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            MsgBox.Show("Warning", ""+e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }

        mFilterOption = null;
    }

    @OnClick({R.id.btnRestart})
    protected void mBtnClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnRestart : restartDiscovery();
                break;
        }
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

    private void restartDiscovery() {
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    MsgBox.Show("Warning", ""+e.getMessage());
                    return;
                }
            }
        }

        mPrinterList.clear();
        mPrinterListAdapter.notifyDataSetChanged();

        try {
            Discovery.start(mContext, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            MsgBox.Show("Warning", ""+e.getMessage());
        }
    }

    private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("PrinterName", deviceInfo.getDeviceName());
                    item.put("Target", deviceInfo.getTarget());
                    mPrinterList.add(item);
                    mPrinterListAdapter.notifyDataSetChanged();
                }
            });
        }
    };
}
