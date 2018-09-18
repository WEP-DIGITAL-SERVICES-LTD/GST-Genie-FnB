package com.wepindia.pos.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by MohanN on 3/30/2018.
 */

public class ExportDatabaseTableAsCSVTask extends AsyncTask<String, Void, Boolean>
{
    private static final String TAG = ExportDatabaseTableAsCSVTask.class.getName();
    private static DatabaseHandler mDBHandler;
    private static int iMode;

    private static Context mContext = null;
    private static ExportDatabaseTableAsCSVTask exportDatabaseTableAsCSVTask = null;

    private ProgressDialog dialog = null;

    private String strTableName = null;

    private String CSV_GENERATE_PATH = Environment.getExternalStorageDirectory().getPath() + "/WeP_FnB_CSVs/";
    private String FILENAME = "";

    public static ExportDatabaseTableAsCSVTask getInstance(Context context, DatabaseHandler dbHandler, int mode){
        if(exportDatabaseTableAsCSVTask == null){
            exportDatabaseTableAsCSVTask = new ExportDatabaseTableAsCSVTask();
        }
        mContext = context;
        mDBHandler = dbHandler;
        iMode = mode;
        return exportDatabaseTableAsCSVTask;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        boolean bResult = false;

        try{
            //App crash error log
            if(strings != null && strings[0] != null
                    && !strings[0].isEmpty()){
                strTableName = strings[0];
            }
        }catch (Exception ex){
            Log.i(TAG,"Error in init the app crash variable." +ex.getMessage());
        }

        File exportDir;
        File file;
        Cursor curCSV = null;
        CSVWriter csvWrite = null;
        String csvHeading = "";
        if (iMode == 0) {
            csvHeading = "MENU CODE,ITEM NAME,SUPPLY TYPE,RATE 1,RATE 2,RATE 3,QUANTITY,UOM,CGST RATE,SGST RATE,IGST RATE,cess RATE,DISCOUNT PERCENT,DEPARTMENT CODE,CATEGORY CODE,HSN,BAR CODE";
            FILENAME = "ExportOutwardItemsFromDatabase.csv";
        } else {
            csvHeading = "MENU CODE,ITEM NAME,SUPPLY TYPE,RATE,QUANTITY,UOM,CGST RATE,SGST RATE,IGST RATE,cess RATE";
            FILENAME = "ExportInwardItemsFromDatabase.csv";
        }
        try{
            exportDir = new File(CSV_GENERATE_PATH);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            file = new File(exportDir, FILENAME);

            file.createNewFile();

            csvWrite = new CSVWriter(new FileWriter(file));

            curCSV = mDBHandler.getDataForGeneratingCSV(strTableName);
            if(curCSV != null && curCSV.getCount() > 0){
                String[] array = csvHeading.split(",");
                csvWrite.writeNext(array);
                curCSV.moveToFirst();
               do {

                   if (iMode == 0) {
                       String arrStr[] = {curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_ItemId)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_ItemName)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_SupplyType)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_DineInPrice1)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_DineInPrice2)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_DineInPrice3)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_Quantity)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_UOM)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_CGSTRate)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_SGSTRate)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_IGSTRate)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_cessRate)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_DiscountPercent)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_DeptCode)).equals("0")?"":curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_DeptCode)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_CategCode)).equals("0")?"":curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_CategCode)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_HSNCode)),
                               curCSV.getString(curCSV.getColumnIndex("ItemBarcode"))};

                       csvWrite.writeNext(arrStr);
                   } else {
                       String arrStr[] ={curCSV.getString(curCSV.getColumnIndex("MenuCode")),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_ItemName)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_SupplyType)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_Rate)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_Quantity)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_UOM)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_CGSTRate)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_SGSTRate)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_IGSTRate)),
                               curCSV.getString(curCSV.getColumnIndex(DatabaseHandler.KEY_cessRate))};

                       csvWrite.writeNext(arrStr);
                   }
                    bResult = true;
                } while(curCSV.moveToNext());
            }
        } catch(SQLException sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
            bResult = false;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            bResult = false;
        } catch (Exception ex){
            Log.i(TAG,"Unable to export data as .csv file format from database file. " +ex.getMessage());
        } finally {
            if(curCSV != null && !curCSV.isClosed()){
                curCSV.close();
            }
            if(csvWrite != null){
                try {
                    csvWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Exporting database please wait...");
        dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean bResult) {
        super.onPostExecute(bResult);
        if (dialog!= null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if(exportDatabaseTableAsCSVTask != null){
            exportDatabaseTableAsCSVTask = null;
        }
        if (bResult) {
            Toast.makeText(mContext, "Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }
}
