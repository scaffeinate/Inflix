package dev.learn.movies.app.popular_movies.activities;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.databinding.ActivityDetailBinding;
import dev.learn.movies.app.popular_movies.fragments.MovieDetailsFragment;
import dev.learn.movies.app.popular_movies.fragments.TVShowDetailsFragment;
import dev.learn.movies.app.popular_movies.utils.DisplayUtils;

import static dev.learn.movies.app.popular_movies.Inflix.MOVIES;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TYPE;
import static dev.learn.movies.app.popular_movies.Inflix.TV_SHOWS;

/**
 * DetailActivity - Movie Details Screen
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener, DetailActivityCallbacks {

    private ActivityDetailBinding mBinding;
    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private Toast mFavToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mFragmentManager = getSupportFragmentManager();

        setSupportActionBar(mBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState == null) {
            String resourceType = getIntent().getStringExtra(RESOURCE_TYPE);
            long resourceId = getIntent().getLongExtra(RESOURCE_ID, 0);
            String resourceTitle = getIntent().getStringExtra(RESOURCE_TITLE);

            if (resourceType != null && resourceId != 0 && resourceTitle != null) {
                switch (resourceType) {
                    case MOVIES:
                        mFragment = MovieDetailsFragment.newInstance(resourceId, resourceTitle);
                        break;
                    case TV_SHOWS:
                        mFragment = TVShowDetailsFragment.newInstance(resourceId, resourceTitle);
                        break;
                }

                if (mFragment != null) {
                    mFragmentManager.beginTransaction()
                            .replace(R.id.layout_outlet, mFragment)
                            .commit();
                }
            }
        }

        mBinding.btnFav.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        mFragment = mFragmentManager.findFragmentById(R.id.layout_outlet);
        OnFavBtnClickListener listener = (OnFavBtnClickListener) mFragment;
        if (listener != null) {
            listener.onFavBtnClicked(v);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFavToast != null) {
            mFavToast.cancel();
        }
    }

    @Override
    public void showFavToast(boolean favored) {
        String toastMessage;
        if (favored) {
            toastMessage = getResources().getString(R.string.added_to_favorites);
        } else {
            toastMessage = getResources().getString(R.string.removed_from_favorites);
        }

        if (mFavToast != null) {
            mFavToast.cancel();
        }
        mFavToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
        mFavToast.show();
    }

    @Override
    public void updateBackdrop(Uri imageUri) {
        DisplayUtils.fitImageInto(mBinding.imageViewBackdrop, imageUri);
    }

    @Override
    public void updateFavBtn(boolean favored) {
        if (favored) {
            mBinding.btnFav.setImageResource(R.drawable.ic_heart_white_24dp);
        } else {
            mBinding.btnFav.setImageResource(R.drawable.ic_heart_outline_white_24dp);
        }
    }

    @Override
    public void showFavBtn() {
        mBinding.btnFav.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFavBtn() {
        mBinding.btnFav.setVisibility(View.INVISIBLE);
    }

    @Override
    public void scrollToTop() {
        mBinding.layoutOutlet.scrollTo(0, 0);
        mBinding.appBarLayout.setExpanded(true);
    }

    public interface OnFavBtnClickListener {
        void onFavBtnClicked(View v);
    }
}
