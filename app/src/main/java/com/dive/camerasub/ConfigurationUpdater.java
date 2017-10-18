package com.dive.camerasub;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by vinicius.barbosa on 18/10/2017.
 */

public class ConfigurationUpdater {
    private static final String TAG = "ConfigurationUpdater";

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public void UpdateAppConfiguration() {
        try {
            JSONObject json = new RetrieveConfigTask().execute("https://raw.githubusercontent.com/vizeke/camerasub/master/app.config.json").get();
            if (!json.equals(null)) {
                Configuration.mTimeLoop = json.getInt("timeLoop");
                Configuration.mDefaultFolder = json.getString("defaultFolder");
            }
        }catch(Exception ex) {
            Log.d(TAG, "Failed retrieving config data");
        }
    }

    private class RetrieveConfigTask extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... urls) {
            try {
                return readJsonFromUrl(urls[0]);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
