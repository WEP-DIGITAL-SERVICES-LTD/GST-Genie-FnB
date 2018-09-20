package com.wepindia.pos.views.InwardManagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mswipetech.wisepad.sdktest.view.ApplicationData;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.Supplier_Model;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.views.WepButton;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.Validations;
import com.wepindia.pos.views.InwardManagement.Adapters.SupplierAdapter;
import com.wepindia.pos.views.InwardManagement.Adapters.SupplierSuggestionAdapter;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.GSTINValidation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SupplierDetailsActivity extends WepBaseActivity {

    Context myContext;
    DatabaseHandler dbSupplierDetails;
    MessageDialog MsgBox;
    Toolbar toolbar;
    ListView lstSupplierDetails;
    EditText edt_supplierGSTIN,et_inw_supplierAddress,autocompletetv_supplierPhn, et_inw_supplierEmail;
    TextView tv_suppliercode;
    AutoCompleteTextView  autocompletetv_suppliername;
    WepButton btnAddSupplier,btnUpdateSupplier, btnClear,btnClose;
    ArrayList<Supplier_Model> SupplierList ;
    com.wepindia.pos.views.InwardManagement.Adapters.SupplierAdapter SupplierAdapter;
    //    ArrayList<String> labelsSupplierName;
    ArrayList<String> labelsSupplierPhone;
    ArrayList<HashMap<String, String>> autoCompleteDetails;
    private Supplier_Model supplier_model = null;

    String suppliername_clicked , suppliergstin_clicked, supplierphone_clicked;
    private final int CHECK_INTEGER_VALUE = 0;
    private final int CHECK_DOUBLE_VALUE = 1;
    private final int CHECK_STRING_VALUE = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_supplier_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String userName = ApplicationData.getUserName(this);
        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        try {

            myContext = this;
            MsgBox = new MessageDialog(myContext);
            dbSupplierDetails = new DatabaseHandler(myContext);
            dbSupplierDetails.CloseDatabase();
            dbSupplierDetails.CreateDatabase();
            dbSupplierDetails.OpenDatabase();

            InitialiseViewVariables();
            AssignClickActions();
            loadAutoCompleteSupplierName();
            InitialDisplaySettings();
            Clear();
            Display();

        } catch (Exception e) {
            e.printStackTrace();
            MsgBox.Show("Oops", "" + e.getMessage());
        }
        finally {
            com.wep.common.app.ActionBarUtils.setupToolbar(this,toolbar,getSupportActionBar(),"Supplier Details",userName," Date:"+s.toString());
        }

    }

    private void InitialDisplaySettings()
    {
        Cursor cursor_billsetting = dbSupplierDetails.getBillSetting();
        if(cursor_billsetting!=null && cursor_billsetting.moveToFirst())
        {
            if(cursor_billsetting.getInt(cursor_billsetting.getColumnIndex("GSTIN_In")) !=1)
            {
                edt_supplierGSTIN.setEnabled(false);
            }
        }
    }
    private void InitialiseViewVariables()
    {
        lstSupplierDetails = (ListView) findViewById(R.id.lstSupplierDetails);
        tv_suppliercode = (TextView) findViewById(R.id.tv_suppliercode);
        edt_supplierGSTIN = (EditText) findViewById(R.id.edt_supplierGSTIN);
        et_inw_supplierEmail = (EditText) findViewById(R.id.et_inw_supplierEmail);
        et_inw_supplierAddress = (EditText) findViewById(R.id.et_inw_supplierAddress);
        autocompletetv_supplierPhn = (EditText) findViewById(R.id.autocompletetv_supplierPhn);
        autocompletetv_suppliername = (AutoCompleteTextView) findViewById(R.id.autocompletetv_suppliername);
        btnAddSupplier = (WepButton) findViewById(R.id.btnAddSupplier);
        btnUpdateSupplier = (WepButton) findViewById(R.id.btnUpdateSupplier);
        btnClear = (WepButton) findViewById(R.id.btnClear);
        btnClose = (WepButton) findViewById(R.id.btnClose);
    }

    private  void AssignClickActions()
    {
        btnAddSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AddSupplier()) {
                    loadAutoCompleteSupplierName();
                    Clear();
                    Display();
                    hideKeyboard();
                }
            }
        });
        btnUpdateSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UpdateSupplier()){
                    loadAutoCompleteSupplierName();
                    Clear();
                    Display();
                    hideKeyboard();
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
                Display();
                hideKeyboard();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                Close();
            }
        });
        lstSupplierDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListClickEvent(SupplierAdapter.getItem(position));
            }});

        /*autocompletetv_suppliername.setOnTouchListener(new View.OnTouchListener(){
            //@Override
            public boolean onTouch(View v, MotionEvent event){
                autocompletetv_suppliername.showDropDown();
                return false;
            }
        });*/
        autocompletetv_suppliername.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> data = autoCompleteDetails.get(position);

                //String suppliername_str = autocompletetv_suppliername.getText().toString().toUpperCase(); // TODO: changed here
                //autocompletetv_suppliername.setText(data.get("name"));
                String suppliername_str = data.get("name");
                String supplierphone_str = data.get("phone");
                autocompletetv_suppliername.setText(data.get("name"));
                Cursor supplierdetail_cursor = dbSupplierDetails.getSupplierDetailsByPhone(supplierphone_str); // TODO: changed here
                int suppliercode = -1;
                String SupplierPhone= "", SupplierAddress = "";
                if (supplierdetail_cursor!=null && supplierdetail_cursor.moveToFirst())
                {
                    SupplierPhone = supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierPhone"));
                    SupplierAddress = supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("SupplierAddress"));
                    String supp_gstin  = supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex("GSTIN"));
                    String supp_email  = supplierdetail_cursor.getString(supplierdetail_cursor.getColumnIndex(DatabaseHandler.KEY_SupplierEmail));
                    autocompletetv_supplierPhn.setText(SupplierPhone);
                    et_inw_supplierAddress.setText(SupplierAddress);
                    et_inw_supplierEmail.setText(supp_email);
                    if(supp_gstin!=null )
                        edt_supplierGSTIN.setText(supp_gstin);
                    else
                        supp_gstin= "";

                    suppliercode = supplierdetail_cursor.getInt(supplierdetail_cursor.getColumnIndex("SupplierCode"));
                    tv_suppliercode.setText(String.valueOf(suppliercode));
                    suppliergstin_clicked = supp_gstin;
                    suppliername_clicked = suppliername_str;
                    btnAddSupplier.setEnabled(false);
                    btnUpdateSupplier.setEnabled(true);
                    hideKeyboard();
                }
            }
        });
    }

    private void ListClickEvent(Supplier_Model supplier)
    {
        btnUpdateSupplier.setEnabled(true);
        btnAddSupplier.setEnabled(false);

        tv_suppliercode.setText(String.valueOf(supplier.getSupplierCode()));

        autocompletetv_suppliername.setFocusable(false);
        autocompletetv_suppliername.setFocusableInTouchMode(false);
        autocompletetv_suppliername.setText(supplier.getSupplierName());
        autocompletetv_suppliername.setFocusable(true);
        autocompletetv_suppliername.setFocusableInTouchMode(true);

        autocompletetv_supplierPhn.setText(supplier.getSupplierPhone());
        edt_supplierGSTIN.setText(supplier.getSupplierGSTIN());
        et_inw_supplierAddress.setText(supplier.getSupplierAddress());
        et_inw_supplierEmail.setText(supplier.getSupplierEmail());

        suppliername_clicked = supplier.getSupplierName();
        supplierphone_clicked = supplier.getSupplierPhone();
        suppliergstin_clicked = supplier.getSupplierGSTIN();
    }


    private  boolean UpdateSupplier() {
        long l =0;
        Cursor cursor = dbSupplierDetails.getAllSupplierName_nonGST();
//        labelsSupplierName = new ArrayList<String>();
        labelsSupplierPhone = new ArrayList<String>();
        ArrayList<String> labelsSupplierGSTIN = new ArrayList<String>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
//                labelsSupplierName.add(cursor.getString(cursor.getColumnIndex("SupplierName")));// adding
                labelsSupplierPhone.add(cursor.getString(cursor.getColumnIndex("SupplierPhone")));// adding
                String gstin = cursor.getString(cursor.getColumnIndex("GSTIN"));
                if (gstin != null && !gstin.equals(""))
                    labelsSupplierGSTIN.add(gstin);
            } while (cursor.moveToNext());
        }

        MsgBox.setTitle("Incomplete Information")
                .setPositiveButton("Ok", null);

        String supplierType_str = "UnRegistered";
        String suppliername_str = autocompletetv_suppliername.getText().toString().toUpperCase();
        String supplierphn_str = autocompletetv_supplierPhn.getText().toString();
        String supplieraddress_str = et_inw_supplierAddress.getText().toString();
        String supplierEmail = et_inw_supplierEmail.getText().toString().trim();
        String suppliergstin_str = edt_supplierGSTIN.getText().toString().trim().toUpperCase();
        if (suppliergstin_str != null && !suppliergstin_str.equals(""))
            supplierType_str = "Registered";
        else
            suppliergstin_str = "";

        if (labelsSupplierGSTIN.contains(suppliergstin_str) && !suppliergstin_str.equalsIgnoreCase(suppliergstin_clicked) ) {
            MsgBox.Show("Warning", "Supplier with gstin already present in list");
            return false;
        }

        for (String phone : labelsSupplierPhone) {
            if (supplierphn_str.equalsIgnoreCase(phone) && !supplierphn_str.equalsIgnoreCase(supplierphone_clicked)) {
                MsgBox.Show("Warning", "Supplier with phone already present in list");
                return false;
            }
        }


        boolean mFlag = false;
        try {
            if(suppliergstin_str.trim().length() == 0)
            {mFlag = true;}
            else if(GSTINValidation.checkGSTINValidation(suppliergstin_str))
            {
                if(!GSTINValidation.checkValidStateCode(suppliergstin_str,this))
                {
                    MsgBox.Show("Invalid Information","Please Enter Valid StateCode for GSTIN");
                    return false;
                }
                mFlag = true;
            }
            else
            {
                mFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFlag = false;
        }

        if (suppliername_str.equals("") || supplieraddress_str.equals("") || supplierphn_str.equals("")) {
            MsgBox.Show("Incomplete Details","Please fill all mandatory details of Supplier");
            return false;

        } else if (!mFlag)
        {
            MsgBox.Show("Invalid Information","Please fill valid gstin");
            return false;
        }else if(supplierphn_str.length()!=10){
            MsgBox.Show("Invalid Information","Phone no. cannot be less than 10 digits");
            return false;
        } else if(!Validations.isValidEmailAddress(supplierEmail)){
            MsgBox.Show("Invalid Information","Please enter an valid email address.");
            return false;
        } else{

            supplier_model = new Supplier_Model();

            supplier_model.setSupplierType(supplierType_str);
            supplier_model.setSupplierGSTIN(suppliergstin_str);
            supplier_model.setSupplierName(suppliername_str);
            supplier_model.setSupplierPhone(supplierphn_str);
            supplier_model.setSupplierAddress(supplieraddress_str);
            supplier_model.setSupplierEmail(supplierEmail);
            supplier_model.setSupplierCode(Integer.parseInt(tv_suppliercode.getText().toString()));

            l = dbSupplierDetails.updateSupplierDetails(supplier_model);
            if (l > 0) {
                Log.d("Inward_Supplier Detail", " Supplier details updated at " + l);
                Toast.makeText(myContext, "Supplier details saved at " + l, Toast.LENGTH_SHORT).show();

            }
        }
        return true;
    }

    public static int checkDataypeValue(String value, String type) {
        int flag =0;
        try {
            switch(type) {
                case "Int":
                    Integer.parseInt(value);
                    flag = 0;
                    break;
                case "Double" : Double.parseDouble(value);
                    flag = 1;
                    break;
                default : flag =2;
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            flag = -1;
        }
        return flag;
    }


    private  boolean AddSupplier() {
        long l = 0;
        Cursor cursor = dbSupplierDetails.getAllSupplierName_nonGST();
//        labelsSupplierName = new ArrayList<String>();
        labelsSupplierPhone = new ArrayList<String>();
        ArrayList<String> labelsSupplierGSTIN = new ArrayList<String>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
//                labelsSupplierName.add(cursor.getString(cursor.getColumnIndex("SupplierName")));// adding
                labelsSupplierPhone.add(cursor.getString(cursor.getColumnIndex("SupplierPhone")));// adding TODO: changed here
                String gstin = cursor.getString(cursor.getColumnIndex("GSTIN"));
                if (gstin != null && !gstin.equals(""))
                    labelsSupplierGSTIN.add(gstin);
            } while (cursor.moveToNext());
        }

        MsgBox.setTitle("Incomplete Information")
                .setPositiveButton("Ok", null);

        String supplierType_str = "UnRegistered";
        String suppliername_str = autocompletetv_suppliername.getText().toString().toUpperCase();
        String supplierphn_str = autocompletetv_supplierPhn.getText().toString();
        String supplieraddress_str = et_inw_supplierAddress.getText().toString();
        String supplierEmail = et_inw_supplierEmail.getText().toString().trim();
        String suppliergstin_str = edt_supplierGSTIN.getText().toString().trim().toUpperCase();
        if (suppliergstin_str != null && !suppliergstin_str.equals(""))
            supplierType_str = "Registered";
        else
            suppliergstin_str = "";

        if (labelsSupplierGSTIN.contains(suppliergstin_str)) {
            MsgBox.Show("Warning","Supplier with gstin already present in list") ;
            return false;
        }

        boolean mFlag = false;
        try
        {
            if(suppliergstin_str.trim().length() == 0)
            {mFlag = true;}
            else if(GSTINValidation.checkGSTINValidation(suppliergstin_str))
            {
                if(!GSTINValidation.checkValidStateCode(suppliergstin_str,this))
                {
                    MsgBox.Show("Invalid Information","Please Enter Valid StateCode for GSTIN");
                    return false;
                }
                mFlag = true;
            }
            else
            {
                mFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFlag = false;
        }
        for (String phone : labelsSupplierPhone) {
            if (supplierphn_str.equalsIgnoreCase(phone)) {
                MsgBox.Show("Warning","Supplier with phone already present in list");
                return false;
            }
        }

        if (suppliername_str.equals("") || supplieraddress_str.equals("") || supplierphn_str.equals("")) {
            MsgBox.Show("Insufficient Information","Please fill all details of Supplier");
            return false;
        } else if (!mFlag)
        {
            MsgBox.Show("Insufficient Information","Please fill valid gstin");
            return false;
        } else if(supplierphn_str.length()!=10){
            MsgBox.Show("Invalid Information","Phone no. cannot be less than 10 digits");
            return false;
        }  else if(!Validations.isValidEmailAddress(supplierEmail)){
            MsgBox.Show("Invalid Information","Please enter an valid email address.");
            return false;
        } else {

            supplier_model = new Supplier_Model();

            supplier_model.setSupplierType(supplierType_str);
            supplier_model.setSupplierGSTIN(suppliergstin_str);
            supplier_model.setSupplierName(suppliername_str);
            supplier_model.setSupplierPhone(supplierphn_str);
            supplier_model.setSupplierAddress(supplieraddress_str);
            supplier_model.setSupplierEmail(supplierEmail);

            l = dbSupplierDetails.saveSupplierDetails(supplier_model);
            if (l > 0) {
                Log.d("Inward_Item_Entry", " Supplier details saved at " + l);
                Toast.makeText(myContext, "Supplier details saved at " + l, Toast.LENGTH_SHORT).show();

            }
        }
        return true;
    }

    public void loadAutoCompleteSupplierName()
    {
        try {

            Cursor cursor = dbSupplierDetails.getAllSupplierName_nonGST();
//            labelsSupplierName = new ArrayList<String>();
            autoCompleteDetails = new ArrayList<HashMap<String, String>>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
//                    labelsSupplierName.add(cursor.getString(cursor.getColumnIndex("SupplierName")));// adding // TODO: changed here
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("name", cursor.getString(cursor.getColumnIndex("SupplierName")));
                    data.put("phone", cursor.getString(cursor.getColumnIndex("SupplierPhone")));
                    autoCompleteDetails.add(data);

                } while (cursor.moveToNext());
            }

            String[] fields = {"name", "phone"};
            int[] res = {R.id.adapterName, R.id.adapterPhone};

            //SimpleAdapter dataAdapter = new SimpleAdapter(myContext, autoCompleteDetails, R.layout.row_supplier_name_phone_list, fields, res);
            SupplierSuggestionAdapter dataAdapter = new SupplierSuggestionAdapter(myContext, R.layout.adapter_supplier_name, autoCompleteDetails);
            autocompletetv_suppliername.setAdapter(dataAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            MsgBox.Show("Error",e.getMessage());
        }
    }
    public void Display()
    {
        if(SupplierList == null)
            SupplierList = new ArrayList<>();

        Cursor supplierCursor = dbSupplierDetails.getAllSupplierName_nonGST();
        while(supplierCursor!=null && supplierCursor.moveToNext())
        {
            int suppliercode = supplierCursor.getInt(supplierCursor.getColumnIndex("SupplierCode"));
            String suppliergstin = supplierCursor.getString(supplierCursor.getColumnIndex("GSTIN"));
            String suppliername = supplierCursor.getString(supplierCursor.getColumnIndex("SupplierName"));
            String suppliernphone = supplierCursor.getString(supplierCursor.getColumnIndex("SupplierPhone"));
            String supplieraddress = supplierCursor.getString(supplierCursor.getColumnIndex("SupplierAddress"));
            String supplierEmail = supplierCursor.getString(supplierCursor.getColumnIndex(DatabaseHandler.KEY_SupplierEmail));
            Supplier_Model supplier_model = new Supplier_Model();

            supplier_model.setSupplierGSTIN(suppliergstin);
            supplier_model.setSupplierName(suppliername);
            supplier_model.setSupplierPhone(suppliernphone);
            supplier_model.setSupplierAddress(supplieraddress);
            supplier_model.setSupplierEmail(supplierEmail);
            supplier_model.setSupplierCode(suppliercode);

            SupplierList.add(supplier_model);
        } // end of while
        if (SupplierList.size() >0)
        {
            if(SupplierAdapter == null)
            {
                SupplierAdapter = new SupplierAdapter(this,SupplierList,dbSupplierDetails , this.getClass().getName());
                lstSupplierDetails.setAdapter(SupplierAdapter);
            } else {
                SupplierAdapter.notifyNewDataAdded(SupplierList);
            }
        }

    }

    public void Clear()
    {
        suppliername_clicked="";
        suppliergstin_clicked="";
        btnAddSupplier.setEnabled(true);
        btnUpdateSupplier.setEnabled(false);
        tv_suppliercode.setText("-1");
        edt_supplierGSTIN.setText("");
        et_inw_supplierEmail.setText("");
        autocompletetv_suppliername.setText("");
        autocompletetv_supplierPhn.setText("");
        et_inw_supplierAddress.setText("");
        SupplierList = null;
        SupplierAdapter = null;
        lstSupplierDetails.setAdapter(SupplierAdapter);
    }

    public void Close() {
        dbSupplierDetails.CloseDatabase();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);
            AuthorizationDialog
                    .setTitle("Are you sure you want to exit ?")
                    .setIcon(R.drawable.ic_launcher)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dbSupplierDetails.CloseDatabase();
                            finish();
                        }
                    })
                    .show();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);    }
}
