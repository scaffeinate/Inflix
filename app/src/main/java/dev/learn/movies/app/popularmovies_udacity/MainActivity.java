package dev.learn.movies.app.popularmovies_udacity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

import dev.learn.movies.app.popularmovies_udacity.common.Movie;
import dev.learn.movies.app.popularmovies_udacity.common.MoviesResult;
import dev.learn.movies.app.popularmovies_udacity.network.HTTPHelper;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnItemClickHandler, NetworkTaskCallback {

    private final static String TAG = "MainActivity";

    private final static String REQUEST_FOR = "request_for";
    private final static String DISCOVER_MOVIES = "Discover";
    private final static String MOST_POPULAR_MOVIES = "Most Popular";
    private final static String TOP_RATED_MOVIES = "Top Rated";

    private String requestFor = null;

    private Toolbar mToolbar;

    private RecyclerView mRecyclerViewMovies;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageDisplay;
    private TextView mToolbarTitle;

    private RecyclerView.LayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;

    private List<Movie> movieList;
    private final Gson gson = new Gson();

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
        mToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewMovies.setHasFixedSize(true);
        mRecyclerViewMovies.setLayoutManager(mLayoutManager);

        mAdapter = new MoviesAdapter(this, this);
        mRecyclerViewMovies.setAdapter(mAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(REQUEST_FOR)) {
            requestFor = savedInstanceState.getString(REQUEST_FOR);
        } else {
            requestFor = DISCOVER_MOVIES;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            case R.id.action_discover:
                requestFor = DISCOVER_MOVIES;
                fetchMovies();
                return true;
            case R.id.action_sort_popular:
                requestFor = MOST_POPULAR_MOVIES;
                fetchMovies();
                return true;
            case R.id.action_sort_rating:
                requestFor = TOP_RATED_MOVIES;
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
            Bundle bundle = new Bundle();
            bundle.putLong(DetailActivity.MOVIE_ID, movie.getId());
            bundle.putString(DetailActivity.MOVIE_NAME, movie.getTitle());
            detailActivityIntent.putExtras(bundle);
        }

        startActivity(detailActivityIntent);
    }

    @Override
    public void onPreExecute() {
        showProgressBar();
    }

    @Override
    public void onPostExecute(String s) {
        MoviesResult moviesResult = (s == null) ? null : gson.fromJson(s, MoviesResult.class);

        if (moviesResult == null || moviesResult.getResults() == null || moviesResult.getResults().isEmpty()) {
            showErrorMessage();
        } else {
            this.movieList = moviesResult.getResults();
            mAdapter.setMovieList(movieList);
            showRecyclerView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(REQUEST_FOR, requestFor);
    }

    private void fetchMovies() {
        if (requestFor == null) return;

        mToolbarTitle.setText(requestFor);
        switch (requestFor) {
            case DISCOVER_MOVIES:
                new DiscoverMoviesTask(this).execute(HTTPHelper.buildDiscoverURL());
                break;
            case MOST_POPULAR_MOVIES:
                new DiscoverMoviesTask(this).execute(HTTPHelper.buildMostPopularURL());
                break;
            case TOP_RATED_MOVIES:
                new DiscoverMoviesTask(this).execute(HTTPHelper.builTopRatedURL());
                break;
        }
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
