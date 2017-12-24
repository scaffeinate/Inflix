package dev.learn.movies.app.popular_movies.utils;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import dev.learn.movies.app.popular_movies.BuildConfig;

import static dev.learn.movies.app.popular_movies.Inflix.CREDITS;
import static dev.learn.movies.app.popular_movies.Inflix.MOST_POPULAR;
import static dev.learn.movies.app.popular_movies.Inflix.NOW_PLAYING;
import static dev.learn.movies.app.popular_movies.Inflix.SIMILAR;
import static dev.learn.movies.app.popular_movies.Inflix.TOP_RATED;
import static dev.learn.movies.app.popular_movies.Inflix.TV_AIRING_TODAY;
import static dev.learn.movies.app.popular_movies.Inflix.TV_ON_THE_AIR;
import static dev.learn.movies.app.popular_movies.Inflix.TV_POPULAR;
import static dev.learn.movies.app.popular_movies.Inflix.TV_TOP_RATED;
import static dev.learn.movies.app.popular_movies.Inflix.UPCOMING;

/**
 * URIBuilderUtils - Contains network related helper methods
 */

public final class URIBuilderUtils {

    public static final String IMAGE_SIZE_SMALL = "w342";
    public static final String IMAGE_SIZE_MEDIUM = "w500";
    public static final String IMAGE_SIZE_XLARGE = "w780";
    private static final String TAG = URIBuilderUtils.class.getSimpleName();
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String SCHEME = "https";

    private static final String TMDB_BASE_PATH = "/www.themoviedb.org";

    private static final String API_BASE_PATH = "/api.themoviedb.org";
    private static final String API_VERSION = "3";

    private static final String IMAGE_BASE_PATH = "/image.tmdb.org/t/p";

    private static final String API_KEY_PARAM = "api_key";
    private static final String QUERY_PARAM = "query";
    private static final String PAGE_PARAM = "page";
    private static final String LANGUAGE_PARAM = "language";

    private static final String SEPARATOR = "/";

    private static final String MOVIE_PATH = "movie";

    private static final String TV_SHOW_PATH = "tv";

    private static final String MOVIE_REVIEWS_PATH = "reviews";

    private static final String VIDEOS_PATH = "videos";

    private static final String SEARCH_PATH = "search";

    private static final String NOW_PLAYING_PATH = MOVIE_PATH + SEPARATOR + NOW_PLAYING;

    private static final String MOST_POPULAR_PATH = MOVIE_PATH + SEPARATOR + MOST_POPULAR;

    private static final String TOP_RATED_PATH = MOVIE_PATH + SEPARATOR + TOP_RATED;

    private static final String UPCOMING_PATH = MOVIE_PATH + SEPARATOR + UPCOMING;

    private static final String TV_AIRING_TODAY_PATH = TV_SHOW_PATH + SEPARATOR + TV_AIRING_TODAY;

    private static final String TV_ON_THE_AIR_PATH = TV_SHOW_PATH + SEPARATOR + TV_ON_THE_AIR;

    private static final String TV_POPULAR_PATH = TV_SHOW_PATH + SEPARATOR + TV_POPULAR;

    private static final String TV_TOP_RATED_PATH = TV_SHOW_PATH + SEPARATOR + TV_TOP_RATED;

    private static final String MOVIE_SEARCH_PATH = SEARCH_PATH + SEPARATOR + MOVIE_PATH;

    private static final String TV_SHOW_SEARCH_PATH = SEARCH_PATH + SEPARATOR + TV_SHOW_PATH;

    private static final String EN_US = "en-US";

    private static final String YOUTUBE_BASE_PATH = "/youtube.com";

    private static final String YOUTUBE_IMAGE_BASE_PATH = "/img.youtube.com";

    private static final String YOUTUBE_VI = "vi";

