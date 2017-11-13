package dev.learn.movies.app.popular_movies.activities;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.databinding.ActivityDetailBinding;
import dev.learn.movies.app.popular_movies.fragments.MovieDetailsFragment;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;

/**
 * DetailActivity - Movie Details Screen
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener, MovieDetailCallbacks {

    public static final String RESOURCE_ID = "resource_id";
    public static final String RESOURCE_TYPE = "resource_type";

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
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(RESOURCE_ID)) {
                long movieId = getIntent().getExtras().getLong(RESOURCE_ID);
                mFragment = MovieDetailsFragment.newInstance(movieId);
                mFragmentManager.beginTransaction()
                        .replace(R.id.layout_outlet, mFragment)
                        .commit();
            }
        }

        adjustBackdropSize();
        mBinding.btnFav.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
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

    /**
     * Based on the screen size and orientation scales the parent image layouts.
     */
    private void adjustBackdropSize() {
        int[] screen = DisplayUtils.getScreenMetrics(this);
        mBinding.appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (Math.max(screen[0], screen[1]) / 2.25)));
    }

    public interface OnFavBtnClickListener {
        void onFavBtnClicked(View v);
    }
}
