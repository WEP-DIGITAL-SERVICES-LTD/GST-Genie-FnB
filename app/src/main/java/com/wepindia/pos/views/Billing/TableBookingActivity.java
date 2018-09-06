package com.wepindia.pos.views.Billing;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.Database.TableBooking;
import com.wep.common.app.WepBaseActivity;
import com.wepindia.pos.GenericClasses.ImageAdapter;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.RecyclerDirectory.TableBookingAdapter;
import com.wepindia.pos.RecyclerDirectory.TableBookingResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import com.wepindia.pos.R;
import com.wepindia.pos.utils.ActionBarUtils;

public class TableBookingActivity extends WepBaseActivity implements TableBookingAdapter.OnTableViewClickListener {

    // Context object
    Context myContext;

    String tempStrCustomerName, tempStrTimeBooking, tempStrMobileNo, tempStrTableNo;

    // DatabaseHandler object
    DatabaseHandler dbTableBooking = new DatabaseHandler(TableBookingActivity.this);
    // MessageDialog object
    MessageDialog MsgBox;// = new MessageDialog(HeaderFooterActivity.this);
    String strTime = "", strTableNo = "";
    // View handlers
    int iMaxTables = 0;
    ArrayList<Integer> arrlstTableNumbers, arrlstTableNumbers_reserved;
    EditText tvCustomerName, tvTimeBooking, tvTableNo, tvMobileNo, tvSearchMobileNo;
    TableLayout tblTableBooking;
    com.wep.common.app.views.WepButton btnAddTB, btnSaveTB;
    LinearLayout linear_table;
    String strCustomerName, strTimeBooking, strMobileNo;
    int iTableNo, iTBookId;
    private Toolbar toolbar;
    private int mHour, mMinute;

    GridView grdTable;
    TextView txtTblNo;
    String previous_time_pendingKOT = "0", next_time_pendingKOT = "0";
    //String strUserName;


