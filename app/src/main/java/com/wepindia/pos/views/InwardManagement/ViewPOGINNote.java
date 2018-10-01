package com.wepindia.pos.views.InwardManagement;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.models.PurchaseOrderBean;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.views.InwardManagement.Adapters.ViewPOGINAdapter;
import com.wepindia.pos.views.InwardManagement.Listeners.OnViewPOGINListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewPOGINNote extends DialogFragment {

    @BindView(R.id.btnViewPOGINRePrint)
    Button btnRePrint;
    @BindView(R.id.btnViewPOGINShare)
    Button btnShare;
    @BindView(R.id.btnViewPOGINClose)
    Button btnClose;

    @BindView(R.id.et_ViewPOGIN_supplier_name)
    EditText et_ViewPOGIN_supplier_name;
    @BindView(R.id.et_ViewPOGIN_supplier_phone)
    EditText et_ViewPOGIN_supplier_phone;
    @BindView(R.id.et_ViewPOGIN_supplier_gstin)
    EditText et_ViewPOGIN_supplier_gstin;
    @BindView(R.id.et_ViewPOGIN_supplier_address)
    EditText et_ViewPOGIN_supplier_address;
    @BindView(R.id.et_ViewPOGIN_po)
    EditText et_ViewPOGIN_po;
    @BindView(R.id.et_ViewPOGIN_invoice_no)
    EditText et_ViewPOGIN_invoice_no;
    @BindView(R.id.et_ViewPOGIN_invoice_date)
    EditText et_ViewPOGIN_invoice_date;
    @BindView(R.id.et_ViewPOGIN_additional_charge_name)
    EditText et_ViewPOGIN_additional_charge_name;
    @BindView(R.id.et_ViewPOGIN_additional_amount)
    EditText et_ViewPOGIN_additional_amount;
    @BindView(R.id.et_ViewPOGIN_sub_total)
    EditText et_ViewPOGIN_sub_total;
    @BindView(R.id.et_ViewPOGIN_grand_total)
    EditText et_ViewPOGIN_grand_total;

    @BindView(R.id.sp_ViewPOGIN_state_code)
    Spinner spStateCode;
    @BindView(R.id.cb_ViewPOGIN_state_code)
    CheckBox cbStateCode;
    @BindView(R.id.cb_ViewPOGIN_additional_charge)
    CheckBox cbAdditionalCharge;

    @BindView(R.id.rv_view_po_gin_item_list)
    ListView listView;

    private PurchaseOrderBean model;
    private Context myContext;
    private MessageDialog MsgBox;
    private String[] arrayPOS;
    private List<PurchaseOrderBean> purchaseOrderBeanList = null;
    private ViewPOGINAdapter purchaseOrderAdapter = null;
    private OnViewPOGINListener onViewPOGINListener;
    private DatabaseHandler dbHandler;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = (PurchaseOrderBean) getArguments().getSerializable("PurchaseOrderBean");
        View fragmentView = inflater.inflate(R.layout.activity_view_pogin, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this,fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialiseVariables();
        loadSpinnerData();
        populateScreen();
    }

    @OnClick({R.id.btnViewPOGINRePrint, R.id.btnViewPOGINShare,
            R.id.btnViewPOGINClose})
    protected void mBtnClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnViewPOGINClose : close();
                break;
            case R.id.btnViewPOGINShare :
                if (onViewPOGINListener != null)
                    onViewPOGINListener.onPurchaseOrderShareClicked(model);
                break;
            case R.id.btnViewPOGINRePrint :
                if (onViewPOGINListener != null)
                    onViewPOGINListener.onPurchaseOrderPrintClicked(model);
                break;
        }
    }

    public void setOnViewPOGINListener(OnViewPOGINListener onViewPOGINListener) {
        this.onViewPOGINListener = onViewPOGINListener;
    }

    void initialiseVariables()
    {
        myContext = getActivity();
        MsgBox = new MessageDialog(myContext);
        spStateCode.setEnabled(false);
        spStateCode.setClickable(false);
        purchaseOrderBeanList = new ArrayList<>();

        dbHandler = new DatabaseHandler(myContext);
        dbHandler.CreateDatabase();
        dbHandler.OpenDatabase();
    }

    void loadSpinnerData() {
        arrayPOS = getResources().getStringArray(R.array.poscode);
        //Creating the ArrayAdapter instance having the POSCode list
        ArrayAdapter adapterPOS = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, arrayPOS);
        adapterPOS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spStateCode.setAdapter(adapterPOS);
    }

    void populateScreen()
    {
        if (model != null) {

            if (purchaseOrderBeanList.size() > 0)
                purchaseOrderBeanList.clear();
            if (model.getStrInvoiceNo().equalsIgnoreCase("-"))
                mFetchDataOnPurchaseOrderNo(""+model.getiPurchaseOrderNo(), model.getStrSupplierId(), null, model.getStrPurchaseOrderDate());
            else
                mFetchDataOnPurchaseOrderNo(""+model.getiPurchaseOrderNo(), model.getStrSupplierId(), model.getStrInvoiceNo(), model.getStrPurchaseOrderDate());

            et_ViewPOGIN_supplier_name.setText(model.getStrSupplierName());
            et_ViewPOGIN_supplier_phone.setText(model.getStrSupplierPhone());
            et_ViewPOGIN_supplier_gstin.setText(model.getStrSupplierGSTIN());
            et_ViewPOGIN_supplier_address.setText(model.getStrSupplierAddress());
            if(arrayPOS != null && arrayPOS.length > 0) {
                if (!model.getStrSupplierPOS().isEmpty() && !isPosSame((model.getStrSupplierPOS()))) {
                    cbStateCode.setChecked(true);
                    for (int i = 0; i <arrayPOS.length; i++) {
                        String strStateCode = arrayPOS[i];
                        int l = strStateCode.length();
                        String strState_cd = strStateCode.substring(l-2,l);
                        if(strState_cd.equals(model.getStrSupplierPOS())) {
                            spStateCode.setSelection(i);
                            break;
                        }
                    }
                }
            }
            if (model.getiPurchaseOrderNo() == -1)
                et_ViewPOGIN_po.setText("NA");
            else
                et_ViewPOGIN_po.setText(""+model.getiPurchaseOrderNo());

            et_ViewPOGIN_invoice_no.setText(model.getStrInvoiceNo());
            et_ViewPOGIN_invoice_date.setText(model.getStrInvoiceDate());
            if (model.getDblAdditionalChargeAmount() > 0)
                cbAdditionalCharge.setChecked(true);
            else
                cbAdditionalCharge.setChecked(false);
            et_ViewPOGIN_additional_charge_name.setText(model.getStrAdditionalCharge());
            et_ViewPOGIN_additional_amount.setText(String.format("%.2f",model.getDblAdditionalChargeAmount()));
            et_ViewPOGIN_sub_total.setText(String.format("%.2f",mCalculateSubTotal()));
            et_ViewPOGIN_grand_total.setText(String.format("%.2f",mCalculateGrandTotal(model.getDblAdditionalChargeAmount())));
        }
    }

    private void mFetchDataOnPurchaseOrderNo(String strPurchaseOrderNo, String supplierId, String invoiceNo, String invoiceDate){
        Cursor cursorPO = null;
        try {
            if (!invoiceDate.equals("") && !invoiceDate.equalsIgnoreCase("-")) {
                Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(invoiceDate);
                long milliseconds = date.getTime();
                invoiceDate = String.valueOf(milliseconds);
            } else {
                invoiceDate = "";
                invoiceNo = "";
            }
            cursorPO = dbHandler.mGetPurchaseOrderData(strPurchaseOrderNo, supplierId, invoiceNo, invoiceDate);
            if (cursorPO != null){
                cursorPO.moveToFirst();
                do{
                    PurchaseOrderBean purchaseOrderBean = new PurchaseOrderBean();
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

                    purchaseOrderBean.setDblCGSTRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
                    purchaseOrderBean.setDblCGSTAmount(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_CGSTAmount)));

                    purchaseOrderBean.setDblSGSTRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
                    purchaseOrderBean.setDblSGSTAmount(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_SGSTAmount)));

                    purchaseOrderBean.setDblIGSTRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
                    purchaseOrderBean.setDblIGSTAmount(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_IGSTAmount)));

                    purchaseOrderBean.setDblCessRate(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_cessRate)));
                    purchaseOrderBean.setDblCessAmount(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_cessAmount)));

                    purchaseOrderBean.setDblCessAmountPerUnit(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_cessAmountPerUnit)));
                    purchaseOrderBean.setDblAdditionalCessAmount(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));

                    purchaseOrderBean.setDblQuantity(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_Quantity)));
                    purchaseOrderBean.setDblAmount(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_Amount)));

                    purchaseOrderBean.setDblAdditionalChargeAmount(cursorPO.getDouble(cursorPO.getColumnIndex(DatabaseHandler.KEY_AdditionalChargeAmount)));

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
                    purchaseOrderBeanList.add(purchaseOrderBean);
                }while(cursorPO.moveToNext());
                if (purchaseOrderAdapter == null) {
                    purchaseOrderAdapter = new ViewPOGINAdapter(getActivity(), purchaseOrderBeanList);
                    listView.setAdapter(purchaseOrderAdapter);
                } else {
                    purchaseOrderAdapter.notifyDataSetChanged(purchaseOrderBeanList);
                }
            }
        }catch (Exception ex){
            Log.i("View_PO_GIN","Unable to fetch data from purchase order table based on the purchase order no.");
        } finally {
            if(cursorPO != null){
                cursorPO.close();
            }
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

    private boolean isPosSame(String POS) {
        boolean isSame = false;

        String ownerPOS = dbHandler.getOwnerPOS();
        if (ownerPOS.equalsIgnoreCase(POS))
            isSame = true;

        return isSame;
    }

    private void close()
    {
        dismiss();
    }

}
