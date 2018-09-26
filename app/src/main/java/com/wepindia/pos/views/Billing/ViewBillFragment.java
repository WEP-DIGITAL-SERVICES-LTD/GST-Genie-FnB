package com.wepindia.pos.views.Billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.models.BillItemBean;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GSTSupport.HTTPAsyncTask_Frag;
import com.wepindia.pos.GenericClasses.DateTime;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.SendBillInfoToCustUtility;
import com.wepindia.pos.views.Billing.Adapters.BillingListAdapter;
import com.wepindia.pos.views.Billing.Listeners.OnBillingSelectedProductListsListeners;
import com.wepindia.pos.views.Billing.Listeners.ViewBillPrintOption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewBillFragment extends DialogFragment implements HTTPAsyncTask_Frag.OnHTTPRequestCompletedListener, OnBillingSelectedProductListsListeners {


   /* @BindView(R.id.toolbar)
    Toolbar toolbar;*/
    @BindView(R.id.etViewBillNumber)
    EditText etViewBillNumber;
    @BindView(R.id.tvViewBillInvoiceDate)
    TextView tv_inv_date;
    @BindView(R.id.rv_view_bill_item_list)
    RecyclerView rvBillingProductList;

    @BindView(R.id.et_view_bill_customer_name)
    EditText et_view_bill_customer_name;
    @BindView(R.id.et_view_bill_customer_mobile)
    EditText et_view_bill_customer_mobile;
    @BindView(R.id.et_view_bill_customer_gstin)
    EditText et_view_bill_customer_gstin;
    @BindView(R.id.et_view_bill_customer_email)
    EditText et_view_bill_customer_email;

    @BindView(R.id.et_view_bill_total_igst)
    EditText et_view_bill_total_igst;
    @BindView(R.id.et_view_bill_total_cgst)
    EditText et_view_bill_total_cgst;
    @BindView(R.id.et_view_bill_total_sgst)
    EditText et_view_bill_total_sgst;
    @BindView(R.id.et_view_bill_total_cess)
    EditText et_view_bill_total_cess;
    @BindView(R.id.et_view_bill_roundoff)
    EditText et_view_bill_roundoff;
    @BindView(R.id.et_view_bill_total_discount)
    EditText et_view_bill_total_discount;
    @BindView(R.id.et_view_bill_other_charges)
    EditText et_view_bill_other_charges;
    @BindView(R.id.et_view_bill_total_taxable_value)
    EditText et_view_bill_total_taxable_value;
    @BindView(R.id.et_view_bill_total_bill_amount)
    EditText et_view_bill_total_bill_amount;

    @BindView(R.id.btnCalenderViewBill)
    ImageButton btnCalenderViewBill;
    @BindView(R.id.btnViewBillView)
    Button btnViewBillView;
    @BindView(R.id.btnViewBillShare)
    Button btnViewBillShare;
   /* @BindView(R.id.btnViewBilRePrintToken)
    Button btnViewBilRePrintToken;*/
    @BindView(R.id.btnViewBilRePrintBill)
    Button btnViewBilRePrintBill;
    @BindView(R.id.btnViewBillClear)
    Button btnViewBillClear;
    @BindView(R.id.btnViewBillClose)
    Button btnViewBillClose;

    private Context mContext = null;
    private MessageDialog MsgBox ;
    private String customerId = "0";
    private String BUSINESS_DATE = "";
    private int SHAREBILL = 0, TOKEN_PRINT = 0;
    private boolean trainingMode = false;

    private List<BillItemBean> billingItemsList;
    private BillingListAdapter billingListAdapter;
    private boolean REVERSETAX = false;
    private ViewBillPrintOption viewBillPrintOption;
    
    private DatabaseHandler dbHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        mContext = getActivity();
        MsgBox = new MessageDialog(mContext);
        dbHandler = new DatabaseHandler(mContext);
        dbHandler.CreateDatabase();
        dbHandler.OpenDatabase();        
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_view_bill, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this,fragmentView);
        billingItemsList = new ArrayList<BillItemBean>();
        Cursor crsrSettings = dbHandler.getBillSettings();
        if (crsrSettings != null && crsrSettings.moveToFirst()) {
            BUSINESS_DATE = crsrSettings.getString(crsrSettings.getColumnIndex(DatabaseHandler.KEY_BusinessDate));
            SHAREBILL = crsrSettings.getInt(crsrSettings.getColumnIndex(DatabaseHandler.KEY_ShareBill));
//            TOKEN_PRINT = crsrSettings.getInt(crsrSettings.getColumnIndex(DatabaseHandler.KEY_TOKENPRINT));
        }

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mClear();
    }

    @OnClick({R.id.btnViewBillView, R.id.btnViewBillShare, R.id.btnViewBilRePrintBill,
            R.id.btnViewBillClear, R.id.btnViewBillClose, R.id.btnCalenderViewBill})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnCalenderViewBill:
                DateSelection(tv_inv_date);
                break;
            case R.id.btnViewBillView:
                mViewBill();
                break;
            case R.id.btnViewBillShare:
                if (SHAREBILL == 1)
                    checkForBillSending();
                else
                    Toast.makeText(mContext, "Share bill is disabled. To enable please go to settings.", Toast.LENGTH_SHORT).show();
                break;
           /* case R.id.btnViewBilRePrintToken:
                if (etViewBillNumber.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Please enter Bill Number.");
                    return;
                } else if (tv_inv_date.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Please enter Bill Date.");
                    return;
                } else {
//                    viewBillPrintOption.printViewedBillToken(etViewBillNumber.getText().toString(), tv_inv_date.getText().toString());
                }
                break;*/
            case R.id.btnViewBilRePrintBill:
                if (etViewBillNumber.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Please enter Bill Number.");
                    return;
                } else if (tv_inv_date.getText().toString().equalsIgnoreCase("")) {
                    MsgBox.Show("Warning", "Please enter Bill Date.");
                    return;
                } else {
                    viewBillPrintOption.printViewedBill(etViewBillNumber.getText().toString(), tv_inv_date.getText().toString());
                }
                break;
            case R.id.btnViewBillClear:
                mClear();
                break;
            case R.id.btnViewBillClose:
                dismiss();
                break;

        }
    }

    void mViewBill(){
        if (etViewBillNumber.getText().toString().equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please enter Bill Number");
            return;
        } else if (tv_inv_date.getText().toString().equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please enter Bill Date");
            return;
        } else {

            etViewBillNumber.setEnabled(false);
            btnCalenderViewBill.setEnabled(false);
            try{
                int billNo = 0;
                if (trainingMode) {
                    billNo = Integer.valueOf("-" + etViewBillNumber.getText().toString());
                    etViewBillNumber.setText("TM" + etViewBillNumber.getText().toString());
                }
                else {
                    billNo = Integer.valueOf(etViewBillNumber.getText().toString());
                }
                String date_viewBill = tv_inv_date.getText().toString();
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(date_viewBill);
                Cursor LoadItemToView = dbHandler.getItemsFromBillItem_new(billNo, date.getTime()+"");

                if (LoadItemToView.moveToFirst()) {
                    Cursor cursor = dbHandler.getBillDetail(billNo, date.getTime()+"");
                    if (cursor != null && cursor.moveToFirst()) {
                        int billStatus = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_BillStatus));
                        if (billStatus == 0) {
                            MsgBox.Show("Warning", "This bill has been deleted");
                            return;
                        }
                        double dTotalDiscount = cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_TotalDiscountAmount));
                        float discper = cursor.getFloat(cursor.getColumnIndex(DatabaseHandler.KEY_DiscPercentage));

                        et_view_bill_total_discount.setText(String.format("%.2f", dTotalDiscount));
                        et_view_bill_other_charges.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DeliveryCharge))));

                        et_view_bill_total_igst.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTAmount))));
                        et_view_bill_total_cgst.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTAmount))));
                        et_view_bill_total_sgst.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTAmount))));
                        et_view_bill_total_cess.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmount))));
                        et_view_bill_total_taxable_value.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_TaxableValue))));
                        et_view_bill_total_bill_amount.setText(String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_BillAmount))));
                        et_view_bill_roundoff.setText(String.format("%.2f",cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_RoundOff))));

                        LoadItemsForReprintBill(LoadItemToView);
                        customerId = (cursor.getString(cursor.getColumnIndex("CustId")));
                        et_view_bill_customer_name.setText(cursor.getString(cursor.getColumnIndex("CustName")));
                        et_view_bill_customer_mobile.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CustContactNumber)));
                        et_view_bill_customer_gstin.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                        et_view_bill_customer_email.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CUST_EMAIL)));
                        btnViewBilRePrintBill.setEnabled(true);
                        btnViewBilRePrintBill.setBackgroundResource(R.drawable.background_btn);