    private ArrayList<TableBookingResponse> mTableBookingList;
    private RecyclerView mRecyclerView;
    private TableBookingAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tablebooking);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTableBookingList = new ArrayList<>();


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myContext = this;
        MsgBox = new MessageDialog(myContext);

        tvCustomerName = (EditText) findViewById(R.id.etTBCustomerName);
        tvTimeBooking = (EditText) findViewById(R.id.etTBTimeBooking);
        tvTableNo = (EditText) findViewById(R.id.etTBTableNo);
        tvMobileNo = (EditText) findViewById(R.id.etTBMobileNo);
        tvSearchMobileNo = (EditText) findViewById(R.id.etTBSearchMobileNo);
        tblTableBooking = (TableLayout) findViewById(R.id.tblTableBooking);


        btnAddTB = (com.wep.common.app.views.WepButton) findViewById(R.id.btnTBAdd);
        btnSaveTB = (com.wep.common.app.views.WepButton) findViewById(R.id.btnTBSave);

        linear_table = (LinearLayout) findViewById(R.id.linear_table);

        arrlstTableNumbers = new ArrayList<Integer>();
        arrlstTableNumbers_reserved = new ArrayList<Integer>();

        try {
            String strUserName = getIntent().getStringExtra("USER_NAME");
            Date d = new Date();
            CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
            com.wep.common.app.ActionBarUtils.setupToolbar(this, toolbar, getSupportActionBar(), "Table Booking", strUserName, " Date:" + s.toString());
            /*tvTitleUserName.setText(strUserName.toUpperCase());
            Date d = new Date();
            CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
            tvTitleDate.setText("Date : " + s);*/
            dbTableBooking.CreateDatabase();
            dbTableBooking.OpenDatabase();
            ResetTableBooking();

            // DisplayItems();

            DisplayTableBooking();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("deprecation")
    public void DisplayTableBooking() {

        Cursor crsrTBooking;
        crsrTBooking = dbTableBooking.getAllTableBooking();
        mTableBookingList.clear();

        int i = 1;
        if (crsrTBooking.moveToFirst()) {
            do {
                TableBookingResponse tableBookingResponse = new TableBookingResponse();

                tableBookingResponse.setsNo(i);
                tableBookingResponse.setiTBookId(crsrTBooking.getInt(crsrTBooking.getColumnIndex("TBookId")));
                tableBookingResponse.setCustomerName(crsrTBooking.getString(crsrTBooking.getColumnIndex("CustName")));
                tableBookingResponse.setTimeBooking(crsrTBooking.getString(crsrTBooking.getColumnIndex("TimeForBooking")));
                tableBookingResponse.setTableNo(Integer.parseInt(crsrTBooking.getString(crsrTBooking.getColumnIndex("TableNo"))));
                tableBookingResponse.setMobileNo(crsrTBooking.getString(crsrTBooking.getColumnIndex("MobileNo")));

                mTableBookingList.add(tableBookingResponse);

                i++;
            } while (crsrTBooking.moveToNext());
        } else {
            Log.d("Display Table Booking", "No Table Booking found");
        }

        mAdapter = new TableBookingAdapter(mTableBookingList);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

    }

    public void TimeSelection(View view) {
        try {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            String mins_str = String.valueOf(minute);
                            if (minute >= 0 && minute < 10) {
                                mins_str = "0" + minute;
                            }
                            tvTimeBooking.setText(hourOfDay + ":" + mins_str);
                            strTimeBooking = hourOfDay + ":" + mins_str;
                            linear_table.setVisibility(View.VISIBLE);
                            tvMobileNo.setVisibility(View.VISIBLE);
                            if (tvMobileNo.getText().toString().equals(""))
                                tvMobileNo.setEnabled(true);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void TableSelection(View view) {
        try {
            if (strTimeBooking == null || strTimeBooking.equals("")) {
                MsgBox = new MessageDialog(myContext);
                MsgBox.Show(" Information ", " Please Select Time First");
                return;
            }
            AlertDialog.Builder TableSelectionDialog = new AlertDialog.Builder(myContext);

            LayoutInflater TableSelection = (LayoutInflater) myContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View vwTableSelection = TableSelection.inflate(R.layout.table_selection, null);

            txtTblNo = (TextView) vwTableSelection.findViewById(R.id.tvSelectedTableNo);
            grdTable = (GridView) vwTableSelection.findViewById(R.id.table_selection_grid);
            grdTable.setOnItemClickListener(GridTableClick);

            //
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            calendar.setTime(sdf.parse(strTimeBooking));
            calendar.add(Calendar.MINUTE, -30);
            Date previous_time = calendar.getTime();

            calendar.setTime(sdf.parse(strTimeBooking));
            calendar.add(Calendar.MINUTE, 30);
            Date next_time = calendar.getTime();
            long millis = previous_time.getTime();

            // Create an instance of SimpleDateFormat used for formatting
            // the string representation of date (month/day/year)
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");

            // Get the date today using Calendar object.
            Date today = Calendar.getInstance().getTime();
            // Using DateFormat format method we can create a string
            // representation of a date with the defined format.
            String strTimeBookingStart = df.format(previous_time);
            String strTimeBookingEnd = df.format(next_time);


            Cursor OccupiedTables = dbTableBooking.getBookedTableBetweenTime(strTimeBookingStart, strTimeBookingEnd);

            //Cursor OccupiedTables = dbTableBooking.getOccupiedTables();
            arrlstTableNumbers_reserved.clear();
            if (OccupiedTables != null && OccupiedTables.moveToFirst()) {
                do {
                    // Add table number to array list
                    arrlstTableNumbers_reserved.add(OccupiedTables.getInt(3));
                } while (OccupiedTables.moveToNext());
            }

            calendar.setTime(sdf.parse(strTimeBooking));
            calendar.add(Calendar.MINUTE, -30);
            previous_time = calendar.getTime();

            calendar.setTime(sdf.parse(strTimeBooking));
            calendar.add(Calendar.MINUTE, 30);
            next_time = calendar.getTime();
            strTimeBookingStart = df.format(previous_time);
            strTimeBookingEnd = df.format(next_time);

            OccupiedTables = null;
            OccupiedTables = dbTableBooking.getCurrentlyUsedTableBetweenTime(strTimeBookingStart, strTimeBookingEnd);

            //Cursor OccupiedTables = dbTableBooking.getOccupiedTables();
            arrlstTableNumbers.clear();
            if (OccupiedTables != null && OccupiedTables.moveToFirst()) {
                do {
                    // Add table number to array list
                    arrlstTableNumbers.add(OccupiedTables.getInt(OccupiedTables.getColumnIndex("TableNumber")));
                } while (OccupiedTables.moveToNext());
            }

            Cursor crsrSettings = dbTableBooking.getBillSetting();
            if (crsrSettings != null && crsrSettings.moveToFirst()) {
                iMaxTables = crsrSettings.getInt(crsrSettings.getColumnIndex("MaximumTables"));

                InitializeTableGrid(iMaxTables);
            }


            TableSelectionDialog.setIcon(R.drawable.ic_launcher)
                    .setTitle("Select Table for Booking")
                    .setView(vwTableSelection)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            tvTableNo.setText(txtTblNo.getText().toString());

                        }
                    });
            AlertDialog alert11 = TableSelectionDialog.create();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert11.getWindow().getAttributes());
            lp.width = 150;
            lp.height = 500;
            /*lp.x=-170;
            lp.y=100;*/
            alert11.getWindow().setAttributes(lp);

            alert11.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    AdapterView.OnItemClickListener GridTableClick = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapter, View v, int position,
                                long id) {
            // TODO Auto-generated method stub
            if (v.getTag() != null) {
                strTableNo = String.valueOf(v.getTag());
                //Cursor crsrtblSelection = dbTableBooking.getOccupiedTablesByTableNo(Integer.valueOf(strTableNo));
                //Cursor crsrtblSelection = dbTableBooking.getCurrentlyUsedTableBetweenTime(previous_time_pendingKOT, next_time_pendingKOT);
                /*boolean flag = false;
                for (int i =0;flag == false && i< iMaxTables;i++)
                {
                    if(arrlstTableNumbers.contains(i+1))
                        flag = true;
                }
                if (flag) {*/
                if (arrlstTableNumbers.contains(position + 1)) {
                    MsgBox.Show("Warning", "Please Select Another Table, this Table is Occupied");
                    txtTblNo.setText("");
                } else {
                    txtTblNo.setText(strTableNo);
                    InitializeTableGrid1(iMaxTables, Integer.parseInt(strTableNo));
                }
            }
        }
    };

    private void InitializeTableGrid1(int Limit, int selection) {

        String[] TableText = new String[Limit];
        String[] TableImage = new String[Limit];
        int[] TableId = new int[Limit];

        selection -= 1;

        for (int i = 0; i < Limit; i++) {
            TableText[i] = "Table" + String.valueOf(i + 1);
            //TableImage[i] = String.valueOf(R.drawable.img_table);
            if (arrlstTableNumbers.size() > 0 &&
                    arrlstTableNumbers.contains(i + 1)) {
                TableImage[i] = String.valueOf(R.drawable.img_table_occupied);
            } else if (arrlstTableNumbers_reserved.size() > 0 &&
                    arrlstTableNumbers_reserved.contains(i + 1)) {
                TableImage[i] = String.valueOf(R.drawable.img_table_advance_reserved);
            } else {
                TableImage[i] = String.valueOf(R.drawable.img_table);
            }
            TableId[i] = i + 1;
        }
        TableImage[selection] = String.valueOf(R.drawable.img_table_selected);

        grdTable.setAdapter(new ImageAdapter(myContext, TableText, TableId, TableImage, Byte.parseByte("2")));
    }

    private void InitializeTableGrid(int Limit) {

        String[] TableText = new String[Limit];
        String[] TableImage = new String[Limit];
        int[] TableId = new int[Limit];

        for (int i = 0; i < Limit; i++) {
            TableText[i] = "Table" + String.valueOf(i + 1);
            //TableImage[i] = String.valueOf(R.drawable.img_table);
            if (arrlstTableNumbers.size() > 0 &&
                    arrlstTableNumbers.contains(i + 1)) {
                TableImage[i] = String.valueOf(R.drawable.img_table_occupied);
            } else if (arrlstTableNumbers_reserved.size() > 0 &&
                    arrlstTableNumbers_reserved.contains(i + 1)) {
                TableImage[i] = String.valueOf(R.drawable.img_table_advance_reserved);
            } else {
                TableImage[i] = String.valueOf(R.drawable.img_table);
            }
            TableId[i] = i + 1;
        }

        grdTable.setAdapter(new ImageAdapter(myContext, TableText, TableId, TableImage, Byte.parseByte("2")));
    }

    private void InsertTableBooking(int iTBookId, String strCustomerName, String strTimeBooking, String strMobileNo,
                                    int iTableNo) {
        long lRowId;

        TableBooking objTableBooking = new TableBooking(strCustomerName, strTimeBooking, strMobileNo, iTableNo,
                iTBookId);

        lRowId = dbTableBooking.addTableBooking(objTableBooking);

        Log.d("TableBooking", "Row Id: " + String.valueOf(lRowId));
    }

    private void ClearTableBooking() {

        tempStrCustomerName = "";
        tempStrMobileNo = "";
        tempStrTableNo = "";
        tempStrTimeBooking = "";

        for (int i = 1; i < tblTableBooking.getChildCount(); i++) {
            View Row = tblTableBooking.getChildAt(i);
            if (Row instanceof TableRow) {
                ((TableRow) Row).removeAllViews();
            }
        }
    }

    private void ResetTableBooking() {
        tvCustomerName.setText("");
        tvTimeBooking.setText("");
        tvTableNo.setText("");
        tvMobileNo.setText("");
        tvSearchMobileNo.setText("");

        tempStrCustomerName = "";
        tempStrMobileNo = "";
        tempStrTableNo = "";
        tempStrTimeBooking = "";

        btnAddTB.setEnabled(true);
        btnSaveTB.setEnabled(false);
        iTBookId =0;
        //btnAddTB.setTextColor(Color.BLACK);
        //btnSaveTB.setTextColor(Color.GRAY);
        linear_table.setVisibility(View.INVISIBLE);
        tvMobileNo.setVisibility(View.INVISIBLE);

    }

    public void AddTableBooking(View v) {
        try {
            if (tvCustomerName.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please fill CustomerName before Booking");
            } else if (tvTimeBooking.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Select Time for Booking");
            } else if (tvTableNo.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Select Table for Booking");
            } else if (tvMobileNo.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Enter Mobile No for Booking");
            } else if (tvMobileNo.getText().toString().length()!= 10) {
                MsgBox.Show("Warning", "Please fill 10 digit customer phone number");
            } else {

                Cursor crsrCust = dbTableBooking.getTableBookingByMobile(tvMobileNo.getText().toString());
                if (crsrCust.moveToFirst()) {
                    MsgBox.Show("Note", "Customer has already booked a table. Please use a different mobile number for booking.");
                } else {
                    try {
                        final String strCustomerName = tvCustomerName.getText().toString();
                        final String strTimeBooking = tvTimeBooking.getText().toString();
                        final String strMobileNo = tvMobileNo.getText().toString();
                        final int iTableNo = Integer.valueOf(tvTableNo.getText().toString());
                        int iTBookId;

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                        calendar.setTime(sdf.parse(strTimeBooking));
                        calendar.add(Calendar.MINUTE, -15);
                        Date previous_time = calendar.getTime();

                        calendar.setTime(sdf.parse(strTimeBooking));
                        calendar.add(Calendar.MINUTE, 15);
                        Date next_time = calendar.getTime();
                        long millis = previous_time.getTime();

                        // Create an instance of SimpleDateFormat used for formatting
                        // the string representation of date (month/day/year)
                        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

                        // Get the date today using Calendar object.
                        Date today = Calendar.getInstance().getTime();
                        // Using DateFormat format method we can create a string
                        // representation of a date with the defined format.
                        String strTimeBookingStart = df.format(previous_time);
                        String strTimeBookingEnd = df.format(next_time);


                        Cursor crsr = dbTableBooking.checkBookingStatus(iTableNo, strTimeBookingStart, strTimeBookingEnd);
                        if (crsr != null && crsr.moveToFirst()) {
                            String timeBookedAt = "";
                            do {
                                timeBookedAt += crsr.getString(crsr.getColumnIndex("TimeForBooking")) + ", ";
                            } while (crsr.moveToNext());
                            //String timeBookedAt = crsr.getString(crsr.getColumnIndex("TimeForBooking"));
                            String msg = " Table " + iTableNo + " is already booked for " + timeBookedAt + " Do you still want to book it ";
                            MsgBox.setMessage(msg)
                                    .setTitle("Note")
                                    .setIcon(R.drawable.ic_launcher)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int iTBookId = dbTableBooking.getTableBookingId();
                                            iTBookId++;
                                            Log.d("Insert TableBooking", "Table Booking Id: " + String.valueOf(iTBookId));
                                            InsertTableBooking(iTBookId, strCustomerName, strTimeBooking, strMobileNo, iTableNo);
                                            ResetTableBooking();
                                            //    ClearTableBooking();
                                            mTableBookingList.clear();
                                            DisplayTableBooking();
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        } else {
                            //  table not booked
                            iTBookId = dbTableBooking.getTableBookingId();
                            iTBookId++;
                            Log.d("Insert TableBooking", "Table Booking Id: " + String.valueOf(iTBookId));
                            InsertTableBooking(iTBookId, strCustomerName, strTimeBooking, strMobileNo, iTableNo);
                            ResetTableBooking();
                            //     ClearTableBooking();
                            mTableBookingList.clear();
                            DisplayTableBooking();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(myContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Table Booking :", ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(myContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Table Booking :", ex.getMessage());
        }
    }

    public void SaveTableBooking(View v) {

        try {

            if (tvCustomerName.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Enter CustomerName before Booking");
            } else if (tvTimeBooking.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Select Time for Booking");
            } else if (tvTableNo.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Select Table for Booking");
            } else if (tvMobileNo.getText().toString().equalsIgnoreCase("")) {
                MsgBox.Show("Warning", "Please Enter Mobile No for Booking");
            } else if (tvMobileNo.getText().toString().length()!= 10) {
                MsgBox.Show("Warning", "Please fill 10 digit customer phone number");
            } else {

                strCustomerName = tvCustomerName.getText().toString();
                strTimeBooking = tvTimeBooking.getText().toString();
                strMobileNo = tvMobileNo.getText().toString();
                strTableNo = tvTableNo.getText().toString();
                iTableNo = Integer.parseInt(tvTableNo.getText().toString());

                if (strCustomerName.equals(tempStrCustomerName)
                        && strTimeBooking.equals(tempStrTimeBooking)
                        && strMobileNo.equals(tempStrMobileNo)
                        && strTableNo.equals(tempStrTableNo)) {

                    if (IS_DELETE && strMobileNo.equals(deleted_mobile))
                        MsgBox.Show("Warning", "Update failed. The record has been deleted.");
                    ResetTableBooking();
                    IS_DELETE = false;
                    deleted_mobile = "";

                } else {

//                    Cursor crsrCust = dbTableBooking.getTableBookingByMobile(tvMobileNo.getText().toString());
//                    if (crsrCust.moveToFirst()) {
//                        if (strMobileNo.equals(crsrCust.getString(crsrCust.getColumnIndex("CustName")))) {
//                            MsgBox.Show("Note", "Customer has already booked a table. Please use a different mobile number for booking.");
//                            return;
//                        }
//                    }
//                    else {

                    //TableBookingResponse cc = (TableBookingResponse)v;
                    Log.d("TableBooking Selection", "Code: " + iTBookId + " Name: " + strCustomerName);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

                    calendar.setTime(sdf.parse(strTimeBooking));
                    calendar.add(Calendar.MINUTE, -15);
                    Date previous_time = calendar.getTime();

                    calendar.setTime(sdf.parse(strTimeBooking));
                    calendar.add(Calendar.MINUTE, 15);
                    Date next_time = calendar.getTime();

                    // Create an instance of SimpleDateFormat used for formatting
                    // the string representation of date (month/day/year)
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm");

                    // Using DateFormat format method we can create a string
                    // representation of a date with the defined format.
                    String strTimeBookingStart = df.format(previous_time);
                    String strTimeBookingEnd = df.format(next_time);

                    Cursor crsr = dbTableBooking.checkBookingStatus(iTableNo, strTimeBookingStart, strTimeBookingEnd);

                    if (crsr != null && crsr.moveToFirst()) {
                        String timeBookedAt = "";
                        do {

                            if (!strMobileNo.equals(crsr.getString(crsr.getColumnIndex("MobileNo")))
                                    && strTableNo.equals(crsr.getString(crsr.getColumnIndex("TableNo")))) {

                                timeBookedAt += crsr.getString(crsr.getColumnIndex("TimeForBooking")) + ", ";

                            }

                        } while (crsr.moveToNext());
                        //String timeBookedAt = crsr.getString(crsr.getColumnIndex("TimeForBooking"));
                        String msg = " Table " + iTableNo + " is already booked for " + timeBookedAt + " Do you still want to book it ";
                        if (!timeBookedAt.equals(""))
                            MsgBox.setMessage(msg)
                                    .setTitle("Note")
                                    .setIcon(R.drawable.ic_launcher)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //                                    int iTBookId = dbTableBooking.getTableBookingId();
                                            //                                    iTBookId++;
                                            Log.d("Insert TableBooking", "Table Booking Id: " + String.valueOf(iTBookId));
                                            int iResult = dbTableBooking.updateTableBooking(iTBookId, strCustomerName, strTimeBooking, iTableNo,
                                                    strMobileNo);
                                            ResetTableBooking();
                                            //    ClearTableBooking();
                                            if (iResult > 0) {
                                                //    ClearTableBooking();
                                                mTableBookingList.clear();
                                                DisplayTableBooking();
                                            } else {
                                                MsgBox.Show("Warning", "Update failed");
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        else {
                            int iResult = dbTableBooking.updateTableBooking(iTBookId, strCustomerName, strTimeBooking, iTableNo,
                                    strMobileNo);
                            ResetTableBooking();
                            //    ClearTableBooking();
                            if (iResult > 0) {
                                //    ClearTableBooking();
                                mTableBookingList.clear();
                                DisplayTableBooking();
                            } else {
                                MsgBox.Show("Warning", "Update failed");
                            }
                        }


                    } else {
                        //  table not booked
                        int iResult = dbTableBooking.updateTableBooking(iTBookId, strCustomerName, strTimeBooking, iTableNo,
                                strMobileNo);
                        Log.d("updateDept", "Updated Rows: " + String.valueOf(iResult));
                        ResetTableBooking();

                        if (iResult > 0) {
                            //    ClearTableBooking();
                            mTableBookingList.clear();
                            DisplayTableBooking();
                        } else {
                            MsgBox.Show("Warning", "Update failed");
                        }
                    }
//                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void ClearTableBooking(View v) {
        ResetTableBooking();
        //  ClearTableBooking();
        mTableBookingList.clear();
        DisplayTableBooking();
    }

    public void CloseTableBooking(View v) {

        dbTableBooking.CloseDatabase();
        this.finish();
    }

    public void SearchTableBooking(View v) {
        //   ClearTableBooking();
        mTableBookingList.clear();
        //   DisplayTBookingByMobileNo();
        DisplayTBookingByMobileNoTest();
    }

    public void DisplayTBookingByMobileNoTest() {
        if (tvSearchMobileNo.getText().toString().equalsIgnoreCase("")) {
            MsgBox.Show("Warning", "Please fill MobileNo for Search");
        } else if (tvSearchMobileNo.getText().toString().length()<10) {
            MsgBox.Show("Warning", "Please enter 10 digit mobile no");

        } else{
            Cursor crsrTBooking;
            crsrTBooking = dbTableBooking.getTableBookingByMobile(tvSearchMobileNo.getText().toString());

            int i = 1;
            if (crsrTBooking.moveToFirst()) {
                do {
                    TableBookingResponse tableBookingResponse = new TableBookingResponse();

                    tableBookingResponse.setsNo(i);
                    tableBookingResponse.setiTBookId(crsrTBooking.getInt(0));
                    tableBookingResponse.setCustomerName(crsrTBooking.getString(1));
                    tableBookingResponse.setTimeBooking(crsrTBooking.getString(2));
                    tableBookingResponse.setTableNo(Integer.parseInt(crsrTBooking.getString(3)));
                    tableBookingResponse.setMobileNo(crsrTBooking.getString(4));

                    mTableBookingList.add(tableBookingResponse);

                    i++;
                } while (crsrTBooking.moveToNext());
                hideKeyboard();
            } else {
                MsgBox.Show("Warning", "Table Booking not found");
                Log.d("Display Table Booking", "No Table Booking found");
            }

            mAdapter = new TableBookingAdapter(mTableBookingList);
            mRecyclerView.setAdapter(mAdapter);

            btnAddTB.setEnabled(false);
            btnSaveTB.setEnabled(true);
            //btnAddTB.setTextColor(Color.GRAY);
            //btnSaveTB.setTextColor(Color.BLACK);
        }
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
                            /*Intent returnIntent =new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);*/
                            finish();
                        }
                    })
                    .show();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onHomePressed() {
        ActionBarUtils.navigateHome(this);
//        finish();
    }

    @Override
    public void onItemClick(int position, View v) {

        //Log.d("position", " hi" + position);
        strTimeBooking = mTableBookingList.get(position).getTimeBooking();
        tvCustomerName.setText(mTableBookingList.get(position).getCustomerName());
        tvTimeBooking.setText(mTableBookingList.get(position).getTimeBooking());
        tvTableNo.setText("" + mTableBookingList.get(position).getTableNo());
        tvMobileNo.setText(mTableBookingList.get(position).getMobileNo());
        iTBookId = (mTableBookingList.get(position).getiTBookId());

        tempStrCustomerName = tvCustomerName.getText().toString().trim();
        tempStrTimeBooking = tvTimeBooking.getText().toString().trim();
        tempStrMobileNo = tvMobileNo.getText().toString().trim();
        tempStrTableNo = tvTableNo.getText().toString().trim();


        btnAddTB.setEnabled(false);
        btnSaveTB.setEnabled(true);
        //btnAddTB.setTextColor(Color.GRAY);
        //btnSaveTB.setTextColor(Color.BLACK);
        linear_table.setVisibility(View.VISIBLE);
        tvMobileNo.setVisibility(View.VISIBLE);
        tvMobileNo.setEnabled(false);

    }

    boolean IS_DELETE = false;
    String deleted_mobile;

    @Override
    public void onDeleteItemClick(final int position) {
        MsgBox.setTitle("Confirm")
                .setIcon((R.drawable.ic_launcher))
                .setMessage("Do you want to delete this Booking")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int result = dbTableBooking.DeleteTableBooking_WithDetails(mTableBookingList.get(position).getCustomerName(),
                                    mTableBookingList.get(position).getTimeBooking(), String.valueOf(mTableBookingList.get(position).getTableNo()),
                                    mTableBookingList.get(position).getMobileNo());
//                            mTableBookingList.remove(position);

                            if (result > 0) {
                                IS_DELETE = true;
                                deleted_mobile = mTableBookingList.get(position).getMobileNo();
                                DisplayTableBooking();
                            } else {
                                MsgBox.Show("Warning", "Not able to delete the record.");
                            }

//                            mAdapter = new TableBookingAdapter(mTableBookingList);
//                            mRecyclerView.setAdapter(mAdapter);

//                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            MsgBox.Show("Error", e.getMessage());
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
