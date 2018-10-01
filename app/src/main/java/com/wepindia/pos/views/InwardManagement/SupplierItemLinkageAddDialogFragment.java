package com.wepindia.pos.views.InwardManagement;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.Supplier_Model;
import com.wep.common.app.models.ItemInward;
import com.wep.common.app.models.ItemModel;
import com.wep.common.app.models.SupplierItemLinkageBean;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.EMOJI_FILTER;
import com.wepindia.pos.views.InwardManagement.Adapters.SupplierItemLinkageLinkingAdapter;
import com.wepindia.pos.views.InwardManagement.Listeners.OnSupplierItemLinkageAddListener;
import com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners.CustomItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SachinV on 12-03-2018.
 */

public class SupplierItemLinkageAddDialogFragment extends DialogFragment {

    @BindView(R.id.tv_SupplierItemLinkageTitle)                     TextView tv_title;

    @BindView(R.id.av_supplier_item_linkage_supplier_name)          AutoCompleteTextView actv_SupplierSearch;
    @BindView(R.id.et_supplier_item_linkage_gstin)                  EditText edtGSTIN;
    @BindView(R.id.av_supplier_item_linkage_supplier_phone)         AutoCompleteTextView actv_PhoneSearch;
    @BindView(R.id.et_supplier_item_linkage_address)                EditText edtAddress;
    @BindView(R.id.et_supplier_item_linkage_supplier_id)            EditText edtSupplierId;

    @BindView(R.id.bt_supplier_item_linkage_update_item)            Button btnUpdate;
    @BindView(R.id.bt_supplier_item_linkage_clear)                  Button btnClear;
    @BindView(R.id.bt_supplier_item_linkage_close)                  Button btnClose;

    @BindView(R.id.rb_supplier_item_linkage_list_all)               RadioButton rb_displayAll;
    @BindView(R.id.rb_supplier_item_linkage_list_barnd)             RadioButton rb_displayBrandwise;
    @BindView(R.id.rb_supplier_item_linkage_list_dept)              RadioButton rb_displayDepartmentwise;
    @BindView(R.id.rb_supplier_item_linkage_list_categ)             RadioButton rb_displayCategorywise;
    @BindView(R.id.rb_supplier_item_linkage_list_active_i)          RadioButton rb_displayActiveItem;
    @BindView(R.id.rb_supplier_item_linkage_list_inactive_i)        RadioButton rb_displayTnActiveItem;
    @BindView(R.id.rg_supplier_item_linkage_list_item)              RadioGroup rg_displayItemCriteria;

    @BindView(R.id.av_supplier_item_linkage_item_name)              AutoCompleteTextView actv_ItemSearch;
    @BindView(R.id.rv_supplier_item_linkage_radio_list)
    RecyclerView rv_RadioItemList;
    @BindView(R.id.rv_supplier_item_linkage_item_list)
    RecyclerView rv_ItemList;

    private final int CRITERIA_ITEM_CLICK = 0;
    private final int CRITERIA_CHECK_BOX_SELECTED = 1;
    private final int ITEM_CLICK = 2;
    private final int ITEM_CHECK_BOX_SELECTED = 3;
    private final int DISMISS_PROGRESS_DIALOG = 4;

    private MsgHandler msgHandler = new MsgHandler();

    private static final String TAG = SupplierItemLinkageAddDialogFragment.class.getSimpleName();

    private DatabaseHandler dbSupplierItemAddLink;

    Context myContext ;
    MessageDialog MsgBox ;
    OnSupplierItemLinkageAddListener onSupplierItemLinkageAddListener;

    SimpleCursorAdapter mAdapterItems;
    Supplier_Model supplierModel = null;
    LoadItemsAsync loadItemsAsync = null;
    ItemLinkage itemLinkage = null;
    ProgressDialog progressDialog;
    boolean itemsearchclicked = false;
    SupplierItemLinkageLinkingAdapter itemAdapter;
    SelectionCriteriaAdapter criteriaAdapter;
    ArrayList<CriteriaBean> criteriaList;
    ArrayList<ItemInward> itemList;
    int linkCount = 0, deLinkCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        myContext = getContext();
        MsgBox = new MessageDialog(myContext);
        dbSupplierItemAddLink = new DatabaseHandler(myContext);
        
