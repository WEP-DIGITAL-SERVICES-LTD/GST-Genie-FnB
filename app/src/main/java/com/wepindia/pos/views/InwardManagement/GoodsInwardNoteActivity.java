package com.wepindia.pos.views.InwardManagement;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.Supplier_Model;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.PurchaseOrderBean;
import com.wep.common.app.models.SupplierItemLinkageBean;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.DateTime;
import com.wepindia.pos.GenericClasses.DecimalDigitsInputFilter;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.EMOJI_FILTER;
import com.wepindia.pos.utils.InstantAutoComplete;
import com.wepindia.pos.utils.SendBillInfoToCustUtility;
import com.wepindia.pos.utils.Validations;
import com.wepindia.pos.views.InwardManagement.Adapters.PurchaseOrderAdapter;
import com.wepindia.pos.views.InwardManagement.Adapters.SupplierSuggestionAdapter;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.views.InwardManagement.Listeners.OnPurchaseOrderItemListListener;
import com.wepindia.pos.views.InwardManagement.PdfPurchaseOrder.CreatePdfPO;
import com.wepindia.pos.views.InwardManagement.PdfPurchaseOrder.PdfPOBean;
import com.wepindia.pos.views.InwardManagement.PdfPurchaseOrder.PdfPOItemBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoodsInwardNoteActivity extends WepBaseActivity implements OnPurchaseOrderItemListListener {

    Context myContext;
    DatabaseHandler dbGoodsInwardNote;
    public MessageDialog MsgBox;
    private Toolbar toolbar;
    private String strUserName, strUserId;

    private static final String TAG = GoodsInwardNoteActivity.class.getName();
    private final int ADD = 0;
    private final int UPDATE = 1;
    String businessDate="";

    @BindView(R.id.bt_goods_inward_note_save)
    Button btnSaveNote;
    @BindView(R.id.bt_goods_inward_note_goods_inward)
    Button btnGoodsInward;
    @BindView(R.id.bt_goods_inward_note_clear)
    Button btnClear;
    @BindView(R.id.bt_goods_inward_note_add_item)
    Button btnAddItem;
    @BindView(R.id.bt_goods_inward_note_supplier_add)
    Button btnSupplierAdd;

    @BindView(R.id.av_goods_inward_note_supplier_name)
    AutoCompleteTextView avSupplierName;
    @BindView(R.id.av_goods_inward_note_po)
    InstantAutoComplete avPurchaseOrder;
    @BindView(R.id.av_goods_inward_note_item_name)
    AutoCompleteTextView avItemName;

    @BindView(R.id.et_goods_inward_note_supplier_phone)
    EditText edtPhone;
    @BindView(R.id.et_goods_inward_note_supplier_address)
    EditText edtAddress;
    @BindView(R.id.et_goods_inward_note_supplier_gstin)
    EditText edtGSTIN;
    @BindView(R.id.et_goods_inward_note_Qty)
    EditText edtQty;
    @BindView(R.id.et_goods_inward_note_mrp)
    EditText edtMrp;
    @BindView(R.id.et_goods_inward_note_additional_charge_name)
    EditText edtAdditionalChargeName;
    @BindView(R.id.et_goods_inward_note_additional_amount)
    EditText edtAdditionalAmt;
    @BindView(R.id.et_goods_inward_note_sub_total)
    EditText edtSubTotal;
    @BindView(R.id.et_goods_inward_note_grand_total)
    EditText edtGrandTotal;
    @BindView(R.id.et_goods_inward_note_invoice_no)
    EditText edtInvoiceNo;
    @BindView(R.id.et_goods_inward_note_invoice_date)
    EditText edtInvoiceDate;
    @BindView(R.id.et_goods_inward_PurchaseOrderId)
    EditText edtPurchaseOrderId;

    @BindView(R.id.et_goods_inward_note_purchase_rate)
    EditText edtPurchaseRate;

    @BindView(R.id.sp_goods_inward_note_state_code)
    Spinner spStateCode;

    @BindView(R.id.cb_goods_inward_note_state_code)
    CheckBox cbStateCode;
    @BindView(R.id.cb_goods_inward_note_additional_charge)
    CheckBox cbAdditionalChargeStatus;
    @BindView(R.id.cb_goods_inward_note_all_items)
    CheckBox cbSearchInAllItems;

    @BindView(R.id.lv_goods_inward_note_list)
    ListView lvPurchaseOrderList;

    @BindView(R.id.iv_goods_inward_note_invoice_date)
    ImageView ivInvoiceDate;

    SimpleCursorAdapter mAdapterSupplierName, mAdapterItemData, mAdapterPurchaseOrderNoData;

    Supplier_Model supplier_model;
    String[] arrayPOS;
    boolean searchInAllItems = false;
    boolean updateItem = false;

    PurchaseOrderBean purchaseOrderBean;
    PurchaseOrderBean updatePurchaseOrderBean;

    PurchaseOrderAdapter purchaseOrderAdapter = null;
    List<PurchaseOrderBean> purchaseOrderBeanList = null;

    private Boolean machine_changed_edittext = false;

    String strDate= "";
    ArrayList<HashMap<String, String>> listName;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goods_inward_note);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        dbGoodsInwardNote = new DatabaseHandler(GoodsInwardNoteActivity.this);
        myContext = this;
        MsgBox =  new MessageDialog(myContext);

        try {
            strUserName = getIntent().getStringExtra("USER_NAME");
            strUserId = getIntent().getStringExtra("USER_ID");


            //tvTitleUserName.setText(strUserName.toUpperCase());
            Date d = new Date();
            CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
            //tvTitleDate.setText("Date : " + s);

            com.wep.common.app.ActionBarUtils.setupToolbar(GoodsInwardNoteActivity.this,toolbar,getSupportActionBar(),"Goods Inward Note",strUserName," Date:"+s.toString());

            dbGoodsInwardNote.CreateDatabase();
            dbGoodsInwardNote.OpenDatabase();

            Cursor businessDate_cursor = dbGoodsInwardNote.getBusinessDate();
            if(businessDate_cursor!=null && businessDate_cursor.moveToNext())
            {
                businessDate = businessDate_cursor.getString(businessDate_cursor.getColumnIndex(DatabaseHandler.KEY_BusinessDate));
            }else
            {
                Date date = new Date();
                businessDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
            }

            purchaseOrderBeanList = new ArrayList<PurchaseOrderBean>();
            cbStateCode.setChecked(false);
            spStateCode.setEnabled(false);

            cbAdditionalChargeStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(!isChecked)
                    {
                        edtAdditionalChargeName.setEnabled(false);
                        edtAdditionalAmt.setEnabled(false);
                        if(!edtAdditionalAmt.getText().toString().isEmpty()){
                            edtAdditionalAmt.setText("");
                            edtGrandTotal.setText(""+mCalculateGrandTotal(0));
                        }
                        edtAdditionalChargeName.setText("");
                    } else {
                        edtAdditionalChargeName.setEnabled(true);
                        edtAdditionalAmt.setEnabled(true);
                    }
                }
            });

            cbStateCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked)
                    {
                        spStateCode.setSelection(0);
                        spStateCode.setEnabled(false);
                        if(purchaseOrderAdapter== null || purchaseOrderBeanList == null || purchaseOrderBeanList.size()==0)
                            return;
                        for(PurchaseOrderBean po : purchaseOrderBeanList)
                        {
                            double sgstAmt =0, cgstAmt =0;
                            double taxval = po.getDblTaxableValue();
                            sgstAmt= taxval*po.getDblSGSTRate()/100;
                            cgstAmt= taxval*po.getDblCGSTRate()/100;
                            po.setDblIGSTAmount(0);
                            po.setDblCGSTAmount(cgstAmt);
                            po.setDblSGSTAmount(sgstAmt);
                        }
                        purchaseOrderAdapter.notifyDataSetChanged(purchaseOrderBeanList);
                    }
                    else
                    {
                        spStateCode.setSelection(0);
                        spStateCode.setEnabled(true);
                        if(purchaseOrderAdapter== null || purchaseOrderBeanList == null || purchaseOrderBeanList.size()==0)
                            return;
                        for(PurchaseOrderBean po : purchaseOrderBeanList)
                        {
                            double sgst = po.getDblSGSTAmount();
                            double cgst = po.getDblCGSTAmount();
                            po.setDblIGSTAmount(sgst+cgst);
                            po.setDblCGSTAmount(0);
                            po.setDblSGSTAmount(0);
                        }
                        purchaseOrderAdapter.notifyDataSetChanged(purchaseOrderBeanList);
                    }
                }
            });

            cbSearchInAllItems.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                        searchInAllItems = true;
                    else
                        searchInAllItems = false;
                }
            });

            edtAdditionalAmt.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (! machine_changed_edittext) {
                        machine_changed_edittext = true;
                        if (!s.toString().equals("")) {
                            if (s.toString().equals(".")) {
                                edtAdditionalAmt.setText("0.");
                                edtAdditionalAmt.setSelection(edtAdditionalAmt.getText().length());
                            }
                            else
                                edtGrandTotal.setText(String.format("%.2f", mCalculateGrandTotal(Double.parseDouble(s.toString()))));
                        } else {
                            edtGrandTotal.setText(edtSubTotal.getText().toString());
                        }
                        machine_changed_edittext = false;
                    }
                }
            });

            lvPurchaseOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    btnAddItem.setText("Update Item");
                    updatePurchaseOrderBean = purchaseOrderBeanList.get(i);
                    avItemName.setText(updatePurchaseOrderBean.getStrItemName());
                    edtMrp.setText(""+updatePurchaseOrderBean.getDblRate());
                    edtQty.setText(""+updatePurchaseOrderBean.getDblQuantity());
                    edtPurchaseRate.setText(""+updatePurchaseOrderBean.getDblPurchaseRate());

                    updateItem = true;
                }
            });

            avItemName.addTextChangedListener(filterTextWatcher);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AlertDialog.Builder msg = new AlertDialog.Builder(myContext);
            msg.setMessage(""+e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initAutoCompleteTextDataForSupplierName();
        mPopulateStateCodeSpinnerData();
        setFilters();
    }

    @OnClick({R.id.bt_goods_inward_note_save, R.id.bt_goods_inward_note_goods_inward, R.id.bt_goods_inward_note_clear,
            R.id.bt_goods_inward_note_add_item, R.id.bt_goods_inward_note_supplier_add, R.id.iv_goods_inward_note_invoice_date})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_goods_inward_note_save:
                try {
                    long i = savePurchaseOrder(0);
                    if (i > -1) {
                        mClear();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_goods_inward_note_goods_inward:
                mValidateGoodsInwardNote();
                break;
            case R.id.bt_goods_inward_note_clear:
                mClear();
                break;
            case R.id.bt_goods_inward_note_add_item:
                if (updateItem)
                    mValidateAndUpdatingListAdapter(); // Updating item
                else
                    mValidateAndAddingListAdapter(); // Adding Item and updating quantity
                break;
            case R.id.bt_goods_inward_note_supplier_add:
               /* Fragment fragment = new SupplierDetailsFragment();
                if (fragment != null) {
                    FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }*/
                break;
            case R.id.iv_goods_inward_note_invoice_date:
                mDateSelection_inward();
                break;
            default:
                break;
        }
    }

    private void initAutoCompleteTextDataForSupplierName(){

        Cursor cursor = null;
        try {
            cursor = dbGoodsInwardNote.getAllSuppliersByMode(1);
            listName = new ArrayList<HashMap<String, String>>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("name", cursor.getString(cursor.getColumnIndex("SupplierName")));
                    data.put("phone", cursor.getString(cursor.getColumnIndex("SupplierPhone")));
                    listName.add(data);
                } while (cursor.moveToNext());
            }
            SupplierSuggestionAdapter dataAdapter = new SupplierSuggestionAdapter(this, R.layout.adapter_supplier_name, listName);
            avSupplierName.setThreshold(1);
            avSupplierName.setAdapter(dataAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            MsgBox.Show("Error",e.getMessage());
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }

        avSupplierName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> data = (HashMap<String, String>) parent.getAdapter().getItem(position);

                String supplierphone_str = data.get("phone");
                avSupplierName.setText(data.get("name"));
                Cursor supplierdetail_cursor = dbGoodsInwardNote.getSupplierDetailsByPhone(supplierphone_str); // TODO: changed here
                if (supplierdetail_cursor!=null && supplierdetail_cursor.moveToFirst())
                {
                    supplier_model = new Supplier_Model();
                    supplier_model.set_id(supplierdetail_cursor.getInt(supplierdetail_cursor.getColumnIndex("_id")));
                    supplier_model.setSupplierName(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierName")));
                    supplier_model.setSupplierPhone(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierPhone")));
                    supplier_model.setSupplierEmail(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierEmail")));
                    supplier_model.setSupplierAddress(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierAddress")));
                    supplier_model.setSupplierGSTIN(supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("GSTIN")));
                    if (!supplier_model.getSupplierGSTIN().isEmpty())
                        supplier_model.setSupplierType("Registered");
                    else
                        supplier_model.setSupplierType("UnRegistered");
                    supplier_model.setIsActive(supplierdetail_cursor.getInt(supplierdetail_cursor.getColumnIndex("isActive")));
                }
                // Update the parent class's TextView
                if(supplier_model != null){
                    edtPhone.setText(supplier_model.getSupplierPhone());
                    edtAddress.setText(supplier_model.getSupplierAddress());
//                    if(!supplier_model.getSupplierGSTIN().isEmpty()){
                    edtGSTIN.setText(supplier_model.getSupplierGSTIN());
//                    }
                    initAutoCompleteTextDataForItems(supplier_model.get_id());
                    initAutoCompleteTextDataForPurchaseOrderNo(supplier_model.get_id());
                }
            }
        });

