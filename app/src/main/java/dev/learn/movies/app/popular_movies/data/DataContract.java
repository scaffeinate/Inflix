package dev.learn.movies.app.popular_movies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sudharti on 11/4/17.
 */

public class DataContract {

    public static final String DATABASE_NAME = "moviesDb";

    public static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "dev.learn.movies.app.popular_movies";

    public static final String FAVORITES_PATH = "favorites";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(FAVORITES_PATH).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_GENRES = "genres";
    }
}
