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
import android.widget.TextView;

import dev.learn.movies.app.popular_movies.databinding.ActivityMainBinding;
import dev.learn.movies.app.popular_movies.fragments.LocalMoviesFragment;
import dev.learn.movies.app.popular_movies.fragments.MoviesFragment;

/**
 * MainActivity - To show the movies grid
 */
public class MainActivity extends AppCompatActivity {


    public final static String DISCOVER = "discover";
    public final static String MOST_POPULAR = "most_popular";
    public final static String TOP_RATED = "top_rated";
    public final static String FAVORITES = "favorites";

    private FragmentManager mFragmentManager;
    private ActivityMainBinding mBinding;

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

        mFragmentManager.beginTransaction()
                .replace(R.id.layout_content, MoviesFragment.newInstance(DISCOVER))
                .commit();
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
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.action_discover:
                fragment = MoviesFragment.newInstance(DISCOVER);
                mBinding.toolbar.tvToolbarTitle.setText(getResources().getString(R.string.discover));
                selected = true;
                break;
            case R.id.action_sort_popular:
                fragment = MoviesFragment.newInstance(MOST_POPULAR);
                mBinding.toolbar.tvToolbarTitle.setText(getResources().getString(R.string.most_popular));
                selected = true;
                break;
            case R.id.action_sort_rating:
                fragment = MoviesFragment.newInstance(TOP_RATED);
                mBinding.toolbar.tvToolbarTitle.setText(getResources().getString(R.string.top_rated));
                selected = true;
                break;
            case R.id.action_favorites:
                fragment = LocalMoviesFragment.newInstance(FAVORITES);
                mBinding.toolbar.tvToolbarTitle.setText(getResources().getString(R.string.favorites));
                selected = true;
                break;
        }

        mFragmentManager.beginTransaction()
                .replace(R.id.layout_content, fragment)
                .commit();

        return selected;
    }
}
