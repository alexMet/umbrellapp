package com.example.root.umbrellapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public abstract class HttpGetWheather extends AsyncTask<String, Void, Integer> implements HelperActivity.HttpData {
    private String townName;
    private String[] icons, maxTemps, minTemps;

    public abstract void onResponseReceived(int error, String townName, String[] icons, String[] maxTemps, String[] minTemps);

    @Override
    protected Integer doInBackground(String... input) {
        final String TAG = input[0];
        String data = "";
        String server = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&appid=cb89a2d010e5f7e225c403c1f7b1335f&units=metric&cnt=6&";
        int error = -1, result = 0;
        java.net.URL url;
        HttpURLConnection conn = null;

        String URL = server + input[1];

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
                icons = new String[6];
                maxTemps = new String[6];
                minTemps = new String[6];

                // Metatrepoume ta data pou mas gyrise o server se json
                JSONObject obj = new JSONObject(data);

                // Pairnoume to onoma ths polhs kai ton kwdiko ths xwras
                JSONObject j = obj.getJSONObject("city");
                townName = j.getString("name") + ", " + j.getString("country");

                // Pairnoume thn lista me tis 8ermokrasies
                JSONArray list = obj.getJSONArray("list");

                // Pairnoume tis 8ermokrasies twn 5 hmerwn
                for (int i = 0; i < list.length(); i++) {
                    j = list.getJSONObject(i);

                    JSONObject j2 = j.getJSONObject("temp");
                    maxTemps[i] = String.valueOf((int) j2.getDouble("max"));
                    minTemps[i] = String.valueOf((int) j2.getDouble("min"));
                    icons[i] = j.getJSONArray("weather").getJSONObject(0).getString("icon");
                }

            } catch (JSONException e) {
                error = 1;
                Log.e(TAG, "JSONException");
                e.printStackTrace();
            } catch (Exception e) {
                error = 1;
                Log.e(TAG, "Exception");
                e.printStackTrace();
            }
        }
        else
            error = 1;

        return error;
    }

    @Override
    protected void onPostExecute(Integer error) {
        onResponseReceived(error, townName, icons, maxTemps, minTemps);
    }
}
