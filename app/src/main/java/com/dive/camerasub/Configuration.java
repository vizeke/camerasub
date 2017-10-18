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

public class Configuration  {
    public static int mTimeLoop = 10000;
    public static String mDefaultFolder = "CameraSub";
}
