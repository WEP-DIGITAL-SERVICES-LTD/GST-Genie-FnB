package com.wepindia.pos.views.Masters.OutwardItemMaster;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.models.ItemMasterBean;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.EMOJI_FILTER;
import com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners.CustomItemClickListener;
import com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners.ItemDeleteAdapter;
import com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners.OnItemListetner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ItemDeleteFragment extends DialogFragment {

    private static final String TAG = ItemDeleteFragment.class.getSimpleName();

    @BindView(R.id.autocomplete_item_delete_list_search)
    AutoCompleteTextView actv_itemSearch;
    @BindView(R.id.tv_item_delete_item_id)
    TextView tvitemId;

    @BindView(R.id.bt_item_delete_add_dialog_save)
    Button btnDelete;
    @BindView(R.id.bt_item_delete_clear)
    Button btnClear;
    @BindView(R.id.bt_item_delete_add_dialog_close)
    Button btnClose;
    @BindView(R.id.cb_item_delete_title_select_all)
    CheckBox cbSelectAll;

    @BindView(R.id.lv_item_delete)
    ListView lv_item_delete;

    private List<ItemDeletionBean> listItems;
    private ItemDeleteAdapter itemDeleteAdapter;
    private SimpleCursorAdapter mAdapterItems;

    Context myContext ;
    MessageDialog MsgBox ;
    OnItemListetner onItemListetner;
    private boolean isSearched = false;
    ItemMasterBean itemMasterBean;
    int isCheck = 0, itemId = -1;

    LoadItem loadItem = null;

    private DatabaseHandler dbItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        try {
            myContext = getActivity();
            dbItems = new DatabaseHandler(myContext);
            dbItems.CreateDatabase();
            dbItems.OpenDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.item_delete_fragment, container, false);
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
            setFilters();
            hideKeyboard();
            loadAutocompleteData();
//            populateSupplierList(0, -1);
        }catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isCheck = 0;
        itemId = -1;
        if(loadItem == null) {
            loadItem = new LoadItem();
            loadItem.execute();
        }
    }

    @OnClick({R.id.bt_item_delete_add_dialog_save, R.id.bt_item_delete_clear, R.id.bt_item_delete_add_dialog_close})
    protected void onWidgetClick(View view) {
        switch (view.getId()) {
            case R.id.bt_item_delete_add_dialog_save:
                mDelete();
                break;
            case R.id.bt_item_delete_clear:
                mClear();
                break;
            case R.id.bt_item_delete_add_dialog_close:
                dismiss();
                break;
            default:
                break;
        }
    }

    void mDelete() {

        new AsyncTask<Void, Void, Void>() {
            ProgressDialog pd;
            ArrayList<Integer> itemIds = new ArrayList<>();
            boolean deleted = false;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(myContext);
                pd.setIcon(R.drawable.ic_launcher);
                pd.setTitle(Constants.processing);
                pd.setMessage("Deleting Items. Please wait...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (listItems.size() > 0) {
                    long status = -1;
                    deleted = true;
                    for (int i=0; i<listItems.size(); i++) {
                        if (listItems.get(i).getCheckBox() == 1) {
                            status = dbItems.deleteItem(listItems.get(i).getItemId());
                            if (status > 0) {
                                Log.d("Item_Delete", listItems.get(i).getItemName() + " Deleted successfully");
                                itemIds.add(listItems.get(i).getItemId());
                            }
                        }
                    }

                } else {
                    deleted = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    if (deleted) {
                        onItemListetner.onItemDeleteSuccess(itemIds);
                        mClear();
                    } else {
                        Toast.makeText(myContext, "Please add some items first.", Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void loadAutocompleteData() {
        mAdapterItems = new SimpleCursorAdapter(getActivity(), R.layout.auto_complete_textview_two_strings, null,
                new String[]{DatabaseHandler.KEY_ItemShortName, DatabaseHandler.KEY_ItemBarcode, DatabaseHandler.KEY_DineInPrice1},
                new int[]{R.id.tv_auto_complete_textview_item_one, R.id.tv_auto_complete_textview_two, R.id.tv_auto_complete_textview_three},
                0);

        actv_itemSearch.setAdapter(mAdapterItems);

        mAdapterItems.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                Log.i(TAG, "Item name search data." +str);
                if(str != null) {
                    return dbItems.mGetAllItemSearchData(str);
                }
                return null;
            } });

        mAdapterItems.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(DatabaseHandler.KEY_ItemShortName);
                return cur.getString(index);
            }});

        actv_itemSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    isSearched = true;
                    cbSelectAll.setChecked(false);
                    tvitemId.setText(""+cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id)));
                    isCheck = 0;
                    itemId = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id));
