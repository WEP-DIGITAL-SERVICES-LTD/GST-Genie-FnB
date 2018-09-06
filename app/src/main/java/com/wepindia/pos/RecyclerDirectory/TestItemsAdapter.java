package com.wepindia.pos.RecyclerDirectory;


import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wep.common.app.models.Items;
import com.wep.common.app.utils.AppUtils;
import com.wepindia.pos.R;

import java.io.File;
import java.util.ArrayList;

public class TestItemsAdapter extends RecyclerView.Adapter<TestItemsAdapter.ItemsViewHolder> {

    private Activity activity;
    private ArrayList<Items> itemsList;
    private OnItemsImageClickListener mOnItemsImageClickListener;

    public TestItemsAdapter(Activity activity, ArrayList<Items> itemsList) {
        this.activity = activity;
        this.itemsList = itemsList;
    }

    @Override
    public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_images, parent, false);
        return new ItemsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemsViewHolder holder, int position) {

        Items items = itemsList.get(position);
        String title = items.getItemName();
        String icon = AppUtils.getImagePath(items.getItemImage(), title);

        Uri uri = Uri.fromFile(new File(icon));
        holder.textView.setText(title + "");
        try {
            Picasso.with(activity)
                    .load(uri)
                    .resize(200, 200)
                    .placeholder(R.drawable.img_noimage) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.img_noimage)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(holder.imageView);
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.img_noimage);
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageView imageView;

        ItemsViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.grid_item_label);
            imageView = (ImageView) itemView.findViewById(R.id.grid_item_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnItemsImageClickListener.onItemClick(getAdapterPosition(), itemsList.get(getAdapterPosition()).getItemCode(), v);
        }
    }

    public void notifyDataSetChanged(ArrayList<Items> list) {
        this.itemsList = list;
        notifyDataSetChanged();
    }

    public interface OnItemsImageClickListener {
        void onItemClick(int position, int itemCode, View v);
    }

    public void setOnItemClickListener(OnItemsImageClickListener onItemClickListener) {
        mOnItemsImageClickListener = onItemClickListener;
    }
}