//                        btnViewBilRePrintToken.setEnabled(true);
//                        btnViewBilRePrintToken.setBackgroundResource(R.drawable.button_blue_color);
                        btnViewBillShare.setEnabled(true);
                        if (SHAREBILL == 1)
                            btnViewBillShare.setBackgroundResource(R.drawable.background_btn);
                        else
                            btnViewBillShare.setBackgroundResource(R.color.holo_blue);
                    }
                } else {
                    etViewBillNumber.setEnabled(true);
                    btnCalenderViewBill.setEnabled(true);
                    MsgBox.Show("Warning", "Bill does not exist. Please enter a valid Invoice number and Invoice date.");
                }

            } catch (Exception e) {
                Log.e("Error Occurred" , e+"");
                Toast.makeText(mContext, "Error occurred in retrieving bill details.", Toast.LENGTH_SHORT).show();
//                mClear();
            }

        }
    }

    String custPhoneNo = "";
    String custEmail = "";

    private  boolean isWifiConnected ()
    {
        boolean isWifiConnected = false;
        try{
            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                isWifiConnected = true;
            }

        }catch (Exception e)
        {
            isWifiConnected = false;
            Log.e("NetWorkConnectivity", e.toString());
        }
        finally {
            return  isWifiConnected ;
        }
    }

    private void checkForBillSending()
    {

        if(!isWifiConnected())
        {
            Toast.makeText(mContext, "Kindly connect to internet to share the bill.", Toast.LENGTH_LONG).show();
            return;
        }

        if (checkForInvoice()) {

            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.customer_detail_missing_alert, null);
            final EditText etCustPhone = alertLayout.findViewById(R.id.et_phone);
            final EditText etCustEmail = alertLayout.findViewById(R.id.et_email);
            final Button btnCancel = alertLayout.findViewById(R.id.btn_cancel);
            final Button btnSend = alertLayout.findViewById(R.id.btn_send);
            final CheckBox customerMobileCheckBox = (CheckBox) alertLayout.findViewById(R.id.customer_mobile);
            final CheckBox customerEmailCheckBox = (CheckBox) alertLayout.findViewById(R.id.customer_email);
            etCustPhone.setText(et_view_bill_customer_mobile.getText().toString().trim());
            etCustEmail.setText(et_view_bill_customer_email.getText().toString().trim());
            customerMobileCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        custPhoneNo = etCustPhone.getText().toString().trim();
                        etCustPhone.setEnabled(true);
                    }
                    else {
                        custPhoneNo = "";
                        etCustPhone.setEnabled(false);
                    }

                }
            });
            customerEmailCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        custEmail = etCustEmail.getText().toString().trim();
                        etCustEmail.setEnabled(true);
                    }
                    else {
                        custEmail = "";
                        etCustEmail.setEnabled(false);
                    }

                }
            });

            final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle("Customer Details");
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

                    if (etCustPhone.isEnabled() && etCustPhone.getText().toString().isEmpty()) {
                        etCustPhone.setError("Please fill this field.");
                    }
                    else if (etCustEmail.isEnabled() && etCustEmail.getText().toString().isEmpty()){
                        etCustEmail.setError("Please fill this field.");
                    }
                    else if (etCustPhone.isEnabled() || etCustEmail.isEnabled()){

                        String custPhoneNo = "", custEmail = "";

                        if (customerMobileCheckBox.isChecked())
                            custPhoneNo = etCustPhone.getText().toString().trim();

                        if (customerEmailCheckBox.isChecked())
                            custEmail = etCustEmail.getText().toString().trim();

                        String messageContent = "";
                        String billAmount = et_view_bill_total_bill_amount.getText().toString().trim();
                        String firmName = "";
                        Cursor crsrOwnerDetails = dbHandler.getOwnerDetail();
                        if (crsrOwnerDetails != null && crsrOwnerDetails.moveToFirst())
                            firmName = crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_FIRM_NAME));
                        if (!firmName.equalsIgnoreCase(""))
                            messageContent = "Dear " + "Customer" + ", you have made a transaction of Rs. " + billAmount + " with " + firmName + ". Thank you for shopping with us.";
                        else
                            messageContent = "Dear " + "Customer" + ", you have made a transaction of Rs. " + billAmount + ". Thank you for shopping with us.";

                        String filename = "Invoice_" + etViewBillNumber.getText().toString().trim() + "_" + tv_inv_date.getText().toString() + ".pdf";

                        String attachment = Environment.getExternalStorageDirectory().getPath() + "/"+ Constants.PDF_INVOICE_DIRECTORY+"/"
                                + "Invoice_" + etViewBillNumber.getText().toString().trim() + "_" + tv_inv_date.getText().toString() + ".pdf";

                        SendBillInfoToCustUtility smsUtility = new SendBillInfoToCustUtility(mContext, "Invoice", Constants.message_email + firmName, messageContent, custPhoneNo, true, true, false,
                                ViewBillFragment.this, custEmail, attachment, filename, firmName);
                        smsUtility.sendBillInfo();
                        Toast.makeText(mContext, "Sending....", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(mContext, "Please select either SMS or email or both to share invoice details with the customer.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            MsgBox.Show("Warning", "Bill does not exist. Please enter a valid Invoice number and Invoice date.");
        }
    }

    private boolean checkForInvoice(){
        Cursor LoadItemToView = null;

        if (etViewBillNumber.getText().toString().equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please enter Bill Number");
            return false;
        }
        if (tv_inv_date.getText().toString().equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please enter Bill Date");
            return false;
        }

        try{
            int billNo = 0;
            if (trainingMode)
                billNo = Integer.valueOf("-"+etViewBillNumber.getText().toString().substring(2));
            else
                billNo = Integer.valueOf(etViewBillNumber.getText().toString());
            String invoiceDate = tv_inv_date.getText().toString().trim();
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(invoiceDate);
            LoadItemToView = dbHandler.getItemsFromBillItem_new(billNo, date.getTime()+"");

            if (LoadItemToView != null && LoadItemToView.moveToFirst())
                return true;

        } catch (Exception e) {
            Log.e("Error Occurred" , e+"");
            return false;
        } finally {
            if (LoadItemToView != null)
                LoadItemToView.close();
        }

        return false;
    }

    public void mInitListener(ViewBillPrintOption viewBillPrintOption){
        this.viewBillPrintOption = viewBillPrintOption;
    }

    public boolean isTrainingMode() {
        return trainingMode;
    }

    public void setTrainingMode(boolean trainingMode) {
        this.trainingMode = trainingMode;
    }

    private void LoadItemsForReprintBill(Cursor crsrBillItems) {

        billingItemsList.clear();

        if (crsrBillItems.moveToFirst()) {

            // reverse tax
            REVERSETAX = crsrBillItems.getString(crsrBillItems.getColumnIndex("IsReverseTaxEnable")).equalsIgnoreCase("YES");

            // Display items in table
            do {

                BillItemBean billItemBean = new BillItemBean();

                billItemBean.setiItemId(crsrBillItems.getInt(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_ItemId)));
                billItemBean.setStrItemName(crsrBillItems.getString(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_ItemName)));
                billItemBean.setStrHSNCode(crsrBillItems.getString(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
                billItemBean.setDblQty(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_Quantity)));
                billItemBean.setDblValue(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_Value)));
                billItemBean.setDblAmount(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_Amount)));
                billItemBean.setDblCGSTRate(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
                billItemBean.setDblCGSTAmount(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_CGSTAmount)));
                billItemBean.setDblDiscountPercent(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_DiscountPercent)));
                billItemBean.setDblDiscountAmount(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_DiscountAmount)));
