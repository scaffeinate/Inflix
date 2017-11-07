package dev.learn.movies.app.popular_movies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import dev.learn.movies.app.popular_movies.databinding.ActivityMainBinding;
import dev.learn.movies.app.popular_movies.fragments.LocalMoviesFragment;
import dev.learn.movies.app.popular_movies.fragments.MoviesFragment;

import static dev.learn.movies.app.popular_movies.util.AppConstants.DISCOVER;
import static dev.learn.movies.app.popular_movies.util.AppConstants.FAVORITES;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOST_POPULAR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TOP_RATED;

/**
 * MainActivity - To show the movies grid
 */
public class MainActivity extends AppCompatActivity {

    private static final String TITLE = "title";

    private FragmentManager mFragmentManager;
    private ActivityMainBinding mBinding;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();

        setSupportActionBar(mBinding.toolbar.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState == null) {
            mTitle = getResources().getString(R.string.discover);
            mFragmentManager.beginTransaction()
                    .replace(R.id.layout_content, MoviesFragment.newInstance(DISCOVER))
                    .commit();
        } else {
            mTitle = savedInstanceState.getString(TITLE);
        }

        mBinding.toolbar.tvToolbarTitle.setText(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean selected = super.onOptionsItemSelected(item);
        Fragment fragment;

        switch (item.getItemId()) {
            case R.id.action_discover:
                fragment = MoviesFragment.newInstance(DISCOVER);
                mTitle = getResources().getString(R.string.discover);
                selected = true;
                break;
            case R.id.action_sort_popular:
                fragment = MoviesFragment.newInstance(MOST_POPULAR);
                mTitle = getResources().getString(R.string.most_popular);
                selected = true;
                break;
            case R.id.action_sort_rating:
                fragment = MoviesFragment.newInstance(TOP_RATED);
                mTitle = getResources().getString(R.string.top_rated);
                selected = true;
                break;
            case R.id.action_favorites:
                fragment = LocalMoviesFragment.newInstance(FAVORITES);
                mTitle = getResources().getString(R.string.favorites);
                selected = true;
                break;
            default:
                mTitle = getResources().getString(R.string.discover);
                fragment = MoviesFragment.newInstance(DISCOVER);
                break;
        }

        mBinding.toolbar.tvToolbarTitle.setText(mTitle);
        mFragmentManager.beginTransaction()
                .replace(R.id.layout_content, fragment)
                .commit();

        return selected;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE, mTitle);
    }
}
