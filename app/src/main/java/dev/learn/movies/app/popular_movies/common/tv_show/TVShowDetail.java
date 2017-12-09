
package dev.learn.movies.app.popular_movies.common.tv_show;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.MediaDetail;

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

    public TVShowDetail() {}

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
}
