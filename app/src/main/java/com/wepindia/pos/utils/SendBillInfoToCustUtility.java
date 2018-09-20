package com.wepindia.pos.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.wep.gstcall.api.util.Config;
import com.wepindia.pos.Constants;
import com.wepindia.pos.GSTSupport.HTTPAsyncTask_Frag;

import java.net.URLEncoder;

/**
 * Created by RichaA on 4/10/2018.
 */

public class SendBillInfoToCustUtility {


    String TAG  = SendBillInfoToCustUtility.class.getName();
    private String urlpath="";
    private String messageContent="";
    private String subject = "";
    private String emailContent="";
    private String CustMobile="";
    private boolean isMobile= false;
    private boolean isEmail = false;
    private boolean isWhatsApp = false;
    private String firmName = "";
 /*   private ViewBillFragment activity;
    private BillingFragment activity1;
    private PurchaseOrderFragment activity2;
    private GoodsInwardNoteFragment activity3;*/
//    private ListPOGINNote activity4;
    private Activity activity;
    private Fragment fragment;
    private Context context;
    private String messageContentForEmail = "";
    String custEmail, attachment, filename;
    private HTTPAsyncTask_Frag.OnHTTPRequestCompletedListener httpRequestCompletedListener;


    public SendBillInfoToCustUtility(Context context, String subject, String emailContent, String messageContent, String CustMobile, boolean isMobile,
                                     boolean isEmail, boolean isWhatsApp, Fragment fragment,
                                     String custEmail, String attachment, String filename, String firmName) {
        this.context = context;
        this.subject = subject;
        this.urlpath = Config.url_sms_demo;
        this.messageContent = messageContent;
        this.emailContent = emailContent;
        this.CustMobile = CustMobile;
        this.isMobile = isMobile;
        this.isEmail = isEmail;
        this.isWhatsApp = isWhatsApp;
        this.fragment = fragment;
        httpRequestCompletedListener = (HTTPAsyncTask_Frag.OnHTTPRequestCompletedListener) fragment;
        this.custEmail = custEmail;
        this.attachment = attachment;
        this.filename = filename;
        this.firmName = firmName;
    }

    public SendBillInfoToCustUtility(Context context, String subject, String emailContent, String messageContent, String CustMobile, boolean isMobile,
                                     boolean isEmail, boolean isWhatsApp, Activity activity,
                                     String custEmail, String attachment, String filename, String firmName) {
        this.context = context;
        this.subject = subject;
        this.urlpath = Config.url_sms_demo;
        this.messageContent = messageContent;
        this.emailContent = emailContent;
        this.CustMobile = CustMobile;
        this.isMobile = isMobile;
        this.isEmail = isEmail;
        this.isWhatsApp = isWhatsApp;
        this.activity = activity;
        httpRequestCompletedListener = (HTTPAsyncTask_Frag.OnHTTPRequestCompletedListener) fragment;
        this.custEmail = custEmail;
        this.attachment = attachment;
        this.filename = filename;
        this.firmName = firmName;
    }

    public void sendBillInfo()
    {
        try{
            if(isMobile && !CustMobile.isEmpty())
            {
                String msg ="MobileNo="+CustMobile+"&Message="+URLEncoder.encode(messageContent.trim(),"UTF-8")+"&AppId=wepsalt";
                if (fragment != null)
                    new HTTPAsyncTask_Frag(fragment, Constants.HTTP_GET, Constants.SMS_SENDING, urlpath+msg).execute();
                if (activity != null)
                    new HTTPAsyncTask_Frag(activity, Constants.HTTP_GET, Constants.SMS_SENDING, urlpath+msg).execute();
            }
            if(isEmail && !custEmail.equals(""))
            {
                sendEmail();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendEmail()
    {
        new SendEmail().execute();
    }


    class SendEmail extends AsyncTask{

        boolean error = false;
        String errMsg = "";

        @Override
        protected Object doInBackground(Object[] objects) {

            try {

                GMailSender sender = new GMailSender("noreply@wepdigital.com", "wep@12345");

                sender.sendMail(subject, emailContent,

                        "noreply@wepdigital.com",

                        custEmail, attachment, filename);

            } catch (Exception e) {
                error = true;
                errMsg = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (error) {
               Toast.makeText(context, "Some network issue occurred.", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context, "Mail/SMS sent successfully.", Toast.LENGTH_SHORT).show();
            }
            error = false;
        }
    }

    public void sendEmail1() {
        Log.i("Send email", "");
        messageContentForEmail = messageContent;
        String TO = custEmail;
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bill Data");
        emailIntent.putExtra(Intent.EXTRA_TEXT, messageContentForEmail);

        try {
            fragment.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            //Toast.makeText(get, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

}

