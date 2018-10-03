package com.wep.gstcall.api.util;

import android.os.Environment;

/**
 * Created by PriyabratP on 21-11-2016.
 */

public class Config {

    public static final String FILE_PATH = Environment.getExternalStorageDirectory().toString();

    public static final String BASE_URL = "http://103.230.84.67";
/*
    public static final String GSTR1_URL = "http://13.71.118.152/AndroWeb/API/GSTR1Save/SendGSTR1Save";

    public static final String GSTR1A_URL = BASE_URL+"/GstAPI/API/GSTR1/sendGstr1A";

    public static final String GSTR2_URL = "http://13.71.118.152/AndroWeb/API/GSTR2Save/SendGSTR2Save";

    public static final String GSTR_GET_API = BASE_URL+"/API/API/GSTR2A?gstin=04AABFN9870CMZT&ret_period=072016&action=B2B&action_required=Y";

    public static final String GSTR2_B2B_GET_API = BASE_URL+"/API/API/GSTR2AJson?gstin=04AABFN9870CMZT&ret_period=072016&action=B2B&action_required=Y";

    public static final String GSTR1_SUMMERY_GET_API = BASE_URL+"/API/API/GSTR1Json?ret_period=072016&gstin=04AABFN9870CMZT&action=RETSUM";

    public static final String GSTR1A_SUMMERY_GET_API = BASE_URL+"/API/API/GSTR1Json?ret_period=072016&gstin=04AABFN9870CMZT&action=RETSUM";

    public static final String GSTR3_GET = "http://103.230.84.67/API/API/GSTR3Json?ret_period=072016&gstin=04AABFN9870CMZT&action=RETDET";

    */

    public static final String GSTR2_DAY= BASE_URL+"/API/API/Inward?";

    public static final String GSTR1_DAY= BASE_URL+"/API/API/Outward?";

    public static final String Upload_No_of_Invoices = "http://metering.wepgspservices.com/api/Device/addBillCount?";

    public static final String HeaderAuthorizationData_POST_APIS_DEMO_TESTING ="Ocp-Apim-Subscription-Key@07cde031cc1646efae45746a8c844974"+
            ",SOURCE_TYPE@POS";

    public static final String HeaderAuthorizationData_POST_APIS_PRODUCTION ="Ocp-Apim-Subscription-Key@42eef4feafa44d988877b51a827db058"+
            ",SOURCE_TYPE@POS";

    public static final String Header_TokenAuth ="Ocp-Apim-Subscription-Key@07cde031cc1646efae45746a8c844974," +
            "client_id@e03001c6-59b7-4bbb-919a-778108e643b9,client_secret@nOViR/b/Q7L/iwQkzWIDG19DzcqbaiC82yNVFF3J9qc=";

    public static final String Base_URL_Azure = "https://api.wepgst.com";

    public static final String GET_TOKEN_API = "/wepapis/auth/token/get";
    public static final String POST_GSTR1_PRODUCTION = "/v1/gstr1/savegstr1";
    public static final String POST_GSTR2_PRODUCTION = "/v1/gstr2/savegstr2";

    public static final String GET_GSTR1_PRODUCTION = "/v1/gstr1/download/gstr1csv";
    public static final String GET_GSTR1A_PRODUCTION = "/v1/gstr1a/download/gstr1acsv";
    public static final String GET_GSTR1_SUMMARY_PRODUCTION = "/v1/gstr1/download/gstr1summarycsv";
    public static final String GET_GSTR2A_PRODUCTION = "/v1/gstr2a/download/gstr2acsv";
    public static final String GET_GSTR2_RECONCILED_PRODUCTION = "/v1/gstr2/download/reconciledcsv";
    public static final String GET_GSTR3_PRODUCTION = "/v1/gstr3/download/gstr3csv";
     // DEMO APIs
    public static final String Base_URL_DEMO = "http://api.wepgspservices.com/api";
    public static final String POST_GSTR1_DEMO = "/GSTR1Save/SendGSTR1Save";
    public static final String POST_GSTR2_DEMO = "/GSTR2Save/SendGSTR2Save" +
            "";
    public static final String GET_GSTR1_DEMO = "/GetGSTR1CSV/Download";
    public static final String GET_GSTR1A_DEMO = "/GetGSTR1ACSV/Download";
    public static final String GET_GSTR1_SUMMARY_DEMO = "/GetGSTR1SummaryCSV/Download";
    public static final String GET_GSTR2A_DEMO = "/GetGSTR2ACSV/Download";
    public static final String GET_GSTR2_RECONCILED_DEMO = "/GetReconciledCSV/Download";
    public static final String GET_GSTR3_DEMO = "/GetGSTR3CSV/Download";

    public static final String url_sms_demo = "http://api.wepgspservices.com/api/POS/SMS?";



    /*public static final String GSTR1_SAVE_AZURE_API = "/asp/gstr1/save";
    public static final String GSTR2_SAVE_AZURE_API = "/asp/gstr2/save";*/




}
