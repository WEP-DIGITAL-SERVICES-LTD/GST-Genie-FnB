/****************************************************************************
 * Project Name		:	VAJRA
 * <p/>
 * File Name		:	ItemManagementActivity
 * <p/>
 * Purpose			:	Represents Item creation activity, takes care of all
 * UI back end operations in this activity, such as event
 * handling data read from or display in views.
 * <p/>
 * DateOfCreation	:	20-November-2012
 * <p/>
 * Author			:	Balasubramanya Bharadwaj B S
 ****************************************************************************/
package com.wepindia.pos.views.Masters.OutwardItemMaster;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.ItemMasterBean;
import com.wep.common.app.models.ItemModel;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.FilePickerDialogFragment;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.GenericClasses.OnFilePickerClickListener;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.EMOJI_FILTER;
import com.wepindia.pos.utils.ExportDatabaseTableAsCSVTask;
import com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners.OnItemListetner;
import com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners.SimpleCursorRecyclerAdapter;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wepindia.pos.Constants.FRAGMENTNAME;


public class ItemManagementActivity extends WepBaseActivity implements OnItemListetner, OnFilePickerClickListener {

    String strUserName = "";
    private Toolbar toolbar;

    private static final String TAG = ItemManagementActivity.class.getSimpleName();
    private String CSV_GENERATE_PATH = Environment.getExternalStorageDirectory().getPath() + "/WeP_FnB_CSVs/";
    private String FILENAME = "Sample_OutwardItem.csv";

    Context myContext;
    MessageDialog MsgBox;
    static SimpleCursorRecyclerAdapter adapter;

    LoadItemsAsync loadItemsAsync = null;
    String tx = "";
    String linefeed = "";
    boolean itemsearchclicked = false, brandCategDeptSearchClicked = false;
    String[] from = {DatabaseHandler.KEY_ItemName, DatabaseHandler.KEY_Quantity, DatabaseHandler.KEY_UOM, DatabaseHandler.KEY_DineInPrice1, DatabaseHandler.KEY_IGSTRate};
    int[] to = new int[]{R.id.tv_item_master_list_title_name, R.id.tv_item_master_list_title_qty,
            R.id.tv_item_master_list_title_uom,
            R.id.tv_item_master_list_title_retail, R.id.tv_item_master_list_title_disc, R.id.tv_item_master_list_title_active};
    ProgressDialog progressDialog;

    private long lastClickTime = 0;
    private BufferedReader buffer;
    private int counter = 0;

    @BindView(R.id.bt_item_master_list_add)
    Button btItemMasterAdd;
    @BindView(R.id.rv_item_master_list_items)
    RecyclerView rv_list;
    @BindView(R.id.bt_item_master_list_count)
    Button btnItemCount;
    @BindView(R.id.autocomplete_item_master_list_search)
    AutoCompleteTextView actv_itemsearch;
    @BindView(R.id.autocomplete_item_search_brand_cat_dept)
    AutoCompleteTextView actv_BrandCategDeptSearch;
    @BindView(R.id.iv_item_master_list_refresh)
    ImageView iv_refresh;
    @BindView(R.id.iv_item_master_list_delete)
    ImageView iv_delete;

    @BindView(R.id.bt_item_master_list_generate_csv)
    Button btnGenerateCSV;
    @BindView(R.id.bt_item_master_list_upload_file)
    Button btnUploadCSV;

    @BindView(R.id.bt_item_master_list_clear) Button btnClear;

    @BindView(R.id.rb_item_master_list_all)
    RadioButton rb_displayAll;
    @BindView(R.id.rb_item_master_list_brand)
    RadioButton rb_displayBrandwise;
    @BindView(R.id.rb_item_master_list_department)
    RadioButton rb_displayDepartmentwise;
    @BindView(R.id.rb_item_master_list_category)
    RadioButton rb_displayCategorywise;
    @BindView(R.id.rb_item_master_list_active)
    RadioButton rb_displayActiveItems;
    @BindView(R.id.rb_item_master_list_inactive)
    RadioButton rb_displayInactiveItems;
    @BindView(R.id.rb_item_master_list_min_stock)
    RadioButton rb_displayMinimumStockItems;
    @BindView(R.id.rg_item_master_list)
    RadioGroup rg_displayItemCriteria;
    @BindView(R.id.ll_itemMaster)  LinearLayout ll_itemMasters;
    @BindView(R.id.tv_item_master_list_file_path) TextView tv_filePath;

    @BindView(R.id.tv_item_master_list_title_mode) TextView tvMode;

    String strUploadFilepath = "";
    private String mUserCSVInvalidValue = "";
    private boolean mFlag;
    private Map<Integer, ItemModel> mHashMapItemCode = new TreeMap<>();
    private int mCheckCSVValueType;
    private final int CHECK_INTEGER_VALUE = 0;
    private final int CHECK_DOUBLE_VALUE = 1;
    private final int CHECK_STRING_VALUE = 2;
    private ArrayList<Integer> mKitchenCodeList = new ArrayList<>();
    private ArrayList<Integer> mDepartmentCodeList = new ArrayList<>();
    private Map<Integer, Integer> mCategoryCodeList = new LinkedHashMap<>();
    HashMap<Integer,String> departmentHashMapList;
    HashMap<Integer,String> categoryHashMapList;
    HashMap<Integer,String> kitchenHashMapList;

    String businessDate = "";

    private int mMenuCode = -1;
    private String mItemShortName = "";
    private String mItemLongName = "";
    private String mSupplyType = "";
    private double mRate1 = 0.00;
    private double mRate2 = 0.00;
    private double mRate3 = 0.00;
    private double mQuantity = 0.00;
    private double mMinimumStock = 0.00;
    private String mUOM = "";
    private double mCGSTRate = 0.00;
    private double mSGSTRate = 0.00;
    private double mIGSTRate = 0.00;
    private double mCESSRate = 0.00;
    private double mCESSAmount = 0.00;
    private double mAdditionalCESSAmount = 0.00;
    private double mPurchaseRate = 0.00;
    private double mDiscountPercent = 0.00;
    private double mDiscountAmount = 0.00;
    private int mDeptCode = 0;
    private int mCategCode = 0;
    private int mKitchenCode = 0;
    private String mbarCode = "";
    private String mImageUri = "";
    private String mHSN = "";
    private int mFav = -1;
    private int mActive = -1;

    public static ProgressBar pb;
    private int itemCount = 0;
    private int deleteOption = 0;

