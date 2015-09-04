package com.sferadev.htmlsourceviewer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.sferadev.htmlsourceviewer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class NetworkUtils {
    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";
    private static final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void openChromeTab(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        Bundle extras = new Bundle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            extras.putBinder(EXTRA_CUSTOM_TABS_SESSION, null);
            extras.putInt(EXTRA_CUSTOM_TABS_TOOLBAR_COLOR, context.getResources().getColor(R.color.colorPrimary));
        }
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static String getURLOutput(final String url) {
        return getURLOutput(url, "UTF-8");
    }

    public static String getURLOutput(final String url, final String encoding) {
        final String[] response = {null};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String tempUrl = url;
                    HttpURLConnection connection;
                    CookieHandler.setDefault(new CookieManager());
                    do {
                        connection = (HttpURLConnection) new URL(tempUrl).openConnection();
                        connection.setInstanceFollowRedirects(false);
                        connection.setUseCaches(false);
                        connection.setRequestMethod("GET");
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode >= 300 && responseCode < 400) {
                            String redirectedUrl = connection.getHeaderField("Location");
                            if (redirectedUrl == null) break;
                            tempUrl = redirectedUrl;
                        } else break;
                    } while (connection.getResponseCode() != HttpURLConnection.HTTP_OK);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), Charset.forName(encoding)));
                    response[0] = readAll(reader);
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return response[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
