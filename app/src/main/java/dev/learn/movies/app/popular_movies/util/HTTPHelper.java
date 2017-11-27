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

import static dev.learn.movies.app.popular_movies.util.AppConstants.CREDITS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOST_POPULAR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.NOW_PLAYING;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RECOMMENDATIONS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.SIMILAR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TOP_RATED;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_AIRING_TODAY;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_ON_THE_AIR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_POPULAR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_TOP_RATED;
import static dev.learn.movies.app.popular_movies.util.AppConstants.UPCOMING;

/**
 * HTTPHelper - Contains network related helper methods
 */

public final class HTTPHelper {

    public static final String IMAGE_SIZE_SMALL = "w342";
    public static final String IMAGE_SIZE_MEDIUM = "w500";
    public static final String IMAGE_SIZE_XLARGE = "w780";
    private static final String TAG = HTTPHelper.class.getSimpleName();
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String SCHEME = "https";
    private static final String BASE_PATH = "/api.themoviedb.org";
    private static final String API_VERSION = "3";

    private static final String IMAGE_BASE_PATH = "/image.tmdb.org/t/p";
    private static final String API_KEY_PARAM = "api_key";
    private static final String SEPARATOR = "/";

    private static final String MOVIE_DETAIL_PATH = "movie";

    private static final String TV_PATH = "tv";

    private static final String MOVIE_REVIEWS_PATH = "reviews";

    private static final String MOVIE_TRAILERS_PATH = "videos";

    private static final String NOW_PLAYING_PATH = MOVIE_DETAIL_PATH + SEPARATOR + NOW_PLAYING;

    private static final String MOST_POPULAR_PATH = MOVIE_DETAIL_PATH + SEPARATOR + MOST_POPULAR;

    private static final String TOP_RATED_PATH = MOVIE_DETAIL_PATH + SEPARATOR + TOP_RATED;

    private static final String UPCOMING_PATH = MOVIE_DETAIL_PATH + SEPARATOR + UPCOMING;

    private static final String TV_AIRING_TODAY_PATH = TV_PATH + SEPARATOR + TV_AIRING_TODAY;

    private static final String TV_ON_THE_AIR_PATH = TV_PATH + SEPARATOR + TV_ON_THE_AIR;

    private static final String TV_POPULAR_PATH = TV_PATH + SEPARATOR + TV_POPULAR;

    private static final String TV_TOP_RATED_PATH = TV_PATH + SEPARATOR + TV_TOP_RATED;

    private static final String LANGUAGE = "language";

    private static final String EN_US = "en-US";

    private static final String PAGE = "page";

    private static final String YOUTUBE_BASE_PATH = "/youtube.com";

    private static final String YOUTUBE_WATCH_PATH = "watch";

    private static final String IMDB_BASE_PATH = "/www.imdb.com";

    private static final String IMDB_TITLE_PATH = "title";

    /**
     * Builds movie/now_playing URL
     *
     * @return movie/now_playing URL
     */
    public static URL buildNowPlayingURL(int page) {
        return buildTMDBURL(NOW_PLAYING_PATH, page);
    }

    /**
     * Builds movie/upcoming URL
     *
     * @return movie/upcoming URL
     */
    public static URL buildUpcomingURL(int page) {
        return buildTMDBURL(UPCOMING_PATH, page);
    }

    /**
     * Builds movie/popular URL
     *
     * @return movie/popular URL
     */
    public static URL buildMostPopularURL(int page) {
        return buildTMDBURL(MOST_POPULAR_PATH, page);
    }

    /**
     * Builds movie/top_rated URL
     *
     * @return movie/top_rated URL
     */
    public static URL builTopRatedURL(int page) {
        return buildTMDBURL(TOP_RATED_PATH, page);
    }

    /**
     * Builds tv/airing_today URL
     *
     * @return tv/airing_today URL
     */
    public static URL buildTVAiringTodayURL(int page) {
        return buildTMDBURL(TV_AIRING_TODAY_PATH, page);
    }

    /**
     * Builds tv/on_the_air URL
     *
     * @return tv/on_the_air URL
     */
    public static URL buildTVOnTheAirURL(int page) {
        return buildTMDBURL(TV_ON_THE_AIR_PATH, page);
    }

    /**
     * Builds tv/popular URL
     *
     * @return tv/popular URL
     */
    public static URL builldTVPopularURL(int page) {
        return buildTMDBURL(TV_POPULAR_PATH, page);
    }

    /**
     * Builds tv/top_rated URL
     *
     * @return tv/top_rated URL
     */
    public static URL buildTVTopRatedURL(int page) {
        return buildTMDBURL(TV_TOP_RATED_PATH, page);
    }

    /**
     * Builds movie/{movieId} URL
     *
     * @param movieId Movie Id to fetch details for
     * @return movieDetail URL
     */
    public static URL buildMovieDetailsURL(String movieId) {
        return buildTMDBURL(MOVIE_DETAIL_PATH + SEPARATOR + movieId, -1);
    }

    /**
     * Builds movie/{movieId}/reviews URL
     *
     * @param movieId Movie Id to fetch details for
     * @return movieReviews URL
     */
    public static URL buildMovieReviewsURL(String movieId, int page) {
        String path = MOVIE_DETAIL_PATH + SEPARATOR + movieId + SEPARATOR + MOVIE_REVIEWS_PATH;
        return buildTMDBURL(path, page);
    }

    /**
     * Builds movie/{movieId}/videos URL
     *
     * @param movieId Movie Id to fetch details for
     * @return movieTrailers URL
     */
    public static URL buildMovieTrailersURL(String movieId) {
        String path = MOVIE_DETAIL_PATH + SEPARATOR + movieId + SEPARATOR + MOVIE_TRAILERS_PATH;
        return buildTMDBURL(path, -1);
    }

    public static URL buildMovieRecommendationsURL(String movieId) {
        String path = MOVIE_DETAIL_PATH + SEPARATOR + movieId + SEPARATOR + RECOMMENDATIONS;
        return buildTMDBURL(path, -1);
    }

    public static URL buildSimilarMoviesURL(String movieId) {
        String path = MOVIE_DETAIL_PATH + SEPARATOR + movieId + SEPARATOR + SIMILAR;
        return buildTMDBURL(path, -1);
    }

    public static URL buildMovieCastURL(String movieId) {
        String path = MOVIE_DETAIL_PATH + SEPARATOR + movieId + SEPARATOR + CREDITS;
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

    public static URL buildIMDBURL(String imdbId) {
        Uri uri = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(IMDB_BASE_PATH)
                .appendEncodedPath(IMDB_TITLE_PATH)
                .appendEncodedPath(imdbId)
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

    /**
     * Builds TMDB Api URL
     *
     * @return URL
     */
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