package dev.learn.movies.app.popular_movies.common.tv_show;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Season implements Parcelable {

    public static final Creator<Season> CREATOR = new Creator<Season>() {
        @Override
        public Season createFromParcel(Parcel in) {
            return new Season(in);
        }

        @Override
        public Season[] newArray(int size) {
            return new Season[size];
        }
    };
    @SerializedName("air_date")
    @Expose
    private String airDate;
    @SerializedName("episode_count")
    @Expose
    private long episodeCount;
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("season_number")
    @Expose
    private long seasonNumber;

    public Season() {
    }

    protected Season(Parcel in) {
        airDate = in.readString();
        episodeCount = in.readLong();
        id = in.readLong();
        posterPath = in.readString();
        seasonNumber = in.readLong();
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public long getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(long episodeCount) {
        this.episodeCount = episodeCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public long getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(long seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(airDate);
        dest.writeLong(episodeCount);
        dest.writeLong(id);
        dest.writeString(posterPath);
        dest.writeLong(seasonNumber);
    }
}
