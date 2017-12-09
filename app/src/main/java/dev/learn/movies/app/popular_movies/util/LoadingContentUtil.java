package dev.learn.movies.app.popular_movies.util;

import android.content.Context;
import android.view.View;

/**
 * Created by sudhar on 12/9/17.
 */

public final class LoadingContentUtil {

    private Context mContext;
    private boolean mShowToast = false;
    private boolean mHideOnError = false;
    private View mContent;
    private View mError;
    private View mProgress;
    private View mParent;

    private LoadingContentUtil(Context context) {
        this.mContext = context;
    }

    public static LoadingContentUtil with(Context context) {
        LoadingContentUtil loadingContentUtil = new LoadingContentUtil(context);
        return loadingContentUtil;
    }

    public LoadingContentUtil setParent(View parent) {
        mParent = parent;
        return this;
    }

    public LoadingContentUtil setContent(View content) {
        mContent = content;
        return this;
    }

    public LoadingContentUtil setError(View error) {
        mError = error;
        return this;
    }

    public LoadingContentUtil setProgress(View progress) {
        mProgress = progress;
        return this;
    }

    public LoadingContentUtil showToastOnError() {
        mShowToast = true;
        return this;
    }

    public LoadingContentUtil hideParentOnError() {
        mHideOnError = true;
        return this;
    }

    public void success() {
        show(mContent);
        hide(mError);
        hide(mProgress);
    }

    public void error() {
        if(mHideOnError) {
            hide(mParent);
        } else {
            show(mError);
            hide(mContent);
            hide(mProgress);
        }
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
