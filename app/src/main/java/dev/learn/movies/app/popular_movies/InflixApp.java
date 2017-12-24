package dev.learn.movies.app.popular_movies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * InflixApp - Application
 */

public class InflixApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}
