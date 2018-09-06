package com.wepindia.pos.views.Billing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mswipetech.wisepad.payment.MSwipePaymentActivity;
import com.mswipetech.wisepad.payment.fragments.FragmentLogin;
import com.payphi.merchantsdk.PayPhiSdk;
import com.razorpay.Checkout;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.models.AddedItemsToOrderTableClass;
import com.wep.common.app.models.PayBillDialogBean;
import com.wep.common.app.models.PayBillPaidAmountBean;
import com.wep.common.app.models.PaymentDetails;
import com.wep.common.app.models.PaymentOptionsBean;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.OnWalletPaymentResponseListener;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Billing.Adapters.PayBillPaidAmountAdapter;
import com.wepindia.pos.views.Billing.Adapters.PayBillRecyclerViewAdapter;
import com.wepindia.pos.views.Billing.Adapters.PayBillViewHolder;
import com.wepindia.pos.views.Billing.Listeners.AEPSCompleteListener;
import com.wepindia.pos.views.Billing.Listeners.OnMSwipeResultResponseListener;
import com.wepindia.pos.views.Billing.Listeners.OnProceedToPayCompleteListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayBillFragment extends DialogFragment implements PayBillViewHolder.ProceedToPayItemListener, PayBillPaidAmountAdapter.onDeleteClickListener,
        OnWalletPaymentResponseListener, FragmentLogin.OnLoginCompletedListener, OnMSwipeResultResponseListener,
        PayPhiSdk.IAppPaymentResponseListener, AEPSCompleteListener {

    private static final String TAG = PayBillFragment.class.getSimpleName();

    @BindView(R.id.bt_billing_proceed_to_pay_dialog_cancel)
    Button btCancel;
    @BindView(R.id.bt_billing_proceed_to_pay_dialog_pay)      
    Button btSaveBill;
    @BindView(R.id.bt_billing_proceed_to_pay_dialog_pay_print)      
    Button btSaveNPrintBill;
    @BindView(R.id.rv_proceed_to_pay_dialog_payment_mode)
    RecyclerView rvProceedToPayDialogPaymentMode;
    @BindView(R.id.tv_proceed_to_pay_dialog_taxable_value)
    TextView tvTaxableValue;
    @BindView(R.id.tv_proceed_to_pay_dialog_tax_amount)          
    TextView tvTaxAmount;
    @BindView(R.id.tv_proceed_to_pay_dialog_discount_amt)        
    TextView tvDiscountAmount;
    /*@BindView(R.id.tv_proceed_to_pay_dialog_tax_amt)             
    TextView tvTaxAmount;*/
    @BindView(R.id.tv_proceed_to_pay_dialog_otherCharges)        
    TextView tvOtherCharges;
    @BindView(R.id.tv_proceed_to_pay_dialog_round_off)           
    TextView tvRoundOff;
    @BindView(R.id.tv_proceed_to_pay_dialog_bill_amt)            
    TextView tvBillAmount;
    @BindView(R.id.tv_proceed_to_pay_dialog_paid_total_amount)          
    TextView tvPaidTotalAmount;
    @BindView(R.id.tv_proceed_to_pay_dialog_difference_amount)           
    public TextView tvDifferenceAmount;
    @BindView(R.id.ll_proceed_to_pay_dialog_paid_amount_list)
    ListView llPaidAmountList;
    @BindView(R.id.tv_proceed_to_pay_dialog_return_amount)       
    TextView tvReturnAmount;
    @BindView(R.id.tv_proceed_to_pay_dialog_discount_percent)     
    TextView tvDiscountPercent;
    @BindView(R.id.et_proceed_to_pay_dialog_no_of_prints)
    EditText etNoOfPrints;

    SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    List listData;
    double toPayAmount =0;
    double checkedDiscount =0;
    public String phoneReceived, phoneReceived2;
    int FORWARDTAX ;
    int ROUNDOFF ;
    int ITEMWISEDISCOUNT;
    int REWARDPOINTSENABLED =0;
    int REWARDPOINTS_LIMIT =0;
    double REWARDPOINTINAMOUNT =0;
    int checkCount =0;
    double discPercent =0;
    double taxableValue_recieved =0;
    double otherCharges_recieved =0;
    double discountAmount_recieved = 0;
    double taxAmount_recieved =0;
    double totalBillAmount_recieved =0;
    double totalcessAmount =0;
    double totalIGSTAmount =0;
    double totalCGSTAmount =0;
    double totalSGSTAmount =0;
    double totalBillAmount =0;
    boolean strOrderDelivered = false;
    ArrayList<AddedItemsToOrderTableClass> orderList_recieved;
    ArrayList<Map<String, String>> MapList;
    Map<String,String> map_ii, maprecieved;
    String walletTransactionId="";
    //MSwipe
    public int REQUEST_CODE_CARD_PAYMENT = 12;

    String txt;

    String appId = "";
    String mId = "";
    String aggreId = "";
    String secretKey = "";

    OnProceedToPayCompleteListener ProceedToPayCompleteListener;

    MessageDialog msgBox;
    Context myContext;
    PayBillPaidAmountAdapter paidAmountAdapter;
    ArrayList<PayBillPaidAmountBean> paidAmountList;
    List<PaymentOptionsBean> paymentOptionsBeanList;

    private DatabaseHandler dbHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Custom_Dialog);
        myContext = getActivity();
        dbHandler = new DatabaseHandler(myContext);
        dbHandler.CreateDatabase();
        dbHandler.OpenDatabase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        dbHandler.CloseDatabase();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pay_bill_fragment, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, rootView);
        paymentOptionsBeanList =  new ArrayList<PaymentOptionsBean>();
        mFetchPaymentOptionData();
        createPaymentOptions();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        msgBox = new MessageDialog(myContext);
        try{
            setSettingsParam();
//            getRewardPointConfiguration();
            mpopulateRecievedData();
            paidAmountList = new ArrayList<>();
            CalculateDueAmount();
            sharedPreferences = myContext.getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        }catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @OnClick({R.id.bt_billing_proceed_to_pay_dialog_cancel, R.id.bt_billing_proceed_to_pay_dialog_pay_print,
            R.id.bt_billing_proceed_to_pay_dialog_pay})
    protected void onWidgetClick(View view){
        switch(view.getId()){
            case R.id.bt_billing_proceed_to_pay_dialog_cancel:
                ProceedToPayCompleteListener.onDismiss();
                dismiss();
                break;
            case R.id.bt_billing_proceed_to_pay_dialog_pay:
                mSaveData(false);
                break;
            case R.id.bt_billing_proceed_to_pay_dialog_pay_print:
                mSaveData(true);
                break;
            default:
                break;
        }
    }

    void mSaveData(boolean isPrint)
    {

        double billamount = Double.parseDouble(tvBillAmount.getText().toString().trim());
        double totalAmountPaid = Double.parseDouble(tvPaidTotalAmount.getText().toString().trim());
        if(totalAmountPaid < billamount)
        {
            msgBox.Show(getString(R.string.incomplete_action), getString(R.string.totalPaidAmount_less_than_billAmount_message));
            return;
        }
        if (etNoOfPrints.getText().toString().trim().equals("")) {
            msgBox.Show(getString(R.string.invalid_information), getString(R.string.empty_no_print_bill));
            return;
        }
        if (Integer.valueOf(etNoOfPrints.getText().toString().trim()) <= 0 && isPrint == true) {
            msgBox.Show(getString(R.string.invalid_information), getString(R.string.zero_no_print_bill)+" or click on Pay Button");
            return;
        }
        if (Integer.parseInt(etNoOfPrints.getText().toString()) > 3) {
            msgBox.Show(getString(R.string.invalid_information), "Number of prints is restricted to 3 only.");
            return;
        }

        PaymentDetails paymentObj = new PaymentDetails();

        if(discPercent >0) {
            paymentObj.setDiscounted(true);
            paymentObj.setTotalDiscountPercent(discPercent);
            paymentObj.setTotalDiscountAmount(Double.parseDouble(String.format("%.2f",Double.parseDouble(tvDiscountAmount.getText().toString()))));
        }else
        {
            paymentObj.setDiscounted(false);
            paymentObj.setTotalDiscountPercent(0);
            paymentObj.setTotalDiscountAmount(Double.parseDouble(String.format("%.2f",Double.parseDouble(tvDiscountAmount.getText().toString()))));
        }

        paymentObj.setToPrintBill(isPrint);
        paymentObj.setNoOfPrint(Integer.valueOf(etNoOfPrints.getText().toString().trim()));
        boolean petty_reward_payment_present = false;
        for(PayBillPaidAmountBean paidObj : paidAmountList)
        {
            switch (paidObj.getModeName())
            {
                case Constants.CASH : paymentObj.setCashAmount(paidObj.getAmount());
                    break;
                case Constants.OTHERCARDS : paymentObj.setCardAmount(paidObj.getAmount());
                    break;
                case Constants.EWALLET : paymentObj.setWalletAmount(paidObj.getAmount());
                    if(!walletTransactionId.equals(""))
                    {
                        saveTransactionDetails(paidObj, walletTransactionId);
                    }
                    break;
                case Constants.COUPON : paymentObj.setCouponAmount(paidObj.getAmount());
                    break;
                case Constants.CREDITCUSTOMER : paymentObj.setPettyCash(paidObj.getAmount());
                    petty_reward_payment_present = true;
                    break;
                case Constants.PETTYCASH: //Added this on 01/03/2018 by mohan
                    paymentObj.setPettyCash(paidObj.getAmount());
                    petty_reward_payment_present = true;
                    break;
                case  Constants.REWARDSPOINTS : paymentObj.setRewardPoints(paidObj.getAmount());
                    petty_reward_payment_present = true;
                    break;
                case Constants.AEPS_UPI : paymentObj.setAepsAmount(paidObj.getAmount());
                    if(maprecieved!=null)
                    {
                        if(maprecieved.get("respDescription")!= null && maprecieved.get("respDescription").equalsIgnoreCase("Transaction successful") )
                        {
                            saveTransactionDetails(paidObj, maprecieved.get("paymentID"));
                        }
                    }
                    break;
                case Constants.MSWIPE : paymentObj.setmSwipeAmount(paidObj.getAmount());
                    break;
                case DatabaseHandler.PAYTM_WALLET: paymentObj.setmPaytmAmount(paidObj.getAmount());
                    break;

            }
        }

        paymentObj.setTotalPaidAmount(Double.parseDouble(tvPaidTotalAmount.getText().toString()));
        paymentObj.setTotalRoundOff(Double.parseDouble(tvRoundOff.getText().toString()));
        paymentObj.setTotalreturnAmount(Double.parseDouble(tvReturnAmount.getText().toString().substring(1, tvReturnAmount.getText().toString().length())));
        paymentObj.setTotalFinalBillAmount(Double.parseDouble(tvBillAmount.getText().toString()));
        paymentObj.setOrderDelivered(strOrderDelivered);
        paymentObj.setOrderList(orderList_recieved);
        paymentObj.setTotalBillAmount(totalBillAmount);
        paymentObj.setTotalIGSTAmount(totalIGSTAmount);
        paymentObj.setTotalCGSTAmount(totalCGSTAmount);
        paymentObj.setTotalSGSTAmount(totalSGSTAmount);
        paymentObj.setTotalcessAmount(totalcessAmount);

        paymentObj.setCustPhoneNo(phoneReceived);
        ProceedToPayCompleteListener.onProceedToPayComplete(paymentObj);
        dismiss();
    }

    void saveTransactionDetails(PayBillPaidAmountBean paidObj, String transactionId)
    {

        double amount = paidObj.getAmount();
        String invoiceNo = ""+dbHandler.getNewBillNumber();
        String InvoiceDate = "";
        Cursor cursor = dbHandler.getBusinessDate();
        if(cursor!=null && cursor.moveToFirst())
        {
            InvoiceDate = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_BusinessDate));
        }else
        {
            Date date = new Date();
            String dd = new SimpleDateFormat("dd-MM-yyyy").format(date);
            InvoiceDate = dd;
        }
        long result = dbHandler.insertTransactionDetails(invoiceNo, InvoiceDate, paidObj.getModeName()
                , transactionId, amount );
    }

    void setSettingsParam()
    {
        Cursor cursor = dbHandler.getBillSettings();
        if(cursor!=null && cursor.moveToFirst())
        {
            FORWARDTAX = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_Tax)); //1->forward, 0->reverse
