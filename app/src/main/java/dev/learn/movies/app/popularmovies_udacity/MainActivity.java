package dev.learn.movies.app.popularmovies_udacity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URL;

import dev.learn.movies.app.popularmovies_udacity.common.MoviesResult;
import dev.learn.movies.app.popularmovies_udacity.network.HTTPHelper;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerViewMovies;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageDisplay;

    private RecyclerView.LayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewMovies.setHasFixedSize(true);
        mRecyclerViewMovies.setLayoutManager(mLayoutManager);

        mAdapter = new MoviesAdapter(this);
        mRecyclerViewMovies.setAdapter(mAdapter);

        URL discoverURL = HTTPHelper.buildDiscoverURL();
        new DiscoverMoviesTask().execute(discoverURL);
    }

    // TODO (2) Figure out the the best way to show the sort option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    // TODO (3) Handle sort option clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_popular:
                return true;
            case R.id.action_sort_rating:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showRecyclerView() {
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
    }

    private class DiscoverMoviesTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
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
            if (s != null) {
                MoviesResult moviesResult = new GsonBuilder().create().fromJson(s, MoviesResult.class);
                if (moviesResult != null && moviesResult.getResults() != null) {
                    mAdapter.setMovieList(moviesResult.getResults());
                }
                showRecyclerView();
            } else {
                showErrorMessage();
            }
        }
    }
}
