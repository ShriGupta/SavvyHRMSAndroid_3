package com.savvy.hrmsnewapp.rahul;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class AddressDetails  extends AsyncTask<String,String,String> {
    Context context;
    double lat,lon;

    public static String add;

    public AddressDetails(Context context, double lat, double lon) {
        Log.e("AddressDetails: ", ""+lat+"\n"+lon);
        this.context = context;
        this.lat = lat;
        this.lon = lon;
    }

    protected String doInBackground(String... params){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lon+"&sensor=true");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            Log.e("doInBackground: ", buffer.toString() );

            JSONObject parentobj = new JSONObject(buffer.toString());

            JSONArray mainobj = parentobj.getJSONArray("results");
            int i=0;
            while (i<1) {
                JSONObject finalobject = mainobj.getJSONObject(i);

                add = (finalobject.getString("formatted_address"));

                i++;
            }
            return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("onPostExecute: ", s);

    }
}
