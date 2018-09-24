package com.wepindia.pos.views.Masters.OutwardItemMaster;

import android.app.Dialog;
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
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;


public class ItemMasterAddItemDialogFragment extends DialogFragment implements Constants, OnFilePickerClickListener {

    private static final String TAG = ItemMasterAddItemDialogFragment.class.getSimpleName();
    private static long back_pressed;
    private static final String TextInputEditText = "android.support.design.widget.TextInputEditText";

    Context myContext ;
    MessageDialog MsgBox ;
    DatabaseHandler dbHandler;
    OnItemListetner onItemAddListener;
    OnFilePickerClickListener filePickerClickListener;
    String strImageUri = "";
    String businessDate="";

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
    @BindView(R.id.bt_item_master_add_dialog_save) Button btn_saveItem;
    @BindView(R.id.bt_item_master_add_dialog_clear) Button btn_clear;
    @BindView(R.id.bt_item_master_add_dialog_close) Button btn_close;

    @BindView(R.id.et_item_master_add_dialog_incentive_amount) TextInputEditText etIncentiveAmount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        myContext = getActivity();
        MsgBox = new MessageDialog(myContext);
        dbHandler = new DatabaseHandler(myContext);
        dbHandler.CreateDatabase();
        dbHandler.OpenDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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
            initialiseVariables();
            loadSpinnerData();
            applyDecimalValidation();
            applyValidations();
            applyBillSettings();
//            addTextWatcher();
        }catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
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
    void applyDecimalValidation()
    {
        try {
          /*  et_retailPrice.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
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
        Cursor businessDate_cursor = dbHandler.getBusinessDate();
        if(businessDate_cursor!=null && businessDate_cursor.moveToNext())
        {
            businessDate = businessDate_cursor.getString(businessDate_cursor.getColumnIndex(DatabaseHandler.KEY_BusinessDate));
        }else
        {
            Date date = new Date();
            businessDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
        }
    }


    @OnClick({R.id.bt_item_master_add_dialog_close, R.id.bt_item_master_add_dialog_clear,R.id.bt_item_master_add_dialog_save,
            R.id.bt_item_master_add_dialog_browse_image})
    protected void mBtnClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bt_item_master_add_dialog_close : close();
                break;
            case R.id.bt_item_master_add_dialog_clear : clear();
                break;
            case R.id.bt_item_master_add_dialog_save : saveItem();
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

    @OnItemSelected(R.id.sp_item_master_add_dialog_department)
    public void onItemSelected(AdapterView<?> parent, View view, int position)
    {
        Cursor mdeptCursor = (Cursor)spnrDept.getItemAtPosition(position);
        int mdeptCode = mdeptCursor.getInt(mdeptCursor.getColumnIndex("DepartmentCode"));
        loadCategoryforDeptId(mdeptCode);
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
      /*  if(et_retailPrice.getText().toString().equals(""))
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

    private void saveItem()
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
                int kitchenCode  = getSpinnerCode(spnrKitchen, "Kitchen");
                int deptCode = getSpinnerCode(spnrDept, "Department");
                int categCode = getSpinnerCode(spnrCateg, "Category");
                String supplyType = spnrSupplyType.getSelectedItem().toString();


                double retailPrice = Double.parseDouble(String.format("%.2f",Double.parseDouble(et_retailPrice.getText().toString().trim())));
                double mrp = et_MRP.getText().toString().equals("")? retailPrice: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_MRP.getText().toString().trim())));
                double wholeSalePrice = et_wholeSalePrice.getText().toString().equals("")? retailPrice: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_wholeSalePrice.getText().toString().trim())));
                double purchaseRate = et_purchaseRate.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_purchaseRate.getText().toString().trim())));

                double minimum_stock = et_low_stock.getText().toString().equals("")? 0: Double.parseDouble(et_low_stock.getText().toString().trim());
                double quantity = et_quantity.getText().toString().equals("")? 0.00: Double.parseDouble(String.format("%.2f",Double.parseDouble(et_quantity.getText().toString().trim())));
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
                Cursor cursor = dbHandler.checkDuplicateItem(shortName, barCode, uom);
                if (cursor.moveToFirst()) {
                    MsgBox.Show("Duplicate Item","Same item already exist with same MRP");
                    return;
                }

                /*if (purchaseRate > mrp) {
                    MsgBox.Show("Warning","Purchase rate can not be greater than MRP.");
                    return;
                }*/

                ItemModel itemObject = new ItemModel(shortCode,shortName,longName,barCode,uom,kitchenCode,deptCode, categCode,
                        retailPrice, mrp, wholeSalePrice, minimum_stock, quantity, hsn, purchaseRate,cgstRate,sgstRate,igstRate,cessRate,cessAmount,additionalCessAmount,fav,
                        supplyType,active, 0,strImageUri,discountPercent,discountAmount);

                itemObject.setIncentive_amount(dblIncentiveAmount);

                long insertStatus = dbHandler.insertItem(itemObject);
                if(insertStatus >0)
                {
                    /****** stock update begin *********/
                    Date date = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
                    /*ItemStockClass itemStock = new ItemStockClass( insertStatus,String.valueOf(date.getTime()),shortCode ,shortName,longName,
                            quantity,quantity,0.00,0.00,active,0.00 );
                    long stockinsert = dbHandler.insertNewItemInItemStock(itemStock);
                    if(stockinsert<1)
                    {
                        Log.d(TAG, ": Issue while inserting "+shortName+ " in ItemStock \n");
                    }*/
                    /****** stock update ends *********/
                    onItemAddListener.onItemAddSuccess();
                    Log.d(TAG, shortName+" inserted successfully");
                    Toast.makeText(myContext, shortName+" inserted successfully", Toast.LENGTH_SHORT).show();
                    clear();
                }else
                {
                    Log.d(TAG, " issue in inserting "+shortName);
                    Toast.makeText(myContext, " issue in inserting "+shortName, Toast.LENGTH_SHORT).show();
                }
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
        frag.mInitListener(ItemMasterAddItemDialogFragment.this);
        frag.show(fm1, "File Picker");
    }
    private int getSpinnerCode (Spinner spnr, String spinnerName)
    {
        Cursor cc = (Cursor)spnr.getSelectedItem();
        cc.moveToPosition(spnr.getSelectedItemPosition());
        int code = -1;
        switch (spinnerName)
        {
            case "Department": code = cc.getInt(cc.getColumnIndex("DepartmentCode"));
                break;
            case "Category":code = cc.getInt(cc.getColumnIndex("CategoryCode"));
                break;
            case "Kitchen":code = cc.getInt(cc.getColumnIndex("KitchenCode"));
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
    private void loadCategoryforDeptId(int mdeptCode)
    {
        /*ArrayList<String> categList = new ArrayList<>();
        categList.add("Select");
        Cursor cursor = dbHandler.getCategoriesForDeptId(mdeptCode);
        while (cursor!=null && cursor.moveToNext())
        {
            String mCategName = cursor.getString(cursor.getColumnIndex("CategoryName"));
            categList.add(mCategName);
        }

        ArrayAdapter<String> categAdapter = new ArrayAdapter<String>(myContext,android.R.layout.simple_spinner_item, categList);
        categAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
        if(mdeptCode == -1)
        {
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
            Cursor cursor = dbHandler.getCategoriesForDeptId(mdeptCode);
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
        }
    }
    void applyBillSettings()
    {
        Cursor cursor = dbHandler.getBillSettings();
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

        //Department
        /*ArrayList deptList = new ArrayList();
        deptList.add("Select");*/
        //cursor.moveToFirst();
        /*while(cursor!=null && cursor.moveToNext())
        {
            String deptName = cursor.getString(cursor.getColumnIndex("DepartmentName"));
            deptList.add(deptName);
        }
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(myContext,android.R.layout.simple_spinner_item, deptList);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
        Cursor deptCursor = dbHandler.getAllDepartments();
        MatrixCursor extras = new MatrixCursor(new String[] { "_id", "DepartmentCode","DepartmentName" });
        extras.addRow(new String[] { "-1", "-1" ,"Select" });
        Cursor[] cursors = { extras, deptCursor };
        Cursor extendedCursor = new MergeCursor(cursors);
        SimpleCursorAdapter deptCAdapter =  new SimpleCursorAdapter(myContext,
                android.R.layout.simple_spinner_item,
                extendedCursor,
                new String[] {"DepartmentName"},
                new int[] {android.R.id.text1}, 0);
        deptCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrDept.setAdapter(deptCAdapter);


        Cursor kitchenCursor = dbHandler.getAllKitchen();
        extras = new MatrixCursor(new String[] { "_id", "KitchenCode","KitchenName" });
        extras.addRow(new String[] { "-1", "-1" ,"Select" });
        Cursor[] cursors1 = { extras, kitchenCursor };
        Cursor kitchenExtendedCursor = new MergeCursor(cursors1);
        SimpleCursorAdapter  brandCAdapter =  new SimpleCursorAdapter(myContext,
                android.R.layout.simple_spinner_item,
                kitchenExtendedCursor,
                new String[] {"KitchenName"},
                new int[] {android.R.id.text1}, 0);
        brandCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrKitchen.setAdapter(brandCAdapter);
        loadCategoryforDeptId(-1);
    }

    private  void clear ()
    {
        et_shortCode.setText("");
        et_ItemLongName.setText("");
        et_ItemShorName.setText("");
        et_ItemBarCode.setText("");
        et_MRP.setText("");
        et_retailPrice.setText("");
        et_wholeSalePrice.setText("");
        et_purchaseRate.setText("");
        et_quantity.setText("0");
        et_low_stock.setText("0");
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

        strImageUri = "";
        iv_itemImage.setImageResource(R.mipmap.ic_image_blank);

    }

    private void addTextWatcher(){
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
        /*if(! (dbHandler!=null))
        {
            dbHandler.CloseDatabase();
            dbHandler = null;
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
