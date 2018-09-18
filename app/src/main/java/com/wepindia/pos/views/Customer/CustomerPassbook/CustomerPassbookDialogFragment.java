package com.wepindia.pos.views.Customer.CustomerPassbook;

/**
 * Created by MohanN on 3/1/2018.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.wep.common.app.Database.Customer;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.models.CustomerPassbookBean;
import com.wepindia.pos.GenericClasses.DateTime;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerPassbookDialogFragment extends DialogFragment {
    private static final String TAG = CustomerPassbookDialogFragment.class.getName();

    @BindView(R.id.iv_customer_passbook_dialog_close)
    ImageView ivClose;

    @BindView(R.id.bt_customer_passbook_dialog_from_date)
    Button btFromDate;
    @BindView(R.id.bt_customer_passbook_dialog_to_date)
    Button btToDate;
    @BindView(R.id.bt_customer_passbook_dialog_view)
    Button btView;

    @BindView(R.id.et_customer_passbook_dialog_from_date)
    EditText etFromDate;
    @BindView(R.id.et_customer_passbook_dialog_to_date)
    EditText etToDate;

    @BindView(R.id.et_customer_passbook_dialog_customer_name)
    TextInputEditText etCustName;
    @BindView(R.id.et_customer_passbook_dialog_customer_phone_no)
    TextInputEditText etCustPhoneNo;

    @BindView(R.id.rv_customer_passbook_dialog_list)
    RecyclerView rvCustPassbookList;

    private Customer customerBean;
    private Context mContext;
    private DateTime objDate;
    private String strDate = "";
    private Date startDate_date, endDate_date;

    private MessageDialog msgBox;

    private long lastClickTime = 0;
    DatabaseHandler dbCustomer;

    private List<CustomerPassbookBean> customerPassbookBeanList;
    private CustomerPassbookAdapter customerPassbookAdapter = null;
    private DataPopulatingFromCustomerPassbook dataPopulatingFromCustomerPassbook = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        dbCustomer = new DatabaseHandler(mContext);
        dbCustomer.CreateDatabase();
        dbCustomer.OpenDatabase();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = 800;
            int height = 700;
            d.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        View rootView = inflater.inflate(R.layout.customer_passbook_dialog, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            msgBox = new MessageDialog(getActivity());
            ButterKnife.bind(this, view);

            Date d = new Date();
            CharSequence currentdate = DateFormat.format("yyyy-MM-dd", d.getTime());
            objDate = new DateTime(currentdate.toString());

            Bundle args = getArguments();
            if (args != null) {
                customerBean = args.getParcelable(Customer.CUSTOMER_PARCELABLE_KEY);
            } else {
                Log.w(TAG, "Customer passbook arguments expected, but missing");
            }

            if (customerBean != null) {
                etCustName.setText(customerBean.getStrCustName());
                etCustPhoneNo.setText(customerBean.getStrCustPhone());
            }

            customerPassbookBeanList = new ArrayList<CustomerPassbookBean>();
            //mFetchDataFromCustomerPassbookTable();
        } catch (Exception ex) {
            Log.i(TAG, "Unable to init the customer passbook dialog fragment data." + ex.getMessage());
        }
    }

    @OnClick({R.id.bt_customer_passbook_dialog_from_date, R.id.bt_customer_passbook_dialog_to_date,
            R.id.bt_customer_passbook_dialog_view, R.id.iv_customer_passbook_dialog_close})
    protected void onWidgetClick(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.bt_customer_passbook_dialog_from_date:
                From();
                break;
            case R.id.bt_customer_passbook_dialog_to_date:
                To();
                break;
            case R.id.bt_customer_passbook_dialog_view:
                viewCustomerPassBookData();
                break;
            case R.id.iv_customer_passbook_dialog_close:
                dismiss();
                break;
            default:
                break;
        }
    }

    public void From() {
        DateSelection(1);
    }

    public void To() {
        if (!etFromDate.getText().toString().equalsIgnoreCase("")) {
            DateSelection(2);

        } else {
            msgBox.Show("Warning", "Please select report FROM date");
        }
    }

    private void viewCustomerPassBookData() {
        String txtFromDate = etFromDate.getText().toString();
        String txtToDate = etToDate.getText().toString();
        if (txtFromDate.equalsIgnoreCase("") || txtToDate.equalsIgnoreCase("")) {
            msgBox.Show("Warning", "Please select From & To Date");
        } else if (startDate_date.getTime() > endDate_date.getTime()) {
            msgBox.Show("Warning", "'From Date' cannot be greater than 'To Date' ");
        } else {
            try {
                mFetchDataFromCustomerPassbookTable();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Some error occurred while displaying report");
            }
        }
    }

    private void DateSelection(final int DateType) {        // FromDate: DateType = 1 ToDate: DateType = 2
        try {
            AlertDialog.Builder dlgReportDate = new AlertDialog.Builder(getActivity());
            final DatePicker dateReportDate = new DatePicker(getActivity());

            // Initialize date picker value to business date
            dateReportDate.updateDate(objDate.getYear(), objDate.getMonth(), objDate.getDay());
            String strMessage = "";
            if (DateType == 1) {
                strMessage = "Select report Start date";
            } else {
                strMessage = "Select report End date";
            }

            dlgReportDate
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("Date Selection")
                    .setMessage(strMessage)
                    .setView(dateReportDate)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            // richa date format change

                            //strDate = String.valueOf(dateReportDate.getYear()) + "-";
                            if (dateReportDate.getDayOfMonth() < 10) {
                                strDate = "0" + String.valueOf(dateReportDate.getDayOfMonth()) + "-";
                            } else {
                                strDate = String.valueOf(dateReportDate.getDayOfMonth()) + "-";
                            }
                            if (dateReportDate.getMonth() < 9) {
                                strDate += "0" + String.valueOf(dateReportDate.getMonth() + 1) + "-";
                            } else {
                                strDate += String.valueOf(dateReportDate.getMonth() + 1) + "-";
                            }

                            strDate += String.valueOf(dateReportDate.getYear());

                            if (DateType == 1) {
                                etFromDate.setText(strDate);
                                startDate_date = new Date(dateReportDate.getYear() - 1900, dateReportDate.getMonth(), dateReportDate.getDayOfMonth());
                            } else {
                                etToDate.setText(strDate);
                                endDate_date = new Date(dateReportDate.getYear() - 1900, dateReportDate.getMonth(), dateReportDate.getDayOfMonth());

                            }
                            Log.d(TAG, "Selected Date:" + strDate);
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

    private void mFetchDataFromCustomerPassbookTable() {
        if(dataPopulatingFromCustomerPassbook == null) {
            dataPopulatingFromCustomerPassbook = new DataPopulatingFromCustomerPassbook();
            dataPopulatingFromCustomerPassbook.execute();
        }
    }

    private class DataPopulatingFromCustomerPassbook extends AsyncTask<Void , Void, Void>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Loading Customer Passbook Data...");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Cursor cursorCustomerPassBookData = null;
            try {
                if(!etFromDate.getText().toString().isEmpty() && !etToDate.getText().toString().isEmpty()) {
                    cursorCustomerPassBookData = dbCustomer.getCustomerPassbookData(customerBean.getiCustId(), etCustPhoneNo.getText().toString(),
                            String.valueOf(startDate_date.getTime()), String.valueOf(endDate_date.getTime()));
                } else{
                    cursorCustomerPassBookData = dbCustomer.getCustomerPassbookData(customerBean.getiCustId(), etCustPhoneNo.getText().toString());
                }
                if (cursorCustomerPassBookData != null && cursorCustomerPassBookData.getCount() > 0) {
                    if (cursorCustomerPassBookData.moveToFirst()) {
                        customerPassbookBeanList.clear();
                        do {
                            CustomerPassbookBean customerPassbookBean = new CustomerPassbookBean();
                            customerPassbookBean.set_id(cursorCustomerPassBookData.getInt(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_id)));
                            customerPassbookBean.setStrCustomerID(cursorCustomerPassBookData.getString(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_CUSTOMER_ID)));
                            customerPassbookBean.setStrName(cursorCustomerPassBookData.getString(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_CUSTOMER_NAME)));
                            customerPassbookBean.setStrPhoneNo(cursorCustomerPassBookData.getString(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_CUSTOMER_PHONE_NO)));
                            customerPassbookBean.setDblOpeningBalance(cursorCustomerPassBookData.getDouble(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_OPENING_BALANCE)));
                            //customerPassbookBean.setDblTotalAmount(Math.abs(cursorCustomerPassBookData.getDouble(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_TOTAL_AMOUNT))));
                            customerPassbookBean.setDblTotalAmount(cursorCustomerPassBookData.getDouble(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_TOTAL_AMOUNT)));
                            customerPassbookBean.setStrDescription(cursorCustomerPassBookData.getString(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_DESCRIPTION)));
                            String dateInMillis = cursorCustomerPassBookData.getString(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_DATE));
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                            String dateString = formatter.format(Long.parseLong(dateInMillis));
                            customerPassbookBean.setStrDate(dateString);
                            customerPassbookBean.setDblDepositAmount(cursorCustomerPassBookData.getDouble(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_DEPOSIT_AMOUNT)));
                            customerPassbookBean.setDblCreditAmount(cursorCustomerPassBookData.getDouble(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_CREDIT_AMOUNT)));
                            customerPassbookBean.setDblPettyCashTransaction(cursorCustomerPassBookData.getDouble(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_PETTY_CASH_TRANSACTION)));
                            customerPassbookBean.setDblRewardPoints(cursorCustomerPassBookData.getDouble(cursorCustomerPassBookData.getColumnIndex(DatabaseHandler.KEY_REWARD_POINTS)));
                            customerPassbookBeanList.add(customerPassbookBean);
                        } while (cursorCustomerPassBookData.moveToNext());
                    }
                } else {
                    Log.i(TAG, "No customer data selected for storing customer passbook.");
                }
            } catch (Exception ex) {
                Log.e(TAG, "Unable to fetch data from customer passbook for populate on dialog." + ex.getMessage());
            } finally {
                if (cursorCustomerPassBookData != null) {
                    cursorCustomerPassBookData.close();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (customerPassbookBeanList.size() > 0) {
                if(customerPassbookAdapter == null) {
                    rvCustPassbookList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    customerPassbookAdapter = new CustomerPassbookAdapter(getActivity(), customerPassbookBeanList);
                    rvCustPassbookList.setAdapter(customerPassbookAdapter);
                } else {
                    customerPassbookAdapter.setNotifyData(customerPassbookBeanList);
                }
            }
            if(dataPopulatingFromCustomerPassbook != null){
                dataPopulatingFromCustomerPassbook = null;
            }
            pd.dismiss();
        }
    }
}