//        mAdapterSupplierName = new SimpleCursorAdapter(this, R.layout.auto_complete_textview_two_strings, null,
//                new String[] {DatabaseHandler.KEY_SUPPLIERNAME, DatabaseHandler.KEY_SupplierPhone},
//                new int[] {R.id.tv_auto_complete_textview_item_one, R.id.tv_auto_complete_textview_two},
//                0);
//        avSupplierName.setThreshold(1);
//        avSupplierName.setAdapter(mAdapterSupplierName);
//
//        mAdapterSupplierName.setFilterQueryProvider(new FilterQueryProvider() {
//            public Cursor runQuery(CharSequence str) {
//                Log.i(TAG, "Supplier name search data." +str);
//                return dbGoodsInwardNote.mGetSupplierDetails(str);
//            } });
//
//        mAdapterSupplierName.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
//            public CharSequence convertToString(Cursor cur) {
//                int index = cur.getColumnIndex(DatabaseHandler.KEY_SUPPLIERNAME);
//                return cur.getString(index);
//            }});
//
//        avSupplierName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> listView, View view,
//                                    int position, long id) {
//                // Get the cursor, positioned to the corresponding row in the
//                // result set
//
//                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
//
//                // Get the state's capital from this row in the database.
//                if(cursor != null){
//                    supplier_model = new Supplier_Model();
//                    supplier_model.setSupplierPhone(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierPhone)));
//                    supplier_model.setSupplierId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierCode)));
//                    if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)) != null && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)).isEmpty()){
//                        supplier_model.setSupplierGSTIN(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
//                    }
//                    supplier_model.setSupplierAddress(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierAddress)));
//                    supplier_model.setSupplierType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierType)));
//                }
//                // Update the parent class's TextView
//                if(supplier_model != null){
//                    edtPhone.setText(supplier_model.getSupplierPhone());
//                    edtAddress.setText(supplier_model.getSupplierAddress());
//                    if(!supplier_model.getSupplierGSTIN().isEmpty()){
//                        edtGSTIN.setText(supplier_model.getSupplierGSTIN());
//                    }
//                    initAutoCompleteTextDataForItems(supplier_model.get_id());
//                    initAutoCompleteTextDataForPurchaseOrderNo(supplier_model.get_id());
//                }
//            }
//        });
    }

    private void mPopulateStateCodeSpinnerData(){
        arrayPOS = getResources().getStringArray(R.array.poscode);
        //Creating the ArrayAdapter instance having the POSCode list
        ArrayAdapter adapterPOS = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayPOS);
        adapterPOS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spStateCode.setAdapter(adapterPOS);
    }

    private void initAutoCompleteTextDataForItems(final int iSupplierId){
        mAdapterItemData = new SimpleCursorAdapter(this, R.layout.auto_complete_textview_two_strings, null,
                new String[]{DatabaseHandler.KEY_ItemShortName, DatabaseHandler.KEY_ItemBarcode, DatabaseHandler.KEY_AverageRate},
                new int[]{R.id.tv_auto_complete_textview_item_one, R.id.tv_auto_complete_textview_two,
                        R.id.tv_auto_complete_textview_three},
                0);
        avItemName.setThreshold(1);
        avItemName.setAdapter(mAdapterItemData);

        mAdapterItemData.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                Log.i(TAG, "Item name search data in Supplier's Firm item linkage table." +str);
                if(str != null && !avSupplierName.getText().toString().isEmpty()) {
                    if (!searchInAllItems)
                        return dbGoodsInwardNote.mGetPurchaseOrderItems(str,iSupplierId);
                    else
                        return dbGoodsInwardNote.mGetItemInwardSearchData(str);
                }
                return null;
            } });

        mAdapterItemData.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(DatabaseHandler.KEY_ItemShortName);
                return cur.getString(index);
            }});

        avItemName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                // Get the state's capital from this row in the database.
                if(cursor != null){
                    purchaseOrderBean = new PurchaseOrderBean();
                    if (!searchInAllItems) {
                        purchaseOrderBean.setiItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ItemId)));
                        purchaseOrderBean.setStrSupplierId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierCode)));
                        purchaseOrderBean.setStrSupplierName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUPPLIERNAME)));
                        purchaseOrderBean.setStrSupplierPhone(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierPhone)));
                        purchaseOrderBean.setStrSupplierAddress(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierAddress)));
                        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)) != null){
                            purchaseOrderBean.setStrSupplierGSTIN(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                        } else {
                            purchaseOrderBean.setStrSupplierGSTIN("");
                        }
                        if (!purchaseOrderBean.getStrSupplierGSTIN().isEmpty())
                            purchaseOrderBean.setStrSupplierPOS(purchaseOrderBean.getStrSupplierGSTIN().substring(0, 2));
                        else
                            purchaseOrderBean.setStrSupplierPOS("");

                        purchaseOrderBean.setStrBarcode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
                        purchaseOrderBean.setStrItemName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));

                        purchaseOrderBean.setDblPurchaseRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)));
                        purchaseOrderBean.setDblRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Rate)));

                        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)) != null){
                            purchaseOrderBean.setStrSupplyType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)));
                        }
                        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierType)) != null){
                            purchaseOrderBean.setStrSupplierType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierType)));
                        }
                        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)) != null){
                            purchaseOrderBean.setStrHSNCode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
                        }
                        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)) != null){
                            purchaseOrderBean.setStrUOM(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)));
                        }

                        purchaseOrderBean.setDblCGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
                        purchaseOrderBean.setDblSGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
                        purchaseOrderBean.setDblIGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
                        purchaseOrderBean.setDblCessRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)));
                        purchaseOrderBean.setDblCessAmountPerUnit(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmountPerUnit)));
                        purchaseOrderBean.setDblAdditionalCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));
                    } else {
                        purchaseOrderBean.setiItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id)));
                        purchaseOrderBean.setStrSupplierId(""+supplier_model.get_id());
                        purchaseOrderBean.setStrSupplierName(supplier_model.getSupplierName());
                        purchaseOrderBean.setStrSupplierPhone(supplier_model.getSupplierPhone());
                        purchaseOrderBean.setStrSupplierAddress(supplier_model.getSupplierAddress());
                        if (supplier_model.getSupplierGSTIN() != null) {
                            purchaseOrderBean.setStrSupplierGSTIN(supplier_model.getSupplierGSTIN());
                        } else {
                            purchaseOrderBean.setStrSupplierGSTIN("");
                        }
                        if (!purchaseOrderBean.getStrSupplierGSTIN().isEmpty())
                            purchaseOrderBean.setStrSupplierPOS(purchaseOrderBean.getStrSupplierGSTIN().substring(0, 2));
                        else
                            purchaseOrderBean.setStrSupplierPOS("");

                        if (supplier_model.getSupplierType() != null) {
                            purchaseOrderBean.setStrSupplierType(supplier_model.getSupplierType());
                        }

                        purchaseOrderBean.setStrBarcode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
                        purchaseOrderBean.setStrItemName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));

                        purchaseOrderBean.setDblPurchaseRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_AveragePurchaseRate)));
                        purchaseOrderBean.setDblRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Rate)));

                        if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)) != null) {
                            purchaseOrderBean.setStrSupplyType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)));
                        }
                        if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)) != null) {
                            purchaseOrderBean.setStrHSNCode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
                        }
                        if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)) != null) {
                            purchaseOrderBean.setStrUOM(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)));
                        }

                        purchaseOrderBean.setDblCGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
                        purchaseOrderBean.setDblSGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
                        purchaseOrderBean.setDblIGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
                        purchaseOrderBean.setDblCessRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)));
                        purchaseOrderBean.setDblCessAmountPerUnit(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmount)));
                        purchaseOrderBean.setDblAdditionalCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));
                    }

                    edtMrp.setText(""+purchaseOrderBean.getDblRate());
                    edtPurchaseRate.setText(""+purchaseOrderBean.getDblPurchaseRate());
                }
            }
        });
    }


    private void initAutoCompleteTextDataForPurchaseOrderNo(final int iSupplierId){
        mAdapterPurchaseOrderNoData = new SimpleCursorAdapter(this, R.layout.auto_complete_textview_two_strings, null,
                new String[] {DatabaseHandler.KEY_id, DatabaseHandler.KEY_SupplierCode},
                new int[] {R.id.tv_auto_complete_textview_item_one},
                0);

        avPurchaseOrder.setThreshold(1);

        avPurchaseOrder.setAdapter(mAdapterPurchaseOrderNoData);

        mAdapterPurchaseOrderNoData.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                Log.i(TAG, "Purchase order no search data in purchase order table" + str);
                if(str != null && !avSupplierName.getText().toString().isEmpty()) {
                    return dbGoodsInwardNote.mGetPurchaseOrderNo(str,iSupplierId);
                }
                return null;
            } });

        mAdapterPurchaseOrderNoData.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(DatabaseHandler.KEY_id);
                return cur.getString(index);
            }});

        avPurchaseOrder.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                if (!avSupplierName.getText().toString().isEmpty())
                    avPurchaseOrder.showDropDown();
                return false;
            }
        });


        avPurchaseOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                // Get the state's capital from this row in the database.
                if(cursor != null){
                    if (purchaseOrderBeanList.size() > 0)
                        purchaseOrderBeanList.clear();
                    mFetchDataOnPurchaseOrderNo(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_id)), cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierCode)));
                }
            }
        });
    }
    private void mValidateAndAddingListAdapter(){
        if(avSupplierName.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please Select/Add Supplier's Supplier's Firm ");
            return;
        }
        if(avItemName.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please Select Item ");
            return;
        }

//        if(edtInvoiceNo.getText().toString().isEmpty()){
//            MsgBox.Show("Insufficient Information","Please enter invoice number. ");
//            return;
//        }
//
//        if(edtInvoiceDate.getText().toString().isEmpty()){
//            MsgBox.Show("Insufficient Information","Please select invoice date. ");
//            return;
//        }

        if(edtPhone.getText().toString().isEmpty() && edtAddress.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please select proper Supplier's Firm or add Supplier's Firm and select items.");
            return;
        }

        if(purchaseOrderBean == null){
            MsgBox.Show("Warning","Kindly goto \"Supplier's Firm Item Linkage\" and add the desired item." +
                    "\nPlease save your data , if any , before leaving this screen");
            return;
        }
        if(edtQty.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please Enter the Quantity ");
            return;
        }
        if(edtQty.getText().toString().equalsIgnoreCase(".")){
            edtQty.setText("0.0");
        }
        if(edtPurchaseRate.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please Enter the Purchase Rate. ");
            return;
        }
        if (edtPurchaseRate.getText().toString().equalsIgnoreCase(".")) {
            edtPurchaseRate.setText("0.0");
        }
       /* if(Double.parseDouble(edtPurchaseRate.getText().toString().trim()) < 0)
        {
            MsgBox.Show("Wrong Information", "Purchase rate should be greater than zero.");
            return;
        }*/
       /* if(Double.parseDouble(edtPurchaseRate.getText().toString().trim()) > purchaseOrderBean.getDblRate())
        {
            MsgBox.Show("Invalid Information", "Purchase rate cannot be greater than MRP.");
            return;
        }*/

        purchaseOrderBean.setStrInvoiceNo(edtInvoiceNo.getText().toString().trim());
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(edtInvoiceDate.getText().toString().trim());
            purchaseOrderBean.setStrInvoiceDate(""+date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        purchaseOrderBean.setDblPurchaseRate(Double.parseDouble(edtPurchaseRate.getText().toString().trim()));

        Cursor duplicateLinkage = dbGoodsInwardNote.getDuplicateSupplierItemLinkage(supplier_model.get_id(),
                purchaseOrderBean.getiItemId(), purchaseOrderBean.getStrUOM());

        if (duplicateLinkage!= null && duplicateLinkage.moveToFirst()){

        } else {
            LinkItem();
        }

        if (purchaseOrderBeanList != null && purchaseOrderBeanList.size() > 0) {
            boolean bExistsStatus = true;
            for (int i = 0; i < purchaseOrderBeanList.size(); i++) {
                if (purchaseOrderBeanList.get(i).getiItemId() ==  purchaseOrderBean.getiItemId()
                        && purchaseOrderBeanList.get(i).getDblPurchaseRate() == purchaseOrderBean.getDblPurchaseRate()) {
                    mAmountCalculation(purchaseOrderBeanList.get(i),UPDATE);
                    bExistsStatus = false;
                    break;
                } else if (purchaseOrderBeanList.get(i).getiItemId() ==  purchaseOrderBean.getiItemId()
                        && purchaseOrderBeanList.get(i).getDblPurchaseRate() != purchaseOrderBean.getDblPurchaseRate()) {
                    MsgBox.Show("Duplicate Information","Same item is already added.");
                    bExistsStatus = false;
                }
            }
            if(bExistsStatus){
                mAmountCalculation(purchaseOrderBean,ADD);
            }
        } else {
            mAmountCalculation(purchaseOrderBean,ADD);
        }
    }

    private void mValidateAndUpdatingListAdapter(){
        if(avSupplierName.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please Select/Add Supplier's Firm ");
            return;
        }
        if(avItemName.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please Select Item ");
            return;
        }

        if(edtPhone.getText().toString().isEmpty() && edtAddress.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please select proper Supplier's Firm or add Supplier's Firm and select items.");
            return;
        }

        if(updatePurchaseOrderBean == null){
            MsgBox.Show("Warning","Kindly goto \"Supplier's Firm Item Linkage\" and add the desired item." +
                    "\nPlease save your data , if any , before leaving this screen");
            return;
        }
        if(edtQty.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please Enter the Quantity ");
            return;
        }
        if(edtQty.getText().toString().equalsIgnoreCase(".")){
            edtQty.setText("0.0");
        }
        if(edtPurchaseRate.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information","Please Enter the Purchase Rate. ");
            return;
        }
        if (edtPurchaseRate.getText().toString().equalsIgnoreCase(".")) {
            edtPurchaseRate.setText("0.0");
        }
        if(Double.parseDouble(edtPurchaseRate.getText().toString().trim()) > updatePurchaseOrderBean.getDblRate())
        {
            MsgBox.Show("Invalid Information", "Purchase rate cannot be greater than MRP.");
            return;
        }
        updatePurchaseOrderBean.setDblPurchaseRate(Double.parseDouble(edtPurchaseRate.getText().toString().trim()));

        if (purchaseOrderBeanList != null && purchaseOrderBeanList.size() > 0) {
            for (int i = 0; i < purchaseOrderBeanList.size(); i++) {
                if (updateItem) {
                    if (purchaseOrderBeanList.get(i).getiItemId() ==  updatePurchaseOrderBean.getiItemId()
                            && purchaseOrderBeanList.get(i).getDblPurchaseRate() == updatePurchaseOrderBean.getDblPurchaseRate()) {
                        mAmountCalculation(updatePurchaseOrderBean,UPDATE);
                    }
                }
            }
        }
    }

    private void LinkItem () {
        SupplierItemLinkageBean supplierItemLinkageBean = new SupplierItemLinkageBean();
        supplierItemLinkageBean.setiSupplierID(supplier_model.get_id());
        supplierItemLinkageBean.setStrSupplierName(avSupplierName.getText().toString());
        supplierItemLinkageBean.setStrSupplierPhone(edtPhone.getText().toString());
        supplierItemLinkageBean.setStrSupplierAddress(edtAddress.getText().toString());
        if(!edtGSTIN.getText().toString().isEmpty()){
            supplierItemLinkageBean.setStrGSTIN(edtGSTIN.getText().toString());
            supplierItemLinkageBean.setStrSupplierType("Registered");
        } else {
            supplierItemLinkageBean.setStrGSTIN("");
            supplierItemLinkageBean.setStrSupplierType("UnRegistered");
        }
        supplierItemLinkageBean.setiItemID(purchaseOrderBean.getiItemId());
        supplierItemLinkageBean.setStrBarcode(purchaseOrderBean.getStrBarcode());
        supplierItemLinkageBean.setStrItemName(purchaseOrderBean.getStrItemName());
        if(purchaseOrderBean.getStrHSNCode() != null && !purchaseOrderBean.getStrHSNCode().equalsIgnoreCase("")) {
            supplierItemLinkageBean.setStrHSNCode(purchaseOrderBean.getStrHSNCode());
        }
        if(purchaseOrderBean.getStrSupplyType() != null && !purchaseOrderBean.getStrSupplyType().equalsIgnoreCase("")) {
            supplierItemLinkageBean.setStrSupplyType(purchaseOrderBean.getStrSupplyType());
        }
        if(purchaseOrderBean.getStrUOM() != null && !purchaseOrderBean.getStrUOM().equalsIgnoreCase("")) {
            supplierItemLinkageBean.setStrUOM(purchaseOrderBean.getStrUOM());
        }
        supplierItemLinkageBean.setDblRate1(purchaseOrderBean.getDblRate());
        supplierItemLinkageBean.setDblPurchaseRate(purchaseOrderBean.getDblPurchaseRate());
        supplierItemLinkageBean.setDblCGSTPer(purchaseOrderBean.getDblCGSTRate());
        supplierItemLinkageBean.setDblUTGST_SGSTPer(purchaseOrderBean.getDblSGSTRate());
        supplierItemLinkageBean.setDblIGSTPer(purchaseOrderBean.getDblIGSTRate());
        supplierItemLinkageBean.setDblCessPer(purchaseOrderBean.getDblCessRate());
        supplierItemLinkageBean.setDblCessAmount(purchaseOrderBean.getDblCessAmount());
        supplierItemLinkageBean.setDblAdditionalCessAmount(purchaseOrderBean.getDblAdditionalCessAmount());
        supplierItemLinkageBean.setDblPurchaseRate(purchaseOrderBean.getDblPurchaseRate());

        long lLinkItemStatus = -1;
        lLinkItemStatus = dbGoodsInwardNote.mLinkSupplierWithItem(supplierItemLinkageBean, Constants.INSERT);
        if(lLinkItemStatus>0) {
            Log.i(TAG, "New Item linked successfully...");
        }
    }

    private void mFetchDataOnPurchaseOrderNo(String purchaseOrderNo, String supplierId){
        Cursor cursorPO = dbGoodsInwardNote.mGetPurchaseOrderData(purchaseOrderNo, supplierId);
        try {
            if (cursorPO != null){
                cursorPO.moveToFirst();
                do{
                    purchaseOrderBean = new PurchaseOrderBean();
                    purchaseOrderBean.set_id(cursorPO.getInt(cursorPO.getColumnIndex(DatabaseHandler.KEY_id)));
                    purchaseOrderBean.setiItemId(cursorPO.getInt(cursorPO.getColumnIndex(DatabaseHandler.KEY_ItemId)));
                    purchaseOrderBean.setStrSupplierId(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SupplierCode)));
                    purchaseOrderBean.setStrSupplierName(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SUPPLIERNAME)));
                    purchaseOrderBean.setStrSupplierPhone(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SupplierPhone)));
                    purchaseOrderBean.setStrSupplierAddress(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SupplierAddress)));
                    if(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_GSTIN)) != null){
                        purchaseOrderBean.setStrSupplierGSTIN(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                    } else {
                        purchaseOrderBean.setStrSupplierGSTIN("");
                    }
                    purchaseOrderBean.setStrBarcode(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
                    purchaseOrderBean.setStrItemName(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_ItemName)));
                    if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)) > 0) {
                        purchaseOrderBean.setDblPurchaseRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)));
                    }
                    if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_Rate)) > 0) {
                        purchaseOrderBean.setDblRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_Rate)));
                    }
                   /* if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_RetailPrice)) > 0){
                        purchaseOrderBean.setDblRetailPrice(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_RetailPrice)));
                    }
                    if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_WholeSalePrice)) > 0){
                        purchaseOrderBean.setDblWholeSalePrice(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_WholeSalePrice)));
                    }*/
                    if(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SupplyType)) != null){
                        purchaseOrderBean.setStrSupplyType(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SupplyType)));
                    } else {
                        purchaseOrderBean.setStrSupplyType("");
                    }
                    if(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SupplierType)) != null){
                        purchaseOrderBean.setStrSupplierType(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SupplierType)));
                    }else {
                        purchaseOrderBean.setStrSupplierType("");
                    }
                    if(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_HSNCode)) != null){
                        purchaseOrderBean.setStrHSNCode(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
                    } else {
                        purchaseOrderBean.setStrHSNCode("");
                    }
                    if(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_UOM)) != null){
                        purchaseOrderBean.setStrUOM(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_UOM)));
                    } else {
                        purchaseOrderBean.setStrUOM("");
                    }
