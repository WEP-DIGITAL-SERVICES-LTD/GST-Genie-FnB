package com.wepindia.pos.views.InwardManagement;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.print.PrintManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.Supplier_Model;
import com.wep.common.app.models.PurchaseOrderBean;
import com.wep.common.app.utils.Preferences;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GSTSupport.HTTPAsyncTask_Frag;
import com.wepindia.pos.GenericClasses.DateTime;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.SendBillInfoToCustUtility;
import com.wepindia.pos.utils.Validations;
import com.wepindia.pos.views.InwardManagement.Adapters.ListPOGINAdapter;
import com.wepindia.pos.views.InwardManagement.Adapters.SupplierSuggestionAdapter;
import com.wepindia.pos.views.InwardManagement.Listeners.OnViewPOGINListener;
import com.wepindia.printers.WepPrinterBaseActivity;
import com.wepindia.printers.WifiPrinterBaseActivity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListPOGinNote extends WepPrinterBaseActivity implements OnViewPOGINListener, HTTPAsyncTask_Frag.OnHTTPRequestCompletedListener {

    private static final String TAG = ListPOGinNote.class.getSimpleName();

    private Context myContext;
    private DatabaseHandler dbHandler;
    private Toolbar toolbar;
    private MessageDialog MsgBox;
    private String strUserName, strUserId;

    @BindView(R.id.autocomplete_po_gin_list_search)
    AutoCompleteTextView avSupplierName;
    @BindView(R.id.iv_po_gin_list_refresh)
    ImageView iv_refresh;

    @BindView(R.id.btn_po_gin_DateFrom)
    Button btn_po_gin_DateFrom;
    @BindView(R.id.btn_po_gin_DateTo)
    Button btn_po_gin_DateTo;
    @BindView(R.id.btn_po_gin_view)
    Button btn_po_gin_view;
    @BindView(R.id.etPOGINDateStart)
    EditText etPOGINDateStart;
    @BindView(R.id.etPOGINDateEnd)
    EditText etPOGINDateEnd;

    @BindView(R.id.rb_po_gin_list_all)
    RadioButton rb_po_gin_list_all;
    @BindView(R.id.rb_po_gin_list_po)
    RadioButton rb_po_gin_list_po;
    @BindView(R.id.rb_po_gin_list_gin)
    RadioButton rb_po_gin_list_gin;
    @BindView(R.id.rg_po_gin_list)
    RadioGroup rg_po_gin_list;

    @BindView(R.id.rv_po_gin_list_items)
    ListView rv_po_gin_list_items;

    private String strDate = "";
    private Date startDate_date, endDate_date;
    private DateTime objDate;
    private ShowData showData;
    private List<PurchaseOrderBean> purchaseOrderBeanList = null;
    private ListPOGINAdapter listPOGINAdapter;
    private ArrayList<HashMap<String, String>> listName;
    private Supplier_Model supplier_model;
    private PurchaseOrderBean beanShare;

    private String PDF_PURCHASEORDER_GENERATE_PATH = Environment.getExternalStorageDirectory().getPath() + "/WeP_Retail_PurchaseOrder/";

    @Override
    public void onConfigurationRequired() {

    }

    @Override
    public void onPrinterAvailable(int flag) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list_po_gin_note);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        dbHandler = new DatabaseHandler(ListPOGinNote.this);
        myContext = this;
        MsgBox =  new MessageDialog(myContext);
        purchaseOrderBeanList = new ArrayList<PurchaseOrderBean>();

        try {

            strUserName = getIntent().getStringExtra("USER_NAME");
            strUserId = getIntent().getStringExtra("USER_ID");

            Date d = new Date();
            CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
            objDate = new DateTime(s.toString());
            //tvTitleDate.setText("Date : " + s);
            com.wep.common.app.ActionBarUtils.setupToolbar(ListPOGinNote.this,toolbar,getSupportActionBar()," List PO & GIN ",strUserName," Date:"+s.toString());

            dbHandler.CreateDatabase();
            dbHandler.OpenDatabase();

            setDefaultDates();

            rv_po_gin_list_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    PurchaseOrderBean model = (PurchaseOrderBean) adapterView.getAdapter().getItem(i);
                    if (model != null) {
                        FragmentManager fm = getSupportFragmentManager();
                        ViewPOGINNote viewPOGINNote = new ViewPOGINNote();
                        Bundle args = new Bundle();
                        args.putSerializable("PurchaseOrderBean", model);
                        viewPOGINNote.setArguments(args);
                        viewPOGINNote.setOnViewPOGINListener(ListPOGinNote.this);
                        viewPOGINNote.show(fm, "View PO & GIN");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick({R.id.btn_po_gin_DateFrom, R.id.btn_po_gin_DateTo, R.id.btn_po_gin_view, R.id.iv_po_gin_list_refresh})
    protected void mBtnClick(View v) {
        switch (v.getId()) {
            case R.id.btn_po_gin_DateFrom:
                From();
                break;
            case R.id.btn_po_gin_DateTo:
                To();
                break;
            case R.id.btn_po_gin_view:
                if (supplier_model == null){
                    MsgBox.Show("Invalid Information", "Please select a firm first.");
                } else {
                    displayList();
                }
                break;
            case R.id.iv_po_gin_list_refresh:
                mClear();
                displayList();
                break;
        }
    }

    @OnClick({R.id.rb_po_gin_list_all, R.id.rb_po_gin_list_po, R.id.rb_po_gin_list_gin})
    public void onRadioButtonClicked(RadioButton radioButton) {
        if (showData == null)
            showData = new ShowData();
        showData.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayList();
        initAutoCompleteTextDataForSupplierName();
    }

    @Override
    public void onPurchaseOrderShareClicked(PurchaseOrderBean purchaseOrderBean) {
        beanShare = purchaseOrderBean;
        Cursor cursor = dbHandler.getSupplierEmailById(Integer.parseInt(purchaseOrderBean.getStrSupplierId()));
        if (cursor != null && cursor.moveToFirst()){
            purchaseOrderBean.setStrSupplierEmail(cursor.getString(cursor.getColumnIndex("SupplierEmail")));
        } else {
            purchaseOrderBean.setStrSupplierEmail("");
        }
        sendPurchaseOrder(String.valueOf(purchaseOrderBean.getiPurchaseOrderNo()), purchaseOrderBean.getStrSupplierEmail());
    }

    @Override
    public void onPurchaseOrderPrintClicked(PurchaseOrderBean purchaseOrderBean) {
        String prf = Preferences.getSharedPreferencesForPrint(ListPOGinNote.this).getString("purchaseOrder", "--Select--");
        if (prf.equalsIgnoreCase("Heyday")) {
            MsgBox.Show("Warning", "Purchase Order can't be printed on Bluetooth/USB Printers. Please configure WiFi Printer.");
        } else if (prf.equalsIgnoreCase("NGX")) {
            MsgBox.Show("Warning", "Purchase Order can't be printed on Bluetooth/USB Printers. Please configure WiFi Printer.");
        } else if (prf.equalsIgnoreCase("TM Printer")) {
            MsgBox.Show("Warning", "Purchase Order can't be printed on Bluetooth/USB Printers. Please configure WiFi Printer.");
        } else if (prf.equalsIgnoreCase(Constants.USB_BIXOLON_PRINTER_NAME)) {
            MsgBox.Show("Warning", "Purchase Order can't be printed on Bluetooth/USB Printers. Please configure WiFi Printer.");
        } else if (prf.equalsIgnoreCase(Constants.USB_WEP_PRINTER_NAME)) {
            MsgBox.Show("Warning", "Purchase Order can't be printed on Bluetooth/USB Printers. Please configure WiFi Printer.");
        } else if(prf.equalsIgnoreCase(Constants.USB_TVS_PRINTER_NAME)) {
            MsgBox.Show("Warning", "Purchase Order can't be printed on Bluetooth/USB Printers. Please configure WiFi Printer.");
        } else if(prf.equalsIgnoreCase(Constants.USB_WiFi_PRINTER_NAME)){
            if (isWifiConnected()) {
                String E_INVOICE_NAME;
                if (purchaseOrderBean.getStrInvoiceNo().isEmpty() && purchaseOrderBean.getStrInvoiceDate().isEmpty()) {
                    E_INVOICE_NAME = "PO_" + purchaseOrderBean.getiPurchaseOrderNo() + "_" + "-" + "_" + "-" + ".pdf";
                } else {
                    E_INVOICE_NAME = "PO_" + purchaseOrderBean.getiPurchaseOrderNo() + "_" + purchaseOrderBean.getStrInvoiceNo() + "_" + purchaseOrderBean.getStrInvoiceDate() + ".pdf";
                }
                String filePathString = PDF_PURCHASEORDER_GENERATE_PATH + E_INVOICE_NAME;
                File f = new File(filePathString);
                if(f.exists() && !f.isDirectory()) {

                    PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                    printManager.print("Printing Invoice...", new WifiPrinterBaseActivity(filePathString), null);

                } else {
                    MsgBox.Show("Warning", "Invoice does not exist in PDF form or moved to a different location.\n" +
                            "If moved to other location please place it in WeP_Retail_Invoices directory.");
                }
            } else {
                Toast.makeText(myContext, "Please connect to a WiFi network first.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(myContext, "Printer not configured. Kindly goto settings and configure printer", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHttpRequestComplete(int requestCode, String filePath) {

    }

    private void displayList() {
        try {
            if (showData == null) {
                showData = new ShowData();
                showData.execute();
            } else {
                Toast.makeText(myContext, "Please wait...", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHomePressed() {

    }

    private class ShowData extends AsyncTask {

        Cursor cursor = null;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(myContext);
            progressDialog.setIcon(R.mipmap.ic_company_logo);
            progressDialog.setTitle(Constants.processing);
            progressDialog.setMessage("Loading POs & GINs. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            String from_date = etPOGINDateStart.getText().toString();
            String to_date = etPOGINDateEnd.getText().toString();

            try {
                Date from = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(from_date);
                Date to = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(to_date);

                switch (rg_po_gin_list.getCheckedRadioButtonId()) {
                    case R.id.rb_po_gin_list_all:
                        if (supplier_model != null)
                            cursor = dbHandler.getPurchaseOrderByDates(supplier_model.get_id(), String.valueOf(from.getTime()), String.valueOf(to.getTime()));
                        else
                            cursor = dbHandler.getPurchaseOrderByDates(0, String.valueOf(from.getTime()), String.valueOf(to.getTime()));
                        break;
                    case R.id.rb_po_gin_list_po:
                        if (supplier_model != null)
                            cursor = dbHandler.getPurchaseOrderByDates(supplier_model.get_id(),String.valueOf(from.getTime()), String.valueOf(to.getTime()), 0);
                        else
                            cursor = dbHandler.getPurchaseOrderByDates(0, String.valueOf(from.getTime()), String.valueOf(to.getTime()), 0);
                        break;
                    case R.id.rb_po_gin_list_gin:
                        if (supplier_model != null)
                            cursor = dbHandler.getPurchaseOrderByDates(supplier_model.get_id(),String.valueOf(from.getTime()), String.valueOf(to.getTime()), 1);
                        else
                            cursor = dbHandler.getPurchaseOrderByDates(0, String.valueOf(from.getTime()), String.valueOf(to.getTime()), 1);
                        break;
                    default:
                        if (supplier_model != null)
                            cursor = dbHandler.getPurchaseOrderByDates(supplier_model.get_id(), String.valueOf(from.getTime()), String.valueOf(to.getTime()));
                        else
                            cursor = dbHandler.getPurchaseOrderByDates(0, String.valueOf(from.getTime()), String.valueOf(to.getTime()));
                        break;
                }

                purchaseOrderBeanList.clear();
                if (cursor != null && cursor.moveToFirst()) {
                    boolean isExisting = false;
                    do {
                        PurchaseOrderBean purchaseOrder = new PurchaseOrderBean();

                        for (int i=0; i<purchaseOrderBeanList.size(); i++) {
                            if (purchaseOrderBeanList.get(i).getStrInvoiceNo().equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_InvoiceNo)))
                                    && purchaseOrderBeanList.get(i).getStrInvoiceDate().equalsIgnoreCase(getDate(cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_InvoiceDate)), "dd-MM-yyyy"))
                                    && purchaseOrderBeanList.get(i).getStrSupplierId().equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierCode)))
                                    && purchaseOrderBeanList.get(i).getiPurchaseOrderNo() == cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseOrderNo))){

                                purchaseOrderBeanList.get(i).setDblAmount(purchaseOrderBeanList.get(i).getDblAmount()
                                        + cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Amount)) );

                                isExisting = true;
                            }
                        }

                        if (!isExisting) {
                            purchaseOrder.set_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id)));
                            purchaseOrder.setiItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ItemId)));
                            purchaseOrder.setStrSupplierId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierCode)));
                            purchaseOrder.setStrSupplierName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUPPLIERNAME)));
                            purchaseOrder.setStrSupplierPhone(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierPhone)));
                            purchaseOrder.setStrSupplierAddress(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierAddress)));
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)) != null) {
                                purchaseOrder.setStrSupplierGSTIN(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                            } else {
                                purchaseOrder.setStrSupplierGSTIN("");
                            }
                            purchaseOrder.setStrBarcode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
                            purchaseOrder.setStrItemName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemName)));
                            if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)) > 0) {
                                purchaseOrder.setDblPurchaseRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)));
                            }
                            if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Rate)) > 0) {
                                purchaseOrder.setDblRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Rate)));
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)) != null) {
                                purchaseOrder.setStrSupplyType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)));
                            } else {
                                purchaseOrder.setStrSupplyType("");
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierType)) != null) {
                                purchaseOrder.setStrSupplierType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierType)));
                            } else {
                                purchaseOrder.setStrSupplierType("");
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)) != null) {
                                purchaseOrder.setStrHSNCode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
                            } else {
                                purchaseOrder.setStrHSNCode("");
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)) != null) {
                                purchaseOrder.setStrUOM(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)));
                            } else {
                                purchaseOrder.setStrUOM("");
                            }

                            purchaseOrder.setDblCGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
                            purchaseOrder.setDblSGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
                            purchaseOrder.setDblIGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
                            purchaseOrder.setDblCessRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)));
                            purchaseOrder.setDblCessAmountPerUnit(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmountPerUnit)));
                            purchaseOrder.setDblAdditionalCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));
                            purchaseOrder.setDblQuantity(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Quantity)));
                            purchaseOrder.setDblAdditionalChargeAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeAmount)));
                            purchaseOrder.setDblAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Amount)));

                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeName)) != null) {
                                purchaseOrder.setStrAdditionalCharge(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeName)));
                            } else {
                                purchaseOrder.setStrAdditionalCharge("");
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_InvoiceNo)) != null) {
                                purchaseOrder.setStrInvoiceNo(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_InvoiceNo)));
                            } else {
                                purchaseOrder.setStrInvoiceNo("-");
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_InvoiceDate)) != null) {
                                purchaseOrder.setStrInvoiceDate(getDate(cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_InvoiceDate)), "dd-MM-yyyy"));
                            } else {
                                purchaseOrder.setStrInvoiceDate("-");
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseOrderNo)) != null) {
                                purchaseOrder.setiPurchaseOrderNo(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseOrderNo)));
                            } else {
                                purchaseOrder.setiPurchaseOrderNo(-1);
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseOrderDate)) != null) {
                                purchaseOrder.setStrPurchaseOrderDate(getDate(cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseOrderDate)), "dd-MM-yyyy"));
                            } else {
                                purchaseOrder.setStrPurchaseOrderDate("-");
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_isGoodinward)) != null) {
                                purchaseOrder.setiIsgoodInward(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_isGoodinward)));
                            } else {
                                purchaseOrder.setiIsgoodInward(0);
                            }
                            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierPOS)) != null) {
                                purchaseOrder.setStrSupplierPOS(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierPOS)));
                            } else {
                                purchaseOrder.setStrSupplierPOS("");
                            }

                            purchaseOrderBeanList.add(purchaseOrder);
                        }
                        isExisting = false;
                    } while (cursor.moveToNext());
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (progressDialog != null)
                progressDialog.dismiss();
            if (purchaseOrderBeanList != null) {
                if (listPOGINAdapter == null) {
                    listPOGINAdapter = new ListPOGINAdapter(myContext, R.layout.row_list_po_gin, purchaseOrderBeanList);
                    rv_po_gin_list_items.setAdapter(listPOGINAdapter);
                } else {
                    listPOGINAdapter.setNotifyAdapter(purchaseOrderBeanList);
                }
            }
            if (showData != null)
                showData = null;
        }
    }

    public void From() {
        DateSelection(1);
    }

    public void To() {
        if (!etPOGINDateStart.getText().toString().equalsIgnoreCase("")) {
            DateSelection(2);

        } else {
            MsgBox.Show("Warning", "Please select report FROM date");
        }
    }

    private void DateSelection(final int DateType) {        // StartDate: DateType = 1 EndDate: DateType = 2
        try {
            AlertDialog.Builder dlgReportDate = new AlertDialog.Builder(myContext);
            final DatePicker dateReportDate = new DatePicker(myContext);

            // Initialize date picker value to business date
            dateReportDate.updateDate(objDate.getYear(), objDate.getMonth(), objDate.getDay());
            String strMessage = "";
            if (DateType == 1) {
                strMessage = "Select report Start date";
            } else {
                strMessage = "Select report End date";
            }

            dlgReportDate
                    .setIcon(R.mipmap.ic_company_logo)
                    .setTitle("Date Selection")
                    .setMessage(strMessage)
                    .setView(dateReportDate)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            // richa date format change

                            //strDate = String.valueOf(dateReportDate.getYear()) + "-";
                            if (dateReportDate.getDayOfMonth() < 10) {
                                strDate = "0" + String.valueOf(dateReportDate.getDayOfMonth()) + "-";
                            } else {
                                strDate = String.valueOf(dateReportDate.getDayOfMonth()) + "-";
                            }
                            if (dateReportDate.getMonth() < 9) {
                                strDate += "0" + String.valueOf(dateReportDate.getMonth() + 1) + "-";
                            } else {
                                strDate += String.valueOf(dateReportDate.getMonth() + 1) + "-";
                            }

                            strDate += String.valueOf(dateReportDate.getYear());

                            if (DateType == 1) {
                                etPOGINDateStart.setText(strDate);
                                startDate_date = new Date(dateReportDate.getYear() - 1900, dateReportDate.getMonth(), dateReportDate.getDayOfMonth());
                            } else {
                                etPOGINDateEnd.setText(strDate);
                                endDate_date = new Date(dateReportDate.getYear() - 1900, dateReportDate.getMonth(), dateReportDate.getDayOfMonth());

                            }
                            Log.d("ReportDate", "Selected Date:" + strDate);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefaultDates() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            etPOGINDateEnd.setText(format.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, -1);
            etPOGINDateStart.setText(format.format(calendar.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        String time = "";
        if (milliSeconds > 0) {
            // Create a DateFormatter object for displaying date in specified format.
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            time = formatter.format(calendar.getTime());
        } else
            time = "";

        return time;
    }

    private void initAutoCompleteTextDataForSupplierName() {

        Cursor cursor = null;
        try {
            cursor = dbHandler.getAllSuppliersByMode(1);
            listName = new ArrayList<HashMap<String, String>>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("name", cursor.getString(cursor.getColumnIndex("SupplierName")));
                    data.put("phone", cursor.getString(cursor.getColumnIndex("SupplierPhone")));
                    listName.add(data);
                } while (cursor.moveToNext());
            }
            SupplierSuggestionAdapter dataAdapter = new SupplierSuggestionAdapter(myContext, R.layout.adapter_supplier_name, listName);
            avSupplierName.setThreshold(1);
            avSupplierName.setAdapter(dataAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        avSupplierName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> data = (HashMap<String, String>) parent.getAdapter().getItem(position);

                String supplierphone_str = data.get("phone");
                avSupplierName.setText(data.get("name"));
                Cursor supplierdetail_cursor = dbHandler.getSupplierDetailsByPhone(supplierphone_str); // TODO: changed here
                if (supplierdetail_cursor != null && supplierdetail_cursor.moveToFirst()) {
                    supplier_model = new Supplier_Model();
                    supplier_model.set_id(supplierdetail_cursor.getInt(supplierdetail_cursor.getColumnIndex("_id")));
                    supplier_model.setSupplierName(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierName")));
                    supplier_model.setSupplierPhone(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierPhone")));
                    supplier_model.setSupplierEmail(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierEmail")));
                    supplier_model.setSupplierAddress(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierAddress")));
                    supplier_model.setSupplierGSTIN(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("GSTIN")));
                    if (!supplier_model.getSupplierGSTIN().isEmpty())
                        supplier_model.setSupplierType("Registered");
                    else
                        supplier_model.setSupplierType("UnRegistered");
                    supplier_model.setIsActive(supplierdetail_cursor.getInt(supplierdetail_cursor.getColumnIndex("isActive")));
                }
            }
        });
    }

    void mClear (){
        supplier_model = null;
        avSupplierName.setText("");
        rb_po_gin_list_all.setChecked(true);
        setDefaultDates();
    }

    private void sendPurchaseOrder(final String purchaseOrderNo, final String supplierEmail) {

        if (!isWifiConnected()) {
            Toast.makeText(myContext, "Kindly connect to internet to share the bill.", Toast.LENGTH_LONG).show();
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.customer_detail_missing_alert, null);
        final EditText etCustPhone = alertLayout.findViewById(R.id.et_phone);
        final EditText etCustEmail = alertLayout.findViewById(R.id.et_email);
        final TextView tvMobile = alertLayout.findViewById(R.id.mobile_title);
        final TextView tvEmail = alertLayout.findViewById(R.id.email_title);
        final Button btnCancel = alertLayout.findViewById(R.id.btn_cancel);
        final Button btnSend = alertLayout.findViewById(R.id.btn_send);
        final CheckBox customerMobileCheckBox = (CheckBox) alertLayout.findViewById(R.id.customer_mobile);
        final CheckBox customerEmailCheckBox = (CheckBox) alertLayout.findViewById(R.id.customer_email);
        etCustPhone.setVisibility(View.GONE);
        customerMobileCheckBox.setVisibility(View.GONE);
        customerEmailCheckBox.setVisibility(View.GONE);
        tvMobile.setVisibility(View.GONE);

        etCustEmail.setEnabled(true);
        etCustEmail.setText(supplierEmail);

        final AlertDialog.Builder alert = new AlertDialog.Builder(myContext);
        alert.setTitle("Share PurchaseOrder");

        alert.setIcon(R.mipmap.ic_company_logo);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCustEmail.isEnabled() && etCustEmail.getText().toString().isEmpty()) {
                    etCustEmail.setError("Please fill this field.");
                } else if (etCustEmail.isEnabled() && etCustEmail != null
                        && !etCustEmail.getText().toString().isEmpty()
                        && !Validations.isValidEmailAddress(etCustEmail.getText().toString())) {
                    etCustEmail.setError("Please Enter Valid Email id.");
                } else {
                    send(etCustEmail.getText().toString(), String.valueOf(beanShare.getiPurchaseOrderNo()), beanShare.getStrInvoiceNo(), beanShare.getStrInvoiceDate());
                    Toast.makeText(myContext, "Sending....", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void send(String custEmail, String purchaseOrderNo, String invoiceNo, String invoiceDate) {
        String emailContent = "Please find the PurchaseOrder as attachment of this mail.";
        String firmName = "";

        String filename = "PO_" + purchaseOrderNo + "_" + invoiceNo + "_" + invoiceDate + ".pdf";

        String attachment = Environment.getExternalStorageDirectory().getPath() + "/WeP_Retail_PurchaseOrder/"
                + filename;

        SendBillInfoToCustUtility smsUtility = new SendBillInfoToCustUtility(myContext, "Purchase Order",  emailContent, "", "", false, true, false,
                ListPOGinNote.this, custEmail, attachment, filename, firmName);
        smsUtility.sendBillInfo();
        Toast.makeText(myContext, "Sharing Purchase Order...", Toast.LENGTH_SHORT).show();
    }

    private boolean isWifiConnected() {
        boolean isWifiConnected = false;
        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                isWifiConnected = true;
            }

        } catch (Exception e) {
            isWifiConnected = false;
            Log.e(TAG, e.toString());
        } finally {
            return isWifiConnected;
        }
    }
}
