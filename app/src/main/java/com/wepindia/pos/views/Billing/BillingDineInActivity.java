package com.wepindia.pos.views.Billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;
import com.epson.epos2.printer.Printer;
import com.mswipetech.wisepad.payment.MSwipePaymentActivity;
import com.mswipetech.wisepad.payment.PasswordChangeActivity;
import com.mswipetech.wisepad.payment.fragments.FragmentLogin;
import com.mswipetech.wisepad.sdktest.view.ApplicationData;
import com.razorpay.PaymentResultListener;
import com.wep.common.app.Database.BillDetail;
import com.wep.common.app.Database.BillItem;
import com.wep.common.app.Database.Category;
import com.wep.common.app.Database.ComplimentaryBillDetail;
import com.wep.common.app.Database.Customer;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.DeletedKOT;
import com.wep.common.app.Database.Department;
import com.wep.common.app.Database.PendingKOT;
import com.wep.common.app.models.Items;
import com.wep.common.app.models.PaymentDetails;
import com.wep.common.app.print.BillKotItem;
import com.wep.common.app.print.BillServiceTaxItem;
import com.wep.common.app.print.BillSubTaxItem;
import com.wep.common.app.print.BillTaxItem;
import com.wep.common.app.print.BillTaxSlab;
import com.wep.common.app.print.PrintKotBillItem;
import com.wep.common.app.utils.Preferences;
import com.wep.common.app.views.WepButton;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.DateTime;
import com.wepindia.pos.GenericClasses.DecimalDigitsInputFilter;
import com.wepindia.pos.GenericClasses.EditTextInputHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.OnWalletPaymentResponseListener;
import com.wepindia.pos.R;
import com.wepindia.pos.RecyclerDirectory.TestItemsAdapter;
import com.wepindia.pos.views.Billing.Listeners.OnProceedToPayCompleteListener;
import com.wepindia.pos.views.Billing.PdfInvoice.CreatePdfInvoice;
import com.wepindia.pos.views.Billing.PdfInvoice.PdfInvoiceBean;
import com.wepindia.pos.views.Billing.PdfInvoice.PdfItemBean;
import com.wepindia.pos.views.Configurations.Category.Adapters.CategoryAdapter;
import com.wepindia.pos.views.Configurations.Department.Adapters.DepartmentAdapter;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wep.common.app.models.AddedItemsToOrderTableClass;
import com.wepindia.pos.utils.StockOutwardMaintain;
import com.wepindia.printers.BixolonPrinterBaseAcivity;
import com.wepindia.printers.EPSONPrinterBaseActivity;
import com.wepindia.printers.TVSPrinterBaseActivity;
import com.wepindia.printers.WePTHPrinterBaseActivity;
import com.wepindia.printers.WepPrinterBaseActivity;
import com.wepindia.printers.WifiPrinterBaseActivity;
import com.wepindia.printers.wep.PrinterConnectionError;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by RichaA on 3/20/2017.
 */


import java.util.regex.Matcher;

public class BillingDineInActivity extends WepPrinterBaseActivity implements TextWatcher , TestItemsAdapter.OnItemsImageClickListener, PrinterConnectionError,
        OnProceedToPayCompleteListener, PaymentResultListener, FragmentLogin.OnLoginCompletedListener{

    String linefeed = "";
    String tx ="";
    int CUSTOMER_FOUND =0;
    DecimalFormat df_2, df_3;
    Pattern p = Pattern.compile("^(-?[0-9]+[\\.\\,][0-9]{1,2})?[0-9]*$");
    int PRINTOWNERDETAIL = 0, BOLDHEADER = 0, PRINTSERVICE = 0, BILLAMOUNTROUNDOFF = 0,isForwardTaxEnabled = 1;
    int AMOUNTPRINTINNEXTLINE = 0;
    private static final String TAG = BillingDineInActivity.class.getSimpleName();
    private DatabaseHandler db;
    private DepartmentAdapter departmentAdapter;
    private CategoryAdapter categoryAdapter;
    Context myContext;
    DatabaseHandler dbBillScreen = new DatabaseHandler(BillingDineInActivity.this);
    MessageDialog MsgBox;
    EditText txtSearchItemBarcode,  tvWaiterNumber, tvTableNumber, tvTableSplitNo, tvBillNumber;
    ListView lstvwDepartment, lstvwCategory, lstvwKOTModifiers;
    //  GridView grdItems;

    private TextView tvServiceTax_text;
    private int UTGSTENABLED = 0,HSNPRINTENABLED=0;
    String fastBillingMode ="1";

    TableLayout tblOrderItems;
    TextView tvSalesTax, tvServiceTax;
    WepButton  btnShiftTable, btnMergeTable, btn_ReprintKOT,btnModifier,
            btnSaveKOT, btnPayBill, btnLoadKOT, btnDeleteKOT,btnDeleteBill,
            btnKOTStatus, btnPrintKOT, btnPrintBill, btnClear, btnReprint, btnPreviewBill;
    TextView tvDate, tvSubTotal,  tvIGSTValue, tvCGSTValue, tvSGSTValue, tvBillAmount, tvSubUdfValue, tvOthercharges,  tvDiscountAmount , tvDiscountPercentage;
    TextView tvWaiterName, tVLabelTableNo, tVLabelWaiterNo, tVLabelOrderNo, etCustGSTIN;
    EditText edtCustId, edtCustName, edtCustPhoneNo, edtCustAddress, edtCustDineInPhoneNo;
    Button btnAddCustomer, btn_item_fastBillingMode;
    CheckBox chk_interstate = null;
    EditText et_pos = null;
    LinearLayout relative_Interstate, relative_pos;
    Calendar Time; // Time variable
    private LinearLayout idd_date;
    Cursor crsrSettings = null, crsrCustomerDetails = null;
    ArrayAdapter<String> adapDept, adapCateg, adapModifiers;
    String[] Name;
    int REPRINT_KOT =0;
    String[] ImageUri;
    String strUserId = "", strUserName = "", strDate = "";
    int[] MenuCode;
    byte jBillingMode = 0, jWeighScale = 0;
    int BillwithStock = 0;
    int iTaxType = 0, iTotalItems = 0, iCustId = 0, iTokenNumber = 0;
    float fTotalDiscount = 0;
    double fCashPayment = 0, fCardPayment = 0, fCouponPayment = 0, fPaidTotalPayment = 0;
    double dPettCashPayment = 0;
    double fChangePayment = 0, fRoundOfValue =0;
    double dFinalBillValue=0;
    double fWalletPayment = 0;
    double dServiceTaxPercent = 0, dOtherChrgs = 0;
    String strPaymentStatus = "", strMakeOrder = "", strJurisdictionsPrint = "";
    Date d;
    int PrintBillPayment = 0;
    AutoCompleteTextView aTViewSearchItem, aTViewSearchMenuCode,autoCompleteTextViewSearchItemBarcode;
    Spinner spnr_pos;
    ArrayAdapter<CharSequence> POS_LIST;
    SimpleCursorAdapter deptdataAdapter, categdataAdapter;
    LinearLayout rowbtns, rowtexts;/*, idd3, idd2, idd0;*/
    String GSTEnable = "", HSNEnable_out = "", POSEnable = "";
    TextView tvHSNCode_out;
    Date strDate_date;
    int tableSplit =0, JURISDICTIONS_PRINT_STATUS = 0;
    float fTotalsubTaxPercent = 0;
    Button btndepart, btncateg, btnitem;
    TextView tvdeptline, tvcategline,tvcessValue;
    String HomeDeliveryCaption="", TakeAwayCaption="", DineInCaption = "", CounterSalesCaption = "";
    int iKOTNo = -1;
    int iPrintKOTStatus = 0;
    boolean isKOTReprint = false;
    public boolean isPrinterAvailable = false;
    private Toolbar toolbar;
    private AppCompatDelegate delegate;
    String ownerPos= "";
    int reprintBillingMode =0;
    boolean isReprint = false;
    int ItemwiseDiscountEnabled =0;
    RelativeLayout boxDept, boxCat,boxItem;
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

    private TextView mItemNameTextView;
    private TextView mHSNTextView;
    private TextView mQuantityTextView;
    private TextView mRateTextView;
    private TextView mAmountTextView;
    private TextView mDeleteTextView;

    private RecyclerView mRecyclerGridView;
    private GridLayoutManager mGridLayoutManager;
    //  private TestImageAdapter mTestImageAdapter;
    private TestItemsAdapter mTestItemsAdapter;

    private ArrayList<Items> mItemsList = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String customerId = "0";

    private FragmentManager fm;
    PayBillFragment proceedToPayBillingFragment = null;
    double dblCashPayment = 0, dblCardPayment = 0, dblCouponPayment = 0, dblPettyCashPayment = 0, dblPaidTotalPayment = 0, dblWalletPayment = 0, dblDiscountAmount = 0,
            dblChangePayment = 0, dblRoundOfValue = 0, dblOtherCharges = 0, dblRewardPointsAmount = 0, dblAEPSAmount = 0, dblMSwipeAmount = 0, dblPaytmAmount = 0;
    int  PRINT_DISCOUNT = 0, SHAREBILL = 0;
    boolean trainingMode = false;
    String custPhone = "", OWNERPOS = "", BUSINESS_DATE = "";
    private CreatePdfInvoice createPdfInvoice = null;
    //MSwipe
    public final int REQUEST_CODE_CARD_PAYMENT = 12;
    TVSPrinterBaseActivity tvsPrinterBaseActivity;
    private String PDF_INVOICES_GENERATE_PATH = Environment.getExternalStorageDirectory().getPath() + "/"+ Constants.PDF_INVOICE_DIRECTORY+"/";

    public void onConfigurationRequired() {

    }

    public void onPrinterAvailable(int flag) {

        //Toast.makeText(BillingCounterSalesActivity.this, "Bill Printer Status : " + flag, Toast.LENGTH_SHORT).show();
        //isPrinterAvailable = flag;
        if(flag == 2)
        {
            btnPrintBill.setEnabled(false);
            btnReprint.setEnabled(false);
            btnPrintKOT.setEnabled(false);
            SetPrinterAvailable(false);
        }
        else if(flag == 5)
        {
            btnPrintBill.setEnabled(true);
            btnReprint.setEnabled(true);
            btnPrintKOT.setEnabled(true);
            SetPrinterAvailable(true);
        }
        else if(flag == 0)
        {
            btnPrintBill.setEnabled(true);
            btnReprint.setEnabled(true);
            btnPrintKOT.setEnabled(true);
            SetPrinterAvailable(false);
        }
    }
    public void SetPrinterAvailable(boolean flag) {
        String status="Offline";
        if(flag)
            status = "Available";
        Toast.makeText(this, "Bill Printer Status : " + status, Toast.LENGTH_SHORT).show();
        isPrinterAvailable = flag;
        //btn_PrintBill.setEnabled(true);
        //btn_Reprint.setEnabled(true);
    }

    boolean REVERSETAX = false;

    /************************************************************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_billing_dinein);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewCenter = (TextView) toolbar.findViewById(com.wep.common.app.R.id.textViewCenter);
        db = new DatabaseHandler(this);
        myContext = BillingDineInActivity.this;
        sharedPreferences = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this); // getSharedPreferences("PrinterConfigurationActivity", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (getPrinterName(this, "bill").equalsIgnoreCase("NGX")) {
            mConnectNGX();
        }

        try {
            df_2 = new DecimalFormat("0.##");
            df_2.setRoundingMode(RoundingMode.FLOOR);

            df_3 = new DecimalFormat("0.###");
            df_3.setRoundingMode(RoundingMode.FLOOR);

            MsgBox = new MessageDialog(myContext);
            String strBillMode = getIntent().getStringExtra("BILLING_MODE");
            jBillingMode = Byte.parseByte(strBillMode);
            strUserId = ApplicationData.getUserId(this);//ApplicationData.USER_ID;
            strUserName = ApplicationData.getUserName(this);//ApplicationData.USER_NAME;
            iCustId = getIntent().getIntExtra("CUST_ID", 0);
            strPaymentStatus = getIntent().getStringExtra("Payment_Status");
            strMakeOrder = getIntent().getStringExtra("MAKE_ORDER");
            d = new Date();
            CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
            com.wep.common.app.ActionBarUtils.setupToolbar(BillingDineInActivity.this,toolbar,getSupportActionBar(),"",strUserName," Date:"+s.toString());
            IntializeViewVariables();
            getScreenResolutionWidthType(checkScreenResolutionWidthType(this));
            dbBillScreen.OpenDatabase();
            ClearAll();
            crsrSettings = dbBillScreen.getBillSetting();
            if (crsrSettings.moveToFirst())
            {
                BUSINESS_DATE = crsrSettings.getString(crsrSettings.getColumnIndex("BusinessDate"));
                DineInCaption = crsrSettings.getString(crsrSettings.getColumnIndex("HomeDineInCaption"));
                CounterSalesCaption = crsrSettings.getString(crsrSettings.getColumnIndex("HomeCounterSalesCaption"));
                HomeDeliveryCaption = crsrSettings.getString(crsrSettings.getColumnIndex("HomeHomeDeliveryCaption"));
                TakeAwayCaption = crsrSettings.getString(crsrSettings.getColumnIndex("HomeTakeAwayCaption"));
                ownerPos = crsrSettings.getString(crsrSettings.getColumnIndex("POSNumber"));

                BillwithStock = crsrSettings.getInt(crsrSettings.getColumnIndex("BillwithStock"));
                iTaxType = crsrSettings.getInt(crsrSettings.getColumnIndex("TaxType"));
                ItemwiseDiscountEnabled = crsrSettings.getInt(crsrSettings.getColumnIndex("DiscountType"));
                tableSplit = crsrSettings.getInt(crsrSettings.getColumnIndex("TableSpliting"));

                JURISDICTIONS_PRINT_STATUS = crsrSettings.getInt(crsrSettings.getColumnIndex(DatabaseHandler.KEY_JURISDICTIONS_STATUS));

                if (crsrSettings.getString(crsrSettings.getColumnIndex(DatabaseHandler.KEY_JURISDICTIONS)) != null)
                    strJurisdictionsPrint = crsrSettings.getString(crsrSettings.getColumnIndex(DatabaseHandler.KEY_JURISDICTIONS));

                isForwardTaxEnabled = crsrSettings.getInt(crsrSettings.getColumnIndex("Tax"));

                // GSt
                HSNEnable_out = crsrSettings.getString(crsrSettings.getColumnIndex("HSNCode_Out"));
                if (HSNEnable_out == null || HSNEnable_out.equals("0")|| HSNEnable_out.equals("")) {
                    HSNEnable_out = "0";
                    tvHSNCode_out.setVisibility(View.INVISIBLE);
                }
                else
                {tvHSNCode_out.setVisibility(View.VISIBLE);}

                POSEnable = crsrSettings.getString(crsrSettings.getColumnIndex("POS_Out"));
                if (POSEnable == null || POSEnable.equals("0")) {
                    POSEnable = "0";
                    relative_Interstate.setVisibility(View.INVISIBLE);
                }else {
                    relative_Interstate.setVisibility(View.VISIBLE);
                }
                GSTEnable = "1";

                if ((crsrSettings.getInt(crsrSettings.getColumnIndex("PrintOwnerDetail")) == 1)) { // print owner detail
                    PRINTOWNERDETAIL = 1;
                }else
                {
                    PRINTOWNERDETAIL = 0;
                }

                if ((crsrSettings.getInt(crsrSettings.getColumnIndex("BoldHeader")) == 1)) { // bold header
                    BOLDHEADER = 1;
                }else
                {
                    BOLDHEADER = 0;
                }

                if ((crsrSettings.getInt(crsrSettings.getColumnIndex("PrintService")) == 1)) { // bold header
                    PRINTSERVICE = 1;
                }else
                {
                    PRINTSERVICE = 0;
                }

                if ((crsrSettings.getInt(crsrSettings.getColumnIndex("BillAmountRoundOff")) == 1)) { // Bill Amount Round Off
                    BILLAMOUNTROUNDOFF = 1;
                }else
                {
                    BILLAMOUNTROUNDOFF = 0;
                }

            }

           /*if ((crsrSettings != null) && crsrSettings.moveToFirst()) {
                GSTEnable = crsrSettings.getString(crsrSettings.getColumnIndex("GSTEnable"));
                HSNEnable_out = crsrSettings.getString(crsrSettings.getColumnIndex("HSNCode_Out"));
                if (HSNEnable_out == null) {
                    HSNEnable_out = "0";
                }
                POSEnable = crsrSettings.getString(crsrSettings.getColumnIndex("POS_Out"));
                if (POSEnable == null) {
                    POSEnable = "0";
                }

            }
            if ((GSTEnable != null) && (GSTEnable.equals("1"))) {
                // setting TAX name
                tvSalesTax.setText(NAME_CGST_RATE);
                tvServiceTax.setText(NAME_SGST_RATE);
                etCustGSTIN.setVisibility(View.VISIBLE);


            } else {
                tvSalesTax.setText(NAME_SALES_RATE);
                tvServiceTax.setText(NAME_SERVICE_RATE);
                etCustGSTIN.setVisibility(View.GONE);
            }*/

            tvTableNumber.setEnabled(false);
            tvTableSplitNo.setEnabled(false);
            tvWaiterNumber.setEnabled(false);

            if (jBillingMode == 1)
            {
                textViewCenter.setText(DineInCaption);
                btnDeleteKOT.setEnabled(false);
                btnPayBill.setEnabled(false);
                btnPrintKOT.setEnabled(true);
                btnPrintBill.setEnabled(false);
                // Table Activity
                if (TableActivity.TABLE_NO.equalsIgnoreCase("") && TableActivity.TABLE_SPLIT_NO.equalsIgnoreCase(""))
                {
                    Close(null);
                }
                else if (TableActivity.TABLE_NO.equalsIgnoreCase("0") && TableActivity.WAITER_NO.equalsIgnoreCase("0"))
                {
                    MsgBox.Show("Warning", "Either Load KOT or Select Table and Waiter");
                }
                else
                {
                    tvTableNumber.setText(getIntent().getStringExtra(TableActivity.TABLE_NO));
                    tvWaiterNumber.setText(getIntent().getStringExtra(TableActivity.WAITER_NO));
                    tvTableSplitNo.setText(getIntent().getStringExtra(TableActivity.TABLE_SPLIT_NO));
                    if (tvTableNumber.getText().toString().equalsIgnoreCase("0") && tvWaiterNumber.getText().toString().equalsIgnoreCase("0"))
                    {
                        MsgBox.Show("Warning", "Either Load KOT or Select Table and Waiter");
                    }
                    /*Cursor LoadKOT = dbBillScreen.getKOTItems(Integer.parseInt(tvTableNumber.getText().toString()), Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
                    if (LoadKOT.moveToFirst())
                    {
                        LoadKOTItems(LoadKOT);
                        btnPayBill.setEnabled(true);
                        btnPrintKOT.setEnabled(true);
                        btnPrintBill.setEnabled(true);
                        MsgBox.Show("Check","LoadKOT encountered in onCreate");
                    }*/

                    Cursor crsrWaiter = dbBillScreen.getUsers(tvWaiterNumber.getText().toString());
                    if (crsrWaiter.moveToFirst())
                    {
                        tvWaiterName.setText(crsrWaiter.getString(crsrWaiter.getColumnIndex("Name")));
                    }
                    else
                    {
                        tvWaiterName.setText("Waiter");
                    }
                }
                // Table Activity
                if (crsrSettings.getInt(crsrSettings.getColumnIndex("TableSpliting")) == 1) {
                    tvTableSplitNo.setVisibility(View.VISIBLE);
                } else {
                    tvTableSplitNo.setVisibility(View.INVISIBLE);
                }

//                if (GSTEnable.equalsIgnoreCase("1")) {
//                    if (HSNEnable_out.equalsIgnoreCase("1")) {
//                        tvHSNCode_out.setVisibility(View.VISIBLE);
//                    } else {
//                        tvHSNCode_out.setVisibility(View.INVISIBLE);
//                    }
//                } else { // no gst
//                    tvHSNCode_out.setVisibility(View.INVISIBLE);
//                }
                Cursor crssOtherChrg = dbBillScreen.getKOTModifierByModes(DineInCaption);
                if (crssOtherChrg.moveToFirst()) {
                    do {
                        dOtherChrgs += crssOtherChrg.getDouble(crssOtherChrg.getColumnIndex("ModifierAmount"));
                    } while (crssOtherChrg.moveToNext());
                    tvOthercharges.setText(String.format("%.2f", dOtherChrgs));
                }
            }

            // Get bill number
            int iBillNumber = db.getNewBillNumber();
            //Log.d("Richa_init : ", String.valueOf(db.getNewBillNumber()));
            tvBillNumber.setText(String.valueOf(iBillNumber));



            chk_interstate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == false) {
                        //et_pos.setBackgroundColor(Color.WHITE);
                        spnr_pos.setSelection(0);
                        spnr_pos.setEnabled(false);
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvCGSTValue.setTextColor(Color.WHITE);
                        tvSGSTValue.setTextColor(Color.WHITE);
                        /*float taxAmount = Float.parseFloat(tvCGSTValue.getText().toString());
                        if (taxAmount <= 0) {
                            tvCGSTValue.setText("0");
                            tvSGSTValue.setText("0");
                        } else {
                            tvCGSTValue.setText(String.valueOf(taxAmount / 2));
                            tvCGSTValue.setText(String.valueOf(taxAmount / 2));
                        }
                        tvSGSTValue.setVisibility(View.VISIBLE);
                        tvServiceTax.setVisibility(View.VISIBLE);
                        */
                    } else {
                        // interstate
                        //et_pos.setBackground(Color.GRAY);
                        spnr_pos.setSelection(0);
                        spnr_pos.setEnabled(true);
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvCGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvSGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

                        /*tvSalesTax.setText("IGST Tax");
                        tvServiceTax.setText("");
                        float taxAmount = Float.parseFloat(tvCGSTValue.getText().toString());
                        if (taxAmount <= 0) {
                            tvCGSTValue.setText("0");
                            tvSGSTValue.setText("");
                        } else {
                            tvCGSTValue.setText(String.valueOf(taxAmount * 2));
                            tvSGSTValue.setText("");

                        }
                        tvSGSTValue.setVisibility(View.INVISIBLE);
                        tvServiceTax.setVisibility(View.INVISIBLE);*/

                    }
                }
            });

            // Table Activity Starts
            tVLabelTableNo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intentDineIn = new Intent(myContext, TableActivity.class);
                    intentDineIn.putExtra("BILLING_MODE", String.valueOf(jBillingMode));
                    intentDineIn.putExtra("USER_ID", strUserId);
                    intentDineIn.putExtra("USER_NAME", strUserName);
                    intentDineIn.putExtra("CUST_ID", 0);
                    startActivity(intentDineIn);

                    finish();
                }
            });
            tVLabelWaiterNo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intentDineIn = new Intent(myContext, TableActivity.class);
                    intentDineIn.putExtra("BILLING_MODE", String.valueOf(jBillingMode));
                    intentDineIn.putExtra("USER_ID", strUserId);
                    intentDineIn.putExtra("USER_NAME", strUserName);
                    intentDineIn.putExtra("CUST_ID", 0);
                    startActivity(intentDineIn);

                    finish();
                }
            });
            // Table Activity Ends

            loadAutoCompleteData();


            /*btndepart.setVisibility(View.INVISIBLE);
            btncateg.setVisibility(View.INVISIBLE);
            btnitem.setVisibility(View.INVISIBLE);*/
            if (!(crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1)) { // reverse tax
                REVERSETAX = true;
            }else
            {
                REVERSETAX = false;
            }
            HSNPRINTENABLED = crsrSettings.getInt(crsrSettings.getColumnIndex("HSNPrintEnabled_out"));
            UTGSTENABLED = crsrSettings.getInt(crsrSettings.getColumnIndex("UTGSTEnabled"));
            if(UTGSTENABLED ==1)
                tvServiceTax_text.setText("UTGST-Tax :");
            else
                tvServiceTax_text.setText("SGST-Tax :");

            fastBillingMode = crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode"));
            if(fastBillingMode == null)
            {
                fastBillingMode = "1";
            }
            if (fastBillingMode.equalsIgnoreCase("1")) {
                boxDept.setVisibility(View.GONE);
                boxCat.setVisibility(View.GONE);
                //btnitem.setVisibility(View.VISIBLE);
            } else if (fastBillingMode.equalsIgnoreCase("2")) {
                boxDept.setVisibility(View.VISIBLE);
                boxCat.setVisibility(View.GONE);
                //btnitem.setVisibility(View.VISIBLE);
            } else if (fastBillingMode.equalsIgnoreCase("3")) {
                boxDept.setVisibility(View.VISIBLE);
                boxCat.setVisibility(View.VISIBLE);
                //btnitem.setVisibility(View.VISIBLE);
            }

            btndepart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if(fastBillingMode.equals("3"))
                        lstvwCategory.setVisibility(View.INVISIBLE);

                    //  gridViewItems.setVisibility(View.INVISIBLE);
                    mRecyclerGridView.setVisibility(View.INVISIBLE);
                    loadDepartments();
                }
            });

            btncateg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    lstvwDepartment.setVisibility(View.INVISIBLE);

                    //  gridViewItems.setVisibility(View.INVISIBLE);
                    mRecyclerGridView.setVisibility(View.INVISIBLE);
                    loadCategories(0);

                }
            });

            btnitem.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    switch (Integer.parseInt(fastBillingMode))
                    {
                        case 3 : lstvwCategory.setVisibility(View.INVISIBLE);
                        case 2 : lstvwDepartment.setVisibility(View.INVISIBLE);
                    }
                    loadItems(0);
                }
            });

            // Load Items without Dept and Categ
            if (crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode")).equalsIgnoreCase("1")) {
                // grdItems.setNumColumns(6);

                mGridLayoutManager = new GridLayoutManager(BillingDineInActivity.this, 6);
                mRecyclerGridView.setLayoutManager(mGridLayoutManager);

                GetItemDetails();
                //tvdeptline.setVisibility(View.GONE);
                //tvcategline.setVisibility(View.GONE);
                boxDept.setVisibility(View.GONE);
                boxCat.setVisibility(View.GONE);


            } else if (crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode")).equalsIgnoreCase("2")) {
                //  grdItems.setNumColumns(4);

                mGridLayoutManager = new GridLayoutManager(BillingDineInActivity.this, 4);
                mRecyclerGridView.setLayoutManager(mGridLayoutManager);

                //GetItemDetailswithoutDeptCateg();
                GetItemDetails();
                //tvcategline.setVisibility(View.GONE);
                boxCat.setVisibility(View.GONE);
            } else {
                //GetItemDetailswithoutDeptCateg();
                mGridLayoutManager = new GridLayoutManager(BillingDineInActivity.this, 2);
                mRecyclerGridView.setLayoutManager(mGridLayoutManager);
                GetItemDetails();
            }

            if (Name.length > 0) {
                // Assign item grid to image adapter
                //   grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));


              /*  mTestImageAdapter = new TestImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1"));
                mRecyclerGridView.setAdapter(mTestImageAdapter);
                mTestImageAdapter.setOnItemClickListener(BillingDineInActivity.this);
                mTestImageAdapter.notifyDataSetChanged();*/

                if (mTestItemsAdapter == null) {
                    mTestItemsAdapter = new TestItemsAdapter(BillingDineInActivity.this, mItemsList);
                    mRecyclerGridView.setAdapter(mTestItemsAdapter);
                    mTestItemsAdapter.setOnItemClickListener(BillingDineInActivity.this);
                } else {
                    mTestItemsAdapter.notifyDataSetChanged(mItemsList);
                }



                // Make the item grid visible
                //    grdItems.setVisibility(View.VISIBLE);
                mRecyclerGridView.setVisibility(View.VISIBLE);
            } else {
                // Make the item grid invisible
                //    grdItems.setVisibility(View.INVISIBLE);
                mRecyclerGridView.setVisibility(View.INVISIBLE);
            }

            // DINE IN BILLING
            if (jBillingMode == 1) {
                if (tvTableNumber.getText().toString().equalsIgnoreCase("") && tvTableSplitNo.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Note", "No KOT's Found");
                } else {
//                            Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
//                    if (LoadKOT.moveToFirst()) {
//                        LoadKOTItems(LoadKOT);
//                    }
                }
            }
        } catch (Exception e) {
            MsgBox.Show("Exception", e.getMessage());
            e.printStackTrace();
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
                mHeadingTextSize = 14;
                mDataMiniDeviceTextsize = 9;

                mItemNameWidth = 140;
                mHSNWidth = 70;
                mQuantityWidth = 65;
                mRateWidth = 65;
                mAmountWidth = 75;

                mItemNameTextView.setTextSize(mHeadingTextSize);
                mHSNTextView.setTextSize(mHeadingTextSize);
                mQuantityTextView.setTextSize(mHeadingTextSize);
                mRateTextView.setTextSize(mHeadingTextSize);
                mAmountTextView.setTextSize(mHeadingTextSize);
                mDeleteTextView.setTextSize(mHeadingTextSize);
                break;

            case mDataMiniScreenResolutionWidth:
                mHeadingTextSize = 14;
                mDataMiniDeviceTextsize = 11;

                mItemNameWidth = 140;
                mHSNWidth = 70;
                mQuantityWidth = 65;
                mRateWidth = 65;
                mAmountWidth = 85;

                mItemNameTextView.setTextSize(mHeadingTextSize);
                mHSNTextView.setTextSize(mHeadingTextSize);
                mQuantityTextView.setTextSize(mHeadingTextSize);
                mRateTextView.setTextSize(mHeadingTextSize);
                mAmountTextView.setTextSize(mHeadingTextSize);
                mDeleteTextView.setTextSize(mHeadingTextSize);
                break;
        }
    }

    void setInvoiceDate()
    {
        Cursor crsrSetting = dbBillScreen.getBillSetting();
        if (crsrSetting.moveToFirst())
        {
            if (crsrSetting.getInt(crsrSetting.getColumnIndex("DateAndTime")) == 1)
            {
                Date date1 = new Date();
                try {
                    CharSequence sdate = DateFormat.format("dd-MM-yyyy", date1.getTime());
                    tvDate.setText(String.valueOf(sdate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                String strDate = crsrSetting.getString(crsrSetting.getColumnIndex("BusinessDate"));
                try {
                    tvDate.setText(String.valueOf(strDate));
                    Date date1 = new Date();
                    CharSequence sdate = DateFormat.format("dd-MM-yyyy", date1.getTime());
                    if(strDate.equals(sdate.toString()))
                        idd_date.setVisibility(View.INVISIBLE);
                    else
                        idd_date.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /*************************************************************************************************************************************
     * Initializes all view handlers present in the billing screen with
     * appropriate controls
     ************************************************************************************************************************************/
    private void IntializeViewVariables() {
        mItemNameTextView = (TextView) findViewById(R.id.tvColItemName);
        mHSNTextView = (TextView) findViewById(R.id.tvColHSN);
        mQuantityTextView = (TextView) findViewById(R.id.tvColItemQty);
        mRateTextView = (TextView) findViewById(R.id.tvColRate);
        mAmountTextView = (TextView) findViewById(R.id.tvColAmount);
        mDeleteTextView = (TextView) findViewById(R.id.tvColDelete);

        tvSalesTax = (TextView) findViewById(R.id.tvTaxTotal);
        tvServiceTax = (TextView) findViewById(R.id.tvServiceTax);
        tvcessValue = (TextView) findViewById(R.id.tvcessValue);
        btndepart = (Button) findViewById(R.id.btn_depart);
        btncateg = (Button) findViewById(R.id.btn_categ);
        btnitem = (Button) findViewById(R.id.btn_item);
        tvdeptline = (TextView) findViewById(R.id.tvdeptline);
        tvcategline = (TextView) findViewById(R.id.tvcategline);
        //txtTotalItems = (EditText) findViewById(R.id.etTotalItems);
        tvHSNCode_out = (TextView) findViewById(R.id.tvColHSN);
        /*txtSearchItemBarcode = (EditText) findViewById(R.id.etSearchItemBarcode);
        txtSearchItemBarcode.setOnKeyListener(Item_Search_Barcode_KeyPressEvent);*/
        rowbtns = (LinearLayout) findViewById(R.id.rowbtns);
        rowtexts = (LinearLayout) findViewById(R.id.rowtexts);
        /*idd3 = (LinearLayout) findViewById(R.id.idd3);
        idd0 = (LinearLayout) findViewById(R.id.idd0);
        idd2 = (LinearLayout) findViewById(R.id.idd2);*/
        tvOthercharges = (TextView) findViewById(R.id.txtOthercharges);
        tvDiscountAmount = (TextView) findViewById(R.id.tvDiscountAmount);
        edtCustId = (EditText) findViewById(R.id.edtCustId);
        edtCustName = (EditText) findViewById(R.id.edtCustName);
        edtCustPhoneNo = (EditText) findViewById(R.id.edtCustPhoneNo);
        edtCustDineInPhoneNo = (EditText) findViewById(R.id.edtCustDineInPhoneNo);
        etCustGSTIN = (TextView) findViewById(R.id.etCustGSTIN);
        edtCustAddress = (EditText) findViewById(R.id.edtCustAddress);
        btnAddCustomer = (Button) findViewById(R.id.btn_DineInAddCustomer);


        tvDiscountPercentage = (TextView) findViewById(R.id.tvDiscountPercentage);
        tvDiscountAmount = (TextView) findViewById(R.id.tvDiscountAmount);

        boxDept = (RelativeLayout) findViewById(R.id.boxDept);
        boxCat = (RelativeLayout) findViewById(R.id.boxCat);
        boxItem = (RelativeLayout) findViewById(R.id.boxItem);

        edtCustDineInPhoneNo.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    if (edtCustDineInPhoneNo.getText().toString().length() == 10) {
                        Cursor crsrCust = dbBillScreen.getCustomer(edtCustDineInPhoneNo.getText().toString());
                        if (crsrCust.moveToFirst()) {
                            //if(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")) == edtCustPhoneNo.getText().toString()) {
                            edtCustId.setText(crsrCust.getString(crsrCust.getColumnIndex("CustId")));
                            edtCustName.setText(crsrCust.getString(crsrCust.getColumnIndex("CustName")));
                            //edtCustPhoneNo.setText(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")));
                            edtCustAddress.setText(crsrCust.getString(crsrCust.getColumnIndex("CustAddress")));
                            String gstin = crsrCust.getString(crsrCust.getColumnIndex("GSTIN"));
                            if (gstin==null)
                                etCustGSTIN.setText("");
                            else
                                etCustGSTIN.setText(gstin);
                            Toast.makeText(myContext, "Customer Fetched Successfully", Toast.LENGTH_LONG).show();
                            checkForInterStateTax();
                            //}
                        } else {
                            MsgBox.Show("Note", "Customer is not Found, Please Add Customer before Order");
                        }
                    } else if (edtCustDineInPhoneNo.getText().toString().trim().equals("")) {
                        chk_interstate.setChecked(false);
                        chk_interstate.setEnabled(true);
                        spnr_pos.setEnabled(false);
                    }
                } catch (Exception ex) {
                    MsgBox.Show("Error", ex.getMessage());
                    ex.printStackTrace();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        idd_date  = (LinearLayout) findViewById(R.id.idd_date);

        tVLabelTableNo = (TextView) findViewById(R.id.textViewLabelTableNo);
        tVLabelWaiterNo = (TextView) findViewById(R.id.textViewLabelWaiterNo);
        tVLabelOrderNo = (TextView) findViewById(R.id.textViewLabelOrderNo);
        relative_Interstate = (LinearLayout) findViewById(R.id.relative_interstate);
        relative_pos = (LinearLayout) findViewById(R.id.relative_pos);

        tvServiceTax_text = (TextView) findViewById(R.id.tvServiceTax);

        tVLabelTableNo.setPaintFlags(tVLabelTableNo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tVLabelWaiterNo.setPaintFlags(tVLabelWaiterNo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // et_pos = (EditText) findViewById(R.id.etpos);
        chk_interstate = (CheckBox) findViewById(R.id.checkbox_interstate);
        //tvUserName = (TextView) findViewById(R.id.tvBillUserName);
        tvDate = (TextView) findViewById(R.id.tvBillDateValue);
        tvBillNumber = (EditText) findViewById(R.id.tvBillNumberValue);
        //tvSubUdfName = (TextView) findViewById(R.id.tvSubUdfText);
        tvSubUdfValue = (EditText) findViewById(R.id.tvSubUdfValue);
        tvTableSplitNo = (EditText) findViewById(R.id.tvTableSplitNoValue);
        tvWaiterName = (EditText) findViewById(R.id.tvWaiterName);
        tvWaiterNumber = (EditText) findViewById(R.id.tvWaiterNoValue);
        tvTableNumber = (EditText) findViewById(R.id.tvTableNoValue);
        tvSubTotal = (TextView) findViewById(R.id.tvSubTotalValue);
        tvCGSTValue = (TextView) findViewById(R.id.tvTaxTotalValue);
        tvIGSTValue = (TextView) findViewById(R.id.tvIGSTValue);
        tvSGSTValue = (TextView) findViewById(R.id.tvServiceTaxValue);
        tvBillAmount = (TextView) findViewById(R.id.tvBillTotalValue);

        lstvwDepartment = (ListView) findViewById(R.id.lstDepartmentNames);
        lstvwDepartment.setOnItemClickListener(deptClick);
        lstvwCategory = (ListView) findViewById(R.id.lstCategoryNames);
        lstvwCategory.setOnItemClickListener(catClick);

    /*
        grdItems = (GridView) findViewById(R.id.gridItems);
        grdItems.setOnItemClickListener(GridItemImageClick);*/


        mRecyclerGridView = (RecyclerView) findViewById(R.id.gridItems);
        mRecyclerGridView.setHasFixedSize(true);

        //btnPayCash = (Button) findViewById(R.id.btn_PayCash);

        btnDeleteBill = (WepButton) findViewById(R.id.btn_DeleteBill);
        btnPayBill = (WepButton) findViewById(R.id.btn_PayBill);
        btnShiftTable = (WepButton) findViewById(R.id.btn_ShiftTable);
        btnMergeTable = (WepButton) findViewById(R.id.btn_MergeTable);
        btnPreviewBill = (WepButton) findViewById(R.id.btn_PreviewBill);
        btnModifier = (WepButton) findViewById(R.id.btn_Modifier);
        btn_ReprintKOT = (WepButton) findViewById(R.id.btn_ReprintKOT);
        btnSaveKOT = (WepButton) findViewById(R.id.btn_SaveKOT);
        //btnTable = (Button) findViewById(R.id.btn_TableNo);
        //btnWaiter = (Button) findViewById(R.id.btn_WaiterNo);
        btnLoadKOT = (WepButton) findViewById(R.id.btn_LoadKOT);
        btnDeleteKOT = (WepButton) findViewById(R.id.btn_DeleteKOT);
        btnKOTStatus = (WepButton) findViewById(R.id.btn_KOTStatus);
        btnPrintKOT = (WepButton) findViewById(R.id.btn_PrintKOT);
        btnPrintBill = (WepButton) findViewById(R.id.btn_PrintBill);
        btnClear = (WepButton) findViewById(R.id.btn_Clear);
        btnReprint = (WepButton) findViewById(R.id.btn_Reprint);
        tblOrderItems = (TableLayout) findViewById(R.id.tblOrderItems);

        btnLoadKOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadKOT(v);
            }
        });
        btnModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyKOT(v);
            }
        });
        btn_ReprintKOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReprintKOT(v);
            }
        });
        btnDeleteKOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoidKOT(v);
            }
        });
        btnShiftTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShiftTable(v);
            }
        });
        btnMergeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MergeTable(v);
            }
        });
        btnPreviewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewBill(v);
            }
        });
        btnSaveKOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveKOT(v);
            }
        });
        btnPrintKOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printKOT(v);
            }
        });
        btnPrintBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printBILL(v);
            }
        });
        btnPayBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayBill(v);
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear(v);
            }
        });
        btnDeleteBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteBill(v);
            }
        });
        btnReprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReprintBill(v);
            }
        });
        btnKOTStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KOTStatus(v);
            }
        });



        aTViewSearchItem = (AutoCompleteTextView) findViewById(R.id.aCTVSearchItem);
        aTViewSearchItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    /*Toast.makeText(BillingDineInActivity.this, aTViewSearchItem.getText().toString(),
                            Toast.LENGTH_SHORT).show();*/
                    if (tvTableNumber.getText().toString().equalsIgnoreCase("")
                            || tvWaiterNumber.getText().toString().equalsIgnoreCase("")
                            || tvTableNumber.getText().toString().equalsIgnoreCase("0")
                            || tvWaiterNumber.getText().toString().equalsIgnoreCase("0"))
                    {
                        Toast.makeText(myContext, "Select waiter and table before adding item to bill",Toast.LENGTH_LONG).show();

                        //Table_Waiter(null);
                        Intent intentDineIn = new Intent(myContext, TableActivity.class);
                        intentDineIn.putExtra("BILLING_MODE", String.valueOf(jBillingMode));
                        intentDineIn.putExtra("USER_ID", strUserId);//spUser.getString("USER_ID", "GHOST"));
                        intentDineIn.putExtra("USER_NAME", strUserName);//spUser.getString("USER_NAME", "GHOST"));
                        intentDineIn.putExtra("CUST_ID", 0);
                        startActivity(intentDineIn);
                        finish();
                        return;
                    }else if ((aTViewSearchItem.getText().toString().equals(""))) {
                        MsgBox.Show("Warning", "Enter Item Name");
                    } else {
                        //Cursor MenucodeItem = dbBillScreen.getItemList(aTViewSearchItem.getText().toString().trim());
                        Cursor MenucodeItem = db.getItemDetail(aTViewSearchItem.getText().toString().trim());
                        if (MenucodeItem.moveToFirst()) {
                            btnClear.setEnabled(true);
                            AddItemToOrderTable(MenucodeItem);
                            aTViewSearchItem.setText("");
                            // ((EditText)v).setText("");
                        } else {
                            MsgBox.Show("Warning", "Item not found for Selected Item");
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        spnr_pos = (Spinner) findViewById(R.id.spnr_pos);

        aTViewSearchMenuCode = (AutoCompleteTextView) findViewById(R.id.aCTVSearchMenuCode);
        aTViewSearchMenuCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    /*Toast.makeText(BillingDineInActivity.this, aTViewSearchMenuCode.getText().toString(),
                            Toast.LENGTH_SHORT).show();*/
                    if (tvTableNumber.getText().toString().equalsIgnoreCase("")
                            || tvWaiterNumber.getText().toString().equalsIgnoreCase("")
                            || tvTableNumber.getText().toString().equalsIgnoreCase("0")
                            || tvWaiterNumber.getText().toString().equalsIgnoreCase("0"))
                    {
                        Toast.makeText(myContext, "Select waiter and table before adding item to bill",Toast.LENGTH_LONG).show();

                        //Table_Waiter(null);
                        Intent intentDineIn = new Intent(myContext, TableActivity.class);
                        intentDineIn.putExtra("BILLING_MODE", String.valueOf(jBillingMode));
                        intentDineIn.putExtra("USER_ID", strUserId);//spUser.getString("USER_ID", "GHOST"));
                        intentDineIn.putExtra("USER_NAME", strUserName);//spUser.getString("USER_NAME", "GHOST"));
                        intentDineIn.putExtra("CUST_ID", 0);
                        startActivity(intentDineIn);
                        finish();
                        return;
                    }else if ((aTViewSearchMenuCode.getText().toString().equals(""))) {
                        MsgBox.Show("Warning", "Enter Menu Code");
                    } else {
                        Cursor MenucodeItem = dbBillScreen
                                .getItem(Integer.parseInt(aTViewSearchMenuCode.getText().toString().trim()));
                        if (MenucodeItem.moveToFirst()) {
                            btnClear.setEnabled(true);
                            AddItemToOrderTable(MenucodeItem);
                            aTViewSearchMenuCode.setText("");
                            // ((EditText)v).setText("");
                        } else {
                            MsgBox.Show("Warning", "Item not found for Selected Item Code");
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        autoCompleteTextViewSearchItemBarcode = (AutoCompleteTextView) findViewById(R.id.aCTVSearchItemBarcode);
        autoCompleteTextViewSearchItemBarcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                                        /*Toast.makeText(BillingScreenActivity.this, aTViewSearchMenuCode.getText().toString(),
                            Toast.LENGTH_SHORT).show();*/
                    if ((autoCompleteTextViewSearchItemBarcode.getText().toString().equals(""))) {
                        MsgBox.Show("Warning", "Scan BarCode");
                    } else {
                        Cursor BarcodeItem = db
                                .getItemssbyBarCode((autoCompleteTextViewSearchItemBarcode.getText().toString().trim()));
                        int i =0, added =0;
                        while (BarcodeItem.moveToNext()) {
                            if(i!=position)
                            {
                                i++;
                                continue;
                            }
                            added =1;
                            btnClear.setEnabled(true);
                            AddItemToOrderTable(BarcodeItem);
                            autoCompleteTextViewSearchItemBarcode.setText("");
                            // ((EditText)v).setText("");
                        } if(added==0) {
                            MsgBox.Show("Warning", "Item not found for Selected BarCode");
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        etCustGSTIN.addTextChangedListener(this);
        edtCustName.addTextChangedListener(this);
        edtCustDineInPhoneNo.addTextChangedListener(this);
        edtCustAddress.addTextChangedListener(this);
        aTViewSearchItem.addTextChangedListener(this);
        aTViewSearchMenuCode.addTextChangedListener(this);
        autoCompleteTextViewSearchItemBarcode.addTextChangedListener(this);


    }
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String str = charSequence.toString();
        View view = getCurrentFocus();
        if(view== null)
            return;
        switch(view.getId()){
            case R.id.etCustGSTIN:
                tx = etCustGSTIN.getText().toString();
                break;
            case R.id.edtCustName:
                tx = edtCustName.getText().toString();
                break;
            case R.id.edtCustDineInPhoneNo:
                tx = edtCustDineInPhoneNo.getText().toString();
                break;
            case R.id.edtCustAddress:
                tx = edtCustAddress.getText().toString();
                break;
            case R.id.aCTVSearchItem:
                tx = aTViewSearchItem.getText().toString();
                break;
            case R.id.aCTVSearchMenuCode:
                tx = aTViewSearchMenuCode.getText().toString();
                break;
            case R.id.aCTVSearchItemBarcode:
                tx = autoCompleteTextViewSearchItemBarcode.getText().toString();
                break;

        }


    }
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    public void afterTextChanged(Editable s) {
        try {
            View view = getCurrentFocus();
            if(view== null  || CUSTOMER_FOUND==1)
                return;
            if(view.getId() == R.id.edtCustPhoneNo){
                try {
                    if (edtCustDineInPhoneNo.getText().toString().length() == 10) {
                        Cursor crsrCust = dbBillScreen.getCustomer(edtCustDineInPhoneNo.getText().toString());
                        if (crsrCust.moveToFirst()) {
                            CUSTOMER_FOUND=1;
                            //if(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")) == edtCustPhoneNo.getText().toString()) {
                            edtCustId.setText(crsrCust.getString(crsrCust.getColumnIndex("CustId")));
                            edtCustName.setText(crsrCust.getString(crsrCust.getColumnIndex("CustName")));
                            //edtCustPhoneNo.setText(crsrCust.getString(crsrCust.getColumnIndex("CustContactNumber")));
                            edtCustAddress.setText(crsrCust.getString(crsrCust.getColumnIndex("CustAddress")));
                            String gstin = crsrCust.getString(crsrCust.getColumnIndex("GSTIN"));
                            if (gstin==null)
                                etCustGSTIN.setText("");
                            else
                                etCustGSTIN.setText(gstin);
                            Toast.makeText(myContext, "Customer Fetched Successfully", Toast.LENGTH_LONG).show();
                            //}
                        } else {
                            MsgBox.Show("Note ", "Customer is not Found, Please Add Customer before Order");
                        }
                    } else if (edtCustPhoneNo.getText().toString().trim().equals("")){
                        CUSTOMER_FOUND=1;
                        edtCustName.setText("");
                        edtCustId.setText("0");
                        edtCustAddress.setText("");
                        etCustGSTIN.setText("");
                        CUSTOMER_FOUND=0;

                    }
                } catch (Exception ex) {
                    MsgBox.Show("Error", ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }catch (Exception ex) {
            MsgBox.Show("Error " + ex.toString(), ex.getMessage());

        }
    }

    private void checkForInterStateTax()
    {
        String custGStin = etCustGSTIN.getText().toString().trim();

        if (custGStin != null && custGStin.length() == 15)
        {

            String GSTCustomerSateCode = custGStin.substring(0, 2);
            Cursor   ownerCursor = db.getOwnerDetail();
            String ownerGSTIN = "";
            if(ownerCursor !=null && ownerCursor.moveToFirst())
            {
                ownerGSTIN = ownerCursor.getString(ownerCursor.getColumnIndex("GSTIN"));
            }

            if (GSTCustomerSateCode.equals(ownerGSTIN.substring(0, 2))) {
                chk_interstate.setChecked(false);
                spnr_pos.setSelection(0);
            } else
            {
                chk_interstate.setChecked(true);
                String stateName = "";
                spnr_pos.setSelection(getIndex_pos(GSTCustomerSateCode));

            }
            chk_interstate.setEnabled(false);
            spnr_pos.setEnabled(false);

        } else {
            chk_interstate.setChecked(false);
            spnr_pos.setSelection(0);
        }
    }


    /*************************************************************************************************************************************
     * Department list click listener which loads the items of selected
     * department to Category list
     *************************************************************************************************************************************/
//    AdapterView.OnItemClickListener DepartmentListClick = new AdapterView.OnItemClickListener() {
//
//        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
//
//            lstvwCategory.setVisibility(View.VISIBLE);
//            Cursor Category = dbBillScreen.getCatItems(position + 1);
//            // Load Category to Category List
//            LoadCategories(Category);
//        }
//
//    };

    /*************************************************************************************************************************************
     * Category list click listener which loads the items of selected Category
     * to item grid
     *************************************************************************************************************************************/
//    AdapterView.OnItemClickListener CategoryListClick = new AdapterView.OnItemClickListener() {
//
//        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
//            // TODO Auto-generated method stub
//            GetItemDetails(position + 1);
//            // This condition is to avoid NullReferenceException in getCount()
//            // in ImageAdapter class.
//            if (Name.length > 0) {
//                // Assign item grid to image adapter
//             //   grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
//                mTestImageAdapter = new TestImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1"));
//                        mRecyclerGridView.setAdapter(mTestImageAdapter);
//                        mTestImageAdapter.setOnItemClickListener(BillingDineInActivity.this);
    // mTestImageAdapter.notifyDataSetChanged();
//                // Make the item grid visible
//             //   grdItems.setVisibility(View.VISIBLE);
//                mRecyclerGridView.setVisibility(View.VISIBLE);
//            } else {
//                // Make the item grid invisible
//              //  grdItems.setVisibility(View.INVISIBLE);
//                mRecyclerGridView.setVisibility(View.INVISIBLE);
//            }
//        }
//
//    };

   /* *//*************************************************************************************************************************************
     * Item grid image click listener which loads the item to billing grid if
     * selected item is not present otherwise increments the quantity by one
     *************************************************************************************************************************************//*
    AdapterView.OnItemClickListener GridItemImageClick = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapter, View v, int position1, long id1) {
            Cursor Item = null;
            if (v.getTag() != null)
            {
                if (jBillingMode == Byte.parseByte("1"))
                {
                    if (tvTableNumber.getText().toString().equalsIgnoreCase("")
                            || tvWaiterNumber.getText().toString().equalsIgnoreCase("")
                            || tvTableNumber.getText().toString().equalsIgnoreCase("0")
                            || tvWaiterNumber.getText().toString().equalsIgnoreCase("0"))
                    {
                        Toast.makeText(myContext, "Select waiter and table before adding item to bill",Toast.LENGTH_LONG).show();

                        //Table_Waiter(null);
                        Intent intentDineIn = new Intent(myContext, TableActivity.class);
                        intentDineIn.putExtra("BILLING_MODE", String.valueOf(jBillingMode));
                        intentDineIn.putExtra("USER_ID", strUserId);//spUser.getString("USER_ID", "GHOST"));
                        intentDineIn.putExtra("USER_NAME", strUserName);//spUser.getString("USER_NAME", "GHOST"));
                        intentDineIn.putExtra("CUST_ID", 0);
                        startActivity(intentDineIn);
                        finish();
                        return;
                    }
                    else
                    {
                        Item = dbBillScreen.getItem(Integer.parseInt(v.getTag().toString()));
                        btnClear.setEnabled(true);
                        AddItemToOrderTable(Item);
                    }
                }

            }
        }
    };*/

    /*************************************************************************************************************************************
     * Modifier list click listener which adds the modifier amount to selected
     * item from the billing table
     *************************************************************************************************************************************/
    AdapterView.OnItemClickListener ModifierListClick = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
            // TODO Auto-generated method stub
            Cursor crsrModifier = null;
            crsrModifier = dbBillScreen.getKOTModifier(position + 1);

            AddModifier(crsrModifier);

        }

    };

    /*************************************************************************************************************************************
     * Menu code item search key-press / key-listener handler which loads the
     * item to billing grid if selected item is not present otherwise increments
     * the quantity by one
     *************************************************************************************************************************************/
//    View.OnKeyListener Item_Search_MenuCode_KeyPressEvent = new View.OnKeyListener() {
//
//        public boolean onKey(View v, int keyCode, KeyEvent event) {
//            // TODO Auto-generated method stub
//
//            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(((EditText) v).getWindowToken(), 0);
//
//                if (((EditText) v).getText().toString().equalsIgnoreCase("")) {
//                    MsgBox.Show("Warning", "Enter item menu code");
//                } else {
//                    Cursor MenucodeItem = dbBillScreen.getItem(Integer.parseInt(((EditText) v).getText().toString()));
//                    if (MenucodeItem.moveToFirst()) {
//                        btnClear.setEnabled(true);
//                        AddItemToOrderTable(MenucodeItem);
//                        // ((EditText)v).setText("");
//                        return true;
//                    } else {
//                        MsgBox.Show("Warning", "Item not found for given menu code");
//                    }
//                }
//            }
//            // ((EditText)v).setText("");
//            return false;
//        }
//    };

    /*************************************************************************************************************************************
     * Item item search key-press / key-listener handler which loads the item to
     * billing grid if selected item is not present otherwise increments the
     * quantity by one
     *************************************************************************************************************************************/
//    View.OnKeyListener Item_Search_Itemname_ClickEvent = new View.OnKeyListener() {
//
//        public boolean onKey(View v, int keyCode, KeyEvent event) {
//            // TODO Auto-generated method stub
//
//            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(((EditText) v).getWindowToken(), 0);
//
//                if (((EditText) v).getText().toString().equalsIgnoreCase("")) {
//                    MsgBox.Show("Warning", "Enter item menu code");
//                } else {
//                    Cursor MenucodeItem = dbBillScreen.getItem(Integer.parseInt(((EditText) v).getText().toString()));
//                    if (MenucodeItem.moveToFirst()) {
//                        btnClear.setEnabled(true);
//                        AddItemToOrderTable(MenucodeItem);
//                        // ((EditText)v).setText("");
//                        return true;
//                    } else {
//                        MsgBox.Show("Warning", "Item not found for given menu code");
//                    }
//                }
//            }
//            // ((EditText)v).setText("");
//            return false;
//        }
//    };

    /*************************************************************************************************************************************
     * Bar-code item search key-press / key-listener handler which loads the
     * item to billing grid if selected item is not present otherwise increments
     * the quantity by one
     ************************************************************************************************************************************/
    View.OnKeyListener Item_Search_Barcode_KeyPressEvent = new View.OnKeyListener() {

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub

            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) v).getWindowToken(), 0);

                if (((EditText) v).getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Scan item barcode");
                } else {
                    Cursor BarcodeItem = dbBillScreen.getItem(((EditText) v).getText().toString());
                    if (BarcodeItem.moveToFirst()) {
                        btnClear.setEnabled(true);
                        AddItemToOrderTable(BarcodeItem);
                        // ((EditText)v).setText("");
                        return true;
                    } else {
                        MsgBox.Show("Warning", "Item not found for the above barcode");
                    }
                }
            }
            // ((EditText)v).setText("");
            return false;
        }
    };

    /*************************************************************************************************************************************
     * Quantity / Rate column text box click event listener which selects all
     * the text present in text box
     *************************************************************************************************************************************/
    View.OnClickListener Qty_Rate_Click = new View.OnClickListener() {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            // ((EditText) v).setSelection(((EditText) v).getText().length());
        }

    };

    /*************************************************************************************************************************************
     * Quantity / Rate column text key press event listener
     *************************************************************************************************************************************/
    View.OnKeyListener Qty_Rate_KeyPressEvent = new View.OnKeyListener() {

        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (v.getTag().toString().equalsIgnoreCase("QTY_RATE"))
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                    return false;
                if ( event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(((EditText) v).getWindowToken(), 0);
                    Qty_Rate_Edit();
                }else if ((event.getKeyCode() >= KeyEvent.KEYCODE_0 &&event.getKeyCode()  <= KeyEvent.KEYCODE_9  ) )
                {
                    int startSelection=((EditText)v).getSelectionStart();
                    int endSelection=((EditText)v).getSelectionEnd();
                    System.out.println("StartSelection "+startSelection);
                    System.out.println("EndSelection "+endSelection);
                    if(startSelection == 0 && endSelection == 0) {
                        ((EditText) v).setText(String.valueOf(event.getNumber()));
                        ((EditText) v).setSelection(((EditText) v).getText().length());
                    }else
                    {

                    }
                }
            }
            return false;
        }
    };

    /*************************************************************************************************************************************
     * Loads all the departments to department list
     *************************************************************************************************************************************/
   /* private void LoadDepartments(Cursor crsrDept) {

        Cursor cursor = dbBillScreen.getDepartments();
        String columns[] = new String[]{"_id", "DeptName"};
        int vals[] = new int[]{R.id.tvlstDeptCode, R.id.tvlstDeptName};
        deptdataAdapter = new SimpleCursorAdapter(this, R.layout.activity_deptnames, cursor, columns, vals);

        lstvwDepartment.setVisibility(View.VISIBLE);
        lstvwDepartment.setAdapter(deptdataAdapter);
        lstvwDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                String deptcode = ((TextView) view.findViewById(R.id.tvlstDeptCode)).getText().toString();

                Cursor Category = dbBillScreen.getCategoryItems(Integer.valueOf(deptcode));
                // Load Category to Category List
                if (Category.moveToFirst()) {
                    lstvwCategory.setVisibility(View.VISIBLE);
                    LoadCategories(Category);
                    //grdItems.setAdapter(null);
                    //mRecyclerGridView.setAdapter(null);
                    GetItemDetailsByDept(Integer.valueOf(deptcode));
                    if (Name.length > 0) {
                        // Assign item grid to image adapter
                        // grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));

                    *//*
                        mTestImageAdapter = new TestImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1"));
                        mRecyclerGridView.setAdapter(mTestImageAdapter);
                        mTestImageAdapter.setOnItemClickListener(BillingDineInActivity.this);
                        mTestImageAdapter.notifyDataSetChanged();*//*


                        if (mTestItemsAdapter == null) {
                            mTestItemsAdapter = new TestItemsAdapter(BillingDineInActivity.this, mItemsList);
                            mRecyclerGridView.setAdapter(mTestItemsAdapter);
                            mTestItemsAdapter.setOnItemClickListener(BillingDineInActivity.this);
                        } else {
                            mTestItemsAdapter.notifyDataSetChanged(mItemsList);
                        }


                        // Make the item grid visible
                        // grdItems.setVisibility(View.VISIBLE);
                        mRecyclerGridView.setVisibility(View.VISIBLE);
                    } else {
                        // Make the item grid invisible
                        // grdItems.setVisibility(View.INVISIBLE);
                        mRecyclerGridView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    lstvwCategory.setAdapter(null);
                    //MsgBox.Show("","Items");
                    GetItemDetailsByDept(Integer.valueOf(deptcode));//, Integer.valueOf(categdeptcode));
                    // This condition is to avoid NullReferenceException in getCount()
                    // in ImageAdapter class.
                    if (Name.length > 0) {
                        // Assign item grid to image adapter
                        //   grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));


                      *//*  mTestImageAdapter = new TestImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1"));
                        mRecyclerGridView.setAdapter(mTestImageAdapter);
                        mTestImageAdapter.setOnItemClickListener(BillingDineInActivity.this);
                        mTestImageAdapter.notifyDataSetChanged();*//*

                        if (mTestItemsAdapter == null) {
                            mTestItemsAdapter = new TestItemsAdapter(BillingDineInActivity.this, mItemsList);
                            mRecyclerGridView.setAdapter(mTestItemsAdapter);
                            mTestItemsAdapter.setOnItemClickListener(BillingDineInActivity.this);
                        } else {
                            mTestItemsAdapter.notifyDataSetChanged(mItemsList);
                        }



                        // Make the item grid visible
                        //      grdItems.setVisibility(View.VISIBLE);
                        mRecyclerGridView.setVisibility(View.VISIBLE);
                    } else {
                        // Make the item grid invisible
                        //     grdItems.setVisibility(View.INVISIBLE);
                        mRecyclerGridView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
*/
    private void loadDepartments() {
        new AsyncTask<Void, Void, ArrayList<Department>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<Department> doInBackground(Void... params) {
                return db.getItemDepartment();
            }

            @Override
            protected void onPostExecute(ArrayList<Department> list) {
                super.onPostExecute(list);
                //tvBillNumber.setText(String.valueOf(db.getNewBillNumber()));
                if(list!=null)
                    setDepartmentAdapter(list);
                lstvwDepartment.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    private void loadCategories(final int deptCode) {
        new AsyncTask<Void, Void, ArrayList<Category>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<Category> doInBackground(Void... params) {
                if(deptCode == 0)
                    return db.getAllItemCategory();
                else
                    return db.getAllItemCategory(deptCode);

            }

            @Override
            protected void onPostExecute(ArrayList<Category> list) {
                super.onPostExecute(list);
                //tvBillNumber.setText(String.valueOf(db.getNewBillNumber()));
                if(list!=null)
                    setCategoryAdapter(list);
                lstvwCategory.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    public void setDepartmentAdapter(ArrayList<Department> list)
    {
        if(departmentAdapter==null){
            departmentAdapter = new DepartmentAdapter(this,list);
            lstvwDepartment.setAdapter(departmentAdapter);
        }
        else
            departmentAdapter.notifyDataSetChanged(list);
    }

    public void setCategoryAdapter(ArrayList<Category> list)
    {
        if(categoryAdapter==null){
            categoryAdapter = new CategoryAdapter(this,list);
            lstvwCategory.setAdapter(categoryAdapter);
        }
        else
            categoryAdapter.notifyDataSetChanged(list);
    }

    public void setItemsAdapter(ArrayList<Items> list)
    {
        if (mTestItemsAdapter == null) {
            mTestItemsAdapter = new TestItemsAdapter(this, list);

            mTestItemsAdapter.setOnItemClickListener(new TestItemsAdapter.OnItemsImageClickListener() {
                @Override
                public void onItemClick(int position, int itemCode, View v) {
                    Cursor cursor = db.getItemss(itemCode);
                    btnClear.setEnabled(true);
                    AddItemToOrderTable(cursor);
                }
            });
            mRecyclerGridView.setAdapter(mTestItemsAdapter);

        } else
            mTestItemsAdapter.notifyDataSetChanged(list);
    }

    private AdapterView.OnItemClickListener deptClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Department department = (Department) departmentAdapter.getItem(position);
            int deptCode = department.getDeptCode();
            if(fastBillingMode.equals("3"))// dept+cat+items
            {
                loadCategories(deptCode);
            }
            loadItems_for_dept(deptCode);


        }
    };

    private AdapterView.OnItemClickListener catClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Category cat = (Category) categoryAdapter.getItem(position);
            int categcode = cat.getCategCode();
            loadItems(categcode);

        }
    };

    private void loadItems_for_dept(final int deptCode) {
        new AsyncTask<Void, Void, ArrayList<Items>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<Items> doInBackground(Void... params) {
                ArrayList<Items> list = null;
                try {
                    list =  db.getItemItems_dept(deptCode);
                } catch (Exception e) {
                    list = null;
                }
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<Items> list) {
                super.onPostExecute(list);
                // tvBillNumber.setText(String.valueOf(db.getNewBillNumber()));
                if(list!=null)
                    setItemsAdapter(list);
                //   gridViewItems.setVisibility(View.VISIBLE);
                mRecyclerGridView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }
    private void loadItems(final int categcode) {
        new AsyncTask<Void, Void, ArrayList<Items>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<Items> doInBackground(Void... params) {
                if(categcode == 0)
                    return db.getItemItems();
                else
                    return db.getItemItems(categcode);
            }

            @Override
            protected void onPostExecute(ArrayList<Items> list) {
                super.onPostExecute(list);
                //tvBillNumber.setText(String.valueOf(db.getNewBillNumber()));
                if(list!=null)
                    setItemsAdapter(list);
                //    gridViewItems.setVisibility(View.VISIBLE);
                mRecyclerGridView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    /*private void LoadDepartmentsItems(Cursor crsrDept) {

        Cursor cursor = dbBillScreen.getDepartments();
        String columns[] = new String[]{"_id", "DeptName"};
        int vals[] = new int[]{R.id.tvlstDeptCode, R.id.tvlstDeptName};
        deptdataAdapter = new SimpleCursorAdapter(this, R.layout.activity_deptnames, cursor, columns, vals);

        lstvwDepartment.setAdapter(deptdataAdapter);
        lstvwDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                //String val = o.get("id");
                        String deptcode = ((TextView) view.findViewById(R.id.tvlstDeptCode)).getText().toString();

                lstvwCategory.setAdapter(null);
                //MsgBox.Show("","Items");
                GetItemDetailsByDept(Integer.valueOf(deptcode));//, Integer.valueOf(categdeptcode));
                // This condition is to avoid NullReferenceException in getCount()
                // in ImageAdapter class.
                if (Name.length > 0) {
                    // Assign item grid to image adapter
                    //     grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));


                 *//*   mTestImageAdapter = new TestImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1"));
                    mRecyclerGridView.setAdapter(mTestImageAdapter);
                    mTestImageAdapter.setOnItemClickListener(BillingDineInActivity.this);
                    mTestImageAdapter.notifyDataSetChanged();*//*


                    if (mTestItemsAdapter == null) {
                        mTestItemsAdapter = new TestItemsAdapter(BillingDineInActivity.this, mItemsList);
                        mRecyclerGridView.setAdapter(mTestItemsAdapter);
                        mTestItemsAdapter.setOnItemClickListener(BillingDineInActivity.this);
                    } else {
                        mTestItemsAdapter.notifyDataSetChanged(mItemsList);
                    }


                    // Make the item grid visible
                    // grdItems.setVisibility(View.VISIBLE);
                    mRecyclerGridView.setVisibility(View.VISIBLE);
                } else {
                    // Make the item grid invisible
                    //    grdItems.setVisibility(View.INVISIBLE);
                    mRecyclerGridView.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    private void LoadCategories(Cursor crsrCateg) {

        //Cursor cursor = crsrCateg;

        String columns[] = new String[]{"_id", "CategName", "DeptCode"};
        int vals[] = new int[]{R.id.tvlstCategCode, R.id.tvlstCategName, R.id.tvlstCategDeptCode};
        categdataAdapter = new SimpleCursorAdapter(this, R.layout.activity_categnames, crsrCateg, columns, vals);

        lstvwCategory.setAdapter(categdataAdapter);
        lstvwCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")

                String categcode = ((TextView) view.findViewById(R.id.tvlstCategCode)).getText().toString();
                String categdeptcode = ((TextView) view.findViewById(R.id.tvlstCategDeptCode)).getText().toString();

//                Toast.makeText(myContext, message, Toast.LENGTH_LONG).show();
                //   grdItems.setVisibility(View.VISIBLE);
                mRecyclerGridView.setVisibility(View.VISIBLE);
                GetItemDetails(Integer.valueOf(categcode));//, Integer.valueOf(categdeptcode));
                // This condition is to avoid NullReferenceException in getCount()
                // in ImageAdapter class.
                if (Name.length > 0) {
                    // Assign item grid to image adapter
                    //   grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));

            *//*
                    mTestImageAdapter = new TestImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1"));
                    mRecyclerGridView.setAdapter(mTestImageAdapter);
                    mTestImageAdapter.setOnItemClickListener(BillingDineInActivity.this);
                    mTestImageAdapter.notifyDataSetChanged();*//*


                    if (mTestItemsAdapter == null) {
                        mTestItemsAdapter = new TestItemsAdapter(BillingDineInActivity.this, mItemsList);
                        mRecyclerGridView.setAdapter(mTestItemsAdapter);
                        mTestItemsAdapter.setOnItemClickListener(BillingDineInActivity.this);
                    } else {
                        mTestItemsAdapter.notifyDataSetChanged(mItemsList);
                    }


                    // Make the item grid visible
                    //     grdItems.setVisibility(View.VISIBLE);
                    mRecyclerGridView.setVisibility(View.VISIBLE);
                } else {
                    // Make the item grid invisible
                    //   grdItems.setVisibility(View.INVISIBLE);
                    mRecyclerGridView.setVisibility(View.INVISIBLE);
                }

            }
        });
    }*/

    /*************************************************************************************************************************************
     * Loads all the KOT modifiers to list
     *************************************************************************************************************************************/
//    private void LoadKOTModifiers() {
//        Cursor crsrKOTModifiers = null;
//        crsrKOTModifiers = dbBillScreen.getAllKOTModifier();
//        if (crsrKOTModifiers.moveToFirst()) {
//
//            adapModifiers = new ArrayAdapter<String>(myContext, android.R.layout.simple_list_item_multiple_choice);
//            lstvwKOTModifiers.setAdapter(adapModifiers);
//            lstvwKOTModifiers.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
//
//            do {
//                adapModifiers.add(crsrKOTModifiers.getString(crsrKOTModifiers.getColumnIndex("ModifierDescription")));
//
//            } while (crsrKOTModifiers.moveToNext());
//
//        } else {
//            MsgBox.Show("Warning", "No KOT Modifiers found");
//        }
//    }

    /*************************************************************************************************************************************
     * Retrieves Item details such as MenuCode, Name, ImageUri to display in
     * Item image grid
     *
     * @param : Item details will be returned based on Department Code
     *          parameter
     ************************************************************************************************************************************/
    // Get Items by CategCode
    private void GetItemDetails() {
        Cursor Items = null;
        Items = dbBillScreen.getAllItems();
        //Items = dbBillScreen.getAllItemsWithoutDeptCateg();
        if (Items.moveToFirst()) {

            Name = new String[Items.getCount()];
            ImageUri = new String[Items.getCount()];
            MenuCode = new int[Items.getCount()];
            mItemsList.clear();

            do {
                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));

                Items items = new Items(Items.getString(Items.getColumnIndex("ItemName"))
                        , Items.getString(Items.getColumnIndex("ImageUri"))
                        , Items.getInt(Items.getColumnIndex("MenuCode")));
                mItemsList.add(items);

            } while (Items.moveToNext());

        } else {
            Name = new String[0];
            ImageUri = new String[0];
            MenuCode = new int[0];
        }
    }

    private void GetItemDetailswithoutDeptCateg() {
        Cursor Items = null;
        //Items = dbBillScreen.getAllItems();
        Items = dbBillScreen.getAllItemsWithoutDeptCateg();
        if (Items.moveToFirst()) {

            Name = new String[Items.getCount()];
            ImageUri = new String[Items.getCount()];
            MenuCode = new int[Items.getCount()];
            mItemsList.clear();

            do {
                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));

                Items items = new Items(Items.getString(Items.getColumnIndex("ItemName"))
                        , Items.getString(Items.getColumnIndex("ImageUri"))
                        , Items.getInt(Items.getColumnIndex("MenuCode")));
                mItemsList.add(items);

            } while (Items.moveToNext());

        } else {
            Name = new String[0];
            ImageUri = new String[0];
            MenuCode = new int[0];
        }
    }

    // Get Items by CategCode
    private void GetItemDetails(int iCategCode) {
        Cursor Items = null;
        Items = dbBillScreen.getCatbyItems(iCategCode);
        if (Items.moveToFirst()) {

            Name = new String[Items.getCount()];
            ImageUri = new String[Items.getCount()];
            MenuCode = new int[Items.getCount()];
            mItemsList.clear();

            do {
                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));

                Items items = new Items(Items.getString(Items.getColumnIndex("ItemName"))
                        , Items.getString(Items.getColumnIndex("ImageUri"))
                        , Items.getInt(Items.getColumnIndex("MenuCode")));
                mItemsList.add(items);

            } while (Items.moveToNext());

        } else {

            Log.d("LoadItemsToGrid", "No Items found for Category " + iCategCode);

            Name = new String[0];
            ImageUri = new String[0];
            MenuCode = new int[0];
        }
    }

    // Get Items by DeptCode
    private void GetItemDetailsByDept(int iDeptCode) {
        Cursor Items = null;
        Items = dbBillScreen.getItems(iDeptCode);
        if (Items.moveToFirst()) {

            Name = new String[Items.getCount()];
            ImageUri = new String[Items.getCount()];
            MenuCode = new int[Items.getCount()];

            mItemsList.clear();

            do {
                MenuCode[Items.getPosition()] = Items.getInt(Items.getColumnIndex("MenuCode"));
                Name[Items.getPosition()] = Items.getString(Items.getColumnIndex("ItemName"));
                ImageUri[Items.getPosition()] = Items.getString(Items.getColumnIndex("ImageUri"));

                Items items = new Items(Items.getString(Items.getColumnIndex("ItemName"))
                        , Items.getString(Items.getColumnIndex("ImageUri"))
                        , Items.getInt(Items.getColumnIndex("MenuCode")));
                mItemsList.add(items);

            } while (Items.moveToNext());

        } else {

            Log.d("LoadItemsToGrid", "No Items found");

            Name = new String[0];
            ImageUri = new String[0];
            MenuCode = new int[0];
        }
    }

    /*************************************************************************************************************************************
     * Adds the item to billing table from the cursor
     *
     * @param crsrItem : Cursor which contains the item details
     *************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    private void AddItemToOrderTable(Cursor crsrItem)
    {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        String strQty = "0";
        double dRate = 0, dTaxPercent = 0, dDiscPercent = 0, dTaxAmt = 0, dIGSTAmt =0, dcessAmt = 0,dDiscAmt = 0, dTempAmt = 0, dServiceTaxPercent = 0;
        double dServiceTaxAmt = 0;
        int iTaxId = 0, iServiceTaxId = 0, iDiscId = 0;
        boolean bItemExists = false;
        TableRow rowItem = null;
        Cursor crsrTax, crsrDiscount;
        TextView tvName, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent, tvServiceTaxAmt;
        EditText etQty, etRate;
        TextView tvHSn;
        CheckBox chkNumber;
        int crsrSettingsCount = crsrSettings.getCount();
        int crsrItemCount = crsrItem.getCount();
        TextView HSNCode;
        if (crsrItem.moveToFirst() && crsrSettings.moveToFirst())
        {
            do {
                tvServiceTaxPercent = new TextView(this);
                if (iTaxType == 1)
                {
                    String txtServiceTaxPercentage = crsrItem.getString(crsrItem.getColumnIndex("ServiceTaxPercent"));
                    tvServiceTaxPercent.setText(txtServiceTaxPercentage);
                }
                else
                {
                    tvServiceTaxPercent.setText("0");
                }
                // Check for the item in Order table, if present update quantity
                // and amounts
                for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++)
                {
                    TableRow Row = (TableRow) tblOrderItems.getChildAt(iRow);
                    // Check against item number present in table
                    if (Row.getChildAt(0) != null)
                    {
                        CheckBox Number = (CheckBox) Row.getChildAt(0);
                        TextView ItemName = (TextView) Row.getChildAt(1);
                        TextView PrintKOTStatus = (TextView) Row.getChildAt(21);
                        // Check for item number and name, if name is not same
                        // add new
                        if (Number.getText().toString().equalsIgnoreCase(crsrItem.getString(crsrItem.getColumnIndex("MenuCode")))
                                && ItemName.getText().toString().equalsIgnoreCase(crsrItem.getString(crsrItem.getColumnIndex("ItemName")))
                                && PrintKOTStatus.getText().toString().equalsIgnoreCase("1"))
                        {
//

                            // Quantity
                            EditText Qty = (EditText) Row.getChildAt(3);
                            //Qty.setSelectAllOnFocus(true);
                            strQty = Qty.getText().toString().equalsIgnoreCase("") ? "0" : Qty.getText().toString(); // Temp

                            if (BillwithStock == 1)
                            {
                                String availableqty = crsrItem.getString(crsrItem.getColumnIndex("Quantity"));
                                if (crsrItem.getFloat(crsrItem.getColumnIndex("Quantity")) < (Float.valueOf(strQty) + 1))
                                {
                                    MsgBox.Show("Warning", "Stock is less, present stock quantity is " + availableqty);
                                    Qty.setText(String.format("%.2f", Double.parseDouble(availableqty)) );
                                    return;
                                }
                                else
                                {
                                    Qty.setText(String.format("%.2f", Double.parseDouble(strQty) + 1));
                                }
                            }
                            else
                            {
                                Qty.setText(String.format("%.2f", Double.parseDouble(strQty) + 1));
                            }

                            // Amount
                            EditText Rate = (EditText) Row.getChildAt(4);
                            //Rate.setSelectAllOnFocus(true);
                            TextView Amount = (TextView) Row.getChildAt(5);
                            dRate = Double.parseDouble(Rate.getText().toString().equalsIgnoreCase("") ? "0" : Rate.getText().toString()); // Temp

                            // Tax and Discount Amount
                            TextView TaxPer = (TextView) Row.getChildAt(6);
                            TextView TaxAmt = (TextView) Row.getChildAt(7);
                            TextView DiscPer = (TextView) Row.getChildAt(8);
                            TextView DiscAmt = (TextView) Row.getChildAt(9);
                            TextView ServiceTax = (TextView) Row.getChildAt(15);
                            TextView ServiceTaxAmt = (TextView) Row.getChildAt(16);
                            TextView IGSTRate = (TextView) Row.getChildAt(23);
                            TextView IGSTAmt = (TextView) Row.getChildAt(24);
                            TextView cessRate = (TextView) Row.getChildAt(25);
                            TextView cessAmt = (TextView) Row.getChildAt(26);
                            TextView tvTaxableValue = (TextView) Row.getChildAt(28);
                            TextView tvKOTNo = (TextView) Row.getChildAt(29);

                            iKOTNo = Integer.parseInt(tvKOTNo.getText().toString());

                            dTaxPercent = Double.parseDouble(TaxPer.getText().toString().equalsIgnoreCase("") ? "0"
                                    : TaxPer.getText().toString()); // Temp
                            dServiceTaxPercent = Double.parseDouble(ServiceTax.getText().toString().equalsIgnoreCase("") ? "0"
                                    : ServiceTax.getText().toString()); // Tempd
                            double dIGSTRate = Double.parseDouble(IGSTRate.getText().toString().equalsIgnoreCase("") ? "0"
                                    : IGSTRate.getText().toString()); // Temp
                            double dcessRate  = Double.parseDouble(cessRate.getText().toString().equalsIgnoreCase("") ? "0"
                                    : cessRate.getText().toString()); // Temp
                            dDiscPercent = Double.parseDouble(DiscPer.getText().toString().equalsIgnoreCase("") ? "0"
                                    : DiscPer.getText().toString()); // Temp
                            double dTaxableValue = Double.parseDouble(tvTaxableValue.getText().toString().equalsIgnoreCase("") ? "0"
                                    : tvTaxableValue.getText().toString()); // Temp

                            if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // forward tax
                                // Discount
                                dDiscAmt = dRate * (dDiscPercent / 100);
                                dTempAmt = dDiscAmt;
                                dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());
                                dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));
                                // TaxableAmount
                                dTaxableValue = (dRate - dTempAmt) *  Double.parseDouble(Qty.getText().toString());
                                double AmountToPrint = (Double.parseDouble(Qty.getText().toString()) * (dRate-dTempAmt));

                                // RatetoPrint = Double.parseDouble(df_2.format(RatetoPrint));
                                AmountToPrint = Double.parseDouble(df_2.format(AmountToPrint));
                                dTaxableValue = Double.parseDouble(df_3.format(dTaxableValue));


                                // Tax
                                dTaxAmt = (dTaxableValue) * (dTaxPercent / 100);
                                //dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());

                                dServiceTaxAmt = (dTaxableValue) * (dServiceTaxPercent / 100);
                                //dServiceTaxAmt = dServiceTaxAmt * Double.parseDouble(Qty.getText().toString());

                                dIGSTAmt = (dTaxableValue) * (dIGSTRate / 100);
                                //dIGSTAmt = dIGSTAmt * Double.parseDouble(Qty.getText().toString());

                                dcessAmt = (dTaxableValue) * (dcessRate / 100);
                                //dcessAmt = dcessAmt * Double.parseDouble(Qty.getText().toString());

                                dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));
                                dTaxAmt = Double.parseDouble(df_2.format(dTaxAmt));
                                dServiceTaxAmt = Double.parseDouble(df_2.format(dServiceTaxAmt));
                                dIGSTAmt = Double.parseDouble(df_2.format(dIGSTAmt));
                                dcessAmt = Double.parseDouble(df_2.format(dcessAmt));


                                TaxAmt.setText(String.format("%.2f", dTaxAmt));
                                DiscAmt.setText(String.format("%.2f", dDiscAmt));
                                ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                                cessAmt.setText(String.format("%.2f", dcessAmt));
                                IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                                Amount.setText(String.format("%.2f", AmountToPrint));
                                tvTaxableValue.setText(String.format("%.2f", Double.parseDouble(df_2.format(dTaxableValue))));

                            } else {// reverse tax
                                TextView tvOriginalRate = (TextView) Row.getChildAt(27);
                                double dOriginalRate = Double.parseDouble(tvOriginalRate.getText().toString().equalsIgnoreCase("") ? "0"
                                        : tvOriginalRate.getText().toString());

                                // Discount
                                dDiscAmt = dOriginalRate * (dDiscPercent / 100);
                                dTempAmt = dDiscAmt;
                                dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());
                                dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));

                                double amount = (dOriginalRate-dTempAmt) *Double.parseDouble(Qty.getText().toString());
                                double dBasePrice = (dOriginalRate -dTempAmt)/ (1 + (dTaxPercent / 100)+(dServiceTaxPercent/100) + (dcessRate/100));
                                double taxableValue_new = dBasePrice*Double.parseDouble(Qty.getText().toString());



                                amount = Double.parseDouble(df_2.format(amount));
                                taxableValue_new = Double.parseDouble(df_2.format(taxableValue_new));

                                // Tax
                                dTaxAmt = (dBasePrice ) * (dTaxPercent / 100);
                                dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());
                                //Service tax
                                dServiceTaxAmt = (dBasePrice ) * (dServiceTaxPercent / 100);
                                dServiceTaxAmt = dServiceTaxAmt * Double.parseDouble(Qty.getText().toString());

                                dIGSTAmt = (dBasePrice ) * (dIGSTRate/ 100);
                                dIGSTAmt = dIGSTAmt * Double.parseDouble(Qty.getText().toString());
                                //Service tax
                                dcessAmt = (dBasePrice ) * (dcessRate / 100);
                                dcessAmt = dcessAmt * Double.parseDouble(Qty.getText().toString());

                                amount = Double.parseDouble(df_2.format(amount));
                                taxableValue_new = Double.parseDouble(df_2.format(taxableValue_new));
                                dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));

                                dTaxAmt = Double.parseDouble(df_3.format(dTaxAmt));
                                dServiceTaxAmt = Double.parseDouble(df_3.format(dServiceTaxAmt));
                                dIGSTAmt = Double.parseDouble(df_3.format(dIGSTAmt));
                                dcessAmt = Double.parseDouble(df_2.format(dcessAmt));

                                ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                                TaxAmt.setText(String.format("%.2f", dTaxAmt));
                                DiscAmt.setText(String.format("%.2f", dDiscAmt));
                                cessAmt.setText(String.format("%.2f", dcessAmt));
                                IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                                Amount.setText(String.format("%.2f", amount));
                                tvTaxableValue.setText(String.format("%.2f", Double.parseDouble(df_2.format(taxableValue_new))));
                            }

                            //tvKOTNo.setText(String.valueOf(KOTNo));

                            // // delete
                            // Delete.setText("Hi");

                            // Clear all variables and set ItemExists to TRUE
                            // and break from the loop
                            dRate = 0;
                            dTaxPercent = 0;
                            dDiscPercent = 0;
                            dTaxAmt = 0;
                            dDiscAmt = 0;
                            dTempAmt = 0;
                            dIGSTAmt =0;
                            dcessAmt =0;
                            dcessRate =0;
                            dIGSTRate =0;
                            bItemExists = true;

                            break;


                        }
                    }
                }


                if (bItemExists == false) {

                    rowItem = new TableRow(BillingDineInActivity.this);
                    rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    //crsrSettings = db.getBillSetting();

                    int DineInRate = crsrSettings.getInt(crsrSettings.getColumnIndex("DineInRate"));

                    if (DineInRate == 1) {
                        dRate = Double.parseDouble(String.format("%.2f",crsrItem.getDouble(crsrItem.getColumnIndex("DineInPrice1"))));
                    } else if (DineInRate == 2) {
                        dRate = Double.parseDouble(String.format("%.2f",crsrItem.getDouble(crsrItem.getColumnIndex("DineInPrice2"))));
                    } else if (DineInRate == 3) {
                        dRate = Double.parseDouble(String.format("%.2f",crsrItem.getDouble(crsrItem.getColumnIndex("DineInPrice3"))));
                    }

                    // Menu Code
                    chkNumber = new CheckBox(BillingDineInActivity.this);
                    chkNumber.setWidth(40); // 57px ~= 85dp
                    chkNumber.setTextSize(0);
                    chkNumber.setTextColor(Color.TRANSPARENT);
                    chkNumber.setText(crsrItem.getString(crsrItem.getColumnIndex("MenuCode")));
                    //Toast.makeText(getApplicationContext(), chkNumber.getText().toString(), Toast.LENGTH_SHORT).show();

                    // Item Name
                    tvName = new TextView(BillingDineInActivity.this);
                    //tvName.setWidth(135); // 154px ~= 230dp
                    //tvName.setTextSize(11);
                    tvName.setWidth(mItemNameWidth); // 154px ~= 230dp
                    tvName.setTextSize(mDataMiniDeviceTextsize);
                    tvName.setText(crsrItem.getString(crsrItem.getColumnIndex("ItemName")));
                    tvName.setTextColor(getResources().getColor(R.color.black));

                    //hsn code
                    tvHSn = new TextView(BillingDineInActivity.this);
                    //tvHSn.setWidth(67); // 154px ~= 230dp
                    //tvHSn.setTextSize(11);
                    tvHSn.setWidth(mHSNWidth); // 154px ~= 230dp
                    tvHSn.setTextSize(mDataMiniDeviceTextsize);
                    tvHSn.setTextColor(getResources().getColor(R.color.black));
                    tvHSn.setText(crsrItem.getString(crsrItem.getColumnIndex("HSNCode")));
                    if ( !HSNEnable_out.equals("1")) {
                        tvHSn.setVisibility(View.INVISIBLE);
                    }

                    // Quantity
                    etQty = new EditText(this);
                    //etQty.setTextSize(11);
                    etQty.setWidth(mQuantityWidth); // 57px ~= 85dp
                    etQty.setTextSize(mDataMiniDeviceTextsize);
                    etQty.setSelectAllOnFocus(true);
                    etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    // Read quantity from weighing scale if read from weigh
                    // scale is set in settings
                    if (jWeighScale == 0) {
                        etQty.setText("1.00");
                    } else {
                        etQty.setText(String.format("%.2f", getQuantityFromWeighScale()));
                    }
                    etQty.setTag("QTY_RATE");
                    etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etInputValidate.ValidateDecimalInput(etQty);
                    etQty.setEnabled(true);
                    //etQty.setOnClickListener(Qty_Rate_Click);
                    etQty.setOnKeyListener(Qty_Rate_KeyPressEvent);
                    etQty.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        public void afterTextChanged(Editable s) {
                            Qty_Rate_Edit();
                        }
                    });

                    if (BillwithStock == 1) {
                        if (crsrItem.getFloat(crsrItem.getColumnIndex("Quantity")) < Float.valueOf(etQty.getText().toString())) {
                            String availableQty = crsrItem.getString(crsrItem.getColumnIndex("Quantity")) ;
                            MsgBox.Show("Warning", "Stock is less, present stock quantity is "
                                    + availableQty);
                            etQty.setText(String.format("%.2f", Double.parseDouble(availableQty)));
                            return;
                        }
                    }

                    // Rate
                    etRate = new EditText(this);
//                    etRate.setWidth(70); // 74px ~= 110dp
//                    etRate.setTextSize(11);
                    etRate.setWidth(mRateWidth); // 74px ~= 110dp
                    etRate.setTextSize(mDataMiniDeviceTextsize);
                    etRate.setSelectAllOnFocus(true);
                    etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                    etRate.setTag("QTY_RATE");
                    // Check whether Price change is enabled for the item, if
                    // not set Rate text box click able property to false
                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("PriceChange")) == 0  ||
                            !(crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1 ) ) {
                        // disabling rate change for reverse tax
                        etRate.setEnabled(false);
                        etRate.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        etRate.addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            public void afterTextChanged(Editable s) {
                                Qty_Rate_Edit();
                            }
                        });
                        //etRate.setOnClickListener(Qty_Rate_Click);
                        etRate.setOnKeyListener(Qty_Rate_KeyPressEvent);
                        etInputValidate.ValidateDecimalInput(etRate);
                    }

                    if(ItemwiseDiscountEnabled ==1 && crsrItem.getString(crsrItem.getColumnIndex("DiscountPercent"))!=null)  // 1->itemwise discount , 0-> billwise discount
                    {
                        dDiscPercent = Double.parseDouble(String.format("%.2f",
                                crsrItem.getDouble(crsrItem.getColumnIndex("DiscountPercent"))));
                    }
                    tvDiscPercent = new TextView(this);
                    tvDiscPercent.setText(String.format("%.2f", dDiscPercent));

                    dTaxPercent = crsrItem.getDouble(crsrItem.getColumnIndex("CGSTRate"));
                    tvTaxPercent = new TextView(this);
                    tvTaxPercent.setText(String.format("%.2f", dTaxPercent));


                    dServiceTaxPercent = crsrItem.getDouble(crsrItem.getColumnIndex("SGSTRate"));
                    tvServiceTaxPercent = new TextView(this);
                    tvServiceTaxPercent.setText(String.format("%.2f", dServiceTaxPercent));


                    double dcessPercent =0;
                    if(crsrSettings!= null && crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) // disabling cess in reverse tax
                        dcessPercent = crsrItem.getDouble(crsrItem.getColumnIndex("cessRate"));
                    TextView tvcess = new TextView(this);
                    tvcess.setText(String.format("%.2f",dcessPercent));

                    double dIGSTPercent = crsrItem.getDouble(crsrItem.getColumnIndex("IGSTRate"));
                    TextView tvIGSTRate = new TextView(this);
                    tvIGSTRate.setText(String.format("%.2f",dIGSTPercent));


                    double AmounttoPrint =0;
                    double RatetoPrint =0;
                    double TaxableValueToPrint =0;

                    // Tax Amount
                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // Forward
                        dDiscAmt = dRate * (dDiscPercent / 100);
                        RatetoPrint = dRate;
                        AmounttoPrint = dRate-dDiscAmt;
                        TaxableValueToPrint = dRate -dDiscAmt;

                        RatetoPrint = Double.parseDouble(df_2.format(RatetoPrint));
                        AmounttoPrint = Double.parseDouble(df_2.format(AmounttoPrint));
                        TaxableValueToPrint = Double.parseDouble(df_2.format(TaxableValueToPrint));

                        // Tax
                        dTaxAmt = (dRate - dDiscAmt) * (dTaxPercent / 100);
                        dServiceTaxAmt = (dRate - dDiscAmt) * (dServiceTaxPercent / 100);
                        dIGSTAmt = (dRate - dDiscAmt) * (dIGSTPercent / 100);
                        dcessAmt = (dRate - dDiscAmt) * (dcessPercent / 100);

                        dDiscAmt = Double.parseDouble(df_2.format(dDiscAmt));
                        dTaxAmt = Double.parseDouble(df_2.format(dTaxAmt));
                        dServiceTaxAmt = Double.parseDouble(df_2.format(dServiceTaxAmt));
                        dIGSTAmt = Double.parseDouble(df_2.format(dIGSTAmt));
                        dcessAmt = Double.parseDouble(df_2.format(dcessAmt));
                    } else { // Reverse Tax
                        double dBasePrice = 0;
                        dDiscAmt = dRate * (dDiscPercent / 100);
                        AmounttoPrint = dRate -dDiscAmt;
                        dBasePrice = (dRate -dDiscAmt)/ (1 + (dTaxPercent / 100)+(dServiceTaxPercent / 100)+ (dcessPercent/100));
                        RatetoPrint = dBasePrice;
                        TaxableValueToPrint = dBasePrice;

                        //dBasePrice = Double.parseDouble(df_2.format(dBasePrice));
                        RatetoPrint = Double.parseDouble(df_2.format(RatetoPrint));
                        AmounttoPrint = Double.parseDouble(df_2.format(AmounttoPrint));
                        TaxableValueToPrint = Double.parseDouble(df_2.format(TaxableValueToPrint));


                        dTaxAmt = (dBasePrice) * (dTaxPercent / 100);
                        dServiceTaxAmt = (dBasePrice ) * (dServiceTaxPercent / 100);
                        dIGSTAmt = (dBasePrice) * (dIGSTPercent / 100);
                        dcessAmt = (dBasePrice ) * (dcessPercent / 100);

                        dDiscAmt = Double.parseDouble(df_3.format(dDiscAmt));
                        dTaxAmt = Double.parseDouble(df_3.format(dTaxAmt));
                        dServiceTaxAmt = Double.parseDouble(df_3.format(dServiceTaxAmt));
                        dIGSTAmt = Double.parseDouble(df_3.format(dIGSTAmt));
                        dcessAmt = Double.parseDouble(df_3.format(dcessAmt));
                    }

                    etRate.setText(String.format("%.2f", RatetoPrint));

                    // taxable Value
                    TextView tvTaxableValue = new TextView(this);
                    tvTaxableValue.setText(String.format("%.2f",(TaxableValueToPrint)));
                    // Amount
                    tvAmount = new TextView(this);
//                    tvAmount.setWidth(60); // 97px ~= 145dp
//                    tvAmount.setTextSize(11);
                    tvAmount.setWidth(mAmountWidth); // 97px ~= 145dp
                    tvAmount.setTextSize(mDataMiniDeviceTextsize);
                    tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                    tvAmount.setText(String.format("  %.2f", AmounttoPrint));
                    tvAmount.setTextColor(getResources().getColor(R.color.black));


                    tvDiscAmt = new TextView(this);
                    tvDiscAmt.setWidth(50);
                    tvDiscAmt.setText(String.format("%.2f", dDiscAmt));


                    tvTaxAmt = new TextView(this);
                    tvTaxAmt.setText(String.format("%.2f", dTaxAmt));

                    tvServiceTaxAmt = new TextView(this);
                    tvServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));

                    TextView tvIGSTAmt = new TextView(this);
                    tvIGSTAmt.setText(String.format("%.2f", dIGSTAmt));

                    TextView tvcessAmt = new TextView(this);
                    tvcessAmt.setText(String.format("%.2f", dcessAmt));

                    // Department Code
                    tvDeptCode = new TextView(this);
                    tvDeptCode.setText(crsrItem.getString(crsrItem.getColumnIndex("DeptCode")));

                    // Category Code
                    tvCategCode = new TextView(this);
                    tvCategCode.setText(crsrItem.getString(crsrItem.getColumnIndex("CategCode")));

                    // Kitchen Code
                    tvKitchenCode = new TextView(this);
                    tvKitchenCode.setText(crsrItem.getString(crsrItem.getColumnIndex("KitchenCode")));

                    // Tax Type [Forward - 1/ Reverse - 0]
                    tvTaxType = new TextView(this);
                    //tvTaxType.setWidth(50);
                    //tvTaxType.setText(crsrItem.getString(crsrItem.getColumnIndex("TaxType")));
                    tvTaxType.setText(crsrSettings.getString(crsrSettings.getColumnIndex("Tax")));

                    // Modifier Charge
                    tvModifierCharge = new TextView(this);
                    tvModifierCharge.setText("0.0");

                    TextView tvUOM = new TextView(this);
                    tvUOM.setText(crsrItem.getString(crsrItem.getColumnIndex("UOM")));


                    // SupplyType
                    TextView SupplyType = new TextView(this);
                    SupplyType.setText(crsrItem.getString(crsrItem.getColumnIndex("SupplyType")));

                    TextView tvSpace = new TextView(this);
                    tvSpace.setText("        ");

                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    ImageButton ImgDelete = new ImageButton(BillingDineInActivity.this);
                    ImgDelete.setImageResource(res);
                    // btnDelete.setText(crsrItem.getString(crsrItem.getColumnIndex("MenuCode")));
                    ImgDelete.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v)
                        {
                            final View v1 = v;
                            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(BillingDineInActivity.this);
                            /*LayoutInflater UserAuthorization = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View vwAuthorization = UserAuthorization.inflate(R.layout.deleteconfirmation, null);*/
                            AuthorizationDialog
                                    .setIcon(R.drawable.ic_launcher)
                                    .setTitle("Confirmation")
                                    .setMessage("Are you sure to delete this item")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            View row = (View) v1.getParent();
                                            ViewGroup container = ((ViewGroup) row.getParent());
                                            container.removeView(row);
                                            container.invalidate();
                                            CalculateTotalAmount();

                                        }
                                    }).show();
                        }
                    });
                    TextView tvSpace1 = new TextView(BillingDineInActivity.this);
                    tvSpace1.setText("       ");
                    TextView tvPrintKOTStatus = new TextView(BillingDineInActivity.this);
                    tvPrintKOTStatus.setText("1");

                    TextView originalrate = new TextView(this);
                    originalrate.setText(String.format("%.2f",dRate));

                    Cursor cursor = dbBillScreen.getKOTNumberslast(Integer.parseInt(tvTableNumber.getText().toString()),
                            Integer.parseInt(tvTableSplitNo.getText().toString()));

                    if (cursor !=null && cursor.moveToFirst())
                    {
                        iKOTNo = (cursor.getInt(cursor.getColumnIndex("TokenNumber"))) ;
                    }else{
                        iKOTNo = dbBillScreen.getKOTNo();
                    }

                    TextView tvKOTNo = new TextView(this);
                    tvKOTNo.setText(String.valueOf(iKOTNo));

                    // Add all text views and edit text to Item Row
                    rowItem.addView(chkNumber);//0
                    rowItem.addView(tvName);//1
                    rowItem.addView(tvHSn);//2
                    rowItem.addView(etQty);//3
                    rowItem.addView(etRate);//4
                    rowItem.addView(tvAmount);//5
                    rowItem.addView(tvTaxPercent);//6
                    rowItem.addView(tvTaxAmt);//7
                    rowItem.addView(tvDiscPercent);//8
                    rowItem.addView(tvDiscAmt);//9
                    rowItem.addView(tvDeptCode);//10
                    rowItem.addView(tvCategCode);//11
                    rowItem.addView(tvKitchenCode);//12
                    rowItem.addView(tvTaxType);//13
                    rowItem.addView(tvModifierCharge);//14
                    rowItem.addView(tvServiceTaxPercent);//15
                    rowItem.addView(tvServiceTaxAmt);//16
                    rowItem.addView(SupplyType);//17
                    rowItem.addView(tvSpace);//18
                    rowItem.addView(ImgDelete);//19
                    rowItem.addView(tvSpace1);//20
                    rowItem.addView(tvPrintKOTStatus);//21
                    rowItem.addView(tvUOM);//22
                    rowItem.addView(tvIGSTRate);//23
                    rowItem.addView(tvIGSTAmt);//24
                    rowItem.addView(tvcess);//25
                    rowItem.addView(tvcessAmt);//26
                    rowItem.addView(originalrate);//27
                    rowItem.addView(tvTaxableValue);//28
                    rowItem.addView(tvKOTNo);//29

                    tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                bItemExists = false;
            } while (crsrItem.moveToNext());
            CalculateTotalAmount();
        }
        else
        {
            Log.d("AddItemToOrderTable", "ItemNotFound Exception");
            //MsgBox.Show("Oops ","Item not found");
        }
    }

    /*************************************************************************************************************************************
     * Calculates all values such as Tax, Discount, Sub Total and grand total
     * whenever Quantity or Rate text value changed
     *************************************************************************************************************************************/
    private void Qty_Rate_Edit() {

        double strQty = 0;
        double dTaxPercent = 0,dServiceTaxPercent = 0, dDiscPercent = 0, dDiscAmt = 0, dTempAmt = 0, dTaxAmt = 0,dServiceTaxAmt =0;
        double dRate,dIGSTAmt=0, dcessAmt=0;
        try {
            for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

                TableRow Row = (TableRow) tblOrderItems.getChildAt(iRow);

                // Check against item number present in table
                if (Row.getChildAt(0) != null) {

                    CheckBox Number = (CheckBox) Row.getChildAt(0);
                    TextView ItemName = (TextView) Row.getChildAt(1);


                    // Quantity
                    EditText Qty = (EditText) Row.getChildAt(3);
                    //Qty.setSelectAllOnFocus(true);
                    strQty = Double.parseDouble(
                            Qty.getText().toString().equalsIgnoreCase("") ? "0" : Qty.getText().toString()); // Temp
                    if (BillwithStock == 1) {
                        Cursor ItemCrsr = db.getItemDetail(ItemName.getText().toString());
                        if(ItemCrsr!=null && ItemCrsr.moveToFirst())
                        {
                            double availableStock = ItemCrsr.getDouble(ItemCrsr.getColumnIndex("Quantity"));
                            if ( availableStock < strQty) {
                                MsgBox.Show("Warning", "Stock is less, present stock quantity is "
                                        + String.valueOf(availableStock));
                                Qty.setText(String.format("%.2f", availableStock));

                                return;
                            }
                        }

                    }

                    // Amount
                    EditText Rate = (EditText) Row.getChildAt(4);
                    //Rate.setSelectAllOnFocus(true);
                    TextView Amount = (TextView) Row.getChildAt(5);
                    dRate = Double.parseDouble(
                            Rate.getText().toString().equalsIgnoreCase("") ? "0" : Rate.getText().toString()); // Temp


                    // Tax and Discount Amount
                    TextView TaxPer = (TextView) Row.getChildAt(6);
                    TextView TaxAmt = (TextView) Row.getChildAt(7);
                    TextView DiscPer = (TextView) Row.getChildAt(8);
                    TextView DiscAmt = (TextView) Row.getChildAt(9);
                    TextView ServiceTax = (TextView) Row.getChildAt(15);
                    TextView ServiceTaxAmt = (TextView) Row.getChildAt(16);
                    TextView IGSTRate = (TextView) Row.getChildAt(23);
                    TextView IGSTAmt = (TextView) Row.getChildAt(24);
                    TextView cessRate = (TextView) Row.getChildAt(25);
                    TextView cessAmt = (TextView) Row.getChildAt(26);
                    TextView tvTaxableValue = (TextView) Row.getChildAt(28);
                    TextView tvOriginalRate = (TextView) Row.getChildAt(27);

                    dTaxPercent = Double.parseDouble(TaxPer.getText().toString().equalsIgnoreCase("") ? "0"
                            : TaxPer.getText().toString()); // Temp
                    dServiceTaxPercent = Double.parseDouble(ServiceTax.getText().toString().equalsIgnoreCase("") ? "0"
                            : ServiceTax.getText().toString()); // Temp
                    double dIGSTRate = Double.parseDouble(IGSTRate.getText().toString().equalsIgnoreCase("") ? "0"
                            : IGSTRate.getText().toString()); // Temp
                    double dcessRate  = Double.parseDouble(cessRate.getText().toString().equalsIgnoreCase("") ? "0"
                            : cessRate.getText().toString()); // Temp
                    dDiscPercent = Double.parseDouble(DiscPer.getText().toString().equalsIgnoreCase("") ? "0"
                            : DiscPer.getText().toString()); // Temp

                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1) { // forward tax
                        // Discount
                        dDiscAmt = dRate * (dDiscPercent / 100);
                        dTempAmt = dDiscAmt;
                        dDiscAmt = dDiscAmt * strQty;

                        // Tax
                        dTaxAmt = (dRate - dTempAmt) * (dTaxPercent / 100);
                        dTaxAmt = dTaxAmt * strQty;

                        dServiceTaxAmt = (dRate - dTempAmt) * (dServiceTaxPercent / 100);
                        dServiceTaxAmt = dServiceTaxAmt * strQty;

                        dIGSTAmt = (dRate - dTempAmt) * (dIGSTRate / 100);
                        dIGSTAmt = dIGSTAmt * strQty;

                        dcessAmt = (dRate - dTempAmt) * (dcessRate / 100);
                        dcessAmt = dcessAmt * strQty;



                        TaxAmt.setText(String.format("%.2f", dTaxAmt));
                        DiscAmt.setText(String.format("%.2f", dDiscAmt));
                        ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                        cessAmt.setText(String.format("%.2f", dcessAmt));
                        IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                        Amount.setText(String.format("%.2f", (strQty * (dRate-dTempAmt))));
                        tvTaxableValue.setText(String.format("%.2f", (strQty * (dRate-dTempAmt))));
                        tvOriginalRate.setText(String.format("%.2f", (dRate)));

                    } else {// reverse tax

                        TextView originalRate = (TextView) Row.getChildAt(27);
                        dRate = Double.parseDouble(originalRate.getText().toString().equalsIgnoreCase("") ? "0"
                                : originalRate.getText().toString()); //
                        // Discount
                        dDiscAmt = dRate * (dDiscPercent / 100);
                        dTempAmt = dDiscAmt;
                        dDiscAmt = dDiscAmt * strQty;

                        double dBasePrice = 0;
                        dBasePrice = (dRate-dTempAmt) / (1 + (dTaxPercent / 100)+(dServiceTaxPercent/100)+ (dcessRate/100));

                        // Tax
                        dTaxAmt = (dBasePrice ) * (dTaxPercent / 100);
                        dTaxAmt = dTaxAmt *strQty;;

                        dIGSTAmt = (dBasePrice ) * (dIGSTRate/ 100);
                        dIGSTAmt = dIGSTAmt * strQty;

                        dcessAmt = (dBasePrice ) * (dcessRate / 100);
                        dcessAmt = dcessAmt * strQty;;

                        //Service tax
                        dServiceTaxAmt = (dBasePrice) * (dServiceTaxPercent / 100);
                        dServiceTaxAmt = dServiceTaxAmt * strQty;

                        double amount = (strQty * (dRate-dTempAmt));
                        double taxVal = (strQty * dBasePrice);

                        dServiceTaxAmt = Double.parseDouble(df_3.format(dServiceTaxAmt));
                        dTaxAmt = Double.parseDouble(df_3.format(dTaxAmt));
                        dDiscAmt = Double.parseDouble(df_3.format(dDiscAmt));
                        dcessAmt = Double.parseDouble(df_3.format(dcessAmt));
                        dIGSTAmt = Double.parseDouble(df_3.format(dIGSTAmt));
                        amount = Double.parseDouble(df_2.format(amount));
                        taxVal = Double.parseDouble(df_2.format(taxVal));

                        Pattern p = Pattern.compile("^(-?[0-9]+[\\.\\,][0-9]{1,2})?[0-9]*$");
                        Matcher m = p.matcher(String.valueOf(taxVal));
                        boolean matchFound = m.find();
                        if (matchFound) {
                            System.out.println(Double.valueOf(m.group(1)));
                        }


                        ServiceTaxAmt.setText(String.format("%.2f", dServiceTaxAmt));
                        TaxAmt.setText(String.format("%.2f", dTaxAmt));
                        DiscAmt.setText(String.format("%.2f", dDiscAmt));
                        cessAmt.setText(String.format("%.2f", dcessAmt));
                        IGSTAmt.setText(String.format("%.2f", dIGSTAmt));
                        Amount.setText(String.format("%.2f",amount ));
                        tvTaxableValue.setText(String.format("%.2f",taxVal ));
                    }
                    // // delete
                    // Delete.setText("Hi");

                    // Clear all variables and set ItemExists to TRUE
                    // and break from the loop
                    dRate = 0;
                    dTaxPercent = 0;
                    dDiscPercent = 0;
                    dTaxAmt = 0;
                    dDiscAmt = 0;
                    dTempAmt = 0;
                    //bItemExists = true;

                }

            }
            CalculateTotalAmount();
        } catch (Exception e) {
            MsgBox.setMessage("Error while changing quantity directly :" + e.getMessage()).setPositiveButton("OK", null).show();
            e.printStackTrace();
        }
    }

    /*************************************************************************************************************************************
     * Adds selected KOT modifier to selected items in billing table
     *
     * @param Modifier : Modifier data which has to included to item
     *************************************************************************************************************************************/
    private void AddModifier(Cursor Modifier) {

        double dModifierAmt = 0;

        CheckBox Number;
        TextView Name, ModifierAmt;

        if (Modifier.moveToFirst()) {

            for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

                TableRow RowItem = (TableRow) tblOrderItems.getChildAt(iRow);

                if (RowItem.getChildAt(0) != null) {

                    Number = (CheckBox) RowItem.getChildAt(0);
                    Name = (TextView) RowItem.getChildAt(1);
                    ModifierAmt = (TextView) RowItem.getChildAt(13);

                    if (Number.isChecked()) {
                        Name.setText(Name.getText().toString() + "+"
                                + Modifier.getString(Modifier.getColumnIndex("ModifierDescription")));

                        if (Modifier.getInt(Modifier.getColumnIndex("IsChargeable")) == 1) {

                            dModifierAmt = Double.parseDouble(ModifierAmt.getText().toString());
                            dModifierAmt += Modifier.getDouble(Modifier.getColumnIndex("ModifierAmount"));
                            ModifierAmt.setText(String.format("%.2f", dModifierAmt));
                        }
                    }
                }
            }
            CalculateTotalAmount();
        } else {
            MsgBox.Show("Warning", "No KOT modifiers found");
        }
    }

    /*************************************************************************************************************************************
     * Deletes selected items from billing table
     *************************************************************************************************************************************/
    private void DeleteItem() {

        CheckBox MenuCode;

        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

            TableRow Row = (TableRow) tblOrderItems.getChildAt(iRow);

            if (Row.getChildAt(0) != null) {

                MenuCode = (CheckBox) Row.getChildAt(0);

                if (MenuCode.isChecked()) {

                    // Remove all the view present in the row.
                    Row.removeAllViews();

                    // Remove the row
                    tblOrderItems.removeView(Row);

                    // Exit from the loop
                    // break;
                }
            } else {
                continue;
            }

        }

        CalculateTotalAmount();

        // Display Total Items
        //txtTotalItems.setText(String.valueOf(tblOrderItems.getChildCount()));

    }

    /*************************************************************************************************************************************
     * Calculates bill sub total, sales tax amount, service tax amount and Bill
     * total amount.
     ************************************************************************************************************************************/
    private void CalculateTotalAmount()
    {
        double dSubTotal = 0,dTaxTotal = 0, dModifierAmt = 0, dServiceTaxAmt = 0, dOtherCharges = 0, dTaxAmt = 0, dSerTaxAmt = 0, dblDiscount = 0;
        float dTaxPercent = 0, dSerTaxPercent = 0;
        double dTotalBillAmount_for_reverseTax =0;
        double dIGSTAmt =0, dcessAmt =0;
        // Item wise tax calculation ----------------------------
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++)
        {
            TableRow RowItem = (TableRow) tblOrderItems.getChildAt(iRow);
            if (RowItem.getChildAt(0) != null)
            {
                TextView ColQuantity = (TextView) RowItem.getChildAt(3);
                TextView ColRate = (TextView) RowItem.getChildAt(4);
                TextView ColTaxType = (TextView) RowItem.getChildAt(13);
                TextView ColAmount = (TextView) RowItem.getChildAt(5);
                TextView ColDisc = (TextView) RowItem.getChildAt(9);
                TextView ColTax = (TextView) RowItem.getChildAt(7);
                TextView ColModifierAmount = (TextView) RowItem.getChildAt(14);
                TextView ColServiceTaxAmount = (TextView) RowItem.getChildAt(16);
                TextView ColIGSTAmount = (TextView) RowItem.getChildAt(24);
                TextView ColcessAmount = (TextView) RowItem.getChildAt(26);
                TextView ColTaxValue = (TextView) RowItem.getChildAt(28);
                dblDiscount += Double.parseDouble(ColDisc.getText().toString());
                dTaxTotal += Double.parseDouble(ColTax.getText().toString());
                dServiceTaxAmt += Double.parseDouble(ColServiceTaxAmount.getText().toString());
                dIGSTAmt += Double.parseDouble(ColIGSTAmount.getText().toString());
                dcessAmt += Double.parseDouble(ColcessAmount.getText().toString());
                if (crsrSettings!=null && crsrSettings.getString(crsrSettings.getColumnIndex("Tax")).equalsIgnoreCase("1"))  // forward tax
                {
                    dSubTotal += Double.parseDouble(ColAmount.getText().toString());
                }
                else // reverse tax
                {
                    double qty = ColQuantity.getText().toString().equals("")?0.00 : Double.parseDouble(ColQuantity.getText().toString());
                    double baseRate = ColRate.getText().toString().equals("")?0.00 : Double.parseDouble(ColRate.getText().toString());
                    dSubTotal += (qty*baseRate);
                    dTotalBillAmount_for_reverseTax += Double.parseDouble(ColAmount.getText().toString());
                }

            }
        }
        // ------------------------------------------
        // Bill wise tax Calculation -------------------------------
        Cursor crsrtax = db.getTaxConfigs(1);
        if (crsrtax.moveToFirst()) {
            dTaxPercent = crsrtax.getFloat(crsrtax.getColumnIndex("TotalPercentage"));
            dTaxAmt += dSubTotal * (dTaxPercent / 100);
        }
        Cursor crsrtax1 = db.getTaxConfigs(2);
        if (crsrtax1.moveToFirst()) {
            dSerTaxPercent = crsrtax1.getFloat(crsrtax1.getColumnIndex("TotalPercentage"));
            dSerTaxAmt += dSubTotal * (dSerTaxPercent / 100);
        }
        // -------------------------------------------------

        dOtherCharges = Double.valueOf(tvOthercharges.getText().toString());
        //String strTax = crsrSettings.getString(crsrSettings.getColumnIndex("Tax"));
        if (crsrSettings.moveToFirst()) {
            if (crsrSettings.getString(crsrSettings.getColumnIndex("Tax")).equalsIgnoreCase("1"))  // forward tax
            {
                if (crsrSettings.getString(crsrSettings.getColumnIndex("TaxType")).equalsIgnoreCase("1"))
                {
                    tvIGSTValue.setText(String.format("%.2f", dIGSTAmt));
                    tvCGSTValue.setText(String.format("%.2f", dTaxTotal));
                    tvSGSTValue.setText(String.format("%.2f", dServiceTaxAmt));
                    tvcessValue.setText(String.format("%.2f",dcessAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvCGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvSGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvCGSTValue.setTextColor(Color.WHITE);
                        tvSGSTValue.setTextColor(Color.WHITE);
                    }

                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dTaxTotal + dServiceTaxAmt + dOtherCharges+dcessAmt));
                }
                else
                {
                    tvIGSTValue.setText(String.format("%.2f", dIGSTAmt));
                    tvCGSTValue.setText(String.format("%.2f", dTaxTotal));
                    tvSGSTValue.setText(String.format("%.2f", dServiceTaxAmt));
                    tvcessValue.setText(String.format("%.2f",dcessAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvCGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvSGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvCGSTValue.setTextColor(Color.WHITE);
                        tvSGSTValue.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dTaxAmt + dSerTaxAmt + dOtherCharges+dcessAmt));
                }
            }
            else // reverse tax
            {
                if (crsrSettings.getString(crsrSettings.getColumnIndex("TaxType")).equalsIgnoreCase("1")) // item wise
                {
                    tvIGSTValue.setText(String.format("%.2f", dIGSTAmt));
                    tvCGSTValue.setText(String.format("%.2f", dTaxTotal));
                    tvSGSTValue.setText(String.format("%.2f", dServiceTaxAmt));
                    tvcessValue.setText(String.format("%.2f",dcessAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvCGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvSGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvCGSTValue.setTextColor(Color.WHITE);
                        tvSGSTValue.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dTotalBillAmount_for_reverseTax + dOtherCharges));

                }
                else
                {
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvIGSTValue.setText(String.format("%.2f", dIGSTAmt));
                    tvCGSTValue.setText(String.format("%.2f", dTaxTotal));
                    tvSGSTValue.setText(String.format("%.2f", dServiceTaxAmt));
                    tvcessValue.setText(String.format("%.2f",dcessAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvCGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvSGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvCGSTValue.setTextColor(Color.WHITE);
                        tvSGSTValue.setTextColor(Color.WHITE);
                    }
                    tvBillAmount.setText(String.format("%.2f", dTotalBillAmount_for_reverseTax + dOtherCharges));
                }
            }
            tvDiscountAmount.setText(String.format("%.2f", dblDiscount));
        }
    }

    private void CalculateTotalAmountforRePrint() {

        double dSubTotal = 0, dTaxTotal = 0, dModifierAmt = 0, dServiceTaxAmt = 0, dOtherCharges = 0, dTaxAmt = 0, dSerTaxAmt = 0;
        float dTaxPercent = 0, dSerTaxPercent = 0;
        double discountamt = Double.parseDouble(tvDiscountAmount.getText().toString());
        // Item wise tax calculation ----------------------------
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

            TableRow RowItem = (TableRow) tblOrderItems.getChildAt(iRow);

            if (RowItem.getChildAt(0) != null) {

                //TextView ColTaxType = (TextView) RowItem.getChildAt(12);
                TextView ColAmount = (TextView) RowItem.getChildAt(5);
                //TextView ColDisc = (TextView) RowItem.getChildAt(8);
                TextView ColTax = (TextView) RowItem.getChildAt(7);
                //TextView ColModifierAmount = (TextView) RowItem.getChildAt(13);
                TextView ColServiceTaxAmount = (TextView) RowItem.getChildAt(16);

                dTaxTotal += Double.parseDouble(ColTax.getText().toString());
                dServiceTaxAmt += Double.parseDouble(ColServiceTaxAmount.getText().toString());

                dSubTotal += Double.parseDouble(ColAmount.getText().toString());
            }
        }
        // ------------------------------------------

        // Bill wise tax Calculation -------------------------------
        Cursor crsrtax = dbBillScreen.getTaxConfig(1);
        if (crsrtax.moveToFirst()) {
            dTaxPercent = crsrtax.getFloat(crsrtax.getColumnIndex("TotalPercentage"));
            dTaxAmt += dSubTotal * (dTaxPercent / 100);
        }
        Cursor crsrtax1 = dbBillScreen.getTaxConfig(2);
        if (crsrtax1.moveToFirst()) {
            dSerTaxPercent = crsrtax1.getFloat(crsrtax1.getColumnIndex("TotalPercentage"));
            dSerTaxAmt += dSubTotal * (dSerTaxPercent / 100);
        }
        // -------------------------------------------------

        //dOtherCharges = Double.valueOf(tvOthercharges.getText().toString());
        if (crsrSettings.moveToFirst()) {
            if (crsrSettings.getString(crsrSettings.getColumnIndex("Tax")).equalsIgnoreCase("1")) {
                if (crsrSettings.getString(crsrSettings.getColumnIndex("TaxType")).equalsIgnoreCase("1")) {

                    tvIGSTValue.setText(String.format("%.2f", dTaxTotal + dServiceTaxAmt));
                    tvCGSTValue.setText(String.format("%.2f", dTaxTotal));
                    tvSGSTValue.setText(String.format("%.2f", dServiceTaxAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvCGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvSGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvCGSTValue.setTextColor(Color.WHITE);
                        tvSGSTValue.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dTaxTotal + dServiceTaxAmt + dOtherCharges-discountamt));
                } else {

                    tvIGSTValue.setText(String.format("%.2f", dTaxTotal + dServiceTaxAmt));
                    tvCGSTValue.setText(String.format("%.2f", dTaxTotal));
                    tvSGSTValue.setText(String.format("%.2f", dServiceTaxAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvCGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvSGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvCGSTValue.setTextColor(Color.WHITE);
                        tvSGSTValue.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dTaxAmt + dSerTaxAmt + dOtherCharges-discountamt));
                }
            } else {
                if (crsrSettings.getString(crsrSettings.getColumnIndex("TaxType")).equalsIgnoreCase("1")) {
                    tvIGSTValue.setText(String.format("%.2f", dTaxTotal + dServiceTaxAmt));
                    tvCGSTValue.setText(String.format("%.2f", dTaxTotal));
                    tvSGSTValue.setText(String.format("%.2f", dServiceTaxAmt));

                    if (chk_interstate.isChecked()) // interstate
                    {
                        tvIGSTValue.setTextColor(Color.WHITE);
                        tvCGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvSGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    } else {
                        tvIGSTValue.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                        tvCGSTValue.setTextColor(Color.WHITE);
                        tvSGSTValue.setTextColor(Color.WHITE);
                    }
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dOtherCharges-discountamt));

                } else {
                    tvSubTotal.setText(String.format("%.2f", dSubTotal));
                    if (chk_interstate.isChecked()) {
                        tvCGSTValue.setText(String.format("%.2f", dTaxAmt + dSerTaxAmt));
                        // tvSGSTValue.setText(String.format("%.2f", dSerTaxAmt));
                        tvSGSTValue.setText("");
                    } else {
                        tvCGSTValue.setText(String.format("%.2f", dTaxAmt));
                        tvSGSTValue.setText(String.format("%.2f", dSerTaxAmt));
                    }
                    tvCGSTValue.setText(String.format("%.2f", dTaxAmt));
                    tvSGSTValue.setText(String.format("%.2f", dSerTaxAmt));
                    tvBillAmount.setText(String.format("%.2f", dSubTotal + dOtherCharges-discountamt));
                }
            }
        }
    }

    /*************************************************************************************************************************************
     * Delete all the items in billing table and clears all text boxes and item
     * grid in billing screen
     *************************************************************************************************************************************/
    private void ClearAll() {

        Time = Calendar.getInstance();
        isReprint = false;
        //txtSearchItemBarcode.setText("");
        tx = "";
        autoCompleteTextViewSearchItemBarcode.setText("");
        reprintBillingMode=0;
        tvWaiterNumber.setText("0");
        tvTableNumber.setText("0");
        tvTableSplitNo.setText("1");
        tvSubUdfValue.setText("1");
        tvSubTotal.setText("0.00");
        tvDiscountAmount.setText("0.00");
        tvDiscountPercentage.setText("0.00");
        tvCGSTValue.setText("0.00");
        tvSGSTValue.setText("0.00");
        tvIGSTValue.setText("0.00");
        tvBillAmount.setText("0.00");
        tvcessValue.setText("0.00");

        aTViewSearchItem.setText("");
        aTViewSearchMenuCode.setText("");

        btnDeleteKOT.setEnabled(false);
        btnAddCustomer.setEnabled(true);
        chk_interstate.setChecked(false);
        spnr_pos.setSelection(0);
        spnr_pos.setEnabled(false);
        chk_interstate.setEnabled(true);
        // Clear order item table
        tblOrderItems.removeAllViews();
        edtCustName.setText("");
        edtCustId.setText("0");
        edtCustPhoneNo.setText("");
        edtCustDineInPhoneNo.setText("");
        edtCustAddress.setText("");
        etCustGSTIN.setText("");
        // Display Total Items
        //txtTotalItems.setText("0");

        // Display current bill number
//        Log.d("Richa_clear : ", tvBillNumber.getText().toString());
//        Log.d("Richa_clear : ", String.valueOf(dbBillScreen.getNewBillNumber()));
        tvBillNumber.setText(String.valueOf(db.getNewBillNumber()));
//        Log.d("Richa_clear :- ", String.valueOf(dbBillScreen.getNewBillNumber()));
//        Log.d("Richa_clear :- ", tvBillNumber.getText().toString());
        btnPrintBill.setEnabled(false);
        btnPreviewBill.setEnabled(false);
        btnPrintKOT.setEnabled(false);
        // By default load 1st department items to grid
        //GetItemDetails(1);
        // This condition is to avoid NullReferenceException in getCount() in
        // ImageAdapter class.
        /*if (Name.length > 0) {
            // Assign item grid to image adapter
         //   grdItems.setAdapter(new ImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1")));
             mTestImageAdapter = new TestImageAdapter(myContext, Name, MenuCode, ImageUri, Byte.parseByte("1"));
                    mRecyclerGridView.setAdapter(mTestImageAdapter);
                    mTestImageAdapter.setOnItemClickListener(BillingDineInActivity.this);
                    mTestImageAdapter.notifyDataSetChanged();
            // Make the item grid visible
          //  grdItems.setVisibility(View.VISIBLE);
            mRecyclerGridView.setVisibility(View.VISIBLE);
        } else {
            // Make the item grid invisible
         //   grdItems.setVisibility(View.INVISIBLE);
            mRecyclerGridView.setVisibility(View.INVISIBLE);
        }*/
        setInvoiceDate();
        fTotalDiscount =0;
        fRoundOfValue =0;
        AMOUNTPRINTINNEXTLINE =0;

        fCashPayment = 0;
        fCardPayment = 0;
        fCouponPayment = 0;
        fPaidTotalPayment = 0;
        dPettCashPayment = 0;
        fChangePayment = 0;
        fWalletPayment = 0;
        dFinalBillValue = 0;
    }


    /*************************************************************************************************************************************
     * Opens a dialog window which takes table number as input and proceeds for
     * billing
     *************************************************************************************************************************************/
   /* private void DinInTender() {

        AlertDialog.Builder DineInTenderDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.tender_dinein, null);

        final EditText txtTblNo = (EditText) vwAuthorization.findViewById(R.id.txtDineInTenderTableNumber);
        final EditText txtTblSplitNo = (EditText) vwAuthorization.findViewById(R.id.txtDineInTenderTableSplitNo);

//        final EditText txtTblNo = new EditText(myContext);
//        txtTblNo.setInputType(InputType.TYPE_CLASS_NUMBER);
//
//        final EditText txtTblSplitNo = new EditText(myContext);
//        txtTblSplitNo.setInputType(InputType.TYPE_CLASS_NUMBER);

        DineInTenderDialog.setIcon(R.drawable.ic_launcher).setTitle("Dine In PayBill").setMessage("Enter table number")
                .setView(vwAuthorization).setNegativeButton("Cancel", null)
                .setNeutralButton("CheckOut", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (txtTblNo.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please enter table number");
                            return;
                        } else {
                            if (dbBillScreen.getCheckedOutStatus(Integer.parseInt(txtTblNo.getText().toString()),
                                    1) == 0) {
                                Cursor BillItems = dbBillScreen
                                        .getKOTItems(Integer.parseInt(txtTblNo.getText().toString()), 1, Integer.parseInt(txtTblSplitNo.getText().toString()));
                                if (BillItems.moveToFirst()) {
                                    int iResult = dbBillScreen
                                            .updateCheckedOutStatus(Integer.parseInt(txtTblNo.getText().toString()), 1);
                                    Log.v("CheckOut", "Updated rows:" + iResult);
                                    LoadKOTItems(BillItems);
                                    //PrintCheckOutBill();
                                    ClearAll();
                                }
                            } else {
                                MsgBox.Show("Warning",
                                        "Table is already checked out or no KOT is pending for the table number "
                                                + txtTblNo.getText().toString());
                            }
                        }
                    }
                }).setPositiveButton("PayBill", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                if (txtTblNo.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Please enter table number");
                    return;
                } else {

                    tvTableNumber.setText(txtTblNo.getText().toString());
                    tvTableSplitNo.setText(txtTblSplitNo.getText().toString());
                    tvSubUdfValue.setText("1");
                    Cursor BillItems = dbBillScreen.getKOTItems(Integer.parseInt(txtTblNo.getText().toString()),
                            1, Integer.parseInt(txtTblSplitNo.getText().toString()));
                    if (BillItems.moveToFirst()) {
                        LoadKOTItems(BillItems);
                        Intent intentTender = new Intent(myContext, TenderActivity.class);
                        intentTender.putExtra("TotalAmount", tvBillAmount.getText().toString());
                        startActivityForResult(intentTender, 1);

                    } else {
                        MsgBox.Show("Warning",
                                "No KOT is present for the table number " + txtTblNo.getText().toString());
                    }
                }
            }
        }).show();
    }*/

    public ArrayList<PendingKOT> getNewItemsList() {

        ArrayList<PendingKOT> billKotItems = new ArrayList<PendingKOT>();
        String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());

        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
            TableRow RowKOTItem = (TableRow) tblOrderItems.getChildAt(iRow);
            CheckBox ItemNumber = (CheckBox) RowKOTItem.getChildAt(0);
            TextView ItemName = (TextView) RowKOTItem.getChildAt(1);
            TextView hsn = (TextView) RowKOTItem.getChildAt(2);
            EditText Quantity = (EditText) RowKOTItem.getChildAt(3);
            EditText Rate = (EditText) RowKOTItem.getChildAt(4);
            TextView Amount = (TextView) RowKOTItem.getChildAt(5);
            TextView SalesTaxPercent = (TextView) RowKOTItem.getChildAt(6);
            TextView SalesTaxAmount = (TextView) RowKOTItem.getChildAt(7);
            TextView DiscountPercent = (TextView) RowKOTItem.getChildAt(8);
            TextView DiscountAmount = (TextView) RowKOTItem.getChildAt(9);
            TextView DeptCode = (TextView) RowKOTItem.getChildAt(10);
            TextView CategCode = (TextView) RowKOTItem.getChildAt(11);
            TextView KitchenCode = (TextView) RowKOTItem.getChildAt(12);
            TextView TaxType = (TextView) RowKOTItem.getChildAt(13);
            TextView ModifierAmount = (TextView) RowKOTItem.getChildAt(14);
            TextView ServiceTaxPercent = (TextView) RowKOTItem.getChildAt(15);
            TextView ServiceTaxAmount = (TextView) RowKOTItem.getChildAt(16);
            TextView SupplyType = (TextView) RowKOTItem.getChildAt(17);
            TextView printstatus = (TextView) RowKOTItem.getChildAt(21);
            TextView UOM = (TextView) RowKOTItem.getChildAt(22);
            TextView IGSTRate = (TextView) RowKOTItem.getChildAt(23);
            TextView IGSTAmt = (TextView) RowKOTItem.getChildAt(24);
            TextView cessRate = (TextView) RowKOTItem.getChildAt(25);
            TextView cessAmt = (TextView) RowKOTItem.getChildAt(26);
            TextView OriginalRate = (TextView) RowKOTItem.getChildAt(27);
            TextView TaxableValue = (TextView) RowKOTItem.getChildAt(28);
            TextView tvKOTNo = (TextView) RowKOTItem.getChildAt(29);

//            if (printstatus.getText().toString().equalsIgnoreCase("1")) {

            PendingKOT objPendingKOT = new PendingKOT();

            objPendingKOT.setPOS("");
            if (jBillingMode == Byte.parseByte("1")) {
                // TableNumber
                objPendingKOT.setTableNumber(Integer.parseInt(tvTableNumber.getText().toString()));

                // SubUdfNumber
                objPendingKOT.setSubUdfNumber(Integer.parseInt(tvSubUdfValue.getText().toString()));

                // WaiterId
                objPendingKOT.setEmployeeId(Integer.parseInt(tvWaiterNumber.getText().toString()));

                // Checked out status
                objPendingKOT.setIsCheckedOut(0);

                // TableSplitNo
                objPendingKOT.setTableSplitNo(Integer.parseInt(tvTableSplitNo.getText().toString()));

                // Customer Id
                objPendingKOT.setCusId(Integer.valueOf(edtCustId.getText().toString()));

            }

            // Time
            objPendingKOT.setTime(strTime);

            // Item Number
            if (RowKOTItem.getChildAt(0) != null) {
                objPendingKOT.setItemNumber(Integer.parseInt(ItemNumber.getText().toString()));
            }

            // Item Name
            if (RowKOTItem.getChildAt(1) != null) {
                objPendingKOT.setItemName(ItemName.getText().toString());
            }

            // Item Name
            if (RowKOTItem.getChildAt(2) != null) {
                objPendingKOT.setHSNCode(hsn.getText().toString());
            }

            // Quantity
            // Quantity
            if (RowKOTItem.getChildAt(3) != null) {
                String qty_str = Quantity.getText().toString();
                double qty_d = 0.00;
                if(qty_str.equals("")) {
                    Quantity.setText("0.00");
                }else
                {
                    qty_d = Double.parseDouble(qty_str);
                }

                objPendingKOT.setQuantity(Float.parseFloat(String.format("%.2f",qty_d)));
            }

            // Rate
            if (RowKOTItem.getChildAt(4) != null) {
                String rate_str = Rate.getText().toString();
                double rate_d = 0.00;
                if(rate_str.equals("")) {
                    Rate.setText("0.00");
                }else
                {
                    rate_d = Double.parseDouble(rate_str);
                }
                objPendingKOT.setRate(Float.parseFloat(String.format("%.2f",rate_d)));
            }


            // Amount
            if (RowKOTItem.getChildAt(5) != null) {
                objPendingKOT.setAmount(Double.parseDouble(Amount.getText().toString()));
            }

            // Sales Tax %
            if (RowKOTItem.getChildAt(6) != null) {
                objPendingKOT.setTaxPercent(Float.parseFloat(SalesTaxPercent.getText().toString()));
            }

            // Sales Tax Amount
            if (RowKOTItem.getChildAt(7) != null) {
                objPendingKOT.setTaxAmount(Double.parseDouble(SalesTaxAmount.getText().toString()));
            }

            // Discount %
            if (RowKOTItem.getChildAt(8) != null) {
                objPendingKOT.setDiscountPercent(Float.parseFloat(DiscountPercent.getText().toString()));
            }

            // Discount Amount
            if (RowKOTItem.getChildAt(9) != null) {
                objPendingKOT.setDiscountAmount(Float.parseFloat(DiscountAmount.getText().toString()));
            }

            // Department Code
            if (RowKOTItem.getChildAt(10) != null) {
                objPendingKOT.setDeptCode(Integer.parseInt(DeptCode.getText().toString()));
            }

            // Category Code
            if (RowKOTItem.getChildAt(11) != null) {
                objPendingKOT.setCategCode(Integer.parseInt(CategCode.getText().toString()));
            }

            // Kitchen Code
            if (RowKOTItem.getChildAt(12) != null) {
                objPendingKOT.setKitchenCode(Integer.parseInt(KitchenCode.getText().toString()));
            }

            // Tax Type
            if (RowKOTItem.getChildAt(13) != null) {
                objPendingKOT.setTaxType(Integer.parseInt(TaxType.getText().toString()));
            }

            // Modifier Amount
            if (RowKOTItem.getChildAt(14) != null) {
                objPendingKOT.setModifierAmount(Float.parseFloat(ModifierAmount.getText().toString()));
            }

            // Service Tax Percent
            if (RowKOTItem.getChildAt(15) != null) {
                objPendingKOT.setServiceTaxPercent(Float.parseFloat(ServiceTaxPercent.getText().toString()));
            }

            // Service Tax Amount
            if (RowKOTItem.getChildAt(16) != null) {
                objPendingKOT.setServiceTaxAmount(Double.parseDouble(ServiceTaxAmount.getText().toString()));
            }

            // SupplyType - G/S
            if (RowKOTItem.getChildAt(17) != null) {
                objPendingKOT.setSupplyType(SupplyType.getText().toString());
            }
            if (RowKOTItem.getChildAt(21) != null) {
                if (printstatus.getText().toString().equals("0"))
                    objPendingKOT.setPrintKOTStatus(0);
                else if (printstatus.getText().toString().equals("1") && iPrintKOTStatus == 0)
                    objPendingKOT.setPrintKOTStatus(0);
                else
                    objPendingKOT.setPrintKOTStatus(1);
            }
//            objPendingKOT.setPrintKOTStatus(iPrintKOTStatus);
            if(RowKOTItem.getChildAt(22)!=null)
            {
                objPendingKOT.setUOM(UOM.getText().toString());
            }
            if(RowKOTItem.getChildAt(23)!=null)
            {
                objPendingKOT.setIGSTRate(Float.parseFloat(IGSTRate.getText().toString()));
            }
            if(RowKOTItem.getChildAt(24)!=null)
            {
                objPendingKOT.setIGSTAmount(Float.parseFloat(IGSTAmt.getText().toString()));
            }
            if(RowKOTItem.getChildAt(25)!=null)
            {
                objPendingKOT.setCessRate(Float.parseFloat(cessRate.getText().toString()));
            }
            if(RowKOTItem.getChildAt(26)!=null)
            {
                objPendingKOT.setCessAmount(Double.parseDouble(cessAmt.getText().toString()));
            }
            if(RowKOTItem.getChildAt(27)!=null)
            {
                objPendingKOT.setOriginalrate(Double.parseDouble(OriginalRate.getText().toString()));
            }
            if(RowKOTItem.getChildAt(28)!=null)
            {
                objPendingKOT.setTaxableValue(Double.parseDouble(TaxableValue.getText().toString()));
                //Log.d("","Richa : taxableVal:"+objPendingKOT.getTaxableValue());
            }
            if (RowKOTItem.getChildAt(29) !=null)
            {
//                    objPendingKOT.setTokenNumber(Integer.parseInt(tvKOTNo.getText().toString()));
                objPendingKOT.setTokenNumber(Integer.parseInt(tvKOTNo.getText().toString()));
                Log.i("sachin_kot", tvKOTNo.getText().toString());
            }

            // Order Mode
            objPendingKOT.setOrderMode(jBillingMode);

            // KOT No
            // objPendingKOT.setTokenNumber(iTokenNumber);

            // Print KOT STatus


            billKotItems.add(objPendingKOT);
//            }
        }
        return billKotItems;
    }

    /*************************************************************************************************************************************
     * Inserts all the ordered item data to database with table number as
     * reference
     *************************************************************************************************************************************/
    private void InsertKOTItems() {
        // Inserted Row Id in database table

        ArrayList<PendingKOT> billKotItems = getNewItemsList();

        long lResult = 0;

        for (int i=0; i<billKotItems.size(); i++) {
            lResult = dbBillScreen.addKOT(billKotItems.get(i));
        }

        if (lResult > 0 ) {
//            iKOTNo += 1;
//            iKOTNo = dbBillScreen.getKOTNo();

            int kotNo  = dbBillScreen.getKOTNo()-1;
            if(kotNo < iKOTNo)
                dbBillScreen.updateKOTNo(iKOTNo);
//            iKOTNo = -1;
        }

    }

    /*************************************************************************************************************************************
     * Loads KOT order items to billing table
     *
     * @param crsrBillItems : Cursor with KOT order item details
     *************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    private void LoadKOTItems(Cursor crsrBillItems, boolean isPrintKOTStatusSent ) {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        TableRow rowItem;
        TextView tvName, tvHSn, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, // tvQty,
                // tvRate,
                tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent,
                tvServiceTaxAmt, tvKOTNo;
        EditText etQty, etRate;
        CheckBox Number;
        ImageButton ImgDelete;
        try{
            if (crsrBillItems.moveToFirst()) {

                // Get Token number
                //iTokenNumber = crsrBillItems.getInt(crsrBillItems.getColumnIndex("TokenNumber"));

           /* et_pos.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("POS")));
            if (et_pos.getText().toString().equals(""))
            {
                chk_interstate.setChecked(false);
            }
            else
            {
                chk_interstate.setChecked(true);
            }
            */// Get waiter Id
                tvWaiterNumber.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("EmployeeId")));

                // Get Table number
                tvTableNumber.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TableNumber")));

                // Get Table Split No
                tvTableSplitNo.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TableSplitNo")));

                // Get Sub Udf number
                tvSubUdfValue.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SubUdfNumber")));

                // Get Cust Id
                edtCustId.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CustId")));
                Cursor crsrCustomer = db.getCustomerById(crsrBillItems.getInt(crsrBillItems.getColumnIndex("CustId")));
                if (crsrCustomer.moveToFirst()) {
                    edtCustPhoneNo.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                    edtCustName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                    edtCustAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
                }

                // Display items in table
                do {
                    rowItem = new TableRow(myContext);
                    rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    // Item Number
                    Number = new CheckBox(myContext);
                    Number.setWidth(40);
                    Number.setTextSize(0);
                    Number.setTextColor(Color.TRANSPARENT);
                    Number.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));

                    // Item Name
                    tvName = new TextView(myContext);
                /*tvName.setWidth(135);
                tvName.setTextSize(11);*/
                    tvName.setWidth(mItemNameWidth); // 154px ~= 230dp
                    tvName.setTextSize(mDataMiniDeviceTextsize);
                    tvName.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemName")));

                    //hsn code
                    tvHSn = new TextView(myContext);
                /*tvHSn.setWidth(67); // 154px ~= 230dp
                tvHSn.setTextSize(11);*/

                    tvHSn.setWidth(mHSNWidth); // 154px ~= 230dp
                    tvHSn.setTextSize(mDataMiniDeviceTextsize);
                    tvHSn.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("HSNCode")));
                    if ( !HSNEnable_out.equals("1")) {
                        tvHSn.setVisibility(View.INVISIBLE);
                    }

                    // Quantity
                    etQty = new EditText(this);
                    etQty.setWidth(mQuantityWidth); // 57px ~= 85dp
                    etQty.setTextSize(mDataMiniDeviceTextsize);
                    etQty.setSelectAllOnFocus(true);
                    etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    // Read quantity from weighing scale if read from weigh
                    // scale is set in settings
                    if (jWeighScale == 0) {
                        etQty.setText("1.00");
                    } else {
                        etQty.setText(String.format("%.2f", getQuantityFromWeighScale()));
                    }
                    etQty.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Quantity"))));
                    if (crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")).equalsIgnoreCase("1")) {
                        etQty.setEnabled(true);
                    } else {
                        etQty.setEnabled(false);
                    }
                    etQty.setTag("QTY_RATE");
                    etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etInputValidate.ValidateDecimalInput(etQty);
                    //              etQty.setEnabled(true);
                    //etQty.setOnClickListener(Qty_Rate_Click);
                    etQty.setOnKeyListener(Qty_Rate_KeyPressEvent);
                    etQty.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        public void afterTextChanged(Editable s) {
                            Qty_Rate_Edit();
                        }
                    });


                    /*if (BillwithStock == 1) {
                        if (crsrBillItems.getFloat(crsrBillItems.getColumnIndex("Quantity")) < Float.valueOf(etQty.getText().toString())) {
                            String availableQty = crsrBillItems.getString(crsrBillItems.getColumnIndex("Quantity")) ;
                            MsgBox.Show("Warning", "Stock is less, present stock quantity is "
                                    + availableQty);
                            etQty.setText(String.format("%.2f", Double.parseDouble(availableQty)));
                            return;
                        }
                    }*/

                    // Rate
                    etRate = new EditText(this);
//                    etRate.setWidth(70); // 74px ~= 110dp
//                    etRate.setTextSize(11);
                    etRate.setWidth(mRateWidth); // 74px ~= 110dp
                    etRate.setTextSize(mDataMiniDeviceTextsize);
                    //etRate.setSelectAllOnFocus(true);
                    etRate.setWidth(mRateWidth); // 74px ~= 110dp
                    etRate.setTextSize(mDataMiniDeviceTextsize);
                    etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    etRate.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Rate"))));
                    if(crsrSettings!=null && crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) !=1) // reverse TAx
                    {
                        etRate.setEnabled(false);
                        etRate.setTextColor(getResources().getColor(R.color.black));
                    }


                    // Amount
                    tvAmount = new TextView(myContext);
                    tvAmount.setTextColor(getResources().getColor(R.color.black));
                    //tvAmount.setWidth(60);
                    //tvAmount.setTextSize(11);
                    tvAmount.setWidth(mAmountWidth); // 97px ~= 145dp
                    tvAmount.setTextSize(mDataMiniDeviceTextsize);

                    tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                    tvAmount.setText(
                            String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount"))));

                    System.out.print("Amount: "+crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount")));
                    // Sales Tax%
                    tvTaxPercent = new TextView(myContext);
                    tvTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxPercent")));

                    // Sales Tax Amount
                    tvTaxAmt = new TextView(myContext);
                    tvTaxAmt.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("TaxAmount"))));

                    // Discount %
                    tvDiscPercent = new TextView(myContext);
                    tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                    // Discount Amount
                    tvDiscAmt = new TextView(myContext);
                    tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));

                    // Dept Code
                    tvDeptCode = new TextView(myContext);
                    tvDeptCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DeptCode")));

                    // Categ Code
                    tvCategCode = new TextView(myContext);
                    tvCategCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CategCode")));

                    // Kitchen Code
                    tvKitchenCode = new TextView(myContext);
                    tvKitchenCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("KitchenCode")));

                    // Tax Type
                    tvTaxType = new TextView(myContext);
                    tvTaxType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxType")));

                    // Modifier Amount
                    tvModifierCharge = new TextView(myContext);
                    tvModifierCharge.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ModifierAmount")));

                    // Service Tax %
                    tvServiceTaxPercent = new TextView(myContext);
                    tvServiceTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxPercent")));

                    // Service Tax Amount
                    tvServiceTaxAmt = new TextView(myContext);
                    tvServiceTaxAmt.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("ServiceTaxAmount"))));

                    // Service Tax Amount
                    TextView tvSupplyType = new TextView(myContext);
                    tvSupplyType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SupplyType")));

                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    ImgDelete = new ImageButton(myContext);
                    ImgDelete.setImageResource(res);
                    ImgDelete.setVisibility(View.INVISIBLE);

                    TextView tvSpace = new TextView(myContext);
                    tvSpace.setText("        ");

                    TextView tvSpace1 = new TextView(myContext);
                    tvSpace1.setText("       ");

                    TextView tvPrintKOTStatus = new TextView(myContext);
                    /*if(REPRINT_KOT == 1)
                        tvPrintKOTStatus.setText("1");
                    else
                        tvPrintKOTStatus.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")));
                     */

                    if(REPRINT_KOT == 1)
                        tvPrintKOTStatus.setText("1");
                    else if(isPrintKOTStatusSent)
                        tvPrintKOTStatus.setText(String.valueOf(iPrintKOTStatus));
                    else
                        tvPrintKOTStatus.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")));


                    TextView tvIGSTRate = new TextView(BillingDineInActivity.this);
                    tvIGSTRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTRate")));

                    TextView tvIGSTAmt = new TextView(BillingDineInActivity.this);
                    tvIGSTAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTAmount")));

                    TextView tvcessRate = new TextView(BillingDineInActivity.this);
                    tvcessRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessRate")));

                    TextView tvcessAmt = new TextView(BillingDineInActivity.this);
                    tvcessAmt.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("cessAmount"))));

                    TextView tvOriginalRate = new TextView(BillingDineInActivity.this);
                    tvOriginalRate.setText(String.format("%.2f",crsrBillItems.getDouble(crsrBillItems.getColumnIndex("OriginalRate"))));

                    TextView tvTaxableValue = new TextView(BillingDineInActivity.this);
                    tvTaxableValue.setText(String.format("%.2f",crsrBillItems.getDouble(crsrBillItems.getColumnIndex("TaxableValue"))));


                    TextView tvUOM = new TextView(BillingDineInActivity.this);
                    tvUOM.setWidth(50);
                    tvUOM.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("UOM")));

                    tvKOTNo = new TextView(myContext);
                    tvKOTNo.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TokenNumber")));





                    // Add all text views and edit text to Item Row
                    // rowItem.addView(tvNumber);
                    rowItem.addView(Number);//0
                    rowItem.addView(tvName);//1
                    rowItem.addView(tvHSn);//2
                    rowItem.addView(etQty);//3
                    rowItem.addView(etRate);//4
                    rowItem.addView(tvAmount);//5
                    rowItem.addView(tvTaxPercent);//6
                    rowItem.addView(tvTaxAmt);//7
                    rowItem.addView(tvDiscPercent);//8
                    rowItem.addView(tvDiscAmt);//9
                    rowItem.addView(tvDeptCode);//10
                    rowItem.addView(tvCategCode);//11
                    rowItem.addView(tvKitchenCode);//12
                    rowItem.addView(tvTaxType);//13
                    rowItem.addView(tvModifierCharge);//14
                    rowItem.addView(tvServiceTaxPercent);//15
                    rowItem.addView(tvServiceTaxAmt);//16
                    rowItem.addView(tvSupplyType);//17
                    rowItem.addView(tvSpace);//18
                    rowItem.addView(ImgDelete);//19
                    rowItem.addView(tvSpace1);//20
                    rowItem.addView(tvPrintKOTStatus);//21
                    rowItem.addView(tvUOM);//22
                    rowItem.addView(tvIGSTRate);//23
                    rowItem.addView(tvIGSTAmt);//24
                    rowItem.addView(tvcessRate);//25
                    rowItem.addView(tvcessAmt);//26
                    rowItem.addView(tvOriginalRate);//27
                    rowItem.addView(tvTaxableValue);//28
                    rowItem.addView(tvKOTNo);//29

                    // Add row to table
                    tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                } while (crsrBillItems.moveToNext());

                REPRINT_KOT =0;

                CalculateTotalAmount();
                btnPreviewBill.setEnabled(true);
                Log.d("LoadKOTItems", "Items loaded successfully");
            } else {
                Log.d("LoadKOTItems", "No rows in cursor");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            MsgBox.Show("Oops","Some error occured while loading KOT");
        }

    }

    /*private void LoadKOTItems_old(Cursor crsrBillItems) {


        if (crsrBillItems.moveToFirst()) {

            // Get Token number
            iTokenNumber = crsrBillItems.getInt(crsrBillItems.getColumnIndex("TokenNumber"));


           // Get waiter Id
            String empId = crsrBillItems.getString(crsrBillItems.getColumnIndex("EmployeeId"));
            tvWaiterNumber.setText(empId);
            Cursor cursor = db.getUsers_counter((empId));
            if(cursor.moveToFirst())
                tvWaiterName.setText(cursor.getString(cursor.getColumnIndex("Name")));

            // Get Table number
            tvTableNumber.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TableNumber")));

            // Get Table Split No
            tvTableSplitNo.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TableSplitNo")));

            // Get Sub Udf number
            tvSubUdfValue.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SubUdfNumber")));

            // Get Cust Id
            edtCustId.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CustId")));
            Cursor crsrCustomer = dbBillScreen.getCustomer(crsrBillItems.getInt(crsrBillItems.getColumnIndex("CustId")));
            if (crsrCustomer.moveToFirst()) {
                edtCustPhoneNo.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                edtCustName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                edtCustAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
            }

            // Display items in table
            do {
                EditTextInputHandler etInputValidate = new EditTextInputHandler();
                TableRow rowItem;
                TextView tvName, tvHSn, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, // tvQty,
                        // tvRate,
                        tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent,
                        tvServiceTaxAmt;
                EditText etQty, etRate;
                CheckBox Number;
                ImageButton ImgDelete;

                rowItem = new TableRow(myContext);
                rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Item Number
                Number = new CheckBox(BillingDineInActivity.this);
                Number.setWidth(40);
                Number.setTextSize(0);
                Number.setTextColor(Color.TRANSPARENT);
                Number.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));

                // Item Name
                tvName = new TextView(BillingDineInActivity.this);
                tvName.setWidth(135);
                tvName.setTextSize(11);
                tvName.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemName")));

                //hsn code
                tvHSn = new TextView(BillingDineInActivity.this);
                tvHSn.setWidth(67); // 154px ~= 230dp
                tvHSn.setTextSize(11);
                if ((HSNEnable_out != null) && HSNEnable_out.equals("1")) {
                    tvHSn.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("HSNCode")));
                }



                // Quantity
                etQty = new EditText(BillingDineInActivity.this);
                etQty.setWidth(55);
                etQty.setTextSize(11);
                if (crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")).equalsIgnoreCase("1")) {
                    etQty.setEnabled(true);
                    etQty.setOnClickListener(Qty_Rate_Click);
                    etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    etQty.setOnKeyListener(Qty_Rate_KeyPressEvent);
                    etInputValidate.ValidateDecimalInput(etQty);
                    etQty.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        public void afterTextChanged(Editable s) {
                            Qty_Rate_Edit();
                        }
                    });
                } else {
                    etQty.setEnabled(false);
                }
                etQty.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Quantity"))));
                //etQty.setSelectAllOnFocus(true);
                etQty.setTag("QTY_RATE");



                // Rate
                etRate = new EditText(BillingDineInActivity.this);
                etRate.setWidth(70);
                etRate.setTextSize(11);
                etRate.setTag("QTY_RATE");
                etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                etRate.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Rate"))));
                    if (crsrSettings.getInt(crsrSettings.getColumnIndex("PriceChange")) == 0) {
                        etRate.setEnabled(false);
                    } else {
                        etRate.setEnabled(true);
                        etRate.setOnClickListener(Qty_Rate_Click);
                        etRate.setOnKeyListener(Qty_Rate_KeyPressEvent);
                        etInputValidate.ValidateDecimalInput(etRate);
                        etRate.addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            public void afterTextChanged(Editable s) {
                                Qty_Rate_Edit();
                            }
                        });}

                // Discount %
                tvDiscPercent = new TextView(myContext);
                tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                // Discount Amount
                double disAMT =0;
                tvDiscAmt = new TextView(BillingDineInActivity.this);
                tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));
                if(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")) !=null)
                    disAMT = crsrBillItems.getDouble(crsrBillItems.getColumnIndex("DiscountAmount"));

                // Amount
                tvAmount = new TextView(BillingDineInActivity.this);
                tvAmount.setWidth(60);
                tvAmount.setTextSize(11);
                tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                tvAmount.setText(
                        String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount"))));
                System.out.println("Amount = "+tvAmount.getText().toString());

                // Sales Tax%
                tvTaxPercent = new TextView(BillingDineInActivity.this);
                tvTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxPercent")));

                // Sales Tax Amount
                tvTaxAmt = new TextView(BillingDineInActivity.this);
                tvTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxAmount")));

                // Discount %
                tvDiscPercent = new TextView(BillingDineInActivity.this);
                tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                // Discount Amount
                tvDiscAmt = new TextView(BillingDineInActivity.this);
                tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));

                // Dept Code
                tvDeptCode = new TextView(BillingDineInActivity.this);
                tvDeptCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DeptCode")));

                // Categ Code
                tvCategCode = new TextView(BillingDineInActivity.this);
                tvCategCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CategCode")));

                // Kitchen Code
                tvKitchenCode = new TextView(BillingDineInActivity.this);
                tvKitchenCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("KitchenCode")));

                // Tax Type
                tvTaxType = new TextView(BillingDineInActivity.this);
                tvTaxType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxType")));

                // Modifier Amount
                tvModifierCharge = new TextView(BillingDineInActivity.this);
                tvModifierCharge.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ModifierAmount")));

                // Service Tax %
                tvServiceTaxPercent = new TextView(BillingDineInActivity.this);
                tvServiceTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxPercent")));

                // Service Tax Amount
                tvServiceTaxAmt = new TextView(BillingDineInActivity.this);
                tvServiceTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxAmount")));

                // Service Tax Amount
                TextView tvSupplyType = new TextView(BillingDineInActivity.this);
                tvSupplyType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SupplyType")));

                TextView tvIGSTRate = new TextView(BillingDineInActivity.this);
                tvIGSTRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTRate")));

                TextView tvIGSTAmt = new TextView(BillingDineInActivity.this);
                tvIGSTAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTAmount")));

                 TextView tvcessRate = new TextView(BillingDineInActivity.this);
                tvcessRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessRate")));

                TextView tvcessAmt = new TextView(BillingDineInActivity.this);
                tvcessAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessAmount")));


                TextView tvUOM = new TextView(BillingDineInActivity.this);
                tvUOM.setWidth(50);
                tvUOM.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("UOM")));

                // Delete
                int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                ImgDelete = new ImageButton(myContext);
                ImgDelete.setImageResource(res);
                ImgDelete.setVisibility(View.INVISIBLE);



                TextView tvSpace = new TextView(BillingDineInActivity.this);
                tvSpace.setText("        ");

                TextView tvSpace1 = new TextView(BillingDineInActivity.this);
                tvSpace1.setText("       ");

                TextView tvPrintKOTStatus = new TextView(BillingDineInActivity.this);
                if(REPRINT_KOT == 1)
                    tvPrintKOTStatus.setText("1");
                else
                    tvPrintKOTStatus.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")));



                // Add all text views and edit text to Item Row
                // rowItem.addView(tvNumber);
                System.out.println("Amount1-- = "+tvAmount.getText().toString());
                rowItem.addView(Number);
                rowItem.addView(tvName);
                rowItem.addView(tvHSn);
                rowItem.addView(etQty);
                rowItem.addView(etRate);
                rowItem.addView(tvAmount);
                rowItem.addView(tvTaxPercent);
                rowItem.addView(tvTaxAmt);
                rowItem.addView(tvDiscPercent);
                rowItem.addView(tvDiscAmt);
                rowItem.addView(tvDeptCode);
                rowItem.addView(tvCategCode);
                rowItem.addView(tvKitchenCode);
                rowItem.addView(tvTaxType);
                rowItem.addView(tvModifierCharge);
                rowItem.addView(tvServiceTaxPercent);
                rowItem.addView(tvServiceTaxAmt);
                rowItem.addView(tvSupplyType);
                rowItem.addView(tvSpace);
                rowItem.addView(ImgDelete);
                rowItem.addView(tvSpace1);
                rowItem.addView(tvPrintKOTStatus);
                rowItem.addView(tvUOM);//22
                rowItem.addView(tvIGSTRate);//23
                rowItem.addView(tvIGSTAmt);//24
                rowItem.addView(tvcessRate);//25
                rowItem.addView(tvcessAmt);//26
                // Add row to table
                tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                System.out.println("Amount1-- = "+tvAmount.getText().toString());
                System.out.println("Reprinttinf");
                for(int i=0;i<tblOrderItems.getChildCount();i++)
                {
                    TableRow row = (TableRow)tblOrderItems.getChildAt(i);
                    TextView tvAmount1 = (TextView)row.getChildAt(5);
                    System.out.println("Amount11--11= = "+tvAmount1.getText().toString());
                }
            } while (crsrBillItems.moveToNext());

            for(int i=0;i<tblOrderItems.getChildCount();i++)
            {
                TableRow row = (TableRow)tblOrderItems.getChildAt(i);
                TextView tvAmount1 = (TextView)row.getChildAt(5);
                System.out.println("Amount11--111= = "+tvAmount1.getText().toString());
            }

            REPRINT_KOT =0;

            CalculateTotalAmount();
            for(int i=0;i<tblOrderItems.getChildCount();i++)
            {
                TableRow row = (TableRow)tblOrderItems.getChildAt(i);
                TextView tvAmount1 = (TextView)row.getChildAt(5);
                System.out.println("Amount11 = "+tvAmount1.getText().toString());
            }
            Log.d("LoadKOTItems", "Items loaded successfully");
        } else {
            Log.d("LoadKOTItems", "No rows in cursor");
        }
    }
*/
    /*************************************************************************************************************************************
     * Loads KOT order items to billing table
     *
     * @param crsrBillItems : Cursor with KOT order item details
     *************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    private void LoadModifyKOTItems(Cursor crsrBillItems) {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        TableRow rowItem;
        TextView tvHSn, tvName, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, // tvQty,
                // tvRate,
                tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent,
                tvServiceTaxAmt;
        EditText etQty, etRate;
        CheckBox Number;
        ImageButton ImgDelete;
        try{
            Cursor billsettingcrsr = dbBillScreen.getBillSetting();
            if(!(billsettingcrsr!=null && billsettingcrsr.moveToFirst()))
            {
                MsgBox.Show("Error","Some issue occurred while retreiving  the bill settings. Modify KOT cannot be performed. ");
                return;
            }

            if (crsrBillItems.moveToFirst()) {

                iTokenNumber = crsrBillItems.getInt(crsrBillItems.getColumnIndex("TokenNumber"));

           /* et_pos.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("POS")));
            if (et_pos.getText().toString().equals(""))
            {
                chk_interstate.setChecked(false);
            }
            else
            {
                chk_interstate.setChecked(true);
            }
            */// Get waiter Id
                tvWaiterNumber.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("EmployeeId")));

                // Get Table number
                tvTableNumber.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TableNumber")));

                // Get Table Split No
                tvTableSplitNo.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TableSplitNo")));

                // Get Sub Udf number
                tvSubUdfValue.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SubUdfNumber")));

                // Get Cust Id
                edtCustId.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CustId")));
                Cursor crsrCustomer = db.getCustomerById(crsrBillItems.getInt(crsrBillItems.getColumnIndex("CustId")));
                if (crsrCustomer.moveToFirst()) {
                    edtCustPhoneNo.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                    edtCustName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                    edtCustAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
                }

                // Display items in table
                do {
                    rowItem = new TableRow(myContext);
                    rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    // Item Number
                    Number = new CheckBox(myContext);
                    Number.setWidth(40);
                    Number.setTextSize(0);
                    Number.setTextColor(Color.TRANSPARENT);
                    Number.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));

                    // Item Name
                    tvName = new TextView(myContext);
                /*tvName.setWidth(135);
                tvName.setTextSize(11);*/
                    tvName.setWidth(mItemNameWidth); // 154px ~= 230dp
                    tvName.setTextSize(mDataMiniDeviceTextsize);
                    tvName.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemName")));
                    tvName.setTextColor(getResources().getColor(R.color.black));

                    //hsn code
                    tvHSn = new TextView(myContext);
                /*tvHSn.setWidth(67); // 154px ~= 230dp
                tvHSn.setTextSize(11);*/
                    tvHSn.setWidth(mHSNWidth); // 154px ~= 230dp
                    tvHSn.setTextSize(mDataMiniDeviceTextsize);
                    tvHSn.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("HSNCode")));
                    if ( !HSNEnable_out.equals("1")) {
                        tvHSn.setVisibility(View.INVISIBLE);
                    }
                    tvHSn.setTextColor(getResources().getColor(R.color.black));
                    // Quantity
                    etQty = new EditText(myContext);
                    etQty.setWidth(mQuantityWidth); // 57px ~= 85dp
                    etQty.setTextSize(mDataMiniDeviceTextsize);

                    if (crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")).equalsIgnoreCase("1")) {
                        etQty.setEnabled(true);
                    } else {
                        etQty.setEnabled(false);
                        etQty.setTextColor(getResources().getColor(R.color.black));
                    }
                    etQty.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Quantity"))));
                    etQty.setSelectAllOnFocus(true);
                    etQty.setTag("QTY_RATE");
                    etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etQty.setTextColor(getResources().getColor(R.color.black));
                    etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);




                    // Rate
                    etRate = new EditText(myContext);
                    //etRate.setWidth(70);
                    etRate.setEnabled(false);
                    //etRate.setTextSize(11);
                    etRate.setWidth(mRateWidth); // 74px ~= 110dp
                    etRate.setTextSize(mDataMiniDeviceTextsize);
                    etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                    etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    etRate.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Rate"))));
                    if (billsettingcrsr.getString(billsettingcrsr.getColumnIndex("PriceChange")).equals("1")) {
                        etRate.setEnabled(true);
                        etRate.setSelectAllOnFocus(true);
                    } else {
                        etRate.setEnabled(false);
                        etRate.setTextColor(getResources().getColor(R.color.black));
                    }


                    // Amount
                    tvAmount = new TextView(myContext);
                    //tvAmount.setWidth(60);
                    //tvAmount.setTextSize(11);
                    tvAmount.setWidth(mAmountWidth); // 97px ~= 145dp
                    tvAmount.setTextSize(mDataMiniDeviceTextsize);
                    tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                    tvAmount.setText(
                            String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount"))));
                    tvAmount.setTextColor(getResources().getColor(R.color.black));

                    // Sales Tax%
                    tvTaxPercent = new TextView(myContext);
                    tvTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxPercent")));

                    // Sales Tax Amount
                    tvTaxAmt = new TextView(myContext);
                    tvTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxAmount")));

                    // Discount %
                    tvDiscPercent = new TextView(myContext);
                    tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                    // Discount Amount
                    tvDiscAmt = new TextView(myContext);
                    tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));


// Dept Code
                    tvDeptCode = new TextView(myContext);
                    tvDeptCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DeptCode")));

                    // Categ Code
                    tvCategCode = new TextView(myContext);
                    tvCategCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CategCode")));

                    // Kitchen Code
                    tvKitchenCode = new TextView(myContext);
                    tvKitchenCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("KitchenCode")));

                    // Tax Type
                    tvTaxType = new TextView(myContext);
                    tvTaxType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxType")));

                    // Modifier Amount
                    tvModifierCharge = new TextView(myContext);
                    tvModifierCharge.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ModifierAmount")));

                    // Service Tax %
                    tvServiceTaxPercent = new TextView(myContext);
                    tvServiceTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxPercent")));

                    // Service Tax Amount
                    tvServiceTaxAmt = new TextView(myContext);
                    tvServiceTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxAmount")));

                    // Service Tax Amount
                    TextView tvSupplyType = new TextView(myContext);
                    tvSupplyType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SupplyType")));


                    // Delete
                    int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                    ImgDelete = new ImageButton(myContext);
                    // ImgDeleteKOT.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));
                    ImgDelete.setImageResource(res);
                    ImgDelete.setOnClickListener(mListener);

                    TextView tvSpace = new TextView(myContext);
                    tvSpace.setText("        ");

                    TextView tvSpace1 = new TextView(myContext);
                    tvSpace1.setText("       ");

                    TextView tvPrintKOTStatus = new TextView(myContext);
                    if(REPRINT_KOT == 1)
                        tvPrintKOTStatus.setText("1");
                    else
                        tvPrintKOTStatus.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")));

                    TextView tvIGSTRate = new TextView(BillingDineInActivity.this);
                    tvIGSTRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTRate")));

                    TextView tvIGSTAmt = new TextView(BillingDineInActivity.this);
                    tvIGSTAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTAmount")));

                    TextView tvcessRate = new TextView(BillingDineInActivity.this);
                    tvcessRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessRate")));

                    TextView tvcessAmt = new TextView(BillingDineInActivity.this);
                    tvcessAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessAmount")));

                    TextView tvOriginalRate = new TextView(BillingDineInActivity.this);
                    tvOriginalRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("OriginalRate")));

                    TextView tvTaxableValue = new TextView(BillingDineInActivity.this);
                    tvTaxableValue.setText(String.format("%.2f",crsrBillItems.getDouble(crsrBillItems.getColumnIndex("TaxableValue"))));

                    TextView tvKOTNo = new TextView(BillingDineInActivity.this);
                    tvKOTNo.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TokenNumber")));


                    TextView tvUOM = new TextView(BillingDineInActivity.this);
                    tvUOM.setWidth(50);
                    tvUOM.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("UOM")));

                    // Add all text views and edit text to Item Row
                    // rowItem.addView(tvNumber);
                    rowItem.addView(Number);
                    rowItem.addView(tvName);
                    rowItem.addView(tvHSn);
                    rowItem.addView(etQty);
                    rowItem.addView(etRate);
                    rowItem.addView(tvAmount);
                    rowItem.addView(tvTaxPercent);
                    rowItem.addView(tvTaxAmt);
                    rowItem.addView(tvDiscPercent);
                    rowItem.addView(tvDiscAmt);
                    rowItem.addView(tvDeptCode);
                    rowItem.addView(tvCategCode);
                    rowItem.addView(tvKitchenCode);
                    rowItem.addView(tvTaxType);
                    rowItem.addView(tvModifierCharge);
                    rowItem.addView(tvServiceTaxPercent);
                    rowItem.addView(tvServiceTaxAmt);
                    rowItem.addView(tvSupplyType);
                    rowItem.addView(tvSpace);
                    rowItem.addView(ImgDelete);
                    rowItem.addView(tvSpace1);
                    rowItem.addView(tvPrintKOTStatus);
                    rowItem.addView(tvUOM);//22
                    rowItem.addView(tvIGSTRate);//23
                    rowItem.addView(tvIGSTAmt);//24
                    rowItem.addView(tvcessRate);//25
                    rowItem.addView(tvcessAmt);//26
                    rowItem.addView(tvOriginalRate); //27
                    rowItem.addView(tvTaxableValue);//28
                    rowItem.addView(tvKOTNo);//29

                    // Add row to table
                    tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                } while (crsrBillItems.moveToNext());

                CalculateTotalAmount();

            } else {
                Log.d("LoadKOTItems", "No rows in cursor");
            }

        }catch(Exception e)
        {
            e.printStackTrace();
            MsgBox.Show("Oops","Some error occured while modifying KOT");
        }
    }
/*
private void LoadModifyKOTItems_old(Cursor crsrBillItems) {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        TableRow rowItem;
        TextView tvHSn, tvName, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, // tvQty,
                // tvRate,
                tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent,
                tvServiceTaxAmt;
        EditText etQty, etRate;
        CheckBox Number;
        ImageButton ImgDelete;

        if (crsrBillItems.moveToFirst()) {

            // Get Token number
            iTokenNumber = crsrBillItems.getInt(crsrBillItems.getColumnIndex("TokenNumber"));

            // Get waiter Id
            tvWaiterNumber.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("EmployeeId")));

            // Get Table number
            tvTableNumber.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TableNumber")));

            // Get Sub Udf number
            tvSubUdfValue.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SubUdfNumber")));

            // Get Cust Id
            edtCustId.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CustId")));
            Cursor crsrCustomer = dbBillScreen.getCustomer(crsrBillItems.getInt(crsrBillItems.getColumnIndex("CustId")));
            if (crsrCustomer.moveToFirst()) {
                edtCustPhoneNo.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustContactNumber")));
                edtCustName.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                edtCustAddress.setText(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress")));
            }

            // Display items in table
            do {
                rowItem = new TableRow(myContext);
                rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Item Number
                Number = new CheckBox(myContext);
                Number.setWidth(40);
                Number.setTextSize(0);
                Number.setTextColor(Color.TRANSPARENT);
                Number.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));

                // Item Name
                tvName = new TextView(myContext);
                tvName.setWidth(135);
                tvName.setTextSize(11);
                tvName.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemName")));

                //hsn code
                tvHSn = new TextView(myContext);
                tvHSn.setWidth(67); // 154px ~= 230dp
                tvHSn.setTextSize(11);
                if ((HSNEnable_out != null) && HSNEnable_out.equals("1")) {
                    tvHSn.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("HSNCode")));
                }
                // Quantity
                etQty = new EditText(myContext);
                etQty.setWidth(55);
                etQty.setTextSize(11);
                etQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if (crsrBillItems.getString(crsrBillItems.getColumnIndex("PrintKOTStatus")).equalsIgnoreCase("1")) {
                    etQty.setEnabled(true);
                    etQty.setSelectAllOnFocus(true);
                    etQty.setTag("QTY_RATE");
                    etQty.setOnClickListener(Qty_Rate_Click);
                    etQty.setOnKeyListener(Qty_Rate_KeyPressEvent);
                    etInputValidate.ValidateDecimalInput(etQty);
                    etQty.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        public void afterTextChanged(Editable s) {
                            Qty_Rate_Edit();
                        }
                    });

                } else {
                    etQty.setEnabled(false);
                }
                etQty.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Quantity"))));

                // Rate
                etRate = new EditText(myContext);
                etRate.setWidth(70);
                etRate.setEnabled(false);
                etRate.setTextSize(11);
                etRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,1)});
                etRate.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Rate"))));
                etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if (crsrSettings.getInt(crsrSettings.getColumnIndex("PriceChange")) == 0) {
                    etRate.setEnabled(false);
                } else {
                    etRate.setEnabled(true);
                    etRate.setTag("QTY_RATE");
                    etRate.setOnClickListener(Qty_Rate_Click);
                    etRate.setOnKeyListener(Qty_Rate_KeyPressEvent);
                    etInputValidate.ValidateDecimalInput(etRate);
                    etRate.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        public void afterTextChanged(Editable s) {
                            Qty_Rate_Edit();
                        }
                    });}

                // Amount
                tvAmount = new TextView(myContext);
                tvAmount.setWidth(60);
                tvAmount.setTextSize(11);
                tvAmount.setGravity(Gravity.RIGHT | Gravity.END);
                tvAmount.setText(
                        String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount"))));

                // Sales Tax%
                tvTaxPercent = new TextView(myContext);
                tvTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxPercent")));

                // Sales Tax Amount
                tvTaxAmt = new TextView(myContext);
                tvTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxAmount")));

                // Discount %
                tvDiscPercent = new TextView(myContext);
                tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                // Discount Amount
                tvDiscAmt = new TextView(myContext);
                tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));

                // Dept Code
                tvDeptCode = new TextView(myContext);
                tvDeptCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DeptCode")));

                // Categ Code
                tvCategCode = new TextView(myContext);
                tvCategCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CategCode")));

                // Kitchen Code
                tvKitchenCode = new TextView(myContext);
                tvKitchenCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("KitchenCode")));

                // Tax Type
                tvTaxType = new TextView(myContext);
                tvTaxType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxType")));

                // Modifier Amount
                tvModifierCharge = new TextView(myContext);
                tvModifierCharge.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ModifierAmount")));

                // Service Tax %
                tvServiceTaxPercent = new TextView(myContext);
                tvServiceTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxPercent")));

                // Service Tax Amount
                tvServiceTaxAmt = new TextView(myContext);
                tvServiceTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ServiceTaxAmount")));

                // SupplyType
                TextView SupplyType = new TextView(myContext);
                SupplyType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SupplyType")));
                SupplyType.setWidth(30);

                // rowItem.setClickable(true);
                // rowItem.setOnClickListener(new OnClickListener() {
                // public void onClick(View view) {
                // TableRow tablerow = (TableRow) view;
                // TextView sample = (TextView) tablerow.getChildAt(0);
                //
                // Toast.makeText(myContext, sample.getText().toString(),
                // Toast.LENGTH_LONG).show();
                // }
                // });
                // Delete
                int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                ImgDelete = new ImageButton(myContext);
                // ImgDeleteKOT.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));
                ImgDelete.setImageResource(res);
                ImgDelete.setOnClickListener(mListener);

                TextView tvSpace = new TextView(myContext);
                tvSpace.setText("        ");

                TextView tvSpace1 = new TextView(myContext);
                tvSpace1.setText("       ");

                TextView tvPrintKOTStatus = new TextView(myContext);
                tvPrintKOTStatus.setText("1");

                TextView tvIGSTRate = new TextView(this);
                tvIGSTRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTRate")));

                TextView tvIGSTAmt = new TextView(this);
                tvIGSTAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTAmount")));

                TextView tvcessRate = new TextView(this);
                tvcessRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessRate")));

                TextView tvcessAmt = new TextView(this);
                tvcessAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessAmount")));


                TextView tvUOM = new TextView(this);
                tvUOM.setWidth(50);
                tvUOM.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("UOM")));

                // Add all text views and edit text to Item Row
                // rowItem.addView(tvNumber);
                rowItem.addView(Number);
                rowItem.addView(tvName);
                rowItem.addView(tvHSn);
                rowItem.addView(etQty);
                rowItem.addView(etRate);
                rowItem.addView(tvAmount);
                rowItem.addView(tvTaxPercent);
                rowItem.addView(tvTaxAmt);
                rowItem.addView(tvDiscPercent);
                rowItem.addView(tvDiscAmt);
                rowItem.addView(tvDeptCode);
                rowItem.addView(tvCategCode);
                rowItem.addView(tvKitchenCode);
                rowItem.addView(tvTaxType);
                rowItem.addView(tvModifierCharge);
                rowItem.addView(tvServiceTaxPercent);
                rowItem.addView(tvServiceTaxAmt);
                rowItem.addView(SupplyType);
                rowItem.addView(tvSpace);
                rowItem.addView(ImgDelete);
                rowItem.addView(tvSpace1);
                rowItem.addView(tvPrintKOTStatus);
                rowItem.addView(tvUOM);//22
                rowItem.addView(tvIGSTRate);//23
                rowItem.addView(tvIGSTAmt);//24
                rowItem.addView(tvcessRate);//25
                rowItem.addView(tvcessAmt);//26

                // Add row to table
                tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            } while (crsrBillItems.moveToNext());

            CalculateTotalAmount();

        } else {
            Log.d("LoadKOTItems", "No rows in cursor");
        }
    }
*/

    private View.OnClickListener mListener = new View.OnClickListener() {

        public void onClick(View v) {

            final View v1 = v;
            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);

            AuthorizationDialog
                    .setTitle("Confirmation")
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure to delete this item")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            TableRow tr = (TableRow) v1.getParent();
                            TextView ItemNumber = (TextView) tr.getChildAt(0);
                            TextView KOTNumber = (TextView) tr.getChildAt(29);

                            DeletedKOT objDeletedKOT = new DeletedKOT();
                            objDeletedKOT.setReason("Modified");
                            objDeletedKOT.setEmployeeId(Integer.parseInt(tvWaiterNumber.getText().toString()));
                            objDeletedKOT.setTableNumber(Integer.parseInt(tvTableNumber.getText().toString()));
                            objDeletedKOT.setSubUdfNumber(Integer.parseInt(tvSubUdfValue.getText().toString()));
                            objDeletedKOT.setTokenNumber(iTokenNumber);
                            objDeletedKOT.setTime(String.format("%tR", Time));
                            long lResult = dbBillScreen.addDeletedKOT(objDeletedKOT);
                            Log.v("VoidKOT", "Deleted KOT added, Row ID:" + lResult);
                            lResult = dbBillScreen.deleteKOTItemsByItemToken(ItemNumber.getText().toString(), Integer.parseInt(KOTNumber.getText().toString()),
                                    Integer.parseInt(tvTableNumber.getText().toString()));
                            Log.v("VoidKOT", "Deleted KOT status :" + lResult);
                            DisplayModifyKOT();

                        }
                    })
                    .show();

//            TableRow tr = (TableRow) v.getParent();
//            TextView ItemNumber = (TextView) tr.getChildAt(0);
//
//            DeletedKOT objDeletedKOT = new DeletedKOT();
//            objDeletedKOT.setReason("Modified");
//            objDeletedKOT.setEmployeeId(Integer.parseInt(tvWaiterNumber.getText().toString()));
//            objDeletedKOT.setTableNumber(Integer.parseInt(tvTableNumber.getText().toString()));
//            objDeletedKOT.setSubUdfNumber(Integer.parseInt(tvSubUdfValue.getText().toString()));
//            objDeletedKOT.setTokenNumber(iTokenNumber);
//            objDeletedKOT.setTime(String.format("%tR", Time));
//            long lResult = dbBillScreen.addDeletedKOT(objDeletedKOT);
//            Log.v("VoidKOT", "Deleted KOT added, Row ID:" + lResult);
//            lResult = dbBillScreen.deleteKOTItemsByItemToken(ItemNumber.getText().toString(), iTokenNumber,
//                    Integer.parseInt(tvSubUdfValue.getText().toString()));
//
//            DisplayModifyKOT();
            // Toast.makeText(myContext, sample.getText().toString(),
            // Toast.LENGTH_LONG).show();
        }
    };

    /*************************************************************************************************************************************
     * Updates the item stock quantity after every transaction
     *
     * @param crsrUpdateStock : Cursor containing Item detail
     * @param Quantity        : Item quantity to be subtracted from current item quantity
     *************************************************************************************************************************************/
    private void UpdateItemStock(Cursor crsrUpdateStock, float Quantity) {
        int iResult = 0;
        float fCurrentStock = 0, fNewStock = 0;

        // Get current stock of item
        fCurrentStock = crsrUpdateStock.getFloat(crsrUpdateStock.getColumnIndex("Quantity"));

        // New Stock
        fNewStock = fCurrentStock - Quantity;

        // Update new stock for item
        iResult = dbBillScreen.updateItemStock(crsrUpdateStock.getInt(crsrUpdateStock.getColumnIndex("MenuCode")),
                fNewStock);

        Log.d("UpdateItemStock", "Updated Rows:" + iResult);

    }

    /*************************************************************************************************************************************
     * Insert each bill item to bill items database table
     *************************************************************************************************************************************/
    private void InsertBillItems() {

        // Inserted Row Id in database table
        long lResult = 0;

        // Bill item object
        BillItem objBillItem;

        // Reset TotalItems count
        iTotalItems = 0;

        Cursor crsrUpdateItemStock = null;

        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
            objBillItem = new BillItem();

            TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(iRow);

            // Increment Total item count if row is not empty
            if (RowBillItem.getChildCount() > 0) {
                iTotalItems++;
            }

            // Bill Number
            objBillItem.setBillNumber(tvBillNumber.getText().toString());
            Log.d("InsertBillItems", "InvoiceNo:" + tvBillNumber.getText().toString());

            objBillItem.setBillStatus(1);
            Log.d("InsertBillItems", "Bill Status:1");

            // richa_2012
            //BillingMode
            objBillItem.setBillingMode(String.valueOf(jBillingMode));
            Log.d("InsertBillItems", "Billing Mode :" + String.valueOf(jBillingMode));

            // Item Number
            if (RowBillItem.getChildAt(0) != null) {
                CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                objBillItem.setItemNumber(Integer.parseInt(ItemNumber.getText().toString()));
                Log.d("InsertBillItems", "Item Number:" + ItemNumber.getText().toString());

                crsrUpdateItemStock = dbBillScreen.getItem(Integer.parseInt(ItemNumber.getText().toString()));
            }

            // Item Name
            if (RowBillItem.getChildAt(1) != null) {
                TextView ItemName = (TextView) RowBillItem.getChildAt(1);
                objBillItem.setItemName(ItemName.getText().toString());
                Log.d("InsertBillItems", "Item Name:" + ItemName.getText().toString());
            }

            if (RowBillItem.getChildAt(2) != null) {
                TextView HSN = (TextView) RowBillItem.getChildAt(2);
                objBillItem.setHSNCode(HSN.getText().toString());
                Log.d("InsertBillItems", "Item HSN:" + HSN.getText().toString());
            }

            // Quantity
            double qty_d = 0.00;
            if (RowBillItem.getChildAt(3) != null) {
                EditText Quantity = (EditText) RowBillItem.getChildAt(3);
                String qty_str = Quantity.getText().toString();
                if(qty_str==null || qty_str.equals(""))
                {
                    Quantity.setText("0.00");
                }else
                {
                    qty_d = Double.parseDouble(qty_str);
                }

                objBillItem.setQuantity(Float.parseFloat(String.format("%.2f",qty_d)));
                Log.d("InsertBillItems", "Quantity:" + Quantity.getText().toString());

                if (crsrUpdateItemStock!=null && crsrUpdateItemStock.moveToFirst()) {
                    // Check if item's bill with stock enabled update the stock
                    // quantity
                    Cursor billsettingCursor = dbBillScreen.getBillSetting();
                    if(billsettingCursor!= null && billsettingCursor.moveToFirst())
                    {
                        //String i = billsettingCursor.getString(billsettingCursor.getColumnIndex("BillwithStock"));
                        if (billsettingCursor.getInt(billsettingCursor.getColumnIndex("BillwithStock")) == 1) {
                            UpdateItemStock(crsrUpdateItemStock, Float.parseFloat(Quantity.getText().toString()));
                        }
                    }

                }
            }


            // Rate
            double rate_d = 0.00;
            if (RowBillItem.getChildAt(4) != null) {
                EditText Rate = (EditText) RowBillItem.getChildAt(4);
                String rate_str = Rate.getText().toString();
                if((rate_str==null || rate_str.equals("")))
                {
                    Rate.setText("0.00");
                }else
                {
                    rate_d = Double.parseDouble(rate_str);
                }

                objBillItem.setValue(Float.parseFloat(String.format("%.2f",rate_d)));
                Log.d("InsertBillItems", "Rate:" + Rate.getText().toString());
            }
            // oRIGINAL rate in case of reverse tax
            if (RowBillItem.getChildAt(27) != null) {
                TextView originalRate = (TextView) RowBillItem.getChildAt(27);
                objBillItem.setOriginalRate(Double.parseDouble(originalRate.getText().toString()));
                Log.d("InsertBillItems", "Original Rate  :" + objBillItem.getOriginalRate());
            }
            if (RowBillItem.getChildAt(28) != null) {
                TextView TaxableValue = (TextView) RowBillItem.getChildAt(28);
                objBillItem.setTaxableValue(Double.parseDouble(TaxableValue.getText().toString()));
                Log.d("InsertBillItems", "TaxableValue  :" + objBillItem.getTaxableValue());
            }

            // Amount
            if (RowBillItem.getChildAt(5) != null) {
                TextView Amount = (TextView) RowBillItem.getChildAt(5);
                objBillItem.setAmount(Double.parseDouble(Amount.getText().toString()));
                String reverseTax = "";
                if (!(crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1)) { // forward tax
                    reverseTax = " (Reverse Tax)";
                    objBillItem.setIsReverTaxEnabled("YES");
                }else
                {
                    objBillItem.setIsReverTaxEnabled("NO");
                }
                Log.d("InsertBillItems", "Amount :" + objBillItem.getAmount()+reverseTax);
            }




            // Service Tax Percent
            float sgatTax = 0;
            if (RowBillItem.getChildAt(15) != null) {
                TextView ServiceTaxPercent = (TextView) RowBillItem.getChildAt(15);
                sgatTax = Float.parseFloat(ServiceTaxPercent.getText().toString());
                if (chk_interstate.isChecked()) {
                    objBillItem.setSGSTRate(0.00f);
                    Log.d("InsertBillItems", "SGST Tax %: 0");

                } else {
                    objBillItem.setSGSTRate(Float.parseFloat(ServiceTaxPercent.getText().toString()));
                    Log.d("InsertBillItems", "SGST Tax %: " + objBillItem.getSGSTRate());
                }
            }

            // Service Tax Amount
            double sgstAmt = 0;
            if (RowBillItem.getChildAt(16) != null) {
                TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                sgstAmt = Double.parseDouble(ServiceTaxAmount.getText().toString());
                if (chk_interstate.isChecked()) {
                    objBillItem.setSGSTAmount(0.00f);
                    Log.d("InsertBillItems", "SGST Amount : 0" );

                } else {
                    objBillItem.setSGSTAmount(Double.parseDouble(String.format("%.2f", sgstAmt)));
                    Log.d("InsertBillItems", "SGST Amount : " + objBillItem.getSGSTAmount());
                }
            }

            // Sales Tax %
            if (RowBillItem.getChildAt(6) != null) {
                TextView SalesTaxPercent = (TextView) RowBillItem.getChildAt(6);
                float cgsttax = (Float.parseFloat(SalesTaxPercent.getText().toString()));
                if (chk_interstate.isChecked()) {
                    //objBillItem.setIGSTRate(Float.parseFloat(String.format("%.2f", cgsttax + sgatTax)));
                    //Log.d("InsertBillItems", " IGST Tax %: " + objBillItem.getIGSTRate());
                    objBillItem.setCGSTRate(0.00f);
                    Log.d("InsertBillItems", " CGST Tax %: 0.00");
                }else{
                    // objBillItem.setIGSTRate(0.00f);
                    // Log.d("InsertBillItems", " IGST Tax %: 0.00");
                    objBillItem.setCGSTRate(Float.parseFloat(String.format("%.2f",Float.parseFloat(SalesTaxPercent.getText().toString()))));
                    Log.d("InsertBillItems", " CGST Tax %: " + SalesTaxPercent.getText().toString());
                }
            }
            // Sales Tax Amount
            if (RowBillItem.getChildAt(7) != null) {
                TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                double cgstAmt = (Double.parseDouble(SalesTaxAmount.getText().toString()));
                if (chk_interstate.isChecked()) {
                    //objBillItem.setIGSTAmount(Float.parseFloat(String.format("%.2f",cgstAmt+sgstAmt)));
                    //Log.d("InsertBillItems", "IGST Amt: " + objBillItem.getIGSTAmount());
                    objBillItem.setCGSTAmount(0.00f);
                    Log.d("InsertBillItems", "CGST Amt: 0");
                } else {
                    // objBillItem.setIGSTAmount(0.00f);
                    //Log.d("InsertBillItems", "IGST Amt: 0");
                    objBillItem.setCGSTAmount(Double.parseDouble(String.format("%.2f", cgstAmt)));
                    Log.d("InsertBillItems", "CGST Amt: " + SalesTaxAmount.getText().toString());
                }
            }

            // IGST Tax %
            if (RowBillItem.getChildAt(23) != null) {
                TextView IGSTTaxPercent = (TextView) RowBillItem.getChildAt(23);
                float igsttax = (Float.parseFloat(IGSTTaxPercent.getText().toString()));
                if (chk_interstate.isChecked()) {
                    objBillItem.setIGSTRate(Float.parseFloat(String.format("%.2f", igsttax)));
                    Log.d("InsertBillItems", " IGST Tax %: " + objBillItem.getIGSTRate());
                }else{
                    objBillItem.setIGSTRate(0.00f);
                    Log.d("InsertBillItems", " IGST Tax %: 0.00");
                }
            }
            // IGST Tax Amount
            if (RowBillItem.getChildAt(24) != null) {
                TextView IGSTTaxAmount = (TextView) RowBillItem.getChildAt(24);
                double igstAmt = (Double.parseDouble(IGSTTaxAmount.getText().toString()));
                if (chk_interstate.isChecked()) {
                    objBillItem.setIGSTAmount(Double.parseDouble(String.format("%.2f",igstAmt)));
                    Log.d("InsertBillItems", "IGST Amt: " + objBillItem.getIGSTAmount());
                } else {
                    objBillItem.setIGSTAmount(0.00f);
                    Log.d("InsertBillItems", "IGST Amt: 0");
                }
            }

            // cess Tax %
            if (RowBillItem.getChildAt(25) != null) {
                TextView cessTaxPercent = (TextView) RowBillItem.getChildAt(25);
                float cesstax = (Float.parseFloat(cessTaxPercent.getText().toString()));
                objBillItem.setCessRate(Float.parseFloat(String.format("%.2f", cesstax)));
                Log.d("InsertBillItems", " cess Tax %: " + objBillItem.getCessRate());
            }
            // cessTax Amount
            if (RowBillItem.getChildAt(26) != null) {
                TextView cessTaxAmount = (TextView) RowBillItem.getChildAt(26);
                double cessAmt = (Double.parseDouble(cessTaxAmount.getText().toString()));
                objBillItem.setCessAmount(Double.parseDouble(String.format("%.2f",cessAmt)));
                Log.d("InsertBillItems", "cess Amt: " + objBillItem.getCessAmount());
            }
            // Discount %
            if (RowBillItem.getChildAt(8) != null) {
                TextView DiscountPercent = (TextView) RowBillItem.getChildAt(8);
                objBillItem.setDiscountPercent(Float.parseFloat(DiscountPercent.getText().toString()));
                Log.d("InsertBillItems", "Disc %:" + DiscountPercent.getText().toString());
            }

            // Discount Amount
            if (RowBillItem.getChildAt(9) != null) {
                TextView DiscountAmount = (TextView) RowBillItem.getChildAt(9);
                objBillItem.setDiscountAmount(Float.parseFloat(DiscountAmount.getText().toString()));
                Log.d("InsertBillItems", "Disc Amt:" + DiscountAmount.getText().toString());

                // dblTotalDiscount += Float.parseFloat(DiscountAmount.getText().toString());
            }

            // Department Code
            if (RowBillItem.getChildAt(10) != null) {
                TextView DeptCode = (TextView) RowBillItem.getChildAt(10);
                objBillItem.setDeptCode(Integer.parseInt(DeptCode.getText().toString()));
                Log.d("InsertBillItems", "Dept Code:" + DeptCode.getText().toString());
            }

            // Category Code
            if (RowBillItem.getChildAt(11) != null) {
                TextView CategCode = (TextView) RowBillItem.getChildAt(11);
                objBillItem.setCategCode(Integer.parseInt(CategCode.getText().toString()));
                Log.d("InsertBillItems", "Categ Code:" + CategCode.getText().toString());
            }

            // Kitchen Code
            if (RowBillItem.getChildAt(12) != null) {
                TextView KitchenCode = (TextView) RowBillItem.getChildAt(12);
                objBillItem.setKitchenCode(Integer.parseInt(KitchenCode.getText().toString()));
                Log.d("InsertBillItems", "Kitchen Code:" + KitchenCode.getText().toString());
            }

            // Tax Type
            if (RowBillItem.getChildAt(13) != null) {
                TextView TaxType = (TextView) RowBillItem.getChildAt(13);
                objBillItem.setTaxType(Integer.parseInt(TaxType.getText().toString()));
                Log.d("InsertBillItems", "Tax Type:" + TaxType.getText().toString());
            }

            // Modifier Amount
            if (RowBillItem.getChildAt(14) != null) {
                TextView ModifierAmount = (TextView) RowBillItem.getChildAt(14);
                objBillItem.setModifierAmount(Float.parseFloat(ModifierAmount.getText().toString()));
                Log.d("InsertBillItems", "Modifier Amt:" + ModifierAmount.getText().toString());
            }

            // Service Tax Percent
            /*if (RowBillItem.getChildAt(15) != null) {
                TextView ServiceTaxPercent = (TextView) RowBillItem.getChildAt(15);
                if (chk_interstate.isChecked()) {
                    objBillItem.setSGSTRate(0);
                    Log.d("InsertBillItems", "SGST Tax %: 0");

                } else {
                    objBillItem.setSGSTRate(Float.parseFloat(ServiceTaxPercent.getText().toString()));
                    Log.d("InsertBillItems", "SGST Tax %: " + objBillItem.getSGSTRate());
                }
            }

            // Service Tax Amount
            if (RowBillItem.getChildAt(16) != null) {
                TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                if (chk_interstate.isChecked()) {
                    objBillItem.setSGSTAmount(0.00f);
                    Log.d("InsertBillItems", "SGST Amount : 0" );

                } else {
                    objBillItem.setSGSTAmount(Float.parseFloat(String.format("%.2f",
                            Float.parseFloat(ServiceTaxAmount.getText().toString()))));
                    Log.d("InsertBillItems", "SGST Amount : " + objBillItem.getSGSTAmount());
                }
            }*/

            if (RowBillItem.getChildAt(17) != null) {
                TextView SupplyType = (TextView) RowBillItem.getChildAt(17);
                objBillItem.setSupplyType(SupplyType.getText().toString());
                Log.d("InsertBillItems", "SupplyType:" + SupplyType.getText().toString());
                /*if (GSTEnable.equals("1")) {
                    objBillItem.setSupplyType(SupplyType.getText().toString());
                    Log.d("InsertBillItems", "SupplyType:" + SupplyType.getText().toString());
                } else {
                    objBillItem.setSupplyType("");
                }*/
            }
            if (RowBillItem.getChildAt(22) != null) {
                TextView UOM = (TextView) RowBillItem.getChildAt(22);
                objBillItem.setUom(UOM.getText().toString());
                Log.d("InsertBillItems", "UOM:" + UOM.getText().toString());

            }

            // subtotal
            double subtotal = objBillItem.getAmount() + objBillItem.getIGSTAmount() + objBillItem.getCGSTAmount() + objBillItem.getSGSTAmount();
            objBillItem.setSubTotal(subtotal);
            Log.d("InsertBillItems", "Sub Total :" + subtotal);

            // Date
            String date_today = tvDate.getText().toString();
            //Log.d("Date ", date_today);
            try {
                Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date_today);
                objBillItem.setInvoiceDate(String.valueOf(date1.getTime()));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            // cust name
            String custname = edtCustName.getText().toString();
            objBillItem.setCustName(custname);
            Log.d("InsertBillItems", "CustName :" + custname);

            String custgstin = etCustGSTIN.getText().toString().trim().toUpperCase();
            objBillItem.setGSTIN(custgstin);
            Log.d("InsertBillItems", "custgstin :" + custgstin);

            // cust StateCode
            //String custStateCode =spnr_pos.getSelectedItem().toString();
            if (chk_interstate.isChecked()) {
                String str = spnr_pos.getSelectedItem().toString();
                int length = str.length();
                String sub = "";
                if (length > 0) {
                    sub = str.substring(length - 2, length);
                }
                objBillItem.setCustStateCode(sub);
                Log.d("InsertBillItems", "CustStateCode :" + sub+" - "+str);
            } else {
                objBillItem.setCustStateCode(dbBillScreen.getOwnerPOS());// to be retrieved from database later -- richa to do
                Log.d("InsertBillItems", "CustStateCode :"+objBillItem.getCustStateCode());
            }

            // BusinessType
            if (etCustGSTIN.getText().toString().equals("")) {
                objBillItem.setBusinessType("B2C");
            } else // gstin present means b2b bussiness
            {
                objBillItem.setBusinessType("B2B");
            }

            Log.d("InsertBillItems", "BusinessType : " + objBillItem.getBusinessType());

            // richa to do - hardcoded b2b bussinies type
            //objBillItem.setBusinessType("B2B");
            lResult = dbBillScreen.addBillItem(objBillItem);
            Log.d("InsertBillItem", "Bill item inserted at position:" + lResult);
        }
    }

    /*************************************************************************************************************************************
     * Insert each bill item to preview bill items database table
     *************************************************************************************************************************************/
    private void InsertPreviewBillItems() {

        // Inserted Row Id in database table
        long lResult = 0;

        // Bill item object

        ArrayList<BillItem> list = new ArrayList<>();

        // Reset TotalItems count
        iTotalItems = 0;

        Cursor crsrUpdateItemStock = null;

        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++)
        {
            BillItem objBillItem = new BillItem();

            TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(iRow);



            // Increment Total item count if row is not empty
            if (RowBillItem.getChildCount() > 0) {
                iTotalItems++;
            }



            // Bill Number
            objBillItem.setBillNumber(tvBillNumber.getText().toString());
            Log.d("InsertBillItems_Preview", "InvoiceNo:" + tvBillNumber.getText().toString());

            objBillItem.setBillStatus(1);
            Log.d("InsertBillItems_Preview", "Bill Status:1");

            // richa_2012
            //BillingMode
            objBillItem.setBillingMode(String.valueOf(jBillingMode));
            Log.d("InsertBillItems_Preview", "Billing Mode :" + String.valueOf(jBillingMode));

            // Item Number
            if (RowBillItem.getChildAt(0) != null) {
                CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                objBillItem.setItemNumber(Integer.parseInt(ItemNumber.getText().toString()));
                Log.d("InsertBillItems_Preview", "Item Number:" + ItemNumber.getText().toString());

                crsrUpdateItemStock = dbBillScreen.getItem(Integer.parseInt(ItemNumber.getText().toString()));
            }

            // Item Name
            if (RowBillItem.getChildAt(1) != null) {
                TextView ItemName = (TextView) RowBillItem.getChildAt(1);
                objBillItem.setItemName(ItemName.getText().toString());
                Log.d("InsertBillItems_Preview", "Item Name:" + ItemName.getText().toString());
            }

            if (RowBillItem.getChildAt(2) != null) {
                TextView HSN = (TextView) RowBillItem.getChildAt(2);
                objBillItem.setHSNCode(HSN.getText().toString());
                Log.d("InsertBillItems_Preview", "Item HSN:" + HSN.getText().toString());
            }

            // Quantity
            double qty_d = 0.00;
            if (RowBillItem.getChildAt(3) != null) {
                EditText Quantity = (EditText) RowBillItem.getChildAt(3);
                String qty_str = Quantity.getText().toString();
                if(qty_str==null || qty_str.equals(""))
                {
                    Quantity.setText("0.00");
                }else
                {
                    qty_d = Double.parseDouble(qty_str);
                }

                objBillItem.setQuantity(Float.parseFloat(String.format("%.2f",qty_d)));
                Log.d("InsertBillItems_Preview", "Quantity:" + Quantity.getText().toString());

                if (crsrUpdateItemStock!=null && crsrUpdateItemStock.moveToFirst()) {
                    // Check if item's bill with stock enabled update the stock
                    // quantity
                    Cursor billsettingCursor = dbBillScreen.getBillSetting();
                    if(billsettingCursor!= null && billsettingCursor.moveToFirst())
                    {
                        //String i = billsettingCursor.getString(billsettingCursor.getColumnIndex("BillwithStock"));
                        if (billsettingCursor.getInt(billsettingCursor.getColumnIndex("BillwithStock")) == 1) {
                            UpdateItemStock(crsrUpdateItemStock, Float.parseFloat(Quantity.getText().toString()));
                        }
                    }

                }
            }


            // Rate
            double rate_d = 0.00;
            if (RowBillItem.getChildAt(4) != null) {
                EditText Rate = (EditText) RowBillItem.getChildAt(4);
                String rate_str = Rate.getText().toString();
                if((rate_str==null || rate_str.equals("")))
                {
                    Rate.setText("0.00");
                }else
                {
                    rate_d = Double.parseDouble(rate_str);
                }

                objBillItem.setValue(Float.parseFloat(String.format("%.2f",rate_d)));
                Log.d("InsertBillItems_Preview", "Rate:" + Rate.getText().toString());
            }
            // oRIGINAL rate in case of reverse tax
            if (RowBillItem.getChildAt(27) != null) {
                TextView originalRate = (TextView) RowBillItem.getChildAt(27);
                objBillItem.setOriginalRate(Double.parseDouble(originalRate.getText().toString()));
                Log.d("InsertBillItems_Preview", "Original Rate  :" + objBillItem.getOriginalRate());
            }
            if (RowBillItem.getChildAt(28) != null) {
                TextView TaxableValue = (TextView) RowBillItem.getChildAt(28);
                objBillItem.setTaxableValue(Double.parseDouble(TaxableValue.getText().toString()));
                Log.d("InsertBillItems_Preview", "TaxableValue  :" + objBillItem.getTaxableValue());
            }

            // Amount
            if (RowBillItem.getChildAt(5) != null) {
                TextView Amount = (TextView) RowBillItem.getChildAt(5);
                objBillItem.setAmount(Double.parseDouble(Amount.getText().toString()));
                String reverseTax = "";
                if (!(crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1)) { // forward tax
                    reverseTax = " (Reverse Tax)";
                    objBillItem.setIsReverTaxEnabled("YES");
                }else
                {
                    objBillItem.setIsReverTaxEnabled("NO");
                }
                Log.d("InsertBillItems_Preview", "Amount :" + objBillItem.getAmount()+reverseTax);
            }

            // Service Tax Percent
            float sgatTax = 0;
            if (RowBillItem.getChildAt(15) != null) {
                TextView ServiceTaxPercent = (TextView) RowBillItem.getChildAt(15);
                sgatTax = Float.parseFloat(ServiceTaxPercent.getText().toString());
                if (chk_interstate.isChecked()) {
                    objBillItem.setSGSTRate(0.00f);
                    Log.d("InsertBillItems_Preview", "SGST Tax %: 0");

                } else {
                    objBillItem.setSGSTRate(Float.parseFloat(ServiceTaxPercent.getText().toString()));
                    Log.d("InsertBillItems_Preview", "SGST Tax %: " + objBillItem.getSGSTRate());
                }
            }

            // Service Tax Amount
            double sgstAmt = 0;
            if (RowBillItem.getChildAt(16) != null) {
                TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                sgstAmt = Double.parseDouble(ServiceTaxAmount.getText().toString());
                if (chk_interstate.isChecked()) {
                    objBillItem.setSGSTAmount(0.00f);
                    Log.d("InsertBillItems_Preview", "SGST Amount : 0" );

                } else {
                    objBillItem.setSGSTAmount(Double.parseDouble(String.format("%.2f", sgstAmt)));
                    Log.d("InsertBillItems_Preview", "SGST Amount : " + objBillItem.getSGSTAmount());
                }
            }

            // Sales Tax %
            if (RowBillItem.getChildAt(6) != null) {
                TextView SalesTaxPercent = (TextView) RowBillItem.getChildAt(6);
                float cgsttax = (Float.parseFloat(SalesTaxPercent.getText().toString()));
                if (chk_interstate.isChecked()) {
                    //objBillItem.setIGSTRate(Float.parseFloat(String.format("%.2f", cgsttax + sgatTax)));
                    //Log.d("InsertBillItems", " IGST Tax %: " + objBillItem.getIGSTRate());
                    objBillItem.setCGSTRate(0.00f);
                    Log.d("InsertBillItems_Preview", " CGST Tax %: 0.00");
                }else{
                    // objBillItem.setIGSTRate(0.00f);
                    // Log.d("InsertBillItems_Preview", " IGST Tax %: 0.00");
                    objBillItem.setCGSTRate(Float.parseFloat(String.format("%.2f",Float.parseFloat(SalesTaxPercent.getText().toString()))));
                    Log.d("InsertBillItems_Preview", " CGST Tax %: " + SalesTaxPercent.getText().toString());
                }
            }
            // Sales Tax Amount
            if (RowBillItem.getChildAt(7) != null) {
                TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                double cgstAmt = (Double.parseDouble(SalesTaxAmount.getText().toString()));
                if (chk_interstate.isChecked()) {
                    //objBillItem.setIGSTAmount(Float.parseFloat(String.format("%.2f",cgstAmt+sgstAmt)));
                    //Log.d("InsertBillItems_Preview", "IGST Amt: " + objBillItem.getIGSTAmount());
                    objBillItem.setCGSTAmount(0.00f);
                    Log.d("InsertBillItems_Preview", "CGST Amt: 0");
                } else {
                    // objBillItem.setIGSTAmount(0.00f);
                    //Log.d("InsertBillItems_Preview", "IGST Amt: 0");
                    objBillItem.setCGSTAmount(Double.parseDouble(String.format("%.2f", cgstAmt)));
                    Log.d("InsertBillItems_Preview", "CGST Amt: " + SalesTaxAmount.getText().toString());
                }
            }

            // IGST Tax %
            if (RowBillItem.getChildAt(23) != null) {
                TextView IGSTTaxPercent = (TextView) RowBillItem.getChildAt(23);
                float igsttax = (Float.parseFloat(IGSTTaxPercent.getText().toString()));
                if (chk_interstate.isChecked()) {
                    objBillItem.setIGSTRate(Float.parseFloat(String.format("%.2f", igsttax)));
                    Log.d("InsertBillItems_Preview", " IGST Tax %: " + objBillItem.getIGSTRate());
                }else{
                    objBillItem.setIGSTRate(0.00f);
                    Log.d("InsertBillItems_Preview", " IGST Tax %: 0.00");
                }
            }
            // IGST Tax Amount
            if (RowBillItem.getChildAt(24) != null) {
                TextView IGSTTaxAmount = (TextView) RowBillItem.getChildAt(24);
                double igstAmt = (Double.parseDouble(IGSTTaxAmount.getText().toString()));
                if (chk_interstate.isChecked()) {
                    objBillItem.setIGSTAmount(Double.parseDouble(String.format("%.2f",igstAmt)));
                    Log.d("InsertBillItems_Preview", "IGST Amt: " + objBillItem.getIGSTAmount());
                } else {
                    objBillItem.setIGSTAmount(0.00f);
                    Log.d("InsertBillItems_Preview", "IGST Amt: 0");
                }
            }

            // cess Tax %
            if (RowBillItem.getChildAt(25) != null) {
                TextView cessTaxPercent = (TextView) RowBillItem.getChildAt(25);
                float cesstax = (Float.parseFloat(cessTaxPercent.getText().toString()));
                objBillItem.setCessRate(Float.parseFloat(String.format("%.2f", cesstax)));
                Log.d("InsertBillItems_Preview", " cess Tax %: " + objBillItem.getCessRate());
            }
            // cessTax Amount
            if (RowBillItem.getChildAt(26) != null) {
                TextView cessTaxAmount = (TextView) RowBillItem.getChildAt(26);
                double cessAmt = (Double.parseDouble(cessTaxAmount.getText().toString()));
                objBillItem.setCessAmount(Double.parseDouble(String.format("%.2f",cessAmt)));
                Log.d("InsertBillItems_Preview", "cess Amt: " + objBillItem.getCessAmount());
            }
            // Discount %
            if (RowBillItem.getChildAt(8) != null) {
                TextView DiscountPercent = (TextView) RowBillItem.getChildAt(8);
                objBillItem.setDiscountPercent(Float.parseFloat(DiscountPercent.getText().toString()));
                Log.d("InsertBillItems_Preview", "Disc %:" + DiscountPercent.getText().toString());
            }

            // Discount Amount
            if (RowBillItem.getChildAt(9) != null) {
                TextView DiscountAmount = (TextView) RowBillItem.getChildAt(9);
                objBillItem.setDiscountAmount(Float.parseFloat(DiscountAmount.getText().toString()));
                Log.d("InsertBillItems_Preview", "Disc Amt:" + DiscountAmount.getText().toString());

                // dblTotalDiscount += Float.parseFloat(DiscountAmount.getText().toString());
            }

            // Department Code
            if (RowBillItem.getChildAt(10) != null) {
                TextView DeptCode = (TextView) RowBillItem.getChildAt(10);
                objBillItem.setDeptCode(Integer.parseInt(DeptCode.getText().toString()));
                Log.d("InsertBillItems_Preview", "Dept Code:" + DeptCode.getText().toString());
            }

            // Category Code
            if (RowBillItem.getChildAt(11) != null) {
                TextView CategCode = (TextView) RowBillItem.getChildAt(11);
                objBillItem.setCategCode(Integer.parseInt(CategCode.getText().toString()));
                Log.d("InsertBillItems_Preview", "Categ Code:" + CategCode.getText().toString());
            }

            // Kitchen Code
            if (RowBillItem.getChildAt(12) != null) {
                TextView KitchenCode = (TextView) RowBillItem.getChildAt(12);
                objBillItem.setKitchenCode(Integer.parseInt(KitchenCode.getText().toString()));
                Log.d("InsertBillItems_Preview", "Kitchen Code:" + KitchenCode.getText().toString());
            }

            // Tax Type
            if (RowBillItem.getChildAt(13) != null) {
                TextView TaxType = (TextView) RowBillItem.getChildAt(13);
                objBillItem.setTaxType(Integer.parseInt(TaxType.getText().toString()));
                Log.d("InsertBillItems_Preview", "Tax Type:" + TaxType.getText().toString());
            }

            // Modifier Amount
            if (RowBillItem.getChildAt(14) != null) {
                TextView ModifierAmount = (TextView) RowBillItem.getChildAt(14);
                objBillItem.setModifierAmount(Float.parseFloat(ModifierAmount.getText().toString()));
                Log.d("InsertBillItems_Preview", "Modifier Amt:" + ModifierAmount.getText().toString());
            }

            if (RowBillItem.getChildAt(17) != null) {
                TextView SupplyType = (TextView) RowBillItem.getChildAt(17);
                objBillItem.setSupplyType(SupplyType.getText().toString());
                Log.d("InsertBillItems_Preview", "SupplyType:" + SupplyType.getText().toString());
                /*if (GSTEnable.equals("1")) {
                    objBillItem.setSupplyType(SupplyType.getText().toString());
                    Log.d("InsertBillItems_Preview", "SupplyType:" + SupplyType.getText().toString());
                } else {
                    objBillItem.setSupplyType("");
                }*/
            }
            if (RowBillItem.getChildAt(22) != null) {
                TextView UOM = (TextView) RowBillItem.getChildAt(22);
                objBillItem.setUom(UOM.getText().toString());
                Log.d("InsertBillItems_Preview", "UOM:" + UOM.getText().toString());

            }

            // subtotal
            double subtotal = objBillItem.getAmount() + objBillItem.getIGSTAmount() + objBillItem.getCGSTAmount() + objBillItem.getSGSTAmount();

            objBillItem.setSubTotal(subtotal);
            Log.d("InsertBillItems_Preview", "Sub Total :" + subtotal);

            // Date
            String date_today = tvDate.getText().toString();
            //Log.d("Date ", date_today);
            try {
                Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date_today);
                objBillItem.setInvoiceDate(String.valueOf(date1.getTime()));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            // cust name
            String custname = edtCustName.getText().toString();
            objBillItem.setCustName(custname);
            Log.d("InsertBillItems_Preview", "CustName :" + custname);

            String custgstin = etCustGSTIN.getText().toString().trim().toUpperCase();
            objBillItem.setGSTIN(custgstin);
            Log.d("InsertBillItems_Preview", "custgstin :" + custgstin);

            // cust StateCode
            //String custStateCode =spnr_pos.getSelectedItem().toString();
            if (chk_interstate.isChecked()) {
                String str = spnr_pos.getSelectedItem().toString();
                int length = str.length();
                String sub = "";
                if (length > 0) {
                    sub = str.substring(length - 2, length);
                }
                objBillItem.setCustStateCode(sub);
                Log.d("InsertBillItems_Preview", "CustStateCode :" + sub+" - "+str);
            } else {
                objBillItem.setCustStateCode(dbBillScreen.getOwnerPOS());// to be retrieved from database later -- richa to do
                Log.d("InsertBillItems_Preview", "CustStateCode :"+objBillItem.getCustStateCode());
            }

            // BusinessType
            if (etCustGSTIN.getText().toString().equals("")) {
                objBillItem.setBusinessType("B2C");
            } else // gstin present means b2b bussiness
            {
                objBillItem.setBusinessType("B2B");
            }

            Log.d("InsertBillItems_Preview", "BusinessType : " + objBillItem.getBusinessType());

            // richa to do - hardcoded b2b bussinies type
            //objBillItem.setBusinessType("B2B");

            int found =0;
            for(BillItem objBillItemInList : list )
            {
                if(objBillItemInList.getItemNumber() == objBillItem.getItemNumber())
                {
                    objBillItemInList.setQuantity(objBillItem.getQuantity() + objBillItemInList.getQuantity());
                    objBillItemInList.setDiscountAmount(objBillItem.getDiscountAmount() + objBillItemInList.getDiscountAmount());
                    objBillItemInList.setCGSTAmount(objBillItem.getCGSTAmount() + objBillItemInList.getCGSTAmount());
                    objBillItemInList.setSGSTAmount(objBillItem.getSGSTAmount() + objBillItemInList.getSGSTAmount());
                    objBillItemInList.setIGSTAmount(objBillItem.getIGSTAmount() + objBillItemInList.getIGSTAmount());
                    objBillItemInList.setCessAmount(objBillItem.getCessAmount() + objBillItemInList.getCessAmount());
                    objBillItemInList.setTaxableValue(objBillItem.getTaxableValue() + objBillItemInList.getTaxableValue());
                    found =1;
                    break;
                }
            }
            if(0 == found)
            {
                list.add(objBillItem);
            }

        }
        for(BillItem objBillItem : list )
        {
            lResult = dbBillScreen.addPreviewBillItem(objBillItem);
            Log.d("InsertBillItem", "Bill item inserted at position:" + lResult);
        }
    }

    /*************************************************************************************************************************************
     * Inserts bill details to bill detail database table
     *
     * @param TenderType : Type of tender, 1 - Pay cash, 2 - PayBill Screen payment
     *************************************************************************************************************************************/
    private void InsertBillDetail(int TenderType) {

        // Inserted Row Id in database table
        long lResult = 0;

        // BillDetail object
        BillDetail objBillDetail;

        objBillDetail = new BillDetail();

        // Date
        //objBillDetail.setDate(String.valueOf(d.getTime()));
        try {
            String date_today = tvDate.getText().toString();
            //Log.d("Date ", date_today);
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date_today);
            objBillDetail.setDate(String.valueOf(date1.getTime()));
            Log.d("InsertBillDetail", "Date:" + objBillDetail.getDate());
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        // Time
        //objBillDetail.setTime(String.format("%tR", Time));
        String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());
        objBillDetail.setTime(strTime);
        Log.d("InsertBillDetail", "Time:" + String.format("%tR", Time));

        // Bill Number
        objBillDetail.setBillNumber(Integer.parseInt(tvBillNumber.getText().toString()));
        Log.d("InsertBillDetail", "Bill Number:" + tvBillNumber.getText().toString());

        // richa_2012
        //BillingMode
        objBillDetail.setBillingMode(String.valueOf(jBillingMode));
        Log.d("InsertBillDetail", "Billing Mode :" + String.valueOf(jBillingMode));

        objBillDetail.setTableNo(tvTableNumber.getText().toString());
        Log.d("InsertBillDetail", "Table No "+objBillDetail.getTableNo());
        if (tableSplit ==1)
        {
            objBillDetail.setTableSplitNo(tvTableSplitNo.getText().toString());
            Log.d("InsertBillDetail", "Table Split No: "+objBillDetail.getTableSplitNo());
        }

        objBillDetail.setPOS(dbBillScreen.getOwnerPOS());// to be retrieved from database later -- richa to do
        Log.d("InsertBillDetail", "POS : "+objBillDetail.getPOS());

        // Total Items
        objBillDetail.setTotalItems(iTotalItems);
        Log.d("InsertBillDetail", "Total Items:" + iTotalItems);

        // Bill Amount
        String billamt_temp = String.format("%.2f",Double.parseDouble(tvBillAmount.getText().toString()));
//        objBillDetail.setBillAmount(Double.parseDouble(billamt_temp));
        objBillDetail.setdBillAmount(Double.parseDouble(billamt_temp));
        Log.d("InsertBillDetail", "Bill Amount:" + tvBillAmount.getText().toString());

        // Discount Percentage
        objBillDetail.setTotalDiscountPercentage(Float.parseFloat(tvDiscountPercentage.getText().toString()));
        Log.d("InsertBillDetail", "Discount Percentage:" + objBillDetail.getTotalDiscountPercentage());

        // Discount Amount
        // if(ItemwiseDiscountEnabled ==1)
        calculateDiscountAmount();
        float discount = Float.parseFloat(tvDiscountAmount.getText().toString());
        //objBillDetail.setTotalDiscountAmount(discount);
        objBillDetail.setTotalDiscountAmount(fTotalDiscount);
        Log.d("InsertBillDetail", "Total Discount:" + discount);

        // Sales Tax Amount
        if (chk_interstate.isChecked()) {
            objBillDetail.setIGSTAmount(Double.parseDouble(String.format("%.2f",Double.parseDouble(tvIGSTValue.getText().toString()))));
            objBillDetail.setCGSTAmount(0.00f);
            objBillDetail.setSGSTAmount(0.00f);
        } else {
            objBillDetail.setIGSTAmount(0.00f);
            objBillDetail.setCGSTAmount(Double.parseDouble(String.format("%.2f",Double.parseDouble(tvCGSTValue.getText().toString()))));
            objBillDetail.setSGSTAmount(Double.parseDouble(String.format("%.2f",Double.parseDouble(tvSGSTValue.getText().toString()))));
        }
        Log.d("InsertBillDetail", "IGSTAmount : " + objBillDetail.getIGSTAmount());
        Log.d("InsertBillDetail", "CGSTAmount : " + objBillDetail.getCGSTAmount());
        Log.d("InsertBillDetail", "SGSTAmount : " + objBillDetail.getSGSTAmount());


        objBillDetail.setCessAmount(Double.parseDouble(String.format("%.2f",Double.parseDouble(tvcessValue.getText().toString()))));
        Log.d("InsertBillDetail", "cessAmount : " + objBillDetail.getCessAmount());
        // Delivery Charge
        objBillDetail.setDeliveryCharge(Float.parseFloat(tvOthercharges.getText().toString()));
        Log.d("InsertBillDetail", "Delivery Charge: "+objBillDetail.getDeliveryCharge());


        // Taxable Value
        double taxval_f = Double.parseDouble(tvSubTotal.getText().toString());
        objBillDetail.setAmount(String.valueOf(taxval_f));
        Log.d("InsertBillDetail", "Taxable Value:" + taxval_f);

        double subtot_f = taxval_f + objBillDetail.getIGSTAmount() + objBillDetail.getCGSTAmount()+ objBillDetail.getSGSTAmount();
        objBillDetail.setSubTotal(subtot_f);
        Log.d("InsertBillDetail", "Sub Total :" + subtot_f);

        // cust name
        String custname = edtCustName.getText().toString();
        objBillDetail.setCustname(custname);
        Log.d("InsertBillDetail", "CustName :" + custname);

        String custgstin = etCustGSTIN.getText().toString().trim().toUpperCase();
        objBillDetail.setCustname(custgstin);
        Log.d("InsertBillDetail", "custgstin :" + custgstin);

        // cust StateCode
        if (chk_interstate.isChecked()) {
            String str = spnr_pos.getSelectedItem().toString();
            int length = str.length();
            String sub = "";
            if (length > 0) {
                sub = str.substring(length - 2, length);
            }
            objBillDetail.setCustStateCode(sub);
            Log.d("InsertBillDetail", "CustStateCode :" + sub+" - "+str);
        } else {
            String userPOS = dbBillScreen.getOwnerPOS();
            objBillDetail.setCustStateCode(userPOS);
            Log.d("InsertBillDetail", "CustStateCode : "+objBillDetail.getCustStateCode());
        }

        // BusinessType
        if (etCustGSTIN.getText().toString().equals("")) {
            objBillDetail.setBusinessType("B2C");
        } else // gstin present means b2b bussiness
        {
            objBillDetail.setBusinessType("B2B");
        }
        //objBillDetail.setBusinessType("B2C");
        Log.d("InsertBillDetail", "BusinessType : " + objBillDetail.getBusinessType());
        // Payment types
        if (TenderType == 1) {
            // Cash Payment
            objBillDetail.setCashPayment(Double.parseDouble(String.format("%.2f", Double.parseDouble(tvBillAmount.getText().toString()))));
            Log.d("InsertBillDetail", "Cash:" + tvBillAmount.getText().toString());

            // Card Payment
            objBillDetail.setCardPayment(Double.parseDouble(String.format("%.2f", dblCardPayment)));
            Log.d("InsertBillDetail", "Card:" + dblCardPayment);

            // Coupon Payment
            objBillDetail.setCouponPayment(Double.parseDouble(String.format("%.2f", dblCouponPayment)));
            Log.d("InsertBillDetail", "Coupon:" + dblCouponPayment);

            // PettyCash Payment
//            objBillDetail.setPettyCashPayment(dblPettCashPayment);
//            Log.d("InsertBillDetail", "PettyCash:" + dblPettCashPayment);

            objBillDetail.setdPettyCashPayment(Double.parseDouble(String.format("%.2f", dblPettyCashPayment)));
            Log.d("InsertBillDetail", "PettyCash:" + dblPettyCashPayment);

            // Wallet Payment
            objBillDetail.setWalletAmount(Double.parseDouble(String.format("%.2f", dblWalletPayment)));
            Log.d("InsertBillDetail", "Wallet:" + dblWalletPayment);

            // PaidTotal Payment
            objBillDetail.setPaidTotalPayment(Double.parseDouble(String.format("%.2f", dblPaidTotalPayment)));

            // Change Payment
            objBillDetail.setChangePayment(Double.parseDouble(String.format("%.2f", dblChangePayment)));

            objBillDetail.setDblRewardPoints(dblRewardPointsAmount);
            Log.d("InsertBillDetail", "RewardPoints :" + dblRewardPointsAmount);

            objBillDetail.setDblMSwipeAmount(dblMSwipeAmount);
            Log.d("InsertBillDetail", "MSwipe Amount :" + dblMSwipeAmount);

            objBillDetail.setDblPaytmAmount(dblPaytmAmount);
            Log.d("InsertBillDetail", "Paytm Amount :" + dblPaytmAmount);

            objBillDetail.setDblAEPSAmount(dblAEPSAmount);
            Log.d("InsertBillDetail", "AEPSAmount :" + dblAEPSAmount);

        } else if (TenderType == 2) {

            if (PrintBillPayment == 1) {
                // Cash Payment
                objBillDetail.setCashPayment(Double.parseDouble(String.format("%.2f", Double.parseDouble(tvBillAmount.getText().toString()))));
                Log.d("InsertBillDetail", "Cash:" + Float.parseFloat(tvBillAmount.getText().toString()));

                // Card Payment
                objBillDetail.setCardPayment(Double.parseDouble(String.format("%.2f", dblCardPayment)));
                Log.d("InsertBillDetail", "Card:" + dblCardPayment);

                // Coupon Payment
                objBillDetail.setCouponPayment(Double.parseDouble(String.format("%.2f", dblCouponPayment)));
                Log.d("InsertBillDetail", "Coupon:" + dblCouponPayment);

                // PettyCash Payment
//                objBillDetail.setPettyCashPayment(dblPettCashPayment);
//                Log.d("InsertBillDetail", "PettyCash:" + dblPettCashPayment);

                objBillDetail.setdPettyCashPayment(Double.parseDouble(String.format("%.2f", dblPettyCashPayment)));
                Log.d("InsertBillDetail", "PettyCash:" + dblPettyCashPayment);

                // Wallet Payment
                objBillDetail.setWalletAmount(Double.parseDouble(String.format("%.2f", dblWalletPayment)));
                Log.d("InsertBillDetail", "Wallet:" + dblWalletPayment);

                // PaidTotal Payment
                objBillDetail.setPaidTotalPayment(Double.parseDouble(String.format("%.2f", dblPaidTotalPayment)));
                Log.d("InsertBillDetail", "PaidTotalPayment:" + dblPaidTotalPayment);

                // Change Payment
                objBillDetail.setChangePayment(Double.parseDouble(String.format("%.2f", dblChangePayment)));
                Log.d("InsertBillDetail", "ChangePayment:" + dblChangePayment);


                objBillDetail.setfRoundOff(Double.parseDouble(String.format("%.2f", dblRoundOfValue)));
                Log.d("InsertBillDetail", "RoundOfValue:" + dblRoundOfValue);

                objBillDetail.setDblRewardPoints(dblRewardPointsAmount);
                Log.d("InsertBillDetail", "RewardPoints :" + dblRewardPointsAmount);

                objBillDetail.setDblMSwipeAmount(dblMSwipeAmount);
                Log.d("InsertBillDetail", "MSwipe Amount :" + dblMSwipeAmount);

                objBillDetail.setDblPaytmAmount(dblPaytmAmount);
                Log.d("InsertBillDetail", "Paytm Amount :" + dblPaytmAmount);

                objBillDetail.setDblAEPSAmount(dblAEPSAmount);
                Log.d("InsertBillDetail", "AEPSAmount :" + dblAEPSAmount);
            } else {
                // Cash Payment
                objBillDetail.setCashPayment(Double.parseDouble(String.format("%.2f", dblCashPayment)));
                Log.d("InsertBillDetail", "Cash:" + dblCashPayment);

                // Card Payment
                objBillDetail.setCardPayment(Double.parseDouble(String.format("%.2f", dblCardPayment)));
                Log.d("InsertBillDetail", "Card:" + dblCardPayment);

                // Coupon Payment
                objBillDetail.setCouponPayment(Double.parseDouble(String.format("%.2f", dblCouponPayment)));
                Log.d("InsertBillDetail", "Coupon:" + dblCouponPayment);

                // PettyCash Payment
//                objBillDetail.setPettyCashPayment(dblPettCashPayment);
//                Log.d("InsertBillDetail", "PettyCash:" + dblPettCashPayment);

                objBillDetail.setdPettyCashPayment(Double.parseDouble(String.format("%.2f", dblPettyCashPayment)));
                Log.d("InsertBillDetail", "PettyCash:" + dblPettyCashPayment);

                // Wallet Payment
                objBillDetail.setWalletAmount(Double.parseDouble(String.format("%.2f", dblWalletPayment)));
                Log.d("InsertBillDetail", "Wallet:" + dblWalletPayment);

                // PaidTotal Payment
                objBillDetail.setPaidTotalPayment(Double.parseDouble(String.format("%.2f", dblPaidTotalPayment)));
                Log.d("InsertBillDetail", "PaidTotalPayment:" + dblPaidTotalPayment);

                // Change Payment
                objBillDetail.setChangePayment(Double.parseDouble(String.format("%.2f", dblChangePayment)));
                Log.d("InsertBillDetail", "ChangePayment:" + dblChangePayment);

                objBillDetail.setfRoundOff(Double.parseDouble(String.format("%.2f", dblRoundOfValue)));
                Log.d("InsertBillDetail", "RoundOfValue:" + dblRoundOfValue);

                objBillDetail.setDblRewardPoints(dblRewardPointsAmount);
                Log.d("InsertBillDetail", "RewardPoints :" + dblRewardPointsAmount);

                objBillDetail.setDblMSwipeAmount(dblMSwipeAmount);
                Log.d("InsertBillDetail", "MSwipe Amount :" + dblMSwipeAmount);

                objBillDetail.setDblPaytmAmount(dblPaytmAmount);
                Log.d("InsertBillDetail", "Paytm Amount :" + dblPaytmAmount);

                objBillDetail.setDblAEPSAmount(dblAEPSAmount);
                Log.d("InsertBillDetail", "AEPSAmount :" + dblAEPSAmount);
            }
        }

        // Reprint Count
        objBillDetail.setReprintCount(0);
        Log.d("InsertBillDetail", "Reprint Count:0");

        // Bill Status

        objBillDetail.setBillStatus(1);
        Log.d("InsertBillDetail", "Bill Status:1");


        // Employee Id (Waiter / Rider)

        objBillDetail.setEmployeeId(Integer.parseInt(tvWaiterNumber.getText().toString()));
        Log.d("InsertBillDetail", "EmployeeId:" + tvWaiterNumber.getText().toString());


        // Customer Id
        objBillDetail.setCustId(Integer.valueOf(edtCustId.getText().toString()));
        Log.d("InsertBillDetail", "Customer Id:" + Integer.valueOf(edtCustId.getText().toString()));

        // User Id
        objBillDetail.setUserId(strUserId);
        Log.d("InsertBillDetail", "UserID:" + strUserId);

        lResult = dbBillScreen.addBill(objBillDetail, etCustGSTIN.getText().toString().trim().toUpperCase());
        Log.d("InsertBill", "Bill inserted at position:" + lResult);
        //lResult = dbBillScreen.updateBill(objBillDetail);

        if (edtCustId.getText().toString().equalsIgnoreCase("") || edtCustId.getText().toString().equalsIgnoreCase("0"))
        {
        }
        else if (dPettCashPayment >0)
        {
            iCustId = Integer.parseInt(edtCustId.getText().toString());
            double fTotalTransaction = dbBillScreen.getCustomerTotalTransaction(iCustId);
            double fCreditAmount = dbBillScreen.getCustomerCreditAmount(iCustId);
            //fCreditAmount = fCreditAmount - Float.parseFloat(tvBillAmount.getText().toString());
            fCreditAmount = fCreditAmount - dPettCashPayment;
            fTotalTransaction += Double.parseDouble(tvBillAmount.getText().toString());

           // long lResult1 = dbBillScreen.updateCustomerTransaction(iCustId, Double.parseDouble(tvBillAmount.getText().toString()), fTotalTransaction, fCreditAmount);
            long lResult1 = dbBillScreen.updateCustomerTransaction(iCustId, dPettCashPayment, fTotalTransaction, fCreditAmount);
        }
        // Bill No Reset Configuration
//        Log.d("Richa : ", tvBillNumber.getText().toString());
//        Log.d("Richa : ", String.valueOf(dbBillScreen.getNewBillNumber()));
        long Result2 = db.UpdateBillNoResetInvoiceNos(Integer.parseInt(tvBillNumber.getText().toString()));
//        System.out.println(Result2);
//        Log.d("Richa :- ", String.valueOf(dbBillScreen.getNewBillNumber()));
    }

    void calculateDiscountAmount()
    {
        fTotalDiscount =0;
        for(int i=0;i<tblOrderItems.getChildCount();i++)
        {
            TableRow row = (TableRow)tblOrderItems.getChildAt(i);
            TextView discountAmt = (TextView) row.getChildAt(9);
            if(discountAmt.getText().toString()!= null && !discountAmt.getText().toString().equals("") )
                fTotalDiscount += Double.parseDouble(discountAmt.getText().toString());
        }
    }

    void updateQuantityKOT() {

        long lResult = 0;

        ArrayList<PendingKOT> arrayList = new ArrayList();

        lResult = dbBillScreen.deleteKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));

        String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());

        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

            PendingKOT objPendingKOT = new PendingKOT();

            TableRow RowKOTItem = (TableRow) tblOrderItems.getChildAt(iRow);
            CheckBox ItemNumber = (CheckBox) RowKOTItem.getChildAt(0);
            TextView ItemName = (TextView) RowKOTItem.getChildAt(1);
            TextView hsn = (TextView) RowKOTItem.getChildAt(2);
            EditText Quantity = (EditText) RowKOTItem.getChildAt(3);
            EditText Rate = (EditText) RowKOTItem.getChildAt(4);
            TextView Amount = (TextView) RowKOTItem.getChildAt(5);
            TextView SalesTaxPercent = (TextView) RowKOTItem.getChildAt(6);
            TextView SalesTaxAmount = (TextView) RowKOTItem.getChildAt(7);
            TextView DiscountPercent = (TextView) RowKOTItem.getChildAt(8);
            TextView DiscountAmount = (TextView) RowKOTItem.getChildAt(9);
            TextView DeptCode = (TextView) RowKOTItem.getChildAt(10);
            TextView CategCode = (TextView) RowKOTItem.getChildAt(11);
            TextView KitchenCode = (TextView) RowKOTItem.getChildAt(12);
            TextView TaxType = (TextView) RowKOTItem.getChildAt(13);
            TextView ModifierAmount = (TextView) RowKOTItem.getChildAt(14);
            TextView ServiceTaxPercent = (TextView) RowKOTItem.getChildAt(15);
            TextView ServiceTaxAmount = (TextView) RowKOTItem.getChildAt(16);
            TextView SupplyType = (TextView) RowKOTItem.getChildAt(17);
            TextView printstatus = (TextView) RowKOTItem.getChildAt(21);
            TextView UOM = (TextView) RowKOTItem.getChildAt(22);
            TextView IGSTRate = (TextView) RowKOTItem.getChildAt(23);
            TextView IGSTAmt = (TextView) RowKOTItem.getChildAt(24);
            TextView cessRate = (TextView) RowKOTItem.getChildAt(25);
            TextView cessAmt = (TextView) RowKOTItem.getChildAt(26);
            TextView OriginalRate = (TextView) RowKOTItem.getChildAt(27);
            TextView TaxableValue = (TextView) RowKOTItem.getChildAt(28);
            TextView KOTNumber = (TextView) RowKOTItem.getChildAt(29);

            objPendingKOT.setPOS("");
            if (jBillingMode == Byte.parseByte("1")) {
                // TableNumber
                objPendingKOT.setTableNumber(Integer.parseInt(tvTableNumber.getText().toString()));

                // SubUdfNumber
                objPendingKOT.setSubUdfNumber(Integer.parseInt(tvSubUdfValue.getText().toString()));

                // WaiterId
                objPendingKOT.setEmployeeId(Integer.parseInt(tvWaiterNumber.getText().toString()));

                // Checked out status
                objPendingKOT.setIsCheckedOut(0);

                // TableSplitNo
                objPendingKOT.setTableSplitNo(Integer.parseInt(tvTableSplitNo.getText().toString()));

                // Customer Id
                objPendingKOT.setCusId(Integer.valueOf(edtCustId.getText().toString()));

            }

            // Time
            objPendingKOT.setTime(strTime);

            // Item Number
            if (RowKOTItem.getChildAt(0) != null) {
                objPendingKOT.setItemNumber(Integer.parseInt(ItemNumber.getText().toString()));
            }

            // Item Name
            if (RowKOTItem.getChildAt(1) != null) {
                objPendingKOT.setItemName(ItemName.getText().toString());
            }

            // Item Name
            if (RowKOTItem.getChildAt(2) != null) {
                objPendingKOT.setHSNCode(hsn.getText().toString());
            }

            // Quantity
            if (RowKOTItem.getChildAt(3) != null) {
                String qty_str = Quantity.getText().toString();
                double qty_d = 0.00;
                if(qty_str.equals("")) {
                    Quantity.setText("0.00");
                }else
                {
                    qty_d = Double.parseDouble(qty_str);
                }

                objPendingKOT.setQuantity(Float.parseFloat(String.format("%.2f",qty_d)));
            }

            // Rate
            if (RowKOTItem.getChildAt(4) != null) {
                String rate_str = Rate.getText().toString();
                double rate_d = 0.00;
                if(rate_str.equals("")) {
                    Rate.setText("0.00");
                }else
                {
                    rate_d = Double.parseDouble(rate_str);
                }
                objPendingKOT.setRate(Float.parseFloat(String.format("%.2f",rate_d)));
            }


            // Amount
            if (RowKOTItem.getChildAt(5) != null) {
                objPendingKOT.setAmount(Double.parseDouble(Amount.getText().toString()));
            }

            // Sales Tax %
            if (RowKOTItem.getChildAt(6) != null) {
                objPendingKOT.setTaxPercent(Float.parseFloat(SalesTaxPercent.getText().toString()));
            }

            // Sales Tax Amount
            if (RowKOTItem.getChildAt(7) != null) {
                objPendingKOT.setTaxAmount(Float.parseFloat(SalesTaxAmount.getText().toString()));
            }

            // Discount %
            if (RowKOTItem.getChildAt(8) != null) {
                objPendingKOT.setDiscountPercent(Float.parseFloat(DiscountPercent.getText().toString()));
            }

            // Discount Amount
            if (RowKOTItem.getChildAt(9) != null) {
                objPendingKOT.setDiscountAmount(Float.parseFloat(DiscountAmount.getText().toString()));
            }

            // Department Code
            if (RowKOTItem.getChildAt(10) != null) {
                objPendingKOT.setDeptCode(Integer.parseInt(DeptCode.getText().toString()));
            }

            // Category Code
            if (RowKOTItem.getChildAt(11) != null) {
                objPendingKOT.setCategCode(Integer.parseInt(CategCode.getText().toString()));
            }

            // Kitchen Code
            if (RowKOTItem.getChildAt(12) != null) {
                objPendingKOT.setKitchenCode(Integer.parseInt(KitchenCode.getText().toString()));
            }

            // Tax Type
            if (RowKOTItem.getChildAt(13) != null) {
                objPendingKOT.setTaxType(Integer.parseInt(TaxType.getText().toString()));
            }

            // Modifier Amount
            if (RowKOTItem.getChildAt(14) != null) {
                objPendingKOT.setModifierAmount(Float.parseFloat(ModifierAmount.getText().toString()));
            }

            // Service Tax Percent
            if (RowKOTItem.getChildAt(15) != null) {
                objPendingKOT.setServiceTaxPercent(Float.parseFloat(ServiceTaxPercent.getText().toString()));
            }

            // Service Tax Amount
            if (RowKOTItem.getChildAt(16) != null) {
                objPendingKOT.setServiceTaxAmount(Float.parseFloat(ServiceTaxAmount.getText().toString()));
            }

            // SupplyType - G/S
            if (RowKOTItem.getChildAt(17) != null) {
                objPendingKOT.setSupplyType(SupplyType.getText().toString());
            }
            if(RowKOTItem.getChildAt(22)!=null)
            {
                objPendingKOT.setUOM(UOM.getText().toString());
            }
            if(RowKOTItem.getChildAt(23)!=null)
            {
                objPendingKOT.setIGSTRate(Float.parseFloat(IGSTRate.getText().toString()));
            }
            if(RowKOTItem.getChildAt(24)!=null)
            {
                objPendingKOT.setIGSTAmount(Float.parseFloat(IGSTAmt.getText().toString()));
            }
            if(RowKOTItem.getChildAt(25)!=null)
            {
                objPendingKOT.setCessRate(Float.parseFloat(cessRate.getText().toString()));
            }
            if(RowKOTItem.getChildAt(26)!=null)
            {
                objPendingKOT.setCessAmount(Float.parseFloat(cessAmt.getText().toString()));
            }
            if(RowKOTItem.getChildAt(27)!=null)
            {
                objPendingKOT.setOriginalrate(Double.parseDouble(OriginalRate.getText().toString()));
            }
            if(RowKOTItem.getChildAt(28)!=null)
            {
                objPendingKOT.setTaxableValue(Double.parseDouble(TaxableValue.getText().toString()));
                // Log.d("","Richa : taxableVal:"+objPendingKOT.getTaxableValue());
            }
            if (RowKOTItem.getChildAt(29) != null)
            {
                objPendingKOT.setTokenNumber(Integer.parseInt(KOTNumber.getText().toString()));
            }


            // Order Mode
            objPendingKOT.setOrderMode(jBillingMode);

            // KOT No
            // objPendingKOT.setTokenNumber(iTokenNumber);

            // Print KOT STatus
            objPendingKOT.setPrintKOTStatus(iPrintKOTStatus);

            int tblno, subudfno, tblsplitno, itemno, Status;
            tblno = Integer.valueOf(tvTableNumber.getText().toString());
            subudfno = Integer.valueOf(tvSubUdfValue.getText().toString());
            tblsplitno = Integer.valueOf(tvTableSplitNo.getText().toString());
            itemno = Integer.valueOf(ItemNumber.getText().toString());
            Status = Integer.valueOf(printstatus.getText().toString());

            Cursor crsrItemsUpdate = dbBillScreen.getItemsForUpdatingKOT(tblno, subudfno, tblsplitno, itemno, jBillingMode);
            if (crsrItemsUpdate.moveToFirst()) {
                float Qty = 0,  TaxAmt = 0, SerTaxAmt = 0;
                double Amt = 0;
                double discountAmt = objPendingKOT.getDiscountAmount() + Float.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("DiscountAmount")));
                Qty = Float.valueOf(Quantity.getText().toString()) + Float.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("Quantity")));
                Amt = Double.valueOf(Amount.getText().toString()) + (crsrItemsUpdate.getDouble(crsrItemsUpdate.getColumnIndex("Amount")));
                TaxAmt = Float.valueOf(SalesTaxAmount.getText().toString()) + Float.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("TaxAmount")));
                SerTaxAmt = Float.valueOf(ServiceTaxAmount.getText().toString()) + Float.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("ServiceTaxAmount")));
                float IAmt = Float.valueOf(IGSTAmt.getText().toString()) + Float.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("IGSTAmount")));
                float cessAmount = Float.valueOf(cessAmt.getText().toString()) + Float.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("cessAmount")));
                double  taxableValue = Double.valueOf(TaxableValue.getText().toString()) +
                        Double.valueOf(crsrItemsUpdate.getString(crsrItemsUpdate.getColumnIndex("TaxableValue")));

                lResult = dbBillScreen.updateKOTDineIn(tblno, tblsplitno, itemno, Qty, Amt, TaxAmt, SerTaxAmt, jBillingMode, IAmt,cessAmount,taxableValue, discountAmt);
                Log.d("UpdateKOT", "KOT item updated at position:" + lResult);
            } else {
//                arrayList.add(objPendingKOT);
                dbBillScreen.addKOT(objPendingKOT);
            }
        }

//        for (PendingKOT pendingKOT: arrayList) {
//            dbBillScreen.addKOT(pendingKOT);
//        }

        tblOrderItems.removeAllViews();
        Cursor LoadKOT = dbBillScreen.getKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
        if (LoadKOT.moveToFirst()) {
            LoadKOTItems(LoadKOT,false); // richa to check
        }
    }

    /*************************************************************************************************************************************
     * Invokes InsertBillItems() and InsertBillDetail() functions
     *
     * @param TenderType  : Type of tender, 1 - Pay cash, 2 - PayBill Screen payment
     *************************************************************************************************************************************/
    private void mSaveBillData(int TenderType) { // TenderType:
        // 1=PayCash
        // 2=PayBill

        updateQuantityKOT();
        checkforRoundOff();


        // Insert all bill items to database
        InsertBillItems();

        // Insert bill details to database
        InsertBillDetail(TenderType);

        /*if (isPrintBill) {
            // Print bill
            PrintBill();
        }*/
    }

    private void checkforRoundOff()
    {
        String temp = tvBillAmount.getText().toString().trim();
        double finalAmount = Double.parseDouble(temp);
        double roundOffFinalAmount =0;
        if (BILLAMOUNTROUNDOFF == 1) {

            if (!temp.contains(".00")){
                roundOffFinalAmount = Math.round(finalAmount);
                fRoundOfValue = Double.parseDouble("0" + temp.substring(temp.indexOf(".")));
            } else {
                roundOffFinalAmount = finalAmount;
            }
        } else {
            roundOffFinalAmount = finalAmount;
            fRoundOfValue = 0;
        }
        tvBillAmount.setText(String.format("%.2f",roundOffFinalAmount));
    }

    /*************************************************************************************************************************************
     * Invokes InsertBillItems() and InsertBillDetail() functions
     *
     * @param TenderType  : Type of tender, 1 - Pay cash, 2 - PayBill Screen payment
     * @param isPrintBill : True if bill needs to be printed else False
     *************************************************************************************************************************************/
    private void lPreviewBill(int TenderType, boolean isPrintBill) { // TenderType:
        // 1=PayCash
        // 2=PayBill

        // Insert all bill items to database
        InsertPreviewBillItems();
        // Insert bill details to database
        //InsertBillDetail(TenderType);

        /*if (isPrintBill) {
            // Print bill
            PrintBill();
        }*/
    }




    /*************************************************************************************************************************************
     * Updates complimentary bill details in database
     *
     * @param BillNumber          : Complimentary bill number
     * @param PaidAmount          : Amount paid for the complimentary bill
     * @param ComplimentaryReason : Reason for giving complimentary bill
     *************************************************************************************************************************************/
    private void SaveComplimentaryBill(int BillNumber, double PaidAmount, String ComplimentaryReason) {
        long lResult = 0;

        ComplimentaryBillDetail objComplimentaryBillDetail = new ComplimentaryBillDetail();

        // Set bill number
        objComplimentaryBillDetail.setBillNumber(BillNumber);

        // Set complimentary reason
        objComplimentaryBillDetail.setComplimentaryReason(ComplimentaryReason);

        // Set paid amount
        objComplimentaryBillDetail.setPaidAmount(PaidAmount);

        lResult = dbBillScreen.addComplimentaryBillDetail(objComplimentaryBillDetail);

        Log.v("SaveComplimentaryBill", "Complimentary Bill inserted at Row:" + lResult);
    }

    /*************************************************************************************************************************************
     * Gets quantity for item from weighing scale
     *
     * @return Returns Weighing scale reading represented by string
     *************************************************************************************************************************************/
    private String getQuantityFromWeighScale() {
        // Read data from weigh scale through serial port

        // Convert data to string and return the value to calling function

        return "1";
    }

    private int getIndex_pos(String substring){

        int index = 0;
        for (int i = 0; index==0 && i < spnr_pos.getCount(); i++){

            if (spnr_pos.getItemAtPosition(i).toString().contains(substring)){
                index = i;
            }

        }

        return index;

    }
    private String getState_pos(String substring){

        String  index = "";
        for (int i = 0; i < spnr_pos.getCount(); i++){

            if (spnr_pos.getItemAtPosition(i).toString().contains(substring)){
                index = spnr_pos.getItemAtPosition(i).toString();
                break;
            }

        }

        return index;

    }
    /*************************************************************************************************************************************
     * Opens tender window in dine in and take away billing mode
     *************************************************************************************************************************************/
    private void PayBill() {


        if (jBillingMode == Byte.parseByte("1")) {
            // DinInTender();

        }

    }

    /*************************************************************************************************************************************
     * Calculates all the amount after giving overall discount in tender window
     * and updates the new values in text boxes
     *
     * @param dDiscountPercent : Discount percent
     *************************************************************************************************************************************/
    private void OverAllDiscount(double dDiscountPercent) {
        double dRate = 0, dTaxPercent = 0, dTaxAmt = 0, dDiscAmt = 0, dTempAmt = 0;
        TableRow rowItem;
        TextView DiscAmt, DiscPercent, Qty, Rate, TaxAmt, TaxPercent, TaxType;

        for (int i = 0; i < tblOrderItems.getChildCount(); i++) {

            // Get Item row
            rowItem = (TableRow) tblOrderItems.getChildAt(i);
            if (rowItem.getChildAt(0) != null) {
                // Get Discount percent
                Qty = (TextView) rowItem.getChildAt(3);
                Rate = (TextView) rowItem.getChildAt(4);
                DiscPercent = (TextView) rowItem.getChildAt(8);
                DiscAmt = (TextView) rowItem.getChildAt(9);
                TaxPercent = (TextView) rowItem.getChildAt(6);
                TaxAmt = (TextView) rowItem.getChildAt(7);
                TaxType = (TextView) rowItem.getChildAt(13);
                DiscPercent.setText(String.format("%.2f", dDiscountPercent));

                dRate = Double.parseDouble(Rate.getText().toString());
                dTaxPercent = Double.parseDouble(TaxPercent.getText().toString());

                if (TaxType.getText().toString().equalsIgnoreCase("1")) {
                    // Discount
                    dDiscAmt = dRate * (dDiscountPercent / 100);
                    dTempAmt = dDiscAmt;
                    dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());

                    // Tax
                    dTaxAmt = (dRate - dTempAmt) * (dTaxPercent / 100);
                    dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());

                    TaxAmt.setText(String.format("%.2f", dTaxAmt));
                    DiscAmt.setText(String.format("%.2f", dDiscAmt));

                } else {
                    double dBasePrice = 0;
                    dBasePrice = dRate / (1 + (dTaxPercent / 100));

                    // Discount
                    dDiscAmt = dBasePrice * (dDiscountPercent / 100);
                    dTempAmt = dDiscAmt;
                    dDiscAmt = dDiscAmt * Double.parseDouble(Qty.getText().toString());

                    // Tax
                    dTaxAmt = (dBasePrice - dTempAmt) * (dTaxPercent / 100);
                    dTaxAmt = dTaxAmt * Double.parseDouble(Qty.getText().toString());

                    TaxAmt.setText(String.format("%.2f", dTaxAmt));
                    DiscAmt.setText(String.format("%.2f", dDiscAmt));
                }
            }
        }

        CalculateTotalAmount();
    }


    /*************************************************************************************************************************************
     * Delete Button Click event, calls DeleteItemByMenuCode() function
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void Delete(View v) {
        DeleteItem();
    }

    /*************************************************************************************************************************************
     * Clear Button Click event, calls ClearAll() function
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void Clear(View v) {
        ClearAll();
        ResetCustomer();
        /*if (jBillingMode == 3) {
            ControlsSetDisabled();
        }*/
        btnPayBill.setEnabled(false);
        btnPrintBill.setEnabled(false);
        btnPreviewBill.setEnabled(false);

        if (crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode")).equalsIgnoreCase("1")) {
            //    grdItems.setNumColumns(6);
            mGridLayoutManager = new GridLayoutManager(BillingDineInActivity.this, 6);
            mRecyclerGridView.setLayoutManager(mGridLayoutManager);
            GetItemDetails();

        } else if (crsrSettings.getString(crsrSettings.getColumnIndex("FastBillingMode")).equalsIgnoreCase("2")) {
            //     grdItems.setNumColumns(4);
            mGridLayoutManager = new GridLayoutManager(BillingDineInActivity.this, 4);
            mRecyclerGridView.setLayoutManager(mGridLayoutManager);

            GetItemDetailswithoutDeptCateg();
            lstvwDepartment.setAdapter(null);
            // grdItems.setAdapter(null);
            mRecyclerGridView.setAdapter(null);
        } else {

            mGridLayoutManager = new GridLayoutManager(BillingDineInActivity.this, 2);
            mRecyclerGridView.setLayoutManager(mGridLayoutManager);

            GetItemDetailswithoutDeptCateg();

            lstvwDepartment.setAdapter(null);
            lstvwCategory.setAdapter(null);
            //      grdItems.setAdapter(null);
            mRecyclerGridView.setAdapter(null);
        }
    }

    /*************************************************************************************************************************************
     * Load KOT Button Click event, opens Load KOT activity only if KOT is
     * pending
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void LoadKOT(View v) {
        Cursor KOT = dbBillScreen.getOccupiedTables();
        if (KOT.moveToFirst()) {
            do {
                Log.d("OccupiedTables", "Table Number:" + KOT.getString(0) + " SubUdf Number:" + KOT.getString(1));
            } while (KOT.moveToNext());
            ClearAll(); // To avoid adding item to existing item in table layout
            Intent intentShiftTable = new Intent(myContext, TableShiftMergeActivity.class);
            intentShiftTable.putExtra("SHIFT_MERGE", 3);
            intentShiftTable.putExtra("USER_NAME", strUserName);
            startActivityForResult(intentShiftTable, 4);

        } else {
            MsgBox.Show("Warning", "No occupied tables");
        }
    }

    /*************************************************************************************************************************************
     * Modify KOT Button Click event, opens Load KOT activity only if KOT is
     * pending
     *
     * @param v  : Clicked Button
     *************************************************************************************************************************************/
    public void ModifyKOT(View v) {
        if (tblOrderItems.getChildCount() > 0) {
            DisplayModifyKOT();
        } else {
            MsgBox.Show("Note", "No Items Found in KOT");
        }
    }

    private void DisplayModifyKOT() {
        tblOrderItems.removeAllViews();
        String strTableNumber = tvTableNumber.getText().toString();
        String strTableSplitNo = tvTableSplitNo.getText().toString();
        // String strSubUdfNumber = data.getStringExtra("SUB_UDF_NUMBER");
        Log.v("Load Modify KOT", "TableNumber:" + strTableNumber);
        Cursor LoadModifyKOT = null;
        if(jBillingMode==1)
            LoadModifyKOT = dbBillScreen.getKOTItems(Integer.parseInt(strTableNumber), 1, Integer.parseInt(strTableSplitNo));
        else
            LoadModifyKOT =dbBillScreen.getKOTItems(iCustId, String.valueOf(jBillingMode));

        if (LoadModifyKOT.moveToFirst()) {
            LoadModifyKOTItems(LoadModifyKOT);
            btnPrintBill.setEnabled(false);
            btnPayBill.setEnabled(false);
            btnPreviewBill.setEnabled(false);

            // btnDeleteKOT.setEnabled(true);
        } else {
            Log.v("Load Modify KOT", "ERROR: No items found");
            MsgBox.Show("Warning", "No Items Found in KOT");
        }
    }

    /*************************************************************************************************************************************
     * Void KOT Button Click event, prompts a dialog to enter reason for voiding
     * KOT and saves the reason in database and deletes the KOT
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void VoidKOT(View v) {
        AlertDialog.Builder VoidKOT = new AlertDialog.Builder(myContext);
        final EditText txtReason = new EditText(myContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
        params.setMargins(15, 5, 5, 5);
        txtReason.setLayoutParams(params);

        VoidKOT .setIcon(R.drawable.ic_launcher)
                .setTitle("Void KOT")
                .setMessage("Please enter reason for voiding KOT")
                .setView(txtReason)
                .setNegativeButton("Cancel", null).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (txtReason.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Enter reason before voiding KOT");
                } else {
                    // Calendar now = Calendar.getInstance();
                    DeletedKOT objDeletedKOT = new DeletedKOT();
                    objDeletedKOT.setReason(txtReason.getText().toString());
                    objDeletedKOT.setEmployeeId(Integer.parseInt(tvWaiterNumber.getText().toString()));
                    objDeletedKOT.setTableNumber(Integer.parseInt(tvTableNumber.getText().toString()));
                    objDeletedKOT.setSubUdfNumber(Integer.parseInt(tvSubUdfValue.getText().toString()));
                    objDeletedKOT.setTokenNumber(iTokenNumber);
                    String strTime = String.format("%tR", Time);
                    objDeletedKOT.setTime(strTime);
                    long lResult = dbBillScreen.addDeletedKOT(objDeletedKOT);
                    Log.v("VoidKOT", "Deleted KOT added, Row ID:" + lResult);
                    lResult = dbBillScreen.deleteKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                            Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
                    Log.v("VoidKOT", "KOT items deleted, Rows Deleted:" + lResult);
                    ClearAll();
                }
            }
        }).show();
    }

    /*************************************************************************************************************************************
     * Pay Cash Button Click event, directly saves and prints the bill without
     * opening PayBill dialog
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void PayCash(View v) {
        if (tblOrderItems.getChildCount() > 0) {
            mSaveBillData(1);
            generateInvoicePdf();
            if (jBillingMode == 1) {
                int iResult = dbBillScreen.deleteKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                        Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
                Log.d("Pay Cash", "Items deleted from pending KOT:" + iResult);
            }
            ClearAll();
        } else {
            MsgBox.Show("Warning", "No item in bill");
        }
    }

    /*************************************************************************************************************************************
     * Close Button Click event, closes database connection and finishes the
     * bill screen activity
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void Close(View v) {

        // Close Database Connection
//        dbBillScreen.updateKOTNo(iKOTNo);
//        dbBillScreen.CloseDatabase();
//        db.CloseDatabase();

        // finish the activity
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbBillScreen.CloseDatabase();
        db.CloseDatabase();
    }

    /*************************************************************************************************************************************
     * Split Bill Button Click event, opens split bill window by loading present
     * bill items in split bill window
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
//    public void SplitBill(View v) {
//        try {
//            String UserName, TableNumber;
//            //UserName = tvUserName.getText().toString();
//            TableNumber = tvTableNumber.getText().toString();
//
//            Intent intentSplitBill = new Intent(myContext, SplitBillActivity.class);
//            intentSplitBill.putExtra("USER_ID", strUserId);
//            intentSplitBill.putExtra("USER_NAME", strUserName);
//            intentSplitBill.putExtra("BILL_DATE", strDate);
//            intentSplitBill.putExtra("BILL_NUMBER", tvBillNumber.getText().toString());
//            intentSplitBill.putExtra("TABLE_NUMBER", TableNumber);
//            intentSplitBill.putExtra("TABLE_SPLIT_NO", tvTableSplitNo.getText().toString());
//            intentSplitBill.putExtra("UDF_NUMBER", "1");// tvSubUdfValue.getText());
//            intentSplitBill.putExtra("SERVICETAX_TYPE", String.valueOf(iTaxType));
//            intentSplitBill.putExtra("SERVICETAX_PERCENT", String.valueOf(dServiceTaxPercent));
//            startActivity(intentSplitBill);
//            ClearAll();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    /*************************************************************************************************************************************
     * PayBill Button Click event, opens tender dialog for bill tender process in
     * dine in billing mode
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void PayBill(View v) {

        if (tvBillAmount.getText().toString().equals("") ) {
            MsgBox.Show("Warning", "Please add item to make bill");
        } else if ( tvSubTotal.getText().toString().equals("0.00")) {
            MsgBox.Show("Warning", "Please add item of rate greater than 0.00");
        }else if (chk_interstate.isChecked() && spnr_pos.getSelectedItem().equals("")) {
            MsgBox.Show("Warning", "Please Select Code for Intersate Supply");
        }
        /*else if (jBillingMode== 4 && strPaymentStatus!= null && strPaymentStatus.equalsIgnoreCase("Paid"))
        { // richa to prevent from repaying the bill in delivery mode
            MsgBox.Show("Warning", "Bill is already paid");
        }*/

        else {
            ArrayList<AddedItemsToOrderTableClass> orderItemList = new ArrayList<>();
            int taxType =0;
            for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {

                int menuCode =0;
                String itemName= "";
                double quantity=0.00;
                double rate=0.00;
                double igstRate=0.00;
                double igstAmt=0.00;
                double cgstRate=0.00;
                double cgstAmt=0.00;
                double sgstRate=0.00;
                double sgstAmt=0.00;
                double cessRate=0.00;
                double cessAmt=0.00;
                double subtotal=0.00;
                double billamount=0.00;
                double discountamount=0.00;

                TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(iRow);


                // Item Number
                if (RowBillItem.getChildAt(0) != null) {
                    CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                    menuCode = (Integer.parseInt(ItemNumber.getText().toString()));
                }

                // Item Name
                if (RowBillItem.getChildAt(1) != null) {
                    TextView ItemName = (TextView) RowBillItem.getChildAt(1);
                    itemName = (ItemName.getText().toString());
                }



                // Quantity
                if (RowBillItem.getChildAt(3) != null) {
                    EditText Quantity = (EditText) RowBillItem.getChildAt(3);
                    String qty_str = Quantity.getText().toString();
                    double qty_d = 0.00;
                    if(qty_str==null || qty_str.equals(""))
                    {
                        Quantity.setText("0.00");
                    }else
                    {
                        qty_d = Double.parseDouble(qty_str);
                    }
                    quantity = (Double.parseDouble(String.format("%.2f",qty_d)));

                }

                // Rate
                if (RowBillItem.getChildAt(4) != null) {
                    EditText Rate = (EditText) RowBillItem.getChildAt(4);
                    String rate_str = Rate.getText().toString();
                    double rate_d = 0.00;
                    if((rate_str==null || rate_str.equals("")))
                    {
                        Rate.setText("0.00");
                    }else
                    {
                        rate_d = Double.parseDouble(rate_str);
                    }
                    rate = (Double.parseDouble(String.format("%.2f",rate_d)));

                }


                // Service Tax Percent

                if(chk_interstate.isChecked()) // IGST
                {
                    cgstRate =0;
                    cgstAmt =0;
                    sgstRate =0;
                    sgstAmt =0;
                    if (RowBillItem.getChildAt(23) != null) {
                        TextView iRate = (TextView) RowBillItem.getChildAt(23);
                        igstRate = (Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(iRate.getText().toString()))));
                    }

                    // Service Tax Amount
                    if (RowBillItem.getChildAt(24) != null) {
                        TextView iAmt = (TextView) RowBillItem.getChildAt(24);
                        igstAmt =  Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(iAmt.getText().toString())));
                    }


                }else // CGST+SGST
                {
                    igstRate =0;
                    igstAmt =0;
                    if (RowBillItem.getChildAt(15) != null) {
                        TextView ServiceTaxPercent = (TextView) RowBillItem.getChildAt(15);
                        sgstRate = (Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(ServiceTaxPercent.getText().toString()))));
                    }

                    // Service Tax Amount
                    if (RowBillItem.getChildAt(16) != null) {
                        TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                        sgstAmt =  Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(ServiceTaxAmount.getText().toString())));
                    }

                    // Sales Tax %
                    if (RowBillItem.getChildAt(6) != null) {
                        TextView SalesTaxPercent = (TextView) RowBillItem.getChildAt(6);
                        cgstRate = Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(SalesTaxPercent.getText().toString())));
                    }
                    // Sales Tax Amount
                    if (RowBillItem.getChildAt(7) != null) {
                        TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                        cgstAmt = Double.parseDouble(String.format("%.2f",
                                Double.parseDouble(SalesTaxAmount.getText().toString())));
                    }
                }
                if (RowBillItem.getChildAt(25) != null) {
                    TextView cessRt = (TextView)RowBillItem.getChildAt(25);
                    if(!cessRt.getText().toString().equals(""))
                        cessRate = Double.parseDouble(cessRt.getText().toString());
                }
                if (RowBillItem.getChildAt(26) != null) {
                    TextView cessAt = (TextView)RowBillItem.getChildAt(26);
                    if(!cessAt.getText().toString().equals(""))
                        cessAmt = Double.parseDouble(cessAt.getText().toString());
                }



                // Tax Type
                if (RowBillItem.getChildAt(13) != null) {
                    TextView TaxType = (TextView) RowBillItem.getChildAt(13);
                    taxType = (Integer.parseInt(TaxType.getText().toString()));
                }
                // subtotal
                subtotal = (rate*quantity) + igstAmt+cgstAmt+sgstAmt;

                AddedItemsToOrderTableClass orderItem = new AddedItemsToOrderTableClass( menuCode, itemName, quantity, rate,
                        igstRate,igstAmt, cgstRate, cgstAmt, sgstRate,sgstAmt, rate*quantity,subtotal, billamount,cessRate,cessAmt,discountamount);
                orderItemList.add(orderItem);
            }

            Bundle bundle = new Bundle();
            bundle.putDouble(Constants.TOTALBILLAMOUNT, Double.parseDouble(tvBillAmount.getText().toString()));
            bundle.putDouble(Constants.TAXABLEVALUE, Double.parseDouble(tvSubTotal.getText().toString()));

//            bundle.putDouble(Constants.ROUNDOFFAMOUNT, Double.parseDouble(edtRoundOff.getText().toString()));
            if (tvOthercharges.getText().toString().isEmpty()) {
                bundle.putDouble(Constants.OTHERCHARGES, Double.parseDouble(tvOthercharges.getText().toString()));
            } else {
                bundle.putDouble(Constants.OTHERCHARGES, Double.parseDouble(tvOthercharges.getText().toString()));
            }
            bundle.putDouble(Constants.DISCOUNTAMOUNT, Double.parseDouble(tvDiscountAmount.getText().toString()));
            bundle.putInt(Constants.TAXTYPE, isForwardTaxEnabled);
            //bundle.putInt(CUSTID, Integer.parseInt(tvCustId.getText().toString()));
            bundle.putString(Constants.PHONENO, edtCustPhoneNo.getText().toString());
            bundle.putParcelableArrayList(Constants.ORDERLIST, orderItemList);
            if (chk_interstate.isChecked()) {
                double igstAmount = Double.parseDouble(tvIGSTValue.getText().toString());
                double cessAmount = Double.parseDouble(tvcessValue.getText().toString());
                bundle.putDouble(Constants.TAXAMOUNT, igstAmount + cessAmount);
            } else {
                double sgstAmount = Double.parseDouble(tvSGSTValue.getText().toString());
                double cgstAmount = Double.parseDouble(tvCGSTValue.getText().toString());
                double cessAmount = Double.parseDouble(tvcessValue.getText().toString());
                bundle.putDouble(Constants.TAXAMOUNT, cgstAmount + sgstAmount + cessAmount);
            }

            fm = getSupportFragmentManager();
            proceedToPayBillingFragment = new PayBillFragment();
            proceedToPayBillingFragment.initProceedToPayListener(this);
            proceedToPayBillingFragment.setArguments(bundle);
            proceedToPayBillingFragment.show(fm, "Proceed To Pay");

           /* Intent intentTender = new Intent(myContext, PayBillActivity.class);
            intentTender.putExtra("TotalAmount", tvBillAmount.getText().toString());
            intentTender.putExtra("CustId", edtCustId.getText().toString());
            intentTender.putExtra("phone", edtCustDineInPhoneNo.getText().toString());
            intentTender.putExtra("BaseValue", Float.parseFloat(tvSubTotal.getText().toString()));
            intentTender.putExtra("OtherCharges", Double.parseDouble(tvOthercharges.getText().toString()));
            intentTender.putExtra("TaxType", taxType);// forward/reverse
            intentTender.putParcelableArrayListExtra("OrderList", orderItemList);
            intentTender.putExtra("USER_NAME", strUserName);
            intentTender.putExtra("BillAmountRoundOff", BILLAMOUNTROUNDOFF);
            startActivityForResult(intentTender, 1);*/
        }
    }


    /*************************************************************************************************************************************
     * Delete Bill Button Click event, calls delte bill function
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void DeleteBill(View v) {

        //DeleteVoid(Byte.parseByte("1"));
        tblOrderItems.removeAllViews();

        AlertDialog.Builder DineInTenderDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.dinein_reprint, null);

        final ImageButton btnCal_reprint = (ImageButton) vwAuthorization.findViewById(R.id.btnCal_reprint);

        final EditText txtReprintBillNo = (EditText) vwAuthorization.findViewById(R.id.txtDineInReprintBillNumber);
        final TextView tv_inv_date = (TextView) vwAuthorization.findViewById(R.id.tv_inv_date);
        tv_inv_date.setText(tvDate.getText().toString());
        btnCal_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelection(tv_inv_date);
            }
        });


        DineInTenderDialog.setIcon(R.drawable.ic_launcher)
                .setTitle("Delete Bill")
                .setView(vwAuthorization)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        if (txtReprintBillNo.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please enter Bill Number");
                            return;
                        } else if (tv_inv_date.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please enter Bill Date");
                            setInvoiceDate();
                            return;
                        }  else {
                            try
                            {
                                int InvoiceNo = Integer.valueOf(txtReprintBillNo.getText().toString());
                                String date_reprint = tv_inv_date.getText().toString();
                                tvDate.setText(date_reprint);
                                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(date_reprint);
                                Cursor result = dbBillScreen.getBillDetail(InvoiceNo, String.valueOf(date.getTime()));

                                if (result.moveToFirst()) {
                                    if (result.getInt(result.getColumnIndex("BillStatus")) != 0) {
                                        VoidBill(InvoiceNo,String.valueOf(date.getTime()));
                                    } else {

                                        // Toast.makeText(myContext, "Bill is already voided", Toast.LENGTH_SHORT).show();
                                        String msg = "Bill Number "+InvoiceNo+ " is already voided";
                                        MsgBox.Show("Note",msg);
                                        Log.d("VoidBill",msg);
                                    }
                                } else {
                                    // Toast.makeText(myContext, "No bill found with bill number " + InvoiceNo, Toast.LENGTH_SHORT).show();
                                    String msg = "No bill found with bill number " + InvoiceNo;
                                    MsgBox.Show("Note",msg);
                                    Log.d("VoidBill",msg);
                                }
                                ClearAll();

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
    }

    /*************************************************************************************************************************************
     * Reprint Bill Button Click event, calls reprint bill function
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void ReprintBill(View v) {

        //ReprintVoid(Byte.parseByte("1"));
        tblOrderItems.removeAllViews();

        AlertDialog.Builder DineInTenderDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.dinein_reprint, null);

        final ImageButton btnCal_reprint = (ImageButton) vwAuthorization.findViewById(R.id.btnCal_reprint);

        final EditText txtReprintBillNo = (EditText) vwAuthorization.findViewById(R.id.txtDineInReprintBillNumber);
        final TextView tv_inv_date = (TextView) vwAuthorization.findViewById(R.id.tv_inv_date);
        tv_inv_date.setText(tvDate.getText().toString());
        btnCal_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelection(tv_inv_date);
            }
        });


        DineInTenderDialog.setIcon(R.drawable.ic_launcher)
                .setTitle("RePrint Bill")
                .setView(vwAuthorization).setNegativeButton("Cancel", null)
                .setPositiveButton("RePrint", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        if (txtReprintBillNo.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please enter Bill Number");
                            setInvoiceDate();
                            return;
                        }else if (tv_inv_date.getText().toString().equalsIgnoreCase("")) {
                            MsgBox.Show("Warning", "Please enter Bill Date");
                            setInvoiceDate();
                            return;
                        }  else {
                            try
                            {
                                int billStatus =0;
                                int billNo = Integer.valueOf(txtReprintBillNo.getText().toString());
                                String date_reprint = tv_inv_date.getText().toString();
                                tvDate.setText(date_reprint);
                                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(date_reprint);
                                Cursor LoadItemForReprint = dbBillScreen.getItemsFromBillItem_new(
                                        billNo, String.valueOf(date.getTime()));
                                if (LoadItemForReprint.moveToFirst())
                                {
                                    Cursor cursor = dbBillScreen.getBillDetail(billNo,String.valueOf(date.getTime()));
                                    if (cursor != null && cursor.moveToFirst()) {
                                        billStatus = cursor.getInt(cursor.getColumnIndex("BillStatus"));
                                        if (billStatus == 0) {
                                            MsgBox.Show("Warning", "This bill has been deleted");
                                            setInvoiceDate();
                                            return;
                                        }
                                        String pos = cursor.getString(cursor.getColumnIndex("POS"));
                                        String custStateCode = cursor.getString(cursor.getColumnIndex("CustStateCode"));
                                        if (pos != null && !pos.equals("") && custStateCode != null && !custStateCode.equals("") && !custStateCode.equalsIgnoreCase(pos)) {
                                            chk_interstate.setChecked(true);
                                            int index = getIndex_pos(custStateCode);
                                            spnr_pos.setSelection(index);
                                            // System.out.println("reprint : InterState");
                                        } else {
                                            chk_interstate.setChecked(false);
                                            spnr_pos.setSelection(0);
                                            //System.out.println("reprint : IntraState");
                                        }
                                        fTotalDiscount = cursor.getFloat(cursor.getColumnIndex("TotalDiscountAmount"));
                                        float discper = cursor.getFloat(cursor.getColumnIndex("DiscPercentage"));
                                        reprintBillingMode = cursor.getInt(cursor.getColumnIndex("BillingMode"));

                                        tvDiscountPercentage.setText(String.format("%.2f", discper));
                                        tvDiscountAmount.setText(String.format("%.2f", fTotalDiscount));
                                        tvBillNumber.setText(txtReprintBillNo.getText().toString());

                                        tvIGSTValue.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("IGSTAmount"))));
                                        tvCGSTValue.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("CGSTAmount"))));
                                        tvSGSTValue.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("SGSTAmount"))));
                                        tvSubTotal.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("TaxableValue"))));
                                        tvBillAmount.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("BillAmount"))));
                                        edtCustId.setText(cursor.getString(cursor.getColumnIndex("CustId")));

                                        LoadItemsForReprintBill(LoadItemForReprint);

                                    }

                                } else {
                                    MsgBox.Show("Warning",
                                            "No Item is present for the Bill Number " + txtReprintBillNo.getText().toString() +", Dated :"+tv_inv_date.getText().toString());
                                    setInvoiceDate();
                                    return;
                                }
                                if(reprintBillingMode ==4 && billStatus ==2)
                                {
                                    strPaymentStatus = "Cash On Delivery";
                                }
                                else
                                    strPaymentStatus = "Paid";
                                isReprint = true;
                                PrintNewBill(date_reprint, 1);
                                // update bill reprint count
                                int Result = dbBillScreen
                                        .updateBillRepintCount(Integer.parseInt(txtReprintBillNo.getText().toString()));
                                ClearAll();
                                if (!(crsrSettings.getInt(crsrSettings.getColumnIndex("Tax")) == 1)) { // reverse tax
                                    REVERSETAX = true;
                                }else
                                {
                                    REVERSETAX = false;
                                }

                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();


    }

    private void DateSelection(final TextView tv_inv_date) {        // StartDate: DateType = 1 EndDate: DateType = 2
        try {
            AlertDialog.Builder dlgReportDate = new AlertDialog.Builder(myContext);
            final DatePicker dateReportDate = new DatePicker(myContext);
            String date_str = tvDate.getText().toString();
            String dd = date_str.substring(6,10)+"-"+date_str.substring(3,5)+"-"+date_str.substring(0,2);
            DateTime objDate = new DateTime(dd);
            dateReportDate.updateDate(objDate.getYear(), objDate.getMonth(), objDate.getDay());
            String strMessage = "";


            dlgReportDate
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("Date Selection")
                    .setMessage(strMessage)
                    .setView(dateReportDate)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        String strDd = "";
                        public void onClick(DialogInterface dialog, int which) {
                            if (dateReportDate.getDayOfMonth() < 10) {
                                strDd = "0" + String.valueOf(dateReportDate.getDayOfMonth())+"-";
                            } else {
                                strDd = String.valueOf(dateReportDate.getDayOfMonth())+"-";
                            }
                            if (dateReportDate.getMonth() < 9) {
                                strDd += "0" + String.valueOf(dateReportDate.getMonth() + 1) + "-";
                            } else {
                                strDd += String.valueOf(dateReportDate.getMonth() + 1) + "-";
                            }

                            strDd += String.valueOf(dateReportDate.getYear());
                            tv_inv_date.setText(strDd);
                            Log.d("ReprintDate", "Selected Date:" + strDd);
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
    /*************************************************************************************************************************************
     * Void Bill Button Click event, opens a dialog to enter admin user id and
     * password for voiding bill if user is admin then bill will be voided
     *
     *************************************************************************************************************************************/
    public void VoidBill(final int invoiceno , final String Invoicedate) {

        AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.user_authorization, null);

        final EditText txtUserId = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserId);
        final EditText txtPassword = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserPassword);

        AuthorizationDialog.setTitle("Authorization").setIcon(R.drawable.ic_launcher).setView(vwAuthorization)
                .setNegativeButton("Cancel", null).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Cursor User = dbBillScreen.getUser(txtUserId.getText().toString(),
                        txtPassword.getText().toString());
                if (User.moveToFirst()) {
                    if (User.getInt(User.getColumnIndex("RoleId")) == 1) {
                        //ReprintVoid(Byte.parseByte("2"));
                        int result = db.makeBillVoids(invoiceno, Invoicedate);
                        if(result >0)
                        {
                            Date dd = new Date(Long.parseLong(Invoicedate));
                            String dd_str = new SimpleDateFormat("dd-MM-yyyy").format(dd);
                            String msg = "Bill Number "+invoiceno+" , Dated : "+dd_str+" voided successfully";
                            // MsgBox.Show("Warning", msg);
                            Toast.makeText(myContext, msg, Toast.LENGTH_SHORT).show();
                            Log.d("VoidBill", msg);
                        }
                    } else {
                        MsgBox.Show("Warning", "Void Bill failed due to in sufficient access privilage");
                    }
                } else {
                    MsgBox.Show("Warning", "Void Bill failed due to wrong user id or password");
                }
            }
        }).show();
    }


    /*************************************************************************************************************************************
     * Shift table Button Click event, opens shift table dialog
     *
     * @param v : Clicked Button
     *************************************************************************************************************************************/
    public void ShiftTable(View v) {

        Cursor KOT = dbBillScreen.getOccupiedTables();
        if (KOT.moveToFirst()) {
            do {
                Log.d("OccupiedTables", "Table Number:" + KOT.getString(0) + " SubUdf Number:" + KOT.getString(1));
            } while (KOT.moveToNext());
            ClearAll();
            Intent intentShiftTable = new Intent(myContext, TableShiftMergeActivity.class);
            intentShiftTable.putExtra("SHIFT_MERGE", 1);
            intentShiftTable.putExtra("USER_NAME", strUserName);
            startActivity(intentShiftTable);

        } else {
            MsgBox.Show("Warning", "No occupied tables");
            Log.d("OccupiedTables", "No rows returned from database");
        }
    }

    /*************************************************************************************************************************************
     * Starts TableShiftMerge activity if and only if pending KOT is present in
     * database
     *
     * @param v : Merge Table button
     ************************************************************************************************************************************/
    public void MergeTable(View v) {

        Cursor KOT = dbBillScreen.getOccupiedTables();
        if (KOT.moveToFirst()) {
            do {
                Log.d("OccupiedTables", "Table Number:" + KOT.getString(0) + " SubUdf Number:" + KOT.getString(1));
            } while (KOT.moveToNext());
            ClearAll();
            Intent intentShiftTable = new Intent(myContext, TableShiftMergeActivity.class);
            intentShiftTable.putExtra("SHIFT_MERGE", 2);
            intentShiftTable.putExtra("USER_NAME", strUserName);
            startActivity(intentShiftTable);

        } else {
            MsgBox.Show("Warning", "No occupied tables");
            Log.d("OccupiedTables", "No rows returned from database");
        }
    }

    /*************************************************************************************************************************************
     * Starts Table activity for table and waiter selection
     *
     * @param v :Table / Waiter buttons present Table - Waiter row in dine in
     *          screen
     ************************************************************************************************************************************/
    public void Table_Waiter(View v) {
        //startActivityForResult(new Intent(myContext, TableActivity.class), 2);
        Intent intentDineIn = new Intent(myContext, TableActivity.class);
        intentDineIn.putExtra("BILLING_MODE", String.valueOf(jBillingMode));
        intentDineIn.putExtra("USER_ID", strUserId);//spUser.getString("USER_ID", "GHOST"));
        intentDineIn.putExtra("USER_NAME", strUserName);//spUser.getString("USER_NAME", "GHOST"));
        intentDineIn.putExtra("CUST_ID", 0);
        startActivity(intentDineIn);
        this.finish();
    }

    /*************************************************************************************************************************************
     * Invokes saveOrder function to save order if order is new else updates the
     * order in database
     *
     * @param v : SaveOrder button
     ************************************************************************************************************************************/
    public void SaveKOT(View v) {
        iPrintKOTStatus = 1;

        isSaveOnly = true;
        int i = SaveKOT();
        Log.d("Billing Activity", "SaveKOT : Status = "+i );
        if(i>0)
        {
            btnPayBill.setEnabled(true);
            btnPrintBill.setEnabled(true);
            btnPreviewBill.setEnabled(true);
        }
        else
        {
            btnPayBill.setEnabled(false);
            btnPrintBill.setEnabled(false);
            btnPreviewBill.setEnabled(true);
        }
        //PrintKOT();
    }

    private boolean isSaveOnly = true;

    public int  SaveKOT() {
        int returnStatus =1;
//        if(iKOTNo == -1)
//            iKOTNo = dbBillScreen.getKOTNo();
        try{
            if (jBillingMode == 1) {
                String tableno = tvTableNumber.getText().toString();
                String waiterno = tvWaiterNumber.getText().toString();
                if (tableno.equalsIgnoreCase("") || waiterno.equalsIgnoreCase("") || tableno.equalsIgnoreCase("0") || waiterno.equalsIgnoreCase("0")) {
                    MsgBox.Show("Warning", "Select Table and Waiter before saving order");
                } else {
                    if (tblOrderItems.getChildCount() < 1) {
                        MsgBox.Show("Warning", "Add Item before Saving KOT");

                    } else {

                        int iResult = 0;

                        iResult = dbBillScreen.deleteKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                                Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));

//                        if (kotPrint().size() > 0) {
//                            iKOTNo = dbBillScreen.getKOTNo();
                        InsertKOTItems();
//                        }
                        Toast.makeText(myContext, "KOT Saved Successfully", Toast.LENGTH_LONG).show();

                        btnPrintKOT.setEnabled(true);
                        btnPrintBill.setEnabled(true);
                        btnPreviewBill.setEnabled(true);
                        // Delete Table Booking
                        int Result = dbBillScreen.DeleteTableBooking(tvTableNumber.getText().toString());
                        //ClearAll();
                        btnPayBill.setEnabled(true);
                    }
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            MsgBox.Show("Opps","Some error occured while saving KOT");
            returnStatus = 0;
        }finally{
            return returnStatus;}
    }

    private void updateOutwardStock()
    {
        Log.d(TAG, "updateOutwardStock()");
        String businessdate = tvDate.getText().toString();
        DatabaseHandler db_local = new DatabaseHandler(getApplicationContext());
        db_local.CreateDatabase();
        db_local.OpenDatabase();
        StockOutwardMaintain stock_outward = new StockOutwardMaintain(getApplicationContext(), db_local);
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
            TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(iRow);
            int menuCode = -1;
            String itemname = "";
            double closingQty = 0;
            // Item Number
            if (RowBillItem.getChildAt(0) != null) {
                CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                menuCode = (Integer.parseInt(ItemNumber.getText().toString()));
            }
            // Item Name
            if (RowBillItem.getChildAt(1) != null) {
                TextView ItemName = (TextView) RowBillItem.getChildAt(1);
                itemname = (ItemName.getText().toString());
            }

            // Quantity
            if (RowBillItem.getChildAt(3) != null){
                TextView ItemQuantity = (TextView) RowBillItem.getChildAt(3);
                double qty_to_reduce = Double.parseDouble(ItemQuantity.getText().toString());
                //Cursor cursor = db_local.getItem(menuCode);
                Cursor cursor = db_local.getOutwardStockItem(businessdate,menuCode);
                if(cursor!=null && cursor.moveToNext())
                {
                    closingQty = cursor.getDouble(cursor.getColumnIndex("ClosingStock"));
                    if(closingQty<= qty_to_reduce)
                        closingQty =0;
                    else
                        closingQty -= qty_to_reduce;
                    stock_outward.updateClosingStock_Outward(businessdate,menuCode,itemname,closingQty);
                }

            }

        } // end of for
        db_local.CloseDatabase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {

        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: // PayBill Activity Result
                    boolean isComplimentaryBill, isDiscounted, isPrintBill = false;
                    float dDiscPercent;
                    String strComplimentaryReason = "";

                    isComplimentaryBill = data.getBooleanExtra(PayBillActivity.IS_COMPLIMENTARY_BILL, false);
                    isDiscounted = data.getBooleanExtra(PayBillActivity.IS_DISCOUNTED, false);
                    isPrintBill = data.getBooleanExtra(PayBillActivity.IS_PRINT_BILL, true);
                    strComplimentaryReason = data.getStringExtra(PayBillActivity.COMPLIMENTARY_REASON);
                    dDiscPercent = data.getFloatExtra("DISCOUNT_PERCENTAGE", 0);
                    fCashPayment = data.getFloatExtra(PayBillActivity.TENDER_CASH_VALUE, 0);
                    fCardPayment = data.getFloatExtra(PayBillActivity.TENDER_CARD_VALUE, 0);
                    fCouponPayment = data.getFloatExtra(PayBillActivity.TENDER_COUPON_VALUE, 0);
                    fTotalDiscount = 0;
                    fTotalDiscount = data.getFloatExtra(PayBillActivity.DISCOUNT_AMOUNT, 0);

//                    dblPettCashPayment = data.getFloatExtra(PayBillActivity.TENDER_PETTYCASH_VALUE, 0);
                    dPettCashPayment = data.getDoubleExtra(PayBillActivity.TENDER_PETTYCASH_VALUE, 0);
                    fPaidTotalPayment = data.getFloatExtra(PayBillActivity.TENDER_PAIDTOTAL_VALUE, 0);
                    fWalletPayment = data.getFloatExtra(PayBillActivity.TENDER_WALLET_VALUE, 0);
                    fChangePayment = data.getDoubleExtra(PayBillActivity.TENDER_CHANGE_VALUE, 0);
                    fRoundOfValue = data.getDoubleExtra(PayBillActivity.TENDER_ROUNDOFF, 0);
                    dFinalBillValue = data.getDoubleExtra(PayBillActivity.TENDER_FINALBILL_VALUE, 0);

                    iCustId = data.getIntExtra("CUST_ID", 1);

                    if (isDiscounted == true) {
                        Log.v("PayBill Result", "Discounted:" + isDiscounted);
                        Log.v("PayBill Result", "Discount Amount:" + fTotalDiscount);
                        tvDiscountAmount.setText(String.valueOf(fTotalDiscount));
                        tvDiscountPercentage.setText(String.valueOf(dDiscPercent));
                        double igst = data.getDoubleExtra("TotalIGSTAmount",0);
                        double cgst = data.getDoubleExtra("TotalCGSTAmount",0);
                        double sgst = data.getDoubleExtra("TotalSGSTAmount",0);
                        double cess = data.getDoubleExtra("TotalcessAmount",0);
                        double billtot = data.getDoubleExtra("TotalBillAmount",0);
                        if(billtot >0)
                        {
                            tvBillAmount.setText(String.format("%.2f",billtot));
                            tvcessValue.setText(String.format("%.2f",cess));
                            if(chk_interstate.isChecked())
                            {
                                tvIGSTValue.setText(String.format("%.2f", igst));
                                tvCGSTValue.setText(String.format("%.2f", igst));
                                tvSGSTValue.setText("0.00");
                            }else {
                                tvCGSTValue.setText(String.format("%.2f", cgst));
                                tvSGSTValue.setText(String.format("%.2f", sgst));
                            }

                        }

                        ArrayList<AddedItemsToOrderTableClass> orderList_recieved = data.getParcelableArrayListExtra("OrderList");
                        double taxableValue =0.00;
                        for(int i =0;i<tblOrderItems.getChildCount();i++)
                        {
                            TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(i);

                            // Item Number
                            if (RowBillItem.getChildAt(0) != null) {
                                CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                                int menucode = (Integer.parseInt(ItemNumber.getText().toString()));
                                for(AddedItemsToOrderTableClass item : orderList_recieved) {
                                    if(item.getMenuCode() == menucode) {

                                        if (RowBillItem.getChildAt(9) != null ) {
                                            TextView DiscountAmount = (TextView) RowBillItem.getChildAt(9);
                                            DiscountAmount.setText(String.format("%.2f",item.getDiscountamount()));
                                        }
                                        // SGST
                                        if (RowBillItem.getChildAt(16) != null) {
                                            TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                                            ServiceTaxAmount.setText(String.format("%.2f",item.getSgstAmt()));
                                        }
                                        // CGST
                                        if (RowBillItem.getChildAt(7) != null) {
                                            TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                                            SalesTaxAmount.setText(String.format("%.2f",item.getCgstAmt()));
                                        }
                                        //IGST
                                        if (RowBillItem.getChildAt(24) != null) {
                                            TextView IAmount = (TextView) RowBillItem.getChildAt(24);
                                            IAmount.setText(String.format("%.2f",item.getIgstAmt()));
                                        }
                                        //cess
                                        if (RowBillItem.getChildAt(26) != null) {
                                            TextView cessAmount = (TextView) RowBillItem.getChildAt(26);
                                            cessAmount.setText(String.format("%.2f",item.getCessAmt()));
                                        }
                                        double quantity_dd =0.00;
                                        if (RowBillItem.getChildAt(3) != null ) {
                                            TextView qty = (TextView) RowBillItem.getChildAt(3);
                                            quantity_dd = qty.getText().toString().trim().equals("")?0.00:
                                                    Double.parseDouble(String.format("%.2f",Double.parseDouble(qty.getText().toString().trim())));
                                            //System.out.println("Richa :  quantity "+quantity_dd);
                                        }
                                        if (RowBillItem.getChildAt(5) != null) {
                                            TextView Amount = (TextView) RowBillItem.getChildAt(5);
                                            Amount.setText(String.format("%.2f",item.getTaxableValue()*quantity_dd));
                                            //System.out.println("Richa :  amt "+(item.getTaxableValue()*quantity_dd));
                                        }
                                        if (RowBillItem.getChildAt(28) != null) {
                                            TextView TaxableVal = (TextView) RowBillItem.getChildAt(28);
                                            TaxableVal.setText(String.format("%.2f",item.getTaxableValue()*quantity_dd));
                                            //System.out.println("Richa :  amt "+(item.getTaxableValue()*quantity_dd));
                                            taxableValue += item.getTaxableValue()*quantity_dd;
                                        }
                                    }
                                }
                            }
                        }
                        //OverAllDiscount(dDiscPercent);
                        tvSubTotal.setText(String.format("%.2f",taxableValue));
                    }
                    tvBillAmount.setText(String.format("%.2f",dFinalBillValue));
                    PrintBillPayment =0;
                    mSaveBillData(2);
                    generateInvoicePdf();
//                    dbBillScreen.updateKOTNo(iKOTNo-1);
                    Toast.makeText(myContext, "Bill saved Successfully", Toast.LENGTH_SHORT).show();
                    updateOutwardStock();
                    if (isComplimentaryBill == true) {
                        // Save complimentary bill details
                        SaveComplimentaryBill(Integer.parseInt(tvBillNumber.getText().toString()),
                                (fCashPayment + fCardPayment + fCouponPayment), strComplimentaryReason);
                    }
                    if (isPrintBill == true) {
                        strPaymentStatus = "Paid";
                        PrintNewBill(BUSINESS_DATE, 1);
                    }
                    if (jBillingMode == 1) {
                        int iResult = dbBillScreen.deleteKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                                Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));

                        ClearAll();
                    }
                    break;

                case 2: // Table Activity Result
                    /*if (TableActivity.TABLE_NO.equalsIgnoreCase("") && TableActivity.TABLE_SPLIT_NO.equalsIgnoreCase("")) {
                        Close(null);
                    } else if (TableActivity.TABLE_NO.equalsIgnoreCase("0") && TableActivity.WAITER_NO.equalsIgnoreCase("0")) {
                        MsgBox.Show("Warning", "Either Load KOT or Select Table and Waiter");
                    } else {
                        tvTableNumber.setText(data.getStringExtra(TableActivity.TABLE_NO));
                        tvWaiterNumber.setText(data.getStringExtra(TableActivity.WAITER_NO));
                        tvTableSplitNo.setText(data.getStringExtra(TableActivity.TABLE_SPLIT_NO));
                        //tvSubUdfValue.setText(data.getStringExtra(TableActivity.SUBUDF_NO));

                        if (tvTableNumber.getText().toString().equalsIgnoreCase("0") && tvWaiterNumber.getText().toString().equalsIgnoreCase("0")) {
                            MsgBox.Show("Warning", "Either Load KOT or Select Table and Waiter");
                        }

                        Cursor LoadKOT = dbBillScreen.getKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                                Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
                        if (LoadKOT.moveToFirst()) {
                            LoadKOTItems(LoadKOT);
                            btnPayBill.setEnabled(true);
                            btnPrintKOT.setEnabled(true);
                            btnPrintBill.setEnabled(true);
                        }
                    }*/
                    break;

                case 3: // Split bill
                    break;

                case 4: // Table Shift Merge (Load KOT)
                    String strTableNumber = data.getStringExtra("TABLE_NUMBER");
                    String strTableSplitNo = data.getStringExtra("TABLE_SPLIT_NO");
                    String strSubUdfNumber = data.getStringExtra("SUB_UDF_NUMBER");
                    Log.v("Load KOT", "TableNumber:" + strTableNumber + " Sub Udf:" + strSubUdfNumber);
                    Cursor LoadKOT = dbBillScreen.getKOTItems(Integer.parseInt(strTableNumber),
                            Integer.parseInt(strSubUdfNumber), Integer.parseInt(strTableSplitNo));
                    if (LoadKOT.moveToFirst()) {
                        iKOTNo = dbBillScreen.getMaxKOTNoForTablePendingKot(Integer.parseInt(strTableNumber), Integer.parseInt(strTableSplitNo));
                        LoadKOTItems(LoadKOT,false);
                        btnDeleteKOT.setEnabled(true);
                        btnPrintKOT.setEnabled(true);
                        btnPrintBill.setEnabled(true);
                        btnPayBill.setEnabled(true);

                    } else {
                        Log.v("Load KOT", "ERROR: No items found");
                    }
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            try {
                if (data.getBooleanExtra("isCancelled", false)) {
                    finish();
                }
            } catch (Exception e) {

            }
        }
    }

    /*************************************************************************************************************************************/


    private void loadAutoCompleteData() {

        // List - Get Item Name
        List<String> labelsItemName = dbBillScreen.getAllItemsName();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                labelsItemName);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // attaching data adapter to spinner
        aTViewSearchItem.setAdapter(dataAdapter);

        // List - Get Menu Code
        List<String> labelsMenuCode = dbBillScreen.getAllMenuCode();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                labelsMenuCode);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // attaching data adapter to spinner
        aTViewSearchMenuCode.setAdapter(dataAdapter1);

        POS_LIST = ArrayAdapter.createFromResource(this, R.array.poscode, android.R.layout.simple_spinner_item);
        spnr_pos.setAdapter(POS_LIST);

        // barcode

        List<String> labelsBarCode = dbBillScreen.getAllBarCode_fnb();
        ArrayAdapter<String> dataAdapter11 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                labelsBarCode);
        dataAdapter11.setDropDownViewResource(android.R.layout.simple_list_item_1);
        autoCompleteTextViewSearchItemBarcode.setAdapter(dataAdapter11);

    }


    public ArrayList<BillKotItem> kotPrint() {
        ArrayList<BillKotItem> billKotItems = new ArrayList<BillKotItem>();
        int count = 1;
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
            TableRow row = (TableRow) tblOrderItems.getChildAt(iRow);
            CheckBox itemId = (CheckBox) row.getChildAt(0);
            TextView itemName = (TextView) row.getChildAt(1);
            TextView HSNCode = (TextView) row.getChildAt(2);
            EditText itemQty = (EditText) row.getChildAt(3);
            EditText itemRate = (EditText) row.getChildAt(4);
            TextView itemAmount = (TextView) row.getChildAt(5);
            TextView printstatus = (TextView) row.getChildAt(21);
            TextView UOM_tv = (TextView) row.getChildAt(22);
            if (printstatus.getText().toString().equalsIgnoreCase("1")) {
                int id = Integer.parseInt(itemId.getText().toString().trim());
                int sno = count;
                String name = itemName.getText().toString().trim();
                String hsn = HSNCode.getText().toString().trim();
                String UOM = UOM_tv.getText().toString().trim();
                double qty = Double.parseDouble(itemQty.getText().toString().trim());
                double rate = Double.parseDouble(itemRate.getText().toString().trim());
                double amount = Double.parseDouble(itemAmount.getText().toString().trim());
                BillKotItem billKotItem = new BillKotItem(sno, name, qty, rate, amount,hsn, UOM,"");
                billKotItems.add(billKotItem);
                count++;
            }
        }
        return billKotItems;
    }

    public ArrayList<BillKotItem> billPreviewPrint(ArrayList<BillTaxSlab> billTaxSlabs) {
        ArrayList<BillKotItem> billKotItems = new ArrayList<BillKotItem>();
        int count = 1;
        AMOUNTPRINTINNEXTLINE =0;
        Date date1 = new Date();
        // Date
        /*String date_today = tvDate.getText().toString();
        try {
             date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date_today);
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/
        Cursor cursor = dbBillScreen.getPreviewBillItem();
        while(cursor !=null  && cursor.moveToNext()){

            int id = cursor.getInt(cursor.getColumnIndex("ItemNumber"));
            int sno = count;
            String name = cursor.getString(cursor.getColumnIndex("ItemName"));
            String hsncode = cursor.getString(cursor.getColumnIndex("HSNCode"));
            String UOM = cursor.getString(cursor.getColumnIndex("UOM"));
            double qty = Double.parseDouble(String.format("%.2f",cursor.getDouble(cursor.getColumnIndex("Quantity"))));
            double rate = Double.parseDouble(String.format("%.2f",cursor.getDouble(cursor.getColumnIndex("Value"))));
            double amount = 0;
           /* if(REVERSETAX)
                amount = originalRate *qty;
            else
                amount = Double.parseDouble(itemAmount.getText().toString().trim());*/

            amount = rate *qty;
            if(String.format("%.2f",amount).length()>8)
                AMOUNTPRINTINNEXTLINE = 1;
            String taxIndex = " ";
            double TaxRate =0;
            if(chk_interstate.isChecked())
                TaxRate = Double.parseDouble(String.format("%.2f",cursor.getDouble(cursor.getColumnIndex("IGSTRate"))));
            else
                TaxRate = Double.parseDouble(String.format("%.2f",cursor.getDouble(cursor.getColumnIndex("CGSTRate")) +
                        cursor.getDouble(cursor.getColumnIndex("SGSTRate"))));


            for (BillTaxSlab taxEntry : billTaxSlabs)
            {
                if(String.format("%.2f",TaxRate).equals(String.format("%.2f",taxEntry.getTaxRate())))
                {
                    taxIndex = taxEntry.getTaxIndex();
                    break;
                }
            }
            BillKotItem billKotItem = new BillKotItem(sno, name, qty, rate, amount, hsncode,UOM,taxIndex);
            billKotItems.add(billKotItem);
            count++;

        }
        return billKotItems;
    }

    public ArrayList<BillKotItem> billPrint(ArrayList<BillTaxSlab> billTaxSlabs) {
        ArrayList<BillKotItem> billKotItems = new ArrayList<BillKotItem>();
        int count = 1;
        AMOUNTPRINTINNEXTLINE =0;
        for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
            TableRow row = (TableRow) tblOrderItems.getChildAt(iRow);
            CheckBox itemId = (CheckBox) row.getChildAt(0);
            TextView itemName = (TextView) row.getChildAt(1);
            TextView HSNCode = (TextView) row.getChildAt(2);
            EditText itemQty = (EditText) row.getChildAt(3);
            EditText itemRate = (EditText) row.getChildAt(4);
            TextView itemAmount = (TextView) row.getChildAt(5);
            TextView printstatus = (TextView) row.getChildAt(21);
            TextView UOM_tv = (TextView) row.getChildAt(22);
            TextView IGST_tv = (TextView) row.getChildAt(23);
            TextView CGST_tv = (TextView) row.getChildAt(6);
            TextView SGST_tv = (TextView) row.getChildAt(15);
            TextView OriginalRate_tv = (TextView) row.getChildAt(27);

            int id = Integer.parseInt(itemId.getText().toString().trim());
            int sno = count;
            String name = itemName.getText().toString().trim();
            String hsncode = HSNCode.getText().toString().trim();
            String UOM = UOM_tv.getText().toString().trim();
            Double qty = Double.parseDouble(itemQty.getText().toString().trim());
            double rate = Double.parseDouble(itemRate.getText().toString().trim());
            double originalRate = Double.parseDouble(
                    OriginalRate_tv.getText().toString().trim().equals("")?"0": OriginalRate_tv.getText().toString().trim());
            double amount = 0;
           /* if(REVERSETAX)
                amount = originalRate *qty;
            else
                amount = Double.parseDouble(itemAmount.getText().toString().trim());*/

            amount = originalRate *qty;
            if(String.format("%.2f",amount).length()>8)
                AMOUNTPRINTINNEXTLINE = 1;
            String taxIndex = " ";
            double TaxRate =0;
            if(chk_interstate.isChecked())
                TaxRate = Double.parseDouble(IGST_tv.getText().toString().trim());
            else
                TaxRate = Double.parseDouble(CGST_tv.getText().toString().trim()) + Double.parseDouble(SGST_tv.getText().toString().trim());


            for (BillTaxSlab taxEntry : billTaxSlabs)
            {
                if(String.format("%.2f",TaxRate).equals(String.format("%.2f",taxEntry.getTaxRate())))
                {
                    taxIndex = taxEntry.getTaxIndex();
                    break;
                }
            }
            BillKotItem billKotItem = new BillKotItem(sno, name, qty, rate, amount, hsncode,UOM,taxIndex);
            billKotItems.add(billKotItem);
            count++;

        }
        return billKotItems;
    }



    public ArrayList<BillTaxSlab> TaxSlabPrint_IntraState() {
        ArrayList<BillTaxSlab> billTaxSlabs = new ArrayList<BillTaxSlab>();

        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForTaxSlabPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            int count = 0;
            //System.out.println(crsrTax.getCount());
            if (crsrTax.moveToFirst()) {
                do {
                    Double taxpercent = Double.parseDouble(String.format("%.2f",(crsrTax.getDouble(crsrTax.getColumnIndex("CGSTRate")) +
                            crsrTax.getDouble(crsrTax.getColumnIndex("SGSTRate")))));

                    Double cgstamt  = Double.parseDouble(String.format("%.2f",
                            crsrTax.getDouble(crsrTax.getColumnIndex("CGSTAmount"))));
                    Double sgstamt  = Double.parseDouble(String.format("%.2f",
                            crsrTax.getDouble(crsrTax.getColumnIndex("SGSTAmount"))));
                    Double taxableValue  = Double.parseDouble(String.format("%.2f",
                            crsrTax.getDouble(crsrTax.getColumnIndex("TaxableValue"))));
                    double cc = crsrTax.getDouble(crsrTax.getColumnIndex("TaxableValue"));

                    if (taxpercent == 0)
                        continue;
                    BillTaxSlab taxItem = new BillTaxSlab("",taxpercent, 0.00,cgstamt,sgstamt, taxableValue,cgstamt+sgstamt);
                    int found =0;
                    for (BillTaxSlab taxSlabItem : billTaxSlabs )
                    {
                        if (taxSlabItem.getTaxRate() == taxpercent)
                        {
                            taxSlabItem.setCGSTAmount(taxSlabItem.getCGSTAmount()+cgstamt);
                            taxSlabItem.setSGSTAmount(taxSlabItem.getSGSTAmount()+sgstamt);
                            taxSlabItem.setTaxableValue(taxSlabItem.getTaxableValue()+taxableValue);
                            taxSlabItem.setTotalTaxAmount(taxSlabItem.getTotalTaxAmount()+cgstamt+sgstamt);
                            found =1;
                            break;
                        }
                    }
                    if(found == 0){
                        taxItem.setTaxIndex(Character.toString((char)('A'+count)));
                        count++;
                        billTaxSlabs.add(taxItem);
                    }

                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxSlabs = new ArrayList<BillTaxSlab>();
        }
        finally
        {
            return billTaxSlabs;
        }

    }
    public ArrayList<BillTaxSlab> TaxSlabPrint_InterState() {
        ArrayList<BillTaxSlab> billTaxSlabs = new ArrayList<BillTaxSlab>();

        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForTaxSlabPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            int count = 0;
            //System.out.println(crsrTax.getCount());
            if (crsrTax.moveToFirst()) {
                do {
                    Double taxpercent = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("IGSTRate"))));
                    Double igstamt  = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("IGSTAmount"))));
                    Double taxableValue  = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("TaxableValue"))));

                    if (taxpercent == 0)
                        continue;
                    BillTaxSlab taxItem = new BillTaxSlab("",taxpercent, igstamt,0.00,0.00, taxableValue,igstamt);
                    int found =0;
                    for (BillTaxSlab taxSlabItem : billTaxSlabs )
                    {
                        if (taxSlabItem.getTaxRate() == taxpercent)
                        {
                            taxSlabItem.setIGSTAmount(taxSlabItem.getIGSTAmount()+igstamt);
                            taxSlabItem.setTaxableValue(taxSlabItem.getTaxableValue()+taxableValue);
                            taxSlabItem.setTotalTaxAmount(taxSlabItem.getTotalTaxAmount()+igstamt);
                            found =1;
                            break;
                        }
                    }
                    if(found == 0){
                        taxItem.setTaxIndex(Character.toString((char)('A'+count)));
                        count++;
                        billTaxSlabs.add(taxItem);
                    }

                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxSlabs = new ArrayList<BillTaxSlab>();
        }
        finally
        {
            return billTaxSlabs;
        }

    }

    public ArrayList<BillTaxSlab> TaxSlabPreviewPrint_InterState() {
        ArrayList<BillTaxSlab> billTaxSlabs = new ArrayList<BillTaxSlab>();

        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForTaxSlabPreviewPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            int count = 0;
            //System.out.println(crsrTax.getCount());
            if (crsrTax.moveToFirst()) {
                do {
                    Double taxpercent = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("IGSTRate"))));
                    Double igstamt  = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("IGSTAmount"))));
                    Double taxableValue  = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("TaxableValue"))));

                    if (taxpercent == 0)
                        continue;
                    BillTaxSlab taxItem = new BillTaxSlab("",taxpercent, igstamt,0.00,0.00, taxableValue,igstamt);
                    int found =0;
                    for (BillTaxSlab taxSlabItem : billTaxSlabs )
                    {
                        if (taxSlabItem.getTaxRate() == taxpercent)
                        {
                            taxSlabItem.setIGSTAmount(taxSlabItem.getIGSTAmount()+igstamt);
                            taxSlabItem.setTaxableValue(taxSlabItem.getTaxableValue()+taxableValue);
                            taxSlabItem.setTotalTaxAmount(taxSlabItem.getTotalTaxAmount()+igstamt);
                            found =1;
                            break;
                        }
                    }
                    if(found == 0){
                        taxItem.setTaxIndex(Character.toString((char)('A'+count)));
                        count++;
                        billTaxSlabs.add(taxItem);
                    }

                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxSlabs = new ArrayList<BillTaxSlab>();
        }
        finally
        {
            return billTaxSlabs;
        }

    }

    public ArrayList<BillTaxSlab> TaxSlabPreviewPrint_IntraState() {
        ArrayList<BillTaxSlab> billTaxSlabs = new ArrayList<BillTaxSlab>();

        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForTaxSlabPreviewPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            int count = 0;
            //System.out.println(crsrTax.getCount());
            if (crsrTax.moveToFirst()) {
                do {
                    Double taxpercent = Double.parseDouble(String.format("%.2f",(crsrTax.getDouble(crsrTax.getColumnIndex("CGSTRate")) +
                            crsrTax.getDouble(crsrTax.getColumnIndex("SGSTRate")))));

                    Double cgstamt  = Double.parseDouble(String.format("%.2f",
                            crsrTax.getDouble(crsrTax.getColumnIndex("CGSTAmount"))));
                    Double sgstamt  = Double.parseDouble(String.format("%.2f",
                            crsrTax.getDouble(crsrTax.getColumnIndex("SGSTAmount"))));
                    Double taxableValue  = Double.parseDouble(String.format("%.2f",
                            crsrTax.getDouble(crsrTax.getColumnIndex("TaxableValue"))));
                    double cc = crsrTax.getDouble(crsrTax.getColumnIndex("TaxableValue"));

                    if (taxpercent == 0)
                        continue;
                    BillTaxSlab taxItem = new BillTaxSlab("",taxpercent, 0.00,cgstamt,sgstamt, taxableValue,cgstamt+sgstamt);
                    int found =0;
                    for (BillTaxSlab taxSlabItem : billTaxSlabs )
                    {
                        if (taxSlabItem.getTaxRate() == taxpercent)
                        {
                            taxSlabItem.setCGSTAmount(taxSlabItem.getCGSTAmount()+cgstamt);
                            taxSlabItem.setSGSTAmount(taxSlabItem.getSGSTAmount()+sgstamt);
                            taxSlabItem.setTaxableValue(taxSlabItem.getTaxableValue()+taxableValue);
                            taxSlabItem.setTotalTaxAmount(taxSlabItem.getTotalTaxAmount()+cgstamt+sgstamt);
                            found =1;
                            break;
                        }
                    }
                    if(found == 0){
                        taxItem.setTaxIndex(Character.toString((char)('A'+count)));
                        count++;
                        billTaxSlabs.add(taxItem);
                    }

                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxSlabs = new ArrayList<BillTaxSlab>();
        }
        finally
        {
            return billTaxSlabs;
        }

    }

    public ArrayList<BillServiceTaxItem> cessTaxPrint() {
        ArrayList<BillServiceTaxItem> billcessTaxItems = new ArrayList<BillServiceTaxItem>();
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForcessTaxPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = "cess "; //crsrTax.getString(crsrTax.getColumnIndex("TaxDescription"));
                    String taxpercent = String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("cessRate")));
                    Double taxvalue = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("cessAmount"))));

                    BillServiceTaxItem taxItem = new BillServiceTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billcessTaxItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billcessTaxItems = new ArrayList<BillServiceTaxItem>();
        }
        finally
        {
            return billcessTaxItems;
        }

    } public ArrayList<BillServiceTaxItem> cessTaxPreviewPrint() {
        ArrayList<BillServiceTaxItem> billcessTaxItems = new ArrayList<BillServiceTaxItem>();
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForcessTaxPreviewPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = "cess "; //crsrTax.getString(crsrTax.getColumnIndex("TaxDescription"));
                    String taxpercent = String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("cessRate")));
                    Double taxvalue = Double.parseDouble(String.format("%.2f",crsrTax.getDouble(crsrTax.getColumnIndex("cessAmount"))));

                    BillServiceTaxItem taxItem = new BillServiceTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billcessTaxItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billcessTaxItems = new ArrayList<BillServiceTaxItem>();
        }
        finally
        {
            return billcessTaxItems;
        }

    }
    public ArrayList<BillTaxItem> taxPrint() {
        ArrayList<BillTaxItem> billTaxItems = new ArrayList<BillTaxItem>();
        try {

            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForCGSTTaxPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = "CGST "; //crsrTax.getString(crsrTax.getColumnIndex("TaxDescription"));
                    String taxpercent = crsrTax.getString(crsrTax.getColumnIndex("CGSTRate"));
                    Double taxvalue = Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("CGSTAmount")));

                    BillTaxItem taxItem = new BillTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billTaxItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxItems = new ArrayList<BillTaxItem>();
        }
        finally
        {
            return billTaxItems;
        }
    }
    public ArrayList<BillTaxItem> taxPrint_IGST() {
        ArrayList<BillTaxItem> billTaxItems = new ArrayList<BillTaxItem>();
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForIGSTTaxPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = "IGST "; //crsrTax.getString(crsrTax.getColumnIndex("TaxDescription"));
                    String taxpercent = crsrTax.getString(crsrTax.getColumnIndex("IGSTRate"));
                    Double taxvalue = Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("IGSTAmount")));

                    BillTaxItem taxItem = new BillTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billTaxItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billTaxItems = new ArrayList<BillTaxItem>();
        }
        finally
        {
            return billTaxItems;
        }
    }
    public ArrayList<BillTaxItem> otherChargesPrint() {
        ArrayList<BillTaxItem> billOtherChargesItems = new ArrayList<BillTaxItem>();

        if(isReprint)
        {
            Cursor crsrTax = dbBillScreen.getBillDetail(Integer.parseInt(tvBillNumber.getText().toString()));
            if(crsrTax.moveToFirst())
            {
                String taxname = "OtherCharges";
                double taxpercent = 0;
                Double taxvalue = crsrTax.getDouble(crsrTax.getColumnIndex("DeliveryCharge"));

                if(taxvalue>0)
                {
                    BillTaxItem taxItem = new BillTaxItem(taxname, (taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billOtherChargesItems.add(taxItem);
                    /*double totalamt = Double.parseDouble(tvBillAmount.getText().toString().trim());
                    totalamt+= taxvalue;
                    tvBillAmount.setText(String.format("%.2f", totalamt));*/
                }
            }

        }else
        { // fresh print
            Cursor crsrTax = dbBillScreen.getItemsForOtherChargesPrint(DineInCaption);
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = crsrTax.getString(crsrTax.getColumnIndex("ModifierDescription"));
                    String taxpercent = "0";
                    Double taxvalue = Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("ModifierAmount")));

                    BillTaxItem taxItem = new BillTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billOtherChargesItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }
        return billOtherChargesItems;
    }

    public ArrayList<BillServiceTaxItem> servicetaxPrint() {
        ArrayList<BillServiceTaxItem> billServiceTaxItems = new ArrayList<BillServiceTaxItem>();

        Cursor crsrTax = dbBillScreen.getItemsForServiceTaxPrint(Integer.valueOf(tvBillNumber.getText().toString()));
        if (crsrTax.moveToFirst()) {
            //do {
            BillServiceTaxItem ServicetaxItem = new BillServiceTaxItem("Service Tax", Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("ServiceTaxPercent"))), Double.parseDouble(String.format("%.2f", Double.parseDouble(tvSGSTValue.getText().toString()))));
            billServiceTaxItems.add(ServicetaxItem);
            //} while (crsrTax.moveToNext());
        }
        return billServiceTaxItems;
    }

    public ArrayList<BillSubTaxItem> subtaxPrint() {
        ArrayList<BillSubTaxItem> billSubTaxItems = new ArrayList<BillSubTaxItem>();
        Cursor crsrSubTax = dbBillScreen.getAllSubTaxConfig("2");
        if (crsrSubTax.moveToFirst()) {
            do {
                String subtaxname = crsrSubTax.getString(crsrSubTax.getColumnIndex("SubTaxDescription"));
                String subtaxpercent = crsrSubTax.getString(crsrSubTax.getColumnIndex("SubTaxPercent"));
                double subtaxvalue = Double.parseDouble(tvSubTotal.getText().toString().trim()) * (Double.parseDouble(subtaxpercent) / 100);

                BillSubTaxItem taxSubItem = new BillSubTaxItem(subtaxname, Double.parseDouble(subtaxpercent), Double.parseDouble(String.format("%.2f", subtaxvalue)));
                billSubTaxItems.add(taxSubItem);

                fTotalsubTaxPercent += Float.parseFloat(subtaxpercent);

            } while (crsrSubTax.moveToNext());
        }
        return billSubTaxItems;
    }

    public ArrayList<BillServiceTaxItem> SGSTtaxPrint() {
        ArrayList<BillServiceTaxItem> billServiceTaxItems = new ArrayList<BillServiceTaxItem>();
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
            Cursor crsrTax = db.getItemsForSGSTTaxPrints(Integer.valueOf(tvBillNumber.getText().toString()), String.valueOf(date.getTime()));
            if (crsrTax.moveToFirst()) {
                do {
                    String taxname = "SGST "; //crsrTax.getString(crsrTax.getColumnIndex("TaxDescription"));
                    String taxpercent = crsrTax.getString(crsrTax.getColumnIndex("SGSTRate"));
                    Double taxvalue = Double.parseDouble(crsrTax.getString(crsrTax.getColumnIndex("SGSTAmount")));

                    BillServiceTaxItem taxItem = new BillServiceTaxItem(taxname, Double.parseDouble(taxpercent), Double.parseDouble(String.format("%.2f", taxvalue)));
                    billServiceTaxItems.add(taxItem);
                } while (crsrTax.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            billServiceTaxItems = new ArrayList<BillServiceTaxItem>();
        }
        finally
        {
            return billServiceTaxItems;
        }
    }


    public void printKOT(View view) {
        int proceed = 1;

        if (tblOrderItems.getChildCount() < 1){
            MsgBox.Show("Warning", "Add Item before Saving KOT");
            proceed =0;
        }

        if(proceed == 1)
        {
//            if (isPrinterAvailable) {
                iPrintKOTStatus = 0;
                int i = 0;
//                if (REPRINT_KOT == 0) {
                i = SaveKOT();
//                }
                if(i == 0) // print only when KOT is saved successfully
                {
                    return;
                }
                PrintKOT();
//                REPRINT_KOT = 0;
//                if (REPRINT_KOT == 0) {
                tblOrderItems.removeAllViews();
                Cursor LoadKOT = dbBillScreen.getKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                        Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
                if (LoadKOT.moveToFirst()) {
                    LoadKOTItems(LoadKOT,true);
                }
//                }
           /* } else {
                Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
                askForConfig();
            }*/
        }
    }

    protected void PrintKOT() {
//        if (isPrinterAvailable) {
            if (tblOrderItems.getChildCount() < 1) {
                MsgBox.Show("Warning", "Insert item before Print KOT");
                return;
            } else {
                int tableId = 0, waiterId = 0, orderId = 0;
                String waiterName;
                if ((!tvTableNumber.getText().toString().trim().equalsIgnoreCase(""))
                        && (!tvWaiterNumber.getText().toString().trim().equalsIgnoreCase(""))
                        && (!tvBillNumber.getText().toString().trim().equalsIgnoreCase("")
                        && (!tvWaiterName.getText().toString().trim().equalsIgnoreCase("")))
                        ) {
                    tableId = Integer.parseInt(tvTableNumber.getText().toString().trim());
                    waiterId = Integer.parseInt(tvWaiterNumber.getText().toString().trim());
                    waiterName = tvWaiterName.getText().toString().trim();
                    String splitNo = tvTableSplitNo.getText().toString();
                    orderId = Integer.parseInt(tvBillNumber.getText().toString().trim());
                    ArrayList<BillKotItem> billKotItems = kotPrint();
                    if(billKotItems.size()<1)
                    {
                        MsgBox.Show("Oops", "No new item in KOT to print");
                        return;
                    }
                    PrintKotBillItem item = new PrintKotBillItem();
                    item.setBillKotItems(billKotItems);
                    item.setTableNo(String.valueOf(tableId) +"-"+splitNo );
                    item.setWaiterNo(waiterId);
                    item.setWaiterName(waiterName);
                    item.setBillNo(String.valueOf(iKOTNo));
                    item.setOrderBy(strUserName);
                    item.setBillingMode(String.valueOf(jBillingMode));
                    item.setDate(tvDate.getText().toString());
                    String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());
                    item.setTime(strTime);
                    /*item.setDate(TimeUtil.getDate());
                    item.setTime(TimeUtil.getTime());*/
            /*Intent intent = new Intent(getApplicationContext(), PrinterSohamsaActivity.class);
            intent.putExtra("printType", "KOT");
            intent.putExtra("printData", item);
            startActivity(intent);*/
                    String prf = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString("kot", "--Select--");
                    Intent intent = null;
                    if (prf.equalsIgnoreCase("Sohamsa")) {
                    /*intent = new Intent(getApplicationContext(), PrinterSohamsaActivity.class);
                    intent.putExtra("printType", "KOT");
                    intent.putExtra("printData", item);
                    startActivity(intent);*/
                        //printSohamsa(item, "KOT");
                        Toast.makeText(myContext, "KOT is printing... ", Toast.LENGTH_LONG).show();
                    } else if (prf.equalsIgnoreCase("Heyday")) {
                    /*intent = new Intent(getApplicationContext(), PrinterFragment.class);
                    intent.putExtra("printType", "KOT");
                    intent.putExtra("printData", item);
                    startActivity(intent);*/
                        if (isPrinterAvailable) {
                            printHeydeyKOT(item, "KOT");
                        } else {
//                            askForConfig();
                            Toast.makeText(BillingDineInActivity.this, "Printer is not available.", Toast.LENGTH_SHORT).show();
                        }
                        /*if (!isKOTReprint) {
                            dbBillScreen.updateKOTNo(iKOTNo);
                        }*/
//                            iKOTNo += 1;
                        Toast.makeText(myContext, "KOT is printing... ", Toast.LENGTH_LONG).show();
                        isKOTReprint = false;
                    } else if(prf.equalsIgnoreCase(Constants.USB_WEP_PRINTER_NAME)) {

                        String target = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(prf, "--Select--");

                        WePTHPrinterBaseActivity wepPrinter = new WePTHPrinterBaseActivity();

                        wepPrinter.setmTarget(target);
                        wepPrinter.setmContext(this);
                        wepPrinter.mInitListener(this);

                        if (wepPrinter.runPrintKOTSequence(item, 1)) {
                            Toast.makeText(this, "KOT is printing....", Toast.LENGTH_SHORT).show();
                        }

                    } else if(prf.equalsIgnoreCase(Constants.USB_EPSON_PRINTER_NAME)) {

                        String target = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(prf, "--Select--");

                        EPSONPrinterBaseActivity epsonPrinter = new EPSONPrinterBaseActivity();

                        epsonPrinter.setmTarget(target);
                        epsonPrinter.setmContext(this);
                        epsonPrinter.mInitListener(this);

                        if (epsonPrinter.runPrintKOTSequence(item)) {
                            Toast.makeText(this, "KOT is printing....", Toast.LENGTH_SHORT).show();
                        }

                    } else if(prf.equalsIgnoreCase(Constants.USB_BIXOLON_PRINTER_NAME)) {

                        String target = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(prf, "--Select--");

                        BixolonPrinterBaseAcivity bixolonPrinter = new BixolonPrinterBaseAcivity();

                        bixolonPrinter.setmTarget(target);
                        bixolonPrinter.setmContext(this);
                        bixolonPrinter.mInitListener(this);

                        if (bixolonPrinter.runPrintKOTSequence(item)) {
                            Toast.makeText(this, "KOT is printing....", Toast.LENGTH_SHORT).show();
                        }

                    } else if(prf.equalsIgnoreCase(Constants.USB_TVS_PRINTER_NAME)) {

                        String target = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(prf, "--Select--");

                        TVSPrinterBaseActivity tvsPrinter = new TVSPrinterBaseActivity();

                        tvsPrinter.setmTarget(target);
                        tvsPrinter.setmContext(this);
                        tvsPrinter.mInitListener(this);

                        if (tvsPrinter.runPrintKOTSequence(item)) {
                            Toast.makeText(this, "KOT is printing....", Toast.LENGTH_SHORT).show();
                        }

                    } else if (prf.equalsIgnoreCase("NGX")) {

                        if (isPrinterAvailable) {
                            printNGXKOT(item);
                        } else {
                            Toast.makeText(BillingDineInActivity.this, "Printer is not available.", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(myContext, "KOT is printing... ", Toast.LENGTH_LONG).show();
                        isKOTReprint = false;
                    }  else {
                        Toast.makeText(myContext, "Printer not configured. Kindly goto settings and configure printer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BillingDineInActivity.this, "Please enter bill,waiter,table number", Toast.LENGTH_SHORT).show();
                }
            }
       /* } else {
            Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
            askForConfig();
        }*/
    }

    public void previewBill(View view){
        int proceed =1;
        if (tblOrderItems.getChildCount() < 1){
            MsgBox.Show("Warning", "Add Item before Printing Bill");
            proceed =0;
        } else if (tvBillAmount.getText().toString().equals("") ) {
            MsgBox.Show("Warning", "Please add item to make bill");
            proceed =0;
        } else if ( tvBillAmount.getText().toString().equals("0.00")) {
            MsgBox.Show("Warning", "Please make bill of amount greater than 0.00");
            proceed =0;
        }else if (chk_interstate.isChecked() && spnr_pos.getSelectedItem().equals("")) {
            MsgBox.Show("Warning", "Please Select Code for Intersate Supply");
            proceed =0;
        }

        if(proceed == 0)
            return;
//        if (isPrinterAvailable) {
            strPaymentStatus = "Paid";
            PrintBillPayment = 1;

            // Print Bill with Save Bill
            if (tblOrderItems.getChildCount() < 1) {
                MsgBox.Show("Warning", "Insert item before Print Bill");
                return;
            } else {

                lPreviewBill(2, true);
                PrintPreviewBill();
//                Toast.makeText(myContext, "Bill Saved Successfully", Toast.LENGTH_SHORT).show();
            }
//        } else {
//            Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
//            askForConfig();
//        }
    }

    public void printBILL(View view) {
        int proceed =1;
        if (tblOrderItems.getChildCount() < 1){
            MsgBox.Show("Warning", "Add Item before Printing Bill");
            proceed =0;
        } else if (tvBillAmount.getText().toString().equals("") ) {
            MsgBox.Show("Warning", "Please add item to make bill");
            proceed =0;
        } else if ( tvBillAmount.getText().toString().equals("0.00")) {
            MsgBox.Show("Warning", "Please make bill of amount greater than 0.00");
            proceed =0;
        }else if (chk_interstate.isChecked() && spnr_pos.getSelectedItem().equals("")) {
            MsgBox.Show("Warning", "Please Select Code for Intersate Supply");
            proceed =0;
        }
        /*else if (jBillingMode==4 )
        {
            String tempCustId = edtCustId.getText().toString();
            if (tempCustId.equalsIgnoreCase("") || tempCustId.equalsIgnoreCase("0")) {
                MsgBox.Show("Warning", "Please Select Customer for Billing");
                proceed =0;
            }
        }*/
        if(proceed == 0)
            return;
//        if (isPrinterAvailable) {
            strPaymentStatus = "Paid";
            PrintBillPayment = 1;

            // Print Bill with Save Bill
            if (tblOrderItems.getChildCount() < 1) {
                MsgBox.Show("Warning", "Insert item before Print Bill");
                return;
            } else {

                mSaveBillData(2);
                generateInvoicePdf();
                updateOutwardStock();
                PrintNewBill(BUSINESS_DATE, 1);
                //dbBillScreen.updateKOTNo(iKOTNo-1);
                Toast.makeText(myContext, "Bill Saved Successfully", Toast.LENGTH_SHORT).show();
                if (jBillingMode == 1) {
                    int iResult = dbBillScreen.deleteKOTItems(Integer.parseInt(tvTableNumber.getText().toString()),
                            Integer.parseInt(tvSubUdfValue.getText().toString()), Integer.parseInt(tvTableSplitNo.getText().toString()));
                    //Log.d("Dine In:TenderActivity Result", "Items deleted from pending KOT:" + iResult);
                    ClearAll();

                }
            }
//        } else {
//            Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
//            askForConfig();
//        }
    }

    private void generateInvoicePdf() {
        try {

            PdfInvoiceBean pdfItem = null;
            String[] arrayPOS = getResources().getStringArray(R.array.poscode);

            if (tblOrderItems.getChildCount() < 1) {
                MsgBox.Show("Warning", "Insert item before Print Bill");
                return;
            } else {
                String orderId = "";
                if ((!tvBillAmount.getText().toString().trim().equalsIgnoreCase(""))) {

                  /*  if (trainingMode)
                        orderId = edtTMBillNoPrefix.getText().toString().trim() + edtBillNumber.getText().toString().trim();
                    else*/
                    orderId = tvBillNumber.getText().toString().trim();

                    pdfItem = new PdfInvoiceBean();

                    pdfItem.setInvoiceNo(orderId);
                    pdfItem.setInvoiceDate(BUSINESS_DATE);
                    pdfItem.setBillAmountRoundOff(BILLAMOUNTROUNDOFF);
                    if (isForwardTaxEnabled == 0)
                        pdfItem.setReverseTax(true);
                    else
                        pdfItem.setReverseTax(false);
                    if (trainingMode)
                        pdfItem.setTrainingMode(true);
                    else
                        pdfItem.setTrainingMode(false);

                    if (JURISDICTIONS_PRINT_STATUS == 1 && strJurisdictionsPrint != null)
                        pdfItem.setStrJurisdictionsPrint(strJurisdictionsPrint);
                    else
                        pdfItem.setStrJurisdictionsPrint("");

                    Cursor crsrHeaderFooterSetting = null;
                    try {
                        crsrHeaderFooterSetting = db.getBillSettings();

                        if (crsrHeaderFooterSetting.moveToFirst()) {
                            pdfItem.setHeaderLine1(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText1")));
                            pdfItem.setHeaderLine2(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText2")));
                            pdfItem.setHeaderLine3(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText3")));
                            pdfItem.setHeaderLine4(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText4")));
                            pdfItem.setHeaderLine5(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText5")));
                            pdfItem.setFooterLine1(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText1")));
                            pdfItem.setFooterLine2(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText2")));
                            pdfItem.setFooterLine3(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText3")));
                            pdfItem.setFooterLine4(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText4")));
                            pdfItem.setFooterLine5(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText5")));
                        } else {
                            Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Unable to fetch header details from billSettings table. From method PrintNewBill()." + ex.getMessage());
                    } finally {
                        if (crsrHeaderFooterSetting != null) {
                            crsrHeaderFooterSetting.close();
                        }
                    }

                    Cursor crsrOwnerDetails = null;
                    try {
                        crsrOwnerDetails = db.getOwnerDetail();

                        if (crsrOwnerDetails.moveToFirst()) {
                            try {
                                pdfItem.setOwnerStateCode(db.getOwnerPOS());
                                pdfItem.setOwnerGstin(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                                String ownerPos = "";
                                for (int i = 0; i < arrayPOS.length; i++) {
                                    if (arrayPOS[i].contains(pdfItem.getOwnerStateCode()))
                                        ownerPos = arrayPOS[i];
                                }
                                ownerPos = ownerPos.substring(0, ownerPos.length() - 2);
                                pdfItem.setOwnerPos(ownerPos);
                                try {
                                    pdfItem.setCompanyLogoPath(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)));
                                } catch (Exception ex) {
                                    pdfItem.setCompanyLogoPath(null);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Unable to fetch data from owner details data from table." + ex.getMessage());
                    } finally {
                        if (crsrOwnerDetails != null) {
                            crsrOwnerDetails.close();
                        }
                    }

                    Cursor crsrCustomer = null;
                    try {
                        String custid = edtCustId.getText().toString();
                        if (!custid.isEmpty()) {
                            crsrCustomer = db.getCustomer(Integer.parseInt(custid));
                            if (crsrCustomer.moveToFirst()) {

                                pdfItem.setCustomerName(crsrCustomer.getString(crsrCustomer.getColumnIndex(DatabaseHandler.KEY_CustName)));
                                pdfItem.setCustomerAddress(crsrCustomer.getString(crsrCustomer.getColumnIndex(DatabaseHandler.KEY_CustAddress)));
                                pdfItem.setCustomerGstin(crsrCustomer.getString(crsrCustomer.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                                String customerStateCode = pdfItem.getCustomerGstin().substring(0, 2);
                                String customerPos = "";
                                for (int i = 0; i < arrayPOS.length; i++) {
                                    if (arrayPOS[i].contains(customerStateCode))
                                        customerPos = arrayPOS[i];
                                }
                                customerPos = customerPos.substring(0, customerPos.length() - 2);
                                pdfItem.setCustomerState(customerPos);

                            } else {
                                pdfItem.setCustomerName(" - - - ");
                                pdfItem.setCustomerAddress("");
                                pdfItem.setCustomerGstin("");
                                pdfItem.setCustomerState("");
                            }
                        } else {
                            pdfItem.setCustomerName(" - - - ");
                            pdfItem.setCustomerAddress("");
                            pdfItem.setCustomerGstin("");
                            pdfItem.setCustomerState("");
                        }

                    } catch (Exception ex) {
                        Log.e(TAG, "Unable to fetch data from the customer table on PrintNewBill()." + ex.getMessage());
                    } finally {
                        if (crsrCustomer != null) {
                            crsrCustomer.close();
                        }
                    }

                    ArrayList<PdfItemBean> pdfItemBeanArrayList = new ArrayList<>();
                    PdfItemBean pdfItemBean;

                    for (int iRow = 0; iRow < tblOrderItems.getChildCount(); iRow++) {
                        pdfItemBean = new PdfItemBean();

                        TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(iRow);

                        // Increment Total item count if row is not empty
                        if (RowBillItem.getChildCount() > 0) {
                            iTotalItems++;
                        }

                        // Item Number
                        if (RowBillItem.getChildAt(0) != null) {
                            CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                            pdfItemBean.setItemId(Integer.parseInt(ItemNumber.getText().toString()));
                        }

                      /*  if(ITEM_LONG_NAME_PRINT_IN_BILL == 1){
                            pdfItemBean.setItemName(billItemBean.getStrItemLongName());
                        } else {
                            pdfItemBean.setItemName(billItemBean.getStrItemName());
                        }*/

                        // Item Name
                        if (RowBillItem.getChildAt(1) != null) {
                            TextView ItemName = (TextView) RowBillItem.getChildAt(1);
                            pdfItemBean.setItemName(ItemName.getText().toString());
                        }

                        // HSN Code
                        if (RowBillItem.getChildAt(2) != null) {
                            TextView HSN = (TextView) RowBillItem.getChildAt(2);
                            pdfItemBean.setHSNCode(HSN.getText().toString());
                        }

                        // Quantity
                        double qty_d = 0.00;
                        if (RowBillItem.getChildAt(3) != null) {
                            EditText Quantity = (EditText) RowBillItem.getChildAt(3);
                            String qty_str = Quantity.getText().toString();
                            if(qty_str==null || qty_str.equals(""))
                            {
                                Quantity.setText("0.00");
                            }else
                            {
                                qty_d = Double.parseDouble(qty_str);
                            }
                            pdfItemBean.setQty(Double.parseDouble(String.format("%.2f",qty_d)));
                        }

                        // UOM
                        if (RowBillItem.getChildAt(22) != null) {
                            TextView UOM = (TextView) RowBillItem.getChildAt(22);
                            pdfItemBean.setUOM(UOM.getText().toString());
                        }

                        // Rate
                        double rate_d = 0.00;
                        if (RowBillItem.getChildAt(4) != null) {
                            EditText Rate = (EditText) RowBillItem.getChildAt(4);
                            String rate_str = Rate.getText().toString();
                            if((rate_str==null || rate_str.equals("")))
                            {
                                Rate.setText("0.00");
                            }else
                            {
                                rate_d = Double.parseDouble(rate_str);
                            }
                            pdfItemBean.setValue(Double.parseDouble(String.format("%.2f",rate_d)));
                        }

                        // Original Rate
                        if (RowBillItem.getChildAt(27) != null) {
                            TextView originalRate = (TextView) RowBillItem.getChildAt(27);
                            pdfItemBean.setRetailPrice(Double.parseDouble(originalRate.getText().toString()));
                            pdfItemBean.setMrp(Double.parseDouble(originalRate.getText().toString()));
                        }

                        // Taxable Value
                        if (RowBillItem.getChildAt(28) != null) {
                            TextView TaxableValue = (TextView) RowBillItem.getChildAt(28);
                            pdfItemBean.setTaxableValue(Double.parseDouble(TaxableValue.getText().toString()));
                        }

                        // Discount Amount
                        if (RowBillItem.getChildAt(9) != null) {
                            TextView DiscountAmount = (TextView) RowBillItem.getChildAt(9);
                            pdfItemBean.setDiscAmount(Double.parseDouble(DiscountAmount.getText().toString()));
                        }

                        // SGST
                        double sgstAmt = 0;
                        if (RowBillItem.getChildAt(16) != null) {
                            TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                            sgstAmt = Double.parseDouble(ServiceTaxAmount.getText().toString());
                            if (chk_interstate.isChecked()) {
                                pdfItemBean.setSgstAmount(0.00);
                            } else {
                                pdfItemBean.setSgstAmount(Double.parseDouble(String.format("%.2f", sgstAmt)));
                            }
                        }

                        // CGST
                        if (RowBillItem.getChildAt(7) != null) {
                            TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                            double cgstAmt = (Double.parseDouble(SalesTaxAmount.getText().toString()));
                            if (chk_interstate.isChecked()) {
                                pdfItemBean.setCgstAmount(0.00);
                                Log.d("InsertBillItems", "CGST Amt: 0");
                            } else {
                                pdfItemBean.setCgstAmount(Double.parseDouble(String.format("%.2f", cgstAmt)));
                            }
                        }

                        // IGST Tax Amount
                        if (RowBillItem.getChildAt(24) != null) {
                            TextView IGSTTaxAmount = (TextView) RowBillItem.getChildAt(24);
                            double igstAmt = (Double.parseDouble(IGSTTaxAmount.getText().toString()));
                            if (chk_interstate.isChecked()) {
                                pdfItemBean.setIgstAmount(Double.parseDouble(String.format("%.2f",igstAmt)));
                            } else {
                                pdfItemBean.setIgstAmount(0.00);
                            }
                        }

                        // cessTax Amount
                        if (RowBillItem.getChildAt(26) != null) {
                            TextView cessTaxAmount = (TextView) RowBillItem.getChildAt(26);
                            double cessAmt = (Double.parseDouble(cessTaxAmount.getText().toString()));
                            pdfItemBean.setCessAmount(Double.parseDouble(String.format("%.2f",cessAmt)));
                        }

                        // Amount
                        if (RowBillItem.getChildAt(5) != null) {
                            if (chk_interstate.isChecked()) {
                                pdfItemBean.setTotal(pdfItemBean.getTaxableValue() + pdfItemBean.getIgstAmount());
                            } else {
                                pdfItemBean.setTotal(pdfItemBean.getTaxableValue() + pdfItemBean.getSgstAmount() + pdfItemBean.getCgstAmount());
                            }
                        }

                        if (chk_interstate.isChecked()) {
                            TextView IGSTTaxPercent = (TextView) RowBillItem.getChildAt(23);
                            double igsttax = Double.parseDouble(IGSTTaxPercent.getText().toString());
                            pdfItemBean.setGstRate(igsttax);
                        } else {
                            TextView SalesTaxPercent = (TextView) RowBillItem.getChildAt(6);
                            double cgsttax = Double.parseDouble(SalesTaxPercent.getText().toString());
                            TextView ServiceTaxPercent = (TextView) RowBillItem.getChildAt(15);
                            double sgatTax = Double.parseDouble(ServiceTaxPercent.getText().toString());
                            pdfItemBean.setGstRate(cgsttax + sgatTax);
                        }

                        pdfItemBeanArrayList.add(pdfItemBean);
                    }

                    pdfItem.setPdfItemBeanArrayList(pdfItemBeanArrayList);
                    pdfItem.setOtherCharges(Double.parseDouble(tvOthercharges.getText().toString()));

//                    if(createPdfInvoice == null)
//                    {

                    try {
                        createPdfInvoice = CreatePdfInvoice.getInstance(this, pdfItem);
                    } catch (Exception e) {
                        Toast.makeText(this, "Error occurred while generating PDF Invoice", Toast.LENGTH_SHORT).show();
                    }


                    //createPdfInvoice.execute();
//                    }

                } else {
                    Toast.makeText(this, "Please Enter Bill Table Number", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected void PrintNewBill(String InvoiceDate, int printCount) {
//        if (isPrinterAvailable) {
            if (tblOrderItems.getChildCount() < 1) {
                MsgBox.Show("Warning", "Insert item before Print Bill");
                return;
            } else {
                int tableId = 0, waiterId = 0, orderId = 0;
                String waiterName;
                if ((!tvTableNumber.getText().toString().trim().equalsIgnoreCase(""))
                        && (!tvWaiterNumber.getText().toString().trim().equalsIgnoreCase(""))
                        && (!tvBillNumber.getText().toString().trim().equalsIgnoreCase(""))
                        && (!tvWaiterName.getText().toString().trim().equalsIgnoreCase(""))) {
                    tableId = Integer.parseInt(tvTableNumber.getText().toString().trim());
                    waiterId = Integer.parseInt(tvWaiterNumber.getText().toString().trim());
                    waiterName = tvWaiterName.getText().toString().trim();
                    orderId = Integer.parseInt(tvBillNumber.getText().toString().trim());
                    ArrayList<BillTaxItem> billOtherChargesItems = otherChargesPrint();
                    ArrayList<BillServiceTaxItem> billcessTaxItems = new ArrayList<BillServiceTaxItem>();
                    ArrayList<BillTaxSlab> billTaxSlabs = new ArrayList<BillTaxSlab>();


                    billcessTaxItems = cessTaxPrint();
                    PrintKotBillItem item = new PrintKotBillItem();

                    item.setPrintService(PRINTSERVICE);
                    item.setBoldHeader(BOLDHEADER);
                    item.setOwnerDetail(PRINTOWNERDETAIL);

                    Cursor crsrCustomer = dbBillScreen.getCustomer(Integer.valueOf(edtCustId.getText().toString()));
                    if (crsrCustomer.moveToFirst()) {
                        item.setCustomerName(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                    } else {
                        item.setCustomerName(" - - - ");
                    }
                    if(reprintBillingMode>0) {
                        item.setIsDuplicate("\n(Duplicate Bill)");
                    }else{
                        item.setIsDuplicate("");
                    }
                    if(chk_interstate.isChecked())
                    {
                        item.setIsInterState("y");
                        billTaxSlabs = TaxSlabPrint_InterState();
                    }
                    else
                    {
                        item.setIsInterState("n");
                        billTaxSlabs = TaxSlabPrint_IntraState();
                    }
                    ArrayList<BillKotItem> billKotItems = billPrint(billTaxSlabs);

                    item.setBillKotItems(billKotItems);
                    item.setAmountInNextLine(AMOUNTPRINTINNEXTLINE);
                    item.setBillOtherChargesItems(billOtherChargesItems);
                    item.setBillTaxSlabs(billTaxSlabs);
                    item.setBillcessTaxItems(billcessTaxItems);
                    item.setSubTotal(Double.parseDouble(tvSubTotal.getText().toString().trim()));

                    item.setNetTotal(Double.parseDouble(tvBillAmount.getText().toString().trim()));

                    String tablemsg = String.valueOf(tableId);
                    if (tableSplit ==1)
                    {
                        tablemsg  =  String.valueOf(tableId) +" - "+ tvTableSplitNo.getText().toString();
                    }
                    item.setTableNo(tablemsg);
                    item.setWaiterNo(waiterId);
                    item.setWaiterName(waiterName);
                    item.setUTGSTEnabled(UTGSTENABLED);
                    item.setHSNPrintEnabled_out(HSNPRINTENABLED);
                    String billNoPrefix  = db.getBillNoPrefix();
                    item.setBillNo(billNoPrefix+String.valueOf(orderId));
                    item.setOrderBy(strUserName);
                    item.setBillingMode(String.valueOf(jBillingMode));
                    if (strPaymentStatus.equalsIgnoreCase("")) {
                        item.setPaymentStatus("");
                    } else {
                        item.setPaymentStatus(strPaymentStatus);
                    }


                    item.setdiscountPercentage(Float.parseFloat(tvDiscountPercentage.getText().toString()));
                    //if(ItemwiseDiscountEnabled ==1)
                    calculateDiscountAmount();
                    item.setFdiscount(fTotalDiscount);

                    item.setTotalsubTaxPercent(fTotalsubTaxPercent);
                    item.setTotalSalesTaxAmount(tvCGSTValue.getText().toString());
                    item.setTotalServiceTaxAmount(tvSGSTValue.getText().toString());
                    item.setRoundOff(fRoundOfValue);
                    String date_today = tvDate.getText().toString();
                    //Log.d("Date ", date_today);
                    try {
                        Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date_today);
                        Cursor paymentModeinBillcrsr = db.getBillDetail_counter((orderId),String.valueOf(date1.getTime()));
                        if(paymentModeinBillcrsr!=null && paymentModeinBillcrsr.moveToFirst())
                        {
                            double cardValue = 0.00, eWalletValue = 0.00, couponValue = 0.00, pettyCashValue = 0.00, cashValue = 0.00, changeValue = 0.00,
                                    rewardValue = 0.00, aepsAmount = 0, dblMSwipeValue = 0.00, dblPaytmValue = 0.00;

                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_CardPayment)) != null)
                                cardValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_CardPayment));
                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_WalletPayment)) != null)
                                eWalletValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_WalletPayment));
                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_CouponPayment)) != null)
                                couponValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_CouponPayment));
                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_PettyCashPayment)) != null)
                                pettyCashValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_PettyCashPayment));
                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_CashPayment)) != null)
                                cashValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_CashPayment));
                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_ChangePayment)) != null)
                                changeValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_ChangePayment));
                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_RewardPointsAmount)) != null)
                                rewardValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_RewardPointsAmount));
                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_AEPSAmount)) != null)
                                aepsAmount = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_AEPSAmount));
                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_MSWIPE_Amount)) != null)
                                dblMSwipeValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_MSWIPE_Amount));
                            if (paymentModeinBillcrsr.getString(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_PAYTM_WALLET)) != null) {
                                dblPaytmValue = paymentModeinBillcrsr.getDouble(paymentModeinBillcrsr.getColumnIndex(DatabaseHandler.KEY_PAYTM_WALLET));
                            }

                            cardValue = Double.parseDouble(String.format("%.2f", cardValue));
                            eWalletValue = Double.parseDouble(String.format("%.2f", eWalletValue));
                            couponValue = Double.parseDouble(String.format("%.2f", couponValue));
                            pettyCashValue = Double.parseDouble(String.format("%.2f", pettyCashValue));
                            cashValue = Double.parseDouble(String.format("%.2f", cashValue));
                            changeValue = Double.parseDouble(String.format("%.2f", changeValue));
                            rewardValue = Double.parseDouble(String.format("%.2f", rewardValue));
                            aepsAmount = Double.parseDouble(String.format("%.2f", aepsAmount));
                            dblMSwipeValue = Double.parseDouble(String.format("%.2f", dblMSwipeValue));
                            dblPaytmValue = Double.parseDouble(String.format("%.2f", dblPaytmValue));

                            item.setCardPaymentValue(cardValue);
                            item.seteWalletPaymentValue(eWalletValue);
                            item.setCouponPaymentValue(couponValue);
                            item.setPettyCashPaymentValue(pettyCashValue);
                            item.setCashPaymentValue(cashValue);
                            item.setChangePaymentValue(changeValue);
                            item.setRewardPoints(rewardValue);
                            item.setAepsPaymentValue(aepsAmount);
                            item.setDblMSwipeVale(dblMSwipeValue);
                            item.setDblPaytmValue(dblPaytmValue);
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    if(reprintBillingMode == 0) {
                        item.setStrBillingModeName(DineInCaption);
                        item.setDate(tvDate.getText().toString());
                        //item.setTime(String.format("%tR", Time));
                        String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());
                        item.setTime(strTime);
                    }else // reprint
                    {
                        switch (reprintBillingMode)
                        {
                            case 1 : item.setStrBillingModeName(DineInCaption);
                                item.setBillingMode("1");
                                //item.setPaymentStatus(""); // payment status not required for dinein Mode
                                break;
                            case 2 : item.setStrBillingModeName(CounterSalesCaption);
                                item.setBillingMode("2");
                                //item.setPaymentStatus(""); // payment status not required for CounterSales Mode
                                break;
                            case 3 : item.setStrBillingModeName(TakeAwayCaption);
                                item.setBillingMode("3");
                                break;
                            case 4 : item.setStrBillingModeName(HomeDeliveryCaption);
                                item.setBillingMode("4");
                                if(crsrCustomer!=null){
                                    String CustDetail = crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName"));
                                    CustDetail = CustDetail +"\n"+crsrCustomer.getString(crsrCustomer.getColumnIndex("CustAddress"));
                                    item.setCustomerName(CustDetail);
                                }
                                break;
                        }
                        try{
                            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(tvDate.getText().toString());
                            Cursor c  = db.getBillDetail_counter(orderId,String.valueOf(date.getTime()));
                            if(c!=null && c.moveToNext()){

                                String time = c.getString(c.getColumnIndex("Time"));
                                item.setTime(time);
                                item.setDate(tvDate.getText().toString());
                                float round = c.getString(c.getColumnIndex("RoundOff")) == (null)?0:c.getFloat(c.getColumnIndex("RoundOff"));
                                item.setRoundOff(Float.parseFloat(String.format("%.2f",round)));

                                if(reprintBillingMode==1)
                                {
                                    String tableNo = c.getString(c.getColumnIndex("TableNo"));
                                    String splitno = c.getString(c.getColumnIndex("TableSplitNo"));
                                    if(splitno!=null && !splitno.equals(""))
                                        item.setTableNo(tableNo+" - "+splitno);
                                    else
                                        item.setTableNo(tableNo);
                                }
                                String userId = c.getString(c.getColumnIndex("UserId"));
                                if(reprintBillingMode != 0 && userId!=null)
                                {
                                    Cursor user_cursor = db.getUsers_counter(userId);
                                    if(user_cursor!=null && user_cursor.moveToFirst())
                                    {
                                        item.setOrderBy(user_cursor.getString(user_cursor.getColumnIndex("Name")));
                                    }
                                }

                            }}catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    String prf = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString("bill", "--Select--");
                /*Intent intent = new Intent(getApplicationContext(), PrinterSohamsaActivity.class);*/
                    Intent intent = null;
                    if (prf.equalsIgnoreCase("Sohamsa")) {
                        String[] tokens = new String[3];
                        tokens[0] = "";
                        tokens[1] = "";
                        tokens[2] = "";
                        Cursor crsrHeaderFooterSetting = null;
                        crsrHeaderFooterSetting = dbBillScreen.getBillSetting();
                        if (crsrHeaderFooterSetting.moveToFirst()) {
                            try {
                                tokens = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText")).split(Pattern.quote("|"));
                            } catch (Exception e) {
                                tokens[0] = "";
                                tokens[1] = "";
                                tokens[2] = "";
                            }
                            if (!tokens[0].equalsIgnoreCase(""))
                                item.setAddressLine1(tokens[0]);
                            if (!tokens[1].equalsIgnoreCase(""))
                                item.setAddressLine2(tokens[1]);
                            if (!tokens[2].equalsIgnoreCase(""))
                            {  if(reprintBillingMode>0) {
                                item.setAddressLine3(tokens[2]+"\n(Duplicate Bill)");
                            }else{
                                item.setAddressLine3(tokens[2]);}
                            }
                            item.setFooterLine1(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText1")));
                            item.setFooterLine2(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText2")));
                            item.setFooterLine3(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText3")));
                            item.setFooterLine4(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText4")));
                            item.setFooterLine5(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText5")));
                        } else {
                            Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                        }

                        //printSohamsaBILL(item, "BILL");
                    } else if (prf.equalsIgnoreCase("Heyday")) {
                        String[] tokens = new String[3];
                        tokens[0] = "";
                        tokens[1] = "";
                        tokens[2] = "";
                        Cursor crsrHeaderFooterSetting = null;
                        crsrHeaderFooterSetting = db.getOwnerDetail();
                        if (crsrHeaderFooterSetting.moveToFirst()) {
                            try {
                                tokens[0] = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("GSTIN"));
                                tokens[1] = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FirmName"));
                                tokens[2] = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("Address"));
                            } catch (Exception e) {
                                tokens[0] = "";
                                tokens[1] = "";
                                tokens[2] = "";
                            }
                            if (!tokens[0].equalsIgnoreCase(""))
                                item.setAddressLine1(tokens[0]);
                            if (!tokens[1].equalsIgnoreCase(""))
                                item.setAddressLine2(tokens[1]);

                            if(chk_interstate.isChecked())
                            {
                                item.setCustomerName(item.getCustomerName()+ "  ("+(spnr_pos.getSelectedItem().toString())+") ");
                                tokens[2] =  tokens[2] + "\n ("+getState_pos(db.getOwnerPOS())+") ";
                            }
                            item.setAddressLine3(tokens[2]);
                            crsrHeaderFooterSetting = db.getBillSettings();
                            if(crsrHeaderFooterSetting.moveToNext()) {
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
                            }
                        }  else {
                            Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                        }
                        //startActivity(intent);
                        if (isPrinterAvailable) {
                            for (int i = 0; i < printCount; i++)
                                printHeydeyBILL(item, "BILL");
                        } else {
//                            askForConfig();
                            Toast.makeText(BillingDineInActivity.this, "Printer is not available.", Toast.LENGTH_SHORT).show();
                        }
                    } else if(prf.equalsIgnoreCase(Constants.USB_WEP_PRINTER_NAME)) {

                        String target = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(prf, "--Select--");

                        WePTHPrinterBaseActivity wepPrinter = new WePTHPrinterBaseActivity();

                        String[] tokens = new String[3];
                        tokens[0] = "";
                        tokens[1] = "";
                        tokens[2] = "";

                        Cursor crsrOwnerDetails = null;
                        try {
                            crsrOwnerDetails = db.getOwnerDetail();

                            if (crsrOwnerDetails.moveToFirst()) {
                                try {
                                    tokens[0] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex("GSTIN"));
                                    tokens[1] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex("FirmName"));
                                    tokens[2] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex("Address"));
                                    item.setCompanyLogoPath(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)));
                                } catch (Exception e) {
                                    tokens[0] = "";
                                    tokens[1] = "";
                                    tokens[2] = "";
                                }
                                if (!tokens[0].equalsIgnoreCase(""))
                                    item.setAddressLine1(tokens[0]);
                                if (!tokens[1].equalsIgnoreCase(""))
                                    item.setAddressLine2(tokens[1]);

                                if(chk_interstate.isChecked())
                                {
                                    item.setCustomerName(item.getCustomerName()+ "  ("+(spnr_pos.getSelectedItem().toString())+") ");
                                    tokens[2] =  tokens[2] + "\n ("+getState_pos(db.getOwnerPOS())+") ";
                                }
                                item.setAddressLine3(tokens[2]);
                            } else {
                                Log.d(TAG, "Display Owner Details No data in BillSettings table");
                            }
                        } catch (Exception ex){
                            Log.e(TAG,"Unable to fetch data from owner details data from table." +ex.getMessage());
                        } finally {
                            if(crsrOwnerDetails != null){
                                crsrOwnerDetails.close();
                            }
                        }
                        Cursor crsrHeaderFooterSetting = null;
                        try {
                            crsrHeaderFooterSetting = db.getBillSettings();

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
                                Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                            }
                        }catch (Exception ex){
                            Log.e(TAG,"Unable to fetch header details from billSettings table. From method PrintNewBill()." +ex.getMessage());
                        } finally {
                            if(crsrHeaderFooterSetting != null){
                                crsrHeaderFooterSetting.close();
                            }
                        }

                        wepPrinter.setmTarget(target);
                        wepPrinter.setmContext(this);
                        wepPrinter.mInitListener(this);

                        if (wepPrinter.runPrintBillSequence(item, printCount)) {
                            Toast.makeText(this, "Bill Printed.", Toast.LENGTH_SHORT).show();
                        }

                    } else if (prf.equalsIgnoreCase("NGX")) {
                        String[] tokens = new String[3];
                        tokens[0] = "";
                        tokens[1] = "";
                        tokens[2] = "";

                        Cursor crsrOwnerDetails = null;
                        try {
                            crsrOwnerDetails = db.getOwnerDetail();

                            if (crsrOwnerDetails.moveToFirst()) {
                                try {
                                    tokens[0] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_GSTIN));
                                    tokens[1] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_FIRM_NAME));
                                    tokens[2] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Address));
                                    item.setCompanyLogoPath(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)));
                                } catch (Exception e) {
                                    tokens[0] = "";
                                    tokens[1] = "";
                                    tokens[2] = "";
                                }
                                if (!tokens[0].equalsIgnoreCase(""))
                                    item.setAddressLine1(tokens[0]);
                                if (!tokens[1].equalsIgnoreCase(""))
                                    item.setAddressLine2(tokens[1]);

                                if (chk_interstate.isChecked()) {
                                    item.setCustomerName(item.getCustomerName() + "\n  (" + (spnr_pos.getSelectedItem().toString()) + ") ");
                                    tokens[2] = tokens[2] + "\n (" + getState_pos(db.getOwnerPOS()) + ") ";
                                }
                                item.setAddressLine3(tokens[2]);
                            } else {
                                Log.d(TAG, "Display Owner Details No data in BillSettings table");
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Unable to fetch data from owner details data from table." + ex.getMessage());
                        } finally {
                            if (crsrOwnerDetails != null) {
                                crsrOwnerDetails.close();
                            }
                        }
                        Cursor crsrHeaderFooterSetting = null;
                        try {
                            crsrHeaderFooterSetting = db.getBillSettings();

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
                                Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Unable to fetch header details from billSettings table. From method PrintNewBill()." + ex.getMessage());
                        } finally {
                            if (crsrHeaderFooterSetting != null) {
                                crsrHeaderFooterSetting.close();
                            }
                        }
                        for (int i = 0; i < printCount; i++)
                            printNGXBILL(item, "BILL");
                    } else if (prf.equalsIgnoreCase("TM Printer")) {

                        String target = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(prf, "--Select--");

                        EPSONPrinterBaseActivity epson = new EPSONPrinterBaseActivity();

                        String[] tokens = new String[3];
                        tokens[0] = "";
                        tokens[1] = "";
                        tokens[2] = "";

                        Cursor crsrOwnerDetails = null;
                        try {
                            crsrOwnerDetails = db.getOwnerDetail();

                            if (crsrOwnerDetails.moveToFirst()) {
                                try {
                                    tokens[0] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_GSTIN));
                                    tokens[1] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_FIRM_NAME));
                                    tokens[2] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Address));
                                    item.setCompanyLogoPath(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)));
                                } catch (Exception e) {
                                    tokens[0] = "";
                                    tokens[1] = "";
                                    tokens[2] = "";
                                    item.setCompanyLogoPath("");
                                }
                                if (!tokens[0].equalsIgnoreCase(""))
                                    item.setAddressLine1(tokens[0]);
                                if (!tokens[1].equalsIgnoreCase(""))
                                    item.setAddressLine2(tokens[1]);

                                if (chk_interstate.isChecked()) {
                                    item.setCustomerName(item.getCustomerName() + "  (" + (spnr_pos.getSelectedItem().toString()) + ") ");
                                    tokens[2] = tokens[2] + "\n (" + getState_pos(db.getOwnerPOS()) + ") ";
                                }
                                item.setAddressLine3(tokens[2]);
                            } else {
                                Log.d(TAG, "Display Owner Details No data in BillSettings table");
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Unable to fetch data from owner details data from table." + ex.getMessage());
                        } finally {
                            if (crsrOwnerDetails != null) {
                                crsrOwnerDetails.close();
                            }
                        }
                        Cursor crsrHeaderFooterSetting = null;
                        try {
                            crsrHeaderFooterSetting = db.getBillSettings();

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
                                Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Unable to fetch header details from billSettings table. From method PrintNewBill()." + ex.getMessage());
                        } finally {
                            if (crsrHeaderFooterSetting != null) {
                                crsrHeaderFooterSetting.close();
                            }
                        }

                        epson.setmTarget(target);
                        epson.setmContext(this);
                        epson.mInitListener(this);

                       /* progressDialog = new ProgressDialog(myContext);
                        progressDialog.setMessage("Printing Bill....");
                        progressDialog.setCancelable(false);
                        progressDialog.show();*/

                        /*Message msg = new Message();
                        msg.what = GIVE_PRINT_COMMAND;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("item", item);
                        bundle.putInt("printCount", printCount);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);*/
                        if (epson.runPrintBillSequence(item, printCount)) {
//                            progressDialog.dismiss();
                            Toast.makeText(this, "Bill Printed.", Toast.LENGTH_SHORT).show();
                        } else {
//                            progressDialog.dismiss();
                        }


                    } else if (prf.equalsIgnoreCase(Constants.USB_BIXOLON_PRINTER_NAME)) {

                        String target = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(prf, "--Select--");

                        BixolonPrinterBaseAcivity bixolon = new BixolonPrinterBaseAcivity();

                        String[] tokens = new String[3];
                        tokens[0] = "";
                        tokens[1] = "";
                        tokens[2] = "";

                        Cursor crsrOwnerDetails = null;
                        try {
                            crsrOwnerDetails = db.getOwnerDetail();

                            if (crsrOwnerDetails.moveToFirst()) {
                                try {
                                    tokens[0] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_GSTIN));
                                    tokens[1] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_FIRM_NAME));
                                    tokens[2] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Address));
                                    item.setCompanyLogoPath(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)));
                                } catch (Exception e) {
                                    tokens[0] = "";
                                    tokens[1] = "";
                                    tokens[2] = "";
                                    item.setCompanyLogoPath("");
                                }
                                if (!tokens[0].equalsIgnoreCase(""))
                                    item.setAddressLine1(tokens[0]);
                                if (!tokens[1].equalsIgnoreCase(""))
                                    item.setAddressLine2(tokens[1]);

                                if (chk_interstate.isChecked()) {
                                    item.setCustomerName(item.getCustomerName() + "  (" + (spnr_pos.getSelectedItem().toString()) + ") ");
                                    tokens[2] = tokens[2] + "\n (" + getState_pos(db.getOwnerPOS()) + ") ";
                                }
                                item.setAddressLine3(tokens[2]);
                            } else {
                                Log.d(TAG, "Display Owner Details No data in BillSettings table");
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Unable to fetch data from owner details data from table." + ex.getMessage());
                        } finally {
                            if (crsrOwnerDetails != null) {
                                crsrOwnerDetails.close();
                            }
                        }
                        Cursor crsrHeaderFooterSetting = null;
                        try {
                            crsrHeaderFooterSetting = db.getBillSettings();

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
                                Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Unable to fetch header details from billSettings table. From method PrintNewBill()." + ex.getMessage());
                        } finally {
                            if (crsrHeaderFooterSetting != null) {
                                crsrHeaderFooterSetting.close();
                            }
                        }

                        bixolon.setmTarget(target);
                        bixolon.setmContext(this);
                        bixolon.mInitListener(this);

                        if (bixolon.runPrintBillSequence(item, printCount)) {
//                            progressDialog.dismiss();
                            Toast.makeText(this, "Bill Printed.", Toast.LENGTH_SHORT).show();
                        } else {
//                            progressDialog.dismiss();
                        }


                    } else if(prf.equalsIgnoreCase(Constants.USB_TVS_PRINTER_NAME)){ //TVS printer implementation
                        String target = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(prf, "--Select--");

                        tvsPrinterBaseActivity = new TVSPrinterBaseActivity();

                        String[] tokens = new String[3];
                        tokens[0] = "";
                        tokens[1] = "";
                        tokens[2] = "";

                        Cursor crsrOwnerDetails = null;
                        try {
                            crsrOwnerDetails = db.getOwnerDetail();

                            if (crsrOwnerDetails.moveToFirst()) {
                                try {
                                    tokens[0] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_GSTIN));
                                    tokens[1] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_FIRM_NAME));
                                    tokens[2] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Address));
                                    item.setCompanyLogoPath(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)));
                                } catch (Exception e) {
                                    tokens[0] = "";
                                    tokens[1] = "";
                                    tokens[2] = "";
                                    item.setCompanyLogoPath("");
                                }
                                if (!tokens[0].equalsIgnoreCase(""))
                                    item.setAddressLine1(tokens[0]);
                                if (!tokens[1].equalsIgnoreCase(""))
                                    item.setAddressLine2(tokens[1]);

                                if (chk_interstate.isChecked()) {
                                    item.setCustomerName(item.getCustomerName() + "  (" + (spnr_pos.getSelectedItem().toString()) + ") ");
                                    tokens[2] = tokens[2] + "\n (" + getState_pos(db.getOwnerPOS()) + ") ";
                                }
                                item.setAddressLine3(tokens[2]);
                            } else {
                                Log.d(TAG, "Display Owner Details No data in BillSettings table");
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Unable to fetch data from owner details data from table." + ex.getMessage());
                        } finally {
                            if (crsrOwnerDetails != null) {
                                crsrOwnerDetails.close();
                            }
                        }
                        Cursor crsrHeaderFooterSetting = null;
                        try {
                            crsrHeaderFooterSetting = db.getBillSettings();

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
                                Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Unable to fetch header details from billSettings table. From method PrintNewBill()." + ex.getMessage());
                        } finally {
                            if (crsrHeaderFooterSetting != null) {
                                crsrHeaderFooterSetting.close();
                            }
                        }

                        tvsPrinterBaseActivity.setmTarget(target);
                        tvsPrinterBaseActivity.setmContext(this);
                        tvsPrinterBaseActivity.mInitListener(this);

                        if (tvsPrinterBaseActivity.runPrintBillSequence(item, printCount)) {
//                            progressDialog.dismiss();
                            //for testing commented on 13/08/2018
                            // Toast.makeText(myContext, "Bill Printed.", Toast.LENGTH_SHORT).show();
                        } else {
//                            progressDialog.dismiss();
                        }

                    } else if(prf.equalsIgnoreCase(Constants.USB_WiFi_PRINTER_NAME)){
                        if (isWifiConnected()) {
                            String E_INVOICE_NAME = "Invoice_" + item.getBillNo() + "_" + item.getDate() + ".pdf";
                            String filePathString = PDF_INVOICES_GENERATE_PATH + E_INVOICE_NAME;
                            File f = new File(filePathString);
                            if(f.exists() && !f.isDirectory()) {

                                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                                printManager.print("Printing Invoice...", new WifiPrinterBaseActivity(filePathString), null);

                            } else {
                                MsgBox.Show("Warning", "Invoice does not exist in PDF form or moved to a different location.\n" +
                                        "If moved to other location please place it in WeP_Retail_Invoices directory.");
                            }
                        } else {
                            Toast.makeText(this, "Please connect to a WiFi network first.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(myContext, "Printer not configured. Kindly goto settings and configure printer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BillingDineInActivity.this, "Please Enter Bill, Waiter, Table Number", Toast.LENGTH_SHORT).show();
                }
            }
       /* } else {
            Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
            askForConfig();
        }*/
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

    protected void PrintPreviewBill() {
//        if (isPrinterAvailable) {
            if (tblOrderItems.getChildCount() < 1) {
                MsgBox.Show("Warning", "Insert item before Print Bill");
                return;
            } else {
                try
                {
                    int tableId = 0, waiterId = 0, orderId = 0;
                    String waiterName;
                    if ((!tvTableNumber.getText().toString().trim().equalsIgnoreCase(""))
                            && (!tvWaiterNumber.getText().toString().trim().equalsIgnoreCase(""))
                            && (!tvBillNumber.getText().toString().trim().equalsIgnoreCase(""))
                            && (!tvWaiterName.getText().toString().trim().equalsIgnoreCase(""))) {
                        tableId = Integer.parseInt(tvTableNumber.getText().toString().trim());
                        waiterId = Integer.parseInt(tvWaiterNumber.getText().toString().trim());
                        waiterName = tvWaiterName.getText().toString().trim();
                        orderId = Integer.parseInt(tvBillNumber.getText().toString().trim());
                        ArrayList<BillTaxItem> billOtherChargesItems = otherChargesPrint();
                        ArrayList<BillServiceTaxItem> billcessTaxItems = new ArrayList<BillServiceTaxItem>();
                        ArrayList<BillTaxSlab> billTaxSlabs = new ArrayList<BillTaxSlab>();


                        billcessTaxItems = cessTaxPreviewPrint();
                        PrintKotBillItem item = new PrintKotBillItem();

                        item.setPrintService(PRINTSERVICE);
                        item.setBoldHeader(BOLDHEADER);
                        item.setOwnerDetail(PRINTOWNERDETAIL);

                        Cursor crsrCustomer = dbBillScreen.getCustomer(Integer.valueOf(edtCustId.getText().toString()));
                        if (crsrCustomer.moveToFirst()) {
                            item.setCustomerName(crsrCustomer.getString(crsrCustomer.getColumnIndex("CustName")));
                        } else {
                            item.setCustomerName(" - - - ");
                        }

                        item.setIsDuplicate("");

                        if(chk_interstate.isChecked())
                        {
                            item.setIsInterState("y");
                            billTaxSlabs = TaxSlabPreviewPrint_InterState();
                        }
                        else
                        {
                            item.setIsInterState("n");
                            billTaxSlabs = TaxSlabPreviewPrint_IntraState();
                        }
                        ArrayList<BillKotItem> billKotItems = billPreviewPrint(billTaxSlabs);

                        item.setBillKotItems(billKotItems);
                        item.setBillOtherChargesItems(billOtherChargesItems);
                        item.setBillTaxSlabs(billTaxSlabs);
                        item.setBillcessTaxItems(billcessTaxItems);
                        item.setSubTotal(Double.parseDouble(tvSubTotal.getText().toString().trim()));

                        double roundOffFinalAmount = 0;
                        double finalAmount = Double.parseDouble(tvBillAmount.getText().toString().trim());
                        if (BILLAMOUNTROUNDOFF == 1) {
                            String temp = tvBillAmount.getText().toString().trim();
                            if (!temp.contains(".00")){
                                roundOffFinalAmount = Math.round(finalAmount);
                                fRoundOfValue = Double.parseDouble("0" + temp.substring(temp.indexOf(".")));
                            } else {
                                roundOffFinalAmount = finalAmount;
                            }
                        } else {
                            roundOffFinalAmount = finalAmount;
                            fRoundOfValue = 0;
                        }

                        item.setNetTotal(roundOffFinalAmount);
                        item.setRoundOff(fRoundOfValue);

                        String tablemsg = String.valueOf(tableId);
                        if (tableSplit ==1)
                        {
                            tablemsg  =  String.valueOf(tableId) +" - "+ tvTableSplitNo.getText().toString();
                        }
                        item.setTableNo(tablemsg);
                        item.setWaiterNo(waiterId);
                        item.setWaiterName(waiterName);
                        item.setUTGSTEnabled(UTGSTENABLED);
                        item.setHSNPrintEnabled_out(HSNPRINTENABLED);
                        String billNoPrefix  = db.getBillNoPrefix();
                        item.setBillNo(billNoPrefix+String.valueOf(orderId));
                        item.setOrderBy(strUserName);
                        item.setBillingMode(String.valueOf(jBillingMode));
                        if (strPaymentStatus.equalsIgnoreCase("")) {
                            item.setPaymentStatus("");
                        } else {
                            item.setPaymentStatus(strPaymentStatus);
                        }


                        item.setdiscountPercentage(Float.parseFloat(tvDiscountPercentage.getText().toString()));
                        //if(ItemwiseDiscountEnabled ==1)
                        calculateDiscountAmount();
                        item.setFdiscount(fTotalDiscount);

                        item.setTotalsubTaxPercent(fTotalsubTaxPercent);
                        item.setTotalSalesTaxAmount(tvCGSTValue.getText().toString());
                        item.setTotalServiceTaxAmount(tvSGSTValue.getText().toString());


                        if(reprintBillingMode == 0) {
                            item.setStrBillingModeName(DineInCaption);
                            item.setDate(tvDate.getText().toString());
                            //item.setTime(String.format("%tR", Time));
                            String strTime = new SimpleDateFormat("kk:mm:ss").format(Time.getTime());
                            item.setTime(strTime);
                        }
                        String prf = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString("bill", "--Select--");
                /*Intent intent = new Intent(getApplicationContext(), PrinterSohamsaActivity.class);*/
                        Intent intent = null;
                        if (prf.equalsIgnoreCase("Sohamsa")) {
                            String[] tokens = new String[3];
                            tokens[0] = "";
                            tokens[1] = "";
                            tokens[2] = "";
                            Cursor crsrHeaderFooterSetting = null;
                            crsrHeaderFooterSetting = dbBillScreen.getBillSetting();
                            if (crsrHeaderFooterSetting.moveToFirst()) {
                                try {
                                    tokens = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText")).split(Pattern.quote("|"));
                                } catch (Exception e) {
                                    tokens[0] = "";
                                    tokens[1] = "";
                                    tokens[2] = "";
                                }
                                if (!tokens[0].equalsIgnoreCase(""))
                                    item.setAddressLine1(tokens[0]);
                                if (!tokens[1].equalsIgnoreCase(""))
                                    item.setAddressLine2(tokens[1]);
                                if (!tokens[2].equalsIgnoreCase(""))
                                {  if(reprintBillingMode>0) {
                                    item.setAddressLine3(tokens[2]+"\n(Duplicate Bill)");
                                }else{
                                    item.setAddressLine3(tokens[2]);}
                                }
                                item.setFooterLine1(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText1")));
                                item.setFooterLine2(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText2")));
                                item.setFooterLine3(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText3")));
                                item.setFooterLine4(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText4")));
                                item.setFooterLine5(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText5")));
                            } else {
                                Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                            }

                            //printSohamsaBILL(item, "BILL");
                        } else if (prf.equalsIgnoreCase("Heyday")) {
                            String[] tokens = new String[3];
                            tokens[0] = "";
                            tokens[1] = "";
                            tokens[2] = "";
                            Cursor crsrHeaderFooterSetting = null;
                            crsrHeaderFooterSetting = db.getOwnerDetail();
                            if (crsrHeaderFooterSetting.moveToFirst()) {
                                try {
                                    tokens[0] = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("GSTIN"));
                                    tokens[1] = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FirmName"));
                                    tokens[2] = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("Address"));
                                } catch (Exception e) {
                                    tokens[0] = "";
                                    tokens[1] = "";
                                    tokens[2] = "";
                                }
                                if (!tokens[0].equalsIgnoreCase(""))
                                    item.setAddressLine1(tokens[0]);
                                if (!tokens[1].equalsIgnoreCase(""))
                                    item.setAddressLine2(tokens[1]);

                                if(chk_interstate.isChecked())
                                {
                                    item.setCustomerName(item.getCustomerName()+ "  ("+(spnr_pos.getSelectedItem().toString())+") ");
                                    tokens[2] =  tokens[2] + "\n ("+getState_pos(db.getOwnerPOS())+") ";
                                }
                                item.setAddressLine3(tokens[2]);
                                crsrHeaderFooterSetting = db.getBillSettings();
                                if(crsrHeaderFooterSetting.moveToNext()) {
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
                                }
                            }  else {
                                Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                            }
                            //startActivity(intent);
                            if (isPrinterAvailable) {
                                printHeydeyBILL(item, "BILL");
                                int iResult = db.deletePreviewBillTable(orderId, item.getDate());
                            } else {
//                                askForConfig();
                                Toast.makeText(BillingDineInActivity.this, "Printer is not available.", Toast.LENGTH_SHORT).show();
                            }
                        } else if(prf.equalsIgnoreCase(Constants.USB_WEP_PRINTER_NAME)) {

                            String target = Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(prf, "--Select--");

                            WePTHPrinterBaseActivity wepPrinter = new WePTHPrinterBaseActivity();

                            String[] tokens = new String[3];
                            tokens[0] = "";
                            tokens[1] = "";
                            tokens[2] = "";

                            Cursor crsrOwnerDetails = null;
                            try {
                                crsrOwnerDetails = db.getOwnerDetail();

                                if (crsrOwnerDetails.moveToFirst()) {
                                    try {
                                        tokens[0] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex("GSTIN"));
                                        tokens[1] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex("FirmName"));
                                        tokens[2] = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex("Address"));
                                    } catch (Exception e) {
                                        tokens[0] = "";
                                        tokens[1] = "";
                                        tokens[2] = "";
                                    }
                                    if (!tokens[0].equalsIgnoreCase(""))
                                        item.setAddressLine1(tokens[0]);
                                    if (!tokens[1].equalsIgnoreCase(""))
                                        item.setAddressLine2(tokens[1]);

                                    if(chk_interstate.isChecked())
                                    {
                                        item.setCustomerName(item.getCustomerName()+ "  ("+(spnr_pos.getSelectedItem().toString())+") ");
                                        tokens[2] =  tokens[2] + "\n ("+getState_pos(db.getOwnerPOS())+") ";
                                    }
                                    item.setAddressLine3(tokens[2]);
                                } else {
                                    Log.d(TAG, "Display Owner Details No data in BillSettings table");
                                }
                            } catch (Exception ex){
                                Log.e(TAG,"Unable to fetch data from owner details data from table." +ex.getMessage());
                            } finally {
                                if(crsrOwnerDetails != null){
                                    crsrOwnerDetails.close();
                                }
                            }
                            Cursor crsrHeaderFooterSetting = null;
                            try {
                                crsrHeaderFooterSetting = db.getBillSettings();

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
                                    Log.d(TAG, "DisplayHeaderFooterSettings No data in BillSettings table");
                                }
                            }catch (Exception ex){
                                Log.e(TAG,"Unable to fetch header details from billSettings table. From method PrintNewBill()." +ex.getMessage());
                            } finally {
                                if(crsrHeaderFooterSetting != null){
                                    crsrHeaderFooterSetting.close();
                                }
                            }

                            wepPrinter.setmTarget(target);
                            wepPrinter.setmContext(this);
                            wepPrinter.mInitListener(this);

                            if (wepPrinter.runPrintBillSequence(item, 1)) {
//                            progressDialog.dismiss();
                                Toast.makeText(this, "Bill Printed.", Toast.LENGTH_SHORT).show();
                            } else {
//                            progressDialog.dismiss();
                            }


                        } else {
                            Toast.makeText(myContext, "Printer not configured. Kindly goto settings and configure printer", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BillingDineInActivity.this, "Please Enter Bill, Waiter, Table Number", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                    dbBillScreen.deletePreviewBillTable(0,"");
                }
            }
      /*  } else {
            Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
            askForConfig();
        }*/
    }

    private int tableNumber = 0, tableSplitNumber = 0, kotNumber = 0;

    public void ReprintKOT(final View v) {
        //takeScreenshot();

        AlertDialog.Builder DineInTenderDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.tender_dinein, null);

        final Spinner spnTblNo = (Spinner) vwAuthorization.findViewById(R.id.txtDineInTenderTableNumber);
        final Spinner spnTblSplitNo = (Spinner) vwAuthorization.findViewById(R.id.txtDineInTenderTableSplitNo);
        final Spinner spnKOTNo = (Spinner) vwAuthorization.findViewById(R.id.spnKOTNo);

        int iMaxTables = crsrSettings.getInt(crsrSettings.getColumnIndex("MaximumTables"));
        final int TableSplitEnable = crsrSettings.getInt(crsrSettings.getColumnIndex("TableSpliting"));
        final ArrayList<String> arrTblNo = new ArrayList<>();
        final ArrayList<String> arrTblSplitNo = new ArrayList<>();
        final ArrayList<String> arrKOTNo = new ArrayList<>();

        arrTblNo.add(0, "--Select--");

        for (int i=1; i<=iMaxTables; i++) {
            arrTblNo.add(i, String.valueOf(i));
        }

        arrTblSplitNo.add("--Select--");
        arrTblSplitNo.add("1");
        arrTblSplitNo.add("2");
        arrTblSplitNo.add("3");
        arrTblSplitNo.add("4");

        ArrayAdapter adapterTblNO = new ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrTblNo);
        ArrayAdapter adapterTblSplitNo = new ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrTblSplitNo);

        spnTblNo.setAdapter(adapterTblNO);

        spnTblSplitNo.setAdapter(adapterTblSplitNo);

        spnTblSplitNo.setEnabled(false);
        spnKOTNo.setEnabled(false);

        spnTblNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrKOTNo.clear();
                arrKOTNo.add(0, "--Select--");
                if (!arrTblNo.get(position).equals("--Select--")) {
                    tableNumber = Integer.parseInt(arrTblNo.get(position));
                    if (TableSplitEnable == 1) {
                        spnTblSplitNo.setEnabled(true);
                    } else {
                        tableSplitNumber = 1;
                        spnTblSplitNo.setEnabled(false);
                        spnKOTNo.setEnabled(true);
//                        arrKOTNo.clear();
//                        arrKOTNo.add(0, "--Select--");
                        Cursor cursor = dbBillScreen.getKOTNumbers(tableNumber, tableSplitNumber);
                        if (cursor !=null && cursor.moveToFirst()) {
                            do{
                                arrKOTNo.add(cursor.getString(cursor.getColumnIndex("TokenNumber")));
                            }while (cursor.moveToNext());
                            if (arrKOTNo.size() > 0) {
                                spnKOTNo.setEnabled(true);
                            } else {
                                spnKOTNo.setEnabled(false);
                            }
                        } else {
                            spnKOTNo.setEnabled(false);
                            Toast.makeText(myContext, "No data found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    spnTblSplitNo.setEnabled(false);
                    spnKOTNo.setEnabled(false);
                }
                ArrayAdapter adapterKOTNo = new ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrKOTNo);
                spnKOTNo.setAdapter(adapterKOTNo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spnTblSplitNo.setEnabled(false);
                spnKOTNo.setEnabled(false);

            }
        });

        spnTblSplitNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrKOTNo.clear();
                arrKOTNo.add(0, "--Select--");
                if (position > 0) {
                    tableSplitNumber = Integer.parseInt(arrTblSplitNo.get(position));
                    Cursor cursor = dbBillScreen.getKOTNumbers(tableNumber, tableSplitNumber);
                    if (cursor !=null && cursor.moveToFirst()) {
                        do{
                            arrKOTNo.add(cursor.getString(cursor.getColumnIndex("TokenNumber")));
                        }while (cursor.moveToNext());
                        if (arrKOTNo.size() > 0) {
                            spnKOTNo.setEnabled(true);
                        } else {
                            spnKOTNo.setEnabled(false);
                        }
                    } else {
                        spnKOTNo.setEnabled(false);
                        Toast.makeText(myContext, "No data found.", Toast.LENGTH_SHORT).show();
                    }

                }
                ArrayAdapter adapterKOTNo = new ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrKOTNo);
                spnKOTNo.setAdapter(adapterKOTNo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spnKOTNo.setEnabled(false);
            }
        });

        spnKOTNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    kotNumber = Integer.parseInt(arrKOTNo.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        DineInTenderDialog.setIcon(R.drawable.ic_launcher).setTitle("RePrint KOT")
                .setView(vwAuthorization).setNegativeButton("Cancel", null)
                .setPositiveButton("RePrint", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        if (tableNumber == 0)
                            return;

                        if (tableSplitNumber == 0)
                            return;

                        if (kotNumber == 0)
                            return;

                        Cursor BillItems = dbBillScreen.getKOTItemsForReprint(tableNumber, tableSplitNumber, kotNumber);

                        if (BillItems.moveToFirst()) {
                            tvTableNumber.setText(""+tableNumber);
                            tvTableSplitNo.setText(""+tableSplitNumber);
                            tvSubUdfValue.setText("1");
                            tblOrderItems.removeAllViews();
                            REPRINT_KOT =1;
                            LoadKOTItems(BillItems,true);
                            isKOTReprint = true;
                            iKOTNo = kotNumber;
                            PrintKOT();
                            Clear(v);
//                            printKOT(v, kotNumber);
                        } else {
                            MsgBox.Show("Warning",
                                    "No KOT is present for the table number " + tableNumber);
                        }
                    }
                }).show();
    }


    /*************************************************************************************************************************************
     * Loads KOT order items to billing table
     *
     * @param crsrBillItems : Cursor with KOT order item details
     *************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    private void LoadItemsForReprintBill(Cursor crsrBillItems) {
        EditTextInputHandler etInputValidate = new EditTextInputHandler();
        TableRow rowItem;
        TextView tvHSn, tvName, tvAmount, tvTaxPercent, tvTaxAmt, tvDiscPercent, tvDiscAmt, // tvQty,
                // tvRate,
                tvDeptCode, tvCategCode, tvKitchenCode, tvTaxType, tvModifierCharge, tvServiceTaxPercent,
                tvServiceTaxAmt,tvUOM;
        EditText etQty, etRate;
        CheckBox Number;
        ImageButton ImgDelete;

        if (crsrBillItems.moveToFirst()) {
            if (crsrBillItems.getString(crsrBillItems.getColumnIndex("IsReverseTaxEnable")).equalsIgnoreCase("YES")) { // reverse tax
                REVERSETAX = true;
            }else
            {
                REVERSETAX = false;
            }

            if ((crsrSettings.getInt(crsrSettings.getColumnIndex("PrintOwnerDetail")) == 1)) { // print owner detail
                PRINTOWNERDETAIL = 1;
            }else
            {
                PRINTOWNERDETAIL = 0;
            }

            if ((crsrSettings.getInt(crsrSettings.getColumnIndex("BoldHeader")) == 1)) { // bold header
                BOLDHEADER = 1;
            }else
            {
                BOLDHEADER = 0;
            }

            if ((crsrSettings.getInt(crsrSettings.getColumnIndex("PrintService")) == 1)) { // bold header
                PRINTSERVICE = 1;
            }else
            {
                PRINTSERVICE = 0;
            }

            // Display items in table
            do {
                rowItem = new TableRow(myContext);
                rowItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Item Number
                Number = new CheckBox(myContext);
                Number.setWidth(40);
                Number.setTextSize(0);
                Number.setTextColor(Color.TRANSPARENT);
                Number.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemNumber")));

                // Item Name
                tvName = new TextView(myContext);
                tvName.setWidth(135);
                tvName.setTextSize(11);
                tvName.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ItemName")));

                //hsn code
                tvHSn = new TextView(myContext);
                tvHSn.setWidth(67); // 154px ~= 230dp
                tvHSn.setTextSize(11);
                if (GSTEnable.equalsIgnoreCase("1") && (HSNEnable_out != null) && HSNEnable_out.equals("1")) {
                    tvHSn.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("HSNCode")));
                }

                // Quantity
                etQty = new EditText(myContext);
                etQty.setWidth(55);
                etQty.setTextSize(11);
                etQty.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Quantity"))));
                //etQty.setOnClickListener(Qty_Rate_Click);
                etInputValidate.ValidateDecimalInput(etQty);

                // Rate
                etRate = new EditText(myContext);
                etRate.setWidth(75);
                etRate.setEnabled(false);
                etRate.setTextSize(11);
                etRate.setText(String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Value"))));

                // Amount
                tvAmount = new TextView(myContext);
                tvAmount.setWidth(105);
                tvAmount.setTextSize(11);
                tvAmount.setText(
                        String.format("%.2f", crsrBillItems.getDouble(crsrBillItems.getColumnIndex("Amount"))));

                // Sales Tax%
                tvTaxPercent = new TextView(myContext);
                tvTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CGSTRate")));

                // Sales Tax Amount
                tvTaxAmt = new TextView(myContext);
                tvTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CGSTAmount")));

                // Discount %
                tvDiscPercent = new TextView(myContext);
                tvDiscPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountPercent")));

                // Discount Amount
                tvDiscAmt = new TextView(myContext);
                tvDiscAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DiscountAmount")));

                // Dept Code
                tvDeptCode = new TextView(myContext);
                tvDeptCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("DeptCode")));

                // Categ Code
                tvCategCode = new TextView(myContext);
                tvCategCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("CategCode")));

                // Kitchen Code
                tvKitchenCode = new TextView(myContext);
                tvKitchenCode.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("KitchenCode")));

                // Tax Type
                tvTaxType = new TextView(myContext);
                tvTaxType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxType")));

                // Modifier Amount
                tvModifierCharge = new TextView(myContext);
                tvModifierCharge.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("ModifierAmount")));

                // Service Tax %
                tvServiceTaxPercent = new TextView(myContext);
                tvServiceTaxPercent.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SGSTRate")));

                // Service Tax Amount
                tvServiceTaxAmt = new TextView(myContext);
                tvServiceTaxAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SGSTAmount")));

                // Delete
                int res = getResources().getIdentifier("delete", "drawable", this.getPackageName());
                ImgDelete = new ImageButton(myContext);
                ImgDelete.setImageResource(res);
                ImgDelete.setVisibility(View.INVISIBLE);

                // UOM
                tvUOM = new TextView(BillingDineInActivity.this);
                tvUOM.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("UOM")));

                // space
                TextView tvSpace = new TextView(BillingDineInActivity.this);
                TextView tvSpace1 = new TextView(BillingDineInActivity.this);
                TextView tvPrintKOTStatus = new TextView(BillingDineInActivity.this);

                // SupplyType
                TextView SupplyType = new TextView(BillingDineInActivity.this);
                SupplyType.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("SupplyType")));

                // IGSTRate
                TextView tvIGSTRate = new TextView(BillingDineInActivity.this);
                tvIGSTRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTRate")));

                // IGSTAmount
                TextView tvIGSTAmt = new TextView(BillingDineInActivity.this);
                tvIGSTAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("IGSTAmount")));

                // cessRate
                TextView tvcess = new TextView(BillingDineInActivity.this);
                tvcess.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessRate")));

                // IGSTAmount
                TextView tvcessAmt = new TextView(BillingDineInActivity.this);
                tvcessAmt.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("cessAmount")));

                TextView originalRate = new TextView(this);
                originalRate.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("OriginalRate")));

                TextView taxableValue = new TextView(this);
                taxableValue.setText(crsrBillItems.getString(crsrBillItems.getColumnIndex("TaxableValue")));


                // Add all text views and edit text to Item Row
                // rowItem.addView(tvNumber);
                rowItem.addView(Number);
                rowItem.addView(tvName);
                rowItem.addView(tvHSn);
                rowItem.addView(etQty);
                rowItem.addView(etRate);
                rowItem.addView(tvAmount);
                rowItem.addView(tvTaxPercent);
                rowItem.addView(tvTaxAmt);
                rowItem.addView(tvDiscPercent);
                rowItem.addView(tvDiscAmt);
                rowItem.addView(tvDeptCode);
                rowItem.addView(tvCategCode);
                rowItem.addView(tvKitchenCode);
                rowItem.addView(tvTaxType);
                rowItem.addView(tvModifierCharge);
                rowItem.addView(tvServiceTaxPercent);
                rowItem.addView(tvServiceTaxAmt);//16
                rowItem.addView(SupplyType);//17
                rowItem.addView(tvSpace);//18
                rowItem.addView(ImgDelete);//19
                rowItem.addView(tvSpace1);//20
                rowItem.addView(tvPrintKOTStatus);//21
                rowItem.addView(tvUOM);//22
                rowItem.addView(tvIGSTRate);//23
                rowItem.addView(tvIGSTAmt);//24
                rowItem.addView(tvcess);//25
                rowItem.addView(tvcessAmt);//26
                rowItem.addView(originalRate);//27
                rowItem.addView(taxableValue);//28

                // Add row to table
                tblOrderItems.addView(rowItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            } while (crsrBillItems.moveToNext());

            //CalculateTotalAmountforRePrint();

        } else {
            Log.d("LoadKOTItems", "No rows in cursor");
        }
    }

    public void KOTStatus(View view) {

        if (jBillingMode == 1) {
            Intent intentDineIn = new Intent(myContext, KOTStatusActivity.class);
            intentDineIn.putExtra("jBillingMode", String.valueOf(jBillingMode));
            intentDineIn.putExtra("USER_NAME", strUserName);
            startActivity(intentDineIn);
        } else if (jBillingMode == 2) {
            Intent intentCounterSales = new Intent(myContext, KOTStatusActivity.class);
            intentCounterSales.putExtra("jBillingMode", String.valueOf(jBillingMode));
            intentCounterSales.putExtra("USER_NAME", strUserName);
            startActivity(intentCounterSales);
        } else if (jBillingMode == 3) {
            Intent intentPickUp = new Intent(myContext, CustomerOrdersActivity.class);
            intentPickUp.putExtra("BILLING_MODE", "3");
            intentPickUp.putExtra("USER_ID", strUserId);//spUser.getString("USER_ID", "GHOST"));
            intentPickUp.putExtra("USER_NAME", strUserName);//spUser.getString("USER_NAME", "GHOST"));
            //startActivity(intentPickUp);
            startActivityForResult(intentPickUp, 5);
            //this.finish();
        } else if (jBillingMode == 4) {
            Intent intentDelivery = new Intent(myContext, CustomerOrdersActivity.class);
            intentDelivery.putExtra("BILLING_MODE", "4");
            intentDelivery.putExtra("USER_ID", strUserId);//spUser.getString("USER_ID", "GHOST"));
            intentDelivery.putExtra("USER_NAME", strUserName);//spUser.getString("USER_NAME", "GHOST"));
            startActivityForResult(intentDelivery, 5);
            //this.finish();
        }

    }

    // Add Customer if Customer is not Found for Pickup and Home Delivery
    public void AddCustomer(View v) {
        try {
            if (edtCustName.getText().toString().equalsIgnoreCase("") || edtCustPhoneNo.getText().toString().equalsIgnoreCase("") || edtCustAddress.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please fill all details before adding customer");
            } else if (edtCustPhoneNo.getText().toString().length()!= 10)
            {
                MsgBox.Show("Warning", "Please fill 10 digit customer phone number");
                return;
            } else {
                Cursor crsrCust = dbBillScreen.getCustomer(edtCustPhoneNo.getText().toString());
                if (crsrCust.moveToFirst()) {
                    MsgBox.Show("Note", "Customer Already Exists");
                } else {
                    String gstin = etCustGSTIN.getText().toString().trim().toUpperCase();
                    if (gstin == null) {
                        gstin = "";
                    } // no new customer can be added here , therefore no gstin validation required here
                    InsertCustomer(edtCustAddress.getText().toString(), edtCustPhoneNo.getText().toString(),
                            edtCustName.getText().toString(), 0, 0, 0, gstin);
                    //ResetCustomer();
                    //MsgBox.Show("", "Customer Added Successfully");
                    Toast.makeText(myContext, "Customer Added Successfully", Toast.LENGTH_SHORT).show();
                    ControlsSetEnabled();

                }
            }
        } catch (Exception ex) {
            MsgBox.Show("Error", ex.getMessage());
        }
    }

    private void InsertCustomer(String strAddress, String strContactNumber, String strName, float fLastTransaction,
                                float fTotalTransaction, float fCreditAmount, String gstin) {
        long lRowId;

        Customer objCustomer = new Customer(strAddress, strName, strContactNumber, fLastTransaction, fTotalTransaction,
                fCreditAmount, gstin,0.00);

        lRowId = dbBillScreen.addCustomer(objCustomer);
        /*edtCustId.setText(String.valueOf(lRowId));*/

        if (edtCustPhoneNo.getText().toString().length() == 10) {
            Cursor crsrCust = dbBillScreen.getCustomer(edtCustPhoneNo.getText().toString());
            if (crsrCust.moveToFirst()) {
                customerId = crsrCust.getString(crsrCust.getColumnIndex("CustId"));
                edtCustId.setText(crsrCust.getString(crsrCust.getColumnIndex("CustId")));
            }
        }
        Log.d("Customer", "Row Id: " + String.valueOf(lRowId));

    }

    private void ResetCustomer() {
        edtCustName.setText("");
        edtCustPhoneNo.setText("");
        edtCustAddress.setText("");
        edtCustId.setText("0");
        edtCustDineInPhoneNo.setText("");
    }

    public void ControlsSetEnabled() {
        btnAddCustomer.setVisibility(View.VISIBLE);
        tvHSNCode_out.setEnabled(true);
        //txtSearchItemBarcode.setEnabled(true);
        tvOthercharges.setEnabled(true);
        btn_item_fastBillingMode.setEnabled(true);
        lstvwDepartment.setEnabled(true);
        lstvwCategory.setEnabled(true);
        //grdItems.setEnabled(true);
        //btnPayBill.setEnabled(true);
        btnSaveKOT.setEnabled(true);
        //btnDeliveryStatus.setEnabled(true);
        if(tblOrderItems.getChildCount()>0)
        {
            btnPayBill.setEnabled(true);
            btnPrintBill.setEnabled(true);
        }else
        {
            btnPayBill.setEnabled(false);
            btnPrintBill.setEnabled(false);
        }
        btnPrintKOT.setEnabled(true);
        //btnPrintBill.setEnabled(true);
        tblOrderItems.setEnabled(true);
        btnClear.setEnabled(true);
        btnReprint.setEnabled(true);
        aTViewSearchItem.setEnabled(true);
        //spnr_pos.setEnabled(true);
        aTViewSearchMenuCode.setEnabled(true);
        btndepart.setEnabled(true);
        btncateg.setEnabled(true);
        btnitem.setEnabled(true);
        if(jBillingMode==2) {
            btnPrintBill.setEnabled(true);
            btnPayBill.setEnabled(true);
        }

    }

    public void ControlsSetDisabled() {
        btnAddCustomer.setVisibility(View.VISIBLE);
        tvHSNCode_out.setEnabled(false);
        //txtSearchItemBarcode.setEnabled(false);
        tvOthercharges.setEnabled(false);
        btn_item_fastBillingMode.setEnabled(false);
        lstvwDepartment.setEnabled(false);
        lstvwCategory.setEnabled(false);
        //grdItems.setEnabled(false);
        btnPayBill.setEnabled(false);
        btnSaveKOT.setEnabled(false);
        //btnDeliveryStatus.setEnabled(false);
        btnPrintKOT.setEnabled(false);
        btnPrintBill.setEnabled(false);
        tblOrderItems.setEnabled(false);
        btnClear.setEnabled(false);
        btnReprint.setEnabled(false);
        aTViewSearchItem.setEnabled(false);
        spnr_pos.setEnabled(false);
        aTViewSearchMenuCode.setEnabled(false);
        btndepart.setEnabled(false);
        btncateg.setEnabled(false);
        btnitem.setEnabled(false);
        if(jBillingMode==2) {
            btnPrintBill.setEnabled(true);
            btnPayBill.setEnabled(true);
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
            final TextView tvAuthorizationUserId = (TextView) vwAuthorization.findViewById(R.id.tvAuthorizationUserId);
            final TextView tvAuthorizationUserPassword = (TextView) vwAuthorization.findViewById(R.id.tvAuthorizationUserPassword);
            tvAuthorizationUserId.setVisibility(View.GONE);
            tvAuthorizationUserPassword.setVisibility(View.GONE);
            txtUserId.setVisibility(View.GONE);
            txtPassword.setVisibility(View.GONE);
            AuthorizationDialog
                    .setIcon((R.drawable.ic_launcher))
                    .setTitle("Are you sure you want to exit ?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            db.close();
//                            dbBillScreen.CloseDatabase();
                            finish();
                        }
                    })
                    .show();
        }

        return super.onKeyDown(keyCode, event);
    }

    void addBarCodeItemToOrderTable()
    {
        String barcode = autoCompleteTextViewSearchItemBarcode.getText().toString().trim();
        System.out.println("Barcode = "+barcode);
        if(barcode == null || barcode.equals("") )
            return;
        Cursor crsr = dbBillScreen.getItem(barcode);
        if(crsr!=null && crsr.moveToFirst())
            AddItemToOrderTable(crsr);
        else
            MsgBox.Show("Oops ","Item not found");
        autoCompleteTextViewSearchItemBarcode.setText("");
        linefeed="";
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
                /*char pressedKey = (char) event.getUnicodeChar();
        String Barcode = "" + pressedKey;
        Toast.makeText(getApplicationContext(), "barcode--->>>" + Barcode, Toast.LENGTH_SHORT).show();

        Toast.makeText(myContext, "keyUp:"+keyCode+" : "+event.toString(), Toast.LENGTH_SHORT).show();*/
        long dd = event.getEventTime()-event.getDownTime();
        //System.out.println("Richa : "+event.getKeyCode()+" SC "+event.getScanCode());
        //Toast.makeText(myContext, "Richa : "+event.getKeyCode()+" keycode = "+keyCode, Toast.LENGTH_SHORT).show();
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            System.out.println("Richa : Enter encountered for barcode");
            addBarCodeItemToOrderTable();
        }else if (event.getKeyCode() == KeyEvent.KEYCODE_J ||event.getKeyCode() == KeyEvent.KEYCODE_CTRL_LEFT   )
        //}else if (event.getKeyCode() == KeyEvent.KEYCODE_J ||event.getKeyCode() == KeyEvent.KEYCODE_CTRL_LEFT ||event.getKeyCode() == KeyEvent.KEYCODE_SHIFT_LEFT  )
        {
            linefeed +=String.valueOf(event.getKeyCode());
            if(linefeed.equalsIgnoreCase("38113")|| linefeed.equalsIgnoreCase("11338")) // line feed value
                addBarCodeItemToOrderTable();
        }else
        {
            linefeed = "";
            if (dd<15 && dd >0 && CUSTOMER_FOUND==0) {
                {
                    View v = getCurrentFocus();
                    //System.out.println(v);
                    //EditText etbar = (EditText)findViewById(R.id.etItemBarcode);
                    //EditText ed = (WepEditText)findViewById(v.getId());

                    if (v.getId()!= R.id.aCTVSearchItemBarcode)
                    {
                        switch (v.getId())
                        {
                            case R.id.aCTVSearchItem :aTViewSearchItem.setText(tx);
                                break;
                            case R.id.aCTVSearchMenuCode:  aTViewSearchMenuCode.setText(tx);
                                break;

                            case R.id.etCustGSTIN:
                            case R.id.edtCustName:
                            case R.id.edtCustDineInPhoneNo:
                            case R.id.edtCustAddress:
                                EditText ed = (EditText)findViewById(v.getId());
                                ed.setText(tx);
                        }
                        String bar_str = autoCompleteTextViewSearchItemBarcode.getText().toString();
                        bar_str += (char)event.getUnicodeChar();
                        autoCompleteTextViewSearchItemBarcode.setText(bar_str.trim());
                        autoCompleteTextViewSearchItemBarcode.showDropDown();

                    }else if (v.getId()== R.id.aCTVSearchItemBarcode){
                        tx += (char)event.getUnicodeChar();
                        autoCompleteTextViewSearchItemBarcode.setText(tx.trim());
                        autoCompleteTextViewSearchItemBarcode.showDropDown();
                    }
                }}}

        return true;
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.wep.common.app.R.menu.menu_main, menu);
        for (int j = 0; j < menu.size(); j++) {
            MenuItem item = menu.getItem(j);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }else if (id == com.wep.common.app.R.id.action_home) {
            //ActionBarUtils.navigateHome(this);
            finish();
        }else if (id == com.wep.common.app.R.id.action_screen_shot) {
            com.wep.common.app.ActionBarUtils.takeScreenshot(this,findViewById(android.R.id.content).getRootView());
        }
        return super.onOptionsItemSelected(item);
    }


    /*************************************************************************************************************************************
     * Item grid image click listener which loads the item to billing grid if
     * selected item is not present otherwise increments the quantity by one
     *************************************************************************************************************************************/
    @Override
    public void onItemClick(int position,int itemCode, View v) {

        Cursor Item = null;
        if (jBillingMode == Byte.parseByte("1")) {
            if (tvTableNumber.getText().toString().equalsIgnoreCase("")
                    || tvWaiterNumber.getText().toString().equalsIgnoreCase("")
                    || tvTableNumber.getText().toString().equalsIgnoreCase("0")
                    || tvWaiterNumber.getText().toString().equalsIgnoreCase("0")) {
                Toast.makeText(myContext, "Select waiter and table before adding item to bill", Toast.LENGTH_LONG).show();

                //Table_Waiter(null);
                Intent intentDineIn = new Intent(myContext, TableActivity.class);
                intentDineIn.putExtra("BILLING_MODE", String.valueOf(jBillingMode));
                intentDineIn.putExtra("USER_ID", strUserId);//spUser.getString("USER_ID", "GHOST"));
                intentDineIn.putExtra("USER_NAME", strUserName);//spUser.getString("USER_NAME", "GHOST"));
                intentDineIn.putExtra("CUST_ID", 0);
                startActivity(intentDineIn);
                finish();
                return;
            } else {
                Item = dbBillScreen.getItem(itemCode);
                btnClear.setEnabled(true);
                AddItemToOrderTable(Item);
            }
        }
    }

    @Override
    public void onError(Printer printer, int errCode, String errMsg) {

    }

    @Override
    public void onError(BixolonPrinter printer, String errMsg) {

    }

    @Override
    public void onError(String errMsg) {
        configureUsbPrinter();
    }

    @Override
    public void onError(int iError) {

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

    public String getPrinterName(String module) {
        return Preferences.getSharedPreferencesForPrint(BillingDineInActivity.this).getString(module, "--Select--");
    }

    @Override
    public void onLoginCompleted(boolean success) {
        try {
            if (success) {
                PayBillFragment frag = (PayBillFragment) getSupportFragmentManager().findFragmentByTag("Proceed To Pay");
                if (frag != null) {
                    Intent intent = new Intent(this, MSwipePaymentActivity.class);
                    intent.putExtra("amount", frag.tvDifferenceAmount.getText().toString());
                    intent.putExtra("phone", frag.phoneReceived);
                    startActivityForResult(intent, REQUEST_CODE_CARD_PAYMENT);
                }
            } else {
                Toast.makeText(this, "Login failed. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            Log.i(TAG,"Unable to navigate to mSwipe payment screen on error : onLoginCompleted()." +ex.getMessage());
        }
    }

    @Override
    public void onChangePassword(boolean success) {
        try{
            PayBillFragment frag = (PayBillFragment)getSupportFragmentManager().findFragmentByTag("Proceed To Pay");
            Intent intent = new Intent(this, PasswordChangeActivity.class);
            intent.putExtra("amount", frag.txt);
            startActivity(intent);
        }catch (Exception ex){
            Log.i(TAG,"Unable to navigate to PasswordChangeActivity screen on error : onChangePassword()." +ex.getMessage());
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        PayBillFragment frag = (PayBillFragment)getSupportFragmentManager().findFragmentByTag("Proceed To Pay");
        OnWalletPaymentResponseListener walletListner =  frag;
        walletListner.onWalletPaymentSuccessListener(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        PayBillFragment frag = (PayBillFragment)getSupportFragmentManager().findFragmentByTag("Proceed To Pay");
        OnWalletPaymentResponseListener walletListner =  frag;
        walletListner.onWalletPaymentErrorListener(i,s);
    }

    void checkForInterstateTransaction(String posCode) {
        if (!posCode.equalsIgnoreCase(OWNERPOS)) {
            chk_interstate.setChecked(true);
            spnr_pos.setSelection(getIndex_pos(posCode));
            Toast.makeText(this, getString(R.string.autosetting_cust_State_message), Toast.LENGTH_SHORT).show();

        } else {
            chk_interstate.setChecked(false);
        }
    }

    @Override
    public void onProceedToPayComplete(PaymentDetails obj) {
        boolean isDiscounted, isPrintBill = false;
        double dDiscPercent;

        custPhone = obj.getCustPhoneNo();
        Cursor cursor = db.getCustomerbyPhone(custPhone);
        if (cursor != null && cursor.moveToFirst()) {
            customerId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CustId));
            edtCustId.setText(customerId);
            edtCustName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CustName)));
            if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)) != null && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)).equals("")) {
                etCustGSTIN.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                checkForInterstateTransaction(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)).substring(0, 2));
            }
        } else {
            customerId = "0";
        }

        /*if(!tv_billing_CustId.getText().toString().equals(""))
            customerId = tv_billing_CustId.getText().toString();
        else
            customerId ="0";*/

        isPrintBill = obj.getToPrintBill();
        dDiscPercent = obj.getTotalDiscountPercent();

        dblCashPayment = obj.getCashAmount();
        dblAEPSAmount = obj.getAepsAmount();
        dblCardPayment = obj.getCardAmount();
        dblCouponPayment = obj.getCouponAmount();
        dblPettyCashPayment = obj.getPettyCash();
        dblPaidTotalPayment = obj.getTotalPaidAmount();
        dblWalletPayment = obj.getWalletAmount();
        dblRewardPointsAmount = obj.getRewardPoints();
        dblMSwipeAmount = obj.getmSwipeAmount();
        dblPaytmAmount = obj.getmPaytmAmount();
        dblChangePayment = obj.getTotalreturnAmount();
        dblRoundOfValue = obj.getTotalRoundOff();
        dFinalBillValue = obj.getTotalFinalBillAmount();
        isDiscounted = obj.getisDiscounted();
        dblDiscountAmount = obj.getTotalDiscountAmount();

        if (isDiscounted == true) {
            PRINT_DISCOUNT = 1;
            Log.v("PayBill Result", "Discounted:" + isDiscounted);
            Log.v("PayBill Result", "Discount Amount:" + dblDiscountAmount);
            tvDiscountAmount.setText(String.valueOf(dblDiscountAmount));
            //edtDiscountPercent.setText(String.valueOf(dDiscPercent));

            double igst = obj.getTotalIGSTAmount();
            double cgst = obj.getTotalCGSTAmount();
            double sgst = obj.getTotalSGSTAmount();
            double cess = obj.getTotalcessAmount();
            double billtot = obj.getTotalBillAmount();
            if (billtot > 0) {
                tvBillAmount.setText(String.format("%.2f", billtot));
                tvcessValue.setText(String.format("%.2f", cess));
                if (chk_interstate.isChecked()) {
                    tvIGSTValue.setText(String.format("%.2f", igst));
                    tvCGSTValue.setText("0.00");
                    tvSGSTValue.setText("0.00");
                } else {
                    tvIGSTValue.setText("0.00");
                    tvCGSTValue.setText(String.format("%.2f", cgst));
                    tvSGSTValue.setText(String.format("%.2f", sgst));
                }

            }

            ArrayList<AddedItemsToOrderTableClass> orderList_recieved = obj.getOrderList();
            double taxableValue = 0.00;

            for(int i =0;i<tblOrderItems.getChildCount();i++)
            {
                TableRow RowBillItem = (TableRow) tblOrderItems.getChildAt(i);

                // Item Number
                if (RowBillItem.getChildAt(0) != null) {
                    CheckBox ItemNumber = (CheckBox) RowBillItem.getChildAt(0);
                    int menucode = (Integer.parseInt(ItemNumber.getText().toString()));
                    for(AddedItemsToOrderTableClass item : orderList_recieved) {
                        if(item.getMenuCode() == menucode) {
                            if (RowBillItem.getChildAt(9) != null ) {
                                TextView DiscountAmount = (TextView) RowBillItem.getChildAt(9);
                                DiscountAmount.setText(String.format("%.2f",item.getDiscountamount()));
                            }
                            if (RowBillItem.getChildAt(16) != null) {
                                TextView ServiceTaxAmount = (TextView) RowBillItem.getChildAt(16);
                                ServiceTaxAmount.setText(String.format("%.2f",item.getSgstAmt()));
                            }
                            // Sales Tax Amount
                            if (RowBillItem.getChildAt(7) != null) {
                                TextView SalesTaxAmount = (TextView) RowBillItem.getChildAt(7);
                                SalesTaxAmount.setText(String.format("%.2f",item.getCgstAmt()));
                            }
                            if (RowBillItem.getChildAt(24) != null) {
                                TextView IGSTTaxAmount = (TextView) RowBillItem.getChildAt(24);
                                IGSTTaxAmount.setText(String.format("%.2f",item.getIgstAmt()));
                            }
                            if (RowBillItem.getChildAt(26) != null) {
                                TextView cessAmount = (TextView) RowBillItem.getChildAt(26);
                                cessAmount.setText(String.format("%.2f",item.getCessAmt()));
                            }
                            double quantity_dd =0.00;
                            if (RowBillItem.getChildAt(3) != null ) {
                                TextView qty = (TextView) RowBillItem.getChildAt(3);
                                quantity_dd = qty.getText().toString().trim().equals("")?0.00:
                                        Double.parseDouble(String.format("%.2f",Double.parseDouble(qty.getText().toString().trim())));
                                //System.out.println("Richa :  quantity "+quantity_dd);
                            }
                            if (RowBillItem.getChildAt(5) != null) {
                                TextView Amount = (TextView) RowBillItem.getChildAt(5);
                                Amount.setText(String.format("%.2f",item.getTaxableValue()*quantity_dd));
                                //System.out.println("Richa :  amt "+(item.getTaxableValue()*quantity_dd));
                            }
                            if (RowBillItem.getChildAt(28) != null) {
                                TextView TaxableVal = (TextView) RowBillItem.getChildAt(28);
                                TaxableVal.setText(String.format("%.2f",item.getTaxableValue()*quantity_dd));
                                //System.out.println("Richa :  amt "+(item.getTaxableValue()*quantity_dd));
                                taxableValue += item.getTaxableValue()*quantity_dd;
                            }
                        }
                    }
                }
            }

            tvSubTotal.setText(String.format("%.2f", taxableValue));
        }
        tvBillAmount.setText(String.format("%.2f", dFinalBillValue));

        PrintBillPayment = 0;
        mSaveBillData(2);
        generateInvoicePdf();
//        generateInvoicePdf();
      /*  if (SHAREBILL == 1) {
            String billNo = "";
            if (trainingMode)
                billNo = "TM" + edtBillNumber.getText().toString().trim();
            else
                billNo = edtBillNumber.getText().toString().trim();
            sendInvoice(customerBean, String.format("%.2f", dFinalBillValue), billNo);
        }*/
        //updateOutwardStock();
        Toast.makeText(this, "Bill saved Successfully", Toast.LENGTH_SHORT).show();
        if (isPrintBill == true) {
            strPaymentStatus = "Paid";
            PrintNewBill(BUSINESS_DATE, obj.getNoOfPrint());
        }
        ClearAll();
        proceedToPayBillingFragment = null;
    }

    @Override
    public void onDismiss() {
        proceedToPayBillingFragment = null;
    }
}


/*************************************************************************************************************************************
 * End Of File
 * <p>
 * <p>
 * /************************************************************
 * End Of File
 * <p>
 * <p>
 * /
 *************************************************************************************************************************************/