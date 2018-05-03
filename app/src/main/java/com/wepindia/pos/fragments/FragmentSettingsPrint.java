package com.wepindia.pos.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.wep.common.app.utils.Preferences;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.TabbedSettingsActivity;
import com.wepindia.pos.fragments.Adapter.PrinterChoiceAdapter;
import com.wepindia.pos.fragments.Model.PrinterChoice;
import com.wepindia.printers.heydey.PrinterFragment;
import com.wepindia.printers.sohamsa.PrinterSohamsaActivity;
import com.wepindia.printers.wep.OnDeviceClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import wep_th_printer.WePDiscoveryFragment;

public class FragmentSettingsPrint extends Fragment implements OnDeviceClickListener {

    Context myContext;
    ArrayList<HashMap<String, String>> printersList;
    private ArrayList<String> stringPrinter;
    private HashMap<String, String> printer;
    MessageDialog messageBox;
    private Spinner spinnerKot,spinnerBill,spinnerReport,spinnerReceipt;
    private SharedPreferences sharedPreferences;
    ArrayAdapter<String> dataAdapter;
    boolean statusKotTest = false,statusBillTest = false,statusReportTest = false;
    private SharedPreferences.Editor editor;
    public static final int PRINTING_REQUEST_CODE_SOHAMSA = 200;
    public static final int PRINTING_REQUEST_CODE_HEYDAY = 201;
    private Button saveKotPrint,resetKotPrint,saveBillPrint,resetBillPrint,
            saveReportPrint,resetReportPrint,saveReceiptPrint,resetReceiptPrint,btnClosePrinter, btnDiscoverPrinter;


