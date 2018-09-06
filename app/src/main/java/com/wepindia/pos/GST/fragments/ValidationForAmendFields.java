package com.wepindia.pos.GST.fragments;

import android.content.Context;

import com.wepindia.pos.GenericClasses.MessageDialog;
import com.wepindia.pos.utils.GSTINValidation;

/**
 * Created by RichaA on 8/29/2017.
 */

public class ValidationForAmendFields {

    static Context  myContext;
    static MessageDialog MsgBx;
    static int CHECK_INTEGER_VALUE = 0;
    static int CHECK_DOUBLE_VALUE = 1;
    static int CHECK_STRING_VALUE = 2;

    public ValidationForAmendFields(Context myContext) {
        this.myContext = myContext;
        this.MsgBx = new MessageDialog(myContext);
    }

    public static boolean validationCheckpoints_GSTR1_B2B(String gstin_ori , String gstin_ecom, String taxval,
                                                          String igstrate, String cgstrate, String sgstrate,
                                                          String igstamt, String cgstamt, String sgstamt, String cessamt)
    {

        boolean  status = true;
        boolean  mFlag = false;

        try{
            if(gstin_ori.trim().length() == 0 )
            {mFlag = true;}
            else if (gstin_ori.trim().length() > 0 && gstin_ori.length() == 15) {
                String[] part = gstin_ori.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                if(part.length != 7)
                {
                    mFlag = false;
                }
                else if (CHECK_INTEGER_VALUE == checkDataypeValue(part[0], "Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[1],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[2],"Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[3],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[4],"Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[5],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[6],"Int")) {

                               /* int length = gstin.length() -1;
                                if(Integer.parseInt(String.valueOf(gstin.charAt(length))) ==  checksumGSTIN(gstin.substring(0,length)))*/
                    mFlag = true;
                } else {
                    mFlag = false;
                }
            } else {
                mFlag = false;
            }
            if(!mFlag)
            {
                MsgBx.Show("Error","Please enter valid original GSTIN");
                status = false;
                return status;
            }
            mFlag = false;
            if(gstin_ecom.trim().length() == 0 )
            {mFlag = true;}
            else if (gstin_ecom.trim().length() > 0 && gstin_ecom.length() == 15) {
                String[] part = gstin_ecom.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                if(part.length != 7)
                {
                    mFlag = false;
                }
                else if (CHECK_INTEGER_VALUE == checkDataypeValue(part[0], "Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[1],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[2],"Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[3],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[4],"Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[5],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[6],"Int")) {

                               /* int length = gstin.length() -1;
                                if(Integer.parseInt(String.valueOf(gstin.charAt(length))) ==  checksumGSTIN(gstin.substring(0,length)))*/
                    mFlag = true;
                } else {
                    mFlag = false;
                }
            } else {
                mFlag = false;
            }
            if(!mFlag)
            {
                MsgBx.Show("Error","Please enter valid ecommerce GSTIN");
                status = false;
                return status;
            }
            if(!(Double.parseDouble(taxval) >= 0 && Double.parseDouble(taxval) <=9999.99))
            {
                MsgBx.Show("Error","Please enter taxable value between 0 and 9999.99");
                status = false;
                return status;
            }
            if(!(Double.parseDouble(igstrate) >= 0 && Double.parseDouble(igstrate) <=99.99))
            {
                MsgBx.Show("Error","Please enter IGST Rate between 0 and 99.99");
                status = false;
                return status;
            }
            if(!(Double.parseDouble(cgstrate) >= 0 && Double.parseDouble(cgstrate) <=99.99))
            {
                MsgBx.Show("Error","Please enter CGST Rate between 0 and 99.99");
                status = false;
                return status;
            }if(!(Double.parseDouble(sgstrate) >= 0 && Double.parseDouble(sgstrate) <=99.99))
            {
                MsgBx.Show("Error","Please enter SGST Rate between 0 and 99.99");
                status = false;
                return status;
            }
            if(!(Double.parseDouble(igstamt) >= 0 && Double.parseDouble(igstamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter IGST Amount between 0 and 9999.99");
                status = false;
                return status;
            }if(!(Double.parseDouble(cgstamt) >= 0 && Double.parseDouble(cgstamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter CGST Amount between 0 and 9999.99");
                status = false;
                return status;
            }if(!(Double.parseDouble(sgstamt) >= 0 && Double.parseDouble(sgstamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter SGST Amount between 0 and 9999.99");
                status = false;
                return status;
            }if(!(Double.parseDouble(cessamt) >= 0 && Double.parseDouble(cessamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter cess Amount between 0 and 9999.99");
                status = false;
                return status;
            }
        }catch (Exception e)
        {

            MsgBx.Show("Error","An error occured due to invalid data");
            e.printStackTrace();
            status = false;;
        }finally {
            return status ;
        }

    }
    public static boolean validationCheckpoints_GSTR1_B2CL(String taxval,String igstrate, String igstamt,String cessamt)
    {

        boolean  status = true;

        try{

            if(!(Double.parseDouble(taxval) >= 0 && Double.parseDouble(taxval) <=9999.99))
            {
                MsgBx.Show("Error","Please enter taxable value between 0 and 9999.99");
                status = false;
                return status;
            }
            if(!(Double.parseDouble(igstrate) >= 0 && Double.parseDouble(igstrate) <=99.99))
            {
                MsgBx.Show("Error","Please enter IGST Rate between 0 and 99.99");
                status = false;
                return status;
            }

            if(!(Double.parseDouble(igstamt) >= 0 && Double.parseDouble(igstamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter IGST Amount between 0 and 9999.99");
                status = false;
                return status;
            }if(!(Double.parseDouble(cessamt) >= 0 && Double.parseDouble(cessamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter cess Amount between 0 and 9999.99");
                status = false;
                return status;
            }
        }catch (Exception e)
        {

            MsgBx.Show("Error","An error occured due to invalid data");
            e.printStackTrace();
            status = false;;
        }finally {
            return status ;
        }

    }

    public static boolean validationCheckpoints_GSTR1_B2CS(String taxval,
                                                          String igstrate, String cgstrate, String sgstrate,
                                                          String igstamt, String cgstamt, String sgstamt, String cessamt)
    {

        boolean  status = true;
        try{
            if(!(Double.parseDouble(taxval) >= 0 && Double.parseDouble(taxval) <=9999.99))
            {
                MsgBx.Show("Error","Please enter taxable value between 0 and 9999.99");
                status = false;
                return status;
            }
            if(!(Double.parseDouble(igstrate) >= 0 && Double.parseDouble(igstrate) <=99.99))
            {
                MsgBx.Show("Error","Please enter IGST Rate between 0 and 99.99");
                status = false;
                return status;
            }
            if(!(Double.parseDouble(cgstrate) >= 0 && Double.parseDouble(cgstrate) <=99.99))
            {
                MsgBx.Show("Error","Please enter CGST Rate between 0 and 99.99");
                status = false;
                return status;
            }if(!(Double.parseDouble(sgstrate) >= 0 && Double.parseDouble(sgstrate) <=99.99))
            {
                MsgBx.Show("Error","Please enter SGST Rate between 0 and 99.99");
                status = false;
                return status;
            }
            if(!(Double.parseDouble(igstamt) >= 0 && Double.parseDouble(igstamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter IGST Amount between 0 and 9999.99");
                status = false;
                return status;
            }if(!(Double.parseDouble(cgstamt) >= 0 && Double.parseDouble(cgstamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter CGST Amount between 0 and 9999.99");
                status = false;
                return status;
            }if(!(Double.parseDouble(sgstamt) >= 0 && Double.parseDouble(sgstamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter SGST Amount between 0 and 9999.99");
                status = false;
                return status;
            }if(!(Double.parseDouble(cessamt) >= 0 && Double.parseDouble(cessamt) <=9999.99))
            {
                MsgBx.Show("Error","Please enter cess Amount between 0 and 9999.99");
                status = false;
                return status;
            }
        }catch (Exception e)
        {

            MsgBx.Show("Error","An error occured due to invalid data");
            e.printStackTrace();
            status = false;;
        }finally {
            return status ;
        }

    }
    public static boolean validationCheckpoints_GSTR2_B2B(String supplierType,String gstin_ori ,  String taxval,
                                                          String igstrate, String cgstrate, String sgstrate,
                                                          String igstamt, String cgstamt, String sgstamt, String cessamt)
    {

        boolean  status = true;
        boolean  mFlag = false;

        try{
            if(supplierType.equals("Registered")) {
                if (!(Double.parseDouble(taxval) >= 0 && Double.parseDouble(taxval) <= 9999.99)) {
                    MsgBx.Show("Error", "Please enter taxable value between 0 and 9999.99");
                    status = false;
                    return status;
                }
                if (!(Double.parseDouble(igstrate) >= 0 && Double.parseDouble(igstrate) <= 99.99)) {
                    MsgBx.Show("Error", "Please enter IGST Rate between 0 and 99.99");
                    status = false;
                    return status;
                }
                if (!(Double.parseDouble(cgstrate) >= 0 && Double.parseDouble(cgstrate) <= 99.99)) {
                    MsgBx.Show("Error", "Please enter CGST Rate between 0 and 99.99");
                    status = false;
                    return status;
                }
                if (!(Double.parseDouble(sgstrate) >= 0 && Double.parseDouble(sgstrate) <= 99.99)) {
                    MsgBx.Show("Error", "Please enter SGST Rate between 0 and 99.99");
                    status = false;
                    return status;
                }
                if (!(Double.parseDouble(igstamt) >= 0 && Double.parseDouble(igstamt) <= 9999.99)) {
                    MsgBx.Show("Error", "Please enter IGST Amount between 0 and 9999.99");
                    status = false;
                    return status;
                }
                if (!(Double.parseDouble(cgstamt) >= 0 && Double.parseDouble(cgstamt) <= 9999.99)) {
                    MsgBx.Show("Error", "Please enter CGST Amount between 0 and 9999.99");
                    status = false;
                    return status;
                }
                if (!(Double.parseDouble(sgstamt) >= 0 && Double.parseDouble(sgstamt) <= 9999.99)) {
                    MsgBx.Show("Error", "Please enter SGST Amount between 0 and 9999.99");
                    status = false;
                    return status;
                }
                if (!(Double.parseDouble(cessamt) >= 0 && Double.parseDouble(cessamt) <= 9999.99)) {
                    MsgBx.Show("Error", "Please enter cess Amount between 0 and 9999.99");
                    status = false;
                    return status;
                }
            }
        }catch (Exception e)
        {

            MsgBx.Show("Error","An error occured due to invalid data");
            e.printStackTrace();
            status = false;;
        }finally {
            return status ;
        }

    }

    public static int checkDataypeValue(String value, String type) {
        int flag =0;
        try {
            switch(type) {
                case "Int":
                    Integer.parseInt(value);
                    flag = 0;
                    break;
                case "Double" : Double.parseDouble(value);
                    flag = 1;
                    break;
                default : flag =2;
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            flag = -1;
        }
        return flag;
    }


}