//                    if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_CGSTRate)) > 0){
                    purchaseOrderBean.setDblCGSTRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
//                    }
//                    if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_SGSTRate)) > 0){
                    purchaseOrderBean.setDblSGSTRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
//                    }
//                    if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_IGSTRate)) > 0){
                    purchaseOrderBean.setDblIGSTRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
//                    }
//                    if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_cessRate)) > 0){
                    purchaseOrderBean.setDblCessRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_cessRate)));

                    purchaseOrderBean.setDblCessAmountPerUnit(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_cessAmountPerUnit)));
                    purchaseOrderBean.setDblAdditionalCessAmount(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));
//                    }
//                    if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_Quantity)) > 0){
                    purchaseOrderBean.setDblQuantity(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_Quantity)));
//                    }
//                    if(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeAmount)) > 0){
                    purchaseOrderBean.setDblAdditionalChargeAmount(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeAmount)));
//                    }
                    if(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeName)) != null){
                        purchaseOrderBean.setStrAdditionalCharge(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeName)));
                    } else {
                        purchaseOrderBean.setStrAdditionalCharge("");
                    }
                    if(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_InvoiceNo)) != null){
                        purchaseOrderBean.setStrInvoiceNo(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_InvoiceNo)));
                    }else {
                        purchaseOrderBean.setStrInvoiceNo("");
                    }
                    if(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_InvoiceDate)) != null){
                        purchaseOrderBean.setStrInvoiceDate(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_InvoiceDate)));
                    } else {
                        purchaseOrderBean.setStrInvoiceDate("");
                    }
                    if(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SupplierPOS)) != null){
                        purchaseOrderBean.setStrSupplierPOS(cursorPO.getString(cursorPO.getColumnIndex(DatabaseHandler.KEY_SupplierPOS)));
                    } else {
                        purchaseOrderBean.setStrSupplierPOS("");
                    }
                    edtPurchaseOrderId.setText(""+purchaseOrderBean.get_id());
                    mAmountCalculation(purchaseOrderBean,ADD);
                }while(cursorPO.moveToNext());
            }
        }catch (Exception ex){
            Log.i(TAG,"Unable to fetch data from purchase order table based on the purchase order no.");
        } finally {
            if(cursorPO != null){
                cursorPO.close();
            }
        }
    }

    private void mAmountCalculation(PurchaseOrderBean purchaseOrderBeanTemp, int iMode) {
        double dblQty = 0, dblPurchaseRate = 0, dblTaxableValue = 0, dblTotalCGSTAmt = 0, dblTotalSGSTAmt = 0, dblTotalIGSTAmt = 0, dblTotalCessAmt = 0, dblAmount = 0;
        if (purchaseOrderBeanTemp != null && purchaseOrderBeanTemp.getDblQuantity() <= 0) {
            try {
                dblQty = Double.parseDouble(edtQty.getText().toString());
                dblPurchaseRate = Double.parseDouble(edtPurchaseRate.getText().toString());
                if (dblQty <= 0) {
                    MsgBox.Show("Insufficient Information", "Please enter quantity more than zero.");
                    return;
                }
               /* if (dblPurchaseRate <= 0) {
                    MsgBox.Show("Insufficient Information", "Please enter purchase rate more than zero.");
                    return;
                }*/
                purchaseOrderBeanTemp.setDblQuantity(dblQty);
                purchaseOrderBeanTemp.setDblPurchaseRate(dblPurchaseRate);
            } catch (Exception ex) {
                Log.i(TAG, "Unable to process calculation on item selection for purchase order. Barcode : "
                        + purchaseOrderBeanTemp.getStrBarcode() + ex.getMessage());
                return;
            }
        } else {
            dblQty = 0;
            if(!edtQty.getText().toString().isEmpty()) {
                dblQty = Double.parseDouble(edtQty.getText().toString());
            }
            if (updateItem)
                purchaseOrderBeanTemp.setDblQuantity(dblQty);
            else
                purchaseOrderBeanTemp.setDblQuantity(dblQty + purchaseOrderBeanTemp.getDblQuantity());
        }
        try {
            dblTaxableValue = purchaseOrderBeanTemp.getDblQuantity() * purchaseOrderBeanTemp.getDblPurchaseRate();
            purchaseOrderBeanTemp.setDblTaxableValue(dblTaxableValue);
            if(cbStateCode.isChecked())
            {
                dblTotalIGSTAmt = (purchaseOrderBeanTemp.getDblIGSTRate() * purchaseOrderBeanTemp.getDblTaxableValue())/100;
                dblTotalCGSTAmt = 0;
                dblTotalSGSTAmt = 0;
            }
            else
            {
                dblTotalCGSTAmt = (purchaseOrderBeanTemp.getDblCGSTRate()*purchaseOrderBeanTemp.getDblTaxableValue())/100;
                dblTotalSGSTAmt = (purchaseOrderBeanTemp.getDblSGSTRate()*purchaseOrderBeanTemp.getDblTaxableValue())/100;
                dblTotalIGSTAmt =0;
            }
            if (purchaseOrderBeanTemp.getDblCessRate() > 0)
                dblTotalCessAmt = (purchaseOrderBeanTemp.getDblCessRate()*purchaseOrderBeanTemp.getDblTaxableValue())/100;
            else
                dblTotalCessAmt = purchaseOrderBeanTemp.getDblCessAmountPerUnit()*purchaseOrderBeanTemp.getDblQuantity();

            dblTotalCessAmt += purchaseOrderBeanTemp.getDblAdditionalCessAmount()*purchaseOrderBeanTemp.getDblQuantity();

            purchaseOrderBeanTemp.setDblCGSTAmount(dblTotalCGSTAmt);
            purchaseOrderBeanTemp.setDblSGSTAmount(dblTotalSGSTAmt);
            purchaseOrderBeanTemp.setDblIGSTAmount(dblTotalIGSTAmt);
            purchaseOrderBeanTemp.setDblCessAmount(dblTotalCessAmt);
            dblAmount = purchaseOrderBeanTemp.getDblTaxableValue() + purchaseOrderBeanTemp.getDblCGSTAmount()
                    + purchaseOrderBeanTemp.getDblSGSTAmount() + purchaseOrderBeanTemp.getDblIGSTAmount()
                    + purchaseOrderBeanTemp.getDblCessAmount();
            purchaseOrderBeanTemp.setDblAmount(dblAmount);
        } catch (Exception ex) {
            Log.i(TAG, "Unable to process calculation on item selection for purchase order. Barcode : "
                    + purchaseOrderBeanTemp.getStrBarcode() + ex.getMessage());
            return;
        }
        switch (iMode){
            case ADD:
                purchaseOrderBeanList.add(purchaseOrderBeanTemp);
                break;
            case UPDATE:
                for (int i = 0; i < purchaseOrderBeanList.size(); i++) {
                    if (purchaseOrderBeanList.get(i).getiItemId() == purchaseOrderBeanTemp.getiItemId()
                            && purchaseOrderBeanList.get(i).getDblPurchaseRate() == purchaseOrderBeanTemp.getDblPurchaseRate()) {
                        purchaseOrderBeanList.remove(i);
                        purchaseOrderBeanList.add(purchaseOrderBeanTemp);
                        break;
                    }
                }
                break;
            default:
                break;
        }
        mPopulatingDataToAdapter();
    }

    private void mPopulatingDataToAdapter(){
        if(purchaseOrderBeanList.size() > 0){
            if (purchaseOrderAdapter == null) {
                purchaseOrderAdapter = new PurchaseOrderAdapter(this,this, purchaseOrderBeanList);
                lvPurchaseOrderList.setAdapter(purchaseOrderAdapter);
            } else {
                purchaseOrderAdapter.notifyDataSetChanged(purchaseOrderBeanList);
            }

            edtSubTotal.setText(String.format("%.2f", mCalculateSubTotal()));
            edtGrandTotal.setText(String.format("%.2f", mCalculateGrandTotal(0)));
            if(purchaseOrderBeanList.get(0).getDblAdditionalChargeAmount() > 0){
                cbAdditionalChargeStatus.setChecked(true);
                if(!purchaseOrderBeanList.get(0).getStrAdditionalCharge().isEmpty()) {
                    edtAdditionalChargeName.setText(purchaseOrderBeanList.get(0).getStrAdditionalCharge());
                }
                edtAdditionalAmt.setText(""+purchaseOrderBeanList.get(0).getDblAdditionalChargeAmount());
            }
            if(!purchaseOrderBeanList.get(0).getStrInvoiceNo().isEmpty()) {
                edtInvoiceNo.setText(purchaseOrderBeanList.get(0).getStrInvoiceNo());
                try {
                    String dateformat = "dd-MM-yyyy";
                    String invoicedate = getDate(Long.parseLong(purchaseOrderBeanList.get(0).getStrInvoiceDate()), dateformat);
                    edtInvoiceDate.setText(invoicedate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
               /* String dateformat = "dd-MM-yyyy";
                String invoicedate = getDate(Long.parseLong(purchaseOrderBeanList.get(0).getStrInvoiceDate()), dateformat);
                edtInvoiceDate.setText(invoicedate);*/
            }
            if(arrayPOS != null && arrayPOS.length > 0) {
                if (!purchaseOrderBeanList.get(0).getStrSupplierPOS().isEmpty() && !isPosSame(purchaseOrderBeanList.get(0).getStrSupplierPOS())) {
                    cbStateCode.setChecked(true);
                    for (int i = 0; i <arrayPOS.length; i++) {
                        String strStateCode = arrayPOS[i];
                        int l = strStateCode.length();
                        String strState_cd = strStateCode.substring(l-2,l);
                        if(strState_cd.equals(purchaseOrderBeanList.get(0).getStrSupplierPOS())) {
                            spStateCode.setSelection(i);
                            break;
                        }
                    }
                }
            }
        } else {
            edtSubTotal.setText("");
            edtGrandTotal.setText("");
            edtAdditionalAmt.setText("");
            edtAdditionalChargeName.setText("");
        }
        avItemName.setText("");
        edtQty.setText("");
        edtMrp.setText("");
        edtPurchaseRate.setText("");
        updateItem = false;
        btnAddItem.setText("Add Item");
    }

    private boolean isPosSame(String POS) {
        boolean isSame = false;

        String ownerPOS = dbGoodsInwardNote.getOwnerPOS();
        if (ownerPOS.equalsIgnoreCase(POS))
            isSame = true;

        return isSame;
    }

    long savePurchaseOrder(int isGoodInward)
    {
        long result = -1;
        if(purchaseOrderBeanList != null && purchaseOrderBeanList.size() > 0){
            if(purchaseOrderBeanList.get(0).getStrSupplierId().isEmpty()){
                MsgBox.Show(" Insufficient Information ", "Supplier's Firm is not in Database. Please add Supplier's Firm");
                return result;
            }
            if (avSupplierName.getText().toString().isEmpty() || edtAddress.getText().toString().isEmpty()
                    || edtPhone.getText().toString().isEmpty()) {
                MsgBox.Show(" Insufficient Information ", "Please fill Supplier's Firm Details");
                return result;
            }

            if (purchaseOrderAdapter == null) {
                MsgBox.Show("Insufficient Information ", " Please add item");
                return result;
            }
            if (cbStateCode.isChecked() && spStateCode.getSelectedItem().toString().equals("Select")) {
                MsgBox.Show("Insufficient Information ", " Please select state for Supplier's Firm");
                return result;
            }

            if (isGoodInward == 1) {
                if(edtInvoiceNo.getText().toString().isEmpty()){
                    MsgBox.Show("Insufficient Information","Please enter invoice number. ");
                    return result;
                }

                if(edtInvoiceDate.getText().toString().isEmpty()){
                    MsgBox.Show("Insufficient Information","Please select invoice date. ");
                    return result;
                }
            }
            if (cbAdditionalChargeStatus.isChecked()) {
                if (edtAdditionalChargeName.getText().toString().isEmpty()){
                    MsgBox.Show("Insufficient Information", "Please enter the additional charge name.");
                    return result;
                }
                if (edtAdditionalAmt.getText().toString().isEmpty()){
                    MsgBox.Show("Insufficient Information", "Please enter the additional charge.");
                    return result;
                }
            }
        }else {
            MsgBox.Show(" Insufficient Information ", "Please fill all the necessary details");
            return result;
        }
        String purchaseorderno = avPurchaseOrder.getText().toString();

        if (edtPurchaseOrderId.getText().toString().isEmpty()) { // check is there is na ir something else

            if (purchaseorderno.isEmpty()) {
                // generate purchase order no
                Cursor crsr = dbGoodsInwardNote.getMaxPurchaseOrderNo();
                int Max =1;
                if (crsr != null && crsr.moveToFirst())
                {
                    if(crsr.getInt(0) != -1)
                    {
                        Max = crsr.getInt(0)+1;
                    }
                }
                avPurchaseOrder.setText(String.valueOf(Max));
            } else {
                if (purchaseorderno.equalsIgnoreCase("NA") && isGoodInward == 1) {
                    avPurchaseOrder.setText("-1");
                } else {
                    MsgBox.Show("Warning", "Purchase Order No. is auto generated, kindly clear Purchase Order No. field.");
                    return result;
                }
            }

        } else { // check if selected po is valid or not

            if (purchaseorderno.isEmpty()) {
                MsgBox.Show("Invalid Information", "Please Select a valid Purchase Order");
                return result;
            } else {
                int purchaseorderId = Integer.parseInt(edtPurchaseOrderId.getText().toString().trim());
                int porchaseOrderNo = Integer.parseInt(avPurchaseOrder.getText().toString().trim());

                Cursor isPurchaseOrderExist = dbGoodsInwardNote.getPurchaseOrderById(supplier_model.get_id(),
                        purchaseorderId, porchaseOrderNo);
                if (isPurchaseOrderExist != null && isPurchaseOrderExist.moveToFirst()){

                } else {
                    MsgBox.Show("Invalid Information", "Please Select a valid Purchase Order");
                    return result;
                }
            }
        }
       /*
        if (!purchaseorderno.equals("")) {

            if (isGoodInward == 0) {
                if (purchaseorderno.equalsIgnoreCase("NA")) {
                    MsgBox.Show("Warning", "Purchase Order No. is auto generated, kindly clear Purchase Order No. field.");
                    return result;
                } else {
                    int purchaseorderId = Integer.parseInt(edtPurchaseOrderId.getText().toString().trim());
                    int porchaseOrderNo = Integer.parseInt(avPurchaseOrder.getText().toString().trim());

                    Cursor isPurchaseOrderExist = dbGoodsInwardNote.getPurchaseOrderById(supplier_model.get_id(),
                            purchaseorderId, porchaseOrderNo);
                    if (isPurchaseOrderExist != null && isPurchaseOrderExist.moveToFirst()){

                    } else {
                        MsgBox.Show("Invalid Information", "Please Select a valid Purchase Order");
                        return result;
                    }
                }
            } else {
                avPurchaseOrder.setText("-1");
            }

        }
        if (edtPurchaseOrderId.getText().toString().isEmpty()) {
            if (purchaseorderno.equals("")) {
                // generate purchase order no
                Cursor crsr = dbGoodsInwardNote.getMaxPurchaseOrderNo();
                int Max =1;
                if (crsr != null && crsr.moveToFirst())
                {
                    if(crsr.getInt(0) != -100)
                    {
                        Max = crsr.getInt(0)+1;
                    }
                }
                avPurchaseOrder.setText(String.valueOf(Max));
            }
        } else {
            if (avPurchaseOrder.getText().toString().isEmpty()) {
                MsgBox.Show("Invalid Information", "Please Select a valid Purchase Order");
                return result;
            }
        }*/

        try
        {
            purchaseorderno = avPurchaseOrder.getText().toString();
            String supp_name = avSupplierName.getText().toString();
            int supplierId  = Integer.parseInt(purchaseOrderBeanList.get(0).getStrSupplierId());
            String sup_phone = edtPhone.getText().toString();
            String sup_address = edtAddress.getText().toString();
            String sup_gstin = edtGSTIN.getText().toString();

            String invoiceno;
            String invodate;

            if (isGoodInward == 1) {
                invoiceno = edtInvoiceNo.getText().toString();
                invodate = edtInvoiceDate.getText().toString();
            } else {
                invoiceno = "";
                invodate = "";
            }

            int deletedrows = dbGoodsInwardNote.deletePurchaseOrder(supplierId, purchaseorderno);
            Log.d("InsertPurchaseOrder", deletedrows+" Rows deleted for Purchase Order "+purchaseorderno);

            if (!invodate.equals("")) {
                Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(invodate);
                long milliseconds = date.getTime();
                invodate = String.valueOf(milliseconds);
            }

            Date d = new Date();
            CharSequence currentdate = DateFormat.format("dd-MM-yyyy", d.getTime());
            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(currentdate.toString());
            long milliseconds = date.getTime();

            for (PurchaseOrderBean po :purchaseOrderBeanList) {
                po.setiPurchaseOrderNo(Integer.parseInt(purchaseorderno));
                po.setStrInvoiceNo(invoiceno);
                po.setStrInvoiceDate(invodate);
                po.setStrPurchaseOrderDate(String.valueOf(milliseconds));
                po.setiIsgoodInward(0);
                if(isGoodInward ==1){
                    po.setiIsgoodInward(1);
                }
                if(cbStateCode.isChecked())
                {
                    String supplier_stateCode = spStateCode.getSelectedItem().toString();
                    int l = supplier_stateCode.length();
                    String state_cd = supplier_stateCode.substring(l-2,l);
                    po.setStrSupplierPOS(state_cd);
                }else
                {
                    po.setStrSupplierPOS("");
                }
                if(cbAdditionalChargeStatus.isChecked())
                {
                    po.setStrAdditionalCharge(edtAdditionalChargeName.getText().toString());
                    if (!edtAdditionalAmt.getText().toString().isEmpty())
                        po.setDblAdditionalChargeAmount(Double.parseDouble(edtAdditionalAmt.getText().toString()));
                    else
                        po.setDblAdditionalChargeAmount(0);
                }
                else
                {
                    po.setStrAdditionalCharge("");
                    po.setDblAdditionalChargeAmount(0);
                }
                result = dbGoodsInwardNote.InsertPurchaseOrder(po);
                if(result > -1 && po.getiIsgoodInward() == 1){
//                    dbGoodsInwardNote.mUpdateSalesOrderFromGoodsInward(po.getiPurchaseOrderNo(),po.getiItemId());
                }
                if(result>-1) {
//                    Toast.makeText(this, "Purchase Order Saved Successfully.", Toast.LENGTH_SHORT).show();
                    Log.d("InsertPurchaseOrder", " item inserted at position:" + result);
                }
            }
            if(result > -1 && purchaseOrderBeanList.get(0).getiIsgoodInward() == 1){
              /*  MobileDataUploadStatusBean mobileDataUploadStatusBean = new MobileDataUploadStatusBean();
                mobileDataUploadStatusBean.setStrInvoiceNo(purchaseOrderBeanList.get(0).getStrInvoiceNo());
                mobileDataUploadStatusBean.setStrInvoiceDate(purchaseOrderBeanList.get(0).getStrInvoiceDate());
                mobileDataUploadStatusBean.setStrInvoiceMode("NORMAL");
                dbGoodsInwardNote.mInsertMobileDataUploadStatusPurchases(mobileDataUploadStatusBean);*/
            }
            if(result>-1) {
                if (!avPurchaseOrder.getText().toString().equalsIgnoreCase("-1")) {
                    generatePOPdf();
                    sendPurchaseOrder(avPurchaseOrder.getText().toString(), edtInvoiceNo.getText().toString().trim(),
                            edtInvoiceDate.getText().toString().trim(), supplier_model.getSupplierEmail());
                }
                Toast.makeText(this, "Purchase Order Generated/Updated :"+purchaseorderno, Toast.LENGTH_SHORT).show();
//                mClear();
            }
        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("log", e.getMessage(), e);
            result = -1;
        }
        return result;
    }

    private void sendPurchaseOrder(final String purchaseOrderNo, final String invoiceNo, final String invoiceDate, final String supplierEmail) {

        if (!isWifiConnected()) {
            Toast.makeText(this, "Kindly connect to internet to share the bill.", Toast.LENGTH_LONG).show();
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.customer_detail_missing_alert, null);
        final EditText etCustPhone = alertLayout.findViewById(R.id.et_phone);
        final EditText etCustEmail = alertLayout.findViewById(R.id.et_email);
        final TextView tvMobile = alertLayout.findViewById(R.id.mobile_title);
        final TextView tvEmail = alertLayout.findViewById(R.id.email_title);
        final Button btnCancel = alertLayout.findViewById(R.id.btn_cancel);
        final Button btnSend = alertLayout.findViewById(R.id.btn_send);
        final CheckBox customerMobileCheckBox = (CheckBox) alertLayout.findViewById(R.id.customer_mobile);
        final CheckBox customerEmailCheckBox = (CheckBox) alertLayout.findViewById(R.id.customer_email);
        etCustPhone.setVisibility(View.GONE);
        customerMobileCheckBox.setVisibility(View.GONE);
        customerEmailCheckBox.setVisibility(View.GONE);
        tvMobile.setVisibility(View.GONE);

        etCustEmail.setEnabled(true);
        etCustEmail.setText(supplierEmail);

        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setTitle("Share PurchaseOrder");

        alert.setIcon(R.mipmap.ic_company_logo);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final android.app.AlertDialog alertDialog = alert.create();
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
                if (etCustEmail.isEnabled() && etCustEmail.getText().toString().isEmpty()) {
                    etCustEmail.setError("Please fill this field.");
                } else if (etCustEmail.isEnabled() && etCustEmail != null
                        && !etCustEmail.getText().toString().isEmpty()
                        && !Validations.isValidEmailAddress(etCustEmail.getText().toString())) {
                    etCustEmail.setError("Please Enter Valid Email id.");
                } else {
                    send(etCustEmail.getText().toString(), purchaseOrderNo, invoiceNo, invoiceDate);
                    Toast.makeText(GoodsInwardNoteActivity.this, "Sending....", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void send(String custEmail, String purchaseOrderNo, String invoiceNo, String invoiceDate) {
        String emailContent = "Please find the generated PurchaseOrder in mail attachment.";
        String firmName = "";

        String filename = "PO_" + purchaseOrderNo + "_" + invoiceNo + "_" + invoiceDate + ".pdf";

        String attachment = Environment.getExternalStorageDirectory().getPath() + "/WeP_Retail_PurchaseOrder/"
                + filename;

        SendBillInfoToCustUtility smsUtility = new SendBillInfoToCustUtility(this,"Purchase Order", emailContent, "", "", false, true, false,
                GoodsInwardNoteActivity.this, custEmail, attachment, filename, firmName);
        smsUtility.sendBillInfo();
        Toast.makeText(this, "Sharing Invoice...", Toast.LENGTH_SHORT).show();
    }

    private boolean isWifiConnected() {
        boolean isWifiConnected = false;
        try {
            ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                isWifiConnected = true;
            }

        } catch (Exception e) {
            isWifiConnected = false;
            Log.e(TAG, e.toString());
        } finally {
            return isWifiConnected;
        }
    }

    private void generatePOPdf() {
        try {

            PdfPOBean pdfItem = null;

            if (purchaseOrderBeanList.size() < 1) {
                MsgBox.Show("Warning", "Insert item before Print Bill");
                return;
            } else {
                String orderId = "";
                if ((!avPurchaseOrder.getText().toString().trim().equalsIgnoreCase(""))) {

                    pdfItem = new PdfPOBean();

                    pdfItem.setiPurchaseOrderNo(Integer.parseInt(avPurchaseOrder.getText().toString()));
                    pdfItem.setStrInvoiceNo(edtInvoiceNo.getText().toString());
                    pdfItem.setStrInvoiceDate(edtInvoiceDate.getText().toString());
                    pdfItem.setStrSupplierName(avSupplierName.getText().toString());
                    pdfItem.setStrSupplierPhone(edtPhone.getText().toString());
                    pdfItem.setStrSupplierGSTIN(edtGSTIN.getText().toString());
                    pdfItem.setStrSupplierAddress(edtAddress.getText().toString());
                    if (edtAdditionalChargeName.getText().toString().isEmpty()) {
                        pdfItem.setStrAdditionalCharge("");
                        pdfItem.setDblAdditionalChargeAmount(0.00);
                    } else {
                        pdfItem.setStrAdditionalCharge(edtAdditionalChargeName.getText().toString().trim());
                        pdfItem.setDblAdditionalChargeAmount(Double.parseDouble(String.format("%.2f", Double.parseDouble(edtAdditionalAmt.getText().toString().trim()))));
                    }

                    if(cbStateCode.isChecked())
                    {
                        String supplier_stateCode = spStateCode.getSelectedItem().toString();
                        int l = supplier_stateCode.length();
                        String state_cd = supplier_stateCode.substring(l-2,l);
                        pdfItem.setStrSupplierPOS(state_cd);
                    }else
                    {
                        pdfItem.setStrSupplierPOS("");
                    }

                    Cursor crsrOwnerDetails = null;
                    try {
                        crsrOwnerDetails = dbGoodsInwardNote.getOwnerDetail();

                        if (crsrOwnerDetails.moveToFirst()) {
                            try {
                                pdfItem.setOwnerPos(dbGoodsInwardNote.getOwnerPOS());
                                pdfItem.setOwnerGstin(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                                pdfItem.setOwnerName(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Owner_Name)));
                                pdfItem.setOwnerAddress(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Address)));
//                                String ownerPos = "";
                                /*for (int i = 0; i < arrayPOS.length; i++) {
                                    if (arrayPOS[i].contains(pdfItem.getOwnerStateCode()))
                                        ownerPos = arrayPOS[i];
                                }
                                ownerPos = ownerPos.substring(0, ownerPos.length() - 2);
                                pdfItem.setOwnerPos(ownerPos);*/
                                pdfItem.setOwnerPos(dbGoodsInwardNote.getOwnerPOS());
                                try {
                                    pdfItem.setCompanyLogoPath(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)));
                                } catch (Exception ex) {
                                    pdfItem.setCompanyLogoPath(null);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Unable to fetch data from owner details data from table." + ex.getMessage());
                    } finally {
                        if (crsrOwnerDetails != null) {
                            crsrOwnerDetails.close();
                        }
                    }

                    ArrayList<PdfPOItemBean> pdfItemBeanArrayList = new ArrayList<>();
                    PdfPOItemBean pdfItemBean;

                    for (PurchaseOrderBean purchaseOrderBean : purchaseOrderBeanList) {
                        pdfItemBean = new PdfPOItemBean();

                        pdfItemBean.setiItemId(purchaseOrderBean.getiItemId());
                        pdfItemBean.setStrItemName(purchaseOrderBean.getStrItemName());
                        pdfItemBean.setDblValue(purchaseOrderBean.getDblPurchaseRate());
                        pdfItemBean.setStrHSNCode(purchaseOrderBean.getStrHSNCode());
                        pdfItemBean.setDblQuantity(purchaseOrderBean.getDblQuantity());
                        pdfItemBean.setStrUOM(purchaseOrderBean.getStrUOM());

                        if (cbStateCode.isChecked()) {
                            pdfItemBean.setDblCGSTRate(0.00);
                            pdfItemBean.setDblSGSTRate(0.00);
                            pdfItemBean.setDblIGSTRate(purchaseOrderBean.getDblIGSTRate());
                        } else {
                            pdfItemBean.setDblCGSTRate(purchaseOrderBean.getDblCGSTRate());
                            pdfItemBean.setDblSGSTRate(purchaseOrderBean.getDblSGSTRate());
                            pdfItemBean.setDblIGSTRate(0.00);
                        }
                        pdfItemBean.setDblCessRate(purchaseOrderBean.getDblCessRate());

                        pdfItemBean.setDblCGSTAmount(purchaseOrderBean.getDblCGSTAmount());
                        pdfItemBean.setDblSGSTAmount(purchaseOrderBean.getDblSGSTAmount());
                        pdfItemBean.setDblIGSTAmount(purchaseOrderBean.getDblIGSTAmount());
                        pdfItemBean.setDblCessAmount(purchaseOrderBean.getDblCessAmount());

                        pdfItemBean.setDblTaxableValue(purchaseOrderBean.getDblTaxableValue());
                        pdfItemBean.setDblAmount(purchaseOrderBean.getDblAmount());

                        pdfItemBeanArrayList.add(pdfItemBean);
                    }

                    pdfItem.setPdfItemBeanArrayList(pdfItemBeanArrayList);

                    try {
                        CreatePdfPO test = CreatePdfPO.getInstance(this, pdfItem);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error occurred while generating PDF PO", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please Enter Bill Table Number", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
    public void mValidateGoodsInwardNote() {
//        if (avPurchaseOrder.getText().toString().isEmpty()) {
//            MsgBox.Show("Insufficient Information", "Please Select Purchase Order");
//            return;
//        }
        String purchaseorderno = avPurchaseOrder.getText().toString();
//        if(!purchaseorderno.equalsIgnoreCase("NA") && !isNumeric(purchaseorderno))
//        {
//            MsgBox.Show("Error ", " Please enter Purchase Order in numbers only");
//            return;
//        }
        if (avSupplierName.getText().toString().isEmpty()) {
            MsgBox.Show("Insufficient Information", "Please Select/Add Supplier's Firm ");
            return;
        }
        if(edtInvoiceNo.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information", "Please enter invoice number. ");
            return;
        }
        if(edtInvoiceDate.getText().toString().isEmpty()){
            MsgBox.Show("Insufficient Information", "Please select invoice date. ");
            return;
        }
        if (purchaseOrderAdapter == null) {
            MsgBox.Show("Insufficient Information", "Please Select Item ");
            return;
        }

        if (edtPhone.getText().toString().isEmpty() && edtAddress.getText().toString().isEmpty()) {
            MsgBox.Show("Insufficient Information", "Please select proper Supplier's Firm or add Supplier's Firm and select items.");
            return;
        }

        if (cbStateCode.isChecked() && spStateCode.getSelectedItem().toString().equals("Select")) {
            MsgBox.Show("Insufficient Information ", " Please select state for Supplier's Firm");
            return;
        }

        if (purchaseOrderBeanList != null && purchaseOrderBeanList.size() == 0) {
            MsgBox.Show(" Insufficient Information ", "Please add some items in the list first.");
            return;
        }

        try{

            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(edtInvoiceDate.getText().toString());
            long milliseconds = date.getTime();
            String invodate = String.valueOf(milliseconds);

            Cursor billAlreadyPresent_crsr = dbGoodsInwardNote.getPurchaseOrder_for_SupplierId(edtInvoiceNo.getText().toString().trim(),invodate,String.valueOf(purchaseOrderBeanList.get(0).getStrSupplierId()));
            if(billAlreadyPresent_crsr!=null && billAlreadyPresent_crsr.moveToFirst())
            {
                MsgBox.setIcon(R.mipmap.ic_company_logo)
                        .setTitle("Duplicate")
                        .setMessage("For this Supplier's Firm, an invoice is already present with same invoice no and date.")
//                        .setNegativeButton("Cancel",null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                goodsinward();
                            }
                        })
                        .show();
            }else
            {
                goodsinward();
            }

        }catch(Exception e)
        {
            e.printStackTrace();
            MsgBox.Show("Oops","Some error came while processing ");
            return ;
        }
    }

    void goodsinward() {

        String purchaseorderno = avPurchaseOrder.getText().toString();
        String invoiceno = edtInvoiceNo.getText().toString();
        String invoicedate = edtInvoiceDate.getText().toString();

        long l = savePurchaseOrder(1);

        if (l > 0) {
            try {
                for(PurchaseOrderBean po : purchaseOrderBeanList)
                {
                    String itemname_str = po.getStrItemName();
                    double quantity_d = po.getDblQuantity();
                    String uom_str = po.getStrUOM();
                    Cursor item_present_crsr = dbGoodsInwardNote.getItemByID(po.getiItemId());
                    try {
                        if (item_present_crsr != null && item_present_crsr.moveToFirst()) {
                            // already present , needs to update
                            double dblQtyFromDB = item_present_crsr.getDouble(item_present_crsr.getColumnIndex(DatabaseHandler.KEY_Quantity));
                            quantity_d += dblQtyFromDB;
                            ContentValues cvItems = new ContentValues();
                            cvItems.put(DatabaseHandler.KEY_Quantity, quantity_d);
                            l = dbGoodsInwardNote.mItemUpdateGoodsInwardNote(""+po.getiItemId(), cvItems); // Updating quantity in Item Master
                            if (l > -1) {
                                Log.d(" GoodsInwardNote ", itemname_str + " updated  successfully.");

                            }

                            /****** stock update begin *********/

                           /* Date date = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
                            Cursor itemStock_cursor = dbGoodsInwardNote.getItemStockDetail(po.getiItemId(),String.valueOf(date.getTime()));
                            double prevStock_opening = 0;
                            double prevStock_closing = 0;
                            double prevStock_purchase = 0;
                            double prevStock_sold = 0;
                            if(itemStock_cursor !=null && itemStock_cursor.moveToNext())
                            {
                                prevStock_opening = itemStock_cursor.getDouble(itemStock_cursor.getColumnIndex(DatabaseHandler.KEY_OpeningStock));
                                prevStock_closing = itemStock_cursor.getDouble(itemStock_cursor.getColumnIndex(DatabaseHandler.KEY_ClosingStock));
                                prevStock_purchase = itemStock_cursor.getDouble(itemStock_cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseStock));
                                prevStock_sold = itemStock_cursor.getDouble(itemStock_cursor.getColumnIndex(DatabaseHandler.KEY_SoldStock));
                            }*/
                            // kindly note this will only update opening stock
                           /* ItemStockClass itemStock = new ItemStockClass( po.getiItemId(),String.valueOf(date.getTime()),0 ,
                                    po.getStrItemName(),"",
                                    Double.parseDouble(String.format("%.2f", (prevStock_opening))),
                                    Double.parseDouble(String.format("%.2f", (prevStock_closing + po.getDblQuantity()))),
                                    Double.parseDouble(String.format("%.2f", (prevStock_purchase + po.getDblQuantity()))),
                                    Double.parseDouble(String.format("%.2f", (prevStock_sold))),
                                    1,0.00 );*/

                            /*long stockinsert = dbGoodsInwardNote.updateItemInItemStock_openingStock(itemStock);
                            if(stockinsert<1)
                            {
                                Log.d(TAG+": Issue while updating "+po.getStrItemName()+ " in ItemStock \n");
                            }*/
//                            stockinsert = dbGoodsInwardNote.updateItemInItemStock_closingStock(itemStock);
                          /*  long stockinsert = dbGoodsInwardNote.updateItemInItemStock(itemStock);
                            if(stockinsert<1)
                            {
                                Log.d(TAG+": Issue while updating "+po.getStrItemName()+ " in ItemStock \n");
                            }*/
                            /****** stock update ends *********/
                        }
                    }catch (Exception ex){
                        Log.i(TAG, "error on updating item quantity." +ex.getMessage());
                    }finally {
                        if(item_present_crsr != null){
                            item_present_crsr.close();
                        }
                    }
                    Cursor supplierItemlinkage = dbGoodsInwardNote.getSupplierItemlinkageBySupplierIdAndItemId(po.getStrSupplierId(), po.getiItemId());
                    try {
                        if (supplierItemlinkage != null && supplierItemlinkage.moveToFirst()) {
                            // already present , needs to update

                            ContentValues cvItems = new ContentValues();
                            cvItems.put(DatabaseHandler.KEY_PurchaseRate, po.getDblPurchaseRate());
                            l = dbGoodsInwardNote.updateItemInSupplierItemLinkage(cvItems, po.getStrSupplierId(), po.getiItemId()); // Updating Purchase Rate in Supplier Item Linkage
                            if (l > -1) {
                                Log.d("GoodsInwardNote", itemname_str + " updated  successfully.");
                            }
                            cvItems = new ContentValues();
                            cvItems.put(DatabaseHandler.KEY_AveragePurchaseRate, po.getDblPurchaseRate());
                            l = dbGoodsInwardNote.updateItemPurchaseRateInItemTable(cvItems, po.getiItemId());
                            if (l > -1) {
                                Log.d("GoodsInwardNote", itemname_str + " updated  successfully purchase rate in item master");
                            }
                        }
                    }catch (Exception ex){
                        Log.i(TAG, "error on updating item quantity." +ex.getMessage());
                    }finally {
                        if(item_present_crsr != null){
                            item_present_crsr.close();
                        }
                        if(supplierItemlinkage != null){
                            supplierItemlinkage.close();
                        }
                    }


               /* double avgPurchaseRate = Double.valueOf(String.format("%.2f", calculateItemAveragePurchaseRate(po.getiItemId())));
                ItemMasterBean itemMasterBean = new ItemMasterBean();
                itemMasterBean.set_id(po.getiItemId());
                itemMasterBean.setDblAveragePurchaseRate(avgPurchaseRate);
                long status = dbGoodsInwardNote.updateItemAvgPurchaseRate(itemMasterBean);
                if (status > 0)
                    Log.d("updateItemAvgPurchaseRate","Avg rate updated successfully");
                else
                    Log.d("updateItemAvgPurchaseRate","Avg rate not updated");*/
                }

                // Putting entry in supplier payment as unpaid
              /*  try {
                    Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(edtInvoiceDate.getText().toString());
                    long milliseconds = date.getTime();

                    double invoiceValue = 0;

                    Cursor invoiceAlreadyExist = dbGoodsInwardNote.getSupplierPaymentDetails(supplier_model.get_id(),
                            edtInvoiceNo.getText().toString().trim(),
                            String.valueOf(milliseconds));

                    if (invoiceAlreadyExist != null && invoiceAlreadyExist.moveToFirst()) {
                        Cursor invoiceDeatils_cursor = dbGoodsInwardNote.getSupplierInvoiceDetails(supplier_model.get_id(),
                                edtInvoiceNo.getText().toString().trim(),
                                String.valueOf(milliseconds));
//                        double invoiceValue = 0;
                        if (invoiceDeatils_cursor!=null && invoiceDeatils_cursor.moveToFirst()) {
                            invoiceValue += invoiceDeatils_cursor.getDouble(invoiceDeatils_cursor.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeAmount));
                            do {
                                invoiceValue += invoiceDeatils_cursor.getDouble(invoiceDeatils_cursor.getColumnIndex(DatabaseHandler.KEY_Amount));
                            } while (invoiceDeatils_cursor.moveToNext());
                        }

                        long status = dbGoodsInwardNote.updateSupplierPaymentDetails(supplier_model.get_id(),
                                edtInvoiceNo.getText().toString().trim(),
                                String.valueOf(milliseconds), invoiceValue);

                        if (status > 0) {
                            Log.i(TAG, "Supplier Payment updated");
                        }

                    } else {
                        SupplierPayment supplierPayment = new SupplierPayment();

                        supplierPayment.setSupplierId(supplier_model.get_id());
                        supplierPayment.setOrganizationName(supplier_model.getSupplierName());
                        supplierPayment.setInvoiceNo(edtInvoiceNo.getText().toString().trim());
//                        Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(edtInvoiceDate.getText().toString());
//                        long milliseconds = date.getTime();
                        String invoiceDate = String.valueOf(milliseconds);
                        supplierPayment.setInvoiceDate(invoiceDate);
                        Cursor invoiceDeatils_cursor = dbGoodsInwardNote.getSupplierInvoiceDetails(supplierPayment.getSupplierId(), supplierPayment.getInvoiceNo(), invoiceDate);
//                        double invoiceValue = 0;
                        if (invoiceDeatils_cursor!=null && invoiceDeatils_cursor.moveToFirst()) {
                            invoiceValue += invoiceDeatils_cursor.getDouble(invoiceDeatils_cursor.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeAmount));
                            do {
                                invoiceValue += invoiceDeatils_cursor.getDouble(invoiceDeatils_cursor.getColumnIndex(DatabaseHandler.KEY_Amount));
                            } while (invoiceDeatils_cursor.moveToNext());
                        }
                        supplierPayment.setBillAmount(invoiceValue);
                        supplierPayment.setPaymentMode("");
                        supplierPayment.setAmountPaid(0);
                        supplierPayment.setRemarks("");
                        supplierPayment.setDateOfPayment("");

                        long status = dbGoodsInwardNote.addSupplierPaymentDetails(supplierPayment);

                        if (status > 0) {
                            Log.i(TAG, "Supplier Payment Generated");
                        }
                    }

                } catch (Exception e) {
                    Log.i(TAG, "error on inserting supplier payment data." +e.getMessage());
                }

                if(l>-1)
                {
//                    savePurchaseOrder(1);
//                    mClear();
                    Toast.makeText(this, " Item quantity updated Successfully", Toast.LENGTH_SHORT).show();
                }*/
                mClear();
            } catch (Exception e)
            {
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("GoodsInwardNote", " "+e.getMessage());
            }
        }
    }

    private double calculateItemAveragePurchaseRate (int itemId) {
        double averageCost = 0;
        double totalPurchaseRate = 0;
        double totalNoPur = 0;

//            int _id = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id));
        Cursor items = dbGoodsInwardNote.getItemFromSupplierItemlinkageById(itemId);
        if (items != null && items.moveToFirst()) {
            try {
                totalPurchaseRate = items.getDouble(0);
                totalNoPur = items.getDouble(1);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (items != null)
                    items.close();
            }
        }
        averageCost = (totalPurchaseRate/totalNoPur);

        return averageCost;
    }

    public void mDateSelection_inward ()
    {
        String currentdate = DateFormat.format("yyyy-MM-dd", (new Date()).getTime()).toString();
        DateTime objDate = new DateTime(currentdate);
        try {
            android.support.v7.app.AlertDialog.Builder dlgReportDate = new android.support.v7.app.AlertDialog.Builder(this);
            final DatePicker dateReportDate = new DatePicker(this);
            // Initialize date picker value to business date
            dateReportDate.updateDate(objDate.getYear(), objDate.getMonth(), objDate.getDay());
            String strMessage = " Select the date";


            dlgReportDate
                    .setIcon(R.drawable.ic_launcher_background)
                    .setTitle("Date Selection")
                    .setMessage(strMessage)
                    .setIcon(R.mipmap.ic_company_logo)
                    .setView(dateReportDate)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
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

                            edtInvoiceDate.setText(strDate);

                            Log.d("ReportDate", "Selected Date:" + strDate);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    })
                    .show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    private double mCalculateSubTotal(){
        double dblSubTotal = 0;
        for(int i = 0; i < purchaseOrderBeanList.size(); i++){
            dblSubTotal = dblSubTotal + purchaseOrderBeanList.get(i).getDblAmount();
        }
        return dblSubTotal;
    }

    private double mCalculateGrandTotal(double dblAdditionalCharges){
        double dblGrandTotal = 0;
        for(int i = 0; i < purchaseOrderBeanList.size(); i++){
            dblGrandTotal = dblGrandTotal + purchaseOrderBeanList.get(i).getDblAmount();
        }
        dblGrandTotal = dblGrandTotal + dblAdditionalCharges;
        return dblGrandTotal;
    }

    public boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    private void mClear()
    {
        avSupplierName.setText("");
        avItemName.setText("");
        avPurchaseOrder.setFocusable(false);
        avPurchaseOrder.setFocusableInTouchMode(false);
        avPurchaseOrder.setText("NA");
        avPurchaseOrder.setFocusable(true);
        avPurchaseOrder.setFocusableInTouchMode(true);
        edtPurchaseOrderId.setText("");
        edtInvoiceNo.setText("");
        edtInvoiceDate.setText("");
        edtPhone.setText("");
        edtAddress.setText("");
        edtGSTIN.setText("");
        edtQty.setText("");
        edtMrp.setText("");
        edtPurchaseRate.setText("");
        edtAdditionalAmt.setText("");
        edtAdditionalChargeName.setText("");
        edtSubTotal.setText("");
        edtGrandTotal.setText("");
        cbStateCode.setChecked(false);
        spStateCode.setEnabled(false);
        cbAdditionalChargeStatus.setChecked(false);
        purchaseOrderBeanList.clear();
        purchaseOrderBean = null;
        supplier_model = null;
        purchaseOrderBeanList.clear();
        listName.clear();
        cbSearchInAllItems.setChecked(false);
        updateItem = false;
        btnAddItem.setText("Add Item");
        if(purchaseOrderAdapter != null){
            purchaseOrderAdapter.notifyDataSetChanged(purchaseOrderBeanList);
        }
    }

    @Override
    public void onPurchaseOrderListItemDeleteSuccess() {
        mPopulatingDataToAdapter();
    }

    private void setFilters(){
        avSupplierName.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
        edtInvoiceNo.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(10)});
        avItemName.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
        avPurchaseOrder.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtAdditionalChargeName.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
        edtQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
        edtPurchaseRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
        edtAdditionalChargeName.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
        edtAdditionalAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
      /*  edtQty.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
        edtPurchaseRate.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
        edtAdditionalAmt.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});*/
    }

    int checkCount =0;
    void inflateMultipleRateOption(Cursor cursor)
    {
        checkCount =0;
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_multiple_item_with_same_name, null, false);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(view);
        final android.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        ImageView iv_close = (ImageView)view.findViewById(R.id.iv_close);
        final TableLayout tbl_rate = (TableLayout)view.findViewById(R.id.tbl_rates);
        Button btnOk = (Button) view.findViewById(R.id.btnok) ;

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avItemName.setText("");
                dialog.dismiss();
            }
        });

        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
        int count =1;

        while(cursor!=null && cursor.moveToNext())
        {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setBackgroundResource(R.drawable.row_background);

            CheckBox checkBox = new CheckBox(this);
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

            TextView tvSno = new TextView(this);
            tvSno.setText(""+count);
            tvSno.setHeight(50);
            count++;
            tvSno.setTextSize(20);
            tvSno.setPadding(5,0,0,0);
            row.addView(tvSno);

            TextView tvName = new TextView(this);
            tvName.setHeight(50);
            tvName.setTextSize(20);
            tvName.setTextColor(Color.parseColor("#000000"));
            tvName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
            row.addView(tvName);

            TextView tvMrp  = new TextView(this);
            tvMrp.setHeight(50);
            tvMrp.setTextSize(20);
            tvMrp.setTextColor(Color.parseColor("#000000"));
            String mrp = String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Rate)));
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
                        Cursor cursor = null;
                        if (searchInAllItems)
                            cursor = dbGoodsInwardNote.getItemByID(id);
                        else
                            cursor = dbGoodsInwardNote.mGetPurchaseOrderItemsByItemId(id, supplier_model.get_id());

                        if(cursor != null && cursor.moveToFirst()) {
                            mAddDataToPurchaseOrderBean(cursor);
                        } else {
                            avItemName.setText("");
                            edtMrp.setText("");
                            edtPurchaseRate.setText("");
                            Toast.makeText(GoodsInwardNoteActivity.this, "Please link item and try again.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }
            });
            tbl_rate.addView(row);
        }
    }

    private void mAddDataToPurchaseOrderBean(Cursor cursor){
        if(cursor != null) {
            purchaseOrderBean = new PurchaseOrderBean();
            if (!searchInAllItems) {
                purchaseOrderBean.setiItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ItemId)));
                purchaseOrderBean.setStrSupplierId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierCode)));
                purchaseOrderBean.setStrSupplierName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUPPLIERNAME)));
                purchaseOrderBean.setStrSupplierPhone(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierPhone)));
                purchaseOrderBean.setStrSupplierAddress(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierAddress)));
                if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)) != null) {
                    purchaseOrderBean.setStrSupplierGSTIN(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                } else {
                    purchaseOrderBean.setStrSupplierGSTIN("");
                }
                if (!purchaseOrderBean.getStrSupplierGSTIN().isEmpty())
                    purchaseOrderBean.setStrSupplierPOS(purchaseOrderBean.getStrSupplierGSTIN().substring(0, 2));
                else
                    purchaseOrderBean.setStrSupplierPOS("");

                purchaseOrderBean.setStrBarcode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
                purchaseOrderBean.setStrItemName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));

                purchaseOrderBean.setDblPurchaseRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)));
                purchaseOrderBean.setDblRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Rate)));

                if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)) != null) {
                    purchaseOrderBean.setStrSupplyType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)));
                }
                if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierType)) != null) {
                    purchaseOrderBean.setStrSupplierType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplierType)));
                }
                if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)) != null) {
                    purchaseOrderBean.setStrHSNCode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
                }
                if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)) != null) {
                    purchaseOrderBean.setStrUOM(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)));
                }

                purchaseOrderBean.setDblCGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
                purchaseOrderBean.setDblSGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
                purchaseOrderBean.setDblIGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
                purchaseOrderBean.setDblCessRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)));
                purchaseOrderBean.setDblCessAmountPerUnit(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmountPerUnit)));
                purchaseOrderBean.setDblAdditionalCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));
            } else {
                purchaseOrderBean.setiItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id)));
                purchaseOrderBean.setStrSupplierId(""+supplier_model.get_id());
                purchaseOrderBean.setStrSupplierName(supplier_model.getSupplierName());
                purchaseOrderBean.setStrSupplierPhone(supplier_model.getSupplierPhone());
                purchaseOrderBean.setStrSupplierAddress(supplier_model.getSupplierAddress());
                if (supplier_model.getSupplierGSTIN() != null) {
                    purchaseOrderBean.setStrSupplierGSTIN(supplier_model.getSupplierGSTIN());
                } else {
                    purchaseOrderBean.setStrSupplierGSTIN("");
                }
                if (!purchaseOrderBean.getStrSupplierGSTIN().isEmpty())
                    purchaseOrderBean.setStrSupplierPOS(purchaseOrderBean.getStrSupplierGSTIN().substring(0, 2));
                else
                    purchaseOrderBean.setStrSupplierPOS("");

                if (supplier_model.getSupplierType() != null) {
                    purchaseOrderBean.setStrSupplierType(supplier_model.getSupplierType());
                }

                purchaseOrderBean.setStrBarcode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
                purchaseOrderBean.setStrItemName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));

                purchaseOrderBean.setDblPurchaseRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_AveragePurchaseRate)));
                purchaseOrderBean.setDblRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Rate)));

                if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)) != null) {
                    purchaseOrderBean.setStrSupplyType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)));
                }
                if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)) != null) {
                    purchaseOrderBean.setStrHSNCode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
                }
                if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)) != null) {
                    purchaseOrderBean.setStrUOM(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)));
                }

                purchaseOrderBean.setDblCGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
                purchaseOrderBean.setDblSGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
                purchaseOrderBean.setDblIGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
                purchaseOrderBean.setDblCessRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)));
                purchaseOrderBean.setDblCessAmountPerUnit(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmount)));
                purchaseOrderBean.setDblAdditionalCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));
            }

            edtMrp.setText(""+purchaseOrderBean.getDblRate());
            edtPurchaseRate.setText(""+purchaseOrderBean.getDblPurchaseRate());
            avItemName.setText(purchaseOrderBean.getStrItemName());
        }
    }


    private void mBarcodeSearch(String strBarcode){
        Cursor cursor = null;
        try{
            cursor = dbGoodsInwardNote.getActiveItemssbyBarCode(strBarcode);
            if(cursor != null && cursor.getCount() > 1){
                inflateMultipleRateOption(cursor);
            } else {
                if (cursor != null && cursor.moveToFirst()) {
                    Cursor cursor1 = null;
                    if (searchInAllItems)
                        cursor1 = dbGoodsInwardNote.getActiveItemssbyBarCode(strBarcode);
                    else
                        cursor1 = dbGoodsInwardNote.mGetPurchaseOrderItems(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)),supplier_model.get_id());
                    if(cursor1 != null && cursor1.moveToFirst()) {
                        mAddDataToPurchaseOrderBean(cursor1);
                    } else {
                        avItemName.setText("");
                        edtMrp.setText("");
                        edtPurchaseRate.setText("");
                        Toast.makeText(this, "Please link item and try again.", Toast.LENGTH_SHORT).show();
                    }
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
                String s_text = start > 0 ? s.subSequence(0, start).toString() : "";
                s_text += start < s.length() ? s.subSequence(start + 1, s.length()).toString() : "";
                Log.i(TAG, "Scanned data in  supplier item linkage: " + s_text);
                avItemName.setText(s_text);
                avItemName.setSelection(s_text.length());
                if(avSupplierName.getText().toString().isEmpty()){
                    avItemName.setText("");
                    Toast.makeText(GoodsInwardNoteActivity.this,"Please select supplier and search item.",Toast.LENGTH_SHORT).show();
                } else {
                    mBarcodeSearch(avItemName.getText().toString());
                }
            }
        }

    };
    

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
    }
}













