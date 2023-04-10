package com.savvy.hrmsnewapp.parser;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.savvy.hrmsnewapp.utils.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSONParser {
    private static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    private String securityToken = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences shared;


    public JSONParser(Context mContext) {

        shared = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        securityToken = shared.getString(Constants.EMPLOYEE_ID_FINAL, "");
    }
    public JSONParser() {
    }

    // function get json from url
    // by making HTTP POST or GET method
    public String makeHttpRequest(String url, String method) {
        // Making HTTP request
        try {
            // check for request method
            if (method.equals("POST")) {
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.addHeader("securityToken",securityToken );
                //httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } else if (method.equals("GET")) {
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                //  String paramString = URLEncodedUtils.format(params, "utf-8");
                // url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
                httpGet.addHeader("securityToken", securityToken);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.i("JSON Parser", json);// me
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
          /*try {
              jObj = new JSONObject(json);
          } catch (JSONException e) {
              Log.e("JSON Parser", "Error parsing data " + e.toString());
          }*/
        // return JSON String
        return json;
    }
}
