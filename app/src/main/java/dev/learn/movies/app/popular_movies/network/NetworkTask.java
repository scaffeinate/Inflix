package dev.learn.movies.app.popular_movies.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * NetworkTask - Generic AsyncTask to fetch results from URL. Returns the response string
 * <p>
 * AsyncTask as an inner class has an implicit reference to the outer Activity which causes memory leaks.
 * So declaring it as a standalone class and use callbacks to update UI.</p>
 */

public class NetworkTask extends AsyncTask<URL, Void, String> {

    private static final String TAG = NetworkTask.class.getSimpleName();

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
