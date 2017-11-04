package dev.learn.movies.app.popular_movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static dev.learn.movies.app.popular_movies.data.DataContract.DATABASE_NAME;
import static dev.learn.movies.app.popular_movies.data.DataContract.DATABASE_VERSION;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry;

/**
 * Created by sudharti on 11/4/17.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String CREATE_FAVORITES_TABLE = " CREATE TABLE " + FavoriteEntry.TABLE_NAME +
            " ( " +
            FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FavoriteEntry.COLUMN_MOVIE_ID + " VARCHAR NOT NULL, " +
            FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
            FavoriteEntry.COLUMN_TAGLINE + " TEXT, " +
            FavoriteEntry.COLUMN_OVERVIEW + " TEXT, " +
            FavoriteEntry.COLUMN_POSTER_PATH + " TEXT, " +
            FavoriteEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
            FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT, " +
            FavoriteEntry.COLUMN_RUNTIME + " INTEGER, " +
            FavoriteEntry.COLUMN_VOTE_AVG + " REAL, " +
            FavoriteEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
            FavoriteEntry.COLUMN_GENRES + " TEXT " +
            " ) ";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