    public FragmentSettingsPrint() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_settings_print, container, false);
        myContext = getActivity();
        messageBox = new MessageDialog(myContext);
        sharedPreferences = Preferences.getSharedPreferencesForPrint(getActivity()); // getSharedPreferences("PrinterConfigurationActivity", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        printersList = new ArrayList();
        stringPrinter = new ArrayList<>();

        spinnerKot = (Spinner) view.findViewById(R.id.spnr1);
        spinnerBill = (Spinner) view.findViewById(R.id.spnr2);
        spinnerReport = (Spinner) view.findViewById(R.id.spnr3);
        spinnerReceipt = (Spinner) view.findViewById(R.id.spnr4);

        addDataToPrinterList();

        //initSinglePrinter();

        saveKotPrint = (Button) view.findViewById(R.id.saveKotPrint);
        saveKotPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveKotPrint();
            }
        });
        resetKotPrint = (Button) view.findViewById(R.id.resetKotPrint);
        resetKotPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetKotPrint();
            }
        });

        saveBillPrint = (Button) view.findViewById(R.id.saveBillPrint);
        saveBillPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBillPrint();
            }
        });
        resetBillPrint = (Button) view.findViewById(R.id.resetBillPrint);
        resetBillPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBillPrint();
            }
        });

        saveReportPrint = (Button) view.findViewById(R.id.saveReportPrint);
        saveReportPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReportPrint();
            }
        });
        resetReportPrint = (Button) view.findViewById(R.id.resetReportPrint);
        resetReportPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetReportPrint();
            }
        });

        saveReceiptPrint = (Button) view.findViewById(R.id.saveReceiptPrint);
        saveReceiptPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReceiptPrint();
            }
        });
        resetReceiptPrint = (Button) view.findViewById(R.id.resetReceiptPrint);
        resetReceiptPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetReceiptPrint();
            }
        });

        btnClosePrinter = (Button) view.findViewById(R.id.btnClosePrinter);
        btnClosePrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Close();
            }
        });
        btnDiscoverPrinter = (Button) view.findViewById(R.id.btnDiscoverPrinter);
        btnDiscoverPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOptionDialog();
            }
        });
        return view;
    }

    void addDataToPrinterList(){
        printersList.clear();
        stringPrinter.clear();

        printer = new HashMap<>();
        printer.put("PrinterName", "--Select--");
        printer.put("--Select--", "Target");
        printersList.add(printer);

        stringPrinter.add(0, "--Select--");

        // Bill printer

        printer = new HashMap<>();
        if (!sharedPreferences.getString("bill","--Select--").equals("--Select--")) {
            printer.put("PrinterName", sharedPreferences.getString("bill","--Select--"));
            printer.put(sharedPreferences.getString("bill","--Select--"),
                    sharedPreferences.getString(sharedPreferences.getString("bill","--Select--"),"--Select--"));
            printersList.add(printer);
        }

        if (!sharedPreferences.getString("bill","--Select--").equals("--Select--"))
            stringPrinter.add(sharedPreferences.getString("bill","--Select--"));

        // KOT Printer

        printer = new HashMap<>();
        if (!sharedPreferences.getString("kot","--Select--").equals("--Select--")) {
            printer.put("PrinterName", sharedPreferences.getString("kot","--Select--"));
            printer.put(sharedPreferences.getString("kot","--Select--"),
                    sharedPreferences.getString(sharedPreferences.getString("bill","--Select--"),"--Select--"));
            printersList.add(printer);
        }

        if (!sharedPreferences.getString("kot","--Select--").equals("--Select--"))
            stringPrinter.add(sharedPreferences.getString("kot","--Select--"));

        // Report Printer

        printer = new HashMap<>();
        if (!sharedPreferences.getString("report","--Select--").equals("--Select--")) {
            printer.put("PrinterName", sharedPreferences.getString("bill","--Select--"));
            printer.put(sharedPreferences.getString("report","--Select--"),
                    sharedPreferences.getString(sharedPreferences.getString("report","--Select--"),"--Select--"));
            printersList.add(printer);
        }

        if (!sharedPreferences.getString("bilreportl","--Select--").equals("--Select--"))
            stringPrinter.add(sharedPreferences.getString("report","--Select--"));

        // Receipt Printer

        printer = new HashMap<>();
        if (!sharedPreferences.getString("receipt","--Select--").equals("--Select--")) {
            printer.put("PrinterName", sharedPreferences.getString("receipt","--Select--"));
            printer.put(sharedPreferences.getString("receipt","--Select--"),
                    sharedPreferences.getString(sharedPreferences.getString("bill","--Select--"),"--Select--"));
            printersList.add(printer);
        }

        if (!sharedPreferences.getString("receipt","--Select--").equals("--Select--"))
            stringPrinter.add(sharedPreferences.getString("receipt","--Select--"));

        dataAdapter = new ArrayAdapter<String>(myContext,android.R.layout.simple_spinner_item, stringPrinter);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerKot.setAdapter(dataAdapter);
        spinnerKot.setSelection(stringPrinter.indexOf(sharedPreferences.getString("kot","--Select--")));
        spinnerBill.setAdapter(dataAdapter);
        spinnerBill.setSelection(stringPrinter.indexOf(sharedPreferences.getString("bill","--Select--")));
        spinnerReport.setAdapter(dataAdapter);
        spinnerReport.setSelection(stringPrinter.indexOf(sharedPreferences.getString("report","--Select--")));
        spinnerReceipt.setAdapter(dataAdapter);
        spinnerReceipt.setSelection(stringPrinter.indexOf(sharedPreferences.getString("receipt","--Select--")));

    }

    void createOptionDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(myContext);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Choose printer connectivity");

        ArrayList<PrinterChoice> printerChoices = new ArrayList<>();

        printerChoices.add(new PrinterChoice(R.drawable.bluetooth, "Bluetooth Printer"));
        printerChoices.add(new PrinterChoice(R.drawable.usb, "USB Printer"));

        final PrinterChoiceAdapter arrayAdapter = new PrinterChoiceAdapter(myContext, R.layout.printer_choice_adapter, printerChoices);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        ((TabbedSettingsActivity)myContext).askForConfig(FragmentSettingsPrint.this);
                        break;
                    case 1:
                        createOptionDialogForUsbPrinter();
                        break;
                }

                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    void createOptionDialogForUsbPrinter(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(myContext);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Choose printer connectivity");

        ArrayList<PrinterChoice> printerChoices = new ArrayList<>();

//        printerChoices.add(new PrinterChoice(0, "EPSON"));
//        printerChoices.add(new PrinterChoice(0, "BIXOLON"));
        printerChoices.add(new PrinterChoice(0, "WeP"));

        final PrinterChoiceAdapter arrayAdapter = new PrinterChoiceAdapter(myContext, R.layout.printer_choice_adapter, printerChoices);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                       /* EPSONDiscoveryFragment myEpsonFragment =
                                (EPSONDiscoveryFragment)getFragmentManager().findFragmentByTag("Discover new devices");
                        if (myEpsonFragment != null && myEpsonFragment.isVisible()) {
                            return;
                        }

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        EPSONDiscoveryFragment EpsonDiscoveryFragment = new EPSONDiscoveryFragment();
                        EpsonDiscoveryFragment.mInitListener(PrinterSettingFragment.this);
                        EpsonDiscoveryFragment.show(fm, "Discover new devices");
                        break;
                    case 1:
                        BixolonDiscoveryFragment myBixolonFragment =
                                (BixolonDiscoveryFragment)getFragmentManager().findFragmentByTag("Discover new devices");
                        if (myBixolonFragment != null && myBixolonFragment.isVisible()) {
                            return;
                        }

                        FragmentManager fmbixolon = getActivity().getSupportFragmentManager();
                        BixolonDiscoveryFragment bixolonDiscoveryFragment = new BixolonDiscoveryFragment();
                        bixolonDiscoveryFragment.mInitListener(PrinterSettingFragment.this);
                        bixolonDiscoveryFragment.show(fmbixolon, "Discover new devices");
                        break;
                    case 2:*/
                        WePDiscoveryFragment myWePFragment =
                                (WePDiscoveryFragment)getFragmentManager().findFragmentByTag("Discover new devices");
                        if (myWePFragment != null && myWePFragment.isVisible()) {
                            return;
                        }

                        FragmentManager fmwep = getActivity().getSupportFragmentManager();
                        WePDiscoveryFragment wePDiscoveryFragment = new WePDiscoveryFragment();
                        wePDiscoveryFragment.mInitListener(FragmentSettingsPrint.this);
                        wePDiscoveryFragment.show(fmwep, "Discover new devices");
                        break;
                }

                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    public void saveKotPrint() {
        HashMap<String, String> printer = printersList.get(spinnerKot.getSelectedItemPosition());
        if(printer.get("PrinterName").equalsIgnoreCase("--Select--"))
        {
            messageBox.Show("Select a Printer","In order to print please select a printer");
        }
        else {
            editor.putString("kot", printer.get("PrinterName"));
            editor.putString(printer.get("PrinterName"), printer.get("Target"));
            editor.commit();
        }
    }

    public void saveBillPrint() {
        HashMap<String, String> printer = printersList.get(spinnerBill.getSelectedItemPosition());
        if(printer.get("PrinterName").equalsIgnoreCase("--Select--"))
        {
            messageBox.Show("Select a Printer","In order to print please select a printer");
        }
        else {
            editor.putString("bill", printer.get("PrinterName"));
            editor.putString(printer.get("PrinterName"), printer.get("Target"));
            editor.commit();
        }
    }

    public void saveReportPrint() {
        HashMap<String, String> printer = printersList.get(spinnerReport.getSelectedItemPosition());
        if(printer.get("PrinterName").equalsIgnoreCase("--Select--"))
        {
            messageBox.Show("Select a Printer","In order to print please select a printer");
        }
        else {
            editor.putString("report", printer.get("PrinterName"));
            editor.putString(printer.get("PrinterName"), printer.get("Target"));
            editor.commit();
        }
    }

    public void saveReceiptPrint() {
        HashMap<String, String> printer = printersList.get(spinnerReceipt.getSelectedItemPosition());
        if(printer.get("PrinterName").equalsIgnoreCase("--Select--"))
        {
            messageBox.Show("Select a Printer","In order to print please select a printer");
        }
        else {
            editor.putString("report", printer.get("PrinterName"));
            editor.putString(printer.get("PrinterName"), printer.get("Target"));
            editor.commit();
        }
    }

    public void resetKotPrint() {
        editor.putString("kot","--Select--");
        editor.commit();
        spinnerKot.setSelection(0);
    }

    public void resetBillPrint() {
        editor.putString("bill","--Select--");
        editor.commit();
        spinnerBill.setSelection(0);
    }

    public void resetReportPrint() {
        editor.putString("report","--Select--");
        editor.commit();
        spinnerReport.setSelection(0);
    }

    public void resetReceiptPrint() {
        editor.putString("receipt","--Select--");
        editor.commit();
        spinnerReceipt.setSelection(0);
    }

    public int getPosition(String str){
        if(str.equalsIgnoreCase("--Select--"))
        {
            return 0;
        }
        else if(str.equalsIgnoreCase("Heyday"))
        {
            return 1;
        }
        else
            return -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null && requestCode == PRINTING_REQUEST_CODE_SOHAMSA)
        {
            if(data.getStringExtra("name").equalsIgnoreCase("kot"))
            {
                Toast.makeText(getActivity(), "KOT Printer Configured Successfully", Toast.LENGTH_SHORT).show();
                editor.putString("kot","Sohamsa");
                editor.commit();

            }
            else if(data.getStringExtra("name").equalsIgnoreCase("bill"))
            {
                Toast.makeText(getActivity(), "Billing Printer Configured Successfully", Toast.LENGTH_SHORT).show();
                editor.putString("bill","Sohamsa");
                editor.commit();

            }
            else if(data.getStringExtra("name").equalsIgnoreCase("report"))
            {
                Toast.makeText(getActivity(), "Report Printer Configured Successfully", Toast.LENGTH_SHORT).show();
                editor.putString("report","Sohamsa");
                editor.commit();
            }
            else if(data.getStringExtra("name").equalsIgnoreCase("receipt"))
            {
                Toast.makeText(getActivity(), "Receipt Printer Configured Successfully", Toast.LENGTH_SHORT).show();
                editor.putString("receipt","Sohamsa");
                editor.commit();
            }
        }
        else if(data!=null && requestCode == PRINTING_REQUEST_CODE_HEYDAY)
        {
            if(data.getStringExtra("name").equalsIgnoreCase("kot"))
            {
                Toast.makeText(getActivity(), "KOT Printer Configured Successfully", Toast.LENGTH_SHORT).show();
                editor.putString("kot","Heyday");
                editor.putInt("printer",data.getIntExtra("printer",0));
                editor.commit();
            }
            else if(data.getStringExtra("name").equalsIgnoreCase("bill"))
            {
                Toast.makeText(getActivity(), "Billing Printer Configured Successfully", Toast.LENGTH_SHORT).show();
                editor.putString("bill","Heyday");
                editor.putInt("printer",data.getIntExtra("printer",0));
                editor.commit();
            }
            else if(data.getStringExtra("name").equalsIgnoreCase("report"))
            {
                Toast.makeText(getActivity(), "Report Printer Configured Successfully", Toast.LENGTH_SHORT).show();
                editor.putString("report","Heyday");
                editor.putInt("printer",data.getIntExtra("printer",0));
                editor.commit();
            }
            else if(data.getStringExtra("name").equalsIgnoreCase("receipt"))
            {
                Toast.makeText(getActivity(), "Receipt Printer Configured Successfully", Toast.LENGTH_SHORT).show();
                editor.putString("receipt","Heyday");
                editor.putInt("printer",data.getIntExtra("printer",0));
                editor.commit();
            }
        }
    }

    protected void testPrintSohamsa(String name,int num) {
        Intent intent = new Intent(getActivity(), PrinterSohamsaActivity.class);
        intent.putExtra("printType", "TEST");
        intent.putExtra("code", PRINTING_REQUEST_CODE_SOHAMSA);
        intent.putExtra("name", name);
        intent.putExtra("printerNum", num);
        startActivityForResult(intent,PRINTING_REQUEST_CODE_SOHAMSA);
    }
    protected void testPrintHeyDay(String name,int num) {
        Intent intent = new Intent(getActivity(), PrinterFragment.class);
        intent.putExtra("printType", "TEST");
        intent.putExtra("code", PRINTING_REQUEST_CODE_HEYDAY);
        intent.putExtra("name", name);
        intent.putExtra("printerNum", num);
        startActivityForResult(intent,PRINTING_REQUEST_CODE_HEYDAY);
        //Toast.makeText(this, "wefh3yir", Toast.LENGTH_SHORT).show();
    }

    public void Close()
    {
        getActivity().finish();
    }

    @Override
    public void onDeviceClicked(String key, HashMap<String, String> device) {
        stringPrinter.add(device.get("PrinterName"));
        printersList.add(device);
        printer = device;
        dataAdapter.notifyDataSetChanged();
    }
}
