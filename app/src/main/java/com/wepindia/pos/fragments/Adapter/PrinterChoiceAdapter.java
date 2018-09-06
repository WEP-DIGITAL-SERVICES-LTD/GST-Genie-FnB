package com.wepindia.pos.fragments.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wepindia.pos.R;
import com.wepindia.pos.fragments.Model.PrinterChoice;

import java.util.ArrayList;

/**
 * Created by SachinV on 22-03-2018.
 */

public class PrinterChoiceAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private ArrayList<PrinterChoice> objects;

    public PrinterChoiceAdapter(@NonNull Context context, int resource, @NonNull ArrayList<PrinterChoice> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(resource, parent,false);

        PrinterChoice printerChoice = objects.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.printer_image);
        image.setImageResource(printerChoice.getmImageDrawable());

        TextView release = (TextView) listItem.findViewById(R.id.printer_name);
        release.setText(printerChoice.getmName());

        return listItem;
    }
}
