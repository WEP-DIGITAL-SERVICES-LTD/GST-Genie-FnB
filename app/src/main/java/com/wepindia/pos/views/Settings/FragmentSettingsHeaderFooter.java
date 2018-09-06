package com.wepindia.pos.views.Settings;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wep.common.app.Database.BillSetting;
import com.wep.common.app.Database.DatabaseHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;

public class FragmentSettingsHeaderFooter extends Fragment {

    Context myContext;
    DatabaseHandler dbHeaderFooterSettings ;
    MessageDialog MsgBox;// = new MessageDialog(HeaderFooterActivity.this);
    EditText headerText1, headerText2, headerText3, headerText4, headerText5, addressLine2,addressLine3,
            addressLineFooter1, addressLineFooter2, addressLineFooter3, addressLineFooter4, addressLineFooter5;
    BillSetting objBillSettings = new BillSetting();
    private String TAG = FragmentSettingsHeaderFooter.class.getSimpleName();
    private Button btnApplyHeaderFooter,btnCloseHeaderFooter;


    public FragmentSettingsHeaderFooter() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHeaderFooterSettings = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_header_footer_text, container, false);
        myContext = getActivity();
        MsgBox = new MessageDialog(myContext);
        headerText1 = (EditText)view.findViewById(R.id.headerText1);
        headerText2 = (EditText)view.findViewById(R.id.headerText2);
        headerText3 = (EditText)view.findViewById(R.id.headerText3);
        headerText4 = (EditText)view.findViewById(R.id.headerText4);
        headerText5 = (EditText)view.findViewById(R.id.headerText5);
        /*addressLine2 = (EditText)view.findViewById(R.id.addressLine2);
        addressLine3 = (EditText)view.findViewById(R.id.addressLine3);*/
        addressLineFooter1 = (EditText)view.findViewById(R.id.addressLineFooter1);
        addressLineFooter2 = (EditText)view.findViewById(R.id.addressLineFooter2);
        addressLineFooter3 = (EditText)view.findViewById(R.id.addressLineFooter3);
        addressLineFooter4 = (EditText)view.findViewById(R.id.addressLineFooter4);
        addressLineFooter5 = (EditText)view.findViewById(R.id.addressLineFooter5);

        btnApplyHeaderFooter = (Button) view.findViewById(R.id.btnApplyHeaderFooter);
        btnApplyHeaderFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Apply();
            }
        });
        btnCloseHeaderFooter = (Button) view.findViewById(R.id.btnCloseHeaderFooter);
        btnCloseHeaderFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Close();
            }
        });
        try{
            dbHeaderFooterSettings.CloseDatabase();
            dbHeaderFooterSettings.CreateDatabase();
            dbHeaderFooterSettings.OpenDatabase();
            DisplaySettings();
        }
        catch(Exception exp){
            exp.printStackTrace();
            MsgBox.Show("Exception", exp.getMessage());
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplaySettings();
    }

    private void DisplaySettings(){
        String[] tokens = new String[3];
        tokens[0] = "";
        tokens[1] = "";
        tokens[2] = "";
        Cursor crsrHeaderFooterSetting = null;
        crsrHeaderFooterSetting = dbHeaderFooterSettings.getBillSetting();
        if(crsrHeaderFooterSetting.moveToFirst()){
            /*try{
                tokens = crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText")).split(Pattern.quote("|"));
            }catch (Exception e){
                tokens[0] = "";
                tokens[1] = "";
                tokens[2] = "";
            }
            if(!tokens[0].equalsIgnoreCase(""))
                headerText1.setText(tokens[0]);
            if(!tokens[1].equalsIgnoreCase(""))
                addressLine2.setText(tokens[1]);
            if(!tokens[2].equalsIgnoreCase(""))
                addressLine3.setText(tokens[2]);*/
            headerText1.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText1")));
            headerText2.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText2")));
            headerText3.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText3")));
            headerText4.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText4")));
            headerText5.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("HeaderText5")));
            addressLineFooter1.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText1")));
            addressLineFooter2.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText2")));
            addressLineFooter3.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText3")));
            addressLineFooter4.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText4")));
            addressLineFooter5.setText(crsrHeaderFooterSetting.getString(crsrHeaderFooterSetting.getColumnIndex("FooterText5")));
        }
        else{
            Log.d(TAG,"DisplayHeaderFooterSettings No data in BillSettings table");
        }
    }

    public void Apply(){
        if(false)/*headerText1.getText().toString().trim().equalsIgnoreCase("") || addressLine2.getText().toString().trim().equalsIgnoreCase(""))*/
        {
            if(headerText1.getText().toString().trim().equalsIgnoreCase(""))
            {
                MsgBox.Show("Information", "Address line1 is mandatory");
            }
            else if(addressLine2.getText().toString().trim().equalsIgnoreCase(""))
            {
                MsgBox.Show("Information", "Address line2 is mandatory");
            }
        }
        else
        {
            String strHeader1 = headerText1.getText().toString().trim();//+" |"+addressLine2.getText().toString().trim()+" |"+" "+addressLine3.getText().toString().trim();
            String strHeader2 = headerText2.getText().toString().trim();
            String strHeader3 = headerText3.getText().toString().trim();
            String strHeader4 = headerText4.getText().toString().trim();
            String strHeader5 = headerText5.getText().toString().trim();
            String strFooter1 = addressLineFooter1.getText().toString().trim();
            String strFooter2 = addressLineFooter2.getText().toString().trim();
            String strFooter3 = addressLineFooter3.getText().toString().trim();
            String strFooter4 = addressLineFooter4.getText().toString().trim();
            String strFooter5 = addressLineFooter5.getText().toString().trim();
            int iResult = 0;
            // Update new settings in database
            iResult = dbHeaderFooterSettings.updateHeaderFooterText(strHeader1,strHeader2, strHeader3, strHeader4, strHeader5,
                    strFooter1, strFooter2, strFooter3, strFooter4, strFooter5);
            if(iResult > 0){
                MsgBox.Show("Information", "Saved Successfully");
            }
            else{
                MsgBox.Show("Exception", "Save Failed");
            }
        }
    }

    public void Close(){
        dbHeaderFooterSettings.CloseDatabase();
        getActivity().finish();
    }

}
