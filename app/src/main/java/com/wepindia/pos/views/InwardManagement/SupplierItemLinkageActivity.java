package com.wepindia.pos.views.InwardManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.Supplier_Model;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.ItemInward;
import com.wep.common.app.models.SupplierItemLinkageBean;
import com.wep.common.app.models.SupplierItemLinkageModel;
import com.wep.common.app.views.WepButton;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.views.InwardManagement.Adapters.SupplierItemLinkageAdapter;
import com.wepindia.pos.views.InwardManagement.Adapters.SupplierSuggestionAdapter;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.views.InwardManagement.Listeners.OnSupplierItemLinkageAddListener;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SupplierItemLinkageActivity extends WepBaseActivity implements OnSupplierItemLinkageAddListener {

    Context myContext;
    DatabaseHandler dbSupplierItemLink;
    public MessageDialog msgBox;

    private Toolbar toolbar;
    String strMenuCode,  strUserName = "";

    private static final String TAG = SupplierItemLinkageActivity.class.getName();
    private final int RESET = 1;

    String tx = "";
    String linefeed = "";

    @BindView(R.id.lv_supplier_item_linkage_list)
    ListView lvSupplierItemLinkage;

    @BindView(R.id.rb_supplier_item_linkage_list_all)
    RadioButton rb_displayAll;
//    @BindView(R.id.rb_supplier_item_linkage_list_active_s)
//    RadioButton rb_displayActiveSupplier;
//    @BindView(R.id.rb_supplier_item_linkage_list_inactive_s)
//    RadioButton rb_displayInactiveSupplier;
    @BindView(R.id.rb_supplier_item_linkage_list_active_i)
    RadioButton rb_displayActiveItem;
    @BindView(R.id.rb_supplier_item_linkage_list_inactive_i)
    RadioButton rb_displayInactiveItem;
    @BindView(R.id.rg_supplier_item_linkage_list)
    RadioGroup rg_displayListCriteria;

    @BindView(R.id.btnLink)
    Button btnLink;

    ArrayList<HashMap<String, String>> listName;
    SupplierItemLinkageBean supplierItemLinkageBean;
    List<SupplierItemLinkageBean> supplierItemLinkageBeanList;

    SupplierItemLinkageAdapter supplierItemLinkageAdapter = null;

    Supplier_Model supplier_model = null;
    ItemInward itemMasterBean = null;
    boolean supplierFirmFirst = false;
    int checkCount =0;
    ProgressDialog progressDialog;
    LoadListAsync loadListAsync  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_item_supplier_linkage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        myContext = this;
        strUserName = getIntent().getStringExtra("USER_NAME");
        try
        {
            Date d = new Date();
            CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
            com.wep.common.app.ActionBarUtils.setupToolbar(this,toolbar,getSupportActionBar(),"Supplier Item Linkage",strUserName," Date:"+s.toString());

            myContext = this;
            msgBox = new MessageDialog(myContext);
            dbSupplierItemLink = new DatabaseHandler(myContext);

            dbSupplierItemLink.CloseDatabase();
            dbSupplierItemLink.CreateDatabase();
            dbSupplierItemLink.OpenDatabase();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        displayItemList();
    }

    @OnClick({R.id.btnLink,
            R.id.bt_supplier_item_linkage_unlink_item,
            R.id.bt_supplier_item_linkage_clear,
            R.id.bt_supplier_item_linkage_update_item,
            R.id.rb_supplier_item_linkage_list_all,
//            R.id.rb_supplier_item_linkage_list_active_s,
//            R.id.rb_supplier_item_linkage_list_inactive_s,
            R.id.rb_supplier_item_linkage_list_active_i,
            R.id.rb_supplier_item_linkage_list_inactive_i})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnLink:
                FragmentManager fm = getSupportFragmentManager();
                SupplierItemLinkageAddDialogFragment myFragment =
                        (SupplierItemLinkageAddDialogFragment) fm.findFragmentByTag("Link new items");
                if (myFragment != null && myFragment.isVisible()) {
                    return;
                }
                
                SupplierItemLinkageAddDialogFragment supplierItemLinkageAddDialogFragment = new SupplierItemLinkageAddDialogFragment();
                supplierItemLinkageAddDialogFragment.mInitListener(this);
                supplierItemLinkageAddDialogFragment.show(fm, "Link new items");
                break;
            case R.id.rb_supplier_item_linkage_list_all:
//            case R.id.rb_supplier_item_linkage_list_active_s:
//            case R.id.rb_supplier_item_linkage_list_inactive_s:
            case R.id.rb_supplier_item_linkage_list_active_i:
            case R.id.rb_supplier_item_linkage_list_inactive_i:
                displayItemList();
                break;
            default:
                break;
        }
    }

    public void displayItemList() {
        try {
            if(loadListAsync == null)
            {
                loadListAsync = new LoadListAsync();
                loadListAsync.execute();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onSupplierItemLinkageSuccess() {
        displayItemList();
    }

    class LoadListAsync extends AsyncTask<Void, Void, Void> {

        int selection = -1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(myContext);
            progressDialog.setIcon(R.mipmap.ic_company_logo);
            progressDialog.setTitle(Constants.loading);
            progressDialog.setMessage("Loading supplier item linkage data. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (rg_displayListCriteria.getCheckedRadioButtonId()) {
                case R.id.rb_supplier_item_linkage_list_all:
                    selection = -1;
                    break;
//                case R.id.rb_supplier_item_linkage_list_active_s:
//                    selection = 2;
//                    break;
//                case R.id.rb_supplier_item_linkage_list_inactive_s:
//                    selection = 3;
//                    break;
                case R.id.rb_supplier_item_linkage_list_active_i:
                    selection = 4;
                    break;
                case R.id.rb_supplier_item_linkage_list_inactive_i:
                    selection = 5;
                    break;
                default:
                    selection = -1;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (selection) {
                case -1:
                    mPopulateDataToAdapterList(-1, "", -1);
                    break;
                case 2:
                    mPopulateDataToAdapterList(2, "", -1);
                    break;
                case 3:
                    mPopulateDataToAdapterList(3, "", -1);
                    break;
                case 4:
                    mPopulateDataToAdapterList(4, "", -1);
                    break;
                case 5:
                    mPopulateDataToAdapterList(5, "", -1);
                    break;
                default:
                    mPopulateDataToAdapterList(-1, "", -1);
            }
            if (progressDialog != null)
                progressDialog.dismiss();
            if(loadListAsync !=null)
                loadListAsync = null;
        }
    }

    /*
     * Mode = -1 means all data to be shown
     * Mode = 0 means data acc to supplier phone to be shown
     * Mode = 1 means data acc to item id to be shown
     * Mode = 2 active supplier
     * Mode = 3 inactive supplier
     * Mode = 4 active items
     * Mode = 5 inactive items
     */

    private void mPopulateDataToAdapterList(int mode, String strPhone, int itemId){
        Cursor cSupplierItemLinkage = null;
        try {

            switch (mode) {
                case -1:
                    cSupplierItemLinkage = dbSupplierItemLink.getSupplierItemlinkage();
                    break;
                case 0:
                    cSupplierItemLinkage = dbSupplierItemLink.getSupplierItemlinkageByPhone(strPhone);
                    break;
                case 1:
                    cSupplierItemLinkage = dbSupplierItemLink.getSupplierItemlinkageByItem(itemId);
                    break;
                case 2:
                    cSupplierItemLinkage = dbSupplierItemLink.getSupplierItemlinkageBySupplierMode(1);
                    break;
                case 3:
                    cSupplierItemLinkage = dbSupplierItemLink.getSupplierItemlinkageBySupplierMode(0);
                    break;
                case 4:
                    cSupplierItemLinkage = dbSupplierItemLink.getSupplierItemlinkageByItemMode(1);
                    break;
                case 5:
                    cSupplierItemLinkage = dbSupplierItemLink.getSupplierItemlinkageByItemMode(0);
                    break;
            }

            if (cSupplierItemLinkage != null && cSupplierItemLinkage.moveToFirst()) {
                supplierItemLinkageBeanList = new ArrayList<SupplierItemLinkageBean>();
                do {
                    supplierItemLinkageBean = new SupplierItemLinkageBean();
                    supplierItemLinkageBean.setStrSupplierName(cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_SUPPLIERNAME)));
                    supplierItemLinkageBean.setStrSupplierPhone(cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_SupplierPhone)));
                    supplierItemLinkageBean.setStrSupplierAddress(cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_SupplierAddress)));
                    supplierItemLinkageBean.setiSupplierID(cSupplierItemLinkage.getInt(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_SupplierCode)));
                    if (cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_GSTIN)) != null) {
                        supplierItemLinkageBean.setStrGSTIN(cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                    }
                    supplierItemLinkageBean.set_id(cSupplierItemLinkage.getInt(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_id)));
                    supplierItemLinkageBean.setiItemID(cSupplierItemLinkage.getInt(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_ItemId)));
                    if(cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)) != null) {
                        supplierItemLinkageBean.setStrBarcode(cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
                    }
                    supplierItemLinkageBean.setStrItemName(cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_ItemName)));
                    supplierItemLinkageBean.setDblPurchaseRate(cSupplierItemLinkage.getDouble(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)));
                    supplierItemLinkageBean.setDblRate1(cSupplierItemLinkage.getDouble(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_Rate)));
                    supplierItemLinkageBean.setStrHSNCode(cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
                    supplierItemLinkageBean.setStrUOM(cSupplierItemLinkage.getString(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_UOM)));
                    supplierItemLinkageBean.setDblCGSTPer(cSupplierItemLinkage.getDouble(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
                    supplierItemLinkageBean.setDblUTGST_SGSTPer(cSupplierItemLinkage.getDouble(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
                    supplierItemLinkageBean.setDblIGSTPer(cSupplierItemLinkage.getDouble(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
                    supplierItemLinkageBean.setDblCessPer(cSupplierItemLinkage.getDouble(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_cessRate)));
                    supplierItemLinkageBean.setDblCessAmount(cSupplierItemLinkage.getDouble(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_cessAmountPerUnit)));
                    supplierItemLinkageBean.setDblAdditionalCessAmount(cSupplierItemLinkage.getDouble(cSupplierItemLinkage.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));
                    supplierItemLinkageBeanList.add(supplierItemLinkageBean);
                }while(cSupplierItemLinkage.moveToNext());
            } else {
                if (supplierItemLinkageBeanList != null) {
                    supplierItemLinkageBeanList.clear();
                    if(supplierItemLinkageAdapter == null)
                    {
                        supplierItemLinkageAdapter = new SupplierItemLinkageAdapter(myContext, supplierItemLinkageBeanList);
                        lvSupplierItemLinkage.setAdapter(supplierItemLinkageAdapter);
                    } else
                    {
                        supplierItemLinkageAdapter.notifyDataSetChanged(supplierItemLinkageBeanList);
                    }
                }
            }
        }catch (Exception ex){
            Log.i(TAG,"Unable to fetch data from Supplier item linkage table based on Supplier phone no. " +ex.getMessage());
        }finally {
            if(cSupplierItemLinkage != null){
                cSupplierItemLinkage.close();
            }
        }

        if(supplierItemLinkageBeanList != null && supplierItemLinkageBeanList.size() > 0){
            if(supplierItemLinkageAdapter == null)
            {
                supplierItemLinkageAdapter = new SupplierItemLinkageAdapter(myContext, supplierItemLinkageBeanList);
                lvSupplierItemLinkage.setAdapter(supplierItemLinkageAdapter);
            }else
            {
                supplierItemLinkageAdapter.notifyDataSetChanged(supplierItemLinkageBeanList);
            }
        } else {
            switch (mode) {
                case -1:
                    break;
                case 0:
                    msgBox.Show("Supplier Item Linkage","No Items linked with this Supplier");
                    break;
                case 1:
                    msgBox.Show("Supplier Item Linkage","This item is not linked with any Supplier.");
                    break;
                default:
                    break;
            }
        }
    }

    void fill(Cursor cursor)
    {
        itemMasterBean = new ItemInward();
        itemMasterBean.set_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id)));
        itemMasterBean.setItemShortName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)) != null) {
            itemMasterBean.setHSNCode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
        }
        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)) != null) {
            itemMasterBean.setUOM(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)));
        }
        if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)) != null && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)).isEmpty()) {
            itemMasterBean.setItemBarcode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
        }
        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_Quantity)) != null &&
                !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_Quantity)).equals("")) {
            itemMasterBean.setQuantity(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Quantity)));
        }

        if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)) != null && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)).isEmpty()) {
            itemMasterBean.setSupplyType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)));
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)) > 0) {
            itemMasterBean.setPurchaseRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)));
        } else{
            itemMasterBean.setPurchaseRate(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)) > 0) {
            itemMasterBean.setCGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
        } else {
            itemMasterBean.setCGSTRate(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)) > 0) {
            itemMasterBean.setSGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
        } else {
            itemMasterBean.setSGSTRate(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)) > 0) {
            itemMasterBean.setIGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
        } else {
            itemMasterBean.setIGSTRate(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)) > 0) {
            itemMasterBean.setCessRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)));
        } else {
            itemMasterBean.setCessRate(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmountPerUnit)) > 0) {
            itemMasterBean.setCessAmountPerUnit(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmountPerUnit)));
        } else {
            itemMasterBean.setCessAmountPerUnit(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)) > 0) {
            itemMasterBean.setAdditionalCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));
        } else {
            itemMasterBean.setAdditionalCessAmount(0);
        }
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
    }

}