        dbSupplierItemAddLink.CreateDatabase();
        dbSupplierItemAddLink.OpenDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_supplier_item_linkage_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this,fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"OnViewCreated()");
        try{
            initialiseVariables();
            setFilters();
            tv_title.setText("Link items");
            loadSupplierAutocompleteData();
            loadItemAutocompleteData();
            hideKeyboard();
            actv_ItemSearch.addTextChangedListener(filterTextWatcher);
            actv_SupplierSearch.addTextChangedListener(filterTextWatcher);
            actv_PhoneSearch.addTextChangedListener(filterTextWatcher);
        }catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
    }

    @OnClick({R.id.rb_supplier_item_linkage_list_all,
            R.id.rb_supplier_item_linkage_list_barnd,
            R.id.rb_supplier_item_linkage_list_dept,
            R.id.rb_supplier_item_linkage_list_categ,
            R.id.rb_supplier_item_linkage_list_active_i,
            R.id.rb_supplier_item_linkage_list_inactive_i})
    public void onRadioButtonClicked(RadioButton radioButton) {
        displayItemList();
    }

    @OnClick({R.id.bt_supplier_item_linkage_update_item,
            R.id.bt_supplier_item_linkage_clear,
            R.id.bt_supplier_item_linkage_close})
    protected void mBtnClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bt_supplier_item_linkage_close : close();
                break;
            case R.id.bt_supplier_item_linkage_clear : clear();
                break;
            case R.id.bt_supplier_item_linkage_update_item : updateSupplierItemLinkage();
                break;
        }
    }

    private void updateSupplierItemLinkage() {

        if (edtSupplierId.getText().toString().isEmpty()) {
            Toast.makeText(myContext, "Please select a valid supplier's firm name or phone no.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if(itemLinkage == null)
            {
                itemLinkage = new ItemLinkage();
                itemLinkage.execute();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            itemLinkage = null;
        }
    }

    private class ItemLinkage extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(myContext);
            progressDialog.setIcon(R.mipmap.ic_company_logo);
            progressDialog.setTitle(Constants.processing);
            progressDialog.setMessage("Linking your items. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if(actv_SupplierSearch.getText().toString().isEmpty())
            {
//                MsgBox.Show("Insufficient Information", "Kindly enter valid Supplier's Firm ");
                return "Kindly enter valid Supplier's Firm ";
            }
            updateItemLinkage();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (o != null)
                MsgBox.Show("Warning", (String) o);
            if (linkCount>0){
                Toast.makeText(myContext, "Items linked successfully", Toast.LENGTH_SHORT).show();
                onSupplierItemLinkageAddListener.onSupplierItemLinkageSuccess();
            } else if (deLinkCount>0){
                Toast.makeText(myContext, "Items unlinked successfully", Toast.LENGTH_SHORT).show();
                onSupplierItemLinkageAddListener.onSupplierItemLinkageSuccess();
            } else if (deLinkCount == 0 && linkCount == 0){
                Toast.makeText(myContext, "No item linked.", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            if(itemLinkage !=null)
                itemLinkage = null;
        }
    }

    private void updateItemLinkage() {
       linkCount = 0;
       deLinkCount = 0;

        if (itemList.size() > 0) {
            for (int i=0; i<itemList.size(); i++) {
                if (itemList.get(i).isSelected()) {
                    SupplierItemLinkageBean supplierItemLinkageBean = new SupplierItemLinkageBean();
                    supplierItemLinkageBean.setiSupplierID(Integer.parseInt(edtSupplierId.getText().toString().trim()));
                    supplierItemLinkageBean.setStrSupplierName(actv_SupplierSearch.getText().toString());
                    supplierItemLinkageBean.setStrSupplierPhone(actv_PhoneSearch.getText().toString());
                    supplierItemLinkageBean.setStrSupplierAddress(edtAddress.getText().toString());
                    if(!edtGSTIN.getText().toString().isEmpty()){
                        supplierItemLinkageBean.setStrGSTIN(edtGSTIN.getText().toString());
                        supplierItemLinkageBean.setStrSupplierType("Registered");
                    } else {
                        supplierItemLinkageBean.setStrGSTIN("");
                        supplierItemLinkageBean.setStrSupplierType("UnRegistered");
                    }
                    supplierItemLinkageBean.setiItemID(itemList.get(i).get_id());
                    supplierItemLinkageBean.setStrBarcode(itemList.get(i).getItemBarcode());
                    supplierItemLinkageBean.setStrItemName(itemList.get(i).getItemShortName());
                    if(itemList.get(i).getHSNCode() != null && !itemList.get(i).getHSNCode().equalsIgnoreCase("")) {
                        supplierItemLinkageBean.setStrHSNCode(itemList.get(i).getHSNCode());
                    }
                    if(itemList.get(i).getSupplyType() != null && !itemList.get(i).getSupplyType().equalsIgnoreCase("")) {
                        supplierItemLinkageBean.setStrSupplyType(itemList.get(i).getSupplyType());
                    }
                    if(itemList.get(i).getUOM() != null && !itemList.get(i).getUOM().equalsIgnoreCase("")) {
                        supplierItemLinkageBean.setStrUOM(itemList.get(i).getUOM());
                    }
                    supplierItemLinkageBean.setDblRate1(itemList.get(i).getRate());
                    supplierItemLinkageBean.setDblCGSTPer(itemList.get(i).getCGSTRate());
                    supplierItemLinkageBean.setDblUTGST_SGSTPer(itemList.get(i).getSGSTRate());
                    supplierItemLinkageBean.setDblIGSTPer(itemList.get(i).getIGSTRate());
                    supplierItemLinkageBean.setDblCessPer(itemList.get(i).getCessRate());
                    supplierItemLinkageBean.setDblCessAmount(itemList.get(i).getCessAmount());
                    supplierItemLinkageBean.setDblAdditionalCessAmount(itemList.get(i).getAdditionalCessAmount());
                    supplierItemLinkageBean.setDblPurchaseRate(itemList.get(i).getPurchaseRate());

                    Cursor duplicateLinkage = dbSupplierItemAddLink.getDuplicateSupplierItemLinkage(supplierItemLinkageBean.getiSupplierID(),
                            supplierItemLinkageBean.getiItemID(), supplierItemLinkageBean.getStrUOM());

                    if (duplicateLinkage!= null && duplicateLinkage.moveToFirst()){
                        Log.i(TAG, supplierItemLinkageBean.getStrItemName() + " is already linked to the Supplier.");
//                        MsgBox.Show("Duplicate Information", supplierItemLinkageBean.getStrItemName() + " is already linked to the Supplier.");
                        continue;
                    }

                    long lLinkItemStatus = -1;
                    lLinkItemStatus = dbSupplierItemAddLink.mLinkSupplierWithItem(supplierItemLinkageBean, Constants.INSERT);
                    if(lLinkItemStatus>0) {
                        linkCount++;
                    }

                } else {
                    SupplierItemLinkageBean supplierItemLinkageBean = new SupplierItemLinkageBean();
                    supplierItemLinkageBean.setiSupplierID(Integer.parseInt(edtSupplierId.getText().toString().trim()));
                    supplierItemLinkageBean.setiItemID(itemList.get(i).get_id());
                    supplierItemLinkageBean.setDblRate1(itemList.get(i).getRate());

//                    Cursor duplicateLinkage = dbSupplierItemAddLink.getDuplicateSupplierItemLinkage(supplierItemLinkageBean.getiSupplierID(),
//                            supplierItemLinkageBean.getiItemID(), supplierItemLinkageBean.getStrUOM());

                    try {
//                        if (duplicateLinkage!= null && duplicateLinkage.moveToFirst()){
                            long status = dbSupplierItemAddLink.mUnLinkSupplierWithItem(""+supplierItemLinkageBean.getiSupplierID(),
                                    ""+supplierItemLinkageBean.getiItemID(),
                                    ""+supplierItemLinkageBean.getDblPurchaseRate());
                            if (status > 0) {
                                Log.i(TAG, supplierItemLinkageBean.getStrItemName() + " is removed to the Supplier.");
                                deLinkCount++;
                            }
//                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
//                        if (duplicateLinkage != null)
//                            duplicateLinkage.close();
                    }
                }
            }

        } else {
            Toast.makeText(myContext, "Please select items for linking", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSupplierAutocompleteData() {
        mAdapterItems = new SimpleCursorAdapter(getActivity(), R.layout.adapter_supplier_name, null,
                new String[]{DatabaseHandler.KEY_SUPPLIERNAME, DatabaseHandler.KEY_SupplierPhone},
                new int[]{R.id.adapterName, R.id.adapterPhone},
                0);

        actv_SupplierSearch.setThreshold(1);
        actv_SupplierSearch.setAdapter(mAdapterItems);
        actv_PhoneSearch.setThreshold(1);
        actv_PhoneSearch.setAdapter(mAdapterItems);

        mAdapterItems.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                Log.i(TAG, "Item search data." + str);
                return dbSupplierItemAddLink.mGetSupplierSearchData(str);
            }
        });

        mAdapterItems.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(DatabaseHandler.KEY_SUPPLIERNAME);
                return cur.getString(index);
            }
        });

        actv_SupplierSearch.setOnItemClickListener(onItemClickListener);
        actv_PhoneSearch.setOnItemClickListener(onItemClickListener);

    }

    private void loadItemAutocompleteData() {
        mAdapterItems = new SimpleCursorAdapter(getActivity(), R.layout.auto_complete_textview_two_strings, null,
                new String[]{DatabaseHandler.KEY_ItemShortName, DatabaseHandler.KEY_ItemBarcode, DatabaseHandler.KEY_DineInPrice1},
                new int[]{R.id.tv_auto_complete_textview_item_one, R.id.tv_auto_complete_textview_two, R.id.tv_auto_complete_textview_three},
                0);

        actv_ItemSearch.setAdapter(mAdapterItems);

        mAdapterItems.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                Log.i(TAG, "Item name search data." +str);
                if(str != null) {
                    return dbSupplierItemAddLink.mGetAllItemSearchData(str);
                }
                return null;
            } });

        mAdapterItems.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(DatabaseHandler.KEY_ItemShortName);
                return cur.getString(index);
            }});

        actv_ItemSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                        itemList = generateSearchedItemArrayList(cursor);
                }
                itemAdapter = new SupplierItemLinkageLinkingAdapter(myContext, itemList, itemClickListener);
                if (criteriaList != null)
                    criteriaList.clear();
                rv_ItemList.setLayoutManager(new LinearLayoutManager(myContext));
                rv_ItemList.setAdapter(itemAdapter);
                actv_ItemSearch.setText("" );
