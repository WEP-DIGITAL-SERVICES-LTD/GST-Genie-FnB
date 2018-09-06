package com.wepindia.pos.RecyclerDirectory;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wepindia.pos.R;
import com.wepindia.pos.RecyclerDirectory.TableBookingResponse;

import java.util.ArrayList;


public class TableBookingAdapter extends RecyclerView.Adapter<TableBookingAdapter.TableBookingViewHolder> {

    private static OnTableViewClickListener mOnTableViewClickListener;
    private ArrayList<TableBookingResponse> mTableBookingList;

    public TableBookingAdapter(ArrayList<TableBookingResponse> tableBookingList) {
        mTableBookingList = tableBookingList;
    }

    @Override
    public TableBookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // test_recycler_row_table_booking layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_table_booking, parent, false);
        return new TableBookingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TableBookingViewHolder holder, final int position) {
        holder.mSNo.setText("" + mTableBookingList.get(position).getsNo());
        holder.mCustomerName.setText(mTableBookingList.get(position).getCustomerName());
        holder.mTimeForBooking.setText(String.valueOf(mTableBookingList.get(position).getTimeBooking()));
        holder.mTableNo.setText("" + String.valueOf(mTableBookingList.get(position).getTableNo()));
        holder.mMobileNo.setText(String.valueOf(mTableBookingList.get(position).getMobileNo()));

        holder.mImageURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnTableViewClickListener.onDeleteItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTableBookingList.size();
    }

    class TableBookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mSNo;
        private TextView mCustomerName;
        private TextView mTimeForBooking;
        private TextView mTableNo;
        private TextView mMobileNo;
        private ImageView mImageURL;

        TableBookingViewHolder(View view) {
            super(view);
            mSNo = (TextView) view.findViewById(R.id.tvCaptionTBSNo);
            mCustomerName = (TextView) view.findViewById(R.id.tvCaptionTBCustomerName);
            mTimeForBooking = (TextView) view.findViewById(R.id.tvCaptionTBTimeBooking);
            mTableNo = (TextView) view.findViewById(R.id.tvCaptionTBAllocateTable);
            mMobileNo = (TextView) view.findViewById(R.id.tvCaptionTBMobileNo);
            mImageURL = (ImageView) view.findViewById(R.id.ivCaptionTBDelete);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnTableViewClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public interface OnTableViewClickListener {
        void onItemClick(int position, View v);
        void onDeleteItemClick(int position);
    }

    public void setOnItemClickListener(OnTableViewClickListener onItemClickListener) {
        mOnTableViewClickListener = onItemClickListener;
    }
}
