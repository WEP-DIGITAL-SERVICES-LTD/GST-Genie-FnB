package com.wepindia.pos.adapters;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wep.common.app.utils.AppUtils;
import com.wepindia.pos.R;
import java.io.File;

public class TestImageAdapter extends RecyclerView.Adapter<TestImageAdapter.ImageViewHolder> {

    private Context contextAdapter;
    private final byte ImageAssignType;
    private final String[] DisplayText;
    private final int[] Id;
    private final String[] ImageUri;
    private OnItemsImageClickListener mOnItemsImageClickListener;

    public TestImageAdapter(Context cntxt, String[] strArrDisplayText, int[] iId, String[] strArrImageUri, byte iImageAssignType) {
        this.contextAdapter = cntxt;
        this.ImageAssignType = iImageAssignType;
        this.DisplayText = strArrDisplayText;
        this.Id = iId;
        this.ImageUri = strArrImageUri;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_images, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.tvDisplayText.setText(DisplayText[position]);
        if (ImageAssignType == Byte.parseByte("1")) {
            try {
                String icon = AppUtils.getImagePath(ImageUri[position], DisplayText[position]);
                Uri uri = Uri.fromFile(new File(icon));
                Picasso.with(contextAdapter)
                        .load(uri)
                        .placeholder(R.drawable.img_noimage) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.img_noimage)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(holder.imgGridImage);
            } catch (Exception e) {
                holder.imgGridImage.setImageResource(R.drawable.img_noimage);
            }
        } else if (ImageAssignType == Byte.parseByte("2")) {
            holder.imgGridImage.setImageResource(Integer.parseInt(ImageUri[position]));
        }
    }

    @Override
    public int getItemCount() {
        return DisplayText.length;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDisplayText;
        private ImageView imgGridImage;

        ImageViewHolder(View itemView) {
            super(itemView);
            tvDisplayText = (TextView) itemView.findViewById(R.id.grid_item_label);
            imgGridImage = (ImageView) itemView.findViewById(R.id.grid_item_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
              mOnItemsImageClickListener.onItemClick(getAdapterPosition(),v);
        }
    }

    public interface OnItemsImageClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemsImageClickListener onItemClickListener) {
        mOnItemsImageClickListener = onItemClickListener;
    }
}
