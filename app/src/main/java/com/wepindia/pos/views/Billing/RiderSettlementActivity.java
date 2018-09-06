/****************************************************************************
 * Project Name		:	VAJRA
 *
 * File Name		:	RiderSettlementActivity
 *
 * Purpose			:	Represents settlement of delivery bills activity, and 
 * 						takes care of all UI back end operations in this activity, 
 * 						such as event handling data read from or display in views.
 *
 * DateOfCreation	:	11-January-2013
 *
 * Author			:	Balasubramanya Bharadwaj B S
 *
 ****************************************************************************/
package com.wepindia.pos.views.Billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wepindia.pos.GenericClasses.EditTextInputHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.ActionBarUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RiderSettlementActivity extends WepBaseActivity{

	// Context object
	Context myContext;

	// DatabaseHandler object
	DatabaseHandler dbRiderSettlement = new DatabaseHandler(RiderSettlementActivity.this);
	// MessageDialog object
	MessageDialog MsgBox;

	// View handlers
	EditText txtBillNumber, txtBillDate, txtBillAmount, txtCash, txtPettyCash, txtSettledAmount, txtDeliveryCharge;
	EditText txtDiscountAmt, txtCouponAmt, txtAmountDue;
	TableLayout tblRiderSettlement;
	Button btn_DeliverySettlementUpdate;

	// Variables
	int iRiderCode = 0;
	private Toolbar toolbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_ridersettlement);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        /*TextView tvTitleText = (TextView)findViewById(R.id.tvTitleBarCaption);
		ActionBarUtils.goBack(this, findViewById(R.id.imgTitleBackIcon));
		ActionBarUtils.goHome(this, findViewById(R.id.imgTitleHomeIcon));
		ActionBarUtils.takeScreenshot(this, findViewById(R.id.imgTitleScreenshotIcon), findViewById(R.id.lnrRiderSettlement));
        tvTitleText.setText("Rider Settlement");*/

		myContext = this;

		MsgBox = new MessageDialog(myContext);

		dbRiderSettlement.CreateDatabase();
		dbRiderSettlement.OpenDatabase();

		InitializeViews();

		LoadPendingDelivery();
		com.wep.common.app.ActionBarUtils.setupToolbar(this,toolbar,getSupportActionBar(),"COD Settlement","","");
	}

	private void InitializeViews(){
		EditTextInputHandler etInputValidate =  new EditTextInputHandler();
		txtBillNumber = (EditText)findViewById(R.id.etDeliveryBillNumberValue);
		txtBillDate = (EditText)findViewById(R.id.etDeliveryBillDateValue);
		txtBillAmount = (EditText)findViewById(R.id.etDeliveryBillAmountValue);
		txtCash = (EditText)findViewById(R.id.etDeliveryCashValue);
		txtPettyCash = (EditText)findViewById(R.id.etDeliveryPettyCashValue);
		txtDeliveryCharge = (EditText)findViewById(R.id.etDeliveryDeliveryChargeValue);
		txtDiscountAmt = (EditText)findViewById(R.id.etDeliveryDiscount);
		txtCouponAmt = (EditText)findViewById(R.id.etDeliveryCoupon);
		txtAmountDue = (EditText)findViewById(R.id.etDeliveryAmountDue);
		txtSettledAmount = (EditText)findViewById(R.id.etDeliverySettledAmountValue);
		etInputValidate.ValidateDecimalInput(txtSettledAmount);

		tblRiderSettlement = (TableLayout)findViewById(R.id.tblRiderSettlement);
		btn_DeliverySettlementUpdate = (Button) findViewById(R.id.btn_DeliverySettlementUpdate);
	}

	@SuppressWarnings("deprecation")
	private void LoadPendingDelivery(){
		Cursor crsrPendingDelivery = dbRiderSettlement.getRiderPendingDelivery();

		if(crsrPendingDelivery.moveToFirst()){
			TableRow rowPendingDelivery;
			TextView BillNumber, BillDate, BillAmount, RiderCode,
					PettyCash, SettledAmount, TotalItems, DeliveryCharge;
			do{
				rowPendingDelivery = new TableRow(myContext);
				rowPendingDelivery.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				rowPendingDelivery.setBackgroundResource(R.drawable.row_background);

				// Rider Code
				RiderCode = new TextView(myContext);
				RiderCode.setTextSize(18);
				RiderCode.setText(crsrPendingDelivery.getString
						(crsrPendingDelivery.getColumnIndex("EmployeeId")));

				// Bill Number
				BillNumber = new TextView(myContext);
				BillNumber.setTextSize(18);
				BillNumber.setText(crsrPendingDelivery.getString
						(crsrPendingDelivery.getColumnIndex("InvoiceNo")));

				// Bill Date
				BillDate = new TextView(myContext);
				BillDate.setTextSize(18);
				BillDate.setText(crsrPendingDelivery.getString
						(crsrPendingDelivery.getColumnIndex("InvoiceDate")));

				// Bill Amount
				BillAmount = new TextView(myContext);
				BillAmount.setTextSize(18);
//				BillAmount.setText(crsrPendingDelivery.getDouble(crsrPendingDelivery.getColumnIndex("BillAmount"))); // String.format("%.2f",orirate_d)
				BillAmount.setText(String.format("%.2f", crsrPendingDelivery.getDouble(crsrPendingDelivery.getColumnIndex("BillAmount"))) + " ");

				// Delivery Charge
				DeliveryCharge = new TextView(myContext);
				DeliveryCharge.setTextSize(18);
				DeliveryCharge.setText(crsrPendingDelivery.getString
						(crsrPendingDelivery.getColumnIndex("DeliveryCharge")));

				// Petty Cash
				PettyCash = new TextView(myContext);
				PettyCash.setTextSize(18);
				PettyCash.setText(String.format("%.2f", crsrPendingDelivery.getDouble(crsrPendingDelivery.getColumnIndex("PettyCash"))));

				// Settled Amount
				SettledAmount = new TextView(myContext);
				SettledAmount.setTextSize(18);
				SettledAmount.setText(String.format("%.2f", crsrPendingDelivery.getDouble(crsrPendingDelivery.getColumnIndex("SettledAmount"))));

				// Total Items
				TotalItems = new TextView(myContext);
				TotalItems.setTextSize(18);
				TotalItems.setText(crsrPendingDelivery.getString
						(crsrPendingDelivery.getColumnIndex("TotalItems")));

				// Add views to row
				rowPendingDelivery.addView(RiderCode);
				rowPendingDelivery.addView(BillNumber);
				rowPendingDelivery.addView(TotalItems);
				rowPendingDelivery.addView(BillAmount);
				rowPendingDelivery.addView(PettyCash);
				rowPendingDelivery.addView(SettledAmount);
				rowPendingDelivery.addView(DeliveryCharge);
				rowPendingDelivery.addView(BillDate);

				rowPendingDelivery.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(((TableRow)v).getChildAt(0) != null){

							btn_DeliverySettlementUpdate.setEnabled(true);

							// get the data from the selected row
							TextView rowRiderCode = (TextView)((TableRow)v).getChildAt(0);
							TextView rowBillNumber = (TextView)((TableRow)v).getChildAt(1);
							TextView rowBillAmount = (TextView)((TableRow)v).getChildAt(3);
							TextView rowPettyCash = (TextView)((TableRow)v).getChildAt(4);
							TextView rowSettledAmount = (TextView)((TableRow)v).getChildAt(5);
							TextView rowDeliveryCharge = (TextView)((TableRow)v).getChildAt(6);
							TextView rowBillDate = (TextView)((TableRow)v).getChildAt(7);

							// assign the data to text boxes
							iRiderCode = Integer.parseInt(rowRiderCode.getText().toString());
							txtBillNumber.setText(rowBillNumber.getText());

							DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
							long milliSeconds= Long.parseLong(rowBillDate.getText().toString());
							Calendar calendar = Calendar.getInstance();
							calendar.setTimeInMillis(milliSeconds);
							txtBillDate.setText(formatter.format(calendar.getTime()));

							txtBillAmount.setText(rowBillAmount.getText());
							txtSettledAmount.setText(rowSettledAmount.getText());

							Cursor crsrBillDetail = dbRiderSettlement.getBillDetail(Integer.valueOf(txtBillNumber.getText().toString()), rowBillDate.getText().toString());
							if(crsrBillDetail.moveToFirst())
							{
								txtDeliveryCharge.setText(String.format("%.2f", crsrBillDetail.getDouble(crsrBillDetail.getColumnIndex("DeliveryCharge"))));
								txtCash.setText(String.format("%.2f", crsrBillDetail.getDouble(crsrBillDetail.getColumnIndex("CashPayment"))));
								txtPettyCash.setText(String.format("%.2f", crsrBillDetail.getDouble(crsrBillDetail.getColumnIndex("PettyCashPayment"))));
								txtCouponAmt.setText(String.format("%.2f", crsrBillDetail.getDouble(crsrBillDetail.getColumnIndex("CouponPayment"))));
								txtDiscountAmt.setText(String.format("%.2f", crsrBillDetail.getDouble(crsrBillDetail.getColumnIndex("TotalDiscountAmount"))));

								double billAmt = crsrBillDetail.getDouble(crsrBillDetail.getColumnIndex("BillAmount"));
								double PaidAmt = crsrBillDetail.getDouble(crsrBillDetail.getColumnIndex("PaidTotalPayment"));
								if(billAmt <= PaidAmt) {
									txtAmountDue.setText("0.00");
									txtSettledAmount.setText(String.format("%.2f",billAmt));
								}
								else
								{
									txtAmountDue.setText(String.format("%.2f", billAmt));
									txtSettledAmount.setText("0.00");
//									txtAmountDue.setText(crsrBillDetail.getString(crsrBillDetail.getColumnIndex("BillAmount")));
								}
							}
						}
					}
				});

				// Add row to table
				tblRiderSettlement.addView(rowPendingDelivery,new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			}while(crsrPendingDelivery.moveToNext());
		}
	}

	private void ClearAll(){
		txtBillNumber.setText("");
		txtBillDate.setText("");
		txtBillAmount.setText("");
		txtDeliveryCharge.setText("");
		txtCash.setText("");
		txtPettyCash.setText("");
		txtSettledAmount.setText("");
		txtDiscountAmt.setText("");
		txtCouponAmt.setText("");
		txtAmountDue.setText("");
		btn_DeliverySettlementUpdate.setEnabled(false);


		/*// Clear table
		for(int iPosition = tblRiderSettlement.getChildCount() - 1; iPosition >= 1; iPosition --){

			TableRow Row = (TableRow)tblRiderSettlement.getChildAt(iPosition);

			// Remove views present in row
			Row.removeAllViews();

			// Remove empty row
			tblRiderSettlement.removeView(Row);
		}*/
	}

	public void Clear(View v){
		ClearAll();
	}

	public void Update(View v){
		int iResult = 0;
		String billno_str = txtBillNumber.getText().toString();
		String date_milli_str = "";
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			date = sdf.parse(txtBillDate.getText().toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String bill_date = String.valueOf(date.getTime());
		String billAmount_str = txtBillAmount.getText().toString();
		String cash = txtCash.getText().toString();
		String deliveryCharges_str = txtDeliveryCharge.getText().toString();
		String pettyCash_str = txtPettyCash.getText().toString();
		String discount_str = txtDiscountAmt.getText().toString();
		String coupon_str = txtCouponAmt.getText().toString();
		String amount_str = txtAmountDue.getText().toString();
		String settledAmount_str = txtSettledAmount.getText().toString();

		if(billno_str == null || billno_str.equals("") || billAmount_str == null || billAmount_str.equals("") || bill_date == null || bill_date.equals(""))
		{
			MsgBox = new MessageDialog(myContext);
			MsgBox.Show("Insufficient Information", "Please Select Bill To Update ");
			return;
		}

		if(cash== null | cash.equals(""))
		{
			cash = "0";
		}

		if(deliveryCharges_str== null | deliveryCharges_str.equals(""))
		{
			deliveryCharges_str = "0";
		}
		if(pettyCash_str== null | pettyCash_str.equals(""))
		{
			pettyCash_str = "0";
		}
		if(discount_str== null | discount_str.equals(""))
		{
			discount_str = "0";
		}
		if(coupon_str== null | coupon_str.equals(""))
		{
			coupon_str = "0";
		}
		if(amount_str== null | amount_str.equals(""))
		{
			amount_str = "0";
		}
		if(settledAmount_str== null | settledAmount_str.equals(""))
		{
			settledAmount_str = "0";
		}


		double fSettledAmount = Double.parseDouble(settledAmount_str);
		double fAAmountDue = Double.parseDouble(amount_str);

//		float fAmountToBeSettled = Float.parseFloat(txtBillAmount.getText().toString()) +
//				Float.parseFloat(txtPettyCash.getText().toString()) +
//				Float.parseFloat(txtDeliveryCharge.getText().toString());

		if(fSettledAmount < fAAmountDue ){
			MsgBox.Show("Warning", "Settled Amount is less, it should be " + fAAmountDue +
					"\n" + "i.e, (Bill Amount + Delivery Charge + PettyCash)");

		} else {

			iResult = dbRiderSettlement.updateRiderPendingDelivery
					(Integer.parseInt(billno_str), bill_date,
							fSettledAmount);
			Log.d("UpdateRiderPendDelivery", "Rows Updated:" + iResult);

			iResult = dbRiderSettlement.updatePendingDeliveryBill
					(Integer.parseInt(billno_str), bill_date, iRiderCode,
							Double.parseDouble(deliveryCharges_str), Double.parseDouble(cash), fSettledAmount);
			Log.d("UpdatePendingDelivery", "Rows Updated:" + iResult);

			iResult = dbRiderSettlement.updatePendingDeliveryBill_Ledger
					(Integer.parseInt(billno_str),fSettledAmount);
			/*MsgBox = new MessageDialog(myContext);
            MsgBox.Show("Information", "Deliver order settled");*/
			Toast.makeText(myContext, "Deliver order settled", Toast.LENGTH_SHORT).show();
			ClearAll();
			ClearTable();
			LoadPendingDelivery();

			//Close(v);
		}
	}

	public void ClearTable()
	{
		TableRow heading = (TableRow) tblRiderSettlement.getChildAt(0);
		tblRiderSettlement.removeAllViews();
		tblRiderSettlement.addView(heading);
	}

	public void Close(View v){
		// Close database connection
		dbRiderSettlement.CloseDatabase();

		// Close the activity
		this.finish();
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
							dbRiderSettlement.CloseDatabase();
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
}
