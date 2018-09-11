package com.wepindia.pos.utils;

import android.content.Context;
import android.util.Log;

import com.wepindia.pos.R;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RichaA on 10/23/2017.
 */

public class Validations {

    public static final String TAG = Validations.class.getName();


    public static boolean checkGSTINValidation(String str) {
        boolean mFlag = false;
        try {
            if (str.trim().length() == 0) {
                mFlag = true;
            } else if (str.trim().length() > 0 && str.length() == 15) {
                Pattern p = Pattern.compile("^[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9a-zA-Z]{1}[zZ][0-9a-zA-Z]{1}$");
                Matcher m = p.matcher(str);
                if (m.find()) {
                    mFlag = true;
                } else {
                    mFlag = false;
                }
            } else {
                mFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFlag = false;
        } finally {
            return mFlag;
        }
    }


    public static boolean checkValidStateCode(String gstin, Context activityContext) {
        if (gstin == null || gstin.equals(""))
            return true;
        boolean statecodePresent = false;
        //List<Integer> StateCodeList = Arrays.asList(R.array.poscode_list);
        List<String> StateCodeList = Arrays.asList(activityContext.getResources().getStringArray(R.array.poscode_list));
        String statecode = (gstin.substring(0, 2));
        if (StateCodeList.contains(statecode))
            statecodePresent = true;
        return statecodePresent;
    }

    public static boolean checkValidateUserName(String username) {
        boolean mFlag = false;
        try {
            if (username.trim().length() == 0) {
                mFlag = true;
            } else if (username.trim().length() > 0) {
                Pattern p = Pattern.compile("^([a-zA-Z0-9](_|-| )[a-zA-Z0-9])*$");
                Matcher m = p.matcher(username);

                if (m.find()) {
                    mFlag = true;
                } else {
                    mFlag = false;
                }
            } else {
                mFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFlag = false;
        } finally {
            return mFlag;
        }
    }


    public static boolean isValidEmailAddress(String email) {

        if(email.equalsIgnoreCase(""))
        {
            return true;
        }
        else {
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            Pattern p = Pattern.compile(ePattern);
            Matcher m = p.matcher(email);
            return m.matches();
        }
    }

    public static boolean isValidPincode(String strPinCode) {
        boolean bResult = false;
        try {
            if (strPinCode.length() == 6) {
                bResult = true;
            } else {
                bResult = false;
            }
        } catch (Exception ex) {
            Log.i(TAG, "Unable to validate the pincode." + ex.getMessage());
            bResult = false;
        }
        return bResult;
    }

}
