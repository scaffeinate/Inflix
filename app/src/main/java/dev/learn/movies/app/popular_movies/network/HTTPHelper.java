package dev.learn.movies.app.popular_movies.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * HTTPHelper - Contains network related helper methods
 */

public final class HTTPHelper {

    public final static String IMAGE_SIZE_SMALL = "w342";
    public final static String IMAGE_SIZE_MEDIUM = "w500";
    public final static String IMAGE_SIZE_XLARGE = "w780";
    private final static String TAG = HTTPHelper.class.getSimpleName();
    private final static String API_KEY = "API_KEY";
    private final static String SCHEME = "https";
    private final static String BASE_PATH = "/api.themoviedb.org";
    private final static String API_VERSION = "3";
    private final static String DISCOVER_PATH = "discover/movie";
    private final static String IMAGE_BASE_PATH = "/image.tmdb.org/t/p";
    private final static String API_KEY_PARAM = "api_key";

    private final static String MOST_POPULAR_PATH = "movie/popular";

    private final static String TOP_RATED_PATH = "movie/top_rated";

    private final static String MOVIE_DETAIL_PATH = "movie";

    private final static String LANGUAGE = "language";

    private final static String EN_US = "en-US";

    /**
     * Builds discover/movie url
     *
     * @return discover URL
     */
    public static URL buildDiscoverURL() {
        Uri uri = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(BASE_PATH)
                .appendEncodedPath(API_VERSION)
                .appendEncodedPath(DISCOVER_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE, EN_US)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Builds movie/popular URL
     *
     * @return popular URL
     */
    public static URL buildMostPopularURL() {
        Uri uri = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(BASE_PATH)
                .appendEncodedPath(API_VERSION)
                .appendEncodedPath(MOST_POPULAR_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE, EN_US)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Builds movie/top_rated URL
     *
     * @return top_rated URL
     */
    public static URL builTopRatedURL() {
        Uri uri = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(BASE_PATH)
                .appendEncodedPath(API_VERSION)
                .appendEncodedPath(TOP_RATED_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE, EN_US)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Builds movie/{movieId} URL
     *
     * @param movieId Movie Id to fetch details for
     * @return movie_detail URL
     */
    public static URL buildMovieDetailsURL(String movieId) {
        Uri uri = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(BASE_PATH)
                .appendEncodedPath(API_VERSION)
                .appendEncodedPath(MOVIE_DETAIL_PATH)
                .appendPath(movieId)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE, EN_US)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Builds Image URL
     *
     * @param imgFile   Image File name
     * @param imageSize Image Size
     * @return image URL
     */
    public static Uri buildImageResourceUri(String imgFile, String imageSize) {
        return new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(IMAGE_BASE_PATH)
                .appendEncodedPath(imageSize)
                .appendEncodedPath(imgFile)
                .build();
    }

    /**
     * Fetches the response from the url
     *
     * @param url Input URL
     * @return response String
     * @throws IOException
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