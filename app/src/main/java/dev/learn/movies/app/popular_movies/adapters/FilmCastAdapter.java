package dev.learn.movies.app.popular_movies.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.cast.Cast;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

/**
 * Created by sudharti on 11/26/17.
 */

public class FilmCastAdapter extends RecyclerView.Adapter<FilmCastAdapter.FilmCastHolder> {

    private List<Cast> mFilmCastList;
    private final OnItemClickHandler mHandler;

    public FilmCastAdapter(OnItemClickHandler handler) {
        mFilmCastList = new ArrayList<>();
        mHandler = handler;
    }

    @Override
    public FilmCastHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new FilmCastHolder(layoutInflater.inflate(R.layout.item_film_cast, parent, false));
    }

    @Override
    public void onBindViewHolder(FilmCastHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mFilmCastList.size();
    }

    public void setFilmCastList(List<Cast> filmCastList) {
        this.mFilmCastList = filmCastList;
        notifyDataSetChanged();
    }

    class FilmCastHolder extends RecyclerView.ViewHolder {

        private final TextView mActorNameTextView;
        private final TextView mCharacterNameTextView;
        private final ImageView mProfilePicImageView;

        public FilmCastHolder(View itemView) {
            super(itemView);
            mActorNameTextView = itemView.findViewById(R.id.tv_actor_name);
            mCharacterNameTextView = itemView.findViewById(R.id.tv_character_name);
            mProfilePicImageView = itemView.findViewById(R.id.image_view_profile_pic);
        }

        public void bind(int position) {
            Cast cast = mFilmCastList.get(position);
            if (cast != null) {
                String profilePath = cast.getProfilePath();
                String characterName = cast.getCharacter();
                String actorName = cast.getName();

                if (profilePath != null) {
                    DisplayUtils.fitImageInto(mProfilePicImageView,
                            HTTPHelper.buildImageResourceUri(profilePath, HTTPHelper.IMAGE_SIZE_SMALL));
                }

                if (!TextUtils.isEmpty(actorName)) {
                    mActorNameTextView.setText(actorName);
                }

                if (!TextUtils.isEmpty(characterName)) {
                    mCharacterNameTextView.setText(characterName);
                }
            }
        }
    }
}
