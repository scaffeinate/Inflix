package dev.learn.movies.app.popular_movies.adapters;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_POSTER_PATH;

/**
 * FavoritesAdapter - RecyclerView Adapter for Movies
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private final OnItemClickHandler mHandler;
    private Cursor mCursor;

    public FavoritesAdapter(OnItemClickHandler onItemClickHandler) {
        mHandler = onItemClickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poster, parent, false);
        int height = parent.getMeasuredHeight() / 2;
        ViewHolder viewHolder = new ViewHolder(parent, view);
        viewHolder.mPosterImageView.setMinimumHeight(height);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    /**
     * Swaps the current mCursor with cursor provided and calls notifyDataSetChanged()
     *
     * @param cursor Cursor
     */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class to show item content
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ViewGroup mParent;
        private final ImageView mPosterImageView;

        public ViewHolder(ViewGroup parent, View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.image_view_poster);
            mPosterImageView.setOnClickListener(this);
            mParent = parent;
        }

        /**
         * Populates the layout with movie details
         *
         * @param position Adapter position to populate
         */
        private void bind(int position) {
            mCursor.moveToPosition(position);
            String posterURL = mCursor.getString(mCursor.getColumnIndex(COLUMN_POSTER_PATH));
            if (posterURL != null) {
                Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_MEDIUM);
                DisplayUtils.fitImageInto(mPosterImageView, posterUri);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mHandler.onItemClicked(mParent, view, position);
        }
    }
}