//            ROUNDOFF = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_RoundOff));
            ITEMWISEDISCOUNT = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_DiscountType)); // 1->itemwise , 0 ->billwise
//            REWARDPOINTSENABLED = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_RewardPoints));
        }
    }

    void mpopulateRecievedData()
    {
        Bundle bundle = getArguments();

        taxableValue_recieved = bundle.getDouble(Constants.TAXABLEVALUE,0);
        taxAmount_recieved = bundle.getDouble(Constants.TAXAMOUNT,0);
        otherCharges_recieved = bundle.getDouble(Constants.OTHERCHARGES,0);
        discountAmount_recieved = bundle.getDouble(Constants.DISCOUNTAMOUNT,0);
        totalBillAmount_recieved = bundle.getDouble(Constants.TOTALBILLAMOUNT,0);
        orderList_recieved = bundle.getParcelableArrayList(Constants.ORDERLIST);
        strOrderDelivered = bundle.getBoolean(Constants.ORDERDELIVERED,false);
        phoneReceived = bundle.getString(Constants.PHONENO);
        phoneReceived2 = bundle.getString(Constants.PHONENO);

        tvTaxableValue.setText(String.format("%.2f",taxableValue_recieved));
        tvTaxAmount.setText(String.format("%.2f",taxAmount_recieved));
        tvDiscountAmount.setText(String.format("%.2f",discountAmount_recieved));
        tvOtherCharges.setText(String.format("%.2f",otherCharges_recieved));
        tvRoundOff.setText(String.format("%.2f",bundle.getDouble(Constants.ROUNDOFFAMOUNT)));
        tvBillAmount.setText(String.format("%.2f",totalBillAmount_recieved));
    }

    void CalculateDueAmount(){
        double totalBillAmount = tvBillAmount.getText().toString().trim().equals("")? 0.00: Double.parseDouble(tvBillAmount.getText().toString().trim());
        // double dueAmount = tvPaidTotalAmount.getText().toString().trim().equals("")? 0.00: Double.parseDouble(tvPaidTotalAmount.getText().toString().trim());
        double paidAmount = 0;
        if(paidAmountList.size() == 0)
        {
            tvPaidTotalAmount.setText("0.00");
            tvReturnAmount.setText("0.00");
//            tvDifferenceAmount.setText(String.format("%.2f",totalBillAmount-paidAmount));
            tvReturnAmount.setText("+" + String.format("%.2f",totalBillAmount-paidAmount));
            tvPaidTotalAmount.setTextColor(getResources().getColor(R.color.red));
            tvReturnAmount.setTextColor(getResources().getColor(R.color.green));
            return;
        }
        for(int i =0; i<paidAmountList.size();i++)
        {
            PayBillPaidAmountBean obj = paidAmountList.get(i);
            paidAmount += obj.getAmount();
        }

        double dueAmount = totalBillAmount - paidAmount;
        if(dueAmount>0){
            tvPaidTotalAmount.setText(String.format("%.2f",paidAmount));
//            tvDifferenceAmount.setText(String.format("%.2f",dueAmount));
//            tvReturnAmount.setText("0.00");
            tvReturnAmount.setText("+" + String.format("%.2f",dueAmount));
            tvPaidTotalAmount.setTextColor(getResources().getColor(R.color.red));
            tvReturnAmount.setTextColor(getResources().getColor(R.color.green));
        }else
        {
            tvPaidTotalAmount.setText(String.format("%.2f",paidAmount));
            tvPaidTotalAmount.setTextColor(getResources().getColor(R.color.green));
            if (String.format("%.2f",Math.abs(dueAmount)).equals("0.00"))
                tvReturnAmount.setText(String.format("%.2f",Math.abs(dueAmount)));
            else
                tvReturnAmount.setText("-" + String.format("%.2f",Math.abs(dueAmount)));
            tvReturnAmount.setTextColor(getResources().getColor(R.color.orange));
            tvDifferenceAmount.setText(String.format("0.00"));
        }

    }

    public void initProceedToPayListener(OnProceedToPayCompleteListener listener)
    {
        this.ProceedToPayCompleteListener = listener;
    }

    void createPaymentOptions()
    {
        listData = new ArrayList<PayBillDialogBean>();
        listData.add(new PayBillDialogBean(Constants.CASH, R.mipmap.ic_cash_payment));

        if(paymentOptionsBeanList.size() > 0){
            for (PaymentOptionsBean paymentOptionsBean : paymentOptionsBeanList){
                switch (paymentOptionsBean.getStrName()){
                    case DatabaseHandler.CREDIT_CUSTOMER_PAYMENT_OPTION_CONFIG:
                        if(paymentOptionsBean.isActive()) {
                            listData.add(new PayBillDialogBean(Constants.CREDITCUSTOMER, R.mipmap.ic_credit_customer_payment));
                        }
                        break;
                    case DatabaseHandler.DISCOUNT_PAYMENT_OPTION_CONFIG:
                        if(paymentOptionsBean.isActive()) {
                            listData.add(new PayBillDialogBean(Constants.DISCOUNT, R.mipmap.ic_discount));
                        }
                        break;
                    case DatabaseHandler.MSWIPE_PAYMENT_OPTION_CONFIG:
                        if(paymentOptionsBean.isActive()) {
                            listData.add(new PayBillDialogBean(Constants.MSWIPE, R.mipmap.ic_m_swipe));
                        }
                        break;
                    case DatabaseHandler.E_WALLET_PAYMENT_OPTION_CONFIG:
                        if(paymentOptionsBean.isActive()) {
                            listData.add(new PayBillDialogBean(Constants.EWALLET, R.mipmap.ic_e_wallet_payment));
                        }
                        break;
                    case DatabaseHandler.COUPON_PAYMENT_OPTION_CONFIG:
                        if(paymentOptionsBean.isActive()) {
                            listData.add(new PayBillDialogBean(Constants.COUPON, R.mipmap.ic_coupon_payment));
                        }
                        break;
                    case DatabaseHandler.OTHER_CARDS_PAYMENT_OPTION_CONFIG:
                        if(paymentOptionsBean.isActive()) {
                            listData.add(new PayBillDialogBean(Constants.OTHERCARDS, R.mipmap.ic_other_cards));
                        }
                        break;
                    case DatabaseHandler.REWARD_POINTS_PAYMENT_OPTION_CONFIG:
                        if(paymentOptionsBean.isActive()) {
                            listData.add(new PayBillDialogBean(Constants.REWARDSPOINTS, R.mipmap.ic_reward_points));
                        }
                        break;
                    case DatabaseHandler.AEPS_UPI_PAYMENT_OPTION_CONFIG:
                        if(paymentOptionsBean.isActive()) {
                            listData.add(new PayBillDialogBean(Constants.AEPS_UPI, R.mipmap.ic_aeps));
                        }
                        break;
                    case DatabaseHandler.PAYTM_WALLET:
                        if(paymentOptionsBean.isActive()) {
                            listData.add(new PayBillDialogBean(DatabaseHandler.PAYTM_WALLET, R.mipmap.ic_paytm));
                        }
                        break;
                    default:
                        break;
                }
            }
        }

       /* AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 5);
        rvProceedToPayDialogPaymentMode.setLayoutManager(layoutManager);*/
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        rvProceedToPayDialogPaymentMode.setLayoutManager(manager);

        PayBillRecyclerViewAdapter adapter = new PayBillRecyclerViewAdapter(getActivity(), listData, this);
        rvProceedToPayDialogPaymentMode.setAdapter(adapter);
    }

    private void mFetchPaymentOptionData(){
        Cursor cursorPaymentOptionData = null;
        try{
            cursorPaymentOptionData = dbHandler.getPaymentOptions();
            if(cursorPaymentOptionData != null && cursorPaymentOptionData.moveToFirst()){
                do{
                    PaymentOptionsBean paymentOptionsBean = new PaymentOptionsBean();
                    paymentOptionsBean.setStrName(cursorPaymentOptionData.getString(cursorPaymentOptionData.getColumnIndex(DatabaseHandler.KEY_PAYMENT_OPTION_CONFIG_NAME)));
                    boolean isActive = cursorPaymentOptionData.getInt(cursorPaymentOptionData.getColumnIndex(DatabaseHandler.KEY_PAYMENT_OPTION_CONFIG_IS_ACTIVE))==1?true:false;
                    paymentOptionsBean.setActive(isActive);
                    paymentOptionsBeanList.add(paymentOptionsBean);
                }while (cursorPaymentOptionData.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG,"Unable to fetch data from the payment option table method : mFetchPaymentOptionData()." +ex.getMessage());
        } finally {
            if(cursorPaymentOptionData != null){
                cursorPaymentOptionData.close();
            }
        }
    }

    @Override
    public void onItemClick(PayBillDialogBean item) {
        try{
            ConnectivityManager connManager = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            switch (item.strData)
            {
                case  Constants.CASH:
                    payByCashorOtherCards(Constants.CASH);
                    break;
                case  Constants.OTHERCARDS:
                    payByCashorOtherCards(Constants.OTHERCARDS);
                    break;
                case  Constants.MSWIPE:
                    if (mWifi.isConnected())
                        payByMSwipeCard();
                    else
                        Toast.makeText(myContext, "Please connect to WiFi.", Toast.LENGTH_SHORT).show();
                    break;
                case  Constants.EWALLET:
                    if (mWifi.isConnected())
                        payByWallet(null);
                    else
                        Toast.makeText(myContext, "Please connect to WiFi.", Toast.LENGTH_SHORT).show();
                    break;
                case  Constants.COUPON:
                    payByCoupon();
                    break;
                case  Constants.CREDITCUSTOMER:
                    payByPettyCash();
                    break;
                case  Constants.DISCOUNT:
                    payByDiscount();
                    break;
                case  Constants.REWARDSPOINTS:
                    //payByRewardPoints();
                    break;
                case  Constants.AEPS_UPI:
                    if (mWifi.isConnected())
                        payByAEPS();
                    else
                        Toast.makeText(myContext, "Please connect to WiFi.", Toast.LENGTH_SHORT).show();
                    break;
                case  DatabaseHandler.PAYTM_WALLET:
                    payByCashorOtherCards(DatabaseHandler.PAYTM_WALLET);
                    break;
            }
        }catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
    }

    public void payByCashorOtherCards(final String paymentMode)
    {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_payment_cash_dialog, null, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        ImageView imgClose = (ImageView) view.findViewById(R.id.iv_close);
        Button btnSave = (Button) view.findViewById(R.id.btnOk);
        TextView tvtitle = (TextView)view.findViewById(R.id.tv_dialog_title);
        final EditText edt_amount = (EditText)view.findViewById(R.id.edt_amount);
        TextView tvMode = (TextView)view.findViewById(R.id.tv_mode);

        if(paymentMode.equals(Constants.OTHERCARDS)) {
            tvMode.setText(Constants.OTHERCARDS);
        } else if(paymentMode.equals(DatabaseHandler.PAYTM_WALLET)) {
            tvMode.setText("Paytm");
        }

        tvtitle.setText(getString(R.string.enter_amount));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double amount = 0;

                if(!edt_amount.getText().toString().trim().equals(".") && !edt_amount.getText().toString().trim().equals(""))
                {
                    amount = Double.parseDouble(String.format("%.2f",Double.parseDouble(edt_amount.getText().toString().trim())));
                }
                double totalbillamt = Double.parseDouble(tvBillAmount.getText().toString().trim());
                if(paymentMode.equals(Constants.OTHERCARDS) && amount>totalbillamt)
                {
                    msgBox.Show(getString(R.string.inappropriate_entry),getString(R.string.amount_greater_than_billamt_error_message)+" "+totalbillamt);
                    return;
                } else  if(paymentMode.equals(DatabaseHandler.PAYTM_WALLET) && amount>totalbillamt){
                    msgBox.Show(getString(R.string.inappropriate_entry),getString(R.string.paytm_amount_cannot_be_greater_than_bill_amount)+" "+totalbillamt);
                    return;
                }
                if(amount>0)
                {
                    PayBillPaidAmountBean obj;
                    if(paymentMode.equals(Constants.CASH))
                        obj= new PayBillPaidAmountBean(Constants.CASH,amount);
                    else if(paymentMode.equals(Constants.OTHERCARDS)) {
                        obj = new PayBillPaidAmountBean(Constants.OTHERCARDS, amount);
                    } else {
                        obj = new PayBillPaidAmountBean(DatabaseHandler.PAYTM_WALLET, amount);
                    }
                    updatePaidList(obj);
                }
                dialog.dismiss();

            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(dialog.is)
                dialog.dismiss();
            }
        });
    }

    public void payByCoupon()
    {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View dialog_layout = inflater.inflate(R.layout.row_paybill_tablelist,null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

        builder.setView(dialog_layout);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        final TableLayout tblPayBill = (TableLayout) dialog_layout.findViewById(R.id.tblPayBill);
        TextView tvCaptionPaybillAmount = (TextView) dialog_layout.findViewById(R.id.tvCaptionPaybillAmount);
        tvCaptionPaybillAmount.setText("Qty");
        TextView tvTitle = (TextView) dialog_layout.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(Constants.COUPON);
        ImageView imgClose = (ImageView) dialog_layout.findViewById(R.id.img_paid_mode_del);
        Button btnSave = (Button) dialog.findViewById(R.id.btnok) ;
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(dialog.is)
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = tblPayBill.getChildCount();
                double amount =0;
                for (int i=1;i<count;i++)
                {
                    TableRow rowPayBill =  (TableRow) tblPayBill.getChildAt(i);
                    TextView value =  (TextView) rowPayBill.getChildAt(2);
                    EditText qty =  (EditText) rowPayBill.getChildAt(3);
                    double rate = 0;
                    double quantity =0;
                    if(!(value.getText() == null || value.getText().toString().equals("")))
                        rate = Double.parseDouble(value.getText().toString());
                    if(!(qty.getText() == null || qty.getText().toString().equals("")))
                        quantity = Double.parseDouble(qty.getText().toString());

                    amount += rate*quantity;
                }
                if(amount>0)
                {
                    PayBillPaidAmountBean paidObj = new PayBillPaidAmountBean(Constants.COUPON,amount);
                    updatePaidList(paidObj);
                }
                dialog.dismiss();
            }
        });

        // set the custom dialog components - text, image and button

        Cursor crsrCoupon = dbHandler.getAllCoupon();
        if (crsrCoupon.moveToFirst()) {
            int i = 1;
            TableRow rowPayBill = null;
            TextView tvSno, tvName, tvValue;
            EditText txtCouponQty;

            if (crsrCoupon.moveToFirst()) {
                do {
                    rowPayBill = new TableRow(myContext);
                    rowPayBill.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                    rowPayBill.setBackgroundResource(R.drawable.row_item_background);

                    tvSno = new TextView(myContext);
                    tvSno.setHeight(40);
                    tvSno.setTextSize(20);
                    tvSno.setGravity(1);
                    tvSno.setTextColor(Color.parseColor("#000000"));
                    tvSno.setText(String.valueOf(i++));
                    rowPayBill.addView(tvSno);

                    tvName = new TextView(myContext);
                    tvName.setHeight(40);
                    tvName.setTextSize(20);
                    tvName.setTextColor(Color.parseColor("#000000"));
                    tvName.setText(crsrCoupon.getString(crsrCoupon.getColumnIndex(DatabaseHandler.KEY_CouponDescription)));
                    rowPayBill.addView(tvName);

                    tvValue = new TextView(myContext);
                    tvValue.setHeight(40);
                    tvValue.setTextSize(20);
                    tvValue.setTextColor(Color.parseColor("#000000"));
                    tvValue.setText(crsrCoupon.getString(crsrCoupon.getColumnIndex(DatabaseHandler.KEY_CouponAmount)));
                    rowPayBill.addView(tvValue);

                    txtCouponQty = new EditText(myContext);
                    txtCouponQty.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtCouponQty.setText("0");
                    txtCouponQty.setWidth(80);
                    txtCouponQty.setSelectAllOnFocus(true);
                    /*** Bug 1153 - In Coupons quantity should be restricted to 6 digits in Proceed to pay screen.
                     *
                     */
                    txtCouponQty.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
                    txtCouponQty.setTextColor(Color.parseColor("#000000"));
                    //txtCouponQty.setOnKeyListener(CouponQtyKeyPressEvent);
                    //txtCouponQty.addTextChangedListener(CouponQtyChangeEvent);
                    rowPayBill.addView(txtCouponQty);

                    rowPayBill.setTag("TAG");

                    tblPayBill.addView(rowPayBill, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                } while (crsrCoupon.moveToNext());

            } else {
                Log.d("DisplayCoupon", "No Coupon found");
            }
        }
    }

    private void payByDiscount()
    {
        checkedDiscount =0;
        checkCount =0;
        if(FORWARDTAX != 1) // reverse
        {
            msgBox.Show(getString(R.string.invalid_attempt),getString(R.string.discount_not_applicable_in_reverse_message));
            return;
        }
        if(ITEMWISEDISCOUNT == 1) // Itemwise Discount
        {
            msgBox.Show(getString(R.string.invalid_attempt),getString(R.string.discount_not_applicable_in_itemwise_discount_message));
            return;
        }

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_paybill_tablelist, null, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        ImageView imgClose = (ImageView) view.findViewById(R.id.img_paid_mode_del);
        Button btnOk = (Button) view.findViewById(R.id.btnok);
        TextView tvtitle = (TextView)view.findViewById(R.id.tv_dialog_title);
        final EditText edt_amount = (EditText)view.findViewById(R.id.edt_amount);

        tvtitle.setText(Constants.DISCOUNT);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(dialog.is)
                if(checkedDiscount!=0)
                {
                    discPercent -= checkedDiscount;
                }
                dialog.dismiss();
            }
        });

        final RadioGroup rgDiscPerAmt = (RadioGroup) dialog.findViewById(R.id.rgDiscPerAmt);
        final RadioButton rbDiscPercent = (RadioButton) dialog.findViewById(R.id.rbDiscPercent);
        final RadioButton rbDiscAmount = (RadioButton) dialog.findViewById(R.id.rbDiscAmount);
        rbDiscAmount.setVisibility(View.INVISIBLE);
        rgDiscPerAmt.setVisibility(View.INVISIBLE);
        rbDiscPercent.setChecked(true);

        // set the custom dialog components - text, image and button
        TableLayout tblPayBill = (TableLayout) dialog.findViewById(R.id.tblPayBill);
        TextView tvCaptionPaybillValue = (TextView) dialog.findViewById(R.id.tvCaptionPaybillValue);
        TextView tvCaptionPaybillAmount = (TextView) dialog.findViewById(R.id.tvCaptionPaybillAmount);
        tvCaptionPaybillValue.setText("Percent");
        tvCaptionPaybillAmount.setText("Amount");
        tvCaptionPaybillAmount.setVisibility(View.GONE);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCount <=0) {
                    msgBox.Show(getString(R.string.invalid_attempt),getString(R.string.no_discount_choosen_message));
                    //dialog.dismiss();
                    return;
                }else if(checkCount >1)
                {
                    msgBox.Show(getString(R.string.invalid_attempt),getString(R.string.choose_one_discount_message));
                    return;
                }

                double alreadyDiscountApplied = Double.parseDouble(tvDiscountPercent.getText().toString());
                if(alreadyDiscountApplied >0)
                {
                    restoretoPreviousValue();
                    discPercent -= alreadyDiscountApplied;
                }
                double disc_amt = taxableValue_recieved *  (discPercent/ 100);
                tvDiscountAmount.setText(String.format("%.2f",disc_amt));
                tvDiscountPercent.setText(String.format("%.2f",discPercent));
                RecalculateBillAmount();
                CalculateDueAmount();
                dialog.dismiss();
            }
        });
        Cursor crsrDiscount = dbHandler.getAllDiscountConfig();

        if (crsrDiscount.moveToFirst()) {
            int i = 1;
            TableRow rowPayBill = null;
            TextView tvSno, tvName, tvPercent, tvAmount;
            CheckBox chk;

            if (crsrDiscount.moveToFirst()) {
                do {
                    rowPayBill = new TableRow(myContext);
                    rowPayBill.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                    rowPayBill.setBackgroundResource(R.drawable.row_item_background);

                    tvSno = new TextView(myContext);
                    tvSno.setHeight(40);
                    tvSno.setTextSize(20);
                    tvSno.setPadding(3,0,0,0);
                    tvSno.setTextColor(Color.parseColor("#000000"));
                    tvSno.setText(String.valueOf(i));
                    i++;
                    rowPayBill.addView(tvSno);

                    tvName = new TextView(myContext);
                    tvName.setHeight(40);
                    tvName.setTextSize(20);
                    tvName.setTextColor(Color.parseColor("#000000"));
                    tvName.setText(crsrDiscount.getString(crsrDiscount.getColumnIndex(DatabaseHandler.KEY_DiscDescription)));
                    rowPayBill.addView(tvName);

                    tvPercent = new TextView(myContext);
                    tvPercent.setHeight(40);
                    tvPercent.setTextSize(20);
                    tvPercent.setTextColor(Color.parseColor("#000000"));
                    tvPercent.setText(crsrDiscount.getString(crsrDiscount.getColumnIndex(DatabaseHandler.KEY_DiscPercentage)));
                    rowPayBill.addView(tvPercent);

                    chk = new CheckBox(myContext);
                    chk.setClickable(false);
                    /*chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b){
                                checkCount++;

                            }
                            else {
                                checkCount--;
                            }
                        }
                    });*/
                    rowPayBill.addView(chk);

//                    tvAmount = new TextView(myContext);
//                    tvAmount.setHeight(40);
//                    tvAmount.setTextSize(20);
//                    tvAmount.setTextColor(Color.parseColor("#000000"));
//                    tvAmount.setText(crsrDiscount.getString(3));
                    // rowPayBill.addView(tvAmount);

                    rowPayBill.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (String.valueOf(v.getTag()) == "TAG") {

                                if(rbDiscPercent.isChecked() == true || rbDiscAmount.isChecked() == true) {
                                    TableRow Row = (TableRow) v;
                                    TextView DiscountPercent = (TextView) Row.getChildAt(2);
                                    //TextView DiscountAmount = (TextView) Row.getChildAt(3);
                                    CheckBox chk = (CheckBox)Row.getChildAt(3);
                                    if (rbDiscPercent.isChecked() == true) {
                                        if(chk.isChecked())
                                        {
                                            chk.setChecked(false);
                                            checkCount--;
                                            discPercent -= Double.parseDouble(DiscountPercent.getText().toString());
                                            checkedDiscount -= Double.parseDouble(DiscountPercent.getText().toString());
                                        }else {
                                            chk.setChecked(true);
                                            checkCount++;
                                            discPercent += Double.parseDouble(DiscountPercent.getText().toString());
                                            checkedDiscount += Double.parseDouble(DiscountPercent.getText().toString());
                                        }
//
                                    }
                                }
                            }
                        }
                    });
                    rowPayBill.setTag("TAG");

                    tblPayBill.addView(rowPayBill, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                } while (crsrDiscount.moveToNext());
            } else {
                Log.d("DisplayCoupon", "No Coupon found");
            }
        }

    }

    public void payByPettyCash()
    {

        final double billamount = Double.parseDouble(tvBillAmount.getText().toString());
        final double totalPaidAmount = Double.parseDouble(tvPaidTotalAmount.getText().toString());
        if(billamount-totalPaidAmount <= 0)
        {
            msgBox.Show(getString(R.string.note),getString(R.string.no_due_amount_message));
            return;
        }

        String btnTxtOkay = "Pay";
        String btnTxtCancel = "Cancel";
        final String btnTxtNutral = "Credit & Pay";

        LayoutInflater li = LayoutInflater.from(myContext);
        View promptsView = li.inflate(R.layout.row_payment_petty_cash_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);
        alertDialogBuilder.setView(promptsView);

        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();

        final RelativeLayout rel6 = (RelativeLayout) promptsView.findViewById(R.id.rel6);
        final EditText editTextMobile = (EditText) promptsView.findViewById(R.id.editTextMobile);
        final EditText editTextName = (EditText) promptsView.findViewById(R.id.editTextName);
        final EditText editTextCreditAmount = (EditText) promptsView.findViewById(R.id.editTextCreditAmount);
        final EditText editTextCreditLimit = (EditText) promptsView.findViewById(R.id.editTextCreditLimit);
        final EditText editTextAmountToPay = (EditText) promptsView.findViewById(R.id.editTextAmountToPay);
        final EditText editTextAmountAllowed = (EditText) promptsView.findViewById(R.id.editTextAmountAllowed);

        final TextView textViewBalanceUpdate = (TextView) promptsView.findViewById(R.id.textViewBalanceUpdate);
        final TextView textViewMessage = (TextView) promptsView.findViewById(R.id.textViewMessage);

        ImageView imgClose = (ImageView) promptsView.findViewById(R.id.iv_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(dialog.is)
                dialog.dismiss();
            }
        });
        /*final Button btnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
        btnCancel.setText(btnTxtCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
*/
        editTextMobile.setFocusable(true);

        final Button btnOk = (Button) promptsView.findViewById(R.id.btnOk);
        btnOk.setText(btnTxtOkay);
        editTextMobile.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    if (editTextMobile.getText().toString().length() == 10)
                    {
                        Cursor crsrCust = dbHandler.getCustomerbyPhone(editTextMobile.getText().toString());
                        if (crsrCust.moveToFirst())
                        {
                            try{

                                /*if(crsrCust.getInt(crsrCust.getColumnIndex(DatabaseHandler.KEY_isDelete)) != 1)
                                {
                                    // inactive customer
                                    msgBox.Show(getString(R.string.invalid_attempt), getString(R.string.inactive_customer_message));
                                    return;
                                }*/
                                String custId = crsrCust.getString(crsrCust.getColumnIndex("CustId"));
                                String custName = crsrCust.getString(crsrCust.getColumnIndex(DatabaseHandler.KEY_CustName));
                                double creditAmount = crsrCust.getDouble(crsrCust.getColumnIndex("CreditAmount"));
                                double creditLimit = crsrCust.getDouble(crsrCust.getColumnIndex("CreditLimit"));
                                toPayAmount = creditAmount + creditLimit;
                                editTextName.setText(custName);
                                editTextCreditAmount.setText(String.format("%.2f",creditAmount));
                                editTextCreditLimit.setText(String.format("%.2f",creditLimit));
                                double effectiveAmountTopay = getUpdatedAmountForCreditCustomer(billamount,totalPaidAmount);
                                editTextAmountToPay.setText(String.format("%.2f",effectiveAmountTopay));


                                if((-1 * creditLimit) >= creditAmount)
                                {
                                    textViewBalanceUpdate.setText("");
                                    textViewMessage.setVisibility(View.VISIBLE);
                                    textViewMessage.setText(getString(R.string.text_message_payment_CreditLimit_reached));
                                    textViewBalanceUpdate.setVisibility(View.VISIBLE);
                                    btnOk.setText("Pay");
                                    btnOk.setEnabled(false);
                                    return;
                                }
                                //Balance After Payment: 100-46=54
                                double newBlnc = getUpdatedAmountForCreditCustomer(creditAmount,effectiveAmountTopay);
                                if((creditAmount-effectiveAmountTopay  ) >=(-1*creditLimit))
                                {
                                    textViewBalanceUpdate.setText("Balance After Payment: "+String.format("%.2f",creditAmount)+"-"+String.format("%.2f",effectiveAmountTopay)+"="+String.format("%.2f",newBlnc));
                                    textViewBalanceUpdate.setVisibility(View.VISIBLE);
                                    btnOk.setText("Credit & Pay");
                                    btnOk.setEnabled(true);
                                }
                                else {
                                    textViewBalanceUpdate.setText("");
                                    textViewMessage.setVisibility(View.VISIBLE);
                                    textViewBalanceUpdate.setVisibility(View.VISIBLE);
                                    btnOk.setText("Pay Partially");
                                    btnOk.setEnabled(true);
                                    rel6.setVisibility(View.VISIBLE);
                                    if(ROUNDOFF==1)
                                        editTextAmountAllowed.setText(String.format("%.2f",Math.floor(creditLimit+creditAmount)));
                                    else
                                        editTextAmountAllowed.setText(String.format("%.2f",creditLimit+creditAmount));
                                    textViewMessage.setText(getString(R.string.text_message_payment_zero_negative));
                                }

                            }catch (Exception e){
                                msgBox.Show("", e+"");
                            }
                        }
                        else
                        {
                            msgBox.Show("Error", "Customer is not Found, Please Add Customer for Petty Cash");
                        }
                    }
                    else
                    {
                        editTextName.setText("");
                        editTextCreditAmount.setText("");
                        editTextCreditLimit.setText("");
                        editTextAmountAllowed.setText("");
                        textViewBalanceUpdate.setVisibility(View.INVISIBLE);
                        textViewMessage.setVisibility(View.INVISIBLE);
                        btnOk.setText("Pay");
                    }
                } catch (Exception ex) {
                    editTextName.setText("");
                    editTextCreditAmount.setText("");
                    editTextCreditLimit.setText("");
                    textViewBalanceUpdate.setVisibility(View.INVISIBLE);
                    textViewMessage.setVisibility(View.INVISIBLE);
                    msgBox.Show("Error", ex.getMessage());
                    ex.printStackTrace();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        if(!phoneReceived.equals(""))
        {
            editTextMobile.setText(phoneReceived);
            editTextMobile.setEnabled(false);
        }


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnOk.getText().toString().equalsIgnoreCase("Pay"))
                {
                    msgBox.Show("Insufficient Information","Kindly enter the registered mobile number");
                    return;
                }
                double effectiveAmountTopay = getUpdatedAmountForCreditCustomer(billamount,totalPaidAmount);

                double creditAmount = Double.parseDouble(editTextCreditAmount.getText().toString());
                double creditLimit = Double.parseDouble(editTextCreditLimit.getText().toString());
                double newBlnc = getUpdatedAmountForCreditCustomer(toPayAmount,effectiveAmountTopay);
                if(newBlnc >= 0)
                {
                    //edtPettyCash.setText(String.format("%.2f",effectiveAmountTopay));
                    PayBillPaidAmountBean paidObj = new PayBillPaidAmountBean(Constants.CREDITCUSTOMER,effectiveAmountTopay);
                    updatePaidList(paidObj);
                    phoneReceived = editTextMobile.getText().toString().trim();
                }
                if(newBlnc < 0)
                {
                    //edtPettyCash.setText(String.format("%.2f",Math.abs(creditLimit-Math.abs(creditAmount))));
                    //edtPettyCash.setText(String.format("%.2f",toPayAmount));


                    if(Math.abs(effectiveAmountTopay - creditAmount ) <=Math.abs(creditLimit))
                    {
                        //edtPettyCash.setText(String.format("%.2f",effectiveAmountTopay));
                        PayBillPaidAmountBean paidObj = new PayBillPaidAmountBean(Constants.PETTYCASH,effectiveAmountTopay);
                        updatePaidList(paidObj);
                        phoneReceived = editTextMobile.getText().toString().trim();
                    }
                    else
                    {
                        PayBillPaidAmountBean paidObj = new PayBillPaidAmountBean(Constants.PETTYCASH,toPayAmount);
                        updatePaidList(paidObj);
                        phoneReceived = editTextMobile.getText().toString().trim();
                        /*if(BILLAMOUNTROUNDOFF ==1)
                            edtPettyCash.setText(String.format("%.2f", Math.floor(creditLimit-Math.abs(creditAmount))));
                        else
                            edtPettyCash.setText(String.format("%.2f",Math.abs(creditLimit-Math.abs(creditAmount))));
*/
                    }
                }
                //phoneReceived = editTextMobile.getText().toString();
                dialog.dismiss();
            }
        });
    }

    public void payByWallet(View view) {
        /**
         * You need to pass current activity/fragment in order to let Razorpay create CheckoutActivity
         */

        Cursor paymentDetails = dbHandler.getPaymentModeConfiguration();
        if(!(paymentDetails!=null && paymentDetails.moveToFirst()))
        {
            msgBox.Show("Invalid Credentials"," Please configure key id for razor pay in payment mode configuration module");
            return;
        }

        String keyid = paymentDetails.getString(paymentDetails.getColumnIndex("RazorPay_KeyId")).trim();
        if(!(keyid != null &&  keyid.length()>=8))
        {
            msgBox.Show("Invalid Credentials"," Please configure key id for razor pay in payment mode configuration module");
            return;
        }


        final Checkout co = new Checkout();
        Cursor ownercrsr = dbHandler.getOwnerDetail();
        if(ownercrsr != null && ownercrsr.moveToFirst() )
        {
            String email = "";
           /* if(!phoneReceived.equals(""))
            {
                Cursor cc = dbHandler.getCustomerbyPhone(phoneReceived);
                if(cc!=null && cc.moveToFirst()&& cc.getString(cc.getColumnIndex(DatabaseHandler.KEY_CUST_EMAIL)) !=null)
                {
                    email = cc.getString(cc.getColumnIndex(DatabaseHandler.KEY_CUST_EMAIL));
                }
            }*/

            double billamount = Double.parseDouble(tvBillAmount.getText().toString());
            double totalPaidAmount = Double.parseDouble(tvPaidTotalAmount.getText().toString());
            if(billamount-totalPaidAmount <= 0)
            {
                msgBox.Show(getString(R.string.note),getString(R.string.no_due_amount_message));
                return;
            }
            try {
                String firmName = ownercrsr.getString(ownercrsr.getColumnIndex("FirmName"));
                if(firmName== null)
                    firmName = "";
                JSONObject options = new JSONObject();
                options.put("name", firmName);
                options.put("description", "eWallet Payment");
                //You can omit the image option to fetch the image from dashboard
                options.put("image", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSVnUM4lZzEAgU62oQU9yjp_Z0i6KkrNzdrlZZT5LyfxZUIJpnL");
                options.put("currency", "INR");
                int toPayAmount = getIntegers(""+(billamount-totalPaidAmount));
                options.put("amount", toPayAmount+"");
                JSONObject preFill = new JSONObject();
                preFill.put("email", email);
                preFill.put("contact", phoneReceived);
                options.put("prefill", preFill);
                co.setKeyID(keyid);
                co.open(getActivity(), options);

            } catch (Exception e) {
                Toast.makeText(myContext, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else
        {
            msgBox.Show("Error","Issue occured in fetching owner details.");
        }

    }

    public void payByMSwipeCard() {

//        txt = tvDifferenceAmount.getText().toString();
        txt = tvReturnAmount.getText().toString().substring(1, tvReturnAmount.getText().toString().length());
        if (txt.equalsIgnoreCase("") ||tvReturnAmount.getText().toString().equalsIgnoreCase("0.00") )
        {
            Toast.makeText(getActivity(), "No amount to pay", Toast.LENGTH_SHORT).show();
        }
        else {
            String strReferenceId = "";
            String strSessionTokeniser = "";
            strReferenceId = com.mswipetech.wisepad.sdktest.view.Constants.Reference_Id;
            strSessionTokeniser = com.mswipetech.wisepad.sdktest.view.Constants.Session_Tokeniser;
            if ((sharedPreferences.getString("userId", "").equalsIgnoreCase(""))){
                com.mswipetech.wisepad.sdktest.view.Constants.Reference_Id = "";
                com.mswipetech.wisepad.sdktest.view.Constants.Session_Tokeniser = "";
            }
            if (com.mswipetech.wisepad.sdktest.view.Constants.Reference_Id.length() != 0 && com.mswipetech.wisepad.sdktest.view.Constants.Session_Tokeniser.length() != 0) {
                Intent intent = new Intent(myContext, MSwipePaymentActivity.class);
                intent.putExtra("amount", txt);
                intent.putExtra("phone", phoneReceived);
                startActivityForResult(intent,REQUEST_CODE_CARD_PAYMENT);
                //finish();
            } else {
                //Constants.showDialog(MenuView.this, "SDk List", "Please login first to perform the card sale.", 1);
                if (!(sharedPreferences.getString("userId", "").equalsIgnoreCase(""))) {
                    // Do Auto Authentication
                    validate(sharedPreferences.getString("userId", ""), sharedPreferences.getString("userPass", ""));

                } else {
                    promptLoginFragment();
                }
            }
        }
    }

    public void payByAEPS() {


        // String appId = "c2bc38dccf91ede1";

   /* String appId = "0b95bdd59d3d42cb";
    final String mId = "T_00041";
    final String aggreId = "J_00010";
    String secretKey = "abc";*/

        //String appId = "584f608382e53b42";


        aggreId = "AM_00007";
        /*final String appId = "d224fe3b3e461872";
        final String mId = "P_00064";
        final String aggreId = "AM_00007";
        String secretKey = "9392c19853c24bceb948c4c5343a1e60";*/ // production credentials
        Cursor cursor = dbHandler.getPaymentModeConfiguration();
        try{
            if(cursor!=null && cursor.moveToFirst())
            {
                if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AEPS_AppId)) !=null
                        && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AEPS_AppId)).equals("") )
                {
                    appId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AEPS_AppId));
                }else
                {
                    Toast.makeText(myContext, "AppId not configured for AEPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AEPS_MerchantId)) !=null
                        && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AEPS_MerchantId)).equals("") )
                {
                    mId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AEPS_MerchantId));
                }else
                {
                    Toast.makeText(myContext, "MerchantId not configured", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AEPS_SecretKey)) !=null
                        && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AEPS_SecretKey)).equals("") )
                {
                    secretKey = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AEPS_SecretKey));
                }else
                {
                    Toast.makeText(myContext, "secret key not configured", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(myContext, "Error occured while retrieving AEPS configuration", Toast.LENGTH_SHORT).show();
        }finally
        {
            if(cursor!= null)
                cursor.close();
        }


        final String CurrentCode = "356";
//        final String totamount = tvDifferenceAmount.getText().toString();
        final String totamount = tvReturnAmount.getText().toString().substring(1, tvReturnAmount.getText().toString().length());
        Date d = new Date();
        String dd = new SimpleDateFormat("hhmmss").format(d);
        System.out.println("Richa : "+dd);

        int randomNum = (int)Math.floor(Math.random() * (9999 - 1000 + 1)) + 1000;
        final String txnRefNo = String.valueOf(randomNum) + dd;


//        FirebaseApp.initializeApp(myContext);
//        final String deviceId = FirebaseInstanceId.getInstance().getToken();
        final String deviceId = "d4c0Hnl27oM:APA91bEqX5jH1XFtUOOCcfDadccMZj1qIAekWPVVMsmzzula9WCQKlsLZxA2qLs8mHMF875ovrmAu0V7dWDNeG_KZmN1KohuGh8PIhQuCgSNhuE688y4-pyjvQ24KsYq_haUeHO3LOh6";//temp w/o firebase
        System.out.println("Richa : txnRefNo:"+txnRefNo);
        //System.out.println("Richa : DeviceIn : "+deviceId);
        final String temp = totamount+CurrentCode+mId+txnRefNo;

        try {
            PayPhiSdk.setEnv(PayPhiSdk.PROD); // Todo change to Production env
            //PayPhiSdk.setAppInfo(mId, appId, getApplicationContext(), new
            PayPhiSdk.setAppInfo(mId, appId, myContext, new
                    PayPhiSdk.IAppInitializationListener() {
                        @Override
                        public void onSuccess(String statusCode) {
                            if(Integer.valueOf(statusCode) == 0){
                                //Toast.makeText(myContext, "setAppInfo is successfull", Toast.LENGTH_SHORT).show();
                                System.out.println("setAppInfo : "+statusCode+": setAppInfo is successfull");
                            }
                        }
                        @Override
                        public void onFailure(String errorCode) {
                            String msg="Some error occured in QR payment";
                            switch (Integer.valueOf(errorCode))
                            {
                                case 101 : msg = "Internal error occured for APES"; break;
                                case 504 : msg = "Connection error occured for APES"; break;
                                case 201 : msg = "Invalid app credentials for APES"; break;
                                case 205 : msg = "Payments not enabled for APES"; break;
                                default : msg = " Unknown error code received for APES"; break;
                            }
                            Toast.makeText(myContext, msg, Toast.LENGTH_SHORT).show();
                            System.out.println("setAppInfo : "+errorCode+": "+msg);
                        }
                    });

            String merchantName = "";
            String email = "guest@wepindia.com"; // default email id as required by PayPhi
            Cursor crsr = dbHandler.getOwnerDetail();
            if(crsr!=null && crsr.moveToFirst())
            {
                if(crsr.getString(crsr.getColumnIndex("FirmName")) != null)
                    merchantName = crsr.getString(crsr.getColumnIndex("FirmName"));
                if(crsr.getString(crsr.getColumnIndex("Email")) != null)
                    email = crsr.getString(crsr.getColumnIndex("Email"));
            }
            //final String merchantEmailId = email;
            final String merchantEmailId = "richa.agarwal@wepindia.com";
            PayPhiSdk.setMerchantName(myContext,merchantName);
            final String result = generateHMAC(temp, secretKey);
            //MsgBox.Show("Result",result);
            System.out.println("Richa : phn "+phoneReceived);
            final Map<String,String> map_ini = new HashMap<String,String>() {{
//                put("Amount", String.format("%.2f",Double.parseDouble(tvDifferenceAmount.getText().toString())));
                put("Amount", String.format("%.2f",Double.parseDouble(tvReturnAmount.getText().toString().substring(1, tvReturnAmount.getText().toString().length()))));
                put("MerchantTxnNo", txnRefNo);
                put("CurrencyCode", CurrentCode);
                put("MerchantID", mId);
                put("SecureToken", result);
                put("DeviceID",deviceId);
                put("CustomerEmailID", merchantEmailId);
                put("InvoiceNo", String.valueOf(dbHandler.getNewBillNumber()));
                put("MobileNo",phoneReceived);
            }};
            if(MapList == null)
                MapList = new ArrayList<>();
            MapList.add(map_ini);
            map_ii = map_ini;

            android.support.v4.app.Fragment fragment =
                    PayPhiSdk.makePayment(myContext, map_ini, PayPhiSdk.DIALOG, PayBillFragment.this);
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(myContext, "Error occurred in AEPS payment", Toast.LENGTH_SHORT).show();
        }
    /*finally{
        System.out.println("Amount of AEPS2 :"+amount);
        if(amount >0)
        {
            System.out.println("Amount of AEPS22 :"+amount);
            edtAEPS.setText(String.valueOf(amount));
        }
    }*/

    }

    private  String generateHMAC(String message, String secretKey) {
        Mac sha256_HMAC;
        byte[] hashedBytes = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            hashedBytes = sha256_HMAC.doFinal(message.getBytes());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Check here
        return bytesToHex(hashedBytes);
    }
    public static String bytesToHex(byte[] message) {
        StringBuffer stringBuffer = new StringBuffer();
        try{
            for (int i = 0; i < message.length; i++) {
                stringBuffer.append(Integer.toString((message[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    private void promptLoginFragment() {
        FragmentLogin alertdFragment = new FragmentLogin();
        alertdFragment.setCancelable(false);
        alertdFragment.setCompletedListener(this);
        alertdFragment.show(getActivity().getSupportFragmentManager(), "LoginDialog");
    }

    public void validate(String name, String password) {
       /* MswipeWisepadController wisepadController = new MswipeWisepadController(myContext, AppPrefrences.getGateWayEnv(), null);
        wisepadController.AuthenticateMerchant(loginHandler, name, password);
        progressDialog = new ProgressDialog(myContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();*/
    }

    private int getIntegers(String txt) {
        Integer i = 0;
        try{
            Double d = Double.parseDouble(txt);
            i = d.intValue()*100;
        }catch (Exception e){

        }
        return i;
    }

    public double getUpdatedAmountForCreditCustomer(double amt1,double amt2){
        double amount = 0.0;
        try{
            amount = amt1 - amt2;
        }catch (Exception e){
            amount = 0.0;
        }
        return amount;
    }

    private void RecalculateBillAmount() {
        double dTotalValue = 0.00, dPaidTotal = 0.00;
        double dTenderAmount = 0.00, dChangeAmount = 0.00, dDiscAmt = 0.00;
        totalcessAmount=totalIGSTAmount=totalCGSTAmount = totalSGSTAmount =totalBillAmount=0;
        double totaltaxableValue =0;

        if(discPercent>0){
            for(AddedItemsToOrderTableClass item : orderList_recieved)
            {
                double rate= item.getRate();
                double quantity = item.getQuantity();
                double igstRate = item.getIgstRate();
                double cgstRate = item.getCgstRate();
                double sgstRate = item.getSgstRate();
                double cessRate = item.getCessRate();
                if(FORWARDTAX ==1 ) // forward
                {
                    double discountamount = quantity*rate*discPercent/100 ;
                    double discountedrate_item = rate*(1-(discPercent/100));
                    double discountedrate = discountedrate_item*quantity;
                    double igstAmt_new = discountedrate*igstRate/100;
                    double cgstAmt_new = discountedrate*cgstRate/100;
                    double sgstAmt_new = discountedrate*sgstRate/100;
                    double cessAmt_new = discountedrate*cessRate/100;
                    totalIGSTAmount += igstAmt_new;
                    totalCGSTAmount += cgstAmt_new;
                    totalSGSTAmount += sgstAmt_new;
                    totalcessAmount+= cessAmt_new;
                    totaltaxableValue += discountedrate;

                    dTotalValue += discountedrate+igstAmt_new + cgstAmt_new +sgstAmt_new+cessAmt_new;
                    item.setSubtotal(discountedrate+igstAmt_new + cgstAmt_new +sgstAmt_new+cessAmt_new);
                    item.setIgstAmt(igstAmt_new);
                    item.setCgstAmt(cgstAmt_new);
                    item.setSgstAmt(sgstAmt_new);
                    item.setCessAmt(cessAmt_new);
                    item.setTaxableValue(discountedrate_item);
                    item.setDiscountamount(discountamount);
                    //item.setTaxableValue(discountedrate);
                }

            }  // end of for
            dTotalValue+= otherCharges_recieved;
            totalBillAmount = dTotalValue;
            double dRoundoffTotal = 0;
            if (ROUNDOFF == 1) {
                dRoundoffTotal = Math.round(totalBillAmount);
                String str = String.format("%.2f",totalBillAmount);
                if (!str.contains(".00")) {
                    tvRoundOff.setText("0" + str.substring(str.indexOf(".")));
                } else {
                    tvRoundOff.setText("0");
                }

            } else {
                dRoundoffTotal = totalBillAmount ;// Round off disabled
                tvRoundOff.setText("0");
            }
            tvBillAmount.setText(String.format( "%.2f", dRoundoffTotal ));
            if(totalIGSTAmount>0)
                tvTaxAmount.setText(String.format( "%.2f", totalIGSTAmount+totalcessAmount ));
            else
                tvTaxAmount.setText(String.format( "%.2f", totalCGSTAmount+totalSGSTAmount+totalcessAmount ));
            tvTaxableValue.setText(String.format( "%.2f", totaltaxableValue ));
        }
        else
        { // disc in RS
//            double discAmt  =0;
//            String disAmount_str = tvDiscountAmount.getText().toString();
//            if(disAmount_str.equals(""))
//                tvDiscountAmount.setText("0");
//            else
//            {
//                for(AddedItemsToOrderTableClass item : orderList_recieved) {
//
//                    double rate= item.getRate();
//                    double quantity = item.getQuantity();
//
//                    item.setTaxableValue(rate);// required to change beacuse of reverse tax implementation.
//                    //taxable value is calculated when activity returns.
//
//                    totalIGSTAmount += item.getIgstAmt();
//                    totalCGSTAmount += item.getCgstAmt();
//                    totalSGSTAmount += item.getSgstAmt();
//                    totalcessAmount += item.getCessAmt();
//
//                }
//
//                discAmt = Double.parseDouble(disAmount_str);
//                totalBillAmount = Double.parseDouble(strTotal);
//                totalBillAmount -= discAmt;
//
//                if (BILLAMOUNTROUNDOFF == 1) {
//                    dRoundoffTotal = Math.round(totalBillAmount);
//                    String str = String.format("%.2f",totalBillAmount);
//                    if (!str.contains(".00")) {
//                        edtRoundOff.setText("0" + str.substring(str.indexOf(".")));
//                    } else {
//                        edtRoundOff.setText("0");
//                    }
//
//                } else {
//                    dRoundoffTotal = totalBillAmount ;// Round off disabled
//                    edtRoundOff.setText("0");
//                }
//                edtTotalValue.setText(String.format( "%.2f", dRoundoffTotal ));
//
            //}
        }

    }

    private  void restoretoPreviousValue() {
        double dTotalValue = 0.00;
        totalcessAmount = totalIGSTAmount = totalCGSTAmount = totalSGSTAmount = totalBillAmount = 0;
        for (AddedItemsToOrderTableClass item : orderList_recieved) {
            double rate = item.getRate();
            double quantity = item.getQuantity();
            double igstRate = item.getIgstRate();
            double cgstRate = item.getCgstRate();
            double sgstRate = item.getSgstRate();
            double cessRate = item.getSgstRate();
            if (FORWARDTAX == 1) // forward
            {

                double igstAmt_new = rate * quantity * igstRate / 100;
                double cgstAmt_new = rate * quantity * cgstRate / 100;
                double sgstAmt_new = rate * quantity * sgstRate / 100;
                double cessAmt_new = rate * quantity * cessRate / 100;
                totalIGSTAmount += igstAmt_new;
                totalCGSTAmount += cgstAmt_new;
                totalSGSTAmount += sgstAmt_new;
                totalcessAmount += cessAmt_new;

                dTotalValue += rate * quantity + igstAmt_new + cgstAmt_new + sgstAmt_new + cessAmt_new;
                item.setSubtotal(rate * quantity + igstAmt_new + cgstAmt_new + sgstAmt_new + cessAmt_new);
                item.setIgstAmt(igstAmt_new);
                item.setCgstAmt(cgstAmt_new);
                item.setSgstAmt(sgstAmt_new);
                item.setCessAmt(cessAmt_new);
                item.setTaxableValue(rate * quantity);

            }
        }
    }

    void updatePaidList(final PayBillPaidAmountBean paidObj)
    {

        if(paidAmountList == null)
            paidAmountList = new ArrayList<PayBillPaidAmountBean>();
        boolean isPresent = false;
        int position =-1;
        for(PayBillPaidAmountBean po : paidAmountList)
        {
            position++;
            if(po.getModeName().equals(paidObj.getModeName()))
            {
                isPresent = true;
                break;
            }
        }
        final  int pos = position;
        if(isPresent)
        {
            MessageDialog messageBox = new MessageDialog(myContext);
            messageBox.setTitle(getString(R.string.duplicate))
                    .setIcon(R.drawable.ic_launcher)
                    .setMessage(getString(R.string.overwrite_payment_amount_confirmation_message))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PayBillPaidAmountBean po = paidAmountList.get(pos);
                            po.setAmount(paidObj.getAmount());
                            if(paidAmountAdapter == null)
                            {
                                paidAmountAdapter = new PayBillPaidAmountAdapter(getActivity(),paidAmountList,PayBillFragment.this);
                                llPaidAmountList.setAdapter(paidAmountAdapter);
                                llPaidAmountList.setAdapter(paidAmountAdapter);
                            }else
                            {
                                paidAmountAdapter.notifyNewDataAdded(paidAmountList);
                            }
                            CalculateDueAmount();
                        }
                    })
                    .setNegativeButton(getString(R.string.no),null)
                    .setCancelable(false)
                    .show();


        }else
        {
            paidAmountList.add(paidObj);

            if(paidAmountAdapter == null)
            {
                paidAmountAdapter = new PayBillPaidAmountAdapter(getActivity(), paidAmountList,this);
                llPaidAmountList.setAdapter(paidAmountAdapter);
                //llPaidAmountList.setAdapter(paidAmountAdapter);
            }else
            {
                paidAmountAdapter.notifyNewDataAdded(paidAmountList);
            }
            CalculateDueAmount();
        }
    }

    @Override
    public void onDeleteClicked(final int position) {
        MessageDialog msgbx = new MessageDialog(myContext);
        msgbx.setTitle("Delete Confirmation")
                .setIcon(R.drawable.ic_launcher)
                .setMessage(getString(R.string.paid_mode_delete_msg))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        paidAmountList.remove(position);
                        paidAmountAdapter.notifyNewDataAdded(paidAmountList);
                        CalculateDueAmount();
                        boolean petty_reward_payment_present = false;
                        if(!phoneReceived.equals(""))
                        {
                            for(PayBillPaidAmountBean paidObj  : paidAmountList)
                            {
                                if(paidObj.getModeName().equals(Constants.REWARDSPOINTS) || paidObj.getModeName().equals(Constants.CREDITCUSTOMER))
                                {
                                    petty_reward_payment_present =true;
                                    break;
                                }
                            }
                            if(!petty_reward_payment_present)
                            {
                                phoneReceived = phoneReceived2;
                            }
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel),null)
                .show();
    }


    @Override
    public void onWalletPaymentSuccessListener(String transaction_id) {
        try {
            walletTransactionId = transaction_id;
            double billamount = Double.parseDouble(tvBillAmount.getText().toString().trim());
            double totalAmountPaid = Double.parseDouble(tvPaidTotalAmount.getText().toString().trim());
            PayBillPaidAmountBean paidObject = new PayBillPaidAmountBean(Constants.EWALLET,(billamount-totalAmountPaid));
            updatePaidList(paidObject);
            Toast.makeText(myContext, "Payment Success", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Problem in Payment", e);
        }
    }

    @Override
    public void onWalletPaymentErrorListener(int code, String response) {
        try {
            switch(code)
            {
                case Checkout.NETWORK_ERROR :   /*Toast.makeText(myContext, "Wallet Payment Failed due to *//*internet connection*//*"+response, Toast.LENGTH_SHORT).show();*/
                    Toast.makeText(myContext, "Wallet Payment Failed due to "+response, Toast.LENGTH_SHORT).show();
                    break;
                case Checkout.PAYMENT_CANCELED :   /*Toast.makeText(myContext, "Wallet Payment Failed due to cancellation by user" +response, Toast.LENGTH_SHORT).show();*/
                    Toast.makeText(myContext, "Wallet Payment Failed due to " +response, Toast.LENGTH_SHORT).show();
                    break;
                case Checkout.INVALID_OPTIONS :   /*Toast.makeText(myContext, "Wallet Payment Failed due to *//*invalid options sent*//*"+response, Toast.LENGTH_SHORT).show();*/
                    Toast.makeText(myContext, "Wallet Payment Failed due to "+response, Toast.LENGTH_SHORT).show();
                    break;
                default : Toast.makeText(myContext, "Wallet Payment Failed for unknown reason", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    @Override
    public void onLoginCompleted(boolean success) {
        Intent intent = new Intent(myContext, MSwipePaymentActivity.class);
        intent.putExtra("amount", tvReturnAmount.getText().toString().substring(1, tvReturnAmount.getText().toString().length()));
        intent.putExtra("phone", phoneReceived);
        startActivityForResult(intent,REQUEST_CODE_CARD_PAYMENT);
    }

    @Override
    public void onChangePassword(boolean success) {

    }

    @Override
    public void onMSwipeResultResponseSuccess(String strResult) {
        try {
            double billamount = Double.parseDouble(tvBillAmount.getText().toString().trim());
            double totalAmountPaid = Double.parseDouble(strResult);
            PayBillPaidAmountBean paidObject = new PayBillPaidAmountBean(Constants.MSWIPE,Double.parseDouble(strResult));
            updatePaidList(paidObject);
            Toast.makeText(myContext, "Payment Success", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Problem in Payment", e);
        }
    }

    @Override
    public void onPaymentResponse(int resultCode, Map<String, String> map) {
        onAEPSCompleteListener(resultCode,map);
    }

    @Override
    public void onAEPSCompleteListener(int resultCode, Map<String, String> map) {
        System.out.println("Richa : result Code "+resultCode);
        System.out.println("Richa : map========== "+map);
        map = mapChanger(map);
        System.out.println("Richa : changed map========== "+map);
        String str = "";
        String concatedString = "";
        maprecieved = map;

        getTrans(null);

        //resultCode = -1;
        switch (resultCode)
        {
            case -1: str = ("makePayment result : Ok");
                TreeSet<String> keys = new TreeSet<String>(map.keySet());
                int flag =0;
                for (String key : keys) {
                    if(key.equals("responseCode")) {
                        String value = map.get(key);
                        if(value.equals("0000") || value.equals("000"))
                        {
                            flag =1;
                            break;
                        }
                    }else
                        continue;
                }

                if(0==flag) {
                    String value = "";
                    for (String key : keys) {
                        if(key.equals("respDescription")) {
                            value = map.get(key);
                            break;
                        }else
                            continue;
                    }
                    final String msg1 = value;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(myContext,"Transaction failed as "+msg1,Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                }
                for (String key : keys) {
                    if(key.equals("secureHash")) {
                        continue;
                    }
                    String value = map.get(key);
                    concatedString = concatedString + value;
                }
                if(map.get("secureHash")!= null)
                {
                    if(map.get("secureHash").equals(generateHMAC(concatedString, map_ii.get("SecureToken"))))
                    {
                        str += "   SecureToken matched";
                        flag =1;
                    }
                    else
                    {
                        str += "   SecureToken NOT matched";
                        flag =0;
                    }
                }else {
                    flag = 1;
                }
                if(0==flag) {
                    Toast.makeText(myContext,"Transaction failed - transaction compromised ",Toast.LENGTH_SHORT).show();
                    break;
                }
                String txnRefNo = map.get("merchantTxnNo");
                System.out.println("Richa : merchantTxnNo "+txnRefNo);
                for(Map<String, String> mapPrevious : MapList)
                {
                    System.out.println("Richa : list  "+mapPrevious.get("MerchantTxnNo"));
                    if(map_ii.get("MerchantTxnNo").equals(txnRefNo))
                    {
                        String amt1 = mapPrevious.get("Amount");
                        System.out.println("Richa : list  "+amt1);
                        // System.out.println("Richa : payBill  "+edtRoundOff.getText());
                        double amount = Double.parseDouble(amt1);
                        System.out.println("Amount of AEPS1 :"+amount);
                        final PayBillPaidAmountBean obj= new PayBillPaidAmountBean(Constants.AEPS_UPI,amount);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updatePaidList(obj);
                            }
                        });


                        //updateAEPS(String.valueOf(amount));
                        MapList.remove(mapPrevious);
                        //      Toast.makeText(myContext, "Transaction Success", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else
                    {
                        break;
                    }
                }

                break;
            case 0: str =("makePayment result : Cancelled");
                break;
            case 2: str =("makePayment result : Application Error");
                break;
            case 3: str =("makePayment result : Network Error");
                break;
            case 4: str =("makePayment result : Configuration Error");
                break;

        }
        System.out.println("Richa : AEPS msg : "+str);
        final String str1 = str;
        if(resultCode != -1)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(myContext,"Transaction failed as "+str1,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public Map mapChanger(Map<String, String> map) {
        if(map.containsKey("respType") && map.get("respType").equals("checkstatus")) {

            Map returnMap = new HashMap();
            String statusRespCode = map.get("responseCode");
            if (statusRespCode.equals("000") || statusRespCode.equals("0000")) {
                String respCode = map.get("txnResponseCode");
                returnMap.put("responseCode", respCode);
                returnMap.put("respDescription", map.get("txnRespDescription"));
            } else {
                returnMap.put("responseCode", "039");
                returnMap.put("respDescription", "Invalid txn");
            }
            returnMap.put("respType", map.get("respType"));
            returnMap.put("merchantId", map.get("merchantId"));
            returnMap.put("paymentID", map.get("txnAuthID"));
            returnMap.put("merchantTxnNo", map.get("merchantTxnNo"));
            returnMap.put("txnID", map.get("txnID"));
            returnMap.put("paymentDateTime", map.get("paymentDateTime"));
            if (map.containsKey("addlParam1")) {
                returnMap.put("addlParam1", map.get("addlParam1"));
            }
            if (map.containsKey("addlParam2")) {
                returnMap.put("addlParam2", map.get("addlParam2"));
            }

            return returnMap;
        } else {
            return map;
        }
    }

    public  void getTrans(View view){ // this func is used by PayPhi SDK
        // dbHelper.dropTable();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String date1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println("Date test=="+date);
        try{
            JSONArray jsonArray=  PayPhiSdk.getPaymentTransactions(myContext,date1,date1);
            System.out.println("Richa : transaction data=="+jsonArray.toString());
            if(jsonArray.length()>0)
            {
                // System.out.println("richa : jsonArray.length() = "+jsonArray.length());
                String responseCode ="";
                String respDescription ="";
                String merchantTxnNo = "";
                String txnStatus = "";
                String txnID = "";
                String paymentDateTime = "";
                String paymentID = "";
                String amount = "";
                int length = jsonArray.length();
                JSONObject jsonObject = jsonArray.getJSONObject(length -1);
                if(jsonObject.getString("responseCode") !=null)
                {
                    responseCode = jsonObject.getString("responseCode");
                }
                if(jsonObject.getString("respDescription") !=null)
                {
                    respDescription = jsonObject.getString("respDescription");
                }
                if(jsonObject.getString("merchantTxnNo") !=null)
                {
                    merchantTxnNo = jsonObject.getString("merchantTxnNo");
                }
                if(jsonObject.getString("txnStatus") !=null)
                {
                    txnStatus = jsonObject.getString("txnStatus");
                }
                if(jsonObject.getString("txnID") !=null)
                {
                    txnID = jsonObject.getString("txnID");
                }
                if(jsonObject.getString("paymentDateTime") !=null)
                {
                    paymentDateTime = jsonObject.getString("paymentDateTime");
                }
                if(jsonObject.getString("paymentID") !=null)
                {
                    paymentID = jsonObject.getString("paymentID");
                }
                if(jsonObject.getString("amount") !=null)
                {
                    amount = jsonObject.getString("amount");
                }
                Cursor cursor  = dbHandler.getAEPSTransDetailByMerchantTxnNo(merchantTxnNo);
                if(cursor!=null && cursor.moveToNext())
                {

                }else
                {
                    long insert = dbHandler.insertAEPSTransDetails( responseCode, respDescription,merchantTxnNo,
                            txnStatus, txnID,paymentDateTime,paymentID,amount);
                    System.out.println("Richa : Insert aeps trans details status : "+insert);
                }

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }




        //JSONArray jsonArray=  PayPhiSdk.getPaymentTransactions(myContext,date1,date1);
        //System.out.println("Richa : transaction data=="+jsonArray.toString());

    }
}
