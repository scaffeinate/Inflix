package dev.learn.movies.app.popularmovies_udacity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import dev.learn.movies.app.popularmovies_udacity.network.HTTPHelper;

/**
 * Created by sudhar on 10/11/17.
 */

public class DiscoverMoviesTask extends AsyncTask<URL, Void, String> {

    private static final String TAG = "DiscoverMoviesTask";

    private NetworkTaskCallback mCallback;

    public DiscoverMoviesTask(NetworkTaskCallback callback) {
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallback.onPreExecute();
    }

    @Override
    protected String doInBackground(URL... urls) {
        String response = null;
        try {
            response = HTTPHelper.getHTTPResponse(urls[0]);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mCallback.onPostExecute(s);
    }
}
