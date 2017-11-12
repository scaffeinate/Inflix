package dev.learn.movies.app.popular_movies.activities;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.databinding.ActivityMainBinding;
import dev.learn.movies.app.popular_movies.fragments.LocalMoviesFragment;
import dev.learn.movies.app.popular_movies.fragments.MoviesFragment;
import dev.learn.movies.app.popular_movies.fragments.TvShowsFragment;

import static dev.learn.movies.app.popular_movies.util.AppConstants.FAVORITES;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOST_POPULAR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.NOW_PLAYING;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TOP_RATED;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_AIRING_TODAY;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_ON_THE_AIR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_POPULAR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_TOP_RATED;
import static dev.learn.movies.app.popular_movies.util.AppConstants.UPCOMING;

/**
 * MainActivity - Movies Grid
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SELECTED_TITLE = "selected_title";
    private static final String SELECTED_NAV_ITEM = "selected_nav_item";

    private FragmentManager mFragmentManager;
    private ActivityMainBinding mBinding;
    private int selectedNavItem = 0;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();

        setSupportActionBar(mBinding.toolbar.toolbar);
        mBinding.navigationView.setNavigationItemSelectedListener(this);

        setupToolbar();
        if (savedInstanceState == null) {
            Menu menu = mBinding.navigationView.getMenu();
            onNavigationItemSelected(menu.findItem(R.id.action_now_playing));
        } else {
            setToolbarTitle(savedInstanceState.getString(SELECTED_TITLE));
            selectedNavItem = savedInstanceState.getInt(SELECTED_NAV_ITEM);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_TITLE, mBinding.toolbar.tvToolbarTitle.getText().toString());
        outState.putInt(SELECTED_NAV_ITEM, selectedNavItem);
    }

    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mBinding.drawerLayout, mBinding.toolbar.toolbar, R.string.open, R.string.close);
        mBinding.drawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectDrawerItem(item);
        return true;
    }

    private void selectDrawerItem(MenuItem item) {
        if (item.getItemId() != selectedNavItem) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.action_now_playing:
                    fragment = MoviesFragment.newInstance(NOW_PLAYING);
                    break;
                case R.id.action_upcoming:
                    fragment = MoviesFragment.newInstance(UPCOMING);
                    break;
                case R.id.action_popular:
                    fragment = MoviesFragment.newInstance(MOST_POPULAR);
                    break;
                case R.id.action_top_rated:
                    fragment = MoviesFragment.newInstance(TOP_RATED);
                    break;
                case R.id.action_tv_airing_today:
                    fragment = TvShowsFragment.newInstance(TV_AIRING_TODAY);
                    break;
                case R.id.action_tv_on_the_air:
                    fragment = TvShowsFragment.newInstance(TV_ON_THE_AIR);
                    break;
                case R.id.action_tv_popular:
                    fragment = TvShowsFragment.newInstance(TV_POPULAR);
                    break;
                case R.id.action_tv_top_rated:
                    fragment = TvShowsFragment.newInstance(TV_TOP_RATED);
                    break;
                case R.id.action_favorites:
                    fragment = LocalMoviesFragment.newInstance(FAVORITES);
                    break;
            }

            if (fragment != null) {
                mFragmentManager.beginTransaction()
                        .replace(R.id.layout_content, fragment)
                        .commit();
            }

            mBinding.navigationView.setCheckedItem(item.getItemId());
            selectedNavItem = item.getItemId();
            setToolbarTitle(item.getTitle().toString());
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void setToolbarTitle(String title) {
        mBinding.toolbar.tvToolbarTitle.setText(title);
    }
}