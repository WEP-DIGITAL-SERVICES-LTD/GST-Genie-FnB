package com.wepindia.pos.views.InwardManagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.ItemInward;
import com.wep.common.app.views.WepButton;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.utils.ActionBarUtils;
import com.wepindia.pos.utils.StockInwardMaintain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InwardStockActivity extends WepBaseActivity {

    Context myContext;
    DatabaseHandler dbInwardStock ;
    MessageDialog MsgBox;
    AutoCompleteTextView ItemLongName;
    TextView tvExistingStock;

    EditText txtNewStock, txtRate1;
    WepButton btnUpdate,btnClearStock,btnCloseStock ;

    String strMenuCode = "", strUserName = "";
    Cursor crsrSettings = null;
    private Toolbar toolbar;
    private RecyclerView RV_itemList;
    private InwardStockAdapter mInwardStockAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inward_stock);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbInwardStock = new DatabaseHandler(this);
        myContext = this;
        MsgBox = new MessageDialog(myContext);
        strUserName = getIntent().getStringExtra("USER_NAME");
        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        com.wep.common.app.ActionBarUtils.setupToolbar(this,toolbar,getSupportActionBar(),"Inward Price and Stock",strUserName," Date:"+s.toString());
        try{
            dbInwardStock.CreateDatabase();
            dbInwardStock.OpenDatabase();
            InitializeViews();
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    RV_itemList.getContext(),mLinearLayoutManager.getOrientation());
            RV_itemList.addItemDecoration(mDividerItemDecoration);
            loadAutoCompleteData_ItemNames();
            clickEvent();
            ResetStock();
            loadItems();

        }catch (Exception e)
        {
            e.printStackTrace();
            MsgBox.Show("Error","Some error occured in intial setup");
        }

    }

    private  void InitializeViews()
    {
        RV_itemList = (RecyclerView) findViewById(R.id.RV_itemList);
        mLinearLayoutManager = new LinearLayoutManager(this);
        RV_itemList.setLayoutManager(mLinearLayoutManager);
        ItemLongName = (AutoCompleteTextView) findViewById(R.id.autoTextItemLongNameValue);
        tvExistingStock = (TextView) findViewById(R.id.tvItemExistingStockValue);
        txtNewStock = (EditText) findViewById(R.id.etItemNewStock);
        txtRate1 = (EditText) findViewById(R.id.etItemRate1);

        btnUpdate = (WepButton) findViewById(R.id.btnUpdateStock);
        btnClearStock = (WepButton) findViewById(R.id.btnClearStock);
        btnCloseStock = (WepButton) findViewById(R.id.btnCloseStock);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateStock(v);
            }
        });
        btnClearStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearStock(v);
            }
        });
        btnCloseStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseStock(v);
            }
        });
    }

    private void loadAutoCompleteData_ItemNames() {
        Cursor item_crsr  = dbInwardStock.getAllItem_GoodsInward();
        ArrayList<String> ItemName_list = new ArrayList<String>();
        while (item_crsr !=null && item_crsr.moveToNext()){
            String item = item_crsr.getString(item_crsr.getColumnIndex("ItemName"));
            if(item !=null)
                ItemName_list.add(item);
        }
        /*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,ingredientlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);*/
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, ItemName_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ItemLongName.setAdapter(dataAdapter);
    }

    private void clickEvent()
    {
        /*ItemLongName.setOnTouchListener(new View.OnTouchListener(){
            //@Override
            public boolean onTouch(View v, MotionEvent event){
                ItemLongName.showDropDown();
                return false;
            }
        });*/
        ItemLongName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = ItemLongName.getText().toString();
                Cursor cursorItem = dbInwardStock.getItem_GoodsInward( itemName);
                if(cursorItem!=null && cursorItem.moveToFirst()) {
                    String qty = (String.format("%.2f",cursorItem.getDouble(cursorItem.getColumnIndex("Quantity"))));
                    String rate = (String.format("%.2f",cursorItem.getDouble(cursorItem.getColumnIndex("Value"))));
                    tvExistingStock.setText(qty);
                    txtNewStock.setText("0");
                    txtRate1.setText(rate);
                    strMenuCode = cursorItem.getString(cursorItem.getColumnIndex("MenuCode"));
                    btnUpdate.setEnabled(true);
                }
            }
        });
    }

    private void loadItems() {
        new AsyncTask<Void, Void, ArrayList<ItemInward>>() {

            ProgressDialog pd = new ProgressDialog(InwardStockActivity.this);
            int progress = 0;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd.setMessage("Updating item list...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected ArrayList<ItemInward> doInBackground(Void... params) {
                Cursor item_crsr  = dbInwardStock.getAllItem_GoodsInward();
                ArrayList<ItemInward> InwardItemList = new ArrayList<>();
                while (item_crsr !=null && item_crsr.moveToNext()){
                    String itemName = item_crsr.getString(item_crsr.getColumnIndex("ItemName"));
                    int menuCode  = item_crsr.getInt(item_crsr.getColumnIndex("MenuCode"));
                    double existingStock = Double.parseDouble(String.format("%.2f",item_crsr.getDouble(item_crsr.getColumnIndex("Quantity"))));
                    System.out.println(itemName +" :"+existingStock );
                    double rate = Double.parseDouble(String.format("%.2f",item_crsr.getDouble(item_crsr.getColumnIndex("Value"))));
                    ItemInward item = new ItemInward();
                    item.set_id(menuCode);
                    item.setItemShortName(itemName);
                    item.setQuantity(existingStock);
                    item.setPurchaseRate(rate);
                    InwardItemList.add(item);
                    System.out.println(itemName+" has rate "+rate);
                }
                return InwardItemList;
            }

            @Override
            protected void onPostExecute(ArrayList<ItemInward> InwardItemList) {
                super.onPostExecute(InwardItemList);
                if(InwardItemList!=null)
                    setItemsAdapter(InwardItemList);
                pd.dismiss();
            }

        }.execute();
    }



    public void setItemsAdapter(final ArrayList<ItemInward> InwardItemList) {
        //if (mInwardStockAdapter == null) {
        if (true) {
            mInwardStockAdapter = new InwardStockAdapter(InwardItemList);
            RV_itemList.setAdapter(mInwardStockAdapter);
            mInwardStockAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    ItemInward itemClick = InwardItemList.get(position);
                    strMenuCode = String.valueOf(itemClick.get_id());
                    ItemLongName.setThreshold(1000);
                    ItemLongName.setText(itemClick.getItemShortName());
                    ItemLongName.setThreshold(1);
                    tvExistingStock.setText(String.format("%.2f", itemClick.getQuantity()));
                    txtRate1.setText(String.format("%.2f", itemClick.getPurchaseRate()));
                    txtNewStock.setText("0");
                    btnUpdate.setEnabled(true);
                }
            });


        } /*else
            mInwardStockAdapter.notifyDataSetChanged(InwardItemList);*/

    }

    public void UpdateStock(View v) {
        String strExistingStock = tvExistingStock.getText().toString();
        String strNewStock = txtNewStock.getText().toString();
        String strRate1 = txtRate1.getText().toString();

        if (strNewStock.equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Enter stock before updating");
            return;
        }

        double newStock = Double.parseDouble(String.format("%.2f",Double.parseDouble(strNewStock)));

        String itemName = ItemLongName.getText().toString();


        long lRowId = dbInwardStock.updateIngredient(Integer.parseInt(strMenuCode), itemName,(Double.parseDouble(strExistingStock) + newStock),
                Double.parseDouble(String.format("%.2f",Double.parseDouble(strRate1))),0);
        //Toast.makeText(myContext, "Updated Successfully", Toast.LENGTH_LONG).show();
        if(lRowId>0)
        {
            Log.d("StockUpdate", "Row Id:" + String.valueOf(lRowId)+" For item :"+itemName );

            // updating in table inwardStock

            Cursor date_cursor = dbInwardStock.getCurrentDate();
            String currentdate = "";
            if(date_cursor.moveToNext())
                currentdate = date_cursor.getString(date_cursor.getColumnIndex("BusinessDate"));
            StockInwardMaintain stock_outward = new StockInwardMaintain(myContext, dbInwardStock);
            double OpeningQuantity =0;
            double ClosingQuantity =0;
            Cursor outward_item_stock = dbInwardStock.getInwardStockItem(currentdate,Integer.parseInt(strMenuCode));
            if(outward_item_stock!=null && outward_item_stock.moveToNext()){
                OpeningQuantity = outward_item_stock.getDouble(outward_item_stock.getColumnIndex("OpeningStock"));
                OpeningQuantity += Double.parseDouble(strNewStock);

                ClosingQuantity = outward_item_stock.getDouble(outward_item_stock.getColumnIndex("ClosingStock"));
                ClosingQuantity += Double.parseDouble(strNewStock);
            }
            else
            {
                OpeningQuantity = (Double.parseDouble(strExistingStock) + Double.parseDouble(strNewStock));
                ClosingQuantity = OpeningQuantity;
            }
            stock_outward.updateOpeningStock_Inward( currentdate, Integer.parseInt(strMenuCode),itemName,OpeningQuantity, Double.parseDouble(String.format("%.2f",Double.parseDouble(strRate1))) );
            stock_outward.updateClosingStock_Inward( currentdate, Integer.parseInt(strMenuCode),itemName,ClosingQuantity);
            loadItems();
            tvExistingStock.setText(String.format("%.2f",(Double.parseDouble(strExistingStock) + newStock)));
            txtNewStock.setText("0.00");
            //ResetStock();
            // updating rate in Item_Inward table
            long ll =dbInwardStock.updateItem_Inw(0, 0,  itemName , Double.parseDouble(strNewStock), Double.parseDouble(String.format("%.2f",Double.parseDouble(strRate1))));
            System.out.println(11);
        }else
        {
            Toast.makeText(myContext,"Unable to update the stock ", Toast.LENGTH_SHORT).show();
        }

    }



    public void ClearStock(View v) {
        ResetStock();
    }

    private void ResetStock() {
        ItemLongName.setText("");
        txtNewStock.setText("0.00");
        tvExistingStock.setText("0.00");
        txtRate1.setText("0.00");
        btnUpdate.setEnabled(false);
    }
    public void CloseStock(View v) {
        // Close Database connection and finish the activity
        dbInwardStock.CloseDatabase();
        hideKeyboard();
        this.finish();
    }




    public class InwardStockAdapter extends RecyclerView.Adapter<InwardStockAdapter.MyViewHolder> {

        private List<ItemInward> InwardItemList;
        private OnItemClickListener mOnItemsImageClickListener;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView ItemNametv, SnNotv;

            public MyViewHolder(View view) {
                super(view);
                ItemNametv = (TextView) view.findViewById(R.id.textViewTitle);
                SnNotv = (TextView) view.findViewById(R.id.textViewSno);
                //SnNotv.setVisibility(View.VISIBLE);
                ItemNametv.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mOnItemsImageClickListener.onItemClick(getAdapterPosition(), v);
            }
        }


        public InwardStockAdapter(List<ItemInward> InwardItemList) {
            this.InwardItemList = InwardItemList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_cat, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ItemInward item = (ItemInward)InwardItemList.get(position);
            String ItemInward = item.getItemShortName();
            holder.ItemNametv.setText(ItemInward);
            holder.SnNotv.setText(String.valueOf(position+1));
        }

        @Override
        public int getItemCount() {
            return InwardItemList.size();
        }


        public void notifyDataSetChanged(ArrayList<ItemInward> list) {
            this.InwardItemList = list;
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemsImageClickListener = onItemClickListener;
        }
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

}
