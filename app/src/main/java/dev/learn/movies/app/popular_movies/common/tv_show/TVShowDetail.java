
package dev.learn.movies.app.popular_movies.common.tv_show;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.MediaDetail;

import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_BACKDROP_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_CREATED_BY;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_EPISODE_RUN_TIME;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_FIRST_AIR_DATE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_GENRES;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_HOMEPAGE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_IS_BOOKMARKED;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_IS_FAVORED;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_LAST_AIR_DATE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_MEDIA_ID;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_NUM_EPISODES;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_NUM_SEASONS;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_OVERVIEW;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_POSTER_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_STATUS;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_TITLE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_VOTE_AVG;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_VOTE_COUNT;

public class TVShowDetail extends MediaDetail implements Parcelable {

    @SerializedName("created_by")
    @Expose
    private List<CreatedBy> createdBy = null;
    @SerializedName("episode_run_time")
    @Expose
    private List<Long> episodeRunTime = null;
    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @SerializedName("in_production")
    @Expose
    private boolean inProduction;
    @SerializedName("last_air_date")
    @Expose
    private String lastAirDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("number_of_episodes")
    @Expose
    private long numberOfEpisodes;
    @SerializedName("number_of_seasons")
    @Expose
    private long numberOfSeasons;
    @SerializedName("popularity")
    @Expose
    private double popularity;
    @SerializedName("seasons")
    @Expose
    private List<Season> seasons = null;

    public TVShowDetail() {
    }

    protected TVShowDetail(Parcel in) {
        backdropPath = in.readString();
        firstAirDate = in.readString();
        homepage = in.readString();
        id = in.readLong();
        inProduction = in.readByte() != 0;
        lastAirDate = in.readString();
        name = in.readString();
        numberOfEpisodes = in.readLong();
        numberOfSeasons = in.readLong();
        overview = in.readString();
        popularity = in.readDouble();
        posterPath = in.readString();
        status = in.readString();
        voteAverage = in.readDouble();
        voteCount = in.readLong();
    }

    public static final Creator<TVShowDetail> CREATOR = new Creator<TVShowDetail>() {
        @Override
        public TVShowDetail createFromParcel(Parcel in) {
            return new TVShowDetail(in);
        }

        @Override
        public TVShowDetail[] newArray(int size) {
            return new TVShowDetail[size];
        }
    };

    public List<CreatedBy> getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(List<CreatedBy> createdBy) {
        this.createdBy = createdBy;
    }

    public List<Long> getEpisodeRunTime() {
        return episodeRunTime;
    }

