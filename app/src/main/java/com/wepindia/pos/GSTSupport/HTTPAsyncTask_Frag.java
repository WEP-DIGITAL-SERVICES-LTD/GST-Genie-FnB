package com.wepindia.pos.GSTSupport;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.util.Log;

import com.wep.gstcall.api.http.HTTPAsyncTask;
import com.wepindia.pos.views.Reports.FragmentGSTLink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by RichaA on 4/27/2017.
 */

public class HTTPAsyncTask_Frag extends AsyncTask<Void,Void,String> {

    private static final String TAG = HTTPAsyncTask.class.getSimpleName();
    private final String USER_AGENT = "Mozilla/5.0";
    private Activity activity;
    private Fragment fragment;
    private String strJson;
    private int requestCode;
    private String url;
    private HTTPAsyncTask_Frag.OnHTTPRequestCompletedListener httpRequestCompletedListener;
    public static int HTTP_GET = 1;
    public static int HTTP_POST = 2;
    private int method;
    private String Header;
    public HTTPAsyncTask_Frag(Fragment fragment, int method, String strJson, int requestCode, String url, String Header)
    {
        this.fragment = fragment;
        this.strJson = strJson;
        this.requestCode = requestCode;
        this.url = url;
        this.method = method;
        httpRequestCompletedListener = (HTTPAsyncTask_Frag.OnHTTPRequestCompletedListener) activity;
        this.Header = Header;
    }

    public HTTPAsyncTask_Frag(Fragment fragment, int method, int requestCode, String url)
    {
        this.fragment = fragment;
        this.requestCode = requestCode;
        this.url = url;
        this.method = method;
        httpRequestCompletedListener = (HTTPAsyncTask_Frag.OnHTTPRequestCompletedListener) fragment;

    }

    public HTTPAsyncTask_Frag(Activity activity, int method, int requestCode, String url)
    {
        this.activity = activity;
        this.requestCode = requestCode;
        this.url = url;
        this.method = method;
        httpRequestCompletedListener = (HTTPAsyncTask_Frag.OnHTTPRequestCompletedListener) activity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        String resp = "";
        if(this.method == HTTP_GET)
        {
            resp = sendHTTPGETData(url);
        }
        else if(this.method == HTTP_POST)
        {
            resp = sendHTTPData(url,strJson);
        }
        return resp;
    }

    @Override
    protected void onPostExecute(String resp) {
        super.onPostExecute(resp);
        httpRequestCompletedListener.onHttpRequestComplete(requestCode,resp);
    }

    public interface OnHTTPRequestCompletedListener {
        void onHttpRequestComplete(int requestCode,String filePath);
    }

    public String sendHTTPData(String urlpath, String jsonData) {
        String resp = null;
        HttpURLConnection connection = null;
        try {
            URL obj = new URL(urlpath);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.setDoInput(true);
            if(Header.length()>0)
            {
                String[] headerData=Header.split(",");
                for(String head : headerData)
                {
                    String[] prop = head.split("@");
                    con.setRequestProperty(prop[0], prop[1]);
                }
            }
            OutputStream os = con.getOutputStream();
            os.write(jsonData.getBytes());
            os.flush();
            os.close();
            // For POST only - END

            int responseCode = con.getResponseCode();
            System.out.println("\n Sending 'POST ' request to URL : " + url);
            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
                System.out.println(response.toString());
                resp = String.valueOf(response);
            } else {
                System.out.println("POST request not worked");
            }
        } catch (Exception exception){
            Log.e(TAG, exception.toString());
            resp = null;
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return resp;
    }

    private String sendHTTPGETData(String url) {
        String resp = null;
        try{
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //print result
            Log.d(TAG,response.toString());
            resp = response.toString();
        }catch (Exception e){
            Log.d(TAG,e.toString());
        }
        return resp;
    }
}
