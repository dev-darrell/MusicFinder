package com.dev.darrell.musicfinder;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ApiUtil {
    public static final String BASE_API_URL = "https://api.deezer.com/search";
    public static final String QUERY_PARAMETER = "q";

    public static URL BuildUrl(String title) {
        URL url = null;
            Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAMETER, title)
                    .build();
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String GetJson(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String json = null;
        try {
        InputStream inStrm = new BufferedInputStream(urlConnection.getInputStream());
        Scanner scanner = new Scanner(inStrm);
        if (scanner.hasNext()) {
            json = scanner.next();
        } else {
            return null;
        }
        } catch (Exception e) {
            Log.d("Error while scanning data:", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return json;
    }

//    public static

}
