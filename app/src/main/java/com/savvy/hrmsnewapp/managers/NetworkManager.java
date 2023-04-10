
package com.savvy.hrmsnewapp.managers;

import android.util.Log;


import com.savvy.hrmsnewapp.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkManager {

    private final static String _TAG = "NetworkManager";   //Logging.makeLogTag(NetworkManager.class);

    /**
     * @param serverUrl    : source from data to be download
     * @param type         : Type of class object which is to be returned
     * @param dataToBeSend : json data to be send
     * @return array-list of passed class type
     */

    public static <T> Object requestDataFromServer(String serverUrl, Class<T> type, String dataToBeSend) {

        InputStream inputStream;
        try {

            URL url = new URL(serverUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(Constants.DEFAULT_TIME_OUT);
            conn.setConnectTimeout(Constants.DEFAULT_TIME_OUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
            outputStreamWriter.write(dataToBeSend);
            outputStreamWriter.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("Sending 'POST' request to URL : " + serverUrl);
            System.out.println("Post parameters : " + dataToBeSend);
            System.out.println("Response Code : " + responseCode);

            inputStream = conn.getInputStream();
            if (inputStream != null) {
                String res = convertInputStreamToString(inputStream);
                if (!res.isEmpty())
                    return ParseManager.parseServerResponse(res, type);
                else
                    return null;
            } else
                return null;

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null)
            result.append(line);

        Log.d("", "==result=" + result);

        inputStream.close();

        return result.toString();

    }

}

