package com.example.root.umbrellapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;

public abstract class HttpGetNames extends AsyncTask<String, Void, Integer> implements HelperActivity.HttpIntString {
    ArrayList<String> townNames = null;
    public abstract void onResponseReceived(int error, ArrayList<String> townNames);

    @Override
    protected Integer doInBackground(String... input) {
        final String TAG = input[0];
        String data = "";
        String serverN = "http://api.openweathermap.org/data/2.5/find?mode=json&appid=cb89a2d010e5f7e225c403c1f7b1335f&type=like&cnt=10&";
        int error = -1, result = 0;
        java.net.URL url;
        HttpURLConnection conn = null;

        String URL = serverN + input[1];

        try {

            url = new URL(URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(HelperActivity.timeoutTime);
            conn.setReadTimeout(HelperActivity.timeoutTime);
            conn.setDoInput(true);
            InputStream in = new BufferedInputStream(conn.getInputStream());
            data = HelperActivity.readStream(in);
            result = conn.getResponseCode();

        } catch (ProtocolException e) {
            error = 1;
            Log.e(TAG, "ProtocolException");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            error = 1;
            Log.e(TAG, "MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            error = 2;
            Log.e(TAG, "IOException");
            e.printStackTrace();
        } finally {
            if (null != conn)
                conn.disconnect();
        }

        if (error < 0 && result == 200) {
            try {
                JSONObject obj = new JSONObject(data);
                JSONArray locations = obj.getJSONArray("list");
                townNames = new ArrayList<String>();

                for (int i = 0; i < locations.length(); i++) {
                    JSONObject j = locations.getJSONObject(i);
                    townNames.add(j.getString("name") + ", " + j.getJSONObject("sys").getString("country"));
                }
            } catch (JSONException e) {
                error = 1;
                Log.e(TAG, "JSONException");
                e.printStackTrace();
            }
        }
        else
            error = 1;

        return error;
    }

    @Override
    protected void onPostExecute(Integer error) {
        onResponseReceived(error, townNames);
    }
}

