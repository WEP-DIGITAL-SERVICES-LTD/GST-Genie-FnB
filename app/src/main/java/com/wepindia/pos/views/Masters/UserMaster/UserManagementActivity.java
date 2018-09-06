package com.wepindia.pos.views.Masters.UserMaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wep.common.app.models.User;
import com.wep.common.app.views.WepButton;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Masters.UserMaster.Adapters.UsersListAdapter;
import com.wepindia.pos.utils.ActionBarUtils;

import java.util.ArrayList;
import java.util.Date;

public class UserManagementActivity extends WepBaseActivity implements View.OnClickListener {

    Context myContext;
    private static final int FILE_SELECT_CODE = 345;
    private EditText editTextName, editTextMobile, editTextDesignation, editTextLogin, editTextAdhar, editTextEmail, editTextAddress, editTextPass;
    private TextView textViewAttachment;
    //ImageView imageViewBack, imageViewHome;
    private Spinner spinnerRole;
    private ArrayList<String> listRoles;
    private ArrayAdapter spinnerRoleAdapter;
    private DatabaseHandler dbHelper;
    private ListView listViewUsers;
    private UsersListAdapter usersListAdapter;
    private ArrayList<User> dataList;
    private int userId = -1;
    String strUserName = "";
    MessageDialog MsgBox;
    private Toolbar toolbar;
    private WepButton btnSubmitUser,btnDeleteUser,btnClearUser, btnCloseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_management);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myContext = this;
        MsgBox = new MessageDialog(myContext);

        strUserName = getIntent().getStringExtra("USER_NAME");

        //tvTitleUserName.setText(strUserName.toUpperCase());
        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        //tvTitleDate.setText("Date : " + s);


        com.wep.common.app.ActionBarUtils.setupToolbar(UserManagementActivity.this,toolbar,getSupportActionBar(),"User Management",strUserName," Date:"+s.toString());

        try {

            getDb().CreateDatabase();
            getDb().OpenDatabase();
            listRoles = getDb().getAllRoles();
            InitializeViewVariables();
            spinnerRoleAdapter = new ArrayAdapter(UserManagementActivity.this, android.R.layout.simple_spinner_item, listRoles);
            spinnerRoleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRole.setAdapter(spinnerRoleAdapter);

            updateUsersList();
        } catch (Exception ex) {
            MsgBox.Show("Error", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void InitializeViewVariables()
    {
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextMobile = (EditText) findViewById(R.id.editTextMobile);
        editTextDesignation = (EditText) findViewById(R.id.editTextDesignation);
        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        editTextAdhar = (EditText) findViewById(R.id.editTextAadhar);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        spinnerRole = (Spinner) findViewById(R.id.spinnerRole);
        textViewAttachment = (TextView) findViewById(R.id.textViewAttachment);
        //imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        //imageViewHome = (ImageView) findViewById(R.id.imageViewHome);
        listViewUsers = (ListView) findViewById(R.id.listViewUsers);
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemClickEvent(usersListAdapter.getItems(position));
            }
        });
        btnSubmitUser = (WepButton)findViewById(R.id.btnSubmitUser);
        btnDeleteUser = (WepButton)findViewById(R.id.btnDeleteUser);
        btnClearUser = (WepButton)findViewById(R.id.btnClearUser);
        btnCloseUser = (WepButton)findViewById(R.id.btnCloseUser);

        btnSubmitUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitFormData(v);
            }
        });
        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteItem(v);
            }
        });
        btnClearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFormData(v);
            }
        });
        btnCloseUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Close(v);
            }
        });
        //imageViewHome.setOnClickListener(this);
        //imageViewBack.setOnClickListener(this);
        textViewAttachment.setOnClickListener(this);
    }
    public DatabaseHandler getDb(){
        if(dbHelper==null){
            dbHelper = new DatabaseHandler(this);
            try{
                dbHelper.OpenDatabase();
            }catch (Exception e){

            }
        }
        return dbHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            dbHelper.CloseDatabase();
        }catch (Exception e){

        }
    }

    private void listItemClickEvent(User user) {
        editTextName.setText(user.getUserName());
        editTextMobile.setText(user.getUserMobile());
        editTextDesignation.setText(user.getUserDesignation());
        editTextLogin.setText(user.getUserLogin());
        editTextPass.setText(user.getUserPassword());
        editTextAdhar.setText(user.getUserAdhar());
        editTextEmail.setText(user.getUserEmail());
        editTextAddress.setText(user.getUserAddress());
        int id = getIndex((Integer.parseInt(user.getUserRole()))+"");
        spinnerRole.setSelection(id);
        userId = user.getId();
        //spinnerRole
    }

    public int getIndex(String itemName)
    {
        String item = getDb().getRoleName(itemName);
        for (int i = 0; i < listRoles.size(); i++)
        {
            String auction = listRoles.get(i);
            if (item.equals(auction))
            {
                return i;
            }
        }
        return -1;
    }

    public void SubmitFormData(View view) {
        String txtPass = editTextPass.getText().toString().trim();
        String txtName = editTextName.getText().toString().trim();
        String txtMobile = editTextMobile.getText().toString().trim();
        String txtLogin = editTextLogin.getText().toString().trim();
        int pos = spinnerRole.getSelectedItemPosition() + 1;
        String txtRole = String.valueOf(pos);

        if (txtLogin.equalsIgnoreCase("") || txtPass.equalsIgnoreCase("") || txtName.equalsIgnoreCase("") || txtMobile.equalsIgnoreCase("") || txtRole.equalsIgnoreCase(""))
        {
            if (txtName.equalsIgnoreCase("")) {
                Toast.makeText(UserManagementActivity.this, "Invalid Name", Toast.LENGTH_SHORT).show();
            } else if (txtMobile.equalsIgnoreCase("")) {
                Toast.makeText(UserManagementActivity.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            } else if (txtRole.equalsIgnoreCase("") || txtRole.equalsIgnoreCase("Select Role")) {
                Toast.makeText(UserManagementActivity.this, "Invalid Role", Toast.LENGTH_SHORT).show();
            }else if (txtLogin.equalsIgnoreCase("")) {
                Toast.makeText(UserManagementActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
            } else if (txtPass.equalsIgnoreCase("")) {
                Toast.makeText(UserManagementActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
            }
        } else if (txtName.equalsIgnoreCase("admin")){
            Toast.makeText(UserManagementActivity.this, "Name cannot be "+txtName+". Please another name.", Toast.LENGTH_SHORT).show();
        }else if (txtLogin.equalsIgnoreCase("admin")){
            Toast.makeText(UserManagementActivity.this, "Login cannot be "+txtLogin+". Please another login.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(makeAadharValidatio(editTextAdhar) || !isValidEmailAddress(editTextEmail.getText().toString().trim()))
            {
                if(editTextAdhar.getText().toString().trim().length()<12 && editTextAdhar.getText().toString().trim().length()>0)
                {
                    Toast.makeText(myContext, "Invalid Aadhar number", Toast.LENGTH_SHORT).show();
                }
                else /*if(isValidEmailAddress(editTextEmail.getText().toString().toString()))*/
                {
                    Toast.makeText(myContext, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            } else if(txtLogin.equalsIgnoreCase("d#demo") ||txtPass.equalsIgnoreCase("d#demo") )
            {
                Toast.makeText(myContext, "Login or password cannot be d#demo", Toast.LENGTH_SHORT).show();
            }else if(txtName.equalsIgnoreCase("d#demo") )
            {
                Toast.makeText(myContext, "Name cannot be d#demo", Toast.LENGTH_SHORT).show();
            }
            else
            {
                // Save in Local DB
                final User user = new User(
                        txtName,
                        txtMobile,
                        editTextDesignation.getText().toString().trim(),
                        txtRole,
                        txtLogin,
                        txtPass,
                        editTextAdhar.getText().toString().trim(),
                        editTextEmail.getText().toString().trim(),
                        editTextAddress.getText().toString().trim(),
                        "");
                /*if(txtLogin.equalsIgnoreCase(""))
                {
                    long status = dbHelper.addNewUser(user);
                    if (status > 0) {
                        Toast.makeText(UserManagementActivity.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
                        updateUsersList();
                    } else
                        Toast.makeText(UserManagementActivity.this, "User Adding Failed", Toast.LENGTH_SHORT).show();
                }
                else */if (checkIfUserExist(txtLogin,txtPass))
            {
                user.setId(userId);
                final User uu = user;
                MsgBox.setTitle("Duplicate")
                        .setIcon(R.drawable.ic_launcher)
                        .setMessage("User with this Login is already present. Do you want to update it")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userId = -1;
                            }
                        })
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long status = getDb().updateUser(uu);
                                if (status > 0) {
                                    Toast.makeText(UserManagementActivity.this, "User Updated Successfully", Toast.LENGTH_SHORT).show();
                                    updateUsersList();
                                    userId = -1;
                                    resetValue();
                                }
                            }
                        }).show();

                /*user.setId(userId);
                long status = getDb().updateUser(user);
                if (status > 0) {
                    Toast.makeText(UserManagementActivity.this, "User Updated Successfully", Toast.LENGTH_SHORT).show();
                    updateUsersList();
                    userId = -1;
                    resetValue();
                } else
                    Toast.makeText(UserManagementActivity.this, "Please enter a different login id", Toast.LENGTH_SHORT).show();*/
            }
            else
            {
                long status = getDb().addNewUser(user);
                if (status > 0) {
                    Toast.makeText(UserManagementActivity.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
                    updateUsersList();
                    resetValue();
                } else
                    Toast.makeText(UserManagementActivity.this, "User Adding Failed2", Toast.LENGTH_SHORT).show();
            }
            }
        }
    }

    private boolean makeAadharValidatio(EditText editTextAdhar) {
        if(editTextAdhar.getText().toString().trim().equalsIgnoreCase(""))
        {
            return false;
        }
        else if(editTextAdhar.getText().toString().trim().length()<12)
        {
            return true;
        }
        else
            return false;
    }



    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("*/*");      //all files
        intent.setType("text/xml");   //XML file only
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.textViewAttachment) {
            showFileChooser();
        }
    }

    public void updateUsersList() {
        dataList = getDb().getAllUsers();
        if (usersListAdapter == null) {
            usersListAdapter = new UsersListAdapter(UserManagementActivity.this, dataList,getDb());
            listViewUsers.setAdapter(usersListAdapter);
        } else {
            usersListAdapter.notifyNewDataAdded(dataList);
        }
    }

    public boolean checkIfUserExist(String txtLogin , String txtPass) {
        boolean status = false;
        ArrayList<User> list = dataList;
        for (User user : list) {
            if ( (user.getUserLogin().toString().equalsIgnoreCase(String.valueOf(txtLogin))) &&
                    (user.getUserPassword().toString().equalsIgnoreCase(String.valueOf(txtPass)))){
                status = true;
                userId = user.getId();
                break;
            }
        }
        return status;
    }

    public void DeleteItem(View view) {
        if(editTextLogin.getText().toString().trim().equalsIgnoreCase(""))
        {
            Toast.makeText(myContext, "Select user to delete", Toast.LENGTH_SHORT).show();
        }
        else if(editTextLogin.getText().toString().trim().equalsIgnoreCase("admin"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("Note");
            builder.setMessage("This login cannot be deleted.");
            builder.setNegativeButton("Cancel", null);
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            askForDelete();
        }
    }

    public void resetValue()
    {
        editTextName.setText("");
        editTextMobile.setText("");
        editTextDesignation.setText("");
        editTextLogin.setText("");
        editTextPass.setText("");
        editTextAdhar.setText("");
        editTextEmail.setText("");
        editTextAddress.setText("");
        spinnerRole.setSelection(0);
    }

    public void clearFormData(View view) {
        resetValue();
        //spinnerRole.setSelection(0);
    }

    public void askForDelete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure want to delete?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                getDb().deleteUser(editTextLogin.getText().toString().trim());
                resetValue();
                updateUsersList();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            //@Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void Close(View v)
    {
        this.finish();
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
                            dbHelper.CloseDatabase();
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
    }
}
