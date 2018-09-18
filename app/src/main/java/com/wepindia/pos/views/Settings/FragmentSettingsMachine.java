package com.wepindia.pos.views.Settings;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.OwnerDetailsActivity;
import com.wepindia.pos.R;

import java.io.File;
import java.util.ArrayList;

public class FragmentSettingsMachine extends Fragment {

    Context myContext;
    DatabaseHandler dbBackup;//= new DatabaseHandler(DatabaseBackupActivity.this);
    MessageDialog MsgBox;
    private static final String DB_PATH = Environment.getExternalStorageDirectory().getPath() + "/WeP_FnB/";
    private static final String DB_NAME = "WeP_FnB_Database.db";
    Button btn_RestoreDefault,btn_DbBackup,btn_FactoryReset,btnCloseDatabaseBackup,btn_RestoreDefaultBillData;
    TextView tvSoftwareVersion;

    public FragmentSettingsMachine() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            dbBackup = new DatabaseHandler(getActivity());
            dbBackup.OpenDatabase();
        }catch (Exception e){
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_settings_machine, container, false);
        dbBackup = new DatabaseHandler(getActivity());
        myContext = getActivity();
        MsgBox = new MessageDialog(myContext);
        tvSoftwareVersion = (TextView) view.findViewById(R.id.software_version);
        btn_RestoreDefault = (Button) view.findViewById(R.id.btn_RestoreDefault);
        btn_RestoreDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestoreDefault();
            }
        });
        btn_RestoreDefaultBillData  = (Button) view.findViewById(R.id.btn_RestoreDefaultBillData);
        btn_RestoreDefaultBillData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestoreDefaultBillData();
            }
        });
        btn_DbBackup = (Button) view.findViewById(R.id.btn_DbBackup);
        btn_DbBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackUp();
            }
        });
        btn_FactoryReset = (Button) view.findViewById(R.id.btn_FactoryReset);
        btn_FactoryReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FactoryReset();
            }
        });
        btnCloseDatabaseBackup = (Button) view.findViewById(R.id.btnCloseDatabaseBackup);
        btnCloseDatabaseBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Close();
            }
        });
        try {
            dbBackup.CloseDatabase();
            dbBackup.CreateDatabase();
            dbBackup.OpenDatabase();

            String version ="0.0";
            try {
                PackageInfo pInfo = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                version ="0.0";
            }

            tvSoftwareVersion.setText("Software Version: V" + version);

        } catch (Exception exp) {
            Toast.makeText(myContext, "OnCreate: " + exp.getMessage(), Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void CreateBackup() {
        int iResult = -1;
        long lDbLastModified = 0;

        File DbFile = new File(DB_PATH + DB_NAME);
        lDbLastModified = DbFile.lastModified();

        iResult = dbBackup.BackUpDatabase();
        try {
            if (iResult == 1) {
                MsgBox.Show("Information", "Database backup has been created successfully");
            } else if (iResult == 0) {
                MsgBox.Show("Warning", "No backup created since Database is not updated after creating a backup");
            } else {
                MsgBox.Show("Warning", "Failed to create database backup");
            }
        } catch (Exception e) {
            Toast.makeText(myContext, "DATABACKUP : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void BackUp() {

        AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.user_authorization, null);

        final EditText txtUserId = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserId);
        final EditText txtPassword = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserPassword);

        AuthorizationDialog
                .setTitle("Authorization")
                .setIcon(R.drawable.ic_launcher)
                .setView(vwAuthorization)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Cursor User = dbBackup.getUser(txtUserId.getText().toString(), txtPassword.getText().toString());
                        if ((User != null) && (User.moveToFirst())) {
                            String roleName = User.getString(User.getColumnIndex("RoleId"));
                            ArrayList<String> list = dbBackup.getPermissionsNamesForRole(roleName);
                            String str = "1";
                            Boolean status = false;
                            /*if (list == null)
                                Log.d("BackUp()", "No access to " + txtUserId.getText().toString());

                            for (String s : list) {
                                if (str.equalsIgnoreCase(s)) {
                                    Log.d("BackUp()", txtUserId.getText().toString() + " has access for backup");
                                    status = true;
                                    break;
                                }
                            }*/
                            if (str.equalsIgnoreCase(roleName)) {
                                Log.d("BackUp()", " creating backup");
                                CreateBackup();
                                //dbBackup.DeleteAllTransaction();
                            } else {
                                MsgBox.Show("Warning", "Could not proceed due to in suffiecient access privilage");
                            }
                        } else {
                            MsgBox.Show("Warning", "Could not proceed due to wrong user id or password");
                        }
                    }
                })
                .show();
    }

    public void RestoreDefault() {
        AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.user_authorization, null);

        final EditText txtUserId = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserId);
        final EditText txtPassword = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserPassword);

        AuthorizationDialog
                .setTitle("Authorization")
                .setIcon(R.drawable.ic_launcher)
                .setView(vwAuthorization)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Cursor User = dbBackup.getUser(txtUserId.getText().toString(), txtPassword.getText().toString());
                        if ((User != null) && (User.moveToFirst())) {
                            String roleName = User.getString(User.getColumnIndex("RoleId"));
                            ArrayList<String> list = dbBackup.getPermissionsNamesForRole(roleName);
                            String str = "1";
                            /*Boolean status = false;
                            if (list == null)
                                Log.d("BackUp()", "No access to " + txtUserId.getText().toString());

                            for (String s : list) {
                                if (str.equalsIgnoreCase(s)) {
                                    Log.d("BackUp()", txtUserId.getText().toString() + " has access for backup");
                                    status = true;
                                    break;
                                }
                            }*/
                            if (str.equalsIgnoreCase(roleName)) {
                                Log.d("RestoreDefault()", "Settings Restored");
                                dbBackup.RestoreDefault();
                                Toast.makeText(myContext, "Settings Restored Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                MsgBox.Show("Warning", "Could not proceed due to in suffiecient access privilage");
                            }
                        } else {
                            MsgBox.Show("Warning", "Could not proceed due to wrong user id or password");
                        }
                    }
                })
                .show();
    }

    public void RestoreDefaultBillData() {
        AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.user_authorization, null);

        final EditText txtUserId = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserId);
        final EditText txtPassword = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserPassword);

        AuthorizationDialog
                .setTitle("Authorization")
                .setIcon(R.drawable.ic_launcher)
                .setView(vwAuthorization)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Cursor User = dbBackup.getUser(txtUserId.getText().toString(), txtPassword.getText().toString());
                        if ((User != null) && (User.moveToFirst())) {
                            String roleName = User.getString(User.getColumnIndex("RoleId"));
                            ArrayList<String> list = dbBackup.getPermissionsNamesForRole(roleName);
                            String str = "1";

                            if (str.equalsIgnoreCase(roleName)) {
                                if (dbBackup.RestoreBillData() > 0) {
                                    Log.d("RestoreDefault()", "Bill data Restored");
                                    dbBackup.UpdateBillNoResetInvoiceNo(0);
//                                    dbBackup.UpdateBillNoResetTrainingModeInvoiceNo(0);
                                    Toast.makeText(myContext, "Bill Data Deleted Successfully", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d("RestoreDefault()", "Error occurred in deleting data.");
                                    Toast.makeText(myContext, "No data to delete.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                MsgBox.Show("Warning", "Could not proceed due to in sufficient access privilege");
                            }
                        } else {
                            MsgBox.Show("Warning", "Could not proceed due to wrong user id or password");
                        }
                    }
                })
                .show();
    }

    public void FactoryReset() {
        AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(myContext);

        LayoutInflater UserAuthorization = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View vwAuthorization = UserAuthorization.inflate(R.layout.user_authorization, null);

        final EditText txtUserId = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserId);
        final EditText txtPassword = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserPassword);

        AuthorizationDialog
                .setTitle("Authorization")
                .setIcon(R.drawable.ic_launcher)
                .setView(vwAuthorization)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Cursor User = dbBackup.getUser(txtUserId.getText().toString(), txtPassword.getText().toString());
                        if ((User != null) && (User.moveToFirst())) {
                            String roleName = User.getString(User.getColumnIndex("RoleId"));
                            //ArrayList<String> list = dbBackup.getPermissionsNamesForRole(roleName);
                            String str = "1";

                            if (str.equalsIgnoreCase(roleName)) {
                                Log.d("RestoreDefault()", "Factory Resetted");
                                //dbBackup.FactoryReset();
                                String DB_PATH = Environment.getExternalStorageDirectory().getPath() + "/WeP_FnB/";
                                myContext.deleteDatabase(DB_PATH + "WeP_FnB_Database.db");
                                //Toast.makeText(myContext, "Factory Reset Successfully", Toast.LENGTH_LONG).show();
                                MsgBox.setIcon(R.drawable.ic_launcher)
                                .setTitle("Note")
                                .setMessage("Factory Reset Successfully")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dbBackup.CloseDatabase();
                                                Intent  intent = new Intent(myContext, OwnerDetailsActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                                //System.exit(0);
                            } else {
                                MsgBox.Show("Warning", "Could not proceed due to in suffiecient access privilage");
                            }
                        } else {
                            MsgBox.Show("Warning", "Could not proceed due to wrong user id or password");
                        }
                    }
                })
                .show();
    }

    public void Close()
    {
        dbBackup.CloseDatabase();
        getActivity().finish();
    }
}
