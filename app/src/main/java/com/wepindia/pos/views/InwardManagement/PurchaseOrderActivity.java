package com.wepindia.pos.views.InwardManagement;

import android.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PurchaseOrderActivity extends WepBaseActivity implements OnPurchaseOrderItemListListener {

    private Context myContext;
    private DatabaseHandler dbPurchaseOrder;
    private Toolbar toolbar;
    private MessageDialog MsgBox;
    private String strUserName, strUserId;

    private static final String TAG = PurchaseOrderActivity.class.getName();
    private final int ADD = 0;
    private final int UPDATE = 1;

    @BindView(R.id.bt_purchase_order_generate)
    Button btnGenerate;
    @BindView(R.id.bt_purchase_order_update)
    Button btnUpdate;
    @BindView(R.id.bt_purchase_order_clear)
    Button btnClear;
    @BindView(R.id.bt_purchase_order_add_item)
    Button btnAddItem;
    @BindView(R.id.bt_purchase_order_supplier_add)
    Button btnSupplierAdd;

    @BindView(R.id.av_purchase_order_supplier_name)
    AutoCompleteTextView avSupplierName;
    @BindView(R.id.av_purchase_order_supplier_po)
    InstantAutoComplete avPurchaseOrder;
    @BindView(R.id.av_purchase_order_item_name)
    AutoCompleteTextView avItemName;

    @BindView(R.id.et_PurchaseOrder_SupplierId)
    EditText edtSupplierId;
    @BindView(R.id.et_PurchaseOrderId)
    EditText edtPurchaseOrderId;

    @BindView(R.id.et_purchase_order_supplier_phone)
    EditText edtPhone;
    @BindView(R.id.et_purchase_order_supplier_address)
    EditText edtAddress;
    @BindView(R.id.et_purchase_order_supplier_gstin)
    EditText edtGSTIN;
    @BindView(R.id.et_purchase_order_Qty)
    EditText edtQty;
    @BindView(R.id.et_purchase_order_supplier_mrp)
    EditText edtMrp;
    @BindView(R.id.et_purchase_order_additional_charge_name)
    EditText edtAdditionalChargeName;
    @BindView(R.id.et_purchase_order_additional_amount)
    EditText edtAdditionalAmt;
    @BindView(R.id.et_purchase_order_sub_total)
    EditText edtSubTotal;
    @BindView(R.id.et_purchase_order_grand_total)
    EditText edtGrandTotal;
    @BindView(R.id.et_purchase_order_supplier_purchase_rate)
    EditText edtPurchaseRate;

    @BindView(R.id.et_purchase_order_invoice_no)
    EditText edtInvoiceNo;

    @BindView(R.id.et_purchase_order_invoice_date)
    EditText edtInvoiceDate;

    @BindView(R.id.sp_purchase_order_state_code)
    Spinner spStateCode;

    @BindView(R.id.cb_purchase_order_state_code)
    CheckBox cbStateCode;
    @BindView(R.id.cb_purchase_order_additional_charge)
    CheckBox cbAdditionalChargeStatus;
    @BindView(R.id.cb_purchase_order_all_items)
    CheckBox cbSearchInAllItems;

    @BindView(R.id.lv_purchase_order_list)
    ListView lvPurchaseOrderList;

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
    ArrayList<HashMap<String, String>> listName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_purchase_order);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        dbPurchaseOrder = new DatabaseHandler(PurchaseOrderActivity.this);
        myContext = this;
        MsgBox =  new MessageDialog(myContext);

        try {
            strUserName = getIntent().getStringExtra("USER_NAME");
            strUserId = getIntent().getStringExtra("USER_ID");

            Date d = new Date();
            CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
            //tvTitleDate.setText("Date : " + s);
            com.wep.common.app.ActionBarUtils.setupToolbar(PurchaseOrderActivity.this,toolbar,getSupportActionBar()," Purchase Order ",strUserName," Date:"+s.toString());

            dbPurchaseOrder.CreateDatabase();
            dbPurchaseOrder.OpenDatabase();

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
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initAutoCompleteTextDataForSupplierName();
        mPopulateStateCodeSpinnerData();
        setFilters();
        btnUpdate.setEnabled(false);
        btnUpdate.setBackgroundResource(R.color.holo_blue);
    }

    @OnClick({R.id.bt_purchase_order_generate, R.id.bt_purchase_order_update, R.id.bt_purchase_order_clear,
            R.id.bt_purchase_order_add_item, R.id.bt_purchase_order_supplier_add})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_purchase_order_generate:
                mGeneratePO();
                break;
            case R.id.bt_purchase_order_update:
                mUpdatePO();
                break;
            case R.id.bt_purchase_order_clear:
                mClear();
                break;
            case R.id.bt_purchase_order_add_item:
                if (updateItem)
                    mValidateAndUpdatingListAdapter(); // Updating item
                else
                    mValidateAndAddingListAdapter(); // Adding Item and updating quantity
                break;
            case R.id.bt_purchase_order_supplier_add:
               /* Fragment fragment = new SupplierDetailsFragment();
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }*/
                break;
            default:
                break;
        }
    }

    private void initAutoCompleteTextDataForSupplierName(){

        Cursor cursor = null;
        try {
            cursor = dbPurchaseOrder.getAllSuppliersByMode(1);
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
                Cursor supplierdetail_cursor = dbPurchaseOrder.getSupplierDetailsByPhone(supplierphone_str); // TODO: changed here
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
                    edtSupplierId.setText(""+supplier_model.get_id());
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
    }

    private void mPopulateStateCodeSpinnerData(){
        arrayPOS = getResources().getStringArray(R.array.poscode);
        //Creating the ArrayAdapter instance having the POSCode list
        ArrayAdapter adapterPOS = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayPOS);
        adapterPOS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spStateCode.setAdapter(adapterPOS);
    }

    private void initAutoCompleteTextDataForItems(final int iSupplierCode){
        mAdapterItemData = new SimpleCursorAdapter(this, R.layout.auto_complete_textview_two_strings, null,
                new String[]{DatabaseHandler.KEY_ItemName, DatabaseHandler.KEY_ItemBarcode, DatabaseHandler.KEY_Rate},
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
                        return dbPurchaseOrder.mGetPurchaseOrderItems(str,iSupplierCode);
                    else
                        return dbPurchaseOrder.mGetItemInwardSearchData(str);
                }
                return null;
            } });

        mAdapterItemData.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(DatabaseHandler.KEY_ItemName);
                return cur.getString(index);
            }});

        avItemName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                // Get the state's capital from this row in the database.
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
                        purchaseOrderBean.setStrItemName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemName)));

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
                    edtPurchaseRate.setText("" + purchaseOrderBean.getDblPurchaseRate());
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
                Log.i(TAG, "Purchase order no search data in purchase order table." +str);
                if(str != null && !avSupplierName.getText().toString().isEmpty()) {
                    return dbPurchaseOrder.mGetPurchaseOrderNo(str,iSupplierId);
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
                    btnUpdate.setEnabled(true);
                    btnUpdate.setBackgroundResource(R.drawable.button_blue_color);
                    btnGenerate.setEnabled(false);
                    btnGenerate.setBackgroundResource(R.color.holo_blue);
                }
            }
        });
    }

    private void mValidateAndAddingListAdapter(){
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
        /*if(Double.parseDouble(edtPurchaseRate.getText().toString().trim()) < 0)
        {
            MsgBox.Show("Wrong Information", "Purchase rate should be greater than zero.");
            return;
        }*/
        /*if(Double.parseDouble(edtPurchaseRate.getText().toString().trim()) > purchaseOrderBean.getDblRate())
        {
            MsgBox.Show("Invalid Information", "Purchase rate cannot be greater than MRP.");
            return;
        }*/
        purchaseOrderBean.setDblPurchaseRate(Double.parseDouble(edtPurchaseRate.getText().toString().trim()));

        Cursor duplicateLinkage = dbPurchaseOrder.getDuplicateSupplierItemLinkage(Integer.parseInt(edtSupplierId.getText().toString().trim()),
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
        supplierItemLinkageBean.setiSupplierID(Integer.parseInt(edtSupplierId.getText().toString().trim()));
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
        lLinkItemStatus = dbPurchaseOrder.mLinkSupplierWithItem(supplierItemLinkageBean, Constants.INSERT);
        if(lLinkItemStatus>0) {
            Log.i(TAG, "New Item linked successfully...");
        }
    }

    private void mFetchDataOnPurchaseOrderNo(String strPurchaseOrderNo, String supplierId){
        Cursor cursorPO = dbPurchaseOrder.mGetPurchaseOrderData(strPurchaseOrderNo, supplierId);
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
                    if (purchaseOrderBean.getStrInvoiceNo() != null && edtInvoiceNo.getText().toString().isEmpty())
                        edtInvoiceNo.setText(purchaseOrderBean.getStrInvoiceNo());
                    if (purchaseOrderBean.getStrInvoiceDate() != null && edtInvoiceDate.getText().toString().isEmpty())
                        edtInvoiceDate.setText(purchaseOrderBean.getStrInvoiceDate());
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
                /*if (dblPurchaseRate <= 0) {
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

        String ownerPOS = dbPurchaseOrder.getOwnerPOS();
        if (ownerPOS.equalsIgnoreCase(POS))
            isSame = true;

        return isSame;
    }

    private void mGeneratePO(){
        if(purchaseOrderBeanList != null && purchaseOrderBeanList.size() > 0) {
            if (avSupplierName.getText().toString().isEmpty() || edtAddress.getText().toString().isEmpty()
                    || edtPhone.getText().toString().isEmpty()) {
                MsgBox.Show(" Insufficient Information ", "Please fill Supplier's Firm Details");
                return;
            }
            if (purchaseOrderAdapter == null) {
                MsgBox.Show("Insufficient Information ", " Please add an item");
                return;
            }
            if (cbStateCode.isChecked() && spStateCode.getSelectedItem().toString().equals("Select")) {
                MsgBox.Show("Insufficient Information ", " Please select state for Supplier's Firm");
                return;
            }

            if (cbAdditionalChargeStatus.isChecked()) {
                if (edtAdditionalChargeName.getText().toString().isEmpty()){
                    MsgBox.Show("Insufficient Information", "Please enter the additional charge name.");
                    return;
                }
                if (edtAdditionalAmt.getText().toString().isEmpty()){
                    MsgBox.Show("Insufficient Information", "Please enter the additional charge.");
                    return;
                }
            }

            String purchaseorderno = avPurchaseOrder.getText().toString();
           /* if (!purchaseorderno.equals("")) {
                Cursor isPurchaseOrderExist = dbPurchaseOrder.getPurchaseOrderById(supplier_model.get_id(),
                        Integer.parseInt(edtPurchaseOrderId.getText().toString().trim()), Integer.parseInt(purchaseorderno));
                if (isPurchaseOrderExist != null)
            }*/
            if (!purchaseorderno.equals("")) {
                MsgBox.Show("Warning", "Purchase Order No. is auto generated, kindly clear Purchase Order No. field.");
                return;
            }
            if (purchaseorderno.equals("")) {
                // generate purchase order no
                Cursor crsr = dbPurchaseOrder.getMaxPurchaseOrderNo();
                int Max =1;
                if (crsr != null && crsr.moveToFirst())
                {
                    if(crsr.getInt(0) != -1)
                    {
                        Max = crsr.getInt(0)+1;
                    }
                }
                avPurchaseOrder.setText(String.valueOf(Max));

                long saved = savePurchaseOrder();
                if (saved >-1)// successfully added bill items
                {
                    generatePOPdf();
                    sendPurchaseOrder(String.valueOf(Max), supplier_model.getSupplierEmail());
                    Toast.makeText(this, "Purchase Order Generated :"+Max, Toast.LENGTH_SHORT).show();
                    Log.d(" PurchaseOrderActivty", " Purchase Order Generated : "+Max);
                    mClear();
                }
            }
           /* else {
                Cursor duplicacy_crsr = dbPurchaseOrder.checkduplicatePO(Integer.parseInt(purchaseOrderBeanList.get(0).getStrSupplierId()), purchaseorderno);
                if (duplicacy_crsr!= null && duplicacy_crsr.moveToFirst())
                {
                    MsgBox.setTitle(" Duplicate ")
                            .setIcon(R.mipmap.ic_company_logo)
                            .setMessage(" Purchase Order No already present for Supplier's Firm. Do you want to overwrite")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    savePurchaseOrder();
                                    mClear();
                                }
                            })
                            .show();
                }else { // no duplicacy
                    long saved = savePurchaseOrder();
                    if (saved >0)// successfully added bill items
                    {
                        Toast.makeText(this, "Purchase Order Generated ", Toast.LENGTH_SHORT).show();
                        Log.d(" PurchaseOrderActivty", " Purchase Order Generated at "+saved);
                        mClear();
                    }
                }
            }*/

         /*   long saved = savePurchaseOrder();
            if (saved >0)// successfully added bill items
            {
                Toast.makeText(this, "Purchase Order Generated ", Toast.LENGTH_SHORT).show();
                Log.d(" PurchaseOrderActivty", " Purchase Order Generated at "+saved);
                mClear();
            }*/

        } else {
            MsgBox.Show(" Insufficient Information ", "Please fill all the necessary details");
        }
    }

    private void sendPurchaseOrder(final String purchaseOrderNo, final String supplierEmail) {

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

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Share PurchaseOrder");

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
                if (etCustEmail.isEnabled() && etCustEmail.getText().toString().isEmpty()) {
                    etCustEmail.setError("Please fill this field.");
                } else if (etCustEmail.isEnabled() && etCustEmail != null
                        && !etCustEmail.getText().toString().isEmpty()
                        && !Validations.isValidEmailAddress(etCustEmail.getText().toString())) {
                    etCustEmail.setError("Please Enter Valid Email id.");
                } else {
                    send(etCustEmail.getText().toString(), purchaseOrderNo, "-", "-");
                    Toast.makeText(PurchaseOrderActivity.this, "Sending....", Toast.LENGTH_SHORT).show();
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

        SendBillInfoToCustUtility smsUtility = new SendBillInfoToCustUtility(this, "Purchase Order", emailContent, "", "", false, true, false,
                this, custEmail, attachment, filename, firmName);
        smsUtility.sendBillInfo();
        Toast.makeText(this, "Sharing Purchase Order...", Toast.LENGTH_SHORT).show();
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

    long savePurchaseOrder()
    {
        // 1 -> save
        // 2- >Edit
        if(purchaseOrderBeanList != null && purchaseOrderBeanList.size() > 0){
            if(purchaseOrderBeanList.get(0).getStrSupplierId().isEmpty()){
                MsgBox.Show(" Insufficient Information ", "Supplier's Firm is not in Database. Please add Supplier's Firm");
                return -1;
            }
        }
        // Inserted Row Id in database table
        long result = -1;
        int menucode = 0;
        String additionalchargename = "", additionalCharge = "";

        try
        {
            String purchaseorderno = avPurchaseOrder.getText().toString();
            String supp_name = avSupplierName.getText().toString();
            int suppliercode  = Integer.parseInt(purchaseOrderBeanList.get(0).getStrSupplierId());
            String sup_phone = edtPhone.getText().toString();
            String sup_address = edtAddress.getText().toString();
            String sup_gstin = edtGSTIN.getText().toString();

            int deletedrows = dbPurchaseOrder.deletePurchaseOrder( suppliercode,purchaseorderno);
            Log.d("InsertPurchaseOrder", deletedrows+" Rows deleted for Purchase Order "+purchaseorderno);

            Date d = new Date();
            CharSequence currentdate = DateFormat.format("dd-MM-yyyy", d.getTime());
            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(currentdate.toString());
            long milliseconds = date.getTime();

            for (PurchaseOrderBean po :purchaseOrderBeanList) {

                po.setiPurchaseOrderNo(Integer.parseInt(purchaseorderno));
                po.setStrPurchaseOrderDate(String.valueOf(milliseconds));
                po.setStrInvoiceNo(edtInvoiceNo.getText().toString().trim());
                po.setStrInvoiceDate(edtInvoiceDate.getText().toString().trim());

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
                result = dbPurchaseOrder.InsertPurchaseOrder(po);
                if(result>0) {
                    Log.d("InsertPurchaseOrder", " item inserted at position:" + purchaseorderno);
//                    Toast.makeText(this, "Purchase Order generate with PO no.: " + result, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("log", e.getMessage(), e);
            result = -1;
        }
        return result;
    }

    private void generatePOPdf() {
        try {

            PdfPOBean pdfItem = null;

            if (purchaseOrderBeanList.size() < 1) {
                MsgBox.Show("Warning", "Insert item before Print Bill");
                return;
            } else {
                if ((!avPurchaseOrder.getText().toString().trim().equalsIgnoreCase(""))) {

                    pdfItem = new PdfPOBean();

                    pdfItem.setiPurchaseOrderNo(Integer.parseInt(avPurchaseOrder.getText().toString()));
                    pdfItem.setStrInvoiceNo("-");
                    pdfItem.setStrInvoiceDate("-");
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
                        if (!edtGSTIN.getText().toString().isEmpty())
                            pdfItem.setStrSupplierPOS(edtGSTIN.getText().toString().substring(0,2));
                        else
                            pdfItem.setStrSupplierPOS("");
                    }

                    Cursor crsrOwnerDetails = null;
                    try {
                        crsrOwnerDetails = dbPurchaseOrder.getOwnerDetail();

                        if (crsrOwnerDetails.moveToFirst()) {
                            try {
                                pdfItem.setOwnerPos(dbPurchaseOrder.getOwnerPOS());
                                pdfItem.setOwnerGstin(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                                pdfItem.setOwnerName(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Owner_Name)));
                                pdfItem.setOwnerAddress(crsrOwnerDetails.getString(crsrOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Address)));
//                                String ownerPos = "";
                              /*  for (int i = 0; i < arrayPOS.length; i++) {
                                    if (arrayPOS[i].contains(pdfItem.getOwnerStateCode()))
                                        ownerPos = arrayPOS[i];
                                }
                                ownerPos = ownerPos.substring(0, ownerPos.length() - 2);*/
                                pdfItem.setOwnerPos(dbPurchaseOrder.getOwnerPOS());
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

    public void mUpdatePO() {
        String purchaseorder = avPurchaseOrder.getText().toString();

        if (avPurchaseOrder.getText().toString().isEmpty()) {
            MsgBox.Show("Insufficient Information", "Please Select Purchase Order");
            return;
        } else {

            int purchaseorderId = Integer.parseInt(edtPurchaseOrderId.getText().toString().trim());
            int porchaseOrderNo = Integer.parseInt(avPurchaseOrder.getText().toString().trim());

            Cursor isPurchaseOrderExist = dbPurchaseOrder.getPurchaseOrderById(supplier_model.get_id(),
                    purchaseorderId, porchaseOrderNo);
            if (isPurchaseOrderExist != null && isPurchaseOrderExist.moveToFirst()){

            } else {
                MsgBox.Show("Invalid Information", "Please Select a valid Purchase Order");
                return;
            }

        }
        if (avSupplierName.getText().toString().isEmpty()) {
            MsgBox.Show("Insufficient Information", "Please Select/Add Supplier's Firm ");
            return;
        }
        if (purchaseOrderAdapter == null) {
            MsgBox.Show("Insufficient Information", "Please add an item ");
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
        if (cbAdditionalChargeStatus.isChecked()) {
            if (edtAdditionalChargeName.getText().toString().isEmpty()){
                MsgBox.Show("Insufficient Information", "Please enter the additional charge name.");
                return;
            }
            if (edtAdditionalAmt.getText().toString().isEmpty()){
                MsgBox.Show("Insufficient Information", "Please enter the additional charge.");
                return;
            }
        }

        Cursor duplicacy_crsr = dbPurchaseOrder.checkduplicatePO(Integer.parseInt(purchaseOrderBeanList.get(0).getStrSupplierId()), purchaseorder);
        if (duplicacy_crsr != null && duplicacy_crsr.moveToFirst()) {
            long saved = savePurchaseOrder();
            if (saved > 0)// successfully added bill items
            {
                generatePOPdf();
                sendPurchaseOrder(purchaseorder, supplier_model.getSupplierEmail());
                Toast.makeText(this, " Purchase Order Updated", Toast.LENGTH_SHORT).show();
                Log.d(" PurchaseOrderActivty", " Purchase Order Updated at " + saved);
                mClear();
            }
        }
        else { // no duplicacy
            MsgBox.setTitle(" Error ")
                    .setIcon(R.mipmap.ic_company_logo)
                    .setMessage("This Purchase Order No is not present. Do you want to create new ")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            savePurchaseOrder();
                            generatePOPdf();
                            sendPurchaseOrder(avPurchaseOrder.getText().toString().trim(), supplier_model.getSupplierEmail());
                            mClear();
                        }
                    })
                    .show();
        }
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

    private void mClear()
    {
        edtInvoiceNo.setText("");
        edtInvoiceDate.setText("");
        avSupplierName.setText("");
        avItemName.setText("");
        avPurchaseOrder.setFocusable(false);
        avPurchaseOrder.setFocusableInTouchMode(false);
        avPurchaseOrder.setText("");
        avPurchaseOrder.setFocusable(true);
        avPurchaseOrder.setFocusableInTouchMode(true);
        edtSupplierId.setText("");
        edtPurchaseOrderId.setText("");
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
        listName.clear();
        btnUpdate.setEnabled(false);
        btnUpdate.setBackgroundResource(R.color.holo_blue);
        updateItem = false;
        btnAddItem.setText("Add Item");
        cbSearchInAllItems.setChecked(false);
        if(purchaseOrderAdapter != null){
            purchaseOrderAdapter.notifyDataSetChanged(purchaseOrderBeanList);
        }

        btnUpdate.setEnabled(false);
        btnUpdate.setBackgroundResource(R.color.holo_blue);
        btnGenerate.setEnabled(true);
        btnGenerate.setBackgroundResource(R.drawable.button_blue_color);

    }

    @Override
    public void onPurchaseOrderListItemDeleteSuccess() {
        mPopulatingDataToAdapter();
    }

    private void setFilters(){
        avSupplierName.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
        avItemName.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
        avPurchaseOrder.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtAdditionalChargeName.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
        edtQty.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
        edtPurchaseRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
        edtAdditionalAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
        /*edtQty.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
        edtPurchaseRate.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
        edtAdditionalAmt.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});*/
    }

    int checkCount =0;
    void inflateMultipleRateOption(Cursor cursor)
    {
        checkCount =0;
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_multiple_item_with_same_name, null, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                            cursor = dbPurchaseOrder.getItemByID(id);
                        else
                            cursor = dbPurchaseOrder.mGetPurchaseOrderItemsByItemId(id, supplier_model.get_id());

                        if(cursor != null && cursor.moveToFirst()) {
                            mAddDataToPurchaseOrderBean(cursor);
                        } else {
                            avItemName.setText("");
                            edtMrp.setText("");
                            edtPurchaseRate.setText("");
                            Toast.makeText(PurchaseOrderActivity.this, "Please link item and try again.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }
            });
            tbl_rate.addView(row);
        }
    }


    private void mBarcodeSearch(String strBarcode){
        Cursor cursor = null;
        try{
            cursor = dbPurchaseOrder.getActiveItemssbyBarCode(strBarcode);
            if(cursor != null && cursor.getCount() > 1){
                inflateMultipleRateOption(cursor);
            } else {
                if (cursor != null && cursor.moveToFirst()) {
                    Cursor cursor1 = null;
                    if (searchInAllItems)
                        cursor1 = dbPurchaseOrder.getActiveItemssbyBarCode(strBarcode);
                    else
                        cursor1 = dbPurchaseOrder.mGetPurchaseOrderItems(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)),supplier_model.get_id());
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

    private void mAddDataToPurchaseOrderBean(Cursor cursor){
        if(cursor != null) {
            purchaseOrderBean = new PurchaseOrderBean();
            if (!searchInAllItems) {
                purchaseOrderBean.setiItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ItemId)));
                purchaseOrderBean.setStrSupplierId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_id)));
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

                purchaseOrderBean.setDblPurchaseRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_PurchaseRate)));

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
            edtPurchaseRate.setText("" + purchaseOrderBean.getDblPurchaseRate());
            avItemName.setText(purchaseOrderBean.getStrItemName());
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
                    Toast.makeText(PurchaseOrderActivity.this,"Please select supplier and search item.",Toast.LENGTH_SHORT).show();
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














