/****************************************************************************
 * Project Name		:	VAJRA
 * <p/>
 * File Name		:	ItemManagementActivity
 * <p/>
 * Purpose			:	Represents Item creation activity, takes care of all
 * UI back end operations in this activity, such as event
 * handling data read from or display in views.
 * <p/>
 * DateOfCreation	:	20-November-2012
 * <p/>
 * Author			:	Balasubramanya Bharadwaj B S
 ****************************************************************************/
package com.wepindia.pos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.Item;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.ItemOutward;
import com.wep.common.app.views.WepButton;
import com.wepindia.pos.GenericClasses.EditTextInputHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.adapters.ItemOutwardAdapter;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.StockOutwardMaintain;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemManagementActivity extends WepBaseActivity {

    Context myContext;
    BufferedReader buffer ;
    DatabaseHandler dbItems = new DatabaseHandler(ItemManagementActivity.this);
    MessageDialog MsgBox;
    int ROWCLICKEVENT = 0;
    EditText txtLongName, txtShortName, txtBarcode, txtDineIn1, txtDineIn2, txtDineIn3, txtStock;
    Spinner spnrDepartment, spnrCategory, spnrKitchen, spnrSalesTax, spnrAdditionalTax, spnrOptionalTax1, spnrOptionalTax2, spnrDiscount;
    CheckBox chkPriceChange, chkDiscountEnable, chkBillWithStock;
    RadioButton rbForwardTax, rbReverseTax;
    WepButton btnAdd, btnEdit, btnUploadExcel, btnSaveExcel;
    ImageView imgItemImage;
    TableLayout tblItems;
    TextView tvDineIn1Caption, tvDineIn2Caption, tvDineIn3Caption, tvFileName;
    Spinner spnrG_S, spnrMOU, spnrtaxationtype;
    EditText etRate ,etQuantity ,etHSN ,etGstTax ,etDiscount ;
    FrameLayout frame_rate_nongst, frame_rate_gst, frame_serviceTax, frame_salesTax, frame_GSTTax;
    EditText edtMenuCode, edtItemSalesTax, edtItemServiceTax;
    TextView tvCaptionSNo,tvCaptionLongName,tvCaptionDineInPrice1,tvCaptionDineInPrice2,tvCaptionDineInPrice3,tvCaptionStock, tvCaptionRate,tvCaptionQuanity,tvCaptionMOU,tvCaptionGSTRate,tvCaptionImage,tvCaptionDelete;
    float fRate =0, fQuantity =0, fGSTTax = 0, fDiscount = 0;
    String txtHSNCode = "";
    ArrayAdapter<String> adapDeptCode, adapCategCode, adapKitCode, adapTax, adapDiscount;
    ArrayAdapter<CharSequence> supplytypeAdapter,MOUAdapter, taxationtypeAdapter;
    String strItemId, strImageUri = "";
    String strUploadFilepath = "", strUserName = "";
    private List<String> labelsDept;
    private List<String> labelsCateg;
    String GSTEnable="0", HSNEnable_out="0";
    int iMenuCode = 0;
    Cursor crsrSettings = null;
    private Toolbar toolbar;
    String businessDate = "";

    ItemOutwardAdapter itemListAdapter = null;
    private ListView listViewItems;
    ArrayList<ItemOutward> dataList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemmaster);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myContext = this;
        MsgBox = new MessageDialog(myContext);

        strUserName = getIntent().getStringExtra("USER_NAME");
        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        com.wep.common.app.ActionBarUtils.setupToolbar(ItemManagementActivity.this,toolbar,getSupportActionBar(),"Item Master",strUserName," Date:"+s.toString());
        try {

            InitializeViewVariables();
            ResetItem();
            dbItems.CreateDatabase();
            dbItems.OpenDatabase();
            crsrSettings = dbItems.getBillSetting();
            if (crsrSettings.moveToFirst()) {
                tvDineIn1Caption.setText(crsrSettings.getString(crsrSettings.getColumnIndex("DineIn1Caption")));
                tvDineIn2Caption.setText(crsrSettings.getString(crsrSettings.getColumnIndex("DineIn2Caption")));
                tvDineIn3Caption.setText(crsrSettings.getString(crsrSettings.getColumnIndex("DineIn3Caption")));
                businessDate = crsrSettings.getString(crsrSettings.getColumnIndex("BusinessDate"));
                GSTEnable = crsrSettings.getString(crsrSettings.getColumnIndex("GSTEnable"));
                HSNEnable_out =  crsrSettings.getString(crsrSettings.getColumnIndex("HSNCode_Out"));

                int ServiceTaxType = crsrSettings.getInt(crsrSettings.getColumnIndex("ServiceTaxType"));
//                if (ServiceTaxType != 1) {
//                    spnrAdditionalTax.setEnabled(false);
//                }
            } else {
                Toast.makeText(myContext, "Error: Reading settings", Toast.LENGTH_SHORT).show();
            }
            InitializeAdapters();
            //DisplayItems();

            loadSpinnerData();
            loadSpinnerData1();

            if(crsrSettings.getString(crsrSettings.getColumnIndex("ItemNoReset")).equalsIgnoreCase("1"))
            {
                edtMenuCode.setEnabled(true);
            } else {
                edtMenuCode.setEnabled(false);
            }

            spnrtaxationtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                //@Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String choice = spnrtaxationtype.getItemAtPosition(position).toString();
                    if (choice.equalsIgnoreCase("nonGST") || (choice.equalsIgnoreCase("NilRate")) ||
                            (choice.equalsIgnoreCase("Exempt"))) {
                        MsgBox.setMessage(" For NilRate, nonGST, Exempt, tax will be 0")
                                .setPositiveButton("OK",null)
                                .show();
                        etGstTax.setText("0");
                        etGstTax.setEnabled(false);
                    }
                    else
                    {
                        etGstTax.setText("0");
                        etGstTax.setEnabled(true);
                    }
                }

                //@Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            // Upload Excel data to Database
            btnUploadExcel = (WepButton) findViewById(R.id.buttonUploadExcel);
            btnSaveExcel = (WepButton) findViewById(R.id.buttonSaveExcel);

            btnUploadExcel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //startActivityForResult(new Intent(myContext, UploadFilePickerActivity.class), 1);
                    Intent intent = new Intent(myContext, FilePickerActivity.class);
                    intent.putExtra("contentType","csv");
                    startActivityForResult(intent, FilePickerActivity.FILE_PICKER_CODE);
                }
            });

            btnSaveExcel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    try {
                        AssetManager manager = myContext.getAssets();
                        //Environment.getExternalStorageDirectory() + "/stock1.xls";
                        //InputStream inputStream = getResources().openRawResource(R.raw.itemdb);
                        if (strUploadFilepath.equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "No File Found", Toast.LENGTH_SHORT).show();
                        } else {
                            //tvFileName.setText(strUploadFilepath);
                            //String path = Environment.getExternalStorageDirectory() + "/itemdb.csv";
                            String path = strUploadFilepath;
                            FileInputStream inputStream = new FileInputStream(path);
                            buffer = new BufferedReader(new InputStreamReader(inputStream));
                            Cursor cursor = dbItems.getCurrentDate();
//                            String currentdate = "";
//                            if(cursor.moveToNext())
//                                currentdate = cursor.getString(cursor.getColumnIndex("BusinessDate"));
                            if (dataList.size()>0)
                            {
                                final String current_date = businessDate;
                                AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                                        .setTitle("Replace Item")
                                        .setMessage(" Are you sure you want to Replace all the existing Items, if any")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                int deleted = dbItems.clearOutwardItemdatabase();
                                                Log.d("ItemManagement"," Items deleted before uploading excel :"+deleted);
                                                deleted = dbItems.clearOutwardStock(current_date);
                                                Log.d("ItemManagement"," Outward Stock deleted before uploading excel :"+deleted);
                                                new AsyncTask<Void,Void,Void>(){
                                                    ProgressDialog pd;

                                                    @Override
                                                    protected void onPreExecute() {
                                                        super.onPreExecute();
                                                        pd = new ProgressDialog(ItemManagementActivity.this);
                                                        pd.setMessage("Loading...");
                                                        pd.setCancelable(false);
                                                        pd.show();
                                                    }

                                                    @Override
                                                    protected Void doInBackground(Void... params) {

                                                        try {
                                                            String line = "";
                                                            int iteration = 0;

                                                            while ((line = buffer.readLine()) != null) {
                                                                final String[] colums = line.split(",");
                                                                if (colums.length != 9) {
                                                                    Log.d("CSVParser", "Skipping Bad CSV Row");
                                                                    //Toast.makeText(myContext, "Skipping Bad CSV Row", Toast.LENGTH_LONG).show();
                                                                    continue;
                                                                }
                                                                if (iteration == 0) {
                                                                    iteration++;
                                                                    continue;
                                                                }
                                                                InsertItem(colums[1].trim(), colums[1].trim(), Float.parseFloat(colums[2].trim()),
                                                                        Float.parseFloat(colums[3].trim()), Float.parseFloat(colums[4].trim()),
                                                                        0, 0, 0, Float.parseFloat(colums[5].trim()), 0, 0, 0, 0, 0, 0, 0, 1,
                                                                        2, 0, 0, 0, "", "",0f,0f,"",0f,"",colums[8].trim(),"", Float.parseFloat(colums[6].trim()),
                                                                        Float.parseFloat(colums[7].trim()), Integer.valueOf(colums[0].trim()));
                                                            }
                                                            StockOutwardMaintain stock_outward = new StockOutwardMaintain(myContext, dbItems);
                                                            stock_outward.saveOpeningStock_Outward(current_date);
                                                        } catch (Exception exp) {
                                                            exp.printStackTrace();
                                                            //Toast.makeText(myContext, exp.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                        return null;
                                                    }

                                                    @Override
                                                    protected void onPostExecute(Void aVoid) {
                                                        super.onPostExecute(aVoid);
                                                        try{
                                                            ResetItem();
                                                            //ClearItemTable();
                                                            DisplayItemList();
                                                            Toast.makeText(getApplicationContext(), "Items Imported Successfully", Toast.LENGTH_LONG).show();
                                                            pd.dismiss();
                                                        }catch (Exception e){
                                                            e.printStackTrace();
                                                            //Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }.execute();



                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                //ClearItemTable();
                                                DisplayItemList();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            else
                            {
                                new AsyncTask<Void,Void,Void>(){
                                    ProgressDialog pd;

                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        pd = new ProgressDialog(ItemManagementActivity.this);
                                        pd.setMessage("Loading...");
                                        pd.setCancelable(false);
                                        pd.show();
                                    }

                                    @Override
                                    protected Void doInBackground(Void... params) {

                                        try {
                                            String line = "";
                                            int iteration = 0;

                                            while ((line = buffer.readLine()) != null) {
                                                final String[] colums = line.split(",");
                                                if (colums.length != 9) {
                                                    Log.d("CSVParser", "Skipping Bad CSV Row");
                                                    //Toast.makeText(myContext, "Skipping Bad CSV Row", Toast.LENGTH_LONG).show();
                                                    continue;
                                                }
                                                if (iteration == 0) {
                                                    iteration++;
                                                    continue;
                                                }
                                                InsertItem(colums[1].trim(), colums[1].trim(), Float.parseFloat(colums[2].trim()),
                                                        Float.parseFloat(colums[3].trim()), Float.parseFloat(colums[4].trim()),
                                                        0, 0, 0, Float.parseFloat(colums[5].trim()), 0, 0, 0, 0, 0, 0, 0, 1,
                                                        2, 0, 0, 0, "", "",0f,0f,"",0f,"",colums[8].trim(),"", Float.parseFloat(colums[6].trim()),
                                                        Float.parseFloat(colums[7].trim()), Integer.valueOf(colums[0].trim()));
                                            }

                                        } catch (Exception exp) {
                                            Toast.makeText(myContext, exp.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        try{
                                            ResetItem();
                                            //ClearItemTable();
                                            DisplayItemList();
                                            Toast.makeText(myContext, "Items Imported Successfully", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }catch (Exception e){
                                            Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }.execute();

                            }
                        }
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    finally {
                        tvFileName.setText("");
                        strUploadFilepath="";
                    }
                }
            });

            SetGSTView();
            DisplayItemList();

            tvFileName.setPaintFlags(tvFileName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            spnrSalesTax.setSelection(0);
            spnrAdditionalTax.setSelection(1);
            /*spnrSalesTax.setEnabled(false);
            spnrAdditionalTax.setEnabled(false);*/

        } catch (Exception exp) {
            Toast.makeText(myContext, "OnCreate:" + exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void SetGSTView()
    {
        Cursor crsrSettings = dbItems.getBillSetting();

        if (crsrSettings.moveToFirst()) {
            String GSTEnable = crsrSettings.getString(crsrSettings.getColumnIndex("GSTEnable"));
            if(GSTEnable.equals("0"))
            {
                // GST disabled
                tvCaptionRate.setVisibility(View.GONE);
                tvCaptionQuanity.setVisibility(View.GONE);
                tvCaptionMOU.setVisibility(View.GONE);
                tvCaptionGSTRate.setVisibility(View.GONE);
                tvCaptionDineInPrice1.setVisibility(View.VISIBLE);
                tvCaptionDineInPrice2.setVisibility(View.VISIBLE);
                tvCaptionDineInPrice3.setVisibility(View.VISIBLE);
                tvCaptionStock.setVisibility(View.VISIBLE);
                frame_salesTax.setVisibility(View.VISIBLE);
                frame_serviceTax.setVisibility(View.VISIBLE);
                frame_GSTTax.setVisibility(View.GONE);
                /*FrameLayout  fr_g_s = (FrameLayout) findViewById(R.id.frame_g_s);
                fr_g_s.setVisibility(View.GONE);
                FrameLayout  fr_rate = (FrameLayout) findViewById(R.id.frame_rate);
                fr_rate.setVisibility(View.GONE);
                FrameLayout  fr_quantity = (FrameLayout) findViewById(R.id.frame_quantity);
                fr_quantity.setVisibility(View.GONE);
                FrameLayout  fr_MOU = (FrameLayout) findViewById(R.id.frame_mou);
                fr_MOU.setVisibility(View.GONE);
                FrameLayout  fr_taxationtype = (FrameLayout) findViewById(R.id.frame_mou);
                fr_MOU.setVisibility(View.GONE);
                FrameLayout  fr_HSNCode_out = (FrameLayout) findViewById(R.id.frame_HSNCode);
                fr_HSNCode_out.setVisibility(View.INVISIBLE);
                FrameLayout  fr_GSTTAX = (FrameLayout) findViewById(R.id.frame_GSTTax);
                fr_GSTTAX.setVisibility(View.GONE);
                FrameLayout  fr_discount = (FrameLayout) findViewById(R.id.frame_discount);
                fr_discount.setVisibility(View.GONE); */

                frame_rate_gst.setVisibility(View.GONE);
                frame_rate_nongst.setVisibility(View.VISIBLE);
                frame_salesTax.setVisibility(View.VISIBLE);
                frame_serviceTax.setVisibility(View.VISIBLE);
                frame_GSTTax.setVisibility(View.GONE);


            }
            else
            {   // GST Enabled
                tvCaptionRate.setVisibility(View.VISIBLE);
                tvCaptionQuanity.setVisibility(View.VISIBLE);
                tvCaptionMOU.setVisibility(View.VISIBLE);
                tvCaptionGSTRate.setVisibility(View.VISIBLE);
                tvCaptionDineInPrice1.setVisibility(View.GONE);
                tvCaptionDineInPrice2.setVisibility(View.GONE);
                tvCaptionDineInPrice3.setVisibility(View.GONE);
                tvCaptionStock.setVisibility(View.GONE);
                frame_salesTax.setVisibility(View.GONE);
                frame_serviceTax.setVisibility(View.GONE);
                frame_GSTTax.setVisibility(View.VISIBLE);
                /*FrameLayout  fr_rate1 = (FrameLayout) findViewById(R.id.frame_rate1);
                fr_rate1.setVisibility(View.GONE);
                FrameLayout  fr_rate2 = (FrameLayout) findViewById(R.id.frame_rate2);
                fr_rate2.setVisibility(View.GONE);
                FrameLayout  fr_rate3 = (FrameLayout) findViewById(R.id.frame_rate3);
                fr_rate3.setVisibility(View.GONE);
                FrameLayout  fr_stock = (FrameLayout) findViewById(R.id.frame_stock);
                fr_stock.setVisibility(View.GONE);*/

                frame_rate_gst.setVisibility(View.VISIBLE);
                frame_rate_nongst.setVisibility(View.GONE);
                frame_salesTax.setVisibility(View.GONE);
                frame_serviceTax.setVisibility(View.GONE);
                frame_GSTTax.setVisibility(View.VISIBLE);
                if(HSNEnable_out == null ) {
                    FrameLayout fr_HSNCode_out = (FrameLayout) findViewById(R.id.frame_HSNCode);
                    fr_HSNCode_out.setVisibility(View.INVISIBLE);
                }
                else if(HSNEnable_out.equalsIgnoreCase("0") ) {
                    FrameLayout fr_HSNCode_out = (FrameLayout) findViewById(R.id.frame_HSNCode);
                    fr_HSNCode_out.setVisibility(View.INVISIBLE);
                }
                else {
                    FrameLayout fr_HSNCode_out = (FrameLayout) findViewById(R.id.frame_HSNCode);
                    fr_HSNCode_out.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void InitializeViewVariables() {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();

        txtLongName = (EditText) findViewById(R.id.etItemLongName);
        txtBarcode = (EditText) findViewById(R.id.etItemBarcode);
        txtDineIn1 = (EditText) findViewById(R.id.etItemDineInPrice1);
        etInputValidate.ValidateDecimalInput(txtDineIn1);
        txtDineIn2 = (EditText) findViewById(R.id.etItemDineInPrice2);
        etInputValidate.ValidateDecimalInput(txtDineIn2);
        txtDineIn3 = (EditText) findViewById(R.id.etItemDineInPrice3);
        etInputValidate.ValidateDecimalInput(txtDineIn3);
        txtStock = (EditText) findViewById(R.id.etItemStock);
        tvFileName = (TextView) findViewById(R.id.tvFileName);

        tvDineIn1Caption = (TextView) findViewById(R.id.tvDineInPrice1);
        tvDineIn2Caption = (TextView) findViewById(R.id.tvDineInPrice2);
        tvDineIn3Caption = (TextView) findViewById(R.id.tvDineInPrice3);

        spnrDepartment = (Spinner) findViewById(R.id.spnrItemDeptCode);
        spnrDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (ROWCLICKEVENT == 0)
                {
                    if(!spnrDepartment.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                        int deptid = dbItems.getDepartmentIdByName(labelsDept.get(spnrDepartment.getSelectedItemPosition()));
                        ArrayList<String> categName = dbItems.getCategoryNameByDeptCode(String.valueOf(deptid));
                    /*int categid = getIndexCateg(categName + "");
                    spnrCategory.setSelection(categid);*/
                        loadSpinnerData_cat(categName);
                    }
                    else
                    {
                        loadSpinnerData1();
                    }
                }
                else
                {
                    // setting it to 0;
                    ROWCLICKEVENT =0;
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        spnrCategory = (Spinner) findViewById(R.id.spnrItemCategCode);
        spnrCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int p = position;
                String item = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnrKitchen = (Spinner) findViewById(R.id.spnrItemKitchenCode);
        spnrSalesTax = (Spinner) findViewById(R.id.spnrItemSalesTax);
        spnrAdditionalTax = (Spinner) findViewById(R.id.spnrItemAdditionalTax);
        spnrOptionalTax1 = (Spinner) findViewById(R.id.spnrItemOptionalTax1);
        spnrOptionalTax2 = (Spinner) findViewById(R.id.spnrItemOptionalTax2);
        spnrDiscount = (Spinner) findViewById(R.id.spnrItemDiscount);

        //chkPriceChange = (CheckBox) findViewById(R.id.chkPriceChange);
        //chkDiscountEnable = (CheckBox) findViewById(R.id.chkDiscount);
        //chkBillWithStock = (CheckBox) findViewById(R.id.chkBillwithStock);

        rbForwardTax = (RadioButton) findViewById(R.id.rbForwardTax);
        rbReverseTax =
                (RadioButton) findViewById(R.id.rbReverseTax);

        btnAdd = (WepButton) findViewById(R.id.btnAddItem);
        btnEdit = (WepButton) findViewById(R.id.btnEditItem);

        tblItems = (TableLayout) findViewById(R.id.tblItem);

        imgItemImage = (ImageView) findViewById(R.id.imgItemImage);
        imgItemImage.setImageResource(R.drawable.img_noimage);


        tvCaptionSNo = (TextView) findViewById(R.id.tvCaptionSNo);
        tvCaptionLongName = (TextView) findViewById(R.id.tvCaptionLongName);
        tvCaptionDineInPrice1  = (TextView) findViewById(R.id.tvCaptionDineInPrice1);
        tvCaptionDineInPrice2= (TextView) findViewById(R.id.tvCaptionDineInPrice2);
        tvCaptionDineInPrice3 = (TextView) findViewById(R.id.tvCaptionDineInPrice3);
        tvCaptionStock = (TextView) findViewById(R.id.tvCaptionStock);
        tvCaptionRate =  (TextView) findViewById(R.id.tvCaptionRate);
        tvCaptionQuanity = (TextView) findViewById(R.id.tvCaptionQuanity);
        tvCaptionMOU = (TextView) findViewById(R.id.tvCaptionMOU);
        tvCaptionGSTRate  = (TextView) findViewById(R.id.tvCaptionGSTRate);
        tvCaptionImage = (TextView) findViewById(R.id.tvCaptionImage);
        tvCaptionDelete =  (TextView) findViewById(R.id.tvCaptionDelete);

        spnrG_S = (Spinner) findViewById(R.id.spnr_g_s);
        spnrMOU = (Spinner) findViewById(R.id.spnr_UOM);
        spnrtaxationtype = (Spinner) findViewById(R.id.spnr_taxationtype);
        etRate = (EditText) findViewById(R.id.etRate);
        etQuantity = (EditText) findViewById(R.id.et_quantity);
        etHSN = (EditText) findViewById(R.id.etHSNCode);
        etGstTax = (EditText)findViewById(R.id.etGSTTax);
        etDiscount = (EditText)findViewById(R.id.etdiscount);

        frame_rate_gst = (FrameLayout) findViewById(R.id.frame_rate_gst);
        frame_rate_nongst  = (FrameLayout) findViewById(R.id.frame_rate_nongst);
        frame_salesTax = (FrameLayout) findViewById(R.id.frame_salesTax);
        frame_serviceTax = (FrameLayout) findViewById(R.id.frame_serviceTax);
        frame_GSTTax = (FrameLayout) findViewById(R.id.frame_GSTTax);

        edtMenuCode = (EditText) findViewById(R.id.edtMenuCode);
        edtItemSalesTax = (EditText) findViewById(R.id.edtItemSalesTax);
        edtItemServiceTax = (EditText) findViewById(R.id.edtItemServiceTax);

        listViewItems = (ListView) findViewById(R.id.listViewItems);
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemClickEvent(itemListAdapter.getItems(position));
            }});
    }

    private void listItemClickEvent(ItemOutward item) {
        edtMenuCode.setText(String.valueOf(item.getMenuCode()));
        txtLongName.setText(item.getLongName());

        txtBarcode.setText(item.getBarCode());
        txtDineIn1.setText(String.valueOf(item.getDineIn1()));
        txtDineIn2.setText(String.valueOf(item.getDineIn2()));
        txtDineIn3.setText(String.valueOf(item.getDineIn3()));
        txtStock.setText(String.valueOf(item.getStock()));

        strImageUri = item.getImageUri();

        Cursor crsrDept = dbItems.getDepartment(Integer.valueOf(item.getDeptCode()));
        String deptName = "Select";
        int deptid = 0;
        if (crsrDept.moveToFirst()) {
            deptName = crsrDept.getString(crsrDept.getColumnIndex("DeptName"));
            deptid = getIndexDept(deptName + "");
            spnrDepartment.setSelection(deptid);
        } else {

            deptid = getIndexDept(deptName + "");
            spnrDepartment.setSelection(deptid);
        }

        ArrayList<String> listCateg = dbItems.getCategoryNameByDeptCode(String.valueOf(deptid));
        loadSpinnerData_cat(listCateg);
        if (deptid ==0) {
            spnrCategory.setSelection(0);
        }else {
            Cursor crsrCateg = dbItems.getCategory(Integer.valueOf(item.getCategCode()));
            String categName = "";
            if (crsrCateg.moveToFirst()) {
                categName = crsrCateg.getString(crsrCateg.getColumnIndex("CategName"));
            }
            boolean bool = false;
            int i =0;
            for( i =0; !categName.equals("")&&bool == false && i<listCateg.size();i++)
            {
                if(categName.equalsIgnoreCase(listCateg.get(i)))
                    bool = true;
            }
            if(bool) // true
            {
                spnrCategory.setSelection(i-1);
            }
            else
                spnrCategory.setSelection(0);
        }

        spnrKitchen.setSelection((item.getKitchenCode()) );


        imgItemImage.setImageURI(null);
        if (!strImageUri.equalsIgnoreCase("")) { // &&
            // strImageUri.contains("\\")){
            imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
        } else {
            imgItemImage.setImageResource(R.drawable.img_noimage);
        }
        edtItemSalesTax.setText(String.format("%.2f",item.getSalesTaxPercent()));
        edtItemServiceTax.setText(String.format("%.2f",item.getServiceTaxPercent()));

        String uom_temp = item.getUOM();
        String uom = "("+uom_temp+")";
        int index = getIndex(uom);
        spnrMOU.setSelection(index);
        strItemId = String.valueOf(item.getItemId());
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(true);


        //spinnerRole
    }
    private void InitializeAdapters() {
        Log.d("Functions", "InitializeAdapters");
        // Cursor variable
        Cursor crsrAdapterData;

        // Initialize adapters
        adapDeptCode = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapDeptCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapCategCode = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapCategCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapKitCode = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapKitCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapTax = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapTax.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapDiscount = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapDiscount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        supplytypeAdapter = ArrayAdapter.createFromResource(this, R.array.g_s, android.R.layout.simple_spinner_item);
        supplytypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrG_S.setAdapter(supplytypeAdapter);

        MOUAdapter = ArrayAdapter.createFromResource(this, R.array.UOM, android.R.layout.simple_spinner_item);
        MOUAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrMOU.setAdapter(MOUAdapter);

        taxationtypeAdapter = ArrayAdapter.createFromResource(this, R.array.taxationtype, android.R.layout.simple_spinner_item);
        taxationtypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrtaxationtype.setAdapter(taxationtypeAdapter);

        // Assign adapters to spinners
        spnrDepartment.setAdapter(adapDeptCode);
        //spnrCategory.setAdapter(adapCategCode);
        spnrKitchen.setAdapter(adapKitCode);
        spnrSalesTax.setAdapter(adapTax);
        spnrAdditionalTax.setAdapter(adapTax);
        spnrOptionalTax1.setAdapter(adapTax);
        spnrOptionalTax2.setAdapter(adapTax);
        spnrDiscount.setAdapter(adapDiscount);

        // Add Kitchen to adapter
        crsrAdapterData = null;
        crsrAdapterData = dbItems.getAllKitchen();
        Log.d("Kitchen", "Rows:" + String.valueOf(crsrAdapterData.getCount()));
        if (crsrAdapterData.moveToFirst()) {
            adapKitCode.add("Select");
            do {
                adapKitCode.add(crsrAdapterData.getString(1));
            } while (crsrAdapterData.moveToNext());
        }else
        {
            adapKitCode.add("Select");
        }

        // Add Tax to adapter
        crsrAdapterData = null;
        crsrAdapterData = dbItems.getAllTaxConfig();
        Log.d("Tax", "Rows:" + String.valueOf(crsrAdapterData.getCount()));
        if (crsrAdapterData.moveToFirst()) {
            do {
                adapTax.add(crsrAdapterData.getString(1));
            } while (crsrAdapterData.moveToNext());
        }

        // Add Tax to adapter
        crsrAdapterData = null;
        crsrAdapterData = dbItems.getAllDiscountConfig();
        Log.d("Discount", "Rows:" + String.valueOf(crsrAdapterData.getCount()));
        if (crsrAdapterData.moveToFirst()) {
            do {
                adapDiscount.add(crsrAdapterData.getString(1));
            } while (crsrAdapterData.moveToNext());
        }
    }


    void DisplayItemList()
    {
        dataList = new ArrayList<ItemOutward>();
        Cursor cursorItem = dbItems.getAllItems();

        while(cursorItem!=null && cursorItem.moveToNext())
        {
            ItemOutward item = new ItemOutward();
            item.setMenuCode(cursorItem.getInt(cursorItem.getColumnIndex("MenuCode")));
            item.setLongName(cursorItem.getString(cursorItem.getColumnIndex("ItemName")));
            item.setDineIn1(cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice1")));
            item.setDineIn2(cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice2")));
            item.setDineIn3(cursorItem.getFloat(cursorItem.getColumnIndex("DineInPrice3")));
            item.setStock(cursorItem.getFloat(cursorItem.getColumnIndex("Quantity")));
            item.setDeptCode(cursorItem.getInt(cursorItem.getColumnIndex("DeptCode")));
            item.setCategCode(cursorItem.getInt(cursorItem.getColumnIndex("CategCode")));
            item.setKitchenCode(cursorItem.getInt(cursorItem.getColumnIndex("KitchenCode")));
            item.setBarCode(cursorItem.getString(cursorItem.getColumnIndex("ItemBarcode")));
            item.setImageUri(cursorItem.getString(cursorItem.getColumnIndex("ImageUri")));
            item.setUOM(cursorItem.getString(cursorItem.getColumnIndex("UOM")));
            item.setSalesTaxPercent(cursorItem.getFloat(cursorItem.getColumnIndex("SalesTaxPercent")));
            item.setServiceTaxPercent(cursorItem.getFloat(cursorItem.getColumnIndex("ServiceTaxPercent")));
            item.setItemId(cursorItem.getInt(cursorItem.getColumnIndex("ItemId")));

            dataList.add(item);
        }
        if (itemListAdapter == null) {
            itemListAdapter = new ItemOutwardAdapter(ItemManagementActivity.this, dataList,dbItems);
            listViewItems.setAdapter(itemListAdapter);
        } else {
            itemListAdapter.notifyNewDataAdded(dataList);
        }
    }

    @SuppressWarnings("deprecation")
    private void DisplayItems() {
        Cursor crsrItems = null;

        crsrItems = dbItems.getAllItems();
        //crsrItems = dbItems.getAllItemswithDeptCategName();
        TableRow rowItems = null;

        TextView tvSno, tvMenuCode, tvLongName, tvShortName, tvDineIn1, tvDineIn2, tvDineIn3, tvTakeAway, tvPickUp, tvDelivery,
                tvStock, tvPriceChange, tvDiscountEnable, tvBillWithStock, tvTaxType, tvDeptCode, tvCategCode,
                tvKitchenCode, tvSalesTaxId, tvAdditionalTaxId, tvOptionalTaxId1, tvOptionalTaxId2, tvDiscountId,
                tvItemBarcode, tvImageUri, tvSpace, tvDeptName, tvCategName, tvItemId;
        TextView tvHSNCode_out,tvMOU,tvRate,tvGSTRate,tvSupplyType, tvSalesTaxPercent, tvServiceTaxPercent;
        ImageView imgIcon;
        ImageButton btnItemDelete;
        int i = 1;

        if (crsrItems.moveToFirst()) {
            do {
                rowItems = new TableRow(myContext);
                rowItems.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                rowItems.setBackgroundResource(R.drawable.border_itemdatabase);

                tvSno = new TextView(myContext);
                tvSno.setTextSize(15);
                tvSno.setGravity(1);
                tvSno.setWidth(105);
                tvSno.setTextColor(getResources().getColor(R.color.black));
                tvSno.setText(String.valueOf(i));
                //rowItems.addView(tvSno);

                tvMenuCode = new TextView(myContext);
                tvMenuCode.setTextSize(18);
                tvMenuCode.setGravity(1);
                tvMenuCode.setWidth(160);
                tvMenuCode.setText(crsrItems.getString(crsrItems.getColumnIndex("MenuCode")));
                //rowItems.addView(tvMenuCode);

                tvLongName = new TextView(myContext);
                tvLongName.setTextSize(15);
                tvLongName.setWidth(360);
                //tvLongName.setTypeface(Typeface.DEFAULT_BOLD);
                tvLongName.setTextColor(getResources().getColor(R.color.black));
                tvLongName.setText(crsrItems.getString(crsrItems.getColumnIndex("ItemName")));
                //rowItems.addView(tvLongName);



                tvDineIn1 = new TextView(myContext);
                //tvDineIn1.setGravity(1);
                tvDineIn1.setTextSize(15);
                tvDineIn1.setGravity(1);
                tvDineIn1.setWidth(110);
                //tvDineIn1.setBackgroundResource(R.drawable.border);
                tvDineIn1.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice1")));
                //rowItems.addView(tvDineIn1);

                tvDineIn2 = new TextView(myContext);
                tvDineIn2.setGravity(1);
                tvDineIn2.setTextSize(15);
                tvDineIn2.setWidth(110);
                tvDineIn2.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice2")));
                //rowItems.addView(tvDineIn2);

                tvDineIn3 = new TextView(myContext);
                tvDineIn3.setGravity(1);
                tvDineIn3.setWidth(110);
                tvDineIn3.setTextSize(15);
                tvDineIn3.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice3")));
                //rowItems.addView(tvDineIn3);



                tvStock = new TextView(myContext);
                tvStock.setGravity(1);
                tvStock.setWidth(110);
                tvStock.setTextSize(15);
                //tvStock.setTypeface(Typeface.DEFAULT_BOLD);
                tvStock.setTextColor(getResources().getColor(R.color.black));
                //tvStock.setBackgroundResource(R.drawable.border);
                tvStock.setText(crsrItems.getString(crsrItems.getColumnIndex("Quantity")));
                //rowItems.addView(tvStock);





                tvDeptCode = new TextView(myContext);
                tvDeptCode.setText(crsrItems.getString(crsrItems.getColumnIndex("DeptCode")));
                //rowItems.addView(tvDeptCode);

                tvCategCode = new TextView(myContext);
                tvCategCode.setText(crsrItems.getString(crsrItems.getColumnIndex("CategCode")));
                //rowItems.addView(tvCategCode);

                tvKitchenCode = new TextView(myContext);
                tvKitchenCode.setText(crsrItems.getString(crsrItems.getColumnIndex("KitchenCode")));
                //rowItems.addView(tvKitchenCode);



                tvItemBarcode = new TextView(myContext);
                tvItemBarcode.setText(crsrItems.getString(crsrItems.getColumnIndex("ItemBarcode")));
                //rowItems.addView(tvItemBarcode);

                tvImageUri = new TextView(myContext);
                tvImageUri.setText(crsrItems.getString(crsrItems.getColumnIndex("ImageUri")));
                //rowItems.addView(tvImageUri);

                imgIcon = new ImageView(myContext);
                TableRow.LayoutParams rowparams = new TableRow.LayoutParams(60, 40);
                rowparams.gravity = Gravity.CENTER;
                imgIcon.setLayoutParams(rowparams);
                imgIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imgIcon.setImageURI(null);
                // imgIcon.setBackgroundResource(R.drawable.border);
                if (!crsrItems.getString(crsrItems.getColumnIndex("ImageUri")).equalsIgnoreCase("")) { // &&
                    // strImageUri.contains("\\")){
                    imgIcon.setImageURI(Uri.fromFile(new File(crsrItems.getString(crsrItems.getColumnIndex("ImageUri")))));
                } else {
                    imgIcon.setImageResource(R.drawable.img_noimage);
                }
                //rowItems.addView(imgIcon);

                tvSupplyType = new TextView(myContext);
                tvSupplyType.setGravity(1);
                tvSupplyType.setWidth(110);
                tvSupplyType.setText(crsrItems.getString(crsrItems.getColumnIndex("SupplyType")));
                //rowItems.addView(tvSupplyType);


                tvMOU = new TextView(myContext);
                tvMOU.setGravity(Gravity.RIGHT|Gravity.END);
                tvMOU.setWidth(120);
                tvMOU.setTextSize(15);
                //tvMOU.setTypeface(Typeface.DEFAULT_BOLD);
                tvMOU.setTextColor(getResources().getColor(R.color.black));
                //tvMOU.setBackgroundResource(R.drawable.border);
                tvMOU.setText(crsrItems.getString(crsrItems.getColumnIndex("UOM")));
                //rowItems.addView(tvMOU);



                // For Space purpose
                tvSpace = new TextView(myContext);
                tvSpace.setWidth(49);
                tvSpace.setText("");
                //tvSpace.setBackgroundResource(R.drawable.border);
                //rowItems.addView(tvSpace);

                TextView tvSpace1 = new TextView(myContext);
                tvSpace1.setText("");
                tvSpace1.setWidth(22);

                // Delete
                int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                btnItemDelete = new ImageButton(myContext);

                TableRow.LayoutParams rowparams1 = new TableRow.LayoutParams(40, 35);
                rowparams1.gravity = Gravity.CENTER;
                btnItemDelete.setBackground(getResources().getDrawable(R.drawable.delete_icon_border));
                //btnItemDelete.setLayoutParams(rowparams1);
                btnItemDelete.setLayoutParams(new TableRow.LayoutParams(40, 35));
                //btnItemDelete.setOnClickListener(mListener);




                TextView tvTaxationType = new TextView(myContext);
                tvTaxationType.setText(spnrtaxationtype.getSelectedItem().toString());

                tvItemId = new TextView(myContext);
                tvItemId.setText(crsrItems.getString(crsrItems.getColumnIndex("ItemId")));

                tvSalesTaxPercent = new TextView(myContext);
                tvSalesTaxPercent.setText(crsrItems.getString(crsrItems.getColumnIndex("SalesTaxPercent")));

                tvServiceTaxPercent = new TextView(myContext);
                tvServiceTaxPercent.setText(crsrItems.getString(crsrItems.getColumnIndex("ServiceTaxPercent")));

                if(GSTEnable.equals("0")) {

                    rowItems.addView(tvSno);//0
                    rowItems.addView(tvMenuCode);//1
                    rowItems.addView(tvLongName);//2

                    rowItems.addView(tvDineIn1);//3
                    rowItems.addView(tvDineIn2);//4
                    rowItems.addView(tvDineIn3);//5

                    rowItems.addView(tvStock);//6

                    rowItems.addView(tvDeptCode);//7
                    rowItems.addView(tvCategCode);//8
                    rowItems.addView(tvKitchenCode);//9

                    rowItems.addView(tvItemBarcode);//10
                    rowItems.addView(tvImageUri);//11
                    rowItems.addView(tvSpace1);//12
                    rowItems.addView(imgIcon);//13
                    rowItems.addView(tvSpace);//14
                    rowItems.addView(btnItemDelete);//15


                    rowItems.addView(tvItemId);//16
                    rowItems.addView(tvSalesTaxPercent);//17
                    rowItems.addView(tvServiceTaxPercent);//18
                    rowItems.addView(tvMOU);//19

                    rowItems.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            if (String.valueOf(v.getTag()) == "TAG") {
                                TableRow Row = (TableRow) v;
                                ROWCLICKEVENT = 1;
                                TextView MenuCode = (TextView) Row.getChildAt(1);
                                TextView LongName = (TextView) Row.getChildAt(2);
                                TextView DineIn1 = (TextView) Row.getChildAt(3);
                                TextView DineIn2 = (TextView) Row.getChildAt(4);
                                TextView DineIn3 = (TextView) Row.getChildAt(5);
                                TextView Stock = (TextView) Row.getChildAt(6);
                                TextView DeptCode = (TextView) Row.getChildAt(7);
                                TextView CategCode = (TextView) Row.getChildAt(8);
                                TextView KitchenCode = (TextView) Row.getChildAt(9);
                                TextView Barcode = (TextView) Row.getChildAt(10);
                                TextView ImageUri = (TextView) Row.getChildAt(11);
                                TextView ItemId = (TextView) Row.getChildAt(16);
                                TextView SalesTaxPercent = (TextView) Row.getChildAt(17);
                                TextView ServiceTaxPercent = (TextView) Row.getChildAt(18);
                                TextView MOU = (TextView) Row.getChildAt(19);

                                strItemId = ItemId.getText().toString();
                                edtMenuCode.setText(MenuCode.getText().toString());
                                txtLongName.setText(LongName.getText());
                                //txtShortName.setText(ShortName.getText());
                                txtBarcode.setText(Barcode.getText());
                                txtDineIn1.setText(DineIn1.getText());
                                txtDineIn2.setText(DineIn2.getText());
                                txtDineIn3.setText(DineIn3.getText());
                                txtStock.setText(Stock.getText());
                                strImageUri = ImageUri.getText().toString();

                                Cursor crsrDept = dbItems.getDepartment(Integer.valueOf(DeptCode.getText().toString()));
                                String deptName = "Select";
                                int deptid = 0;
                                if (crsrDept.moveToFirst()) {
                                    deptName = crsrDept.getString(crsrDept.getColumnIndex("DeptName"));
                                    deptid = getIndexDept(deptName + "");
                                    spnrDepartment.setSelection(deptid);
                                } else {

                                    deptid = getIndexDept(deptName + "");
                                    spnrDepartment.setSelection(deptid);
                                }
                                //spnrDepartment.setSelection(Integer.parseInt(DeptCode.getText().toString()));
                                ArrayList<String> listCateg = dbItems.getCategoryNameByDeptCode(String.valueOf(deptid));
                                loadSpinnerData_cat(listCateg);
                                int  deptCode =Integer.parseInt(DeptCode.getText().toString());
                                if (deptCode ==0) {
                                    spnrCategory.setSelection(0);
                                }else {
                                    Cursor crsrCateg = dbItems.getCategory(Integer.valueOf(CategCode.getText().toString()));
                                    String categName = "";
                                    if (crsrCateg.moveToFirst()) {
                                        categName = crsrCateg.getString(crsrCateg.getColumnIndex("CategName"));
                                    }
                                    boolean bool = false;
                                    int i =0;
                                    for( i =0; !categName.equals("")&&bool == false && i<listCateg.size();i++)
                                    {
                                        if(categName.equalsIgnoreCase(listCateg.get(i)))
                                            bool = true;
                                    }
                                    /*ArrayAdapter<String> dataAdapter = (ArrayAdapter<String>) spnrCategory.getAdapter();
                                    int spinnerPosition = dataAdapter.getPosition(categName);*/
                                    if(bool) // true
                                    {
                                        spnrCategory.setSelection(i-1);
                                    }
                                    else
                                        spnrCategory.setSelection(0);
                                }




                                spnrKitchen.setSelection(Integer.parseInt(KitchenCode.getText().toString()) );


                                imgItemImage.setImageURI(null);
                                if (!strImageUri.equalsIgnoreCase("")) { // &&
                                    // strImageUri.contains("\\")){
                                    imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
                                } else {
                                    imgItemImage.setImageResource(R.drawable.img_noimage);
                                }
                                edtItemSalesTax.setText(SalesTaxPercent.getText().toString());
                                edtItemServiceTax.setText(ServiceTaxPercent.getText().toString());
                                String uom_temp = MOU.getText().toString();
                                String uom = "("+uom_temp+")";
                                int index = getIndex(uom);
                                spnrMOU.setSelection(index);
                                btnAdd.setEnabled(false);
                                btnEdit.setEnabled(true);
                            }
                        }
                    });

                    rowItems.setTag("TAG");

                    tblItems.addView(rowItems, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    i++;
                }
                else
                {
                    // gst enabled

                    /*rowItems.addView(tvSno);
                    rowItems.addView(tvMenuCode);
                    rowItems.addView(tvLongName);
                    rowItems.addView(tvShortName);
                    rowItems.addView(tvRate);
                    rowItems.addView(tvStock);
                    rowItems.addView(tvMOU);
                    rowItems.addView(tvTakeAway);
                    rowItems.addView(tvPickUp);
                    rowItems.addView(tvDelivery);
                    rowItems.addView(tvGSTRate);
                    rowItems.addView(tvPriceChange);
                    rowItems.addView(tvDiscountEnable);
                    rowItems.addView(tvBillWithStock);
                    rowItems.addView(tvTaxType);
                    rowItems.addView(tvDeptCode);
                    rowItems.addView(tvCategCode);
                    rowItems.addView(tvKitchenCode);
                    rowItems.addView(tvSalesTaxId);
                    rowItems.addView(tvAdditionalTaxId);
                    rowItems.addView(tvOptionalTaxId1);
                    rowItems.addView(tvOptionalTaxId2);
                    rowItems.addView(tvDiscountId);
                    rowItems.addView(tvItemBarcode);
                    rowItems.addView(tvImageUri);//24
                    rowItems.addView(tvSpace1);//25
                    rowItems.addView(imgIcon);//26
                    rowItems.addView(tvSpace);//27
                    rowItems.addView(btnItemDelete);//28
                    rowItems.addView(tvHSNCode_out);//29
                    rowItems.addView(tvSupplyType);
                    rowItems.addView(tvTaxationType);

                    // For Space purpose
                    tvSpace = new TextView(myContext);
                    tvSpace.setText("              ");
                    rowItems.addView(tvSpace);
                    rowItems.addView(tvItemId);//33
                    rowItems.addView(tvSalesTaxPercent);//34
                    rowItems.addView(tvServiceTaxPercent);//35

                    rowItems.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            if (String.valueOf(v.getTag()) == "TAG") {
                                TableRow Row = (TableRow) v;

                                TextView MenuCode = (TextView) Row.getChildAt(1);
                                TextView LongName = (TextView) Row.getChildAt(2);
                                TextView ShortName = (TextView) Row.getChildAt(3);
                                TextView Rate = (TextView) Row.getChildAt(4);
                                TextView Quantity = (TextView) Row.getChildAt(5);
                                TextView MOU = (TextView) Row.getChildAt(6);
                                TextView TakeAway = (TextView) Row.getChildAt(7);
                                TextView PickUp = (TextView) Row.getChildAt(8);
                                TextView Delivery = (TextView) Row.getChildAt(9);
                                TextView GSTRate = (TextView) Row.getChildAt(10);
                                TextView PriceChange = (TextView) Row.getChildAt(11);
                                TextView DiscountEnable = (TextView) Row.getChildAt(12);
                                TextView BillWithStock = (TextView) Row.getChildAt(13);
                                TextView TaxType = (TextView) Row.getChildAt(14);
                                TextView DeptCode = (TextView) Row.getChildAt(15);
                                TextView CategCode = (TextView) Row.getChildAt(16);
                                TextView KitchenCode = (TextView) Row.getChildAt(17);
                                TextView SalesTaxId = (TextView) Row.getChildAt(18);
                                TextView AdditionalTaxId = (TextView) Row.getChildAt(19);
                                TextView OptionalTaxId1 = (TextView) Row.getChildAt(20);
                                TextView OptionalTaxId2 = (TextView) Row.getChildAt(21);
                                TextView DiscountId = (TextView) Row.getChildAt(22);
                                TextView Barcode = (TextView) Row.getChildAt(23);
                                TextView ImageUri = (TextView) Row.getChildAt(24);
                                TextView SupplyType = (TextView) Row.getChildAt(30);
                                TextView HSNCode_out = (TextView) Row.getChildAt(29);
                                TextView taxationtype =  (TextView) Row.getChildAt(31);
                                TextView ItemId = (TextView) Row.getChildAt(33);
                                TextView SalesTaxPercent = (TextView) Row.getChildAt(34);
                                TextView ServiceTaxPercent = (TextView) Row.getChildAt(35);

                                strItemId = ItemId.getText().toString();
                                edtMenuCode.setText(MenuCode.getText());
                                txtLongName.setText(LongName.getText());
                                //txtShortName.setText(ShortName.getText());
                                txtBarcode.setText(Barcode.getText());
                                *//*txtDineIn1.setText(DineIn1.getText());
                                txtDineIn2.setText(DineIn2.getText());
                                txtDineIn3.setText(DineIn3.getText());
                                txtStock.setText(Stock.getText());*//*
                                strImageUri = ImageUri.getText().toString();

                                // gst
                                etGstTax.setText(GSTRate.getText());
                                etRate.setText(Rate.getText());
                                etQuantity.setText(Quantity.getText());
                                etHSN.setText(HSNCode_out.getText());
                                etDiscount.setText(DiscountId.getText());
                                // richa to do
                                spnrG_S.setSelection(supplytypeAdapter.getPosition(SupplyType.getText().toString()));
                                spnrMOU.setSelection(MOUAdapter.getPosition(MOU.getText().toString()));
                                spnrtaxationtype.setSelection(taxationtypeAdapter.getPosition(taxationtype.getText().toString()));

                                // gst end
                                Cursor crsrDept = dbItems.getDepartment(Integer.valueOf(DeptCode.getText().toString()));
                                if(crsrDept.moveToFirst()) {
                                    String deptName = crsrDept.getString(crsrDept.getColumnIndex("DeptName"));
                                    int deptid = getIndexDept(deptName + "");
                                    spnrDepartment.setSelection(deptid);
                                } else {
                                    String deptName = "Select";
                                    int deptid = getIndexDept(deptName + "");
                                    spnrDepartment.setSelection(deptid);
                                }
                                //spnrDepartment.setSelection(Integer.parseInt(DeptCode.getText().toString()));
                                Cursor crsrCateg = dbItems.getCategory(Integer.valueOf(CategCode.getText().toString()));
                                if(crsrCateg.moveToFirst()) {
                                    String categName = crsrCateg.getString(crsrCateg.getColumnIndex("CategName"));
                                    int categid = getIndexCateg(categName + "");
                                    spnrCategory.setSelection(categid);
                                } else {
                                    String categName = "Select";
                                    int categid = getIndexCateg(categName + "");
                                    spnrCategory.setSelection(categid);
                                }
                                //spnrCategory.setSelection(Integer.parseInt(CategCode.getText().toString()));
                                spnrKitchen.setSelection(Integer.parseInt(KitchenCode.getText().toString()) - 1);
                                spnrSalesTax.setSelection(Integer.parseInt(SalesTaxId.getText().toString()) - 1);
                                spnrAdditionalTax.setSelection(Integer.parseInt(AdditionalTaxId.getText().toString()) - 1);
                                spnrOptionalTax1.setSelection(Integer.parseInt(OptionalTaxId1.getText().toString()) - 1);
                                spnrOptionalTax2.setSelection(Integer.parseInt(OptionalTaxId2.getText().toString()) - 1);
                                spnrDiscount.setSelection(Integer.parseInt(DiscountId.getText().toString()) - 1);

                                if (PriceChange.getText().toString().equalsIgnoreCase("Yes")) {
                                    chkPriceChange.setChecked(true);
                                } else {
                                    chkPriceChange.setChecked(false);
                                }
                                if (DiscountEnable.getText().toString().equalsIgnoreCase("Yes")) {
                                    chkDiscountEnable.setChecked(true);
                                } else {
                                    chkDiscountEnable.setChecked(false);
                                }
                                if (BillWithStock.getText().toString().equalsIgnoreCase("Yes")) {
                                    chkBillWithStock.setChecked(true);
                                } else {
                                    chkBillWithStock.setChecked(false);
                                }

                                if (TaxType.getText().toString().equalsIgnoreCase("Forward Tax")) {
                                    rbForwardTax.setChecked(true);
                                } else if (TaxType.getText().toString().equalsIgnoreCase("Reverse Tax")) {
                                    rbReverseTax.setChecked(true);
                                }

                                imgItemImage.setImageURI(null);
                                if (!strImageUri.equalsIgnoreCase("")) { // &&
                                    // strImageUri.contains("\\")){
                                    imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
                                } else {
                                    imgItemImage.setImageResource(R.drawable.img_noimage);
                                }
                                edtItemSalesTax.setText(SalesTaxPercent.getText().toString());
                                edtItemServiceTax.setText(ServiceTaxPercent.getText().toString());
                                btnAdd.setEnabled(false);
                                btnEdit.setEnabled(true);
                            }
                        }
                    });

                    rowItems.setTag("TAG");


                    tblItems.addView(rowItems, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    i++;*/
                }
            } while (crsrItems.moveToNext());
        } else {
            Log.d("DisplayItem", "No Item found");
        }

    }

    private int getIndex(String substring){

        int index = 0;
        for (int i = 0; index==0 && i < spnrMOU.getCount(); i++){

            if (spnrMOU.getItemAtPosition(i).toString().contains(substring)){
                index = i;
            }

        }

        return index;

    }

    public int getIndexDept(String item)
    {
        for (int i = 0; i < labelsDept.size(); i++)
        {
            String auction = labelsDept.get(i);
            if (item.equals(auction))
            {
                return i;
            }
        }
        return -1;
    }

    public int getIndexCateg(String item)
    {
        for (int i = 0; i < labelsCateg.size(); i++)
        {
            String auction = labelsCateg.get(i);
            if (item.equals(auction))
            {
                return i;
            }
        }
        return -1;
    }



    private boolean IsItemExists(String ItemFullName, String MenuCode) {
        boolean isItemExists = false;

        for (ItemOutward item : dataList) {
            if (item.getLongName().equalsIgnoreCase(ItemFullName.toUpperCase()) || item.getItemId() == Integer.parseInt(MenuCode)) {
                isItemExists = true;
                break;
            }
        }
        return isItemExists;
    }

    private void InsertItem(String LongName, String ShortName, float DineInPrice1, float DineInPrice2,
                            float DineInPrice3, float TakeAwayPrice, float PickUpPrice, float DeliveryPrice, float Stock,
                            int PriceChange, int DiscountEnable, int BillWithStock, int TaxType, int DeptCode, int CategCode,
                            int KitchenCode, int SalesTaxId, int AdditionalTaxId, int OptionalTaxId1, int OptionalTaxId2, int DiscId,
                            String ItemBarcode, String ImageUri , Float frate, Float fquantity,String hsnCode, Float fgsttax,
                            String g_s, String MOU_str, String taxationtype, float SalesTaxPercent, float ServiceTaxPercent, int MenuCode) {

        long lRowId = 0;
        float cgsttax=0,sgsttax=0;

        // gst
        /*if (taxationtype.equalsIgnoreCase("GST")) {
            if (fgsttax == 0) {
                cgsttax =sgsttax = 0;
            } else {
                cgsttax = sgsttax = fgsttax / 2;
            }
        }if (taxationtype.equalsIgnoreCase("AdditionalTax"))
        {
            if (fgsttax == 0) {
                cgsttax = 0;
            } else {
                cgsttax = fgsttax;
            }
        }*/

        Item objItem = new Item(ItemBarcode, LongName, ShortName, AdditionalTaxId, BillWithStock, CategCode, DeptCode,
                DiscId, DiscountEnable, KitchenCode, OptionalTaxId1, OptionalTaxId2, PriceChange, SalesTaxId, TaxType,
                DeliveryPrice, DineInPrice1, DineInPrice2, DineInPrice3, PickUpPrice, Stock, TakeAwayPrice, ImageUri,hsnCode,
                fgsttax,0,cgsttax, 0,sgsttax, 0 ,MOU_str, taxationtype,frate,g_s, SalesTaxPercent, ServiceTaxPercent, MenuCode);

        lRowId = dbItems.addItem(objItem);

        Log.d("Item", "Row Id:" + String.valueOf(lRowId));
    }

    private void ReadData(int Type) {
        String strMenuCode = "", strLongName = "", strShortName = "", strBarcode = "";
        int iDeptCode = 0, iCategCode = 0, iKitchenCode = 0, iSalesTaxId = 0, iAdditionalTaxId = 0, iOptionalTaxId1 = 0,
                iOptionalTaxId2 = 0, iDiscountId = 0, iPriceChange = 0, iDiscountEnable = 0, iBillWithStock = 0,
                iTaxType = 0;
        float fDineIn1 = 0, fDineIn2 = 0, fDineIn3 = 0, fTakeAway = 0, fPickUp = 0, fDelivery = 0, fStock = 0;

        strMenuCode = edtMenuCode.getText().toString();
        strLongName = txtLongName.getText().toString().toUpperCase();
        strBarcode = txtBarcode.getText().toString();

        fDineIn1 = Float.parseFloat(txtDineIn1.getText().toString());
        fDineIn2 = Float.parseFloat(txtDineIn2.getText().toString());
        fDineIn3 = Float.parseFloat(txtDineIn3.getText().toString());
        fPickUp = 0.00f;
        fTakeAway = 0.00f;
        fDelivery = 0.00f;
        fStock = Float.parseFloat(txtStock.getText().toString());

        if(!spnrDepartment.getSelectedItem().toString().equalsIgnoreCase("Select"))
            iDeptCode = dbItems.getDepartmentIdByName(labelsDept.get(spnrDepartment.getSelectedItemPosition()));
        if(!spnrCategory.getSelectedItem().toString().equalsIgnoreCase("Select department"))
            iCategCode = dbItems.getCategoryIdByName(String.valueOf(spnrCategory.getSelectedItem()));
        iKitchenCode = spnrKitchen.getSelectedItemPosition() ;

        // richa - gst

        float frate = 0.0f, fquantity = 0.0f,fgsttax =0.0f;
        String hsnCode = "";


        /*frate = Float.parseFloat(etRate.getText().toString());
        fquantity = Float.parseFloat(etQuantity.getText().toString());
        hsnCode = etHSN.getText().toString();
        fgsttax = Float.parseFloat(etGstTax.getText().toString());*/
        iDiscountId = Integer.parseInt(etDiscount.getText().toString());
        String g_s = spnrG_S.getItemAtPosition(spnrG_S.getSelectedItemPosition()).toString();
        //String MOU_str = spnrMOU.getItemAtPosition(spnrMOU.getSelectedItemPosition()).toString();
        String MOU_str_temp = spnrMOU.getSelectedItem().toString();
        int length  = MOU_str_temp.length();
        String MOU_str = MOU_str_temp.substring(length-3, length-1);

        String taxationtype_str = spnrtaxationtype.getItemAtPosition(spnrtaxationtype.getSelectedItemPosition()).toString();

        float fSalesTax = Float.parseFloat(edtItemSalesTax.getText().toString());
        float fServiceTax = Float.parseFloat(edtItemServiceTax.getText().toString());

        //Cursor crsrSettings = dbItems.getBillSetting();
        if(crsrSettings.getString(crsrSettings.getColumnIndex("ItemNoReset")).equalsIgnoreCase("0")) {
            iMenuCode = dbItems.getItemMenuCode();
            iMenuCode++;
        } else {
            iMenuCode = Integer.valueOf(edtMenuCode.getText().toString());

        }

        if (fgsttax >= 0 && fquantity >= 0) {
            // Type 1 - addItem, Type 2 - updateItem
            if (Type == 1) {
                if (IsItemExists(strLongName.toUpperCase(), String.valueOf(iMenuCode))) {
                    MsgBox.Show("Warning", "Item already present");
                } else {

                    InsertItem(strLongName, strLongName, fDineIn1, fDineIn2, fDineIn3, fTakeAway, fPickUp, fDelivery,
                            fStock, iPriceChange, iDiscountEnable, iBillWithStock, iTaxType, iDeptCode, iCategCode,
                            iKitchenCode, iSalesTaxId, iAdditionalTaxId, iOptionalTaxId1, iOptionalTaxId2, iDiscountId,
                            strBarcode, strImageUri, frate, fquantity, hsnCode, fgsttax, g_s, MOU_str, taxationtype_str,
                            fSalesTax, fServiceTax, iMenuCode);


                    // inserting new item stock in OutwardStock Table
                    double rate =0;
                    if(Double.parseDouble(txtDineIn1.getText().toString()) >0)
                        rate = Double.parseDouble(txtDineIn1.getText().toString());
                    else if(Double.parseDouble(txtDineIn2.getText().toString()) >0)
                        rate = Double.parseDouble(txtDineIn2.getText().toString());
                    else if(Double.parseDouble(txtDineIn3.getText().toString()) >0)
                        rate = Double.parseDouble(txtDineIn3.getText().toString());

                    double quantity =Double.parseDouble(txtStock.getText().toString());
//                    Cursor date_cursor = dbItems.getCurrentDate();
//                    String currentdate = "";
//                    if(date_cursor.moveToNext())
//                        currentdate = date_cursor.getString(date_cursor.getColumnIndex("BusinessDate"));
                    StockOutwardMaintain stock_outward = new StockOutwardMaintain(myContext, dbItems);
                    stock_outward.addItemToStock_Outward( businessDate, iMenuCode,
                            strLongName,quantity, rate );
                }

            } else if (Type == 2) {

                float tax = 0;


                if (fgsttax > 0) {
                    tax = fgsttax / 2;
                }

                int iRowId = dbItems.updateItem(Integer.parseInt(edtMenuCode.getText().toString()), strLongName, strShortName, strBarcode,
                        iDeptCode, iCategCode, iKitchenCode, fDineIn1, fDineIn2, fDineIn3, fTakeAway, fPickUp, fDelivery,
                        iSalesTaxId, iAdditionalTaxId, iOptionalTaxId1, iOptionalTaxId2, iDiscountId, fStock, iPriceChange,
                        iDiscountEnable, iBillWithStock, strImageUri, iTaxType, frate, hsnCode, g_s, MOU_str, taxationtype_str, fgsttax, tax, tax,
                        fSalesTax, fServiceTax, Integer.valueOf(strItemId));
                Log.d("updateCategory", "Updated Rows: " + String.valueOf(iRowId));


                // updating outwardStock table
                double rate =0;
                if(Double.parseDouble(txtDineIn1.getText().toString()) >0)
                    rate = Double.parseDouble(txtDineIn1.getText().toString());
                else if(Double.parseDouble(txtDineIn2.getText().toString()) >0)
                    rate = Double.parseDouble(txtDineIn2.getText().toString());
                else if(Double.parseDouble(txtDineIn3.getText().toString()) >0)
                    rate = Double.parseDouble(txtDineIn3.getText().toString());

                double quantity =Double.parseDouble(txtStock.getText().toString());
                Cursor date_cursor = dbItems.getCurrentDate();
//                String currentdate = "";
//                if(date_cursor.moveToNext())
//                    currentdate = date_cursor.getString(date_cursor.getColumnIndex("BusinessDate"));
                StockOutwardMaintain stock_outward = new StockOutwardMaintain(myContext, dbItems);
                stock_outward.updateOpeningStock_Outward( businessDate, Integer.parseInt(strMenuCode),
                        strLongName,quantity, rate );
                stock_outward.updateClosingStock_Outward( businessDate, Integer.parseInt(strMenuCode),
                        strLongName,quantity);
            }
        }
        else {
            ResetItem();
            Toast.makeText(myContext, " Negative Values not allowed for GST Tax and Quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void ClearItemTable() {
        for (int i = 0; i < tblItems.getChildCount(); i++) {
            View Row = tblItems.getChildAt(i);
            if (Row instanceof TableRow) {
                ((TableRow) Row).removeAllViews();
            }
        }
    }

    private void ResetItem() {
        strItemId = "";
        strImageUri = "";
        txtLongName.setText("");
        //txtShortName.setText("");
        txtBarcode.setText("");
        txtDineIn1.setText("0.00");
        txtDineIn2.setText("0.00");
        txtDineIn3.setText("0.00");
        txtStock.setText("0.00");
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        tvFileName.setText("");
        strUploadFilepath="";
        edtItemSalesTax.setText("0.00");
        edtItemServiceTax.setText("0.00");
        edtMenuCode.setText("");

        etRate.setText("0");
        etQuantity.setText("0");
        etHSN.setText("0");
        etGstTax.setText("0");
        etDiscount.setText("0");

        spnrMOU.setSelection(0);
        spnrCategory.setSelection(0);
        spnrDepartment.setSelection(0);
        spnrKitchen.setSelection(0);

        fRate =0;
        fQuantity =0;
        txtHSNCode = "";
        fGSTTax = 0;
        fDiscount = 0;
        imgItemImage.setImageResource(R.drawable.img_noimage);


    }

    public void Browse(View v) {
        Intent intent = new Intent(myContext, FilePickerActivity.class);
        intent.putExtra("contentType","image");
        startActivityForResult(intent, FilePickerActivity.PICK_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FilePickerActivity.FILE_PICKER_CODE)
            {
                strImageUri = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                Log.d("FilePicker Result", "Path + FileName:" + strImageUri);
                // Toast.makeText(myContext, "Image Path:" + strImageUri,
                // Toast.LENGTH_LONG).show();
                if(!strImageUri.equalsIgnoreCase(""))
                {
                    imgItemImage.setImageURI(null);
                    imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
                }
                else
                {
                    imgItemImage.setImageResource(R.drawable.img_noimage);
                }
                strUploadFilepath = data.getStringExtra(UploadFilePickerActivity.EXTRA_FILE_PATH);
                tvFileName.setText(strUploadFilepath.substring(strUploadFilepath.lastIndexOf("/")+1));
            }
            else if (requestCode == FilePickerActivity.PICK_IMAGE_CODE)
            {
                strImageUri = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                Log.d("FilePicker Result", "Path + FileName:" + strImageUri);
                // Toast.makeText(myContext, "Image Path:" + strImageUri,
                // Toast.LENGTH_LONG).show();
                if(!strImageUri.equalsIgnoreCase("")) {
                    imgItemImage.setImageURI(null);
                    imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
                }else{
                    imgItemImage.setImageResource(R.drawable.img_noimage);
                }
                strUploadFilepath = data.getStringExtra(UploadFilePickerActivity.EXTRA_FILE_PATH);
                tvFileName.setText(strUploadFilepath.substring(strUploadFilepath.lastIndexOf("/")+1));
            }
        }
    }

    public void AddItem(View v) {

        if (txtLongName.getText().toString().equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please Enter Item Name");
            return;
        }

        if (spnrMOU.getSelectedItem().toString().equalsIgnoreCase("") ||spnrMOU.getSelectedItem().toString().equalsIgnoreCase("Select") ) {
            MsgBox.Show("Warning", "Please Select Item UOM");
            return;
        }


        if(crsrSettings.getString(crsrSettings.getColumnIndex("ItemNoReset")).equalsIgnoreCase("1")) {
            if (edtMenuCode.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Enter Item Code");
                return;
            }
            else {
                iMenuCode = Integer.valueOf(edtMenuCode.getText().toString());
                Cursor itemcrsr = dbItems.getItem(iMenuCode);
                if(itemcrsr.moveToNext())
                {
                    String name = itemcrsr.getString(itemcrsr.getColumnIndex("ItemName"));
                    if(!name.equalsIgnoreCase(txtLongName.getText().toString()))
                    {
                        MsgBox = new MessageDialog(myContext);
                        MsgBox.Show("Inconsistent"," Menu Code "+iMenuCode+" already present for item "+name );
                        return;
                    }
                }
            }
        }

        /*if (spnrDepartment.getSelectedItem().toString().equals("Select")) {
            MsgBox.Show("Warning", "Please Select Department");
            return;
        }*/

        if (txtDineIn1.getText().toString().equalsIgnoreCase("")) {
            txtDineIn1.setText("0");
        }

        if (txtDineIn2.getText().toString().equalsIgnoreCase("")) {
            txtDineIn2.setText("0");
        }

        if (txtDineIn3.getText().toString().equalsIgnoreCase("")) {
            txtDineIn3.setText("0");
        }

        if (txtStock.getText().toString().equalsIgnoreCase("")) {
            txtStock.setText("0");
        }
        /*if (etRate.getText().toString().equals(""))
        {
            etRate.setText("0");
        }*/
        /*if (etQuantity.getText().toString().equals(""))
        {
            etQuantity.setText("0");
        }*/
        /*if (etHSN.getText().toString().equals(""))
        {
            etHSN.setText("0");
        }
        if (etGstTax.getText().toString().equals(""))
        {
            etGstTax.setText("0");
        }*/
        if (etDiscount.getText().toString().equals(""))
        {
            etDiscount.setText("0");
        }


        new AsyncTask<Void,Void,Void>(){
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(ItemManagementActivity.this);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    ReadData(1); // 2 - updateItem
                    //ResetItem();
                    //ClearItemTable();
                    //DisplayItems();
                } catch (Exception exp) {
                    //Toast.makeText(myContext, exp.getMessage(), Toast.LENGTH_SHORT).show();
                    exp.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try{
                    ResetItem();
                    //ClearItemTable();
                    DisplayItemList();
                    Toast.makeText(myContext, "Item Added Successfully", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }catch (Exception e){
                    Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }

    public void EditItem(View v) {
        try {
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
            int invoiceno = dbItems.getLastBillNoforDate(String.valueOf(d.getTime()));
            if(invoiceno > 0)
            {
                // since already billing done for this businessdate, hence stock cannot be updated from here.
                // to update stock , goto Price & Stock module
                MsgBox.Show("Restriction", "You cannot update stock after making bill for the day. \n\nTo update stock , " +
                        "please goto Price & Stock module \n\n Or make dayend to update from here ");
                return;
            }


            if (txtLongName.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Enter Item Name");
                return;
            }

            if (spnrMOU.getSelectedItem().toString().equalsIgnoreCase("") || spnrMOU.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                MsgBox.Show("Warning", "Please Select Item UOM");
                return;
            }


            if (crsrSettings.getString(crsrSettings.getColumnIndex("ItemNoReset")).equalsIgnoreCase("1")) {
                if (edtMenuCode.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Please Enter Item Code");
                    return;
                } else {
                    iMenuCode = Integer.valueOf(edtMenuCode.getText().toString());
                    Cursor itemcrsr = dbItems.getItem(iMenuCode);
                    if (itemcrsr.moveToNext()) {
                        String name = itemcrsr.getString(itemcrsr.getColumnIndex("ItemName"));
                        if (!name.equalsIgnoreCase(txtLongName.getText().toString())) {
                            MsgBox = new MessageDialog(myContext);
                            MsgBox.Show("Inconsistent", " Menu Code " + iMenuCode + " already present for item " + name);
                            return;
                        }
                    }
                }
            }

            if (txtDineIn1.getText().toString().equalsIgnoreCase("")) {
                txtDineIn1.setText("0");
            }

            if (txtDineIn2.getText().toString().equalsIgnoreCase("")) {
                txtDineIn2.setText("0");
            }

            if (txtDineIn3.getText().toString().equalsIgnoreCase("")) {
                txtDineIn3.setText("0");
            }

            if (txtStock.getText().toString().equalsIgnoreCase("")) {
                txtStock.setText("0");
            }


        /*if (etRate.getText().toString().equals(""))
        {
            etRate.setText("0");
        }
        if (etQuantity.getText().toString().equals(""))
        {
            etQuantity.setText("0");
        }
        if (etHSN.getText().toString().equals(""))
        {
            etHSN.setText("0");
        }
        if (etGstTax.getText().toString().equals(""))
        {
            etGstTax.setText("0");
        }*/
            if (etDiscount.getText().toString().equals("")) {
                etDiscount.setText("0");
            }

            new AsyncTask<Void, Void, Void>() {
                ProgressDialog pd;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pd = new ProgressDialog(ItemManagementActivity.this);
                    pd.setMessage("Loading...");
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        ReadData(2); // 2 - updateItem
                        //ResetItem();
//                    ClearItemTable();
//                    DisplayItems();
                    } catch (Exception exp) {
                        //Toast.makeText(myContext, exp.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    try {
                        ResetItem();
                        //ClearItemTable();
                        DisplayItemList();
                        Toast.makeText(myContext, "Item Updated Successfully", Toast.LENGTH_LONG).show();
                        pd.dismiss();

                    } catch (Exception e) {
                        Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void ClearItem(View v) {
        ResetItem();
        //loadSpinnerData();
        //loadSpinnerData1();
    }

    public void CloseItem(View v) {
        dbItems.CloseDatabase();
        this.finish();
    }

    private void loadSpinnerData() {
        labelsDept = dbItems.getAllDeptforCateg();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labelsDept);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spnrDepartment.setAdapter(dataAdapter);
    }

    private void loadSpinnerData1() {
        if (spnrDepartment.getSelectedItem().toString().equals("Select"))
        {
            labelsCateg = new ArrayList<String>();
            labelsCateg.add("Select department");
        }
        else
        {
            labelsCateg = dbItems.getAllCategforDept();
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labelsCateg);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spnrCategory.setAdapter(dataAdapter);
    }
    private void loadSpinnerData_cat(ArrayList<String>categName) {
        if (categName.size() ==0)
        {
            return;
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, categName);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spnrCategory.setAdapter(dataAdapter);
    }

    private void loadSpinnerData1_old() {
        labelsCateg = dbItems.getAllCategforDept();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labelsCateg);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spnrCategory.setAdapter(dataAdapter);
    }


    private void DisplayItems_old() {
        Cursor crsrItems = null;

        crsrItems = dbItems.getAllItems();
        //crsrItems = dbItems.getAllItemswithDeptCategName();
        TableRow rowItems = null;

        TextView tvSno, tvMenuCode, tvLongName, tvShortName, tvDineIn1, tvDineIn2, tvDineIn3, tvTakeAway, tvPickUp, tvDelivery,
                tvStock, tvPriceChange, tvDiscountEnable, tvBillWithStock, tvTaxType, tvDeptCode, tvCategCode,
                tvKitchenCode, tvSalesTaxId, tvAdditionalTaxId, tvOptionalTaxId1, tvOptionalTaxId2, tvDiscountId,
                tvItemBarcode, tvImageUri, tvSpace, tvDeptName, tvCategName, tvItemId;
        TextView tvHSNCode_out,tvMOU,tvRate,tvGSTRate,tvSupplyType;
        ImageView imgIcon;
        ImageButton btnItemDelete;
        int i = 1;
        if (crsrItems.moveToFirst()) {
            do {
                rowItems = new TableRow(myContext);
                rowItems.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                rowItems.setBackgroundResource(R.drawable.row_background);

                tvSno = new TextView(myContext);
                tvSno.setTextSize(18);
                tvSno.setGravity(1);
                tvSno.setWidth(80);
                tvSno.setText(String.valueOf(i));
                rowItems.addView(tvSno);

                tvMenuCode = new TextView(myContext);
                tvMenuCode.setTextSize(0);
                tvMenuCode.setText(crsrItems.getString(crsrItems.getColumnIndex("MenuCode")));
                rowItems.addView(tvMenuCode);

                tvLongName = new TextView(myContext);
                tvLongName.setTextSize(18);
                tvLongName.setWidth(300);
                tvLongName.setText(crsrItems.getString(crsrItems.getColumnIndex("LongName")).toUpperCase());
                rowItems.addView(tvLongName);

                tvShortName = new TextView(myContext);
                tvShortName.setTextSize(18);
                //tvShortName.setText(crsrItems.getString(crsrItems.getColumnIndex("ShortName")));
                rowItems.addView(tvShortName);

                tvDineIn1 = new TextView(myContext);
                tvDineIn1.setGravity(1);
                tvDineIn1.setWidth(80);
                tvDineIn1.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice1")));
                rowItems.addView(tvDineIn1);

                tvDineIn2 = new TextView(myContext);
                tvDineIn2.setGravity(1);
                tvDineIn2.setWidth(80);
                tvDineIn2.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice2")));
                rowItems.addView(tvDineIn2);

                tvDineIn3 = new TextView(myContext);
                tvDineIn3.setGravity(1);
                tvDineIn3.setWidth(80);
                tvDineIn3.setText(crsrItems.getString(crsrItems.getColumnIndex("DineInPrice3")));
                rowItems.addView(tvDineIn3);

                tvTakeAway = new TextView(myContext);
                tvTakeAway.setGravity(1);
                tvTakeAway.setText(crsrItems.getString(crsrItems.getColumnIndex("TakeAwayPrice")));
                rowItems.addView(tvTakeAway);

                tvPickUp = new TextView(myContext);
                tvPickUp.setGravity(1);
                tvPickUp.setText(crsrItems.getString(crsrItems.getColumnIndex("PickUpPrice")));
                rowItems.addView(tvPickUp);

                tvDelivery = new TextView(myContext);
                tvDelivery.setGravity(1);
                tvDelivery.setText(crsrItems.getString(crsrItems.getColumnIndex("DeliveryPrice")));
                rowItems.addView(tvDelivery);

                tvStock = new TextView(myContext);
                tvStock.setGravity(1);
                tvStock.setWidth(110);
                tvStock.setText(crsrItems.getString(crsrItems.getColumnIndex("Quantity")));
                rowItems.addView(tvStock);

                tvPriceChange = new TextView(myContext);
                tvPriceChange.setText(crsrItems.getString(crsrItems.getColumnIndex("PriceChange")).equalsIgnoreCase("1")
                        ? "Yes" : "No");
                rowItems.addView(tvPriceChange);

                tvDiscountEnable = new TextView(myContext);
                tvDiscountEnable
                        .setText(crsrItems.getString(crsrItems.getColumnIndex("DiscountEnable")).equalsIgnoreCase("1")
                                ? "Yes" : "No");
                rowItems.addView(tvDiscountEnable);

                tvBillWithStock = new TextView(myContext);
                tvBillWithStock
                        .setText(crsrItems.getString(crsrItems.getColumnIndex("BillWithStock")).equalsIgnoreCase("1")
                                ? "Yes" : "No");
                rowItems.addView(tvBillWithStock);

                tvTaxType = new TextView(myContext);
                tvTaxType.setText(crsrItems.getString(crsrItems.getColumnIndex("TaxType")).equalsIgnoreCase("1")
                        ? "Forward Tax" : "Reverse Tax");
                rowItems.addView(tvTaxType);

                tvDeptCode = new TextView(myContext);
                tvDeptCode.setText(crsrItems.getString(crsrItems.getColumnIndex("DeptCode")));
                rowItems.addView(tvDeptCode);

                tvCategCode = new TextView(myContext);
                tvCategCode.setText(crsrItems.getString(crsrItems.getColumnIndex("CategCode")));
                rowItems.addView(tvCategCode);

                tvKitchenCode = new TextView(myContext);
                tvKitchenCode.setText(crsrItems.getString(crsrItems.getColumnIndex("KitchenCode")));
                rowItems.addView(tvKitchenCode);

                tvSalesTaxId = new TextView(myContext);
                tvSalesTaxId.setText(crsrItems.getString(crsrItems.getColumnIndex("SalesTaxId")));
                rowItems.addView(tvSalesTaxId);

                tvAdditionalTaxId = new TextView(myContext);
                tvAdditionalTaxId.setText(crsrItems.getString(crsrItems.getColumnIndex("AdditionalTaxId")));
                rowItems.addView(tvAdditionalTaxId);

                tvOptionalTaxId1 = new TextView(myContext);
                tvOptionalTaxId1.setText(crsrItems.getString(crsrItems.getColumnIndex("OptionalTaxId1")));
                rowItems.addView(tvOptionalTaxId1);

                tvOptionalTaxId2 = new TextView(myContext);
                tvOptionalTaxId2.setText(crsrItems.getString(crsrItems.getColumnIndex("OptionalTaxId2")));
                rowItems.addView(tvOptionalTaxId2);

                tvDiscountId = new TextView(myContext);
                tvDiscountId.setText(crsrItems.getString(crsrItems.getColumnIndex("DiscId")));
                rowItems.addView(tvDiscountId);

                tvItemBarcode = new TextView(myContext);
                tvItemBarcode.setText(crsrItems.getString(crsrItems.getColumnIndex("ItemBarcode")));
                rowItems.addView(tvItemBarcode);

                tvImageUri = new TextView(myContext);
                tvImageUri.setText(crsrItems.getString(crsrItems.getColumnIndex("ImageUri")));
                rowItems.addView(tvImageUri);

                imgIcon = new ImageView(myContext);
                imgIcon.setLayoutParams(new TableRow.LayoutParams(50, 40));
                imgIcon.setImageURI(null);
                if (!crsrItems.getString(crsrItems.getColumnIndex("ImageUri")).equalsIgnoreCase("")) { // &&
                    // strImageUri.contains("\\")){
                    imgIcon.setImageURI(Uri.fromFile(new File(crsrItems.getString(crsrItems.getColumnIndex("ImageUri")))));
                } else {
                    imgIcon.setImageResource(R.drawable.img_noimage);
                }
                rowItems.addView(imgIcon);

                tvSupplyType = new TextView(myContext);
                tvSupplyType.setGravity(1);
                tvSupplyType.setWidth(110);
                tvSupplyType.setText(crsrItems.getString(crsrItems.getColumnIndex("SupplyType")));
                rowItems.addView(tvSupplyType);

                tvGSTRate = new TextView(myContext);
                tvGSTRate.setGravity(1);
                tvGSTRate.setWidth(110);
                Float fGSTRate =0.0f;
                String IGSTRate_str = crsrItems.getString(crsrItems.getColumnIndex("IGSTRate"));
                if ((IGSTRate_str != null)&&Float.parseFloat(IGSTRate_str) == 0)
                {
                    String CGSTRate_str = crsrItems.getString(crsrItems.getColumnIndex("CGSTRate"));
                    if((CGSTRate_str!= null )&&(Float.parseFloat(IGSTRate_str)> 0)) {
                        Float fCgst = Float.parseFloat(CGSTRate_str);
                        fGSTRate = 2 * fCgst;
                    }
                    else
                    {
                        fGSTRate = Float.parseFloat(IGSTRate_str);
                    }
                }
                else
                {
                    fGSTRate = Float.parseFloat(IGSTRate_str);
                }
                tvGSTRate.setText(fGSTRate.toString());
                rowItems.addView(tvGSTRate);

                tvRate = new TextView(myContext);
                tvRate.setGravity(1);
                tvRate.setWidth(80);
                tvRate.setText(crsrItems.getString(crsrItems.getColumnIndex("Rate")));
                rowItems.addView(tvRate);

                tvMOU = new TextView(myContext);
                tvMOU.setGravity(1);
                tvMOU.setWidth(110);
                tvMOU.setText(crsrItems.getString(crsrItems.getColumnIndex("MOU")));
                rowItems.addView(tvMOU);

                tvHSNCode_out = new TextView(myContext);
                tvHSNCode_out.setGravity(1);
                tvHSNCode_out.setWidth(110);
                tvHSNCode_out.setText(crsrItems.getString(crsrItems.getColumnIndex("HSNCode")));
                rowItems.addView(tvHSNCode_out);

                // For Space purpose
                tvSpace = new TextView(myContext);
                tvSpace.setText("                ");
                rowItems.addView(tvSpace);

                // Delete
                int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                btnItemDelete = new ImageButton(myContext);
                btnItemDelete.setImageResource(res);
                btnItemDelete.setLayoutParams(new TableRow.LayoutParams(60, 40));
                //btnItemDelete.setOnClickListener(mListener);
                rowItems.addView(btnItemDelete);

                // For Space purpose
                tvSpace = new TextView(myContext);
                tvSpace.setText("              ");
                rowItems.addView(tvSpace);

                rowItems.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        if (String.valueOf(v.getTag()) == "TAG") {
                            TableRow Row = (TableRow) v;

                            TextView MenuCode = (TextView) Row.getChildAt(1);
                            TextView LongName = (TextView) Row.getChildAt(2);
                            TextView ShortName = (TextView) Row.getChildAt(3);
                            TextView DineIn1 = (TextView) Row.getChildAt(4);
                            TextView DineIn2 = (TextView) Row.getChildAt(5);
                            TextView DineIn3 = (TextView) Row.getChildAt(6);
                            TextView TakeAway = (TextView) Row.getChildAt(7);
                            TextView PickUp = (TextView) Row.getChildAt(8);
                            TextView Delivery = (TextView) Row.getChildAt(9);
                            TextView Stock = (TextView) Row.getChildAt(10);
                            TextView PriceChange = (TextView) Row.getChildAt(11);
                            TextView DiscountEnable = (TextView) Row.getChildAt(12);
                            TextView BillWithStock = (TextView) Row.getChildAt(13);
                            TextView TaxType = (TextView) Row.getChildAt(14);
                            TextView DeptCode = (TextView) Row.getChildAt(15);
                            TextView CategCode = (TextView) Row.getChildAt(16);
                            TextView KitchenCode = (TextView) Row.getChildAt(17);
                            TextView SalesTaxId = (TextView) Row.getChildAt(18);
                            TextView AdditionalTaxId = (TextView) Row.getChildAt(19);
                            TextView OptionalTaxId1 = (TextView) Row.getChildAt(20);
                            TextView OptionalTaxId2 = (TextView) Row.getChildAt(21);
                            TextView DiscountId = (TextView) Row.getChildAt(22);
                            TextView Barcode = (TextView) Row.getChildAt(23);
                            TextView ImageUri = (TextView) Row.getChildAt(24);
                            TextView SupplyType = (TextView) Row.getChildAt(25);
                            TextView GSTRate = (TextView) Row.getChildAt(26);
                            TextView Rate = (TextView) Row.getChildAt(27);
                            TextView MOU = (TextView) Row.getChildAt(28);
                            TextView HSNCode_out = (TextView) Row.getChildAt(29);

                            edtMenuCode.setText(MenuCode.getText().toString());
                            txtLongName.setText(LongName.getText());
                            // txtShortName.setText(ShortName.getText());
                            txtBarcode.setText(Barcode.getText());
                            txtDineIn1.setText(DineIn1.getText());
                            txtDineIn2.setText(DineIn2.getText());
                            txtDineIn3.setText(DineIn3.getText());
                            txtStock.setText(Stock.getText());
                            strImageUri = ImageUri.getText().toString();

                            // gst
                            etGstTax.setText(GSTRate.getText());
                            etRate.setText(Rate.getText());
                            etHSN.setText(HSNCode_out.getText());
                            spnrG_S.setSelection(Integer.parseInt(SupplyType.getText().toString()));
                            //spnrMOU.

                            // gst end
                            Cursor crsrDept = dbItems.getDepartment(Integer.valueOf(DeptCode.getText().toString()));
                            if(crsrDept.moveToFirst()) {
                                String deptName = crsrDept.getString(crsrDept.getColumnIndex("DeptName"));
                                int deptid = getIndexDept(deptName + "");
                                spnrDepartment.setSelection(deptid);
                            } else {
                                String deptName = "Select";
                                int deptid = getIndexDept(deptName + "");
                                spnrDepartment.setSelection(deptid);
                            }
                            //spnrDepartment.setSelection(Integer.parseInt(DeptCode.getText().toString()));
                            Cursor crsrCateg = dbItems.getCategory(Integer.valueOf(CategCode.getText().toString()));
                            if(crsrCateg.moveToFirst()) {
                                String categName = crsrCateg.getString(crsrCateg.getColumnIndex("CategName"));
                                int categid = getIndexCateg(categName + "");
                                spnrCategory.setSelection(categid);
                            } else {
                                String categName = "Select";
                                int categid = getIndexCateg(categName + "");
                                spnrCategory.setSelection(categid);
                            }
                            //spnrCategory.setSelection(Integer.parseInt(CategCode.getText().toString()));
                            spnrKitchen.setSelection(Integer.parseInt(KitchenCode.getText().toString()) - 1);
                            spnrSalesTax.setSelection(Integer.parseInt(SalesTaxId.getText().toString()) - 1);
                            spnrAdditionalTax.setSelection(Integer.parseInt(AdditionalTaxId.getText().toString()) - 1);
                            spnrOptionalTax1.setSelection(Integer.parseInt(OptionalTaxId1.getText().toString()) - 1);
                            spnrOptionalTax2.setSelection(Integer.parseInt(OptionalTaxId2.getText().toString()) - 1);
                            spnrDiscount.setSelection(Integer.parseInt(DiscountId.getText().toString()) - 1);

                            if (PriceChange.getText().toString().equalsIgnoreCase("Yes")) {
                                chkPriceChange.setChecked(true);
                            } else {
                                chkPriceChange.setChecked(false);
                            }
                            if (DiscountEnable.getText().toString().equalsIgnoreCase("Yes")) {
                                chkDiscountEnable.setChecked(true);
                            } else {
                                chkDiscountEnable.setChecked(false);
                            }
                            if (BillWithStock.getText().toString().equalsIgnoreCase("Yes")) {
                                chkBillWithStock.setChecked(true);
                            } else {
                                chkBillWithStock.setChecked(false);
                            }

                            if (TaxType.getText().toString().equalsIgnoreCase("Forward Tax")) {
                                rbForwardTax.setChecked(true);
                            } else if (TaxType.getText().toString().equalsIgnoreCase("Reverse Tax")) {
                                rbReverseTax.setChecked(true);
                            }

                            imgItemImage.setImageURI(null);
                            if (!strImageUri.equalsIgnoreCase("")) { // &&
                                // strImageUri.contains("\\")){
                                imgItemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
                            } else {
                                imgItemImage.setImageResource(R.drawable.img_noimage);
                            }
                            btnAdd.setEnabled(false);
                            btnEdit.setEnabled(true);
                        }
                    }
                });

                rowItems.setTag("TAG");

                tblItems.addView(rowItems, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                i++;
            } while (crsrItems.moveToNext());
        } else {
            Log.d("DisplayItem", "No Item found");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);
            LayoutInflater UserAuthorization = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vwAuthorization = UserAuthorization.inflate(R.layout.user_authorization, null);
            final EditText txtUserId = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserId);
            final EditText txtPassword = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserPassword);
            final TextView tvAuthorizationUserId= (TextView) vwAuthorization.findViewById(R.id.tvAuthorizationUserId);
            final TextView tvAuthorizationUserPassword= (TextView) vwAuthorization.findViewById(R.id.tvAuthorizationUserPassword);
            tvAuthorizationUserId.setVisibility(View.GONE);
            tvAuthorizationUserPassword.setVisibility(View.GONE);
            txtUserId.setVisibility(View.GONE);
            txtPassword.setVisibility(View.GONE);
            AuthorizationDialog
                    .setTitle("Are you sure you want to exit ?")
                    .setView(vwAuthorization)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            /*Intent returnIntent =new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);*/
                            finish();
                        }
                    })
                    .show();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_delete, menu);
        for (int j = 0; j < menu.size(); j++) {
            MenuItem item = menu.getItem(j);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            finish();
        }
        else if (id == R.id.action_home)
        {
            onHomePressed();
        }
        else if (id == R.id.action_screen_shot)
        {
            com.wep.common.app.ActionBarUtils.takeScreenshot(this,findViewById(android.R.id.content).getRootView());
        }
        else if (id == R.id.action_clear)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Do you really want to Delete?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(myContext, "clear", Toast.LENGTH_SHORT).show();
                            long lResult = dbItems.deleteAllOutwardItem();
                            if(lResult>0)
                            {
                                itemListAdapter.notifyDataSetChanged(dbItems.getAllItem());
                                Toast.makeText(myContext, "Items Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
