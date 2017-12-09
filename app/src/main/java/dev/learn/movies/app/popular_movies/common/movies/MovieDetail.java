package dev.learn.movies.app.popular_movies.common.movies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.MediaDetail;

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
