package com.wepindia.pos.views.Settings;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;

import com.wepindia.pos.FilePickerActivity;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.UploadFilePickerActivity;
import com.wepindia.pos.utils.EMOJI_FILTER;
import com.wepindia.pos.utils.GSTINValidation;
import com.wepindia.pos.utils.Validations;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class FragmentSettingsOwnerDetail extends Fragment {

    private static final String TAG = FragmentSettingsOwnerDetail.class.getName();

    private final int PHONE = 1;
    private final int EMAIL = 2;
    private final int GSTIN = 3;

    //private static DatabaseHelper dbHelper;
    View view;

    @BindView(R.id.et_od_firm_name)     EditText edtFirmName;
    @BindView(R.id.et_od_phone)         EditText edtPhone;
    @BindView(R.id.et_od_email)         EditText edtEmail;
    @BindView(R.id.et_od_address)       EditText edtAddress;
    @BindView(R.id.et_od_gstin)         EditText edtGSTIN;
    @BindView(R.id.et_od_reference_no)  EditText edtRefNo;
    @BindView(R.id.et_od_bill_no_prefix)    EditText edtBillNoPrefix;
    @BindView(R.id.sp_od_pos)           Spinner spnPOS;
    @BindView(R.id.sp_od_is_main_office) Spinner spnIsMainOffice;
    @BindView(R.id.bt_od_apply)          Button btnApply;
    //    @BindView(R.id.bt_browse_image)          Button btnBrowse;
    @BindView(R.id.bt_remove_image)          Button btnRemoveLogo;
    @BindView(R.id.image_path)          TextView tvImagePath;
    @BindView(R.id.iv_browse_image)
    ImageView ivCmopanyLogo;
    @BindView(R.id.bt_browse_image)
    ImageButton bt_browse_image;

    String[] arrayPOS;
    String[] arrayIsMainOffice = {"Select", "Yes", "No"};

    MessageDialog MsgBox;
    Context myContext ;
    public String FRAGMENTNAME = "FragmentName";
    private String strImageUri = "";
    private DatabaseHandler dbHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = getContext();
        dbHandler = new DatabaseHandler(myContext);
        dbHandler.CreateDatabase();
        dbHandler.OpenDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        // dbHelper = DatabaseHelper.getInstance(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings_display_owner_detail, container, false);
        MsgBox = new MessageDialog(myContext);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            loadSpinnerData();
            TextChangeListener();
            applyValidations();

            mPopulateDataOnWidget();

        }catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @OnClick({R.id.bt_od_apply, R.id.iv_browse_image, R.id.bt_remove_image, R.id.bt_browse_image})
    protected void OnClickEvent(View view) {
        switch (view.getId()) {
            case R.id.bt_od_apply:
                mInsertOrUpdate();
                break;
            case R.id.bt_browse_image:
            case R.id.iv_browse_image:
                browseImage();
                break;
            case R.id.bt_remove_image:
                removeImage();
                break;
            default:
                break;
        }
    }

    private void mPopulateDataOnWidget() {
        Cursor cursorOwnerDetails = null;
        try{
            cursorOwnerDetails = dbHandler.getOwnerDetail();
            if(cursorOwnerDetails != null && cursorOwnerDetails.getCount() > 0){
                cursorOwnerDetails.moveToFirst();
                if(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_FIRM_NAME)) != null){
                    edtFirmName.setText(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_FIRM_NAME)));
                }
                if(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_PhoneNo)) != null){
                    edtPhone.setText(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_PhoneNo)));
                }
                if(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_USER_EMAIL)) != null){
                    edtEmail.setText(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_USER_EMAIL)));
                }
                if(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Address)) != null){
                    edtAddress.setText(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_Address)));
                }
                if(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_GSTIN)) != null){
                    edtGSTIN.setText(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_GSTIN)));
                }
                if(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_REFERENCE_NO)) != null){
                    edtRefNo.setText(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_REFERENCE_NO)));
                }
                if(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_BillNoPrefix)) != null){
                    edtBillNoPrefix.setText(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_BillNoPrefix)));
                }
                if(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_POS)) != null){
                    spnPOS.setSelection(getIndex_pos(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_POS))));
                }
                if(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)) != null
                        && !cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)).isEmpty()
                        && !cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)).equals("1234567890")){
                    ivCmopanyLogo.setImageURI(Uri.fromFile(new File(cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN)))));
                    strImageUri = cursorOwnerDetails.getString(cursorOwnerDetails.getColumnIndex(DatabaseHandler.KEY_TINCIN));
                } else {
                    ivCmopanyLogo.setImageResource(R.mipmap.ic_image_blank);
                    strImageUri = "";
                }

            }
        } catch (Exception ex){
            Log.e(TAG,"Unable able to get data from the table owners table " +ex.getMessage());
        }finally {
            if(cursorOwnerDetails != null){
                cursorOwnerDetails.close();
            }
        }
    }

    private void TextChangeListener() {

        try {
            edtGSTIN.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {   }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void afterTextChanged(Editable s) {
                    String gstin = edtGSTIN.getText().toString();
                    if(gstin.equals("")) {
                        spnPOS.setEnabled(true);
                        spnPOS.setSelection(0);
                    }else if(spnPOS.isEnabled()) // gstin entered and spnPos is enabled
                    {
                        spnPOS.setEnabled(false);
                    }
                    if(gstin.length() == 2)
                    {
                        if(Validations.checkValidStateCode(gstin, myContext)){
                            String stateCode = gstin.substring(0,2);
                            spnPOS.setSelection(getIndex_pos(stateCode));
                        }else
                        {
                            MsgBox.Show("Invalid Information","Please Enter Valid StateCode for GSTIN");
                            edtGSTIN.setText("");
                        }
                    }
                }
            });
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

    }

    void browseImage()
    {
       /* FilePickerDialogFragment myFragment1 =
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
        frag.mInitListener(OwnerDetailsSettingsFragment.this);
        frag.show(fm1, "File Picker");*/

        Intent intent = new Intent(myContext, FilePickerActivity.class);
        intent.putExtra("contentType","image");
        startActivityForResult(intent, FilePickerActivity.PICK_IMAGE_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FilePickerActivity.PICK_IMAGE_CODE)
            {
                strImageUri = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                if(!strImageUri.equalsIgnoreCase(""))
                {
                    ivCmopanyLogo.setImageURI(null);
                    ivCmopanyLogo.setImageURI(Uri.fromFile(new File(strImageUri)));
                }
                else
                {
                    ivCmopanyLogo.setImageResource(R.mipmap.ic_image_blank);
                }
            }
        }
    }

    void removeImage(){
//        ivCmopanyLogo.setImageResource(R.drawable.add_image);
        ivCmopanyLogo.setImageResource(R.mipmap.ic_image_blank);
        strImageUri = "";
        String gstin = edtGSTIN.getText().toString().trim();

        try{

            long status = dbHandler.updateOwnerCompanyLogo(strImageUri, gstin);
            if (status > 0)
                Toast.makeText(myContext, "Company logo removed successfully", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(myContext, "Error occurred in updating company logo.", Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(myContext, "Error occurred in updating company logo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void mInsertOrUpdate() {

        if (mValidateEditText(edtFirmName) && mValidateEditText(edtPhone) && mValidateEditText(edtGSTIN)
                && mValidateEditText(edtEmail) && mValidateEditText(edtAddress)
                && spnPOS.getSelectedItemPosition() !=0) {

            if (!mDataValidation(GSTIN)) {
                MsgBox.Show("Invalid Information", "Please Enter Valid GSTIN");
                return;
            }
            if (mDataValidation(EMAIL) && mDataValidation(PHONE)) {

                String gstin = edtGSTIN.getText().toString().toUpperCase().trim();
                String name = edtFirmName.getText().toString();
                String phone =edtPhone.getText().toString();
                String pos= spnPOS.getSelectedItem().toString();
                String email =edtEmail.getText().toString();
                String RefernceNo = edtRefNo.getText().toString();
                String billPrefix = edtBillNoPrefix.getText().toString();
                String address = edtAddress.getText().toString();
                String  isMainOffice = "Yes";

                int length = pos.length();
                String posCode = "";
                if (length > 0) {
                    posCode = pos.substring(length - 2, length);
                }else
                {
                    if (!gstin.equals(""))
                        posCode =pos.substring(length - 2, length);
                }
                int delDetails = dbHandler.deleteOwnerDetails();
                long lDataStored = dbHandler.addOwnerDetails( name,  gstin, phone, email,  address, posCode, isMainOffice,
                        RefernceNo, billPrefix, strImageUri);
                if(lDataStored > -1)
                {
                    //mClearWidgets();
                    Toast.makeText(getActivity(),"Data stored successfully.",Toast.LENGTH_SHORT).show();
                    edtGSTIN.setText(gstin);
                } else {
                    Toast.makeText(getActivity(),"Failed to save. Please try again.",Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            MsgBox.Show("Incomplete Information", "Please fill required details");
        }
    }

    void loadSpinnerData()
    {
        arrayPOS = getResources().getStringArray(R.array.poscode);
        //Creating the ArrayAdapter instance having the POSCode list
        ArrayAdapter adapterPOS = new ArrayAdapter(myContext, android.R.layout.simple_spinner_item, arrayPOS);
        adapterPOS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spnPOS.setAdapter(adapterPOS);

        ArrayAdapter adapterIsMainOffice = new ArrayAdapter(myContext, android.R.layout.simple_spinner_item, arrayIsMainOffice);
        adapterIsMainOffice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnIsMainOffice.setAdapter(adapterIsMainOffice);
    }

    void applyValidations()
    {
        edtAddress.setFilters(new InputFilter[]{new EMOJI_FILTER()});
    }

    private boolean mValidateEditText(EditText edt) {
        if (!edt.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean mDataValidation(int mode) {
        boolean bResult = false;
        switch (mode) {
            case EMAIL:
                bResult = Validations.isValidEmailAddress(edtEmail.getText().toString());
                if (!bResult) {
                    MsgBox.Show("Invalid Information", "Please Enter Valid Email id");
                }
                break;
            case PHONE:
                if (edtPhone.getText().toString().trim().length() == 10) {
                    bResult = true;
                } else {
                    MsgBox.Show("Invalid Information", "Phone no cannot be less than 10 digits.");
                }
                break;
            case GSTIN: String gstin = edtGSTIN.getText().toString().trim();
                if(Validations.checkGSTINValidation(gstin)) {
                    bResult = Validations.checkValidStateCode(gstin, myContext);
                }
                break;
            default:
                break;
        }
        return bResult;
    }

    private int getIndex_pos(String substring) {

        int index = 0;
        for (int i = 0; index == 0 && i < spnPOS.getCount(); i++) {

            if (spnPOS.getItemAtPosition(i).toString().contains(substring)) {
                index = i;
            }

        }
        return index;
    }

}
