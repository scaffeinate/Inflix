package dev.learn.movies.app.popular_movies;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by sudhar on 11/1/17.
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private static final int THRESHOLD = 1;

    private int mVisibleThreshold = THRESHOLD;

    private int mCurrentPage = 1;

    private int mStartingPage = 1;

    private int mCurrentNumberOfItems = 1;

    private boolean isLoading = true;

    RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollListener(RecyclerView.LayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;

        if (layoutManager instanceof GridLayoutManager) {
            mVisibleThreshold *= ((GridLayoutManager) layoutManager).getSpanCount();
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int lastVisiblePosition = 0;
        int itemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisiblePosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisiblePosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        if (itemCount < mCurrentNumberOfItems) {
            mCurrentPage = mStartingPage;
            mCurrentNumberOfItems = itemCount;
            if (itemCount == 0) {
                isLoading = true;
            }
        }

        if (isLoading && (itemCount > mCurrentNumberOfItems)) {
            isLoading = false;
            mCurrentNumberOfItems = itemCount;
        }

        if (!isLoading && ((lastVisiblePosition + mVisibleThreshold) > itemCount)) {
            mCurrentPage++;
            onLoadMore(mCurrentPage, itemCount, recyclerView);
            isLoading = true;
        }
    }

    public void reset() {
        mCurrentPage = mStartingPage;
        mCurrentNumberOfItems = 0;
        isLoading = true;
    }

    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
}