//                billItemBean.setiDeptCode(crsrBillItems.getInt(crsrBillItems.getColumnIndex("DeptCode")));
//                billItemBean.setiCategCode(crsrBillItems.getInt(crsrBillItems.getColumnIndex("CategCode")));
                billItemBean.setStrIsReverseTaxEnabled(crsrBillItems.getString(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_IsReverseTaxEnabled)));
                //billItemBean.setDblModifierAmount(crsrBillItems.getDouble(crsrBillItems.getColumnIndex("ModifierAmount")));
                billItemBean.setDblSGSTRate(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
                billItemBean.setDblSGSTAmount(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_SGSTAmount)));
                billItemBean.setStrUOM(crsrBillItems.getString(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_UOM)));
                billItemBean.setStrSupplyType(crsrBillItems.getString(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_SupplyType)));
                billItemBean.setDblIGSTRate(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
                billItemBean.setDblIGSTAmount(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_IGSTAmount)));
                billItemBean.setDblCessRate(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_cessRate)));
                billItemBean.setDblCessAmount(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_cessAmount)));
                billItemBean.setDblOriginalRate(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_OriginalRate)));
                billItemBean.setDblTaxbleValue(crsrBillItems.getDouble(crsrBillItems.getColumnIndex(DatabaseHandler.KEY_TaxableValue)));

                billingItemsList.add(billItemBean);

            } while (crsrBillItems.moveToNext());

            mPopulateItemsListAdapterData();

        } else {
            Log.d("LoadKOTItems", "No rows in cursor");
        }
    }

    private void mPopulateItemsListAdapterData(){

        if(billingItemsList != null) {
            rvBillingProductList.setLayoutManager(new LinearLayoutManager(mContext));
            if(billingListAdapter!=null) {
                billingListAdapter.notifyData(billingItemsList);
            }
            else {
                billingListAdapter = new BillingListAdapter(this, billingItemsList, 1, 1);
                rvBillingProductList.setAdapter(billingListAdapter);
                rvBillingProductList.smoothScrollToPosition(billingItemsList.size());
            }
        }
    }

    private void DateSelection(final TextView tv_inv_date) {        // StartDate: DateType = 1 EndDate: DateType = 2
        try {
            AlertDialog.Builder dlgReportDate = new AlertDialog.Builder(mContext);
            final DatePicker dateReportDate = new DatePicker(mContext);
            String date_str = getCurrentDate();
            String dd = date_str.substring(6, 10) + "-" + date_str.substring(3, 5) + "-" + date_str.substring(0, 2);
            DateTime objDate = new DateTime(dd);
            dateReportDate.updateDate(objDate.getYear(), objDate.getMonth(), objDate.getDay());
            String strMessage = "";


            dlgReportDate
                    .setIcon(R.mipmap.ic_company_logo)
                    .setTitle("Date Selection")
                    .setMessage(strMessage)
                    .setView(dateReportDate)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        String strDd = "";

                        public void onClick(DialogInterface dialog, int which) {
                            if (dateReportDate.getDayOfMonth() < 10) {
                                strDd = "0" + String.valueOf(dateReportDate.getDayOfMonth()) + "-";
                            } else {
                                strDd = String.valueOf(dateReportDate.getDayOfMonth()) + "-";
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

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    private void mClear(){

        custPhoneNo = "";
        custEmail = "";

        etViewBillNumber.setEnabled(true);
        etViewBillNumber.setText("");
        btnCalenderViewBill.setEnabled(true);
        tv_inv_date.setText(BUSINESS_DATE);

        et_view_bill_customer_name.setText("");
        et_view_bill_customer_mobile.setText("");
        et_view_bill_customer_gstin.setText("");
        et_view_bill_customer_email.setText("");
        et_view_bill_total_igst.setText("");
        et_view_bill_total_cgst.setText("");
        et_view_bill_total_sgst.setText("");
        et_view_bill_total_cess.setText("");
        et_view_bill_roundoff.setText("");
        et_view_bill_total_discount.setText("");
        et_view_bill_other_charges.setText("");
        et_view_bill_total_taxable_value.setText("");
        et_view_bill_total_bill_amount.setText("");

        btnViewBillShare.setEnabled(false);
        btnViewBillShare.setBackgroundResource(R.color.holo_blue);
        btnViewBilRePrintBill.setEnabled(false);
        btnViewBilRePrintBill.setBackgroundResource(R.color.holo_blue);
//        btnViewBilRePrintToken.setEnabled(false);
//        btnViewBilRePrintToken.setBackgroundResource(R.color.holo_blue);

        billingItemsList.clear();
        if (billingListAdapter != null)
            billingListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHttpRequestComplete(int requestCode, String filePath) {

    }

    @Override
    public void onBillingListItemSelected(int iPosition) {

    }

    @Override
    public void onBillingListItemRemove(int iPosition) {

    }
}
