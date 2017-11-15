package dev.learn.movies.app.popular_movies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by sudhar on 11/14/17.
 */

public abstract class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEWTYPE_LOADING = 1;
    public static final int VIEWTYPE_CONTENT = 2;

    private boolean mShowLoading = true;

    @Override
    abstract public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoaderViewHolder) {
            ((LoaderViewHolder) holder).bind(mShowLoading);
        } else if (holder instanceof MoviesAdapter.ContentViewHolder) {
            ((ContentViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEWTYPE_LOADING;
        }

        return VIEWTYPE_CONTENT;
    }

    abstract public int getItemCount();

    /**
     * Toggles progressBar footer view
     *
     * @param showLoading showLoading
     */
    public void showLoading(boolean showLoading) {
        this.mShowLoading = showLoading;
    }

    abstract class ContentViewHolder extends RecyclerView.ViewHolder {
        public ContentViewHolder(View itemView) {
            super(itemView);
        }

        abstract public void bind(int position);
    }
}
