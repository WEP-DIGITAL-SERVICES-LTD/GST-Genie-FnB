/****************************************************************************
 * Project Name		:	VAJRA
 *
 * File Name		:	CustomerDetailActivity
 *
 * Purpose			:	Represents Customer Detail activity, takes care of all
 * 						UI back end operations in this activity, such as event
 * 						handling data read from or display in views.
 *
 * DateOfCreation	:	15-November-2012
 *
 * Author			:	Balasubramanya Bharadwaj B S
 *
 ****************************************************************************/
package com.wepindia.pos.views.Customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;
import com.epson.epos2.printer.Printer;
import com.wep.common.app.Database.Customer;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.CustomerPassbookBean;
import com.wep.common.app.utils.Preferences;
import com.wep.common.app.views.WepButton;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.DecimalDigitsInputFilter;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.GSTINValidation;
import com.wepindia.pos.views.Billing.BillingCounterSalesActivity;
import com.wepindia.pos.views.Customer.CustomerPassbook.CustomerPassbookDialogFragment;
import com.wepindia.printers.BixolonPrinterBaseAcivity;
import com.wepindia.printers.EPSONPrinterBaseActivity;
import com.wepindia.printers.TVSPrinterBaseActivity;
import com.wepindia.printers.WePTHPrinterBaseActivity;
import com.wepindia.printers.WepPrinterBaseActivity;
import com.wepindia.printers.wep.PrinterConnectionError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CustomerDetailActivity extends WepPrinterBaseActivity implements PrinterConnectionError {

    // Context object
    Context myContext;
    // DatabaseHandler object
    DatabaseHandler dbCustomer = new DatabaseHandler(CustomerDetailActivity.this);
    // MessageDialog object
    MessageDialog MsgBox;
    List<String> labelsItemName = null;
    // View handlers
    EditText txtName, txtPhone, txtAddress, txtSearchPhone, txtCreditAmount ,txGSTIN, txtCustomerCreditLimit, etCreditDepositAmt, etCustomerOpeningBal;
    TextView tvCustomerDepositAmt, tvCustomerOpeningBal;
    WepButton btnAdd, btnEdit,btnClearCustomer,btnCloseCustomer, btnEditPrint;
    TableLayout tblCustomer;
    AutoCompleteTextView txtSearchName;
    TextView tv_CustomerDetailMsg;
    String upon_rowClick_Phn = "";
    // Variables
    String Id, Name, Phone, Address, LastTransaction, TotalTransaction, CreditAmount, DepositAmount, strUserName = "", strCustGSTIN ="";
    private Toolbar toolbar;

    private float mHeadingTextSize;
    private float mDataMiniDeviceTextsize;
    private float mSamsungTab3VDeviceTextsize;
    private float mSamsungT561DeviceTextsize;

    private int mItemNameWidth;
    private int mHSNWidth;
    private int mQuantityWidth;
    private int mRateWidth;
    private int mAmountWidth;

    private final static int mSamsungTab3VScreenResolutionWidth = 600;
    private final static int mSamsungT561ScreenResolutionWidth = 800;
    private final static int mDataMiniScreenResolutionWidth = 752;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    TVSPrinterBaseActivity tvsPrinterBaseActivity;

    public boolean isPrinterAvailable = false;

    String BUSINESS_DATE = "";
    int BOLD_HEADER = 0;

    private TextView mItemNameTextView;
    private TextView mHSNTextView;
    private TextView mQuantityTextView;
    private TextView mRateTextView;
    private TextView mAmountTextView;
    private TextView mDeleteTextView;


    @Override
    public void onConfigurationRequired() {

    }

    @Override
    public void onPrinterAvailable(int flag) {
        if(flag == 2)
        {
//                btn_PrintBill.setEnabled(false);
//                btn_Reprint.setEnabled(false);
            SetPrinterAvailable(false);
        }
        else if(flag == 5)
        {
//                btn_PrintBill.setEnabled(true);
//                btn_Reprint.setEnabled(true);
            SetPrinterAvailable(true);
        }
        else if(flag == 0)
        {
//                btn_PrintBill.setEnabled(true);
//                btn_Reprint.setEnabled(true);
            SetPrinterAvailable(false);
        }
    }

    public void SetPrinterAvailable(boolean flag) {
        String status="Offline";
        if(flag)
            status = "Available";
        Toast.makeText(CustomerDetailActivity.this, "Bill Printer Status : " + status, Toast.LENGTH_SHORT).show();
        isPrinterAvailable = flag;
        //btn_PrintBill.setEnabled(true);
        //btn_Reprint.setEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // old   activity_customerdetail
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_customerdetail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = Preferences.getSharedPreferencesForPrint(CustomerDetailActivity.this); // getSharedPreferences("PrinterConfigurationActivity", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initSettingsData();

        myContext = this;
        MsgBox = new MessageDialog(myContext);

        strUserName = getIntent().getStringExtra("USER_NAME");

        //tvTitleUserName.setText(strUserName.toUpperCase());
        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        //tvTitleDate.setText("Date : " + s);
        com.wep.common.app.ActionBarUtils.setupToolbar(CustomerDetailActivity.this,toolbar,getSupportActionBar(),"Customers",strUserName," Date:"+s.toString());

        try {
            dbCustomer.CreateDatabase();
            dbCustomer.OpenDatabase();

            tv_CustomerDetailMsg = (TextView) findViewById(R.id.tv_CustomerDetailMsg);
            txtAddress = (EditText) findViewById(R.id.etCustomerAddress);
            txtName = (EditText) findViewById(R.id.etCustomerName);
            txtPhone = (EditText) findViewById(R.id.etCustomerPhone);
            txtCreditAmount = (EditText) findViewById(R.id.etCreditAmount);
            txtCustomerCreditLimit = (EditText) findViewById(R.id.etCreditCreditLimit);
            etCreditDepositAmt = (EditText) findViewById(R.id.etCreditDepositAmt);
            tvCustomerDepositAmt = (TextView) findViewById(R.id.tvCustomerDepositAmt);
            etCustomerOpeningBal = (EditText) findViewById(R.id.etCustomerOpeningBal);
            tvCustomerOpeningBal = (TextView) findViewById(R.id.tvCustomerOpeningBal);
            txtCreditAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});
            txtCustomerCreditLimit.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});
            etCreditDepositAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});
            etCustomerOpeningBal.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});
            txtSearchName = (AutoCompleteTextView) findViewById(R.id.etSearchCustomerName);
            txtSearchPhone = (EditText) findViewById(R.id.etSearchCustomerPhone);
            txGSTIN = (EditText) findViewById(R.id.etCustomerGSTIN);

            btnAdd = (WepButton) findViewById(R.id.btnAddCustomer);
            btnEdit = (WepButton) findViewById(R.id.btnEditCustomer);
            btnEditPrint = (WepButton) findViewById(R.id.btnEditPrintCustomer);
            btnClearCustomer = (WepButton) findViewById(R.id.btnClearCustomer);
            btnCloseCustomer = (WepButton) findViewById(R.id.btnCloseCustomer);

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddCustomer(v);
                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditCustomer(v, false);
                }
            });
            btnEditPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditCustomer(v, true);
                }
            });
            btnClearCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClearCustomer(v);
                }
            });
            btnCloseCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CloseCustomer(v);
                }
            });
            tblCustomer = (TableLayout) findViewById(R.id.tblCustomer);

            ResetCustomer();
            loadAutoCompleteData();

            txtSearchPhone.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    try {
                        if (txtSearchPhone.getText().toString().length() == 10) {
                            Cursor crsrCust = dbCustomer.getCustomer(txtSearchPhone.getText().toString());
                            if (crsrCust.moveToFirst()) {
                                /*txtName.setText(crsrCust.getString(crsrCust.getColumnIndex("CustName")));
                                txtPhone.setText(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")));
                                txtAddress.setText(crsrCust.getString(crsrCust.getColumnIndex("CustAddress")));
                                LastTransaction =crsrCust.getString(crsrCust.getColumnIndex("LastTransaction"));
                                TotalTransaction = crsrCust.getString(crsrCust.getColumnIndex("TotalTransaction"));

                                txtCreditAmount.setText(String.format("%.2f",crsrCust.getDouble(crsrCust.getColumnIndex("CreditAmount"))));
                                txtCustomerCreditLimit.setText(String.format("%.2f",crsrCust.getDouble(crsrCust.getColumnIndex("CreditLimit"))));
                                String gstin = crsrCust.getString(crsrCust.getColumnIndex("GSTIN"));
                                Id = (crsrCust.getString(crsrCust.getColumnIndex("CustId")));
                                upon_rowClick_Phn = txtPhone.getText().toString();
                                if (gstin==null)
                                    gstin = "";
                                txGSTIN.setText(gstin);*/


                                ClearCustomerTable();
                                DisplayCustomerSearch(txtSearchPhone.getText().toString());
                                txtSearchName.setText("");

                                btnAdd.setEnabled(false);
                                //btnEdit.setEnabled(true);
                                tv_CustomerDetailMsg.setVisibility(View.VISIBLE);
                                //tv_CustomerDetailMsg.setVisibility(View.VISIBLE);
                                //}
                            } else {
                                MsgBox.Show("Error", "Customer is not Found, Please Add Customer before Order");
                            }
                        } else {

                        }
                    } catch (Exception ex) {
                        MsgBox.Show("Error", ex.getMessage());
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            txtSearchName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        Cursor crsrCust = dbCustomer.getCustomerList(txtSearchName.getText().toString());
                        if (crsrCust.moveToFirst()) {
                            /*txtName.setText(crsrCust.getString(crsrCust.getColumnIndex("CustName")));
                            txtPhone.setText(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")));
                            txtAddress.setText(crsrCust.getString(crsrCust.getColumnIndex("CustAddress")));
                            txtCreditAmount.setText(String.format("%.2f",crsrCust.getDouble(crsrCust.getColumnIndex("CreditAmount"))));
                            txtCustomerCreditLimit.setText(String.format("%.2f",crsrCust.getDouble(crsrCust.getColumnIndex("CreditLimit"))));
                            String gstin = crsrCust.getString(crsrCust.getColumnIndex("GSTIN"));
                            if (gstin==null)
                                gstin = "";
                            txGSTIN.setText(gstin);*/
                            //txGSTIN.setText(crsrCust.getString(crsrCust.getColumnIndex("GSTIN")));
                            ClearCustomerTable();
                            DisplayCustomerSearchbyName(txtSearchName.getText().toString());
                            txtSearchPhone.setText("");

                            btnAdd.setEnabled(false);
                            tv_CustomerDetailMsg.setVisibility(View.VISIBLE);
                            //}
                        } else {
                            MsgBox.Show("", "Customer is not Found, Please Add Customer before Order");
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            getScreenResolutionWidthType(checkScreenResolutionWidthType(this));

            DisplayCustomer();
        } catch (Exception exp) {
            Toast.makeText(myContext, "OnCreate: " + exp.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private static int checkScreenResolutionWidthType(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return height;
    }

    private void getScreenResolutionWidthType(int screenResolutionType) {

        switch (screenResolutionType) {

            case mSamsungTab3VScreenResolutionWidth:
                mDataMiniDeviceTextsize = 8;
                mItemNameWidth = 105;
                mHSNWidth = 50;
                mQuantityWidth = 55;
                mRateWidth = 60;
                mAmountWidth = 60;
                break;

            case mSamsungT561ScreenResolutionWidth:
                mHeadingTextSize = 16;
                mDataMiniDeviceTextsize = 9;
                mItemNameWidth = 140;
                mHSNWidth = 70;
                mQuantityWidth = 65;
                mRateWidth = 65;
                mAmountWidth = 75;
              /*  mItemNameTextView.setTextSize(mHeadingTextSize);
                mHSNTextView.setTextSize(mHeadingTextSize);
                mQuantityTextView.setTextSize(mHeadingTextSize);
                mRateTextView.setTextSize(mHeadingTextSize);
                mAmountTextView.setTextSize(mHeadingTextSize);
                mDeleteTextView.setTextSize(mHeadingTextSize);*/
                break;

            case mDataMiniScreenResolutionWidth:
                mHeadingTextSize = 16;
                mDataMiniDeviceTextsize = 11;
                mItemNameWidth = 140;
                mHSNWidth = 70;
                mQuantityWidth = 65;
                mRateWidth = 65;
                mAmountWidth = 85;
              /*  mItemNameTextView.setTextSize(mHeadingTextSize);
                mHSNTextView.setTextSize(mHeadingTextSize);
                mQuantityTextView.setTextSize(mHeadingTextSize);
                mRateTextView.setTextSize(mHeadingTextSize);
                mAmountTextView.setTextSize(mHeadingTextSize);
                mDeleteTextView.setTextSize(mHeadingTextSize);*/
                break;
        }
    }

    private void DisplayCustomerSearch(String PhoneNo) {
        Cursor crsrCustomer;
        crsrCustomer = dbCustomer.getCustomer(PhoneNo);

        TableRow rowCustomer = null;
        TextView tvSno, tvId, tvName, tvLastTransaction, tvTotalTransaction, tvPhone, tvAddress, tvCreditAmount, tvCreditLimit, tvOpeningBalance;
        ImageButton btnImgDelete;
        int i = 1;
        if (crsrCustomer != null && crsrCustomer.getCount() > 0) {
            if (crsrCustomer.moveToFirst()) {
                do {
                    rowCustomer = new TableRow(myContext);
                    rowCustomer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    rowCustomer.setBackgroundResource(R.drawable.row_background);

                    tvSno = new TextView(myContext);
                    tvSno.setTextSize(18);
                    tvSno.setText(String.valueOf(i));
                    tvSno.setGravity(1);
                    rowCustomer.addView(tvSno);

                    tvId = new TextView(myContext);
                    tvId.setTextSize(18);
                    tvId.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustId")));
                    rowCustomer.addView(tvId);

                    tvName = new TextView(myContext);
                    tvName.setTextSize(18);
                    tvName.setPadding(7,0,0,0);
                    tvName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                    rowCustomer.addView(tvName);

                    tvLastTransaction = new TextView(myContext);
                    tvLastTransaction.setTextSize(18);
                    tvLastTransaction.setGravity(Gravity.LEFT);
                    tvLastTransaction.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("LastTransaction")));
                    rowCustomer.addView(tvLastTransaction);

                    tvTotalTransaction = new TextView(myContext);
                    tvTotalTransaction.setTextSize(18);
                    tvTotalTransaction.setGravity(Gravity.LEFT);
                    tvTotalTransaction.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("TotalTransaction")));
                    rowCustomer.addView(tvTotalTransaction);

                    tvPhone = new TextView(myContext);
                    tvPhone.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                    rowCustomer.addView(tvPhone);

                    tvAddress = new TextView(myContext);
                    tvAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
                    rowCustomer.addView(tvAddress);

                    tvCreditAmount = new TextView(myContext);
                    tvCreditAmount.setTextSize(18);
                    double amt = crsrCustomer.getDouble(crsrCustomer.getColumnIndex("CreditAmount"));
                    tvCreditAmount.setText(String.format("%.2f",amt));
                    tvCreditAmount.setGravity(Gravity.LEFT);
                    tvCreditAmount.setPadding(0,0,10,0);
                    rowCustomer.addView(tvCreditAmount);

                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    btnImgDelete = new ImageButton(myContext);
                    btnImgDelete.setImageResource(res);
                    btnImgDelete.setLayoutParams(new TableRow.LayoutParams(60, 40));
                    btnImgDelete.setOnClickListener(mListener);
                    rowCustomer.addView(btnImgDelete);

                    TextView tvGSTIN = new TextView(myContext);
                    String gstin = crsrCustomer.getString(crsrCustomer.getColumnIndex("GSTIN"));
                    if (gstin==null)
                        gstin = "";
                    tvGSTIN.setText(gstin);
                    tvGSTIN.setGravity(1);
                    rowCustomer.addView(tvGSTIN);

                    tvCreditLimit = new TextView(myContext);
                    double limit = crsrCustomer.getDouble(crsrCustomer.getColumnIndex("CreditLimit"));
                    tvCreditLimit.setText(String.format("%.2f",limit));
                    rowCustomer.addView(tvCreditLimit);

                    tvOpeningBalance = new TextView(myContext);
                    tvAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex(DatabaseHandler.KEY_OPENING_BALANCE)));
                    rowCustomer.addView(tvOpeningBalance);

                    rowCustomer.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (String.valueOf(v.getTag()) == "TAG") {
                                TableRow Row = (TableRow) v;

                                TextView rowId = (TextView) Row.getChildAt(1);
                                TextView rowName = (TextView) Row.getChildAt(2);
                                TextView rowLastTransaction = (TextView) Row.getChildAt(3);
                                TextView rowTotalTransaction = (TextView) Row.getChildAt(4);
                                TextView rowPhone = (TextView) Row.getChildAt(5);
                                TextView rowAddress = (TextView) Row.getChildAt(6);
                                TextView rowCreditAmount = (TextView) Row.getChildAt(7);
                                TextView gstin = (TextView)Row.getChildAt(9);
                                TextView rowCreditLimit = (TextView) Row.getChildAt(10);
                                TextView rowOpeningBalance = (TextView) Row.getChildAt(11);


                                Id = rowId.getText().toString();
                                LastTransaction = rowLastTransaction.getText().toString();
                                TotalTransaction = rowTotalTransaction.getText().toString();

                                txtName.setText(rowName.getText());
                                txtPhone.setText(rowPhone.getText());
                                upon_rowClick_Phn = rowPhone.getText().toString();
                                txtAddress.setText(rowAddress.getText());
                                txtCreditAmount.setText(rowCreditAmount.getText());
                                txGSTIN.setText(gstin.getText().toString());
                                txtCustomerCreditLimit.setText(rowCreditLimit.getText());

                                etCustomerOpeningBal.setEnabled(false);
                                etCustomerOpeningBal.setText(String.format("%.2f", Double.parseDouble(rowOpeningBalance.getText().toString().trim())));

                                btnAdd.setEnabled(false);
                                btnEdit.setEnabled(true);
                                btnEditPrint.setEnabled(true);
                            }
                        }
                    });

                    rowCustomer.setTag("TAG");

                    tblCustomer.addView(rowCustomer,
                            new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    i++;
                } while (crsrCustomer.moveToNext());
            } else {
                Log.d("DisplayCustomer", "No Customer found");
            }
        }
    }

    private void DisplayCustomerSearchbyName(String CustomerName) {
        Cursor crsrCustomer;
        crsrCustomer = dbCustomer.getCustomerList(CustomerName);

        TableRow rowCustomer = null;
        TextView tvSno, tvId, tvName, tvLastTransaction, tvTotalTransaction, tvPhone, tvAddress, tvCreditAmount, tvCreditLimit, tvOpeningBalance;
        ImageButton btnImgDelete;
        int i = 1;
        if (crsrCustomer != null && crsrCustomer.getCount() > 0) {
            if (crsrCustomer.moveToFirst()) {
                do {
                    rowCustomer = new TableRow(myContext);
                    rowCustomer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    rowCustomer.setBackgroundResource(R.drawable.row_background);

                    tvSno = new TextView(myContext);
                    tvSno.setTextSize(18);
                    tvSno.setText(String.valueOf(i));
                    tvSno.setGravity(1);
                    rowCustomer.addView(tvSno);

                    tvId = new TextView(myContext);
                    tvId.setTextSize(18);
                    tvId.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustId")));
                    rowCustomer.addView(tvId);

                    tvName = new TextView(myContext);
                    tvName.setTextSize(18);
                    tvName.setPadding(7,0,0,0);
                    tvName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                    rowCustomer.addView(tvName);

                    tvLastTransaction = new TextView(myContext);
                    tvLastTransaction.setTextSize(18);
                    tvLastTransaction.setGravity(Gravity.LEFT);
                    tvLastTransaction.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("LastTransaction")));
                    rowCustomer.addView(tvLastTransaction);

                    tvTotalTransaction = new TextView(myContext);
                    tvTotalTransaction.setTextSize(18);
                    tvTotalTransaction.setGravity(Gravity.LEFT);
                    tvTotalTransaction.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("TotalTransaction")));
                    rowCustomer.addView(tvTotalTransaction);

                    tvPhone = new TextView(myContext);
                    tvPhone.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                    rowCustomer.addView(tvPhone);

                    tvAddress = new TextView(myContext);
                    tvAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
                    rowCustomer.addView(tvAddress);

                    tvCreditAmount = new TextView(myContext);
                    double amt = crsrCustomer.getDouble(crsrCustomer.getColumnIndex("CreditAmount"));
                    tvCreditAmount.setTextSize(18);
                    tvCreditAmount.setText(String.format("%.2f",amt));
                    tvCreditAmount.setGravity(Gravity.LEFT);
                    tvCreditAmount.setPadding(0,0,10,0);
                    rowCustomer.addView(tvCreditAmount);

                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    btnImgDelete = new ImageButton(myContext);
                    btnImgDelete.setImageResource(res);
                    btnImgDelete.setLayoutParams(new TableRow.LayoutParams(60, 40));
                    btnImgDelete.setOnClickListener(mListener);
                    rowCustomer.addView(btnImgDelete);

                    TextView tvGSTIN = new TextView(myContext);
                    String gstin = crsrCustomer.getString(crsrCustomer.getColumnIndex("GSTIN"));
                    if (gstin==null)
                        gstin = "";
                    tvGSTIN.setText(gstin);
                    //tvGSTIN.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("GSTIN")));
                    tvGSTIN.setGravity(1);
                    rowCustomer.addView(tvGSTIN);

                    tvCreditLimit = new TextView(myContext);
                    double limit = crsrCustomer.getDouble(crsrCustomer.getColumnIndex("CreditLimit"));
                    tvCreditLimit.setText(String.format("%.2f",limit));
                    rowCustomer.addView(tvCreditLimit);

                    tvOpeningBalance = new TextView(myContext);
                    tvAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex(DatabaseHandler.KEY_OPENING_BALANCE)));
                    rowCustomer.addView(tvOpeningBalance);

                    rowCustomer.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (String.valueOf(v.getTag()) == "TAG") {
                                TableRow Row = (TableRow) v;

                                TextView rowId = (TextView) Row.getChildAt(1);
                                TextView rowName = (TextView) Row.getChildAt(2);
                                TextView rowLastTransaction = (TextView) Row.getChildAt(3);
                                TextView rowTotalTransaction = (TextView) Row.getChildAt(4);
                                TextView rowPhone = (TextView) Row.getChildAt(5);
                                TextView rowAddress = (TextView) Row.getChildAt(6);
                                TextView rowCreditAmount = (TextView) Row.getChildAt(7);
                                TextView gstin = (TextView) Row.getChildAt(9);
                                TextView rowCreditLimit = (TextView) Row.getChildAt(10);
                                TextView rowOpeningBalance = (TextView) Row.getChildAt(11);

                                Id = rowId.getText().toString();
                                LastTransaction = rowLastTransaction.getText().toString();
                                TotalTransaction = rowTotalTransaction.getText().toString();

                                txtName.setText(rowName.getText());
                                txtPhone.setText(rowPhone.getText());
                                upon_rowClick_Phn = rowPhone.getText().toString();
                                txtAddress.setText(rowAddress.getText());
                                txtCreditAmount.setText(rowCreditAmount.getText());
                                txtCustomerCreditLimit.setText(rowCreditLimit.getText());
                                txGSTIN.setText(gstin.getText().toString());
                                txtCustomerCreditLimit.setText(rowCreditLimit.getText());

                                etCustomerOpeningBal.setEnabled(false);
                                etCustomerOpeningBal.setText(String.format("%.2f", Double.parseDouble(rowOpeningBalance.getText().toString().trim())));

                                btnAdd.setEnabled(false);
                                btnEdit.setEnabled(true);
                                btnEditPrint.setEnabled(true);
                            }
                        }
                    });


                    rowCustomer.setTag("TAG");

                    tblCustomer.addView(rowCustomer,
                            new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    i++;
                } while (crsrCustomer.moveToNext());
            } else {
                Log.d("DisplayCustomer", "No Customer found");
            }
        }
    }


    @SuppressWarnings("deprecation")
    private void DisplayCustomer() {
        Cursor crsrCustomer;
        crsrCustomer = dbCustomer.getAllCustomer();

        TableRow rowCustomer = null;
        TextView tvSno, tvId, tvName, tvLastTransaction, tvTotalTransaction, tvPhone, tvAddress, tvCreditAmount,tvCreditLimit,tvOpeningBalance;
        Button btnPassBook;
        ImageButton btnImgDelete;
        int i = 1;
        if (crsrCustomer != null && crsrCustomer.getCount() > 0) {
            if (crsrCustomer.moveToFirst()) {
                do {
                    TableRow.LayoutParams param = new TableRow.LayoutParams(
                            0,
                            LayoutParams.MATCH_PARENT,
                            1.0f
                    );

                    rowCustomer = new TableRow(myContext);
                    rowCustomer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    rowCustomer.setBackgroundResource(R.drawable.row_background);
                    rowCustomer.setOrientation(LinearLayout.HORIZONTAL);

                    tvSno = new TextView(myContext);
                    tvSno.setTextSize(18);
                    tvSno.setText(String.valueOf(i));
                    tvSno.setGravity(Gravity.CENTER);
                    tvSno.setLayoutParams(new TableRow.LayoutParams(
                            0,
                            LayoutParams.MATCH_PARENT,
                            0.5f
                    ));
                    rowCustomer.addView(tvSno);

                    tvId = new TextView(myContext);
                    tvId.setTextSize(18);
                    tvId.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustId")));
                    tvId.setGravity(Gravity.CENTER_VERTICAL);
                    tvId.setLayoutParams(param);
                    rowCustomer.addView(tvId);

                    tvName = new TextView(myContext);
                    tvName.setTextSize(18);
//                    tvName.setPadding(7,0,0,0);
                    tvName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                    tvName.setGravity(Gravity.CENTER_VERTICAL);
                    tvName.setLayoutParams(param);
                    rowCustomer.addView(tvName);

                    tvLastTransaction = new TextView(myContext);
                    tvLastTransaction.setTextSize(18);
                    //tvLastTransaction.setPadding(15,0,0,0);
                    tvLastTransaction.setText(String.format("%.2f",crsrCustomer.getDouble(crsrCustomer.getColumnIndex("LastTransaction"))) + " ");
                    tvLastTransaction.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    tvLastTransaction.setLayoutParams(param);
                    rowCustomer.addView(tvLastTransaction);

                    tvTotalTransaction = new TextView(myContext);
                    tvTotalTransaction.setTextSize(18);
                    tvTotalTransaction.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    //tvTotalTransaction.setPadding(38,0,0,0);
                    tvTotalTransaction.setText(String.format("%.2f",crsrCustomer.getDouble(crsrCustomer.getColumnIndex("TotalTransaction"))) + " ");
                    tvTotalTransaction.setLayoutParams(param);
                    rowCustomer.addView(tvTotalTransaction);

                    tvPhone = new TextView(myContext);
                    tvPhone.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                    tvPhone.setLayoutParams(param);
                    rowCustomer.addView(tvPhone);

                    tvAddress = new TextView(myContext);
                    tvAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
                    tvAddress.setLayoutParams(param);
                    rowCustomer.addView(tvAddress);

                    tvCreditAmount = new TextView(myContext);
                    double amt = crsrCustomer.getDouble(crsrCustomer.getColumnIndex("CreditAmount"));
                    tvCreditAmount.setText(String.format("%.2f",amt) + " ");
                    tvCreditAmount.setTextSize(18);
                    tvCreditAmount.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    tvCreditAmount.setLayoutParams(param);
                    //tvCreditAmount.setPadding(15,0,0,0);
                    rowCustomer.addView(tvCreditAmount);

                    TableRow.LayoutParams special = new TableRow.LayoutParams(
                            0,
                            LayoutParams.MATCH_PARENT,
                            1f
                    );

                    special.setMargins(5, 5, 5, 5);

                    final Customer customer = new Customer();

                    customer.setiCustId(crsrCustomer.getInt(crsrCustomer.getColumnIndex("CustId")));
                    customer.setStrCustName(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                    customer.setStrCustGSTIN(crsrCustomer.getString(crsrCustomer.getColumnIndex("GSTIN")));
                    customer.setStrCustPhone(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                    customer.setStrCustAddress(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
                    customer.setdLastTransaction(crsrCustomer.getDouble(crsrCustomer.getColumnIndex("LastTransaction")));
                    customer.setdTotalTransaction(crsrCustomer.getDouble(crsrCustomer.getColumnIndex("TotalTransaction")));
                    customer.setdCreditLimit(crsrCustomer.getDouble(crsrCustomer.getColumnIndex("CreditLimit")));
                    customer.setdCreditAmount(crsrCustomer.getDouble(crsrCustomer.getColumnIndex("CreditAmount")));

                    //Passbook
                    btnPassBook = new Button(myContext);
//                    btnPassBook.setLayoutParams(new TableRow.LayoutParams(80, 40));
                    btnPassBook.setBackgroundResource(R.drawable.background_btn);
                    btnPassBook.setTextColor(getResources().getColor(R.color.white));
                    btnPassBook.setText("Passbook");
                    btnPassBook.setGravity(Gravity.CENTER);
                    btnPassBook.setLayoutParams(special);
                    btnPassBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fm = getSupportFragmentManager();
                            CustomerPassbookDialogFragment customerPassbookDialogFragment = new CustomerPassbookDialogFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(Customer.CUSTOMER_PARCELABLE_KEY, customer);
                            customerPassbookDialogFragment.setArguments(bundle);
                            customerPassbookDialogFragment.setCancelable(true);
                            customerPassbookDialogFragment.show(fm, Constants.CUSTOMER_PASSBOOK_TAG);
                        }
                    });
                    rowCustomer.addView(btnPassBook);

                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    btnImgDelete = new ImageButton(myContext);
                    btnImgDelete.setImageResource(res);
                    btnImgDelete.setLayoutParams(new TableRow.LayoutParams(0, 40, 0.4f));
//                    btnImgDelete.setLayoutParams(param);
                    btnImgDelete.setOnClickListener(mListener);
                    rowCustomer.addView(btnImgDelete);

                    TextView tvGSTIN = new TextView(myContext);
                    tvGSTIN.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("GSTIN")));
                    tvGSTIN.setGravity(1);
                    tvGSTIN.setLayoutParams(param);
                    rowCustomer.addView(tvGSTIN);

                    tvCreditLimit = new TextView(myContext);
                    double limit = crsrCustomer.getDouble(crsrCustomer.getColumnIndex("CreditLimit"));
                    tvCreditLimit.setText(String.format("%.2f",limit) + " ");
                    tvCreditLimit.setLayoutParams(param);
                    rowCustomer.addView(tvCreditLimit);

                    tvOpeningBalance = new TextView(myContext);
                    tvOpeningBalance.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex(DatabaseHandler.KEY_OPENING_BALANCE)));
                    tvOpeningBalance.setLayoutParams(param);
                    rowCustomer.addView(tvOpeningBalance);

                    rowCustomer.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (String.valueOf(v.getTag()) == "TAG") {
                                TableRow Row = (TableRow) v;

                                TextView rowId = (TextView) Row.getChildAt(1);
                                TextView rowName = (TextView) Row.getChildAt(2);
                                TextView rowLastTransaction = (TextView) Row.getChildAt(3);
                                TextView rowTotalTransaction = (TextView) Row.getChildAt(4);
                                TextView rowPhone = (TextView) Row.getChildAt(5);
                                TextView rowAddress = (TextView) Row.getChildAt(6);
                                TextView rowCreditAmount = (TextView) Row.getChildAt(7);
                                TextView rowCreditLimit = (TextView) Row.getChildAt(11);
                                TextView gstin = (TextView) Row.getChildAt(10);
                                TextView rowOpeningBalance = (TextView) Row.getChildAt(12);

                                Id = rowId.getText().toString();
                                LastTransaction = rowLastTransaction.getText().toString();
                                TotalTransaction = rowTotalTransaction.getText().toString();

                                txtName.setText(rowName.getText());
                                txtPhone.setText(rowPhone.getText());
                                upon_rowClick_Phn = rowPhone.getText().toString();
                                txtAddress.setText(rowAddress.getText());
                                txtCreditAmount.setText(rowCreditAmount.getText());
                                txtCustomerCreditLimit.setText(rowCreditLimit.getText());
                                txGSTIN.setText(gstin.getText().toString());

                                tvCustomerDepositAmt.setVisibility(View.VISIBLE);
                                etCreditDepositAmt.setVisibility(View.VISIBLE);
                                etCustomerOpeningBal.setEnabled(false);
                                etCustomerOpeningBal.setText(String.format("%.2f", Double.parseDouble(rowOpeningBalance.getText().toString().trim())));
                                btnAdd.setEnabled(false);
                                btnEdit.setEnabled(true);
                                btnEditPrint.setEnabled(true);
                            }
                        }
                    });

                    rowCustomer.setTag("TAG");

                    tblCustomer.addView(rowCustomer,
                            new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    i++;
                } while (crsrCustomer.moveToNext());
            } else {
                Log.d("DisplayCustomer", "No Customer found");
            }
        }
    }

    private View.OnClickListener mListener = new View.OnClickListener() {

        public void onClick(final View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                    .setTitle("Delete")
                    .setIcon(R.drawable.ic_launcher)
                    .setMessage("Are you sure you want to Delete this Customer.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            TableRow tr = (TableRow) v.getParent();
                            TextView CustId = (TextView) tr.getChildAt(1);
                            TextView CustName = (TextView) tr.getChildAt(2);

                            long lResult = dbCustomer.DeleteCustomer(Integer.valueOf(CustId.getText().toString()));
                            if(lResult>0)
                            {
                                MsgBox.Show("Confirmation", "Customer Deleted Successfully");
                                ((ArrayAdapter<String>)(txtSearchName.getAdapter())).remove(CustName.getText().toString());
                                ClearCustomerTable();
                                DisplayCustomer();
                            }

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    private boolean IsCustomerExists(String PhoneNumber) {
        boolean isCustomerExists = false;
        String strPhone = "", strName = "";
        TextView Name, Phone;

        for (int i = 1; i < tblCustomer.getChildCount(); i++) {

            TableRow Row = (TableRow) tblCustomer.getChildAt(i);

            if (Row.getChildAt(0) != null) {
                Phone = (TextView) Row.getChildAt(5);
                Name = (TextView) Row.getChildAt(2);
                strName = Name.getText().toString();
                strPhone = Phone.getText().toString();

                Log.v("CustomerActivity",
                        "Phone:" + strPhone.toUpperCase() + " New Phone:" + PhoneNumber.toUpperCase());

                if (strPhone.toUpperCase().equalsIgnoreCase(PhoneNumber.toUpperCase())) {
                    isCustomerExists = true;
                    break;
                }
            }
        }
        return isCustomerExists;
    }

    private void InsertCustomer(String strAddress, String strContactNumber, String strName, double fLastTransaction,
                                double fTotalTransaction, double fCreditAmount, String gstin, double creditLimit, double depositAmount, double openingBal) {
        long lRowId;

        Customer customer = new Customer();

        customer.setStrCustAddress(strAddress);
        customer.setStrCustPhone(strContactNumber);
        customer.setStrCustName(strName);
        customer.setdLastTransaction(fLastTransaction);
        customer.setdTotalTransaction(fTotalTransaction);
        customer.setdCreditAmount(fCreditAmount + openingBal);
        customer.setStrCustGSTIN(gstin);
        customer.setdCreditLimit(creditLimit);
        customer.setOpeningBalance(openingBal);
        customer.setDblDepositAmt(depositAmount);

        lRowId = dbCustomer.addCustomer(customer);
        
        if (lRowId > 0) {
            mStoreCustomerPassbookData(customer.getStrCustName(),customer.getStrCustPhone(),0);
        }
    }

    private void ClearCustomerTable() {
        for (int i = 1; i < tblCustomer.getChildCount(); i++) {
            View Row = tblCustomer.getChildAt(i);
            if (Row instanceof TableRow) {
                ((TableRow) Row).removeAllViews();
            }
        }
    }

    private void ResetCustomer() {
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtCreditAmount.setText("0.00");
        txtCustomerCreditLimit.setText("0.00");
        tvCustomerDepositAmt.setVisibility(View.GONE);
        etCreditDepositAmt.setVisibility(View.GONE);
        etCreditDepositAmt.setText("0.00");
        etCustomerOpeningBal.setEnabled(true);
        etCustomerOpeningBal.setText("0.00");
        txtSearchPhone.setText("");
        txtSearchName.setText("");
        txGSTIN.setText("");
        upon_rowClick_Phn="";
        LastTransaction ="";
        TotalTransaction = "";
        tv_CustomerDetailMsg.setVisibility(View.GONE);
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        btnEditPrint.setEnabled(false);
    }



    public void AddCustomer(View v) {
        Name = txtName.getText().toString();
        Phone = txtPhone.getText().toString();
        Address = txtAddress.getText().toString();
        CreditAmount = txtCreditAmount.getText().toString();
        CreditAmount = txtCreditAmount.getText().toString();
        String GSTIN  = txGSTIN.getText().toString().trim().toUpperCase();

        if (Name.equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please enter customer name before adding customer");
        } else if (Phone.equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please enter mobile no before adding customer");
        } else if(Phone.length() != 10) {
            MsgBox.Show("Warning", "Please enter correct mobile no before adding customer");
        } else {
            if (IsCustomerExists(Phone)) {
                MsgBox.Show("Warning", "Customer already exists");
            } else {

                if (GSTIN == null ) {
                    GSTIN = "";
                }
                boolean mFlag =  GSTINValidation.checkGSTINValidation(GSTIN);

                if(mFlag) {
                    if (!GSTINValidation.checkValidStateCode(GSTIN,this)) {
                        MsgBox.Show("Invalid Information", "Please Enter Valid StateCode for GSTIN");
                        return;
                    }
                    else{

                        double dDepositamount = 0.00;

                        double dOpeningBalance = etCustomerOpeningBal.getText().toString().trim().equals("") ? 0.00 :
                                Double.parseDouble(String.format("%.2f", Double.parseDouble(etCustomerOpeningBal.getText().toString().trim())));

                        double dCreditAmount = txtCreditAmount.getText().toString().trim().equals("") ? 0.00 :
                                Double.parseDouble(String.format("%.2f", Double.parseDouble(txtCreditAmount.getText().toString().trim())));

                        double dCreditLimit = txtCustomerCreditLimit.getText().toString().trim().equals("") ? 0.00 :
                                Double.parseDouble(String.format("%.2f", Double.parseDouble(txtCustomerCreditLimit.getText().toString().trim())));

                        InsertCustomer(Address, Phone, Name, 0, 0, dCreditAmount,
                                GSTIN, dCreditLimit, dDepositamount, dOpeningBalance);
                        Toast.makeText(myContext, "Customer Added Successfully", Toast.LENGTH_LONG).show();
                        ResetCustomer();
                        ClearCustomerTable();
                        DisplayCustomer();
                        ((ArrayAdapter<String>) (txtSearchName.getAdapter())).add(Name);
                    }
                }else
                {
                    MsgBox.Show("Invalid Information","Please enter valid GSTIN for customer");
                }
            }
        }
    }


    public void EditCustomer(View v, boolean isPrint) {
        Name = txtName.getText().toString();
        Phone = txtPhone.getText().toString();
        Address = txtAddress.getText().toString();
        CreditAmount = txtCreditAmount.getText().toString();
        DepositAmount = etCreditDepositAmt.getText().toString().trim();
        String GSTIN = txGSTIN.getText().toString().trim().toUpperCase();
        if (Name.equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please enter customer name before adding customer");
            return;
        } else if (Phone.equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please enter mobile no before adding customer");
            return;
        } else if(Phone.length() != 10) {
            MsgBox.Show("Warning", "Please enter correct mobile no before adding customer");
            return;
        }
        if(!Phone.equalsIgnoreCase(upon_rowClick_Phn))
        {
            Cursor cursor = dbCustomer.getCustomer(Phone);
            if(cursor!=null && cursor.moveToFirst())
            {
                String name = cursor.getString(cursor.getColumnIndex("CustName"));
                MsgBox.Show("Error", name+" already registered with Phn : "+Phone );
                return;
            }
        }
        if (GSTIN == null) {
            GSTIN = "";
        }
        if (DepositAmount.isEmpty()) {
            DepositAmount = "0.00";
        }
        boolean mFlag = GSTINValidation.checkGSTINValidation(GSTIN);
        if (mFlag)
        {
            if(!GSTINValidation.checkValidStateCode(GSTIN,this))
            {
                MsgBox.Show("Invalid Information","Please Enter Valid StateCode for GSTIN");
                return;
            }else {

                double dDepositAmount = etCreditDepositAmt.getText().toString().trim().equals("") ? 0.00 :
                        Double.parseDouble(String.format("%.2f", Double.parseDouble(etCreditDepositAmt.getText().toString().trim())));

                double dCreditAmount = txtCreditAmount.getText().toString().trim().equals("") ? 0.00 :
                        Double.parseDouble(String.format("%.2f", Double.parseDouble(txtCreditAmount.getText().toString().trim())));

                double dCreditLimit = txtCustomerCreditLimit.getText().toString().trim().equals("") ? 0.00 :
                        Double.parseDouble(String.format("%.2f", Double.parseDouble(txtCustomerCreditLimit.getText().toString().trim())));

                Customer customer = new Customer();

                customer.setStrCustAddress(Address);
                customer.setStrCustPhone(Phone);
                customer.setStrCustName(Name);
                customer.setiCustId(Integer.parseInt(Id));
                customer.setdLastTransaction(Double.parseDouble(LastTransaction));
                customer.setdTotalTransaction(Double.parseDouble(TotalTransaction));
                customer.setdCreditAmount(dCreditAmount + dDepositAmount);
                customer.setStrCustGSTIN(GSTIN);
                customer.setdCreditLimit(dCreditLimit);
                customer.setDblDepositAmt(dDepositAmount);

                int iResult = dbCustomer.updateCustomer(customer);

                Log.d("updateCustomer", "Updated Rows: " + String.valueOf(iResult));
                Toast.makeText(myContext, "Cus tomer Updated Successfully", Toast.LENGTH_LONG).show();
                if (iResult > 0) {
                    mStoreCustomerPassbookData(customer.getStrCustName(),customer.getStrCustPhone(),1);
                    if (isPrint) {
                        PrintAmountDepositReceipt(customer);
                    }
                    ResetCustomer();
                    ClearCustomerTable();
                    DisplayCustomer();
                    loadAutoCompleteData();
                } else {
                    MsgBox.Show("Warning", "Update failed");
                }
            }
        }else
        {
            MsgBox.Show("Invalid Information","Please enter valid GSTIN for customer");
        }
    }

    private void mStoreCustomerPassbookData(String strCustName, String strCustPhoneNo, int iMode){
        Cursor cursorCustomerData = null;
        try{
            cursorCustomerData = dbCustomer.getCustomer(strCustName,strCustPhoneNo);
            if(cursorCustomerData != null && cursorCustomerData.getCount() > 0){
                if (cursorCustomerData.moveToFirst()) {
                    // if(cursorCustomerData.getDouble(cursorCustomerData.getColumnIndex(DatabaseHandler.KEY_OpeningBalance)) > 0) {
                    CustomerPassbookBean customerPassbookBean = new CustomerPassbookBean();
                    customerPassbookBean.setStrCustomerID(cursorCustomerData.getInt(cursorCustomerData.getColumnIndex(DatabaseHandler.KEY_CustId)) + "");
                    customerPassbookBean.setStrName(cursorCustomerData.getString(cursorCustomerData.getColumnIndex(DatabaseHandler.KEY_CustName)));
                    customerPassbookBean.setStrPhoneNo(cursorCustomerData.getString(cursorCustomerData.getColumnIndex(DatabaseHandler.KEY_CustContactNumber)));
                    customerPassbookBean.setDblOpeningBalance(0);
                    customerPassbookBean.setDblCreditAmount(0);
                    switch(iMode){
                        case 0: // Add
                            customerPassbookBean.setDblOpeningBalance(cursorCustomerData.getDouble(cursorCustomerData.getColumnIndex(DatabaseHandler.KEY_OPENING_BALANCE)));
                            customerPassbookBean.setDblTotalAmount(cursorCustomerData.getDouble(cursorCustomerData.getColumnIndex(DatabaseHandler.KEY_OPENING_BALANCE)));
                            customerPassbookBean.setStrDescription(Constants.OPENING_BALANCE);
                            break;
                        case 1: // Update
                            customerPassbookBean.setDblCreditAmount(Double.parseDouble(String.format("%.2f",(Double.parseDouble(etCreditDepositAmt.getText().toString())))));
                                   /* double dblTotalAmountFromCustPassbookDB = getCustomerPassbookAvailableAmount(customerPassbookBean.getStrCustomerID(),customerPassbookBean.getStrPhoneNo());
                                    double dblTotalAmountFinal = 0.00;
                                    if(cursorCustomerData.getDouble(cursorCustomerData.getColumnIndex(DatabaseHandler.KEY_OpeningBalance)) > 0
                                            || cursorCustomerData.getDouble(cursorCustomerData.getColumnIndex(DatabaseHandler.KEY_CreditAmount)) > 0) {
                                        //dblTotalAmountFinal = (Double.parseDouble(String.format("%.2f", (Double.parseDouble(edtDepositAmt.getText().toString())))) - Math.abs(dblTotalAmountFromCustPassbookDB));
                                        dblTotalAmountFinal = (Double.parseDouble(String.format("%.2f",  Math.abs(dblTotalAmountFromCustPassbookDB - (Double.parseDouble(edtDepositAmt.getText().toString()))))));
                                    } else {
                                        dblTotalAmountFinal = (Double.parseDouble(String.format("%.2f", (Double.parseDouble(edtDepositAmt.getText().toString())))) + dblTotalAmountFromCustPassbookDB);
                                    }*/
                            double dblTotalDepositAmount = getCustomerPassbookTotalDepositAndOpeningAmount(customerPassbookBean.getStrCustomerID(),customerPassbookBean.getStrPhoneNo());
                            double dblTotalCrdeitAmount = getCustomerPassbookTotalCreditAmount(customerPassbookBean.getStrCustomerID(),customerPassbookBean.getStrPhoneNo());
                            customerPassbookBean.setDblTotalAmount(Double.parseDouble(String.format("%.2f",((dblTotalCrdeitAmount + customerPassbookBean.getDblCreditAmount())
                                    - dblTotalDepositAmount))));
                            customerPassbookBean.setStrDescription(Constants.DEPOSIT);
                            break;
                        default:
                            break;
                    }
                    Date date1 = new Date();
                    try {
                        date1 = new SimpleDateFormat("dd-MM-yyyy").parse(BUSINESS_DATE);
                    } catch (Exception e) {
                        Log.e("Customer Master", "" + e);
                        Log.e("Customer Master", "" + e);
                    }
                    customerPassbookBean.setStrDate("" + date1.getTime());
                    customerPassbookBean.setDblDepositAmount(0);
                    customerPassbookBean.setDblPettyCashTransaction(0);
                    customerPassbookBean.setDblRewardPoints(0);
                    try {
                        dbCustomer.addCustomerPassbook(customerPassbookBean);
                    }catch (Exception ex){
                        Log.i("Customer Master","Inserting data into customer passbook : " +ex.getMessage());
                    }
                    // }
                }
            } else {
                Log.i("Customer Master","No customer data selected for storing customer passbook.");
            }
        } catch (Exception ex){
            Log.i("Customer Master","Unable to store the customer passbook data." +ex.getMessage());
        } finally {
            if(cursorCustomerData != null){
                cursorCustomerData.close();
            }
        }
    }

    private double getCustomerPassbookTotalDepositAndOpeningAmount(String strCustID, String strCustPhoneNo){
        double dblResult = 0;
        Cursor cursorCustPassbookDeposit = null;
        try {
            cursorCustPassbookDeposit = dbCustomer.getCustomerPassbook_TotalDepositOpeningAmountForSelectedCustomer(strCustID, strCustPhoneNo);
            if(cursorCustPassbookDeposit != null && cursorCustPassbookDeposit.moveToFirst()){
                dblResult = cursorCustPassbookDeposit.getDouble(0);
            }
        } catch (Exception e) {
            Log.i("Customer Master","Fetching customer passbook total deposited and opening amount of selected customer. " +e.getMessage());
        }finally {
            if(cursorCustPassbookDeposit != null){
                cursorCustPassbookDeposit.close();
            }
        }
        return dblResult;
    }

    private double getCustomerPassbookTotalCreditAmount(String strCustID, String strCustPhoneNo){
        double dblResult = 0;
        Cursor cursorCustPassbookCredit = null;
        try {
            cursorCustPassbookCredit = dbCustomer.getCustomerPassbook_TotalCreditAmountForSelectedCustomer(strCustID, strCustPhoneNo);
            if(cursorCustPassbookCredit != null && cursorCustPassbookCredit.moveToFirst()){
                dblResult = cursorCustPassbookCredit.getDouble(0);
            }
        } catch (Exception e) {
            Log.i("Customer Master","Fetching customer passbook total credited amount of selected customer. " +e.getMessage());
        }finally {
            if(cursorCustPassbookCredit != null){
                cursorCustPassbookCredit.close();
            }
        }
        return dblResult;
    }

    private void initSettingsData() {
        Cursor crsrSettings = null;
        try {
            crsrSettings = dbCustomer.getBillSettings();
            if (crsrSettings != null && crsrSettings.moveToFirst()) {
                BUSINESS_DATE = crsrSettings.getString(crsrSettings.getColumnIndex(DatabaseHandler.KEY_BusinessDate));
                BOLD_HEADER = crsrSettings.getInt(crsrSettings.getColumnIndex(DatabaseHandler.KEY_HeaderBold));
            }
        } catch (Exception e) {
            Log.i("Customer Master","Settings init() error on customer master fragment screen. " +e.getMessage());
        }finally {
            if(crsrSettings != null){
                crsrSettings.close();
            }
        }
    }

    private void PrintAmountDepositReceipt(Customer item) {

        String prf = Preferences.getSharedPreferencesForPrint(CustomerDetailActivity.this).getString("receipt", "--Select--");

        if (!etCreditDepositAmt.getText().toString().isEmpty()&& !etCreditDepositAmt.getText().toString().equals(".")) {
            Cursor crsrHeaderFooterSetting = null;
            if (item.getIsDuplicate() == null)
                item.setIsDuplicate("");
            item.setHeaderPrintBold(BOLD_HEADER);
            item.setBusinessDate(BUSINESS_DATE);
            item.setDblDepositAmt(Double.parseDouble(String.format("%.2f", Double.parseDouble(etCreditDepositAmt.getText().toString().trim()))));
            try {
                crsrHeaderFooterSetting = dbCustomer.getBillSettings();

                if (crsrHeaderFooterSetting.moveToFirst()) {
                    item.setHeaderLine1(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText1")));
                    item.setHeaderLine2(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText2")));
                    item.setHeaderLine3(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText3")));
                    item.setHeaderLine4(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText4")));
                    item.setHeaderLine5(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText5")));
                    item.setFooterLine1(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText1")));
                    item.setFooterLine2(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText2")));
                    item.setFooterLine3(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText3")));
                    item.setFooterLine4(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText4")));
                    item.setFooterLine5(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText5")));
                } else {
                    Log.d("Print_Customer_Receipt", "DisplayHeaderFooterSettings No data in BillSettings table");
                }
            }catch (Exception ex){
                Log.e("Print_Customer_Receipt","Unable to fetch header details from billSettings table. From method PrintAmountDepositReceipt()." +ex.getMessage());
            } finally {
                if(crsrHeaderFooterSetting != null){
                    crsrHeaderFooterSetting.close();
                }
            }

            if (getPrinterName("receipt").equalsIgnoreCase(Constants.USB_EPSON_PRINTER_NAME)) {
                String target = Preferences.getSharedPreferencesForPrint(CustomerDetailActivity.this).getString(prf, "--Select--");
                EPSONPrinterBaseActivity epson = new EPSONPrinterBaseActivity();

                epson.setmTarget(target);
                epson.setmContext(myContext);
                epson.mInitListener(this);

                if (epson.runPrintDepositReceiptSequence(item, "Invoice")) {
//                            progressDialog.dismiss();
                    Toast.makeText(myContext, "Printed.", Toast.LENGTH_SHORT).show();
                } else {
//                            progressDialog.dismiss();
                }

            } else  if (getPrinterName("receipt").equalsIgnoreCase("Heyday"))  {
                if (isPrinterAvailable) {
                    printHeydeyDepositAmountReceipt(item, "Invoice");
                } else {
                    Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
                }
            } else  if (getPrinterName("receipt").equalsIgnoreCase("NGX"))  {
                if (isPrinterAvailable) {
                    printNGXDepositAmountReceipt(item, "Invoice");
                } else {
                    Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
                }
            } else if(getPrinterName("receipt").equalsIgnoreCase(Constants.USB_BIXOLON_PRINTER_NAME)) {
                String target = Preferences.getSharedPreferencesForPrint(CustomerDetailActivity.this).getString(prf, "--Select--");
                BixolonPrinterBaseAcivity bixolon = new BixolonPrinterBaseAcivity();

                bixolon.setmTarget(target);
                bixolon.setmContext(myContext);
                bixolon.mInitListener(this);

                if (bixolon.runPrintDepositReceiptSequence(item)) {
//                            progressDialog.dismiss();
                    Toast.makeText(myContext, "Deposit Amount Receipt Printed", Toast.LENGTH_SHORT).show();
                } else {
//                            progressDialog.dismiss();
                }
            } else if(getPrinterName("receipt").equalsIgnoreCase(Constants.USB_WEP_PRINTER_NAME)) {
                String target = Preferences.getSharedPreferencesForPrint(CustomerDetailActivity.this).getString(prf, "--Select--");

                WePTHPrinterBaseActivity wepPrinter = new WePTHPrinterBaseActivity();

                wepPrinter.setmTarget(target);
                wepPrinter.setmContext(myContext);
                wepPrinter.mInitListener(this);

                if (wepPrinter.runPrintDepositReceiptSequence(item)) {
                    Toast.makeText(myContext, "Deposit Amount Receipt Printed", Toast.LENGTH_SHORT).show();
                }

            } else if(getPrinterName("receipt").equalsIgnoreCase(Constants.USB_TVS_PRINTER_NAME)) {
                String target = Preferences.getSharedPreferencesForPrint(CustomerDetailActivity.this).getString(prf, "--Select--");
                tvsPrinterBaseActivity = new TVSPrinterBaseActivity();

                tvsPrinterBaseActivity.setmTarget(target);
                tvsPrinterBaseActivity.setmContext(myContext);
                tvsPrinterBaseActivity.mInitListener(this);

                if (tvsPrinterBaseActivity.runPrintDepositReceiptSequence(item)) {
                    //Toast.makeText(myContext, "Deposit Amount Receipt Printed", Toast.LENGTH_SHORT).show();
                }
            } else if(getPrinterName("receipt").equalsIgnoreCase(Constants.USB_WiFi_PRINTER_NAME)){
                MsgBox.Show("Warning", "Deposit Amount Receipt can't be printed on WiFi printer. Please configure Bluetooth/USB printers.");
            }
        }
    }

    public String getPrinterName(String module) {
        return Preferences.getSharedPreferencesForPrint(CustomerDetailActivity.this).getString(module, "--Select--");
    }

    public void ClearCustomer(View v) {
        ResetCustomer();
        ClearCustomerTable();
        DisplayCustomer();
    }

    public void CloseCustomer(View v) {

        dbCustomer.CloseDatabase();
        this.finish();
    }

    private void loadAutoCompleteData() {
        // List - Get Item Name
        labelsItemName = dbCustomer.getAllCustomerName();
        // Creating adapter for spinner
        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,labelsItemName);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(myContext,android.R.layout.simple_expandable_list_item_1);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        // attaching data adapter to spinner

        txtSearchName.setAdapter(dataAdapter);
        dataAdapter.setNotifyOnChange(true);
        dataAdapter.addAll(labelsItemName);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);
            AuthorizationDialog
                    .setTitle("Are you sure you want to exit ?")
                    .setIcon(R.drawable.ic_launcher)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            /*Intent returnIntent =new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);*/
                            dbCustomer.CloseDatabase();
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
                    .setIcon(R.drawable.ic_launcher)
                    .setMessage("Are you sure to delete all the existing customers?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(myContext, "clear", Toast.LENGTH_SHORT).show();
                            long lResult = dbCustomer.DeleteAllCustomer();
                            if(lResult>0)
                            {
                                ClearCustomer(null);
                                Toast.makeText(myContext, "Items Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onError(Printer printer, int errCode, String errMsg) {
        if (printer == null)
            return;

        if (printer.getStatus().getConnection() == Printer.FALSE)
            configureUsbPrinter();
    }

    @Override
    public void onError(BixolonPrinter printer, String errMsg) {
        if (printer == null)
            return;

        configureUsbPrinter();
    }

    @Override
    public void onError(String errMsg) {
        configureUsbPrinter();
    }

    @Override
    public void onError(int iError) {
        tvsPrinterBaseActivity.mTVSPrinterStatus(iError);
        if(iError != TVSPrinterBaseActivity.POS_SUCCESS) {
            configureUsbPrinter();
        }
    }


    void configureUsbPrinter() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            if (device.getVendorId() == Constants.VENDOR_ID_WEP_POS_PRINTER
                    && device.getProductId() == Constants.PRODUCT_ID_WEP_POS_PRINTER
                    && getPrinterName("bill").equalsIgnoreCase(Constants.USB_WEP_PRINTER_NAME)) {
                editor.putString("bill", device.getProductName());
                editor.putString(device.getProductName(), device.getDeviceName());
                editor.commit();
            }
        }
    }
}