    private static final String IMG_0_JPG = "0.jpg";

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
        return buildTMDBURL(MOVIE_PATH + SEPARATOR + movieId, -1);
    }

    /**
     * Builds movie/{movieId}/reviews URL
     *
     * @param movieId Movie Id to fetch details for
     * @return movieReviews URL
     */
    public static URL buildMovieReviewsURL(String movieId, int page) {
        String path = MOVIE_PATH + SEPARATOR + movieId + SEPARATOR + MOVIE_REVIEWS_PATH;
        return buildTMDBURL(path, page);
    }

    /**
     * Builds movie/{movieId}/videos URL
     *
     * @param movieId Movie Id to fetch details for
     * @return movieTrailers URL
     */
    public static URL buildMovieTrailersURL(String movieId) {
        String path = MOVIE_PATH + SEPARATOR + movieId + SEPARATOR + VIDEOS_PATH;
        return buildTMDBURL(path, -1);
    }

    public static URL buildSimilarMoviesURL(String movieId) {
        String path = MOVIE_PATH + SEPARATOR + movieId + SEPARATOR + SIMILAR;
        return buildTMDBURL(path, -1);
    }

    public static URL buildMovieCastURL(String movieId) {
        String path = MOVIE_PATH + SEPARATOR + movieId + SEPARATOR + CREDITS;
        return buildTMDBURL(path, -1);
    }

    /**
     * Builds tv_show/{tvShowId} URL
     *
     * @param tvShowId Movie Id to fetch details for
     * @return tvShowDetail URL
     */
    public static URL buildTVShowDetailsURL(String tvShowId) {
        return buildTMDBURL(TV_SHOW_PATH + SEPARATOR + tvShowId, -1);
    }

    /**
     * Builds tv_show/{tvShowId}/videos URL
     *
     * @param tvShowId Movie Id to fetch details for
     * @return tvShowTrailers URL
     */
    public static URL buildTVShowTrailersURL(String tvShowId) {
        String path = TV_SHOW_PATH + SEPARATOR + tvShowId + SEPARATOR + VIDEOS_PATH;
        return buildTMDBURL(path, -1);
    }

    public static URL buildSimilarTVShowsURL(String tvShowId) {
        String path = TV_SHOW_PATH + SEPARATOR + tvShowId + SEPARATOR + SIMILAR;
        return buildTMDBURL(path, -1);
    }

    public static URL buildTVShowCastURL(String movieId) {
        String path = TV_SHOW_PATH + SEPARATOR + movieId + SEPARATOR + CREDITS;
        return buildTMDBURL(path, -1);
    }

    public static URL buildMovieSearchURL(String query, int page) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(API_BASE_PATH)
                .appendEncodedPath(API_VERSION)
                .appendEncodedPath(MOVIE_SEARCH_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, EN_US)
                .appendQueryParameter(QUERY_PARAM, query)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page));

        Uri uri = uriBuilder.build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public static URL buildTVShowSearchURL(String query, int page) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(API_BASE_PATH)
                .appendEncodedPath(API_VERSION)
                .appendEncodedPath(TV_SHOW_SEARCH_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, EN_US)
                .appendQueryParameter(QUERY_PARAM, query)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page));

        Uri uri = uriBuilder.build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
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

    public static Uri buildYouTubeThumbURI(String key) {
        return new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(YOUTUBE_IMAGE_BASE_PATH)
                .appendEncodedPath(YOUTUBE_VI)
                .appendEncodedPath(key)
                .appendEncodedPath(IMG_0_JPG)
                .build();
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

    public static URL buildTMDBMovieURL(String movieId) {
        Uri uri = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(TMDB_BASE_PATH)
                .appendEncodedPath(MOVIE_PATH)
                .appendEncodedPath(movieId)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static URL buildTMDBTVShowURL(String tvShowId) {
        Uri uri = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(TMDB_BASE_PATH)
                .appendEncodedPath(TV_SHOW_PATH)
                .appendEncodedPath(tvShowId)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
        if(imgFile == null) return null;
        return new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(IMAGE_BASE_PATH)
                .appendEncodedPath(imageSize)
                .appendEncodedPath(imgFile)
                .build();
    }

    /**
     * Builds TMDB Api URL
     *
     * @return URL
     */
    private static URL buildTMDBURL(String path, int page) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(SCHEME)
                .appendEncodedPath(API_BASE_PATH)
                .appendEncodedPath(API_VERSION)
                .appendEncodedPath(path)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, EN_US);

        if (page != -1) {
            uriBuilder.appendQueryParameter(PAGE_PARAM, String.valueOf(page));
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