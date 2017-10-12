package dev.learn.movies.app.popularmovies_udacity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.net.URL;
import java.util.List;

import dev.learn.movies.app.popularmovies_udacity.common.Movie;
import dev.learn.movies.app.popularmovies_udacity.common.MoviesResult;
import dev.learn.movies.app.popularmovies_udacity.network.HTTPHelper;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnItemClickHandler, NetworkTaskCallback {

    private final static String TAG = "MainActivity";

    private final static String TYPE = "type";
    private final static String DISCOVER = "discover";
    private final static String MOST_POPULAR = "most_popular";
    private final static String TOP_RATED = "top_rated";

    private String type = DISCOVER;

    private Toolbar mToolbar;

    private RecyclerView mRecyclerViewMovies;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageDisplay;

    private RecyclerView.LayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;

    private List<Movie> movieList;
    private final Gson gson = new Gson();

    //TODO (1): Save the grid state in OnSavedInstance and restore
    //TODO (2): OnClick Start DetailActivity
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

        mAdapter = new MoviesAdapter(this, this);
        mRecyclerViewMovies.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            type = savedInstanceState.getString(TYPE);
        }

        fetchMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_popular:
                type = MOST_POPULAR;
                fetchMovies();
                return true;
            case R.id.action_sort_rating:
                type = TOP_RATED;
                fetchMovies();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int position) {
        Intent detailActivityIntent = new Intent(this, DetailActivity.class);

        if (movieList != null && position < movieList.size()) {
            Movie movie = movieList.get(position);
            detailActivityIntent.putExtra(DetailActivity.MOVIE_ID, movie.getId());
        }

        startActivity(detailActivityIntent);
    }

    @Override
    public void onPreExecute() {
        showProgressBar();
    }

    @Override
    public void onPostExecute(String s) {
        MoviesResult moviesResult = null;
        if (s != null) {
            moviesResult = gson.fromJson(s, MoviesResult.class);
        }

        if (moviesResult == null || moviesResult.getResults() == null || moviesResult.getResults().isEmpty()) {
            showErrorMessage();
        } else {
            this.movieList = moviesResult.getResults();
            mAdapter.setMovieList(movieList);
            showRecyclerView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(TYPE, this.type);
    }

    private void fetchMovies() {
        URL url;
        switch (type) {
            case DISCOVER:
                url = HTTPHelper.buildDiscoverURL();
                break;
            case MOST_POPULAR:
                url = HTTPHelper.buildMostPopularURL();
                break;
            case TOP_RATED:
                url = HTTPHelper.builTopRatedURL();
                break;
            default:
                url = HTTPHelper.buildDiscoverURL();
        }

        new DiscoverMoviesTask(this).execute(url);
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
}
