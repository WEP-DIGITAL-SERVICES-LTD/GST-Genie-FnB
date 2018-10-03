/***************************************************************************
 * Project Name		:	VAJRA
 * <p>
 * File Name		:	DatabaseHandler
 * <p>
 * DateOfCreation	:	13-October-2012
 * <p>
 * Author			:	Balasubramanya Bharadwaj B S
 **************************************************************************/
package com.wepindia.pos;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.AppCompatCheckBox;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mswipetech.wisepad.sdktest.view.ApplicationData;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;
import com.wep.common.app.Database.BillSetting;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.utils.Preferences;
import com.wep.gstcall.api.http.HTTPAsyncTask;
import com.wep.gstcall.api.util.Config;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.utils.SubscriptionBillUploadUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.wepindia.pos.HomeActivity.Upload_Invoice_Count;

@SuppressWarnings("Since15")
public class LoginActivity extends WepBaseActivity implements HTTPAsyncTask.OnHTTPRequestCompletedListener {

    // DatabaseHandler object
    DatabaseHandler dbLogin = new DatabaseHandler(LoginActivity.this);
    // MessageDialog Object
    MessageDialog MsgBox;

    // View handling variables
    EditText txtUserId, txtPassword;
    Button btnDateDisplay, btnMonthDisplay, btnYearDisplay, btnLogin, btnClose;
    ImageButton btnHelp;

    // Class Variables
    private static final int HOME_RESULT = 1;
    //private static final String FILE_SHARED_PREFERENCE = "WeP_FnB";
    Calendar calDate;
    // Variables - BillSettings object
    BillSetting objBillSettings = new BillSetting();
    private SharedPreferences sharedPreferences;
    private AppCompatCheckBox appCompatCheckBox;
    private  String isPayPerUseModel = "n";
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog pd;
    private int upload_counter = 0;
    private boolean first_response = true;

    @TargetApi(9)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        try {
            MsgBox = new MessageDialog(this);
            calDate = Calendar.getInstance();
            btnHelp = (ImageButton) findViewById(R.id.btnHelp);
            txtUserId = (EditText) findViewById(R.id.txtUserId);
            txtPassword = (EditText) findViewById(R.id.txtPassword);
            btnDateDisplay = (Button) findViewById(R.id.btnDateDisplay);
            btnMonthDisplay = (Button) findViewById(R.id.btnMonthDisplay);
            btnYearDisplay = (Button) findViewById(R.id.btnYearDisplay);
            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLogin(v);
                }
            });
            btnClose = (Button) findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Close(v);
                }
            });
            appCompatCheckBox = (AppCompatCheckBox) findViewById(R.id.checkboxRememberMe);
            btnDateDisplay.setText(String.valueOf(calDate.get(Calendar.DAY_OF_MONTH)));
            btnMonthDisplay.setText(calDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
            btnYearDisplay.setText(String.valueOf(calDate.get(Calendar.YEAR)));

            btnHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOptions(view);
                }
            });

            dbLogin.CreateDatabase();
            dbLogin.OpenDatabase();
            //clickevent();
