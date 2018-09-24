package com.wepindia.pos.views.Masters.OutwardItemMaster;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.models.ItemModel;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.FilePickerDialogFragment;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.GenericClasses.OnFilePickerClickListener;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.EMOJI_FILTER;
import com.wepindia.pos.utils.InputFilterForDoubleMinMax;
import com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners.OnItemListetner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;


public class ItemMasterEditItemDialogFragment extends DialogFragment implements Constants, OnFilePickerClickListener {

    private static final String TAG = ItemMasterEditItemDialogFragment.class.getSimpleName();
    private static long back_pressed;
    private static final String TextInputEditText = "android.support.design.widget.TextInputEditText";

    Context myContext ;
    MessageDialog MsgBox ;
    ItemModel itemObj;
    int POPULATING_SCREEN =0;
    OnItemListetner onItemAddListener;
    OnFilePickerClickListener filePickerClickListener;
    String strImageUri = "";
    String businessDate="";
    DatabaseHandler dbItems;


    @BindView(R.id.tv_ItemMasterAdditemTitle) TextView tv_title;
    @BindView(R.id.sp_item_master_add_dialog_department)  Spinner spnrDept;
    @BindView(R.id.sp_item_master_add_dialog_category)  Spinner spnrCateg;
    @BindView(R.id.sp_item_master_add_dialog_kitchen)  Spinner spnrKitchen;
    @BindView(R.id.sp_item_master_add_dialog_uom) Spinner spnrUOM;
    @BindView(R.id.sp_item_master_add_dialog_supply_type) Spinner spnrSupplyType;

    @BindView(R.id.et_item_master_add_dialog_short_code) EditText et_shortCode;
    @BindView(R.id.et_item_master_add_dialog_Item_long_name) TextInputEditText et_ItemLongName;
    @BindView(R.id.et_item_master_add_dialog_item_short_name) TextInputEditText et_ItemShorName;
    @BindView(R.id.et_item_master_add_dialog_item_barcode) TextInputEditText et_ItemBarCode;
    @BindView(R.id.et_item_master_add_dialog_mrp) TextInputEditText et_MRP;
    @BindView(R.id.et_item_master_add_dialog_retail_price) TextInputEditText et_retailPrice;
    @BindView(R.id.et_item_master_add_dialog_whole_sale_price) TextInputEditText et_wholeSalePrice;
    @BindView(R.id.et_item_master_add_dialog_purchase_rate) TextInputEditText et_purchaseRate;
    @BindView(R.id.et_item_master_add_dialog_quantity) TextInputEditText et_quantity;
    @BindView(R.id.et_item_master_add_dialog_hsn_code) TextInputEditText et_hsnCode;
    @BindView(R.id.et_item_master_add_dialog_pack_size) TextInputEditText et_packSize;
    @BindView(R.id.et_item_master_add_dialog_disc_per) TextInputEditText et_discountPercent;
    @BindView(R.id.et_item_master_add_dialog_disc_amt) TextInputEditText et_discountAmount;
    @BindView(R.id.et_item_master_add_dialog_expire_date) TextInputEditText et_expiryDate;
    @BindView(R.id.et_item_master_add_dialog_cgst_rate) TextInputEditText et_cgstRate;
    @BindView(R.id.et_item_master_add_dialog_sgst_rate) TextInputEditText et_sgstRate;
    @BindView(R.id.et_item_master_add_dialog_igst_rate) TextInputEditText et_igstRate;
    @BindView(R.id.et_item_master_add_dialog_cess_rate) TextInputEditText et_cessRate;
    @BindView(R.id.et_item_master_add_dialog_cess_amount) TextInputEditText et_cessAmount;
    @BindView(R.id.et_item_master_add_dialog_additional_cess_amount) TextInputEditText et_additionalCessAmount;
    @BindView(R.id.et_item_master_add_dialog_min_stock) TextInputEditText et_low_stock;
    @BindView(R.id.iv_item_master_add_dialog_item_image) ImageView iv_itemImage;
    @BindView(R.id.bt_item_master_add_dialog_browse_image) Button btn_browseImage;
    @BindView(R.id.cb_item_master_add_dialog_fav) CheckBox ck_fav;
    @BindView(R.id.sc_item_master_add_dialog_active) android.support.v7.widget.SwitchCompat sc_active;
    @BindView(R.id.tv_imageURL) TextView tv_imageURL;
    @BindView(R.id.bt_item_master_add_dialog_save) Button btn_updateItem;
    @BindView(R.id.bt_item_master_add_dialog_clear) Button btn_clear;
    @BindView(R.id.bt_item_master_add_dialog_close) Button btn_close;

