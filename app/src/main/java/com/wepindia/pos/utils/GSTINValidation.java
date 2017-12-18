package com.wepindia.pos.utils;

import android.content.Context;

import com.wepindia.pos.R;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RichaA on 10/23/2017.
 */

public class GSTINValidation {

    private static final int CHECK_INTEGER_VALUE = 0;
    private static final int CHECK_DOUBLE_VALUE = 1;
    private static final int CHECK_STRING_VALUE = 2;

    public static boolean checkGSTINValidation(String str )
    {
        boolean mFlag = false;
        try {
            if(str.trim().length() == 0)
            {mFlag = true;}
            else if (str.trim().length() > 0 && str.length() == 15) {
                Pattern p = Pattern.compile("^[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9a-zA-Z]{1}[zZ][0-9a-zA-Z]{1}$");
                Matcher m = p.matcher(str);
                if(m.find())
                {
                    mFlag = true;
                }
                else {
                    mFlag = false;
                }
            } else {
                mFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFlag = false;
        }

        finally {
            return mFlag;
        }
    }
    public static  int checkDataypeValue(String value, String type) {
        int flag =-1;
        try {
            switch(type) {
                case "Int":
                    Integer.parseInt(value);
                    flag = 0;
                    break;
                case "Double" : Double.parseDouble(value);
                    flag = 1;
                    break;
                default : flag = getSpecialCharacterCount(value);
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            flag = -1;
        }
        return flag;
    }

    private  static int getSpecialCharacterCount(String s) {
        int result = -1;
        try{
            if (s == null || s.trim().isEmpty()) {
                System.out.println("Incorrect format of string");
                return result;
            }
            Pattern p = Pattern.compile("[^A-Za-z0-9]");
            Matcher m = p.matcher(s);
            // boolean b = m.matches();
            boolean b = m.find();
            if (b == true) {
                System.out.println("There is a special character in my string ");
            }
            else
            {
                System.out.println("There is no special char.");
                result = 2;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            result = -1;
        }
        finally{
            return  result;
        }

    }

    public static boolean checkValidStateCode(String gstin, Context activityContext)
    {
        if(gstin ==  null || gstin.equals(""))
            return true;
        boolean statecodePresent = false;
        //List<Integer> StateCodeList = Arrays.asList(R.array.poscode_list);
        List<String> StateCodeList = Arrays.asList(activityContext.getResources().getStringArray(R.array.poscode_list));
        String statecode = (gstin.substring(0,2));
        if(StateCodeList.contains(statecode))
            statecodePresent =  true;
        return statecodePresent;
    }

}
