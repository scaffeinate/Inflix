package dev.learn.movies.app.popular_movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry;

import static dev.learn.movies.app.popular_movies.data.DataContract.DATABASE_NAME;
import static dev.learn.movies.app.popular_movies.data.DataContract.DATABASE_VERSION;

/**
 * DbHelper class
 */
public class DbHelper extends SQLiteOpenHelper {

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
            MediaEntry.COLUMN_HOMEPAGE + " VARCHAR, " +
            MediaEntry.COLUMN_SEASONS_JSON + " TEXT " +
            " ) ";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEDIA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Check version and add conditional statement
        // Reference: https://thebhwgroup.com/blog/how-android-sqlite-onupgrade
        //TODO: Schema changes
    }
}
