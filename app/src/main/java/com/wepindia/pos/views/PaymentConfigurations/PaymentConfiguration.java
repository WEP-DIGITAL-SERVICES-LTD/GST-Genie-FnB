package com.wepindia.pos.views.PaymentConfigurations;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.LoginActivity;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.EMOJI_FILTER;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentConfiguration extends WepBaseActivity{

    @BindView(R.id.et_payment_mode_config_key_id)
    EditText edtKeyId;
    @BindView(R.id.et_payment_mode_config_secret_key)
    EditText edtSecretKey;
    @BindView(R.id.cb_payment_mode_config_razor_pay)
    CheckBox cbRazorPay;

    @BindView(R.id.et_payment_mode_config_aeps_appId)
    EditText edtAEPSAppId;
    @BindView(R.id.et_payment_mode_config_aeps_merchantId)
    EditText edtAEPSMerchantId;
    @BindView(R.id.et_payment_mode_config_aeps_secretkey)
    EditText edtAEPSSecretKey;
    @BindView(R.id.cb_payment_mode_config_aeps)
    CheckBox cbAEPS;

    @BindView(R.id.btn_payment_update)
    Button btnUpdate;
    @BindView(R.id.btn_payment_clear)
    Button btnClear;
    @BindView(R.id.btn_payment_close)
    Button btnClose;

    private DatabaseHandler mDataBaseHelper;
    private MessageDialog mErrorMsgBox;
    private String strUserName = "";

    private String mPaymentModeKeyId;
    private String mPaymentModeSecretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment_mode_configuration);
        ButterKnife.bind(this);
        mDataBaseHelper = new DatabaseHandler(PaymentConfiguration.this);
        mErrorMsgBox = new MessageDialog(PaymentConfiguration.this);

        if (getIntent().getStringExtra("USER_NAME") != null) {
            strUserName = getIntent().getStringExtra("USER_NAME");
        }

        try {
            mDataBaseHelper.CreateDatabase();
            mDataBaseHelper.OpenDatabase();
        } catch (Exception ex) {
            mErrorMsgBox.Show("Error", ex.getMessage());
        }

        initialiseViews();
        setKeyIdAndSecretKey();
        applyValidations();
    }

    private void initialiseViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());

        com.wep.common.app.ActionBarUtils.setupToolbar(this, toolbar, getSupportActionBar(), "Payment Mode Configuration", strUserName, " Date:" + s.toString());

       /* mKeyIdEditText = (EditText) findViewById(R.id.et_keyid);
        mSecretKeyEditText = (EditText) findViewById(R.id.et_secretkey);

        mRazorPayCheckBox = (CheckBox) findViewById(R.id.check_box_razorpay);*/

        cbRazorPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    edtKeyId.setEnabled(true);
                    edtSecretKey.setEnabled(true);
                } else {
                    edtKeyId.setEnabled(false);
                    edtSecretKey.setEnabled(false);
                }
            }
        });
    }

    private void setKeyIdAndSecretKey() {
        String strKeyID= "", strSecretKey="", strAEPSAppId= "", strAEPSMerchantId="", strAEPSSecretKey= "";
        Cursor cursorPaymentModeConfig = mDataBaseHelper.getPaymentModeConfiguration();

        try {
            if (cursorPaymentModeConfig != null && cursorPaymentModeConfig.moveToFirst()) {

                if (cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_RAZORPAY_KEYID)) != null) {
                    strKeyID = cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_RAZORPAY_KEYID));
                } else {
                    strKeyID = "";
                }

                if (cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_RAZORPAY_SECRETKEY)) != null) {
                    strSecretKey = cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_RAZORPAY_SECRETKEY));
                } else {
                    strSecretKey = "";
                }

                if (cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_AEPS_AppId)) != null) {
                    strAEPSAppId = cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_AEPS_AppId));
                } else {
                    strAEPSAppId = "";
                }

                if (cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_AEPS_MerchantId)) != null) {
                    strAEPSMerchantId = cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_AEPS_MerchantId));
                } else {
                    strAEPSMerchantId = "";
                }
                if (cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_AEPS_SecretKey)) != null) {
                    strAEPSSecretKey = cursorPaymentModeConfig.getString(cursorPaymentModeConfig.getColumnIndex(DatabaseHandler.KEY_AEPS_SecretKey));
                } else {
                    strAEPSSecretKey = "";
                }

            } else {
                strKeyID = "";
                strSecretKey = "";
                strAEPSAppId = "";
                strAEPSMerchantId = "";
                strAEPSSecretKey = "";
            }
        }catch (Exception ex){
            Log.e("PaymentMode","Unable to get data from payment mode configuration table. " + ex.getMessage());

        } finally {
            if(cursorPaymentModeConfig != null){
                cursorPaymentModeConfig.close();
            }
        }
        edtKeyId.setText(strKeyID);
        edtSecretKey.setText(strSecretKey);
        edtAEPSAppId.setText(strAEPSAppId);
        edtAEPSMerchantId.setText(strAEPSMerchantId);
        edtAEPSSecretKey.setText(strAEPSSecretKey);
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.wep.common.app.R.menu.menu_wep_base, menu);
        menu.getItem(0).setVisible(true);
        for (int j = 0; j < menu.size(); j++) {
            MenuItem item = menu.getItem(j);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.wep.common.app.R.id.action_screen_shot) {
            Log.d("welocme", "screen short");

        } else if (id == com.wep.common.app.R.id.action_logout) {
            Intent intentResult = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentResult);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.btn_payment_update, R.id.cb_payment_mode_config_razor_pay, R.id.cb_payment_mode_config_aeps, R.id.btn_payment_clear, R.id.btn_payment_close})
    protected void onClickEvent(View view){

        switch (view.getId()) {
            case R.id.btn_payment_update:

                mInsertPaymentModeConfiguration();
                /*if (edtKeyId.getText().toString().equalsIgnoreCase("")) {
                    mErrorMsgBox.Show("Warning", "Please Enter Key Id");
                    return;
                }

                *//*if (mSecretKeyEditText.getText().toString().equalsIgnoreCase("")) {
                    mErrorMsgBox.Show("Warning", "Please Enter Secret Key");
                    return;
                }*//*

                mPaymentModeKeyId = edtKeyId.getText().toString().trim();
                mPaymentModeSecretKey = edtSecretKey.getText().toString().trim();

                mDataBaseHelper.updatePaymentModeDetails(mPaymentModeKeyId, mPaymentModeSecretKey);
                edtKeyId.setEnabled(false);
                edtSecretKey.setEnabled(false);
                cbRazorPay.setChecked(false);*/

                break;

            case R.id.btn_payment_clear:
                if (cbRazorPay.isChecked()) {
                    edtKeyId.setText("");
                    edtSecretKey.setText("");
                    edtAEPSAppId.setText("");
                    edtAEPSMerchantId.setText("");
                    edtAEPSSecretKey.setText("");
                }
                break;

            case R.id.btn_payment_close:
                mDataBaseHelper.CloseDatabase();
                mDataBaseHelper.close();
                this.finish();
                break;
            case R.id.cb_payment_mode_config_razor_pay:
                if (cbRazorPay.isChecked()) {
                    edtKeyId.setEnabled(true);
                    edtSecretKey.setEnabled(true);
                } else {
                    edtKeyId.setEnabled(false);
                    edtSecretKey.setEnabled(false);
                }
                break;
            case R.id.cb_payment_mode_config_aeps:
                if (cbAEPS.isChecked()) {
                    edtAEPSAppId.setEnabled(true);
                    edtAEPSMerchantId.setEnabled(true);
                    edtAEPSSecretKey.setEnabled(true);
                } else {
                    edtAEPSAppId.setEnabled(false);
                    edtAEPSMerchantId.setEnabled(false);
                    edtAEPSSecretKey.setEnabled(false);
                }
                break;
        }
    }

    private void mInsertPaymentModeConfiguration(){
        if( cbRazorPay.isChecked() && edtKeyId.getText().toString().trim().equalsIgnoreCase("")) {
            mErrorMsgBox.Show("Information", "Key ID  is mandatory for razorpay");
            return;
        }
        if( cbAEPS.isChecked() && edtAEPSAppId.getText().toString().trim().equalsIgnoreCase("")
                && edtAEPSMerchantId.getText().toString().trim().equalsIgnoreCase("")
                && edtAEPSSecretKey.getText().toString().trim().equalsIgnoreCase("")) {
            mErrorMsgBox.Show("Information", "All field are mandatory for AEPS");
            return;
        }
        else {

            int iResult = 0;
            // Update new payment mode config data into database
            if(cbRazorPay.isChecked()){
                iResult = mDataBaseHelper.updatePaymentModeDetailsRazorPay(edtKeyId.getText().toString().trim(),
                        edtSecretKey.getText().toString().trim());
                if (iResult > 0) {
                    //msgBox.Show("Information", "Saved Successfully");
                    Toast.makeText(this, "Data stored successfully", Toast.LENGTH_SHORT).show();
                    edtKeyId.setEnabled(false);
                    edtSecretKey.setEnabled(false);
                    cbRazorPay.setChecked(false);
                } else {
                    mErrorMsgBox.Show("Exception", "Failed to save razorpay configuration. Please try again");
                }
            }

            if(cbAEPS.isChecked()){
                iResult = mDataBaseHelper.updatePaymentModeDetailsAEPS(edtAEPSAppId.getText().toString().trim(),
                        edtAEPSMerchantId.getText().toString().trim(),edtAEPSSecretKey.getText().toString().trim());
                if (iResult > 0) {
                    //msgBox.Show("Information", "Saved Successfully");
                    Toast.makeText(this, "Data stored successfully", Toast.LENGTH_SHORT).show();
                    edtAEPSAppId.setEnabled(false);
                    edtAEPSMerchantId.setEnabled(false);
                    edtAEPSSecretKey.setEnabled(false);
                    cbAEPS.setChecked(false);
                } else {
                    mErrorMsgBox.Show("Exception", "Failed to save AEPS configuration. Please try again");
                }
            }

        }
    }
    void applyValidations()
    {
        edtKeyId.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtSecretKey.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtAEPSAppId.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtAEPSMerchantId.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtAEPSSecretKey.setFilters(new InputFilter[]{new EMOJI_FILTER()});
    }
}
