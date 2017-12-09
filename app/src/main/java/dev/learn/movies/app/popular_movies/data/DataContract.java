package dev.learn.movies.app.popular_movies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * DataContract
 */
public class DataContract {

    public static final String DATABASE_NAME = "moviesDb";

    public static final int DATABASE_VERSION = 1;

    public static final String FAVORITES_AUTHORITY = "dev.learn.movies.app.popular_movies.favorites";

    public static final String BOOKMARKS_AUTHORITY = "dev.learn.movies.app.popular_movies.bookmarks";

    public static final String MEDIA_AUTHORITY = "dev.learn.movies.app.popular_movies.media";

    public static final String MOVIES = "movies";

    public static final String TV_SHOWS = "tv_shows";

    public static final String MEDIA_PATH = "media";

    public static final String FAVORITES_PATH = "favorites";

    public static final String BOOKMARKS_PATH = "bookmarks";

    public static final Uri FAVORITES_CONTENT_URI = Uri.parse("content://" + FAVORITES_AUTHORITY)
            .buildUpon()
            .appendPath(FAVORITES_PATH)
            .build();

    public static final Uri BOOKMARKS_CONTENT_URI = Uri.parse("content://" + BOOKMARKS_AUTHORITY)
            .buildUpon()
            .appendPath(BOOKMARKS_PATH)
            .build();

    public static final Uri MEDIA_CONTENT_URI = Uri.parse("content://" + MEDIA_AUTHORITY)
            .buildUpon()
            .appendPath(MEDIA_PATH)
            .build();

    public static class MediaEntry implements BaseColumns {
        public static final String TABLE_NAME = "media";
        public static final String COLUMN_MEDIA_ID = "media_id";
        public static final String COLUMN_MEDIA_TYPE = "media_type";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_IS_FAVORED = "is_favored";
        public static final String COLUMN_IS_BOOKMARKED = "is_bookmarked";

        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RUNTIME = "runtime";

        public static final String COLUMN_NUM_EPISODES = "num_episodes";
        public static final String COLUMN_NUM_SEASONS = "num_seasons";
        public static final String COLUMN_FIRST_AIR_DATE = "first_air_date";
        public static final String COLUMN_LAST_AIR_DATE = "last_air_date";
        public static final String COLUMN_EPISODE_RUN_TIME = "episode_run_time";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_HOMEPAGE = "homepage";
    }

    public static class CastEntry implements BaseColumns {
        public static final String TABLE_NAME = "casts";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_ORDER = "cast_order";
        public static final String COLUMN_CHARACTER = "character";
        public static final String COLUMN_PROFILE_PATH = "profile_path";
        public static final String COLUMN_MEDIA_ID = "media_id";
    }

    public static class SeasonEntry implements BaseColumns {
        public static final String TABLE_NAME = "seasons";
        public static final String COLUMN_SEASON_NUMBER = "season_number";
        public static final String COLUMN_EPISODE_COUNT = "episode_count";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_AIR_DATE = "air_date";
        public static final String COLUMN_MEDIA_ID = "media_id";
    }
}
