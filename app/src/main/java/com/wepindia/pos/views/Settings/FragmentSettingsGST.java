package com.wepindia.pos.views.Settings;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.wep.common.app.Database.BillSetting;
import com.wep.common.app.Database.DatabaseHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;


public class FragmentSettingsGST extends Fragment {
    Context myContext;

    // DatabaseHandler object
    DatabaseHandler dbGSTSettings;
    // Message Dialog object
    MessageDialog MsgBox;// = new MessageDialog(DineInSettingsActivity.this);

    // View handlers
    RadioButton rbGstinEnable_in, rbGstinDisable_in,rbPosEnable , rbPosDisable ,rbHsnCodeEnable,  rbHsnCodeDisable ;
    RadioButton rbReverseChargeEnable , rbReverseChargeDisable ,rbGstinEnable_out, rbGstinDisable_out ;
    RadioButton rbPosEnable_out,  rbPosDisable_out,  rbHsnCodeEnable_out,  rbHsnCodeDisable_out , rbReverseChargeEnable_out ,
            rbReverseChargeDisable_out ,rbUTGSTEnabled_out,rbUTGSTDisable_out, rbHSNPrintEnabled_out, rbHSNPrintDisabled_out;

    Button btnApply, btnClose;
    BillSetting objBillSettings ;