//                    populateSupplierList(0, cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_id)));
                    if(loadItem == null) {
                        loadItem = new LoadItem();
                        loadItem.execute();
                    }
                    actv_itemSearch.setText("");
                }
                hideKeyboard();
            }
        });
    }

    class LoadItem extends AsyncTask {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // if (!pd.isShowing())
            pd = new ProgressDialog(getActivity());
            pd.setIcon(R.drawable.ic_launcher);
            pd.setTitle(Constants.processing);
            pd.setMessage("Loading Items. Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            populateSupplierList(isCheck, itemId);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (pd.isShowing())
                pd.dismiss();
            itemDeleteAdapter = new ItemDeleteAdapter(myContext, R.layout.row_supplier_deletion, listItems, itemClickListener);
            lv_item_delete.setAdapter(itemDeleteAdapter);
            actv_itemSearch.setText("");
            if(loadItem != null){
                loadItem = null;
            }
        }
    }

    void populateSupplierList(int isChacked, int itemId) {
        try {
            ItemDeletionBean model;
            Cursor item = null;
            if (listItems != null)
                listItems.clear();

            if (isSearched && itemId > -1) {
                item = dbItems.getItemByID(itemId);

                int count = 1;

                if (item != null && item.moveToFirst()) {

                    model = new ItemDeletionBean();

                    model.setSno(count++);
                    model.setItemId(item.getInt(item.getColumnIndex(DatabaseHandler.KEY_id)));
                    model.setItemName(item.getString(item.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
                    model.setItemMrp(item.getDouble(item.getColumnIndex(DatabaseHandler.KEY_DineInPrice1)));
                    model.setCheckBox(isChacked);

                    listItems.add(model);
                }

            } else {
                item = dbItems.getAllItemsOutward();

                int count = 1;

                if (item != null && item.moveToFirst()) {
                    do {
                        model = new ItemDeletionBean();

                        model.setSno(count++);
                        model.setItemId(item.getInt(item.getColumnIndex(DatabaseHandler.KEY_id)));
                        model.setItemName(item.getString(item.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
                        model.setItemMrp(item.getDouble(item.getColumnIndex(DatabaseHandler.KEY_DineInPrice1)));
                        model.setCheckBox(isChacked);

                        listItems.add(model);

                    } while (item.moveToNext());
                }

            }

            if (item != null) {
                item.close();
                item = null;
            }

          /*  itemDeleteAdapter = new ItemDeleteAdapter(myContext, R.layout.row_supplier_deletion, listItems, itemClickListener);
            lv_item_delete.setAdapter(itemDeleteAdapter);
            actv_itemSearch.setText("");*/
        } catch (Exception ex) {
            Log.i(TAG, "Unable to populate all items due to some error : " +ex.getMessage());
        }
    }

    private CustomItemClickListener itemClickListener = new CustomItemClickListener() {

        @Override
        public void onItemClick(View v, int position, boolean isChecked) {

        }

        @Override
        public void onItemCheckedCallback(View v, int position, boolean b) {
            /* ItemDeletionBean bean = (ItemDeletionBean) listItems.get(position);
           if (b)
                bean.setCheckBox(1);
            else
                bean.setSno(0);*/
           if(listItems.size() > 0) {
               if (listItems.get(position).getCheckBox() == 1) {
                   listItems.get(position).setCheckBox(0);
               } else {
                   listItems.get(position).setCheckBox(1);
               }
           }
           if(itemDeleteAdapter != null){
               itemDeleteAdapter.setNotifyAdapter(listItems);
           }
        }
    };

    void initialiseVariables()
    {
        myContext = getActivity();
        MsgBox = new MessageDialog(myContext);
        listItems = new ArrayList<ItemDeletionBean>();
        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(listItems.size() > 0) {
                if (b) {
                        if (tvitemId.getText().toString().isEmpty()) {
                            isCheck = 1;
                            itemId = -1;
                            if(loadItem == null) {
                                loadItem = new LoadItem();
                                loadItem.execute();
                            }
//                            populateSupplierList(1, -1);
                        } else {
                            isCheck = 1;
                            itemId = Integer.parseInt(tvitemId.getText().toString());
                            if(loadItem == null) {
                                loadItem = new LoadItem();
                                loadItem.execute();
                            }
//                            populateSupplierList(1, Integer.parseInt(tvitemId.getText().toString()));
                        }

                } else {
                    if (tvitemId.getText().toString().isEmpty()) {
                        isCheck = 0;
                        itemId = -1;
                        if(loadItem == null) {
                            loadItem = new LoadItem();
                            loadItem.execute();
                        }
//                        populateSupplierList(0, -1);
                    } else {
                        isCheck = 0;
                        itemId = Integer.parseInt(tvitemId.getText().toString());
                        if(loadItem == null) {
                            loadItem = new LoadItem();
                            loadItem.execute();
                        }
//                        populateSupplierList(0, Integer.parseInt(tvitemId.getText().toString()));
                    }
                }
                } else {
                    cbSelectAll.setChecked(false);
                    Toast.makeText(myContext, "No items to delete.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        actv_itemSearch.addTextChangedListener(filterTextWatcher);

    }

    void mClear(){
        actv_itemSearch.setText("");
        tvitemId.setText("");
        isSearched = false;
        itemMasterBean = null;
        cbSelectAll.setChecked(false);
        loadAutocompleteData();
        isCheck = 0;
        itemId = -1;
        if(loadItem == null) {
            loadItem = new LoadItem();
            loadItem.execute();
        }
//        populateSupplierList(0, -1);
    }

    public void mInitListener(OnItemListetner onItemListetner){
        this.onItemListetner = onItemListetner;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus()!=null)
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void setFilters(){
        actv_itemSearch.setFilters(new InputFilter[]{new EMOJI_FILTER(), new InputFilter.LengthFilter(30)});
    }


    void addBarCodeItemToOrderTable()
    {
        String barcode = actv_itemSearch.getText().toString().trim();
        System.out.println("Barcode = "+barcode);
        try {
            if (barcode == null || barcode.equals(""))
                return;
            Cursor crsr = dbItems.getActiveItemssbyBarCode(barcode);
            if (crsr != null) {
                if (crsr.getCount() > 1) {
                    inflateMultipleRateOption(crsr);
                } else if (crsr.moveToFirst()) {
                    fill(crsr);
                    if (itemMasterBean != null) {
                        isSearched = true;
                        tvitemId.setText(""+itemMasterBean.get_id());
                        isCheck = 0;
                        itemId = itemMasterBean.get_id();
                        if(loadItem == null) {
                            loadItem = new LoadItem();
                            loadItem.execute();
                        }
                        //populateSupplierList(0, itemMasterBean.get_id());
                    }
                } else {
                    Toast.makeText(myContext, "Item not found or deleted?", Toast.LENGTH_SHORT).show();
                    actv_itemSearch.setText("");
                }
            } else {
                MsgBox.Show("Oops ", "Item not found");
                Toast.makeText(myContext, "Item not found", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            actv_itemSearch.setText("");
            Log.i(TAG,"Unable to get or no data present in the item table." +ex.getMessage());
        }
    }

    int checkCount =0;
    void inflateMultipleRateOption(Cursor cursor)
    {
        checkCount =0;
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                actv_itemSearch.setText("");
                dialog.dismiss();
            }
        });

        View view1 = getActivity().getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
            tvName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ItemShortName)));
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

                            if (itemMasterBean != null) {
                                isSearched = true;
                                tvitemId.setText(""+itemMasterBean.get_id());
                                isCheck = 0;
                                itemId = itemMasterBean.get_id();
                                if(loadItem == null) {
                                    loadItem = new LoadItem();
                                    loadItem.execute();
                                }
//                                populateSupplierList(0, itemMasterBean.get_id());
                            }

                        }else {
                            Toast.makeText(myContext, "Some error occured", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Item not found");
                        }
                        dialog.dismiss();
                    }
                }
            });
            tbl_rate.addView(row);
        }
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

       /* if(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_BrandCode)) > 0) {
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

    //testing on 03/05/2018 start
    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int st, int ct,
                                      int af) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 1 || start >= s.length() || start < 0)
                return;

            if (s.subSequence(start, start + 1).toString().equalsIgnoreCase("\n")) {
                String s_text = start > 0 ? s.subSequence(0, start).toString() : "";
                s_text += start < s.length() ? s.subSequence(start + 1, s.length()).toString() : "";
                Log.i(TAG, "Scanned data in item delete fragment : " + s_text);
                actv_itemSearch.setText(s_text);
                actv_itemSearch.setSelection(s_text.length());
                addBarCodeItemToOrderTable();
            }
        }

    };
    //testing 0n 03/05/2018 ends
}
