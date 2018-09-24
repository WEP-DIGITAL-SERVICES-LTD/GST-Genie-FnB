package com.wepindia.pos.GenericClasses;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wepindia.pos.Constants;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Masters.OutwardItemMaster.ItemManagementActivity;
import com.wepindia.pos.views.Masters.OutwardItemMaster.ItemMasterAddItemDialogFragment;
import com.wepindia.pos.views.Masters.OutwardItemMaster.ItemMasterEditItemDialogFragment;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilePickerDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener, Constants {


    public final static String EXTRA_FILE_PATH = "file_path";
    //public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
    public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
    private final static String DEFAULT_INITIAL_DIRECTORY = "/storage";
    protected File mDirectory;
    protected ArrayList<File> mFiles;
    protected boolean mShowHiddenFiles = false;
    protected String[] acceptedFileExtensions;
    private String contentType;
    public static final int FILE_PICKER_CODE = 34890;
    public static final int PICK_IMAGE_CODE = 33890;
    OnFilePickerClickListener onFilePickerClickListener;
    Context myContext;
    String CALLEDFRAGMENTNAME="";


    protected FilePickerListAdapter mAdapter;
    String TAG = FilePickerDialogFragment.class.getSimpleName();
    @BindView(R.id.listView) ListView listView;
    @BindView(R.id.empty)  View empty;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_file_picker, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this,fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"OnViewCreated()");
        try{
            myContext = getActivity();
            contentType = getArguments().getString("contentType");
            mDirectory = new File(DEFAULT_INITIAL_DIRECTORY);
            mFiles = new ArrayList<File>();
            mAdapter = new FilePickerListAdapter(myContext, mFiles);
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(this);
            listView.setEmptyView(empty);
            // Initialize the extensions array to allow any file extensions
            acceptedFileExtensions = new String[] {};
            // Get intent extras
            CALLEDFRAGMENTNAME = getArguments().getString(FRAGMENTNAME);
            /*if(getArguments().hasFileDescriptors(EXTRA_FILE_PATH)) {
                mDirectory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));
            }*/
        /*if(getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
            mShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
        }*/
            /*if(getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
                ArrayList<String> collection = getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);
                acceptedFileExtensions = collection.toArray(new String[collection.size()]);
            }*/
            refreshFilesList();
        }catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    public void mInitListener(OnFilePickerClickListener onFilePickerClickListener){
        this.onFilePickerClickListener = onFilePickerClickListener;
    }
    @Override
    public void onResume() {
        //refreshFilesList();
        super.onResume();
    }

    /**
     * Updates the list view to the current directory
     */
    protected void refreshFilesList() {
        // Clear the files ArrayList
        mFiles.clear();
//        c=1;
        // Set the extension file filter
        ExtensionFilenameFilter filter = new ExtensionFilenameFilter(acceptedFileExtensions);

        // Get the files in the directory
        File[] files = mDirectory.listFiles(filter);
        if(files != null && files.length > 0) {
            for(File f : files) {
                if(f.isHidden() && !mShowHiddenFiles) {
                    // Don't add the file
                    continue;
                }

                // Add the file the ArrayAdapter
                if(!(f.toString().equalsIgnoreCase("/storage/emulated"))){
                    if(isFileExist(f))
                        mFiles.add(f);
                }
            }

            Collections.sort(mFiles, new FileComparator());
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            File newFile = (File)mAdapter.getItem(i);
            if(newFile.isFile())
            {
                @SuppressWarnings("deprecation")
                String strExtension = MimeTypeMap.getFileExtensionFromUrl(newFile.toURL().toString());
                //String strExtension = newFile.getName().substring(newFile.getName().lastIndexOf(".") + 1);
                Log.v("FilePickerActivity", "File Extension:" + strExtension);
                String AddItemFragmentName = ItemMasterAddItemDialogFragment.class.getSimpleName();
                String EditItemFragmentName = ItemMasterEditItemDialogFragment.class.getSimpleName();
                String ItemMastersFragmentName = ItemManagementActivity.class.getSimpleName();
                if(contentType.equalsIgnoreCase("image"))
                {
                    if(strExtension.equalsIgnoreCase("png") || strExtension.equalsIgnoreCase("jpg") || strExtension.equalsIgnoreCase("JPEG")
                            || strExtension.equalsIgnoreCase("bmp"))
                    {
//                        if(CALLEDFRAGMENTNAME.equals(AddItemFragmentName) ||CALLEDFRAGMENTNAME.equals(EditItemFragmentName) )
//                        {
                            onFilePickerClickListener.filePickerSuccessClickListener(newFile.getAbsolutePath());
                            dismiss();
//                        }
                    }
                    else
                    {
                        Toast.makeText(myContext, "Please Select an Image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    if(strExtension.equalsIgnoreCase("csv") || strExtension.equalsIgnoreCase("CSV"))
                    {
                        if(CALLEDFRAGMENTNAME.equals(ItemMastersFragmentName))
                        {
                            onFilePickerClickListener.filePickerSuccessClickListener(newFile.getAbsolutePath());
                            dismiss();
                        }
                    }
                    else
                    {
                        Toast.makeText(myContext, "Please Select a CSV file", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            else
            {
                mDirectory = newFile;
                refreshFilesList();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class FilePickerListAdapter extends ArrayAdapter<File> {

        private List<File> mObjects;

        public FilePickerListAdapter(Context context, List<File> objects) {
            super(context, R.layout.file_picker_list_item, android.R.id.text1, objects);
            mObjects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = null;

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.file_picker_list_item, parent, false);
            } else {
                row = convertView;
            }

            File object = mObjects.get(position);

            ImageView imageView = (ImageView)row.findViewById(R.id.file_picker_image);
            TextView textView = (TextView)row.findViewById(R.id.file_picker_text);
            // Set single line
            textView.setSingleLine(true);
            String fileName = object.getName();
            String title = "";
            if(fileName.contains("UsbDriveA"))
            {
                title = "USB Drive";
            }
            else if(fileName.contains("sdcard0"))
            {
                title = "Internal Storage";
            }
            else if(fileName.contains("extSdCard"))
            {
                title = "SD Card";
            }
            else {
                title = fileName;
            }
            textView.setText(String.valueOf(title));
            if(object.isFile())
            {
                // Show the file icon
                imageView.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
            }
            else
            {
                // Show the folder icon
                imageView.setImageResource(R.drawable.ic_folder_black_24dp);
            }
            return row;
        }
    }

    private class FileComparator implements Comparator<File> {
        public int compare(File f1, File f2) {
            if(f1 == f2) {
                return 0;
            }
            if(f1.isDirectory() && f2.isFile()) {
                // Show directories above files
                return -1;
            }
            if(f1.isFile() && f2.isDirectory()) {
                // Show files below directories
                return 1;
            }
            // Sort the directories alphabetically
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }

    private class ExtensionFilenameFilter implements FilenameFilter {
        private String[] mExtensions;

        public ExtensionFilenameFilter(String[] extensions) {
            super();
            mExtensions = extensions;
        }

        public boolean accept(File dir, String filename) {
            if(new File(dir, filename).isDirectory()) {
                // Accept all directory names
                return true;
            }
            if(mExtensions != null && mExtensions.length > 0) {
                for(int i = 0; i < mExtensions.length; i++) {
                    if(filename.endsWith(mExtensions[i])) {
                        // The filename ends with the extension
                        return true;
                    }
                }
                // The filename did not match any of the extensions
                return false;
            }
            // No extensions has been set. Accept all file extensions.
            return true;
        }
    }

    public boolean isFileExist(File file){
        String name = file.getAbsolutePath().toString();
        if(name.contains("UsbDriveB"))
        {
            return false;
        }
        else if(name.contains("UsbDriveC"))
        {
            return false;
        }
        else if(name.contains("UsbDriveD"))
        {
            return false;
        }
        else if(name.contains("UsbDriveE"))
        {
            return false;
        }
        else if(name.contains("UsbDriveF"))
        {
            return false;
        }
        if(file.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}


