package com.wepindia.pos.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.wep.common.app.Database.DatabaseHandler;
import com.wep.common.app.models.Items;
import com.wep.common.app.utils.AppUtils;
import com.wepindia.pos.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PriyabratP on 14-02-2017.
 */

public class ItemsAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Items> itemsList;

    public ItemsAdapter(Activity activity,ArrayList<Items> itemsList){
        this.activity = activity;
        this.itemsList = itemsList;
    }
    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        /*if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_images, null);
            *//*viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.grid_item_label);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.grid_item_image);
            convertView.setTag(viewHolder);*//*
        }
        else
        {
            //viewHolder = (ViewHolder) convertView.getTag();
            view = (View) convertView.getTag();
        }*/
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.grid_images, null);
        TextView textView = (TextView) view.findViewById(R.id.grid_item_label);
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_image);
        Items items = itemsList.get(position);
        String title = items.getItemName();
        String icon = AppUtils.getImagePath(items.getItemImage(),title);
        Uri uri = Uri.fromFile(new File(icon));
        textView.setText(title+"");
        try{
            Picasso.with(activity)
                    .load(uri)
                    .placeholder(R.drawable.img_noimage) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.img_noimage)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(imageView);
        }catch (Exception e){
            imageView.setImageResource(R.drawable.img_noimage);
        }

        /*try{
            Glide.with(activity).load(uri)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.img_noimage) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.img_noimage)
                    .into(imageView);
        }catch (Exception e){
            imageView.setImageResource(R.drawable.img_noimage);
        }*/

        return view;
    }

    public void notifyDataSetChanged(ArrayList<Items> list) {
        this.itemsList = list;
        notifyDataSetChanged();
    }

    /*static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }*/
}
