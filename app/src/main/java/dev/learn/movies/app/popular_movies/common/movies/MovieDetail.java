package dev.learn.movies.app.popular_movies.common.movies;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.MediaDetail;

import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_BACKDROP_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_GENRES;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_IS_BOOKMARKED;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_IS_FAVORED;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_MEDIA_ID;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_OVERVIEW;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_POSTER_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_RELEASE_DATE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_RUNTIME;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_STATUS;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_TAGLINE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_TITLE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_VOTE_AVG;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_VOTE_COUNT;

@SuppressWarnings("unused")
public class MovieDetail extends MediaDetail implements Parcelable {


    public static final Creator<MovieDetail> CREATOR = new Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };

    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("runtime")
    @Expose
    private long runtime;
    @SerializedName("tagline")
    @Expose
    private String tagline;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    @SerializedName("budget")
    @Expose
    private long budget;
    @SerializedName("revenue")
    @Expose
    private long revenue;

    public MovieDetail() {
    }

    protected MovieDetail(Parcel in) {
        backdropPath = in.readString();
        id = in.readLong();
        overview = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        runtime = in.readLong();
        status = in.readString();
        tagline = in.readString();
        title = in.readString();
        voteAverage = in.readDouble();
        voteCount = in.readLong();
        imdbId = in.readString();
        budget = in.readLong();
        revenue = in.readLong();
        isFavored = (in.readByte() != 0);
        isBookmarked = (in.readByte() != 0);
    }

    public static ContentValues toContentValues(MovieDetail movieDetail) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MEDIA_ID, movieDetail.getId());
        cv.put(COLUMN_OVERVIEW, movieDetail.getOverview());
        cv.put(COLUMN_POSTER_PATH, movieDetail.getPosterPath());
        cv.put(COLUMN_BACKDROP_PATH, movieDetail.getBackdropPath());
        cv.put(COLUMN_VOTE_AVG, movieDetail.getVoteAverage());
        cv.put(COLUMN_VOTE_COUNT, movieDetail.getVoteCount());
        StringBuilder builder = new StringBuilder();
        List<Genre> genreList = movieDetail.getGenres();
        if (genreList != null && !genreList.isEmpty()) {
            for (Genre genre : genreList) {
                builder.append(genre.getName()).append(",");
            }
        }
        cv.put(COLUMN_GENRES, builder.toString());
        cv.put(COLUMN_STATUS, movieDetail.getStatus());
        cv.put(COLUMN_IS_FAVORED, movieDetail.isFavored() ? 1 : 0);
        cv.put(COLUMN_IS_BOOKMARKED, movieDetail.isBookmarked() ? 1 : 0);
        cv.put(COLUMN_TITLE, movieDetail.getTitle());
        cv.put(COLUMN_TAGLINE, movieDetail.getTagline());
        cv.put(COLUMN_RELEASE_DATE, movieDetail.getReleaseDate());
        cv.put(COLUMN_RUNTIME, movieDetail.getRuntime());
        return cv;
    }

    public static MovieDetail fromCursor(Cursor cursor) {
        MovieDetail movieDetail = new MovieDetail();
        if (cursor.moveToFirst()) {
            movieDetail.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_MEDIA_ID)));
            movieDetail.setOverview(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)));
            movieDetail.setPosterPath(cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)));
            movieDetail.setBackdropPath(cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)));
            movieDetail.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(COLUMN_VOTE_AVG)));
            movieDetail.setVoteCount(cursor.getLong(cursor.getColumnIndex(COLUMN_VOTE_COUNT)));
            String genresStr = cursor.getString(cursor.getColumnIndex(COLUMN_GENRES));
            List<Genre> genreList = new ArrayList<>();
            String[] genresArr = genresStr.split(",");
            for (String genre : genresArr) {
                genreList.add(new Genre(0, genre));
            }
            movieDetail.setGenres(genreList);
            movieDetail.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
            movieDetail.setFavored(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAVORED)) == 1);
            movieDetail.setBookmarked(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_BOOKMARKED)) == 1);
            movieDetail.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            movieDetail.setTagline(cursor.getString(cursor.getColumnIndex(COLUMN_TAGLINE)));
            movieDetail.setReleaseDate(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)));
            movieDetail.setRuntime(cursor.getLong(cursor.getColumnIndex(COLUMN_RUNTIME)));
        }
        return movieDetail;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(backdropPath);
        parcel.writeLong(id);
        parcel.writeString(overview);
        parcel.writeString(posterPath);
        parcel.writeString(releaseDate);
        parcel.writeLong(runtime);
        parcel.writeString(status);
        parcel.writeString(tagline);
        parcel.writeString(title);
        parcel.writeDouble(voteAverage);
        parcel.writeLong(voteCount);
        parcel.writeString(imdbId);
        parcel.writeLong(budget);
        parcel.writeLong(revenue);
        parcel.writeByte((byte) (isFavored ? 1 : 0));
        parcel.writeByte((byte) (isBookmarked ? 1 : 0));
    }
}