    @BindView(R.id.tv_item_id) TextView tv_itemid;

    @BindView(R.id.et_item_master_add_dialog_incentive_amount) TextInputEditText etIncentiveAmount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        itemObj = (ItemModel) getArguments().getSerializable("ItemModelObject");
        View fragmentView = inflater.inflate(R.layout.fragment_item_master_add_item_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this,fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"OnViewCreated()");
        try{
            et_quantity.setEnabled(false);
            initialiseVariables();
            loadSpinnerData();
            applyDecimalValidation();
            applyValidations();
            populateScreen();
            applyBillSettings();
//            addTextWatcher();
            btn_updateItem.setText("Update");
            tv_title.setText("Update Item");
        }catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
        // ItemMastersFragment.pb.setVisibility(View.GONE);
        //ItemMastersFragment.ll_itemMasters.setVisibility(View.VISIBLE);

    }

    void populateScreen()
    {
        POPULATING_SCREEN =1;
        tv_itemid.setText(String.valueOf(itemObj.get_id()));
        et_shortCode.setText(String.valueOf(itemObj.getMenuCode()));
        et_ItemShorName.setText(itemObj.getShortName());
        et_ItemLongName.setText(itemObj.getLongName());
        et_ItemBarCode.setText(itemObj.getBarCode());
        spnrUOM.setSelection(getUOMIndex(itemObj.getUOM()));
        /*Cursor brand = dbItems.getBrand(itemObj.getKitchenCode());
        if (brand != null && brand.moveToFirst())
            spnrKitchen.setSelection(getSpinnerCode(spnrKitchen, brand.getString(brand.getColumnIndex(DatabaseHandler.KEY_BrandName))));
        else
            spnrKitchen.setSelection(0);
        Cursor department = dbItems.getDepartment(itemObj.getDeptCode());
        if (department != null && department.moveToFirst())
            spnrDept.setSelection(getSpinnerCode(spnrDept, department.getString(department.getColumnIndex(DatabaseHandler.KEY_DepartmentName))));
        else
            spnrDept.setSelection(0);
        Cursor category = dbItems.getCategory(itemObj.getCategCode());
        if (category != null && category.moveToFirst())
            spnrCateg.setSelection(getSpinnerCode(spnrCateg, category.getString(category.getColumnIndex(DatabaseHandler.KEY_CategoryName))));
        else
            spnrCateg.setSelection(0);*/

        spnrKitchen.setSelection(getSpinnerIndex(spnrKitchen, itemObj.getKitchenCode(), "kitchen"));
        spnrDept.setSelection(getSpinnerIndex(spnrDept, itemObj.getDeptCode(), "department"));

       /* spnrKitchen.setSelection(itemObj.getKitchenCode());
        spnrDept.setSelection(itemObj.getDeptCode());
        spnrCateg.setSelection(itemObj.getCategCode());*/

        et_retailPrice.setText(String.format("%.2f",itemObj.getRetaiPrice()));
        et_MRP.setText(String.format("%.2f",itemObj.getMrp()));
        et_wholeSalePrice.setText(String.format("%.2f",itemObj.getWholeSalePrice()));
        et_purchaseRate.setText(String.format("%.2f",itemObj.getPurchaseRate()));
        et_low_stock.setText(String.format("%.2f", itemObj.getMinimumStock()));
        et_quantity.setText(String.format("%.2f",itemObj.getQuantity()));
        et_hsnCode.setText(itemObj.getHsn());
        et_cgstRate.setText(String.format("%.2f",itemObj.getCgstRate()));
        et_sgstRate.setText(String.format("%.2f",itemObj.getSgstRate()));
        et_igstRate.setText(String.format("%.2f",itemObj.getIgstRate()));
        et_cessRate.setText(String.format("%.2f",itemObj.getCessRate()));
        et_cessAmount.setText(String.format("%.2f",itemObj.getCessAmount()));
        et_additionalCessAmount.setText(String.format("%.2f",itemObj.getAdditionalCessAmount()));
        if(itemObj.getImageURL().equals("")) {
            iv_itemImage.setImageResource(R.mipmap.ic_image_blank);
            strImageUri = "";
        }
        else {
            iv_itemImage.setImageURI(Uri.fromFile(new File(itemObj.getImageURL())));
            strImageUri = itemObj.getImageURL();
        }
        if(itemObj.getFav() ==1)
            ck_fav.setChecked(true);
        else
            ck_fav.setChecked(false);
        if(itemObj.getActive() ==1)
            sc_active.setChecked(true);
        else
            sc_active.setChecked(false);

        if (itemObj.getCategCode() > 0)
            spnrCateg.setSelection(getSpinnerIndex(spnrCateg, itemObj.getCategCode(), "category"));
        else
            loadCategoryforDeptId(-2);
        if(itemObj.getIncentive_amount() > 0) {
            etIncentiveAmount.setText(String.format("%.2f", itemObj.getIncentive_amount()));
        } else {
            etIncentiveAmount.setText("0");
        }

        POPULATING_SCREEN =0;
    }

    private int getSpinnerIndex(Spinner spinner, int code, String type){
        int index = 0;

        switch (type) {
            case "kitchen":
                for (int i=0; i<spinner.getCount(); i++) {
                    Cursor kitchen = (Cursor) spinner.getAdapter().getItem(i);
                    if (kitchen != null && kitchen.getInt(kitchen.getColumnIndex("KitchenCode")) == code) {
                        return i;
                    }
                }
                break;
            case "department":
                for (int i=0; i<spinner.getCount(); i++) {
                    Cursor department = (Cursor) spinner.getAdapter().getItem(i);
                    if (department != null && department.getInt(department.getColumnIndex("DepartmentCode")) == code) {
                        return i;
                    }
                }
                break;
            case "category":
                for (int i=0; i<spinner.getCount(); i++) {
                    Cursor category = (Cursor) spinner.getAdapter().getItem(i);
                    if (category != null && category.getInt(category.getColumnIndex("CategoryCode")) == code) {
                        return i;
                    }
                }
                break;
        }

        return index;
    }

    void applyDecimalValidation()
    {
        try {
            /*et_retailPrice.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
            et_MRP.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
            et_wholeSalePrice.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
            et_purchaseRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
            et_quantity.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
            et_low_stock.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
            et_cgstRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2,2)});
            et_sgstRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2,2)});
            et_igstRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2,2)});
            et_cessRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});
            et_cessAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
            et_additionalCessAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});*/
            et_retailPrice.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
            et_MRP.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
            et_wholeSalePrice.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
            et_purchaseRate.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
            et_quantity.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
            et_low_stock.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
            et_cgstRate.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 99.99)});
            et_sgstRate.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 99.99)});
            et_igstRate.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 99.99)});
            et_cessRate.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999.99)});
            et_cessAmount.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
            et_additionalCessAmount.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
            etIncentiveAmount.setFilters(new InputFilter[] {new InputFilterForDoubleMinMax(0, 999999.99)});
        }catch(Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
    }
    void initialiseVariables()
    {
        myContext = getActivity();
        dbItems = new DatabaseHandler(myContext);
        dbItems.CreateDatabase();
        dbItems.OpenDatabase();

        MsgBox = new MessageDialog(myContext);
        Cursor businessDate_cursor = dbItems.getBusinessDate();
        if(businessDate_cursor!=null && businessDate_cursor.moveToNext())
        {
            businessDate = businessDate_cursor.getString(businessDate_cursor.getColumnIndex(DatabaseHandler.KEY_BusinessDate));
        }else
        {
            Date date = new Date();
            businessDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
        }
        //dbItems = new DatabaseHandler(myContext);
    }


    @OnClick({R.id.bt_item_master_add_dialog_close, R.id.bt_item_master_add_dialog_clear,
            R.id.bt_item_master_add_dialog_save,R.id.bt_item_master_add_dialog_browse_image})
    protected void mBtnClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bt_item_master_add_dialog_close : close();
                break;
            case R.id.bt_item_master_add_dialog_clear : clear();
                break;
            case R.id.bt_item_master_add_dialog_save : updateItem();
                break;
            case R.id.bt_item_master_add_dialog_browse_image : browseImage();
                break;
        }
    }

    @OnTextChanged(value = {R.id.et_item_master_add_dialog_cgst_rate, R.id.et_item_master_add_dialog_sgst_rate},callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected  void handleChange(Editable editable)
    {
        try{
            String SGST = et_sgstRate.getText().toString().equals("")?"0.00":et_sgstRate.getText().toString();
            String CGST = et_cgstRate.getText().toString().equals("")?"0.00":et_cgstRate.getText().toString();
            if(SGST.equals("") || SGST.equals(".")){
                SGST = ("0");
            }
            if(CGST.equals("")|| CGST.equals(".")){
                CGST = ("0");
            }

            double sgst_d = Double.parseDouble(SGST);
            double cgst_d = Double.parseDouble(CGST);
            double igst_d = sgst_d+cgst_d;
            et_igstRate.setText(String.format("%.2f",igst_d));
        }catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }

    }
    @OnTextChanged(value = {R.id.et_item_master_add_dialog_mrp, R.id.et_item_master_add_dialog_retail_price},callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected  void handleChange_for_rate(Editable editable)
    {
        try{
            String Mrp = et_MRP.getText().toString().equals("")?"0.00":et_MRP.getText().toString();
            String retailPrice = et_retailPrice.getText().toString().equals("")?"0.00":et_retailPrice.getText().toString();
            if(Mrp.equals("") || Mrp.equals(".")){
                Mrp = ("0");
            }
            if(retailPrice.equals("")|| retailPrice.equals(".")){
                retailPrice = ("0");
            }
            double mrp_d = Double.parseDouble(Mrp);
            double retail_d = Double.parseDouble(retailPrice);
            if(mrp_d> retail_d){
                double discountAmt = mrp_d-retail_d;
                double discountPercent = (discountAmt/mrp_d) *100;
                et_discountAmount.setText(String.format("%.2f",discountAmt));
                et_discountPercent.setText(String.format("%.2f",discountPercent));
            }else
            {
                et_discountAmount.setText("0.00");
                et_discountPercent.setText("0.00");
            }
        }catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
    }

    private Boolean mIsSpinnerFirstCall = true;
    @OnItemSelected(R.id.sp_item_master_add_dialog_department)
    public void onItemSelected(AdapterView<?> parent, View view, int position)
    {
        if(!mIsSpinnerFirstCall) {
            Cursor mdeptCursor = (Cursor)spnrDept.getItemAtPosition(position);
            int mdeptCode = mdeptCursor.getInt(mdeptCursor.getColumnIndex("DepartmentCode"));
            loadCategoryforDeptId(mdeptCode);
        }
        mIsSpinnerFirstCall = false;
    }


    private boolean ValidateCompulsoryField()
    {
        boolean flag  = false;
        if(et_ItemShorName.getText().toString().equals(""))
        {
            MsgBox.Show("Mandory Information", getString(R.string.itemNameMessage));
            return flag;
        }
        if(spnrUOM.getSelectedItem().toString().equals("Select"))
        {
            MsgBox.Show("Mandory Information", getString(R.string.itemUOMMessage));
            return flag;
        }
       /* if(et_retailPrice.getText().toString().equals(""))
        {
            MsgBox.Show("Mandory Information", getString(R.string.itemRetailPriceMessage));
            return flag;
        }*/
        double retailPrice = Double.parseDouble(et_retailPrice.getText().toString());
       /* if(!et_MRP.getText().toString().equals(""))
        {
            double mrp = Double.parseDouble(et_MRP.getText().toString());
            if(retailPrice > mrp)
            {
                MsgBox.Show("Inconsistent Data", getString(R.string.retailPriceGreaterThanMRPError));
                return flag;
            }
        }
        if(!et_wholeSalePrice.getText().toString().equals(""))
        {
            double wholesalePrice = Double.parseDouble(et_wholeSalePrice.getText().toString());
            if(wholesalePrice > retailPrice)
            {
                MsgBox.Show("Inconsistent Data", getString(R.string.wholePriceGeaterThanRetailError));
                return flag;
            }
        }*/
        if(!et_cessAmount.getText().toString().isEmpty() && !et_cessRate.getText().toString().isEmpty())
        {
            double cessRate = Double.parseDouble(et_cessRate.getText().toString());
            double cessAmount = Double.parseDouble(et_cessAmount.getText().toString());
            if(cessRate > 0 && cessAmount > 0)
            {
                MsgBox.Show("Inconsistent Data", getString(R.string.cessRateAndcessAmountBothError));
                return flag;
            }
        }
        /*double cgstRate = et_cgstRate.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_cgstRate.getText().toString().trim())));
        double sgstRate = et_sgstRate.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_sgstRate.getText().toString().trim())));
        double igstRate = et_igstRate.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_igstRate.getText().toString().trim())));
        if(cgstRate+sgstRate != igstRate)
        {
            MsgBox.Show("Inconsistent Data", "sum of cgstrate and sgstrate should be equal to igstrate");
            return flag;
        }*/
        flag = true;
        return flag;
    }

    private void updateItem()
    {
        try{
            if(ValidateCompulsoryField())
            {
                int shortCode = et_shortCode.getText().toString().trim().equals("")? 0: Integer.valueOf(et_shortCode.getText().toString().trim());
                String shortName = et_ItemShorName.getText().toString().trim();
                String longName = et_ItemLongName.getText().toString().trim();
                if(longName.isEmpty()){
                    longName = shortName;
                }
                String barCode = et_ItemBarCode.getText().toString();
                String uom = getUOMString();
                int brandCode  = getSpinnerCode(spnrKitchen, "Kitchen");
                int deptCode = getSpinnerCode(spnrDept, "Department");
                int categCode = getSpinnerCode(spnrCateg, "Category");
                String supplyType = spnrSupplyType.getSelectedItem().toString();


                double retailPrice = Double.parseDouble(String.format("%.2f",Double.parseDouble(et_retailPrice.getText().toString().trim())));
                double mrp = et_MRP.getText().toString().equals("")? retailPrice: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_MRP.getText().toString().trim())));
                double wholeSalePrice = et_wholeSalePrice.getText().toString().equals("")? retailPrice: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_wholeSalePrice.getText().toString().trim())));
                double purchaseRate = et_purchaseRate.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_purchaseRate.getText().toString().trim())));

                double minimum_stock = et_low_stock.getText().toString().equals("") ? 0.00 : Double.parseDouble(et_low_stock.getText().toString().trim());
                double quantity = et_quantity.getText().toString().equals("") ? 0.00 : Double.parseDouble(String.format("%.2f", Double.parseDouble(et_quantity.getText().toString().trim())));
                String hsn = et_hsnCode.getText().toString().trim();

                double discountPercent = et_discountPercent.getText().toString().equals("")? retailPrice: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_discountPercent.getText().toString().trim())));
                double discountAmount  = et_discountAmount.getText().toString().equals("")? retailPrice: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_discountAmount.getText().toString().trim())));

                double cgstRate = et_cgstRate.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_cgstRate.getText().toString().trim())));
                double sgstRate = et_sgstRate.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_sgstRate.getText().toString().trim())));
                double igstRate = et_igstRate.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_igstRate.getText().toString().trim())));
                double cessRate = et_cessRate.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_cessRate.getText().toString().trim())));
                double cessAmount = et_cessAmount.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_cessAmount.getText().toString().trim())));
                double additionalCessAmount = et_additionalCessAmount.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_additionalCessAmount.getText().toString().trim())));

                double dblIncentiveAmount = etIncentiveAmount.getText().toString().equals("")?0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(etIncentiveAmount.getText().toString().trim())));

                int fav = 0;
                if(ck_fav.isChecked())
                    fav =1;
                int active =0;
                if(sc_active.isChecked())
                    active =1;

                // CHECK : Same short name and barcode can exist with same UOM but different MRP
                Cursor cursor = dbItems.checkDuplicateItem(shortName, barCode, uom);
                if (cursor.moveToFirst()  && itemObj!= null) {
                    if(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id)) != itemObj.get_id() ){
                    MsgBox.Show("Duplicate Item","Same item already exist with same MRP");
                    return;
                    }
                }

              /*  if (purchaseRate > mrp) {
                    MsgBox.Show("Warning","Purchase rate can not be greater than MRP.");
                    return;
                }*/

                ItemModel itemObject = new ItemModel(shortCode, shortName, longName, barCode, uom, brandCode, deptCode, categCode,
                        retailPrice, mrp, wholeSalePrice, minimum_stock, quantity, hsn, purchaseRate, cgstRate, sgstRate, igstRate, cessRate, cessAmount, additionalCessAmount, fav,
                        supplyType,active, 0, strImageUri, discountPercent, discountAmount);

                itemObject.setIncentive_amount(dblIncentiveAmount);
                itemObject.set_id(Integer.valueOf(tv_itemid.getText().toString()));
                long insertStatus = dbItems.updateItem(itemObject);

                if (insertStatus > 0) {
                    onItemAddListener.onItemAddSuccess();
                    Log.d(TAG, shortName+" updated successfully");
                    Toast.makeText(myContext, shortName+" updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, " issue in updating "+shortName);
                    Toast.makeText(myContext, " issue in updating "+shortName, Toast.LENGTH_SHORT).show();
                }

                /*if (insertStatus > 0) {
                    ContentValues cvItems = new ContentValues();
                    cvItems.put(DatabaseHandler.KEY_ItemName, itemObject.getShortName());
                    cvItems.put(DatabaseHandler.KEY_ItemBarcode, itemObject.getBarCode());
                    cvItems.put(DatabaseHandler.KEY_PurchaseRate, itemObject.getPurchaseRate());
                    cvItems.put(DatabaseHandler.KEY_DineInPrice1, itemObject.getMrp());
                    cvItems.put(DatabaseHandler.KEY_DineInPrice2, itemObject.getRetaiPrice());
                    cvItems.put(DatabaseHandler.KEY_DineInPrice3, itemObject.getWholeSalePrice());
                    cvItems.put(DatabaseHandler.KEY_HSNCode, itemObject.getHsn());
                    cvItems.put(DatabaseHandler.KEY_UOM, itemObject.getUOM());
                    cvItems.put(DatabaseHandler.KEY_CGSTRate, itemObject.getCgstRate());
                    cvItems.put(DatabaseHandler.KEY_SGSTRate, itemObject.getSgstRate());
                    cvItems.put(DatabaseHandler.KEY_IGSTRate, itemObject.getIgstRate());
                    cvItems.put(DatabaseHandler.KEY_cessRate, itemObject.getCessRate());
                    cvItems.put(DatabaseHandler.KEY_cessAmountPerUnit, itemObject.getCessAmount());
                    cvItems.put(DatabaseHandler.KEY_additionalCessAmount, itemObject.getAdditionalCessAmount());

                    long status = dbItems.updateItemInSupplierItemLinkage(cvItems, itemObject.get_id());
                    if (status > 0)
                        Log.d("purchase_rate", "Purchase rate updated successfully.");
                }*/

                if (insertStatus > 0) {

                    long status = dbItems.updateBillItem(itemObject);
                    if (status > 0)
                        Log.d("purchase_rate", "Purchase rate updated successfully.");
                }

                /*if(insertStatus >0)
                {
                    *//****** stock update begin *********//*
                    Date date = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
                    // kindly note this will not update closing stock and averagepurchaserate
                    ItemStockClass itemStock = new ItemStockClass( itemObject.get_id(),String.valueOf(date.getTime()),shortCode ,shortName,longName,
                            quantity,quantity,quantity,0.00, active,0.00 );
                    long stockinsert = dbItems.updateItemInItemStock(itemStock);
                    if(stockinsert<1)
                    {
                        Log.d(TAG+": Issue while updating "+shortName+ " in ItemStock \n");
                    }
                    //updating itemname and shortcode for previous dates in item stock
                     stockinsert = dbItems.updateItemDetailsItemStock(itemStock);
                    *//****** stock update ends *********//*
                    onItemAddListener.onItemAddSuccess();
                    Log.d(TAG, shortName+" updated successfully");
                    Toast.makeText(myContext, shortName+" updated successfully", Toast.LENGTH_SHORT).show();
                    //clear();
                }else
                {
                    Log.d(TAG, " issue in updating "+shortName);
                    Toast.makeText(myContext, " issue in updating "+shortName, Toast.LENGTH_SHORT).show();
                }*/
            }
        }catch(Exception e)
        {
            e.printStackTrace();
//            Log.e(TAG, Log.getClassNameMethodNameAndLineNumber(),e);
            MsgBox.Show("Error","Some error occured while saving the item details");
        }
    }

    void browseImage()
    {
        FilePickerDialogFragment myFragment1 =
                (FilePickerDialogFragment)getFragmentManager().findFragmentByTag("File Picker");
        if (myFragment1 != null && myFragment1.isVisible()) {
            return;
        };
        Bundle bundle = new Bundle();
        bundle.putString("contentType","image");
        bundle.putString(FRAGMENTNAME,TAG);
        FragmentManager fm1 = getActivity().getSupportFragmentManager();
        FilePickerDialogFragment frag = new FilePickerDialogFragment();
        frag.setArguments(bundle);
        frag.mInitListener(ItemMasterEditItemDialogFragment.this);
        frag.show(fm1, "File Picker");
    }
    private int getSpinnerCode (Spinner spnr,String spinnerName)
    {
        Cursor cc = (Cursor)spnr.getSelectedItem();
        int code = -1;
        switch (spinnerName)
        {
            case "Department": code = cc.getInt(cc.getColumnIndex(DatabaseHandler.KEY_DepartmentCode));
                break;
            case "Category":code = cc.getInt(cc.getColumnIndex(DatabaseHandler.KEY_CategoryCode));
                break;
            case "Kitchen":code = cc.getInt(cc.getColumnIndex(DatabaseHandler.KEY_KitchenCode));
                break;
        }
        return code;
    }
    private String getUOMString()
    {
        String muomSelected = spnrUOM.getSelectedItem().toString();
        int length = muomSelected.length();
        String uom = muomSelected.substring(length-4,length-1);
        return uom;
    }

    private int getUOMIndex(String uom)
    {
        List<String> UOMList = Arrays.asList(getResources().getStringArray(R.array.UOM_List));
        for (String muom : UOMList)
        {
            if(muom.contains(uom))
                return UOMList.indexOf(muom);
        }
        return 0;
    }
    private void loadCategoryforDeptId(int mdeptCode)
    {

        if(mdeptCode == -1)
        {
            Cursor cursor = dbItems.getAllCategory();
            MatrixCursor extras = new MatrixCursor(new String[] { "_id","CategoryCode","CategoryName", "DepartmentCode" });
            extras.addRow(new String[] { "-1", "-1" ,"Select","-1" });
            Cursor[] cursors = { extras, cursor };
            Cursor extendedCursor = new MergeCursor(cursors);
            SimpleCursorAdapter categCAdapter =  new SimpleCursorAdapter(myContext,
                    android.R.layout.simple_spinner_item,
                    extendedCursor,
                    new String[] {"CategoryName"},
                    new int[] {android.R.id.text1}, 0);
            categCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnrCateg.setAdapter(categCAdapter);
        } else if (mdeptCode == -2) {
            MatrixCursor extras = new MatrixCursor(new String[] { "_id","CategoryCode","CategoryName", "DepartmentCode" });
            extras.addRow(new String[] { "-1", "-1" ,"Select","-1" });
            Cursor[] cursors = { extras, null };
            Cursor extendedCursor = new MergeCursor(cursors);
            SimpleCursorAdapter categCAdapter =  new SimpleCursorAdapter(myContext,
                    android.R.layout.simple_spinner_item,
                    extendedCursor,
                    new String[] {"CategoryName"},
                    new int[] {android.R.id.text1}, 0);
            categCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnrCateg.setAdapter(categCAdapter);
        }
        else
        {
            Cursor cursor = dbItems.getCategoriesForDeptId(mdeptCode);
            MatrixCursor extras = new MatrixCursor(new String[] { "_id","CategoryCode","CategoryName", "DepartmentCode" });
            extras.addRow(new String[] { "-1", "-1" ,"Select","-1" });
            Cursor[] cursors = { extras, cursor };
            Cursor extendedCursor = new MergeCursor(cursors);
            SimpleCursorAdapter categCAdapter =  new SimpleCursorAdapter(myContext,
                    android.R.layout.simple_spinner_item,
                    extendedCursor,
                    new String[] {"CategoryName"},
                    new int[] {android.R.id.text1}, 0);
            categCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnrCateg.setAdapter(categCAdapter);

             /*if(POPULATING_SCREEN ==1)
                spnrCateg.setSelection(itemObj.getCategCode());*/
        }
    }

    void applyBillSettings()
    {
        Cursor cursor = dbItems.getBillSettings();
        if(cursor!=null && cursor.moveToNext() )
        {
            if(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)) != 1 )
            {
                et_hsnCode.setEnabled(false);
                et_hsnCode.setTextColor(getResources().getColor(R.color.gray));
            }
        }
    }

    void loadSpinnerData()
    {
        Cursor deptCursor = dbItems.getAllDepartments();
        MatrixCursor extras = new MatrixCursor(new String[] { "_id", "DepartmentCode","DepartmentName" });
        extras.addRow(new String[] { "-1", "-1" ,"Select" });
        Cursor[] cursors = { extras, deptCursor };
        Cursor deptExtendedCursor = new MergeCursor(cursors);
        SimpleCursorAdapter deptCAdapter =  new SimpleCursorAdapter(myContext,
                android.R.layout.simple_spinner_item,
                deptExtendedCursor,
                new String[] {"DepartmentName"},
                new int[] {android.R.id.text1}, 0);
        deptCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrDept.setAdapter(deptCAdapter);


        Cursor kitchenCursor = dbItems.getAllKitchen();
        extras = new MatrixCursor(new String[] { "_id", "KitchenCode","KitchenName" });
        extras.addRow(new String[] { "-1", "-1" ,"Select" });
        Cursor[] cursors1 = { extras, kitchenCursor };
        Cursor kitchenExtendedCursor = new MergeCursor(cursors1);
        SimpleCursorAdapter  kitchenCAdapter =  new SimpleCursorAdapter(myContext,
                android.R.layout.simple_spinner_item,
                kitchenExtendedCursor,
                new String[] {"KitchenName"},
                new int[] {android.R.id.text1}, 0);
        kitchenCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrKitchen.setAdapter(kitchenCAdapter);
        loadCategoryforDeptId(-1);
    }

    private  void clear ()
    {
        tv_itemid.setText("-1");
        et_shortCode.setText("");
        et_ItemLongName.setText("");
        et_ItemShorName.setText("");
        et_ItemBarCode.setText("");
        et_MRP.setText("");
        et_retailPrice.setText("");
        et_wholeSalePrice.setText("");
        et_purchaseRate.setText("");
        et_quantity.setText("0");
        et_quantity.setEnabled(true);
        et_packSize.setText("");
        et_shortCode.setText("");
        et_discountPercent.setText("");
        et_discountAmount.setText("");
        et_hsnCode.setText("");
        et_expiryDate.setText("");
        et_cgstRate.setText("");
        et_sgstRate.setText("");
        et_igstRate.setText("");
        et_cessRate.setText("");
        et_cessAmount.setText("");
        et_additionalCessAmount.setText("");
        etIncentiveAmount.setText("");

        iv_itemImage.setImageResource(R.mipmap.ic_image_blank);
        ck_fav.setChecked(false);
        sc_active.setChecked(true);
        tv_imageURL.setText("");
        spnrUOM.setSelection(0);
        spnrKitchen.setSelection(0);
        spnrDept.setSelection(0);
        spnrCateg.setSelection(0);
        strImageUri = "";

    }

    private void addTextWatcher(){

        if (!et_cessRate.getText().toString().equalsIgnoreCase("0.00"))
            et_cessAmount.setEnabled(false);
        else if (et_cessRate.getText().toString().equalsIgnoreCase("0.00"))
            et_cessAmount.setEnabled(true);

        if (!et_cessAmount.getText().toString().equalsIgnoreCase("0.00"))
            et_cessRate.setEnabled(false);
        else if (et_cessAmount.getText().toString().equalsIgnoreCase("0.00"))
            et_cessRate.setEnabled(true);

        et_cessRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!et_cessRate.getText().toString().isEmpty())
                    et_cessAmount.setEnabled(false);
                else
                    et_cessAmount.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_cessAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!et_cessAmount.getText().toString().isEmpty())
                    et_cessRate.setEnabled(false);
                else
                    et_cessRate.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void close()
    {
        dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try{
            /*initaliseVariables();
            loadSpinnerData();*/
        }catch (Exception e) {
//            Log.e(TAG,Log.getClassNameMethodNameAndLineNumber(),e);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment ff = getParentFragment();
        getChildFragmentManager();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*if(! (dbItems!=null))
        {
            dbItems.CloseDatabase();
            dbItems = null;
        }*/
        //ItemMastersFragment.ll_itemMasters.setVisibility(View.VISIBLE);
        Log.d(TAG,"OnDestroy() called");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"OnResume() called");
        //ItemMastersFragment.pb.setVisibility(View.GONE);
        /*ItemMastersFragment.ll_itemMasters.setVisibility(View.VISIBLE);*/
    }

    void applyValidations()
    {
        et_ItemShorName.setFilters(new InputFilter[]{new EMOJI_FILTER(),new InputFilter.LengthFilter(20)});
        et_ItemLongName.setFilters(new InputFilter[]{new EMOJI_FILTER(),new InputFilter.LengthFilter(32)});
        et_hsnCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
    }

    public void mInitListener(OnItemListetner onItemAddListener){
        this.onItemAddListener = onItemAddListener;
    }

    @Override
    public void filePickerSuccessClickListener(String absolutePath) {
        strImageUri = absolutePath;
        Log.d("FilePicker Result", "Path + FileName:" + strImageUri);
        if(!strImageUri.equalsIgnoreCase(""))
        {
            iv_itemImage.setImageURI(null);
            iv_itemImage.setImageURI(Uri.fromFile(new File(strImageUri)));
        }
        else
        {
            iv_itemImage.setImageResource(R.mipmap.ic_image_blank);
        }
    }

    @OnTextChanged(value = {R.id.et_item_master_add_dialog_incentive_amount},callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected  void handleChangeIncentiveAmount(Editable editable) {
        try {
            String strIncentiveAmount = etIncentiveAmount.getText().toString().equals("") ? "0.00" : etIncentiveAmount.getText().toString();
            if (strIncentiveAmount.equals("") || strIncentiveAmount.equals(".")) {
                etIncentiveAmount.setText("0");
                if(strIncentiveAmount.equals(".")) {
                    etIncentiveAmount.setText("0.");
                    etIncentiveAmount.setSelection(etIncentiveAmount.getText().toString().length());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
