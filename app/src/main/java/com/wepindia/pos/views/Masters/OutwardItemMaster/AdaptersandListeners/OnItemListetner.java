package com.wepindia.pos.views.Masters.OutwardItemMaster.AdaptersandListeners;

import java.util.ArrayList;

/**
 * Created by Administrator on 17-01-2018.
 */

public interface OnItemListetner {
      void onItemAddSuccess();
      void onItemDeleteSuccess(ArrayList<Integer> itemIds);
}