    public void setEpisodeRunTime(List<Long> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public boolean isInProduction() {
        return inProduction;
    }

    public void setInProduction(boolean inProduction) {
        this.inProduction = inProduction;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(long numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public long getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(long numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(backdropPath);
        dest.writeString(firstAirDate);
        dest.writeString(homepage);
        dest.writeLong(id);
        dest.writeByte((byte) (inProduction ? 1 : 0));
        dest.writeString(lastAirDate);
        dest.writeString(name);
        dest.writeLong(numberOfEpisodes);
        dest.writeLong(numberOfSeasons);
        dest.writeString(overview);
        dest.writeDouble(popularity);
        dest.writeString(posterPath);
        dest.writeString(status);
        dest.writeDouble(voteAverage);
        dest.writeLong(voteCount);
    }

    public static ContentValues toContentValues(TVShowDetail tvShowDetail) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MEDIA_ID, tvShowDetail.getId());
        cv.put(COLUMN_OVERVIEW, tvShowDetail.getOverview());
        cv.put(COLUMN_POSTER_PATH, tvShowDetail.getPosterPath());
        cv.put(COLUMN_BACKDROP_PATH, tvShowDetail.getBackdropPath());
        cv.put(COLUMN_VOTE_AVG, tvShowDetail.getVoteAverage());
        cv.put(COLUMN_VOTE_COUNT, tvShowDetail.getVoteCount());
        StringBuilder builder = new StringBuilder();
        List<Genre> genreList = tvShowDetail.getGenres();
        if (genreList != null && !genreList.isEmpty()) {
            for (Genre genre : genreList) {
                builder.append(genre.getName()).append(",");
            }
        }
        cv.put(COLUMN_GENRES, builder.toString());
        cv.put(COLUMN_STATUS, tvShowDetail.getStatus());
        cv.put(COLUMN_IS_FAVORED, tvShowDetail.isFavored() ? 1 : 0);
        cv.put(COLUMN_IS_BOOKMARKED, tvShowDetail.isBookmarked() ? 1 : 0);
        cv.put(COLUMN_TITLE, tvShowDetail.getName());
        cv.put(COLUMN_NUM_EPISODES, tvShowDetail.getNumberOfEpisodes());
        cv.put(COLUMN_NUM_SEASONS, tvShowDetail.getNumberOfSeasons());
        cv.put(COLUMN_FIRST_AIR_DATE, tvShowDetail.getFirstAirDate());
        cv.put(COLUMN_LAST_AIR_DATE, tvShowDetail.getLastAirDate());
        if (tvShowDetail.getEpisodeRunTime() != null && !tvShowDetail.getEpisodeRunTime().isEmpty()) {
            cv.put(COLUMN_EPISODE_RUN_TIME, tvShowDetail.getEpisodeRunTime().get(0));
        }

        builder = new StringBuilder();
        List<CreatedBy> createdByList = tvShowDetail.getCreatedBy();
        if (createdByList != null && !createdByList.isEmpty()) {
            for (CreatedBy createdBy : createdByList) {
                builder.append(createdBy.getName()).append(",");
            }
        }
        cv.put(COLUMN_CREATED_BY, builder.toString());
        cv.put(COLUMN_HOMEPAGE, tvShowDetail.getHomepage());
        return cv;
    }

    public static TVShowDetail fromCursor(Cursor cursor) {
        TVShowDetail tvShowDetail = new TVShowDetail();
        if (cursor.moveToFirst()) {
            tvShowDetail.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_MEDIA_ID)));
            tvShowDetail.setOverview(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)));
            tvShowDetail.setPosterPath(cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)));
            tvShowDetail.setBackdropPath(cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)));
            tvShowDetail.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(COLUMN_VOTE_AVG)));
            tvShowDetail.setVoteCount(cursor.getLong(cursor.getColumnIndex(COLUMN_VOTE_COUNT)));
            String genresStr = cursor.getString(cursor.getColumnIndex(COLUMN_GENRES));
            List<Genre> genreList = new ArrayList<>();
            String[] genresArr = genresStr.split(",");
            for (String genre : genresArr) {
                genreList.add(new Genre(0, genre));
            }
            tvShowDetail.setGenres(genreList);
            tvShowDetail.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
            tvShowDetail.setFavored(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAVORED)) == 1);
            tvShowDetail.setBookmarked(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_BOOKMARKED)) == 1);
            tvShowDetail.setName(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            tvShowDetail.setNumberOfEpisodes(cursor.getLong(cursor.getColumnIndex(COLUMN_NUM_EPISODES)));
            tvShowDetail.setNumberOfSeasons(cursor.getLong(cursor.getColumnIndex(COLUMN_NUM_SEASONS)));
            tvShowDetail.setFirstAirDate(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_AIR_DATE)));
            tvShowDetail.setLastAirDate(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_AIR_DATE)));
            long episodeRuntime = cursor.getLong(cursor.getColumnIndex(COLUMN_EPISODE_RUN_TIME));
            tvShowDetail.setEpisodeRunTime(new ArrayList<>(Arrays.asList(episodeRuntime)));
            String createdByStr = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_BY));
            String[] createdByArr = createdByStr.split(",");
            List<CreatedBy> createdByList = new ArrayList<>();
            for (String createdBy : createdByArr) {
                createdByList.add(new CreatedBy(createdBy));
            }
            tvShowDetail.setCreatedBy(createdByList);
            tvShowDetail.setHomepage(cursor.getString(cursor.getColumnIndex(COLUMN_HOMEPAGE)));
        }
        return tvShowDetail;
    }
}