//                criteriaAdapter.notifyDataSetChanged();
            }
        });
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
            if (cursor != null) {
//                do {
                    populateDataByCursor(cursor);
//                } while (cursor.moveToNext());
            }
            displayItemList();
            hideKeyboard();
        }
    };

    public void displayItemList() {
        try {
            if(loadItemsAsync == null)
            {
                loadItemsAsync = new LoadItemsAsync();
                loadItemsAsync.execute();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    class LoadItemsAsync extends AsyncTask<Void, Void, Void> {

        Cursor cursor = null;
        int mode = -1;
        HashMap<Integer,String> mapToBePassed = new HashMap<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(myContext);
            progressDialog.setIcon(R.mipmap.ic_company_logo);
            progressDialog.setTitle(Constants.loading);
            progressDialog.setMessage("Loading items. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            switch (rg_displayItemCriteria.getCheckedRadioButtonId()) {
                case R.id.rb_supplier_item_linkage_list_all:
                    mode = 0;
                    cursor = dbSupplierItemAddLink.getAllItemsInward();
                    break;
               /* case R.id.rb_supplier_item_linkage_list_barnd:
                    mode = 1;
                    cursor = dbSupplierItemAddLink.getAllItems_Kitchenwise();
                    break;
                case R.id.rb_supplier_item_linkage_list_dept:
                    mode = 2;
                    cursor = dbSupplierItemAddLink.getAllItems_Departmentwise();
                    break;
                case R.id.rb_supplier_item_linkage_list_categ:
                    mode = 3;
                    cursor = dbSupplierItemAddLink.getAllItems_Categorywise();
                    break;*/
                case R.id.rb_supplier_item_linkage_list_active_i:
                    mode = 4;
                    cursor = dbSupplierItemAddLink.getAllItems_ActiveItems();
                    break;
                case R.id.rb_supplier_item_linkage_list_inactive_i:
                    mode = 5;
                    cursor = dbSupplierItemAddLink.getAllItems_InactiveItems();
                    break;
                default:
                    mode = 0;
                    cursor = dbSupplierItemAddLink.getAllItemsInward();
                    break;
            }
            if(itemsearchclicked == true)
            {
                String  data = actv_ItemSearch.getText().toString();
                String[] parts = data.split("-");
                final String shortCode = parts[0].trim();
                final String itemShortName = parts[1].trim();
                final String itemBarcode = parts[2].trim();
                cursor = dbSupplierItemAddLink.getItemByItemShortName(itemShortName);
            }
            populateItemData(cursor, mode);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            rv_ItemList.setLayoutManager(new LinearLayoutManager(myContext));
            rv_RadioItemList.setLayoutManager(new LinearLayoutManager(myContext));
            rv_ItemList.setAdapter(itemAdapter);
            rv_RadioItemList.setAdapter(criteriaAdapter);
            if (progressDialog != null)
                progressDialog.dismiss();
            if(loadItemsAsync !=null)
                loadItemsAsync = null;
        }
    }

    private void populateDataByCursor(Cursor cursor) {
        if (cursor != null) {
//            do {

                supplierModel = new Supplier_Model();

                supplierModel.set_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id)));
                supplierModel.setSupplierName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUPPLIERNAME)));
                supplierModel.setSupplierGSTIN(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                supplierModel.setSupplierPhone(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierPhone)));
                supplierModel.setSupplierAddress(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierAddress)));
                supplierModel.setIsActive(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_isActive)));

