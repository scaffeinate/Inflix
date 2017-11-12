package dev.learn.movies.app.popular_movies.util;

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

import dev.learn.movies.app.popular_movies.BuildConfig;

import static dev.learn.movies.app.popular_movies.util.AppConstants.MOST_POPULAR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.NOW_PLAYING;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TOP_RATED;
import static dev.learn.movies.app.popular_movies.util.AppConstants.UPCOMING;

/**
 * HTTPHelper - Contains network related helper methods
 */

public final class HTTPHelper {

    public final static String IMAGE_SIZE_SMALL = "w342";
    public final static String IMAGE_SIZE_MEDIUM = "w500";
    public final static String IMAGE_SIZE_XLARGE = "w780";
    private final static String TAG = HTTPHelper.class.getSimpleName();
    private final static String API_KEY = BuildConfig.API_KEY;
    private final static String SCHEME = "https";
    private final static String BASE_PATH = "/api.themoviedb.org";
    private final static String API_VERSION = "3";
    private final static String DISCOVER_PATH = "discover/movie";
    private final static String IMAGE_BASE_PATH = "/image.tmdb.org/t/p";
    private final static String API_KEY_PARAM = "api_key";

    private final static String MOVIE_DETAIL_PATH = "movie/";

    private final static String MOVIE_REVIEWS_PATH = "reviews/";

    private final static String MOVIE_TRAILERS_PATH = "videos/";

    private final static String NOW_PLAYING_PATH = MOVIE_DETAIL_PATH + NOW_PLAYING;

    private final static String MOST_POPULAR_PATH = MOVIE_DETAIL_PATH + MOST_POPULAR;

    private final static String TOP_RATED_PATH = MOVIE_DETAIL_PATH + TOP_RATED;

    private final static String UPCOMING_PATH = MOVIE_DETAIL_PATH + UPCOMING;

    private final static String LANGUAGE = "language";

    private final static String EN_US = "en-US";

    private final static String PAGE = "page";

    private static final String YOUTUBE_BASE_PATH = "/youtube.com";

    private static final String YOUTUBE_WATCH_PATH = "watch";


    public static URL buildNowPlayingURL(int page) {
        return buildTMDBURL(NOW_PLAYING_PATH, page);
    }

    /**
     * Builds movie/popular URL
     *
     * @return popular URL
     */
    public static URL buildMostPopularURL(int page) {
        return buildTMDBURL(MOST_POPULAR_PATH, page);
    }

    /**
     * Builds movie/top_rated URL
     *
     * @return topRated URL
     */
    public static URL builTopRatedURL(int page) {
        return buildTMDBURL(TOP_RATED_PATH, page);
    }

    public static URL buildUpcomingURL(int page) {
        return buildTMDBURL(UPCOMING_PATH, page);
    }

    /**
     * Builds movie/{movieId} URL
     *
     * @param movieId Movie Id to fetch details for
     * @return movieDetail URL
     */
    public static URL buildMovieDetailsURL(String movieId) {
        return buildTMDBURL(MOVIE_DETAIL_PATH + movieId, -1);
    }

    /**
     * Builds movie/{movieId}/reviews URL
     *
     * @param movieId Movie Id to fetch details for
     * @return movieReviews URL
     */
    public static URL buildMovieReviewsURL(String movieId) {
        String path = MOVIE_DETAIL_PATH + movieId + "/" + MOVIE_REVIEWS_PATH;
        return buildTMDBURL(path, -1);
    }

    /**
     * Builds movie/{movieId}/videos URL
     *
     * @param movieId Movie Id to fetch details for
     * @return movieTrailers URL
     */
    public static URL buildMovieTrailersURL(String movieId) {
        String path = MOVIE_DETAIL_PATH + movieId + "/" + MOVIE_TRAILERS_PATH;
        return buildTMDBURL(path, -1);
    }

    /**
     * Builds YouTube url given the key
     *
     * @param key YouTube video key
     * @return youtubeURL
     */
    public static URL buildYouTubeURL(String key) {
        Uri uri = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(YOUTUBE_BASE_PATH)
                .appendEncodedPath(YOUTUBE_WATCH_PATH)
                .appendQueryParameter("v", key)
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

    private static URL buildTMDBURL(String path, int page) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(BASE_PATH)
                .appendEncodedPath(API_VERSION)
                .appendEncodedPath(path)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE, EN_US);

        if (page != -1) {
            uriBuilder.appendQueryParameter(PAGE, String.valueOf(page));
        }

        Uri uri = uriBuilder.build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }
}