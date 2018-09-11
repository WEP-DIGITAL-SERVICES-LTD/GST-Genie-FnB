package com.wepindia.pos.views.Settings;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.BillSetting;
import com.wep.common.app.Database.DatabaseHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.EMOJI_FILTER;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentSettingsHeaderFooter extends Fragment {

    Context myContext;
    int counter =0;
    DatabaseHandler dbHandler ;
    MessageDialog MsgBox;// = new MessageDialog(HeaderFooterActivity.this);
    @BindView(R.id.et_settings_header_text_one)     EditText edtHeaderOne;
    @BindView(R.id.et_settings_header_text_two)     EditText edtHeaderTwo;
    @BindView(R.id.et_settings_header_text_three)   EditText edtHeaderThree;
    @BindView(R.id.et_settings_header_text_four)    EditText edtHeaderFour;
    @BindView(R.id.et_settings_header_text_five)    EditText edtHeaderFive;
    @BindView(R.id.et_settings_Footer_one)          EditText edtFooterOne;
    @BindView(R.id.et_settings_Footer_two)          EditText edtFooterTwo;
    @BindView(R.id.et_settings_Footer_three)        EditText edtFooterThree;
    @BindView(R.id.et_settings_Footer_four)         EditText edtFooterFour;
    @BindView(R.id.et_settings_Footer_five)         EditText edtFooterFive;
    @BindView(R.id.et_settings_jurisdictions)       EditText edtJurisdictions;
    @BindView(R.id.btnApplyHeaderFooter)            Button btnApply;
    @BindView(R.id.btnCloseHeaderFooter)            Button btnClose;
    @BindView(R.id.tv_jurisdictions_text_count)
    TextView tvJurisdictionsTextCount;
    BillSetting objBillSettings = new BillSetting();
    private String TAG = FragmentSettingsHeaderFooter.class.getSimpleName();


    public FragmentSettingsHeaderFooter() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_header_footer_text, container, false);
        myContext = getActivity();
        ButterKnife.bind(this, view);
        MsgBox = new MessageDialog(myContext);

        try{
            dbHandler.CloseDatabase();
            dbHandler.CreateDatabase();
            dbHandler.OpenDatabase();
            applyValidations();
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
        mPopulateInsertedData();
    }

    @OnClick({R.id.btnApplyHeaderFooter})
    protected void onClickEvent(View view){
        switch (view.getId()){
            case R.id.btnApplyHeaderFooter:
                mInsertHeaderFooter();
                break;
            case R.id.btnCloseHeaderFooter:
                Close();
                break;
            default:
                break;
        }
    }

    private void mInsertHeaderFooter(){


        if(edtHeaderOne.getText().toString().trim().equalsIgnoreCase("")) {
            MsgBox.Show("Information", "Address line1 is mandatory");
        } else {
            String strHeader1 = edtHeaderOne.getText().toString().trim();
            String strHeader2 = edtHeaderTwo.getText().toString().trim();
            String strHeader3 = edtHeaderThree.getText().toString().trim();
            String strHeader4 = edtHeaderFour.getText().toString().trim();
            String strHeader5 = edtHeaderFive.getText().toString().trim();
            String strFooter1 = edtFooterOne.getText().toString().trim();
            String strFooter2 = edtFooterTwo.getText().toString().trim();
            String strFooter3 = edtFooterThree.getText().toString().trim();
            String strFooter4 = edtFooterFour.getText().toString().trim();
            String strFooter5 = edtFooterFive.getText().toString().trim();
            String strJurisdictions = edtJurisdictions.getText().toString().trim();
            int iResult = 0;
            // Update new settings in database
            iResult = dbHandler.updateHeaderFooterText(strHeader1, strHeader2, strHeader3, strHeader4, strHeader5,
                    strFooter1, strFooter2, strFooter3, strFooter4, strFooter5, strJurisdictions);
            if (iResult > 0) {
                // msgBox.Show("Information", "Saved Successfully");
                Toast.makeText(getActivity(), "Data stored successfully", Toast.LENGTH_SHORT).show();
            } else {
                MsgBox.Show("Exception", "Failed to save. Please try again");
            }
        }
    }

    private void mPopulateInsertedData(){
        Cursor curHeaderFooter = null;
        curHeaderFooter = dbHandler.getBillSettings();
        try{
            if(curHeaderFooter != null && curHeaderFooter.getCount() > 0){
                curHeaderFooter.moveToFirst();
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText1)) != null){
                    edtHeaderOne.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText1)));
                }
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText2)) != null){
                    edtHeaderTwo.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText2)));
                }
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText3)) != null){
                    edtHeaderThree.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText3)));
                }
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText4)) != null){
                    edtHeaderFour.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText4)));
                }
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText5)) != null){
                    edtHeaderFive.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_HeaderText5)));
                }

                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText1)) != null){
                    edtFooterOne.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText1)));
                }
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText2)) != null){
                    edtFooterTwo.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText2)));
                }
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText3)) != null){
                    edtFooterThree.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText3)));
                }
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText4)) != null){
                    edtFooterFour.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText4)));
                }
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText5)) != null){
                    edtFooterFive.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_FooterText5)));
                }
                if(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_JURISDICTIONS)) != null){
                    edtJurisdictions.setText(curHeaderFooter.getString(curHeaderFooter.getColumnIndex(DatabaseHandler.KEY_JURISDICTIONS)));
                }

            }
        } catch (Exception ex){
            Log.e(TAG,"Unable able to get data from the table bill settings for header and footer " +ex.getMessage());
        }finally {
            if(curHeaderFooter != null){
                curHeaderFooter.close();
            }
        }
    }

    void applyValidations()
    {
        edtFooterOne.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtFooterTwo.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtFooterThree.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtFooterFour.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtFooterFive.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtHeaderOne.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtHeaderTwo.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtHeaderThree.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtHeaderFour.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtHeaderFive.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        edtJurisdictions.setFilters(new InputFilter[]{new EMOJI_FILTER()});

        edtFooterOne.addTextChangedListener(restrictLengthWatcher);
        edtFooterTwo.addTextChangedListener(restrictLengthWatcher);
        edtFooterThree.addTextChangedListener(restrictLengthWatcher);
        edtFooterFour.addTextChangedListener(restrictLengthWatcher);
        edtFooterFive.addTextChangedListener(restrictLengthWatcher);

        edtHeaderOne.addTextChangedListener(restrictLengthWatcher);
        edtHeaderTwo.addTextChangedListener(restrictLengthWatcher);
        edtHeaderThree.addTextChangedListener(restrictLengthWatcher);
        edtHeaderFour.addTextChangedListener(restrictLengthWatcher);
        edtHeaderFive.addTextChangedListener(restrictLengthWatcher);
        edtJurisdictions.addTextChangedListener(restrictLengthWatcher);
    }

    TextWatcher restrictLengthWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(edtJurisdictions.isFocused()) {
                int length = edtJurisdictions.length();
                //String convert = String.valueOf(length);
                tvJurisdictionsTextCount.setText(length +"/150");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

            try{
                if(counter>0)
                {
                    counter--;
                    return;
                }


                if(editable == edtFooterOne.getEditableText())
                {
                    String content = edtFooterOne.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtFooterOne.setText(content.substring(0,30));
                        edtFooterOne.setSelection(edtFooterOne.getText().length());

                    }
                }
                else if(editable == edtFooterTwo.getEditableText())
                {
                    String content = edtFooterTwo.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtFooterTwo.setText(content.substring(0,30));
                        edtFooterTwo.setSelection(edtFooterTwo.getText().length());

                    }
                }
                else if(editable == edtFooterThree.getEditableText())
                {
                    String content = edtFooterThree.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtFooterThree.setText(content.substring(0,30));
                        edtFooterThree.setSelection(edtFooterThree.getText().length());

                    }
                }
                else if(editable == edtFooterFour.getEditableText())
                {
                    String content = edtFooterFour.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtFooterFour.setText(content.substring(0,30));
                        edtFooterFour.setSelection(edtFooterFour.getText().length());

                    }
                }
                else if(editable == edtFooterFive.getEditableText())
                {
                    String content = edtFooterFive.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtFooterFive.setText(content.substring(0,30));
                        edtFooterFive.setSelection(edtFooterFive.getText().length());

                    }
                }
                else if(editable == edtHeaderOne.getEditableText())
                {
                    String content = edtHeaderOne.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtHeaderOne.setText(content.substring(0,30));
                        edtHeaderOne.setSelection(edtHeaderOne.getText().length());

                    }
                }
                else if(editable == edtHeaderTwo.getEditableText())
                {
                    String content = edtHeaderTwo.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtHeaderTwo.setText(content.substring(0,30));
                        edtHeaderTwo.setSelection(edtHeaderTwo.getText().length());

                    }
                }
                else if(editable == edtHeaderThree.getEditableText())
                {
                    String content = edtHeaderThree.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtHeaderThree.setText(content.substring(0,30));
                        edtHeaderThree.setSelection(edtHeaderThree.getText().length());

                    }
                }
                else if(editable == edtHeaderFour.getEditableText())
                {
                    String content = edtHeaderFour.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtHeaderFour.setText(content.substring(0,30));
                        edtHeaderFour.setSelection(edtHeaderFour.getText().length());

                    }
                }
                else if(editable == edtHeaderFive.getEditableText())
                {
                    String content = edtHeaderFive.getText().toString();
                    if(content.length()>30) {
                        counter++;
                        edtHeaderFive.setText(content.substring(0,30));
                        edtHeaderFive.setSelection(edtHeaderFive.getText().length());

                    }
                }
                else if(editable == edtJurisdictions.getEditableText())
                {
                    String content = edtJurisdictions.getText().toString();
                    if(content.length()>150) {
                        counter++;
                        edtJurisdictions.setText(content.substring(0,150));
                        edtJurisdictions.setSelection(edtJurisdictions.getText().length());

                    }
                }
            }catch (Exception e)
            {
                Log.e(TAG, "Error occured");
            }

        }
    };

    public void Close(){
        dbHandler.CloseDatabase();
        getActivity().finish();
    }

}
