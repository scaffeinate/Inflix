package dev.learn.movies.app.popular_movies.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.databinding.ActivityAdditionalInfoBinding;
import dev.learn.movies.app.popular_movies.fragments.UserReviewsFragment;

import static dev.learn.movies.app.popular_movies.util.AppConstants.ADDITIONAL_INFO_ACTIVITY_FRAGMENT_TYPE_REVIEWS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TYPE;

/**
 * Created by sudharti on 11/14/17.
 */

public class AdditionalInfoActivity extends AppCompatActivity {

    private String mTitle;

    private ActivityAdditionalInfoBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_additional_info);

        setSupportActionBar(mBinding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                long resourceId = extras.getLong(RESOURCE_ID, 0);
                String type = extras.getString(RESOURCE_TYPE, "");
                setContent(resourceId, type);
                mTitle = extras.getString(RESOURCE_TITLE);
            }
        } else {
            mTitle = savedInstanceState.getString(RESOURCE_TITLE);
        }

        setToolbarTitle();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RESOURCE_TITLE, mTitle);
    }

    private void setContent(long resourceId, String type) {
        if (resourceId == 0 || type.isEmpty()) return;
        Fragment fragment = null;
        switch (type) {
            case ADDITIONAL_INFO_ACTIVITY_FRAGMENT_TYPE_REVIEWS:
                fragment = UserReviewsFragment.newInstance(resourceId);
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_additional_info_outlet, fragment)
                    .commit();
        }
    }

    private void setToolbarTitle() {
        if (!TextUtils.isEmpty(mTitle)) {
            mBinding.toolbar.tvToolbarTitle.setText(mTitle);
        }
    }
}
