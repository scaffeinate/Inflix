package dev.learn.movies.app.popular_movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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

import dev.learn.movies.app.popular_movies.common.Movie;
import dev.learn.movies.app.popular_movies.common.MoviesResult;
import dev.learn.movies.app.popular_movies.network.HTTPHelper;
import dev.learn.movies.app.popular_movies.network.NetworkTask;
import dev.learn.movies.app.popular_movies.network.NetworkTaskCallback;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;

/**
 * MainActivity - To show the movies grid
 */
public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnItemClickHandler, NetworkTaskCallback {

    private final static String REQUEST_FOR = "request_for";
    private final static String DISCOVER_MOVIES = "Discover";
    private final static String MOST_POPULAR_MOVIES = "Most Popular";
    private final static String TOP_RATED_MOVIES = "Top Rated";
    private final Gson gson = new Gson();
    private String requestFor = null;
    private RecyclerView mRecyclerViewMovies;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageDisplay;
    private TextView mToolbarTitle;
    private MoviesAdapter mAdapter;
    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.recycler_view_movies);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewMovies.setHasFixedSize(true);
        mRecyclerViewMovies.setLayoutManager(mLayoutManager);

        mAdapter = new MoviesAdapter(this);
        mRecyclerViewMovies.setAdapter(mAdapter);

        /*
         * If savedInstanceState is not null then restore the grid type, i.e. Popular, Top Rated etc.
         * Else Default back to discover movies
         */
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

    /**
     * Overrides onClick(position) from MoviesAdapter.OnItemClickHandler
     *
     * @param position OnClick position
     */
    @Override
    public void onClick(int position) {
        if (movieList != null && position < movieList.size()) {
            /*
             * Starts DetailActivity with movieId and movieName passed in a bundle.
             */
            Intent detailActivityIntent = new Intent(this, DetailActivity.class);

            Movie movie = movieList.get(position);
            Bundle bundle = new Bundle();
            bundle.putLong(DetailActivity.MOVIE_ID, movie.getId());
            bundle.putString(DetailActivity.MOVIE_NAME, movie.getTitle());
            detailActivityIntent.putExtras(bundle);

            startActivity(detailActivityIntent);
        }
    }

    /**
     * Overrides onPreExecute() from NetworkTaskCallback
     */
    @Override
    public void onPreExecute() {
        showProgressBar();
    }

    /**
     * Overrides onPostExecute() from NetworkTaskCallback
     *
     * @param s AsyncTask result String
     */
    @Override
    public void onPostExecute(String s) {
        MoviesResult moviesResult = (s == null) ? null : gson.fromJson(s, MoviesResult.class);

        /*
         * Shows recycler_view if moviesResult is not null and the movies list is non empty
         */
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

    /**
     * Fetches movies if there is a  network connection.
     * Otherwise shows an error message.
     */
    private void fetchMovies() {
        if (requestFor == null) return;

        if (HTTPHelper.isNetworkEnabled(this)) {
            mToolbarTitle.setText(requestFor);
            switch (requestFor) {
                case DISCOVER_MOVIES:
                    new NetworkTask(this).execute(HTTPHelper.buildDiscoverURL());
                    break;
                case MOST_POPULAR_MOVIES:
                    new NetworkTask(this).execute(HTTPHelper.buildMostPopularURL());
                    break;
                case TOP_RATED_MOVIES:
                    new NetworkTask(this).execute(HTTPHelper.builTopRatedURL());
                    break;
            }
        } else {
            DisplayUtils.setNoNetworkConnectionMessage(this, mErrorMessageDisplay);
            showErrorMessage();
        }
    }

    /**
     * Shows ProgressBar, Hides ErrorMessage and RecyclerView
     */
    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows RecyclerView, Hides ProgressBar and ErrorMessage
     */
    private void showRecyclerView() {
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and RecyclerView
     */
    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
    }
}
