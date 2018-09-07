/****************************************************************************
 * Project Name		:	VAJRA
 * 
 * File Name		:	PaymentReceiptActivity
 * 
 * Purpose			:	Represents Payment Receipt activity, takes care of all
 * 						UI back end operations in this activity, such as event
 * 						handling data read from or display in views.
 * 
 * DateOfCreation	:	14-November-2012
 * 
 * Author			:	Balasubramanya Bharadwaj B S
 * 
 ****************************************************************************/
package com.wepindia.pos.views.PaymentReceipt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;
import com.epson.epos2.printer.Printer;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.PaymentReceipt;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.utils.Preferences;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.EditTextInputHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.PrintClasses.SendDataToSerialPort;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.InputFilterForDoubleMinMax;
import com.wepindia.pos.views.Billing.BillingCounterSalesActivity;
import com.wepindia.printers.BixolonPrinterBaseAcivity;
import com.wepindia.printers.EPSONPrinterBaseActivity;
import com.wepindia.printers.TVSPrinterBaseActivity;
import com.wepindia.printers.WePTHPrinterBaseActivity;
import com.wepindia.printers.WepPrinterBaseActivity;
import com.wepindia.printers.wep.PrinterConnectionError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentReceiptActivity extends WepPrinterBaseActivity implements PrinterConnectionError {

	Context myContext;

	@BindView(R.id.rbPayment)                       RadioButton rbPayment;
	@BindView(R.id.rbReceipt)                       RadioButton rbReceipt;
	@BindView(R.id.etPaymentReceiptNo)              EditText etPaymentReceiptNo;
	@BindView(R.id.etPaymentReceiptDate)            EditText etPaymentReceiptDate;
	@BindView(R.id.btn_PaymentReceiptDate) 			Button btn_PaymentReceiptDate;
	@BindView(R.id.spnrDescription1)                Spinner spnrDescription1;
	@BindView(R.id.etPaymentReceiptReason)          EditText etPaymentReceiptReason;
	@BindView(R.id.etPaymentReceiptAmount)          EditText etPaymentReceiptAmount;
	@BindView(R.id.bt_pay_receipt_add)              Button bt_pay_receipt_add;
	@BindView(R.id.bt_pay_receipt_add_print)        Button bt_pay_receipt_add_print;
	@BindView(R.id.bt_pay_receipt_reprint)          Button bt_pay_receipt_reprint;
	@BindView(R.id.bt_pay_receipt_clear)            Button bt_pay_receipt_clear;
	
	// DatabaseHandler object
	DatabaseHandler dbPaymentReceipt = new DatabaseHandler(PaymentReceiptActivity.this);
	// DateTime object
	Date objDate;
	Date d, d1;
	Calendar Calobj;
	int DateChange =0;
	int BOLD_HEADER = 0;
				
	// Variables
	ArrayAdapter<String> adapDescriptionText;
	Cursor crsrSettings;
	String strPaymentReceiptDate = "", strUserName = "", BUSINESS_DATE = "";
	MessageDialog msgBox;
	private Toolbar toolbar;
	public boolean isPrinterAvailable = false;

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private TVSPrinterBaseActivity tvsPrinterBaseActivity = null;


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
		Toast.makeText(PaymentReceiptActivity.this, "Bill Printer Status : " + status, Toast.LENGTH_SHORT).show();
		isPrinterAvailable = flag;
		//btn_PrintBill.setEnabled(true);
		//btn_Reprint.setEnabled(true);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_paymentreceipt);
		ButterKnife.bind(this);
		msgBox = new MessageDialog(this);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        myContext = this;
		strUserName = getIntent().getStringExtra("USER_NAME");
		//tvTitleUserName.setText(strUserName.toUpperCase());
		d = new Date();
		CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
		//tvTitleDate.setText("Date : " + s);
        
        EditTextInputHandler etInputValidate =  new EditTextInputHandler();

		sharedPreferences = Preferences.getSharedPreferencesForPrint(PaymentReceiptActivity.this); // getSharedPreferences("PrinterConfigurationActivity", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		if (getPrinterName(this, "bill").equalsIgnoreCase("NGX")) {
			mConnectNGX();
		}
        
        try{
        	dbPaymentReceipt.CreateDatabase();
        	dbPaymentReceipt.OpenDatabase();
        	crsrSettings = dbPaymentReceipt.getBillSetting();
        	if(!crsrSettings.moveToFirst()){
        		Log.d("PaymentReceipt", "No Settings table data");
        	}
			else
			{
				DateChange = (crsrSettings.getInt(crsrSettings.getColumnIndex("DateAndTime")));
				BUSINESS_DATE = crsrSettings.getString(crsrSettings.getColumnIndex("BusinessDate"));
				BOLD_HEADER = crsrSettings.getInt(crsrSettings.getColumnIndex(DatabaseHandler.KEY_HeaderBold));
				if(BUSINESS_DATE!=null && !BUSINESS_DATE.equals(""))
				{
					Calobj = convertDate(BUSINESS_DATE,"dd-MM-yyyy");
					etPaymentReceiptDate.setText(BUSINESS_DATE);
				} else {
					objDate = new Date();
					Calobj.setTime(objDate);
				}
			}
			initDescriptions();
			etPaymentReceiptAmount.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
			com.wep.common.app.ActionBarUtils.setupToolbar(PaymentReceiptActivity.this,toolbar,getSupportActionBar(),"Payment/Receipt",strUserName," Date:"+s.toString());

        }
        catch(Exception exp){
        	Toast.makeText(myContext, "OnCreate: " + exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

	@OnClick({R.id.btn_PaymentReceiptDate, R.id.bt_pay_receipt_add, R.id.bt_pay_receipt_add_print, R.id.bt_pay_receipt_reprint, R.id.bt_pay_receipt_clear})
	protected void OnClickEvent(View view) {
		switch (view.getId()) {
			case R.id.btn_PaymentReceiptDate:
				DateSelection(etPaymentReceiptDate);
				break;
			case R.id.bt_pay_receipt_add:
				SavePaymentReceipt(false);
				break;
			case R.id.bt_pay_receipt_add_print:
				SavePaymentReceipt(true);
				break;
			case R.id.bt_pay_receipt_reprint:
				RePrintPaymentReceipt();
				break;
			case R.id.bt_pay_receipt_clear:
				ClearData();
				break;
			default:
				break;
		}
	}

	private long SavePaymentReceipt(boolean isPrint){
		int iBillType = 0;
		String strReason = "", strDate = "", strAmount = "", paymentReceiptNo = "";

		// get reason and amount from text box
		paymentReceiptNo = etPaymentReceiptNo.getText().toString();
		strReason = etPaymentReceiptReason.getText().toString();
		strAmount = etPaymentReceiptAmount.getText().toString();
		String temp_date = etPaymentReceiptDate.getText().toString();
		Calendar cal = convertStringToCalendar(temp_date);
		strDate = String.valueOf(cal.getTimeInMillis());

		// get bill type based  on radio button selection
		if(rbPayment.isChecked()){
			iBillType = 1;	// Payment Bill Type
		}
		else{
			iBillType = 2;	// Receipt Bill Type
		}

		long l = -1;

		if (paymentReceiptNo.equalsIgnoreCase("")) {
			Toast.makeText(myContext, "Please enter Payment/Receipt No.", Toast.LENGTH_SHORT).show();
			return l;
		}

		Cursor checkDuplicate = dbPaymentReceipt.checkDuplicatePaymentReceipt(Integer.parseInt(paymentReceiptNo), strDate, iBillType);

		if (checkDuplicate != null && checkDuplicate.moveToFirst()) {
			Toast.makeText(myContext, "Payment/Receipt No. already exist for same date.", Toast.LENGTH_SHORT).show();
			return l;
		}

		if(strAmount.equalsIgnoreCase("")){
			Toast.makeText(myContext, "Please enter amount before saving", Toast.LENGTH_SHORT).show();
			return l;
		}
		if (spnrDescription1.getSelectedItem().toString().equalsIgnoreCase("Select")) {
			Toast.makeText(myContext, "Please select description before saving", Toast.LENGTH_SHORT).show();
			return l;
		}
		if (strAmount.equalsIgnoreCase(".")) {
			etPaymentReceiptAmount.setText("0.0");
			strAmount = "0.0";
		}
		if (Double.parseDouble(strAmount) <= 0) {
			Toast.makeText(myContext, "Please enter amount more than zero", Toast.LENGTH_SHORT).show();
			return l;
		}

		PaymentReceipt paymentReceipt = new PaymentReceipt();

		paymentReceipt.setPaymentReceiptNo(Integer.parseInt(paymentReceiptNo));
		paymentReceipt.setStrReason(strReason);
		paymentReceipt.setdAmount(Double.parseDouble(String.format("%.2f", Double.parseDouble(strAmount))));
		paymentReceipt.setStrDate(strDate);
		paymentReceipt.setiBillType(iBillType);
		paymentReceipt.setDescriptionText(spnrDescription1.getSelectedItem().toString());

		l = InsertPaymentReceipt(paymentReceipt.getPaymentReceiptNo(),
				paymentReceipt.getStrReason(),
				paymentReceipt.getdAmount(),
				paymentReceipt.getStrDate(),
				paymentReceipt.getiBillType(),
				paymentReceipt.getDescriptionText());

		if (l>0){
			Toast.makeText(myContext, "Payments/Receipts Saved Successfully", Toast.LENGTH_SHORT).show();
			if (isPrint) {
				String dateformat = "dd-MM-yyyy";
				String invoicedate = getDate(Long.parseLong(paymentReceipt.getStrDate()), dateformat);
				paymentReceipt.setStrDate(invoicedate);
//                if (((HomeActivity) myContext).isPrinterAvailable) {
				PrintPaymentReceipt(paymentReceipt);
//                } else {
//                    Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
//                }
			}
			ClearData();
		} else {
			Toast.makeText(myContext, "Error : Payments/Receipts cannot be saved", Toast.LENGTH_SHORT).show();
		}

		return l;
	}

	private Calendar convertDate(String startDateString, String format)
	{

		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar startDate= null;
		try {
			startDate =  Calendar.getInstance();
			startDate.setTime(df.parse(startDateString));
			//String newDateString = df.format(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return startDate;
	}

	private void DateSelection(final TextView tv_inv_date){
		try {

			AlertDialog.Builder dlgReportDate = new AlertDialog.Builder(myContext);
			final DatePicker dateReportDate = new DatePicker(myContext);

			int year = Calobj.get(Calendar.YEAR);
			int month = Calobj.get(Calendar.MONTH);
			int day = Calobj.get(Calendar.DAY_OF_MONTH);

			dateReportDate.updateDate(year, month, day);

			String strMessage = "";

			dlgReportDate
					.setIcon(R.drawable.ic_launcher)
					.setTitle("Date Selection")
					.setMessage(strMessage)
					.setView(dateReportDate)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String strDate = "";
							if (dateReportDate.getDayOfMonth() < 10) {
								strDate = "0" + String.valueOf(dateReportDate.getDayOfMonth())+"-";
							} else {
								strDate = String.valueOf(dateReportDate.getDayOfMonth())+"-";
							}
							if (dateReportDate.getMonth() < 9) {
								strDate += "0" + String.valueOf(dateReportDate.getMonth() + 1) + "-";
							} else {
								strDate += String.valueOf(dateReportDate.getMonth() + 1) + "-";
							}

							strDate += String.valueOf(dateReportDate.getYear());
							tv_inv_date.setText(strDate);

							Log.d("PaymentReceipt", "Date:" + strDate);
						}
					})
					.setNegativeButton("Cancel", null)
					.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private long InsertPaymentReceipt(int paymentReceiptNo, String Reason,
									  Double Amount,
									  String Date,
									  int BillType,
									  String DescriptionText){
		long lRowId = 0;
		PaymentReceipt objPaymentReceipt = new PaymentReceipt(paymentReceiptNo,Date,Reason,BillType,Amount,DescriptionText);
		lRowId = dbPaymentReceipt.addPaymentReceipt(objPaymentReceipt);

		Log.d("PaymentReceipt","Row Id: " + String.valueOf(lRowId));
		return lRowId;
	}

	private void RePrintPaymentReceipt() {
		AlertDialog.Builder DineInTenderDialog = new AlertDialog.Builder(myContext);

		LayoutInflater UserAuthorization = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rePrintDialog = UserAuthorization.inflate(R.layout.payment_receipt_reprint_dialog, null);

		final ImageButton btnCal_reprint = (ImageButton) rePrintDialog.findViewById(R.id.btnCal_reprint);
		final EditText txtReprintBillNo = (EditText) rePrintDialog.findViewById(R.id.txtDineInReprintBillNumber);
		final TextView tv_inv_date = (TextView) rePrintDialog.findViewById(R.id.tv_inv_date);
		final RadioButton rbPayment = (RadioButton) rePrintDialog.findViewById(R.id.rbPayment);
		final RadioButton rbReceipt = (RadioButton) rePrintDialog.findViewById(R.id.rbReceipt);

		tv_inv_date.setText(BUSINESS_DATE);
		btnCal_reprint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DateSelection(tv_inv_date);
			}
		});

		DineInTenderDialog.setIcon(R.drawable.ic_launcher).setTitle("RePrint Payment/Receipt")
				.setView(rePrintDialog).setNegativeButton("Cancel", null)
				.setPositiveButton("RePrint", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						if (txtReprintBillNo.getText().toString().equalsIgnoreCase("")) {
							msgBox.Show("Warning", "Please enter Bill Number");
							return;
						} else if (tv_inv_date.getText().toString().equalsIgnoreCase("")) {
							msgBox.Show("Warning", "Please enter Bill Date");
							return;
						} else {
							try {

								PaymentReceipt item;

								int iBillType = 0;
								if(rbPayment.isChecked()){
									iBillType = 1;	// Payment Bill Type
								}
								else{
									iBillType = 2;	// Receipt Bill Type
								}
								int billNo = Integer.valueOf(txtReprintBillNo.getText().toString());
								String date_reprint = tv_inv_date.getText().toString();
								Date date = new SimpleDateFormat("dd-MM-yyyy").parse(date_reprint);

								Cursor LoadPaymentReceiptForReprint = dbPaymentReceipt.getPaymentReceipt(billNo, date.getTime()+"", iBillType);

								if (LoadPaymentReceiptForReprint.moveToFirst()) {

									item = new PaymentReceipt();

									item.setIsDuplicate("(Duplicate)");
									item.setiBillType(LoadPaymentReceiptForReprint.getInt(LoadPaymentReceiptForReprint.getColumnIndex("BillType")));
									item.setPaymentReceiptNo(LoadPaymentReceiptForReprint.getInt(LoadPaymentReceiptForReprint.getColumnIndex(DatabaseHandler.KEY_PAYMENT_RECEIPT_NO)));
									String dateformat = "dd-MM-yyyy";
									String invoicedate = getDate(Long.parseLong(LoadPaymentReceiptForReprint.getString(LoadPaymentReceiptForReprint.getColumnIndex(DatabaseHandler.KEY_InvoiceDate))), dateformat);
									item.setStrDate(invoicedate);
									item.setDescriptionText(LoadPaymentReceiptForReprint.getString(LoadPaymentReceiptForReprint.getColumnIndex(DatabaseHandler.KEY_DescriptionText)));
									item.setdAmount(LoadPaymentReceiptForReprint.getDouble(LoadPaymentReceiptForReprint.getColumnIndex(DatabaseHandler.KEY_Amount)));
									item.setStrReason(LoadPaymentReceiptForReprint.getString(LoadPaymentReceiptForReprint.getColumnIndex(DatabaseHandler.KEY_Reason)));

								} else {
									msgBox.Show("Warning", "No deatils present for the Payment/Receipt No " + txtReprintBillNo.getText().toString() + ", Dated :" + tv_inv_date.getText().toString());
									return;
								}
								if (item != null)
									PrintPaymentReceipt(item);

								ClearData();
							} catch (Exception e) {

							}
						}
					}
				}).show();
	}

	private void PrintPaymentReceipt(PaymentReceipt item) {
		Cursor crsrHeaderFooterSetting = null;
		if (item.getIsDuplicate() == null)
			item.setIsDuplicate("");
		item.setHeaderPrintBold(BOLD_HEADER);
		try {
			crsrHeaderFooterSetting = dbPaymentReceipt.getBillSettings();

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
				Log.d("Print_Payment_Receipt", "DisplayHeaderFooterSettings No data in BillSettings table");
			}
		}catch (Exception ex){
			Log.e("Print_Payment_Receipt","Unable to fetch header details from billSettings table. From method PrintNewBill()." +ex.getMessage());
		} finally {
			if(crsrHeaderFooterSetting != null){
				crsrHeaderFooterSetting.close();
			}
		}

		String prf = Preferences.getSharedPreferencesForPrint(PaymentReceiptActivity.this).getString("receipt", "--Select--");

		if (prf.equalsIgnoreCase(Constants.USB_EPSON_PRINTER_NAME)) {
			String target = Preferences.getSharedPreferencesForPrint(PaymentReceiptActivity.this).getString(prf, "--Select--");
			EPSONPrinterBaseActivity epson = new EPSONPrinterBaseActivity();

			epson.setmTarget(target);
			epson.setmContext(myContext);
			epson.mInitListener(this);

			if (epson.runPrintReceiptSequence(item, "Invoice")) {
//                            progressDialog.dismiss();
				Toast.makeText(myContext, "Payment/Receipt Printed", Toast.LENGTH_SHORT).show();
			}

		} else  if (prf.equalsIgnoreCase("Heyday"))  {
			if(((PaymentReceiptActivity) myContext).isPrinterAvailable) {
				((PaymentReceiptActivity) myContext).printHeydeyPaymentReceipt(item, "Invoice");
				Toast.makeText(myContext, "Payment/Receipt Printed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
			}
		} else  if (prf.equalsIgnoreCase("NGX"))  {
			if(((PaymentReceiptActivity) myContext).isPrinterAvailable) {
				((PaymentReceiptActivity) myContext).printNGXPaymentReceipt(item, "Invoice");
				Toast.makeText(myContext, "Payment/Receipt Printed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(myContext, "Printer is not ready", Toast.LENGTH_SHORT).show();
			}
		} else if (prf.equalsIgnoreCase(Constants.USB_BIXOLON_PRINTER_NAME)) {
			String target = Preferences.getSharedPreferencesForPrint(PaymentReceiptActivity.this).getString(prf, "--Select--");
			BixolonPrinterBaseAcivity bixolon = new BixolonPrinterBaseAcivity();

			bixolon.setmTarget(target);
			bixolon.setmContext(myContext);
			bixolon.mInitListener(this);

			if (bixolon.runPrintReceiptSequence(item)) {
				Toast.makeText(myContext, "Payment/Receipt Printed", Toast.LENGTH_SHORT).show();
			}

		} else if(prf.equalsIgnoreCase(Constants.USB_WEP_PRINTER_NAME)) {
			String target = Preferences.getSharedPreferencesForPrint(PaymentReceiptActivity.this).getString(prf, "--Select--");

			WePTHPrinterBaseActivity wepPrinter = new WePTHPrinterBaseActivity();

			wepPrinter.setmTarget(target);
			wepPrinter.setmContext(myContext);
			wepPrinter.mInitListener(this);

			if (wepPrinter.runPrintReceiptSequence(item)) {
				Toast.makeText(myContext, "Payment/Receipt Printed", Toast.LENGTH_SHORT).show();
			}

		} else if(prf.equalsIgnoreCase(Constants.USB_TVS_PRINTER_NAME)){
			String target = Preferences.getSharedPreferencesForPrint(PaymentReceiptActivity.this).getString(prf, "--Select--");

			tvsPrinterBaseActivity = new TVSPrinterBaseActivity();
			tvsPrinterBaseActivity.setmTarget(target);
			tvsPrinterBaseActivity.setmContext(myContext);
			tvsPrinterBaseActivity.mInitListener(this);

			if (tvsPrinterBaseActivity.runPrintReceiptSequence(item)) {
				Toast.makeText(myContext, "Payment/Receipt Printed", Toast.LENGTH_SHORT).show();
			}
		} else if(prf.equalsIgnoreCase(Constants.USB_WiFi_PRINTER_NAME)){
			msgBox.Show("Warning", "Payment/Receipt can't be printed on WiFi printer. Please configure Bluetooth/USB printers.");
		}

	}

	private void initDescriptions(){

		try{
			Cursor crsrDescription = dbPaymentReceipt.getAllDescription();

			adapDescriptionText = new ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_item);
			adapDescriptionText.add("Select");
			adapDescriptionText.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spnrDescription1.setAdapter(adapDescriptionText);
//            spnrDescription2.setAdapter(adapDescriptionText);
//            spnrDescription3.setAdapter(adapDescriptionText);

			if(crsrDescription.moveToFirst()){
				do{
					adapDescriptionText.add(crsrDescription.getString(1));
				}while(crsrDescription.moveToNext());
			}
			else{
				Log.d("SpinnerData", "No Description Text");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    Calendar convertStringToCalendar(String selectedDate)
    {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        Calendar cal=Calendar.getInstance();

        Date date = null;
        try {
            date = df.parse(selectedDate);
            cal.setTime(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

	private void ResetPaymentReceipt(){
		etPaymentReceiptNo.setText("");
		etPaymentReceiptReason.setText("");
		etPaymentReceiptAmount.setText("");
	}

	void ClearData(){
		ResetPaymentReceipt();
		String date_str = crsrSettings.getString(crsrSettings.getColumnIndex("BusinessDate"));
		etPaymentReceiptDate.setText(date_str);
		spnrDescription1.setSelection(0);
	}

	public static String getDate(long milliSeconds, String dateFormat)
	{
		// Create a DateFormatter object for displaying date in specified format.
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		// Create a calendar object that will convert the date and time value in milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);
			AuthorizationDialog
					.setIcon(R.drawable.ic_launcher)
					.setTitle("Are you sure you want to exit ?")
					.setNegativeButton("No", null)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							/*Intent returnIntent =new Intent();
							setResult(Activity.RESULT_OK,returnIntent);*/
							dbPaymentReceipt.CloseDatabase();
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
		if(tvsPrinterBaseActivity != null){
			tvsPrinterBaseActivity.mTVSPrinterStatus(iError);
		}
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
			if (device.getVendorId() == 1208
					&& getPrinterName(this, "receipt").equalsIgnoreCase(Constants.USB_EPSON_PRINTER_NAME)) {
				editor.putString("receipt", "TM Printer");
				editor.putString("TM Printer", "USB:"+device.getDeviceName());
				editor.commit();
			} else if (device.getVendorId() == Constants.VENDOR_ID_BIXOLON_POS_PRINTER
					&& device.getProductId() == Constants.PRODUCT_ID_BIXOLON_POS_PRINTER
					&& getPrinterName(this, "receipt").equalsIgnoreCase(Constants.USB_BIXOLON_PRINTER_NAME)){
				editor.putString("receipt", Constants.USB_BIXOLON_PRINTER_NAME);
				editor.putString(Constants.USB_BIXOLON_PRINTER_NAME, device.getSerialNumber());
				editor.commit();
			} else if (device.getVendorId() == Constants.VENDOR_ID_WEP_POS_PRINTER
					&& device.getProductId() == Constants.PRODUCT_ID_WEP_POS_PRINTER
					&& getPrinterName(this, "receipt").equalsIgnoreCase(Constants.USB_WEP_PRINTER_NAME)) {
				editor.putString("receipt", device.getProductName());
				editor.putString(device.getProductName(), device.getDeviceName());
				editor.commit();
			} else if (device.getVendorId() == Constants.VENDOR_ID_TVS_POS_PRINTER
					&& device.getProductId() == Constants.PRODUCT_ID_TVS_POS_PRINTER
					&& getPrinterName(this, "receipt").equalsIgnoreCase(Constants.USB_TVS_PRINTER_NAME)) {
				editor.putString("receipt", Constants.USB_TVS_PRINTER_NAME);
				editor.putString(Constants.USB_TVS_PRINTER_NAME, device.getDeviceName());
				editor.commit();
			}
		}
	}

	public String getPrinterName(Activity homeActivity, String module) {
		return Preferences.getSharedPreferencesForPrint(homeActivity).getString(module, "--Select--");
	}
}
