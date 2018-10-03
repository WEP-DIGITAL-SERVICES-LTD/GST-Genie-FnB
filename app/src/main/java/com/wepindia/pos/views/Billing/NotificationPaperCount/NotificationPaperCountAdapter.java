package com.wepindia.pos.views.Billing.NotificationPaperCount;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wep.common.app.models.NotificationPaperCountBean;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Billing.NotificationPaperCount.Listener.OnNotificationDataSelect;

import java.util.List;

public class NotificationPaperCountAdapter extends RecyclerView.Adapter<NotificationPaperCountViewHolder> {

    private static final String TAG = NotificationPaperCountAdapter.class.getName();
    private List<NotificationPaperCountBean> notificationPaperCountBeanList;
    private Context mContext;
    private OnNotificationDataSelect onNotificationDataSelect = null;

    public NotificationPaperCountAdapter(Context context, List<NotificationPaperCountBean> list, OnNotificationDataSelect onNotificationDataSelect) {
        this.mContext = context;
        this.notificationPaperCountBeanList = list;
        this.onNotificationDataSelect = onNotificationDataSelect;
    }


    @Override
    public NotificationPaperCountViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_paper_count_dialog_list_row,viewGroup,false);
        return new NotificationPaperCountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationPaperCountViewHolder holder, final int position) {
        NotificationPaperCountBean myObject = notificationPaperCountBeanList.get(position);
        holder.bind(myObject,position);
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNotificationDataSelect.onRowDataSelect(notificationPaperCountBeanList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationPaperCountBeanList.size();
    }

    public void setNotifyData(List<NotificationPaperCountBean> list){
        this.notificationPaperCountBeanList = list;
        notifyDataSetChanged();
    }
}