    public FragmentSettingsGST() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            dbGSTSettings = new DatabaseHandler(getActivity());
            dbGSTSettings.OpenDatabase();
        }catch (Exception e){

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_settings_gst, container, false);
        myContext = getActivity();

        objBillSettings =  new BillSetting();

        MsgBox = new MessageDialog(myContext);

        try {
            InitializeViews(view);

            dbGSTSettings.CloseDatabase();
            dbGSTSettings.CreateDatabase();
            dbGSTSettings.OpenDatabase();
           // DisplaySettings();
        } catch (Exception exp) {
            exp.printStackTrace();
            MsgBox.Show("Exception", exp.getMessage());
        }
        return view;
    }

    private void InitializeViews(View view) {

        rbGstinEnable_in = (RadioButton) view.findViewById(R.id.rbGstinEnable_in);
        rbGstinDisable_in = (RadioButton) view.findViewById(R.id.rbGstinDisable_in);

        rbPosEnable = (RadioButton) view.findViewById(R.id.rbPosEnable);
        rbPosDisable = (RadioButton) view.findViewById(R.id.rbPosDisable);

        rbHsnCodeEnable = (RadioButton) view.findViewById(R.id.rbHsnCodeEnable);
        rbHsnCodeDisable = (RadioButton) view.findViewById(R.id.rbHsnCodeDisable);

        rbReverseChargeEnable = (RadioButton) view.findViewById(R.id.rbReverseChargeEnable);
        rbReverseChargeDisable = (RadioButton) view.findViewById(R.id.rbReverseChargeDisable);

        rbGstinEnable_out = (RadioButton) view.findViewById(R.id.rbGstinEnable_out);
        rbGstinDisable_out = (RadioButton) view.findViewById(R.id.rbGstinDisable_out);

        rbPosEnable_out = (RadioButton) view.findViewById(R.id.rbPosEnable_out);
        rbPosDisable_out = (RadioButton) view.findViewById(R.id.rbPosDisable_out);

        rbHsnCodeEnable_out = (RadioButton) view.findViewById(R.id.rbHsnCodeEnable_out);
        rbHsnCodeDisable_out = (RadioButton) view.findViewById(R.id.rbHsnCodeDisable_out);

        rbReverseChargeEnable_out = (RadioButton) view.findViewById(R.id.rbReverseChargeEnable_out);
        rbReverseChargeDisable_out = (RadioButton) view.findViewById(R.id.rbReverseChargeDisable_out);

        rbUTGSTEnabled_out = (RadioButton) view.findViewById(R.id.rbUTGSTEnabled_out);
        rbUTGSTDisable_out = (RadioButton) view.findViewById(R.id.rbUTGSTDisable_out);

        rbHSNPrintEnabled_out = (RadioButton) view.findViewById(R.id.rbHSNPrintEnabled_out);
        rbHSNPrintDisabled_out = (RadioButton) view.findViewById(R.id.rbHSNPrintDisabled_out);

        btnApply = (Button) view.findViewById(R.id.btnApplyOtherSettings);
        btnClose = (Button) view.findViewById(R.id.btnCloseOtherSettings);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Apply(v);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Close(v);
            }
        });
    }

    private void DisplaySettings() {
        Cursor crsrBillSetting = null;

        // Read the settings from database and display it in views.
        crsrBillSetting = dbGSTSettings.getBillSetting();

        if (crsrBillSetting.moveToFirst()) {

            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("GSTIN_In")) == 1) {
                rbGstinEnable_in.setChecked(true);
            } else {
                rbGstinDisable_in.setChecked(true);
            }


            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("POS")) == 1) {
                rbPosEnable.setChecked(true);
            } else {
                rbPosDisable.setChecked(true);
            }


            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("HSNCode")) == 1) {
                rbHsnCodeEnable.setChecked(true);
            } else {
                rbHsnCodeDisable.setChecked(true);
            }


            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("ReverseCharge")) == 1) {
                rbReverseChargeEnable.setChecked(true);
            } else {
                rbReverseChargeDisable.setChecked(true);
            }

            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("GSTIN_Out")) == 1) {
                rbGstinEnable_out.setChecked(true);
            } else {
                rbGstinDisable_out.setChecked(true);
            }


            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("POS_Out")) == 1) {
                rbPosEnable_out.setChecked(true);
            } else {
                rbPosDisable_out.setChecked(true);
            }


            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("HSNCode_Out")) == 1) {
                rbHsnCodeEnable_out.setChecked(true);
            } else {
                rbHsnCodeDisable_out.setChecked(true);
            }


            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("ReverseCharge_Out")) == 1) {
                rbReverseChargeEnable_out.setChecked(true);
            }
            else {
                rbReverseChargeDisable_out.setChecked(true);
            }


            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("UTGSTEnabled")) == 1) {
                rbUTGSTEnabled_out.setChecked(true);
            } else {
                rbUTGSTDisable_out.setChecked(true);
            }

            if (crsrBillSetting.getInt(crsrBillSetting.getColumnIndex("HSNPrintEnabled_out")) == 1) {
                rbHSNPrintEnabled_out.setChecked(true);
            } else {
                rbHSNPrintDisabled_out.setChecked(true);
            }

        }

        else {
            Log.d("OtherSettings", "No data in BillSettings table");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplaySettings();
    }

    private void ReadGSTSettings() {

        // Local variables
        int igstin_in, igstin_out, ipos, ipos_out, ihsncode, ihsncode_out , ireversecharge, ireversecharge_out,igst;
        int urgst_out =0, hsnprint_out =0;

        igst =1;

        if (rbGstinEnable_in.isChecked() == true) {
            igstin_in= 1;
        } else {
            igstin_in = 0;
        }

        // Price Change
        if (rbPosEnable.isChecked() == true) {
            ipos = 1;
        } else {
            ipos = 0;
        }

        // BillwithStock
        if (rbHsnCodeEnable.isChecked() == true) {
            ihsncode = 1;
        } else {
            ihsncode= 0;
        }

        // Tax
        if (rbReverseChargeEnable.isChecked() == true) {
            ireversecharge = 1;
        } else {
            ireversecharge = 0;
        }
        if (rbGstinEnable_out.isChecked() == true) {
            igstin_out= 1;
        } else {
            igstin_out = 0;
        }

        // Price Change
        if (rbPosEnable_out.isChecked() == true) {
            ipos_out = 1;
        } else {
            ipos_out = 0;
        }

        // BillwithStock
        if (rbHsnCodeEnable_out.isChecked() == true) {
            ihsncode_out = 1;
        } else {
            ihsncode_out= 0;
        }

        // Tax
        if (rbReverseChargeEnable_out.isChecked() == true) {
            ireversecharge_out = 1;
        } else {
            ireversecharge_out = 0;
        }
        // UTGST
        if (rbUTGSTEnabled_out.isChecked() == true) {
            urgst_out = 1;
        } else {
            urgst_out = 0;
        }

        // HSN Print
        if (rbHSNPrintEnabled_out.isChecked() == true) {
            hsnprint_out = 1;
        } else {
            hsnprint_out = 0;
        }

        // Initialize all the settings variable
        objBillSettings.setLoginWith(0);
        objBillSettings.setGSTIN(igstin_in);
        objBillSettings.setPOS(ipos);
        objBillSettings.setHSNCode(ihsncode);
        objBillSettings.setReverseCharge(ireversecharge);
        objBillSettings.setGSTIN_out(igstin_out);
        objBillSettings.setPOS_out(ipos_out);
        objBillSettings.setHSNCode_out(ihsncode_out);
        objBillSettings.setReverseCharge_out(ireversecharge_out);
        objBillSettings.setGSTEnable(igst);
        objBillSettings.setUTGSTEnabled_out(urgst_out);
        objBillSettings.setHSNPrintenabled_out(hsnprint_out);
    }

    private void SaveGSTSettings() {
        int iResult = 0;

        // Update new settings in database
        iResult = dbGSTSettings.updateGSTSettings(objBillSettings);

        if (iResult > 0) {
            // Toast.makeText(myContext, "Settings saved",
            // Toast.LENGTH_LONG).show();
            MsgBox.Show("Information", "Settings saved");
        } else {
            // Toast.makeText(myContext, "Apply Settings failed - Updated row:"
            // + String.valueOf(iResult), Toast.LENGTH_LONG).show();
            MsgBox.Show("Warning", "Failed to save settings");
        }
    }

    public void Apply(View v) {
        // Read data to update
        ReadGSTSettings();

        // Save data in database
        SaveGSTSettings();

        // Display saved data
        // DisplaySettings();
    }

    public void Close(View v) {

        // close database connection
        dbGSTSettings.CloseDatabase();
        getActivity().finish();
    }
}
