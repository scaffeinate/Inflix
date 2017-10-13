package dev.learn.movies.app.popularmovies_udacity.network;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by sudharti on 10/10/17.
 */

public final class HTTPHelper {

    private final static String TAG = HTTPHelper.class.getSimpleName();

    private final static String API_KEY = "API_KEY";

    private final static String SCHEME = "https";

    private final static String BASE_PATH = "/api.themoviedb.org";

    private final static String API_VERSION = "3";

    private final static String DISCOVER_PATH = "discover/movie";

    private final static String IMAGE_BASE_PATH = "/image.tmdb.org/t/p";


    public final static String IMAGE_SIZE_SMALL = "w342";

    public final static String IMAGE_SIZE_MEDIUM = "w500";

    public final static String IMAGE_SIZE_XLARGE = "w780";

    private final static String API_KEY_PARAM = "api_key";

    private final static String MOST_POPULAR_PATH = "movie/popular";

    private final static String TOP_RATED_PATH = "movie/top_rated";

    private final static String MOVIE_DETAIL_PATH = "movie";

    private final static String LANGUAGE = "language";

    private final static String EN_US = "en-US";

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

    public static Uri buildImageResourceUri(String imgFile) {
        return buildImageResourceUri(imgFile, IMAGE_SIZE_MEDIUM);
    }

    public static Uri buildImageResourceUri(String imgFile, String imageSize) {
        return new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(IMAGE_BASE_PATH)
                .appendEncodedPath(imageSize)
                .appendEncodedPath(imgFile)
                .build();
    }

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
}