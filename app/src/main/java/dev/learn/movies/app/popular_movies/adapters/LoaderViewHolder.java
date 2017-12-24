package dev.learn.movies.app.popular_movies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * ViewHolder class to show loading progress footer
 * <p>
 * Reference: http://www.jayrambhia.com/blog/footer-loader
 */
public class LoaderViewHolder extends RecyclerView.ViewHolder {
    public LoaderViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(boolean showLoading) {
        if (showLoading) {
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setMinimumHeight(0);
            itemView.setVisibility(View.GONE);
        }
    }
}