    private DatabaseHandler dbItems;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.item_masters_outward);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        myContext = this;
        MsgBox = new MessageDialog(myContext);

        strUserName = getIntent().getStringExtra("USER_NAME");
        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        com.wep.common.app.ActionBarUtils.setupToolbar(ItemManagementActivity.this,toolbar,getSupportActionBar(),"Outward Items Master",strUserName," Date:"+s.toString());
        try {
            dbItems = new DatabaseHandler(myContext);
            dbItems.CreateDatabase();
            dbItems.OpenDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            initSettingsData();
            displayItemList();
            loadAutocompleteData();
            clickEvent();
            applyValidations();
            setAllDepartmentCode();
            setAllKitchenCode();
            populateHashMapData();
            mClear();
            Cursor cursor = dbItems.getBusinessDate();
            if(cursor!=null && cursor.moveToNext())
            {
                businessDate = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_BusinessDate));
            }else
            {
                Date date = new Date();
                businessDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAllDepartmentCode() {
        try {
            Cursor crsrDept = dbItems.getAllDepartments();
            while (crsrDept != null && crsrDept.moveToNext()) {
                mDepartmentCodeList.add(crsrDept.getInt(crsrDept.getColumnIndex(DatabaseHandler.KEY_DepartmentCode)));
            }

            Cursor crsrCategory = dbItems.getAllCategory();
            while (crsrCategory != null && crsrCategory.moveToNext()) {
                mCategoryCodeList.put(crsrCategory.getInt(crsrCategory.getColumnIndex(DatabaseHandler.KEY_CategoryCode)),
                        crsrCategory.getInt(crsrCategory.getColumnIndex(DatabaseHandler.KEY_DepartmentCode)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAllKitchenCode(){
        try {
            Cursor crsrBrand = dbItems.getAllKitchen();
            while (crsrBrand != null && crsrBrand.moveToNext()) {
                mKitchenCodeList.add(crsrBrand.getInt(crsrBrand.getColumnIndex(DatabaseHandler.KEY_KitchenCode)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void applyValidations()
    {
        actv_itemsearch.setFilters(new InputFilter[]{new EMOJI_FILTER()});
        actv_BrandCategDeptSearch.setFilters(new InputFilter[]{new EMOJI_FILTER()});
    }

    void clickEvent() {
        actv_itemsearch.addTextChangedListener(textWatcher);
        actv_itemsearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                actv_BrandCategDeptSearch.setText("");
                String data = actv_itemsearch.getText().toString();
                itemsearchclicked = true;
                brandCategDeptSearchClicked = false;

                String[] parts = data.split("-");
                final String shortCode = parts[0].trim();
                final String itemShortName = parts[1].trim();
                final String itemBarcode = parts[2].trim();
                btItemMasterAdd.setEnabled(false);
                btItemMasterAdd.setTextColor(getResources().getColor(R.color.grey));

                try {
                    if(loadItemsAsync == null)
                    {
                        loadItemsAsync = new LoadItemsAsync();
                        loadItemsAsync.execute();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }
        });
        actv_BrandCategDeptSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                actv_itemsearch.setText("");
                brandCategDeptSearchClicked = true;
                itemsearchclicked = false;

                btItemMasterAdd.setEnabled(false);
                btItemMasterAdd.setTextColor(getResources().getColor(R.color.grey));

                try {
                    if(loadItemsAsync == null)
                    {
                        loadItemsAsync = new LoadItemsAsync();
                        loadItemsAsync.execute();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }
        });
    }

    @OnClick({R.id.rb_item_master_list_all, R.id.rb_item_master_list_brand, R.id.rb_item_master_list_department, R.id.rb_item_master_list_category,
            R.id.rb_item_master_list_active, R.id.rb_item_master_list_inactive, R.id.rb_item_master_list_min_stock})
    public void onRadioButtonClicked(RadioButton radioButton) {
        switch (rg_displayItemCriteria.getCheckedRadioButtonId()) {
            case R.id.rb_item_master_list_all:
                tvMode.setText(R.string.mode);
                actv_BrandCategDeptSearch.setVisibility(View.INVISIBLE);
                break;
            case R.id.rb_item_master_list_brand:
                tvMode.setText(R.string.kitchen);
                actv_BrandCategDeptSearch.setVisibility(View.VISIBLE);
                actv_BrandCategDeptSearch.setHint("Kitchen");
                actv_BrandCategDeptSearch.setText("");
                break;
            case R.id.rb_item_master_list_department:
                tvMode.setText(R.string.department);
                actv_BrandCategDeptSearch.setVisibility(View.VISIBLE);
                actv_BrandCategDeptSearch.setHint("Department");
                actv_BrandCategDeptSearch.setText("");
                break;
            case R.id.rb_item_master_list_category:
                tvMode.setText(R.string.category);
                actv_BrandCategDeptSearch.setVisibility(View.VISIBLE);
                actv_BrandCategDeptSearch.setHint("Category");
                actv_BrandCategDeptSearch.setText("");
                break;
            case R.id.rb_item_master_list_active:
                tvMode.setText(R.string.mode);
                actv_BrandCategDeptSearch.setVisibility(View.INVISIBLE);
                break;
            case R.id.rb_item_master_list_inactive:
                tvMode.setText(R.string.mode);
                actv_BrandCategDeptSearch.setVisibility(View.INVISIBLE);
                break;
            case R.id.rb_item_master_list_min_stock:
                tvMode.setText(R.string.mode);
                actv_BrandCategDeptSearch.setVisibility(View.INVISIBLE);
                break;
            default:
                tvMode.setText(R.string.mode);
                break;
        }
        brandCategDeptSearchClicked = false;
        loadAutocompleteDataForBrandCategDept();
        displayItemList();
    }

    @OnClick({R.id.bt_item_master_list_add, R.id.iv_item_master_list_refresh,
            R.id.bt_item_master_list_clear,R.id.bt_item_master_list_generate_csv,
            R.id.bt_item_master_list_browse_file, R.id.bt_item_master_list_upload_file,
            R.id.bt_item_master_list_import_csv, R.id.iv_item_master_list_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_item_master_list_add:
                /*pb.setIndeterminate(true);
                pb.setVisibility(View.VISIBLE);
                ll_itemMasters.setVisibility(View.GONE);*/
                FragmentManager fm = getSupportFragmentManager();
                ItemMasterAddItemDialogFragment myFragment =
                        (ItemMasterAddItemDialogFragment)fm.findFragmentByTag("Add new Item");
                if (myFragment != null && myFragment.isVisible()) {
                    return;
                }


                ItemMasterAddItemDialogFragment itemMasterAddItemDialogFragment = new ItemMasterAddItemDialogFragment();
                itemMasterAddItemDialogFragment.mInitListener(this);
                itemMasterAddItemDialogFragment.show(fm, "Add new Item");
                break;
            case R.id.bt_item_master_list_generate_csv:
                generateCSV();
                break;
            case R.id.bt_item_master_list_import_csv:
                if (itemCount == 0) {
                    Toast.makeText(myContext, "Please first add some item to export.", Toast.LENGTH_SHORT).show();
                } else {
                    generateExportCSV();
                }
                break;
            case R.id.bt_item_master_list_clear : mClear();
                break;
            case R.id.bt_item_master_list_browse_file:
                browseFile();
                break;
            case R.id.bt_item_master_list_upload_file:
                uploadCSV();
                break;
            case R.id.iv_item_master_list_refresh:
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }
                mClear();
                loadAutocompleteData();
                displayItemList();
                break;
            case R.id.iv_item_master_list_delete:
                if (deleteOption == 1) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    ItemDeleteFragment itemDelete =
                            (ItemDeleteFragment) fragmentManager.findFragmentByTag("Delete Item");
                    if (itemDelete != null && itemDelete.isVisible()) {
                        return;
                    }

                    ItemDeleteFragment itemDeleteFragment = new ItemDeleteFragment();
                    itemDeleteFragment.mInitListener(this);
                    itemDeleteFragment.show(fragmentManager, "Delete Item");
                } else {
                    Toast.makeText(myContext, "Delete Option is disabled. Go to Settings to enable it.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public void loadAutocompleteDataForBrandCategDept() {
        new AsyncTask<Void, Void, Void>() {
            ArrayList<String> search_list = new ArrayList<>();
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(myContext);
                pd.setIcon(R.mipmap.ic_company_logo);
                pd.setTitle(Constants.processing);
                pd.setMessage("Loading Brand, Category or Department search data. Please wait...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (tvMode.getText().toString().trim().equalsIgnoreCase("Kitchen"))
                    search_list = dbItems.getAllKitchen_for_autocomplete();
                else if(tvMode.getText().toString().trim().equalsIgnoreCase("Department"))
                    search_list = dbItems.getAllDepartment_for_autocomplete();
                else if (tvMode.getText().toString().trim().equalsIgnoreCase("Category"))
                    search_list = dbItems.getAllCategory_for_autocomplete();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pd.dismiss();
                ArrayAdapter<String> dataAdapter_actv = new ArrayAdapter<String>(myContext, android.R.layout.simple_list_item_1, search_list);
                dataAdapter_actv.setDropDownViewResource(android.R.layout.simple_list_item_1);
                actv_BrandCategDeptSearch.setAdapter(dataAdapter_actv);

            }

        }.execute();
    }

    void uploadCSV(){
        try {
            if (strUploadFilepath.equalsIgnoreCase("")) {
                Toast.makeText(myContext, "No File Found", Toast.LENGTH_SHORT).show();
            } else {
                String path = strUploadFilepath;
                       /* FileInputStream inputStream = new FileInputStream(path);
                        buffer = new BufferedReader(new InputStreamReader(inputStream));*/
                buffer = new BufferedReader(
                        new InputStreamReader(new FileInputStream(path), "ISO-8859-1"));
                setCSVFileToDB(itemCount);
            }
        } catch (IOException e) {
            Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            mClear();

        }
    }

    private void setCSVFileToDB(int itemCount) {

        if (itemCount > 0) {
            showCSVAlertMessage();
        } else {
            downloadCSVData();
        }
    }

    private void showCSVAlertMessage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                .setTitle("Replace Item")
                .setIcon(R.mipmap.ic_company_logo)
                .setMessage(" Are you sure you want to Replace all the existing Items and their linkage to the suppliers, if any")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbItems.deleteSupplierItemLinkage();
                        downloadCSVData();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //ClearItemTable();
                        displayItemList();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void browseFile()
    {
        FragmentManager fm1 = getSupportFragmentManager();
        FilePickerDialogFragment myFragment1 =
                (FilePickerDialogFragment)fm1.findFragmentByTag("File Picker");
        if (myFragment1 != null && myFragment1.isVisible()) {
            return;
        };
        Bundle bundle = new Bundle();
        bundle.putString("contentType","csv");
        bundle.putString(FRAGMENTNAME,TAG);

        FilePickerDialogFragment frag = new FilePickerDialogFragment();
        frag.setArguments(bundle);
        frag.mInitListener(ItemManagementActivity.this);
        frag.show(fm1, "File Picker");
    }

    public void generateExportCSV(){
        File temp = new File(CSV_GENERATE_PATH + "ExportItemsFromDatabase.csv");
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                .setIcon(R.mipmap.ic_company_logo)
                .setTitle("Export CSV Alert")
                .setMessage("Are you sure to export existing item database into csv file.")
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ExportDatabaseTableAsCSVTask exportDatabaseTableAsCSVTask =
                                ExportDatabaseTableAsCSVTask.getInstance(myContext, dbItems, 0);
                        exportDatabaseTableAsCSVTask.execute(DatabaseHandler.TBL_ITEM_Outward);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

        /*if (!temp.exists())
            new GenerateCSV().execute();
        else
            alert.show();*/
    }

    public void generateCSV() {
        File temp = new File(CSV_GENERATE_PATH + FILENAME);

        AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                .setIcon(R.mipmap.ic_company_logo)
                .setTitle("Overwrite Alert")
                .setMessage("There already exists a CSV file, regenerating a " +
                        "CSV file will overwrite the existing one. " +
                        "Are you sure you want to overwrite the file?")
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new GenerateCSV().execute();
                    }
                });
        AlertDialog alert = builder.create();

        if (!temp.exists())
            new GenerateCSV().execute();
        else
            alert.show();
    }

    @Override
    public void filePickerSuccessClickListener(String absolutePath) {
        strUploadFilepath = absolutePath;
        tv_filePath.setText(strUploadFilepath.substring(strUploadFilepath.lastIndexOf("/")+1));
    }

    class GenerateCSV extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd = new ProgressDialog(myContext);
        int progress = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = 0;
            pd.setIcon(R.mipmap.ic_company_logo);
            pd.setTitle(Constants.processing);
            pd.setMessage("Generating sample csv. Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            progress = 1;

            try {
                File directory = new File(CSV_GENERATE_PATH);
                if (!directory.exists())
                    directory.mkdirs();

                InputStream isAssetDbFile = myContext.getAssets().open(FILENAME);
                OutputStream osNewDbFile = new FileOutputStream(CSV_GENERATE_PATH + FILENAME);
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
                Toast.makeText(myContext, "CSV generated successfully! Path:" + CSV_GENERATE_PATH + FILENAME, Toast.LENGTH_LONG).show();
            } else if (progress == 3 || progress == 4) {
                Toast.makeText(myContext, "Error occurred!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress = 2;
            pd.dismiss();
        }
    }

    private void initSettingsData(){
        Cursor crsrSettings = null;
        try {
            crsrSettings = dbItems.getBillSettings();
            if (crsrSettings != null && crsrSettings.moveToFirst()) {
//                deleteOption = crsrSettings.getInt(crsrSettings.getColumnIndex(DatabaseHandler.KEY_DeleteOption));
                deleteOption = 1;
            }
        } catch (Exception e) {
            Log.i(TAG,"Settings init() error on billing screen. " +e.getMessage());
        }finally {
            if(crsrSettings != null){
                crsrSettings.close();
            }
        }
    }

    /**
     * Download all data in Background then it will be return to UI
     */

    private void downloadCSVData() {
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(ItemManagementActivity.this);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                readCSVValue();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    if (mFlag) {
                        MsgBox.Show("Note", mUserCSVInvalidValue);
                    }else if(mUserCSVInvalidValue.equals(""))
                    {
                        Toast.makeText(getApplicationContext(), "Items Imported Successfully", Toast.LENGTH_LONG).show();
                    }
                    displayItemList();
                    loadAutocompleteData();
                    mClear();
                    //ClearItemTable();

                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public void loadAutocompleteData() {
        new AsyncTask<Void, Void, Void>() {
            ArrayList<String> list_actv = new ArrayList<>();
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(myContext);
                pd.setIcon(R.drawable.ic_launcher);
                pd.setTitle(Constants.processing);
                pd.setMessage("Loading Items search data. Please wait...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                list_actv = dbItems.getAllItems_for_autocomplete();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pd.dismiss();
                ArrayAdapter<String> dataAdapter_actv = new ArrayAdapter<String>(myContext, android.R.layout.simple_list_item_1, list_actv);
                dataAdapter_actv.setDropDownViewResource(android.R.layout.simple_list_item_1);
                actv_itemsearch.setAdapter(dataAdapter_actv);

            }

        }.execute();
    }

    private void readCSVValue() {

        mUserCSVInvalidValue = "";
        String checkUOMTypye = "BAG, BAL, BDL, BKL, BOU, BOX, BTL, BUN, CAN, CBM, CCM, CMS, CTN, DOZ, DRM, GGR, GMS, GRS, " +
                "GYD, KGS, KLR, KME, LTR, MLT, MTR, NOS, PAC, PCS, PRS, QTL, ROL, SET, SQF, SQM, SQY, TBS, TGM, THD, TON, TUB, UGS, UNT, YDS, OTH";
        String[] checkSupplyType = {"G", "S"};
        String csvHeading = "MENU CODE,ITEM SHORT NAME,ITEM LONG NAME,SUPPLY TYPE,RATE 1,RATE 2,RATE 3,QUANTITY,UOM,CGST RATE,SGST RATE,IGST RATE,cess RATE,CESS AMOUNT,ADDITIONAL CESS AMOUNT,DISCOUNT PERCENT,DEPARTMENT CODE,CATEGORY CODE,KITCHEN CODE,HSN,BAR CODE,MINIMUM STOCK,ACTIVE";
        boolean flag;
        boolean   mCSVHashCheckflag = false;
        try {
            String line;
            String chechCSVHeaderLine;
            chechCSVHeaderLine = buffer.readLine();


            mFlag = false;

            flag = csvHeading.equalsIgnoreCase(chechCSVHeaderLine);

            if (!flag) {
                mFlag = true;
                mUserCSVInvalidValue = getResources().getString(R.string.header_value_empty) + "\n"+csvHeading;
                //+ "MENU CODE,ITEM NAME,SUPPLY TYPE,RATE 1,RATE 2,RATE 3,QUANTITY,UOM,CGST RATE,SGST RATE,IGST RATE,cess RATE,DISCOUNT PERCENT";
                return;
            }

            //dataList.clear();
            mHashMapItemCode.clear();

            while ((line = buffer.readLine()) != null) {
                if (line.length() == 22)
                    continue;

                final String[] colums = line.split(",", -1);
                // final String[] colums = line.split("\\r\\n\\r\\n", 15);
                if (colums.length == 0)
                    continue;
                else if (colums.length != 23) {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.insufficient_information);
                    break;
                }

                mItemShortName = "";
                mItemLongName = "";
                mMenuCode = 0;
                mSupplyType = "";
                mUOM = "";
                mRate1 = 0.00;
                mRate2 = 0.00;
                mRate3 = 0.00;
                mCGSTRate = 0.00;
                mSGSTRate = 0.00;
                mIGSTRate = 0.00;
                mCESSRate = 0.00;
                mCESSAmount = 0.00;
                mAdditionalCESSAmount = 0.00;
                mPurchaseRate = 0.00;
                mHSN = "";
                mQuantity = 0.00;
                mMinimumStock = 0.00;
                mbarCode = "";
                mKitchenCode = -1;
                mDeptCode = -1;
                mCategCode = -1;
                mFav = 0;
                mActive = 0;
                mImageUri = "";

                // MenuCode
                if (colums[0] != null &&  colums[0].trim().length() > 0)
                {
                    mCheckCSVValueType = checkCSVTypeValue(colums[0], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {
                            mMenuCode = Integer.parseInt(colums[0]);
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.item_short_code_invalid);
                        break;
                    }
                }

                // Item short name
                if (colums[1] != null &&  colums[1].trim().length() > 0)
                {
                    mCheckCSVValueType = checkCSVTypeValue(colums[1], "String");
                    if (mCheckCSVValueType == CHECK_STRING_VALUE) {
                        if (colums[1].length() > 20)
                            mItemShortName = colums[1].substring(0, 21).toUpperCase();
                        else
                            mItemShortName = colums[1].trim();
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.item_short_name_invalid) + "" + counter;
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.item_short_name_empty) + "" + counter;
                    break;
                }

                // Item long name
                if (colums[2] != null && colums[2].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[2], "String");
                    if (mCheckCSVValueType == CHECK_STRING_VALUE) {
                        if (colums[1].length() > 32)
                            mItemLongName = colums[2].substring(0, 32).toUpperCase();
                        else
                            mItemLongName = colums[2].trim();
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.item_long_name_invalid) + "" + counter;
                        break;
                    }
                } else {
                    mItemLongName = mItemShortName;
                }

               /* // Short code
                if (colums[2] != null && colums[2].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[2], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {
                        mMenuCode = Integer.parseInt(colums[2]);
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.short_code_invalid) + colums[0];
                        break;
                    }
                }*/

                // Supply Type
                if (colums[3] != null  && colums[3].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[3], "String");
                    if (mCheckCSVValueType == CHECK_STRING_VALUE) {
                        mSupplyType = colums[3];
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.supply_type_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.supply_type_empty) + colums[1];
                    break;
                }

                // Rate 1
                if (colums[4] != null &&  colums[4].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[4], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[4], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mRate1 = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[4])));
                        if (!(mRate1 >= 0 && mRate1 <= 999999.99)) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter Retail Price between 0 and 999999.99 for item " + colums[0];
                            break;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.retail_price_invalid) + colums[0];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.retail_price_empty) + colums[0];
                    break;
                }

                // Rate 2
                if (colums[5] != null &&  colums[5].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[5], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[5], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mRate2 = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[5])));
                        if (!(mRate2 >= 0 && mRate2 <= 999999.99)) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter MRP between 0 and 999999.99 for item " + colums[0];
                            break;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.mrp_invalid) + colums[0];
                        break;
                    }
                }

                // Rate 3
                if (colums[6] != null &&  colums[6].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[6], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[6], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mRate3 = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[6])));
                        if (!(mRate3 >= 0 && mRate3 < 999999.99)) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter Whole Sale Price between 0 and 999999.99 for item " + colums[0];
                            break;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.whole_sale_price_invalid) + colums[0];
                        break;
                    }
                }

                // Quantity
                if (colums[7] != null &&  colums[7].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[7], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[7], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mQuantity = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[7])));
                        if (mQuantity > 999999.99 || mQuantity < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter Quantity between 0 and 99.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.quantity_invalid) + colums[1];
                        break;
                    }
                }

                // UOM
                if (colums[8] != null  && colums[8].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[7], "String");
                    if (mCheckCSVValueType == CHECK_STRING_VALUE) {

                        if (colums[8].trim().length() == 3 && checkUOMTypye.contains(colums[8])) {
                            mUOM = colums[8];
                        } else {
                            mFlag = true;
                            mUserCSVInvalidValue = getResources().getString(R.string.uom_invalid) + colums[1] + "e.g " + checkUOMTypye;
                            break;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.uom_invalid) + colums[1];
                        break;
                    }
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.uom_empty) + colums[1];
                    break;
                }

                // CGST Rate
                if (colums[9] != null &&  colums[9].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[9], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[9], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mCGSTRate = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[9])));
                        if (mCGSTRate > 99.99 || mCGSTRate < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter CGST Rate between 0 and 99.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.cgst_rate_invalid) + colums[1];
                        break;
                    }
                }

                // SGST Rate
                if (colums[10] != null && colums[10].length() > 0 && colums[10].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[10], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[10], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mSGSTRate = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[10])));
                        if (mSGSTRate > 99.99 || mSGSTRate < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter SGST Rate between 0 and 99.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.sgst_rate_invalid) + colums[1];
                        break;
                    }
                }

                if (mCGSTRate + mSGSTRate < 0 || mCGSTRate + mSGSTRate > 99.99) {
                    mFlag = true;
                    mUserCSVInvalidValue = "Please note sum of SGST Rate and CGST Rate should between 0 and 99.99 for item " + colums[0];
                    return;
                }

                // IGST Rate
                if (colums[11] != null && colums[11].length() > 0 && colums[11].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[11], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[11], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mIGSTRate = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[11])));
                        if (mIGSTRate > 99.99 || mIGSTRate < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter IGST Rate between 0 and 99.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.igst_rate_invalid) + colums[1];
                        break;
                    }
                }

                // CESS Rate
                if (colums[12] != null && colums[12].length() > 0 && colums[12].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[12], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[12], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mCESSRate = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[12])));
                        if (mCESSRate > 999.99 || mCESSRate < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter cess Rate between 0 and 999.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.cess_rate_invalid) + colums[1];
                        break;
                    }
                }

                // CESS Amount
                if (colums[13] != null && colums[13].length() > 0 && colums[13].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[13], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[13], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mCESSAmount = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[13])));
                        if (mCESSAmount > 999999.99 || mCESSAmount < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter cess amount between 0 and 999999.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.cess_amount_invalid) + colums[1];
                        break;
                    }
                }

                // Additional CESS Amount
                if (colums[14] != null && colums[14].length() > 0 && colums[14].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[14], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[14], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mAdditionalCESSAmount = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[14])));
                        if (mAdditionalCESSAmount > 999999.99 || mAdditionalCESSAmount < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter additional cess amount between 0 and 999999.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.add_cess_amount_invalid) + colums[1];
                        break;
                    }
                }

                // Discount Percent
                if (colums[15] != null && colums[15].length() > 0 && colums[15].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[15], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[15], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mDiscountPercent = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[15])));
                        if (mDiscountPercent > 999999.99 || mDiscountPercent < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter discount percent between 0 and 999999.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.discount_invalid) + colums[1];
                        break;
                    }
                }

                // Department Code
                if (colums[16] != null && colums[16].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[16], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {
                        if (mDepartmentCodeList != null && mDepartmentCodeList.size() > 0) {
                            if (mDepartmentCodeList.contains(Integer.parseInt(colums[16]))) {
                                mDeptCode = Integer.parseInt(colums[16]);
                            } else {
                                mFlag = true;
                                mUserCSVInvalidValue = "This department code " + colums[16] + " is not present in the database for Item Name " + colums[1];
                                break;
                            }
                        } else {
                            mFlag = true;
                            mUserCSVInvalidValue = getResources().getString(R.string.database_department_null);
                            break;
                        }

                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.department_code_invalid) + " " + colums[1];
                        break;
                    }
                } else {
                    mDeptCode = -1;
                    mCategCode = -1;
                }

                // Category Code
                boolean categoryFlag = false;
                if (colums[17] != null && colums[17].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[17], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {

                        if (mCategoryCodeList != null && mCategoryCodeList.size() > 0) {

                            if (!(mDeptCode != 0 && mDeptCode!=-1)) {
                                mFlag = true;
                                mUserCSVInvalidValue = "Item Short Name " + mItemShortName + " can not have category code " + colums[17] + " without department";
                                break;
                            }

                            if (mCategoryCodeList.containsKey(Integer.parseInt(colums[17]))) {
                                categoryFlag = true;
                            } else {
                                mFlag = true;
                                mUserCSVInvalidValue = "This category code " + colums[17] + " is not present in the database for Item Name " + colums[1];
                                break;
                            }

                            if (categoryFlag) {

                                if (checkDeptandCategoryTogether(mDeptCode, Integer.parseInt(colums[17])) != 0) {
                                    mCategCode = Integer.parseInt(colums[17]);
                                } else {
                                    mFlag = true;
                                    mUserCSVInvalidValue = "Category code " + colums[17] + " is not linked to department code " + mDeptCode + " for Item Name " + mItemShortName
                                            + "  in category tab of configurations feature";
                                    break;
                                }

                            }
                        } else {
                            mFlag = true;
                            mUserCSVInvalidValue = getResources().getString(R.string.database_category_null);
                            break;
                        }

                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.category_code_invalid) + " " + colums[1];
                        break;
                    }

                } else {
                    mCategCode = -1;
                }

                // Kitchen Code
                if (colums[18] != null && colums[18].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[18], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {
                        if (mKitchenCodeList != null && mKitchenCodeList.size() > 0) {
                            if (mKitchenCodeList.contains(Integer.parseInt(colums[18]))) {
                                mKitchenCode = Integer.parseInt(colums[18]);
                            } else {
                                mFlag = true;
                                mUserCSVInvalidValue = "This kitchen code " + colums[18] + " is not present in the database for Item Name " + colums[1];
                                break;
                            }
                        } else {
                            mFlag = true;
                            mUserCSVInvalidValue = getResources().getString(R.string.database_kitchen_null);
                            break;
                        }

                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.kitchen_code_invalid) + " " + colums[1];
                        break;
                    }
                } else {
                    mKitchenCode = -1;
                }

                // HSN
                if (colums[19] != null && colums[19].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[19], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {
                        if (mHSN.length() > 10) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter HSN between 0 and 999999999 for item " + colums[1];
                            return;
                        }
                        mHSN = colums[19];
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.hsn_invalid) + colums[1];
                        break;
                    }
                }

                // Barcode
                if (colums[20] != null && colums[20].trim().length() > 0) {
                    mbarCode = colums[20];
                }

                // Minimum Stock
                if (colums[21] != null && colums[21].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[21], "Double");
                    int mCheckCSVValueType1 = checkCSVTypeValue(colums[21], "Int");
                    if (mCheckCSVValueType == CHECK_DOUBLE_VALUE || mCheckCSVValueType1 == CHECK_INTEGER_VALUE) {
                        mMinimumStock = Double.parseDouble(String.format("%.2f", Double.parseDouble(colums[21])));
                        if (mMinimumStock > 999999.99 || mMinimumStock < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter minimum stock between 0 and 999999.99 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.min_stock_invalid) + colums[1];
                        break;
                    }
                }

               /* // Favorite
                if (colums[21] != null && colums[21].length() > 0 && colums[21].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[21], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {
                        mFav = Integer.valueOf(colums[21]);
                        if (mFav > 1 || mFav < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter favorite as 0 or 1 for item " + colums[1];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.favorite_invalid) + " " + colums[0];
                        break;
                    }
                }*/

                // Active/Inactive
                if (colums[22] != null && colums[22].length() > 0 && colums[22].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[22], "Int");
                    if (mCheckCSVValueType == CHECK_INTEGER_VALUE) {
                        mActive = Integer.valueOf(colums[22]);
                        if (mActive > 1 || mActive < 0) {
                            mFlag = true;
                            mUserCSVInvalidValue = "Please enter active/inactive as 0 or 1 for item " + colums[0];
                            return;
                        }
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.active_invalid) + colums[0];
                        break;
                    }
                }

              /*  // Image URI
                mImageUri = "";
                if (colums[23] != null && colums[23].length() > 0 && colums[23].trim().length() > 0) {
                    mCheckCSVValueType = checkCSVTypeValue(colums[2], "String");
                    if (mCheckCSVValueType == CHECK_STRING_VALUE) {
                        mImageUri = colums[23];
                    } else {
                        mFlag = true;
                        mUserCSVInvalidValue = getResources().getString(R.string.image_invalid) + colums[0];
                        break;
                    }
                }*/


                // CHECK : Same short name and barcode can exist with same UOM but different MRP
                for (Map.Entry<Integer, ItemModel> entry : mHashMapItemCode.entrySet()) {
                    ItemModel entryValue = entry.getValue();
                    if (entryValue.getShortName().equalsIgnoreCase(mItemShortName) && entryValue.getBarCode().equalsIgnoreCase(mbarCode)) {
                        if (entryValue.getUOM().equalsIgnoreCase(mUOM)) {
                            if (entryValue.getMrp() == mRate2) {
                                mFlag = true;
                                mUserCSVInvalidValue = getResources().getString(R.string.same_item_same_mrp) + " " + entryValue.getShortName() + " & " + mItemShortName;
                                break;
                            }
                        } else {
                            mFlag = true;
                            mUserCSVInvalidValue = getResources().getString(R.string.same_item_different_uom) + " " + entryValue.getShortName() + " & " + mItemShortName;
                            break;
                        }
                    }
                }

                // if mrp and whole sale price is ZERO
                if (mRate2 == 0)
                    mRate2 = mRate1;
                if (mRate3 == 0)
                    mRate3 = mRate1;

                if (mRate3 > mRate1)
                {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.wholesale_price_greater_than_retail_price) + colums[0];
                    break;
                }
                if (mRate1 > mRate2)
                {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.retail_price_greater_than_mrp) + colums[0];
                    break;
                }
                if (mCESSRate != 0 && mCESSAmount != 0)
                {
                    mFlag = true;
                    mUserCSVInvalidValue = getResources().getString(R.string.either_cess_rate_or_amount) + colums[0];
                    break;
                }

                // Discount Amount
