package com.wepindia.pos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.utils.ActionBarUtils;

import java.util.Date;

public class PaymentModeConfigurationActivity extends WepBaseActivity implements View.OnClickListener {

    private EditText mKeyIdEditText;
    private EditText mSecretKeyEditText;
    private CheckBox mRazorPayCheckBox;

    private DatabaseHandler mDataBaseHelper;
    private MessageDialog mErrorMsgBox;
    private String strUserName = "";

    private String mPaymentModeKeyId;
    private String mPaymentModeSecretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode_configuration);

        mDataBaseHelper = new DatabaseHandler(PaymentModeConfigurationActivity.this);
        mErrorMsgBox = new MessageDialog(PaymentModeConfigurationActivity.this);

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
    }

    private void initialiseViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());

        com.wep.common.app.ActionBarUtils.setupToolbar(this, toolbar, getSupportActionBar(), "Payment Mode Configuration", strUserName, " Date:" + s.toString());

        mKeyIdEditText = (EditText) findViewById(R.id.et_keyid);
        mSecretKeyEditText = (EditText) findViewById(R.id.et_secretkey);

        mRazorPayCheckBox = (CheckBox) findViewById(R.id.check_box_razorpay);
        findViewById(R.id.btn_payment_update).setOnClickListener(this);
        findViewById(R.id.btn_payment_close).setOnClickListener(this);
        findViewById(R.id.btn_payment_clear).setOnClickListener(this);

        mRazorPayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mKeyIdEditText.setEnabled(true);
                    mSecretKeyEditText.setEnabled(true);
                } else {
                    mKeyIdEditText.setEnabled(false);
                    mSecretKeyEditText.setEnabled(false);
                }
            }
        });
    }

    private void setKeyIdAndSecretKey() {
        Cursor cursorPaymentMode = mDataBaseHelper.getPaymentModeConfiguration();

        if (cursorPaymentMode != null && cursorPaymentMode.moveToFirst()) {

            if (cursorPaymentMode.getString(cursorPaymentMode.getColumnIndex("RazorPay_KeyId")) != null) {
                mPaymentModeKeyId = cursorPaymentMode.getString(cursorPaymentMode.getColumnIndex("RazorPay_KeyId"));
            } else {
                mPaymentModeKeyId = "";
            }

            if (cursorPaymentMode.getString(cursorPaymentMode.getColumnIndex("RazorPay_SecretKey")) != null) {
                mPaymentModeSecretKey = cursorPaymentMode.getString(cursorPaymentMode.getColumnIndex("RazorPay_SecretKey"));
            } else {
                mPaymentModeSecretKey = "";
            }
        } else {
            mPaymentModeKeyId = "";
            mPaymentModeSecretKey = "";
        }

        mKeyIdEditText.setText(mPaymentModeKeyId);
        mSecretKeyEditText.setText(mPaymentModeSecretKey);
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


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_payment_update:

                if (mKeyIdEditText.getText().toString().equalsIgnoreCase("")) {
                    mErrorMsgBox.Show("Warning", "Please Enter Key Id");
                    return;
                }

                /*if (mSecretKeyEditText.getText().toString().equalsIgnoreCase("")) {
                    mErrorMsgBox.Show("Warning", "Please Enter Secret Key");
                    return;
                }*/

                mPaymentModeKeyId = mKeyIdEditText.getText().toString().trim();
                mPaymentModeSecretKey = mSecretKeyEditText.getText().toString().trim();

                mDataBaseHelper.updatePaymentModeDetails(mPaymentModeKeyId, mPaymentModeSecretKey);
                mKeyIdEditText.setEnabled(false);
                mSecretKeyEditText.setEnabled(false);
                mRazorPayCheckBox.setChecked(false);

                break;

            case R.id.btn_payment_clear:
                if (mRazorPayCheckBox.isChecked()) {
                    mKeyIdEditText.setText("");
                    mSecretKeyEditText.setText("");
                }
                break;

            case R.id.btn_payment_close:
                mDataBaseHelper.CloseDatabase();
                mDataBaseHelper.close();
                this.finish();
                break;
        }
    }
}
