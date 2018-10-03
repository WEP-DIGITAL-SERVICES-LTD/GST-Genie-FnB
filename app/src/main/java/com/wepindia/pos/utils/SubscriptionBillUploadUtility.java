package com.wepindia.pos.utils;

import android.database.Cursor;

import com.wep.common.app.Database.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by RichaA on 4/1/2018.
 */

public class SubscriptionBillUploadUtility {




    static public Boolean checkForBillsCountToUpload(DatabaseHandler dbHandler)
    {


        Boolean result = false;
        Calendar calendar = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        int day = Integer.parseInt(new SimpleDateFormat("dd").format(calendar.getTime()));
        if(day>24 && day<=31)
        {
            startDate.add(Calendar.MONTH,-1);
            startDate.set(Calendar.DAY_OF_MONTH,25);
            endDate.set(Calendar.DAY_OF_MONTH,24);
        }else if(day>0 && day<25)
        {
            startDate.add(Calendar.MONTH,-2);
            startDate.set(Calendar.DAY_OF_MONTH,25);
            endDate.set(Calendar.DAY_OF_MONTH,24);
            endDate.add(Calendar.MONTH,-1);
        }
        Cursor cc = dbHandler.getMeteringDataCalculatedforDate(endDate.getTimeInMillis());
        if(cc!=null && cc.moveToNext()){
            result = true;
        }
        return result;
    }


}