//                mDiscountAmount = mRate2 - mRate1;
                // Discount Percent
//                mDiscountPercent = Double.parseDouble(String.format("%.2f", ((mRate2 - mRate1)/ mRate2)*100));

                // Duplicate short code
                /*if (mHashMapItemCode.containsKey(mMenuCode)) {
                    mCSVHashCheckflag = true;
                    break;
                }*/

                ItemModel item_add = new ItemModel(mMenuCode, mItemShortName, mItemLongName, mbarCode, mUOM, mKitchenCode,
                        mDeptCode, mCategCode, mRate1, mRate2, mRate3, mMinimumStock, mQuantity,
                        mHSN, mPurchaseRate, mCGSTRate, mSGSTRate, mIGSTRate, mCESSRate, mCESSAmount, mAdditionalCESSAmount, mFav,
                        mSupplyType,mActive, 0, mImageUri, mDiscountPercent, mDiscountAmount);

                mHashMapItemCode.put(counter++, item_add);

                /*if (!mCSVHashCheckflag) {
                    mHashMapItemCode.put(mMenuCode, item_add);
                } else {
                    mFlag = true;
                    mUserCSVInvalidValue = "ShortCode " + mMenuCode + " is already present in CSV file";
                    mHashMapItemCode.clear();
                    return;
                }*/
            }

            if (!mCSVHashCheckflag && !mFlag) {
                saveCSVinDatabase();

            }

        } catch (Exception exp) {
            mCSVHashCheckflag = true;
            exp.printStackTrace();
        }
    }



    private void saveCSVinDatabase() {

        ArrayList<ItemModel> dataList = new ArrayList<>(mHashMapItemCode.values());

        try{
            if (dataList.size() > 0) {

                int deleted = dbItems.clearItemdatabase();
                Log.d(TAG, " Items deleted before uploading excel :" + deleted);
                for (int i = 0; i < dataList.size(); i++) {
                    long item_id = dbItems.insertItem(dataList.get(i));
                    ItemModel itemModel = dataList.get(i);
                    int shortCode = itemModel.getMenuCode();
                    String itemShortName = itemModel.getShortName();
                    String itemLongName = itemModel.getLongName();
                    double quantity = itemModel.getQuantity();
                    int isActive = itemModel.getActive();
                    Date date = new SimpleDateFormat("dd-MM-yyyy").parse(businessDate);
                    /*ItemStockClass itemStock = new ItemStockClass(item_id, String.valueOf(date.getTime()), shortCode, itemShortName, itemLongName,
                            quantity, quantity, 0.00, 0.00, isActive, 0.00);
                    long stockinsert = dbItems.insertNewItemInItemStock(itemStock);
                    if (stockinsert < 1) {
                        Log.d(TAG,  ": Issue while inserting " + itemShortName + " in ItemStock \n");
                    }*/
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    void populateHashMapData()
    {
        departmentHashMapList = dbItems.getDepartmentList();
        categoryHashMapList = dbItems.getCategoryList();
        kitchenHashMapList = dbItems.getKitchenList();
    }

    public static int checkCSVTypeValue(String value, String dataType) {
        int flag = 2;
        try {
            switch (dataType) {
                case "Int":
                    Integer.parseInt(value);
                    flag = 0;
                    break;
                case "Double":
                    Double.parseDouble(value);
                    flag = 1;
                    break;
                default:
                    flag = 2;
            }
        } catch (Exception nfe) {
            // nfe.printStackTrace();
        } finally {
            return flag;
        }
    }

    private int checkDeptandCategoryTogether(int departmentCSVValue, int categoryCSVValue) {
        int categoryCode = 0;

        for (Map.Entry entry : mCategoryCodeList.entrySet()) {

            if (entry.getKey().equals(categoryCSVValue) && entry.getValue().equals(departmentCSVValue)) {
                categoryCode = categoryCSVValue;
                return categoryCode;
            }
        }
        return categoryCode;
    }
    void mClear()
    {
        tv_filePath.setText("");
        strUploadFilepath="";
        actv_itemsearch.setText("");
        actv_BrandCategDeptSearch.setText("");
        itemsearchclicked = false;
        brandCategDeptSearchClicked = false;
        btItemMasterAdd.setEnabled(true);
        btItemMasterAdd.setTextColor(getResources().getColor(R.color.white));
    }
    public void displayItemList() {
        try {
            if(loadItemsAsync == null)
            {
                loadItemsAsync = new LoadItemsAsync();
                loadItemsAsync.execute();
            }
//            new LoadItemsAsync().execute();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /*long dd = event.getEventTime()-event.getDownTime();

        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            System.out.println("Richa : Enter encountered for barcode");
            addBarCodeItemToOrderTable();
        }else if (event.getKeyCode() == KeyEvent.KEYCODE_J ||event.getKeyCode() == KeyEvent.KEYCODE_CTRL_LEFT   )
        //}else if (event.getKeyCode() == KeyEvent.KEYCODE_J ||event.getKeyCode() == KeyEvent.KEYCODE_CTRL_LEFT ||event.getKeyCode() == KeyEvent.KEYCODE_SHIFT_LEFT  )
        {
            linefeed +=String.valueOf(event.getKeyCode());
            if(linefeed.equalsIgnoreCase("38113")|| linefeed.equalsIgnoreCase("11338")) // line feed value
                addBarCodeItemToOrderTable();
        }else {
            linefeed = "";
            if (dd < 15 && dd > 0 ) {
                View v = getView();
                if (v.getId() != R.id.autocomplete_billing_items_search) {
                    switch (v.getId()) {
                        case R.id.autocomplete_billing_customer_search:
                            actv_itemsearch.setText(tx);
                            break;
                        case R.id.av_billing_SalesManId:
                            actv_itemsearch.setText(tx);
                            break;
                    }
                    String bar_str = actv_itemsearch.getText().toString();
                    bar_str += (char) event.getUnicodeChar();
                    actv_itemsearch.setText(bar_str.trim());
                    actv_itemsearch.showDropDown();

                } else if (v.getId() == R.id.autocomplete_billing_items_search) {
                    tx += (char) event.getUnicodeChar();
                    actv_itemsearch.setText(tx.trim());
                    actv_itemsearch.showDropDown();
                }
            }
        }*/
        /*Toast.makeText(myContext, "keyUp:"+keyCode+" : "+dd, Toast.LENGTH_SHORT).show();*/
        return true;
    }

    ItemMasterBean itemMasterBean;

    void addBarCodeItemToOrderTable()
    {
        String barcode = actv_itemsearch.getText().toString().trim();
        System.out.println("Barcode = "+barcode);
        if(barcode == null || barcode.equals("") )
            return;
        Cursor crsr = dbItems.getActiveItemssbyBarCode(barcode);
        if(crsr!=null )
        {
            if(crsr.getCount()>1)
            {
                inflateMultipleRateOption(crsr);
            }else if(crsr.moveToFirst())
            {
                fill(crsr);
                if (itemMasterBean != null) {
                    //mPopulateItemsListAdapterData(billingItemsList);
//                    mAddItemToOrderTableList(itemMasterBean, false);
                    String data = actv_itemsearch.getText().toString();
                    itemsearchclicked = true;
                    brandCategDeptSearchClicked = false;

                    btItemMasterAdd.setEnabled(false);
                    btItemMasterAdd.setTextColor(getResources().getColor(R.color.grey));
                    try {
                        if(loadItemsAsync == null)
                        {
                            loadItemsAsync = new LoadItemsAsync();
                            loadItemsAsync.execute();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            } else {
                Toast.makeText(myContext, "Item not found or deleted", Toast.LENGTH_SHORT).show();
                actv_itemsearch.setText("");
            }
        }
        else {
            MsgBox.Show("Oops ","Item not found");
            Toast.makeText(myContext, "Item not found", Toast.LENGTH_SHORT).show();
        }
//        actv_itemsearch.setText("");
        linefeed="";
    }
    void fill(Cursor cursor)
    {
        itemMasterBean = new ItemMasterBean();
        itemMasterBean.set_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id)));
        itemMasterBean.setStrShortName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
        if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemLongName)) != null
                && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemLongName)).isEmpty()) {
            itemMasterBean.setStrLongName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemLongName)));
        } else {
            itemMasterBean.setStrLongName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
        }
        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)) != null) {
            itemMasterBean.setStrHSNCode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
        }
        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)) != null) {
            itemMasterBean.setStrUOM(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)));
        }
        if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)) != null && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)).isEmpty()) {
            itemMasterBean.setStrBarcode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
        }
        if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_Quantity)) != null &&
                !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_Quantity)).equals("")) {
            itemMasterBean.setDblQty(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Quantity)));
        }

        /*if(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_BrandCode)) > 0) {
            itemMasterBean.setiBrandCode(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_BrandCode)));
        }*/
        if(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_DepartmentCode)) > 0) {
            itemMasterBean.setiDeptCode(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_DepartmentCode)));
        }
        if(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_CategoryCode)) > 0) {
            itemMasterBean.setiCategoryCode(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_CategoryCode)));
        }

        if (cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)) != null && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)).isEmpty()) {
            itemMasterBean.setStrSupplyType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SupplyType)));
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice1)) > 0) {
            itemMasterBean.setDblRetailPrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice1)));
        } else{
            itemMasterBean.setDblRetailPrice(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice2)) > 0) {
            itemMasterBean.setDblMRP(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice2)));
        } else{
            itemMasterBean.setDblMRP(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice3)) > 0) {
            itemMasterBean.setDblWholeSalePrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice3)));
        } else{
            itemMasterBean.setDblWholeSalePrice(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)) > 0) {
            itemMasterBean.setDblCGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
        } else {
            itemMasterBean.setDblCGSTRate(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)) > 0) {
            itemMasterBean.setDblSGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
        } else {
            itemMasterBean.setDblSGSTRate(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)) > 0) {
            itemMasterBean.setDblIGSTRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
        } else {
            itemMasterBean.setDblIGSTRate(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)) > 0) {
            itemMasterBean.setDblCessRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)));
        } else {
            itemMasterBean.setDblCessRate(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmount)) > 0) {
            itemMasterBean.setDblCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmount)));
        } else {
            itemMasterBean.setDblCessAmount(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DiscountAmount)) > 0) {
            itemMasterBean.setDblDiscountAmt(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DiscountAmount)));
        } else {
            itemMasterBean.setDblDiscountAmt(0);
        }
        if (cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DiscountPercent)) > 0) {
            itemMasterBean.setDbDiscountPer(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DiscountPercent)));
        } else {
            itemMasterBean.setDbDiscountPer(0);
        }

    }
    int checkCount =0;
    void inflateMultipleRateOption(Cursor cursor)
    {
        checkCount =0;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_multiple_item_with_same_name, null, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        ImageView iv_close = (ImageView)view.findViewById(R.id.iv_close);
        final TableLayout tbl_rate = (TableLayout)view.findViewById(R.id.tbl_rates);
        Button btnOk = (Button) view.findViewById(R.id.btnok) ;

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        View view1 = getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
        int count =1;

        while(cursor!=null && cursor.moveToNext())
        {

            TableRow row = new TableRow(myContext);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setBackgroundResource(R.drawable.row_background);

            CheckBox checkBox = new CheckBox(myContext);
            String item_id = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_id));
            checkBox.setText(item_id);
            checkBox.setHeight(40);
            checkBox.setTextSize(1);
            checkBox.setVisibility(View.GONE);
            //checkBox.setTextColor(getResources().getColor(R.drawable.row_item_background));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked)
                        checkCount++;
                    else
                        checkCount--;
                }
            });
            row.addView(checkBox);

            TextView tvSno = new TextView(myContext);
            tvSno.setText(""+count);
            tvSno.setHeight(50);
            count++;
            tvSno.setTextSize(20);
            tvSno.setPadding(5,0,0,0);
            row.addView(tvSno);

            TextView tvName = new TextView(myContext);
            tvName.setHeight(50);
            tvName.setTextSize(20);
            tvName.setTextColor(Color.parseColor("#000000"));
            tvName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemName)));
            row.addView(tvName);

            TextView tvMrp  = new TextView(myContext);
            tvMrp.setHeight(50);
            tvMrp.setTextSize(20);
            tvMrp.setTextColor(Color.parseColor("#000000"));
            String mrp = String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice1)));
            tvMrp.setText(mrp);
            row.addView(tvMrp);

            row.setTag("TAG");
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (String.valueOf(view.getTag()) == "TAG") {
                        TableRow Row = (TableRow) view;
                        CheckBox checkBox1= (CheckBox) Row.getChildAt(0);
                        int id = Integer.parseInt(checkBox1.getText().toString());
                        Cursor cursor = dbItems.getItemByID(id);
                        if (cursor!=null && cursor.moveToNext())
                        {
                            fill(cursor);
                            if(itemMasterBean != null) {
//                            mAddItemToOrderTableList(itemMasterBean, false);
                                actv_itemsearch.setText( cursor.getString( cursor.getColumnIndex(DatabaseHandler.KEY_MenuCode))  + "-"
                                        + cursor.getString( cursor.getColumnIndex(DatabaseHandler.KEY_ItemName)) + "-"
                                        + cursor.getString( cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
                                itemsearchclicked = true;
                                brandCategDeptSearchClicked = false;

                                btItemMasterAdd.setEnabled(false);
                                btItemMasterAdd.setTextColor(getResources().getColor(R.color.grey));
                                try {
                                    if(loadItemsAsync == null)
                                    {
                                        loadItemsAsync = new LoadItemsAsync();
                                        loadItemsAsync.execute();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }else {
                            Toast.makeText(myContext, "Some error occurred", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Item not found");
                        }
                        dialog.dismiss();
                    }
                }
            });
            tbl_rate.addView(row);
        }
    }

    @Override
    public void onItemAddSuccess() {
        displayItemList();
        loadAutocompleteData();
    }

    @Override
    public void onItemDeleteSuccess(ArrayList<Integer> itemIds) {
        if (itemIds.size() > 0)
            Toast.makeText(myContext, "Item Deleted!", Toast.LENGTH_SHORT).show();
        long status = -1;
        for (int item: itemIds){
            status = dbItems.deleteSupplierItemLinkageByItemId(item);
        }
        if (status > 0) {
            Toast.makeText(myContext, "Items linkage removed!", Toast.LENGTH_SHORT).show();
        }
        loadAutocompleteData();
        displayItemList();
    }

    class LoadItemsAsync extends AsyncTask<Void, Void, Void> {

        Cursor cursor = null;
        HashMap<Integer,String> mapToBePassed = new HashMap<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(myContext);
            progressDialog.setIcon(R.drawable.ic_launcher);
            progressDialog.setTitle(Constants.processing);
            progressDialog.setMessage("Loading Items. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            itemCount = 0;
            //t1.start();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            int iMode = Constants.MODE;

            String[] from = {"ItemShortName", "Quantity", "UOM", DatabaseHandler.KEY_DineInPrice1, "IGSTRate", DatabaseHandler.KEY_isActive};
            String[] fromKitchen = {"ItemShortName", "Quantity", "UOM", DatabaseHandler.KEY_DineInPrice1, "IGSTRate", DatabaseHandler.KEY_KitchenCode};
            String[] fromDept = {"ItemShortName", "Quantity", "UOM", DatabaseHandler.KEY_DineInPrice1, "IGSTRate", DatabaseHandler.KEY_DepartmentCode};
            String[] fromCategory = {"ItemShortName", "Quantity", "UOM", DatabaseHandler.KEY_DineInPrice1, "IGSTRate", DatabaseHandler.KEY_CategoryCode};
            String[] fromColumn;

            switch (rg_displayItemCriteria.getCheckedRadioButtonId()) {
                case R.id.rb_item_master_list_all:
                    iMode = Constants.MODE;
                    cursor = dbItems.getAllItems();
                    fromColumn = from;
                    break;
                case R.id.rb_item_master_list_brand:
                    iMode = Constants.KITCHEN_MODE;
                    cursor = dbItems.getAllItems_Kitchenwise();
                    fromColumn = fromKitchen;
                    mapToBePassed = kitchenHashMapList;
                    break;
                case R.id.rb_item_master_list_department:
                    iMode = Constants.DEPARTMENT_MODE;
                    cursor = dbItems.getAllItems_Departmentwise();
                    fromColumn = fromDept;
                    mapToBePassed = departmentHashMapList;
                    break;
                case R.id.rb_item_master_list_category:
                    iMode = Constants.CATEGORY_MODE;
                    cursor = dbItems.getAllItems_Categorywise();
                    fromColumn = fromCategory;
                    mapToBePassed = categoryHashMapList;
                    break;
                case R.id.rb_item_master_list_active:
                    iMode = Constants.ACTIVE_MODE;
                    cursor = dbItems.getAllItems_ActiveItems();
                    fromColumn = from;
                    break;
                case R.id.rb_item_master_list_inactive:
                    iMode = Constants.INACTIVE_MODE;
                    cursor = dbItems.getAllItems_InactiveItems();
                    fromColumn = from;
                    break;
                case R.id.rb_item_master_list_min_stock:
                    iMode = Constants.MINSTOCK_MODE;
                    cursor = dbItems.getAllItems_MinStockItems();
                    fromColumn = from;
                    break;
                default:
                    iMode = Constants.MODE;
                    cursor = dbItems.getAllItems();
                    fromColumn = from;
                    break;
            }
            if(itemsearchclicked == true)
            {
                String  data = actv_itemsearch.getText().toString();
                if(!data.equals("") && data.contains("-"))
                {
                    String[] parts = data.split("-");
                    final String shortCode = parts[0].trim();
                    final String itemShortName = parts[1].trim();
                    final String itemBarcode = parts[2].trim();

                    cursor = dbItems.getAllItems_details(Integer.parseInt(shortCode), itemShortName, itemBarcode,0);
                } else if (!data.equals("")){
                    cursor = dbItems.mGetItemSearchData(data);
                }
            }
            if(brandCategDeptSearchClicked == true)
            {
                String  datasearch = actv_BrandCategDeptSearch.getText().toString();
                switch (iMode) {
                    case Constants.KITCHEN_MODE:
                        cursor = dbItems.mGetItemSearchDataByKitchen(dbItems.getKitchenIdByName(datasearch));
                        break;
                    case Constants.DEPARTMENT_MODE:
                        cursor = dbItems.mGetItemSearchDataByDepartment(dbItems.getDepartmentIdByName(datasearch));
                        break;
                    case Constants.CATEGORY_MODE:
                        cursor = dbItems.mGetItemSearchDataByCategory(dbItems.getCategoryIdByName(datasearch));
                        break;
                }
            }
            itemCount = cursor.getCount();
            if (adapter != null)
                adapter = null;
            adapter = new SimpleCursorRecyclerAdapter(R.layout.row_item_list, cursor, fromColumn, to, itemClickListener, mapToBePassed);

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            //handle.sendMessage(handle.obtainMessage());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //deptCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            rv_list.setLayoutManager(new LinearLayoutManager(myContext));
            rv_list.setAdapter(adapter);
            btnItemCount.setText(String.valueOf(itemCount));
            if (progressDialog != null)
                progressDialog.dismiss();
            if(loadItemsAsync !=null)
                loadItemsAsync = null;
            actv_itemsearch.setText("");
            //progressBar.clearAnimation();
        }
    }

    SimpleCursorRecyclerAdapter.OnItemClickListener itemClickListener = new SimpleCursorRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

            FragmentManager fm = getSupportFragmentManager();

            ItemMasterEditItemDialogFragment myFragment =
                    (ItemMasterEditItemDialogFragment) fm.findFragmentByTag("Update Item");
            if (myFragment != null && myFragment.isVisible()) {
                return;
            }

            Cursor cursor = (((SimpleCursorRecyclerAdapter) rv_list.getAdapter()).getCursor());
            cursor.moveToPosition(position);


            ItemModel obj = new ItemModel();
            obj.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            obj.setMenuCode(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_MenuCode)));
            if(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemLongName)) != null
                    && !cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemLongName)).isEmpty())
            {
                obj.setLongName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemLongName)));
            } else {
                obj.setLongName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
            }
            obj.setShortName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
            obj.setBarCode(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemBarcode)));
            obj.setUOM(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_UOM)));
            obj.setKitchenCode(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_KitchenCode)));
            obj.setDeptCode(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_DepartmentCode)));
            obj.setCategCode(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_CategoryCode)));
            obj.setRetaiPrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice1)));
            obj.setMrp(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice2)));
            obj.setWholeSalePrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_DineInPrice3)));
            obj.setPurchaseRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_AveragePurchaseRate)));
            obj.setMinimumStock(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_MinimumStock)));
            obj.setQuantity(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_Quantity)));
            obj.setHsn(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_HSNCode)));
            obj.setCgstRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_CGSTRate)));
            obj.setSgstRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_SGSTRate)));
            obj.setIgstRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_IGSTRate)));
            obj.setCessRate(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessRate)));
            obj.setCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_cessAmount)));
            obj.setAdditionalCessAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_additionalCessAmount)));
            obj.setImageURL(cursor.getString(cursor.getColumnIndex("ImageUri")));
            obj.setFav(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_isFavrouite)));
            obj.setActive(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_isActive)));
            if(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_INCENTIVE_AMOUNT)) > 0) {
                obj.setIncentive_amount(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_INCENTIVE_AMOUNT)));
            } else {
                obj.setIncentive_amount(0);
            }

            /*ItemMasterEditItemDialogFragment ldf = new ItemMasterEditItemDialogFragment();
            ldf.mInitListener(ItemMastersFragment.this);
            Bundle args = new Bundle();
            args.putSerializable("ItemModelObject", obj);
            ldf.setArguments(args);
            getFragmentManager().beginTransaction().add(0, ldf).commit();*/

            fm = getSupportFragmentManager();
            ItemMasterEditItemDialogFragment itemMasterEditItemDialogFragment = new ItemMasterEditItemDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable("ItemModelObject", obj);
            itemMasterEditItemDialogFragment.setArguments(args);
            itemMasterEditItemDialogFragment.mInitListener(ItemManagementActivity.this);
            itemMasterEditItemDialogFragment.show(fm, "Update Item");
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }


        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 1 || start >= s.length() || start < 0)
                return;

            // If it was Enter
            if (s.subSequence(start, start + 1).toString().equalsIgnoreCase("\n")) {
                // Change text to show without '\n'
                String s_text = start > 0 ? s.subSequence(0, start).toString() : "";
                s_text += start < s.length() ? s.subSequence(start + 1, s.length()).toString() : "";
                Log.i(TAG, "Scanned data after CR : " + s_text);
                actv_itemsearch.setText(s_text);
                actv_itemsearch.setSelection(s_text.length());
                addBarCodeItemToOrderTable();
            }
        }

        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onHomePressed() {

    }
}
