package com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners;

import android.view.View;

/**
 * Created by SachinV on 12-03-2018.
 */

public interface CustomItemClickListener {
    public void onItemClick(View v, int position, boolean isChecked);
    public void onItemCheckedCallback(View v, int position, boolean b);
}