//            } while (cursor.moveToNext());
        }
       if (supplierModel != null) {
           edtSupplierId.setText(""+supplierModel.get_id());
           if (supplierModel.getSupplierName() != null && !supplierModel.getSupplierName().equalsIgnoreCase(""))
               actv_SupplierSearch.setText(supplierModel.getSupplierName());
           if (supplierModel.getSupplierGSTIN() != null && !supplierModel.getSupplierGSTIN().equalsIgnoreCase(""))
                edtGSTIN.setText(supplierModel.getSupplierGSTIN());
           if (supplierModel.getSupplierPhone() != null && !supplierModel.getSupplierPhone().equalsIgnoreCase(""))
               actv_PhoneSearch.setText(supplierModel.getSupplierPhone());
           if (supplierModel.getSupplierAddress() != null && !supplierModel.getSupplierAddress().equalsIgnoreCase(""))
               edtAddress.setText(supplierModel.getSupplierAddress());
       }
    }

    private void populateItemData(Cursor cursor, int mode) {
        itemList = new ArrayList<>();
        criteriaList = new ArrayList();

        switch (mode) {
            case 0:
                itemList = generateItemArrayList(cursor, false, true);
                break;
            case 1:
                criteriaList = generateCriteriaNameList(mode, -1, false);
                break;
            case 2:
                criteriaList = generateCriteriaNameList(mode, -1, false);
                break;
            case 3:
                criteriaList = generateCriteriaNameList(mode, -1, false);
                break;
            case 4:
                itemList = generateItemArrayList(cursor, false, true);
                break;
            case 5:
                itemList = generateItemArrayList(cursor, false, true);
                break;
        }
        itemAdapter = new SupplierItemLinkageLinkingAdapter(myContext, itemList, itemClickListener);
        criteriaAdapter = new SelectionCriteriaAdapter(myContext, criteriaList, criteriaClickListener);
    }

    private ArrayList<ItemInward> generateItemArrayList(Cursor cursor, boolean isChecked, boolean isCriteriaSelected) {
        ItemInward obj;
        ArrayList<ItemInward> arrayList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {

            try{

                do {

                    obj = createItemObject(cursor);

                    if (isCriteriaSelected) {
                        if (!edtSupplierId.getText().toString().isEmpty()) {
                            Cursor duplicateLinkage = null;
                            duplicateLinkage = dbSupplierItemAddLink.getDuplicateSupplierItemLinkage(Integer.parseInt(edtSupplierId.getText().toString().trim()),
                                    obj.get_id(), obj.getUOM());
                            try {
                                if (duplicateLinkage != null && duplicateLinkage.moveToFirst()) {
                                    obj.setSelected(true);
                                } else {
                                    obj.setSelected(isChecked);
                                }
                            } catch (Exception ex){
                                Log.i(TAG,"Error on duplicate linkage error : " +ex.getMessage());
                            } finally {
                                if(duplicateLinkage != null){
                                    duplicateLinkage.close();
                                }
                            }
                        } else {
                            obj.setSelected(isChecked);
                        }
                    } else {
                        obj.setSelected(false);
                    }

                    arrayList.add(obj);

                } while (cursor.moveToNext());

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(myContext, "Some error occurred", Toast.LENGTH_SHORT).show();
            } finally {
                if(cursor != null){
                    cursor.close();
                }
            }
        }
        return arrayList;
    }

    private ArrayList<ItemInward> generateSearchedItemArrayList(Cursor cursor) {
        ItemInward obj;
        ArrayList<ItemInward> arrayList = new ArrayList<>();
        if (cursor != null) {

                obj = createItemObject(cursor);

                if (!edtSupplierId.getText().toString().isEmpty()) {
                    Cursor duplicateLinkage = dbSupplierItemAddLink.getDuplicateSupplierItemLinkage(Integer.parseInt(edtSupplierId.getText().toString().trim()),
                            obj.get_id(), obj.getUOM());
                    if (duplicateLinkage!= null && duplicateLinkage.moveToFirst()){
                            obj.setSelected(true);
                    } else {
                        obj.setSelected(false);
                    }
                } else {
                    obj.setSelected(false);
                }

                arrayList.add(obj);

        }
        return arrayList;
    }

    private ItemInward createItemObject(Cursor cursor) {

        ItemInward obj = new ItemInward();

        obj.set_id(cursor.getInt(cursor.getColumnIndex("MenuCode")));
        obj.setItemShortName(cursor.getString(cursor.getColumnIndex("ItemName")));
        obj.setItemBarcode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
        obj.setUOM(cursor.getString(cursor.getColumnIndex("UOM")));
        obj.setPurchaseRate(cursor.getDouble(cursor.getColumnIndex("AverageRate")));
        obj.setQuantity(cursor.getDouble(cursor.getColumnIndex("Quantity")));
        obj.setHSNCode(cursor.getString(cursor.getColumnIndex("HSNCode")));
        obj.setCGSTRate(cursor.getDouble(cursor.getColumnIndex("CGSTRate")));
        obj.setSGSTRate(cursor.getDouble(cursor.getColumnIndex("SGSTRate")));
        obj.setIGSTRate(cursor.getDouble(cursor.getColumnIndex("IGSTRate")));
        obj.setCessRate(cursor.getDouble(cursor.getColumnIndex("cessRate")));
        obj.setCessAmount(cursor.getDouble(cursor.getColumnIndex("cessAmount")));
//        obj.setAdditionalCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));

        return  obj;
    }

    private ArrayList<CriteriaBean> generateCriteriaNameList(int mode, int position, boolean isSelected){
        ArrayList<CriteriaBean> arrayList = new ArrayList<>();
        Cursor cursor = null;
        CriteriaBean criteriaBean;
        int _id;
        switch (mode) {
            case 1:
                    cursor = dbSupplierItemAddLink.getAllKitchen();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        _id = 1;
                        criteriaBean = new CriteriaBean();
                        criteriaBean.set_id(_id++);
                        criteriaBean.setName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_KitchenCode)));
                        criteriaBean.setSelected(false);

                        arrayList.add(criteriaBean);
                    } while (cursor.moveToNext());
                }
                if (position > -1) {
                    arrayList.get(position).setSelected(isSelected);
                }
                break;
            case 2:
                    cursor = dbSupplierItemAddLink.getAllDepartments();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        _id = 1;
                        criteriaBean = new CriteriaBean();
                        criteriaBean.set_id(_id++);
                        criteriaBean.setName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_DepartmentName)));
                        criteriaBean.setSelected(false);

                        arrayList.add(criteriaBean);
                    } while (cursor.moveToNext());
                }
                if (position > -1) {
                    arrayList.get(position).setSelected(isSelected);
                }
                break;
            case 3:
                    cursor = dbSupplierItemAddLink.getAllCategories();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        _id = 1;
                        criteriaBean = new CriteriaBean();
                        criteriaBean.set_id(_id++);
                        criteriaBean.setName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CategoryName)));
                        criteriaBean.setSelected(false);

                        arrayList.add(criteriaBean);
                    } while (cursor.moveToNext());
                }
                if (position > -1) {
                    arrayList.get(position).setSelected(isSelected);
                }
                break;
        }
        return arrayList;
    }

    private CustomItemClickListener criteriaClickListener = new CustomItemClickListener() {
        @Override
        public void onItemClick(View v, int position, boolean isChecked) {

            progressDialog = new ProgressDialog(myContext);
            progressDialog.setIcon(R.mipmap.ic_company_logo);
            progressDialog.setTitle(Constants.processing);
            progressDialog.setMessage("Linking your items. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Message msg = new Message();
            msg.what = CRITERIA_ITEM_CLICK;
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putBoolean("isChecked", isChecked);
            msg.setData(bundle);
            msgHandler.sendMessage(msg);
        }

        @Override
        public void onItemCheckedCallback(View v, int position, boolean b) {

            progressDialog = new ProgressDialog(myContext);
            progressDialog.setIcon(R.mipmap.ic_company_logo);
            progressDialog.setTitle(Constants.processing);
            progressDialog.setMessage("Linking your items. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Message msg = new Message();
            msg.what = CRITERIA_CHECK_BOX_SELECTED;
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putBoolean("isChecked", b);
            msg.setData(bundle);
            msgHandler.sendMessage(msg);

        }
    };

    private CustomItemClickListener itemClickListener = new CustomItemClickListener() {

        @Override
        public void onItemClick(View v, int position, boolean isChecked) {

        }

        @Override
        public void onItemCheckedCallback(View v, int position, boolean b) {
            ItemInward itemModel = (ItemInward) itemList.get(position);
            itemModel.setSelected(b);
        }
    };

    void initialiseVariables()
    {
        myContext = getActivity();
        MsgBox = new MessageDialog(myContext);
    }

    private void setFilters(){
        actv_SupplierSearch.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
        actv_ItemSearch.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
    }

    public void clear()
    {
        edtSupplierId.setText("");
        actv_SupplierSearch.setText("");
        edtGSTIN.setText("");
        actv_PhoneSearch.setText("");
        edtAddress.setText("");

        actv_ItemSearch.setText("");

        // Clear item list
        displayItemList();
    }

    private void close()
    {
        dismiss();
    }

    public void mInitListener(OnSupplierItemLinkageAddListener onSupplierItemLinkageAddListener){
        this.onSupplierItemLinkageAddListener = onSupplierItemLinkageAddListener;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus()!=null)
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    class MsgHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            int position;
            boolean isChecked, b;
            Bundle bundle;
            CriteriaBean bean = null;
            Cursor cursor = null, items = null;
            String name;

            switch (msg.what) {
                case CRITERIA_ITEM_CLICK:
                    bundle = msg.getData();

                    position = bundle.getInt("position", -1);
                    isChecked = bundle.getBoolean("isChecked", false);

                    //in case of any problem in using handler thread just copy from here and paste it into criteriaClickListener - onItemClick

                    bean = (CriteriaBean) criteriaList.get(position);
                    name = bean.getName();
                    itemList = new ArrayList<>();
                    switch (rg_displayItemCriteria.getCheckedRadioButtonId()) {
                        case R.id.rb_supplier_item_linkage_list_barnd:
                            cursor = dbSupplierItemAddLink.getKitchenByName(name);
                            if (cursor != null && cursor.moveToFirst()) {
                                do {
                                    items = dbSupplierItemAddLink.getAllItemByBrand(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_CategoryCode)));
                                }while (cursor.moveToNext());
                                itemList = generateItemArrayList(items, isChecked, true);
                            }
//                            criteriaList = generateCriteriaNameList(1, position, isChecked);
                            break;
                        case R.id.rb_supplier_item_linkage_list_dept:
                            cursor = dbSupplierItemAddLink.getDepartmentNameByName(name);
                            if (cursor != null && cursor.moveToFirst()) {
                                do {
                                    items = dbSupplierItemAddLink.getAllItemByDepartment(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_DepartmentCode)));
                                }while (cursor.moveToNext());
                                itemList = generateItemArrayList(items, isChecked, true);
                            }
                            break;
                        case R.id.rb_supplier_item_linkage_list_categ:
                            cursor = dbSupplierItemAddLink.getCategoriesNameByName(name);
                            if (cursor != null && cursor.moveToFirst()) {
                                do {
                                    items = dbSupplierItemAddLink.getAllItemByCategory(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_CategoryCode)));
                                }while (cursor.moveToNext());
                                itemList = generateItemArrayList(items, isChecked, true);
                            }
                            break;
                    }

                    itemAdapter = new SupplierItemLinkageLinkingAdapter(myContext, itemList, itemClickListener);
                    rv_ItemList.setLayoutManager(new LinearLayoutManager(myContext));
                    rv_ItemList.setAdapter(itemAdapter);

                    // copy till here

                    msgHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
                    break;
                case CRITERIA_CHECK_BOX_SELECTED:
                    bundle = msg.getData();

                    position = bundle.getInt("position", -1);
                    b = bundle.getBoolean("isChecked", false);

                    //in case of any problem in using handler thread just copy from here and paste it into criteriaClickListener - onItemCheckedCallback

                    bean = (CriteriaBean) criteriaList.get(position);
                    name = bean.getName();
                    itemList = new ArrayList<>();
                    switch (rg_displayItemCriteria.getCheckedRadioButtonId()) {
                        case R.id.rb_supplier_item_linkage_list_barnd:
                            cursor = dbSupplierItemAddLink.getKitchenByName(name);
                            if (cursor != null && cursor.moveToFirst()) {
                                do {
                                    items = dbSupplierItemAddLink.getAllItemByBrand(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_KitchenCode)));
                                }while (cursor.moveToNext());

                                itemList = generateItemArrayList(items, b, b);
                            }
                            criteriaList = generateCriteriaNameList(1, position, b);
                            break;
                        case R.id.rb_supplier_item_linkage_list_dept:
                            cursor = dbSupplierItemAddLink.getDepartmentNameByName(name);
                            if (cursor != null && cursor.moveToFirst()) {
                                do {
                                    items = dbSupplierItemAddLink.getAllItemByDepartment(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_DepartmentCode)));
                                }while (cursor.moveToNext());
                                itemList = generateItemArrayList(items, b, b);
                            }
                            criteriaList = generateCriteriaNameList(2, position, b);
                            break;
                        case R.id.rb_supplier_item_linkage_list_categ:
                            cursor = dbSupplierItemAddLink.getCategoriesNameByName(name);
                            if (cursor != null && cursor.moveToFirst()) {
                                do {
                                    items = dbSupplierItemAddLink.getAllItemByCategory(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_CategoryCode)));
                                }while (cursor.moveToNext());
                                itemList = generateItemArrayList(items, b, b);
                            }
                            criteriaList = generateCriteriaNameList(3, position, b);
                            break;
                    }
                    itemAdapter = new SupplierItemLinkageLinkingAdapter(myContext, itemList, itemClickListener);
                    criteriaAdapter = new SelectionCriteriaAdapter(myContext, criteriaList, criteriaClickListener);
                    rv_ItemList.setLayoutManager(new LinearLayoutManager(myContext));
                    rv_ItemList.setAdapter(itemAdapter);
                    rv_RadioItemList.setLayoutManager(new LinearLayoutManager(myContext));
                    rv_RadioItemList.setAdapter(criteriaAdapter);

                    // copy till here

                    msgHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
                    break;
                case ITEM_CLICK:
                    msgHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
                    break;
                case ITEM_CHECK_BOX_SELECTED:
                    msgHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
                    break;
                case DISMISS_PROGRESS_DIALOG:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    break;
            }
        }
    }

    private void mBarcodeSearch(String strBarcode){
        Cursor cursor = null;
        try{
            cursor = dbSupplierItemAddLink.getActiveItemssbyBarCode(strBarcode);
            if(cursor != null && cursor.getCount() > 1){
                inflateMultipleRateOption(cursor);
            } else {
                if (cursor != null && cursor.moveToFirst()) {
                    itemList = generateSearchedItemArrayList(cursor);
                    itemAdapter = new SupplierItemLinkageLinkingAdapter(myContext, itemList, itemClickListener);
                    if (criteriaList != null)
                        criteriaList.clear();
                    rv_ItemList.setLayoutManager(new LinearLayoutManager(myContext));
                    rv_ItemList.setAdapter(itemAdapter);
                    actv_ItemSearch.setText("");
                }
            }
        } catch (Exception ex){
            Log.i(TAG,"Unable to search and populate data by barcode scanner search." +ex.getMessage());
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
    }

    int checkCount =0;
    void inflateMultipleRateOption(Cursor cursor)
    {
        checkCount =0;
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_multiple_item_with_same_name, null, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        ImageView iv_close = (ImageView)view.findViewById(R.id.iv_close);
        final TableLayout tbl_rate = (TableLayout)view.findViewById(R.id.tbl_rates);
        Button btnOk = (Button) view.findViewById(R.id.btnok) ;

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actv_ItemSearch.setText("");
                dialog.dismiss();
            }
        });

        View view1 = getActivity().getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
        int count =1;

        while(cursor!=null && cursor.moveToNext())
        {

            TableRow row = new TableRow(myContext);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setBackgroundResource(R.drawable.row_background);

            CheckBox checkBox = new CheckBox(myContext);
            String item_id = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_id));
            checkBox.setText(item_id);
            checkBox.setHeight(40);
            checkBox.setTextSize(1);
            checkBox.setVisibility(View.GONE);
            //checkBox.setTextColor(getResources().getColor(R.drawable.row_item_background));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked)
                        checkCount++;
                    else
                        checkCount--;
                }
            });
            row.addView(checkBox);

            TextView tvSno = new TextView(myContext);
            tvSno.setText(""+count);
            tvSno.setHeight(50);
            count++;
            tvSno.setTextSize(20);
            tvSno.setPadding(5,0,0,0);
            row.addView(tvSno);

            TextView tvName = new TextView(myContext);
            tvName.setHeight(50);
            tvName.setTextSize(20);
            tvName.setTextColor(Color.parseColor("#000000"));
            tvName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
            row.addView(tvName);

            TextView tvMrp  = new TextView(myContext);
            tvMrp.setHeight(50);
            tvMrp.setTextSize(20);
            tvMrp.setTextColor(Color.parseColor("#000000"));
            String mrp = String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice1)));
            tvMrp.setText(mrp);
            row.addView(tvMrp);

            row.setTag("TAG");
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (String.valueOf(view.getTag()) == "TAG") {
                        TableRow Row = (TableRow) view;
                        CheckBox checkBox1= (CheckBox) Row.getChildAt(0);
                        int id = Integer.parseInt(checkBox1.getText().toString());
                        Cursor cursor = dbSupplierItemAddLink.getItemByID(id);
                        if (cursor != null && cursor.moveToFirst()){
                                itemList = generateSearchedItemArrayList(cursor);
                                itemAdapter = new SupplierItemLinkageLinkingAdapter(myContext, itemList, itemClickListener);
                                if (criteriaList != null)
                                    criteriaList.clear();
                                rv_ItemList.setLayoutManager(new LinearLayoutManager(myContext));
                                rv_ItemList.setAdapter(itemAdapter);
                                actv_ItemSearch.setText("");
                        }else {
                            Toast.makeText(myContext, "Some error occurred", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Item not found");
                        }
                        dialog.dismiss();
                    }
                }
            });
            tbl_rate.addView(row);
        }
    }


    //testing on 10/05/2018 start
    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int st, int ct,
                                      int af) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 1 || start >= s.length() || start < 0)
                return;

            if (s.subSequence(start, start + 1).toString().equalsIgnoreCase("\n")) {
                if(actv_SupplierSearch.isFocused()){
                    actv_SupplierSearch.setText("");
                }
                if(actv_PhoneSearch.isFocused()){
                    actv_PhoneSearch.setText("");
                }
                String s_text = start > 0 ? s.subSequence(0, start).toString() : "";
                s_text += start < s.length() ? s.subSequence(start + 1, s.length()).toString() : "";
                Log.i(TAG, "Scanned data in  supplier item linkage: " + s_text);
                actv_ItemSearch.setText(s_text);
                actv_ItemSearch.setSelection(s_text.length());
                if(actv_SupplierSearch.getText().toString().isEmpty()){
                    actv_ItemSearch.setText("");
                    Toast.makeText(getActivity(),"Please select supplier and search item.",Toast.LENGTH_SHORT).show();
                } else {
                    mBarcodeSearch(actv_ItemSearch.getText().toString());
                }
            }
        }

    };
    //testing 0n 10/05/2018 ends

}