//            initSinglePrinter();
            sharedPreferences = Preferences.getSharedPreferencesForPrint(LoginActivity.this); // getSharedPreferences("PrinterConfigurationActivity", Context.MODE_PRIVATE);

            checkForisPayPerUseModel();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void checkForisPayPerUseModel() {
        Cursor cursorBillsetting = dbLogin.getBillSettings();
        if(cursorBillsetting!=null && cursorBillsetting.moveToFirst())
        {
            isPayPerUseModel = cursorBillsetting.getString(cursorBillsetting.getColumnIndex(DatabaseHandler.KEY_isPayPerUseModel));
        }
    }

    private void initSinglePrinter() {
        sharedPreferences = Preferences.getSharedPreferencesForPrint(LoginActivity.this); // getSharedPreferences("PrinterConfigurationActivity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("kot","Heyday");
        editor.putString("bill","Heyday");
        editor.putString("report","Heyday");
        editor.putString("receipt","Heyday");
        editor.commit();

        appCompatCheckBox.setChecked(sharedPreferences.getBoolean("isChecked",false));
        txtUserId.setText(sharedPreferences.getString("userNameTxt",""));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Login Activity", "OnStart() Event");
    }

    // About button
    public void About(View v) {
        String version ="0.0";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version ="0.0";
        }
        String strAboutMsg = "WeP EasyBill P1\n"+"\nAbout WeP Solutions Limited." +
                "\n\n\tWeP Digital is the Digital Services arm of WeP Solutions Limited (WeP). WeP is an innovative, reliable and a dynamic company. WeP came into being as a result of entrepreneurial work culture in Wipro. It has a committed and experienced team, helping the company grow leaps and bounds. We have grown and diversified, having spread our roots in an array of different areas like Managed Printing Solutions (MPS), Manufacturing and distribution of IT peripherals, Retail Billing solutions, and Document Management Solutions." +
                "\n\n\tWeP has been a very dynamic and adaptable organization. We keep reinventing ourselves to adapt to the ever-changing technology by introducing new products to the market, based on the need of the hour. We are a self-reliant company for technology for both new products and its manufacturing." +
                "\n\n\tWe are the pioneers of retail printers and billing solutions. We have a large presence in the retail automation space in select segments. We have introduced a lot of innovative products in the retail space which help the small time store owners as well as high-end supermarkets to support their customers. We have innovative business models suiting all kinds of consumers based on their requirements. We are known for our reliability and dependability in the market. We are a pan-India company. So our clients can seek our support across the country and we will be there to serve them.";
        AlertDialog.Builder PickUpTender = new AlertDialog.Builder(this);
        PickUpTender
                .setIcon(R.drawable.ic_launcher)
                .setTitle("About")
                .setMessage(strAboutMsg)
                .setNeutralButton("OK", null)
                .show();
    }

    // Download documents

    String FILENAME = "";

    void showOptions(View v) {
        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(this, btnHelp);
        droppyBuilder.addMenuItem(new DroppyMenuItem("Download Quick Start Guide"))
                .addMenuItem(new DroppyMenuItem("Download User Manual"))
                .addSeparator();
        droppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {
                switch (id) {
                    case 0:
                        FILENAME = "Quick_Start_Guide";
                        new GenerateDocuments().execute();
                        break;
                    case 1:
                        FILENAME = "User_Manual";
                        new GenerateDocuments().execute();
                        break;
                }
            }
        });
        droppyBuilder.build().show();
    }

    private String DOCUMENT_GENERATE_PATH = Environment.getExternalStorageDirectory().getPath() + "/EasyBill_Documents/";

    class GenerateDocuments extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        int progress = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = 0;
            pd.setMessage("Copying document...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            progress = 1;

            try {
                File directory = new File(DOCUMENT_GENERATE_PATH);
                if (!directory.exists())
                    directory.mkdirs();

                InputStream isAssetDbFile = getApplicationContext().getAssets().open( FILENAME + ".pdf");
                OutputStream osNewDbFile = new FileOutputStream(DOCUMENT_GENERATE_PATH + FILENAME + ".pdf");
                byte[] bFileBuffer = new byte[1024];
                int iBytesRead = 0;

                while ((iBytesRead = isAssetDbFile.read(bFileBuffer)) > 0) {
                    osNewDbFile.write(bFileBuffer, 0, iBytesRead);
                }

                osNewDbFile.flush();
                osNewDbFile.close();
                isAssetDbFile.close();
                pd.dismiss();
                publishProgress();

            } catch (FileNotFoundException e) {
                pd.dismiss();
                progress = 3;
                publishProgress();
                e.printStackTrace();
            } catch (IOException e) {
                pd.dismiss();
                progress = 4;
                publishProgress();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (progress == 1) {
                Toast.makeText(LoginActivity.this, "Document generated successfully! Path:" + DOCUMENT_GENERATE_PATH + FILENAME, Toast.LENGTH_LONG).show();
            } else if (progress == 3 || progress == 4){
                Toast.makeText(LoginActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(1 == progress )
            {
                if(!isPDFSupported(LoginActivity.this))
                {
                    MsgBox.Show("Error","No pdf reader found to open file");
                    return;
                }
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/EasyBill_Documents/" + FILENAME + ".pdf");
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file),"application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    // Instruct the user to install a PDF reader here, or something
                    Toast.makeText(LoginActivity.this, "Please install a PDF reader to open the document.", Toast.LENGTH_LONG).show();
                }
            }
            progress = 2;
            pd.dismiss();

        }
    }

    public static boolean isPDFSupported( Context context ) {
        Intent i = new Intent( Intent.ACTION_VIEW );
        final File tempFile = new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), "test.pdf" );
        i.setDataAndType( Uri.fromFile( tempFile ), "application/pdf" );
        return context.getPackageManager().queryIntentActivities( i, PackageManager.MATCH_DEFAULT_ONLY ).size() > 0;
    }

    // Login button event

    public void onLogin(View view) {

        boolean blockBilling = false;
        if(isPayPerUseModel.equalsIgnoreCase("Y") )
        {
            blockBilling = checkForBillUpload();

        }

        String userNameTxt = txtUserId.getText().toString();
        String userPassTxt = txtPassword.getText().toString();
        if(userNameTxt.equalsIgnoreCase("") || userPassTxt.equalsIgnoreCase(""))
        {
            MsgBox.Show("Login", "Please enter a Username & Password");
        }
        else
        {
            if(appCompatCheckBox.isChecked()){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userNameTxt",userNameTxt);
                editor.putBoolean("isChecked",true);
                editor.commit();
            }else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userNameTxt","");
                editor.putBoolean("isChecked",false);
                editor.commit();
            }
            Cursor User = dbLogin.getUser(userNameTxt,userPassTxt);
            if (User != null) {
                if (User.moveToFirst()) {
                    Intent intentHomeScreen = new Intent(this, HomeActivity.class);
                    Intent intentOwnerDetail = new Intent(this, OwnerDetailsActivity.class);
                    String userId = User.getString(User.getColumnIndex("UserId"));
                    String userName = User.getString(User.getColumnIndex("Name"));
                    String userRole = User.getString(User.getColumnIndex("RoleId"))+"";

                    ApplicationData.savePreference(this,ApplicationData.USER_ID,userId);
                    ApplicationData.savePreference(this,ApplicationData.USER_NAME,userName);
                    ApplicationData.savePreference(this,ApplicationData.USER_ROLE,userRole);

                    intentHomeScreen.putExtra(Constants.BLOCKBILLING,blockBilling);

                    startActivityForResult(intentHomeScreen, HOME_RESULT);
                    /*Cursor cursor = dbLogin.getAllBillDetail();
                    if(cursor!=null && cursor.moveToFirst())
                        startActivity(intentHomeScreen);
                    else
                        startActivity(intentOwnerDetail);*/

//                    finish();

                } else {
                    MsgBox.Show("Login", "Wrong user id or password");
                }
            } else {
                MsgBox.Show("Login", "Login DB Error");
            }
        }
    }

    boolean checkForBillUpload()
    {
        boolean result = false;
        result = uploadMeteringData();

        return result;
    }

    private boolean uploadMeteringData()
    {
        boolean result = false;
        try
        {
            Cursor cursor = dbLogin.getMeteringData();
            while (cursor!=null && cursor.moveToNext())
            {
                int totalInvoiceCount  = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_TotalInvoiceCount));
                int uploadedInvoiceCount  = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_UploadedInvoiceCount));
                long invoiceDate  = cursor.getLong(cursor.getColumnIndex("InvoiceDate"));
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(invoiceDate);
                String date = new SimpleDateFormat("dd-MM-yyyy").format(calendar1.getTime());
                Cursor cursor_owner = dbLogin.getOwnerDetail();
                if(cursor_owner!= null && cursor_owner.moveToNext())
                {
                    // String deviceid = cursor_owner.getString(cursor_owner.getColumnIndex("DeviceId"));
                    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    String deviceid = "00";
                    try{
                        deviceid = telephonyManager.getDeviceId();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        deviceid = "00";
                    }
                    // String deviceName = cursor_owner.getString(cursor_owner.getColumnIndex("DeviceName"));
                    String Email = cursor_owner.getString(cursor_owner.getColumnIndex("Email"));
                    String paramStr ="data="+deviceid+","+Email+","+date+","+(totalInvoiceCount-uploadedInvoiceCount)+",POS";
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (mWifi.isConnected()) {
                        // Do whatever
                        Log.d("Home Screen","wifi connected");
                        if (pd==null){
                            pd = new ProgressDialog(this);
                            pd.setIcon(R.mipmap.ic_company_logo);
                            pd.setTitle(Constants.processing);
                            pd.setMessage("Uploading Invoices...");
                            pd.setCancelable(false);
                            pd.show();
                            upload_counter = 0;
                        }
                        upload_counter++;
                        first_response = true;
                        new HTTPAsyncTask(LoginActivity.this,HTTPAsyncTask.HTTP_GET,"",Upload_Invoice_Count, Config.Upload_No_of_Invoices+paramStr).execute();
                    }else
                    {
                        if(SubscriptionBillUploadUtility.checkForBillsCountToUpload(dbLogin)){
                            System.out.println("richa : sys should be blocked");
                            result = true;
                        }else
                        {
                            System.out.print("richa : sys is ok ");
                        }
                        Log.d(TAG,"wifi not connected");
                    }
                    cursor_owner.close();
                }
                else
                {
                    Log.d(TAG, "Cannot upload invoices count due to insufficient owners details");
                }

            }
            cursor.close();

        }catch (Exception e)
        {
            result = false;
            e.printStackTrace();
        }finally
        {
            return result;
        }
    }

    // Close button event
    public void Close(View v) {
        // Close Database connection
        dbLogin.CloseDatabase();

        // Close the application
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == HOME_RESULT) {
                if(resultCode == RESULT_OK)
                {
                    String BillsUploadedStatus = data.getStringExtra("BillsUploadedStatus");
                    if(BillsUploadedStatus.equalsIgnoreCase("Pending"))
                    {
                        MessageDialog msg = new MessageDialog(this);
                        msg.setIcon(R.mipmap.ic_company_logo)
                                .setTitle("Locked")
                                .setMessage("Kindly connect to wi-fi to upload bill count")
                                .setPositiveButton("OK", null)
                                .setCancelable(false)
                                .show();
                    }
                }
            } else {
                this.txtUserId.setText("");
                this.txtPassword.setText("");
            }
        }
    }

    public void onHttpRequestComplete(int requestCode, String data) {
        //progressDialog.dismiss();
        if (data != null) {
            try{
                if (upload_counter == 1)
                {
                    pd.dismiss();
                    pd = null;
                }
                else
                    upload_counter--;
                if (requestCode == Upload_Invoice_Count) // Upload_invoice count for metering
                {
                    if(data.contains("\"success\":true,\"message\":\"Data Updated Successfully\""))
                    {
                        String [] recvdData = data.split(":");
                        try {
                            String datenCount = recvdData[3];
                            String[] dataa = datenCount.split(",");
                            String[] date = dataa[0].split("\"");
                            String[] cc = dataa[1].split("\"");
                            int count = Integer.parseInt(cc[0]);
                            //Toast.makeText(myContext, "No of invoices uploaded sucessfully.", Toast.LENGTH_SHORT).show();
                            Date dd = new SimpleDateFormat("dd-MM-yyyy").parse(date[1]);
                            Cursor cursor = dbLogin.getMeteringDataforDate(""+dd.getTime());
                            if(cursor!=null && cursor.moveToFirst() && first_response)
                            {
                                int uploadedInvoiceCount = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_UploadedInvoiceCount));
                                dbLogin.updateMeteringDataforDate_uploadedInvoiceCount(""+dd.getTime(),uploadedInvoiceCount+count);
                                first_response = false;
                            }
                            Log.d(TAG,date[1]+": No of invoices uploaded : "+count);

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if (data.contains("\"success\":false,\"message\":\"Check Parameters\""))
                    {
                        //Toast.makeText(myContext, "No of invoices uploading failed.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"No of invoices uploading failed.");
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error due to " + e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Sending error", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(this);
            AuthorizationDialog
                    .setTitle("Are you sure you want to exit ?")
                    .setIcon(R.drawable.ic_launcher)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dbLogin.CloseDatabase();
                            finish();
                        }
                    })
                    .show();
        }

        return super.onKeyDown(keyCode, event);
    }

    /*public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }*/

    @Override
    public void onHomePressed()
    {}
}
