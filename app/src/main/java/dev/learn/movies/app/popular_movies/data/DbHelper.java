package dev.learn.movies.app.popular_movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry;

import static dev.learn.movies.app.popular_movies.data.DataContract.CastEntry;
import static dev.learn.movies.app.popular_movies.data.DataContract.DATABASE_NAME;
import static dev.learn.movies.app.popular_movies.data.DataContract.DATABASE_VERSION;
import static dev.learn.movies.app.popular_movies.data.DataContract.SeasonEntry;

/**
 * DbHelper class
 */
public class DbHelper extends SQLiteOpenHelper {

    // Create favorites table query
    /*private static final String CREATE_SAVED_TABLE = " CREATE TABLE " + SavedEntry.TABLE_NAME +
            " ( " +
            SavedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SavedEntry.COLUMN_SAVED_TYPE + " VARCHAR NOT NULL, " +
            SavedEntry.COLUMN_SAVED_ID + " VARCHAR NOT NULL, " +
            SavedEntry.COLUMN_ACTION + " VARCHAR NOT NULL " +
            " ) ";*/

    private static final String CREATE_MEDIA_TABLE = " CREATE TABLE " + MediaEntry.TABLE_NAME +
            " ( " +
            MediaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MediaEntry.COLUMN_TITLE + " VARCHAR NOT NULL, " +
            MediaEntry.COLUMN_MEDIA_ID + " VARCHAR NOT NULL, " +
            MediaEntry.COLUMN_MEDIA_TYPE + " VARCHAR NOT NULL, " +
            MediaEntry.COLUMN_OVERVIEW + " VARCHAR, " +
            MediaEntry.COLUMN_POSTER_PATH + " VARCHAR, " +
            MediaEntry.COLUMN_BACKDROP_PATH + " VARCHAR, " +
            MediaEntry.COLUMN_VOTE_AVG + " REAL NOT NULL DEFAULT 0, " +
            MediaEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL DEFAULT 0, " +
            MediaEntry.COLUMN_GENRES + " VARCHAR, " +
            MediaEntry.COLUMN_STATUS + " VARCHAR, " +
            MediaEntry.COLUMN_IS_FAVORED + " BOOLEAN DEFAULT FALSE, " +
            MediaEntry.COLUMN_IS_BOOKMARKED + " BOOLEAN DEFAULT FALSE, " +
            MediaEntry.COLUMN_TAGLINE + " VARHCAR, " +
            MediaEntry.COLUMN_RELEASE_DATE + " VARCHAR, " +
            MediaEntry.COLUMN_RUNTIME + " INTEGER NOT NULL DEFAULT 0, " +
            MediaEntry.COLUMN_NUM_EPISODES + " INTEGER NOT NULL DEFAULT 0, " +
            MediaEntry.COLUMN_NUM_SEASONS + " INTEGER NOT NULL DEFAULT 0, " +
            MediaEntry.COLUMN_FIRST_AIR_DATE + " VARCHAR, " +
            MediaEntry.COLUMN_LAST_AIR_DATE + " VARCHAR, " +
            MediaEntry.COLUMN_EPISODE_RUN_TIME + " INTEGER NOT NULL DEFAULT 0, " +
            MediaEntry.COLUMN_CREATED_BY + " VARCHAR, " +
            MediaEntry.COLUMN_HOMEPAGE + " VARCHAR " +
            " ) ";

    private static final String CREATE_CASTS_TABLE = " CREATE TABLE " + CastEntry.TABLE_NAME +
            " ( " +
            CastEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CastEntry.COLUMN_MEDIA_ID + " VARCHAR NOT NULL, " +
            CastEntry.COLUMN_NAME + " VARCHAR NOT NULL, " +
            CastEntry.COLUMN_CHARACTER + " VARCHAR NOT NULL, " +
            CastEntry.COLUMN_GENDER + " INTEGER, " +
            CastEntry.COLUMN_ORDER + " INTEGER NOT NULL DEFAULT 0, " +
            CastEntry.COLUMN_PROFILE_PATH + " VARCHAR " +
            " ) ";

    private static final String CREATE_SEASONS_TABLE = " CREATE TABLE " + SeasonEntry.TABLE_NAME +
            " ( " +
            SeasonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SeasonEntry.COLUMN_MEDIA_ID + " VARCHAR NOT NULL, " +
            SeasonEntry.COLUMN_SEASON_NUMBER + " INTEGER NOT NULL, " +
            SeasonEntry.COLUMN_EPISODE_COUNT + " INTEGER NOT NULL DEFAULT 0, " +
            SeasonEntry.COLUMN_POSTER_PATH + " VARCHAR, " +
            SeasonEntry.COLUMN_AIR_DATE + " VARCHAR, " +
            " FOREIGN KEY ( " + SeasonEntry.COLUMN_MEDIA_ID + " ) REFERENCES " +
            MediaEntry.TABLE_NAME + " ( " + MediaEntry.COLUMN_MEDIA_ID + " ) " +
            " ) ";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEDIA_TABLE);
        db.execSQL(CREATE_CASTS_TABLE);
        db.execSQL(CREATE_SEASONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Check version and add conditional statement
        // Reference: https://thebhwgroup.com/blog/how-android-sqlite-onupgrade
        //TODO: Schema changes
    }
}
