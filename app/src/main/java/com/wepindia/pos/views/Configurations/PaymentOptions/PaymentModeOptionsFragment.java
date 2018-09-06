package com.wepindia.pos.views.Configurations.PaymentOptions;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.models.PaymentOptionsBean;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Configurations.PaymentOptions.adapter.PaymentOptionsListAdapter;
import com.wepindia.pos.views.Configurations.PaymentOptions.listeners.OnPaymentOptionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MohanN on 2/15/2018.
 */

public class PaymentModeOptionsFragment extends Fragment implements OnPaymentOptionListener {

    private static final String TAG = PaymentModeOptionsFragment.class.getName();

    View view;
    MessageDialog msgBox;
    Context myContext;
    DatabaseHandler dbPaymentOption;

    @BindView(R.id.bt_payment_mode_options_update)
    Button btnUpdate;
    @BindView(R.id.rv_payment_mode_options)
    RecyclerView rvPaymentModeOptions;

    List<PaymentOptionsBean> paymentOptionsBeanList = new ArrayList<PaymentOptionsBean>();
    PaymentOptionsListAdapter paymentOptionsListAdapter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            dbPaymentOption = new DatabaseHandler(getActivity());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        view = inflater.inflate(R.layout.payment_mode_option_fragment, container, false);
        try {
            dbPaymentOption.CloseDatabase();
            dbPaymentOption.CreateDatabase();
            dbPaymentOption.OpenDatabase();
        } catch (Exception exp) {
            Toast.makeText(myContext, "OnCreate: " + exp.getMessage(), Toast.LENGTH_LONG).show();
        }
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ButterKnife.bind(this, view);
            myContext = getActivity();
            msgBox = new MessageDialog(myContext);
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDisplayPaymentOptionList();
    }

    @OnClick({R.id.bt_payment_mode_options_update})
    public void buttonClickEvent(View view) {
        switch (view.getId()) {
            case R.id.bt_payment_mode_options_update:
                mUpdate();
                break;
            default:
                break;
        }
    }

    void mUpdate() {
        try {
            long lResult = -1, lDBResult = -1;
            if(paymentOptionsBeanList.size() > 0){
                for (PaymentOptionsBean paymentOptionsBean : paymentOptionsBeanList){
                    lDBResult = dbPaymentOption.mInsertUpdatePaymentOptions(paymentOptionsBean);
                    if(lDBResult > -1){
                        lResult = lDBResult;
                    }
                }
            }
            if(lResult > -1){
                Toast.makeText(getActivity(),"Update Successful.",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(),"Update Error. Please try again later.",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void mDisplayPaymentOptionList() {
        getDefaultAccessPermissionData();
        if (paymentOptionsBeanList != null && paymentOptionsBeanList.size() > 0) {
            rvPaymentModeOptions.setLayoutManager(new GridLayoutManager(getActivity(),3));
            paymentOptionsListAdapter = new PaymentOptionsListAdapter(getActivity(),this, paymentOptionsBeanList);
            rvPaymentModeOptions.setAdapter(paymentOptionsListAdapter);
           /* if(paymentOptionsListAdapter == null) {
                paymentOptionsListAdapter = new PaymentOptionsListAdapter(this, paymentOptionsBeanList);
                rvPaymentModeOptions.setAdapter(paymentOptionsListAdapter);
            } else {
                paymentOptionsListAdapter.notify(paymentOptionsBeanList);
            }*/
        }
    }

    private void getDefaultAccessPermissionData() {
        paymentOptionsBeanList.clear();
        paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.CASH_PAYMENT_OPTION_CONFIG,
                R.color.parrot_green, R.mipmap.ic_cash_payment,  false));
        paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.CREDIT_CUSTOMER_PAYMENT_OPTION_CONFIG,
                R.color.app_color_background, R.mipmap.ic_credit_customer_payment,false));
        paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.DISCOUNT_PAYMENT_OPTION_CONFIG,
                R.color.app_color_background, R.mipmap.ic_discount,false));
        paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.MSWIPE_PAYMENT_OPTION_CONFIG,
                R.color.app_color_background, R.mipmap.ic_m_swipe,false));
        paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.E_WALLET_PAYMENT_OPTION_CONFIG,
                R.color.app_color_background, R.mipmap.ic_e_wallet_payment,false));
        paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.COUPON_PAYMENT_OPTION_CONFIG,
                R.color.app_color_background, R.mipmap.ic_coupon_payment,false));
        paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.OTHER_CARDS_PAYMENT_OPTION_CONFIG,
                R.color.app_color_background, R.mipmap.ic_other_cards,false));
      /*  paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.REWARD_POINTS_PAYMENT_OPTION_CONFIG,
                R.color.app_color_background, R.mipmap.ic_reward_points,false));*/
       /* paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.AEPS_UPI_PAYMENT_OPTION_CONFIG,
                R.color.app_color_background, R.mipmap.ic_aeps,false));*/
        paymentOptionsBeanList.add(new PaymentOptionsBean(DatabaseHandler.PAYTM_WALLET,
                R.color.app_color_background, R.mipmap.ic_paytm,false));
        mFetchValuesFromDB();
    }

    private void mFetchValuesFromDB(){
        Cursor cursorPaymentOptions = null;
        try {
            cursorPaymentOptions = dbPaymentOption.getPaymentOptions();
            if (cursorPaymentOptions != null && cursorPaymentOptions.moveToFirst()) {
                do{
                    if(cursorPaymentOptions.getString(cursorPaymentOptions.getColumnIndex(DatabaseHandler.KEY_PAYMENT_OPTION_CONFIG_NAME)) != null){
                        boolean isActive = (cursorPaymentOptions.getInt(cursorPaymentOptions.getColumnIndex(DatabaseHandler.KEY_PAYMENT_OPTION_CONFIG_IS_ACTIVE))
                                == 1 ? true:false);
                        mSetValues(cursorPaymentOptions.getString(cursorPaymentOptions.getColumnIndex(DatabaseHandler.KEY_PAYMENT_OPTION_CONFIG_NAME)),
                                isActive);
                    }
                } while (cursorPaymentOptions.moveToNext());
            }
        }catch (Exception ex){
            Log.i(TAG,"Unable to fetch the data from payment option table method: mFetchValuesFromDB(). "
             + ex.getMessage());
        } finally {
            if(cursorPaymentOptions != null){
                cursorPaymentOptions.close();
            }
        }
    }

    private void mSetValues(String strName, boolean isActive){
        try {
            if (paymentOptionsBeanList != null && paymentOptionsBeanList.size() > 0) {
                for(PaymentOptionsBean paymentOptionsBean : paymentOptionsBeanList){
                    if(strName.toUpperCase().trim().equals(paymentOptionsBean.getStrName().toUpperCase().trim())){
                        paymentOptionsBean.setActive(isActive);
                        if(paymentOptionsBean.isActive()) {
                            paymentOptionsBean.setiColor(R.color.parrot_green);
                        } else {
                            paymentOptionsBean.setiColor(R.color.app_color_background);
                        }
                        break;
                    }
                }
            }
        }catch (Exception ex){
            Log.i(TAG,"Unable to set the values error on mSetValues()." +ex.getMessage());
        }
    }

    @Override
    public void onPaymentOptionEnable(int position) {
        if(paymentOptionsBeanList.size() > 0){
            paymentOptionsBeanList.get(position).setActive(true);
            paymentOptionsBeanList.get(position).setiColor(R.color.parrot_green);
            paymentOptionsListAdapter.notify(paymentOptionsBeanList);
        }
    }

    @Override
    public void onPaymentOptionDisable(int position) {
        if(paymentOptionsBeanList.size() > 0){
            paymentOptionsBeanList.get(position).setActive(false);
            paymentOptionsBeanList.get(position).setiColor(R.color.app_color_background);
            paymentOptionsListAdapter.notify(paymentOptionsBeanList);
        }
    }
}