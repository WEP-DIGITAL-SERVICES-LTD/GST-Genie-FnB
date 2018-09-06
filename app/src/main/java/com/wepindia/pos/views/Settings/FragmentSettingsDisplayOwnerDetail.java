package com.wepindia.pos.views.Settings;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;

import com.wepindia.pos.R;
import com.wepindia.pos.utils.GSTINValidation;

public class FragmentSettingsDisplayOwnerDetail extends Fragment {

    private TextView Name,Phone,Email,Address, POS, IsMainOffice;
    private EditText mId, appId, BillNoPrefix,RefernceNo,Gstin;
    private DatabaseHandler dbHelper;
    Spinner spinner1, spinner2;
    Context myContext;
    Button btnClose, btnApply;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_settings_display_owner_detail, container, false);
        myContext = getActivity();
        initialseViewVariablesAndDisplay(view);
        TextChangeListener();
        return view;
    }

    private void initialseViewVariablesAndDisplay(View view)
    {

        dbHelper = new DatabaseHandler(myContext);
        btnClose=(Button)view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close(v);
            }
        });
        btnApply=(Button)view.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply(v);
            }
        });
        Name=(TextView)view.findViewById(R.id.ownerName);
        Gstin=(EditText) view.findViewById(R.id.ownerGstin);
        RefernceNo=(EditText) view.findViewById(R.id.ownerReferenceNo);
        Phone=(TextView)view.findViewById(R.id.ownerContact);
        Email=(TextView)view.findViewById(R.id.ownerEmail);
        POS=(TextView) view.findViewById(R.id.ownerPos);
        IsMainOffice=(TextView) view.findViewById(R.id.ownerOffice);
        /*spinner1=(Spinner)view.findViewById(R.id.ownerPos);
        spinner2=(Spinner)view.findViewById(R.id.ownerOffice);
        spinner2.setSelection(1);*/
        Address=(TextView)view.findViewById(R.id.ownerAddress);
        mId =(EditText) view.findViewById(R.id.OwnerDetail_mId);
        appId =(EditText) view.findViewById(R.id.OwnerDetail_appId);
        BillNoPrefix =(EditText) view.findViewById(R.id.ownerBillPrefix);

        loadOwnerDetail();
    }
    private void loadOwnerDetail()
    {
        dbHelper.CreateDatabase();
        dbHelper.OpenDatabase();
        Cursor cursor = dbHelper.getOwnerDetail();
        if(cursor!=null && cursor.moveToFirst())
        {
            String name = cursor.getString(cursor.getColumnIndex("OwnerName"));
            String gstin = cursor.getString(cursor.getColumnIndex("GSTIN"));
            String refernceNo = cursor.getString(cursor.getColumnIndex("refNo"));
            String phone = cursor.getString(cursor.getColumnIndex("Phone"));
            String email = cursor.getString(cursor.getColumnIndex("Email"));
            String address = cursor.getString(cursor.getColumnIndex("Address"));
            String pos = cursor.getString(cursor.getColumnIndex("POS"));
            String mainOffice = cursor.getString(cursor.getColumnIndex("IsMainOffice"));
            String BillNoPrefix_str = cursor.getString(cursor.getColumnIndex("BillNoPrefix"));
            if(BillNoPrefix_str == null)
                BillNoPrefix_str = "";

            Name.setText(name);
            Gstin.setText(gstin);
            RefernceNo.setText(refernceNo);
            Phone.setText(phone);
            Email.setText(email);
            Address.setText(address);
            BillNoPrefix.setText(BillNoPrefix_str);
            POS.setText(pos);
            IsMainOffice.setText(mainOffice);

        }
        // dbHelper.close();
    }
    private int getIndex_pos(String substring){

        int index = 0;
        for (int i = 0; index==0 && i < spinner1.getCount(); i++){

            if (spinner1.getItemAtPosition(i).toString().contains(substring)){
                index = i;
            }

        }

        return index;

    }
    private void close(View v)
    {
        dbHelper.CloseDatabase();
        getActivity().finish();
    }
    private void TextChangeListener() {


        try {
            Gstin.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {   }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void afterTextChanged(Editable s) {
                    String gstin = Gstin.getText().toString();
                    if(gstin.length() == 2)
                    {
                        if(GSTINValidation.checkValidStateCode(gstin, myContext)){
                            String stateCode = gstin.substring(0,2);
                            POS.setText(stateCode);
                        }else
                        {
                            Toast.makeText(myContext, "Invalid StateCode for GSTIN",Toast.LENGTH_SHORT).show();
                            Gstin.setText("");
                        }
                    }
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void apply(View v)
    {
        String billPrefix = BillNoPrefix.getText().toString().trim();
        String gstin = Gstin.getText().toString().trim().toUpperCase();
        if (Gstin.getText().toString().trim().toUpperCase().equals("") )
        {
            Toast.makeText(myContext, "GSTIN cannot  be empty ", Toast.LENGTH_SHORT).show();
            return;
        }else if (Gstin.getText().toString().trim().toUpperCase().length()!=15){
            Toast.makeText(myContext, "Please enter 15 characters GSTIN ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!GSTINValidation.checkGSTINValidation(gstin))
        {
            Toast.makeText(myContext, "Invalid GSTIN", Toast.LENGTH_SHORT).show();
            return;
        }else if(!GSTINValidation.checkValidStateCode(gstin,myContext))
        {
            Toast.makeText(myContext, "Invalid StateCode for GSTIN",Toast.LENGTH_SHORT).show();
            Gstin.setText("");
            return;
        }

        String GSTIN = Gstin.getText().toString();
        String stateSelected = POS.getText().toString();
        int length = stateSelected.length();
        String stateCode = stateSelected.substring(length-2,length);
        if(!GSTIN.substring(0,2).equals(stateCode))
        {
            POS.setText(GSTIN.substring(0,2));
        }

        String referenceNo = RefernceNo.getText().toString().trim();
        dbHelper.CreateDatabase();
        dbHelper.OpenDatabase();
        int ll = dbHelper.updateOwnerDetails(billPrefix,gstin,referenceNo,GSTIN.substring(0,2));
        if(ll>0)
            Toast.makeText(myContext, "Details updated successfully", Toast.LENGTH_SHORT).show();
    }


}
