package dev.learn.movies.app.popular_movies.utils;

import android.content.Context;
import android.view.View;

/**
 * ContentLoadingUtil - Hide/Show content with Loading bar and Error Message support
 */

public final class ContentLoadingUtil {

    private Context mContext;
    private boolean mHideOnError = false;
    private View mContent;
    private View mError;
    private View mProgress;
    private View mParent;

    private ContentLoadingUtil(Context context) {
        this.mContext = context;
    }

    public static ContentLoadingUtil with(Context context) {
        return new ContentLoadingUtil(context);
    }

    public ContentLoadingUtil setParent(View parent) {
        mParent = parent;
        return this;
    }

    public ContentLoadingUtil setContent(View content) {
        mContent = content;
        return this;
    }

    public ContentLoadingUtil setError(View error) {
        mError = error;
        return this;
    }

    public ContentLoadingUtil setProgress(View progress) {
        mProgress = progress;
        return this;
    }

    public ContentLoadingUtil hideParentOnError() {
        mHideOnError = true;
        return this;
    }

    public void success() {
        show(mContent);
        hide(mError);
        hide(mProgress);
    }

    public void error() {
        if (mHideOnError) {
            hide(mParent);
        } else {
            show(mError);
            hide(mContent);
            hide(mProgress);
        }
    }

    public void inProgress() {
        show(mProgress);
        hide(mContent);
        hide(mError);
    }

    private void show(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void hide(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }
}
