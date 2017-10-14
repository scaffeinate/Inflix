package dev.learn.movies.app.popular_movies.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * NetworkTask
 */

public class NetworkTask extends AsyncTask<URL, Void, String> {

    private static final String TAG = "NetworkTask";

    private final NetworkTaskCallback mCallback;

    public NetworkTask(NetworkTaskCallback callback) {
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
