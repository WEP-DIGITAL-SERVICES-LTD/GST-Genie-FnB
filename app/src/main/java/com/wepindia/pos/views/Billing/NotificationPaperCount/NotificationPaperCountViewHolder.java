package com.wepindia.pos.views.Billing.NotificationPaperCount;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wep.common.app.models.NotificationPaperCountBean;
import com.wepindia.pos.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationPaperCountViewHolder extends RecyclerView.ViewHolder {

 /*   @BindView(R.id.tv_notification_paper_count_dialog_list_row_date)
    TextView tvDate;*/
    @BindView(R.id.tv_notification_paper_count_dialog_list_row_description)
    TextView tvDescription;
    @BindView(R.id.tv_notification_paper_count_dialog_list_row_id)
    TextView tvSerialNo;
    @BindView(R.id.bt_notification_paper_count_dialog_list_row_delete)
    Button btDelete;

    public NotificationPaperCountViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(NotificationPaperCountBean notificationPaperCountBean, int position) {
        if (notificationPaperCountBean != null) {
            //tvDate.setText(notificationPaperCountBean.getStrDate());
            tvDescription.setText(notificationPaperCountBean.getStrMessage());
            tvSerialNo.setText(""+position+1);
        }
    }
}