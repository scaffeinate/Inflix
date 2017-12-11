package dev.learn.movies.app.popular_movies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by sudhar on 12/11/17.
 */

public class HTTPUtils {
    /**
     * Fetches the response from the url
     *
     * @param url Input URL
     * @return response String
     * @throws IOException when an exception occurs trying to reach the URL
     */
    public static String getHTTPResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String response = null;
        try {
            InputStream in = connection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                response = scanner.next();
            }
            scanner.close();
        } finally {
            connection.disconnect();
        }
        return response;
    }

    /**
     * Checks whether the device is connected to the internet
     * Reference: https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
     *
     * @param context Context
     * @return isNetworkEnabled
     */
    public static boolean isNetworkEnabled(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
