package com.wepindia.pos.views.Masters.UserMaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.WepBaseActivity;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Masters.UserMaster.Listeners.AddRoleClickListener;
import com.wepindia.pos.views.Masters.UserMaster.Adapters.UserRolesAdapter;
import com.wepindia.pos.utils.ActionBarUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class AddRolesActivity extends WepBaseActivity implements View.OnClickListener {

    // Context object
    Context myContext;
    private EditText editTextAddUser;
    private com.wep.common.app.views.WepButton btnAddRole,btnClose;
    private ArrayList<String> rolesList;
    private RecyclerView gridViewRoles;
    private UserRolesAdapter rolesAdapter;
    private ArrayAdapter<String> adapterAccess;
    private DatabaseHandler dbHelper;
    private ArrayList<String> allRoles;
    Cursor crsrRole;
    MessageDialog MsgBox;
    Button btnClearRole,btnCloseRole, delete, btnaddrole;
    String strUserName = "";
    private Toolbar toolbar;
    ArrayList<String> listsAccess=null;
    GridView gridViewAccesses=null;
    Button btndelete, btnupdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_role);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myContext = this;
        MsgBox = new MessageDialog(myContext);
        dbHelper = new DatabaseHandler(AddRolesActivity.this);
        strUserName = getIntent().getStringExtra("USER_NAME");
        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        com.wep.common.app.ActionBarUtils.setupToolbar(AddRolesActivity.this, toolbar, getSupportActionBar(), "Add Role", strUserName, " Date:" + s.toString());


        rolesList = new ArrayList<String>();
        rolesAdapter = null;
        initialiseViewVariables();
        clickEvents();
        try {
            dbHelper.CreateDatabase();
            dbHelper.OpenDatabase();
            displayAccessList();
            displayRoleList();
        } catch (Exception ex) {
            MsgBox.Show("Error", ex.getMessage());
        }
    }

    private void displayAccessList()
    {
        listsAccess = getAllAccesses();

        adapterAccess = new ArrayAdapter<String>(AddRolesActivity.this, android.R.layout.simple_list_item_multiple_choice, listsAccess);
        gridViewAccesses.setAdapter(adapterAccess);
        gridViewAccesses.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        gridViewAccesses.setItemChecked(0, true);

        gridViewAccesses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position ==0){
                    if(((android.support.v7.widget.AppCompatCheckedTextView)view).isChecked() == false)
                    {
                        Toast.makeText(myContext,"Operator permission is given to all roles by default",Toast.LENGTH_SHORT).show();;
                        gridViewAccesses.setItemChecked(0, true);

                    }
                }
            }
        });
    }

    private void clear()
    {
        editTextAddUser = (EditText) findViewById(R.id.editTextAddUser);
        editTextAddUser.setText("");
        editTextAddUser.setEnabled(true);
        int c = gridViewAccesses.getCheckedItemCount();
        SparseBooleanArray arr = new SparseBooleanArray(c);
        arr = gridViewAccesses.getCheckedItemPositions();
        int size = arr.size();
        for (int i = 0; i < size; i++) {
            int key = arr.keyAt(i);
            gridViewAccesses.setItemChecked(key, false);
        }
        gridViewAccesses.setItemChecked(0, true);
        //displayRoleList();
        delete.setEnabled(false);
        btnupdate.setEnabled(false);
        btnaddrole.setEnabled(true);

    }
    private void  clickEvents()
    {
        btnClearRole.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //dbHelper.deleteRole(roleName)
                clear();
            }
        });


        btnCloseRole.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //dbHelper.deleteRole(roleName)
                close(v);
            }
        });
    }
    private void initialiseViewVariables()
    {
        try {
            delete=(Button)findViewById(R.id.btnDeleteRole);
            btnupdate =(Button)findViewById(R.id.btnGrantAccess);
            btnaddrole =(Button)findViewById(R.id.btnAddRole);
            btnCloseRole = (Button) findViewById(R.id.closeRole);
            editTextAddUser = (EditText) findViewById(R.id.editTextAddUser);
            gridViewAccesses = (GridView) findViewById(R.id.gridViewAccesses);
            btnClearRole = (Button) findViewById(R.id.btnclearAccess);

            btnAddRole = (com.wep.common.app.views.WepButton) findViewById(R.id.btnAddRole);
            btnAddRole.setOnClickListener(this);
            btndelete = (Button) findViewById(R.id.btnDeleteRole);
            btndelete.setEnabled(false);
            btnupdate.setEnabled(false);
            btnClose = (com.wep.common.app.views.WepButton) findViewById(R.id.closeRole);
            btnClose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dbHelper.CloseDatabase();
                    close(v);
                }
            });
            gridViewRoles = (RecyclerView) findViewById(R.id.gridViewRoles);
            gridViewRoles.addOnItemTouchListener(new RecyclerTouchListener(this,
                    gridViewRoles, new AddRoleClickListener() {

                @Override
                public void onClick(View view, final int position) {
                    ArrayList<String> list = rolesAdapter.getItems();
                    String roleName = list.get(position);
                    showDialog(roleName, position);
                    editTextAddUser = (EditText) findViewById(R.id.editTextAddUser);
                    editTextAddUser.setText(roleName);

                    btndelete.setEnabled(true);
                    btnupdate.setEnabled(true);
                    btnAddRole.setEnabled(false);
                    editTextAddUser.setEnabled(false);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }catch(Exception e)
        {
            e.printStackTrace();
            MsgBox.Show("Error",e.getMessage());
        }
    }
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnAddRole) {
            String txt = editTextAddUser.getText().toString().trim();
            if (txt.equalsIgnoreCase("")) {
                Toast.makeText(AddRolesActivity.this, "Please enter a role", Toast.LENGTH_SHORT).show();
            }
            else {
                boolean b1 = isContains(txt);
                // Add the role to DB and Update gridView
                if (b1) {
                    MsgBox.Show("Note", "Role already exist");
//                    Toast.makeText(AddRolesActivity.this, "Role already exist", Toast.LENGTH_SHORT).show();
                } else {
                    gridViewAccesses = (GridView) findViewById(R.id.gridViewAccesses);
                    int c=gridViewAccesses.getCheckedItemCount();
                    if(c==0){

                        Toast.makeText(myContext, "Select Permissions", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SparseBooleanArray checkedItems = gridViewAccesses.getCheckedItemPositions();
                        dbHelper.addAccessesForRole(txt, listsAccess, checkedItems);
                        long status = dbHelper.addRole(txt);
                        if (status > 0) {
                            if (rolesList == null)
                                rolesList = new ArrayList<>();
                            rolesList.add(txt);
                            if (rolesAdapter == null) {
                                rolesAdapter = new UserRolesAdapter(this, listsAccess);
                            } else {
                                /*displayRoleList();
                                gridViewRoles.setAdapter(rolesAdapter);
*/                              rolesAdapter.add(txt,rolesAdapter.getItemCount());
                            }
                            editTextAddUser.setText("");
                            int d=gridViewAccesses.getCheckedItemCount();
                            SparseBooleanArray arr=new SparseBooleanArray(d);
                            arr=gridViewAccesses.getCheckedItemPositions();
                            int size=arr.size();
                            for(int i=0;i<size;i++){
                                int key=arr.keyAt(i);
                                gridViewAccesses.setItemChecked(key,false);
//                                createPDF(txt,listsAccess.toString());
                            }
                            gridViewAccesses.setItemChecked(0, true);
                        } else
                            Toast.makeText(myContext, "Sorry role cannot be added", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }

    public void displayRoleList() {

        ArrayList<String> list = dbHelper.getAllRoles();
        if (rolesAdapter != null) {
            rolesList.clear();
            rolesList= dbHelper.getAllRoles();
            rolesAdapter.notifyDataSetChanged();

        } else {
            rolesAdapter = new UserRolesAdapter(AddRolesActivity.this, list);
            gridViewRoles.setLayoutManager(new LinearLayoutManager(this));
            gridViewRoles.setAdapter(rolesAdapter);
        }
    }

    private void showDialog(final String roleName, final int roleId) {
        listsAccess = getAllAccesses();
        gridViewAccesses = (GridView) findViewById(R.id.gridViewAccesses);
        adapterAccess = new ArrayAdapter<String>(AddRolesActivity.this, android.R.layout.simple_list_item_multiple_choice, listsAccess);
        gridViewAccesses.setAdapter(adapterAccess);
        gridViewAccesses.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        //gridViewAccesses.setItemChecked(0, true);
        ArrayList<Integer> list = dbHelper.getPermissionsForRole(roleName);
        Iterator<Integer> it = list.iterator();
        int count = 0;
        while (it.hasNext()) {
            count++;
            gridViewAccesses.setItemChecked(it.next(), true);
        }
        if (count == 0)
            gridViewAccesses.setItemChecked(0, true);


        btnupdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View id) {
                gridViewAccesses = (GridView) findViewById(R.id.gridViewAccesses);
                int c=gridViewAccesses.getCheckedItemCount();
                if(c==0){
                    Toast.makeText(myContext, "Select Permissions", Toast.LENGTH_SHORT).show();
                } else {
                    SparseBooleanArray checkedItems = gridViewAccesses.getCheckedItemPositions();
                    int l = dbHelper.deleteAccessesForRole(roleName);
                    dbHelper.addAccessesForRole(roleName, listsAccess, checkedItems);
                    clear();
                }
            }
        });

        Button btnDeleteRole = (Button) findViewById(R.id.btnDeleteRole);
        btnDeleteRole.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //dbHelper.deleteRole(roleName);
                //displayRoleList();
                askForDelete(roleName);

            }
        });
    }

    private void close(View v)
    {
        dbHelper.close();
        this.finish();
    }
      /*public void askForClear()
      {
          editTextAddUser=(EditText)findViewById(R.id.editTextAddUser);
          editTextAddUser.setText("");
          gridViewAccess1 = (GridView) findViewById(R.id.gridViewAccesses);
           SparseBooleanArray arr1[]=new SparseBooleanArray[gridViewAccess1.getCheckedItemCount()];
           SparseBooleanArray  arr=gridViewAccess1.getCheckedItemPositions();
          while()
          {
              gridViewAccess1.setItemChecked(i,false);
          }
      }*/
    /*private void showDialog(final String roleName) {
        final ArrayList<String> listsAccess = getAllAccesses();
        final GridView gridViewAccesses = (GridView) findViewById(R.id.gridViewAccesses);
        adapterAccess = new ArrayAdapter<String>(AddRolesActivity.this, android.R.layout.simple_list_item_multiple_choice, listsAccess);
        gridViewAccesses.setAdapter(adapterAccess);
        gridViewAccesses.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        gridViewAccesses.setItemChecked(0, true);
        ArrayList<Integer> list = dbHelper.getPermissionsForRole(roleName);
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            gridViewAccesses.setItemChecked(it.next(), true);
        }
        Button btnCancelAccess = (Button) findViewById(R.id.btnCancelAccess);
        btnCancelAccess.setOnClickListene(new View.OnClickListener() {
            public void onClick(View arg0) {
                return;
            }
        });
        Button btnGrantAccess = (Button)findViewById(R.id.btnGrantAccess);
        btnGrantAccess.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                SparseBooleanArray checkedItems = gridViewAccesses.getCheckedItemPositions();
                dbHelper.addAccessesForRole(roleName, listsAccess, checkedItems);

            }
        });
    }*/

    public ArrayList<String> getAllAccesses() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(0, "Operator");
        list.add(1, "Masters");
        list.add(2, "Payment & Receipt");
        list.add(3, "Reports");
        //list.add(4, "Purchase Order");
        return list;
    }

    public boolean isContains(String str) {
        boolean status = false;
        ArrayList<String> list = dbHelper.getAllRoles();
        for (String s : list) {
            if (str.equalsIgnoreCase(s)) {
                status = true;
                break;
            }
        }
        return status;
    }

    public ArrayList<String> getAllRoles(Cursor crsrRole) {
        ArrayList<String> list = new ArrayList<String>();
        list = new ArrayList<String>();
//        list.add(0, "Manager");
//        list.add(1, "Head Cook");
//        list.add(2, "Waiter");

//		String SELECT_QUERY = "SELECT * FROM " + TBL_USERSROLE;
//		Cursor cursor = dbFNB.rawQuery(SELECT_QUERY, null);
        if (crsrRole != null) {
            //Log.d(TAG,"fetched "+cursor.getCount()+" Items");
            while (crsrRole.moveToNext()) {
                String role = crsrRole.getString(crsrRole.getColumnIndex("RoleName"));
                list.add(role);
            }
        }
        return list;
    }

    public void askForDelete(final String roleName) {
        if(roleName.equalsIgnoreCase("Manager") || roleName.equalsIgnoreCase("HeadCook") || roleName.equalsIgnoreCase("Waiter")
                || roleName.equalsIgnoreCase("Rider"))
        {
            MsgBox.Show("Error","Default roles can not be deleted");
            return;
        }
        int roleId = dbHelper.getRoleIdForUserName(strUserName);
        if(roleId <1) {
            System.out.println("roleId received from database : "+roleId);
            Toast.makeText(myContext, "Sorry cannot delete",Toast.LENGTH_SHORT).show();
            return;
        }
        String getRoleName = dbHelper.getRoleName(String.valueOf(roleId));
        if(getRoleName.equals("")) {
            System.out.println("roleName received from database : "+getRoleName);
            Toast.makeText(myContext, "Sorry cannot delete",Toast.LENGTH_SHORT).show();
            return;
        }
        if(getRoleName.equalsIgnoreCase(roleName))
        {
            String msg = "You are logged in with user name \""+strUserName+"\" which is assigned role \""+getRoleName+"\".\n" +
                    "If you want to delete this role, then kindly login with different user name, who is not assigned this role.";
            MsgBox.Show("Error",msg);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmation");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage("Are you sure want to delete? \nPlease note all the logins for this role(if any) will also be deleted.");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                int roleIdtoDelete = dbHelper.getRoleIdforRoleName(roleName);
                int deleterole = dbHelper.deleteRole(roleName);
                int deleteuserName = dbHelper.deleteUser(roleIdtoDelete);
                rolesAdapter.remove(roleName);
                rolesAdapter.notifyDataSetChanged();
                clear();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder AuthorizationDialog = new AlertDialog.Builder(AddRolesActivity.this);
            LayoutInflater UserAuthorization = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vwAuthorization = UserAuthorization.inflate(R.layout.user_authorization, null);
            final EditText txtUserId = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserId);
            final EditText txtPassword = (EditText) vwAuthorization.findViewById(R.id.etAuthorizationUserPassword);
            final TextView tvAuthorizationUserId= (TextView) vwAuthorization.findViewById(R.id.tvAuthorizationUserId);
            final TextView tvAuthorizationUserPassword= (TextView) vwAuthorization.findViewById(R.id.tvAuthorizationUserPassword);
            tvAuthorizationUserId.setVisibility(View.GONE);
            tvAuthorizationUserPassword.setVisibility(View.GONE);
            txtUserId.setVisibility(View.GONE);
            txtPassword.setVisibility(View.GONE);
            AuthorizationDialog
                    .setIcon((R.drawable.ic_launcher))
                    .setTitle("Are you sure you want to exit ?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
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

    //implementing on click for recyclre view
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private AddRoleClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final AddRoleClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    //    public void createPDF(String name,String list)
//
//    {
//
//        Document doc = new Document();
//
//
//        try {
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/My File"+"/droidText";
//
//            File dir = new File(path);
//            if(!dir.exists())
//                dir.mkdirs();
//
//            Log.d("PDFCreator", "PDF Path: " + path);
//
//
//            File file = new File(dir, "sample.pdf");
//            FileOutputStream fOut = new FileOutputStream(file);
//
//            PdfWriter.getInstance(doc, fOut);
//
//            //open the document
//            doc.open();
//
//            doc.add(createFirstTable());
//            doc.add(createSecondTable());
//            doc.add(createThirdTable());
//            doc.add(createFourthTable());
//            Paragraph p=new Paragraph(new Phrase(" "));
//            p.setExtraParagraphSpace(7f);
//            doc.add(p);
//
//            Paragraph p1=new Paragraph(new Phrase("Certfied that above particulars are true"));
//            p1.setExtraParagraphSpace(5f);
//            doc.add(p1);
//
//            Paragraph p2=new Paragraph(new Phrase("Signature"));
//            p2.setExtraParagraphSpace(5f);
//            doc.add(p2);
//
//
//        } catch (DocumentException de) {
//            Log.e("PDFCreator", "DocumentException:" + de);
//        } catch (IOException e) {
//            Log.e("PDFCreator", "ioException:" + e);
//        }
//        finally
//        {
//
//            doc.close();
//        }
//        viewPdf("sample.pdf", "My File/droidText");
//
//    }
//
//
//    private PdfPTable createFirstTable()
//    {
//        PdfPTable table=new PdfPTable(12);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(0f);
//        table.setSpacingAfter(0f);
//        table.addCell("Name");
//        PdfPCell cell;
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(11);
//        table.addCell(cell);
//        return table;
//
//    }
//    private PdfPTable createFifthTable()
//    {
//        PdfPTable table=new PdfPTable(3);
//        table.setWidthPercentage(20);
//        table.setSpacingBefore(0f);
//        table.setSpacingAfter(0f);
//        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell("CR/DR No.");
//        PdfPCell cell;
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(2);
//        table.addCell(cell);
//        table.addCell("Date");
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(2);
//        table.addCell(cell);
//        return table;
//
//    }
//    private PdfPTable createSecondTable()
//    {
//        PdfPTable table=new PdfPTable(6);
//        table.setWidthPercentage(100);
//        table.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.setSpacingBefore(0f);
//        table.setSpacingAfter(0f);
//
//        PdfPCell cell;
//        cell=new PdfPCell(new Phrase("Address"));
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(3);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("CR/DR No."));
//        cell.setPaddingLeft(10f);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase(""));
//        table.addCell(cell);
//        table.addCell("GSTIN");
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(3);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("Date"));
//        cell.setPaddingLeft(10f);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase(""));
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("Buyers Details"));
//        cell.setColspan(4);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("Address Of Delivery"));
//        cell.setPaddingLeft(10f);
//        cell.setColspan(2);
//        table.addCell(cell);
//        table.addCell("Name");
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(3);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setPaddingLeft(10f);
//        cell.setColspan(2);
//        cell.setRowspan(3);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("Address"));
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(3);
//        table.addCell(cell);
//        table.addCell("GSTIN");
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(3);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("For Unregistered Buyers"));
//        cell.setColspan(4);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("For Inter-State Supply"));
//        cell.setPaddingLeft(10f);
//        cell.setColspan(2);
//        table.addCell(cell);
//        return table;
//
//    }
//    private PdfPTable createThirdTable()
//    {
//        PdfPTable table=new PdfPTable(5);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(0f);
//        table.setSpacingAfter(0f);
//        table.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell("State code");
//        PdfPCell cell;
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(2);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("Place Of Supply"));
//        cell.setPaddingLeft(15f);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase(" "));
//        table.addCell(cell);
//        table.addCell("State");
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(2);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("State"));
//        cell.setPaddingLeft(15f);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase(" "));
//        table.addCell(cell);
//        return table;
//
//    }
//    private PdfPTable createFourthTable()
//    {
//        PdfPTable table=new PdfPTable(12);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(0f);
//        table.setSpacingAfter(0f);
//        PdfPCell cell;
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(8);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("Tax"));
//        cell.setColspan(4);
//        table.addCell(cell);
//        cell=new PdfPCell(new Phrase("Description"));
//        cell.setColspan(4);
//        table.addCell(cell);
//        table.addCell("Invoice No");
//        table.addCell("Invoice Date");
//        table.addCell("Taxable Value");
//        table.addCell("Rate of Tax");
//        table.addCell("Central Tax");
//        table.addCell("State Tax/UT Tax");
//        table.addCell("Integrated Tax");
//        table.addCell("Cess");
//        cell=new PdfPCell(new Phrase(" "));
//        cell.setColspan(4);
//        table.addCell(cell);
//        table.addCell(" ");
//        table.addCell(" ");
//        table.addCell("In Rs.");
//        table.addCell("%");
//        table.addCell("In Rs.");
//        table.addCell("In Rs.");
//        table.addCell("In Rs.");
//        table.addCell("In Rs.");
//        for(int i=1;i<=13;i++)
//        {
//            cell=new PdfPCell(new Phrase(" "));
//            cell.setColspan(4);
//            table.addCell(cell);
//            table.addCell(" ");
//            table.addCell(" ");
//            table.addCell(" ");
//            table.addCell(" ");
//            table.addCell(" ");
//            table.addCell(" ");
//            table.addCell(" ");
//            table.addCell(" ");
//        }
//        table.addCell("TOTAL");
//        for(int i=1;i<=11;i++)
//        table.addCell(" ");
//        return table;
//
//    }
//
//
//    private void viewPdf(String file, String directory) {
//
//        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
//        Uri path = Uri.fromFile(pdfFile);
//
//        // Setting the intent for pdf reader
//        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//        pdfIntent.setDataAndType(path, "application/pdf");
//        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        try {
//            startActivity(pdfIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(AddRolesActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
//        }
//    }
}


