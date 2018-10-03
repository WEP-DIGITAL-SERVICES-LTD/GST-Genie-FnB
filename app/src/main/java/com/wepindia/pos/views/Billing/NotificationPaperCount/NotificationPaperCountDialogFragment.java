package com.wepindia.pos.views.Billing.NotificationPaperCount;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.wep.common.app.models.NotificationPaperCountBean;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.R;
import com.wepindia.pos.views.Billing.NotificationPaperCount.Listener.OnNotificationDataSelect;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationPaperCountDialogFragment extends DialogFragment implements OnNotificationDataSelect {
    private static final String TAG = NotificationPaperCountDialogFragment.class.getName();

    @BindView(R.id.iv_notification_paper_count_dialog_close)
    ImageView ivClose;

    @BindView(R.id.rv_notification_paper_count_dialog_list)
    RecyclerView rvNotificationPaperCountList;

    private MessageDialog msgBox = null;

    private List<NotificationPaperCountBean> notificationPaperCountBeanList;
    private NotificationPaperCountAdapter notificationPaperCountAdapter = null;

    private long lastClickTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = 800;
            int height = 700;
            d.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        View rootView = inflater.inflate(R.layout.notification_paper_count_dialog, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            msgBox = new MessageDialog(getActivity());
            ButterKnife.bind(this, view);

            notificationPaperCountBeanList = new ArrayList<NotificationPaperCountBean>();

            Bundle args = getArguments();
            if (args != null) {
                notificationPaperCountBeanList = args.getParcelableArrayList(Constants.NOTIFICATION_PAPER_COUNT);
            } else {
                Log.w(TAG, "Notification paper out arguments expected, but missing");
            }

            if (notificationPaperCountBeanList.size() > 0) {
                if(notificationPaperCountAdapter == null) {
                    rvNotificationPaperCountList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    notificationPaperCountAdapter = new NotificationPaperCountAdapter(getActivity(), notificationPaperCountBeanList,this);
                    rvNotificationPaperCountList.setAdapter(notificationPaperCountAdapter);
                } else {
                    notificationPaperCountAdapter.setNotifyData(notificationPaperCountBeanList);
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, "Unable to init the notification paper count dialog fragment data." + ex.getMessage());
        }
    }

    @OnClick({R.id.iv_notification_paper_count_dialog_close})
    protected void onWidgetClick(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.iv_notification_paper_count_dialog_close:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRowDataSelect(NotificationPaperCountBean notificationPaperCountBean) {
        notificationPaperCountBeanList.remove(notificationPaperCountBean);
        notificationPaperCountAdapter.setNotifyData(notificationPaperCountBeanList);
    }
}
